<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('#Ti_parte').change(function () {
                    var input = $(this).val();
                    checkTiParte(input);
                })

                checkTiParte($('#Ti_parte').val());
            });


            function checkTiParte(input) {

                if (input === 'ANNO') {
                    $('#Ni_min_char_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Ni_max_char_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Ni_min_char_parte').val("4");
                    $('#Ni_max_char_parte').val("4");
                } else {
                    $('#Ni_min_char_parte').removeAttr('readonly').css('background-color', '#ffffff');
                    $('#Ni_max_char_parte').removeAttr('readonly').css('background-color', '#ffffff');
                }

                if (input === 'ANNO' || input === 'CLASSIF' || input === 'PROGR_FASC' || input === 'PROGR_SUB_FASC') {
                    $('#Dl_valori_parte').attr('readonly', 'readonly').css('background-color', '#e8e7e6');
                    $('#Dl_valori_parte').val("");
                } else {
                    $('#Dl_valori_parte').removeAttr('readonly').css('background-color', '#ffffff');
                }

                if (input === 'CLASSIF') {
                    $('#Ti_pad_parte').val("FORMAT_CLASSIF");
                    $('#Ti_pad_parte_combo').val("FORMAT_CLASSIF");
                    $("#Desc_pad_parte").text("Esegue la formattazione della classifica per consentirne l'ordinamento");
                    $("#Ti_pad_parte_combo").attr("disabled", true);
                } else {
                    $("#Ti_pad_parte_combo").attr("disabled", false);
                    $("#Desc_pad_parte").text("");
                }
            }
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />

            <slf:wizard name="<%= StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.NAME%>">

                <slf:wizardNavBar name="<%=StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.NAME%>" />

                <sl:newLine skipLine="true"/>

                <slf:step name="<%= StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.DETTAGLIO_PERIODO_STEP%>">
                    <slf:fieldSet borderHidden="false">
                        <slf:lblField name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.ID_AA_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.AA_INI_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.AA_FIN_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                    </slf:fieldSet>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloClassificazioneSection.NAME%>" styleClass="importantContainer">
                        <sl:newLine skipLine="true"/>
                        <%--<slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_CLASSIF%>" colSpan="3" />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_CLASSIF_NEG%>" colSpan="3" />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_CLASSIF%>" colSpan="3" /> <sl:newLine />--%>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.NI_CHAR_PAD_PARTE_CLASSIF%>" width="w80" labelWidth="w30" controlWidth="w20" /> 
                    </slf:section>
                    <%--<sl:newLine />
                    <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloNumeroFascSection.NAME%>" styleClass="importantContainer">
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_NUMERO%>" colSpan="3" /> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_NUMERO_NEG%>" colSpan="3" /> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_NUMERO%>" colSpan="3" /> 
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloCollegamentiSection.NAME%>" styleClass="importantContainer">
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_COLLEG%>" colSpan="3" /> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_COLLEG_NEG%>" colSpan="3" /> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_COLLEG%>" colSpan="3" /> 
                    </slf:section>--%>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=StrutTipiFascicoloForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                        <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriAmministrazioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    </slf:section>
                    <slf:section name="<%=StrutTipiFascicoloForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                        <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriConservazioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    </slf:section>
                    <slf:section name="<%=StrutTipiFascicoloForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                        <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriGestioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    </slf:section>

                </slf:step>

                <slf:step name="<%= StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.XSD_METADATI_PROFILO_FASC_STEP%>">
                    <slf:section name="<%=StrutTipiFascicoloForm.XsdMetadatiProfiloFascicoloSection.NAME%>" styleClass="importantContainer">
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.TI_MODELLO_XSD%>" width="w50" controlWidth="w60" labelWidth="w30"/> <sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.ID_MODELLO_XSD_FASCICOLO%>" width="w50" controlWidth="w60" labelWidth="w30"/> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.VISUALIZZA_MODELLO_XSD_FASCICOLO%>" width="w10" />
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.FL_STANDARD_FIELD%>" width="w50" controlWidth="w60" labelWidth="w30"/><sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.DT_ISTITUZ%>" width="w50" controlWidth="w20" labelWidth="w30" /><sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.DT_SOPPRES%>" width="w50" controlWidth="w20" labelWidth="w30" /><sl:newLine />
                        <sl:newLine/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.ADD_METADATI_PROFILO_FASCICOLO%>" width="w10"/>
                        </sl:pulsantiera>                    
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <div class="livello1"><b><font color="#d3101c">XSD dei metadati di profilo fascicolo</font></b></div>
                            <slf:container width="w90">
                                <slf:listNavBar name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" pageSizeRelated="true"/>
                                <slf:list name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>"/>
                                <slf:listNavBar  name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" />
                            </slf:container>
                        </slf:step>

                <slf:step name="<%= StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.XSD_METADATI_PROFILO_ARK_STEP%>">
                    <slf:section name="<%=StrutTipiFascicoloForm.XsdMetadatiProfiloArkSection.NAME%>" styleClass="importantContainer">
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.TI_MODELLO_XSD%>" width="w50" controlWidth="w60" labelWidth="w30"/> <sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.ID_MODELLO_XSD_FASCICOLO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.VISUALIZZA_MODELLO_XSD_FASCICOLO%>" width="w10"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.FL_STANDARD_FIELD%>" width="w50" controlWidth="w60" labelWidth="w30"/><sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.DT_ISTITUZ%>" width="w50" controlWidth="w20" labelWidth="w30" /><sl:newLine />
                        <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.DT_SOPPRES%>" width="w50" controlWidth="w20" labelWidth="w30" /><sl:newLine />
                        <sl:newLine/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfilo.ADD_METADATI_PROFILO_ARK%>" width="w10"/>
                        </sl:pulsantiera>                    
                    </slf:section>

                    <sl:newLine skipLine="true"/>

                    <div class="livello1"><b><font color="#d3101c">XSD dei metadati di profilo archivistico</font></b></div>
                            <slf:container width="w90">
                                <slf:listNavBar name="<%= StrutTipiFascicoloForm.MetadatiProfiloArkList.NAME%>" pageSizeRelated="true"/>
                                <slf:list name="<%= StrutTipiFascicoloForm.MetadatiProfiloArkList.NAME%>"/>
                                <slf:listNavBar  name="<%= StrutTipiFascicoloForm.MetadatiProfiloArkList.NAME%>" />
                            </slf:container>
                        </slf:step>

                <slf:step name="<%= StrutTipiFascicoloForm.InserimentoPeriodoValiditaWizard.PARTI_STEP%>">
                    <slf:fieldSet borderHidden="false">
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NI_PARTE_NUMERO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.ID_PARTE_NUMERO_FASCICOLO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NM_PARTE_NUMERO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DS_PARTE_NUMERO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NI_MIN_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NI_MAX_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_CHAR_SEP%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_PAD_PARTE_COMBO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_PAD_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <slf:field name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DESC_PAD_PARTE%>" controlWidth="w50" />
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DL_VALORI_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                        <div id="Desc_dl_valori_parte" class="slText w30">Se i valori accettati sono un insieme, ogni valore deve essere separato mediante "," senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;</div>
                        <sl:newLine/>                      
                        <sl:pulsantiera>
                            <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.ADD_PARTE_NUMERO_FASCICOLO%>" width="w10"/>
                            <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.CLEAN_PARTE_NUMERO_FASCICOLO%>" width="w10"/>
                        </sl:pulsantiera>                    
                    </slf:fieldSet>

                    <sl:newLine skipLine="true"/>

                    <div class="livello1"><b><font color="#d3101c">Lista parti del numero del tipo fascicolo</font></b></div>
                    <slf:container width="w90">
                        <slf:listNavBar name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>"/>
                        <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" />
                    </slf:container>
                </slf:step>
            </slf:wizard>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>