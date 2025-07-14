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

<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Versamenti annullati unità documentarie" >
        <script type="text/javascript">
            $(document).ready(function () {
                creaLinkDownloadRapportiDiVersamento();
                creaLinkDownloadRichiestaXML();
                creaLinkDownloadRispostaXML();
                
                function creaLinkDownloadRapportiDiVersamento() {
                    var row = 0;
                    var downloadIdx = $('#UnitaDocumentarieAnnullateList tr th:contains("Rapporti di versamento")').index();
                    $("#UnitaDocumentarieAnnullateList tbody").find("tr").each(function(ind, elem) {
                        $(elem).find('td:eq(' + downloadIdx + ')').html(function () {
                            var el = $("<a></a>").attr("href", "UnitaDocumentarie.html?operation__scarica_rv__" + row).addClass("DownloadFileSessione");
                            
                            return el;
                        });
                        row++;
                    });
                }
                
                function creaLinkDownloadRichiestaXML() {
                    var row = 0;
                    var downloadIdx = $('#UnitaDocumentarieAnnullateList tr th:contains("Richiesta annullamento")').index();
                    $("#UnitaDocumentarieAnnullateList tbody").find("tr").each(function(ind, elem) {
                        if ($(elem).find("td input[name='Ti_creazione_rich_annul_vers']").val() === 'WEB_SERVICE') {
                            $(elem).find('td:eq(' + downloadIdx + ')').html(function () {
                                var el = $("<a></a>").attr("href", "UnitaDocumentarie.html?operation__scarica_xml_rich__" + row).addClass("DownloadFileSessione");

                                return el;
                            });
                        }
                        row++;
                    });
                }
                
                function creaLinkDownloadRispostaXML() {
                    var row = 0;
                    var downloadIdx = $('#UnitaDocumentarieAnnullateList tr th:contains("Esito annullamento")').index();
                    $("#UnitaDocumentarieAnnullateList tbody").find("tr").each(function(ind, elem) {
                        if ($(elem).find("td input[name='Ti_creazione_rich_annul_vers']").val() === 'WEB_SERVICE') {
                            $(elem).find('td:eq(' + downloadIdx + ')').html(function () {
                                var el = $("<a></a>").attr("href", "UnitaDocumentarie.html?operation__scarica_xml_risp__" + row).addClass("DownloadFileSessione");

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
            <sl:contentTitle title="VERSAMENTI ANNULLATI UNIT&Agrave; DOCUMENTARIE"/>
            <%--<slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.NAME%>" hideOperationButton="true" />--%>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:section name="<%=UnitaDocumentarieForm.UDRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.ID_TIPO_UNITA_DOC%>"  colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.ID_TIPO_DOC%>" colSpan="2"/>
                    <sl:newLine />  
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.DT_CREAZIONE_UNITA_DOC_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.ORE_DT_CREAZIONE_UNITA_DOC_DA%>" name2="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.MINUTI_DT_CREAZIONE_UNITA_DOC_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.DT_CREAZIONE_UNITA_DOC_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.ORE_DT_CREAZIONE_UNITA_DOC_A%>" name2="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.MINUTI_DT_CREAZIONE_UNITA_DOC_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />            
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.DT_REG_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.DT_REG_UNITA_DOC_A%>"  />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.DT_ANNUL_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.DT_ANNUL_A%>"  />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAnnullate.TI_ANNULLAMENTO%>"  colSpan="2" />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.RICERCA_UDANNULLATE%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.DOWNLOAD_CONTENUTO_ANNULLATE%>" colSpan="3" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->                
            <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentarieAnnullateList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= UnitaDocumentarieForm.UnitaDocumentarieAnnullateList.NAME%>" />
            <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentarieAnnullateList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
