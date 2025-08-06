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

<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.CambioStatoSerie.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {

                $('.erroriContenutoBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            var nota = $("#Ds_nota_azione").val();
                            window.location = "SerieUD.html?operation=confermaCambioStato&popup=true&Ds_nota_azione=" + nota;
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <c:if test="${!empty requestScope.erroriContenutoBox}">
                <div class="messages erroriContenutoBox ">
                    <ul>
                        <li class="message warning ">ATTENZIONE: il contenuto effettivo presenta errori. Confermare il cambio stato?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="<%=SerieUDForm.CambioStatoSerie.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= SerieUDForm.ContenutoSerieDetail.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_STATO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.ContenutoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_CONTENUTO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.TI_STATO_CONTENUTO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_ERR_CONTENUTO_FILE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_ERR_CONTENUTO%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.NI_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_FIRST_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.CD_LAST_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ContenutoSerieDetail.FL_JOB_BLOCCATO%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.CambioStatoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.CambioStatoSerie.DS_AZIONE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.CambioStatoSerie.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.CambioStatoSerie.DS_NOTA_AZIONE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=SerieUDForm.CambioStatoSerie.CONFERMA_CAMBIO_STATO%>" width="w20"/>
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
