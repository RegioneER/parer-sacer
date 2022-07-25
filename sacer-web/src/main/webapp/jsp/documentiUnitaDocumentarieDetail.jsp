<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio Documento">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>
                <slf:messageBox/>
                <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO DOCUMENTO"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <c:choose>
                <c:when test="${empty navTable or navTable eq 'vuoto'}">
                    <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NAME%>" hideOperationButton="true"/>
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= UnitaDocumentarieForm.DocumentiUDList.NAME%>" />
                </c:otherwise>
            </c:choose>

            <slf:section name="<%=UnitaDocumentarieForm.VersamentoAnnullatoDocSection.NAME%>" styleClass="importantContainer">
                <h2><b><font color="#d3101c">Il versamento del presente documento è stato annullato</font></b></h2>
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DT_ANNUL_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NT_ANNUL_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                    </slf:section>
                    <slf:section name="<%=UnitaDocumentarieForm.VersamentoAnnullatoUDSection.NAME%>" styleClass="importantContainer">
                <h2><b><font color="#d3101c">Il versamento della presente unità documentaria è stato annullato</font></b></h2>
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DT_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w70" labelWidth="w30"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NT_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w70" labelWidth="w30"/>
                    </slf:section>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioTabs.NAME%>" tabElement="InfoPrincipali">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=UnitaDocumentarieForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=UnitaDocumentarieForm.UnitaDocSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.AA_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_TIPO_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DL_OGGETTO_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DT_REG_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DT_CREAZIONE_UD%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=UnitaDocumentarieForm.ProfiloDoc.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.TI_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_TIPO_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DL_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_AUTORE_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.URN_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NI_ORD_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_KEY_DOC_VERS%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DT_CREAZIONE%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_TIPO_STRUT_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioTabs.NAME%>" tabElement="InfoVersamento">
                <!--  piazzo i campi da visualizzare nel tab -->
                <slf:fieldSet borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_VERSIONE_XML%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.UTENTE_VERS%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.UTENTE%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />                    
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.TI_CONSERVAZIONE%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NM_SISTEMA_MIGRAZ%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.FL_FORZA_ACCETTAZIONE%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.FL_FORZA_CONSERVAZIONE%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DT_CREAZIONE%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.FL_DOC_FIRMATO%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.TI_ESITO_VERIF_FIRME%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_MSG_ESITO_VERIF_FIRME%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.TI_STATO_ELENCO_VERS%>" colSpan="2" controlWidth="w100" labelWidth="w20" />                    
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.TI_STATO_CONSERVAZIONE%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                    <sl:newLine />                
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.ID_ELENCO_VERS%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>                    
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioTabs.NAME%>" tabElement="XMLRichiestaDoc">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_HASH_XML_RICH_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_ALGO_HASH_XML_RICH_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_ENCODING_HASH_XML_RICH_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RICH_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RICH_DOC_NORMALIZ%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RICH_DOC_INIZIALE%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.BL_XML_RICH_DOC%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioTabs.NAME%>" tabElement="XMLRispostaDoc">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_HASH_XML_RISP_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_ALGO_HASH_XML_RISP_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_ENCODING_HASH_XML_RISP_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RISP_DOC%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RISP_DOC_NORMALIZ%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RISP_DOC_INIZIALE%>"  colSpan="4" controlWidth="w100"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.BL_XML_RISP_DOC%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioTabs.NAME%>" tabElement="XMLRapportoDoc">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_HASH_XML_RAPP_DOC%>"  width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_ALGO_HASH_XML_RAPP_DOC%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.CD_ENCODING_HASH_XML_RAPP_DOC%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RAPP_DOC%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RAPP_DOC_NORMALIZ%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.DS_URN_XML_RAPP_DOC_INIZIALE%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                    <sl:newLine skipLine="true"/>
                    <slf:field name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.BL_XML_RAPP_DOC%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioTabs.NAME%>" tabElement="NoteDoc">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.NT_DOC%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.SCARICA_COMP_FILE_DOC%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.SCARICA_DIP_ESIBIZIONE_DOC%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.SCARICA_SIP_DOC%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.SCARICA_XML_DOC%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.SCARICA_DIP_DOC%>" colSpan="3" />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioListsTabs.NAME%>" tabElement="ListaComponenti">
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
                <slf:list name="<%= UnitaDocumentarieForm.ComponentiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.ComponentiList.NAME%>" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar name="<%= UnitaDocumentarieForm.ComponentiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
            </slf:tab>  

            <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiMigrazioneUD">
            </slf:tab>  
            <%--<slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioListsTabs.NAME%>" tabElement="ListaVolumi">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                <c:choose>
                    <c:when test="${empty navTable}">
                        <slf:listNavBar  name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" mainNavTable="vuoto" />
                    </c:when>
                    <c:otherwise>
                        <slf:listNavBar  name="<%= UnitaDocumentarieForm.VolumiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    </c:otherwise>
                </c:choose>
            </slf:tab>--%>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiDoc">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.VERSIONE_XSD_DATI_SPEC_DOC%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:container width="w50">
                    <c:choose>
                        <c:when test="${empty navTable}">
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiDocList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                        </c:when>
                        <c:otherwise>                        
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiDocList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                        </c:otherwise>
                    </c:choose>
                    <slf:list name="<%= UnitaDocumentarieForm.DatiSpecificiDocList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    <c:choose>
                        <c:when test="${empty navTable}">
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiDocList.NAME%>" mainNavTable="vuoto" />
                        </c:when>
                        <c:otherwise>                        
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiDocList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                        </c:otherwise>
                    </c:choose>
                </slf:container>
            </slf:tab>

            <slf:tab  name="<%= UnitaDocumentarieForm.DocumentiDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiMigrazioneDoc">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=UnitaDocumentarieForm.DocumentiUnitaDocumentarieDetail.VERSIONE_XSD_DATI_SPEC_MIGR_DOC%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:container width="w50">
                    <c:choose>
                        <c:when test="${empty navTable}">
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneDocList.NAME%>" pageSizeRelated="true" mainNavTable="vuoto" />
                        </c:when>
                        <c:otherwise>                        
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneDocList.NAME%>" pageSizeRelated="true" mainNavTable="${fn:escapeXml(navTable)}" />
                        </c:otherwise>
                    </c:choose>
                    <slf:list name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneDocList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    <c:choose>
                        <c:when test="${empty navTable}">
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneDocList.NAME%>" mainNavTable="vuoto" />
                        </c:when>
                        <c:otherwise>                        
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.DatiSpecificiMigrazioneDocList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                        </c:otherwise>
                    </c:choose>
                </slf:container>
            </slf:tab>   

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>