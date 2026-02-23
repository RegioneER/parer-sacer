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

package it.eng.parer.restWS;

import static it.eng.spagoCore.ConfigProperties.StandardProperty.WS_INSTANCE_NAME;
import static it.eng.spagoCore.ConfigProperties.StandardProperty.WS_STAGING_UPLOAD_DIR;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.restWS.util.RequestPrsr;
import it.eng.parer.restWS.util.Response405;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recuperoFasc.dto.RecuperoFascExt;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.dto.WSDescRecAipFasc;
import it.eng.parer.ws.recuperoFasc.ejb.RecuperoFascSync;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.SyncFakeSessn;
import it.eng.parer.ws.xml.versRespStatoFasc.StatoConservazioneFasc;
import it.eng.spagoCore.ConfigSingleton;
import it.eng.spagoLite.security.KeycloakAuthorizationServlet;

/**
 * Servlet per il recupero dello stato di conservazione di un fascicolo
 *
 * @author Piccioli_G
 */
@WebServlet(urlPatterns = {
        "/RecStatoConservFascicoloSync" }, asyncSupported = true)
public class RecStatoConservFascicoloSrvlt extends KeycloakAuthorizationServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RecStatoConservFascicoloSrvlt.class);

    private String uploadDir;
    private String instanceName;

    @EJB(mappedName = "java:app/Parer-ejb/RecuperoFascSync")
    private RecuperoFascSync recuperoFascSync;

    @EJB(mappedName = "java:app/Parer-ejb/XmlContextCache")
    private XmlContextCache xmlContextCache;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // custom
        uploadDir = ConfigSingleton.getInstance().getStringValue(WS_STAGING_UPLOAD_DIR.name());
        instanceName = ConfigSingleton.getInstance().getStringValue(WS_INSTANCE_NAME.name());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Response405.fancy405(resp, Response405.NomeWebServiceRest.REC_STATO_CONSERV_FASCICOLO_SYNC);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        RispostaWSRecuperoFasc rispostaWsFasc;
        RecuperoFascExt myRecuperoFascExt;
        StatoConservazioneFasc myEsito;
        SyncFakeSessn sessioneFinta = new SyncFakeSessn();
        Iterator<FileItem> tmpIterator = null;
        DiskFileItem tmpFileItem = null;
        List<FileItem> fileItems = null;
        AvanzamentoWs tmpAvanzamento;
        RequestPrsr myRequestPrsr = new RequestPrsr();
        RequestPrsr.ReqPrsrConfig tmpPrsrConfig = new RequestPrsr().new ReqPrsrConfig();

        rispostaWsFasc = new RispostaWSRecuperoFasc();
        myRecuperoFascExt = new RecuperoFascExt();
        myRecuperoFascExt.setDescrizione(new WSDescRecAipFasc());

        tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS(instanceName,
                AvanzamentoWs.Funzioni.RecStatoConservFascicoloSync);
        tmpAvanzamento.logAvanzamento();

        recuperoFascSync.initRispostaWs(rispostaWsFasc, tmpAvanzamento, myRecuperoFascExt);
        myEsito = rispostaWsFasc.getIstanzaEsito();

        sessioneFinta.setTmApertura(new Date());
        //
        sessioneFinta.setIpChiamante(myRequestPrsr.leggiIpVersante(request));
        // MEV#33897 - Eliminazione controllo LOGINNAME/PASSWORD nella chiamata ai servizi di
        // recupero con certificato
        sessioneFinta.setCertCommonName(myRequestPrsr.leggiCertCommonName(request));

        myRecuperoFascExt.getParametriFascParser().setLeggiAncheFascAnnullati(true);

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
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
                    // MEV#33897 - Eliminazione controllo LOGINNAME/PASSWORD nella chiamata ai
                    // servizi di recupero con certificato
                    fileItems = myRequestPrsr.parse(rispostaWsFasc, tmpPrsrConfig, null,
                            sessioneFinta.getCertCommonName() == null ? false : true);

                    //
                    if (rispostaWsFasc.getSeverity() != SeverityEnum.OK) {
                        rispostaWsFasc.setEsitoWsError(rispostaWsFasc.getErrorCode(),
                                rispostaWsFasc.getErrorMessage());
                    }

                    tmpAvanzamento
                            .setCheckPoint(AvanzamentoWs.CheckPoints.VerificaStrutturaChiamataWs)
                            .setFase("completata").logAvanzamento();

                    /*
                     * *****************************************************************************
                     * *** fine della verifica della struttura/signature del web service. Verifica
                     * dei dati effettivamente versati
                     * *****************************************************************************
                     * ***
                     */
                    // testa se la versione è corretta
                    if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaSemantica)
                                .setFase("verifica versione").logAvanzamento();
                        recuperoFascSync.verificaVersione(sessioneFinta.getVersioneWS(),
                                rispostaWsFasc, myRecuperoFascExt);
                    }

                    boolean hasAuthHeader = hasAuthorizationHeader(request);
                    // testa le credenziali utente, tramite ejb
                    myEsito = rispostaWsFasc.getIstanzaEsito();
                    if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setFase("verifica credenziali").logAvanzamento();
                        recuperoFascSync.verificaCredenziali(sessioneFinta.getLoginName(),
                                sessioneFinta.getPassword(), sessioneFinta.getIpChiamante(),
                                rispostaWsFasc, myRecuperoFascExt,
                                sessioneFinta.getCertCommonName(), hasAuthHeader);
                    }

                    // verifica formale e semantica dell'XML di richiesta
                    myEsito = rispostaWsFasc.getIstanzaEsito();
                    if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setFase("verifica xml").logAvanzamento();

                        myRecuperoFascExt.getParametriRecuperoFasc().setTipoEntitaSacer(
                                CostantiDB.TipiEntitaRecupero.FASC_STATO_CONSERV);
                        recuperoFascSync.parseXML(sessioneFinta.getDatiIndiceSipXml(),
                                rispostaWsFasc, myRecuperoFascExt);
                    }

                    // prepara risposta con stato conservazione
                    myEsito = rispostaWsFasc.getIstanzaEsito();
                    if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                        tmpAvanzamento.setFase("generazione xml stato conservazione")
                                .logAvanzamento();
                        recuperoFascSync.recuperaStatoConservazioneFASC(rispostaWsFasc,
                                myRecuperoFascExt);
                    }

                    myEsito = rispostaWsFasc.getIstanzaEsito();
                } catch (FileUploadException e1) {
                    rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                    rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Eccezione generica nella servlet recupero stato conservazione fascicolo "
                                    + e1.getMessage());
                    log.error("Eccezione nella servlet recupero stato conservazione fascicolo ",
                            e1);
                } catch (Exception e1) {
                    rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                    rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Eccezione generica nella servlet recupero stato conservazione fascicolo "
                                    + e1.getMessage());
                    log.error(
                            "Eccezione generica nella servlet recupero stato conservazione fascicolo ",
                            e1);
                } finally {
                    if (fileItems != null) {
                        // elimina i file temporanei
                        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.Pulizia).setFase("")
                                .logAvanzamento();
                        tmpIterator = fileItems.iterator();
                        while (tmpIterator.hasNext()) {
                            tmpFileItem = (DiskFileItem) tmpIterator.next();
                            tmpFileItem.delete();
                        }
                    }
                }
            } else {
                rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.WS_CHECK,
                        "La chiamata non è multipart/formdata ");
                log.error(
                        "Errore nella servlet recupero stato conservazione fascicolo: la chiamata non è multipart/formdata ");
            }
        }

        // rispondi con XML dello stato conservazione
        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.InvioRisposta).setFase("")
                .logAvanzamento();

        response.reset();
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/xml; charset=\"utf-8\"");

        try (ServletOutputStream out = response.getOutputStream();
                OutputStreamWriter tmpStreamWriter = new OutputStreamWriter(out,
                        StandardCharsets.UTF_8);) {

            Marshaller marshaller = xmlContextCache.getVersRespStatoFascCtx_StatoConservazioneFasc()
                    .createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(myEsito, tmpStreamWriter);
        } catch (JAXBException | IOException e) {
            log.error("Eccezione nella servlet recupero stato conservazione fascicolo", e);
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
        return "Servlet per il recupero dello stato di conservazione di un fascicolo";
    }
}