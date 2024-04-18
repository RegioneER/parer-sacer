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
                            var elemento = $(this).find('td:eq(2), td:eq(3), td:eq(4)');
                            elemento.css({"text-align": "right"});
                            
                            // Mostra/nascondi icona link ud
                            var elemento1 = $(this).find('td:eq(0)').children().text();                            
                            if(elemento1 !== 'IN_CODA_VERIFICA_FIRMA_DT_VERS'
                            && elemento1 !== 'IN_CODA_INDICE_AIP_DA_ELAB'
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
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriMonitoraggioIndiceAIP.NI_GG_STATO%>" colSpan="1" />              
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