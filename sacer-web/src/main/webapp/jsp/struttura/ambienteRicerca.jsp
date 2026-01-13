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

<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Gestione Ambienti" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Gestione Ambienti"/>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.VisAmbiente.NM_AMBIENTE%>" colSpan="4" controlWidth="w40" />
                <sl:newLine skipLine="true"/>
            </slf:fieldSet>

            <sl:pulsantiera>

                <slf:lblField  name="<%=AmbienteForm.VisAmbiente.VIS_AMBIENTE_BUTTON%>" colSpan="4" />

            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= AmbienteForm.AmbientiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= AmbienteForm.AmbientiList.NAME%>" />
            <slf:listNavBar  name="<%= AmbienteForm.AmbientiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
