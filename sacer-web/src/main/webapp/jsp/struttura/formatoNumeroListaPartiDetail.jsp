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
                <sl:contentTitle title="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DESCRIPTION%>"/>


            <c:if test="${sessionScope['###_FORM_CONTAINER']['parteNumeroFascicoloList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NAME%>" hideBackButton="true" />
            </c:if>

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['parteNumeroFascicoloList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiFascicoloForm.ParteNumeroFascicoloList.NAME%>" />
            </c:if>
            <sl:newLine skipLine="true"/>


            <slf:fieldSet>
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
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NI_PARTE_NUMERO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.ID_PARTE_NUMERO_FASCICOLO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NM_PARTE_NUMERO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DS_PARTE_NUMERO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NI_MIN_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.NI_MAX_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_CHAR_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_CHAR_SEP%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_PAD_PARTE_COMBO%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.TI_PAD_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <slf:field name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DESC_PAD_PARTE%>" controlWidth="w50" />
                    <sl:newLine/>
                    <slf:lblField name="<%=StrutTipiFascicoloForm.ParteNumeroFascicoloDetail.DL_VALORI_PARTE%>" width="w50" controlWidth="w60" labelWidth="w30"/>
                    <c:if test="${!(sessionScope['###_FORM_CONTAINER']['parteNumeroFascicoloList'].status eq 'view') }"> <div id="Desc_dl_valori_parte" class="slText w30">Se i valori accettati sono un insieme, ogni valore deve essere separato mediante "," senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;</div> </c:if>
                    <sl:newLine/>                      
                </slf:section> 
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>