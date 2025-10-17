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

<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="<%= ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/custom/customPollFirmaFasc.js' />" ></script>
        <script type='text/javascript'>
            function leadingZeros(input) {
                if(!isNaN(input.val()) && input.val().length === 1) {
                    input.val('0' + input.val());
                }
            };

            $(document).ready(function () {
                leadingZeros($("#Ore_ts_creazione_elenco_idx_aip_da"));
                leadingZeros($("#Minuti_ts_creazione_elenco_idx_aip_da"));
                leadingZeros($("#Ore_ts_creazione_elenco_idx_aip_a"));
                leadingZeros($("#Minuti_ts_creazione_elenco_idx_aip_a"));

                // MAC#38913 - Firma elenchi – pop-up credenziali non sempre visibile
                $('input[name="operation__firmaElenchiIndiciAipFascHsm"]').on('click', function() {
                    clearTimeout(timeoutPollingIdIndiciAipFascicoli);
                });
                console.log('id del timer dell indice AIP:'+timeoutPollingIdIndiciAipFascicoli);

                // Imposto i campi relativi all'ora e minuti precompilati con 00:00 (da)
                $("#Ts_creazione_elenco_idx_aip_da").on("change", function(){
                    if ($("#Ts_creazione_elenco_idx_aip_da").val()) {
                        if ($("#Ore_ts_creazione_elenco_idx_aip_da").val() === '') 
                            $("#Ore_ts_creazione_elenco_idx_aip_da").val('00');

                        if ($("#Minuti_ts_creazione_elenco_idx_aip_da").val() === '') 
                            $("#Minuti_ts_creazione_elenco_idx_aip_da").val('00');
                    }
                });

                $("#Ore_ts_creazione_elenco_idx_aip_da").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Ore_ts_creazione_elenco_idx_aip_da"));
                    }
                });

                $("#Minuti_ts_creazione_elenco_idx_aip_da").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Minuti_ts_creazione_elenco_idx_aip_da"));
                    }
                });

                // Imposto i campi relativi all'ora e minuti precompilati con 23:59 (a)
                $("#Ts_creazione_elenco_idx_aip_a").on("change", function(){
                    if ($("#Ts_creazione_elenco_idx_aip_a").val()) {
                        if ($("#Ore_ts_creazione_elenco_idx_aip_a").val() === '')
                            $("#Ore_ts_creazione_elenco_idx_aip_a").val('23');

                        if ($("#Minuti_ts_creazione_elenco_idx_aip_a").val() === '')
                            $("#Minuti_ts_creazione_elenco_idx_aip_a").val('59');
                    }
                });

                $("#Ore_ts_creazione_elenco_idx_aip_a").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Ore_ts_creazione_elenco_idx_aip_a"));
                    }
                });

                $("#Minuti_ts_creazione_elenco_idx_aip_a").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Minuti_ts_creazione_elenco_idx_aip_a"));
                    }
                });

                pollIndiciAipFasc();
                $('.credenzialiFirmaBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            var idAmbiente = $("#Id_ambiente").val();
                            var idEnte = $("#Id_ente").val();
                            var idStrut = $("#Id_strut").val();
                            var presenzaNote = $("#Elenchi_con_note").val();
                            var user = $("#User").val();
                            var passwd = $("#Passwd").val();
                            var otp = $("#Otp").val();
                            $(this).dialog("close");

                            var textDialog = "<div id=\"waiting\" class=\"messages infobox \"><ul><span class=\"ui-icon ui-icon-info\"></span><li class=\"message info \">Attendere...</li></ul></div>";
                            $(textDialog).appendTo($("div#content"));
                            $("div#waiting").dialog({
                                autoOpen: true,
                                width: 600,
                                modal: true,
                                closeOnEscape: false,
                                resizable: false,
                                beforeClose: function (event, ui) {
                                    return false;
                                },
                                dialogClass: "noclose"
                            });

                            // MAC#38913 - Firma elenchi – pop-up credenziali non sempre visibile
                            // Disabilito momentaneamente il polling sennò la post non torna piu'!
                            clearTimeout(timeoutPollingIdIndiciAipFascicoli);

                            $.post("ElenchiVersFascicoli.html", {
                                operation: "firmaElenchiIndiciAipFascHsmJs",
                                Id_ambiente: idAmbiente,
                                Id_ente: idEnte,
                                Id_strut: idStrut,
                                Elenchi_con_note: presenzaNote,
                                User: user,
                                Passwd: passwd,
                                Otp: otp
                            }).done(
                                // LOGICA CHIARAMENTE MIGLIORABILE
                                function (data) {
                                    $("div#waiting").dialog("close");
                                    $("div#waiting").remove();
                                    var object = data.map[0];
                                    if (object.error && object.error.length !== 0) {
                                        // Ho ricevuto un errore, gestisco la messageBox
                                        var textDialog = "<div class=\"messages errorbox\"><ul><span class=\"ui-icon ui-icon-alert\"></span>";
                                        $.each(object.error, function () {
                                            textDialog += "<li class=\"message error \">" + this + "</li>";
                                        });
                                        textDialog += "</ul></div>"
                                        $(textDialog).appendTo($("div#content"));
                                        $("div.messages").dialog({
                                            autoOpen: true,
                                            width: 600,
                                            modal: true,
                                            closeOnEscape: true,
                                            resizable: false,
                                            dialogClass: "alertBox",
                                            buttons: {
                                                "Ok": function () {
                                                    $(this).dialog("close");
                                                }
                                            }
                                        });
                                    } else if (object.status && object.status.length !== 0) {
                                        // Sessione già terminata con successo, gestisco la messageBox
                                        var box;
                                        var icon;
                                        var message;
                                        if (object.status === 'OK') {
                                            box = "infobox";
                                            icon = message = "info";
                                        } else {
                                            // WARNING
                                            box = "warnbox";
                                            icon = "alert";
                                            message = "warning";
                                        }

                                        var textDialog = "<div class=\"messages " + box + "\"><ul><span class=\"ui-icon ui-icon-" + icon + "\"></span>";
                                        $.each(object.error, function () {
                                            textDialog += "<li class=\"message " + message + " \">" + this + "</li>";
                                        });
                                        textDialog += "</ul></div>"
                                        $(textDialog).appendTo($("div#content"));
                                        $("div.messages").dialog({
                                            autoOpen: true,
                                            width: 600,
                                            modal: true,
                                            closeOnEscape: true,
                                            resizable: false,
                                            dialogClass: "alertBox",
                                            buttons: {
                                                "Ok": function () {
                                                    $(this).dialog("close");
                                                }
                                            }
                                        });
                                    } else {
                                        /* TODO LB: Secondo me non dovrebbe ricaricare la pagina degli elenchi ma mostrare un messaggio di info e svuotare la lista selezionati
                                         (Ovviamente chiamando un metodo apposito)*/
                                        window.location = "ElenchiVersFascicoli.html?operation=loadListaElenchiIndiciAipFascDaFirmare&cleanFilter=false";
                                    }
                                });
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
            <div class="firmaBox "></div>
            <c:if test="${!empty requestScope.customElenchiVersFascicoliSelect}">
                <div class="credenzialiFirmaBox" title="Inserire credenziali per firmare">
                    <ul>
                        <li class="message info ">
                        <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.USER%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />

                        <div class="containerLeft w100">
                            <label class="slLabel w20" for="Passwd">Password:&nbsp;</label>
                            <input id="Passwd" class="slText w60" type="password" name="Passwd" />
                        </div><sl:newLine />

                        <div class="containerLeft w100">
                            <label class="slLabel w20" for="Otp">OTP:&nbsp;</label>
                            <input id="Otp" class="slText w60" type="password" name="Otp" />
                        </div><sl:newLine />
                        </li>
                    </ul>
                </div>
            </c:if>
            <sl:newLine skipLine="true"/>

            <slf:fieldBarDetailTag name="<%= ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.NAME%>" hideOperationButton="true" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}"/>
            <sl:contentTitle title="<%= ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.DESCRIPTION%>"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <span class="legend">Selezionare gli elenchi indici AIP fascicoli che si desidera firmare</span>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <slf:lblField colSpan="4" name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.ID_AMBIENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.ID_ENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.ID_STRUT%>" />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.ID_ELENCO_VERS_FASC%>" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.TS_CREAZIONE_ELENCO_IDX_AIP_DA%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.ORE_TS_CREAZIONE_ELENCO_IDX_AIP_DA%>" name2="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.MINUTI_TS_CREAZIONE_ELENCO_IDX_AIP_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.TS_CREAZIONE_ELENCO_IDX_AIP_A%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.ORE_TS_CREAZIONE_ELENCO_IDX_AIP_A%>" name2="<%=ElenchiVersFascicoliForm.FiltriElenchiIndiciAipFascDaFirmare.MINUTI_TS_CREAZIONE_ELENCO_IDX_AIP_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
            </slf:fieldSet>

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=ElenchiVersFascicoliForm.ElenchiIdxAipFascDaFirmareBtnList.RICERCA_ELENCHI_IDX_AIP_FASC_DA_FIRMARE%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <span class="legend">Elenchi indici AIP fascicoli da firmare</span>
            </slf:fieldSet>
            <sl:newLine />
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME%>" pageSizeRelated="true"/>
            <slf:selectList name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME%>" addList="true"/>
            <slf:listNavBar  name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:lblField name="<%=ElenchiVersFascicoliForm.ElenchiIdxAipFascDaFirmareBtnList.SELECT_ALL_ELENCHI_INDICI_AIP_FASC%>" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersFascicoliForm.ElenchiIdxAipFascDaFirmareBtnList.DESELECT_ALL_ELENCHI_INDICI_AIP_FASC%>" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersFascicoliForm.ElenchiIdxAipFascDaFirmareBtnList.SELECT_HUNDRED_ELENCHI_INDICI_AIP_FASC%>" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersFascicoliForm.ElenchiIdxAipFascDaFirmareBtnList.FIRMA_ELENCHI_INDICI_AIP_FASC_HSM%>" colSpan="1"/>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <slf:section name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelSection.NAME%>" styleClass="importantContainer">
                <slf:listNavBar name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME%>" pageSizeRelated="true"/>
                <slf:selectList name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME%>"  addList="false"/>
                <slf:listNavBar  name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME%>" />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
