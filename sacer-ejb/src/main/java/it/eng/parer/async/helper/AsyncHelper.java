package it.eng.parer.async.helper;

import it.eng.parer.entity.LogLockElab;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "AsyncHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class AsyncHelper {

    Logger log = LoggerFactory.getLogger(AsyncHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private JobHelper jobHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void initLockPerStrut(String asyncTask, Long idStrut) {
        // Verifico la presenza del lock per la struttura
        Query query = entityManager.createQuery(
                "SELECT count(lock) FROM LogLockElab lock WHERE lock.nmElab = :nmElab AND lock.tiLockElab = :tiLock AND lock.orgStrut.idStrut = :idStrut");
        query.setParameter("nmElab", asyncTask);
        query.setParameter("tiLock", JobConstants.LockTypeEnum.LOCK_PER_STRUT.name());
        query.setParameter("idStrut", idStrut);
        Long count = (Long) query.getSingleResult();
        if (count == 0) {
            OrgStrut strut = entityManager.find(OrgStrut.class, idStrut.longValue(), LockModeType.PESSIMISTIC_WRITE);
            count = (Long) query.getSingleResult();
            if (count == 0) {
                LogLockElab lockRecord = new LogLockElab();
                lockRecord.setNmElab(asyncTask);
                lockRecord.setTiLockElab(JobConstants.LockTypeEnum.LOCK_PER_STRUT.name());
                lockRecord.setOrgStrut(strut);
                lockRecord.setFlElabAttiva(JobConstants.DB_FALSE);
                entityManager.persist(lockRecord);
                entityManager.flush();
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long getLock(String asyncTask, Long idStrut) {
        // Verifico la presenza del lock
        LogLockElab lockRecord;
        try {
            if (idStrut != null) {
                // Considero che sia il caso 'LOCK_PER_STRUT'
                Query query = entityManager.createQuery(
                        "SELECT lock FROM LogLockElab lock WHERE lock.nmElab = :nmElab AND lock.tiLockElab = :tiLock AND lock.orgStrut.idStrut = :idStrut");
                query.setParameter("nmElab", asyncTask);
                query.setParameter("tiLock", JobConstants.LockTypeEnum.LOCK_PER_STRUT.name());
                query.setParameter("idStrut", idStrut);
                lockRecord = (LogLockElab) query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
            } else {
                // Considero che sia il caso 'LOCK_UNICO'
                Query query = entityManager.createQuery(
                        "SELECT lock FROM LogLockElab lock WHERE lock.nmElab = :nmElab AND lock.tiLockElab = :tiLock");
                query.setParameter("nmElab", asyncTask);
                query.setParameter("tiLock", JobConstants.LockTypeEnum.LOCK_UNICO.name());
                lockRecord = (LogLockElab) query.setLockMode(LockModeType.PESSIMISTIC_WRITE).getSingleResult();
            }
            // Verifica del lock
            if (lockRecord.getFlElabAttiva().equals(JobConstants.DB_FALSE)) {
                lockRecord.setFlElabAttiva(JobConstants.DB_TRUE);
                return lockRecord.getIdLockElab();
            } else {
                // Scrivo già che il servizio chiude con errore in quanto impossibile acquisire il lock
                log.info(asyncTask + " --- Impossibile acquisire il lock");
                return null;
            }
        } catch (LockTimeoutException lte) {
            log.info(asyncTask + " --- Impossibile acquisire il lock", lte);
            return null;
        }
    }

    public void writeEndLogLock(Long idLock, String asyncTask, String opType, String desc) {
        writeEndLogLock(idLock, asyncTask, opType, desc, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeEndLogLock(Long idLock, String asyncTask, String opType, String desc, Long idStrut) {
        LogLockElab lockRecord = entityManager.find(LogLockElab.class, idLock);
        lockRecord.setFlElabAttiva(JobConstants.DB_FALSE);
        entityManager.flush();

        if (idStrut != null) {
            jobHelper.writeLogJob(asyncTask, opType, desc, idStrut);
        } else {
            jobHelper.writeLogJob(asyncTask, opType, desc);
        }
    }

}
