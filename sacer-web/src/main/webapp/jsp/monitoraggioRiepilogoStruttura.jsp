<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Riepilogo struttura" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RIEPILOGO PER STRUTTURA"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioForm.RiepilogoStruttura.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.RiepilogoStruttura.RICERCA_RIEPILOGO_STRUTTURA_BUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.RiepilogoStrutturaList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioForm.RiepilogoStrutturaList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.RiepilogoStrutturaList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>