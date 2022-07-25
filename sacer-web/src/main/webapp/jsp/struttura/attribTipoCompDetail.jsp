<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Attrib Tipo Comp Detail" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />    
            <sl:newLine skipLine="true"/>
            

   
            <slf:listNavBarDetail name="<%= StruttureForm.AttribTipoCompList.NAME%>" /> 
            <sl:newLine />
            
            <slf:fieldSet >
                <slf:lblField name="<%=StruttureForm.AttribTipoComp.NM_ATTRIB_TIPO_COMP%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.AttribTipoComp.DS_ATTRIB_TIPO_COMP%>" colSpan="4" controlWidth="w40"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.AttribTipoComp.NI_ORD_ATTRIB%>" colSpan="4" controlWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=StruttureForm.AttribTipoComp.DT_ISTITUZ%>" colSpan="4" controlWidth="w20" /><sl:newLine />
                <slf:lblField name="<%=StruttureForm.AttribTipoComp.DT_SOPPRES%>" colSpan="4" controlWidth="w20" /><sl:newLine />
            </slf:fieldSet>
            
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

