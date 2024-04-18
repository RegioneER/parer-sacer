<%@ page import="it.eng.parer.slite.gen.form.StrutTipoStrutForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Dettaglio aggiunta rimozione concatenazioni per formati specifici" >                
    </sl:head>

    <c:set var="desc" value="Seleziona i formati file da concatenare ai formati del tipo componente" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Formati Documento" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="Dettaglio aggiunta rimozione concatenazioni"/>        
            <slf:listNavBarDetail name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" /> 
            <div class="newLine skipLine"></div>
            <slf:fieldSet>
                <slf:section name="<%=StrutTipoStrutForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                </slf:section>
                 <slf:section name="<%=StrutTipoStrutForm.STipoStrutDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.NM_TIPO_STRUT_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.STipoCompDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.NM_TIPO_COMP_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.ID_TIPO_COMP_DOC%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                </slf:section>
                 <slf:section name="<%=StrutTipoStrutForm.AnteprimaFormatiFileDoc.NAME%>" styleClass="importantContainer">  
                        <slf:lblField name="<%=StrutTipoStrutForm.FormatoFileDoc.NM_FORMATO_FILE_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipoStrutForm.FormatoFileDoc.DS_FORMATO_FILE_DOC%>"  colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipoStrutForm.FormatoFileDoc.NI_PUNTEGGIO_TOTALE%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipoStrutForm.FormatoFileDoc.CD_VERSIONE%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipoStrutForm.FormatoFileDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTipoStrutForm.FormatoFileDoc.DT_SOPPRES%>" colSpan= "2" labelWidth="w20" controlWidth="w70" />
                        <sl:newLine />
                    </slf:section>
            </slf:fieldSet> 
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutTipoStrutForm.FormatiAmmissibiliComp.NAME%>" styleClass="importantContainer"> 
                <slf:listNavBar name="<%=StrutTipoStrutForm.FormatoFileStandardToCompList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=StrutTipoStrutForm.FormatoFileStandardToCompList.NAME%>" />
                <slf:listNavBar name="<%=StrutTipoStrutForm.FormatoFileStandardToCompList.NAME%>" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera>
                <slf:buttonList name="<%=StrutTipoStrutForm.SelectConcatButtonList.NAME%>" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutTipoStrutForm.FormatiConcatenabiliSelezionatiComp.NAME%>" styleClass="importantContainer"> 
                <slf:listNavBar name="<%=StrutTipoStrutForm.SelectFormatoFileStandardCompList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=StrutTipoStrutForm.SelectFormatoFileStandardCompList.NAME%>" />
                <slf:listNavBar name="<%=StrutTipoStrutForm.SelectFormatoFileStandardCompList.NAME%>" />
                <sl:newLine skipLine="true"/>
            </slf:section>
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>