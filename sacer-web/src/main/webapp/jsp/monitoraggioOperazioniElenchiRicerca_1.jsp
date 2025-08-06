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

<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm, it.eng.parer.slite.gen.form.ElenchiVersamentoForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista operazioni sugli elenchi di versamento" >       
    </sl:head>
    <sl:body>        
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA OPERAZIONI SUGLI ELENCHI DI VERSAMENTO"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (lastMenuEntry.contains("RicercaElenchiVersamento")) {%>       
            <slf:fieldBarDetailTag name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.NAME%>" hideOperationButton="true" hideBackButton="false"/>                 
            <% } else {%>
            <slf:fieldBarDetailTag name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.NAME%>" hideOperationButton="true" hideBackButton="true" />
            <% }%>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.ID_ELENCO_VERS%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.TM_OPER_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.ORE_TM_OPER_DA%>" name2="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.MINUTI_TM_OPER_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.TM_OPER_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.ORE_TM_OPER_A%>" name2="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.MINUTI_TM_OPER_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_CHIUSURA_ELENCO%>" colSpan="1" />    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_CREA_ELENCO%>" colSpan="1" />    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_CREA_INDICE_ELENCO%>" colSpan="1" />    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_DEF_NOTE_ELENCO_CHIUSO%>" colSpan="1" />    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_DEF_NOTE_INDICE_ELENCO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_ELIMINA_ELENCO%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_FIRMA_ELENCO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_FIRMA_IN_CORSO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_FIRMA_IN_CORSO_FALLITA%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_MOD_ELENCO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_RECUPERA_ELENCO_APERTO%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_RECUPERA_ELENCO_SCADUTO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_RIMUOVI_DOC_ELENCO%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_RIMUOVI_UD_ELENCO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_SET_ELENCO_APERTO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_SET_ELENCO_DA_CHIUDERE%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_START_CREA_ELENCO_INDICI_AIP%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_END_CREA_ELENCO_INDICI_AIP%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_FIRMA_ELENCO_INDICI_AIP%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_FIRMA_ELENCO_INDICI_AIP_IN_CORSO%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_FIRMA_ELENCO_INDICI_AIP_FALLITA%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_MARCA_ELENCO_INDICI_AIP%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.FL_OPER_MARCA_ELENCO_INDICI_AIP_FALLITA%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.TI_OUTPUT%>" colSpan="4" />    
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!-- piazzo i bottoni ricerca e pulisci -->
            <sl:pulsantiera>
                <input type="hidden" value="true" name="back" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.RICERCA_OPERAZIONI_ELENCHI_VERSAMENTO%>" width="w33" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniElenchiVersamento.PULISCI_OPERAZIONI_ELENCHI_VERSAMENTO%>" width="w33" />
            </sl:pulsantiera>                
            <sl:newLine skipLine="true"/>

            <c:if test="${!empty output}">
                <c:choose>		
                    <c:when test="${ output eq 'aggregato' }">
                        <!--  piazzo la lista con i risultati -->
                        <slf:container width="w50">
                            <slf:listNavBar name="<%= MonitoraggioForm.OutputAggregatoListElenchi.NAME%>" pageSizeRelated="true"/>
                            <slf:list name="<%= MonitoraggioForm.OutputAggregatoListElenchi.NAME%>" />
                        </slf:container>
                    </c:when>
                    <c:otherwise>
                        <!--  piazzo la lista con i risultati -->
                        <slf:listNavBar name="<%= MonitoraggioForm.OutputAnaliticoCronologicoListElenchi.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= MonitoraggioForm.OutputAnaliticoCronologicoListElenchi.NAME%>" />
                        <slf:listNavBar  name="<%= MonitoraggioForm.OutputAnaliticoCronologicoListElenchi.NAME%>" />
                    </c:otherwise>
                </c:choose>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
