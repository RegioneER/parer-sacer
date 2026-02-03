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
    <sl:head  title="Ricerca cancellazioni" >
        <script type="text/javascript" src="<c:url value="/js/sips/customDataMartMessageBox.js"/>"></script>        
        <style>
            .progress { display: flex; height: 1rem; overflow: hidden; font-size: .75rem; background-color: #e9ecef; border-radius: .25rem; }
            .progress-bar { display: flex; flex-direction: column; justify-content: center; color: #fff; text-align: center; white-space: nowrap; background-color: #0d6efd; transition: width .6s ease; }
            .progress-bar-striped { background-image: linear-gradient(45deg, rgba(255, 255, 255, .15) 25%, transparent 25%, transparent 50%, rgba(255, 255, 255, .15) 50%, rgba(255, 255, 255, .15) 75%, transparent 75%, transparent); background-size: 1rem 1rem; }
            .progress-bar-animated { animation: progress-bar-stripes 1s linear infinite; }
            @keyframes progress-bar-stripes { 0% { background-position-x: 1rem; } }
            .riga-selezionata td { background-color: yellow !important; }
            /*.riga-completata td {background-color: #f0f0f0; color: #888; }*/
            #RichiesteDataMartList tbody tr, #NumUdDataMartList tbody tr { cursor: pointer; }
            #RichiesteDataMartList tbody tr:not(.riga-selezionata):hover, #NumUdDataMartList tbody tr:not(.riga-selezionata):hover { background-color: #f0f0f0; }
            #RichiesteDataMartList tbody tr td:nth-child(8), #RichiesteDataMartList thead tr th:nth-child(8),
            #NumUdDataMartList tbody tr td:nth-child(5), #NumUdDataMartList thead tr th:nth-child(5), #NumUdDataMartList tbody tr td:nth-child(6), #NumUdDataMartList thead tr th:nth-child(6) { text-align: right; }
        </style>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="RICERCA CANCELLAZIONI"/>
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet  borderHidden="false">
                <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.TI_MOT_CANCELLAZIONE%>" />
                <sl:newLine />
                <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.TI_STATO_RICHIESTA%>"  />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.ID_ENTE%>"  />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.ID_STRUT%>"  />
                <sl:newLine />
                <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.DT_CREAZIONE_DA%>"  />
                <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.DT_CREAZIONE_A%>"  />
                <sl:newLine />
                <slf:section name="<%=UnitaDocumentarieForm.ChiaveUdDataMartSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.AA_KEY_UNITA_DOC%>"  />
                    <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.CD_KEY_UNITA_DOC%>"  />         
                </slf:section>                
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <slf:lblField colSpan="1" name="<%=UnitaDocumentarieForm.FiltriRicercaDataMart.RICERCA_DATA_MART%>"  />                    
            <sl:newLine skipLine="true"/>

            <slf:section name="<%=UnitaDocumentarieForm.RichiesteDataMartSection.NAME%>" styleClass="importantContainer">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.RichiesteDataMartList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.RichiesteDataMartList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.RichiesteDataMartList.NAME%>" />
            </slf:section>
            <sl:newLine skipLine="true"/>         
            
            <div style="display: flex; align-items: flex-start; gap: 20px;">
                <div style="flex: 1; min-width: 0">
                    <slf:section name="<%=UnitaDocumentarieForm.NumUdDataMartSection.NAME%>" styleClass="importantContainer">                        
                        <slf:list name="<%= UnitaDocumentarieForm.NumUdDataMartList.NAME%>" />                        
                        <sl:newLine skipLine="true"/>   
                        <slf:lblField colSpan="3" name="<%=UnitaDocumentarieForm.MicroservizioDataMartFields.CALL_MICROSERVIZIO_DATA_MART%>"  />                                
                        <slf:lblField colSpan="3" name="<%=UnitaDocumentarieForm.MicroservizioDataMartFields.RECUP_CANCELLAZIONE_LOGICA_DATA_MART%>"  />                                
                        <slf:lblField colSpan="3" name="<%=UnitaDocumentarieForm.MicroservizioDataMartFields.ESEGUI_CANCELLAZIONE_DATA_MART%>"  />  
                    </slf:section>
                </div>
                <div style="flex: 1; min-width: 0">
                    <c:if test="${idUdDelRichiestaPerPolling != null}">
                        <div id="monitoraggio-unificato" data-id-ud-del-richiesta="${idUdDelRichiestaPerPolling}" data-id-richiesta="${idRichiestaPerPolling}" style="border: 1px solid #ccc; padding: 15px; border-radius: 5px; background-color: #f9f9f9; display: none;">
                            <h4 id="monitoraggio-titolo">Monitoraggio</h4>
                            <p style="font-size: 0.9em;">Stato: <strong id="monitoraggio-stato-generale">${statoRichiestaSelezionata}</strong></p>
                            <div class="progress" style="height: 20px;"><div id="monitoraggio-progress-bar" class="progress-bar"></div></div>
                            <p id="monitoraggio-progress-text" style="text-align: center; font-size: 0.9em;">--</p>
                        </div>
                    </c:if>
                </div>
            </div>
            <sl:newLine skipLine="true"/>            
            
            <slf:section name="<%=UnitaDocumentarieForm.UdDataMartSection.NAME%>" styleClass="importantContainer">
                <slf:listNavBar name="<%= UnitaDocumentarieForm.UdDataMartList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= UnitaDocumentarieForm.UdDataMartList.NAME%>" />
                <slf:listNavBar  name="<%= UnitaDocumentarieForm.UdDataMartList.NAME%>" />
            </slf:section>

            <!-- IMPORTANTE!!! Aggiungiamo l'attributo 'name' a entrambi gli input nascosti.
                 Questo risolve l'errore 'match on undefined' in classes.js. che cerca gli elementi proprio per name -->
            <input type="hidden" id="selectedRigaIdR_val" name="selectedRigaIdR_val_name" value="<c:out value='${selectedRigaIdR}' default=''/>" />
            <input type="hidden" id="selectedRigaMotivoR_val" name="selectedRigaMotivoR_val_name" value="<c:out value='${selectedRigaMotivoR}' default=''/>" />
            <input type="hidden" id="selectedRigaStatoN_val" name="selectedRigaStatoN_val_name" value="<c:out value='${selectedRigaStatoN}' default=''/>" />
            <input type="hidden" id="selectedRigaIdForN_val" name="selectedRigaIdForN_val_name" value="<c:out value='${selectedRigaIdForN}' default=''/>" />
            <input type="hidden" id="isProcessoKafkaAttivo_val" name="isProcessoKafkaAttivo_val_name" value="<c:out value='${isProcessoKafkaAttivo}' default='false'/>" />
            <input type="hidden" id="statoRichiestaSelezionata_val" name="statoRichiestaSelezionata_val_name" value="<c:out value='${statoRichiestaSelezionata}' default=''/>" />
            <input type="hidden" id="statoInternoRichiesta_val" name="statoInternoRichiesta_val_name" value="<c:out value='${statoInternoRichiesta}' default=''/>" />
            <input type="hidden" id="selectedRigaIdStrut_val" name="selectedRigaIdStrut_val_name" value="<c:out value='${selectedRigaIdStrut}' default=''/>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
