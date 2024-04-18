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
