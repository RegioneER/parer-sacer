/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.disciplinare;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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
import javax.xml.XMLConstants;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.configuration.Configuration;
import org.apache.fop.configuration.DefaultConfigurationBuilder;
import org.apache.xmlgraphics.util.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ConnectionException;
import it.eng.parer.grantedEntity.AplParamApplicReport;
import it.eng.parer.helper.GenericHelper;
import it.eng.spagoCore.util.JpaUtils;

/**
 * @author Iacolucci_M
 */
@Stateless
@LocalBean
public class DisciplinareTecnicoHelper extends GenericHelper {

    public static final String CHARSET_NAME = StandardCharsets.UTF_8.name();
    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(DisciplinareTecnicoHelper.class);

    @SuppressWarnings("unchecked")
    public AplParamApplicReport getAplParamApplicReportByAppReport(String nmApplic,
            String nmReport) {
        AplParamApplicReport ret = null;
        Query q = entityManager.createNamedQuery("AplParamApplicReport.findByApplicReport");
        q.setParameter("nmApplic", nmApplic);
        q.setParameter("nmReport", nmReport);
        List<AplParamApplicReport> l = q.getResultList();
        if (l != null && !l.isEmpty()) {
            ret = l.get(0);
        }
        return ret;
    }

    /*
     * Ho dovuto mettere requires_new altrimenti mi falliva la scrittura di un entity su un'altro
     * schema con sequence.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Clob eseguiQuery(String query, long idOggetto) {
        Clob disciplinareTecnicoClob = null;
        query = query.replace(":ID_OGGETTO", idOggetto + "");
        Connection conn = null;
        try {
            conn = JpaUtils.provideConnectionFrom(entityManager);
        } catch (SQLException e) {
            throw new ConnectionException(
                    "Impossibile ottenere una connessione: " + e.getMessage());
        }
        try (Statement ps = conn.createStatement(); ResultSet rs = ps.executeQuery(query)) {
            if (rs.next()) {
                disciplinareTecnicoClob = rs.getClob("DISCIPLINARE_TECNICO");
            }
        } catch (Exception ex) {
            log.error(null, ex);
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                log.error("Errore nella chiusura della connessione:", ex);
            }
        }

        log.debug("Fine query!");
        return disciplinareTecnicoClob;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String trasformaXmlInXml(String xml, String xsl) {
        String xmlRet = null;
        try {
            // Nota: al fine di evitare problemi di classloading e "override" del parser (vedi
            // libreria Saxon-HE)
            // viene esplicitato a codice quale impementazione (xalan standard in questo caso)
            // utilizzare
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Source xslt = new StreamSource(new StringReader(xsl));
            Transformer transformer = factory.newTransformer(xslt);
            Source source = new StreamSource(new StringReader(xml));
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);
            transformer.transform(source, result);
            xmlRet = new String(writer.toString().getBytes(CHARSET_NAME), CHARSET_NAME);
        } catch (Exception ex) {
            log.error(null, ex);
        }
        return xmlRet;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String trasformaInXmlIntermedio(Clob fotoXml, String xslPerIntermedio) {
        String ret = null;
        try {
            ret = trasformaXmlInXml(new String(getClobAsByteArray(fotoXml), CHARSET_NAME),
                    xslPerIntermedio);
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
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();) {

            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder.build(
                    this.getClass().getClassLoader().getResourceAsStream("META-INF/fop.xconf"));
            FopFactoryBuilder fopFactoryBuilder = new FopFactoryBuilder(new File(".").toURI())
                    .setConfiguration(cfg);

            FopFactory fopFactory = fopFactoryBuilder.build();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Nota: al fine di evitare problemi di classloading e "override" del parser (vedi
            // libreria Saxon-HE)
            // viene esplicitato a codice quale impementazione (xalan standard in questo caso)
            // utilizzare
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            Transformer transformer = factory.newTransformer(); // identity transformer
            Source src = new StreamSource(new StringReader(foString));
            Result res = new SAXResult(fop.getDefaultHandler());
            transformer.transform(src, res);

            return out.toByteArray();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
        return new byte[] {};
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
        return sb.toString().getBytes(CHARSET_NAME);
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
        return new String(sb.toString().getBytes(CHARSET_NAME), CHARSET_NAME);
    }
}
