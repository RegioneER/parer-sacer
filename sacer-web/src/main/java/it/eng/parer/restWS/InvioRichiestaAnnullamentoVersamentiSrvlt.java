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

package it.eng.parer.restWS;

import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.WS_INSTANCE_NAME;
import static it.eng.spagoCore.configuration.ConfigProperties.StandardProperty.WS_STAGING_UPLOAD_DIR;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.restWS.util.RequestPrsr;
import it.eng.parer.restWS.util.Response405;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.InvioRichiestaAnnullamentoVersamentiExt;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.RispostaWSInvioRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.WSDescRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.ejb.InvioRichiestaAnnullamentoVersamentiEjb;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.SyncFakeSessn;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.spagoCore.configuration.ConfigSingleton;
import it.eng.spagoCore.util.Oauth2Srvlt;

/**
 *
 * @author gilioli_p
 */
public class InvioRichiestaAnnullamentoVersamentiSrvlt extends Oauth2Srvlt {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(InvioRichiestaAnnullamentoVersamentiSrvlt.class);
    private String uploadDir;
    private String instanceName;
    // Percorso del file XSD di risposta

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // custom
        uploadDir = ConfigSingleton.getInstance().getStringValue(WS_STAGING_UPLOAD_DIR.name());
        instanceName = ConfigSingleton.getInstance().getStringValue(WS_INSTANCE_NAME.name());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Response405.fancy405(resp, Response405.NomeWebServiceRest.INVIO_RICHIESTA_ANNULLAMENTO_VERSAMENTI);
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
        InvioRichiestaAnnullamentoVersamentiEjb invioRichiestaAnnullamentoVersamentiEjb;
        XmlContextCache xmlContextCache;
        RispostaWSInvioRichiestaAnnullamentoVersamenti rispostaWs;
        InvioRichiestaAnnullamentoVersamentiExt ravExt;
        EsitoRichiestaAnnullamentoVersamenti myEsito;
        SyncFakeSessn sessioneFinta = new SyncFakeSessn();
        Iterator tmpIterator = null;
        DiskFileItem tmpFileItem = null;
        List fileItems = null;
        AvanzamentoWs tmpAvanzamento;
        RequestPrsr myRequestPrsr = new RequestPrsr();
        RequestPrsr.ReqPrsrConfig tmpPrsrConfig = new RequestPrsr().new ReqPrsrConfig();

        rispostaWs = new RispostaWSInvioRichiestaAnnullamentoVersamenti();
        ravExt = new InvioRichiestaAnnullamentoVersamentiExt();
        ravExt.setDescrizione(new WSDescRichiestaAnnullamentoVersamenti());

        tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS(instanceName, AvanzamentoWs.Funzioni.Annullamento);
        tmpAvanzamento.logAvanzamento();

        // Recupera l'ejb, se possibile - altrimenti segnala errore
        try {
            invioRichiestaAnnullamentoVersamentiEjb = (InvioRichiestaAnnullamentoVersamentiEjb) new InitialContext()
                    .lookup("java:app/Parer-ejb/InvioRichiestaAnnullamentoVersamentiEjb");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB di Richiesta Annullamento Versamenti", ex);
            throw new ServletException("Impossibile recuperare l'ejb di Richiesta Annullamento Versamenti", ex);
        }

        try {
            xmlContextCache = (XmlContextCache) new InitialContext().lookup("java:app/Parer-ejb/XmlContextCache");
        } catch (NamingException ex) {
            log.error("Errore nel recupero dell'EJB XmlContextCache ", ex);
            throw new ServletException("Impossibile recuperare l'ejb XmlContextCache ", ex);
        }

        tmpAvanzamento.setFase("EJB recuperato").logAvanzamento();
        Date now = Calendar.getInstance().getTime();
        //////////////////////
        // INIT RISPOSTA WS //
        //////////////////////
        invioRichiestaAnnullamentoVersamentiEjb.initRispostaWs(rispostaWs, ravExt, now);
        myEsito = rispostaWs.getEsitoRichiestaAnnullamentoVersamenti();

        // Configura il parser del WS - è un parser generico
        ravExt.getParametriParser().setLeggiAncheUdAnnullate(false);
        //
        sessioneFinta.setTmApertura(now);
        // Recupero l'ip del chiamante
        HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(request);
        sessioneFinta.setIpChiamante(myRequestPrsr.leggiIpVersante(wrapper));
        // log.info("Request, indirizzo IP di provenienza: " + sessioneFinta.getIpChiamante());

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            // Verifico che la richiesta sia multipart/formdata
            boolean isMultipart = ServletFileUpload.isMultipartContent(wrapper);
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
                    tmpPrsrConfig.setRequest(wrapper);
                    tmpPrsrConfig.setUploadHandler(upload);
                    /*
                     * ///////////////////////// VERIFICA SIGNATURE WS // ////////////////////////
                     */

                    if (isOauth2Request(wrapper)) {
                        super.doPost(request, response);
                        fileItems = myRequestPrsr.parse(rispostaWs, tmpPrsrConfig, super.session.getToken());
                    } else {
                        fileItems = myRequestPrsr.parse(rispostaWs, tmpPrsrConfig);
                    }

                    //
                    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.OK) {
                        rispostaWs.setEsitoWsError(rispostaWs.getErrorCode(), rispostaWs.getErrorMessage());
                    }
                    tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaStrutturaChiamataWs)
                            .setFase("completata").logAvanzamento();

                    /*
                     * ***************************************************************************** *** fine della
                     * verifica della struttura/signature del web service. Verifica dei dati effettivamente versati
                     * ***************************************************************************** ***
                     */

                    /* CONTROLLI UTENTE (RICH_ANN_VERS_001) */
                    if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                        tmpAvanzamento.setFase("verifica credenziali").logAvanzamento();
                        invioRichiestaAnnullamentoVersamentiEjb.verificaCredenziali(sessioneFinta.getLoginName(),
                                sessioneFinta.getPassword(), sessioneFinta.getIpChiamante(), rispostaWs, ravExt);
                    }

                    /* CONTROLLI VERSIONE XSD (RICH_ANN_VERS_003) */
                    if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaSemantica)
                                .setFase("verifica versione").logAvanzamento();
                        invioRichiestaAnnullamentoVersamentiEjb.verificaVersione(sessioneFinta.getVersioneWS(),
                                rispostaWs, ravExt);
                    }

                    /* CONTROLLI COERENZA CON XML SCHEMA E CONTROLLI SUCCESSIVI */
                    if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                        tmpAvanzamento.setFase("verifica xml").logAvanzamento();
                        invioRichiestaAnnullamentoVersamentiEjb.parseXML(sessioneFinta.getDatiIndiceSipXml(),
                                rispostaWs, ravExt);
                    }

                    /* PREPARA LA RISPOSTA */
                    if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                        tmpAvanzamento.setFase("generazione xml").logAvanzamento();
                        invioRichiestaAnnullamentoVersamentiEjb.esaminaRichiesteAnnullamentoVersamento(rispostaWs,
                                ravExt);
                    }

                    myEsito = rispostaWs.getEsitoRichiestaAnnullamentoVersamenti();
                } catch (FileUploadException e1) {
                    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Eccezione generica nella servlet recupero sync " + e1.getMessage());
                    log.error("Eccezione nella servlet recupero sync", e1);
                } catch (Exception e1) {
                    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Eccezione generica nella servlet recupero sync " + e1.getMessage());
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
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.WS_CHECK, "La chiamata non è multipart/formdata ");
                log.error("Errore nella servlet recupero sync: la chiamata non è multipart/formdata ");
            }
        }

        // rispondi
        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.InvioRisposta).setFase("").logAvanzamento();
        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/xml; charset=\"utf-8\"");
        ServletOutputStream out = response.getOutputStream();
        OutputStreamWriter tmpStreamWriter = new OutputStreamWriter(out, "UTF-8");

        try {
            Marshaller marshaller = xmlContextCache.getEsitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti()
                    .createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(myEsito, tmpStreamWriter);
        } catch (MarshalException e) {
            log.error("Eccezione nella servlet richieste annullamento ud ", e);
        } catch (ValidationException e) {
            log.error("Eccezione nella servlet richieste annullamento ud", e);
        } catch (Exception e) {
            log.error("Eccezione nella servlet richieste annullamento ud", e);
        } finally {
            try {
                tmpStreamWriter.flush();
                tmpStreamWriter.close();
            } catch (Exception ei) {
                log.error("Eccezione nella servlet richieste annullamento ud", ei);
            }
            try {
                out.flush();
                out.close();
            } catch (Exception ei) {
                log.error("Eccezione nella servlet richieste annullamento ud", ei);
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servelet di prova per testare il WS di invio richiesta annullamento versamenti";
    }

}
