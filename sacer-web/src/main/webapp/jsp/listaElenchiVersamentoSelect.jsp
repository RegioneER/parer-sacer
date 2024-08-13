<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="<%=ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/custom/customPollFirma.js' />" ></script>
        <script type='text/javascript'>
            function leadingZeros(input) {
                if(!isNaN(input.val()) && input.val().length === 1) {
                    input.val('0' + input.val());
                }
            };

            $(document).ready(function () {
                leadingZeros($("#Ore_dt_creazione_elenco_da"));
                leadingZeros($("#Minuti_dt_creazione_elenco_da"));
                leadingZeros($("#Ore_dt_creazione_elenco_a"));
                leadingZeros($("#Minuti_dt_creazione_elenco_a"));

                // Imposto i campi relativi all'ora e minuti precompilati con 00:00 (da)
                $("#Dt_creazione_elenco_da").on("change", function(){
                    if ($("#Dt_creazione_elenco_da").val()) {
                        if ($("#Ore_dt_creazione_elenco_da").val() === '') 
                            $("#Ore_dt_creazione_elenco_da").val('00');

                        if ($("#Minuti_dt_creazione_elenco_da").val() === '') 
                            $("#Minuti_dt_creazione_elenco_da").val('00');
                    }
                });

                $("#Ore_dt_creazione_elenco_da").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Ore_dt_creazione_elenco_da"));
                    }
                });

                $("#Minuti_dt_creazione_elenco_da").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Minuti_dt_creazione_elenco_da"));
                    }
                });

                // Imposto i campi relativi all'ora e minuti precompilati con 23:59 (a)
                $("#Dt_creazione_elenco_a").on("change", function(){
                    if ($("#Dt_creazione_elenco_a").val()) {
                        if ($("#Ore_dt_creazione_elenco_a").val() === '')
                            $("#Ore_dt_creazione_elenco_a").val('23');

                        if ($("#Minuti_dt_creazione_elenco_a").val() === '')
                            $("#Minuti_dt_creazione_elenco_a").val('59');
                    }
                });

                $("#Ore_dt_creazione_elenco_a").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Ore_dt_creazione_elenco_a"));
                    }
                });

                $("#Minuti_dt_creazione_elenco_a").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Minuti_dt_creazione_elenco_a"));
                    }
                });

                poll();
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
                            var flElencoFisc = $("#Fl_elenco_fisc").val();
                            var tiGestElenco = $("#Ti_gest_elenco").val();
                            var tiValidElenco = $("#Ti_valid_elenco").val();
                            var tiModValidElenco = $("#Ti_mod_valid_elenco").val();
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

                            $.post("ElenchiVersamento.html", {
                                operation: "firmaElenchiHsmJs",
                                Id_ambiente: idAmbiente,
                                Id_ente: idEnte,
                                Id_strut: idStrut,
                                Elenchi_con_note: presenzaNote,
                                Fl_elenco_fisc: flElencoFisc,
                                Ti_gest_elenco: tiGestElenco,
                                Ti_valid_elenco: tiValidElenco,
                                Ti_mod_valid_elenco: tiModValidElenco,
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
                                        window.location = "ElenchiVersamento.html?operation=loadListaElenchiVersamentoDaFirmare&cleanFilter=false";
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
            <c:if test="${!empty requestScope.customElenchiVersamentoSelect}">
                <div class="credenzialiFirmaBox" title="Inserire credenziali per firmare">
                    <ul>
                        <li class="message info ">
                        <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.USER%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />

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

            <c:if test="${((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}">
                <slf:fieldBarDetailTag name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NAME%>" hideOperationButton="true" />
            </c:if>
            <sl:contentTitle title="ELENCHI UNITÀ DOCUMENTARIE DA VALIDARE"/>
            <sl:contentTitle title="Selezionare gli elenchi di versamento che si desidera firmare/validare" showHelpBtn="false"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ID_AMBIENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ID_ENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ID_STRUT%>" />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ID_ELENCO_VERS%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ELENCHI_CON_NOTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.FL_ELENCO_FISC%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.TI_VALID_ELENCO%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.TI_MOD_VALID_ELENCO%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.TI_GEST_ELENCO%>" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.DT_CREAZIONE_ELENCO_DA%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ORE_DT_CREAZIONE_ELENCO_DA%>" name2="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.MINUTI_DT_CREAZIONE_ELENCO_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.DT_CREAZIONE_ELENCO_A%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.ORE_DT_CREAZIONE_ELENCO_A%>" name2="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.MINUTI_DT_CREAZIONE_ELENCO_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
            </slf:fieldSet>

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiDaFirmare.RICERCA_ELENCHI_DA_FIRMARE_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <c:if test="${sessionScope['tiValidElenco'] == 'FIRMA'}"><span class="legend">Elenchi di versamento da firmare</span></c:if>
                <c:if test="${sessionScope['tiValidElenco'] == 'NO_FIRMA'}"><span class="legend">Elenchi di versamento da validare</span></c:if>
            </slf:fieldSet>
            <sl:newLine />
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" pageSizeRelated="true"/>
            <slf:selectList name="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" addList="true"/>
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:buttonList name="<%= ElenchiVersamentoForm.ListaElenchiVersamentoDaFirmareButtonList.NAME%>" >
                    <slf:lblField name="<%=ElenchiVersamentoForm.ListaElenchiVersamentoDaFirmareButtonList.SELECT_ALL_ELENCHI_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ListaElenchiVersamentoDaFirmareButtonList.DESELECT_ALL_ELENCHI_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ListaElenchiVersamentoDaFirmareButtonList.SELECT_HUNDRED_ELENCHI_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ListaElenchiVersamentoDaFirmareButtonList.VALIDA_ELENCHI_BUTTON%>" colSpan="2"/>
                </slf:buttonList>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <c:if test="${sessionScope['tiValidElenco'] == 'FIRMA'}"><span class="legend">Elenchi di versamento selezionati per la firma</span></c:if>
                <c:if test="${sessionScope['tiValidElenco'] == 'NO_FIRMA'}"><span class="legend">Elenchi di versamento selezionati per la validazione</span></c:if>
            </slf:fieldSet>
            <sl:newLine />
            <!--  piazzo la lista con i selezionati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" pageSizeRelated="true"/>
            <slf:selectList name="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>"  addList="false"/>
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
