package it.eng.parer.xml.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Bonora_L
 */
public class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class.getName());

    public static void validateXml(String xsd, String xml) throws SAXException, IOException {
        validateXml(new StreamSource(new StringReader(xsd)), new StreamSource(new StringReader(xml)));
    }

    public static void validateXml(InputStream xsd, String xml) throws SAXException, IOException {
        validateXml(new StreamSource(xsd), new StreamSource(new StringReader(xml)));
    }

    public static void validateXml(File xsd, File xml) throws SAXException, IOException {
        validateXml(new StreamSource(xsd), new StreamSource(xml));
    }

    public static void validateXml(File xsd, String xml) throws SAXException, IOException {
        validateXml(new StreamSource(xsd), new StreamSource(new StringReader(xml)));
    }

    public static void validateXml(InputStream xsd, InputStream xml) throws SAXException, IOException {
        validateXml(new StreamSource(xsd), new StreamSource(xml));
    }

    public static void validateXml(InputStream xsd, byte[] xml) throws SAXException, IOException {
        validateXml(xsd, new ByteArrayInputStream(xml));
    }

    public static void validateXml(String xsd, byte[] xml) throws SAXException, IOException {
        validateXml(new StreamSource(new StringReader(xsd)), new StreamSource(new ByteArrayInputStream(xml)));
    }

    public static void validateXml(Source xsdSource, Source xmlSource) throws SAXException, IOException {
        Schema xsdSchema;
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        xsdSchema = schemaFactory.newSchema(xsdSource);
        Validator validator = xsdSchema.newValidator();
        validator.validate(xmlSource);
    }

    public static Schema getSchemaValidation(InputStream xsd) throws SAXException {
        return getSchemaValidation(new StreamSource(xsd));
    }

    public static Schema getSchemaValidation(Source xsdSource) throws SAXException {
        Schema xsdSchema;
        SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        xsdSchema = schemaFactory.newSchema(xsdSource);
        return xsdSchema;
    }

    public static Document getXmlDocument(String xml) throws SAXException, IOException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document xmlDocument = builder.parse(new ByteArrayInputStream(xml.getBytes()));
            return xmlDocument;
        } catch (ParserConfigurationException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /*
     * Esegue la valutazione di un'espressione XPath 1.0
     */
    public static Object compileXpathExpression(Document xmlDocument, String expression, QName objectType)
            throws XPathExpressionException {
        XPath xPath = XPathFactory.newInstance().newXPath();
        return xPath.compile(expression).evaluate(xmlDocument, objectType);
    }

    public static <T> T unmarshallResponse(Unmarshaller um, InputStream response, Class<T> classType)
            throws JAXBException {
        JAXBElement<T> jaxbObject = um.unmarshal(new StreamSource(response), classType);
        T obj = jaxbObject.getValue();
        return obj;
    }
}
