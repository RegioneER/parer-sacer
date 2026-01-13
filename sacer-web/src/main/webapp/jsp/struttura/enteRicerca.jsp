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

<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Gestione Enti" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Enti" />
        <sl:menu />

        <sl:content>
            
             <sl:contentTitle title="Gestione Enti"/>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.VisEnte.NM_ENTE%>" colSpan="4" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=AmbienteForm.VisEnte.ID_AMBIENTE%>" colSpan="4" />
                <sl:newLine />   
                <slf:lblField name="<%=AmbienteForm.VisEnte.TIPO_DEF_TEMPLATE_ENTE%>" colSpan="4" />  
                <sl:newLine /> 
               
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <!-- piazzo il bottone di ricerca e pulisci -->
                <slf:lblField  name="<%=AmbienteForm.VisEnte.VIS_ENTE_BUTTON%>"  width="w50"/>
            </sl:pulsantiera>
                
            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= AmbienteForm.EntiList.NAME %>" pageSizeRelated="true"/>
<slf:list name="<%= AmbienteForm.EntiList.NAME %>" />
            <slf:listNavBar  name="<%= AmbienteForm.EntiList.NAME %>" />
                    
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
