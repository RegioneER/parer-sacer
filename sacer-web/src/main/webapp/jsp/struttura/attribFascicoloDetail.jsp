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

<%@page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm"%>
<%@ page import="it.eng.parer.slite.gen.form.StrutDatiSpecForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio attributo fascicolo" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Attributi Fascicolo"/>
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio attributo"/>

            <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.AttribFascicoloList.NAME%>" />  
            <sl:newLine skipLine="true"/>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=StrutTipiFascicoloForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.STRUTTURA%>" colSpan= "4" /><sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.ID_ENTE%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                <slf:section name="<%=StrutTipiFascicoloForm.TipoFascicoloSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.NM_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DS_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                </slf:section>



                <sl:newLine />
                <slf:section name="<%=StrutTipiFascicoloForm.SAttribFascicolo.NAME%>" styleClass="importantContainer">   
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AttribFascicolo.NM_ATTRIB_FASCICOLO%>" colSpan= "4" /> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AttribFascicolo.DS_ATTRIB_FASCICOLO%>" colSpan= "4" /> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AttribFascicolo.NI_ORD_ATTRIB%>" colSpan= "4" /> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AttribFascicolo.DT_ISTITUZ%>" colSpan= "4" /> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AttribFascicolo.DT_SOPPRES%>" colSpan= "4" /> <sl:newLine />
                </slf:section>
            </slf:fieldSet>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

