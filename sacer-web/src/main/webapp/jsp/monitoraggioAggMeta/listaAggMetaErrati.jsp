<%@page import="it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista aggiornamenti metadati errati" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA AGGIORNAMENTI METADATI ERRATI"/>

            <slf:fieldSet  borderHidden="false">
                <!-- Range di date -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.DT_INI_SES_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.HH_INI_SES_DA%>" name2="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.MM_INI_SES_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.DT_INI_SES_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.HH_INI_SES_A%>" name2="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.MM_INI_SES_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.TI_STATO_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.ID_CLASSE_ERR_SACER%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.ID_ERR_SACER%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMetaErrati.RICERCA_AGG_META_ERRATI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioAggMetaForm.AggMetaErratiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioAggMetaForm.AggMetaErratiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.AggMetaErratiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>