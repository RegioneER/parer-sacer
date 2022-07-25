<%@ page import="it.eng.parer.slite.gen.form.ComponentiForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Scarica Dip">
        <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="SCARICA DIP"/>

            <slf:fieldBarDetailTag name="<%=ComponentiForm.ComponentiDetail.NAME%>" hideOperationButton="true"/>
            <slf:fieldSet borderHidden="false">
                <slf:section name="<%=ComponentiForm.VersatoreSection.NAME%>" styleClass="importantContainer">
                    <slf:fieldSet borderHidden="true">
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_AMBIENTE%>" colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_ENTE%>"  colSpan="3" controlWidth="w100"/>
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.NM_STRUT%>"  colSpan="3" controlWidth="w100"/>
                    </slf:fieldSet>
                </slf:section>
                <slf:section name="<%=ComponentiForm.UnitaDocSection.NAME%>" styleClass="importantContainer">
                    <slf:fieldSet borderHidden="true">
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_REGISTRO_KEY_UNITA_DOC%>"colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />    
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.AA_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine />    
                        <slf:lblField name="<%=ComponentiForm.ComponentiDetail.CD_KEY_UNITA_DOC%>"  colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    </slf:fieldSet>
                </slf:section>
                <sl:newLine />
            </slf:fieldSet>

            <slf:listNavBar name="<%= ComponentiForm.ComponentiDipList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= ComponentiForm.ComponentiDipList.NAME%>" />
            <slf:listNavBar  name="<%= ComponentiForm.ComponentiDipList.NAME%>" />
            <sl:newLine />
            <sl:pulsantiera>
                <slf:lblField name="<%=ComponentiForm.ScaricaDipBL.SCARICA_ZIP%>"  colSpan="1"/>
            </sl:pulsantiera>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>