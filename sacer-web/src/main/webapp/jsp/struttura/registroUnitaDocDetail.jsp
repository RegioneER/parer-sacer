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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm "%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio registro" >
        <script type="text/javascript" src="<c:url value='/js/custom/controlliSerie.js'/>"></script>
        <script type='text/javascript' >

            $(document).ready(function () {
                //$("#Fl_conserv_illimitata").attr("disabled", true);       
                //$("#Ni_aa_conserv").attr("disabled", true);       
                $("#AaRegistroUnitaDocList tr ").each(function () {
                    var elemento = $(this).find('div[id^="Controllo_formato_"] img');
                    if (elemento.attr('src') === '/sacer/img/checkbox-on.png') {
                        elemento.attr('title', 'Tutte le ud versate rispettano il formato numero del registro');
                    } else {
                        elemento.attr('title', 'Sono presenti ud versate che non rispettano il formato numero del registro');
                    }
                });


                initModelliPage();
                if ($("#Ni_anni_conserv").val().length !== 0) {
                    $("#Ni_anni_conserv").trigger('change');
                }
                // Gestione dei campi relativi alla conservazione
                initChangeEvents();

                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-off.png"]').attr("src", "./img/alternative/checkbox-on.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-on.png"]').attr("src", "./img/alternative/checkbox-off.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-warn.png"]').remove();

                $('div[id^="Controllo_formato_da_list"] > img[src$="checkbox-off.png"]').attr("src", "./img/alternative/checkbox-on.png");
                $('div[id^="Controllo_formato_da_list"] > img[src$="checkbox-warn.png"]').remove();
                
                  var elemento = $('div[id^="Controllo_formato"] img');
                if (elemento.attr('src') === './img/alternative/checkbox-off.png') {
                    elemento.attr('title', 'Tutte le ud versate rispettano il formato numero del registro');
                } else {
                    elemento.attr('title', 'Sono presenti ud versate che non rispettano il formato numero del registro');
                }
                

                $('.warningModificaSerie').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            window.location = "StrutTipi.html?operation=confermaSalvataggioRegistroUnitaDoc&table=" + $("input[name='nmAzionePerCriteri']").val();
                            $(this).dialog("close");
                        },
                        "Annulla": function () {
                            window.location = "StrutTipi.html?operation=elencoOnClick";
                            $(this).dialog("close");
                        }
                    }
                });
            });

            function initChangeEvents() {
                $('#Fl_crea_serie').change(function () {
                    var value;
                    if ($(this).is("div")) {
                        value = $('#Fl_crea_serie_HIDDEN').val() === "1";
                    } else {
                        value = $(this).is(":checked");
                    }
                    if (value) {
                        $('#Ni_anni_conserv').attr("readonly", false);
                        $('#Conserv_unlimited').attr("disabled", false);
                        $("#Fl_crea_tipo_serie_standard").attr("disabled", false);
                    } else {
                        $('#Ni_anni_conserv').attr("readonly", true);
                        $('#Conserv_unlimited').attr("disabled", true);
                        $("#Fl_crea_tipo_serie_standard").attr("disabled", true);
                        $("#Id_modello_tipo_serie").attr("disabled", true);
                        $("#Fl_crea_tipo_serie_standard").val("");
                        $('#Ni_anni_conserv').val("");
                        $('#Conserv_unlimited').val("");
                        $("#Id_modello_tipo_serie").val("");
                    }
                }).trigger('change');

                $('#Fl_crea_tipo_serie_standard').change(function () {
                    var input = $(this).val();
                    if (input === '1') {
                        $("#Id_modello_tipo_serie").attr("disabled", false);
                    } else {
                        $("#Id_modello_tipo_serie").attr("disabled", true);
                        $("#Id_modello_tipo_serie").val("");
                    }
                }).trigger('change');
                
                $("#Fl_conserv_uniforme").change(function () {
                    var select = $("#Fl_conserv_uniforme").val();                     
                    
                    if (select === "1") {
                        $("#Fl_conserv_illimitata").attr("disabled", false);
                        $("#Ni_aa_conserv").attr("readonly", "readonly");                        
                    } else {
                        $("#Fl_conserv_illimitata").val("").attr("disabled", true);
                        $("#Ni_aa_conserv").val("").attr("readonly", "readonly");                        
                    }
                }).trigger('change');
                
                $("#Fl_conserv_illimitata").change(function () {
                    var select = $("#Fl_conserv_illimitata").val();
                    
                    if (select === "1") {
                        $("#Ni_aa_conserv").val("9999").attr("readonly", "readonly");
                    } else if (select === "0") {
                        $("#Ni_aa_conserv").removeAttr("readonly");
                    }else{
                        $("#Ni_aa_conserv").val("").attr("readonly", "readonly");                        
                    } 
                }).trigger('change');

            }

        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Registri" />
        <sl:menu />

        <sl:content>
            <slf:messageBox /> 
            <c:if test="${!empty requestScope.warningModificaSerie}">
                <div class="messages ui-state-highlight warnBox warningModificaSerie ">
                    <span class="ui-icon ui-icon-alert"></span>
                    <ul>
                        <c:forTokens items="${fn:escapeXml(requestScope.warningModificaSerie)}" delims="#" var="riga" varStatus="iterator">
                            <c:choose><c:when test="${!fn:startsWith(riga, 'Attenzione:')}"><li class="message "></c:when><c:otherwise><p></c:otherwise></c:choose>
                                    ${riga}
                                    <c:choose><c:when test="${!fn:startsWith(riga, 'Attenzione:')}"></li></c:when><c:otherwise></p></c:otherwise></c:choose>
                            </c:forTokens>
                    </ul>
                </div>
            </c:if>

            <sl:contentTitle title="Dettaglio registro"/>

            <div>
                <input name="nmAzionePerCriteri" type="hidden" value="${fn:escapeXml(requestScope.nmAzionePerCriteri)}" />
            </div>

            <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['registroUnitaDocList'].status eq 'insert')}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiForm.RegistroUnitaDoc.NAME%>" 
                                           hideBackButton="${(sessionScope['###_FORM_CONTAINER']['registroUnitaDocList'].status eq 'insert')}"/> 
                </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['registroUnitaDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipiForm.RegistroUnitaDocList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w30" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan = "2" labelWidth="w30" controlWidth="w70" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiForm.SRegistroUnitaDoc.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CD_REGISTRO_UNITA_DOC%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CD_REGISTRO_NORMALIZ%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DS_REGISTRO_UNITA_DOC%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_REGISTRO_FISC%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CONTROLLO_FORMATO%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.MAX_LEN_NUMERO%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DT_ISTITUZ%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DT_FIRST_VERS%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DT_LAST_VERS%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DT_SOPPRES%>" width="w100" labelWidth="w30" controlWidth="w70"/> 
                </slf:section>
                <slf:section name="<%=StrutTipiForm.ConfigSerieSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_CREA_SERIE%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_CREA_TIPO_SERIE_STANDARD%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.ID_MODELLO_TIPO_SERIE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.NM_TIPO_SERIE_DA_CREARE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DS_TIPO_SERIE_DA_CREARE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CD_SERIE_DA_CREARE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DS_SERIE_DA_CREARE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_TIPO_SERIE_MULT%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />

                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CONSERV_UNLIMITED%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.NI_ANNI_CONSERV%>" width="w100" labelWidth="w30" controlWidth="w10"/> <sl:newLine />
                </slf:section>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['registroUnitaDocList'].status eq 'insert') }">
                    <slf:section name="<%=StrutTipiForm.CreaCriterioRegistroSection.NAME%>" styleClass="importantContainer"> 
                        <slf:lblField name="<%=StrutTipiForm.RegistroCreazioneCriterio.CRITERIO_AUTOM_REGISTRO%>" width="w100" labelWidth="w30" controlWidth="w20"/>
                    </slf:section>
                </c:if>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['registroUnitaDocList'].status eq 'update') }">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=StrutTipiForm.RegistroCreazioneCriterio.CREA_CRITERIO_RAGGR_STANDARD_REGISTRO_BUTTON%>" />
                    </sl:pulsantiera>
                </c:if>
                <slf:section name="<%=StrutTipiForm.TempoConservazioneUnitaDocSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_CONSERV_UNIFORME%>" colSpan="1" labelWidth="w30" controlWidth="w100" /> 
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_CONSERV_ILLIMITATA%>" colSpan="1" labelWidth="w30" controlWidth="w100" /> 
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.NI_AA_CONSERV%>" colSpan="1" labelWidth="w30" controlWidth="w100" /> 
                    <sl:newLine />
                </slf:section>
                <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.ID_REGISTRO_UNITA_DOC%>" />
            </slf:fieldSet>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['registroUnitaDoc'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DUPLICA_REGISTRO_BUTTON%>" />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.LOG_EVENTI%>" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Tipologie unit&agrave; documentarie ammesse</b></div>
                        <slf:listNavBar name="<%= StrutTipiForm.RegistroTipoUnitaDocAmmessoList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTipiForm.RegistroTipoUnitaDocAmmessoList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutTipiForm.RegistroTipoUnitaDocAmmessoList.NAME%>" />
                        <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Periodi di validit&agrave; registro</b></div>
                        <slf:listNavBar name="<%= StrutTipiForm.AaRegistroUnitaDocList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTipiForm.AaRegistroUnitaDocList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutTipiForm.AaRegistroUnitaDocList.NAME%>" />
                        <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Criteri di raggruppamento</b></div>
                <slf:listNavBar name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>"  />
                <slf:listNavBar  name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
