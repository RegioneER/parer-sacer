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

<%@ page import="it.eng.parer.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<sl:html>
    <sl:head  title="Configurazione logger" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <c:forEach var="currentLogger" items="${requestScope.loggers}" varStatus="status">
                    <div class="containerLeft w30">
                        <c:if test="${fn:contains(currentLogger,'org.eclipse')}" >
                            <label class="slLabel w60" >SQL logger:&nbsp;</label>
                            <select name="loggerlevel_${fn:escapeXml(status.count)}" class="slText w10">

                                <option value="" <c:if test="${requestScope.levels[status.count-1] == '' }">selected</c:if>></option>
                                <option value="ALL" <c:if test="${requestScope.levels[status.count-1] == 'ALL' }">selected</c:if>>All</option>                                
                                <option value="FINER" <c:if test="${requestScope.levels[status.count-1] == 'FINER' }">selected</c:if>>Finer</option>
                                <option value="FINE" <c:if test="${requestScope.levels[status.count-1] == 'FINE' }">selected</c:if>>Fine</option>
                                <option value="CONFIG" <c:if test="${requestScope.levels[status.count-1] == 'CONFIG' }">selected</c:if>>Config</option>                    
                                <option value="INFO" <c:if test="${requestScope.levels[status.count-1] == 'INFO' }">selected</c:if>>Info</option>
                                <option value="WARNING" <c:if test="${requestScope.levels[status.count-1] == 'WARNING' }">selected</c:if>>Warning</option>                            
                                <option value="SEVERE" <c:if test="${requestScope.levels[status.count-1] == 'SEVERE' }">selected</c:if>>Severe</option>
                                <option value="OFF" <c:if test="${requestScope.levels[status.count-1] == 'OFF' }">selected</c:if>>Off</option>                                
                            </select>
                        </c:if>
                        <c:if test="${!fn:contains(currentLogger,'org.eclipse')}" >
                            <label class="slLabel w60" >${fn:escapeXml(currentLogger)}:&nbsp;</label>
                            <select name="loggerlevel_${fn:escapeXml(status.count)}" class="slText w10">
                                <option value="" <c:if test="${requestScope.levels[status.count-1] == '' }">selected</c:if>></option>
                                <option value="ALL" <c:if test="${requestScope.levels[status.count-1] == 'ALL' }">selected</c:if>>All</option>
                                <option value="TRACE" <c:if test="${requestScope.levels[status.count-1] == 'TRACE' }">selected</c:if>>Trace</option>
                                <option value="DEBUG" <c:if test="${requestScope.levels[status.count-1] == 'DEBUG' }">selected</c:if>>Debug</option>
                                <option value="INFO" <c:if test="${requestScope.levels[status.count-1] == 'INFO' }">selected</c:if>>Info</option>
                                <option value="WARN" <c:if test="${requestScope.levels[status.count-1] == 'WARN' }">selected</c:if>>Warn</option>                    
                                <option value="ERROR" <c:if test="${requestScope.levels[status.count-1] == 'ERROR' }">selected</c:if>>Error</option>
                                <option value="FATAL" <c:if test="${requestScope.levels[status.count-1] == 'FATAL' }">selected</c:if>>Fatal</option>
                                <option value="OFF" <c:if test="${requestScope.levels[status.count-1] == 'OFF' }">selected</c:if>>Off</option>    
                            </select>
                        </c:if>

                    </div>
                    <c:if test="${fn:escapeXml(status.count) mod 3 == 0}">
                        <sl:newLine skipLine="true"/>
                    </c:if>
                    <div>
                        <input type="hidden" name="loggers" value="${fn:escapeXml(currentLogger)}" />
                    </div>
                </c:forEach>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AmministrazioneForm.Bottoni.APPLICA_LIVELLI%>" colSpan="4" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
