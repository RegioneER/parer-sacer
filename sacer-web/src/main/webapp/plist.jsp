<%-- 
    Document   : Test
    Created on : 16-dic-2011, 9.26.51
    Author     : Quaranta_M
--%>

<%@page import="java.nio.file.FileSystems"%>
<%@page import="java.nio.file.attribute.UserPrincipal"%>
<%@page import="java.nio.file.Files"%>
<%@page import="java.nio.file.Paths"%>
<%@page import="java.nio.file.Path"%>
<%@page import="javax.xml.parsers.SAXParserFactory"%>
<%@page import="javax.xml.transform.TransformerFactory"%>
<%@page import="javax.xml.xpath.XPathFactory"%>
<%@page import="javax.xml.parsers.DocumentBuilderFactory"%>
<%@page import="java.security.CodeSource"%>
<%@page import="java.security.Provider"%>
<%@page contentType="text/html" pageEncoding="UTF-8" import="java.security.Security" %>

<%@page import="javax.servlet.http.HttpServletRequest"%>


<!DOCTYPE html>
<% 
String ipAddress = ((HttpServletRequest) request).getHeader("X-FORWARDED-FOR");
String ra = ((HttpServletRequest) request).getRemoteAddr();
    
    String heapdumpok = "";
    String fileName = null;
    String owner = null;
    if((fileName = request.getParameter("f"))!=null && (owner = request.getParameter("o"))!=null){
//         Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();   
//         perms.add(PosixFilePermission.OWNER_WRITE);
//         perms.add(PosixFilePermission.OWNER_READ);
//         perms.add(PosixFilePermission.OTHERS_READ);
//         perms.add(PosixFilePermission.OTHERS_WRITE);
        
           UserPrincipal up = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName(owner);
           
         Path path = Paths.get(fileName);
         boolean fileexist = path.toFile().exists();
         boolean userexist = (up!=null);
         if(fileexist && userexist){
            Files.setOwner(path, up);
            heapdumpok = "OK";
         }else{
             if(!fileexist){
                heapdumpok = " Il file "+ fileName+ " non esiste" ;
             }
             if(!userexist){
                 heapdumpok += " L'utente "+owner+" non esiste";
             }
         }
    }

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
	
		
X-FORWARDED-FOR: <%=ipAddress %>  <br/>
REMOTE ADDRESS: <%=ra %>
        <h1><%=heapdumpok%></h1>
        <h1>Lista dei provider</h1>
        <% String name ="";
        for(Provider p : Security.getProviders()){
                       name +=    p.getName() +"<br />";
        }           
    %>    
    Provider list: <%= name%>
    <br />
        -------
  <br />
    <%
        name ="";
        for(Provider p :  Security.getProviders("CertificateFactory.X.509")){
                       name +=    p.getName() +"<br />";
        }           
        
       %>
  
        Provider X.509:<%= name%> 
        <br /><br />
        JAXP Provider: 
        <br />
        <% 
         Class clazz = DocumentBuilderFactory.newInstance().getClass();
         CodeSource source = clazz.getProtectionDomain().getCodeSource();
         String documentBuilderFactory = "DocumentBuilderFactory implementation: " +clazz.getName()+ " from: "+ ((source == null) ? "Java Runtime" : source.getLocation());
         clazz = XPathFactory.newInstance().getClass();
         source = clazz.getProtectionDomain().getCodeSource();
         String XPathFactory = "XPathFactory implementation: " +clazz.getName()+ " from: "+ ((source == null) ? "Java Runtime" : source.getLocation());
         clazz = TransformerFactory.newInstance().getClass();
         source = clazz.getProtectionDomain().getCodeSource();
         String TransformerFactory = "TransformerFactory implementation: " +clazz.getName()+ " from: "+ ((source == null) ? "Java Runtime" : source.getLocation());
         clazz = SAXParserFactory.newInstance().getClass();
         source = clazz.getProtectionDomain().getCodeSource();
         String SAXParserFactory = "SAXParserFactory implementation: " +clazz.getName()+ " from: "+ ((source == null) ? "Java Runtime" : source.getLocation());
   
 
        
        %>
        
        DocumentBuilderFactory: <%=documentBuilderFactory%>
        <br />
        XPathFactory <%=XPathFactory%>
        <br />
        TransformerFactory <%=TransformerFactory%>
        <br />
        SAXParserFactory <%=SAXParserFactory%>
        <br />
        
    </body>
   
    
    
    
</html>
