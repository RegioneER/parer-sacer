package it.eng.parer.job.tpi.helper;

import it.eng.parer.entity.TpiDtSched;
import it.eng.parer.entity.TpiErrArk;
import it.eng.parer.entity.TpiPathElab;
import it.eng.parer.entity.TpiSchedJob;
import it.eng.parer.job.utils.JobConstants;
import it.eng.tpi.bean.Job;
import it.eng.tpi.bean.JobErrArk;
import it.eng.tpi.bean.PathElab;
import it.eng.tpi.bean.SchedulazioniJobTPIRisposta;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "RegistraSchedulazioniJobTPIHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class RegistraSchedulazioniJobTPIHelper {

    Logger log = LoggerFactory.getLogger(RegistraSchedulazioniJobTPIHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private RegistraSchedulazioniJobTPIHelper me;

    public Date findLastDaySched() {
        javax.persistence.Query query = entityManager.createQuery("SELECT t FROM TpiDtSched t order by t.dtSched desc");
        query.setMaxResults(1);
        List<TpiDtSched> lstObjects = (List<TpiDtSched>) query.getResultList();
        return lstObjects.get(0).getDtSched();
    }

    public List<TpiDtSched> getTpiDtSchedbyStatus(String status) {
        javax.persistence.Query query = entityManager
                .createQuery("SELECT t FROM TpiDtSched t WHERE t.tiStatoDtSched = :status order by t.dtSched");
        query.setParameter("status", status);
        List<TpiDtSched> lstObjects = (List<TpiDtSched>) query.getResultList();
        return lstObjects;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaDataRegistrata(Date date) {
        TpiDtSched dt = new TpiDtSched();
        dt.setDtSched(date);
        dt.setTiStatoDtSched(JobConstants.StatoSchedJob.REGISTRATA.name());
        dt.setFlMigrazInCorso(JobConstants.DB_FALSE);
        dt.setFlPresenzaSecondario(JobConstants.DB_FALSE);
        entityManager.persist(dt);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void handleResp(SchedulazioniJobTPIRisposta resp, Long idDtSched, Date currentDate) {
        TpiDtSched dtSched = entityManager.find(TpiDtSched.class, idDtSched);
        dtSched.setFlMigrazInCorso(resp.getFlMigrazInCorso() ? JobConstants.DB_TRUE : JobConstants.DB_FALSE);
        dtSched.setFlPresenzaSecondario(
                resp.getFlPresenzaSitoSecondario() ? JobConstants.DB_TRUE : JobConstants.DB_FALSE);
        // elimina i record di TPI_SCHED_JOB relativi alla data di schedulazione corrente
        me.deleteSchedJob(idDtSched);
        // ricrea i record
        for (Job job : resp.getListaJob()) {
            log.debug("{} --- Creo i record di schedulazione per il PRIMARIO",
                    JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI);
            me.createJobRecords(job, dtSched, JobConstants.ArkPath.PRIMARIO.name());
        }
        for (Job job : resp.getListaJobSecondario()) {
            log.debug("{} --- Creo i record di schedulazione per il SECONDARIO",
                    JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI);
            me.createJobRecords(job, dtSched, JobConstants.ArkPath.SECONDARIO.name());
            log.debug("{} --- Fine creazione record", JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI);
        }
        entityManager.flush();
        if (dtSched.getDtSched().equals(currentDate)
                && (resp.getListaJob().size() > 0 || resp.getListaJobSecondario().size() > 0)) {
            log.info("{} --- Imposto le date precedenti il {} a CONSOLIDATA",
                    JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI, dtSched.getDtSched());
            updateConsolidate(currentDate);
        }
    }

    public void deleteSchedJob(Long idDtSched) {
        String queryStr = "DELETE FROM TpiSchedJob tb WHERE tb.tpiDtSched.idDtSched = :idDtSched";
        Query q = entityManager.createQuery(queryStr);
        q.setParameter("idDtSched", idDtSched);
        q.executeUpdate();
        entityManager.flush();
    }

    public void createJobRecords(Job job, TpiDtSched dtSched, String tiTpiSchedJob) {
        TpiSchedJob sched = new TpiSchedJob();
        sched.setTiTpiSchedJob(tiTpiSchedJob);
        sched.setNmJob(job.getNmJob());
        sched.setDtSchedJob(job.getDtSchedJob());
        sched.setFlJobOk(
                job.getFlJobOk() != null ? (job.getFlJobOk() ? JobConstants.DB_TRUE : JobConstants.DB_FALSE) : null);
        sched.setFlMigraz(job.getFlMigraz() ? JobConstants.DB_TRUE : JobConstants.DB_FALSE);
        sched.setDlErrJob(job.getDlErrJob());
        sched.setDsDurataJob(job.getDsDurataJob());
        sched.setNiOrdSchedJob(new BigDecimal(job.getNiOrdSchedJob().intValue()));

        if (job.getListaErrArk() != null && !job.getListaErrArk().isEmpty()) {
            if (sched.getTpiErrArks() == null) {
                sched.setTpiErrArks(new ArrayList<TpiErrArk>());
            }
            for (JobErrArk jobErr : job.getListaErrArk()) {
                TpiErrArk error = new TpiErrArk();
                error.setCdErrArk(jobErr.getCdErrArk());
                error.setDlErrArk(jobErr.getDsErrArk());
                error.setNiErrArk(new BigDecimal(jobErr.getNiErrArk().intValue()));
                error.setTiErrArk(jobErr.getTiErrArk());
                error.setTpiSchedJob(sched);
                sched.getTpiErrArks().add(error);
            }
        }

        if (job.getListaPathElab() != null && !job.getListaPathElab().isEmpty()) {
            if (sched.getTpiPathElabs() == null) {
                sched.setTpiPathElabs(new ArrayList<TpiPathElab>());
            }
            for (PathElab jobPath : job.getListaPathElab()) {
                TpiPathElab path = new TpiPathElab();
                path.setDlPathElab(jobPath.getDsPath());
                path.setDtVersElab(jobPath.getDtSched());
                path.setNiFileDaElab(new BigDecimal(jobPath.getNiFileDaElab()));
                path.setNiFileElab(new BigDecimal(jobPath.getNiFileElab()));
                path.setTpiSchedJob(sched);
                sched.getTpiPathElabs().add(path);
            }
        }

        sched.setTpiDtSched(dtSched);
        if (dtSched.getTpiSchedJobs() == null) {
            dtSched.setTpiSchedJobs(new ArrayList<TpiSchedJob>());
        }
        dtSched.getTpiSchedJobs().add(sched);
    }

    public void updateConsolidate(Date data) {
        javax.persistence.Query query = entityManager
                .createQuery("UPDATE TpiDtSched t " + "SET t.tiStatoDtSched = :status "
                        + "WHERE t.dtSched < :dtSchedIn " + "and t.tiStatoDtSched = :tiStatoDtSchedIn");

        query.setParameter("status", JobConstants.StatoSchedJob.CONSOLIDATA.name());
        query.setParameter("dtSchedIn", data);
        query.setParameter("tiStatoDtSchedIn", JobConstants.StatoSchedJob.REGISTRATA.name());
        query.executeUpdate();
        entityManager.flush();
    }
}
