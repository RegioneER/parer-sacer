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
package it.eng.parer.web.test;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare;
import it.eng.parer.elencoVersamento.utils.ComponenteInElenco;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.UnitaDocumentariaInElenco;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.web.util.WebConstants;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * Servlet di test per i metodi dell'helper {@link ElencoVersamentoHelper}.
 *
 * Probabilmente in futuro questa classe verrà eliminata.
 *
 * @author Snidero_L
 */
@WebServlet(name = "TestEvhelper", urlPatterns = {
        "/TestEvhelper" })
public class TestElencoVersamentoHelperServlet extends HttpServlet {

    private static final long serialVersionUID = 3909951923813298438L;

    @EJB
    private ElencoVersamentoHelper evHelper;

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

        String idStrutt = request.getParameter("idStruttura");
        String idElenco = request.getParameter("idElencoVers");
        String idUnita = request.getParameter("idUnitaDoc");
        String idComp = request.getParameter("idCompDoc");

        int idStruttura = Integer.parseInt(StringUtils.isNotBlank(idStrutt) ? idStrutt : "8");
        int idElencoVers = Integer.parseInt(StringUtils.isNotBlank(idElenco) ? idElenco : "16");
        int idUnitaDoc = Integer.parseInt(StringUtils.isNotBlank(idUnita) ? idUnita : "24");
        int idCompDoc = Integer.parseInt(StringUtils.isNotBlank(idComp) ? idComp : "32");

        StringBuilder sb = new StringBuilder();

        sb.append("<h2>").append("elaboraStrutturaFase1").append("</h2>");

        List<ElvElencoVersDaElab> elenchiValidati = evHelper.retrieveElenchi(idStruttura,
                ElencoEnums.ElencoStatusEnum.VALIDATO);
        new PrintElvElencoVersDaElab(elenchiValidati, sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "Buono: SELECT t1.ID_ELENCO_VERS_DA_ELAB, t1.AA_KEY_UNITA_DOC, t1.ID_CRITERIO_RAGGR, t1.ID_STRUT, t1.TI_STATO_ELENCO, t1.ID_ELENCO_VERS FROM ELV_ELENCO_VERS t0, ELV_ELENCO_VERS_DA_ELAB t1 WHERE (((t1.TI_STATO_ELENCO = ?) AND (t1.ID_STRUT = ?)) AND (t0.ID_ELENCO_VERS = t1.ID_ELENCO_VERS)) ORDER BY t0.DT_FIRMA_INDICE ASC\n"
                        + "	bind => [VALIDATO, 8]");
        sb.append("</pre>");

        Collection<Long> retrieveUdVersOrAggInElenco = evHelper
                .retrieveUdVersOrAggInElencoValidate(idElencoVers);
        new PrintId(retrieveUdVersOrAggInElenco, "UD versato o aggiornate in elenco", sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "  Buono: SELECT ID_UNITA_DOC FROM ARO_UNITA_DOC WHERE (((ID_ELENCO_VERS = ?) AND (DT_ANNUL = {d '2444-12-31'})) AND (TI_STATO_UD_ELENCO_VERS = ?))\n"
                        + "	bind => [16, IN_ELENCO_VALIDATO]\n"
                        + "Buono: SELECT DISTINCT t0.ID_UNITA_DOC FROM ARO_UNITA_DOC t0, ARO_DOC t1 WHERE ((((t1.ID_ELENCO_VERS = ?) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND (t1.TI_STATO_DOC_ELENCO_VERS = ?)) AND (t0.ID_UNITA_DOC = t1.ID_UNITA_DOC))\n"
                        + "	bind => [16, IN_ELENCO_VALIDATO]");
        sb.append("</pre>");

        sb.append("<h2>").append("elaboraStrutturaFase1 &gt; elaboraUDFase1").append("</h2>");

        Collection<ComponenteDaVerificare> retrieveCompsToVerify = evHelper
                .retrieveCompsToVerify(idElencoVers, idUnitaDoc);
        new PrintComponenteDaVerificare(retrieveCompsToVerify, sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "    Buono: SELECT t0.ID_COMP_DOC, t1.DT_CREAZIONE, t0.FL_COMP_FIRMATO FROM ARO_STRUT_DOC t3, ARO_DOC t2, ARO_UNITA_DOC t1, ARO_COMP_DOC t0 WHERE (((((t2.ID_UNITA_DOC = ?) AND (t2.TI_CREAZIONE = ?)) AND (t1.ID_ELENCO_VERS = ?)) AND (t2.DT_ANNUL = {d '2444-12-31'})) AND (((t3.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t2.ID_DOC = t3.ID_DOC)) AND (t1.ID_UNITA_DOC = t2.ID_UNITA_DOC)))\n"
                        + "  	bind => [24, VERSAMENTO_UNITA_DOC, 16]\n"
                        + "  Buono: SELECT t0.ID_COMP_DOC, t1.DT_CREAZIONE, t0.FL_COMP_FIRMATO FROM ARO_COMP_DOC t0, ARO_STRUT_DOC t2, ARO_DOC t1 WHERE (((((t1.ID_UNITA_DOC = ?) AND (t1.TI_CREAZIONE = ?)) AND (t1.ID_ELENCO_VERS = ?)) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND ((t2.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t1.ID_DOC = t2.ID_DOC)))\n"
                        + "  	bind => [24, AGGIUNTA_DOCUMENTO, 16]");
        sb.append("</pre>");

        AroCompDoc retrieveCompDocById = evHelper.retrieveCompDocById(idCompDoc);
        new PrintAroCompDoc(retrieveCompDocById, sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "      Buono: SELECT ID_COMP_DOC, CD_ENCODING_HASH_FILE_CALC, CD_ENCODING_HASH_FILE_VERS, DL_URN_COMP_VERS, DS_ALGO_HASH_FILE_CALC, DS_ALGO_HASH_FILE_VERS, DS_ESITO_VERIF_FIRME_DT_VERS, DS_FORMATO_RAPPR_CALC, DS_FORMATO_RAPPR_ESTESO_CALC, DS_HASH_FILE_CALC, DS_HASH_FILE_CONTR, DS_HASH_FILE_VERS, DS_ID_COMP_VERS, DS_MSG_ESITO_CONTR_FORMATO, DS_MSG_ESITO_VERIF_FIRME, DS_NOME_COMP_VERS, DS_NOME_FILE_ARK, DS_RIF_TEMP_VERS, DS_URN_COMP_CALC, FL_COMP_FIRMATO, FL_NO_CALC_FMT_VERIF_FIRME, FL_NO_CALC_HASH_FILE, FL_RIF_TEMP_DATA_FIRMA_VERS, ID_STRUT, NI_ORD_COMP_DOC, NI_SIZE_FILE_CALC, TI_ESITO_CONTR_FORMATO_FILE, TI_ESITO_CONTR_HASH_VERS, TI_ESITO_VERIF_FIRME, TI_ESITO_VERIF_FIRME_DT_VERS, TI_SUPPORTO_COMP, TM_RIF_TEMP_VERS, ID_COMP_DOC_PADRE, ID_STRUT_DOC, ID_UNITA_DOC_RIF, ID_FORMATO_FILE_VERS, ID_FORMATO_FILE_CALC, ID_TIPO_COMP_DOC, ID_TIPO_RAPPR_COMP FROM ARO_COMP_DOC WHERE (ID_COMP_DOC = ?)\n"
                        + "    	bind => [32]");
        sb.append("</pre>");

        sb.append("<h2>").append("elaboraStrutturaFase2").append("</h2>");

        List<ElvElencoVersDaElab> elenchiPerAip = evHelper.retrieveElenchi(idStruttura,
                ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS);
        new PrintElvElencoVersDaElab(elenchiPerAip, sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "      Buono: SELECT t1.ID_ELENCO_VERS_DA_ELAB, t1.AA_KEY_UNITA_DOC, t1.ID_CRITERIO_RAGGR, t1.ID_STRUT, t1.TI_STATO_ELENCO, t1.ID_ELENCO_VERS FROM ELV_ELENCO_VERS t0, ELV_ELENCO_VERS_DA_ELAB t1 WHERE (((t1.TI_STATO_ELENCO = ?) AND (t1.ID_STRUT = ?)) AND (t0.ID_ELENCO_VERS = t1.ID_ELENCO_VERS)) ORDER BY t0.DT_FIRMA_INDICE ASC\n"
                        + "	bind => [IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS, 8]");
        sb.append("</pre>");

        Set<UnitaDocumentariaInElenco> udInElenco = evHelper.retrieveUdInElenco(idElencoVers);
        new PrintUnitaDocumentariaInElenco(udInElenco, sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "        Buono: SELECT ID_UNITA_DOC, ? FROM ARO_UNITA_DOC WHERE (((ID_ELENCO_VERS = ?) AND (DT_ANNUL = {d '2444-12-31'})) AND (TI_STATO_UD_ELENCO_VERS = ?))\n"
                        + "  	bind => [false, 16, IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS]\n"
                        + "  Buono: SELECT t0.ID_UNITA_DOC, ? FROM ARO_UNITA_DOC t0, ARO_DOC t1 WHERE (((((t1.ID_ELENCO_VERS = ?) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND (t1.TI_STATO_DOC_ELENCO_VERS = ?)) AND NOT EXISTS (SELECT ? FROM ELV_ELENCO_VERS t4, ELV_ELENCO_VERS t3, ARO_UNITA_DOC t2 WHERE (((t3.ID_ELENCO_VERS = t4.ID_ELENCO_VERS) AND (t2.ID_UNITA_DOC = t0.ID_UNITA_DOC)) AND ((t3.ID_ELENCO_VERS = t2.ID_ELENCO_VERS) AND (t4.ID_ELENCO_VERS = t1.ID_ELENCO_VERS)))) ) AND (t0.ID_UNITA_DOC = t1.ID_UNITA_DOC))\n"
                        + "  	bind => [true, 16, IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS, 1]");
        sb.append("</pre>");

        sb.append("<h2>").append("elaboraStrutturaFase2 &gt; elaboraUDFase2").append("</h2>");

        sb.append("<h2>").append(
                "elaboraStrutturaFase2 &gt; elaboraUDFase2 &gt; aggiornaElencoSeNonCiSonoDocAggiunti")
                .append("</h2>");

        Set<ComponenteInElenco> retrieveComponentiInElenco = evHelper
                .retrieveComponentiInElenco(idUnitaDoc, idElencoVers);
        new PrintComponenteInElenco(retrieveComponentiInElenco, sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "        Buono: SELECT t0.ID_COMP_DOC, t0.DS_URN_COMP_CALC FROM ARO_UNITA_DOC t3, ARO_STRUT_DOC t2, ARO_DOC t1, ARO_COMP_DOC t0 WHERE (((((t1.TI_CREAZIONE = ?) AND (t1.ID_UNITA_DOC = ?)) AND (t3.ID_ELENCO_VERS = ?)) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND (((t2.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t1.ID_DOC = t2.ID_DOC)) AND (t3.ID_UNITA_DOC = t1.ID_UNITA_DOC)))\n"
                        + "	bind => [VERSAMENTO_UNITA_DOC, 24, 16]\n"
                        + "Buono: SELECT t0.ID_COMP_DOC, t0.DS_URN_COMP_CALC FROM ARO_COMP_DOC t0, ARO_STRUT_DOC t2, ARO_DOC t1 WHERE (((((t1.ID_UNITA_DOC = ?) AND (t1.ID_ELENCO_VERS = ?)) AND (t1.TI_CREAZIONE = ?)) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND ((t2.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t1.ID_DOC = t2.ID_DOC)))\n"
                        + "	bind => [24, 16, AGGIUNTA_DOCUMENTO]");
        sb.append("</pre>");

        sb.append("<h2>").append(
                "elaboraStrutturaFase2 &gt; elaboraUDFase2 &gt; aggiornaElencoSeCiSonoDocAggiunti")
                .append("</h2>");

        sb.append("<h3>Data minima in elenco vale: </h3>");
        String data = null;
        Date dataMinimaDocInElenco = evHelper.getDataMinimaDocInElenco(idUnitaDoc, idElencoVers);
        if (dataMinimaDocInElenco != null) {
            data = new SimpleDateFormat(WebConstants.DATE_FORMAT_TIMESTAMP_TYPE)
                    .format(dataMinimaDocInElenco);
        } else {
            data = "non disponibile";
        }
        sb.append("<p>" + data + "</p>");

        sb.append("<pre class=\"query\">");
        sb.append(
                "          Buono: SELECT MIN(DT_CREAZIONE) FROM ARO_DOC WHERE ((((ID_UNITA_DOC = ?) AND (ID_ELENCO_VERS = ?)) AND (TI_CREAZIONE = ?)) AND (DT_ANNUL = {d '2444-12-31'}))\n"
                        + "  	bind => [24, 16, AGGIUNTA_DOCUMENTO]");
        sb.append("</pre>");

        Set<Long> retrieveDocNonInElenco = evHelper.retrieveDocNonInElenco(idUnitaDoc, new Date(),
                idElencoVers);
        new PrintId(retrieveDocNonInElenco, "ID doc non in elenco", sb).print();

        sb.append("<pre class=\"query\">");
        sb.append(
                "          Buono: SELECT t0.ID_DOC FROM ARO_DOC t0, ARO_UNITA_DOC t1 WHERE ((((((t0.TI_CREAZIONE = ?) AND (t0.ID_UNITA_DOC = ?)) AND (t0.DT_ANNUL = {d '2444-12-31'})) AND (t1.DT_CREAZIONE < ?)) AND ((t1.ID_ELENCO_VERS IS NULL) OR ((NOT ((t1.ID_ELENCO_VERS IS NULL)) AND (t1.ID_ELENCO_VERS <> ?)) AND (t1.TI_STATO_UD_ELENCO_VERS <> ?)))) AND (t1.ID_UNITA_DOC = t0.ID_UNITA_DOC))\n"
                        + "    	bind => [VERSAMENTO_UNITA_DOC, 24, to_date('2017-04-13 15:09:18','yyyy-mm-dd hh24:mi:ss'), 16, IN_ELENCO_IN_CODA_INDICE_AIP]\n"
                        + "    Buono: SELECT ID_DOC FROM ARO_DOC WHERE (((((ID_UNITA_DOC = ?) AND (TI_CREAZIONE = ?)) AND (DT_ANNUL = {d '2444-12-31'})) AND (DT_CREAZIONE < ?)) AND ((ID_ELENCO_VERS IS NULL) OR ((NOT ((ID_ELENCO_VERS IS NULL)) AND (ID_ELENCO_VERS <> ?)) AND (TI_STATO_DOC_ELENCO_VERS <> ?))))\n"
                        + "    	bind => [24, AGGIUNTA_DOCUMENTO, to_date('2017-04-13 15:09:18','yyyy-mm-dd hh24:mi:ss'), 16, IN_ELENCO_IN_CODA_INDICE_AIP]");
        sb.append("</pre>");

        Set<ComponenteInElenco> retrieveCompInElenco = evHelper.retrieveCompInElenco(idUnitaDoc,
                idElencoVers);
        new PrintComponenteInElenco(retrieveCompInElenco, sb).print();
        sb.append("<pre class=\"query\">");
        sb.append(
                "          Buono: SELECT t0.ID_COMP_DOC, t0.DS_URN_COMP_CALC FROM ARO_COMP_DOC t0, ARO_STRUT_DOC t2, ARO_DOC t1 WHERE (((((t1.ID_UNITA_DOC = ?) AND (t1.ID_ELENCO_VERS = ?)) AND (t1.TI_CREAZIONE = ?)) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND ((t2.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t1.ID_DOC = t2.ID_DOC)))\n"
                        + "	bind => [24, 16, AGGIUNTA_DOCUMENTO]\n"
                        + "Buono: SELECT t0.ID_COMP_DOC, t0.DS_URN_COMP_CALC FROM ARO_UNITA_DOC t3, ARO_STRUT_DOC t2, ARO_DOC t1, ARO_COMP_DOC t0 WHERE (((((t1.TI_CREAZIONE = ?) AND (t1.ID_UNITA_DOC = ?)) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND (t3.TI_STATO_UD_ELENCO_VERS = ?)) AND (((t2.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t1.ID_DOC = t2.ID_DOC)) AND (t3.ID_UNITA_DOC = t1.ID_UNITA_DOC)))\n"
                        + "	bind => [VERSAMENTO_UNITA_DOC, 24, IN_ELENCO_IN_CODA_INDICE_AIP]\n"
                        + "Buono: SELECT t0.ID_COMP_DOC, t0.DS_URN_COMP_CALC FROM ARO_COMP_DOC t0, ARO_STRUT_DOC t2, ARO_DOC t1 WHERE (((((t1.TI_CREAZIONE = ?) AND (t1.ID_UNITA_DOC = ?)) AND (t1.DT_ANNUL = {d '2444-12-31'})) AND (t1.TI_STATO_DOC_ELENCO_VERS = ?)) AND ((t2.ID_STRUT_DOC = t0.ID_STRUT_DOC) AND (t1.ID_DOC = t2.ID_DOC)))\n"
                        + "	bind => [AGGIUNTA_DOCUMENTO, 24, IN_ELENCO_IN_CODA_INDICE_AIP]");
        sb.append("</pre>");

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet TestEvhelper</title>");
            out.println("<style>.query{ background-color: lightgray; }</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet TestEvhelper at " + request.getContextPath() + "</h1>");
            out.println(sb);
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Classi per stampare su un buffer">
    class PrintAroCompDoc extends Printer<AroCompDoc> {
        public PrintAroCompDoc(AroCompDoc aroCompDoc, StringBuilder buffer) {
            super(Arrays.asList(new AroCompDoc[] {
                    aroCompDoc }), "Retreive componente: ", buffer);
        }

        @Override
        protected String print(AroCompDoc element) {
            return element != null
                    ? "componente con id " + element.getIdCompDoc() + " con urn "
                            + element.getDsUrnCompCalc()
                    : "componente null";
        }

    }

    class PrintElvElencoVersDaElab extends Printer<ElvElencoVersDaElab> {

        public PrintElvElencoVersDaElab(Collection<ElvElencoVersDaElab> elements,
                StringBuilder buffer) {
            super(elements, "Retreive Elenchi:", buffer);
        }

        @Override
        protected String print(ElvElencoVersDaElab e) {
            return e.getIdElencoVersDaElab() + " per struttura " + e.getIdStrut() + " con stato "
                    + e.getTiStatoElenco();
        }
    }

    class PrintComponenteDaVerificare extends Printer<ComponenteDaVerificare> {

        public PrintComponenteDaVerificare(Collection<ComponenteDaVerificare> elements,
                StringBuilder buffer) {
            super(elements, "Componenti da verifica:", buffer);
        }

        @Override
        protected String print(ComponenteDaVerificare componente) {
            return " id: " + componente.getIdCompDoc() + " data " + componente.getDtCreazione()
                    + " isFirmato " + componente.getFlCompFirmato();
        }
    }

    class PrintId extends Printer<Long> {

        public PrintId(Collection<Long> elements, String title, StringBuilder buffer) {
            super(elements, title, buffer);
        }

        @Override
        protected String print(Long id) {
            return "id vale " + id;
        }
    }

    class PrintUnitaDocumentariaInElenco extends Printer<UnitaDocumentariaInElenco> {

        public PrintUnitaDocumentariaInElenco(Collection<UnitaDocumentariaInElenco> elements,
                StringBuilder buffer) {
            super(elements, "UD in elenco:", buffer);
        }

        @Override
        protected String print(UnitaDocumentariaInElenco ud) {
            return (ud.getIdUnitaDoc() + " id unita doc, solo aggiunti: "
                    + ud.isFlSoloDocAggiunti());
        }

    }

    class PrintComponenteInElenco extends Printer<ComponenteInElenco> {

        public PrintComponenteInElenco(Collection<ComponenteInElenco> element, StringBuilder sb) {
            super(element, "Componenti in elenco", sb);
        }

        @Override
        protected String print(ComponenteInElenco comp) {
            return comp.getIdCompDoc() + " id comp doc";
        }
    }

    abstract class Printer<T> {

        Collection<T> elements;
        String title;
        StringBuilder buffer;

        Printer(Collection<T> elements, String title, StringBuilder buffer) {
            this.elements = elements;
            this.title = title;
            this.buffer = buffer;
        }

        public StringBuilder print() {

            buffer.append("<h3>" + title + "</h3>");
            buffer.append("<ul>");
            for (T element : elements) {
                buffer.append("<li>");
                buffer.append(print(element));
                buffer.append("<li>");
            }
            buffer.append("</ul>");
            return buffer;
        }

        protected abstract String print(T element);

    }
    // </editor-fold>

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
        return "Short description";
    }// </editor-fold>

}
