<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.FiltriRicercaSerie.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="Ricerca serie di unità documentarie"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.ID_AMBIENTE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.ID_ENTE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.ID_STRUT%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.CD_COMPOSITO_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.DS_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.TI_STATO_COR_SERIE%>" colSpan="2" labelWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.TI_STATO_CONSERVAZIONE%>" colSpan="2" labelWidth="w20"/>
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.NM_TIPO_SERIE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.AA_SERIE_DA%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.AA_SERIE_A%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.DT_INIZIO_SERIE%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.DT_FINE_SERIE%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.TI_STATO_CONTENUTO_CALC%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_ERR_CONTENUTO_CALC%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.TI_STATO_CONTENUTO_ACQ%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_ERR_CONTENUTO_FILE%>" width="w25" controlWidth="w20" labelWidth="w80" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_ERR_CONTENUTO_ACQ%>" width="w25" controlWidth="w20" labelWidth="w80" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.TI_STATO_CONTENUTO_EFF%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_ERR_CONTENUTO_EFF%>" width="w25" controlWidth="w20" labelWidth="w80" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_ELAB_BLOCCATA%>" width="w25" controlWidth="w20" labelWidth="w80" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_ERR_VALIDAZIONE%>" width="w25" controlWidth="w20" labelWidth="w80" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.TI_CREA_STANDARD%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.ID_MODELLO_TIPO_SERIE%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_PRESENZA_CONSIST_ATTESA%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <slf:lblField name="<%=SerieUDForm.FiltriRicercaSerie.FL_DA_RIGENERA%>" width="w50" controlWidth="w20" labelWidth="w40" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=SerieUDForm.FiltriRicercaSerie.RICERCA_SERIE%>"  width="w50" />
            </sl:pulsantiera> 
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= SerieUDForm.SerieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= SerieUDForm.SerieList.NAME%>" />
            <slf:listNavBar  name="<%= SerieUDForm.SerieList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>