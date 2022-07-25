<%@ page import="it.eng.parer.slite.gen.form.FascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Fascicolo" />
      
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Dettaglio fascicolo"/>
            <sl:newLine skipLine="true"/>

            <slf:listNavBarDetail name="<%= FascicoliForm.FascicoliList.NAME%>" />

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="InfoPrincipaliFascicolo">
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>

                    <%--
                    <slf:section name="<%=FascicoliForm.SoggProduttoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_AMBIENTE_ENTE_CONVENZ%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_ENTE_CONVENZ%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NM_ENTE_CONVENZ%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                    </slf:section>
                    --%>

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
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.AMMIN_TITOL%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.PROC_AMMIN%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.NI_AA_CONSERVAZIONE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_LIVELLO_RISERV%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TS_INI_SES%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TI_STATO_CONSERVAZIONE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.TI_STATO_FASC_ELENCO_VERS%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                    </slf:section>

                    <slf:section name="<%=FascicoliForm.SegnaturaArchivisticaSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_COMPOSITO_VOCE_TITOL%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:label name="<%=FascicoliForm.FascicoloDetail.LABEL_TREE%>" labelWidth="w15"/>
                        <slf:tree name="<%=FascicoliForm.TreeClassif.NAME%>" additionalJsonParams="\"core\" : { \"expand_selected_onload\" : true }"/>
                        <script type="text/javascript">
                            var tree = $("#tree_TreeClassif");

                            tree.prev("div.skipLine").remove();
                            tree.css({'display': "flex"});
                            tree.on('loaded.jstree', function () {
                                tree.jstree('open_all');
                            });
                        </script>
                        <sl:newLine />

                        <slf:section name="<%=FascicoliForm.FascicoloAppartenenzaDettSection.NAME%>" styleClass="importantContainer">
                            <slf:lblField name="<%=FascicoliForm.FascicoloDetail.AA_FASCICOLO_PADRE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                            <sl:newLine />
                            <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_KEY_FASCICOLO_PADRE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                            <sl:newLine />
                            <slf:lblField name="<%=FascicoliForm.FascicoloDetail.DS_OGGETTO_FASCICOLO_PADRE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        </slf:section>
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

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="InfoVersamentoFascicolo">
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
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_XSD_PROFILO%>" colSpan="1"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_DEFAULT_PROFILO%>" colSpan="1"/>
                        <sl:newLine />
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.CD_XSD_SEGNATURA%>" colSpan="1"/>
                        <slf:lblField name="<%=FascicoliForm.FascicoloDetail.FL_DEFAULT_SEGNATURA%>" colSpan="1"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="XMLRichiestaFascicolo">
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

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="XMLRapportoFascicolo">
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

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="XMLMetaFileFascicolo">
                <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.InfoVersatoreSection.NAME%>" styleClass="noborder">
                        <slf:lblField name="<%=FascicoliForm.MetaFileFascicoloDetail.DS_URN_FILE_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaFileFascicoloDetail.DS_URN_NORMALIZ_FILE_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaFileFascicoloDetail.DS_HASH_FILE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaFileFascicoloDetail.DS_ALGO_HASH_FILE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaFileFascicoloDetail.CD_ENCODING_HASH_FILE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:field name="<%=FascicoliForm.MetaFileFascicoloDetail.BL_FILE_VER_INDICE_AIP%>" colSpan="4" controlWidth="w100"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioTabs.NAME%>" tabElement="XMLMetaIndiceAipFascicolo">
                <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=FascicoliForm.InfoVersatoreSection.NAME%>" styleClass="noborder">
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.DS_URN_AIP_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.DS_URN_NORMALIZ_AIP_FASCICOLO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.HASH_PERSONALIZZATO%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.NM_ENTE_CONSERV%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <%--<slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.DS_HASH_FILE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.DS_ALGO_HASH_FILE%>" width="w100" labelWidth="w15" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.CD_ENCODING_HASH_FILE%>" width="w100" labelWidth="w15" controlWidth="w70"/>--%>
                        <sl:newLine skipLine="true"/>
                        <slf:field name="<%=FascicoliForm.MetaIndiceAipFascicoloDetail.BL_FILE_VER_INDICE_AIP%>" colSpan="4" controlWidth="w100"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="UnitaDocumentarie">
                <slf:fieldSet borderHidden="false">
                    <slf:field name="<%=FascicoliForm.FascicoloDetail.DS_ABILITAZIONE_UD%>" colSpan="4" controlWidth="w100"/>
                    <script type="text/javascript">
                            var nota = $("#Ds_abilitazione_ud");
                            nota.css({
                                'color': "red"
                            });
                    </script>
                    <sl:newLine skipLine="true"/>
                    <slf:listNavBar name="<%= FascicoliForm.UnitaDocList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.UnitaDocList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.UnitaDocList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="AmministrazioniPartecipanti">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%= FascicoliForm.AmministrazioniPartecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.AmministrazioniPartecList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.AmministrazioniPartecList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="SoggettiCoinvolti">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%= FascicoliForm.SoggettiCoinvoltiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.SoggettiCoinvoltiList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.SoggettiCoinvoltiList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="Responsabili">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%= FascicoliForm.ResponsabiliList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.ResponsabiliList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.ResponsabiliList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="UOResponsabili">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%= FascicoliForm.UOResponsabiliList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.UOResponsabiliList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.UOResponsabiliList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="Collegamenti">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%= FascicoliForm.CollegamentiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.CollegamentiList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.CollegamentiList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= FascicoliForm.FascicoliDettaglioListsTabs.NAME%>" tabElement="ElvFascicoli">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%= FascicoliForm.ElvFascicoliList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FascicoliForm.ElvFascicoliList.NAME%>" />
                    <slf:listNavBar  name="<%= FascicoliForm.ElvFascicoliList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>