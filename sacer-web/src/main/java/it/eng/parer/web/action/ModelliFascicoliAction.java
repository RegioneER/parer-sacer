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

import static it.eng.spagoCore.ConfigProperties.StandardProperty.LOAD_XSD_APP_MAX_FILE_SIZE;
import static it.eng.spagoCore.ConfigProperties.StandardProperty.LOAD_XSD_APP_MAX_REQUEST_SIZE;
import static it.eng.spagoCore.ConfigProperties.StandardProperty.LOAD_XSD_APP_UPLOAD_DIR;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.ejb.ModelliFascicoliEjb;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.common.helper.ParamApplicHelper;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ModelliFascicoliAbstractAction;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.spagoCore.ConfigSingleton;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;

/**
 *
 * @author DiLorenzo_F
 */
public class ModelliFascicoliAction extends ModelliFascicoliAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(ModelliFascicoliAction.class);

    // Costanti per literal duplicati (fix SonarQube)
    private static final String TI_MODELLO_XSD = "ti_modello_xsd";
    private static final String TI_RIFERIMENTO = "ti_riferimento";
    private static final String TIPO_RIFERIMENTO_IMPORT = "IMPORT";
    private static final String TIPO_RIFERIMENTO_INCLUDE = "INCLUDE";

    @EJB(mappedName = "java:app/Parer-ejb/ModelliFascicoliEjb")
    private ModelliFascicoliEjb modelliFascicoliEjb;
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
    @EJB(mappedName = "java:app/Parer-ejb/XsdRepositoryHelper")
    private it.eng.parer.xml.xsd.helper.XsdRepositoryHelper xsdRepositoryHelper;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void process() throws EMFError {

        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());

        if (isMultipart) {

            if (getLastPublisher()
                    .equals(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL)) {

                // Solo per operazioni di salvataggio/annullamento del form principale
                try {
                    readUploadForm();
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }

        }
    }

    /**
     * Metodo che legge la form e passa i dati alla validazione
     *
     * @throws ParerUserError errore generico
     * @throws EMFError       errore generico
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
            factory.setRepository(new File(
                    ConfigSingleton.getInstance().getStringValue(LOAD_XSD_APP_UPLOAD_DIR.name())));
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum size before a FileUploadException will be thrown
            upload.setSizeMax(ConfigSingleton.getInstance()
                    .getLongValue(LOAD_XSD_APP_MAX_REQUEST_SIZE.name()));
            upload.setFileSizeMax(
                    ConfigSingleton.getInstance().getLongValue(LOAD_XSD_APP_MAX_FILE_SIZE.name()));
            List<FileItem> items = upload.parseRequest(getRequest());
            Iterator<FileItem> iter = items.iterator();

            DiskFileItem tmpFileItem = null;
            DiskFileItem tmpOperation = null;

            while (iter.hasNext()) {
                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    tmpFileItem = (DiskFileItem) item;
                } else if (item.getFieldName().equals(getForm().getModelliXsdTipiFascicoloDetail()
                        .getTi_modello_xsd().getName())) {
                    getForm().getModelliXsdTipiFascicoloDetail().getTi_modello_xsd()
                            .setValue(item.getString());
                } else if (item.getFieldName().equals(
                        getForm().getModelliXsdTipiFascicoloDetail().getFl_default().getName())) {
                    getForm().getModelliXsdTipiFascicoloDetail().getFl_default()
                            .setValue(item.getString());
                } else if (item.getFieldName().equals(
                        getForm().getModelliXsdTipiFascicoloDetail().getCd_xsd().getName())) {
                    getForm().getModelliXsdTipiFascicoloDetail().getCd_xsd()
                            .setValue(item.getString());
                } else if (item.getFieldName().equals(
                        getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd().getName())) {
                    getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd()
                            .setValue(item.getString());
                } else if (item.getFieldName().equals(
                        getForm().getModelliXsdTipiFascicoloDetail().getDt_istituz().getName())) {
                    getForm().getModelliXsdTipiFascicoloDetail().getDt_istituz()
                            .setValue(item.getString());
                } else if (item.getFieldName().equals(
                        getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres().getName())) {
                    getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres()
                            .setValue(item.getString());
                } else if (!item.getFieldName().equals("table")) {
                    String fieldName = item.getFieldName();
                    if (fieldName.contains(ListAction.NE_DETTAGLIO_CANCEL)
                            || fieldName.contains(ListAction.NE_DETTAGLIO_SAVE)) {
                        tmpOperation = (DiskFileItem) item;
                    }
                }
            }

            if (tmpOperation != null) {
                if (tmpOperation.getFieldName().contains(ListAction.NE_DETTAGLIO_CANCEL)) {
                    goBack();
                } else if (tmpOperation.getFieldName().contains(ListAction.NE_DETTAGLIO_SAVE)) {
                    // Verrà letto in saveModelloXsdTipiFasc() con .parse()
                    // controllo ambiente
                    BigDecimal idAmbiente = getForm().getModelliXsdTipiFascicoloDetail()
                            .getId_ambiente().parse();
                    if (idAmbiente == null) {
                        getMessageBox().addError("Ambiente non inserito");
                    }

                    String tiModelloXsd = getForm().getModelliXsdTipiFascicoloDetail()
                            .getTi_modello_xsd().parse();

                    // controllo flag standard
                    String flDefault = getForm().getModelliXsdTipiFascicoloDetail().getFl_default()
                            .parse();
                    if (StringUtils.isBlank(flDefault)) {
                        getMessageBox().addError("Flag standard non inserito");
                    }

                    // controllo esistenza codice versione
                    String cdVersione = getForm().getModelliXsdTipiFascicoloDetail().getCd_xsd()
                            .parse();
                    if (StringUtils.isBlank(cdVersione)) {
                        getMessageBox().addError("Codice versione non inserito");
                    }

                    String dsVersione = getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd()
                            .parse();
                    if (StringUtils.isBlank(dsVersione)) {
                        getMessageBox().addError("Descrizione non inserita");
                    }

                    SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
                    Date dtIstituz = getForm().getModelliXsdTipiFascicoloDetail().getDt_istituz()
                            .parse();
                    if (dtIstituz == null) {
                        dtIstituz = Calendar.getInstance().getTime();
                        getForm().getModelliXsdTipiFascicoloDetail().getDt_istituz()
                                .setValue(df.format(dtIstituz));
                    }

                    Date dtSoppres = getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres()
                            .parse();
                    if (dtSoppres == null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(2444, 11, 31, 0, 0, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres()
                                .setValue(df.format(calendar.getTime()));
                    }

                    if (getMessageBox().isEmpty()) {
                        String clob = null;
                        if (StringUtils.isNotBlank(tmpFileItem.getName())) {
                            FileBinario fileBin = getFileBinario(tmpFileItem);
                            // conversione in stringa
                            clob = new String(fileBin.getDati());
                        }

                        if (StringUtils.isNotBlank(clob)) {
                            // XSD: validazione schema
                            // Se l'XSD contiene import/include, la validazione nativa fallirebbe
                            // per mancanza di resolver.
                            // In quel caso la saltiamo qui (verrà fatta dall'EJB con
                            // DbXsdResourceResolver).
                            // Se invece è un XSD autonomo, eseguiamo la validazione sintattica
                            // standard.
                            if (!xsdContieneDipendenze(clob)) {
                                // 1. Lookup a factory for the W3C XML Schema language
                                SchemaFactory schemaFactory = SchemaFactory
                                        .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
                                schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
                                try {
                                    // 2. Compile the schema.
                                    schemaFactory
                                            .newSchema(new StreamSource(new StringReader(clob)));
                                } catch (SAXException e) {
                                    String saxMsg = e.getMessage() != null ? e.getMessage() : "";
                                    throw new FileUploadException(
                                            "Il file XSD non è sintatticamente valido: " + saxMsg,
                                            e);
                                }
                            }
                        }

                        // NOTA: In INSERT mode, la lista dipendenze è già in sessione (modificata
                        // dall'utente tramite il bottone "Aggiungi"). Viene letta direttamente da
                        // saveModelloXsdTipiFasc() senza bisogno di post esplicito.

                        saveModelloXsdTipiFasc(tiModelloXsd, flDefault, cdVersione, dsVersione,
                                dtIstituz, dtSoppres, clob);
                    }
                }
            } else {
                getMessageBox().addError("Operazione non riconosciuta");
                log.error("Operazione non riconosciuta");
                forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
            }
        } catch (FileUploadException ex) {
            log.error("Eccezione nell'upload dei file", ex);
            String uploadMsg = ex.getLocalizedMessage();
            if (uploadMsg != null && uploadMsg.startsWith("Il file XSD")) {
                getMessageBox().addError(uploadMsg);
            } else {
                getMessageBox().addError("Eccezione nell'upload dei file: " + uploadMsg);
            }
            forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
        } catch (Exception ex) {
            if (getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd().parse().length() > 254) {
                log.error("Errore nella verifica dei dati del fascicolo.", ex);
                getMessageBox().addError(MessaggiWSBundle.getString(MessaggiWSBundle.FASC_008_001));
            } else {
                log.error("Eccezione generica nell'importazione del modello xsd di tipo fascicolo",
                        ex);
                getMessageBox().addError(
                        "Si \u00E8 verificata un'eccezione nell'importazione del modello xsd di tipo fascicolo: "
                                + ExceptionUtils.getRootCauseMessage(ex));
            }
            forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
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
        try {
            if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                    || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                    || getNavigationEvent().equals(ListAction.NE_NEXT)
                    || getNavigationEvent().equals(ListAction.NE_PREV)) {
                if (getTableName().equals(getForm().getModelliXsdTipiFascicoloList().getName())) {
                    initModelloXsdTipiFascicoloDetail();
                    BigDecimal idModelloXsdFascicolo = ((DecModelloXsdFascicoloTableBean) getForm()
                            .getModelliXsdTipiFascicoloList().getTable()).getCurrentRow()
                            .getIdModelloXsdFascicolo();
                    loadDettaglioModelloXsdTipoFascicolo(idModelloXsdFascicolo);
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void loadDettaglioModelloXsdTipoFascicolo(BigDecimal idModelloXsdFascicolo)
            throws EMFError, ParerUserError {
        try {
            // Caricamento dettaglio modello xsd del tipo fascicolo
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = modelliFascicoliEjb
                    .getDecModelloXsdFascicoloRowBean(idModelloXsdFascicolo);

            XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
            String xmlFormatted = formatter
                    .prettyPrintWithDOM3LS(modelloXsdFascicoloRowBean.getBlXsd());
            modelloXsdFascicoloRowBean.setBlXsd(xmlFormatted);
            // Reinizializza le DecodeMap prima di copyFromBean
            initModelloXsdTipiFascicoloDetail();
            // copyFromBean
            if (it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.RICHIAMABILE
                    .name().equals(modelloXsdFascicoloRowBean.getTiModelloXsd())) {
                BaseTable tiModelloTable = new BaseTable();
                for (CostantiDB.TiModelloXsd val : CostantiDB.TiModelloXsd.values()) {
                    BaseRow row = new BaseRow();
                    row.setString(TI_MODELLO_XSD, val.name());
                    tiModelloTable.add(row);
                }
                BaseRow richiamabileRow = new BaseRow();
                richiamabileRow.setString(TI_MODELLO_XSD,
                        it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.RICHIAMABILE
                                .name());
                tiModelloTable.add(richiamabileRow);
                DecodeMap mappaConRichiamabile = new DecodeMap();
                mappaConRichiamabile.populatedMap(tiModelloTable, TI_MODELLO_XSD, TI_MODELLO_XSD);
                getForm().getModelliXsdTipiFascicoloDetail().getTi_modello_xsd()
                        .setDecodeMap(mappaConRichiamabile);
            }
            getForm().getModelliXsdTipiFascicoloDetail().copyFromBean(modelloXsdFascicoloRowBean);
            getForm().getModelliXsdTipiFascicoloDetail().setViewMode();
            getForm().getModelliXsdTipiFascicoloDetail().setStatus(Status.view);
            getForm().getModelliXsdTipiFascicoloList().setStatus(Status.view);

            // Carica lista dipendenze XSD in view mode (sempre non editabile)
            loadDipendenzaXsd(idModelloXsdFascicolo);

            // Avviso visibile sempre in VIEW se l'XSD è modulare ma mancano dipendenze
            String xsdContent = modelloXsdFascicoloRowBean.getBlXsd();
            int totaleRiferimenti = contaRiferimentiXsd(xsdContent);
            if (totaleRiferimenti > 0) {
                it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean depsCaricate = (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean) getForm()
                        .getDipendenzaXsdList().getTable();
                int depsConfigurate = depsCaricate != null ? depsCaricate.size() : 0;
                if (depsConfigurate < totaleRiferimenti) {
                    getMessageBox().addWarning("L'XSD dichiara " + totaleRiferimenti
                            + " riferimenti (import/include), " + "configurate " + depsConfigurate
                            + " su " + totaleRiferimenti
                            + ". Il modello non è ancora utilizzabile per la validazione.");
                }
            }

            // La visibilità dei bottoni dipendenze sarà gestita automaticamente da
            // postLoad()

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    /**
     * Inizializza le combo del form di dettaglio modello XSD tipo fascicolo
     */
    private void initModelloXsdTipiFascicoloDetail() {
        BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();
        getForm().getModelliXsdTipiFascicoloDetail().getId_ambiente().setDecodeMap(
                DecodeMap.Factory.newInstance(ambienteTableBean, "id_ambiente", "nm_ambiente"));

        // Imposto la combo "Tipo modello xsd"
        getForm().getModelliXsdTipiFascicoloDetail().getTi_modello_xsd()
                .setDecodeMap(ComboGetter.getMappaTiModelloXsd());

        // Imposto la combo "Standard"
        getForm().getModelliXsdTipiFascicoloDetail().getFl_default()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    @Override
    public void undoDettaglio() throws EMFError {
        try {
            if (getLastPublisher().equals(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL)
                    && getForm().getModelliXsdTipiFascicoloList().getStatus()
                            .equals(Status.update)) {
                DecModelloXsdFascicoloRowBean currentRow = (DecModelloXsdFascicoloRowBean) getForm()
                        .getModelliXsdTipiFascicoloList().getTable().getCurrentRow();
                BigDecimal idModelloXsdFascicolo = currentRow.getIdModelloXsdFascicolo();
                if (idModelloXsdFascicolo != null) {
                    loadDettaglioModelloXsdTipoFascicolo(idModelloXsdFascicolo);
                }
                getForm().getModelliXsdTipiFascicoloDetail().setViewMode();
                getForm().getModelliXsdTipiFascicoloDetail().setStatus(Status.view);
                getForm().getModelliXsdTipiFascicoloList().setStatus(Status.view);

                forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);

            } else {
                goBack();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getDipendenzaXsdList().getName())) {
            // Richiamato quando l'utente clicca "Inserisci" dalla DipendenzaXsdList
            // Naviga alla pagina di ricerca/selezione modelli
            BigDecimal idModelloXsdFascicolo = ((DecModelloXsdFascicoloTableBean) getForm()
                    .getModelliXsdTipiFascicoloList().getTable()).getCurrentRow()
                    .getIdModelloXsdFascicolo();
            if (modelliFascicoliEjb.isModelloXsdInUseInTipologieFascicolo(idModelloXsdFascicolo)) {
                getMessageBox().addError(
                        "Il modello xsd di tipo fascicolo \u00E8 gi\u00E0 stato utilizzato per gestire delle tipologie di fascicolo: la modifica delle dipendenze non \u00E8 consentita");
                forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
                return;
            }

            initDipendenzaXsdDetail();
            forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
        } else if (getTableName().equals(getForm().getModelliXsdTipiFascicoloList().getName())) {
            getForm().getModelliXsdTipiFascicoloDetail().reset();
            getForm().getModelliXsdTipiFascicoloDetail().setEditMode();
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setHidden(true);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setHidden(true);

            // Le dipendenze verranno gestite in view
            getForm().getDipendenzaXsdList()
                    .setTable(new it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean());
            getForm().getDipendenzaXsdList().setStatus(Status.view);
            getForm().getDipendenzaXsdList().setHidden(true);

            // Setta ambiente in base ai valori della pagina di ricerca modelli xsd
            initComboCreaModelloXsdTipiFascicoloFromRicercaModelliXsd();

            populateDefaultValues();

            getForm().getModelliXsdTipiFascicoloList().setStatus(Status.insert);
            getForm().getModelliXsdTipiFascicoloDetail().setStatus(Status.insert);

            forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
        }
    }

    /**
     * Inizializza le combo del DETTAGLIO DI UN MODELLO XSD di tipo fascicoli, ricavando i valori
     * dai filtri di ricerca modelli xsd. Se impostato, restituisce il valore dell'id del filtro
     * relativo all'ambiente
     *
     * @return l'id dell'ambiente impostato come filtro di ricerca
     */
    private BigDecimal initComboCreaModelloXsdTipiFascicoloFromRicercaModelliXsd() {
        // Inizializzo la combo ambiente in base ai valori impostati nella pagina di
        // ricerca modelli
        // xsd
        ComboBox ambienteCombo = getForm().getFiltriModelliXsdTipiFascicolo().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        getForm().getModelliXsdTipiFascicoloDetail().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getModelliXsdTipiFascicoloDetail().getId_ambiente()
                .setValue(getForm().getFiltriModelliXsdTipiFascicolo().getId_ambiente().getValue());

        // Inizializzo le combo "Tipo modello xsd" e "Standard"
        getForm().getModelliXsdTipiFascicoloDetail().getTi_modello_xsd()
                .setDecodeMap(ComboGetter.getMappaTiModelloXsd());
        getForm().getModelliXsdTipiFascicoloDetail().getFl_default()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        return StringUtils.isNotBlank(
                getForm().getFiltriModelliXsdTipiFascicolo().getId_ambiente().getValue())
                        ? new BigDecimal(getForm().getFiltriModelliXsdTipiFascicolo()
                                .getId_ambiente().getValue())
                        : null;
    }

    /**
     * Metodo che inserisce i valori di default dei campi nella form di creazione modelli XSD tipi
     * Fascicolo
     *
     * @throws EMFError errore generico
     */
    private void populateDefaultValues() throws EMFError {
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31);
        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getForm().getModelliXsdTipiFascicoloDetail().getDt_istituz()
                .setValue(df.format(Calendar.getInstance().getTime()));
        getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres()
                .setValue(df.format(cal.getTime()));
    }

    private void saveModelloXsdTipiFasc(String tiModelloXsd, String flStandard, String cdVersione,
            String dsVersione, Date dtIstituz, Date dtSoppres, String file) throws EMFError {
        getMessageBox().clear();
        BigDecimal idModelloXsdFascicolo = getForm().getModelliXsdTipiFascicoloDetail()
                .getId_modello_xsd_fascicolo().parse();
        if (getForm().getModelliXsdTipiFascicoloList().getStatus().equals(Status.update)) {
            if (StringUtils.isNotBlank(file) && modelliFascicoliEjb
                    .isModelloXsdInUseInTipologieFascicolo(idModelloXsdFascicolo)) {
                getMessageBox().addError(
                        "Il modello xsd di tipo fascicolo \u00E8 gi\u00E0 stato utilizzato per gestire delle tipologie di fascicolo: la modifica del File Xsd non \u00E8 consentita");
            }
        }
        String flDefault = getForm().getModelliXsdTipiFascicoloDetail().getFl_default().parse();
        if (StringUtils.isBlank(flDefault)) {
            getMessageBox().addError("Flag standard non inserito");
        }

        // Leggi flag richiamabile
        // Carica dipendenze: SEMPRE dalla sessione (modificate dall'utente tramite
        // Aggiungi/Rimuovi)
        it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean deps = null;
        if (getForm().getDipendenzaXsdList().getTable() != null) {
            deps = (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean) getForm()
                    .getDipendenzaXsdList().getTable();
        }

        // Filtra righe vuote (senza target selezionato)
        if (deps != null && !deps.isEmpty()) {
            it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean filteredDeps = new it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean();
            for (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean row : deps) {
                if (row.getIdModelloXsdFascicoloTarget() != null) {
                    filteredDeps.add(row);
                }
            }
            deps = filteredDeps;
        }

        // Un modello è richiamabile se ha tipo modello = RICHIAMABILE
        boolean isRichiamabile = it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.RICHIAMABILE
                .name().equals(tiModelloXsd);

        // Validazioni su ogni dipendenza (se presenti)
        if (deps != null && !deps.isEmpty()) {
            for (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean row : deps) {
                // Tipo riferimento obbligatorio
                if (StringUtils.isBlank(row.getTiRiferimento())) {
                    getMessageBox()
                            .addError("Tipo riferimento obbligatorio per tutte le dipendenze");
                    forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
                    return;
                }

                // Per IMPORT, namespace_uri è obbligatorio
                if (TIPO_RIFERIMENTO_IMPORT.equals(row.getTiRiferimento())
                        && StringUtils.isBlank(row.getNamespaceUri())) {
                    getMessageBox()
                            .addError("Namespace URI obbligatorio per dipendenze di tipo IMPORT");
                    forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
                    return;
                }
            }
        }

        boolean contieneDipendenzeXsd = StringUtils.isNotBlank(file) && xsdContieneDipendenze(file);

        // Un modello RICHIAMABILE non può contenere import o include
        if (isRichiamabile && contieneDipendenzeXsd) {
            getMessageBox().addError("Un modello RICHIAMABILE non può contenere import o include");
        }

        // Validazione modello standard duplicate (solo per non richiamabili)
        if (!isRichiamabile && StringUtils.isNotBlank(tiModelloXsd)
                && CostantiDB.Flag.TRUE.equals(flDefault)
                && modelliFascicoliEjb.existAnotherDecModelloXsdStd(
                        getForm().getModelliXsdTipiFascicoloDetail().getId_ambiente().parse(),
                        idModelloXsdFascicolo, tiModelloXsd,
                        CostantiDB.TiUsoModelloXsd.VERS.name())) {
            getMessageBox().addError("Modello xsd standard già presente nell'ambiente");
        }

        // Validazione sintassi XSD per tipo (solo se non richiamabile e XSD senza
        // import/include)
        try {
            if (StringUtils.isNotBlank(file) && !isRichiamabile && !contieneDipendenzeXsd
                    && !this.validazioneXsd(tiModelloXsd, file)) {
                getMessageBox().addError(
                        "Elementi contenuti nel file XSD non conformi alla tipologia richiesta</br>");
            }

            // Per modelli richiamabili e per i modelli modulari con import/include: la
            // validazione XSD con DbXsdResourceResolver
            // avviene in saveDipendenze (EJB) quando l'utente configura le dipendenze in
            // VIEW.
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = new DecModelloXsdFascicoloRowBean();

            // Salva il modello
            if (!getMessageBox().hasError()) {
                checkModelloXsdTipoFascicolo();
                BigDecimal idAmbiente = getForm().getModelliXsdTipiFascicoloDetail()
                        .getId_ambiente().parse();

                modelloXsdFascicoloRowBean.setCdXsd(cdVersione);
                if (StringUtils.isNotBlank(file)) {
                    modelloXsdFascicoloRowBean.setBlXsd(file);
                }
                modelloXsdFascicoloRowBean.setIdAmbiente(idAmbiente);
                modelloXsdFascicoloRowBean.setDtIstituz(new Timestamp(dtIstituz.getTime()));
                modelloXsdFascicoloRowBean.setDtSoppres(new Timestamp(dtSoppres.getTime()));
                modelloXsdFascicoloRowBean.setDsXsd(dsVersione);
                modelloXsdFascicoloRowBean
                        .setTiUsoModelloXsd(CostantiDB.TiUsoModelloXsd.VERS.name());

                // Tipo sempre dall'utente
                modelloXsdFascicoloRowBean.setTiModelloXsd(tiModelloXsd);
                modelloXsdFascicoloRowBean.setFlDefault(flDefault);

                /*
                 * Codice aggiuntivo per il logging
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                if (getForm().getModelliXsdTipiFascicoloList().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());

                    Long idModelloXsdFascicoloNew = modelliFascicoliEjb
                            .saveModelloXsdFascicolo(param, modelloXsdFascicoloRowBean);
                    if (idModelloXsdFascicoloNew != null) {
                        getForm().getModelliXsdTipiFascicoloDetail().getId_modello_xsd_fascicolo()
                                .setValue(idModelloXsdFascicoloNew.toString());
                    }

                    getForm().getModelliXsdTipiFascicoloDetail()
                            .copyToBean(modelloXsdFascicoloRowBean);
                    getForm().getModelliXsdTipiFascicoloList().getTable().last();
                    getForm().getModelliXsdTipiFascicoloList().getTable()
                            .add(modelloXsdFascicoloRowBean);

                    if (contieneDipendenzeXsd) {
                        getMessageBox().addWarning(
                                "Modello salvato. L'XSD contiene import/include: configurare le dipendenze.");
                        getMessageBox().setViewMode(ViewMode.plain);
                        loadDettaglioModelloXsdTipoFascicolo(
                                new BigDecimal(idModelloXsdFascicoloNew));
                        postLoad();
                        forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
                        return;
                    } else {
                        getMessageBox().addInfo("Modello XSD salvato con successo");
                        getMessageBox().setViewMode(ViewMode.plain);
                    }

                } else if (getForm().getModelliXsdTipiFascicoloList().getStatus()
                        .equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idModelloXsdFascicoloUpd = getForm()
                            .getModelliXsdTipiFascicoloDetail().getId_modello_xsd_fascicolo()
                            .parse();

                    boolean nuovoFileXsd = StringUtils.isNotBlank(file);

                    Date oggi = new Date();
                    boolean disattivazione = dtSoppres != null && dtSoppres.before(oggi);

                    // Blocco: un modello RICHIAMABILE non può essere disattivato
                    // se è ancora referenziato da un root attivo in DEC_MODELLO_XSD_FASC_RIF
                    if (disattivazione && isRichiamabile && modelliFascicoliEjb
                            .isModelloReferenziato(idModelloXsdFascicoloUpd)) {
                        getMessageBox().addError(
                                "Il modello RICHIAMABILE è referenziato dai altri modelli attivi e non può essere disattivato");
                        forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
                        return;
                    }

                    // Warning: le dipendenze verranno disattivate insieme al modello root
                    int numeroDipendenze = modelliFascicoliEjb
                            .countDipendenze(idModelloXsdFascicoloUpd);
                    if (!isRichiamabile && numeroDipendenze > 0 && disattivazione) {
                        getMessageBox()
                                .addWarning("Attenzione: il modello verrà disattivato insieme a "
                                        + numeroDipendenze + " dipendenza/e.");
                    }

                    if (nuovoFileXsd) {
                        // Nuovo XSD caricato: cancella tutte le dipendenze obsolete
                        modelliFascicoliEjb.deleteAllDipendenze(idModelloXsdFascicoloUpd);

                        if (contieneDipendenzeXsd) {
                            getMessageBox().addWarning(
                                    "XSD aggiornato: le dipendenze precedenti sono state eliminate. "
                                            + "Se il nuovo XSD contiene import/include, configurare le nuove dipendenze.");
                        } else {
                            getMessageBox().addInfo(
                                    "Modello XSD aggiornato con successo. Se il nuovo XSD contiene import/include, configurare le nuove dipendenze.");
                        }
                    }
                    // Aggiorna campi del modello (sempre)
                    modelliFascicoliEjb.updateModelloXsdFascicolo(param, idModelloXsdFascicoloUpd,
                            modelloXsdFascicoloRowBean);
                    getMessageBox().setViewMode(ViewMode.plain);
                }

                reloadAfterGoBack(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
    }

    private boolean validazioneXsd(String tiModelloXsd, String stringaFile) throws ParerUserError {

        ByteArrayInputStream bais = null;

        if (!stringaFile.isEmpty()) {
            bais = new ByteArrayInputStream(
                    stringaFile.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        boolean isValidType = false;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
            // almost all XML entity attacks are prevented
            final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbf.setFeature(FEATURE, true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // ... and these as well, per Timothy Morgan's 2014 paper:
            // "XML Schema, DTD, and Entity Attacks" (see reference below)
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            // As stated in the documentation, "Feature for Secure Processing (FSP)" is the
            // central
            // mechanism that will
            // help you safeguard XML processing. It instructs XML processors, such as
            // parsers,
            // validators,
            // and transformers, to try and process XML securely, and the FSP can be used as
            // an
            // alternative to
            // dbf.setExpandEntityReferences(false); to allow some safe level of Entity
            // Expansion
            // Exists from JDK6.
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            // ... and, per Timothy Morgan:
            // "If for some reason support for inline DOCTYPEs are a requirement, then
            // ensure the entity settings are disabled (as shown above) and beware that SSRF
            // attacks
            // (http://cwe.mitre.org/data/definitions/918.html) and denial
            // of service attacks (such as billion laughs or decompression bombs via "jar:")
            // are a risk."
            dbf.setNamespaceAware(true);
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            Document doc;

            doc = db.parse(bais);

            String tagName = (CostantiDB.TiModelloXsd.AIP_UNISYNCRO.name().equals(tiModelloXsd))
                    ? "schema"
                    : "element";
            NodeList nl = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, tagName);

            for (int it = 0; it < nl.getLength(); it++) {
                Node n = nl.item(it);
                NamedNodeMap map = n.getAttributes();

                Node item;
                if (!CostantiDB.TiModelloXsd.AIP_UNISYNCRO.name().equals(tiModelloXsd)) {
                    item = map.getNamedItem("name");
                    if (CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO.name()
                            .equals(tiModelloXsd)) {
                        if (item != null
                                && item.getNodeValue().equals("ProfiloGeneraleFascicolo")) {
                            isValidType = true;
                        }
                    }
                    if (CostantiDB.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO.name()
                            .equals(tiModelloXsd)) {
                        if (item != null
                                && item.getNodeValue().equals("ProfiloArchivisticoFascicolo")) {
                            isValidType = true;
                        }
                    }
                    if (CostantiDB.TiModelloXsd.PROFILO_SPECIFICO_FASCICOLO.name()
                            .equals(tiModelloXsd)) {
                        if (item != null && item.getNodeValue().equals("ProfiloSpecifico")) {
                            isValidType = true;
                        }
                    }
                    if (CostantiDB.TiModelloXsd.FASCICOLO.name().equals(tiModelloXsd)) {
                        if (item != null && item.getNodeValue().equals("Fascicolo")) {
                            isValidType = true;
                        }
                    }
                    if (CostantiDB.TiModelloXsd.AIP_SELF_DESCRIPTION_MORE_INFO.name()
                            .equals(tiModelloXsd)) {
                        if (item != null
                                && item.getNodeValue().equals("MetadatiIntegratiSelfDescription")) {
                            isValidType = true;
                        }
                    }
                    if (CostantiDB.TiModelloXsd.PROFILO_NORMATIVO_FASCICOLO.name()
                            .equals(tiModelloXsd)) {
                        if (item != null && item.getNodeValue()
                                .equals("AggregazioneDocumentaliInformatiche")) {
                            isValidType = true;
                        }
                    }
                } else {
                    item = map.getNamedItem("targetNamespace");
                    if (item != null
                            && item.getNodeValue().equals("http://www.uni.com/U3011/sincro-v2/")) {
                        isValidType = true;
                    }
                }
            }
        } catch (SAXException e) {
            log.error("Operazione non effettuata: file non ben formato ", e);
            throw new ParerUserError("Operazione non effettuata: file non ben formato ");
        } catch (IOException e) {
            log.error("Operazione non effettuata: dimensione file troppo elevate", e);
            throw new ParerUserError("Operazione non effettuata: dimensione file troppo elevate");
        } catch (ParserConfigurationException e) {
            log.error("Operazione non effettuata: " + e.toString(), e);
            throw new ParerUserError("Operazione non effettuata: " + e.toString());
        }

        if (!isValidType) {
            return false;
        }

        return true;
    }

    private void checkModelloXsdTipoFascicolo() throws EMFError, ParerUserError {
        log.debug("Eseguo i controlli sul salvataggio del modello xsd");
        ModelliXsdTipiFascicoloDetail modelliXsdTipiFascicoloDetail = getForm()
                .getModelliXsdTipiFascicoloDetail();
        BigDecimal idAmbiente = modelliXsdTipiFascicoloDetail.getId_ambiente().parse();
        final String tiModelloXsd = modelliXsdTipiFascicoloDetail.getTi_modello_xsd().parse();
        String cdXsd = modelliXsdTipiFascicoloDetail.getCd_xsd().parse();
        BigDecimal idModelloXsdUpdate = modelliXsdTipiFascicoloDetail.getId_modello_xsd_fascicolo()
                .parse();

        // Controllo esistenza codice versione modello xsd
        DecModelloXsdFascicoloRowBean tmpRow = modelliFascicoliEjb.getDecModelloXsdFascicoloRowBean(
                idAmbiente, tiModelloXsd, CostantiDB.TiUsoModelloXsd.VERS.name(), cdXsd);
        if (idModelloXsdUpdate != null) {
            if (tmpRow != null && !idModelloXsdUpdate.equals(tmpRow.getIdModelloXsdFascicolo())) {
                throw new ParerUserError(
                        "Il codice versione del modello xsd \u00E8 gi\u00E0 presente nell'ambiente");
            }
        } else if (tmpRow != null) {
            throw new ParerUserError(
                    "Il codice versione del modello xsd \u00E8 gi\u00E0 presente nell'ambiente");
        }

        // MEV#26576
        // Controllo che non vi sia un'altra versione attiva alla data per lo stesso
        // ambiente.
        // Se l'entità del modello è FASCICOLO, AIP_SELF_DESCRIPTION_MORE_INFO,
        // AIP_UNISYNCRO
        // non è possibile avere più versioni attive alla stessa data
        // if (TiModelloXsd.FASCICOLO.name().equals(tiModelloXsd)
        // || TiModelloXsd.AIP_SELF_DESCRIPTION_MORE_INFO.name().equals(tiModelloXsd)
        // || TiModelloXsd.AIP_UNISYNCRO.name().equals(tiModelloXsd)) {
        // List<DecModelloXsdFascicolo> modelliXsdAttivi =
        // modelliFascicoliEjb.checkModelliXsdAttiviInUse(idAmbiente,
        // tiModelloXsd);
        // if (modelliXsdAttivi != null && !modelliXsdAttivi.isEmpty()) {
        // throw new ParerUserError("Esiste già un modello attivo alla data");
        // }
        // }
        // end MEV#26576
    }

    /**
     * Analizza il contenuto di un file XSD per verificare se contiene import o include
     *
     * @param xsdContent contenuto del file XSD come stringa
     * @return true se l'XSD contiene tag xs:import o xs:include, false altrimenti
     */
    private boolean xsdContieneDipendenze(String xsdContent) {
        if (StringUtils.isBlank(xsdContent)) {
            return false;
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            // Security settings
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(
                    xsdContent.getBytes(java.nio.charset.StandardCharsets.UTF_8)));

            // Cerca tag <xs:import> o <xsd:import>
            NodeList imports = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                    "import");
            if (imports.getLength() > 0) {
                log.info("XSD contiene {} tag <import>", imports.getLength());
                return true;
            }

            // Cerca tag <xs:include> o <xsd:include>
            NodeList includes = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                    "include");
            if (includes.getLength() > 0) {
                log.info("XSD contiene {} tag <include>", includes.getLength());
                return true;
            }

            return false;

        } catch (Exception e) {
            // In caso di errore nel parsing, assume che non ci siano dipendenze
            // (l'XSD verrà comunque validato da validazioneXsd)
            log.warn("Errore nell'analisi delle dipendenze XSD", e);
            return false;
        }
    }

    @Override
    public void updateModelliXsdTipiFascicoloList() throws EMFError {
        getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setViewMode();

        // Recupera il modello corrente
        DecModelloXsdFascicoloRowBean currentRow = (DecModelloXsdFascicoloRowBean) getForm()
                .getModelliXsdTipiFascicoloList().getTable().getCurrentRow();
        BigDecimal idModello = currentRow.getIdModelloXsdFascicolo();

        // Mette in edit mode i campi modificabili del modello (sempre)
        getForm().getModelliXsdTipiFascicoloDetail().setViewMode();
        getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd().setEditMode();
        getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres().setEditMode();
        getForm().getModelliXsdTipiFascicoloDetail().getFl_default()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getModelliXsdTipiFascicoloDetail().getFl_default().setEditMode();

        getForm().getModelliXsdTipiFascicoloDetail().setStatus(Status.update);
        getForm().getModelliXsdTipiFascicoloList().setStatus(Status.update);

        // Carica le dipendenze XSD per mostrarle in modalità VIEW (toolbar
        // insert/delete)
        try {
            // Carica dipendenze esistenti (possono essere 0 o più)
            it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean deps = modelliFascicoliEjb
                    .getDipendenzaXsdTableBean(idModello);
            getForm().getDipendenzaXsdList().setTable(deps);
            getForm().getDipendenzaXsdList().setStatus(Status.view);

            // Verifica se XSD è modulare e mancano dipendenze
            DecModelloXsdFascicoloRowBean modelloCompleto = modelliFascicoliEjb
                    .getDecModelloXsdFascicoloRowBean(idModello);
            String xsdContent = modelloCompleto.getBlXsd();
            boolean isModulare = xsdContieneDipendenze(xsdContent);

            if (isModulare && (deps == null || deps.isEmpty())) {
                getMessageBox().addWarning(
                        "L'XSD contiene import/include ma non sono state ancora configurate le dipendenze. "
                                + "Utilizzare il pulsante 'Inserisci' per aggiungere i modelli richiamati.");
            }

            log.info("UPDATE: caricate {} dipendenze per modello ID={}",
                    deps != null ? deps.size() : 0, idModello);

        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore nel caricamento dipendenze: " + ex.getDescription());
        } catch (Exception e) {
            log.error("Errore in updateModelliXsdTipiFascicoloList", e);
            getMessageBox().addError("Errore durante il caricamento del modello XSD");
        }

        forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
    }

    @Override
    public void deleteModelliXsdTipiFascicoloList() throws EMFError {
        DecModelloXsdFascicoloRowBean currentRow = (DecModelloXsdFascicoloRowBean) getForm()
                .getModelliXsdTipiFascicoloList().getTable().getCurrentRow();
        getMessageBox().clear();
        BigDecimal idModelloXsdFascicolo = currentRow.getIdModelloXsdFascicolo();
        int riga = getForm().getModelliXsdTipiFascicoloList().getTable().getCurrentRowIndex();

        if (getLastPublisher().equals(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL)) {
            if (!idModelloXsdFascicolo.equals(getForm().getModelliXsdTipiFascicoloDetail()
                    .getId_modello_xsd_fascicolo().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione del modello xsd");
            }
        }

        if (!getMessageBox().hasError() && idModelloXsdFascicolo != null) {
            try {
                if (modelliFascicoliEjb
                        .isModelloXsdInUseInTipologieFascicolo(idModelloXsdFascicolo)) {
                    getMessageBox().addError(
                            "Il modello xsd di tipo fascicolo \u00E8 gi\u00E0 stato utilizzato per gestire delle tipologie di fascicolo: eliminazione non consentita");
                } else {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configHelper.getValoreParamApplicByApplic(
                                    CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (param.getNomePagina().equalsIgnoreCase(
                            Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                                getForm().getModelliXsdTipiFascicoloList()));
                    }
                    modelliFascicoliEjb.deleteDecModelloXsdFascicolo(param, idModelloXsdFascicolo);
                    getForm().getModelliXsdTipiFascicoloList().getTable().remove(riga);

                    getMessageBox().addInfo("Modello xsd eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (javax.ejb.EJBException ex) {
                Throwable rootCause = ExceptionUtils.getRootCause(ex);
                String rootMsg = rootCause != null ? rootCause.getMessage() : ex.getMessage();
                if (rootMsg != null && rootMsg.contains("FK_MDL_XSD_FASC_RIF_TGT")) {
                    getMessageBox().addError(
                            "Il modello XSD non pu\u00F2 essere eliminato poich\u00E9 \u00E8 associato a uno o pi\u00F9 modelli.");
                } else {
                    getMessageBox()
                            .addError("Il modello xsd non pu\u00F2 essere eliminato: " + rootMsg);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(
                        "Il modello xsd non pu\u00F2 essere eliminato: " + ex.getDescription());
            }
        }
        if (!getMessageBox().hasError() && getLastPublisher()
                .equals(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getModelliXsdTipiFascicoloList().getName())) {
                getForm().getModelliXsdTipiFascicoloDetail().setStatus(Status.view);
                getForm().getModelliXsdTipiFascicoloDetail().setViewMode();
                forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MODELLI_FASCICOLI;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName
                    .equals(Application.Publisher.LISTA_MODELLI_XSD_TIPI_FASCICOLO_SELECT)) {
                int rowIndex;
                int pageSize;
                if (getForm().getModelliXsdTipiFascicoloList().getTable() != null) {
                    rowIndex = getForm().getModelliXsdTipiFascicoloList().getTable()
                            .getCurrentRowIndex();
                    pageSize = getForm().getModelliXsdTipiFascicoloList().getTable().getPageSize();
                } else {
                    rowIndex = 0;
                    pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                }

                /*
                 * Ricavo la lista degli ambienti da cercare in base a come è stata impostata nei
                 * filtri di ricerca la combo Ambiente
                 */
                List<BigDecimal> idAmbientiToFind = getAmbientiToFind(
                        getForm().getFiltriModelliXsdTipiFascicolo());

                // Lista modelli xsd tipi Fascicolo
                getForm().getModelliXsdTipiFascicoloList().setTable(
                        modelliFascicoliEjb.getDecModelloXsdTipoFascicoliAbilitatiAmbienteTableBean(
                                getForm().getFiltriModelliXsdTipiFascicolo(), idAmbientiToFind,
                                CostantiDB.TiUsoModelloXsd.VERS.name(),
                                getForm().getModelliXsdTipiFascicoloList().isFilterValidRecords()));
                getForm().getModelliXsdTipiFascicoloList().getTable().setPageSize(pageSize);
                getForm().getModelliXsdTipiFascicoloList().getTable().setCurrentRowIndex(rowIndex);
            } else if (publisherName
                    .equals(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL)) {
                DecModelloXsdFascicoloRowBean currentRow = (DecModelloXsdFascicoloRowBean) getForm()
                        .getModelliXsdTipiFascicoloList().getTable().getCurrentRow();
                BigDecimal idModelloXsdFascicolo = currentRow.getIdModelloXsdFascicolo();
                if (idModelloXsdFascicolo != null) {
                    loadDettaglioModelloXsdTipoFascicolo(idModelloXsdFascicolo);
                }
                getForm().getModelliXsdTipiFascicoloDetail().setViewMode();
                getForm().getModelliXsdTipiFascicoloDetail().setStatus(Status.view);
                getForm().getModelliXsdTipiFascicoloList().setStatus(Status.view);
            }
            postLoad();
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(getLastPublisher());
        } catch (EMFError e) {
            log.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
        }
    }

    @Secure(action = "Menu.Amministrazione.ModelliTipiFascicoli")
    public void loadListaModelliXsdFascicoli() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.ModelliTipiFascicoli");

        // Azzero i filtri e la lista risultato della form di ricerca
        getForm().getFiltriModelliXsdTipiFascicolo().reset();

        getForm().getModelliXsdTipiFascicoloList().clear();
        getForm().getModelliXsdTipiFascicoloList().setUserOperations(true, true, true, true);

        // Inizializzo le combo di ricerca
        initComboRicercaModelliXsdTipiFascicolo();

        // Imposto tutti i filtri in edit mode
        getForm().getFiltriModelliXsdTipiFascicolo().setEditMode();

        // Imposto come visibile il bottone di ricerca mdoelli xsd tipi Fascicolo e
        // disabilito la
        // clessidra (per IE)
        getForm().getFiltriModelliXsdTipiFascicolo().getRicercaModelliButton().setEditMode();
        getForm().getFiltriModelliXsdTipiFascicolo().getRicercaModelliButton()
                .setDisableHourGlass(true);

        // Carico la pagina di ricerca
        forwardToPublisher(Application.Publisher.LISTA_MODELLI_XSD_TIPI_FASCICOLO_SELECT);
    }

    /**
     * Metodo che crea le mappe coi valori e setta le combo presenti nella pagina di ricerca modelli
     * XSD tipi Fascicolo
     *
     * @throws EMFError errore generico
     */
    private void initComboRicercaModelliXsdTipiFascicolo() throws EMFError {
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
        BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

        // Inizializzo le combo settando l'ambiente corrente
        BaseTableInterface ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerStrut(
                getUser().getIdUtente(),
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        ambienteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienteTableBean.sort();

        getForm().getFiltriModelliXsdTipiFascicolo().getId_ambiente().setDecodeMap(
                DecodeMap.Factory.newInstance(ambienteTableBean, "id_ambiente", "nm_ambiente"));
        getForm().getFiltriModelliXsdTipiFascicolo().getId_ambiente()
                .setValue(idAmbiente.toString());

        // Imposto le combo "Tipo modello xsd", "Standard" e "Attivo"
        getForm().getFiltriModelliXsdTipiFascicolo().getTi_modello_xsd()
                .setDecodeMap(ComboGetter.getMappaTiModelloXsd());
        getForm().getFiltriModelliXsdTipiFascicolo().getFl_default()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriModelliXsdTipiFascicolo().getAttivo_xsd()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    }

    /**
     * Ricava la lista degli ambienti da passare alla Ricerca Modelli xsd tipi fascicolo in base a
     * come sono stati
     *
     * @param filtriModelliXsdTipiFascicolo oggetto di tipo {@link FiltriModelliXsdTipiFascicolo}
     *
     * @return la lista degli ambienti da utilizzare per la ricerca
     *
     * @throws EMFError errore generico
     */
    private List<BigDecimal> getAmbientiToFind(
            ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo filtriModelliXsdTipiFascicolo)
            throws EMFError {
        /* Se la combo ambiente è vuota ricavo gli ambienti abilitati */
        List<BigDecimal> idAmbientiToFind = new ArrayList<>();
        if (filtriModelliXsdTipiFascicolo.getId_ambiente().parse() != null) {
            /*
             * Se la combo ambiente è compilata, passo l'ambiente tra quelli a cui l'utente è
             * abilitato in funzione delle strutture cui l'utente è abilitato
             */
            idAmbientiToFind.add(filtriModelliXsdTipiFascicolo.getId_ambiente().parse());
        } else {
            // Riprendo i valori dalla combo (non da DB, così evito un accesso)
            DecodeMapIF mappaFiltri = filtriModelliXsdTipiFascicolo.getId_ambiente().getDecodeMap();
            idAmbientiToFind = new ArrayList(mappaFiltri.keySet());
        }
        return idAmbientiToFind;
    }

    /**
     * Metodo richiamato al click del bottone di ricerca modelli xsd dei tipi fascicolo
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaModelliButton() throws EMFError {
        ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo filtriModelliXsdTipiFasc = getForm()
                .getFiltriModelliXsdTipiFascicolo();
        /* Esegue la post dei filtri compilati */
        filtriModelliXsdTipiFasc.post(getRequest());
        /*
         * Valida i filtri per verificarne la correttezza sintattica e l'obbligatorietà
         */
        if (filtriModelliXsdTipiFasc.validate(getMessageBox())) {
            /* Se la validazione non ha riportato errori */
            if (!getMessageBox().hasError()) {
                /*
                 * Ricavo la lista degli ambienti da cercare in base a come è stata impostata nei
                 * filtri di ricerca la combo Ambiente
                 */
                List<BigDecimal> idAmbientiToFind = getAmbientiToFind(filtriModelliXsdTipiFasc);

                // Lista modelli xsd tipi Fascicolo
                getForm().getModelliXsdTipiFascicoloList().setTable(
                        modelliFascicoliEjb.getDecModelloXsdTipoFascicoliAbilitatiAmbienteTableBean(
                                filtriModelliXsdTipiFasc, idAmbientiToFind,
                                CostantiDB.TiUsoModelloXsd.VERS.name(),
                                getForm().getModelliXsdTipiFascicoloList().isFilterValidRecords()));
                getForm().getModelliXsdTipiFascicoloList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getModelliXsdTipiFascicoloList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.LISTA_MODELLI_XSD_TIPI_FASCICOLO_SELECT);
    }

    @Override
    public void filterInactiveRecordsModelliXsdTipiFascicoloList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getModelliXsdTipiFascicoloList().getTable() != null) {
            rowIndex = getForm().getModelliXsdTipiFascicoloList().getTable().getCurrentRowIndex();
            pageSize = getForm().getModelliXsdTipiFascicoloList().getTable().getPageSize();
        }

        /*
         * Ricavo la lista degli ambienti da cercare in base a come è stata impostata nei filtri di
         * ricerca la combo Ambiente
         */
        List<BigDecimal> idAmbientiToFind = getAmbientiToFind(
                getForm().getFiltriModelliXsdTipiFascicolo());

        // Lista modelli xsd tipi Fascicolo
        getForm().getModelliXsdTipiFascicoloList().setTable(
                modelliFascicoliEjb.getDecModelloXsdTipoFascicoliAbilitatiAmbienteTableBean(
                        getForm().getFiltriModelliXsdTipiFascicolo(), idAmbientiToFind,
                        CostantiDB.TiUsoModelloXsd.VERS.name(),
                        getForm().getModelliXsdTipiFascicoloList().isFilterValidRecords()));
        getForm().getModelliXsdTipiFascicoloList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getModelliXsdTipiFascicoloList().getTable().setPageSize(pageSize);
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void logEventi() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp()
                .setValue(paramApplicHelper.getApplicationName().getDsValoreParamApplic());
        form.getOggettoDetail().getNm_tipo_oggetto()
                .setValue(SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO);
        DecModelloXsdFascicoloRowBean rb = (DecModelloXsdFascicoloRowBean) getForm()
                .getModelliXsdTipiFascicoloList().getTable().getCurrentRow();
        form.getOggettoDetail().getIdOggetto()
                .setValue(rb.getIdModelloXsdFascicolo().toPlainString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    protected void postLoad() {

        if (getForm().getModelliXsdTipiFascicoloList().getStatus().equals(Status.view)) {
            // Modalità VIEW: mostra bottoni dettaglio
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setHidden(false);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setHidden(false);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setEditMode();
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setEditMode();
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton()
                    .setDisableHourGlass(true);

            // Mostra sezione dipendenze SOLO se modello è root (non RICHIAMABILE) e ha
            // import/include
            boolean hasDipendenze = false;
            try {
                String tiModello = getForm().getModelliXsdTipiFascicoloDetail().getTi_modello_xsd()
                        .parse();
                boolean isRichiamabile = it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.RICHIAMABILE
                        .name().equals(tiModello);

                if (!isRichiamabile) {
                    String blXsd = getForm().getModelliXsdTipiFascicoloDetail().getBl_xsd().parse();
                    hasDipendenze = contaRiferimentiXsd(blXsd) > 0;
                }
            } catch (Exception e) {
                log.warn("postLoad: impossibile determinare riferimenti XSD dal form", e);
            }
            getForm().getDipendenzaXsdList().setHidden(!hasDipendenze);
            if (hasDipendenze) {
                getForm().getDipendenzaXsdList().setUserOperations(false, false, true, true);
            } else {
                getForm().getDipendenzaXsdList().setUserOperations(false, false, false, false);
            }
            log.debug("postLoad: VIEW mode - hasDipendenze={}", hasDipendenze);

        } else if (getForm().getModelliXsdTipiFascicoloList().getStatus().equals(Status.insert)) {
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setHidden(true);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setHidden(true);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setViewMode();
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setViewMode();

            getForm().getDipendenzaXsdList().setHidden(true);

        } else {
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setHidden(true);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setHidden(true);
            getForm().getModelliXsdTipiFascicoloDetail().getLogEventi().setViewMode();
            getForm().getModelliXsdTipiFascicoloDetail().getScaricaXsdButton().setViewMode();

            // In UPDATE mode, DipendenzaXsdList non è visibile (come in INSERT)
            getForm().getDipendenzaXsdList().setHidden(true);

            log.debug("postLoad: UPDATE mode - deps nascoste (visibili solo in VIEW)");
        }
    }

    @Override
    public void scaricaXsdButton() throws EMFError {
        DecModelloXsdFascicoloRowBean currentRow = (DecModelloXsdFascicoloRowBean) getForm()
                .getModelliXsdTipiFascicoloList().getTable().getCurrentRow();
        String nomeTipo = new String();
        try {
            if (getForm().getModelliXsdTipiFascicoloDetail().getId_modello_xsd_fascicolo()
                    .parse() != null) {
                nomeTipo = modelliFascicoliEjb
                        .getDecModelloXsdFascicoloRowBean(currentRow.getIdModelloXsdFascicolo())
                        .getTiModelloXsd();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        // TODO: verificare
        /*
         * if (getForm().getIdList().getNm_sys_migraz().parse() != null) { String sysMigraz =
         * getForm().getIdList().getNm_sys_migraz().parse(); nomeTipo = "Migr_" + sysMigraz + "_" +
         * getForm().getIdList().getNm_sacer_type().parse(); }
         */

        String codiceVersione = currentRow.getCdXsd();

        String filename = nomeTipo + "_xsd_" + codiceVersione;
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + ".zip");
        // definiamo l'output previsto che sarà un file in formato zip
        // di cui si occuperà la servlet per fare il download
        ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
        try {
            zipXsdTipoFascicolo(out, currentRow, filename);
            out.flush();
            out.close();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(
                    new Message(MessageLevel.ERR, "Errore nel recupero dei file da zippare"));
            log.error(e.getMessage(), e);
        }
    }

    /*
     *
     * Metodo per comprimere il clob presente all'interno della tabella Dec_Modello_Xsd_Fascicolo,
     * contenente il file xsd.
     *
     */
    private void zipXsdTipoFascicolo(ZipOutputStream out, DecModelloXsdFascicoloRowBean xsdRowBean,
            String filename) throws IOException {

        // definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        if (xsdRowBean != null) {

            byte[] blob = xsdRowBean.getBlXsd().getBytes(java.nio.charset.StandardCharsets.UTF_8);
            if (blob != null) {
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
    }

    @Override
    public JSONObject triggerDipendenzaXsdDetailId_modello_xsd_fascicoloOnTrigger()
            throws EMFError {
        getForm().getDipendenzaXsdDetail().post(getRequest());
        BigDecimal idModello = getForm().getDipendenzaXsdDetail().getId_modello_xsd_fascicolo()
                .parse();
        if (idModello != null) {
            try {
                DecModelloXsdFascicoloRowBean modello = modelliFascicoliEjb
                        .getDecModelloXsdFascicoloRowBean(idModello);
                if (modello != null) {
                    String ns = xsdRepositoryHelper.extractTargetNamespace(modello.getBlXsd());
                    getForm().getDipendenzaXsdDetail().getNamespace_uri()
                            .setValue(ns != null ? ns : "");
                    String cdXsd = modello.getCdXsd();
                    getForm().getDipendenzaXsdDetail().getSchema_location()
                            .setValue(cdXsd.endsWith(".xsd") ? cdXsd : cdXsd + ".xsd");
                }
            } catch (Exception e) {
                log.warn("Errore nel trigger id_modello_xsd_fascicolo: {}", e.getMessage());
            }
        } else {
            getForm().getDipendenzaXsdDetail().getNamespace_uri().setValue("");
            getForm().getDipendenzaXsdDetail().getSchema_location().setValue("");
        }
        return getForm().getDipendenzaXsdDetail().asJSON();
    }

    public void triggerModelliXsdTipiFascicoloDetailId_ambienteOnTriggerJs() throws EMFError {
        getForm().getModelliXsdTipiFascicoloDetail().getId_ambiente().post(getRequest());
        redirectToAjax(getForm().getModelliXsdTipiFascicoloDetail().asJSON());
    }

    /**
     * Carica la lista delle dipendenze XSD (sempre in view mode, non editabile inline)
     *
     * @param idPadre id del modello padre
     *
     * @throws ParerUserError errore generico
     */
    private void loadDipendenzaXsd(BigDecimal idPadre) throws ParerUserError {
        it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean table = modelliFascicoliEjb
                .getDipendenzaXsdTableBean(idPadre);

        // Popola il campo cd_xsd_target per la visualizzazione
        for (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean row : table) {
            if (row.getIdModelloXsdFascicoloTarget() != null) {
                try {
                    it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloRowBean modello = modelliFascicoliEjb
                            .getDecModelloXsdFascicoloRowBean(row.getIdModelloXsdFascicoloTarget());
                    if (modello != null) {
                        row.setString("cd_xsd_target", modello.getCdXsd());
                    }
                } catch (Exception e) {
                    log.warn(
                            "Errore nel recupero del cd_xsd per id_modello_xsd_fascicolo_target={}",
                            row.getIdModelloXsdFascicoloTarget(), e);
                }
            }
        }

        getForm().getDipendenzaXsdList().setTable(table);
        getForm().getDipendenzaXsdList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getDipendenzaXsdList().getTable().first();
        // Lista sempre in VIEW mode: abilita insert e delete tramite toolbar, no inline
        // edit
        getForm().getDipendenzaXsdList().setUserOperations(false, false, true, true);
    }

    /**
     * Elimina una dipendenza XSD: cancella la riga dalla sessione e, se la dipendenza esisteva già
     * sul DB, la elimina dal DB.
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteDipendenzaXsdList() throws EMFError {
        int rowIndex = getForm().getDipendenzaXsdList().getTable().getCurrentRowIndex();
        if (rowIndex >= 0 && rowIndex < getForm().getDipendenzaXsdList().getTable().size()) {

            // Se il modello esiste sul DB (ha un id): cancella la dep dal DB
            BigDecimal idModelloCheck = getForm().getModelliXsdTipiFascicoloDetail()
                    .getId_modello_xsd_fascicolo().parse();

            if (modelliFascicoliEjb.isModelloXsdInUseInTipologieFascicolo(idModelloCheck)) {
                getMessageBox().addError(
                        "Il modello xsd di tipo fascicolo \u00E8 gi\u00E0 stato utilizzato per gestire delle tipologie di fascicolo: la modifica delle dipendenze non \u00E8 consentita");
                forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
                return;
            }

            if (idModelloCheck != null) {
                eseguiDeleteDipendenza(rowIndex, idModelloCheck);
            }
        }
        forwardToPublisher(Application.Publisher.MODELLI_XSD_TIPI_FASCICOLO_DETAIL);
    }

    /**
     * Esegue la cancellazione effettiva della dipendenza dalla sessione e dal DB.
     */
    private void eseguiDeleteDipendenza(int rowIndex, BigDecimal idModelloCheck) {
        try {
            it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean rowDaRimuovere = (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean) getForm()
                    .getDipendenzaXsdList().getTable().getRow(rowIndex);
            BigDecimal idDipendenza = rowDaRimuovere.getIdModelloXsdFascRif();

            if (idDipendenza != null) {
                modelliFascicoliEjb.deleteDipendenza(idDipendenza);
            }

            getForm().getDipendenzaXsdList().getTable().remove(rowIndex);

            // Rilegge deps aggiornate dal DB e ricontrolla coerenza con XSD
            DecModelloXsdFascicoloRowBean modello = modelliFascicoliEjb
                    .getDecModelloXsdFascicoloRowBean(idModelloCheck);
            String xsdRoot = modello.getBlXsd();
            DecModelloXsdFascRifTableBean depsAggiornate = modelliFascicoliEjb
                    .getDipendenzaXsdTableBean(idModelloCheck);
            int totaleRiferimentiXsd = contaRiferimentiXsd(xsdRoot);
            int totaleDepsConfigurate = depsAggiornate != null ? depsAggiornate.size() : 0;

            if (totaleDepsConfigurate < totaleRiferimentiXsd) {
                getMessageBox().addWarning("Dipendenza rimossa. Attenzione: l'XSD dichiara "
                        + totaleRiferimentiXsd + " riferimenti, configurate "
                        + totaleDepsConfigurate + " su " + totaleRiferimentiXsd
                        + ". Aggiungere le dipendenze mancanti.");
            } else {
                getMessageBox().addInfo("Dipendenza rimossa con successo");
            }
            getMessageBox().setViewMode(ViewMode.plain);

        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore di validazione: " + ex.getDescription());
        } catch (Exception ex) {
            getMessageBox().addError("Errore nel salvataggio delle modifiche: " + ex.getMessage());
        }
    }

    /**
     * Inizializza la pagina di dettaglio per l'associazione di dipendenze XSD
     */
    private void initDipendenzaXsdDetail() throws EMFError {
        getForm().getDipendenzaXsdDetail().reset();

        // Recupera il modello corrente
        BigDecimal idModelloXsdFascicolo = getForm().getModelliXsdTipiFascicoloDetail()
                .getId_modello_xsd_fascicolo().parse();
        String cdXsd = getForm().getModelliXsdTipiFascicoloDetail().getCd_xsd().parse();
        String dsXsd = getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd().parse();

        // Imposta i dati del modello padre in sola lettura (solo per contesto visivo)
        getForm().getDipendenzaXsdDetail().getCd_xsd().setValue(cdXsd);
        getForm().getDipendenzaXsdDetail().getDs_xsd().setValue(dsXsd);
        // La combo getId_modello_xsd_fascicolo parte vuota: l'utente seleziona il
        // richiamabile

        // Inizializza combo tipo riferimento
        BaseTable tipoRifTable = new BaseTable();
        BaseRow rowImport = new BaseRow();
        BaseRow rowInclude = new BaseRow();
        rowImport.setString(TI_RIFERIMENTO, TIPO_RIFERIMENTO_IMPORT);
        rowInclude.setString(TI_RIFERIMENTO, TIPO_RIFERIMENTO_INCLUDE);
        tipoRifTable.add(rowImport);
        tipoRifTable.add(rowInclude);
        DecodeMap mappaTipoRif = DecodeMap.Factory.newInstance(tipoRifTable, TI_RIFERIMENTO,
                TI_RIFERIMENTO);
        getForm().getDipendenzaXsdDetail().getTi_riferimento().setDecodeMap(mappaTipoRif);

        // Carica modelli richiamabili per la combo
        try {
            DecModelloXsdFascicoloTableBean modelliRichiamabili = modelliFascicoliEjb
                    .getModelliRichiamabili();

            // Costruisce DecodeMap e JSON per JavaScript
            BaseTable modelliTable = new BaseTable();
            StringBuilder jsonBuilder = new StringBuilder("{");
            boolean first = true;

            for (DecModelloXsdFascicoloRowBean modello : modelliRichiamabili) {
                // Escludi solo il modello corrente (non può dipendere da se stesso)
                if (idModelloXsdFascicolo != null
                        && modello.getIdModelloXsdFascicolo().equals(idModelloXsdFascicolo)) {
                    continue;
                }

                // Aggiungi alla DecodeMap
                BaseRow row = new BaseRow();
                row.setBigDecimal("id_modello", modello.getIdModelloXsdFascicolo());
                row.setString("cd_xsd", modello.getCdXsd() + " - " + modello.getDsXsd());
                modelliTable.add(row);

                // Estrai targetNamespace dall'XSD
                String targetNamespace = estraiTargetNamespace(modello.getBlXsd());

                // Aggiungi al JSON per JavaScript
                if (!first) {
                    jsonBuilder.append(",");
                }
                jsonBuilder.append("\"").append(modello.getIdModelloXsdFascicolo()).append("\":{");
                jsonBuilder.append("\"namespace\":\"").append(targetNamespace.replace("\"", "\\\""))
                        .append("\",");
                String cdXsdConExt = modello.getCdXsd().endsWith(".xsd") ? modello.getCdXsd()
                        : modello.getCdXsd() + ".xsd";
                jsonBuilder.append("\"schemaLocation\":\"")
                        .append(cdXsdConExt.replace("\"", "\\\"")).append("\"");
                jsonBuilder.append("}");
                first = false;
            }
            jsonBuilder.append("}");

            // Imposta la DecodeMap per la combo
            DecodeMap mappaModelli = DecodeMap.Factory.newInstance(modelliTable, "id_modello",
                    "cd_xsd");
            getForm().getDipendenzaXsdDetail().getId_modello_xsd_fascicolo()
                    .setDecodeMap(mappaModelli);

            // Passa il JSON al JSP tramite request attribute
            getRequest().setAttribute("modelliJsonData", jsonBuilder.toString());

        } catch (Exception e) {
            getMessageBox()
                    .addError("Errore nel caricamento dei modelli richiamabili: " + e.getMessage());
        }

        getForm().getDipendenzaXsdDetail().setEditMode();
        getForm().getDipendenzaXsdDetail().setStatus(Status.insert);
        getForm().getDipendenzaXsdDetail().getCd_xsd().setViewMode();
        getForm().getDipendenzaXsdDetail().getDs_xsd().setViewMode();
    }

    /**
     * Estrae il targetNamespace dall'XSD di un modello richiamabile. Restituisce stringa vuota in
     * caso di XSD assente o errore di parsing.
     */
    private String estraiTargetNamespace(String blXsd) {
        if (blXsd == null || blXsd.trim().isEmpty()) {
            return "";
        }
        try {
            String ns = xsdRepositoryHelper.extractTargetNamespace(blXsd);
            return ns != null ? ns : "";
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Salva le associazioni selezionate e torna al dettaglio modello
     */
    @Override
    public void saveDettaglio() throws EMFError {
        // Check if we're coming from the dipendenza detail page
        if (getTableName().equals(getForm().getDipendenzaXsdDetail().getName())) {
            saveAssociazioniDipendenzaXsd();
            return;
        }
        if (getForm().getModelliXsdTipiFascicoloList().getStatus().equals(Status.update)) {
            getForm().getModelliXsdTipiFascicoloDetail().post(getRequest());
            String tiModelloXsd = getForm().getModelliXsdTipiFascicoloDetail().getTi_modello_xsd()
                    .parse();
            String flDefault = getForm().getModelliXsdTipiFascicoloDetail().getFl_default().parse();
            String cdVersione = getForm().getModelliXsdTipiFascicoloDetail().getCd_xsd().parse();
            String dsVersione = getForm().getModelliXsdTipiFascicoloDetail().getDs_xsd().parse();
            Date dtIstituz = getForm().getModelliXsdTipiFascicoloDetail().getDt_istituz().parse();
            Date dtSoppres = getForm().getModelliXsdTipiFascicoloDetail().getDt_soppres().parse();
            // Nessun file XSD in questo path: null indica al metodo di non toccare bl_xsd
            saveModelloXsdTipiFasc(tiModelloXsd, flDefault, cdVersione, dsVersione, dtIstituz,
                    dtSoppres, null);
        }
    }

    /**
     * Salva le associazioni di dipendenza XSD per il modello corrente, con validazione
     *
     * @throws EMFError
     */
    private void saveAssociazioniDipendenzaXsd() throws EMFError {
        if (!getForm().getDipendenzaXsdDetail().postAndValidate(getRequest(), getMessageBox())) {
            forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
            return;
        }

        try {
            String tiRiferimento = getForm().getDipendenzaXsdDetail().getTi_riferimento().parse();
            String namespaceUri = getForm().getDipendenzaXsdDetail().getNamespace_uri().parse();
            String schemaLocation = getForm().getDipendenzaXsdDetail().getSchema_location().parse();
            BigDecimal idModelloRichiamabile = getForm().getDipendenzaXsdDetail()
                    .getId_modello_xsd_fascicolo().parse();

            // Validazione campi obbligatori
            if (StringUtils.isBlank(tiRiferimento)) {
                getMessageBox().addError("Tipo riferimento obbligatorio");
                forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
                return;
            }
            if (idModelloRichiamabile == null) {
                getMessageBox().addError("Selezionare un modello richiamabile");
                forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
                return;
            }
            if (TIPO_RIFERIMENTO_IMPORT.equals(tiRiferimento)
                    && StringUtils.isBlank(namespaceUri)) {
                getMessageBox().addError("Namespace URI obbligatorio per tipo riferimento IMPORT");
                forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
                return;
            }
            if (StringUtils.isBlank(schemaLocation)) {
                getMessageBox().addError("Schema location obbligatorio");
                forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
                return;
            }

            // Recupera il modello selezionato dal DB
            DecModelloXsdFascicoloRowBean modelloSelezionato = modelliFascicoliEjb
                    .getDecModelloXsdFascicoloRowBean(idModelloRichiamabile);
            if (modelloSelezionato == null) {
                getMessageBox().addError("Modello selezionato non trovato");
                forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
                return;
            }

            // Costruisce la nuova dipendenza
            DecModelloXsdFascRifRowBean newDep = new DecModelloXsdFascRifRowBean();
            newDep.setIdModelloXsdFascicoloTarget(modelloSelezionato.getIdModelloXsdFascicolo());
            newDep.setString("cd_xsd_target", modelloSelezionato.getCdXsd());
            newDep.setTiRiferimento(tiRiferimento);
            newDep.setNamespaceUri(namespaceUri);
            newDep.setSchemaLocation(schemaLocation);

            // Imposta le date standard
            Calendar cal = Calendar.getInstance();
            newDep.setDtIstituz(new Timestamp(cal.getTimeInMillis()));
            cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            cal.set(Calendar.MILLISECOND, 0);
            newDep.setDtSoppres(new Timestamp(cal.getTimeInMillis()));

            BigDecimal idModelloXsdFascicolo = getForm().getModelliXsdTipiFascicoloDetail()
                    .getId_modello_xsd_fascicolo().parse();

            if (idModelloXsdFascicolo == null) {
                getMessageBox()
                        .addError("ID modello non trovato: impossibile salvare le dipendenze");
                forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
                return;
            }

            // Persiste la singola dipendenza sul DB (insert + flush)
            modelliFascicoliEjb.saveDipendenze(idModelloXsdFascicolo, newDep);

            // Recupera XSD root e deps aggiornate dal DB (inclusa la dep appena inserita)
            DecModelloXsdFascicoloRowBean modelloEsistente = modelliFascicoliEjb
                    .getDecModelloXsdFascicoloRowBean(idModelloXsdFascicolo);
            String xsdRoot = modelloEsistente.getBlXsd();
            DecModelloXsdFascRifTableBean deps = modelliFascicoliEjb
                    .getDipendenzaXsdTableBean(idModelloXsdFascicolo);
            int totaleRiferimentiXsd = contaRiferimentiXsd(xsdRoot);
            int totaleDepsConfigurate = deps != null ? deps.size() : 0;

            // Mostra il messaggio di successo solo se le dipendenze sono complete
            if (totaleDepsConfigurate >= totaleRiferimentiXsd) {
                getMessageBox().addInfo("Dipendenze configurate: " + totaleDepsConfigurate + " su "
                        + totaleRiferimentiXsd
                        + ". Il modello è ora utilizzabile per la validazione.");
            }
            getMessageBox().setViewMode(ViewMode.plain);

        } catch (javax.ejb.EJBException ex) {
            // EJBException wrappa IllegalStateException lanciata dall'EJB (es. validazione
            // XSD fallita)
            // Il container ha già fatto rollback
            log.error("Errore nella validazione XSD delle dipendenze", ex);
            Throwable rootCause = ExceptionUtils.getRootCause(ex);
            getMessageBox().addError(rootCause != null ? rootCause.getMessage() : ex.getMessage());
            forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
            return;
        } catch (Exception ex) {
            log.error("Errore nel salvataggio delle dipendenze XSD", ex);
            getMessageBox().addError("Errore nel salvataggio delle dipendenze: "
                    + ExceptionUtils.getRootCauseMessage(ex));
            forwardToPublisher(Application.Publisher.DIPENDENZA_XSD_MODELLO_DETAIL);
            return;
        }

        goBack();
    }

    /**
     * Conta il numero totale di xs:import e xs:include dichiarati nell'XSD root.
     */
    private int contaRiferimentiXsd(String blXsd) {
        if (StringUtils.isBlank(blXsd)) {
            return 0;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(
                    blXsd.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
            return doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "import")
                    .getLength()
                    + doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "include")
                            .getLength();
        } catch (Exception e) {
            log.warn("Impossibile contare i riferimenti nell'XSD", e);
            return 0;
        }
    }
}