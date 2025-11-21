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

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<!-- Setto l'attributo navTable che mi servirà per gestire il tasto "indietro" a seconda della mia provenienza
     quando dovrò scorrere la FileList in fondo pagina-->
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio versamento fallito" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>       
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO VERSAMENTO FALLITO"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.VersamentiFallitiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.VersamentiFallitiList.NAME%>"/>
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.SessioniList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= MonitoraggioForm.SessioniList.NAME%>" />
                </c:when>
            </c:choose>

            <slf:tab name="<%= MonitoraggioForm.VersamentiFallitiTabs.NAME%>" tabElement="InfoVersamento">
                <slf:fieldSet  borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.NM_STRUT%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:doubleLblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.NM_COGNOME_USER%>" name2="<%=MonitoraggioForm.VersamentiFallitiDetail.NM_NOME_USER%>" controlWidth="w20" controlWidth2="w20" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.ID_SESSIONE_VERS%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.TI_SESSIONE_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.DT_APERTURA%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.DT_CHIUSURA%>" colSpan="4"/>
                    <sl:newLine />
                    <slf:section name="<%=MonitoraggioForm.ChiaveSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="3" />
                        <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.AA_KEY_UNITA_DOC%>" colSpan="3" />
                        <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.CD_KEY_UNITA_DOC%>" colSpan="3" />
                    </slf:section>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.CD_KEY_DOC_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.CD_ERR%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.DS_ERR%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.FL_RISOLTO%>" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.FL_SESSIONE_ERR_VERIF%>" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.VersamentiFallitiDetail.FL_SESSIONE_ERR_NON_RISOLUB%>" colSpan="4" />
                </slf:fieldSet>
                <sl:newLine skipLine="true" />

                <sl:newLine skipLine="true"/>
                <h2 class="titleFiltri">Lista File</h2>
                <sl:newLine skipLine="true"/>

                <!--  piazzo la lista con i risultati -->
                <c:choose>
                    <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.VersamentiFallitiList.NAME)%>'>
                        <slf:listNavBar  name="<%= MonitoraggioForm.FileList.NAME%>" pageSizeRelated="true" mainNavTable="<%= MonitoraggioForm.VersamentiFallitiList.NAME%>"/>
                    </c:when>
                    <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.SessioniList.NAME)%>'>
                        <slf:listNavBar  name="<%= MonitoraggioForm.FileList.NAME%>" pageSizeRelated="true" mainNavTable="<%= MonitoraggioForm.SessioniList.NAME%>"/>
                    </c:when>
                </c:choose>
                <slf:list name="<%= MonitoraggioForm.FileList.NAME%>" />

                <c:choose>
                    <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.VersamentiFallitiList.NAME)%>'>
                        <slf:listNavBar  name="<%= MonitoraggioForm.FileList.NAME%>" mainNavTable="<%= MonitoraggioForm.VersamentiFallitiList.NAME%>"/>
                    </c:when>
                    <c:when test='<%= request.getAttribute("navTable").equals(MonitoraggioForm.SessioniList.NAME)%>'>
                        <slf:listNavBar  name="<%= MonitoraggioForm.FileList.NAME%>" mainNavTable="<%= MonitoraggioForm.SessioniList.NAME%>"/>
                    </c:when>
                </c:choose>

                <sl:pulsantiera>
                    <slf:buttonList name="<%=MonitoraggioForm.ScaricaFileXMLButtonList.NAME%>">
                        <slf:lblField name="<%=MonitoraggioForm.ScaricaFileXMLButtonList.SCARICA_FILE_XMLVERSAMENTO%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </slf:buttonList>
                </sl:pulsantiera>

            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.VersamentiFallitiTabs.NAME%>" tabElement="VersamentoXMLRich">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=MonitoraggioForm.VersamentiFallitiDetail.BL_XML_RICH%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.VersamentiFallitiTabs.NAME%>" tabElement="VersamentoXMLIndex">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=MonitoraggioForm.VersamentiFallitiDetail.BL_XML_INDEX%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.VersamentiFallitiTabs.NAME%>" tabElement="VersamentoXMLRisp">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=MonitoraggioForm.VersamentiFallitiDetail.BL_XML_RISP%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <!-- Risetto il parametro table -->        
            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}"/>
            </div>

            <!-- Mantengo il valore di mainNavTable quando navigo tra i tab -->
            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
