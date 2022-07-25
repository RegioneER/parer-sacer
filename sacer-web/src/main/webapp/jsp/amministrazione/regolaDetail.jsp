<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTipiForm.RegolaSubStrut.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:choose>
                <c:when test="${fn:contains(fn:escapeXml(sessionScope.provenienzaRegola),'tipoUnitaDocDetail')}">
                    <c:set scope="request" var="title" value="su tipo unit&agrave; documentarie" />
                </c:when>
                <c:otherwise>
                    <c:set scope="request" var="title" value="su tipo documento principale" />
                </c:otherwise>
            </c:choose>
            <sl:contentTitle title='<%=StrutTipiForm.RegolaSubStrut.DESCRIPTION + request.getAttribute("title")  %>' />
            <%--${fn:escapeXml(title)}--%>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['regoleSubStrutList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.RegolaSubStrut.NAME%>" hideBackButton="true" /> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['regoleSubStrutList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>" /> 
            </c:if>
            <sl:newLine skipLine="true"/>
            <sl:newLine />
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
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['regoleSubStrutList'].status eq 'view') }"> 
                <!--  piazzo la lista con i risultati -->
                <div class="livello1"><b><font color="#d3101c">Campi</font></b></div>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= StrutTipiForm.CampiSubStrutList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StrutTipiForm.CampiSubStrutList.NAME%>"  />
                <slf:listNavBar  name="<%= StrutTipiForm.CampiSubStrutList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>