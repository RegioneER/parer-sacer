<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioTpiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Monitoraggio - Dettaglio data versamento"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO DATA VERSAMENTO"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= MonitoraggioTpiForm.DateVersamentoList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.DT_VERS%>" />
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.TI_STATO_DT_VERS%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_MIGRAZ%>" />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_PRESENZA_SECONDARIO%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_ARK%>" />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_FILE_NO_ARK%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_ARK_SECONDARIO%>" />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_FILE_NO_ARK_SECONDARIO%>" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=MonitoraggioTpiForm.DateVersamentoDetail.CALL_RI_ARK%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" pageSizeRelated="true"/>
			<slf:list name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>