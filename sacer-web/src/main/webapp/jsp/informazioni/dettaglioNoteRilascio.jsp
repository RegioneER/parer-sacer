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

<%@ page import="it.eng.parer.slite.gen.form.NoteRilascioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca note di rilascio" ></sl:head>
    <sl:body>
        <c:if test="${(sessionScope['###_FORM_CONTAINER']['listaNoteRilascio'].status eq 'view') && !(sessionScope['###_FORM_CONTAINER']['listaNoteRilascio'].table['empty'])}">
            <c:set var="legend" value="Novità della versione ${sessionScope['###_FORM_CONTAINER']['dettaglioNoteRilascio']['cd_versione'].value} del ${sessionScope['###_FORM_CONTAINER']['dettaglioNoteRilascio']['dt_versione'].value}" />
        </c:if>
        <sl:header showChangeOrganizationBtn="false"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio note di rilascio"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['listaNoteRilascio'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= NoteRilascioForm.DettaglioNoteRilascio.NAME%>" hideBackButton="true" /> 
            </c:if>   
            
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['listaNoteRilascio'].table['empty']) && !empty sessionScope.isFromNotaRilascioPrec}">
                <slf:listNavBarDetail name="<%= NoteRilascioForm.ListaNoteRilascio.NAME%>" />  
            </c:if>
            

            <sl:newLine skipLine="true"/>
            <c:choose>
                <c:when test="${!empty showNotaRilascio}">  
                    <slf:fieldSet legend="${legend}">
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.ID_NOTA_RILASCIO %>" colSpan="4" controlWidth="w40"/> <sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.NM_APPLIC%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.CD_VERSIONE%>" colSpan="4" controlWidth="w40"/> <sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.DT_VERSIONE%>" colSpan="4" controlWidth="w40"/> <sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.DT_INI_VAL%>" colSpan="4" controlWidth="w40"/> <sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.DT_FINE_VAL%>" colSpan="4" controlWidth="w40"/> <sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.DS_EVIDENZA%>" colSpan="4" controlWidth="w60" /> <sl:newLine />
                        <slf:lblField name="<%=NoteRilascioForm.DettaglioNoteRilascio.BL_NOTA%>" colSpan="4" controlWidth="w60"/> <sl:newLine />
                    </slf:fieldSet> 
                </c:when>
                <c:otherwise>
                    <slf:fieldSet legend="${legend}">
                        <p style="text-align:left; word-wrap: break-word;"><c:out value="${infoDaMostrare}" escapeXml="false" />                   
                    </slf:fieldSet>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['listaNoteRilascio'].status eq 'view') && !(sessionScope['###_FORM_CONTAINER']['listaNoteRilascio'].table['empty']) && empty sessionScope.isFromNotaRilascioPrec}">
                <slf:section name="<%=NoteRilascioForm.NoteRilascioPrecSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= NoteRilascioForm.NoteRilascioPrecedentiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= NoteRilascioForm.NoteRilascioPrecedentiList.NAME%>" />
                    <slf:listNavBar  name="<%= NoteRilascioForm.NoteRilascioPrecedentiList.NAME%>" />
                </slf:section>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
