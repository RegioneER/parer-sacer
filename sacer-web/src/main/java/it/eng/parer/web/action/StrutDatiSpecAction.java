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

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutDatiSpecAbstractAction;
import it.eng.parer.slite.gen.form.StrutDatiSpecForm.AttribDatiSpec;
import it.eng.parer.slite.gen.form.StrutTipiForm;
import it.eng.parer.slite.gen.form.StrutTipoStrutForm;
import it.eng.parer.slite.gen.tablebean.*;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.spagoCore.ConfigSingleton;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static it.eng.spagoCore.ConfigProperties.StandardProperty.*;

public class StrutDatiSpecAction extends StrutDatiSpecAbstractAction {

    private static Logger log = LoggerFactory.getLogger(StrutDatiSpecAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecificiEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void process() throws EMFError {

        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());

        if (isMultipart) {

            if (getLastPublisher().equals(Application.Publisher.XSD_DATI_SPEC_DETAIL)) {
                try {
                    readXsdTipoDocForm();
                } catch (ParerUserError | UnsupportedEncodingException ex) {
                    getMessageBox().addError(ex.getMessage());
                }
            }
        }
    }

    @Override
    public void scaricaXsdButton() throws EMFError {
        DecXsdDatiSpecRowBean currentRow = (DecXsdDatiSpecRowBean) getForm().getXsdDatiSpecList()
                .getTable().getCurrentRow();
        String nomeTipo = new String();

        if (getForm().getIdList().getId_tipo_doc().parse() != null) {
            nomeTipo = tipoDocEjb.getDecTipoDocRowBean(currentRow.getIdTipoDoc(), null)
                    .getNmTipoDoc();
        } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
            nomeTipo = tipoUnitaDocEjb
                    .getDecTipoUnitaDocRowBean(currentRow.getIdTipoUnitaDoc(), null)
                    .getNmTipoUnitaDoc();
        } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
            nomeTipo = tipoStrutDocEjb.getDecTipoCompDocRowBean(currentRow.getIdTipoCompDoc())
                    .getNmTipoCompDoc();
        }

        if (getForm().getIdList().getNm_sys_migraz().parse() != null) {
            String sysMigraz = getForm().getIdList().getNm_sys_migraz().parse();
            nomeTipo = "Migr_" + sysMigraz + "_" + getForm().getIdList().getNm_sacer_type().parse();
        }

        String codiceVersione = currentRow.getCdVersioneXsd();

        String filename = nomeTipo + "_xsd_" + codiceVersione;
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + filename + ".zip");
        // definiamo l'output previsto che sarà un file in formato zip
        // di cui si occuperà la servlet per fare il download
        ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
        try {
            zipXsdTipoDoc(out, currentRow, filename);
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
     * Metodo per comprimere il clob presente all'interno della tabella Dec_Xsd_Tipo_doc, contenente
     * il file xsd.
     *
     */
    private void zipXsdTipoDoc(ZipOutputStream out, DecXsdDatiSpecRowBean xsdRowBean,
            String filename) throws IOException {

        // definiamo il buffer per lo stream di bytes
        byte[] data = new byte[1000];
        if (xsdRowBean != null) {

            byte[] blob = xsdRowBean.getBlXsd().getBytes();
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

    @Override
    public void caricaXsdButton() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {

        String lista = getTableName();
        if (lista.equals(getForm().getXsdDatiSpecList().getName())
                && (getForm().getXsdDatiSpecList().getTable() != null)
                && (getForm().getXsdDatiSpecList().getTable().size() > 0)) {

            if (getNavigationEvent().equals(NE_DETTAGLIO_VIEW)) {
                getForm().getXsdDatiSpec().getScaricaXsdButton().setEditMode();
                getForm().getXsdDatiSpec().getScaricaXsdButton().setDisableHourGlass(true);
            }

            if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                getRequest().setAttribute("lastPage", "tipoDoc");
            } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                getRequest().setAttribute("lastPage", "tipoUnitaDoc");
            } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
                getRequest().setAttribute("lastPage", "tipoCompDoc");
            }

            getSession().setAttribute("xsdPublisher", getLastPublisher());

            getForm().getXsdDatiSpec().setViewMode();
            getForm().getXsdDatiSpec().setStatus(Status.view);
            getForm().getXsdDatiSpecList().setStatus(Status.view);

            BigDecimal idXsdDatiSpec = ((DecXsdDatiSpecRowBean) getForm().getXsdDatiSpecList()
                    .getTable().getCurrentRow()).getIdXsdDatiSpec();

            DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
                    .getXsdDatiSpecList().getTable().getCurrentRow());
            getForm().getXsdDatiSpec().copyFromBean(xsdDatiSpecRowBean);

            getForm().getXsdDatiSpec().getScaricaXsdButton().setEditMode();
            getForm().getXsdDatiSpec().getScaricaXsdButton().setDisableHourGlass(true);
            getForm().getXsdDatiSpec().getCaricaXsdButton().setViewMode();

            if (getForm().getXsdDatiSpecList().getTable().size() > 0) {

                DecAttribDatiSpecTableBean attribDatiSpecTableBean = datiSpecificiEjb
                        .getDecAttribDatiSpecTableBeanFromXsd(idXsdDatiSpec);
                getForm().getAttribDatiSpecList().setTable(attribDatiSpecTableBean);
                getForm().getAttribDatiSpecList().getTable().first();

                for (DecAttribDatiSpecRowBean row : attribDatiSpecTableBean) {
                    DecXsdAttribDatiSpecRowBean xsdAttribDatiSpecRowBean = datiSpecificiEjb
                            .getDecXsdAttribDatiSpecRowBeanByAttrib(row.getIdAttribDatiSpec(),
                                    xsdDatiSpecRowBean.getIdXsdDatiSpec());
                    getForm().getAttribDatiSpecList().getTable().getCurrentRow().setBigDecimal(
                            "ni_ord_attrib", xsdAttribDatiSpecRowBean.getNiOrdAttrib());
                    getForm().getAttribDatiSpecList().getTable().getCurrentRow().setString(
                            "ds_attrib_dati_spec", xsdAttribDatiSpecRowBean.getDsAttribDatiSpec());
                    getForm().getAttribDatiSpecList().getTable().next();
                }

                getForm().getAttribDatiSpecList().getTable().addSortingRule("ni_ord_attrib",
                        SortingRule.ASC);
                getForm().getAttribDatiSpecList().getTable().sort();
                getForm().getAttribDatiSpecList().getTable().first();
                getForm().getAttribDatiSpecList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            }

            String cessato = (String) getRequest().getParameter("cessato");
            if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                getForm().getXsdDatiSpecList().setUserOperations(true, false, false, false);
                getForm().getAttribDatiSpecList().setUserOperations(true, false, false, false);
            }

        } else if (lista.equals(getForm().getAttribDatiSpecList().getName())
                && (getForm().getAttribDatiSpecList().getTable() != null)
                && (getForm().getAttribDatiSpecList().getTable().size() > 0)) {

            if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                getRequest().setAttribute("lastPage", "tipoDoc");
            } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                getRequest().setAttribute("lastPage", "tipoUnitaDoc");
            } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
                getRequest().setAttribute("lastPage", "tipoCompDoc");
            }

            getForm().getAttribDatiSpec().setViewMode();
            getForm().getAttribDatiSpecList().setStatus(Status.view);

            BigDecimal idAttribDatiSpec = ((DecAttribDatiSpecRowBean) getForm()
                    .getAttribDatiSpecList().getTable().getCurrentRow()).getIdAttribDatiSpec();
            BigDecimal idXsdDatiSpec = ((DecXsdDatiSpecRowBean) getForm().getXsdDatiSpecList()
                    .getTable().getCurrentRow()).getIdXsdDatiSpec();
            DecAttribDatiSpecRowBean attribDatiSpecRowBean = datiSpecificiEjb
                    .getDecAttribDatiSpecRowBean(idAttribDatiSpec);
            DecXsdAttribDatiSpecRowBean xsdAttribDatiSpecRowBean = datiSpecificiEjb
                    .getDecXsdAttribDatiSpecRowBeanByAttrib(idAttribDatiSpec, idXsdDatiSpec);

            getForm().getAttribDatiSpec().copyFromBean(attribDatiSpecRowBean);
            getForm().getAttribDatiSpec().getNi_ord_attrib()
                    .setValue(xsdAttribDatiSpecRowBean.getNiOrdAttrib().toString());
            getForm().getAttribDatiSpec().getDs_attrib_dati_spec()
                    .setValue(xsdAttribDatiSpecRowBean.getDsAttribDatiSpec());

        }

    }

    @Override
    public void undoDettaglio() throws EMFError {
        goBack();
    }

    @Override
    public void insertDettaglio() throws EMFError {

        String lista = getRequest().getParameter("table");

        if (lista.equals(getForm().getXsdDatiSpecList().getName())) {
            getForm().getXsdDatiSpec().setEditMode();
            getForm().getXsdDatiSpec().clear();

            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);

            getForm().getXsdDatiSpec().getDt_istituz()
                    .setValue(df.format(Calendar.getInstance().getTime()));
            getForm().getXsdDatiSpec().getDt_soppres().setValue(df.format(calendar.getTime()));

            getSession().setAttribute("xsdPublisher", getLastPublisher());

            getForm().getXsdDatiSpec().setStatus(Status.insert);
            getForm().getXsdDatiSpecList().setStatus(Status.insert);

            getForm().getXsdDatiSpec().getFile_xsd().setViewMode();
            getForm().getXsdDatiSpec().getFile_xsd().setValue("-");

        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL)) {
            salvaDsAttribDatiSpec();

        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {

        // String lista = getRequest().getParameter("table");
        String lista = getTableName();
        String action = getNavigationEvent();
        getSession().setAttribute("lista", lista);

        if (action != null && !action.equals(NE_DETTAGLIO_DELETE)) {
            if (getForm().getXsdDatiSpecList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
            } else if (getForm().getAttribDatiSpecList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.XSD_DATI_SPEC_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (getLastPublisher().equals(Application.Publisher.XSD_DATI_SPEC_DETAIL)) {
                if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                    getRequest().setAttribute("lastPage", "tipoDoc");
                } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                    getRequest().setAttribute("lastPage", "tipoUnitaDoc");
                } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
                    getRequest().setAttribute("lastPage", "tipoCompDoc");
                }

                DecXsdDatiSpecTableBean xsdDatiSpecTableBean;
                DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();

                if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                    xsdDatiSpecRowBean.setIdTipoDoc(getForm().getIdList().getId_tipo_doc().parse());
                    xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.DOC.name());
                } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                    xsdDatiSpecRowBean.setIdTipoUnitaDoc(
                            getForm().getIdList().getId_tipo_unita_doc().parse());
                    xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.UNI_DOC.name());
                } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
                    xsdDatiSpecRowBean
                            .setIdTipoCompDoc(getForm().getIdList().getId_tipo_comp_doc().parse());
                    xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.COMP.name());
                }
                xsdDatiSpecRowBean.setIdStrut(getForm().getIdList().getId_strut().parse());
                xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());

                xsdDatiSpecTableBean = datiSpecificiEjb
                        .getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

                getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
                getForm().getXsdDatiSpecList().getTable().first();
                getForm().getXsdDatiSpecList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getXsdDatiSpecList().setStatus(Status.view);

            } else if (getLastPublisher().equals(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL)) {

                if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                    getRequest().setAttribute("lastPage", "tipoDoc");
                } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                    getRequest().setAttribute("lastPage", "tipoUnitaDoc");
                } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
                    getRequest().setAttribute("lastPage", "tipoCompDoc");
                }

                DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
                        .getXsdDatiSpecList().getTable().getCurrentRow());
                DecAttribDatiSpecTableBean attribDatiSpecTableBean = datiSpecificiEjb
                        .getDecAttribDatiSpecTableBeanFromXsd(
                                xsdDatiSpecRowBean.getIdXsdDatiSpec());

                getForm().getAttribDatiSpecList().setTable(attribDatiSpecTableBean);
                getForm().getAttribDatiSpecList().getTable().first();

                for (DecAttribDatiSpecRowBean row : attribDatiSpecTableBean) {
                    DecXsdAttribDatiSpecRowBean xsdAttribDatiSpecRowBean = datiSpecificiEjb
                            .getDecXsdAttribDatiSpecRowBeanByAttrib(row.getIdAttribDatiSpec(),
                                    xsdDatiSpecRowBean.getIdXsdDatiSpec());
                    getForm().getAttribDatiSpecList().getTable().getCurrentRow().setBigDecimal(
                            "ni_ord_attrib", xsdAttribDatiSpecRowBean.getNiOrdAttrib());
                    getForm().getAttribDatiSpecList().getTable().getCurrentRow().setString(
                            "ds_attrib_dati_spec", xsdAttribDatiSpecRowBean.getDsAttribDatiSpec());
                    getForm().getAttribDatiSpecList().getTable().next();
                }

                getForm().getAttribDatiSpecList().getTable().addSortingRule("ni_ord_attrib",
                        SortingRule.ASC);
                getForm().getAttribDatiSpecList().getTable().sort();
                getForm().getAttribDatiSpecList().getTable().first();
                getForm().getAttribDatiSpecList().setStatus(Status.view);
                getForm().getAttribDatiSpecList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            }
        } catch (EMFError ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUT_DATI_SPEC;
    }

    /**
     * Metodo che legge la form e passa i dati alla SalvaXsdTipoDoc
     *
     * @throws ParerUserError               errore generico
     * @throws EMFError                     errore generico
     * @throws UnsupportedEncodingException errore generico
     */
    public void readXsdTipoDocForm() throws ParerUserError, EMFError, UnsupportedEncodingException {

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
                    // se è un FormField, è sicuramente il nome file
                    tmpFileItem = (DiskFileItem) item;
                } else if (item.getFieldName().equals("Cd_versione_xsd")) {
                    getForm().getXsdDatiSpec().getCd_versione_xsd().setValue(item.getString());
                } else if (item.getFieldName().equals("Ds_versione_xsd")) {
                    getForm().getXsdDatiSpec().getDs_versione_xsd()
                            .setValue(item.getString("UTF-8"));
                } else if (item.getFieldName().equals("Dt_istituz")) {
                    getForm().getXsdDatiSpec().getDt_istituz().setValue(item.getString());
                } else if (item.getFieldName().equals("Dt_soppres")) {
                    getForm().getXsdDatiSpec().getDt_soppres().setValue(item.getString());
                    // se non è cd_versione_xsd e non è table, allora è l'operation
                } else if (!item.getFieldName().equals("table")) {
                    String fieldName = item.getFieldName();
                    if (fieldName.contains(NE_DETTAGLIO_CANCEL)
                            || fieldName.contains(NE_DETTAGLIO_SAVE)) {
                        tmpOperation = (DiskFileItem) item;
                    }
                }
            }

            if (tmpOperation != null && tmpOperation.getFieldName().contains(NE_DETTAGLIO_CANCEL)) {
                goBack();
            } else if (tmpOperation != null
                    && tmpOperation.getFieldName().contains(NE_DETTAGLIO_SAVE)) {
                if (getForm().getXsdDatiSpec().getStatus().equals(Status.insert)) {
                    // controllo esistenza del file
                    if (tmpFileItem == null || StringUtils.isBlank(tmpFileItem.getName())) {
                        getMessageBox().addError("Nessun file selezionato");
                    }
                }
                // controllo esistenza codice versione
                String cdVersione = getForm().getXsdDatiSpec().getCd_versione_xsd().parse();
                String dsVersione = getForm().getXsdDatiSpec().getDs_versione_xsd().parse();
                if (StringUtils.isBlank(cdVersione)) {
                    getMessageBox().addError("Versione non inserita");
                }

                SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
                Date dtIstituz = getForm().getXsdDatiSpec().getDt_istituz().parse();
                if (dtIstituz == null) {
                    dtIstituz = Calendar.getInstance().getTime();
                    getForm().getXsdDatiSpec().getDt_istituz().setValue(df.format(dtIstituz));
                }

                Date dtSoppres = getForm().getXsdDatiSpec().getDt_soppres().parse();
                if (dtSoppres == null) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(2444, 11, 31, 0, 0, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    dtSoppres = calendar.getTime();

                    getForm().getXsdDatiSpec().getDt_soppres().setValue(df.format(dtSoppres));
                }

                if (getMessageBox().isEmpty()) {
                    String clob = null;
                    if (tmpFileItem != null && StringUtils.isNotBlank(tmpFileItem.getName())) {
                        FileBinario fileBin = getFileBinario(tmpFileItem);
                        // conversione in stringa
                        clob = new String(fileBin.getDati());
                    }
                    if (StringUtils.isNotBlank(clob)) {
                        // compilazione schema
                        // 1. Lookup a factory for the W3C XML Schema language
                        SchemaFactory tmpSchemaFactoryValidazSpec = SchemaFactory
                                .newInstance("http://www.w3.org/2001/XMLSchema");
                        // anche in questo caso l'eccezione non deve mai verificarsi, a meno di non
                        // aver caricato
                        // nel database un xsd danneggiato...
                        try {
                            // 2. Compile the schema.
                            tmpSchemaFactoryValidazSpec
                                    .newSchema(new StreamSource(new StringReader(clob)));
                        } catch (SAXException e) {
                            log.error("Eccezione nel parsing dello schema del file xsd", e);
                            throw new FileUploadException(e.getLocalizedMessage());
                        }
                    }
                    // getForm().getXsdDatiSpec().getFile_xsd().setValue("File " +
                    // tmpFileItem.getName() + " caricato");
                    salvaXsdDatiSpec(cdVersione, dsVersione, dtIstituz, dtSoppres, clob);
                }
            }
        } catch (FileUploadException ex) {
            log.error("Eccezione nell'upload dei file", ex);
            throw new ParerUserError("Eccezione nell'upload dei file");
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

    private void salvaXsdDatiSpec(String cdVersione, String dsVersione, Date dtIstituz,
            Date dtSoppres, String file) throws EMFError {
        getMessageBox().clear();
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();

        try {
            if (StringUtils.isNotBlank(file) && !this.validazioneXsd(file)) {
                getMessageBox().addError(
                        "Dati specifici contenuti nel file XSD non conformi alla tipologia richiesta</br>");
            }

            if (getMessageBox().isEmpty()) {
                // inizializzo il rowbean
                BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

                xsdDatiSpecRowBean.setCdVersioneXsd(cdVersione);
                if (StringUtils.isNotBlank(file)) {
                    xsdDatiSpecRowBean.setBlXsd(file);
                }
                xsdDatiSpecRowBean.setIdStrut(idStrut);
                xsdDatiSpecRowBean.setDtIstituz(new Timestamp(dtIstituz.getTime()));
                xsdDatiSpecRowBean.setDtSoppres(new Timestamp(dtSoppres.getTime()));
                xsdDatiSpecRowBean.setDsVersioneXsd(dsVersione);

                /*
                 * Se l'xsd è da usare per le migrazioni, inizializzo diversamente il rowbean
                 */
                if (getForm().getIdList().getNm_sys_migraz().parse() != null) {

                    xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name());
                    xsdDatiSpecRowBean
                            .setTiEntitaSacer(getForm().getIdList().getNm_sacer_type().parse());
                    xsdDatiSpecRowBean
                            .setNmSistemaMigraz(getForm().getIdList().getNm_sys_migraz().parse());

                } else {
                    xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());

                    if (getForm().getIdList().getId_tipo_doc().parse() != null) {
                        xsdDatiSpecRowBean
                                .setIdTipoDoc(getForm().getIdList().getId_tipo_doc().parse());
                        xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.DOC.name());
                    } else if (getForm().getIdList().getId_tipo_unita_doc().parse() != null) {
                        xsdDatiSpecRowBean.setIdTipoUnitaDoc(
                                getForm().getIdList().getId_tipo_unita_doc().parse());
                        xsdDatiSpecRowBean
                                .setTiEntitaSacer(CostantiDB.TipiEntitaSacer.UNI_DOC.name());
                    } else if (getForm().getIdList().getId_tipo_comp_doc().parse() != null) {
                        xsdDatiSpecRowBean.setIdTipoCompDoc(
                                getForm().getIdList().getId_tipo_comp_doc().parse());

                        DecTipoCompDocRowBean tipoCompDocRowBean = tipoStrutDocEjb
                                .getDecTipoCompDocRowBean(
                                        getForm().getIdList().getId_tipo_comp_doc().parse());
                        if (tipoCompDocRowBean.getTiUsoCompDoc().equals("CONTENUTO")) {
                            xsdDatiSpecRowBean
                                    .setTiEntitaSacer(CostantiDB.TipiEntitaSacer.COMP.name());
                        } else {
                            xsdDatiSpecRowBean
                                    .setTiEntitaSacer(CostantiDB.TipiEntitaSacer.SUB_COMP.name());
                        }
                    }
                }

                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getXsdDatiSpec().getStatus().equals(Status.insert)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                    getForm().getAttribDatiSpec().setStatus(Status.insert);

                    datiSpecificiEjb.insNewXsdDatiSpec(param, cdVersione, file, xsdDatiSpecRowBean);
                    getMessageBox().addMessage(
                            new Message(MessageLevel.INF, "File Xsd salvato con successo"));
                } else if (getForm().getXsdDatiSpec().getStatus().equals(Status.update)) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    BigDecimal idXsdDatiSpec = ((DecXsdDatiSpecRowBean) getForm()
                            .getXsdDatiSpecList().getTable().getCurrentRow()).getIdXsdDatiSpec();

                    getForm().getAttribDatiSpec().setStatus(Status.update);
                    xsdDatiSpecRowBean.setIdXsdDatiSpec(idXsdDatiSpec);
                    datiSpecificiEjb.updateXsdDatiSpec(param, file, idXsdDatiSpec,
                            xsdDatiSpecRowBean);

                    getMessageBox().addMessage(
                            new Message(MessageLevel.INF, "File Xsd modificato con successo"));
                }

                goBack();
                getMessageBox().setViewMode(ViewMode.plain);

            }
        } catch (ParerUserError e) {

            getMessageBox().addError(e.getDescription());
            forwardToPublisher(Application.Publisher.XSD_DATI_SPEC_DETAIL);
        }

    }

    /**
     * Metodo che visualizza la form associata a XsdDatiSpec in status update
     *
     * @throws EMFError errore generico
     */
    @Override
    public void updateXsdDatiSpecList() throws EMFError {
        getForm().getXsdDatiSpec().getScaricaXsdButton().setViewMode();

        getForm().getXsdDatiSpec().setViewMode();
        getForm().getXsdDatiSpec().getDs_versione_xsd().setEditMode();
        getForm().getXsdDatiSpec().getDt_soppres().setEditMode();
        getForm().getXsdDatiSpec().getBl_xsd().setEditMode();

        getForm().getXsdDatiSpec().setStatus(Status.update);
        getForm().getXsdDatiSpecList().setStatus(Status.update);
    }

    @Override
    public void updateAttribDatiSpecList() throws EMFError {

        getForm().getXsdDatiSpec().getScaricaXsdButton().setViewMode();

        getForm().getAttribDatiSpec().getDs_attrib_dati_spec().setEditMode();
        getForm().getAttribDatiSpec().setStatus(Status.update);
        getForm().getAttribDatiSpecList().setStatus(Status.update);

    }

    @Override
    public void deleteXsdDatiSpecList() throws EMFError {

        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
                .getXsdDatiSpecList().getTable().getCurrentRow());
        getMessageBox().clear();
        Date dtSoppres = xsdDatiSpecRowBean.getDtSoppres();
        Date today = Calendar.getInstance().getTime();
        if (dtSoppres.compareTo(today) < 0) {
            getMessageBox().addError("Versione XSD gi\u00E0 disattivata in precedenza");
            if (StringUtils.isNotBlank(getLastPublisher())) {
                forwardToPublisher(getLastPublisher());
            } else {
                goBack();
            }
        } else {
            // Il sistema controlla che tale attributo non sia associato a nessun tipo serie,
            // altrimenti da errore
            if (datiSpecificiEjb
                    .isXsdDatiSpecInUseInTipiSerie(xsdDatiSpecRowBean.getIdXsdDatiSpec())) {
                getMessageBox().addError(
                        "Almeno un attributo dell'xsd \u00E8 utilizzato da un tipo serie . L'eliminazione dell'xsd non \u00E8 consentita");
            }
            if (!getMessageBox().hasError()) {
                boolean isInUse = datiSpecificiEjb.isXsdDatiSpecInUse(xsdDatiSpecRowBean);
                boolean isInUseOnCampiRegole = datiSpecificiEjb.isXsdDatiSpecInUseOnCampi(
                        xsdDatiSpecRowBean.getIdXsdDatiSpec(), "DATO_SPEC_UNI_DOC",
                        "DATO_SPEC_DOC_PRINC");
                // se in uso non posso cancellare, ma posso disattivare
                if (isInUse || isInUseOnCampiRegole) {
                    if (StringUtils.isNotBlank(getLastPublisher())) {
                        // Mostra messaggio di disattivazione
                        getRequest().setAttribute("confermaDisattivazioneXsd", true);
                        forwardToPublisher(getLastPublisher());
                    } else {
                        deleteXsd(xsdDatiSpecRowBean);
                    }
                } else {
                    deleteXsd(xsdDatiSpecRowBean);
                }
            } else {
                if (StringUtils.isNotBlank(getLastPublisher())) {
                    forwardToPublisher(getLastPublisher());
                } else {
                    goBack();
                }
            }
        }
    }

    private void deleteXsd(DecXsdDatiSpecRowBean xsdDatiSpecRowBean) throws EMFError {
        // se non in uso e ultimo in lista
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        if (Application.Publisher.TIPO_COMP_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
            StrutTipoStrutForm form = (StrutTipoStrutForm) SpagoliteLogUtil.getForm(this);
            param.setNomeAzione(
                    SpagoliteLogUtil.getDetailActionNameDelete(form, form.getXsdDatiSpecList()));
        } else if (Application.Publisher.TIPO_DOC_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
            StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
            param.setNomeAzione(
                    SpagoliteLogUtil.getDetailActionNameDelete(form, form.getXsdDatiSpecList()));
        } else if (Application.Publisher.TIPO_UNITA_DOC_DETAIL
                .equalsIgnoreCase(param.getNomePagina())) {
            StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
            param.setNomeAzione(
                    SpagoliteLogUtil.getDetailActionNameDelete(form, form.getXsdDatiSpecList()));
        } else if (Application.Publisher.XSD_DATI_SPEC_DETAIL
                .equalsIgnoreCase(param.getNomePagina())) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
        }
        try {
            datiSpecificiEjb.delXsdDatiSpec(param, xsdDatiSpecRowBean);
        } catch (ParerUserError ex) {
            log.error(ex.getMessage(), ex);
            getMessageBox().addError("Errore inatteso nell'eliminazione del xsd");
        }
        if (!getMessageBox().hasError()) {
            DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecificiEjb
                    .getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

            getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            goBack();
        } else {
            if (StringUtils.isNotBlank(getLastPublisher())) {
                forwardToPublisher(getLastPublisher());
            } else {
                goBack();
            }
        }
    }

    public void confermaDisattivazione() throws EMFError {
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
                .getXsdDatiSpecList().getTable().getCurrentRow());
        disattivaXsd(xsdDatiSpecRowBean.getIdXsdDatiSpec());
        if (!getMessageBox().hasError()) {
            DecXsdDatiSpecTableBean xsdDatiSpecTableBean = datiSpecificiEjb
                    .getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

            getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
            getForm().getXsdDatiSpecList().getTable().first();
            getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            goBack();
        } else if (StringUtils.isNotBlank(getLastPublisher())) {
            forwardToPublisher(getLastPublisher());
        } else {
            goBack();
        }
    }

    private void disattivaXsd(BigDecimal idXsdDatiSpec) throws EMFError {
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        datiSpecificiEjb.deactivateXsdAndLog(param, idXsdDatiSpec);
    }

    private void salvaDsAttribDatiSpec() throws EMFError {

        getMessageBox().clear();
        AttribDatiSpec attribDatiSpec = getForm().getAttribDatiSpec();
        attribDatiSpec.post(getRequest());
        BigDecimal idXsdDatiSpec = getForm().getXsdDatiSpec().getId_xsd_dati_spec().parse();

        if (attribDatiSpec.getDs_attrib_dati_spec().parse() == null) {
            getMessageBox().addError("Errore di compilazione form: descrizione non inserito</br>");
        }
        if (getMessageBox().isEmpty()) {

            String dsAttribDatiSpec = attribDatiSpec.getDs_attrib_dati_spec().parse();

            if (getForm().getAttribDatiSpec().getStatus().equals(Status.update)) {
                BigDecimal idAttribDatiSpec = ((DecAttribDatiSpecRowBean) getForm()
                        .getAttribDatiSpecList().getTable().getCurrentRow()).getIdAttribDatiSpec();
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplicByApplic(
                                CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getToolbarUpdate());
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                datiSpecificiEjb.updateDsDecAttribDatiSpec(param, idAttribDatiSpec,
                        dsAttribDatiSpec, idXsdDatiSpec);
                getForm().getAttribDatiSpecList().getTable().setCurrentRowIndex(
                        getForm().getAttribDatiSpecList().getTable().getCurrentRowIndex());
            }
            getForm().getAttribDatiSpec().setViewMode();
            getForm().getAttribDatiSpec().setStatus(Status.view);
            getForm().getAttribDatiSpecList().setStatus(Status.view);
            getMessageBox().setViewMode(ViewMode.plain);
        }
        forwardToPublisher(Application.Publisher.ATTRIB_DATI_SPEC_DETAIL);
    }

    private boolean validazioneXsd(String stringaFile) throws ParerUserError {

        ByteArrayInputStream bais = null;

        if (!stringaFile.isEmpty()) {
            bais = new ByteArrayInputStream(stringaFile.getBytes());
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
            // As stated in the documentation, "Feature for Secure Processing (FSP)" is the central
            // mechanism that will
            // help you safeguard XML processing. It instructs XML processors, such as parsers,
            // validators,
            // and transformers, to try and process XML securely, and the FSP can be used as an
            // alternative to
            // dbf.setExpandEntityReferences(false); to allow some safe level of Entity Expansion
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
            NodeList nl = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, "element");

            for (int it = 0; it < nl.getLength(); it++) {
                Node n = nl.item(it);
                NamedNodeMap map = n.getAttributes();

                if ((map.getNamedItem("name").getNodeValue().equals("DatiSpecifici"))) {

                    if (getForm().getIdList().getNm_sys_migraz().getValue() != null) {
                        return false;
                    }
                    isValidType = true;

                }
                if ((map.getNamedItem("name").getNodeValue().equals("DatiSpecificiMigrazione"))) {
                    if (getForm().getIdList().getNm_sys_migraz().getValue() == null) {
                        return false;
                    }

                    isValidType = true;

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

}
