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

package it.eng.parer.job.indiceAipSerieUd.helper;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;

import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.helper.GenericHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipSerieUdHelper")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipSerieUdHelper extends GenericHelper {

    /**
     * Recupera la lista dei record da elaborare per creare l'indice AIP Serie UD in base alla
     * struttura ed allo stato della versione
     *
     * @param idStrut         id struttura
     * @param tiStatoVerSerie stato versamento serie
     *
     * @return la lista da elaborare
     */
    @SuppressWarnings("unchecked")
    public List<SerVerSerieDaElab> getSerVerSerieDaElab(BigDecimal idStrut,
            String tiStatoVerSerie) {
        List<SerVerSerieDaElab> serieDaElabList;
        String whereWord = "WHERE ";
        String queryStr = "SELECT u FROM SerVerSerieDaElab u ";
        if (idStrut != null) {
            queryStr = queryStr + whereWord + "u.idStrut = :idStrut ";
            whereWord = "AND ";
        }
        if (tiStatoVerSerie != null) {
            queryStr = queryStr + whereWord + "u.tiStatoVerSerie = :tiStatoVerSerie ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (tiStatoVerSerie != null) {
            query.setParameter("tiStatoVerSerie", tiStatoVerSerie);
        }
        serieDaElabList = query.getResultList();
        return serieDaElabList;
    }

    /**
     * Conta le ud appartenenti al contenuto di tipo EFFETTIVO per le quali la foreign key al volume
     * è nulla
     *
     * @param idVerSerie id versamento serie
     *
     * @return pk entity AroUdAppartVerSerie
     */
    public Long getNumUdEffettiveSenzaVolume(Long idVerSerie) {
        String queryStr = "SELECT COUNT(u) FROM AroUdAppartVerSerie u "
                + "JOIN u.serContenutoVerSerie contenutoVerSerie "
                + "JOIN contenutoVerSerie.serVerSerie verSerie "
                + "WHERE contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' "
                + "AND u.serVolVerSerie IS NULL " + "AND verSerie.idVerSerie = :idVerSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerSerie", idVerSerie);
        return (Long) query.getSingleResult();
    }

    /**
     * Recupera l'ultimo progressivo stato versione serie
     *
     * @param idVerSerie id versamento serie
     *
     * @return progressivo
     */
    public BigDecimal getUltimoProgressivoSerStatoVerSerie(Long idVerSerie) {
        String queryStr = "SELECT MAX(u.pgStatoVerSerie) FROM SerStatoVerSerie u "
                + "WHERE u.serVerSerie.idVerSerie = :idVerSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idVerSerie", idVerSerie);
        return (BigDecimal) query.getSingleResult();
    }

    /**
     * Recupera l'ultimo progressivo stato serie
     *
     * @param idSerie id serie
     *
     * @return progressivo
     */
    public BigDecimal getUltimoProgressivoSerStatoSerie(Long idSerie) {
        String queryStr = "SELECT MAX(u.pgStatoSerie) FROM SerStatoSerie u "
                + "WHERE u.serSerie.idSerie = :idSerie ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idSerie", idSerie);
        return (BigDecimal) query.getSingleResult();
    }

    /**
     * Recupera gli ID delle UD che appartengono ad altre entità (elenchi versamento Fascicoli e/o
     * Serie) che con stato NON idoneo per il passaggio dell'ud allo stato di conservazione
     * IN_ARCHIVIO
     *
     * @param udIds            lista ID delle UD candidate
     * @param idSerieToExclude ID della Serie corrente (da escludere dai controlli)
     * @return Lista delle ud che sono "bloccate" in quanto appartenenti ad altre aggregazioni
     */
    public List<Long> findUdIdsBlockedForFirmaIndiceAipSerie(List<Long> udIds,
            Long idSerieToExclude) {
        if (udIds == null || udIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Stati Elenco di Versamento Fascicoli che non sono idonei al passaggio dell'ud allo stato
        // di conservazione IN_ARCHIVIO
        List<TiStatoElencoFasc> statiElencoBloccanti = Arrays.asList(
                TiStatoElencoFasc.IN_CODA_CREAZIONE_AIP, TiStatoElencoFasc.AIP_CREATI,
                TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO,
                TiStatoElencoFasc.ELENCO_INDICI_AIP_FIRMA_IN_CORSO);

        // Stati Serie che non sono idonei al passaggio dell'ud allo stato di conservazione
        // IN_ARCHIVIO
        List<String> statiSerieBloccanti = Arrays.asList("PRESA_IN_CARICO", "AIP_GENERATO");

        String hql = "SELECT DISTINCT ud.idUnitaDoc FROM AroUnitaDoc ud "
                + "WHERE ud.idUnitaDoc IN :udIds " + "AND ("

                // 1. Blocco da FASCICOLI (tramite Stato dell'Elenco di versamento fascicoli)
                + "   EXISTS (SELECT 1 FROM FasUnitaDocFascicolo udf, ElvStatoElencoVersFasc se "
                + "      JOIN udf.fasFascicolo f " + "      JOIN f.elvElencoVersFasc e "
                + "      WHERE udf.aroUnitaDoc = ud " + "      AND f.dtAnnull = :defaultAnnull "

                // Join manuale tra Elenco e il suo Stato Corrente
                + "      AND e.idStatoElencoVersFascCor = se.idStatoElencoVersFasc "

                // Verifica stati elenco bloccanti
                + "      AND se.tiStato IN :statiElencoBloccanti) "

                + "   OR "

                // 2. Blocco da ALTRE SERIE (tramite Stato Serie)
                + "   EXISTS (SELECT 1 FROM AroUdAppartVerSerie uds, SerStatoSerie ss "
                + "      JOIN uds.serContenutoVerSerie c " + "      JOIN c.serVerSerie vs "
                + "      JOIN vs.serSerie s " + "      WHERE uds.aroUnitaDoc = ud "
                + "      AND c.tiContenutoVerSerie = 'EFFETTIVO' "

                // Join manuale tra Serie e il suo Stato Corrente
                + "      AND s.idStatoSerieCor = ss.idStatoSerie "

                // Serie attiva
                + "      AND s.dtAnnul = :defaultAnnull "
                // Escludo la serie che sto firmando ora
                + "      AND s.idSerie != :idSerieToExclude "

                // Verifica stati serie bloccanti
                + "      AND ss.tiStatoSerie IN :statiSerieBloccanti) " + ")";

        Query query = getEntityManager().createQuery(hql);
        query.setParameter("udIds", udIds);
        query.setParameter("idSerieToExclude", idSerieToExclude);
        query.setParameter("statiElencoBloccanti", statiElencoBloccanti);
        query.setParameter("statiSerieBloccanti", statiSerieBloccanti);

        Calendar c = Calendar.getInstance();
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        query.setParameter("defaultAnnull", c.getTime());

        return query.getResultList();
    }
}
