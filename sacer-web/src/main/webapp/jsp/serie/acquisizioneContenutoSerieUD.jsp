<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

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
