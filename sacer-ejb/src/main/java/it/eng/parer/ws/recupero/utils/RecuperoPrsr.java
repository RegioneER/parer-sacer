/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.utils;

import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.dto.ParametriParser;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.parer.ws.xml.versRespStato.ECEsitoPosNegType;
import it.eng.spagoLite.security.User;
import java.io.StringReader;
import java.math.BigDecimal;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
public class RecuperoPrsr {

    private static final Logger log = LoggerFactory.getLogger(RecuperoPrsr.class);
    private RispostaWSRecupero rispostaWs;
    private RispostaControlli rispostaControlli;
    // l'istanza della request decodificata dall'XML di versamento
    Recupero parsedUnitaDoc = null;
    // stateless ejb per i controlli sul db
    ControlliSemantici controlliSemantici = null;
    // stateless ejb per verifica autorizzazione ws
    ControlliWS controlliEjb = null;
    // stateless ejb per i controlli specifici del recupero
    ControlliRecupero controlliRecupero = null;
    // singleton ejb - cache dei JAXBContext
    XmlContextCache xmlContextCache = null;

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public RecuperoPrsr(RispostaWSRecupero risp) {
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();
    }

    public void parseXML(String datiXml, RecuperoExt recupero) {
        StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
        AvanzamentoWs myAvanzamentoWs = rispostaWs.getAvanzamento();
        StringReader tmpReader;
        parsedUnitaDoc = null;

        // stateless ejb per i controlli sul db
        controlliSemantici = null;

        // stateless ejb per verifica autorizzazione
        controlliEjb = null;

        // recupera l'ejb singleton, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                xmlContextCache = (XmlContextCache) new InitialContext().lookup("java:module/XmlContextCache");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB singleton XMLContext " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB singleton XMLContext ", ex);
            }
        }

        recupero.setDatiXml(datiXml);
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            XmlValidationEventHandler validationHandler = new XmlValidationEventHandler();
            tmpReader = new StringReader(datiXml);
            try {
                myAvanzamentoWs.setFase("Unmarshall XML").logAvanzamento();

                Unmarshaller unmarshaller = xmlContextCache.getVersReqStatoCtx_Recupero().createUnmarshaller();
                unmarshaller.setSchema(xmlContextCache.getSchemaOfVersReqStato());
                unmarshaller.setEventHandler(validationHandler);
                parsedUnitaDoc = (Recupero) unmarshaller.unmarshal(tmpReader);
                recupero.setStrutturaRecupero(parsedUnitaDoc);
            } catch (UnmarshalException e) {
                ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode("-1");
                rispostaWs.setErrorMessage(
                        "Errore: XML malformato nel blocco di dati generali. Eccezione: " + event.getMessage());
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_001_001, event.getMessage());
            } catch (Exception e) {
                ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode("-1");
                rispostaWs.setErrorMessage(
                        "Errore di validazione del blocco di dati generali. Eccezione: " + event.getMessage());
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_001_002, event.getMessage());
            }
        }

        // se l'unmarshalling è andato bene, recupero la versione XML di versamento
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            myEsito.setVersioneXMLChiamata(parsedUnitaDoc.getVersione());
            myAvanzamentoWs.setFase("Unmarshall OK")
                    .setChAnno(Long.toString(parsedUnitaDoc.getChiave().getAnno().longValue()))
                    .setChNumero(parsedUnitaDoc.getChiave().getNumero())
                    .setChRegistro(parsedUnitaDoc.getChiave().getTipoRegistro())
                    .setVrsAmbiente(parsedUnitaDoc.getVersatore().getAmbiente())
                    .setVrsEnte(parsedUnitaDoc.getVersatore().getEnte())
                    .setVrsStruttura(parsedUnitaDoc.getVersatore().getStruttura())
                    .setVrsUser(parsedUnitaDoc.getVersatore().getUserID()).logAvanzamento();
        }

        myAvanzamentoWs.setFase("verifica semantica richiesta - inizio").logAvanzamento();
        /*
         * come prima cosa verifico che il versatore e la versione dichiarati nel WS coincidano con quelli nell'xml
         */
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (!parsedUnitaDoc.getVersione().equals(recupero.getVersioneWsChiamata())) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.UD_001_013, parsedUnitaDoc.getVersione());
                myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (!parsedUnitaDoc.getVersatore().getUserID().equals(recupero.getLoginName())) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.UD_001_005, parsedUnitaDoc.getVersatore().getUserID());
                myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
            }
        }

        // un po' di controlli relativi alla versione del WS invocata...
        // posso usare il tag Utente?
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && !recupero.getModificatoriWSCalc().contains(Costanti.ModificatoriWS.TAG_REC_USR_DOC_COMP)
                && parsedUnitaDoc.getVersatore().getUtente() != null
                && !parsedUnitaDoc.getVersatore().getUtente().isEmpty()) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<Utente>");
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        }

        // posso usare il tag IDDocumento?
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && !recupero.getModificatoriWSCalc().contains(Costanti.ModificatoriWS.TAG_REC_USR_DOC_COMP)
                && parsedUnitaDoc.getChiave().getIDDocumento() != null
                && !parsedUnitaDoc.getChiave().getIDDocumento().isEmpty()) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<IDDocumento>");
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        }

        // posso usare il tag OrdinePresentazioneComponente?
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && !recupero.getModificatoriWSCalc().contains(Costanti.ModificatoriWS.TAG_REC_USR_DOC_COMP)
                && parsedUnitaDoc.getChiave().getOrdinePresentazioneComponente() != null) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<OrdinePresentazioneComponente>");
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        }

        // posso usare il tag TipoDocumento?
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && !recupero.getModificatoriWSCalc().contains(Costanti.ModificatoriWS.TAG_REC_USR_DOC_COMP)
                && parsedUnitaDoc.getChiave().getTipoDocumento() != null) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<TipoDocumento>");
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        }

        //
        // recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                controlliSemantici = (ControlliSemantici) new InitialContext().lookup("java:module/ControlliSemantici");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB dei controlli semantici  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli semantici ", ex);
            }
        }

        // recupera l'ejb dell'autenticazione, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                controlliEjb = (ControlliWS) new InitialContext().lookup("java:module/ControlliWS");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB di verifica delle autorizzazioni  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB di verifica delle autorizzazioni ", ex);
            }
        }

        // recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                controlliRecupero = (ControlliRecupero) new InitialContext().lookup("java:module/ControlliRecupero");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB dei Controlli Recupero  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei Controlli Recupero ", ex);
            }
        }

        CSVersatore tmpCSVersatore = new CSVersatore();
        // verifica il versatore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            tmpCSVersatore.setAmbiente(parsedUnitaDoc.getVersatore().getAmbiente());
            tmpCSVersatore.setEnte(parsedUnitaDoc.getVersatore().getEnte());
            tmpCSVersatore.setStruttura(parsedUnitaDoc.getVersatore().getStruttura());

            rispostaControlli.reset();
            rispostaControlli = controlliSemantici.checkIdStrut(tmpCSVersatore, TipiWSPerControlli.VERSAMENTO_RECUPERO);
            if (rispostaControlli.getrLong() < 1) {
                setRispostaWsError();

                myEsito.getEsitoChiamataWS().setIdentificazioneVersatore(rispostaControlli.getDsErr());
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                // salvo idstruttura
                recupero.setIdStruttura(rispostaControlli.getrLong());
                myEsito.getEsitoChiamataWS().setIdentificazioneVersatore("POSITIVO");
            }
        }

        /*
         * Nota: non verifico l'eventuale presenza obbligatoria dell'utente alternativo poiché è per definizione
         * opzionale e gestito quando presente
         * 
         * MEV #21799 : attenzione questa verifica non viene più effettuata sul tag <Utente>,
         * getGestioneUtenteAlternativo() per default è Ignorato
         */
        // se necessario provo a gestire l'eventuale utente umano in override sull'utente automatico
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getGestioneUtenteAlternativo() == ParametriParser.TipiGestione.Gestito
                && parsedUnitaDoc.getVersatore().getUtente() != null
                && !parsedUnitaDoc.getVersatore().getUtente().isEmpty()) {

            // verifico l'esistenza dell'utente e il suo stato (attivo, scaduto, ecc)
            rispostaControlli = controlliEjb.checkUtente(parsedUnitaDoc.getVersatore().getUtente());
            if (rispostaControlli.isrBoolean()) {
                // se l'utente è usabile, effettuo l'override.
                // da questo momento il WS gestirà questo utente come se fosse il chiamante
                recupero.setLoginName(parsedUnitaDoc.getVersatore().getUtente());
                recupero.getParametriRecupero().setUtente((User) rispostaControlli.getrObject());
            } else {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
            }
        }

        // verifica se l'utente (umano o automatico o umano in override) è autorizzato ad usare il WS sulla struttura
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            recupero.getParametriRecupero().getUtente()
                    .setIdOrganizzazioneFoglia(new BigDecimal(recupero.getIdStruttura()));
            rispostaControlli.reset();
            rispostaControlli = controlliEjb.checkAuthWS(recupero.getParametriRecupero().getUtente(),
                    recupero.getDescrizione(), TipiWSPerControlli.VERSAMENTO_RECUPERO);
            if (!rispostaControlli.isrBoolean()) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
            }
        }
        // vedo MEV#21799 : questo controllo non viene più fatto per il recupero

        CSChiave tmpCSChiave = new CSChiave();
        // verifica la chiave
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            tmpCSChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
            tmpCSChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
            tmpCSChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());

            rispostaControlli.reset();
            if (recupero.getParametriParser().isLeggiAncheUdAnnullate()) {
                rispostaControlli = controlliSemantici.checkChiave(tmpCSChiave, recupero.getIdStruttura(),
                        ControlliSemantici.TipiGestioneUDAnnullate.CARICA);
            } else {
                rispostaControlli = controlliSemantici.checkChiave(tmpCSChiave, recupero.getIdStruttura(),
                        ControlliSemantici.TipiGestioneUDAnnullate.CONSIDERA_ASSENTE);
            }

            if (rispostaControlli.isrBoolean() || rispostaControlli.getrLong() == -1) {
                setRispostaWsError();
                myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                // OK - popolo la risposta versamento
                myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.POSITIVO);
                // memorizzo l'ID della chiave Unità doc trovata
                recupero.getParametriRecupero().setIdUnitaDoc(rispostaControlli.getrLong());
                /*
                 * memorizzo il tipo di salvataggio (blob in tabella o filesystem)
                 */
                recupero.setTipoSalvataggioFile(CostantiDB.TipoSalvataggioFile.valueOf(rispostaControlli.getrString()));
                recupero.getParametriRecupero().setIdRegistro(rispostaControlli.getrLongExtended());
                // normalized cd_key
                recupero.getParametriRecupero().setNumeroUdNormalized((String) rispostaControlli.getrMap()
                        .get(RispostaControlli.ValuesOnrMap.CD_KEY_NORMALIZED.name()));
            }
        }

        // verifica struttura cessata
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliSemantici.checkStrutCessata(recupero.getIdStruttura());
            if (!rispostaControlli.isrBoolean()) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
            }
        }

        // gestione URN pregressi
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.calcCdKeyNormAndUrnPreg(tmpCSVersatore, tmpCSChiave,
                    recupero.getParametriRecupero().getIdRegistro(), recupero.getParametriRecupero().getIdUnitaDoc(),
                    recupero.getParametriRecupero().getNumeroUdNormalized());
            if (!rispostaControlli.isrBoolean()) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.NEGATIVO);
            }
        }

        // verifico se devo ricevere la chiave del documento
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getPresenzaDocumento() == ParametriParser.TipiPresenzaTag.Richiesto
                && (parsedUnitaDoc.getChiave().getIDDocumento() == null
                        || parsedUnitaDoc.getChiave().getIDDocumento().isEmpty())) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_001, "<IDDocumento>");
            myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
        }

        // verifico se posso gestire la chiave del documento
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getGestioneDocumento() == ParametriParser.TipiGestione.Gestito
                && parsedUnitaDoc.getChiave().getIDDocumento() != null
                && !parsedUnitaDoc.getChiave().getIDDocumento().isEmpty()) {
            rispostaControlli = controlliRecupero.checkIdDocumentoinUD(recupero.getParametriRecupero().getIdUnitaDoc(),
                    parsedUnitaDoc.getChiave().getIDDocumento());
            if (rispostaControlli.isrBoolean()) {
                // memorizzo l'ID della chiave doc trovata
                recupero.getParametriRecupero().setIdDocumento(rispostaControlli.getrLong());
                switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
                case UNI_DOC:
                    recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.DOC);
                    break;
                case UNI_DOC_DIP:
                    recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.DOC_DIP);
                    break;
                case UNI_DOC_DIP_ESIBIZIONE:
                    recupero.getParametriRecupero()
                            .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.DOC_DIP_ESIBIZIONE);
                    break;
                default:
                    // UNI_DOC_UNISYNCRO non ha una versione per Documento e Componente
                    // EVO#20972
                    // UNI_DOC_UNISYNCRO_V2 non ha una versione per Documento e Componente
                    // end EVO#20972
                    break;
                }
            } else {
                setRispostaWsError();
                myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            }
        }

        // verifico se devo ricevere la chiave del componente
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getPresenzaComponente() == ParametriParser.TipiPresenzaTag.Richiesto
                && (parsedUnitaDoc.getChiave().getOrdinePresentazioneComponente() == null)) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_001, "<OrdinePresentazioneComponente>");
            myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
        }

        // verifico se posso gestire la chiave del componente
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getGestioneComponente() == ParametriParser.TipiGestione.Gestito
                && parsedUnitaDoc.getChiave().getOrdinePresentazioneComponente() != null) {

            if (recupero.getParametriRecupero().getIdDocumento() != null) {
                rispostaControlli = controlliRecupero.checkIdComponenteinDoc(
                        recupero.getParametriRecupero().getIdDocumento(),
                        parsedUnitaDoc.getChiave().getOrdinePresentazioneComponente());
                if (rispostaControlli.isrBoolean()) {
                    // memorizzo l'ID della chiave comp trovata
                    recupero.getParametriRecupero().setIdComponente(rispostaControlli.getrLong());
                    switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
                    case DOC:
                        if (rispostaControlli.getrLongExtended() == -1) {
                            recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP);
                        } else {
                            recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.SUB_COMP);
                        }
                        break;
                    case DOC_DIP:
                        // se il riferimento è ad un sottocomponente lo tratto come un componente, e non trovo nulla
                        recupero.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP);
                        break;
                    case DOC_DIP_ESIBIZIONE:
                        recupero.getParametriRecupero()
                                .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP_ESIBIZIONE);
                        break;
                    default:
                        // UNI_DOC_UNISYNCRO non ha una versione per Documento e Componente
                        // EVO#20972
                        // UNI_DOC_UNISYNCRO_V2 non ha una versione per Documento e Componente
                        // end EVO#20972
                        break;
                    }
                } else {
                    setRispostaWsError();
                    myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                }
            } else {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.COMP_010_002,
                        parsedUnitaDoc.getChiave().getOrdinePresentazioneComponente());
                myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
            }
        }

        // verifico se devo ricevere la chiave del tipo documento
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getPresenzaTipoDocumento() == ParametriParser.TipiPresenzaTag.Richiesto
                && (parsedUnitaDoc.getChiave().getTipoDocumento() == null)) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_001, "<TipoDocumento>");
            myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
        }

        // verifico se posso gestire la chiave del tipo documento
        if (rispostaWs.getSeverity() == SeverityEnum.OK
                && recupero.getParametriParser().getGestioneTipoDocumento() == ParametriParser.TipiGestione.Gestito
                && parsedUnitaDoc.getChiave().getTipoDocumento() != null) {

            if (recupero.getParametriRecupero().getIdDocumento() == null) {
                CSChiave csChiave = new CSChiave();
                csChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
                csChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());
                csChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
                String descChiaveUD = MessaggiWSFormat.formattaUrnPartUnitaDoc(csChiave);
                rispostaControlli = controlliRecupero.checkTipoDocumentoperStrut(recupero.getIdStruttura(),
                        parsedUnitaDoc.getChiave().getTipoDocumento(), descChiaveUD);
                if (rispostaControlli.isrBoolean()) {
                    rispostaControlli = controlliRecupero.checkTipoDocumentoinUD(
                            recupero.getParametriRecupero().getIdUnitaDoc(),
                            parsedUnitaDoc.getChiave().getTipoDocumento(), descChiaveUD);
                    if (rispostaControlli.isrBoolean()) {
                        // memorizzo l'ID della chiave tipo doc trovata
                        recupero.getParametriRecupero().setIdTipoDoc(rispostaControlli.getrLong());
                    } else {
                        setRispostaWsError();
                        myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
                        rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                    }
                } else {
                    setRispostaWsError();
                    myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                }
            } else {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.DOC_011_001);
                myEsito.getEsitoChiamataWS().setIdentificazioneChiave(ECEsitoPosNegType.NEGATIVO);
            }
        }

        //
        myAvanzamentoWs.setFase("verifica semantica richiesta - fine").logAvanzamento();
    }

    private void setRispostaWsError() {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }
}
