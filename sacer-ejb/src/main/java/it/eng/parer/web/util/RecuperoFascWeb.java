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

import java.math.BigDecimal;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.recuperoFasc.dto.ParametriRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.dto.RecuperoFascExt;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.dto.WSDescRecAipFasc;
import it.eng.parer.ws.recuperoFasc.ejb.RecuperoFascSync;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.versReqStatoFasc.RecuperoFascicolo;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.security.User;

/**
 *
 * @author DiLorenzo_F
 */
public class RecuperoFascWeb {

    private static final Logger log = LoggerFactory.getLogger(RecuperoFascWeb.class);
    private final RecuperoFascicolo recuperoFasc;
    private final User user;
    private final BigDecimal idFascicolo;
    private final CostantiDB.TipoSalvataggioFile tipoSalvataggioFile;
    private final CostantiDB.TipiEntitaRecupero tipoEntitaSacer;

    /**
     * Costruttore recupero fascicolo oppure FASCICOLO per UniSyncro
     *
     * @param recuperoFasc        bean Recupero
     * @param user                bean User
     * @param idFascicolo         id fascicolo
     * @param tipoSalvataggioFile tipo salvataggio file CostantiDB.TipoSalvataggioFile
     * @param tipoEntitaSacer     tipo entita sacer CostantiDB.TipiEntitaRecupero
     */
    public RecuperoFascWeb(RecuperoFascicolo recuperoFasc, User user, BigDecimal idFascicolo,
	    CostantiDB.TipoSalvataggioFile tipoSalvataggioFile,
	    CostantiDB.TipiEntitaRecupero tipoEntitaSacer) {
	this.recuperoFasc = recuperoFasc;
	this.user = user;
	this.idFascicolo = idFascicolo;
	this.tipoSalvataggioFile = tipoSalvataggioFile;
	// tipoEntita = FASCICOLO o FASC_UNISYNCRO
	this.tipoEntitaSacer = tipoEntitaSacer;
    }

    public RispostaWSRecuperoFasc recuperaOggettoFasc() throws EMFError {
	RecuperoFascSync recuperoFascSync;
	RispostaWSRecuperoFasc rispostaWs;
	RecuperoFascExt myRecuperoFascExt;
	AvanzamentoWs tmpAvanzamento;

	rispostaWs = new RispostaWSRecuperoFasc();
	myRecuperoFascExt = new RecuperoFascExt();
	WSDescRecAipFasc wsdes = new WSDescRecAipFasc();

	myRecuperoFascExt.setDescrizione(wsdes);

	tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS("prova",
		AvanzamentoWs.Funzioni.RecuperoFascWeb);
	tmpAvanzamento.logAvanzamento();

	// Recupera l'ejb, se possibile - altrimenti segnala errore
	try {
	    recuperoFascSync = (RecuperoFascSync) new InitialContext()
		    .lookup("java:app/Parer-ejb/RecuperoFascSync");
	} catch (NamingException ex) {
	    log.error("Errore nel recupero dell'EJB ", ex);
	    throw new EMFError(EMFError.ERROR, "Impossibile recuperare l'ejb: " + ex);
	}

	tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();
	recuperoFascSync.initRispostaWs(rispostaWs, tmpAvanzamento, myRecuperoFascExt);
	// set versione after initRispostaWs
	recuperoFasc.setVersione(wsdes.getVersione(myRecuperoFascExt.getWsVersions()));

	if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
	    // Popolo parzialmente RecuperoFascExt con i valori che mi
	    // serviranno in fase di recupero fascicolo
	    ParametriRecuperoFasc tmpParametriRecuperoFasc = myRecuperoFascExt
		    .getParametriRecuperoFasc();
	    tmpParametriRecuperoFasc.setIdFascicolo(idFascicolo.longValue());
	    tmpParametriRecuperoFasc.setUtente(user);
	    tmpParametriRecuperoFasc
		    .setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.DOWNLOAD);
	    tmpParametriRecuperoFasc.setTipoEntitaSacer(tipoEntitaSacer);
	    /*
	     * VERIFICARE switch (tipoEntitaSacer) { case FASCICOLO: case DOC_FASCICOLO...:
	     * tmpParametriRecupero.setIdDocumento(idCompDoc.longValue()); break; case COMP: case
	     * COMP_DIP_ESIBIZIONE: case SUB_COMP:
	     * tmpParametriRecupero.setIdComponente(idCompDoc.longValue()); break; default: break; }
	     */

	    myRecuperoFascExt.setDatiXml(" ");
	    myRecuperoFascExt.setStrutturaRecuperoFasc(recuperoFasc);
	    myRecuperoFascExt.setVersioneWsChiamata("---");
	    myRecuperoFascExt.setTipoSalvataggioFile(tipoSalvataggioFile);
	    // prepara risposta
	    recuperoFascSync.recuperaOggettoFasc(rispostaWs, myRecuperoFascExt,
		    System.getProperty("java.io.tmpdir"));
	}

	return rispostaWs;
    }
}
