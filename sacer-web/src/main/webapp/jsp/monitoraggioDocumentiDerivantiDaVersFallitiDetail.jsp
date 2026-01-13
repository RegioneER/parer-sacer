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

<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Dettaglio unità doc. / documento non versato" >  
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
        <script src="<c:url value='/js/sips/customCheckBoxSesVerif.js' />" type="text/javascript" ></script>
        <script type='text/javascript'>
            $(document).ready(function() {
                checkVerificati = $('table.list td > input[name="Fl_sessione_err_verif"]:checked');
                checkNonRisolubili = $('table.list td > input[name="Fl_sessione_err_non_risolub"]:checked');
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="DETTAGLIO UNITA' DOC. / DOCUMENTO DERIVANTE DA VERSAMENTI FALLITI"/>

            <slf:listNavBarDetail name="<%= MonitoraggioForm.DocumentiDerivantiDaVersFallitiList.NAME%>"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.STRUTTURA%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.CHIAVE_UD%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.CD_KEY_DOC_VERS%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.FL_VERIF%>" colSpan="4"/>
                <sl:newLine />               
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.FL_NON_RISOLUB%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.DT_FIRST_SES_ERR%>" colSpan="4" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.DocumentiDerivantiDaVersFallitiDetail.DT_LAST_SES_ERR%>" colSpan="4" />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:newLine skipLine="true"/>
            <h2 class="titleFiltri">Lista sessioni</h2>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= MonitoraggioForm.SessioniList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= MonitoraggioForm.SessioniList.NAME%>" />
            <slf:listNavBar  name="<%= MonitoraggioForm.SessioniList.NAME%>" />

            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <slf:buttonList name="<%=MonitoraggioForm.SalvaVerificaButtonList.NAME%>">
                    <slf:lblField name="<%=MonitoraggioForm.SalvaVerificaButtonList.SALVA_VERIFICA_VERSAMENTO_DA_DOC_DER_VERS_FALLITI%>" colSpan="2"/>
                </slf:buttonList>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
