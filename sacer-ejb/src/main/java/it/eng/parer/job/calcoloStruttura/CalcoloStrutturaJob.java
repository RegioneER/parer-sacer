package it.eng.parer.job.calcoloStruttura;

import it.eng.parer.async.ejb.CalcoloMonitoraggioAsync;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
