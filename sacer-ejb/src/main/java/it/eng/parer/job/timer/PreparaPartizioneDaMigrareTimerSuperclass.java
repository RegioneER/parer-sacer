package it.eng.parer.job.timer;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.migrazioneObjectStorage.job.ProducerCodaDaMigrareEjb;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 * 
 *         Superclasse da cui ereditare altre classi che rappresentano il job ennesimo di PREPARA_PARTIZIONE_DA_MIGRARE.
 */
public class PreparaPartizioneDaMigrareTimerSuperclass extends JobTimer {

    protected Logger logger = LoggerFactory.getLogger(PreparaPartizioneDaMigrareTimerSuperclass.class);
    @EJB
    private ProducerCodaDaMigrareEjb producerCodaDaMigrareEjb;

    protected int numeroJob;

    /*
     * public PreparaPartizioneDaMigrareTimerSuperclass() {
     * super(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE.name()+"_"+numeroJob);
     * logger.debug(PreparaPartizioneDaMigrareTimerSuperclass.class.getName() + " creato"); }
     */

    protected PreparaPartizioneDaMigrareTimerSuperclass(String jobName) {
        super(jobName);
    }

    @Override
    public void startSingleAction(String applicationName) {
        boolean existTimer = false;

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                existTimer = true;
            }
        }
        if (!existTimer) {
            timerService.createTimer(TIME_DURATION, jobName);
        }
    }

    @Override
    public void startCronScheduled(CronSchedule sched, String applicationName) {
        boolean existTimer = false;
        ScheduleExpression tmpScheduleExpression;

        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                existTimer = true;
            }
        }
        if (!existTimer) {
            logger.info("Schedulazione: Ore: " + sched.getHour());
            logger.info("Schedulazione: Minuti: " + sched.getMinute());
            logger.info("Schedulazione: DOW: " + sched.getDayOfWeek());
            logger.info("Schedulazione: Mese: " + sched.getMonth());
            logger.info("Schedulazione: DOM: " + sched.getDayOfMonth());

            tmpScheduleExpression = new ScheduleExpression();
            tmpScheduleExpression.hour(sched.getHour());
            tmpScheduleExpression.minute(sched.getMinute());
            tmpScheduleExpression.dayOfWeek(sched.getDayOfWeek());
            tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
            tmpScheduleExpression.month(sched.getMonth());
            logger.info("Lancio il timer PreparaPartizioneDaMigrareTimer" + numeroJob + "...");
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
        }
    }

    @Override
    public void stop(String applicationName) {
        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                timer.cancel();
            }
        }
    }

    @Override
    public void doJob(Timer timer) {
        // Implementarla nelle classi derivate
    }

    @Override
    public void startProcess(Timer timer) {
        try {
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null);
            producerCodaDaMigrareEjb.eseguiPreparazioneJob(numeroJob);
        } catch (ParerInternalError e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            Exception nativeExcp = e.getNativeException();
            if (nativeExcp != null) {
                message = nativeExcp.getMessage();
            }
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            if (message == null) {
                message = e.getDescription();
            }
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.ERRORE.name(), message);
            logger.error("Errore nell'esecuzione del job di Prepara Partizione da migrare [" + numeroJob + "] ", e);
            timer.cancel();
        } catch (Throwable e) {
            // questo log viene scritto solo in caso di errore.
            String message = "Errore nell'esecuzione del job di Prepara Partizione da migrare [" + numeroJob + "] "
                    + ExceptionUtils.getRootCauseMessage(e);
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.ERRORE.name(), message);
            logger.error("Errore nell'esecuzione del job di Prepara Partizione da migrare [" + numeroJob + "] ", e);
            timer.cancel();
        }
    }
}
