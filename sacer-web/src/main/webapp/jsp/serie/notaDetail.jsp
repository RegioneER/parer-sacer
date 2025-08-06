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

<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.NotaDetail.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.NotaDetail.DESCRIPTION%>"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['noteList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= SerieUDForm.NotaDetail.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['noteList'].status eq 'insert'}"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['noteList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= SerieUDForm.NoteList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieDetail.TI_STATO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.NotaSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.NotaDetail.ID_NOTA_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.NotaDetail.ID_TIPO_NOTA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.NotaDetail.PG_NOTA_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.NotaDetail.DS_NOTA_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.NotaDetail.DT_NOTA_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.NotaDetail.NM_USERID%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
