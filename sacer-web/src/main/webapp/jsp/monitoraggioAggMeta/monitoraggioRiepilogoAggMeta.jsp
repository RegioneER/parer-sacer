<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head  title="Monitoraggio - Riepilogo aggiornamento metadati" >
        <script type="text/javascript">
            $(document).ready(function () {

                function initAnni() {
                    var value = $("#Id_strut").val();
                    if (value) {
                        $("#Aa_key_unita_doc").attr("disabled", false);
                        $("#Aa_key_unita_doc_da").attr("disabled", false);
                        $("#Aa_key_unita_doc_a").attr("disabled", false);
                    } else {
                        $("#Aa_key_unita_doc").val("");
                        $("#Aa_key_unita_doc_da").val("");
                        $("#Aa_key_unita_doc_a").val("");
                        $("#Aa_key_unita_doc").attr("disabled", true);
                        $("#Aa_key_unita_doc_da").attr("disabled", true);
                        $("#Aa_key_unita_doc_a").attr("disabled", true);
                    }
                }

                initAnni();

                $("#Id_strut").change(function () {
                    initAnni();
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="RIEPILOGO AGGIORNAMENTO METADATI"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamentiSintetico")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.NAME%>" hideOperationButton="true" />                 
            <% }%>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.ID_REGISTRO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.AA_KEY_UNITA_DOC%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.AA_KEY_UNITA_DOC_DA%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.AA_KEY_UNITA_DOC_A%>" colSpan="1" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.GENERA_MONITORAGGIO_AGG_META_BUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <!-- Sezione aggiornamenti metadati -->
            <%@ include file="aggiornamentiMetadati.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- Sezione aggiornamenti metadati falliti -->
            <%@ include file="aggiornamentiMetadatiFalliti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- MEV#22438 -->
            <!-- Sezione unità documentarie derivanti da aggiornamenti metadati falliti -->
            <%@ include file="udUpdFalliti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- end MEV#22438 -->
            <%--<!-- Sezione versamenti falliti, visualizzabile solo in caso idTipoUnitaDoc sia nullo -->
            <% String id = request.getParameter("Id_tipo_unita_doc");
                pageContext.setAttribute("Id_tipo_unita_doc", id);
            %>
            <c:if test="${empty Id_tipo_unita_doc}">
                <%@ include file="versFalliti.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
                <%@ include file="aggFallite.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
                <%@ include file="udVersFalliti.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
                <%@ include file="docAggFallite.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
            </c:if>

            <%@ include file="udAnnul.jspf"%>--%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
