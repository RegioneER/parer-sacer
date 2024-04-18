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

package it.eng.parer.migrazioneObjectStorage.job;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.migrazioneObjectStorage.ejb.ElaborazioneCodaDaMigrareEjb;
import it.eng.parer.migrazioneObjectStorage.utils.MsgUtil;

/**
 *
 * @author Iacolucci_M
 */
@Stateless(mappedName = "ProducerCodaDaMigrareEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ProducerCodaDaMigrareEjb {

    Logger log = LoggerFactory.getLogger(ProducerCodaDaMigrareEjb.class);

    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElaborazioneCodaDaMigrareEjb elaborazioneCodaDaMigrareEjb;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eseguiPreparazioneJob(int numeroJob) throws ParerInternalError {
        elaborazioneCodaDaMigrareEjb.completaSubpartizioniBlob(numeroJob);
        elaborazioneCodaDaMigrareEjb.aggiungiSubpartizioniBlob(numeroJob);
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE.name() + "_" + numeroJob,
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
        final String msgDebugName = JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE.name() + "_" + numeroJob;
        log.debug("{} - Chiusura transazione di PreparaPartizioneDaMigrareEjb", msgDebugName);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eseguiProducerJob(int numeroJob) throws ParerInternalError {
        ElaborazioneCodaDaMigrareEjb.ContatoriPerMigrazioni totalizzatore = elaborazioneCodaDaMigrareEjb
                .aggiungiInCodaDaMigrare(numeroJob);
        String messaggioJob = null;
        if (totalizzatore.isIsAddedInQueue()) {
            /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP */
            log.debug("{} - Chiusura transazione di ProducerCodaDaMigrareEjb",
                    JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE);
        } else {
            if (totalizzatore.isIsNotFileToMigrate()) {
                messaggioJob = MsgUtil.getCompleteMessage("OST-002");
            } else {
                if (totalizzatore.isIsCodaPiena()) {
                    messaggioJob = MsgUtil.getCompleteMessage("OST-003");
                }
            }
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE.name() + "_" + numeroJob,
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), messaggioJob);
        final String msgDebugName = JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE.name() + "_" + numeroJob;
        log.debug("{} - Chiusura transazione di ProducerCodaDaMigrareEjb", msgDebugName);
    }

}
