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

package it.eng.parer.async.helper;

import it.eng.parer.entity.LogLockElab;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.job.helper.JobHelper;

import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.*;

import it.eng.parer.job.utils.JobConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "AsyncHelper")
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class AsyncHelper {

    @PersistenceContext(unitName = "ParerJPA")
    protected EntityManager entityManager;
    Logger log = LoggerFactory.getLogger(AsyncHelper.class);

    @EJB
    private JobHelper jobHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void initLockPerStrut(String asyncTask, Long idStrut) {
	// Verifico la presenza del lock per la struttura
	Long count = countLock(asyncTask, idStrut);
	if (count == 0) {
	    OrgStrut strut = entityManager.find(OrgStrut.class, idStrut,
		    LockModeType.PESSIMISTIC_WRITE);
	    count = countLock(asyncTask, idStrut);
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
		lockRecord = (LogLockElab) query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.getSingleResult();
	    } else {
		// Considero che sia il caso 'LOCK_UNICO'
		Query query = entityManager.createQuery(
			"SELECT lock FROM LogLockElab lock WHERE lock.nmElab = :nmElab AND lock.tiLockElab = :tiLock");
		query.setParameter("nmElab", asyncTask);
		query.setParameter("tiLock", JobConstants.LockTypeEnum.LOCK_UNICO.name());
		lockRecord = (LogLockElab) query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
			.getSingleResult();
	    }
	    // Verifica del lock
	    if (lockRecord.getFlElabAttiva().equals(JobConstants.DB_FALSE)) {
		lockRecord.setFlElabAttiva(JobConstants.DB_TRUE);
		return lockRecord.getIdLockElab();
	    } else {
		// Scrivo già che il servizio chiude con errore in quanto impossibile acquisire il
		// lock
		log.info("{} --- Impossibile acquisire il lock", asyncTask);
		return null;
	    }
	} catch (LockTimeoutException lte) {
	    log.info("{} --- Impossibile acquisire il lock", asyncTask, lte);
	    return null;
	}
    }

    public void writeEndLogLock(Long idLock, String asyncTask, String opType, String desc) {
	writeEndLogLock(idLock, asyncTask, opType, desc, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeEndLogLock(Long idLock, String asyncTask, String opType, String desc,
	    Long idStrut) {
	lockRecord(idLock);

	if (idStrut != null) {
	    jobHelper.writeLogJob(asyncTask, opType, desc, idStrut);
	} else {
	    jobHelper.writeLogJob(asyncTask, opType, desc);
	}
    }

    public Long countLock(String asyncTask, Long idStrut) {
	Query query = entityManager.createQuery(
		"SELECT count(lock) FROM LogLockElab lock WHERE lock.nmElab = :nmElab AND lock.tiLockElab = :tiLock AND lock.orgStrut.idStrut = :idStrut");
	query.setParameter("nmElab", asyncTask);
	query.setParameter("tiLock", JobConstants.LockTypeEnum.LOCK_PER_STRUT.name());
	query.setParameter("idStrut", idStrut);
	return (Long) query.getSingleResult();
    }

    public void lockRecord(Long idLock) {
	LogLockElab lockRecord = entityManager.find(LogLockElab.class, idLock);
	if (lockRecord == null) {
	    throw new NoResultException("no LogLockElab found for id " + idLock);
	}
	lockRecord.setFlElabAttiva(JobConstants.DB_FALSE);
	entityManager.flush();
    }
}
