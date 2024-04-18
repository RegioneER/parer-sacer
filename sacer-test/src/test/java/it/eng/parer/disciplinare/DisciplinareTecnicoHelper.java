/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.disciplinare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.xmlgraphics.util.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import prova.TestDisciplinareTecnico;

/**
 *
 * @author Iacolucci_M
 */
@Stateless
public class DisciplinareTecnicoHelper {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(DisciplinareTecnicoHelper.class);
/*
    public AplParamApplicReport getAplParamApplicReportByAppReport(String nmApplic, String nmReport) {
        AplParamApplicReport ret=null;
        Query q=entityManager.createNamedQuery("AplParamApplicReport.findByAppReport");
        q.setParameter("nmApplic",nmApplic);
        q.setParameter("nmReport",nmReport);
        List<AplParamApplicReport> l=q.getResultList();
        if (l!=null&&l.size()>0) {
            ret=l.get(0);
        }
        return ret;
    }
*/    
    public Clob eseguiQuery(String query, long idOggetto) {
        Clob disciplinareTecnicoClob = null;
//        System.out.println("Creating statement...");
        query = query.replaceAll(":ID_OGGETTO", idOggetto + "");
//        System.out.println("SQL PROCESSATO:" + query);
        try (   Connection conn = entityManager.unwrap(Connection.class);
                Statement ps = conn.createStatement();
                ResultSet rs = ps.executeQuery(query);) {
            if (rs.next()) {
                disciplinareTecnicoClob = rs.getClob("DISCIPLINARE_TECNICO");
            }
        } catch (Exception ex) {
            log.error(null, ex);
        }
        log.debug("Fine query!");
        return disciplinareTecnicoClob;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String trasformaXmlInXml(String xml, String xsl) {
        String xmlRet = null;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new StringReader(xsl));
            Transformer transformer = factory.newTransformer(xslt);
            Source source = new StreamSource(new StringReader(xml));
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            transformer.transform(source, result);
            xmlRet = new String(writer.toString().getBytes("UTF-8"), "UTF-8");
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return xmlRet;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public byte[] trasformaInPDF(String foString) {
        ByteArrayOutputStream out = null;
        try {
            
//            this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf");
//            FopFactory fopFactory = FopFactory.newInstance(new File(RISORSE + "/fop.xconf"));


/*            FopFactory fopFactory = FopFactory.newInstance(this.getClass().getClassLoader().getResource("META-INF/fop.xconf").toURI(),
                                                           this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf")); */
            
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
//            Configuration cfg = cfgBuilder.buildFromFile(new File(this.getClass().getClassLoader().getResource("META-INF/fop.xconf").getFile()));
            Configuration cfg = cfgBuilder.build(this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf"));
            FopFactory fopFactory = FopFactory.newInstance();
            fopFactory.setUserConfig(cfg);



            out = new ByteArrayOutputStream();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer
            Source src = new StreamSource(new StringReader(foString));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

        } catch (Exception ex) {
            java.util.logging.LoggerFactory.getLogger(TestDisciplinareTecnico.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                java.util.logging.LoggerFactory.getLogger(TestDisciplinareTecnico.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return out != null ? out.toByteArray() : null;
    }

    
/*    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public byte[] trasformaInPDF(String foString) {
        ByteArrayOutputStream out = null;
        try {
            this.getClass().getClassLoader().getResource("META-INF/fop.xconf");
            
//            this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf");
//            FopFactory fopFactory = FopFactory.newInstance(new File(RISORSE + "/fop.xconf"));


            FopFactory fopFactory = FopFactory.newInstance(this.getClass().getClassLoader().getResource("META-INF/fop.xconf").toURI(),
                                                           this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf"));
            out = new ByteArrayOutputStream();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(); // identity transformer
            Source src = new StreamSource(new StringReader(foString));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

        } catch (Exception ex) {
            java.util.logging.LoggerFactory.getLogger(TestDisciplinareTecnico.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                java.util.logging.LoggerFactory.getLogger(TestDisciplinareTecnico.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return out != null ? out.toByteArray() : null;
    }
*/    
    
    
    
    
    
    
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public byte[] getClobAsByteArray(Clob clob) throws SQLException, IOException {
        StringBuilder sb = new StringBuilder((int) clob.length());
        Reader r = clob.getCharacterStream();
        char[] cbuf = new char[2048];
        int n;
        while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            sb.append(cbuf, 0, n);
        }
        return sb.toString().getBytes("UTF-8");
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getClobAsString(Clob clob) throws SQLException, IOException {
        StringBuilder sb = new StringBuilder((int) clob.length());
        Reader r = clob.getCharacterStream();
        char[] cbuf = new char[2048];
        int n;
        while ((n = r.read(cbuf, 0, cbuf.length)) != -1) {
            sb.append(cbuf, 0, n);
        }
        return new String(sb.toString().getBytes("UTF-8"), "UTF-8");
    }

}
