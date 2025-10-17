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

<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Monitoraggio struttura" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="MONITORAGGIO STRUTTURA"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("MonitoraggioStruttura")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.MonitoraggioStruttura.NAME%>" hideOperationButton="true" />                 
            <% }%>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioForm.MonitoraggioStruttura.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.MonitoraggioStruttura.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.MonitoraggioStruttura.ID_STRUT%>" colSpan="2" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.MonitoraggioStruttura.RICERCA_MONITORAGGIO_STRUTTURA_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            
            <!-- Sezione riepilogo versamenti -->
            <%@ include file="riepilogoVersamenti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- Sezione riepilogo versamenti falliti -->
            <%@ include file="riepilogoVersamentiFalliti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- Sezione riepilogo annullamenti -->
            <%@ include file="riepilogoAnnullamenti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
