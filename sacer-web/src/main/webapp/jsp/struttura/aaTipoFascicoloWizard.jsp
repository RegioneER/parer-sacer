<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio periodo validita fascicolo" >
        <!--script type="text/javascript">
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
        </script-->
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />

            <sl:contentTitle title="Dettaglio periodo validita fascicolo"/>
           
           

            
             <slf:messageBox /> 
            <sl:contentTitle title="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.DESCRIPTION%>"/>
            <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                <c:if test="${sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.NAME%>" hideBackButton="false"/> 
                </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.AaTipoFascicoloList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            
            
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.CaricamentoPeriodoValiditaFascicolo.NAME%>" styleClass="importantContainer">
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.ID_AA_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.AA_INI_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.AA_FIN_TIPO_FASCICOLO%>" colSpan="2" controlWidth="w20"/>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloClassificazioneSection.NAME%>" styleClass="importantContainer">
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.NI_CHAR_PAD_PARTE_CLASSIF%>" width="w80" labelWidth="w30" controlWidth="w20" /> 
                </slf:section>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
               
                <sl:newLine skipLine="true"/>
                <slf:container width="w90">
                    <slf:listNavBar name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" />
                </slf:container>

                <!--ark-->
                <slf:section name="<%=StrutTipiFascicoloForm.FormatoNumeroListaPartiSection.NAME%>" styleClass="importantContainer">
                    <slf:fieldSet>
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
                    <slf:container width="w90">
                        <slf:listNavBar name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>"/>
                        <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" />
                    </slf:container>
                </slf:section>

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
            </c:if>



        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>