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

<%@ page import="it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="<%= CriteriRaggruppamentoForm.CriterioRaggrList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="<%= CriteriRaggruppamentoForm.CriterioRaggrList.DESCRIPTION%>" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="GESTIONE CRITERI DI RAGGRUPPAMENTO UNITÀ DOCUMENTARIE" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.FL_CRITERIO_RAGGR_STANDARD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.FL_CRITERIO_RAGGR_FISC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.TI_VALID_ELENCO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.TI_MOD_VALID_ELENCO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.TI_GEST_ELENCO_CRITERIO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.NM_CRITERIO_RAGGR%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_REGISTRO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.AA_KEY_UNITA_DOC%>" colSpan="1" controlWidth="w30"/>
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.CRITERIO_ATTIVO%>" colSpan="2" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=CriteriRaggruppamentoForm.FiltriCriteriRaggr.RICERCA_CRITERI_RAGGR_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" />
            <slf:listNavBar  name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
