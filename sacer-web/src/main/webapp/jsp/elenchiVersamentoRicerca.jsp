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

<%@ page import="it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="<%=ElenchiVersamentoForm.ElenchiVersamentoList.DESCRIPTION%>"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="Ricerca elenchi di versamento unità documentarie"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.ID_ELENCO_VERS%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.NM_ELENCO%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DS_ELENCO%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.TI_STATO_ELENCO%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.HH_STATO_ELENCO_IN_CODA_JMS%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.TI_VALID_ELENCO%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.TI_MOD_VALID_ELENCO%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.TI_GEST_ELENCO%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_CREAZIONE_ELENCO_DA%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_CREAZIONE_ELENCO_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_VALIDAZIONE_ELENCO_DA%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_VALIDAZIONE_ELENCO_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.FL_ELENCO_STANDARD%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.FL_ELENCO_FISC%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.FL_ELENCO_FIRMATO%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.FL_ELENCO_INDICI_AIP_CREATO%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_CREAZIONE_ELENCO_IX_AIP_DA%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_CREAZIONE_ELENCO_IX_AIP_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_FIRMA_ELENCO_IX_AIP_DA%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.DT_FIRMA_ELENCO_IX_AIP_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.NT_ELENCO_CHIUSO%>" colSpan="2"/>
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.NT_INDICE_ELENCO%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.TI_STATO_CONSERVAZIONE%>" colSpan="2"/> 
                <sl:newLine />
                <slf:section name="<%=ElenchiVersamentoForm.ChiaveSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.AA_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.AA_KEY_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.AA_KEY_UNITA_DOC_A%>" />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.CD_KEY_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="1" name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.CD_KEY_UNITA_DOC_A%>" />
                </slf:section>
                <sl:newLine />
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.NM_CRITERIO_RAGGR%>" colSpan="2"/>
                <sl:newLine />
            </slf:fieldSet>

            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.RICERCA_ELENCHI_BUTTON%>" width="w25" />                
                <slf:lblField name="<%=ElenchiVersamentoForm.FiltriElenchiVersamento.RIPORTA_STATO_INDIETRO_DA_RICERCA_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
