<%--
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
 --%>

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
