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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo fascicolo" >        
        <script type='text/javascript' >

            $(document).ready(function () {
                // Modellazione grafica dei flag relativi al controllo formato
                $('div[id^="Controllo_formato_numero"] > img[src$="checkbox-field-off.png"]').attr("src", "./img/alternative/checkbox-on.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato_numero"] > img[src$="checkbox-field-on.png"]').attr("src", "./img/alternative/checkbox-off.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato_numero"] > img[src$="checkbox-field-warn.png"]').remove();

                $('div[id^="Controllo_formato_da_list"] > img[src$="checkbox-off.png"]').attr("src", "./img/alternative/checkbox-on.png");
                $('div[id^="Controllo_formato_da_list"] > img[src$="checkbox-warn.png"]').remove();
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />

        <sl:content>
            <slf:messageBox />             

            <sl:contentTitle title="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DESCRIPTION%>"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoFascicoloList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.TipoFascicoloDetail.NAME%>" hideBackButton="${(sessionScope['###_FORM_CONTAINER']['tipoFascicoloList'].status eq 'insert')}"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoFascicoloList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.TipoFascicoloList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.ID_ENTE%>"  colSpan = "2" labelWidth="w30" controlWidth="w70" />                  
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w30" controlWidth="w70" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.TipoFascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.ID_TIPO_FASCICOLO%>" colSpan="2" labelWidth="w30" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.NM_TIPO_FASCICOLO%>" colSpan="2" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DS_TIPO_FASCICOLO%>" colSpan="2" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DT_ISTITUZ%>" colSpan="2" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DT_SOPPRES%>" colSpan="2" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <%--<slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.CONTROLLO_FORMATO_NUMERO%>" colSpan="2" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.NI_CHAR_PAD_PARTE_CLASSIF%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />--%>
                </slf:section>
                <%--<slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloClassificazioneSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_CLASSIF%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_CLASSIF_NEG%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_CLASSIF%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />                                        
                </slf:section>
                <%--<slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloNumeroFascSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_NUMERO%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_NUMERO_NEG%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_NUMERO%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                </slf:section>
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloCollegamentiSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_COLLEG%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_COLLEG_NEG%>" width="w100" labelWidth="w30" controlWidth="w20"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_COLLEG%>" width="w100" labelWidth="w30" controlWidth="w10"/> <sl:newLine />
                </slf:section>--%>
            </slf:fieldSet>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoFascicoloList'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.LOG_EVENTI_TIPO_FASCICOLO%>" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoFascicoloDetail']['nm_tipo_fascicolo'].value ne 'Tipo fascicolo sconosciuto') }">
                    <slf:section name="<%=StrutTipiFascicoloForm.PeriodiValiditaSection.NAME%>" styleClass="importantContainer"> 
                        <slf:listNavBar name="<%= StrutTipiFascicoloForm.AaTipoFascicoloList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTipiFascicoloForm.AaTipoFascicoloList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutTipiFascicoloForm.AaTipoFascicoloList.NAME%>" />
                    </slf:section>    
                    <slf:section name="<%=StrutTipiFascicoloForm.CriteriRaggrFascSection.NAME%>" styleClass="importantContainer"> 
                        <slf:listNavBar name="<%= StrutTipiFascicoloForm.CriteriRaggrFascicoloList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTipiFascicoloForm.CriteriRaggrFascicoloList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutTipiFascicoloForm.CriteriRaggrFascicoloList.NAME%>" />
                    </slf:section>
                </c:if>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
