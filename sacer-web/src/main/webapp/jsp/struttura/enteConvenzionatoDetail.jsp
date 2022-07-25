<%@ page import="it.eng.parer.slite.gen.form.EntiConvenzionatiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%= EntiConvenzionatiForm.EnteConvenzOrg.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%= EntiConvenzionatiForm.EnteConvenzOrg.DESCRIPTION%>"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['enteConvenzOrgList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= EntiConvenzionatiForm.EnteConvenzOrg.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['enteConvenzOrgList'].status eq 'insert'}"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['enteConvenzOrgList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= EntiConvenzionatiForm.EnteConvenzOrgList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.ID_ENTE_CONVENZ_ORG%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.ID_AMBIENTE_ENTE_CONVENZ%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.ID_ENTE_CONVENZ%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.DT_INI_VAL%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=EntiConvenzionatiForm.EnteConvenzOrg.DT_FINE_VAL%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
