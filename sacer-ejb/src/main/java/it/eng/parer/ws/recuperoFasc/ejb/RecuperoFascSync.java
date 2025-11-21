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
package it.eng.parer.ws.recuperoFasc.ejb;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.recuperoFasc.dto.ParametriFascParser;
import it.eng.parer.ws.recuperoFasc.dto.ParametriRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.dto.RecuperoFascExt;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.utils.RecuperoFascPrsr;
import it.eng.parer.ws.recuperoFasc.utils.RecuperoXmlFascGen;
import it.eng.parer.ws.recuperoFasc.utils.RecuperoZipFascGen;
import it.eng.parer.ws.recuperoFasc.utils.XmlValidationEventHandler;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.versReqStatoFasc.RecuperoFascicolo;
import it.eng.parer.ws.xml.versRespStatoFasc.ECEsitoExtType;
import it.eng.parer.ws.xml.versRespStatoFasc.ECEsitoPosNegType;
import it.eng.parer.ws.xml.versRespStatoFasc.EsitoChiamataWSType;
import it.eng.parer.ws.xml.versRespStatoFasc.EsitoGenericoType;
import it.eng.parer.ws.xml.versRespStatoFasc.StatoConservazioneFasc;
import it.eng.spagoLite.security.User;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "RecuperoFascSync")
@LocalBean
public class RecuperoFascSync {

    @EJB
    private ControlliWS myControlliWs;
    //
    @EJB
    private ControlliRecuperoFasc controlliRecuperoFasc;

    private static final Logger log = LoggerFactory.getLogger(RecuperoFascSync.class);
    //

    public void initRispostaWs(RispostaWSRecuperoFasc rispostaWsFasc, AvanzamentoWs avanzamento,
            RecuperoFascExt recFasc) {

        log.debug("sono nel metodo init");
        StatoConservazioneFasc myEsito = new StatoConservazioneFasc();

        RispostaControlli rs = this.loadWsVersions(recFasc);

        rispostaWsFasc.setSeverity(SeverityEnum.OK);
        rispostaWsFasc.setErrorCode("");
        rispostaWsFasc.setErrorMessage("");

        // prepara la classe esito e la aggancia alla rispostaWS
        myEsito.setEsitoGenerale(new EsitoGenericoType());
        rispostaWsFasc.setIstanzaEsito(myEsito);

        // aggiunge l'istanza della classe parametri di recupero fascicolo
        recFasc.setParametriRecuperoFasc(new ParametriRecuperoFasc());
        recFasc.getParametriRecuperoFasc()
                .setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.SERVIZIO);

        // aggiunge l'istanza della classe parametri del parser
        recFasc.setParametriFascParser(new ParametriFascParser());

        // aggancia alla rispostaWS
        rispostaWsFasc.setAvanzamento(avanzamento);

        XMLGregorianCalendar d = XmlDateUtility.dateToXMLGregorianCalendar(new Date());
        myEsito.setDataRichiestaStato(d);

        //
        if (!rs.isrBoolean()) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsError(rs.getCodErr(), rs.getDsErr());
        } else {
            myEsito.getEsitoGenerale().setCodiceEsito(ECEsitoExtType.POSITIVO);
            myEsito.getEsitoGenerale().setCodiceErrore("");
            myEsito.getEsitoGenerale().setMessaggioErrore("");

            myEsito.setVersione(recFasc.getDescrizione().getVersione(recFasc.getWsVersions()));

            myEsito.setEsitoChiamataWS(new EsitoChiamataWSType());
            myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.POSITIVO);
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.POSITIVO);
        }
    }

    public void verificaVersione(String versione, RispostaWSRecuperoFasc rispostaWsFasc,
            RecuperoFascExt recFasc) {
        StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
        RispostaControlli tmpRispostaControlli = null;
        recFasc.setVersioneWsChiamata(versione);
        myEsito.setVersioneXMLChiamata(versione);

        tmpRispostaControlli = myControlliWs.checkVersione(versione,
                recFasc.getDescrizione().getNomeWs(), recFasc.getWsVersions(),
                TipiWSPerControlli.VERSAMENTO_RECUPERO_FASC);
        if (!tmpRispostaControlli.isrBoolean()) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsError(tmpRispostaControlli.getCodErr(),
                    tmpRispostaControlli.getDsErr());
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        } else {
            recFasc.checkVersioneRequest(versione);
            myEsito.setVersione(recFasc.getVersioneCalc());
        }
    }

    public void verificaCredenziali(String loginName, String password, String indirizzoIp,
            RispostaWSRecuperoFasc rispostaWsFasc, RecuperoFascExt recFasc) {
        verificaCredenziali(loginName, password, indirizzoIp, rispostaWsFasc, recFasc, null);
    }

    public void verificaCredenziali(String loginName, String password, String indirizzoIp,
            RispostaWSRecuperoFasc rispostaWsFasc, RecuperoFascExt recFasc, String certCommonName) {
        StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
        RispostaControlli tmpRispostaControlli = null;

        tmpRispostaControlli = myControlliWs.checkCredenziali(loginName, password, indirizzoIp,
                TipiWSPerControlli.VERSAMENTO_RECUPERO_FASC, certCommonName);
        if (!tmpRispostaControlli.isrBoolean()) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsError(tmpRispostaControlli.getCodErr(),
                    tmpRispostaControlli.getDsErr());
            myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
        }

        /* logga il login name oppure il CM del certificato se passato */
        if (certCommonName != null && !certCommonName.isEmpty()) {
            recFasc.setLoginName(certCommonName);
        } else {
            recFasc.setLoginName(loginName);
        }
        recFasc.getParametriRecuperoFasc().setUtente((User) tmpRispostaControlli.getrObject());
    }

    public void parseXML(String datiXml, RispostaWSRecuperoFasc rispostaWsFasc,
            RecuperoFascExt recFasc) {
        StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
        AvanzamentoWs tmpAvanzamentoWs = rispostaWsFasc.getAvanzamento();

        if (recFasc.getParametriRecuperoFasc().getUtente() == null) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore: l'utente non è autenticato.");
            return;
        }

        try {
            RecuperoFascPrsr tmpPrsr = new RecuperoFascPrsr(rispostaWsFasc);
            tmpPrsr.parseXML(datiXml, recFasc);
            tmpAvanzamentoWs.resetFase();
        } catch (Exception e) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella fase di parsing dell'XML del EJB " + e.getMessage());
            log.error("Eccezione nella fase di parsing dell'XML del EJB ", e);
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.ERROR) {
            myEsito.setXMLRichiesta(datiXml);
        }
    }

    // questo metodo viene usato nel WS di recupero stato conservazione FASC
    public void recuperaStatoConservazioneFASC(RispostaWSRecuperoFasc rispostaWsFasc,
            RecuperoFascExt recFasc) {
        StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
        AvanzamentoWs tmpAvanzamentoWs = rispostaWsFasc.getAvanzamento();

        if (recFasc.getParametriRecuperoFasc().getUtente() == null) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore: l'utente non è autenticato.");
            return;
        }

        try {
            RecuperoXmlFascGen tmpFascGen = new RecuperoXmlFascGen(rispostaWsFasc);
            tmpFascGen.generaStatoConservazioneFasc(recFasc);
            tmpAvanzamentoWs.resetFase();
        } catch (Exception e) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella fase di generazione dell'XML di risposta del EJB "
                            + e.getMessage());
            log.error("Errore nella fase di generazione dell'XML di risposta del EJB ", e);
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.ERROR) {
            myEsito.setXMLRichiesta(recFasc.getDatiXml());
        }
    }

    // questo metodo viene usato nel WS di recupero FASC
    public void recuperaOggettoFasc(RispostaWSRecuperoFasc rispostaWsFasc,
            RecuperoFascExt recuperoFasc, String path) {
        StatoConservazioneFasc myEsito = rispostaWsFasc.getIstanzaEsito();
        AvanzamentoWs tmpAvanzamentoWs = rispostaWsFasc.getAvanzamento();
        boolean tentaRecuperoDIP = true;

        if (recuperoFasc.getParametriRecuperoFasc().getUtente() == null) {
            rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
            rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore: l'utente non è autenticato.");
            return;
        }

        // verifica se è stato prodotto l'AIP nel caso venga richiesto il recupero AIP
        // se non lo trova rende un errore ed esce
        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            if (recuperoFasc.getParametriRecuperoFasc()
                    .getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.FASC_UNISYNCRO) {
                RispostaControlli rc = controlliRecuperoFasc.contaXMLIndiceAIPFasc(
                        recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
                if (rc.getrLong() == 0) {
                    StringReader tmpReader = new StringReader(recuperoFasc.getDatiXml());
                    XmlContextCache xmlContextCache = null;
                    try {
                        xmlContextCache = (XmlContextCache) new InitialContext()
                                .lookup("java:module/XmlContextCache");
                    } catch (NamingException ex) {
                        rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                        rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                "Errore nel recupero dell'EJB singleton XMLContext "
                                        + ex.getMessage());
                        log.error("Errore nel recupero dell'EJB singleton XMLContext ", ex);
                        return;
                    }
                    XmlValidationEventHandler validationHandler = new XmlValidationEventHandler();
                    RecuperoFascicolo parsedFasc = null;
                    try {

                        Unmarshaller unmarshaller = xmlContextCache
                                .getVersReqStatoFascCtx_RecuperoFasc().createUnmarshaller();
                        unmarshaller.setSchema(xmlContextCache.getSchemaOfVersReqStatoFasc());
                        unmarshaller.setEventHandler(validationHandler);
                        parsedFasc = (RecuperoFascicolo) unmarshaller.unmarshal(tmpReader);
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

                    CSChiaveFasc tmpCSChiaveFasc = new CSChiaveFasc();
                    tmpCSChiaveFasc.setAnno(parsedFasc.getChiave().getAnno().intValue());
                    tmpCSChiaveFasc.setNumero(parsedFasc.getChiave().getNumero());
                    rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                    rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.RECFAS_001_001,
                            MessaggiWSFormat.formattaUrnPartFasc(tmpCSChiaveFasc));
                    return;
                }
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            try {
                RecuperoZipFascGen tmpFascGen = new RecuperoZipFascGen(rispostaWsFasc);
                tmpFascGen.generaZipOggettoFasc(path, recuperoFasc);
                tmpAvanzamentoWs.resetFase();
            } catch (Exception e) {
                rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella fase di generazione dello zip del EJB " + e.getMessage());
                log.error("Errore nella fase di generazione dello zip del EJB ", e);
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.ERROR) {
            myEsito.setXMLRichiesta(recuperoFasc.getDatiXml());
        }
    }

    protected RispostaControlli loadWsVersions(RecuperoFascExt ext) {
        RispostaControlli rs = myControlliWs.loadWsVersions(ext.getDescrizione());
        // if positive ...
        if (rs.isrBoolean()) {
            ext.setWsVersions((HashMap<String, String>) rs.getrObject());
        }
        return rs;
    }

}
