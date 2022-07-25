<%@ page import="it.eng.parer.slite.gen.form.StruttureForm "%>

<%@ include file="../../include.jsp"%>



<sl:html>
    <sl:head title="Gestione Ambito Territoriale" >
        <script type="text/javascript" src="<c:url value="/js/sips/customStrutMessageBox.js"/>" ></script>
       
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Registri" />
        <sl:menu />
      
        <sl:content>
            <sl:contentTitle title="Gestione Ambito Territoriale"/>
            <slf:messageBox />
            
            <slf:fieldSet>
                <slf:lblField name="<%=StruttureForm.AmbitoTerritoriale.CD_AMBITO_TERRIT%>" colSpan="2" /> 
                <sl:newLine />  
                <slf:lblField name="<%=StruttureForm.AmbitoTerritoriale.TI_AMBITO_TERRIT%>" colSpan="4" /> <sl:newLine />  
                <slf:lblField name="<%=StruttureForm.AmbitoTerritoriale.ID_AMBITO_TERRIT_PADRE%>" colSpan="4" /> <sl:newLine />   
                
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <slf:tree name="<%=StruttureForm.GestAmbTree.NAME%>" additionalJsonParams="\"core\" : { \"expand_selected_onload\" : true, \"check_callback\" : true }"/>
           <script type="text/javascript" src="<c:url value="/js/custom/customGestAmbTree.js" />" ></script>
            <sl:newLine skipLine="true"/>
      

        </sl:content>
        
        <sl:footer />
    </sl:body>

</sl:html>

