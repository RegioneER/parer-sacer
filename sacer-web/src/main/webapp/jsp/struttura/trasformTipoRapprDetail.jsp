<%@ page import="it.eng.parer.slite.gen.form.TrasformatoriForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Dettaglio trasformatore rappresentazione componente" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="<%=TrasformatoriForm.TrasformTipoRappr.DESCRIPTION%>"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['trasformTipoRapprList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= TrasformatoriForm.TrasformTipoRappr.NAME%>" hideBackButton="false"/> 
            </c:if>  
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['trasformTipoRapprList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= TrasformatoriForm.TrasformTipoRapprList.NAME%>" />   
            </c:if>            
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
                <sl:newLine />
                <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.TI_STATO_FILE_TRASFORM%>"  colSpan="4" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.DT_INS_TRASFORM%>" colSpan= "4"  controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.DT_LAST_MOD_TRASFORM%>" colSpan="4" controlWidth="w70"/>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField  name="<%=TrasformatoriForm.TrasformTipoRappr.SBLOCCA_FILE_TRASFORMATORE%>"  width="w50" />
                    <sl:newLine />
                </sl:pulsantiera> 
            </slf:section>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['trasformTipoRapprList'].status eq 'view') }">
                <slf:section name="<%=TrasformatoriForm.FileTrasform.NAME%>" styleClass="importantContainer">  
                    <slf:fieldSet borderHidden="true">
                        <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.ST_FILE_TRASFORM%>" colSpan="4" controlWidth="w100" />
                        <sl:newLine />
                        <slf:lblField name="<%=TrasformatoriForm.TrasformTipoRappr.DS_HASH_FILE_TRASFORM%>"/>
                        <sl:pulsantiera>
                            <slf:lblField  name="<%=TrasformatoriForm.TrasformTipoRappr.SCARICA_TRASFORMATORE%>"  width="w50" />
                            <slf:lblField  name="<%=TrasformatoriForm.TrasformTipoRappr.TEST_TRASFORMATORE%>"  width="w50" />
                            <slf:lblField  name="<%=TrasformatoriForm.TrasformTipoRappr.CARICA_FILE_TRASFORMATORE%>"  width="w50" />
                        </sl:pulsantiera> 
                    </slf:fieldSet>
                </slf:section>
            </c:if>

            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['trasformTipoRapprList'].status eq 'view') }">
                <div class="livello1"><b><font color="#d3101c">Immagini del Trasformatore</font></b></div>
                <slf:listNavBar name="<%= TrasformatoriForm.ImageTrasformList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= TrasformatoriForm.ImageTrasformList.NAME%>"  />
                <slf:listNavBar  name="<%= TrasformatoriForm.ImageTrasformList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

