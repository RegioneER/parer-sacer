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

package it.eng.parer.web.action;

import it.eng.hsm.beans.HSMUser;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.common.signature.Signature;
import it.eng.parer.entity.SerUrnFileVerSerie;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.ejb.SerieSignatureSessionEjb;
import it.eng.parer.firma.crypto.sign.SignerHsmEjb;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.firma.crypto.sign.SigningResponse;
import it.eng.parer.firma.crypto.verifica.SpringTikaSingleton;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.serie.dto.*;
import it.eng.parer.serie.ejb.ModelliSerieEjb;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.serie.utils.FutureUtils;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.SerieUDAbstractAction;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.parer.slite.gen.form.StrutSerieForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.*;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.TypeValidator;
import it.eng.parer.ws.dto.CSChiaveSerie;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.SuppressLogging;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Bonora_L
 */
public class SerieUDAction extends SerieUDAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(SerieUDAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/SerieEjb")
    private SerieEjb serieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SpringTikaSingleton")
    private SpringTikaSingleton singleton;
    @EJB(mappedName = "java:app/Parer-ejb/TipoSerieEjb")
    private TipoSerieEjb tipoSerieEjb;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ModelliSerieEjb")
    private ModelliSerieEjb modelliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SignerHsmEjb")
    private SignerHsmEjb firmaHsmEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/SerieSignatureSessionEjb")
    private SerieSignatureSessionEjb serieSignSessionEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RecuperoDocumento")
    private RecuperoDocumento recuperoDocumento;

    private static final String ECCEZIONE_RECUPERO_INDICE_AIP = "Errore non gestito nel recupero del file";
    private static final String ERRORE_DOWNLOAD_MESSAGE_INIT = "Errore in download ";
    private static final String ERRORE_DOWNLOAD_MESSAGE = "Errore inatteso nella preparazione del download";

    @Override
    public void initOnClick() throws EMFError {
        // si può cancellare?
    }

    // <editor-fold defaultstate="collapsed" desc="Creazione serie">
    @Secure(action = "Menu.Serie.CreazioneSerie")
    public void loadCreazioneSerie() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.CreazioneSerie");

        getForm().getCreazioneSerie().reset();
        getForm().getCreazioneSerie().setEditMode();

        getForm().getCreazioneSerie().getTi_creazione().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("ti_creazione", CostantiDB.TipoCreazioneSerie.values()));
        DecodeMap tipoSerie = DecodeMap.Factory.newInstance(
                tipoSerieEjb.getDecTipoSerieDaCreareTableBean(getUser().getIdOrganizzazioneFoglia(),
                        CostantiDB.TipoContenSerie.UNITA_DOC.name(), true),
                "id_tipo_serie", getForm().getCreazioneSerie().getNm_tipo_serie().getName());
        getForm().getCreazioneSerie().getNm_tipo_serie().setDecodeMap(tipoSerie);
        getForm().getCreazioneSerie().getConserv_unlimited()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getCreazioneSerie().getFl_fornito_ente()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        getForm().getCreazioneSerie().getDisable_dates().setValue("0");

        forwardToPublisher(Application.Publisher.CREAZIONE_SERIE_UD);
    }

    @Override
    public void process() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.CREAZIONE_SERIE_UD)) {
            creaSerieUD();
        } else if (getLastPublisher()
                .equals(Application.Publisher.ACQUISIZIONE_CONTENUTO_SERIE_UD)) {
            acquisisciContenutoUD();
        }
    }

    @Override
    public void creaSerieUD() throws EMFError {
        int size10Mb = 10 * WebConstants.FILESIZE * WebConstants.FILESIZE;
        try {
            getForm().getCreazioneSerie().postMultipart(getRequest(), size10Mb);
            getForm().getCreazioneSerie().validate(getMessageBox());

            String tipoCreazione = getForm().getCreazioneSerie().getTi_creazione().parse();
            String cdSerie = getForm().getCreazioneSerie().getCd_serie().parse();
            if (!getMessageBox().hasError()) {
                if (!serieEjb.checkCdSerie(cdSerie)) {
                    getMessageBox().addError(
                            "Caratteri consentiti per il codice: lettere, numeri, punto, meno, underscore, due punti");
                }
            }
            if (!getMessageBox().hasError()) {
                // controlli per il tipo acquisizione file
                if (tipoCreazione.equals(CostantiDB.TipoCreazioneSerie.ACQUISIZIONE_FILE.name())) {
                    if (getForm().getCreazioneSerie().getBl_file_input_serie().parse() == null) {
                        getMessageBox().addError("Nessun file selezionato");
                    }
                    if (StringUtils
                            .isBlank(getForm().getCreazioneSerie().getFl_fornito_ente().parse())) {
                        getMessageBox().addError("<br/>Campo "
                                + getForm().getCreazioneSerie().getFl_fornito_ente()
                                        .getDescription()
                                + " obbligatorio per il tipo creazione 'ACQUISIZIONE_FILE'");
                    }
                }
            }

            BigDecimal annoInserito = null;
            if (!getMessageBox().hasError()) {
                int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
                annoInserito = getForm().getCreazioneSerie().getAa_serie().parse();
                if (annoInserito.intValue() > annoCorrente) {
                    getMessageBox().addError(
                            "L'anno per cui creare una serie deve essere pari o inferiore all'anno corrente");
                }
            }

            SerSerieRowBean serieRow = null;
            BigDecimal idTipoSerie = null;
            String tiPeriodoSelSerie = null;
            BigDecimal niPeriodoSelSerie = null;
            if (!getMessageBox().hasError()) {
                idTipoSerie = getForm().getCreazioneSerie().getNm_tipo_serie().parse();
                Date dtInizio = getForm().getCreazioneSerie().getDt_inizio_serie().parse();
                Date dtFine = getForm().getCreazioneSerie().getDt_fine_serie().parse();
                if ((dtInizio == null && dtFine != null) || (dtInizio != null && dtFine == null)) {
                    getMessageBox().addError(
                            "Valorizzare entrambi i valori del range di date con cui selezionare le unit\u00E0 documentarie");
                } else if (dtInizio != null && dtFine != null) {
                    Calendar calInizio = Calendar.getInstance();
                    calInizio.setTime(dtInizio);
                    calInizio.set(Calendar.HOUR_OF_DAY, 0);
                    calInizio.set(Calendar.MINUTE, 0);
                    calInizio.set(Calendar.SECOND, 0);
                    Calendar calFine = Calendar.getInstance();
                    calFine.setTime(dtFine);
                    calFine.set(Calendar.HOUR_OF_DAY, 23);
                    calFine.set(Calendar.MINUTE, 59);
                    calFine.set(Calendar.SECOND, 59);
                    dtInizio = calInizio.getTime();
                    dtFine = calFine.getTime();
                }
                if (!getMessageBox().hasError()) {
                    if (idTipoSerie != null) {
                        DecTipoSerieRowBean row = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
                        String dsListaAnniSelSerie = getForm().getCreazioneSerie()
                                .getDs_lista_anni_sel_serie().parse();
                        serieEjb.checkDecTipoSerieUd(idTipoSerie);
                        if (!getMessageBox().hasError()) {
                            checkFieldsTipoSelUdTipoSerie(row.getTiSelUd(), dtInizio, dtFine,
                                    dsListaAnniSelSerie);
                        }
                        if (!getMessageBox().hasError()) {
                            if (dtInizio == null || dtFine == null) {
                                if (serieEjb.isSerieExisting(getUser().getIdOrganizzazioneFoglia(),
                                        idTipoSerie, annoInserito, null, null)) {
                                    getMessageBox().addError(
                                            "Per la tipologia e l'anno definiti esiste gi\u00E0 una serie");
                                }
                            } else {
                                if (serieEjb.isSerieExisting(getUser().getIdOrganizzazioneFoglia(),
                                        idTipoSerie, annoInserito, dtInizio, dtFine)) {
                                    getMessageBox().addError(
                                            "Per la tipologia, l'anno ed il range di dati definiti esiste gi\u00E0 una serie");
                                }
                                SerieAutomBean creaAutomBean = checkIntervalliEListaTipoSerieDtUdSerie(
                                        row, annoInserito, dtInizio, dtFine, dsListaAnniSelSerie);
                                if (!getMessageBox().hasError() && creaAutomBean != null) {
                                    int index = creaAutomBean.getIntervalli()
                                            .indexOf(new IntervalliSerieAutomBean(dtInizio, dtFine,
                                                    null, null));
                                    tiPeriodoSelSerie = creaAutomBean.getTipoIntervallo();
                                    if (index != -1 && tiPeriodoSelSerie != null) {
                                        niPeriodoSelSerie = new BigDecimal(index + 1);
                                    }
                                }
                            }
                        }

                        if (!getMessageBox().hasError()) {
                            if (annoInserito != null) {
                                if (row.getIdTipoSeriePadre() != null) {
                                    serieRow = serieEjb.getSerSerieRowBean(
                                            row.getIdTipoSeriePadre(),
                                            getUser().getIdOrganizzazioneFoglia(), annoInserito);
                                }
                            }
                        }

                        if (!getMessageBox().hasError()) {
                            String padreDaCreare = getForm().getCreazioneSerie()
                                    .getCd_serie_padre_da_creare().parse();
                            String descPadreDaCreare = getForm().getCreazioneSerie()
                                    .getDs_serie_padre_da_creare().parse();

                            if (row.getIdTipoSeriePadre() != null) {
                                String cdPadre = (serieRow != null ? serieRow.getCdCompositoSerie()
                                        : null);

                                if (StringUtils.isBlank(padreDaCreare)
                                        && StringUtils.isBlank(cdPadre)) {
                                    getMessageBox().addError(
                                            "La serie 'padre' di quella da creare non \u00E8 gi\u00E0 definita e deve, quindi, essere fornito il codice con cui crearla");
                                }

                                if (StringUtils.isNotBlank(padreDaCreare)
                                        && StringUtils.isNotBlank(cdPadre)) {
                                    getMessageBox().addError(
                                            "La serie 'padre' di quella da creare \u00E8 gi\u00E0 definita e non deve, quindi, essere fornito il codice con cui crearla");
                                }

                                if (StringUtils.isNotBlank(padreDaCreare)
                                        && StringUtils.isBlank(descPadreDaCreare)) {
                                    getMessageBox().addError(
                                            "Insieme al codice della serie 'padre' da creare, deve essere anche fornita la descrizione");
                                }

                                if (!getMessageBox().hasError()
                                        && StringUtils.isNotBlank(padreDaCreare)) {
                                    if (!serieEjb.checkCdSerie(padreDaCreare)) {
                                        getMessageBox().addError(
                                                "Caratteri consentiti per il codice della serie 'padre' da creare: lettere, numeri,.,-,_,:");
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!getMessageBox().hasError() && tipoCreazione
                    .equals(CostantiDB.TipoCreazioneSerie.CALCOLO_AUTOMATICO.name())) {
                Date dtRegInizio = getForm().getCreazioneSerie().getDt_reg_unita_doc_da().parse();
                Date dtRegFine = getForm().getCreazioneSerie().getDt_reg_unita_doc_a().parse();
                if ((dtRegInizio == null && dtRegFine != null)
                        || (dtRegInizio != null && dtRegFine == null)) {
                    getMessageBox().addError(
                            "Valorizzare entrambi i valori del range di date delle unit\u00E0 documentarie");
                }

                if (!getMessageBox().hasError() && dtRegInizio != null && dtRegFine != null) {
                    if (dtRegInizio.after(dtRegFine)) {
                        getMessageBox().addError(
                                "L'estremo inferiore del range di date delle unit\u00E0 documentarie non \u00E8 minore o uguale di quello superiore");
                    }
                }
            }

            if (!getMessageBox().hasError() && tipoCreazione
                    .equals(CostantiDB.TipoCreazioneSerie.CALCOLO_AUTOMATICO.name())) {
                Date dtCreazioneInizio = getForm().getCreazioneSerie()
                        .getDt_creazione_unita_doc_da().parse();
                Date dtCreazioneFine = getForm().getCreazioneSerie().getDt_creazione_unita_doc_a()
                        .parse();

                if ((dtCreazioneInizio == null && dtCreazioneFine != null)
                        || (dtCreazioneInizio != null && dtCreazioneFine == null)) {
                    getMessageBox().addError(
                            "Valorizzare entrambi i valori del range di date di versamento delle unit\u00E0 documentarie");
                }
                if (!getMessageBox().hasError() && dtCreazioneInizio != null
                        && dtCreazioneFine != null) {
                    if (dtCreazioneInizio.after(dtCreazioneFine)) {
                        getMessageBox().addError(
                                "L'estremo inferiore del range di date di versamento delle unit\u00E0 documentarie non \u00E8 minore o uguale di quello superiore");
                    }
                }
            }

            if (!getMessageBox().hasError()) {
                String unlimited = getForm().getCreazioneSerie().getConserv_unlimited().parse();
                BigDecimal anniConserv = getForm().getCreazioneSerie().getNi_anni_conserv().parse();
                checkAnniConservazioneIllimitata(unlimited, anniConserv);
            }

            byte[] fileByteArray = null;
            if (!getMessageBox().hasError() && tipoCreazione
                    .equals(CostantiDB.TipoCreazioneSerie.ACQUISIZIONE_FILE.name())) {
                fileByteArray = getForm().getCreazioneSerie().getBl_file_input_serie()
                        .getFileBytes();
                String mime = singleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.CSV_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/csv");
                } else // Carico il file come CSV e verifico che esistano i campi definiti in
                       // decCampoInpUd
                if (!serieEjb.checkCsvHeaders(fileByteArray, idTipoSerie)) {
                    getMessageBox().addError(
                            "Il primo record del file non riporta i nomi dei campi del record");
                }
            }

            if (!getMessageBox().hasError()) {
                CreazioneSerieBean serieBean = new CreazioneSerieBean(
                        getForm().getCreazioneSerie());
                serieBean.setId_serie_padre(serieRow != null ? serieRow.getIdSerie() : null);
                serieBean.setCd_serie_padre(
                        serieRow != null ? serieRow.getCdCompositoSerie() : null);
                serieBean.setDs_serie_padre(serieRow != null ? serieRow.getDsSerie() : null);
                serieBean.setDs_azione("Creazione manuale");
                serieBean.setTi_periodo_sel_serie(tiPeriodoSelSerie);
                serieBean.setNi_periodo_sel_serie(niPeriodoSelSerie);
                // EVO#16486
                // calcola e verifica il codice normalizzato
                String cdSerieNormalized = MessaggiWSFormat.normalizingKey(serieBean.getCd_serie()); // base
                if (serieEjb.existsCdSerieNormalized(getUser().getIdOrganizzazioneFoglia(),
                        serieBean.getAa_serie(), cdSerieNormalized)) {
                    // codice normalizzato già presente su sistema
                    throw new ParerInternalError(
                            /*
                             * "Per la tipologia e l'anno definiti esiste gi\u00E0 una serie con un codice normalizzato già presente su sistema"
                             */
                            "Il controllo univocità del codice normalizzato della serie "
                                    + serieBean.getCd_serie() + " della struttura "
                                    + getUser().getIdOrganizzazioneFoglia()
                                    + " , ha restituito errore");
                } else {
                    // cd serie normalized (se calcolato)
                    serieBean.setCd_serie_normaliz(cdSerieNormalized);
                }
                // end EVO#16486

                Long idVerSerie = serieEjb.saveSerie(getUser().getIdUtente(),
                        getUser().getIdOrganizzazioneFoglia(), serieBean, fileByteArray);
                if (idVerSerie != null) {
                    boolean acquisizione = serieBean.getTi_creazione()
                            .equals(CostantiDB.TipoCreazioneSerie.ACQUISIZIONE_FILE.name());
                    String keyFuture = FutureUtils.buildKeyFuture(serieBean,
                            getUser().getIdOrganizzazioneFoglia(), idVerSerie);
                    Future<String> future = serieEjb.callCreazioneSerieAsync(
                            getUser().getIdUtente(), idVerSerie, acquisizione);
                    FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture,
                            future);
                    getMessageBox().addInfo("Creazione della serie lanciato con successo");
                    getForm().getCreazioneSerie().setEditMode();
                    getForm().getCreazioneSerie().clear();
                } else {
                    log.info("SALVATAGGIO SERIE - Errore in fase di salvataggio dei dati");
                    getMessageBox().addError(
                            "Si \u00E8 verificata un'eccezione nel salvataggio della serie");
                }
            } else {
                getForm().getCreazioneSerie().getCd_serie_padre()
                        .setValue(serieRow != null ? serieRow.getCdCompositoSerie() : null);
                getForm().getCreazioneSerie().getDs_serie_padre()
                        .setValue(serieRow != null ? serieRow.getDsSerie() : null);
            }
        } catch (FileUploadException ex) {
            log.error("Eccezione nell'upload del file", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'upload del file", ex);
        } catch (ParerUserError pue) {
            getMessageBox().addError(pue.getDescription());
        } catch (Exception ex) {
            log.error("Eccezione generica nella creazione della serie: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox()
                    .addError("Si \u00E8 verificata un'eccezione nella creazione della serie");
        }
        forwardToPublisher(Application.Publisher.CREAZIONE_SERIE_UD);
    }

    private void checkFieldsTipoSelUdTipoSerie(String tiSelUd, Date dtInizio, Date dtFine,
            String dsListaAnniSelSerie) {
        if (tiSelUd.equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
            if (dtInizio == null || dtFine == null) {
                getMessageBox()
                        .addError("Il range di date con cui selezionare le unit\u00E0 documentarie "
                                + "che appartengono alla serie da creare, deve essere valorizzato poich\u00E9 "
                                + "la tipologia di serie da creare ha 'Tipo di selezione delle unit\u00E0 documentarie' "
                                + "pari a 'Data delle unit\u00E0 documentarie'");
            } else if (dtInizio.after(dtFine)) {
                getMessageBox().addError(
                        "L'estremo inferiore del range di date di selezione delle unit\u00E0 documentarie non \u00E8 minore o uguale di quello superiore");
            }
            if (StringUtils.isBlank(dsListaAnniSelSerie)) {
                getMessageBox().addError(
                        "Il campo '" + getForm().getCreazioneSerie().getDs_lista_anni_sel_serie()
                                .getDescription() + "' deve essere valorizzato");
            }
        } else {
            if (dtInizio != null || dtFine != null) {
                getMessageBox()
                        .addError("Il range di date con cui selezionare le unit\u00E0 documentarie "
                                + "che appartengono alla serie da creare, non deve essere valorizzato");
            }
            if (StringUtils.isNotBlank(dsListaAnniSelSerie)) {
                getMessageBox()
                        .addError(
                                "Il campo '"
                                        + getForm().getCreazioneSerie().getDs_lista_anni_sel_serie()
                                                .getDescription()
                                        + "' non deve essere valorizzato");
            }
        }
    }

    private SerieAutomBean checkIntervalliEListaTipoSerieDtUdSerie(DecTipoSerieRowBean row,
            BigDecimal aaSerie, Date dtInizio, Date dtFine, String dsListaAnniSelSerie) {
        Calendar calInizio = Calendar.getInstance();
        Calendar calFine = Calendar.getInstance();

        if (!getMessageBox().hasError()) {
            calInizio.setTime(dtInizio);
            calInizio.set(Calendar.HOUR_OF_DAY, 0);
            calInizio.set(Calendar.MINUTE, 0);
            calInizio.set(Calendar.SECOND, 0);
            calFine.setTime(dtFine);
            calFine.set(Calendar.HOUR_OF_DAY, 23);
            calFine.set(Calendar.MINUTE, 59);
            calFine.set(Calendar.SECOND, 59);

            int inizioAnno = calInizio.get(Calendar.YEAR);
            int fineAnno = calFine.get(Calendar.YEAR);

            if (inizioAnno != aaSerie.intValue() || fineAnno != aaSerie.intValue()) {
                getMessageBox().addError(
                        "L'anno del range di date deve coincidere con l'anno della serie");
            }
        }
        SerieAutomBean creaAutomBean = null;
        if (row.getNiMmCreaAutom() != null) {
            if (!getMessageBox().hasError()) {
                try {
                    creaAutomBean = serieEjb.generateIntervalliSerieAutom(row.getNiMmCreaAutom(),
                            row.getCdSerieDefault(), row.getDsSerieDefault(), aaSerie.intValue(),
                            row.getTiSelUd());
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
            if (!getMessageBox().hasError() && creaAutomBean != null) {
                IntervalliSerieAutomBean tmpBean = new IntervalliSerieAutomBean(calInizio.getTime(),
                        calFine.getTime(), null, null);
                if (!creaAutomBean.getIntervalli().contains(tmpBean)) {
                    getMessageBox().addError(
                            "Il range di date non \u00E8 coerente con il numero di mesi per cui creare la serie definito dal tipo di serie");
                }
            }
        }

        if (!getMessageBox().hasError()) {
            if (StringUtils.isNotBlank(dsListaAnniSelSerie)) {
                Matcher matcher = Pattern.compile("^([0-9]{4}(,[0-9]{4})*)?$")
                        .matcher(dsListaAnniSelSerie);
                if (matcher.matches()) {
                    boolean annoSerieIncluso = false;
                    if (dsListaAnniSelSerie.contains(",")) {
                        // Contiene più di un valore
                        String[] valori = StringUtils.split(dsListaAnniSelSerie, ",");
                        for (String valoreStr : valori) {
                            BigDecimal anno = new BigDecimal(valoreStr);
                            if (!annoSerieIncluso && aaSerie.compareTo(anno) == 0) {
                                annoSerieIncluso = true;
                            }
                        }
                    } else // Contiene un solo valore
                    if (aaSerie.compareTo(new BigDecimal(dsListaAnniSelSerie)) == 0) {
                        annoSerieIncluso = true;
                    }
                    if (!annoSerieIncluso) {
                        getMessageBox().addError("Per il campo '"
                                + getForm().getCreazioneSerie().getDs_lista_anni_sel_serie()
                                        .getDescription()
                                + "' deve essere incluso almeno l'anno della serie in corso");
                    }
                } else {
                    getMessageBox().addError("Per il campo '"
                            + getForm().getCreazioneSerie().getDs_lista_anni_sel_serie()
                                    .getDescription()
                            + "' ogni valore deve essere separato mediante \",\" senza spazi");
                }
            }
        }
        return creaAutomBean;
    }

    private void checkAnniConservazioneIllimitata(String unlimited, BigDecimal anniConserv) {
        boolean error = false;
        if (unlimited != null && anniConserv != null) {
            if ((unlimited.equals("1") && !anniConserv.equals(new BigDecimal(9999)))
                    || (unlimited.equals("0") && anniConserv.equals(new BigDecimal(9999)))) {
                error = true;
            }
        }
        if ((anniConserv == null && StringUtils.isBlank(unlimited))
                || (unlimited != null && unlimited.equals("0") && anniConserv == null)) {
            error = true;
        }
        if (error) {
            getMessageBox().addError(
                    "'Anni di conservazione' \u00E8 alternativo a 'Conservazione illimitata'");
        }
    }
    // </editor-fold>

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getSerieList().getName())) {
            loadCreazioneSerie();
        } else if (getTableName().equals(getForm().getUdList().getName())) {
            String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                    .parse();
            BigDecimal idVerSerie = getForm().getContenutoSerieDetail().getId_ver_serie().parse();
            BigDecimal idTipoSerie = getForm().getContenutoSerieDetail().getId_tipo_serie().parse();
            boolean contenEff = false;
            boolean contenCalc = false;
            boolean contenAcq = false;
            if (tipoContenuto != null && idVerSerie != null) {
                if (tipoContenuto.equals(CostantiDB.TipoContenutoVerSerie.CALCOLATO.name())) {
                    contenCalc = true;
                } else if (tipoContenuto
                        .equals(CostantiDB.TipoContenutoVerSerie.ACQUISITO.name())) {
                    contenAcq = true;
                } else if (tipoContenuto
                        .equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())) {
                    contenEff = true;
                }
                try {
                    boolean forwardToUdRicercaAvanzata = false;
                    if (contenCalc || contenAcq) {
                        if (serieEjb.checkVersione(idVerSerie,
                                CostantiDB.StatoVersioneSerie.APERTA.name())
                                && serieEjb.checkContenuto(idVerSerie, contenCalc, contenAcq,
                                        contenEff, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                                .name(),
                                        CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST
                                                .name())) {
                            forwardToUdRicercaAvanzata = true;
                        }
                    } else if (serieEjb.checkVersione(idVerSerie,
                            CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                            CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
                            && serieEjb.checkContenuto(idVerSerie, contenCalc, contenAcq, contenEff,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name(),
                                    CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
                        forwardToUdRicercaAvanzata = true;
                    }
                    if (forwardToUdRicercaAvanzata) {
                        // Ottengo i registri/tipi ud presenti per il tipo serie della serie
                        // visualizzata
                        DecTipoSerieUdTableBean decTipoSerieUdTableBean = serieEjb
                                .getDecTipoSerieUdTableBean(idTipoSerie);
                        List<Object> regSerie = decTipoSerieUdTableBean
                                .toList("id_registro_unita_doc");
                        List<Object> tipiUdSerie = decTipoSerieUdTableBean
                                .toList("id_tipo_unita_doc");

                        // Inizializzo la form per andare alla ricerca ud avanzata inserendo i
                        // valori trovati
                        UnitaDocumentarieForm unitaDocumentarieForm = new UnitaDocumentarieForm();
                        unitaDocumentarieForm.getUnitaDocumentariePerSerie().getId_contenuto_serie()
                                .setValue(getForm().getContenutoSerieDetail()
                                        .getId_contenuto_ver_serie().parse().toPlainString());

                        DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
                                .getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                                        getUser().getIdOrganizzazioneFoglia());
                        DecodeMap mappaTipoUD = DecodeMap.Factory.newInstance(tmpTableBeanTipoUD,
                                "id_tipo_unita_doc", "nm_tipo_unita_doc");
                        unitaDocumentarieForm.getUnitaDocumentariePerSerie().getNm_tipo_unita_doc()
                                .setDecodeMap(mappaTipoUD);
                        unitaDocumentarieForm.getUnitaDocumentariePerSerie().getNm_tipo_unita_doc()
                                .setValues(getStringValues(tipiUdSerie));

                        // Setto i valori della combo TIPO REGISTRO ricavati dalla tabella
                        // DEC_REGISTRO_UNITA_DOC
                        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
                                .getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                                        getUser().getIdOrganizzazioneFoglia());
                        DecodeMap mappaRegistro = DecodeMap.Factory.newInstance(tmpTableBeanReg,
                                "id_registro_unita_doc", "cd_registro_unita_doc");
                        unitaDocumentarieForm.getUnitaDocumentariePerSerie()
                                .getCd_registro_key_unita_doc().setDecodeMap(mappaRegistro);
                        unitaDocumentarieForm.getUnitaDocumentariePerSerie()
                                .getCd_registro_key_unita_doc()
                                .setValues(getStringValues(regSerie));

                        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                                "?operation=unitaDocumentarieRicercaAvanzata",
                                unitaDocumentarieForm);
                    }
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                    forwardToPublisher(getLastPublisher());
                }
            }
        } else if (getTableName().equals(getForm().getLacuneList().getName())) {
            getForm().getLacunaDetail().clear();
            getForm().getLacunaDetail().setEditMode();

            int sizeLacune = getForm().getLacuneList().getTable().size();
            BigDecimal idConsistVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail()
                    .getId_consist_ver_serie().parse();
            BigDecimal lastPgLacuna = serieEjb.getMaxPgLacuna(idConsistVerSerie);
            String nextPg = String.valueOf(sizeLacune + 1);

            if (lastPgLacuna.intValue() > sizeLacune) {
                // Buco di numerazione
                SerLacunaConsistVerSerieTableBean tb = (SerLacunaConsistVerSerieTableBean) getForm()
                        .getLacuneList().getTable();
                List<Object> progressivi = tb.toList(
                        SerLacunaConsistVerSerieRowBean.TABLE_DESCRIPTOR.COL_PG_LACUNA,
                        new SortingRule[] {
                                SortingRule.getAscending(
                                        SerLacunaConsistVerSerieRowBean.TABLE_DESCRIPTOR.COL_PG_LACUNA) });
                int index = 1;
                for (Object progressivo : progressivi) {
                    int pg = ((BigDecimal) progressivo).intValue();
                    if (index != pg) {
                        nextPg = String.valueOf(index);
                        break;
                    }
                    index++;
                }
            }

            getForm().getLacunaDetail().getPg_lacuna().setValue(nextPg);
            getForm().getLacunaDetail().getTi_lacuna().setDecodeMap(ComboGetter
                    .getMappaSortedGenericEnum("ti_lacuna", CostantiDB.TipoLacuna.values()));
            getForm().getLacunaDetail().getTi_mod_lacuna().setDecodeMap(ComboGetter
                    .getMappaSortedGenericEnum("ti_mod_lacuna", CostantiDB.TipoModLacuna.values()));

            getForm().getLacuneList().setStatus(Status.insert);
            getForm().getLacunaDetail().setStatus(Status.insert);

            forwardToPublisher(Application.Publisher.LACUNA_DETAIL);
        } else if (getTableName().equals(getForm().getNoteList().getName())) {
            BigDecimal idVersione = getForm().getSerieDetail().getId_ver_serie().parse();
            if (serieEjb.checkVersione(idVersione, CostantiDB.StatoVersioneSerie.APERTA.name(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {

                BaseRow tmpRow = new BaseRow();
                getForm().getSerieDetail().copyToBean(tmpRow);
                getForm().getDatiSerieDetail().copyFromBean(tmpRow);

                DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_HOUR_MINUTE_TYPE);

                getForm().getNotaDetail().clear();
                getForm().getNotaDetail().setViewMode();

                getForm().getNotaDetail().getPg_nota_ver_serie().setValue(null);
                getForm().getNotaDetail().getNm_userid().setValue(getUser().getUsername());
                getForm().getNotaDetail().getDt_nota_ver_serie()
                        .setValue(formato.format(Calendar.getInstance().getTime()));
                getForm().getNotaDetail().getId_tipo_nota_serie()
                        .setDecodeMap(DecodeMap.Factory.newInstance(
                                tipoSerieEjb.getDecTipoNotaSerieNotInVerSerieTableBean(idVersione),
                                "id_tipo_nota_serie", "ds_tipo_nota_serie"));
                getForm().getNotaDetail().getId_tipo_nota_serie().setEditMode();
                getForm().getNotaDetail().getDs_nota_ver_serie().setEditMode();

                getForm().getNoteList().setStatus(Status.insert);
                getForm().getNotaDetail().setStatus(Status.insert);

                forwardToPublisher(Application.Publisher.NOTA_DETAIL);
            } else {
                getMessageBox().addError(
                        "La versione della serie non \u00E8 quella corrente o lo stato della versione \u00E8 diverso da APERTA, DA_CONTROLLARE, CONTROLLATA, DA_VALIDARE");
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    private String[] getStringValues(List<Object> bigDecimalValues) {
        String[] values = new String[bigDecimalValues.size()];
        for (int index = 0; index < bigDecimalValues.size(); index++) {
            BigDecimal value = (BigDecimal) bigDecimalValues.get(index);
            values[index] = value.toPlainString();
        }
        return values;
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getSerieList().getName())) {
                loadCurrentDettaglioSerieFromList(getForm().getSerieList().getName());
            } else if (getTableName().equals(getForm().getSerieDaFirmareList().getName())) {
                loadCurrentDettaglioSerieFromList(getForm().getSerieDaFirmareList().getName());
            } else if (getTableName().equals(getForm().getSerieDaValidareList().getName())) {
                loadCurrentDettaglioSerieFromList(getForm().getSerieDaValidareList().getName());
            } else if (getTableName().equals(getForm().getVersioniPrecedentiList().getName())
                    || getTableName()
                            .equals(getForm().getVersioniPrecedentiDetailList().getName())) {
                log.info("Caricamento dettaglio di una serie PRECEDENTE");
                getSession().setAttribute("navTableSerie",
                        getForm().getVersioniPrecedentiList().getName());
                SerVLisVerSeriePrecRowBean row;
                if (getTableName().equals(getForm().getVersioniPrecedentiList().getName())) {
                    getForm().getVersioniPrecedentiDetailList()
                            .setTable(getForm().getVersioniPrecedentiList().getTable());
                    row = (SerVLisVerSeriePrecRowBean) getForm().getVersioniPrecedentiList()
                            .getTable().getCurrentRow();
                } else {
                    row = (SerVLisVerSeriePrecRowBean) getForm().getVersioniPrecedentiDetailList()
                            .getTable().getCurrentRow();
                }
                // Il livello di annidamento cambia in base a quello del dettaglio visualizzato in
                // precedenza
                BigDecimal level = getForm().getSerieDetail().getLevel_ver_serie().parse();
                loadDettaglioSerie(row.getIdVerSerie(), false);
                getForm().getSerieDetail().getId_ver_serie_corr()
                        .setValue(row.getIdVerSerieInput().toPlainString());

                VerSerieDetailBean lastDetailStack = getLastIdVerSerieDetailStack();

                if (getNavigationEvent().equals(ListAction.NE_NEXT)
                        || getNavigationEvent().equals(ListAction.NE_PREV)) {

                    if (lastDetailStack != null && level.intValue() == lastDetailStack.getLevel()) {
                        popIdVerSerieDetailStack();
                        getForm().getSerieDetail().getLevel_ver_serie()
                                .setValue(String.valueOf(level));
                    } else if (lastDetailStack != null) {
                        int nextLevel = lastDetailStack.getLevel() + 1;
                        getForm().getSerieDetail().getLevel_ver_serie()
                                .setValue(String.valueOf(nextLevel));
                    } else {
                        // fallback se non c'è uno stack precedente
                        getForm().getSerieDetail().getLevel_ver_serie().setValue("1"); // o un altro
                        // valore
                        // sensato
                        log.warn("Nessun VerSerieDetailStack trovato nella sessione.");
                    }

                } else {
                    if (lastDetailStack != null) {
                        int nextLevel = lastDetailStack.getLevel() + 1;
                        getForm().getSerieDetail().getLevel_ver_serie()
                                .setValue(String.valueOf(nextLevel));
                    } else {
                        getForm().getSerieDetail().getLevel_ver_serie().setValue("1");
                        log.warn("Nessun VerSerieDetailStack trovato nella sessione.");
                    }
                }
                pushIdVerSerieDetailStack(row.getIdVerSerie(),
                        getForm().getVersioniPrecedentiList().getName(),
                        getForm().getVersioniPrecedentiDetailList().getTable(),
                        getForm().getSerieDetail().getLevel_ver_serie().parse().intValue());
            } else if (getTableName().equals(getForm().getErroriContenutiList().getName())) {
                SerVLisErrContenSerieUdRowBean currentRow = (SerVLisErrContenSerieUdRowBean) getForm()
                        .getErroriContenutiList().getTable().getCurrentRow();
                BigDecimal idErrContenutoVerSerie = currentRow.getIdErrContenutoVerSerie();

                SerVVisErrContenSerieUdRowBean detailRow = serieEjb
                        .getSerVVisErrContenSerieUdRowBean(idErrContenutoVerSerie);
                getForm().getErroreContenutoDetail().copyFromBean(detailRow);

                SerVLisUdNoversTableBean listaUd = serieEjb
                        .getSerVLisUdNoversTableBean(idErrContenutoVerSerie);
                getForm().getErroreContenutoUdList().setTable(listaUd);
                getForm().getErroreContenutoUdList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getErroreContenutoUdList().getTable().first();

                getForm().getErroreContenutoDetail().getFl_job_bloccato()
                        .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

                String[] blocco = serieEjb.getFlContenutoBloccato(
                        JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
                        detailRow.getIdContenutoVerSerie());
                if (blocco != null) {
                    getForm().getErroreContenutoDetail().getFl_job_bloccato().setValue(blocco[0]);
                }
            } else if (getTableName().equals(getForm().getErroriFileInputList().getName())) {
                SerVLisErrFileSerieUdRowBean currentRow = (SerVLisErrFileSerieUdRowBean) getForm()
                        .getErroriFileInputList().getTable().getCurrentRow();
                BigDecimal idErrFileInput = currentRow.getIdErrFileInput();

                SerVVisErrFileSerieUdRowBean detailRow = serieEjb
                        .getSerVVisErrFileSerieUdRowBean(idErrFileInput);
                getForm().getErroriFileInputDetail().copyFromBean(detailRow);

                SerVLisUdErrFileInputTableBean listaUd = serieEjb
                        .getSerVLisUdErrFileInputTableBean(idErrFileInput);
                getForm().getErroriFileInputUdList().setTable(listaUd);
                getForm().getErroriFileInputUdList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getErroriFileInputUdList().getTable().first();

                getForm().getErroriFileInputDetail().getFl_job_bloccato()
                        .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
                getForm().getErroriFileInputDetail().getFl_err_contenuto()
                        .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

                String[] blocco = serieEjb.getFlContenutoBloccato(
                        JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
                        detailRow.getIdContenutoVerSerie());
                if (blocco != null) {
                    getForm().getErroriFileInputDetail().getFl_job_bloccato().setValue(blocco[0]);
                }
            } else if (getTableName().equals(getForm().getNoteList().getName())) {
                SerVLisNotaSerieRowBean currentRow = (SerVLisNotaSerieRowBean) getForm()
                        .getNoteList().getTable().getCurrentRow();
                BaseRow tmpRow = new BaseRow();
                getForm().getSerieDetail().copyToBean(tmpRow);
                getForm().getDatiSerieDetail().copyFromBean(tmpRow);

                getForm().getNotaDetail().getId_tipo_nota_serie()
                        .setDecodeMap(DecodeMap.Factory.newInstance(
                                tipoSerieEjb.getSingleDecTipoNotaSerieTableBean(
                                        currentRow.getIdTipoNotaSerie()),
                                "id_tipo_nota_serie", "ds_tipo_nota_serie"));
                getForm().getNotaDetail().setViewMode();

                getForm().getNotaDetail().copyFromBean(currentRow);

                getForm().getNoteList().setStatus(Status.view);
                getForm().getNotaDetail().setStatus(Status.view);
            } else if (getTableName().equals(getForm().getStatiList().getName())) {
                BaseRow tmpRow = new BaseRow();
                getForm().getSerieDetail().copyToBean(tmpRow);
                getForm().getDatiSerieDetail().copyFromBean(tmpRow);

                SerVLisStatoSerieRowBean currentRow = (SerVLisStatoSerieRowBean) getForm()
                        .getStatiList().getTable().getCurrentRow();
                getForm().getStatoDetail().copyFromBean(currentRow);

                getForm().getStatoDetail().setViewMode();
                getForm().getStatiList().setStatus(Status.view);
            } else if (getTableName().equals(getForm().getLacuneList().getName())) {
                getForm().getLacunaDetail().getTi_lacuna().setDecodeMap(ComboGetter
                        .getMappaSortedGenericEnum("ti_lacuna", CostantiDB.TipoLacuna.values()));
                getForm().getLacunaDetail().getTi_mod_lacuna()
                        .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_mod_lacuna",
                                CostantiDB.TipoModLacuna.values()));
                SerLacunaConsistVerSerieRowBean row = (SerLacunaConsistVerSerieRowBean) getForm()
                        .getLacuneList().getTable().getCurrentRow();
                getForm().getLacunaDetail().copyFromBean(row);
            } else if (getTableName().equals(getForm().getVolumiList().getName())) {
                SerVLisVolSerieUdRowBean row = (SerVLisVolSerieUdRowBean) getForm().getVolumiList()
                        .getTable().getCurrentRow();
                SerVVisVolVerSerieUdRowBean detailRow = serieEjb
                        .getSerVVisVolSerieUdRowBean(row.getIdVolVerSerie());
                getForm().getVolumeDetail().copyFromBean(detailRow);
                getForm().getVolumeDetail().getDownloadIxVol().setEditMode();
                getForm().getVolumeDetail().getDownloadIxVol().setDisableHourGlass(true);

                getForm().getFiltriContenutoSerieDetail().clear();
                getForm().getFiltriContenutoSerieDetail().setEditMode();

                log.info("Carico le liste contenute nei vari tab di dettaglio");
                RicercaUdAppartBean parametri = initParametriRicercaContenuto(
                        detailRow.getAaSerie());
                SerVLisUdAppartVolSerieTableBean listaUd = serieEjb
                        .getSerVLisUdAppartVolSerieTableBean(row.getIdVolVerSerie(), parametri);
                if (getForm().getUdVolumeList().getTable() != null) {
                    // Ricarico le liste con paginazione
                    int paginaCorrenteUd = getForm().getUdVolumeList().getTable()
                            .getCurrentPageIndex();
                    int inizioUd = getForm().getUdVolumeList().getTable().getFirstRowPageIndex();
                    int pageSizeUd = getForm().getUdVolumeList().getTable().getPageSize();

                    getForm().addComponent(new SerieUDForm.UdVolumeList());
                    getForm().getUdVolumeList().setTable(listaUd);
                    getForm().getUdVolumeList().getTable().first();
                    getForm().getUdVolumeList().getTable().setPageSize(pageSizeUd);
                    this.lazyLoadGoPage(getForm().getUdVolumeList(), paginaCorrenteUd);
                    // Ritorno alla pagina
                    getForm().getUdVolumeList().getTable().setCurrentRowIndex(inizioUd);
                } else {
                    getForm().addComponent(new SerieUDForm.UdVolumeList());

                    getForm().getUdVolumeList().setTable(listaUd);
                    getForm().getUdVolumeList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getUdVolumeList().getTable().first();
                }
            } else if (getTableName().equals(getForm().getConsistenzaSerieList().getName())) {
                SerVRicConsistSerieUdRowBean currentRow = (SerVRicConsistSerieUdRowBean) getForm()
                        .getConsistenzaSerieList().getTable().getCurrentRow();
                getForm().getComunicazioneConsistenzaDetail().getShow_edit()
                        .setValue(String.valueOf(false));
                getForm().getComunicazioneConsistenzaDetail().getShow_delete()
                        .setValue(String.valueOf(false));
                getForm().getComunicazioneConsistenzaDetail().setViewMode();
                getForm().getComunicazioneConsistenzaDetail().setStatus(Status.view);
                getForm().getConsistenzaSerieList().setStatus(Status.view);
                if (currentRow != null) {
                    DecodeMap registri = DecodeMap.Factory.newInstance(
                            tipoSerieEjb.getDecRegistroUnitaDocTableBeanFromTipoSerie(
                                    getUser().getIdOrganizzazioneFoglia(),
                                    currentRow.getIdTipoSerie()),
                            "cd_registro_unita_doc", "cd_registro_unita_doc");
                    getForm().getComunicazioneConsistenzaDetail().getCd_registro_first()
                            .setDecodeMap(registri);
                    getForm().getComunicazioneConsistenzaDetail().getCd_registro_last()
                            .setDecodeMap(registri);

                    if (currentRow.getFlPresenzaConsistAttesa().equals("1")) {
                        // Mostro la consistenza attesa in visualizzazione
                        log.info("Visualizzo la consistenza in modalit\u00E0 visualizzazione");
                        SerVVisConsistSerieUdRowBean row = serieEjb
                                .getSerVVisConsistSerieUdRowBean(currentRow.getIdConsistVerSerie());
                        getForm().getComunicazioneConsistenzaDetail().copyFromBean(row);

                        if (serieEjb.checkVersione(currentRow.getIdVerSerie(),
                                CostantiDB.StatoVersioneSerie.APERTA.name(),
                                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                                CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                                CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                                && (serieEjb.checkContenuto(currentRow.getIdVerSerie(), true, true,
                                        true, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                        CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST
                                                .name(),
                                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                                .name()))) {
                            getForm().getComunicazioneConsistenzaDetail().getShow_edit()
                                    .setValue(String.valueOf(true));
                            getForm().getComunicazioneConsistenzaDetail().getShow_delete()
                                    .setValue(String.valueOf(true));
                        }
                    } else if (serieEjb.checkVersione(currentRow.getIdVerSerie(),
                            CostantiDB.StatoVersioneSerie.APERTA.name(),
                            CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                            CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                            CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                            && (serieEjb.checkContenuto(currentRow.getIdVerSerie(), true, true,
                                    true, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name()))) {
                        // Mostro la consistenza attesa in inserimento
                        getForm().getComunicazioneConsistenzaDetail().copyFromBean(currentRow);
                        getForm().getComunicazioneConsistenzaDetail().getNm_userid_consist()
                                .setValue(getUser().getUsername());
                        getForm().getComunicazioneConsistenzaDetail()
                                .getDt_comunic_consist_ver_serie().setValue(ActionUtils
                                        .getStringDateTime(Calendar.getInstance().getTime()));

                        getForm().getComunicazioneConsistenzaDetail().setEditMode();

                        getForm().getComunicazioneConsistenzaDetail().getShow_edit()
                                .setValue(String.valueOf(true));
                        getForm().getComunicazioneConsistenzaDetail().getShow_delete()
                                .setValue(String.valueOf(true));
                        getForm().getComunicazioneConsistenzaDetail().setStatus(Status.insert);
                        getForm().getConsistenzaSerieList().setStatus(Status.insert);
                    } else {
                        getMessageBox().addError(
                                "Lo stato della serie \u00E8 diverso da APERTA, DA_CONTROLLARE, CONTROLLATA o DA_VALIDARE");
                    }
                }
            }
        }
    }

    private void initSerieDetail() {
        getForm().getSerieDetail().getConserv_unlimited()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_presenza_lacune()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_err_contenuto_calc()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_calc_bloccato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_err_contenuto_file()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_err_contenuto_acq()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_fornito_ente()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_acq_bloccato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_val_bloccato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_err_contenuto_eff()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getSerieDetail().getFl_eff_bloccato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    private void loadCurrentDettaglioSerieFromList(String listName) throws EMFError {
        log.info("Caricamento dettaglio di una serie CORRENTE");
        getSession().setAttribute("navTableSerie", listName);
        it.eng.spagoLite.form.list.List formList = ((it.eng.spagoLite.form.list.List) getForm()
                .getComponent(listName));
        BaseRowInterface listRow = formList.getTable().getCurrentRow();
        initSerieDetail();
        // Siamo per forza al primo livello di annidamento
        BigDecimal level = getForm().getSerieDetail().getLevel_ver_serie().parse() != null
                ? getForm().getSerieDetail().getLevel_ver_serie().parse()
                : BigDecimal.ONE;
        // Attualmente \u00E8 per forza la versione corrente
        BigDecimal idVerSerie = listRow.getBigDecimal("id_ver_serie");
        loadDettaglioSerie(listRow.getBigDecimal("id_ver_serie"), true);
        getForm().getSerieDetail().getId_ver_serie_corr().setValue(idVerSerie.toPlainString());
        getForm().getSerieDetail().getLevel_ver_serie().setValue(BigDecimal.ONE.toPlainString());
        VerSerieDetailBean lastStackItem = getLastIdVerSerieDetailStack();
        if ((getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) && lastStackItem != null
                && level.intValue() == lastStackItem.getLevel()) {
            // Se mi sono spostato nella collezione eseguo una pop dallo stack per gli elementi
            // dello stesso livello
            popIdVerSerieDetailStack();
        }
        pushIdVerSerieDetailStack(listRow.getBigDecimal("id_ver_serie"), formList.getName(),
                formList.getTable(), 1);
    }

    private void loadDettaglioSerie(BigDecimal idVerSerie, boolean versioneCorrente)
            throws EMFError {
        SerVVisSerieUdRowBean detailRow = serieEjb.getSerVVisSerieUdRowBean(idVerSerie);
        getForm().getSerieDetail().copyFromBean(detailRow);

        CSVersatore tmpVers = new CSVersatore();
        tmpVers.setAmbiente(detailRow.getNmAmbiente());
        tmpVers.setEnte(detailRow.getNmEnte());
        tmpVers.setStruttura(detailRow.getNmStrut());

        CSChiaveSerie chiaveSerie = new CSChiaveSerie();
        chiaveSerie.setAnno(detailRow.getAaSerie().intValue());
        chiaveSerie.setNumero(detailRow.getCdCompositoSerie());

        getForm().getSerieDetail().getUrn_serie()
                .setValue(MessaggiWSFormat.formattaUrnDocUniDoc(MessaggiWSFormat
                        .formattaBaseUrnSerie(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                                MessaggiWSFormat.formattaUrnPartSerie(chiaveSerie))));

        if (detailRow.getNiAnniConserv().equals(new BigDecimal(9999))) {
            getForm().getSerieDetail().getConserv_unlimited().setValue("1");
        } else {
            getForm().getSerieDetail().getConserv_unlimited().setValue("0");
        }
        if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoCalc())
                && detailRow.getTiStatoContenutoCalc()
                        .equals(CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
            getForm().getSerieDetail().getFl_calc_bloccato().setHidden(false);
            getForm().getSerieDetail().getDl_msg_job_bloccato_calc().setHidden(false);
            String[] blocco = serieEjb.getFlContenutoBloccato(
                    JobConstants.JobEnum.CALCOLO_SERIE.name(), detailRow.getIdContenutoCalc());
            if (blocco != null) {
                getForm().getSerieDetail().getFl_calc_bloccato().setValue(blocco[0]);
                getForm().getSerieDetail().getDl_msg_job_bloccato_calc().setValue(blocco[1]);
            }
        } else {
            getForm().getSerieDetail().getFl_calc_bloccato().setHidden(true);
            getForm().getSerieDetail().getDl_msg_job_bloccato_calc().setHidden(true);
        }
        if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoAcq())
                && detailRow.getTiStatoContenutoAcq()
                        .equals(CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
            getForm().getSerieDetail().getFl_acq_bloccato().setHidden(false);
            getForm().getSerieDetail().getDl_msg_job_bloccato_acq().setHidden(false);
            String[] blocco = serieEjb.getFlContenutoBloccato(
                    JobConstants.JobEnum.INPUT_SERIE.name(), detailRow.getIdContenutoAcq());
            if (blocco != null) {
                getForm().getSerieDetail().getFl_acq_bloccato().setValue(blocco[0]);
                getForm().getSerieDetail().getDl_msg_job_bloccato_acq().setValue(blocco[1]);
            }
        } else {
            getForm().getSerieDetail().getFl_acq_bloccato().setHidden(true);
            getForm().getSerieDetail().getDl_msg_job_bloccato_acq().setHidden(true);
        }
        if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoEff())
                && detailRow.getTiStatoContenutoEff()
                        .equals(CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
            getForm().getSerieDetail().getFl_eff_bloccato().setHidden(false);
            getForm().getSerieDetail().getDl_msg_job_bloccato_eff().setHidden(false);
            String[] blocco = serieEjb.getFlContenutoBloccato(
                    JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
                    detailRow.getIdContenutoEff());
            if (blocco != null) {
                getForm().getSerieDetail().getFl_eff_bloccato().setValue(blocco[0]);
                getForm().getSerieDetail().getDl_msg_job_bloccato_eff().setValue(blocco[1]);
            }
        } else {
            getForm().getSerieDetail().getFl_eff_bloccato().setHidden(true);
            getForm().getSerieDetail().getDl_msg_job_bloccato_eff().setHidden(true);
        }

        // Hash "personalizzato"
        if (StringUtils.isNotBlank(detailRow.getDsHashIxAip())) {
            String descrizione = "Hash " + detailRow.getDsAlgoHashIxAip() + " ("
                    + detailRow.getCdEncodingHashIxAip() + ")";
            String valore = detailRow.getDsHashIxAip();
            getForm().getSerieDetail().getHash_personalizzato().setHidden(false);
            getForm().getSerieDetail().getHash_personalizzato().setDescription(descrizione);
            getForm().getSerieDetail().getHash_personalizzato().setValue(valore);
        } else {
            getForm().getSerieDetail().getHash_personalizzato().setHidden(true);
        }

        if (detailRow.getIdEnteConserv() != null) {
            getForm().getSerieDetail().getNm_ente_conserv().setValue(udHelper
                    .findById(SIOrgEnteSiam.class, detailRow.getIdEnteConserv()).getNmEnteSiam());
        }

        log.info("Carico le liste contenute nei vari tab di dettaglio");
        getForm().getSerieDetailTabs()
                .setCurrentTab(getForm().getSerieDetailTabs().getInfoPrincipali());
        getForm().getSerieDetailSubTabs()
                .setCurrentTab(getForm().getSerieDetailSubTabs().getListaNote());

        SerVLisNotaSerieTableBean noteTb = serieEjb.getSerVLisNotaSerieTableBean(idVerSerie);
        getForm().getNoteList().setTable(noteTb);
        getForm().getNoteList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getNoteList().getTable().first();
        getForm().getNoteList().setHideUpdateButton(true);
        getForm().getNoteList().setHideDeleteButton(true);
        getForm().getNoteList().setHideInsertButton(true);
        getForm().getNoteList().setHideDetailButton(true);

        SerVLisStatoSerieTableBean statiTb = serieEjb.getSerVLisStatoSerieTableBean(idVerSerie);
        getForm().getStatiList().setTable(statiTb);
        getForm().getStatiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getStatiList().getTable().first();

        SerVLisVolSerieUdTableBean volTb = serieEjb.getSerVLisVolSerieUdTableBean(idVerSerie);
        getForm().getVolumiList().setTable(volTb);
        getForm().getVolumiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVolumiList().getTable().first();

        SerVLisVerSeriePrecTableBean verPrecTb = serieEjb
                .getSerVLisVerSeriePrecTableBean(idVerSerie);
        getForm().getVersioniPrecedentiList().setTable(verPrecTb);
        getForm().getVersioniPrecedentiList().getTable()
                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVersioniPrecedentiList().getTable().first();

        log.info("Controlli sulla versione per la visualizzazione dei bottoni");
        getForm().getSerieList().setHideUpdateButton(true);
        getForm().getSerieList().setHideDeleteButton(true);

        getForm().getSerieDetailButtonList().setEditMode();
        getForm().getSerieDetailButtonList().hideAll();

        if (versioneCorrente) {
            if (detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.APERTA.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
                getForm().getSerieList().setHideUpdateButton(false);
            }
            if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name())
                    && (detailRow.getIdContenutoCalc() == null
                            || (serieEjb.checkContenuto(idVerSerie, true, false, false,
                                    CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())))
                    && (detailRow.getIdContenutoAcq() == null || (serieEjb.checkContenuto(
                            idVerSerie, false, true, false,
                            CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                            CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                            CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())))
                    && detailRow.getIdContenutoEff() == null) {
                getForm().getSerieList().setHideDeleteButton(false);
            } else if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoCalc())) {
                if (detailRow.getTiStatoContenutoCalc()
                        .equals(CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
                    String[] blocco = serieEjb.getFlContenutoBloccato(
                            JobConstants.JobEnum.CALCOLO_SERIE.name(),
                            detailRow.getIdContenutoCalc());
                    if (blocco != null && blocco[0].equals("1")) {
                        getForm().getSerieList().setHideDeleteButton(false);
                    }
                } else if (detailRow.getTiStatoContenutoCalc().equals(
                        CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO.name())) {
                    String[] blocco = serieEjb.getFlContenutoBloccato(
                            JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
                            detailRow.getIdContenutoCalc());
                    if (blocco != null && blocco[0].equals("1")) {
                        getForm().getSerieList().setHideDeleteButton(false);
                    }
                }
            } else if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoAcq())) {
                if (detailRow.getTiStatoContenutoAcq()
                        .equals(CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
                    String[] blocco = serieEjb.getFlContenutoBloccato(
                            JobConstants.JobEnum.INPUT_SERIE.name(), detailRow.getIdContenutoAcq());
                    if (blocco != null && blocco[0].equals("1")) {
                        getForm().getSerieList().setHideDeleteButton(false);
                    }
                } else if (detailRow.getTiStatoContenutoAcq().equals(
                        CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO.name())) {
                    String[] blocco = serieEjb.getFlContenutoBloccato(
                            JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
                            detailRow.getIdContenutoAcq());
                    if (blocco != null && blocco[0].equals("1")) {
                        getForm().getSerieList().setHideDeleteButton(false);
                    }
                }
            }

            if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
                    && (detailRow.getIdContenutoEff() == null || (serieEjb.checkContenuto(
                            idVerSerie, false, false, true,
                            CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                            CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())))) {
                getForm().getSerieList().setHideDeleteButton(false);
            } else if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoEff())) {
                if (detailRow.getTiStatoContenutoEff()
                        .equals(CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
                    String[] blocco = serieEjb.getFlContenutoBloccato(
                            JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
                            detailRow.getIdContenutoEff());
                    if (blocco != null && blocco[0].equals("1")) {
                        getForm().getSerieList().setHideDeleteButton(false);
                    }
                } else if (detailRow.getTiStatoContenutoEff().equals(
                        CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO.name())) {
                    String[] blocco = serieEjb.getFlContenutoBloccato(
                            JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
                            detailRow.getIdContenutoEff());
                    if (blocco != null && blocco[0].equals("1")) {
                        getForm().getSerieList().setHideDeleteButton(false);
                    }
                }
            }

            if (detailRow.getTiStatoVerSerie()
                    .equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
                getForm().getSerieDetailButtonList().getCambiaStatoAperta().setHidden(false);
            }
            if (detailRow.getTiStatoVerSerie()
                    .equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())) {
                getForm().getSerieDetailButtonList().getCambiaStatoDaValidare().setHidden(false);
            }
            if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name())) {
                if (detailRow.getIdContenutoCalc() == null
                        || (serieEjb.checkContenuto(idVerSerie, true, false, false,
                                CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                                CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())
                                && detailRow.getIdContenutoEff() == null)) {
                    getForm().getSerieDetailButtonList().getCalcolaContenuto().setHidden(false);
                }

                if (detailRow.getIdContenutoAcq() == null
                        || (serieEjb.checkContenuto(idVerSerie, false, true, false,
                                CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                                CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())
                                && detailRow.getIdContenutoEff() == null)) {
                    getForm().getSerieDetailButtonList().getAcquisisciContenuto().setHidden(false);
                }

                if (((detailRow.getIdContenutoCalc() != null
                        && serieEjb.checkContenuto(idVerSerie, true, false, false,
                                CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                                CostantiDB.StatoContenutoVerSerie.CREATO.name()))
                        || (detailRow.getIdContenutoAcq() != null
                                && serieEjb.checkContenuto(idVerSerie, false, true, false,
                                        CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST
                                                .name(),
                                        CostantiDB.StatoContenutoVerSerie.CREATO.name())))) {
                    if (detailRow.getIdContenutoEff() == null
                            || !serieEjb.checkContenuto(idVerSerie, false, false, true,
                                    CostantiDB.StatoContenutoVerSerie.CREAZIONE_IN_CORSO.name())) {
                        getForm().getSerieDetailButtonList().getGeneraContenutoEffettivo()
                                .setHidden(false);
                    }
                }
            }
            if (detailRow.getTiStatoVerSerie()
                    .equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())) {
                getForm().getSerieDetail().getFl_val_bloccato().setHidden(false);
                getForm().getSerieDetail().getDl_msg_job_bloccato_val().setHidden(false);
                String[] blocco = serieEjb.getFlVersioneBloccata(
                        JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(),
                        detailRow.getIdVerSerie());
                if (blocco != null) {
                    getForm().getSerieDetail().getFl_val_bloccato().setValue(blocco[0]);
                    getForm().getSerieDetail().getDl_msg_job_bloccato_val().setValue(blocco[1]);
                }
            } else {
                getForm().getSerieDetail().getFl_val_bloccato().setHidden(true);
                getForm().getSerieDetail().getDl_msg_job_bloccato_val().setHidden(true);
            }

            if (detailRow.getTiStatoVerSerie()
                    .equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.VALIDATA.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.DA_FIRMARE.name())) {
                getForm().getSerieDetailButtonList().getCambiaStatoDaControllare().setHidden(false);
            }

            if (!serieEjb.checkSerieDaRigenerare(detailRow.getIdVerSerie())
                    && (detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                            || detailRow.getTiStatoVerSerie()
                                    .equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name()))
                    && serieEjb.checkExistNoteObblig(detailRow.getIdVerSerie())
                    && serieEjb.checkStatoConservazioneUdInContenEff(detailRow.getIdVerSerie())) {
                // se per il contenuto di tipo EFFETTIVO non sono presenti errori con origine =
                // VALIDAZIONE
                if (!serieEjb.checkErroriContenutoEffettivo(detailRow.getIdContenutoEff(), null,
                        null, CostantiDB.TipoOrigineErroreContenuto.VALIDAZIONE.name())) {
                    getForm().getSerieDetailButtonList().getCambiaStatoValidazioneInCorso()
                            .setHidden(false);
                } else {
                    getForm().getSerieDetailButtonList().getCambiaStatoForzaValidazione()
                            .setHidden(false);
                }
            }

            if (detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.APERTA.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
                getForm().getNoteList().setHideUpdateButton(false);
                getForm().getNoteList().setHideDeleteButton(false);
                getForm().getNoteList().setHideInsertButton(false);
                getForm().getNoteList().setHideDetailButton(false);
            }

            if (detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.FIRMATA.name())
                    || detailRow.getTiStatoVerSerie()
                            .equals(CostantiDB.StatoVersioneSerie.IN_CUSTODIA.name())) {
                getForm().getSerieDetailButtonList().getCambiaStatoAnnullata().setHidden(false);
                getForm().getSerieDetailButtonList().getCambiaStatoAggiorna().setHidden(false);
            }
        }

        if (detailRow.getIdContenutoCalc() != null) {
            getForm().getSerieDetailButtonList().getVisualizzaContenutoCalcolato().setHidden(false);
        }
        if (detailRow.getIdContenutoAcq() != null) {
            getForm().getSerieDetailButtonList().getVisualizzaContenutoAcquisito().setHidden(false);
        }

        if (detailRow.getIdContenutoEff() != null) {
            getForm().getSerieDetailButtonList().getVisualizzaContenutoEffettivo().setHidden(false);
        }

        boolean consistenzaAttesaPopolata = false;
        if (detailRow.getIdConsistVerSerie() != null && (detailRow.getNiUnitaDocAttese() != null
                || detailRow.getCdFirstUnitaDocAttesa() != null
                || detailRow.getCdLastUnitaDocAttesa() != null
                || detailRow.getTiModConsistFirstLast() != null
                || detailRow.getCdDocConsistVerSerie() != null
                || detailRow.getDsDocConsistVerSerie() != null)) {
            consistenzaAttesaPopolata = true;
        }

        /*
         * Il pulsante di visualizzazione della consistenza può essere visualizzato se la versione è
         * APERTA o DA_CONTROLLARE o CONTROLLATA o DA_VALIDARE e tutti i contenuti definiti hanno
         * stato CREATO o CONTROLLATA_CONSIST o DA_CONTROLLARE_CONSIST. Se le condizioni non sono
         * soddisfatte, e la consistenza non è definita non è visibile
         */
        if ((serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                && serieEjb.checkContenuto(idVerSerie, (detailRow.getIdContenutoCalc() != null),
                        (detailRow.getIdContenutoAcq() != null),
                        (detailRow.getIdContenutoEff() != null),
                        CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                        CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name()))
                || consistenzaAttesaPopolata) {
            getForm().getSerieDetailButtonList().getVisualizzaConsistenzaAttesa().setHidden(false);
        }

        String flCalc = getForm().getSerieDetail().getFl_calc_bloccato().getValue();
        if (detailRow.getIdContenutoCalc() != null
                && (StringUtils.isNotBlank(flCalc) && flCalc.equals("1"))) {
            getForm().getSerieDetailButtonList().getRiavviaCalcoloContenuto().setHidden(false);
        }
        String flAcq = getForm().getSerieDetail().getFl_acq_bloccato().getValue();
        if (detailRow.getIdContenutoAcq() != null
                && (StringUtils.isNotBlank(flAcq) && flAcq.equals("1"))) {
            getForm().getSerieDetailButtonList().getRiavviaAcquisizioneContenuto().setHidden(false);
        }
        String flEff = getForm().getSerieDetail().getFl_eff_bloccato().getValue();
        if (detailRow.getIdContenutoEff() != null
                && (StringUtils.isNotBlank(flEff) && flEff.equals("1"))) {
            getForm().getSerieDetailButtonList().getRiavviaGenerazioneContenuto().setHidden(false);
        }
        String flVal = getForm().getSerieDetail().getFl_val_bloccato().getValue();
        if (StringUtils.isNotBlank(flVal) && flVal.equals("1")) {
            getForm().getSerieDetailButtonList().getRiavviaValidazioneSerie().setHidden(false);
        }
        if (StringUtils.isNotBlank(detailRow.getBlFileIxAip())) {
            getForm().getSerieDetailButtonList().getDownloadAIP().setHidden(false);
            getForm().getSerieDetailButtonList().getDownloadAIP().setDisableHourGlass(true);
        }
        if (detailRow.getTiStatoVerSerie().equals(CostantiDB.StatoVersioneSerie.FIRMATA.name())
                || detailRow.getTiStatoVerSerie()
                        .equals(CostantiDB.StatoVersioneSerie.IN_CUSTODIA.name())
                || detailRow.getTiStatoVerSerie()
                        .equals(CostantiDB.StatoVersioneSerie.ANNULLATA.name())) {
            getForm().getSerieDetailButtonList().getDownloadPacchettoArk().setHidden(false);
            getForm().getSerieDetailButtonList().getDownloadPacchettoArk()
                    .setDisableHourGlass(true);
        }
        if (!getMessageBox().hasError()
                && StringUtils.isNotBlank(detailRow.getDsMsgSerieDaRigenera())) {
            getMessageBox().addWarning(detailRow.getDsMsgSerieDaRigenera());
            getMessageBox().setViewMode(ViewMode.plain);
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.SERIE_UD_DETAIL)) {
            getForm().getSerieList().setStatus(Status.view);
            getForm().getSerieDetailTabs()
                    .setCurrentTab(getForm().getSerieDetailTabs().getInfoPrincipali());

            getForm().getSerieDetail().setViewMode();
            loadDettaglioSerie(getForm().getSerieDetail().getId_ver_serie().parse(), true);
        } else if (getLastPublisher().equals(Application.Publisher.CONSISTENZA_ATTESA_DETAIL)) {
            Status status = getForm().getConsistenzaAttesaDetail().getStatus();
            getForm().getConsistenzaAttesaDetail().setStatus(Status.view);
            getForm().getConsistenzaAttesaDetail().setViewMode();

            if (status.equals(Status.insert)) {
                goBack();
            } else {
                BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
                BigDecimal idVerSerieCorr = getForm().getSerieDetail().getId_ver_serie_corr()
                        .parse();
                boolean versioneCorrente = idVerSerie.equals(idVerSerieCorr);
                loadDettaglioSerie(idVerSerie, versioneCorrente);
                if (versioneCorrente) {
                    getForm().getSerieDetail().getId_ver_serie_corr()
                            .setValue(idVerSerie.toPlainString());
                }

                visualizzaConsistenzaAttesa();
            }
        } else if (getLastPublisher().equals(Application.Publisher.LACUNA_DETAIL)) {
            goBack();
        } else if (getLastPublisher().equals(Application.Publisher.NOTA_DETAIL)) {
            goBack();
        } else if (getLastPublisher().equals(Application.Publisher.STATO_DETAIL)) {
            goBack();
        } else if (getLastPublisher()
                .equals(Application.Publisher.COMUNICAZIONE_CONSISTENZA_DETAIL)) {
            goBack();
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getSerieList().getName())) {
            // <editor-fold defaultstate="collapsed" desc="SalvataggioSerie">
            if (getForm().getSerieDetail().postAndValidate(getRequest(), getMessageBox())) {
                // Se la tabella \u00E8 serieList, dovrei essere potenzialmente sicuro che sia la
                // versione corrente
                String cdComposito = getForm().getSerieDetail().getCd_composito_serie().parse();
                final String descSerie = getForm().getSerieDetail().getDs_serie().parse();
                final BigDecimal anniConserv = getForm().getSerieDetail().getNi_anni_conserv()
                        .parse();
                final String conservUnlimited = getForm().getSerieDetail().getConserv_unlimited()
                        .parse();
                final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
                final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
                final BigDecimal aaSerie = getForm().getSerieDetail().getAa_serie().parse();
                final BigDecimal idTipoSerie = getForm().getSerieDetail().getId_tipo_serie()
                        .parse();
                final Date dtInizioSelSerie = getForm().getSerieDetail().getDt_inizio_sel_serie()
                        .parse();
                final Date dtFineSelSerie = getForm().getSerieDetail().getDt_fine_sel_serie()
                        .parse();
                final String dsListaAnniSelSerie = getForm().getSerieDetail()
                        .getDs_lista_anni_sel_serie().parse();

                if (!idVerSerie.equals(getForm().getSerieDetail().getId_ver_serie_corr().parse())) {
                    getMessageBox().addError(
                            "La serie non \u00E8 modificabile perch\u00E9 la versione visualizzata non \u00E8 quella corrente");
                }
                final String statoVerSerie = getForm().getSerieDetail().getTi_stato_ver_serie()
                        .parse();
                if (!statoVerSerie.equals(CostantiDB.StatoVersioneSerie.APERTA.name())
                        && !statoVerSerie
                                .equals(CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
                    getMessageBox().addError(
                            "La serie non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA e da DA_CONTROLLARE");
                }
                if (!getMessageBox().hasError()) {
                    checkAnniConservazioneIllimitata(conservUnlimited, anniConserv);
                }
                String tiPeriodoSelSerie = null;
                BigDecimal niPeriodoSelSerie = null;
                if (!getMessageBox().hasError()) {
                    DecTipoSerieRowBean tipoSerieRow = tipoSerieEjb
                            .getDecTipoSerieRowBean(idTipoSerie);
                    if (!getMessageBox().hasError()) {
                        checkFieldsTipoSelUdTipoSerie(tipoSerieRow.getTiSelUd(), dtInizioSelSerie,
                                dtFineSelSerie, dsListaAnniSelSerie);
                    }
                    if (!getMessageBox().hasError() && tipoSerieRow.getTiSelUd()
                            .equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
                        SerieAutomBean creaAutomBean = checkIntervalliEListaTipoSerieDtUdSerie(
                                tipoSerieRow, aaSerie, dtInizioSelSerie, dtFineSelSerie,
                                dsListaAnniSelSerie);
                        if (creaAutomBean != null) {
                            tiPeriodoSelSerie = creaAutomBean.getTipoIntervallo();
                            if (!getMessageBox().hasError()) {
                                int index = creaAutomBean.getIntervalli()
                                        .indexOf(new IntervalliSerieAutomBean(dtInizioSelSerie,
                                                dtFineSelSerie, null, null));
                                if (index != -1) {
                                    niPeriodoSelSerie = (tiPeriodoSelSerie != null
                                            ? new BigDecimal(index + 1)
                                            : null);
                                    String cdSerie = cdComposito;
                                    String newCdSerie;
                                    if (cdSerie.contains("-" + aaSerie.toPlainString())) {
                                        newCdSerie = cdSerie.substring(0,
                                                cdSerie.indexOf("-" + aaSerie.toPlainString()))
                                                + "-" + aaSerie.toPlainString() + "-" + (index + 1);
                                    } else {
                                        newCdSerie = cdSerie + "-" + aaSerie.toPlainString() + "-"
                                                + (index + 1);
                                    }
                                    cdComposito = newCdSerie;
                                }
                            }
                        }
                    } else {
                        String cdSerie = cdComposito;
                        String newCdSerie;
                        if (cdSerie.contains("-" + aaSerie.toPlainString())) {
                            newCdSerie = cdSerie.substring(0,
                                    cdSerie.indexOf("-" + aaSerie.toPlainString())) + "-"
                                    + aaSerie.toPlainString();
                        } else {
                            newCdSerie = cdSerie += "-" + aaSerie.toPlainString();
                        }
                        cdComposito = newCdSerie;
                    }
                }

                if (!getMessageBox().hasError()) {
                    try {

                        serieEjb.updateSerie(idSerie, idVerSerie, cdComposito, descSerie,
                                anniConserv, dtInizioSelSerie, dtFineSelSerie, dsListaAnniSelSerie,
                                tiPeriodoSelSerie, niPeriodoSelSerie);

                        getForm().getSerieDetail().getCd_composito_serie().setValue(cdComposito);
                        getMessageBox().addInfo("Serie modificata con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }

            }
            if (!getMessageBox().hasError()) {
                getForm().getSerieList().setStatus(Status.view);
                getForm().getSerieDetail().setViewMode();
                getForm().getSerieDetailButtonList().setEditMode();
            }
            forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
            // </editor-fold>
        } else if (getTableName().equals(getForm().getConsistenzaAttesaDetail().getName())) {
            // <editor-fold defaultstate="collapsed" desc="Salvataggio consistenza">
            BigDecimal idVerSerie = null;
            if (getForm().getConsistenzaAttesaDetail().postAndValidate(getRequest(),
                    getMessageBox())) {
                BigDecimal idSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_serie()
                        .parse();
                idVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_ver_serie()
                        .parse();
                BigDecimal idConsistVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail()
                        .getId_consist_ver_serie().parse();
                BigDecimal niUdAttese = getForm().getConsistenzaAttesaDetail()
                        .getNi_unita_doc_attese().parse();
                String tiMod = getForm().getConsistenzaAttesaDetail().getTi_mod_consist_first_last()
                        .parse();
                String cdFirstUdAttesa = getForm().getConsistenzaAttesaDetail()
                        .getCd_first_unita_doc_attesa().parse();
                String cdLastUdAttesa = getForm().getConsistenzaAttesaDetail()
                        .getCd_last_unita_doc_attesa().parse();
                String cdDocConsist = getForm().getConsistenzaAttesaDetail()
                        .getCd_doc_consist_ver_serie().parse();
                String dsDocConsist = getForm().getConsistenzaAttesaDetail()
                        .getDs_doc_consist_ver_serie().parse();
                Date dtComunicConsistVerSerie = getForm().getConsistenzaAttesaDetail()
                        .getDt_comunic_consist_ver_serie().parse();
                String cdRegistroFirst = getForm().getConsistenzaAttesaDetail()
                        .getCd_registro_first().parse();
                BigDecimal aaUnitaDocFirst = getForm().getConsistenzaAttesaDetail()
                        .getAa_unita_doc_first().parse();
                String cdUnitaDocFirst = getForm().getConsistenzaAttesaDetail()
                        .getCd_unita_doc_first().parse();
                String cdRegistroLast = getForm().getConsistenzaAttesaDetail().getCd_registro_last()
                        .parse();
                BigDecimal aaUnitaDocLast = getForm().getConsistenzaAttesaDetail()
                        .getAa_unita_doc_last().parse();
                String cdUnitaDocLast = getForm().getConsistenzaAttesaDetail()
                        .getCd_unita_doc_last().parse();

                if (StringUtils.isNotBlank(tiMod)) {
                    if (!tiMod.equals(CostantiDB.ModalitaDefPrimaUltimaUd.CHIAVE_UD.name())
                            && (StringUtils.isBlank(cdFirstUdAttesa)
                                    || StringUtils.isBlank(cdLastUdAttesa))) {
                        getMessageBox().addError(
                                "Valorizzare i campi di 'Prima unit\u00E0 documentaria' e 'Ultima unit\u00E0 documentaria' per la modalit\u00E0 selezionata");
                    } else if (tiMod.equals(CostantiDB.ModalitaDefPrimaUltimaUd.CHIAVE_UD.name())) {
                        if (StringUtils.isBlank(cdRegistroFirst)
                                && StringUtils.isBlank(cdRegistroLast)
                                && StringUtils.isBlank(cdUnitaDocFirst)
                                && StringUtils.isBlank(cdUnitaDocLast) && aaUnitaDocFirst == null
                                && aaUnitaDocLast == null) {
                            getMessageBox().addError(
                                    "Valorizzare i campi di 'Prima unit\u00E0 documentaria' e 'Ultima unit\u00E0 documentaria' per la modalit\u00E0 selezionata");
                        } else if (!((StringUtils.isNotBlank(cdRegistroFirst)
                                && aaUnitaDocFirst != null
                                && StringUtils.isNotBlank(cdUnitaDocFirst))
                                || (StringUtils.isBlank(cdRegistroFirst) && aaUnitaDocFirst == null
                                        && StringUtils.isBlank(cdUnitaDocFirst)))) {
                            getMessageBox().addError(
                                    "Valorizzare i campi di 'Prima unit\u00E0 documentaria' in maniera corretta");
                        } else if (!((StringUtils.isNotBlank(cdRegistroLast)
                                && aaUnitaDocLast != null && StringUtils.isNotBlank(cdUnitaDocLast))
                                || (StringUtils.isBlank(cdRegistroLast) && aaUnitaDocLast == null
                                        && StringUtils.isBlank(cdUnitaDocLast)))) {
                            getMessageBox().addError(
                                    "Valorizzare i campi di 'Ultima unit\u00E0 documentaria' in maniera corretta");
                        }
                    }
                    if (tiMod.equals(CostantiDB.ModalitaDefPrimaUltimaUd.PROGRESSIVO.name())) {
                        if (!StringUtils.isNumeric(cdFirstUdAttesa)
                                || !StringUtils.isNumeric(cdLastUdAttesa)) {
                            getMessageBox().addError(
                                    "Il valore di 'Prima unit\u00E0 documentaria' o 'Ultima unit\u00E0 documentaria' non \u00E8 coerente con la modalit\u00E0 selezionata");
                        }
                    }
                } else if (niUdAttese == null && StringUtils.isBlank(cdDocConsist)
                        && StringUtils.isBlank(dsDocConsist)) {
                    getMessageBox().addError(
                            "Valorizzare almeno un campo per il salvataggio della consistenza");
                }

                if (!getMessageBox().hasError()) {
                    try {
                        BigDecimal id;
                        if (StringUtils.isNotBlank(tiMod) && tiMod
                                .equals(CostantiDB.ModalitaDefPrimaUltimaUd.CHIAVE_UD.name())) {
                            id = serieEjb.saveConsistenzaAttesa(getUser().getIdUtente(),
                                    idConsistVerSerie, idSerie, idVerSerie, niUdAttese,
                                    cdDocConsist, dsDocConsist, dtComunicConsistVerSerie,
                                    cdRegistroFirst, aaUnitaDocFirst, cdUnitaDocFirst,
                                    cdRegistroLast, aaUnitaDocLast, cdUnitaDocLast);
                        } else {
                            id = serieEjb.saveConsistenzaAttesa(getUser().getIdUtente(),
                                    idConsistVerSerie, idSerie, idVerSerie, niUdAttese, tiMod,
                                    cdFirstUdAttesa, cdLastUdAttesa, cdDocConsist, dsDocConsist,
                                    dtComunicConsistVerSerie);
                        }

                        if (id != null) {
                            getForm().getDatiSerieConsistenzaAttesaDetail()
                                    .getId_consist_ver_serie().setValue(id.toPlainString());
                            getForm().getSerieDetail().getId_consist_ver_serie()
                                    .setValue(id.toPlainString());
                            getForm().getContenutoSerieDetail().getId_consist_ver_serie()
                                    .setValue(id.toPlainString());
                        }

                        getMessageBox().addInfo("Consistenza attesa salvata con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    } catch (ParerUserError ex) {
                        getMessageBox()
                                .addError("La consistenza attesa non pu\u00F2 essere salvata: "
                                        + ex.getDescription());
                    }
                }
            }
            if (!getMessageBox().hasError()) {
                // Ricarica consistenza attesa
                getForm().getConsistenzaAttesaDetail().setStatus(Status.view);
                getForm().getConsistenzaAttesaDetail().setViewMode();

                getForm().getLacuneList().setHideInsertButton(false);
                getForm().getLacuneList().setHideUpdateButton(false);
                getForm().getLacuneList().setHideDeleteButton(false);

                if (serieEjb.checkVersione(idVerSerie,
                        CostantiDB.StatoVersioneSerie.APERTA.name())) {
                    if (serieEjb.existContenuto(idVerSerie,
                            CostantiDB.TipoContenutoVerSerie.CALCOLATO.name())
                            && serieEjb.checkContenuto(idVerSerie, true, false, false,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())) {
                        getForm().getConsistenzaButtonList()
                                .getControllaContenutoCalcConsistAttesa().setHidden(false);
                    }
                    if (serieEjb.existContenuto(idVerSerie,
                            CostantiDB.TipoContenutoVerSerie.ACQUISITO.name())
                            && serieEjb.checkContenuto(idVerSerie, false, true, false,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())) {
                        getForm().getConsistenzaButtonList().getControllaContenutoAcqConsistAttesa()
                                .setHidden(false);
                    }
                } else if (serieEjb.checkVersione(idVerSerie,
                        CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
                    if (serieEjb.existContenuto(idVerSerie,
                            CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())
                            && serieEjb.checkContenuto(idVerSerie, false, false, true,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())) {
                        getForm().getConsistenzaButtonList().getControllaContenutoEffConsistAttesa()
                                .setHidden(false);
                    }
                }
                getForm().getDatiSerieConsistenzaAttesaDetail().getShow_edit()
                        .setValue(String.valueOf(true));
                getForm().getDatiSerieConsistenzaAttesaDetail().getShow_delete()
                        .setValue(String.valueOf(true));
            }
            forwardToPublisher(Application.Publisher.CONSISTENZA_ATTESA_DETAIL);
            // </editor-fold>
        } else if (getTableName().equals(getForm().getLacunaDetail().getName())
                || getTableName().equals(getForm().getLacuneList().getName())) {
            // <editor-fold defaultstate="collapsed" desc="Salvataggio lacuna">
            if (getForm().getLacunaDetail().postAndValidate(getRequest(), getMessageBox())) {
                BigDecimal idConsistVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail()
                        .getId_consist_ver_serie().parse();
                String tiModConsist = getForm().getConsistenzaAttesaDetail()
                        .getTi_mod_consist_first_last().parse();

                BigDecimal pg = getForm().getLacunaDetail().getPg_lacuna().parse();
                String tiLacuna = getForm().getLacunaDetail().getTi_lacuna().parse();
                String tiModLacuna = getForm().getLacunaDetail().getTi_mod_lacuna().parse();
                BigDecimal niIniLacuna = getForm().getLacunaDetail().getNi_ini_lacuna().parse();
                BigDecimal niFinLacuna = getForm().getLacunaDetail().getNi_fin_lacuna().parse();
                String dlLacuna = getForm().getLacunaDetail().getDl_lacuna().parse();
                String dlNotaLacuna = getForm().getLacunaDetail().getDl_nota_lacuna().parse();

                if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())
                        && StringUtils.isNotBlank(dlLacuna)) {
                    getMessageBox().addError(
                            "Il campo 'Descrizione' non pu\u00F2 essere popolato se la modalit\u00E0 \u00E8 'RANGE_PROGRESSIVI'");
                }

                if (!getMessageBox().hasError()) {
                    if (tiModLacuna.equals(CostantiDB.TipoModLacuna.DESCRIZIONE.name())
                            && (niIniLacuna != null || niFinLacuna != null)) {
                        getMessageBox().addError(
                                "I campi di range non possono essere popolati se la modalit\u00E0 \u00E8 'DESCRIZIONE'");
                    }
                }

                if (!getMessageBox().hasError()) {
                    if (tiModLacuna.equals(CostantiDB.TipoModLacuna.DESCRIZIONE.name())
                            && StringUtils.isBlank(dlLacuna)) {
                        getMessageBox().addError("Descrizione lacuna non inserita");
                    }
                }
                if (!getMessageBox().hasError()) {
                    if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())
                            && (niIniLacuna == null || niFinLacuna == null)) {
                        getMessageBox().addError("Campi di range non inseriti");
                    }
                }

                if (!getMessageBox().hasError()) {
                    if (tiModLacuna.equals(CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name())) {
                        BigDecimal idLacuna = getForm().getLacunaDetail()
                                .getId_lacuna_consist_ver_serie().parse();
                        if (serieEjb.existLacuna(idConsistVerSerie, idLacuna, niIniLacuna,
                                niFinLacuna)) {
                            if (getForm().getLacuneList().getStatus().equals(Status.insert)) {
                                getMessageBox().addError(
                                        "La lacuna aggiunta si interseca con altre lacune");
                            } else if (getForm().getLacuneList().getStatus()
                                    .equals(Status.update)) {
                                getMessageBox().addError(
                                        "La lacuna modificata si interseca con altre lacune");
                            }
                        }
                        if (tiModConsist == null || !tiModConsist
                                .equals(CostantiDB.ModalitaDefPrimaUltimaUd.PROGRESSIVO.name())) {
                            getMessageBox().addError(
                                    "\u00C8 possibile definire una lacuna mediante un range di progressivi solo se la \"Modalit\u00E0 definizione prima ed ultima unit\u00E0 documentaria\" \u00E8 pari a PROGRESSIVO");
                        }
                    }
                }
                if (!getMessageBox().hasError()) {
                    try {
                        if (getForm().getLacuneList().getStatus().equals(Status.insert)) {
                            BigDecimal idLacuna = serieEjb.saveLacuna(getUser().getIdUtente(),
                                    idConsistVerSerie, pg, tiLacuna, tiModLacuna, niIniLacuna,
                                    niFinLacuna, dlLacuna, dlNotaLacuna);
                            if (idLacuna != null) {
                                getForm().getLacunaDetail().getId_lacuna_consist_ver_serie()
                                        .setValue(idLacuna.toPlainString());
                            }
                            SerLacunaConsistVerSerieRowBean row = new SerLacunaConsistVerSerieRowBean();
                            getForm().getLacunaDetail().copyToBean(row);
                            getForm().getLacuneList().getTable().last();
                            getForm().getLacuneList().getTable().add(row);
                        } else if (getForm().getLacuneList().getStatus().equals(Status.update)) {
                            BigDecimal idLacuna = getForm().getLacunaDetail()
                                    .getId_lacuna_consist_ver_serie().parse();
                            serieEjb.saveLacuna(getUser().getIdUtente(), idLacuna, tiLacuna,
                                    niIniLacuna, niFinLacuna, dlLacuna, dlNotaLacuna);
                        }
                        getMessageBox().addInfo("Lacuna salvata con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(
                                "La lacuna non pu\u00F2 essere salvata: " + ex.getDescription());
                    }
                }
            }
            // Salvataggio lacuna
            if (!getMessageBox().hasError()) {
                getForm().getLacuneList().setStatus(Status.view);
                getForm().getLacunaDetail().setStatus(Status.view);
                getForm().getLacunaDetail().setViewMode();
            }
            forwardToPublisher(Application.Publisher.LACUNA_DETAIL);
            // </editor-fold>
        } else if (getTableName().equals(getForm().getNotaDetail().getName())
                || getTableName().equals(getForm().getNoteList().getName())) {
            // <editor-fold defaultstate="collapsed" desc="Salvataggio nota (elemento di
            // descrizione)">
            BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
            if (getForm().getNotaDetail().postAndValidate(getRequest(), getMessageBox())) {
                BigDecimal idTipoNotaSerie = getForm().getNotaDetail().getId_tipo_nota_serie()
                        .parse();
                BigDecimal pgNota = getForm().getNotaDetail().getPg_nota_ver_serie().parse();
                String dsNota = getForm().getNotaDetail().getDs_nota_ver_serie().parse();
                Date dtNota = new Date(
                        getForm().getNotaDetail().getDt_nota_ver_serie().parse().getTime());

                try {
                    int rowIndex = getForm().getNoteList().getTable().getCurrentRowIndex();
                    if (getForm().getNoteList().getStatus().equals(Status.insert)) {
                        BigDecimal idNota = serieEjb.saveNota(getUser().getIdUtente(), idVerSerie,
                                idTipoNotaSerie, pgNota, dsNota, dtNota);
                        if (idNota != null) {
                            getForm().getNotaDetail().getId_nota_ver_serie()
                                    .setValue(idNota.toPlainString());
                        }
                        rowIndex = getForm().getNoteList().getTable().size();
                    } else if (getForm().getNoteList().getStatus().equals(Status.update)) {
                        BigDecimal idNota = getForm().getNotaDetail().getId_nota_ver_serie()
                                .parse();
                        serieEjb.saveNota(idNota, dsNota, getUser().getIdUtente(), dtNota);
                    }
                    SerVLisNotaSerieTableBean noteTb = serieEjb
                            .getSerVLisNotaSerieTableBean(idVerSerie);
                    if (getForm().getNoteList().getTable() != null) {
                        int pageSize = getForm().getNoteList().getTable().getPageSize();
                        getForm().getNoteList().setTable(noteTb);
                        getForm().getNoteList().getTable().setPageSize(pageSize);
                        getForm().getNoteList().getTable().setCurrentRowIndex(rowIndex);
                    } else {
                        getForm().getNoteList().setTable(noteTb);
                        getForm().getNoteList().getTable()
                                .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        getForm().getNoteList().getTable().setCurrentRowIndex(rowIndex);
                    }

                    getMessageBox().addInfo("Elemento di descrizione salvato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } catch (ParerUserError ex) {
                    getMessageBox()
                            .addError("L'elemento di descrizione non pu\u00F2 essere salvato: "
                                    + ex.getDescription());
                }
            }
            // Salvataggio nota
            if (!getMessageBox().hasError()) {
                getForm().getNoteList().setStatus(Status.view);
                getForm().getNotaDetail().setStatus(Status.view);
                getForm().getNotaDetail().setViewMode();
            }
            forwardToPublisher(Application.Publisher.NOTA_DETAIL);
            // </editor-fold>
        } else if (getTableName().equals(getForm().getStatiList().getName())) {
            // <editor-fold defaultstate="collapsed" desc="Salvataggio stato">
            if (getForm().getStatoDetail().postAndValidate(getRequest(), getMessageBox())) {
                BigDecimal idStato = getForm().getStatoDetail().getId_stato_ver_serie().parse();
                String dsAzione = getForm().getStatoDetail().getDs_azione().parse();
                String dsNota = getForm().getStatoDetail().getDs_nota_azione().parse();
                try {
                    if (idStato != null) {
                        serieEjb.saveStato(idStato, dsAzione, dsNota);
                    }

                    BigDecimal id = getForm().getStatiList().getTable().getCurrentRow()
                            .getBigDecimal(
                                    getForm().getStatiList().getId_stato_ver_serie().getName());
                    if (id.equals(idStato)) {
                        // Controllo giusto per sicurezza che sto gestendo la stessa riga, e setto
                        // le stringhe
                        // anzich\u00E9 ricaricare tutta la lista
                        getForm().getStatiList().getTable().getCurrentRow().setString(
                                getForm().getStatiList().getDs_azione().getName(), dsAzione);
                        getForm().getStatiList().getTable().getCurrentRow().setString(
                                getForm().getStatiList().getDs_nota_azione().getName(), dsNota);
                    }
                    getMessageBox().addInfo("Stato salvato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(
                            "Lo stato non pu\u00F2 essere salvato: " + ex.getDescription());
                }
            }
            // Salvataggio stato
            if (!getMessageBox().hasError()) {
                getForm().getStatiList().setStatus(Status.view);
                getForm().getStatoDetail().setViewMode();
            }
            forwardToPublisher(Application.Publisher.STATO_DETAIL);
            // </editor-fold>
        } else if (getTableName().equals(getForm().getComunicazioneConsistenzaDetail().getName())) {
            // <editor-fold defaultstate="collapsed" desc="Salvataggio comunicazione consistenza">
            BigDecimal idSerie = null;
            BigDecimal idVerSerie = null;
            if (getForm().getComunicazioneConsistenzaDetail().postAndValidate(getRequest(),
                    getMessageBox())) {
                idSerie = getForm().getComunicazioneConsistenzaDetail().getId_serie().parse();
                idVerSerie = getForm().getComunicazioneConsistenzaDetail().getId_ver_serie()
                        .parse();
                BigDecimal idConsistVerSerie = getForm().getComunicazioneConsistenzaDetail()
                        .getId_consist_ver_serie().parse();
                BigDecimal niUdAttese = getForm().getComunicazioneConsistenzaDetail()
                        .getNi_unita_doc_attese().parse();
                String cdRegistroFirst = getForm().getComunicazioneConsistenzaDetail()
                        .getCd_registro_first().parse();
                BigDecimal aaUnitaDocFirst = getForm().getComunicazioneConsistenzaDetail()
                        .getAa_unita_doc_first().parse();
                String cdUnitaDocFirst = getForm().getComunicazioneConsistenzaDetail()
                        .getCd_unita_doc_first().parse();
                String cdRegistroLast = getForm().getComunicazioneConsistenzaDetail()
                        .getCd_registro_last().parse();
                BigDecimal aaUnitaDocLast = getForm().getComunicazioneConsistenzaDetail()
                        .getAa_unita_doc_last().parse();
                String cdUnitaDocLast = getForm().getComunicazioneConsistenzaDetail()
                        .getCd_unita_doc_last().parse();
                Date dtComunicConsistVerSerie = getForm().getComunicazioneConsistenzaDetail()
                        .getDt_comunic_consist_ver_serie().parse();
                BigDecimal idLacunaMancanti = getForm().getComunicazioneConsistenzaDetail()
                        .getId_lacuna_mancanti().parse();
                String dlLacunaMancanti = getForm().getComunicazioneConsistenzaDetail()
                        .getDl_lacuna_mancanti().parse();
                String dlNotaLacunaMancanti = getForm().getComunicazioneConsistenzaDetail()
                        .getDl_nota_lacuna_mancanti().parse();
                BigDecimal idLacunaNonProdotte = getForm().getComunicazioneConsistenzaDetail()
                        .getId_lacuna_non_prodotte().parse();
                String dlLacunaNonProdotte = getForm().getComunicazioneConsistenzaDetail()
                        .getDl_lacuna_non_prodotte().parse();
                String dlNotaLacunaNonProdotte = getForm().getComunicazioneConsistenzaDetail()
                        .getDl_nota_lacuna_non_prodotte().parse();

                if (niUdAttese == null && StringUtils.isBlank(cdRegistroFirst)
                        && aaUnitaDocFirst == null && StringUtils.isBlank(cdUnitaDocFirst)
                        && StringUtils.isBlank(cdRegistroLast) && aaUnitaDocLast == null
                        && StringUtils.isBlank(cdUnitaDocLast)) {
                    getMessageBox().addError(
                            "Valorizzare almeno un campo per il salvataggio della consistenza");
                }
                if (!getMessageBox().hasError()) {
                    if (!((StringUtils.isNotBlank(cdRegistroFirst) && aaUnitaDocFirst != null
                            && StringUtils.isNotBlank(cdUnitaDocFirst))
                            || (StringUtils.isBlank(cdRegistroFirst) && aaUnitaDocFirst == null
                                    && StringUtils.isBlank(cdUnitaDocFirst)))) {
                        getMessageBox().addError(
                                "Valorizzare i campi di 'Prima unit\u00E0 documentaria' in maniera corretta");
                    } else if (!((StringUtils.isNotBlank(cdRegistroLast) && aaUnitaDocLast != null
                            && StringUtils.isNotBlank(cdUnitaDocLast))
                            || (StringUtils.isBlank(cdRegistroLast) && aaUnitaDocLast == null
                                    && StringUtils.isBlank(cdUnitaDocLast)))) {
                        getMessageBox().addError(
                                "Valorizzare i campi di 'Ultima unit\u00E0 documentaria' in maniera corretta");
                    }
                }

                if (!getMessageBox().hasError()) {
                    try {
                        if (StringUtils.isNotBlank(cdRegistroFirst)
                                || StringUtils.isNotBlank(cdRegistroLast)) {
                            idConsistVerSerie = serieEjb.saveConsistenzaAttesa(
                                    getUser().getIdUtente(), idConsistVerSerie, idSerie, idVerSerie,
                                    niUdAttese, null, null, dtComunicConsistVerSerie,
                                    cdRegistroFirst, aaUnitaDocFirst, cdUnitaDocFirst,
                                    cdRegistroLast, aaUnitaDocLast, cdUnitaDocLast);
                        } else {
                            idConsistVerSerie = serieEjb.saveConsistenzaAttesa(
                                    getUser().getIdUtente(), idConsistVerSerie, idSerie, idVerSerie,
                                    niUdAttese, null, null, null, null, null,
                                    dtComunicConsistVerSerie);
                        }

                        if (idConsistVerSerie != null) {
                            getForm().getComunicazioneConsistenzaDetail().getId_consist_ver_serie()
                                    .setValue(idConsistVerSerie.toPlainString());

                            if (idLacunaMancanti != null) {
                                // record già esistente
                                if (StringUtils.isBlank(dlLacunaMancanti)) {
                                    // elimina il record della lacuna
                                    serieEjb.deleteLacuna(getUser().getIdUtente(),
                                            idLacunaMancanti);
                                    getForm().getComunicazioneConsistenzaDetail()
                                            .getId_lacuna_mancanti().setValue(null);
                                    getForm().getComunicazioneConsistenzaDetail()
                                            .getDl_lacuna_mancanti().setValue(null);
                                    getForm().getComunicazioneConsistenzaDetail()
                                            .getDl_nota_lacuna_mancanti().setValue(null);
                                } else {
                                    // aggiorna il record
                                    serieEjb.saveLacuna(getUser().getIdUtente(), idLacunaMancanti,
                                            CostantiDB.TipoLacuna.MANCANTI.name(), null, null,
                                            dlLacunaMancanti, dlNotaLacunaMancanti);
                                }
                            } else if (StringUtils.isNotBlank(dlLacunaMancanti)) {
                                // inserisce la nuova lacuna
                                BigDecimal maxPgLacuna = serieEjb.getMaxPgLacuna(idConsistVerSerie);
                                BigDecimal idLacuna = serieEjb.saveLacuna(getUser().getIdUtente(),
                                        idConsistVerSerie, maxPgLacuna.add(BigDecimal.ONE),
                                        CostantiDB.TipoLacuna.MANCANTI.name(),
                                        CostantiDB.TipoModLacuna.DESCRIZIONE.name(), null, null,
                                        dlLacunaMancanti, dlNotaLacunaMancanti);
                                getForm().getComunicazioneConsistenzaDetail()
                                        .getId_lacuna_mancanti().setValue(idLacuna.toPlainString());
                            }
                            if (idLacunaNonProdotte != null) {
                                // record già esistente
                                if (StringUtils.isBlank(dlLacunaNonProdotte)) {
                                    // elimina il record della lacuna
                                    serieEjb.deleteLacuna(getUser().getIdUtente(),
                                            idLacunaNonProdotte);
                                    getForm().getComunicazioneConsistenzaDetail()
                                            .getId_lacuna_non_prodotte().setValue(null);
                                    getForm().getComunicazioneConsistenzaDetail()
                                            .getDl_lacuna_non_prodotte().setValue(null);
                                    getForm().getComunicazioneConsistenzaDetail()
                                            .getDl_nota_lacuna_non_prodotte().setValue(null);
                                } else {
                                    // aggiorna il record
                                    serieEjb.saveLacuna(getUser().getIdUtente(),
                                            idLacunaNonProdotte,
                                            CostantiDB.TipoLacuna.NON_PRODOTTE.name(), null, null,
                                            dlLacunaNonProdotte, dlNotaLacunaNonProdotte);
                                }
                            } else if (StringUtils.isNotBlank(dlLacunaNonProdotte)) {
                                // inserisce la nuova lacuna
                                BigDecimal maxPgLacuna = serieEjb.getMaxPgLacuna(idConsistVerSerie);
                                BigDecimal idLacuna = serieEjb.saveLacuna(getUser().getIdUtente(),
                                        idConsistVerSerie, maxPgLacuna.add(BigDecimal.ONE),
                                        CostantiDB.TipoLacuna.NON_PRODOTTE.name(),
                                        CostantiDB.TipoModLacuna.DESCRIZIONE.name(), null, null,
                                        dlLacunaNonProdotte, dlNotaLacunaNonProdotte);
                                getForm().getComunicazioneConsistenzaDetail()
                                        .getId_lacuna_non_prodotte()
                                        .setValue(idLacuna.toPlainString());
                            }
                        }

                        getMessageBox().addInfo("Consistenza attesa salvata con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    } catch (ParerUserError ex) {
                        getMessageBox()
                                .addError("La consistenza attesa non pu\u00F2 essere salvata: "
                                        + ex.getDescription());
                    }
                }
            }
            if (!getMessageBox().hasError()) {
                // Ricarica consistenza attesa
                getForm().getConsistenzaSerieList().setStatus(Status.view);
                getForm().getComunicazioneConsistenzaDetail().setStatus(Status.view);
                getForm().getComunicazioneConsistenzaDetail().setViewMode();

                getForm().getComunicazioneConsistenzaDetail().getShow_edit()
                        .setValue(String.valueOf(true));
                getForm().getComunicazioneConsistenzaDetail().getShow_delete()
                        .setValue(String.valueOf(true));

                if (!getMessageBox().hasError()) {
                    if (idSerie != null && idVerSerie != null) {
                        if (serieEjb.checkVersione(idVerSerie,
                                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
                                && serieEjb.existContenuto(idVerSerie,
                                        CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())
                                && serieEjb.checkContenuto(idVerSerie, false, false, true,
                                        CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                                .name())) {
                            // se stato versione = DA_CONTROLLARE e se contenuto di tipo EFFETTIVO
                            // di versione serie
                            // esiste ed ha stato CREATO o DA_CONTROLLARE_CONSIST eseguo controllo
                            try {
                                serieEjb.initControlloContenuto(idSerie, idVerSerie,
                                        CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
                                serieEjb.callControlloContenutoAsync(getUser().getIdUtente(),
                                        idVerSerie.longValue(),
                                        CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());

                                getMessageBox().addInfo(
                                        "Lanciato automaticamente controllo del contenuto della serie a causa della modifica della consistenza");
                            } catch (ParerUserError ex) {
                                getMessageBox().addError(ex.getDescription());
                            }
                        }
                    }
                }
            }
            forwardToPublisher(Application.Publisher.COMUNICAZIONE_CONSISTENZA_DETAIL);
            // </editor-fold>
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getSerieList().getName())
                    || getTableName().equals(getForm().getSerieDaFirmareList().getName())
                    || getTableName().equals(getForm().getSerieDaValidareList().getName())) {
                forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
            } else if (getTableName().equals(getForm().getVersioniPrecedentiList().getName())
                    || getTableName()
                            .equals(getForm().getVersioniPrecedentiDetailList().getName())) {
                if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)) {
                    SessionManager.addPrevExecutionToHistory(getSession(), false, true);
                }
                forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
            } else if (getTableName().equals(getForm().getStatiList().getName())) {
                forwardToPublisher(Application.Publisher.STATO_DETAIL);
            } else if (getTableName().equals(getForm().getErroriContenutiList().getName())) {
                forwardToPublisher(Application.Publisher.ERR_CONTENUTO_DETAIL);
            } else if (getTableName().equals(getForm().getErroriFileInputList().getName())) {
                forwardToPublisher(Application.Publisher.ERR_FILE_INPUT_DETAIL);
            } else if (getTableName().equals(getForm().getNoteList().getName())) {
                forwardToPublisher(Application.Publisher.NOTA_DETAIL);
            } else if (getTableName().equals(getForm().getUdList().getName())) {
                SerVLisUdAppartSerieRowBean currentRowBean = (SerVLisUdAppartSerieRowBean) getForm()
                        .getUdList().getTable().getCurrentRow();
                BigDecimal idUnitaDoc = currentRowBean.getIdUnitaDoc();
                if (idUnitaDoc != null) {
                    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
                            .getAroVRicUnitaDocRowBean(idUnitaDoc, null, null);
                    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())
                            && !"1".equals(aroVRicUnitaDocRowBean.getFlUnitaDocAnnul())) {
                        SerVLisUdAppartSerieTableBean tb = (SerVLisUdAppartSerieTableBean) getForm()
                                .getUdList().getTable();
                        // List<BigDecimal> idUnitaDocs = (List<BigDecimal>) (List<?>)
                        // tb.toList(SerVLisUdAppartSerieRowBean.TABLE_DESCRIPTOR.COL_ID_UNITA_DOC,
                        // new

                        loadUdDetail(tb);
                    } else {
                        getMessageBox().addError(
                                "Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
                        forwardToPublisher(getLastPublisher());
                    }
                }
            } else if (getTableName().equals(getForm().getUdVolumeList().getName())) {
                SerVLisUdAppartVolSerieTableBean tb = (SerVLisUdAppartVolSerieTableBean) getForm()
                        .getUdVolumeList().getTable();
                loadUdDetail(tb);
            } else if (getTableName().equals(getForm().getErroreContenutoUdList().getName())) {
                SerVLisUdNoversRowBean currentRowBean = (SerVLisUdNoversRowBean) getForm()
                        .getErroreContenutoUdList().getTable().getCurrentRow();
                BigDecimal idUnitaDocNonVers = currentRowBean.getIdUnitaDocNonVers();
                String cdRegistroKeyUnitaDoc = currentRowBean.getCdRegistroKeyUnitaDoc();
                BigDecimal aaKeyUnitaDoc = currentRowBean.getAaKeyUnitaDoc();
                String cdKeyUnitaDoc = currentRowBean.getCdKeyUnitaDoc();
                BigDecimal idStrut = currentRowBean.getIdStrut();

                loadUdNoVersDetail(idUnitaDocNonVers, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                        cdKeyUnitaDoc, idStrut);
            } else if (getTableName().equals(getForm().getErroriFileInputUdList().getName())) {
                SerVLisUdErrFileInputTableBean tb = (SerVLisUdErrFileInputTableBean) getForm()
                        .getErroriFileInputUdList().getTable();
                loadUdDetail(tb);
            } else if (getTableName().equals(getForm().getLacuneList().getName())) {
                forwardToPublisher(Application.Publisher.LACUNA_DETAIL);
            } else if (getTableName().equals(getForm().getVolumiList().getName())) {
                forwardToPublisher(Application.Publisher.VOLUME_SERIE_UD_DETAIL);
            } else if (getTableName().equals(getForm().getConsistenzaSerieList().getName())
                    || getTableName()
                            .equals(getForm().getComunicazioneConsistenzaDetail().getName())) {
                if (!getMessageBox().hasError()) {
                    forwardToPublisher(Application.Publisher.COMUNICAZIONE_CONSISTENZA_DETAIL);
                } else {
                    forwardToPublisher(getLastPublisher());
                }
            }
        }
    }

    private void loadUdNoVersDetail(BigDecimal idUnitaDocNonVers, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal idStrut) {
        MonitoraggioForm form = new MonitoraggioForm();
        MonVLisUdNonVersIamTableBean tb = new MonVLisUdNonVersIamTableBean();
        MonVLisUdNonVersIamRowBean row = new MonVLisUdNonVersIamRowBean();
        row.setAaKeyUnitaDoc(aaKeyUnitaDoc);
        row.setCdRegistroKeyUnitaDoc(cdRegistroKeyUnitaDoc);
        row.setCdKeyUnitaDoc(cdKeyUnitaDoc);
        row.setIdStrut(idStrut);
        row.setIdUnitaDocNonVers(idUnitaDocNonVers);
        tb.add(row);
        form.getDocumentiDerivantiDaVersFallitiList().setTable(tb);
        form.getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().setValue("UNITA_DOC");
        redirectToAction(Application.Actions.MONITORAGGIO,
                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW
                        + "&table=" + MonitoraggioForm.DocumentiDerivantiDaVersFallitiList.NAME
                        + "&riga=0",
                form);
    }

    private void loadUdDetail(AbstractBaseTable tb) {
        UnitaDocumentarieForm form = new UnitaDocumentarieForm();
        form.getUnitaDocumentarieList().setTable(tb);
        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW
                        + "&table=" + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga="
                        + tb.getCurrentRowIndex(),
                form);
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.CREAZIONE_SERIE_UD;
    }

    @Override
    public void reloadAfterGoBack(String publisher) {
        try {
            if (publisher.equals(Application.Publisher.SERIE_UD_DETAIL)) {
                BigDecimal idVerSerie;
                int level = 1;
                if (getLastPublisher().equals(publisher)) {
                    // Se anche il last publisher = SERIE_UD_DETAIL, bisogna caricare il dettaglio
                    // di una versione di
                    // serie diversa.
                    VerSerieDetailBean detailBean = popIdVerSerieDetailStack();
                    idVerSerie = detailBean != null ? detailBean.getIdVerSerie() : null;
                    if (idVerSerie != null && idVerSerie
                            .equals(getForm().getSerieDetail().getId_ver_serie().parse())) {
                        // Se l'idVerSerie ottenuto è l'ultimo dello stack, non devo ricaricare
                        // quello ma il precedente,
                        // quindi faccio un'altra pop
                        detailBean = popIdVerSerieDetailStack();
                        idVerSerie = detailBean != null ? detailBean.getIdVerSerie() : null;
                    } else if (idVerSerie == null) {
                        // Nel caso lo stack sia vuoto, non dovrebbe succedere mai.
                        idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
                    }
                    if (detailBean != null) {
                        getSession().setAttribute("navTableSerie", detailBean.getSourceList());
                        if (detailBean.getSourceList()
                                .equals(getForm().getVersioniPrecedentiList().getName())) {
                            getForm().getVersioniPrecedentiDetailList()
                                    .setTable(detailBean.getSourceTable());
                        } else {
                            ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) getForm()
                                    .getComponent(detailBean.getSourceList()))
                                    .setTable(detailBean.getSourceTable());
                        }
                        level = detailBean.getLevel();
                    }
                } else {
                    idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
                    level = getForm().getSerieDetail().getLevel_ver_serie().parse() != null
                            ? getForm().getSerieDetail().getLevel_ver_serie().parse().intValue()
                            : 1;
                }
                boolean versioneCorrente = serieEjb.checkVersione(idVerSerie, (String[]) null);

                String primaryTab = getForm().getSerieDetailTabs().getCurrentTab().getName();
                String subTab = getForm().getSerieDetailSubTabs().getCurrentTab().getName();

                loadDettaglioSerie(idVerSerie, versioneCorrente);
                if (versioneCorrente && idVerSerie != null) {
                    getForm().getSerieDetail().getId_ver_serie_corr()
                            .setValue(idVerSerie.toPlainString());
                }
                getForm().getSerieDetail().getLevel_ver_serie().setValue(String.valueOf(level));
                getForm().getSerieDetailTabs()
                        .setCurrentTab(getForm().getSerieDetailTabs().getComponent(primaryTab));
                getForm().getSerieDetailSubTabs()
                        .setCurrentTab(getForm().getSerieDetailSubTabs().getComponent(subTab));
            } else if (publisher.equals(Application.Publisher.RICERCA_SERIE_UD)) {
                resetIdVerSerieDetailStack();
                RicercaSerieBean filtri = new RicercaSerieBean(getForm().getFiltriRicercaSerie());
                if (filtri.getAa_serie_a() == null) {
                    int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
                    BigDecimal anno = new BigDecimal(annoCorrente - 1);
                    filtri.setAa_serie_a(anno);
                }
                if (!getMessageBox().hasError()) {
                    SerVRicSerieUdTableBean table = serieEjb
                            .getSerVRicSerieUdTableBean(getUser().getIdUtente(), filtri);
                    getForm().getSerieList().setTable(table);
                    getForm().getSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getSerieList().getTable().first();

                }
                getForm().getSerieList().setHideUpdateButton(false);
                getForm().getSerieList().setHideDeleteButton(false);
            } else if (publisher.equals(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL)) {
                BigDecimal idContenutoVerSerie = getForm().getContenutoSerieDetail()
                        .getId_contenuto_ver_serie().parse();
                String tipoContenuto = getForm().getContenutoSerieDetail()
                        .getTi_contenuto_ver_serie().parse();

                String primaryTab = getForm().getContenutoSerieDetailTabs().getCurrentTab()
                        .getName();
                String subTab = getForm().getContenutoSerieDetailSubTabs().getCurrentTab()
                        .getName();
                loadDettaglioContenuto(idContenutoVerSerie, tipoContenuto, false);

                getForm().getContenutoSerieDetailTabs().setCurrentTab(
                        getForm().getContenutoSerieDetailTabs().getComponent(primaryTab));
                getForm().getContenutoSerieDetailSubTabs().setCurrentTab(
                        getForm().getContenutoSerieDetailSubTabs().getComponent(subTab));
            } else if (publisher.equals(Application.Publisher.CONSISTENZA_ATTESA_DETAIL)) {
                BigDecimal idConsist = getForm().getDatiSerieConsistenzaAttesaDetail()
                        .getId_consist_ver_serie().parse();
                SerLacunaConsistVerSerieTableBean lacuneTable = serieEjb
                        .getSerLacunaConsistVerSerieTableBean(idConsist);
                if (getForm().getLacuneList().getTable() != null) {
                    int pageSize = getForm().getLacuneList().getTable().getPageSize();
                    getForm().getLacuneList().setTable(lacuneTable);
                    getForm().getLacuneList().getTable().setPageSize(pageSize);
                    getForm().getLacuneList().getTable().first();
                } else {
                    getForm().getLacuneList().setTable(lacuneTable);
                    getForm().getLacuneList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getLacuneList().getTable().first();
                }
            } else if (publisher.equals(Application.Publisher.COMUNICAZIONE_CONSISTENZA_SERIE_UD)) {
                RicercaSerieBean filtri = new RicercaSerieBean(
                        getForm().getFiltriComunicazioneConsistenzaSerieUD());
                SerVRicConsistSerieUdTableBean table = serieEjb
                        .getSerVRicConsistSerieUdTableBean(filtri);

                int rowIndex;
                int pageSize;
                if (getForm().getConsistenzaSerieList().getTable() != null) {
                    rowIndex = getForm().getConsistenzaSerieList().getTable().getCurrentRowIndex();
                    pageSize = getForm().getConsistenzaSerieList().getTable().getPageSize();
                } else {
                    rowIndex = 0;
                    pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                }
                getForm().getConsistenzaSerieList().setTable(table);
                getForm().getConsistenzaSerieList().getTable().setPageSize(pageSize);
                getForm().getConsistenzaSerieList().getTable().setCurrentRowIndex(rowIndex);
            }
        } catch (EMFError e) {
            log.error("Errore nel ricaricamento del dettaglio serie", e);
            getMessageBox().addError("Errore nel ricaricamento del dettaglio serie");
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.SERIE_UD;
    }

    @Override
    public JSONObject triggerCreazioneSerieNm_tipo_serieOnTrigger() throws EMFError {
        getForm().getCreazioneSerie().post(getRequest());
        BigDecimal idTipoSerie = getForm().getCreazioneSerie().getNm_tipo_serie().parse();
        try {
            if (idTipoSerie != null) {
                DecTipoSerieRowBean row = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
                if (row.getNiAnniConserv() != null
                        && row.getNiAnniConserv().equals(new BigDecimal(9999))) {
                    getForm().getCreazioneSerie().getNi_anni_conserv().setValue(null);
                    getForm().getCreazioneSerie().getConserv_unlimited().setValue("1");
                } else if (row.getNiAnniConserv() != null) {
                    getForm().getCreazioneSerie().getNi_anni_conserv()
                            .setValue(row.getNiAnniConserv().toString());
                    getForm().getCreazioneSerie().getConserv_unlimited().setValue("0");
                } else {
                    // Non sono definiti gli anni di conservazione, vado a prendere il dato dai
                    // registri legati al tipo
                    // serie
                    BigDecimal niAnniConserv = registroEjb.getMaxAnniConserv(idTipoSerie);
                    if (niAnniConserv != null) {
                        getForm().getCreazioneSerie().getNi_anni_conserv()
                                .setValue(niAnniConserv.toPlainString());
                        getForm().getCreazioneSerie().getConserv_unlimited()
                                .setValue(niAnniConserv.equals(new BigDecimal(9999)) ? "1" : "0");
                    } else {
                        getForm().getCreazioneSerie().getNi_anni_conserv().setValue(null);
                        getForm().getCreazioneSerie().getConserv_unlimited().setValue(null);
                    }
                }

                if (row.getTiSelUd().equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
                    getForm().getCreazioneSerie().getDisable_dates().setValue("0");
                } else {
                    getForm().getCreazioneSerie().getDisable_dates().setValue("1");
                }

                getForm().getCreazioneSerie().getCd_serie().setValue(row.getCdSerieDefault());
                getForm().getCreazioneSerie().getDs_serie().setValue(row.getDsSerieDefault());
            } else {
                getForm().getCreazioneSerie().getCd_serie().setValue(null);
                getForm().getCreazioneSerie().getDs_serie().setValue(null);
                getForm().getCreazioneSerie().getNi_anni_conserv().setValue(null);
                getForm().getCreazioneSerie().getConserv_unlimited().setValue(null);
            }
        } catch (Exception ex) {
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
        }

        return getForm().getCreazioneSerie().asJSON();
    }

    @Override
    public JSONObject triggerCreazioneSerieConserv_unlimitedOnTrigger() throws EMFError {
        getForm().getCreazioneSerie().post(getRequest());
        return ActionUtils.getConservUnlimitedTrigger(getForm().getCreazioneSerie());
    }

    @Override
    public JSONObject triggerSerieDetailConserv_unlimitedOnTrigger() throws EMFError {
        getForm().getSerieDetail().post(getRequest());
        return ActionUtils.getConservUnlimitedTrigger(getForm().getSerieDetail());
    }

    @Secure(action = "button/SerieUDForm#SerieDetailButtonList/populateSeriePadre")
    public void populateSeriePadre() throws EMFError {
        getForm().getCreazioneSerie().post(getRequest());
        BigDecimal idTipoSerie = getForm().getCreazioneSerie().getNm_tipo_serie().parse();
        BigDecimal aaSerie = getForm().getCreazioneSerie().getAa_serie().parse();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(getForm().getCreazioneSerie().getCd_serie_padre().getName(),
                    JSONObject.NULL);
            jsonObject.put(getForm().getCreazioneSerie().getDs_serie_padre().getName(),
                    JSONObject.NULL);
            jsonObject.put(getForm().getCreazioneSerie().getCd_serie_padre_da_creare().getName(),
                    JSONObject.NULL);
            jsonObject.put(getForm().getCreazioneSerie().getDs_serie_padre_da_creare().getName(),
                    JSONObject.NULL);
            jsonObject.put(getForm().getCreazioneSerie().getNi_anni_conserv().getName(),
                    JSONObject.NULL);
            jsonObject.put(getForm().getCreazioneSerie().getConserv_unlimited().getName(),
                    JSONObject.NULL);
            jsonObject.put(getForm().getCreazioneSerie().getDs_lista_anni_sel_serie().getName(),
                    JSONObject.NULL);
            if (idTipoSerie != null && aaSerie != null) {
                DecTipoSerieRowBean row = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
                boolean setAnni = false;
                if (row.getIdTipoSeriePadre() != null) {
                    SerSerieRowBean serieRow = serieEjb.getSerSerieRowBean(
                            row.getIdTipoSeriePadre(), getUser().getIdOrganizzazioneFoglia(),
                            aaSerie);
                    if (serieRow != null) {
                        jsonObject.put(getForm().getCreazioneSerie().getCd_serie_padre().getName(),
                                serieRow.getCdCompositoSerie());
                        jsonObject.put(getForm().getCreazioneSerie().getDs_serie_padre().getName(),
                                serieRow.getDsSerie());
                        if (serieRow.getNiAnniConserv() != null
                                && serieRow.getNiAnniConserv().equals(new BigDecimal(9999))) {
                            jsonObject.put(
                                    getForm().getCreazioneSerie().getNi_anni_conserv().getName(),
                                    JSONObject.NULL);
                            jsonObject.put(
                                    getForm().getCreazioneSerie().getConserv_unlimited().getName(),
                                    "1");
                        } else {
                            jsonObject.put(
                                    getForm().getCreazioneSerie().getNi_anni_conserv().getName(),
                                    serieRow.getNiAnniConserv().toString());
                            jsonObject.put(
                                    getForm().getCreazioneSerie().getConserv_unlimited().getName(),
                                    "0");
                        }
                    } else {
                        setAnni = true;
                    }
                } else {
                    setAnni = true;
                }
                if (setAnni) {
                    if (row.getNiAnniConserv() != null
                            && row.getNiAnniConserv().equals(new BigDecimal(9999))) {
                        jsonObject.put(getForm().getCreazioneSerie().getNi_anni_conserv().getName(),
                                JSONObject.NULL);
                        jsonObject.put(
                                getForm().getCreazioneSerie().getConserv_unlimited().getName(),
                                "1");
                    } else if (row.getNiAnniConserv() != null) {
                        jsonObject.put(getForm().getCreazioneSerie().getNi_anni_conserv().getName(),
                                row.getNiAnniConserv().toString());
                        jsonObject.put(
                                getForm().getCreazioneSerie().getConserv_unlimited().getName(),
                                "0");
                    } else {
                        // Non sono definiti gli anni di conservazione, vado a prendere il dato dai
                        // registri legati al
                        // tipo serie
                        BigDecimal niAnniConserv = registroEjb.getMaxAnniConserv(idTipoSerie);
                        if (niAnniConserv != null) {
                            jsonObject.put(
                                    getForm().getCreazioneSerie().getNi_anni_conserv().getName(),
                                    niAnniConserv.toPlainString());
                            jsonObject.put(
                                    getForm().getCreazioneSerie().getConserv_unlimited().getName(),
                                    niAnniConserv.equals(new BigDecimal(9999)) ? "1" : "0");
                        }
                    }
                    if (row.getIdTipoSeriePadre() != null) {
                        DecTipoSerieRowBean tipoSeriePadre = tipoSerieEjb
                                .getDecTipoSerieRowBean(row.getIdTipoSeriePadre());
                        jsonObject.put(getForm().getCreazioneSerie().getCd_serie_padre_da_creare()
                                .getName(), tipoSeriePadre.getCdSerieDefault());
                        jsonObject.put(getForm().getCreazioneSerie().getDs_serie_padre_da_creare()
                                .getName(), tipoSeriePadre.getDsSerieDefault());
                    }
                }
                if (row.getTiSelUd().equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
                    BigDecimal niAaSelUd = row.getNiAaSelUd();
                    BigDecimal niAaSelUdSuc = row.getNiAaSelUdSuc();
                    String dsLista = buildDsListaAnniSerie(niAaSelUd, niAaSelUdSuc, aaSerie);
                    jsonObject.put(
                            getForm().getCreazioneSerie().getDs_lista_anni_sel_serie().getName(),
                            dsLista);
                }
            }
        } catch (Exception ex) {
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
        }
        redirectToAjax(jsonObject);
    }

    private String buildDsListaAnniSerie(BigDecimal niAaSelUd, BigDecimal niAaSelUdSuc,
            BigDecimal aaSerie) {
        if (niAaSelUd == null) {
            niAaSelUd = BigDecimal.ZERO;
        }
        if (niAaSelUdSuc == null) {
            niAaSelUdSuc = BigDecimal.ZERO;
        }
        int fromIndex = aaSerie.subtract(niAaSelUd).intValue();
        int toIndex = aaSerie.add(niAaSelUdSuc).intValue();
        StringBuilder dsLista = new StringBuilder();
        for (int i = fromIndex; i < (toIndex + 1); i++) {
            dsLista.append(String.valueOf(i)).append(",");
        }
        dsLista.deleteCharAt(dsLista.length() - 1);
        return dsLista.toString();
    }

    @Secure(action = "Menu.Serie.RicercaSerie")
    public void loadRicercaSerie() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.RicercaSerie");

        getSession().removeAttribute("navTableSerie");
        resetIdVerSerieDetailStack();
        getForm().getFiltriRicercaSerie().reset();
        getForm().getFiltriRicercaSerie().setEditMode();

        initFiltriStrut(getForm().getFiltriRicercaSerie().getName());

        DecodeMap tipoSerie = DecodeMap.Factory.newInstance(
                tipoSerieEjb.getDecTipoSerieDaCreareTableBean(getUser().getIdOrganizzazioneFoglia(),
                        CostantiDB.TipoContenSerie.UNITA_DOC.name(), false),
                "id_tipo_serie", getForm().getFiltriRicercaSerie().getNm_tipo_serie().getName());
        getForm().getFiltriRicercaSerie().getNm_tipo_serie().setDecodeMap(tipoSerie);
        getForm().getFiltriRicercaSerie().getTi_stato_cor_serie().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("stato_serie", CostantiDB.StatoVersioneSerie.values()));
        getForm().getFiltriRicercaSerie().getTi_stato_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
                        CostantiDB.StatoConservazioneSerie.values()));
        int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
        BigDecimal anno = new BigDecimal(annoCorrente);
        getForm().getFiltriRicercaSerie().getAa_serie_a().setValue(anno.toPlainString());
        DecodeMapIF siNoDecodeMap = ComboGetter.getMappaGenericFlagSiNo();
        getForm().getFiltriRicercaSerie().getFl_da_rigenera().setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_presenza_consist_attesa()
                .setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_elab_bloccata().setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_err_contenuto_acq().setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_err_contenuto_calc().setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_err_contenuto_eff().setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_err_contenuto_file().setDecodeMap(siNoDecodeMap);
        getForm().getFiltriRicercaSerie().getFl_err_validazione().setDecodeMap(siNoDecodeMap);

        DecodeMapIF statiContenutoDecodeMap = ComboGetter.getMappaSortedGenericEnum(
                "ti_stato_contenuto", CostantiDB.StatoContenutoVerSerie.values());
        getForm().getFiltriRicercaSerie().getTi_stato_contenuto_calc()
                .setDecodeMap(statiContenutoDecodeMap);
        getForm().getFiltriRicercaSerie().getTi_stato_contenuto_acq()
                .setDecodeMap(statiContenutoDecodeMap);
        getForm().getFiltriRicercaSerie().getTi_stato_contenuto_eff()
                .setDecodeMap(statiContenutoDecodeMap);
        getForm().getFiltriRicercaSerie().getTi_crea_standard()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_crea_standard",
                        CostantiDB.TipoSerieCreaStandard.values()));

        getForm().getSerieList().setTable(null);

        forwardToPublisher(Application.Publisher.RICERCA_SERIE_UD);
    }

    @Override
    public void ricercaSerie() throws EMFError {
        if (getForm().getFiltriRicercaSerie().postAndValidate(getRequest(), getMessageBox())) {
            RicercaSerieBean filtri = new RicercaSerieBean(getForm().getFiltriRicercaSerie());
            if (filtri.getAa_serie_a() == null) {
                int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
                BigDecimal anno = new BigDecimal(annoCorrente - 1);
                filtri.setAa_serie_a(anno);

                getForm().getFiltriRicercaSerie().getAa_serie_a().setValue(anno.toPlainString());
            }
            TypeValidator validator = new TypeValidator(getMessageBox());
            validator.validaOrdineDateOrari(filtri.getDt_inizio_serie(), filtri.getDt_fine_serie(),
                    getForm().getFiltriRicercaSerie().getDt_inizio_serie().getName(),
                    getForm().getFiltriRicercaSerie().getDt_fine_serie().getName());
            if (!getMessageBox().hasError()) {
                SerVRicSerieUdTableBean table = serieEjb
                        .getSerVRicSerieUdTableBean(getUser().getIdUtente(), filtri);
                getForm().getSerieList().setTable(table);
                getForm().getSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getSerieList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_SERIE_UD);
    }

    @Override
    public void deleteSerieList() throws EMFError {
        SerVRicSerieUdRowBean row = (SerVRicSerieUdRowBean) getForm().getSerieList().getTable()
                .getCurrentRow();
        int riga = getForm().getSerieList().getTable().getCurrentRowIndex();
        try {
            serieEjb.deleteSerVerSerie(getUser().getIdUtente(), row.getIdSerie(),
                    row.getIdVerSerie());

            getForm().getSerieList().getTable().remove(riga);
            getMessageBox().addInfo("Serie eliminata con successo");
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.SERIE_UD_DETAIL)) {
            goBack();
        } else {
            getForm().getSerieList().setHideUpdateButton(false);
            getForm().getSerieList().setHideDeleteButton(false);

            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateSerieList() throws EMFError {
        SerVRicSerieUdRowBean row = (SerVRicSerieUdRowBean) getForm().getSerieList().getTable()
                .getCurrentRow();
        BigDecimal idVerSerie = row.getIdVerSerie();

        if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
            DecTipoSerieRowBean tipoSerieRow = tipoSerieEjb
                    .getDecTipoSerieRowBean(row.getIdTipoSerie());
            getForm().getSerieList().setStatus(Status.update);
            getForm().getSerieDetailTabs()
                    .setCurrentTab(getForm().getSerieDetailTabs().getInfoPrincipali());

            getForm().getSerieDetail().getCd_composito_serie().setEditMode();
            getForm().getSerieDetail().getDs_serie().setEditMode();
            getForm().getSerieDetail().getNi_anni_conserv().setEditMode();
            getForm().getSerieDetail().getConserv_unlimited().setEditMode();

            BigDecimal aaSerie = getForm().getSerieDetail().getAa_serie().parse();
            Date dtInizioSelSerie = getForm().getSerieDetail().getDt_inizio_sel_serie().parse();
            Date dtFineSelSerie = getForm().getSerieDetail().getDt_fine_sel_serie().parse();
            String dsListaAnniSelSerie = getForm().getSerieDetail().getDs_lista_anni_sel_serie()
                    .parse();
            if (tipoSerieRow.getTiSelUd()
                    .equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
                // Le date devono essere modificabili se sono nulle o se non rispettano gli
                // intervalli richiesti dal
                // tipo
                boolean editDates = false;
                if (dtInizioSelSerie == null && dtFineSelSerie == null
                        && StringUtils.isBlank(dsListaAnniSelSerie)) {
                    editDates = true;
                } else {
                    SerieAutomBean creaAutomBean = null;
                    try {
                        creaAutomBean = serieEjb.generateIntervalliSerieAutom(
                                tipoSerieRow.getNiMmCreaAutom(), tipoSerieRow.getCdSerieDefault(),
                                tipoSerieRow.getDsSerieDefault(), aaSerie.intValue(),
                                tipoSerieRow.getTiSelUd());
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                    if (!getMessageBox().hasError() && creaAutomBean != null) {
                        Calendar calInizio = Calendar.getInstance();
                        calInizio.setTime(dtInizioSelSerie);
                        calInizio.set(Calendar.HOUR_OF_DAY, 0);
                        calInizio.set(Calendar.MINUTE, 0);
                        calInizio.set(Calendar.SECOND, 0);
                        calInizio.set(Calendar.MILLISECOND, 0);
                        Calendar calFine = Calendar.getInstance();
                        calFine.setTime(dtFineSelSerie);
                        calFine.set(Calendar.HOUR_OF_DAY, 23);
                        calFine.set(Calendar.MINUTE, 59);
                        calFine.set(Calendar.SECOND, 59);
                        calFine.set(Calendar.MILLISECOND, 999);

                        IntervalliSerieAutomBean tmpBean = new IntervalliSerieAutomBean(
                                calInizio.getTime(), calFine.getTime(), null, null);
                        if (!creaAutomBean.getIntervalli().contains(tmpBean)) {
                            editDates = true;
                        }
                    }
                }
                if (editDates) {
                    getForm().getSerieDetail().getDt_inizio_sel_serie().setEditMode();
                    getForm().getSerieDetail().getDt_fine_sel_serie().setEditMode();
                    getForm().getSerieDetail().getDs_lista_anni_sel_serie().setEditMode();
                    if (StringUtils.isBlank(dsListaAnniSelSerie)) {
                        // Inizializza il campo con i valori di default
                        BigDecimal niAaSelUd = tipoSerieRow.getNiAaSelUd();
                        BigDecimal niAaSelUdSuc = tipoSerieRow.getNiAaSelUdSuc();
                        String dsLista = buildDsListaAnniSerie(niAaSelUd, niAaSelUdSuc, aaSerie);
                        getForm().getSerieDetail().getDs_lista_anni_sel_serie().setValue(dsLista);
                    }
                }
            } else if (tipoSerieRow.getTiSelUd()
                    .equals(CostantiDB.TipoSelUdTipiSerie.ANNO_KEY.name())) {
                if (dtInizioSelSerie != null || dtFineSelSerie != null
                        || StringUtils.isNotBlank(dsListaAnniSelSerie)) {
                    getForm().getSerieDetail().getDt_inizio_sel_serie().setValue(null);
                    getForm().getSerieDetail().getDt_fine_sel_serie().setValue(null);
                    getForm().getSerieDetail().getDs_lista_anni_sel_serie().setValue(null);
                }
            }

            getForm().getSerieDetailButtonList().setViewMode();

            forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
        } else {
            getForm().getSerieList().setHideUpdateButton(false);
            getForm().getSerieList().setHideDeleteButton(false);

            getMessageBox().addError(
                    "Non \u00E8 possibile modificare una serie con stato diverso da APERTA o DA_CONTROLLARE");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void tabInfoPrincipaliOnClick() throws EMFError {
        getForm().getSerieDetailTabs()
                .setCurrentTab(getForm().getSerieDetailTabs().getInfoPrincipali());
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void tabIndiceAIPOnClick() throws EMFError {
        getForm().getSerieDetailTabs().setCurrentTab(getForm().getSerieDetailTabs().getIndiceAIP());
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void tabListaNoteOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs()
                .setCurrentTab(getForm().getSerieDetailSubTabs().getListaNote());
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void tabListaVolumiOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs()
                .setCurrentTab(getForm().getSerieDetailSubTabs().getListaVolumi());
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void tabListaStatiOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs()
                .setCurrentTab(getForm().getSerieDetailSubTabs().getListaStati());
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void tabListaVersioniPrecedentiOnClick() throws EMFError {
        getForm().getSerieDetailSubTabs()
                .setCurrentTab(getForm().getSerieDetailSubTabs().getListaVersioniPrecedenti());
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void calcolaContenuto() throws EMFError {
        getRequest().setAttribute("confermaCalcoloBox", true);
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    public void confermaCalcoloContenuto() throws EMFError {
        log.info("Inviata richiesta di calcolo contenuto");
        final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
        final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        try {
            serieEjb.checkStatoSerie(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name());
            serieEjb.checkStatoContenutoSerie(idVerSerie, true, false, false,
                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                    CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());

            calcolaOAcquisisciContenutoSerie(idSerie, idVerSerie, false, false, null, null, null,
                    null, null);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    private void calcolaOAcquisisciContenutoSerie(final BigDecimal idSerie,
            final BigDecimal idVerSerie, boolean acquisizione, boolean ricreaFileInput,
            String cdDocFileInputVerSerie, String dsDocFileInputVerSerie, String flFornitoEnte,
            byte[] file, Long idUser) throws EMFError, ParerUserError {
        if (!getMessageBox().hasError()) {
            log.info("Controlli eseguiti con successo, proseguo...");
            String op = acquisizione ? "Acquisizione" : "Calcolo";
            String tiCreazione = acquisizione
                    ? CostantiDB.TipoCreazioneSerie.ACQUISIZIONE_FILE.name()
                    : CostantiDB.TipoCreazioneSerie.CALCOLO_AUTOMATICO.name();
            if (ricreaFileInput) {
                serieEjb.cleanContenutoSerie(idSerie, idVerSerie, cdDocFileInputVerSerie,
                        dsDocFileInputVerSerie, flFornitoEnte, file, idUser);
            } else {
                serieEjb.cleanContenutoSerie(idSerie, idVerSerie,
                        acquisizione ? CostantiDB.TipoContenutoVerSerie.ACQUISITO.name()
                                : CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
            }

            String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
            BigDecimal aaSerie = getForm().getSerieDetail().getAa_serie().parse();
            String keyFuture = FutureUtils.buildKeyFuture(tiCreazione, cdSerie, aaSerie,
                    getUser().getIdOrganizzazioneFoglia(), idVerSerie.longValue());
            Future<String> future = serieEjb.callCreazioneSerieAsync(getUser().getIdUtente(),
                    idVerSerie.longValue(), acquisizione);
            FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture, future);

            getMessageBox().addInfo(op + " della serie lanciato con successo");
            loadDettaglioSerie(idVerSerie, true);
        }
    }

    @Override
    public void riavviaCalcoloContenuto() throws EMFError {
        log.info("Inviata richiesta di riavvio calcolo contenuto");
        final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
        final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        final BigDecimal idContenutoCalc = getForm().getSerieDetail().getId_contenuto_calc()
                .parse();
        String[] blocco = serieEjb.getFlContenutoBloccato(JobConstants.JobEnum.CALCOLO_SERIE.name(),
                idContenutoCalc);
        if (blocco != null) {
            if (!blocco[0].equals("1")) {
                getMessageBox().addError("Il calcolo del contenuto non \u00E8 bloccato");
            }
            if (!getMessageBox().hasError()) {
                try {
                    calcolaOAcquisisciContenutoSerie(idSerie, idVerSerie, false, false, null, null,
                            null, null, null);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        } else {
            getMessageBox().addError("Il calcolo del contenuto non \u00E8 bloccato");
        }
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void acquisisciContenutoUD() throws EMFError {
        int size10Mb = 10 * WebConstants.FILESIZE * WebConstants.FILESIZE;
        try {
            getForm().getCreazioneSerie().postMultipart(getRequest(), size10Mb);

            if (!getMessageBox().hasError()) {
                if (getForm().getCreazioneSerie().getBl_file_input_serie().parse() == null) {
                    getMessageBox().addError("Nessun file selezionato");
                }
            }
            String flFornitoEnte = getForm().getCreazioneSerie().getFl_fornito_ente().parse();
            if (!getMessageBox().hasError()) {
                if (StringUtils.isBlank(flFornitoEnte)) {
                    getMessageBox().addError("<br/>Campo "
                            + getForm().getCreazioneSerie().getFl_fornito_ente().getDescription()
                            + " obbligatorio per il tipo creazione 'ACQUISIZIONE_FILE'");
                }
            }
            byte[] fileByteArray = null;
            if (!getMessageBox().hasError()) {
                BigDecimal idTipoSerie = getForm().getSerieDetail().getId_tipo_serie().parse();
                fileByteArray = getForm().getCreazioneSerie().getBl_file_input_serie()
                        .getFileBytes();
                String mime = singleton.detectMimeType(fileByteArray);
                if (!mime.equals(VerFormatiEnums.CSV_MIME)
                        && !mime.equals(VerFormatiEnums.TEXT_PLAIN_MIME)) {
                    getMessageBox().addError(
                            "Il formato del file caricato non corrisponde al tipo testo/csv");
                } else // Carico il file come CSV e verifico che esistano i campi definiti in
                       // decCampoInpUd
                if (!serieEjb.checkCsvHeaders(fileByteArray, idTipoSerie)) {
                    getMessageBox().addError(
                            "Il primo record del file non riporta i nomi dei campi del record");
                }
            }

            final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
            final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
            serieEjb.checkStatoSerie(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name());
            serieEjb.checkStatoContenutoSerie(idVerSerie, false, true, false,
                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                    CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name());

            String cdDoc = getForm().getCreazioneSerie().getCd_doc_file_input_ver_serie().parse();
            String dsDoc = getForm().getCreazioneSerie().getDs_doc_file_input_ver_serie().parse();
            calcolaOAcquisisciContenutoSerie(idSerie, idVerSerie, true, true, cdDoc, dsDoc,
                    flFornitoEnte, fileByteArray, getUser().getIdUtente());
        } catch (FileUploadException ex) {
            log.error("Eccezione nell'upload del file", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'upload del file", ex);
        } catch (ParerUserError pue) {
            getMessageBox().addError(pue.getDescription());
        } catch (Exception ex) {
            log.error("Eccezione generica nella acquisizione del contenuto della serie: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(
                    "Si \u00E8 verificata un'eccezione nella acquisizione del contenuto della serie");
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.ACQUISIZIONE_CONTENUTO_SERIE_UD);
        } else {
            goBack();
        }
    }

    @Override
    public void acquisisciContenuto() throws EMFError {
        log.info("Inviata richiesta di acquisizione contenuto");
        getForm().getCreazioneSerie().reset();
        getForm().getCreazioneSerie().setEditMode();
        getForm().getCreazioneSerie().getFl_fornito_ente()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        forwardToPublisher(Application.Publisher.ACQUISIZIONE_CONTENUTO_SERIE_UD);
    }

    @Override
    public void riavviaAcquisizioneContenuto() throws EMFError {
        log.info("Inviata richiesta di riavvio acquisizione contenuto");
        final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
        final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        final BigDecimal idContenutoAcq = getForm().getSerieDetail().getId_contenuto_acq().parse();
        String[] blocco = serieEjb.getFlContenutoBloccato(JobConstants.JobEnum.INPUT_SERIE.name(),
                idContenutoAcq);
        if (blocco != null) {
            if (!blocco[0].equals("1")) {
                getMessageBox().addError("L'acquisizione del contenuto non \u00E8 bloccata");
            }
            if (!getMessageBox().hasError()) {
                try {
                    calcolaOAcquisisciContenutoSerie(idSerie, idVerSerie, true, false, null, null,
                            null, null, null);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        } else {
            getMessageBox().addError("L'acquisizione del contenuto non \u00E8 bloccata");
        }

        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void generaContenutoEffettivo() throws EMFError {
        getForm().getSerieDetail().getTi_contenuto_generazione()
                .setDecodeMap(ComboGetter.getMappaOrdinalGenericEnum("tiGenerazioneContenuto",
                        CostantiDB.TipoContenutoPerEffettivo.values()));
        getForm().getSerieDetail().getTi_contenuto_generazione().setEditMode();
        getForm().getContenutoEffettivoButtonList().setEditMode();
        boolean calc = false;
        boolean acq = false;
        if (StringUtils.isNotBlank(getForm().getSerieDetail().getId_contenuto_calc().getValue())) {
            calc = true;
        }
        if (StringUtils.isNotBlank(getForm().getSerieDetail().getId_contenuto_acq().getValue())) {
            acq = true;
        }
        if (calc && acq) {
            getForm().getSerieDetail().getTi_contenuto_generazione()
                    .setValue(CostantiDB.TipoContenutoPerEffettivo.ENTRAMBI.name());
        } else if (calc) {
            getForm().getSerieDetail().getTi_contenuto_generazione()
                    .setValue(CostantiDB.TipoContenutoPerEffettivo.CALCOLATO.name());
        } else if (acq) {
            getForm().getSerieDetail().getTi_contenuto_generazione()
                    .setValue(CostantiDB.TipoContenutoPerEffettivo.ACQUISITO.name());
        } else {
            getMessageBox().addError(
                    "Eccezione inaspettata nell'inizializzazione dei dati per la generazione");
        }
        if (!getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.GENERA_CONTENUTO_EFFETTIVO);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void confermaGenerazioneEffettivo() throws EMFError {
        getForm().getSerieDetail().getTi_contenuto_generazione().post(getRequest());
        String tipo = getForm().getSerieDetail().getTi_contenuto_generazione().parse();

        boolean calc = false;
        boolean acq = false;
        BigDecimal niCalc = BigDecimal.ZERO;
        BigDecimal niAcq = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(getForm().getSerieDetail().getId_contenuto_calc().getValue())) {
            calc = true;
            niCalc = getForm().getSerieDetail().getNi_unita_doc_calc().parse();
        }
        if (StringUtils.isNotBlank(getForm().getSerieDetail().getId_contenuto_acq().getValue())) {
            acq = true;
            niAcq = getForm().getSerieDetail().getNi_unita_doc_acq().parse();
        }

        if (!getMessageBox().hasError()) {
            try {
                if (StringUtils.isNotBlank(tipo)) {
                    final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie()
                            .parse();
                    final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
                    boolean checkCalc = false;
                    boolean checkAcq = false;

                    CostantiDB.TipoContenutoPerEffettivo tipoCont = CostantiDB.TipoContenutoPerEffettivo
                            .valueOf(tipo);
                    switch (tipoCont) {
                    case CALCOLATO:
                        checkCalc = true;
                        break;
                    case ACQUISITO:
                        checkAcq = true;
                        break;
                    case ENTRAMBI:
                        checkCalc = checkAcq = true;
                        break;
                    default:
                        throw new AssertionError(tipoCont.name());
                    }

                    if (checkCalc && checkAcq && (!calc || !acq)) {
                        String msg = "Selezionata la generazione del contenuto effettivo utilizzando entrambi i contenuti, ma il contenuto ";
                        if (!calc) {
                            msg += "CALCOLATO risulta non esistente";
                        } else if (!acq) {
                            msg += "ACQUISITO risulta non esistente";
                        } else {
                            msg = "Eccezione inaspettata nell'inizializzazione dei dati per la generazione";
                        }
                        getMessageBox().addError(msg);
                    } else if (checkCalc && !calc) {
                        getMessageBox().addError(
                                "Selezionata la generazione del contenuto effettivo utilizzando il contenuto CALCOLATO che risulta non esistente");
                    } else if (checkAcq && !acq) {
                        getMessageBox().addError(
                                "Selezionata la generazione del contenuto effettivo utilizzando il contenuto ACQUISITO che risulta non esistente");
                    }
                    if (!getMessageBox().hasError()) {
                        if (checkCalc && checkAcq && niCalc.intValue() == 0
                                && niAcq.intValue() == 0) {
                            getMessageBox().addError(
                                    "Selezionata la generazione del contenuto effettivo utilizzando entrambi i contenuti, ma i contenuti non contengono unit\u00E0 documentarie");
                        } else if (checkCalc && niCalc.intValue() == 0) {
                            getMessageBox().addError(
                                    "Selezionata la generazione del contenuto effettivo utilizzando il contenuto CALCOLATO che non contiene unit\u00E0 documentarie");
                        } else if (checkAcq && niAcq.intValue() == 0) {
                            getMessageBox().addError(
                                    "Selezionata la generazione del contenuto effettivo utilizzando il contenuto ACQUISITO che non contiene unit\u00E0 documentarie");
                        }
                    }
                    if (!getMessageBox().hasError()) {
                        serieEjb.checkStatoSerie(idVerSerie,
                                CostantiDB.StatoVersioneSerie.APERTA.name());
                        serieEjb.checkStatoContenutoSerie(idVerSerie, checkCalc, checkAcq, false,
                                CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name());

                        serieEjb.cleanContenutoSerie(idSerie, idVerSerie,
                                CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());

                        String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
                        BigDecimal aaSerie = getForm().getSerieDetail().getAa_serie().parse();
                        String keyFuture = FutureUtils.buildKeyFuture(
                                CostantiDB.TipoChiamataAsync.GENERAZIONE_EFFETTIVO.name(), cdSerie,
                                aaSerie, getUser().getIdOrganizzazioneFoglia(),
                                idVerSerie.longValue());
                        Future<String> future = serieEjb.callGenerazioneEffettivoAsync(
                                getUser().getIdUtente(), idVerSerie.longValue(), checkCalc,
                                checkAcq);
                        FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture,
                                future);

                        getMessageBox().addInfo(
                                "Generazione del contenuto effettivo della serie lanciata con successo");
                    }
                } else {
                    getMessageBox().addError(
                            "\u00C8 necessario scegliere quale contenuto utilizzare per generare il contenuto effettivo; \u00E0 anche possibile scegliere entrambi i contenuti");
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError()) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void visualizzaContenutoCalcolato() throws EMFError {
        final BigDecimal idContenutoVerSerie = getForm().getSerieDetail().getId_contenuto_calc()
                .parse();
        getForm().getFiltriContenutoSerieDetail().clear();
        getForm().getFiltriContenutoSerieDetail().getTi_stato_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
                        CostantiDB.StatoConservazioneUnitaDoc.values()));
        loadDettaglioContenuto(idContenutoVerSerie,
                CostantiDB.TipoContenutoVerSerie.CALCOLATO.name(), true);
    }

    @Override
    public void visualizzaContenutoAcquisito() throws EMFError {
        final BigDecimal idContenutoVerSerie = getForm().getSerieDetail().getId_contenuto_acq()
                .parse();
        getForm().getFiltriContenutoSerieDetail().clear();
        getForm().getFiltriContenutoSerieDetail().getTi_stato_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
                        CostantiDB.StatoConservazioneUnitaDoc.values()));
        loadDettaglioContenuto(idContenutoVerSerie,
                CostantiDB.TipoContenutoVerSerie.ACQUISITO.name(), true);
    }

    @Override
    public void visualizzaContenutoEffettivo() throws EMFError {
        final BigDecimal idContenutoVerSerie = getForm().getSerieDetail().getId_contenuto_eff()
                .parse();
        getForm().getFiltriContenutoSerieDetail().clear();
        getForm().getFiltriContenutoSerieDetail().getTi_stato_conservazione()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
                        CostantiDB.StatoConservazioneUnitaDoc.values()));
        loadDettaglioContenuto(idContenutoVerSerie,
                CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name(), true);
    }

    public void loadDettaglioContenuto(BigDecimal idContenutoVerSerie, String tipoContenuto,
            boolean doForward) throws EMFError {
        SerVVisContenutoSerieUdRowBean detailRow = serieEjb
                .getSerVVisContenutoSerieUdRowBean(idContenutoVerSerie, tipoContenuto);
        getForm().getContenutoSerieDetail().getFl_presenza_lacune()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getContenutoSerieDetail().getFl_job_bloccato()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getContenutoSerieDetail().getFl_err_contenuto()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getContenutoSerieDetail().getFl_err_contenuto_file()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        if (detailRow != null) {
            getForm().getFiltriContenutoSerieDetail().setEditMode();
            getForm().getContenutoButtonList().setEditMode();
            getForm().getContenutoSerieDetail().copyFromBean(detailRow);

            log.info("Carico le liste contenute nei vari tab di dettaglio");
            RicercaUdAppartBean parametri = initParametriRicercaContenuto(detailRow.getAaSerie());
            SerVLisUdAppartSerieTableBean udTb = serieEjb
                    .getSerVLisUdAppartSerieTableBean(idContenutoVerSerie, parametri);
            if (getForm().getUdList().getTable() != null) {
                // Ricarico le liste con paginazione
                int paginaCorrenteUd = getForm().getUdList().getTable().getCurrentPageIndex();
                int inizioUd = getForm().getUdList().getTable().getFirstRowPageIndex();
                int pageSizeUd = getForm().getUdList().getTable().getPageSize();

                getForm().addComponent(new SerieUDForm.UdList());
                getForm().getUdList().setTable(udTb);
                getForm().getUdList().getTable().first();
                getForm().getUdList().getTable().setPageSize(pageSizeUd);
                this.lazyLoadGoPage(getForm().getUdList(), paginaCorrenteUd);
                // Ritorno alla pagina
                getForm().getUdList().getTable().setCurrentRowIndex(inizioUd);
            } else {
                getForm().addComponent(new SerieUDForm.UdList());

                getForm().getUdList().setTable(udTb);
                getForm().getUdList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getUdList().getTable().first();
            }

            getForm().getUdList().setHideDeleteButton(true);
            getForm().getUdList().setHideInsertButton(true);

            SerVLisErrContenSerieUdTableBean errContenTb = serieEjb
                    .getSerVLisErrContenSerieUdTableBean(idContenutoVerSerie);
            if (getForm().getErroriContenutiList().getTable() != null) {
                int pageSizeErrCon = getForm().getErroriContenutiList().getTable().getPageSize();
                getForm().getErroriContenutiList().setTable(errContenTb);
                getForm().getErroriContenutiList().getTable().setPageSize(pageSizeErrCon);
                getForm().getErroriContenutiList().getTable().first();
            } else {
                getForm().getErroriContenutiList().setTable(errContenTb);
                getForm().getErroriContenutiList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getErroriContenutiList().getTable().first();
            }

            getForm().getContenutoSerieDetailTabs()
                    .setCurrentTab(getForm().getContenutoSerieDetailTabs().getDettaglioContenuto());
            getForm().getContenutoSerieDetailSubTabs().setCurrentTab(
                    getForm().getContenutoSerieDetailSubTabs().getListaUnitaDocumentarie());
            getForm().getContenutoSerieDetailSubTabs().getListaErroriFileInput().setHidden(true);
            getForm().getContenutoSerieDetail().getFl_err_contenuto_file().setHidden(true);
            getForm().getContenutoSerieDetail().getNi_vol_ver_serie().setHidden(false);
            getForm().getContenutoButtonList().getDownloadFile().setHidden(true);
            getForm().getContenutoButtonList().getDownloadErroriFileInput().setHidden(true);
            getForm().getContenutoButtonList().getDownloadQuery().setHidden(true);
            getForm().getContenutoButtonList().getControllaContenuto().setHidden(true);
            getForm().getContenutoButtonList().getRiavviaControlloContenuto().setHidden(true);

            if (StringUtils.isNotBlank(detailRow.getTiStatoContenutoVerSerie())
                    && detailRow.getTiStatoContenutoVerSerie().equals(
                            CostantiDB.StatoContenutoVerSerie.CONTROLLO_CONSIST_IN_CORSO.name())) {
                getForm().getContenutoSerieDetail().getFl_job_bloccato().setHidden(false);
                getForm().getContenutoSerieDetail().getDl_msg_job_bloccato().setHidden(false);
                String[] blocco = serieEjb.getFlContenutoBloccato(
                        JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(),
                        detailRow.getIdContenutoVerSerie());
                if (blocco != null) {
                    getForm().getContenutoSerieDetail().getFl_job_bloccato().setValue(blocco[0]);
                    getForm().getContenutoSerieDetail().getDl_msg_job_bloccato()
                            .setValue(blocco[1]);
                    if (StringUtils.isNotBlank(blocco[0]) && blocco[0].equals("1")) {
                        getForm().getContenutoButtonList().getRiavviaControlloContenuto()
                                .setHidden(false);
                    }
                }
            } else {
                getForm().getContenutoSerieDetail().getFl_job_bloccato().setHidden(true);
                getForm().getContenutoSerieDetail().getDl_msg_job_bloccato().setHidden(true);
            }

            String descColonna1 = getForm().getUdList().getFl_presente_contenuto_1()
                    .getDescription();
            String descColonna2 = getForm().getUdList().getFl_presente_contenuto_2()
                    .getDescription();

            boolean contenEff = false;
            boolean contenCalc = false;
            boolean contenAcq = false;
            if (detailRow.getTiContenutoVerSerie()
                    .equals(CostantiDB.TipoContenutoVerSerie.ACQUISITO.name())) {
                getForm().getContenutoSerieDetail().getFl_err_contenuto_file().setHidden(false);
                getForm().getContenutoButtonList().getDownloadFile().setHidden(false);
                getForm().getContenutoButtonList().getDownloadFile().setDisableHourGlass(true);
                getForm().getContenutoButtonList().getDownloadErroriFileInput().setHidden(false);
                getForm().getContenutoButtonList().getDownloadErroriFileInput()
                        .setDisableHourGlass(true);

                getForm().getUdList().getFl_presente_contenuto_1().setDescription(
                        descColonna1 + CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
                getForm().getUdList().getFl_presente_contenuto_2().setDescription(
                        descColonna2 + CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
                contenAcq = true;

                getForm().getContenutoSerieDetailSubTabs().getListaErroriFileInput()
                        .setHidden(false);
                SerVLisErrFileSerieUdTableBean errFileInputTb = serieEjb
                        .getSerVLisErrFileSerieUdTableBean(detailRow.getIdVerSerie(),
                                CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name());
                if (getForm().getErroriFileInputList().getTable() != null) {
                    int paginaCorrenteErrFile = getForm().getErroriFileInputList().getTable()
                            .getCurrentPageIndex();
                    int inizioErrFile = getForm().getErroriFileInputList().getTable()
                            .getFirstRowPageIndex();
                    int pageSizeErrFile = getForm().getErroriFileInputList().getTable()
                            .getPageSize();

                    getForm().getErroriFileInputList().setTable(errFileInputTb);
                    getForm().getErroriFileInputList().getTable().first();
                    getForm().getErroriFileInputList().getTable().setPageSize(pageSizeErrFile);
                    this.lazyLoadGoPage(getForm().getErroriFileInputList(), paginaCorrenteErrFile);
                    // Ritorno alla pagina
                    getForm().getErroriFileInputList().getTable().setCurrentRowIndex(inizioErrFile);
                } else {
                    getForm().getErroriFileInputList().setTable(errFileInputTb);
                    getForm().getErroriFileInputList().getTable()
                            .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getErroriFileInputList().getTable().first();
                }
            } else if (detailRow.getTiContenutoVerSerie()
                    .equals(CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())) {
                getForm().getContenutoSerieDetail().getNi_vol_ver_serie().setHidden(true);

                getForm().getUdList().getFl_presente_contenuto_1().setDescription(
                        descColonna1 + CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
                getForm().getUdList().getFl_presente_contenuto_2().setDescription(
                        descColonna2 + CostantiDB.TipoContenutoVerSerie.ACQUISITO.name());
                contenEff = true;
            } else {
                getForm().getUdList().getFl_presente_contenuto_1().setDescription(
                        descColonna1 + CostantiDB.TipoContenutoVerSerie.ACQUISITO.name());
                getForm().getUdList().getFl_presente_contenuto_2().setDescription(
                        descColonna2 + CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
                contenCalc = true;
            }

            if (serieEjb.checkVersione(detailRow.getIdVerSerie(),
                    CostantiDB.StatoVersioneSerie.APERTA.name(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
                if (serieEjb.checkContenuto(detailRow.getIdVerSerie(), contenCalc, contenAcq,
                        contenEff, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
                    getForm().getContenutoButtonList().getControllaContenuto().setHidden(false);
                }
            }

            if (contenCalc || contenAcq) {
                getForm().getContenutoButtonList().getDownloadQuery().setHidden(false);
                getForm().getContenutoButtonList().getDownloadQuery().setDisableHourGlass(true);
                if (serieEjb.checkVersione(detailRow.getIdVerSerie(),
                        CostantiDB.StatoVersioneSerie.APERTA.name())
                        && serieEjb.checkContenuto(detailRow.getIdVerSerie(), contenCalc, contenAcq,
                                contenEff, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name(),
                                CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
                    getForm().getUdList().setHideDeleteButton(false);
                    // il bottone di inserimento lo mostro solo se la serie fa parte della struttura
                    // in cui sono loggato
                    if (detailRow.getIdStrut()
                            .compareTo(getUser().getIdOrganizzazioneFoglia()) == 0) {
                        getForm().getUdList().setHideInsertButton(false);
                    }
                }
            } else if (serieEjb.checkVersione(detailRow.getIdVerSerie(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                    CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
                    && serieEjb.checkContenuto(detailRow.getIdVerSerie(), contenCalc, contenAcq,
                            contenEff, CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                            CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name(),
                            CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
                getForm().getUdList().setHideDeleteButton(false);
                // il bottone di inserimento lo mostro solo se la serie fa parte della struttura in
                // cui sono loggato
                if (detailRow.getIdStrut().compareTo(getUser().getIdOrganizzazioneFoglia()) == 0) {
                    getForm().getUdList().setHideInsertButton(false);
                }
            }
            if (!getMessageBox().hasError()
                    && StringUtils.isNotBlank(detailRow.getDsMsgSerieDaRigenera())) {
                getMessageBox().addWarning(detailRow.getDsMsgSerieDaRigenera());
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addError(
                    "Impossibile caricare il contenuto. Errore imprevisto nel caricamento dei dati");
        }
        if (!getMessageBox().hasError()) {
            if (doForward) {
                forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void riavviaGenerazioneContenuto() throws EMFError {
        log.info("Inviata richiesta di riavvio generazione contenuto");
        final BigDecimal idContenutoEff = getForm().getSerieDetail().getId_contenuto_eff().parse();
        String[] blocco = serieEjb.getFlContenutoBloccato(
                JobConstants.JobEnum.GENERAZIONE_CONTENUTO_EFFETTIVO_SERIE_UD.name(),
                idContenutoEff);
        if (blocco != null) {
            if (!blocco[0].equals("1")) {
                getMessageBox()
                        .addError("La generazione del contenuto effettivo non \u00E8 bloccata");
            }
        } else {
            getMessageBox().addError("La generazione del contenuto effettivo non \u00E8 bloccata");
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            generaContenutoEffettivo();
        }
    }

    @Override
    public void visualizzaConsistenzaAttesa() throws EMFError {
        // Se uno qualsiasi dei campi di consistenza \u00E8 definito, passo in modalità
        // visualizzazione, altrimenti vado
        // in inserimento
        getForm().getConsistenzaAttesaDetail().clear();
        Fields<Field> formAttiva = getForm().getSerieDetail();
        if (getLastPublisher().equals(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL)) {
            formAttiva = getForm().getContenutoSerieDetail();
        }
        BigDecimal idConsist = ((Input<BigDecimal>) formAttiva
                .getComponent(SerieUDForm.DatiSerieConsistenzaAttesaDetail.id_consist_ver_serie))
                .parse();
        BigDecimal idVerSerie = ((Input<BigDecimal>) formAttiva
                .getComponent(SerieUDForm.DatiSerieConsistenzaAttesaDetail.id_ver_serie)).parse();
        BigDecimal idStrut = ((Input<BigDecimal>) formAttiva
                .getComponent(SerieUDForm.DatiSerieConsistenzaAttesaDetail.id_strut)).parse();
        BigDecimal idTipoSerie = ((Input<BigDecimal>) formAttiva
                .getComponent(SerieUDForm.DatiSerieConsistenzaAttesaDetail.id_tipo_serie)).parse();
        getForm().getConsistenzaAttesaDetail().getTi_mod_consist_first_last()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("modalita",
                        CostantiDB.ModalitaDefPrimaUltimaUd.values()));
        getForm().getConsistenzaButtonList().setEditMode();
        getForm().getConsistenzaButtonList().hideAll();

        DecodeMap registri = DecodeMap.Factory.newInstance(
                tipoSerieEjb.getDecRegistroUnitaDocTableBeanFromTipoSerie(idStrut, idTipoSerie),
                "cd_registro_unita_doc", "cd_registro_unita_doc");
        getForm().getConsistenzaAttesaDetail().getCd_registro_first().setDecodeMap(registri);
        getForm().getConsistenzaAttesaDetail().getCd_registro_last().setDecodeMap(registri);

        if (((SingleValueField) formAttiva
                .getComponent(SerieUDForm.ConsistenzaAttesaDetail.ni_unita_doc_attese))
                .parse() != null
                || ((SingleValueField) formAttiva.getComponent(
                        SerieUDForm.ConsistenzaAttesaDetail.cd_first_unita_doc_attesa))
                        .parse() != null
                || ((SingleValueField) formAttiva
                        .getComponent(SerieUDForm.ConsistenzaAttesaDetail.cd_last_unita_doc_attesa))
                        .parse() != null
                || ((SingleValueField) formAttiva.getComponent(
                        SerieUDForm.ConsistenzaAttesaDetail.ti_mod_consist_first_last))
                        .parse() != null
                || ((SingleValueField) formAttiva
                        .getComponent(SerieUDForm.ConsistenzaAttesaDetail.cd_doc_consist_ver_serie))
                        .parse() != null
                || ((SingleValueField) formAttiva
                        .getComponent(SerieUDForm.ConsistenzaAttesaDetail.ds_doc_consist_ver_serie))
                        .parse() != null) {
            log.info("Visualizzo la consistenza in modalit\u00E0 visualizzazione");
            SerVVisConsistSerieUdRowBean row = serieEjb.getSerVVisConsistSerieUdRowBean(idConsist);
            getForm().getConsistenzaAttesaDetail().copyFromBean(row);
            getForm().getDatiSerieConsistenzaAttesaDetail().copyFromBean(row);

            getForm().getConsistenzaAttesaDetail().setViewMode();
            getForm().getConsistenzaAttesaDetail().setStatus(Status.view);

            SerLacunaConsistVerSerieTableBean lacuneTable = serieEjb
                    .getSerLacunaConsistVerSerieTableBean(idConsist);
            if (getForm().getLacuneList().getTable() != null) {
                int pageSize = getForm().getLacuneList().getTable().getPageSize();
                getForm().getLacuneList().setTable(lacuneTable);
                getForm().getLacuneList().getTable().setPageSize(pageSize);
                getForm().getLacuneList().getTable().first();
            } else {
                getForm().getLacuneList().setTable(lacuneTable);
                getForm().getLacuneList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getLacuneList().getTable().first();
            }
            getForm().getLacuneList().setStatus(Status.view);

            getForm().getDatiSerieConsistenzaAttesaDetail().getShow_edit()
                    .setValue(String.valueOf(false));
            getForm().getDatiSerieConsistenzaAttesaDetail().getShow_delete()
                    .setValue(String.valueOf(false));
            getForm().getLacuneList().setHideInsertButton(true);
            getForm().getLacuneList().setHideUpdateButton(true);
            getForm().getLacuneList().setHideDeleteButton(true);

            if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                    && (serieEjb.checkContenuto(idVerSerie, true, true, true,
                            CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                            CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                            CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name()))) {
                getForm().getLacuneList().setHideInsertButton(false);
                getForm().getLacuneList().setHideUpdateButton(false);
                getForm().getLacuneList().setHideDeleteButton(false);
                getForm().getDatiSerieConsistenzaAttesaDetail().getShow_edit()
                        .setValue(String.valueOf(true));
                getForm().getDatiSerieConsistenzaAttesaDetail().getShow_delete()
                        .setValue(String.valueOf(true));

                if (serieEjb.checkVersione(idVerSerie,
                        CostantiDB.StatoVersioneSerie.APERTA.name())) {
                    if (serieEjb.existContenuto(idVerSerie,
                            CostantiDB.TipoContenutoVerSerie.CALCOLATO.name())
                            && serieEjb.checkContenuto(idVerSerie, true, false, false,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())) {
                        getForm().getConsistenzaButtonList()
                                .getControllaContenutoCalcConsistAttesa().setHidden(false);
                    }
                    if (serieEjb.existContenuto(idVerSerie,
                            CostantiDB.TipoContenutoVerSerie.ACQUISITO.name())
                            && serieEjb.checkContenuto(idVerSerie, false, true, false,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())) {
                        getForm().getConsistenzaButtonList().getControllaContenutoAcqConsistAttesa()
                                .setHidden(false);
                    }
                } else if (serieEjb.checkVersione(idVerSerie,
                        CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())) {
                    if (serieEjb.existContenuto(idVerSerie,
                            CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())
                            && serieEjb.checkContenuto(idVerSerie, false, false, true,
                                    CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                    CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST
                                            .name())) {
                        getForm().getConsistenzaButtonList().getControllaContenutoEffConsistAttesa()
                                .setHidden(false);
                    }
                }
            }

            if (!getMessageBox().hasError()
                    && StringUtils.isNotBlank(row.getDsMsgSerieDaRigenera())) {
                getMessageBox().addWarning(row.getDsMsgSerieDaRigenera());
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else if (idVerSerie != null
                && serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                        CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                        CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                        CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
            log.info("Visualizzo la consistenza in modalit\u00E0 inserimento");
            getForm().getLacuneList().setTable(new SerLacunaConsistVerSerieTableBean());
            getForm().getLacuneList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            BaseRow row = new BaseRow();
            formAttiva.copyToBean(row);
            getForm().getConsistenzaAttesaDetail().setEditMode();

            getForm().getConsistenzaAttesaDetail().getNm_userid_consist()
                    .setValue(getUser().getUsername());
            getForm().getConsistenzaAttesaDetail().getNm_userid_consist().setViewMode();

            getForm().getDatiSerieConsistenzaAttesaDetail().copyFromBean(row);
            getForm().getDatiSerieConsistenzaAttesaDetail().getShow_edit()
                    .setValue(String.valueOf(true));
            getForm().getDatiSerieConsistenzaAttesaDetail().getShow_delete()
                    .setValue(String.valueOf(true));
            if (idConsist != null) {
                SerVVisConsistSerieUdRowBean consistRow = serieEjb
                        .getSerVVisConsistSerieUdRowBean(idConsist);
                getForm().getConsistenzaAttesaDetail().copyFromBean(consistRow);
                getForm().getConsistenzaAttesaDetail().setStatus(Status.update);
            } else {
                getForm().getConsistenzaAttesaDetail().setStatus(Status.insert);
            }
            getForm().getConsistenzaAttesaDetail().getDt_comunic_consist_ver_serie()
                    .setValue(ActionUtils.getStringDateTime(Calendar.getInstance().getTime()));
        } else {
            getMessageBox().addError(
                    "Lo stato della serie \u00E8 diverso da APERTA, DA_CONTROLLARE, CONTROLLATA o DA_VALIDARE");
        }
        if (!getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.CONSISTENZA_ATTESA_DETAIL);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateConsistenzaAttesaDetail() throws EMFError {
        log.info("Visualizzo la consistenza in modalit\u00E0 inserimento");
        getForm().getConsistenzaAttesaDetail().setEditMode();
        getForm().getConsistenzaAttesaDetail().getNm_userid_consist()
                .setValue(getUser().getUsername());
        getForm().getConsistenzaAttesaDetail().getNm_userid_consist().setViewMode();
        getForm().getConsistenzaButtonList().hideAll();
        getForm().getConsistenzaAttesaDetail().setStatus(Status.update);
        forwardToPublisher(Application.Publisher.CONSISTENZA_ATTESA_DETAIL);
    }

    private void cambiaStato(String dsAzione, String tiStatoVerSerie) throws EMFError {
        BigDecimal idVersione = getForm().getSerieDetail().getId_ver_serie().parse();
        if (!serieEjb.checkVersione(idVersione, (String[]) null)) {
            getMessageBox().addError("La versione della serie non \u00E8 quella corrente");
            forwardToPublisher(getLastPublisher());
        } else {
            final BigDecimal idContenutoVerSerie = getForm().getSerieDetail().getId_contenuto_eff()
                    .parse();
            getForm().getFiltriContenutoSerieDetail().clear();
            if (idContenutoVerSerie != null) {
                loadDettaglioContenuto(idContenutoVerSerie,
                        CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name(), false);
                getForm().getCambioStatoSerie().getConfermaCambioStato().setEditMode();
                getForm().getCambioStatoSerie().getDs_nota_azione().setEditMode();
                getForm().getCambioStatoSerie().getDs_nota_azione().clear();

                getForm().getCambioStatoSerie().getDs_azione().setValue(dsAzione);
                getForm().getCambioStatoSerie().getTi_stato_ver_serie().setValue(tiStatoVerSerie);
                forwardToPublisher(Application.Publisher.CAMBIO_STATO_SERIE_UD);
            } else {
                getMessageBox().addError(
                        "Errore inaspettato nel caricamento del contenuto di tipo effettivo");
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    @Override
    public void cambiaStatoAperta() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_APERTA, CostantiDB.StatoVersioneSerie.APERTA.name());
    }

    @Override
    public void cambiaStatoDaValidare() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_DA_VALIDARE,
                CostantiDB.StatoVersioneSerie.DA_VALIDARE.name());
    }

    @Override
    public void cambiaStatoDaControllare() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_DA_CONTROLLARE,
                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name());
    }

    @Override
    public void cambiaStatoValidazioneInCorso() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_VALIDAZIONE_IN_CORSO,
                CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name());
    }

    @Override
    public void cambiaStatoForzaValidazione() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_FORZA_VALIDAZIONE,
                CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name());
    }

    @Override
    public void cambiaStatoAnnullata() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_ANNULLATA,
                CostantiDB.StatoVersioneSerie.ANNULLATA.name());
    }

    @Override
    public void cambiaStatoAggiorna() throws EMFError {
        cambiaStato(SerieEjb.AZIONE_SERIE_AGGIORNA, CostantiDB.StatoVersioneSerie.ANNULLATA.name());
    }

    @Override
    public void deleteNoteList() throws EMFError {
        SerVLisNotaSerieRowBean currentRow = (SerVLisNotaSerieRowBean) getForm().getNoteList()
                .getTable().getCurrentRow();
        if (!serieEjb.checkVersione(currentRow.getIdVerSerie(),
                CostantiDB.StatoVersioneSerie.APERTA.name(),
                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
            getMessageBox().addError(
                    "La versione della serie non \u00E8 quella corrente o lo stato della versione \u00E8 diverso da APERTA, DA_CONTROLLARE, CONTROLLATA, DA_VALIDARE");
            forwardToPublisher(getLastPublisher());
        } else {
            BigDecimal idNota = currentRow.getIdNotaVerSerie();
            int riga = getForm().getNoteList().getTable().getCurrentRowIndex();
            // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se
            // sono nel dettaglio
            if (getLastPublisher().equals(Application.Publisher.NOTA_DETAIL)) {
                if (!idNota.equals(getForm().getNotaDetail().getId_nota_ver_serie().parse())) {
                    getMessageBox().addError(
                            "Eccezione imprevista nell'eliminazione dell'elemento di descrizione");
                }
            }

            if (!getMessageBox().hasError() && idNota != null) {
                try {
                    serieEjb.deleteNota(idNota);
                    getForm().getNoteList().getTable().remove(riga);

                    getMessageBox().addInfo("Elemento di descrizione eliminato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } catch (ParerUserError ex) {
                    getMessageBox()
                            .addError("L'elemento di descrizione non pu\u00F2 essere eliminato: "
                                    + ex.getDescription());
                }
            }
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.NOTA_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void deleteNotaDetail() throws EMFError {
        deleteNoteList();
    }

    @Override
    public void updateNoteList() throws EMFError {
        SerVLisNotaSerieRowBean currentRow = (SerVLisNotaSerieRowBean) getForm().getNoteList()
                .getTable().getCurrentRow();
        if (!serieEjb.checkVersione(currentRow.getIdVerSerie(),
                CostantiDB.StatoVersioneSerie.APERTA.name(),
                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
            getMessageBox().addError(
                    "La versione della serie non \u00E8 quella corrente o lo stato della versione \u00E8 diverso da APERTA, DA_CONTROLLARE, CONTROLLATA, DA_VALIDARE");
            forwardToPublisher(getLastPublisher());
        } else {
            BaseRow tmpRow = new BaseRow();
            getForm().getSerieDetail().copyToBean(tmpRow);
            getForm().getDatiSerieDetail().copyFromBean(tmpRow);

            getForm().getNotaDetail().getId_tipo_nota_serie()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            tipoSerieEjb.getSingleDecTipoNotaSerieTableBean(
                                    currentRow.getIdTipoNotaSerie()),
                            "id_tipo_nota_serie", "ds_tipo_nota_serie"));
            getForm().getNotaDetail().setViewMode();

            getForm().getNotaDetail().copyFromBean(currentRow);
            getForm().getNotaDetail().getDs_nota_ver_serie().setEditMode();
            DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_HOUR_MINUTE_TYPE);
            getForm().getNotaDetail().getDt_nota_ver_serie()
                    .setValue(formato.format(Calendar.getInstance().getTime()));

            getForm().getNoteList().setStatus(Status.update);
            getForm().getNotaDetail().setStatus(Status.update);

            forwardToPublisher(Application.Publisher.NOTA_DETAIL);
        }
    }

    @Override
    public JSONObject triggerNotaDetailId_tipo_nota_serieOnTrigger() throws EMFError {
        getForm().getNotaDetail().post(getRequest());
        BigDecimal idTipoNotaSerie = getForm().getNotaDetail().getId_tipo_nota_serie().parse();
        BigDecimal idVersione = getForm().getSerieDetail().getId_ver_serie().parse();

        if (idTipoNotaSerie != null) {
            BigDecimal lastPgNota = serieEjb.getMaxPgNota(idVersione, idTipoNotaSerie);
            String nextPg = lastPgNota.add(BigDecimal.ONE).toPlainString();
            getForm().getNotaDetail().getPg_nota_ver_serie().setValue(nextPg);
        } else {
            getForm().getNotaDetail().getPg_nota_ver_serie()
                    .setValue(BigDecimal.ONE.toPlainString());
        }

        return getForm().getNotaDetail().asJSON();
    }

    @Override
    public void updateStatiList() throws EMFError {
        SerVLisStatoSerieRowBean currentRow = (SerVLisStatoSerieRowBean) getForm().getStatiList()
                .getTable().getCurrentRow();
        BaseRow tmpRow = new BaseRow();
        getForm().getSerieDetail().copyToBean(tmpRow);
        getForm().getDatiSerieDetail().copyFromBean(tmpRow);

        getForm().getStatoDetail().setViewMode();

        getForm().getStatoDetail().copyFromBean(currentRow);
        getForm().getStatoDetail().getDs_nota_azione().setEditMode();

        getForm().getStatiList().setStatus(Status.update);

        forwardToPublisher(Application.Publisher.STATO_DETAIL);
    }

    /* TAB CONTENUTO SERIE */
    @Override
    public void tabListaUnitaDocumentarieOnClick() throws EMFError {
        getForm().getContenutoSerieDetailSubTabs().setCurrentTab(
                getForm().getContenutoSerieDetailSubTabs().getListaUnitaDocumentarie());
        forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
    }

    @Override
    public void tabListaErroriContenutoOnClick() throws EMFError {
        getForm().getContenutoSerieDetailSubTabs().setCurrentTab(
                getForm().getContenutoSerieDetailSubTabs().getListaErroriContenuto());
        forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
    }

    @Override
    public void tabListaErroriFileInputOnClick() throws EMFError {
        getForm().getContenutoSerieDetailSubTabs().setCurrentTab(
                getForm().getContenutoSerieDetailSubTabs().getListaErroriFileInput());
        forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
    }

    @Override
    public void tabDettaglioContenutoOnClick() throws EMFError {
        getForm().getContenutoSerieDetailTabs()
                .setCurrentTab(getForm().getContenutoSerieDetailTabs().getDettaglioContenuto());
        forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
    }

    @Override
    public void tabFiltriContenutoOnClick() throws EMFError {
        getForm().getContenutoSerieDetailTabs()
                .setCurrentTab(getForm().getContenutoSerieDetailTabs().getFiltriContenuto());
        forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
    }

    @Override
    public void downloadFile() throws EMFError {
        BigDecimal idVerSerie = getForm().getContenutoSerieDetail().getId_ver_serie().parse();
        BigDecimal anno = getForm().getContenutoSerieDetail().getAa_serie().parse();
        String cdCompSerie = getForm().getContenutoSerieDetail().getCd_composito_serie().parse();
        String cdVerSerie = getForm().getContenutoSerieDetail().getCd_ver_serie().parse();
        String filename = "FileInput_" + cdCompSerie + "_" + anno.toPlainString() + "_" + cdVerSerie
                + ".csv";
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);
        try {
            String fileString = serieEjb.getFileAcquisito(idVerSerie);
            if (fileString != null) {

                try (FileOutputStream fileOs = new FileOutputStream(tmpFile); // Try With Resources
                        InputStream is = new ByteArrayInputStream(
                                fileString.getBytes(Charset.forName("UTF-8")))) {
                    byte[] data = new byte[1024];
                    int count;
                    while ((count = is.read(data, 0, 1024)) != -1) {
                        fileOs.write(data, 0, count);
                    }
                }
                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                        tmpFile.getName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                        tmpFile.getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                        VerFormatiEnums.CSV_MIME);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } catch (IOException ex) {
            log.error(ERRORE_DOWNLOAD_MESSAGE_INIT + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ERRORE_DOWNLOAD_MESSAGE);
        } finally {
            // MAC #37610: rimuovo le chiusure degli stream, non più necessarie in quanto ci pensa
            // il blocco
            // try-with-resource
            if (getMessageBox().hasError()) {
                forwardToPublisher(getLastPublisher());
            } else {
                forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
            }
        }

    }

    @Override
    public void downloadContenuto() throws EMFError {
        BigDecimal anno = getForm().getContenutoSerieDetail().getAa_serie().parse();
        String cdCompSerie = getForm().getContenutoSerieDetail().getCd_composito_serie().parse();
        String cdVerSerie = getForm().getContenutoSerieDetail().getCd_ver_serie().parse();
        String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                .parse();
        String filename = "Contenuto_" + tipoContenuto + "_" + cdCompSerie + "_"
                + anno.toPlainString() + "_" + cdVerSerie + ".csv";
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);

        try {

            SerVLisUdAppartSerieTableBean fullUdTable = serieEjb
                    .getSerVLisUdAppartSerieTableBeanForDownload(
                            getForm().getContenutoSerieDetail().getId_contenuto_ver_serie().parse(),
                            getRicercaUdAppartBeanFromContenutoSerie());
            // uso il Fields solo per sapere i nomi di colonna, ma poi estraggo la table completa,
            // bypassando il lazy
            // loading usato per l'interfaccia utente
            ActionUtils.buildCsvString(getForm().getUdList(), fullUdTable,
                    SerVLisUdAppartSerieRowBean.TABLE_DESCRIPTOR, tmpFile);

            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                    getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                    tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                    Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    VerFormatiEnums.CSV_MIME);
        } catch (IOException ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ERRORE_DOWNLOAD_MESSAGE);
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void downloadErroriConsistenza() throws EMFError {
        BigDecimal anno = getForm().getContenutoSerieDetail().getAa_serie().parse();
        String cdCompSerie = getForm().getContenutoSerieDetail().getCd_composito_serie().parse();
        String cdVerSerie = getForm().getContenutoSerieDetail().getCd_ver_serie().parse();
        String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                .parse();
        String filename = "ErroriConsistenza_" + tipoContenuto + "_" + cdCompSerie + "_"
                + anno.toPlainString() + "_" + cdVerSerie + ".csv";
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);

        try {
            ActionUtils.buildCsvString(getForm().getErroriContenutiList(),
                    getForm().getErroriContenutiList().getTable(),
                    SerVLisErrContenSerieUdRowBean.TABLE_DESCRIPTOR, tmpFile);

            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                    getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                    tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                    Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    VerFormatiEnums.CSV_MIME);
        } catch (IOException ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ERRORE_DOWNLOAD_MESSAGE);
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void downloadQuery() throws EMFError {
        BigDecimal idContenutoVerSerie = getForm().getContenutoSerieDetail()
                .getId_contenuto_ver_serie().parse();
        BigDecimal anno = getForm().getContenutoSerieDetail().getAa_serie().parse();
        String cdCompSerie = getForm().getContenutoSerieDetail().getCd_composito_serie().parse();
        String cdVerSerie = getForm().getContenutoSerieDetail().getCd_ver_serie().parse();
        String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                .parse();
        String prefixFile = "QueryContenuto_" + tipoContenuto + "_";
        String suffixFile = "_" + cdCompSerie + "_" + anno.toPlainString() + "_" + cdVerSerie
                + ".txt";
        String filename = "QueryContenuto_" + tipoContenuto + "_" + cdCompSerie + "_"
                + anno.toPlainString() + "_" + cdVerSerie + ".zip";
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);

        try (FileOutputStream fileOs = new FileOutputStream(tmpFile); // Try With Resources
                ZipOutputStream out = new ZipOutputStream(fileOs)) {

            serieEjb.createZipSerQueryContenutoVerSerie(idContenutoVerSerie, out, prefixFile,
                    suffixFile);

            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                    getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                    tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                    Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    "application/zip");

        } catch (IOException ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ERRORE_DOWNLOAD_MESSAGE);
        } finally {
            if (getMessageBox().hasError()) {
                forwardToPublisher(getLastPublisher());
            } else {
                forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
            }
        }
    }

    @Override
    public void downloadErroriFileInput() throws EMFError {
        BigDecimal anno = getForm().getContenutoSerieDetail().getAa_serie().parse();
        String cdCompSerie = getForm().getContenutoSerieDetail().getCd_composito_serie().parse();
        String cdVerSerie = getForm().getContenutoSerieDetail().getCd_ver_serie().parse();
        String filename = "ErroriFileInput_" + cdCompSerie + "_" + anno.toPlainString() + "_"
                + cdVerSerie + ".csv";
        File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);

        try {
            final SerVLisErrFileSerieUdTableBean fullListTable = serieEjb
                    .getSerVLisErrFileSerieUdTableBeanForDownload(
                            getForm().getSerieDetail().getId_ver_serie().parse(),
                            CostantiDB.ScopoFileInputVerSerie.ACQUISIRE_CONTENUTO.name());
            ActionUtils.buildCsvString(getForm().getErroriFileInputList(), fullListTable,
                    SerVLisErrFileSerieUdRowBean.TABLE_DESCRIPTOR, tmpFile);

            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                    getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                    tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                    Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    VerFormatiEnums.CSV_MIME);
        } catch (IOException ex) {
            log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ERRORE_DOWNLOAD_MESSAGE);
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    @Override
    public void controllaContenuto() throws EMFError {
        log.info("Inviata richiesta di controllo contenuto");
        final BigDecimal idVerSerie = getForm().getContenutoSerieDetail().getId_ver_serie().parse();
        final BigDecimal idSerie = getForm().getContenutoSerieDetail().getId_serie().parse();
        final BigDecimal idContenuto = getForm().getContenutoSerieDetail()
                .getId_contenuto_ver_serie().parse();
        final String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                .parse();

        try {
            String cdSerie = getForm().getContenutoSerieDetail().getCd_composito_serie().parse();
            BigDecimal aaSerie = getForm().getContenutoSerieDetail().getAa_serie().parse();
            String keyFuture = FutureUtils.buildKeyFuture(
                    CostantiDB.TipoChiamataAsync.CONTROLLO_CONTENUTO.name(), cdSerie, aaSerie,
                    getUser().getIdOrganizzazioneFoglia(), idVerSerie.longValue());

            serieEjb.initControlloContenuto(idSerie, idVerSerie, tipoContenuto);
            Future<String> future = serieEjb.callControlloContenutoAsync(getUser().getIdUtente(),
                    idVerSerie.longValue(), tipoContenuto);
            FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture, future);

            getMessageBox().addInfo("Controllo del contenuto della serie lanciato con successo");
            loadDettaglioContenuto(idContenuto, tipoContenuto, true);
            getForm().getContenutoButtonList().getControllaContenuto().setHidden(true);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
        }
    }

    @Override
    public void riavviaControlloContenuto() throws EMFError {
        log.info("Inviata richiesta di riavvio controllo contenuto");
        final BigDecimal idVerSerie = getForm().getContenutoSerieDetail().getId_ver_serie().parse();
        final BigDecimal idSerie = getForm().getContenutoSerieDetail().getId_serie().parse();
        final BigDecimal idContenuto = getForm().getContenutoSerieDetail()
                .getId_contenuto_ver_serie().parse();
        final String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                .parse();
        String[] blocco = serieEjb.getFlContenutoBloccato(
                JobConstants.JobEnum.CONTROLLA_CONTENUTO_SERIE_UD.name(), idContenuto);
        if (blocco != null) {
            if (!blocco[0].equals("1")) {
                getMessageBox().addError("Il controllo del contenuto non \u00E8 bloccato");
            }
            if (!getMessageBox().hasError()) {
                try {
                    serieEjb.riavvioControlloContenuto(idSerie, idVerSerie, idContenuto,
                            tipoContenuto);
                    String cdSerie = getForm().getContenutoSerieDetail().getCd_composito_serie()
                            .parse();
                    BigDecimal aaSerie = getForm().getContenutoSerieDetail().getAa_serie().parse();
                    String keyFuture = FutureUtils.buildKeyFuture(
                            CostantiDB.TipoChiamataAsync.CONTROLLO_CONTENUTO.name(), cdSerie,
                            aaSerie, getUser().getIdOrganizzazioneFoglia(), idVerSerie.longValue());

                    Future<String> future = serieEjb.callControlloContenutoAsync(
                            getUser().getIdUtente(), idVerSerie.longValue(), tipoContenuto);
                    FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture,
                            future);

                    getMessageBox()
                            .addInfo("Controllo del contenuto della serie lanciato con successo");
                    loadDettaglioContenuto(idContenuto, tipoContenuto, true);
                    getForm().getContenutoButtonList().getRiavviaControlloContenuto()
                            .setHidden(true);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        } else {
            getMessageBox().addError("Il controllo del contenuto non \u00E8 bloccato");
        }
        forwardToPublisher(Application.Publisher.CONTENUTO_SERIE_UD_DETAIL);
    }

    @Override
    public void ricercaContenuto() throws EMFError {
        if (getForm().getFiltriContenutoSerieDetail().postAndValidate(getRequest(),
                getMessageBox())) {
            log.info("Carico la lista in base ai parametri");
            SerVLisUdAppartSerieTableBean udTb = serieEjb.getSerVLisUdAppartSerieTableBean(
                    getForm().getContenutoSerieDetail().getId_contenuto_ver_serie().parse(),
                    getRicercaUdAppartBeanFromContenutoSerie());
            int lastPageSize = getForm().getUdList().getTable().getPageSize();
            getForm().getUdList().setTable(udTb);
            getForm().getUdList().getTable().setPageSize(lastPageSize);
            getForm().getUdList().getTable().first();
        }
        forwardToPublisher(getLastPublisher());
    }

    private RicercaUdAppartBean getRicercaUdAppartBeanFromContenutoSerie() throws EMFError {
        BigDecimal aaSerie = getForm().getContenutoSerieDetail().getAa_serie().parse();
        RicercaUdAppartBean parametri = initParametriRicercaContenuto(aaSerie);
        return parametri;
    }

    private RicercaUdAppartBean initParametriRicercaContenuto(BigDecimal aaSerie) throws EMFError {
        RicercaUdAppartBean parametri = new RicercaUdAppartBean(
                getForm().getFiltriContenutoSerieDetail());
        if (parametri.getDtUdSerieDa() != null && parametri.getDtUdSerieA() == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(aaSerie.intValue(), 11, 31, 23, 59, 59);
            cal.set(Calendar.MILLISECOND, 999);
            parametri.setDtUdSerieA(cal.getTime());
        } else if (parametri.getDtUdSerieDa() == null && parametri.getDtUdSerieA() != null) {
            Calendar cal = Calendar.getInstance();
            cal.set(aaSerie.intValue(), 0, 1, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            parametri.setDtUdSerieDa(cal.getTime());
        }
        if (parametri.getPgUdSerieDa() != null && parametri.getPgUdSerieA() == null) {
            parametri.setPgUdSerieA(new BigDecimal(999999));
        } else if (parametri.getPgUdSerieDa() == null && parametri.getPgUdSerieA() != null) {
            parametri.setPgUdSerieDa(BigDecimal.ONE);
        }
        return parametri;
    }

    @Override
    public void deleteUdList() throws EMFError {
        SerVLisUdAppartSerieRowBean row = (SerVLisUdAppartSerieRowBean) getForm().getUdList()
                .getTable().getCurrentRow();
        BigDecimal idSerie = getForm().getContenutoSerieDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getContenutoSerieDetail().getId_ver_serie().parse();
        BigDecimal idContenuto = getForm().getContenutoSerieDetail().getId_contenuto_ver_serie()
                .parse();
        String tipoContenuto = getForm().getContenutoSerieDetail().getTi_contenuto_ver_serie()
                .parse();
        BigDecimal idUdAppartVerSerie = row.getIdUdAppartVerSerie();
        boolean contenCalc;
        boolean contenAcq = false;
        boolean contenEff = false;
        try {
            if ((contenCalc = tipoContenuto
                    .equals(CostantiDB.TipoContenutoVerSerie.CALCOLATO.name()))
                    || (contenAcq = tipoContenuto
                            .equals(CostantiDB.TipoContenutoVerSerie.ACQUISITO.name()))) {
                if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name())
                        && serieEjb.checkContenuto(idVerSerie, contenCalc, contenAcq, contenEff,
                                CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name(),
                                CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
                    serieEjb.deleteAroUdAppartVerSerie(getUser().getIdUtente(), idSerie,
                            idUdAppartVerSerie);
                } else {
                    getMessageBox().addError(
                            "L'unit\u00E0 documentaria selezionata non pu\u00F2 essere rimossa dalla serie");
                }
            } else if (serieEjb.checkVersione(idVerSerie,
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                    CostantiDB.StatoVersioneSerie.CONTROLLATA.name())
                    && serieEjb.checkContenuto(idVerSerie, contenCalc, contenAcq, contenEff,
                            CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                            CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name(),
                            CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name())) {
                serieEjb.deleteAroUdAppartVerSerie(getUser().getIdUtente(), idSerie,
                        idUdAppartVerSerie);
            } else {
                getMessageBox().addError(
                        "L'unit\u00E0 documentaria selezionata non pu\u00F2 essere rimossa dalla serie");
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        if (!getMessageBox().hasError()) {
            getMessageBox().addInfo("Unit\u00E0 documentaria rimossa con successo dalla serie");
            loadDettaglioContenuto(idContenuto, tipoContenuto, true);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    public void download() throws EMFError, IOException {
        String filename = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        Boolean deleteFile = Boolean.parseBoolean((String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la
                 * servlet per fare il download
                 */
                getResponse().setContentType(
                        StringUtils.isBlank(contentType) ? "application/zip" : contentType);
                getResponse().setHeader("Content-Disposition",
                        "attachment; filename=\"" + filename);

                try (OutputStream outUD = getServletOutputStream();
                        FileInputStream inputStream = new FileInputStream(fileToDownload);) {

                    getResponse().setHeader("Content-Length",
                            String.valueOf(fileToDownload.length()));
                    byte[] bytes = new byte[8000];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(bytes)) != -1) {
                        outUD.write(bytes, 0, bytesRead);
                    }
                    outUD.flush();
                } catch (IOException e) {
                    log.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile.booleanValue()) {
                    Files.delete(fileToDownload.toPath());
                }
            } else {
                getMessageBox()
                        .addError("Errore durante il tentativo di download. File non trovato");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
            forwardToPublisher(getLastPublisher());
        }
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
    }

    @Override
    public void updateLacuneList() throws EMFError {
        initUpdateLacuna();
    }

    @Override
    public void updateLacunaDetail() throws EMFError {
        initUpdateLacuna();
    }

    private void initUpdateLacuna() {
        getForm().getLacunaDetail().setEditMode();
        getForm().getLacunaDetail().getTi_mod_lacuna().setViewMode();

        getForm().getLacuneList().setStatus(Status.update);
        getForm().getLacunaDetail().setStatus(Status.update);

        forwardToPublisher(Application.Publisher.LACUNA_DETAIL);
    }

    @Override
    public void deleteLacuneList() throws EMFError {
        SerLacunaConsistVerSerieRowBean row = (SerLacunaConsistVerSerieRowBean) getForm()
                .getLacuneList().getTable().getCurrentRow();
        BigDecimal idLacuna = row.getIdLacunaConsistVerSerie();
        int riga = getForm().getLacuneList().getTable().getCurrentRowIndex();
        // Eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono
        // nel dettaglio
        if (getLastPublisher().equals(Application.Publisher.LACUNA_DETAIL)) {
            if (!idLacuna
                    .equals(getForm().getLacunaDetail().getId_lacuna_consist_ver_serie().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione della lacuna");
            }
        }

        if (!getMessageBox().hasError() && idLacuna != null) {
            try {
                serieEjb.deleteLacuna(getUser().getIdUtente(), idLacuna);
                getForm().getLacuneList().getTable().remove(riga);

                getMessageBox().addInfo("Lacuna eliminata con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(
                        "La lacuna non pu\u00F2 essere eliminata: " + ex.getDescription());
            }
        }

        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.LACUNA_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void deleteLacunaDetail() throws EMFError {
        deleteLacuneList();
    }

    @Override
    public void confermaCambioStato() throws EMFError {
        getForm().getCambioStatoSerie().getDs_nota_azione().post(getRequest());
        BigDecimal idContenutoVerSerie = getForm().getContenutoSerieDetail()
                .getId_contenuto_ver_serie().parse();
        String azione = getForm().getCambioStatoSerie().getDs_azione().parse();
        String stato = getForm().getCambioStatoSerie().getTi_stato_ver_serie().parse();
        String popup = getRequest().getParameter("popup");

        if (idContenutoVerSerie != null && StringUtils.isNotBlank(azione)
                && StringUtils.isNotBlank(stato)) {
            if (stato.equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())
                    && serieEjb.checkErroriContenutoEffettivo(idContenutoVerSerie,
                            CostantiDB.TipoGravitaErrore.ERRORE.name())
                    && StringUtils.isBlank(popup)) {
                getRequest().setAttribute("erroriContenutoBox", true);
                forwardToPublisher(getLastPublisher());
            } else {
                eseguiCambioStato();
            }
        } else {
            getMessageBox()
                    .addError("Errore inaspettato nell'esecuzione del cambio di stato della serie");
            goBack();
        }
    }

    private void eseguiCambioStato() throws EMFError {
        BigDecimal idSerie = getForm().getContenutoSerieDetail().getId_serie().parse();
        BigDecimal idContenutoVerSerie = getForm().getContenutoSerieDetail()
                .getId_contenuto_ver_serie().parse();
        BigDecimal idVerSerie = getForm().getContenutoSerieDetail().getId_ver_serie().parse();
        String azione = getForm().getCambioStatoSerie().getDs_azione().parse();
        String stato = getForm().getCambioStatoSerie().getTi_stato_ver_serie().parse();
        String nota = getForm().getCambioStatoSerie().getDs_nota_azione().parse();
        String statoSerieCorrente = getForm().getSerieDetail().getTi_stato_ver_serie().getValue();
        String tipoOperazione = "CAMBIA_STATO";

        if (idSerie != null && idVerSerie != null && StringUtils.isNotBlank(azione)
                && StringUtils.isNotBlank(stato) && StringUtils.isNotBlank(statoSerieCorrente)) {
            try {
                serieEjb.cambiaStatoSerie(getUser().getIdUtente(), idSerie, idVerSerie,
                        idContenutoVerSerie, azione, stato, nota, statoSerieCorrente,
                        tipoOperazione);
                if (stato.equals(CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name())) {
                    String cdSerie = getForm().getContenutoSerieDetail().getCd_composito_serie()
                            .parse();
                    BigDecimal aaSerie = getForm().getContenutoSerieDetail().getAa_serie().parse();
                    String keyFuture = FutureUtils.buildKeyFuture(
                            CostantiDB.TipoChiamataAsync.VALIDAZIONE_SERIE.name(), cdSerie, aaSerie,
                            getUser().getIdOrganizzazioneFoglia(), idVerSerie.longValue());

                    Future<String> future = serieEjb.callValidazioneSerieAsync(idVerSerie);
                    FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture,
                            future);

                    getMessageBox().addInfo(
                            "La validazione della serie \u00E8 stata schedulata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } else {
                    getMessageBox()
                            .addInfo("Modifica dello stato della serie eseguita con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        } else {
            getMessageBox()
                    .addError("Errore inaspettato nell'esecuzione del cambio di stato della serie");
        }
        goBack();
    }

    public void aggiornaCdSerieDaRangeDate() throws EMFError {
        getForm().getCreazioneSerie().post(getRequest());
        Date dtInizio = getForm().getCreazioneSerie().getDt_inizio_serie().parse();
        Date dtFine = getForm().getCreazioneSerie().getDt_fine_serie().parse();
        BigDecimal anno = getForm().getCreazioneSerie().getAa_serie().parse();
        JSONObject jsonObject = new JSONObject();
        try {
            if (dtInizio != null && dtFine != null) {
                BigDecimal idTipoSerie = getForm().getCreazioneSerie().getNm_tipo_serie().parse();
                if (idTipoSerie != null) {
                    DecTipoSerieRowBean row = tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie);
                    if (row.getTiSelUd().equals(CostantiDB.TipoSelUdTipiSerie.DT_UD_SERIE.name())) {
                        if (row.getNiMmCreaAutom() != null) {
                            SerieAutomBean creaAutomBean = null;
                            if (!getMessageBox().hasError()) {
                                try {
                                    creaAutomBean = serieEjb.generateIntervalliSerieAutom(
                                            row.getNiMmCreaAutom(), row.getCdSerieDefault(),
                                            row.getDsSerieDefault(), anno.intValue(),
                                            row.getTiSelUd());
                                } catch (ParerUserError ex) {
                                    getMessageBox().addError(ex.getDescription());
                                }
                            }
                            if (!getMessageBox().hasError() && creaAutomBean != null) {
                                Calendar calInizio = Calendar.getInstance();
                                Calendar calFine = Calendar.getInstance();

                                calInizio.setTime(dtInizio);
                                calInizio.set(Calendar.HOUR_OF_DAY, 0);
                                calInizio.set(Calendar.MINUTE, 0);
                                calInizio.set(Calendar.SECOND, 0);
                                calInizio.set(Calendar.MILLISECOND, 0);
                                calFine.setTime(dtFine);
                                calFine.set(Calendar.HOUR_OF_DAY, 23);
                                calFine.set(Calendar.MINUTE, 59);
                                calFine.set(Calendar.SECOND, 59);
                                calFine.set(Calendar.MILLISECOND, 999);

                                IntervalliSerieAutomBean tmpBean = new IntervalliSerieAutomBean(
                                        calInizio.getTime(), calFine.getTime(), null, null);
                                if (!creaAutomBean.getIntervalli().contains(tmpBean)) {
                                    getMessageBox().addError(
                                            "Il range di date non \u00E8 coerente con il numero di mesi per cui creare la serie definito dal tipo di serie");
                                } else {
                                    int index = creaAutomBean.getIntervalli().indexOf(tmpBean);
                                    if (index != -1) {
                                        String cdSerie = getForm().getCreazioneSerie().getCd_serie()
                                                .parse();
                                        String newCdSerie;
                                        if (cdSerie.contains("-" + anno.toPlainString())) {
                                            newCdSerie = cdSerie.substring(0,
                                                    cdSerie.indexOf("-" + anno.toPlainString()))
                                                    + "-" + anno.toPlainString() + "-"
                                                    + (index + 1);
                                        } else {
                                            newCdSerie = cdSerie + "-" + anno.toPlainString() + "-"
                                                    + (index + 1);
                                        }
                                        getForm().getCreazioneSerie().getCd_serie()
                                                .setValue(newCdSerie);
                                        jsonObject.put(getForm().getCreazioneSerie().getCd_serie()
                                                .getName(), newCdSerie);
                                    }
                                }
                            }
                        } else {
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
                            String cdSerie = getForm().getCreazioneSerie().getCd_serie().parse();
                            String newCdSerie;
                            if (cdSerie.contains("-" + anno.toPlainString())) {
                                newCdSerie = cdSerie.substring(0,
                                        cdSerie.indexOf("-" + anno.toPlainString())) + "-"
                                        + anno.toPlainString() + "-" + df.format(dtFine);
                            } else {
                                newCdSerie = cdSerie + "-" + anno.toPlainString() + "-"
                                        + df.format(dtFine);
                            }
                            getForm().getCreazioneSerie().getCd_serie().setValue(newCdSerie);
                            jsonObject.put(getForm().getCreazioneSerie().getCd_serie().getName(),
                                    newCdSerie);
                        }
                    }
                }
            }
        } catch (JSONException ex) {
            log.error("Eccezione nel popolamento dell'oggetto JSON", ex);
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.CREAZIONE_SERIE_UD);
        } else {
            redirectToAjax(jsonObject);
        }
    }

    @Override
    public void riavviaValidazioneSerie() throws EMFError {
        log.info("Inviata richiesta di riavvio validazione serie");
        final BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        final BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie().parse();
        String[] blocco = serieEjb.getFlVersioneBloccata(
                JobConstants.JobEnum.SET_SERIE_UD_VALIDATA.name(), idVerSerie);
        if (blocco != null) {
            if (!blocco[0].equals("1")) {
                getMessageBox().addError("La validazione della serie non \u00E8 bloccata");
            }
            if (!getMessageBox().hasError()) {
                try {
                    serieEjb.riavvioValidazioneSerie(idSerie, idVerSerie);
                    String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
                    BigDecimal aaSerie = getForm().getSerieDetail().getAa_serie().parse();
                    String keyFuture = FutureUtils.buildKeyFuture(
                            CostantiDB.TipoChiamataAsync.VALIDAZIONE_SERIE.name(), cdSerie, aaSerie,
                            getUser().getIdOrganizzazioneFoglia(), idVerSerie.longValue());

                    Future<String> future = serieEjb.callValidazioneSerieAsync(idVerSerie);
                    FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture,
                            future);

                    getMessageBox().addInfo(
                            "La validazione della serie \u00E8 stata schedulata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
        } else {
            getMessageBox().addError("La validazione della serie non \u00E8 bloccata");
        }
        forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
    }

    @Override
    public void downloadAIP() throws EMFError {
        BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie_corr().parse();
        String tiStatoVerSerie = getForm().getSerieDetail().getTi_stato_ver_serie().parse();
        String extension = null;
        String tiFileVerSerie = null;
        if (tiStatoVerSerie.equals(CostantiDB.StatoVersioneSerie.DA_FIRMARE.name())) {
            extension = ".xml";
            tiFileVerSerie = CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO.name();
        } else if (tiStatoVerSerie.equals(CostantiDB.StatoVersioneSerie.FIRMATA.name())) {
            tiFileVerSerie = CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO.name();

            // MEV#15967 - Attivazione della firma Xades e XadesT
            try {
                String tiFirma = serieEjb.getTipoFirmaFileVerSerie(idVerSerie, tiFileVerSerie);
                if (tiFirma != null && tiFirma.equals(
                        it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.XADES.name())) {
                    extension = ".xml";
                } else {
                    extension = ".xml.p7m";
                }
            } catch (ParerUserError ex) {
                throw new EMFError(ex.getMessage(), ex);
            }
            //

        } else {
            getMessageBox().addError(
                    "Errore inaspettato per il download dell'indice AIP : stato versione diverso da 'DA_FIRMARE' o 'FIRMATA'");
        }

        String maxResultStandard = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
        SerUrnFileVerSerie verIndice = serieEjb
                .getUrnFileVerSerieNormalizzatoByIdVerSerieAndTiFile(idVerSerie, tiFileVerSerie);
        String filename = null;

        if (verIndice != null) {
            filename = verIndice.getDsUrn();
        }

        if (StringUtils.isNotBlank(filename)) {
            ZipArchiveOutputStream out = new ZipArchiveOutputStream(getServletOutputStream());
            getResponse().setContentType("application/zip");
            getResponse().setHeader("Content-Disposition", "attachment; filename=\""
                    + MessaggiWSFormat.bonificaUrnPerNomeFile(filename) + ".zip");

            try {
                // Nome del file IndiceAIPSerieUD-<versione
                // serie>_<ambiente>_<ente>_<struttura>_<codice serie>”
                String cdVersioneSerie = getForm().getSerieDetail().getCd_ver_serie().parse();
                String[] ambEnteStrutSerie = serieEjb.ambienteEnteStrutturaSerie(idSerie);
                String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
                String nomeXml = "IndiceAIPSerieUD-" + cdVersioneSerie + "_" + ambEnteStrutSerie[0]
                        + "_" + ambEnteStrutSerie[1] + "_" + ambEnteStrutSerie[2] + "_" + cdSerie;

                nomeXml = nomeXml + extension;

                zippaIndiceAIPOs(out, idVerSerie.longValue(), nomeXml, tiFileVerSerie);
                out.flush();
                out.close();
            } catch (Exception e) {
                log.error("Eccezione", e);
            } finally {
                freeze();
            }
        }
    }

    // MEV#30400
    private void zippaIndiceAIPOs(ZipArchiveOutputStream zipOutputStream, Long idVerIndiceAip,
            String nomeXml, String tiFile) throws IOException {

        ZipArchiveEntry verIndiceAipZae = new ZipArchiveEntry(nomeXml);
        zipOutputStream.putArchiveEntry(verIndiceAipZae);

        // recupero documento blob vs obj storage
        // build dto per recupero
        RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                Constants.TiEntitaSacerObjectStorage.INDICE_AIP_SERIE, idVerIndiceAip,
                zipOutputStream, RecBlbOracle.TabellaBlob.SER_FILE_VER_SERIE, tiFile);
        // recupero
        boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);

        if (!esitoRecupero) {
            throw new IOException(ECCEZIONE_RECUPERO_INDICE_AIP);
        }

        zipOutputStream.closeArchiveEntry();
    }
    // end MEV#30400

    public void scriviBlobboSuStream(OutputStream out, byte[] blobbo) throws IOException {
        // Ricavo lo stream di input
        InputStream is = new ByteArrayInputStream(blobbo);
        byte[] data = new byte[1024];
        int count;

        while ((count = is.read(data, 0, 1024)) != -1) {
            out.write(data, 0, count);
        }

        is.close();
    }

    /**
     * Metodo chiamato in polling tramite ajax dalle pagine di creazione serie, dettaglio serie e
     * dettaglio contenuto per fornire la mappa di future aggiornata e nel caso mostrare il popup di
     * visualizzazione dettaglio serie aggiornata
     *
     */
    @SuppressLogging
    public void checkSerieFuture() {
        try {
            JSONObject futureListObject = new JSONObject();
            JSONArray array = new JSONArray();
            Map<String, Future<?>> futureMap = FutureUtils.getFutureMap(getSession(),
                    getSession().getId());
            if (futureMap != null) {

                Iterator<Entry<String, Future<?>>> it = futureMap.entrySet().iterator();
                while (it.hasNext()) {
                    Entry<String, Future<?>> item = it.next();
                    String key = item.getKey();
                    Future<?> future = item.getValue();
                    if (key.equals(FutureUtils.PARAMETER_FUTURE_MAP)) {
                        if (future.isDone()) {
                            Map<String, ?> map = (Map<String, ?>) future.get();
                            for (Map.Entry<String, ?> entry : map.entrySet()) {
                                String keyMap = entry.getKey();
                                if (entry.getValue() instanceof String) {
                                    String value = (String) entry.getValue();

                                    String[] futureStrings = FutureUtils.unbuildKeyFuture(keyMap);
                                    JSONObject futureObject = new JSONObject();
                                    futureObject.put(
                                            WebConstants.PARAMETER_JSON_FUTURE_SERIE.TIPO_CREAZIONE
                                                    .name(),
                                            futureStrings[0]);
                                    futureObject
                                            .put(WebConstants.PARAMETER_JSON_FUTURE_SERIE.ID_STRUT
                                                    .name(), futureStrings[1]);
                                    futureObject
                                            .put(WebConstants.PARAMETER_JSON_FUTURE_SERIE.ANNO_SERIE
                                                    .name(), futureStrings[2]);
                                    futureObject.put(
                                            WebConstants.PARAMETER_JSON_FUTURE_SERIE.CODICE_SERIE
                                                    .name(),
                                            futureStrings[3]);
                                    futureObject.put(
                                            WebConstants.PARAMETER_JSON_FUTURE_SERIE.ID_VERSIONE
                                                    .name(),
                                            futureStrings[4]);
                                    futureObject.put(
                                            WebConstants.PARAMETER_JSON_FUTURE_SERIE.RESULT.name(),
                                            value);

                                    array.put(futureObject);
                                }
                            }
                            it.remove();
                            FutureUtils.putFutureMapInAppContext(getSession(), getSession().getId(),
                                    futureMap);
                        }
                    } else {
                        String[] futureStrings = FutureUtils.unbuildKeyFuture(key);
                        JSONObject futureObject = new JSONObject();
                        futureObject.put(
                                WebConstants.PARAMETER_JSON_FUTURE_SERIE.TIPO_CREAZIONE.name(),
                                futureStrings[0]);
                        futureObject.put(WebConstants.PARAMETER_JSON_FUTURE_SERIE.ID_STRUT.name(),
                                futureStrings[1]);
                        futureObject.put(WebConstants.PARAMETER_JSON_FUTURE_SERIE.ANNO_SERIE.name(),
                                futureStrings[2]);
                        futureObject.put(
                                WebConstants.PARAMETER_JSON_FUTURE_SERIE.CODICE_SERIE.name(),
                                futureStrings[3]);
                        futureObject.put(
                                WebConstants.PARAMETER_JSON_FUTURE_SERIE.ID_VERSIONE.name(),
                                futureStrings[4]);

                        String result;
                        if (!future.isDone()) {
                            result = WebConstants.PARAMETER_JSON_FUTURE_SERIE_RESULT.WORKING.name();
                        } else {
                            result = (String) future.get();
                            it.remove();
                            FutureUtils.putFutureMapInAppContext(getSession(), getSession().getId(),
                                    futureMap);
                        }

                        futureObject.put(WebConstants.PARAMETER_JSON_FUTURE_SERIE.RESULT.name(),
                                result);
                        array.put(futureObject);
                    }
                }
            }
            futureListObject.put("array", array);
            redirectToAjax(futureListObject);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(getLastPublisher());
        } catch (ExecutionException | JSONException ex) {
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(getLastPublisher());
        }
    }

    /**
     * Metodo chiamato tramite javascript nel caso il metodo checkSerieFuture abbia permesso la
     * visualizzazione del popup di aggiornamento dettaglio serie
     *
     * @throws EMFError errore generico
     */
    public void reloadSerie() throws EMFError {
        if (getRequest().getParameterValues("VerSerie") != null) {
            String[] verSeriesParam = getRequest().getParameterValues("VerSerie");
            List<BigDecimal> verSeries = new ArrayList<>();
            for (String verSerie : verSeriesParam) {
                verSeries.add(new BigDecimal(verSerie));
            }
            // Se devo visualizzare piu versioni mostor la ricerca, altrimenti il dettaglio serie
            boolean showRicercaSerie = false;
            if (getRequest().getParameterValues("VerSerie").length > 1) {
                showRicercaSerie = true;
            } else {
                BigDecimal idVerSerie = verSeries.get(0);
                if (getForm().getSerieList().getTable() != null) {
                    SerVRicSerieUdRowBean row = (SerVRicSerieUdRowBean) getForm().getSerieList()
                            .getTable().getCurrentRow();
                    if (row != null && !idVerSerie.equals(row.getIdVerSerie())) {
                        showRicercaSerie = true;
                    }
                } else {
                    SerVRicSerieUdTableBean table = serieEjb.getSerVRicSerieUdTableBean(verSeries,
                            getUser().getIdOrganizzazioneFoglia());
                    getForm().getSerieList().setTable(table);
                    getForm().getSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    getForm().getSerieList().getTable().first();
                }
            }

            if (showRicercaSerie) {
                loadRicercaSerie();

                SerVRicSerieUdTableBean table = serieEjb.getSerVRicSerieUdTableBean(verSeries,
                        getUser().getIdOrganizzazioneFoglia());
                getForm().getSerieList().setTable(table);
                getForm().getSerieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getSerieList().getTable().first();
                forwardToPublisher(Application.Publisher.RICERCA_SERIE_UD);
            } else {
                getSession().setAttribute("navTableSerie", getForm().getSerieList().getName());
                BigDecimal idVerSerie = verSeries.get(0);
                resetIdVerSerieDetailStack();
                pushIdVerSerieDetailStack(idVerSerie, getForm().getSerieList().getName(),
                        getForm().getSerieList().getTable(), 1);
                String primaryTab = getForm().getSerieDetailTabs().getCurrentTab().getName();
                String subTab = getForm().getSerieDetailSubTabs().getCurrentTab().getName();
                boolean versioneCorrente = serieEjb.checkVersione(idVerSerie, (String[]) null);
                loadDettaglioSerie(idVerSerie, versioneCorrente);
                if (versioneCorrente) {
                    getForm().getSerieDetail().getId_ver_serie_corr()
                            .setValue(idVerSerie.toPlainString());
                }
                getForm().getSerieDetailTabs()
                        .setCurrentTab(getForm().getSerieDetailTabs().getComponent(primaryTab));
                getForm().getSerieDetailSubTabs()
                        .setCurrentTab(getForm().getSerieDetailSubTabs().getComponent(subTab));

                forwardToPublisher(Application.Publisher.SERIE_UD_DETAIL);
            }
        }
    }

    @Secure(action = "Menu.Serie.ListaSerieDaFirmare")
    public void loadListaSerieDaFirmare() throws EMFError {
        /*
         * Controllo lo stato della history di navigazione se non ci sono pagine precedenti, vuol
         * dire che arrivo qui da un link del menu, se ci sono pagine allora devo passare alla jsp
         * l'id della struttura
         */
        boolean cleanList = false;
        if (getRequest().getParameter("cleanhistory") != null) {
            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.Serie.ListaSerieDaFirmare");
            // Rimuovo l'attributo perchè arrivo da un link del menu e non da una lista
            getSession().removeAttribute("navTableSerie");
            getSession().removeAttribute("idStrutRif");
            getSession().removeAttribute("isStrutNull");
            cleanList = true;
        }
        /* Ricavo Ambiente, Ente e Struttura da visualizzare */
        BigDecimal idStrut;
        if (getSession().getAttribute("idStrutRif") != null) {
            idStrut = (BigDecimal) getSession().getAttribute("idStrutRif");
        } else if (getSession().getAttribute("isStrutNull") != null) {
            idStrut = null;
        } else {
            idStrut = getUser().getIdOrganizzazioneFoglia();
            cleanList = true;
        }

        boolean cleanFilter = true;
        if (getRequest().getParameter("cleanFilter") != null) {
            cleanFilter = false;
        }

        if (idStrut != null && cleanFilter) {
            /* Ricavo Ambiente, Ente e Struttura CORRENTI */
            OrgStrutRowBean strutWithAmbienteEnte = evEjb
                    .getOrgStrutRowBeanWithAmbienteEnte(idStrut);
            /* Inizializza le combo dei filtri ambiente/ente/struttura CORRENTI */
            initFiltriSerieDaFirmare(strutWithAmbienteEnte);

            if (cleanList) {
                /*
                 * Carico la lista delle serie da firmare: quelli della struttura dell'utente e con
                 * stato DA_FIRMARE
                 */
                BaseTable serieDaFirmareBeanList = serieEjb.getSerieDaFirmareBeanList(
                        strutWithAmbienteEnte.getBigDecimal("id_ambiente"),
                        strutWithAmbienteEnte.getIdEnte(), strutWithAmbienteEnte.getIdStrut(),
                        CostantiDB.StatoVersioneSerie.DA_FIRMARE);
                getForm().getSerieDaFirmareList().setTable(serieDaFirmareBeanList);
                getForm().getSerieDaFirmareList().getTable().setPageSize(10);
                getForm().getSerieDaFirmareList().getTable().first();

                /* Inizializzo la lista delle serie selezionate */
                getForm().getSerieSelezionateList().setTable(new BaseTable());
                getForm().getSerieSelezionateList().getTable().setPageSize(10);
            }
        }

        /* Rendo visibili i bottoni delle operazioni sulla lista che mi interessano */
        getForm().getListaSerieDaFirmareButtonList().setEditMode();
        // Se non ci sono serie in stato FIRMATA_NO_MARCA, nascondo il bottone per "marcare"
        // if (!serieEjb.existsFirmataNoMarca(null)) {
        // getForm().getListaSerieDaFirmareButtonList().getMarcaturaIndiciAIPSerieButton().setViewMode();
        // }

        // Check if some signature session is active
        Future<Boolean> futureFirma = (Future<Boolean>) getSession()
                .getAttribute(Signature.FUTURE_ATTR_SERIE);
        if (serieSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())
                || futureFirma != null) {
            // Se esistono delle sessioni bloccate per quell'utente le sblocco
            if (serieSignSessionEjb.hasUserBlockedSessions(getUser().getIdUtente())) {
                // Sessione di firma bloccata
                serieSignSessionEjb.unlockBlockedSessions(getUser().getIdUtente());

                getForm().getListaSerieDaFirmareButtonList().getFirmaIndiciAIPSerieHsmButton()
                        .setReadonly(false);
                getMessageBox().addInfo("\u00C8 stata sbloccata una sessione di firma bloccata");
                getMessageBox().setViewMode(ViewMode.plain);
            } else {
                getForm().getListaSerieDaFirmareButtonList().getFirmaIndiciAIPSerieHsmButton()
                        .setReadonly(true);
                // Sessione di firma attiva
                getMessageBox().addInfo("Sessione di firma attiva");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getForm().getListaSerieDaFirmareButtonList().getFirmaIndiciAIPSerieHsmButton()
                    .setReadonly(false);
        }

        getForm().getListaSerieDaFirmareButtonList().getFirmaIndiciAIPSerieHsmButton()
                .setHidden(false);

        getSession().setAttribute("idStrutRif", idStrut);

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    /**
     * Inizializza i FILTRI DI LISTA SERIE DA FIRMARE in base alla struttura con la quale l'utente è
     * loggato
     *
     * @param strutWithAmbienteEnte entity {@link OrgStrutRowBean}
     *
     * @throws EMFError errore generico
     */
    private void initFiltriSerieDaFirmare(OrgStrutRowBean strutWithAmbienteEnte) {
        // Azzero i filtri
        getForm().getFiltriSerieDaFirmare().reset();

        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idAmbiente = strutWithAmbienteEnte.getBigDecimal("id_ambiente");
        BigDecimal idEnte = strutWithAmbienteEnte.getIdEnte();
        BigDecimal idStrut = strutWithAmbienteEnte.getIdStrut();

        // Inizializzo le combo settando la struttura corrente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        OrgEnteTableBean tmpTableBeanEnte = null;
        OrgStrutTableBean tmpTableBeanStruttura = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            log.error("Errore durante il recupero dei filtri Ambiente - Ente - Struttura", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriSerieDaFirmare().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriSerieDaFirmare().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriSerieDaFirmare().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriSerieDaFirmare().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriSerieDaFirmare().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriSerieDaFirmare().getId_strut().setValue(idStrut.toString());

        // Imposto i filtri in editMode
        getForm().getFiltriSerieDaFirmare().setEditMode();

        // Imposto come visibile il bottone di ricerca e disabilito la clessidra (per IE)
        getForm().getFiltriSerieDaFirmare().getRicercaSerieDaFirmareButton().setEditMode();
        getForm().getFiltriSerieDaFirmare().getRicercaSerieDaFirmareButton()
                .setDisableHourGlass(true);
    }

    @Override
    public JSONObject triggerFiltriSerieDaFirmareId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriSerieDaFirmare().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriSerieDaFirmare().getId_ente().setValue("");
        getForm().getFiltriSerieDaFirmare().getId_strut().setValue("");

        BigDecimal idAmbiente = getForm().getFiltriSerieDaFirmare().getId_ambiente().parse();
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getFiltriSerieDaFirmare().getId_ente().setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getFiltriSerieDaFirmare().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte());
            } else {
                getForm().getFiltriSerieDaFirmare().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriSerieDaFirmare().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriSerieDaFirmare().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriSerieDaFirmare().asJSON();
    }

    @Override
    public JSONObject triggerFiltriSerieDaFirmareId_enteOnTrigger() throws EMFError {
        getForm().getFiltriSerieDaFirmare().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriSerieDaFirmare().getId_strut().setValue("");

        BigDecimal idEnte = getForm().getFiltriSerieDaFirmare().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriSerieDaFirmare().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriSerieDaFirmare().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            getForm().getFiltriSerieDaFirmare().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriSerieDaFirmare().asJSON();
    }

    public void checkUniqueEnteInCombo(BigDecimal idEnte) {
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.FALSE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriSerieDaFirmare().getId_strut().setDecodeMap(mappaStrut);

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di
            // essa
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriSerieDaFirmare().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        }
    }

    @Override
    public void ricercaSerieDaFirmareButton() throws EMFError {
        getForm().getFiltriSerieDaFirmare().post(getRequest());
        /* Ricavo Ambiente, Ente e Struttura CORRENTI */
        BigDecimal idAmbiente = getForm().getFiltriSerieDaFirmare().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriSerieDaFirmare().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriSerieDaFirmare().getId_strut().parse();

        getSession().setAttribute("idStrutRif", idStrut);
        if (idStrut == null) {
            // Rimuovo l'attributo idStrutRif /se presente in sessione vuol dire che si riferisce ad
            // una struttura
            // selezionata precedentemente
            getSession().removeAttribute("idStrutRif");
            // Traccio in sessione un attributo specifico
            getSession().setAttribute("isStrutNull", true);
        }

        BaseTable serieDaFirmareBeanList = serieEjb.getSerieDaFirmareBeanList(idAmbiente, idEnte,
                idStrut, CostantiDB.StatoVersioneSerie.DA_FIRMARE);
        getForm().getSerieDaFirmareList().setTable(serieDaFirmareBeanList);
        getForm().getSerieDaFirmareList().getTable().setPageSize(10);
        getForm().getSerieDaFirmareList().getTable().first();
        /* Rengo visibili i bottoni delle operazioni sulla lista che mi interessano */
        getForm().getListaSerieDaFirmareButtonList().setEditMode();
        // Se non ci sono serie in stato FIRMATA_NO_MARCA, nascondo il bottone per "marcare"
        // if (!serieEjb.existsFirmataNoMarca(null)) {
        // getForm().getListaSerieDaFirmareButtonList().getMarcaturaIndiciAIPSerieButton().setViewMode();
        // }

        /* Inizializzo la lista delle serie selezionate */
        getForm().getSerieSelezionateList().setTable(new BaseTable());
        getForm().getSerieSelezionateList().getTable().setPageSize(10);

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    @Override
    public void selectAllSerieButton() throws EMFError {
        BaseTable serieList = (BaseTable) getForm().getSerieDaFirmareList().getTable();
        for (BaseRow serie : serieList) {
            getForm().getSerieSelezionateList().getTable().add(serie);
        }
        serieList.removeAll();
        getForm().getSerieSelSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    @Override
    public void deselectAllSerieButton() throws EMFError {
        BaseTable serieList = (BaseTable) getForm().getSerieSelezionateList().getTable();
        for (BaseRow serie : serieList) {
            getForm().getSerieDaFirmareList().getTable().add(serie);
        }
        serieList.removeAll();
        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    // @Override
    // public void marcaturaIndiciAIPSerieButton() throws EMFError {
    // long idUtente = SessionManager.getUser(getSession()).getIdUtente();
    // try {
    // int marcati = serieEjb.marcaturaIndici(idUtente);
    // if (marcati > 0) {
    // getMessageBox().addMessage(new Message(MessageLevel.INF,
    // "Marcatura eseguita correttamente: marcati tutti i " + marcati + " indici AIP versione
    // serie"));
    // getMessageBox().setViewMode(ViewMode.plain);
    // }
    // } catch (ParerUserError ex) {
    // /* Se non ho marcato tutti gli indici mostro un warning */
    // getMessageBox().addMessage(new Message(MessageLevel.WAR, ex.getDescription()));
    // getMessageBox().setViewMode(ViewMode.plain);
    // }
    // forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    // }

    @Override
    public void tabInfoPrincipaliVolumeOnClick() throws EMFError {
        getForm().getVolumeDetailTabs()
                .setCurrentTab(getForm().getVolumeDetailTabs().getInfoPrincipaliVolume());
        forwardToPublisher(Application.Publisher.VOLUME_SERIE_UD_DETAIL);
    }

    @Override
    public void tabIndiceVolumeOnClick() throws EMFError {
        getForm().getVolumeDetailTabs()
                .setCurrentTab(getForm().getVolumeDetailTabs().getIndiceVolume());
        forwardToPublisher(Application.Publisher.VOLUME_SERIE_UD_DETAIL);
    }

    @Override
    public void ricercaContenutoSuVolume() throws EMFError {
        if (getForm().getFiltriContenutoSerieDetail().postAndValidate(getRequest(),
                getMessageBox())) {
            log.info("Carico la lista in base ai parametri");
            RicercaUdAppartBean parametri = initParametriRicercaContenuto(
                    getForm().getVolumeDetail().getAa_serie().parse());
            int lastPageSize = getForm().getUdVolumeList().getTable().getPageSize();

            SerVLisUdAppartVolSerieTableBean udTb = serieEjb.getSerVLisUdAppartVolSerieTableBean(
                    getForm().getVolumeDetail().getId_vol_ver_serie().parse(), parametri);
            getForm().getUdVolumeList().setTable(udTb);
            getForm().getUdVolumeList().getTable().setPageSize(lastPageSize);
            getForm().getUdVolumeList().getTable().first();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void downloadIxVol() throws EMFError {
        BigDecimal idVolumeVerSerie = getForm().getVolumeDetail().getId_vol_ver_serie().parse();

        try {
            // Ricavo il file da scaricare
            String[] nameAndFileVolume = serieEjb
                    .getNameAndFileIndiceVolume(idVolumeVerSerie.longValue());
            String nomeFileVolume = nameAndFileVolume[0];
            String fileVolume = nameAndFileVolume[1];

            // Nome del file IndiceAIPSerieUD-<versione serie>_<ambiente>_<ente>_<struttura>_<codice
            // serie>”
            getResponse().setContentType(
                    it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getContentType());
            getResponse().setHeader("Content-Disposition",
                    "attachment; filename=\"" + nomeFileVolume + ".xml\"");

            // Caccio dentro nello zippone il blobbo
            if (fileVolume != null) {
                try (InputStream is = new ByteArrayInputStream(fileVolume.getBytes());
                        OutputStream out = getServletOutputStream()) { // Try with resources
                    byte[] data = new byte[1024];
                    int count;

                    while ((count = is.read(data, 0, 1024)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.flush();
                } // Gli stream vengono chiusi automaticamente
            }

        } catch (ParerUserError ex) {
            log.error("Errore nel recupero del file XML relativo all'indice del volume: {}",
                    ex.getMessage());
            getMessageBox()
                    .addError("Errore nel recupero del file XML relativo all'indice del volume");
        } catch (IOException e) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Errore nel recupero del file XML relativo all'indice del volume "));
            log.error("Eccezione", e);
        } finally {
            if (!getMessageBox().hasError()) {
                freeze();
            }
            if (getMessageBox().hasError()) {
                forwardToPublisher(Application.Publisher.VOLUME_SERIE_UD_DETAIL);
            }
        }

    }

    /**
     * Bottone "+" della "Lista serie da firmare" per spostare una serie da questa lista a quella
     * delle serie selezionate pronte per essere firmate
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectSerieDaFirmareList() throws EMFError {
        /* Ricavo il record interessato della "Lista serie da firmare" */
        BaseRow row = (BaseRow) getForm().getSerieDaFirmareList().getTable().getCurrentRow();
        int index = getForm().getSerieDaFirmareList().getTable().getCurrentRowIndex();
        /* Lo tolgo dalla lista serie da firmare */
        getForm().getSerieDaFirmareList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getSerieDaFirmareList().getTable().getCurrentPageIndex();
        int inizio = getForm().getSerieDaFirmareList().getTable().getFirstRowPageIndex();
        this.lazyLoadGoPage(getForm().getSerieDaFirmareList(), paginaCorrente);
        getForm().getSerieDaFirmareList().getTable().setCurrentRowIndex(inizio);
        /* Aggiungo il record nella lista delle serie selezionate */
        getForm().getSerieSelSection().setLoadOpened(true);
        getForm().getSerieSelezionateList().add(row);
        getForm().getSerieSelezionateList().getTable().addSortingRule("nm_ambiente",
                SortingRule.ASC);
        getForm().getSerieSelezionateList().getTable().addSortingRule("nm_ente", SortingRule.ASC);
        getForm().getSerieSelezionateList().getTable().addSortingRule("nm_strut", SortingRule.ASC);
        getForm().getSerieSelezionateList().getTable().addSortingRule("cd_composito_serie",
                SortingRule.ASC);
        getForm().getSerieSelezionateList().getTable().sort();
        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    /**
     * Bottone "-" della "Lista serie selezionate" per spostare un elenco da questa lista a quella
     * delle serie da firmare
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectSerieSelezionateList() throws EMFError {
        /* Ricavo il record interessato della "Lista serie selezionate" */
        BaseRow row = (BaseRow) getForm().getSerieSelezionateList().getTable().getCurrentRow();
        int index = getForm().getSerieSelezionateList().getTable().getCurrentRowIndex();
        /* Lo tolgo dalla lista eserie selezionate */
        getForm().getSerieSelezionateList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getSerieSelezionateList().getTable().getCurrentPageIndex();
        int inizio = getForm().getSerieSelezionateList().getTable().getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getSerieSelezionateList(), paginaCorrente);
        // Ritorno alla pagina
        getForm().getSerieSelezionateList().getTable().setCurrentRowIndex(inizio);

        // Pagina Volumi da firmare
        getForm().getSerieDaFirmareList().add(row);
        int paginaCorrenteVF = getForm().getSerieDaFirmareList().getTable().getCurrentPageIndex();
        int inizioVF = getForm().getSerieDaFirmareList().getTable().getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getSerieDaFirmareList(), paginaCorrenteVF);
        // Ritorno alla pagina
        getForm().getSerieDaFirmareList().getTable().setCurrentRowIndex(inizioVF);

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    @Override
    public void downloadPacchettoArk() throws EMFError {
        BigDecimal idSerie = getForm().getSerieDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getSerieDetail().getId_ver_serie_corr().parse();
        String cdVersioneSerie = getForm().getSerieDetail().getCd_ver_serie().parse();
        String cdSerie = getForm().getSerieDetail().getCd_composito_serie().parse();
        String[] ambEnteStrutSerie = serieEjb.ambienteEnteStrutturaSerie(idSerie);
        String filename = ambEnteStrutSerie[0] + "_" + ambEnteStrutSerie[1] + "_"
                + ambEnteStrutSerie[2] + "_" + cdSerie + ".zip";
        String aipFirmato = "IndiceAIPSerieUD-" + cdVersioneSerie + "_" + ambEnteStrutSerie[0] + "_"
                + ambEnteStrutSerie[1] + "_" + ambEnteStrutSerie[2] + "_" + cdSerie;
        String volIx = "IndiceVolumeSerie_" + ambEnteStrutSerie[0] + "_" + ambEnteStrutSerie[1]
                + "_" + ambEnteStrutSerie[2] + "_" + cdSerie + "-";

        if (!getMessageBox().hasError()) {
            File tmpFile = new File(System.getProperty("java.io.tmpdir"), filename);

            try (FileOutputStream fileOs = new FileOutputStream(tmpFile);
                    ZipOutputStream out = new ZipOutputStream(fileOs)) {

                serieEjb.createZipPacchettoArk(idVerSerie, out, aipFirmato, volIx);

                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(),
                        getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                        tmpFile.getName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                        tmpFile.getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                        "application/zip");
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            } catch (IOException ex) {
                log.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
                getMessageBox().addError(ERRORE_DOWNLOAD_MESSAGE);
            } finally {
                // MAC 37610: Rimossa la parte di codice che chiudeva i flussi nel blocco finally
                // poiché il
                // try-with-resources si occupa automaticamente della chiusura degli stream
                if (getMessageBox().hasError()) {
                    forwardToPublisher(getLastPublisher());
                } else {
                    forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
                }
            }
        }

    }

    @Override
    public void loadTipoSerieDetail() throws EMFError {
        BigDecimal idTipoSerie = getForm().getSerieDetail().getId_tipo_serie().parse();
        DecTipoSerieTableBean tipoSerieTableBean = new DecTipoSerieTableBean();
        tipoSerieTableBean.add(tipoSerieEjb.getDecTipoSerieRowBean(idTipoSerie));

        StrutSerieForm form = new StrutSerieForm();
        OrgStrutRowBean strutRow = struttureEjb
                .getOrgStrutRowBean(getUser().getIdOrganizzazioneFoglia(), null);
        form.getStrutRif().getId_strut()
                .setValue(getUser().getIdOrganizzazioneFoglia().toPlainString());
        form.getStrutRif().getStruttura()
                .setValue(strutRow.getNmStrut() + " - " + strutRow.getDsStrut());
        form.getStrutRif().getId_ente().setValue(getUser().getOrganizzazioneMap().get("ENTE"));

        form.getTipologieSerieList().setTable(tipoSerieTableBean);
        redirectToAction(Application.Actions.STRUT_SERIE,
                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW
                        + "&table=" + form.getTipologieSerieList().getName() + "&riga=0",
                form);
    }

    private List<VerSerieDetailBean> pushIdVerSerieDetailStack(BigDecimal idVerSerieDetail,
            String sourceList, BaseTableInterface<?> sourceTable, int level) {
        List<VerSerieDetailBean> idVerSerieDetailStack = (List<VerSerieDetailBean>) getSession()
                .getAttribute(WebConstants.PARAMETER_VER_SERIE);
        if (idVerSerieDetailStack == null) {
            idVerSerieDetailStack = new ArrayList<>();
        }
        idVerSerieDetailStack
                .add(new VerSerieDetailBean(idVerSerieDetail, sourceList, sourceTable, level));
        getSession().setAttribute(WebConstants.PARAMETER_VER_SERIE, idVerSerieDetailStack);

        return idVerSerieDetailStack;
    }

    private VerSerieDetailBean popIdVerSerieDetailStack() {
        VerSerieDetailBean last = null;
        List<VerSerieDetailBean> idVerSerieDetailStack = (List<VerSerieDetailBean>) getSession()
                .getAttribute(WebConstants.PARAMETER_VER_SERIE);
        if (idVerSerieDetailStack != null && !idVerSerieDetailStack.isEmpty()) {
            last = idVerSerieDetailStack.remove(idVerSerieDetailStack.size() - 1);
        }
        return last;
    }

    private VerSerieDetailBean getLastIdVerSerieDetailStack() {
        VerSerieDetailBean last = null;
        List<VerSerieDetailBean> idVerSerieDetailStack = (List<VerSerieDetailBean>) getSession()
                .getAttribute(WebConstants.PARAMETER_VER_SERIE);
        if (idVerSerieDetailStack != null && !idVerSerieDetailStack.isEmpty()) {
            last = idVerSerieDetailStack.get(idVerSerieDetailStack.size() - 1);
        }
        return last;
    }

    private void resetIdVerSerieDetailStack() {
        getSession().removeAttribute(WebConstants.PARAMETER_VER_SERIE);
    }

    /*
     * GESTIONE SERIE DA VALIDARE
     */
    @Secure(action = "Menu.Serie.ListaSerieDaValidare")
    public void loadListaSerieDaValidare() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.ListaSerieDaValidare");

        getSession().removeAttribute("navTableSerie");

        /* Ricavo Ambiente, Ente e Struttura CORRENTI */
        OrgStrutRowBean strutWithAmbienteEnte = evEjb
                .getOrgStrutRowBeanWithAmbienteEnte(getUser().getIdOrganizzazioneFoglia());

        /* Inizializza le combo dei filtri ambiente/ente/struttura CORRENTI */
        initFiltriSerieDaFirmare(strutWithAmbienteEnte);
        try {
            /* Carico la lista delle serie da validare */
            SerVLisSerDaValidareTableBean serieDaValidare = serieEjb
                    .getSerVLisSerDaValidareTableBean(getUser().getIdUtente(),
                            strutWithAmbienteEnte.getBigDecimal("id_ambiente"),
                            strutWithAmbienteEnte.getIdEnte(), strutWithAmbienteEnte.getIdStrut());
            getForm().getSerieDaValidareList().setTable(serieDaValidare);
            getForm().getSerieDaValidareList().getTable().setPageSize(10);
            getForm().getSerieDaValidareList().getTable().first();
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        /* Inizializzo la lista delle serie selezionate */
        getForm().getSerieSelezionateDaValidareList().setTable(new SerVLisSerDaValidareTableBean());
        getForm().getSerieSelezionateDaValidareList().getTable().setPageSize(10);

        getForm().getSerieSelezionateDaValidareList().getTable().addSortingRule("nm_ambiente",
                SortingRule.ASC);
        getForm().getSerieSelezionateDaValidareList().getTable().addSortingRule("nm_ente",
                SortingRule.ASC);
        getForm().getSerieSelezionateDaValidareList().getTable().addSortingRule("nm_strut",
                SortingRule.ASC);
        getForm().getSerieSelezionateDaValidareList().getTable()
                .addSortingRule("cd_composito_serie", SortingRule.ASC);
        /* Rendo visibili i bottoni delle operazioni sulla lista */
        getForm().getListaSerieDaValidareButtonList().setEditMode();

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_VALIDARE_SELECT);
    }

    @Override
    public void ricercaSerieDaValidareButton() throws EMFError {
        getForm().getFiltriSerieDaFirmare().post(getRequest());
        /* Ricavo Ambiente, Ente e Struttura CORRENTI */
        BigDecimal idAmbiente = getForm().getFiltriSerieDaFirmare().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriSerieDaFirmare().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriSerieDaFirmare().getId_strut().parse();

        try {
            SerVLisSerDaValidareTableBean serieDaValidare = serieEjb
                    .getSerVLisSerDaValidareTableBean(getUser().getIdUtente(), idAmbiente, idEnte,
                            idStrut);
            getForm().getSerieDaValidareList().setTable(serieDaValidare);
            getForm().getSerieDaValidareList().getTable().setPageSize(10);
            getForm().getSerieDaValidareList().getTable().first();

            getForm().getSerieDaValidareList().getTable().addSortingRule("nm_ambiente",
                    SortingRule.ASC);
            getForm().getSerieDaValidareList().getTable().addSortingRule("nm_ente",
                    SortingRule.ASC);
            getForm().getSerieDaValidareList().getTable().addSortingRule("nm_strut",
                    SortingRule.ASC);
            getForm().getSerieDaValidareList().getTable().addSortingRule("cd_composito_serie",
                    SortingRule.ASC);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_VALIDARE_SELECT);
    }

    @Override
    public void selectAllSerieDaValidareButton() throws EMFError {
        BaseTableInterface<? extends BaseRowInterface> serieList = getForm()
                .getSerieDaValidareList().getTable();
        for (BaseRowInterface serie : serieList) {
            getForm().getSerieSelezionateDaValidareList().getTable().add(serie);
        }
        serieList.removeAll();
        getForm().getSerieSelValidazioneSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_VALIDARE_SELECT);
    }

    @Override
    public void deselectAllSerieDaValidareButton() throws EMFError {
        BaseTableInterface<? extends BaseRowInterface> serieList = getForm()
                .getSerieSelezionateDaValidareList().getTable();
        for (BaseRowInterface serie : serieList) {
            getForm().getSerieDaValidareList().getTable().add(serie);
        }
        serieList.removeAll();
        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_VALIDARE_SELECT);
    }

    @Override
    public void selectSerieSelezionateDaValidareList() throws EMFError {
        /* Ricavo il record interessato della "Lista serie selezionate" */
        BaseRowInterface row = getForm().getSerieSelezionateDaValidareList().getTable()
                .getCurrentRow();
        int index = getForm().getSerieSelezionateDaValidareList().getTable().getCurrentRowIndex();
        /* Lo tolgo dalla lista serie da firmare */
        getForm().getSerieSelezionateDaValidareList().getTable().remove(index);
        /* Aggiungo il record nella lista delle serie selezionate */
        getForm().getSerieDaValidareList().add(row);
        getForm().getSerieDaValidareList().getTable().sort();

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_VALIDARE_SELECT);
    }

    @Override
    public void selectSerieDaValidareList() throws EMFError {
        /* Ricavo il record interessato della "Lista serie da validare" */
        BaseRowInterface row = getForm().getSerieDaValidareList().getTable().getCurrentRow();
        int index = getForm().getSerieDaValidareList().getTable().getCurrentRowIndex();
        /* Lo tolgo dalla lista serie da validare */
        getForm().getSerieDaValidareList().getTable().remove(index);
        /* Aggiungo il record nella lista delle serie selezionate */
        getForm().getSerieSelValidazioneSection().setLoadOpened(true);
        getForm().getSerieSelezionateDaValidareList().add(row);
        getForm().getSerieSelezionateDaValidareList().getTable().sort();

        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_VALIDARE_SELECT);
    }

    @Override
    public void validaSerie() throws EMFError {
        String tipoOperazione = "VALIDA_SERIE";
        if (getForm().getSerieSelezionateDaValidareList().getTable().isEmpty()) {
            getMessageBox().addError("Selezionare almeno una serie da validare");
        } else {
            SerVLisSerDaValidareTableBean table = (SerVLisSerDaValidareTableBean) getForm()
                    .getSerieSelezionateDaValidareList().getTable();
            // Se ho richiesto di validare solo una serie, chiamo il metodo asincrono di validazione
            // singola, altrimenti
            // quello multiplo
            try {
                if (table.size() == 1) {
                    SerVLisSerDaValidareRowBean row = table.getRow(0);
                    serieEjb.cambiaStatoSerie(getUser().getIdUtente(), row.getIdSerie(),
                            row.getIdVerSerie(), row.getIdContenutoVerSerie(),
                            SerieEjb.AZIONE_SERIE_VALIDAZIONE_IN_CORSO,
                            CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name(), null,
                            CostantiDB.StatoVersioneSerie.DA_VALIDARE.name(), tipoOperazione);

                    String keyFuture = FutureUtils.buildKeyFuture(
                            CostantiDB.TipoChiamataAsync.VALIDAZIONE_SERIE.name(),
                            row.getCdCompositoSerie(), row.getAaSerie(),
                            getUser().getIdOrganizzazioneFoglia(), row.getIdVerSerie().longValue());
                    Future<String> future = serieEjb.callValidazioneSerieAsync(row.getIdVerSerie());
                    FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture,
                            future);

                    getMessageBox().addInfo(
                            "La validazione della serie \u00E8 stata schedulata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } else {
                    Map<String, BigDecimal> idVerSeries = new HashMap<>();
                    for (SerVLisSerDaValidareRowBean row : table) {
                        serieEjb.cambiaStatoSerie(getUser().getIdUtente(), row.getIdSerie(),
                                row.getIdVerSerie(), row.getIdContenutoVerSerie(),
                                SerieEjb.AZIONE_SERIE_VALIDAZIONE_IN_CORSO,
                                CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name(), null,
                                CostantiDB.StatoVersioneSerie.DA_VALIDARE.name(), tipoOperazione);

                        String keyFuture = FutureUtils.buildKeyFuture(
                                CostantiDB.TipoChiamataAsync.VALIDAZIONE_SERIE.name(),
                                row.getCdCompositoSerie(), row.getAaSerie(),
                                getUser().getIdOrganizzazioneFoglia(),
                                row.getIdVerSerie().longValue());
                        idVerSeries.put(keyFuture, row.getIdVerSerie());
                    }

                    Future<Map<String, ?>> futures = serieEjb
                            .callValidazioneSerieAsync(idVerSeries);
                    FutureUtils.putFuturesInMap(getSession(), getSession().getId(), futures);

                    getMessageBox().addInfo(
                            "La validazione delle serie \u00E8 stata schedulata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError()) {
            getForm().getSerieSelezionateDaValidareList().getTable().removeAll();
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaSerie().getId_ente().setValue("");
        getForm().getFiltriRicercaSerie().getId_strut().setValue("");
        getForm().getFiltriRicercaSerie().getNm_tipo_serie().setValue("");

        BigDecimal idAmbiente = getForm().getFiltriRicercaSerie().getId_ambiente().parse();
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getFiltriRicercaSerie().getId_ente().setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getFiltriRicercaSerie().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                BigDecimal idEnte = tmpTableBeanEnte.getRow(0).getIdEnte();
                if (idEnte != null) {
                    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
                    OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                            .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.FALSE);
                    DecodeMap mappaStrut = new DecodeMap();
                    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
                    getForm().getFiltriRicercaSerie().getId_strut().setDecodeMap(mappaStrut);

                    // Se la combo struttura ha un solo valore presente, lo imposto e faccio
                    // controllo su di essa
                    if (tmpTableBeanStrut.size() == 1) {
                        getForm().getFiltriRicercaSerie().getId_strut()
                                .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    }
                }
            } else {
                getForm().getFiltriRicercaSerie().getId_strut().setDecodeMap(new DecodeMap());
                getForm().getFiltriRicercaSerie().getNm_tipo_serie().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaSerie().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSerie().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSerie().getNm_tipo_serie().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaSerie().getId_strut().setValue("");
        getForm().getFiltriRicercaSerie().getNm_tipo_serie().setValue("");

        BigDecimal idEnte = getForm().getFiltriRicercaSerie().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriRicercaSerie().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaSerie().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            getForm().getFiltriRicercaSerie().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSerie().getNm_tipo_serie().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieId_strutOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaSerie().getNm_tipo_serie().setValue("");

        BigDecimal idStrut = getForm().getFiltriRicercaSerie().getId_strut().parse();
        if (idStrut != null) {
            final DecTipoSerieTableBean tipiSerie = tipoSerieEjb.getDecTipoSerieDaCreareTableBean(
                    idStrut, CostantiDB.TipoContenSerie.UNITA_DOC.name(), false);
            DecodeMap tipoSerie = DecodeMap.Factory.newInstance(tipiSerie, "id_tipo_serie",
                    getForm().getFiltriRicercaSerie().getNm_tipo_serie().getName());
            getForm().getFiltriRicercaSerie().getNm_tipo_serie().setDecodeMap(tipoSerie);

            // Se ho una sola struttura la setto già impostata nella combo
            if (tipiSerie.size() == 1) {
                getForm().getFiltriRicercaSerie().getNm_tipo_serie()
                        .setValue(tipiSerie.getRow(0).getIdTipoSerie().toString());
            }
        } else {
            getForm().getFiltriRicercaSerie().getNm_tipo_serie().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaSerie().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSerieTi_crea_standardOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSerie().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriRicercaSerie().getId_ambiente().parse();
        String tiCreaStandard = getForm().getFiltriRicercaSerie().getTi_crea_standard().parse();
        if (StringUtils.isNotBlank(tiCreaStandard) && idAmbiente != null) {
            getForm().getFiltriRicercaSerie().getId_modello_tipo_serie()
                    .setDecodeMap(
                            DecodeMap.Factory.newInstance(
                                    modelliEjb.getDecModelloTipoSerieAbilitatiAmbienteTableBean(
                                            idAmbiente, true),
                                    "id_modello_tipo_serie", "nm_modello_tipo_serie"));
        } else {
            getForm().getFiltriRicercaSerie().getId_modello_tipo_serie()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaSerie().asJSON();
    }

    private void initFiltriStrut(String fieldSetName) {
        /* Ricavo Ambiente, Ente e Struttura CORRENTI */
        OrgStrutRowBean strutWithAmbienteEnte = evEjb
                .getOrgStrutRowBeanWithAmbienteEnte(getUser().getIdOrganizzazioneFoglia());
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idAmbiente = strutWithAmbienteEnte.getBigDecimal("id_ambiente");
        BigDecimal idEnte = strutWithAmbienteEnte.getIdEnte();
        BigDecimal idStrut = strutWithAmbienteEnte.getIdStrut();

        // Inizializzo le combo settando la struttura corrente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        OrgEnteTableBean tmpTableBeanEnte = null;
        OrgStrutTableBean tmpTableBeanStruttura = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            log.error("Errore durante il recupero dei filtri Ambiente - Ente - Struttura", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        ComboBox<BigDecimal> comboAmbiente = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_ambiente"));
        comboAmbiente.setDecodeMap(mappaAmbiente);
        comboAmbiente.setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        ComboBox<BigDecimal> comboEnte = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_ente"));
        comboEnte.setDecodeMap(mappaEnte);
        comboEnte.setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        ComboBox<BigDecimal> comboStrut = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_strut"));
        comboStrut.setDecodeMap(mappaStrut);
        comboStrut.setValue(idStrut.toString());
    }

    // <editor-fold defaultstate="collapsed" desc="Controllo contenuto">
    @Override
    public void controllaContenutoCalcConsistAttesa() throws Throwable {
        BigDecimal idSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_ver_serie()
                .parse();
        String cdSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getCd_composito_serie()
                .parse();
        BigDecimal aaSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getAa_serie().parse();
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        controllaContenuto(idVerSerie, idSerie, cdSerie, aaSerie, idStrut,
                CostantiDB.TipoContenutoVerSerie.CALCOLATO.name());
        getForm().getConsistenzaButtonList().getControllaContenutoCalcConsistAttesa()
                .setHidden(true);
        forwardToPublisher(Application.Publisher.CONSISTENZA_ATTESA_DETAIL);
    }

    @Override
    public void controllaContenutoAcqConsistAttesa() throws Throwable {
        BigDecimal idSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_ver_serie()
                .parse();
        String cdSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getCd_composito_serie()
                .parse();
        BigDecimal aaSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getAa_serie().parse();
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        controllaContenuto(idVerSerie, idSerie, cdSerie, aaSerie, idStrut,
                CostantiDB.TipoContenutoVerSerie.ACQUISITO.name());
        getForm().getConsistenzaButtonList().getControllaContenutoAcqConsistAttesa()
                .setHidden(true);
        forwardToPublisher(Application.Publisher.CONSISTENZA_ATTESA_DETAIL);
    }

    @Override
    public void controllaContenutoEffConsistAttesa() throws Throwable {
        BigDecimal idSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_ver_serie()
                .parse();
        String cdSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getCd_composito_serie()
                .parse();
        BigDecimal aaSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getAa_serie().parse();
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        controllaContenuto(idVerSerie, idSerie, cdSerie, aaSerie, idStrut,
                CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
        getForm().getConsistenzaButtonList().getControllaContenutoEffConsistAttesa()
                .setHidden(true);
        forwardToPublisher(Application.Publisher.CONSISTENZA_ATTESA_DETAIL);
    }

    private void controllaContenuto(BigDecimal idVerSerie, BigDecimal idSerie, String cdSerie,
            BigDecimal aaSerie, BigDecimal idStrut, String tipoContenuto) {
        log.info("Inviata richiesta di controllo contenuto {}", tipoContenuto);
        try {
            String keyFuture = FutureUtils.buildKeyFuture(
                    CostantiDB.TipoChiamataAsync.CONTROLLO_CONTENUTO.name(), cdSerie, aaSerie,
                    idStrut, idVerSerie.longValue());

            serieEjb.initControlloContenuto(idSerie, idVerSerie, tipoContenuto);
            Future<String> future = serieEjb.callControlloContenutoAsync(getUser().getIdUtente(),
                    idVerSerie.longValue(), tipoContenuto);
            FutureUtils.putFutureInMap(getSession(), getSession().getId(), keyFuture, future);

            getMessageBox().addInfo("Controllo del contenuto " + tipoContenuto
                    + " della serie lanciato con successo");
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Comunicazione consistenza attesa">
    @Secure(action = "Menu.Serie.ComunicazioneConsistenza")
    public void loadComunicazioneConsistenza() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Serie.ComunicazioneConsistenza");

        getForm().getFiltriComunicazioneConsistenzaSerieUD().reset();
        getForm().getFiltriComunicazioneConsistenzaSerieUD().setEditMode();

        initFiltriStrut(getForm().getFiltriComunicazioneConsistenzaSerieUD().getName());

        DecodeMap tipoSerie = DecodeMap.Factory.newInstance(
                tipoSerieEjb.getDecTipoSerieDaCreareTableBean(getUser().getIdOrganizzazioneFoglia(),
                        CostantiDB.TipoContenSerie.UNITA_DOC.name(), false),
                "id_tipo_serie",
                getForm().getFiltriComunicazioneConsistenzaSerieUD().getNm_tipo_serie().getName());
        getForm().getFiltriComunicazioneConsistenzaSerieUD().getNm_tipo_serie()
                .setDecodeMap(tipoSerie);
        int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
        BigDecimal anno = new BigDecimal(annoCorrente);
        getForm().getFiltriComunicazioneConsistenzaSerieUD().getAa_serie_a()
                .setValue(anno.toPlainString());
        getForm().getFiltriComunicazioneConsistenzaSerieUD().getFl_presenza_consist_attesa()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriComunicazioneConsistenzaSerieUD().getId_tipo_unita_doc()
                .setDecodeMap(DecodeMap.Factory.newInstance(
                        tipoSerieEjb.getDecTipoUnitaDocTableBeanFromTipoSerie(
                                getUser().getIdOrganizzazioneFoglia()),
                        "id_tipo_unita_doc", "nm_tipo_unita_doc"));
        getForm().getFiltriComunicazioneConsistenzaSerieUD().getId_registro_unita_doc()
                .setDecodeMap(DecodeMap.Factory.newInstance(
                        tipoSerieEjb.getDecRegistroUnitaDocTableBeanFromTipoSerie(
                                getUser().getIdOrganizzazioneFoglia()),
                        "id_registro_unita_doc", "cd_registro_unita_doc"));

        getForm().getConsistenzaSerieList().setTable(null);

        forwardToPublisher(Application.Publisher.COMUNICAZIONE_CONSISTENZA_SERIE_UD);
    }

    @Override
    public JSONObject triggerFiltriComunicazioneConsistenzaSerieUDId_ambienteOnTrigger()
            throws EMFError {
        getForm().getFiltriComunicazioneConsistenzaSerieUD().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerAmbienteGenerico(getForm().getFiltriComunicazioneConsistenzaSerieUD(),
                getUser().getIdUtente(), Boolean.TRUE);
        return getForm().getFiltriComunicazioneConsistenzaSerieUD().asJSON();
    }

    @Override
    public JSONObject triggerFiltriComunicazioneConsistenzaSerieUDId_enteOnTrigger()
            throws EMFError {
        getForm().getFiltriComunicazioneConsistenzaSerieUD().getId_ente().post(getRequest());
        ActionUtils utile = new ActionUtils();
        utile.triggerEnteGenerico(getForm().getFiltriComunicazioneConsistenzaSerieUD(),
                getUser().getIdUtente(), Boolean.TRUE);
        return getForm().getFiltriComunicazioneConsistenzaSerieUD().asJSON();
    }

    @Override
    public void ricercaConsistenzaSerie() throws EMFError {
        if (getForm().getFiltriComunicazioneConsistenzaSerieUD().postAndValidate(getRequest(),
                getMessageBox())) {
            RicercaSerieBean filtri = new RicercaSerieBean(
                    getForm().getFiltriComunicazioneConsistenzaSerieUD());
            if (filtri.getAa_serie_a() == null) {
                int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
                BigDecimal anno = new BigDecimal(annoCorrente - 1);
                filtri.setAa_serie_a(anno);

                getForm().getFiltriRicercaSerie().getAa_serie_a().setValue(anno.toPlainString());
            }
            if (!getMessageBox().hasError()) {
                SerVRicConsistSerieUdTableBean table = serieEjb
                        .getSerVRicConsistSerieUdTableBean(filtri);
                getForm().getConsistenzaSerieList().setTable(table);
                getForm().getConsistenzaSerieList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getConsistenzaSerieList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.COMUNICAZIONE_CONSISTENZA_SERIE_UD);
    }

    @Override
    public void deleteConsistenzaSerieList() throws EMFError {
        SerVRicConsistSerieUdRowBean currentRow = (SerVRicConsistSerieUdRowBean) getForm()
                .getConsistenzaSerieList().getTable().getCurrentRow();
        BigDecimal idVerSerie = null;
        BigDecimal idSerie = null;
        if (currentRow != null) {
            idVerSerie = currentRow.getIdVerSerie();
            idSerie = currentRow.getIdSerie();
            BigDecimal idConsistVerSerie = currentRow.getIdConsistVerSerie();
            deleteConsistenza(idConsistVerSerie, idVerSerie, idSerie);
        }

        if (!getMessageBox().hasError()) {
            if (idSerie != null && idVerSerie != null) {
                if (serieEjb.checkVersione(getMessageBox(), idVerSerie,
                        CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
                        && serieEjb.existContenuto(idVerSerie,
                                CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name())
                        && serieEjb.checkContenuto(idVerSerie, false, false, true,
                                CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                                CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
                    // se stato versione = DA_CONTROLLARE e se contenuto di tipo EFFETTIVO di
                    // versione serie esiste ed
                    // ha stato CREATO o DA_CONTROLLARE_CONSIST eseguo controllo
                    try {
                        serieEjb.initControlloContenuto(idSerie, idVerSerie,
                                CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
                        serieEjb.callControlloContenutoAsync(getUser().getIdUtente(),
                                idVerSerie.longValue(),
                                CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
                        getMessageBox().addInfo(
                                "Lanciato automaticamente controllo del contenuto della serie a causa della eliminazione della consistenza");
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    }
                }
            }
        }

        if (!getMessageBox().hasError() && getLastPublisher()
                .equals(Application.Publisher.COMUNICAZIONE_CONSISTENZA_DETAIL)) {
            goBack();
        } else {
            RicercaSerieBean filtri = new RicercaSerieBean(
                    getForm().getFiltriComunicazioneConsistenzaSerieUD());
            SerVRicConsistSerieUdTableBean table = serieEjb
                    .getSerVRicConsistSerieUdTableBean(filtri);

            int rowIndex;
            int pageSize;
            if (getForm().getConsistenzaSerieList().getTable() != null) {
                rowIndex = getForm().getConsistenzaSerieList().getTable().getCurrentRowIndex();
                pageSize = getForm().getConsistenzaSerieList().getTable().getPageSize();
            } else {
                rowIndex = 0;
                pageSize = WebConstants.DEFAULT_PAGE_SIZE;
            }
            getForm().getConsistenzaSerieList().setTable(table);
            getForm().getConsistenzaSerieList().getTable().setPageSize(pageSize);
            getForm().getConsistenzaSerieList().getTable().setCurrentRowIndex(rowIndex);

            forwardToPublisher(getLastPublisher());
        }
    }

    private void deleteConsistenza(BigDecimal idConsistVerSerie, BigDecimal idVerSerie,
            BigDecimal idSerie) {
        if (idVerSerie != null && idSerie != null && idConsistVerSerie != null) {
            if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())) {
                if (serieEjb.checkContenuto(idVerSerie, true, true, true,
                        CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                        CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
                    try {
                        serieEjb.deleteSerConsistVerSerie(getUser().getIdUtente(),
                                idConsistVerSerie, idVerSerie, idSerie);
                    } catch (ParerUserError pue) {
                        getMessageBox().addError(pue.getDescription());
                    }
                } else {
                    getMessageBox().addError(
                            "La consistenza attesa non pu\u00F2 essere eliminata perch\u00E9 almeno un contenuto ha stato diverso da CREATO e CONTROLLATA_CONSIST e DA_CONTROLLARE_CONSIST");
                }
            } else {
                getMessageBox().addError(
                        "La consistenza attesa non pu\u00F2 essere eliminta perch\u00E9 la versione della serie ha stato diverso da APERTA e DA_CONTROLLARE e CONTROLLATA e DA_VALIDARE");
            }
        }
    }

    @Override
    public void deleteComunicazioneConsistenzaDetail() throws EMFError {
        deleteConsistenzaSerieList();
    }

    @Override
    public void deleteConsistenzaAttesaDetail() throws EMFError {
        BigDecimal idSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_serie().parse();
        BigDecimal idVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail().getId_ver_serie()
                .parse();
        BigDecimal idConsistVerSerie = getForm().getDatiSerieConsistenzaAttesaDetail()
                .getId_consist_ver_serie().parse();
        if (idSerie != null && idVerSerie != null && idConsistVerSerie != null) {
            deleteConsistenza(idConsistVerSerie, idVerSerie, idSerie);
        }
        if (!getMessageBox().hasError()
                && getLastPublisher().equals(Application.Publisher.CONSISTENZA_ATTESA_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateConsistenzaSerieList() throws EMFError {
        SerVRicConsistSerieUdRowBean currentRow = (SerVRicConsistSerieUdRowBean) getForm()
                .getConsistenzaSerieList().getTable().getCurrentRow();
        if (currentRow != null) {
            if (serieEjb.checkVersione(currentRow.getIdVerSerie(),
                    CostantiDB.StatoVersioneSerie.APERTA.name(),
                    CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name(),
                    CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                    CostantiDB.StatoVersioneSerie.DA_VALIDARE.name())
                    && (serieEjb.checkContenuto(currentRow.getIdVerSerie(), true, true, true,
                            CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                            CostantiDB.StatoContenutoVerSerie.CONTROLLATA_CONSIST.name(),
                            CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name()))) {
                getForm().getComunicazioneConsistenzaDetail().getNm_userid_consist()
                        .setValue(getUser().getUsername());
                getForm().getComunicazioneConsistenzaDetail().getDt_comunic_consist_ver_serie()
                        .setValue(ActionUtils.getStringDateTime(Calendar.getInstance().getTime()));
                getForm().getComunicazioneConsistenzaDetail().setEditMode();

                getForm().getComunicazioneConsistenzaDetail().getShow_edit()
                        .setValue(String.valueOf(true));
                getForm().getComunicazioneConsistenzaDetail().getShow_delete()
                        .setValue(String.valueOf(true));
                getForm().getComunicazioneConsistenzaDetail().setStatus(Status.update);
                getForm().getConsistenzaSerieList().setStatus(Status.update);
                forwardToPublisher(Application.Publisher.COMUNICAZIONE_CONSISTENZA_DETAIL);
            } else {
                forwardToPublisher(getLastPublisher());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void updateComunicazioneConsistenzaDetail() throws EMFError {
        updateConsistenzaSerieList();
    }

    public void loadDettaglioContenutoEffDaComunicazioneConsistenza() throws EMFError {
        String rigaParam = getRequest().getParameter("riga");
        int riga = Integer.parseInt(rigaParam);
        setTableName(getForm().getConsistenzaSerieList().getName());
        setRiga(rigaParam);

        SerVRicConsistSerieUdRowBean currentRow = (SerVRicConsistSerieUdRowBean) getForm()
                .getConsistenzaSerieList().getTable().getRow(riga);
        if (currentRow.getIdContenutoEff() != null) {
            loadDettaglioContenuto(currentRow.getIdContenutoEff(),
                    CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name(), true);
        } else {
            getMessageBox().addError("Contenuto effettivo assente");
            forwardToPublisher(getLastPublisher());
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Gestione Firma HSM">
    @Override
    public void firmaIndiciAIPSerieHsmButton() throws EMFError {
        if (getForm().getSerieSelezionateList().getTable().isEmpty()) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "Selezionare almeno una serie da firmare"));
        } else {
            /* Richiedo le credenziali del HSM utilizzando apposito popup */
            if (!getMessageBox().hasError()) {
                // Ricavo l'id ambiente da un qualsiasi record degli elenchi da firmare
                // PS: non lo prendo dal filtro di ricerca perchè l'utente potrebbe cambiarlo dalla
                // combo senza fare la
                // ricerca
                // e così verrebbe preso un ambiente errato
                BigDecimal idStrut = ((BaseTable) getForm().getSerieSelezionateList().getTable())
                        .getRow(0).getBigDecimal("id_strut");
                OrgAmbienteRowBean ambienteRowBean = struttureEjb
                        .getOrgAmbienteRowBeanByIdStrut(idStrut);
                BigDecimal idAmbiente = ambienteRowBean.getIdAmbiente();
                if (idAmbiente != null) {
                    // Ricavo il parametro HSM_USERNAME (parametro multiplo dell'ambiente) associato
                    // all'utente corrente
                    String hsmUserName = amministrazioneEjb.getHsmUsername(getUser().getIdUtente(),
                            idAmbiente);
                    if (hsmUserName != null) {

                        getRequest().setAttribute("customSerieSelect", true);
                        getForm().getFiltriSerieDaFirmare().getUser().setValue(hsmUserName);
                        getForm().getFiltriSerieDaFirmare().getUser().setViewMode();
                    }
                } else {
                    getMessageBox()
                            .addError("Utente non rientra tra i firmatari definiti sull’ambiente");
                }
            }
        }
        forwardToPublisher(Application.Publisher.LISTA_SERIE_DA_FIRMARE_SELECT);
    }

    /**
     * Signs the list of file selected
     *
     * @throws EMFError errore generico
     *
     */
    public void firmaSerieHsmJs() throws EMFError {
        List<String> errorList = new ArrayList<>();
        JSONObject result = new JSONObject();

        // Recupero informazioni riguardo all'Utente (idSacer e credenziali HSM)
        long idUtente = SessionManager.getUser(getSession()).getIdUtente();

        getForm().getFiltriSerieDaFirmare().post(getRequest());
        String user = getForm().getFiltriSerieDaFirmare().getUser().parse();
        char[] passwd = getForm().getFiltriSerieDaFirmare().getPasswd().parse().toCharArray();
        char[] otp = getForm().getFiltriSerieDaFirmare().getOtp().parse().toCharArray();
        if (StringUtils.isBlank(user)) {
            errorList.add("Il campo \"Utente\" non può essere vuoto.");
        }
        if (passwd == null || passwd.length == 0) {
            errorList.add("Il campo \"Password\" non può essere vuoto.");
        }
        if (otp == null || otp.length == 0) {
            errorList.add("Il campo \"OTP\" non può essere vuoto.");
        }

        if (serieSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())) {
            getMessageBox().addError("Sessione di firma attiva");
        }

        BaseTable serieDaFirmare = (BaseTable) getForm().getSerieSelezionateList().getTable();

        try {
            if (errorList.isEmpty() && !getMessageBox().hasError() && serieDaFirmare != null) {
                SigningRequest request = new SigningRequest(idUtente);
                HSMUser userHSM = new HSMUser(user, passwd);
                userHSM.setOTP(otp);
                request.setUserHSM(userHSM);
                request.setType(TiSessioneFirma.SERIE);
                for (BaseRow serie : serieDaFirmare) {
                    BigDecimal idSerie = serie.getBigDecimal("id_ver_serie");
                    request.addFile(idSerie);
                }
                // MEV#15967 - Attivazione della firma Xades e XadesT
                Future<SigningResponse> provaAsync = null;
                it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma = amministrazioneEjb
                        .getTipoFirmaPerStruttura(getIdStrutCorrente());
                switch (tipoFirma) {
                case CADES:
                    provaAsync = firmaHsmEjb.signP7MRequest(request);
                    break;
                case XADES:
                    provaAsync = firmaHsmEjb.signXades(request);
                    break;
                }
                //
                getSession().setAttribute(Signature.FUTURE_ATTR_SERIE, provaAsync);
            }

            if (errorList.isEmpty() && !result.has("status")) {
                result.put("info", "Sessione di firma avviata");
            } else if (!errorList.isEmpty()) {
                result.put("error", errorList);
            }
        } catch (JSONException ex) {
            log.error(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma dei file",
                    ex);
            getMessageBox().addError("Errore inatteso nel recupero e firma dei file");
        }
        if (!getMessageBox().hasError()) {
            redirectToAjax(result);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @SuppressLogging
    public void checkSignatureFuture() {
        Future<SigningResponse> futureObj = (Future<SigningResponse>) getSession()
                .getAttribute(Signature.FUTURE_ATTR_SERIE);
        JSONObject result = new JSONObject();
        try {
            result.put("status", Signature.NO_SESSION);
            if (futureObj != null) {
                if (futureObj.isDone()) {
                    SigningResponse resp = futureObj.get();
                    result.put("status", resp.name());
                    switch (resp) {
                    case ACTIVE_SESSION_YET:
                    case AUTH_WRONG:
                    case USER_BLOCKED:
                    case OTP_WRONG:
                    case OTP_EXPIRED:
                    case HSM_ERROR:
                    case UNKNOWN_ERROR:
                        result.put("error", resp.getDescription());
                        break;
                    case WARNING:
                    case OK:
                        result.put("info", resp.getDescription());
                        getForm().getSerieSelezionateList().getTable().clear();
                        break;
                    default:
                        throw new AssertionError(resp.name());
                    }
                    getSession().removeAttribute(Signature.FUTURE_ATTR_SERIE);
                } else {
                    result.put("status", Signature.WORKING);
                }
            }
            redirectToAjax(result);
        } catch (InterruptedException | ExecutionException ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
            }
            log.error("Errore inatteso nell'esecuzione del metodo asincrono di firma", ex);
            try {
                result.put("status", SigningResponse.UNKNOWN_ERROR.name());
                result.put("error", "Errore inatteso nella procedura di firma");
                redirectToAjax(result);
                getSession().removeAttribute(Signature.FUTURE_ATTR_SERIE);
            } catch (JSONException ex1) {
                log.error("Errore inatteso nella creazione del JSON", ex1);
                getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
                forwardToPublisher(getLastPublisher());
                getSession().removeAttribute(Signature.FUTURE_ATTR_SERIE);
            }
        } catch (JSONException ex) {
            getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(getLastPublisher());
            getSession().removeAttribute(Signature.FUTURE_ATTR_SERIE);
        }
    }

    /* Getter di valori utilizzati all'interno della action */
    private BigDecimal getIdStrutCorrente() {
        return getUser().getIdOrganizzazioneFoglia();
    }

    // </editor-fold>
    private static class VerSerieDetailBean implements Serializable {

        BigDecimal idVerSerie;
        String sourceList;
        BaseTableInterface<?> sourceTable;
        int level = 1;

        public VerSerieDetailBean(BigDecimal idVerSerie, String sourceList,
                BaseTableInterface<?> sourceTable, int level) {
            this.idVerSerie = idVerSerie;
            this.sourceTable = sourceTable;
            this.sourceList = sourceList;
            this.level = level;
        }

        public BigDecimal getIdVerSerie() {
            return idVerSerie;
        }

        public void setIdVerSerie(BigDecimal idVerSerie) {
            this.idVerSerie = idVerSerie;
        }

        public String getSourceList() {
            return sourceList;
        }

        public void setSourceList(String sourceList) {
            this.sourceList = sourceList;
        }

        public BaseTableInterface<?> getSourceTable() {
            return sourceTable;
        }

        public void setSourceTable(BaseTableInterface<?> sourceTable) {
            this.sourceTable = sourceTable;
        }

        public void addLevel() {
            this.level++;
        }

        public int getLevel() {
            return level;
        }

    }

}
