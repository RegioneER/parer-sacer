package prova;

import it.eng.parer.jboss.timer.common.JbossJobTimer;
import it.eng.parer.jboss.timer.common.JobTable;
import it.eng.parer.jboss.timer.exception.TimerNotFoundException;
import it.eng.parer.jboss.timer.helper.AbstractJbossTimerHelper;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;

@Stateless(name = "timerHelper")
public class DummyTimerHelper extends AbstractJbossTimerHelper {

    @Override
    public String getApplicationName() {
        return null;
    }

    @Override
    public JobTable getJob(String jobName) throws TimerNotFoundException {
        return null;
    }

    @Override
    public List<JobTable> getJobs() {
        return null;
    }

    @Override
    public JbossJobTimer getTimer(String jobName) throws TimerNotFoundException {
        return null;
    }

    @Override
    public Set<String> getApplicationTimerNames() {
        return null;
    }

}
