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

<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>



<sl:html>
    <sl:head title="Dettaglio tipo rappresentazione componente" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>


            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo rappresentazione componente"/>


            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoRapprCompList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StruttureForm.TipoRapprComp.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoRapprCompList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StruttureForm.TipoRapprCompList.NAME%>" />   
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
                <slf:section name="<%=StruttureForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <%--                    <slf:lblField name="<%=StruttureForm.InsStruttura.NM_STRUT%>" colSpan= "4" controlWidth="w40" />
                                        <sl:newLine />
                                        <slf:lblField name="<%=StruttureForm.InsStruttura.DS_STRUT%>"  colSpan= "4" controlWidth="w40" />--%>
                    <slf:lblField name="<%=StruttureForm.InsStruttura.STRUTTURA%>"  colSpan= "4" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE%>"  colSpan = "4" controlWidth="w100" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoRapprComp.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.NM_TIPO_RAPPR_COMP%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.DS_TIPO_RAPPR_COMP%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.ID_FORMATO_CONTENUTO%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.ID_FORMATO_CONVERTIT%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.TI_ALGO_RAPPR%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.TI_OUTPUT_RAPPR%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.ID_FORMATO_OUTPUT_RAPPR%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.DT_ISTITUZ%>" colSpan="4" controlWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.DT_SOPPRES%>" colSpan="4" controlWidth="w20"/><sl:newLine />
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.LOG_EVENTI_TIPO_RAPPR_COMP%>" />
                </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoRapprCompList'].status eq 'view') }">
                <div class="livello1"><b style="color: #d3101c;">Trasformatori</b></div>
                <slf:listNavBar name="<%= StruttureForm.TrasformTipoRapprList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StruttureForm.TrasformTipoRapprList.NAME%>"  />
                <slf:listNavBar  name="<%= StruttureForm.TrasformTipoRapprList.NAME%>" />

                <sl:newLine skipLine="true"/>
                
                <slf:section name="<%=StruttureForm.STipoComp.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprCompList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprCompList.NAME%>" />
                    <slf:listNavBar  name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprCompList.NAME%>" />
                </slf:section>

            </c:if>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

