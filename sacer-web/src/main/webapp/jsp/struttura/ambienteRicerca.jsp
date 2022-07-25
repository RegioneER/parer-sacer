<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Gestione Ambienti" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Gestione Ambienti"/>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.VisAmbiente.NM_AMBIENTE%>" colSpan="4" controlWidth="w40" />
                <sl:newLine skipLine="true"/>
            </slf:fieldSet>

            <sl:pulsantiera>

                <slf:lblField  name="<%=AmbienteForm.VisAmbiente.VIS_AMBIENTE_BUTTON%>" colSpan="4" />

            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= AmbienteForm.AmbientiList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= AmbienteForm.AmbientiList.NAME%>" />
            <slf:listNavBar  name="<%= AmbienteForm.AmbientiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
