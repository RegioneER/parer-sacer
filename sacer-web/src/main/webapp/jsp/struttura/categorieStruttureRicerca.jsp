<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Ricerca Categorie Strutture" />

    <sl:body>

        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <sl:contentTitle title="Ricerca Categorie Strutture"/>
            <slf:messageBox />      
            <slf:fieldSet >
                <slf:lblField name="<%=StruttureForm.CategorieStrutture.CD_CATEG_STRUT%>" colSpan="4"  />
                <sl:newLine />   
                <slf:lblField name="<%=StruttureForm.CategorieStrutture.DS_CATEG_STRUT%>" colSpan="4" />
                <sl:newLine />   
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <sl:pulsantiera>
                <slf:lblField  name="<%=StruttureForm.CategorieStrutture.RICERCA_CATEGORIE_STRUT_BUTTON%>"  width="w50" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <slf:listNavBar name="<%= StruttureForm.CategorieStruttureList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StruttureForm.CategorieStruttureList.NAME%>" />
            <slf:listNavBar  name="<%= StruttureForm.CategorieStruttureList.NAME%>" />
                
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
