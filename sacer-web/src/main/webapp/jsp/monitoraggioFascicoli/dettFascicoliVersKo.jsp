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
    <sl:head  title="Monitoraggio - Dettaglio fascicolo derivante da versamenti falliti" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO FASCICOLO DERIVANTE DA VERSAMENTI FALLITI"/>
            <slf:listNavBarDetail name="<%=MonitoraggioFascicoliForm.FascicoliKoList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.NM_AMBIENTE_ENTE_STRUTTURA%>" colSpan="3" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.AA_FASCICOLO%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.CD_KEY_FASCICOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.NM_TIPO_FASCICOLO%>" colSpan="3" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.ID_SES_KO_FIRST%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.TS_INI_FIRST_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.ID_SES_KO_LAST%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.TS_INI_LAST_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.CD_ERR_PRINC%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.DS_ERR_PRINC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.DettaglioFascVersKo.TI_STATO_FASCICOLO_KO%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioFascicoliForm.VersamentiFascicoliKoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioFascicoliForm.VersamentiFascicoliKoList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioFascicoliForm.VersamentiFascicoliKoList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
