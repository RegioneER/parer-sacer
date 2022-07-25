<%@ page import="it.eng.parer.slite.gen.form.ModelliUDForm" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<sl:html>
    <sl:head title="Ricerca Modelli XSD" >        
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />             
            <sl:contentTitle title="RICERCA MODELLI DI XSD DELLE UNIT&Agrave; DOCUMENTARIE"/>
            <c:if test="${((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}">
                <slf:fieldBarDetailTag name="<%= ModelliUDForm.ModelliXsdUdDetail.NAME%>" hideOperationButton="true" />
            </c:if>
            <slf:fieldSet>
                <slf:lblField name="<%=ModelliUDForm.FiltriModelliXsdUd.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />                    
                <slf:lblField name="<%=ModelliUDForm.FiltriModelliXsdUd.TI_MODELLO_XSD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliUDForm.FiltriModelliXsdUd.FL_DEFAULT %>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliUDForm.FiltriModelliXsdUd.CD_XSD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliUDForm.FiltriModelliXsdUd.DS_XSD%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=ModelliUDForm.FiltriModelliXsdUd.RICERCA_MODELLI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ModelliUDForm.ModelliXsdUdList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ModelliUDForm.ModelliXsdUdList.NAME%>" />
            <slf:listNavBar  name="<%= ModelliUDForm.ModelliXsdUdList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>