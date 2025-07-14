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
    <sl:head title="Monitoraggio - Ricerca date schedulazioni job TPI">
        <script type="text/javascript" src="<c:url value="/js/sips/customCheckboxIconSubstitute.js"/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA DATE SCHEDULAZIONI JOB TPI"/>

            <slf:fieldSet  borderHidden="false">
                <slf:lblField name="<%=MonitoraggioTpiForm.FiltriRicercaDateSchedJobTpi.DT_SCHED_DA %>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioTpiForm.FiltriRicercaDateSchedJobTpi.DT_SCHED_A %>" colSpan="2" />
                <sl:newLine />
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.FiltriRicercaDateSchedJobTpi.TI_STATO_DT_SCHED %>" />
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.FiltriRicercaDateSchedJobTpi.FL_ANOMALIA %>"  />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=MonitoraggioTpiForm.FiltriRicercaDateSchedJobTpi.RICERCA_DATE_SCHED_JOB_TPI%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioTpiForm.DateSchedJobTpiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioTpiForm.DateSchedJobTpiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.DateSchedJobTpiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
