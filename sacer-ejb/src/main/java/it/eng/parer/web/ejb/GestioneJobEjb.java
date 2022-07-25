package it.eng.parer.web.ejb;

import it.eng.parer.entity.DecJob;
import it.eng.parer.entity.DecJobFoto;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.slite.gen.form.GestioneJobForm;
import it.eng.parer.web.helper.GestioneJobHelper;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class GestioneJobEjb {

    private static final Logger LOGGER = LoggerFactory.getLogger(GestioneJobEjb.class);

    @EJB
    GestioneJobHelper helper;

    public void salvaFoto() {
        // Cancello tutti i record di DEC_JOB_FOTO
        helper.bulkDeleteDecJobFoto();
        // Copio tutti i record di DEC_JOB in DEC_JOB_FOTO
        helper.copyToFoto();
    }

    public void disabilitaAllJobs() {
        // Disattivo tutti i jobs
        helper.disabilitaAllJobs();
    }

    public boolean isDecJobFotoEmpty() {
        return helper.isDecJobFotoEmpty();
    }

    public boolean areAllJobsDisattivati() {
        return helper.areAllJobsDisattivati();
    }

    public void ripristinaFotoGestioneJob() {
        // Ripristino solo lo stato, se i dati di schedulazione sono non nulli,
        // altrimenti non ripristino lo stato.
        List<DecJobFoto> jobFotoList = helper.getEntityManager().createNamedQuery("DecJobFoto.findAll").getResultList();
        for (DecJobFoto jobFoto : jobFotoList) {
            // Se il job nella foto risulta schedulato
            if (jobFoto.getDtProssimaAttivazioneFoto() != null) {
                DecJob job = helper.getEntityManager().find(DecJob.class, jobFoto.getIdJobFoto());
                // Ripristino lo stato di schedulato (ATTIVO) solo se nel frattempo non ho annullato
                // i parametri di schedulazione
                if (job.getCdSchedHour() != null) {
                    job.setTiStatoTimer("ATTIVO");
                }
            }
        }
    }

    public BaseRow getInfoJobFotoRowBean() {
        // Recupero la data di ultima foto
        Date dataLastFoto = helper.getDataLastFotoJob();
        // Recupero il numero totale di job presenti nella foto
        int numJobFoto = helper.getNumJobFoto();
        // Recupero il numero di job attivi presenti nella foto
        int numJobAttivi = helper.getNumJobFotoAttivi();
        // 1) Recupero il numero di job "nuovi" in DEC_JOB (e non in foto)
        // 2) Recupero il numero di job in foto, eliminati da DEC_JOB
        int[] numJob = helper.getNumJobRimossiPresenti();
        int numJobNuovi = numJob[0];
        int numJobSoloFoto = numJob[1];
        BaseRow row = new BaseRow();
        row.setBigDecimal("ni_tot_job_presenti2", BigDecimal.valueOf(numJobFoto));
        row.setBigDecimal("ni_tot_job_attivi2", BigDecimal.valueOf(numJobAttivi));
        row.setBigDecimal("ni_tot_job_disattivi2", BigDecimal.valueOf(numJobFoto - numJobAttivi));
        row.setBigDecimal("ni_tot_job_nuovi2", BigDecimal.valueOf(numJobNuovi));
        row.setBigDecimal("ni_tot_job_solo_foto", BigDecimal.valueOf(numJobSoloFoto));
        row.setTimestamp("last_job_foto", new Timestamp(dataLastFoto.getTime()));
        return row;
    }

    public Object[] getInfoJobFotoNomiJobRowBean() {
        Object[] nmJobObj = helper.getNomiJobRimossiPresenti();
        return nmJobObj;
    }

    public BaseTable getAmbitoJob() {
        List<String> ambitoList = helper.getAmbitoJob();
        BaseTable ambitoTableBean = new BaseTable();
        try {
            for (String ambito : ambitoList) {
                BaseRow ambitoRowBean = new BaseRow();
                ambitoRowBean.setString("nm_ambito", ambito);
                ambitoTableBean.add(ambitoRowBean);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ambitoTableBean;
    }

    public BaseRow getInfoJobRowBean() {
        Object[] obj = helper.getInfoJob();
        Long numTot = (Long) obj[0];
        Long numTot2 = (Long) obj[1];
        BaseRow row = new BaseRow();
        row.setTimestamp("dt_stato_job", new Timestamp(new Date().getTime()));
        row.setBigDecimal("ni_tot_job_presenti", BigDecimal.valueOf(numTot));
        row.setBigDecimal("ni_tot_job_attivi", BigDecimal.valueOf(numTot2));
        row.setBigDecimal("ni_tot_job_disattivi", BigDecimal.valueOf(numTot - numTot2));
        return row;
    }

    public BaseTable getDecJobTableBean(GestioneJobForm.GestioneJobRicercaFiltri filtri) throws EMFError {
        BaseTable jobTableBean = new BaseTable();
        List<Object[]> listaJob = helper.getDecJobList(filtri.getNm_ambito().parse(), filtri.getDs_job().parse(),
                filtri.getTi_stato_job().parse());
        try {
            if (listaJob != null && !listaJob.isEmpty()) {
                for (Object[] job : listaJob) {
                    boolean attivo = false;
                    BaseRow row = new BaseRow();
                    row.setString("nm_job", ((String) job[0]));
                    row.setString("ds_job", (String) job[1]);
                    if (job[2] != null) {
                        row.setTimestamp("dt_prossima_attivazione", new Timestamp(((Date) job[2]).getTime()));
                        if ((String) job[9] == null
                                || (((String) job[9]).equals("ATTIVO") || ((String) job[9]).equals("DISATTIVO"))) {
                            attivo = true;
                        }
                    }
                    // Data ultima esecuzione
                    if (job[4] != null) {
                        row.setTimestamp("dt_ultima_esecuzione", new Timestamp(((Date) job[4]).getTime()));
                    }

                    String statoJob = "";
                    if (attivo) {
                        row.setString("stato_job", "ATTIVO");
                        row.setString("operazione_job_start", "0");
                        row.setString("operazione_job_single", "0");
                        row.setString("operazione_job_stop", "1");
                    } else if (job[5] != null && ((String) job[5]).equals("1")) {
                        row.setString("stato_job", "IN_ESECUZIONE");
                        row.setString("operazione_job_start", "0");
                        row.setString("operazione_job_stop", "0");
                        row.setString("operazione_job_single", "0");
                    } else {
                        row.setString("stato_job", "DISATTIVO");
                        row.setString("operazione_job_stop", "0");
                        row.setString("operazione_job_single", "1");
                        row.setString("operazione_job_start", "1");
                    }

                    // Ultima esecuzione OK
                    row.setString("last_exec_ok", (String) job[6]);

                    row.setString("job_selezionati", "0");

                    row.setString("nm_ambito", (String) job[7]);
                    row.setBigDecimal("ni_ord_exec", (BigDecimal) job[8]);

                    jobTableBean.add(row);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jobTableBean;
    }

    public BaseTable getDecJobTableBeanPerAmm() {
        List<Object[]> listaJob = helper.getDecJobListPerAmm();
        return populateBaseTableJob(listaJob);
    }

    public BaseTable getDecJobFotoTableBeanPerAmm() {
        List<Object[]> listaJob = helper.getDecJobFotoListPerAmm();
        return populateBaseTableJobFoto(listaJob);
    }

    public BaseTable populateBaseTableJob(List<Object[]> listaJob) {
        BaseTable jobTableBean = new BaseTable();
        try {
            if (listaJob != null && !listaJob.isEmpty()) {
                for (Object[] job : listaJob) {
                    boolean attivo = false;
                    BaseRow row = new BaseRow();
                    row.setString("nm_job", ((String) job[0]));
                    row.setString("ds_job", (String) job[1]);
                    if (job[2] != null) {
                        row.setTimestamp("dt_prossima_attivazione", new Timestamp(((Date) job[2]).getTime()));
                        if ((String) job[6] == null
                                || (((String) job[6]).equals("ATTIVO") || ((String) job[6]).equals("DISATTIVO"))) {
                            attivo = true;
                        }
                    }
                    // Data ultima esecuzione
                    if (job[4] != null) {
                        row.setTimestamp("dt_ultima_esecuzione", new Timestamp(((Date) job[4]).getTime()));
                    }

                    String statoJob = "";
                    if (attivo) {
                        row.setString("stato_job", "ATTIVO");

                    } else if (job[5] != null && ((String) job[5]).equals("1")) {
                        row.setString("stato_job", "IN_ESECUZIONE");

                    } else {
                        row.setString("stato_job", "DISATTIVO");

                    }

                    jobTableBean.add(row);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jobTableBean;
    }

    public BaseTable populateBaseTableJobFoto(List<Object[]> listaJob) {
        BaseTable jobTableBean = new BaseTable();
        try {
            if (listaJob != null && !listaJob.isEmpty()) {
                for (Object[] job : listaJob) {
                    boolean attivo = false;
                    BaseRow row = new BaseRow();
                    row.setString("nm_job", ((String) job[0]));
                    row.setString("ds_job", (String) job[1]);
                    if (job[2] != null) {
                        row.setTimestamp("dt_prossima_attivazione", new Timestamp(((Date) job[2]).getTime()));
                        if ((String) job[6] == null
                                || (((String) job[6]).equals("ATTIVO") || ((String) job[6]).equals("DISATTIVO"))) {
                            attivo = true;
                        }
                    }
                    // Data ultima esecuzione
                    if (job[4] != null) {
                        row.setTimestamp("dt_ultima_esecuzione", new Timestamp(((Date) job[4]).getTime()));
                    }

                    String statoJob = "";
                    if (attivo) {
                        row.setString("stato_job", "ATTIVO");

                    } else if (job[5] != null && ((String) job[5]).equals("1")) {
                        row.setString("stato_job", "DISATTIVO");

                    } else {
                        row.setString("stato_job", "DISATTIVO");

                    }

                    jobTableBean.add(row);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jobTableBean;
    }

}
