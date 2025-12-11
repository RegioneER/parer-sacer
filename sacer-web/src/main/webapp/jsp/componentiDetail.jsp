<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

<%@ page import="it.eng.parer.slite.gen.form.ComponentiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Componente"/>    
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO COMPONENTE"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= ComponentiForm.ComponentiList.NAME%>" />

            <slf:section name="<%=ComponentiForm.VersamentoAnnullatoDocSection.NAME%>" styleClass="importantContainer">
                <h2><b style="color: #d3101c;">Il versamento del presente documento è stato annullato</b></h2>
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_ANNUL_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NT_ANNUL_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
            </slf:section>
            <slf:section name="<%=ComponentiForm.VersamentoAnnullatoUDSection.NAME%>" styleClass="importantContainer">
                <h2><b style="color: #d3101c;">Il versamento della presente unità documentaria è stato annullato</b></h2>
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w70" labelWidth="w30"/>
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NT_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w70" labelWidth="w30"/>
            </slf:section>

            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioTabs.NAME%>" tabElement="InfoPrincipaliComp">
                <slf:fieldSet borderHidden="false">
                    <!--  piazzo i campi da visualizzare nel dettaglio -->   
                    <slf:section name="<%=ComponentiForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:section>
                    <slf:section name="<%=ComponentiForm.UnitaDocSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_REGISTRO_KEY_UNITA_DOC%>"colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />    
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.AA_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />    
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DL_OGGETTO_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_REG_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_CREAZIONE_UD%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine skipLine="true"/>
                        <div class="containerLeft w70"></div>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DETTAGLIO_UD%>"  width="w20"/>
                    </slf:section> 
                    <slf:section name="<%=ComponentiForm.Documento.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DL_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_AUTORE_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.URN_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NI_ORD_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_DOC_VERS%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_CREAZIONE_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_STRUT_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine skipLine="true"/>
                        <div class="containerLeft w70"></div>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DETTAGLIO_DOC%>"  width="w20"/>
                    </slf:section>
                    <slf:section name="<%=ComponentiForm.ProfiloComponente.NAME%>" styleClass="importantContainer">
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Profilo componente</b></div>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sulle firme</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_URN_ORIGINALE%>" colSpan="2" controlWidth="w60" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_COMP_FIRMATO%>"  width="w50" labelWidth="w60" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_URN_NORMALIZZATO%>" colSpan="2" controlWidth="w60" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_RIF_TEMP_DATA_FIRMA_VERS%>" width="w50" labelWidth="w60" controlWidth="w20"  />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_URN_COMP_CALC%>" colSpan="2" controlWidth="w60" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TM_RIF_TEMP_VERS%>" width="w50" labelWidth="w60" controlWidth="w40"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NI_ORD_COMP_DOC%>" colSpan="2" controlWidth="w60"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_RIF_TEMP_VERS%>" width="w50" labelWidth="w60" controlWidth="w40" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_NOME_COMP_VERS%>"  colSpan="2" controlWidth="w60"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_SISTEMA_VERIF_FIRME%>" width="w50" labelWidth="w60" controlWidth="w40" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_COMP_DOC%>" colSpan="2"  controlWidth="w60"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_SUPPORTO_COMP%>" colSpan="2" controlWidth="w60"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_RAPPR_COMP%>" colSpan="2" />
                        <sl:newLine skipLine="true"/>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sul file</b></div>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Controlli eseguiti</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NI_SIZE_FILE_CALC%>" colSpan="2" controlWidth="w70"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_NO_CALC_HASH_FILE%>"  width="w50" labelWidth="w60" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.HASH_CALC_PERSONALIZZATO%>"  colSpan="2" controlWidth="w90"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_NO_CALC_FMT_VERIF_FIRME%>" width="w50" labelWidth="w60" controlWidth="w20"/>                        
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.HASH_PERSONALIZZATO%>" colSpan="4" controlWidth="w90"/>
                        <sl:newLine skipLine="true"/>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sul formato</b></div>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sull'archiviazione</b></div>
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_FORMATO_RAPPR_CALC%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TIPO_ARCHIVIAZIONE%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_FORMATO_RAPPR_FILE_DOC%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TENANT%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_STATO_DT_VERS%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_SUB_PARTITION%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_FORMATO_RAPPR_ESTESO_CALC%>" width="w50" labelWidth="w50" controlWidth="w50"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_BUCKET%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_NOME_FILE_ARK%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.ID_FILE_ORACLE%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_FORMATO_CALC%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_FILE%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_MIMETYPE_FILE%>" width="w50" labelWidth="w50" controlWidth="w50"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TRASFORMATORE%>" width="w50" labelWidth="w50" controlWidth="w50"/>
                        <%--<slf:label name="<%=ComponentiForm.ComponentiDetail.COMP_RIF%>" labelWidth="w20" />--%>
                        <%--<c:if test="${sessionScope.visualizzaLabel}" >
                            <div id="comp_rif" class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Componente riferito</b></div>
                        </c:if>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_REGISTRO_KEY_UNITA_DOC_RIF%>" colSpan="2" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.AA_KEY_UNITA_DOC_RIF%>" colSpan="2" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_UNITA_DOC_RIF%>" colSpan="2" controlWidth="w70" />  --%>
                        <sl:newLine />
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>   

            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioTabs.NAME%>" tabElement="InfoVersateComp">
                <!--  piazzo i campi da visualizzare -->   
                <slf:fieldSet borderHidden="false">
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NI_ORD_COMP_DOC%>"  colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DL_URN_COMP_VERS%>"  colSpan="2" />
                    <sl:newLine />
                    <%--<slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_SUPPORTO_COMP%>"  colSpan="2" />--%>
                    <%--<sl:newLine />--%>
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_NOME_COMP_VERS%>"  colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_FORMATO_VERS%>"  colSpan="2" />
                    <sl:newLine />
                    <%--<slf:lblField name="<%= ComponentiForm.ComponentiDetail.NM_MIMETYPE_FILE%>"  colSpan="2" />--%>
                    <%--<sl:newLine />--%>
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_HASH_FILE_VERS%>"  colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_ID_COMP_VERS%>"  colSpan="2" />
                    <%--<sl:newLine />--%>
                    <%--<slf:lblField name="<%= //ComponentiForm.ComponentiDetail.NI_ORD_COMP_PADRE%>"  colSpan="2" />--%>
                </slf:fieldSet>
            </slf:tab>   

            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioTabs.NAME%>" tabElement="InfoVolumeConservComp">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.ID_VOLUME_CONSERV%>"  width="w100" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_STATO_VOLUME_CONSERV%>"  width="w100" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_CHIUS_VOLUME%>"  width="w100" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_ESITO_VERIF_FIRME_CHIUS%>"  width="w100" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_ESITO_VERIF_FIRME_CHIUS%>"  width="w100" labelWidth="w20" controlWidth="w70" />              
                    <sl:newLine />
                </slf:fieldSet>
            </slf:tab>

            <%--                 <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioTabs.NAME%>" tabElement="InfoElencoVersComp">
          piazzo i campi da visualizzare nel dettaglio 
        <slf:fieldSet borderHidden="false">
            <slf:lblField name="<%=ComponentiForm.ComponentiDetail.ID_ELENCO_VERS%>"  width="w100" labelWidth="w20" controlWidth="w70" />
            <sl:newLine />
            <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_STATO_ELENCO_VERS%>"  width="w100" labelWidth="w20" controlWidth="w70" />
            <sl:newLine />
            <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_CHIUS_ELENCO_VERS%>"  width="w100" labelWidth="w20" controlWidth="w70" />
            <sl:newLine />
            <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_ESITO_VERIF_FIRME_DT_VERS%>"  width="w100" labelWidth="w20" controlWidth="w70" />
            <sl:newLine />
            <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_ESITO_VERIF_FIRME_DT_VERS%>"  width="w100" labelWidth="w20" controlWidth="w70" />              
            <sl:newLine />
        </slf:fieldSet>
    </slf:tab>--%>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.SCARICA_COMP_FILE%>"  width="w20" controlWidth="w100" />
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.SCARICA_DIP_ESIBIZIONE_COMP_DOC%>"  width="w20" controlWidth="w100" />
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.SCARICA_DIP_COMP%>"  width="w20" controlWidth="w100" />
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.SCARICA_REPORT_FIRMA%>"  width="w20" controlWidth="w100" />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioListsTabs.NAME%>" tabElement="ListaFirmeComp">
                <slf:listNavBar name="<%= ComponentiForm.FirmeList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.FirmeList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.FirmeList.NAME%>" />
            </slf:tab>  
            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioListsTabs.NAME%>" tabElement="ListaMarcheComp">
                <slf:listNavBar name="<%= ComponentiForm.MarcheList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.MarcheList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.MarcheList.NAME%>" />
            </slf:tab>  
            <%--            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioListsTabs.NAME%>" tabElement="ListaDatiComp">
                            <slf:listNavBar name="<%= ComponentiForm.DatiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.DatiList.NAME%>" />
                            <slf:listNavBar  name="<%= ComponentiForm.DatiList.NAME%>" />
                        </slf:tab>  --%>
            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiComp">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.VERSIONE_XSD_DATI_SPEC_COMP%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= ComponentiForm.DatiSpecificiCompList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.DatiSpecificiCompList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.DatiSpecificiCompList.NAME%>" />
            </slf:tab>  
            <slf:tab  name="<%= ComponentiForm.ComponentiDettaglioListsTabs.NAME%>" tabElement="ListaDatiSpecificiMigrazioneComp">
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=ComponentiForm.ComponentiDetail.VERSIONE_XSD_DATI_SPEC_MIGR_COMP%>" colSpan="2"/>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= ComponentiForm.DatiSpecificiMigrazioneCompList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.DatiSpecificiMigrazioneCompList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.DatiSpecificiMigrazioneCompList.NAME%>" />
            </slf:tab>  

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
