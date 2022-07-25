package it.eng.parer.job.timer;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.migrazioneObjectStorage.job.VerificaMigrazioneSubPartizioneEjb;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
@Singleton(mappedName = "VerificaMigrazioneSubPartizioneTimer")
@Lock(LockType.READ)
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class VerificaMigrazioneSubPartizioneTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(VerificaMigrazioneSubPartizioneTimer.class);
    @EJB
    private VerificaMigrazioneSubPartizioneTimer thisTimer;
    @EJB
    private VerificaMigrazioneSubPartizioneEjb verificaMigrazioneSubPartizioneEjb;

    public VerificaMigrazioneSubPartizioneTimer() {
        super(JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name());
        logger.debug(VerificaMigrazioneSubPartizioneTimer.class.getName() + " creato");
    }

    @Override
    @Lock(LockType.WRITE)
    public void startSingleAction(String appplicationName) {
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
    @Lock(LockType.WRITE)
    public void startCronScheduled(CronSchedule sched, String appplicationName) {
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
            logger.info("Lancio il timer VerificaMigrazioneSubPartizioneTimer...");
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void stop(String appplicationName) {
        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();
            if (scheduled.equals(jobName)) {
                timer.cancel();
            }
        }
    }

    @Timeout
    public void doJob(Timer timer) {
        if (timer.getInfo().equals(jobName)) {
            try {
                thisTimer.startProcess(timer);
            } catch (Exception e) {
                logger.error("Errore nell'esecuzione del job di verifica migrazione subpartizione", e);
            }
        }
    }

    @Override
    public void startProcess(Timer timer) {
        try {
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null);
            verificaMigrazioneSubPartizioneEjb.verificaMigrazioneSubPartizione();
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
            logger.error("Errore nell'esecuzione del job di verifica migrazione subpartizione", e);
            timer.cancel();
        } catch (Throwable e) {
            // questo log viene scritto solo in caso di errore.
            String message = "Errore nell'esecuzione del job di verifica migrazione subpartizione "
                    + ExceptionUtils.getRootCauseMessage(e);
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.ERRORE.name(), message);
            logger.error("Errore nell'esecuzione del job di verifica migrazione subpartizione", e);
            timer.cancel();
        }
    }
}
