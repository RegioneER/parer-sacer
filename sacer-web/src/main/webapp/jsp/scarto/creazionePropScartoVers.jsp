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

<%@ page import="it.eng.parer.slite.gen.form.ScartoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set var="isPropostaSalvata" value="${sessionScope['###_FORM_CONTAINER']['creazionePropScartoVers']['id_prop_scarto_vers'].value != null}"/>
<sl:html>
    <sl:head title="<%=ScartoForm.CreazionePropScartoTabs.DESCRIPTION%>" >
        <style>
            /* Regole per la tabella Report UD: allineamento a destra delle colonne numeriche dalla 2 alla 7 */
            #ReportUdPropScartoList tbody tr td:nth-child(2), #ReportUdPropScartoList thead tr th:nth-child(2),
            #ReportUdPropScartoList tbody tr td:nth-child(3), #ReportUdPropScartoList thead tr th:nth-child(3),
            #ReportUdPropScartoList tbody tr td:nth-child(4), #ReportUdPropScartoList thead tr th:nth-child(4),
            #ReportUdPropScartoList tbody tr td:nth-child(5), #ReportUdPropScartoList thead tr th:nth-child(5),
            #ReportUdPropScartoList tbody tr td:nth-child(6), #ReportUdPropScartoList thead tr th:nth-child(6),
            #ReportUdPropScartoList tbody tr td:nth-child(7), #ReportUdPropScartoList thead tr th:nth-child(7),
            #ReportUdPropScartoList tbody tr td:nth-child(8), #ReportUdPropScartoList thead tr th:nth-child(8) {
                text-align: right;
            }

            /* Aggiungo l'effetto hover sulla riga */
            #ReportUdPropScartoList tbody tr { cursor: pointer; }
            #ReportUdPropScartoList tbody tr:hover { background-color: #f0f0f0; }
            
            #ReportUdPropScartoList tbody tr td.cella-cliccabile {
                color: #0d6efd;
                cursor: pointer;
                font-weight: bold;
            }
            #ReportUdPropScartoList tbody tr td.cella-cliccabile:hover {
                text-decoration: underline;
                color: #0a58ca;
            }
            
            /* Stili per i pallini semaforo */
            .dot-verde {
                height: 14px; width: 14px;
                background-color: #198754; /* Verde Bootstrap */
                border-radius: 50%;
                display: inline-block;
                box-shadow: 1px 1px 3px rgba(0,0,0,0.3);
            }
            .dot-rosso {
                height: 14px; width: 14px;
                background-color: #dc3545; /* Rosso Bootstrap */
                border-radius: 50%;
                display: inline-block;
                box-shadow: 1px 1px 3px rgba(0,0,0,0.3);
            }
            .center-cell { text-align: center; vertical-align: middle; }
            
            /* Fix per bottoni tagliati nel popup */
            #modalConfermaUd .pulsante {
                height: auto !important;
                line-height: normal !important;
                display: inline-block !important;
            }
        </style>   
        
        <c:if test="${not isPropostaSalvata}">
            <style>
                /* Se la proposta è NUOVA, nascondo il bottone Salva generato dal framework in alto a destra.
                   Se invece è già salvata, questo blocco CSS non viene stampato, e il bottone riappare! */
                .listToolBar input.crud.save {
                    display: none !important;
                }
            </style>
        </c:if>
        
        <script type="text/javascript">
            jQuery(document).ready(function() {
                
                // ==============================================================
                // 1. LOGICA PER I NUMERI CLICCABILI SULLA TABELLA DEL REPORT
                // ==============================================================
                
                // Mappatura colonne
                var mappaColonne = {            
                    2: 'RAGGIUNTO',          // Colonna qta_raggiunto
                    3: 'NON_RAGGIUNTO',      // Colonna qta_non_raggiunto
                    4: 'SENZA_INDICAZIONE',  // Colonna qta_senza_indicazione
                    5: 'ILLIMITATA',         // Colonna qta_illimitate
                    6: 'CONFLITTI',          // Colonna qta_conflitti
                    7: 'IN_ALTRE_PROPOSTE',  // Colonna qta_in_altre_prop
                    8: 'TOTALE'              // Colonna qta_totale
                };

                // Applica visivamente il link ai numeri
                jQuery('#ReportUdPropScartoList tbody tr td').each(function() {
                    var colIndex = jQuery(this).index() + 1; 
                    if (mappaColonne[colIndex]) {
                        var valore = jQuery(this).text().trim();
                        // Rendi cliccabile solo se > 0
                        if (valore !== '0' && valore !== '') {
                            jQuery(this).addClass('cella-cliccabile');
                        }
                    }
                });

                // Evento Click sui numeri
                jQuery('body').on('click', '#ReportUdPropScartoList tbody tr td.cella-cliccabile', function(e) {
                    e.preventDefault();
                    var $td = jQuery(this);
                    var colIndex = $td.index() + 1;
                    
                    var tipologia = $td.closest('tr').find('td:nth-child(1)').text().trim();
                    var colonnaNome = mappaColonne[colIndex];

                    jQuery('input[name*="idden_tipologia_cliccata"], input[id*="idden_tipologia_cliccata"]').val(tipologia);
                    jQuery('input[name*="idden_colonna_cliccata"], input[id*="idden_colonna_cliccata"]').val(colonnaNome);

                    jQuery('input[type="submit"][name*="caricaListaUd"], input[id*="caricaListaUd"]').get(0).click();
                });
                
                // ==============================================================
                // LOGICA PER I PALLINI SEMAFORO (Verde = Scartabile, Rosso = No)
                // ==============================================================
                
                function disegnaPalliniScartabilita(idTabella) {
                    var indiceColonna = -1;
                    
                    jQuery('#' + idTabella + ' thead th').each(function(index) {
                        if (jQuery(this).text().trim() === 'Scartabile') {
                            indiceColonna = index;
                            return false; 
                        }
                    });

                    if (indiceColonna !== -1) {
                        jQuery('#' + idTabella + ' tbody tr').each(function() {
                            var $td = jQuery(this).find('td').eq(indiceColonna);
                            var valore = $td.text().trim();
                            
                            $td.addClass('center-cell'); 
                            
                            if (valore === 'SI') {
                                $td.html('<span class="dot-verde" title="Tempo raggiunto. Elemento scartabile."></span>');
                            } else if (valore === 'NO') {
                                $td.html('<span class="dot-rosso" title="Attenzione! Elemento NON scartabile."></span>');
                            }
                        });
                    }
                }

                disegnaPalliniScartabilita('UdDaSelPropScartoList');
                disegnaPalliniScartabilita('UdSelezionatePropScartoList');
                disegnaPalliniScartabilita('UdInAltrePropScartoList');
                
            });
        </script>
    </sl:head>
    <sl:body>        
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />

            <%-- ========================================================================= --%>
            <%-- POPUP MODALE (Scatta in automatico grazie a requestScope.numAlertTotale)  --%>
            <%-- ========================================================================= --%>
            <c:if test="${!empty requestScope.numAlertTotale}">
                <!-- Overlay scuro che blocca la pagina -->
                <div id="overlayPopupUd" style="position: fixed; top: 0; left: 0; width: 100%; height: 100%; background-color: rgba(0,0,0,0.6); z-index: 9998;"></div>

                <!-- Finestra del popup -->
                <div id="modalConfermaUd" style="position: fixed; top: 30%; left: 50%; transform: translate(-50%, -30%); background: white; border: 2px solid #D3101C; border-radius: 5px; z-index: 9999; padding: 25px; box-shadow: 0px 4px 20px rgba(0,0,0,0.5); min-width: 450px; font-family: Arial, sans-serif;">
                    
                    <h3 style="color: #D3101C; border-bottom: 1px solid #ccc; padding-bottom: 10px; margin-top: 0;">
                        <span style="font-size: 1.2em;">&#9888;</span> Attenzione!
                    </h3>
                    
                    <p style="font-size: 14px; margin: 15px 0; line-height: 1.5;">
                        Sono state selezionate <b><c:out value="${requestScope.numAlertTotale}" /></b> Unit&agrave; Documentarie con incoerenze.<br/><br/>
                        <b>Dettaglio delle anomalie riscontrate:</b>
                    </p>
                    
                    <div style="margin-top: 10px; padding-left: 20px; line-height: 1.8; font-size: 14px;">
                        <c:if test="${requestScope.numAlertNonRaggiunto > 0}">
                            &bull; <b><c:out value="${requestScope.numAlertNonRaggiunto}" /></b> UD: Tempo di conservazione non raggiunto.<br/>
                        </c:if>
                        <c:if test="${requestScope.numAlertSenzaInd > 0}">
                            &bull; <b><c:out value="${requestScope.numAlertSenzaInd}" /></b> UD: Tempo di conservazione senza indicazione.<br/>
                        </c:if>
                        <c:if test="${requestScope.numAlertIllimitata > 0}">
                            &bull; <b><c:out value="${requestScope.numAlertIllimitata}" /></b> UD: Tenuta illimitata.<br/>
                        </c:if>
                    </div>
                    
                    <p style="font-size: 14px; margin: 20px 0 0 0; border-top: 1px solid #eee; padding-top: 15px;">
                        Vuoi comunque procedere con l'inserimento delle UD nella Proposta di Scarto?
                    </p>
                    
                    <div style="text-align: right; margin-top: 25px;">
                        <!-- TASTO ANNULLA: Padding uniformato a 6px 15px -->
                        <input type="button" class="pulsante" value="Annulla" 
                            onclick="document.getElementById('modalConfermaUd').style.display='none'; document.getElementById('overlayPopupUd').style.display='none';" 
                            style="background-color: #888; border: 1px solid #666; color: white; margin-right: 10px; padding: 6px 15px; cursor: pointer; height: auto !important; line-height: normal !important; min-width: 80px;" />
    
                        <!-- TASTO OK: Padding identico all'Annulla (6px 15px) e min-width aggiunto -->
                        <input type="button" class="pulsante" value="OK" 
                            onclick="jQuery('input[name*=\'idden_forza_aggiunta\']').val('SI'); jQuery('input[name*=\'operation__${requestScope.azioneInSospeso}\']').get(0).click();" 
                            style="background-color: #D3101C; color: white; border: none; padding: 6px 15px; font-weight: bold; cursor: pointer; height: auto !important; line-height: normal !important; min-width: 80px;" />
                    </div>
                </div>
            </c:if>
            
            <sl:contentTitle title="<%=ScartoForm.CreazionePropScartoVers.DESCRIPTION%>" /> 
            <slf:fieldBarDetailTag name="<%=ScartoForm.CreazionePropScartoVers.NAME%>" hideBackButton="false" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:section name="<%=ScartoForm.InfoPropostaScartoSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.ID_PROP_SCARTO_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.ID_AMBIENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.ID_ENTE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.ID_STRUT%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.CD_PROP_SCARTO_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.DS_PROP_SCARTO_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.NT_PROP_SCARTO_VERS%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.DT_CREAZIONE_PROP_SCARTO_VERS%>" colSpan="1" />
                    <sl:newLine />    
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.DT_ULTIMA_MOD_PROP_SCARTO_VERS%>" colSpan="1" />
                    <sl:newLine />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.TI_STATO_PROP_SCARTO_VERS_COR%>" colSpan="2" />
                    <sl:newLine />                
                    
                    <%-- Mostro i contatori solo se la proposta è salvata --%>
                    <c:if test="${isPropostaSalvata}">
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.NI_UD_INSERITE%>" colSpan="3" />
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.NI_FASC_INSERITI%>" colSpan="3" />
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.NI_SERIE_INSERITE%>" colSpan="3" />
                    </c:if>

                    <sl:newLine skipLine="true"/>
                    <sl:pulsantiera>
                    <slf:lblField  name="<%=ScartoForm.CreazionePropScartoVers.SALVA_PROPOSTA%>" colSpan="1" />
                    </sl:pulsantiera>
                    <sl:newLine />
                </slf:section>

                <%-- ============================================================== --%>
                <%-- SEZIONE RICHIESTA DI AUTORIZZAZIONE                             --%>
                <%-- ============================================================== --%>
                <c:if test="${isPropostaSalvata}">
                <slf:section name="<%=ScartoForm.RichiestaAutorizzazioneSection.NAME%>" styleClass="importantContainer">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.AVVIA_RICHIESTA_AUTORIZZAZIONE%>" colSpan="1" />
                    </sl:pulsantiera>
                    <sl:newLine />
                </slf:section>

                <%-- ============================================================== --%>
                <%-- SEZIONE DATI RICHIESTA AUTORIZZAZIONE (display readonly)        --%>
                <%-- ============================================================== --%>
                <slf:section name="<%=ScartoForm.DatiRichiestaAutSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.NT_AUTORITA%>" colSpan="4" />
                    <sl:newLine />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.CD_REGISTRO_RICH_AUT%>" colSpan="3" />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.AA_RICH_AUT%>" colSpan="3" />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.CD_RICH_AUT%>" colSpan="3" />
                    <sl:newLine />
                </slf:section>

                <%-- ============================================================== --%>
                <%-- SEZIONE RISPOSTA DELL'AUTORITA' (pulsanti)                      --%>
                <%-- ============================================================== --%>
                <slf:section name="<%=ScartoForm.RispostaAutorizzazioneSection.NAME%>" styleClass="importantContainer">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.REGISTRA_AUTORIZZAZIONE%>" colSpan="1" />
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.ANNULLA_PROPOSTA_SCARTO%>" colSpan="1" />
                    </sl:pulsantiera>
                    <sl:newLine />
                </slf:section>

                <%-- ============================================================== --%>
                <%-- SEZIONE DATI RISPOSTA AUTORIZZAZIONE (display readonly)         --%>
                <%-- ============================================================== --%>
                <slf:section name="<%=ScartoForm.DatiRispostaAutSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.TI_AUTORIZZAZIONE%>" colSpan="2" />
                    <sl:newLine />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.CD_REGISTRO_RISP_AUT%>" colSpan="3" />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.AA_RISP_AUT%>" colSpan="3" />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.CD_RISP_AUT%>" colSpan="3" />
                    <sl:newLine />
                </slf:section>

                <%-- ============================================================== --%>
                <%-- SEZIONE PROVVEDIMENTO DI SCARTO (display readonly)              --%>
                <%-- ============================================================== --%>
                <slf:section name="<%=ScartoForm.ProvvedimentoScartoDettaglioSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.CD_REGISTRO_PROVV_SCARTO%>" colSpan="3" />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.AA_PROVV_SCARTO%>" colSpan="3" />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.CD_PROVV_SCARTO%>" colSpan="3" />
                    <sl:newLine />
                    <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.DS_FIRMATO_DA%>" colSpan="3" />
                    <sl:newLine />
                </slf:section>
                
                 <%-- ============================================================== --%>
                <%-- SEZIONE REVISIONE post-autorizzazione parziale (pulsante)       --%>
                <%-- ============================================================== --%>
                <slf:section name="<%=ScartoForm.RevisioneSection.NAME%>" styleClass="importantContainer">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=ScartoForm.CreazionePropScartoVers.COMPLETA_REVISIONE_PROPOSTA_SCARTO%>" colSpan="1" />
                    </sl:pulsantiera>
                    <sl:newLine />
                </slf:section>

                </c:if>
            </slf:fieldSet>
            
            <sl:newLine skipLine="true"/>
            <%-- MOSTRA I TABS DI RICERCA SOLO SE LA PROPOSTA E' STATA SALVATA --%>
            <c:if test="${isPropostaSalvata}">
            
                <%-- ============================================================== --%>
                <%-- TAB 1: UNITA' DOCUMENTARIE (UD)                                --%>
                <%-- ============================================================== --%>
                <slf:tab  name="<%= ScartoForm.CreazionePropScartoTabs.NAME%>" tabElement="TabUnitaDoc">
                    <slf:fieldSet borderHidden="false">
                        <slf:section name="<%=ScartoForm.FiltriRicercaUdPropostaScartoSection.NAME%>" styleClass="importantContainer">
                            <%-- Registro e Anni --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.ID_REGISTRO_UNITA_DOC%>" colSpan="1" /> <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.ANNO%>" colSpan="1" /> 
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.ANNO_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.ANNO_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Numeri UD --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.NUMERO_UD%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.NUMERO_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.NUMERO_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Date UD --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.DT_UD%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.DT_UD_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.DT_UD_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Oggetto e Tipologia --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.OGGETTO_UD%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.ID_TIPO_UNITA_DOC%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.CLASSIFICA%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Tempi --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.TEMPO_CONSERVAZIONE%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.FL_ILLIMITATO%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.FL_TEMPO_SUPERATO%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Inclusioni --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.FL_INCLUDI_FASCICOLI%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.FL_INCLUDI_SERIE%>" colSpan="1" />
                            <sl:newLine skipLine="true"/>

                            <sl:pulsantiera>
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.RICERCA_UD%>" colSpan="1" />
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.PULISCI_UD%>" colSpan="1" />
                            </sl:pulsantiera>

                            <%-- BLOCCO INVISIBILE PER I COMANDI JAVASCRIPT E GLI STATI --%>
                            <div style="display: none;">
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.HIDDEN_TIPOLOGIA_CLICCATA%>" colSpan="1" />
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.HIDDEN_COLONNA_CLICCATA%>" colSpan="1" />
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.HIDDEN_FORZA_AGGIUNTA%>" colSpan="1" />
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.HIDDEN_UD_DA_AGGIUNGERE%>" colSpan="1" />
                                <sl:pulsantiera>
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdPropScarto.CARICA_LISTA_UD%>" colSpan="1" />
                                </sl:pulsantiera>
                            </div>
                        </slf:section>                        

                        <%-- Risultati riepilogativi UD --%>
                        <slf:section name="<%=ScartoForm.ResultTotSection.NAME%>" styleClass="importantContainer">
                            <slf:fieldSet borderHidden="true">
                                <slf:lblField name="<%=ScartoForm.RiepilogoUdPropScarto.TOTALE_UD%>" colSpan="1" />
                                <sl:newLine />
                                <slf:lblField name="<%=ScartoForm.RiepilogoUdPropScarto.ANNI_RIF%>" colSpan="1" />
                                <sl:newLine skipLine="true"/>
                            </slf:fieldSet>

                            <slf:listNavBar name="<%= ScartoForm.ReportUdPropScartoList.NAME%>" pageSizeRelated="true"/>
                            <slf:list name="<%= ScartoForm.ReportUdPropScartoList.NAME%>" />
                            <slf:listNavBar name="<%= ScartoForm.ReportUdPropScartoList.NAME%>" />
                        </slf:section>
                        
                        <c:choose>
                            <c:when test="${!(sessionScope['###_FORM_CONTAINER']['propScartoVersList'].status eq 'view') }">
                                <slf:tab  name="<%= ScartoForm.UdPropScartoTabs.NAME%>" tabElement="TabPopolaPropostaUnitaDoc">
                                    <slf:fieldSet borderHidden="false">
                                        <%-- Elenco UD --%>
                                        <slf:section name="<%=ScartoForm.ListaUdDaSelSection.NAME%>" styleClass="importantContainer">
                                            <slf:listNavBar name="<%= ScartoForm.UdDaSelPropScartoList.NAME%>" pageSizeRelated="true"/>
                                            <slf:list name="<%= ScartoForm.UdDaSelPropScartoList.NAME%>" />
                                            <slf:listNavBar name="<%= ScartoForm.UdDaSelPropScartoList.NAME%>" />
                                        </slf:section>
                                        <sl:pulsantiera>
                                            <slf:lblField name="<%=ScartoForm.SelectButtonList.SELECT_UD%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.SelectButtonList.SELECT_ALL_UD%>" colSpan="1" />
                                        </sl:pulsantiera>
                                        <sl:newLine skipLine="true"/>
                                        <slf:section name="<%=ScartoForm.ListaUdSelezionateSection.NAME%>" styleClass="importantContainer"> 
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ID_REGISTRO_UNITA_DOC_SEL%>" colSpan="1" />
                                            <sl:newLine />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ANNO_SEL%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ANNO_DA_SEL%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ANNO_A_SEL%>" colSpan="1" />
                                            <sl:newLine />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.NUMERO_UD_SEL%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.NUMERO_DA_SEL%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.NUMERO_A_SEL%>" colSpan="1" />
                                            <sl:newLine />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ID_TIPO_UNITA_DOC_SEL%>" colSpan="1" />                                            
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.DS_ALERT_SEL%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.FL_SCARTABILE_SEL%>" colSpan="1" />                                            
                                            <sl:newLine skipLine="true" />
                                            <sl:pulsantiera>
                                                <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.RICERCA_UD_SELEZIONATE%>" colSpan="1" />                                                
                                            </sl:pulsantiera>
                                            <sl:newLine skipLine="true" />
                                            <slf:listNavBar name="<%= ScartoForm.UdSelezionatePropScartoList.NAME%>" pageSizeRelated="true"/>
                                            <slf:list name="<%= ScartoForm.UdSelezionatePropScartoList.NAME%>" />
                                            <slf:listNavBar  name="<%= ScartoForm.UdSelezionatePropScartoList.NAME%>" />
                                        </slf:section>
                                        <sl:pulsantiera>
                                            <slf:lblField name="<%=ScartoForm.SelectButtonList.DESELECT_UD%>" colSpan="1" />
                                            <slf:lblField name="<%=ScartoForm.SelectButtonList.DESELECT_ALL_UD%>" colSpan="1" />
                                        </sl:pulsantiera>
                                    </slf:fieldSet>    
                                </slf:tab>
                                <slf:tab  name="<%= ScartoForm.UdPropScartoTabs.NAME%>" tabElement="TabConsultaUdAltreProposte">
                                    <slf:fieldSet borderHidden="false">
                                        <slf:listNavBar name="<%= ScartoForm.UdInAltrePropScartoList.NAME%>" pageSizeRelated="true"/>
                                        <slf:list name="<%= ScartoForm.UdInAltrePropScartoList.NAME%>" />
                                        <slf:listNavBar  name="<%= ScartoForm.UdInAltrePropScartoList.NAME%>" />
                                    </slf:fieldSet>
                                </slf:tab>
                            </c:when>
                            <c:otherwise>
                                <slf:section name="<%=ScartoForm.ListaUdSelezionateSection.NAME%>" styleClass="importantContainer">
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ID_REGISTRO_UNITA_DOC_SEL%>" colSpan="1" />
                                    <sl:newLine />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ANNO_SEL%>" colSpan="1" />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ANNO_DA_SEL%>" colSpan="1" />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ANNO_A_SEL%>" colSpan="1" />
                                    <sl:newLine />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.NUMERO_UD_SEL%>" colSpan="1" />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.NUMERO_DA_SEL%>" colSpan="1" />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.NUMERO_A_SEL%>" colSpan="1" />
                                    <sl:newLine />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.ID_TIPO_UNITA_DOC_SEL%>" colSpan="1" />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.DS_ALERT_SEL%>" colSpan="1" />
                                    <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.FL_SCARTABILE_SEL%>" colSpan="1" />
                                    <sl:newLine skipLine="true" />
                                    <sl:pulsantiera>
                                        <slf:lblField name="<%=ScartoForm.FiltriRicercaUdSelezionate.RICERCA_UD_SELEZIONATE%>" colSpan="1" />
                                    </sl:pulsantiera>
                                    <sl:newLine skipLine="true" />
                                    <slf:listNavBar name="<%= ScartoForm.UdSelezionatePropScartoList.NAME%>" pageSizeRelated="true"/>
                                    <slf:list name="<%= ScartoForm.UdSelezionatePropScartoList.NAME%>" />
                                    <slf:listNavBar  name="<%= ScartoForm.UdSelezionatePropScartoList.NAME%>" />
                                </slf:section>
                            </c:otherwise>
                        </c:choose>    
                    </slf:fieldSet>
                </slf:tab>
            
                <%-- ============================================================== --%>
                <%-- TAB 2: FASCICOLI                                               --%>
                <%-- ============================================================== --%>     
                <slf:tab  name="<%= ScartoForm.CreazionePropScartoTabs.NAME%>" tabElement="TabFascicoli">                    
                    <slf:fieldSet borderHidden="false">
                        <slf:section name="<%=ScartoForm.FiltriRicercaFascicoliPropostaScartoSection.NAME%>" styleClass="importantContainer">
                            <%-- Anni --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.ANNO%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.ANNO_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.ANNO_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Numeri --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.NUMERO_FASCICOLO%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.NUMERO_FASCICOLO_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.NUMERO_FASCICOLO_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Oggetto e Tipo --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.OGGETTO_FASCICOLO%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.TIPO_FASCICOLO%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Date --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.DT_APERTURA_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.DT_APERTURA_A%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.DT_CHIUSURA_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.DT_CHIUSURA_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Classifica e Tempi --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.CLASSIFICA%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.TEMPO_CONSERVAZIONE%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.FL_ILLIMITATO%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.FL_TEMPO_SUPERATO%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Sottofascicolo --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.ANNO_APPARTENENZA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.NUMERO_APPARTENENZA%>" colSpan="1" />
                            <sl:newLine skipLine="true"/>
                            <sl:pulsantiera>
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.RICERCA_FASC%>" colSpan="1" />
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaFascPropScarto.PULISCI_FASC%>" colSpan="1" />
                            </sl:pulsantiera>
                        </slf:section>

                        <sl:newLine />

                        <%-- Risultati Fascicoli --%>
                        <slf:section name="<%=ScartoForm.ResultTotSection.NAME%>" styleClass="importantContainer">
                        <slf:fieldSet borderHidden="true">
                            <slf:lblField name="<%=ScartoForm.RiepilogoFascPropScarto.TOTALE_FASC%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.RiepilogoFascPropScarto.ANNI_RIF%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.RiepilogoFascPropScarto.UD_CONFLITTO%>" colSpan="1" />
                            <sl:newLine skipLine="true"/>
                        </slf:fieldSet>
                        <slf:listNavBar name="<%= ScartoForm.ReportFascPropScartoList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= ScartoForm.ReportFascPropScartoList.NAME%>" />
                        <slf:listNavBar name="<%= ScartoForm.ReportFascPropScartoList.NAME%>" />
                        </slf:section>

                    </slf:fieldSet>
                </slf:tab>

                <%-- ============================================================== --%>
                <%-- TAB 3: SERIE                                                   --%>
                <%-- ============================================================== --%>
                <slf:tab  name="<%= ScartoForm.CreazionePropScartoTabs.NAME%>" tabElement="TabSerie">
                    <slf:fieldSet borderHidden="false">
                        <slf:section name="<%=ScartoForm.FiltriRicercaFascicoliPropostaScartoSection.NAME%>" styleClass="importantContainer">
                            <%-- Anni --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.ANNO_SERIE%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.ANNO_DA%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.ANNO_A%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Tipo e Codice --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.TIPO_SERIE%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.CODICE_SERIE%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Tipologie Registro/UD --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.TIPOLOGIA_UD%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.TIPOLOGIA_REGISTRO%>" colSpan="1" />
                            <sl:newLine />
                            <%-- Tempi --%>
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.TEMPO_CONSERVAZIONE%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.FL_ILLIMITATO%>" colSpan="1" />
                            <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.FL_TEMPO_SUPERATO%>" colSpan="1" />
                            <sl:newLine skipLine="true"/>
                            <sl:pulsantiera>
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.RICERCA_SERIE%>" colSpan="1" />
                                <slf:lblField name="<%=ScartoForm.FiltriRicercaSeriePropScarto.PULISCI_SERIE%>" colSpan="1" />
                            </sl:pulsantiera>
                        </slf:section>
                    
                        <sl:newLine />

                        <%-- Risultati Serie --%>
                        <slf:section name="<%=ScartoForm.ResultTotSection.NAME%>" styleClass="importantContainer">
                        <slf:fieldSet borderHidden="true">
                            <slf:lblField name="<%=ScartoForm.RiepilogoSeriePropScarto.TOTALE_SERIE%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.RiepilogoSeriePropScarto.ANNI_RIF%>" colSpan="1" />
                            <sl:newLine />
                            <slf:lblField name="<%=ScartoForm.RiepilogoSeriePropScarto.UD_CONFLITTO%>" colSpan="1" />
                            <sl:newLine skipLine="true"/>
                        </slf:fieldSet>

                        <slf:listNavBar name="<%= ScartoForm.ReportSeriePropScartoList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= ScartoForm.ReportSeriePropScartoList.NAME%>" />
                        <slf:listNavBar name="<%= ScartoForm.ReportSeriePropScartoList.NAME%>" />
                        </slf:section>
                    
                    </slf:fieldSet>
                </slf:tab>
                
            </c:if>
                
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>