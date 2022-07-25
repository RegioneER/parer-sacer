$(document).ready(function() {
    $('div[id^="Fl_anomalia"] > img[src$="checkbox-on.png"]').attr("src","./img/alternative/checkbox-on.png");
    $('div[id^="Fl_anomalia"] > img[src$="checkbox-off.png"]').attr("src","./img/alternative/checkbox-off.png");
    $('div[id^="Fl_anom_"] > img[src$="checkbox-field-off.png"][alt^=" Non"]').attr("src","./img/checkbox-field-on.png");
    $('div[id^="Fl_anom_"] > img[src$="checkbox-field-on.png"][alt^=" Sel"]').attr("src","./img/checkbox-field-off.png");
});


