<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Gestione Enti" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Enti" />
        <sl:menu />

        <sl:content>
            
             <sl:contentTitle title="Gestione Enti"/>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.VisEnte.NM_ENTE%>" colSpan="4" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=AmbienteForm.VisEnte.ID_AMBIENTE%>" colSpan="4" />
                <sl:newLine />   
                <slf:lblField name="<%=AmbienteForm.VisEnte.TIPO_DEF_TEMPLATE_ENTE%>" colSpan="4" />  
                <sl:newLine /> 
               
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <!-- piazzo il bottone di ricerca e pulisci -->
                <slf:lblField  name="<%=AmbienteForm.VisEnte.VIS_ENTE_BUTTON%>"  width="w50"/>
            </sl:pulsantiera>
                
            <sl:newLine skipLine="true"/>
            
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= AmbienteForm.EntiList.NAME %>" pageSizeRelated="true"/>
<slf:list name="<%= AmbienteForm.EntiList.NAME %>" />
            <slf:listNavBar  name="<%= AmbienteForm.EntiList.NAME %>" />
                    
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
