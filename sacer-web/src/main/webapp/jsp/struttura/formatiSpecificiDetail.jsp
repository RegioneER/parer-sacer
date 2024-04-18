<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Gestione formati specifici" >       
        <script type="text/javascript" src="<c:url value='/js/sips/customDeleteFormatiSpecificiMessageBox.js'/>" ></script>        
    </sl:head>

    <c:set var="desc" value="Seleziona i formati specifici da eliminare" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Formati specifici" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.customDeleteFormatiSpecifici}">
                <div class="messages customDeleteFormatiSpecificiMessageBox ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.messaggioDeleteFormatiSpecifici}"/></li>
                    </ul>   
                </div>
            </c:if>
            <sl:contentTitle title="Gestione formati specifici"/>        
            <slf:fieldBarDetailTag name="<%= StruttureForm.InsStruttura.NAME%>" hideBackButton="false" /> 
            <div class="newLine skipLine"></div>
            <slf:fieldSet>
                <slf:section name="<%=StruttureForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>
            </slf:fieldSet> 
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StruttureForm.FormatiSpecifici.NAME%>" styleClass="importantContainer"> 
                <slf:listNavBar name="<%=StruttureForm.FormatoFileDocSpecificoList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=StruttureForm.FormatoFileDocSpecificoList.NAME%>" />
                <slf:listNavBar name="<%=StruttureForm.FormatoFileDocSpecificoList.NAME%>" />
            </slf:section>            
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>