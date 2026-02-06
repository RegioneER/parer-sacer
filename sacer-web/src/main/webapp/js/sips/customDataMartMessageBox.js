/*
 * File: customDataMartMessageBox.js - Versione Finale Robusta
 * Gestisce l'intera logica di monitoraggio e interazione per la pagina del Data Mart.
 */
var activePollingIntervalId = null; 

$(document).ready(function () {
    CMessagesAlertBox();
    applicaEvidenziazioneAlCaricamento();
    inizializzaLogicaDiMonitoraggio();

    // GESTORI EVENTI
    $('#RichiesteDataMartList tbody').on('click', 'tr', function () {
        stopActivePolling();
        var $rigaCliccata = $(this);
        var datiDaInviare = {
            operation: "caricaNumUdDataMartList",
            idUdDelRichiesta: $rigaCliccata.find('td').eq(0).find('input[name="Id_ud_del_richiesta"]').val(),
            idRichiesta: $rigaCliccata.find('td').eq(1).text().trim(),
            idStrut: $rigaCliccata.find('td').eq(8).find('input[name="Id_strut"]').val(),
            //mappaTotaliUD: $rigaCliccata.find('td').eq(7).find('input[name="Json_totali_ud"]').val() || '{}',
            selectedRigaIdR: $rigaCliccata.find('td').eq(0).find('input[name="Id_ud_del_richiesta"]').val(),
            selectedRigaMotivoR: $rigaCliccata.find('td').eq(3).find('input[name="Ti_mot_cancellazione"]').val(),
            indiceRigaCliccata: $rigaCliccata.index()
        };
        $.ajax({
            type: 'POST', url: 'UnitaDocumentarie.html', data: datiDaInviare,
            success: function (response) { document.open(); document.write(response); document.close(); },
            error: function (jqXHR) { console.error("Errore AJAX [RichiesteDataMartList]: ", jqXHR.statusText); }
        });
    });
    $('#NumUdDataMartList tbody').on('click', 'tr', function () {
        stopActivePolling();
        var $rigaCliccata = $(this);
        var datiDaInviare = {
            operation: "caricaUdDataMartList",
            idUdDelRichiesta: $rigaCliccata.find('input[name="Id_ud_del_richiesta"]').val(),
            idRichiesta: $rigaCliccata.find('input[name="Id_richiesta"]').val(),
            idStrut: $rigaCliccata.find('input[name="Id_strut"]').val(),
            tiStatoUdCancellate: $rigaCliccata.find('td').eq(3).text().trim(),
            tiMotCancellazione: $rigaCliccata.find('input[name="Ti_mot_cancellazione"]').val(),
            selectedRigaIdR: $('#selectedRigaIdR_val').val(),
            selectedRigaMotivoR: $('#selectedRigaMotivoR_val').val(),
            selectedRigaStatoN: $rigaCliccata.find('td').eq(3).text().trim(),
            selectedRigaIdForN: $rigaCliccata.find('input[name="Id_ud_del_richiesta"]').val()
        };
        $.ajax({
            type: 'POST', url: 'UnitaDocumentarie.html', data: datiDaInviare,
            success: function (response) { document.open(); document.write(response); document.close(); },
            error: function (jqXHR) { console.error("Errore AJAX [NumUdDataMartList]: ", jqXHR.statusText); }
        });
    });
    $('input[name="operation__ricercaDataMart"]').on('click', function() {
        stopActivePolling();
        return true;
    });

    // LOGICA DI POLLING
    function inizializzaLogicaDiMonitoraggio() {
        stopActivePolling();
        const monitorDiv = $('#monitoraggio-unificato');
        if (monitorDiv.length === 0) return;
        const statoRichiesta = $('#statoRichiestaSelezionata_val').val();
        const statoInterno = $('#statoInternoRichiesta_val').val();
        if (statoRichiesta === 'EVASA') { monitorDiv.hide(); return; }
        const statiLogiciInCorso = ['INVIATA_A_MS', 'IN_ELABORAZIONE_LOGICA'];
        const statiFisiciInCorso = ['IN_PREPARAZIONE_FISICA', 'IN_CODA_CANCELLAZIONE', 'IN_CANCELLAZIONE_FISICA'];
        if (statiLogiciInCorso.includes(statoInterno)) {
            monitorDiv.show();
            startPollingLogico(monitorDiv.data('id-ud-del-richiesta'), monitorDiv.data('id-richiesta'), $('#selectedRigaMotivoR_val').val());
        } else if (statiFisiciInCorso.includes(statoInterno)) {
            monitorDiv.show();
            startPollingFisico(monitorDiv.data('id-ud-del-richiesta'));
        } else {
            monitorDiv.hide();
        }
    }

    function stopActivePolling() {
        if (activePollingIntervalId) { clearInterval(activePollingIntervalId); activePollingIntervalId = null; }
    }

    // FUNZIONI DI POLLING
    function startPollingLogico(idUdDelRichiesta, idRichiesta, tiMotivo) {
    const url = `/sacer/monitoraggio-datamart/cancellazione-logica-status?idUdDelRichiesta=${idUdDelRichiesta}&idRichiesta=${idRichiesta}&tiMotivo=${tiMotivo}`;
    
    // Nascondi esplicitamente i pulsanti all'avvio del polling
    $('input[name="operation__callMicroservizioDataMart"]').hide();
    $('input[name="operation__recupCancellazioneLogicaDataMart"]').hide();
    $('input[name="operation__eseguiCancellazioneDataMart"]').hide();

    const updateFunction = function() {
        $.getJSON(url, function(data) {
            // Aggiorna sempre la UI (barra e tabella) con i dati ricevuti.
            aggiornaUILogicaCompleta(data);

            const statiFinali = ['PRONTA_PER_FISICA', 'ERRORE_LOGICO', 'ERRORE_INVIO_MS', 'ERRORE_LOGICO_RIPRISTINABILE', 'ERRORE_LOGICO_GESTITO'];
            
            if (statiFinali.includes(data.statoInternoRichiesta)) {
                // Il processo è terminato, quindi fermiamo il polling.
                stopActivePolling();
                console.log("Fase logica terminata. Polling fermato. Aggiorno UI finale senza ricaricare.");
                
                // Ora gestiamo la visibilità dei pulsanti in base al risultato.
                if (data.statoInternoRichiesta === 'PRONTA_PER_FISICA') {
                    // Successo: mostra il pulsante per la fase successiva.
                    $('input[name="operation__eseguiCancellazioneDataMart"]').show();
                } else if ((data.statoInternoRichiesta === 'ERRORE_LOGICO_RIPRISTINABILE')){
                    // Errore: mostra il pulsante "Riprendi cancellazione logica" per permettere un nuovo tentativo.
                    $('input[name="operation__recupCancellazioneLogicaDataMart"]').show().prop('disabled', false);
                } else{
                    // Errore: mostra di nuovo il pulsante "Cancellazione Logica" per permettere un nuovo tentativo.
                    $('input[name="operation__callMicroservizioDataMart"]').show().prop('disabled', false);
                }
            }
        }).fail(function() {
            // Se la comunicazione fallisce, ferma il polling e riattiva il pulsante per riprovare.
            stopActivePolling();
            $('input[name="operation__callMicroservizioDataMart"]').show().prop('disabled', false);
        });
    };
    
    activePollingIntervalId = setInterval(updateFunction, 5000);
    updateFunction();
}
    
    function startPollingFisico(idUdDelRichiesta) {
    const url = `/sacer/monitoraggio-datamart/cancellazione-fisica-status?idUdDelRichiesta=${idUdDelRichiesta}`;
    
    // Nascondi esplicitamente i pulsanti all'avvio del polling
    $('input[name="operation__callMicroservizioDataMart"]').hide();
    $('input[name="operation__recupCancellazioneLogicaDataMart"]').hide();
    $('input[name="operation__eseguiCancellazioneDataMart"]').hide();

    const updateFunction = function() {
        $.getJSON(url, function(data) {
            aggiornaUIFisica(data);

            const statiInterniDiErrore = ['ERRORE_PREPARAZIONE', 'ERRORE_FISICO_CRITICO', 'ERRORE_FISICO_PARZIALE'];
            
            // Controlla se il processo è terminato (con successo o con errore)
            if (data.statoRichiesta === 'EVASA' || statiInterniDiErrore.includes(data.statoInternoRichiesta)) {
                
                if (data.statoRichiesta === 'EVASA') {
                    // ...aggiorniamo dinamicamente la tabella principale.
                    const idUdDelRichiestaCompletata = $('#selectedRigaIdR_val').val();
                    const tiMotivoCompletato = $('#selectedRigaMotivoR_val').val();
                     // Cerca la riga corrispondente nella tabella RichiesteDataMartList
                    $('#RichiesteDataMartList tbody tr').each(function () {
                        var $riga = $(this);
                        var idRiga = $riga.find('td').eq(0).find('input[name="Id_ud_del_richiesta"]').val();
                        var motivoRiga = $riga.find('td').eq(3).find('input[name="Ti_mot_cancellazione"]').val();

                        if (idRiga === idUdDelRichiestaCompletata && motivoRiga === tiMotivoCompletato) {
                            // Trovata! Aggiorniamo il testo nella colonna dello stato.
                            $riga.find('td').eq(6).text('EVASA');
                            
                            // 2. Rimuovi la classe che la rende "cliccabile" e aggiungine una nuova
                            /*$riga.css('cursor', 'default');
                            $riga.addClass('riga-completata'); // Classe per lo stile (es. grigio chiaro)
                            
                            // 3. Disabilita l'handler di click per questa specifica riga
                            $riga.off('click');*/

                            // Se hai un filtro attivo per "DA_EVADERE", potremmo nascondere la riga.
                            // Questo codice la fa sparire con una dissolvenza.
                            //$riga.fadeOut(2000, function() { $(this).remove(); });
                            
                            return false; // Esce dal ciclo .each
                        }
                    });
                }
                
                
                stopActivePolling();
                console.log("Fase fisica terminata. Polling fermato.");
            }
        }).fail(() => stopActivePolling());
    };
    
    activePollingIntervalId = setInterval(updateFunction, 8000);
    updateFunction();
}
    
    // FUNZIONI DI AGGIORNAMENTO UI
    
    function aggiornaUILogicaCompleta(data) {
        const ui = {
            titolo: $('#monitoraggio-titolo'),
            stato: $('#monitoraggio-stato-generale'),
            barra: $('#monitoraggio-progress-bar'),
            testo: $('#monitoraggio-progress-text'),
            tabellaBody: $('#NumUdDataMartList tbody') // Selettore per il corpo della tabella
        };

        // 1. Aggiorna la barra di avanzamento (come prima)
        ui.titolo.text('Avanzamento Cancellazione Logica');
        const statoInterno = data.statoInternoRichiesta || 'In Attesa';
        ui.stato.text(statoInterno.replace(/_/g, ' '));
        let percentuale = (data.totali > 0) ? (data.elaborati / data.totali) * 100 : 0;
        if (statoInterno === 'PRONTA_PER_FISICA') percentuale = 100;
        ui.barra.css('width', `${percentuale.toFixed(2)}%`).text(`${percentuale.toFixed(0)}%`);
        ui.testo.text(`UD Elaborate: ${data.elaborati} / ${data.totali}`);
        if (statoInterno.includes('ERRORE')) {
            ui.barra.css('background-color', '#dc3545').removeClass('progress-bar-animated');
        } else if (statoInterno === 'PRONTA_PER_FISICA') {
            ui.barra.css('background-color', '#198754').removeClass('progress-bar-animated');
        } else {
            ui.barra.css('background-color', '#0d6efd').addClass('progress-bar-striped progress-bar-animated');
        }

        // 2. Ricostruisci la tabella dei conteggi dinamicamente
        ui.tabellaBody.empty(); // Svuota la tabella attuale
        let righeHtml = '';
        
        const conteggi = data.conteggiDettagliati || []; // Ora è una LISTA di oggetti
        let isPari = true;

        // Itera sulla LISTA
        for (const rigaDati of conteggi) {
            // Accedi ai campi per nome
            const nmEnteStrut = rigaDati.nmEnte + " - " + rigaDati.nmStrut;
            const statoUD = rigaDati.tiStatoUdCancellate;
            const conteggio = rigaDati.conteggio;
            const conteggioAnnullate = (rigaDati.niUdStatoAnnullate !== undefined) ? rigaDati.niUdStatoAnnullate : 0;

            const classeCss = isPari ? "rigaPari rigaCorrente" : "rigaDispari rigaCorrente";

            // Costruisci la riga HTML con le 3 colonne visibili corrette
            righeHtml += `<tr>
                            <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.idUdDelRichiesta}" name="Id_ud_del_richiesta"></td>
                            <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.idRichiesta}" name="Id_richiesta"></td>
                            <td title="${nmEnteStrut}" class="${classeCss}">${nmEnteStrut}</td>
                            <td title="${statoUD}" class="${classeCss}">${statoUD}</td>
                            <td title="${conteggio}" class="${classeCss}">${conteggio}</td>
                            <td title="${conteggioAnnullate}" class="${classeCss}">${conteggioAnnullate}</td>
                            <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.idStrut}" name="Id_strut"></td>
                            <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.tiMotCancellazione}" name="Ti_mot_cancellazione"></td>
                          </tr>`;
            isPari = !isPari;
        }

        if (righeHtml === '') {
            righeHtml = '<tr><td colspan="5">Nessun dato da visualizzare.</td></tr>';
        }
        ui.tabellaBody.html(righeHtml);
    }

    function aggiornaUIFisica(data) {
    const ui = {
        titolo: $('#monitoraggio-titolo'),
        stato: $('#monitoraggio-stato-generale'),
        barra: $('#monitoraggio-progress-bar'),
        testo: $('#monitoraggio-progress-text'),
        tabellaBody: $('#NumUdDataMartList tbody')
    };
    const statoInterno = data.statoInternoRichiesta;
    ui.titolo.text('Avanzamento Cancellazione Fisica');
    ui.stato.text(statoInterno ? statoInterno.replace(/_/g, ' ') : 'In attesa...');

    let percentuale = 0;
    let testoAvanzamento = "";
    
    if (data.totali > 0) {
        percentuale = (data.cancellate / data.totali) * 100;
        testoAvanzamento = `UD Cancellate: ${data.cancellate} / ${data.totali}`;
    } else if (data.statoRichiesta === 'EVASA') {
        percentuale = 100;
        testoAvanzamento = `Processo completato: ${data.totali} / ${data.totali} UD cancellate`;
    } else {
        testoAvanzamento = `Stato: ${statoInterno ? statoInterno.replace(/_/g, ' ') : 'N/D'}`;
    }

    // Gestione visibilità e colore in caso di errore
    if (statoInterno && statoInterno.includes('ERRORE')) {
        // Se c'è un errore, mostra una barra piena e rossa per un feedback chiaro.
        ui.barra.css('width', '100%');
        ui.barra.css('background-color', '#dc3545').removeClass('progress-bar-animated progress-bar-striped');
        ui.testo.text(testoAvanzamento); // Mostra il conteggio parziale (se c'è) o lo stato
    } else if (data.statoRichiesta === 'EVASA') {
        ui.barra.css('width', '100%');
        ui.barra.css('background-color', '#198754').removeClass('progress-bar-animated progress-bar-striped');
        ui.testo.text(testoAvanzamento);
    } else {
        // Comportamento normale durante l'avanzamento
        ui.barra.css('width', `${percentuale.toFixed(2)}%`);
        ui.barra.css('background-color', '#0d6efd').addClass('progress-bar-striped progress-bar-animated');
        ui.testo.text(testoAvanzamento);
    }
    
    // Aggiungi la percentuale numerica (opzionale, ma utile)
    ui.barra.text(`${percentuale.toFixed(0)}%`);
    
    // RICOSTRUZIONE TABELLA DEI CONTEGGI
    ui.tabellaBody.empty();
    let righeHtml = '';    
    const conteggi = data.conteggiDettagliati || []; // Ora è una LISTA di oggetti
    let isPari = true;

    // Itera sulla LISTA
    for (const rigaDati of conteggi) {
        // Accedi ai campi per nome
        const nmEnteStrut = rigaDati.nmEnte + " - " + rigaDati.nmStrut;
        const statoUD = rigaDati.tiStatoUdCancellate;
        const conteggio = rigaDati.conteggio;
        const conteggioAnnullate = (rigaDati.niUdStatoAnnullate !== undefined) ? rigaDati.niUdStatoAnnullate : 0;

        const classeCss = isPari ? "rigaPari rigaCorrente" : "rigaDispari rigaCorrente";

        // Costruisci la riga HTML con le 3 colonne visibili corrette
        righeHtml += `<tr>
                        <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.idUdDelRichiesta}" name="Id_ud_del_richiesta"></td>
                        <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.idRichiesta}" name="Id_richiesta"></td>
                        <td title="${nmEnteStrut}" class="${classeCss}">${nmEnteStrut}</td>
                        <td title="${statoUD}" class="${classeCss}">${statoUD}</td>
                        <td title="${conteggio}" class="${classeCss}">${conteggio}</td>
                        <td title="${conteggioAnnullate}" class="${classeCss}">${conteggioAnnullate}</td>
                        <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.idStrut}" name="Id_strut"></td>
                        <td class="${classeCss} displayNone"><input type="hidden" value="${rigaDati.tiMotCancellazione}" name="Ti_mot_cancellazione"></td>
                      </tr>`;
        isPari = !isPari;
    }
    if (righeHtml === '') {
        righeHtml = '<tr><td colspan="5">In attesa di dati...</td></tr>';
    }
    ui.tabellaBody.html(righeHtml);
}

    // FUNZIONI UTILITY
    function applicaEvidenziazioneAlCaricamento() {
        var selIdR = $('#selectedRigaIdR_val').val();
        var selMotivoR = $('#selectedRigaMotivoR_val').val();
        if (selIdR && selMotivoR) {
            $('#RichiesteDataMartList tbody tr').each(function () {
                var $riga = $(this);
                if ($riga.find('td').eq(0).find('input[name="Id_ud_del_richiesta"]').val() === selIdR && $riga.find('td').eq(3).find('input[name="Ti_mot_cancellazione"]').val() === selMotivoR) {
                    $riga.addClass('riga-selezionata');
                    return false;
                }
            });
        }
        var selStatoN = $('#selectedRigaStatoN_val').val();
        var selIdForN = $('#selectedRigaIdForN_val').val();
        var selIdStrutN = $('#selectedRigaIdStrut_val').val();
        if (selStatoN && (selIdForN || selIdR)) {
            var idUdDelRichiestaDaCercare = selIdForN || selIdR;
            $('#NumUdDataMartList tbody tr').each(function () {
                var $riga = $(this);
                 var idStrutRiga = $riga.find('input[name="Id_strut"]').val();
                if ($riga.find('td').eq(3).text().trim() === selStatoN && 
                    $riga.find('input[name="Id_ud_del_richiesta"]').val() === idUdDelRichiestaDaCercare &&
                    (idStrutRiga === selIdStrutN || !selIdStrutN)) { // Il fallback !selIdStrutN serve se il dato non è ancora in sessione
                    
                    $riga.addClass('riga-selezionata');
                    return false; // Esci dal ciclo: hai trovato la riga corretta
                }
            });
        }
    }

    function CMessagesAlertBox() {
        $('.infoBox, .warnBox, .errorBox').dialog({
            autoOpen: true, width: 600, modal: true, closeOnEscape: true, resizable: false,
            dialogClass: "alertBox", buttons: {"Ok": function () { $(this).dialog("close"); }}
        });
    }
});