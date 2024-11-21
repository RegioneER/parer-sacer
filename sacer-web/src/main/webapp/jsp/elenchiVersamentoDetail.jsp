<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio elenco di versamento" >
        <script type="text/javascript" src="<c:url value='/js/sips/customDeleteElencoMessageBox.js'/>" ></script>
        <script type="text/javascript">
            $(document).ready(function () {
                $('.chiudiElencoBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            var note = $(".chiudiElencoBox #Nt_indice_elenco").val();
                            var navTable = $("input[name='mainNavTable']").val();
                            window.location = "ElenchiVersamento.html?operation=confermaChiusuraElenco&Nt_indice_elenco=" + note + "&mainNavTable=" + navTable;
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
                
                //MEV#32249 - Funzione per riportare indietro lo stato di un elenco per consentire la firma dell'AIP
                var pulsanteRiportaIndietro = $("input[name='operation__riportaStatoIndietroButton']");
                if (pulsanteRiportaIndietro!==null) {
                    pulsanteRiportaIndietro.unbind();
                    pulsanteRiportaIndietro.click(function (event) {
                        var c = confirm("Confermi di rimettere AIP alla firma?");
                        if (c === false) {
                            event.preventDefault();
                            event.stopPropagation();
                            return c;
                        }
                    });
                }                
                
            });
        </script>

    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.chiudiElencoBox}">
                <div class="messages chiudiElencoBox ">
                    <ul>
                        <li class="message info ">
                            <p>Inserisci le note su indice elenco</p>
                        <sl:newLine />
                        <div class="containerLeft w100">
                            <label class="slLabel w30" for="Nt_indice_elenco">Note indice elenco</label>
                            <textarea id="Nt_indice_elenco" class="slText w70" name="Nt_indice_elenco" rows="5" cols="5">${requestScope.nt_indice_elenco}</textarea>                                
                        </div>
                        </li>
                    </ul>
                </div>
            </c:if>

            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO ELENCO DI VERSAMENTO"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" /> 
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />
                </c:when>
            </c:choose>
            <slf:tab  name="<%= ElenchiVersamentoForm.DettaglioElencoTabs.NAME%>" tabElement="DettaglioElencoTab">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <label class="slLabel w50" for="Ds_volume_label" style="text-align:center;"><font color="#d3101c">Informazioni descrittive dell'elenco di versamento</font></label>
                    <label class="slLabel w50" for="Ds_criterio_label" style="text-align:center;"><font color="#d3101c">Informazioni descrittive del criterio di raggruppamento</font></label>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.ID_ELENCO_VERS%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NM_CRITERIO_RAGGR%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.AMB_ENTE_STRUT%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DS_CRITERIO_RAGGR%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NM_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_MAX_COMP%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DS_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_SCAD_CHIUS%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_STATO_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:doubleLblField name="<%=ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_TEMPO_SCAD_CHIUS%>" name2="<%=ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_TEMPO_SCAD_CHIUS%>" labelWidth="w40" controlWidth="w10" controlWidth2="w20" width="w50"/>
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.URN_ORIGINALE%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.URN_NORMALIZZATO%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.URN_INIZIALE%>" labelWidth="w30" controlWidth="70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_VALID_ELENCO%>"  labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_MOD_VALID_ELENCO%>"  labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_CREAZIONE_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_SCAD_CHIUS%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_CHIUS%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DL_MOTIVO_CHIUS%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NUM_COMP%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DIM_BYTES%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.FL_ELENCO_FIRMATO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_FIRMA_INDICE%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NM_COGNOME_NOME_USER %>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.CD_VERSIONE_INDICE%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NT_INDICE_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NT_ELENCO_CHIUSO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=ElenchiVersamentoForm.UdVersateSection.NAME%>" styleClass="importantContainer">
                        <%--<label class="slLabel w50" for="Ds_volume_label" style="text-align:center;"><font color="#d3101c">Unità documentarie versate:</font></label>--%>
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_UNITA_DOC_VERS_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_DOC_VERS_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_COMP_VERS_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_SIZE_VERS_ELENCO%>"  labelWidth="w30" controlWidth="w70" width="w90" />                            
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=ElenchiVersamentoForm.DocAggiuntiSection.NAME%>" styleClass="importantContainer" >
                        <%--<label class="slLabel w50" for="Ds_volume_label" style="text-align:center;"><font color="#d3101c">Documenti aggiunti:</font></label>--%>
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_UNITA_DOC_MOD_ELENCO%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_DOC_AGG_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <!--<div class="slLabel wlbl" >&nbsp;</div>-->
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_COMP_AGG_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_SIZE_AGG_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90" />
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=ElenchiVersamentoForm.InfoElencoIndiciAipSection.NAME%>" styleClass="importantContainer" >
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.FL_ELENCO_STANDARD%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.FL_ELENCO_FISC%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_GEST_ELENCO_AMB%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_FIRMA%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_GEST_ELENCO_CRITERIO%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.TI_GEST_ELENCO%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_CREAZIONE_ELENCO_IX_AIP%>"  labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_FIRMA_ELENCO_IX_AIP%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.DT_MARCA_ELENCO_IX_AIP%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.NI_INDICI_AIP%>" labelWidth="w30" controlWidth="w70" width="w90" />
                        <sl:newLine />
                        <slf:lblField name="<%= ElenchiVersamentoForm.ElenchiVersamentoDetail.CD_VERSIONE_XSD%>" labelWidth="w30" controlWidth="w70" width="w90" />
                    </slf:section>
                </slf:fieldSet>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%= ElenchiVersamentoForm.DettaglioElenchiVersamentoButtonList.CHIUDI_ELENCO_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersamentoForm.DettaglioElenchiVersamentoButtonList.SCARICA_INDICE_ELENCO_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersamentoForm.DettaglioElenchiVersamentoButtonList.GENERA_INDICE_ELENCO_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersamentoForm.DettaglioElenchiVersamentoButtonList.SCARICA_ELENCO_INDICI_AIP_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersamentoForm.DettaglioElenchiVersamentoButtonList.LISTA_OPERAZIONI_ELENCO_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersamentoForm.DettaglioElenchiVersamentoButtonList.RIPORTA_STATO_INDIETRO_BUTTON%>" width="w20"/>
                </sl:pulsantiera>
            </slf:tab>

            <slf:tab  name="<%= ElenchiVersamentoForm.DettaglioElencoTabs.NAME%>" tabElement="FiltriRicercaComponentiTab">
                <slf:fieldSet borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->
                    <slf:section name="<%=ElenchiVersamentoForm.ChiaveSection.NAME%>" styleClass="importantContainer">
                        <slf:fieldSet borderHidden="false">
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" />
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.AA_KEY_UNITA_DOC%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.CD_KEY_UNITA_DOC%>" colSpan="1" />
                            <sl:newLine />
                            <div class="slLabel wlbl" >&nbsp;</div>
                            <div class="containerLeft w2ctr">&nbsp;</div>
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                            <sl:newLine />
                            <div class="slLabel wlbl" >&nbsp;</div>
                            <div class="containerLeft w2ctr">&nbsp;</div>
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                        </slf:fieldSet>
                        <sl:newLine />
                    </slf:section> 
                    <sl:newLine />
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DT_CREAZIONE_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.ORE_DT_CREAZIONE_DA%>" name2="<%=ElenchiVersamentoForm.ComponentiFiltri.MINUTI_DT_CREAZIONE_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DT_CREAZIONE_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.ORE_DT_CREAZIONE_A%>" name2="<%=ElenchiVersamentoForm.ComponentiFiltri.MINUTI_DT_CREAZIONE_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.FL_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.FL_FORZA_CONSERVAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.NM_TIPO_STRUT_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.NM_TIPO_COMP_DOC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_NOME_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.NM_FORMATO_FILE_VERS%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.NM_MIMETYPE_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_HASH_FILE_VERS%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DL_URN_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_URN_COMP_CALC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.NI_SIZE_FILE_DA%>" colSpan="2" />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.NI_SIZE_FILE_A%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_HASH_FILE_CALC%>" colSpan="2" />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_ALGO_HASH_FILE_CALC%>" colSpan="1" />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.CD_ENCODING_HASH_FILE_CALC%>" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.FL_COMP_FIRMATO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.TI_ESITO_CONTR_CONFORME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DT_SCAD_FIRMA_COMP_DA%>" colSpan="2" controlWidth="w20"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DT_SCAD_FIRMA_COMP_A%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.TI_ESITO_VERIF_FIRME_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.TI_ESITO_CONTR_FORMATO_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_FORMATO_RAPPR_CALC%>" colSpan="2"/>
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.DS_FORMATO_RAPPR_ESTESO_CALC%>" colSpan="2"/>
                </slf:fieldSet>
                <sl:newLine />
                <sl:pulsantiera>
                    <!-- piazzo il bottone di ricerca -->
                    <slf:lblField name="<%=ElenchiVersamentoForm.ComponentiFiltri.RICERCA_COMP%>" width="w25" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
            </slf:tab>

            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>
            <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista Stati appartenenti all'elenco di versamento</h2>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati degli stati dell'elenco di versamento -->
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>
            <slf:list name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />

            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.StatiElencoList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>

            <%--<sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= ElenchiVersamentoForm.ListaComponentiButtonList.ELIMINA_APPARTENENZA_UD_DOC_DA_ELENCO%>" width="w33" />
            </sl:pulsantiera>--%>
            
            <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista Componenti appartenenti all'elenco di versamento</h2>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati degli stati dellìelenco di versamento -->
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>
            <slf:list name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />

            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.ComponentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= ElenchiVersamentoForm.ListaComponentiButtonList.ELIMINA_APPARTENENZA_UD_DOC_DA_ELENCO%>" width="w33" />
            </sl:pulsantiera>
            
            <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista Aggiornamenti appartenenti all'elenco di versamento</h2>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati degli aggiornamenti dell'elenco di versamento -->
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>
            <slf:list name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />

            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiVersamentoSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersamentoForm.AggiornamentiList.NAME%>" mainNavTable="<%= ElenchiVersamentoForm.ElenchiIndiciAipSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= ElenchiVersamentoForm.ListaAggiornamentiButtonList.ELIMINA_APPARTENENZA_UPD_DA_ELENCO%>" width="w33" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
