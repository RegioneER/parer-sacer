<%@page import="it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio unità documentaria derivante da aggiornamenti metadati falliti" >
    </sl:head>
    <link media="screen" type="text/css" rel="stylesheet" href="<c:url value='/css/snippet/jquery.snippet.anjuta.css' />" />
    <script src="<c:url value='/js/snippet/jquery.snippet.min.js' />" type="text/javascript"></script>    
    <script src="<c:url value='/js/snippet/launch.js' />" type="text/javascript"></script>    
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO UNITÀ DOCUMENTARIA DERIVANTE DA AGGIORNAMENTI METADATI FALLITI"/>
            <slf:listNavBarDetail name="<%=MonitoraggioAggMetaForm.UnitaDocAggMetaFallitiList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= MonitoraggioAggMetaForm.UnitaDocAggMetaFallitiTabs.NAME%>" tabElement="InformazioniPrincipaliUnitaDocAggFalliti">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.ID_UPD_UNITA_DOC_KO%>" colSpan="3" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.DS_ENTE_STRUT%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.DS_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.NM_TIPO_UNITA_DOC_LAST%>" colSpan="3" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.NM_TIPO_DOC_PRINC_LAST%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.CD_VERSIONE_WS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.CD_DS_ERR_PRINC%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.CD_CONTROLLO_WS_PRINC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.TI_STATO_UPD_UD_KO%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.NM_USERID%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.DS_TS_INI_LAST_SES%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.DS_TS_FINE_LAST_SES%>" colSpan="2" />
                    <sl:newLine />
                </slf:fieldSet>
                <sl:newLine skipLine="true" />
                <sl:pulsantiera>
                    <!-- piazzo il bottone -->
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.SCARICA_XML_AGG_FALLITO_LAST_BUTTON%>" width="w25" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true" />
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.UnitaDocAggMetaFallitiTabs.NAME%>" tabElement="IndiceSipAggiornamentoUnitaDocAggFalliti">
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.BL_XML_RICH_LAST%>" colSpan="4" controlWidth="w100" />
                <sl:newLine /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.UnitaDocAggMetaFallitiTabs.NAME%>" tabElement="EsitoNegativoVersamentoUnitaDocAggFalliti">
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioUnitaDocAggMetaFallito.BL_XML_RISP_LAST%>" colSpan="4" controlWidth="w100" />
                <sl:newLine /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.UnitaDocAggMetaFallitiTabs.NAME%>" tabElement="ListaUlterioriErroriUnitaDocAggFalliti">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.UlterioriErroriUnitaDocAggMetaFallitiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.UlterioriErroriUnitaDocAggMetaFallitiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.UlterioriErroriUnitaDocAggMetaFallitiList.NAME%>" /> 
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>