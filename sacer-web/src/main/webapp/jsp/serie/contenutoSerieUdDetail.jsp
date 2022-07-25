<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.ContenutoSerieDetail.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value="/js/custom/customPollSerie.js" />" ></script>
        <script type="text/javascript">
            $(document).ready(function () {
                var intervalId = poll();
                
                $("#UdList tbody a").click(function() {
                    clearInterval(intervalId);
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <div class="serieMessageBox "></div>
            <sl:contentTitle title="<%=SerieUDForm.ContenutoSerieDetail.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= SerieUDForm.ContenutoSerieDetail.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>
            <slf:tab  name="<%= SerieUDForm.ContenutoSerieDetailTabs.NAME%>" tabElement="<%= SerieUDForm.ContenutoSerieDetailTabs.dettaglio_contenuto%>">
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DT_INIZIO_SEL_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DT_FINE_SEL_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_STATO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </slf:section>
                    <div id="container1" class="w100">
                        <slf:section name="<%=SerieUDForm.ConsistenzaSection.NAME%>" styleClass="importantContainer containerRight w50">
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NI_UNITA_DOC_ATTESE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_MOD_CONSIST_FIRST_LAST%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_FIRST_UNITA_DOC_ATTESA%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_LAST_UNITA_DOC_ATTESA%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_PRESENZA_LACUNE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_DOC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DS_DOC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NM_USERID_CONSIST%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <sl:newLine skipLine="true"/>
                            <sl:pulsantiera>
                                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.VISUALIZZA_CONSISTENZA_ATTESA%>" />
                            </sl:pulsantiera>
                        </slf:section>
                        <slf:section name="<%=SerieUDForm.ContenutoSection.NAME%>" styleClass="importantContainer containerLeft w50">
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_CONTENUTO_VER_SERIE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_STATO_CONTENUTO_VER_SERIE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_ERR_CONTENUTO_FILE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_ERR_CONTENUTO%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NI_UNITA_DOC%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_FIRST_UNITA_DOC%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DT_FIRST_UNITA_DOC%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_LAST_UNITA_DOC%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DT_LAST_UNITA_DOC%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NI_VOL_VER_SERIE%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_JOB_BLOCCATO%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DL_MSG_JOB_BLOCCATO%>" width="w100" controlWidth="w60" labelWidth="w40"/><sl:newLine />
                        </slf:section>
                    </div>
                    <sl:newLine />
                </slf:fieldSet>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.DOWNLOAD_FILE%>" width="w20"/>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.DOWNLOAD_CONTENUTO%>" width="w20"/>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.DOWNLOAD_QUERY%>" width="w20"/>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.CONTROLLA_CONTENUTO%>" width="w20"/>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.RIAVVIA_CONTROLLO_CONTENUTO%>" width="w20"/>
                </sl:pulsantiera>
            </slf:tab>
            <slf:tab  name="<%= SerieUDForm.ContenutoSerieDetailTabs.NAME%>" tabElement="<%= SerieUDForm.ContenutoSerieDetailTabs.filtri_contenuto%>">
                <slf:section name="<%=SerieUDForm.FiltriContenutoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.CD_UD_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.DT_UD_SERIE_DA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.DT_UD_SERIE_A%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.INFO_UD_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.PG_UD_SERIE_DA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.PG_UD_SERIE_A%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.TI_STATO_CONSERVAZIONE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUDForm.FiltriContenutoSerieDetail.RICERCA_CONTENUTO%>"/>
                </sl:pulsantiera>
            </slf:tab>
            <slf:tab  name="<%= SerieUDForm.ContenutoSerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.ContenutoSerieDetailSubTabs.lista_unita_documentarie%>">
                <slf:listNavBar name="<%= SerieUDForm.UdList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.UdList.NAME%>" />
                <slf:listNavBar  name="<%= SerieUDForm.UdList.NAME%>" />
            </slf:tab>    
            <slf:tab  name="<%= SerieUDForm.ContenutoSerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.ContenutoSerieDetailSubTabs.lista_errori_contenuto%>">
                <slf:listNavBar name="<%= SerieUDForm.ErroriContenutiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.ErroriContenutiList.NAME%>" />
                <slf:listNavBar  name="<%= SerieUDForm.ErroriContenutiList.NAME%>" />
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.DOWNLOAD_ERRORI_CONSISTENZA%>"/>
                </sl:pulsantiera>
            </slf:tab>    
            <slf:tab  name="<%= SerieUDForm.ContenutoSerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.ContenutoSerieDetailSubTabs.lista_errori_file_input%>">
                <slf:listNavBar name="<%= SerieUDForm.ErroriFileInputList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.ErroriFileInputList.NAME%>" />
                <slf:listNavBar  name="<%= SerieUDForm.ErroriFileInputList.NAME%>" />
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUDForm.ContenutoButtonList.DOWNLOAD_ERRORI_FILE_INPUT%>"/>
                </sl:pulsantiera>
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>