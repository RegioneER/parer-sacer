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

package it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.OrgVCorrPing;

/**
 *
 * @author gpiccioli
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class CorrispondenzePingHelper extends GenericHelper {

    public List<OrgVCorrPing> retrieveOrgVCorrPingList(BigDecimal idStrut, BigDecimal idEnte, BigDecimal idAmbiente) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT corr FROM OrgVCorrPing corr WHERE idOrganizApplic IN (:idStrut, :idEnte, :idAmbiente)");

        // creazione query dalla stringa
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idStrut != null && idStrut.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        if (idEnte != null && idEnte.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        if (idAmbiente != null && idAmbiente.compareTo(BigDecimal.ZERO) != 0) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }

        // getEntityManager().unwrap(JpagetEntityManager().class)
        return query.getResultList();
    }

    public boolean checkPingRelations(long id, int type) {
        String queryStr = "SELECT tab FROM OrgVCorrPing tab WHERE idOrganizApplic = :id";

        switch (type) {
        case 0:
            queryStr += " AND ti_dich_vers = 'STRUTTURA'";
            break;
        case 1:
            queryStr += " AND ti_dich_vers = 'ENTE'";
            break;
        case 2:
            queryStr += " AND ti_dich_vers = 'AMBIENTE'";
            break;
        default:
            break;
        }

        Query query1 = getEntityManager().createQuery(queryStr);
        query1.setParameter("id", id);
        List<Object[]> list = query1.getResultList();

        return !list.isEmpty();
    }

}
