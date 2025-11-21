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

<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Ricerca elenchi di versamento fascicoli"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA ELENCHI DI VERSAMENTO FASCICOLI"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.ID_ELENCO_VERS_FASC%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.TI_STATO%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.TS_CREAZIONE_ELENCO_DA%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.TS_CREAZIONE_ELENCO_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.FL_ELENCO_STANDARD%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.NT_ELENCO_CHIUSO%>" colSpan="2"/>
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.NT_INDICE_ELENCO%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.ID_CRITERIO_RAGGR_FASC%>" colSpan="2"/>
                <sl:newLine />
            <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.DT_FIRMA_ELENCO_IX_AIP_DA%>" colSpan="2" controlWidth="w20" />
            <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.DT_FIRMA_ELENCO_IX_AIP_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:section name="<%=ElenchiVersFascicoliForm.FascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.ID_TIPO_FASCICOLO%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.AA_FASCICOLO%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.AA_FASCICOLO_DA%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.AA_FASCICOLO_A%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.CD_KEY_FASCICOLO%>"  />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.CD_KEY_FASCICOLO_DA%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.CD_KEY_FASCICOLO_A%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.CD_COMPOSITO_VOCE_TITOL%>" />
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>

            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli.RICERCA_ELENCHI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
                
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />
            <slf:listNavBar  name="<%= ElenchiVersFascicoliForm.ElenchiVersFascicoliList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
