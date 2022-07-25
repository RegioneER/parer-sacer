<%@ page import="it.eng.parer.slite.gen.form.StrutTipoStrutForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo documento ammesso" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Dettaglio tipo documento ammesso" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />    

            <sl:contentTitle title="Dettaglio tipo documento ammesso"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoDocAmmessoDaTipoStrutDocList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipoStrutForm.TipoDocAmmessoDaTipoStrutDoc.NAME%>" hideBackButton="true" /> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoDocAmmessoDaTipoStrutDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipoStrutForm.TipoDocAmmessoDaTipoStrutDocList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:section name="<%=StrutTipoStrutForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.STipoStrutDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.NM_TIPO_STRUT_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.DS_TIPO_STRUT_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.STipoDocAmmesso.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoDocAmmessoDaTipoStrutDoc.ID_TIPO_DOC_AMMESSO%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
            </slf:fieldSet>
                
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>