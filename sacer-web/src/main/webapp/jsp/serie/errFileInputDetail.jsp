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

<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.ErroriFileInputDetail.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.ErroriFileInputDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= SerieUDForm.ErroriFileInputList.NAME%>" />  
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />

                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.TI_STATO_VER_SERIE_COR%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.TI_STATO_SERIE_COR%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.ContenutoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.TI_STATO_CONTENUTO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.FL_ERR_CONTENUTO%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.NI_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.CD_FIRST_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.CD_LAST_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.FL_JOB_BLOCCATO%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.ErroreSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.NI_REC_ERR%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.TI_ERR_REC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ErroriFileInputDetail.DS_REC_ERR%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:listNavBar name="<%= SerieUDForm.ErroriFileInputUdList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= SerieUDForm.ErroriFileInputUdList.NAME%>" />
            <slf:listNavBar name="<%= SerieUDForm.ErroriFileInputUdList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
