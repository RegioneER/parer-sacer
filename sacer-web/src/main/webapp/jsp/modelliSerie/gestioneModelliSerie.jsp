<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.ModelliTipiSerieList.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=ModelliSerieForm.ModelliTipiSerieList.DESCRIPTION%>"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%=ModelliSerieForm.ModelliTipiSerieList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%=ModelliSerieForm.ModelliTipiSerieList.NAME%>" abbrLongList="true"/>
            <slf:listNavBar name="<%=ModelliSerieForm.ModelliTipiSerieList.NAME%>" /> 

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>