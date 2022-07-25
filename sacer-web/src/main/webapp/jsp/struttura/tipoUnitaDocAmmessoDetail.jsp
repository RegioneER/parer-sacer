<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <c:choose>
        <c:when test="${param.table=='RegistroTipoUnitaDocAmmessoList'}">
            <c:set scope="request" var="titlePage" value="Dettaglio tipologia documentaria ammessa" />
        </c:when>
        <c:otherwise>
            <c:set scope="request" var="titlePage" value="Tipo registro ammesso" />
        </c:otherwise>
    </c:choose>
    <sl:head title="${fn:escapeXml(titlePage)}" >
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Registri associati" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    

            <sl:contentTitle title="${fn:escapeXml(titlePage)}"/>

            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>
            
            <c:if test="${param.table=='RegistroTipoUnitaDocAmmessoList'}">
                <c:if test="${sessionScope['###_FORM_CONTAINER']['registroTipoUnitaDocAmmessoList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoUnitaDocAmmesso.NAME%>" hideBackButton="false" /> 
                </c:if>   
                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['registroTipoUnitaDocAmmessoList'].table['empty']) }">
                    <slf:listNavBarDetail name="<%= StrutTipiForm.RegistroTipoUnitaDocAmmessoList.NAME%>" /> 
                </c:if>
            </c:if>
            <c:if test="${param.table=='TipoUnitaDocAmmessoList'}">
                <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoUnitaDocAmmessoList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoUnitaDocAmmesso.NAME%>" hideBackButton="false" /> 
                </c:if>   
                <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoUnitaDocAmmessoList'].table['empty']) }">
                    <slf:listNavBarDetail name="<%= StrutTipiForm.TipoUnitaDocAmmessoList.NAME%>" /> 
                </c:if>
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <c:choose>                   
                    <c:when test="${param.table=='RegistroTipoUnitaDocAmmessoList'}">
                        <slf:section name="<%=StrutTipiForm.SRegistroUnitaDoc.NAME%>" styleClass="importantContainer">  
                            <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CD_REGISTRO_UNITA_DOC%>" colSpan ="2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                            <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DS_REGISTRO_UNITA_DOC%>" colSpan ="2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                            <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.FL_REGISTRO_FISC%>" colSpan ="1" labelWidth="w20" controlWidth="w20"/> <sl:newLine />
                            <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DT_ISTITUZ%>" colSpan ="2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                            <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DT_SOPPRES%>" colSpan ="2" labelWidth="w20" controlWidth="w70"/> 
                        </slf:section>
                        <slf:section name="<%=StrutTipiForm.STipoUnitaDocAmmesso.NAME%>" styleClass="importantContainer">  
                            <slf:lblField name="<%=StrutTipiForm.TipoUnitaDocAmmesso.ID_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        </slf:section>
                    </c:when>
                    <c:otherwise>
                        <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer">  
                            <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                            <sl:newLine />
                            <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                        </slf:section>
                        <slf:section name="<%=StrutTipiForm.STipoRegistroAmmesso.NAME%>" styleClass="importantContainer">  
                            <slf:lblField name="<%=StrutTipiForm.TipoUnitaDocAmmesso.ID_REGISTRO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        </slf:section>
                    </c:otherwise>
                </c:choose>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDocAmmesso.CREA_TIPO_SERIE_STANDARD%>" width="w20"/>
                </sl:pulsantiera>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>