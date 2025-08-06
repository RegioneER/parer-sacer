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
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="<%=UnitaDocumentarieForm.NotaDetail.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=UnitaDocumentarieForm.NotaDetail.DESCRIPTION%>"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['noteList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.NotaDetail.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['noteList'].status eq 'insert'}"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['noteList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= UnitaDocumentarieForm.NoteList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=UnitaDocumentarieForm.UnitaDocSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.NM_ENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.NM_STRUT%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.CD_REGISTRO_KEY_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.AA_KEY_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.CD_KEY_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.NM_TIPO_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.DatiUDDetail.DL_OGGETTO_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=UnitaDocumentarieForm.NotaSection.NAME%>" styleClass="importantContainer w100">
                    <c:if test="${sessionScope['###_FORM_CONTAINER']['noteList'].status ne 'insert'}">
                        <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.ID_NOTA_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </c:if>   
                    <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.ID_TIPO_NOTA_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.PG_NOTA_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.DS_NOTA_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.DT_NOTA_UNITA_DOC%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.NM_USERID_NOTA%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:fieldSet legend="Informazioni Indice AIP" borderHidden="true">
                        <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.ID_VER_INDICE_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                        <slf:lblField name="<%=UnitaDocumentarieForm.NotaDetail.DT_CREAZIONE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    </slf:fieldSet>
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
