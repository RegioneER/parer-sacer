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

<%@ page import="it.eng.parer.slite.gen.form.StrutFormatoFileForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Dettaglio formato ammesso" >        
        <script type="text/javascript" src="<c:url value='/js/sips/customAttivazioneFormatoEsistenteMessageBox.js'/>" ></script>
    </sl:head>
    <c:choose>
        <c:when test="${(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'insert') }">
            <c:set var="desc" value="Seleziona i formati file da aggiungere alla struttura" />
        </c:when>
        <c:when test="${(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'update') }">
            <c:set var="desc" value="Seleziona i formati file da concatenare al formato da modificare" />
        </c:when>
    </c:choose>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Formati Documento" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${requestScope.warningAttivazioneFormatoEsistente != null}">
                <div class="messages customAttivazioneFormatoEsistenteMessageBox ">
                    <ul>
                        <li class="message warning "><c:out value='${requestScope.warningAttivazioneFormatoEsistente}' /></li>
                    </ul>
                </div>
            </c:if>

            <sl:contentTitle title="Dettaglio formato ammesso"/>
            <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
            <!-- Aggiungo le status bar in base se la lista di formati è vuota o no -->
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutFormatoFileForm.FormatoFileDoc.NAME%>" hideBackButton="true"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutFormatoFileForm.FormatoFileDocList.NAME%>" /> 
            </c:if>
            <div class="newLine skipLine"></div>
            <slf:fieldSet>
                <slf:section name="<%=StrutFormatoFileForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutFormatoFileForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutFormatoFileForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>

                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'insert') }"> 
                    <slf:section name="<%=StrutFormatoFileForm.AnteprimaFormatiFileDoc.NAME%>" styleClass="importantContainer">  
                        <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.NM_FORMATO_FILE_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.DS_FORMATO_FILE_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.NI_PUNTEGGIO_TOTALE%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.CD_VERSIONE%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.DT_SOPPRES%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                    </slf:section>

                    <sl:newLine skipLine="true"/>
                    <sl:newLine skipLine="true"/>
                    <div><input type="hidden" name="duplicaRecord" value="${!empty duplicaRecord? (fn:escapeXml(duplicaRecord)) : (fn:escapeXml(param.duplicaRecord))}" /></div>
                    <div><input type="hidden" name="inUse" value="${fn:escapeXml(param.inUse)}" /></div>
                    </c:if>  
                </slf:fieldSet> 
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera >
                    <slf:lblField name="<%=StrutFormatoFileForm.FormatoFileDoc.LOG_EVENTI_FORMATO_FILE_DOC%>"/>
                </sl:pulsantiera>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'insert') 
                          || ((sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'update') ) }">
                <c:if test="${!inUse}">
                    <h2>${fn:escapeXml(desc)}</h2>
                    <slf:section name="<%=StrutFormatoFileForm.FormatiAmmissibiliTab.NAME%>" styleClass="importantContainer"> 
                        <slf:listNavBar name="<%=StrutFormatoFileForm.FormatoFileStandardToDocList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%=StrutFormatoFileForm.FormatoFileStandardToDocList.NAME%>" />
                        <slf:listNavBar name="<%=StrutFormatoFileForm.FormatoFileStandardToDocList.NAME%>" />
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <sl:pulsantiera>
                        <slf:buttonList name="<%=StrutFormatoFileForm.SelectButtonList.NAME%>" />
                    </sl:pulsantiera>
                    <sl:newLine skipLine="true"/>

                    <c:choose>
                        <c:when test="${(sessionScope['###_FORM_CONTAINER']['formatoFileDocList'].status eq 'update') }">
                            <c:set scope="request" var="tipoSezione" value="<%=StrutFormatoFileForm.FormatiConcatenabiliSelezionatiTab.NAME%>" />
                        </c:when>
                        <c:otherwise>
                            <c:set scope="request" var="tipoSezione" value="<%=StrutFormatoFileForm.FormatiAmmessiTab.NAME%>" />                            
                        </c:otherwise>
                    </c:choose>

                    <slf:section name='<%=(String) request.getAttribute("tipoSezione")%>' styleClass="importantContainer"> 
                        <slf:listNavBar name="<%=StrutFormatoFileForm.SelectFormatoFileStandardList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%=StrutFormatoFileForm.SelectFormatoFileStandardList.NAME%>" />
                        <slf:listNavBar name="<%=StrutFormatoFileForm.SelectFormatoFileStandardList.NAME%>" />
                        <sl:newLine skipLine="true"/>
                    </slf:section>
                </c:if> 
            </c:if> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>

