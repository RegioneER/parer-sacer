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

<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.ModelliTipiSerieDetail.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/custom/controlliSerie.js'/>"></script>
        <script type="text/javascript">
            $(document).ready(function () {

                initModelliPage();
                if ($("#Ni_anni_conserv").val().length !== 0) {
                    $("#Ni_anni_conserv").trigger('change');
                }
                if ($("#Ti_rgl_nm_tipo_serie").val().length !== 0) {
                    $("#Ti_rgl_nm_tipo_serie").trigger('change');
                }
                if ($("#Ti_rgl_ds_tipo_serie").val().length !== 0) {
                    $("#Ti_rgl_ds_tipo_serie").trigger('change');
                }
                if ($("#Ti_rgl_cd_serie").val().length !== 0) {
                    $("#Ti_rgl_cd_serie").trigger('change');
                }
                if ($("#Ti_rgl_ds_serie").val().length !== 0) {
                    $("#Ti_rgl_ds_serie").trigger('change');
                }
                if ($("#Ti_rgl_anni_conserv").val().length !== 0) {
                    $("#Ti_rgl_anni_conserv").trigger('change');
                }
                if ($("#Fl_crea_autom").val().length !== 0) {
                    $("#Fl_crea_autom").trigger('change');
                }
                if ($("#Ti_rgl_range_anni_crea_autom").val().length !== 0) {
                    $("#Ti_rgl_range_anni_crea_autom").trigger('change');
                }
                if ($('#Ti_sel_ud').val().length !== 0) {
                    $('#Ti_sel_ud').trigger('change');
                }
                if ($("#Fl_crea_autom").val().length !== 0) {
                    $("#Fl_crea_autom").trigger('change');
                }

                $('.datiSpecBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            var tipoUd = $("#Id_tipo_unita_doc_dati_spec_combo").val();
                            var tipoDoc = $("#Id_tipo_doc_dati_spec_combo").val();
                            if (tipoUd) {
                                var nmTipoUd = $("#Id_tipo_unita_doc_dati_spec_combo option:selected").text();
                                $.getJSON("ModelliSerie.html", {operation: "populateIdTipoUnitaDocDatiSpecOnTriggerJs",
                                    Id_tipo_unita_doc_dati_spec: tipoUd,
                                    Nm_tipo_unita_doc_dati_spec: nmTipoUd
                                }).done(function (data) {
                                    CAjaxDataFormWalk(data);
                                });
                            } else if (tipoDoc) {
                                var nmTipoDoc = $("#Id_tipo_doc_dati_spec_combo option:selected").text();
                                $.getJSON("ModelliSerie.html", {operation: "populateIdTipoDocDatiSpecOnTriggerJs",
                                    Id_tipo_doc_dati_spec: tipoDoc,
                                    Nm_tipo_doc_dati_spec: nmTipoDoc
                                }).done(function (data) {
                                    CAjaxDataFormWalk(data);
                                });
                            }
                            $(this).dialog("close");
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                $('#Id_ente').change(function () {
                    var value = $(this).val();
                    if (value) {
                        $.getJSON("ModelliSerie.html", {operation: "triggerModelliTipiSerieDetailId_enteOnTriggerJs",
                            Id_ente: value
                        }).done(function (data) {
                            CAjaxDataFormWalk(data);
                        });
                    }
                });
                $('#Id_strut').change(function () {
                    var value = $(this).val();
                    if (value) {
                        $.getJSON("ModelliSerie.html", {operation: "triggerModelliTipiSerieDetailId_strutOnTriggerJs",
                            Id_strut: value
                        }).done(function (data) {
                            CAjaxDataFormWalk(data);
                        });
                    }
                });

            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <c:if test="${!empty requestScope.customModelloTipoUdBox || !empty requestScope.customModelloTipoDocBox}">
                <div class="messages datiSpecBox ">
                    <ul>
                        <li class="message info ">
                            <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_AMBIENTE%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                            <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_ENTE%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                            <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_STRUT%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                            <c:choose>
                                <c:when test="${!empty requestScope.customModelloTipoUdBox }">
                                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_TIPO_UNITA_DOC_DATI_SPEC_COMBO%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                                </c:when>
                                <c:otherwise>
                                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_TIPO_DOC_DATI_SPEC_COMBO%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                                </c:otherwise>
                            </c:choose>
                        </li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="<%=ModelliSerieForm.ModelliTipiSerieDetail.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <c:choose>
                <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliTipiSerieList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= ModelliSerieForm.ModelliTipiSerieDetail.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['modelliTipiSerieList'].status eq 'insert'}"/> 
                </c:when>   
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= ModelliSerieForm.ModelliTipiSerieList.NAME%>" />   
                </c:otherwise>
            </c:choose>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_AMBIENTE%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                <slf:section name="<%=ModelliSerieForm.InfoModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.DS_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.DT_ISTITUZ%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.DT_SOPPRES%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_TIPO_SERIE_DA_CREARE%>" width="w50" controlWidth="w30" labelWidth="w60" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_NM_TIPO_SERIE%>" controlWidth="w50" controlPosition="left"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.DS_TIPO_SERIE_DA_CREARE%>" width="w50" controlWidth="w30" labelWidth="w60" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_DS_TIPO_SERIE%>" controlWidth="w50" controlPosition="left"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.CD_SERIE_DA_CREARE%>" width="w50" controlWidth="w30" labelWidth="w60" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_CD_SERIE%>" controlWidth="w50" controlPosition="left"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.DS_SERIE_DA_CREARE%>" width="w50" controlWidth="w30" labelWidth="w60" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_DS_SERIE%>" controlWidth="w50" controlPosition="left" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.CONSERV_UNLIMITED%>" width="w50" controlWidth="w10" labelWidth="w60" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NI_ANNI_CONSERV%>" width="w50" controlWidth="w10" labelWidth="w60" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_ANNI_CONSERV%>" controlWidth="w50" controlPosition="left"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_CONSERVAZIONE_SERIE%>" width="w50" controlWidth="w40" labelWidth="w60" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_CONSERVAZIONE_SERIE%>" controlWidth="w50" controlPosition="left"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_SEL_UD%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NI_AA_SEL_UD%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NI_AA_SEL_UD_SUC%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NI_UNITA_DOC_VOLUME%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.FL_CONTROLLO_CONSIST_OBBLIG%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.FL_CREA_AUTOM%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.GG_CREA_AUTOM%>" width="w50" controlWidth="w10" labelWidth="w60" /><sl:newLine /> 
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.AA_INI_CREA_AUTOM%>" width="w40" controlWidth="w10" labelWidth="w80" />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.AA_FIN_CREA_AUTOM%>" width="w20" controlWidth="w30" labelWidth="w10" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_RANGE_ANNI_CREA_AUTOM%>" controlWidth="w40" controlPosition="left" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NI_TRANSCODED_MM_CREA_AUTOM%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_STATO_VER_SERIE_AUTOM%>" width="w100" controlWidth="w60" labelWidth="w30" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_TIPO_UNITA_DOC_DATI_SPEC%>" width="w100" controlWidth="w60" labelWidth="w30" />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_TIPO_DOC_DATI_SPEC%>" width="w100" controlWidth="w60" labelWidth="w30" />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.FiltriDatiSpecModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_TIPO_UNITA_DOC_DATI_SPEC%>" width="w50" controlWidth="w60" labelWidth="w40" />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.CERCA_TIPO_UD%>" width="w10" />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.RIMUOVI_TIPO_UD%>" width="w10" /><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_TIPO_DOC_DATI_SPEC%>" width="w50" controlWidth="w60" labelWidth="w40" />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.CERCA_TIPO_DOC%>" width="w10" />
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.RIMUOVI_TIPO_DOC%>" width="w10" />
                    <slf:field name="<%=ModelliSerieForm.ModelliTipiSerieDetail.TI_RGL_FILTRO_TI_DOC%>" controlWidth="w40" controlPosition="left"/><sl:newLine />
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.LOG_EVENTI%>" />
            </sl:pulsantiera>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['modelliTipiSerieList'].status eq 'view') }">
                <slf:section name="<%=ModelliSerieForm.NoteModelloSerieSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= ModelliSerieForm.NoteModelloTipoSerieList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ModelliSerieForm.NoteModelloTipoSerieList.NAME%>" />
                    <slf:listNavBar  name="<%= ModelliSerieForm.NoteModelloTipoSerieList.NAME%>" />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.RegoleAcquisizioneModelloSerieSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= ModelliSerieForm.RegoleAcquisizioneList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ModelliSerieForm.RegoleAcquisizioneList.NAME%>" />
                    <slf:listNavBar  name="<%= ModelliSerieForm.RegoleAcquisizioneList.NAME%>" />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.RegoleFiltraggioTipoDocModelloSerieSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= ModelliSerieForm.RegoleFiltraggioList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ModelliSerieForm.RegoleFiltraggioList.NAME%>" />
                    <slf:listNavBar  name="<%= ModelliSerieForm.RegoleFiltraggioList.NAME%>" />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.FiltriDatiSpecSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= ModelliSerieForm.DatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ModelliSerieForm.DatiSpecList.NAME%>" />
                    <slf:listNavBar  name="<%= ModelliSerieForm.DatiSpecList.NAME%>" />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.RegoleRappresentazioneModelloSerieSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= ModelliSerieForm.RegoleRapprList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ModelliSerieForm.RegoleRapprList.NAME%>" />
                    <slf:listNavBar  name="<%= ModelliSerieForm.RegoleRapprList.NAME%>" />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.StrutModelloSection.NAME%>" styleClass="noborder w100">
                    <slf:listNavBar name="<%= ModelliSerieForm.StrutModelloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ModelliSerieForm.StrutModelloList.NAME%>" />
                    <slf:listNavBar  name="<%= ModelliSerieForm.StrutModelloList.NAME%>" />
                </slf:section>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
