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

<%@ page import="it.eng.parer.slite.gen.form.StrutTipoStrutForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Dettaglio tipo componente documento" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('.confermaDisattivazioneXsd').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutTipoStrut.html?operation=confermaDisattivazioneXsd";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Tipi Componenti"/>
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.confermaDisattivazioneXsd}">
                <div class="messages confermaDisattivazioneXsd ">
                    <ul>
                        <li class="message info ">Desideri disattivare la versione di XSD ?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="Dettaglio tipo componente documento"/>


            <c:if test="${sessionScope['###_FORM_CONTAINER']['tipoCompDocList'].table['empty']}">
                <slf:fieldBarDetailTag name="<%= StrutTipoStrutForm.TipoCompDoc.NAME%>" hideBackButton="true" /> 
            </c:if>   

            <c:if test="${!(sessionScope['###_FORM_CONTAINER']['tipoCompDocList'].table['empty']) }">
                <slf:listNavBarDetail name="<%= StrutTipoStrutForm.TipoCompDocList.NAME%>" /> 
            </c:if>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet>
            <sl:newLine />
            <slf:section name="<%=StrutTipoStrutForm.Struttura.NAME%>" styleClass="importantContainer">  
                    <%-- <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.NM_STRUT%>" colSpan= "4" controlWidth="w40" />
                <sl:newLine />
                <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.DS_STRUT%>"  colSpan= "4" controlWidth="w40" />
                    <sl:newLine />--%>
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.STRUTTURA%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
                <sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.StrutRif.ID_ENTE%>"  colSpan= "2" labelWidth="w20" controlWidth="w70" />
            </slf:section>
            <slf:section name="<%=StrutTipoStrutForm.STipoStrutDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.NM_TIPO_STRUT_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoStrutDoc.DS_TIPO_STRUT_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
            </slf:section>
            <slf:section name="<%=StrutTipoStrutForm.STipoCompDoc.NAME%>" styleClass="importantContainer"> 
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.ID_TIPO_COMP_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.NM_TIPO_COMP_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.DS_TIPO_COMP_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.TI_USO_COMP_DOC%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.DT_ISTITUZ%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.DT_SOPPRES%>"colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
            </slf:section>
             <slf:section name="<%=StrutTipoStrutForm.AllineamentoFormatiAmmessi.NAME%>" styleClass="importantContainer">
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.FL_GESTITI%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine /><%-- --%>
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.FL_IDONEI%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    <slf:lblField name="<%=StrutTipoStrutForm.TipoCompDoc.FL_DEPRECATI%>" colSpan= "2" labelWidth="w20" controlWidth="w70"/><sl:newLine />
                    </slf:section>
             </slf:fieldSet>
           <sl:newLine skipLine="true"/>

            <c:if test="${(sessionScope['###_FORM_CONTAINER']['tipoCompDoc'].status eq 'view') }">
                <!--  piazzo la lista con i risultati -->
                 <sl:newLine skipLine="true"/>
                 <slf:section name="<%=StrutTipoStrutForm.XsdDatiSpecTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipoStrutForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipoStrutForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipoStrutForm.XsdDatiSpecList.NAME%>" />
                </slf:section>
                <slf:section name="<%=StrutTipoStrutForm.FormatoFileAmmessoTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" />
                    <slf:listNavBar  name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" />
                    
                </slf:section>
                 <slf:section name="<%=StrutTipoStrutForm.STipoRapprCompTab.NAME%>" styleClass="importantContainer"> 
                    <slf:listNavBar name="<%= StrutTipoStrutForm.TipoRapprCompAmmessoDaTipoCompList.NAME%>" pageSizeRelated="true"/>
                    <slf:list name="<%= StrutTipoStrutForm.TipoRapprCompAmmessoDaTipoCompList.NAME%>" />
                    <slf:listNavBar  name="<%= StrutTipoStrutForm.TipoRapprCompAmmessoDaTipoCompList.NAME%>" />
                </slf:section>
                <%--
                <slf:tab  name="<%=StrutTipoStrutForm.DecTipoCompDocTab.NAME%>" tabElement="DecFormatoFileAmmesso">


                    <slf:listNavBar name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" />
                    <slf:listNavBar  name="<%= StrutTipoStrutForm.FormatoFileAmmessoList.NAME%>" />

                </slf:tab>

                <slf:tab  name="<%=StrutTipoStrutForm.DecTipoCompDocTab.NAME%>" tabElement="DecTipoCompDocXsdDatiSpec">

                  
                    <slf:listNavBar name="<%= StrutTipoStrutForm.XsdDatiSpecList.NAME%>" pageSizeRelated="true"/>
<slf:list name="<%= StrutTipoStrutForm.XsdDatiSpecList.NAME%>"  />
                    <slf:listNavBar  name="<%= StrutTipoStrutForm.XsdDatiSpecList.NAME%>" />

                </slf:tab>
                --%>
            </c:if>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

