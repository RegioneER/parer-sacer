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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
 <sl:head title="Dettaglio tipo struttura documento ammesso" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Dettaglio tipo struttura documento ammesso" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo struttura documento ammesso"/>

           <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoStrutDocAmmessoList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoStrutDocAmmesso.NAME%>" hideBackButton="true" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoStrutDocAmmessoList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipiForm.TipoStrutDocAmmessoList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.STipoDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.ID_TIPO_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.NM_TIPO_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.STipoStrutDocAmmesso.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutDocAmmesso.ID_TIPO_STRUT_DOC_AMMESSO%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
