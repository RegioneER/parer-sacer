<%@ page import="it.eng.parer.slite.gen.form.ModelliFascicoliForm" pageEncoding="UTF-8" %>
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
            <sl:contentTitle title="RICERCA MODELLI DI XSD DEI TIPI FASCICOLO"/>
            <c:if test="${((fn:length(sessionScope['###_NAVHIS_CONTAINER'])) gt 1 )}">
                <slf:fieldBarDetailTag name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloDetail.NAME%>" hideOperationButton="true" />
            </c:if>
            <slf:fieldSet>
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />                    
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.TI_MODELLO_XSD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.FL_DEFAULT %>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.CD_XSD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.DS_XSD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.ATTIVO_XSD %>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo.RICERCA_MODELLI_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloList.NAME%>" />
            <slf:listNavBar  name="<%= ModelliFascicoliForm.ModelliXsdTipiFascicoloList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>