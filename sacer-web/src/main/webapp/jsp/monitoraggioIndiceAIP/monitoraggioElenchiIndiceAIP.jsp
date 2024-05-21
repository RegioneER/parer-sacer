<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioIndiceAIPForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head  title="Monitoraggio - Lista elenchi per processo generazione indice AIP" >
        <script type='text/javascript' >
            $(document).ready(function () {
                $('#ElenchiMonitoraggioIndiceAIPList tr').each(
                        function (index) {
                            //var elemento = $(this).find('td:eq(2), td:eq(3), td:eq(4)');
                            //elemento.css({"text-align": "right"});
                            
                            // Mostra/nascondi icona link ud
                            var elemento1 = $("[name='Cd_ti_eve_stato_elenco_vers']").val();
                            if(elemento1 !== 'IN_CODA_VERIFICA_FIRMA_DT_VERS'
                            && elemento1 !== 'IN_CODA_INDICE_AIP_DA_ELAB'
                            && elemento1 !== 'ESEGUITA_VERIFICA_FIRMA_DT_VERS'){
                            var elemento2 = $(this).find('td:eq(16)');                            
                                elemento2.empty();
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
            <sl:contentTitle title="LISTA ELENCHI PER PROCESSO GENERAZIONE INDICE AIP"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamentiSintetico")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.NAME%>" hideOperationButton="true" />                 
            <% }%>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.ID_ENTE%>" colSpan="2" />
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.ID_STRUT%>" colSpan="2" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.AA_KEY_UNITA_DOC%>" colSpan="1"/>              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.TI_STATO_ELENCO%>" colSpan="2" />              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.DT_CREAZIONE_ELENCO_DA%>" colSpan="2" />                              
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.DT_CREAZIONE_ELENCO_A%>" colSpan="2" />              
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.NI_GG_STATO_DA%>" colSpan="2" />                              
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.NI_GG_STATO_A%>" colSpan="2" />   
                  <sl:newLine />                             
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.CD_TI_EVE_STATO_ELENCO_VERS%>" colSpan="2" />         
                  <sl:newLine />                             
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.FL_ELENCO_FISC%>" colSpan="2" />              
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
             <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriElenchiMonitoraggioIndiceAIP.GENERA_ELENCHI_MON_INDICE_AIPBUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= MonitoraggioIndiceAIPForm.ElenchiMonitoraggioIndiceAIPList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioIndiceAIPForm.ElenchiMonitoraggioIndiceAIPList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>