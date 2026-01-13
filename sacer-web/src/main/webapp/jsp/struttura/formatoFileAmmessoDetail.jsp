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
    <sl:head title="Dettaglio Formato File Ammesso" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio Formato File Ammesso"/>

            <c:choose>
                <c:when test="${sessionScope['###_FORM_CONTAINER']['formatoFileAmmessoList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= StrutTipoStrutForm.FormatoFileAmmesso.NAME%>" /> 
                </c:when>   
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" /> 
                </c:otherwise>
            </c:choose>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet>
                <slf:section name="<%=StrutTipoStrutForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.STRUTTURA%>"  colSpan= "4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.ID_ENTE%>"  colSpan = "4" controlWidth="w80" />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.STipoStrutDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.NM_TIPO_STRUT_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.STipoCompDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.NM_TIPO_COMP_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <slf:section name="<%=StrutTipoStrutForm.FormatiAmmissibiliTab.NAME%>" styleClass="importantContainer"> 
                <slf:lblField name="<%=StrutTipoStrutForm.FiltriFormatoFileDoc.NM_FORMATO_FILE_STANDARD%>" colSpan="2" labelWidth="w20" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=StrutTipoStrutForm.FiltriFormatoFileDoc.NM_MIMETYPE_FILE%>" colSpan="2" labelWidth="w20" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField  name="<%=StrutTipoStrutForm.FiltriFormatoFileDoc.RICERCA_FORMATO_BUTTON%>" colSpan="4" />
               <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= StrutTipoStrutForm.FormatoFileDocList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StrutTipoStrutForm.FormatoFileDocList.NAME%>" />
                <slf:listNavBar  name="<%= StrutTipoStrutForm.FormatoFileDocList.NAME%>" />
                <sl:newLine skipLine="true"/>
            </slf:section>
            <sl:pulsantiera>
                <slf:buttonList name="<%=StrutTipoStrutForm.SelectButtonList.NAME%>" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutTipoStrutForm.FormatiAmmessiTab.NAME%>" styleClass="importantContainer"> 
                <slf:listNavBar name="<%= StrutTipoStrutForm.SelectFormatoFileAmmessoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StrutTipoStrutForm.SelectFormatoFileAmmessoList.NAME%>" />
                <slf:listNavBar  name="<%= StrutTipoStrutForm.SelectFormatoFileAmmessoList.NAME%>" />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

