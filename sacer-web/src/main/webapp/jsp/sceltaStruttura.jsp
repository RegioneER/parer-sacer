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

<%@page import="it.eng.parer.slite.gen.form.SceltaOrganizzazioneForm" pageEncoding="UTF-8"%>
<%@page import="it.eng.spagoCore.ConfigSingleton"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Scelta struttura" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" showHomeBtn="false" showChangeOrganizationBtn="false" description=""/>
        <div class="toolBar">

            <h2 class="floatLeft">Scelta struttura</h2>
            <div class="right"> <h2><a title="Logout" href="Logout.html">
                        <img title="Logout" alt="Logout" src="<c:url value='/img/base/IconaLogout.png' />" style="padding-right: 5px;">Logout</a></h2></div>
        </div>

        <sl:content> 
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <div class="center">
                <div class="floatLeft" >
                    <c:if test="${empty requestScope.errore}" >
                        <sl:contentTitle title="<%=SceltaOrganizzazioneForm.Strutture.DESCRIPTION%>"/>
                    </c:if>
                </div>
                <sl:newLine skipLine="true"/>
                <slf:fieldSet borderHidden="true" styleClass="">
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%=SceltaOrganizzazioneForm.Strutture.ID_AMBIENTE%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%=SceltaOrganizzazioneForm.Strutture.ID_ENTE%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%=SceltaOrganizzazioneForm.Strutture.ID_STRUT%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField position="left" width="auto" name="<%=SceltaOrganizzazioneForm.Strutture.SELEZIONA_STRUTTURA%>" />
                </slf:fieldSet>
            </div>
        </sl:content>
        <!--Footer-->
        <sl:footer />
    </sl:body>
</sl:html>

