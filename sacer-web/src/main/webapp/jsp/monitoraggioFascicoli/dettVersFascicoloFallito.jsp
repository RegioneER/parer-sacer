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

<%@page import="it.eng.parer.slite.gen.form.MonitoraggioFascicoliForm" pageEncoding="UTF-8"%>
<%@include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio versamento fascicolo fallito" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO VERSAMENTO FASCICOLO FALLITO"/>
            <slf:listNavBarDetail name="<%=MonitoraggioFascicoliForm.VersamentiFascicoliKoList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.NM_AMBIENTE_ENTE_STRUTTURA%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.NM_UTENTE%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.ID_SES_FASCICOLO_KO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.TS_INI_SES%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.TS_FINE_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.AA_FASCICOLO%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.CD_KEY_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.NM_TIPO_FASCICOLO%>" colSpan="3" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.CD_ERR_PRINC%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.DS_ERR_PRINC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.TI_STATO_SES%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.SCARICA_XML_VERS_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true" />
            <!--  piazzo i tabs -->
            <slf:tab  name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKoTabs.NAME%>" tabElement="IndiceSip">
                <slf:fieldSet borderHidden="false">
                    <slf:field name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.BL_XML_SIP%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKoTabs.NAME%>" tabElement="RapportoVersamento">
                <slf:fieldSet borderHidden="false">
                    <slf:field name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKo.BL_XML_RAPP_VERS%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%=MonitoraggioFascicoliForm.DettaglioVersamentoKoTabs.NAME%>" tabElement="ListaErrori">
                <slf:fieldSet borderHidden="false">
                    <slf:listNavBar name="<%=MonitoraggioFascicoliForm.ErroriVersList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=MonitoraggioFascicoliForm.ErroriVersList.NAME%>" />
                    <slf:listNavBar  name="<%=MonitoraggioFascicoliForm.ErroriVersList.NAME%>" />
                </slf:fieldSet>
            </slf:tab>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
