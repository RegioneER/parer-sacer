function CustomBoxModificaEnte() {
    $('.pulsantieraModificaEnte').hide();
    $('.customModificaEnteMessageBox').dialog({
        autoOpen: true,
        width: 600,
        modal: true,
        closeOnEscape: true,
        resizable: false,
        dialogClass: "alertBox",
        buttons: {
            "Continua": function () {
                $(this).dialog("close");
                window.location = "Ambiente.html?operation=confermaSalvataggioModificaEnte";
            },
            "Annulla": function () {
                $(this).dialog("close");
                window.location = "Ambiente.html?operation=annullaSalvataggioModificaEnte";
            }
        }
    });
}
