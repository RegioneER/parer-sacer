<!--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
-->

<%@ page import="it.eng.parer.slite.gen.form.FormatiForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio Formato" />

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />

        <sl:content>

            <slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="Dettaglio Formato"/>


            <c:if test="${sessionScope['###_FORM_CONTAINER']['formatoFileStandardList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= FormatiForm.FormatoFileStandard.NAME%>" hideBackButton="${!(sessionScope['###_FORM_CONTAINER']['formatoFileStandardList'].status eq 'view')}" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['formatoFileStandardList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= FormatiForm.FormatoFileStandardList.NAME%>" />  
            </c:if> 


            <slf:fieldSet borderHidden="true">
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.NM_FORMATO_FILE_STANDARD%>" colSpan="4" controlWidth="w40" />
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.DS_FORMATO_FILE_STANDARD%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.CD_VERSIONE%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.DS_COPYRIGHT%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.NM_MIMETYPE_FILE%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.TI_ESITO_CONTR_FORMATO%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.NI_PUNTEGGIO_TOTALE%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />   
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.FL_FORMATO_CONCAT%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.NT_IDONEITA%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
                <slf:lblField name="<%=FormatiForm.FormatoFileStandard.DT_VALUTAZIONE_FORMATO%>" colSpan="4" controlWidth="w40"/>
                <sl:newLine />
                <sl:newLine skipLine="true"/>
            </slf:fieldSet >
            
            <c:if test="${(sessionScope['###_FORM_CONTAINER']['formatoFileStandard'].status eq 'view') }">
                <slf:tab  name="<%=FormatiForm.DecFormatoFileStandardTab.NAME%>" tabElement="DecFormatoFileValutazione">

                    <slf:listNavBar name="<%= FormatiForm.FormatoFileParametriValutazioneList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FormatiForm.FormatoFileParametriValutazioneList.NAME%>"  />
                    <slf:listNavBar  name="<%= FormatiForm.FormatoFileParametriValutazioneList.NAME%>" />

                </slf:tab>
                <slf:tab  name="<%=FormatiForm.DecFormatoFileStandardTab.NAME%>" tabElement="DecEstensioneFile">

                    <slf:listNavBar name="<%= FormatiForm.EstensioneFileList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FormatiForm.EstensioneFileList.NAME%>"  />
                    <slf:listNavBar  name="<%= FormatiForm.EstensioneFileList.NAME%>" />

                </slf:tab>

                <slf:tab  name="<%=FormatiForm.DecFormatoFileStandardTab.NAME%>" tabElement="DecFormatoFileBusta">

                    <slf:listNavBar name="<%= FormatiForm.FormatoFileBustaList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= FormatiForm.FormatoFileBustaList.NAME%>"  />
                    <slf:listNavBar  name="<%= FormatiForm.FormatoFileBustaList.NAME%>" />

                </slf:tab>
                
            </c:if>


        </sl:content>
        <sl:footer />

    </sl:body>

</sl:html>
