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
    
    $('.pulsantieraAssociazioniScaduteRestArch').hide();
    $('.customBoxAssociazioniScaduteRestArch').dialog({
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