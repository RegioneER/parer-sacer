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

<%@ page import="it.eng.parer.slite.gen.form.ScartoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ScartoForm.CreazioneRichScartoVers.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />
            <sl:contentTitle title="<%=ScartoForm.CreazioneRichScartoVers.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=ScartoForm.CreazioneRichScartoVers.NAME%>" hideBackButton="false" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField  name="<%=ScartoForm.CreazioneRichScartoVers.CD_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=ScartoForm.CreazioneRichScartoVers.DS_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=ScartoForm.CreazioneRichScartoVers.NT_RICH_SCARTO_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />                                
                <slf:lblField  name="<%=ScartoForm.CreazioneRichScartoVers.BL_FILE%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />                
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField  name="<%=ScartoForm.CreazioneRichScartoVers.CREA_RICH_SCARTO_VERS%>"  width="w50" />
            </sl:pulsantiera> 
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
