<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
 <sl:head title="Dettaglio tipo struttura documento ammesso" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Dettaglio tipo struttura documento ammesso" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio tipo struttura documento ammesso"/>
           <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoStrutDocAmmessoDaTipoDocList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoStrutDocAmmessoDaTipoDoc.NAME%>" hideBackButton="true" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoStrutDocAmmessoDaTipoDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipiForm.TipoStrutDocAmmessoDaTipoDocList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.STipoDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.ID_TIPO_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.NM_TIPO_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoDoc.DS_TIPO_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.STipoStrutDocAmmesso.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.TipoStrutDocAmmessoDaTipoDoc.ID_TIPO_STRUT_DOC_AMMESSO%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>