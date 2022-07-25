package it.eng.parer.web.util;

import it.eng.parer.serie.utils.FutureUtils;
import java.util.Map;
import java.util.concurrent.Future;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author bonora_l
 */
public class SerieSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    /**
     * Invalida a fine della sessione la mappa di future utilizzata per la creazione delle serie
     *
     * @param se
     *            sessione {@link HttpSessionEvent}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        Map<String, Map<String, Future<?>>> futureMap = (Map<String, Map<String, Future<?>>>) se.getSession()
                .getServletContext().getAttribute(FutureUtils.PARAMETER_FUTURE_SERIE);
        if (futureMap != null) {
            futureMap.remove(se.getSession().getId());
        }
    }

}
