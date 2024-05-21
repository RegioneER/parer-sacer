<%@ page import="it.eng.parer.slite.gen.form.AnnulVersForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:choose>
                <c:when test='<%="FASCICOLI".equals((String) session.getAttribute("tiRichAnnulVers"))%>'>
                    <sl:contentTitle title='<%=AnnulVersForm.FiltriRicercaRichAnnullVers.DESCRIPTION + " fascicoli"%>'/>
                </c:when>
                <c:otherwise>
                    <sl:contentTitle title='<%=AnnulVersForm.FiltriRicercaRichAnnullVers.DESCRIPTION + " unità documentarie"%>'/>
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.CD_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.DS_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.NT_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.TI_STATO_RICH_ANNUL_VERS_COR%>" colSpan="2" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.DT_CREAZIONE_RICH_ANNUL_VERS_DA%>" width="w50" controlWidth="w20" labelWidth="w40"/>
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.DT_CREAZIONE_RICH_ANNUL_VERS_A%>" width="w50" controlWidth="w20" labelWidth="w40" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.FL_IMMEDIATA%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.FL_ANNUL_PING%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.FL_NON_ANNUL%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.TI_ANNULLAMENTO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                <slf:section name="<%=AnnulVersForm.UdSection.NAME%>" styleClass="noborder w100">
                    <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.CD_REGISTRO_KEY_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.AA_KEY_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.CD_KEY_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=AnnulVersForm.FascSection.NAME%>" styleClass="noborder w100">
                    <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.AA_FASCICOLO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                    <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.CD_KEY_FASCICOLO%>" width="w100" controlWidth="w30" labelWidth="w20" /><sl:newLine />
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.RICERCA_RICH_ANNUL_VERS%>"  width="w25" />
                <slf:lblField name="<%=AnnulVersForm.FiltriRicercaRichAnnullVers.CREA_RICH_ANNUL_IMMEDIATA%>"  width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:section name="<%=AnnulVersForm.ListaRichiesteSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= AnnulVersForm.RichAnnulVersList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= AnnulVersForm.RichAnnulVersList.NAME%>" />
                <slf:listNavBar  name="<%= AnnulVersForm.RichAnnulVersList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>