package it.eng.parer.job.timer;

import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.interceptor.Interceptors;

/**
 *
 * @author Iacolucci_M
 */
@Singleton(mappedName = "ProducerCodaDaMigrareTimer2")
@Lock(LockType.READ)
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ProducerCodaDaMigrareTimer2 extends ProducerCodaDaMigrareTimerSuperclass {

    @EJB
    private ProducerCodaDaMigrareTimer2 thisTimer;

    /*
     * COSTRUTTORE: in tutte le classi copiate da questa sostituire il numero 1 di questo costruttore col numero del
     * timer ennesimo che si vuole deployare.
     */
    public ProducerCodaDaMigrareTimer2() {
        super(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name());
        numeroJob = 2;
        logger.debug(ProducerCodaDaMigrareTimer2.class.getName() + numeroJob + " creato");
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
                logger.error("Errore nell'esecuzione del job di Producer coda da migrare [" + numeroJob + "] ", e);
            }
        }
    }

    @Override
    public void startProcess(Timer timer) {
        super.startProcess(timer);
    }
}
