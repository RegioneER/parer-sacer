<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.DESCRIPTION%>" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <sl:contentTitle title="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.DESCRIPTION%>"/>
            <c:choose>
                <c:when test="${sessionScope['###_FORM_CONTAINER']['noteModelloTipoSerieList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= ModelliSerieForm.NoteModelloTipoSerieDetail.NAME%>" hideBackButton="${sessionScope['###_FORM_CONTAINER']['noteModelloTipoSerieList'].status eq 'insert'}"/> 
                </c:when>   
                <c:otherwise>
                    <slf:listNavBarDetail name="<%= ModelliSerieForm.NoteModelloTipoSerieList.NAME%>" />   
                </c:otherwise>
            </c:choose>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.ID_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:section name="<%=ModelliSerieForm.InfoModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=ModelliSerieForm.NotaModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                    <slf:lblField name="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.ID_NOTA_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.ID_TIPO_NOTA_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.PG_NOTA_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.DT_NOTA_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=ModelliSerieForm.NoteModelloTipoSerieDetail.DS_NOTA_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>