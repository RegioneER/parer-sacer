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

<%@ page import="it.eng.parer.slite.gen.form.UtilizzoMicroserviziForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Utilizzo Microservizi" ></sl:head>
    <sl:body>
        <sl:header showChangeOrganizationBtn="false"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />    
            <sl:contentTitle title="Utilizzo microservizi"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Dati di accesso ai microservizi" >
                <slf:lblField name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziFields.NM_URL_TOKEN%>" colSpan="4" controlWidth="w100"/> 
            </slf:fieldSet> 
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziSection.NAME%>" styleClass="importantContainer">
                <!--  piazzo la lista con i risultati -->
                <slf:listNavBar name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziList.NAME%>" />
                <slf:listNavBar  name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziList.NAME%>" />
            </slf:section>
        <sl:footer />
        </sl:content>
    </sl:body>
</sl:html>
