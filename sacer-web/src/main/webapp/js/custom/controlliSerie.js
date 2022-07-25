function initNiAnniConserv() {
    $("#Ni_anni_conserv").change(function () {
        var value = $("#Ni_anni_conserv").val();
        if (value) {
            if (value === '9999') {
                $("#Conserv_unlimited").val("1");
            } else {
                $("#Conserv_unlimited").val("0");
            }
        }
    });
}

function initTiRgl() {
    $("#Ti_rgl_nm_tipo_serie").change(function () {
        var value = $(this).val();
        if (value) {
            if (value === 'EREDITA_DA_TIPO_UD_REG') {
                $("#Nm_tipo_serie_da_creare").val("");
                $("#Nm_tipo_serie_da_creare").attr("disabled", true);
            } else {
                $("#Nm_tipo_serie_da_creare").attr("disabled", false);
            }
        }
    });
    $("#Ti_rgl_ds_tipo_serie").change(function () {
        var value = $(this).val();
        if (value) {
            if (value === 'EREDITA_DA_TIPO_UD_REG') {
                $("#Ds_tipo_serie_da_creare").val("");
                $("#Ds_tipo_serie_da_creare").attr("disabled", true);
            } else {
                $("#Ds_tipo_serie_da_creare").attr("disabled", false);
            }
        }
    });
    $("#Ti_rgl_cd_serie").change(function () {
        var value = $(this).val();
        if (value) {
            if (value === 'EREDITA_DA_TIPO_UD_REG') {
                $("#Cd_serie_da_creare").val("");
                $("#Cd_serie_da_creare").attr("disabled", true);
            } else {
                $("#Cd_serie_da_creare").attr("disabled", false);
            }
        }
    });
    $("#Ti_rgl_ds_serie").change(function () {
        var value = $(this).val();
        if (value) {
            if (value === 'EREDITA_DA_TIPO_UD_REG') {
                $("#Ds_serie_da_creare").val("");
                $("#Ds_serie_da_creare").attr("disabled", true);
            } else {
                $("#Ds_serie_da_creare").attr("disabled", false);
            }
        }
    });
    $("#Ti_rgl_anni_conserv").change(function () {
        var value = $(this).val();
        if (value) {
            if (value === 'EREDITA_DA_REG') {
                $("#Conserv_unlimited").val("");
                $("#Ni_anni_conserv").val("");
                $("#Conserv_unlimited").attr("disabled", true);
                $("#Ni_anni_conserv").attr("disabled", true);
            } else {
                $("#Conserv_unlimited").attr("disabled", false);
                $("#Ni_anni_conserv").attr("disabled", false);
            }
        }
    });
    $("#Ti_rgl_range_anni_crea_autom").change(function () {
        var value = $(this).val();
        if (value) {
            if (value === 'EREDITA_DA_REG') {
                $("#Aa_ini_crea_autom").val("");
                $("#Aa_fin_crea_autom").val("");
                $("#Aa_ini_crea_autom").attr("disabled", true);
                $("#Aa_fin_crea_autom").attr("disabled", true);
            } else {
                $("#Aa_ini_crea_autom").attr("disabled", false);
                $("#Aa_fin_crea_autom").attr("disabled", false);
            }
        }
    });
}

function initCreaAutom() {
    $("#Fl_crea_autom").change(function () {
        var tiSelUd = $('#Ti_sel_ud').val();
        var value = $(this).val();
        if (value) {
            if (value === '1') {
                $("#Gg_crea_autom").attr("disabled", false);
                $("#Aa_ini_crea_autom").attr("disabled", false);
                $("#Aa_fin_crea_autom").attr("disabled", false);
                $("#Ti_stato_ver_serie_autom").attr("disabled", false);
                if (tiSelUd && tiSelUd === 'DT_UD_SERIE') {
                    $("#Ni_transcoded_mm_crea_autom").attr("disabled", false);
                }

                $("#Ti_stato_ver_serie_autom").val("DA_VALIDARE");
            } else {
                $("#Gg_crea_autom").val("");
                $("#Aa_ini_crea_autom").val("");
                $("#Aa_fin_crea_autom").val("");
                $("#Ni_transcoded_mm_crea_autom").val("");
                $("#Ti_stato_ver_serie_autom").val("");
                $("#Gg_crea_autom").attr("disabled", true);
                $("#Aa_ini_crea_autom").attr("disabled", true);
                $("#Aa_fin_crea_autom").attr("disabled", true);
                $("#Ni_transcoded_mm_crea_autom").attr("disabled", true);
                $("#Ti_stato_ver_serie_autom").attr("disabled", true);
            }
        } else {
            $("#Gg_crea_autom").val("");
            $("#Aa_ini_crea_autom").val("");
            $("#Aa_fin_crea_autom").val("");
            $("#Ni_transcoded_mm_crea_autom").val("");
            $("#Ti_stato_ver_serie_autom").val("");
            $("#Gg_crea_autom").attr("disabled", true);
            $("#Aa_ini_crea_autom").attr("disabled", true);
            $("#Aa_fin_crea_autom").attr("disabled", true);
            $("#Ni_transcoded_mm_crea_autom").attr("disabled", true);
            $("#Ti_stato_ver_serie_autom").attr("disabled", true);
        }
    });
    $('#Ti_sel_ud').change(function () {
        var creaAutom = $("#Fl_crea_autom").val();
        var value = $(this).val();
        if (value) {
            if (value === 'DT_UD_SERIE') {
                $("#Ni_aa_sel_ud").attr("disabled", false);
                $("#Ni_aa_sel_ud_suc").attr("disabled", false);
                if (creaAutom && creaAutom === '1') {
                    $("#Ni_transcoded_mm_crea_autom").attr("disabled", false);
                }
            } else {
                $("#Ni_aa_sel_ud").val("");
                $("#Ni_aa_sel_ud_suc").val("");
                $("#Ni_transcoded_mm_crea_autom").val("");
                $("#Ni_aa_sel_ud").attr("disabled", true);
                $("#Ni_aa_sel_ud_suc").attr("disabled", true);
                $("#Ni_transcoded_mm_crea_autom").attr("disabled", true);
            }
        }
    });
}

function initTipoSeriePage() {
    initNiAnniConserv();
    initCreaAutom();
}

function initModelliPage() {
    initNiAnniConserv();
    initTiRgl();
    initCreaAutom();
}