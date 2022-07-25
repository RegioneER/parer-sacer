<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Dettaglio Categorie Enti" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>


            <slf:messageBox />
            <sl:contentTitle title="Dettaglio Categorie Enti"/>

            <sl:newLine skipLine="true"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['categorieEntiList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StruttureForm.CategorieEnti.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['categorieEntiList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StruttureForm.CategorieEntiList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:lblField name="<%=StruttureForm.CategorieEnti.CD_CATEG_ENTE%>" colSpan="4"  />
                <sl:newLine />   
                <slf:lblField name="<%=StruttureForm.CategorieEnti.DS_CATEG_ENTE%>" colSpan="4" />
                <sl:newLine />   
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
