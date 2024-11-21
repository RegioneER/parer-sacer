<%@ page import="it.eng.parer.slite.gen.form.RestituzioneArchivioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.DESCRIPTION%>" >
    </sl:head>
    <script type="text/javascript" src="<c:url value='/js/sips/customAssociazioniScaduteRestArchMessageBox.js'/>" ></script>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.customBoxAssociazioniScaduteRestArch}">
                <div class="messages customBoxAssociazioniScaduteRestArch ">
                    <ul>
                        <li class="message warning ">Attenzione:</li>
                    </ul>
                    <c:if test="${!empty requestScope.associazioniScadute}">            
                        Le seguenti strutture presentano un'associazione non valida con l'ente convenzionato:                        
                        <ul>
                            <c:forTokens items = "${requestScope.associazioniScadute}" delims = "," var = "associazioniScadute">
                                <li><c:out value = "${associazioniScadute}"/></li>
                            </c:forTokens>
                        </ul>
                    </c:if>
                    <br>
                    <br>
                    <c:if test="${!empty requestScope.associazioniScaduteConFuture}">            
                        Le seguenti strutture presentano un'associazione non valida con l'ente convenzionato e associazioni future con altri enti convenzionati:
                        <ul>
                            <%--<c:forTokens items = "${requestScope.associazioniScaduteConFuture}" delims = "," var = "associazioniScaduteConFuture">
                                <li><c:out value = "${associazioniScaduteConFuture}"/></li>
                            </c:forTokens>--%>
                        <c:forEach items="${requestScope.associazioniScaduteConFuture}" var="elem">
                            ${elem}<br>
                        </c:forEach>
                        </ul>
                    </c:if>
                    Si desidera proseguire con il salvataggio?
                </div>
            </c:if>
            <c:if test="${!empty requestScope.creaRichRestArchBox}">
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