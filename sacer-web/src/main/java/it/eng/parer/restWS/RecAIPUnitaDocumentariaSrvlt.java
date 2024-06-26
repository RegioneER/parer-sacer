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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.restWS;

import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.WS_INSTANCE_NAME;
import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.WS_STAGING_UPLOAD_DIR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.job.indiceAip.helper.CreazioneIndiceAipHelper;
import it.eng.parer.restWS.util.RequestPrsr;
import it.eng.parer.restWS.util.Response405;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.dto.WSDescRecAipUnitaDoc;
import it.eng.parer.ws.recupero.ejb.RecuperoSync;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.SyncFakeSessn;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.spagoCore.configuration.ConfigSingleton;

/**
 *
 * @author Fioravanti_F
 */
public class RecAIPUnitaDocumentariaSrvlt extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RecAIPUnitaDocumentariaSrvlt.class);
    private static final int BUFFERSIZE = 10 * 1024 * 1024;

    private String uploadDir;
    private String instanceName;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // custom
        uploadDir = ConfigSingleton.getInstance().getStringValue(WS_STAGING_UPLOAD_DIR.name());
        instanceName = ConfigSingleton.getInstance().getStringValue(WS_INSTANCE_NAME.name());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response405.fancy405(resp, Response405.NomeWebServiceRest.REC_AIP_UNITA_DOC_SYNC);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request
     *            servlet request
     * @param response
     *            servlet response
     * 
     * @throws ServletException
     *             if a servlet-specific error occurs
     * @throws IOException
     *             if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // EVO#20972
        CreazioneIndiceAipHelper creazioneIndiceAipHelper;
        // end EVO#20972
        RecuperoSync recuperoSync;
        XmlContextCache xmlContextCache;
        RispostaWSRecupero rispostaWs;
        RecuperoExt myRecuperoExt;
        StatoConservazione myEsito;
        SyncFakeSessn sessioneFinta = new SyncFakeSessn();
        Iterator tmpIterator = null;
        DiskFileItem tmpFileItem = null;
        List fileItems = null;
        AvanzamentoWs tmpAvanzamento;
        RequestPrsr myRequestPrsr = new RequestPrsr();
        RequestPrsr.ReqPrsrConfig tmpPrsrConfig = new RequestPrsr().new ReqPrsrConfig();

        rispostaWs = new RispostaWSRecupero();
        myRecuperoExt = new RecuperoExt();
        myRecuperoExt.setDescrizione(new WSDescRecAipUnitaDoc());

        tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS(instanceName, AvanzamentoWs.Funzioni.Recupero);
        tmpAvanzamento.logAvanzamento();

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            recuperoSync = (RecuperoSync) new InitialContext().lookup("java:app/Parer-ejb/RecuperoSync");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB ", ex);
            throw new ServletException("Impossibile recuperare l'ejb", ex);
        }

        try {
            xmlContextCache = (XmlContextCache) new InitialContext().lookup("java:app/Parer-ejb/XmlContextCache");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB XmlContextCache ", ex);
            throw new ServletException("Impossibile recuperare l'ejb XmlContextCache ", ex);
        }

        // EVO#20972
        try {
            creazioneIndiceAipHelper = (CreazioneIndiceAipHelper) new InitialContext()
                    .lookup("java:app/Parer-ejb/CreazioneIndiceAipHelper");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB CreazioneIndiceAipHelper ", ex);
            throw new ServletException("Impossibile recuperare l'ejb CreazioneIndiceAipHelper ", ex);
        }
        // end EVO#20972

        tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();

        recuperoSync.initRispostaWs(rispostaWs, tmpAvanzamento, myRecuperoExt);
        myEsito = rispostaWs.getIstanzaEsito();

        sessioneFinta.setTmApertura(new Date());
        //
        sessioneFinta.setIpChiamante(myRequestPrsr.leggiIpVersante(request));
        sessioneFinta.setCertCommonName(myRequestPrsr.leggiCertCommonName(request));
        // log.info("Request, indirizzo IP di provenienza: " + sessioneFinta.getIpChiamante());

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            // Check that we have a file upload request
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (isMultipart) {
                // Create a factory for disk-based file items
                DiskFileItemFactory factory = new DiskFileItemFactory();

                // maximum size that will be stored in memory
                factory.setSizeThreshold(1);

                factory.setRepository(new File(uploadDir));

                // Create a new file upload handler
                ServletFileUpload upload = new ServletFileUpload(factory);

                tmpAvanzamento.setFase("Servlet pronta a ricevere i file").logAvanzamento();
                try {

                    tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.TrasferimentoPayloadIn)
                            .setFase("pronto a ricevere").logAvanzamento();

                    //
                    tmpPrsrConfig.setLeggiFile(false);
                    tmpPrsrConfig.setLeggindiceMM(false);
                    tmpPrsrConfig.setAvanzamentoWs(tmpAvanzamento);
                    tmpPrsrConfig.setSessioneFinta(sessioneFinta);
                    tmpPrsrConfig.setRequest(request);
                    tmpPrsrConfig.setUploadHandler(upload);
                    //
                    fileItems = myRequestPrsr.parse(rispostaWs, tmpPrsrConfig);
                    //
                    if (rispostaWs.getSeverity() != SeverityEnum.OK) {
                        rispostaWs.setEsitoWsError(rispostaWs.getErrorCode(), rispostaWs.getErrorMessage());
                    }

                    tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaStrutturaChiamataWs)
                            .setFase("completata").logAvanzamento();

                    /*
                     * ******************************************************************************** fine della
                     * verifica della struttura/signature del web service. Verifica dei dati effettivamente versati
                     * ********************************************************************************
                     */
                    // testa se la versione è corretta
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaSemantica)
                                .setFase("verifica versione").logAvanzamento();
                        recuperoSync.verificaVersione(sessioneFinta.getVersioneWS(), rispostaWs, myRecuperoExt);
                    }

                    // testa le credenziali utente, tramite ejb
                    myEsito = rispostaWs.getIstanzaEsito();
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setFase("verifica credenziali").logAvanzamento();
                        recuperoSync.verificaCredenziali(sessioneFinta.getLoginName(), sessioneFinta.getPassword(),
                                sessioneFinta.getIpChiamante(), rispostaWs, myRecuperoExt,
                                sessioneFinta.getCertCommonName());
                    }

                    // verifica formale e semantica dell'XML di versamento
                    myEsito = rispostaWs.getIstanzaEsito();
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setFase("verifica xml").logAvanzamento();

                        // EVO#20972
                        // In questa fase preliminare in cui si sta effettuando solo il
                        // parsing xml con le verifiche semantiche sui dati,
                        // non serve gestire coerentemente il parametro
                        // del servizio di recupero in base alla versione Unisincro con cui
                        // è stato prodotto l'ultimo Indice AIP (se presente), perchè si avrebbe lo stesso effetto sia
                        // impostando il valore
                        // UNI_DOC_UNISYNCRO che UNI_DOC_UNISYNCRO_V2.
                        myRecuperoExt.getParametriRecupero()
                                .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO); // idem settando
                                                                                                      // UNI_DOC_UNISYNCRO_V2
                        // end EVO#20972
                        recuperoSync.parseXML(sessioneFinta.getDatiIndiceSipXml(), rispostaWs, myRecuperoExt);
                    }

                    // prepara risposta
                    myEsito = rispostaWs.getIstanzaEsito();
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        // EVO#20972
                        // In questa fase in cui si sta richiedendo il
                        // recupero vero e proprio per la generazione del pacchetto AIP,
                        // è necessario gestire coerentemente il parametro
                        // del servizio di recupero in base alla versione Unisincro con cui
                        // è stato prodotto l'ultimo Indice AIP (se presente), perchè il pacchetto AIP viene generato in
                        // modo differente impostando il valore
                        // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X).
                        AroVerIndiceAipUd verIndiceAipUd = creazioneIndiceAipHelper
                                .getUltimaVersioneIndiceAip(myRecuperoExt.getParametriRecupero().getIdUnitaDoc());
                        if (verIndiceAipUd != null) {
                            // Scompatto il campo cdVerIndiceAip
                            String[] numbers = verIndiceAipUd.getCdVerIndiceAip().split("[.]");
                            int majorNumber = Integer.parseInt(numbers[0]);
                            if (majorNumber > 0) {
                                myRecuperoExt.getParametriRecupero()
                                        .setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2);
                            }
                        }
                        // end EVO#20972

                        // tmpAvanzamento.setFase("generazione xml").
                        // logAvanzamento();
                        //
                        recuperoSync.recuperaOggetto(rispostaWs, myRecuperoExt, uploadDir);
                    }

                    myEsito = rispostaWs.getIstanzaEsito();
                } catch (FileUploadException e1) {
                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Eccezione generica nella servlet recupero AIP sync " + e1.getMessage());
                    log.error("Eccezione nella servlet recupero sync", e1);
                } catch (Exception e1) {
                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Eccezione generica nella servlet recupero AIP sync " + e1.getMessage());
                    log.error("Eccezione generica nella servlet recupero sync", e1);
                } finally {
                    if (fileItems != null) {
                        // elimina i file temporanei
                        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.Pulizia).setFase("").logAvanzamento();
                        tmpIterator = fileItems.iterator();
                        while (tmpIterator.hasNext()) {
                            tmpFileItem = (DiskFileItem) tmpIterator.next();
                            tmpFileItem.delete();
                        }
                    }
                }
            } else {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.WS_CHECK, "La chiamata non è multipart/formdata ");
                log.error("Errore nella servlet recupero AIP sync: la chiamata non è multipart/formdata ");
            }
        }

        // rispondi
        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.InvioRisposta).setFase("").logAvanzamento();

        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            String filename = rispostaWs.getNomeFile();
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename);
            ServletOutputStream out = null;
            FileInputStream inputStream = null;
            try {
                response.setHeader("Content-Length",
                        String.valueOf(rispostaWs.getRifFileBinario().getFileSuDisco().length()));
                out = response.getOutputStream();
                inputStream = new FileInputStream(rispostaWs.getRifFileBinario().getFileSuDisco());
                byte[] buffer = new byte[BUFFERSIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            } catch (Exception e) {
                log.error("Eccezione nella servlet recupero AIP sync", e);
            } finally {
                IOUtils.closeQuietly(inputStream);
                IOUtils.closeQuietly(out);
                inputStream = null;
                out = null;
            }
        } else {
            response.setContentType("application/xml; charset=\"utf-8\"");
            ServletOutputStream out = null;
            OutputStreamWriter tmpStreamWriter = null;

            try {
                out = response.getOutputStream();
                tmpStreamWriter = new OutputStreamWriter(out, "UTF-8");

                Marshaller marshaller = xmlContextCache.getVersRespStatoCtx_StatoConservazione().createMarshaller();
                marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(myEsito, tmpStreamWriter);
            } catch (MarshalException e) {
                log.error("Eccezione nella servlet recupero AIP sync", e);
            } catch (ValidationException e) {
                log.error("Eccezione nella servlet recupero AIP sync", e);
            } catch (Exception e) {
                log.error("Eccezione nella servlet recupero AIP sync", e);
            } finally {
                IOUtils.closeQuietly(tmpStreamWriter);
                IOUtils.closeQuietly(out);
                tmpStreamWriter = null;
                out = null;
            }
        }

        // elimina il file zip, in ogni caso
        if (rispostaWs.getRifFileBinario() != null && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            rispostaWs.getRifFileBinario().getFileSuDisco().delete();
        }

        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.Fine).setFase("").logAvanzamento();
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
