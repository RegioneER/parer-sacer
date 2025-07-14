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

package it.eng.parer.fascicoli.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.fascicoli.helper.CriteriRaggrFascicoliHelper;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascTableBean;
import it.eng.parer.web.util.Transform;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({
	TransactionInterceptor.class })
public class CriteriRaggrFascicoliEjb {

    @EJB
    private CriteriRaggrFascicoliHelper crHelper;

    private static final Logger logger = LoggerFactory.getLogger(CriteriRaggrFascicoliEjb.class);

    public DecCriterioRaggrFascTableBean getDecCriterioRaggrFascTableBean(BigDecimal idAmbiente,
	    BigDecimal idEnte, BigDecimal idStrut, String nmCriterioRaggr) {
	DecCriterioRaggrFascTableBean table = new DecCriterioRaggrFascTableBean();
	List<DecCriterioRaggrFasc> list = crHelper.retrieveDecCriterioRaggrFascList(idAmbiente,
		idEnte, idStrut, nmCriterioRaggr);
	if (list != null && !list.isEmpty()) {
	    try {
		table = (DecCriterioRaggrFascTableBean) Transform.entities2TableBean(list);
	    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
		    | IllegalAccessException | IllegalArgumentException
		    | InvocationTargetException ex) {
		logger.error("Errore durante il recupero dei criteri di raggruppamento fascicoli "
			+ ExceptionUtils.getRootCauseMessage(ex), ex);
		throw new IllegalStateException(
			"Errore durante il recupero dei criteri di raggruppamento fascicoli");
	    }
	}
	return table;
    }
}
