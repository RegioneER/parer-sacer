<%@ page import="it.eng.parer.slite.gen.form.StrutTipiFascicoloForm" pageEncoding="UTF-8" %>
<%@ include file="../../include.jsp"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set scope="request" var="navTable" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" />
<sl:html>
    <sl:head title="Dettaglio XSD" >        

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi fascicolo" />
        <sl:menu />
        <sl:content>
         <div><input name="mainNavTable" type="hidden" value="${(empty param.mainNavTable) ? (fn:escapeXml(param.table)) : (fn:escapeXml(param.mainNavTable))  }" /></div>         
            <slf:messageBox />             
            <sl:contentTitle title="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DESCRIPTION%>"/>

            <c:if test='<%= request.getAttribute("navTable").equals(StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME)%>'>                
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.MetadatiProfiloFascicoloList.NAME%>"/>
            </c:if>

            <c:if test='<%= request.getAttribute("navTable").equals(StrutTipiFascicoloForm.MetadatiProfiloArkList.NAME)%>'>
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.MetadatiProfiloArkList.NAME%>" />
            </c:if>
         
            <c:if test='<%= request.getAttribute("navTable").equals(StrutTipiFascicoloForm.AttribFascicoloList.NAME)%>'>
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.AttribFascicoloList.NAME%>" />
            </c:if>
            
            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
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
                <slf:section name="<%=StrutTipiFascicoloForm.XsdSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.ID_TIPO_FASCICOLO%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.TI_MODELLO_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.FL_STANDARD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.CD_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DS_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DT_ISTITUZ%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.DT_SOPPRES%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.MetadatiProfiloDetail.BL_XSD%>" width="w100" labelWidth="w30" controlWidth="w70"/> <sl:newLine />
                </slf:section>

                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTipiFascicoloForm.XsdAttribFascicoloSection.NAME%>" styleClass="importantContainer">
                    <slf:listNavBar name="<%=StrutTipiFascicoloForm.AttribFascicoloList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=StrutTipiFascicoloForm.AttribFascicoloList.NAME%>"  />
                    <slf:listNavBar  name="<%=StrutTipiFascicoloForm.AttribFascicoloList.NAME%>" />
                </slf:section>
            </slf:fieldSet>
            


        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>