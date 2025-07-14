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

package it.eng.parer.serie.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import it.eng.parer.entity.DecModelloCampoInpUd;
import it.eng.parer.entity.DecModelloCampoOutSelUd;
import it.eng.parer.entity.DecModelloFiltroSelUdattb;
import it.eng.parer.entity.DecModelloFiltroTiDoc;
import it.eng.parer.entity.DecModelloOutSelUd;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.parer.entity.DecNotaModelloTipoSerie;
import it.eng.parer.entity.DecUsoModelloTipoSerie;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.util.Constants;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings({
	"unchecked" })
@Stateless
@LocalBean
public class ModelliSerieHelper extends GenericHelper {

    /**
     * Ottiene la lista di modelli abilitati per l'ambiente e non per la struttura
     *
     * @param idAmbiente  id ambiente
     * @param idStrut     id struttura
     * @param filterValid true per prendere i record attivi attualmente
     *
     * @return lista di modelli
     */
    public List<DecModelloTipoSerie> retrieveDecModelloTipoSerie(Long idAmbiente, Long idStrut,
	    boolean filterValid) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT m FROM DecModelloTipoSerie m WHERE m.orgAmbiente.idAmbiente = :idAmbiente ");
	if (idStrut != null) {
	    queryStr.append(
		    "AND NOT EXISTS (SELECT u FROM DecUsoModelloTipoSerie u JOIN u.decModelloTipoSerie mu WHERE mu.idModelloTipoSerie = m.idModelloTipoSerie AND u.orgStrut.idStrut != :idStrut) ");
	}
	if (filterValid) {
	    queryStr.append("AND m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate ");
	}
	queryStr.append("ORDER BY m.nmModelloTipoSerie");

	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idAmbiente", idAmbiente);
	if (idStrut != null) {
	    query.setParameter("idStrut", idStrut);
	}
	if (filterValid) {
	    query.setParameter("filterDate", Calendar.getInstance().getTime());
	}
	return query.getResultList();
    }

    /**
     * Ottiene la lista di modelli in uso per la struttura in input
     *
     * @param idStrut     id struttura
     * @param filterValid true per prendere i record attivi attualmente
     *
     * @return lista di modelli
     */
    public List<DecModelloTipoSerie> retrieveDecModelloTipoSerieFromDecUsoModello(
	    BigDecimal idStrut, boolean filterValid) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT m FROM DecUsoModelloTipoSerie u JOIN u.decModelloTipoSerie m WHERE u.orgStrut.idStrut = :idStrut ");
	if (filterValid) {
	    queryStr.append("AND m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate ");
	}
	queryStr.append("ORDER BY m.nmModelloTipoSerie");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	if (filterValid) {
	    query.setParameter("filterDate", Calendar.getInstance().getTime());
	}
	return query.getResultList();
    }

    /**
     * Ottiene il modello di tipo serie dati i dati di chiave unique
     *
     * @param nmModelloTipoSerie nome modello tipo serie
     * @param idAmbiente         id ambiente
     *
     * @return modello entity DecModelloTipoSerie
     */
    public DecModelloTipoSerie getDecModelloTipoSerie(String nmModelloTipoSerie,
	    BigDecimal idAmbiente) {
	Query query = getEntityManager().createQuery(
		"SELECT m FROM DecModelloTipoSerie m WHERE m.nmModelloTipoSerie = :nmModelloTipoSerie AND m.orgAmbiente.idAmbiente = :idAmbiente");
	query.setParameter("nmModelloTipoSerie", nmModelloTipoSerie);
	query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
	List<DecModelloTipoSerie> list = query.getResultList();
	DecModelloTipoSerie modello = null;
	if (!list.isEmpty()) {
	    modello = list.get(0);
	}
	return modello;
    }

    /**
     * Ottiene la lista di elementi di descrizione per il modello dato in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     *
     * @return lista di elementi di descrizione
     */
    public List<DecNotaModelloTipoSerie> retrieveDecNotaModelloTipoSerie(
	    BigDecimal idModelloTipoSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT n FROM DecNotaModelloTipoSerie n WHERE n.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie ORDER BY n.dtNotaTipoSerie DESC");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	return query.getResultList();
    }

    /**
     * Ritorna la lista di regole di acquisizione per il modello dato in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     *
     * @return lista di regole di acquisizione
     */
    public List<DecModelloCampoInpUd> retrieveDecModelloCampoInpUd(BigDecimal idModelloTipoSerie) {
	return retrieveDecModelloCampoInpUd(idModelloTipoSerie, null);
    }

    /**
     * Ritorna la lista di regole di acquisizione per il modello e il tipo di campo dati in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     * @param tiCampo            tipo campo
     *
     * @return lista di regole di acquisizione
     */
    public List<DecModelloCampoInpUd> retrieveDecModelloCampoInpUd(BigDecimal idModelloTipoSerie,
	    String tiCampo) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT c FROM DecModelloCampoInpUd c WHERE c.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie ");
	if (tiCampo != null) {
	    queryStr.append("AND c.tiCampo = :tiCampo ");
	}
	queryStr.append("ORDER BY c.tiCampo, c.pgOrdCampo");
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	if (tiCampo != null) {
	    query.setParameter("tiCampo", tiCampo);
	}
	return query.getResultList();
    }

    /**
     * Ritorna la regola di acquisizione per il modello, il tipo di campo e il nome del campo dati
     * in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     * @param tiCampo            tipo campo
     * @param nmCampo            nome campo
     *
     * @return lista di regole di acquisizione
     */
    public DecModelloCampoInpUd getDecModelloCampoInpUd(BigDecimal idModelloTipoSerie,
	    String tiCampo, String nmCampo) {
	Query query = getEntityManager().createQuery(
		"SELECT c FROM DecModelloCampoInpUd c WHERE c.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie AND c.tiCampo = :tiCampo AND c.nmCampo = :nmCampo ");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	query.setParameter("tiCampo", tiCampo);
	query.setParameter("nmCampo", nmCampo);
	List<DecModelloCampoInpUd> list = query.getResultList();
	if (!list.isEmpty()) {
	    return list.get(0);
	} else {
	    return null;
	}
    }

    /**
     * Elimina tutte le regole di acquisizione per il modello dato in input
     *
     * @param idModelloTipoSerie id modello serie
     */
    public void deleteAllDecModelloCampoInpUd(BigDecimal idModelloTipoSerie) {
	Query q = getEntityManager().createQuery(
		"DELETE FROM DecModelloCampoInpUd d WHERE d.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	q.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	q.executeUpdate();
    }

    /**
     * Ritorna la lista di regole di rappresentazione per il modello dato in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     *
     * @return lista di regole di rappresentazione
     */
    public List<DecModelloOutSelUd> retrieveDecModelloOutSelUd(BigDecimal idModelloTipoSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT o FROM DecModelloOutSelUd o WHERE o.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	return query.getResultList();
    }

    /**
     * Ritorna la lista di campi per la regola di rappresentazione e il tipo di campo dati in input
     *
     * @param idModelloOutSelUd id modello unita doc
     * @param tiCampo           tipo campo
     *
     * @return lista di campi della regola di rappresentazione
     */
    public List<DecModelloCampoOutSelUd> retrieveDecModelloCampoOutSelUd(
	    BigDecimal idModelloOutSelUd, String tiCampo) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT c FROM DecModelloCampoOutSelUd c WHERE c.decModelloOutSelUd.idModelloOutSelUd = :idModelloOutSelUd ");
	if (tiCampo != null) {
	    queryStr.append("AND c.tiCampo = :tiCampo ");
	}
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idModelloOutSelUd", longFromBigDecimal(idModelloOutSelUd));
	if (tiCampo != null) {
	    query.setParameter("tiCampo", tiCampo);
	}
	return query.getResultList();
    }

    /**
     * Elimina tutti i campi per la regola di rappresentazione data in input
     *
     * @param idModelloOutSelUd id modello unita doc
     */
    public void deleteAllDecModelloCampoOutSelUd(BigDecimal idModelloOutSelUd) {
	Query q = getEntityManager().createQuery(
		"DELETE FROM DecModelloCampoOutSelUd d WHERE d.decModelloOutSelUd.idModelloOutSelUd = :idModelloOutSelUd");
	q.setParameter("idModelloOutSelUd", longFromBigDecimal(idModelloOutSelUd));
	q.executeUpdate();
    }

    /**
     * Ritorna la lista di regole di filtraggio per i tipi documento principale per il modello dato
     * in input
     *
     * @param idModelloTipoSerie id modello
     *
     * @return la lista di regole
     */
    public List<DecModelloFiltroTiDoc> retrieveDecModelloFiltroTiDoc(
	    BigDecimal idModelloTipoSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT f FROM DecModelloFiltroTiDoc f WHERE f.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	return query.getResultList();
    }

    /**
     * Elimina tutte le regole di filtraggio per i tipi di documento principale per il modello dato
     * in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     */
    public void deleteAllDecModelloFiltroTiDoc(BigDecimal idModelloTipoSerie) {
	Query q = getEntityManager().createQuery(
		"DELETE FROM DecModelloFiltroTiDoc d WHERE d.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	q.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	q.executeUpdate();
    }

    /**
     * Ritorna la lista di filtri su dati specifici del modello dato in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     *
     * @return lista di filtri
     */
    public List<DecModelloFiltroSelUdattb> retrieveDecModelloFiltroSelUdattb(
	    BigDecimal idModelloTipoSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT f FROM DecModelloFiltroSelUdattb f WHERE f.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	return query.getResultList();
    }

    /**
     * Elimina tutti i filtri su dati specifici del modello dato in input
     *
     * @param idModelloTipoSerie id modello tipo serie
     */
    public void deleteAllDecModelloFiltroSelUdattb(BigDecimal idModelloTipoSerie) {
	Query q = getEntityManager().createQuery(
		"DELETE FROM DecModelloFiltroSelUdattb d WHERE d.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	q.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	q.executeUpdate();
    }

    /**
     * Ritorna la lista di associazioni del modello alle strutture
     *
     * @param idModelloTipoSerie id modello tipo serie
     *
     * @return lista oggetti di tipo {@link DecUsoModelloTipoSerie}
     */
    public List<DecUsoModelloTipoSerie> retrieveDecUsoModelloTipoSerie(
	    BigDecimal idModelloTipoSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT u FROM DecUsoModelloTipoSerie u WHERE u.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	return query.getResultList();
    }

    /**
     * Ritorna il massimo progressivo creato per il modello di tipo serie e il tipo di nota dati in
     * input
     *
     * @param idModelloTipoSerie id modello tipo serie
     * @param idTipoNotaSerie    id nota serie
     *
     * @return il massimo progressivo
     */
    public BigDecimal getMaxPgDecNotaModelloSerie(BigDecimal idModelloTipoSerie,
	    BigDecimal idTipoNotaSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT MAX(d.pgNotaTipoSerie) FROM DecNotaModelloTipoSerie d WHERE d.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie AND d.decTipoNotaSerie.idTipoNotaSerie = :idTipoNotaSerie ");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	query.setParameter("idTipoNotaSerie", longFromBigDecimal(idTipoNotaSerie));
	return (BigDecimal) query.getSingleResult();
    }

    public boolean existDecUsoModelloTipoSerie(BigDecimal idModelloTipoSerie, BigDecimal idStrut) {
	Query query = getEntityManager().createQuery(
		"SELECT d FROM DecUsoModelloTipoSerie d WHERE d.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie AND d.orgStrut.idStrut = :idStrut");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	query.setParameter("idStrut", longFromBigDecimal(idStrut));
	List<DecUsoModelloTipoSerie> list = query.getResultList();
	return !list.isEmpty();
    }

    public boolean existsRelationsWithModello(long idTipoDato, Constants.TipoDato tipoDato) {
	StringBuilder queryStr = new StringBuilder(
		"SELECT COUNT(modelloTipoSerie) FROM DecModelloTipoSerie modelloTipoSerie ");
	switch (tipoDato) {
	case TIPO_UNITA_DOC:
	    queryStr.append("WHERE modelloTipoSerie.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
	    break;
	case TIPO_DOC:
	    queryStr.append("WHERE modelloTipoSerie.decTipoDoc.idTipoDoc = :idTipoDato ");
	    break;
	default:
	    break;
	}
	Query query = getEntityManager().createQuery(queryStr.toString());
	query.setParameter("idTipoDato", idTipoDato);
	return (Long) query.getSingleResult() > 0;
    }

    public List<DecModelloFiltroTiDoc> getDecModelloFiltroTiDoc(BigDecimal idModelloTipoSerie) {
	Query query = getEntityManager().createQuery(
		"SELECT modelloFiltroTiDoc FROM DecModelloFiltroTiDoc modelloFiltroTiDoc "
			+ "WHERE modelloFiltroTiDoc.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
	query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
	return query.getResultList();
    }
}
