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
<c:set scope="request" var="table" value="${!empty param.table}" />

<sl:html>
    <sl:head title="Dettaglio Xsd Migrazione">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
        <script type="text/javascript">
            $(document).ready(function () {
                $('.confermaDisattivazioneXsd').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "Strutture.html?operation=confermaDisattivazione";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.confermaDisattivazioneXsd}">
                <div class="messages confermaDisattivazioneXsd ">
                    <ul>
                        <li class="message info ">Desideri disattivare la versione di XSD ?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="Gestione Xsd Migrazione"/>
            <slf:fieldBarDetailTag name="<%= StruttureForm.GestioneXsdMigrazione.NAME%>" 
                                   hideBackButton="${!(sessionScope['###_FORM_CONTAINER']['gestioneXsdMigrazione'].status eq 'view')}"
                                   hideUpdateButton="${(sessionScope['###_FORM_CONTAINER']['gestioneXsdMigrazione'].status eq 'view')}" hideDeleteButton="true"/> 
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=StruttureForm.Descrizione.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.InsStruttura.NM_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.DS_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=StruttureForm.GestioneXsdMigrazione.NM_SISTEMA_MIGRAZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <slf:lblField name="<%=StruttureForm.GestioneXsdMigrazione.ID_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['gestioneXsdMigrazione'].status eq 'view'}">
                <slf:tab  name="<%=StruttureForm.XsdMigrStrutTab.NAME%>" tabElement="<%=StruttureForm.XsdMigrStrutTab.xsd_migr_tipo_unita_doc%>">
                    <slf:listNavBar name="<%= StruttureForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StruttureForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StruttureForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%=StruttureForm.XsdMigrStrutTab.NAME%>" tabElement="<%=StruttureForm.XsdMigrStrutTab.xsd_migr_tipo_doc%>">
                    <slf:listNavBar name="<%= StruttureForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StruttureForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StruttureForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%=StruttureForm.XsdMigrStrutTab.NAME%>" tabElement="<%=StruttureForm.XsdMigrStrutTab.xsd_migr_tipo_comp_doc%>">
                    <slf:listNavBar name="<%= StruttureForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StruttureForm.XsdDatiSpecList.NAME%>"   />
                    <slf:listNavBar  name="<%= StruttureForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
