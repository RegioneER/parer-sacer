<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Modello XSD ammesso" >        
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>   
        <script type="text/javascript">
            $(document).ready(function () {
                $('.confermaDisattivazioneXsdUdAmmesso').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutTipi.html?operation=confermaDisattivazioneXsdUdAmmesso";
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
            <c:if test="${!empty requestScope.confermaDisattivazioneXsdUdAmmesso}">
                <div class="messages confermaDisattivazioneXsdUdAmmesso ">
                    <ul>
                        <li class="message info ">Desideri disattivare la versione di XSD del profilo?</li>
                    </ul>
                </div>
            </c:if>   
            <sl:contentTitle title="<%=StrutTipiForm.XsdModelliUdDetail.DESCRIPTION%>"/>
            
           
                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdModelliUdDetail'].status eq 'insert') && !(sessionScope['###_FORM_CONTAINER']['xsdModelliUdDetail'].status eq 'update') }">
	                <sl:form> 
	                    <sl:newLine skipLine="true"/>
	                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
	
	                    <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdModelliUdList'].table['empty']}">
	                        <slf:fieldBarDetailTag name="<%= StrutTipiForm.XsdModelliUdDetail.NAME%>" hideBackButton="true" hideInsertButton="true"/> 
	                    </c:if>   
	                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdModelliUdList'].table['empty']) }">
	                        <slf:listNavBarDetail name="<%= StrutTipiForm.XsdModelliUdList.NAME%>" />
	                    </c:if>
						
						<sl:newLine skipLine="true"/>
			            <slf:fieldSet >
			                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
			                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
			                    <sl:newLine />
			                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
			                </slf:section>
			                 <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer">  
			                     <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
			                      <sl:newLine />
			                      <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
			                  </slf:section>
            
                     	    <sl:newLine />
	                        <sl:newLine skipLine="true"/>
	                    	<h2><font color="#d3101c">Dettaglio profilo XSD ammesso</font></h2>
	                        <sl:newLine skipLine="true"/>
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.FL_STANDARD%>" colSpan="4" controlWidth="w40"/><sl:newLine />                                                
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_USO_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_USO_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.BL_XSD%>" colSpan="4" controlWidth="w100" />
	                        <sl:newLine />
	                    </slf:fieldSet>      
	                    <sl:newLine skipLine="true"/>
	                    <sl:pulsantiera>
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.SCARICA_XSD_MODELLI_UD_BUTTON%>" width="w50" />
	                    </sl:pulsantiera>                    
					</sl:form> 
	            </c:if>
	            
	            <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdModelliUdDetail'].status eq 'insert'}">
	               <sl:form>
	                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
	                    <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdModelliUdList'].table['empty']}">
	                        <slf:fieldBarDetailTag name="<%=  StrutTipiForm.XsdModelliUdDetail.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
	                    </c:if>   
	                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdModelliUdList'].table['empty']) }">
	                        <slf:listNavBarDetail name="<%=  StrutTipiForm.XsdModelliUdList.NAME%>"   />  
	                    </c:if>
	                    
						<sl:newLine skipLine="true"/>
			            <slf:fieldSet >
			                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
			                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
			                    <sl:newLine />
			                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
			                </slf:section>
			                 <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer">  
			                     <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
			                      <sl:newLine />
			                      <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
			                  </slf:section>
			            
	                   		<sl:newLine />
	                        <sl:newLine skipLine="true" />
	                        <h2><font color="#d3101c">Inserimento profilo XSD ammesso</font></h2>
	                        <sl:newLine skipLine="true"/>
	                        <!-- hidden fiels -->
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.ID_TIPO_UNI_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />	                        
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.ID_TIPO_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />	       
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.TI_USO_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />	        
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w40"/><sl:newLine />	                        	                       	               	                                       	                       	               
							<!--  -->	                        	                      
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.FL_STANDARD%>" colSpan="4" controlWidth="w40" tooltip="Campo non compilabile in quanto non utilizzato in fase di versamento" /><sl:newLine />                                                
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_USO_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_USO_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                      	<sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                    </slf:fieldSet>
	                    <sl:newLine skipLine="true"/>
	                    <sl:pulsantiera>
	                    	 <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.SCARICA_XSD_MODELLI_UD_BUTTON%>" width="w50" />
	                 	</sl:pulsantiera> 
	                  </sl:form>
	                <sl:newLine />
	                <sl:newLine skipLine="true"/>	               
	            </c:if>
	
	            <c:if test="${(sessionScope['###_FORM_CONTAINER']['xsdModelliUdDetail'].status eq 'update') }"> 
	                <sl:form>
	                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
	                    <c:if test="${sessionScope['###_FORM_CONTAINER']['xsdModelliUdList'].table['empty']}">
	                        <slf:fieldBarDetailTag name="<%= StrutTipiForm.XsdModelliUdDetail.NAME%>" hideInsertButton="true"/> 
	                    </c:if>   
	                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['xsdModelliUdList'].table['empty']) }">
	                        <slf:listNavBarDetail name="<%= StrutTipiForm.XsdModelliUdList.NAME%>"   />  
	                    </c:if>
	                    
						<sl:newLine skipLine="true"/>
			            <slf:fieldSet >
			                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
			                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
			                    <sl:newLine />
			                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
			                </slf:section>
			                 <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer">  
			                     <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
			                      <sl:newLine />
			                      <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
			                  </slf:section>
			            
		                    <sl:newLine />
	                        <sl:newLine skipLine="true" />
	                        <h2><font color="#d3101c">Modifica profilo XSD ammesso</font></h2>
	                        <sl:newLine skipLine="true"/>
	                         <!-- hidden fiels -->
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.ID_TIPO_UNI_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />	                        
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.ID_TIPO_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />	       
	                       	<slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.TI_USO_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />	
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w40"/><sl:newLine />	                        	                       	               	                                       	                       	               	                       	                        	                       	              
							<!--  -->	                        	                      
	                   	    <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.FL_STANDARD%>" colSpan="4" controlWidth="w40" tooltip="Campo non compilabile in quanto non utilizzato in fase di versamento" /><sl:newLine />                                                
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_USO_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                        <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.DT_USO_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
	                    </slf:fieldSet>
	                    <sl:newLine skipLine="true"/>
	                    <sl:pulsantiera>
	                    	 <slf:lblField name="<%=StrutTipiForm.XsdModelliUdDetail.SCARICA_XSD_MODELLI_UD_BUTTON%>" width="w50" />
	                 	</sl:pulsantiera> 
	                </sl:form>
	                <sl:newLine />
	                <sl:newLine skipLine="true"/>
	            </c:if>   
    	    </div>
        <sl:footer />
    </sl:body>
</sl:html>