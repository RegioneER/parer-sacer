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

<%@ page import="it.eng.parer.slite.gen.form.TrasformatoriForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>



<sl:html>
    <sl:head title="Carica Immagine Trasformatore" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>     
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>

            <sl:newLine skipLine="true"/>
             
            <c:if test="${sessionScope['###_FORM_CONTAINER']['imageTrasformList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= TrasformatoriForm.ImageTrasform.NAME%>" hideBackButton="false"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['imageTrasformList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= TrasformatoriForm.ImageTrasformList.NAME%>" />   
            </c:if>

                <slf:messageBox />
                <sl:contentTitle title="<%=TrasformatoriForm.ImageTrasform.DESCRIPTION%>"/>
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=TrasformatoriForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=TrasformatoriForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformatoriForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                </slf:section>
                <slf:section name="<%=TrasformatoriForm.Trasform.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.NM_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.CD_VERSIONE_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                </slf:section>
                <slf:section name="<%=TrasformatoriForm.Image.NAME%>"  styleClass="importantContainer">
                    <slf:lblField name="<%=TrasformatoriForm.ImageTrasform.NM_IMAGE_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformatoriForm.ImageTrasform.DT_LAST_MOD_IMAGE_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformatoriForm.ImageTrasform.DT_LAST_SCARICO_IMAGE_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformatoriForm.ImageTrasform.NM_COMPLETO_IMAGE_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                    <sl:newLine />
                    <slf:lblField name="<%=TrasformatoriForm.ImageTrasform.TI_PATH_TRASFORM%>" colSpan= "4"  controlWidth="w70"/>
                    <sl:newLine />
                    <sl:pulsantiera>
                            <slf:lblField  name="<%=TrasformatoriForm.ImageTrasform.CARICA_FILE_IMG_TRASFORMATORE%>"  width="w50" />
                            <slf:lblField  name="<%=TrasformatoriForm.ImageTrasform.SCARICA_FILE_IMG_TRASFORMATORE%>"  width="w50" />
                    </sl:pulsantiera> 
                </slf:section>

        </sl:content>
                  
        <sl:footer />
    </sl:body>

</sl:html>

