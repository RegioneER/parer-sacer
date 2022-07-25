<%@ page import="it.eng.parer.slite.gen.form.StrutTitolariForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTitolariForm.ImportaTitolario.NAME%>" ></sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
          <!--  <div id="content">
         <form id="multipartForm" action="StrutTitolari.html" method="post" enctype="multipart/form-data" > -->
           <sl:content multipartForm="true">
          
                <slf:messageBox />
                <sl:contentTitle title="<%=StrutTitolariForm.ImportaTitolario.NAME%>"/>
                <sl:newLine skipLine="true"/>
                <slf:fieldBarDetailTag name="<%=StrutTitolariForm.ImportaTitolario.NAME%>"  />
                <sl:newLine skipLine="true"/>
                <slf:section name="<%=StrutTitolariForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTitolariForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
                </slf:section>
                <slf:section name="<%=StrutTitolariForm.DocTrasmSection.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTitolariForm.ImportaTitolario.CD_REGISTRO_DOC_INVIO%>" colSpan= "2" />
                    <slf:lblField name="<%=StrutTitolariForm.ImportaTitolario.AA_DOC_INVIO%>" colSpan= "1" />
                    <slf:lblField name="<%=StrutTitolariForm.ImportaTitolario.CD_DOC_INVIO%>" colSpan= "1" />
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTitolariForm.ImportaTitolario.DT_DOC_INVIO%>" colSpan="1" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.ImportaTitolario.DT_ISTITUZ%>" colSpan="1" controlWidth="w70"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTitolariForm.ImportaTitolario.DT_SOPPRES%>" colSpan="1" controlWidth="w70"/>
                    <sl:newLine skipLine="true"/>
                </slf:section>
                <slf:fieldSet borderHidden="true">
                    <label class="slLabel wlbl" for="File_titolario">File titolario:&nbsp;</label>
                    <div class="containerLeft w2ctr">
                        <input id="Importa_file_titolario" class="slText w80" type="file" name="File_titolario" />
                    </div>
                </slf:fieldSet>
                <sl:pulsantiera>
                    <slf:lblField  name="<%=StrutTitolariForm.ImportaTitolario.IMPORTA_FILE_TITOLARIO%>"  width="w50" />
                </sl:pulsantiera>
            </sl:content> 
           <!--  </form>
        </div> -->

        <sl:footer />
    </sl:body>
</sl:html>