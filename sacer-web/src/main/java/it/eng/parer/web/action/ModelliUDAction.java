/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.web.action;

import static it.eng.spagoCore.ConfigProperties.StandardProperty.LOAD_XSD_APP_UPLOAD_DIR;
import static it.eng.spagoCore.ConfigProperties.StandardProperty.LOAD_XSD_APP_MAX_REQUEST_SIZE;
import static it.eng.spagoCore.ConfigProperties.StandardProperty.LOAD_XSD_APP_MAX_FILE_SIZE;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import it.eng.parer.amministrazioneStrutture.gestioneModelliXsdUd.ejb.ModelliXsdUdEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.common.helper.ParamApplicHelper;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ModelliUDAbstractAction;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo;
import it.eng.parer.slite.gen.form.ModelliUDForm;
import it.eng.parer.slite.gen.form.ModelliUDForm.FiltriModelliXsdUd;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdUdRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdUdTableBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.spagoCore.ConfigSingleton;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.util.logging.Level;
import javax.xml.XMLConstants;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ModelliUDAction extends ModelliUDAbstractAction {

    private static final Logger LOG = LoggerFactory.getLogger(ModelliUDAction.class);

    @EJB(mappedName = "java:app/Parer-ejb/ModelliXsdUdEjb")
    private ModelliXsdUdEjb modelliXsdUdEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/ParamApplicHelper")
    private ParamApplicHelper paramApplicHelper;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Operazione non supportata");
    }

    @Override
    public void process() throws EMFError {

        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());

        if (isMultipart && getLastPublisher().equals(Application.Publisher.MODELLI_XSD_UD_DETAIL)) {
            try {
                readUploadForm();
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    /**
     * Metodo che legge la form e passa i dati alla validazione
     *
     * @throws ParerUserError
     *             errore generico
     * @throws EMFError
     *             errore generico
     */
    public void readUploadForm() throws ParerUserError, EMFError {

        getMessageBox().clear();

        int sizeMb = WebConstants.FILESIZE * WebConstants.FILESIZE;

        try {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // maximum size that will be stored in memory
            factory.setSizeThreshold(sizeMb);
            // the location for saving data that is larger than
            factory.setRepository(
                    new File(ConfigSingleton.getInstance().getStringValue(LOAD_XSD_APP_UPLOAD_DIR.name())));
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(ConfigSingleton.getInstance().getLongValue(LOAD_XSD_APP_MAX_REQUEST_SIZE.name()));
            upload.setFileSizeMax(ConfigSingleton.getInstance().getLongValue(LOAD_XSD_APP_MAX_FILE_SIZE.name()));
            List<FileItem> items = upload.parseRequest(getRequest());
            Iterator<FileItem> iter = items.iterator();

            DiskFileItem tmpFileItem = null;
            DiskFileItem tmpOperation = null;

            while (iter.hasNext()) {

                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    tmpFileItem = (DiskFileItem) item;
                } else if (item.getFieldName()
                        .equals(getForm().getModelliXsdUdDetail().getTi_modello_xsd().getName())) {
                    getForm().getModelliXsdUdDetail().getTi_modello_xsd().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getModelliXsdUdDetail().getFl_default().getName())) {
                    getForm().getModelliXsdUdDetail().getFl_default().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getModelliXsdUdDetail().getCd_xsd().getName())) {
                    getForm().getModelliXsdUdDetail().getCd_xsd().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getModelliXsdUdDetail().getDs_xsd().getName())) {
                    getForm().getModelliXsdUdDetail().getDs_xsd().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getModelliXsdUdDetail().getDt_istituz().getName())) {
                    getForm().getModelliXsdUdDetail().getDt_istituz().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getModelliXsdUdDetail().getDt_soppres().getName())) {
                    getForm().getModelliXsdUdDetail().getDt_soppres().setValue(item.getString());
                } else if (!item.getFieldName().equals("table")) {
                    String fieldName = item.getFieldName();
                    if (fieldName.contains(NE_DETTAGLIO_CANCEL) || fieldName.contains(NE_DETTAGLIO_SAVE)) {
                        tmpOperation = (DiskFileItem) item;
                    }
                }
            }

            if (tmpOperation != null && tmpOperation.getFieldName().contains(NE_DETTAGLIO_CANCEL)) {
                goBack();
            } else if (tmpOperation != null && tmpOperation.getFieldName().contains(NE_DETTAGLIO_SAVE)) {
                if (getForm().getModelliXsdUdDetail().getStatus().equals(Status.insert) && tmpFileItem != null
                        && (StringUtils.isBlank(tmpFileItem.getName()) || tmpFileItem.getSize() == 0)) {
                    // controllo esistenza del file
                    getMessageBox().addError("Nessun file selezionato");
                }
                // id modello xsd
                BigDecimal idModelloXsdUd = getForm().getModelliXsdUdDetail().getId_modello_xsd_ud().parse();

                // controllo ambiente
                BigDecimal idAmbiente = getForm().getModelliXsdUdDetail().getId_ambiente().parse();
                if (idAmbiente == null) {
                    getMessageBox().addError("Ambiente non inserito");
                }

                // controllo esistenza tipo modello xsd
                String tiModelloXsd = getForm().getModelliXsdUdDetail().getTi_modello_xsd().parse();
                if (StringUtils.isBlank(tiModelloXsd)) {
                    getMessageBox().addError("Tipologia non inserita");
                }

                // controllo flag standard
                String flDefault = getForm().getModelliXsdUdDetail().getFl_default().parse();
                if (StringUtils.isBlank(flDefault)) {
                    getMessageBox().addError("Flag standard non inserito");
                } else if (getForm().getModelliXsdUdDetail().getFl_default().parse().equals(CostantiDB.Flag.TRUE)
                        && modelliXsdUdEjb.existAnotherDecModelloXsdStd(
                                getForm().getModelliXsdUdDetail().getId_ambiente().parse(), idModelloXsdUd,
                                getForm().getModelliXsdUdDetail().getTi_modello_xsd().parse(),
                                CostantiDB.TiUsoModelloXsd.VERS.name())) {
                    getMessageBox().addError("Modello xsd standard gi\u00E0 presente nell'ambiente");
                }

                // controllo esistenza codice versione
                String cdVersione = getForm().getModelliXsdUdDetail().getCd_xsd().parse();
                if (StringUtils.isBlank(cdVersione)) {
                    getMessageBox().addError("Codice versione non inserito");
                } else {
                    // controllo caratteri non corretti
                    Pattern pattern = Pattern.compile(WebConstants.MODELLO_XSD_VERSION_REGEXP);
                    Matcher m = pattern.matcher(cdVersione);
                    if (!m.find()) {
                        getMessageBox().addError(
                                "Codice versione non valido, necessario inserire un alfanumerico con separatori ammessi '.' '_' '-' opzionali");
                    }
                }

                String dsVersione = getForm().getModelliXsdUdDetail().getDs_xsd().parse();
                if (StringUtils.isBlank(dsVersione)) {
                    getMessageBox().addError("Descrizione non inserita");
                }

                SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
                Date dtIstituz = getForm().getModelliXsdUdDetail().getDt_istituz().parse();
                if (dtIstituz == null) {
                    dtIstituz = Calendar.getInstance().getTime();
                    getForm().getModelliXsdUdDetail().getDt_istituz().setValue(df.format(dtIstituz));
                }

                Date dtSoppres = getForm().getModelliXsdUdDetail().getDt_soppres().parse();
                if (dtSoppres == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(2444, 11, 31, 0, 0, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    getForm().getModelliXsdUdDetail().getDt_soppres().setValue(df.format(calendar.getTime()));
                }
                // date check
                Date today = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                if (getForm().getModelliXsdUdDetail().getStatus().equals(Status.insert) && !dtIstituz.equals(today)
                        && dtIstituz.before(today)) {
                    getMessageBox()
                            .addError("La data di attivazione deve essere successiva o uguale alla data odierna");
                }
                if (dtSoppres.before(dtIstituz)) {
                    getMessageBox()
                            .addError("La data di fine validit\u00E0 deve essere successiva alla data di attivazione");
                }

                // Controllo esistenza codice versione modello xsd
                DecModelloXsdUdRowBean tmpRow = modelliXsdUdEjb.getDecModelloXsdUd(
                        getForm().getModelliXsdUdDetail().getId_ambiente().parse(),
                        getForm().getModelliXsdUdDetail().getTi_modello_xsd().parse(),
                        CostantiDB.TiUsoModelloXsd.VERS.name(), getForm().getModelliXsdUdDetail().getCd_xsd().parse(),
                        false);

                if (idModelloXsdUd != null) {
                    if (tmpRow != null && !idModelloXsdUd.equals(tmpRow.getIdModelloXsdUd())) {
                        getMessageBox()
                                .addError("Il codice versione del modello xsd \u00E8 gi\u00E0 presente nell'ambiente");
                    }
                } else if (tmpRow != null) {
                    getMessageBox()
                            .addError("Il codice versione del modello xsd \u00E8 gi\u00E0 presente nell'ambiente");
                }

                // xsd
                String clob = null;
                if (StringUtils.isNotBlank(tmpFileItem.getName())) {
                    FileBinario fileBin = getFileBinario(tmpFileItem);
                    // conversione in stringa
                    clob = new String(fileBin.getDati());
                    // if update
                    if (getForm().getModelliXsdUdDetail().getStatus().equals(Status.update)
                            && modelliXsdUdEjb.isModelloXsdUdInUse(idModelloXsdUd)) {
                        getMessageBox()
                                .addError("Esiste almeno un versamento per questo modello, xsd non aggiornabile");
                    }
                }
                // validate xsd
                validateXsd(clob);
                // no errors
                if (getMessageBox().isEmpty()) {
                    // save
                    saveModelloXsdUd(idModelloXsdUd, tiModelloXsd, flDefault, cdVersione, dsVersione, dtIstituz,
                            dtSoppres, clob);
                }
            }
        } catch (FileUploadException ex) {
            LOG.error("Eccezione nell'upload dei file", ex);
            getMessageBox().addError("Eccezione nell'upload dei file: " + ex.getLocalizedMessage(), ex);
            forwardToPublisher(Application.Publisher.MODELLI_XSD_UD_DETAIL);
        } catch (Exception ex) {
            LOG.error("Eccezione generica nell'importazione del modello xsd dell'unità documentaria", ex);
            getMessageBox().addError(
                    "Si \u00E8 verificata un'eccezione nell'importazione del modello xsd dell'unità documentaria: "
                            + ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(Application.Publisher.MODELLI_XSD_UD_DETAIL);
        }
    }

    private void validateXsd(String clob) throws FileUploadException {
        if (StringUtils.isNotBlank(clob)) {
            // compilazione schema
            // 1. Lookup a factory for the W3C XML Schema language
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try {
                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            } catch (SAXException ex) {
                java.util.logging.Logger.getLogger(ModelliUDAction.class.getName()).log(Level.SEVERE, null, ex);
            }
            // anche in questo caso l'eccezione non deve mai verificarsi, a meno di non aver
            // caricato
            // nel database un xsd danneggiato...
            try {
                // 2. Compile the schema.
                schemaFactory.newSchema(new StreamSource(new StringReader(clob)));
            } catch (SAXException e) {
                throw new FileUploadException("Eccezione nel parsing dello schema del file xsd", e);
            }
        }
    }

    private FileBinario getFileBinario(DiskFileItem tmpFileItem) {

        FileBinario tmpFileBinario;
        tmpFileBinario = new FileBinario();

        if (!tmpFileItem.isFormField()) {
            long sizeInBytes = tmpFileItem.getSize();
            String fileName = tmpFileItem.getName();
            if (sizeInBytes > 0 && fileName.length() > 0) {

                tmpFileBinario.setId(tmpFileItem.getFieldName());
                if (tmpFileItem.isInMemory()) {
                    tmpFileBinario.setInMemoria(true);
                    tmpFileBinario.setDati(tmpFileItem.get());
                    tmpFileBinario.setDimensione(sizeInBytes);
                } else {
                    tmpFileBinario.setInMemoria(false);
                    tmpFileBinario.setFileSuDisco(tmpFileItem.getStoreLocation());
                    tmpFileBinario.setDimensione(sizeInBytes);
                }

            }
        }

        return tmpFileBinario;
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getModelliXsdUdList().getName())) {
                initModelloXsdUdDetail();
                BigDecimal idModelloXsdUd = ((DecModelloXsdUdTableBean) getForm().getModelliXsdUdList().getTable())
                        .getCurrentRow().getIdModelloXsdUd();
                loadDettaglioModelloXsdUd(idModelloXsdUd);
            }
        }
    }

    private void loadDettaglioModelloXsdUd(BigDecimal idModelloXsdUd) throws EMFError {
        // Caricamento dettaglio modello xsd ud
        DecModelloXsdUdRowBean modelloXsdUdRowBean = modelliXsdUdEjb.getDecModelloXsdUd(idModelloXsdUd);
        getForm().getModelliXsdUdDetail().copyFromBean(modelloXsdUdRowBean);
    }

    private void initModelloXsdUdDetail() {
        BaseTableInterface<?> ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        getForm().getModelliXsdUdDetail().getId_ambiente()
                .setDecodeMap(DecodeMap.Factory.newInstance(ambienteTableBean, "id_ambiente", "nm_ambiente"));

        // Imposto la combo "Tipo modello xsd"
        getForm().getModelliXsdUdDetail().getTi_modello_xsd().setDecodeMap(ComboGetter.getMappaTiModelloXsdUd());
        // Imposto la combo "Flag Default"
        getForm().getModelliXsdUdDetail().getFl_default().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.MODELLI_XSD_UD_DETAIL)
                && getForm().getModelliXsdUdList().getStatus().equals(Status.update)) {
            DecModelloXsdUdRowBean currentRow = (DecModelloXsdUdRowBean) getForm().getModelliXsdUdList().getTable()
                    .getCurrentRow();
            BigDecimal idModelloXsdUd = currentRow.getIdModelloXsdUd();
            if (idModelloXsdUd != null) {
                loadDettaglioModelloXsdUd(idModelloXsdUd);
            }
            getForm().getModelliXsdUdDetail().setViewMode();
            getForm().getModelliXsdUdDetail().setStatus(Status.view);
            getForm().getModelliXsdUdList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.MODELLI_XSD_UD_DETAIL);
        } else {
            goBack();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getModelliXsdUdList().getName())) {
            getForm().getModelliXsdUdDetail().reset();
            getForm().getModelliXsdUdDetail().setEditMode();
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setHidden(true);
            getForm().getModelliXsdUdDetail().getLogEventi().setHidden(true);

            // Setta ambiente in base ai valori della pagina di ricerca modelli xsd
            initComboCreaModelloXsdUdFromRicercaModelliXsd();

            populateDefaultValues();

            getForm().getModelliXsdUdList().setStatus(Status.insert);
            getForm().getModelliXsdUdDetail().setStatus(Status.insert);

            forwardToPublisher(Application.Publisher.MODELLI_XSD_UD_DETAIL);
        }
    }

    /**
     * Inizializza le combo del DETTAGLIO DI UN MODELLO XSD, ricavando i valori dai filtri di ricerca modelli xsd. Se
     * impostato, restituisce il valore dell'id del filtro relativo all'ambiente
     *
     * @return l'id dell'ambiente impostato come filtro di ricerca
     */
    private BigDecimal initComboCreaModelloXsdUdFromRicercaModelliXsd() {
        // Inizializzo la combo ambiente in base ai valori impostati nella pagina di
        // ricerca modelli xsd
        ComboBox<?> ambienteCombo = getForm().getFiltriModelliXsdUd().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        getForm().getModelliXsdUdDetail().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getModelliXsdUdDetail().getId_ambiente()
                .setValue(getForm().getFiltriModelliXsdUd().getId_ambiente().getValue());

        // Inizializzo le combo "Tipo modello xsd" e "Standard"
        getForm().getModelliXsdUdDetail().getTi_modello_xsd().setDecodeMap(ComboGetter.getMappaTiModelloXsdUd());
        getForm().getModelliXsdUdDetail().getFl_default().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        return StringUtils.isNotBlank(getForm().getFiltriModelliXsdUd().getId_ambiente().getValue())
                ? new BigDecimal(getForm().getFiltriModelliXsdUd().getId_ambiente().getValue()) : null;
    }

    /**
     * Metodo che inserisce i valori di default dei campi nella form di creazione modelli XSD tipi Fascicolo
     *
     * @throws EMFError
     *             errore generico
     */
    private void populateDefaultValues() {
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        getForm().getModelliXsdUdDetail().getDt_istituz().setValue(df.format(Calendar.getInstance().getTime()));
        getForm().getModelliXsdUdDetail().getDt_soppres().setValue(df.format(cal.getTime()));
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Operazione non supportata");
    }

    private void saveModelloXsdUd(BigDecimal idModelloXsdUd, String tiModelloXsd, String flStandard, String cdVersione,
            String dsVersione, Date dtIstituz, Date dtSoppres, String file) throws EMFError {
        getMessageBox().clear();
        DecModelloXsdUdRowBean modelloXsdUdRowBean = new DecModelloXsdUdRowBean();

        try {
            // salva il modello
            if (!getMessageBox().hasError()) {

                // inizializzo il rowbean
                BigDecimal idAmbiente = new BigDecimal(
                        getForm().getModelliXsdUdDetail().getId_ambiente().parse().intValue());

                // normalize string
                modelloXsdUdRowBean.setCdXsd(cdVersione.toUpperCase().trim());

                if (StringUtils.isNotBlank(file)) {
                    modelloXsdUdRowBean.setBlXsd(file);
                }
                modelloXsdUdRowBean.setIdAmbiente(idAmbiente);
                modelloXsdUdRowBean.setDtIstituz(new Timestamp(dtIstituz.getTime()));
                modelloXsdUdRowBean.setDtSoppres(new Timestamp(dtSoppres.getTime()));
                modelloXsdUdRowBean.setDsXsd(dsVersione);
                modelloXsdUdRowBean.setTiUsoModelloXsd(CostantiDB.TiUsoModelloXsd.VERS.name());
                modelloXsdUdRowBean.setTiModelloXsd(tiModelloXsd);
                modelloXsdUdRowBean.setFlDefault(flStandard);

                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getModelliXsdUdList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());

                    Long pk = modelliXsdUdEjb.saveModelloXsdUd(param, modelloXsdUdRowBean);
                    if (pk != null) {
                        getForm().getModelliXsdUdDetail().getId_modello_xsd_ud().setValue(pk.toString());
                    }

                    getForm().getModelliXsdUdDetail().copyToBean(modelloXsdUdRowBean);
                    getForm().getModelliXsdUdList().getTable().last();
                    getForm().getModelliXsdUdList().getTable().add(modelloXsdUdRowBean);

                } else if (getForm().getModelliXsdUdList().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    modelloXsdUdRowBean.setIdModelloXsdUd(idModelloXsdUd);
                    modelliXsdUdEjb.updateModelloXsdUd(param, modelloXsdUdRowBean);
                }
                reloadAfterGoBack(Application.Publisher.MODELLI_XSD_UD_DETAIL);
                getMessageBox().addInfo("Modello xsd salvato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        forwardToPublisher(Application.Publisher.MODELLI_XSD_UD_DETAIL);
    }

    @Override
    public void updateModelliXsdUdList() throws EMFError {
        getForm().getModelliXsdUdDetail().getScaricaXsdButton().setViewMode();

        getForm().getModelliXsdUdDetail().setViewMode();
        getForm().getModelliXsdUdDetail().getDs_xsd().setEditMode();
        getForm().getModelliXsdUdDetail().getDt_soppres().setEditMode();
        getForm().getModelliXsdUdDetail().getBl_xsd().setEditMode();
        getForm().getModelliXsdUdDetail().getFl_default().setEditMode();

        getForm().getModelliXsdUdDetail().setStatus(Status.update);
        getForm().getModelliXsdUdList().setStatus(Status.update);
    }

    @Override
    public void deleteModelliXsdUdList() throws EMFError {
        DecModelloXsdUdRowBean currentRow = (DecModelloXsdUdRowBean) getForm().getModelliXsdUdList().getTable()
                .getCurrentRow();
        getMessageBox().clear();
        int row = getForm().getModelliXsdUdList().getTable().getCurrentRowIndex();

        if (!getMessageBox().hasError()) {
            try {
                if (modelliXsdUdEjb.existDecUsoModelloXsdUdAtMostOnce(currentRow.getIdModelloXsdUd())) {
                    getMessageBox().addError(
                            "Il modello xsd dell'unit\u00E0 documentaria viene gi\u00E0 utilizzato: eliminazione non consentita");
                } else {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (param.getNomePagina().equals(Application.Publisher.MODELLI_XSD_UD_DETAIL)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    } else {
                        param.setNomeAzione(
                                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getModelliXsdUdList()));
                    }
                    modelliXsdUdEjb.deleteDecModelloXsdUd(param, currentRow);
                    getForm().getModelliXsdUdList().getTable().remove(row);

                    getMessageBox().addInfo("Modello xsd eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError("Il modello xsd non pu\u00F2 essere eliminato: " + ex.getDescription());
            }
        }
        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.MODELLI_XSD_UD_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getModelliXsdUdList().getName())) {
                getForm().getModelliXsdUdDetail().setStatus(Status.view);
                getForm().getModelliXsdUdDetail().setViewMode();
                // button edit mode
                getForm().getModelliXsdUdDetail().getScaricaXsdButton().setEditMode();
                getForm().getModelliXsdUdDetail().getScaricaXsdButton().setDisableHourGlass(true);
                forwardToPublisher(Application.Publisher.MODELLI_XSD_UD_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MODELLI_XSD_UD_DETAIL;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MODELLI_UD;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.LISTA_MODELLI_XSD_UD_SELECT)) {
                int rowIndex;
                int pageSize;
                if (getForm().getModelliXsdUdList().getTable() != null) {
                    rowIndex = getForm().getModelliXsdUdList().getTable().getCurrentRowIndex();
                    pageSize = getForm().getModelliXsdUdList().getTable().getPageSize();
                } else {
                    rowIndex = 0;
                    pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                }

                /*
                 * Ricavo la lista degli ambienti da cercare in base a come è stata impostata nei filtri di ricerca la
                 * combo Ambiente
                 */
                List<BigDecimal> idAmbientiToFind = getAmbientiToFind(getForm().getFiltriModelliXsdUd());

                // Lista modelli xsd ud
                getForm().getModelliXsdUdList()
                        .setTable(modelliXsdUdEjb.findDecModelloXsdUd(getForm().getFiltriModelliXsdUd(),
                                idAmbientiToFind, CostantiDB.TiUsoModelloXsd.VERS.name(),
                                getForm().getModelliXsdUdList().isFilterValidRecords()));
                getForm().getModelliXsdUdList().getTable().setPageSize(pageSize);
                getForm().getModelliXsdUdList().getTable().setCurrentRowIndex(rowIndex);
            } else if (publisherName.equals(Application.Publisher.MODELLI_XSD_UD_DETAIL)) {
                DecModelloXsdUdRowBean currentRow = (DecModelloXsdUdRowBean) getForm().getModelliXsdUdList().getTable()
                        .getCurrentRow();
                BigDecimal idModelloXsdUd = currentRow.getIdModelloXsdUd();
                if (idModelloXsdUd != null) {
                    getForm().getModelliXsdUdDetail().copyFromBean(modelliXsdUdEjb.getDecModelloXsdUd(idModelloXsdUd));
                }
            }
            getForm().getModelliXsdUdDetail().setViewMode();
            // button edit mode
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setEditMode();
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setDisableHourGlass(true);
            getForm().getModelliXsdUdDetail().setStatus(Status.view);
            getForm().getModelliXsdUdList().setStatus(Status.view);
            postLoad();
        } catch (EMFError e) {
            LOG.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
        }
    }

    @Secure(action = "Menu.Amministrazione.ModelliUD")
    public void loadListaModelliXsdUd() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.ModelliUD");

        // Azzero i filtri e la lista risultato della form di ricerca
        getForm().getFiltriModelliXsdUd().reset();

        getForm().getModelliXsdUdList().clear();
        getForm().getModelliXsdUdList().setUserOperations(true, true, true, true);

        // Inizializzo le combo di ricerca
        initComboRicercaModelliXsdUd();

        // Imposto tutti i filtri in edit mode
        getForm().getFiltriModelliXsdUd().setEditMode();

        // Imposto come visibile il bottone di ricerca mdoelli xsd tipi Fascicolo e
        // disabilito la clessidra (per IE)
        getForm().getFiltriModelliXsdUd().getRicercaModelliButton().setEditMode();
        getForm().getFiltriModelliXsdUd().getRicercaModelliButton().setDisableHourGlass(true);

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.LISTA_MODELLI_XSD_UD_SELECT);
    }

    /**
     * Metodo che crea le mappe coi valori e setta le combo presenti nella pagina di ricerca modelli XSD tipi Fascicolo
     *
     * @throws EMFError
     *             errore generico
     */
    private void initComboRicercaModelliXsdUd() {
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
        BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

        // Inizializzo le combo settando l'ambiente corrente
        BaseTableInterface<?> ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();

        getForm().getFiltriModelliXsdUd().getId_ambiente()
                .setDecodeMap(DecodeMap.Factory.newInstance(ambienteTableBean, "id_ambiente", "nm_ambiente"));
        getForm().getFiltriModelliXsdUd().getId_ambiente().setValue(idAmbiente.toString());

        // Imposto le combo "Tipo modello xsd", "Standard" e "Attivo"
        getForm().getFiltriModelliXsdUd().getTi_modello_xsd().setDecodeMap(ComboGetter.getMappaTiModelloXsdUd());
        getForm().getFiltriModelliXsdUd().getFl_default().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    /**
     * Ricava la lista degli ambienti da passare alla Ricerca Modelli xsd tipi fascicolo in base a come sono stati
     *
     * @param filtriModelliXsdUd
     *            oggetto di tipo {@link FiltriModelliXsdTipiFascicolo}
     *
     * @return la lista degli ambienti da utilizzare per la ricerca
     *
     * @throws EMFError
     *             errore generico
     */
    private List<BigDecimal> getAmbientiToFind(FiltriModelliXsdUd filtriModelliXsdUd) throws EMFError {
        /* Se la combo ambiente è vuota ricavo gli ambienti abilitati */
        List<BigDecimal> idAmbientiToFind = new ArrayList<>();
        if (filtriModelliXsdUd.getId_ambiente().parse() != null) {
            /*
             * Se la combo ambiente è compilata, passo l'ambiente tra quelli a cui l'utente è abilitato in funzione
             * delle strutture cui l’utente è abilitato
             */
            idAmbientiToFind.add(filtriModelliXsdUd.getId_ambiente().parse());
        } else {
            // Riprendo i valori dalla combo (non da DB, così evito un accesso)
            DecodeMapIF mappaFiltri = filtriModelliXsdUd.getId_ambiente().getDecodeMap();
            idAmbientiToFind = new ArrayList(mappaFiltri.keySet());
        }
        return idAmbientiToFind;
    }

    /**
     * Metodo richiamato al click del bottone di ricerca modelli xsd dei tipi fascicolo
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaModelliButton() throws EMFError {
        ModelliUDForm.FiltriModelliXsdUd filtriModelliXsdUd = getForm().getFiltriModelliXsdUd();
        /*
         * Esegue la post dei filtri compilati e Valida i filtri per verificarne la correttezza sintattica e
         * l'obbligatorietà
         */
        if (filtriModelliXsdUd.postAndValidate(getRequest(), getMessageBox())) {
            /*
             * Ricavo la lista degli ambienti da cercare in base a come è stata impostata nei filtri di ricerca la combo
             * Ambiente
             */
            List<BigDecimal> idAmbientiToFind = getAmbientiToFind(filtriModelliXsdUd);

            // Lista modelli xsd ud
            getForm().getModelliXsdUdList()
                    .setTable(modelliXsdUdEjb.findDecModelloXsdUd(filtriModelliXsdUd, idAmbientiToFind,
                            CostantiDB.TiUsoModelloXsd.VERS.name(),
                            getForm().getModelliXsdUdList().isFilterValidRecords()));
            getForm().getModelliXsdUdList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getModelliXsdUdList().getTable().first();
        }
        forwardToPublisher(Application.Publisher.LISTA_MODELLI_XSD_UD_SELECT);
    }

    @Override
    public void filterInactiveRecordsModelliXsdUdList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getModelliXsdUdList().getTable() != null) {
            rowIndex = getForm().getModelliXsdUdList().getTable().getCurrentRowIndex();
            pageSize = getForm().getModelliXsdUdList().getTable().getPageSize();
        }

        /*
         * Ricavo la lista degli ambienti da cercare in base a come è stata impostata nei filtri di ricerca la combo
         * Ambiente
         */
        List<BigDecimal> idAmbientiToFind = getAmbientiToFind(getForm().getFiltriModelliXsdUd());

        // Lista modelli xsd ud
        getForm().getModelliXsdUdList()
                .setTable(modelliXsdUdEjb.findDecModelloXsdUd(getForm().getFiltriModelliXsdUd(), idAmbientiToFind,
                        CostantiDB.TiUsoModelloXsd.VERS.name(),
                        getForm().getModelliXsdUdList().isFilterValidRecords()));
        getForm().getModelliXsdUdList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getModelliXsdUdList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void logEventi() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_MODELLO_UD);
        DecModelloXsdUdRowBean rb = (DecModelloXsdUdRowBean) getForm().getModelliXsdUdList().getTable().getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(rb.getIdModelloXsdUd().toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        if (getForm().getModelliXsdUdList().getStatus().equals(Status.view)) {
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setHidden(false);
            getForm().getModelliXsdUdDetail().getLogEventi().setHidden(false);
            getForm().getModelliXsdUdDetail().getLogEventi().setEditMode();
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setEditMode();
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setDisableHourGlass(true);
        } else {
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setHidden(true);
            getForm().getModelliXsdUdDetail().getLogEventi().setHidden(true);
            getForm().getModelliXsdUdDetail().getLogEventi().setViewMode();
            getForm().getModelliXsdUdDetail().getScaricaXsdButton().setViewMode();
        }
    }

    @Override
    public void scaricaXsdButton() throws EMFError {
        String nomeTipo = null;
        if (getForm().getModelliXsdUdDetail().getId_modello_xsd_ud().parse() != null) {
            nomeTipo = modelliXsdUdEjb
                    .getDecModelloXsdUd(getForm().getModelliXsdUdDetail().getId_modello_xsd_ud().parse())
                    .getTiModelloXsd();
        }

        String codiceVersione = getForm().getModelliXsdUdDetail().getCd_xsd().parse();
        String filename = nomeTipo + "_xsd_" + codiceVersione;
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename + ".zip");
        // definiamo l'output previsto che sarà un file in formato zip
        // di cui si occuperà la servlet per fare il download
        ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
        try {
            zipXsdModUd(out, getForm().getModelliXsdUdDetail().getBl_xsd().parse(), filename);
            out.flush();
            out.close();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
            LOG.error(e.getMessage(), e);
        }
    }

    /*
     *
     * Metodo per comprimere il clob presente all'interno della tabella Dec_Modello_Xsd_Fascicolo, contenente il file
     * xsd.
     *
     */
    private void zipXsdModUd(ZipOutputStream out, String blXsd, String filename) throws IOException {

        // definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        if (StringUtils.isNotBlank(blXsd)) {
            byte[] blob = blXsd.getBytes();
            try (InputStream is = new ByteArrayInputStream(blob)) {
                int count;
                out.putNextEntry(new ZipEntry(filename + ".xsd"));
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                out.closeEntry();
            }
        }
    }

    public void triggerModelliXsdUdDetailId_ambienteOnTriggerJs() throws EMFError {
        getForm().getModelliXsdUdDetail().getId_ambiente().post(getRequest());
        redirectToAjax(getForm().getModelliXsdUdDetail().asJSON());
    }
}
