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

<%@ page import="it.eng.parer.slite.gen.form.VolumiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>
<sl:html>
    <sl:head title="Ricerca volumi"/>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA VOLUMI"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=VolumiForm.Filtri.ID_VOLUME_CONSERV%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=VolumiForm.Filtri.NM_VOLUME_CONSERV%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=VolumiForm.Filtri.DS_VOLUME_CONSERV%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=VolumiForm.Filtri.TI_STATO_VOLUME_CONSERV%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=VolumiForm.Filtri.DT_CREAZIONE_DA%>" colSpan="2" controlWidth="w20" />
                <slf:lblField name="<%=VolumiForm.Filtri.DT_CREAZIONE_A%>" colSpan="2" controlWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=VolumiForm.Filtri.TI_PRESENZA_FIRME%>" colSpan="2"/>
                <slf:lblField name="<%=VolumiForm.Filtri.TI_VAL_FIRME%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=VolumiForm.Filtri.NT_VOLUME_CHIUSO%>" colSpan="2"/>
                <slf:lblField name="<%=VolumiForm.Filtri.NT_INDICE_VOLUME%>" colSpan="2"/>
                <sl:newLine />	
                <slf:lblField name="<%=VolumiForm.Filtri.NM_CRITERIO_RAGGR%>" colSpan="2"/>
                <slf:lblField name="<%=VolumiForm.Filtri.CREATO_MAN%>" colSpan="1"/>
                <sl:newLine />
                <slf:section name="<%=VolumiForm.ChiaveSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=VolumiForm.Filtri.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=VolumiForm.Filtri.AA_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=VolumiForm.Filtri.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=VolumiForm.Filtri.AA_KEY_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="1" name="<%=VolumiForm.Filtri.AA_KEY_UNITA_DOC_A%>" />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=VolumiForm.Filtri.CD_KEY_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="1" name="<%=VolumiForm.Filtri.CD_KEY_UNITA_DOC_A%>" />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=VolumiForm.Filtri.RICERCA%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= VolumiForm.VolumiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= VolumiForm.VolumiList.NAME%>" />
            <slf:listNavBar  name="<%= VolumiForm.VolumiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
