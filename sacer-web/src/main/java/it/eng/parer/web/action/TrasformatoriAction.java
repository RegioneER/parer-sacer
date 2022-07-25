package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb.TipoRappresentazioneEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.TrasformatoriAbstractAction;
import it.eng.parer.slite.gen.form.TrasformatoriForm;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecImageTrasformRowBean;
import it.eng.parer.slite.gen.tablebean.DecImageTrasformTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompRowBean;
import it.eng.parer.slite.gen.tablebean.DecTrasformTipoRapprRowBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.recuperoDip.ejb.RecuperoDip;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.parer.ws.xml.versRespStato.ECEsitoExtType;
import it.eng.parer.ws.xml.versRespStato.ECEsitoPosNegType;
import it.eng.parer.ws.xml.versRespStato.EsitoChiamataWSType;
import it.eng.parer.ws.xml.versRespStato.EsitoGenericoType;
import it.eng.spagoCore.error.EMFError;
import static it.eng.spagoLite.actions.form.ListAction.NE_DETTAGLIO_INSERT;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletOutputStream;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Parucci_M
 */
public class TrasformatoriAction extends TrasformatoriAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(TrasformatoriAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoRappresentazioneEjb")
    private TipoRappresentazioneEjb tipoRapprEjb;

    // Pattern per l'inserimento del nome registro conforme al set di caratteri ammessi
    private static final String NOME_REG = "^[A-Za-z0-9_][A-Za-z0-9\\. _-]*$";
    private static final Pattern regPattern = Pattern.compile(NOME_REG);
    RecuperoDip recuperoDip;

    @Override
    public void initOnClick() throws EMFError {

    }

    @Override
    public void loadDettaglio() throws EMFError {
        String lista = getTableName();
        String action = getNavigationEvent();
        if (!action.equals(NE_DETTAGLIO_INSERT)) {
            String cessato = (String) getRequest().getParameter("cessato");
            if (lista.equals(getForm().getTrasformTipoRapprList().getName())
                    && (getForm().getTrasformTipoRapprList().getTable() != null)
                    && (getForm().getTrasformTipoRapprList().getTable().size() > 0)) {

                getForm().getTrasformTipoRappr().setViewMode();
                getForm().getTrasformTipoRappr().setStatus(Status.view);
                getForm().getTrasformTipoRapprList().setStatus(Status.view);

                BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                        .getTable().getCurrentRow()).getIdTrasformTipoRappr();

                DecTrasformTipoRapprRowBean trasformTipoRapprRowBean = tipoRapprEjb
                        .getDecTrasformTipoRapprRowBean(idTrasformTipoRappr);

                getForm().getTrasformTipoRappr().copyFromBean(trasformTipoRapprRowBean);
                reloadImagesList();
                if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                    getForm().getTrasformTipoRapprList().setUserOperations(true, false, false, false);
                }
                if (!action.equals(NE_DETTAGLIO_INSERT)) {
                    // TODO: VERIFICARE
                    if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                        getForm().getTrasformTipoRappr().getTestTrasformatore().setViewMode();
                        getForm().getTrasformTipoRappr().getCaricaFileTrasformatore().setViewMode();
                    } else {
                        getForm().getTrasformTipoRappr().getTestTrasformatore().setEditMode();
                        getForm().getTrasformTipoRappr().getCaricaFileTrasformatore().setEditMode();
                    }
                    // end TODO
                    getForm().getTrasformTipoRappr().getScaricaTrasformatore().setEditMode();
                    String stato = trasformTipoRapprRowBean.getTiStatoFileTrasform();
                    // TODO: VERIFICARE
                    if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                        getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setHidden(true);
                        getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setViewMode();
                    } else if (!CostantiDB.StatoFileTrasform.ERRATO.name().equals(stato)) {
                        getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setHidden(false);
                        getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setEditMode();
                    }
                    // end TODO
                } else {
                    getForm().getTrasformTipoRappr().getTestTrasformatore().setViewMode();
                    getForm().getTrasformTipoRappr().getCaricaFileTrasformatore().setViewMode();
                    getForm().getTrasformTipoRappr().getScaricaTrasformatore().setViewMode();
                    getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setViewMode();
                }
            } else if (lista.equals(getForm().getImageTrasformList().getName())
                    && (getForm().getImageTrasformList().getTable() != null)
                    && (getForm().getImageTrasformList().getTable().size() > 0)) {
                getForm().getImageTrasform();
                getForm().getImageTrasform().setViewMode();
                getForm().getImageTrasform().getNm_completo_image_trasform().setHidden(false);
                getForm().getImageTrasform().getDt_last_mod_image_trasform().setHidden(false);
                getForm().getImageTrasform().getDt_last_scarico_image_trasform().setHidden(false);
                getForm().getImageTrasform().getTi_path_trasform().setHidden(false);
                getForm().getImageTrasform().getCaricaFileImgTrasformatore().setHidden(false);
                getForm().getImageTrasform().getScaricaFileImgTrasformatore().setHidden(false);

                getForm().getImageTrasform().setStatus(Status.view);
                getForm().getImageTrasformList().setStatus(Status.view);

                BigDecimal idImageTrasform = ((DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                        .getCurrentRow()).getIdImageTrasform();

                DecImageTrasformRowBean trasformTipoRapprRowBean = tipoRapprEjb
                        .getDecImageTrasformRowBean(idImageTrasform);

                getForm().getImageTrasform().copyFromBean(trasformTipoRapprRowBean);

                if (!action.equals(NE_DETTAGLIO_INSERT)) {
                    getForm().getImageTrasform().getScaricaFileImgTrasformatore().setEditMode();
                    // TODO: VERIFICARE
                    if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                        getForm().getImageTrasform().getCaricaFileImgTrasformatore().setViewMode();
                    } else {
                        getForm().getImageTrasform().getCaricaFileImgTrasformatore().setEditMode();
                    }
                    // end TODO
                } else {
                    getForm().getImageTrasform().getScaricaFileImgTrasformatore().setViewMode();
                    getForm().getImageTrasform().getCaricaFileImgTrasformatore().setViewMode();
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        String publisher = getLastPublisher();
        String stato = getForm().getTrasformTipoRappr().getStatus() != null
                ? getForm().getTrasformTipoRappr().getStatus().toString() : null;
        if (Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL.equals(publisher) && stato.equals("insert")) {
            goBack();
        } else {
            loadDettaglio();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        TrasformatoriForm trasformatori = getForm();
        TrasformatoriForm.ImageTrasform imageTransform = trasformatori.getImageTrasform();

        imageTransform.setEditMode();
        imageTransform.clear();
        imageTransform.getNm_image_trasform().setEditMode();
        imageTransform.getNm_completo_image_trasform().setViewMode();
        imageTransform.getDt_last_mod_image_trasform().setViewMode();
        imageTransform.getDt_last_scarico_image_trasform().setViewMode();
        imageTransform.getTi_path_trasform().setViewMode();
        imageTransform.getCaricaFileImgTrasformatore().setEditMode();
        imageTransform.getScaricaFileImgTrasformatore().setViewMode();

        imageTransform.getNm_completo_image_trasform().setHidden(true);
        imageTransform.getDt_last_mod_image_trasform().setHidden(true);
        imageTransform.getDt_last_scarico_image_trasform().setHidden(true);
        imageTransform.getTi_path_trasform().setHidden(true);
        imageTransform.getCaricaFileImgTrasformatore().setHidden(true);
        imageTransform.getScaricaFileImgTrasformatore().setHidden(true);

        imageTransform.setStatus(Status.insert);
        trasformatori.getImageTrasformList().setStatus(Status.insert);
        forwardToPublisher(Application.Publisher.IMPORTA_FILE_IMMAGINE);
    }

    @Override
    public void deleteImageTrasformList() throws EMFError {
        String lastPublisher = getLastPublisher();
        boolean isDeleted = false;
        DecImageTrasformRowBean row = ((DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                .getCurrentRow());
        if (row.getDtLastScaricoImageTrasform() != null) {
            getMessageBox().addError(
                    "Eliminazione dell'immagine del trasformatore non possibile, data ultimo scarico valorizzata");
            if (Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL.equals(lastPublisher)) {
                goBack();
            }
        } else {
            /*
             * Codice aggiuntivo per il logging...
             */
            TrasformatoriForm form = (TrasformatoriForm) SpagoliteLogUtil.getForm(this);
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getImageTrasformList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            DecTrasformTipoRapprRowBean bean = (DecTrasformTipoRapprRowBean) form.getTrasformTipoRapprList().getTable()
                    .getCurrentRow();
            BigDecimal idtipoRapprComp = bean.getIdTipoRapprComp();

            tipoRapprEjb.deleteDecImageTrasform(param, row, idtipoRapprComp);
            getMessageBox().addInfo("Immagine del trasformatore eliminata con successo.");
            isDeleted = true;
            goBackTo(getDefaultPublsherName());
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        if (getForm().getTrasformTipoRapprList().getName().equalsIgnoreCase(lista)) {
            forwardToPublisher(getDefaultPublsherName());
        } else if (getForm().getImageTrasformList().getName().equalsIgnoreCase(lista)) {
            forwardToPublisher(Application.Publisher.IMAGE_TRASFORM_DETAIL);
        }
    }

    public void download() throws EMFError {
        DecTrasformTipoRapprRowBean decTrasformTipoRapprRowBean = (DecTrasformTipoRapprRowBean) getForm()
                .getTrasformTipoRapprList().getTable().getCurrentRow();
        String filename = "Test_Trasformatore_" + decTrasformTipoRapprRowBean.getNmTrasform();
        String path = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        String contentType = (String) getSession()
                .getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name());
        Boolean deleteFile = Boolean.parseBoolean(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sarà  un file in formato zip di cui si occuperà  la servlet per fare
                 * il download
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
                    logger.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    IOUtils.closeQuietly(outUD);
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

    @Override
    public void elencoOnClick() throws EMFError {
        String lastPublisher = getLastPublisher();
        if (Application.Publisher.IMAGE_TRASFORM_DETAIL.equalsIgnoreCase(lastPublisher)) {
            goBackTo(getDefaultPublsherName());
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisher) {
        if (Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL.equals(publisher)) {
            try {
                getForm().getTrasformTipoRappr().setViewMode();
                getForm().getTrasformTipoRappr().setStatus(Status.view);
                getForm().getTrasformTipoRapprList().setStatus(Status.view);

                BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                        .getTable().getCurrentRow()).getIdTrasformTipoRappr();

                DecTrasformTipoRapprRowBean trasformTipoRapprRowBean = tipoRapprEjb
                        .getDecTrasformTipoRapprRowBean(idTrasformTipoRappr);

                getForm().getTrasformTipoRappr().copyFromBean(trasformTipoRapprRowBean);
                getForm().getTrasformTipoRappr().getTestTrasformatore().setEditMode();
                getForm().getTrasformTipoRappr().getCaricaFileTrasformatore().setEditMode();
                getForm().getTrasformTipoRappr().getScaricaTrasformatore().setEditMode();
                String stato = trasformTipoRapprRowBean.getTiStatoFileTrasform();
                if (!CostantiDB.StatoFileTrasform.ERRATO.name().equals(stato)) {
                    getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setEditMode();
                }
                reloadImagesList();
            } catch (EMFError ex) {
                logger.error("Errore nel reloadAfterGoBack di TrasformatoriAction per il publisher :" + publisher, ex);
            }

        } else if (Application.Publisher.IMAGE_TRASFORM_DETAIL.equals(publisher)) {
            try {
                getForm().getImageTrasform().setViewMode();
                getForm().getImageTrasform().setStatus(Status.view);
                getForm().getImageTrasformList().setStatus(Status.view);

                BigDecimal idImageTrasform = ((DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                        .getCurrentRow()).getIdImageTrasform();

                DecImageTrasformRowBean imageTrasformTipoRapprRowBean = tipoRapprEjb
                        .getDecImageTrasformRowBean(idImageTrasform);

                getForm().getImageTrasform().copyFromBean(imageTrasformTipoRapprRowBean);
                getForm().getImageTrasform().getCaricaFileImgTrasformatore().setEditMode();
                getForm().getImageTrasform().getScaricaFileImgTrasformatore().setEditMode();

            } catch (EMFError ex) {
                logger.error("Errore nel reloadAfterGoBack di TrasformatoriAction per il publisher :" + publisher, ex);
            }

        }
    }

    private void reloadImagesList() {
        BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                .getCurrentRow()).getIdTrasformTipoRappr();
        DecImageTrasformTableBean tableImgs = tipoRapprEjb.getDecImageTrasformTableBean(idTrasformTipoRappr);
        getForm().getImageTrasformList().setTable(tableImgs);
        getForm().getImageTrasformList().getTable().first();
        getForm().getImageTrasformList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        String cessato = (String) getRequest().getParameter("cessato");
        if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
            getForm().getImageTrasformList().setUserOperations(true, false, false, false);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.TRASFORMATORI;
    }

    @Override
    public void testTrasformatore() throws EMFError {
        DecTrasformTipoRapprRowBean decTrasformTipoRapprRowBean = (DecTrasformTipoRapprRowBean) getForm()
                .getTrasformTipoRapprList().getTable().getCurrentRow();
        try {
            // Viene effettuato un test del trasformatore.
            // Se va a buon fine ritorna uno stream da passare all'utente
            // altrimenti visualizza un messaggio d'errore.
            RispostaWSRecupero rispostaWS = new RispostaWSRecupero();
            RecuperoExt rec = new RecuperoExt();
            StatoConservazione myEsito = new StatoConservazione();
            AvanzamentoWs avanzamento = AvanzamentoWs.nuovoAvanzamentoWS("prova", AvanzamentoWs.Funzioni.RecuperoWeb);
            avanzamento.logAvanzamento();

            rispostaWS.setSeverity(IRispostaWS.SeverityEnum.OK);
            rispostaWS.setErrorCode("");
            rispostaWS.setErrorMessage("");

            // prepara la classe esito e la aggancia alla rispostaWS
            myEsito.setEsitoGenerale(new EsitoGenericoType());
            rispostaWS.setIstanzaEsito(myEsito);

            // aggiunge l'istanza della classe parametri
            rec.setParametriRecupero(new ParametriRecupero());
            // rec.getParametriRecupero().setTipoRichiedente(JobConstants.TipoSessioniRecupEnum.SERVIZIO);
            /*
             * myRecuperoExt.getParametriRecupero().setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP_DIP);
             * myRecuperoExt.getParametriRecupero().setUtente(getUser());
             * myRecuperoExt.getParametriRecupero().setIdUnitaDoc(componenteRB.getIdUnitaDoc().longValue());
             * myRecuperoExt.getParametriRecupero().setIdComponente(idComp.longValue());
             */
            // aggancia alla rispostaWS
            rispostaWS.setAvanzamento(avanzamento);

            XMLGregorianCalendar d = XmlDateUtility.dateToXMLGregorianCalendar(new Date());
            myEsito.setDataRichiestaStato(d);

            myEsito.getEsitoGenerale().setCodiceEsito(ECEsitoExtType.POSITIVO);
            myEsito.getEsitoGenerale().setCodiceErrore("");
            myEsito.getEsitoGenerale().setMessaggioErrore("");

            myEsito.setVersione(null);

            myEsito.setEsitoChiamataWS(new EsitoChiamataWSType());
            myEsito.getEsitoChiamataWS().setCredenzialiOperatore(ECEsitoPosNegType.POSITIVO);
            myEsito.getEsitoChiamataWS().setVersioneWSCorretta(ECEsitoPosNegType.POSITIVO);

            if (recuperoDip == null) {
                recuperoDip = (RecuperoDip) new InitialContext().lookup("java:app/Parer-ejb/RecuperoDip");
            }
            BigDecimal idTrasformTipoRappr = decTrasformTipoRapprRowBean.getIdTrasformTipoRappr();
            recuperoDip.collaudaConvertitore(rispostaWS, idTrasformTipoRappr.longValue(),
                    System.getProperty("java.io.tmpdir"));

            if (getMessageBox().isEmpty()) {
                switch (rispostaWS.getSeverity()) {
                case OK:
                    getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(),
                            rispostaWS.getNomeFile());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                            rispostaWS.getRifFileBinario().getFileSuDisco().getPath());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                            Boolean.toString(true));
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                            rispostaWS.getMimeType());
                    break;
                case WARNING:
                    getMessageBox().addInfo(rispostaWS.getErrorMessage());
                    break;
                case ERROR:
                    getMessageBox().addError(rispostaWS.getErrorMessage());
                    break;
                }
            }
            if (!getMessageBox().isEmpty()) {
                getMessageBox().addWarning("Il test del trasformatore ha ritornato il seguente messaggio : "
                        + rispostaWS.getErrorMessage());
                decTrasformTipoRapprRowBean.setRisultatoTestTrasformatore("Errore");

                forwardToPublisher(getLastPublisher());
            } else {
                decTrasformTipoRapprRowBean.setRisultatoTestTrasformatore("Successo");
                forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
            }

        } catch (NamingException ex) {
            getMessageBox().addError("Il test del trasformatore non \u00e8 andato a buon fine");
            decTrasformTipoRapprRowBean.setRisultatoTestTrasformatore("Errore");

            forwardToPublisher(getLastPublisher());
            logger.error("Eccezione", ex);
        }

    }

    @Override
    public void process() throws EMFError {
        boolean isMultipart = ServletFileUpload.isMultipartContent(getRequest());
        if (isMultipart && (getLastPublisher().equals(Application.Publisher.IMPORTA_FILE_TRASFORMATORE)
                || getLastPublisher().equals(Application.Publisher.IMPORTA_FILE_IMMAGINE))) {
            readUploadForm();
        }
    }

    /**
     * Metodo che legge la form e passa i dati alla validazione
     *
     */
    public void readUploadForm() {

        getMessageBox().clear();

        int sizeMb = WebConstants.FILESIZE * WebConstants.FILESIZE;
        boolean isFromCaricaImmagine = false;

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
            TrasformatoriForm.TrasformTipoRappr trasform = getForm().getTrasformTipoRappr();
            TrasformatoriForm.ImageTrasform imageTransform = getForm().getImageTrasform();
            boolean isImageInsert = Status.insert.equals(imageTransform.getStatus());
            while (iter.hasNext()) {

                FileItem item = (FileItem) iter.next();
                if (!item.isFormField()) {
                    // se è un FormField, è sicuramente il nome file
                    tmpFileItem = (DiskFileItem) item;
                } else {
                    if (item.getFieldName().equals(trasform.getNm_trasform().getName())) {
                        trasform.getNm_trasform().setValue(item.getString());
                    } else if (item.getFieldName().equals(trasform.getCd_versione_trasform().getName())) {
                        trasform.getCd_versione_trasform().setValue(item.getString());
                    } else if (item.getFieldName().equals(imageTransform.getNm_image_trasform().getName())) {
                        imageTransform.getNm_image_trasform().setValue(item.getString());
                    } else if ("file_immagine".equals(item.getFieldName())) {

                        isFromCaricaImmagine = "true".equals(item.getString());

                    }
                    // nell'eventualità  che si voglia permettere di modificare un'altro dei campi rimasti, scommentare
                    // il relativo else if.
                    // else if
                    // (item.getFieldName().equals(getForm().getTrasformTipoRappr().getTi_stato_file_trasform().getName()))
                    // {
                    // getForm().getTrasformTipoRappr().getTi_stato_file_trasform().setValue(item.getString());
                    // } else if
                    // (item.getFieldName().equals(getForm().getTrasformTipoRappr().getDt_ins_trasform().getName())) {
                    // getForm().getTrasformTipoRappr().getDt_ins_trasform().setValue(item.getString());
                    // } else if
                    // (item.getFieldName().equals(getForm().getTrasformTipoRappr().getDt_last_mod_trasform().getName()))
                    // {
                    // getForm().getTrasformTipoRappr().getDt_last_mod_trasform().setValue(item.getString());
                    // }
                }
            }

            // getForm().getTrasformTipoRappr().validate(getMessageBox());
            // controllo esistenza del file
            if (tmpFileItem != null && (StringUtils.isBlank(tmpFileItem.getName()) || tmpFileItem.getSize() == 0)) {
                getMessageBox().addError("Nessun file selezionato");
            }
            if (isFromCaricaImmagine && imageTransform.getNm_image_trasform().parse() == null) {
                getMessageBox().addError(
                        "Errore di compilazione : il nome dell'immagine trasformatore \u00e8 obbligatorio <br/>");
            } else {
                if (isFromCaricaImmagine && Status.insert.equals(getForm().getImageTrasform().getStatus())) {
                    BigDecimal idTrasform = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                            .getTable().getCurrentRow()).getIdTrasformTipoRappr();
                    final String nmImageTrasform = imageTransform.getNm_image_trasform().parse();
                    if (tipoRapprEjb.existDecImageTrasformByName(idTrasform, nmImageTrasform)) {
                        getMessageBox().addError(
                                "Errore di compilazione : Al trasformatore \u00e8 gi\u00e0 stata associata un’immagine avente lo stesso nome della presente <br/>");
                    }
                }
            }

            FileBinario fileBin;
            // conversione in stringa
            String clob = null;
            if (!getMessageBox().hasError()) {
                aggiornaIstanza(tmpFileItem, isFromCaricaImmagine, isImageInsert);

            } else {

                if (isFromCaricaImmagine) {
                    forwardToPublisher(Application.Publisher.IMPORTA_FILE_IMMAGINE);
                } else {
                    forwardToPublisher(Application.Publisher.IMPORTA_FILE_TRASFORMATORE);
                }

            }

        } catch (FileUploadException | EMFError ex) {
            logger.error("Eccezione nell'upload dei file", ex);
            getMessageBox().addError("Eccezione nell'upload dei file", ex);
            if (isFromCaricaImmagine) {
                forwardToPublisher(Application.Publisher.IMPORTA_FILE_IMMAGINE);
            } else {
                forwardToPublisher(Application.Publisher.IMPORTA_FILE_TRASFORMATORE);
            }

        }
    }

    private void aggiornaIstanza(DiskFileItem tmpFileItem, boolean isFromCaricaImmagine, boolean isImageInsert)
            throws EMFError {
        FileBinario fileBin;
        fileBin = getFileBinario(tmpFileItem);
        if (isFromCaricaImmagine) {
            if (isImageInsert) {
                String nomeImmagine = getForm().getImageTrasform().getNm_image_trasform().parse();
                insertImmagine(nomeImmagine, fileBin);
            } else {
                BigDecimal idImageTrasform = ((DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                        .getCurrentRow()).getIdImageTrasform();
                modificaImmagine(idImageTrasform, fileBin);
                getForm().getImageTrasform().setViewMode();
                getForm().getImageTrasform().setStatus(Status.view);
                getForm().getImageTrasformList().setStatus(Status.view);

                DecImageTrasformRowBean trasformTipoRapprRowBean = tipoRapprEjb
                        .getDecImageTrasformRowBean(idImageTrasform);

                getForm().getImageTrasform().copyFromBean(trasformTipoRapprRowBean);

                getForm().getImageTrasform().getScaricaFileImgTrasformatore().setEditMode();
                getForm().getImageTrasform().getCaricaFileImgTrasformatore().setEditMode();
                getMessageBox().addInfo("File dell'immagine caricato con successo.");
                forwardToPublisher(Application.Publisher.IMAGE_TRASFORM_DETAIL);
            }

        } else {
            BigDecimal idTrasformatore = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                    .getCurrentRow()).getIdTrasformTipoRappr();
            modificaTrasformatore(idTrasformatore, fileBin);
            getForm().getTrasformTipoRappr().setViewMode();
            getForm().getTrasformTipoRappr().setStatus(Status.view);
            getForm().getTrasformTipoRapprList().setStatus(Status.view);

            DecTrasformTipoRapprRowBean trasformTipoRapprRowBean = tipoRapprEjb
                    .getDecTrasformTipoRapprRowBean(idTrasformatore);

            getForm().getTrasformTipoRappr().copyFromBean(trasformTipoRapprRowBean);
            reloadImagesList();
            getForm().getTrasformTipoRappr().getTestTrasformatore().setEditMode();
            getForm().getTrasformTipoRappr().getCaricaFileTrasformatore().setEditMode();
            getForm().getTrasformTipoRappr().getScaricaTrasformatore().setEditMode();
            getMessageBox().addInfo("File del trasformatore caricato con successo.");

            forwardToPublisher(Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL);
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
    public void updateTrasformTipoRapprList() throws EMFError {
        BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                .getCurrentRow()).getIdTrasformTipoRappr();
        DecTrasformTipoRapprRowBean riga = tipoRapprEjb.getDecTrasformTipoRapprRowBean(idTrasformTipoRappr);
        String stato = riga.getTiStatoFileTrasform();
        if (CostantiDB.StatoFileTrasform.ERRATO.name().equals(stato)) {
            TrasformatoriForm trasformatoriForm = getForm();
            TrasformatoriForm.TrasformTipoRappr trasform = trasformatoriForm.getTrasformTipoRappr();
            trasform.setEditMode();
            trasform.getTi_stato_file_trasform().setViewMode();
            trasform.getDt_ins_trasform().setViewMode();
            trasform.getDt_last_mod_trasform().setViewMode();
            trasform.getDs_hash_file_trasform().setViewMode();
            trasform.getSt_file_trasform().setViewMode();
            trasform.getSbloccaFileTrasformatore().setViewMode();
            trasform.getSbloccaFileTrasformatore().setHidden(true);
            trasformatoriForm.getTrasformTipoRapprList().setStatus(Status.update);
            trasform.setStatus(Status.update);
        } else {
            getMessageBox()
                    .addWarning("Un trasformatore pu\u00f2 essere modificato solo se il suo stato \u00e8 : ERRATO");

        }
    }

    private void modificaTrasformatore(BigDecimal idTrasformatore, FileBinario fileDelTrasformatore) {

        DecTrasformTipoRapprRowBean row = (DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                .getCurrentRow();
        byte[] blob = null;
        if (fileDelTrasformatore != null && fileDelTrasformatore.isInMemoria()) {
            blob = fileDelTrasformatore.getDati();

        } else if (fileDelTrasformatore != null && !fileDelTrasformatore.isInMemoria()) {
            File fileDaInserire = fileDelTrasformatore.getFileSuDisco();
            blob = new byte[(int) fileDelTrasformatore.getDimensione()];
            try {
                // convert file into array of bytes
                FileInputStream fileInputStream = new FileInputStream(fileDaInserire);
                fileInputStream.read(blob);
                fileInputStream.close();

            } catch (Exception e) {
                logger.error("Errore nel caricamento del file del trasformatore", e);
            }

        }
        if (blob != null) {
            row.setBlFileTrasform(blob);
        }
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        TrasformatoriForm form2 = (TrasformatoriForm) SpagoliteLogUtil.getForm(this);
        // DecTrasformTipoRapprRowBean bean=(DecTrasformTipoRapprRowBean)
        // form2.getTrasformTipoRapprList().getTable().getCurrentRow();
        // BigDecimal idTipoRapprComp=bean.getIdTipoRapprComp();
        if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.IMPORTA_FILE_TRASFORMATORE)) {
            param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(form2, form2.getTrasformTipoRappr(),
                    form2.getTrasformTipoRappr().getCaricaFileTrasformatore().getName()));
        } else {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
        }

        tipoRapprEjb.updateTrasformTipoRappr(param, row);
    }

    @Override
    public void saveDettaglio() throws EMFError {
        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL)) {
            salvaTrasformatore();

        }
        if (publisher.equals(Application.Publisher.IMAGE_TRASFORM_DETAIL)) {
            salvaImmagine();

        }
    }

    private void salvaTrasformatore() {
        MessageBox msgBox = getMessageBox();
        msgBox.clear();
        DecTrasformTipoRapprRowBean decTrasformTipoRapprRowBean = new DecTrasformTipoRapprRowBean();
        BigDecimal idTrasformatore = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                .getCurrentRow()).getIdTrasformTipoRappr();
        // Ci copia dentro anche l'id tipo rappre comp che dovrà essere loggato come foto xml
        decTrasformTipoRapprRowBean.setIdTipoRapprComp(
                ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable().getCurrentRow())
                        .getIdTipoRapprComp());
        TrasformatoriForm.TrasformTipoRappr form = getForm().getTrasformTipoRappr();
        form.post(getRequest());
        if (form.validate(msgBox)) {

            try {
                checkCampiObbligatori(form, msgBox);

                if (msgBox.isEmpty()) {
                    form.copyToBean(decTrasformTipoRapprRowBean);
                    decTrasformTipoRapprRowBean.setIdTrasformTipoRappr(idTrasformatore);
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                            SpagoliteLogUtil.getToolbarUpdate());
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    tipoRapprEjb.updateTrasformTipoRappr(param, decTrasformTipoRapprRowBean);
                    getForm().getTrasformTipoRappr().setViewMode();
                    getForm().getTrasformTipoRappr().setStatus(Status.view);
                    getForm().getTrasformTipoRapprList().setStatus(Status.view);
                    // goBack();
                    getMessageBox().setViewMode(ViewMode.plain);
                    reloadImagesList();
                    msgBox.addInfo("Aggiornamento del trasformatore effettuato con successo");

                }
            } catch (EMFError ex) {
                logger.error("Eccezione nell'aggiornamento del trasformatore", ex);
                getMessageBox().addError("Eccezione nell'aggiornamento del trasformatore", ex);
            }

        }
        forwardToPublisher(Application.Publisher.TRASFORM_TIPO_RAPPR_DETAIL);

    }

    private void salvaImmagine() {
        MessageBox msgBox = getMessageBox();
        msgBox.clear();
        DecImageTrasformRowBean decImageTrasformRowBean = new DecImageTrasformRowBean();
        TrasformatoriForm.ImageTrasform form = getForm().getImageTrasform();
        form.post(getRequest());
        boolean isModifica = form.getStatus().equals(Status.update);
        boolean isInserimento = form.getStatus().equals(Status.insert);
        if (form.validate(msgBox)) {

            try {
                if (form.getNm_image_trasform().parse() == null) {
                    msgBox.addError(
                            "Errore di compilazione : il nome dell'immagine trasformatore \u00e8 obbligatorio <br/>");
                } else {
                    BigDecimal idTrasform = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                            .getTable().getCurrentRow()).getIdTrasformTipoRappr();
                    final String nmImageTrasform = form.getNm_image_trasform().parse();
                    if (tipoRapprEjb.existDecImageTrasformByName(idTrasform, nmImageTrasform)) {
                        msgBox.addError(
                                "Errore di compilazione : Al trasformatore \u00e8 gi\u00e0 stata associata un’immagine avente lo stesso nome della presente <br/>");
                    }
                }
                if (msgBox.isEmpty()) {
                    form.copyToBean(decImageTrasformRowBean);
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    TrasformatoriForm form2 = (TrasformatoriForm) SpagoliteLogUtil.getForm(this);
                    DecTrasformTipoRapprRowBean bean = (DecTrasformTipoRapprRowBean) form2.getTrasformTipoRapprList()
                            .getTable().getCurrentRow();
                    BigDecimal idTipoRapprComp = bean.getIdTipoRapprComp();

                    if (isModifica) {
                        BigDecimal idImageTrasform = ((DecImageTrasformRowBean) getForm().getImageTrasformList()
                                .getTable().getCurrentRow()).getIdImageTrasform();
                        decImageTrasformRowBean.setIdImageTrasform(idImageTrasform);
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        tipoRapprEjb.updateImageTrasform(param, decImageTrasformRowBean, idTipoRapprComp);

                    } else if (isInserimento) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        BigDecimal idTrasform = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                                .getTable().getCurrentRow()).getIdTrasformTipoRappr();

                        decImageTrasformRowBean.setIdTrasformTipoRappr(idTrasform);
                        decImageTrasformRowBean.setBlImageTrasform("   ".getBytes());
                        // DA FINIRE !!!!!!!!
                        tipoRapprEjb.insertImageTrasform(param, decImageTrasformRowBean, idTipoRapprComp);
                        reloadImagesList();

                    }
                    decImageTrasformRowBean = tipoRapprEjb
                            .getDecImageTrasformRowBean(decImageTrasformRowBean.getIdImageTrasform());

                    getForm().getImageTrasform().copyFromBean(decImageTrasformRowBean);
                    getForm().getImageTrasform().setViewMode();
                    getForm().getImageTrasform().setStatus(Status.view);
                    getForm().getImageTrasformList().setStatus(Status.view);
                    getForm().getImageTrasform().getScaricaFileImgTrasformatore().setEditMode();
                    getForm().getImageTrasform().getCaricaFileImgTrasformatore().setEditMode();

                    // goBack();
                    getMessageBox().setViewMode(ViewMode.plain);

                    msgBox.addInfo("Aggiornamento dell'immagine trasformatore effettuato con successo");

                }
            } catch (EMFError ex) {
                logger.error("Eccezione nell'aggiornamento dell'immagine del trasformatore", ex);
                getMessageBox().addError("Eccezione nell'aggiornamento dell'immagine del trasformatore", ex);
            }

        }
        forwardToPublisher(Application.Publisher.IMAGE_TRASFORM_DETAIL);

    }

    private void checkCampiObbligatori(TrasformatoriForm.TrasformTipoRappr form, MessageBox msgBox) throws EMFError {

        if (form.getNm_trasform().parse() == null) {
            msgBox.addError("Errore di compilazione : il nome del trasformatore \u00e8 obbligatorio <br/>");
        }
        if (form.getCd_versione_trasform().parse() == null) {
            msgBox.addError("Errore di compilazione : la versione del trasformatore \u00e8 obbligatoria <br/>");
        }

    }

    @Override
    public void caricaFileTrasformatore() throws EMFError {
        TrasformatoriForm trasformatoriForm = getForm();
        TrasformatoriForm.TrasformTipoRappr trasform = trasformatoriForm.getTrasformTipoRappr();
        BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                .getCurrentRow()).getIdTrasformTipoRappr();
        DecTrasformTipoRapprRowBean riga = tipoRapprEjb.getDecTrasformTipoRapprRowBean(idTrasformTipoRappr);
        String stato = riga.getTiStatoFileTrasform();
        if (CostantiDB.StatoFileTrasform.ERRATO.name().equals(stato)) {

            trasform.setEditMode();
            trasform.getNm_trasform().setViewMode();
            trasform.getCd_versione_trasform().setViewMode();
            trasform.getTi_stato_file_trasform().setViewMode();
            trasform.getDt_ins_trasform().setViewMode();
            trasform.getDt_last_mod_trasform().setViewMode();
            trasform.getDs_hash_file_trasform().setViewMode();
            trasformatoriForm.getTrasformTipoRapprList().setStatus(Status.update);
            trasform.setStatus(Status.update);
            forwardToPublisher(Application.Publisher.IMPORTA_FILE_TRASFORMATORE);
        } else {
            getMessageBox()
                    .addWarning("Un trasformatore pu\u00f2 essere modificato solo se il suo stato \u00e8 : ERRATO");

        }

    }

    @Override
    public void scaricaTrasformatore() throws EMFError {
        BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                .getCurrentRow()).getIdTrasformTipoRappr();
        DecTrasformTipoRapprRowBean decTrasformTipoRappr = tipoRapprEjb
                .getDecTrasformTipoRapprRowBean(idTrasformTipoRappr);

        // DecTrasformTipoRapprRowBean trasformTipoRapprRowBean =
        // struttureEjb.getDecTipoRapprCompRowBean(idTrasformTipoRappr);
        String nomeFile = tipoRapprEjb.getDownLoadNameForTrasformTipoRappr(idTrasformTipoRappr,
                decTrasformTipoRappr.getNmTrasform(), decTrasformTipoRappr.getCdVersioneTrasform());
        BigDecimal idTipoRapprComp = decTrasformTipoRappr.getIdTipoRapprComp();
        DecTipoRapprCompRowBean decTipoRapprCompRowBean = tipoRapprEjb.getDecTipoRapprCompRowBean(idTipoRapprComp,
                null);
        DecFormatoFileDocRowBean decFormatoFileDocRowBean = formatoFileDocEjb
                .getDecFormatoFileDocRowBean(decTipoRapprCompRowBean.getIdFormatoConvertit(), null);
        String formatoConvertitore = decFormatoFileDocRowBean.getNmFormatoFileDoc();
        // nomeFile=nomeFile.concat(".".concat(decTipoRapprCompRowBean.getIdFormatoConvertit()));
        nomeFile = nomeFile.concat(".".concat(formatoConvertitore));
        ServletOutputStream out = getServletOutputStream();
        try {
            byte[] data = new byte[1000];
            InputStream is;

            getResponse().setContentType("application/octet-stream");
            getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + nomeFile);

            byte[] blob = decTrasformTipoRappr.getBlFileTrasform();
            if (blob != null) {
                is = new ByteArrayInputStream(blob);
                int count;
                while ((count = is.read(data, 0, 1000)) != -1) {
                    out.write(data, 0, count);
                }
                is.close();
            }
            out.flush();
            out.close();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(
                    new Message(Message.MessageLevel.ERR, "Errore nel recupero dei file del trasformatore"));
            logger.error(e.getMessage(), e);
        }

    }

    @Override
    public void updateImageTrasformList() throws EMFError {
        TrasformatoriForm trasformatori = getForm();
        TrasformatoriForm.ImageTrasform imageTransform = trasformatori.getImageTrasform();

        imageTransform.setEditMode();
        imageTransform.getNm_completo_image_trasform().setViewMode();
        imageTransform.getDt_last_mod_image_trasform().setViewMode();
        imageTransform.getDt_last_scarico_image_trasform().setViewMode();
        imageTransform.getTi_path_trasform().setViewMode();
        imageTransform.getScaricaFileImgTrasformatore().setViewMode();
        imageTransform.getCaricaFileImgTrasformatore().setViewMode();

        imageTransform.setStatus(Status.update);
        trasformatori.getImageTrasformList().setStatus(Status.update);

    }

    @Override
    public void caricaFileImgTrasformatore() throws EMFError {
        TrasformatoriForm trasformatoriForm = getForm();
        TrasformatoriForm.TrasformTipoRappr trasform = trasformatoriForm.getTrasformTipoRappr();
        TrasformatoriForm.ImageTrasform imgTrasform = trasformatoriForm.getImageTrasform();
        imgTrasform.setEditMode();
        imgTrasform.getNm_image_trasform().setViewMode();
        imgTrasform.getDt_last_mod_image_trasform().setViewMode();
        imgTrasform.getDt_last_scarico_image_trasform().setViewMode();

        imgTrasform.getNm_completo_image_trasform().setViewMode();
        imgTrasform.getTi_path_trasform().setViewMode();
        trasformatoriForm.getImageTrasformList().setStatus(Status.update);
        imgTrasform.setStatus(Status.update);
        forwardToPublisher(Application.Publisher.IMPORTA_FILE_IMMAGINE);
    }

    @Override
    public void scaricaFileImgTrasformatore() throws EMFError {
        BigDecimal idImageTrasform = ((DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                .getCurrentRow()).getIdImageTrasform();
        DecImageTrasformRowBean decImageTrasform = tipoRapprEjb.getDecImageTrasformRowBean(idImageTrasform);
        ServletOutputStream out = getServletOutputStream();
        try {
            byte[] data = new byte[1000];
            InputStream is = null;
            if (decImageTrasform != null) {

                String filename = decImageTrasform.getString("nm_completo_image_trasform");
                getResponse().setContentType("application/octet-stream");
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                byte[] blob = decImageTrasform.getBlImageTrasform();
                if (blob != null) {
                    is = new ByteArrayInputStream(blob);
                    int count;

                    while ((count = is.read(data, 0, 1000)) != -1) {
                        out.write(data, 0, count);
                    }

                    is.close();
                }
            }
            out.flush();
            out.close();
            freeze();
        } catch (Exception e) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR, "Errore nel recupero dei file immagine"));
            logger.error(e.getMessage(), e);
        }
    }

    private void modificaImmagine(BigDecimal idImageTrasform, FileBinario fileBin) {

        DecImageTrasformRowBean row = (DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                .getCurrentRow();
        byte[] blob = null;
        if (fileBin != null && fileBin.isInMemoria()) {
            blob = fileBin.getDati();

        } else if (fileBin != null && !fileBin.isInMemoria()) {
            File fileDaInserire = fileBin.getFileSuDisco();
            blob = new byte[(int) fileBin.getDimensione()];
            try {
                // convert file into array of bytes
                FileInputStream fileInputStream = new FileInputStream(fileDaInserire);
                fileInputStream.read(blob);
                fileInputStream.close();

            } catch (Exception e) {
                logger.error("Errore nel recupero del file dell'immagine del trasformatore", e);
            }

        }
        if (blob != null) {
            row.setBlImageTrasform(blob);
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            TrasformatoriForm form2 = (TrasformatoriForm) SpagoliteLogUtil.getForm(this);
            DecTrasformTipoRapprRowBean bean = (DecTrasformTipoRapprRowBean) form2.getTrasformTipoRapprList().getTable()
                    .getCurrentRow();
            BigDecimal idTipoRapprComp = bean.getIdTipoRapprComp();
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.IMPORTA_FILE_IMMAGINE)) {
                param.setNomeAzione(SpagoliteLogUtil.getButtonActionName(form2, form2.getImageTrasform(),
                        form2.getImageTrasform().getCaricaFileImgTrasformatore().getName()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            }
            tipoRapprEjb.updateImageTrasform(param, row, idTipoRapprComp);
        }
    }

    @Override
    public void sbloccaFileTrasformatore() throws EMFError {
        try {
            DecTrasformTipoRapprRowBean row = (DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                    .getTable().getCurrentRow();
            /*
             * Codice aggiuntivo per il logging...
             */
            TrasformatoriForm form2 = (TrasformatoriForm) SpagoliteLogUtil.getForm(this);
            DecTrasformTipoRapprRowBean bean = (DecTrasformTipoRapprRowBean) form2.getTrasformTipoRapprList().getTable()
                    .getCurrentRow();
            BigDecimal idTipoRapprComp = bean.getIdTipoRapprComp();
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                    SpagoliteLogUtil.getButtonActionName(form2, form2.getTrasformTipoRappr(),
                            form2.getTrasformTipoRappr().getSbloccaFileTrasformatore().getName()));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            tipoRapprEjb.setDecTrasformTipoRapprStatusAtError(param, row, idTipoRapprComp);
            getMessageBox().addInfo("Stato del trasformatore reimpostato per effettuare la modifica del file ");
            getForm().getTrasformTipoRappr().setViewMode();
            getForm().getTrasformTipoRappr().setStatus(Status.view);
            getForm().getTrasformTipoRapprList().setStatus(Status.view);

            BigDecimal idTrasformTipoRappr = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList()
                    .getTable().getCurrentRow()).getIdTrasformTipoRappr();

            DecTrasformTipoRapprRowBean trasformTipoRapprRowBean = tipoRapprEjb
                    .getDecTrasformTipoRapprRowBean(idTrasformTipoRappr);

            getForm().getTrasformTipoRappr().copyFromBean(trasformTipoRapprRowBean);
            reloadImagesList();
            getForm().getTrasformTipoRappr().getTestTrasformatore().setEditMode();
            getForm().getTrasformTipoRappr().getCaricaFileTrasformatore().setEditMode();
            getForm().getTrasformTipoRappr().getScaricaTrasformatore().setEditMode();
            String stato = trasformTipoRapprRowBean.getTiStatoFileTrasform();
            if (!CostantiDB.StatoFileTrasform.ERRATO.name().equals(stato)) {
                getForm().getTrasformTipoRappr().getSbloccaFileTrasformatore().setEditMode();
            }

        } catch (Exception e) {
            logger.error("Errore nella procedura sbloccaFileTrasformatore()", e);
            getMessageBox().addError("Errore nella procedura di sblocco del trasformatore");
        }
    }

    private void insertImmagine(String nomeImmagine, FileBinario fileBin) throws EMFError {
        try {

            DecImageTrasformRowBean row = new DecImageTrasformRowBean();
            TrasformatoriForm.ImageTrasform form = getForm().getImageTrasform();
            row.setNmImageTrasform(form.getNm_image_trasform().parse());
            byte[] blob = null;
            if (fileBin != null && fileBin.isInMemoria()) {
                blob = fileBin.getDati();

            } else if (fileBin != null && !fileBin.isInMemoria()) {
                File fileDaInserire = fileBin.getFileSuDisco();
                blob = new byte[(int) fileBin.getDimensione()];
                // convert file into array of bytes
                FileInputStream fileInputStream = new FileInputStream(fileDaInserire);
                fileInputStream.read(blob);
                fileInputStream.close();

            }
            if (blob != null) {
                row.setBlImageTrasform(blob);
                BigDecimal idTrasform = ((DecTrasformTipoRapprRowBean) getForm().getTrasformTipoRapprList().getTable()
                        .getCurrentRow()).getIdTrasformTipoRappr();
                row.setIdTrasformTipoRappr(idTrasform);
                /*
                 * Codice aggiuntivo per il logging...
                 */
                TrasformatoriForm form2 = (TrasformatoriForm) SpagoliteLogUtil.getForm(this);
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                        SpagoliteLogUtil.getToolbarInsert());
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                DecTrasformTipoRapprRowBean bean = (DecTrasformTipoRapprRowBean) form2.getTrasformTipoRapprList()
                        .getTable().getCurrentRow();
                BigDecimal idTipoRapprComp = bean.getIdTipoRapprComp();
                tipoRapprEjb.insertImageTrasform(param, row, idTipoRapprComp);
                DecImageTrasformTableBean tableImgs = new DecImageTrasformTableBean();
                row = tipoRapprEjb.getDecImageTrasformRowBean(row.getIdImageTrasform());
                tableImgs.add(row);
                getForm().getImageTrasformList().setTable(tableImgs);
                getForm().getImageTrasformList().getTable().first();
                getForm().getImageTrasformList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                getForm().getImageTrasform().setViewMode();
                getForm().getImageTrasform().getNm_completo_image_trasform().setHidden(false);
                getForm().getImageTrasform().getDt_last_mod_image_trasform().setHidden(false);
                getForm().getImageTrasform().getDt_last_scarico_image_trasform().setHidden(false);
                getForm().getImageTrasform().getTi_path_trasform().setHidden(false);
                getForm().getImageTrasform().getCaricaFileImgTrasformatore().setHidden(false);
                getForm().getImageTrasform().getScaricaFileImgTrasformatore().setHidden(false);

                getForm().getImageTrasform().setStatus(Status.view);
                getForm().getImageTrasformList().setStatus(Status.view);

                BigDecimal idImageTrasform = ((DecImageTrasformRowBean) getForm().getImageTrasformList().getTable()
                        .getCurrentRow()).getIdImageTrasform();

                DecImageTrasformRowBean trasformTipoRapprRowBean = tipoRapprEjb
                        .getDecImageTrasformRowBean(idImageTrasform);

                getForm().getImageTrasform().copyFromBean(trasformTipoRapprRowBean);
                getForm().getImageTrasform().getScaricaFileImgTrasformatore().setEditMode();
                getForm().getImageTrasform().getCaricaFileImgTrasformatore().setEditMode();
                // getForm().getTrasformTipoRappr().getScaricaTrasformatore().setViewMode();

            }
            forwardToPublisher(Application.Publisher.IMAGE_TRASFORM_DETAIL);
        } catch (Exception e) {
            logger.error("Errore nel recupero del file dell'immagine del trasformatore", e);
            throw new EMFError(EMFError.BLOCKING, "Errore nel salvataggio dell'immagine del trasformatore ", e);
        }

    }

}
