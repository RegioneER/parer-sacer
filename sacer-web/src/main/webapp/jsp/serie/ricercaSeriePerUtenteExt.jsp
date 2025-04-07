<%@ page import="it.eng.parer.slite.gen.form.SerieUdPerUtentiExtForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="Ricerca serie di unità documentarie"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.DS_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.AA_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.AA_SERIE_DA%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.AA_SERIE_A%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.NM_TIPO_UNITA_DOC%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.CD_REGISTRO_UNITA_DOC%>" width="w100" controlWidth="w20" labelWidth="w20" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=SerieUdPerUtentiExtForm.FiltriRicercaSerie.RICERCA_SERIE%>"  width="w50" />
            </sl:pulsantiera> 
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= SerieUdPerUtentiExtForm.SerieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= SerieUdPerUtentiExtForm.SerieList.NAME%>" />
            <slf:listNavBar  name="<%= SerieUdPerUtentiExtForm.SerieList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>