<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@page import="it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio aggiornamento metadati errato" >
       <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO AGGIORNAMENTO METADATI ERRATO"/>
            <slf:listNavBarDetail name="<%=MonitoraggioAggMetaForm.AggMetaErratiList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaErratiTabs.NAME%>" tabElement="InformazioniPrincipaliErrati">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.ID_SES_UPD_UNITA_DOC_ERR%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.DS_TS_INI_SES%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.DS_TS_FINE_SES%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.NM_AMBIENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.NM_ENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.NM_STRUT%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.FL_ESISTE_STRUT%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.AA_KEY_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.CD_KEY_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.FL_ESISTE_REG%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.NM_TIPO_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.FL_ESISTE_TIPO_UNITA_DOC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.NM_TIPO_DOC_PRINC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.FL_ESISTE_TIPO_DOC_PRINC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.CD_DS_ERR_PRINC%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.CD_CONTROLLO_WS_PRINC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.TI_STATO_SES%>" colSpan="2" />
                    <sl:newLine />
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaErratiTabs.NAME%>" tabElement="IndiceSipAggiornamentoErrati">
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.BL_XML_RICH%>" colSpan="4" controlWidth="w100" />
                <sl:newLine /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaErratiTabs.NAME%>" tabElement="EsitoNegativoVersamentoErrati">
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.BL_XML_RISP%>" colSpan="4" controlWidth="w100" />
                <sl:newLine /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaErratiTabs.NAME%>" tabElement="ListaUlterioriErroriErrati">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.UlterioriErroriErratiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.UlterioriErroriErratiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.UlterioriErroriErratiList.NAME%>" /> 
            </slf:tab>
            <sl:newLine skipLine="true" />
                <sl:pulsantiera>
                    <!-- piazzo il bottone -->
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMetaErrato.SCARICA_XML_AGG_ERRATO_BUTTON%>" width="w25" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
