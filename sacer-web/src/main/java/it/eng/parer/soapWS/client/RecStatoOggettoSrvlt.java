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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.soapWS.client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.security.auth.SOAPClientLoginHandlerResolver;
import javax.xml.ws.soap.SOAPFaultException;

/**
 *
 * @author paogilio
 */
@WebServlet(name = "RecStatoOggettoSrvlt", urlPatterns = {
        "/RecStatoOggettoPing" })
public class RecStatoOggettoSrvlt extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(RecStatoOggettoSrvlt.class);
    @EJB
    private ConfigurationHelper configurationHelper;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String className = this.getClass().getSimpleName();
        String methodName = new Object() {
        }.getClass().getEnclosingMethod().getName();

        log.debug(className + "." + methodName
                + ": invocata la servlet per il recupero stato oggetto di PING");

        RecStatoOggettoPing_Service service = new RecStatoOggettoPing_Service();
        service.setHandlerResolver(new SOAPClientLoginHandlerResolver());

        log.debug(className + "." + methodName
                + ": recupero i parametri per accedere al Web Service Recupero Stato Oggetto di PING");
        String url = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.URL_RECUP_OGGETTO_PING);
        String username = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.USERID_RECUP_OGGETTO_PING);
        String password = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.PSW_RECUP_OGGETTO_PING);

        String ambiente = request.getParameter("AMBIENTE");
        String versatore = request.getParameter("VERSATORE");
        String cdKeyOggetto = request.getParameter("CD_KEY_OGGETTO");

        try { // Call Web Service Operation
            log.debug(className + "." + methodName
                    + ": genero il client del Web Service Recupero Stato Oggetto di PING");
            RecStatoOggettoPing client = service.getRecStatoOggettoPingPort();
            Map<String, Object> requestContext = ((BindingProvider) client).getRequestContext();
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
            requestContext.put("user", username);
            requestContext.put("pass", password);

            log.debug(className + "." + methodName
                    + ": ottengo la risposta del Web Service Recupero Stato Oggetto di PING");

            // RISPOSTA
            RecuperoStatoOggettoRisposta risp = client.getStatoOggetto(ambiente, versatore,
                    cdKeyOggetto);
            String err = risp.getCdErr() != null ? risp.getCdErr() : " ";
            String dlErr = risp.getDlErr() != null ? risp.getDlErr() : " ";
            String statoOggetto = risp.getStatoOggetto() != null ? risp.getStatoOggetto()
                    : "Impossibile recuperare l'informazione";
            String descrizioneStatoOggetto = risp.getDescrizioneStatoOggetto() != null
                    ? risp.getDescrizioneStatoOggetto()
                    : "Impossibile recuperare l'informazione";

            log.debug(className + "." + methodName
                    + ": preparo la risposta del Web Service Recupero Stato Oggetto di PING nella pagina JSP");
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet RecStatoOggettoSrvlt</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet RecStatoOggettoSrvlt at " + request.getContextPath()
                        + "</h1>");
                out.println("<h4>Esito: " + risp.getCdEsito() + "</h4>");
                out.println("<h4>Codice errore: " + err + "</h4>");
                out.println("<h4>Descrizione errore: " + dlErr + "</h4 >");
                out.println("<h4>Ambiente: " + ambiente + "</h4>");
                out.println("<h4>Versatore: " + versatore + "</h4>");
                out.println("<h4>Codice oggetto: " + cdKeyOggetto + "</h4>");
                out.println("<h4>Stato oggetto: " + statoOggetto + "</h4>");
                out.println(
                        "<h4>Descrizione stato oggetto: " + descrizioneStatoOggetto + "</h4><br>");
                out.println("</body>");
                out.println("</html>");
            }
        } catch (SOAPFaultException e) {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet RecStatoOggettoSrvlt</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>Servlet RecStatoOggettoSrvlt at " + request.getContextPath()
                        + "</h1>");
                out.println("<h4>Esito: NEGATIVO </h4>");
                out.println("<h4>Codice errore: </h4>");
                out.println("<h4>Descrizione errore: " + e.getMessage() + "</h4 >");
                out.println("<h4>Ambiente: " + ambiente + "</h4>");
                out.println("<h4>Versatore: " + versatore + "</h4>");
                out.println("<h4>Codice oggetto: " + cdKeyOggetto + "</h4>");
                out.println("<h4>Stato oggetto: </h4>");
                out.println("<h4>Descrizione stato oggetto: </h4><br>");
                out.println("</body>");
                out.println("</html>");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the
    // left to edit the
    // code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Servlet di prova per testare il WS di Recupero Stato Oggetto di PING";
    }// </editor-fold>

}
