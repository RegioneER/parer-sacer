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

<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <%
        String var = "";
    %>
    <c:if test="${!empty TIPORICERCA && TIPORICERCA == 'AVANZATA'}">
        <%
            var = "Ricerca avanzata unità documentarie";
        %>
    </c:if>
    <c:if test="${!empty TIPORICERCA && TIPORICERCA == 'VERS_ANNULLATI'}">
        <% var = "Ricerca versamenti annullati";%>
    </c:if>
    <sl:head  title="<%=var%>">

        <script type='text/javascript' src='<c:url value="/js/sips/customUDMessageBox.js" />'></script>

    <script type='text/javascript'>
        $(document).ready(function () {
            $('#Ds_link_unita_doc').hide();
            $('#Ds_link_unita_doc_oggetto').hide();
            $('#Cd_registro_key_unita_doc_link').hide();
            $('#Aa_key_unita_doc_link').hide();
            $('#Cd_key_unita_doc_link').hide();            
            gestisciRifTemp();
            initChangeEvents();
        });

        function initChangeEvents() {
            $('#Con_collegamento').change(function () {
                var input = $(this).val();
                if (input === '1') {
                    $('#Ds_link_unita_doc').show();
                    $('#Cd_registro_key_unita_doc_link').show();
                    $('#Aa_key_unita_doc_link').show();
                    $('#Cd_key_unita_doc_link').show();
                } else {
                    $('#Ds_link_unita_doc').hide();
                    $('#Cd_registro_key_unita_doc_link').hide();
                    $('#Aa_key_unita_doc_link').hide();
                    $('#Cd_key_unita_doc_link').hide();
                }
            });

            $('#Fl_rif_temp_vers').change(function () {
                gestisciRifTemp();
            });

            // Ricontrollo se vale uno quando ricarico la pagina               
            if ($('#Con_collegamento').val() === '1') {
                $('#Ds_link_unita_doc').show();
                $('#Cd_registro_key_unita_doc_link').show();
                $('#Aa_key_unita_doc_link').show();
                $('#Cd_key_unita_doc_link').show();
            }
            

            $('#Is_oggetto_collegamento').change(function () {
                var $input = $(this).val();
                if ($input === '1') {
                    $('#Ds_link_unita_doc_oggetto').show();
                } else {
                    $('#Ds_link_unita_doc_oggetto').hide();
                }
            })

            // Ricontrollo se vale uno quando ricarico la pagina               
            if ($('#Is_oggetto_collegamento').val() === '1') {
                $('#Ds_link_unita_doc_oggetto').show();
            }
        };

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
    <c:set var="addSerie" value="${sessionScope['###_FORM_CONTAINER']['unitaDocumentariePerSerie']['id_contenuto_serie'].value != null}"/>
    <c:set var="addRichAnnul" value="${sessionScope['###_FORM_CONTAINER']['unitaDocumentariePerRichAnnulVers']['id_rich_annul_vers'].value != null}"/>
    <sl:header changeOrganizationBtnDescription="Cambia struttura" />
    <sl:menu />
    <sl:content>
        <!--    Bottoni per custom MessageBox in caso javascript sia disabilitato -->
        <slf:messageBox  />
        <%--<c:if test="${!empty requestScope.customBox}">
            <div class="messages customBox ">
                <ul>
                    <li class="message warning ">Nella lista che hai selezionato sono presenti documenti appartenenti a volumi chiusi. Vuoi che questi ultimi siano esclusi dall'inserimento?</li>
                </ul>
            </div>
        </c:if>--%>
        <!--            <div class="pulsantieraMB">
        <%--<sl:pulsantiera >
            <slf:buttonList name="<%= UnitaDocumentarieForm.VolumeCustomMessageButtonList.NAME%>"/>
        </sl:pulsantiera>--%>
    </div>-->

        <sl:newLine skipLine="true"/>

        <c:if test="${!empty TIPORICERCA && TIPORICERCA == 'AVANZATA' }">
            <sl:contentTitle title="RICERCA AVANZATA UNIT&Agrave; DOCUMENTARIE"/>
        </c:if>
        <c:if test="${!empty TIPORICERCA && TIPORICERCA == 'VERS_ANNULLATI'}">
            <sl:contentTitle title="RICERCA VERSAMENTI ANNULLATI"/>
        </c:if>

        <c:choose>
            <c:when test="${!empty volCorrente && volCreato eq false}">
                <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.NAME%>" hideOperationButton="true" />
            </c:when>
            <c:otherwise>
                <slf:fieldBarDetailTag name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            </c:otherwise>
        </c:choose>

        <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieTabs.NAME%>" tabElement="FiltriRicercaAvanzata">

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:section name="<%=UnitaDocumentarieForm.UDRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_KEY_UNITA_DOC%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_KEY_UNITA_DOC_CONTIENE%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.NM_TIPO_UNITA_DOC%>"  colSpan="2" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FLAG_DATI_SPEC_PRESENTI_UD%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_VERSIONE_XSD_UD%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DL_OGGETTO_UNITA_DOC%>"  />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_PROFILO_NORMATIVO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DT_REG_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DT_REG_UNITA_DOC_A%>" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.NM_TIPO_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FLAG_DATI_SPEC_PRESENTI_DOC%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_VERSIONE_XSD_DOC%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DL_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DS_AUTORE_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.TI_DOC%>"  />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_KEY_DOC_VERS%>" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=UnitaDocumentarieForm.ParamVersRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="4" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.TI_CONSERVAZIONE%>"  />                    
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_FORZA_ACCETTAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_FORZA_CONSERVAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_FORZA_COLLEGAMENTO%>" colSpan="2"/>
                    <sl:newLine />
                    <%--<slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_FORZA_HASH%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_FORZA_FMT_NUMERO%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_FORZA_FMT_FILE%>" colSpan="1"/>
                    <sl:newLine />--%>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_VERSIONE_WS%>" colSpan="1"/>
                </slf:section>
                <slf:section name="<%=UnitaDocumentarieForm.ContrConservRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DT_ACQUISIZIONE_UNITA_DOC_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.ORE_DT_ACQUISIZIONE_UNITA_DOC_DA%>" name2="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.MINUTI_DT_ACQUISIZIONE_UNITA_DOC_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DT_ACQUISIZIONE_UNITA_DOC_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.ORE_DT_ACQUISIZIONE_UNITA_DOC_A%>" name2="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.MINUTI_DT_ACQUISIZIONE_UNITA_DOC_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_DOC_AGGIUNTI%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_DOC_ANNUL%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_UNITA_DOC_ANNUL%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_AGG_META%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_UNITA_DOC_FIRMATO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.TI_ESITO_VERIF_FIRME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FL_HASH_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.TI_STATO_CONSERVAZIONE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.TI_STATO_UD_ELENCO_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.NM_SISTEMA_MIGRAZ%>" colSpan="2"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FLAG_DATI_SPEC_PRESENTI_SM%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.NM_SUB_STRUT%>" colSpan="2"/>
                </slf:section>
                <slf:section name="<%=UnitaDocumentarieForm.ProfiloArchivistico.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DS_CLASSIF%>" colSpan="3" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_FASCIC%>" colSpan="2" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.CD_SOTTOFASCIC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DS_OGGETTO_FASCIC%>" colSpan="2" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.DS_OGGETTO_SOTTOFASCIC%>" colSpan="2" />
                </slf:section>
                <slf:section name="<%=UnitaDocumentarieForm.FiltriCollegamenti.NAME%>" styleClass="importantContainer">
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <span class="containerLeft titleFiltri w2ctr">Filtri su unità documentaria con collegamento</span>
                    <span class="containerLeft titleFiltri w2ctr">Filtri su unità documentaria oggetto di collegamenti</span>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.CON_COLLEGAMENTO%>" />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.IS_OGGETTO_COLLEGAMENTO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.COLLEGAMENTO_RISOLTO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.CD_REGISTRO_KEY_UNITA_DOC_LINK%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.AA_KEY_UNITA_DOC_LINK%>"  />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.CD_KEY_UNITA_DOC_LINK%>"  />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="2" controlWidth="w100" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.DS_LINK_UNITA_DOC%>" />
                    <slf:lblField colSpan="2" controlWidth="w100" name="<%=UnitaDocumentarieForm.FiltriCollegamentiUnitaDocumentarie.DS_LINK_UNITA_DOC_OGGETTO%>" />
                    <sl:newLine skipLine="true"/>
                    <%--<slf:section name="<%=UnitaDocumentarieForm.UnitaDocCollegata.NAME%>" styleClass="importantContainer">--%>
                    <%--</slf:section>--%>
                </slf:section>
                <slf:section name="<%=UnitaDocumentarieForm.FiltriComponenti.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DT_CREAZIONE_DA%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.ORE_DT_CREAZIONE_DA%>" name2="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.MINUTI_DT_CREAZIONE_DA%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DT_CREAZIONE_A%>" controlWidth="w70" colSpan="1"/>
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.ORE_DT_CREAZIONE_A%>" name2="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.MINUTI_DT_CREAZIONE_A%>" controlWidth="w20" controlWidth2="w20" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NM_TIPO_STRUT_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DL_URN_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NM_TIPO_COMP_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_NOME_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.TI_SUPPORTO_COMP%>" colSpan="2"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_ID_COMP_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NM_TIPO_RAPPR_COMP%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.FL_FORZA_ACCETTAZIONE_COMP%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.FL_FORZA_CONSERVAZIONE_COMP%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_HASH_FILE_CALC%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_HASH_FILE_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NI_SIZE_FILE_DA%>" colSpan="2" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NI_SIZE_FILE_A%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NM_MIMETYPE_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_FORMATO_RAPPR_CALC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_FORMATO_RAPPR_ESTESO_CALC%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.NM_FORMATO_FILE_VERS%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.TI_ESITO_CONTR_FORMATO_FILE%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.FL_COMP_FIRMATO%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.TI_ESITO_CONTR_CONFORME%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DT_SCAD_FIRMA_COMP_DA%>" colSpan="2" controlWidth="w20"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DT_SCAD_FIRMA_COMP_A%>" colSpan="2" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.FL_RIF_TEMP_VERS%>" colSpan="2" controlWidth="w20"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.DS_RIF_TEMP_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.TI_ESITO_VERIF_FIRMA%>" colSpan="2"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriComponentiUnitaDocumentarie.TI_ESITO_VERIF_FIRME_CHIUS%>" colSpan="2"/>
                </slf:section>
                    <slf:section name="<%=UnitaDocumentarieForm.FiltriFirmatari.NAME%>" styleClass="importantContainer">
                    <%--<slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie.CON_FIRMATARIO%>" />
                    <sl:newLine />--%>
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie.NM_FIRMATARIO%>"  />                                        
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie.NM_COGNOME_FIRMATARIO%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriFirmatariUnitaDocumentarie.CD_FIRMATARIO%>" />                    
                </slf:section>
                    
                <slf:section name="<%=UnitaDocumentarieForm.FiltriFascicoli.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.CD_COMPOSITO_VOCE_TITOL%>" />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.AA_FASCICOLO%>"  />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.CD_KEY_FASCICOLO%>"  />
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.AA_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.AA_FASCICOLO_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.CD_KEY_FASCICOLO_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.CD_KEY_FASCICOLO_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.NM_TIPO_FASCICOLO%>"  colSpan="2" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.DS_OGGETTO_FASCICOLO%>"  />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.DT_APE_FASCICOLO_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.DT_APE_FASCICOLO_A%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.DT_CHIU_FASCICOLO_DA%>" />
                    <slf:lblField colSpan="2" controlWidth="w30" name="<%=UnitaDocumentarieForm.FiltriFascicoliUnitaDocumentarie.DT_CHIU_FASCICOLO_A%>" />
                </slf:section>

                <%--<c:if test="${!empty volCorrente}">
                    <slf:section name="<%=UnitaDocumentarieForm.VolumeCorrente.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.VOL_CURR%>" colSpan="1" />
                        <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.STATO_VOL%>" colSpan="1" />
                        <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.ESCLUDI_UD_VOL_CURR%>" colSpan="1" />
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.VISUALIZZA_VOLUME%>" colSpan="1" />
                    </slf:section>
                </c:if>--%>

                <slf:lblField colSpan="4" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata.FILTRI_DATI_SPEC%>"  />
                <sl:newLine />

            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!-- piazzo i bottoni ricerca e pulisci -->
            <sl:pulsantiera>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.RICERCA_UD%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.PULISCI_UD%>" colSpan="3" />
                <c:if test="${!empty TIPORICERCA && TIPORICERCA == 'AVANZATA'}">
                    <c:if test="${empty volCorrente}">
                        <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.CREA_CRITERIO_RAGGR%>" colSpan="3" />
                    </c:if>
                </c:if>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.DOWNLOAD_CONTENUTO%>" colSpan="3" />
            </sl:pulsantiera>
            <div><slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentariePerSerie.ID_CONTENUTO_SERIE%>" colSpan="1" /></div>
            <div><slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentariePerRichAnnulVers.ID_RICH_ANNUL_VERS%>" colSpan="1" /></div>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" pageSizeRelated="true"/>
            <c:choose>
                <c:when test="${!empty TIPORICERCA && TIPORICERCA == 'AVANZATA' && (addSerie || addRichAnnul)}">
                    <slf:selectList name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" addList="true"/>
                </c:when>
                <c:otherwise>
                    <!-- Caso versamenti annullati e ricerca avanzata normale-->
                    <slf:list name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>"/>
                </c:otherwise>
            </c:choose>
            <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" />
            <sl:newLine skipLine="true"/>

            <c:if test="${!empty TIPORICERCA && TIPORICERCA == 'AVANZATA'}">
                <c:choose>
                    <c:when test="${addSerie}">
                        <!-- Inserisco la lista vuota delle unità documentarie da aggiungere alla serie -->
                        <slf:section name="<%=UnitaDocumentarieForm.UnitaDocumentarieToSerieSection.NAME%>" styleClass="importantContainer">                
                            <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentariePerSerieList.NAME%>" pageSizeRelated="true"/>
                            <slf:selectList name="<%= UnitaDocumentarieForm.UnitaDocumentariePerSerieList.NAME%>" addList="false"/>
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentariePerSerieList.NAME%>" />

                            <sl:newLine skipLine="true"/>
                            <sl:pulsantiera>
                                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.ADD_TO_SERIE%>" colSpan="3" />
                            </sl:pulsantiera>
                        </slf:section>
                    </c:when>
                    <c:when test="${addRichAnnul}">
                        <!-- Inserisco la lista vuota delle unità documentarie da aggiungere alla richiesta di annullamento -->
                        <slf:section name="<%=UnitaDocumentarieForm.UnitaDocumentarieToRichAnnulVersSection.NAME%>" styleClass="importantContainer">                
                            <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentariePerRichAnnulVersList.NAME%>" pageSizeRelated="true"/>
                            <slf:selectList name="<%= UnitaDocumentarieForm.UnitaDocumentariePerRichAnnulVersList.NAME%>" addList="false"/>
                            <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentariePerRichAnnulVersList.NAME%>" />

                            <sl:newLine skipLine="true"/>
                            <sl:pulsantiera>
                                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.ADD_TO_RICH_ANNUL%>" colSpan="3" />
                            </sl:pulsantiera>
                        </slf:section>
                    </c:when>
                </c:choose>
            </c:if>
        </slf:tab>

        <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieTabs.NAME%>" tabElement="FiltriDatiSpec">
            <!--  piazzo la lista dei filtri di ricerca dati specifici -->
            <%--<slf:listNavBar name="<%= UnitaDocumentarieForm.FiltriDatiSpecUnitaDocumentarieList.NAME%>" pageSizeRelated="true"/>--%>
            <slf:nestedList name="<%= UnitaDocumentarieForm.FiltriDatiSpecUnitaDocumentarieList.NAME%>" subListName="<%= UnitaDocumentarieForm.DefinitoDaList.NAME%>" multiRowEdit="true"/>
            <%--<slf:listNavBar name="<%= UnitaDocumentarieForm.FiltriDatiSpecUnitaDocumentarieList.NAME%>" />--%>
            <sl:newLine skipLine="true"/>
        </slf:tab>

    </sl:content>
    <sl:footer />
</sl:body>
</sl:html>
