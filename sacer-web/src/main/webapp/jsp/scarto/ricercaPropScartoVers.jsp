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

<%@ page import="it.eng.parer.slite.gen.form.ScartoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ScartoForm.FiltriRicercaPropScartoVers.DESCRIPTION%>" >
        <style>
            /* Centratura delle colonne: Codice Proposta (4) e Data Creazione (5) */
            #PropScartoVersList tbody tr td:nth-child(4), #PropScartoVersList thead tr th:nth-child(3),
            #PropScartoVersList tbody tr td:nth-child(5), #PropScartoVersList thead tr th:nth-child(4) {
                text-align: center;
            }
        </style>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            
            <sl:contentTitle title='<%=ScartoForm.FiltriRicercaPropScartoVers.DESCRIPTION%>'/>
            
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
            <slf:section name="<%=ScartoForm.FiltriPropostaScartoSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.ID_AMBIENTE%>" colSpan="4" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.ID_ENTE%>" colSpan="4" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.ID_STRUT%>" colSpan="4" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.CD_PROP_SCARTO_VERS%>" colSpan="2" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.DT_CREAZIONE_PROP_SCARTO_VERS_DA%>" colSpan="1"/>
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.DT_CREAZIONE_PROP_SCARTO_VERS_A%>" colSpan="1" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.DT_ULTIMA_MOD_PROP_SCARTO_VERS_DA%>" colSpan="1"/>
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.DT_ULTIMA_MOD_PROP_SCARTO_VERS_A%>" colSpan="1" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.TI_STATO_PROP_SCARTO_VERS%>" colSpan="4" /><sl:newLine />
            </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=ScartoForm.FiltriRicercaPropScartoVers.RICERCA_PROP_SCARTO_VERS%>"  colSpan="4" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:section name="<%=ScartoForm.ListaProposteSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= ScartoForm.PropScartoVersList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= ScartoForm.PropScartoVersList.NAME%>" />
                <slf:listNavBar  name="<%= ScartoForm.PropScartoVersList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
