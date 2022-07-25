<%@ page import="it.eng.parer.slite.gen.form.RestituzioneArchivioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=RestituzioneArchivioForm.CambioStatoRichiesta.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {

                $('.erroriItemBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                            window.location = "RestituzioneArchivio.html?operation=elencoOnClick";
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
            <%--
            <c:choose>
                <c:when test="${!empty requestScope.itemInErrore}">
                    <div class="messages erroriItemBox ">
                        <ul>
                            <li class="message warning ">ATTENZIONE: la richiesta contiene estrazioni in errore. Procedere con l'annullamento?</li>
                        </ul>
                    </div>
                </c:when>
                <c:when test="${!empty requestScope.tuttiItemEstratti}">
                    <div class="messages erroriItemBox ">
                        <ul>
                            <li class="message warning ">ATTENZIONE: la richiesta non contiene errori. Procedere con l'annullamento?</li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise></c:otherwise>
            </c:choose>
            --%>
            <sl:contentTitle title="<%=RestituzioneArchivioForm.CambioStatoRichiesta.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= RestituzioneArchivioForm.RichRestArchDetail.NAME%>" hideOperationButton="true"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=RestituzioneArchivioForm.InfoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.NM_ENTE_CONVENZ%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.NM_ENTE_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.RichRestArchDetail.TI_STATO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=RestituzioneArchivioForm.CambioStatoSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=RestituzioneArchivioForm.CambioStatoRichiesta.TI_STATO_RICH_REST_ARCH%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=RestituzioneArchivioForm.CambioStatoRichiesta.DS_NOTA_RICH_REST_ARCH%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=RestituzioneArchivioForm.CambioStatoRichiesta.CONFERMA_CAMBIO_STATO%>" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>