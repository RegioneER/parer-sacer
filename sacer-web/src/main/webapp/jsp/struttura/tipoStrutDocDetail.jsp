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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipoStrutForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo struttura documento" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi Struttura Documento" />
        <sl:menu />
        <sl:content>


            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo struttura documento"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoStrutDocList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipoStrutForm.TipoStrutDoc.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoStrutDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipoStrutForm.TipoStrutDocList.NAME%>" />   
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >

                <slf:section name="<%=StrutTipoStrutForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <%--                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.NM_STRUT%>" colSpan= "4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.DS_STRUT%>"  colSpan= "4" controlWidth="w40" />--%>
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.STipoStrutDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.NM_TIPO_STRUT_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.DS_TIPO_STRUT_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.DT_SOPPRES%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                </slf:section>
                <sl:newLine skipLine="true"/>
            </slf:fieldSet>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoStrutDoc'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.LOG_EVENTI_TIPO_STRUT_DOC%>" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>


                <div class="livello1"><b style="color: #d3101c;">Tipi componente ammessi</b></div>
                <sl:newLine skipLine="true"/>

                <slf:listNavBar name="<%= StrutTipoStrutForm.TipoCompDocList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StrutTipoStrutForm.TipoCompDocList.NAME%>"  />
                <slf:listNavBar  name="<%= StrutTipoStrutForm.TipoCompDocList.NAME%>" />

                <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Tipi documento ammessi</b></div>
                <sl:newLine skipLine="true"/>

                <slf:listNavBar name="<%= StrutTipoStrutForm.TipoDocAmmessoDaTipoStrutDocList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StrutTipoStrutForm.TipoDocAmmessoDaTipoStrutDocList.NAME%>"  />
                <slf:listNavBar  name="<%= StrutTipoStrutForm.TipoDocAmmessoDaTipoStrutDocList.NAME%>" />
            </c:if>


        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

