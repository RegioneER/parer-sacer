<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio parametri periodo tipo fascicolo" >
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Parametri periodo tipo fascicolo" />
        <sl:menu />
        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio parametri periodo tipo fascicolo"/>            
            <slf:fieldBarDetailTag name="<%=StrutTipiFascicoloForm.AaTipoFascicoloDetail.NAME%>" hideBackButton="false" />
            <slf:section name="<%=StrutTipiFascicoloForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.STRUTTURA%>"  width="w100" controlWidth="w40" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.StrutRif.ID_ENTE%>"  width="w100" controlWidth="w80" labelWidth="w20"/>
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=StrutTipiFascicoloForm.TipoFascicoloSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.NM_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.TipoFascicoloDetail.DS_TIPO_FASCICOLO%>" width="w100" labelWidth="w20" controlWidth="w70"/>
                </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=StrutTipiFascicoloForm.RicercaParametriAaTipoFasc.FUNZIONE%>" colSpan="2"/>                              
                <slf:lblField name="<%=StrutTipiFascicoloForm.RicercaParametriAaTipoFasc.RICERCA_PARAMETRI_AA_TIPO_FASC_BUTTON%>" colSpan="2"/>                              
            </sl:pulsantiera>                     
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriAmministrazioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParametriAmministrazioneAaTipoFascList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriConservazioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParametriConservazioneAaTipoFascList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiFascicoloForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <slf:editableList name="<%= StrutTipiFascicoloForm.ParametriGestioneAaTipoFascList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= StrutTipiFascicoloForm.ParametriGestioneAaTipoFascList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

