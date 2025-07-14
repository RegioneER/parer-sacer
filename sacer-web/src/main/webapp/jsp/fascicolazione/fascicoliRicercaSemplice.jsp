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

<%@ page import="it.eng.parer.slite.gen.form.FascicoliForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Ricerca fascicoli" />
    <sl:body>
        <c:set var="addRichAnnul" value="${sessionScope['###_FORM_CONTAINER']['fascicoliPerRichAnnulVers']['id_rich_annul_vers'].value != null}"/>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Ricerca Fascicoli"/>            
            <c:choose>
                <c:when test="${addRichAnnul}">
                    <slf:fieldBarDetailTag name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.NAME%>" hideBackButton="false" />
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%= FascicoliForm.FiltriFascicoliRicercaSemplice.NAME%>" hideBackButton="true" />
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:section name="<%=FascicoliForm.FascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_FASCICOLO%>" />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w1ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_FASCICOLO_DA%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_FASCICOLO_A%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_FASCICOLO%>" />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w1ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_FASCICOLO_DA%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_FASCICOLO_A%>" />
                    <sl:newLine />
                    <sl:newLine />

                    <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.ID_TIPO_FASCICOLO%>" />
                    <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.ID_MODELLO_XSD_FASCICOLO%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_XSD%>" />
                    <sl:newLine />
                    <sl:newLine />
                    <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DS_OGGETTO_FASCICOLO%>" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DT_APE_FASCIOLO_DA%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DT_APE_FASCIOLO_A%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DT_CHIU_FASCIOLO_DA%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DT_CHIU_FASCIOLO_A%>" />
                    <sl:newLine />
                    <slf:section name="<%=FascicoliForm.ProcedimentoSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_PROC_AMMIN%>" controlWidth="w100" />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DS_PROC_AMMIN%>" controlWidth="w100" />
                        <sl:newLine />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.NI_AA_CONSERVAZIONE%>" controlWidth="w100" />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_LIVELLO_RISERV%>" controlWidth="w100" />
                        <sl:newLine />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.NM_SISTEMA_VERSANTE%>" controlWidth="w100" />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.NM_USERID%>" controlWidth="w100" />
                        <sl:newLine />
                    </slf:section>
                </slf:section>
                <sl:newLine />

                <slf:section name="<%=FascicoliForm.ProfiloArchivisticoSection.NAME%>" styleClass="importantContainer" >
                    <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_COMPOSITO_VOCE_TITOL%>" />
                    <sl:newLine />
                    <slf:section name="<%=FascicoliForm.FascicoloAppartSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_FASCICOLO_PADRE%>" />
                        <div class="slLabel wlbl" >&nbsp;</div>
                        <div class="containerLeft w1ctr">&nbsp;</div>
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_FASCICOLO_PADRE_DA%>" />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_FASCICOLO_PADRE_A%>" />
                        <sl:newLine />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_FASCICOLO_PADRE%>" />
                        <div class="slLabel wlbl" >&nbsp;</div>
                        <div class="containerLeft w1ctr">&nbsp;</div>
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_FASCICOLO_PADRE_DA%>" />
                        <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_FASCICOLO_PADRE_A%>" />
                        <sl:newLine />
                        <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.DS_OGGETTO_FASCICOLO_PADRE%>" controlWidth="w100" />
                        <sl:newLine />
                    </slf:section>
                </slf:section>
                <sl:newLine />

                <slf:section name="<%=FascicoliForm.UDContenuteNelFascSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_REGISTRO_KEY_UNITA_DOC%>" />
                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w1ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_KEY_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_UNITA_DOC%>" />
                    <sl:newLine />

                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_KEY_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.AA_KEY_UNITA_DOC_A%>" />
                    <sl:newLine />

                    <div class="slLabel wlbl" >&nbsp;</div>
                    <div class="containerLeft w2ctr">&nbsp;</div>
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_UNITA_DOC_DA%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_KEY_UNITA_DOC_A%>" />
                    <sl:newLine />

                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.FL_UPD_ANNUL_UNITA_DOC%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.FL_UPD_MODIF_UNITA_DOC%>" />
                    <sl:newLine />
                </slf:section>
                <sl:newLine />

                <slf:section name="<%=FascicoliForm.ParametriSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="2" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.TI_CONSERVAZIONE%>" />
                    <sl:newLine />

                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.FL_FORZA_CONTR_CLASSIF%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.FL_FORZA_CONTR_NUMERO%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.FL_FORZA_CONTR_COLLEG%>" />
                    <sl:newLine />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.CD_VERSIONE_WS%>" />
                </slf:section>
                <sl:newLine />

                <slf:section name="<%=FascicoliForm.ControlliSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.TS_VERS_FASCICOLO_DA%>" />
                    <slf:doubleLblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.ORE_TS_VERS_FASCICOLO_DA%>" name2="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.MINUTI_TS_VERS_FASCICOLO_DA%>" controlWidth="w20" controlWidth2="w20" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.TS_VERS_FASCICOLO_A%>" />
                    <slf:doubleLblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.ORE_TS_VERS_FASCICOLO_A%>" name2="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.MINUTI_TS_VERS_FASCICOLO_A%>" controlWidth="w20" controlWidth2="w20" />
                    <sl:newLine />

                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.TI_ESITO%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.TI_STATO_CONSERVAZIONE%>" />
                    <slf:lblField colSpan="1" name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.TI_STATO_FASC_ELENCO_VERS%>" />
                    <sl:newLine />
                </slf:section>
                <sl:newLine />

            </slf:fieldSet>
            <sl:newLine skipLine="true"/>

            <sl:pulsantiera>
                <slf:lblField name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.RICERCA_FASCICOLI%>" width="w50" />
                <slf:lblField name="<%=FascicoliForm.FiltriFascicoliRicercaSemplice.PULISCI_FASCICOLI%>" width="w50" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= FascicoliForm.FascicoliList.NAME%>" pageSizeRelated="true"/>
            <c:choose>
                <c:when test="${addRichAnnul}">
                    <slf:selectList name="<%= FascicoliForm.FascicoliList.NAME%>" addList="true"/>
                </c:when>
                <c:otherwise>                    
                    <slf:list name="<%= FascicoliForm.FascicoliList.NAME%>"/>
                </c:otherwise>
            </c:choose>
            <slf:listNavBar name="<%= FascicoliForm.FascicoliList.NAME%>" />

            <sl:newLine skipLine="true"/>
            <c:choose>
                <c:when test="${addRichAnnul}">
                    <!-- Inserisco la lista vuota dei fascicoli da aggiungere alla richiesta di annullamento -->
                    <slf:section name="<%=FascicoliForm.FascicoliToRichAnnulVersSection.NAME%>" styleClass="importantContainer">                
                        <slf:listNavBar name="<%= FascicoliForm.FascicoliPerRichAnnulVersList.NAME%>" pageSizeRelated="true"/>
                        <slf:selectList name="<%= FascicoliForm.FascicoliPerRichAnnulVersList.NAME%>" addList="false"/>
                        <slf:listNavBar  name="<%= FascicoliForm.FascicoliPerRichAnnulVersList.NAME%>" />

                        <sl:newLine skipLine="true"/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=FascicoliForm.FascicoliPerRichAnnulVers.ADD_TO_RICH_ANNUL%>" colSpan="3" />
                        </sl:pulsantiera>
                    </slf:section>
                </c:when>
            </c:choose>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
