<%@ page import="it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="<%= CriteriRaggruppamentoForm.CriterioRaggrList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="<%= CriteriRaggruppamentoForm.CriterioRaggrList.DESCRIPTION%>" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="GESTIONE CRITERI DI RAGGRUPPAMENTO UNITÀ DOCUMENTARIE" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.FL_CRITERIO_RAGGR_STANDARD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.FL_CRITERIO_RAGGR_FISC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.TI_VALID_ELENCO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.TI_MOD_VALID_ELENCO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.TI_GEST_ELENCO_CRITERIO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.NM_CRITERIO_RAGGR%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_REGISTRO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.AA_KEY_UNITA_DOC%>" colSpan="1" controlWidth="w30"/>
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.CRITERIO_ATTIVO%>" colSpan="2" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.RICERCA_CRITERI_RAGGR_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" />
            <slf:listNavBar  name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
