<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio sessione errata" >
     	<script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO SESSIONE ERRATA"/>
            <slf:listNavBarDetail name="<%= MonitoraggioForm.SessioniErrateList.NAME%>"/>

            <slf:tab name="<%= MonitoraggioForm.SessioniErrateTabs.NAME%>" tabElement="InfoSessione">
                <slf:fieldSet  borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->

                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.STRUTTURA%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.NM_USERID_WS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.NM_USERID%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.ID_SESSIONE_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.TI_SESSIONE_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DT_APERTURA%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DT_CHIUSURA%>" colSpan="4" />
                    <sl:newLine />

                    <slf:section name="<%=MonitoraggioForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.ID_AMBIENTE%>" colSpan="3" />
                        <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.ID_ENTE%>" colSpan="3" />
                        <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.ID_STRUT%>" colSpan="3" />
                    </slf:section>
                    <slf:section name="<%=MonitoraggioForm.ChiaveSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="3" />
                        <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.AA_KEY_UNITA_DOC%>" colSpan="3" />
                        <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_KEY_UNITA_DOC%>" colSpan="3" />
                    </slf:section>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_KEY_DOC_VERS%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.CD_ERR%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.DS_ERR%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.SessioniErrateDetail.FL_SESSIONE_ERR_VERIF%>" colSpan="4" />
                </slf:fieldSet>
                <sl:newLine skipLine="true" />

                <sl:newLine skipLine="true"/>
                <h2 class="titleFiltri">Lista File</h2>
                <sl:newLine skipLine="true"/>

                <!--  piazzo la lista con i risultati -->
                <slf:listNavBar name="<%= MonitoraggioForm.FileList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.FileList.NAME%>" />
                <slf:listNavBar  name="<%= MonitoraggioForm.FileList.NAME%>"/>

                <sl:pulsantiera>
                    <slf:buttonList name="<%= MonitoraggioForm.ScaricaFileXMLButtonList.NAME%>" >
                        <slf:lblField name="<%=MonitoraggioForm.ScaricaFileXMLButtonList.SCARICA_FILE_XMLSESSIONE%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </slf:buttonList>
                </sl:pulsantiera>

            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.SessioniErrateTabs.NAME%>" tabElement="SessioneXMLRich">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=MonitoraggioForm.SessioniErrateDetail.BL_XML_RICH%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.SessioniErrateTabs.NAME%>" tabElement="SessioneXMLIndex">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=MonitoraggioForm.SessioniErrateDetail.BL_XML_INDEX%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <slf:tab name="<%= MonitoraggioForm.SessioniErrateTabs.NAME%>" tabElement="SessioneXMLRisp">
                <slf:fieldSet  borderHidden="false">
                    <slf:field name="<%=MonitoraggioForm.SessioniErrateDetail.BL_XML_RISP%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

            <!-- Risetto il parametro table -->        
            <div>
                <input name="table" type="hidden" value="<%= MonitoraggioForm.SessioniErrateList.NAME%>"/>
            </div>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>