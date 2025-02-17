<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <%
        String var = "";
    %>
    <sl:head  title="Ricerca unità documentarie per dati specifici">
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
        <sl:newLine skipLine="true"/>
       
        <sl:contentTitle title="RICERCA UNIT&Agrave; DOCUMENTARIE PER DATI SPECIFICI "/>

        <c:choose>
            <c:when test="${!empty volCorrente && volCreato eq false}">
                <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.NAME%>" hideOperationButton="true" />
            </c:when>
            <c:otherwise>
                <slf:fieldBarDetailTag name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            </c:otherwise>
        </c:choose>

        <slf:tab  name="<%= UnitaDocumentarieForm.UnitaDocumentarieTabs.NAME%>" tabElement="FiltriRicercaAvanzata">

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:section name="<%=UnitaDocumentarieForm.UDRicercaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.CD_REGISTRO_KEY_UNITA_DOC%>" />                    
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.AA_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.AA_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.CD_KEY_UNITA_DOC_DA%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.CD_KEY_UNITA_DOC_A%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.NM_TIPO_UNITA_DOC%>"  colSpan="2" />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.FLAG_DATI_SPEC_PRESENTI_UD%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.CD_VERSIONE_XSD_UD%>" colSpan="1"/>
                    <sl:newLine />                    
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.NM_TIPO_DOC%>" colSpan="2"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.FLAG_DATI_SPEC_PRESENTI_DOC%>" colSpan="1"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.CD_VERSIONE_XSD_DOC%>" colSpan="1"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.NM_SUB_STRUT%>" colSpan="2"/>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:lblField colSpan="4" name="<%=UnitaDocumentarieForm.FiltriUnitaDocumentarieDatiSpec.FILTRI_DATI_SPEC%>"  />
                <sl:newLine />

            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!-- piazzo i bottoni ricerca e pulisci -->
            <sl:pulsantiera>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.RICERCA_UD%>" colSpan="3" />
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieRicercaButtonList.PULISCI_UD%>" colSpan="3" />               
            </sl:pulsantiera>
            <div><slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentariePerSerie.ID_CONTENUTO_SERIE%>" colSpan="1" /></div>
            <div><slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentariePerRichAnnulVers.ID_RICH_ANNUL_VERS%>" colSpan="1" /></div>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>"/>
            <slf:listNavBar  name="<%= UnitaDocumentarieForm.UnitaDocumentarieList.NAME%>" />
            <sl:newLine skipLine="true"/>
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