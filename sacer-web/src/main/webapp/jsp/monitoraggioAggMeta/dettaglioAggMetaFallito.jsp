<%@page import="it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio aggiornamento metadati fallito" >
          <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO AGGIORNAMENTO METADATI FALLITO"/>
            <slf:listNavBarDetail name="<%=MonitoraggioAggMetaForm.AggMetaFallitiList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaFallitiTabs.NAME%>" tabElement="InformazioniPrincipaliFalliti">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.ID_SES_UPD_UNITA_DOC_KO%>" colSpan="3" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.DS_ENTE_STRUT%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.DS_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.NM_TIPO_UNITA_DOC%>" colSpan="3" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.NM_TIPO_DOC_PRINC%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.CD_VERSIONE_WS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.CD_DS_ERR_PRINC%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.CD_CONTROLLO_WS_PRINC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.TI_STATO_SES_UPD_KO%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.NM_USERID%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.DS_TS_INI_SES%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.DS_TS_FINE_SES%>" colSpan="2" />
                    <sl:newLine />
                </slf:fieldSet>
                <sl:newLine skipLine="true" />
                <sl:pulsantiera>
                    <!-- piazzo il bottone -->
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.SCARICA_XML_AGG_FALLITO_BUTTON%>" width="w25" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true" />
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaFallitiTabs.NAME%>" tabElement="IndiceSipAggiornamentoFalliti">
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.BL_XML_RICH%>" colSpan="4" controlWidth="w100" />
                <sl:newLine /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaFallitiTabs.NAME%>" tabElement="EsitoNegativoVersamentoFalliti">
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaFallito.BL_XML_RISP%>" colSpan="4" controlWidth="w100" />
                <sl:newLine /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaFallitiTabs.NAME%>" tabElement="ListaUlterioriErroriFalliti">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.UlterioriErroriList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.UlterioriErroriList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.UlterioriErroriList.NAME%>" /> 
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>