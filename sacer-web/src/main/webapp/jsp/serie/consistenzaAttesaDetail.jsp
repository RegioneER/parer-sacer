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
    <sl:head title="<%=SerieUDForm.ConsistenzaAttesaDetail.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value="/js/custom/customPollSerie.js" />" ></script>
        <script type="text/javascript">
            $(document).ready(function () {

                $("#Ti_mod_consist_first_last").change(function () {
                    var value = $(this).val() || $(this).text();
                    if (value) {
                        // Se la modalita è CHIAVE_UD, mostro i campi di registro anno numero, altrimenti mostro solo i campi di prima e ultima unità doc
                        if (value === 'CHIAVE_UD') {
                            $("#showUdAttesa").hide();
                            $("fieldset#PrimaUnitaDocAttesaSection").show();
                            $("fieldset#UltimaUnitaDocAttesaSection").show();
                        } else {
                            $("#showUdAttesa").show();
                            $("fieldset#PrimaUnitaDocAttesaSection").hide();
                            $("fieldset#UltimaUnitaDocAttesaSection").hide();
                        }
                    } else {
                        $("#showUdAttesa").show();
                        $("fieldset#PrimaUnitaDocAttesaSection").hide();
                        $("fieldset#UltimaUnitaDocAttesaSection").hide();
                    }
                });


                if ($("select#Ti_mod_consist_first_last").val()) {
                    $("#Ti_mod_consist_first_last").trigger('change');
                } else if ($("div#Ti_mod_consist_first_last").text()) {
                    $("#Ti_mod_consist_first_last").trigger('change');
                } else {
                    $("#showUdAttesa").show();
                    $("fieldset#PrimaUnitaDocAttesaSection").hide();
                    $("fieldset#UltimaUnitaDocAttesaSection").hide();
                }

                poll();
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <div class="serieMessageBox "></div>
            <sl:contentTitle title="<%=SerieUDForm.ConsistenzaAttesaDetail.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= SerieUDForm.ConsistenzaAttesaDetail.NAME%>" 
                                   hideDeleteButton="${!(sessionScope['###_FORM_CONTAINER']['datiSerieConsistenzaAttesaDetail']['show_delete'].value) }" 
                                   hideUpdateButton="${!(sessionScope['###_FORM_CONTAINER']['datiSerieConsistenzaAttesaDetail']['show_edit'].value) }"
                                   />
            <%--<c:out value="${(sessionScope['###_FORM_CONTAINER']['datiSerieConsistenzaAttesaDetail']['show_edit'].value) }"/>--%>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.DatiSerieConsistenzaAttesaDetail.TI_STATO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.ConsistenzaSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.NI_UNITA_DOC_ATTESE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.TI_MOD_CONSIST_FIRST_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <div id="showUdAttesa">
                        <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_FIRST_UNITA_DOC_ATTESA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_LAST_UNITA_DOC_ATTESA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </div>
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_DOC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.DS_DOC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.DT_COMUNIC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.NM_USERID_CONSIST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.PrimaUnitaDocAttesaSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_REGISTRO_FIRST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.AA_UNITA_DOC_FIRST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_UNITA_DOC_FIRST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.UltimaUnitaDocAttesaSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_REGISTRO_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.AA_UNITA_DOC_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ConsistenzaAttesaDetail.CD_UNITA_DOC_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=SerieUDForm.ConsistenzaButtonList.CONTROLLA_CONTENUTO_CALC_CONSIST_ATTESA%>" width="w20"/>
                <slf:lblField name="<%=SerieUDForm.ConsistenzaButtonList.CONTROLLA_CONTENUTO_ACQ_CONSIST_ATTESA%>" width="w20"/>
                <slf:lblField name="<%=SerieUDForm.ConsistenzaButtonList.CONTROLLA_CONTENUTO_EFF_CONSIST_ATTESA%>" width="w20"/>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['consistenzaAttesaDetail'].status eq 'view') }">
                <div class="livello1"><b style="color: #d3101c;">Lista lacune</b></div>
                <slf:listNavBar name="<%= SerieUDForm.LacuneList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.LacuneList.NAME%>" />
                <slf:listNavBar  name="<%= SerieUDForm.LacuneList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
