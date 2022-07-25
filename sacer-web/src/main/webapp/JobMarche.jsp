<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Marche temporali e mimetype</title>
    </head>
    <body>
        <h1>Test marcatura temporale</h1>
        <form title="Verifica file firmati" action="TestMarcatura" method="post"  enctype="multipart/form-data" >   

            File da marcare <input type="file" id="contDaMarcare"  name="contDaMarcare"/>    
            <br/><br/>
            <input name="startMarcatura" type="submit" value="Marca"/> 
            <input name="startMarcaturaTSD" type="submit" value="Marca TSD"/> 
            <br/><br/> 
            <!--            Lancia la verifica dei file nella cartella C:/tmp   
                        <br/>
                        <label>
                            <span>Verifica i formati</span> <input type="checkbox" id="doFormatCheck" class="input-text" checked name="doFormatCheck"/>
                        </label>
                        <br/>
                        <label>
                            <span>Verifica le firme</span> <input type="checkbox" id="doFirmeCheck" class="input-text" checked name="doFirmeCheck"/>
                        </label>
                        <br/>
                        <label>
                            <span>Verifica le firme e le marche come sottocomponenti</span> <input type="checkbox" id="doSottoCompVer" class="input-text" checked name="doSottoCompVer"/>
                        </label>
                        <br/>
                        <input name="startTestTmp" type="submit" value="Avvia test"/>
            -->


        </form>

        <c:if test="${ not empty requestScope.tstTime}">
            TimeStamp apposto da TSA : <c:out value="${requestScope.tstTime}" />

        </c:if>
        <h1>Test mimetype formato</h1>
        <form title="Test formato" action="TestMarcatura" method="post"  enctype="multipart/form-data" >   

            File da testare <input type="file" id="contFormato"  name="contFormato"/>    
            <br/><br/>
            <input name="test" type="submit" value="Verifica"/>                   
            <br/><br/> 
        </form>

        <c:if test="${ not empty requestScope.mimeType}">
            MimeType calcolato : <c:out value="${requestScope.mimeType}" />

        </c:if>
    </body>
</html>