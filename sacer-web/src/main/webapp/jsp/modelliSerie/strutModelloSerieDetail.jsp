<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.StrutModelloDetail.DESCRIPTION%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <slf:messageBox />
        <sl:content>
            <sl:contentTitle title="<%=ModelliSerieForm.StrutModelloDetail.DESCRIPTION%>"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%= ModelliSerieForm.StrutModelloDetail.NAME%>" hideBackButton="false" hideDeleteButton="false" hideDetailButton="true" hideUpdateButton="false" hideInsertButton="false"/>
            <slf:messageBox />

            <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            <slf:section name="<%=ModelliSerieForm.InfoModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                <slf:lblField name="<%=ModelliSerieForm.StrutModelloDetail.ID_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=ModelliSerieForm.StrutModelloDetail.ID_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            </slf:section>

            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=ModelliSerieForm.StrutModelloDetail.NM_STRUT%>" width="w100" controlWidth="w50" labelWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=ModelliSerieForm.StrutModelloDetail.NM_ENTE%>" width="w100" controlWidth="w50" labelWidth="w20" />
            </slf:fieldSet>
            <sl:pulsantiera>
                <slf:lblField name="<%=ModelliSerieForm.StrutModelloDetail.CERCA_STRUTTURE%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=ModelliSerieForm.StrutRicercateSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= ModelliSerieForm.StrutRicercateList.NAME%>" pageSizeRelated="true"/>
                <slf:selectList name="<%= ModelliSerieForm.StrutRicercateList.NAME%>" addList="true"/>
                <slf:listNavBar  name="<%= ModelliSerieForm.StrutRicercateList.NAME%>" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=ModelliSerieForm.StrutSelezionateSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= ModelliSerieForm.StrutSelezionateList.NAME%>" pageSizeRelated="true"/>
                <slf:selectList name="<%= ModelliSerieForm.StrutSelezionateList.NAME%>" addList="false"/>
                <slf:listNavBar  name="<%= ModelliSerieForm.StrutSelezionateList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

