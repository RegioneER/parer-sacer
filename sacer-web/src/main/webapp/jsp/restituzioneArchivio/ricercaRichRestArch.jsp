<%@ page import="it.eng.parer.slite.gen.form.RestituzioneArchivioForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=RestituzioneArchivioForm.FiltriRicercaRichRestArch.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
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