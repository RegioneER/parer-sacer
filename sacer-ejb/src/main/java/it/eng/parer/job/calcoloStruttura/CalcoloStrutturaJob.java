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

package it.eng.parer.job.calcoloStruttura;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.async.ejb.CalcoloMonitoraggioAsync;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloStrutturaJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalcoloStrutturaJob.class);
    @EJB
    private JobHelper jobHelper;
    @EJB
    private CalcoloMonitoraggioAsync calcoloAsync;

    public void calcolaStruttura() throws ParerInternalError {
        boolean success = calcoloAsync.calcolaStruttura();
        LOGGER.info(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name() + " --- Fine schedulazione job");
        if (success) {
            jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
        } else {
            throw new ParerInternalError("Impossibile acquisire il lock");
        }
    }
}
