<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista unità doc. / documenti non versati" >
        <script src='<c:url value="/js/sips/MonitoraggioDocumentiDerivantiDaVersFallitiRicerca.js"/>' type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <!--    Bottoni per custom MessageBox in caso javascript sia disabilitato -->
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="Lista versamenti annullati unità documentarie"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.NAME%>" hideOperationButton="true"/>

            <slf:tab  name="<%= MonitoraggioForm.FiltriListaDocumentiDerivantiVersFallitiTabs.NAME%>" tabElement="FiltriGeneraliDerivanti">
                <slf:fieldSet  borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.ID_AMBIENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.ID_ENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.ID_STRUT%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.TIPO_LISTA%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.FL_VERIF%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.FL_NON_RISOLUB%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.CLASSE_ERRORE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.SOTTOCLASSE_ERRORE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.CODICE_ERRORE%>" colSpan="4" />
                    <sl:newLine />
                </slf:fieldSet>

                <sl:pulsantiera>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.RICERCA_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.VERIFICA_VERSAMENTI_FALLITI%>" colSpan="2" />

                </sl:pulsantiera>
                <sl:newLine skipLine="true" />
            </slf:tab>

            <slf:tab name="<%=MonitoraggioForm.FiltriListaDocumentiDerivantiVersFallitiTabs.NAME%>" tabElement="FiltriUlterioriDerivanti">
                <slf:fieldSet  borderHidden="false">
                    <sl:newLine />
                    <sl:newLine skipLine="true" />
                    <slf:section name="<%=MonitoraggioForm.FiltriVersamentiChiaveSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.CD_REGISTRO_KEY_UNITA_DOC_ULT%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.AA_KEY_UNITA_DOC_ULT%>" colSpan="1" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.CD_KEY_UNITA_DOC_ULT%>" colSpan="1" />
                        <sl:newLine />
                        <div class="slLabel wlbl" >&nbsp;</div>
                        <div class="containerLeft w2ctr">&nbsp;</div>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.AA_KEY_UNITA_DOC_DA_ULT%>" colSpan="1"/>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.AA_KEY_UNITA_DOC_A_ULT%>" colSpan="1"/>
                        <sl:newLine />
                        <div class="slLabel wlbl" >&nbsp;</div>
                        <div class="containerLeft w2ctr">&nbsp;</div>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.CD_KEY_UNITA_DOC_DA_ULT%>" colSpan="1"/>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori.CD_KEY_UNITA_DOC_A_ULT%>" colSpan="1"/>
                    </slf:section>

                </slf:fieldSet>
            </slf:tab>

            <sl:newLine skipLine="true" />
            <sl:newLine skipLine="true"/>
            <c:choose>
                <c:when test="${sessionScope.filtriListaVersFallitiDistintiDoc.tipoLista == 'UNITA_DOC'}" >
                    <h2 class="titleFiltri">Lista unità documentarie derivanti da versamenti falliti</h2>
                </c:when>
                <c:otherwise>
                    <h2 class="titleFiltri">Lista documenti derivanti da versamenti falliti</h2>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.DocumentiDerivantiDaVersFallitiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.DocumentiDerivantiDaVersFallitiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.DocumentiDerivantiDaVersFallitiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>