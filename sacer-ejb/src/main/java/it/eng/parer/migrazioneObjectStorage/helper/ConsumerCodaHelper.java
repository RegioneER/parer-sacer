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

package it.eng.parer.migrazioneObjectStorage.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroCompHashCalc;
import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.migrazioneObjectStorage.exception.MigObjStorageCompHashCalcMoreThanOneException;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ConsumerCodaHelper")
@LocalBean
public class ConsumerCodaHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(ConsumerCodaHelper.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public OstMigrazFile getOstMigrazFileLocked(String nmTabellaIdOggetto, BigDecimal idOggetto) {
        Query query = entityManager.createQuery("SELECT migrazFile FROM OstMigrazFile migrazFile "
                + "WHERE migrazFile.nmTabellaIdOggetto = :nmTabellaIdOggetto "
                + "AND migrazFile.idOggetto = :idOggetto ");
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        query.setParameter("nmTabellaIdOggetto", nmTabellaIdOggetto);
        query.setParameter("idOggetto", idOggetto);
        List<OstMigrazFile> migrazFileList = query.getResultList();
        if (!migrazFileList.isEmpty()) {
            return migrazFileList.get(0);
        }
        return null;
    }

    public void deleteOstMigrazFileErrList(long idMigrazFile) {
        Query q = getEntityManager().createQuery("DELETE FROM OstMigrazFileErr migrazFileErr "
                + "WHERE migrazFileErr.ostMigrazFile.idMigrazFile = :idMigrazFile ");
        q.setParameter("idMigrazFile", idMigrazFile);
        q.executeUpdate();
        getEntityManager().flush();
    }

    public AroCompHashCalc getAroCompHashCalc(long idCompDoc, String dsAlgoHashFile) {
        Query query = entityManager.createQuery("SELECT compHashCalc FROM AroCompHashCalc compHashCalc "
                + "WHERE compHashCalc.aroCompDoc.idCompDoc = :idCompDoc "
                + "AND compHashCalc.dsAlgoHashFile = :dsAlgoHashFile ");
        query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
        query.setParameter("idCompDoc", idCompDoc);
        query.setParameter("dsAlgoHashFile", dsAlgoHashFile);
        List<AroCompHashCalc> compHashCalcList = query.getResultList();
        if (!compHashCalcList.isEmpty()) {
            return compHashCalcList.get(0);
        }
        return null;
    }

    public AroCompHashCalc getAroCompHashCalcByIdOggetto(BigDecimal idOggetto) {
        Query query = entityManager.createQuery("SELECT compHashCalc FROM AroCompHashCalc compHashCalc "
                + "WHERE compHashCalc.aroCompDoc.idCompDoc = :idOggetto  ");
        query.setParameter("idOggetto", longFromBigDecimal(idOggetto));
        List<AroCompHashCalc> compHashCalcList = query.getResultList();
        if (compHashCalcList != null && !compHashCalcList.isEmpty()) {
            if (compHashCalcList.size() != 1) {
                final String msg = String.format("Per il componente %s sono presenti più di un hash calcolato",
                        idOggetto);
                log.error(msg);
                throw new MigObjStorageCompHashCalcMoreThanOneException(msg);
            } else {
                return compHashCalcList.get(0);
            }
        }
        // null
        final String msg = String.format("Per il componente %s non sono presenti hash calcolati", idOggetto);
        log.error(msg);
        throw new MigObjStorageCompHashCalcMoreThanOneException(msg);
    }

}
