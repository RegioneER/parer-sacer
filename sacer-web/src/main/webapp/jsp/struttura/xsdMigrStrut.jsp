<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="table" value="${!empty param.table}" />

<sl:html>
    <sl:head title="Dettaglio Xsd Migrazione">
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="Gestione Xsd Migrazione"/>
            <slf:fieldBarDetailTag name="<%= StruttureForm.GestioneXsdMigrazione.NAME%>" 
                                   hideBackButton="${!(sessionScope['###_FORM_CONTAINER']['gestioneXsdMigrazione'].status eq 'view')}"
                                   hideUpdateButton="${(sessionScope['###_FORM_CONTAINER']['gestioneXsdMigrazione'].status eq 'view')}" hideDeleteButton="true"/> 
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:section name="<%=StruttureForm.Descrizione.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.InsStruttura.NM_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.DS_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=StruttureForm.GestioneXsdMigrazione.NM_SISTEMA_MIGRAZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <slf:lblField name="<%=StruttureForm.GestioneXsdMigrazione.ID_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <c:if test="${sessionScope['###_FORM_CONTAINER']['gestioneXsdMigrazione'].status eq 'view'}">
                <slf:tab  name="<%=StruttureForm.XsdMigrStrutTab.NAME%>" tabElement="<%=StruttureForm.XsdMigrStrutTab.xsd_migr_tipo_unita_doc%>">
                    <slf:listNavBar name="<%= StruttureForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StruttureForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StruttureForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%=StruttureForm.XsdMigrStrutTab.NAME%>" tabElement="<%=StruttureForm.XsdMigrStrutTab.xsd_migr_tipo_doc%>">
                    <slf:listNavBar name="<%= StruttureForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StruttureForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StruttureForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
                <slf:tab  name="<%=StruttureForm.XsdMigrStrutTab.NAME%>" tabElement="<%=StruttureForm.XsdMigrStrutTab.xsd_migr_tipo_comp_doc%>">
                    <slf:listNavBar name="<%= StruttureForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StruttureForm.XsdDatiSpecList.NAME%>"   />
                    <slf:listNavBar  name="<%= StruttureForm.XsdDatiSpecList.NAME%>" />
                </slf:tab>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
