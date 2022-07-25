<%@ page import="it.eng.parer.slite.gen.form.AmbienteForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Scelta struttura" >
        <script type="text/javascript" src="<c:url value='/js/sips/customModificaAmbienteMessageBox.js'/>" ></script>
        <script type='text/javascript' >
            $(document).ready(function () {
                $("#Id_ambiente_ente_convenz").unbind('change');
                $("#Id_ente_gestore").unbind('change');

                $("#Id_ambiente_ente_convenz").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("Ambiente.html?operation=triggerInsAmbienteId_ambiente_ente_convenzOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

                $("#Id_ente_gestore").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("Ambiente.html?operation=triggerInsAmbienteId_ente_gestoreOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });
                
                CustomBoxModificaAmbiente();

            });
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />

        <sl:menu />
        <sl:content>
            <div>
                <input name="table" type="hidden" value="${fn:escapeXml(param.table)}" />
            </div>
            <slf:messageBox /> 
            <c:if test="${!empty requestScope.customModificaAmbiente}">
                <div class="messages customModificaAmbienteMessageBox ">
                    <ul>
                        <li class="message warning "><c:out value="${requestScope.messaggioModificaAmbiente}"/></li>
                    </ul>   
                </div>
            </c:if>            
            <sl:contentTitle title="Dettaglio Ambiente"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['ambientiList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= AmbienteForm.InsAmbiente.NAME%>" hideBackButton="true"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['ambientiList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= AmbienteForm.AmbientiList.NAME%>" />  
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:fieldSet>
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.NM_AMBIENTE%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.DS_AMBIENTE%>" colSpan="2"/>
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.ID_AMBIENTE_ENTE_CONVENZ%>" colSpan="2"/>                
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.DT_INI_VAL%>" colSpan="2"/>
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.ID_ENTE_GESTORE%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.DT_FIN_VAL%>" colSpan="2"/>
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.ID_ENTE_CONSERV%>" colSpan="2"/>
                <sl:newLine />
                <slf:lblField name="<%=AmbienteForm.InsAmbiente.DS_NOTE%>" colSpan="2"/>
                <sl:newLine />
            </slf:fieldSet>                       

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['insAmbiente'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <div class="livello1"><b>Enti appartenenti all'ambiente</b></div>
                <sl:newLine skipLine="true"/>
                <slf:listNavBar name="<%= AmbienteForm.EntiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= AmbienteForm.EntiList.NAME%>" />
                <slf:listNavBar  name="<%= AmbienteForm.EntiList.NAME%>" />
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=AmbienteForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=AmbienteForm.ParametriAmbienteButtonList.PARAMETRI_AMMINISTRAZIONE_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    <slf:editableList name="<%= AmbienteForm.ParametriAmministrazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriAmministrazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmbienteForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=AmbienteForm.ParametriAmbienteButtonList.PARAMETRI_CONSERVAZIONE_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    <slf:editableList name="<%= AmbienteForm.ParametriConservazioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriConservazioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmbienteForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=AmbienteForm.ParametriAmbienteButtonList.PARAMETRI_GESTIONE_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    <slf:editableList name="<%= AmbienteForm.ParametriGestioneAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriGestioneAmbienteList.NAME%>" />
                </slf:section>
                <slf:section name="<%=AmbienteForm.ParametriMultipliSection.NAME%>" styleClass="noborder w100">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=AmbienteForm.ParametriAmbienteButtonList.PARAMETRI_MULTIPLI_AMBIENTE_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                    </sl:pulsantiera>
                    Nel campo "valori" occorre editare eventuali valori multipli accettati dal parametro separandoli dal carattere |
                    <slf:editableList name="<%= AmbienteForm.ParametriMultipliAmbienteList.NAME%>" multiRowEdit="true"/>
                    <slf:listNavBar  name="<%= AmbienteForm.ParametriMultipliAmbienteList.NAME%>" />
                </slf:section>
            </c:if>


        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
