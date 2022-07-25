<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Dettaglio Categorie Strutture" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>


            <slf:messageBox />
            <sl:contentTitle title="Dettaglio Categorie Strutture"/>

            <sl:newLine skipLine="true"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['categorieStruttureList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StruttureForm.CategorieStrutture.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['categorieStruttureList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StruttureForm.CategorieStruttureList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:lblField name="<%=StruttureForm.CategorieStrutture.CD_CATEG_STRUT%>" colSpan="4"  />
                <sl:newLine />   
                <slf:lblField name="<%=StruttureForm.CategorieStrutture.DS_CATEG_STRUT%>" colSpan="4" />
                <sl:newLine />   
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
