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

package it.eng.parer.job.timer;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.interceptor.Interceptors;

import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.utils.JobConstants;

/**
 *
 * @author Iacolucci_M
 */
@Singleton(mappedName = "PreparaPartizioneDaMigrareTimer2")
@Lock(LockType.READ)
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class PreparaPartizioneDaMigrareTimer2 extends PreparaPartizioneDaMigrareTimerSuperclass {

    @EJB
    private PreparaPartizioneDaMigrareTimer2 thisTimer;

    /*
     * COSTRUTTORE: in tutte le classi copiate da questa sostituire il numero 1 di questo
     * costruttore col numero del timer ennesimo che si vuole deployare.
     */
    public PreparaPartizioneDaMigrareTimer2() {
	super(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name());
	numeroJob = 2;
	logger.debug(PreparaPartizioneDaMigrareTimer2.class.getName() + numeroJob + " creato");
    }

    @Override
    @Lock(LockType.WRITE)
    public void startSingleAction(String applicationName) {
	super.startSingleAction(applicationName);
    }

    @Override
    @Lock(LockType.WRITE)
    public void startCronScheduled(CronSchedule sched, String applicationName) {
	super.startCronScheduled(sched, applicationName);
    }

    @Override
    @Lock(LockType.WRITE)
    public void stop(String applicationName) {
	super.stop(applicationName);
    }

    @Timeout
    @Override
    public void doJob(Timer timer) {
	if (timer.getInfo().equals(jobName)) {
	    try {
		thisTimer.startProcess(timer);
	    } catch (Exception e) {
		logger.error("Errore nell'esecuzione del job di Prepara Partizione da migrare ["
			+ numeroJob + "] ", e);
	    }
	}
    }

    @Override
    public void startProcess(Timer timer) {
	super.startProcess(timer);
    }
}
