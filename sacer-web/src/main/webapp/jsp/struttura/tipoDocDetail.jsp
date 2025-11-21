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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo documento" >      
        <script type='text/javascript' >
            $(document).ready(function () {
                // Gestione dei campi relativi alla creazione automatica dei criteri e alla periodicità di versamento
                $('#Criterio_autom_tipo_doc').attr("disabled", true);
                $("#Ds_periodicita_vers").attr('disabled', true);
                initChangeEvents();

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
                            window.location = "StrutTipi.html?operation=confermaDisattivazioneXsd";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
            });

            function initChangeEvents() {
                $('#Fl_tipo_doc_principale').change(function () {
                    var input = $(this).val();
                    if (input === '1') {
                        $('#Criterio_autom_tipo_doc').attr("disabled", false);
                        $("#Ds_periodicita_vers").attr("disabled", false);
                    } else {
                        $('#Criterio_autom_tipo_doc').attr("disabled", true);
                        $('#Criterio_autom_tipo_doc').removeAttr('checked');
                        $("#Ds_periodicita_vers").attr("disabled", true);
                        $("#Ds_periodicita_vers").val('');
                    }
                });

                // Ricontrollo se vale uno quando ricarico la pagina               
                if ($('#Fl_tipo_doc_principale').val() === '1') {
                    $('#Criterio_autom_tipo_doc').attr("disabled", false);
                    $("#Ds_periodicita_vers").attr("disabled", false);
                } else {
                    $('#Criterio_autom_tipo_doc').attr("disabled", true);
                    $('#Criterio_autom_tipo_doc').removeAttr('checked');
                    $("#Ds_periodicita_vers").attr("disabled", true);
                    $("#Ds_periodicita_vers").val('');
                }
            };

        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi Documento"/>
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
            <sl:contentTitle title="Dettaglio tipo documento"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoDocList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoDoc.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipiForm.TipoDocList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>    
                <slf:section name="<%=StrutTipiForm.STipoDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.NM_TIPO_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DS_TIPO_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.FL_TIPO_DOC_PRINCIPALE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" /> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DS_PERIODICITA_VERS%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DL_NOTE_TIPO_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70" /> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DT_SOPPRES%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                </slf:section>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoDocList'].status eq 'insert') }">
                    <slf:section name="<%=StrutTipiForm.CreaCriterioTipoDocSection.NAME%>" styleClass="importantContainer"> 
                        <slf:lblField name="<%=StrutTipiForm.TipoDocCreazioneCriterio.CRITERIO_AUTOM_TIPO_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    </slf:section>
                </c:if>
                 <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoDocList'].status eq 'update') }">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=StrutTipiForm.TipoDocCreazioneCriterio.CREA_CRITERIO_RAGGR_STANDARD_TIPO_DOC_BUTTON%>" />
                    </sl:pulsantiera>
                </c:if>
                <slf:lblField name="<%=StrutTipiForm.TipoDoc.ID_TIPO_DOC%>" />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoDoc'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.LOG_EVENTI_TIPO_DOC%>" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>

                <slf:section name="<%=StrutTipiForm.XsdDatiSpecTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.XsdDatiSpecList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.RegoleSubStrutTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.TipoStrutDocAmmessoTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.TipoStrutDocAmmessoDaTipoDocList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.TipoStrutDocAmmessoDaTipoDocList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.TipoStrutDocAmmessoDaTipoDocList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.CriteriRaggrTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>" />
                </slf:section>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
