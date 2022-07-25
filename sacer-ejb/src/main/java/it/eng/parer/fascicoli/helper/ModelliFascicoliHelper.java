package it.eng.parer.fascicoli.helper;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecUsoModelloXsdFasc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm;
import it.eng.spagoCore.error.EMFError;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class ModelliFascicoliHelper extends GenericHelper {

    /**
     * Ottiene la lista di modelli xsd abilitati per l'ambiente
     *
     * @param filtriModelliXsdTipiFasc
     *            filtro modelli xsd tipo fascicolo
     * @param idAmbienteList
     *            lista id ambiente
     * @param tiUsoModelloXsd
     *            tipo modello in uso xsd
     * @param filterValid
     *            true per prendere i record attivi attualmente
     * 
     * @return lista di modelli xsd
     * 
     * @throws EMFError
     *             errore generico
     */
    public List<DecModelloXsdFascicolo> retrieveDecModelloXsdTipoFascicolo(
            ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo filtriModelliXsdTipiFasc,
            List<BigDecimal> idAmbienteList, String tiUsoModelloXsd, boolean filterValid) throws EMFError {
        StringBuilder queryStr = new StringBuilder(
                "SELECT modelloXsdFascicolo FROM DecModelloXsdFascicolo modelloXsdFascicolo ");
        String whereClause = " WHERE ";
        if (!idAmbienteList.isEmpty()) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.orgAmbiente.idAmbiente IN :idAmbienteList ");
            whereClause = " AND ";
        }
        String flAttivo = filtriModelliXsdTipiFasc.getAttivo_xsd().parse();
        if (!StringUtils.isEmpty(flAttivo)) {
            if (flAttivo.equals("1")) {
                queryStr.append(whereClause)
                        .append("(modelloXsdFascicolo.dtIstituz <= :data AND modelloXsdFascicolo.dtSoppres >= :data) ");
            } else {
                queryStr.append(whereClause)
                        .append("(modelloXsdFascicolo.dtIstituz > :data OR modelloXsdFascicolo.dtSoppres < :data) ");
            }
            whereClause = " AND ";
        }
        String cdXsd = filtriModelliXsdTipiFasc.getCd_xsd().parse();
        if (!StringUtils.isEmpty(cdXsd)) {
            queryStr.append(whereClause).append("UPPER(modelloXsdFascicolo.cdXsd) LIKE :cdXsd ");
            whereClause = " AND ";
        }
        String dsXsd = filtriModelliXsdTipiFasc.getDs_xsd().parse();
        if (!StringUtils.isEmpty(dsXsd)) {
            queryStr.append(whereClause).append("UPPER(modelloXsdFascicolo.dsXsd) LIKE :dsXsd ");
            whereClause = " AND ";
        }
        String flDefault = filtriModelliXsdTipiFasc.getFl_default().parse();
        if (!StringUtils.isEmpty(flDefault)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.flDefault = :flDefault ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.tiUsoModelloXsd = :tiUsoModelloXsd ");
            whereClause = " AND ";
        }
        String tiModelloXsd = filtriModelliXsdTipiFasc.getTi_modello_xsd().parse();
        if (!StringUtils.isEmpty(tiModelloXsd)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.tiModelloXsd = :tiModelloXsd ");
            whereClause = " AND ";
        }
        // TODO: verificare
        /*
         * if (idStrut != null) { queryStr.append(whereClause).
         * append("NOT EXISTS (SELECT u FROM DecUsoModelloXsdFasc u JOIN u.decModelloXsdFascicolo mu WHERE mu.idModelloXsdFascicolo = modelloXsdFascicolo.idModelloXsdFascicolo AND u.orgStrut.idStrut != :idStrut) "
         * ); }
         */
        if (filterValid) {
            queryStr.append(whereClause).append(
                    "(modelloXsdFascicolo.dtIstituz <= :filterDate AND modelloXsdFascicolo.dtSoppres >= :filterDate) ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (!idAmbienteList.isEmpty()) {
            query.setParameter("idAmbienteList", idAmbienteList);
        }
        if (!StringUtils.isEmpty(flAttivo)) {
            Calendar dataOdierna = Calendar.getInstance();
            dataOdierna.set(Calendar.HOUR_OF_DAY, 0);
            dataOdierna.set(Calendar.MINUTE, 0);
            dataOdierna.set(Calendar.SECOND, 0);
            dataOdierna.set(Calendar.MILLISECOND, 0);
            query.setParameter("data", dataOdierna.getTime());
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
            query.setParameter("tiModelloXsd", tiModelloXsd);
        }
        // TODO: verificare
        /*
         * if (idStrut != null) { query.setParameter("idStrut", idStrut); }
         */
        if (filterValid) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        List<DecModelloXsdFascicolo> list = query.getResultList();
        return list;
    }

    /**
     * Ottiene la lista di modelli xsd in uso per l'ambiente e il tipo passati in input
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello xsd
     * @param filterValid
     *            true per prendere i record attivi attualmente
     * 
     * @return lista di modelli xsd
     */
    public List<DecModelloXsdFascicolo> retrieveDecModelloXsdFascicolo(BigDecimal idAmbiente, String tiModelloXsd,
            boolean filterValid) {
        StringBuilder queryStr = new StringBuilder("SELECT m FROM DecModelloXsdFascicolo m "
                + "WHERE m.orgAmbiente.idAmbiente = :idAmbiente AND m.tiModelloXsd = :tiModelloXsd ");
        if (filterValid) {
            queryStr.append("AND m.dtIstituz <= :filterDate AND m.dtSoppres >= :filterDate ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idAmbiente", idAmbiente);
        query.setParameter("tiModelloXsd", tiModelloXsd);
        if (filterValid) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        List<DecModelloXsdFascicolo> list = query.getResultList();
        return list;
    }

    /**
     * Ottiene il modello xsd di tipo fascicolo dati i dati di chiave unique
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello xsd
     * @param tiUsoModelloXsd
     *            tipo modello xsd in uso
     * @param cdXsd
     *            codice xsd
     * 
     * @return modello entity DecModelloXsdFascicolo
     */
    public DecModelloXsdFascicolo getDecModelloXsdFascicolo(BigDecimal idAmbiente, String tiModelloXsd,
            String tiUsoModelloXsd, String cdXsd) {
        Query query = getEntityManager()
                .createQuery("SELECT m FROM DecModelloXsdFascicolo m WHERE m.orgAmbiente.idAmbiente = :idAmbiente "
                        + "AND m.tiModelloXsd = :tiModelloXsd AND m.tiUsoModelloXsd = :tiUsoModelloXsd AND m.cdXsd = :cdXsd");
        query.setParameter("idAmbiente", idAmbiente);
        query.setParameter("tiModelloXsd", tiModelloXsd);
        query.setParameter("tiUsoModelloXsd", tiUsoModelloXsd);
        query.setParameter("cdXsd", cdXsd);
        List<DecModelloXsdFascicolo> list = query.getResultList();
        DecModelloXsdFascicolo modello = null;
        if (!list.isEmpty()) {
            modello = list.get(0);
        }
        return modello;
    }

    /**
     * Ritorna la lista di associazioni del modello xsd agli ambienti
     *
     * @param idModelloXsdFascicolo
     *            id modello xsd fascicolo
     * 
     * @return lista oggetti di tipo {@link DecUsoModelloXsdFasc}
     */
    public List<DecUsoModelloXsdFasc> retrieveDecUsoModelloXsdFasc(BigDecimal idModelloXsdFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT u FROM DecUsoModelloXsdFasc u WHERE u.decModelloXsdFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo");
        query.setParameter("idModelloXsdFascicolo", idModelloXsdFascicolo);
        List<DecUsoModelloXsdFasc> list = query.getResultList();
        return list;
    }

    public boolean existDecUsoModelloXsdFasc(BigDecimal idModelloXsdFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecUsoModelloXsdFasc d WHERE d.decModelloXsdFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo");
        query.setParameter("idModelloXsdFascicolo", idModelloXsdFascicolo);
        List<DecUsoModelloXsdFasc> list = query.getResultList();
        return !list.isEmpty();
    }
}
