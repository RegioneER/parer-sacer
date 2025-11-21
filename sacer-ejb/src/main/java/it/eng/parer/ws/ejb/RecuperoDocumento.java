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

package it.eng.parer.ws.ejb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.crypto.model.CryptoSignedP7mUri;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.ws.recupero.ejb.objectStorage.RecObjectStorage;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recupero.ejb.oracleClb.RecClbOracle;

/**
 *
 * @author Sinatti_S
 */
@Stateless(mappedName = "RecuperoDocumento")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecuperoDocumento {

    private static final Logger log = LoggerFactory.getLogger(RecuperoDocumento.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    //
    @EJB
    RecBlbOracle recBlbOracle;
    //
    @EJB
    RecClbOracle recClbOracle;
    //
    @EJB
    RecObjectStorage recObjectStorage;
    //
    @EJB
    ObjectStorageService objectStorageService;

    @EJB
    CryptoInvoker cryptoInvoker;

    /**
     * Passaggio di un wrapper con l'oggetto interessato dall'object storing
     *
     * @param dto bean con informazioni per il recupero
     *
     * @return true se è andato tutto bene, false altrimenti
     */
    public boolean callRecuperoDocSuStream(RecuperoDocBean dto) {
        // verifica esistenza object storage
        if (existInObjectStorage(dto)) {
            log.debug(
                    "RecuperoDocumento.callRecuperoDocSuStream : recupero from ObjectStorage, doc = {}",
                    dto);
            return recObjectStorage.recuperaObjectStorageSuStream(dto);
        }
        // default (ASIS : pre object storage)
        if (dto.getTabellaBlobDaLeggere() != null) {
            log.debug(
                    "RecuperoDocumento.callRecuperoDocSuStream : recupero from BlbOracle, doc = {}",
                    dto);
            return recBlbOracle.recuperaBlobCompSuStream(dto.getId(), dto.getOs(),
                    dto.getTabellaBlobDaLeggere(), dto);
        } else {
            log.debug(
                    "RecuperoDocumento.callRecuperoDocSuStream : recupero from ClbOracle, doc = {}",
                    dto);
            return recClbOracle.recuperaClobDataSuStream(dto.getId(), dto.getOs(),
                    dto.getTabellaClobDaLeggere());
        }
    }

    /**
     * Recupare il file originale da documento firmato del componente se richiesto. Nota: supporto
     * esclusivo per P7M
     *
     * @param dto bean con informazioni per il recupero
     *
     * @return true se è andato tutto bene, false altrimenti
     */
    public boolean callRecuperoOriginalDocFromSignedSuStream(RecuperoDocBean dto) {
        boolean result = false;
        // verifica esistenza object storage
        try {
            if (existInObjectStorage(dto)) {
                log.debug(
                        "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : recupero from ObjectStorage, doc = {} e invoca servizio",
                        dto);
                // get URL
                URL url = getPresignedURLFromOS(dto);
                // call service
                if (Objects.nonNull(url)) {
                    log.debug(
                            "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : invoca servizio per recupero documento 7m originale da URL generato {}",
                            url);
                    CryptoSignedP7mUri signed = new CryptoSignedP7mUri(url.toURI());
                    byte[] response = cryptoInvoker.retriveOriginalP7mFromURL(signed);
                    IOUtils.copyLarge(new ByteArrayInputStream(response), dto.getOs());
                    result = true; // ok
                } else {
                    log.warn(
                            "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : URL del documento su object storage non generato, impossibile recuperare p7m originale per tipo documento {} con id {}",
                            dto.getTipo().name(), dto.getId());
                }
            } else if (dto.getTabellaBlobDaLeggere() != null) {
                log.debug(
                        "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : recupero from BlbOracle, doc = {} e invoca servizio",
                        dto);
                //
                File tmpDoc = File.createTempFile("original-doc", ".p7m");
                try (OutputStream out = new FileOutputStream(tmpDoc);) {
                    if (recBlbOracle.recuperaBlobCompSuStream(dto.getId(), out,
                            dto.getTabellaBlobDaLeggere(), dto)) {
                        byte[] response = cryptoInvoker.retriveOriginalP7mFromFile(tmpDoc);
                        IOUtils.copyLarge(new ByteArrayInputStream(response), dto.getOs());
                        result = true;
                    }
                } finally {
                    FileUtils.deleteQuietly(tmpDoc);
                }
            } else {
                log.warn(
                        "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : impossibile recuperare p7m originale, documento con id {} non presente su object storage o su base dati",
                        dto.getId());
            }
        } catch (CryptoParerException ex) {
            log.warn(
                    "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : errore restituito da invocazione servizio",
                    ex);
        } catch (URISyntaxException ex) {
            log.error(
                    "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : errore elaborazione presigned URI da object storage",
                    ex);
        } catch (IOException ex) {
            log.error(
                    "RecuperoDocumento.callRecuperoOriginalDocFromSignedSuStream : errore generico in fase di recupero",
                    ex);
        }
        return result;
    }

    private boolean existInObjectStorage(RecuperoDocBean doc) {
        boolean result = false;

        switch (doc.getTipo()) {
        case COMP_DOC:
            result = objectStorageService.isComponenteOnOs(doc.getId());
            break;
        case REPORTVF:
            result = objectStorageService.isReportvfOnOsByIdCompDoc(doc.getId());
            break;
        // MEV#30395
        case INDICE_AIP:
            result = objectStorageService.isIndiceAipOnOs(doc.getId());
            break;
        // end MEV#30395
        // MEV#30397
        case ELENCO_INDICI_AIP:
            result = objectStorageService.isElencoIndiciAipOnOs(doc.getId());
            break;
        // end MEV#30397
        // MEV #30398
        case INDICE_AIP_FASC:
            result = objectStorageService.isIndiceAipFascicoloOnOs(doc.getId());
            break;
        // end MEV #30398
        // MEV #30399
        case ELENCO_INDICI_AIP_FASC:
            result = objectStorageService.isElencoIndiciAipFascOnOs(doc.getId());
            break;
        // end MEV #30399
        // MEV#30400
        case INDICE_AIP_SERIE:
            result = objectStorageService.isSerFileVerSerieUDOnOs(doc.getId(), doc.getTiFile());
            break;
        // end MEV#30400
        default:
            break;
        }

        return result;
    }

    private URL getPresignedURLFromOS(RecuperoDocBean doc) {
        URL result = null;

        switch (doc.getTipo()) {
        case COMP_DOC:
            result = objectStorageService.getPresignedURLComponente(doc.getId());
            break;
        default:
            break;
        }

        return result;
    }

}
