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
    <sl:head  title="Monitoraggio - Lista fascicoli derivanti da versamenti falliti" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA FASCICOLI DERIVANTI DA VERSAMENTI FALLITI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioFascicoliForm.RiepilogoVersamentiFascicoli.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ID_TIPO_FASCICOLO%>" colSpan="2" />
                <sl:newLine />

                <slf:section name="<%=MonitoraggioFascicoliForm.ChiaveFascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.AA_FASCICOLO%>"  />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_KEY_FASCICOLO%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.AA_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.AA_FASCICOLO_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_FASCICOLO_A%>" colSpan="1"/>
                </slf:section> 
                <!-- Range di date -->
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ORE_VERS_DA%>" name2="<%=MonitoraggioFascicoliForm.FiltriFascicoli.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.ORE_VERS_A%>" name2="<%=MonitoraggioFascicoliForm.FiltriFascicoli.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.TI_STATO_SES%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_CLASSE_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.CD_ERR%>" colSpan="4" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioFascicoliForm.FiltriFascicoli.RICERCA_FASCICOLI_KO_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioFascicoliForm.FascicoliKoList.NAME%>" pageSizeRelated="true" />
                <slf:list name="<%= MonitoraggioFascicoliForm.FascicoliKoList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioFascicoliForm.FascicoliKoList.NAME%>" />
            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
