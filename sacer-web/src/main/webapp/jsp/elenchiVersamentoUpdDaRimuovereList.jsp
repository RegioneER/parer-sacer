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
    <sl:head title="<%= ElenchiVersamentoForm.UpdDaRimuovereList.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA AGGIORNAMENTI DA ELIMINARE" />

            <h2>Vuoi rimuovere i seguenti aggiornamenti dall'elenco di versamento ${ fn:escapeXml(requestScope.elenco) }?</h2>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ElenchiVersamentoForm.UpdDaRimuovereList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ElenchiVersamentoForm.UpdDaRimuovereList.NAME%>" />
            <slf:listNavBar  name="<%= ElenchiVersamentoForm.UpdDaRimuovereList.NAME%>" />

            <div><input name="table" type="hidden" value="<%= ElenchiVersamentoForm.ElenchiVersamentoList.NAME%>" /></div>

            <sl:pulsantiera>
                <slf:buttonList name="<%= ElenchiVersamentoForm.ListaUpdDaRimuovereButtonList.NAME%>" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
