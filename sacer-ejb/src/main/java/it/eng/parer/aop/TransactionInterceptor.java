package it.eng.parer.aop;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionInterceptor {

    @Resource
    SessionContext ctx;

    @AroundInvoke
    public Object catchException(InvocationContext inv) throws Exception {
        Logger logger = LoggerFactory.getLogger(inv.getTarget().getClass());
        try {
            Object obj = inv.proceed();
            return obj;
        } catch (ParerUserError ue) {
            logger.error("ParerUserError nel metodo " + inv.getMethod().getName() + ": " + ue.getDescription());
            ctx.setRollbackOnly();
            throw ue;
        } catch (ParerInternalError ie) {
            logger.error("ParerInternalError nel metodo " + inv.getMethod().getName() + ": " + ie.getMessage());
            ctx.setRollbackOnly();
            throw ie;
        } catch (ParerWarningException uw) {
            logger.error("ParerWarningException nel metodo " + inv.getMethod().getName() + ": " + uw.getDescription());
            throw uw;
        } catch (Exception e) {
            logger.error(
                    "Exception nel metodo " + inv.getMethod().getName() + ": " + ExceptionUtils.getRootCauseMessage(e),
                    e);
            ctx.setRollbackOnly();
            throw e;
        }
    }
}
