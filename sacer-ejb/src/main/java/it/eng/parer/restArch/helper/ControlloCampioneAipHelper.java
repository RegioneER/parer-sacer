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

package it.eng.parer.restArch.helper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Helper che esegue il controllo a campione del pacchetto AIP prodotto dal job di Restituzione
 * Archivio: verifica che l'indice AIP sia presente nello zip, che sia validabile con lo XSD incluso
 * nel pacchetto e che tutti i file richiamati dall'indice (tag {@code <sincro:Path>} e attributo
 * {@code sincro:xmlSchema} dei tag {@code <sincro:MoreInfo>}) siano effettivamente presenti nello
 * zip.
 *
 * @author Copilot
 */
@Stateless
@LocalBean
public class ControlloCampioneAipHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControlloCampioneAipHelper.class);

    /** Nome del file xml dell'indice AIP (Unisincro) atteso all'interno dello zip. */
    public static final String NOME_FILE_INDICE_AIP = "PIndexUD.xml";

    /** Nome del file xsd dell'indice AIP (Unisincro) atteso all'interno dello zip. */
    public static final String NOME_FILE_XSD_INDICE_AIP = "Unisincro_PIndex.xsd";

    /** Codici di errore prodotti dal controllo a campione. */
    public enum CdErroreControlloAip {
        INDICE_AIP_MANCANTE, XSD_INDICE_AIP_MANCANTE, INDICE_AIP_NON_VALIDABILE,
        FILE_REFERENZIATI_MANCANTI, ERRORE_GENERICO_CONTROLLO
    }

    /**
     * Esito del controllo a campione di un pacchetto AIP.
     */
    public static class EsitoControlloAip {
        private final boolean esito;
        private final CdErroreControlloAip cdErrore;
        private final String dsErrore;

        private EsitoControlloAip(boolean esito, CdErroreControlloAip cdErrore, String dsErrore) {
            this.esito = esito;
            this.cdErrore = cdErrore;
            this.dsErrore = dsErrore;
        }

        static EsitoControlloAip ok() {
            return new EsitoControlloAip(true, null, null);
        }

        static EsitoControlloAip ko(CdErroreControlloAip cdErrore, String dsErrore) {
            return new EsitoControlloAip(false, cdErrore, dsErrore);
        }

        public boolean isEsito() {
            return esito;
        }

        public CdErroreControlloAip getCdErrore() {
            return cdErrore;
        }

        public String getDsErrore() {
            return dsErrore;
        }
    }

    /**
     * Esegue il controllo a campione sul pacchetto AIP indicato.
     *
     * @param zipAip file zip del pacchetto AIP appena estratto/copiato
     *
     * @return esito del controllo, con eventuale codice/descrizione errore
     */
    public EsitoControlloAip controlla(File zipAip) {
        try (ZipFile zipFile = new ZipFile(zipAip)) {
            // 1) l'indice AIP deve esistere nello zip
            ZipEntry indiceAipEntry = trovaEntry(zipFile, NOME_FILE_INDICE_AIP);
            if (indiceAipEntry == null) {
                return EsitoControlloAip.ko(CdErroreControlloAip.INDICE_AIP_MANCANTE,
                        "Il file " + NOME_FILE_INDICE_AIP
                                + " non e' presente all'interno del pacchetto AIP "
                                + zipAip.getName());
            }

            // 2) l'xsd dell'indice AIP deve esistere nello zip
            ZipEntry xsdEntry = trovaEntry(zipFile, NOME_FILE_XSD_INDICE_AIP);
            if (xsdEntry == null) {
                return EsitoControlloAip.ko(CdErroreControlloAip.XSD_INDICE_AIP_MANCANTE,
                        "Il file " + NOME_FILE_XSD_INDICE_AIP
                                + " non e' presente all'interno del pacchetto AIP "
                                + zipAip.getName());
            }

            byte[] indiceAipBytes = leggiEntry(zipFile, indiceAipEntry);
            byte[] xsdBytes = leggiEntry(zipFile, xsdEntry);

            // 3) l'indice AIP deve essere validabile con lo xsd trovato nello zip
            try {
                validaXsd(indiceAipBytes, xsdBytes);
            } catch (SAXException e) {
                return EsitoControlloAip.ko(CdErroreControlloAip.INDICE_AIP_NON_VALIDABILE,
                        "Il file " + NOME_FILE_INDICE_AIP
                                + " non e' valido rispetto allo XSD " + NOME_FILE_XSD_INDICE_AIP
                                + ": " + e.getMessage());
            }

            // 4) tutti i file referenziati dall'indice AIP (tag Path e attributo xmlSchema dei tag
            // MoreInfo) devono essere presenti nello zip
            List<String> pathReferenziati = estraiPathReferenziati(indiceAipBytes);
            List<String> pathMancanti = new ArrayList<>();
            for (String path : pathReferenziati) {
                if (StringUtils.isBlank(path)) {
                    continue;
                }
                if (trovaEntry(zipFile, normalizza(path)) == null) {
                    pathMancanti.add(path);
                }
            }

            if (!pathMancanti.isEmpty()) {
                return EsitoControlloAip.ko(CdErroreControlloAip.FILE_REFERENZIATI_MANCANTI,
                        "I seguenti file referenziati dall'indice AIP non sono presenti nello zip: "
                                + String.join(", ", pathMancanti));
            }

            return EsitoControlloAip.ok();
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Errore durante il controllo a campione del pacchetto AIP {}",
                    zipAip.getAbsolutePath(), e);
            return EsitoControlloAip.ko(CdErroreControlloAip.ERRORE_GENERICO_CONTROLLO,
                    "Errore durante il controllo a campione del pacchetto AIP: " + e.getMessage());
        }
    }

    /**
     * Cerca nello zip un'entry il cui nome (o path relativo) corrisponda a quello indicato,
     * confrontando sia il nome completo sia il solo nome del file (senza percorso), in modo da
     * essere tollerante rispetto alla struttura interna delle cartelle del pacchetto AIP.
     */
    private ZipEntry trovaEntry(ZipFile zipFile, String nomeORelativePath) {
        String normalizzato = normalizza(nomeORelativePath);
        ZipEntry entry = zipFile.getEntry(normalizzato);
        if (entry != null) {
            return entry;
        }
        String soloNomeFile = estraiNomeFile(normalizzato);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry candidata = entries.nextElement();
            String normalizzataCandidata = normalizza(candidata.getName());
            if (normalizzataCandidata.equals(normalizzato)
                    || normalizzataCandidata.endsWith("/" + normalizzato)
                    || estraiNomeFile(normalizzataCandidata).equals(soloNomeFile)) {
                return candidata;
            }
        }
        return null;
    }

    private String normalizza(String path) {
        String p = path.trim().replace('\\', '/');
        while (p.startsWith("./")) {
            p = p.substring(2);
        }
        while (p.startsWith("/")) {
            p = p.substring(1);
        }
        return p;
    }

    private String estraiNomeFile(String path) {
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }

    private byte[] leggiEntry(ZipFile zipFile, ZipEntry entry) throws IOException {
        try (InputStream is = zipFile.getInputStream(entry)) {
            return org.apache.commons.io.IOUtils.toByteArray(is);
        }
    }

    private void validaXsd(byte[] xmlBytes, byte[] xsdBytes) throws SAXException, IOException {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        Schema schema = schemaFactory
                .newSchema(new StreamSource(new ByteArrayInputStream(xsdBytes)));
        Validator validator = schema.newValidator();
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        validator.validate(new StreamSource(new ByteArrayInputStream(xmlBytes)));
    }

    /**
     * Estrae dall'indice AIP (Unisincro) i path completi dei file referenziati, cercando sia il
     * testo dei tag {@code <sincro:Path>} sia il valore dell'attributo {@code sincro:xmlSchema} (o
     * {@code xmlSchema}) dei tag {@code <sincro:MoreInfo>}. La ricerca xpath e' basata su
     * {@code local-name()} per essere indipendente dal prefisso di namespace effettivamente usato
     * nel documento.
     */
    private List<String> estraiPathReferenziati(byte[] xmlBytes) throws IOException {
        List<String> risultato = new ArrayList<>();
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            DocumentBuilder builder = dbFactory.newDocumentBuilder();
            Document document = builder
                    .parse(new ByteArrayInputStream(xmlBytes), StandardCharsets.UTF_8.name());

            XPath xPath = XPathFactory.newInstance().newXPath();

            NodeList pathNodes = (NodeList) xPath.evaluate("//*[local-name()='Path']", document,
                    XPathConstants.NODESET);
            for (int i = 0; i < pathNodes.getLength(); i++) {
                risultato.add(pathNodes.item(i).getTextContent());
            }

            NodeList moreInfoNodes = (NodeList) xPath.evaluate("//*[local-name()='MoreInfo']",
                    document, XPathConstants.NODESET);
            for (int i = 0; i < moreInfoNodes.getLength(); i++) {
                org.w3c.dom.NamedNodeMap attributi = moreInfoNodes.item(i).getAttributes();
                if (attributi == null) {
                    continue;
                }
                for (int j = 0; j < attributi.getLength(); j++) {
                    org.w3c.dom.Node attributo = attributi.item(j);
                    if ("xmlSchema".equalsIgnoreCase(attributo.getLocalName())) {
                        risultato.add(attributo.getNodeValue());
                    }
                }
            }
        } catch (Exception e) {
            throw new IOException(
                    "Errore durante l'estrazione dei path referenziati dall'indice AIP: "
                            + e.getMessage(),
                    e);
        }
        return risultato;
    }
}
