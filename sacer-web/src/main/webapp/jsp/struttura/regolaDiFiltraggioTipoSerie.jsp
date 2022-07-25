<%@ page import="it.eng.parer.slite.gen.form.StrutSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutSerieForm.RegolaDiFiltraggioDetail.DESCRIPTION%>" >
        <script type="text/javascript" src='<c:url value="/js/sips/regolaDiFiltraggioTipoSerie.js"/>' ></script>
        <script type="text/javascript" src="<c:url value='/js/sips/customTipologiaSerieVincolataMessageBox.js'/>"></script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>
            <sl:contentTitle title="<%=StrutSerieForm.RegolaDiFiltraggioDetail.DESCRIPTION%>"/>

            <sl:newLine skipLine="true"/>
            <slf:messageBox />

            <c:if test="${!empty requestScope.customBox}">
                <div class="messages customBox ">
                    <ul>
                        <li class="message warning ">L’operazione comporterà il ricalcolo di almeno una serie. Si intende procedere?</li>
                    </ul>
                </div>
            </c:if>
            <div class="pulsantieraMB">
                <sl:pulsantiera >
                    <slf:buttonList name="<%= StrutSerieForm.TipoSerieCustomMessageButtonList.NAME%>"/>
                </sl:pulsantiera>
            </div>

            <sl:newLine skipLine="true"/>

            <c:if test="${sessionScope['###_FORM_CONTAINER']['registroRegoleFiltraggioList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutSerieForm.RegolaDiFiltraggioDetail.NAME%>" hideBackButton="false"/> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['registroRegoleFiltraggioList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutSerieForm.RegistroRegoleFiltraggioList.NAME%>" />   
            </c:if>
            
                <slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.NM_AMBIENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.NM_ENTE%>" colSpan= "2" labelWidth="w30" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.NM_STRUT%>" colSpan= "2" labelWidth="w30" controlWidth="w100"/>
                <sl:newLine skipLine="true"/>

            <slf:section name="<%=StrutSerieForm.SerieUniDoc.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.StrutRif.NM_TIPO_SERIE_PADRE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
            </slf:section>
            <slf:section name="<%=StrutSerieForm.SerieRegistroUniDoc.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutSerieForm.StrutRif.REGISTRO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.StrutRif.TIPO_UNITA_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                <slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.FLAG_DATI_SPEC_PRESENTI_SM%>" colSpan="1"/>                    
                <sl:newLine />
            </slf:section>
            <slf:section name="<%=StrutSerieForm.SerieRegistroUniDocRegolaFiltraggio.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.TI_FILTRO%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.ID_TIPO_DOC_PRINC%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <%--<slf:lblField name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.FLAG_DATI_SPEC_PRESENTI_DOC%>" colSpan="1"/>                    --%>
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=StrutSerieForm.RegolaDiFiltraggioDetail.FILTRI_DATI_SPEC%>"  />
                <sl:newLine />
            </slf:section>
            <sl:newLine skipLine="true"/>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

