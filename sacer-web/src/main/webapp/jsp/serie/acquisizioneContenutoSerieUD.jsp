<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SerieUDForm.CreazioneSerie.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <sl:contentTitle title="<%=SerieUDForm.CreazioneSerie.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=SerieUDForm.CreazioneSerie.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <div id="Desc_Bl_file_input_serie" class="containerRight w50">
                    <p>Il file deve essere composto da campi separati da virgola, di cui:</p>
                    <p>la prima riga (intestazione) deve contenere i nomi dei campi</p>
                    <p>le righe successive devono contenere i record da considerare nel calcolo.</p>
                    <p>In caso di campi alfanumerici i cui valori possono contenere virgole, &egrave; necessario delimitare il campo con doppi apici (")</p>
                </div>
                <slf:section name="<%=SerieUDForm.FileSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.CD_DOC_FILE_INPUT_VER_SERIE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.DS_DOC_FILE_INPUT_VER_SERIE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.FL_FORNITO_ENTE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                    <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.BL_FILE_INPUT_SERIE%>" width="w100" controlWidth="w50" labelWidth="w40" />
                    <sl:newLine />
                </slf:section>
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField  name="<%=SerieUDForm.CreazioneSerie.ACQUISISCI_CONTENUTO_UD%>"  width="w50" />
            </sl:pulsantiera> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
