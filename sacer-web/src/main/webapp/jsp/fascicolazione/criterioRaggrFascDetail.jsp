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

<%@ page import="it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="back" value="${empty param.back}" />
<c:set scope="request" var="table" value="${!empty param.table}" />
<sl:html>
    <sl:head  title="Criterio di raggruppamento fascicoli" />
    <script type="text/javascript" src="<c:url value='/js/sips/customNumMaxFascMessageBox.js'/>" ></script>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.DESCRIPTION%>" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />            
            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Attenzione: numero massimo fascicoli maggiore di <c:out value="${requestScope.customBox}" />. Vuoi proseguire con il salvataggio?</li>
                    </ul>
                </div>
            </c:if>

            <c:if test="${!empty requestScope.customBox2}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Attenzione: le modifiche richieste classificheranno il criterio come non standard; si desidera procedere?</li>
                    </ul>
                </div>
            </c:if>

            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= CriteriRaggrFascicoliForm.CriterioCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:newLine skipLine="true"/>
            <div>
                <c:if test="${!back}">
                    <input name="back" type="hidden" value="${fn:escapeXml(param.back)}" />
                </c:if>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>
            <sl:contentTitle title="DETTAGLIO CRITERIO DI RAGGRUPPAMENTO FASCICOLI"/>
            <c:choose>
                <c:when test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrFascicoliList'].status eq 'insert'}">
                    <slf:fieldBarDetailTag name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NAME%>" hideBackButton="true" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false" />
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.NAME %>" />
                </c:otherwise>                
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <c:choose>
                    <c:when test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrFascicoliList'].status eq 'insert'}">
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.ID_AMBIENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.ID_ENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.ID_STRUT%>" colSpan="2" />
                    </c:when>
                    <c:otherwise>
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NM_AMBIENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NM_ENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NM_STRUT%>" colSpan="2" />
                    </c:otherwise>         
                </c:choose>
                <sl:newLine skipLine="true" />
                
                <slf:section name="<%= CriteriRaggrFascicoliForm.InfoDescCriterioFasc.NAME%>" styleClass="importantContainer">                        
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NM_CRITERIO_RAGGR%>" colSpan="2" />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.ID_CRITERIO_RAGGR_FASC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.DS_CRITERIO_RAGGR%>" colSpan="2"  />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NI_MAX_FASC%>" colSpan="2"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.DT_ISTITUZ%>" colSpan="2" controlWidth="w20" />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.TI_SCAD_CHIUS%>" colSpan="2"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.DT_SOPPRES%>" colSpan="2" controlWidth="w20" />
                    <slf:doubleLblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NI_TEMPO_SCAD_CHIUS%>" name2="<%=CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.TI_TEMPO_SCAD_CHIUS%>" colSpan="2" controlWidth2="w30" controlWidth="w10" />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.FL_CRITERIO_RAGGR_STANDARD%>" colSpan="2"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NT_CRITERIO_RAGGR%>" colSpan="4"  />
                </slf:section>
                <sl:newLine skipLine="true"/>
                
                <slf:section name="<%= CriteriRaggrFascicoliForm.InfoDescFascicolo.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.AA_FASCICOLO%>" colSpan="1"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.AA_FASCICOLO_DA%>" colSpan="1"  />
                    <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.AA_FASCICOLO_A%>" colSpan="1"  />
                    <sl:newLine />
                    <slf:lblField name="<%=CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NM_TIPO_FASCICOLO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.TI_CONSERVAZIONE%>" colSpan="2" />
                    <sl:newLine skipLine="true"/>
                    <%--
                    <div style="float: right;">
                        <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.INSERISCI_VOCE_CLASSIFICAZIONE %>" width="w100"/>
                    </div>
                    --%>
                    <slf:section name="<%=CriteriRaggrFascicoliForm.TitolarioSection.NAME%>" styleClass="importantContainer"> 
                        <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['titolariList'].status eq 'view'}">
                                <slf:listNavBar name="<%= CriteriRaggrFascicoliForm.TitolariList.NAME%>" pageSizeRelated="true"/>
                                <slf:list name="<%= CriteriRaggrFascicoliForm.TitolariList.NAME%>" />
                                <slf:listNavBar  name="<%= CriteriRaggrFascicoliForm.TitolariList.NAME%>" />
                            </c:when>
                            <c:otherwise>
                                <slf:listNavBar name="<%= CriteriRaggrFascicoliForm.TitolariList.NAME%>" pageSizeRelated="true"/>
                                <slf:selectList name="<%= CriteriRaggrFascicoliForm.TitolariList.NAME%>" addList="false" />
                                <slf:listNavBar  name="<%= CriteriRaggrFascicoliForm.TitolariList.NAME%>" />
                            </c:otherwise>                
                        </c:choose>
                        <sl:newLine skipLine="true"/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.INSERISCI_VOCE_CLASSIFICAZIONE %>" />
                        </sl:pulsantiera>
                    </slf:section>
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.DUPLICA_CRIT_BUTTON%>"/>
                <slf:lblField name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.LOG_EVENTI_CRITERI_RAGGRUPPAMENTO%>"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
