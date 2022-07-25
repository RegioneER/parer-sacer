<%@ page import="it.eng.parer.slite.gen.form.ModelliUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Modello XSD" >        
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
        <script type="text/javascript">
        $(document).ready(function () {
            
            $('#Id_ambiente').change(function () {
                var value = $(this).val();
                    $.getJSON("ModelliUD.html", {operation: "triggerModelliXsdUdDetailId_ambienteOnTriggerJs",
                        Id_ambiente: value
                    }).done(function (data) {
                        CAjaxDataFormWalk(data);
                    });
            });
      
            $('#Cd_xsd').on("input", function(e) {
                var value = $(this).val();
				$(this).val($.trim(value.toUpperCase())); 
            });
        });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <div id="content">
            <slf:messageBox /> 
            <sl:contentTitle title="<%=ModelliUDForm.ModelliXsdUdDetail.DESCRIPTION%>"/>
            
           
                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdUdDetail'].status eq 'insert') && !(sessionScope['###_FORM_CONTAINER']['modelliXsdUdDetail'].status eq 'update') }">
	                <sl:form> 
	                    <sl:newLine skipLine="true"/>
	                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
	
	                    <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].table['empty']}">
	                        <slf:fieldBarDetailTag name="<%= ModelliUDForm.ModelliXsdUdDetail.NAME%>" hideBackButton="true" hideInsertButton="true"/> 
	                    </c:if>   
	                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].table['empty']) }">
	                        <slf:listNavBarDetail name="<%= ModelliUDForm.ModelliXsdUdList.NAME%>" />
	                    </c:if>
						
						<sl:newLine skipLine="true"/>
			            <slf:fieldSet >
			               <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].status eq 'insert'}">
                                <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:when>
                            <c:otherwise>
                                <slf:lblField name="<%= ModelliUDForm.ModelliXsdUdDetail.NM_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:otherwise>
                        	</c:choose>
                     	    <sl:newLine />
	                        <sl:newLine skipLine="true"/>
	                    	<h2><font color="#d3101c">Dettaglio modello XSD</font></h2>
	                        <sl:newLine skipLine="true"/>
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.FL_DEFAULT%>" colSpan="4" controlWidth="w40"/><sl:newLine />                                                
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.BL_XSD%>" colSpan="4" controlWidth="w100" />
	                        <sl:newLine />
	                    </slf:fieldSet>      
	                    <sl:newLine skipLine="true"/>
	                    <sl:pulsantiera>
	                    	<slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.LOG_EVENTI%>" width="w50" />	                    
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.SCARICA_XSD_BUTTON%>" width="w50" />
	                    </sl:pulsantiera>                    
					</sl:form> 
	            </c:if>
	            
	            <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdDetail'].status eq 'insert'}">
	               <sl:form id="multipartForm" multipartForm="true">
	                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
	                    <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].table['empty']}">
	                        <slf:fieldBarDetailTag name="<%=  ModelliUDForm.ModelliXsdUdDetail.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
	                    </c:if>   
	                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].table['empty']) }">
	                        <slf:listNavBarDetail name="<%=  ModelliUDForm.ModelliXsdUdList.NAME%>"   />  
	                    </c:if>
	                    
						<sl:newLine skipLine="true"/>
			            <slf:fieldSet >
			                
			                <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].status eq 'insert'}">
                                <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:when>
                            <c:otherwise>
                                <slf:lblField name="<%= ModelliUDForm.ModelliXsdUdDetail.NM_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:otherwise>
                        	</c:choose>
	                   		<sl:newLine />
	                        <sl:newLine skipLine="true" />
	                        <h2><font color="#d3101c">Inserimento modello XSD</font></h2>
	                        <sl:newLine skipLine="true"/>
							<!--  -->	                        	                      
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />	                        
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.FL_DEFAULT%>" colSpan="4" controlWidth="w40"/><sl:newLine />                                                
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <label class="slLabel wlbl" for="BL_XSD" >File Xsd&nbsp;</label>
                        	<div class="containerLeft w4ctr">                        
                            	<div><input type="file" id="BL_XSD"  name="BL_XSD" size="80" /></div>
                        	</div> 
	                      	<sl:newLine />
	                    </slf:fieldSet>
	                    <sl:newLine skipLine="true"/>
	                    <sl:pulsantiera>
	                    	 <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.SCARICA_XSD_BUTTON%>" width="w50" />
	                 	</sl:pulsantiera> 
	                  </sl:form>
	                <sl:newLine />
	                <sl:newLine skipLine="true"/>	               
	            </c:if>
	
	            <c:if test="${(sessionScope['###_FORM_CONTAINER']['modelliXsdUdDetail'].status eq 'update') }"> 
	               <sl:form id="multipartForm" multipartForm="true">
	                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
	                    <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].table['empty']}">
	                        <slf:fieldBarDetailTag name="<%= ModelliUDForm.ModelliXsdUdDetail.NAME%>" hideInsertButton="true"/> 
	                    </c:if>   
	                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].table['empty']) }">
	                        <slf:listNavBarDetail name="<%= ModelliUDForm.ModelliXsdUdList.NAME%>"   />  
	                    </c:if>
	                    
						<sl:newLine skipLine="true"/>
			            <slf:fieldSet >
			            
			                <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliXsdUdList'].status eq 'insert'}">
                                <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:when>
                            <c:otherwise>
                                <slf:lblField name="<%= ModelliUDForm.ModelliXsdUdDetail.NM_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:otherwise>
                        	</c:choose>
		                    <sl:newLine />
	                        <sl:newLine skipLine="true" />
	                        <h2><font color="#d3101c">Modifica modello XSD</font></h2>
	                        <sl:newLine skipLine="true"/>
	                   	    <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />	                        
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.FL_DEFAULT%>" colSpan="4" controlWidth="w40"/><sl:newLine />                                                
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <label class="slLabel wlbl" for="BL_XSD" >File Xsd&nbsp;</label>
	                        <div class="containerLeft w4ctr">                        
	                            <div><input type="file" id="BL_XSD"  name="BL_XSD" size="80" /></div>
	                        </div> 
	                    </slf:fieldSet>
	                    <sl:newLine skipLine="true"/>
	                    <sl:pulsantiera>
	                    	 <slf:lblField name="<%=ModelliUDForm.ModelliXsdUdDetail.SCARICA_XSD_BUTTON%>" width="w50" />
	                 	</sl:pulsantiera> 
	                </sl:form>
	                <sl:newLine />
	                <sl:newLine skipLine="true"/>
	            </c:if>   
    	    </div>
        <sl:footer />
    </sl:body>
</sl:html>