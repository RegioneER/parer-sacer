package it.eng.parer.async.aop;

import it.eng.parer.async.helper.AsyncHelper;
import it.eng.parer.exception.AsyncException;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.utils.JobConstants;
import javax.ejb.EJB;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 *
 * @author Bonora_L
 */
public class LockInterceptor {

    @EJB
    private AsyncHelper asyncHelper;

    @AroundInvoke
    public Object catchException(InvocationContext inv) throws Exception {
        try {
            Object obj = inv.proceed();
            return obj;
        } catch (ParerInternalError ie) {
            String message = null;
            Long idLock = null;
            Long idStrut = null;
            String task = null;
            Exception nativeExcp = ie.getNativeException();
            if (nativeExcp != null) {
                message = nativeExcp.getMessage();
                if (nativeExcp instanceof AsyncException) {
                    idLock = ((AsyncException) nativeExcp).getIdLock();
                    task = ((AsyncException) nativeExcp).getAsyncTask();
                    idStrut = ((AsyncException) nativeExcp).getIdStrut();
                }
            }
            if (ie.getCause() != null) {
                message = ie.getCause().getMessage();
            }
            if (message == null) {
                message = ie.getDescription();
            }
            if (message.length() > 1024) {
                message = message.substring(0, 1024);
            }

            if (idLock != null && task != null) {
                asyncHelper.writeEndLogLock(idLock, task, JobConstants.OpTypeEnum.ERRORE.name(), message, idStrut);
            }
            throw ie;
        }
    }

}
