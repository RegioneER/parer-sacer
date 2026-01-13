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

<%@ page import="it.eng.parer.slite.gen.form.FascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Versamenti annullati fascicoli" >
        <script type="text/javascript">
            $(document).ready(function () {
                creaLinkDownloadRapportoDiVersamento();
                creaLinkDownloadRichiestaXML();
                creaLinkDownloadRispostaXML();
                
                function creaLinkDownloadRapportoDiVersamento() {
                    var row = 0;
                    var downloadIdx = $('#FascicoliAnnullatiList tr th:contains("Rapporto di versamento")').index();
                    $("#FascicoliAnnullatiList tbody").find("tr").each(function(ind, elem) {
                        $(elem).find('td:eq(' + downloadIdx + ')').html(function () {
                            var el = $("<a></a>").attr("href", "Fascicoli.html?operation__scarica_rv_fasc__" + row).addClass("DownloadFileSessione");
                            
                            return el;
                        });
                        row++;
                    });
                }
                
                function creaLinkDownloadRichiestaXML() {
                    var row = 0;
                    var downloadIdx = $('#FascicoliAnnullatiList tr th:contains("Richiesta annullamento")').index();
                    $("#FascicoliAnnullatiList tbody").find("tr").each(function(ind, elem) {
                        if ($(elem).find("td input[name='Ti_creazione_rich_annul_vers']").val() === 'WEB_SERVICE') {
                            $(elem).find('td:eq(' + downloadIdx + ')').html(function () {
                                var el = $("<a></a>").attr("href", "Fascicoli.html?operation__scarica_xml_rich__" + row).addClass("DownloadFileSessione");

                                return el;
                            });
                        }
                        row++;
                    });
                }
                
                function creaLinkDownloadRispostaXML() {
                    var row = 0;
                    var downloadIdx = $('#FascicoliAnnullatiList tr th:contains("Esito annullamento")').index();
                    $("#FascicoliAnnullatiList tbody").find("tr").each(function(ind, elem) {
                        if ($(elem).find("td input[name='Ti_creazione_rich_annul_vers']").val() === 'WEB_SERVICE') {
                            $(elem).find('td:eq(' + downloadIdx + ')').html(function () {
                                var el = $("<a></a>").attr("href", "Fascicoli.html?operation__scarica_xml_risp__" + row).addClass("DownloadFileSessione");

                                return el;
                            });
                        }
                        row++;
                    });
                }
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:contentTitle title="VERSAMENTI ANNULLATI FASCICOLI"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:section name="<%=FascicoliForm.FascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliAnnullati.AA_FASCICOLO%>"  />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliAnnullati.CD_KEY_FASCICOLO%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.AA_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.AA_FASCICOLO_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.CD_KEY_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.CD_KEY_FASCICOLO_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.NM_TIPO_FASCICOLO%>"  colSpan="2" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=FascicoliForm.FiltriFascicoliAnnullati.DT_ANNUL_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=FascicoliForm.FiltriFascicoliAnnullati.DT_ANNUL_A%>"  />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=FascicoliForm.ProfiloArchivisticoSection.NAME%>" styleClass="importantContainer" >
                    <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliAnnullati.CD_COMPOSITO_VOCE_TITOL%>" />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.RICERCA_FASC_ANNULLATI%>" colSpan="3" />
                <slf:lblField name="<%=FascicoliForm.FiltriFascicoliAnnullati.PULISCI_FASC_ANNULLATI%>" colSpan="3" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->                
            <slf:listNavBar name="<%= FascicoliForm.FascicoliAnnullatiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= FascicoliForm.FascicoliAnnullatiList.NAME%>" />
            <slf:listNavBar  name="<%= FascicoliForm.FascicoliAnnullatiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
