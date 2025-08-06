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

<%@ page import="it.eng.parer.slite.gen.form.HomeForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Home page" >
        <script type='text/javascript' >

            $(document).on('click', 'table.listaNews > tbody > tr.news > td > img.apriChiudi', apriChiudiNewsDaFreccia);

            function CTableHandler() {
                this.load = CTableHandlerLoad;
            }

            function CTableHandlerLoad() {
                var righeNascoste = $("table > tbody > tr.nascondiRiga");
                CMostraRighe(righeNascoste);
            }

            function CMostraRighe(righeNascoste) {
                righeNascoste.hide();
            }

            function apriChiudiNewsDaFreccia(event) {
                if ($(this).parent().parent().is('.rigaVisibile')) {
                    $(this).parent().parent().next().hide();
                    $(this).parent().parent().removeClass('rigaVisibile');
                    //$(this).attr('src', './img/toolBar/closeGreen.png');
                    $(this).attr('src', './img/window/aperto.png');
                } else {
                    $(this).parent().parent().next().show();
                    $(this).parent().parent().addClass('rigaVisibile');
                    //$(this).attr('src', './img/toolBar/openGreen.png');
                    $(this).attr('src', './img/window/chiuso.png');
                }
            }

            $(document).ready(function () {
                var imm = $("img.apriChiudi", this);
                //imm.attr('src', './img/toolBar/closeGreen.png');
                imm.attr('src', './img/window/aperto.png');
                var tableHandler = new CTableHandler();
                tableHandler.load();

                $("table.listaNews > tbody > tr.news:first img.apriChiudi").trigger('click');
            });
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="${TitoloHomeDinamico}" />

            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=HomeForm.ContenutoSacerTotaliUdDocComp.TITOLO%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=HomeForm.ContenutoSacerTotaliUdDocComp.NUM_UD%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=HomeForm.ContenutoSacerTotaliUdDocComp.NUM_DOC%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=HomeForm.ContenutoSacerTotaliUdDocComp.NUM_COMP%>" colSpan="2" />
                <sl:newLine />
                <slf:lblField name="<%=HomeForm.ContenutoSacerTotaliUdDocComp.DIM_BYTES%>" colSpan="2" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <!-- piazzo il bottone -->
                <slf:lblField name="<%=HomeForm.ContenutoSacerTotaliUdDocComp.SCARICA_DISCIPLINARE_BUTTON%>" width="w25" />
            </sl:pulsantiera>


            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= HomeForm.ContenutoSacerList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= HomeForm.ContenutoSacerList.NAME%>" />
            <slf:listNavBar name="<%= HomeForm.ContenutoSacerList.NAME%>" />

            <table class="listaNews" style="table-layout: fixed; width: 100%" >
                <c:forEach items="${news}" var="current">
                    <tr class="news" >

                        <td style="width:15px;cursor:pointer"><img alt="Leggi News"  class="apriChiudi" src="./img/window/chiuso.png" /></td>
                        <td><p style="text-align:left; word-wrap: break-word;"><c:out value="${current.dsOggetto}" escapeXml="false" /></p></td>
                            <%-- <td><c:out value="${current.dtIniPubblic}" /><td> --%>
                    </tr>
                    <tr class="nascondiRiga">
                        <td>&nbsp;</td>
                        <td style="word-wrap:break-word; "><c:out value="${current.dlTesto}" escapeXml="false"/></td>

                    </tr>
                </c:forEach>
            </table>

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>
