package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutTitolariAbstractAction;
import it.eng.parer.slite.gen.form.StrutTitolariForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTitolRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolRowBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolTableBean;
import it.eng.parer.titolario.xml.CreaTitolario;
import it.eng.parer.titolario.xml.LivelloType;
import it.eng.parer.titolario.xml.ModificaTitolario;
import it.eng.parer.titolario.xml.TipoFormatoLivelloType;
import it.eng.parer.titolario.xml.TitolarioType;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.dto.Voce;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb.StrutTitolariCheck;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb.StrutTitolariEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils.AttributesTitolario;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.xml.utils.XmlUtils;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.wizard.Wizard;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.xml.sax.SAXException;

/**
 *
 * @author Bonora_L
 */
public class StrutTitolariAction extends StrutTitolariAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(StrutTitolariAction.class.getName());

    private static final String CREA_XSD_FILENAME = "/xsd/CreaTitolario_1.0.xsd";
    private static final String MODIFICA_XSD_FILENAME = "/xsd/ModificaTitolario_1.0.xsd";

    @EJB(mappedName = "java:app/Parer-ejb/StrutTitolariCheck")
    private StrutTitolariCheck checker;
    @EJB(mappedName = "java:app/Parer-ejb/StrutTitolariEjb")
    private StrutTitolariEjb titolariEjb;

    @EJB(mappedName = "java:app/Parer-ejb/XmlContextCache")
    private XmlContextCache xmlContextCache;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    public void loadImportaTitolario() throws EMFError {
        getForm().getImportaTitolario().reset();
        getForm().getImportaTitolario().setEditMode();
        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia());
        getForm().getImportaTitolario().getCd_registro_doc_invio().setDecodeMap(
                DecodeMap.Factory.newInstance(tmpTableBeanReg, "cd_registro_unita_doc", "cd_registro_unita_doc"));
        getForm().getImportaTitolario().getDt_istituz().setValue(ActionUtils.getStringDate(new Date()));
        getForm().getImportaTitolario().getDt_soppres().setValue(ActionUtils.getStringDate(null));

        if (getForm().getTitolariList().getStatus().equals(Status.update)) {
            DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
            getForm().getImportaTitolario()
                    .copyFromBean(titolariEjb.getDocTrasmRowBean(row.getIdTitol(), Calendar.getInstance().getTime()));

            getForm().getImportaTitolario().getDt_soppres().setHidden(true);
            getForm().getImportaTitolario().getDt_istituz().setValue(null);
        }

        forwardToPublisher(Application.Publisher.IMPORTA_TITOLARIO);
    }

    @Override
    public void process() throws EMFError {

        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());

        if (isMultipart) {
            if (getLastPublisher().equals(Application.Publisher.IMPORTA_TITOLARIO)) {
                readUploadForm();
            }

        }
    }

    /**
     * Metodo che legge la form e passa i dati alla validazione
     *
     * @throws EMFError
     *             errore generico
     */
    public void readUploadForm() throws EMFError {

        getMessageBox().clear();

        int sizeMb = WebConstants.FILESIZE * WebConstants.FILESIZE;

        try {
            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();

            // maximum size that will be stored in memory
            factory.setSizeThreshold(sizeMb);
            Properties props = new Properties();
            try {
                props.load(this.getClass().getClassLoader().getResourceAsStream("/Sacer.properties"));
            } catch (IOException ex) {
                throw new EMFError(EMFError.BLOCKING, "Errore nel caricamento delle impostazioni per l'upload", ex);
            }
            // the location for saving data that is larger than
            factory.setRepository(new File(props.getProperty("loadXsdApp.upload.directory")));
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(Long.parseLong(props.getProperty("loadXsdApp.maxRequestSize")));
            upload.setFileSizeMax(Long.parseLong(props.getProperty("loadXsdApp.maxFileSize")));
            List items = upload.parseRequest(getRequest());
            Iterator iter = items.iterator();

            DiskFileItem tmpFileItem = null;

            while (iter.hasNext()) {

                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    tmpFileItem = (DiskFileItem) item;
                } else if (item.getFieldName()
                        .equals(getForm().getImportaTitolario().getCd_registro_doc_invio().getName())) {
                    getForm().getImportaTitolario().getCd_registro_doc_invio().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getImportaTitolario().getAa_doc_invio().getName())) {
                    getForm().getImportaTitolario().getAa_doc_invio().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getImportaTitolario().getCd_doc_invio().getName())) {
                    getForm().getImportaTitolario().getCd_doc_invio().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getImportaTitolario().getDt_doc_invio().getName())) {
                    getForm().getImportaTitolario().getDt_doc_invio().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getImportaTitolario().getDt_istituz().getName())) {
                    getForm().getImportaTitolario().getDt_istituz().setValue(item.getString());
                } else if (item.getFieldName().equals(getForm().getImportaTitolario().getDt_soppres().getName())) {
                    getForm().getImportaTitolario().getDt_soppres().setValue(item.getString());
                }
            }

            getForm().getImportaTitolario().validate(getMessageBox());
            // controllo esistenza del file
            if (tmpFileItem != null && (StringUtils.isBlank(tmpFileItem.getName()) || tmpFileItem.getSize() == 0)) {
                getMessageBox().addError("Nessun file selezionato");
            }

            // controllo esistenza chiave
            // if (!getMessageBox().hasError()) {
            // if (titolariEjb.existChiaveUd(getForm().getStrutRif().getId_strut().parse(),
            // getForm().getImportaTitolario().getCd_registro_doc_invio().parse(),
            // getForm().getImportaTitolario().getAa_doc_invio().parse(),
            // getForm().getImportaTitolario().getCd_doc_invio().parse(),
            // new Date(getForm().getImportaTitolario().getDt_doc_invio().parse().getTime()))) {
            // getMessageBox().addError("Chiave del documento inesistente");
            // }
            // }
            FileBinario fileBin;
            // conversione in stringa
            byte[] titolario = null;
            if (!getMessageBox().hasError()) {
                fileBin = getFileBinario(tmpFileItem);
                titolario = fileBin.getDati();
                log.info("Eseguo validazione dell'xml con l'xsd di gestione titolario");
                try {
                    String xsd = CREA_XSD_FILENAME;
                    if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                        xsd = MODIFICA_XSD_FILENAME;
                    }
                    XmlUtils.validateXml(this.getClass().getClassLoader().getResourceAsStream(xsd), titolario);
                    log.info("Documento validato con successo");
                } catch (SAXException | IOException ex) {
                    log.error(ex.getMessage(), ex);
                    getMessageBox().addError("Il file non rispetta l'XSD previsto per lo scambio");
                }
            }

            // Modificare in base all'intervento da eseguire sul titolario (CREA, MODIFICA, CHIUDI) in quanto cambia
            // l'unmarshaller da usare
            if (getForm().getTitolariList().getStatus().equals(Status.insert)) {
                handleCreazioneTitolario(titolario);
            } else if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                handleModificaTitolario(titolario);
            }
        } catch (FileUploadException ex) {
            log.error("Eccezione nell'upload dei file", ex);
            getMessageBox().addError("Eccezione nell'upload dei file", ex);
            forwardToPublisher(Application.Publisher.IMPORTA_TITOLARIO);
        } catch (Exception ex) {
            log.error("Eccezione generica nell'importazione del titolario", ex);
            getMessageBox().addError("Si \u00E8 verificata un'eccezione nell'importazione del titolario: "
                    + ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(Application.Publisher.IMPORTA_TITOLARIO);
        }
    }

    public void handleCreazioneTitolario(byte[] titolario) throws EMFError {
        CreaTitolario creaTitolarioObj = null;
        if (!getMessageBox().hasError()) {
            creaTitolarioObj = unmarshallTitolario(titolario, CreaTitolario.class);
        }
        if (!getMessageBox().hasError() && creaTitolarioObj != null) {
            log.info("Eseguo il controllo sull'esistenza di un titolario con medesimo nome");
            if (titolariEjb.existTitolario(getForm().getStrutRif().getId_strut().parse(),
                    creaTitolarioObj.getIntestazione().getDenominazione())) {
                getMessageBox().addError(
                        "Titolario con denominazione gi\u00E0 esistente, modificare il campo denominazione all'interno del file e riprovare");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Date dtIstituz = getForm().getImportaTitolario().getDt_istituz().parse() != null
                    ? new Date(getForm().getImportaTitolario().getDt_istituz().parse().getTime()) : null;
            Date dtSoppres = getForm().getImportaTitolario().getDt_soppres().parse() != null
                    ? new Date(getForm().getImportaTitolario().getDt_soppres().parse().getTime()) : calendar.getTime();
            if (!getMessageBox().hasError()) {
                log.info("Eseguo il controllo sull'esistenza di un titolario con simile periodo di validit\u00E0");
                if (titolariEjb.existTitolario(getForm().getStrutRif().getId_strut().parse(), dtIstituz, dtSoppres)) {
                    getMessageBox().addError(
                            "L'intervallo di validit\u00E0 indicato si sovrappone a quello di un'altra versione di titolario");
                }
            }
            if (!getMessageBox().hasError()) {
                log.info("Eseguo il controllo dei tag");
                log.info("Controllo livelli");
                List<LivelloType> livelli = checker.checkLivelli(creaTitolarioObj, getMessageBox());
                if (livelli != null) {
                    log.info("Controllo livelli OK - inizio il parsing delle voci");
                    Map<String, Voce> vociMap = checker.parseVoci(null,
                            creaTitolarioObj.getListaOperazioniVoce().getOperazioneVoce(), livelli,
                            new Date(getForm().getImportaTitolario().getDt_istituz().parse().getTime()),
                            getMessageBox());
                    if (!getMessageBox().hasError() && !vociMap.isEmpty()) {
                        log.info("Parsing OK - ottenuta l'alberatura di voci");
                        log.info("Eseguo il salvataggio");
                        Date dtDocInvio = getForm().getImportaTitolario().getDt_doc_invio().parse() != null
                                ? new Date(getForm().getImportaTitolario().getDt_doc_invio().parse().getTime()) : null;

                        titolariEjb.saveMap(creaTitolarioObj.getIntestazione(), vociMap, livelli,
                                getForm().getStrutRif().getId_strut().parse(),
                                getForm().getImportaTitolario().getCd_registro_doc_invio().parse(),
                                getForm().getImportaTitolario().getAa_doc_invio().parse(),
                                getForm().getImportaTitolario().getCd_doc_invio().parse(), dtDocInvio, dtIstituz,
                                dtSoppres);
                        getMessageBox().addInfo("Inserimento del titolario "
                                + creaTitolarioObj.getIntestazione().getDenominazione() + " avvenuto con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                        goBack();
                    }
                }
            }
        }
    }

    public void handleModificaTitolario(byte[] titolario) throws EMFError {
        ModificaTitolario modificaTitolarioObj = null;
        if (!getMessageBox().hasError()) {
            modificaTitolarioObj = unmarshallTitolario(titolario, ModificaTitolario.class);
        }
        if (!getMessageBox().hasError() && modificaTitolarioObj != null) {
            try {
                log.info("Recupero i livelli");
                DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
                List<LivelloType> livelli = titolariEjb.getLivelliForParsing(row.getIdTitol());
                log.info("Ottengo la mappa di voci preesistente dal database");
                Map<String, Voce> vociMap = titolariEjb.getVociMap(row.getIdTitol(), livelli, null);
                log.info("Eseguo il controllo dei tag");
                if (!getMessageBox().hasError() && !vociMap.isEmpty()) {
                    checker.parseVoci(vociMap, modificaTitolarioObj.getListaOperazioniVoce().getOperazioneVoce(),
                            livelli, new Date(row.getDtIstituz().getTime()), getMessageBox());
                }
                if (!getMessageBox().hasError() && !vociMap.isEmpty()) {
                    log.info("Parsing OK - ottenuta l'alberatura di voci");
                    log.info("Eseguo il salvataggio");

                    Date dtDocInvio = getForm().getImportaTitolario().getDt_doc_invio().parse() != null
                            ? new Date(getForm().getImportaTitolario().getDt_doc_invio().parse().getTime()) : null;
                    Date dtIstituz = getForm().getImportaTitolario().getDt_istituz().parse() != null
                            ? new Date(getForm().getImportaTitolario().getDt_istituz().parse().getTime()) : null;

                    titolariEjb.saveMap(row.getIdTitol(), null, vociMap,
                            getForm().getImportaTitolario().getCd_registro_doc_invio().parse(),
                            getForm().getImportaTitolario().getAa_doc_invio().parse(),
                            getForm().getImportaTitolario().getCd_doc_invio().parse(), dtDocInvio, dtIstituz);
                    getMessageBox().addInfo("Modifica del titolario avvenuta con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    goBack();
                }
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
    }

    public <T> T unmarshallTitolario(byte[] titolario, Class<T> classType) {
        // Eseguo l'unmarshalling dell'xml
        T titolarioObj = null;
        log.info("Eseguo l'unmarshalling dell'xml");
        InputStream titolarioIS = new ByteArrayInputStream(titolario);
        try {
            JAXBContext titolarioCtx = null;
            String xsd = null;
            if (classType.equals(CreaTitolario.class)) {
                titolarioCtx = xmlContextCache.getCreaTitolarioCtx();
                xsd = CREA_XSD_FILENAME;
            } else if (classType.equals(ModificaTitolario.class)) {
                titolarioCtx = xmlContextCache.getModificaTitolarioCtx();
                xsd = MODIFICA_XSD_FILENAME;
            }

            Unmarshaller um = titolarioCtx.createUnmarshaller();
            um.setSchema(XmlUtils.getSchemaValidation(this.getClass().getClassLoader().getResourceAsStream(xsd)));
            titolarioObj = XmlUtils.unmarshallResponse(um, titolarioIS, classType);
        } catch (SAXException | JAXBException ex) {
            log.error(ex.getMessage(), ex);
            getMessageBox().addError("Eccezione nel parsing del xml: " + ExceptionUtils.getRootCauseMessage(ex));
        }
        return titolarioObj;
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
    public void importaFileTitolario() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getTitolariList().getName())) {
                DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
                Date showDate;
                if (titolariEjb.isTitolarioChiuso(row.getIdTitol())) {
                    showDate = row.getDtSoppres();
                } else {
                    showDate = Calendar.getInstance().getTime();
                }
                loadTitolarioTree(showDate);
            }

            String cessato = (String) getRequest().getParameter("cessato");
            if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                getForm().getTitolariList().setUserOperations(true, false, false, false);
            }
        }
    }

    public void loadTitolarioTree(Date day) throws EMFError {
        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.DATE_TITOLARIO.name(), day);
        getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.DATE_TITOLARIO_STRING.name(), df.format(day));

        DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
        getForm().getTitolarioDetail().copyFromBean(row);
        getForm().getTitolarioDetail().getDt_visualiz().setEditMode();
        getForm().getTitolarioDetail().getLoadUd().setEditMode();
        getForm().getTitolarioDetail().getReloadTitolario().setEditMode();

        String cessato = (String) getRequest().getParameter("cessato");
        if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
            getForm().getTitolarioDetail().getValidaTitolario().setViewMode();
            getForm().getTitolarioDetail().getChiudiTitolario().setViewMode();
        } else {
            getForm().getTitolarioDetail().getValidaTitolario().setEditMode();
            getForm().getTitolarioDetail().getChiudiTitolario().setEditMode();
        }
        getForm().getTitolarioDetail().getEsportaTitolario().setEditMode();

        getForm().getTitolarioDetail().getValidaTitolario()
                .setHidden(row.getTiStatoTitol().equals(CostantiDB.StatoTitolario.VALIDATO.name()));
        getForm().getTitolarioDetail().getChiudiTitolario().setHidden(titolariEjb.isTitolarioChiuso(row.getIdTitol())
                || (StringUtils.isNotBlank(cessato) && "1".equals(cessato)));

        getForm().getDocTrasm().copyFromBean(titolariEjb.getDocTrasmRowBean(row.getIdTitol(), day));
        DecVTreeTitolTableBean treeTableBean = titolariEjb.getDecVociTreeTableBean(row.getIdTitol(), day, true);
        getForm().getTitolariTree().setTable(treeTableBean);
    }

    @Override
    public void undoDettaglio() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
    }

    @Override
    public void saveDettaglio() throws EMFError {
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getTitolariList().getName())) {
                forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.IMPORTA_TITOLARIO)) {
            getForm().getTitolariList().setStatus(Status.view);
        }
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.IMPORTA_TITOLARIO;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        log.info(publisherName);
        try {
            if (publisherName.equals(Application.Publisher.TITOLARIO_DETAIL)) {
                Date date;
                DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
                if (titolariEjb.isTitolarioChiuso(row.getIdTitol())) {
                    date = row.getDtSoppres();
                } else {
                    date = Calendar.getInstance().getTime();
                }
                loadTitolarioTree(date);
            }
        } catch (EMFError e) {
            getMessageBox().addError("Errore inaspettato in fase di ricarica dei dati del titolario");
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUT_TITOLARI;
    }

    @Secure(action = "button/StrutTitolariForm#TitolarioDetail/showDettaglioVoce")
    public void showDettaglioVoce() throws EMFError {
        String id = getRequest().getParameter("id");
        String position = getRequest().getParameter("position");
        log.debug("Test chiamata js - Selezionato nodo " + id + " con posizione " + position);

        if (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(position)) {
            DecVTreeTitolRowBean row = (DecVTreeTitolRowBean) getForm().getTitolariTree().getTable()
                    .getRow(Integer.parseInt(position));
            // Verifichiamo di aver preso la riga corretta, altrimenti qualcosa non va
            if (row.getIdVoceTitol().longValue() == Long.parseLong(id)) {
                getForm().getVoceTitolarioDetail().copyFromBean(row);
                Calendar calendar = Calendar.getInstance();
                calendar.set(2444, 11, 31, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Date defaultSoppres = calendar.getTime();
                Date dtRow = new Date(row.getDtSoppres().getTime());

                getForm().getVoceTitolarioDetail().getDt_soppres().setHidden(!dtRow.before(defaultSoppres));

                getForm().getTracciaList().setTable(titolariEjb.getTracciaVociTableBean(row.getIdVoceTitol()));
                getForm().getTracciaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getTracciaList().getTable().first();
                forwardToPublisher(Application.Publisher.VOCE_TITOLARIO_DETAIL);
            } else {
                getMessageBox().addError("Errore nel recupero dei dati della voce di classificazione");
                forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
            }
        } else {
            forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
        }
    }

    @Override
    public void reloadTitolario() throws EMFError {
        getForm().getTitolarioDetail().getDt_visualiz().post(getRequest());
        Date date = null;
        if (getForm().getTitolarioDetail().getDt_visualiz().parse() != null) {
            date = new Date(getForm().getTitolarioDetail().getDt_visualiz().parse().getTime());
            Date dtIstituz = new Date(getForm().getTitolarioDetail().getDt_istituz().parse().getTime());
            Date dtSoppres = new Date(getForm().getTitolarioDetail().getDt_soppres().parse().getTime());
            SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
            if (date.before(dtIstituz) || date.after(dtSoppres)) {
                getMessageBox().addError("Il titolario selezionato risulta valido dalla data " + df.format(dtIstituz)
                        + " alla data " + df.format(dtSoppres));
            }
        }
        if (date != null && !getMessageBox().hasError()) {
            loadTitolarioTree(date);
        }
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    @Override
    public void loadUd() throws EMFError {
        /*
         * Verifica se esiste l'unità documentaria con i dati del documento di trasmissione. Se non esiste messaggio di
         * errore
         */
        DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
        BigDecimal idStrut = row.getIdStrut();
        String registro = getForm().getDocTrasm().getCd_registro_doc_invio().parse();
        BigDecimal anno = getForm().getDocTrasm().getAa_doc_invio().parse();
        String codice = getForm().getDocTrasm().getCd_doc_invio().parse();
        if (registro != null && anno != null && codice != null) {
            AroVRicUnitaDocTableBean unitaDocTB = titolariEjb.getUnitaDocTableBean(registro, anno, codice, idStrut);
            if (!unitaDocTB.isEmpty()) {
                UnitaDocumentarieForm form = new UnitaDocumentarieForm();
                form.getUnitaDocumentarieList().setTable(unitaDocTB);
                redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                        "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=0",
                        form);
            } else {
                getMessageBox().addError("Unit\u00E0 documentaria inesistente all'interno del sistema");
                forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
            }
        } else {
            forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
        }
    }

    @Override
    public void validaTitolario() throws EMFError {
        getRequest().setAttribute("validaTitolarioBox", true);
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    public void confermaValidazioneTitolario() throws EMFError {
        DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
        try {
            titolariEjb.validaTitolario(row.getIdTitol());
            row.setTiStatoTitol(CostantiDB.StatoTitolario.VALIDATO.name());
            getForm().getTitolarioDetail().getValidaTitolario().setHidden(true);
            getMessageBox().addInfo("Validazione del titolario avvenuta con successo");
            getMessageBox().setViewMode(MessageBox.ViewMode.plain);
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    @Override
    public void chiudiTitolario() throws EMFError {
        getRequest().setAttribute("chiudiTitolarioBox", true);
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    public void confermaChiusuraTitolario() throws EMFError {
        String date = (String) getRequest().getParameter("Dt_soppres");
        String note = (String) getRequest().getParameter("Dl_note");
        DateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);

        if (StringUtils.isBlank(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            date = df.format(calendar.getTime());
        }

        Date dataFineValidita = null;
        try {
            dataFineValidita = df.parse(date);
        } catch (ParseException ex) {
            log.error("Errore nella data di fine validit\u00E0 inserita", ex);
            getMessageBox().addError("Errore nella data di fine validit\u00E0 inserita");
        }
        if (!getMessageBox().hasError()) {
            try {
                log.info("Recupero i livelli");
                DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
                List<LivelloType> livelli = titolariEjb.getLivelliForParsing(row.getIdTitol());
                log.info("Ottengo la mappa di voci preesistente dal database");
                Map<String, Voce> vociMap = titolariEjb.getVociMap(row.getIdTitol(), livelli, null);

                titolariEjb.chiudiTitolario(row.getIdTitol(), vociMap, dataFineValidita, note);
                getForm().getTitolarioDetail().getValidaTitolario().setHidden(true);
                getForm().getTitolarioDetail().getChiudiTitolario().setHidden(true);
                getMessageBox().addInfo("Chiusura del titolario avvenuta con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    public void loadWizard() throws EMFError {
        getForm().getInserimentoWizard().reset();
        getForm().getDatiTitolarioInserimento().reset();
        getForm().getLivelliInserimento().reset();
        getForm().getDatiTitolarioInserimento().setEditMode();
        getForm().getLivelliInserimento().setEditMode();

        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                getUser().getIdOrganizzazioneFoglia());
        getForm().getDatiTitolarioInserimento().getCd_registro_doc_invio().setDecodeMap(
                DecodeMap.Factory.newInstance(tmpTableBeanReg, "cd_registro_unita_doc", "cd_registro_unita_doc"));
        getForm().getDatiTitolarioInserimento().getDt_istituz().setValue(ActionUtils.getStringDate(new Date()));
        getForm().getDatiTitolarioInserimento().getDt_soppres().setValue(ActionUtils.getStringDate(null));
        getForm().getLivelliInserimento().getTi_fmt_voce_titol().setDecodeMap(ComboGetter
                .getMappaSortedGenericEnum("ti_fmt_voce_titol", CostantiDB.TipoFormatoLivelloTitolario.values()));
        getForm().getVociInserimento().getFl_uso_classif().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getVociInserimento().getConserv_unlimited().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        cleanSession();
        getForm().getVociInserimento().getWarning_shown().setChecked(false);

        if (getForm().getTitolariList().getStatus().equals(Status.update)) {
            String riga = getRequest().getParameter("riga");
            if (riga != null) {
                /*
                 * Sono arrivato dalla pagina delle strutture Nel caso arrivassi dal dettaglio del titolario questi dati
                 * sarebbero già corretti
                 */
                setTableName(getForm().getTitolariList().getName());
                setRiga(riga);
                getForm().getTitolariList().getTable().setCurrentRowIndex(Integer.parseInt(riga));
            }

            DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
            initWizard(row);
            getForm().getLivelliList().setHideUpdateButton(false);
            getForm().getVociList().setHideUpdateButton(false);
        } else {
            getForm().getLivelliList().setTable(new BaseTable());
            getForm().getVociList().setTable(new BaseTable());
            getForm().getVociList().getTable()
                    .addSortingRule(getForm().getVociList().getCd_composito_voce_titol().getName(), SortingRule.ASC);

            getForm().getLivelliList().setHideUpdateButton(true);
            getForm().getVociList().setHideUpdateButton(true);
        }
        getForm().getLivelliList().getTable().addSortingRule(getForm().getLivelliList().getNi_livello().getName(),
                SortingRule.ASC);
        getForm().getLivelliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getVociList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        forwardToPublisher(Application.Publisher.TITOLARIO_WIZARD);
    }

    public void initWizard(DecTitolRowBean row) throws EMFError {
        AttributesTitolario attributes;
        try {
            attributes = titolariEjb.initAttributesTitolario(row.getIdTitol());

            getForm().getLivelliList().setTable(attributes.getLivelliTableBean());
            getForm().getVociList().setTable(attributes.getVociTableBean());
            getForm().getVociList().getTable().first();

            getForm().getDatiTitolarioInserimento().copyFromBean(row);
            getForm().getDatiTitolarioInserimento().getDt_istituz().setViewMode();
            getForm().getDatiTitolarioInserimento().getDt_soppres().setViewMode();

            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_TITOLARIO.name(),
                    attributes.getLivelli());
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name(),
                    attributes.getLivelliParsing());
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.NOME_LIVELLI_TITOLARIO.name(),
                    attributes.getNomeLivelli());
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name(),
                    attributes.getNumeroOrdinePrimoLivelloSet());
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name(), attributes.getVociMap());
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name(),
                    attributes.getLivelliVociMap());
        } catch (ParerUserError ex) {
            log.error(ex.getDescription(), ex);
            getMessageBox().addError(ex.getDescription());
        }
    }

    @Override
    public boolean inserimentoWizardOnSave() throws EMFError {
        boolean result = true;
        Map<String, Voce> vociMap = (Map<String, Voce>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
        List<LivelloType> livelli = (List<LivelloType>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());
        StrutTitolariForm.DatiTitolarioInserimento datiTitolario = getForm().getDatiTitolarioInserimento();
        TitolarioType titolario = new TitolarioType();
        titolario.setDenominazione(datiTitolario.getNm_titol().parse());
        titolario.setNoteTitolario(datiTitolario.getDl_note().parse());
        titolario.setNumeroLivelliUtilizzati(datiTitolario.getNi_livelli().parse().toBigInteger());
        titolario.setSeparatoreVociTitolarioFascicoli(datiTitolario.getCd_sep_fascicolo().parse());
        if (!getMessageBox().hasError() && !vociMap.isEmpty()) {
            log.info("Parsing OK - ottenuta l'alberatura di voci");
            log.info("Eseguo il salvataggio");
            Calendar calSoppres = Calendar.getInstance();
            calSoppres.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            calSoppres.set(Calendar.MILLISECOND, 0);

            Date dtDocInvio = datiTitolario.getDt_doc_invio().parse() != null
                    ? new Date(datiTitolario.getDt_doc_invio().parse().getTime()) : null;
            Date dtIstituz = datiTitolario.getDt_istituz().parse() != null
                    ? new Date(datiTitolario.getDt_istituz().parse().getTime()) : null;
            Date dtSoppres = datiTitolario.getDt_soppres().parse() != null
                    ? new Date(datiTitolario.getDt_soppres().parse().getTime()) : calSoppres.getTime();

            try {
                if (getForm().getTitolariList().getStatus().equals(Status.insert)) {
                    titolariEjb.saveMap(titolario, vociMap, livelli, getForm().getStrutRif().getId_strut().parse(),
                            datiTitolario.getCd_registro_doc_invio().parse(), datiTitolario.getAa_doc_invio().parse(),
                            datiTitolario.getCd_doc_invio().parse(), dtDocInvio, dtIstituz, dtSoppres);
                    getMessageBox().addInfo(
                            "Inserimento del titolario " + titolario.getDenominazione() + " avvenuto con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                } else if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                    DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
                    // Eseguo il salvataggio dei livelli
                    titolariEjb.saveLevels(row.getIdTitol(), getForm().getLivelliList().getTable());
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    // Eseguo il salvataggio delle voci
                    titolariEjb.saveMap(row.getIdTitol(), titolario, vociMap,
                            datiTitolario.getCd_registro_doc_invio().parse(), datiTitolario.getAa_doc_invio().parse(),
                            datiTitolario.getCd_doc_invio().parse(), dtDocInvio, calendar.getTime());
                    getMessageBox().addInfo("Modifica del titolario avvenuta con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
                getForm().getTitolariList().setStatus(Status.view);
                cleanSession();
                goBack();
            } catch (ParerUserError pue) {
                log.error("Eccezione generica nel salvataggio del titolario", pue);
                getMessageBox().addError(pue.getDescription());
                result = false;
            } catch (Exception ex) {
                log.error("Eccezione generica nel salvataggio del titolario", ex);
                getMessageBox().addError("Si \u00E8 verificata un'eccezione nel salvataggio del titolario: "
                        + ExceptionUtils.getRootCauseMessage(ex));
                result = false;
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void inserimentoWizardOnCancel() throws EMFError {
        cleanSession();
        getForm().getTitolariList().setStatus(Status.view);
        goBack();
    }

    public void cleanSession() {
        getSession().removeAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_TITOLARIO.name());
        getSession().removeAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());
        getSession().removeAttribute(WebConstants.PARAMETER_TITOLARIO.NOME_LIVELLI_TITOLARIO.name());
        getSession().removeAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name());
        getSession().removeAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
        getSession().removeAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());
    }

    @Override
    public String getDefaultInserimentoWizardPublisher() throws EMFError {
        return Application.Publisher.TITOLARIO_WIZARD;
    }

    @Override
    public void inserimentoWizardDatiTitolarioOnEnter() throws EMFError {
        getForm().getDatiTitolarioInserimento().setEditMode();
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public boolean inserimentoWizardDatiTitolarioOnExit() throws EMFError {
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
        boolean result = true;
        if (getForm().getDatiTitolarioInserimento().postAndValidate(getRequest(), getMessageBox())) {
            StrutTitolariForm.DatiTitolarioInserimento datiTitolarioInserimento = getForm()
                    .getDatiTitolarioInserimento();
            BigDecimal numLivelli = datiTitolarioInserimento.getNi_livelli().parse();
            BigDecimal anno = datiTitolarioInserimento.getAa_doc_invio().parse();
            String codice = datiTitolarioInserimento.getCd_doc_invio().parse();
            String registro = datiTitolarioInserimento.getCd_registro_doc_invio().parse();
            Timestamp data = datiTitolarioInserimento.getDt_doc_invio().parse();
            if (!(anno != null && codice != null && registro != null && data != null)
                    && (anno != null || codice != null || registro != null || data != null)) {
                getMessageBox().addError("Inserire tutti i dati riguardanti il documento di trasmissione");
                result = false;
            }
            if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                log.info("Verifico che non sia stato modificato in negativo il numero dei livelli");
                DecTitolRowBean titolRow = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
                BigDecimal numLivelliDiff = numLivelli.subtract(titolRow.getNiLivelli());
                if (numLivelliDiff.intValue() < 0) {
                    HashMap<BigDecimal, BaseTableInterface<?>> livelliVociMap = (HashMap<BigDecimal, BaseTableInterface<?>>) getSession()
                            .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());
                    if (livelliVociMap != null) {
                        // Ho ridotto i livelli, devo controllare che i livelli superiori a numLivelli siano senza voci
                        // altrimenti errore
                        for (int index = (numLivelli.intValue() + 1); index <= titolRow.getNiLivelli()
                                .intValue(); index++) {
                            if (!livelliVociMap.get(new BigDecimal(index)).isEmpty()) {
                                getMessageBox().addError(
                                        "Impossibile ridurre il numero dei livelli in quanto esistono delle voci associate ad essi");
                                result = false;
                                break;
                            }
                        }
                    }
                }
                log.info("Eseguo il controllo sull'esistenza di un titolario con medesimo nome");
                if (titolariEjb.existTitolario(getForm().getStrutRif().getId_strut().parse(),
                        datiTitolarioInserimento.getNm_titol().parse(), titolRow.getIdTitol())) {
                    getMessageBox().addError(
                            "Titolario con denominazione gi\u00E0 esistente, modificare il campo denominazione all'interno del file e riprovare");
                    result = false;
                }
            } else {
                log.info("Eseguo il controllo sull'esistenza di un titolario con medesimo nome");
                if (titolariEjb.existTitolario(getForm().getStrutRif().getId_strut().parse(),
                        datiTitolarioInserimento.getNm_titol().parse())) {
                    getMessageBox().addError(
                            "Titolario con denominazione gi\u00E0 esistente, modificare il campo denominazione all'interno del file e riprovare");
                    result = false;
                }

                Calendar calendar = Calendar.getInstance();
                calendar.set(2444, 11, 31, 0, 0, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Date dtIstituz = datiTitolarioInserimento.getDt_istituz().parse() != null
                        ? new Date(datiTitolarioInserimento.getDt_istituz().parse().getTime()) : null;
                Date dtSoppres = datiTitolarioInserimento.getDt_soppres().parse() != null
                        ? new Date(datiTitolarioInserimento.getDt_soppres().parse().getTime()) : calendar.getTime();
                if (!getMessageBox().hasError()) {
                    log.info("Eseguo il controllo sull'esistenza di un titolario con simile periodo di validit\u00E0");
                    if (titolariEjb.existTitolario(getForm().getStrutRif().getId_strut().parse(), dtIstituz,
                            dtSoppres)) {
                        getMessageBox().addError(
                                "L'intervallo di validit\u00E0 indicato si sovrappone a quello di un'altra versione di titolario");
                        result = false;
                    }
                }
            }
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void inserimentoWizardLivelliTitolarioOnEnter() throws EMFError {
        getForm().getLivelliInserimento().clear();
        getForm().getLivelliInserimento().setEditMode();

        int numLivelli = getForm().getDatiTitolarioInserimento().getNi_livelli().parse().intValue();
        int livelliInseriti = getForm().getLivelliList().getTable().size();

        int livelloDaInserire = (livelliInseriti < numLivelli) ? livelliInseriti + 1 : numLivelli;
        getForm().getLivelliInserimento().getNi_livello().setValue(String.valueOf(livelloDaInserire));

        if (getForm().getTitolariList().getStatus().equals(Status.update)) {
            getForm().getLivelliList().setHideUpdateButton(false);
            getForm().getLivelliInserimento().getCleanLivello().setHidden(false);
        } else {
            getForm().getLivelliList().setHideUpdateButton(true);
            getForm().getLivelliInserimento().getCleanLivello().setHidden(true);
        }

        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public boolean inserimentoWizardLivelliTitolarioOnExit() throws EMFError {
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
        getForm().getLivelliInserimento().clear();
        boolean result = true;
        BigDecimal numLivelli = getForm().getDatiTitolarioInserimento().getNi_livelli().parse();
        if (getForm().getLivelliList().getTable().size() != numLivelli.intValue()) {
            getMessageBox().addError(
                    "\u00C8 necessario indicare le informazioni per ciascuno dei livelli dichiarati nel campo "
                            + getForm().getDatiTitolarioInserimento().getNi_livelli().getDescription());
            getForm().getLivelliInserimento().getNi_livello()
                    .setValue(String.valueOf(getForm().getLivelliList().getTable().size() + 1));
            result = false;
        }
        if (result) {
            HashMap<BigDecimal, BaseTableInterface<?>> livelliVociMap = new HashMap<>();
            List<LivelloType> livelliList = new ArrayList<>();
            for (BaseRowInterface row : (BaseTableInterface<?>) getForm().getLivelliList().getTable()) {
                LivelloType tmpLiv = new LivelloType();
                tmpLiv.setCarattereSeparatoreLivello(
                        row.getString(getForm().getLivelliList().getCd_sep_livello().getName()));
                tmpLiv.setNomeLivello(row.getString(getForm().getLivelliList().getNm_livello_titol().getName()));
                tmpLiv.setNumeroLivello(
                        row.getBigDecimal(getForm().getLivelliList().getNi_livello().getName()).toBigInteger());
                tmpLiv.setTipoFormatoLivello(TipoFormatoLivelloType
                        .fromValue(row.getString(getForm().getLivelliList().getTi_fmt_voce_titol().getName())));

                livelliList.add(tmpLiv);
                livelliVociMap.put(row.getBigDecimal(getForm().getLivelliList().getNi_livello().getName()),
                        new BaseTable());
            }
            // FIXME: Controllare l'ordinamento (vedi voce Ordinamenti di liste con "delegate" sulla wiki)
            Collections.sort(livelliList, new Comparator<LivelloType>() {
                @Override
                public int compare(LivelloType o1, LivelloType o2) {
                    Integer s1 = o1.getNumeroLivello().intValue();
                    Integer s2 = o2.getNumeroLivello().intValue();
                    return s1.compareTo(s2);

                }
            });

            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name(), livelliList);
            if (getSession().getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name()) == null) {
                getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name(), livelliVociMap);
            }
        }
        return result;
    }

    @Override
    public void inserimentoWizardVociTitolarioOnEnter() throws EMFError {
        getForm().getVociInserimento().clear();
        getForm().getVociInserimento().setEditMode();
        getForm().getVociInserimento().getDt_istituz()
                .setValue(getForm().getDatiTitolarioInserimento().getDt_istituz().getValue());

        String key = getForm().getLivelliList().getNi_livello().getName();
        String value = getForm().getLivelliList().getNm_livello_titol().getName();

        DecodeMap livelli = DecodeMap.Factory.newInstance(getForm().getLivelliList().getTable(), key, value);
        getForm().getVociInserimento().getNi_livello().setDecodeMap(livelli);
        getForm().getVociInserimento().getCd_composito_voce_padre().setDecodeMap(new DecodeMap());

        if (getForm().getTitolariList().getStatus().equals(Status.update)) {
            getForm().getVociList().setHideUpdateButton(false);
            getForm().getVociInserimento().getCleanVoce().setHidden(false);
        } else {
            getForm().getVociList().setHideUpdateButton(true);
            getForm().getVociInserimento().getCleanVoce().setHidden(true);
        }

        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public boolean inserimentoWizardVociTitolarioOnExit() throws EMFError {
        boolean result = true;
        if (getForm().getVociList().getTable().isEmpty()) {
            getMessageBox().addError("Non \u00E8 stata inserita nessuna voce di classificazione");
            result = false;
        }
        return result;
    }

    @Override
    public void inserimentoWizardAlberoTitolarioOnEnter() throws EMFError {
        Map<String, Voce> vociMap = (Map<String, Voce>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
        if (vociMap != null && !vociMap.isEmpty()) {
            DecVTreeTitolTableBean treeTableBean = titolariEjb
                    .getDecVociTreeTableBean(getForm().getDatiTitolarioInserimento().getNm_titol().parse(), vociMap);
            getForm().getTitolariTree().setTable(treeTableBean);
        }
    }

    @Override
    public boolean inserimentoWizardAlberoTitolarioOnExit() throws EMFError {
        return true;
    }

    @Override
    public void addLivello() throws EMFError {
        if (getForm().getLivelliInserimento().postAndValidate(getRequest(), getMessageBox())) {
            int numLivelli = getForm().getDatiTitolarioInserimento().getNi_livelli().parse().intValue();
            int numLivello = getForm().getLivelliInserimento().getNi_livello().parse().intValue();
            String nomeLivello = getForm().getLivelliInserimento().getNm_livello_titol().parse();

            Set<Integer> livelli = (Set<Integer>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_TITOLARIO.name());
            Set<String> nomeLivelli = (Set<String>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.NOME_LIVELLI_TITOLARIO.name());
            if (livelli == null) {
                livelli = new HashSet<>();
            }
            if (nomeLivelli == null) {
                nomeLivelli = new HashSet<>();
            }

            if (numLivello > numLivelli) {
                getMessageBox().addError(
                        "Il numero che identifica il livello \u00E8 superiore al numero di livelli dichiarato");
            } else if (numLivello <= 0) {
                getMessageBox().addError(
                        "Il numero che identifica il livello deve essere un intero positivo minore o uguale al numero di livelli dichiarato");
            } else if (numLivello > 1
                    && StringUtils.isBlank(getForm().getLivelliInserimento().getCd_sep_livello().parse())) {
                getMessageBox()
                        .addError("Per tutti i livelli diversi dal primo deve essere indicato il carattere separatore");
            } else if (numLivello == 1
                    && StringUtils.isNotBlank(getForm().getLivelliInserimento().getCd_sep_livello().parse())) {
                getMessageBox().addError(
                        "Il carattere separatore NON deve essere indicato nell'inserimento del primo livello");
            } else if (getForm().getLivelliList().getTable().size() == numLivelli) {
                getMessageBox().addError(
                        "Sono gi\u00E0 stati definiti tutti i livelli definiti dal numero di livelli dichiarato");
            } else if (nomeLivelli.contains(nomeLivello)) {
                getMessageBox().addError("Nome livello gi\u00E0 definito precedentemente");
            } else if (livelli.contains(numLivello)) {
                getMessageBox().addError("Numero di livello gi\u00E0 definito precedentemente");
            } else {
                BaseRow row = new BaseRow();
                getForm().getLivelliInserimento().copyToBean(row);
                if (StringUtils
                        .isBlank(row.getString(getForm().getLivelliInserimento().getCd_sep_livello().getName()))) {
                    row.setString(getForm().getLivelliInserimento().getCd_sep_livello().getName(), null);
                }
                getForm().getLivelliList().getTable().add(row);
                getForm().getLivelliList().getTable().sort();

                livelli.add(numLivello);
                nomeLivelli.add(nomeLivello);

                getForm().getLivelliInserimento().clear();
                int livelloDaInserire = (numLivello < numLivelli) ? numLivello + 1 : numLivelli;
                getForm().getLivelliInserimento().getNi_livello().setValue(String.valueOf(livelloDaInserire));
                if (getForm().getLivelliList().getTable().size() == numLivelli) {
                    wizardNavigationOnClick(getForm().getInserimentoWizard().getName(),
                            Wizard.WizardNavigation.Next.name(),
                            getForm().getInserimentoWizard().getVociTitolario().getName());
                }
                getForm().getLivelliInserimento().setEditMode();
            }
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_TITOLARIO.name(), livelli);
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.NOME_LIVELLI_TITOLARIO.name(), nomeLivelli);
        }
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public void deleteLivelliList() throws EMFError {
        BaseRow row = (BaseRow) getForm().getLivelliList().getTable().getCurrentRow();
        int index = getForm().getLivelliList().getTable().getCurrentRowIndex();

        BigDecimal numLivello = row.getBigDecimal(getForm().getLivelliList().getNi_livello().getName());
        String nomeLivello = row.getString(getForm().getLivelliList().getNm_livello_titol().getName());
        int numLivelli = getForm().getDatiTitolarioInserimento().getNi_livelli().parse().intValue();

        Set<Integer> livelli = (Set<Integer>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_TITOLARIO.name());
        Set<String> nomeLivelli = (Set<String>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.NOME_LIVELLI_TITOLARIO.name());
        HashMap<BigDecimal, BaseTableInterface<?>> livelliVociMap = (HashMap<BigDecimal, BaseTableInterface<?>>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());

        // Se per il livello da eliminare o per quelli successivi esistono voci associate, errore
        if (livelliVociMap != null) {
            for (int indexLevel = (numLivello.intValue()); indexLevel <= numLivelli; indexLevel++) {
                if (!livelliVociMap.get(new BigDecimal(indexLevel)).isEmpty()) {
                    getMessageBox().addError(
                            "Impossibile eliminare il livello in quanto esistono delle voci associate ad esso o ai livelli successivi ad esso");
                    break;
                }
            }
        }

        if (!getMessageBox().hasError()) {
            livelli.remove(numLivello.intValue());
            nomeLivelli.remove(nomeLivello);

            getForm().getLivelliList().getTable().remove(index);
            getForm().getLivelliInserimento().getNi_livello().setValue(String.valueOf(numLivello.intValue()));
        }

        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public JSONObject triggerVociInserimentoNi_livelloOnTrigger() throws EMFError {
        getForm().getVociInserimento().post(getRequest());
        BigDecimal livello = getForm().getVociInserimento().getNi_livello().parse();
        String separatore;

        if (livello != null) {
            if (livello.intValue() > 1) {
                int livelloDaVisualizzare = livello.intValue() - 1;
                Map<BigDecimal, BaseTableInterface<?>> livelliVociMap = (Map<BigDecimal, BaseTableInterface<?>>) getSession()
                        .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());
                getForm().getVociInserimento().getCd_composito_voce_padre().setDecodeMap(
                        DecodeMap.Factory.newInstance(livelliVociMap.get(new BigDecimal(livelloDaVisualizzare)),
                                "cd_composito_voce_titol", "cd_composito_voce_titol"));

                List<LivelloType> livelli = (List<LivelloType>) getSession()
                        .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());
                separatore = livelli.get(livelloDaVisualizzare).getCarattereSeparatoreLivello();
            } else {
                Set<Integer> numeroOrdinePrimoLivelloSet = (Set<Integer>) getSession()
                        .getAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name());
                if (numeroOrdinePrimoLivelloSet == null) {
                    getForm().getVociInserimento().getNi_ord_voce_titol().setValue(String.valueOf(1));
                } else {
                    getForm().getVociInserimento().getNi_ord_voce_titol()
                            .setValue(String.valueOf(numeroOrdinePrimoLivelloSet.size() + 1));
                }

                separatore = null;
                getForm().getVociInserimento().getCd_composito_voce_padre().setDecodeMap(new DecodeMap());
                String codiceVoce = getForm().getVociInserimento().getCd_voce_titol().parse();
                getForm().getVociInserimento().getCd_composito_visualizzato().setValue(codiceVoce);
                getForm().getVociInserimento().getCd_composito_voce_titol().setValue(codiceVoce);
            }

            getForm().getVociInserimento().getCd_sep_livello().setValue(separatore);
        }

        return getForm().getVociInserimento().asJSON();
    }

    @Override
    public void addVoce() throws EMFError {
        if (getForm().getVociInserimento().postAndValidate(getRequest(), getMessageBox())) {
            Set<Integer> numOrdinePrimoLivello = (Set<Integer>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name());
            if (numOrdinePrimoLivello == null) {
                numOrdinePrimoLivello = new HashSet<>();
            }
            Map<String, Voce> vociMap = (Map<String, Voce>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
            if (vociMap == null) {
                vociMap = new HashMap<>();
            }

            Map<BigDecimal, BaseTableInterface<?>> livelliVociMap = (Map<BigDecimal, BaseTableInterface<?>>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());

            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            BaseRow row = new BaseRow();
            getForm().getVociInserimento().copyToBean(row);
            Date dataInizioValiditaTitolario = new Date(
                    getForm().getDatiTitolarioInserimento().getDt_istituz().parse().getTime());

            String codiceVoceComposito = row
                    .getString(getForm().getVociInserimento().getCd_composito_voce_titol().getName());
            int numeroOrdine = row.getBigDecimal(getForm().getVociInserimento().getNi_ord_voce_titol().getName())
                    .intValue();
            String descrizioneVoce = row.getString(getForm().getVociInserimento().getDs_voce_titol().getName());
            Date dataInizioValidita = new Date(
                    row.getTimestamp(getForm().getVociInserimento().getDt_istituz().getName()).getTime());
            Timestamp fineTimestamp = row.getTimestamp(getForm().getVociInserimento().getDt_soppres().getName());
            String flUsoClassif = row.getString(getForm().getVociInserimento().getFl_uso_classif().getName());
            Voce.AttivoClass attivoPerClassificazione = flUsoClassif.equals("1") ? Voce.AttivoClass.SI
                    : Voce.AttivoClass.NO;
            BigDecimal anniConserv = row.getBigDecimal(getForm().getVociInserimento().getNi_anni_conserv().getName());
            String unlimited = row.getString(getForm().getVociInserimento().getConserv_unlimited().getName());
            String noteVoceTitolario = row.getString(getForm().getVociInserimento().getDl_note().getName());

            Date dataFineValidita = (fineTimestamp != null) ? new Date(fineTimestamp.getTime()) : calendar.getTime();

            final BigDecimal livello = row.getBigDecimal(getForm().getVociInserimento().getNi_livello().getName());
            String nomeLivello = getForm().getVociInserimento().getNi_livello().getDecodeMap().getDescrizione(livello);
            row.setString(getForm().getVociList().getNm_livello_titol().getName(), nomeLivello);

            String separatore = row.getString(getForm().getVociInserimento().getCd_sep_livello().getName());

            if (StringUtils.isBlank(codiceVoceComposito)) {
                getMessageBox().addError(
                        "Errore inaspettato: codice voce composito non valorizzato. Verificare i dati immessi");
                log.error("Errore inaspettato: codice voce composito non valorizzato. Verificare i dati immessi");
            }

            boolean edit = false;

            if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                BigDecimal idVoceTitol = row.getBigDecimal("id_voce_titol");
                if (idVoceTitol != null) {
                    edit = true;
                }
            }

            if (!edit) {
                if (livello.equals(BigDecimal.ONE) && !numOrdinePrimoLivello.add(numeroOrdine)) {
                    getMessageBox().addError("Per lo stesso livello di " + codiceVoceComposito
                            + " \u00E8 gi\u00E0 esistente una voce con lo stesso numero d'ordine");
                    log.error("Per lo stesso livello di " + codiceVoceComposito
                            + " \u00E8 gi\u00E0 esistente una voce con lo stesso numero d'ordine");
                }
            }

            int tempoConservazione = 0;
            if (!getMessageBox().hasError()) {
                boolean error = false;
                if (unlimited != null && anniConserv != null) {
                    if ((unlimited.equals("1") && !anniConserv.equals(new BigDecimal(9999)))
                            || ((unlimited.equals("0") && anniConserv.equals(new BigDecimal(9999))))) {
                        error = true;
                    }
                }
                if (anniConserv == null && unlimited == null) {
                    error = true;
                }
                if (error) {
                    getMessageBox().addError("'Anni di conservazione' \u00E8 alternativo a 'Conservazione illimitata'");
                } else if (anniConserv == null) {
                    getMessageBox().addError("'Anni di conservazione' \u00E8 alternativo a 'Conservazione illimitata'");
                } else {
                    tempoConservazione = anniConserv.intValue();
                }
            }

            List<LivelloType> livelli = (List<LivelloType>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());
            if (!getMessageBox().hasError()) {
                Voce newVoce = new Voce(codiceVoceComposito, numeroOrdine, descrizioneVoce, dataInizioValidita,
                        dataFineValidita, attivoPerClassificazione, tempoConservazione, noteVoceTitolario);
                if (edit) {
                    newVoce.setOperation(Voce.Operation.MODIFICA);
                }

                checker.checkVoci(newVoce, livelli, livello.intValue() - 1, vociMap, dataInizioValiditaTitolario,
                        getMessageBox());
            }

            if (!getMessageBox().hasError()) {
                getForm().getVociInserimento().clear();
                getForm().getVociInserimento().getDt_istituz()
                        .setValue(getForm().getDatiTitolarioInserimento().getDt_istituz().getValue());
                getForm().getVociInserimento().getCd_composito_voce_padre().setDecodeMap(new DecodeMap());

                if (edit) {
                    int index = getForm().getVociList().getTable().getCurrentRowIndex();
                    getForm().getVociList().getTable().remove(index);
                    getForm().getVociList().getTable().add(row);
                } else {
                    getForm().getVociList().getTable().add(row);
                    getForm().getVociList().getTable().sort();

                    BaseTableInterface<?> table = livelliVociMap.get(livello);
                    if (table == null) {
                        table = new BaseTable();
                        livelliVociMap.put(livello, table);
                    }
                    livelliVociMap.get(livello).add(row);
                }

                getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name(),
                        numOrdinePrimoLivello);
                getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name(), vociMap);
                getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name(), livelliVociMap);

                cleanVoce();
            } else {
                if (livello.equals(BigDecimal.ONE)) {
                    numOrdinePrimoLivello.remove(numeroOrdine);
                }
                getForm().getVociInserimento().getCd_composito_voce_titol().setValue(codiceVoceComposito);
                getForm().getVociInserimento().getCd_composito_visualizzato().setValue(codiceVoceComposito);
                getForm().getVociInserimento().getCd_sep_livello().setValue(separatore);
            }
        }
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public void deleteVociList() throws EMFError {
        int index = getForm().getVociList().getTable().getCurrentRowIndex();
        BaseRow row = (BaseRow) getForm().getVociList().getTable().getCurrentRow();

        Map<String, Voce> vociMap = (Map<String, Voce>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
        List<LivelloType> livelli = (List<LivelloType>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());
        Set<Integer> numOrdinePrimoLivello = (Set<Integer>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name());
        Map<BigDecimal, BaseTableInterface<?>> livelliVociMap = (Map<BigDecimal, BaseTableInterface<?>>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());

        BigDecimal livello = row.getBigDecimal("ni_livello");
        String codToRemove = null;
        boolean deleteVoceTitol = false;
        if (livello.equals(BigDecimal.ONE)) {
            String codiceVoce = row.getString(getForm().getVociInserimento().getCd_voce_titol().getName());
            Voce voce = getVoce(vociMap, livelli, codiceVoce, livello.intValue() - 1);
            if (voce.getNumeroFigli() > 0) {
                getMessageBox().addError("Il nodo selezionato presenta dei figli, impossibile eliminarlo");
            } else {
                if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                    BigDecimal idVoceTitol = row.getBigDecimal("id_voce_titol");
                    if (idVoceTitol != null) {
                        getForm().getVociInserimento().getId_voce_titol().setValue(idVoceTitol.toString());

                        getRequest().setAttribute("customBox", true);
                        deleteVoceTitol = true;
                    } else {
                        vociMap.remove(codiceVoce);
                    }
                } else {
                    vociMap.remove(codiceVoce);
                }
                if (!deleteVoceTitol) {
                    numOrdinePrimoLivello.remove(voce.getNumeroOrdine());
                    codToRemove = codiceVoce;
                }
            }
        } else {
            String codiceVocePadre = row
                    .getString(getForm().getVociInserimento().getCd_composito_voce_padre().getName());
            String codiceVoce = row.getString(getForm().getVociInserimento().getCd_voce_titol().getName());

            Voce padre = getVoce(vociMap, livelli, codiceVocePadre, livello.intValue() - 2);
            Voce voce = padre.getFiglio(codiceVoce);
            if (voce.getNumeroFigli() > 0) {
                getMessageBox().addError("Il nodo selezionato presenta dei figli, impossibile eliminarlo");
            } else if (getForm().getTitolariList().getStatus().equals(Status.update)) {
                BigDecimal idVoceTitol = row.getBigDecimal("id_voce_titol");
                if (idVoceTitol != null) {
                    getForm().getVociInserimento().getId_voce_titol().setValue(idVoceTitol.toString());

                    getRequest().setAttribute("customBox", true);
                    deleteVoceTitol = true;
                } else {
                    padre.removeFiglio(codiceVoce);
                }
            } else {
                padre.removeFiglio(codiceVoce);
            }
            codToRemove = codiceVocePadre + livelli.get(livello.intValue() - 1).getCarattereSeparatoreLivello()
                    + codiceVoce;
        }

        if (!deleteVoceTitol && !getMessageBox().hasError()) {
            getForm().getVociList().getTable().remove(index);
            if (codToRemove != null) {
                for (int i = 0; i < livelliVociMap.get(livello).size(); i++) {
                    BaseRowInterface rowVoce = livelliVociMap.get(livello).getRow(i);
                    if (rowVoce.getString("cd_composito_voce_titol").equals(codToRemove)) {
                        livelliVociMap.get(livello).remove(i);
                        break;
                    }
                }
                getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name(), livelliVociMap);
            }

            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name(), numOrdinePrimoLivello);
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name(), vociMap);
        } else {
            getForm().getVociInserimento().getCd_composito_voce_titol().setValue(codToRemove);
        }
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    public Voce getVoce(Map<String, Voce> vociMap, List<LivelloType> livelli, String codiceVoceComposito,
            int indexLevel) {
        Voce root;
        Voce result;
        if (indexLevel == 0) {
            result = vociMap.get(codiceVoceComposito);
        } else {
            String[] codiceVoceSplittato = checker.getCodiceVoce(codiceVoceComposito, livelli, indexLevel);
            root = vociMap.get(codiceVoceSplittato[0]);
            result = getVoceRicorsiva(root, codiceVoceSplittato, 1);
        }
        return result;
    }

    public Voce getVoceRicorsiva(Voce root, String[] codiceVoceSplittato, int index) {
        String codiceVoceSingolo = codiceVoceSplittato[index];
        Voce son = root.getFiglio(codiceVoceSingolo);
        Voce result;
        if (index == (codiceVoceSplittato.length - 1)) {
            result = son;
        } else {
            result = getVoceRicorsiva(son, codiceVoceSplittato, index + 1);
        }
        return result;
    }

    @Override
    public JSONObject triggerVociInserimentoCd_composito_voce_padreOnTrigger() throws EMFError {
        getForm().getVociInserimento().post(getRequest());
        BigDecimal livello = getForm().getVociInserimento().getNi_livello().parse();
        String codiceVoceComposito = getForm().getVociInserimento().getCd_composito_voce_padre().parse();

        if (StringUtils.isNotBlank(codiceVoceComposito)) {

            Map<String, Voce> vociMap = (Map<String, Voce>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
            List<LivelloType> livelli = (List<LivelloType>) getSession()
                    .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());

            Voce voce = getVoce(vociMap, livelli, codiceVoceComposito, livello.intValue() - 2);
            Date dataFine = voce.getDataFineValidita();
            int numeroFigli = voce.getNumeroFigli();
            getForm().getVociInserimento().getNi_ord_voce_titol().setValue(String.valueOf(numeroFigli + 1));
            if (dataFine != null) {
                getForm().getVociInserimento().getDt_soppres().setValue(ActionUtils.getStringDate(dataFine));
            }
        }

        return getForm().getVociInserimento().asJSON();
    }

    @Override
    public void updateVociList() throws EMFError {
        DecVTreeTitolRowBean row = (DecVTreeTitolRowBean) getForm().getVociList().getTable().getCurrentRow();
        getForm().getVociInserimento().copyFromBean(row);
        getForm().getVociInserimento().getCd_composito_visualizzato()
                .setValue(StringEscapeUtils.unescapeJava(row.getCdCompositoVoceTitol()));
        getForm().getVociInserimento().getNi_livello().setViewMode();
        getForm().getVociInserimento().getCd_composito_voce_padre().setViewMode();
        getForm().getVociInserimento().getDt_istituz().setViewMode();
        getForm().getVociInserimento().getCd_voce_titol().setViewMode();
        getForm().getVociInserimento().getNi_ord_voce_titol().setViewMode();
    }

    @Override
    public void updateLivelliList() throws EMFError {
        BaseRowInterface row = getForm().getLivelliList().getTable().getCurrentRow();
        getForm().getLivelliInserimento().copyFromBean(row);

        BigDecimal numLivello = row.getBigDecimal(getForm().getLivelliList().getNi_livello().getName());
        int numLivelli = getForm().getDatiTitolarioInserimento().getNi_livelli().parse().intValue();
        HashMap<BigDecimal, BaseTableInterface<?>> livelliVociMap = (HashMap<BigDecimal, BaseTableInterface<?>>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());
        if (livelliVociMap != null) {
            for (int indexLevel = (numLivello.intValue()); indexLevel <= numLivelli; indexLevel++) {
                if (!livelliVociMap.get(new BigDecimal(indexLevel)).isEmpty()) {
                    getForm().getLivelliInserimento().getNi_livello().setViewMode();
                    getForm().getLivelliInserimento().getCd_sep_livello().setViewMode();
                    getForm().getLivelliInserimento().getTi_fmt_voce_titol().setViewMode();
                    break;
                }
            }
        }
    }

    @Override
    public void cleanLivello() throws EMFError {
        inserimentoWizardLivelliTitolarioOnEnter();
    }

    @Override
    public void cleanVoce() throws EMFError {
        inserimentoWizardVociTitolarioOnEnter();
    }

    public void chiudiVoce() throws EMFError {
        int index = getForm().getVociList().getTable().getCurrentRowIndex();
        BaseRowInterface row = (BaseRowInterface) getForm().getVociList().getTable().getCurrentRow();

        Map<String, Voce> vociMap = (Map<String, Voce>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name());
        List<LivelloType> livelli = (List<LivelloType>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_PARSING.name());
        Set<Integer> numOrdinePrimoLivello = (Set<Integer>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name());
        Map<BigDecimal, BaseTableInterface<?>> livelliVociMap = (Map<BigDecimal, BaseTableInterface<?>>) getSession()
                .getAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name());

        BigDecimal livello = row.getBigDecimal("ni_livello");
        String date = (String) getRequest().getParameter("Dt_soppres");
        String note = (String) getRequest().getParameter("Dl_note");
        DateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);

        if (StringUtils.isBlank(date)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            date = df.format(calendar.getTime());
        }

        Date dataFineValidita = null;
        try {
            dataFineValidita = df.parse(date);
        } catch (ParseException ex) {
            log.error("Errore nella data di fine validit\u00E0 inserita", ex);
            getMessageBox().addError("Errore nella data di fine validit\u00E0 inserita");
        }
        Voce voceDaEliminare = null;
        String codiceVoce = null;
        if (!getMessageBox().hasError()) {
            codiceVoce = getForm().getVociInserimento().getCd_composito_voce_titol().parse();
            voceDaEliminare = getVoce(vociMap, livelli, codiceVoce, livello.intValue() - 1);
            voceDaEliminare.setOperation(Voce.Operation.CHIUDI);
            voceDaEliminare.setDataFineValidita(dataFineValidita);
            voceDaEliminare.setNoteVoceTitolario(note);

            Date dataInizioValiditaTitolario = new Date(
                    getForm().getDatiTitolarioInserimento().getDt_istituz().parse().getTime());

            checker.checkVoci(voceDaEliminare, livelli, livello.intValue() - 1, vociMap, dataInizioValiditaTitolario,
                    getMessageBox());
        }

        if (!getMessageBox().hasError()) {

            if (livello.equals(BigDecimal.ONE)) {
                numOrdinePrimoLivello.remove(voceDaEliminare.getNumeroOrdine());
            }

            getForm().getVociList().getTable().remove(index);
            if (codiceVoce != null) {
                for (int i = 0; i < livelliVociMap.get(livello).size(); i++) {
                    BaseRowInterface rowVoce = livelliVociMap.get(livello).getRow(i);
                    if (rowVoce.getString("cd_composito_voce_titol").equals(codiceVoce)) {
                        livelliVociMap.get(livello).remove(i);
                        break;
                    }
                }
                getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.LIVELLI_VOCI.name(), livelliVociMap);
            }

            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.NUM_ORDINE_VOCI.name(), numOrdinePrimoLivello);
            getSession().setAttribute(WebConstants.PARAMETER_TITOLARIO.VOCI_MAP.name(), vociMap);
            cleanVoce();
        }
        forwardToPublisher(getDefaultInserimentoWizardPublisher());
    }

    @Override
    public void updateTitolariList() throws EMFError {
        DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
        if (titolariEjb.isTitolarioChiuso(row.getIdTitol())) {
            getMessageBox().addError("Il titolario \u00E8 gi\u00E0 stato chiuso, non \u00E8 possibile modificarlo");
        } else {
            getRequest().setAttribute("caricaTitolarioBox", true);
            getForm().getTitolariList().setStatus(Status.update);
        }
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    public void annullaModifica() {
        getForm().getTitolariList().setStatus(Status.view);
        forwardToPublisher(Application.Publisher.TITOLARIO_DETAIL);
    }

    @Override
    public JSONObject triggerVociInserimentoConserv_unlimitedOnTrigger() throws EMFError {
        getForm().getVociInserimento().post(getRequest());
        String unlimited = getForm().getVociInserimento().getConserv_unlimited().parse();
        BigDecimal anniConserv = getForm().getVociInserimento().getNi_anni_conserv().parse();
        if (unlimited != null && unlimited.equals("1")) {
            getForm().getVociInserimento().getNi_anni_conserv().setValue("9999");
        } else if (anniConserv != null && anniConserv.equals(new BigDecimal(9999))) {
            getForm().getVociInserimento().getNi_anni_conserv().setValue(null);
        }
        String cdComposito = getForm().getVociInserimento().getCd_composito_voce_titol().parse();
        getForm().getVociInserimento().getCd_composito_visualizzato().setValue(cdComposito);
        return getForm().getVociInserimento().asJSON();
    }

    @Override
    public void esportaTitolario() throws EMFError {
        Date day = (Date) getSession().getAttribute(WebConstants.PARAMETER_TITOLARIO.DATE_TITOLARIO.name());
        DecTitolRowBean row = (DecTitolRowBean) getForm().getTitolariList().getTable().getCurrentRow();
        try {
            CreaTitolario tree = titolariEjb.generateXmlObject(row, day);
            File tmpFile = File.createTempFile(row.getNmTitol(), ".xml",
                    new File(System.getProperty("java.io.tmpdir")));

            // Devo eseguire i controlli per vedere se le voci sono valide dopo eventuale chiusura
            DecVTreeTitolTableBean tabellaVociTitolario = titolariEjb.getDecVociTreeTableBean(row.getIdTitol(),
                    new Date(), false);
            if (tabellaVociTitolario.isEmpty()) {
                getMessageBox().addError(
                        "Attenzione: non è possibile esportare il titolario in quanto tutte le voci non sono valide");
            }

            Date dataOdierna = new Date();
            int countVociNonValide = 0;
            for (DecVTreeTitolRowBean rb : tabellaVociTitolario) {
                if (!(rb.getDtIniVal().compareTo(dataOdierna) <= 0 && rb.getDtFinVal().compareTo(dataOdierna) >= 0
                        && rb.getDtIstituz().compareTo(dataOdierna) <= 0
                        && rb.getDtSoppres().compareTo(dataOdierna) >= 0)) {
                    countVociNonValide++;
                }
            }

            if (countVociNonValide > 0 && countVociNonValide == tabellaVociTitolario.size()) {
                getMessageBox().addError(
                        "Attenzione: non è possibile esportare il titolario in quanto tutte le voci non sono valide");
            }

            if (!getMessageBox().hasError()) {
                JAXBContext titolarioCtx = xmlContextCache.getCreaTitolarioCtx();
                Marshaller jaxbMarshaller = titolarioCtx.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.setSchema(XmlUtils
                        .getSchemaValidation(this.getClass().getClassLoader().getResourceAsStream(CREA_XSD_FILENAME)));
                jaxbMarshaller.marshal(tree, tmpFile);

                getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                        Boolean.toString(true));
                getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(), "application/xml");
            }
        } catch (SAXException | JAXBException | IOException ex) {
            log.error(ex.getMessage(), ex);
            getMessageBox().addError("Eccezione nella generazione del xml: " + ExceptionUtils.getRootCauseMessage(ex));
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    public void download() throws EMFError {
        String filename = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        Boolean deleteFile = Boolean.parseBoolean(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
                 * download
                 */
                OutputStream outUD = getServletOutputStream();
                getResponse().setContentType(StringUtils.isBlank(contentType) ? "application/zip" : contentType);
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                FileInputStream inputStream = null;
                try {
                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
                    inputStream = new FileInputStream(fileToDownload);
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
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
                    inputStream = null;
                    outUD = null;
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile) {
                    fileToDownload.delete();
                }
            } else {
                getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
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

}
