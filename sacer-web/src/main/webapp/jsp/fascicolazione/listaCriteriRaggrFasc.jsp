<%@ page import="it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.DESCRIPTION%>" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Gestione criteri di raggruppamento fascicoli" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.FL_CRITERIO_RAGGR_STANDARD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.NM_CRITERIO_RAGGR%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.ID_TIPO_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.AA_FASCICOLO%>" colSpan="1" controlWidth="w30"/>
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.CD_COMPOSITO_VOCE_TITOL%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.CRITERIO_ATTIVO%>" colSpan="2" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli.RICERCA_CRITERI_RAGGR_FASCICOLI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.NAME%>" />
            <slf:listNavBar  name="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>