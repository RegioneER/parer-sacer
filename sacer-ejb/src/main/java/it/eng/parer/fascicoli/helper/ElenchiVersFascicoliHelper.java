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

package it.eng.parer.fascicoli.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.longListFrom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFasc_;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.ElvStatoElencoVersFasc_;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli;
import it.eng.parer.viewEntity.ElvVRicElencoFasc;
import it.eng.parer.viewEntity.ElvVRicElencoFascByFas;
import it.eng.parer.viewEntity.ElvVRicElencoFascByStato;
import it.eng.parer.web.util.StringPadding;
import it.eng.spagoCore.error.EMFError;
import java.util.Arrays;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({
        "unchecked" })
@Stateless
@LocalBean
public class ElenchiVersFascicoliHelper extends GenericHelper {

    private static final Logger log = LoggerFactory
            .getLogger(ElenchiVersFascicoliHelper.class.getName());

    public List<ElvVRicElencoFasc> retrieveElvVRicElencoFascList(long idUserIam,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        return retrieveElvVRicElencoFascList(idUserIam, new Filtri(filtriElenchiVersFascicoli));
    }

    public List<ElvVRicElencoFasc> retrieveElvVRicElencoFascList(long idUserIam, Filtri filtri) {
        Query query = createElvVRicElencoFascQuery(
                "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFasc "
                        + "(u.id.idElencoVersFasc, u.tiStato, u.aaFascicolo, u.niFascVersElenco, u.dlMotivoChius, "
                        + "u.tsCreazioneElenco, u.dtChiusura, u.dtValidazione, u.idCriterioRaggrFasc, u.nmCriterioRaggr, "
                        + "u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo, u.dtFirma) "
                        + "FROM ElvVRicElencoFasc u WHERE u.idUserIam = :idUserIam ",
                filtri);
        setElvVRicElencoCommonParameters(query, idUserIam, filtri);
        List<ElvVRicElencoFasc> listaElenchiVersFascicoli = query.getResultList();
        return listaElenchiVersFascicoli;
    }

    public List<ElvVRicElencoFascByStato> retrieveElvVRicElencoFascByStatoList(long idUserIam,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        return retrieveElvVRicElencoFascByStatoList(idUserIam,
                new Filtri(filtriElenchiVersFascicoli));
    }

    public List<ElvVRicElencoFascByStato> retrieveElvVRicElencoFascByStatoList(long idUserIam,
            Filtri filtri) {
        Query query = createElvVRicElencoFascQuery(
                "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFascByStato "
                        + "(u.id.idElencoVersFasc, u.tiStato, u.aaFascicolo, u.niFascVersElenco, u.dlMotivoChius, "
                        + "u.tsCreazioneElenco, u.dtChiusura, u.dtValidazione, u.idCriterioRaggrFasc, u.nmCriterioRaggr, "
                        + "u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo, u.flAnnull, u.dtFirma) "
                        + "FROM ElvVRicElencoFascByStato u WHERE u.idUserIam = :idUserIam ",
                filtri);
        setElvVRicElencoCommonParameters(query, idUserIam, filtri);
        return query.getResultList();
    }

    public List<ElvVRicElencoFascByFas> retrieveElvVRicElencoFascByFasList(long idUserIam,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        return retrieveElvVRicElencoFascByFasList(idUserIam,
                new Filtri(filtriElenchiVersFascicoli));
    }

    public List<ElvVRicElencoFascByFas> retrieveElvVRicElencoFascByFasList(long idUserIam,
            Filtri filtri) {
        Query query = createElvVRicElencoFascQuery(
                "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFascByFas "
                        + "(u.id.idElencoVersFasc, u.tiStato, u.aaFascicoloElenco, u.niFascVersElenco, u.dlMotivoChius, "
                        + "u.tsCreazioneElenco, u.dtChiusura, u.dtValidazione, u.idCriterioRaggrFasc, u.nmCriterioRaggr, "
                        + "u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo, u.dtFirma) "
                        + "FROM ElvVRicElencoFascByFas u WHERE u.idUserIam = :idUserIam ",
                filtri);

        setElvVRicElencoCommonParameters(query, idUserIam, filtri);

        BigDecimal idTipoFascicolo = filtri.getIdTipoFascicolo();
        BigDecimal aaFascicolo = filtri.getAaFascicolo();
        String cdKeyFascicolo = filtri.getCdKeyFascicolo();
        BigDecimal aaFascicoloDa = filtri.getAaFascicoloDa();
        BigDecimal aaFascicoloA = filtri.getAaFascicoloA();
        String cdKeyFascicoloDa = filtri.getCdKeyFascicoloDa();
        String cdKeyFascicoloA = filtri.getCdKeyFascicoloA();
        String cdCompositoVoceTitol = filtri.getCdCompositoVoceTitol();
        if (idTipoFascicolo != null) {
            query.setParameter("idTipoFascicolo", idTipoFascicolo);
        }
        if (aaFascicolo != null) {
            query.setParameter("aaFascicolo", aaFascicolo);
        }
        if (cdKeyFascicolo != null) {
            query.setParameter("cdKeyFascicolo", cdKeyFascicolo);
        }
        if (aaFascicoloDa != null && aaFascicoloA != null) {
            query.setParameter("aaFascicoloDa", aaFascicoloDa);
            query.setParameter("aaFascicoloA", aaFascicoloA);
        }
        if (cdKeyFascicoloDa != null && cdKeyFascicoloA != null) {
            cdKeyFascicoloDa = StringPadding.padString(cdKeyFascicoloDa, "0", 12,
                    StringPadding.PADDING_LEFT);
            cdKeyFascicoloA = StringPadding.padString(cdKeyFascicoloA, "0", 12,
                    StringPadding.PADDING_LEFT);
            query.setParameter("cdKeyFascicoloDa", cdKeyFascicoloDa);
            query.setParameter("cdKeyFascicoloA", cdKeyFascicoloA);
        }
        if (cdCompositoVoceTitol != null) {
            query.setParameter("cdCompositoVoceTitol", cdCompositoVoceTitol);
        }

        /* ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "ELENCHI DI VERSAMENTO FASCICOLI" */
        return query.getResultList();
    }

    private void setElvVRicElencoCommonParameters(Query query, long idUserIam, Filtri filtri) {
        /* Recupero i campi da assegnare come parametri alla query */
        Date tsCreazioneElencoDa = null;
        Date tsCreazioneElencoA = null;
        Date tsFirmaElencoAipDa = null;
        Date tsFirmaElencoAipA = null;
        if (filtri.getCreazioneElencoDa() != null) {
            tsCreazioneElencoDa = new Date(filtri.getCreazioneElencoDa().getTime());
            if (filtri.getCreazioneElencoA() != null) {
                tsCreazioneElencoA = new Date(filtri.getCreazioneElencoA().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tsCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                tsCreazioneElencoA = calendar.getTime();
            } else {
                tsCreazioneElencoA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tsCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                tsCreazioneElencoA = calendar.getTime();
            }
        }

        if (filtri.getDataFirmaElencoAipDa() != null || filtri.getDataFirmaElencoAipA() != null) {

            // --- Gestione separata della Data Inizio ---
            if (filtri.getDataFirmaElencoAipDa() != null) {
                tsFirmaElencoAipDa = new Date(filtri.getDataFirmaElencoAipDa().getTime());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
                tsFirmaElencoAipDa = calendar.getTime();
            }

            // --- Gestione separata della Data Fine ---
            // 1. Determina la data di base (dal filtro o da oggi)
            Date dataFineBase = (filtri.getDataFirmaElencoAipA() != null)
                    ? filtri.getDataFirmaElencoAipA()
                    : new Date();

            // 2. Esegui la manipolazione UNA SOLA VOLTA
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataFineBase);
            calendar.add(Calendar.DATE, 1);
            tsFirmaElencoAipA = calendar.getTime();
        }

        /* Passaggio dei valori dei parametri di ricerca */
        query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
        BigDecimal idAmbiente = filtri.getIdAmbiente();

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        BigDecimal idEnte = filtri.getIdEnte();
        if (filtri.getIdEnte() != null) {
            query.setParameter("idEnte", idEnte);
        }

        BigDecimal idStrut = filtri.getIdStrut();
        if (filtri.getIdStrut() != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (filtri.getIdElencoVersFasc() != null) {
            query.setParameter("idElencoVersFasc", filtri.getIdElencoVersFasc());
        }

        if (filtri.getIdCriterioRaggrFasc() != null) {
            query.setParameter("idCriterioRaggrFasc", filtri.getIdCriterioRaggrFasc());
        }

        if (filtri.getTiStato() != null) {
            query.setParameter("tiStato", filtri.getTiStato());
        }

        if (tsCreazioneElencoDa != null && tsCreazioneElencoA != null) {
            query.setParameter("tsCreazioneElencoDa", tsCreazioneElencoDa, TemporalType.TIMESTAMP);
            query.setParameter("tsCreazioneElencoA", tsCreazioneElencoA, TemporalType.TIMESTAMP);
        }

        if (filtri.getNtElencoChiuso() != null) {
            query.setParameter("ntElencoChiuso",
                    "%" + filtri.getNtElencoChiuso().toUpperCase() + "%");
        }

        if (filtri.getNtIndiceElenco() != null) {
            query.setParameter("ntIndiceElenco",
                    "%" + filtri.getNtIndiceElenco().toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(filtri.getFlElencoStandard())) {
            query.setParameter("flElencoStandard", filtri.getFlElencoStandard());
        }
        if (tsFirmaElencoAipDa != null) {
            query.setParameter("tsFirmaElencoAipDa", tsFirmaElencoAipDa, TemporalType.TIMESTAMP);
        }
        if (tsFirmaElencoAipA != null) {
            query.setParameter("tsFirmaElencoAipA", tsFirmaElencoAipA, TemporalType.TIMESTAMP);
        }
    }

    // TODO: fdilorenzo, refactory per criteria api e metamodels
    private Query createElvVRicElencoFascQuery(String selectQuery, Filtri filtri) {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(selectQuery);

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI DI VERSAMENTO FASCICOLI */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno o
         * più errori di firma dell'elenco di versamento fascicolo, di più stati CHIUSO, in modo da
         * considerare solo l'ultimo registrato
         */
        queryStr.append(whereWord).append(
                "((u.dtChiusura IS NULL) OR (u.dtChiusura = (SELECT s.tsStato FROM ElvStatoElencoVersFasc s WHERE s.idStatoElencoVersFasc = u.idStatoElencoVersFascCor AND s.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc)) ");
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di più
         * stati CHIUSO, di molteplici stati FIRMATO o VALIDATO, in modo da considerare solo lo
         * stato di chiusura registrato prima della firma/validazione dell'elenco di versamento
         * fascicolo
         */
        queryStr.append(
                "OR ((u.dtFirma IS NOT NULL OR u.dtValidazione IS NOT NULL ) AND (u.dtChiusura = (SELECT MAX(s1.tsStato) FROM ElvStatoElencoVersFasc s1 WHERE s1.tiStato = :statoChiuso AND s1.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc)))) ");

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
        /*
         * NECESSARIO SOLO SE NELLE VISTE SI AGGIUNGE LA COLONNA DT_CREAZIONE_ELENCO_IX_AIP,
         * ALTRIMENTI BASTA UNA DISTINCT SU UN SOTTOINSIEME LIMITATO DI COLONNE.
         */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno o
         * più errori di firma dell'elenco indice AIP fascicolo, di più stati
         * ELENCO_INDICI_AIP_CREATO, in modo da considerare solo l'ultimo registrato
         */

        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di più
         * stati ELENCO_INDICI_AIP_CREATO, di molteplici stati COMPLETATO, in modo da considerare
         * solo lo stato di creazione indice aip registrato prima della firma dell'elenco indice AIP
         * fascicolo
         */
        // queryStr.append("OR ((u.dtFirma IS NOT NULL) AND (u.dtCreazioneElencoIxAip = (SELECT
        // MAX(sIdxAip1.tsStato)
        // FROM ElvStatoElencoVersFasc sIdxAip1 WHERE sIdxAip1.tiStato = :statoIdxAipCreato AND

        /* Inserimento nella query del filtro ID_AMBIENTE */
        BigDecimal idAmbiente = filtri.getIdAmbiente();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
        }

        /* Inserimento nella query del filtro ID_ENTE */
        BigDecimal idEnte = filtri.getIdEnte();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
        }

        /* Inserimento nella query del filtro ID_STRUT */
        BigDecimal idStrut = filtri.getIdStrut();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
        }

        /* Inserimento nella query del filtro ID ELENCO VERS FASC */
        if (filtri.getIdElencoVersFasc() != null) {
            queryStr.append(whereWord).append("u.id.idElencoVersFasc = :idElencoVersFasc ");
        }

        /* Inserimento nella query del filtro TI_STATO */
        if (filtri.getTiStato() != null) {
            queryStr.append(whereWord).append("u.tiStato = :tiStato ");
        }

        Date tsCreazioneElencoDa = null;
        Date tsCreazioneElencoA = null;
        Date tsFirmaElencoAipDa = null;
        Date tsFirmaElencoAipA = null;
        if (filtri.getCreazioneElencoDa() != null) {
            tsCreazioneElencoDa = new Date(filtri.getCreazioneElencoDa().getTime());
            if (filtri.getCreazioneElencoA() != null) {
                tsCreazioneElencoA = new Date(filtri.getCreazioneElencoA().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tsCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                tsCreazioneElencoA = calendar.getTime();
            } else {
                tsCreazioneElencoA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(tsCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                tsCreazioneElencoA = calendar.getTime();
            }
        }
        if (filtri.getDataFirmaElencoAipDa() != null || filtri.getDataFirmaElencoAipA() != null) {

            // --- Gestione separata della Data Inizio ---
            if (filtri.getDataFirmaElencoAipDa() != null) {
                tsFirmaElencoAipDa = new Date(filtri.getDataFirmaElencoAipDa().getTime());
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
                tsFirmaElencoAipDa = calendar.getTime();
            }

            // --- Gestione separata della Data Fine ---
            // 1. Determina la data di base (dal filtro o da oggi)
            Date dataFineBase = (filtri.getDataFirmaElencoAipA() != null)
                    ? filtri.getDataFirmaElencoAipA()
                    : new Date();

            // 2. Esegui la manipolazione UNA SOLA VOLTA
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataFineBase);
            calendar.add(Calendar.DATE, 1);
            tsFirmaElencoAipA = calendar.getTime();
        }

        if ((tsCreazioneElencoDa != null) && (tsCreazioneElencoA != null)) {
            queryStr.append(whereWord).append(
                    "(u.tsCreazioneElenco between :tsCreazioneElencoDa AND :tsCreazioneElencoA) ");
        }

        /* Inserimento nella query del filtro NT_ELENCO_CHIUSO */
        if (filtri.getNtElencoChiuso() != null) {
            queryStr.append(whereWord).append("UPPER(u.ntElencoChiuso) LIKE :ntElencoChiuso ");
        }

        /* Inserimento nella query del filtro NT_INDICE_ELENCO */
        if (filtri.getNtIndiceElenco() != null) {
            queryStr.append(whereWord).append("UPPER(u.ntIndiceElenco) LIKE :ntIndiceElenco ");
        }

        if (filtri.getIdTipoFascicolo() != null) {
            queryStr.append(whereWord).append("u.idTipoFascicolo = :idTipoFascicolo ");
        }

        /* Inserimento nella query del filtro CHIAVE FASCICOLO */
        if (filtri.getAaFascicolo() != null) {
            queryStr.append(whereWord).append("u.aaFascicolo = :aaFascicolo ");
        }

        if (filtri.getCdKeyFascicolo() != null) {
            queryStr.append(whereWord).append("u.cdKeyFascicolo = :cdKeyFascicolo ");
        }

        /* Inserimento nella query del filtro CHIAVE FASCICOLO per range */
        if (filtri.getAaFascicoloDa() != null && filtri.getAaFascicoloA() != null) {
            queryStr.append(whereWord)
                    .append("u.aaFascicolo BETWEEN :aaFascicoloDa AND :aaFascicoloA ");
        }

        if (filtri.getCdKeyFascicoloDa() != null && filtri.getCdKeyFascicoloA() != null) {
            queryStr.append(whereWord).append(
                    "LPAD( u.cdKeyFascicolo, 12, '0') BETWEEN :cdKeyFascicoloDa AND :cdKeyFascicoloA ");
        }

        if (filtri.getCdCompositoVoceTitol() != null) {
            queryStr.append(whereWord)
                    .append("u.decVoceTitol.cdCompositoVoceTitol = :cdCompositoVoceTitol ");
        }

        /* Inserimento nella query del filtro ID_CRITERIO_RAGGR_FASC */
        if (filtri.getIdCriterioRaggrFasc() != null) {
            queryStr.append(whereWord).append("u.idCriterioRaggrFasc = :idCriterioRaggrFasc ");
        }

        if (StringUtils.isNotBlank(filtri.getFlElencoStandard())) {
            queryStr.append(whereWord).append("u.flElencoStandard = :flElencoStandard ");
        }

        if (tsFirmaElencoAipDa != null) {
            queryStr.append(whereWord).append("(u.dtFirma >= :tsFirmaElencoAipDa) ");
        }
        if (tsFirmaElencoAipA != null) {
            queryStr.append(whereWord).append("(u.dtFirma <= :tsFirmaElencoAipA) ");
        }

        /* Ordina per idElencoVersFasc decrescente */
        queryStr.append(" ORDER BY u.id.idElencoVersFasc DESC");
        /* CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER */
        return getEntityManager().createQuery(queryStr.toString());
    }

    // TODO: fdilorenzo, refactory per criteria api e metamodels
    public List<ElvVRicElencoFascByStato> getListaElenchiVersFascicoliDaFirmare(
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal idElencoVersFasc, String note, ElencoEnums.ElencoStatusEnum tiStato,
            Date[] dateCreazioneElencoFascValidate, long idUserIam) {
        String queryStr = "SELECT u FROM ElvVRicElencoFascByStato u "
                + "WHERE u.tiStato = :tiStato AND u.idUserIam = :idUserIam ";

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI DI VERSAMENTO FASCICOLI */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di un
         * errore di firma dell'elenco di versamento fascicolo, di un secondo stato CHIUSO, in modo
         * da considerare solo l'ultimo registrato
         */
        queryStr = queryStr.concat(
                "AND (u.dtChiusura = (SELECT MAX(s.tsStato) FROM ElvStatoElencoVersFasc s WHERE s.tiStato = :statoChiuso AND s.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc)) ");

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di un
         * errore di firma dell'elenco indice AIP fascicolo, di più stati ELENCO_INDICI_AIP_CREATO,
         * in modo da considerare solo l'ultimo registrato
         */
        if (tiStato.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO)) {
            queryStr = queryStr.concat(
                    "AND (u.dtCreazioneElencoIxAip = (SELECT MAX(sIdxAip.tsStato) FROM ElvStatoElencoVersFasc sIdxAip WHERE sIdxAip.tiStato = :statoIdxAipCreato AND sIdxAip.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc)) ");
        }

        if (idAmbiente != null) {
            queryStr = queryStr.concat("AND u.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr = queryStr.concat("AND u.idEnte = :idEnte ");
        }
        if (idStrut != null) {
            queryStr = queryStr.concat("AND u.idStrut = :idStrut ");
        }
        if (idElencoVersFasc != null) {
            queryStr = queryStr.concat("AND u.idElencoVersFasc = :idElencoVersFasc ");
        }
        if (note != null) {
            queryStr = ("1".equals(note))
                    ? queryStr.concat(
                            "AND (u.ntElencoChiuso IS NOT NULL OR u.ntIndiceElenco IS NOT NULL) ")
                    : queryStr
                            .concat("AND (u.ntElencoChiuso IS NULL AND u.ntIndiceElenco IS NULL) ");
        }

        Date data_da = (dateCreazioneElencoFascValidate != null ? dateCreazioneElencoFascValidate[0]
                : null);
        Date data_a = (dateCreazioneElencoFascValidate != null ? dateCreazioneElencoFascValidate[1]
                : null);

        if ((data_da != null) && (data_a != null)) {
            if (tiStato.equals(ElencoEnums.ElencoStatusEnum.CHIUSO)) {
                queryStr = queryStr.concat(
                        "AND (u.tsCreazioneElenco >= :datada AND u.tsCreazioneElenco <= :dataa) ");
            } else {
                queryStr = queryStr.concat(
                        "AND (u.dtCreazioneElencoIxAip >= :datada AND u.dtCreazioneElencoIxAip <= :dataa) ");
            }
        }

        queryStr = queryStr.concat("ORDER BY u.dtChiusura ASC ");

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("tiStato", tiStato.name());
        query.setParameter("idUserIam", bigDecimalFromLong(idUserIam));
        query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
        if (tiStato.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO)) {
            query.setParameter("statoIdxAipCreato", TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO);
        }
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (idElencoVersFasc != null) {
            query.setParameter("idElencoVersFasc", idElencoVersFasc);
        }
        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
        }
        return query.getResultList();
    }

    public List<ElvVRicElencoFascByStato> getListaElenchiVersFascicoliDaFirmare(
            List<BigDecimal> idElencoVersFascList, Long idUserIam) {
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = null;
        if (idElencoVersFascList != null && !idElencoVersFascList.isEmpty() && idUserIam != null) {
            String queryStr = "SELECT u FROM ElvVRicElencoFascByStato u "
                    + "WHERE u.id.idElencoVersFasc IN (:idElencoVersFascList) "
                    + "AND u.idUserIam = :idUserIam";
            /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI DI VERSAMENTO FASCICOLI */
            /*
             * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno
             * o più errori di firma dell'elenco di versamento fascicolo, di più stati CHIUSO, in
             * modo da considerare solo l'ultimo registrato
             */
            queryStr = queryStr.concat(" AND ").concat(
                    "((u.dtChiusura IS NULL) OR (u.dtChiusura = (SELECT MAX(s.tsStato) FROM ElvStatoElencoVersFasc s WHERE s.tiStato = :statoChiuso AND s.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc))) ");

            /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
            /*
             * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno
             * o più errori di firma dell'elenco indice AIP fascicolo, di più stati
             * ELENCO_INDICI_AIP_CREATO, in modo da considerare solo l'ultimo registrato
             */
            queryStr = queryStr.concat(" AND ").concat(
                    "((u.dtCreazioneElencoIxAip IS NULL) OR (u.dtCreazioneElencoIxAip = (SELECT MAX(sIdxAip.tsStato) FROM ElvStatoElencoVersFasc sIdxAip WHERE sIdxAip.tiStato = :statoIdxAipCreato AND sIdxAip.elvElencoVersFasc.idElencoVersFasc = u.id.idElencoVersFasc))) ");

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idElencoVersFascList", idElencoVersFascList);
            query.setParameter("idUserIam", new BigDecimal(idUserIam));
            query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
            query.setParameter("statoIdxAipCreato", TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO);
            listaElenchiVersFascicoli = query.getResultList();
        }
        return listaElenchiVersFascicoli;
    }

    public boolean isElencoDeletable(BigDecimal idElencoVersFasc) {
        String queryStr = "SELECT sf FROM ElvStatoElencoVersFasc sf "
                + "WHERE sf.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf.tiStato IN (:statoElencoDeletable) "
                + "AND NOT EXISTS (SELECT sf2 FROM ElvStatoElencoVersFasc sf2 "
                + "WHERE sf2.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf2.tiStato IN (:statoElencoNotDeletable))";
        Query query = getEntityManager().createQuery(queryStr);
        List<TiStatoElencoFasc> statoElencoDeletable = new ArrayList<>();
        statoElencoDeletable.add(TiStatoElencoFasc.CHIUSO);
        statoElencoDeletable.add(TiStatoElencoFasc.DA_CHIUDERE);
        statoElencoDeletable.add(TiStatoElencoFasc.APERTO);
        List<TiStatoElencoFasc> statoElencoNotDeletable = new ArrayList<>();
        statoElencoNotDeletable.add(TiStatoElencoFasc.FIRMATO);
        statoElencoNotDeletable.add(TiStatoElencoFasc.FIRMA_IN_CORSO);

        query.setParameter("idElencoVersFasc", longFromBigDecimal(idElencoVersFasc));
        query.setParameter("statoElencoDeletable", statoElencoDeletable);
        query.setParameter("statoElencoNotDeletable", statoElencoNotDeletable);
        return !query.getResultList().isEmpty();
    }

    public boolean isElencoClosable(BigDecimal idElencoVersFasc) {
        String queryStr = "SELECT sf FROM ElvStatoElencoVersFasc sf "
                + "WHERE sf.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf.tiStato = :statoElencoClosable "
                + "AND NOT EXISTS (SELECT sf2 FROM ElvStatoElencoVersFasc sf2 "
                + "WHERE sf2.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf2.tiStato IN (:statoElencoFascNotClosable))";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFasc", longFromBigDecimal(idElencoVersFasc));
        query.setParameter("statoElencoClosable", TiStatoElencoFasc.APERTO);

        List<TiStatoElencoFasc> statoElencoFascNotClosable = new ArrayList<>();
        statoElencoFascNotClosable.add(TiStatoElencoFasc.CHIUSO);
        statoElencoFascNotClosable.add(TiStatoElencoFasc.DA_CHIUDERE);
        query.setParameter("statoElencoFascNotClosable", statoElencoFascNotClosable);

        List<ElvStatoElencoVersFasc> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            return !elenchi.get(0).getElvElencoVersFasc().getFasFascicoli().isEmpty();
        }
    }

    // TIP: fdilorenzo, test criteria api con metamodels
    public boolean isElencoClosable2(BigDecimal idElencoVersFasc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<ElvStatoElencoVersFasc> query = cb.createQuery(ElvStatoElencoVersFasc.class);
        Root<ElvStatoElencoVersFasc> sf = query.from(ElvStatoElencoVersFasc.class);

        Path<ElvElencoVersFasc> elvElencoVersFasc = sf
                .get(ElvStatoElencoVersFasc_.elvElencoVersFasc);
        Path<Long> id = elvElencoVersFasc.get(ElvElencoVersFasc_.idElencoVersFasc);
        Predicate pOnIdElencoVersFasc = cb.in(id).value(idElencoVersFasc.longValue());
        Predicate pOnTiStato = cb.in(sf.get(ElvStatoElencoVersFasc_.tiStato))
                .value(TiStatoElencoFasc.APERTO);

        Subquery<ElvStatoElencoVersFasc> subquery = query.subquery(ElvStatoElencoVersFasc.class);
        Root<ElvStatoElencoVersFasc> sf2 = subquery.from(ElvStatoElencoVersFasc.class);

        Path<ElvElencoVersFasc> elvElencoVersFasc2 = sf2
                .get(ElvStatoElencoVersFasc_.elvElencoVersFasc);
        Path<Long> id2 = elvElencoVersFasc2.get(ElvElencoVersFasc_.idElencoVersFasc);
        Predicate pOnIdElencoVersFasc2 = cb.in(id2).value(idElencoVersFasc.longValue());
        Predicate pOnTiStato2 = cb.in(sf2.get(ElvStatoElencoVersFasc_.tiStato))
                .value(TiStatoElencoFasc.CHIUSO).value(TiStatoElencoFasc.DA_CHIUDERE);
        Predicate pOnSubQuery = cb.and(pOnIdElencoVersFasc2, pOnTiStato2);

        subquery.where(pOnSubQuery);
        subquery.select(sf2);

        Predicate pOnNotExists = cb.not(cb.exists(subquery));
        Predicate pOnQuery = cb.and(pOnIdElencoVersFasc, pOnTiStato, pOnNotExists);

        query.where(pOnQuery);
        query.select(sf);

        Query qry = getEntityManager().createQuery(query);

        List<ElvStatoElencoVersFasc> elenchi = qry.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            return !elenchi.get(0).getElvElencoVersFasc().getFasFascicoli().isEmpty();
        }
    }

    public boolean areFascDeletables(BigDecimal idElencoVersFasc) {
        String queryStr = "SELECT sf FROM ElvStatoElencoVersFasc sf "
                + "WHERE sf.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf.tiStato IN (:statoElencoFascDeletables) "
                + "AND NOT EXISTS (SELECT sf2 FROM ElvStatoElencoVersFasc sf2 "
                + "WHERE sf2.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf2.tiStato = :statoElencoFascNotDeletables)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFasc", longFromBigDecimal(idElencoVersFasc));
        List<TiStatoElencoFasc> statoElencoFascDeletables = new ArrayList<>();
        statoElencoFascDeletables.add(TiStatoElencoFasc.APERTO);
        statoElencoFascDeletables.add(TiStatoElencoFasc.DA_CHIUDERE);
        query.setParameter("statoElencoFascDeletables", statoElencoFascDeletables);
        query.setParameter("statoElencoFascNotDeletables", TiStatoElencoFasc.CHIUSO);
        List<ElvStatoElencoVersFasc> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            return !elenchi.get(0).getElvElencoVersFasc().getFasFascicoli().isEmpty();
        }
    }

    public boolean areAllElenchiNonPresentiFirmati(List<BigDecimal> idElencoVersFascSelezionatiList,
            Date dataChiusura, BigDecimal idStrut) {
        String queryStr = "SELECT COUNT(elab) FROM ElvElencoVersFascDaElab elab JOIN elab.elvElencoVersFasc u "
                + "WHERE u.dtScadChius < :dataChiusura "
                + "AND elab.elvElencoVersFasc.idElencoVersFasc NOT IN (:idElencoVersFascSelezionatiList) "
                + "AND elab.tiStato = :tiStato " + "AND elab.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFascSelezionatiList",
                longListFrom(idElencoVersFascSelezionatiList));
        query.setParameter("dataChiusura", dataChiusura);
        query.setParameter("tiStato", TiStatoElencoFascDaElab.CHIUSO);
        query.setParameter("idStrut", idStrut);
        return (Long) query.getSingleResult() == 0;
    }

    public boolean existIdElenco(BigDecimal idElencoVersFasc, BigDecimal idStrut) {
        String queryStr = "SELECT u FROM ElvElencoVersFasc u "
                + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.id.idElencoVersFasc = :idElencoVersFasc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFasc", longFromBigDecimal(idElencoVersFasc));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        return !query.getResultList().isEmpty();
    }

    /**
     * @deprecated (i parametri idUserIam e operList non vengono mai usati, utilizzare il metodo
     *             senza questi parametri
     *
     * @param idUserIam        NON USATO
     * @param idElencoVersFasc id elenco versamento
     * @param ntIndiceElenco   note indice elenco
     * @param ntElencoChiuso   note elenco chiuso
     * @param operList         NON USATO
     */
    @Deprecated
    public void saveNote(Long idUserIam, BigDecimal idElencoVersFasc, String ntIndiceElenco,
            String ntElencoChiuso, List<ElencoEnums.OpTypeEnum> operList) {
        saveNote(idElencoVersFasc, ntIndiceElenco, ntElencoChiuso);
    }

    public void saveNote(BigDecimal idElencoVersFasc, String ntIndiceElenco,
            String ntElencoChiuso) {
        ElvElencoVersFasc elenco = getEntityManager().find(ElvElencoVersFasc.class,
                idElencoVersFasc.longValue());
        elenco.setNtIndiceElenco(ntIndiceElenco);
        elenco.setNtElencoChiuso(ntElencoChiuso);
        /* TODO: verificare, A seconda di cosa ho modificato, scrivo nel log */
    }

    public List<ElvStatoElencoVersFasc> retrieveStatiElencoByElencoVersFasc(
            BigDecimal idElvElencoVersFasc) {
        List<ElvStatoElencoVersFasc> result = null;
        try {
            ElvElencoVersFasc elenco = getEntityManager().find(ElvElencoVersFasc.class,
                    idElvElencoVersFasc.longValue());
            Query query = getEntityManager().createNamedQuery(
                    "ElvStatoElencoVersFasc.findByElencoVersFasc", ElvStatoElencoVersFasc.class);
            query.setParameter("elvElencoVersFasc", elenco);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            log.error("Errore nell'estrazione degli stati", ex);
            throw ex;
        }
        return result;
    }

    static class Filtri {
        BigDecimal idElencoVersFasc;
        String tiStato;
        Timestamp creazioneElencoDa;
        Timestamp creazioneElencoA;
        String ntElencoChiuso;
        String ntIndiceElenco;
        BigDecimal idTipoFascicolo;
        BigDecimal aaFascicolo;
        String cdKeyFascicolo;
        BigDecimal aaFascicoloDa;
        BigDecimal aaFascicoloA;
        String cdKeyFascicoloDa;
        String cdKeyFascicoloA;
        String cdCompositoVoceTitol;
        BigDecimal idCriterioRaggrFasc;
        String flElencoStandard;
        private BigDecimal idAmbiente;
        private BigDecimal idEnte;
        private BigDecimal idStrut;
        Timestamp dataFirmaElencoAipDa;
        Timestamp dataFirmaElencoAipA;

        Filtri(FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
            idElencoVersFasc = filtriElenchiVersFascicoli.getId_elenco_vers_fasc().parse();
            tiStato = filtriElenchiVersFascicoli.getTi_stato().parse();
            creazioneElencoDa = filtriElenchiVersFascicoli.getTs_creazione_elenco_da().parse();
            creazioneElencoA = filtriElenchiVersFascicoli.getTs_creazione_elenco_a().parse();
            ntElencoChiuso = filtriElenchiVersFascicoli.getNt_elenco_chiuso().parse();
            ntIndiceElenco = filtriElenchiVersFascicoli.getNt_indice_elenco().parse();
            idTipoFascicolo = filtriElenchiVersFascicoli.getId_tipo_fascicolo().parse();
            aaFascicolo = filtriElenchiVersFascicoli.getAa_fascicolo().parse();
            cdKeyFascicolo = filtriElenchiVersFascicoli.getCd_key_fascicolo().parse();
            aaFascicoloDa = filtriElenchiVersFascicoli.getAa_fascicolo_da().parse();
            aaFascicoloA = filtriElenchiVersFascicoli.getAa_fascicolo_a().parse();
            cdKeyFascicoloDa = filtriElenchiVersFascicoli.getCd_key_fascicolo_da().parse();
            cdKeyFascicoloA = filtriElenchiVersFascicoli.getCd_key_fascicolo_a().parse();
            cdCompositoVoceTitol = filtriElenchiVersFascicoli.getCd_composito_voce_titol().parse();
            idCriterioRaggrFasc = filtriElenchiVersFascicoli.getId_criterio_raggr_fasc().parse();
            flElencoStandard = filtriElenchiVersFascicoli.getFl_elenco_standard().parse();
            idAmbiente = filtriElenchiVersFascicoli.getId_ambiente().parse();
            idEnte = filtriElenchiVersFascicoli.getId_ente().parse();
            idStrut = filtriElenchiVersFascicoli.getId_strut().parse();
            dataFirmaElencoAipDa = filtriElenchiVersFascicoli.getDt_firma_elenco_ix_aip_da()
                    .parse();
            dataFirmaElencoAipA = filtriElenchiVersFascicoli.getDt_firma_elenco_ix_aip_a().parse();
        }

        public BigDecimal getIdElencoVersFasc() {
            return idElencoVersFasc;
        }

        void setIdElencoVersFasc(BigDecimal idElencoVersFasc) {
            this.idElencoVersFasc = idElencoVersFasc;
        }

        public String getTiStato() {
            return tiStato;
        }

        void setTiStato(String tiStato) {
            this.tiStato = tiStato;
        }

        public Timestamp getCreazioneElencoDa() {
            return creazioneElencoDa;
        }

        void setCreazioneElencoDa(Timestamp creazioneElencoDa) {
            this.creazioneElencoDa = creazioneElencoDa;
        }

        public Timestamp getCreazioneElencoA() {
            return creazioneElencoA;
        }

        void setCreazioneElencoA(Timestamp creazioneElencoA) {
            this.creazioneElencoA = creazioneElencoA;
        }

        public String getNtElencoChiuso() {
            return ntElencoChiuso;
        }

        void setNtElencoChiuso(String ntElencoChiuso) {
            this.ntElencoChiuso = ntElencoChiuso;
        }

        public String getNtIndiceElenco() {
            return ntIndiceElenco;
        }

        void setNtIndiceElenco(String ntIndiceElenco) {
            this.ntIndiceElenco = ntIndiceElenco;
        }

        public BigDecimal getIdTipoFascicolo() {
            return idTipoFascicolo;
        }

        void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
            this.idTipoFascicolo = idTipoFascicolo;
        }

        public BigDecimal getAaFascicolo() {
            return aaFascicolo;
        }

        void setAaFascicolo(BigDecimal aaFascicolo) {
            this.aaFascicolo = aaFascicolo;
        }

        public String getCdKeyFascicolo() {
            return cdKeyFascicolo;
        }

        void setCdKeyFascicolo(String cdKeyFascicolo) {
            this.cdKeyFascicolo = cdKeyFascicolo;
        }

        public BigDecimal getAaFascicoloDa() {
            return aaFascicoloDa;
        }

        void setAaFascicoloDa(BigDecimal aaFascicoloDa) {
            this.aaFascicoloDa = aaFascicoloDa;
        }

        public BigDecimal getAaFascicoloA() {
            return aaFascicoloA;
        }

        void setAaFascicoloA(BigDecimal aaFascicoloA) {
            this.aaFascicoloA = aaFascicoloA;
        }

        public String getCdKeyFascicoloDa() {
            return cdKeyFascicoloDa;
        }

        void setCdKeyFascicoloDa(String cdKeyFascicoloDa) {
            this.cdKeyFascicoloDa = cdKeyFascicoloDa;
        }

        public String getCdKeyFascicoloA() {
            return cdKeyFascicoloA;
        }

        void setCdKeyFascicoloA(String cdKeyFascicoloA) {
            this.cdKeyFascicoloA = cdKeyFascicoloA;
        }

        public String getCdCompositoVoceTitol() {
            return cdCompositoVoceTitol;
        }

        void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
            this.cdCompositoVoceTitol = cdCompositoVoceTitol;
        }

        public BigDecimal getIdCriterioRaggrFasc() {
            return idCriterioRaggrFasc;
        }

        void setIdCriterioRaggrFasc(BigDecimal idCriterioRaggrFasc) {
            this.idCriterioRaggrFasc = idCriterioRaggrFasc;
        }

        public String getFlElencoStandard() {
            return flElencoStandard;
        }

        void setFlElencoStandard(String flElencoStandard) {
            this.flElencoStandard = flElencoStandard;
        }

        Filtri() {
        }

        public BigDecimal getIdAmbiente() {
            return idAmbiente;
        }

        void setIdAmbiente(BigDecimal idAmbiente) {
            this.idAmbiente = idAmbiente;
        }

        public BigDecimal getIdEnte() {
            return idEnte;
        }

        void setIdEnte(BigDecimal idEnte) {
            this.idEnte = idEnte;
        }

        public BigDecimal getIdStrut() {
            return idStrut;
        }

        void setIdStrut(BigDecimal idStrut) {
            this.idStrut = idStrut;
        }

        public Timestamp getDataFirmaElencoAipDa() {
            return dataFirmaElencoAipDa;
        }

        public Timestamp getDataFirmaElencoAipA() {
            return dataFirmaElencoAipA;
        }

        void setDataFirmaElencoAipDa(Timestamp dataFirmaElencoAipDa) {
            this.dataFirmaElencoAipDa = dataFirmaElencoAipDa;
        }

        void setDataFirmaElencoAipA(Timestamp dataFirmaElencoAipA) {
            this.dataFirmaElencoAipA = dataFirmaElencoAipA;
        }
    }

    // MAC #39492
    /**
     * Metodo per "filtrare" le ud di un fascicolo, restituendo quelle che hanno legami con altre
     * entità (elenchi versamento fascicoli e/o serie) con stati "bloccanti" per il passaggio di
     * stato a IN_ARCHIO dell'ud
     *
     * @param udIds       la lista delle ud in ingresso
     * @param idFascicolo il fascicolo che sto analizzando
     * @return la lista delle ud "bloccate"
     */
    public List<Long> findUdIdsBlockedByAltreEntita(List<Long> udIds, Long idFascicolo) {
        if (udIds == null || udIds.isEmpty()) {
            return new ArrayList<>();
        }

        // Stati Elenco di versamento fascicoli che bloccano il passaggio ud da
        // VERSAMENTO_IN_ARCHIVIO a IN_ARCHIVIO
        List<TiStatoElencoFasc> statiElencoBloccanti = Arrays.asList(
                TiStatoElencoFasc.IN_CODA_CREAZIONE_AIP, TiStatoElencoFasc.AIP_CREATI,
                TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO,
                TiStatoElencoFasc.ELENCO_INDICI_AIP_FIRMA_IN_CORSO);

        // Stati Serie che bloccano il passaggio ud da VERSAMENTO_IN_ARCHIVIO a IN_ARCHIVIO
        List<String> statiSerieBloccanti = Arrays.asList("PRESA_IN_CARICO", "AIP_GENERATO");

        String hql = "SELECT DISTINCT ud.idUnitaDoc FROM AroUnitaDoc ud "
                + "WHERE ud.idUnitaDoc IN :udIds " + "AND ("

                // 1. Blocco da ALTRI FASCICOLI (tramite Stato dell'Elenco di versamento fascicoli)
                // Join: UD -> Fascicolo -> Elenco -> Stato Elenco Corrente
                + "   EXISTS (SELECT 1 FROM FasUnitaDocFascicolo udf, ElvStatoElencoVersFasc se "
                + "      JOIN udf.fasFascicolo f " + "      JOIN f.elvElencoVersFasc e "
                + "      WHERE udf.aroUnitaDoc = ud "

                // Escludo il fascicolo corrente
                + "      AND f.idFascicolo != :idFascicolo "
                + "      AND f.dtAnnull = :defaultAnnull "

                // Join manuale tra Elenco e il suo Stato Corrente
                + "      AND e.idStatoElencoVersFascCor = se.idStatoElencoVersFasc "

                // Verifica stati elenco bloccanti
                + "      AND se.tiStato IN :statiElencoBloccanti) "

                + "   OR "

                // 2. Blocco da SERIE (tramite Stato Serie)
                // Join: UD -> Appartenenza -> Contenuto -> Versione -> Serie -> Stato Serie
                // Corrente
                + "   EXISTS (SELECT 1 FROM AroUdAppartVerSerie uds, SerStatoSerie ss "
                + "      JOIN uds.serContenutoVerSerie c " + "      JOIN c.serVerSerie vs "
                + "      JOIN vs.serSerie s " + "      WHERE uds.aroUnitaDoc = ud "
                + "      AND c.tiContenutoVerSerie = 'EFFETTIVO' "

                // Join manuale tra Serie e il suo Stato Corrente
                + "      AND s.idStatoSerieCor = ss.idStatoSerie "

                // Serie attiva
                + "      AND s.dtAnnul = :defaultAnnull "

                // Verifica stati serie bloccanti
                + "      AND ss.tiStatoSerie IN :statiSerieBloccanti) " + ")";

        Query query = getEntityManager().createQuery(hql);
        query.setParameter("udIds", udIds);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("statiElencoBloccanti", statiElencoBloccanti);
        query.setParameter("statiSerieBloccanti", statiSerieBloccanti);

        Calendar c = Calendar.getInstance();
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        query.setParameter("defaultAnnull", c.getTime());

        return query.getResultList();
    }

    // end MAC #39492

}
