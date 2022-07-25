<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Esame contenuto SACER" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="ESAME CONTENUTO SACER"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:section name="<%=MonitoraggioForm.AmbitoTerritorialeSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_AMBITO_TERRIT_LIVELLO_1%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_AMBITO_TERRIT_LIVELLO_2%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_AMBITO_TERRIT_LIVELLO_3%>" colSpan="2" />
                </slf:section>                
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_CATEG_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_CATEG_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_SUB_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_REGISTRO_UNITA_DOC%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.AA_KEY_UNITA_DOC%>" colSpan="1" controlWidth="w30" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_CATEG_TIPO_UNITA_DOC%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_SOTTOCATEG_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.DT_RIF_DA%>" colSpan="2" controlWidth="w0" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.DT_RIF_A%>" colSpan="2" controlWidth="w0"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioForm.ContenutoSacerTotaliUdDocComp.NUM_UD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.ContenutoSacerTotaliUdDocComp.NUM_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.ContenutoSacerTotaliUdDocComp.NUM_COMP%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.ContenutoSacerTotaliUdDocComp.DIM_BYTES%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.RICERCA_CONTENUTO_SACER%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriContenutoSacer.PULISCI_CONTENUTO_SACER%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.ContenutoSacerList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioForm.ContenutoSacerList.NAME%>" />
            <slf:listNavBar name="<%= MonitoraggioForm.ContenutoSacerList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>