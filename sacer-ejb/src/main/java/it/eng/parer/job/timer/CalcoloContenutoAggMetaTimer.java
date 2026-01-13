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
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.calcoloContenutoAggMeta.ejb.CalcoloContenutoAggMetaEjb;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.volume.utils.VolumeEnums.OpTypeEnum;

/**
 *
 * @author Gilioli_P
 */
@Singleton(mappedName = "CalcoloContenutoAggMetaTimer")
@LocalBean
@Lock(LockType.READ)
public class CalcoloContenutoAggMetaTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(CalcoloContenutoAggMetaTimer.class);
    @EJB
    private CalcoloContenutoAggMetaTimer thisTimer;
    @EJB
    private CalcoloContenutoAggMetaEjb calcoloContenutoAggMetaEjb;

    public CalcoloContenutoAggMetaTimer() {
        super(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name());
        logger.debug(CalcoloContenutoAggMetaTimer.class.getName() + " creato");
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
            logger.info("Schedulazione: Ore: {}", sched.getHour());
            logger.info("Schedulazione: Minuti: {}", sched.getMinute());
            logger.info("Schedulazione: DOW: {}", sched.getDayOfWeek());
            logger.info("Schedulazione: Mese: {}", sched.getMonth());
            logger.info("Schedulazione: DOM: {}", sched.getDayOfMonth());

            tmpScheduleExpression = new ScheduleExpression();
            tmpScheduleExpression.hour(sched.getHour());
            tmpScheduleExpression.minute(sched.getMinute());
            tmpScheduleExpression.dayOfWeek(sched.getDayOfWeek());
            tmpScheduleExpression.month(sched.getMonth());
            tmpScheduleExpression.dayOfMonth(sched.getDayOfMonth());
            logger.info("Lancio il timer CalcoloContenutoAggMetaTimer...");
            timerService.createCalendarTimer(tmpScheduleExpression,
                    new TimerConfig(jobName, false));
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
        logger.info("Calcolo Contenuto Aggiornamenti Metadati - Inizio schedulazione");
        try {
            calcoloContenutoAggMetaEjb.calcolaContenutoAggMetaEjb();
        } catch (ParerInternalError e) {
            // jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
            logger.error(
                    "Calcolo Contenuto Aggiornamenti Metadati - Errore durante l'esecuzione del job ",
                    e);
            jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name(), e.getDescription());
        } catch (Exception e) {
            // jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
            String message = "Calcolo Contenuto Aggiornamenti Metadati - Errore generico durante l'esecuzione del job";
            if (e.getCause() != null) {
                message = message + ": " + ExceptionUtils.getRootCauseMessage(e);
            }
            logger.error(
                    "Calcolo Contenuto Aggiornamenti Metadati - Errore generico durante l'esecuzione del job ",
                    e);
            jobHelper.writeAtomicLogJob(jobName, OpTypeEnum.ERRORE.name(), message);
        }
    }
}
