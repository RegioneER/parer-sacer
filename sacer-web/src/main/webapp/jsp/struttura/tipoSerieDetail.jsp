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

<%@ page import="it.eng.parer.slite.gen.form.StrutSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutSerieForm.TipoSerieDetail.DESCRIPTION%>" >
        <script type="text/javascript" src="<c:url value='/js/sips/customTipologiaSerieVincolataMessageBox.js'/>"></script>
        <script type="text/javascript" src='<c:url value="/js/custom/controlliSerie.js"/>' ></script>
        <script type="text/javascript">
            $(document).ready(function () {

                initTipoSeriePage();
                if ($("#Ni_anni_conserv").val().length !== 0) {
                    $("#Ni_anni_conserv").trigger('change');
                }
                if ($("#Fl_crea_autom").val().length !== 0) {
                    $("#Fl_crea_autom").trigger('change');
                }
                if ($('#Ti_sel_ud').val().length !== 0) {
                    $('#Ti_sel_ud').trigger('change');
                }
            });
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <slf:messageBox />

            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">L'operazione comporter&agrave; il ricalcolo di almeno una serie. Si intende procedere?</li>
                    </ul>
                </div>
            </c:if>
            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= StrutSerieForm.TipoSerieCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:contentTitle title="<%=StrutSerieForm.TipoSerieDetail.DESCRIPTION%>"/>

            <sl:newLine skipLine="true"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipologieSerieList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutSerieForm.TipoSerieDetail.NAME%>" hideBackButton="false"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipologieSerieList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutSerieForm.TipologieSerieList.NAME%>" />   
            </c:if>

            <sl:newLine skipLine="true"/>
            <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
            <sl:newLine skipLine="true"/>

            <slf:section name="<%=StrutSerieForm.TipoSerie.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_TIPO_SERIE%>" width="w100" controlWidth="w60" labelWidth="w30" />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.DS_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.DT_ISTITUZ%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.DT_SOPPRES%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.TI_CREA_STANDARD%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NM_MODELLO_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.CD_SERIE_DEFAULT%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.DS_SERIE_DEFAULT%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.CONSERV_UNLIMITED%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NI_ANNI_CONSERV%>" colSpan= "2" labelWidth="w30" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.TI_CONSERVAZIONE_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w40" />
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.TIPO_CONTEN_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.TI_SEL_UD%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NI_AA_SEL_UD%>" colSpan= "2" labelWidth="w30" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NI_AA_SEL_UD_SUC%>" colSpan= "2" labelWidth="w30" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NI_UNITA_DOC_VOLUME%>" colSpan= "2" labelWidth="w30" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.FL_CONTROLLO_CONSIST_OBBLIG%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.FL_CREA_AUTOM%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.GG_CREA_AUTOM%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.AA_INI_CREA_AUTOM%>" colSpan= "1" labelWidth="w30" controlWidth="w100" />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.AA_FIN_CREA_AUTOM%>" colSpan= "1" labelWidth="w10" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NI_TRANSCODED_MM_CREA_AUTOM%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.TI_STATO_VER_SERIE_AUTOM%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
            </slf:section>

            <slf:section name="<%=StrutSerieForm.InfoPadre.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.ID_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.NI_ANNI_CONSERV_PADRE%>" colSpan= "2" labelWidth="w30" controlWidth="w40" />
                <sl:newLine />
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField  name="<%=StrutSerieForm.TipoSerieDetail.VIEW_DETTAGLIO_SERIE_PADRE%>"  width="w50" />
                    <sl:newLine />
                </sl:pulsantiera> 
            </slf:section>

            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=StrutSerieForm.TipoSerieDetail.LOG_EVENTI%>" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipologieSerieList'].status eq 'view') }">
                <div class="livello1"><b style="color: #d3101c;">Elementi di descrizione</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:listNavBar name="<%= StrutSerieForm.NoteTipoSerieList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutSerieForm.NoteTipoSerieList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutSerieForm.NoteTipoSerieList.NAME%>" />
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Registri - Tipologie di unit&agrave; documentarie</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:listNavBar name="<%= StrutSerieForm.TipoSerieRegistriList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutSerieForm.TipoSerieRegistriList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutSerieForm.TipoSerieRegistriList.NAME%>" />
                        <sl:newLine skipLine="true"/>
                        <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Regole di acquisizione</b></div>
                        <sl:newLine skipLine="true"/>
                        <slf:listNavBar name="<%= StrutSerieForm.RegoleAcquisizioneList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutSerieForm.RegoleAcquisizioneList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutSerieForm.RegoleAcquisizioneList.NAME%>" />
                    </c:if>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

