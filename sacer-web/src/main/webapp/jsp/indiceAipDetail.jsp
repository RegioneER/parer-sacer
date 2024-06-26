<%@ page import="it.eng.parer.slite.gen.form.UnitaDocumentarieForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio versione indice AIP">
         <script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="DETTAGLIO VERSIONE INDICE AIP"/>
            <slf:listNavBarDetail name="<%= UnitaDocumentarieForm.IndiciAIPList.NAME%>" />
            <!--  piazzo i campi da visualizzare nel dettaglio -->
            <slf:fieldSet borderHidden="false">
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.NM_AMBIENTE%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.NM_ENTE%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.NM_STRUT%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.CD_REGISTRO_KEY_UNITA_DOC%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.AA_KEY_UNITA_DOC%>" width="w100" labelWidth="w20" controlWidth="w80" />
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.CD_KEY_UNITA_DOC%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine skipLine="true"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.CD_VER_INDICE_AIP%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.DT_CREAZIONE%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.URN%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.URN_NORMALIZZATO%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.URN_INIZIALE%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.DS_HASH_INDICE_AIP%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.TI_FORMATO_INDICE_AIP%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.DS_CAUSALE%>" width="w100" labelWidth="w20" controlWidth="w80"/> 
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.ID_ELENCO_VERS%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                <sl:newLine />
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.TI_STATO_ELENCO_VERS%>" width="w100" labelWidth="w20" controlWidth="w80"/> 
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.BL_FILE_VER_INDICE_AIP%>" width="w100" controlWidth="w100"/>
                <sl:newLine skipLine="true"/>
                <sl:pulsantiera>
                    <slf:lblField name="<%=UnitaDocumentarieForm.VersioneIndiceAIPDetail.SCARICA_INDICE_AIP_DETAIL%>"  width="w50" labelWidth="w20" controlWidth="w80" />
                </sl:pulsantiera>
            </slf:fieldSet>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>