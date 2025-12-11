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
    <sl:head  title="Monitoraggio - Lista aggiornamenti metadati falliti" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA AGGIORNAMENTI METADATI FALLITI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioAggMetaForm.FiltriRicercaMonitoraggioAggMeta.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ID_REGISTRO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />

                <slf:section name="<%=MonitoraggioAggMetaForm.ChiaveAggMetaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                </slf:section> 
                <!-- Range di date -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.DT_INI_SES_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.HH_INI_SES_DA%>" name2="<%=MonitoraggioAggMetaForm.FiltriAggMeta.MM_INI_SES_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.DT_INI_SES_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.HH_INI_SES_A%>" name2="<%=MonitoraggioAggMetaForm.FiltriAggMeta.MM_INI_SES_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.TI_STATO_SES_UPD_KO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.CD_CLASSE_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.CD_ERR%>" colSpan="4" />
                <sl:newLine />

            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.RICERCA_AGG_META_FALLITI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioAggMetaForm.AggMetaFallitiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioAggMetaForm.AggMetaFallitiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioAggMetaForm.AggMetaFallitiList.NAME%>" />
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.VERIFICA_AGG_META_FALLITI_BUTTON%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ASSEGNA_NON_RISOLUB_AGG_META_FALLITI_BUTTON%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ASSEGNA_NON_VERIF_AGG_META_FALLITI_BUTTON%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioAggMetaForm.FiltriAggMeta.ASSEGNA_VERIF_AGG_META_FALLITI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
