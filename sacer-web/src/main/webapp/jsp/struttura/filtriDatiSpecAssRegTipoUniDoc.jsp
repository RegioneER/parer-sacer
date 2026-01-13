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
    <sl:head title="<%=StrutSerieForm.FiltriDatiSpec.DESCRIPTION%>" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
        <script type="text/javascript" src="<c:url value='/js/sips/customTipologiaSerieVincolataMessageBox.js'/>"></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <sl:contentTitle title="<%=StrutSerieForm.FiltriDatiSpec.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%= StrutSerieForm.RegistroDetail.NAME%>" hideBackButton="false"  /> 
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

            <sl:newLine skipLine="true"/>
            <slf:lblField name="<%=StrutSerieForm.FiltriDatiSpec.NM_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.FiltriDatiSpec.NM_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
            <sl:newLine />
            <slf:lblField name="<%=StrutSerieForm.FiltriDatiSpec.NM_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
            <sl:newLine skipLine="true"/>

            <%--                <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutSerieForm.SerieUniDoc.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
            </slf:section>
            <slf:section name="<%=StrutSerieForm.SerieRegistroUniDoc.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutSerieForm.RegistroDetail.ID_REGISTRO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegistroDetail.NI_ANNI_CONSERV%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />

                    <slf:lblField name="<%=StrutSerieForm.RegistroDetail.TIPO_UNITA_DOC_VIS%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                            <sl:newLine />                                
                </slf:section>
            --%>
            <!--  piazzo la lista dei filtri di ricerca dati specifici -->
            <%--<slf:listNavBar name="<%= StrutSerieForm.FiltriDatiSpecList.NAME%>" pageSizeRelated="true"/>--%>
            <slf:nestedList name="<%= StrutSerieForm.FiltriDatiSpecList.NAME%>" subListName="<%= StrutSerieForm.DefinitoDaList.NAME%>" multiRowEdit="true"/>
            <%--<slf:listNavBar name="<%= // StrutSerieForm.FiltriDatiSpecList.NAME%>" />--%>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%= StrutSerieForm.FiltriDatiSpec.SALVA_FILTRI_DATI_SPEC%>" width="w25" />
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
