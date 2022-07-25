<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.SerieDaValidareList.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value="/js/custom/customPollSerie.js" />" ></script>
        <script type="text/javascript">
            $(document).ready(function () {
                $('div[id^="Fl_err_contenuto_eff"] > img[src$="checkbox-on.png"]').attr("src", "./img/alternative/checkbox-on.png");
                $('div[id^="Fl_err_contenuto_eff"] > img[src$="checkbox-off.png"]').attr("src", "./img/alternative/checkbox-off.png");

                poll();
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <div class="serieMessageBox "></div>
            <sl:contentTitle title="SERIE DI UNIT&Agrave; DOCUMENTARIE DA VALIDARE"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <slf:lblField colSpan="4" name="<%=SerieUDForm.FiltriSerieDaFirmare.ID_AMBIENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=SerieUDForm.FiltriSerieDaFirmare.ID_ENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=SerieUDForm.FiltriSerieDaFirmare.ID_STRUT%>" />
            </slf:fieldSet>

            <sl:pulsantiera>
                <slf:lblField name="<%=SerieUDForm.FiltriSerieDaFirmare.RICERCA_SERIE_DA_VALIDARE_BUTTON%>" width="w50" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= SerieUDForm.SerieDaValidareList.NAME%>" pageSizeRelated="true"/>
            <slf:selectList name="<%= SerieUDForm.SerieDaValidareList.NAME%>" addList="true"/>
            <slf:listNavBar  name="<%= SerieUDForm.SerieDaValidareList.NAME%>" />

            <sl:pulsantiera >
                <slf:buttonList name="<%= SerieUDForm.ListaSerieDaValidareButtonList.NAME%>" >
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaValidareButtonList.SELECT_ALL_SERIE_DA_VALIDARE_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaValidareButtonList.DESELECT_ALL_SERIE_DA_VALIDARE_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaValidareButtonList.VALIDA_SERIE%>" colSpan="2"/>
                </slf:buttonList>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <slf:section name="<%= SerieUDForm.SerieSelValidazioneSection.NAME%>" styleClass="importantContainer">
                <slf:listNavBar name="<%= SerieUDForm.SerieSelezionateDaValidareList.NAME%>" pageSizeRelated="true"/>
                <slf:selectList name="<%= SerieUDForm.SerieSelezionateDaValidareList.NAME%>" abbrLongList="" addList="false"/>
                <slf:listNavBar  name="<%= SerieUDForm.SerieSelezionateDaValidareList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>