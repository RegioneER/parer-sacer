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
import javax.xml.XMLConstants;

/**
 *
 * @author Bonora_L
 */
public class XmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(XmlUtils.class.getName());

    public static void validateXml(String xsd, String xml) throws SAXException, IOException {
	validateXml(new StreamSource(new StringReader(xsd)),
		new StreamSource(new StringReader(xml)));
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

    public static void validateXml(InputStream xsd, InputStream xml)
	    throws SAXException, IOException {
	validateXml(new StreamSource(xsd), new StreamSource(xml));
    }

    public static void validateXml(InputStream xsd, byte[] xml) throws SAXException, IOException {
	validateXml(xsd, new ByteArrayInputStream(xml));
    }

    public static void validateXml(String xsd, byte[] xml) throws SAXException, IOException {
	validateXml(new StreamSource(new StringReader(xsd)),
		new StreamSource(new ByteArrayInputStream(xml)));
    }

    public static void validateXml(Source xsdSource, Source xmlSource)
	    throws SAXException, IOException {
	Schema xsdSchema;
	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
	xsdSchema = schemaFactory.newSchema(xsdSource);
	Validator validator = xsdSchema.newValidator();
	validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
	validator.validate(xmlSource);
    }

    public static Schema getSchemaValidation(InputStream xsd) throws SAXException {
	return getSchemaValidation(new StreamSource(xsd));
    }

    public static Schema getSchemaValidation(Source xsdSource) throws SAXException {
	Schema xsdSchema;
	SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
	xsdSchema = schemaFactory.newSchema(xsdSource);
	return xsdSchema;
    }

    public static Document getXmlDocument(String xml) throws SAXException, IOException {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try {
	    // XXE: This is the PRIMARY defense. If DTDs (doctypes) are disallowed,
	    // almost all XML entity attacks are prevented
	    final String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
	    dbf.setFeature(FEATURE, true);
	    dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

	    dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
	    // ... and these as well, per Timothy Morgan's 2014 paper:
	    // "XML Schema, DTD, and Entity Attacks" (see reference below)
	    dbf.setXIncludeAware(false);
	    dbf.setExpandEntityReferences(false);
	    // As stated in the documentation, "Feature for Secure Processing (FSP)" is the central
	    // mechanism that will
	    // help you safeguard XML processing. It instructs XML processors, such as parsers,
	    // validators,
	    // and transformers, to try and process XML securely, and the FSP can be used as an
	    // alternative to
	    // dbf.setExpandEntityReferences(false); to allow some safe level of Entity Expansion
	    // Exists from JDK6.
	    dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
	    // ... and, per Timothy Morgan:
	    // "If for some reason support for inline DOCTYPEs are a requirement, then
	    // ensure the entity settings are disabled (as shown above) and beware that SSRF
	    // attacks
	    // (http://cwe.mitre.org/data/definitions/918.html) and denial
	    // of service attacks (such as billion laughs or decompression bombs via "jar:")
	    // are a risk."
	    DocumentBuilder builder = dbf.newDocumentBuilder();
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
    public static Object compileXpathExpression(Document xmlDocument, String expression,
	    QName objectType) throws XPathExpressionException {
	XPath xPath = XPathFactory.newInstance().newXPath();
	return xPath.compile(expression).evaluate(xmlDocument, objectType);
    }

    public static <T> T unmarshallResponse(Unmarshaller um, InputStream response,
	    Class<T> classType) throws JAXBException {
	JAXBElement<T> jaxbObject = um.unmarshal(new StreamSource(response), classType);
	T obj = jaxbObject.getValue();
	return obj;
    }
}
