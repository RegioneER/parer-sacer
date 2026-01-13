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

<%@ page import="it.eng.parer.slite.gen.form.StrutTitolariForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTitolariForm.VoceTitolarioDetail.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio voce di classificazione"/>
            <slf:fieldBarDetailTag name="<%= StrutTitolariForm.VoceTitolarioDetail.NAME%>" hideBackButton="false" hideOperationButton="true" /> 
            <sl:newLine skipLine="true"/>
            <sl:newLine />
            <slf:fieldSet >
                <slf:section name="<%=StrutTitolariForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTitolariForm.StrutRif.STRUTTURA%>" width="w100" labelWidth="w10" controlWidth="w90" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.StrutRif.ID_ENTE%>" width="w100" labelWidth="w10" controlWidth="w90"/>
                </slf:section>
                <slf:section name="<%=StrutTitolariForm.VoceSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTitolariForm.VoceTitolarioDetail.CD_VOCE_TITOL%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.VoceTitolarioDetail.DS_VOCE_TITOL%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.VoceTitolarioDetail.NI_ANNI_CONSERV%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.VoceTitolarioDetail.FL_USO_CLASSIF%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.VoceTitolarioDetail.DL_NOTE%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.VoceTitolarioDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w100"/>
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tracciaList'].status eq 'view') }"> 
                <!--  piazzo la lista con i risultati -->
                <div class="livello1"><b style="color: #d3101c;"><%=StrutTitolariForm.TracciaList.DESCRIPTION%></b></div>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= StrutTitolariForm.TracciaList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StrutTitolariForm.TracciaList.NAME%>"  />
                <slf:listNavBar  name="<%= StrutTitolariForm.TracciaList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
