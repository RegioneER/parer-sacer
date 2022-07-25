<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioTpiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Monitoraggio - Dettaglio path versamento"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO PATH VERSAMENTO"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <slf:section name="<%=MonitoraggioTpiForm.DataVersSection.NAME%>" styleClass="importantContainer">
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
                </slf:section>
                <slf:section name="<%=MonitoraggioTpiForm.PathVersSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.DL_PATH%>" />
                    <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.NI_FILE_PATH%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.FL_PATH_ARK%>" />
                    <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.FL_PATH_FILE_NO_ARK%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="4" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.DL_ARK%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.FL_PATH_ARK_SECONDARIO%>" />
                    <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.FL_PATH_FILE_NO_ARK_SECONDARIO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="4" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.DL_ARK_SECONDARIO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.NI_FILE_PATH_ARK%>" />
                    <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.PathVersamentoDetail.NI_FILE_PATH_ARK_SECONDARIO%>" />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <h2><%= MonitoraggioTpiForm.FileNoArkPrimarioList.DESCRIPTION%></h2>
            <sl:newLine />
            <slf:listNavBar name="<%= MonitoraggioTpiForm.FileNoArkPrimarioList.NAME%>" pageSizeRelated="true"/>
			<slf:list name="<%= MonitoraggioTpiForm.FileNoArkPrimarioList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.FileNoArkPrimarioList.NAME%>" />

            <sl:newLine skipLine="true" />
            <h2><%= MonitoraggioTpiForm.FileNoArkSecondarioList.DESCRIPTION%></h2>
            <sl:newLine />
            <slf:listNavBar name="<%= MonitoraggioTpiForm.FileNoArkSecondarioList.NAME%>" pageSizeRelated="true"/>
			<slf:list name="<%= MonitoraggioTpiForm.FileNoArkSecondarioList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.FileNoArkSecondarioList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>