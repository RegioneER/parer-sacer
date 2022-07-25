<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTipiForm.InserimentoWizard.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {

                $("#Fl_aa_key_unita_doc").change(function () {
                    var tipo = $("#Fl_aa_key_unita_doc").val();
                    $("#Ni_min_char_parte").attr("readonly", false);
                    $("#Ni_max_char_parte").attr("readonly", false);

                    if (tipo) {
                        if (tipo === '1') {
                            $("#Ti_pad_sx_parte").val(null);
                            $("#Ti_pad_sx_parte").attr("disabled", true);
                            $("#Ti_char_parte").val("NUMERICO");

                            $("#Ni_min_char_parte").val('4');
                            $("#Ni_max_char_parte").val('4');

                            $("#Ni_min_char_parte").attr("readonly", true);
                            $("#Ni_max_char_parte").attr("readonly", true);
                        }
                    }
                });

                $("#Ni_max_char_parte").change(function () {
                    var max = $("#Ni_max_char_parte").val();
                    var chars = $("#Ti_char_parte").val();

                    checkTiPadSxParte(max, chars);
                });

                $("#Ti_char_parte").change(function () {
                    var max = $("#Ni_max_char_parte").val();
                    var chars = $("#Ti_char_parte").val();

                    checkTiPadSxParte(max, chars);
//                    checkTiParte(chars);
                });


                $('#Ti_parte').change(function () {
                    var input = $(this).val();
                    checkTiParte(input);
                })

                $("#Ti_pad_sx_parte").attr("disabled", true);
//                $("#Ti_parte").attr("disabled", true);

                checkTiParte($('#Ti_parte').val());

            });


            function checkTiParte(input) {

                if (input === 'ANNO') {
                    $('#Ni_min_char_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Ni_max_char_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Ni_min_char_parte').val("4");
                    $('#Ni_max_char_parte').val("4");
                } else if (input === 'REGISTRO') {
                    $('#Ni_min_char_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Ni_max_char_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');

                    var regio = "${fn:escapeXml(sessionScope['###_FORM_CONTAINER']['registroUnitaDoc']['cd_registro_unita_doc'].value)}";
                    $('#Ni_min_char_parte').val(regio.length);
                    $('#Ni_max_char_parte').val(regio.length);
                    $('#Ti_char_parte').val("PARTE_GENERICO");
                } else {
                    $('#Ni_min_char_parte').removeAttr('readonly').css('background-color', '#ffffff');
                    $('#Ni_max_char_parte').removeAttr('readonly').css('background-color', '#ffffff');
                }

                if (input === 'ANNO' || input === 'PROGR' || input === 'REGISTRO') {
                    $('#Dl_valori_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Dl_valori_parte ').val("");
                } else {
                    $('#Dl_valori_parte').removeAttr('readonly').css('background-color', '#ffffff');
                }
            }

            function checkTiPadSxParte(max, chars) {
                $("#Ti_pad_sx_parte").val(null);
                $("#Ti_pad_sx_parte").attr("disabled", true);
                $("#Desc_pad_sx_parte").text("");
                $("#Fl_parte_progr").val('0');
                $("#Fl_parte_progr").attr("disabled", true);
                if (max.length === 0 && chars.length !== 0) {
                    if (chars === 'ALFABETICO' || chars === 'ALFANUMERICO' || chars === 'PARTE_GENERICO') {
                        $("#Ti_pad_sx_parte").attr("disabled", false);
                    }
                }
                if (chars.length !== 0) {
                    if (chars === 'NUMERICO' || chars === 'NUMERI_ROMANI') {
                        $("#Fl_parte_progr").attr("disabled", false);
                    }
                }
            }





//            function checkTiParte(chars) {
//                $("#Ti_parte").val(null);
//                $("#Ti_parte").attr("disabled", true);
//                if (chars.length !== 0) {
//                    if (chars === 'ALFABETICO' || chars === 'ALFANUMERICO') {
//                        $("#Ti_parte").attr("disabled", false);
//                    }
//                }
//            }
//            
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Registri" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />

            <slf:wizard name="<%= StrutTipiForm.InserimentoWizard.NAME%>">
                <slf:wizardNavBar name="<%=StrutTipiForm.InserimentoWizard.NAME%>" />
                <sl:newLine skipLine="true"/>
                <slf:step name="<%= StrutTipiForm.InserimentoWizard.ANNI_PARTE%>">
                    <slf:fieldSet borderHidden="false">
                        <slf:lblField name="<%= StrutTipiForm.DatiAnniParte.AA_MIN_REGISTRO_UNITA_DOC%>" colSpan="4" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%= StrutTipiForm.DatiAnniParte.AA_MAX_REGISTRO_UNITA_DOC%>" colSpan="4" controlWidth="w20"/>
                    </slf:fieldSet>
                </slf:step>
                <slf:step name="<%= StrutTipiForm.InserimentoWizard.PARTI%>">

                    <slf:fieldSet borderHidden="false">
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.NI_PARTE_NUMERO_REGISTRO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.ID_PARTE_NUMERO_REGISTRO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.NM_PARTE_NUMERO_REGISTRO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.DS_PARTE_NUMERO_REGISTRO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.NI_MIN_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.NI_MAX_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.TI_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.TI_CHAR_SEP%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.TI_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.TI_PAD_SX_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:field name="<%=StrutTipiForm.PartiInserimento.DESC_PAD_SX_PARTE%>" controlWidth="w50" />
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.DL_VALORI_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <div id="Desc_dl_valori_parte" class="slText w30">Se i valori accettati sono un insieme, ogni valore deve essere separato mediante "," senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;</div>
                        <sl:newLine/>
                        <%--<slf:lblField name="<%=StrutTipiForm.PartiInserimento.FL_AA_KEY_UNITA_DOC%>" width="w50" controlWidth="w10" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.FL_CD_KEY_UNITA_DOC%>" width="w50" controlWidth="w10" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.PartiInserimento.FL_PARTE_PROGR%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine skipLine="true"/>--%>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=StrutTipiForm.PartiInserimento.ADD_PARTE%>" width="w10"/>
                            <slf:lblField name="<%=StrutTipiForm.PartiInserimento.CLEAN_PARTE%>" width="w10"/>
                        </sl:pulsantiera>
                    </slf:fieldSet>
                    <sl:newLine skipLine="true"/>
                    <div class="livello1"><b><font color="#d3101c">Lista parti del numero del registro</font></b></div>
                            <slf:container width="w90">
                                <slf:listNavBar name="<%= StrutTipiForm.PartiList.NAME%>" pageSizeRelated="true"/>
                                <slf:list name="<%= StrutTipiForm.PartiList.NAME%>"/>
                                <slf:listNavBar  name="<%= StrutTipiForm.PartiList.NAME%>" />
                            </slf:container>
                        </slf:step>

                <slf:step name="<%= StrutTipiForm.InserimentoWizard.DESC_FORMATO_NUMERO%>">
                        <div class="livello1"><b><font color="#d3101c">Lista parti del numero del registro</font></b></div>
                        <slf:container width="w90">
                            <slf:listNavBar name="<%= StrutTipiForm.PartiList.NAME%>" pageSizeRelated="true"/>
                            <slf:list name="<%= StrutTipiForm.PartiList.NAME%>"/>
                            <slf:listNavBar  name="<%= StrutTipiForm.PartiList.NAME%>" />
                        </slf:container>
                        <sl:newLine skipLine="true"/>
                        <slf:fieldSet borderHidden="false">
                            <slf:lblField name="<%=StrutTipiForm.DatiDescFormatoNumero.CD_FORMATO_NUMERO%>" colSpan="2"/><sl:newLine/>
                        <slf:lblField name="<%=StrutTipiForm.DatiDescFormatoNumero.DS_FORMATO_NUMERO%>" colSpan="2"/>
                    </slf:fieldSet>
                </slf:step>

            </slf:wizard>
        </sl:content>

        <sl:footer />
    </sl:body>
</sl:html>