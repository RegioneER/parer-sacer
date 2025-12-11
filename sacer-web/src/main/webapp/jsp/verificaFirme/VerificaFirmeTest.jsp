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

<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="it" xml:lang="it">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Verifica firme</title>
    </head>
    <body>
        <h1>Verifica firme</h1>
        <p> Qui di seguito i servizi di verifica firme supportati al parer</p>
        <h2>Verifica firma EIDAS</h2>
        <a href="${requestScope.EIDAS_LINK}" target="_blank" title="Servizio di verifica eIDAS">Verifica firma "eIDAS"</a>
        <h2>Verifica firma crypto</h2>
        <a href="${requestScope.CRYPTO_LINK}" target="_blank" title="Servizio di verifica Crypto">Verifica firma "Crypto"</a>

    </body>
</html>
