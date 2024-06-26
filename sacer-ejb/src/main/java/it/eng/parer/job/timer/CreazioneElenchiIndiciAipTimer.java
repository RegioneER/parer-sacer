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
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.LogJob;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.indiceAip.ejb.CreazioneIndiceAipEjb;
import it.eng.parer.job.utils.JobConstants;

/**
 *
 * @author Gilioli_P
 */
@Singleton(mappedName = "CreazioneElenchiIndiciAipTimer")
@Lock(LockType.READ)
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneElenchiIndiciAipTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(CreazioneElenchiIndiciAipTimer.class);
    @EJB
    private CreazioneElenchiIndiciAipTimer thisTimer;
    @EJB
    private CreazioneIndiceAipEjb creazioneIndiceAipEjb;

    public CreazioneElenchiIndiciAipTimer() {
        super(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name());
        logger.debug(CreazioneElenchiIndiciAipTimer.class.getName() + " creato");
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
            logger.info("Lancio il timer CreazioneElenchiIndiciAipTimer...");
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
                logger.error("Errore nell'esecuzione del job di creazione degli elenchi indici AIP", e);
            }
        }
    }

    @Override
    public void startProcess(Timer timer) {
        try {
            LogJob log = jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(),
                    null);
            creazioneIndiceAipEjb.creazioneElenchiIndiceAip(log.getIdLogJob());
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
            logger.error("Errore nell'esecuzione del job di creazione elenchi indici AIP", e);
            timer.cancel();
        } catch (Exception e) {
            // questo log viene scritto solo in caso di errore.
            String message = "Errore nell'esecuzione del job di creazione elenchi indici AIP "
                    + ExceptionUtils.getRootCauseMessage(e);
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.ERRORE.name(), message);
            logger.error("Errore nell'esecuzione del job di creazione elenchi indici AIP", e);
            timer.cancel();
        }
    }
}
