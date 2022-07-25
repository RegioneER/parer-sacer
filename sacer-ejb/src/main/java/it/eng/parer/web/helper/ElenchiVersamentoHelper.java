package it.eng.parer.web.helper;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.DocStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm.FiltriElenchiVersamento;
import it.eng.parer.viewEntity.ElvVLisElencoVersStato;
import it.eng.parer.viewEntity.ElvVRicElencoVers;
import it.eng.parer.viewEntity.ElvVRicElencoVersByStato;
import it.eng.parer.viewEntity.ElvVRicElencoVersByUd;
import it.eng.parer.web.util.StringPadding;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class ElenchiVersamentoHelper extends GenericHelper {

    /* Definizione variabili della classe ElenchiVersamentoHelper */
    @EJB
    private ElencoVersamentoHelper evHelper;

    private static final Logger log = LoggerFactory.getLogger(VolumiHelper.class.getName());

    public List<ElvVRicElencoVers> retrieveElvVRicElencoVersList(long idUserIam,
            FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
        Query query = createElvVRicElencoVersQuery("SELECT u FROM ElvVRicElencoVers u WHERE u.idUserIam = :idUserIam ",
                filtriElenchiVersamento);
        setElvVRicElencoCommonParameters(filtriElenchiVersamento, query, idUserIam);
        List<ElvVRicElencoVers> listaElenchiVersamento = query.getResultList();
        return listaElenchiVersamento;
    }

    public List<ElvVRicElencoVersByStato> retrieveElvVRicElencoVersByStatoList(long idUserIam,
            FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
        Query query = createElvVRicElencoVersQuery(
                "SELECT u FROM ElvVRicElencoVersByStato u WHERE u.idUserIam = :idUserIam ", filtriElenchiVersamento);

        setElvVRicElencoCommonParameters(filtriElenchiVersamento, query, idUserIam);

        BigDecimal hhStatoElencoInCodaJms = filtriElenchiVersamento.getHh_stato_elenco_in_coda_jms().parse();
        if (hhStatoElencoInCodaJms != null) {
            query.setParameter("hhStatoElencoInCodaJms", hhStatoElencoInCodaJms);
        }

        List<ElvVRicElencoVersByStato> listaElenchiVersamento = query.getResultList();
        return listaElenchiVersamento;
    }

    public List<ElvVRicElencoVersByUd> retrieveElvVRicElencoVersByUdList(long idUserIam,
            FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
        Query query = createElvVRicElencoVersQuery("SELECT DISTINCT new it.eng.parer.viewEntity.ElvVRicElencoVersByUd "
                + "(u.idElencoVers, u.nmElenco, u.dsElenco, u.tiStatoElenco, u.tiGestElenco, u.niCompAggElenco, u.niCompVersElenco, "
                + "u.niSizeVersElenco, u.niSizeAggElenco, u.dtCreazioneElenco, u.dtChius, u.dtFirmaIndice, "
                + "u.idCriterioRaggr, u.nmCriterioRaggr, u.nmAmbiente, u.nmEnte, u.nmStrut, "
                + "u.flElencoFisc, u.flElencoStandard, u.flElencoFirmato, u.niIndiciAip, "
                + "u.dtCreazioneElencoIxAip, u.dtFirmaElencoIxAip, u.tsStatoElencoInCodaJms) "
                + "FROM ElvVRicElencoVersByUd u WHERE u.idUserIam = :idUserIam ", filtriElenchiVersamento);

        setElvVRicElencoCommonParameters(filtriElenchiVersamento, query, idUserIam);

        String cdRegistroKeyUnitaDoc = filtriElenchiVersamento.getCd_registro_key_unita_doc().parse();
        BigDecimal aaKeyUnitaDoc = filtriElenchiVersamento.getAa_key_unita_doc().parse();
        String cdKeyUnitaDoc = filtriElenchiVersamento.getCd_key_unita_doc().parse();
        BigDecimal aaKeyUnitaDocDa = filtriElenchiVersamento.getAa_key_unita_doc_da().parse();
        BigDecimal aaKeyUnitaDocA = filtriElenchiVersamento.getAa_key_unita_doc_a().parse();
        String cdKeyUnitaDocDa = filtriElenchiVersamento.getCd_key_unita_doc_da().parse();
        String cdKeyUnitaDocA = filtriElenchiVersamento.getCd_key_unita_doc_a().parse();
        if (StringUtils.isNotBlank(cdRegistroKeyUnitaDoc)) {
            query.setParameter("cdRegistroKeyUnitaDoc", cdRegistroKeyUnitaDoc);
        }
        if (aaKeyUnitaDoc != null) {
            query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        }
        if (cdKeyUnitaDoc != null) {
            query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        }
        if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
            query.setParameter("aaKeyUnitaDocDa", aaKeyUnitaDocDa);
            query.setParameter("aaKeyUnitaDocA", aaKeyUnitaDocA);
        }
        if (cdKeyUnitaDocDa != null && cdKeyUnitaDocA != null) {
            cdKeyUnitaDocDa = StringPadding.padString(cdKeyUnitaDocDa, "0", 12, StringPadding.PADDING_LEFT);
            cdKeyUnitaDocA = StringPadding.padString(cdKeyUnitaDocA, "0", 12, StringPadding.PADDING_LEFT);
            query.setParameter("cdKeyUnitaDocDa", cdKeyUnitaDocDa);
            query.setParameter("cdKeyUnitaDocA", cdKeyUnitaDocA);
        }

        /* ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA DI "ELENCHI DI VERSAMENTO" */
        List<ElvVRicElencoVersByUd> listaElenchiVersamento = query.getResultList();
        return listaElenchiVersamento;
    }

    private void setElvVRicElencoCommonParameters(FiltriElenchiVersamento filtriElenchiVersamento, Query query,
            long idUserIam) throws EMFError {
        /* Recupero i campi da assegnare come parametri alla query */
        BigDecimal idAmbiente = filtriElenchiVersamento.getId_ambiente().parse();
        BigDecimal idEnte = filtriElenchiVersamento.getId_ente().parse();
        BigDecimal idStrut = filtriElenchiVersamento.getId_strut().parse();
        BigDecimal idElencoVers = filtriElenchiVersamento.getId_elenco_vers().parse();
        String nmElenco = filtriElenchiVersamento.getNm_elenco().parse();
        String dsElenco = filtriElenchiVersamento.getDs_elenco().parse();
        List<String> tiStatoElenco = filtriElenchiVersamento.getTi_stato_elenco().parse();
        String tiGestElenco = filtriElenchiVersamento.getTi_gest_elenco().parse();
        Date dtCreazioneElencoDa = null;
        Date dtCreazioneElencoA = null;
        if (filtriElenchiVersamento.getDt_creazione_elenco_da().parse() != null) {
            dtCreazioneElencoDa = new Date(filtriElenchiVersamento.getDt_creazione_elenco_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_creazione_elenco_a().parse() != null) {
                dtCreazioneElencoA = new Date(filtriElenchiVersamento.getDt_creazione_elenco_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoA = calendar.getTime();
            } else {
                dtCreazioneElencoA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoA = calendar.getTime();
            }
        }
        Date dtValidazioneElencoDa = null;
        Date dtValidazioneElencoA = null;
        if (filtriElenchiVersamento.getDt_validazione_elenco_da().parse() != null) {
            dtValidazioneElencoDa = new Date(filtriElenchiVersamento.getDt_validazione_elenco_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_validazione_elenco_a().parse() != null) {
                dtValidazioneElencoA = new Date(filtriElenchiVersamento.getDt_validazione_elenco_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtValidazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtValidazioneElencoA = calendar.getTime();
            } else {
                dtValidazioneElencoA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtValidazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtValidazioneElencoA = calendar.getTime();
            }
        }

        String ntElencoChiuso = filtriElenchiVersamento.getNt_elenco_chiuso().parse();
        String ntIndiceElenco = filtriElenchiVersamento.getNt_indice_elenco().parse();
        String nmCriterioRaggr = filtriElenchiVersamento.getNm_criterio_raggr().parse();
        String flElencoFisc = filtriElenchiVersamento.getFl_elenco_fisc().parse();
        String flElencoStandard = filtriElenchiVersamento.getFl_elenco_standard().parse();
        String flElencoFirmato = filtriElenchiVersamento.getFl_elenco_firmato().parse();
        /* Inserimento nella query del filtro DATA CREAZIONE IX AIP DA - A */
        Date dtCreazioneElencoIxAipDa = null;
        Date dtCreazioneElencoIxAipA = null;
        if (filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_da().parse() != null) {
            dtCreazioneElencoIxAipDa = new Date(
                    filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_a().parse() != null) {
                dtCreazioneElencoIxAipA = new Date(
                        filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoIxAipA = calendar.getTime();
            } else {
                dtCreazioneElencoIxAipA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoIxAipA = calendar.getTime();
            }
        }
        /* Inserimento nella query del filtro DATA FIRMA IX AIP DA - A */
        Date dtFirmaElencoIxAipDa = null;
        Date dtFirmaElencoIxAipA = null;
        if (filtriElenchiVersamento.getDt_firma_elenco_ix_aip_da().parse() != null) {
            dtFirmaElencoIxAipDa = new Date(filtriElenchiVersamento.getDt_firma_elenco_ix_aip_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_firma_elenco_ix_aip_a().parse() != null) {
                dtFirmaElencoIxAipA = new Date(filtriElenchiVersamento.getDt_firma_elenco_ix_aip_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtFirmaElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtFirmaElencoIxAipA = calendar.getTime();
            } else {
                dtFirmaElencoIxAipA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtFirmaElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtFirmaElencoIxAipA = calendar.getTime();
            }
        }

        /* Passaggio dei valori dei parametri di ricerca */
        query.setParameter("idUserIam", idUserIam);

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }

        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }

        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }

        if (idElencoVers != null) {
            query.setParameter("idElencoVers", idElencoVers);
        }

        if (nmElenco != null) {
            query.setParameter("nmElenco", "%" + nmElenco.toUpperCase() + "%");
        }

        if (dsElenco != null) {
            query.setParameter("dsElenco", "%" + dsElenco.toUpperCase() + "%");
        }

        if (!tiStatoElenco.isEmpty()) {
            if (tiStatoElenco.contains("IN_CODA_JMS_GENERA_INDICE_AIP")) {
                tiStatoElenco.add("IN_CODA_JMS_GENERA_INDICE_AIP (IN_CODA_JMS_GENERA_INDICE_AIP)");
            }
            if (tiStatoElenco.contains("IN_CODA_JMS_VERIFICA_FIRME_DT_VERS")) {
                tiStatoElenco.add("IN_CODA_JMS_VERIFICA_FIRME_DT_VERS (IN_CODA_JMS_VERIFICA_FIRME_DT_VERS)");
            }
            query.setParameter("tiStatoElenco", tiStatoElenco);
        }

        if (tiGestElenco != null) {
            query.setParameter("tiGestElenco", tiGestElenco);
        }

        if (dtCreazioneElencoDa != null && dtCreazioneElencoA != null) {
            query.setParameter("dtCreazioneElencoDa", dtCreazioneElencoDa, TemporalType.DATE);
            query.setParameter("dtCreazioneElencoA", dtCreazioneElencoA, TemporalType.DATE);
        }

        if (dtValidazioneElencoDa != null && dtValidazioneElencoA != null) {
            query.setParameter("dtValidazioneElencoDa", dtValidazioneElencoDa, TemporalType.DATE);
            query.setParameter("dtValidazioneElencoA", dtValidazioneElencoA, TemporalType.DATE);
        }

        if (ntElencoChiuso != null) {
            query.setParameter("ntElencoChiuso", "%" + ntElencoChiuso.toUpperCase() + "%");
        }

        if (ntIndiceElenco != null) {
            query.setParameter("ntIndiceElenco", "%" + ntIndiceElenco.toUpperCase() + "%");
        }
        if (nmCriterioRaggr != null) {
            query.setParameter("nmCriterioRaggr", nmCriterioRaggr);
        }
        if (StringUtils.isNotBlank(flElencoFisc)) {
            query.setParameter("flElencoFisc", flElencoFisc);
        }
        if (StringUtils.isNotBlank(flElencoStandard)) {
            query.setParameter("flElencoStandard", flElencoStandard);
        }
        if (StringUtils.isNotBlank(flElencoFirmato)) {
            query.setParameter("flElencoFirmato", flElencoFirmato);
        }
        if ((dtCreazioneElencoIxAipDa != null) && (dtCreazioneElencoIxAipA != null)) {
            query.setParameter("dtCreazioneElencoIxAipDa", dtCreazioneElencoIxAipDa, TemporalType.DATE);
            query.setParameter("dtCreazioneElencoIxAipA", dtCreazioneElencoIxAipA, TemporalType.DATE);
        }
        if ((dtFirmaElencoIxAipDa != null) && (dtFirmaElencoIxAipA != null)) {
            query.setParameter("dtFirmaElencoIxAipDa", dtFirmaElencoIxAipDa, TemporalType.DATE);
            query.setParameter("dtFirmaElencoIxAipA", dtFirmaElencoIxAipA, TemporalType.DATE);
        }
    }

    private Query createElvVRicElencoVersQuery(String selectQuery, FiltriElenchiVersamento filtriElenchiVersamento)
            throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(selectQuery);
        /* Inserimento nella query del filtro ID_AMBIENTE */
        BigDecimal idAmbiente = filtriElenchiVersamento.getId_ambiente().parse();
        if (idAmbiente != null) {
            queryStr.append(whereWord).append("u.idAmbiente = :idAmbiente ");
        }

        /* Inserimento nella query del filtro ID_ENTE */
        BigDecimal idEnte = filtriElenchiVersamento.getId_ente().parse();
        if (idEnte != null) {
            queryStr.append(whereWord).append("u.idEnte = :idEnte ");
        }

        /* Inserimento nella query del filtro ID_STRUT */
        BigDecimal idStrut = filtriElenchiVersamento.getId_strut().parse();
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.idStrut = :idStrut ");
        }

        /* Inserimento nella query del filtro ID ELENCO VERS */
        BigDecimal idElencoVers = filtriElenchiVersamento.getId_elenco_vers().parse();
        if (idElencoVers != null) {
            queryStr.append(whereWord).append("u.idElencoVers = :idElencoVers ");
        }

        /* Inserimento nella query del filtro NM_ELENCO */
        String nmElenco = filtriElenchiVersamento.getNm_elenco().parse();
        if (nmElenco != null) {
            queryStr.append(whereWord).append("UPPER(u.nmElenco) LIKE :nmElenco ");
        }

        /* Inserimento nella query del filtro DS_ELENCO */
        String dsElenco = filtriElenchiVersamento.getDs_elenco().parse();
        if (dsElenco != null) {
            queryStr.append(whereWord).append("UPPER(u.dsElenco) LIKE :dsElenco ");
        }

        /* Inserimento nella query del filtro TI_STATO_ELENCO */
        List<String> tiStatoElenco = filtriElenchiVersamento.getTi_stato_elenco().parse();
        if (!tiStatoElenco.isEmpty()) {
            queryStr.append(whereWord).append("u.tiStatoElenco IN :tiStatoElenco ");
        }

        /* Inserimento nella query del filtro TI_GEST_ELENCO */
        String tiGestElenco = filtriElenchiVersamento.getTi_gest_elenco().parse();
        if (tiGestElenco != null) {
            queryStr.append(whereWord).append("u.tiGestElenco = :tiGestElenco ");
        }

        /* Inserimento nella query del filtro DATA CREAZIONE DA - A */
        Date dtCreazioneElencoDa = null;
        Date dtCreazioneElencoA = null;
        if (filtriElenchiVersamento.getDt_creazione_elenco_da().parse() != null) {
            dtCreazioneElencoDa = new Date(filtriElenchiVersamento.getDt_creazione_elenco_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_creazione_elenco_a().parse() != null) {
                dtCreazioneElencoA = new Date(filtriElenchiVersamento.getDt_creazione_elenco_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoA = calendar.getTime();
            } else {
                dtCreazioneElencoA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoA = calendar.getTime();
            }
        }

        if ((dtCreazioneElencoDa != null) && (dtCreazioneElencoA != null)) {
            queryStr.append(whereWord)
                    .append("(u.dtCreazioneElenco between :dtCreazioneElencoDa AND :dtCreazioneElencoA) ");
        }

        /* Inserimento nella query del filtro DATA VALIDAZIONE DA - A */
        Date dtValidazioneElencoDa = null;
        Date dtValidazioneElencoA = null;
        if (filtriElenchiVersamento.getDt_validazione_elenco_da().parse() != null) {
            dtValidazioneElencoDa = new Date(filtriElenchiVersamento.getDt_validazione_elenco_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_validazione_elenco_a().parse() != null) {
                dtValidazioneElencoA = new Date(filtriElenchiVersamento.getDt_validazione_elenco_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtValidazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtValidazioneElencoA = calendar.getTime();
            } else {
                dtValidazioneElencoA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtValidazioneElencoA);
                calendar.add(Calendar.DATE, 1);
                dtValidazioneElencoA = calendar.getTime();
            }
        }

        if ((dtValidazioneElencoDa != null) && (dtValidazioneElencoA != null)) {
            queryStr.append(whereWord)
                    .append("(u.dtFirmaIndice between :dtValidazioneElencoDa AND :dtValidazioneElencoA) ");
        }

        /* Inserimento nella query del filtro NT_ELENCO_CHIUSO */
        String ntElencoChiuso = filtriElenchiVersamento.getNt_elenco_chiuso().parse();
        if (ntElencoChiuso != null) {
            queryStr.append(whereWord).append("UPPER(u.ntElencoChiuso) LIKE :ntElencoChiuso ");
        }

        /* Inserimento nella query del filtro NT_INDICE_ELENCO */
        String ntIndiceElenco = filtriElenchiVersamento.getNt_indice_elenco().parse();
        if (ntIndiceElenco != null) {
            queryStr.append(whereWord).append("UPPER(u.ntIndiceElenco) LIKE :ntIndiceElenco ");
        }

        /* Inserimento nella query del filtro CHIAVE UNITA' DOCUMENTARIA */
        String cdRegistroKeyUnitaDoc = filtriElenchiVersamento.getCd_registro_key_unita_doc().parse();
        BigDecimal aaKeyUnitaDoc = filtriElenchiVersamento.getAa_key_unita_doc().parse();
        String cdKeyUnitaDoc = filtriElenchiVersamento.getCd_key_unita_doc().parse();

        if (StringUtils.isNotBlank(cdRegistroKeyUnitaDoc)) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDoc ");
            // /* Se ho inserito il registro, allora devo aggiungere anche il parametro id_strut_uni_doc */
            // queryStr.append(whereWord).append("u.idStrutUniDoc = :idStrut ");
        }

        if (aaKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :aaKeyUnitaDoc ");
        }

        if (cdKeyUnitaDoc != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :cdKeyUnitaDoc ");
        }

        /* Inserimento nella query del filtro CHIAVE UNITA' DOCUMENTARIA per range */
        BigDecimal aaKeyUnitaDocDa = filtriElenchiVersamento.getAa_key_unita_doc_da().parse();
        BigDecimal aaKeyUnitaDocA = filtriElenchiVersamento.getAa_key_unita_doc_a().parse();
        String cdKeyUnitaDocDa = filtriElenchiVersamento.getCd_key_unita_doc_da().parse();
        String cdKeyUnitaDocA = filtriElenchiVersamento.getCd_key_unita_doc_a().parse();

        if (aaKeyUnitaDocDa != null && aaKeyUnitaDocA != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc BETWEEN :aaKeyUnitaDocDa AND :aaKeyUnitaDocA ");
        }

        if (cdKeyUnitaDocDa != null && cdKeyUnitaDocA != null) {
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :cdKeyUnitaDocDa AND :cdKeyUnitaDocA ");
        }

        /* Inserimento nella query del filtro NM_CRITERIO_RAGGR */
        String nmCriterioRaggr = filtriElenchiVersamento.getNm_criterio_raggr().parse();
        if (nmCriterioRaggr != null) {
            queryStr.append(whereWord).append("u.nmCriterioRaggr = :nmCriterioRaggr ");
        }

        String flElencoStandard = filtriElenchiVersamento.getFl_elenco_standard().parse();
        if (StringUtils.isNotBlank(flElencoStandard)) {
            queryStr.append(whereWord).append("u.flElencoStandard = :flElencoStandard ");
        }
        String flElencoFisc = filtriElenchiVersamento.getFl_elenco_fisc().parse();
        if (StringUtils.isNotBlank(flElencoFisc)) {
            queryStr.append(whereWord).append("u.flElencoFisc = :flElencoFisc ");
        }
        String flElencoFirmato = filtriElenchiVersamento.getFl_elenco_firmato().parse();
        if (StringUtils.isNotBlank(flElencoFirmato)) {
            queryStr.append(whereWord).append("u.flElencoFirmato = :flElencoFirmato ");
        }
        String flElencoIndiciAipCreato = filtriElenchiVersamento.getFl_elenco_indici_aip_creato().parse();
        if (StringUtils.isNotBlank(flElencoIndiciAipCreato)) {
            if (flElencoIndiciAipCreato.equals("1")) {
                queryStr.append(whereWord).append("u.dtCreazioneElencoIxAip IS NOT NULL");
            } else {
                queryStr.append(whereWord).append("u.dtCreazioneElencoIxAip IS NULL");
            }
        }

        /* Inserimento nella query del filtro DATA CREAZIONE IX AIP DA - A */
        Date dtCreazioneElencoIxAipDa = null;
        Date dtCreazioneElencoIxAipA = null;
        if (filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_da().parse() != null) {
            dtCreazioneElencoIxAipDa = new Date(
                    filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_a().parse() != null) {
                dtCreazioneElencoIxAipA = new Date(
                        filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoIxAipA = calendar.getTime();
            } else {
                dtCreazioneElencoIxAipA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtCreazioneElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtCreazioneElencoIxAipA = calendar.getTime();
            }
        }

        if ((dtCreazioneElencoIxAipDa != null) && (dtCreazioneElencoIxAipA != null)) {
            queryStr.append(whereWord).append(
                    "(u.dtCreazioneElencoIxAip between :dtCreazioneElencoIxAipDa AND :dtCreazioneElencoIxAipA) ");
        }

        /* Inserimento nella query del filtro DATA FIRMA IX AIP DA - A */
        Date dtFirmaElencoIxAipDa = null;
        Date dtFirmaElencoIxAipA = null;
        if (filtriElenchiVersamento.getDt_firma_elenco_ix_aip_da().parse() != null) {
            dtFirmaElencoIxAipDa = new Date(filtriElenchiVersamento.getDt_firma_elenco_ix_aip_da().parse().getTime());
            if (filtriElenchiVersamento.getDt_firma_elenco_ix_aip_a().parse() != null) {
                dtFirmaElencoIxAipA = new Date(filtriElenchiVersamento.getDt_firma_elenco_ix_aip_a().parse().getTime());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtFirmaElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtFirmaElencoIxAipA = calendar.getTime();
            } else {
                dtFirmaElencoIxAipA = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dtFirmaElencoIxAipA);
                calendar.add(Calendar.DATE, 1);
                dtFirmaElencoIxAipA = calendar.getTime();
            }
        }

        if ((dtFirmaElencoIxAipDa != null) && (dtFirmaElencoIxAipA != null)) {
            queryStr.append(whereWord)
                    .append("(u.dtFirmaElencoIxAip between :dtFirmaElencoIxAipDa AND :dtFirmaElencoIxAipA) ");
        }

        /* Inserimento nella query del filtro HH_STATO_ELENCO_IN_CODA_JMS */
        BigDecimal hhStatoElencoInCodaJms = filtriElenchiVersamento.getHh_stato_elenco_in_coda_jms().parse();
        if (hhStatoElencoInCodaJms != null) {
            queryStr.append(whereWord).append("u.hhStatoElencoInCodaJms >= :hhStatoElencoInCodaJms ");
        }

        /* Ordina per data creazione ascendente */
        queryStr.append(" ORDER BY u.dtCreazioneElenco ASC ");
        /* CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER */
        Query query = getEntityManager().createQuery(queryStr.toString());
        return query;
    }

    public List<ElvVLisElencoVersStato> getListaElenchiDaFirmare(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, BigDecimal idElencoVers, String note, String flElencoFisc, String tiGestElenco,
            Date[] dateCreazioneElencoValidate, long idUserIam, String... statiElenco) {
        String queryStr = "SELECT u FROM ElvVLisElencoVersStato u "
                + "WHERE u.tiStatoElenco IN :statiElenco AND u.idUserIam = :idUserIam ";

        if (idAmbiente != null) {
            queryStr = queryStr.concat("AND u.idAmbiente = :idAmbiente ");
        }
        if (idEnte != null) {
            queryStr = queryStr.concat("AND u.idEnte = :idEnte ");
        }
        if (idStrut != null) {
            queryStr = queryStr.concat("AND u.idStrut = :idStrut ");
        }
        if (idElencoVers != null) {
            queryStr = queryStr.concat("AND u.idElencoVers = :idElencoVers ");
        }
        if (note != null) {
            queryStr = queryStr.concat("AND u.flNoteElenco = :note ");
        }
        if (flElencoFisc != null) {
            queryStr = queryStr.concat("AND u.flElencoFisc = :flElencoFisc ");
        }
        if (tiGestElenco != null) {
            queryStr = queryStr.concat("AND u.tiGestElenco = :tiGestElenco ");
        }

        Date data_da = (dateCreazioneElencoValidate != null ? dateCreazioneElencoValidate[0] : null);
        Date data_a = (dateCreazioneElencoValidate != null ? dateCreazioneElencoValidate[1] : null);

        if ((data_da != null) && (data_a != null)) {
            if (Arrays.asList(statiElenco).contains(ElencoEnums.ElencoStatusEnum.CHIUSO.name())) {
                queryStr = queryStr.concat("AND (u.dtCreazioneElenco >= :datada AND u.dtCreazioneElenco <= :dataa) ");
            } else {
                queryStr = queryStr
                        .concat("AND (u.dtCreazioneElencoIxAip >= :datada AND u.dtCreazioneElencoIxAip <= :dataa) ");
            }
        }

        queryStr = queryStr.concat("ORDER BY u.dtChius ASC ");

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("statiElenco", Arrays.asList(statiElenco));
        query.setParameter("idUserIam", idUserIam);
        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idEnte != null) {
            query.setParameter("idEnte", idEnte);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (idElencoVers != null) {
            query.setParameter("idElencoVers", idElencoVers);
        }
        if (note != null) {
            query.setParameter("note", note);
        }
        if (flElencoFisc != null) {
            query.setParameter("flElencoFisc", flElencoFisc);
        }
        if (tiGestElenco != null) {
            query.setParameter("tiGestElenco", tiGestElenco);
        }
        if (data_da != null && data_a != null) {
            query.setParameter("datada", data_da, TemporalType.TIMESTAMP);
            query.setParameter("dataa", data_a, TemporalType.TIMESTAMP);
        }
        List<ElvVLisElencoVersStato> listaElenchiVersamento = query.getResultList();
        return listaElenchiVersamento;
    }

    public List<ElvVLisElencoVersStato> getListaElenchiDaFirmare(List<BigDecimal> idElencoVersList, Long idUserIam) {
        List<ElvVLisElencoVersStato> listaElenchiVersamento = null;
        if (idElencoVersList != null && !idElencoVersList.isEmpty() && idUserIam != null) {
            String queryStr = "SELECT u FROM ElvVLisElencoVersStato u " + "WHERE u.idElencoVers IN :idElencoVersList "
                    + "AND u.idUserIam = :idUserIam";
            Query query = getEntityManager().createQuery(queryStr);
            query.setParameter("idElencoVersList", idElencoVersList);
            query.setParameter("idUserIam", new BigDecimal(idUserIam));
            listaElenchiVersamento = (List<ElvVLisElencoVersStato>) query.getResultList();
        }
        return listaElenchiVersamento;
    }

    public long countElencIndiciAipInStates(long idUserIam, List<String> elencoStates) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(el) FROM ElvVLisElencoVersStato el WHERE el.idUserIam = :idUser AND el.tiStatoElenco IN :states ");
        query.setParameter("idUser", idUserIam);
        query.setParameter("states", elencoStates);

        return (Long) query.getSingleResult();
    }

    public boolean isElencoDeletable(BigDecimal idElencoVers) {
        String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
                + "AND u.tiStatoElenco IN :statoElencoDeletable ";
        Query query = getEntityManager().createQuery(queryStr);
        ElencoStatusEnum[] elencoEnums = ElencoStatusEnum.getStatoElencoDeletable();
        List<String> elencoString = new ArrayList<>();
        for (ElencoStatusEnum elencoEnum : elencoEnums) {
            elencoString.add(elencoEnum.name());
        }
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("statoElencoDeletable", elencoString);
        return !query.getResultList().isEmpty();
    }

    public boolean isElencoClosable(BigDecimal idElencoVers) {
        String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
                + "AND u.tiStatoElenco = :statoElencoClosable ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("statoElencoClosable", ElencoEnums.ElencoStatusEnum.APERTO.name());
        List<ElvElencoVer> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            if (elenchi.get(0).getAroUnitaDocs().size() > 0) {
                return true;
            } else if (elenchi.get(0).getAroDocs().size() > 0) {
                return true;
            } else if (elenchi.get(0).getAroUpdUnitaDocs().size() > 0) {
                return true;
            }
            return false;
        }
    }

    public boolean isElencoValidable(BigDecimal idElencoVers) {
        String queryStr = "SELECT u FROM ElvElencoVer u JOIN u.decCriterioRaggr crit "
                + "WHERE u.idElencoVers = :idElencoVers " + "AND u.tiStatoElenco = :statoElencoValidable "
                + "AND ((u.tiModValidElenco IS NOT NULL AND u.tiValidElenco IS NOT NULL "
                + " AND u.tiModValidElenco = :tiModValidElenco AND u.tiValidElenco = :tiValidElenco) "
                + " OR ((u.tiModValidElenco IS NULL OR u.tiValidElenco IS NULL) "
                + " AND crit.tiModValidElenco = :tiModValidElencoCriterio AND crit.tiValidElenco = :tiValidElencoCriterio)) ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("statoElencoValidable", ElencoEnums.ElencoStatusEnum.CHIUSO.name());
        query.setParameter("tiModValidElenco", TiModValidElenco.AUTOMATICA);
        query.setParameter("tiValidElenco", TiValidElenco.NO_FIRMA);
        query.setParameter("tiModValidElencoCriterio", TiModValidElencoCriterio.AUTOMATICA);
        query.setParameter("tiValidElencoCriterio", TiValidElencoCriterio.NO_FIRMA);
        return !query.getResultList().isEmpty();
    }

    public boolean areUdDocDeletables(BigDecimal idElencoVers) {
        String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
                + "AND u.tiStatoElenco IN :statoElencoUdDocDeletables ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        List<String> statoElencoUdDocDeletables = new ArrayList<>();
        statoElencoUdDocDeletables.add(ElencoEnums.ElencoStatusEnum.APERTO.name());
        statoElencoUdDocDeletables.add(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
        query.setParameter("statoElencoUdDocDeletables", statoElencoUdDocDeletables);
        List<ElvElencoVer> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            if (elenchi.get(0).getAroUnitaDocs().size() > 0) {
                return true;
            } else if (elenchi.get(0).getAroDocs().size() > 0) {
                return true;
            }
            return false;
        }
    }

    public boolean areUpdDeletables(BigDecimal idElencoVers) {
        String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.idElencoVers = :idElencoVers "
                + "AND u.tiStatoElenco IN :statoElencoUpdDeletables ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        List<String> statoElencoUpdDeletables = new ArrayList<>();
        statoElencoUpdDeletables.add(ElencoEnums.ElencoStatusEnum.APERTO.name());
        statoElencoUpdDeletables.add(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
        query.setParameter("statoElencoUpdDeletables", statoElencoUpdDeletables);
        List<ElvElencoVer> elenchi = query.getResultList();
        if (elenchi.isEmpty()) {
            return false;
        } else {
            if (elenchi.get(0).getAroUpdUnitaDocs().size() > 0) {
                return true;
            }
            return false;
        }
    }

    public boolean areAllElenchiNonPresentiFirmati(List<BigDecimal> idElencoVersSelezionatiList, Date dataChiusura,
            BigDecimal idStrut) {
        String queryStr = "SELECT COUNT(elab) FROM ElvElencoVersDaElab elab JOIN elab.elvElencoVer u "
                + "WHERE u.dtChius < :dataChiusura "
                + "AND elab.elvElencoVer.idElencoVers NOT IN :idElencoVersSelezionatiList "
                + "AND elab.tiStatoElenco = 'CHIUSO' " + "AND elab.idStrut = :idStrut ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVersSelezionatiList", idElencoVersSelezionatiList);
        query.setParameter("dataChiusura", dataChiusura);
        query.setParameter("idStrut", idStrut);
        return (Long) query.getSingleResult() == 0;
    }

    public boolean existNomeElenco(String nmElenco, BigDecimal idStrut) {
        String queryStr = "SELECT u FROM ElvElencoVer u " + "WHERE u.orgStrut.idStrut = :idStrut "
                + "AND u.nmElenco = :nmElenco ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmElenco", nmElenco);
        query.setParameter("idStrut", idStrut);
        return !query.getResultList().isEmpty();
    }

    public void saveNomeDesNote(Long idUserIam, BigDecimal idElencoVers, String nmElenco, String dsElenco,
            String ntIndiceElenco, String ntElencoChiuso, List<ElencoEnums.OpTypeEnum> operList) {
        ElvElencoVer elenco = getEntityManager().find(ElvElencoVer.class, idElencoVers.longValue());
        elenco.setNmElenco(nmElenco);
        elenco.setDsElenco(dsElenco);
        elenco.setNtIndiceElenco(ntIndiceElenco);
        elenco.setNtElencoChiuso(ntElencoChiuso);
        /* A seconda di cosa ho modificato, scrivo nel log */
        for (ElencoEnums.OpTypeEnum oper : operList) {
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam, oper.name());
        }
    }

    public void removeAppartenenzaUdElenco(Long idUnitaDoc) {
        AroUnitaDoc unitaDoc = findById(AroUnitaDoc.class, idUnitaDoc);
        unitaDoc.setElvElencoVer(null);
        unitaDoc.setTiStatoUdElencoVers(UdDocStatusEnum.IN_ATTESA_SCHED.name());
    }

    public void removeAppartenenzaDocElenco(Long idDoc) {
        AroDoc doc = findById(AroDoc.class, idDoc);
        doc.setElvElencoVer(null);
        doc.setTiStatoDocElencoVers(DocStatusEnum.IN_ATTESA_SCHED.name());
    }
}
