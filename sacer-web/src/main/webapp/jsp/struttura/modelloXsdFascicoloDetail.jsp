<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DESCRIPTION%>" >        
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />             
            <sl:contentTitle title="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DESCRIPTION%>"/>
            <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.NAME%>" hideBackButton="false" />
            <sl:newLine skipLine="true"/>
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.NM_AMBIENTE%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.TI_MODELLO_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.TI_USO_MODELLO_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.CD_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DS_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DT_ISTITUZ%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.DT_SOPPRES%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ModelloXsdFascicoloDetail.BL_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                </slf:section>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>