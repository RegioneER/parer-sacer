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

package it.eng.parer.web.util;

import java.util.Date;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.recupero.dto.ParametriParser;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.RecuperoZipGen;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.versRespStato.ECEsitoExtType;
import it.eng.parer.ws.xml.versRespStato.ECEsitoPosNegType;
import it.eng.parer.ws.xml.versRespStato.EsitoChiamataWSType;
import it.eng.parer.ws.xml.versRespStato.EsitoGenericoType;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;

/**
 * Classe deputata alla gestione del download SIP da dettaglio unità documentaria e documento. Si
 * appoggia a "contenitori" implementati per la gestione tramite WS
 *
 * @author gilioli_p
 */
@Stateless(mappedName = "DownloadSip")
@LocalBean
public class DownloadSip {

    private static Logger logger = LoggerFactory.getLogger(DownloadSip.class.getName());

    @EJB
    ControlliWS controlliWS;

    @EJB
    private RecuperoZipGen tmpGen;

    public void recuperaSip(RispostaWSRecupero rispostaWs, RecuperoExt rec, String path) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
	    try {
		tmpGen.generaZipSip(path, rec, rispostaWs);
		tmpAvanzamentoWs.resetFase();
	    } catch (Exception e) {
		rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nella fase di generazione dello zip del EJB " + e.getMessage());
		logger.error("Errore nella fase di generazione dello zip del EJB ", e);
	    }
	}

	if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    public void initRispostaWs(RispostaWSRecupero rispostaWs, AvanzamentoWs avanzamento,
	    RecuperoExt rec) {

	logger.debug("sono nel metodo init");
	StatoConservazione myEsito = new StatoConservazione();

	RispostaControlli rs = this.loadWsVersions(rec);

	rispostaWs.setSeverity(IRispostaWS.SeverityEnum.OK);
	rispostaWs.setErrorCode("");
	rispostaWs.setErrorMessage("");

	// prepara la classe esito e la aggancia alla rispostaWS
	myEsito.setEsitoGenerale(new EsitoGenericoType());
	rispostaWs.setIstanzaEsito(myEsito);

	// aggiunge l'istanza della classe parametri di recupero
	rec.setParametriRecupero(new ParametriRecupero());
	rec.getParametriRecupero().setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.DOWNLOAD);

	// aggiunge l'istanza della classe parametri del parser
	rec.setParametriParser(new ParametriParser());

	// aggancia alla rispostaWS
	rispostaWs.setAvanzamento(avanzamento);

	XMLGregorianCalendar d = XmlDateUtility.dateToXMLGregorianCalendar(new Date());
	myEsito.setDataRichiestaStato(d);

	if (!rs.isrBoolean()) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsError(rs.getCodErr(), rs.getDsErr());
	} else {
	    myEsito.getEsitoGenerale().setCodiceEsito(ECEsitoExtType.POSITIVO);
	    myEsito.getEsitoGenerale().setCodiceErrore("");
	    myEsito.getEsitoGenerale().setMessaggioErrore("");

	    myEsito.setVersione(rec.getDescrizione().getVersione(rec.getWsVersions()));

	    myEsito.setEsitoChiamataWS(new EsitoChiamataWSType());
	    myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.POSITIVO);
	    myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.POSITIVO);
	}
    }

    @SuppressWarnings("unchecked")
    protected RispostaControlli loadWsVersions(RecuperoExt ext) {
	RispostaControlli rs = controlliWS.loadWsVersions(ext.getDescrizione());
	// if positive ...
	if (rs.isrBoolean()) {
	    ext.setWsVersions((HashMap<String, String>) rs.getrObject());
	}
	return rs;
    }
}
