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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.ElencoEnums.OpTypeEnum;
import it.eng.parer.entity.LogJob;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.codaIndiceAip.ejb.CodaIndiciAipUdDaElabEjb;
import it.eng.parer.job.utils.JobConstants;

/**
 *
 * @author DiLorenzo_F
 */
@Singleton(mappedName = "ProducerCodaIndiciAipDaElabTimer")
@LocalBean
@Lock(LockType.READ)
public class ProducerCodaIndiciAipDaElabTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(ProducerCodaIndiciAipDaElabTimer.class);

    @EJB
    private CodaIndiciAipUdDaElabEjb codaIndiciAipUdDaElabEjb;
    @EJB
    private ProducerCodaIndiciAipDaElabTimer thisTimer;

    public ProducerCodaIndiciAipDaElabTimer() {
        super(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name());
        logger.debug("{} creato", ProducerCodaIndiciAipDaElabTimer.class.getName());
    }

    @Override
    @Lock(LockType.WRITE)
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
    @Lock(LockType.WRITE)
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
            logger.info("Lancio il timer ProducerCodaIndiciAipDaElabTimer...");
            timerService.createCalendarTimer(tmpScheduleExpression, new TimerConfig(jobName, false));
        }
    }

    @Override
    @Lock(LockType.WRITE)
    public void stop(String applicationName) {
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
                logger.error("Errore nell'esecuzione del job di producer coda indici aip da elaborare", e);
            }
        }
    }

    @Override
    public void startProcess(Timer timer) throws Exception {
        logger.info("Job automatico per il producer coda indici aip da elaborare avviato");
        LogJob logJob = jobHelper.writeAtomicLogJob(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(),
                OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
        try {
            codaIndiciAipUdDaElabEjb.codaIndiciAipUdDaElab();
        } catch (ParerUserError ue) {
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(),
                    OpTypeEnum.ERRORE.name(), ue.getDescription());
            logger.error("Errore nell'esecuzione del job di producer coda indici aip da elaborare", ue);
            logger.info("Timer cancellato");
            timer.cancel();
        } catch (Exception e) {
            String message = null;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            jobHelper.writeAtomicLogJob(OpTypeEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(), OpTypeEnum.ERRORE.name(),
                    message);
            logger.error("Errore nell'esecuzione del job di producer coda indici aip da elaborare", e);
            logger.info("Timer cancellato");
            timer.cancel();
        }
    }
}
