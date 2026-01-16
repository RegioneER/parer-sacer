<!--
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
-->

<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.NAME%>" hideBackButton="${!((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField  name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.TI_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.CD_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.DS_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.NT_RICH_ANNUL_VERS%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.FL_FORZA_ANNUL%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />
                <slf:lblField  name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetailAnnulUd.TI_ANNULLAMENTO%>" width="w100" controlWidth="w30" labelWidth="w20" />
                <sl:newLine />                
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
