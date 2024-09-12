<%@ page import="it.eng.parer.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="<%= AmministrazioneForm.ConfigurationList.DESCRIPTION%>" >

        <style>
            /* Stile personalizzato alla prima colonna (visibile))*/
            th:nth-of-type(2){
                width: 10%;
            }
            td:nth-of-type(2) input {width:100%;}

            /* Stile personalizzato alla terza colonna (visibile))*/
            th:nth-of-type(4){
                width: 20%;
            }
            td:nth-of-type(4) input {width:100%;}

            /* Stile personalizzato alla quarta colonna (visibile))*/
            th:nth-of-type(5){
                width: 20%;
            }
            td:nth-of-type(5) input {width:100%;}

            /* Stile personalizzato alla quinta colonna (visibile))*/            
            td:nth-of-type(6) input {width:100%;}

            /* Stile personalizzato alla ottava colonna (visibile))*/            
            td:nth-of-type(9) input {width:100%;}

        </style>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Gestione parametri SACER" />
            <slf:fieldSet>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.TI_PARAM_APPLIC_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.LOAD_CONFIG_LIST%>" width="w25" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.EDIT_CONFIG%>" width="w25" /><sl:newLine/>                
                <slf:lblField name="<%= AmministrazioneForm.Configuration.TI_GESTIONE_PARAM_COMBO%>" colSpan="2" />
                <slf:lblField name="<%= AmministrazioneForm.Configuration.ADD_CONFIG%>" width="w25" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_APPLIC_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_AMBIENTE_COMBO%>" colSpan="2" /><sl:newLine/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_STRUT_COMBO%>" colSpan="2" /><sl:newLine/>                
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_TIPO_UNITA_DOC_COMBO%>" colSpan="2" /><sl:newLine/>                
                <slf:lblField name="<%= AmministrazioneForm.Configuration.FL_APPART_AA_TIPO_FASCICOLO_COMBO%>" colSpan="2" /><sl:newLine/>                
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.LOG_EVENTI_REGISTRO_PARAMETRI%>" colSpan="2" /><sl:newLine/>                
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <%--<c:out value="${(sessionScope['###_FORM_CONTAINER']['configurationList']['table']!=null)}"/>
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['configurationList'].table['empty'])}">--%>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['configurationList']['table']!=null)}">
                Nel campo "valori possibili" occorre editare eventuali valori multipli accettati dal parametro separandoli dal carattere | (esempio: FIRMA|MARCA)
            </c:if>
            <sl:newLine skipLine="true"/>
            <!--  piazzo la lista con i risultati -->
            <%--<slf:listNavBar name="<%= AmministrazioneForm.ConfigurationList.NAME%>" pageSizeRelated="true"/>--%>
            <slf:editableList name="<%= AmministrazioneForm.ConfigurationList.NAME%>" multiRowEdit="true" />
            <slf:listNavBar  name="<%= AmministrazioneForm.ConfigurationList.NAME%>" />
            <sl:pulsantiera>
                <slf:lblField name="<%= AmministrazioneForm.Configuration.SAVE_CONFIG%>" width="w25" />
            </sl:pulsantiera>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>