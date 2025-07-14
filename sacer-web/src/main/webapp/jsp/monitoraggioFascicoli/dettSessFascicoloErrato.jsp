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
    <sl:head  title="Monitoraggio - Dettaglio sessione fascicolo errato" >
         <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO SESSIONE FASCICOLO ERRATO"/>
            <slf:listNavBarDetail name="<%= MonitoraggioFascicoliForm.SesFascicoliErrList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.ID_SES_FASCICOLO_ERR%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.TS_INI_SES%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.TS_FINE_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.NM_USERID_WS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.FL_ESISTE_STRUTTURA%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.AA_FASCICOLO%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.CD_KEY_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.ID_TIPO_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.FL_ESISTE_TIPO_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.CD_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.DS_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.TI_STATO_SES%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.SCARICA_XML_VERS_SESS_KO_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true" />

            <!--  piazzo i tabs -->
            <slf:tab  name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKoTabs.NAME%>" tabElement="IndiceSipDettSessFascKo">
                <slf:fieldSet borderHidden="false">
                    <slf:field name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.BL_XML_SIP%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKoTabs.NAME%>" tabElement="RapportoVersamentoDettSessFascKo">
                <slf:fieldSet borderHidden="false">
                    <slf:field name="<%=MonitoraggioFascicoliForm.DettaglioSessFascKo.BL_XML_RAPP_VERS%>" colSpan="4" controlWidth="w100"/>
                </slf:fieldSet>
            </slf:tab>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
