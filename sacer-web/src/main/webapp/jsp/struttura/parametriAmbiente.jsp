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

<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri ambiente" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Parametri ambiente"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri ambiente"/>
            <slf:fieldBarDetailTag name="<%=AmbienteForm.InsAmbiente.NAME%>" hideBackButton="true" />
            <slf:section name="<%=AmbienteForm.NomeAmbienteSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=AmbienteForm.InsAmbiente.NM_AMBIENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AmbienteForm.RicercaParametriAmbiente.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=AmbienteForm.RicercaParametriAmbiente.RICERCA_PARAMETRI_AMBIENTE_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >                
                <slf:section name="<%=AmbienteForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmbienteForm.ParametriAmministrazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriAmministrazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmbienteForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmbienteForm.ParametriConservazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriConservazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmbienteForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmbienteForm.ParametriGestioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriGestioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmbienteForm.ParametriMultipliSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= AmbienteForm.ParametriMultipliAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriMultipliAmbienteList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

