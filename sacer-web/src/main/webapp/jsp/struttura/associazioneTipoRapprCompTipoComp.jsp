<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo componente ammesso nel tipo di rappresentazione componente" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Dettaglio tipo componente" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo componente ammesso nel tipo di rappresentazione componente"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoCompAmmessoDaTipoRapprCompList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprComp.NAME%>" hideBackButton="true" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoCompAmmessoDaTipoRapprCompList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StruttureForm.TipoCompAmmessoDaTipoRapprCompList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StruttureForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.InsStruttura.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.InsStruttura.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoRapprComp.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.ID_TIPO_RAPPR_COMP%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoRapprComp.NM_TIPO_RAPPR_COMP%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoStrutDoc.NAME%>" styleClass="importantContainer">  
                    <sl:newLine />
                    <slf:lblField name="<%=StruttureForm.TipoCompAmmessoDaTipoRapprComp.ID_TIPO_STRUT_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StruttureForm.STipoRapprComp.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.TipoCompAmmessoDaTipoRapprComp.ID_TIPO_COMP_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>