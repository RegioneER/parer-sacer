<%@ page import="it.eng.parer.slite.gen.form.FormatiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Formato File Busta Detail" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />  
            <sl:contentTitle title="Dettaglio Formato File Busta"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['formatoFileBustaList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= FormatiForm.FormatoFileBusta.NAME%>" hideBackButton="true"/> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['formatoFileBustaList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= FormatiForm.FormatoFileBustaList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=FormatiForm.FormatoFileBusta.ID_FORMATO_FILE_BUSTA%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.NM_FORMATO_FILE_STANDARD%>" colSpan="4" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileBusta.TI_FORMATO_FIRMA_MARCA%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
                <slf:lblField name="<%=FormatiForm.FormatoFileBusta.DS_FORMATO_FIRMA_MARCA%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>