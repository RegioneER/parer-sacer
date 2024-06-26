<%@ page import="it.eng.parer.slite.gen.form.StrutDatiSpecForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio attributo dati specifici" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Attributi Dati Specifici"/>
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:contentTitle title="Dettaglio attributo"/>
 
            <slf:listNavBarDetail name="<%= StrutDatiSpecForm.AttribDatiSpecList.NAME%>" />  
            <sl:newLine skipLine="true"/>
            
            <slf:fieldSet>
            <slf:section name="<%=StrutDatiSpecForm.Struttura.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutDatiSpecForm.StrutRif.STRUTTURA%>" colSpan= "4" /><sl:newLine />
                <slf:lblField name="<%=StrutDatiSpecForm.StrutRif.ID_ENTE%>" colSpan= "4" /><sl:newLine />
            </slf:section>
            <c:choose>
                <c:when test="${requestScope.lastPage=='tipoUnitaDoc'}">
                    <slf:section name="<%=StrutDatiSpecForm.STipoUnitaDoc.NAME%>" styleClass="importantContainer">  
                        <slf:lblField name="<%=StrutDatiSpecForm.TipoUdRif.NM_TIPO_UNITA_DOC%>" colSpan= "4" />
                    </slf:section>

                </c:when>
                <c:when test="${requestScope.lastPage=='tipoDoc'}">
                    <slf:section name="<%=StrutDatiSpecForm.STipoDoc.NAME%>" styleClass="importantContainer">  
                        <slf:lblField name="<%=StrutDatiSpecForm.TipoDocRif.NM_TIPO_DOC%>" colSpan= "4" />
                    </slf:section>

                </c:when>
                <c:when test="${requestScope.lastPage=='tipoCompDoc'}">
                    <slf:section name="<%=StrutDatiSpecForm.STipoStrutDoc.NAME%>" styleClass="importantContainer">  
                        <slf:lblField name="<%=StrutDatiSpecForm.TipoStrutDocRif.NM_TIPO_STRUT_DOC%>" colSpan= "4" />
                    </slf:section>
                    <slf:section name="<%=StrutDatiSpecForm.STipoCompDoc.NAME%>" styleClass="importantContainer">  
                        <slf:lblField name="<%=StrutDatiSpecForm.TipoCompDocRif.NM_TIPO_COMP_DOC%>" colSpan= "4" />
                    </slf:section>
                </c:when>
                <c:otherwise>

                </c:otherwise>
            </c:choose>
             <slf:section name="<%=StrutDatiSpecForm.SXsdDatiSpec.NAME%>" styleClass="importantContainer"> 
                 <slf:lblField name="<%=StrutDatiSpecForm.XsdDatiSpec.CD_VERSIONE_XSD%>" colSpan= "4" />
             </slf:section>

            <sl:newLine />
           <slf:section name="<%=StrutDatiSpecForm.SAttribDatiSpec.NAME%>" styleClass="importantContainer">   
            <slf:lblField name="<%=StrutDatiSpecForm.AttribDatiSpec.NM_ATTRIB_DATI_SPEC%>" colSpan= "4" /> <sl:newLine />
            <slf:lblField name="<%=StrutDatiSpecForm.AttribDatiSpec.DS_ATTRIB_DATI_SPEC%>" colSpan= "4" /> <sl:newLine />
            <slf:lblField name="<%=StrutDatiSpecForm.AttribDatiSpec.NI_ORD_ATTRIB%>" colSpan= "4" /> <sl:newLine />
            <slf:lblField name="<%=StrutDatiSpecForm.AttribDatiSpec.DT_ISTITUZ%>" colSpan= "4" /> <sl:newLine />
            <slf:lblField name="<%=StrutDatiSpecForm.AttribDatiSpec.DT_SOPPRES%>" colSpan= "4" /> <sl:newLine />
           </slf:section>
            </slf:fieldSet>
 
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

