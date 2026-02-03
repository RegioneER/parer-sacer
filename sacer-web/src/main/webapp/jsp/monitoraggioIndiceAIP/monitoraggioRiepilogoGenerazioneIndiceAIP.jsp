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
    <sl:head  title="Monitoraggio - Riepilogo processo generazione indice AIP" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('#MonitoraggioIndiceAIPList tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(2), td:eq(3), td:eq(4), td:eq(5)');
                            elemento.css({"text-align": "right"});
                            
                            // Mostra/nascondi icona link ud
                            var elemento1 = $(this).find('td:eq(0)').children().text();                            
                            if(elemento1 !== 'IN_CODA_INDICE_AIP_DA_ELAB'
                            && elemento1 !== 'ESEGUITA_VERIFICA_FIRMA_DT_VERS'){
                            var elemento2 = $(this).find('td:eq(5)');                            
                                elemento2.empty();
                            }
                            
                            // Mostra/nascondi link stati
                            
                            var colonna1 = $(this).find('td:eq(0)');
                            //alert(colonna1);
                            if($("#Id_strut").val()!==""){
                                //alert('sono dentro if');
                                //alert('id strut è '+$("#Id_strut option:selected").val());
                                                            
                                var test = colonna1.children().text();
                                //alert(test);
                                colonna1.children().remove();
                                colonna1.text(test);
                            }
                                
                            
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
            <sl:contentTitle title="RIEPILOGO PROCESSO GENERAZIONE INDICE AIP"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamentiSintetico")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.NAME%>" hideOperationButton="true" hideBackButton="true"/>                 
            <% }%>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.ID_STRUT%>" colSpan="2" />
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.AA_KEY_UNITA_DOC%>" colSpan="1"/>              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.TI_STATO_ELENCO%>" colSpan="2" />              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.DT_CREAZIONE_ELENCO_DA%>" colSpan="2" />                              
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.DT_CREAZIONE_ELENCO_A%>" colSpan="2" />              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.NI_GG_STATO_DA%>" colSpan="2" />                              
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.NI_GG_STATO_A%>" colSpan="2" />              
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.GENERA_MONITORAGGIO_INDICE_AIPBUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioIndiceAIPForm.MonitoraggioIndiceAIPList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioIndiceAIPForm.MonitoraggioIndiceAIPList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
