<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Importa Struttura" >
        <script type="text/javascript" src="<c:url value='/js/sips/customStruttureMessageBox.js'/>" ></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <%--        <div id="content">
                    <form id="multipartForm" action="Strutture.html" method="post" enctype="multipart/form-data" >--%>

        <sl:content multipartForm="true">

            <slf:messageBox />

            <%@ include file="mascheraCreazioneStruttureTemplate.jspf"%>

            <sl:contentTitle title="Importa Struttura"/>
            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%=StruttureForm.InsStruttura.NAME%>"  />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_SOST_STRUT%>" width="w50" controlWidth="w50" labelWidth="w40" />
                <sl:newLine />   
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CHECK_DUP_STRUT%>" width="w50" controlWidth="w50" labelWidth="w40" />
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
                <slf:lblField  name="<%=StruttureForm.InsStruttura.BL_XML_STRUT%>" width="w50" controlWidth="w50" labelWidth="w40" />
            </slf:fieldSet>

            <sl:pulsantiera>
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CONFERMA_IMPORTA_STRUTTURA%>"  width="w50" />
                <slf:lblField  name="<%=StruttureForm.InsStruttura.CREA_STRUTTURE_TEMPLATE%>"  width="w50" /> 
            </sl:pulsantiera> 

            <%-- </form>
         </div> --%>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
