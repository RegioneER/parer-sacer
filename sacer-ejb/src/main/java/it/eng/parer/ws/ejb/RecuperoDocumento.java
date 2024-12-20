/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.ws.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * Passaggio di un wrapper con l'oggetto interessato dall'object storing
     *
     * @param dto
     *            dot recupero
     *
     * @return true se è andato tutto bene, false altrimenti
     */
    public boolean callRecuperoDocSuStream(RecuperoDocBean dto) {
        // verifica esistenza object storage
        if (existInObjectStorage(dto)) {
            log.debug("RecuperoDocumento.callRecuperoDocSuStream : recupero from ObjectStorage, doc = {}", dto);
            return recObjectStorage.recuperaObjectStorageSuStream(dto);
        }
        // default (ASIS : pre object storage)
        if (dto.getTabellaBlobDaLeggere() != null) {
            log.debug("RecuperoDocumento.callRecuperoDocSuStream : recupero from BlbOracle, doc = {}", dto);
            return recBlbOracle.recuperaBlobCompSuStream(dto.getId(), dto.getOs(), dto.getTabellaBlobDaLeggere(), dto);
        } else {
            // MEV#34239 - Estensione servizio per recupero file sbustati
            // In questo ramo per quanto riguarda lo sbustamento dei .p7m non ci parrerà mai
            // perché sono dei binari, quindi BLOB
            log.debug("RecuperoDocumento.callRecuperoDocSuStream : recupero from ClbOracle, doc = {}", dto);
            return recClbOracle.recuperaClobDataSuStream(dto.getId(), dto.getOs(), dto.getTabellaClobDaLeggere());
        }
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

}
