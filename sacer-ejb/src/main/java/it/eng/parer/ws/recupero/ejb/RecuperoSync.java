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
package it.eng.parer.ws.recupero.ejb;

import java.util.Date;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.recupero.dto.ParametriParser;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RecuperoMMExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.utils.IndiceMMPrsr;
import it.eng.parer.ws.recupero.utils.RecuperoPrsr;
import it.eng.parer.ws.recupero.utils.RecuperoXmlGen;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.recuperoTpi.utils.GestSessRecupero;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.versRespStato.ECEsitoExtType;
import it.eng.parer.ws.xml.versRespStato.ECEsitoPosNegType;
import it.eng.parer.ws.xml.versRespStato.EsitoChiamataWSType;
import it.eng.parer.ws.xml.versRespStato.EsitoGenericoType;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.spagoLite.security.User;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "RecuperoSync")
@LocalBean
public class RecuperoSync {

    @EJB
    private ControlliWS myControlliWs;
    //
    @EJB
    private ControlliRecupero controlliRecupero;

    @EJB
    private RecuperoZipGen recuperoZipGen;

    private static final Logger log = LoggerFactory.getLogger(RecuperoSync.class);
    //

    public void initRispostaWs(RispostaWSRecupero rispostaWs, AvanzamentoWs avanzamento,
	    RecuperoExt rec) {

	log.debug("sono nel metodo init");
	StatoConservazione myEsito = new StatoConservazione();

	RispostaControlli rs = this.loadWsVersions(rec);

	rispostaWs.setSeverity(SeverityEnum.OK);
	rispostaWs.setErrorCode("");
	rispostaWs.setErrorMessage("");

	// prepara la classe esito e la aggancia alla rispostaWS
	myEsito.setEsitoGenerale(new EsitoGenericoType());
	rispostaWs.setIstanzaEsito(myEsito);

	// aggiunge l'istanza della classe parametri di recupero
	rec.setParametriRecupero(new ParametriRecupero());
	rec.getParametriRecupero().setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.SERVIZIO);

	// aggiunge l'istanza della classe parametri del parser
	rec.setParametriParser(new ParametriParser());

	// aggancia alla rispostaWS
	rispostaWs.setAvanzamento(avanzamento);

	XMLGregorianCalendar d = XmlDateUtility.dateToXMLGregorianCalendar(new Date());
	myEsito.setDataRichiestaStato(d);

	//
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

    public void verificaVersione(String versione, RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	RispostaControlli tmpRispostaControlli = null;
	rec.setVersioneWsChiamata(versione);
	myEsito.setVersioneXMLChiamata(versione);

	tmpRispostaControlli = myControlliWs.checkVersione(versione,
		rec.getDescrizione().getNomeWs(), rec.getWsVersions(),
		TipiWSPerControlli.VERSAMENTO_RECUPERO);
	if (!tmpRispostaControlli.isrBoolean()) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsError(tmpRispostaControlli.getCodErr(),
		    tmpRispostaControlli.getDsErr());
	    myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
	} else {
	    rec.checkVersioneRequest(versione);
	    myEsito.setVersione(rec.getVersioneCalc());
	}
    }

    public void verificaCredenziali(String loginName, String password, String indirizzoIp,
	    RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	verificaCredenziali(loginName, password, indirizzoIp, rispostaWs, rec, null);
    }

    public void verificaCredenziali(String loginName, String password, String indirizzoIp,
	    RispostaWSRecupero rispostaWs, RecuperoExt rec, String certCommonName) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	RispostaControlli tmpRispostaControlli = null;

	tmpRispostaControlli = myControlliWs.checkCredenziali(loginName, password, indirizzoIp,
		TipiWSPerControlli.VERSAMENTO_RECUPERO, certCommonName);
	if (!tmpRispostaControlli.isrBoolean()) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsError(tmpRispostaControlli.getCodErr(),
		    tmpRispostaControlli.getDsErr());
	    myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
	}

	/* logga il login name oppure il CM del certificato se passato */
	if (certCommonName != null && !certCommonName.isEmpty()) {
	    rec.setLoginName(certCommonName);
	} else {
	    rec.setLoginName(loginName);
	}
	rec.getParametriRecupero().setUtente((User) tmpRispostaControlli.getrObject());
    }

    public void parseXMLIndiceMM(String datiXml, RispostaWSRecupero rispostaWs, RecuperoMMExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	try {
	    IndiceMMPrsr tmpPrsr = new IndiceMMPrsr(rec, rispostaWs);
	    tmpPrsr.parseXML(datiXml, rec.getParametriRecupero().getUtente());
	    tmpAvanzamentoWs.resetFase();
	} catch (Exception e) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore nella fase di parsing dell'XML indice del EJB " + e.getMessage());
	    log.error("Eccezione nella fase di parsing dell'indice del EJB ", e);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(datiXml);
	}
    }

    public void parseXML(String datiXml, RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	try {
	    RecuperoPrsr tmpPrsr = new RecuperoPrsr(rispostaWs);
	    tmpPrsr.parseXML(datiXml, rec);
	    tmpAvanzamentoWs.resetFase();
	} catch (Exception e) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore nella fase di parsing dell'XML del EJB " + e.getMessage());
	    log.error("Eccezione nella fase di parsing dell'XML del EJB ", e);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(datiXml);
	}
    }

    // questo metodo viene usato nel WS di recupero stato conservazione UD
    public void recuperaStatoConservazioneUD(RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	try {
	    RecuperoXmlGen tmpGen = new RecuperoXmlGen(rispostaWs);
	    tmpGen.generaStatoConservazione(rec);
	    tmpAvanzamentoWs.resetFase();
	} catch (Exception e) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore nella fase di generazione dell'XML di risposta del EJB "
			    + e.getMessage());
	    log.error("Errore nella fase di generazione dell'XML di risposta del EJB ", e);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    // questo metodo viene usato nel WS di recupero UD
    // viene impiegato sia dal recupero sincrono che in quello multimedia, poichè al termine
    // dell'operazione
    // provvede a lasciare un file zip nell'area temporanea, pronto per lo streaming o per il
    // trasferimento
    public void recuperaOggetto(RispostaWSRecupero rispostaWs, RecuperoExt recupero, String path) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();
	boolean salvaSessioneRecupero = false;
	boolean tentaRecuperoDIP = true;
	GestSessRecupero gestSessRecupero = null;

	if (recupero.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero = new GestSessRecupero(rispostaWs);
	}

	// carica i parametri, stabilisce se è attivo il TPI, predispone i nomi di tutti i
	// path utili per il salvataggio filesystem
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero.caricaParametri(recupero);
	}

	// verifica se è stato prodotto l'AIP nel caso venga richiesto il recupero AIP
	// se non lo trova rende un errore ed esce
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    // EVO#20972
	    if (recupero.getParametriRecupero()
		    .getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO
		    || recupero.getParametriRecupero()
			    .getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2) // end
													 // EVO#20972
	    {
		RispostaControlli rc = controlliRecupero
			.contaXMLIndiceAIP(recupero.getParametriRecupero().getIdUnitaDoc());
		if (rc.getrLong() == 0) {
		    rispostaWs.setSeverity(SeverityEnum.ERROR);
		    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.UD_005_003,
			    recupero.getParametriRecupero().getDescUnitaDoc());
		    return;
		}
	    }
	}

	// verifica e carica le date di versamento di tutti i documenti dell'UD/DOC/COMP
	// che devono essere recuperati
	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    gestSessRecupero.verificaDate(recupero);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.OK
		&& recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE
		&& recupero.isTpiAbilitato()) {
	    gestSessRecupero.verificaPrenotaRecAsync(recupero);
	    // il parametro tentaRecuperoDIP è sempre false, se sta recuperando da TPI
	    tentaRecuperoDIP = false;
	    /*
	     * se rende OK -> recupera da filesystem se rende WARNING -> prenotazione effettuata o
	     * recupero in corso, non recupera nulla se rende ERROR -> recupero e prenotazione
	     * impossibili
	     */
	}

	if (rispostaWs.getSeverity() == SeverityEnum.OK) {
	    salvaSessioneRecupero = true;
	    try {
		recuperoZipGen.generaZipOggetto(path, recupero, tentaRecuperoDIP, rispostaWs);
		tmpAvanzamentoWs.resetFase();
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nella fase di generazione dello zip del EJB " + e.getMessage());
		log.error("Errore nella fase di generazione dello zip del EJB ", e);
	    }
	}

	if (salvaSessioneRecupero) {
	    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
		recupero.getDatiSessioneRecupero()
			.setStatoSess(JobConstants.StatoSessioniRecupEnum.ELIMINATO);
		recupero.getDatiSessioneRecupero()
			.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.RECUPERATA);
	    } else {
		recupero.getDatiSessioneRecupero()
			.setStatoSess(JobConstants.StatoSessioniRecupEnum.CHIUSO_ERR);
		recupero.getDatiSessioneRecupero()
			.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.ERRORE);
		recupero.getDatiSessioneRecupero().setErrorCode(rispostaWs.getErrorCode());
		recupero.getDatiSessioneRecupero().setErrorMessage(rispostaWs.getErrorMessage());
	    }
	    //
	    if (recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE
		    && recupero.isTpiAbilitato()) {
		gestSessRecupero.chiudiSessRec(recupero);
	    } else {
		gestSessRecupero.creaSessRecChiusa(recupero);
	    }
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(recupero.getDatiXml());
	}
    }

    // questo metodo viene usato nel WS di recupero prove di conservazione UD
    // viene impiegato sia dal recupero sincrono che in quello multimedia, poichè al termine
    // dell'operazione
    // provvede a lasciare un file zip nell'area temporanea, pronto per lo streaming o per il
    // trasferimento
    public void recuperaProveConserv(RispostaWSRecupero rispostaWs, RecuperoExt rec, String path) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	try {
	    RecuperoXmlGen tmpGen = new RecuperoXmlGen(rispostaWs);
	    tmpGen.generaIndiceProveCons(rec);
	    tmpAvanzamentoWs.resetFase();
	} catch (Exception e) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore nella fase di generazione dell'XML di risposta del EJB "
			    + e.getMessage());
	    log.error("Errore nella fase di generazione dell'XML  ", e);
	}

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		recuperoZipGen.generaZipProveCons(path, rec, rispostaWs);
		tmpAvanzamentoWs.resetFase();
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nella fase di generazione dello zip del EJB " + e.getMessage());
		log.error("Errore nella fase di generazione dello zip del EJB ", e);
	    }
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    public void recuperaRapportiVers(RispostaWSRecupero rispostaWs, RecuperoExt rec, String path) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
	    try {
		recuperoZipGen.generaZipRapportiVers(path, rec, rispostaWs);
		tmpAvanzamentoWs.resetFase();
	    } catch (Exception e) {
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			"Errore nella fase di generazione dello zip del EJB " + e.getMessage());
		log.error("Errore nella fase di generazione dello zip del EJB ", e);
	    }
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

    @SuppressWarnings("unchecked")
    protected RispostaControlli loadWsVersions(RecuperoExt ext) {
	RispostaControlli rs = myControlliWs.loadWsVersions(ext.getDescrizione());
	// if positive ...
	if (rs.isrBoolean()) {
	    ext.setWsVersions((HashMap<String, String>) rs.getrObject());
	}
	return rs;
    }

    // questo metodo viene usato nel WS di recupero stato conservazione UD
    public void recuperaLogStatoConservazioneUD(RispostaWSRecupero rispostaWs, RecuperoExt rec) {
	StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
	AvanzamentoWs tmpAvanzamentoWs = rispostaWs.getAvanzamento();

	if (rec.getParametriRecupero().getUtente() == null) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore: l'utente non è autenticato.");
	    return;
	}

	try {
	    RecuperoXmlGen tmpGen = new RecuperoXmlGen(rispostaWs);
	    tmpGen.generaLogStatoConservazione(rec);
	    tmpAvanzamentoWs.resetFase();
	} catch (Exception e) {
	    rispostaWs.setSeverity(SeverityEnum.ERROR);
	    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
		    "Errore nella fase di generazione dell'XML di risposta del EJB "
			    + e.getMessage());
	    log.error("Errore nella fase di generazione dell'XML di risposta del EJB ", e);
	}

	if (rispostaWs.getSeverity() == SeverityEnum.ERROR) {
	    myEsito.setXMLRichiesta(rec.getDatiXml());
	}
    }

}
