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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="it.eng.parer.slite.gen.form.StrutSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio regola di acquisizione file" >
        <script type="text/javascript" src="<c:url value='/js/sips/customTipologiaSerieVincolataMessageBox.js'/>"></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <slf:messageBox />

        <c:if test="${!empty requestScope.customBox}">
            <div class="messages customBox ">
                <ul>
                    <li class="message warning ">L’operazione comporterà il ricalcolo di almeno una serie. Si intende procedere?</li>
                </ul>
            </div>
        </c:if>
        <div class="pulsantieraMB">
            <sl:pulsantiera >
                <slf:buttonList name="<%= StrutSerieForm.TipoSerieCustomMessageButtonList.NAME%>"/>
            </sl:pulsantiera>
        </div>

        <sl:content>
            <sl:contentTitle title="Dettaglio regola di acquisizione file"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%= StrutSerieForm.RegoleAcquisizioneDetail.NAME%>" hideBackButton="false" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false" hideOperationButton="false"/>
            <slf:messageBox />

            <sl:newLine skipLine="true"/>
            <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>

            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutSerieForm.SerieUniDoc.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NM_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
            </slf:section>
            <%--<slf:lblField name="<%=StrutSerieForm.RegoleAcquisizioneDetail.DL_VALORE%>" />--%>
            <sl:newLine skipLine="true"/>   

            <c:choose>
                <c:when test="${!(sessionScope['###_FORM_CONTAINER']['regoleAcquisizioneList'].status eq 'insert') 
                                && !(sessionScope['###_FORM_CONTAINER']['regoleAcquisizioneList'].status eq 'update')}">
                    <slf:listNavBar name="<%= StrutSerieForm.RegoleAcquisizioneList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutSerieForm.RegoleAcquisizioneList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutSerieForm.RegoleAcquisizioneList.NAME%>" />
                </c:when>
                <c:otherwise>
                    <slf:section name="<%=StrutSerieForm.DatiProfiloSection.NAME%>" styleClass="importantContainer">
                    <!--<div class="livello1"><b><font color="#d3101c"><%=StrutSerieForm.DatiProfiloList.DESCRIPTION%></font></b></div>-->
                        <%--<slf:listNavBar name="<%= StrutSerieForm.DatiProfiloList.NAME%>" pageSizeRelated="true"/>--%>
                        <slf:editableList name="<%= StrutSerieForm.DatiProfiloList.NAME%>" multiRowEdit="true" />
                        <%--<slf:listNavBar  name="<%= StrutSerieForm.DatiProfiloList.NAME%>" />--%>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=StrutSerieForm.DatiSpecTipoUdSection.NAME%>" styleClass="importantContainer">
                    <!--<div class="livello1"><b><font color="#d3101c"><%=StrutSerieForm.AttributiTipoUnitaDocList.DESCRIPTION%></font></b></div>-->
                        <%--<slf:listNavBar name="<%= StrutSerieForm.AttributiTipoUnitaDocList.NAME%>" pageSizeRelated="true"/>--%>
                        <slf:editableList name="<%= StrutSerieForm.AttributiTipoUnitaDocList.NAME%>" multiRowEdit="true" />
                        <%--<slf:listNavBar  name="<%= StrutSerieForm.AttributiTipoUnitaDocList.NAME%>" />--%>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=StrutSerieForm.DatiSpecTipoDocSection.NAME%>" styleClass="importantContainer">
                    <!--<div class="livello1"><b><font color="#d3101c"><%=StrutSerieForm.AttributiTipoDocList.DESCRIPTION%></font></b></div>-->
                        <%--<slf:listNavBar name="<%= StrutSerieForm.AttributiTipoDocList.NAME%>" pageSizeRelated="true"/>--%>
                        <slf:editableList name="<%= StrutSerieForm.AttributiTipoDocList.NAME%>" multiRowEdit="true" />
                        <%--<slf:listNavBar  name="<%= StrutSerieForm.AttributiTipoDocList.NAME%>" />--%>
                    </slf:section>
                </c:otherwise>
            </c:choose>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

