<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
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