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

package it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper;

import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecAttribFascicolo;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecErrAaTipoFascicolo;
import it.eng.parer.entity.DecModelloXsdAttribFascicolo;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecParteNumeroFascicolo;
import it.eng.parer.entity.DecSelCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecUsoModelloXsdFasc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;
import org.springframework.util.StringUtils;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiUsoModelloXsd;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd;
import java.util.stream.Collectors;

/**
 *
 * @author gilioli_p
 */
@Stateless
@LocalBean
public class TipoFascicoloHelper extends GenericHelper {

    public List<DecTipoFascicolo> getDecTipoFascicoloList(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo ");
        String whereClause = " WHERE ";
        if (idStrut != null) {
            queryStr.append(whereClause).append("tipoFascicolo.orgStrut.idStrut = :idStrut ");
            whereClause = " AND ";
        }
        if (filterValid) {
            queryStr.append(whereClause)
                    .append("tipoFascicolo.dtIstituz <= :filterDate AND tipoFascicolo.dtSoppres >= :filterDate ");
        }

        queryStr.append("ORDER BY tipoFascicolo.nmTipoFascicolo ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        return query.getResultList();
    }

    public List<DecTipoFascicolo> getTipiFascicoloAbilitati(long idUtente, BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo, IamAbilTipoDato iatd "
                        + "WHERE iatd.idTipoDatoApplic = tipoFascicolo.idTipoFascicolo "
                        + "AND iatd.nmClasseTipoDato = '" + Constants.TipoDato.TIPO_FASCICOLO + "' "
                        + "AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");
        if (idStrut != null) {
            queryStr.append("AND tipoFascicolo.orgStrut.idStrut = :idStrut ");
        }
        if (filterValid) {
            queryStr.append("AND tipoFascicolo.dtIstituz <= :filterDate AND tipoFascicolo.dtSoppres >= :filterDate ");
        }

        queryStr.append("ORDER BY tipoFascicolo.nmTipoFascicolo ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        return query.getResultList();
    }

    /*
     * Torna true se l'utente per una determinata struttura è abilitato al tipo fascicolo
     */
    public boolean isTipoFascicoloAbilitato(long idUtente, BigDecimal idStrut, BigDecimal idTipoFascicolo,
            boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo, IamAbilTipoDato iatd "
                        + "WHERE iatd.idTipoDatoApplic = tipoFascicolo.idTipoFascicolo "
                        + "AND iatd.nmClasseTipoDato = '" + Constants.TipoDato.TIPO_FASCICOLO + "' "
                        + "AND tipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
                        + "AND iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente ");
        if (idStrut != null) {
            queryStr.append("AND tipoFascicolo.orgStrut.idStrut = :idStrut ");
        }
        if (filterValid) {
            queryStr.append("AND tipoFascicolo.dtIstituz <= :filterDate AND tipoFascicolo.dtSoppres >= :filterDate ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        List<DecTipoFascicolo> list = query.getResultList();
        return (list != null && !list.isEmpty());
    }

    public DecAaTipoFascicolo getLastDecAaTipoFascicolo(BigDecimal idTipoFascicolo) {
        if (idTipoFascicolo != null) {
            Query query = getEntityManager()
                    .createQuery("SELECT aaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
                            + "WHERE aaTipoFascicolo.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
                            + "ORDER BY aaTipoFascicolo.aaIniTipoFascicolo DESC ");
            query.setParameter("idTipoFascicolo", idTipoFascicolo.longValue());
            List<DecAaTipoFascicolo> aaTipoFascicoloList = query.getResultList();
            if (!aaTipoFascicoloList.isEmpty()) {
                return aaTipoFascicoloList.get(0);
            }
            return null;
        } else {
            throw new IllegalArgumentException("Parametro idTipoFascicolo nullo");
        }
    }

    public List<DecAaTipoFascicolo> getDecAaTipoFascicoloList(BigDecimal idTipoFascicolo) {
        StringBuilder queryStr = new StringBuilder("SELECT aaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo ");
        String whereClause = " WHERE ";
        if (idTipoFascicolo != null) {
            queryStr.append(whereClause).append("aaTipoFascicolo.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo ");
        }
        queryStr.append("ORDER BY aaTipoFascicolo.aaIniTipoFascicolo DESC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
        }
        return query.getResultList();
    }

    public List<DecCriterioRaggrFasc> getDecCriterioRaggrFascList(BigDecimal idTipoFascicolo) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT critRaggrFasc FROM DecSelCriterioRaggrFasc selCritRaggrFasc JOIN selCritRaggrFasc.decCriterioRaggrFasc critRaggrFasc ");
        String whereClause = " WHERE ";
        if (idTipoFascicolo != null) {
            queryStr.append(whereClause)
                    .append("selCritRaggrFasc.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo ");
            whereClause = " AND ";
        }
        queryStr.append(whereClause).append("critRaggrFasc.flFiltroTipoFascicolo = '1' ");
        queryStr.append("ORDER BY critRaggrFasc.dtIstituz DESC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
        }
        return query.getResultList();
    }

    public boolean existsDecTipoFascicoloCaseInsensitive(BigDecimal idStrut, String nmTipoFascicolo) {
        if (idStrut != null && !StringUtils.isEmpty(nmTipoFascicolo)) {
            Query query = getEntityManager().createQuery(
                    "SELECT COUNT(tipoFascicolo) FROM DecTipoFascicolo tipoFascicolo WHERE tipoFascicolo.orgStrut.idStrut = :idStrut AND UPPER(tipoFascicolo.nmTipoFascicolo) = :nmTipoFascicolo ");
            query.setParameter("idStrut", idStrut.longValue());
            query.setParameter("nmTipoFascicolo", nmTipoFascicolo.toUpperCase());
            return (Long) query.getSingleResult() > 0L;
        } else {
            throw new IllegalArgumentException("Identificativo struttura e/o nome fascicolo nulli");
        }
    }

    public boolean existsDecCriterioRaggrFascStandard(BigDecimal idStrut, BigDecimal idTipoFascicolo) {
        if (idStrut != null && idTipoFascicolo != null) {
            Query query = getEntityManager().createQuery(
                    "SELECT COUNT(selCritRaggrFasc) FROM DecSelCriterioRaggrFasc selCritRaggrFasc WHERE selCritRaggrFasc.decCriterioRaggrFasc.orgStrut.idStrut = :idStrut "
                            + "AND selCritRaggrFasc.decCriterioRaggrFasc.flCriterioRaggrStandard = '1' "
                            + "AND selCritRaggrFasc.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo ");
            query.setParameter("idStrut", idStrut.longValue());
            query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
            return (Long) query.getSingleResult() > 0L;
        } else {
            throw new IllegalArgumentException("Identificativo struttura e/o id tipo fascicolo nulli");
        }
    }

    public List<DecModelloXsdFascicolo> retrieveDecModelloXsdFascicolo(BigDecimal idAmbiente, Date data,
            String flDefault, String tiUsoModelloXsd) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT modelloXsdFascicolo FROM DecModelloXsdFascicolo modelloXsdFascicolo ");
        String whereClause = " WHERE ";
        if (idAmbiente != null) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.orgAmbiente.idAmbiente = :idAmbiente ");
            whereClause = " AND ";
        }
        if (data != null) {
            queryStr.append(whereClause)
                    .append("modelloXsdFascicolo.dtIstituz <= :data AND modelloXsdFascicolo.dtSoppres >= :data ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(flDefault)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.flDefault = :flDefault ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.tiUsoModelloXsd = :tiUsoModelloXsd ");
            whereClause = " AND ";
        }
        queryStr.append(whereClause).append("modelloXsdFascicolo.tiModelloXsd IN( :tiModelloXsd )");

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente.longValue());
        }
        if (data != null) {
            query.setParameter("data", data);
        }
        if (!StringUtils.isEmpty(flDefault)) {
            query.setParameter("flDefault", flDefault);
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            query.setParameter("tiUsoModelloXsd", TiUsoModelloXsd.valueOf(tiUsoModelloXsd));
        }
        final List<it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd> tiModelloXsd = Arrays
                .stream(CostantiDB.TiModelloXsdProfilo.values())
                .map(c -> it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.valueOf(c.name()))
                .collect(Collectors.toList());
        if (tiModelloXsd != null && !tiModelloXsd.isEmpty()) {
            query.setParameter("tiModelloXsd", tiModelloXsd);

        }
        return query.getResultList();
    }

    public List<DecParteNumeroFascicolo> getDecParteNumeroFascicoloList(BigDecimal idAaTipoFascicolo) {
        String queryStr = "SELECT parteNumeroFascicolo FROM DecParteNumeroFascicolo parteNumeroFascicolo "
                + "WHERE parteNumeroFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                + "ORDER BY parteNumeroFascicolo.niParteNumero ASC ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo.longValue());
        return query.getResultList();
    }

    public String getDecVChkFmtNumeroFascForPeriodo(BigDecimal idAaTipoFascicolo) {
        String queryStr = "SELECT check.flFmtNumeroOk FROM DecVChkFmtNumeroFasc check "
                + "WHERE check.idAaTipoFascicolo = :idAaTipoFascicolo ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        String risultato;
        risultato = (String) query.getSingleResult();
        if (risultato != null) {
            return risultato;
        } else {
            return "2";
        }
    }

    public DecErrAaTipoFascicolo getDecErrAaTipoFascicolo(BigDecimal idAaTipoFascicolo, Integer aaFascicolo) {
        if (idAaTipoFascicolo != null && aaFascicolo != null) {
            String queryStr = "SELECT errAaTipoFascicolo FROM DecErrAaTipoFascicolo errAaTipoFascicolo "
                    + "WHERE errAaTipoFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                    + "AND errAaTipoFascicolo.aaFascicolo = :aaFascicolo ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idAaTipoFascicolo", longFromBigDecimal(idAaTipoFascicolo));
            query.setParameter("aaFascicolo", bigDecimalFromInteger(aaFascicolo));
            List<DecErrAaTipoFascicolo> list = query.getResultList();
            if (!list.isEmpty()) {
                return list.get(0);
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("Parametri idAaTipoFascicolo e/o aaFascicolo nulli");
        }
    }

    public List<DecUsoModelloXsdFasc> getDecUsoModelloXsdFascList(BigDecimal idAaTipoFascicolo,
            BigDecimal idModelloXsdFascicolo, String flStandard) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT usoModelloXsdFasc FROM DecUsoModelloXsdFasc usoModelloXsdFasc ");
        String whereWord = " WHERE ";
        if (idAaTipoFascicolo != null) {
            queryStr.append(whereWord)
                    .append("usoModelloXsdFasc.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo ");
            whereWord = " AND ";
        }
        if (idModelloXsdFascicolo != null) {
            queryStr.append(whereWord)
                    .append("usoModelloXsdFasc.decModelloXsdFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo ");
            whereWord = " AND ";
        }
        if (flStandard != null) {
            queryStr.append(whereWord).append("usoModelloXsdFasc.flStandard = :flStandard ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idAaTipoFascicolo != null) {
            query.setParameter("idAaTipoFascicolo", longFromBigDecimal(idAaTipoFascicolo));
        }
        if (idModelloXsdFascicolo != null) {
            query.setParameter("idModelloXsdFascicolo", longFromBigDecimal(idModelloXsdFascicolo));
        }
        if (flStandard != null) {
            query.setParameter("flStandard", flStandard);
        }

        return query.getResultList();
    }

    public boolean existPeriodiValiditaSovrappostiFascicoli(BigDecimal idAaTipoFascicoloExcluded,
            BigDecimal idTipoFascicolo, BigDecimal aaIniTipoFascicolo, BigDecimal aaFinTipoFascicolo) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo "
                + "WHERE tipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
                + "AND EXISTS (SELECT aaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
                + "WHERE aaTipoFascicolo.decTipoFascicolo = tipoFascicolo "
                + "AND aaTipoFascicolo.aaIniTipoFascicolo <= :aaFinTipoFascicolo "
                + "AND (aaTipoFascicolo.aaFinTipoFascicolo IS NULL OR aaTipoFascicolo.aaFinTipoFascicolo >= :aaIniTipoFascicolo)");

        // Escludo il fascicolo che sto trattando
        if (idAaTipoFascicoloExcluded != null) {
            queryStr.append(" AND aaTipoFascicolo.idAaTipoFascicolo != :idAaTipoFascicoloExcluded )");
        } else {
            queryStr.append(")");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idAaTipoFascicoloExcluded != null) {
            query.setParameter("idAaTipoFascicoloExcluded", longFromBigDecimal(idAaTipoFascicoloExcluded));
        }
        query.setParameter("idTipoFascicolo", idTipoFascicolo.longValue());
        query.setParameter("aaIniTipoFascicolo", aaIniTipoFascicolo);
        query.setParameter("aaFinTipoFascicolo",
                aaFinTipoFascicolo != null ? aaFinTipoFascicolo : new BigDecimal(9999));
        return !query.getResultList().isEmpty();
    }

    public List<String> getNmAttribFascList(BigDecimal idAaTipoFascicolo, BigDecimal idTipoFascicolo) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT(attribFascicolo.nmAttribFascicolo) FROM DecAttribFascicolo attribFascicolo WHERE attribFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo ");
        String clause = " AND ";
        if (idTipoFascicolo != null) {
            queryStr.append(clause).append("attribFascicolo.decTipoFascicolo.idTipoFascicolo= :idTipoFascicolo");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idAaTipoFascicolo", longFromBigDecimal(idAaTipoFascicolo));
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", longFromBigDecimal(idTipoFascicolo));
        }

        return query.getResultList();
    }

    public List<DecAttribFascicolo> retrieveDecAttribFascicoloList(BigDecimal idXsdFascicolo,
            BigDecimal idAaTipoFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT attribFascicolo FROM DecModelloXsdAttribFascicolo xsdAttrib JOIN xsdAttrib.decAttribFascicolo attribFascicolo WHERE xsdAttrib.decModelloXsdFascicolo.idModelloXsdFascicolo = :idXsdFascicolo AND attribFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo");
        query.setParameter("idXsdFascicolo", longFromBigDecimal(idXsdFascicolo));
        query.setParameter("idAaTipoFascicolo", longFromBigDecimal(idAaTipoFascicolo));
        return query.getResultList();
    }

    /*
     * getNmAttribFascList modifica, aggiunta del controllo incrociato sia su idXsd che su idAttrib
     */
    public DecModelloXsdAttribFascicolo getDecXsdAttribFascicoloByAttrib(BigDecimal idAttribFascicolo,
            BigDecimal idXsdFascicolo) {
        String queryStr = "SELECT xsdAttribFascicolo FROM DecModelloXsdAttribFascicolo xsdAttribFascicolo "
                + "WHERE xsdAttribFascicolo.decAttribFascicolo.idAttribFascicolo = :idAttribFascicolo "
                + "AND xsdAttribFascicolo.decModelloXsdFascicolo.idModelloXsdFascicolo = :idXsdFascicolo";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAttribFascicolo", longFromBigDecimal(idAttribFascicolo));
        query.setParameter("idXsdFascicolo", longFromBigDecimal(idXsdFascicolo));
        List<DecModelloXsdAttribFascicolo> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return (list.get(0));

    }

    /**
     * 
     * @param idAttribFascicolo
     *            id Attributo fascicolo
     * 
     * @return DecModelloXsdAttribFascicolo
     */
    public DecModelloXsdAttribFascicolo getDecModelloXsdAttribFascicoloByAttrib(BigDecimal idAttribFascicolo) {
        String queryStr = "SELECT modelloXsdAttribFascicolo FROM DecModelloXsdAttribFascicolo modelloXsdAttribFascicolo "
                + "WHERE modelloXsdAttribFascicolo.decAttribFascicolo.idAttribFascicolo = :idAttribFascicolo ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAttribFascicolo", longFromBigDecimal(idAttribFascicolo));
        List<DecModelloXsdAttribFascicolo> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return (list.get(0));

    }

    /**
     * 
     * @param idAaTipoFascicolo
     *            id periodo di validita fascicolo
     * @param idModelloXsdFascicolo
     *            id modello xsd fascicolo
     * 
     * @return DecUsoModelloXsdFasc
     */
    public DecUsoModelloXsdFasc getDecUsoModelloXsdFascicoloByAttrib(BigDecimal idAaTipoFascicolo,
            BigDecimal idModelloXsdFascicolo) {
        String queryStr = "SELECT usoModelloXsdFasc FROM DecUsoModelloXsdFasc usoModelloXsdFasc "
                + "WHERE usoModelloXsdFasc.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                + "AND  usoModelloXsdFasc.decModelloXsdFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAaTipoFascicolo", longFromBigDecimal(idAaTipoFascicolo));
        query.setParameter("idModelloXsdFascicolo", longFromBigDecimal(idModelloXsdFascicolo));
        List<DecUsoModelloXsdFasc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return (list.get(0));
    }

    public DecTipoFascicolo findDecTipoFascicolo(Long idTipoFascicolo) {
        return getEntityManager().find(DecTipoFascicolo.class, idTipoFascicolo);
    }

    /**
     * 
     * @param tiModelloXsd
     *            tipo modello xsd
     * 
     * @return lista di elementi di tipo DecModelloXsdFascicolo
     */
    public List<DecModelloXsdFascicolo> getDecModelloXsdFascicoloByTiModelloXsd(String tiModelloXsd) {

        List<DecModelloXsdFascicolo> result = null;

        String queryStr = "SELECT modelloXsdFasc FROM DecModelloXsdFascicolo modelloXsdFasc WHERE modelloXsdFasc.tiModelloXsd = :tiModelloXsd";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("tiModelloXsd",
                it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.valueOf(tiModelloXsd));

        result = query.getResultList();

        return result;

    }

    /**
     * Ritorna l'oggetto DecTipoFascicolo dato il tipo fascicolo e la struttura di riferimento
     *
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     * @param idStrut
     *            id struttura
     *
     * @return l'oggetto DecTipoFascicolo o null se inesistente
     */
    public DecTipoFascicolo getDecTipoFascicoloByName(String nmTipoFascicolo, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery("SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo "
                + "WHERE UPPER(tipoFascicolo.nmTipoFascicolo) = :nmTipoFascicolo AND tipoFascicolo.orgStrut.idStrut=:idStrut");
        query.setParameter("nmTipoFascicolo", nmTipoFascicolo.toUpperCase());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecTipoFascicolo> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public DecAaTipoFascicolo getDecAaTipoFascicoloByAaIni(BigDecimal idStrut, String nmTipoFascicolo,
            BigDecimal aaIniTipoFascicolo) {
        String queryStr = "SELECT aaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
                + "WHERE aaTipoFascicolo.aaIniTipoFascicolo = :aaIniTipoFascicolo "
                + "AND aaTipoFascicolo.decTipoFascicolo.nmTipoFascicolo = :nmTipoFascicolo "
                + "AND aaTipoFascicolo.decTipoFascicolo.orgStrut.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("aaIniTipoFascicolo", aaIniTipoFascicolo);
        query.setParameter("nmTipoFascicolo", nmTipoFascicolo);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecAaTipoFascicolo> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public DecSelCriterioRaggrFasc getDecSelCriterioRaggrFasc(BigDecimal idStrutturaCorrente, String nmCriterioRaggr,
            String nmTipoFascicolo) {
        String queryStr = "SELECT selCriterioRaggrFasc FROM DecSelCriterioRaggrFasc selCriterioRaggrFasc "
                + "WHERE selCriterioRaggrFasc.decCriterioRaggrFasc.nmCriterioRaggr = :nmCriterioRaggr "
                + "AND selCriterioRaggrFasc.decTipoFascicolo.nmTipoFascicolo = :nmTipoFascicolo "
                + "AND selCriterioRaggrFasc.decTipoFascicolo.orgStrut.idStrut = :idStrut "
                + "AND selCriterioRaggrFasc.tiSel = :tiSel ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        query.setParameter("nmTipoFascicolo", nmTipoFascicolo);
        query.setParameter("idStrut", longFromBigDecimal(idStrutturaCorrente));
        query.setParameter("tiSel",
                it.eng.parer.entity.constraint.DecSelCriterioRaggrFasc.TiSelFasc.TIPO_FASCICOLO.name());
        List<DecSelCriterioRaggrFasc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public void deleteAaTipoFascicolo(List<Long> idAaTipoFascicoloList) {
        String queryStr = "DELETE FROM DecAaTipoFascicolo aaTipoFascicolo "
                + "WHERE aaTipoFascicolo.idAaTipoFascicolo IN :idAaTipoFascicoloList ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAaTipoFascicoloList", idAaTipoFascicoloList);

        query.executeUpdate();
    }

}
