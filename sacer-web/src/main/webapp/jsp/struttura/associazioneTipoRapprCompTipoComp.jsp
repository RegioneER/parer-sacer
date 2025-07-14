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

<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo componente ammesso nel tipo di rappresentazione componente" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Dettaglio tipo componente" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo componente ammesso nel tipo di rappresentazione componente"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoCompAmmessoDaTipoRapprCompList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprComp.NAME%>" hideBackButton="true" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoCompAmmessoDaTipoRapprCompList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprCompList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StruttureForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.InsStruttura.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoRapprComp.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.ID_TIPO_RAPPR_COMP%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.NM_TIPO_RAPPR_COMP%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoStrutDoc.NAME%>" styleClass="importantContainer">  
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoCompAmmessoDaTipoRapprComp.ID_TIPO_STRUT_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoRapprComp.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.TipoCompAmmessoDaTipoRapprComp.ID_TIPO_COMP_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
