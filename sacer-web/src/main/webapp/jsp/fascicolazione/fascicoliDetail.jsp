<%@ page import="it.eng.parer.slite.gen.form.FascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Fascicolo" >   
        <script type="text/javascript">
            $(document).ready(function () {
               // Personalizzo graficamente i campi anno e numero
               $('#Aa_fascicolo_titolo').css({"color": "#000000", "font-size": "13px", "text-align": "left"});
               $("label[for='Aa_fascicolo_titolo']").css({"color": "#000000", "font-size": "13px","text-align": "left", "font-weight": "bold"});
               $('#Cd_key_fascicolo_titolo').css({"color": "#000000", "font-size": "13px","text-align": "left"});
               $("label[for='Cd_key_fascicolo_titolo']").css({"color": "#000000", "font-size": "13px","text-align": "left", "font-weight": "bold"});
            });
        </script>
    </sl:head>    

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>     
            <fieldset class="noborder" style="padding: 5px;">
            <font size="3"><b><slf:field name="<%=FascicoliForm.FascicoloDetail.DS_OGGETTO_FASCICOLO%>" colSpan="4" /></b></font>
            <sl:newLine skipLine="true"/>            
            <slf:lblField name="<%=FascicoliForm.FascicoloDetail.AA_FASCICOLO_TITOLO%>" colSpan="1" labelWidth="w1" controlWidth="w50" />
            <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_KEY_FASCICOLO_TITOLO%>" colSpan="2" labelWidth="w1" controlWidth="w90" />
            <sl:newLine skipLine="true"/>
            <slf:field name="<%=FascicoliForm.FascicoloDetail.URN_FAS%>" colSpan="4" />
            </fieldset>
            <sl:newLine skipLine="true"/>
            <slf:listNavBarDetail name="<%= FascicoliForm.FascicoliList.NAME%>" />
            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="InfoPrincipaliFascicolo">
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>

                    <slf:section name="<%=FascicoliForm.FascicoloDettSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.AA_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_KEY_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_TIPO_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_OGGETTO_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DT_APE_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DT_CHIU_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />                        
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TS_INI_SES%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.URN_FAS%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TI_STATO_CONSERVAZIONE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TI_STATO_FASC_ELENCO_VERS%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                    </slf:section>

                    <slf:section name="<%=FascicoliForm.ComposizioneSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NI_UNITA_DOC%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.UNITA_DOC_FIRST%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.UNITA_DOC_LAST%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_UPD_ANNUL_UNITA_DOC%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_UPD_MODIF_UNITA_DOC%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                    </slf:section>

                    <slf:section name="<%=FascicoliForm.NoteSection.NAME%>" styleClass="importantContainer">
                        <slf:field name="<%=FascicoliForm.FascicoloDetail.DS_NOTA%>" colSpan="4" controlWidth="w90"/>
                        <script type="text/javascript">
                            var nota = $("#Ds_nota");
                            nota.css({
                                'margin-left': "1%", 'margin-right': "1%",
                                'padding': "1%",
                                'background': "white"
                            });
                        </script>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="ContenutoFascicolo">
                <slf:fieldSet borderHidden="false">
                    <slf:field name="<%=FascicoliForm.FascicoloDetail.DS_ABILITAZIONE_UD%>" colSpan="4" controlWidth="w100"/>
                    <script type="text/javascript">
                        var nota = $("#Ds_abilitazione_ud");
                        nota.css({
                            'color': "red"
                        });
                    </script>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=FascicoliForm.UnitaDocumentarieSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%= FascicoliForm.UnitaDocList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.UnitaDocList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.UnitaDocList.NAME%>" />
                    </slf:section>
                    <%-- TODO: da decommentare quanto verranno gestiti i SOTTOSFASCICOLI <sl:newLine skipLine="true"/>
                    <slf:section name="<%=FascicoliForm.SottofascicoliSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%= FascicoliForm.SottofascicoliList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.SottofascicoliList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.SottofascicoliList.NAME%>" />
                    </slf:section>--%>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="ProfiloGeneraleFascicolo">
                <slf:fieldSet borderHidden="false">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_XSD_PROFILO%>" colSpan="1" />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_DEFAULT_PROFILO%>" colSpan="1"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_XSD_PROFILO_GENERALE%>" colSpan="1"  />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_PROFILO_GENERALE%>" colSpan="1"  />
                <sl:newLine />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.PROC_AMMIN%>" colSpan="2"/>                    
                <sl:newLine />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_LIVELLO_RISERV%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=FascicoliForm.SoggettiSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%= FascicoliForm.SoggettiCoinvoltiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.SoggettiCoinvoltiList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.SoggettiCoinvoltiList.NAME%>" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=FascicoliForm.EventiSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%= FascicoliForm.EventiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.EventiList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.EventiList.NAME%>" />
                </slf:section>    
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="ProfiloArchivisticoFascicolo">
                <slf:fieldSet borderHidden="false">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_XSD_SEGNATURA%>" colSpan="1" />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_DEFAULT_SEGNATURA%>" colSpan="1"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_XSD_PROFILO_ARCHIVISTICO%>" colSpan="1"  />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_PROFILO_ARCHIVISTICO%>" colSpan="1"  />
                <sl:newLine />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.INDICE_CLASSIF%>" colSpan="2"/>                    
                <sl:newLine />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_LIVELLO_RISERV%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NI_AA_CONSERVAZIONE%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_INFO_CONSERVAZIONE%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=FascicoliForm.FascicoliCollegatiSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%= FascicoliForm.CollegamentiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.CollegamentiList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.CollegamentiList.NAME%>" />
                </slf:section>   
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="ProfiloNormativoFascicolo">
            <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
                <slf:fieldSet borderHidden="false">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_XSD_NORMATIVO%>" colSpan="1" />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_DEFAULT_NORMATIVO%>" colSpan="1"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_XSD_PROFILO_NORMATIVO%>" colSpan="1"  />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_PROFILO_NORMATIVO%>" colSpan="1"  />
                <sl:newLine skipLine="true"/>
                <slf:field name="<%=FascicoliForm.FascicoloDetail.BL_XML_NORMATIVO%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="ProfiloSpecificoFascicolo">
                <slf:fieldSet borderHidden="false">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_XSD_SPECIFICO%>" colSpan="1" />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_DEFAULT_SPECIFICO%>" colSpan="1"/>
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_XSD_PROFILO_SPECIFICO%>" colSpan="1"  />
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_PROFILO_SPECIFICO%>" colSpan="1"  />
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=FascicoliForm.AttributiFascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%= FascicoliForm.DatiSpecificiFascicoloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.DatiSpecificiFascicoloList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.DatiSpecificiFascicoloList.NAME%>" />
                </slf:section>  
                </slf:fieldSet>
            </slf:tab>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_XML_UNISINCRO_FASC%>" width="w20" labelWidth="w40" controlWidth="w60"  />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>    

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioBottomTabs.NAME%>" tabElement="InfoVersamentoFascicolo">
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.InfoVersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_VERSIONE_XML_SIP%>" width="w80" labelWidth="w30" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_VERSIONE_XML_RAPP%>" width="w80" labelWidth="w30" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_USERID%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_SISTEMA_VERSANTE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_IND_IP_CLIENT%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_IND_SERVER%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TI_CONSERVAZIONE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_FORZA_CONTR_CLASSIF%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_FORZA_CONTR_NUMERO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_FORZA_CONTR_COLLEG%>" width="w100" labelWidth="w15" controlWidth="w70"/>                        
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioBottomTabs.NAME%>" tabElement="XMLRichiestaFascicolo">
                <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.InfoVersatoreSection.NAME%>" styleClass="noborder">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_URN_XML_SIP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_HASH_XML_SIP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_ALGO_HASH_XML_SIP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_ENCODING_HASH_XML_SIP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:field name="<%=FascicoliForm.FascicoloDetail.BL_XML_VERS_SIP%>" colSpan="4" controlWidth="w100"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioBottomTabs.NAME%>" tabElement="XMLRapportoFascicolo">
                <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
                <slf:fieldSet borderHidden="false" >
                    <slf:section name="<%=FascicoliForm.InfoVersatoreSection.NAME%>" styleClass="noborder">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_URN_XML_RAPP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_HASH_XML_RAPP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_ALGO_HASH_XML_RAPP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_ENCODING_HASH_XML_RAPP%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:field name="<%=FascicoliForm.FascicoloDetail.BL_XML_VERS_RAPP%>" colSpan="4" controlWidth="w100"/>
                        <sl:newLine />
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioBottomTabs.NAME%>" tabElement="XMLMetaIndiceAipFascicolo">
                <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.InfoVersatoreSection.NAME%>" styleClass="noborder">
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.DS_URN_AIP_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.DS_URN_NORMALIZ_AIP_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.HASH_PERSONALIZZATO%>" width="w100" labelWidth="w15" controlWidth="w70"/>                        
                        <sl:newLine skipLine="true"/>                        
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.ALGORITMO_PERSONALIZZATO%>" width="w100" labelWidth="w15" controlWidth="w70"/>                        
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.CD_ENCODING_HASH_AIP_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>                        
                        <sl:newLine skipLine="true"/>
                        <slf:field name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.BL_FILE_VER_INDICE_AIP%>" colSpan="4" controlWidth="w100"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.SCARICA_EXTERNAL_METADATA%>" colSpan="1"  />
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>