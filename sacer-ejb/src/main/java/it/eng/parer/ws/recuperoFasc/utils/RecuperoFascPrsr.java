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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoFasc.utils;

import java.io.StringReader;
import java.math.BigDecimal;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recuperoFasc.dto.ParametriFascParser;
import it.eng.parer.ws.recuperoFasc.dto.RecuperoFascExt;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.ejb.ControlliRecuperoFasc;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.versReqStatoFasc.RecuperoFascicolo;
import it.eng.parer.ws.xml.versRespStatoFasc.ECEsitoPosNegType;
import it.eng.parer.ws.xml.versRespStatoFasc.StatoConservazioneFasc;
import it.eng.spagoLite.security.User;

/**
 *
 * @author DiLorenzo_F
 */
public class RecuperoFascPrsr {

    private static final Logger log = LoggerFactory.getLogger(RecuperoFascPrsr.class);
    private RispostaWSRecuperoFasc rispostaWsFasc;
    private RispostaControlli rispostaControlli;
    // l'istanza della request decodificata dall'XML di versamento
    RecuperoFascicolo parsedFasc = null;
    // stateless ejb per i controlli sul db
    ControlliSemantici controlliSemantici = null;
    // stateless ejb per verifica autorizzazione ws
    ControlliWS controlliEjb = null;
    // stateless ejb per i controlli specifici del recupero fascicolo
    ControlliRecuperoFasc controlliRecuperoFasc = null;
    // singleton ejb - cache dei JAXBContext
    XmlContextCache xmlContextCache = null;

    public RispostaWSRecuperoFasc getRispostaWsFasc() {
	return rispostaWsFasc;
    }

    public RecuperoFascPrsr(RispostaWSRecuperoFasc risp) {
	rispostaWsFasc = risp;
	rispostaControlli = new RispostaControlli();
    }

    public void parseXML(String datiXml, RecuperoFascExt recuperoFasc) {
	StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
	AvanzamentoWs myAvanzamentoWs = rispostaWsFasc.getAvanzamento();
	StringReader tmpReader;
	parsedFasc = null;

	// stateless ejb per i controlli sul db
	controlliSemantici = null;

	// stateless ejb per verifica autorizzazione
	controlliEjb = null;

	// recupera l'ejb singleton, se possibile - altrimenti segnala errore
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    try {
		xmlContextCache = (XmlContextCache) new InitialContext()
			.lookup("java:module/XmlContextCache");
	    } catch (NamingException ex) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nel recupero dell'EJB singleton XMLContext " + ex.getMessage());
		log.error("Errore nel recupero dell'EJB singleton XMLContext ", ex);
	    }
	}

	recuperoFasc.setDatiXml(datiXml);
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    XmlValidationEventHandler validationHandler = new XmlValidationEventHandler();
	    tmpReader = new StringReader(datiXml);
	    try {
		myAvanzamentoWs.setFase("Unmarshall XML").logAvanzamento();

		Unmarshaller unmarshaller = xmlContextCache.getVersReqStatoFascCtx_RecuperoFasc()
			.createUnmarshaller();
		unmarshaller.setSchema(xmlContextCache.getSchemaOfVersReqStatoFasc());
		unmarshaller.setEventHandler(validationHandler);
		parsedFasc = (RecuperoFascicolo) unmarshaller.unmarshal(tmpReader);
		recuperoFasc.setStrutturaRecuperoFasc(parsedFasc);
	    } catch (UnmarshalException e) {
		ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setErrorCode("-1");
		rispostaWsFasc.setErrorMessage(
			"Errore: XML malformato nel blocco di dati generali. Eccezione: "
				+ event.getMessage());
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.XSD_001_001,
			event.getMessage());
	    } catch (Exception e) {
		ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setErrorCode("-1");
		rispostaWsFasc.setErrorMessage(
			"Errore di validazione del blocco di dati generali. Eccezione: "
				+ event.getMessage());
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.XSD_001_002,
			event.getMessage());
	    }
	}

	// se l'unmarshalling è andato bene, recupero la versione XML di versamento fascicolo
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    myEsito.setVersioneXMLChiamata(parsedFasc.getVersione());
	    myAvanzamentoWs.setFase("Unmarshall OK")
		    .setChAnno(Long.toString(parsedFasc.getChiave().getAnno().longValue()))
		    .setChNumero(parsedFasc.getChiave().getNumero())
		    .setVrsAmbiente(parsedFasc.getVersatore().getAmbiente())
		    .setVrsEnte(parsedFasc.getVersatore().getEnte())
		    .setVrsStruttura(parsedFasc.getVersatore().getStruttura())
		    .setVrsUser(parsedFasc.getVersatore().getUserID()).logAvanzamento();
	}

	myAvanzamentoWs.setFase("verifica semantica richiesta - inizio").logAvanzamento();
	/*
	 * come prima cosa verifico che il versatore e la versione dichiarati nel WS coincidano con
	 * quelli nell'xml
	 */
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    if (!parsedFasc.getVersione().equals(recuperoFasc.getVersioneWsChiamata())) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.UD_001_013,
			parsedFasc.getVersione());
		myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
	    }
	}

	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    if (!parsedFasc.getVersatore().getUserID().equals(recuperoFasc.getLoginName())) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.UD_001_005,
			parsedFasc.getVersatore().getUserID());
		myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
	    }
	}

	// un po' di controlli relativi alla versione del WS invocata...
	// posso usare il tag Utente?
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK
		&& parsedFasc.getVersatore().getUtente() != null
		&& !parsedFasc.getVersatore().getUtente().isEmpty()) {
	    rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
	    rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<Utente>");
	    myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
	}

	//
	// recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    try {
		controlliSemantici = (ControlliSemantici) new InitialContext()
			.lookup("java:module/ControlliSemantici");
	    } catch (NamingException ex) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nel recupero dell'EJB dei controlli semantici  " + ex.getMessage());
		log.error("Errore nel recupero dell'EJB dei controlli semantici ", ex);
	    }
	}

	// recupera l'ejb dell'autenticazione, se possibile - altrimenti segnala errore
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    try {
		controlliEjb = (ControlliWS) new InitialContext().lookup("java:module/ControlliWS");
	    } catch (NamingException ex) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nel recupero dell'EJB di verifica delle autorizzazioni  "
				+ ex.getMessage());
		log.error("Errore nel recupero dell'EJB di verifica delle autorizzazioni ", ex);
	    }
	}

	// recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    try {
		controlliRecuperoFasc = (ControlliRecuperoFasc) new InitialContext()
			.lookup("java:module/ControlliRecuperoFasc");
	    } catch (NamingException ex) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nel recupero dell'EJB dei Controlli Recupero Fasc "
				+ ex.getMessage());
		log.error("Errore nel recupero dell'EJB dei Controlli Recupero Fasc ", ex);
	    }
	}

	CSVersatore tmpCSVersatoreFasc = new CSVersatore();
	// verifica il versatore del fascicolo
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    tmpCSVersatoreFasc.setAmbiente(parsedFasc.getVersatore().getAmbiente());
	    tmpCSVersatoreFasc.setEnte(parsedFasc.getVersatore().getEnte());
	    tmpCSVersatoreFasc.setStruttura(parsedFasc.getVersatore().getStruttura());

	    rispostaControlli.reset();
	    rispostaControlli = controlliSemantici.checkIdStrut(tmpCSVersatoreFasc,
		    TipiWSPerControlli.VERSAMENTO_RECUPERO_FASC);
	    if (rispostaControlli.getrLong() < 1) {
		setRispostaWsError();

		myEsito.getEsitoChiamataWS()
			.setIdentificazioneVersatore(rispostaControlli.getDsErr());
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
	    } else {
		// salvo idstruttura
		recuperoFasc.setIdStruttura(rispostaControlli.getrLong());
		myEsito.getEsitoChiamataWS().setIdentificazioneVersatore("POSITIVO");
	    }
	}

	/*
	 * Nota: non verifico l'eventuale presenza obbligatoria dell'utente alternativo poiché è per
	 * definizione opzionale e gestito quando presente
	 *
	 * MEV #21799 : attenzione questa verifica non viene più effettuata sul tag <Utente>,
	 * getGestioneUtenteAlternativo() per default è Ignorato
	 */
	// se necessario provo a gestire l'eventuale utente umano in override sull'utente automatico
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK
		&& recuperoFasc.getParametriFascParser()
			.getGestioneUtenteAlternativo() == ParametriFascParser.TipiGestione.Gestito
		&& parsedFasc.getVersatore().getUtente() != null
		&& !parsedFasc.getVersatore().getUtente().isEmpty()) {

	    // verifico l'esistenza dell'utente e il suo stato (attivo, scaduto, ecc)
	    rispostaControlli = controlliEjb.checkUtente(parsedFasc.getVersatore().getUtente());
	    if (rispostaControlli.isrBoolean()) {
		// se l'utente è usabile, effettuo l'override.
		// da questo momento il WS gestirà questo utente come se fosse il chiamante
		recuperoFasc.setLoginName(parsedFasc.getVersatore().getUtente());
		recuperoFasc.getParametriRecuperoFasc()
			.setUtente((User) rispostaControlli.getrObject());
	    } else {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
		myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
	    }
	}

	// verifica se l'utente (umano o automatico o umano in override) è autorizzato ad usare il
	// WS sulla struttura
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    recuperoFasc.getParametriRecuperoFasc().getUtente()
		    .setIdOrganizzazioneFoglia(new BigDecimal(recuperoFasc.getIdStruttura()));
	    rispostaControlli.reset();
	    rispostaControlli = controlliEjb.checkAuthWS(
		    recuperoFasc.getParametriRecuperoFasc().getUtente(),
		    recuperoFasc.getDescrizione(), TipiWSPerControlli.VERSAMENTO_RECUPERO_FASC);
	    if (!rispostaControlli.isrBoolean()) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
		myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
	    }
	}
	// vedo MEV#21799 : questo controllo non viene più fatto per il recupero

	CSChiaveFasc tmpCSChiaveFasc = new CSChiaveFasc();
	// verifica la chiave del fascicolo
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    tmpCSChiaveFasc.setAnno(parsedFasc.getChiave().getAnno().intValue());
	    tmpCSChiaveFasc.setNumero(parsedFasc.getChiave().getNumero());

	    rispostaControlli.reset();
	    if (recuperoFasc.getParametriFascParser().isLeggiAncheFascAnnullati()) {
		rispostaControlli = controlliSemantici.checkChiaveFasc(tmpCSChiaveFasc,
			recuperoFasc.getIdStruttura(),
			ControlliSemantici.TipiGestioneFASCAnnullati.CARICA);
	    } else {
		rispostaControlli = controlliSemantici.checkChiaveFasc(tmpCSChiaveFasc,
			recuperoFasc.getIdStruttura(),
			ControlliSemantici.TipiGestioneFASCAnnullati.CONSIDERA_ASSENTE);
	    }

	    if (rispostaControlli.isrBoolean() || rispostaControlli.getrLong() == -1) {
		setRispostaWsError();
		myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
	    } else {
		// OK - popolo la risposta versamento
		myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.POSITIVO);
		// memorizzo l'ID della chiave del fascicolo trovata
		recuperoFasc.getParametriRecuperoFasc()
			.setIdFascicolo(rispostaControlli.getrLong());
		// normalized cd_key_fasc
		recuperoFasc.getParametriRecuperoFasc()
			.setNumeroFascNormalized((String) rispostaControlli.getrMap()
				.get(RispostaControlli.ValuesOnrMap.CD_KEY_NORMALIZED.name()));
	    }
	}

	// verifica struttura cessata
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
	    rispostaControlli.reset();
	    rispostaControlli = controlliSemantici.checkStrutCessata(recuperoFasc.getIdStruttura());
	    if (!rispostaControlli.isrBoolean()) {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			rispostaControlli.getDsErr());
		myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
	    }
	}

	// verifico se devo ricevere la chiave del tipo fascicolo
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK
		&& recuperoFasc.getParametriFascParser()
			.getPresenzaTipoFascicolo() == ParametriFascParser.TipiPresenzaTag.Richiesto
		&& (parsedFasc.getChiave().getTipoFascicolo() == null)) {
	    rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
	    rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_001, "<TipoFascicolo>");
	    myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
	}

	// verifico se posso gestire la chiave del tipo fascicolo
	if (rispostaWsFasc.getSeverity() == SeverityEnum.OK
		&& recuperoFasc.getParametriFascParser()
			.getGestioneTipoFascicolo() == ParametriFascParser.TipiGestione.Gestito
		&& parsedFasc.getChiave().getTipoFascicolo() != null) {

	    if (recuperoFasc.getParametriRecuperoFasc().getIdFascicolo() == null) {
		CSChiaveFasc csChiaveFasc = new CSChiaveFasc();
		csChiaveFasc.setAnno(parsedFasc.getChiave().getAnno().intValue());
		csChiaveFasc.setNumero(parsedFasc.getChiave().getNumero());
		String descChiaveFasc = MessaggiWSFormat.formattaUrnPartFasc(csChiaveFasc);
		rispostaControlli = controlliRecuperoFasc.checkTipoFascicoloperStrut(
			recuperoFasc.getIdStruttura(), parsedFasc.getChiave().getTipoFascicolo(),
			descChiaveFasc);
		if (rispostaControlli.isrBoolean()) {
		    rispostaControlli = controlliRecuperoFasc.checkTipoFascicoloinFASC(
			    recuperoFasc.getParametriRecuperoFasc().getIdFascicolo(),
			    parsedFasc.getChiave().getTipoFascicolo(), descChiaveFasc);
		    if (rispostaControlli.isrBoolean()) {
			// memorizzo l'ID della chiave tipo fasc trovata
			recuperoFasc.getParametriRecuperoFasc()
				.setIdTipoFascicolo(rispostaControlli.getrLong());
		    } else {
			setRispostaWsError();
			myEsito.getEsitoChiamataWS()
				.setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
			rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
				rispostaControlli.getDsErr());
		    }
		} else {
		    setRispostaWsError();
		    myEsito.getEsitoChiamataWS()
			    .setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
		    rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(),
			    rispostaControlli.getDsErr());
		}
	    } else {
		rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
		rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.DOC_011_001);
		myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
	    }
	}

	//
	myAvanzamentoWs.setFase("verifica semantica richiesta - fine").logAvanzamento();
    }

    private void setRispostaWsError() {
	rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
	rispostaWsFasc.setErrorCode(rispostaControlli.getCodErr());
	rispostaWsFasc.setErrorMessage(rispostaControlli.getDsErr());
    }
}
