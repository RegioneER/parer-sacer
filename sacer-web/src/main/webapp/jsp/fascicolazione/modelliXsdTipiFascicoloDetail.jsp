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

<%@ page import="it.eng.parer.slite.gen.form.ModelliFascicoliForm" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sl:html>
    <sl:head title="Dettaglio XSD" >        
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
        <script type="text/javascript">
            $(document).ready(function () {
                
                $('#Id_ambiente').change(function () {
                    var value = $(this).val();
//                    if (value) {
                        $.getJSON("ModelliFascicoli.html", {operation: "triggerModelliXsdTipiFascicoloDetailId_ambienteOnTriggerJs",
                            Id_ambiente: value
                        }).done(function (data) {
                            CAjaxDataFormWalk(data);
                        });
//                    }
                });

            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <div id="content">
            <slf:messageBox />    
            <sl:contentTitle title="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DESCRIPTION%>"/>
            
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloDetail'].status eq 'insert') && !(sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloDetail'].status eq 'update') }">
                <sl:form> 
              <!--   <form id="spagoLiteAppForm" action="ModelliFascicoli.html" method="post" > -->
                    <sl:newLine skipLine="true"/>
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>

                    <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].table['empty']}">
                        <slf:fieldBarDetailTag name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NAME%>" hideBackButton="true" hideInsertButton="true"/> 
                    </c:if>   

                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloList.NAME%>" />
                    </c:if>

                    <sl:newLine skipLine="true"/>

                    <slf:fieldSet>
                        <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].status eq 'insert'}">
                                <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:when>
                            <c:otherwise>
                                <slf:lblField name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NM_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:otherwise>
                        </c:choose>
                        <sl:newLine skipLine="true" />
                            <h2><font color="#d3101c">Dettaglio XSD</font></h2>
                            <sl:newLine skipLine="true"/>
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.FL_DEFAULT%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                            <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.BL_XSD%>" colSpan="4" controlWidth="w100" />
                            <sl:newLine />
                    </slf:fieldSet>      
                    <sl:newLine skipLine="true"/>
                    <sl:pulsantiera>
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.LOG_EVENTI%>" />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.SCARICA_XSD_BUTTON%>" width="w50" />
                    </sl:pulsantiera>  
				</sl:form> 
               <!--  </form> -->
            </c:if>
            
            <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloDetail'].status eq 'insert'}">
               <!--  <form id="multipartForm" action="ModelliFascicoli.html" method="post" enctype="multipart/form-data" > -->
               <sl:form id="multipartForm" multipartForm="true">
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                    <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].table['empty']}">
                        <slf:fieldBarDetailTag name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NAME%>" hideInsertButton="true" hideBackButton="true"/> 
                    </c:if>   
                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloList.NAME%>"   />  
                    </c:if>
                    <sl:newLine />
                    <slf:fieldSet>
                        <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].status eq 'insert'}">
                                <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:when>
                            <c:otherwise>
                                <slf:lblField name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NM_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:otherwise>
                        </c:choose>
                        <sl:newLine skipLine="true" />
                        <h2><font color="#d3101c">Dettaglio XSD</font></h2>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.FL_DEFAULT%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
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

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloDetail'].status eq 'update') }"> 
               <!--  <form id="multipartForm" action="ModelliFascicoli.html" method="post" enctype="multipart/form-data" > -->
                <sl:form id="multipartForm" multipartForm="true">
                    <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                    <c:if test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].table['empty']}">
                        <slf:fieldBarDetailTag name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NAME%>" hideInsertButton="true"/> 
                    </c:if>   
                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].table['empty']) }">
                        <slf:listNavBarDetail name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloList.NAME%>"   />  
                    </c:if>
                    <sl:newLine />
                    <slf:fieldSet>
                        <c:choose>
                            <c:when test="${sessionScope['###_FORM_CONTAINER']['modelliXsdTipiFascicoloList'].status eq 'update'}">
                                <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.ID_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:when>
                            <c:otherwise>
                                <slf:lblField name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NM_AMBIENTE%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                            </c:otherwise>
                        </c:choose>
                        <sl:newLine skipLine="true" />
                        <h2><font color="#d3101c">Dettaglio XSD</font></h2>
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.TI_MODELLO_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.FL_DEFAULT%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.CD_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DS_XSD%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DT_ISTITUZ%>" colSpan="4" controlWidth="w10"/><sl:newLine />
                        <slf:lblField name="<%=ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.DT_SOPPRES%>" colSpan="4" controlWidth="w10"/><sl:newLine />
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
        </div>
        <sl:footer />
    </sl:body>
</sl:html>
