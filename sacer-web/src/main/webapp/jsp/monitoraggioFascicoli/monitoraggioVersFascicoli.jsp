<%@page import="it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>
<sl:html>
    <sl:head  title="Monitoraggio - Riepilogo versamenti fascicoli" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="RIEPILOGO VERSAMENTI FASCICOLI"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.ID_TIPO_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.GENERA_RIEPILOGO_VERS_FASCICOLI_BUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!-- Sezione fascicoli versati -->
            <%@ include file="fascicoliVersati.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- Sezione fascicoli da versamenti falliti -->
            <%@ include file="fascicoliVersamentiFalliti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <sl:newLine skipLine="true"/>

        </sl:content>
    </sl:body>
</sl:html>