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

<%@ page import="it.eng.parer.slite.gen.form.StrutSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio elemento di descrizione sul tipo di serie" >
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <sl:contentTitle title="Dettaglio elemento di descrizione sul tipo di serie"/>
            <slf:messageBox />

            <sl:newLine skipLine="true"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['noteTipoSerieList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutSerieForm.NoteTipoSerieDetail.NAME%>" hideBackButton="false"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['noteTipoSerieList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutSerieForm.NoteTipoSerieList.NAME%>" />   
            </c:if>

            <sl:newLine skipLine="true"/>
            <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.NM_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.NM_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.NM_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>

            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutSerieForm.SerieUniDoc.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
            </slf:section>
            <slf:section name="<%=StrutSerieForm.NotaSulTipoDiSerie.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.PG_NOTA_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.ID_TIPO_NOTA_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.DT_NOTA_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.NoteTipoSerieDetail.DS_NOTA_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

