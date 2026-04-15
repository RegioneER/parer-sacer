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

package it.eng.parer.xml.xsd.helper;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.constraint.DecModelloXsdFascRif.TiRiferimento;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.xml.xsd.XsdBlob;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Calendar;
import java.util.Date;

@Stateless
@LocalBean
public class XsdRepositoryHelper extends GenericHelper {

    /**
     * Trova un XSD importato/incluso
     *
     * @param tipo           Tipo di riferimento (IMPORT/INCLUDE)
     * @param namespaceUri   Namespace URI (xs:import/@namespace)
     * @param schemaLocation Schema location (xs:import/@schemaLocation o
     *                       xs:include/@schemaLocation)
     * @param idPadre        ID del modello XSD padre
     * @return XsdBlob con codice e contenuto dell'XSD, null se non trovato
     */
    public XsdBlob findImportedXsd(String tipo, String namespaceUri, String schemaLocation,
            Long idPadre) {
        // Tentativo 1: match stretto su namespace + schemaLocation + padre
        XsdBlob xsd = findByTipoNamespaceAndLocation(tipo, namespaceUri, schemaLocation, idPadre);
        if (xsd != null) {
            return xsd;
        }

        // Fallback 1: se IMPORT e schemaLocation non risolve, prova solo namespace + padre
        if ("IMPORT".equalsIgnoreCase(tipo) && namespaceUri != null && !namespaceUri.isBlank()) {
            xsd = findByImportNamespaceOnly(namespaceUri, idPadre);
            if (xsd != null) {
                return xsd;
            }
        }

        // Fallback 2: se INCLUDE, spesso namespaceUri è null; prova per schemaLocation + padre
        if ("INCLUDE".equalsIgnoreCase(tipo) && schemaLocation != null
                && !schemaLocation.isBlank()) {
            xsd = findByIncludeLocationOnly(schemaLocation, idPadre);
            if (xsd != null) {
                return xsd;
            }
        }

        return null;
    }

    /**
     * Match esatto: tipo + namespace + schemaLocation + padre
     *
     * @param tipo           Tipo di riferimento (IMPORT/INCLUDE)
     * @param namespaceUri   Namespace URI
     * @param schemaLocation Schema location
     * @param idPadre        ID del modello XSD padre
     * @return il modello XSD se trovato, null altrimenti
     */
    private XsdBlob findByTipoNamespaceAndLocation(String tipo, String namespaceUri,
            String schemaLocation, Long idPadre) {
        try {
            String jpql = "SELECT NEW it.eng.parer.xml.xsd.XsdBlob(tgt.cdXsd, tgt.blXsd) "
                    + "FROM DecModelloXsdFascRif r "
                    + "JOIN r.decModelloXsdFascicoloTarget tgt "
                    + "WHERE r.tiRiferimento = :tipo "
                    + "AND COALESCE(r.namespaceUri, '-') = COALESCE(:namespaceUri, '-') "
                    + "AND COALESCE(r.schemaLocation, '-') = COALESCE(:schemaLocation, '-') "
                    + "AND r.decModelloXsdFascicoloPadre.idModelloXsdFascicolo = :idPadre "
                    + "AND r.dtSoppres > :now "
                    + "AND tgt.dtSoppres > :now";

            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("tipo", TiRiferimento.valueOf(tipo.toUpperCase()));
            query.setParameter("namespaceUri", namespaceUri);
            query.setParameter("schemaLocation", schemaLocation);
            query.setParameter("idPadre", idPadre);
            query.setParameter("now", new java.util.Date());

            return (XsdBlob) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Cerca per IMPORT: match solo su namespace + padre
     *
     * @param namespaceUri namespaceURI
     * @param idPadre      ID del modello XSD padre
     * @return il modello XSD se trovato, null altrimenti
     */
    private XsdBlob findByImportNamespaceOnly(String namespaceUri, Long idPadre) {
        try {
            String jpql = "SELECT NEW it.eng.parer.xml.xsd.XsdBlob(tgt.cdXsd, tgt.blXsd) "
                    + "FROM DecModelloXsdFascRif r "
                    + "JOIN r.decModelloXsdFascicoloTarget tgt "
                    + "WHERE r.tiRiferimento = :tipo "
                    + "AND r.namespaceUri = :namespaceUri "
                    + "AND r.decModelloXsdFascicoloPadre.idModelloXsdFascicolo = :idPadre "
                    + "AND r.dtSoppres > :now "
                    + "AND tgt.dtSoppres > :now "
                    + "ORDER BY r.idModelloXsdFascRif";

            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("tipo", TiRiferimento.IMPORT);
            query.setParameter("namespaceUri", namespaceUri);
            query.setParameter("idPadre", idPadre);
            query.setParameter("now", new java.util.Date());
            query.setMaxResults(1);

            return (XsdBlob) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Cerca per INCLUDE: match solo su schemaLocation + padre
     *
     * @param schemaLocation schemaLocation della dipendenza
     * @param idPadre        l'ID del modello XSD padre
     * @return il modello XSD se trovato, null altrimenti
     */
    private XsdBlob findByIncludeLocationOnly(String schemaLocation, Long idPadre) {
        try {
            String jpql = "SELECT NEW it.eng.parer.xml.xsd.XsdBlob(tgt.cdXsd, tgt.blXsd) "
                    + "FROM DecModelloXsdFascRif r "
                    + "JOIN r.decModelloXsdFascicoloTarget tgt "
                    + "WHERE r.tiRiferimento = :tipo "
                    + "AND r.schemaLocation = :schemaLocation "
                    + "AND r.decModelloXsdFascicoloPadre.idModelloXsdFascicolo = :idPadre "
                    + "AND r.dtSoppres > :now "
                    + "AND tgt.dtSoppres > :now "
                    + "ORDER BY r.idModelloXsdFascRif";

            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("tipo", TiRiferimento.INCLUDE);
            query.setParameter("schemaLocation", schemaLocation);
            query.setParameter("idPadre", idPadre);
            query.setParameter("now", new java.util.Date());
            query.setMaxResults(1);

            return (XsdBlob) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Recupera un modello XSD per ID, filtrando opzionalmente per validità temporale.
     *
     * @param idModelloXsdFascicolo ID del modello XSD da recuperare
     * @param filterValid           se true, restituisce solo se il modello è valido
     * @return il modello XSD se trovato
     */
    public DecModelloXsdFascicolo getModelloXsdFascicolo(Long idModelloXsdFascicolo,
            boolean filterValid) {
        try {
            StringBuilder jpql = new StringBuilder(
                    "SELECT m FROM DecModelloXsdFascicolo m WHERE m.idModelloXsdFascicolo = :id");

            if (filterValid) {
                jpql.append(" AND m.dtIstituz <= :now AND m.dtSoppres > :now");
            }

            Query query = getEntityManager().createQuery(jpql.toString());
            query.setParameter("id", idModelloXsdFascicolo);

            if (filterValid) {
                Date now = Calendar.getInstance().getTime();
                query.setParameter("now", now);
            }

            return (DecModelloXsdFascicolo) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Estrae il targetNamespace da un XSD blob
     *
     * @param xsdContent il contenuto del file XSD
     * @return il targetNamespace se presente, null altrimenti
     */
    public String extractTargetNamespace(String xsdContent) {
        if (xsdContent == null || xsdContent.isBlank()) {
            return null;
        }
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setNamespaceAware(true);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db
                    .parse(new java.io.ByteArrayInputStream(xsdContent.getBytes()));

            org.w3c.dom.Element rootElement = doc.getDocumentElement();
            String targetNamespace = rootElement.getAttribute("targetNamespace");

            return (targetNamespace != null && !targetNamespace.isBlank()) ? targetNamespace : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica se un file XSD contiene riferimenti a dipendenze esterne (import/include).
     *
     * @param xsdContent il contenuto del file XSD
     * @return true se sono presenti riferimenti, false altrimenti o in caso di errori di parsing
     */
    public boolean xsdContieneDipendenze(String xsdContent) {
        if (xsdContent == null || xsdContent.isBlank()) {
            return false;
        }
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance();
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);

            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db
                    .parse(new java.io.ByteArrayInputStream(xsdContent.getBytes()));

            org.w3c.dom.NodeList imports = doc
                    .getElementsByTagNameNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "import");
            if (imports.getLength() > 0) {
                return true;
            }

            org.w3c.dom.NodeList includes = doc.getElementsByTagNameNS(
                    javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "include");
            return includes.getLength() > 0;

        } catch (Exception e) {
            return false;
        }
    }
}