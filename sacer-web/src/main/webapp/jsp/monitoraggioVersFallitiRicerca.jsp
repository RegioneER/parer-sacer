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

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista versamenti falliti" >
        <script type='text/javascript' src="<c:url value="/js/sips/customCheckBoxSesVerif.js"/>" ></script>
        <script type='text/javascript'>
            $(document).ready(function() {
                checkVerificati = $('table.list td > input[name="Fl_sessione_err_verif"]:checked');

                $(document).bind('keypress', function (e) {
                    if (e.keyCode == 13) {
                        e.preventDefault();
                        $("input[name='operation__ricercaVers']").trigger('click');
                    }
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA VERSAMENTI FALLITI"/>
            <slf:fieldBarDetailTag name="<%= MonitoraggioForm.RiepilogoVersamenti.NAME%>" hideOperationButton="true"/>
            <slf:tab name="<%=MonitoraggioForm.FiltriRicercaVersamentiFallitiTabs.NAME%>" tabElement="FiltriGenerali">
                <slf:fieldSet  borderHidden="false">
                    <!-- piazzo i campi del filtro di ricerca -->
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.ID_AMBIENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.ID_ENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.ID_STRUT%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.TI_SESSIONE_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.PERIODO_VERS%>" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.GIORNO_VERS_DA%>" colSpan="1" />
                    <slf:doubleLblField name="<%=MonitoraggioForm.FiltriVersamenti.ORE_VERS_DA%>" name2="<%=MonitoraggioForm.FiltriVersamenti.MINUTI_VERS_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.GIORNO_VERS_A%>" colSpan="1" />
                    <slf:doubleLblField name="<%=MonitoraggioForm.FiltriVersamenti.ORE_VERS_A%>" name2="<%=MonitoraggioForm.FiltriVersamenti.MINUTI_VERS_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.FL_RISOLTO%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERSAMENTO_SES_ERR_VERIF%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERSAMENTO_SES_ERR_NON_RISOLUB%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.CLASSE_ERRORE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.SOTTOCLASSE_ERRORE%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.CODICE_ERRORE%>" colSpan="4" />
                    <sl:newLine />
                </slf:fieldSet>

                <sl:pulsantiera>
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.RICERCA_VERS%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamenti.VERIFICA_AUTOMATICA%>" colSpan="2" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true" />

            </slf:tab>

            <slf:tab name="<%=MonitoraggioForm.FiltriRicercaVersamentiFallitiTabs.NAME%>" tabElement="FiltriUlteriori">
                <slf:fieldSet  borderHidden="false">
                    <sl:newLine />
                    <sl:newLine skipLine="true" />
                    <slf:section name="<%=MonitoraggioForm.FiltriVersamentiChiaveSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.AA_KEY_UNITA_DOC%>" colSpan="1" />
                    <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.CD_KEY_UNITA_DOC%>" colSpan="1" />
                        <sl:newLine />
                        <div class="slLabel wlbl" >&nbsp;</div>
                        <div class="containerLeft w2ctr">&nbsp;</div>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                        <sl:newLine />
                        <div class="slLabel wlbl" >&nbsp;</div>
                        <div class="containerLeft w2ctr">&nbsp;</div>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                        <slf:lblField name="<%=MonitoraggioForm.FiltriVersamentiUlteriori.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    </slf:section>
                    
                </slf:fieldSet>
            </slf:tab>

            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.VersamentiFallitiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioForm.VersamentiFallitiList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.VersamentiFallitiList.NAME%>" />

            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:buttonList name="<%=MonitoraggioForm.SalvaVerificaButtonList.NAME%>">
                    <slf:lblField name="<%=MonitoraggioForm.SalvaVerificaButtonList.SALVA_VERIFICA_VERSAMENTO%>" colSpan="2"/>
                </slf:buttonList>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
