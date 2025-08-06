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

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioTpiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Monitoraggio - Dettaglio data versamento"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO DATA VERSAMENTO"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= MonitoraggioTpiForm.DateVersamentoList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.DT_VERS%>" />
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.TI_STATO_DT_VERS%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_MIGRAZ%>" />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_PRESENZA_SECONDARIO%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_ARK%>" />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_FILE_NO_ARK%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_ARK_SECONDARIO%>" />
                <slf:lblField colSpan="2" controlWidth="w20" name="<%=MonitoraggioTpiForm.DateVersamentoDetail.FL_FILE_NO_ARK_SECONDARIO%>" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=MonitoraggioTpiForm.DateVersamentoDetail.CALL_RI_ARK%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" pageSizeRelated="true"/>
			<slf:list name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.DateVersamentoDetailPathList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
