<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Visualizza Schedulazioni Job" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="VISUALIZZA SCHEDULAZIONI JOB"/>
            
            <c:if test="${sessionScope.backToRicercaJob != null}">
                <slf:fieldBarDetailTag name="<%=MonitoraggioForm.FiltriJobSchedulati.NAME%>" hideBackButton="false"/> 
            </c:if>   
            
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.NM_JOB%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.DT_REG_LOG_JOB_DA%>" colSpan="1" controlWidth="w70" />                
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriJobSchedulati.ORE_DT_REG_LOG_JOB_DA%>" name2="<%=MonitoraggioForm.FiltriJobSchedulati.MINUTI_DT_REG_LOG_JOB_DA%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.DT_REG_LOG_JOB_A%>" colSpan="1" controlWidth="w70"  />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriJobSchedulati.ORE_DT_REG_LOG_JOB_A%>" name2="<%=MonitoraggioForm.FiltriJobSchedulati.MINUTI_DT_REG_LOG_JOB_A%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.ID_AMBIENTE%>" colSpan="3"  />
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.ID_ENTE%>" colSpan="3"  />
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.ID_STRUT%>" colSpan="3"  />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.RICERCA_JOB_SCHEDULATI%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriJobSchedulati.PULISCI_JOB_SCHEDULATI%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del risultato -->
                <slf:lblField name="<%=MonitoraggioForm.StatoJob.ATTIVO%>" width="w20" labelWidth="w70" controlWidth="w30"/>
                <slf:lblField name="<%=MonitoraggioForm.StatoJob.DT_REG_LOG_JOB_INI%>" width="w20" labelWidth="w10" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.StatoJob.DT_PROSSIMA_ATTIVAZIONE%>" labelWidth="w40"  width = "w50" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.JobSchedulatiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.JobSchedulatiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.JobSchedulatiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>