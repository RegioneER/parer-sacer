package it.eng.parer.job.tpi.helper;

import it.eng.parer.elencoVersamento.utils.PayLoad;
import it.eng.parer.entity.ElvDocAggDaElabElenco;
import it.eng.parer.entity.ElvUdVersDaElabElenco;
import it.eng.parer.entity.VrsArkPathDtVers;
import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.entity.VrsFileNoarkPathDtVers;
import it.eng.parer.entity.VrsPathDtVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.util.ejb.JmsProducerUtilEjb;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.tpi.bean.Aggregato;
import it.eng.tpi.bean.Archiviazione;
import it.eng.tpi.bean.EliminaCartellaArchiviataRisposta;
import it.eng.tpi.bean.FilePathNoArk;
import it.eng.tpi.bean.PathStrutture;
import it.eng.tpi.bean.StatoArchiviazioneCartellaRisposta;
import it.eng.tpi.dto.EsitoConnessione;
import it.eng.tpi.dto.RichiestaTpi;
import it.eng.tpi.dto.RichiestaTpiInput;
import it.eng.tpi.util.RichiestaWSTpi;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(mappedName = "AggiornaStatoArchiviazioneHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class AggiornaStatoArchiviazioneHelper {

    Logger log = LoggerFactory.getLogger(AggiornaStatoArchiviazioneHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    // MEV#27048
    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/ElenchiDaElabInAttesaSchedQueue")
    private Queue queue;
    // end MEV#27048
    @EJB
    private JobHelper jobHelper;
    @EJB
    private JmsProducerUtilEjb jmsProducerUtilEjb;

    public List<VrsDtVers> findArkDatesByStatus(String... tiStatoDtVers) {
        List<VrsDtVers> lstObjects;
        StringBuilder queryStr = new StringBuilder("SELECT v FROM VrsDtVers v");
        String cond = " WHERE ";
        if (tiStatoDtVers.length > 1) {
            queryStr.append(cond).append("v.tiStatoDtVers IN :stati");
        } else if (tiStatoDtVers.length > 0) {
            queryStr.append(cond).append("v.tiStatoDtVers = :stati");
        }
        queryStr.append(" ORDER BY v.dtVers DESC");
        javax.persistence.Query query = entityManager.createQuery(queryStr.toString());
        if (tiStatoDtVers.length > 1) {
            query.setParameter("stati", Arrays.asList(tiStatoDtVers));
        } else if (tiStatoDtVers.length > 0) {
            query.setParameter("stati", tiStatoDtVers[0]);
        }
        lstObjects = (List<VrsDtVers>) query.getResultList();

        return lstObjects;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean handleResponse(StatoArchiviazioneCartellaRisposta resp, Long idVrsDate, Map<String, String> params)
            throws ParerInternalError {
        VrsDtVers vrsDate = entityManager.find(VrsDtVers.class, idVrsDate);
        if (vrsDate.getTiStatoDtVers().equals(JobConstants.ArkStatusEnum.REGISTRATA.name())
                && resp.getFlDaArchiviare()) {
            vrsDate.setTiStatoDtVers(JobConstants.ArkStatusEnum.DA_ARCHIVIARE.name());
        }
        vrsDate.setFlArk(JobConstants.DB_TRUE);
        vrsDate.setFlFileNoArk(JobConstants.DB_FALSE);
        vrsDate.setFlPresenzaSecondario(
                resp.getFlPresenzaSitoSecondario() ? JobConstants.DB_TRUE : JobConstants.DB_FALSE);
        vrsDate.setFlArkSecondario(!resp.getFlPresenzaSitoSecondario() ? null : JobConstants.DB_TRUE);
        vrsDate.setFlFileNoArkSecondario(!resp.getFlPresenzaSitoSecondario() ? null : JobConstants.DB_FALSE);
        Date dtLastArk = null;
        Date dtLastArkSecondario = null;
        for (VrsPathDtVers path : vrsDate.getVrsPathDtVers()) {
            /*
             * Elimina i record di VRS_ARK_PATH_DT_VERS e VRS_FILE_NOARK_PATH_DT_VERS relativi al path nella data di
             * versamento
             */
            log.debug(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- gestisco il path "
                    + path.getDlPath());

            deleteArkPath(VrsArkPathDtVers.class.getSimpleName(), path.getIdPathDtVers());
            deleteArkPath(VrsFileNoarkPathDtVers.class.getSimpleName(), path.getIdPathDtVers());

            path.setFlPathArk(JobConstants.DB_TRUE);
            path.setFlPathFileNoArk(JobConstants.DB_FALSE);
            path.setFlPathArkSecondario(!resp.getFlPresenzaSitoSecondario() ? null : JobConstants.DB_TRUE);
            path.setFlPathFileNoArkSecondario(!resp.getFlPresenzaSitoSecondario() ? null : JobConstants.DB_FALSE);

            Aggregato aggregato = null;
            PathStrutture pathStrutture = null;
            Date dtLastArkPath = null;
            Date dtLastArkPathSecondario = null;
            // Ricerco l'aggregato che contiene il path
            for (Aggregato aggr : resp.getListaAggreg()) {
                for (PathStrutture pathStrut : aggr.getListaPath()) {
                    if (pathStrut.getDsPath().equals(path.getDlPath())) {
                        pathStrutture = pathStrut;
                        break;
                    }
                }
                if (pathStrutture != null) {
                    aggregato = aggr;
                    break;
                }
            }
            if (aggregato != null && pathStrutture != null) {
                log.debug(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                        + " --- Path contenuto nell'aggregato " + aggregato.getDsAggreg());
                if (aggregato.getListaArk() == null || aggregato.getListaArk().isEmpty()) {
                    path.setFlPathArk(JobConstants.DB_FALSE);
                    path.setFlPathFileNoArk(null);
                    vrsDate.setFlArk(JobConstants.DB_FALSE);
                    vrsDate.setFlFileNoArk(null);
                } else {
                    for (Archiviazione ark : aggregato.getListaArk()) {
                        VrsArkPathDtVers arkPath = new VrsArkPathDtVers();
                        arkPath.setDsArk(ark.getDsArk());
                        arkPath.setDtArkPathDtVers(ark.getDtArk());
                        arkPath.setTiArkPath(JobConstants.ArkPath.PRIMARIO.name());
                        arkPath.setVrsPathDtVers(path);
                        path.getVrsArkPathDtVers().add(arkPath);
                        entityManager.persist(arkPath);

                        if (dtLastArkPath == null || dtLastArkPath.compareTo(ark.getDtArk()) < 0) {
                            dtLastArkPath = ark.getDtArk();
                        }
                    }
                    if (pathStrutture.getListaFileNoArk() != null && !pathStrutture.getListaFileNoArk().isEmpty()) {
                        path.setFlPathFileNoArk(JobConstants.DB_TRUE);
                        vrsDate.setFlFileNoArk(JobConstants.DB_TRUE);

                        for (FilePathNoArk filePathNoArk : pathStrutture.getListaFileNoArk()) {
                            VrsFileNoarkPathDtVers vrsFileNoArk = new VrsFileNoarkPathDtVers();
                            vrsFileNoArk.setDsFileNoark(filePathNoArk.getDlFile());
                            vrsFileNoArk.setTiArkFileNoark(JobConstants.ArkPath.PRIMARIO.name());
                            vrsFileNoArk.setVrsPathDtVers(path);
                            path.getVrsFileNoarkPathDtVers().add(vrsFileNoArk);
                            entityManager.persist(vrsFileNoArk);
                        }
                    }
                    //
                    path.setNiFilePathArk(pathStrutture.getNiFilePathArk());
                }
                if (resp.getFlPresenzaSitoSecondario()) {
                    if (aggregato.getListaArkSecondario() == null || aggregato.getListaArkSecondario().isEmpty()) {
                        path.setFlPathArkSecondario(JobConstants.DB_FALSE);
                        path.setFlPathFileNoArkSecondario(null);
                        vrsDate.setFlArkSecondario(JobConstants.DB_FALSE);
                        vrsDate.setFlFileNoArkSecondario(null);
                    } else {
                        for (Archiviazione ark : aggregato.getListaArkSecondario()) {
                            VrsArkPathDtVers arkPath = new VrsArkPathDtVers();
                            arkPath.setDsArk(ark.getDsArk());
                            arkPath.setDtArkPathDtVers(ark.getDtArk());
                            arkPath.setTiArkPath(JobConstants.ArkPath.SECONDARIO.name());
                            arkPath.setVrsPathDtVers(path);
                            path.getVrsArkPathDtVers().add(arkPath);
                            entityManager.persist(arkPath);

                            if (dtLastArkPathSecondario == null
                                    || dtLastArkPathSecondario.compareTo(ark.getDtArk()) < 0) {
                                dtLastArkPathSecondario = ark.getDtArk();
                            }
                        }
                        if (pathStrutture.getListaFileNoArkSecondario() != null
                                && !pathStrutture.getListaFileNoArkSecondario().isEmpty()) {
                            path.setFlPathFileNoArkSecondario(JobConstants.DB_TRUE);
                            vrsDate.setFlFileNoArkSecondario(JobConstants.DB_TRUE);

                            for (FilePathNoArk filePathNoArk : pathStrutture.getListaFileNoArkSecondario()) {
                                VrsFileNoarkPathDtVers vrsFileNoArk = new VrsFileNoarkPathDtVers();
                                vrsFileNoArk.setDsFileNoark(filePathNoArk.getDlFile());
                                vrsFileNoArk.setTiArkFileNoark(JobConstants.ArkPath.SECONDARIO.name());
                                vrsFileNoArk.setVrsPathDtVers(path);
                                path.getVrsFileNoarkPathDtVers().add(vrsFileNoArk);
                                entityManager.persist(vrsFileNoArk);
                            }
                        }
                        //
                        path.setNiFilePathArkSecondario(pathStrutture.getNiFilePathArkSecondario());
                    }
                }
                path.setDtLastArkPath(dtLastArkPath);
                path.setDtLastArkPathSecondario(dtLastArkPathSecondario);

                if (dtLastArk == null || (dtLastArkPath != null && dtLastArk.compareTo(dtLastArkPath) < 0)) {
                    dtLastArk = dtLastArkPath;
                }
                if (dtLastArkSecondario == null || (dtLastArkPathSecondario != null
                        && dtLastArkSecondario.compareTo(dtLastArkPathSecondario) < 0)) {
                    dtLastArkSecondario = dtLastArkPathSecondario;
                }
            } else {
                // Aggregato o pathStrutture inesistente
                SimpleDateFormat df = new SimpleDateFormat(Costanti.UrnFormatter.SPATH_DATA_FMT_STRING);
                log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- Il path " + path.getDlPath()
                        + " non è presente in nessun aggregato esistente per la data di versamento "
                        + df.format(vrsDate.getDtVers()));
                // Aggregato o pathStrutture inaspettatamente inesistente registro la sessione
                // con stato di errore e chiudo il job
                throw new ParerInternalError("Il path " + path.getDlPath()
                        + " non è presente in nessun aggregato esistente per la data di versamento "
                        + df.format(vrsDate.getDtVers()));
            }
        }
        vrsDate.setDtLastArkDtVers(dtLastArk);
        vrsDate.setDtLastArkDtVersSecondario(dtLastArkSecondario);

        if (vrsDate.getFlArk().equals(JobConstants.DB_TRUE)) {
            if (vrsDate.getFlArkSecondario() != null) {
                if (vrsDate.getFlArkSecondario().equals(JobConstants.DB_TRUE)) {
                    if (vrsDate.getFlFileNoArk().equals(JobConstants.DB_FALSE)
                            && vrsDate.getFlFileNoArkSecondario().equals(JobConstants.DB_FALSE)) {
                        vrsDate.setTiStatoDtVers(JobConstants.ArkStatusEnum.ARCHIVIATA.name());
                    } else {
                        if (!resp.getFlDaRiArchiviare()) {
                            vrsDate.setTiStatoDtVers(JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name());
                        }
                    }
                }
            } else {
                if (vrsDate.getFlFileNoArk().equals(JobConstants.DB_FALSE)) {
                    vrsDate.setTiStatoDtVers(JobConstants.ArkStatusEnum.ARCHIVIATA.name());
                } else if (!resp.getFlDaRiArchiviare()) {
                    vrsDate.setTiStatoDtVers(JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name());
                }
            }
        }
        log.debug(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- Data aggiornata con stato "
                + vrsDate.getTiStatoDtVers());
        List<ElvUdVersDaElabElenco> tmpUdDaElabElencoList = new ArrayList<>();
        List<ElvDocAggDaElabElenco> tmpDocDaElabElencoList = new ArrayList<>();
        boolean jobChiusoOk = true;
        // Ottengo la lista di strutture con tiSaveFile = FILE, che mi serviranno nelle
        // prossime operazioni
        Query query = entityManager.createQuery(
                "SELECT DISTINCT tipoUd.orgStrut.idStrut from DecTipoUnitaDoc tipoUd where tipoUd.tiSaveFile = 'FILE'");
        List<Long> idStrutList = query.getResultList();
        if (vrsDate.getTiStatoDtVers().equals(JobConstants.ArkStatusEnum.ARCHIVIATA.name())) {
            boolean flMigraz = vrsDate.getFlMigraz().equals(JobConstants.DB_TRUE);
            Calendar from = Calendar.getInstance();
            from.setTime(vrsDate.getDtVers());
            from.set(Calendar.HOUR_OF_DAY, 0);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);
            Calendar to = Calendar.getInstance();
            to.setTime(vrsDate.getDtVers());
            to.set(Calendar.HOUR_OF_DAY, 23);
            to.set(Calendar.MINUTE, 59);
            to.set(Calendar.SECOND, 59);
            if (flMigraz) {
                log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                        + " --- MIGRAZIONE - Aggiorna tutti i path assegnando flCancFileMigraz = false");
                // Analisi aggiornata - Aggiorna tutti i path assegnando flCancFileMigraz =
                // false
                updateFlCancVrsPaths(vrsDate.getIdDtVers());
            } else {
                log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                        + " --- VERSAMENTO - Eseguo l'update tutte le UD con data creazione "
                        + "inclusa nella data di versamento corrente, assegnando stato = IN_ATTESA_SCHED");
                tmpUdDaElabElencoList = updateUdDaElabElenco(from.getTime(), to.getTime(), idStrutList);
                log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                        + " --- VERSAMENTO - Eseguo l'update tutti i documenti con data creazione "
                        + "inclusa nella data di versamento corrente, assegnando stato = IN_ATTESA_SCHED");
                tmpDocDaElabElencoList = updateDocDaElabElenco(from.getTime(), to.getTime(), idStrutList);
            }

            SimpleDateFormat requestDateFormat = new SimpleDateFormat("ddMMyyyy");
            String dateString = requestDateFormat.format(vrsDate.getDtVers());
            log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                    + " --- chiamo il servizio di elimina cartella archiviazione per la data " + dateString);

            String urlRequest = params.get(CostantiDB.ParametroAppl.TPI_TPI_HOST_URL)
                    + params.get(CostantiDB.ParametroAppl.TPI_URL_ELIMINACARTELLAARK);
            Integer timeout = Integer.parseInt(params.get(CostantiDB.ParametroAppl.TPI_TIMEOUT));
            RichiestaTpiInput inputParams = new RichiestaTpiInput(RichiestaTpi.TipoRichiesta.ELIMINA_CARTELLA_ARK,
                    urlRequest, timeout,
                    new BasicNameValuePair(RichiestaTpi.NM_USER, params.get(CostantiDB.ParametroAppl.TPI_NM_USER_TPI)),
                    new BasicNameValuePair(RichiestaTpi.CD_PSW, params.get(CostantiDB.ParametroAppl.TPI_CD_PSW_TPI)),
                    new BasicNameValuePair(RichiestaTpi.FL_CARTELLA_MIGRAZ, String.valueOf(flMigraz)),
                    new BasicNameValuePair(RichiestaTpi.DT_VERS, dateString),
                    new BasicNameValuePair(RichiestaTpi.ROOT_DT_VERS,
                            (flMigraz ? params.get(CostantiDB.ParametroAppl.TPI_ROOT_ARK_MIGRAZ)
                                    : params.get(CostantiDB.ParametroAppl.TPI_ROOT_ARK_VERS))));

            EsitoConnessione esitoConn = RichiestaWSTpi.callWs(inputParams);
            String codiceErrore = esitoConn.getCodiceErrore();
            String codiceEsito = esitoConn.getCodiceEsito();
            String messaggioErrore = esitoConn.getMessaggioErrore();

            if (esitoConn.isErroreConnessione()) {
                log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- EliminaCartellaArk - "
                        + esitoConn.getDescrErrConnessione());
                // Il servizio non ha risposto per un errore di connessione
                // Registro l'errore e chiudo il job
                jobHelper.writeAtomicLogJob(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(),
                        JobConstants.OpTypeEnum.ERRORE.name(), "Timeout dal servizio EliminaCartellaArk");
                jobChiusoOk = false;
            } else if (codiceEsito.equals(EsitoConnessione.Esito.KO.name())) {
                log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name() + " --- EliminaCartellaArk - "
                        + codiceErrore + " - " + messaggioErrore);
                // se il risultato è stato inaspettatamente NEGATIVO registro la sessione con
                // stato di errore e chiudo il job
                throw new ParerInternalError("EliminaCartellaArk - " + codiceErrore + " - " + messaggioErrore);
            } else {
                // risultato OK! Mi tengo l'oggetto di risposta
                EliminaCartellaArchiviataRisposta eliminaResp = (EliminaCartellaArchiviataRisposta) esitoConn
                        .getResponse();
                if (eliminaResp != null) {
                    log.debug(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                            + " --- Servizio EliminaCartellaArk OK");
                } else {
                    log.error(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                            + " --- Risposta inaspettata dal servizio EliminaCartellaArk");
                    throw new ParerInternalError("Risposta inaspettata dal servizio EliminaCartellaArk");
                }
            }
        }
        /*
         * Se non ci sono stati problemi nella eventuale chiamata di EliminaCartellaArk e se la data di versamento
         * corrente ha stato = DA_ARCHIVIARE o ARCHIVIATA o ARCHIVIATA_ERR e se per la data di versamento esiste almeno
         * un record in VRS_PATH_DT_VERS con ni_file_path nullo) aggiorna tutti i record VRS_PATH_DT_VERS della data di
         * versamento corrente, calcolando ni_file_path
         */
        if (jobChiusoOk
                && (vrsDate.getTiStatoDtVers().equals(JobConstants.ArkStatusEnum.DA_ARCHIVIARE.name())
                        || vrsDate.getTiStatoDtVers().equals(JobConstants.ArkStatusEnum.ARCHIVIATA.name())
                        || vrsDate.getTiStatoDtVers().equals(JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name()))
                && checkNiFilePath(vrsDate.getIdDtVers())) {
            Calendar from = Calendar.getInstance();
            from.setTime(vrsDate.getDtVers());
            from.set(Calendar.HOUR_OF_DAY, 0);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);
            Calendar to = Calendar.getInstance();
            to.setTime(vrsDate.getDtVers());
            to.set(Calendar.HOUR_OF_DAY, 23);
            to.set(Calendar.MINUTE, 59);
            to.set(Calendar.SECOND, 59);

            for (Long strut : idStrutList) {
                Long count = getComponentCount(strut, from.getTime(), to.getTime());
                String pathString = getPathString(strut);
                VrsPathDtVers path = getPathDtVers(vrsDate.getIdDtVers(), pathString);
                if (path != null) {
                    path.setNiFilePath(new BigDecimal(count));
                }
            }
        }
        //
        // MEV#27048
        if (tmpUdDaElabElencoList != null && !tmpUdDaElabElencoList.isEmpty()) {
            log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                    + " --- VERSAMENTO - Invio dei messaggi in coda per tutte le UD con data creazione "
                    + "inclusa nella data di versamento corrente, con stato = IN_ATTESA_SCHED");
            for (ElvUdVersDaElabElenco elenco : tmpUdDaElabElencoList) {
                PayLoad pl = new PayLoad();
                pl.setTipoEntitaSacer("UNI_DOC");
                pl.setStato(CostantiDB.TipiStatoElementoVersato.IN_ATTESA_SCHED.name());
                pl.setId(elenco.getAroUnitaDoc().getIdUnitaDoc());
                pl.setIdStrut(elenco.getIdStrut().longValue());
                pl.setAaKeyUnitaDoc(elenco.getAaKeyUnitaDoc().longValue());
                pl.setDtCreazione(elenco.getDtCreazione().getTime());

                jmsProducerUtilEjb.manageMessageGroupingInFormatoJson(connectionFactory, queue, pl,
                        "CodaElenchiDaElabInAttesaSched", String.valueOf(pl.getIdStrut()));
            }
        }

        if (tmpDocDaElabElencoList != null && !tmpDocDaElabElencoList.isEmpty()) {
            log.info(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name()
                    + " --- VERSAMENTO - Invio dei messaggi in coda per tutti i documenti con data creazione "
                    + "inclusa nella data di versamento corrente, con stato = IN_ATTESA_SCHED");
            for (ElvDocAggDaElabElenco elenco : tmpDocDaElabElencoList) {
                PayLoad pl = new PayLoad();
                pl.setTipoEntitaSacer("DOC");
                pl.setStato(CostantiDB.TipiStatoElementoVersato.IN_ATTESA_SCHED.name());
                pl.setId(elenco.getAroDoc().getIdDoc());
                pl.setIdStrut(elenco.getIdStrut().longValue());
                pl.setAaKeyUnitaDoc(elenco.getAaKeyUnitaDoc().longValue());
                pl.setDtCreazione(elenco.getDtCreazione().getTime());

                jmsProducerUtilEjb.manageMessageGroupingInFormatoJson(connectionFactory, queue, pl,
                        "CodaElenchiDaElabInAttesaSched", String.valueOf(pl.getIdStrut()));
            }
        }
        // end MEV#27048
        //
        return jobChiusoOk;
    }

    private void deleteArkPath(String table, Long idPath) {
        String queryStr = "DELETE FROM " + table + " tb WHERE tb.vrsPathDtVers.idPathDtVers = :idPath";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idPath", idPath);
        q.executeUpdate();
        entityManager.flush();
    }

    private void updateFlCancVrsPaths(Long idDtVers) {
        Query q = entityManager.createQuery(
                "UPDATE VrsPathDtVers path SET path.flCancFileMigraz = :flag WHERE path.vrsDtVers.idDtVers = :idDtVers");
        q.setParameter("idDtVers", idDtVers);
        q.setParameter("flag", JobConstants.DB_FALSE);
        q.executeUpdate();
        entityManager.flush();
    }

    private List<ElvUdVersDaElabElenco> updateUdDaElabElenco(Date from, Date to, List<Long> idStrutList) {
        List<ElvUdVersDaElabElenco> tmpUdElabElencoList = new ArrayList<>();
        for (Long idStrut : idStrutList) {
            String queryStr = "SELECT deel from ElvUdVersDaElabElenco deel " + "where deel.idStrut = :idStrut "
                    + "AND deel.tiStatoUdDaElab =  :tiStato "
                    + "AND deel.dtCreazione between :dtVersFrom and :dtVersTo ";
            Query q = entityManager.createQuery(queryStr);
            q.setParameter("idStrut", idStrut);
            q.setParameter("tiStato", CostantiDB.TipiStatoElementoVersato.IN_ATTESA_MEMORIZZAZIONE.name());
            q.setParameter("dtVersFrom", from);
            q.setParameter("dtVersTo", to);
            List<ElvUdVersDaElabElenco> udVersDaElabElencoList = q.getResultList();
            for (ElvUdVersDaElabElenco elenco : udVersDaElabElencoList) {
                elenco.setTiStatoUdDaElab(CostantiDB.TipiStatoElementoVersato.IN_ATTESA_SCHED.name());
                elenco.getAroUnitaDoc()
                        .setTiStatoUdElencoVers(CostantiDB.TipiStatoElementoVersato.IN_ATTESA_SCHED.name());
            }
            tmpUdElabElencoList.addAll(udVersDaElabElencoList);
        }
        entityManager.flush();

        return tmpUdElabElencoList;
    }

    private List<ElvDocAggDaElabElenco> updateDocDaElabElenco(Date from, Date to, List<Long> idStrutList) {
        List<ElvDocAggDaElabElenco> tmpDocElabElencoList = new ArrayList<>();
        for (Long idStrut : idStrutList) {
            String queryStr = "SELECT deel from ElvDocAggDaElabElenco deel " + "where deel.idStrut = :idStrut "
                    + "AND deel.tiStatoDocDaElab =  :tiStato "
                    + "AND deel.dtCreazione between :dtVersFrom and :dtVersTo ";
            Query q = entityManager.createQuery(queryStr);
            q.setParameter("idStrut", idStrut);
            q.setParameter("tiStato", CostantiDB.TipiStatoElementoVersato.IN_ATTESA_MEMORIZZAZIONE.name());
            q.setParameter("dtVersFrom", from);
            q.setParameter("dtVersTo", to);
            List<ElvDocAggDaElabElenco> docAggDaElabElencoList = q.getResultList();
            for (ElvDocAggDaElabElenco elenco : docAggDaElabElencoList) {
                elenco.setTiStatoDocDaElab(CostantiDB.TipiStatoElementoVersato.IN_ATTESA_SCHED.name());
                elenco.getAroDoc().setTiStatoDocElencoVers(CostantiDB.TipiStatoElementoVersato.IN_ATTESA_SCHED.name());
            }
            tmpDocElabElencoList.addAll(docAggDaElabElencoList);
        }
        entityManager.flush();

        return tmpDocElabElencoList;
    }

    private boolean checkNiFilePath(Long idDtVers) {
        Query query = entityManager.createQuery(
                "SELECT count(path) FROM VrsPathDtVers path WHERE path.vrsDtVers.idDtVers = :idDtVers AND (path.niFilePath IS NULL OR path.niFilePath = 0)");
        query.setParameter("idDtVers", idDtVers);
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    private Long getComponentCount(Long idStrut, Date from, Date to) {
        Query query = entityManager
                .createQuery("SELECT COUNT(comp) FROM AroCompDoc comp JOIN comp.aroStrutDoc strutDoc "
                        + "JOIN strutDoc.aroDoc doc JOIN doc.aroUnitaDoc ud JOIN ud.decTipoUnitaDoc tipoUd "
                        + "WHERE doc.idStrut = :idStrut " + "AND doc.dtCreazione between :dtVersFrom and :dtVersTo "
                        + "AND tipoUd.tiSaveFile = 'FILE' " + "AND comp.tiSupportoComp = 'FILE'");
        query.setParameter("idStrut", idStrut);
        query.setParameter("dtVersFrom", from);
        query.setParameter("dtVersTo", to);
        return (Long) query.getSingleResult();
    }

    private VrsPathDtVers getPathDtVers(Long idDtVers, String path) {
        Query query = entityManager.createQuery(
                "SELECT path FROM VrsPathDtVers path WHERE path.vrsDtVers.idDtVers = :idDtVers AND path.dlPath = :path");
        query.setParameter("idDtVers", idDtVers);
        query.setParameter("path", path);
        List<VrsPathDtVers> listaPath = query.getResultList();
        if (listaPath != null && !listaPath.isEmpty()) {
            return listaPath.get(0);
        }
        return null;
    }

    private String getPathString(Long idStrut) {
        Query query = entityManager.createQuery("SELECT CONCAT(amb.nmAmbiente, '-', ente.nmEnte,'-', strut.nmStrut) "
                + "FROM OrgStrut strut JOIN strut.orgEnte ente " + "JOIN ente.orgAmbiente amb "
                + "WHERE strut.idStrut = :idStrut");
        query.setParameter("idStrut", idStrut);
        return (String) query.getSingleResult();
    }
}
