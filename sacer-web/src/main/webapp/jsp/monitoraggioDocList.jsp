<%--
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
 --%>

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista documenti" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA DOCUMENTI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.RiepilogoVersamenti.NAME%>" hideOperationButton="true" hideBackButton="${sessionScope.hideBackButton == true}"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.ID_STRUT%>" colSpan="2" />
                <sl:newLine />

                <slf:section name="<%=MonitoraggioForm.ChiaveSection.NAME%>" styleClass="importantContainer">

                    <slf:lblField colSpan="2" name="<%=MonitoraggioForm.FiltriDocumenti.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriDocumenti.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=MonitoraggioForm.FiltriDocumenti.CD_KEY_UNITA_DOC%>"  />
                <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                </slf:section> 


                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.ID_TIPO_UNITA_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.PERIODO_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.GIORNO_VERS_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriDocumenti.ORE_VERS_DA%>" name2="<%=MonitoraggioForm.FiltriDocumenti.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.GIORNO_VERS_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriDocumenti.ORE_VERS_A%>" name2="<%=MonitoraggioForm.FiltriDocumenti.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.FL_DOC_PRINCIPALE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.ID_TIPO_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.TI_STATO_DOC_ELENCO_VERS%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriDocumenti.RICERCA_DOC%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.DocumentiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioForm.DocumentiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.DocumentiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
