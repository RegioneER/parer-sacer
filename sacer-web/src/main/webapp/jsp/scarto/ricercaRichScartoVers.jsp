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

<%@ page import="it.eng.parer.slite.gen.form.ScartoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ScartoForm.FiltriRicercaRichScartoVers.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:choose>
                <c:when test='<%="FASCICOLI".equals((String) session.getAttribute("tiRichAnnulVers"))%>'>
                    <sl:contentTitle title='<%=ScartoForm.FiltriRicercaRichScartoVers.DESCRIPTION + " fascicoli"%>'/>
                </c:when>
                <c:otherwise>
                    <sl:contentTitle title='<%=ScartoForm.FiltriRicercaRichScartoVers.DESCRIPTION + " unità documentarie"%>'/>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.CD_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.DS_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.NT_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.TI_STATO_RICH_SCARTO_VERS_COR%>" colSpan="2" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.DT_CREAZIONE_RICH_SCARTO_VERS_DA%>" width="w50" controlWidth="w20" labelWidth="w40"/>
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.DT_CREAZIONE_RICH_SCARTO_VERS_A%>" width="w50" controlWidth="w20" labelWidth="w40" /><sl:newLine />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.FL_NON_SCARTABILE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:section name="<%=ScartoForm.UdSection.NAME%>" styleClass="noborder w100">
                    <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.CD_REGISTRO_KEY_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.AA_KEY_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.CD_KEY_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.RICERCA_RICH_SCARTO_VERS%>"  width="w25" />
                <slf:lblField name="<%=ScartoForm.FiltriRicercaRichScartoVers.CREA_RICH_SCARTO_IMMEDIATA%>"  width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:section name="<%=ScartoForm.ListaRichiesteSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= ScartoForm.RichScartoVersList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= ScartoForm.RichScartoVersList.NAME%>" />
                <slf:listNavBar  name="<%= ScartoForm.RichScartoVersList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
