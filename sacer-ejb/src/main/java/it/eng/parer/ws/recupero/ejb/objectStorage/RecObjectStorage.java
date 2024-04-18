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

package it.eng.parer.ws.recupero.ejb.objectStorage;

import java.io.OutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;

/**
 *
 * @author Sinatti_S
 */
@Stateless(mappedName = "RecObjectStorage")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecObjectStorage {

    private static final Logger log = LoggerFactory.getLogger(RecObjectStorage.class);

    private static final String LOG_EXCEPTION_OS = "Eccezione gestione object storage su stream ";

    @EJB
    private ObjectStorageService objectStorageService;

    private boolean recuperaObjectStorageCompSuStream(long idCompDoc, OutputStream outputStream) {
        boolean rc = false;

        try {
            objectStorageService.getObjectComponente(idCompDoc, outputStream);
            rc = true;
        } catch (Exception e) {

            log.error(LOG_EXCEPTION_OS, e);
        }

        return rc;
    }

    private boolean recuperaObjectStorageReportvfSuStream(long idCompDoc, OutputStream outputStream) {
        boolean rc = false;

        try {
            objectStorageService.getObjectReportvf(idCompDoc, outputStream);
            rc = true;
        } catch (Exception e) {
            log.error(LOG_EXCEPTION_OS, e);

        }
        return rc;

    }

    // MEV#30395
    private boolean recuperaObjectStorageIndiceAipUdSuStream(long idVerIndiceAip, OutputStream outputStream) {
        boolean rc = false;

        try {
            objectStorageService.getObjectIndiceAipUd(idVerIndiceAip, outputStream);
            rc = true;
        } catch (Exception e) {

            log.error(LOG_EXCEPTION_OS, e);
        }

        return rc;
    }
    // end MEV#30395

    /**
     * Recupera il tipo oggetto identificato nel dto.
     *
     * @param dto
     *            report verifica oppure componente
     *
     * @return true se l'oggetto è stato scritto sull'output stream del dto, false altrimenti
     */
    public boolean recuperaObjectStorageSuStream(RecuperoDocBean dto) {

        boolean rc = false;
        switch (dto.getTipo()) {
        case COMP_DOC:
            rc = this.recuperaObjectStorageCompSuStream(dto.getId(), dto.getOs());
            break;
        case REPORTVF:
            rc = this.recuperaObjectStorageReportvfSuStream(dto.getId(), dto.getOs());
            break;
        // MEV#30395
        case INDICE_AIP:
            rc = this.recuperaObjectStorageIndiceAipUdSuStream(dto.getId(), dto.getOs());
            break;
        // end MEV#30395
        default:
            log.warn("Tipo oggetto {} non supportato", dto.getTipo());
            break;
        }

        return rc;
    }
}
