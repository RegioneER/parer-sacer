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

<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.ModelliTipiSerieList.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=ModelliSerieForm.ModelliTipiSerieList.DESCRIPTION%>"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%=ModelliSerieForm.ModelliTipiSerieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%=ModelliSerieForm.ModelliTipiSerieList.NAME%>" abbrLongList="true"/>
            <slf:listNavBar name="<%=ModelliSerieForm.ModelliTipiSerieList.NAME%>" /> 

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
