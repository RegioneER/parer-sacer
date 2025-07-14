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

<%@ page import="it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="back" value="${empty param.back}" />
<c:set scope="request" var="table" value="${!empty param.table}" />
<sl:html>
    <sl:head title="<%=CriteriRaggrFascicoliForm.TitolariTree.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {
                $("#Id_titol").on('change', function () {
                    var idTitol = $(this).val();
                    window.location = "CriteriRaggrFascicoli.html?operation=triggerTitolarioDetailId_titolOnTrigger&Id_titol="+idTitol;               
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <div>
                <c:if test="${!back}">
                    <input name="back" type="hidden" value="${fn:escapeXml(param.back)}" />
                </c:if>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>
            <sl:contentTitle title="<%=CriteriRaggrFascicoliForm.TitolariTree.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli.NAME%>" hideBackButton="false" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false" />
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.TitolarioDetail.ID_TITOL%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.TitolarioDetail.CD_COMPOSITO_VOCE_TITOL%>" colSpan="1" controlWidth="w70"/>
                <slf:lblField name="<%=CriteriRaggrFascicoliForm.TitolarioDetail.SELEZIONA_TITOLARIO%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine />
                <slf:tree name="<%=CriteriRaggrFascicoliForm.TitolariTree.NAME%>" additionalJsonParams="\"core\" : { \"expand_selected_onload\" : true, \"multiple\": false }"/>
                <script type="text/javascript" src="<c:url value="/js/custom/customCritRaggrFascTitolTree.js" />" ></script>
            </slf:fieldSet>
        </sl:content>

        <sl:footer />
    </sl:body>
</sl:html>
