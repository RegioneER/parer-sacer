<%@ page import="it.eng.parer.slite.gen.form.SerieUDForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="Serie da firmare" >
        <script type="text/javascript" src="<c:url value='/js/custom/customPollFirma.js' />" ></script>
        <script type='text/javascript'>
            $(document).ready(function () {
                pollSerie();
                $('.credenzialiFirmaBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            var idAmbiente = $("#Id_ambiente").val();
                            var idEnte = $("#Id_ente").val();
                            var idStrut = $("#Id_strut").val();
                            var user = $("#User").val();
                            var passwd = $("#Passwd").val();
                            var otp = $("#Otp").val();
                            $(this).dialog("close");


                            var textDialog = "<div id=\"waiting\" class=\"messages infobox \"><ul><span class=\"ui-icon ui-icon-info\"></span><li class=\"message info \">Attendere...</li></ul></div>";
                            $(textDialog).appendTo($("div#content"));
                            $("div#waiting").dialog({
                                autoOpen: true,
                                width: 600,
                                modal: true,
                                closeOnEscape: false,
                                resizable: false,
                                beforeClose: function (event, ui) {
                                    return false;
                                },
                                dialogClass: "noclose"
                            });


                            $.post("SerieUD.html", {
                                operation: "firmaSerieHsmJs",
                                Id_ambiente: idAmbiente,
                                Id_ente: idEnte,
                                Id_strut: idStrut,
                                User: user,
                                Passwd: passwd,
                                Otp: otp
                            }).done(
                                // LOGICA CHIARAMENTE MIGLIORABILE
                                function (data) {
                                    $("div#waiting").dialog("close");
                                    $("div#waiting").remove();

                                    var object = data.map[0];
                                    if (object.error && object.error.length !== 0) {
                                        // Ho ricevuto un errore, gestisco la messageBox
                                        var textDialog = "<div class=\"messages errorbox\"><ul><span class=\"ui-icon ui-icon-alert\"></span>";
                                        $.each(object.error, function () {
                                            textDialog += "<li class=\"message error \">" + this + "</li>";
                                        });
                                        textDialog += "</ul></div>"
                                        $(textDialog).appendTo($("div#content"));
                                        $("div.messages").dialog({
                                            autoOpen: true,
                                            width: 600,
                                            modal: true,
                                            closeOnEscape: true,
                                            resizable: false,
                                            dialogClass: "alertBox",
                                            buttons: {
                                                "Ok": function () {
                                                    $(this).dialog("close");
                                                }
                                            }
                                        });
                                    } else if (object.status && object.status.length !== 0) {
                                        // Sessione già terminata con successo, gestisco la messageBox
                                        var box;
                                        var icon;
                                        var message;
                                        if (object.status === 'OK') {
                                            box = "infobox";
                                            icon = message = "info";
                                        } else {
                                            // WARNING
                                            box = "warnbox";
                                            icon = "alert";
                                            message = "warning";
                                        }

                                        var textDialog = "<div class=\"messages " + box + "\"><ul><span class=\"ui-icon ui-icon-" + icon + "\"></span>";
                                        $.each(object.error, function () {
                                            textDialog += "<li class=\"message " + message + " \">" + this + "</li>";
                                        });
                                        textDialog += "</ul></div>"
                                        $(textDialog).appendTo($("div#content"));
                                        $("div.messages").dialog({
                                            autoOpen: true,
                                            width: 600,
                                            modal: true,
                                            closeOnEscape: true,
                                            resizable: false,
                                            dialogClass: "alertBox",
                                            buttons: {
                                                "Ok": function () {
                                                    $(this).dialog("close");
                                                }
                                            }
                                        });
                                    } else {
                                        /* TODO LB: Secondo me non dovrebbe ricaricare la pagina degli elenchi ma mostrare un messaggio di info e svuotare la lista selezionati
                                         (Ovviamente chiamando un metodo apposito)*/
                                        window.location = "SerieUD.html?operation=loadListaSerieDaFirmare&cleanFilter=false";
                                    }
                                });
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

            <div class="firmaBox "></div>
            <c:if test="${!empty requestScope.customSerieSelect}">
                <div class="credenzialiFirmaBox" title="Inserire credenziali per firmare">
                    <ul>
                        <li class="message info ">
                        <slf:lblField name="<%=SerieUDForm.FiltriSerieDaFirmare.USER%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />

                        <div class="containerLeft w100">
                            <label class="slLabel w20" for="Passwd">Password:&nbsp;</label>
                            <input id="Passwd" class="slText w60" type="password" name="Passwd" />
                        </div><sl:newLine />

                        <div class="containerLeft w100">
                            <label class="slLabel w20" for="Otp">OTP:&nbsp;</label>
                            <input id="Otp" class="slText w60" type="password" name="Otp" />
                        </div><sl:newLine />
                        </li>
                    </ul>
                </div>
            </c:if>
            
            <sl:newLine skipLine="true"/>

            <sl:contentTitle title="SERIE DI UNIT&Agrave; DOCUMENTARIE DA FIRMARE"/>
            <sl:contentTitle title="Selezionare le serie che si desidera firmare" showHelpBtn="false"/>
           
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <slf:lblField colSpan="4" name="<%=SerieUDForm.FiltriSerieDaFirmare.ID_AMBIENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=SerieUDForm.FiltriSerieDaFirmare.ID_ENTE%>" />
                <sl:newLine />
                <slf:lblField colSpan="4" name="<%=SerieUDForm.FiltriSerieDaFirmare.ID_STRUT%>" />
            </slf:fieldSet>

            <sl:pulsantiera>
                <!-- piazzo i bottoni di ricerca ed inserimento -->
                <slf:lblField name="<%=SerieUDForm.FiltriSerieDaFirmare.RICERCA_SERIE_DA_FIRMARE_BUTTON%>" width="w25" />
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>
            <slf:fieldSet borderHidden="true" styleClass="">
                <span class="legend">Serie da firmare</span>
            </slf:fieldSet>
            <sl:newLine />
            <!--  piazzo la lista con i risultati -->
            <slf:listNavBar name="<%= SerieUDForm.SerieDaFirmareList.NAME%>" pageSizeRelated="true"/>
            <slf:selectList name="<%= SerieUDForm.SerieDaFirmareList.NAME%>" addList="true"/>
            <slf:listNavBar  name="<%= SerieUDForm.SerieDaFirmareList.NAME%>" />
            <sl:newLine skipLine="true"/>
            <sl:pulsantiera >
                <slf:buttonList name="<%= SerieUDForm.ListaSerieDaFirmareButtonList.NAME%>" >
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaFirmareButtonList.SELECT_ALL_SERIE_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaFirmareButtonList.DESELECT_ALL_SERIE_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaFirmareButtonList.FIRMA_INDICI_AIPSERIE_HSM_BUTTON%>" colSpan="2"/>
                    <slf:lblField name="<%=SerieUDForm.ListaSerieDaFirmareButtonList.MARCATURA_INDICI_AIPSERIE_BUTTON%>" colSpan="2"/>
                </slf:buttonList>
            </sl:pulsantiera>
            <sl:newLine skipLine="true"/>

            <slf:section name="<%= SerieUDForm.SerieSelSection.NAME%>" styleClass="importantContainer">
                <slf:listNavBar name="<%= SerieUDForm.SerieSelezionateList.NAME%>" pageSizeRelated="true"/>
                <slf:selectList name="<%= SerieUDForm.SerieSelezionateList.NAME%>"  addList="false"/>
                <slf:listNavBar  name="<%= SerieUDForm.SerieSelezionateList.NAME%>" />
            </slf:section>

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>
