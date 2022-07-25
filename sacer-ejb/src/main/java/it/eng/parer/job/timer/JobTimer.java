package it.eng.parer.job.timer;

import it.eng.parer.jboss.timer.common.CronSchedule;
import it.eng.parer.jboss.timer.common.JbossJobTimer;
import it.eng.parer.job.helper.JobHelper;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Timer;
import javax.ejb.TimerService;

/**
 *
 * @author Moretti_Lu
 */
@Lock(LockType.READ)
public abstract class JobTimer implements JbossJobTimer {

    protected static final int TIME_DURATION = 2000;

    protected final String jobName;
    @Resource
    protected TimerService timerService;
    @EJB(mappedName = "java:app/Parer-ejb/JobHelper")
    protected JobHelper jobHelper;

    /**
     *
     * @param jobName
     *            Job name
     */
    protected JobTimer(String jobName) {
        if (jobName == null || jobName.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.jobName = jobName;
    }

    @Override
    public String getJobName() {
        return jobName;
    }

    /**
     * Returns the date of the job next elaboration.
     *
     * @return Date
     */
    @Override
    public Date getNextElaboration(String applicationName) {
        for (Object obj : timerService.getTimers()) {
            Timer timer = (Timer) obj;
            String scheduled = (String) timer.getInfo();

            if (scheduled.equals(jobName)) {
                return timer.getNextTimeout();
            }
        }

        return null;
    }

    /**
     * This method is invoked by <code>doJob</code> and invokes the job business logic.
     *
     * @param timer
     *            entity Timer
     *
     * @throws Exception
     *             errore generico
     */
    public abstract void startProcess(Timer timer) throws Exception;

    @Override
    public abstract void startCronScheduled(CronSchedule sched, String applicationName);

    @Override
    public abstract void startSingleAction(String applicationName);

    @Override
    public abstract void stop(String applicationName);
}
