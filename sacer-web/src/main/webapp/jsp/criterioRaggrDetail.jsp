<%@ page import="it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<c:set scope="request" var="back" value="${empty param.back}" />
<c:set scope="request" var="table" value="${!empty param.table}" />
<sl:html>
    <sl:head  title="Criterio di raggruppamento" />
    <script type="text/javascript" src="<c:url value='/js/sips/customNumMaxUDCompMessageBox.js'/>" ></script>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="<%= CriteriRaggruppamentoForm.CriterioRaggrList.DESCRIPTION%>" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />            
            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Attenzione: numero massimo componenti maggiore di <c:out value="${requestScope.customBox}" />. Vuoi proseguire con il salvataggio?</li>
                    </ul>
                </div>
            </c:if>

            <c:if test="${!empty requestScope.customBox2}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">Attenzione: le modifiche richieste classificheranno il criterio come non standard; si desidera procedere?</li>
                    </ul>
                </div>
            </c:if>

            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= CriteriRaggruppamentoForm.CriterioCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:newLine skipLine="true"/>
            <div>
                <c:if test="${!back}">
                    <input name="back" type="hidden" value="${fn:escapeXml(param.back)}" />
                </c:if>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>
            <sl:contentTitle title="DETTAGLIO CRITERIO DI RAGGRUPPAMENTO"/>
            <c:choose>
                <c:when test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrList'].status eq 'insert'}">
                    <slf:fieldBarDetailTag name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NAME%>" hideBackButton="true" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false" />
                </c:when>
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= CriteriRaggruppamentoForm.CriterioRaggrList.NAME%>" />
                </c:otherwise>                
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <c:choose>
                    <c:when test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrList'].status eq 'insert'}">
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.ID_AMBIENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.ID_ENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.ID_STRUT%>" colSpan="2" />
                    </c:when>
                    <c:otherwise>
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_AMBIENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_ENTE%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_STRUT%>" colSpan="2" />
                    </c:otherwise>         
                </c:choose>

                <sl:newLine skipLine="true" />
                <%--<slf:section name="<%= CriteriRaggruppamentoForm.InfoDescCriterio.NAME%>" styleClass="importantContainer">--%>                        
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_CRITERIO_RAGGR%>" colSpan="2" />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.ID_CRITERIO_RAGGR%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DS_CRITERIO_RAGGR%>" colSpan="2"  />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NI_MAX_COMP%>" colSpan="2"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DT_ISTITUZ%>" colSpan="2" controlWidth="w20" />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NI_MAX_ELENCHI_BY_GG%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DT_SOPPRES%>" colSpan="2" controlWidth="w20" />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_SCAD_CHIUS_VOLUME%>" colSpan="2"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.FL_CRITERIO_RAGGR_STANDARD%>" colSpan="2"  />
                    <slf:doubleLblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NI_TEMPO_SCAD_CHIUS%>" name2="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_TEMPO_SCAD_CHIUS%>" colSpan="2" controlWidth2="w30" controlWidth="w10" />
                    <%--<c:if test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrList'].status eq 'view'}">--%>
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.FL_CRITERIO_RAGGR_FISC%>" colSpan="2"  />
                    <%--</c:if>--%>     
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NT_CRITERIO_RAGGR%>" colSpan="2"  />
                <%--</slf:section>--%>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%= CriteriRaggruppamentoForm.GestElenchiVersamento.NAME%>" styleClass="importantContainer">
                    <%--<c:if test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrList'].status eq 'view'}">--%>
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_VALID_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90"  />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_MOD_VALID_ELENCO%>" labelWidth="w30" controlWidth="w70" width="w90"  />
                    <%--</c:if>--%>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%= CriteriRaggruppamentoForm.GestElenchiIndiciAip.NAME%>" styleClass="importantContainer">
                    <c:if test="${sessionScope['###_FORM_CONTAINER']['criterioRaggrList'].status eq 'view'}">
                        <label class="slLabel w50" for="Ds_gest_elenchi_indici_aip_amb" style="text-align:left;"><font color="#d3101c">Tipo gestione elenchi indici AIP nell'ambiente:</font></label>
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_GEST_ELENCO_STD_NOFISC%>" labelWidth="w30" controlWidth="w70" width="w90"  />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_GEST_ELENCO_STD_FISC%>" labelWidth="w30" controlWidth="w70" width="w90"  />
                        <sl:newLine />
                        <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_GEST_ELENCO_NOSTD%>" labelWidth="w30" controlWidth="w70" width="w90"  />                    
                        <sl:newLine />
                    </c:if>
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_GEST_ELENCO_CRITERIO%>" labelWidth="w30" controlWidth="w70" width="w90"  />
                </slf:section>
                <sl:newLine skipLine="true"/>                
                <slf:section name="<%= CriteriRaggruppamentoForm.ChiaveInfoVersate.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="1"  />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.AA_KEY_UNITA_DOC%>" colSpan="1"  />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.CD_KEY_UNITA_DOC%>" colSpan="1"  />
                    <sl:newLine />
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_TIPO_UNITA_DOC%>" colSpan="1" />    
                    <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_TIPO_DOC%>" colSpan="1" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.AA_KEY_UNITA_DOC_DA%>" colSpan="2"/>
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.AA_KEY_UNITA_DOC_A%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.CD_KEY_UNITA_DOC_DA%>" colSpan="2"/>
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.CD_KEY_UNITA_DOC_A%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DL_OGGETTO_UNITA_DOC%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DT_REG_UNITA_DOC_DA%>" colSpan="2"/>
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DT_REG_UNITA_DOC_A%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DL_DOC%>" colSpan="2"/>
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DS_AUTORE_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.DT_CREAZIONE_UNITA_DOC_DA%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.ORE_DT_CREAZIONE_UNITA_DOC_DA%>" name2="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.MINUTI_DT_CREAZIONE_UNITA_DOC_DA%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1"/>
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.DT_CREAZIONE_UNITA_DOC_A%>" controlWidth="w70" colSpan="1"/>
                <slf:doubleLblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.ORE_DT_CREAZIONE_UNITA_DOC_A%>" name2="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.MINUTI_DT_CREAZIONE_UNITA_DOC_A%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1"/>
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_CONSERVAZIONE%>"  />
                <sl:newLine />
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.FL_UNITA_DOC_FIRMATO%>" colSpan="2"/>
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.FL_FORZA_ACCETTAZIONE%>" colSpan="1" />
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.FL_FORZA_CONSERVAZIONE%>" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_ESITO_VERIF_FIRME%>" colSpan="2" />
                <sl:newLine />
                <%--<slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.TI_STATO_CONSERVAZIONE%>" colSpan="2"/>
                <sl:newLine />--%>
                <slf:lblField name="<%=CriteriRaggruppamentoForm.CreaCriterioRaggr.NM_SISTEMA_MIGRAZ%>" colSpan="2"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.DUPLICA_CRIT_BUTTON%>"/>
                <slf:lblField name="<%= CriteriRaggruppamentoForm.CreaCriterioRaggr.LOG_EVENTI_CRITERI_RAGGRUPPAMENTO%>"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
