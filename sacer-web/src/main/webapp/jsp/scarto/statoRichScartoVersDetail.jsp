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
    <sl:head title="<%=ScartoForm.StatoRichScartoVersDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=ScartoForm.StatoRichScartoVersDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= ScartoForm.StatiList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=ScartoForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.NM_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.NM_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.NM_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.CD_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.DS_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.TI_STATO_RICH_SCARTO_VERS_COR%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=ScartoForm.StatoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.PG_STATO_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.TI_STATO_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.DT_REG_STATO_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.DS_NOTA_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.StatoRichScartoVersDetail.NM_USERID_STATO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
