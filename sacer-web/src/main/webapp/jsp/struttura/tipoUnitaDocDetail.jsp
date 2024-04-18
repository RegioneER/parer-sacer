<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipologia unità documentaria" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('.confermaDisattivazioneXsd').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutTipi.html?operation=confermaDisattivazioneXsd";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                $('.confermaDisattivazioneXsdUdAmmesso').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutTipi.html?operation=confermaDisattivazioneXsdUdAmmesso";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                // NOTA:  il codice si seguito riportato serve per "sganciarsi" dal framework e gestire i trigger attraverso 
                // il passaggio dei parametri JSON tramite POST e non (come effettuato dal framework) tramite GET. 
                // Necessario in quanto in questo caso la lunghezza della query string dei parametri passati tramite GET superava i limiti imposti.
                $("#Ti_categ_strut").unbind('change');

                $("#Ti_categ_strut").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("StrutTipi.html?operation=triggerTipoUnitaDocTi_categ_strutOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

                $("#Nm_categ_strut").unbind('change');

                $("#Nm_categ_strut").on('change', function () {
                    var parameters = $('#spagoLiteAppForm').serializeArray();
                    $.post("StrutTipi.html?operation=triggerTipoUnitaDocNm_categ_strutOnTriggerAjax", parameters).done(function (jsonData) {
                        CAjaxDataFormWalk(jsonData);
                        $('#loading').remove();
                    });
                });

            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi Unità Documentarie"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.confermaDisattivazioneXsd}">
                <div class="messages confermaDisattivazioneXsd ">
                    <ul>
                        <li class="message info ">Desideri disattivare la versione di XSD ?</li>
                    </ul>
                </div>
            </c:if>
            <c:if test="${!empty requestScope.confermaDisattivazioneXsdUdAmmesso}">
                <div class="messages confermaDisattivazioneXsdUdAmmesso ">
                    <ul>
                        <li class="message info ">Desideri disattivare la versione di XSD del profilo?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="Dettaglio tipologia unità documentaria"/>
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoUnitaDocList'].status eq 'insert')}">
                <slf:fieldBarDetailTag name="<%= StrutTipiForm.TipoUnitaDoc.NAME%>" hideBackButton="${(sessionScope['###_FORM_CONTAINER']['tipoUnitaDocList'].status eq 'insert') }"/>
            </c:if>   
            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoUnitaDocList'].status eq 'insert')}">
                <slf:listNavBarDetail name="<%= StrutTipiForm.TipoUnitaDocList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>
            <sl:newLine />
            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.TI_CATEG_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_CATEG_STRUT%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.TI_SAVE_FILE%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.STORAGE_UTILIZZATO_VERS%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.STORAGE_UTILIZZATO_VERS_MM%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.STORAGE_UTILIZZATO_AGG_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.ID_TIPO_SERVIZIO%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.ID_TIPO_SERVIZIO_ATTIV%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.ID_TIPO_SERV_CONSERV_TIPO_UD%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.ID_TIPO_SERV_ATTIV_TIPO_UD%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />                    
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DT_FIRST_VERS%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DT_LAST_VERS%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DT_SOPPRES%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DL_NOTE_TIPO_UD%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.ConfigSerieSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.FL_CREA_TIPO_SERIE_STANDARD%>" colSpan="1" labelWidth="w30" controlWidth="w100"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.ID_MODELLO_TIPO_SERIE%>" colSpan="1" labelWidth="w30" controlWidth="w100"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.NM_TIPO_SERIE_DA_CREARE%>" colSpan="1" labelWidth="w30" controlWidth="w100"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_TIPO_SERIE_DA_CREARE%>" colSpan="1" labelWidth="w30" controlWidth="w100"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.CD_SERIE_DA_CREARE%>" colSpan="1" labelWidth="w30" controlWidth="w100"/> <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.DS_SERIE_DA_CREARE%>" colSpan="1" labelWidth="w30" controlWidth="w100"/> <sl:newLine />
                </slf:section>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoUnitaDocList'].status eq 'insert') }">
                    <slf:section name="<%=StrutTipiForm.CreaCriterioTipoUnitaDocSection.NAME%>" styleClass="importantContainer"> 
                        <slf:lblField name="<%=StrutTipiForm.TipoUnitaDocCreazioneCriterio.CRITERIO_AUTOM_TIPO_UD%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    </slf:section>
                </c:if>
                <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoUnitaDocList'].status eq 'update') }">
                    <sl:pulsantiera>
                        <slf:lblField name="<%=StrutTipiForm.TipoUnitaDocCreazioneCriterio.CREA_CRITERIO_RAGGR_STANDARD_TIPO_UD_BUTTON%>" />
                    </sl:pulsantiera>
                </c:if>
                <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.ID_TIPO_UNITA_DOC%>" />
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoUnitaDoc'].status eq 'view') }">
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.TipoUnitaDoc.LOG_EVENTI_TIPO_UD%>" />
                </sl:pulsantiera>
                <sl:newLine skipLine="true"/>

                <slf:section name="<%=StrutTipiForm.TipoUnitaDocAmmessoTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.TipoUnitaDocAmmessoList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.TipoUnitaDocAmmessoList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.TipoUnitaDocAmmessoList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.XsdDatiSpecTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.XsdDatiSpecList.NAME%>" />
                </slf:section>
                 <slf:section name="<%=StrutTipiForm.XsdModelliUdTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.XsdModelliUdList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.XsdModelliUdList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.XsdModelliUdList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.TipoStrutUnitaDocTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.TipoStrutUnitaDocList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.TipoStrutUnitaDocList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.TipoStrutUnitaDocList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.RegoleSubStrutTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.RegoleSubStrutList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.SistemiVersantiTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.SistemiVersantiList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.SistemiVersantiList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.SistemiVersantiList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipiForm.CriteriRaggrTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipiForm.CriteriRaggruppamentoList.NAME%>" />
                </slf:section>
            </c:if>

            <slf:section name="<%=StrutTipiForm.ParametriAmministrazioneSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.ParametriTipoUdButtonList.PARAMETRI_AMMINISTRAZIONE_TIPO_UD_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= StrutTipiForm.ParametriAmministrazioneTipoUdList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= StrutTipiForm.ParametriAmministrazioneTipoUdList.NAME%>" />
            </slf:section>
            <slf:section name="<%=StrutTipiForm.ParametriConservazioneSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.ParametriTipoUdButtonList.PARAMETRI_CONSERVAZIONE_TIPO_UD_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= StrutTipiForm.ParametriConservazioneTipoUdList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= StrutTipiForm.ParametriConservazioneTipoUdList.NAME%>" />
            </slf:section>
            <slf:section name="<%=StrutTipiForm.ParametriGestioneSection.NAME%>" styleClass="noborder w100">
                <sl:pulsantiera>
                    <slf:lblField name="<%=StrutTipiForm.ParametriTipoUdButtonList.PARAMETRI_GESTIONE_TIPO_UD_BUTTON%>" width="w50" controlWidth="w30" labelWidth="w40"/>
                </sl:pulsantiera>
                <slf:editableList name="<%= StrutTipiForm.ParametriGestioneTipoUdList.NAME%>" multiRowEdit="true"/>
                <slf:listNavBar  name="<%= StrutTipiForm.ParametriGestioneTipoUdList.NAME%>" />
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
