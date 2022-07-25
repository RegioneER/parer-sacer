function poll() {
    setTimeout(function () {
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'ElenchiVersamento.html',
            data: {
                operation: "checkSignatureFuture",
                Id_ambiente: $("#Id_ambiente").val(),
                Id_ente: $("#Id_ente").val(),
                Id_strut: $("#Id_strut").val(),
                Elenchi_con_note: $("#Elenchi_con_note").val(),
                Fl_elenco_fisc: $("#Fl_elenco_fisc").val(),
                Ti_gest_elenco: $("#Ti_gest_elenco").val(),
                Ti_valid_elenco: $("#Ti_valid_elenco").val(),
                Ti_mod_valid_elenco: $("#Ti_mod_valid_elenco").val()
            },
            success:
                    function (data) {
                        var box;
                        var icon;
                        var message;
                        var description;
                        var object = data.map[0];
                        switch (object.status) {
                            case "OK":
                                box = "infobox";
                                icon = message = "info";
                                description = object.info;
                                break;
                            case "WARNING":
                                box = "warnbox";
                                icon = "alert";
                                message = "warning";
                                description = object.info;
                                break;
                            default:
                                // ERROR
                                box = "errorbox";
                                icon = "alert";
                                message = "error";
                                description = object.error;
                                break;
                        }
                        var msg = '<div class="messages ' + box + '"><ul><span class="ui-icon ui-icon-' + icon + '"></span><li class="message ' + message + '">';
                        var counter = 0;
                        if (object.status !== 'WORKING' && object.status !== 'NO_SESSION') {
                            counter++;
                            msg += description;
                        }
                        msg += '</li></ul></div>';

                        if (counter > 0) {
                            $('.firmaBox').append(msg);
                        } else {
                            if (!$('.firmaBox').data('POPUP')) {
                                $('.firmaBox').html('');
                            }
                        }
                    },
            complete: function () {
                if ($('.firmaBox').html().trim() && !$('.firmaBox').data('POPUP')) {
                    $('.firmaBox').data('POPUP', true);
                    $('.firmaBox').dialog({
                        autoOpen: true,
                        width: 600,
                        modal: true,
                        closeOnEscape: true,
                        resizable: false,
                        dialogClass: "alertBox",
                        buttons: {
                            "Ok": function () {
                                $(this).dialog("close");
                                $('.firmaBox').removeData('POPUP');
                                window.location = "ElenchiVersamento.html?operation=loadListaElenchiVersamentoDaFirmare&cleanFilter=false";
//                                var verSerieCreata = $(".firmaWarningBox #VerSerie").serialize();
//                                window.location = "SerieUD.html?operation=reloadSerie&" + verSerieCreata;
                            }
//                            ,
//                            "Annulla": function () {
//                                $('.firmaBox').removeData('POPUP');
//                                $(this).dialog("close");
//                            }
                        }
                    });
                }
                poll();
            }
        });
    }, 2500);
}

function pollIndiciAip() {
    setTimeout(function () {
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'ElenchiVersamento.html',
            data: {
                operation: "checkSignatureIndiciAipFuture",
                Id_ambiente: $("#Id_ambiente").val(),
                Id_ente: $("#Id_ente").val(),
                Id_strut: $("#Id_strut").val(),
                Fl_elenco_fisc: $("#Fl_elenco_fisc").val(),
                Ti_gest_elenco: $("#Ti_gest_elenco").val()
            },
            success:
                function (data) {
                    var box;
                    var icon;
                    var message;
                    var description;
                    var object = data.map[0];
                    $('.firmaBox').data('TO_MARK', false);
                    switch (object.status) {
                        case "OK":
                            box = "infobox";
                            icon = message = "info";
                            description = object.info;
                            $('.firmaBox').data('TO_MARK', true);
                            break;
                        case "WARNING":
                            box = "warnbox";
                            icon = "alert";
                            message = "warning";
                            description = object.info;
                            break;
                        default:
                            // ERROR
                            box = "errorbox";
                            icon = "alert";
                            message = "error";
                            description = object.error;
                            break;
                        }
                        var msg = '<div class="messages ' + box + '"><ul><span class="ui-icon ui-icon-' + icon + '"></span><li class="message ' + message + '">';
                        var counter = 0;
                        if (object.status !== 'WORKING' && object.status !== 'NO_SESSION') {
                            counter++;
                            msg += description;
                        }
                        msg += '</li></ul></div>';

                        if (counter > 0) {
                            $('.firmaBox').append(msg);
                        } else {
                            if (!$('.firmaBox').data('POPUP')) {
                                $('.firmaBox').html('');
                            }
                        }
                    },
            complete: function () {
                if ($('.firmaBox').html().trim() && !$('.firmaBox').data('POPUP')) {
                    $('.firmaBox').data('POPUP', true);
                    $('.firmaBox').dialog({
                        autoOpen: true,
                        width: 600,
                        modal: true,
                        closeOnEscape: false,
                        open: function(event, ui) {
                            if ( $('.firmaBox').data('TO_MARK') ) {
                                $(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
                            }
                        },
                        resizable: false,
                        dialogClass: "alertBox",
                        buttons: {
                            "Completa processo": function () {
                                // Change message and disable button
                                var box="infobox";
                                var icon="info";
                                var message="info";
                                var description="Marcatura avviata! Attendere...";
                                var msg = '<div class="messages ' + box + '"><ul><span class="ui-icon ui-icon-' + icon + '"></span><li class="message ' + message + '">';
                                msg += description;
                                msg += '</li></ul></div>';
                                $('.firmaBox').html(msg);
                                $(".ui-dialog-buttonpane button:contains('Completa processo')").attr("disabled", true).addClass("ui-state-disabled");

                                // Launch marca
                                $.ajax({
                                    type: 'POST',
                                    datatype: 'json',
                                    url: 'ElenchiVersamento.html',
                                    async: false,
                                    data: {
                                        operation: "startMarcaFromJs",
                                        Id_ambiente: $("#Id_ambiente").val(),
                                        Id_ente: $("#Id_ente").val(),
                                        Id_strut: $("#Id_strut").val(),
                                        Fl_elenco_fisc: $("#Fl_elenco_fisc").val(),
                                        Ti_gest_elenco: $("#Ti_gest_elenco").val()
                                    },
                                    success: function (data) {
                                        var box;
                                        var icon;
                                        var message;
                                        var description;
                                        var object = data.map[0];
                                        switch (object.status) {
                                            case "OK":
                                                box = "infobox";
                                                icon = message = "info";
                                                description = object.info;
                                                break;
                                            case "WARNING":
                                                box = "warnbox";
                                                icon = "alert";
                                                message = "warning";
                                                description = object.info;
                                                break;
                                            default:
                                                // ERROR
                                                box = "errorbox";
                                                icon = "alert";
                                                message = "error";
                                                description = object.error;
                                                break;
                                        }
                                        var msg = '<div class="messages ' + box + '"><ul><span class="ui-icon ui-icon-' + icon + '"></span><li class="message ' + message + '">';
                                        var counter = 0;
                                        if (object.status !== 'WORKING' && object.status !== 'NO_SESSION') {
                                            counter++;
                                            msg += description;
                                        }
                                        msg += '</li></ul></div>';

                                        if (counter > 0) {
                                            $('.firmaBox').html(msg);
                                        } else {
                                            if (!$('.firmaBox').data('POPUP')) {
                                                $('.firmaBox').html('');
                                            }
                                        }
                                    },
                                    complete: function () {
                                        // Returns the result and enable close button
                                        var buttons = $('.firmaBox').dialog('option', 'buttons');
                                        $('.firmaBox').dialog('option', 'buttons', buttons);
                                        $('.firmaBox').dialog( "option", "closeOnEscape", true);
                                        $(".ui-dialog-buttonpane button:contains('Completa processo')").attr("disabled", true).addClass("ui-state-disabled");
                                    }
                                });
                            },
                            "Chiudi" : function () {
                                $('.firmaBox').removeData('POPUP');
                                $(this).dialog("close");
                                window.location = "ElenchiVersamento.html?operation=loadListaElenchiIndiciAipDaFirmare&cleanFilter=false";
                            }
                        }
                    });

                    if( $('.firmaBox').data('TO_MARK') ) {
                        $(".ui-dialog-buttonpane button:contains('Chiudi')").attr("disabled", true).addClass("ui-state-disabled");
                    } else {
                        $(".ui-dialog-buttonpane button:contains('Completa processo')").attr("disabled", true).addClass("ui-state-disabled");
                    }
                }
                pollIndiciAip();
            }
        });
    }, 2500);
}

function pollSerie() {
    setTimeout(function () {
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'SerieUD.html',
            data: {
                operation: "checkSignatureFuture",
                Id_ambiente: $("#Id_ambiente").val(),
                Id_ente: $("#Id_ente").val(),
                Id_strut: $("#Id_strut").val()
            },
            success:
                    function (data) {
                        var box;
                        var icon;
                        var message;
                        var description;
                        var object = data.map[0];
                        switch (object.status) {
                            case "OK":
                                box = "infobox";
                                icon = message = "info";
                                description = object.info;
                                break;
                            case "WARNING":
                                box = "warnbox";
                                icon = "alert";
                                message = "warning";
                                description = object.info;
                                break;
                            default:
                                // ERROR
                                box = "errorbox";
                                icon = "alert";
                                message = "error";
                                description = object.error;
                                break;
                        }
                        var msg = '<div class="messages ' + box + '"><ul><span class="ui-icon ui-icon-' + icon + '"></span><li class="message ' + message + '">';
                        var counter = 0;
                        if (object.status !== 'WORKING' && object.status !== 'NO_SESSION') {
                            counter++;
                            msg += description;
                        }
                        msg += '</li></ul></div>';

                        if (counter > 0) {
                            $('.firmaBox').append(msg);
                        } else {
                            if (!$('.firmaBox').data('POPUP')) {
                                $('.firmaBox').html('');
                            }
                        }
                    },
            complete: function () {
                if ($('.firmaBox').html().trim() && !$('.firmaBox').data('POPUP')) {
                    $('.firmaBox').data('POPUP', true);
                    $('.firmaBox').dialog({
                        autoOpen: true,
                        width: 600,
                        modal: true,
                        closeOnEscape: true,
                        resizable: false,
                        dialogClass: "alertBox",
                        buttons: {
                            "Ok": function () {
                                $(this).dialog("close");
                                $('.firmaBox').removeData('POPUP');
                                window.location = "SerieUD.html?operation=loadListaSerieDaFirmare&cleanFilter=false";
//                                var verSerieCreata = $(".firmaWarningBox #VerSerie").serialize();
//                                window.location = "SerieUD.html?operation=reloadSerie&" + verSerieCreata;
                            }
//                            ,
//                            "Annulla": function () {
//                                $('.firmaBox').removeData('POPUP');
//                                $(this).dialog("close");
//                            }
                        }
                    });
                }
                pollSerie();
            }
        });
    }, 2500);
}
