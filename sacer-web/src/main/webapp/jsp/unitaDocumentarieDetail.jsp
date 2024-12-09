<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Unità Documentaria">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="DETTAGLIO UNIT&Agrave; DOCUMENTARIA"/>

            <c:choose>
                <c:when test="${(sessionScope['###_FORM_CONTAINER']['fakeUnitaDocumentarieList'].table != null) && !(sessionScope['###_FORM_CONTAINER']['fakeUnitaDocumentarieList'].table['empty'])}">
                    <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.UnitaDocumentarieDetail.NAME%>" /> 
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" />
                </c:otherwise>
            </c:choose>

            <slf:section name="<%=UnitaDocumentarieForm.VersamentoAnnullatoUDSection.NAME%>" styleClass="importantContainer">
                <h2><b><font color="#d3101c">Il versamento della presente unità documentaria è stato annullato</font></b></h2>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DT_ANNUL%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NT_ANNUL%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.VISUALIZZA_UD_VERSATA%>" width="w20" position="right" />
                    </slf:section>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="InfoPrincipaliUD">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=UnitaDocumentarieForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=UnitaDocumentarieForm.UnitaDocSection.NAME%>" styleClass="importantContainer">
                        <slf:fieldSet borderHidden="true">
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine />
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.AA_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine/>
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine/>
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_KEY_UNITA_DOC_NORMALIZ%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine/>
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_TIPO_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                            <sl:newLine />
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DL_OGGETTO_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine />
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DT_REG_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine skipLine="true"/>
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DT_CREAZIONE%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                            <sl:newLine />
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.URN_UD%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                            <sl:newLine />
                            <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.TI_STATO_CONSERVAZIONE%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        </slf:fieldSet>
                    </slf:section>    
                    <slf:section name="<%=UnitaDocumentarieForm.ProfiloArchivistico.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_CLASSIF_PRINC%>" colSpan="2" controlWidth="w100"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_FASCIC_PRINC%>" colSpan="2" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_SOTTOFASCIC_PRINC%>" colSpan="2" controlWidth="w100"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_OGGETTO_FASCIC_PRINC%>" colSpan="2" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_OGGETTO_SOTTOFASCIC_PRINC%>" colSpan="2" controlWidth="w100"/>
                        <sl:newLine />
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <%--<slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_STATO_UNITA_DOC%>" colSpan="2" />--%>
                    <%--<sl:newLine skipLine="true"/>--%>
                    <slf:section name="<%=UnitaDocumentarieForm.Composizione.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_TIPO_DOC%>" colSpan="1" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NI_ALLEG%>" colSpan="1"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NI_ANNESSI%>"  colSpan="1"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NI_ANNOT%>"  colSpan="1"/>
                    </slf:section>
                    <sl:newLine />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="InfoVersamentoUD">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_VERSIONE_XML%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.UTENTE_VERS%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.UTENTE%>"  />
                    <sl:newLine />                    
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_IND_IP_CLIENT%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_SISTEMA_VERSANTE%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.TI_CONSERVAZIONE%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_SISTEMA_MIGRAZ%>" />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.FL_FORZA_ACCETTAZIONE%>" />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.FL_FORZA_CONSERVAZIONE%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.FL_FORZA_COLLEGAMENTO%>"  />
                    <sl:newLine />
                    <%--                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DT_CREAZIONE%>"  />
                                        <sl:newLine />--%>
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.FL_UNITA_DOC_FIRMATO%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.TI_ESITO_VERIF_FIRME_UD%>"  />
                    <sl:newLine />
                    <slf:lblField width="w50" labelWidth="w60" controlWidth="w40" name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_MSG_ESITO_VERIF_FIRME_UD%>"  />
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="XMLRichiestaUD">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_HASH_XML_RICH_UD%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_ALGO_HASH_XML_RICH_UD%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_ENCODING_HASH_XML_RICH_UD%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RICH_UD%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RICH_UD_NORMALIZ%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RICH_UD_INIZIALE%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.BL_XML_RICH_UD%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="XMLIndiceUD">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_HASH_XML_INDEX_UD%>"  colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_ALGO_HASH_XML_INDEX_UD%>"  colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_ENCODING_HASH_XML_INDEX_UD%>"  colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_INDEX_UD%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_INDEX_UD_NORMALIZ%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_INDEX_UD_INIZIALE%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.BL_XML_INDEX_UD%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="XMLRispostaUD">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_HASH_XML_RISP_UD%>"  colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_ALGO_HASH_XML_RISP_UD%>"  colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_ENCODING_HASH_XML_RISP_UD%>"  colSpan="2"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RISP_UD%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RISP_UD_NORMALIZ%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RISP_UD_INIZIALE%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.BL_XML_RISP_UD%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="XMLRapportoUD">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_HASH_XML_RAPP_UD%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_ALGO_HASH_XML_RAPP_UD%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_ENCODING_HASH_XML_RAPP_UD%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RAPP_UD%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RAPP_UD_NORMALIZ%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.DS_URN_XML_RAPP_UD_INIZIALE%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.BL_XML_RAPP_UD%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
                <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="ProfiloNormativoUD">
                <slf:fieldSet  borderHidden="false">
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.PROFILO_NORMATIVO%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioTabs.NAME%>" tabElement="UltimoIndiceAIP">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.CD_VER_INDICE_AIP%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.DT_CREAZIONE%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <%--<sl:newLine />--%>
                    <%--<slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.DS_URN%>" width="w100" labelWidth="w10" controlWidth="w80"/>--%>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.URN%>" width="w100" labelWidth="w10" controlWidth="w80"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.URN_NORMALIZZATO%>" width="w100" labelWidth="w10" controlWidth="w80"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.URN_INIZIALE%>" width="w100" labelWidth="w10" controlWidth="w80"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.DS_HASH_INDICE_AIP%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.HASH_PERSONALIZZATO%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.NM_ENTE_CONSERV%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.TI_FORMATO_INDICE_AIP%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.DS_CAUSALE%>" colSpan="4" labelWidth="w10" controlWidth="w60"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.BL_FILE_VER_INDICE_AIP%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                </slf:fieldSet>
                <sl:pulsantiera>
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPLast.SCARICA_INDICE_AIP_LAST%>"  width="w50" labelWidth="w40" controlWidth="w60" />
                </sl:pulsantiera>
            </slf:tab>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_COMP_FILE_UD%>" width="w20" labelWidth="w40" controlWidth="w60" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_DIP_ESIBIZIONE_UD%>" width="w20" labelWidth="w40" controlWidth="w60" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_SIP_UD%>" width="w20" labelWidth="w40" controlWidth="w60" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_RV%>"  width="w20" labelWidth="w40" controlWidth="w60" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_XML_UD%>"  width="w20" labelWidth="w40" controlWidth="w60" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_XML_UNISINCRO%>" width="w20" labelWidth="w40" controlWidth="w60"  />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.SCARICA_DIP_UD%>" width="w20" labelWidth="w40" controlWidth="w60"  />
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.ASSEGNA_PROGRESSIVO%>"  width="w20" labelWidth="w40" controlWidth="w60" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.VISUALIZZA_UD_ANNUL%>" width="w20" labelWidth="w40" controlWidth="w60"  />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaDocumentiUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.DocumentiUDList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.DocumentiUDList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.DocumentiUDList.NAME%>" />
            </slf:tab>   
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaCollegamentiUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.CollegamentiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.CollegamentiList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.CollegamentiList.NAME%>" />
            </slf:tab>   
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaArchiviazioniSecondarieUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.ArchiviazioniSecondarieList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.ArchiviazioniSecondarieList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.ArchiviazioniSecondarieList.NAME%>" />
            </slf:tab>   
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiUD">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.VERSIONE_XSD_DATI_SPEC_UD%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= UnitaDocumentarieForm.DatiSpecificiUDList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.DatiSpecificiUDList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiUDList.NAME%>" />
            </slf:tab>   
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiMigrazioneUD">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.VERSIONE_XSD_DATI_SPEC_MIGR_UD%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneUDList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneUDList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneUDList.NAME%>" />
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaIndiciAIP">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.IndiciAIPList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.IndiciAIPList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.IndiciAIPList.NAME%>" />
            </slf:tab> 
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaVolumiUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" />
            </slf:tab> 
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaElenchiVersamentoUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.ElenchiVersamentoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.ElenchiVersamentoList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.ElenchiVersamentoList.NAME%>" />
            </slf:tab> 
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaSerieAppartenenzaUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.SerieAppartenenzaList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.SerieAppartenenzaList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.SerieAppartenenzaList.NAME%>" />
            </slf:tab> 
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaFascicoliAppartenenzaUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.FascicoliAppartenenzaList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.FascicoliAppartenenzaList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.FascicoliAppartenenzaList.NAME%>" />
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaAggiornamentiMetadatiUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.AggiornamentiMetadatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.AggiornamentiMetadatiList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.AggiornamentiMetadatiList.NAME%>" />
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaNoteUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.NoteList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.NoteList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.NoteList.NAME%>" />
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaStatiConservUD">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.StatiConservazioneUdList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.StatiConservazioneUdList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.StatiConservazioneUdList.NAME%>" />
            </slf:tab>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>