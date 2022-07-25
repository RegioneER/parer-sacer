/* 
 * Override di CMessagesAlertBox() in classes.js per mostrare la finestrella di creazione strutture template
 * dipendenti dall'ambiente selezionato
 */
function CMessagesAlertBox() {
    $('.infoBox').dialog({
        autoOpen: true,
        width: 600,
        modal: false,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Ok": function () {
                $(this).dialog("close");
            }
        }
    });

    $('.pulsantieraStruttureTemplate').hide();    
    $('.customBoxStruttureTemplate').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Salva": function () {            
                $(this).dialog("close");                
                //$.post('../../struttura/strutturaRicerca.jsp', {idAmbienteStruttureTemplate:"2"});                
                window.location = "Strutture.html?operation=confermaCreazioneStruttureTemplate&isFromJavaScript=true&idAmbiente=" + $('#Id_ambiente_strutture_template').find('option:selected').val() + "&nmAmbiente=" + $('#Id_ambiente_strutture_template').find('option:selected').text() + "&idEnte=" + $('#Id_ente_strutture_template').find('option:selected').val() + "&nmEnte=" + $('#Id_ente_strutture_template').find('option:selected').text();
            },
            "Annulla": function () {
                $(this).dialog("close");
            }
        }
    })
    //.parent().appendTo($("#spagoLiteAppForm"));

    $('#Id_ambiente_strutture_template').change(function () {
        window.location = "Strutture.html?operation=triggerStruttureTemplateCreatorByJavaScript&idAmbiente=" + this.value;
    });

    $('.warnBox').dialog({
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

    $('.errorBox').dialog({
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
}