/* 
 * Override di CMessagesAlertBox() in classes.js per mostrare il warning relativo all'action 
 * ElenchiVersFascicoliAction/eliminaElenco costituito da 2 bottoni
 */
function CMessagesAlertBox() {

    $('.infoBox').dialog({
        autoOpen : true,
        width : 600,
        modal : false,
        resizable: false,
        dialogClass: "alertBox",
        closeOnEscape : true,
        buttons : {
            "Ok" : function() {
                $(this).dialog("close");
            }
        }
    });
    
    $('.pulsantieraMB').hide();
    $('.warnBox').dialog({
        autoOpen : true,
        width : 600,
        modal : true,
        closeOnEscape : true,
        resizable: false,
        dialogClass: "alertBox",
        buttons : {
            "OK" : function() {
                $(this).dialog("close");
                window.location = "ElenchiVersFascicoli.html?operation=confermaEliminaElenco";
            },
            "Annulla" : function() {
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