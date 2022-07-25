<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTipiForm.CampoSubStrut.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />    
            <sl:contentTitle title="<%=StrutTipiForm.CampoSubStrut.DESCRIPTION%>"/>

            <c:choose>
                <c:when test="${sessionScope['###_FORM_CONTAINER']['campiSubStrutList'].status eq 'insert'||
                        sessionScope['###_FORM_CONTAINER']['campiSubStrutList'].status eq 'update'}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiForm.CampoSubStrut.NAME%>" hideBackButton="true" />
                </c:when>
                <c:otherwise>
                    <%--<slf:fieldBarDetailTag name="<%= StrutTipiForm.CampoSubStrut.NAME%>" hideBackButton="false" />--%>
                    <slf:listNavBarDetail name="<%= StrutTipiForm.CampiSubStrutList.NAME%>" /> 
                </c:otherwise>                
            </c:choose>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>

                <slf:section name="<%=StrutTipiForm.SRegola.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/> 
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegolaSubStrut.ID_TIPO_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.NM_TIPO_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/> 
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DS_TIPO_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegolaSubStrut.ID_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegolaSubStrut.DT_ISTITUZ%>" colSpan= "2" labelWidth="w30" controlWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegolaSubStrut.DT_SOPPRES%>" colSpan= "2" labelWidth="w30" controlWidth="w20"/>
                </slf:section>

                <slf:section name="<%=StrutTipiForm.SCampo.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.CampoSubStrut.TI_CAMPO%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.CampoSubStrut.NM_CAMPO%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.CampoSubStrut.ID_SUB_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.CampoSubStrut.ID_ATTRIB_DATI_SPEC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>




        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>