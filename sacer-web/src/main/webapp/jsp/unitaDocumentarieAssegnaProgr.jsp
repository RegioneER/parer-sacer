<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Assegna progressivo Unità Documentaria">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>    
    <script type="text/javascript" src="<c:url value='/js/sips/customAssegnaProgrMessageBox.js'/>"></script>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>

            <c:if test="${!empty requestScope.customBoxAssegnaProgr}">
                <div class="messages customBoxAssegnaProgr ">
                    <ul>
                        <li class="message info ">Il nuovo valore del progressivo coincide con quello delle seguenti unità documentarie: 
                        <c:forEach items="${requestScope.listaUdString}" var="lista">
                            ${lista}<br>
                        </c:forEach>
                        <br> Procedere con la modifica?</li>
                    </ul>                   
                </div>
                <br>
                <div class="pulsantieraAssegnaProgr">
                    <slf:doubleLblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CONFERMA_MODIFICA_ASSEGNA_PROGR%>" name2="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.ANNULLA_MODIFICA_ASSEGNA_PROGR%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                </div> 
            </c:if>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="ASSEGNA PROGRESSIVO UNIT&Agrave; DOCUMENTARIA"/>

            <slf:fieldBarDetailTag name="<%= UnitaDocumentarieForm.UnitaDocumentarieDetail.NAME%>" />

            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:section name="<%=UnitaDocumentarieForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                </slf:section>
                <sl:newLine />
                <slf:section name="<%=UnitaDocumentarieForm.UnitaDocSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_REGISTRO_KEY_UNITA_DOC%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.AA_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    <sl:newLine/>
                    <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.CD_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                </slf:section>
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=UnitaDocumentarieForm.UnitaDocumentarieDetail.PG_UNITA_DOC%>"  colSpan="2" controlWidth="w20" labelWidth="w20"/>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>