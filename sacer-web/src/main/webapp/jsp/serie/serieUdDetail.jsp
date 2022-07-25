<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="<%=SerieUDForm.SerieDetail.DESCRIPTION%>" >
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
        <script type="text/javascript" src="<c:url value="/js/custom/customPollSerie.js" />" ></script>
        <script type="text/javascript">
            $(document).ready(function () {
                $("#Ni_anni_conserv").change(function () {
                    var value = $("#Ni_anni_conserv").val();
                    if (value) {
                        if (value === '9999') {
                            $("#Conserv_unlimited").val("1");
                        } else {
                            $("#Conserv_unlimited").val("0");
                        }
                    }
                });

                if ($("#Ni_anni_conserv").val().length !== 0) {
                    $("#Ni_anni_conserv").trigger('change');
                }

                $('.confermaCalcoloBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "SerieUD.html?operation=confermaCalcoloContenuto";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                poll();
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content >
            <slf:messageBox />
            <div class="serieMessageBox "></div>
            <c:if test="${!empty requestScope.confermaCalcoloBox}">
                <div class="messages confermaCalcoloBox ">
                    <ul>
                        <li class="message info ">Desideri ricalcolare la serie ?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="<%=SerieUDForm.SerieDetail.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <c:choose>
                <c:when test='<%= session.getAttribute("navTableSerie").equals(SerieUDForm.SerieList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= SerieUDForm.SerieList.NAME%>" />
                </c:when>
                <c:when test='<%= session.getAttribute("navTableSerie").equals(SerieUDForm.SerieDaFirmareList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= SerieUDForm.SerieDaFirmareList.NAME%>" />
                </c:when>
                <c:when test='<%= session.getAttribute("navTableSerie").equals(SerieUDForm.SerieDaValidareList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= SerieUDForm.SerieDaValidareList.NAME%>" />
                </c:when>
                <c:when test='<%= session.getAttribute("navTableSerie").equals(SerieUDForm.VersioniPrecedentiList.NAME)%>'>
                    <slf:listNavBarDetail name="<%= SerieUDForm.VersioniPrecedentiDetailList.NAME%>" />
                </c:when>
                <c:otherwise>
                    <slf:fieldBarDetailTag name="<%= SerieUDForm.SerieDetail.NAME%>" hideOperationButton="true"/>
                </c:otherwise>
            </c:choose>
            <slf:tab  name="<%= SerieUDForm.SerieDetailTabs.NAME%>" tabElement="<%= SerieUDForm.SerieDetailTabs.info_principali%>">
                <slf:fieldSet borderHidden="false">
                    <%@include file="jspf/infoPrincipaliTab.jspf" %>
                    <sl:pulsantiera>
                        <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.DOWNLOAD_PACCHETTO_ARK%>" />
                    </sl:pulsantiera>
                </slf:fieldSet>
            </slf:tab>
            <slf:tab  name="<%= SerieUDForm.SerieDetailTabs.NAME%>" tabElement="<%= SerieUDForm.SerieDetailTabs.indice_aip%>">
                <slf:fieldSet borderHidden="false">
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.CD_VER_XSD_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.DT_CREAZIONE_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <!--EVO#16486--> 
                    <%--<slf:lblField name="<%=SerieUDForm.SerieDetail.DS_URN_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />--%>
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.DS_URN_AIP_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.DS_URN_NORMALIZ_AIP_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <!--end EVO#16486-->
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.HASH_PERSONALIZZATO%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.NM_ENTE_CONSERV%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <%--<slf:lblField name="<%=SerieUDForm.SerieDetail.DS_HASH_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.DS_ALGO_HASH_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.CD_ENCODING_HASH_IX_AIP%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />--%>
                    <slf:lblField name="<%=SerieUDForm.SerieDetail.BL_FILE_IX_AIP%>" width="w100" controlWidth="w80" labelWidth="w20" /><sl:newLine />
                    <sl:pulsantiera>
                        <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.DOWNLOAD_AIP%>" width="w30" position="right" />
                    </sl:pulsantiera>
                </slf:fieldSet>
            </slf:tab>    
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_APERTA%>" />
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_DA_VALIDARE%>" />
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_DA_CONTROLLARE%>" />
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_VALIDAZIONE_IN_CORSO%>" />
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_FORZA_VALIDAZIONE%>" />
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_ANNULLATA%>" />
                <slf:lblField name="<%=SerieUDForm.SerieDetailButtonList.CAMBIA_STATO_AGGIORNA%>" />
            </sl:pulsantiera>
            <sl:newLine />   
            <slf:tab  name="<%= SerieUDForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.SerieDetailSubTabs.lista_note%>">
                <slf:listNavBar name="<%= SerieUDForm.NoteList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.NoteList.NAME%>" />
                <slf:listNavBar name="<%= SerieUDForm.NoteList.NAME%>" />
            </slf:tab>    
            <slf:tab  name="<%= SerieUDForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.SerieDetailSubTabs.lista_volumi%>">
                <slf:listNavBar name="<%= SerieUDForm.VolumiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.VolumiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUDForm.VolumiList.NAME%>" />
            </slf:tab>    
            <slf:tab  name="<%= SerieUDForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.SerieDetailSubTabs.lista_stati%>">
                <slf:listNavBar name="<%= SerieUDForm.StatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.StatiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUDForm.StatiList.NAME%>" />
            </slf:tab>    
            <slf:tab  name="<%= SerieUDForm.SerieDetailSubTabs.NAME%>" tabElement="<%= SerieUDForm.SerieDetailSubTabs.lista_versioni_precedenti%>">
                <slf:listNavBar name="<%= SerieUDForm.VersioniPrecedentiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= SerieUDForm.VersioniPrecedentiList.NAME%>" />
                <slf:listNavBar name="<%= SerieUDForm.VersioniPrecedentiList.NAME%>" />
            </slf:tab>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>