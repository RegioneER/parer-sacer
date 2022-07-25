<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Pulizia cache CRL</title>
    </head>
    <body>
        <c:if test="${not empty requestScope.cachepulita}" >
            ${requestScope.cachepulita}
        </c:if>

        <h1>Pulizia cache CRL</h1>
        <form title="Pulizia cache CRL" action="PuliziaCacheCRL" method="post" >
            Pulisci la cache delle CRL
            <input name="clearCRLCache" type="submit" value="Pulisci"/>
        </form>
    </body>
</html>