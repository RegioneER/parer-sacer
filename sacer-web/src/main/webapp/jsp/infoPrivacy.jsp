<%@ page import="it.eng.parer.slite.gen.form.HomeForm" pageEncoding="UTF-8"%>
<%@ include file="../include.jsp"%>

<sl:html>
    <sl:head title="Info privacy" >
        <script type='text/javascript' >
            $(document).ready(function () {
                $.getJSON('#', {
                    operation: 'mostraHelpPagina',
                    codiceMenuHelp: '',
                    tipoHelpInfoPrivacy: 'true'
                }).done(function (data) {
                    var obj = jQuery.parseJSON(data.map[0].risposta);
                    if (obj.cdEsito === 'OK') {
                        $('#infoPrivacyDiv').html(obj.blHelp).first().focus();
                    } else {
                        $('#infoPrivacyDiv').html(obj.dlErr).first().focus();
                    }
                }).fail(function () {
                    $('#infoPrivacyDiv').html('Errore di comunicazione con il server.');
                });
            });
        </script>
    </sl:head>

    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia struttura" />
        <sl:menu />
        <sl:content>
            <slf:messageBox />
            <%--<sl:contentTitle title="Informativa sulla privacy" />--%>
            <div id="infoPrivacyDiv" style="height: 1500px; width: 1500px;"></div>
        </sl:content>
        <sl:footer />
    </sl:body>

</sl:html>
