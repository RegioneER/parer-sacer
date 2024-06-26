<%@page import="it.eng.spagoLite.security.menu.MenuEntry"%>
<%@page import="it.eng.spagoLite.security.User"%>
<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioSinteticoForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head  title="Monitoraggio - Riepilogo versamenti sintetico" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="LISTA VERSAMENTI ANNULLATI UNITÀ DOCUMENTARIE"/>

            <% User u = (User) request.getSession().getAttribute("###_USER_CONTAINER");
                int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
                String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
                if (!lastMenuEntry.contains("RiepilogoVersamentiSintetico")) {%>                 
            <slf:fieldBarDetailTag name="<%= MonitoraggioSinteticoForm.RiepilogoVersamentiSintetico.NAME%>" hideOperationButton="true" />                 
            <% }%>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi di selezione -->
                <slf:lblField name="<%=MonitoraggioSinteticoForm.RiepilogoVersamentiSintetico.ID_AMBIENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioSinteticoForm.RiepilogoVersamentiSintetico.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioSinteticoForm.RiepilogoVersamentiSintetico.ID_STRUT%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioSinteticoForm.RiepilogoVersamentiSintetico.ID_TIPO_UNITA_DOC%>" colSpan="2" />                   
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=MonitoraggioSinteticoForm.RiepilogoVersamentiSintetico.GENERA_RIEPILOGO_VERS_SINTETICO_BUTTON%>" width="w20" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <!-- Sezione unità documentarie versate -->
            <%@ include file="unitaDocVersate.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- Sezione documenti aggiunti -->
            <%@ include file="docAggiunti.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>
            <!-- Sezione versamenti falliti, visualizzabile solo in caso idTipoUnitaDoc sia nullo -->
            <% String id = request.getParameter("Id_tipo_unita_doc");
                pageContext.setAttribute("Id_tipo_unita_doc", id);
            %>
            <c:if test="${empty Id_tipo_unita_doc}">
                <%@ include file="versFalliti.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
                <%@ include file="aggFallite.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
                <%@ include file="udVersFalliti.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
                <%@ include file="docAggFallite.jspf"%>
                <sl:newLine skipLine="true"/>
                <sl:newLine skipLine="true"/>
            </c:if>

            <%@ include file="udAnnul.jspf"%>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>