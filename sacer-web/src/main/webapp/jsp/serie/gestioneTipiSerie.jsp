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

<%@ page import="it.eng.parer.slite.gen.form.StrutSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutSerieForm.TipologieSerieList.DESCRIPTION%>" >
        <script type="text/javascript">

            $(document).ready(function () {
                inserisciIllimitato();
                initChangeEvents();
            });

            function inserisciIllimitato() {
                $("#TipologieSerieList tbody tr td[title='9.999']").each(function (index) {
                    $(this).html('Illimitato');
                });
            }

            function initChangeEvents() {
                $('#Tipi_serie_no_gen_modello').change(function () {
                    if ($(this).is(":checked")) {
                        // Nascondo e azzero la casella di testo...
                        $('#Id_modello_tipo_serie').hide();
                        $('#Id_modello_tipo_serie').val("");
                        // ... e nascondo la relativa label
                        $('label[for=Id_modello_tipo_serie], input#Id_modello_tipo_serie').hide();
                    } else {
                        $('#Id_modello_tipo_serie').show();
                        $('label[for=Id_modello_tipo_serie], input#Id_modello_tipo_serie').show();
                    }
                });
                $("#Tipi_serie_no_gen_modello").trigger('change');
            }
            ;
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="Gestione tipi di serie di unità documentarie"/>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet borderHidden="true" styleClass="">
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.FiltriTipologieSerie.ID_AMBIENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.FiltriTipologieSerie.ID_ENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.FiltriTipologieSerie.ID_STRUT%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.FiltriTipologieSerie.IS_ATTIVO%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.FiltriTipologieSerie.TIPI_SERIE_NO_GEN_MODELLO%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.FiltriTipologieSerie.ID_MODELLO_TIPO_SERIE%>" />
            </slf:fieldSet>

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=StrutSerieForm.FiltriTipologieSerie.RICERCA_TIPOLOGIE_SERIE_BUTTON%>" width="w25" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%=StrutSerieForm.TipologieSerieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%=StrutSerieForm.TipologieSerieList.NAME%>" />
            <slf:listNavBar name="<%=StrutSerieForm.TipologieSerieList.NAME%>" /> 

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
