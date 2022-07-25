<%@page import="it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm"%>
<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio elenco di versamento fascicoli" >
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
                            window.location = "ElenchiVersFascicoli.html?operation=confermaChiusuraElenco&Nt_indice_elenco=" + note + "&mainNavTable=" + navTable;
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
                
                $('.customBox').dialog({
                    autoOpen : true,
                    width : 600,
                    modal : true,
                    closeOnEscape : true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons : {
                        "Si" : function() {
                            $(this).dialog("close");                
                            window.location = "ElenchiVersFascicoli.html?operation=confermaRimozioneFascButton&table="+$("input[name='table']").attr("value")+"&back="+$("input[name='back']").attr("value");
                        },
                        "No" : function() {
                            $(this).dialog("close");                
                        }
                    }
                });
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
            
            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Attenzione: l'operazione richiesta eliminerà l'appartenenza dei fascicoli selezionati dall'elenco di versamento; si desidera procedere?</li>
                    </ul>
                </div>
            </c:if>

            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= ElenchiVersFascicoliForm.FascDaRimuovereCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO ELENCO DI VERSAMENTO FASCICOLI"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" /> 
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliDaFirmareList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDaFirmareList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliSelezionatiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliSelezionatiList.NAME%>" />
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME%>" />
                </c:when>
                <c:otherwise>
                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['elenchiVersFascicoliList'].status eq 'insert')}">
                        <slf:listNavBarDetail name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />  
                    </c:if>
                </c:otherwise>
            </c:choose>
            <slf:tab  name="<%= ElenchiVersFascicoliForm.DettaglioElencoTabs.NAME%>" tabElement="DettaglioElencoTab">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <label class="slLabel w50" for="Ds_volume_label" style="text-align:center;"><font color="#d3101c">Informazioni descrittive dell'elenco di versamento fascicoli</font></label>
                    <label class="slLabel w50" for="Ds_criterio_label" style="text-align:center;"><font color="#d3101c">Informazioni descrittive del criterio di raggruppamento fascicoli</font></label>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.ID_ELENCO_VERS_FASC%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.NM_CRITERIO_RAGGR%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.DS_URN_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.DS_CRITERIO_RAGGR%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.DS_URN_NORMALIZ_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.NI_MAX_FASC_CRIT%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.AMB_ENTE_STRUT%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.TI_SCAD_CHIUS_CRIT%>" labelWidth="w40" controlWidth="60" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.TI_STATO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <slf:doubleLblField name="<%=ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.NI_TEMPO_SCAD_CHIUS_CRIT%>" name2="<%=ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.TI_TEMPO_SCAD_CHIUS_CRIT%>" labelWidth="w40" controlWidth="w10" controlWidth2="w20" width="w50"/>
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.TS_CREAZIONE_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.DT_SCAD_CHIUS%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.TS_STATO_CHIUSO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.DL_MOTIVO_CHIUS%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.NI_FASC_VERS_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.TS_STATO_FIRMATO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.CD_VER_XSD_FILE%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.NT_INDICE_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.NT_ELENCO_CHIUSO%>" labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine />
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDetail.FL_ELENCO_STANDARD%>"  labelWidth="w30" controlWidth="w70" width="w50" />
                    <sl:newLine skipLine="true"/>
                    <label class="slLabel w50" for="Ds_stati_label" style="text-align:center;"><font color="#d3101c">Stati assunti dall'elenco di versamento fascicoli</font></label>
                    <sl:newLine skipLine="true"/>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.StatiElencoVersFascicoliList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />
                    <slf:list name="<%= ElenchiVersFascicoliForm.StatiElencoVersFascicoliList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.StatiElencoVersFascicoliList.NAME%>" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />
                </slf:fieldSet>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.DettaglioElenchiVersFascicoliButtonList.CHIUDI_ELENCO_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.DettaglioElenchiVersFascicoliButtonList.SCARICA_INDICE_ELENCO_BUTTON%>" width="w20"/>
                    <slf:lblField name="<%= ElenchiVersFascicoliForm.DettaglioElenchiVersFascicoliButtonList.SCARICA_ELENCO_IDX_AIP_FASC_BTN%>" width="w20"/>
                </sl:pulsantiera>
            </slf:tab>

            <slf:tab  name="<%= ElenchiVersFascicoliForm.DettaglioElencoTabs.NAME%>" tabElement="FiltriRicercaFascicoliTab">
                <slf:fieldSet borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->
                    <slf:section name="<%=ElenchiVersFascicoliForm.FascicoloSection.NAME%>" styleClass="importantContainer">
                        <slf:fieldSet borderHidden="true">
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.ID_TIPO_FASCICOLO%>" colSpan="2" />
                            <sl:newLine />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.AA_FASCICOLO%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.AA_FASCICOLO_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.AA_FASCICOLO_A%>" colSpan="1"/>
                            <sl:newLine />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.CD_KEY_FASCICOLO%>" colSpan="1" />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.CD_KEY_FASCICOLO_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.CD_KEY_FASCICOLO_A%>" colSpan="1"/>
                            <sl:newLine />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.TS_INI_SES_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.TS_INI_SES_A%>" colSpan="1"/>
                            <sl:newLine />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.DT_APE_FASCICOLO_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.DT_APE_FASCICOLO_A%>" colSpan="1"/>
                            <sl:newLine />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.DT_CHIU_FASCICOLO_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.DT_CHIU_FASCICOLO_A%>" colSpan="1"/>
                            <sl:newLine />
                            <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.CD_COMPOSITO_VOCE_TITOL%>" colSpan="1"/>
                        </slf:fieldSet>
                        <sl:newLine />
                    </slf:section> 
                </slf:fieldSet>
                <sl:newLine />
                <sl:pulsantiera>
                    <!-- piazzo il bottone di ricerca -->
                    <slf:lblField name="<%=ElenchiVersFascicoliForm.FascicoliFiltri.RICERCA_FASC%>" width="w25" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
            </slf:tab>

            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>
            <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista Fascicoli appartenenti all'elenco di versamento</h2>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati dei fascicoli del volume -->
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>
            <slf:list name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />

            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />                        
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascDaFirmareList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiVersFascicoliSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliSelezionatiList.NAME%>" />                      
                </c:when>
                <c:when test='<%= request.getAttribute("navTable").equals(ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME)%>'>
                    <slf:listNavBar name="<%= ElenchiVersFascicoliForm.FascicoliList.NAME%>" mainNavTable="<%= ElenchiVersFascicoliForm.ElenchiIndiciAipFascSelezionatiList.NAME%>" />                      
                </c:when>
            </c:choose>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= ElenchiVersFascicoliForm.ListaFascicoliButtonList.ELIMINA_APPARTENENZA_FASC_DA_ELENCO%>" width="w33" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
