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

<%@ page import="it.eng.parer.slite.gen.form.RestituzioneArchivioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=RestituzioneArchivioForm.RichRestArchDetail.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/sips/customCambioStatoMessageBox.js'/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.customBox}" /></li>
                    </ul>
                    <p style="margin-left: 70px;">
                    <c:forTokens items = "${requestScope.customBoxElencoStrutture}" delims = "," var = "strutturaInteressata">
                        <c:out value = "${strutturaInteressata}"/><br>
                    </c:forTokens>
                </div>
            </c:if>
            <sl:contentTitle title="<%=RestituzioneArchivioForm.RichRestArchDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= RestituzioneArchivioForm.RichRestArchList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=RestituzioneArchivioForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.ID_RICHIESTA_RA%>" width="w10" />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.ID_STRUT%>" width="w10" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.NM_ENTE_CONVENZ%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.NM_ENTE_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.TS_INIZIO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.TS_FINE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.FTP_PATH%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=RestituzioneArchivioForm.StatoCorrenteSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.TI_STATO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.NM_USERID%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['richRestArchList'].status eq 'view') }">
                <sl:pulsantiera>
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetailButtonList.RIELABORA_RICHIESTA%>" width="w10"/>
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetailButtonList.ANNULLA_RICHIESTA%>" width="w10" />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetailButtonList.VERIFICA_RICHIESTA%>" width="w10"/>
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetailButtonList.RESTITUZIONE_RICHIESTA%>" width="w10"/>
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= RestituzioneArchivioForm.ItemList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= RestituzioneArchivioForm.ItemList.NAME%>" />
                <slf:listNavBar  name="<%= RestituzioneArchivioForm.ItemList.NAME%>" /> 
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
