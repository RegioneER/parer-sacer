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

package it.eng.parer.web.util;

import it.eng.parer.serie.utils.FutureUtils;
import java.util.Map;
import java.util.concurrent.Future;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 *
 * @author bonora_l
 */
@WebListener
public class SerieSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    /**
     * Invalida a fine della sessione la mappa di future utilizzata per la creazione delle serie
     *
     * @param se sessione {@link HttpSessionEvent}
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
	Map<String, Map<String, Future<?>>> futureMap = (Map<String, Map<String, Future<?>>>) se
		.getSession().getServletContext().getAttribute(FutureUtils.PARAMETER_FUTURE_SERIE);
	if (futureMap != null) {
	    futureMap.remove(se.getSession().getId());
	}
    }

}
