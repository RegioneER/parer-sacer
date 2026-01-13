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

package it.eng.parer.ws.richiestaAnnullamentoVersamenti.utils;

import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.ControlliWS;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.recupero.utils.XmlValidationEventHandler;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.InvioRichiestaAnnullamentoVersamentiExt;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.RispostaWSInvioRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.ejb.ControlliWSInvioRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.Costanti.TipiWSPerControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.esitoRichAnnullVers.RichiestaType;
import it.eng.parer.ws.xml.esitoRichAnnullVers.VersatoreType;
import it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.richAnnullVers.TipoAnnullamentoType;
import it.eng.parer.ws.xml.richAnnullVers.TipoVersamentoType;
import it.eng.parer.ws.xml.richAnnullVers.VersamentoDaAnnullareType;

/**
 *
 * @author Gilioli_P
 */
public class InvioRichiestaAnnullamentoVersamentiParser {

    private static final Logger log = LoggerFactory
            .getLogger(InvioRichiestaAnnullamentoVersamentiParser.class);
    private RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs;
    private RispostaControlli rispostaControlli;
    // Istanza della richiesta annullamento versamenti decodificata dall'XML di richiesta
    RichiestaAnnullamentoVersamenti richiesta = null;
    // stateless ejb per i controlli sul db
    ControlliSemantici controlliSemantici = null;
    // stateless ejb per verifica autorizzazione ws
    ControlliWS controlliEjb = null;
    // stateless ejb per i controlli specifici
    ControlliRecupero controlliRecupero = null;
    // singleton ejb - cache dei JAXBContext
    XmlContextCache xmlContextCache = null;
    // stateless ejb per i controlli specifici dell'annullamento versamenti ud
    ControlliWSInvioRichiestaAnnullamentoVersamenti controlliWSrav = null;

    public RispostaWSInvioRichiestaAnnullamentoVersamenti getRispostaWs() {
        return rispostaWs;
    }

    public InvioRichiestaAnnullamentoVersamentiParser(
            RispostaWSInvioRichiestaAnnullamentoVersamenti risp) {
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();
    }

    public void parseXML(String datiXml, InvioRichiestaAnnullamentoVersamentiExt ravExt) {
        EsitoRichiestaAnnullamentoVersamenti myEsito = rispostaWs
                .getEsitoRichiestaAnnullamentoVersamenti();
        StringReader tmpReader;

        // stateless ejb per i controlli sul db
        controlliSemantici = null;

        // stateless ejb per verifica autorizzazione
        controlliEjb = null;

        // recupera l'ejb singleton, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                xmlContextCache = (XmlContextCache) new InitialContext()
                        .lookup("java:module/XmlContextCache");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB singleton XMLContext " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB singleton XMLContext ", ex);
            }
        }

        ravExt.setXmlRichiesta(datiXml);
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            tmpReader = new StringReader(datiXml);
            XmlValidationEventHandler validationHandler = new XmlValidationEventHandler();
            try {
                Unmarshaller unmarshaller = xmlContextCache
                        .getRichAnnVersCtx_RichiestaAnnullamentoVersamenti().createUnmarshaller();
                unmarshaller.setSchema(xmlContextCache.getSchemaOfRichAnnVers());
                unmarshaller.setEventHandler(validationHandler);
                richiesta = (RichiestaAnnullamentoVersamenti) unmarshaller.unmarshal(tmpReader);
                ravExt.setRichiestaAnnullamentoVersamenti(richiesta);
            } catch (JAXBException e) {
                ValidationEvent event = validationHandler.getFirstErrorValidationEvent();
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.RICH_ANN_VERS_002);
                rispostaWs.setErrorMessage(MessaggiWSBundle
                        .getString(MessaggiWSBundle.RICH_ANN_VERS_002, event.getMessage()));
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RICH_ANN_VERS_002,
                        event.getMessage());
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setErrorCode(MessaggiWSBundle.RICH_ANN_VERS_002);
                rispostaWs.setErrorMessage(
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_002));
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RICH_ANN_VERS_002,
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_002));
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            popolaVersatoreRichiestaEsito(ravExt);
        }
        // se l'unmarshalling è andato bene, recupero la versione XML di richiesta
        // if (rispostaWs.getSeverity() == SeverityEnum.OK) {
        // myEsito.setVersioneXmlRichiesta(richiesta.getVersioneXmlRichiesta());
        // }
        // Recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                controlliSemantici = (ControlliSemantici) new InitialContext()
                        .lookup("java:module/ControlliSemantici");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB dei controlli semantici  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli semantici ", ex);
            }
        }

        // Recupera l'ejb dell'autenticazione, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                controlliEjb = (ControlliWS) new InitialContext().lookup("java:module/ControlliWS");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB di verifica delle autorizzazioni  "
                                + ex.getMessage());
                log.error("Errore nel recupero dell'EJB di verifica delle autorizzazioni ", ex);
            }
        }

        // recupera l'ejb dei controlli, se possibile - altrimenti segnala errore
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                controlliWSrav = (ControlliWSInvioRichiestaAnnullamentoVersamenti) new InitialContext()
                        .lookup("java:module/ControlliWSInvioRichiestaAnnullamentoVersamenti");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nel recupero dell'EJB dei Controlli Richiesta Annullamento Versamenti  "
                                + ex.getMessage());
                log.error(
                        "Errore nel recupero dell'EJB dei Controlli Richiesta Annullamento Versamenti ",
                        ex);
            }
        }

        /* CONTROLLO TAG VERSIONE XML (RICH_ANN_VERS_010) */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            if (!richiesta.getVersioneXmlRichiesta().equals(ravExt.getVersioneCalc())) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_010,
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_010));
                // Popolo i tag da dare in risposta in questo caso
                // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                // ravExt.getDataElaborazione());
                // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
            }
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR && !ravExt.getModificatoriWS()
                .contains(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING)
        // MEV#26446
                && !ravExt.getModificatoriWS().contains(Costanti.ModificatoriWS.TAG_ANNUL_FASC)
                // end MEV#26446
                && (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                        .isForzaAnnullamento() != null
                        || ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                                .isRichiestaDaPreIngest() != null)) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_016,
                    MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_016));
        }

        /* CONTROLLO TAG USERID (RICH_ANN_VERS_007) */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            if (!richiesta.getVersatore().getUserID().equals(ravExt.getNmUserid())) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_007,
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_007));
                // Popolo i tag da dare in risposta in questo caso
                // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                // ravExt.getDataElaborazione());
                // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
            }
        }

        // // posso usare il tag Utente?
        // if (rispostaWs.getSeverity() == SeverityEnum.OK
        // &&
        // !richiesta.getVersatore().getUserID().contains(Costanti.ModificatoriWS.TAG_REC_USR_DOC_COMP)
        // && parsedUnitaDoc.getVersatore().getUtente() != null
        // && !parsedUnitaDoc.getVersatore().getUtente().isEmpty()) {
        // rispostaWs.setSeverity(SeverityEnum.ERROR);
        // rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<Utente>");
        // myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        // }
        //
        // // posso usare il tag Utente?
        // if (rispostaWsFasc.getSeverity() == SeverityEnum.OK &&
        // parsedFasc.getVersatore().getUtente() != null
        // && !parsedFasc.getVersatore().getUtente().isEmpty()) {
        // rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
        // rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.XSD_011_003, "<Utente>");
        // myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.NEGATIVO);
        // }

        // MEV#26446
        /* CONTROLLO TAG TIPOVERSAMENTO (RICH_ANN_VERS_017) */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            if ((richiesta.getVersamentiDaAnnullare().getVersamentoDaAnnullare().stream()
                    .collect(Collectors.groupingBy(VersamentoDaAnnullareType::getTipoVersamento))
                    .values().stream().distinct().count() > 1)) {
                // I versamenti da annullare definiti nella richiesta devono avere stesso Tipo
                // Versamento
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_017,
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_017));
                // Popolo i tag da dare in risposta in questo caso
                // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                // ravExt.getDataElaborazione());
                // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
            }
        }

        /* CONTROLLO TAG IMMEDIATA (RICH_ANN_VERS_018) */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            Optional<VersamentoDaAnnullareType> optVersamento = richiesta.getVersamentiDaAnnullare()
                    .getVersamentoDaAnnullare().stream().findFirst();

            if (optVersamento.isPresent()) {
                VersamentoDaAnnullareType versamentoDaAnnullare = optVersamento.get();
                if (versamentoDaAnnullare.getTipoVersamento().value()
                        .equals(TipoVersamentoType.FASCICOLO.value())) {
                    if (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                            .isImmediata() != null
                            && !ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                                    .isImmediata()) {
                        // Per il Tipo Versamento specificato nella richiesta è necessario definire
                        // una richiesta
                        // Immediata
                        rispostaWs.setSeverity(SeverityEnum.ERROR);
                        rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_018,
                                MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_018));
                        // Popolo i tag da dare in risposta in questo caso
                        // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                        // ravExt.getDataElaborazione());
                        // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                        // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
                    }
                }
            }
        }

        /* CONTROLLO TAG RICHIESTADAPREINGEST (RICH_ANN_VERS_019) */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            Optional<VersamentoDaAnnullareType> optVersamento = richiesta.getVersamentiDaAnnullare()
                    .getVersamentoDaAnnullare().stream().findFirst();
            if (optVersamento.isPresent()) {
                VersamentoDaAnnullareType versamentoDaAnnullare = optVersamento.get();
                if (versamentoDaAnnullare.getTipoVersamento().value()
                        .equals(TipoVersamentoType.FASCICOLO.value())) {
                    if (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                            .isRichiestaDaPreIngest() != null
                            && ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                                    .isRichiestaDaPreIngest()) {
                        // Richiesta da PreIngest definita ma il Tipo Versamento specificato non lo
                        // supporta
                        rispostaWs.setSeverity(SeverityEnum.ERROR);
                        rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_019,
                                MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_019));
                        // Popolo i tag da dare in risposta in questo caso
                        // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                        // ravExt.getDataElaborazione());
                        // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                        // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
                    }
                }
            }
        }

        // RICH_ANN_VERS_020
        // RICH_ANN_VERS_021
        /* CONTROLLO TAG TIPOREGISTRO */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            Optional<VersamentoDaAnnullareType> optVersamento = richiesta.getVersamentiDaAnnullare()
                    .getVersamentoDaAnnullare().stream().findFirst();
            if (optVersamento.isPresent()) {
                VersamentoDaAnnullareType versamentoDaAnnullare = optVersamento.get();
                if (versamentoDaAnnullare.getTipoVersamento().value()
                        .equals(TipoVersamentoType.FASCICOLO.value())
                        && richiesta.getVersamentiDaAnnullare().getVersamentoDaAnnullare().stream()
                                .map(VersamentoDaAnnullareType::getTipoRegistro)
                                .anyMatch(x -> x != null)) {
                    // Per almeno un fascicolo definito nella richiesta è presente il Tipo Registro
                    // ma non è previsto
                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_020,
                            MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_020));
                    // Popolo i tag da dare in risposta in questo caso
                    // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                    // ravExt.getDataElaborazione());
                    // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                    // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
                } else if (versamentoDaAnnullare.getTipoVersamento().value()
                        .equals(TipoVersamentoType.UNITA_DOCUMENTARIA.value())
                        && richiesta.getVersamentiDaAnnullare().getVersamentoDaAnnullare().stream()
                                .map(VersamentoDaAnnullareType::getTipoRegistro)
                                .anyMatch(x -> x == null)) {
                    // Per almeno una unità documentaria definita nella richiesta è necessario
                    // specificare il Tipo
                    // Registro
                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_021,
                            MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_021));
                    // Popolo i tag da dare in risposta in questo caso
                    // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                    // ravExt.getDataElaborazione());
                    // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                    // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
                }
            }
        }
        // end MEV#26446

        // MEV 30721

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR
                && !ravExt.getModificatoriWS()
                        .contains(Costanti.ModificatoriWS.TAG_ANNUL_TIPO_ANNUL)
                && (ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                        .getTipoAnnullamento() != null)) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_024,
                    MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_024));
        }

        if (rispostaWs.getSeverity() != SeverityEnum.ERROR && ravExt.getModificatoriWS()
                .contains(Costanti.ModificatoriWS.TAG_ANNUL_TIPO_ANNUL)) {
            VersamentoDaAnnullareType versamentoDaAnnullare = richiesta.getVersamentiDaAnnullare()
                    .getVersamentoDaAnnullare().stream().findFirst().get();
            if (versamentoDaAnnullare.getTipoVersamento().value()
                    .equals(TipoVersamentoType.FASCICOLO.value())
                    && ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                            .getTipoAnnullamento().equals(TipoAnnullamentoType.CANCELLAZIONE)) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_025,
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_025));
            }

        }

        // RICH_ANNUL_VERS_004
        // RICH_ANNUL_VERS_005
        // RICH_ANNUL_VERS_006
        /* CONTROLLO VERSATORE (ambiente, ente e struttura) */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            CSVersatore tmpCSVersatore = new CSVersatore();
            tmpCSVersatore.setAmbiente(richiesta.getVersatore().getAmbiente());
            tmpCSVersatore.setEnte(richiesta.getVersatore().getEnte());
            tmpCSVersatore.setStruttura(richiesta.getVersatore().getStruttura());

            rispostaControlli.reset();
            rispostaControlli = controlliSemantici.checkIdStrut(tmpCSVersatore,
                    TipiWSPerControlli.ANNULLAMENTO);
            if (rispostaControlli.getrLong() < 1) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
                // Popolo i tag da dare in risposta in questo caso
                // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                // ravExt.getDataElaborazione());
                // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
            } else {
                // salvo idStruttura
                ravExt.setIdStrut(rispostaControlli.getrLong());
            }
        }

        /*
         * CONTROLLO ABILITAZIONE SERVIZIO -- (RICH_ANN_VERS_008) N.B. LO METTO QUI PERCHE' DEVO
         * AVER CALCOLATO LA STRUTTURA PRIMA!
         */
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            ravExt.getUser().setIdOrganizzazioneFoglia(new BigDecimal(ravExt.getIdStrut()));
            rispostaControlli.reset();
            rispostaControlli = controlliEjb.checkAuthWS(ravExt.getUser(), ravExt.getDescrizione(),
                    TipiWSPerControlli.ANNULLAMENTO);
            if (!rispostaControlli.isrBoolean()) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(MessaggiWSBundle.RICH_ANN_VERS_008,
                        MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_008));
                // Popolo i tag da dare in risposta in questo caso
                // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                // ravExt.getDataElaborazione());
                // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
            }
        }

        // Verifica se esiste già una richiesta appartenente alla struttura comunicata
        // con lo stesso userId e con stato corrente diverso da INVIO_FALLITO
        // RICH_ANNUL_VERS_009
        if (rispostaWs.getSeverity() != SeverityEnum.ERROR) {
            // recupero.getParametriRecupero().getUtente().setIdOrganizzazioneFoglia(new
            // BigDecimal(recupero.getIdStruttura()));
            rispostaControlli.reset();
            rispostaControlli = controlliWSrav.checkRichiestaEsistenteInvioFallito(
                    ravExt.getIdStrut(),
                    ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getCodice());
            if (!rispostaControlli.isrBoolean()) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
                // Popolo i tag da dare in risposta in questo caso
                // popolaVersioniEDataEsito(myEsito, ravExt.getVersioneCalc(),
                // ravExt.getDataElaborazione());
                // popolaVersatoreRichiestaEsito(myEsito, ravExt);
                // rispostaWs.setEsitoRichiestaAnnullamentoVersamenti(myEsito);
            }
        }
    }

    // private void popolaVersioniEDataEsito(EsitoRichiestaAnnullamentoVersamenti esito, String
    // versioneXsd, Date
    // adesso) {
    // esito.setVersioneXmlEsito("1.0");
    // esito.setVersioneXmlRichiesta(versioneXsd);
    // esito.setDataRichiesta(adesso);
    // }
    private void popolaVersatoreRichiestaEsito(InvioRichiestaAnnullamentoVersamentiExt ravExt) {
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().setVersatore(new VersatoreType());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersatore().setAmbiente(
                ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getAmbiente());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersatore()
                .setEnte(ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getEnte());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersatore().setStruttura(
                ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getStruttura());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersatore()
                .setUserID(ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getUserID());
        if (ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getUtente() != null
                && !ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getUtente()
                        .isEmpty()) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getVersatore().setUtente(
                    ravExt.getRichiestaAnnullamentoVersamenti().getVersatore().getUtente());
        }
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().setRichiesta(new RichiestaType());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta()
                .setCodice(ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getCodice());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta().setDescrizione(
                ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getDescrizione());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta().setMotivazione(
                ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().getMotivazione());
        rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta().setImmediata(
                ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta().isImmediata());
        if (ravExt.getModificatoriWS().contains(Costanti.ModificatoriWS.TAG_ANNUL_FORZA_PING)) {
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta()
                    .setForzaAnnullamento(ravExt.getRichiestaAnnullamentoVersamenti().getRichiesta()
                            .isForzaAnnullamento());
            rispostaWs.getEsitoRichiestaAnnullamentoVersamenti().getRichiesta()
                    .setRichiestaDaPreIngest(ravExt.getRichiestaAnnullamentoVersamenti()
                            .getRichiesta().isRichiestaDaPreIngest());
        }
    }
}
