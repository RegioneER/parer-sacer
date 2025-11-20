function poll() {
	setTimeout(function () {
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'ElenchiVersFascicoli.html',
            data: {
                operation: "checkSignatureFuture",
                Id_ambiente: $("#Id_ambiente").val(),
                Id_ente: $("#Id_ente").val(),
                Id_strut: $("#Id_strut").val(),
                Elenchi_con_note: $("#Elenchi_con_note").val()
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
                                window.location = "ElenchiVersFascicoli.html?operation=loadListaElenchiVersFascicoliDaFirmare&cleanFilter=false";
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

// MAC#38913 - Firma elenchi – pop-up credenziali non sempre visibile
var timeoutPollingIdIndiciAipFascicoli=null;
 
function pollIndiciAipFasc() {
    timeoutPollingIdIndiciAipFascicoli=setTimeout(function () {
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'ElenchiVersFascicoli.html',
            data: {
                operation: "checkSignatureIndiciAipFascFuture",
                Id_ambiente: $("#Id_ambiente").val(),
                Id_ente: $("#Id_ente").val(),
                Id_strut: $("#Id_strut").val(),
                Elenchi_con_note: $("#Elenchi_con_note").val()
            },
            success:
                function (data) {
                    var box;
                    var icon;
                    var message;
                    var description;
                    var object = data.map[0];
                    console.log('checkSignatureIndiciAipFascFuture SUCCESS: Status di ritorno: '+object.status);
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
                console.log('checkSignatureIndiciAipFascFuture COMPLETE.');
                if ($('.firmaBox').html().trim() && !$('.firmaBox').data('POPUP')) {
                    $('.firmaBox').data('POPUP', true);
                    $('.firmaBox').dialog({
                        autoOpen: true,
                        width: 600,
                        modal: true,
                        closeOnEscape: false,
                        resizable: false,
                        dialogClass: "alertBox",
                        buttons: {
                                "Chiudi" : function () {
                                    $(this).dialog("close");
                                    $('.firmaBox').removeData('POPUP');
                                    window.location = "ElenchiVersFascicoli.html?operation=loadListaElenchiIndiciAipFascDaFirmare&cleanFilter=false";
                                }
                            }
                        }); 
                    }
                    pollIndiciAipFasc();
                }
            });
        }, 5000); // Aumentato a 5 secondi
    }

