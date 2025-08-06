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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri tipo unit� documentaria" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Parametri tipo unit� documentaria"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri tipo unit� documentaria"/>
            <slf:fieldBarDetailTag name="<%=StrutTipiForm.TipoUnitaDoc.NAME%>" hideBackButton="false" />     
             <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>

            <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer"> 
                <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/> 
                <sl:newLine />
                <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=StrutTipiForm.RicercaParametriTipoUd.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=StrutTipiForm.RicercaParametriTipoUd.RICERCA_PARAMETRI_TIPO_UD_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiForm.ParametriAmministrazioneTipoUdList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiForm.ParametriAmministrazioneTipoUdList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiForm.ParametriConservazioneTipoUdList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiForm.ParametriConservazioneTipoUdList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiForm.ParametriGestioneTipoUdList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiForm.ParametriGestioneTipoUdList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

