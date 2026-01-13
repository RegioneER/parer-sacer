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

<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="it.eng.parer.crypto.model.ParerRevokedCertificate"%>
<%@page import="it.eng.parer.crypto.model.ParerCRL"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="it" xml:lang="it">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Gestione CRL</title>
    </head>
    <body>
        <h1>Gestione CRL</h1>
        <form title="Gestione CRL" action="GestioneCRL" method="post" enctype="multipart/form-data">   

            <fieldset>
                <legend>Ottieni CRL dal file del firmatario</legend>
                <label for="blobFirmatario">Blob del firmatario:</label>
                <input type="file" name="blobFirmatario" id="blobFirmatario" />
                <input name="crlFromFirmatario" type="submit" value="Ottieni CRL"/>
            </fieldset>

            <fieldset>
                <legend>Ottieni CRL da DN e KeyID</legend>
                <label for="crlDN">DN della CRL:</label>
                <input type="text" name="crlDN" id="crlDN" value="CN=Regione Umbria - CA Cittadini,OU=Servizi di Certificazione,O=Postecom S.p.A.,C=IT"/>
                <label for="crlKeyId">Key ID della CRL:</label>
                <input typr="text" name="crlKeyId" id="crlKeyId" value="13aa9b078b17b8db87b782df6a8ad34a994594ee"/>
                <input name="crlFromDnKeyId" type="submit" value="Ottieni CRL"/>
            </fieldset>

            <fieldset>
                <legend>Aggiungi CRL passando una lista di URL (separati da ;)</legend>
                <label for="listaCRL">Lista CRL:</label>
                <textarea name="listaCRL" id="listaCRL">
                    	http://cns.postecert.poste.it/pi-cns-ts/regionelazio/crl.crl;
                        ldap://certificati.postecert.it:389/CN=Regione%20Lazio%20-%20CA%20Cittadini,OU%3dServizi%20di%20Certificazione,O%3dPoste%20Italiane%20S.p.A.,C%3dIT?certificateRevocationList
                </textarea>
                <input name="addCrlFromUrls" type="submit" value="Aggiungi CRL"/>
            </fieldset>
        </form>

        <c:if test="${ not empty requestScope.message}">
            <h2>Risultato:</h2>
            <%
                ParerCRL parerCRL = (ParerCRL) request.getAttribute("message");

                final StringBuilder sb = new StringBuilder();

                sb.append("Numero CRL: ");
                sb.append(parerCRL.getCrlNum());
                sb.append("<br/>\n");
                sb.append("Key ID: ");
                sb.append(parerCRL.getKeyId());
                sb.append("<br/>\n");
                sb.append("This update: ");
                sb.append(parerCRL.getThisUpdate());
                sb.append("<br/>\n");
                sb.append("Next update: ");
                sb.append(parerCRL.getNextUpdate());
                sb.append("<br/>\n");
                sb.append("Principal: ");
                sb.append(parerCRL.getPrincipalName());
                sb.append("<br/>\n");
                sb.append("SubjectDN: ");
                sb.append(parerCRL.getSubjectDN());
                sb.append("<br/>\n");
                sb.append("Certificati revocati:");
                sb.append("<ul>");
                for (ParerRevokedCertificate cert : parerCRL.getRevokedCertificates()) {
                    sb.append("<li>Seriale e data di revoca :");
                    sb.append(cert.getRevocationDate()).append(" - ").append(cert.getSerialNumber()).append("</li>");
                }
                sb.append("<ul>");

                out.append(sb.toString());

            %>

        </c:if>
        <c:if test="${ not empty requestScope.errore}">
            <h2>Errore:</h2>
            <c:out value="${requestScope.errore}"/>
        </c:if>
        <hr/>
        <p><em>Pagina generata il <%= LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)%></em></p>
    </body>
</html>
