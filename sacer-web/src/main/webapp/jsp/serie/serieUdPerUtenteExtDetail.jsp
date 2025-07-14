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

<%@ page import="it.eng.parer.slite.gen.form.SerieUdPerUtentiExtForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="<%=SerieUdPerUtentiExtForm.SerieDetail.DESCRIPTION%>" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUdPerUtentiExtForm.SerieDetail.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:listNavBarDetail name="<%= SerieUdPerUtentiExtForm.SerieList.NAME%>" />
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailTabs.info_principali%>">
                <slf:fieldSet borderHidden="false">
                    <slf:section name="<%=SerieUdPerUtentiExtForm.StrutturaSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=SerieUdPerUtentiExtForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.FL_UPD_ANNUL_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.FL_UPD_MODIF_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_INIZIO_SEL_SERIE%>" width="w40" controlWidth="w20" labelWidth="w50"/>
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_FINE_SEL_SERIE%>" width="w40" controlWidth="w20" labelWidth="w50"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.URN_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.TI_STATO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                    </slf:section>
                    <slf:section name="<%=SerieUdPerUtentiExtForm.ContenutoSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NI_UNITA_DOC_EFF%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_FIRST_UNITA_DOC_EFF%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_FIRST_UNITA_DOC_EFF%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_LAST_UNITA_DOC_EFF%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_LAST_UNITA_DOC_EFF%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NI_MB_SIZE_CONTENUTO_EFF%>" width="w100" controlWidth="w40" labelWidth="w20"/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetailButtonList.CALCOLA_DIMENSIONE_SERIE%>" />
                        </sl:pulsantiera>
                    </slf:section>
                </slf:fieldSet>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetailButtonList.DOWNLOAD_CONTENUTO%>" />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetailButtonList.DOWNLOAD_PACCHETTO_ARK%>" />
                </sl:pulsantiera>
            </slf:tab>
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailTabs.info_versate%>">
                <slf:section name="<%=SerieUdPerUtentiExtForm.ConsistenzaSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NI_UNITA_DOC_ATTESE%>" width="w50" controlWidth="w40" labelWidth="w60"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.TI_MOD_CONSIST_FIRST_LAST%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_FIRST_UNITA_DOC_ATTESA%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_LAST_UNITA_DOC_ATTESA%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_DOC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_DOC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NM_USERID_CONSIST%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine skipLine="true"/>
                    <sl:pulsantiera>
                        <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetailButtonList.VISUALIZZA_CONSISTENZA_ATTESA%>" />
                    </sl:pulsantiera>
                </slf:section>
                <slf:section name="<%=SerieUdPerUtentiExtForm.ContenutoAcquisitoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.TI_STATO_CONTENUTO_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.FL_ERR_CONTENUTO_FILE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.FL_ERR_CONTENUTO_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NI_UNITA_DOC_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_FIRST_UNITA_DOC_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_FIRST_UNITA_DOC_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_LAST_UNITA_DOC_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_LAST_UNITA_DOC_ACQ%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_DOC_FILE_INPUT_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_DOC_FILE_INPUT_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.NM_USERID_FILE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                    <sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.FL_FORNITO_ENTE%>" width="w100" controlWidth="w40" labelWidth="w30"/>
                </slf:section>
            </slf:tab>
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailTabs.indice_aip%>">
                <slf:fieldSet borderHidden="false">
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_VER_XSD_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DT_CREAZIONE_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <!--EVO#16486--> 
                    <%--<slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_URN_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />--%>
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_URN_AIP_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_URN_NORMALIZ_AIP_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <!--end EVO#16486-->
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_HASH_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.DS_ALGO_HASH_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.CD_ENCODING_HASH_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetail.BL_FILE_IX_AIP%>" width="w100" controlWidth="w80" labelWidth="w20" /><sl:newLine />
                    <sl:newLine skipLine="true"/>
                </slf:fieldSet>
                <sl:pulsantiera>
                    <slf:lblField name="<%=SerieUdPerUtentiExtForm.SerieDetailButtonList.DOWNLOAD_AIP%>" />
                </sl:pulsantiera>
            </slf:tab>
            <sl:newLine skipLine="true"/>
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.lista_unita_documentarie%>">
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.UdList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUdPerUtentiExtForm.UdList.NAME%>" />
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.UdList.NAME%>" />
            </slf:tab> 
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.lista_note%>">
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.NoteList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUdPerUtentiExtForm.NoteList.NAME%>" />
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.NoteList.NAME%>" />
            </slf:tab> 
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.lista_volumi%>">
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.VolumiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUdPerUtentiExtForm.VolumiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.VolumiList.NAME%>" />
            </slf:tab> 
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.lista_stati%>">
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.StatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUdPerUtentiExtForm.StatiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.StatiList.NAME%>" />
            </slf:tab> 
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.lista_errori_contenuto%>">
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.ErroriContenutiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUdPerUtentiExtForm.ErroriContenutiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.ErroriContenutiList.NAME%>" />
            </slf:tab> 
            <slf:tab name="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUdPerUtentiExtForm.SerieDetailSubTabs.lista_versioni_precedenti%>">
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.VersioniPrecedentiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUdPerUtentiExtForm.VersioniPrecedentiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.VersioniPrecedentiList.NAME%>" />
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
