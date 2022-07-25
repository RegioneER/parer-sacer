<%@ page import="it.eng.parer.slite.gen.form.SubStruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=SubStruttureForm.SubStrut.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />
            <sl:contentTitle title="<%=SubStruttureForm.SubStrut.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['subStrutList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= SubStruttureForm.SubStrut.NAME%>" hideBackButton="true"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['subStrutList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= SubStruttureForm.SubStrutList.NAME%>" />   
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=SubStruttureForm.Struttura.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=SubStruttureForm.StrutRif.STRUTTURA%>" width="w100" labelWidth="w10" controlWidth="w90" />
                <sl:newLine />
                <slf:lblField name="<%=SubStruttureForm.StrutRif.ID_ENTE%>" width="w100" labelWidth="w10" controlWidth="w90"/>
            </slf:section>
            <slf:section name="<%=SubStruttureForm.SubStrutturaSection.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=SubStruttureForm.SubStrut.NM_SUB_STRUT%>" width="w100" labelWidth="w10" controlWidth="w90" />
                <sl:newLine />
                <slf:lblField name="<%=SubStruttureForm.SubStrut.DS_SUB_STRUT%>" width="w100" labelWidth="w10" controlWidth="w90"/>
            </slf:section>
            <sl:newLine />
            <slf:section name="<%=SubStruttureForm.RegoleSubStrutturaSection.NAME%>" styleClass="importantContainer">  
                <slf:list name="<%= SubStruttureForm.RegoleSubStrutList.NAME%>"  />
                <slf:listNavBar  name="<%= SubStruttureForm.RegoleSubStrutList.NAME%>" />
            </slf:section>
        </sl:content>

        <sl:footer />
    </sl:body>
</sl:html>