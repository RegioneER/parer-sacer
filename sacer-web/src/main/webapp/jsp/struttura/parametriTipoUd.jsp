<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri tipo unità documentaria" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Parametri tipo unità documentaria"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri tipo unità documentaria"/>
            <slf:fieldBarDetailTag name="<%=StrutTipiForm.TipoUnitaDoc.NAME%>" hideBackButton="false" />     
             <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>

            <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer"> 
                <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/> 
                <sl:newLine />
                <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=StrutTipiForm.RicercaParametriTipoUd.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=StrutTipiForm.RicercaParametriTipoUd.RICERCA_PARAMETRI_TIPO_UD_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiForm.ParametriAmministrazioneTipoUdList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiForm.ParametriAmministrazioneTipoUdList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiForm.ParametriConservazioneTipoUdList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiForm.ParametriConservazioneTipoUdList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiForm.ParametriGestioneTipoUdList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiForm.ParametriGestioneTipoUdList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

