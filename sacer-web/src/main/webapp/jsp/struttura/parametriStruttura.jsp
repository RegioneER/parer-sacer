<%@ page import="it.eng.parer.slite.gen.form.StruttureForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri struttura" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Parametri struttura"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri struttura"/>
            <slf:fieldBarDetailTag name="<%=StruttureForm.InsStruttura.NAME%>" hideBackButton="false" />
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StruttureForm.StrutturaSection.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_STRUT%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <sl:newLine />
                <slf:lblField name="<%=StruttureForm.InsStruttura.VIEW_NM_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=StruttureForm.RicercaParametriStruttura.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=StruttureForm.RicercaParametriStruttura.RICERCA_PARAMETRI_STRUTTURA_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StruttureForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StruttureForm.ParametriAmministrazioneStrutturaList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StruttureForm.ParametriAmministrazioneStrutturaList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StruttureForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StruttureForm.ParametriConservazioneStrutturaList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StruttureForm.ParametriConservazioneStrutturaList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StruttureForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StruttureForm.ParametriGestioneStrutturaList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StruttureForm.ParametriGestioneStrutturaList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

