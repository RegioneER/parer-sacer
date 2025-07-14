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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
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
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.dto.WSDescRecStatoCons;
import it.eng.parer.ws.recupero.ejb.RecuperoSync;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.SyncFakeSessn;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.spagoCore.ConfigSingleton;

/**
 *
 * @author Fioravanti_F
 */
@WebServlet(urlPatterns = {
	"/RecDIPStatoConservazioneSync" }, asyncSupported = true)
public class RecStatoConservSrvlt extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RecStatoConservSrvlt.class);
    private String uploadDir;
    private String instanceName;

    @EJB(mappedName = "java:app/Parer-ejb/RecuperoSync")
    private RecuperoSync recuperoSync;

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
	Response405.fancy405(resp, Response405.NomeWebServiceRest.REC_STATO_CONSERVAZIONE_SYNC);
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
	RispostaWSRecupero rispostaWs;
	RecuperoExt myRecuperoExt;
	StatoConservazione myEsito;
	SyncFakeSessn sessioneFinta = new SyncFakeSessn();
	Iterator<FileItem> tmpIterator = null;
	DiskFileItem tmpFileItem = null;
	List<FileItem> fileItems = null;
	AvanzamentoWs tmpAvanzamento;
	RequestPrsr myRequestPrsr = new RequestPrsr();
	RequestPrsr.ReqPrsrConfig tmpPrsrConfig = new RequestPrsr().new ReqPrsrConfig();

	rispostaWs = new RispostaWSRecupero();
	myRecuperoExt = new RecuperoExt();
	myRecuperoExt.setDescrizione(new WSDescRecStatoCons());

	tmpAvanzamento = AvanzamentoWs.nuovoAvanzamentoWS(instanceName,
		AvanzamentoWs.Funzioni.Recupero);
	tmpAvanzamento.logAvanzamento();

	recuperoSync.initRispostaWs(rispostaWs, tmpAvanzamento, myRecuperoExt);
	myEsito = rispostaWs.getIstanzaEsito();

	// configura il parser del WS - è un parser generico e deve adattarsi a tutti i ws di
	// recupero
	myRecuperoExt.getParametriParser().setLeggiAncheUdAnnullate(true);
	// serve a mostrare lo stato di conservazione anche se l'UD è annullata
	//

	sessioneFinta.setTmApertura(new Date());
	//
	sessioneFinta.setIpChiamante(myRequestPrsr.leggiIpVersante(request));
	// MEV#33897 - Eliminazione controllo LOGINNAME/PASSWORD nella chiamata ai servizi di
	// recupero con certificato
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
		    // fileItems = myRequestPrsr.parseWithCommonName(rispostaWs, tmpPrsrConfig);
		    // MEV#33897 - Eliminazione controllo LOGINNAME/PASSWORD nella chiamata ai
		    // servizi di recupero con
		    // certificato
		    fileItems = myRequestPrsr.parse(rispostaWs, tmpPrsrConfig, null,
			    sessioneFinta.getCertCommonName() == null ? false : true);

		    //
		    if (rispostaWs.getSeverity() != SeverityEnum.OK) {
			rispostaWs.setEsitoWsError(rispostaWs.getErrorCode(),
				rispostaWs.getErrorMessage());
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
		    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
			tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaSemantica)
				.setFase("verifica versione").logAvanzamento();
			recuperoSync.verificaVersione(sessioneFinta.getVersioneWS(), rispostaWs,
				myRecuperoExt);
		    }

		    // testa le credenziali utente, tramite ejb
		    myEsito = rispostaWs.getIstanzaEsito();
		    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
			tmpAvanzamento.setFase("verifica credenziali").logAvanzamento();
			recuperoSync.verificaCredenziali(sessioneFinta.getLoginName(),
				sessioneFinta.getPassword(), sessioneFinta.getIpChiamante(),
				rispostaWs, myRecuperoExt, sessioneFinta.getCertCommonName());
		    }

		    // verifica formale e semantica dell'XML di versamento
		    myEsito = rispostaWs.getIstanzaEsito();
		    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
			tmpAvanzamento.setFase("verifica xml").logAvanzamento();

			recuperoSync.parseXML(sessioneFinta.getDatiIndiceSipXml(), rispostaWs,
				myRecuperoExt);
		    }

		    // prepara risposta
		    myEsito = rispostaWs.getIstanzaEsito();
		    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
			tmpAvanzamento.setFase("generazione xml").logAvanzamento();

			recuperoSync.recuperaStatoConservazioneUD(rispostaWs, myRecuperoExt);
		    }

		    // inserisco i record della tabella ARO_LOG_STATO_CONSERVAZIONE
		    myEsito = rispostaWs.getIstanzaEsito();
		    if (rispostaWs.getSeverity() == SeverityEnum.OK
			    && myRecuperoExt.getModificatoriWSCalc()
				    .contains(Costanti.ModificatoriWS.TAG_LOG_STATO_CONSERV_UD)) {
			tmpAvanzamento.setFase("inserimento dati ARO_LOG_STATO_CONSERVAZIONE")
				.logAvanzamento();

			recuperoSync.recuperaLogStatoConservazioneUD(rispostaWs, myRecuperoExt);
		    }

		    myEsito = rispostaWs.getIstanzaEsito();
		} catch (FileUploadException e1) {
		    rispostaWs.setSeverity(SeverityEnum.ERROR);
		    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			    "Eccezione generica nella servlet recupero sync " + e1.getMessage());
		    log.error("Eccezione nella servlet recupero sync", e1);
		} catch (Exception e1) {
		    rispostaWs.setSeverity(SeverityEnum.ERROR);
		    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
			    "Eccezione generica nella servlet recupero sync " + e1.getMessage());
		    log.error("Eccezione generica nella servlet recupero sync", e1);
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
		rispostaWs.setSeverity(SeverityEnum.ERROR);
		rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.WS_CHECK,
			"La chiamata non è multipart/formdata ");
		log.error(
			"Errore nella servlet recupero sync: la chiamata non è multipart/formdata ");
	    }
	}

	// rispondi
	tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.InvioRisposta).setFase("")
		.logAvanzamento();
	response.reset();
	response.setStatus(HttpServletResponse.SC_OK);
	response.setContentType("application/xml; charset=\"utf-8\"");

	try (ServletOutputStream out = response.getOutputStream();
		OutputStreamWriter tmpStreamWriter = new OutputStreamWriter(out,
			StandardCharsets.UTF_8);) {

	    Marshaller marshaller = xmlContextCache.getVersRespStatoCtx_StatoConservazione()
		    .createMarshaller();
	    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
	    marshaller.marshal(myEsito, tmpStreamWriter);
	} catch (JAXBException | IOException e) {
	    log.error("Eccezione nella servlet recupero sync", e);
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
