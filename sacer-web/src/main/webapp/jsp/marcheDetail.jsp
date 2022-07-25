<%@ page import="it.eng.parer.slite.gen.form.ComponentiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>


<sl:html>
    <sl:head title="Dettaglio Marca" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO MARCA"/>

            <!--  rimpiazzo la barra di scorrimento record -->
            <slf:listNavBarDetail name="<%= ComponentiForm.MarcheList.NAME%>" />
            
            <slf:section name="<%=ComponentiForm.VersamentoAnnullatoDocSection.NAME%>" styleClass="importantContainer">
                <h2><b><font color="#d3101c">Il versamento del presente documento è stato annullato</font></b></h2>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_ANNUL_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NT_ANNUL_DOC%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                    </slf:section>
                    <slf:section name="<%=ComponentiForm.VersamentoAnnullatoUDSection.NAME%>" styleClass="importantContainer">
                <h2><b><font color="#d3101c">Il versamento della presente unità documentaria è stato annullato</font></b></h2>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.DT_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w70" labelWidth="w30"/>
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NT_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w70" labelWidth="w30"/>
                    </slf:section>

            <slf:tab  name="<%= ComponentiForm.MarcheDettaglioTabs.NAME%>" tabElement="InfoPrincipaliMarche">
                <!--  piazzo i campi da visualizzare nel dettaglio -->
                <slf:fieldSet borderHidden="false">
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
                        <div class="slLabel w50" style="text-align:center;"><b><font color="#d3101c">Profilo componente</font></b></div>
                        <div class="slLabel w50" style="text-align:center;"><b><font color="#d3101c">Informazioni sulle firme</font></b></div>
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
                        <div class="slLabel w50" style="text-align:center;"><b><font color="#d3101c">Informazioni sul file</font></b></div>
                        <div class="slLabel w50" style="text-align:center;"><b><font color="#d3101c">Controlli eseguiti</font></b></div>
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
                        <div class="slLabel w50" style="text-align:center;"><b><font color="#d3101c">Informazioni sul formato</font></b></div>
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
                                <sl:newLine skipLine="true"/>
                                <%--<slf:label name="<%=ComponentiForm.ComponentiDetail.COMP_RIF%>" labelWidth="w20" />--%>
                                <%--<c:if test="${sessionScope.visualizzaLabel}" >
                            <label id="comp_rif" class="slLabel w20" style="color: red">Componente riferito </label>
                        </c:if>
                    <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_REGISTRO_KEY_UNITA_DOC_RIF%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.AA_KEY_UNITA_DOC_RIF%>" colSpan="1" labelWidth="w20" controlWidth="w70" />
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_UNITA_DOC_RIF%>" colSpan="1" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />--%>
                    </slf:section>
                    <slf:section name="<%=ComponentiForm.Marca.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.PG_BUSTA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.PG_MARCA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.TM_MARCA_TEMP%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ISSUER_CERTIF_TSA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.SERIAL_TSA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DT_INI_VAL_CERTIF_TSA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DT_FIN_VAL_CERTIF_TSA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DT_SCAD_MARCA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.TI_FORMATO_MARCA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.TI_MARCA_TEMP%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DS_MARCA_BASE64%>" width="w100" labelWidth="w20" controlWidth="w80" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DS_ALGO_MARCA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.TI_ESITO_CONTR_CONFORME%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DS_MSG_ESITO_CONTR_CONFORME%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.TI_ESITO_VERIF_MARCA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DS_MSG_ESITO_VERIF_MARCA%>" colSpan="2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DS_NOTE%>" width="w100" labelWidth="w20" controlWidth="w60"/>
                        <sl:newLine />
                    </slf:section>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab  name="<%= ComponentiForm.MarcheDettaglioTabs.NAME%>" tabElement="ControlloMarca">
                <slf:fieldSet borderHidden="false">
                <!--  piazzo i campi da visualizzare nel dettaglio -->   
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ESITO_CRITTOGRAFICO%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.MSG_ESITO_CRITTOGRAFICO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ESITO_CATENA%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.MSG_ESITO_CATENA%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ESITO_CERTIFICATO%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.MSG_ESITO_CERTIFICATO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ESITO_CRL%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.MSG_ESITO_CRL%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ISSUER_CRL%>" colSpan="2" />               
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.SERIAL_CA_CRL%>" colSpan="2" />               
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.SERIAL_CRL%>" colSpan="2" />               
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.DT_SCAD_CRL%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ESITO_OCSP%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.MSG_ESITO_OCSP%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.ISSUER_CERTIF_OCSP%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.MarcheUnitaDocumentarieDetail.SERIAL_CERTIF_OCSP%>" colSpan="2" />     
                <sl:newLine skipLine="true"/>
                </slf:fieldSet>
            </slf:tab>   

            <slf:tab  name="<%= ComponentiForm.MarcheDettaglioListsTabs.NAME%>" tabElement="ListaCertificatiCAMarche">
                <slf:listNavBar name="<%= ComponentiForm.CertificatiCAList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.CertificatiCAList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.CertificatiCAList.NAME%>" />
            </slf:tab>  
            
             <slf:tab  name="<%= ComponentiForm.MarcheDettaglioListsTabs.NAME%>" tabElement="ListaCertificatiCAOCSPMarche">
                <slf:listNavBar name="<%= ComponentiForm.CertificatiCAOCSPList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.CertificatiCAOCSPList.NAME%>" />
                <slf:listNavBar  name="<%= ComponentiForm.CertificatiCAOCSPList.NAME%>" />
            </slf:tab>  
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>