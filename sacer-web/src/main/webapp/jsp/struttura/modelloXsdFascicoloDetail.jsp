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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DESCRIPTION%>" >        
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />             
            <sl:contentTitle title="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.NAME%>" hideBackButton="false" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.NM_AMBIENTE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.TI_MODELLO_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.TI_USO_MODELLO_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.CD_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DS_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DT_ISTITUZ%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DT_SOPPRES%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.BL_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
