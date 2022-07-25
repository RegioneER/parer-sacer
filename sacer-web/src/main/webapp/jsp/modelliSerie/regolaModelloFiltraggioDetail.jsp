<%@ page import="it.eng.parer.slite.gen.form.ModelliSerieForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="<%=ModelliSerieForm.RegoleFiltraggioDetail.DESCRIPTION%>" >
        <script type="text/javascript">
            $(document).ready(function () {
                $('.datiSpecBox').dialog({
                    autoOpen: true,
                    width: 600,
                    modal: true,
                    closeOnEscape: true,
                    resizable: false,
                    dialogClass: "alertBox",
                    buttons: {
                        "Ok": function () {
                            var tipoDoc = $("#Id_tipo_doc_dati_spec_combo").val();
                            var nmTipoDoc = $("#Id_tipo_doc_dati_spec_combo option:selected").text();
                            $.getJSON("ModelliSerie.html", {operation: "populateIdTipoDocDatiSpecFiltraggioOnTriggerJs",
                                Id_tipo_doc_dati_spec: tipoDoc,
                                Nm_tipo_doc_dati_spec: nmTipoDoc
                            }).done(function (data) {
                                CAjaxDataFormWalk(data);
                            });
                            $(this).dialog("close");
                        },
                        "Annulla": function () {
                            $(this).dialog("close");
                        }
                    }
                });

                $('#Id_ente').change(function () {
                    var value = $(this).val();
                    if (value) {
                        $.getJSON("ModelliSerie.html", {operation: "triggerRegoleFiltraggioDetailId_enteOnTriggerJs",
                            Id_ente: value
                        }).done(function (data) {
                            CAjaxDataFormWalk(data);
                        });
                    }
                });
                $('#Id_strut').change(function () {
                    var value = $(this).val();
                    if (value) {
                        $.getJSON("ModelliSerie.html", {operation: "triggerRegoleFiltraggioDetailId_strutOnTriggerJs",
                            Id_strut: value
                        }).done(function (data) {
                            CAjaxDataFormWalk(data);
                        });
                    }
                });

            });
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <slf:messageBox />
        <c:if test="${!empty requestScope.customModelloTipoDocFiltraggioBox}">
            <div class="messages datiSpecBox ">
                <ul>
                    <li class="message info ">
                        <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.NM_AMBIENTE%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                        <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.ID_ENTE%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                        <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.ID_STRUT%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                        <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.ID_TIPO_DOC_DATI_SPEC_COMBO%>" width="w100" controlWidth="w60" labelWidth="w20" /><sl:newLine />
                    </li>
                </ul>
            </div>
        </c:if>
        <sl:content>
            <sl:contentTitle title="<%=ModelliSerieForm.RegoleFiltraggioDetail.DESCRIPTION%>"/>

            <sl:newLine skipLine="true"/>
            <slf:fieldBarDetailTag name="<%= ModelliSerieForm.RegoleFiltraggioDetail.NAME%>" hideBackButton="true" hideDeleteButton="true" />
            <slf:messageBox />

            <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.NM_AMBIENTE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            <slf:section name="<%=ModelliSerieForm.InfoModelloSerieSection.NAME%>" styleClass="importantContainer w100">
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.NM_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
                <slf:lblField name="<%=ModelliSerieForm.ModelliTipiSerieDetail.DS_MODELLO_TIPO_SERIE%>" width="w100" controlWidth="w40" labelWidth="w20"/><sl:newLine />
            </slf:section>

            <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.NM_TIPO_DOC_DATI_SPEC%>" colSpan= "2" labelWidth="w20" controlWidth="w100"/>
            <slf:lblField name="<%=ModelliSerieForm.RegoleFiltraggioDetail.CERCA_TIPO_DOC_FILTRAGGIO%>" width="w10" />

        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>

