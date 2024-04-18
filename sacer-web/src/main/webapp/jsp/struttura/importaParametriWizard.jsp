<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StruttureForm.ImportaParametri.DESCRIPTION%>" />

    <script type="text/javascript" src="<c:url value='/js/sips/customImportaParametriMessageBox.js'/>" ></script>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content multipartForm="true">
            <slf:messageBox />

            <c:if test="${!empty requestScope.customBoxImportaParametri}">
                <div class="messages customBoxImportaParametri ">
                    <ul>
                        <li class="message warning ">Impossibile procedere alla creazione del tipo serie; si desidera procedere con l’importazione della tipologia di unità documentaria?</li>
                    </ul>
                </div>
            </c:if>

            <sl:newLine skipLine="true"/>

            <slf:wizard name="<%= StruttureForm.InserimentoWizard.NAME%>">
                <slf:wizardNavBar name="<%=StruttureForm.InserimentoWizard.NAME%>" />
                <sl:newLine skipLine="true"/>   
                <slf:step name="<%= StruttureForm.InserimentoWizard.PASSO1%>">
                    <slf:fieldSet>
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.XML_PARAMETRI%>" width="w100" controlWidth="w30" labelWidth="w20" />
                    </slf:fieldSet>
                </slf:step>

                <slf:step name="<%= StruttureForm.InserimentoWizard.PASSO2%>">
                    <slf:fieldSet>
                        <slf:section name="<%=StruttureForm.ImportaParametriTipiUdSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.NM_TIPO_UNITA_DOC%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.NM_TIPO_STRUT_UNITA_DOC%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.CHECK_INCLUDI_REGISTRI%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.CHECK_INCLUDI_CRITERI%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.CHECK_INCLUDI_SISTEMI_MIGRAZ%>" colSpan="2" />                        
                        <sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.CHECK_INCLUDI_FORMATI_COMPONENTE%>" colSpan="2" />
                        </slf:section>
                    </slf:fieldSet>
                    <slf:fieldSet>
                        <slf:section name="<%=StruttureForm.ImportaParametriTipiFascicoloSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.NM_TIPO_FASCICOLO%>" colSpan="2" />
                        <sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.AA_TIPO_FASCICOLO%>" colSpan="2" />
                        <%--<sl:newLine />
                        <slf:lblField  name="<%=StruttureForm.ImportaParametri.CHECK_SOVRASCRIVI_PERIODI%>" colSpan="2" />  --%>                      
                        </slf:section>
                    </slf:fieldSet>
                </slf:step>

            </slf:wizard>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
