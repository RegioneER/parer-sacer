/* 
 * Override di CMessagesAlertBox() in classes.js per mostrare il warning relativo all'action 
 * UnitaDocumentarieAction costituito da 3 bottoni
 */
function CMessagesAlertBox() {
    $('.infoBox').dialog({
        autoOpen : true,
        width : 600,
        modal : false,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
    
    $('.pulsantieraMB').hide();
    $('.customBox').dialog({
        autoOpen : true,
        width : 600,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Importazione" : function() {
                $(this).dialog("close");
                window.location = "Strutture.html?operation=importazioneTitolario";
            },
            "Inserimento manuale" : function() {
                $(this).dialog("close");
                window.location = "Strutture.html?operation=inserimentoManualeTitolario";
            },
            "Annulla" : function() {
                $(this).dialog("close");
                window.location = "Strutture.html?operation=annulla";
            }
        }
    });
    
    $('.warnBox').dialog({
        autoOpen : true,
        width : 600,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
    
    $('.errorBox').dialog({
        autoOpen : true,
        width : 600,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
}