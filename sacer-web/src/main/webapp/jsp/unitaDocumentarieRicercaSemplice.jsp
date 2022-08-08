<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Ricerca unità documentarie" >
        <script type="text/javascript" src="<c:url value="/js/sips/customUDMessageBox.js"/>" ></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <!--    Bottoni per custom MessageBox in caso javascript sia disabilitato -->
            <slf:messageBox  />

            <%--<c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Nella lista che hai selezionato sono presenti documenti appartenenti a volumi chiusi. Vuoi che questi ultimi siano esclusi dall'inserimento?</li>
                    </ul>
                </div>
            </c:if>
            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= UnitaDocumentarieForm.VolumeCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>
--%>
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA UNIT&Agrave; DOCUMENTARIE"/>

            <c:if test="${!empty volCorrente && volCreato eq false}">
                <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.NAME%>" hideOperationButton="true" />
            </c:if>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:section name="<%=UnitaDocumentarieForm.UDRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_KEY_UNITA_DOC_CONTIENE%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.NM_TIPO_UNITA_DOC%>"  colSpan="2" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_VERSIONE_XSD_UD%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DL_OGGETTO_UNITA_DOC%>" />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_PROFILO_NORMATIVO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DT_REG_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DT_REG_UNITA_DOC_A%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.NM_TIPO_DOC%>" colSpan="2" controlWidth="w100"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_VERSIONE_XSD_DOC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DL_DOC%>" controlWidth="w100"/>
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DS_AUTORE_DOC%>" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.TI_DOC%>" />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.CD_KEY_DOC_VERS%>" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=UnitaDocumentarieForm.ParamVersRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="4" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.TI_CONSERVAZIONE%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_FORZA_ACCETTAZIONE%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_FORZA_CONSERVAZIONE%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_FORZA_COLLEGAMENTO%>" colSpan="1"/>
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=UnitaDocumentarieForm.ContrConservRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DT_ACQUISIZIONE_UNITA_DOC_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.ORE_DT_ACQUISIZIONE_UNITA_DOC_DA%>" name2="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.MINUTI_DT_ACQUISIZIONE_UNITA_DOC_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.DT_ACQUISIZIONE_UNITA_DOC_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.ORE_DT_ACQUISIZIONE_UNITA_DOC_A%>" name2="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.MINUTI_DT_ACQUISIZIONE_UNITA_DOC_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_DOC_AGGIUNTI%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_DOC_ANNUL%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_UNITA_DOC_ANNUL%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_UNITA_DOC_FIRMATO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.TI_ESITO_VERIF_FIRME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.FL_HASH_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.TI_STATO_CONSERVAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.TI_STATO_UD_ELENCO_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.NM_SUB_STRUT%>" colSpan="2"/>
                </slf:section>
                <sl:newLine />
                <%--<c:if test="${!empty volCorrente}">
                    <slf:section name="<%=UnitaDocumentarieForm.VolumeCorrente.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.VOL_CURR%>" colSpan="1" />
                        <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.STATO_VOL%>" colSpan="1" />
                        <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice.ESCLUDI_UD_VOL_CURR%>" colSpan="1" />
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.VISUALIZZA_VOLUME%>" colSpan="1" />
                    </slf:section>
                </c:if>--%>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!-- piazzo i bottoni ricerca e pulisci -->
            <sl:pulsantiera>
                <div><input type="hidden" value="true" name="back" /></div>
                <div><input type="hidden" value="true" name="simpleSearch" /></div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.RICERCA_UD%>" colSpan="3" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.PULISCI_UD%>" colSpan="3" />
                    <c:if test="${empty volCorrente}">
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.CREA_CRITERIO_RAGGR%>" colSpan="3" />
                    </c:if>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.DOWNLOAD_CONTENUTO%>" colSpan="3" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->                
            <%--<slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" pageSizeRelated="true"/>
<slf:selectList name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" addList="true"/>--%>
            <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" />
            <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" />
            <%--<sl:newLine skipLine="true"/>

            <!-- Inserisco la lista vuota delle unità documentarie da aggiungere a volume -->
            <slf:section name="<%=UnitaDocumentarieForm.UnitaDocumentarieToVolumeSection.NAME%>" styleClass="importantContainer">                
                <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentarieToVolumeList.NAME%>" pageSizeRelated="true"/>
<slf:selectList name="<%= UnitaDocumentarieForm.UnitaDocumentarieToVolumeList.NAME%>"  addList="false"/>
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentarieToVolumeList.NAME%>" />

                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:buttonList name="<%= UnitaDocumentarieForm.AddToVolumeButtonList.NAME%>" />                    
                </sl:pulsantiera>
            </slf:section>--%>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>