<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<c:set scope="request" var="table" value="${!empty param.table}" />

<sl:html>
    <sl:head title="Dettaglio Ente">
        <script type="text/javascript" src="<c:url value='/js/sips/customModificaEnteMessageBox.js'/>" ></script>
        <script type='text/javascript' >
            $(document).ready(function () {
                CustomBoxModificaEnte();
            });
        </script>
    </sl:head>    
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Enti" />
        <sl:menu />
        <sl:content>
            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>

            <slf:messageBox />
            <c:if test="${!empty requestScope.customModificaEnte}">
                <div class="messages customModificaEnteMessageBox ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.messaggioModificaEnte}"/></li>
                    </ul>   
                </div>
            </c:if>        
            <sl:contentTitle title="Dettaglio Ente"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['entiList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= AmbienteForm.InsEnte.NAME%>" hideBackButton="${!(sessionScope['###_FORM_CONTAINER']['entiList'].status eq 'view')}"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['entiList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmbienteForm.EntiList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>    

            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.InsEnte.ID_ENTE%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />    
                <slf:lblField name="<%=AmbienteForm.InsEnte.NM_ENTE%>" colSpan="2" controlWidth="w100" />
                <slf:lblField name="<%=AmbienteForm.InsEnte.ID_CATEG_ENTE%>" colSpan= "2" controlWidth="w100"/>
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.CD_ENTE_NORMALIZ%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_INI_VAL%>" colSpan="2" />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_FINE_VAL%>" colSpan="2" />   
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.FL_CESSATO%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DS_ENTE%>" colSpan="2" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.ID_AMBIENTE%>" colSpan="2" />                
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_INI_VAL_APPART_AMBIENTE%>" colSpan="2" />
                <slf:lblField name="<%=AmbienteForm.InsEnte.DT_FIN_VAL_APPART_AMBIENTE%>" colSpan= "2" />
                <sl:newLine />                
                <slf:lblField name="<%=AmbienteForm.InsEnte.TIPO_DEF_TEMPLATE_ENTE%>" colSpan="2" controlWidth="w100" />  
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>
            <div class="livello1"><b>Precedenti appartenenze ad ambienti</b></div>
            <sl:newLine skipLine="true"/>

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['insEnte'].status eq 'insert') }">
                <slf:listNavBar name="<%= AmbienteForm.StoricoEnteAmbienteList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= AmbienteForm.StoricoEnteAmbienteList.NAME%>" />
                <slf:listNavBar  name="<%= AmbienteForm.StoricoEnteAmbienteList.NAME%>" />
            </c:if>

            <sl:newLine skipLine="true"/>
            <div class="livello1"><b>Strutture appartenenti all'ente</b></div>
            <sl:newLine skipLine="true"/>

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['insEnte'].status eq 'insert') }">
                <slf:listNavBar name="<%= AmbienteForm.StruttureList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= AmbienteForm.StruttureList.NAME%>" />
                <slf:listNavBar  name="<%= AmbienteForm.StruttureList.NAME%>" />
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
