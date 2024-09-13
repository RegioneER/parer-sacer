<%@ page import="it.eng.parer.slite.gen.form.UtilizzoMicroserviziForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Utilizzo Microservizi" ></sl:head>
    <sl:body>
        <sl:header showChangeOrganizationBtn="false"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox />    
            <sl:contentTitle title="Utilizzo microservizi"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldSet legend="Dati di accesso ai microservizi" >
                <slf:lblField name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziFields.NM_URL_TOKEN%>" colSpan="4" controlWidth="w100"/> 
            </slf:fieldSet> 
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziSection.NAME%>" styleClass="importantContainer">
                <!--  piazzo la lista con i risultati -->
                <slf:listNavBar name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziList.NAME%>" />
                <slf:listNavBar  name="<%=UtilizzoMicroserviziForm.DatiUtilizzoMicroserviziList.NAME%>" />
            </slf:section>
        <sl:footer />
        </sl:content>
    </sl:body>
</sl:html>