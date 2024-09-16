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

package it.eng.parer.fascicoli.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.web.util.Transform;

@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CriteriRaggrFascicoliHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(CriteriRaggrFascicoliHelper.class.getName());

    @SuppressWarnings("unchecked")
    public List<DecCriterioRaggrFasc> retrieveDecCriterioRaggrFascList(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggrFasc u ");
        String whereWord = "WHERE ";
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
            whereWord = "AND ";
        }
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.orgStrut.orgEnte.idEnte = :idEnte ");
            whereWord = "AND ";
        }
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr");
        }
        queryStr.append(" ORDER BY u.nmCriterioRaggr ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();
    }

    public List<DecCriterioRaggrFasc> retrieveDecCriterioRaggrFascList(BigDecimal idStruttura) {
        return retrieveDecCriterioRaggrFascList(null, null, idStruttura, null);
    }

    public OrgStrutRowBean getOrgStrutById(BigDecimal idStrut) {
        OrgStrutRowBean strutRB = new OrgStrutRowBean();
        try {
            strutRB = (OrgStrutRowBean) Transform
                    .entity2RowBean(getEntityManager().find(OrgStrut.class, idStrut.longValue()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return strutRB;
    }

    public DecCriterioRaggrFasc getDecCriterioRaggrFascByStrutturaCorrenteAndCriterio(BigDecimal idStrutCorrente,
            String nmCriterioRaggr) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecCriterioRaggrFasc u ");
        String whereWord = "WHERE ";

        if (idStrutCorrente != null) {
            queryStr.append(whereWord).append("u.orgStrut.idStrut = :idStrutCorrente ");
            whereWord = "AND ";
        }

        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrutCorrente != null) {
            query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        }

        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }

        List<DecCriterioRaggrFasc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

}
