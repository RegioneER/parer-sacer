<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.DESCRIPTION%>">
        <script type="text/javascript">
            $(document).ready(function () {
                $('div[id^="Fl_presenza"] > img[src$="checkbox-off.png"]').attr("src", "./img/alternative/checkbox-on.png");
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.DS_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.NM_TIPO_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.AA_SERIE_DA%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.AA_SERIE_A%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.FL_PRESENZA_CONSIST_ATTESA%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.ID_TIPO_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.ID_REGISTRO_UNITA_DOC%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=SerieUDForm.FiltriComunicazioneConsistenzaSerieUD.RICERCA_CONSISTENZA_SERIE%>" width="w50" />
            </sl:pulsantiera> 
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= SerieUDForm.ConsistenzaSerieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= SerieUDForm.ConsistenzaSerieList.NAME%>" />
            <slf:listNavBar  name="<%= SerieUDForm.ConsistenzaSerieList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>