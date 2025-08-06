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

<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.RegoleRapprDetail.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {

                $("input[type='checkbox'][id*='_selezionato_'").change(function () {
                    var key = $(this).parent().siblings().children("input[name*='Nm_campo']").val();
                    var textarea = $('textarea#Dl_formato_out').val();
                    if ($(this).is(':checked')) {
                        if (textarea.indexOf('<' + key + '>') === -1) {
                            $('textarea#Dl_formato_out').val(function (i, val) {
                                return val + ('<' + key + '>');
                            });
                        }
                    } else {
                        if (textarea.indexOf('<' + key + '>') !== -1) {
                            $('textarea#Dl_formato_out').val(textarea.replace('<' + key + '>', ""));
                        }
                    }
                });

            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <slf:messageBox />
        <sl:content>
            <sl:contentTitle title="<%=ModelliSerieForm.RegoleRapprDetail.DESCRIPTION%>"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%= ModelliSerieForm.RegoleRapprDetail.NAME%>" hideBackButton="true" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false"/>
            <slf:messageBox />

            <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            <slf:section name="<%=ModelliSerieForm.InfoModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=ModelliSerieForm.RegoleRapprDetail.ID_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=ModelliSerieForm.RegoleRapprDetail.ID_MODELLO_OUT_SEL_UD%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            </slf:section>

            <slf:lblField name="<%=ModelliSerieForm.RegoleRapprDetail.TI_OUT%>" width="w100" controlWidth="w40" labelWidth="w20" />

            <slf:section name="<%=ModelliSerieForm.DatiProfiloSection.NAME%>" styleClass="importantContainer">
                <%--<slf:listNavBar name="<%= ModelliSerieForm.DatiProfiloList.NAME%>" pageSizeRelated="true"/>--%>
                <slf:editableList name="<%= ModelliSerieForm.DatiProfiloList.NAME%>" multiRowEdit="true" />
                <%--<slf:listNavBar  name="<%= ModelliSerieForm.DatiProfiloList.NAME%>" />--%>
            </slf:section>
            <sl:newLine />
            <slf:section name="<%=ModelliSerieForm.DatiSpecTipoUdSection.NAME%>" styleClass="importantContainer">
                <%--<slf:listNavBar name="<%= ModelliSerieForm.AttributiTipoUnitaDocList.NAME%>" pageSizeRelated="true"/>--%>
                <slf:editableList name="<%= ModelliSerieForm.AttributiTipoUnitaDocList.NAME%>" multiRowEdit="true" />
                <%--<slf:listNavBar  name="<%= ModelliSerieForm.AttributiTipoUnitaDocList.NAME%>" />--%>
            </slf:section>
            <sl:newLine />
            <slf:section name="<%=ModelliSerieForm.DatiSpecTipoDocSection.NAME%>" styleClass="importantContainer">
                <%--<slf:listNavBar name="<%= ModelliSerieForm.AttributiTipoDocList.NAME%>" pageSizeRelated="true"/>--%>
                <slf:editableList name="<%= ModelliSerieForm.AttributiTipoDocList.NAME%>" multiRowEdit="true" />
                <%--<slf:listNavBar  name="<%= ModelliSerieForm.AttributiTipoDocList.NAME%>" />--%>
            </slf:section>

            <slf:lblField name="<%=ModelliSerieForm.RegoleRapprDetail.DL_FORMATO_OUT%>" width="w100" controlWidth="w80" labelWidth="w20"/>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

