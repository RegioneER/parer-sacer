<%@ page import="it.eng.parer.slite.gen.form.StrutDatiSpecForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio versione XSD metadati specifici" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
        <script type="text/javascript">
            $(document).ready(function () {
                $('.confermaDisattivazioneXsd').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutDatiSpec.html?operation=confermaDisattivazione";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
            });
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <div id="content">
            <slf:messageBox />
            <c:if test="${!empty requestScope.confermaDisattivazioneXsd}">
                <div class="messages confermaDisattivazioneXsd ">
                    <ul>
                        <li class="message info ">Desideri disattivare la versione di XSD ?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="Dettaglio versione XSD metadati specifici"/>
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'insert') && !(sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'update') }"> 
             <sl:form> 
             <!--    <form id="spagoLiteAppForm" action="StrutDatiSpec.html" method="post" > -->
                    <sl:newLine skipLine="true"/>
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>

                    <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']}">
                        <slf:fieldBarDetailTag name="<%= StrutDatiSpecForm.XsdDatiSpec.NAME%>" hideBackButton="true" hideInsertButton="true"/> 
                    </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= StrutDatiSpecForm.XsdDatiSpecList.NAME%>"   />  
                    </c:if>

                    <sl:newLine skipLine="true"/>

                    <slf:fieldSet>
                        <%@ include file="xsdDatiSpec.jspf"%>
                        <h2><font color="#d3101c">Dettaglio XSD</font></h2>
                            <sl:newLine skipLine="true"/>
                            <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DS_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                            <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                            <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.BL_XSD%>" colSpan="4" controlWidth="w100" />
                            <sl:newLine />
                        </slf:fieldSet>
                        <sl:newLine skipLine="true"/>
                        <sl:pulsantiera>
                            <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.SCARICA_XSD_BUTTON%>" width="w50" />
                        </sl:pulsantiera>
                        <sl:newLine skipLine="true"/>
                    <div class="livello1"><b><font color="#d3101c">Elenco metadati</font></b></div>
                            <slf:listNavBar name="<%= StrutDatiSpecForm.AttribDatiSpecList.NAME%>" pageSizeRelated="true"/>
                            <slf:list name="<%= StrutDatiSpecForm.AttribDatiSpecList.NAME%>"  />
                            <slf:listNavBar  name="<%= StrutDatiSpecForm.AttribDatiSpecList.NAME%>" />
                </sl:form>
                <!-- </form> -->
            </c:if>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'insert'}">
             <sl:form id="multipartForm" multipartForm="true">
              <!--   <form id="multipartForm" action="StrutDatiSpec.html" method="post" enctype="multipart/form-data" > -->
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= StrutDatiSpecForm.XsdDatiSpec.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
                        </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= StrutDatiSpecForm.XsdDatiSpecList.NAME%>"   />  
                    </c:if>
                    <sl:newLine />
                    <slf:fieldSet>
                        <%@ include file="xsdDatiSpec.jspf"%>

                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DS_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                        <label class="slLabel wlbl" for="BL_XSD" >File Xsd&nbsp;</label>
                        <div class="containerLeft w4ctr">                        
                            <div><input type="file" id="BL_XSD"  name="BL_XSD" size="80" /></div>
                        </div> 
                    </slf:fieldSet>
                </sl:form>
               <!--  </form> -->
                <sl:newLine />
                <sl:newLine skipLine="true"/>
            </c:if>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdDatiSpec'].status eq 'update') }"> 
             <sl:form id="multipartForm" multipartForm="true">
               <!--  <form id="multipartForm" action="StrutDatiSpec.html" method="post" enctype="multipart/form-data" > -->
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= StrutDatiSpecForm.XsdDatiSpec.NAME%>" hideInsertButton="true"/> 
                        </c:if>   
                        <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdDatiSpecList'].table['empty']) }">
                            <slf:listNavBarDetail name="<%= StrutDatiSpecForm.XsdDatiSpecList.NAME%>"   />  
                        </c:if>
                        <sl:newLine />
                        <slf:fieldSet>
                            <%@ include file="xsdDatiSpec.jspf"%>

                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DS_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                        <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                        <label class="slLabel wlbl" for="BL_XSD" >File Xsd&nbsp;</label>

                        <div class="containerLeft w4ctr">                        
                            <div><input type="file" id="BL_XSD"  name="BL_XSD" size="80" /></div>
                        </div> 
                    </slf:fieldSet>
                </sl:form>
                <!-- </form> -->
                <sl:newLine />
                <sl:newLine skipLine="true"/>
            </c:if>
        </div>
        <sl:footer />
    </sl:body>
</sl:html>
