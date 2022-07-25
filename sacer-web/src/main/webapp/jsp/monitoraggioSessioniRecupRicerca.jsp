<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Monitoraggio - Ricerca sessioni recupero"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA SESSIONI RECUPERO"/>

            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.DT_OPER_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.ORE_DT_OPER_DA%>" name2="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.MINUTI_DT_OPER_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.DT_OPER_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.ORE_DT_OPER_A%>" name2="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.MINUTI_DT_OPER_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.ID_STRUT%>" colSpan="2" />
                <sl:newLine />                
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.AA_KEY_UNITA_DOC%>" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.CD_KEY_UNITA_DOC%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.TI_STATO%>" />
                <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.TI_SESSIONE%>" />
                <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.NM_USERID%>" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriRicercaSessioniRecupero.RICERCA_SESSIONI_RECUP%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.MonitSessioniRecupList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioForm.MonitSessioniRecupList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.MonitSessioniRecupList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>