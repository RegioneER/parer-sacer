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
    <sl:head title="Dettaglio struttura" >
        <script type='text/javascript' >
            function disableFirstSelect(obj) {
                if (this.value != "") {
                    $("#Ti_scad_chius_volume").attr("disabled", true);
                } else {
                    if (obj.data[0].value == "") {
                        $("#Ti_scad_chius_volume").attr("disabled", false);
                    }
                }
            }
            $(document).ready(function () {
                
                 $("[name='operation__scaricaStruttura']").click(function() {
                        $('.overlay').hide();
                    }); 
                

                $("#RegistroUnitaDocList tr ").each(function () {
                    var elemento = $(this).find('div[id^="Controllo_formato_"] img');
                    if (elemento.attr('src') === '/sacer/img/checkbox-on.png') {
                        elemento.attr('title', 'Tutte le ud versate rispettano il formato numero del registro');
                    } else {
                        elemento.attr('title', 'Sono presenti ud versate che non rispettano il formato numero del registro');
                    }
                });

                $("#Ti_scad_chius_volume").change(function () {



                    if (this.value != "") {
                        $("#Ti_tempo_scad_chius").attr("disabled", true);
                        $("#Ni_tempo_scad_chius").attr("disabled", true);
                    } else {
                        $("#Ti_tempo_scad_chius").attr("disabled", false);
                        $("#Ni_tempo_scad_chius").attr("disabled", false);
                    }
                });
                $("#Ti_tempo_scad_chius").change($("#Ni_tempo_scad_chius"), disableFirstSelect);
                $("#Ni_tempo_scad_chius").keyup($("#Ti_tempo_scad_chius"), disableFirstSelect);

//                // Gestione del flag gestione fascicoli per abilitare i parametri configurazione fascicolo
//                $("#Fl_gest_fascicoli").change(function () {
//                    var value = $(this).is(":checked");
//                    initFascicolazioneFags(value);
//                });

                //initFascicolazioneFags($("#Fl_gest_fascicoli").is(":checked"));

                initFascicolazioneParameters($($("input[value='FL_GEST_FASCICOLI']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').val());

                $($("input[value='FL_GEST_FASCICOLI']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').on('change', function () {
                    initFascicolazioneParameters($(this).val());
                });

                inserisciIllimitato();

                // NOTA:  il codice si seguito riportato serve per "sganciarsi" dal framework e gestire i trigger attraverso 
                // il passaggio dei parametri JSON tramite POST e non (come effettuato dal framework) tramite GET. 
                // Necessario in quanto in questo caso la lunghezza della query string dei parametri passati tramite GET superava i limiti imposti.
                $("#Nm_strut").unbind('change');
                $("#Id_ambiente_rif").unbind('change');
                $("#Id_ambiente_ente_convenz").unbind('change');

                $("#Nm_strut").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("Strutture.html?operation=triggerInsStrutturaNm_strutOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

                $("#Id_ambiente_rif").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("Strutture.html?operation=triggerInsStrutturaId_ambiente_rifOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

                $("#Id_ambiente_ente_convenz").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("Strutture.html?operation=triggerInsStrutturaId_ambiente_ente_convenzOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });


            });


            function initFascicolazioneParameters(value) {
                if (value !== 'true') {
                    $($("input[value='NUM_GG_SCAD_CRITERIO_FASC_STD']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='NUM_GG_SCAD_CRITERIO_FASC_STD']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='NUM_FASC_CRITERIO_STD']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='NUM_FASC_CRITERIO_STD']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_ABILITA_CONTR_CLASSIF']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_ABILITA_CONTR_CLASSIF']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_ACCETTA_CONTR_CLASSIF_NEG']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_ACCETTA_CONTR_CLASSIF_NEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_FORZA_CONTR_CLASSIF']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_FORZA_CONTR_CLASSIF']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_ABILITA_CONTR_NUMERO']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_ABILITA_CONTR_NUMERO']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_ACCETTA_CONTR_NUMERO_NEG']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_ACCETTA_CONTR_NUMERO_NEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_FORZA_CONTR_NUMERO']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_FORZA_CONTR_NUMERO']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_ABILITA_CONTR_COLLEG']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_ABILITA_CONTR_COLLEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_ACCETTA_CONTR_COLLEG_NEG']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_ACCETTA_CONTR_COLLEG_NEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);

                    $($("input[value='FL_FORZA_CONTR_COLLEG']").parent().parent().find("td")[9]).find('input').val("");
                    $($("input[value='FL_FORZA_CONTR_COLLEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", true);
                } else {

                    $($("input[value='NUM_GG_SCAD_CRITERIO_FASC_STD']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='NUM_FASC_CRITERIO_STD']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_ABILITA_CONTR_CLASSIF']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_ACCETTA_CONTR_CLASSIF_NEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_FORZA_CONTR_CLASSIF']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_ABILITA_CONTR_NUMERO']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_ACCETTA_CONTR_NUMERO_NEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_FORZA_CONTR_NUMERO']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_ABILITA_CONTR_COLLEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_ACCETTA_CONTR_COLLEG_NEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);
                    $($("input[value='FL_FORZA_CONTR_COLLEG']").parent().parent().find("td")[9]).find('input[name="Ds_valore_param_applic_strut_cons"]').attr("readonly", false);

                }
            }

            function inserisciIllimitato() {
                $("#TipologieSerieList tbody tr td[title='9.999']").each(function (index) {
                    $(this).html('Illimitato');
                });
            }

        </script>


        <script type="text/javascript" src="<c:url value='/js/sips/customStruttureMessageBox.js'/>" ></script>

        <script type="text/javascript" >
            $(document).ready(function () {
                $('div[id^="Flag_criterio_standard"] > img[src$="checkbox-off.png"]').attr("src", "./img/alternative/checkbox-on.png");
                $('div[id^="Flag_criterio_standard"] > img[src$="checkbox-warn.png"]').remove();

                $('div[id^="Controllo_formato"] > img[src$="checkbox-off.png"]').attr("src", "./img/alternative/checkbox-on.png");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-warn.png"]').remove();
            });

        </script>
        
        
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />

            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message info ">Seleziona la modalità di caricamento del titolario</li>
                    </ul>                   
                </div>
            </c:if>
            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= StruttureForm.TitolarioCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <c:if test="${!empty requestScope.customBoxSalvataggioStruttura}">
                <div class="messages customBoxNumMaxCompStrut ">
                    <ul>
                        <li class="message info ">Attenzione: numero massimo componenti maggiore di <c:out value="${requestScope.customBoxSalvataggioStruttura}" />. Vuoi proseguire con il salvataggio?</li>
                    </ul>                   
                </div>

                <div class="pulsantieraNumMaxCompStrut">
                    <sl:pulsantiera >
                        <slf:buttonList name="<%= StruttureForm.SalvaStrutturaCustomMessageButtonList.NAME%>"/>
                    </sl:pulsantiera>
                </div>
            </c:if>

            <%@ include file="mascheraCreazioneStruttureTemplate.jspf"%>

            <sl:contentTitle title="Dettaglio struttura"/>

            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['struttureList'].table['empty']) ||(sessionScope.id_struttura_lavorato != null) }">
                <h1><c:out value="${fn:escapeXml(sessionScope.id_struttura_lavorato)}"/></h1>
                <slf:fieldBarDetailTag name="<%=StruttureForm.InsStruttura.NAME%>" hideBackButton="false" />
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['struttureList'].table['empty']) && (sessionScope.id_struttura_lavorato == null)}">
                <slf:listNavBarDetail name="<%= StruttureForm.StruttureList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet>

                <slf:section name="<%=StruttureForm.Descrizione.NAME%>" styleClass="importantContainer">  

                    <slf:lblField name="<%=StruttureForm.InsStruttura.NM_STRUT%>" colSpan= "4" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.DS_STRUT%>"  colSpan= "4"  />
                    <sl:newLine />

                    <c:choose>
                        <c:when test="${(sessionScope['###_FORM_CONTAINER']['insStruttura'].status eq 'view') }">

                            <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_STRUT%>"  colSpan="4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.CD_STRUT_NORMALIZ%>" colSpan= "4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_AMB%>"  colSpan = "2" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_ENTE%>"  colSpan = "4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.ID_CATEG_STRUT%>" colSpan= "4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.CD_IPA%>" colSpan= "4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.DT_INI_VAL_STRUT%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.DT_FINE_VAL_STRUT%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.FL_ARCHIVIO_RESTITUITO%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.FL_CESSATO%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.FL_TEMPLATE%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.PARTIZ_COMPLET%>" colSpan="4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.CESSA_STRUTTURA%>" width="w20" />    
                            <slf:lblField name="<%=StruttureForm.InsStruttura.SCARICA_STRUTTURA%>" width="w20" />                              
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.ImportaParametri.IMPORTA_PARAMETRI_BUTTON%>" width="w20" />                                                        
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.LOG_EVENTI%>" width="w20" />                              
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.ELIMINA_FORMATI_SPECIFICI%>" width="w20" />
                            <sl:newLine />
                        </c:when>
                        <c:otherwise>
                            <slf:lblField name="<%=StruttureForm.InsStruttura.CD_STRUT_NORMALIZ%>" colSpan= "4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.ID_AMBIENTE_RIF%>" colSpan="4" />
                            <sl:newLine />   
                            <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE_RIF%>" colSpan="4" />
                            <sl:newLine />   
                            <slf:lblField name="<%=StruttureForm.InsStruttura.ID_CATEG_STRUT%>" colSpan= "2" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.CD_IPA%>" colSpan= "4" />
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.DT_INI_VAL_STRUT%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.DT_FINE_VAL_STRUT%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.FL_ARCHIVIO_RESTITUITO%>" colSpan="4" />  
                            <sl:newLine />
                            <slf:lblField name="<%=StruttureForm.InsStruttura.FL_TEMPLATE%>" colSpan="2" />  
                            <sl:newLine />
                            <c:if test="${(sessionScope['###_FORM_CONTAINER']['insStruttura'].status eq 'insert') }">
                                <slf:section name="<%=StruttureForm.EnteConvenzionatoSection.NAME%>" styleClass="importantContainer">
                                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_AMBIENTE_ENTE_CONVENZ%>" colSpan="4" />  
                                    <sl:newLine />
                                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE_CONVENZ%>" colSpan="4" />  
                                    <sl:newLine />
                                    <slf:lblField name="<%=StruttureForm.InsStruttura.DT_INI_VAL%>" colSpan="4" />  
                                    <sl:newLine />
                                    <slf:lblField name="<%=StruttureForm.InsStruttura.DT_FINE_VAL%>" colSpan="4" />  
                                </slf:section>
                                <sl:newLine />
                            </c:if>
                            <slf:lblField  name="<%=StruttureForm.InsStruttura.CREA_STRUTTURE_TEMPLATE%>"  colSpan="2" /> 
                        </c:otherwise>
                    </c:choose>
                </slf:section>

                <slf:section name="<%=StruttureForm.NoteSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.InsStruttura.DL_NOTE_STRUT%>" colSpan= "4"/>
                </slf:section>

                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['insStruttura'].status eq 'insert') 
                              && !(sessionScope['###_FORM_CONTAINER']['insStruttura'].status eq 'update')}">

                      <sl:newLine skipLine="true"/>
                      <slf:section name="<%=StruttureForm.EntiConvenzionatiTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.EnteConvenzOrgList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.EnteConvenzOrgList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.EnteConvenzOrgList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.CorrispondenzePingSection.NAME%>" styleClass="importantContainer">
                          <slf:list name="<%= StruttureForm.CorrispondenzePingList.NAME%>" />
                          <slf:listNavBar  name="<%= StruttureForm.CorrispondenzePingList.NAME%>" />
                      </slf:section> 
                      <slf:section name="<%=StruttureForm.RegistriTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.RegistroUnitaDocList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.RegistroUnitaDocList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.RegistroUnitaDocList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.TipoUdTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TipoUnitaDocList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TipoUnitaDocList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.TipoUnitaDocList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.TipoDocTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TipoDocList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TipoDocList.NAME%>"   />
                          <slf:listNavBar  name="<%= StruttureForm.TipoDocList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.TipoFascicoloTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TipoFascicoloList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TipoFascicoloList.NAME%>"   />
                          <slf:listNavBar  name="<%= StruttureForm.TipoFascicoloList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.SerieTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TipologieSerieList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TipologieSerieList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.TipologieSerieList.NAME%>" />
                      </slf:section>
                      <%--<slf:section name="<%=StruttureForm.FormatoFileTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.FormatoFileDocList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.FormatoFileDocList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.FormatoFileDocList.NAME%>" />
                          <sl:pulsantiera>               
                              <slf:lblField  name="<%=StruttureForm.FormatoFileDocButtonList.ELIMINA_TUTTI_FORMATI_FILE_DOC%>" colSpan="4" />
                          </sl:pulsantiera>
                      </slf:section>--%>
                      <slf:section name="<%=StruttureForm.TipoStrutTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TipoStrutDocList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TipoStrutDocList.NAME%>"    />
                          <slf:listNavBar  name="<%= StruttureForm.TipoStrutDocList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.TitolariTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TitolariList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TitolariList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.TitolariList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.TipoRapprTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.TipoRapprCompList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.TipoRapprCompList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.TipoRapprCompList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.CriteriRaggruppamentoTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.CriteriRaggruppamentoList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.CriteriRaggruppamentoList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.CriteriRaggruppamentoList.NAME%>" />
                      </slf:section>
                      <slf:section name="<%=StruttureForm.SubStrutTab.NAME%>" styleClass="importantContainer"> 
                          <slf:listNavBar name="<%= StruttureForm.SubStrutList.NAME%>" pageSizeRelated="true"/>
                          <slf:list name="<%= StruttureForm.SubStrutList.NAME%>"  />
                          <slf:listNavBar  name="<%= StruttureForm.SubStrutList.NAME%>" />
                      </slf:section>
                      <c:if test="${!(sessionScope['###_FORM_CONTAINER']['insStruttura'].status eq 'insert') 
                                    && !(sessionScope['###_FORM_CONTAINER']['insStruttura'].status eq 'update')}">
                          <slf:section name="<%=StruttureForm.SysMigrTab.NAME%>" styleClass="importantContainer"> 
                              <slf:listNavBar name="<%= StruttureForm.SistemiMigrazioneList.NAME%>" pageSizeRelated="true"/>
                              <slf:list name="<%= StruttureForm.SistemiMigrazioneList.NAME%>"  />
                              <slf:listNavBar  name="<%= StruttureForm.SistemiMigrazioneList.NAME%>" />
                          </slf:section>
                      </c:if>                      
                      <slf:section name="<%=StruttureForm.ParametriAmministrazioneSection.NAME%>" styleClass="importantContainer">
                          <sl:pulsantiera>
                              <slf:lblField name="<%=StruttureForm.ParametriStrutturaButtonList.PARAMETRI_AMMINISTRAZIONE_STRUTTURA_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                          </sl:pulsantiera>
                          <slf:editableList name="<%= StruttureForm.ParametriAmministrazioneStrutturaList.NAME%>" multiRowEdit="true"/>
                          <slf:listNavBar  name="<%= StruttureForm.ParametriAmministrazioneStrutturaList.NAME%>" />
                      </slf:section>                      
                      <slf:section name="<%=StruttureForm.ParametriConservazioneSection.NAME%>" styleClass="importantContainer">
                          <sl:pulsantiera>
                              <slf:lblField name="<%=StruttureForm.ParametriStrutturaButtonList.PARAMETRI_CONSERVAZIONE_STRUTTURA_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                          </sl:pulsantiera>
                          <slf:editableList name="<%= StruttureForm.ParametriConservazioneStrutturaList.NAME%>" multiRowEdit="true"/>
                          <slf:listNavBar  name="<%= StruttureForm.ParametriConservazioneStrutturaList.NAME%>" />
                      </slf:section>                      
                      <slf:section name="<%=StruttureForm.ParametriGestioneSection.NAME%>" styleClass="importantContainer">
                          <sl:pulsantiera>
                              <slf:lblField name="<%=StruttureForm.ParametriStrutturaButtonList.PARAMETRI_GESTIONE_STRUTTURA_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                          </sl:pulsantiera>
                          <slf:editableList name="<%= StruttureForm.ParametriGestioneStrutturaList.NAME%>" multiRowEdit="true"/>
                          <slf:listNavBar  name="<%= StruttureForm.ParametriGestioneStrutturaList.NAME%>" />
                      </slf:section>                      
                      <sl:newLine />
                </c:if>
                <sl:newLine skipLine="true"/>
            </slf:fieldSet> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
