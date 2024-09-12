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
import static it.eng.parer.util.Utils.longListFrom;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecUsoModelloXsdFasc;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiUsoModelloXsd;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm;
import it.eng.spagoCore.error.EMFError;

/**
 * @author DiLorenzo_F
 */
@SuppressWarnings({ "unchecked" })
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
        return retrieveDecModelloXsdTipoFascicolo(idAmbienteList, tiUsoModelloXsd, filterValid,
                new Filtri(filtriModelliXsdTipiFasc));
    }

    /**
     * Ottiene la lista di modelli xsd abilitati per l'ambiente
     *
     * @param idAmbienteList
     *            lista di ID ambiente
     * @param tiUsoModelloXsd
     *            tipo uso del modello Xsd
     * @param filterValid
     *            true per prendere i record attivi attualmente
     * @param filtri
     *            filtri di ricerca
     *
     * @return lista di modelli xsd
     */
    public List<DecModelloXsdFascicolo> retrieveDecModelloXsdTipoFascicolo(List<BigDecimal> idAmbienteList,
            String tiUsoModelloXsd, boolean filterValid, Filtri filtri) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT modelloXsdFascicolo FROM DecModelloXsdFascicolo modelloXsdFascicolo ");
        String whereClause = " WHERE ";
        if (!idAmbienteList.isEmpty()) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.orgAmbiente.idAmbiente IN (:idAmbienteList) ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(filtri.getFlAttivo())) {
            if (filtri.getFlAttivo().equals("1")) {
                queryStr.append(whereClause)
                        .append("(modelloXsdFascicolo.dtIstituz <= :data AND modelloXsdFascicolo.dtSoppres >= :data) ");
            } else {
                queryStr.append(whereClause)
                        .append("(modelloXsdFascicolo.dtIstituz > :data OR modelloXsdFascicolo.dtSoppres < :data) ");
            }
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(filtri.getCdXsd())) {
            queryStr.append(whereClause).append("UPPER(modelloXsdFascicolo.cdXsd) LIKE :cdXsd ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(filtri.getDsXsd())) {
            queryStr.append(whereClause).append("UPPER(modelloXsdFascicolo.dsXsd) LIKE :dsXsd ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(filtri.getFlDefault())) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.flDefault = :flDefault ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.tiUsoModelloXsd = :tiUsoModelloXsd ");
            whereClause = " AND ";
        }
        if (!StringUtils.isEmpty(filtri.getTiModelloXsd())) {
            queryStr.append(whereClause).append("modelloXsdFascicolo.tiModelloXsd = :tiModelloXsd ");
            whereClause = " AND ";
        }
        if (filterValid) {
            queryStr.append(whereClause).append(
                    "(modelloXsdFascicolo.dtIstituz <= :filterDate AND modelloXsdFascicolo.dtSoppres >= :filterDate) ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        if (!idAmbienteList.isEmpty()) {
            query.setParameter("idAmbienteList", longListFrom(idAmbienteList));
        }
        if (!StringUtils.isEmpty(filtri.getFlAttivo())) {
            Calendar dataOdierna = Calendar.getInstance();
            dataOdierna.set(Calendar.HOUR_OF_DAY, 0);
            dataOdierna.set(Calendar.MINUTE, 0);
            dataOdierna.set(Calendar.SECOND, 0);
            dataOdierna.set(Calendar.MILLISECOND, 0);
            query.setParameter("data", dataOdierna.getTime());
        }
        if (!StringUtils.isEmpty(filtri.getCdXsd())) {
            query.setParameter("cdXsd", filtri.getCdXsd() + "%");
        }
        if (!StringUtils.isEmpty(filtri.getDsXsd())) {
            query.setParameter("dsXsd", "%" + filtri.getDsXsd().toUpperCase() + "%");
        }
        if (!StringUtils.isEmpty(filtri.getFlDefault())) {
            query.setParameter("flDefault", filtri.getFlDefault());
        }
        if (!StringUtils.isEmpty(tiUsoModelloXsd)) {
            query.setParameter("tiUsoModelloXsd", TiUsoModelloXsd.valueOf(tiUsoModelloXsd));
        }
        if (!StringUtils.isEmpty(filtri.getTiModelloXsd())) {
            query.setParameter("tiModelloXsd", TiModelloXsd.valueOf(filtri.getTiModelloXsd()));
        }
        if (filterValid) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        return query.getResultList();
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
        query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        query.setParameter("tiModelloXsd", tiModelloXsd);
        if (filterValid) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        return query.getResultList();
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
        query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        query.setParameter("tiModelloXsd", TiModelloXsd.valueOf(tiModelloXsd));
        query.setParameter("tiUsoModelloXsd", TiUsoModelloXsd.valueOf(tiUsoModelloXsd));
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
        query.setParameter("idModelloXsdFascicolo", longFromBigDecimal(idModelloXsdFascicolo));
        return query.getResultList();
    }

    public boolean existDecUsoModelloXsdFasc(BigDecimal idModelloXsdFascicolo) {
        Query query = getEntityManager().createQuery(
                "SELECT d FROM DecUsoModelloXsdFasc d WHERE d.decModelloXsdFascicolo.idModelloXsdFascicolo = :idModelloXsdFascicolo");
        query.setParameter("idModelloXsdFascicolo", longFromBigDecimal(idModelloXsdFascicolo));
        List<DecUsoModelloXsdFasc> list = query.getResultList();
        return !list.isEmpty();
    }

    public static class Filtri {
        String flAttivo;
        String cdXsd;
        String dsXsd;
        String flDefault;
        String tiModelloXsd;

        public Filtri() {

        }

        public Filtri(ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo filtriModelliXsdTipiFasc) throws EMFError {
            flAttivo = filtriModelliXsdTipiFasc.getAttivo_xsd().parse();
            cdXsd = filtriModelliXsdTipiFasc.getCd_xsd().parse();
            dsXsd = filtriModelliXsdTipiFasc.getDs_xsd().parse();
            flDefault = filtriModelliXsdTipiFasc.getFl_default().parse();
            tiModelloXsd = filtriModelliXsdTipiFasc.getTi_modello_xsd().parse();
        }

        public String getFlAttivo() {
            return flAttivo;
        }

        public void setFlAttivo(String flAttivo) {
            this.flAttivo = flAttivo;
        }

        public String getCdXsd() {
            return cdXsd;
        }

        public void setCdXsd(String cdXsd) {
            this.cdXsd = cdXsd;
        }

        public String getDsXsd() {
            return dsXsd;
        }

        public void setDsXsd(String dsXsd) {
            this.dsXsd = dsXsd;
        }

        public String getFlDefault() {
            return flDefault;
        }

        public void setFlDefault(String flDefault) {
            this.flDefault = flDefault;
        }

        public String getTiModelloXsd() {
            return tiModelloXsd;
        }

        public void setTiModelloXsd(String tiModelloXsd) {
            this.tiModelloXsd = tiModelloXsd;
        }
    }
}
