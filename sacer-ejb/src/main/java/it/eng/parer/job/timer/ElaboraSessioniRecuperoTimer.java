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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.job.tpi.ejb.ElaboraSessioniRecuperoJob;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliTpi;

@Singleton(mappedName = "ElaboraSessioniRecuperoTimer")
@LocalBean
@Lock(LockType.READ)
public class ElaboraSessioniRecuperoTimer extends JobTimer {

    private Logger logger = LoggerFactory.getLogger(ElaboraSessioniRecuperoTimer.class);
    @EJB
    private ElaboraSessioniRecuperoTimer thisTimer;
    @EJB
    private ControlliTpi controlliTpi;
    @EJB
    private ElaboraSessioniRecuperoJob elabRecEjb;
    // private final String NAME_JOB_ELABORA_SESSIONI_REC = "IntervalloElaboraSessioniRecupero";

    public ElaboraSessioniRecuperoTimer() {
        super(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name());
        logger.debug(ElaboraSessioniRecuperoTimer.class.getName() + " creato");
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
            logger.info("Lancio il timer ElaboraSessioniRecuperoTimer...");
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
        try {
            jobHelper.writeAtomicLogJob(jobName,
                    JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
            /*
             * se il TPI non è stato installato, vuol dire che tutta la gestione asincrona del
             * versamento basata su TIVOLI è inutilizabile, compresi i job.
             */
            RispostaControlli rispostaControlli = controlliTpi.verificaAbilitazioneTpi();
            if (rispostaControlli.isrBoolean()) { // il TPI è abilitato, esegue il job
                elabRecEjb.elaboraSessioniRecupero();
            } else if (rispostaControlli.getCodErr() != null) {
                // si è verificato un errore leggendo le property, ferma tutto e logga l'errore.
                throw new ParerInternalError(rispostaControlli.getDsErr());
            } else { // il TPI non è abilitato. Ferma tutto e logga l'errore.
                throw new ParerInternalError("Il job non può essere avviato se TPI_Enable=false");
            }
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
            logger.error("Errore nell'esecuzione del job di elabora sessioni di recupero", e);
            timer.cancel();
        } catch (Exception e) {
            // questo log viene scritto solo in caso di errore.
            String message = null;
            if (e.getCause() != null) {
                message = e.getCause().getMessage();
            }
            jobHelper.writeAtomicLogJob(jobName, JobConstants.OpTypeEnum.ERRORE.name(), message);
            logger.error("Errore nell'esecuzione del job di elabora sessioni di recupero", e);
            timer.cancel();
        }
    }
}
