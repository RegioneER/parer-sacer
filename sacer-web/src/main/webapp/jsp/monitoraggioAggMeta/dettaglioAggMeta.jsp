<!--
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
-->

<%@page import="it.eng.parer.slite.gen.form.MonitoraggioAggMetaForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio aggiornamento metadati" >
            <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO AGGIORNAMENTO METADATI"/>
            <slf:listNavBarDetail name="<%=MonitoraggioAggMetaForm.AggMetaList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaTabs.NAME%>" tabElement="InformazioniPrincipaliAggMeta">                
                <slf:section name="<%=MonitoraggioAggMetaForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.ID_UPD_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.ID_UNITA_DOC%>" colSpan="4" />                        
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NM_AMBIENTE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NM_ENTE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NM_STRUT%>" colSpan="4" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=MonitoraggioAggMetaForm.UnitaDocumentariaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.AA_KEY_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.CD_KEY_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NM_TIPO_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NM_TIPO_DOC_PRINC%>" colSpan="4" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=MonitoraggioAggMetaForm.AggiornamentoSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.PG_UPD_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.DS_URN_UPD_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_PROFILO_ARCHIV%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_FASCICOLO_PRINC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_FASCICOLI_SEC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_PROFILO_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_LNK_UNITA_DOC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_DATI_SPEC%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_UPD_DATI_SPEC_MIGRAZ%>" colSpan="4" />
                </slf:section>
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaTabs.NAME%>" tabElement="InformazioniVersamentoAggMeta">                
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.CD_VERSIONE_XML%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NM_USERID%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.TS_INI_SES%>" colSpan="4" />                
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.TIPO_UPD_UNITA_DOC%>" colSpan="4" />                
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.FL_FORZA_UPD%>" colSpan="4" />                
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.NT_UPD%>" colSpan="4" />                
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.TI_STATO_UPD_ELENCO_VERS%>" colSpan="4" />                
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.TI_STATO_CONSERVAZIONE%>" colSpan="4" />                
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.ID_ELENCO_VERS%>" colSpan="4" />                
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaTabs.NAME%>" tabElement="IndiceSipAggiornamentoAggMeta">
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.DS_HASH_XML_RICH%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.DS_ALGO_HASH_XML_RICH%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.CD_ENCODING_HASH_XML_RICH%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.BL_XML_RICH%>" colSpan="4" controlWidth="w100" />
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaTabs.NAME%>" tabElement="RapportoVersamentoAggMeta">
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.DS_HASH_XML_RISP%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.DS_ALGO_HASH_XML_RISP%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.CD_ENCODING_HASH_XML_RISP%>" colSpan="4" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.BL_XML_RISP%>" colSpan="4" controlWidth="w100" />
            </slf:tab>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.DettaglioAggMeta.SCARICA_XML_AGG_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true" />
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaListsTabs.NAME%>" tabElement="ListaDocAggiornatiAggMeta">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.DocumentiAggiornatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.DocumentiAggiornatiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.DocumentiAggiornatiList.NAME%>" /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaListsTabs.NAME%>" tabElement="ListaCompAggiornatiAggMeta">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.ComponentiAggiornatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.ComponentiAggiornatiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.ComponentiAggiornatiList.NAME%>" /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaListsTabs.NAME%>" tabElement="ListaAggMetaRisoltiAggMeta">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.AggMetaRisoltiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.AggMetaRisoltiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.AggMetaRisoltiList.NAME%>" /> 
            </slf:tab>
            <slf:tab  name="<%= MonitoraggioAggMetaForm.AggMetaListsTabs.NAME%>" tabElement="ListaWarnRilevatiAggMeta">
                <slf:listNavBar name="<%= MonitoraggioAggMetaForm.WarningRilevatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioAggMetaForm.WarningRilevatiList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.WarningRilevatiList.NAME%>" /> 
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
