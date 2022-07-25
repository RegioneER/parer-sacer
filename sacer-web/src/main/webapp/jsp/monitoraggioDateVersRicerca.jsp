<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioTpiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Monitoraggio - Ricerca date versamento"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA DATE VERSAMENTO"/>

            <slf:fieldSet  borderHidden="false">
                <slf:lblField colSpan="2" controlWidth="w30" name="<%=MonitoraggioTpiForm.FiltriRicercaDateVers.DT_VERS_DA%>" />
                <slf:lblField colSpan="2" controlWidth="w30" name="<%=MonitoraggioTpiForm.FiltriRicercaDateVers.DT_VERS_A%>"  />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w30" name="<%=MonitoraggioTpiForm.FiltriRicercaDateVers.TI_STATO_DT_VERS%>" />
                <slf:lblField colSpan="2" controlWidth="w30" name="<%=MonitoraggioTpiForm.FiltriRicercaDateVers.FL_MIGRAZ%>"  />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=MonitoraggioTpiForm.FiltriRicercaDateVers.RICERCA_DATE_VERS%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioTpiForm.DateVersamentoList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioTpiForm.DateVersamentoList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.DateVersamentoList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>