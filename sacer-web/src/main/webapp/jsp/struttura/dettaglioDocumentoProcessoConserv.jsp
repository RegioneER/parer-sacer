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

<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StruttureForm.DocumentoProcessoConservDetail.DESCRIPTION%>">
    </sl:head>
    <script type="text/javascript" src="<c:url value='/js/customDocumentoProcessoConservMessageBox.js'/>" ></script>
    <sl:body>
        <sl:header showChangeOrganizationBtn="false" />
        <sl:menu showChangePasswordBtn="true" />
        <sl:content multipartForm="true" >
            <slf:messageBox />
             <c:if test="${!empty requestScope.customBoxSalvataggioDocumentoProcessoConserv}">
                <div class="messages customBoxSalvataggioDocumentoProcessoConserv ">
                    <ul>
                        <li class="message info "><c:out value="${requestScope.customMessageSalvataggioDocumentoProcessoConserv}"/> </li>
                    </ul>                   
                </div>
            </c:if>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="<%=StruttureForm.DocumentoProcessoConservDetail.DESCRIPTION%>" />
            <c:if test="${sessionScope['###_FORM_CONTAINER']['documentiProcessoConservList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StruttureForm.DocumentoProcessoConservDetail.NAME%>" hideBackButton="false"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['documentiProcessoConservList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StruttureForm.DocumentiProcessoConservList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StruttureForm.EnteConvenzionatoSection.NAME%>" styleClass="importantContainer w100">                
                <slf:lblField name="<%=StruttureForm.EnteConvenzionatoDetail.ID_ENTE_SIAM%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=StruttureForm.EnteConvenzionatoDetail.CD_ENTE_CONVENZ%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=StruttureForm.EnteConvenzionatoDetail.TI_CD_ENTE_CONVENZ%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=StruttureForm.EnteConvenzionatoDetail.NM_ENTE_SIAM%>" colSpan="2"/>                
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StruttureForm.DocumentiProcessoConservSection.NAME%>" styleClass="importantContainer w100">
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.ID_ORGANIZ_IAM%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.ENTE_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.STRUTTURA_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.ID_TIPO_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.CD_REGISTRO_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.AA_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.CD_KEY_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.PG_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.DT_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.BL_DOC_PROCESSO_CONSERV%>" colSpan="2"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.DS_DOC_PROCESSO_CONSERV%>" colSpan="4"/><sl:newLine />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=StruttureForm.DocumentoProcessoConservDetail.DOWNLOAD_FILE_DOC_PROCESSO%>" colSpan="2" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
