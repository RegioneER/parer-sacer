<!--
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
-->

<%@ page import="it.eng.parer.slite.gen.form.ComponentiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>


<sl:html>
    <sl:head title="Dettaglio Firma" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO FIRMA"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= ComponentiForm.FirmeList.NAME%>" />

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

            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioTabs.NAME%>" tabElement="InfoPrincipaliFirme">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
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
                        <!--<div class="containerLeft w70"></div>-->
                        <%--<slf:lblField name="<%=ComponentiForm.ComponentiDetail.DETTAGLIO_UD%>"  width="w20"/>--%>
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
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_DOC_VERS%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_CREAZIONE_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_STRUT_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <%--<sl:newLine skipLine="true"/>--%>
                        <!--<div class="containerLeft w70"></div>-->
                        <%--<slf:lblField name="<%=ComponentiForm.ComponentiDetail.DETTAGLIO_DOC%>"  width="w20"/>--%>
                    </slf:section>
                    <slf:section name="<%=ComponentiForm.ProfiloComponente.NAME%>" styleClass="importantContainer">
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Profilo componente</b></div>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sulle firme</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_URN_COMP_CALC%>" width="w100" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NI_ORD_COMP_DOC%>" colSpan="2" controlWidth="w60"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_COMP_FIRMATO%>"  width="w50" labelWidth="w60" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_NOME_COMP_VERS%>"  colSpan="2" controlWidth="w60"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_RIF_TEMP_DATA_FIRMA_VERS%>" width="w50" labelWidth="w60" controlWidth="w20"  />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_COMP_DOC%>" colSpan="2"  controlWidth="w60"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TM_RIF_TEMP_VERS%>" width="w50" labelWidth="w60" controlWidth="w50"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.TI_SUPPORTO_COMP%>" colSpan="2" controlWidth="w60"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_RIF_TEMP_VERS%>" width="w50" labelWidth="w60" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_TIPO_RAPPR_COMP%>" colSpan="2" />
                        <sl:newLine skipLine="true"/>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sul file</b></div>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Controlli eseguiti</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NI_SIZE_FILE_CALC%>" colSpan="2" controlWidth="w70"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_NO_CALC_HASH_FILE%>"  width="w50" labelWidth="w60" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_HASH_FILE_CALC%>"  colSpan="2" controlWidth="w70"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.FL_NO_CALC_FMT_VERIF_FIRME%>" width="w50" labelWidth="w60" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_ALGO_HASH_FILE_CALC%>"  colSpan="2" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_ENCODING_HASH_FILE_CALC%>" colSpan="2" controlWidth="w70"/>
                        <sl:newLine skipLine="true"/>
                        <div class="slLabel w50" style="text-align:center;"><b style="color: #d3101c;">Informazioni sul formato</b></div>
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_FORMATO_RAPPR_CALC%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_FORMATO_RAPPR_FILE_DOC%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DS_FORMATO_RAPPR_ESTESO_CALC%>" width="w50" labelWidth="w50" controlWidth="w50"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_FORMATO_CALC%>"  width="w50" labelWidth="w50" controlWidth="w50" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_MIMETYPE_FILE%>" width="w50" labelWidth="w50" controlWidth="w50"/>
                        <sl:newLine skipLine="true"/>
                        <%--<slf:label name="<%=ComponentiForm.ComponentiDetail.COMP_RIF%>" labelWidth="w20" />--%>
                        <%--<c:if test="${sessionScope.visualizzaLabel}" >
                            <label id="comp_rif" class="slLabel w20" style="color: red">Componente riferito </label>
                        </c:if>
                        <sl:newLine />
                        <%--<slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_REGISTRO_KEY_UNITA_DOC_RIF%>" colSpan="2" labelWidth="w20" controlWidth="w70" />--%>
                        <%--<slf:lblField name="<%=ComponentiForm.ComponentiDetail.AA_KEY_UNITA_DOC_RIF%>" colSpan="1" labelWidth="w20" controlWidth="w70" />--%>
                        <%--<slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_UNITA_DOC_RIF%>" colSpan="1" labelWidth="w20" controlWidth="w70" />--%>
                        <sl:newLine />
                    </slf:section>
                    <%--<sl:newLine skipLine="true"/>--%>
                    <slf:section name="<%=ComponentiForm.Firma.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.PG_BUSTA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>    
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.PG_FIRMA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:doubleLblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.NM_COGNOME_FIRMATARIO%>" name2="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.NM_FIRMATARIO%>" colSpan="2" labelWidth="w20" controlWidth="w20" controlWidth2="w20" />                    
                        <slf:field name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.CD_FIRMATARIO%>" controlWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DT_FIRMA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:doubleLblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.NM_COGNOME_FIRMATARIO_PADRE%>" name2="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.NM_FIRMATARIO_PADRE%>" colSpan="2" labelWidth="w20" controlWidth="w20" controlWidth2="w20" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TI_FORMATO_FIRMA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TI_FIRMA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.BL_FIRMA_BASE64%>" width="w100" labelWidth="w20" controlWidth="w60"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DS_ALGO_FIRMA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TI_ESITO_CONTR_CONFORME%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DS_MSG_ESITO_CONTR_CONFORME%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TM_RIF_TEMP_USATO%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TI_RIF_TEMP_USATO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TI_ESITO_VERIF_FIRMA%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.TI_ESITO_VERIF_FIRMA_DT_VERS%>" colSpan="2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DS_NOTE%>" width="w100" labelWidth="w20" controlWidth="w60"/>
                        <sl:newLine />
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>
            <%--<sl:newLine skipLine="true"/>--%>
            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioTabs.NAME%>" tabElement="CertificatoFirmatario">
                <slf:fieldSet borderHidden="false">
                    <!--  piazzo i campi da visualizzare nel dettaglio -->   
                    <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ISSUER_CERTIF_FIRMATARIO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DS_SERIAL_CERTIF_CA%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DS_SERIAL_CERTIF_FIRMATARIO%>" colSpan="4" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DT_INI_VAL_CERTIF_FIRMATARIO%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DT_FIN_VAL_CERTIF_FIRMATARIO%>" colSpan="2" />
                </slf:fieldSet>
            </slf:tab>   
            <%--<sl:newLine skipLine="true"/>--%>

            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioTabs.NAME%>" tabElement="ControlliFirma">
                <slf:section name="<%=ComponentiForm.FirmaVersamento.NAME%>" styleClass="importantContainer">
                    <slf:fieldSet borderHidden="false">                
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CRITTOGRAFICO%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CRITTOGRAFICO%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CATENA%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CATENA%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CERTIFICATO%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CERTIFICATO%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CRL%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CRL%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DT_SCAD_CRL%>" colSpan="4" />     
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_OCSP%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_OCSP%>" colSpan="2" />  
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ISSUER_CERTIF_OCSP%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.SERIAL_CERTIF_OCSP%>" colSpan="2" />      
                    </slf:fieldSet>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=ComponentiForm.FirmaDataVersamento.NAME%>" styleClass="importantContainer">
                    <slf:fieldSet borderHidden="false">                
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CRITTOG_DT_VERS%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CRITTOG_DT_VERS%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CATENA_DT_VERS%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CATENA_DT_VERS%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CERTIFICATO_DT_VERS%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CERTIFICATO_DT_VERS%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CRL_DT_VERS%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CRL_DT_VERS%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DT_SCAD_CRL_DT_VERS%>" colSpan="4" />  
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_OCSP_DT_VERS%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_OCSP_DT_VERS%>" colSpan="2" />   
                    </slf:fieldSet>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=ComponentiForm.FirmaChiusuraVolume.NAME%>" styleClass="importantContainer">
                    <slf:fieldSet borderHidden="false">
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CRITTOGRAFICO_VOL%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CRITTOGRAFICO_VOL%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CATENA_VOL%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CATENA_VOL%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CERTIFICATO_VOL%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CERTIFICATO_VOL%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.ESITO_CRL_VOL%>" colSpan="2" />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.MSG_ESITO_CRL_VOL%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.FirmeUnitaDocumentarieDetail.DT_SCAD_CRL_VOL%>" colSpan="4" />               
                    </slf:fieldSet>
                </slf:section>
            </slf:tab>   
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioTabs.NAME%>" tabElement="ControlloFirmaChiusVol">
                <!--  piazzo i campi da visualizzare nel dettaglio -->   

            </slf:tab>   
            <sl:newLine skipLine="true"/>

            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioListsTabs.NAME%>" tabElement="ListaCertificatiCAFirme">
                <slf:listNavBar name="<%= ComponentiForm.CertificatiCAList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.CertificatiCAList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.CertificatiCAList.NAME%>" />
            </slf:tab>  
            
            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioListsTabs.NAME%>" tabElement="ListaCertificatiCAOCSPFirme">
                <slf:listNavBar name="<%= ComponentiForm.CertificatiCAOCSPList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.CertificatiCAOCSPList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.CertificatiCAOCSPList.NAME%>" />
            </slf:tab>  

            <slf:tab  name="<%= ComponentiForm.FirmeDettaglioListsTabs.NAME%>" tabElement="ListaControfirmatari">
                <slf:listNavBar name="<%= ComponentiForm.ControfirmatariList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.ControfirmatariList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.ControfirmatariList.NAME%>" />
            </slf:tab> 

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
