<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Duplica Struttura" >
        <script type="text/javascript" src="<c:url value='/js/sips/customStruttureMessageBox.js'/>" ></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <slf:messageBox />

            <%@ include file="mascheraCreazioneStruttureTemplate.jspf"%>

            <sl:contentTitle title="Duplica Struttura"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=StruttureForm.CheckDuplicaStruttura.NAME%>" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >

                <slf:lblField name="<%=StruttureForm.CheckDuplicaStruttura.CHECK_SOST_STRUT%>" labelWidth="w40" controlWidth="w50"/>
                <sl:newLine />
                <slf:lblField name="<%=StruttureForm.CheckDuplicaStruttura.CHECK_DUP_STRUT%>" labelWidth="w40" controlWidth="w50"/>
                <sl:newLine />
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_INCLUDI_CRITERI%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />   
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_INCLUDI_FORMATI%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />   
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_INCLUDI_TIPI_FASCICOLO%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />   
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_INCLUDI_ELEMENTI_DISATTIVI%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />   
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_MANTIENI_DATE_FINE_VALIDITA%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_INCLUDI_SISTEMI_MIGRAZ%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />   
                
                
                <sl:pulsantiera>
                    <slf:lblField  name="<%=StruttureForm.CheckDuplicaStruttura.CONFERMA_SCELTA_DUP%>"  width="w50" />
                    <slf:lblField  name="<%=StruttureForm.InsStruttura.CREA_STRUTTURE_TEMPLATE%>"  width="w50" /> 
                </sl:pulsantiera>

            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
