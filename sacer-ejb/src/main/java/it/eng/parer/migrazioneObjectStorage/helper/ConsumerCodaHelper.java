package it.eng.parer.migrazioneObjectStorage.helper;

import it.eng.parer.entity.AroCompHashCalc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.migrazioneObjectStorage.exception.MigObjStorageCompHashCalcMoreThanOneException;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.entity.OstMigrazFile;

import javax.persistence.LockModeType;

/**
 *
 * @author Gilioli_P
 */
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
        List<OstMigrazFile> migrazFileList = (List<OstMigrazFile>) query.getResultList();
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
        List<AroCompHashCalc> compHashCalcList = (List<AroCompHashCalc>) query.getResultList();
        if (!compHashCalcList.isEmpty()) {
            return compHashCalcList.get(0);
        }
        return null;
    }

    public AroCompHashCalc getAroCompHashCalcByIdOggetto(BigDecimal idOggetto) {
        Query query = entityManager.createQuery("SELECT compHashCalc FROM AroCompHashCalc compHashCalc "
                + "WHERE compHashCalc.aroCompDoc.idCompDoc = :idOggetto  ");
        query.setParameter("idOggetto", idOggetto);
        List<AroCompHashCalc> compHashCalcList = (List<AroCompHashCalc>) query.getResultList();
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
