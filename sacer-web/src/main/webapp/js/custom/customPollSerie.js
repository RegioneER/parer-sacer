function poll() {
    return setInterval(function () {
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: 'SerieUD.html',
            data: {
                operation: "checkSerieFuture"
            },
            success:
                    function (data) {
                        var msg = '<div class="messages serieWarningBox"><ul><li class="message warning ">';
                        var div = '';
                        var array = data.map[0].array;
                        var counter = 0;
                        var cdSeries = '';
                        array.forEach(function (entry) {
                            if (entry.RESULT !== 'WORKING') {
                                counter++;
                                cdSeries += '<br/>TIPO: ' + entry.TIPO_CREAZIONE + ' , CODICE: ' + entry.CODICE_SERIE + ' , ANNO: ' 
                                        + entry.ANNO_SERIE + ',<br/>ESITO: ' + (entry.RESULT === 'OK' ? '<b>OK</b>' : '<b>NEGATIVO</b><br/>');
                                div += '<div><input id="VerSerie" type="hidden" value="' + entry.ID_VERSIONE + '" name="VerSerie"></div>';
                            }
                        });
                        if (counter > 1) {
                            msg += 'Le operazioni sulle serie ' + cdSeries + '<br/><br/>sono state eseguite. Vuoi visualizzare le serie?';
                        } else if (counter === 1) {
                            msg += 'L\'operazione sulla serie ' + cdSeries + '<br/><br/>&egrave; stata eseguita. Vuoi visualizzare la serie?';
                        }
                        msg += '</li>' + div + '</ul></div>';
                        if (counter > 0) {
                            $('.serieMessageBox').append(msg);
                        } else {
                            if (!$('.serieMessageBox').data('POPUP')) {
                                $('.serieMessageBox').html('');
                            }
                        }
                    },
            complete: function () {
                if ($('.serieMessageBox').html().trim() && !$('.serieMessageBox').data('POPUP')) {
                    $('.serieMessageBox').data('POPUP', true);
                    $('.serieMessageBox').dialog({
                        autoOpen: true,
                        width: 600,
                        modal: true,
                        closeOnEscape: true,
                        resizable: false,
                        dialogClass: "alertBox",
                        buttons: {
                            "Ok": function () {
                                $(this).dialog("close");
                                $('.serieMessageBox').removeData('POPUP');
                                var verSerieCreata = $(".serieWarningBox #VerSerie").serialize();
                                window.location = "SerieUD.html?operation=reloadSerie&" + verSerieCreata;
                            },
                            "Annulla": function () {
                                $('.serieMessageBox').removeData('POPUP');
                                $(this).dialog("close");
                            }
                        }
                    });
                }
            }
        });
    }, 2500);
}
