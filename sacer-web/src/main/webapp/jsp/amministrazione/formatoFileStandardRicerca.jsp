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

<%@ page import="it.eng.parer.slite.gen.form.FormatiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=FormatiForm.VisFormatoFileStandard.DESCRIPTION%>" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="<%=FormatiForm.VisFormatoFileStandard.DESCRIPTION%>"/>

            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=FormatiForm.VisFormatoFileStandard.NM_FORMATO_FILE_STANDARD%>" colSpan="2" labelWidth="w20" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.VisFormatoFileStandard.NM_MIMETYPE_FILE%>" colSpan="2" labelWidth="w20" controlWidth="w40" />
                <sl:newLine />   
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>


            <sl:pulsantiera>
                <slf:lblField  name="<%=FormatiForm.VisFormatoFileStandard.VIS_FORMATO_BUTTON%>" colSpan="4" />
                <slf:lblField  name="<%=FormatiForm.VisFormatoFileStandard.EXTRACT_CSV_BUTTON%>" colSpan="4" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%=FormatiForm.FormatoFileStandardList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%=FormatiForm.FormatoFileStandardList.NAME%>" />
            <slf:listNavBar  name="<%=FormatiForm.FormatoFileStandardList.NAME%>" />


        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
