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
    <sl:head title="Dettaglio tipo struttura unità documentaria" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Struttura UD" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo struttura unità documentaria"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoStrutUnitaDocList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoStrutUnitaDoc.NAME%>" hideBackButton="false" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoStrutUnitaDocList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiForm.TipoStrutUnitaDocList.NAME%>" /> 
            </c:if>
            <sl:newLine skipLine="true"/>
            <sl:newLine />
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>

                <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/> 
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                </slf:section>

                <slf:section name="<%=StrutTipiForm.STipoStrutUnitaDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.NM_TIPO_STRUT_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_TIPO_STRUT_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DT_SOPPRES%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>

                <slf:section name="<%=StrutTipiForm.PeriodoValiditaSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.AA_MIN_TIPO_STRUT_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.AA_MAX_TIPO_STRUT_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                </slf:section>

                <slf:section name="<%=StrutTipiForm.ChiaveUdSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.REGISTRO_TIPO_STRUT_UNITA_DOC%>" colSpan= "4" /><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_ANNO_TIPO_STRUT_UNITA_DOC%>" colSpan= "4" /><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_NUMERO_TIPO_STRUT_UNITA_DOC%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.ProfiloUdSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_DATA_TIPO_STRUT_UNITA_DOC%>" colSpan= "4" /><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_OGG_TIPO_STRUT_UNITA_DOC%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.SistemiVersantiSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.SIS_VERS_TIPO_STRUT_UNITA_DOC%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.MetadatiSpecificiSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.METADATI_SPECIFICI%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.RiferimentiTemporaliSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_RIF_TEMP_TIPO_STRUT_UD%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.CollegamentiUdSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_COLLEGAMENTI_UD%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.PeriodicitaVersamentiSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_PERIODICITA_VERS%>" colSpan= "4" /><sl:newLine />
                </slf:section>
                
                <slf:section name="<%=StrutTipiForm.FirmaSection.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_FIRMA%>" colSpan= "4" /><sl:newLine />
                </slf:section>

            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoStrutUnitaDoc'].status eq 'view') }"> 
                <!--  piazzo la lista con i risultati -->
                <div class="livello1"><b style="color: #d3101c;">Tipi documento ammessi</b></div>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= StrutTipiForm.TipoDocAmmessoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StrutTipiForm.TipoDocAmmessoList.NAME%>"  />
                <slf:listNavBar  name="<%= StrutTipiForm.TipoDocAmmessoList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
