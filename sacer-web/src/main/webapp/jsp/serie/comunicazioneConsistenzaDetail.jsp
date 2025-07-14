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
    <sl:head title="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <div class="serieMessageBox "></div>
            <sl:contentTitle title="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= SerieUDForm.ComunicazioneConsistenzaDetail.NAME%>" 
                                   hideDeleteButton="${!(sessionScope['###_FORM_CONTAINER']['comunicazioneConsistenzaDetail']['show_delete'].value) }" 
                                   hideUpdateButton="${!(sessionScope['###_FORM_CONTAINER']['comunicazioneConsistenzaDetail']['show_edit'].value) }"
                                   hideBackButton="${sessionScope['###_FORM_CONTAINER']['consistenzaSerieList'].status eq 'insert'}"
                                   />
            <%--<c:out value="${(sessionScope['###_FORM_CONTAINER']['ComunicazioneConsistenzaDetail']['show_edit'].value) }"/>--%>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=SerieUDForm.InfoSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.ID_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.ID_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.ID_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.AA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DS_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.NM_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.CD_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.TI_STATO_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.TI_STATO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=SerieUDForm.ConsistenzaSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.NM_USERID_CONSIST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DT_COMUNIC_CONSIST_VER_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.TI_MOD_CONSIST_FIRST_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.NI_UNITA_DOC_ATTESE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:section name="<%=SerieUDForm.PrimaUnitaDocAttesaSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.CD_REGISTRO_FIRST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.AA_UNITA_DOC_FIRST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.CD_UNITA_DOC_FIRST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </slf:section>
                    <slf:section name="<%=SerieUDForm.UltimaUnitaDocAttesaSection.NAME%>" styleClass="importantContainer w100">
                        <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.CD_REGISTRO_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.AA_UNITA_DOC_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.CD_UNITA_DOC_LAST%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.ID_LACUNA_NON_PRODOTTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DL_LACUNA_NON_PRODOTTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DL_NOTA_LACUNA_NON_PRODOTTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.ID_LACUNA_MANCANTI%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DL_LACUNA_MANCANTI%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.ComunicazioneConsistenzaDetail.DL_NOTA_LACUNA_MANCANTI%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
