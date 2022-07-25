<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Xsd Tipo Doc" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <div id="content">
           <!--  <form id="spagoLiteAppForm" action="Strutture.html" method="post" > -->
 			<sl:form> 
                <slf:messageBox />    
                <sl:newLine skipLine="true"/>
                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdTipoDoc'].status eq 'insert')&& !(sessionScope['###_FORM_CONTAINER']['xsdTipoDoc'].status eq 'update') }"> 


                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdTipoDocList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= StruttureForm.XsdDatiSpec.NAME%>" hideBackButton="true" hideInsertButton="true"/> 
                        </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdTipoDocList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= StruttureForm.XsdDatiSpecList.NAME%>"   />  
                    </c:if>

                    <sl:contentTitle title="Caricamento XSD"/>



                    <sl:newLine />

                    <slf:lblField name="<%=StruttureForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <%--  <slf:lblField name="<%=StruttureForm.XsdTipoDoc.DT_VERSIONE_XSD%>" colSpan="4" controlWidth="w20"/> --%>

                    <slf:lblField name="<%=StruttureForm.XsdDatiSpec.BL_XSD_TIPO_DOC%>" colSpan="4" controlWidth="w100" />

                    <sl:newLine />

                    <sl:pulsantiera>
                        <slf:lblField name="<%=StruttureForm.XsdDatiSpec.SCARICA_XSD_BUTTON%>" width="w50" />

                    </sl:pulsantiera>

                    <div class="livello1"><b>Lista Attributi Associati</b></div>
                    <slf:listNavBar name="<%= StruttureForm.AttribTipoDocList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StruttureForm.AttribTipoDocList.NAME%>"  />
                    <slf:listNavBar  name="<%= StruttureForm.AttribTipoDocList.NAME%>" />
                </c:if>



			</sl:form>
          <!--   </form> -->


      <%--      <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdTipoDoc'].status eq 'insert') } ">  --%>
      
 <%--<c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdTipoDoc'].status eq 'insert') } "> --%>
 
    <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdTipoDoc'].status eq 'insert'}">
          <sl:form id="multipartForm" multipartForm="true">
               <!--  <form id="multipartForm" action="Strutture.html" method="post" enctype="multipart/form-data" > -->
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdTipoDocList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= StruttureForm.XsdTipoDoc.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
                        </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdTipoDocList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= StruttureForm.XsdTipoDocList.NAME%>"   />  
                    </c:if>
                    <sl:newLine />

                    <slf:lblField name="<%=StruttureForm.XsdTipoDoc.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />

                    <%--              <slf:lblField name="<%=StruttureForm.XsdTipoDoc.FILE_XSD%>" colSpan="4" controlWidth="w100" /> --%>

                    <label class="slLabel wlbl" for="BL_XSD_TIPO_DOC" >File Xsd&nbsp;</label>
   
                    <div class="containerLeft w4ctr">                        
                        <div><input type="file" id="BL_XSD_TIPO_DOC"  name="BL_XSD_TIPO_DOC" size="80" /></div>
                    </div> 
    <%--                 <sl:pulsantiera>

                        <slf:lblField name="<%=StruttureForm.XsdTipoDoc.CARICA_XSD_BUTTON%>" width="w50" />
                    </sl:pulsantiera> --%>
                <!-- </form> -->
                </sl:form>

                <sl:newLine />
                <sl:newLine skipLine="true"/>

            </c:if>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdTipoDoc'].status eq 'update') }"> 


 				<sl:form id="multipartForm" multipartForm="true">
               <!--  <form id="multipartForm" action="Strutture.html" method="post" enctype="multipart/form-data" > -->
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                        <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdTipoDocList'].table['empty']}">
                            <slf:fieldBarDetailTag name="<%= StruttureForm.XsdTipoDoc.NAME%>" hideInsertButton="true"/> 
                        </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdTipoDocList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= StruttureForm.XsdTipoDocList.NAME%>"   />  
                    </c:if>
                    <sl:newLine />

                    <slf:lblField name="<%=StruttureForm.XsdTipoDoc.CD_VERSIONE_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />

                    <%--              <slf:lblField name="<%=StruttureForm.XsdTipoDoc.FILE_XSD%>" colSpan="4" controlWidth="w100" /> --%>

                    <label class="slLabel wlbl" for="BL_XSD_TIPO_DOC" >File Xsd&nbsp;</label>
  
                    <div class="containerLeft w4ctr">                        
                        <div><input type="file" id="BL_XSD_TIPO_DOC"  name="BL_XSD_TIPO_DOC" size="80" /></div>
                    </div> 
          <%--          <sl:pulsantiera>

                        <slf:lblField name="<%=StruttureForm.XsdTipoDoc.CARICA_XSD_BUTTON%>" width="w50" />
                    </sl:pulsantiera> --%>
               <!--  </form> -->
               </sl:form>

                <sl:newLine />
                <sl:newLine skipLine="true"/>

            </c:if>



        </div>
        <sl:footer />
    </sl:body>

</sl:html>

