<%@page import="it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista fascicoli versati" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA FASCICOLI VERSATI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_TIPO_FASCICOLO%>" colSpan="2" />
                <sl:newLine />

                <slf:section name="<%=MonitoraggioFascicoliForm.ChiaveFascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.AA_FASCICOLO%>"  />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_KEY_FASCICOLO%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.AA_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.AA_FASCICOLO_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_FASCICOLO_A%>" colSpan="1"/>
                </slf:section> 
                <!-- Range di date -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ORE_VERS_DA%>" name2="<%=MonitoraggioFascicoliForm.FiltriFascicoli.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ORE_VERS_A%>" name2="<%=MonitoraggioFascicoliForm.FiltriFascicoli.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.TI_STATO_FASC_ELENCO_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.TI_STATO_CONSERVAZIONE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.FL_SES_FASCICOLO_KO%>" colSpan="2" />
                <sl:newLine />
                
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.RICERCA_FASCICOLI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioFascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioFascicoliForm.FascicoliList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioFascicoliForm.FascicoliList.NAME%>" />
            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>