<%@page import="it.eng.parer.slite.gen.form.MonitoraggioIndiceAIPForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista strutture per processo generazione indice AIP" >
    </sl:head>
    <link media="screen" type="text/css" rel="stylesheet" href="<c:url value='/css/snippet/jquery.snippet.anjuta.css' />" />
    <script src="<c:url value='/js/snippet/jquery.snippet.min.js' />" type="text/javascript"></script>    
    <script src="<c:url value='/js/snippet/launch.js' />" type="text/javascript"></script>    
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA STRUTTURE PER AGGIORNAMENTO INDICE AIP"/>
            <slf:listNavBarDetail name="<%=MonitoraggioIndiceAIPForm.StruttureMonitoraggioIndiceAIPList.NAME%>" />
            <sl:newLine skipLine="true"/>
             <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.ID_STRUT%>" colSpan="2" />
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioIndiceAIPForm.FiltriStruttureMonitoraggioIndiceAIP.CD_TI_EVE_STATO_ELENCO_VERS%>" colSpan="1" />              
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:newLine skipLine="true" />
                <slf:listNavBar name="<%= MonitoraggioIndiceAIPForm.StruttureMonitoraggioIndiceAIPList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioIndiceAIPForm.StruttureMonitoraggioIndiceAIPList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioIndiceAIPForm.StruttureMonitoraggioIndiceAIPList.NAME%>" /> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>