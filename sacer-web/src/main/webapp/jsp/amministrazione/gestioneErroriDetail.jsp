<%@ page import="it.eng.parer.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AmministrazioneForm.DettaglioGestioneErrore.DESCRIPTION%>" >
        <link href="<c:url value='/css/vendor/quill/quill.snow.css' />" rel="stylesheet" />
        <link href="<c:url value='/css/vendor/quill/quill.bubble.css' />" rel="stylesheet" />
        <link href="<c:url value='/css/vendor/quill/quill.core.css' />" rel="stylesheet" />
        <script type="text/javascript" src="<c:url value='/js/vendor/quill/quill.js' />"></script>
        <script type="text/javascript" src="<c:url value='/js/custom/gestioneErroriRichText.js' />"></script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title='<%=AmministrazioneForm.DettaglioGestioneErrore.DESCRIPTION %>' />
            <c:if test="${sessionScope['###_FORM_CONTAINER']['gestioneErroriList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= AmministrazioneForm.DettaglioGestioneErrore.NAME%>" hideBackButton="true" />
            </c:if>
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['gestioneErroriList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.GestioneErroriList.NAME%>" />
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.ID_ERR_SACER%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.CD_CLASSE_ERR_SACER%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.CD_ERR%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.DS_ERR%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.DS_ERR_FILTRO%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.TI_ERR_SACER%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.SOTTOCLASSE%>" colSpan= "2"/>
                <sl:newLine />
                <c:choose>
                    <c:when test="${sessionScope['###_FORM_CONTAINER']['dettaglioGestioneErrore'].status eq 'view'}">
                        <div class="slLabel wlbl">Casistica:</div>
                        <div class="slText w60 ge-quill-preview">
                            <c:out value="${sessionScope['###_FORM_CONTAINER']['dettaglioGestioneErrore']['casistica'].value}" escapeXml="false"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.CASISTICA%>" colSpan= "2"/>
                    </c:otherwise>
                </c:choose>
                <sl:newLine />
                <c:choose>
                    <c:when test="${sessionScope['###_FORM_CONTAINER']['dettaglioGestioneErrore'].status eq 'view'}">
                        <div class="slLabel wlbl">Soluzione suggerita:</div>
                        <div class="slText w60 ge-quill-preview">
                            <c:out value="${sessionScope['###_FORM_CONTAINER']['dettaglioGestioneErrore']['soluzione_sugg'].value}" escapeXml="false"/>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.SOLUZIONE_SUGG%>" colSpan= "2"/>
                    </c:otherwise>
                </c:choose>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.VERS_INIZIO_VAL%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.VERS_FINE_VAL%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.DEPRECATO%>" colSpan= "2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmministrazioneForm.DettaglioGestioneErrore.PUBBLICO%>" colSpan= "2"/>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>