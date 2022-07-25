<%@ page import="it.eng.parer.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=AmministrazioneForm.DettaglioSistemaMigrazione.DESCRIPTION%>" />
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title='<%=AmministrazioneForm.DettaglioSistemaMigrazione.DESCRIPTION %>' />
            <c:if test="${sessionScope['###_FORM_CONTAINER']['sistemiMigrazioneList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= AmministrazioneForm.DettaglioSistemaMigrazione.NAME%>" hideBackButton="true" /> 
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['sistemiMigrazioneList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmministrazioneForm.SistemiMigrazioneList.NAME%>" /> 
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                    <slf:lblField name="<%=AmministrazioneForm.DettaglioSistemaMigrazione.ID_SISTEMA_MIGRAZ%>" colSpan= "2"/> 
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.DettaglioSistemaMigrazione.NM_SISTEMA_MIGRAZ%>" colSpan= "2"/> 
                    <sl:newLine />
                    <slf:lblField name="<%=AmministrazioneForm.DettaglioSistemaMigrazione.DS_SISTEMA_MIGRAZ%>" colSpan= "2"/>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>