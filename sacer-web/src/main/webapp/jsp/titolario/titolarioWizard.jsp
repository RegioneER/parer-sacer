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

<%@ page import="it.eng.parer.slite.gen.form.StrutTitolariForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTitolariForm.InserimentoWizard.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {
                $("#Cd_composito_visualizzato").attr("disabled", true);

                $("#Cd_voce_titol").change(function () {
                    var livello = $("#Ni_livello").val();
                    if (livello === '1') {
                        var nodo = $("#Cd_voce_titol").val();
                        $("#Cd_composito_visualizzato").val(nodo);
                        $("#Cd_composito_voce_titol").val(nodo);
                    } else {
                        var separatore = $("#Cd_sep_livello").val();
                        var nodo = $("#Cd_voce_titol").val();
                        var padre = $("#Cd_composito_voce_padre").val();
                        var composito = padre + separatore + nodo;

                        $("#Cd_composito_visualizzato").val(composito);
                        $("#Cd_composito_voce_titol").val(composito);
                    }
                });

                $("fieldset#VoceSection #Dt_soppres").change(function () {
                    var date = $(this).val();
                    if (date !== null) {
                        var chiudiVoceBox = !($(".chiudiVoceBox").length);
                        var showWarning = !($("#Warning_shown").is(':checked'));
                        if (showWarning && chiudiVoceBox) {
                            $(this).after("<div class=\"messages\"><ul><li class=\"message warning \">Attenzione: le voci figlie della voce selezionata erediteranno la data di fine validit\u00E0 della voce padre.</li></ul></div>");
                            $(".messages").dialog({
                                autoOpen: true,
                                width: 600,
                                modal: false,
                                resizable: false,
                                dialogClass: "alertBox",
                                closeOnEscape: true,
                                buttons: {
                                    "Ok": function () {
                                        $(this).dialog("close");
                                    }
                                }
                            });
                            $("#Warning_shown").prop("checked", true);
                        }
                    }
                });

                $('.chiudiVoceBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            var date = $(".chiudiVoceBox #Dt_soppres").val();
                            var note = $(".chiudiVoceBox #Dl_note").val();
                            $.post("StrutTitolari.html", {operation: "chiudiVoce", Dt_soppres: date, Dl_note: note});
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                            window.location = "StrutTitolari.html?operation=cleanVoce";
                        }
                    }
                });

                $("#Ni_anni_conserv").change(function () {
                    var value = $("#Ni_anni_conserv").val();
                    if (value) {
                        if (value === '9999') {
                            $("#Conserv_unlimited").val("1");
                        } else {
                            $("#Conserv_unlimited").val("0");
                        }
                    }
                });

                if ($("#Ni_anni_conserv").length) {
                    if ($("#Ni_anni_conserv").val().length !== 0) {
                        $("#Ni_anni_conserv").trigger('change');
                    }
                }
            });
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <c:if test="${!empty requestScope.customBox}">
                <div class="messages chiudiVoceBox ">
                    <ul>
                        <li class="message info ">
                            <p>Inserisci la data di fine validità della voce e le note di chiusura</p>
                            <sl:newLine />
                            <div class="containerLeft w100">
                                <label class="slLabel w30" for="Dt_soppres">Data di fine validità</label>
                                <input id="Dt_soppres" class="slText w20 date hasDatepicker" type="text" value="" name="Dt_soppres">
                            </div>
                            <sl:newLine />
                            <slf:lblField name="<%=StrutTitolariForm.VociInserimento.DL_NOTE%>" width="w100" labelWidth="w30" controlWidth="w70"/>
                        </li>
                    </ul>
                </div>
            </c:if>
            <slf:section name="<%=StrutTitolariForm.Struttura.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutTitolariForm.StrutRif.STRUTTURA%>" colSpan= "2" labelWidth="w20" controlWidth="w100" />
                <sl:newLine />
                <slf:lblField name="<%=StrutTitolariForm.StrutRif.ID_ENTE%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
            </slf:section>
            <!--<h2><b><font color="#d3101c"></font></b></h2>-->
            <slf:wizard name="<%= StrutTitolariForm.InserimentoWizard.NAME%>">
                <slf:wizardNavBar name="<%=StrutTitolariForm.InserimentoWizard.NAME%>" />
                <sl:newLine skipLine="true"/>   
                <slf:step name="<%= StrutTitolariForm.InserimentoWizard.DATI_TITOLARIO%>">
                    <slf:section name="<%=StrutTitolariForm.DocTrasmSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.CD_REGISTRO_DOC_INVIO%>" colSpan= "2" />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.AA_DOC_INVIO%>" colSpan= "1" />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.CD_DOC_INVIO%>" colSpan= "1" />
                        <sl:newLine skipLine="true"/>
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.DT_DOC_INVIO%>" colSpan="1" controlWidth="w70"/>
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:section name="<%=StrutTitolariForm.IntestazioneSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.NM_TITOL%>" width="w100" controlWidth="w80" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.DT_ISTITUZ%>" colSpan="1" controlWidth="w70" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.DT_SOPPRES%>" colSpan="1" controlWidth="w70" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.NI_LIVELLI%>" colSpan="1" controlWidth="w70" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.CD_SEP_FASCICOLO%>" colSpan="1" controlWidth="w70" labelWidth="w20"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.DatiTitolarioInserimento.DL_NOTE%>" colSpan="2" controlWidth="w100" labelWidth="w20"/>
                    </slf:section>
                </slf:step>
                <slf:step name="<%= StrutTitolariForm.InserimentoWizard.LIVELLI_TITOLARIO%>">
                    <slf:section name="<%=StrutTitolariForm.LivelliSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=StrutTitolariForm.LivelliInserimento.NM_LIVELLO_TITOL%>" colSpan="1" controlWidth="w70"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.LivelliInserimento.NI_LIVELLO%>" colSpan="1" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.LivelliInserimento.TI_FMT_VOCE_TITOL%>" colSpan="1" controlWidth="w70"/>
                        <sl:newLine />
                        <slf:lblField name="<%=StrutTitolariForm.LivelliInserimento.CD_SEP_LIVELLO%>" colSpan="1" controlWidth="w70"/>
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTitolariForm.LivelliInserimento.ADD_LIVELLO%>" colSpan="1" controlWidth="w70"/>
                    <slf:lblField name="<%=StrutTitolariForm.LivelliInserimento.CLEAN_LIVELLO%>" colSpan="1" controlWidth="w70"/>
                    <sl:newLine skipLine="true"/>
                    <slf:container width="w50">
                        <slf:listNavBar name="<%= StrutTitolariForm.LivelliList.NAME%>" pageSizeRelated="true"/>
                        <slf:list name="<%= StrutTitolariForm.LivelliList.NAME%>"/>
                        <slf:listNavBar  name="<%= StrutTitolariForm.LivelliList.NAME%>" />
                    </slf:container>
                </slf:step>
                <slf:step name="<%= StrutTitolariForm.InserimentoWizard.VOCI_TITOLARIO%>">
                    <slf:section name="<%=StrutTitolariForm.VoceSection.NAME%>" styleClass="importantContainer">
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.ID_VOCE_TITOL%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.NI_LIVELLO%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CD_COMPOSITO_VOCE_PADRE%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CD_COMPOSITO_VISUALIZZATO%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CD_COMPOSITO_VOCE_TITOL%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CD_SEP_LIVELLO%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.WARNING_SHOWN%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CD_VOCE_TITOL%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.NI_ORD_VOCE_TITOL%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.DS_VOCE_TITOL%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.NI_ANNI_CONSERV%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CONSERV_UNLIMITED%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.FL_USO_CLASSIF%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.DL_NOTE%>" width="w100" controlWidth="w70" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.DT_ISTITUZ%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                        <slf:lblField name="<%=StrutTitolariForm.VociInserimento.DT_SOPPRES%>" colSpan="1" controlWidth="w100" labelWidth="w20"/>
                        <sl:newLine/>
                    </slf:section>
                    <sl:newLine skipLine="true"/>
                    <slf:lblField name="<%=StrutTitolariForm.VociInserimento.ADD_VOCE%>" colSpan="1" controlWidth="w70"/>
                    <slf:lblField name="<%=StrutTitolariForm.VociInserimento.CLEAN_VOCE%>" colSpan="1" controlWidth="w70"/>
                    <sl:newLine skipLine="true"/>
                    <slf:listNavBar name="<%= StrutTitolariForm.VociList.NAME%>" pageSizeRelated="true"/>
                    <slf:selectList name="<%= StrutTitolariForm.VociList.NAME%>" addList="false"/>
                    <slf:listNavBar  name="<%= StrutTitolariForm.VociList.NAME%>" />
                </slf:step>
                <slf:step name="<%= StrutTitolariForm.InserimentoWizard.ALBERO_TITOLARIO%>">
                    <slf:tree name="<%=StrutTitolariForm.TitolariTree.NAME%>" additionalJsonParams="\"core\" : { \"check_callback\" : true } , \"contextmenu\" : {\"select_node\": false,\"show_at_node\": false, \"items\" : function(node){ var config; return config;}}"/>
                    <script type="text/javascript">
                        var tree = $("#tree_TitolariTree");
                        tree.on('loaded.jstree', function () {
                            tree.jstree('open_node', 'li#0');
                        });
                        tree.on("select_node.jstree", function (event, data) {
                            tree.jstree('toggle_node', data.node);
                        });
                    </script>
                </slf:step>
            </slf:wizard>
        </sl:content>

        <sl:footer />
    </sl:body>
</sl:html>
