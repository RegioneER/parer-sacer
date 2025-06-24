<%@page import="it.eng.parer.slite.gen.form.SceltaOrganizzazioneForm" pageEncoding="UTF-8"%>
<%@page import="it.eng.spagoCore.ConfigSingleton"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Scelta struttura" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" showHomeBtn="false" showChangeOrganizationBtn="false" description=""/>
        <div class="toolBar">

            <h2 class="floatLeft">Scelta struttura</h2>
            <div class="right"> <h2><a title="Logout" href="Logout.html">
                        <img title="Logout" alt="Logout" src="<c:url value='/img/base/IconaLogout.png' />" style="padding-right: 5px;">Logout</a></h2></div>
        </div>

        <sl:content> 
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <div class="center">
                <div class="floatLeft" >
                    <c:if test="${empty requestScope.errore}" >
                        <sl:contentTitle title="<%=SceltaOrganizzazioneForm.Strutture.DESCRIPTION%>"/>
                    </c:if>
                </div>
                <sl:newLine skipLine="true"/>
                <slf:fieldSet borderHidden="true" styleClass="">
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%=SceltaOrganizzazioneForm.Strutture.ID_AMBIENTE%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%=SceltaOrganizzazioneForm.Strutture.ID_ENTE%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField colSpan="4" width="w80" labelWidth="w20" name="<%=SceltaOrganizzazioneForm.Strutture.ID_STRUT%>" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField position="left" width="auto" name="<%=SceltaOrganizzazioneForm.Strutture.SELEZIONA_STRUTTURA%>" />
                </slf:fieldSet>
            </div>
        </sl:content>
        <!--Footer-->
        <sl:footer />
    </sl:body>
</sl:html>

