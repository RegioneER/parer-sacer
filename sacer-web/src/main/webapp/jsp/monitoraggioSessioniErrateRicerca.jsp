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

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista sessioni errate" >
        <script type="text/javascript" src="<c:url value="/js/sips/customCalcStrutVersMessageBox.js"/>" ></script>
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>

        <script type='text/javascript'>
            $(document).ready(function () {
                checkVerificati = $('table.list td > input[name="Fl_sessione_err_verif"]:checked');
                $('#Sessione_ses_err_verif').change(
                        function () {
                            window.location = "Monitoraggio.html?operation=filtraSessioniVerificate&Sessione_ses_err_verif=" + this.value;
                        });
            });
        </script>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <!--  Bottoni per custom MessageBox in caso javascript sia disabilitato -->
            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= MonitoraggioForm.SessioniErrateCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA SESSIONI ERRATE"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessione.SESSIONE_SES_ERR_VERIF%>" colSpan="1"/>
                <!-- Bottone cerca in caso di javascript disattivato -->
                <noscript><slf:lblField name="<%=MonitoraggioForm.FiltriSessione.CERCA_SESSIONI_ERRATE%>" colSpan="1"/></noscript>    
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.SessioniErrateList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= MonitoraggioForm.SessioniErrateList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.SessioniErrateList.NAME%>" />

            <sl:pulsantiera>
                <slf:buttonList name="<%=MonitoraggioForm.SalvaVerificaButtonList.NAME%>">
                    <slf:lblField name="<%=MonitoraggioForm.SalvaVerificaButtonList.SALVA_VERIFICA_SESSIONE%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </slf:buttonList>
                <slf:lblField name="<%=MonitoraggioForm.FiltriSessione.CALCOLA_STRUTTURA_VERSANTE%>" width="w50" controlWidth="w30" labelWidth="w40"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
