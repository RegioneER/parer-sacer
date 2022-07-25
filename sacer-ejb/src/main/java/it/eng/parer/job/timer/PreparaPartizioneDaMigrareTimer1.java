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
@Singleton(mappedName = "PreparaPartizioneDaMigrareTimer1")
@Lock(LockType.READ)
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class PreparaPartizioneDaMigrareTimer1 extends PreparaPartizioneDaMigrareTimerSuperclass {

    @EJB
    private PreparaPartizioneDaMigrareTimer1 thisTimer;

    /*
     * COSTRUTTORE: in tutte le classi copiate da questa sostituire il numero 1 di questo costruttore col numero del
     * timer ennesimo che si vuole deployare.
     */
    public PreparaPartizioneDaMigrareTimer1() {
        super(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name());
        numeroJob = 1;
        logger.debug(PreparaPartizioneDaMigrareTimer1.class.getName() + numeroJob + " creato");
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
                logger.error("Errore nell'esecuzione del job di Prepara Partizione da migrare [" + numeroJob + "] ", e);
            }
        }
    }

    @Override
    public void startProcess(Timer timer) {
        super.startProcess(timer);
    }
}
