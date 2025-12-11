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

<%@ page import="it.eng.parer.slite.gen.form.StrutSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Regola di Rappresentazione" >
        <script type="text/javascript" src="<c:url value='/js/sips/customTipologiaSerieVincolataMessageBox.js'/>"></script>
        <script type="text/javascript">
            $(document).ready(function () {

                $('.warningPeriodiValBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutSerie.html?operation=confermaSalvataggioRegolaRappresentazione";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                $("input[type='checkbox'][id*='_selezionato_'").change(function () {
                    var key = $(this).parent().next().next().children("input[name*='Key_campo']").val();
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

        <sl:content>
            <sl:contentTitle title="<%=StrutSerieForm.RegoleRapprDetail.DESCRIPTION%>"/>
            <slf:messageBox />
            <c:if test="${!empty requestScope.warningPeriodiValBox}">
                <div class="messages warningPeriodiValBox ">
                    <ul>
                        <li class="message warning ">ATTENZIONE: Non tutti i periodi di validit&agrave; del registro presentano una parte di tipo NUMERICO. Vuoi continuare? </li>
                    </ul>
                </div>
            </c:if>    

            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">L’operazione comporterà il ricalcolo di almeno una serie. Si intende procedere?</li>
                    </ul>
                </div>
            </c:if>
            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= StrutSerieForm.TipoSerieCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>                

            <sl:newLine skipLine="true"/>
            <slf:wizard name="<%= StrutSerieForm.InserimentoWizard.NAME%>">
                <slf:wizardNavBar name="<%=StrutSerieForm.InserimentoWizard.NAME%>" />
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=StrutSerieForm.RegoleRapprDetail.NM_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegoleRapprDetail.NM_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegoleRapprDetail.NM_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutSerieForm.SerieUniDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                </slf:section>
                <slf:section name="<%=StrutSerieForm.SerieRegistroUniDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutSerieForm.StrutRif.REGISTRO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutSerieForm.StrutRif.TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                    <sl:newLine />
                </slf:section>
                <slf:step name="<%= StrutSerieForm.InserimentoWizard.TIPO_RAPPR%>"> 
                    <slf:section name="<%=StrutSerieForm.SerieRegistroUniDocRegolaRappr.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=StrutSerieForm.RegoleRapprDetail.TI_OUT%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                        <sl:newLine />

                    </slf:section>
                </slf:step>
                <slf:step name="<%= StrutSerieForm.InserimentoWizard.CAMPI_OUT%>"> 
                    <slf:section name="<%=StrutSerieForm.DatiProfiloSection.NAME%>" styleClass="importantContainer">
                        <!-- <div class="livello1"><b><font color="#d3101c"><%=StrutSerieForm.DatiProfiloList.DESCRIPTION%></font></b></div>-->
                        <%--<slf:listNavBar name="<%= StrutSerieForm.DatiProfiloList.NAME%>" pageSizeRelated="true"/>--%>
                        <slf:editableList name="<%= StrutSerieForm.DatiProfiloList.NAME%>" multiRowEdit="true" />
                        <%--<slf:listNavBar  name="<%= StrutSerieForm.DatiProfiloList.NAME%>" />--%>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=StrutSerieForm.DatiSpecTipoUdSection.NAME%>" styleClass="importantContainer">
                    <!--<div class="livello1"><b><font color="#d3101c"><%=StrutSerieForm.AttributiTipoUnitaDocList.DESCRIPTION%></font></b></div>-->
                        <%--<slf:listNavBar name="<%= StrutSerieForm.AttributiTipoUnitaDocList.NAME%>" pageSizeRelated="true"/>--%>
                        <slf:editableList name="<%= StrutSerieForm.AttributiTipoUnitaDocList.NAME%>" multiRowEdit="true" />
                        <%--<slf:listNavBar  name="<%= StrutSerieForm.AttributiTipoUnitaDocList.NAME%>" />--%>
                    </slf:section>
                    <sl:newLine />
                    <slf:section name="<%=StrutSerieForm.DatiSpecTipoDocSection.NAME%>" styleClass="importantContainer">
                        <!--                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['attributiTipoDocList'].table['empty'])}">
                                                <div class="livello1"><b><font color="#d3101c"><%=StrutSerieForm.AttributiTipoDocList.DESCRIPTION%></font></b></div>
                        </c:if>-->
                        <%--<slf:listNavBar name="<%= StrutSerieForm.AttributiTipoDocList.NAME%>" pageSizeRelated="true"/>--%>
                        <slf:editableList name="<%= StrutSerieForm.AttributiTipoDocList.NAME%>" multiRowEdit="true" />
                        <%--<slf:listNavBar  name="<%= StrutSerieForm.AttributiTipoDocList.NAME%>" />--%>
                    </slf:section>
                    <sl:newLine />
                    <div class="livello1"><b style="color: #d3101c;">Definizione del formato di output</b></div>
                            <sl:newLine />
                            <slf:lblField name="<%=StrutSerieForm.RegoleRapprDetail.DL_FORMATO_OUT%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />

                </slf:step>
            </slf:wizard>

            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <%--            <c:if test="${(sessionScope['###_FORM_CONTAINER']['registroRegoleFiltraggioList'].status eq 'view') }">
                            <div class="livello1"><b style="color: #d3101c;">Registri - Tipologie di Unità documentarie legate alla serie</b></div>
                            <slf:listNavBar name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>"  />
                            <slf:listNavBar  name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>"  />
                            <sl:newLine skipLine="true"/>

            </c:if>
            --%>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

