<%@ page import="it.eng.parer.slite.gen.form.RestituzioneArchivioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('.creaRichRestArchBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            var priorita = $(".creaRichRestArchBox #Priorita").val();
                            var navTable = $("input[name='mainNavTable']").val();
                            window.location = "RestituzioneArchivio.html?operation=creaRichRestArch&Priorita=" + priorita + "&mainNavTable=" + navTable;
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.creaRichRestArchBox}">
                <div class="messages creaRichRestArchBox ">
                    <ul>
                        <li class="message info ">
                            <p>Inserisci la priorità della richiesta di restituzione archivio</p>
                        <sl:newLine />
                        <div class="containerLeft w100">
                            <label class="slLabel w30" for="Priorita">Priorità</label>
                            <select id="Priorita" class="w30" name="Priorita">
                                <option value="1" selected>1</option>
                                <option value="2">2</option>                                
                                <option value="3">3</option>
                                <option value="4">4</option>
                                <option value="5">5</option>                    
                                <option value="6">6</option>
                                <option value="7">7</option>                            
                                <option value="8">8</option>
                                <option value="9">9</option>
                                <option value="10">10</option>
                            </select>                                
                        </div>
                        </li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.TI_STATO_RICH_REST_ARCH_COR%>" colSpan="2" controlWidth="w30" labelWidth="w20" /><sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.RICERCA_RICH_REST_ARCH%>"  width="w25" />
                <slf:lblField name="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.CREA_RICH_REST_ARCH_BTN%>"  width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:section name="<%=RestituzioneArchivioForm.ListaRichiesteSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= RestituzioneArchivioForm.RichRestArchList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= RestituzioneArchivioForm.RichRestArchList.NAME%>" />
                <slf:listNavBar  name="<%= RestituzioneArchivioForm.RichRestArchList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>