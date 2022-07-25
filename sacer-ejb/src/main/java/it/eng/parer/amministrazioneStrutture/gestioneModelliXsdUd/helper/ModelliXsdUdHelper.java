package it.eng.parer.amministrazioneStrutture.gestioneModelliXsdUd.helper;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.DecModelloXsdUd;
import it.eng.parer.entity.constraint.DecModelloXsdUd.TiModelloXsdUd;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ModelliUDForm.FiltriModelliXsdUd;
import it.eng.parer.ws.utils.CostantiDB.TipiEntitaSacer;
import it.eng.spagoCore.error.EMFError;

@Stateless
@LocalBean
public class ModelliXsdUdHelper extends GenericHelper {

    /**
     * Restituisce una lista di modelli xsd
     * 
     * @param idTiEntita
     *            id entità
     * @param tiEntitaSacer
     *            tipi entità
     * @param tiUsoModello
     *            VERS / MIGRAZ
     * @param filterValid
     *            se valido o meno
     * 
     * @return lista dei modelli xsd
     */
    public List retrieveDecModelliXsdUdListByTiEntitaInUso(BigDecimal idTiEntita, TipiEntitaSacer tiEntitaSacer,
            String tiUsoModello, boolean filterValid) {
        return retrieveDecUsoModelloXsdUdListByTiEntitaInUso(null, idTiEntita, tiEntitaSacer, tiUsoModello, null,
                StringUtils.EMPTY, filterValid);
    }

    /**
     * Restituisce una lista di modelli xsd
     * 
     * @param idTiEntita
     *            id entità
     * @param tiEntitaSacer
     *            tipi entità
     * @param tiUsoModello
     *            VERS / MIGRAZ
     * @param cdXsd
     *            versione modello
     * @param filterValid
     *            se valido o meno
     * 
     * @return modello xsd
     */
    public List retrieveDecModelliXsdUdDetailByTiEntitaInUso(BigDecimal idTiEntita, TipiEntitaSacer tiEntitaSacer,
            String tiUsoModello, String cdXsd, boolean filterValid) {
        return retrieveDecUsoModelloXsdUdListByTiEntitaInUso(null, idTiEntita, tiEntitaSacer, tiUsoModello, cdXsd,
                StringUtils.EMPTY, filterValid);
    }

    /**
     * Restiusice l'uso del modello
     * 
     * @param idModelloXsdUd
     *            id modello (opzionale)
     * @param idTiEntita
     *            id entita
     * @param tiEntitaSacer
     *            tipo entita
     * @param tiUsoModello
     *            tipo uso
     * @param cdXsd
     *            versione modello (opzionale)
     * @param flStandard
     *            true = standard / false = altrimenti
     * @param filterValid
     *            se valido (opzionale)
     * 
     * @return lista uso del modello
     */
    public List retrieveDecUsoModelloXsdUdListByTiEntitaInUso(BigDecimal idModelloXsdUd, BigDecimal idTiEntita,
            TipiEntitaSacer tiEntitaSacer, String tiUsoModello, String cdXsd, String flStandard, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder();
        //
        queryStr.append("select uso ");

        if (tiEntitaSacer.equals(TipiEntitaSacer.UNI_DOC)) {
            queryStr.append("from DecUsoModelloXsdUniDoc uso ");
            queryStr.append("where uso.decTipoUnitaDoc.idTipoUnitaDoc = :idTiEntita ");
        } else if (tiEntitaSacer.equals(TipiEntitaSacer.DOC)) {
            queryStr.append("from DecUsoModelloXsdDoc uso ");
            queryStr.append("where uso.decTipoDoc.idTipoDoc = :idTiEntita ");
        } else if (tiEntitaSacer.equals(TipiEntitaSacer.COMP) || tiEntitaSacer.equals(TipiEntitaSacer.SUB_COMP)) {
            queryStr.append("from DecUsoModelloXsdCompDoc uso ");
            queryStr.append("where uso.decTipoCompDoc.idTipoCompDoc = :idTiEntita ");
        }
        //
        if (idModelloXsdUd != null) {
            queryStr.append("AND uso.decModelloXsdUd.idModelloXsdUd = :idModelloXsdUd ");
        }
        if (StringUtils.isNotBlank(cdXsd)) {
            queryStr.append("AND uso.decModelloXsdUd.cdXsd = :cdXsd ");
        }
        if (StringUtils.isNotBlank(flStandard)) {
            queryStr.append("AND uso.flStandard = :flStandard ");
        }
        //
        if (filterValid) {
            queryStr.append("AND uso.dtIstituz <= :filterDate AND uso.dtSoppres >= :filterDate ");
        }
        // common part (mandatory)
        queryStr.append("AND uso.decModelloXsdUd.tiUsoModelloXsd = :tiUsoModelloXsd ");

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (idModelloXsdUd != null) {
            query.setParameter("idModelloXsdUd", idModelloXsdUd);
        }
        if (StringUtils.isNotBlank(cdXsd)) {
            query.setParameter("cdXsd", cdXsd);
        }
        if (StringUtils.isNotBlank(flStandard)) {
            query.setParameter("flStandard", flStandard);
        }
        if (filterValid) {
            query.setParameter("filterDate", new Date());
        }

        // mandatory
        query.setParameter("idTiEntita", idTiEntita);
        query.setParameter("tiUsoModelloXsd", tiUsoModello);

        return query.getResultList();
    }

    /**
     * Restituisce la lista dei modelli per ambiente
     * 
     * @param idAmbiente
     *            id ambiente
     * @param tiUsoModelloXsd
     *            uso modello
     * @param filterValid
     *            se valido o meno
     * 
     * @return lista modelli
     */
    public List<DecModelloXsdUd> retrieveDecModelliXsdUd4Amb(BigDecimal idAmbiente, String tiUsoModelloXsd,
            boolean filterValid) {
        return retrieveDecModelliXsdUd(idAmbiente, null, tiUsoModelloXsd, null, StringUtils.EMPTY, filterValid);
    }

    /**
     * Restituisce la lista dei modelli per ambiente, tipo modello e versione
     * 
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello
     * @param tiUsoModelloXsd
     *            uso modello
     * @param cdXsd
     *            versione
     * @param filterValid
     *            se valido o meno
     * 
     * @return lista modelli
     */
    public List<DecModelloXsdUd> retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd(BigDecimal idAmbiente, String tiModelloXsd,
            String tiUsoModelloXsd, String cdXsd, boolean filterValid) {
        return retrieveDecModelliXsdUd(idAmbiente, tiModelloXsd, tiUsoModelloXsd, cdXsd, StringUtils.EMPTY,
                filterValid);
    }

    /**
     * Restituisce la lista dei modelli per ambiente e tipo modello
     * 
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello
     * @param tiUsoModelloXsd
     *            uso modello
     * @param filterValid
     *            se valido o meno
     * 
     * @return lista modelli
     */
    public List<DecModelloXsdUd> retrieveDecModelliXsdUd4AmbAndTiModelloXsd(BigDecimal idAmbiente, String tiModelloXsd,
            String tiUsoModelloXsd, boolean filterValid) {
        return retrieveDecModelliXsdUd(idAmbiente, tiModelloXsd, tiUsoModelloXsd, null, StringUtils.EMPTY, filterValid);
    }

    /**
     * Restituisce la lista dei modelli per ambiente / tipo modello e default
     * 
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello
     * @param tiUsoModelloXsd
     *            uso modello
     * @param flDefault
     *            1 = default / 0 altrimenti
     * @param filterValid
     *            se valido o meno
     * 
     * @return lista modelli
     */
    public List<DecModelloXsdUd> retrieveDecModelliXsdUd4AmbAndTiModelloDefXsd(BigDecimal idAmbiente,
            String tiModelloXsd, String tiUsoModelloXsd, String flDefault, boolean filterValid) {
        return retrieveDecModelliXsdUd(idAmbiente, tiModelloXsd, tiUsoModelloXsd, null, flDefault, filterValid);
    }

    private List<DecModelloXsdUd> retrieveDecModelliXsdUd(BigDecimal idAmbiente, String tiModelloXsd,
            String tiUsoModelloXsd, String cdXsd, String flDefault, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder();
        //
        queryStr.append("select d ");
        queryStr.append("from DecModelloXsdUd d ");
        queryStr.append("where d.orgAmbiente.idAmbiente = :idAmbiente ");
        //
        if (StringUtils.isNotBlank(tiModelloXsd)) {
            queryStr.append("AND d.tiModelloXsd = :tiModelloXsd ");
        }
        if (StringUtils.isNotBlank(cdXsd)) {
            queryStr.append("AND d.cdXsd = :cdXsd ");
        }
        if (StringUtils.isNotBlank(flDefault)) {
            queryStr.append("AND d.flDefault = :flDefault ");
        }
        queryStr.append("AND d.tiUsoModelloXsd = :tiUsoModelloXsd ");
        //
        if (filterValid) {
            queryStr.append("AND d.dtIstituz <= :filterDate AND d.dtSoppres >= :filterDate ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", idAmbiente);
        if (StringUtils.isNotBlank(tiModelloXsd)) {
            query.setParameter("tiModelloXsd", TiModelloXsdUd.valueOf(tiModelloXsd));
        }
        if (StringUtils.isNotBlank(cdXsd)) {
            query.setParameter("cdXsd", cdXsd);
        }
        if (StringUtils.isNotBlank(flDefault)) {
            query.setParameter("flDefault", flDefault);
        }
        query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
        if (filterValid) {
            query.setParameter("filterDate", new Date());
        }
        return query.getResultList();
    }

    /**
     * Verifica se il modello associato all'entità è in uso, o meno, sui versamenti UD
     * 
     * @param idStrut
     *            id struttura
     * @param idUsoModelloXsdUd
     *            id uso modello
     * @param tiEntitaSacer
     *            tipo entità
     * 
     * @return true se non esiste modello in uso su versamento / false altrimenti
     */
    public boolean decUsoModelloXsdUdInUseOnVrs(BigDecimal idStrut, BigDecimal idUsoModelloXsdUd,
            TipiEntitaSacer tiEntitaSacer) {
        boolean result = true;
        StringBuilder queryStr = new StringBuilder();

        queryStr.append("select uso.decModelloXsdUd ");

        if (tiEntitaSacer.equals(TipiEntitaSacer.UNI_DOC)) {
            queryStr.append("from DecUsoModelloXsdUniDoc uso ");
            queryStr.append("where uso.idUsoModelloXsdUniDoc = :idUsoModelloXsdUd ");
            queryStr.append(
                    " AND NOT EXISTS (select vrs from VrsXmlModelloSessioneVers vrs where vrs.decUsoModelloXsdUniDoc.idUsoModelloXsdUniDoc = uso.idUsoModelloXsdUniDoc and vrs.idStrut = :idStrut) ");
        } else if (tiEntitaSacer.equals(TipiEntitaSacer.DOC)) {
            queryStr.append("from DecUsoModelloXsdDoc uso ");
            queryStr.append("where uso.idUsoModelloXsdDoc = :idUsoModelloXsdUd ");
            queryStr.append(
                    " AND NOT EXISTS (select vrs from VrsXmlModelloSessioneVers vrs where vrs.decUsoModelloXsdDoc.idUsoModelloXsdDoc = uso.idUsoModelloXsdDoc and vrs.idStrut = :idStrut) ");
        } else if (tiEntitaSacer.equals(TipiEntitaSacer.COMP) || tiEntitaSacer.equals(TipiEntitaSacer.SUB_COMP)) {
            queryStr.append("from DecUsoModelloXsdCompDoc uso ");
            queryStr.append("where uso.idUsoModelloXsdCompDoc = :idUsoModelloXsdUd ");
            queryStr.append(
                    " AND NOT EXISTS (select vrs from VrsXmlModelloSessioneVers vrs where vrs.decUsoModelloXsdCompDoc.idUsoModelloXsdCompDoc = uso.idUsoModelloXsdCompDoc and vrs.idStrut = :idStrut) ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", idStrut);
        query.setParameter("idUsoModelloXsdUd", idUsoModelloXsdUd);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    /**
     * Verifica se il modello associato all'entità è in uso, o meno, sui versamenti UD
     * 
     * @param idModelloXsdUd
     *            id modello xsd
     * 
     * @return true se non esiste modello in uso su versamento / false altrimenti
     */
    public boolean decModelloXsdUdInUseOnVrs(BigDecimal idModelloXsdUd) {
        StringBuilder queryStr = new StringBuilder();

        queryStr.append("select m ");

        queryStr.append("from DecModelloXsdUd m ");
        queryStr.append("where m.idModelloXsdUd = :idModelloXsdUd ");
        queryStr.append(" AND (");
        queryStr.append(
                " EXISTS (select vrsusounidoc from VrsXmlModelloSessioneVers vrsusounidoc where vrsusounidoc.decUsoModelloXsdUniDoc.decModelloXsdUd.idModelloXsdUd =  m.idModelloXsdUd) ");
        queryStr.append(
                " OR EXISTS (select vrsusodoc from VrsXmlModelloSessioneVers vrsusodoc where vrsusodoc.decUsoModelloXsdDoc.decModelloXsdUd.idModelloXsdUd = m.idModelloXsdUd) ");
        queryStr.append(
                " OR EXISTS (select vrsusocomp from VrsXmlModelloSessioneVers vrsusocomp where vrsusocomp.decUsoModelloXsdCompDoc.decModelloXsdUd.idModelloXsdUd = m.idModelloXsdUd) ");
        queryStr.append(")");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idModelloXsdUd", idModelloXsdUd);
        List<Object[]> list = query.getResultList();
        return list.isEmpty();
    }

    /**
     * Verifica se il modello già in uso in una delle relazioni
     * 
     * @param idModelloXsdUd
     *            id del modello
     * @param filterValid
     *            se valido o meno
     * 
     * @return true se il modello è utilizzato / false altrimenti
     */
    public boolean existDecModelliXsdUdListInUso(BigDecimal idModelloXsdUd, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder();
        //
        queryStr.append("select m from DecModelloXsdUd m " + "where (m.idModelloXsdUd IN ("
                + "select usoud.decModelloXsdUd.idModelloXsdUd " + "from DecUsoModelloXsdUniDoc usoud "
                + "where usoud.decModelloXsdUd.idModelloXsdUd = m.idModelloXsdUd ) "

                + "OR m.idModelloXsdUd IN (" + "select usod.decModelloXsdUd.idModelloXsdUd "
                + "from DecUsoModelloXsdDoc usod " + "where usod.decModelloXsdUd.idModelloXsdUd = m.idModelloXsdUd ) "

                + "OR m.idModelloXsdUd IN (" + "select usoc.decModelloXsdUd.idModelloXsdUd "
                + "from DecUsoModelloXsdCompDoc usoc "
                + "where usoc.decModelloXsdUd.idModelloXsdUd = m.idModelloXsdUd )) ");

        queryStr.append("AND m.idModelloXsdUd = :idModelloXsdUd ");

        //
        if (filterValid) {
            queryStr.append("AND m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idModelloXsdUd", idModelloXsdUd);
        if (filterValid) {
            query.setParameter("filterDate", new Date());
        }
        List<Object[]> list = query.getResultList();
        return !list.isEmpty();
    }

    /**
     * Retituisce la lista dei modelli con filtro applicato
     * 
     * @param filtriModelliXsdUd
     *            filtro
     * @param idAmbientiToFind
     *            lista id ambienti
     * @param tiUsoModelloXsd
     *            tipo uso modello
     * @param filterValid
     *            true = valido / false altrimenti
     * 
     * @return lista modelli xsd ud
     * 
     * @throws EMFError
     *             eccezione generica
     */
    public List<DecModelloXsdUd> findDecModelliXsdUdList(FiltriModelliXsdUd filtriModelliXsdUd,
            List<BigDecimal> idAmbientiToFind, String tiUsoModelloXsd, boolean filterValid) throws EMFError {
        StringBuilder queryStr = new StringBuilder("select m FROM DecModelloXsdUd m ");
        String whereClause = " WHERE ";
        if (!idAmbientiToFind.isEmpty()) {
            queryStr.append(whereClause).append("m.orgAmbiente.idAmbiente IN :idAmbientiToFind ");
            whereClause = " AND ";
        }
        String cdXsd = filtriModelliXsdUd.getCd_xsd().parse();
        if (!StringUtils.isEmpty(cdXsd)) {
            queryStr.append(whereClause).append("UPPER(m.cdXsd) LIKE :cdXsd ");
            whereClause = " AND ";
        }
        String dsXsd = filtriModelliXsdUd.getDs_xsd().parse();
        if (!StringUtils.isEmpty(dsXsd)) {
            queryStr.append(whereClause).append("UPPER(m.dsXsd) LIKE :dsXsd ");
            whereClause = " AND ";
        }
        String flDefault = filtriModelliXsdUd.getFl_default().parse();
        if (!StringUtils.isEmpty(flDefault)) {
            queryStr.append(whereClause).append("m.flDefault = :flDefault ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            queryStr.append(whereClause).append("m.tiUsoModelloXsd = :tiUsoModelloXsd ");
            whereClause = " AND ";
        }
        String tiModelloXsd = filtriModelliXsdUd.getTi_modello_xsd().parse();
        if (!StringUtils.isEmpty(tiModelloXsd)) {
            queryStr.append(whereClause).append("m.tiModelloXsd = :tiModelloXsd ");
            whereClause = " AND ";
        }

        if (filterValid) {
            queryStr.append(whereClause).append("(m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate) ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (!idAmbientiToFind.isEmpty()) {
            query.setParameter("idAmbientiToFind", idAmbientiToFind);
        }

        if (!StringUtils.isEmpty(cdXsd)) {
            query.setParameter("cdXsd", cdXsd + "%");
        }
        if (!StringUtils.isEmpty(dsXsd)) {
            query.setParameter("dsXsd", "%" + dsXsd.toUpperCase() + "%");
        }
        if (!StringUtils.isEmpty(flDefault)) {
            query.setParameter("flDefault", flDefault);
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
        }
        if (!StringUtils.isEmpty(tiModelloXsd)) {
            query.setParameter("tiModelloXsd", TiModelloXsdUd.valueOf(tiModelloXsd));
        }
        if (filterValid) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        return query.getResultList();
    }

}
