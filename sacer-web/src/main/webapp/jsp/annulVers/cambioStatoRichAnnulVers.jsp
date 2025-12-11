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
    <sl:head title="<%=AnnulVersForm.CambioStatoRichiesta.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {

                $('.erroriItemBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                            window.location = "AnnulVers.html?operation=elencoOnClick";
                        }
                    }
                });

            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:choose>
                <c:when test="${!empty requestScope.itemNonFattibili}">
                    <div class="messages erroriItemBox ">
                        <ul>
                            <li class="message warning ">ATTENZIONE: la richiesta contiene annullamenti non fattibili. Confermare il cambio stato?</li>
                        </ul>
                    </div>
                </c:when>
                <c:when test="${!empty requestScope.tuttiItemNonFattibili}">
                    <div class="messages erroriItemBox ">
                        <ul>
                            <li class="message warning ">ATTENZIONE: la richiesta contiene solo annullamenti non fattibili. Confermare il cambio stato?</li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise></c:otherwise>
            </c:choose>
            <sl:contentTitle title="<%=AnnulVersForm.CambioStatoRichiesta.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= AnnulVersForm.RichAnnulVersDetail.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=AnnulVersForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.NM_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.CD_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.DS_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.RichAnnulVersDetail.TI_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=AnnulVersForm.CambioStatoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=AnnulVersForm.CambioStatoRichiesta.TI_STATO_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.CambioStatoRichiesta.DS_NOTA_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AnnulVersForm.CambioStatoRichiesta.CONFERMA_CAMBIO_STATO%>" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
