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

import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.renews.News;
import it.eng.integriam.client.ws.renews.RestituzioneNewsApplicazione;
import it.eng.integriam.client.ws.renews.RestituzioneNewsApplicazioneRisposta;
import it.eng.parer.disciplinare.DisciplinareTecnicoEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.HomeAbstractAction;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import it.eng.spagoLite.security.auth.PwdUtil;
import org.apache.commons.codec.binary.Base64;
import javax.xml.ws.BindingProvider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.renews.News;
import it.eng.integriam.client.ws.renews.RestituzioneNewsApplicazione;
import it.eng.integriam.client.ws.renews.RestituzioneNewsApplicazioneRisposta;
import it.eng.parer.disciplinare.DisciplinareTecnicoEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.HomeAbstractAction;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import org.apache.commons.codec.binary.Base64;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import it.eng.spagoLite.security.auth.PwdUtil;
import it.eng.util.EncryptionUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.xml.ws.BindingProvider;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class HomeAction extends HomeAbstractAction {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(HomeAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/DisciplinareTecnicoEjb")
    private DisciplinareTecnicoEjb disciplinareTecnicoEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    public void process() throws EMFError {
        try {
            // Ricerca news
            findNews();
            // Calcola totali contenuto Sacer
            calcolaTotali();
            if (getUser().getScadenzaPwd() != null) {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(getUser().getScadenzaPwd());
                int numGiorni = Integer.parseInt(
                        configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_GIORNI_ESPONI_SCAD_PSW));
                cal.add(Calendar.DATE, -numGiorni);

                if (cal.getTime().before(now) && getUser().getScadenzaPwd().after(now)) {
                    long from = now.getTime();
                    long to = getUser().getScadenzaPwd().getTime();
                    long millisecondiFraDueDate = to - from;
                    // 1 giorno medio = 1000*60*60*24 ms = 86400000 ms
                    double diffGiorni = Math.round(millisecondiFraDueDate / 86400000.0);
                    getMessageBox().addError("Attenzione: la password scadr\u00E0 tra " + (int) diffGiorni
                            + " giorni. Si prega di modificarla al pi\u00F9 presto");
                }

            }
            getForm().getContenutoSacerTotaliUdDocComp().getScaricaDisciplinareButton().setEditMode();
            getForm().getContenutoSacerTotaliUdDocComp().getScaricaDisciplinareButton().setDisableHourGlass(true);
        } catch (Exception e) {
            logger.error("Errore nella caricamento della Home Page", e);
        }
        forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public String getControllerName() {
        return Application.Actions.HOME;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.HOME;
    }

    @Override
    public void loadDettaglio() throws EMFError {
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    public void changePwd() throws EMFError, IOException {
        this.freeze();
        StringBuilder sb = new StringBuilder();
        sb.append(getRequest().getScheme());
        sb.append("://");
        sb.append(getRequest().getServerName());
        sb.append(":");
        sb.append(getRequest().getServerPort());
        sb.append(getRequest().getContextPath());
        String retURL = sb.toString();
        String salt = Base64.encodeBase64URLSafeString(PwdUtil.generateSalt());
        String hmac = EncryptionUtil.getHMAC(retURL + ":" + salt);
        this.getResponse()
                .sendRedirect(configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_MODIFICA_PASSWORD)
                        + "?r=" + retURL + "&h=" + hmac + "&s=" + salt);

    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
    }

    private void findNews() {

        ArrayList<Map<String, String>> list = new ArrayList<>();
        String url = configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_RECUP_NEWS);
        String psw = configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.PSW_RECUP_INFO);
        String user = configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERID_RECUP_INFO);
        String timeoutString = configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.TIMEOUT_RECUP_NEWS);

        RestituzioneNewsApplicazione client = IAMSoapClients.restituzioneNewsApplicazioneClient(user, psw, url);

        // imposto il valore di timeout. vedi MEV #23814
        if (timeoutString != null && timeoutString.matches("^[0-9]+$")) {
            int timeoutRecuperoNews = Integer.parseInt(timeoutString);
            IAMSoapClients.changeRequestTimeout((BindingProvider) client, timeoutRecuperoNews);
        } else {
            logger.warn("Il valore personalizzato \"" + timeoutString
                    + "\" per il parametro TIMEOUT_RECUP_NEWS non è corretto. Utilizzo il valore predefinito");
        }

        RestituzioneNewsApplicazioneRisposta resp = client.restituzioneNewsApplicazione(
                configHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));

        String newline = System.getProperty("line.separator");
        if (resp.getListaNews() != null) {
            for (News row : resp.getListaNews().getNews()) {
                Map<String, String> news = new HashMap<>();
                String line = "";
                if (row.getDlTesto() != null) {
                    line = row.getDlTesto().replaceAll(newline, "<br />");
                }
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
                String dateFormatted = fmt.format(row.getDtIniPubblic().toGregorianCalendar().getTime());
                news.put("dsOggetto", "<font size=\"1\">" + dateFormatted + "</font></br><b><font size=\"2\"> "
                        + row.getDsOggetto() + "</font></b>");
                news.put("dlTesto", line);
                news.put("dtIniPubblic", dateFormatted);

                list.add(news);
            }
        }
        getRequest().setAttribute("news", list);

    }

    @Override
    public void mostraInformativa() throws EMFError {
        forwardToPublisher("/login/informativa");
    }

    private void calcolaTotali() throws EMFError {
        // Ricavo i filtri necessari per ottenere i risultati
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
        BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
        BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);
        // Inizializzo le date
        Calendar da = Calendar.getInstance();
        Calendar a = Calendar.getInstance();
        da.set(2011, 11, 1, 00, 00);
        a.add(Calendar.DATE, -1);
        a.set(Calendar.HOUR_OF_DAY, 23);
        a.set(Calendar.MINUTE, 59);

        /* Calcolo i totali */
        BaseRowInterface totali = monitoraggioHelper.getTotaliUdDocCompForHome(idAmbiente, idEnte, idStrut,
                da.getTime(), a.getTime(), getUser().getIdUtente());
        getForm().getContenutoSacerTotaliUdDocComp().copyFromBean(totali);
        Map<String, String> organizzazione = getUser().getOrganizzazioneMap();

        // MEV #18740 - calcolo la data effettiva di aggiornamento del contenuto dell'archivio
        // data dal giorno precedente a quello in cui il job calcolo contenuto sacer ha girato senza errori
        Calendar ieri = monitoraggioHelper.getLastPositiveRunCalcoloContenutoSacer();

        // Calendar ieri = Calendar.getInstance();
        ieri.add(Calendar.DATE, -1);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String ieriString = format.format(ieri.getTime());
        String titolo = " " + organizzazione.get("ENTE") + " - " + organizzazione.get("STRUTTURA") + " al "
                + ieriString;
        getSession().setAttribute("TitoloHomeDinamico",
                getForm().getContenutoSacerTotaliUdDocComp().getTitolo().getDescription() + titolo);
        /* Ricavo la lista risultato */
        BaseTableInterface totSacer = monitoraggioHelper.getMonTotSacerForHomeTable(idAmbiente, idEnte, idStrut,
                da.getTime(), a.getTime(), getUser().getIdUtente());
        getForm().getContenutoSacerList().setTable(totSacer);
        getForm().getContenutoSacerList().getTable().first();
        getForm().getContenutoSacerList().getTable().setPageSize(10);
    }

    @Override
    public void scaricaDisciplinareButton() throws EMFError {
        File tmpFile = null;
        FileOutputStream out = null;
        try {
            long idStrut = getUser().getIdOrganizzazioneFoglia().longValueExact();
            Date dat = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
            String nomeFile = "disciplinare_" + getUser().getOrganizzazioneMap().get("STRUTTURA") + "_"
                    + sdf.format(dat) + ".pdf";
            // Ottiene l'idStrut della struttura selezionata
            byte[] pdf = disciplinareTecnicoEjb.generaDisciplinareTecnicoPDF(idStrut);

            tmpFile = new File(System.getProperty("java.io.tmpdir"), nomeFile);
            out = new FileOutputStream(tmpFile);
            IOUtils.write(pdf, out);
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(),
                    WebConstants.MIME_TYPE_PDF);
        } catch (ParerUserError ex) {
            logger.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(ex.getDescription());
        } catch (Exception ex) {
            logger.error("Errore in download " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso nella preparazione del download<br/>");
        } finally {
            IOUtils.closeQuietly(out);
        }

        if (getMessageBox().hasError() || getMessageBox().hasWarning()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    public void download() throws EMFError, IOException {
        logger.debug(">>>DOWNLOAD");
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
                 * Definiamo l'output previsto che sarà  un file in formato zip di cui si occuperà  la servlet per fare
                 * il download
                 */
                getResponse()
                        .setContentType(StringUtils.isBlank(contentType) ? WebConstants.MIME_TYPE_PDF : contentType);
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                try (OutputStream outUD = getServletOutputStream();
                        FileInputStream inputStream = new FileInputStream(fileToDownload);) {

                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
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
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile.booleanValue()) {
                    Files.delete(fileToDownload.toPath());
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
