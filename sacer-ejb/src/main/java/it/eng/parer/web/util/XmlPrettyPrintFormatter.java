/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.dom.ls.LSSerializerFilter;
import org.w3c.dom.traversal.NodeFilter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Gilioli_P
 */
public class XmlPrettyPrintFormatter {

    Logger log = LoggerFactory.getLogger(XmlPrettyPrintFormatter.class);

    public XmlPrettyPrintFormatter() {
    }

    static LSSerializerFilter skipWhitespaceFilter = new LSSerializerFilter() {
        public short acceptNode(Node n) {
            if (n.getNodeType() == Node.TEXT_NODE) {
                return (n.getNodeValue().trim().length() == 0) ? FILTER_REJECT : FILTER_ACCEPT;
            }
            return FILTER_ACCEPT;
        }

        public int getWhatToShow() {
            return NodeFilter.SHOW_ALL;
        }

        public short startElement(Element elem) {
            return FILTER_ACCEPT;
        }
    };

    public String prettyPrintWithDOM3LS(String unformattedXml) {
        // Pretty-prints a DOM document to XML using DOM Load and Save's LSSerializer.
        // Note that the "format-pretty-print" DOM configuration parameter can only be set in JDK 1.6+.
        // unformattedXml = unformattedXml.replaceAll(" ", "");
        Document document = parseXmlFile(unformattedXml);
        if (document != null) {
            DOMImplementation domImplementation = document.getImplementation();
            if (domImplementation.hasFeature("LS", "3.0") && domImplementation.hasFeature("Core", "2.0")) {
                DOMImplementationLS domImplementationLS = (DOMImplementationLS) domImplementation.getFeature("LS",
                        "3.0");
                LSSerializer lsSerializer = domImplementationLS.createLSSerializer();
                lsSerializer.setFilter(skipWhitespaceFilter);
                DOMConfiguration domConfiguration = lsSerializer.getDomConfig();
                if (domConfiguration.canSetParameter("format-pretty-print", Boolean.TRUE)) {
                    lsSerializer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
                    LSOutput lsOutput = domImplementationLS.createLSOutput();
                    // lsOutput.setEncoding("UTF-8");
                    StringWriter stringWriter = new StringWriter();
                    lsOutput.setCharacterStream(stringWriter);
                    lsSerializer.write(document, lsOutput);
                    return stringWriter.toString();
                } else {
                    throw new RuntimeException("DOMConfiguration 'format-pretty-print' parameter isn't settable.");
                }
            } else {
                throw new RuntimeException("DOM 3.0 LS and/or DOM 2.0 Core not supported.");
            }
        } else {
            return unformattedXml;
        }
    }

    private Document parseXmlFile(String in) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            String FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbf.setFeature(FEATURE, false);

            // // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            // FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            // dbf.setFeature(FEATURE, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);
        } catch (ParserConfigurationException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
