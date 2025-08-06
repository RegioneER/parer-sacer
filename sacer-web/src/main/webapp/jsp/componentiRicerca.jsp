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

<%@ page import="it.eng.parer.slite.gen.form.ComponentiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca componenti" >

        <script type='text/javascript'>
            $(document).ready(function () {
                // Al caricamento della pagina, eseguo gestisciRifTemp() e inizializzo il change sul campo
                gestisciRifTemp();
                initChangeEvents();
            });

            function initChangeEvents() {
                $('#Fl_rif_temp_vers').change(function () {
                    gestisciRifTemp();
                });
            }
            ;

            function gestisciRifTemp() {
                var flRifTemp = $('[name=Fl_rif_temp_vers]');
                if (flRifTemp.val() === '1') {
                    $('#Ds_rif_temp_vers').show();
                } else {
                    $('#Ds_rif_temp_vers').val("");
                    $('#Ds_rif_temp_vers').hide();
                }
            }
            ;
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="RICERCA COMPONENTI"/>

            <slf:fieldSet borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:section name="<%=ComponentiForm.ChiaveSection.NAME%>" styleClass="importantContainer">

                    <slf:lblField colSpan="2" name="<%=ComponentiForm.RicComponentiFiltri.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=ComponentiForm.RicComponentiFiltri.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=ComponentiForm.RicComponentiFiltri.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                </slf:section> 

                <sl:newLine />
                <slf:section name="<%=ComponentiForm.ComponenteRicerca.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DT_CREAZIONE_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=ComponentiForm.RicComponentiFiltri.ORE_DT_CREAZIONE_DA%>" name2="<%=ComponentiForm.RicComponentiFiltri.MINUTI_DT_CREAZIONE_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DT_CREAZIONE_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=ComponentiForm.RicComponentiFiltri.ORE_DT_CREAZIONE_A%>" name2="<%=ComponentiForm.RicComponentiFiltri.MINUTI_DT_CREAZIONE_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NM_TIPO_STRUT_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DL_URN_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NM_TIPO_COMP_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_NOME_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.TI_SUPPORTO_COMP%>" colSpan="2"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_ID_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NM_TIPO_RAPPR_COMP%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.FL_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.FL_FORZA_CONSERVAZIONE%>" colSpan="2"/>
                    <%--                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.FL_DOC_ANNUL%>" colSpan="2"/>--%>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NM_SUB_STRUT%>" colSpan="2"/>
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=ComponentiForm.File.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_HASH_FILE_CALC%>" colSpan="2" />
                    <sl:newLine />                    
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.FL_HASH_VERS%>" colSpan="2"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_HASH_FILE_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NI_SIZE_FILE_DA%>" colSpan="2" />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NI_SIZE_FILE_A%>" colSpan="2" />
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=ComponentiForm.Formato.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NM_MIMETYPE_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_FORMATO_RAPPR_CALC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_FORMATO_RAPPR_ESTESO_CALC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.NM_FORMATO_FILE_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.TI_ESITO_CONTR_FORMATO_FILE%>" colSpan="2"/>
                </slf:section>
                <slf:section name="<%=ComponentiForm.Firma.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.FL_COMP_FIRMATO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.TI_ESITO_CONTR_CONFORME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DT_SCAD_FIRMA_COMP_DA%>" colSpan="2" controlWidth="w20"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DT_SCAD_FIRMA_COMP_A%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.FL_RIF_TEMP_VERS%>" colSpan="2" controlWidth="w20"/>
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.DS_RIF_TEMP_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.TI_ESITO_VERIF_FIRME%>" colSpan="2"/>
                    <%--<sl:newLine />
                    <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.TI_ESITO_VERIF_FIRME_CHIUS%>" colSpan="2"/>--%>
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone di ricerca e pulisci -->
                <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.RICERCA%>" colSpan="2" />
                <slf:lblField name="<%=ComponentiForm.RicComponentiFiltri.PULISCI%>" colSpan="2" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ComponentiForm.ComponentiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ComponentiForm.ComponentiList.NAME%>" />
            <slf:listNavBar  name="<%= ComponentiForm.ComponentiList.NAME%>" exportExcel="true" />

            <sl:newLine skipLine="true"/>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
