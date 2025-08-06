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

<%@ page import="it.eng.parer.slite.gen.form.StruttureForm "%>

<%@ include file="../../include.jsp"%>



<sl:html>
    <sl:head title="Gestione Categorie Tipo UD" >
        <script type="text/javascript" src="<c:url value="/js/sips/customStrutMessageBox.js"/>" ></script>
            
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" description="Strutture - Registri" />
        <sl:menu />
      
        <sl:content>
            <sl:contentTitle title="Gestione Categorie UD"/>
            <slf:messageBox />
            <c:if test="${!empty requestScope.confRemove}">
                <div class="messages confRemove ">
                    <ul>
                        <li class="message warning ">La procedura cancellerà il nodo selezionato. Procedere?</li>
                    </ul>
                </div>
            </c:if>
            
            <slf:fieldSet>
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=StruttureForm.CategorieTipoUd.CD_CATEG_TIPO_UNITA_DOC%>" colSpan= "2" controlWidth="w40" />
                <slf:lblField name="<%=StruttureForm.CategorieTipoUd.SALVA_CATEGORIE_TIPO_UD%>" colSpan= "2" controlWidth="w40" />
                <sl:newLine /> 
                <slf:lblField name="<%=StruttureForm.CategorieTipoUd.DS_CATEG_TIPO_UNITA_DOC%>" colSpan= "4" controlWidth="w40" /><sl:newLine />
                <slf:lblField name="<%=StruttureForm.CategorieTipoUd.ID_CATEG_TIPO_UNITA_DOC_PADRE%>" colSpan= "4" controlWidth="w40" /><sl:newLine />
                <%--<slf:lblField name="<%=StruttureForm.CategorieTipoUd.ID_TIPO_SERVIZIO_CONSERV%>" colSpan= "4" controlWidth="w40" /><sl:newLine />
                <slf:lblField name="<%=StruttureForm.CategorieTipoUd.ID_TIPO_SERVIZIO_ATTIV%>" colSpan= "4" controlWidth="w40" /><sl:newLine />--%>
            </slf:fieldSet>   
            <sl:newLine skipLine="true"/>
                
            <slf:tree name="<%=StruttureForm.GestCatTiUdTree.NAME%>" additionalJsonParams="\"core\" : { \"expand_selected_onload\" : true, \"check_callback\" : true }"/>
            <script type="text/javascript" src="<c:url value="/js/custom/customGestCatTiUdTree.js" />" ></script>
            
        </sl:content>
        
        <sl:footer />
    </sl:body>

</sl:html>

