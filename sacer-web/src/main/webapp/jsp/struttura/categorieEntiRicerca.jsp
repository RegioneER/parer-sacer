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

<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Ricerca Categorie Enti" />

    <sl:body>

        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <sl:contentTitle title="Ricerca Categorie Enti"/>
            <slf:messageBox />      
            <slf:fieldSet>
                <slf:lblField name="<%=StruttureForm.CategorieEnti.CD_CATEG_ENTE%>" colSpan="4"  />
                <sl:newLine />   
                <slf:lblField name="<%=StruttureForm.CategorieEnti.DS_CATEG_ENTE%>" colSpan="4" />
                <sl:newLine />   
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <sl:pulsantiera>
                <slf:lblField  name="<%=StruttureForm.CategorieEnti.RICERCA_CATEGORIE_ENTE_BUTTON%>"  width="w50" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <slf:listNavBar name="<%= StruttureForm.CategorieEntiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StruttureForm.CategorieEntiList.NAME%>" />
            <slf:listNavBar  name="<%= StruttureForm.CategorieEntiList.NAME%>" />
                
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
