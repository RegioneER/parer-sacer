<%@ page import="it.eng.parer.slite.gen.form.FormatiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>




<sl:html>
    <sl:head title="Estensione File Detail" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
<slf:messageBox/>

            <sl:contentTitle title="Dettaglio Estensione File"/>

           
            <c:if test="${sessionScope['###_FORM_CONTAINER']['estensioneFileList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= FormatiForm.EstensioneFile.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['estensioneFileList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= FormatiForm.EstensioneFileList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.NM_FORMATO_FILE_STANDARD%>" colSpan="4" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.EstensioneFile.CD_ESTENSIONE_FILE%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
                <slf:lblField name="<%=FormatiForm.EstensioneFile.ID_ESTENSIONE_FILE%>" colSpan="4" controlWidth="w40"/>
            </slf:fieldSet>
            
        </sl:content>
        <sl:footer />

    </sl:body>

</sl:html>