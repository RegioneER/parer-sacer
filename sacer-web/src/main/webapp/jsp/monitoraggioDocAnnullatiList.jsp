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

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista versamenti documenti annullati" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA VERSAMENTI DOCUMENTI ANNULLATI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.RiepilogoVersamenti.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ID_STRUT%>" colSpan="2" />
                <sl:newLine />

                <slf:section name="<%=MonitoraggioForm.ChiaveSection.NAME%>" styleClass="importantContainer">

                    <slf:lblField colSpan="2" name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                </slf:section> 


                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.TI_VERS_ANNUL%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ORE_VERS_DA%>" name2="<%=MonitoraggioForm.FiltriDocumentiAnnullati.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ORE_VERS_A%>" name2="<%=MonitoraggioForm.FiltriDocumentiAnnullati.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.TI_STATO_ANNUL%>" colSpan="2" />
                <sl:newLine />

                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumentiAnnullati.RICERCA_DOC_ANNUL%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.DocumentiAnnullatiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.DocumentiAnnullatiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.DocumentiAnnullatiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
