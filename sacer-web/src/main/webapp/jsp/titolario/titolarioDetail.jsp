<%@ page import="it.eng.parer.slite.gen.form.StrutTitolariForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=StrutTitolariForm.TitolariTree.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {

                $('.caricaTitolarioBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Importazione": function () {
                            $(this).dialog("close");
                            window.location = "StrutTitolari.html?operation=loadImportaTitolario";
                        },
                        "Inserimento manuale": function () {
                            $(this).dialog("close");
                            window.location = "StrutTitolari.html?operation=loadWizard";
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                            window.location = "StrutTitolari.html?operation=annullaModifica";
                        }
                    }
                });

                $('.chiudiTitolarioBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            var date = $(".chiudiTitolarioBox #Dt_soppres").val();
                            var note = $(".chiudiTitolarioBox #Dl_note").val();
                            window.location = "StrutTitolari.html?operation=confermaChiusuraTitolario&Dt_soppres=" + date+ "&Dl_note=" + note;
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });
                
                $('.validaTitolarioBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            $(this).dialog("close");
                            window.location = "StrutTitolari.html?operation=confermaValidazioneTitolario";
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
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>

            <slf:messageBox />
            <c:if test="${!empty requestScope.caricaTitolarioBox}">
                <div class="messages caricaTitolarioBox ">
                    <ul>
                        <li class="message info ">Seleziona la modalità di caricamento del titolario</li>
                    </ul>
                </div>
            </c:if>
            <c:if test="${!empty requestScope.chiudiTitolarioBox}">
                <div class="messages chiudiTitolarioBox ">
                    <ul>
                        <li class="message info ">
                            <p>Inserisci la data di fine validità della voce e le note di chiusura</p>
                            <sl:newLine />
                            <div class="containerLeft w100">
                                <label class="slLabel w30" for="Dt_soppres">Data di fine validità</label>
                                <input id="Dt_soppres" class="slText w20 date hasDatepicker" type="text" value="" name="Dt_soppres">
                            </div>
                            <sl:newLine />
                            <div class="containerLeft w100">
                                <label class="slLabel w30" for="Dl_note">Note</label>
                                <input id="Dl_note" class="slText w70" type="text" value="" name="Dl_note">
                            </div>
                        </li>
                    </ul>
                </div>
            </c:if>
            <c:if test="${!empty requestScope.validaTitolarioBox}">
                <div class="messages validaTitolarioBox ">
                    <ul>
                        <li class="message info ">Desideri validare il titolario ?</li>
                    </ul>
                </div>
            </c:if>
            <sl:contentTitle title="<%=StrutTitolariForm.TitolariTree.DESCRIPTION%>"/>
            <sl:newLine skipLine="true"/>
            <slf:listNavBarDetail name="<%= StrutTitolariForm.TitolariList.NAME%>" />   
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=StrutTitolariForm.Struttura.NAME%>" styleClass="importantContainer">  
                <slf:lblField name="<%=StrutTitolariForm.StrutRif.STRUTTURA%>" width="w100" labelWidth="w10" controlWidth="w90" />
                <sl:newLine />
                <slf:lblField name="<%=StrutTitolariForm.StrutRif.ID_ENTE%>" width="w100" labelWidth="w10" controlWidth="w90"/>
            </slf:section>
            <slf:section name="<%=StrutTitolariForm.DocTrasmSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutTitolariForm.DocTrasm.CD_REGISTRO_DOC_INVIO%>" colSpan= "2" />
                <slf:lblField name="<%=StrutTitolariForm.DocTrasm.AA_DOC_INVIO%>" colSpan= "1" />
                <slf:lblField name="<%=StrutTitolariForm.DocTrasm.CD_DOC_INVIO%>" colSpan= "1" />
                <sl:newLine skipLine="true"/>
                <slf:lblField name="<%=StrutTitolariForm.DocTrasm.DT_DOC_INVIO%>" colSpan="1" controlWidth="w70"/>
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.LOAD_UD%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine skipLine="true"/>
            </slf:section>
            <slf:section name="<%=StrutTitolariForm.TitolarioSection.NAME%>" styleClass="importantContainer">
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.NM_TITOL%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.DT_ISTITUZ%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.DT_SOPPRES%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.DL_NOTE%>" colSpan="2" controlWidth="w70"/>
                <sl:newLine />
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.DT_VISUALIZ%>" colSpan="1" controlWidth="w70"/>
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.RELOAD_TITOLARIO%>" colSpan="1" controlWidth="w70"/>
                <sl:newLine />
            </slf:section>
            <sl:pulsantiera>
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.VALIDA_TITOLARIO%>" colSpan="1" controlWidth="w70"/>
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.CHIUDI_TITOLARIO%>" colSpan="1" controlWidth="w70"/>
                <slf:lblField name="<%=StrutTitolariForm.TitolarioDetail.ESPORTA_TITOLARIO%>" colSpan="1" controlWidth="w70"/>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:tree name="<%=StrutTitolariForm.TitolariTree.NAME%>" additionalJsonParams="\"core\" : { \"check_callback\" : true } , \"contextmenu\" : {\"select_node\": false,\"show_at_node\": false, \"items\" : function(node){var idVoce = node.id; var config; if (idVoce != 0)  { config = {\"dettaglio\":{\"separator_before\":false,\"separator_after\":true,\"label\":\"Dettaglio\",\"icon\":false,\"action\":function(obj){var ref = $.jstree.reference(obj.reference); node = ref.get_node(obj.reference); var idVoce = node.id; var position = node.li_attr.tableposition;  if (idVoce != 0)  { window.location = \"StrutTitolari.html?operation=showDettaglioVoce&id=\" + idVoce + \"&position=\" + position ; }       }}}; } return config;}}"/>
            <script type="text/javascript">
                var tree = $("#tree_TitolariTree");
                tree.on('loaded.jstree', function () {
                    //tree.jstree('open_all');
                    tree.jstree('open_node', 'li#0');
                });
                tree.on("select_node.jstree", function (event, data) {
                    tree.jstree('toggle_node', data.node);
                });
            </script>
        </sl:content>

        <sl:footer />
    </sl:body>
</sl:html>