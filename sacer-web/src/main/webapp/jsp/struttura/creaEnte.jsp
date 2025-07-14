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

<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="table" value="${!empty param.table}" />

<sl:html>
    <sl:head title="Dettaglio Ente">
        <script type="text/javascript" src="<c:url value='/js/sips/customModificaEnteMessageBox.js'/>" ></script>
        <script type='text/javascript' >
            $(document).ready(function () {
                CustomBoxModificaEnte();
            });
        </script>
    </sl:head>    
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Enti" />
        <sl:menu />
        <sl:content>
            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>

            <slf:messageBox />
            <c:if test="${!empty requestScope.customModificaEnte}">
                <div class="messages customModificaEnteMessageBox ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.messaggioModificaEnte}"/></li>
                    </ul>   
                </div>
            </c:if>        
            <sl:contentTitle title="Dettaglio Ente"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['entiList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= AmbienteForm.InsEnte.NAME%>" hideBackButton="${!(sessionScope['###_FORM_CONTAINER']['entiList'].status eq 'view')}"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['entiList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmbienteForm.EntiList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>    

            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.InsEnte.ID_ENTE%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />    
                <slf:lblField name="<%=AmbienteForm.InsEnte.NM_ENTE%>" colSpan="2" controlWidth="w100" />
                <slf:lblField name="<%=AmbienteForm.InsEnte.ID_CATEG_ENTE%>" colSpan= "2" controlWidth="w100"/>
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.CD_ENTE_NORMALIZ%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_INI_VAL%>" colSpan="2" />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_FINE_VAL%>" colSpan="2" />   
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.FL_CESSATO%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DS_ENTE%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.ID_AMBIENTE%>" colSpan="2" />                
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_INI_VAL_APPART_AMBIENTE%>" colSpan="2" />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_FIN_VAL_APPART_AMBIENTE%>" colSpan= "2" />
                <sl:newLine />                
                <slf:lblField name="<%=AmbienteForm.InsEnte.TIPO_DEF_TEMPLATE_ENTE%>" colSpan="2" controlWidth="w100" />  
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <div class="livello1"><b>Precedenti appartenenze ad ambienti</b></div>
            <sl:newLine skipLine="true"/>

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['insEnte'].status eq 'insert') }">
                <slf:listNavBar name="<%= AmbienteForm.StoricoEnteAmbienteList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= AmbienteForm.StoricoEnteAmbienteList.NAME%>" />
                <slf:listNavBar  name="<%= AmbienteForm.StoricoEnteAmbienteList.NAME%>" />
            </c:if>

            <sl:newLine skipLine="true"/>
            <div class="livello1"><b>Strutture appartenenti all'ente</b></div>
            <sl:newLine skipLine="true"/>

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['insEnte'].status eq 'insert') }">
                <slf:listNavBar name="<%= AmbienteForm.StruttureList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= AmbienteForm.StruttureList.NAME%>" />
                <slf:listNavBar  name="<%= AmbienteForm.StruttureList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
