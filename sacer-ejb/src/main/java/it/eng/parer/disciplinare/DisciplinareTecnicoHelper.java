package it.eng.parer.disciplinare;

import it.eng.parer.grantedEntity.AplParamApplicReport;
import it.eng.parer.helper.GenericHelper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

/**
 *
 * @author Iacolucci_M
 */
@Stateless
@LocalBean
public class DisciplinareTecnicoHelper extends GenericHelper {

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(DisciplinareTecnicoHelper.class);

    public AplParamApplicReport getAplParamApplicReportByAppReport(String nmApplic, String nmReport) {
        AplParamApplicReport ret = null;
        Query q = entityManager.createNamedQuery("AplParamApplicReport.findByApplicReport");
        q.setParameter("nmApplic", nmApplic);
        q.setParameter("nmReport", nmReport);
        List<AplParamApplicReport> l = q.getResultList();
        if (l != null && l.size() > 0) {
            ret = l.get(0);
        }
        return ret;
    }

    /*
     * Ho dovuto mettere requires_new altrimenti mi falliva la scrittura di un entity su un'altro schema con sequence.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Clob eseguiQuery(String query, long idOggetto) {
        Clob disciplinareTecnicoClob = null;
        query = query.replaceAll(":ID_OGGETTO", idOggetto + "");
        try (Connection conn = entityManager.unwrap(Connection.class); Statement ps = conn.createStatement();
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
            // Nota: al fine di evitare problemi di classloading e "override" del parser (vedi libreria Saxon-HE)
            // viene esplicitato a codice quale impementazione (xalan standard in questo caso) utilizzare
            TransformerFactory factory = TransformerFactory
                    .newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
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
    public String trasformaInXmlIntermedio(Clob fotoXml, String xslPerIntermedio) {
        String ret = null;
        try {
            ret = trasformaXmlInXml(new String(getClobAsByteArray(fotoXml), "UTF-8"), xslPerIntermedio);
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return ret;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String trasformaInFo(String fotoXml, String xslPerIntermedio) {
        String ret = null;
        try {
            ret = trasformaXmlInXml(fotoXml, xslPerIntermedio);
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return ret;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public byte[] trasformaInPDF(String foString) {
        ByteArrayOutputStream out = null;
        try {
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder
                    .build(this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf"));
            FopFactory fopFactory = FopFactory.newInstance();
            fopFactory.setUserConfig(cfg);

            out = new ByteArrayOutputStream();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            // Nota: al fine di evitare problemi di classloading e "override" del parser (vedi libreria Saxon-HE)
            // viene esplicitato a codice quale impementazione (xalan standard in questo caso) utilizzare
            TransformerFactory factory = TransformerFactory
                    .newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
            Transformer transformer = factory.newTransformer(); // identity transformer
            Source src = new StreamSource(new StringReader(foString));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
        }
        return out != null ? out.toByteArray() : null;
    }

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
