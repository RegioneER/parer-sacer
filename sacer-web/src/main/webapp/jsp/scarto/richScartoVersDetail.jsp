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
    <sl:head title="<%=ScartoForm.RichScartoVersDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <sl:contentTitle title="<%=ScartoForm.RichScartoVersDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= ScartoForm.RichScartoVersList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=ScartoForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.ID_RICH_SCARTO_VERS%>" width="w10" />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.ID_STRUT%>" width="w10" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NM_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NM_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NM_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.CD_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.DS_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NT_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.DT_CREAZIONE_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NI_ITEM%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NI_ITEM_NON_SCARTATI%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=ScartoForm.StatoCorrenteSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.TI_STATO_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.DT_REG_STATO_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.DS_NOTA_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.NM_USERID_STATO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['richScartoVersList'].status eq 'view') }">
                <slf:fieldSet borderHidden="true">
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetail.BL_FILE%>" width="w50" labelWidth="w30"/>
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetailButtonList.UPLOAD_FILE%>" />
                </slf:fieldSet>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <%--<slf:lblField name="<%=ScartoForm.RichScartoVersDetailButtonList.CONTROLLA_RICHIESTA%>" width="w10"/>
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetailButtonList.RIFIUTA_RICHIESTA%>" width="w10" />--%>
                    <slf:lblField name="<%=ScartoForm.RichScartoVersDetailButtonList.CHIUDI_RICHIESTA%>" width="w10"/>
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
                <slf:tab  name="<%= ScartoForm.RichScartoVersDetailSubTabs.NAME%>" tabElement="<%= ScartoForm.RichScartoVersDetailSubTabs.lista_item%>">
                    <slf:listNavBar name="<%= ScartoForm.ItemList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ScartoForm.ItemList.NAME%>" />
                    <slf:listNavBar  name="<%= ScartoForm.ItemList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%= ScartoForm.RichScartoVersDetailSubTabs.NAME%>" tabElement="<%= ScartoForm.RichScartoVersDetailSubTabs.lista_item_cancellati%>">
                    <slf:listNavBar name="<%= ScartoForm.ItemCancellatiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ScartoForm.ItemCancellatiList.NAME%>" />
                    <slf:listNavBar  name="<%= ScartoForm.ItemCancellatiList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%= ScartoForm.RichScartoVersDetailSubTabs.NAME%>" tabElement="<%= ScartoForm.RichScartoVersDetailSubTabs.lista_stati%>">
                    <slf:listNavBar name="<%= ScartoForm.StatiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= ScartoForm.StatiList.NAME%>" />
                    <slf:listNavBar  name="<%= ScartoForm.StatiList.NAME%>" />
                </slf:tab>                
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
