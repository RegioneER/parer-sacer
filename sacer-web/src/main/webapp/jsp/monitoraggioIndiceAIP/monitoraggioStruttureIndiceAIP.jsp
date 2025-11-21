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
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioIndiceAIPForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head  title="Monitoraggio - Lista strutture per processo generazione indice AIP" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('#StrutMonitoraggioIndiceAIPList tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(4), td:eq(5), td:eq(6)');
                            elemento.css({"text-align": "right"});
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
            <sl:contentTitle title="LISTA STRUTTURE PER PROCESSO GENERAZIONE INDICE AIP"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamentiSintetico")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.NAME%>" hideOperationButton="true" />                 
            <% }%>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.ID_STRUT%>" colSpan="2" />
                <sl:newLine />                             
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.AA_KEY_UNITA_DOC%>" colSpan="1"/>              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.TI_STATO_ELENCO%>" colSpan="2" />              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.DT_CREAZIONE_ELENCO_DA%>" colSpan="2" />                              
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.DT_CREAZIONE_ELENCO_A%>" colSpan="2" />              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.NI_GG_STATO_DA%>" colSpan="2" />                              
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.NI_GG_STATO_A%>" colSpan="2" /> 
                <sl:newLine />                             
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.CD_TI_EVE_STATO_ELENCO_VERS%>" colSpan="2" />              
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.GENERA_STRUT_MON_INDICE_AIPBUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioIndiceAIPForm.StrutMonitoraggioIndiceAIPList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioIndiceAIPForm.StrutMonitoraggioIndiceAIPList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
