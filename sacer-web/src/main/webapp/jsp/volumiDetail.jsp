<%@ page import="it.eng.parer.slite.gen.form.VolumiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio volume" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO VOLUME"/>
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(VolumiForm.VolumiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= VolumiForm.VolumiList.NAME%>" /> 
                </c:when>
            </c:choose>
            <slf:tab  name="<%= VolumiForm.VolumiTabs.NAME%>" tabElement="DettaglioVol">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <label class="slLabel w50" for="Ds_volume_label" style="text-align:center;"><font color="#d3101c">Informazioni sul volume</font></label>
                    <label class="slLabel w50" for="Ds_criterio_label" style="text-align:center;"><font color="#d3101c">Informazioni sul criterio di raggruppamento</font></label>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.ID_VOLUME_CONSERV%>" colSpan="2" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NM_CRITERIO_RAGGR%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NM_VOLUME_CONSERV%>" colSpan="2" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DS_CRITERIO_RAGGR%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DS_VOLUME_CONSERV%>" colSpan="2" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NI_MAX_UNITA_DOC%>"  colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.TI_STATO_VOLUME_CONSERV%>" colSpan="2" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NI_MAX_COMP%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DT_CREAZIONE%>" colSpan="2" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.TI_SCAD_CHIUS_VOLUME%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DT_CHIUS%>" colSpan="2" />
                        <slf:doubleLblField name="<%=VolumiForm.VolumiDetail.NI_TEMPO_SCAD_CHIUS%>" name2="<%=VolumiForm.VolumiDetail.TI_TEMPO_SCAD_CHIUS%>" controlWidth="w10" controlWidth2="w20" colSpan="2"/>
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DL_MOTIVO_CHIUS%>" colSpan="2" />
                        <slf:doubleLblField name="<%=VolumiForm.VolumiDetail.NI_TEMPO_SCAD_CHIUS_FIRME%>" name2="<%=VolumiForm.VolumiDetail.TI_TEMPO_SCAD_CHIUS_FIRME%>" controlWidth="w10" controlWidth2="w20" colSpan="2"/>
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DT_SCAD_CHIUS%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DT_FIRMA_MARCA%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NI_KB_SIZE%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NI_UNITA_DOC_VOLUME%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NI_COMP_VOLUME%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.TI_PRESENZA_FIRME%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.TI_VAL_FIRME%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.CD_VERSIONE_INDICE%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NT_INDICE_VOLUME%>" colSpan="4" />
                        <sl:newLine />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.NT_VOLUME_CHIUSO%>" colSpan="4" />
                    </slf:fieldSet>
                    <sl:newLine skipLine="true"/>
                    <sl:pulsantiera>
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.RICERCA_UD_SEMPLICE%>" width="w30" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.RICERCA_UD_AVANZATA%>" width="w30" />
                    </sl:pulsantiera>
                    <sl:pulsantiera>
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.LISTA_OPERAZIONI_VOLUME%>" width="w30" />
                        <slf:lblField name="<%= VolumiForm.VolumiDetail.DOWNLOAD_PROVE_CONSERVAZIONE%>" width="w30" />
                    </sl:pulsantiera>
                </slf:tab>

            <slf:tab  name="<%= VolumiForm.VolumiTabs.NAME%>" tabElement="FiltriComp">
                <slf:fieldSet borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->
                    <slf:section name="<%=VolumiForm.ChiaveSection.NAME%>" styleClass="importantContainer">
                        <slf:fieldSet borderHidden="false">
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" />
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.AA_KEY_UNITA_DOC%>" colSpan="1"/>
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.CD_KEY_UNITA_DOC%>" colSpan="1" />
                            <sl:newLine />
                            <div class="slLabel wlbl" >&nbsp;</div>
                            <div class="containerLeft w2ctr">&nbsp;</div>
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                            <sl:newLine />
                            <div class="slLabel wlbl" >&nbsp;</div>
                            <div class="containerLeft w2ctr">&nbsp;</div>
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                            <slf:lblField name="<%=VolumiForm.ComponentiFiltri.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                        </slf:fieldSet>
                        <sl:newLine />
                    </slf:section> 
                    <sl:newLine />
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DT_CREAZIONE_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=VolumiForm.ComponentiFiltri.ORE_DT_CREAZIONE_DA%>" name2="<%=VolumiForm.ComponentiFiltri.MINUTI_DT_CREAZIONE_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DT_CREAZIONE_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=VolumiForm.ComponentiFiltri.ORE_DT_CREAZIONE_A%>" name2="<%=VolumiForm.ComponentiFiltri.MINUTI_DT_CREAZIONE_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.FL_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.FL_FORZA_CONSERVAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.NM_TIPO_STRUT_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.NM_TIPO_COMP_DOC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_NOME_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.NM_FORMATO_FILE_VERS%>" colSpan="2"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.NM_MIMETYPE_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_HASH_FILE_VERS%>" colSpan="2"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DL_URN_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_URN_COMP_CALC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.NI_SIZE_FILE_DA%>" colSpan="2" />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.NI_SIZE_FILE_A%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_HASH_FILE_CALC%>" colSpan="2" />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_ALGO_HASH_FILE_CALC%>" colSpan="1" />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.CD_ENCODING_HASH_FILE_CALC%>" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.FL_COMP_FIRMATO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.TI_ESITO_CONTR_CONFORME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DT_SCAD_FIRMA_COMP_DA%>" colSpan="2" controlWidth="w20"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DT_SCAD_FIRMA_COMP_A%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.TI_ESITO_VERIF_FIRME_VERS%>" colSpan="2"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.TI_ESITO_VERIF_FIRME_CHIUS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.TI_ESITO_CONTR_FORMATO_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_FORMATO_RAPPR_CALC%>" colSpan="2"/>
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.DS_FORMATO_RAPPR_ESTESO_CALC%>" colSpan="2"/>
                </slf:fieldSet>
                <sl:newLine />
                <sl:pulsantiera>
                    <!-- piazzo il bottone di ricerca -->
                    <slf:lblField name="<%=VolumiForm.ComponentiFiltri.RICERCA_COMP%>" width="w25" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
            </slf:tab>

            <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>
                <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista Componenti appartenenti al volume</h2>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati dei componenti del volume -->
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(VolumiForm.VolumiList.NAME)%>'>
                    <slf:listNavBar name="<%= VolumiForm.ComponentiList.NAME%>" pageSizeRelated="true" mainNavTable="<%= VolumiForm.VolumiList.NAME%>" />                        
                </c:when>
            </c:choose>
            <slf:list name="<%= VolumiForm.ComponentiList.NAME%>" mainNavTable="${fn:escapeXml(navTable)}" />
            <c:choose>
                <c:when test='<%= request.getAttribute("navTable").equals(VolumiForm.VolumiList.NAME)%>'>
                    <slf:listNavBar name="<%= VolumiForm.ComponentiList.NAME%>" mainNavTable="<%= VolumiForm.VolumiList.NAME%>" />                        
                </c:when>
            </c:choose>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>