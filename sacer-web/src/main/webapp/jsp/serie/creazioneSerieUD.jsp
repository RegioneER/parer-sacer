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

<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.CreazioneSerie.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/custom/customPollSerie.js' />" ></script>
        <script type="text/javascript" src="<c:url value='/js/custom/controlliSerie.js'/>"></script>
        <script type="text/javascript">
            if (typeof String.prototype.endsWith !== 'function') {
                String.prototype.endsWith = function (suffix) {
                    return this.indexOf(suffix, this.length - suffix.length) !== -1;
                };
            }

            $(document).ready(function () {
                initNiAnniConserv();
                $("#Ti_creazione").change(function () {
                    var tipo = $("#Ti_creazione").val();
                    $("#Cd_doc_file_input_ver_serie").attr("disabled", true);
                    $("#Ds_doc_file_input_ver_serie").attr("disabled", true);
                    $("#Fl_fornito_ente").attr("disabled", true);
                    $("#Bl_file_input_serie").attr("disabled", true);

                    $("#Dt_reg_unita_doc_da").attr("disabled", false);
                    $("#Dt_reg_unita_doc_a").attr("disabled", false);
                    $("#Dt_creazione_unita_doc_da").attr("disabled", false);
                    $("#Dt_creazione_unita_doc_a").attr("disabled", false);

                    $("#Ds_nota_azione").val("");

                    if (tipo) {
                        $("#Ds_nota_azione").val("Serie creata manualmente mediante " + tipo);
                        if (tipo === 'ACQUISIZIONE_FILE') {
                            $("#Cd_doc_file_input_ver_serie").attr("disabled", false);
                            $("#Ds_doc_file_input_ver_serie").attr("disabled", false);
                            $("#Fl_fornito_ente").attr("disabled", false);
                            $("#Bl_file_input_serie").attr("disabled", false);

                            $("#Dt_reg_unita_doc_da").attr("disabled", true);
                            $("#Dt_reg_unita_doc_a").attr("disabled", true);
                            $("#Dt_creazione_unita_doc_da").attr("disabled", true);
                            $("#Dt_creazione_unita_doc_a").attr("disabled", true);
                        }
                    }
                });

                $("#Nm_tipo_serie").focusout(function () {
                    $("#Disable_dates").trigger('change');
                    var anno = $("#Aa_serie").val();
                    var serie = $("#Nm_tipo_serie").val();
                    if (anno.length !== 0 && serie.length !== 0) {
                        $("#Aa_serie").trigger('change');
                    }
                });

                $("#Disable_dates").change(function () {
                    var value = $("#Disable_dates").val();
                    if (value) {
                        if (value === '0') {
                            $("#Dt_inizio_serie").attr("disabled", false);
                            $("#Dt_fine_serie").attr("disabled", false);

                            $("#Ds_lista_anni_sel_serie").attr("disabled", false);
                        } else {
                            $("#Dt_inizio_serie").attr("disabled", true);
                            $("#Dt_fine_serie").attr("disabled", true);

                            $("#Ds_lista_anni_sel_serie").attr("disabled", true);
                        }
                    }
                });

                $("#Aa_serie").change(function () {
                    var anno = $("#Aa_serie").val();
                    var serie = $("#Nm_tipo_serie").val();

                    var oldAnno = $("#Aa_serie").data('oldYear');
                    var oldCdSerie = $("#Cd_serie").data('realName');

                    var cdSerie = $("#Cd_serie").val();
                    if ((!oldCdSerie && !oldAnno) || cdSerie !== (oldCdSerie + '-' + anno)) {
                        if (cdSerie.endsWith('-' + anno) || cdSerie.indexOf('-' + anno) != -1) {
                            oldCdSerie = cdSerie.substring(0, cdSerie.indexOf('-' + anno));
                            $("#Cd_serie").data('realName', oldCdSerie);
                        } else if (cdSerie.endsWith('-' + oldAnno)) {
                            var tmp = cdSerie.substring(0, cdSerie.indexOf('-' + oldAnno));
                            if (tmp !== oldCdSerie) {
                                oldCdSerie = tmp;
                                $("#Cd_serie").data('realName', tmp);
                            }
                        } else {
                            oldCdSerie = cdSerie;
                            $("#Cd_serie").data('realName', cdSerie);
                        }
                        $("#Aa_serie").data('oldYear', anno);
                    }

                    if (anno.length !== 0) {
                        $("#Cd_serie").val(oldCdSerie + '-' + anno);
                    }

                    if (anno.length !== 0 && serie.length !== 0) {
                        $.getJSON("SerieUD.html", {operation: "populateSeriePadre",
                            Nm_tipo_serie: serie, Aa_serie: anno
                        }).done(function (data) {
                            var cod = data.map[0].Cd_serie_padre;
                            var desc = data.map[0].Ds_serie_padre;
                            var anni = data.map[0].Ni_anni_conserv;
                            var cons = data.map[0].Conserv_unlimited;
                            var codPadreDaCreare = data.map[0].Cd_serie_padre_da_creare;
                            var descPadreDaCreare = data.map[0].Ds_serie_padre_da_creare;
                            var dsListaAnniSel = data.map[0].Ds_lista_anni_sel_serie;
                            $("#Ni_anni_conserv").val(anni);
                            $("#Conserv_unlimited").val(cons);
                            $("#Ds_lista_anni_sel_serie").val(dsListaAnniSel);
                            if (cod !== null && cod.length !== 0 && desc !== null && desc.length !== 0) {
                                $("#Cd_serie_padre").text(cod);
                                $("#Ds_serie_padre").text(desc);
                                $("#Cd_serie_padre_da_creare").val("");
                                $("#Ds_serie_padre_da_creare").val("");
                                $("#Cd_serie_padre_da_creare").attr("readonly", true);
                                $("#Ds_serie_padre_da_creare").attr("readonly", true);
                                $("#Ni_anni_conserv").attr("readonly", true);
                                $("#Conserv_unlimited").attr("disabled", true);
                            } else {
                                $("#Cd_serie_padre").text("");
                                $("#Ds_serie_padre").text("");
                                $("#Cd_serie_padre_da_creare").val(codPadreDaCreare);
                                $("#Ds_serie_padre_da_creare").val(descPadreDaCreare);
                                $("#Cd_serie_padre_da_creare").attr("readonly", false);
                                $("#Ds_serie_padre_da_creare").attr("readonly", false);
                                $("#Ni_anni_conserv").attr("readonly", false);
                                $("#Conserv_unlimited").attr("disabled", false);
                            }
                        });
                    }
                });

                $("#Dt_inizio_serie").focusout(dtSelOnFocusout);
                $("#Dt_fine_serie").focusout(dtSelOnFocusout);

                $("#Ti_creazione").trigger('change');

                if ($("#Nm_tipo_serie").val().length !== 0) {
                    $("#Disable_dates").trigger('change');
                }

                if ($("#Aa_serie").val().length !== 0) {
                    $("#Aa_serie").trigger('change');
                }

                if ($("#Ni_anni_conserv").val().length !== 0) {
                    $("#Ni_anni_conserv").trigger('change');
                }

                poll();

            });

            function dtSelOnFocusout() {
                var dtInizio = $("#Dt_inizio_serie").val();
                var dtFine = $("#Dt_fine_serie").val();
                var anno = $("#Aa_serie").val();
                var serie = $("#Nm_tipo_serie").val();
                var cdSerie = $("#Cd_serie").data('realName');

                if (anno !== null && anno !== ""
                        && serie !== null && serie !== ""
                        && dtInizio !== null && dtInizio !== ""
                        && dtFine !== null && dtFine !== ""
                        ) {
                    editCdSerie(anno, serie, dtInizio, dtFine, cdSerie);
                }
            }

            function editCdSerie(anno, serie, dtInizio, dtFine, cdSerie) {
                $.getJSON("SerieUD.html", {
                    operation: "aggiornaCdSerieDaRangeDate",
                    Nm_tipo_serie: serie, Aa_serie: anno,
                    Dt_inizio_serie: dtInizio, Dt_fine_serie: dtFine,
                    Cd_serie: cdSerie
                }).done(function (data) {
                    var cod = data.map[0].Cd_serie;
                    if (cod && cod !== null && cod !== "") {
                        $("#Cd_serie").val(cod);
                    }
                });
            }
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <div class="serieMessageBox "></div>
            <sl:contentTitle title="<%=SerieUDForm.CreazioneSerie.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <%--<c:out value="${(fn:length(sessionScope['###_NAVHIS_CONTAINER'])) }"/>--%>
            <slf:fieldBarDetailTag name="<%=SerieUDForm.CreazioneSerie.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.TI_CREAZIONE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.NM_TIPO_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CD_SERIE%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <div id="Desc_Cd_serie" class="slText w30">Caratteri consentiti: lettere, numeri, punto, meno , underscore, due punti</div>
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.AA_SERIE%>" width="w100" controlWidth="w10" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DT_INIZIO_SERIE%>" width="w40" controlWidth="w20" labelWidth="w50" />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DT_FINE_SERIE%>" width="w40" controlWidth="w20" labelWidth="w50" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DT_REG_UNITA_DOC_DA%>" width="w40" controlWidth="w20" labelWidth="w50" />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DT_REG_UNITA_DOC_A%>" width="w40" controlWidth="w20" labelWidth="w50" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DT_CREAZIONE_UNITA_DOC_DA%>" width="w40" controlWidth="w20" labelWidth="w50" />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DT_CREAZIONE_UNITA_DOC_A%>" width="w40" controlWidth="w20" labelWidth="w50" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_LISTA_ANNI_SEL_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DISABLE_DATES%>"  />
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_NOTA_AZIONE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <div id="Desc_Bl_file_input_serie" class="containerRight w50">
                    <p>Il file deve essere composto da campi separati da virgola, di cui:</p>
                    <p>la prima riga (intestazione) deve contenere i nomi dei campi</p>
                    <p>le righe successive devono contenere i record da considerare nel calcolo.</p>
                    <p>In caso di campi alfanumerici i cui valori possono contenere virgole, &egrave; necessario delimitare il campo con doppi apici (")</p>
                </div>
                <slf:section name="<%=SerieUDForm.FileSection.NAME%>" styleClass="importantContainer w50">
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CD_DOC_FILE_INPUT_VER_SERIE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_DOC_FILE_INPUT_VER_SERIE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.FL_FORNITO_ENTE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.BL_FILE_INPUT_SERIE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                </slf:section>
                <%--
                SEZIONE DEL CODICE PADRE RIMOSSA COME DA ANALISI 1.5
                <slf:section name="<%=SerieUDForm.PadreSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CD_SERIE_PADRE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_SERIE_PADRE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CD_SERIE_PADRE_DA_CREARE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_SERIE_PADRE_DA_CREARE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                </slf:section>--%>
                <slf:section name="<%=SerieUDForm.ConservSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.NI_ANNI_CONSERV%>" width="w50" controlWidth="w10" labelWidth="w40" />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CONSERV_UNLIMITED%>" width="w50" controlWidth="w10" labelWidth="w50" />
                </slf:section>
                <sl:newLine skipLine="true"/>
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CREA_SERIE_UD%>"  width="w50" />
            </sl:pulsantiera> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
