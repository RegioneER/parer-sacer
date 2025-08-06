/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.job.allineamentoEntiConvenzionati.ejb;

import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.allenteconv.AllineamentoEnteConvenzionato;
import it.eng.integriam.client.ws.allenteconv.RispostaWSAllineamentoEnteConvenzionato;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.entity.IamEnteConvenzDaAllinea;
import it.eng.parer.job.allineamentoEntiConvenzionati.utils.CostantiAllineaEntiConv;
import it.eng.parer.job.allineamentoEntiConvenzionati.utils.CostantiAllineaEntiConv.EsitoServizio;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "AllineamentoEntiConvenzionatiEjb")
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class AllineamentoEntiConvenzionatiEjb {

    Logger log = LoggerFactory.getLogger(AllineamentoEntiConvenzionatiEjb.class);
    @EJB
    private ConfigurationHelper coHelper;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private JobHelper jobHelper;

    /**
     * Metodo chiamato dal JOB di allineamento ente convenzionato su IAM
     *
     */
    public void allineaEntiConvenzionati() {
	allineaEntiConvenzionati(null);
    }

    /**
     * Metodo chiamato per il ricalcolo su IAM dei servizi erogati sull'ultimo accordo dell'ente
     * convenzionato associato alla struttura
     *
     * @param enteConvenzDaAllineaList lista oggetti di tipo {@link IamEnteConvenzDaAllinea}
     */
    public void allineaEntiConvenzionati(List<IamEnteConvenzDaAllinea> enteConvenzDaAllineaList) {
	boolean arrivoDaOnLine = false;
	/*
	 * Determino l'insieme delle registrazioni nel log degli enti da allineare con stato
	 * DA_ALLINEA, ALLINEA_IN_TIMEOUT o ALLINEA_IN_ERRORE
	 */
	if (enteConvenzDaAllineaList == null) {
	    enteConvenzDaAllineaList = struttureHelper.getIamEnteConvenzDaAllinea();
	} else {
	    arrivoDaOnLine = true;
	}

	// Istanzio risposta con esito OK di default
	RispostaWSAllineamentoEnteConvenzionato rispostaWsAec = new RispostaWSAllineamentoEnteConvenzionato();

	/*
	 * Mi tengo una variabile che mi dice se la replica è andata o meno a buon fine per la
	 * scrittura sulla tabella di log
	 */
	boolean replicaOK = true;

	/* Ricavo i dati per la chiamata del ws */
	String url = coHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_ALLINEA_ENTE_CONVENZ);
	String nmUserid = coHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERID_REPLICA_ORG);
	String cdPsw = coHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.PSW_REPLICA_ORG);
	String timeoutString = coHelper.getValoreParamApplicByApplic(
		CostantiDB.ParametroAppl.TIMEOUT_ALLINEA_ENTE_CONVENZ);

	/* Per ogni registrazione determinata */
	for (IamEnteConvenzDaAllinea enteConvenzDaAllinea : enteConvenzDaAllineaList) {

	    BigDecimal idEnteConvenz = enteConvenzDaAllinea.getIdEnteConvenz();

	    try {

		/* Recupero il client per chiamata al WS */
		AllineamentoEnteConvenzionato client = IAMSoapClients
			.allineamentoEnteConvenzionatoClient(nmUserid, cdPsw, url);

		if (client != null) {
		    // imposto il valore di timeout. vedi MEV #23814
		    if (timeoutString != null && timeoutString.matches("^[0-9]+$")) {
			int timeoutAllienaEnteConvenz = Integer.parseInt(timeoutString);
			IAMSoapClients.changeRequestTimeout((BindingProvider) client,
				timeoutAllienaEnteConvenz);
		    } else {
			log.warn("Il valore personalizzato \"" + timeoutString
				+ "\" per il parametro TIMEOUT_ALLINEA_ENTE_CONVENZ non è corretto. Utilizzo il valore predefinito");
		    }
		    log.info(
			    "Allineamento Ente Convenzionato - Preparazione attivazione servizio per l'ente convenzionato "
				    + idEnteConvenz);

		    // Esito chiamata WS
		    rispostaWsAec = client.ricalcoloServiziErogati(idEnteConvenz.intValue());

		    // La risposta del WS può avere esito OK, WARNING o ERROR, per esitoServizio in
		    // questa fase divido
		    // in OK e KO
		    EsitoServizio esitoServizio = rispostaWsAec.getEsito().name()
			    .equals(CostantiAllineaEntiConv.EsitoServizio.OK.name())
				    ? CostantiAllineaEntiConv.EsitoServizio.OK
				    : CostantiAllineaEntiConv.EsitoServizio.KO;

		    // Scrivo l'esito del singolo Allineamento Ente Convenzionato
		    struttureHelper.writeEsitoIamEnteConvenzDaAllinea(
			    enteConvenzDaAllinea.getIdEnteConvenzDaAllinea(), esitoServizio,
			    rispostaWsAec.getErrorCode(), rispostaWsAec.getErrorMessage());

		    String posNeg = esitoServizio.name()
			    .equals(CostantiAllineaEntiConv.EsitoServizio.OK.name()) ? "positiva"
				    : "negativa";
		    log.info("Allineamento Ente Convenzionato - Risposta WS " + posNeg
			    + " per l'ente convenzionato " + idEnteConvenz);

		    // Se non è OK mi salvo l'informazione
		    if (!esitoServizio.name()
			    .equals(CostantiAllineaEntiConv.EsitoServizio.OK.name())) {
			replicaOK = false;
		    }

		} else {
		    /* Se il client è null, ci sono stati problemi */
		    struttureHelper.writeEsitoIamEnteConvenzDaAllinea(
			    enteConvenzDaAllinea.getIdEnteConvenzDaAllinea(),
			    CostantiAllineaEntiConv.EsitoServizio.KO,
			    CostantiAllineaEntiConv.SERVIZI_ENTE_001,
			    "Errore nella creazione del client per la chiamata al WS di AllineamentoEnteConvenzionato");
		    log.error(
			    "Allineamento Ente Convenzionato - Risposta WS negativa per l'ente convenzionato "
				    + idEnteConvenz);
		    break;
		}

	    } catch (SOAPFaultException e) {
		/* Errori di autenticazione */
		struttureHelper.writeEsitoIamEnteConvenzDaAllinea(
			enteConvenzDaAllinea.getIdEnteConvenzDaAllinea(),
			CostantiAllineaEntiConv.EsitoServizio.KO,
			CostantiAllineaEntiConv.SERVIZI_ENTE_002,
			e.getFault().getFaultCode() + ": " + e.getFault().getFaultString());
		log.error(
			"Allineamento Ente Convenzionato - Risposta WS negativa per l'ente convenzionato "
				+ idEnteConvenz
				+ " - Utente che attiva il servizio non riconosciuto o non abilitato",
			e);
		replicaOK = false;
		break;
	    } catch (WebServiceException e) {
		/* Se non risponde... */
		struttureHelper.writeEsitoIamEnteConvenzDaAllinea(
			enteConvenzDaAllinea.getIdEnteConvenzDaAllinea(),
			CostantiAllineaEntiConv.EsitoServizio.NO_RISPOSTA,
			CostantiAllineaEntiConv.ALLINEA_ENTE_001,
			"Il servizio di allineamento ente convenzionato non risponde");
		log.error(
			"Allineamento Ente Convenzionato - Risposta WS negativa per l'ente convenzionato "
				+ idEnteConvenz
				+ " - Il servizio di allineamento ente convenzionato non risponde");
		replicaOK = false;
		break;
	    } catch (Exception e) {
		/* ... o si verifica qualche errore di esecuzione */
		struttureHelper.writeEsitoIamEnteConvenzDaAllinea(
			enteConvenzDaAllinea.getIdEnteConvenzDaAllinea(),
			CostantiAllineaEntiConv.EsitoServizio.KO,
			CostantiAllineaEntiConv.ALLINEA_ENTE_001, e.getMessage());
		log.error(
			"Allineamento Ente Convenzionato - Risposta WS negativa per l'ente convenzionato "
				+ idEnteConvenz,
			e);
		replicaOK = false;
		break;
	    }

	} // End for

	/* Scrivo nel log del job l'esito finale */
	if (!arrivoDaOnLine) {
	    if (replicaOK) {
		jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(),
			JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
	    } else {
		jobHelper.writeAtomicLogJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(),
			JobConstants.OpTypeEnum.ERRORE.name(),
			"Errore durante la chiamata al WS di allineamento ente convenzionato");
	    }
	}
    }
}
