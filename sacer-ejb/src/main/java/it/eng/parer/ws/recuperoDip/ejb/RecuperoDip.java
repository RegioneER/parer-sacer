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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recuperoDip.dto.CompRecDip;
import it.eng.parer.ws.recuperoDip.dto.DatiRecuperoDip;
import it.eng.parer.ws.recuperoDip.utils.GestSessRecDip;
import it.eng.parer.ws.recuperoTpi.utils.GestSessRecupero;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "RecuperoDip")
@LocalBean
public class RecuperoDip {

    @EJB
    private ControlliRecDip controlliRecDip;
    //
    private static final Logger log = LoggerFactory.getLogger(RecuperoDip.class);
    //

    public void contaComponenti(RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	RispostaControlli controlli = controlliRecDip.contaComponenti(rec.getParametriRecupero());
	if (!controlli.isrBoolean()) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsError(controlli.getCodErr(), controlli.getDsErr());
	} else {
	    if (rispostaWs.getDatiRecuperoDip() == null) {
		rispostaWs.setDatiRecuperoDip(new DatiRecuperoDip());
	    }
	    rispostaWs.getDatiRecuperoDip().setNumeroElementiTrovati(controlli.getrLong());
	    /*
	     * da notare che nel caso il valore fosse 0. questo sarà da trattare come un errore nel
	     * ws e come un caso normale - non viene mostrato il pulsante estrai/converti - nel caso
	     * dell'online. per questo qui non viene impostato il flag ed il messaggio di errore
	     */
	}
	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    public void listaComponenti(RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	if (rispostaWs.getDatiRecuperoDip() == null) {
	    rispostaWs.setDatiRecuperoDip(new DatiRecuperoDip());
	}

	GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);
	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    gestSessRecDip.caricaListaComponenti(rec);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    public void recuperaCompConvertito(RispostaWSRecupero rispostaWs, RecuperoExt rec,
	    String path) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	boolean salvaSessioneRecupero = false;

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	// VERIFICA CHE LA RICHIESTA SIA DAVVERO RELATIVA AD UN SOLO COMPONENTE!!!
	if (rec.getParametriRecupero()
		.getTipoEntitaSacer() != CostantiDB.TipiEntitaRecupero.COMP_DIP) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: questo servizio supporta solo l'estrazione di singoli componenti.");
	    return;
	}

	GestSessRecupero gestSessRecupero = new GestSessRecupero(rispostaWs);
	// carica i parametri, stabilisce se è attivo il TPI, predispone i nomi di tutti i
	// path utili per il salvataggio filesystem
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero.caricaParametri(rec);
	}

	GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecDip.caricaParametri(rec);
	}

	// verifica e carica la data del componente
	// che deve essere recuperato
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero.verificaDate(rec);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    salvaSessioneRecupero = true;
	    try {
		gestSessRecDip.recuperaCompConvertito(path, rec,
			rispostaWs.getDatiRecuperoDip().getElementiTrovati());
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nella fase di recupero DIP del EJB " + e.getMessage());
		log.error("Errore nella fase di recupero DIP del EJB ", e);
	    }
	}

	if (salvaSessioneRecupero) {
	    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
		rec.getDatiSessioneRecupero()
			.setStatoSess(JobConstants.StatoSessioniRecupEnum.ELIMINATO);
		rec.getDatiSessioneRecupero()
			.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.RECUPERATA);
	    } else {
		rec.getDatiSessioneRecupero()
			.setStatoSess(JobConstants.StatoSessioniRecupEnum.CHIUSO_ERR);
		rec.getDatiSessioneRecupero()
			.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.ERRORE);
		rec.getDatiSessioneRecupero().setErrorCode(rispostaWs.getErrorCode());
		rec.getDatiSessioneRecupero().setErrorMessage(rispostaWs.getErrorMessage());
	    }
	    //
	    gestSessRecupero.creaSessRecChiusa(rec);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    public void recuperaUnitaDocumentaria(RispostaWSRecupero rispostaWs, RecuperoExt rec,
	    String path) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	boolean salvaSessioneRecupero = false;

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	GestSessRecupero gestSessRecupero = new GestSessRecupero(rispostaWs);
	// carica i parametri, stabilisce se è attivo il TPI, predispone i nomi di tutti i
	// path utili per il salvataggio filesystem
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero.caricaParametri(rec);
	}

	GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecDip.caricaParametri(rec);
	}

	// verifica e carica le date di versamento di tutti i documenti dell'UD
	// che devono essere recuperati
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero.verificaDate(rec);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    salvaSessioneRecupero = true;
	    try {
		gestSessRecDip.recuperaZip(path, rec,
			rispostaWs.getDatiRecuperoDip().getElementiTrovati());
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nella fase di generazione dello zip del EJB " + e.getMessage());
		log.error("Errore nella fase di generazione dello zip del EJB ", e);
	    }
	}

	if (salvaSessioneRecupero) {
	    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
		rec.getDatiSessioneRecupero()
			.setStatoSess(JobConstants.StatoSessioniRecupEnum.ELIMINATO);
		rec.getDatiSessioneRecupero()
			.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.RECUPERATA);
	    } else {
		rec.getDatiSessioneRecupero()
			.setStatoSess(JobConstants.StatoSessioniRecupEnum.CHIUSO_ERR);
		rec.getDatiSessioneRecupero()
			.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.ERRORE);
		rec.getDatiSessioneRecupero().setErrorCode(rispostaWs.getErrorCode());
		rec.getDatiSessioneRecupero().setErrorMessage(rispostaWs.getErrorMessage());
	    }
	    //
	    gestSessRecupero.creaSessRecChiusa(rec);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    public void collaudaConvertitore(RispostaWSRecupero rispostaWs, long idConvertitore,
	    String path) {
	RecuperoExt rec = new RecuperoExt();
	GestSessRecupero gestSessRecupero = new GestSessRecupero(rispostaWs);
	CompRecDip comp = null;

	GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecDip.caricaParametri(rec);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    RispostaControlli controlli = controlliRecDip.caricaConvertitore(idConvertitore);
	    if (!controlli.isrBoolean()) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsError(controlli.getCodErr(), controlli.getDsErr());
	    } else {
		try {
		    comp = (CompRecDip) controlli.getrObject();
		    gestSessRecDip.collaudaConvertitore(path, rec, comp);
		} catch (Exception e) {
		    rispostaWs.setSeverity(SeverityEnum.ERROR);
		    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			    "Errore nella fase di collaudo del convertitore " + e.getMessage());
		    log.error("Errore nella fase di collaudo del convertitore ", e);
		}
	    }
	}

    }

}
