<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioIndiceAIPForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head  title="Monitoraggio - Lista unità documentarie per processo generazione indice AIP" >
        <script type='text/javascript' >
            $(document).ready(function () {
                 // NOTA:  il codice si seguito riportato serve per "sganciarsi" dal framework e gestire i trigger attraverso 
                // il passaggio dei parametri JSON tramite POST e non (come effettuato dal framework) tramite GET. 
                // Necessario in quanto in questo caso la lunghezza della query string dei parametri passati tramite GET superava i limiti imposti.
                $("#Id_ambiente").unbind('change');
                $("#Id_ente").unbind('change');
                $("#Id_strut").unbind('change');
             $("#Id_ambiente").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("MonitoraggioIndiceAIP.html?operation=triggerFiltriUdMonitoraggioIndiceAIPId_ambienteOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

                $("#Id_ente").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("MonitoraggioIndiceAIP.html?operation=triggerFiltriUdMonitoraggioIndiceAIPId_enteOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

                $("#Id_strut").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("MonitoraggioIndiceAIP.html?operation=triggerFiltriUdMonitoraggioIndiceAIPId_strutOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
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
            <sl:contentTitle title="LISTA UNITA' DOCUMENTARIE PER PROCESSO GENERAZIONE INDICE AIP"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamentiSintetico")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.NAME%>" hideOperationButton="true" />                 
            <% }%>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.ID_ENTE%>" colSpan="2" />
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.ID_STRUT%>" colSpan="2" />
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.AA_KEY_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.CD_KEY_UNITA_DOC%>" colSpan="2" />            
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.NI_GG_STATO%>" colSpan="1" />  
                <sl:newLine />                             
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.CD_TI_EVE_STATO_ELENCO_VERS%>" colSpan="2" />         
                <sl:newLine />                             
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.FL_ELENCO_FISC%>" colSpan="2" />  
                <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.ID_ELENCO_VERS%>" colSpan="2" />
                 <sl:newLine />  
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.TI_STATO_UD_ELENCO_VERS%>" colSpan="2" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
             <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.GENERA_UD_MON_INDICE_AIPBUTTON%>" width="w20" />
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriUdMonitoraggioIndiceAIP.CONTA_UD_MON_INDICE_AIPBUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <h2><b><font color="#d3101c">Elenco unità documentarie versate</font></b></h2>
            <slf:list   name="<%= MonitoraggioIndiceAIPForm.UdMonitoraggioIndiceAIPList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioIndiceAIPForm.UdMonitoraggioIndiceAIPList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <h2><b><font color="#d3101c">Risultati conteggio</font></b></h2>
            <slf:list   name="<%= MonitoraggioIndiceAIPForm.ConteggioMonitoraggioIndiceAIPList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioIndiceAIPForm.ConteggioMonitoraggioIndiceAIPList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>