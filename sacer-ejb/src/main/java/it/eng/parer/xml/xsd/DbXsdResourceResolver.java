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

import it.eng.parer.xml.xsd.helper.XsdRepositoryHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * LSResourceResolver che risolve xs:import e xs:include caricando gli XSD dal database.
 *
 * Durante la compilazione/validazione di uno schema XML, il parser JAXP incontra dichiarazioni
 * xs:import/xs:include e chiama questo resolver per recuperare il contenuto degli schemi
 * referenziati.
 *
 * Il resolver effettua lookup nel database tramite XsdRepositoryHelper, utilizzando i parametri
 * namespace e systemId (schemaLocation) per individuare l'XSD corretto.
 */
public class DbXsdResourceResolver implements LSResourceResolver {

    private static final Logger logger = LoggerFactory.getLogger(DbXsdResourceResolver.class);

    private final XsdRepositoryHelper helper;
    private final Long idPadre;

    /**
     * Costruisce un nuovo resolver con riferimento al modello XSD padre.
     *
     * @param helper  Helper per l'accesso al repository XSD
     * @param idPadre ID del modello XSD padre
     */
    public DbXsdResourceResolver(XsdRepositoryHelper helper, Long idPadre) { // MODIFICATO
        this.helper = helper;
        this.idPadre = idPadre;
    }

    /**
     * Risolve una risorsa XSD importata/inclusa.
     *
     * @param type         Tipo di risorsa (tipicamente "http://www.w3.org/2001/XMLSchema")
     * @param namespaceURI Namespace dell'import (valorizzato per xs:import, null per xs:include)
     * @param publicId     Public ID (raramente usato)
     * @param systemId     System ID / schema location (es. "catasto-model-1.xsd")
     * @param baseURI      URI base dello schema parent
     * @return LSInput con il contenuto dell'XSD, null se non trovato
     */
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId,
            String systemId, String baseURI) {

        logger.debug("Risoluzione XSD: type={}, namespace={}, systemId={}, baseURI={}", type,
                namespaceURI, systemId, baseURI);

        // Determina il tipo di riferimento (IMPORT/INCLUDE)
        // Nota: il parametro 'type' non indica import/include, quindi usiamo una strategia
        // pragmatica
        // In pratica, se c'è namespace di solito è IMPORT, altrimenti INCLUDE
        final String riferimento = determineRiferimentoType(namespaceURI, systemId);

        try {
            XsdBlob xsd = helper.findImportedXsd(riferimento, namespaceURI, systemId, idPadre);

            if (xsd == null) {
                logger.warn("XSD non trovato nel repository: namespace={}, systemId={}",
                        namespaceURI, systemId);
                throw new IllegalStateException(
                        "Nessuna dipendenza configurata sul modello XSD corrisponde al riferimento dichiarato nell'XSD. "
                                + "Verificare che tipo riferimento, namespace URI e schema location della dipendenza "
                                + "siano coerenti con quanto dichiarato nei tag xs:import/xs:include dell'XSD.");
            }

            logger.debug("XSD risolto dal DB: cdXsd={}, namespace={}, systemId={}", xsd.getCdXsd(),
                    namespaceURI, systemId);

            // Crea LSInput con il contenuto dell'XSD
            StringLSInput input = new StringLSInput();
            input.setPublicId(publicId);
            input.setSystemId(systemId != null ? systemId : xsd.getCdXsd());
            input.setBaseURI(baseURI);
            input.setEncoding("UTF-8");
            input.setStringData(xsd.getBlXsd());

            return input;

        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Errore durante la risoluzione XSD: namespace={}, systemId={}",
                    namespaceURI, systemId, e);
            throw new IllegalStateException("Errore durante la validazione delle dipendenze XSD.",
                    e);
        }
    }

    /**
     * Determina il tipo di riferimento (IMPORT/INCLUDE) in base ai parametri.
     *
     * @param namespaceURI Namespace URI
     * @param systemId     System ID
     * @return "IMPORT" o "INCLUDE"
     */
    private String determineRiferimentoType(String namespaceURI, String systemId) {
        // In pratica, xs:import ha sempre namespace valorizzato
        // xs:include tipicamente ha namespace null
        // Tuttavia, per robustezza usiamo IMPORT come default e lasciamo
        // che il DAO gestisca i fallback appropriati
        if (namespaceURI != null && !namespaceURI.isBlank()) {
            return "IMPORT";
        } else if (systemId != null && !systemId.isBlank()) {
            // Probabilmente un include
            return "INCLUDE";
        }
        return "IMPORT"; // Default
    }
}