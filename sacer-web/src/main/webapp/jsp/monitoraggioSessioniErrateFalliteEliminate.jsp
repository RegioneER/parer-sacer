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

<%-- 
    Document   : monitoraggioSessioniEliminateRicerca
    Created on : 20 set 2024, 15:53:39
    Author     : gpiccioli
--%>

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista sessioni errate" >
        <script type="text/javascript" src="<c:url value="/js/sips/customCalcStrutVersMessageBox.js"/>" ></script>
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="RIEPILOGO SESSIONI FALLITE CANCELLATE"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.SESS_ERRATE_FALLITE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.DATA_ELAB_DA%>" colSpan="1" controlWidth="w70" labelWidth="w20" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.DATA_ELAB_A%>" colSpan="1" controlWidth="w70" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.DATA_RIF_DA%>" colSpan="1" controlWidth="w70" labelWidth="w20" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.DATA_RIF_A%>" colSpan="1" controlWidth="w70" labelWidth="w20" /><sl:newLine />

            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessioniErrateFalliteEliminate.CERCA_SESSIONI_ERRATE_FALLITE_ELIMINATE%>"  width="w25" />
            </sl:pulsantiera>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.SessioniErrateFalliteEliminateList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioForm.SessioniErrateFalliteEliminateList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.SessioniErrateFalliteEliminateList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
