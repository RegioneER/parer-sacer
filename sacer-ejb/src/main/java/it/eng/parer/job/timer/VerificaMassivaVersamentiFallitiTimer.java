package it.eng.parer.job.timer;

import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.job.verificaVersamenti.ejb.VerificaMassivaVersamentiFallitiEjb;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Singleton(mappedName = "VerificaMassivaVersamentiFallitiTimer")
@Lock(LockType.READ)
@LocalBean
public class VerificaMassivaVersamentiFallitiTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(VerificaMassivaVersamentiFallitiTimer.class);
    @EJB
    private VerificaMassivaVersamentiFallitiTimer thisTimer;
    @EJB
    private VerificaMassivaVersamentiFallitiEjb verificaMassivaVersamentiFallitiEjb;

    // private final String NAME_JOB_VERIFICA_MASSIVA_VERS_FALLITI = "VerificaMassivaVersamentiFallitiTimer";
    public VerificaMassivaVersamentiFallitiTimer() {
        super(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name());
        logger.debug(VerificaMassivaVersamentiFallitiTimer.class.getName() + " creato");
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
            tmpScheduleExpression.month(sched.getMonth());
            tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
            logger.info("Lancio il timer VerificaMassivaVersamentiFallitiTimer...");
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
            thisTimer.startProcess(timer);
        }
    }

    @Override
    public void startProcess(Timer timer) {
        try {
            // Scrittura atomica dell'avvio del job presente all'interno del metodo sottostante
            verificaMassivaVersamentiFallitiEjb.verificaMassivaVersamentiFalliti();
        } catch (Exception e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            logger.error("Errore nell'esecuzione del job di verifica massiva versamenti falliti: "
                    + ExceptionUtils.getRootCauseMessage(e), e);
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.ERRORE.name(), message);
        }
    }
}