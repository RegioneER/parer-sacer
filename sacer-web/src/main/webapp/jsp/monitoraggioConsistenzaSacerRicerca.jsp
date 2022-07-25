<%@ page import="it.eng.parer.slite.gen.form.MonitoraggioForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Esame consistenza SACER" >
        <script type="text/javascript">
            $(document).ready(function () {

               

                $('#ConsistenzaSacerList tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(3), td:eq(4)');
                            elemento.css({"background-color": "#AFE4FF", "text-align": "right"});
                            var elemento2 = $(this).find('td:eq(5), td:eq(6),td:eq(7), td:eq(8)');
                            elemento2.css({"background-color": "#FEF5C2", "text-align": "right"});
                            var elemento3 = $(this).find('td:eq(9) a');
                            elemento3.parent().css({"text-align": "right"});                            
                            if (elemento3.html() == 0) {
                                elemento3.removeAttr("href");
                            }
                            if(elemento3.html()>999){
                                elemento3.html(elemento3.html().replace(/(\d)(?=(\d\d\d)+(?!\d))/g, "$1."));
                            }
                        });


                $('#ConsistenzaSacerProblemsList tr').each(
                        function (index) {
                            var elemento = $(this).find('td:eq(12), td:eq(13), td:eq(14)');
                            elemento.css({"text-align": "right"});
                        });

            });
            
//            $('input[name="operation__scaricaReport"]').click(
//                        function (index) {
//                            alert('ciao');
//                            $('#loading').remove();
//                        });
            
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox  />
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="ESAME CONSISTENZA SACER"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.ID_AMBIENTE%>" colSpan="2" />                                
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.ID_ENTE%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.ID_STRUT%>" colSpan="2" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.DIFFERENZA_ZERO%>" colSpan="2" />
                <sl:newLine /> 
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.DT_RIF_DA%>" colSpan="2" controlWidth="w0" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.DT_RIF_A%>" colSpan="2" controlWidth="w0"/>
            </slf:fieldSet>
            <sl:newLine skipLine="true" />     

            <slf:section name="<%=MonitoraggioForm.TotaliConsistenzaSection.NAME%>" styleClass="importantContainer">
                <slf:fieldSet  borderHidden="false">
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.FILTRI_UTILIZZATI%>" width="w100" labelWidth="w30" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_VERS%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_ANNUL%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right"/>                
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_PRESA_IN_CARICO%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_AIP_IN_AGGIORN%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right" />
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_AIP_GENERATO%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_IN_VOLUME%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right"/>
                    <sl:newLine />
                    <slf:lblField name="<%=MonitoraggioForm.ConsistenzaSacerTotaliUdDocComp.TOTALE_NI_COMP_DELTA%>" width="w100" labelWidth="w30" controlWidth="w10" labelPosition="left" controlPosition="right"/>
                </slf:fieldSet>
            </slf:section>            
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.RICERCA_CONSISTENZA_SACER%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.PULISCI_CONSISTENZA_SACER%>" width="w25" />
                <slf:lblField name="<%=MonitoraggioForm.FiltriConsistenzaSacer.SCARICA_REPORT%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <sl:newLine skipLine="true"/>

            <!--  piazzo la lista con i risultati -->            
            <slf:listNavBar name="<%= MonitoraggioForm.ConsistenzaSacerList.NAME%>" />
            <slf:list name="<%= MonitoraggioForm.ConsistenzaSacerList.NAME%>" />
            <slf:listNavBar name="<%= MonitoraggioForm.ConsistenzaSacerList.NAME%>" />

            <c:if test="${sessionScope.apriDelta}">
                <sl:newLine skipLine="true"/>
                <h2><c:out value="${sessionScope.problemiDelta}"/></h2>
                <slf:listNavBar name="<%= MonitoraggioForm.ConsistenzaSacerProblemsList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= MonitoraggioForm.ConsistenzaSacerProblemsList.NAME%>" />
                <slf:listNavBar name="<%= MonitoraggioForm.ConsistenzaSacerProblemsList.NAME%>" />
                <sl:newLine skipLine="true"/>
                <%--                <slf:listNavBar name="<%= MonitoraggioForm.ConsistenzaSacerProblemsAnnullList.NAME%>" pageSizeRelated="true"/>
                                <slf:list name="<%= MonitoraggioForm.ConsistenzaSacerProblemsAnnullList.NAME%>" />
                                <slf:listNavBar name="<%= MonitoraggioForm.ConsistenzaSacerProblemsAnnullList.NAME%>" />--%>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera >
                    <slf:buttonList name="<%= MonitoraggioForm.MostraNascondiIdButtonList.NAME%>"/>
                </sl:pulsantiera>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>