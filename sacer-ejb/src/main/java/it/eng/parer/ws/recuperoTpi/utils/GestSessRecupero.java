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
package it.eng.parer.ws.recuperoTpi.utils;

import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.ControlliTpi;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.recuperoTpi.dto.DatiSessioneRecupero;
import it.eng.parer.ws.recuperoTpi.ejb.ControlliRecTpi;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
public class GestSessRecupero {

    private static final Logger log = LoggerFactory.getLogger(GestSessRecupero.class);
    private RispostaWSRecupero rispostaWs;
    private RispostaControlli rispostaControlli;
    // EJB:
    // stateless ejb per i controlli TPI
    ControlliTpi controlliTpi = null;
    // stateless ejb per i controlli relativi alle sessioni di recupero
    ControlliRecTpi controlliRecTpi = null;
    // stateless ejb per i controlli sul db
    ControlliSemantici controlliSemantici = null;
    // stateless ejb di controlli generici di recupero dei file
    ControlliRecupero controlliRecupero = null;

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public GestSessRecupero(RispostaWSRecupero risp) {
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();

        try {
            controlliSemantici = (ControlliSemantici) new InitialContext()
                    .lookup("java:module/ControlliSemantici");
        } catch (NamingException ex) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "GestSessRecupero Errore nel recupero dell'EJB dei controlli semantici  "
                            + ex.getMessage());
            log.error("Errore nel recupero dell'EJB dei controlli semantici ", ex);
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                controlliTpi = (ControlliTpi) new InitialContext()
                        .lookup("java:module/ControlliTpi");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecupero Errore nel recupero dell'EJB dei controlli TPI  "
                                + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli TPI ", ex);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                controlliRecTpi = (ControlliRecTpi) new InitialContext()
                        .lookup("java:module/ControlliRecTpi");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecupero Errore nel recupero dell'EJB dei controlli recupero TPI  "
                                + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli  recupero TPI ", ex);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                controlliRecupero = (ControlliRecupero) new InitialContext()
                        .lookup("java:module/ControlliRecupero");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecupero Errore nel recupero dell'EJB ControlliRecupero  "
                                + ex.getMessage());
                log.error("Errore nel recupero dell'EJB ControlliRecupero ", ex);
            }
        }
    }

    public void caricaParametri(RecuperoExt recupero) {
        HashMap<String, String> tpiDefaults = null;
        rispostaControlli.reset();
        rispostaControlli = controlliSemantici
                .caricaDefaultDaDBParametriApplic(CostantiDB.TipoParametroAppl.TPI);
        if (rispostaControlli.isrBoolean() == false) {
            setRispostaWsError();
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        } else {
            tpiDefaults = (HashMap<String, String>) rispostaControlli.getrObject();
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            DateFormat dateFormat = new SimpleDateFormat(Costanti.TPI_DATA_PATH_FMT_STRING);
            String dataFine = tpiDefaults != null
                    ? tpiDefaults.get(CostantiDB.ParametroAppl.TPI_DATA_FINE_USO_BLOB)
                    : null;
            try {
                Date date = dateFormat.parse(dataFine);
                DatiSessioneRecupero datiSessioneRecupero = new DatiSessioneRecupero();
                recupero.setDatiSessioneRecupero(datiSessioneRecupero);
                datiSessioneRecupero.setDataFineUsoBlob(date);
            } catch (ParseException e) {
                setRispostaWsError();
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecupero.caricaParametri - TPI_DATA_FINE_USO_BLOB: "
                                + e.getMessage());
                log.error("Eccezione nella decodifica del parametro TPI_DATA_FINE_USO_BLOB " + e);
            }
        }

        /*
         * se il TPI non è stato installato, vuol dire che tutta la gestione asincrona del
         * versamento basata su TIVOLI è inutilizabile. In questo caso lo storage dei documenti
         * avviene su una tabella di blob dedicata chiamata ARO_FILE_COMP con struttura identica a
         * ARO_CONTENUTO_COMP
         */
        recupero.setTpiAbilitato(false);
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK
                && recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE) {
            rispostaControlli = controlliTpi.verificaAbilitazioneTpi();
            if (rispostaControlli.isrBoolean()) {
                recupero.setTpiAbilitato(true);
            } else if (rispostaControlli.getCodErr() != null) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            }
        }

        // leggo i parametri per la memorizzazione su file system, se necessario.
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK
                && recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE
                && recupero.isTpiAbilitato()) {
            //
            if (tpiDefaults == null || tpiDefaults.isEmpty()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecupero.caricaParametri - TPI non abilitato o non configurato correttamente");
                return;
            }
            recupero.setTpiRootTpi(tpiDefaults.get(CostantiDB.ParametroAppl.TPI_ROOT_TPI));
            recupero.setTpiRootRecup(tpiDefaults.get(CostantiDB.ParametroAppl.TPI_ROOT_RECUP));
            //
            recupero.setTpiListaFile(CostantiDB.DatiCablati.TPI_PATH_LISTA_FILE);
            //
            rispostaControlli.reset();
            rispostaControlli = controlliTpi.caricaRootPath();
            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                recupero.setTpiRootTpiDaSacer(rispostaControlli.getrString());
            }

            CSVersatore tmpVersatore = null;
            CSChiave tmpChiave = null;

            // genero i nomi delle cartelle relative alla struttura ed all'UD per il recupero
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                rispostaControlli = controlliRecupero
                        .leggiChiaveUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
                if (rispostaControlli.isrBoolean()) {
                    tmpChiave = (CSChiave) rispostaControlli.getrObject();
                    recupero.setSubPathUnitaDocArk(
                            MessaggiWSFormat.formattaSubPathUnitaDocArk(tmpChiave));
                    recupero.getParametriRecupero()
                            .setDescUnitaDoc(recupero.getSubPathUnitaDocArk());
                } else {
                    setRispostaWsError();
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                            rispostaControlli.getDsErr());
                }
            }

            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                rispostaControlli = controlliRecupero
                        .leggiVersatoreUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
                if (rispostaControlli.isrBoolean()) {
                    tmpVersatore = (CSVersatore) rispostaControlli.getrObject();
                    recupero.setSubPathVersatoreArk(
                            MessaggiWSFormat.formattaSubPathVersatoreArk(tmpVersatore));
                } else {
                    setRispostaWsError();
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                            rispostaControlli.getDsErr());
                }
            }

            // genero il nome del file relativo al log di retrieve del TSM
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                recupero.setFileLogRetrieve(
                        MessaggiWSFormat.formattaFileLogRetrieve(tmpVersatore, tmpChiave));
            }
        }
    }

    public void verificaDate(RecuperoExt recupero) {
        rispostaControlli = controlliRecTpi.caricaDateDocumenti(recupero.getParametriRecupero());
        if (rispostaControlli.isrBoolean()) {
            recupero.getDatiSessioneRecupero()
                    .setDateDocumenti((Collection<Date>) rispostaControlli.getrObject());
        } else if (rispostaControlli.getCodErr() != null) {
            setRispostaWsError();
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        }
    }

    public void verificaPrenotaRecAsync(RecuperoExt recupero) {
        rispostaControlli = controlliRecTpi.verificaPrenotaRecupero(recupero.getParametriRecupero(),
                recupero.getDatiSessioneRecupero());
        /*
         * se rende OK -> recupera da filesystem se rende WARNING -> prenotazione effettuata o
         * recupero in corso, non recupera nulla se rende ERROR -> recupero e prenotazione
         * impossibili
         */

        if (rispostaControlli.isrBoolean()) {
            if (rispostaControlli.getCodErr() != null) {
                setRispostaWsWarning();
                rispostaWs.setEsitoWsWarning(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            }
        } else if (rispostaControlli.getCodErr() != null) {
            setRispostaWsError();
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        }
    }

    public void creaSessRecChiusa(RecuperoExt recupero) {
        rispostaControlli = controlliRecTpi.creaSessRecuperoChiusa(recupero.getParametriRecupero(),
                recupero.getDatiSessioneRecupero());
        if (!rispostaControlli.isrBoolean()) {
            setRispostaWsError();
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        }
    }

    public void chiudiSessRec(RecuperoExt recupero) {
        rispostaControlli = controlliRecTpi.chiudiSessRecupero(recupero);
        if (!rispostaControlli.isrBoolean()) {
            setRispostaWsError();
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        }
    }

    // ****************
    private void setRispostaWsError() {
        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }

    private void setRispostaWsWarning() {
        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.WARNING);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }
}
