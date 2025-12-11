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

<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio Aggiornamento">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>
                <slf:messageBox/>
                <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO AGGIORNAMENTO"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <c:choose>
                <c:when test="${empty navTable or navTable eq 'vuoto'}">
                    <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NAME%>" hideOperationButton="true"/>
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= UnitaDocumentarieForm.AggiornamentiMetadatiList.NAME%>" />
                </c:otherwise>
            </c:choose>

            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioTabs.NAME%>" tabElement="InfoPrincipaliUpd">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=UnitaDocumentarieForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=UnitaDocumentarieForm.UnitaDocSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.AA_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.CD_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NM_TIPO_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NM_TIPO_DOC_PRINC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=UnitaDocumentarieForm.ProfiloUpd.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.PG_UPD_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_URN_UPD_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_PROFILO_ARCHIV%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_FASCICOLO_PRINC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_FASCICOLI_SEC%>" colSpan="2" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_PROFILO_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_PROFILO_NORMATIVO%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_LINK_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_DATI_SPEC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_UPD_DATI_SPEC_MIGRAZ%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioTabs.NAME%>" tabElement="InfoVersamentoUpd">
                <!--  piazzo i campi da visualizzare nel tab -->
                <slf:fieldSet borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.CD_VERSIONE_XML%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NM_USERID%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.TS_INI_SES%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.TIPO_UPD_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.FL_FORZA_UPD%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.NT_UPD%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.TI_STATO_UPD_ELENCO_VERS%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.TI_STATO_CONSERVAZIONE%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.ID_ELENCO_VERS%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioTabs.NAME%>" tabElement="XMLRichiestaUpd">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_HASH_XML_RICH%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_ALGO_HASH_XML_RICH%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.CD_ENCODING_HASH_XML_RICH%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_URN_XML_RICH%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_URN_NORMALIZ_XML_RICH%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.BL_XML_RICH%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioTabs.NAME%>" tabElement="XMLRispostaUpd">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_HASH_XML_RISP%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_ALGO_HASH_XML_RISP%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.CD_ENCODING_HASH_XML_RISP%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_URN_XML_RISP%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.DS_URN_NORMALIZ_XML_RISP%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.BL_XML_RISP%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=UnitaDocumentarieForm.AggiornamentiMetadatiUDDetail.SCARICA_XML_UPD%>" colSpan="3" />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioListsTabs.NAME%>" tabElement="ListaDocAggiornati">
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.DocumentiUpdUDList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.DocumentiUpdUDList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
                <slf:list name="<%= UnitaDocumentarieForm.DocumentiUpdUDList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.DocumentiUpdUDList.NAME%>" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.DocumentiUpdUDList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
            </slf:tab>
            
            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioListsTabs.NAME%>" tabElement="ListaCompAggiornati">
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.CompUpdUDList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.CompUpdUDList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
                <slf:list name="<%= UnitaDocumentarieForm.CompUpdUDList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.CompUpdUDList.NAME%>" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.CompUpdUDList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
            </slf:tab>
            
            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioListsTabs.NAME%>" tabElement="ListaUpdRisolti">
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDKoRisoltiList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDKoRisoltiList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
                <slf:list name="<%= UnitaDocumentarieForm.UpdUDKoRisoltiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDKoRisoltiList.NAME%>" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDKoRisoltiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
            </slf:tab>
            
            <slf:tab  name="<%= UnitaDocumentarieForm.AggiornamentiDettaglioListsTabs.NAME%>" tabElement="ListaUpdWarning">
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDWarningList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDWarningList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
                <slf:list name="<%= UnitaDocumentarieForm.UpdUDWarningList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDWarningList.NAME%>" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.UpdUDWarningList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
            </slf:tab>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
