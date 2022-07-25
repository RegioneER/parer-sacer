package it.eng.parer.fascicoli.helper;

import it.eng.parer.web.helper.*;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFasc_;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.ElvStatoElencoVersFasc_;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli;
import it.eng.parer.viewEntity.ElvVRicElencoFascByFas;
import it.eng.parer.viewEntity.ElvVRicElencoFascByStato;
import it.eng.parer.viewEntity.ElvVRicElencoFasc;
import it.eng.parer.web.util.StringPadding;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
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

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class ElenchiVersFascicoliHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(VolumiHelper.class.getName());

    public List<ElvVRicElencoFasc> retrieveElvVRicElencoFascList(long idUserIam,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        Query query = createElvVRicElencoFascQuery("SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFasc "
                + "(u.idElencoVersFasc, u.tiStato, u.aaFascicolo, u.niFascVersElenco, u.dlMotivoChius, "
                + "u.tsCreazioneElenco, u.dtChiusura, u.dtFirma, u.idCriterioRaggrFasc, u.nmCriterioRaggr, "
                + "u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo) "
                + "FROM ElvVRicElencoFasc u WHERE u.idUserIam = :idUserIam ", filtriElenchiVersFascicoli);
        setElvVRicElencoCommonParameters(filtriElenchiVersFascicoli, query, idUserIam);
        List<ElvVRicElencoFasc> listaElenchiVersFascicoli = query.getResultList();
        return listaElenchiVersFascicoli;
    }

    public List<ElvVRicElencoFascByStato> retrieveElvVRicElencoFascByStatoList(long idUserIam,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        Query query = createElvVRicElencoFascQuery(
                "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFascByStato "
                        + "(u.idElencoVersFasc, u.tiStato, u.aaFascicolo, u.niFascVersElenco, u.dlMotivoChius, "
                        + "u.tsCreazioneElenco, u.dtChiusura, u.dtFirma, u.idCriterioRaggrFasc, u.nmCriterioRaggr, "
                        + "u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo, u.flAnnull) "
                        + "FROM ElvVRicElencoFascByStato u WHERE u.idUserIam = :idUserIam ",
                filtriElenchiVersFascicoli);
        setElvVRicElencoCommonParameters(filtriElenchiVersFascicoli, query, idUserIam);
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = query.getResultList();
        return listaElenchiVersFascicoli;
    }

    public List<ElvVRicElencoFascByFas> retrieveElvVRicElencoFascByFasList(long idUserIam,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        Query query = createElvVRicElencoFascQuery("SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoFascByFas "
                + "(u.idElencoVersFasc, u.tiStato, u.aaFascicoloElenco, u.niFascVersElenco, u.dlMotivoChius, "
                + "u.tsCreazioneElenco, u.dtChiusura, u.dtFirma, u.idCriterioRaggrFasc, u.nmCriterioRaggr, "
                + "u.ntElencoChiuso, u.ntIndiceElenco, u.nmAmbiente, u.nmEnte, u.nmStrut, u.flElencoStandard, u.cdVoceTitol, u.nmTipoFascicolo) "
                + "FROM ElvVRicElencoFascByFas u WHERE u.idUserIam = :idUserIam ", filtriElenchiVersFascicoli);

        setElvVRicElencoCommonParameters(filtriElenchiVersFascicoli, query, idUserIam);

        BigDecimal idTipoFascicolo = filtriElenchiVersFascicoli.getId_tipo_fascicolo().parse();
        BigDecimal aaFascicolo = filtriElenchiVersFascicoli.getAa_fascicolo().parse();
        String cdKeyFascicolo = filtriElenchiVersFascicoli.getCd_key_fascicolo().parse();
        BigDecimal aaFascicoloDa = filtriElenchiVersFascicoli.getAa_fascicolo_da().parse();
        BigDecimal aaFascicoloA = filtriElenchiVersFascicoli.getAa_fascicolo_a().parse();
        String cdKeyFascicoloDa = filtriElenchiVersFascicoli.getCd_key_fascicolo_da().parse();
        String cdKeyFascicoloA = filtriElenchiVersFascicoli.getCd_key_fascicolo_a().parse();
        String cdCompositoVoceTitol = filtriElenchiVersFascicoli.getCd_composito_voce_titol().parse();
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
            cdKeyFascicoloDa = StringPadding.padString(cdKeyFascicoloDa, "0", 12, StringPadding.PADDING_LEFT);
            cdKeyFascicoloA = StringPadding.padString(cdKeyFascicoloA, "0", 12, StringPadding.PADDING_LEFT);
            query.setParameter("cdKeyFascicoloDa", cdKeyFascicoloDa);
            query.setParameter("cdKeyFascicoloA", cdKeyFascicoloA);
        }
        if (cdCompositoVoceTitol != null) {
            query.setParameter("cdCompositoVoceTitol", cdCompositoVoceTitol);
        }

        /* ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "ELENCHI DI VERSAMENTO FASCICOLI" */
        List<ElvVRicElencoFascByFas> listaElenchiVersFascicoli = query.getResultList();
        return listaElenchiVersFascicoli;
    }

    private void setElvVRicElencoCommonParameters(FiltriElenchiVersFascicoli filtriElenchiVersFascicoli, Query query,
            long idUserIam) throws EMFError {
        /* Recupero i campi da assegnare come parametri alla query */
        BigDecimal idAmbiente = filtriElenchiVersFascicoli.getId_ambiente().parse();
        BigDecimal idEnte = filtriElenchiVersFascicoli.getId_ente().parse();
        BigDecimal idStrut = filtriElenchiVersFascicoli.getId_strut().parse();
        BigDecimal idElencoVersFasc = filtriElenchiVersFascicoli.getId_elenco_vers_fasc().parse();
        BigDecimal idCriterioRaggrFasc = filtriElenchiVersFascicoli.getId_criterio_raggr_fasc().parse();
        String tiStato = filtriElenchiVersFascicoli.getTi_stato().parse();
        Date tsCreazioneElencoDa = null;
        Date tsCreazioneElencoA = null;
        if (filtriElenchiVersFascicoli.getTs_creazione_elenco_da().parse() != null) {
            tsCreazioneElencoDa = new Date(filtriElenchiVersFascicoli.getTs_creazione_elenco_da().parse().getTime());
            if (filtriElenchiVersFascicoli.getTs_creazione_elenco_a().parse() != null) {
                tsCreazioneElencoA = new Date(filtriElenchiVersFascicoli.getTs_creazione_elenco_a().parse().getTime());
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

        String ntElencoChiuso = filtriElenchiVersFascicoli.getNt_elenco_chiuso().parse();
        String ntIndiceElenco = filtriElenchiVersFascicoli.getNt_indice_elenco().parse();
        String flElencoStandard = filtriElenchiVersFascicoli.getFl_elenco_standard().parse();

        /* Passaggio dei valori dei parametri di ricerca */
        query.setParameter("idUserIam", idUserIam);
        query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
        // query.setParameter("statoIdxAipCreato", TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO);

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

        if (idCriterioRaggrFasc != null) {
            query.setParameter("idCriterioRaggrFasc", idCriterioRaggrFasc);
        }

        if (tiStato != null) {
            query.setParameter("tiStato", tiStato);
        }

        if (tsCreazioneElencoDa != null && tsCreazioneElencoA != null) {
            query.setParameter("tsCreazioneElencoDa", tsCreazioneElencoDa, TemporalType.TIMESTAMP);
            query.setParameter("tsCreazioneElencoA", tsCreazioneElencoA, TemporalType.TIMESTAMP);
        }

        if (ntElencoChiuso != null) {
            query.setParameter("ntElencoChiuso", "%" + ntElencoChiuso.toUpperCase() + "%");
        }

        if (ntIndiceElenco != null) {
            query.setParameter("ntIndiceElenco", "%" + ntIndiceElenco.toUpperCase() + "%");
        }

        if (StringUtils.isNotBlank(flElencoStandard)) {
            query.setParameter("flElencoStandard", flElencoStandard);
        }
    }

    // TODO: fdilorenzo, refactory per criteria api e metamodels
    private Query createElvVRicElencoFascQuery(String selectQuery,
            FiltriElenchiVersFascicoli filtriElenchiVersFascicoli) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(selectQuery);

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI DI VERSAMENTO FASCICOLI */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno o più errori di firma
         * dell'elenco di versamento fascicolo, di più stati CHIUSO, in modo da considerare solo l'ultimo registrato
         */
        queryStr.append(whereWord).append(
                "((u.dtChiusura IS NULL) OR (u.dtChiusura = (SELECT s.tsStato FROM ElvStatoElencoVersFasc s WHERE s.idStatoElencoVersFasc = u.idStatoElencoVersFascCor AND s.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)) ");
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di più stati CHIUSO, di
         * molteplici stati FIRMATO, in modo da considerare solo lo stato di chiusura registrato prima della firma
         * dell'elenco di versamento fascicolo
         */
        queryStr.append(
                "OR ((u.dtFirma IS NOT NULL) AND (u.dtChiusura = (SELECT MAX(s1.tsStato) FROM ElvStatoElencoVersFasc s1 WHERE s1.tiStato = :statoChiuso AND s1.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)))) ");

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
        /*
         * NECESSARIO SOLO SE NELLE VISTE SI AGGIUNGE LA COLONNA DT_CREAZIONE_ELENCO_IX_AIP, ALTRIMENTI BASTA UNA
         * DISTINCT SU UN SOTTOINSIEME LIMITATO DI COLONNE.
         */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno o più errori di firma
         * dell'elenco indice AIP fascicolo, di più stati ELENCO_INDICI_AIP_CREATO, in modo da considerare solo l'ultimo
         * registrato
         */
        // queryStr.append(whereWord).append("((u.dtCreazioneElencoIxAip IS NULL) OR (u.dtCreazioneElencoIxAip = (SELECT
        // sIdxAip.tsStato FROM ElvStatoElencoVersFasc sIdxAip WHERE sIdxAip.idStatoElencoVersFasc =
        // u.idStatoElencoVersFascCor AND sIdxAip.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)) ");
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di più stati
         * ELENCO_INDICI_AIP_CREATO, di molteplici stati COMPLETATO, in modo da considerare solo lo stato di creazione
         * indice aip registrato prima della firma dell'elenco indice AIP fascicolo
         */
        // queryStr.append("OR ((u.dtFirma IS NOT NULL) AND (u.dtCreazioneElencoIxAip = (SELECT MAX(sIdxAip1.tsStato)
        // FROM ElvStatoElencoVersFasc sIdxAip1 WHERE sIdxAip1.tiStato = :statoIdxAipCreato AND
        // sIdxAip1.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)))) ");

        /* Inserimento nella query del filtro ID_AMBIENTE */
        BigDecimal idAmbiente = filtriElenchiVersFascicoli.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
        }

        /* Inserimento nella query del filtro ID_ENTE */
        BigDecimal idEnte = filtriElenchiVersFascicoli.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
        }

        /* Inserimento nella query del filtro ID_STRUT */
        BigDecimal idStrut = filtriElenchiVersFascicoli.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
        }

        /* Inserimento nella query del filtro ID ELENCO VERS FASC */
        BigDecimal idElencoVersFasc = filtriElenchiVersFascicoli.getId_elenco_vers_fasc().parse();
        if (idElencoVersFasc != null) {
            queryStr.append(whereWord).append("u.idElencoVersFasc = :idElencoVersFasc ");
        }

        /* Inserimento nella query del filtro TI_STATO */
        String tiStato = filtriElenchiVersFascicoli.getTi_stato().parse();
        if (tiStato != null) {
            queryStr.append(whereWord).append("u.tiStato = :tiStato ");
        }

        /* Inserimento nella query del filtro DATA CREAZIONE DA - A */
        Date tsCreazioneElencoDa = null;
        Date tsCreazioneElencoA = null;
        if (filtriElenchiVersFascicoli.getTs_creazione_elenco_da().parse() != null) {
            tsCreazioneElencoDa = new Date(filtriElenchiVersFascicoli.getTs_creazione_elenco_da().parse().getTime());
            if (filtriElenchiVersFascicoli.getTs_creazione_elenco_a().parse() != null) {
                tsCreazioneElencoA = new Date(filtriElenchiVersFascicoli.getTs_creazione_elenco_a().parse().getTime());
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

        if ((tsCreazioneElencoDa != null) && (tsCreazioneElencoA != null)) {
            queryStr.append(whereWord)
                    .append("(u.tsCreazioneElenco between :tsCreazioneElencoDa AND :tsCreazioneElencoA) ");
        }

        /* Inserimento nella query del filtro NT_ELENCO_CHIUSO */
        String ntElencoChiuso = filtriElenchiVersFascicoli.getNt_elenco_chiuso().parse();
        if (ntElencoChiuso != null) {
            queryStr.append(whereWord).append("UPPER(u.ntElencoChiuso) LIKE :ntElencoChiuso ");
        }

        /* Inserimento nella query del filtro NT_INDICE_ELENCO */
        String ntIndiceElenco = filtriElenchiVersFascicoli.getNt_indice_elenco().parse();
        if (ntIndiceElenco != null) {
            queryStr.append(whereWord).append("UPPER(u.ntIndiceElenco) LIKE :ntIndiceElenco ");
        }

        BigDecimal idTipoFascicolo = filtriElenchiVersFascicoli.getId_tipo_fascicolo().parse();
        if (idTipoFascicolo != null) {
            queryStr.append(whereWord).append("u.idTipoFascicolo = :idTipoFascicolo ");
        }

        /* Inserimento nella query del filtro CHIAVE FASCICOLO */
        BigDecimal aaFascicolo = filtriElenchiVersFascicoli.getAa_fascicolo().parse();
        String cdKeyFascicolo = filtriElenchiVersFascicoli.getCd_key_fascicolo().parse();

        if (aaFascicolo != null) {
            queryStr.append(whereWord).append("u.aaFascicolo = :aaFascicolo ");
        }

        if (cdKeyFascicolo != null) {
            queryStr.append(whereWord).append("u.cdKeyFascicolo = :cdKeyFascicolo ");
        }

        /* Inserimento nella query del filtro CHIAVE FASCICOLO per range */
        BigDecimal aaFascicoloDa = filtriElenchiVersFascicoli.getAa_fascicolo_da().parse();
        BigDecimal aaFascicoloA = filtriElenchiVersFascicoli.getAa_fascicolo_a().parse();
        String cdKeyFascicoloDa = filtriElenchiVersFascicoli.getCd_key_fascicolo_da().parse();
        String cdKeyFascicoloA = filtriElenchiVersFascicoli.getCd_key_fascicolo_a().parse();

        if (aaFascicoloDa != null && aaFascicoloA != null) {
            queryStr.append(whereWord).append("u.aaFascicolo BETWEEN :aaFascicoloDa AND :aaFascicoloA ");
        }

        if (cdKeyFascicoloDa != null && cdKeyFascicoloA != null) {
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyFascicolo, 12, '0') BETWEEN :cdKeyFascicoloDa AND :cdKeyFascicoloA ");
        }

        String cdCompositoVoceTitol = filtriElenchiVersFascicoli.getCd_composito_voce_titol().parse();
        if (cdCompositoVoceTitol != null) {
            queryStr.append(whereWord).append("u.decVoceTitol.cdCompositoVoceTitol = :cdCompositoVoceTitol ");
        }

        /* Inserimento nella query del filtro ID_CRITERIO_RAGGR_FASC */
        BigDecimal idCriterioRaggrFasc = filtriElenchiVersFascicoli.getId_criterio_raggr_fasc().parse();
        if (idCriterioRaggrFasc != null) {
            queryStr.append(whereWord).append("u.idCriterioRaggrFasc = :idCriterioRaggrFasc ");
        }

        String flElencoStandard = filtriElenchiVersFascicoli.getFl_elenco_standard().parse();
        if (StringUtils.isNotBlank(flElencoStandard)) {
            queryStr.append(whereWord).append("u.flElencoStandard = :flElencoStandard ");
        }

        /* Ordina per idElencoVersFasc decrescente */
        queryStr.append(" ORDER BY u.idElencoVersFasc DESC");
        /* CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER */
        Query query = getEntityManager().createQuery(queryStr.toString());
        return query;
    }

    // TODO: fdilorenzo, refactory per criteria api e metamodels
    public List<ElvVRicElencoFascByStato> getListaElenchiVersFascicoliDaFirmare(BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idElencoVersFasc, String note,
            ElencoEnums.ElencoStatusEnum tiStato, Date[] dateCreazioneElencoFascValidate, long idUserIam) {
        String queryStr = "SELECT u FROM ElvVRicElencoFascByStato u "
                + "WHERE u.tiStato = :tiStato AND u.idUserIam = :idUserIam ";

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI DI VERSAMENTO FASCICOLI */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di un errore di firma
         * dell'elenco di versamento fascicolo, di un secondo stato CHIUSO, in modo da considerare solo l'ultimo
         * registrato
         */
        queryStr = queryStr.concat(
                "AND (u.dtChiusura = (SELECT MAX(s.tsStato) FROM ElvStatoElencoVersFasc s WHERE s.tiStato = :statoChiuso AND s.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)) ");

        /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
        /*
         * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di un errore di firma
         * dell'elenco indice AIP fascicolo, di più stati ELENCO_INDICI_AIP_CREATO, in modo da considerare solo l'ultimo
         * registrato
         */
        if (tiStato.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO)) {
            queryStr = queryStr.concat(
                    "AND (u.dtCreazioneElencoIxAip = (SELECT MAX(sIdxAip.tsStato) FROM ElvStatoElencoVersFasc sIdxAip WHERE sIdxAip.tiStato = :statoIdxAipCreato AND sIdxAip.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc)) ");
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
                    ? queryStr.concat("AND (u.ntElencoChiuso IS NOT NULL OR u.ntIndiceElenco IS NOT NULL) ")
                    : queryStr.concat("AND (u.ntElencoChiuso IS NULL AND u.ntIndiceElenco IS NULL) ");
        }

        Date data_da = (dateCreazioneElencoFascValidate != null ? dateCreazioneElencoFascValidate[0] : null);
        Date data_a = (dateCreazioneElencoFascValidate != null ? dateCreazioneElencoFascValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            if (tiStato.equals(ElencoEnums.ElencoStatusEnum.CHIUSO)) {
                queryStr = queryStr.concat("AND (u.tsCreazioneElenco >= :datada AND u.tsCreazioneElenco <= :dataa) ");
            } else {
                queryStr = queryStr
                        .concat("AND (u.dtCreazioneElencoIxAip >= :datada AND u.dtCreazioneElencoIxAip <= :dataa) ");
            }
        }

        queryStr = queryStr.concat("ORDER BY u.dtChiusura ASC ");

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("tiStato", tiStato.name());
        query.setParameter("idUserIam", idUserIam);
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
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = query.getResultList();
        return listaElenchiVersFascicoli;
    }

    public List<ElvVRicElencoFascByStato> getListaElenchiVersFascicoliDaFirmare(List<BigDecimal> idElencoVersFascList,
            Long idUserIam) {
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = null;
        if (idElencoVersFascList != null && !idElencoVersFascList.isEmpty() && idUserIam != null) {
            String queryStr = "SELECT u FROM ElvVRicElencoFascByStato u "
                    + "WHERE u.idElencoVersFasc IN :idElencoVersFascList " + "AND u.idUserIam = :idUserIam";
            /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI DI VERSAMENTO FASCICOLI */
            /*
             * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno o più errori di
             * firma dell'elenco di versamento fascicolo, di più stati CHIUSO, in modo da considerare solo l'ultimo
             * registrato
             */
            queryStr = queryStr.concat(" AND ").concat(
                    "((u.dtChiusura IS NULL) OR (u.dtChiusura = (SELECT MAX(s.tsStato) FROM ElvStatoElencoVersFasc s WHERE s.tiStato = :statoChiuso AND s.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc))) ");

            /* TIP: fdilorenzo, WORKAROUND PER GLI ELENCHI INDICI AIP FASCICOLI */
            /*
             * Inserimento nella query del filtro per gestire l'eventuale presenza, a seguito di uno o più errori di
             * firma dell'elenco indice AIP fascicolo, di più stati ELENCO_INDICI_AIP_CREATO, in modo da considerare
             * solo l'ultimo registrato
             */
            queryStr = queryStr.concat(" AND ").concat(
                    "((u.dtCreazioneElencoIxAip IS NULL) OR (u.dtCreazioneElencoIxAip = (SELECT MAX(sIdxAip.tsStato) FROM ElvStatoElencoVersFasc sIdxAip WHERE sIdxAip.tiStato = :statoIdxAipCreato AND sIdxAip.elvElencoVersFasc.idElencoVersFasc = u.idElencoVersFasc))) ");

            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idElencoVersFascList", idElencoVersFascList);
            query.setParameter("idUserIam", new BigDecimal(idUserIam));
            query.setParameter("statoChiuso", TiStatoElencoFasc.CHIUSO);
            query.setParameter("statoIdxAipCreato", TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO);
            listaElenchiVersFascicoli = (List<ElvVRicElencoFascByStato>) query.getResultList();
        }
        return listaElenchiVersFascicoli;
    }

    // TODO: verificare
    /*
     * public long countElencIndiciAipInStates(long idUserIam, List<String> elencoStates) { Query query =
     * getEntityManager().
     * createQuery("SELECT COUNT(el) FROM ElvVLisElencoVersStato el WHERE el.idUserIam = :idUser AND el.tiStatoElenco IN :states "
     * ); query.setParameter("idUser", idUserIam); query.setParameter("states", elencoStates);
     * 
     * return (Long) query.getSingleResult(); }
     */

    public boolean isElencoDeletable(BigDecimal idElencoVersFasc) {
        String queryStr = "SELECT sf FROM ElvStatoElencoVersFasc sf "
                + "WHERE sf.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf.tiStato IN :statoElencoDeletable "
                + "AND NOT EXISTS (SELECT sf2 FROM ElvStatoElencoVersFasc sf2 "
                + "WHERE sf2.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf2.tiStato IN :statoElencoNotDeletable)";
        Query query = getEntityManager().createQuery(queryStr);
        List<TiStatoElencoFasc> statoElencoDeletable = new ArrayList<>();
        statoElencoDeletable.add(TiStatoElencoFasc.CHIUSO);
        statoElencoDeletable.add(TiStatoElencoFasc.DA_CHIUDERE);
        statoElencoDeletable.add(TiStatoElencoFasc.APERTO);
        List<TiStatoElencoFasc> statoElencoNotDeletable = new ArrayList<>();
        statoElencoNotDeletable.add(TiStatoElencoFasc.FIRMATO);
        statoElencoNotDeletable.add(TiStatoElencoFasc.FIRMA_IN_CORSO);

        query.setParameter("idElencoVersFasc", idElencoVersFasc);
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
                + "AND sf2.tiStato IN :statoElencoFascNotClosable)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("statoElencoClosable", TiStatoElencoFasc.APERTO);

        List<TiStatoElencoFasc> statoElencoFascNotClosable = new ArrayList<>();
        statoElencoFascNotClosable.add(TiStatoElencoFasc.CHIUSO);
        statoElencoFascNotClosable.add(TiStatoElencoFasc.DA_CHIUDERE);
        query.setParameter("statoElencoFascNotClosable", statoElencoFascNotClosable);

        List<ElvStatoElencoVersFasc> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            if (elenchi.get(0).getElvElencoVersFasc().getFasFascicoli().size() > 0) {
                return true;
            }
            return false;
        }
    }

    // TIP: fdilorenzo, test criteria api con metamodels
    public boolean isElencoClosable2(BigDecimal idElencoVersFasc) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();

        CriteriaQuery<ElvStatoElencoVersFasc> query = cb.createQuery(ElvStatoElencoVersFasc.class);
        Root<ElvStatoElencoVersFasc> sf = query.from(ElvStatoElencoVersFasc.class);

        Path<ElvElencoVersFasc> elvElencoVersFasc = sf.get(ElvStatoElencoVersFasc_.elvElencoVersFasc);
        Path<Long> id = elvElencoVersFasc.get(ElvElencoVersFasc_.idElencoVersFasc);
        Predicate pOnIdElencoVersFasc = cb.in(id).value(idElencoVersFasc.longValue());
        Predicate pOnTiStato = cb.in(sf.get(ElvStatoElencoVersFasc_.tiStato)).value(TiStatoElencoFasc.APERTO);

        Subquery<ElvStatoElencoVersFasc> subquery = query.subquery(ElvStatoElencoVersFasc.class);
        Root<ElvStatoElencoVersFasc> sf2 = subquery.from(ElvStatoElencoVersFasc.class);

        Path<ElvElencoVersFasc> elvElencoVersFasc2 = sf2.get(ElvStatoElencoVersFasc_.elvElencoVersFasc);
        Path<Long> id2 = elvElencoVersFasc2.get(ElvElencoVersFasc_.idElencoVersFasc);
        Predicate pOnIdElencoVersFasc2 = cb.in(id2).value(idElencoVersFasc.longValue());
        Predicate pOnTiStato2 = cb.in(sf2.get(ElvStatoElencoVersFasc_.tiStato)).value(TiStatoElencoFasc.CHIUSO)
                .value(TiStatoElencoFasc.DA_CHIUDERE);
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
            return elenchi.get(0).getElvElencoVersFasc().getFasFascicoli().size() > 0;
        }
    }

    public boolean areFascDeletables(BigDecimal idElencoVersFasc) {
        String queryStr = "SELECT sf FROM ElvStatoElencoVersFasc sf "
                + "WHERE sf.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf.tiStato IN :statoElencoFascDeletables "
                + "AND NOT EXISTS (SELECT sf2 FROM ElvStatoElencoVersFasc sf2 "
                + "WHERE sf2.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND sf2.tiStato = :statoElencoFascNotDeletables)";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        List<TiStatoElencoFasc> statoElencoFascDeletables = new ArrayList<>();
        statoElencoFascDeletables.add(TiStatoElencoFasc.APERTO);
        statoElencoFascDeletables.add(TiStatoElencoFasc.DA_CHIUDERE);
        query.setParameter("statoElencoFascDeletables", statoElencoFascDeletables);
        query.setParameter("statoElencoFascNotDeletables", TiStatoElencoFasc.CHIUSO);
        List<ElvStatoElencoVersFasc> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            if (elenchi.get(0).getElvElencoVersFasc().getFasFascicoli().size() > 0) {
                return true;
            }
            return false;
        }
    }

    public boolean areAllElenchiNonPresentiFirmati(List<BigDecimal> idElencoVersFascSelezionatiList, Date dataChiusura,
            BigDecimal idStrut) {
        String queryStr = "SELECT COUNT(elab) FROM ElvElencoVersFascDaElab elab JOIN elab.elvElencoVersFasc u "
                + "WHERE u.dtScadChius < :dataChiusura "
                + "AND elab.elvElencoVersFasc.idElencoVersFasc NOT IN :idElencoVersFascSelezionatiList "
                + "AND elab.tiStato = :tiStato " + "AND elab.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFascSelezionatiList", idElencoVersFascSelezionatiList);
        query.setParameter("dataChiusura", dataChiusura);
        query.setParameter("tiStato", TiStatoElencoFascDaElab.CHIUSO);
        query.setParameter("idStrut", idStrut);
        return (Long) query.getSingleResult() == 0;
    }

    public boolean existIdElenco(BigDecimal idElencoVersFasc, BigDecimal idStrut) {
        String queryStr = "SELECT u FROM ElvElencoVersFasc u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.idElencoVersFasc = :idElencoVersFasc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("idStrut", idStrut);
        return !query.getResultList().isEmpty();
    }

    public void saveNote(Long idUserIam, BigDecimal idElencoVersFasc, String ntIndiceElenco, String ntElencoChiuso,
            List<ElencoEnums.OpTypeEnum> operList) {
        ElvElencoVersFasc elenco = getEntityManager().find(ElvElencoVersFasc.class, idElencoVersFasc.longValue());
        elenco.setNtIndiceElenco(ntIndiceElenco);
        elenco.setNtElencoChiuso(ntElencoChiuso);
        /* TODO: verificare, A seconda di cosa ho modificato, scrivo nel log */
        /*
         * for (ElencoEnums.OpTypeEnum oper : operList) { evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(),
         * idUserIam, oper.name()); }
         */
    }

    public List<ElvStatoElencoVersFasc> retrieveStatiElencoByElencoVersFasc(BigDecimal idElvElencoVersFasc) {
        List<ElvStatoElencoVersFasc> result = null;
        try {
            ElvElencoVersFasc elenco = getEntityManager().find(ElvElencoVersFasc.class,
                    idElvElencoVersFasc.longValue());
            Query query = getEntityManager().createNamedQuery("ElvStatoElencoVersFasc.findByElencoVersFasc",
                    ElvStatoElencoVersFasc.class);
            query.setParameter("elvElencoVersFasc", elenco);
            result = query.getResultList();
        } catch (RuntimeException ex) {
            log.error("Errore nell'estrazione degli stati", ex);
            throw ex;
        }
        return result;
    }
}
