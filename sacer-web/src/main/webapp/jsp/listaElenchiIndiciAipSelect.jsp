<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="<%= ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/custom/customPollFirma.js' />" ></script>
        <script type='text/javascript'>
            function leadingZeros(input) {
                if(!isNaN(input.val()) && input.val().length === 1) {
                    input.val('0' + input.val());
                }
            };

            $(document).ready(function () {
                leadingZeros($("#Ore_dt_creazione_elenco_idx_aip_da"));
                leadingZeros($("#Minuti_dt_creazione_elenco_idx_aip_da"));
                leadingZeros($("#Ore_dt_creazione_elenco_idx_aip_a"));
                leadingZeros($("#Minuti_dt_creazione_elenco_idx_aip_a"));

                // Imposto i campi relativi all'ora e minuti precompilati con 00:00 (da)
                $("#Dt_creazione_elenco_idx_aip_da").on("change", function(){
                    if ($("#Dt_creazione_elenco_idx_aip_da").val()) {
                        if ($("#Ore_dt_creazione_elenco_idx_aip_da").val() === '') 
                            $("#Ore_dt_creazione_elenco_idx_aip_da").val('00');

                        if ($("#Minuti_dt_creazione_elenco_idx_aip_da").val() === '') 
                            $("#Minuti_dt_creazione_elenco_idx_aip_da").val('00');
                    }
                });

                $("#Ore_dt_creazione_elenco_idx_aip_da").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Ore_dt_creazione_elenco_idx_aip_da"));
                    }
                });

                $("#Minuti_dt_creazione_elenco_idx_aip_da").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Minuti_dt_creazione_elenco_idx_aip_da"));
                    }
                });

                // Imposto i campi relativi all'ora e minuti precompilati con 23:59 (a)
                $("#Dt_creazione_elenco_idx_aip_a").on("change", function(){
                    if ($("#Dt_creazione_elenco_idx_aip_a").val()) {
                        if ($("#Ore_dt_creazione_elenco_idx_aip_a").val() === '')
                            $("#Ore_dt_creazione_elenco_idx_aip_a").val('23');

                        if ($("#Minuti_dt_creazione_elenco_idx_aip_a").val() === '')
                            $("#Minuti_dt_creazione_elenco_idx_aip_a").val('59');
                    }
                });

                $("#Ore_dt_creazione_elenco_idx_aip_a").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Ore_dt_creazione_elenco_idx_aip_a"));
                    }
                });

                $("#Minuti_dt_creazione_elenco_idx_aip_a").on('blur', function() {
                    if (this.value) {
                        leadingZeros($("#Minuti_dt_creazione_elenco_idx_aip_a"));
                    }
                });

                pollIndiciAip();
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
                            var flElencoFisc = $("#Fl_elenco_fisc").val();
                            var tiGestElenco = $("#Ti_gest_elenco").val();
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
                                operation: "firmaElenchiIndiciAipHsmJs",
                                Id_ambiente: idAmbiente,
                                Id_ente: idEnte,
                                Id_strut: idStrut,
                                Fl_elenco_fisc: flElencoFisc,
                                Ti_gest_elenco: tiGestElenco,
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
                                        window.location = "ElenchiVersamento.html?operation=loadListaElenchiIndiciAipDaFirmare&cleanFilter=false";
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
                        <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.USER%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />

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

            <slf:fieldBarDetailTag name="<%= ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.NAME%>" hideOperationButton="true" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}"/>
            <sl:contentTitle title="ELENCHI INDICI AIP UNITÀ DOCUMENTARIE DA FIRMARE"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <span class="legend">Selezionare gli elenchi indici AIP che si desidera firmare</span>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.ID_AMBIENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.ID_ENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.ID_STRUT%>" />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.ID_ELENCO_VERS%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.FL_ELENCO_FISC %>" />
                <sl:newLine />
                <slf:lblField colSpan="2" name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.TI_GEST_ELENCO %>" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.DT_CREAZIONE_ELENCO_IDX_AIP_DA%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.ORE_DT_CREAZIONE_ELENCO_IDX_AIP_DA%>" name2="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.MINUTI_DT_CREAZIONE_ELENCO_IDX_AIP_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.DT_CREAZIONE_ELENCO_IDX_AIP_A%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.ORE_DT_CREAZIONE_ELENCO_IDX_AIP_A%>" name2="<%=ElenchiVersamentoForm.FiltriElenchiIndiciAipDaFirmare.MINUTI_DT_CREAZIONE_ELENCO_IDX_AIP_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
            </slf:fieldSet>

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareButtonList.RICERCA_ELENCHI_INDICI_AIP_DA_FIRMARE%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <span class="legend">Elenchi indici AIP da firmare</span>
            </slf:fieldSet>
            <sl:newLine />
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" pageSizeRelated="true"/>
            <slf:selectList name="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" addList="true"/>
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:lblField name="<%=ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareButtonList.SELECT_ALL_ELENCHI_INDICI_AIP%>" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareButtonList.DESELECT_ALL_ELENCHI_INDICI_AIP%>" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareButtonList.SELECT_HUNDRED_ELENCHI_INDICI_AIP%>" colSpan="1"/>
                <slf:lblField name="<%=ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareButtonList.FIRMA_ELENCHI_INDICI_AIP_HSM%>" colSpan="1"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareButtonList.MARCA_ELENCHI_INDICI_AIP%>" colSpan="1"/>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <slf:section name="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelSection.NAME%>" styleClass="importantContainer">
                <slf:listNavBar name="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" pageSizeRelated="true"/>
                <slf:selectList name="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>"  addList="false"/>
                <slf:listNavBar  name="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
