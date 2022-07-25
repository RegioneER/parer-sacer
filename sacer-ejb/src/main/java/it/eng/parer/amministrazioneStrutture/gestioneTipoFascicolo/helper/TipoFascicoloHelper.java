package it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper;

import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecAttribFascicolo;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecErrAaTipoFascicolo;
import it.eng.parer.entity.DecModelloXsdAttribFascicolo;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecParteNumeroFascicolo;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecUsoModelloXsdFasc;
import it.eng.parer.entity.DecXsdAttribDatiSpec;
import it.eng.parer.helper.GenericHelper;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 *
 * @author gilioli_p
 */
@Stateless
@LocalBean
public class TipoFascicoloHelper extends GenericHelper {

    private static final Logger logger = LoggerFactory.getLogger(TipoFascicoloHelper.class);

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
            whereClause = " AND ";
        }

        queryStr.append("ORDER BY tipoFascicolo.nmTipoFascicolo ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        List<DecTipoFascicolo> list = (List<DecTipoFascicolo>) query.getResultList();
        return list;
    }

    public List<DecTipoFascicolo> getTipiFascicoloAbilitati(long idUtente, BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo, IamAbilTipoDato iatd "
                        + "WHERE iatd.idTipoDatoApplic = tipoFascicolo.idTipoFascicolo "
                        + "AND iatd.nmClasseTipoDato = 'TIPO_FASCICOLO' "
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
            query.setParameter("idStrut", idStrut);
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        List<DecTipoFascicolo> list = (List<DecTipoFascicolo>) query.getResultList();
        return list;
    }

    /*
     * Torna true se l'utente per una determinata struttura è abilitato al tipo fascicolo
     */
    public boolean isTipoFascicoloAbilitato(long idUtente, BigDecimal idStrut, BigDecimal idTipoFascicolo,
            boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoFascicolo FROM DecTipoFascicolo tipoFascicolo, IamAbilTipoDato iatd "
                        + "WHERE iatd.idTipoDatoApplic = tipoFascicolo.idTipoFascicolo "
                        + "AND iatd.nmClasseTipoDato = 'TIPO_FASCICOLO' "
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
            query.setParameter("idStrut", idStrut);
        }
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }
        List<DecTipoFascicolo> list = (List<DecTipoFascicolo>) query.getResultList();
        return (list != null && !list.isEmpty()) ? true : false;
    }

    public DecAaTipoFascicolo getLastDecAaTipoFascicolo(BigDecimal idTipoFascicolo) {
        if (idTipoFascicolo != null) {
            Query query = getEntityManager()
                    .createQuery("SELECT aaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
                            + "WHERE aaTipoFascicolo.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
                            + "ORDER BY aaTipoFascicolo.aaIniTipoFascicolo DESC ");
            query.setParameter("idTipoFascicolo", idTipoFascicolo.longValue());
            List<DecAaTipoFascicolo> aaTipoFascicoloList = (List<DecAaTipoFascicolo>) query.getResultList();
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
            whereClause = " AND ";
        }
        queryStr.append("ORDER BY aaTipoFascicolo.aaIniTipoFascicolo DESC ");
        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
        }
        List<DecAaTipoFascicolo> list = (List<DecAaTipoFascicolo>) query.getResultList();
        return list;
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
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
        }
        List<DecCriterioRaggrFasc> list = (List<DecCriterioRaggrFasc>) query.getResultList();
        return list;
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
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
            return (Long) query.getSingleResult() > 0L;
        } else {
            throw new IllegalArgumentException("Identificativo struttura e/o id tipo fascicolo nulli");
        }
    }

    // public OrgStrutConfigFascicolo getOrgStrutConfigFascicoloByIdStrut(BigDecimal idStrut) {
    // if (idStrut != null) {
    // Query query = getEntityManager().createQuery("SELECT strutConfigFascicolo FROM OrgStrutConfigFascicolo
    // strutConfigFascicolo WHERE strutConfigFascicolo.orgStrut.idStrut = :idStrut");
    // query.setParameter("idStrut", idStrut.longValue());
    // List<OrgStrutConfigFascicolo> strutConfigFascicoloList = (List<OrgStrutConfigFascicolo>) query.getResultList();
    // if (!strutConfigFascicoloList.isEmpty()) {
    // return strutConfigFascicoloList.get(0);
    // }
    // return null;
    // } else {
    // throw new IllegalArgumentException("Parametro idStrut nullo");
    // }
    // }

    public List<DecModelloXsdFascicolo> retrieveDecModelloXsdFascicolo(BigDecimal idAmbiente, Date data,
            String flDefault, String tiUsoModelloXsd, String tiModelloXsd) {
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
        if (!StringUtils.isEmpty(tiModelloXsd)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.tiModelloXsd = :tiModelloXsd ");
            whereClause = " AND ";
        }

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
            query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
        }
        if (!StringUtils.isEmpty(tiModelloXsd)) {
            query.setParameter("tiModelloXsd", tiModelloXsd);
        }
        List<DecModelloXsdFascicolo> list = (List<DecModelloXsdFascicolo>) query.getResultList();
        return list;
    }

    public List<DecParteNumeroFascicolo> getDecParteNumeroFascicoloList(BigDecimal idAaTipoFascicolo) {
        String queryStr = "SELECT parteNumeroFascicolo FROM DecParteNumeroFascicolo parteNumeroFascicolo "
                + "WHERE parteNumeroFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                + "ORDER BY parteNumeroFascicolo.niParteNumero ASC ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo.longValue());
        return (List<DecParteNumeroFascicolo>) query.getResultList();
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

    public List<DecErrAaTipoFascicolo> getDecErrAaTipoFascicoloList(BigDecimal idAaTipoFascicolo) {
        if (idAaTipoFascicolo != null) {
            String queryStr = "SELECT errAaTipoFascicolo FROM DecErrAaTipoFascicolo errAaTipoFascicolo "
                    + "WHERE errAaTipoFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                    + "ORDER BY errAaTipoFascicolo.aaTipoFascicolo DESC ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
            List<DecErrAaTipoFascicolo> list = (List<DecErrAaTipoFascicolo>) query.getResultList();
            return list;
        } else {
            throw new IllegalArgumentException("Parametro idAaTipoFascicolo nullo");
        }
    }

    public DecErrAaTipoFascicolo getDecErrAaTipoFascicolo(BigDecimal idAaTipoFascicolo, Integer aaFascicolo) {
        if (idAaTipoFascicolo != null && aaFascicolo != null) {
            String queryStr = "SELECT errAaTipoFascicolo FROM DecErrAaTipoFascicolo errAaTipoFascicolo "
                    + "WHERE errAaTipoFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                    + "AND errAaTipoFascicolo.aaFascicolo = :aaFascicolo ";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
            query.setParameter("aaFascicolo", aaFascicolo);
            List<DecErrAaTipoFascicolo> list = (List<DecErrAaTipoFascicolo>) query.getResultList();
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
            whereWord = " AND ";
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idAaTipoFascicolo != null) {
            query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        }
        if (idModelloXsdFascicolo != null) {
            query.setParameter("idModelloXsdFascicolo", idModelloXsdFascicolo);
        }
        if (flStandard != null) {
            query.setParameter("flStandard", flStandard);
        }

        return (List<DecUsoModelloXsdFasc>) query.getResultList();
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
            query.setParameter("idAaTipoFascicoloExcluded", idAaTipoFascicoloExcluded);
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

        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
        }

        return query.getResultList();
    }

    public List<DecAttribFascicolo> retrieveDecAttribFascicoloList(BigDecimal idXsdFascicolo,
            BigDecimal idAaTipoFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT attribFascicolo FROM DecModelloXsdAttribFascicolo xsdAttrib JOIN xsdAttrib.decAttribFascicolo attribFascicolo WHERE xsdAttrib.decModelloXsdFascicolo.idModelloXsdFascicolo = :idXsdFascicolo AND attribFascicolo.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo");
        query.setParameter("idXsdFascicolo", idXsdFascicolo);
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
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
        query.setParameter("idAttribFascicolo", idAttribFascicolo);
        query.setParameter("idXsdFascicolo", idXsdFascicolo);
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
        query.setParameter("idAttribFascicolo", idAttribFascicolo);
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
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        query.setParameter("idModelloXsdFascicolo", idModelloXsdFascicolo);
        List<DecUsoModelloXsdFasc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return (list.get(0));

    }

}
