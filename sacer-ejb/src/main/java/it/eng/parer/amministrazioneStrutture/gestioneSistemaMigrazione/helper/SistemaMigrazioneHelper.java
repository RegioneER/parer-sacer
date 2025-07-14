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

package it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.AplSistemaMigraz;
import it.eng.parer.entity.OrgUsoSistemaMigraz;
import it.eng.parer.helper.GenericHelper;

/**
 * Helper di gestione del sistema di migrazione
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")

@Stateless
@LocalBean
public class SistemaMigrazioneHelper extends GenericHelper {

    /**
     * Ritorna la lista di sistemi di migrazione per l'applicazione
     *
     * @param idStrut id struttura
     *
     * @return restituisce lista di elementi di tipo AplSistemaMigraz
     */
    public List<AplSistemaMigraz> retrieveAplSistemaMigraz(BigDecimal idStrut) {
	Query query = getEntityManager().createQuery(
		"SELECT apl FROM AplSistemaMigraz apl WHERE NOT EXISTS (SELECT orgUso FROM OrgUsoSistemaMigraz orgUso WHERE orgUso.orgStrut.idStrut = :idStrut AND orgUso.aplSistemaMigraz = apl)");
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	return query.getResultList();
    }

    /**
     * Ritorna la lista di sistemi di migrazione per la struttura
     *
     * @param idStrut id struttura
     *
     * @return restituisce lista di elementi di tipo OrgUsoSistemaMigraz
     */
    public List<OrgUsoSistemaMigraz> retrieveOrgUsoSistemaMigraz(BigDecimal idStrut) {
	Query query = getEntityManager().createQuery(
		"SELECT orgUso FROM OrgUsoSistemaMigraz orgUso WHERE orgUso.orgStrut.idStrut = :idStrut");
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	return query.getResultList();
    }

    public List<String> retrieveNmSistemaMigraz(BigDecimal idStrut) {
	Query query = getEntityManager().createQuery(
		"SELECT DISTINCT v.nmSistemaMigraz FROM OrgUsoSistemaMigraz u JOIN u.aplSistemaMigraz v WHERE u.orgStrut.idStrut = :idStrutturain AND v.nmSistemaMigraz IS NOT NULL ORDER BY v.nmSistemaMigraz");
	query.setParameter("idStrutturain", longFromBigDecimal(idStrut));

	return query.getResultList();
    }

    public List<AplSistemaMigraz> retrieveAplSistemaMigraz(String nmSistemaMigraz,
	    String dsSistemaMigraz) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT sistemaMigraz FROM AplSistemaMigraz sistemaMigraz ");
	String whereWord = " WHERE ";

	if (nmSistemaMigraz != null) {
	    queryStr.append(whereWord)
		    .append("UPPER(sistemaMigraz.nmSistemaMigraz) LIKE :nmSistemaMigraz ");
	    whereWord = "AND ";
	}
	if (dsSistemaMigraz != null) {
	    queryStr.append(whereWord)
		    .append("UPPER(sistemaMigraz.dsSistemaMigraz) LIKE :dsSistemaMigraz ");
	}

	queryStr.append("ORDER BY sistemaMigraz.nmSistemaMigraz");

	Query query = getEntityManager().createQuery(queryStr.toString());

	if (nmSistemaMigraz != null) {
	    query.setParameter("nmSistemaMigraz", "%" + nmSistemaMigraz.toUpperCase() + "%");
	}
	if (dsSistemaMigraz != null) {
	    query.setParameter("dsSistemaMigraz", "%" + dsSistemaMigraz.toUpperCase() + "%");
	}

	return query.getResultList();
    }

    public AplSistemaMigraz getAplSistemaMigrazByName(String nmSistemaMigraz) {
	String queryStr = "SELECT sistemaMigraz FROM AplSistemaMigraz sistemaMigraz "
		+ "WHERE sistemaMigraz.nmSistemaMigraz = :nmSistemaMigraz ";

	Query query = getEntityManager().createQuery(queryStr);
	query.setParameter("nmSistemaMigraz", nmSistemaMigraz);

	List<AplSistemaMigraz> lista = query.getResultList();
	if (lista != null && !lista.isEmpty()) {
	    return lista.get(0);
	}
	return null;
    }

    /**
     * Ritorna l'oggetto sistema di migrazione se esiste
     *
     * @param nmSistemaMigraz nome sistema migrazione
     *
     * @return l'entity richiesta se esiste, oppure <code>null</code>
     */
    public AplSistemaMigraz getAplSistemaMigraz(String nmSistemaMigraz) {
	AplSistemaMigraz sistemaMigraz = null;
	if (StringUtils.isNotBlank(nmSistemaMigraz)) {
	    Query query = getEntityManager().createQuery(
		    "SELECT sistemaMigraz FROM AplSistemaMigraz sistemaMigraz WHERE sistemaMigraz.nmSistemaMigraz = :nmSistemaMigraz");
	    query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
	    List<AplSistemaMigraz> list = query.getResultList();
	    if (!list.isEmpty()) {
		sistemaMigraz = list.get(0);
	    }
	} else {
	    throw new IllegalArgumentException("Parametro nmSistemaMigraz nullo");
	}
	return sistemaMigraz;
    }

    /**
     * Ritorna l'informazione circa l'appartenenza di un sistema di migrazione ad una qualunque
     * struttura
     *
     * @param idSistemaMigraz id sistema migrazione
     *
     * @return true/false
     */
    public boolean existsOrgUsoSistemaMigraz(BigDecimal idSistemaMigraz) {
	Query query = getEntityManager().createQuery(
		"SELECT COUNT(orgUso) FROM OrgUsoSistemaMigraz orgUso WHERE orgUso.aplSistemaMigraz.idSistemaMigraz = :idSistemaMigraz");
	query.setParameter("idSistemaMigraz", longFromBigDecimal(idSistemaMigraz));
	return (Long) query.getSingleResult() > 0;
    }
}
