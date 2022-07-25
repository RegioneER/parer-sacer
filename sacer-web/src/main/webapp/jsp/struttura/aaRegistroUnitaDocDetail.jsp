<%@ page import="it.eng.parer.slite.gen.form.StrutTipiForm"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio periodo di validità registro" >
        <script type='text/javascript' >
            $(document).ready(function () {
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-off.png"]').attr("src", "./img/alternative/checkbox-on.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-on.png"]').attr("src", "./img/alternative/checkbox-off.png").attr("width", "12").attr("heigth", "12");
                $('div[id^="Controllo_formato"] > img[src$="checkbox-field-warn.png"]').remove();
                
                  var elemento = $('div[id^="Controllo_formato"] img');
                if (elemento.attr('src') === './img/alternative/checkbox-off.png') {
                    elemento.attr('title', 'Tutte le ud versate rispettano il formato numero del registro');
                } else {
                    elemento.attr('title', 'Sono presenti ud versate che non rispettano il formato numero del registro');
                }
                
                
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Registri" />
        <sl:menu />

        <sl:content>
            <slf:messageBox /> 
            <sl:contentTitle title="Dettaglio periodo di validità registro"/>

            <div><input type="hidden" name="table" value="${fn:escapeXml(param.table)}" /></div>
                <c:if test="${sessionScope['###_FORM_CONTAINER']['aaRegistroUnitaDocList'].table['empty']}">
                    <slf:fieldBarDetailTag name="<%= StrutTipiForm.AARegistroUnitaDoc.NAME%>" hideBackButton="false"/> 
                </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['aaRegistroUnitaDocList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipiForm.AaRegistroUnitaDocList.NAME%>" />  
            </c:if>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
                <slf:section name="<%=StrutTipiForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.STRUTTURA%>"  width="w100" controlWidth="w40" labelWidth="w20"/>
                    <sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.StrutRif.ID_ENTE%>"  width="w100" controlWidth="w80" labelWidth="w20"/>
                </slf:section>
                <sl:newLine skipLine="true"/>

                <slf:section name="<%=StrutTipiForm.SRegistroUnitaDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.CD_REGISTRO_UNITA_DOC%>" width="w100" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.RegistroUnitaDoc.DS_REGISTRO_UNITA_DOC%>" width="w100" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                </slf:section>

                <slf:section name="<%=StrutTipiForm.SAaRegistroUnitaDoc.NAME%>" styleClass="importantContainer">  
                    <slf:lblField name="<%=StrutTipiForm.AARegistroUnitaDoc.AA_MIN_REGISTRO_UNITA_DOC%>" width="w100" controlWidth="w20" labelWidth="w20"/>
                    <slf:lblField name="<%=StrutTipiForm.AARegistroUnitaDoc.AA_MAX_REGISTRO_UNITA_DOC%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.AARegistroUnitaDoc.CD_FORMATO_NUMERO%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.AARegistroUnitaDoc.DS_FORMATO_NUMERO%>" width="w100" controlWidth="w20" labelWidth="w20"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.AARegistroUnitaDoc.FL_UPD_FMT_NUMERO%>" width="w100" controlWidth="w20" labelWidth="w40"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipiForm.AARegistroUnitaDoc.CONTROLLO_FORMATO%>" width="w100" controlWidth="w20" labelWidth="w40"/><sl:newLine />
                </slf:section>                               
            </slf:fieldSet>
            <sl:newLine skipLine="true"/>
            <div class="livello1"><b><font color="#d3101c">Riepilogo errori sul periodo di validit&agrave; del registro</font></b></div>
                    <sl:newLine skipLine="true"/>
                    <slf:listNavBar name="<%=StrutTipiForm.ErroriSuRegistroList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=StrutTipiForm.ErroriSuRegistroList.NAME%>"  />
                    <slf:listNavBar  name="<%=StrutTipiForm.ErroriSuRegistroList.NAME%>" />

            <sl:newLine skipLine="true"/>

            <div class="livello1"><b><font color="#d3101c">Lista parti del numero del registro</font></b></div>
                    <sl:newLine skipLine="true"/>
                    <slf:listNavBar name="<%=StrutTipiForm.DecParteNumRegistroList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%=StrutTipiForm.DecParteNumRegistroList.NAME%>"  />
                    <slf:listNavBar  name="<%=StrutTipiForm.DecParteNumRegistroList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

