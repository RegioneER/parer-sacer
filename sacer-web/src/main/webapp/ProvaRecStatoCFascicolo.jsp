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

<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Prova Recupero Stato Conservazione Fascicolo</title>
        <style type="text/css">
            * {
                padding: 0px;
                margin: 0px;
            }

            html {
                background: #f9f9f9;
            }

            body {
                font-family: Verdana, Geneva, sans-serif;
                font-size: 11px;
                background: #f9f9f9;
                margin: 10px;
            }

            fieldset {
                margin: 20px 0px 20px 0px;
                padding: 20px;
                border: 1px solid #DDD;
                background: white;
            }

            legend {
                font-weight: bold;
                font-size: 13px;
                color: #333;
                padding: 5px 10px;
                background: #F5F5F5;
                border: 1px solid #DDD;
            }

            label {
                display: block;
                margin-bottom: 10px;
                color: #555555;
            }

            label span {
                display: block;
                float: left;
                padding-right: 6px;
                width: 180px;
                text-align: right;
                font-weight: bold;
            }

            .input-text, textarea, select {
                border: 1px solid #CCCCCC;
                color: #666666;
                padding: 5px;
                font-size: 11px;
                font-family: Verdana, Geneva, sans-serif;
            }

            textarea {
                width: 500px;
                height: 300px;
            }

            .button {
                background: #5B74A8;
                padding: 8px 12px;
                border: 1px solid #29447E;
                color: #FFFFFF;
                font-size: 11px;
                margin: 20px 0px 0px 188px;
                cursor: pointer;
                font-family: Verdana, Geneva, sans-serif;
            }

            .button:hover {
                background: #29447E;
            }

            h1 {
                font-size: 18px;
                margin-bottom: 20px;
                color: #333;
            }

            .info {
                background: #E3F2FD;
                border: 1px solid #2196F3;
                padding: 10px;
                margin: 10px 0px;
                color: #1565C0;
            }

            .error {
                background: #FFEBEE;
                border: 1px solid #F44336;
                padding: 10px;
                margin: 10px 0px;
                color: #C62828;
            }

            .xml-output {
                background: #f5f5f5;
                border: 1px solid #ddd;
                padding: 15px;
                margin: 20px 0px;
                overflow-x: auto;
            }

            .xml-output pre {
                margin: 0;
                white-space: pre-wrap;
                word-wrap: break-word;
            }

            a {
                color: #5B74A8;
                text-decoration: none;
            }

            a:hover {
                text-decoration: underline;
            }
        </style>
        <script type="text/javascript">
            function generaXMLEsempio() {
                var ambiente = document.getElementById('ambiente').value || 'AMBIENTE_ESEMPIO';
                var ente = document.getElementById('ente').value || 'ENTE_ESEMPIO';
                var struttura = document.getElementById('struttura').value || 'STRUTTURA_ESEMPIO';
                var anno = document.getElementById('anno').value || '2024';
                var numero = document.getElementById('numero').value || '001';
                var versione = document.getElementById('versione').value || '1.0';
                var loginname = document.getElementById('LOGINNAME').value;

                var xml = '<?xml version="1.0" encoding="UTF-8"?>\n';
                xml += '<RecuperoFascicolo>\n';
                xml += '    <Versione>' + versione + '</Versione>\n';
                xml += '    <Versatore>\n';
                xml += '        <Ambiente>' + ambiente + '</Ambiente>\n';
                xml += '        <Ente>' + ente + '</Ente>\n';
                xml += '        <Struttura>' + struttura + '</Struttura>\n';
                xml += '        <UserID>' + loginname + '</UserID>\n';
                xml += '    </Versatore>\n';
                xml += '    <Chiave>\n';
                    xml += '    <Numero>' + numero + '</Numero>\n';
                xml += '        <Anno>' + anno + '</Anno>\n';
                xml += '    </Chiave>\n';
                xml += '</RecuperoFascicolo>';

                document.getElementById('xmlRichiestaVisibile').value = xml;
            }

            function validaForm() {
                var loginname = document.getElementById('LOGINNAME').value;
                var password = document.getElementById('PASSWORD').value;
                var xmlRichiesta = document.getElementById('xmlRichiestaVisibile').value;

                if (!loginname || !password) {
                    alert('Inserire le credenziali (Username e Password)');
                    return false;
                }

                if (!xmlRichiesta.trim()) {
                    alert('Inserire l\'XML di richiesta');
                    return false;
                }

                // Copia il valore dalla textarea visibile a quella nascosta (che verrà inviata)
                document.getElementById('XMLSIP').value = xmlRichiesta;

                return true;
            }
        </script>
    </head>
    <body>
        <h1>Prova Recupero Stato Conservazione Fascicolo</h1>

        <div class="info">
            <strong>Endpoint:</strong> <c:url value="/RecStatoConservFascicoloSync" /><br/>
            <strong>Metodo:</strong> POST multipart/form-data<br/>
            <strong>Descrizione:</strong> Servizio per il recupero dello stato di conservazione di un fascicolo
        </div>

        <form name="uploadForm" method="POST" action='<c:url value="/RecStatoConservFascicoloSync" />' 
              enctype="multipart/form-data" onsubmit="return validaForm();">

            <fieldset>
                <legend>Credenziali di accesso</legend>
                <label>
                    <span>Versione:</span>
                    <input type="text" name="VERSIONE" id="VERSIONE" class="input-text" size="40" 
                           value="1.0" placeholder="es. 1.0" />
                </label>

                <label>
                    <span>Username:</span>
                    <input type="text" name="LOGINNAME" id="LOGINNAME" class="input-text" size="40" 
                           placeholder="Inserire username" />
                </label>

                <label>
                    <span>Password:</span>
                    <input type="password" name="PASSWORD" id="PASSWORD" class="input-text" size="40" 
                           placeholder="Inserire password" />
                </label>
                
                <!-- Textarea nascosta che verrà effettivamente inviata, posizionata subito dopo PASSWORD -->
                <textarea name="XMLSIP" id="XMLSIP" style="display:none;"></textarea>

            </fieldset>

            <fieldset>
                <legend>Dati del Fascicolo</legend>

                <label>
                    <span>Ambiente:</span>
                    <input type="text" name="ambiente" id="ambiente" class="input-text" size="40" 
                           placeholder="es. AMBIENTE_PROVA" />
                </label>

                <label>
                    <span>Ente:</span>
                    <input type="text" name="ente" id="ente" class="input-text" size="40" 
                           placeholder="es. ENTE_PROVA" />
                </label>

                <label>
                    <span>Struttura:</span>
                    <input type="text" name="struttura" id="struttura" class="input-text" size="40" 
                           placeholder="es. STRUTTURA_PROVA" />
                </label>

                <label>
                    <span>Anno:</span>
                    <input type="text" name="anno" id="anno" class="input-text" size="40" 
                           placeholder="es. 2024" />
                </label>

                <label>
                    <span>Numero:</span>
                    <input type="text" name="numero" id="numero" class="input-text" size="40" 
                           placeholder="es. 001" />
                </label>

                <label>
                    <span>Versione XML:</span>
                    <input type="text" name="versione" id="versione" class="input-text" size="40" 
                           value="1.0" placeholder="es. 1.0" />
                </label>

                <input type="button" value="Genera XML di esempio" onclick="generaXMLEsempio();" 
                       class="button" style="margin-left: 188px;" />
            </fieldset>

            <fieldset>
                <legend>XML di Richiesta</legend>

                <label>
                    <span style="vertical-align: top;">XML Richiesta:</span>
                    <!-- Textarea visibile solo per l'utente, NON verrà inviata -->
                    <textarea id="xmlRichiestaVisibile" 
                              placeholder="Inserire l'XML di richiesta o generarlo con il pulsante sopra"></textarea>
                </label>

                <div class="info" style="margin-left: 188px; max-width: 500px;">
                    <strong>Nota:</strong> L'XML deve essere conforme allo schema RecuperoFascicolo.xsd.<br/>
                    Il servizio restituirà un XML con lo stato di conservazione del fascicolo richiesto.
                </div>
            </fieldset>

            <input type="submit" value="Invia Richiesta" class="button" />
            <input type="button" value="Torna al Lab" onclick="location.href='lab.jsp';" 
                   class="button" style="background: #999; border-color: #666;" />
        </form>

        <div style="margin-top: 30px; padding: 10px; background: #f5f5f5; border: 1px solid #ddd;">
            <h3 style="margin-bottom: 10px;">Esempio di risposta XML:</h3>
            <pre style="margin: 0; white-space: pre-wrap;">
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;StatoConservazioneFasc xmlns="http://www.uni.com/U3018/2013/conservazione/"&gt;
    &lt;DataRichiestaStato&gt;2024-12-31T10:30:00&lt;/DataRichiestaStato&gt;
    &lt;Versione&gt;1.0&lt;/Versione&gt;
    &lt;VersioneXMLChiamata&gt;1.0&lt;/VersioneXMLChiamata&gt;
    &lt;EsitoGenerale&gt;
        &lt;CodiceEsito&gt;POSITIVO&lt;/CodiceEsito&gt;
        &lt;CodiceErrore&gt;&lt;/CodiceErrore&gt;
        &lt;MessaggioErrore&gt;&lt;/MessaggioErrore&gt;
    &lt;/EsitoGenerale&gt;
    &lt;EsitoChiamataWS&gt;
        &lt;CredenzialiOperatore&gt;POSITIVO&lt;/CredenzialiOperatore&gt;
        &lt;VersioneWSCorretta&gt;POSITIVO&lt;/VersioneWSCorretta&gt;
    &lt;/EsitoChiamataWS&gt;
    &lt;StatoFascicolo&gt;
        &lt;StatoConservazione&gt;PRESA_IN_CARICO&lt;/StatoConservazione&gt;
        &lt;DataVersamento&gt;2024-01-15T14:20:00&lt;/DataVersamento&gt;
        &lt;!-- Altri dati del fascicolo --&gt;
    &lt;/StatoFascicolo&gt;
&lt;/StatoConservazioneFasc&gt;
            </pre>
        </div>

    </body>
</html>