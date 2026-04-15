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

package it.eng.parer.xml.xsd;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.xml.utils.XmlUtils;
import it.eng.parer.xml.xsd.helper.XsdRepositoryHelper;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * Service per la validazione XML con supporto per XSD modulari (xs:import/xs:include).
 *
 * Questo service fornisce metodi per validare istanze XML rispetto a schemi XSD che referenziano
 * altri XSD tramite xs:import/xs:include, caricando tutte le dipendenze dal database.
 *
 * Esempio di utilizzo:
 *
 * <pre>
 * &#64;EJB
 * private XsdValidationService validationService;
 *
 * // Validazione con XSD modulare
 * DecModelloXsdFascicolo xsdRoot = ... // recuperato da DB
 * String xmlContent = ... // XML da validare
 *
 * validationService.validateXmlWithModularXsd(xsdRoot, xmlContent);
 * </pre>
 */
@Stateless
@LocalBean
public class XsdValidationService {

    private static final Logger logger = LoggerFactory.getLogger(XsdValidationService.class);

    @EJB
    private XsdRepositoryHelper xsdRepositoryHelper;

    /**
     * Valida un XML rispetto a uno XSD modulare che può contenere xs:import/xs:include.
     *
     * Questo metodo: 1. Crea un LSResourceResolver che risolve import/include dal database 2.
     * Compila lo schema XSD root con tutte le dipendenze 3. Valida l'XML rispetto allo schema
     * compilato
     *
     * @param xsdRoot    Modello XSD root (padre) che può importare/includere altri XSD
     * @param xmlContent Contenuto XML da validare
     * @throws SAXException             Se la validazione fallisce o lo schema non è valido
     * @throws IOException              Se ci sono errori di I/O
     * @throws IllegalArgumentException Se xsdRoot o xmlContent sono null/vuoti
     */
    public void validateXmlWithModularXsd(DecModelloXsdFascicolo xsdRoot, String xmlContent)
            throws SAXException, IOException {

        if (xsdRoot == null || xsdRoot.getBlXsd() == null || xsdRoot.getBlXsd().isBlank()) {
            throw new IllegalArgumentException("XSD root non può essere null o vuoto");
        }

        if (xmlContent == null || xmlContent.isBlank()) {
            throw new IllegalArgumentException("XML content non può essere null o vuoto");
        }

        logger.debug("Inizio validazione XML con XSD modulare: {}", xsdRoot.getCdXsd());

        // Crea il resolver per import/include
        DbXsdResourceResolver resolver = new DbXsdResourceResolver(xsdRepositoryHelper,
                xsdRoot.getIdModelloXsdFascicolo());

        try {
            // Valida usando il resolver
            XmlUtils.validateXmlWithResolver(xsdRoot.getBlXsd(), xmlContent, resolver);

            logger.info("Validazione XML completata con successo: XSD={}", xsdRoot.getCdXsd());

        } catch (SAXException e) {
            logger.error("Errore di validazione XML: XSD={}, Errore={}", xsdRoot.getCdXsd(),
                    e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.error("Errore I/O durante validazione XML: XSD={}", xsdRoot.getCdXsd(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Errore imprevisto durante validazione XML: XSD={}", xsdRoot.getCdXsd(),
                    e);
            throw new RuntimeException("Errore durante validazione XML", e);
        }
    }

    /**
     * Valida un XML rispetto a uno XSD modulare, specificando l'ID del modello XSD.
     *
     * @param idModelloXsdFascicolo ID del modello XSD root
     * @param xmlContent            Contenuto XML da validare
     * @throws SAXException             Se la validazione fallisce o lo schema non è valido
     * @throws IOException              Se ci sono errori di I/O
     * @throws IllegalArgumentException Se l'XSD non viene trovato o i parametri sono invalidi
     */
    public void validateXmlWithModularXsd(Long idModelloXsdFascicolo, String xmlContent)
            throws SAXException, IOException {

        if (idModelloXsdFascicolo == null) {
            throw new IllegalArgumentException("ID modello XSD non può essere null");
        }

        // Recupera il modello XSD dal database
        DecModelloXsdFascicolo xsdRoot = xsdRepositoryHelper
                .getModelloXsdFascicolo(idModelloXsdFascicolo, true);

        if (xsdRoot == null) {
            throw new IllegalArgumentException(
                    "Modello XSD non trovato o non valido: ID=" + idModelloXsdFascicolo);
        }

        validateXmlWithModularXsd(xsdRoot, xmlContent);
    }

    /**
     * Valida un XML rispetto a uno XSD standard (senza import/include).
     *
     * Questo metodo è equivalente alla validazione tradizionale e non utilizza il resolver per
     * import/include. Utile per mantenere compatibilità con codice esistente che non necessita di
     * XSD modulari.
     *
     * @param xsd Contenuto dello schema XSD
     * @param xml Contenuto XML da validare
     * @throws SAXException Se la validazione fallisce
     * @throws IOException  Se ci sono errori di I/O
     */
    public void validateXmlSimple(String xsd, String xml) throws SAXException, IOException {
        XmlUtils.validateXml(xsd, xml);
    }
}
