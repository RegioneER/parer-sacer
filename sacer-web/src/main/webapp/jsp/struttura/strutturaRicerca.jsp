<%@ page import="it.eng.parer.slite.gen.form.StruttureForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Gestione Strutture" >
        <script type="text/javascript" src="<c:url value='/js/sips/customStruttureMessageBox.js'/>" ></script>

        <script type="text/javascript" >
            $(document).ready(function () {//
                $('#Num_strut_templ_disp').html($('#Num_strut_templ_disp').text());//
                $('#Num_strut_templ_part').html($('#Num_strut_templ_part').text());
            });
        </script>

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

       <!--  <div id="content">
            <form id="spagoLiteAppForm" action="Strutture.html" method="post" > -->
		<sl:content>
                <slf:messageBox />

                <sl:newLine skipLine="true"/>

                <%@ include file="mascheraCreazioneStruttureTemplate.jspf"%>

                <sl:contentTitle title="Gestione Strutture"/>

                <sl:newLine skipLine="true"/>

                <slf:fieldSet >
                    <slf:lblField name="<%=StruttureForm.VisStrutture.NM_STRUT%>" colSpan="4" controlWidth="w40" />
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_AMBIENTE%>" colSpan="4" />
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_ENTE%>" colSpan="4" />
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.FL_TEMPLATE%>" colSpan="2" />                      
                    <slf:lblField name="<%=StruttureForm.VisStrutture.FL_PARTIZ%>" colSpan="2" />  
                    <sl:newLine />                       
                    <slf:lblField name="<%=StruttureForm.VisStrutture.FL_PARAMETRI_SPECIFICI%>" colSpan="2" />                                          
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.NM_SISTEMA_VERSANTE%>" colSpan="4" />  
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_REGIONE_STATO%>" colSpan="4" />  
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_PROVINCIA%>" colSpan="4" />  
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_FORMA_ASSOCIATA%>" colSpan="4" />  
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_CATEG_ENTE%>" colSpan="4" />  
                     <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_AMBIENTE_ENTE_CONVENZ%>" colSpan="4" />  
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.VisStrutture.ID_ENTE_CONVENZ%>" colSpan="4" />  
                </slf:fieldSet>

                <sl:newLine skipLine="true"/>

                <slf:section name="<%=StruttureForm.StruttureTemplateSection.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StruttureForm.StruttureTemplate.NUM_STRUT_TEMPL_DISP%>" width="w100" labelWidth="w20" controlWidth="w80"/>
                    <sl:newLine />   
                    <slf:lblField name="<%=StruttureForm.StruttureTemplate.NUM_STRUT_TEMPL_PART%>" width="w100" labelWidth="w20" controlWidth="w80"/>  
                </slf:section>

                <sl:newLine skipLine="true"/>

                <sl:pulsantiera>
                    <!-- piazzo il bottone di ricerca e pulisci -->
                    <slf:lblField  name="<%=StruttureForm.VisStrutture.RICERCA_STRUTTURA_BUTTON%>"  width="w50" />
                </sl:pulsantiera>

                <sl:newLine skipLine="true"/>  

                <!--  piazzo la lista con i risultati -->
                <slf:listNavBar name="<%= StruttureForm.StruttureList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%= StruttureForm.StruttureList.NAME%>" />
                <slf:listNavBar  name="<%= StruttureForm.StruttureList.NAME%>" />

                <sl:newLine skipLine="true"/>

                <sl:pulsantiera>
                    <slf:lblField  name="<%=StruttureForm.InsStruttura.IMPORTA_STRUTTURA%>"  width="w30" /> 
                    <slf:lblField  name="<%=StruttureForm.ImportaParametri.IMPORTA_PARAMETRI_DA_RICERCA_BUTTON%>"  width="w30" /> 
                    <slf:lblField  name="<%=StruttureForm.InsStruttura.CREA_STRUTTURE_TEMPLATE%>"  width="w30" /> 
                </sl:pulsantiera> 
         <!--    </form>
        </div> -->
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
