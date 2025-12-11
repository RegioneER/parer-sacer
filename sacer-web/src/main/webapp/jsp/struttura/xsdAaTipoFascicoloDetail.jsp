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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio XSD" >        

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu/>
        <sl:content>    
            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>         
                <slf:messageBox />             
                <sl:contentTitle title="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DESCRIPTION%>"/>

              
            <c:if test="${sessionScope['###_FORM_CONTAINER']['metadatiProfiloFascicoloList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.MetadatiProfiloDetail.NAME%>" hideBackButton="true" />
            </c:if>

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['metadatiProfiloFascicoloList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" />
            </c:if>
            <sl:newLine skipLine="true"/>


            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.STRUTTURA%>"  width="w100" controlWidth="w40" labelWidth="w20"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.ID_ENTE%>"  width="w100" controlWidth="w80" labelWidth="w20"/>
                </slf:section>
                <sl:newLine/>
                <slf:section name="<%=StrutTipiFascicoloForm.TipoFascicoloSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.NM_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DS_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.XsdSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.ID_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/> 
                    <sl:newLine/>      
                    <slf:lblField name="<%= StrutTipiFascicoloForm.MetadatiProfiloDetail.ID_AA_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%= StrutTipiFascicoloForm.MetadatiProfiloDetail.ID_USO_MODELLO_XSD_FASC%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.TI_MODELLO_XSD%>" width="w100" labelWidth="w20" controlWidth="w70"/> 
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.ID_MODELLO_XSD_FASCICOLO%>" width="w100" labelWidth="w20" />                    
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.VISUALIZZA_MODELLO_XSD_FASCICOLO%>" width="w100" labelWidth="w20" />
                    <sl:newLine/>
                    <%--slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.FL_STANDARD%>"  width="w100" labelWidth="w20" />
                    <sl:newLine/--%>
                    <c:if test="${(sessionScope['###_FORM_CONTAINER']['metadatiProfiloFascicoloList'].status eq 'view') }"> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DS_XSD%>" width="w100" labelWidth="w20" controlWidth="w70"/> 
                        <sl:newLine/>
                    </c:if>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DT_ISTITUZ%>" width="w100" labelWidth="w20" controlWidth="w70"/> 
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DT_SOPPRES%>" width="w100" labelWidth="w20" controlWidth="w70"/> 
                    <sl:newLine skipLine="true"/>
                    <c:if test="${(sessionScope['###_FORM_CONTAINER']['metadatiProfiloFascicoloList'].status eq 'view') }"> 
                         <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.BL_XSD%>" width="w100" labelWidth="w20" controlWidth="w70"/> 
                        <sl:newLine/>
                    </c:if>
                </slf:section>

                <%--<c:if test="${(sessionScope['###_FORM_CONTAINER']['metadatiProfiloFascicoloList'].status eq 'view') }"> 
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=StrutTipiFascicoloForm.XsdAttribFascicoloSection.NAME%>" styleClass="importantContainer">
                        <slf:listNavBar name="<%=StrutTipiFascicoloForm.AttribFascicoloList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%=StrutTipiFascicoloForm.AttribFascicoloList.NAME%>"  />
                        <slf:listNavBar  name="<%=StrutTipiFascicoloForm.AttribFascicoloList.NAME%>" />
                    </slf:section>
                </c:if>--%>
            </slf:fieldSet>
        </sl:content>
        <sl:footer/>
    </sl:body>
</sl:html>
