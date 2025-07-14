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
    <sl:head title="Monitoraggio - Dettaglio data schedulazione job TPI">
        <script type="text/javascript" src="<c:url value="/js/sips/customCheckboxIconSubstitute.js"/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO DATA SCHEDULAZIONE JOB TPI"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= MonitoraggioTpiForm.DateSchedJobTpiList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.DataSchedDetail.DT_SCHED%>" />
                <slf:lblField colSpan="2" name="<%=MonitoraggioTpiForm.DataSchedDetail.TI_STATO_DT_SCHED%>" />
                <sl:newLine skipLine="true" />
                <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_PRESENZA_SECONDARIO%>" />
                <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_MIGRAZ_IN_CORSO%>" />
                <sl:newLine skipLine="true" />
                <slf:section name="<%=MonitoraggioTpiForm.AnomalieVersPrimSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_ARK_VERS_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_COPIA_VERS_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_BACKUP_VERS_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_MIGRATE_VERS_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_RI_ARK_VERS_PRIM%>" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=MonitoraggioTpiForm.AnomalieMigrazPrimSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_ARK_MIGRAZ_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_COPIA_MIGRAZ_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_BACKUP_MIGRAZ_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_MIGRATE_MIGRAZ_PRIM%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_RI_ARK_MIGRAZ_PRIM%>" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=MonitoraggioTpiForm.AnomalieVersSecSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_ARK_VERS_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_COPIA_VERS_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_BACKUP_VERS_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_MIGRATE_VERS_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_RI_ARK_VERS_SECOND%>" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=MonitoraggioTpiForm.AnomalieMigrazSecSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_ARK_MIGRAZ_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_COPIA_MIGRAZ_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_BACKUP_MIGRAZ_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_MIGRATE_MIGRAZ_SECOND%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioTpiForm.DataSchedDetail.FL_ANOM_RI_ARK_MIGRAZ_SECOND%>" />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <h2><%= MonitoraggioTpiForm.JobFileVersatiPrimarioList.DESCRIPTION%></h2>
            <slf:listNavBar name="<%= MonitoraggioTpiForm.JobFileVersatiPrimarioList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioTpiForm.JobFileVersatiPrimarioList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.JobFileVersatiPrimarioList.NAME%>" />
            <sl:newLine skipLine="true" />
            <h2><%= MonitoraggioTpiForm.JobFileMigratiPrimarioList.DESCRIPTION%></h2>
            <slf:listNavBar name="<%= MonitoraggioTpiForm.JobFileMigratiPrimarioList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioTpiForm.JobFileMigratiPrimarioList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.JobFileMigratiPrimarioList.NAME%>" />
            <sl:newLine skipLine="true" />
            <h2><%= MonitoraggioTpiForm.JobFileVersatiSecondarioList.DESCRIPTION%></h2>
            <slf:listNavBar name="<%= MonitoraggioTpiForm.JobFileVersatiSecondarioList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioTpiForm.JobFileVersatiSecondarioList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.JobFileVersatiSecondarioList.NAME%>" />
            <sl:newLine skipLine="true" />
            <h2><%= MonitoraggioTpiForm.JobFileMigratiSecondarioList.DESCRIPTION%></h2>
            <slf:listNavBar name="<%= MonitoraggioTpiForm.JobFileMigratiSecondarioList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioTpiForm.JobFileMigratiSecondarioList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioTpiForm.JobFileMigratiSecondarioList.NAME%>" />
            <sl:newLine skipLine="true" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
