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

<%@ page import="it.eng.parer.slite.gen.form.AnnulVersForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AnnulVersForm.RichAnnulVersDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <sl:contentTitle title="<%=AnnulVersForm.RichAnnulVersDetail.DESCRIPTION%>"/>
            <slf:listNavBarDetail name="<%= AnnulVersForm.RichAnnulVersList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=AnnulVersForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.ID_RICH_ANNUL_VERS%>" width="w10" />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.ID_STRUT%>" width="w10" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.CD_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.DS_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NT_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.DT_CREAZIONE_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <c:choose>
                        <c:when test="${(sessionScope['###_FORM_CONTAINER']['richAnnulVersList'].status eq 'view') }">
                            <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.FL_FORZA_ANNUL%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                        </c:when>
                        <c:otherwise>
                            <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.FL_FORZA_ANNUL_COMBO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                        </c:otherwise>
                    </c:choose>
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.FL_IMMEDIATA%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.FL_RICH_PING%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NI_ITEM%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NI_ITEM_PING%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NI_ITEM_NON_ANNUL%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.TI_CREAZIONE_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=AnnulVersForm.StatoCorrenteSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.TI_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.DT_REG_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.DS_NOTA_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_USERID_STATO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['richAnnulVersList'].status eq 'view') }">
                <slf:fieldSet borderHidden="true">
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.BL_FILE%>" width="w50" labelWidth="w30"/>
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetailButtonList.UPLOAD_FILE%>" />
                </slf:fieldSet>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetailButtonList.CONTROLLA_RICHIESTA%>" width="w10"/>
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetailButtonList.RIFIUTA_RICHIESTA%>" width="w10" />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetailButtonList.CHIUDI_RICHIESTA%>" width="w10"/>
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
                <slf:tab  name="<%= AnnulVersForm.RichAnnulVersDetailSubTabs.NAME%>" tabElement="<%= AnnulVersForm.RichAnnulVersDetailSubTabs.lista_item%>">
                    <slf:listNavBar name="<%= AnnulVersForm.ItemList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= AnnulVersForm.ItemList.NAME%>" />
                    <slf:listNavBar  name="<%= AnnulVersForm.ItemList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%= AnnulVersForm.RichAnnulVersDetailSubTabs.NAME%>" tabElement="<%= AnnulVersForm.RichAnnulVersDetailSubTabs.lista_item_cancellati%>">
                    <slf:listNavBar name="<%= AnnulVersForm.ItemCancellatiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= AnnulVersForm.ItemCancellatiList.NAME%>" />
                    <slf:listNavBar  name="<%= AnnulVersForm.ItemCancellatiList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%= AnnulVersForm.RichAnnulVersDetailSubTabs.NAME%>" tabElement="<%= AnnulVersForm.RichAnnulVersDetailSubTabs.lista_stati%>">
                    <slf:listNavBar name="<%= AnnulVersForm.StatiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= AnnulVersForm.StatiList.NAME%>" />
                    <slf:listNavBar  name="<%= AnnulVersForm.StatiList.NAME%>" />
                </slf:tab>                
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
