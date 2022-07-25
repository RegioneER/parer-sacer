<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo documento ammesso" >
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo documento ammesso"/>
            <sl:newLine skipLine="true"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoDocAmmessoList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoDocAmmesso.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoDocAmmessoList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiForm.TipoDocAmmessoList.NAME%>" /> 
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <sl:newLine />
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>

                <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>

                <slf:section name="<%=StrutTipiForm.STipoStrutUnitaDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.NM_TIPO_STRUT_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutUnitaDoc.DS_TIPO_STRUT_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>
                <slf:section name="<%=StrutTipiForm.STipoDocAmmesso.NAME%>" styleClass="importantContainer"> 
                    <c:choose>
                        <c:when test="${ sessionScope['###_FORM_CONTAINER']['tipoDocAmmessoList'].status eq 'insert' }">
                            <slf:lblField name="<%=StrutTipiForm.TipoDocAmmesso.ID_TIPO_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                        </c:when>
                        <c:otherwise>
                            <slf:lblField name="<%=StrutTipiForm.TipoDocAmmesso.NM_TIPO_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                        </c:otherwise>
                    </c:choose>
                    <slf:lblField name="<%=StrutTipiForm.TipoDocAmmesso.TI_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDocAmmesso.FL_OBBL%>" colSpan= "2" labelWidth="w20" controlWidth="w70" /><sl:newLine />
                </slf:section>
            </slf:fieldSet>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>


