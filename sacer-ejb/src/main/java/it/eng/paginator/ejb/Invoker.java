package it.eng.paginator.ejb;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.sacerlog.common.SacerLogEjbType;
import it.eng.spagoLite.db.base.table.LazyListBean;

/**
 * Questa classe è responsabile dell'invocazione dinamica (lookup e esecuzione) di un metodo di un EJB
 *
 * @author Quaranta_M
 */
@Stateless
@EJB(name = "java:app/paginator/Invoker", beanInterface = it.eng.paginator.ejb.InvokerLocal.class)
public class Invoker implements InvokerLocal {

    @Resource
    private SessionContext context;
    Logger log = LoggerFactory.getLogger(Invoker.class);

    // @Resource
    // private TransactionSynchronizationRegistry transactionCtx;

    /**
     * 
     * @param <T>
     *            Tipo generico dell'EJB su cui invocare il metodo
     * @param helperEJB
     *            EJB su cui invocare il metodo
     * @param method
     *            Metodo da invocare
     * @param parameterValue
     *            Valori dei parametri da passare al metodo
     * 
     * @return Risultato dell'esecuzione del metodo
     */
    public <T> Object invokeEJBMethod(Class<T> helperEJB, Method method, Object[] parameterValue) {
        /*
         * Se l'ejb da paginare è annotato con @SacerLogEjbType allora il lookup verrà effettuato nel modulo ejb
         * "sacerlog-ejb"
         */
        T helper = null;
        if (helperEJB.isAnnotationPresent(SacerLogEjbType.class)) {
            helper = (T) context.lookup("java:app/sacerlog-ejb/" + helperEJB.getSimpleName());
        } else {
            helper = (T) context.lookup("java:module/" + helperEJB.getSimpleName());
        }
        try {
            return method.invoke(helper, parameterValue);
        } catch (IllegalAccessException ex) {
            log.error("Visibilità del metodo " + method + " dell'helper " + helperEJB.getName() + " non corretta ", ex);
        } catch (IllegalArgumentException ex) {
            log.error(
                    "Argomenti passati al metodo " + method + " dell'helper " + helperEJB.getName() + " non corretti ",
                    ex);
        } catch (InvocationTargetException ex) {
            log.error("L'invocazione al metodo " + method + " dell'helper " + helperEJB.getName()
                    + " ha prodotto un'eccezione. Segue stacktrace..", ex);
        } catch (SecurityException ex) {
            log.error("Errore {}", ex);
        }
        return null;
    }

    @Override
    public <T> Object invokeLazyPagination(Class<T> helperEJB, Method method, Object[] parameterValue,
            LazyListBean llBean) {
        if (llBean != null) {
            llBean.setQueryAlreadyExecuted(false);
            PaginatorInterceptor.setLazyListBean(llBean);
        }
        return this.invokeEJBMethod(helperEJB, method, parameterValue);
    }
}
