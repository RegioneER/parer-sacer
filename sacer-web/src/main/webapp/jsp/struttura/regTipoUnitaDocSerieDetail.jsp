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
    <sl:head title="Dettaglio Associazione registro - tipologia di Unità documentarie" >
        <script type="text/javascript" src="<c:url value='/js/sips/customTipologiaSerieVincolataMessageBox.js'/>"></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <sl:contentTitle title="Dettaglio Associazione registro - tipologia di Unità documentarie"/>

            <sl:newLine skipLine="true"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoSerieRegistriList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutSerieForm.RegistroDetail.NAME%>" hideBackButton="false"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoSerieRegistriList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutSerieForm.TipoSerieRegistriList.NAME%>" />   
            </c:if>

            <slf:messageBox />

            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">L’operazione comporterà il ricalcolo di almeno una serie. Si intende procedere?</li>
                    </ul>
                </div>
            </c:if>

            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= StrutSerieForm.TipoSerieCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <slf:lblField name="<%=StrutSerieForm.RegistroDetail.NM_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.RegistroDetail.NM_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.RegistroDetail.NM_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>

            <sl:newLine skipLine="true"/>

            <slf:section name="<%=StrutSerieForm.SerieUniDoc.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
            </slf:section>

            <slf:section name="<%=StrutSerieForm.SerieRegistroUniDoc.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutSerieForm.RegistroDetail.ID_REGISTRO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegistroDetail.NI_ANNI_CONSERV%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoSerieRegistriList'].status eq 'view') }">
                    <slf:lblField name="<%=StrutSerieForm.RegistroDetail.TIPO_UNITA_DOC_VIS%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />                                
                </c:if>
                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoSerieRegistriList'].status eq 'view') }">
                    <slf:lblField name="<%=StrutSerieForm.RegistroDetail.ID_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                    <sl:newLine />                                
                </c:if>
                <slf:lblField name="<%=StrutSerieForm.RegistroDetail.FL_SEL_UNITA_DOC_ANNUL%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />                                
            </slf:section>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoSerieRegistriList'].status eq 'view') }">
                <div class="livello1"><b style="color: #d3101c;">Regole di filtraggio sul tipo documento principale</b></div>
                        <slf:listNavBar name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>"  />
                        <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;">Filtri su dati specifici</b></div>
                        <slf:listNavBar name="<%= StrutSerieForm.AssociazioneDatiSpecList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutSerieForm.AssociazioneDatiSpecList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutSerieForm.AssociazioneDatiSpecList.NAME%>"  />
                        <sl:newLine skipLine="true"/>
                <div class="livello1"><b style="color: #d3101c;"><%= StrutSerieForm.RegoleDiRappresentazioneList.DESCRIPTION%></b></div>
                        <slf:listNavBar name="<%= StrutSerieForm.RegoleDiRappresentazioneList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutSerieForm.RegoleDiRappresentazioneList.NAME%>"  />
                        <slf:listNavBar  name="<%= StrutSerieForm.RegoleDiRappresentazioneList.NAME%>"  />
                        <sl:newLine skipLine="true"/>
                    </c:if>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

