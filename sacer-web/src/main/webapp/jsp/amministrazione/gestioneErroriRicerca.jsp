<%@ page import="it.eng.parer.slite.gen.form.AmministrazioneForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
    <sl:head title="Gestione errori SACER" >
        <script type="text/javascript">
            (function ($) {
                function renderHtmlColumn(headerLabel) {
                    var normalizedHeaderLabel = $.trim(headerLabel).toLowerCase();
                    var tables = $("table").filter(function () {
                        return $(this).find("th").filter(function () {
                            return $.trim($(this).text()).toLowerCase() === normalizedHeaderLabel;
                        }).length > 0;
                    });

                    tables.each(function () {
                        var table = $(this);
                        var headerIndex = -1;

                        table.find("th").each(function (index) {
                            if ($.trim($(this).text()).toLowerCase() === normalizedHeaderLabel) {
                                headerIndex = index;
                                return false;
                            }
                        });

                        if (headerIndex < 0) {
                            return;
                        }

                        table.find("tbody tr").each(function () {
                            var cell = $(this).children().eq(headerIndex);
                            if (cell.length === 0) {
                                return;
                            }

                            var rawHtml = $.trim(cell.text());
                            if (!rawHtml || rawHtml.indexOf("<") === -1) {
                                return;
                            }

                            cell.html(rawHtml);
                        });
                    });
                }

                function renderRichTextColumns() {
                    renderHtmlColumn("Casistica");
                    renderHtmlColumn("Soluzione suggerita");
                }

                $(document).ready(renderRichTextColumns);
            })(jQuery);
        </script>
    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <sl:contentTitle title="Gestione errori SACER"/>
            <sl:newLine skipLine="true"/>

            <slf:fieldSet >
                <slf:lblField name="<%=AmministrazioneForm.FiltriRicercaGestioneErrori.CD_CLASSE_ERR_SACER_RIC%>" colSpan="2"/>
            </slf:fieldSet>

            <sl:newLine skipLine="true"/>

            <sl:pulsantiera>
                <slf:lblField  name="<%=AmministrazioneForm.FiltriRicercaGestioneErrori.RICERCA_GESTIONE_ERRORI_BUTTON%>"  width="w50" />
            </sl:pulsantiera>

            <sl:newLine skipLine="true"/>

            <slf:listNavBar name="<%= AmministrazioneForm.GestioneErroriList.NAME%>" pageSizeRelated="true"/>
            <slf:list name="<%= AmministrazioneForm.GestioneErroriList.NAME%>" />
            <slf:listNavBar  name="<%= AmministrazioneForm.GestioneErroriList.NAME%>" />
        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>