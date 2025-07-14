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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio validità registro" >
        <script type='text/javascript' >
            $(document).ready(function () {
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-off.png"]').attr("src", "./img/alternative/checkbox-on.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-on.png"]').attr("src", "./img/alternative/checkbox-off.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-warn.png"]').remove();
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.DESCRIPTION%>"/>
            <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                <c:if test="${sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.AaTipoFascicoloDetail.NAME%>" hideBackButton="false"/> 
                </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.AaTipoFascicoloList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.STRUTTURA%>"  width="w100" controlWidth="w50" labelWidth="w50"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.ID_ENTE%>"  width="w100" controlWidth="w50" labelWidth="w50"/>
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=StrutTipiFascicoloForm.TipoFascicoloSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.NM_TIPO_FASCICOLO%>" width="w100" labelWidth="w50" controlWidth="w50"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DS_TIPO_FASCICOLO%>" width="w100" labelWidth="w50" controlWidth="w50"/>
                </slf:section>
                <sl:newLine />

                <slf:section name="<%=StrutTipiFascicoloForm.PeriodoValiditaSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.ID_AA_TIPO_FASCICOLO%>" width="w100" controlWidth="w50" labelWidth="w50"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.AA_INI_TIPO_FASCICOLO%>" width="w100" controlWidth="w50" labelWidth="w50"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.AA_FIN_TIPO_FASCICOLO%>" width="w100" controlWidth="w50" labelWidth="w50"/>
                    <c:if test="${(sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].status eq 'view') }"> 
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_UPD_FMT_NUMERO%>" width="w100" controlWidth="w50" labelWidth="w50"/>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.CONTROLLO_FORMATO%>" width="w100" controlWidth="w50" labelWidth="w50"/>
                       
                    </c:if>
                     <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.NI_CHAR_PAD_PARTE_CLASSIF%>" width="w100" labelWidth="w50" controlWidth="w50" />
                </slf:section>   
               
                <sl:newLine />
                <%--<slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloClassificazioneSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_CLASSIF%>" colSpan="3" />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_CLASSIF_NEG%>" colSpan="3" />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_CLASSIF%>" colSpan="3" /> <sl:newLine />
                </slf:section>
                <%--<sl:newLine />
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloNumeroFascSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_NUMERO%>" colSpan="3"/> 
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_NUMERO_NEG%>" colSpan="3"/> 
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_NUMERO%>" colSpan="3"/> 
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriControlloCollegamentiSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ABILITA_CONTR_COLLEG%>" colSpan="3"/> 
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_ACCETTA_CONTR_COLLEG_NEG%>" colSpan="3"/> 
                    <slf:lblField name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.FL_FORZA_CONTR_COLLEG%>" colSpan="3"/> 
                </slf:section>--%>
            </slf:fieldSet>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['aaTipoFascicoloList'].status eq 'view') }"> 
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.ErroriPeriodoValiditaSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%=StrutTipiFascicoloForm.ErrAaTipoFascicoloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=StrutTipiFascicoloForm.ErrAaTipoFascicoloList.NAME%>"  />
                    <slf:listNavBar  name="<%=StrutTipiFascicoloForm.ErrAaTipoFascicoloList.NAME%>" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.VersioniXsdProfiloFasAmmessiSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%=StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>"  />
                    <slf:listNavBar  name="<%=StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>" />
                </slf:section>

                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.ListaPartiSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>"  />
                    <slf:listNavBar  name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" />
                </slf:section>

                <slf:section name="<%=StrutTipiFascicoloForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParametriAaTipoFascButtonList.PARAMETRI_AMMINISTRAZIONE_AA_TIPO_FASC_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriAmministrazioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParametriAmministrazioneAaTipoFascList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParametriAaTipoFascButtonList.PARAMETRI_CONSERVAZIONE_AA_TIPO_FASC_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriConservazioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParametriConservazioneAaTipoFascList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=StrutTipiFascicoloForm.ParametriAaTipoFascButtonList.PARAMETRI_GESTIONE_AA_TIPO_FASC_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriGestioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParametriGestioneAaTipoFascList.NAME%>" />
                </slf:section>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>

