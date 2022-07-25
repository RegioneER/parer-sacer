<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Ricerca Categorie Enti" />

    <sl:body>

        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <sl:contentTitle title="Ricerca Categorie Enti"/>
            <slf:messageBox />      
            <slf:fieldSet>
                <slf:lblField name="<%=StruttureForm.CategorieEnti.CD_CATEG_ENTE%>" colSpan="4"  />
                <sl:newLine />   
                <slf:lblField name="<%=StruttureForm.CategorieEnti.DS_CATEG_ENTE%>" colSpan="4" />
                <sl:newLine />   
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <sl:pulsantiera>
                <slf:lblField  name="<%=StruttureForm.CategorieEnti.RICERCA_CATEGORIE_ENTE_BUTTON%>"  width="w50" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <slf:listNavBar name="<%= StruttureForm.CategorieEntiList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StruttureForm.CategorieEntiList.NAME%>" />
            <slf:listNavBar  name="<%= StruttureForm.CategorieEntiList.NAME%>" />
                
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
