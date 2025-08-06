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
    <sl:head title="<%=ModelliSerieForm.DatiSpecDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <slf:messageBox />
        <sl:content>
            <sl:contentTitle title="<%=ModelliSerieForm.DatiSpecDetail.DESCRIPTION%>"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%= ModelliSerieForm.DatiSpecDetail.NAME%>" hideBackButton="true" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false"/>
            <slf:messageBox />

            <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            <slf:section name="<%=ModelliSerieForm.InfoModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            </slf:section>
            <%--<slf:listNavBar name="<%= ModelliSerieForm.FiltriDatiSpecList.NAME%>" pageSizeRelated="true"/>--%>
            <slf:nestedList name="<%= ModelliSerieForm.FiltriDatiSpecList.NAME%>" subListName="<%= ModelliSerieForm.VersioneXsdDatiSpecList.NAME%>" multiRowEdit="true"/>
            <%--<slf:listNavBar name="<%= ModelliSerieForm.FiltriDatiSpecList.NAME%>" />--%>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

