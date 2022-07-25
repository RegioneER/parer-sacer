package it.eng.parer.job.timer;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.calcoloContenutoFascicoli.ejb.CalcoloContenutoFascicoliEjb;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.volume.utils.VolumeEnums.OpTypeEnum;
import javax.ejb.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
@Singleton(mappedName = "CalcoloContenutoFascicoliTimer")
@LocalBean
@Lock(LockType.READ)
public class CalcoloContenutoFascicoliTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(CalcoloContenutoFascicoliTimer.class);
    @EJB
    private CalcoloContenutoFascicoliTimer thisTimer;
    @EJB
    private CalcoloContenutoFascicoliEjb calcoloContenutoFascicoliEjb;

    public CalcoloContenutoFascicoliTimer() {
        super(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name());
        logger.debug(CalcoloContenutoFascicoliTimer.class.getName() + " creato");
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
            logger.info("Lancio il timer CalcoloContenutoFascicoliTimer...");
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
        logger.info("Calcolo Contenuto Fascicoli - Inizio schedulazione");
        try {
            calcoloContenutoFascicoliEjb.calcolaContenutoFascicoli();
        } catch (ParerInternalError e) {
            jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
            logger.error("Calcolo Contenuto Fascicoli - Errore durante l'esecuzione del job ", e);
            jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name(), e.getDescription());
        } catch (Exception e) {
            jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
            String message = "Calcolo Contenuto Fascicoli - Errore generico durante l'esecuzione del job";
            if (e.getCause() != null) {
                message = message + ": " + ExceptionUtils.getRootCauseMessage(e);
            }
            logger.error("Calcolo Contenuto Fascicoli - Errore generico durante l'esecuzione del job ", e);
            jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name(), message);
        }
    }
}
