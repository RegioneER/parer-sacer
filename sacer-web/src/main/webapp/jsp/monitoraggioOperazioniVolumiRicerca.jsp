<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm, it.eng.parer.slite.gen.form.VolumiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista operazioni sui volumi" >       
    </sl:head>
    <sl:body>        
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="LISTA OPERAZIONI SUI VOLUMI"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (lastMenuEntry.contains("GestioneVolumi")) {%>       
            <slf:fieldBarDetailTag name="<%=MonitoraggioForm.FiltriOperazioniVolumi.NAME%>" hideOperationButton="true" hideBackButton="false"/>                 
            <% } else {%>
            <slf:fieldBarDetailTag name="<%=MonitoraggioForm.FiltriOperazioniVolumi.NAME%>" hideOperationButton="true" hideBackButton="true"/>
            <% }%>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.ID_VOLUME_CONSERV%>" colSpan="2" />
                <sl:newLine />
                <!--<div class="containerLeft w50">
                    <label class="slLabel w40" for="Dt_oper_da">Data registrazione da:&nbsp;</label>
                    <input id="Dt_oper_da" class="slText w20 date hasDatepicker" type="text" name="Dt_oper_da" value="" onclick="Calendar.show(this, '%d/%m/%Y', true)"
                           onfocus="Calendar.show(this, '%d/%m/%Y', true)" onblur="Calendar.hide()" />                
                </div>
                -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.DT_OPER_DA%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.ORE_DT_OPER_DA%>" name2="<%=MonitoraggioForm.FiltriOperazioniVolumi.MINUTI_DT_OPER_DA%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.DT_OPER_A%>" colSpan="1" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.ORE_DT_OPER_A%>" name2="<%=MonitoraggioForm.FiltriOperazioniVolumi.MINUTI_DT_OPER_A%>" controlWidth="w15" controlWidth2="w15" colSpan="1" />

                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_CREA_VOLUME%>" colSpan="1" />    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_RECUPERA_VOLUME_APERTO%>" colSpan="1" />    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_AGGIUNGI_DOC_VOLUME%>" colSpan="1" />    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_RECUPERA_VOLUME_SCADUTO%>" colSpan="1" />    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_SET_VOLUME_DA_CHIUDERE%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_SET_VOLUME_APERTO%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_INIZIO_CREA_INDICE%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_RECUPERA_VOLUME_IN_ERRORE%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_CREA_INDICE_VOLUME%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_MARCA_INDICE_VOLUME%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_SET_VOLUME_IN_ERRORE%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_INIZIO_VERIF_FIRME%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_CHIUSURA_VOLUME%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_ERR_VERIF_FIRME%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_RIMUOVI_DOC_VOLUME%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_ELIMINA_VOLUME%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_MODIFICA_VOLUME%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_FIRMA_NO_MARCA_VOLUME%>" colSpan="1"/>    
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.FL_OPER_FIRMA_VOLUME%>" colSpan="1"/>    
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.TI_OUTPUT%>" colSpan="4" />    
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!-- piazzo i bottoni ricerca e pulisci -->
            <sl:pulsantiera>
                <input type="hidden" value="true" name="back" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.RICERCA_OPERAZIONI_VOLUMI%>" width="w33" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriOperazioniVolumi.PULISCI_OPERAZIONI_VOLUMI%>" width="w33" />
            </sl:pulsantiera>                
            <sl:newLine skipLine="true"/>

            <c:if test="${!empty output}">
                <c:choose>		
                    <c:when test="${ output eq 'aggregato' }">
                        <!--  piazzo la lista con i risultati -->
                        <slf:container width="w50">
                            <slf:listNavBar name="<%= MonitoraggioForm.OutputAggregatoList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.OutputAggregatoList.NAME%>" />
                        </slf:container>
                    </c:when>
                    <c:otherwise>
                        <!--  piazzo la lista con i risultati -->
                        <slf:listNavBar name="<%= MonitoraggioForm.OutputAnaliticoCronologicoList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.OutputAnaliticoCronologicoList.NAME%>" />
                        <slf:listNavBar  name="<%= MonitoraggioForm.OutputAnaliticoCronologicoList.NAME%>" />
                    </c:otherwise>
                </c:choose>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>