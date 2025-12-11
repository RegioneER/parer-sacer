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
    <sl:head  title="Monitoraggio - Imposta data verifica versamenti falliti" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="IMPOSTA DATA VERIFICA VERSAMENTI FALLITI"/>
            <slf:fieldBarDetailTag name="<%=MonitoraggioForm.FiltriVerificaVersamenti.NAME%>" hideDeleteButton="true" hideUpdateButton="true"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.NM_AMBIENTE%>" colSpan="3" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.NM_ENTE%>" colSpan="3" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.NM_STRUT%>" colSpan="3" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.ID_STRUT%>" colSpan="3" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.DATA_REGISTRAZIONE%>" colSpan="1" labelWidth="w20" controlWidth="w70" />
                <slf:doubleLblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.ORE_REGISTRAZIONE%>" name2="<%=MonitoraggioForm.FiltriVerificaVersamenti.MINUTI_REGISTRAZIONE%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriVerificaVersamenti.CONFERMA_VERIFICA_AUTOMATICA%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
