package it.eng.parer.web.action;

import it.eng.parer.jboss.timer.service.JbossTimerEjb;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.GestioneJobAbstractAction;
import it.eng.parer.slite.gen.form.GestioneJobForm.GestioneJobRicercaFiltri;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.viewbean.LogVVisLastSchedRowBean;
import it.eng.parer.web.ejb.GestioneJobEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.GestioneJobHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import javax.ejb.EJB;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
public class GestioneJobAction extends GestioneJobAbstractAction {

    private final Logger LOG = LoggerFactory.getLogger(GestioneJobAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    @EJB(mappedName = "java:app/Parer-ejb/GestioneJobHelper")
    private GestioneJobHelper gestioneJobHelper;

    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb jbossTimerEjb;

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    @EJB(mappedName = "java:app/Parer-ejb/GestioneJobEjb")
    private GestioneJobEjb gestioneJobEjb;

    private enum OPERAZIONE {
        START("lancio il timer"), ESECUZIONE_SINGOLA("esecuzione singola"), STOP("stop");

        protected String desc;

        OPERAZIONE(String desc) {
            this.desc = desc;
        }

        public String description() {
            return desc;
        }
    };

    /**
     * Returns the activation date of job otherwise <code>null</code>
     *
     * @param jobName
     *            the job name
     *
     * @return
     *
     * @throws EMFError
     */
    private Timestamp getActivationDateJob(String jobName) throws EMFError {
        Timestamp res = null;
        LogVVisLastSchedRowBean rb = monitoraggioHelper.getLogVVisLastSchedRowBean(jobName);

        if (rb.getFlJobAttivo() != null) {
            if (rb.getFlJobAttivo().equals("1")) {
                res = rb.getDtRegLogJobIni();
            }
        }

        return res;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.GESTIONE_JOB;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.GESTIONE_JOB;
    }

    @Secure(action = "Menu.Amministrazione.GestioneJob")
    @Override
    public void initOnClick() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneJob");

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione elenchi">
        Timestamp dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name());
        StatoJob creazioneElenchi = new StatoJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(),
                getForm().getCreazioneElenchi().getFl_data_accurata(),
                getForm().getCreazioneElenchi().getStartCreazioneElenchi(),
                getForm().getCreazioneElenchi().getStartOnceCreazioneElenchi(),
                getForm().getCreazioneElenchi().getStopCreazioneElenchi(),
                getForm().getCreazioneElenchi().getDt_prossima_attivazione(),
                getForm().getCreazioneElenchi().getAttivo(), getForm().getCreazioneElenchi().getDt_reg_log_job_ini(),
                dataAttivazioneJob);

        gestisciStatoJob(creazioneElenchi);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione indici">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS.name());
        StatoJob creazioneIndici = new StatoJob(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(),
                getForm().getCreazioneIndici().getFl_data_accurata(),
                getForm().getCreazioneIndici().getStartCreazioneIndici(),
                getForm().getCreazioneIndici().getStartOnceCreazioneIndici(),
                getForm().getCreazioneIndici().getStopCreazioneIndici(),
                getForm().getCreazioneIndici().getDt_prossima_attivazione(), getForm().getCreazioneIndici().getAttivo(),
                getForm().getCreazioneIndici().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneIndici);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Verifica firme">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.VERIFICA_FIRME_A_DATA_VERS.name());
        StatoJob verificaFirme = new StatoJob(JobConstants.JobEnum.VERIFICA_FIRME_A_DATA_VERS.name(),
                getForm().getVerificaFirme().getFl_data_accurata(),
                getForm().getVerificaFirme().getStartVerificaFirme(),
                getForm().getVerificaFirme().getStartOnceVerificaFirme(),
                getForm().getVerificaFirme().getStopVerificaFirme(),
                getForm().getVerificaFirme().getDt_prossima_attivazione(), getForm().getVerificaFirme().getAttivo(),
                getForm().getVerificaFirme().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(verificaFirme);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Coda indici aip da elaborare">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name());
        StatoJob codaIndiciAipDaElab = new StatoJob(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(),
                getForm().getCodaIndiciAipDaElab().getFl_data_accurata(),
                getForm().getCodaIndiciAipDaElab().getStartCodaIndiciAipDaElab(),
                getForm().getCodaIndiciAipDaElab().getStartOnceCodaIndiciAipDaElab(),
                getForm().getCodaIndiciAipDaElab().getStopCodaIndiciAipDaElab(),
                getForm().getCodaIndiciAipDaElab().getDt_prossima_attivazione(),
                getForm().getVerificaFirme().getAttivo(), getForm().getCodaIndiciAipDaElab().getDt_reg_log_job_ini(),
                dataAttivazioneJob);

        gestisciStatoJob(codaIndiciAipDaElab);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Calcolo contenuto Sacer">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name());
        StatoJob calcoloContenuto = new StatoJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name(),
                getForm().getCalcoloContenutoSacer().getFl_data_accurata(),
                getForm().getCalcoloContenutoSacer().getStartCalcoloContenutoSacer(),
                getForm().getCalcoloContenutoSacer().getStartOnceCalcoloContenutoSacer(),
                getForm().getCalcoloContenutoSacer().getStopCalcoloContenutoSacer(),
                getForm().getCalcoloContenutoSacer().getDt_prossima_attivazione(),
                getForm().getCalcoloContenutoSacer().getAttivo(),
                getForm().getCalcoloContenutoSacer().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(calcoloContenuto);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Calcolo consistenza">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name());
        StatoJob calcoloConsistenza = new StatoJob(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(),
                getForm().getCalcoloConsistenza().getFl_data_accurata(),
                getForm().getCalcoloConsistenza().getStartCalcoloConsistenza(),
                getForm().getCalcoloConsistenza().getStartOnceCalcoloConsistenza(),
                getForm().getCalcoloConsistenza().getStopCalcoloConsistenza(),
                getForm().getCalcoloConsistenza().getDt_prossima_attivazione(),
                getForm().getCalcoloConsistenza().getAttivo(),
                getForm().getCalcoloConsistenza().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(calcoloConsistenza);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Allineamento organizzazioni">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name());
        StatoJob allineamentoOrganizzazioni = new StatoJob(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(),
                getForm().getAllineamentoOrganizzazioni().getFl_data_accurata(),
                getForm().getAllineamentoOrganizzazioni().getStartAllineamentoOrganizzazioni(),
                getForm().getAllineamentoOrganizzazioni().getStartOnceAllineamentoOrganizzazioni(),
                getForm().getAllineamentoOrganizzazioni().getStopAllineamentoOrganizzazioni(),
                getForm().getAllineamentoOrganizzazioni().getDt_prossima_attivazione(),
                getForm().getAllineamentoOrganizzazioni().getAttivo(),
                getForm().getAllineamentoOrganizzazioni().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(allineamentoOrganizzazioni);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Aggiorna stato archiviazione">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name());
        StatoJob aggiornaStatoArk = new StatoJob(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(),
                getForm().getAggiornamentoStatoArk().getFl_data_accurata(),
                getForm().getAggiornamentoStatoArk().getStartAggiornamentoStatoArk(),
                getForm().getAggiornamentoStatoArk().getStartOnceAggiornamentoStatoArk(),
                getForm().getAggiornamentoStatoArk().getStopAggiornamentoStatoArk(),
                getForm().getAggiornamentoStatoArk().getDt_prossima_attivazione(),
                getForm().getAggiornamentoStatoArk().getAttivo(),
                getForm().getAggiornamentoStatoArk().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(aggiornaStatoArk);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Elabora sessioni recupero archiviazione">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name());
        StatoJob elaboraSessioniRecupero = new StatoJob(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(),
                getForm().getElaboraSessioniRecupero().getFl_data_accurata(),
                getForm().getElaboraSessioniRecupero().getStartElaboraSessioniRecupero(),
                getForm().getElaboraSessioniRecupero().getStartOnceElaboraSessioniRecupero(),
                getForm().getElaboraSessioniRecupero().getStopElaboraSessioniRecupero(),
                getForm().getElaboraSessioniRecupero().getDt_prossima_attivazione(),
                getForm().getElaboraSessioniRecupero().getAttivo(),
                getForm().getElaboraSessioniRecupero().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(elaboraSessioniRecupero);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Registra schedulazione TPI">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name());
        StatoJob registraSchedTpi = new StatoJob(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(),
                getForm().getRegistraSchedTpi().getFl_data_accurata(),
                getForm().getRegistraSchedTpi().getStartRegistraSchedTpi(),
                getForm().getRegistraSchedTpi().getStartOnceRegistraSchedTpi(),
                getForm().getRegistraSchedTpi().getStopRegistraSchedTpi(),
                getForm().getRegistraSchedTpi().getDt_prossima_attivazione(),
                getForm().getRegistraSchedTpi().getAttivo(), getForm().getRegistraSchedTpi().getDt_reg_log_job_ini(),
                dataAttivazioneJob);

        gestisciStatoJob(registraSchedTpi);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione indici AIP">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name());
        StatoJob creazioneIndiciAip = new StatoJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(),
                getForm().getCreazioneIndiciAip().getFl_data_accurata(),
                getForm().getCreazioneIndiciAip().getStartCreazioneIndiciAip(),
                getForm().getCreazioneIndiciAip().getStartOnceCreazioneIndiciAip(),
                getForm().getCreazioneIndiciAip().getStopCreazioneIndiciAip(),
                getForm().getCreazioneIndiciAip().getDt_prossima_attivazione(),
                getForm().getCreazioneIndiciAip().getAttivo(),
                getForm().getCreazioneIndiciAip().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneIndiciAip);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Verifica massiva versamenti falliti">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name());
        StatoJob verficaMassivaVersamentiFalliti = new StatoJob(
                JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(),
                getForm().getVerificaMassivaVersamentiFalliti().getFl_data_accurata(),
                getForm().getVerificaMassivaVersamentiFalliti().getStartVerificaMassivaVersamentiFalliti(),
                getForm().getVerificaMassivaVersamentiFalliti().getStartOnceVerificaMassivaVersamentiFalliti(),
                getForm().getVerificaMassivaVersamentiFalliti().getStopVerificaMassivaVersamentiFalliti(),
                getForm().getVerificaMassivaVersamentiFalliti().getDt_prossima_attivazione(),
                getForm().getVerificaMassivaVersamentiFalliti().getAttivo(),
                getForm().getVerificaMassivaVersamentiFalliti().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(verficaMassivaVersamentiFalliti);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Verifica periodo registro">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name());
        StatoJob verificaPeriodoRegistro = new StatoJob(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name(),
                getForm().getVerificaPeriodoRegistro().getFl_data_accurata(),
                getForm().getVerificaPeriodoRegistro().getStartVerificaPeriodoRegistro(),
                getForm().getVerificaPeriodoRegistro().getStartOnceVerificaPeriodoRegistro(),
                getForm().getVerificaPeriodoRegistro().getStopVerificaPeriodoRegistro(),
                getForm().getVerificaPeriodoRegistro().getDt_prossima_attivazione(),
                getForm().getVerificaPeriodoRegistro().getAttivo(),
                getForm().getVerificaPeriodoRegistro().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(verificaPeriodoRegistro);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione automatica serie">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name());
        StatoJob creazioneAutomaticaSerie = new StatoJob(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name(),
                getForm().getCreazioneAutomaticaSerie().getFl_data_accurata(),
                getForm().getCreazioneAutomaticaSerie().getStartCreazioneAutomaticaSerie(),
                getForm().getCreazioneAutomaticaSerie().getStartOnceCreazioneAutomaticaSerie(),
                getForm().getCreazioneAutomaticaSerie().getStopCreazioneAutomaticaSerie(),
                getForm().getCreazioneAutomaticaSerie().getDt_prossima_attivazione(),
                getForm().getCreazioneAutomaticaSerie().getAttivo(),
                getForm().getCreazioneAutomaticaSerie().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneAutomaticaSerie);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione indici AIP serie UD">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name());
        StatoJob creazioneIndiciAipSerieUd = new StatoJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(),
                getForm().getCreazioneIndiciAipSerieUd().getFl_data_accurata(),
                getForm().getCreazioneIndiciAipSerieUd().getStartCreazioneIndiciAipSerieUd(),
                getForm().getCreazioneIndiciAipSerieUd().getStartOnceCreazioneIndiciAipSerieUd(),
                getForm().getCreazioneIndiciAipSerieUd().getStopCreazioneIndiciAipSerieUd(),
                getForm().getCreazioneIndiciAipSerieUd().getDt_prossima_attivazione(),
                getForm().getCreazioneIndiciAipSerieUd().getAttivo(),
                getForm().getCreazioneIndiciAipSerieUd().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneIndiciAipSerieUd);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Generazione automatica contenuto effettivo
        // serie">
        dataAttivazioneJob = getActivationDateJob(
                JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name());
        StatoJob generazioneAutomaticaEffettivoSerie = new StatoJob(
                JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getFl_data_accurata(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getStartGenerazioneAutomaticaEffettivo(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getStartOnceGenerazioneAutomaticaEffettivo(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getStopGenerazioneAutomaticaEffettivo(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getDt_prossima_attivazione(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getAttivo(),
                getForm().getGenerazioneAutomaticaEffettivoSerie().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(generazioneAutomaticaEffettivoSerie);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Evasione richieste annullamento versamenti">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name());
        StatoJob evasioneRichiesteAnnullamentoVersamenti = new StatoJob(
                JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getFl_data_accurata(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getStartEvasioneRichiesteAnnulVers(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getStartOnceEvasioneRichiesteAnnulVers(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getStopEvasioneRichiesteAnnulVers(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getDt_prossima_attivazione(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getAttivo(),
                getForm().getEvasioneRichiesteAnnullamentoVersamenti().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(evasioneRichiesteAnnullamentoVersamenti);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per l'inizializzazione dei LOG">
        String nomeApplicazione = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        dataAttivazioneJob = getActivationDateJob(
                it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name() + "_" + nomeApplicazione);
        StatoJob inizializzazioneLog = new StatoJob(
                it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name(),
                getForm().getInizializzazioneLog().getFl_data_accurata(), null,
                getForm().getInizializzazioneLog().getStartOnceInizializzazioneLog(), null,
                getForm().getInizializzazioneLog().getDt_prossima_attivazione(),
                getForm().getInizializzazioneLog().getAttivo(),
                getForm().getInizializzazioneLog().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(inizializzazioneLog);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per l'allineamento dei LOG">
        dataAttivazioneJob = getActivationDateJob(
                it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name() + "_" + nomeApplicazione);
        StatoJob allineamentoLog = new StatoJob(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(),
                getForm().getAllineamentoLog().getFl_data_accurata(),
                getForm().getAllineamentoLog().getStartAllineamentoLog(),
                getForm().getAllineamentoLog().getStartOnceAllineamentoLog(),
                getForm().getAllineamentoLog().getStopAllineamentoLog(),
                getForm().getAllineamentoLog().getDt_prossima_attivazione(), getForm().getAllineamentoLog().getAttivo(),
                getForm().getAllineamentoLog().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(allineamentoLog);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Controllo automatico contenuto effettivo
        // serie">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name());
        StatoJob controlloAutomaticoEffettivoSerie = new StatoJob(
                JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name(),
                getForm().getControlloAutomaticoEffettivoSerie().getFl_data_accurata(),
                getForm().getControlloAutomaticoEffettivoSerie().getStartControlloAutomaticoEffettivo(),
                getForm().getControlloAutomaticoEffettivoSerie().getStartOnceControlloAutomaticoEffettivo(),
                getForm().getControlloAutomaticoEffettivoSerie().getStopControlloAutomaticoEffettivo(),
                getForm().getControlloAutomaticoEffettivoSerie().getDt_prossima_attivazione(),
                getForm().getControlloAutomaticoEffettivoSerie().getAttivo(),
                getForm().getControlloAutomaticoEffettivoSerie().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(controlloAutomaticoEffettivoSerie);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per calcolo struttura">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name());
        StatoJob calcoloStruttura = new StatoJob(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name(),
                getForm().getCalcoloStruttura().getFl_data_accurata(),
                getForm().getCalcoloStruttura().getStartCalcoloStruttura(),
                getForm().getCalcoloStruttura().getStartOnceCalcoloStruttura(),
                getForm().getCalcoloStruttura().getStopCalcoloStruttura(),
                getForm().getCalcoloStruttura().getDt_prossima_attivazione(),
                getForm().getCalcoloStruttura().getAttivo(), getForm().getCalcoloStruttura().getDt_reg_log_job_ini(),
                dataAttivazioneJob);

        gestisciStatoJob(calcoloStruttura);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per evasione richieste di restituzione archivio">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name());
        StatoJob evasioneRichiesteRestArch = new StatoJob(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getFl_data_accurata(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getStartEvasioneRichiesteRestArch(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getStartOnceEvasioneRichiesteRestArch(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getStopEvasioneRichiesteRestArch(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getDt_prossima_attivazione(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getAttivo(),
                getForm().getEvasioneRichiesteRestituzioneArchivio().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(evasioneRichiesteRestArch);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione Elenchi indici AIP">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name());
        StatoJob creazioneElenchiIndiciAipUd = new StatoJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(),
                getForm().getCreazioneElenchiIndiciAip().getFl_data_accurata(),
                getForm().getCreazioneElenchiIndiciAip().getStartCreazioneElenchiIndiciAip(),
                getForm().getCreazioneElenchiIndiciAip().getStartOnceCreazioneElenchiIndiciAip(),
                getForm().getCreazioneElenchiIndiciAip().getStopCreazioneElenchiIndiciAip(),
                getForm().getCreazioneElenchiIndiciAip().getDt_prossima_attivazione(),
                getForm().getCreazioneElenchiIndiciAip().getAttivo(),
                getForm().getCreazioneElenchiIndiciAip().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneElenchiIndiciAipUd);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Allineamento enti convenzionati">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name());
        StatoJob allineaEntiConvenzionati = new StatoJob(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(),
                getForm().getAllineaEntiConvenzionati().getFl_data_accurata(),
                getForm().getAllineaEntiConvenzionati().getStartAllineaEntiConvenzionati(),
                getForm().getAllineaEntiConvenzionati().getStartOnceAllineaEntiConvenzionati(),
                getForm().getAllineaEntiConvenzionati().getStopAllineaEntiConvenzionati(),
                getForm().getAllineaEntiConvenzionati().getDt_prossima_attivazione(),
                getForm().getAllineaEntiConvenzionati().getAttivo(),
                getForm().getAllineaEntiConvenzionati().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(allineaEntiConvenzionati);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Calcolo contenuto fascicoli">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name());
        StatoJob calcoloContenutoFascicoli = new StatoJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(),
                getForm().getCalcoloContenutoFascicoli().getFl_data_accurata(),
                getForm().getCalcoloContenutoFascicoli().getStartCalcoloContenutoFascicoli(),
                getForm().getCalcoloContenutoFascicoli().getStartOnceCalcoloContenutoFascicoli(),
                getForm().getCalcoloContenutoFascicoli().getStopCalcoloContenutoFascicoli(),
                getForm().getCalcoloContenutoFascicoli().getDt_prossima_attivazione(),
                getForm().getCalcoloContenutoFascicoli().getAttivo(),
                getForm().getCalcoloContenutoFascicoli().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(calcoloContenutoFascicoli);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Verifica Periodo tipo fascicolo">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name());
        StatoJob verificaPeriodoTipoFasc = new StatoJob(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name(),
                getForm().getVerificaPeriodoTipoFascicolo().getFl_data_accurata(),
                getForm().getVerificaPeriodoTipoFascicolo().getStartVerificaPeriodoTipoFasc(),
                getForm().getVerificaPeriodoTipoFascicolo().getStartOnceVerificaPeriodoTipoFasc(),
                getForm().getVerificaPeriodoTipoFascicolo().getStopVerificaPeriodoTipoFasc(),
                getForm().getVerificaPeriodoTipoFascicolo().getDt_prossima_attivazione(),
                getForm().getVerificaPeriodoTipoFascicolo().getAttivo(),
                getForm().getVerificaPeriodoTipoFascicolo().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(verificaPeriodoTipoFasc);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione elenchi versamento fascicoli">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name());
        StatoJob creazioneElenchiVersFascicoli = new StatoJob(
                JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name(),
                getForm().getCreazioneElenchiVersFascicoli().getFl_data_accurata(),
                getForm().getCreazioneElenchiVersFascicoli().getStartCreazioneElenchiVersFascicoli(),
                getForm().getCreazioneElenchiVersFascicoli().getStartOnceCreazioneElenchiVersFascicoli(),
                getForm().getCreazioneElenchiVersFascicoli().getStopCreazioneElenchiVersFascicoli(),
                getForm().getCreazioneElenchiVersFascicoli().getDt_prossima_attivazione(),
                getForm().getCreazioneElenchiVersFascicoli().getAttivo(),
                getForm().getCreazioneElenchiVersFascicoli().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneElenchiVersFascicoli);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione indici versamento fascicoli">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name());
        StatoJob creazioneIndiciVersFascicoli = new StatoJob(
                JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(),
                getForm().getCreazioneIndiciVersFascicoli().getFl_data_accurata(),
                getForm().getCreazioneIndiciVersFascicoli().getStartCreazioneIndiciVersFascicoli(),
                getForm().getCreazioneIndiciVersFascicoli().getStartOnceCreazioneIndiciVersFascicoli(),
                getForm().getCreazioneIndiciVersFascicoli().getStopCreazioneIndiciVersFascicoli(),
                getForm().getCreazioneIndiciVersFascicoli().getDt_prossima_attivazione(),
                getForm().getCreazioneIndiciVersFascicoli().getAttivo(),
                getForm().getCreazioneIndiciVersFascicoli().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneIndiciVersFascicoli);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Validazione fascicoli">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name());
        StatoJob validazioneFascicoli = new StatoJob(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name(),
                getForm().getValidazioneFascicoli().getFl_data_accurata(),
                getForm().getValidazioneFascicoli().getStartValidazioneFascicoli(),
                getForm().getValidazioneFascicoli().getStartOnceValidazioneFascicoli(),
                getForm().getValidazioneFascicoli().getStopValidazioneFascicoli(),
                getForm().getValidazioneFascicoli().getDt_prossima_attivazione(),
                getForm().getValidazioneFascicoli().getAttivo(),
                getForm().getValidazioneFascicoli().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(validazioneFascicoli);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione indici AIP fascicoli">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name());
        StatoJob creazioneIndiciAipFasc = new StatoJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(),
                getForm().getCreazioneIndiciAipFascicoli().getFl_data_accurata(),
                getForm().getCreazioneIndiciAipFascicoli().getStartCreazioneIndiciAipFasc(),
                getForm().getCreazioneIndiciAipFascicoli().getStartOnceCreazioneIndiciAipFasc(),
                getForm().getCreazioneIndiciAipFascicoli().getStopCreazioneIndiciAipFasc(),
                getForm().getCreazioneIndiciAipFascicoli().getDt_prossima_attivazione(),
                getForm().getCreazioneIndiciAipFascicoli().getAttivo(),
                getForm().getCreazioneIndiciAipFascicoli().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneIndiciAipFasc);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Creazione Elenchi indici AIP fascicoli">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name());
        StatoJob creazioneElenchiIndiciAipFasc = new StatoJob(
                JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getFl_data_accurata(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getStartCreazioneElenchiIndiciAipFasc(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getStartOnceCreazioneElenchiIndiciAipFasc(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getStopCreazioneElenchiIndiciAipFasc(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getDt_prossima_attivazione(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getAttivo(),
                getForm().getCreazioneElenchiIndiciAipFascicoli().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(creazioneElenchiIndiciAipFasc);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Preparazione partizione da migrare 1">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name());
        StatoJob preparazionePartizioneDaMigrare1 = new StatoJob(
                JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name(),
                getForm().getPreparazionePartizioneDaMigrare1().getFl_data_accurata(),
                getForm().getPreparazionePartizioneDaMigrare1().getStartPreparazionePartizioneDaMigrare1(),
                getForm().getPreparazionePartizioneDaMigrare1().getStartOncePreparazionePartizioneDaMigrare1(),
                getForm().getPreparazionePartizioneDaMigrare1().getStopPreparazionePartizioneDaMigrare1(),
                getForm().getPreparazionePartizioneDaMigrare1().getDt_prossima_attivazione(),
                getForm().getPreparazionePartizioneDaMigrare1().getAttivo(),
                getForm().getPreparazionePartizioneDaMigrare1().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(preparazionePartizioneDaMigrare1);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Preparazione partizione da migrare 2">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name());
        StatoJob preparazionePartizioneDaMigrare2 = new StatoJob(
                JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name(),
                getForm().getPreparazionePartizioneDaMigrare2().getFl_data_accurata(),
                getForm().getPreparazionePartizioneDaMigrare2().getStartPreparazionePartizioneDaMigrare2(),
                getForm().getPreparazionePartizioneDaMigrare2().getStartOncePreparazionePartizioneDaMigrare2(),
                getForm().getPreparazionePartizioneDaMigrare2().getStopPreparazionePartizioneDaMigrare2(),
                getForm().getPreparazionePartizioneDaMigrare2().getDt_prossima_attivazione(),
                getForm().getPreparazionePartizioneDaMigrare2().getAttivo(),
                getForm().getPreparazionePartizioneDaMigrare2().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(preparazionePartizioneDaMigrare2);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Preparazione partizione da migrare 3">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_3.name());
        StatoJob preparazionePartizioneDaMigrare3 = new StatoJob(
                JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_3.name(),
                getForm().getPreparazionePartizioneDaMigrare3().getFl_data_accurata(),
                getForm().getPreparazionePartizioneDaMigrare3().getStartPreparazionePartizioneDaMigrare3(),
                getForm().getPreparazionePartizioneDaMigrare3().getStartOncePreparazionePartizioneDaMigrare3(),
                getForm().getPreparazionePartizioneDaMigrare3().getStopPreparazionePartizioneDaMigrare3(),
                getForm().getPreparazionePartizioneDaMigrare3().getDt_prossima_attivazione(),
                getForm().getPreparazionePartizioneDaMigrare3().getAttivo(),
                getForm().getPreparazionePartizioneDaMigrare3().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(preparazionePartizioneDaMigrare3);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Preparazione partizione da migrare 4">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_4.name());
        StatoJob preparazionePartizioneDaMigrare4 = new StatoJob(
                JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_4.name(),
                getForm().getPreparazionePartizioneDaMigrare4().getFl_data_accurata(),
                getForm().getPreparazionePartizioneDaMigrare4().getStartPreparazionePartizioneDaMigrare4(),
                getForm().getPreparazionePartizioneDaMigrare4().getStartOncePreparazionePartizioneDaMigrare4(),
                getForm().getPreparazionePartizioneDaMigrare4().getStopPreparazionePartizioneDaMigrare4(),
                getForm().getPreparazionePartizioneDaMigrare4().getDt_prossima_attivazione(),
                getForm().getPreparazionePartizioneDaMigrare4().getAttivo(),
                getForm().getPreparazionePartizioneDaMigrare4().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(preparazionePartizioneDaMigrare4);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per produzione coda da migrare 1">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_1.name());
        StatoJob producerCodaDaMigrare1 = new StatoJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_1.name(),
                getForm().getProducerCodaDaMigrare1().getFl_data_accurata(),
                getForm().getProducerCodaDaMigrare1().getStartProducerCodaDaMigrare1(),
                getForm().getProducerCodaDaMigrare1().getStartOnceProducerCodaDaMigrare1(),
                getForm().getProducerCodaDaMigrare1().getStopProducerCodaDaMigrare1(),
                getForm().getProducerCodaDaMigrare1().getDt_prossima_attivazione(),
                getForm().getProducerCodaDaMigrare1().getAttivo(),
                getForm().getProducerCodaDaMigrare1().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(producerCodaDaMigrare1);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per produzione coda da migrare 2">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name());
        StatoJob producerCodaDaMigrare2 = new StatoJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name(),
                getForm().getProducerCodaDaMigrare2().getFl_data_accurata(),
                getForm().getProducerCodaDaMigrare2().getStartProducerCodaDaMigrare2(),
                getForm().getProducerCodaDaMigrare2().getStartOnceProducerCodaDaMigrare2(),
                getForm().getProducerCodaDaMigrare2().getStopProducerCodaDaMigrare2(),
                getForm().getProducerCodaDaMigrare2().getDt_prossima_attivazione(),
                getForm().getProducerCodaDaMigrare2().getAttivo(),
                getForm().getProducerCodaDaMigrare2().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(producerCodaDaMigrare2);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per produzione coda da migrare 3">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_3.name());
        StatoJob producerCodaDaMigrare3 = new StatoJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_3.name(),
                getForm().getProducerCodaDaMigrare3().getFl_data_accurata(),
                getForm().getProducerCodaDaMigrare3().getStartProducerCodaDaMigrare3(),
                getForm().getProducerCodaDaMigrare3().getStartOnceProducerCodaDaMigrare3(),
                getForm().getProducerCodaDaMigrare3().getStopProducerCodaDaMigrare3(),
                getForm().getProducerCodaDaMigrare3().getDt_prossima_attivazione(),
                getForm().getProducerCodaDaMigrare3().getAttivo(),
                getForm().getProducerCodaDaMigrare3().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(producerCodaDaMigrare3);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per produzione coda da migrare 4">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_4.name());
        StatoJob producerCodaDaMigrare4 = new StatoJob(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_4.name(),
                getForm().getProducerCodaDaMigrare4().getFl_data_accurata(),
                getForm().getProducerCodaDaMigrare4().getStartProducerCodaDaMigrare4(),
                getForm().getProducerCodaDaMigrare4().getStartOnceProducerCodaDaMigrare4(),
                getForm().getProducerCodaDaMigrare4().getStopProducerCodaDaMigrare4(),
                getForm().getProducerCodaDaMigrare4().getDt_prossima_attivazione(),
                getForm().getProducerCodaDaMigrare4().getAttivo(),
                getForm().getProducerCodaDaMigrare4().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(producerCodaDaMigrare4);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per verifica migrazione subpartizione">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name());
        StatoJob verificaMigrazioneSubpartizione = new StatoJob(
                JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(),
                getForm().getVerificaMigrazioneSubpartizione().getFl_data_accurata(),
                getForm().getVerificaMigrazioneSubpartizione().getStartVerificaMigrazioneSubpartizione(),
                getForm().getVerificaMigrazioneSubpartizione().getStartOnceVerificaMigrazioneSubpartizione(),
                getForm().getVerificaMigrazioneSubpartizione().getStopVerificaMigrazioneSubpartizione(),
                getForm().getVerificaMigrazioneSubpartizione().getDt_prossima_attivazione(),
                getForm().getVerificaMigrazioneSubpartizione().getAttivo(),
                getForm().getVerificaMigrazioneSubpartizione().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(verificaMigrazioneSubpartizione);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Calcolo contenuto aggiornamenti metadati">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name());
        StatoJob calcoloContenutoAggMeta = new StatoJob(
                JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getFl_data_accurata(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getStartCalcoloContenutoAgg(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getStartOnceCalcoloContenutoAgg(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getStopCalcoloContenutoAgg(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getDt_prossima_attivazione(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getAttivo(),
                getForm().getCalcoloContenutoAggiornamentiMetadati().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJob(calcoloContenutoAggMeta);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per controlla migrazione subpartizione">
        dataAttivazioneJob = getActivationDateJob(JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name());
        StatoJob controlloMigrazioneSubpartizione = new StatoJob(
                JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name(),
                getForm().getControllaMigrazioneSubpartizione().getFl_data_accurata(),
                getForm().getControllaMigrazioneSubpartizione().getStartControllaMigrazioneSubpartizione(),
                getForm().getControllaMigrazioneSubpartizione().getStartOnceControllaMigrazioneSubpartizione(),
                getForm().getControllaMigrazioneSubpartizione().getStopControllaMigrazioneSubpartizione(),
                getForm().getControllaMigrazioneSubpartizione().getDt_prossima_attivazione(),
                getForm().getControllaMigrazioneSubpartizione().getAttivo(),
                getForm().getControllaMigrazioneSubpartizione().getDt_reg_log_job_ini(), dataAttivazioneJob);
        gestisciStatoJob(controlloMigrazioneSubpartizione);
        // </editor-fold>

        forwardToPublisher(Application.Publisher.GESTIONE_JOB);
    }

    /**
     * Cuore della classe: qui è definita la logica STANDARD degli stati dei job a livello di <b>interfaccia web<b>. Per
     * i job che devono implementare una logica non standard non è consigliabile utilizzare questo metodo. Si è cercato
     * di mantenere una simmetria tra esposizione/inibizione dei controlli grafici.
     *
     * @param statoJob
     *            Rappresentazione dello stato <b>a livello di interfaccia grafica</b> del job.
     *
     * @throws EMFError
     *             in caso di errore generale
     */
    private void gestisciStatoJob(StatoJob statoJob) throws EMFError {
        // se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare nulla
        boolean operazioneInCorso = jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

        statoJob.getFlagDataAccurata().setViewMode();
        statoJob.getFlagDataAccurata().setValue("L'operazione richiesta verrà effettuata entro il prossimo minuto.");
        statoJob.getStart().setHidden(operazioneInCorso);
        statoJob.getEsecuzioneSingola().setHidden(operazioneInCorso);
        statoJob.getStop().setHidden(operazioneInCorso);
        statoJob.getDataProssimaAttivazione().setHidden(operazioneInCorso);

        statoJob.getFlagDataAccurata().setHidden(!operazioneInCorso);
        if (operazioneInCorso) {
            return;
        }

        // Posso operare sulla pagina
        Date nextActivation = jbossTimerEjb.getDataProssimaAttivazione(statoJob.getNomeJob());
        boolean dataAccurata = jbossTimerEjb.isDataProssimaAttivazioneAccurata(statoJob.getNomeJob());
        DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_TIMESTAMP_TYPE);

        /*
         * Se il job è già schedulato o in esecuzione singola nascondo il pulsante Start/esecuzione singola, mostro Stop
         * e visualizzo la prossima attivazione. Viceversa se è fermo mostro Start e nascondo Stop
         */
        if (nextActivation != null) {
            statoJob.getStart().setViewMode();
            statoJob.getEsecuzioneSingola().setViewMode();
            statoJob.getStop().setEditMode();
            statoJob.getDataProssimaAttivazione().setValue(formato.format(nextActivation));
        } else {
            statoJob.getStart().setEditMode();
            statoJob.getEsecuzioneSingola().setEditMode();
            statoJob.getStop().setViewMode();
            statoJob.getDataProssimaAttivazione().setValue(null);
        }

        boolean flagHidden = nextActivation == null || dataAccurata;
        // se la data c'è ma non è accurata non visualizzare la "data prossima attivazione"
        statoJob.getDataProssimaAttivazione().setHidden(!flagHidden);

        if (statoJob.getDataAttivazione() != null) {
            statoJob.getCheckAttivo().setChecked(true);
            statoJob.getDataRegistrazioneJob()
                    .setValue(formato.format(new Date(statoJob.getDataAttivazione().getTime())));
        } else {
            statoJob.getCheckAttivo().setChecked(false);
            statoJob.getDataRegistrazioneJob().setValue(null);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="UI Classe che mappa lo stato dei job">
    /**
     * Astrazione dei componenti della pagina utilizzati per i "box" dei job.
     *
     * @author Snidero_L
     */
    private static final class StatoJob {

        private final String nomeJob;
        private final Input<String> flagDataAccurata;
        private final Button<String> start;
        private final Button<String> esecuzioneSingola;
        private final Button<String> stop;
        private final Input<Timestamp> dataProssimaAttivazione;
        private final CheckBox<String> checkAttivo;
        private final Input<Timestamp> dataRegistrazioneJob;
        private final Timestamp dataAttivazione;

        // Mi serve per evitare una null pointer Exception
        private static final Button<String> NULL_BUTTON = new Button<String>(null, "EMPTY_BUTTON", "Pulsante vuoto",
                null, null, null, false, true, true, false);

        public StatoJob(String nomeJob, Input<String> flagDataAccurata, Button<String> start,
                Button<String> esecuzioneSingola, Button<String> stop, Input<Timestamp> dataProssimaAttivazione,
                CheckBox<String> checkAttivo, Input<Timestamp> dataRegistrazioneJob, Timestamp dataAttivazione) {
            this.nomeJob = nomeJob;
            this.flagDataAccurata = flagDataAccurata;
            this.start = start;
            this.esecuzioneSingola = esecuzioneSingola;
            this.stop = stop;
            this.dataProssimaAttivazione = dataProssimaAttivazione;
            this.checkAttivo = checkAttivo;
            this.dataRegistrazioneJob = dataRegistrazioneJob;
            this.dataAttivazione = dataAttivazione;
        }

        public String getNomeJob() {
            return nomeJob;
        }

        public Input<String> getFlagDataAccurata() {
            return flagDataAccurata;
        }

        public Button<String> getStart() {
            if (start == null) {
                return NULL_BUTTON;
            }
            return start;
        }

        public Button<String> getEsecuzioneSingola() {
            return esecuzioneSingola;
        }

        public Button<String> getStop() {
            if (stop == null) {
                return NULL_BUTTON;
            }
            return stop;
        }

        public Input<Timestamp> getDataProssimaAttivazione() {
            return dataProssimaAttivazione;
        }

        public CheckBox<String> getCheckAttivo() {
            return checkAttivo;
        }

        public Input<Timestamp> getDataRegistrazioneJob() {
            return dataRegistrazioneJob;
        }

        public Timestamp getDataAttivazione() {
            return dataAttivazione;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funzioni non implementate">
    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elencoOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void process() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        getSession().removeAttribute("backToRicercaJob");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneElenchi schedulation">
    @Override
    public void startCreazioneElenchi() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), "Creazione elenchi", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneElenchi() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), "Creazione elenchi", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneElenchi() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), "Creazione elenchi", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneIndici schedulation">
    @Override
    public void startCreazioneIndici() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(), "Creazione indici", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneIndici() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(), "Creazione indici", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneIndici() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(), "Creazione indici", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage VerificaFirme schedulation">
    @Override
    public void startVerificaFirme() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_FIRME_A_DATA_VERS.name(), "Verifica firme", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceVerificaFirme() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_FIRME_A_DATA_VERS.name(), "Verifica firme", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopVerificaFirme() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_FIRME_A_DATA_VERS.name(), "Verifica firme", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CodaIndiciAipDaElab schedulation">
    @Override
    public void startCodaIndiciAipDaElab() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(), "Coda indici aip da elaborare", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceCodaIndiciAipDaElab() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(), "Coda indici aip da elaborare", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCodaIndiciAipDaElab() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(), "Coda indici aip da elaborare", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CalcolaContenutoSacer schedulation">
    @Override
    public void startCalcoloContenutoSacer() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name(), "Calcolo Contenuto Sacer", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCalcoloContenutoSacer() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name(), "Calcolo Contenuto Sacer", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCalcoloContenutoSacer() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name(), "Calcolo Contenuto Sacer", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage AllineamentoOrganizzazioni schedulation">
    @Override
    public void startAllineamentoOrganizzazioni() throws EMFError {
        esegui(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(), "Allineamento organizzazioni", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceAllineamentoOrganizzazioni() throws EMFError {
        esegui(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(), "Allineamento organizzazioni", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopAllineamentoOrganizzazioni() throws EMFError {
        esegui(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(), "Allineamento organizzazioni", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage AggiornaStatoArk schedulation">
    @Override
    public void startAggiornamentoStatoArk() throws EMFError {
        esegui(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(), "Aggiorna stato archiviazione", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceAggiornamentoStatoArk() throws EMFError {
        esegui(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(), "Aggiorna stato archiviazione", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopAggiornamentoStatoArk() throws EMFError {
        esegui(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(), "Aggiorna stato archiviazione", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ElaboraSessioniRecupero schedulation">
    @Override
    public void startElaboraSessioniRecupero() throws EMFError {
        esegui(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(), "Elabora sessioni recupero archiviazione", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceElaboraSessioniRecupero() throws EMFError {
        esegui(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(), "Elabora sessioni recupero archiviazione", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopElaboraSessioniRecupero() throws EMFError {
        esegui(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(), "Elabora sessioni recupero archiviazione", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage RegistraSchedTpi schedulation">
    @Override
    public void startRegistraSchedTpi() throws EMFError {
        esegui(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(), "Registra schedulazioni job TPI", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceRegistraSchedTpi() throws EMFError {
        esegui(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(), "Registra schedulazioni job TPI", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopRegistraSchedTpi() throws EMFError {
        esegui(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(), "Registra schedulazioni job TPI", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneIndiciAIP schedulation">
    @Override
    public void startCreazioneIndiciAip() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(), "Creazione indici AIP", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneIndiciAip() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(), "Creazione indici AIP", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneIndiciAip() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(), "Creazione indici AIP", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ProducerCodaDaMigrare1 schedulation">
    @Override
    public void startProducerCodaDaMigrare1() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_1.name(), "Producer coda da migrare 1", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceProducerCodaDaMigrare1() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_1.name(), "Producer coda da migrare 1", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopProducerCodaDaMigrare1() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_1.name(), "Producer coda da migrare 1", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ProducerCodaDaMigrare2 schedulation">
    @Override
    public void startProducerCodaDaMigrare2() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name(), "Producer coda da migrare 2", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceProducerCodaDaMigrare2() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name(), "Producer coda da migrare 2", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopProducerCodaDaMigrare2() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name(), "Producer coda da migrare 2", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ProducerCodaDaMigrare3 schedulation">
    @Override
    public void startProducerCodaDaMigrare3() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_3.name(), "Producer coda da migrare 3", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceProducerCodaDaMigrare3() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_3.name(), "Producer coda da migrare 3", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopProducerCodaDaMigrare3() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_3.name(), "Producer coda da migrare 3", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ProducerCodaDaMigrare4 schedulation">
    @Override
    public void startProducerCodaDaMigrare4() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_4.name(), "Producer coda da migrare 4", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceProducerCodaDaMigrare4() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_4.name(), "Producer coda da migrare 4", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopProducerCodaDaMigrare4() throws EMFError {
        esegui(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_4.name(), "Producer coda da migrare 4", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage PreparazionePartizioneDaMigrare1 schedulation">
    @Override
    public void startPreparazionePartizioneDaMigrare1() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name(), "Preparazione Partizione da migrare 1",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOncePreparazionePartizioneDaMigrare1() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name(), "Preparazione Partizione da migrare 1",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopPreparazionePartizioneDaMigrare1() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name(), "Preparazione Partizione da migrare 1",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage PreparazionePartizioneDaMigrare2 schedulation">
    @Override
    public void startPreparazionePartizioneDaMigrare2() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name(), "Preparazione Partizione da migrare 2",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOncePreparazionePartizioneDaMigrare2() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name(), "Preparazione Partizione da migrare 2",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopPreparazionePartizioneDaMigrare2() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name(), "Preparazione Partizione da migrare 2",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage PreparazionePartizioneDaMigrare3 schedulation">
    @Override
    public void startPreparazionePartizioneDaMigrare3() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_3.name(), "Preparazione Partizione da migrare 3",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOncePreparazionePartizioneDaMigrare3() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_3.name(), "Preparazione Partizione da migrare 3",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopPreparazionePartizioneDaMigrare3() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_3.name(), "Preparazione Partizione da migrare 3",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage PreparazionePartizioneDaMigrare4 schedulation">
    @Override
    public void startPreparazionePartizioneDaMigrare4() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_4.name(), "Preparazione Partizione da migrare 4",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOncePreparazionePartizioneDaMigrare4() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_4.name(), "Preparazione Partizione da migrare 4",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopPreparazionePartizioneDaMigrare4() throws EMFError {
        esegui(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_4.name(), "Preparazione Partizione da migrare 4",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage VerificaMigrazioneSubpartizione schedulation">
    @Override
    public void startVerificaMigrazioneSubpartizione() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(), "Verifica migrazione subpartizione", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceVerificaMigrazioneSubpartizione() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(), "Verifica migrazione subpartizione", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopVerificaMigrazioneSubpartizione() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(), "Verifica migrazione subpartizione", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ControllaMigrazioneSubpartizione schedulation">
    @Override
    public void startControllaMigrazioneSubpartizione() throws EMFError {
        esegui(JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name(), "Controlla migrazione subpartizione",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOnceControllaMigrazioneSubpartizione() throws EMFError {
        esegui(JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name(), "Controlla migrazione subpartizione",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopControllaMigrazioneSubpartizione() throws EMFError {
        esegui(JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name(), "Controlla migrazione subpartizione",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage VerificaMassivaVersamentiFalliti schedulation">
    @Override
    public void startVerificaMassivaVersamentiFalliti() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(), "Verifica massiva versamenti falliti", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceVerificaMassivaVersamentiFalliti() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(), "Verifica massiva versamenti falliti", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopVerificaMassivaVersamentiFalliti() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(), "Verifica massiva versamenti falliti", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage VerificaPeriodoRegistro schedulation">
    @Override
    public void startVerificaPeriodoRegistro() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name(), "Verifica periodo registro", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceVerificaPeriodoRegistro() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name(), "Verifica periodo registro", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopVerificaPeriodoRegistro() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name(), "Verifica periodo registro", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneAutomaticaSerie schedulation">
    @Override
    public void startCreazioneAutomaticaSerie() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name(), "Creazione automatica serie", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneAutomaticaSerie() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name(), "Creazione automatica serie", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneAutomaticaSerie() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name(), "Creazione automatica serie", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneIndiciAIPSerieUD schedulation">
    @Override
    public void startCreazioneIndiciAipSerieUd() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(), "Creazione indice AIP serie ud", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneIndiciAipSerieUd() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(), "Creazione indice AIP serie ud", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneIndiciAipSerieUd() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(), "Creazione indice AIP serie ud", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage GenerazioneAutomaticaEffettivo schedulation">
    @Override
    public void startGenerazioneAutomaticaEffettivo() throws EMFError {
        esegui(JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name(),
                "Generazione automatica contenuto effettivo serie", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceGenerazioneAutomaticaEffettivo() throws EMFError {
        esegui(JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name(),
                "Generazione automatica contenuto effettivo serie", null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopGenerazioneAutomaticaEffettivo() throws EMFError {
        esegui(JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name(),
                "Generazione automatica contenuto effettivo serie", null, OPERAZIONE.STOP);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Methods to manage ControlloAutomaticoEffettivo schedulation">
    @Override
    public void startControlloAutomaticoEffettivo() throws EMFError {
        esegui(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name(),
                "Controllo automatico contenuto effettivo serie", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceControlloAutomaticoEffettivo() throws EMFError {
        esegui(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name(),
                "Controllo automatico contenuto effettivo serie", null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopControlloAutomaticoEffettivo() throws EMFError {
        esegui(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name(),
                "Controllo automatico contenuto effettivo serie", null, OPERAZIONE.STOP);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Methods to manage EvasioneRichiesteAnnullamentoVersamenti
    // schedulation">
    @Override
    public void startEvasioneRichiesteAnnulVers() throws EMFError {
        esegui(JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name(), "Evasione richieste di annullamento versamenti",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOnceEvasioneRichiesteAnnulVers() throws EMFError {
        esegui(JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name(), "Evasione richieste di annullamento versamenti",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopEvasioneRichiesteAnnulVers() throws EMFError {
        esegui(JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name(), "Evasione richieste di annullamento versamenti",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CalcoloStruttura schedulation">
    @Override
    public void startCalcoloStruttura() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name(), "Calcolo struttura", null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCalcoloStruttura() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name(), "Calcolo struttura", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCalcoloStruttura() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name(), "Calcolo struttura", null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CalcoloEstrazione schedulation">
    @Override
    public void startEvasioneRichiesteRestArch() throws EMFError {
        esegui(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name(), "Evasione richieste di restituzione archivio", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceEvasioneRichiesteRestArch() throws EMFError {
        esegui(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name(), "Evasione richieste di restituzione archivio", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopEvasioneRichiesteRestArch() throws EMFError {
        esegui(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name(), "Evasione richieste di restituzione archivio", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage AllineamentoLog schedulation">
    @Override
    public void startOnceInizializzazioneLog() throws EMFError {
        String nomeApplicazione = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name(), "Inizializzazione Log",
                nomeApplicazione, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void startAllineamentoLog() throws EMFError {
        String nomeApplicazione = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(), "Allineamento Log",
                nomeApplicazione, OPERAZIONE.START);
    }

    @Override
    public void startOnceAllineamentoLog() throws EMFError {
        String nomeApplicazione = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(), "Allineamento Log",
                nomeApplicazione, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopAllineamentoLog() throws EMFError {
        String nomeApplicazione = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        esegui(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(), "Allineamento Log",
                nomeApplicazione, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneElenchiIndiciAIPUd schedulation">
    @Override
    public void startCreazioneElenchiIndiciAip() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(), "Creazione elenchi indici AIP", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneElenchiIndiciAip() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(), "Creazione elenchi indici AIP", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneElenchiIndiciAip() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(), "Creazione elenchi indici AIP", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CalcolaContenutoFascicoli schedulation">
    @Override
    public void startCalcoloContenutoFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(), "Calcolo contenuto fascicoli", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceCalcoloContenutoFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(), "Calcolo contenuto fascicoli", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCalcoloContenutoFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(), "Calcolo contenuto fascicoli", null,
                OPERAZIONE.STOP);
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Methods to manage AllineaEntiConvenzionati schedulation">
    @Override
    public void startAllineaEntiConvenzionati() throws EMFError {
        esegui(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(), "Allinea enti convenzionati", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceAllineaEntiConvenzionati() throws EMFError {
        esegui(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(), "Allinea enti convenzionati", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopAllineaEntiConvenzionati() throws EMFError {
        esegui(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(), "Allinea enti convenzionati", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage VerificaPeriodoTipoFascicolo schedulation">
    @Override
    public void startVerificaPeriodoTipoFasc() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name(), "Verifica periodo tipo fascicolo", null,
                OPERAZIONE.START);
    }

    @Override
    public void startOnceVerificaPeriodoTipoFasc() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name(), "Verifica periodo tipo fascicolo", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopVerificaPeriodoTipoFasc() throws EMFError {
        esegui(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name(), "Verifica periodo tipo fascicolo", null,
                OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneElencoVersFascicoli schedulation">
    @Override
    public void startCreazioneElenchiVersFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name(), "Creazione elenchi versamento fascicoli",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneElenchiVersFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name(), "Creazione elenchi versamento fascicoli",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneElenchiVersFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name(), "Creazione elenchi versamento fascicoli",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneIndiciVersFascicoli schedulation">
    @Override
    public void startCreazioneIndiciVersFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(), "Creazione indici versamento fascicoli",
                null, OPERAZIONE.START);
    }

    @Override
    public void startOnceCreazioneIndiciVersFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(), "Creazione indici versamento fascicoli",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void stopCreazioneIndiciVersFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(), "Creazione indici versamento fascicoli",
                null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage ValidazioneFascicoli schedulation">
    @Override
    public void startValidazioneFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name(), "Validazione fascicoli", null, OPERAZIONE.START);
    }

    @Override
    public void stopValidazioneFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name(), "Validazione fascicoli", null, OPERAZIONE.STOP);
    }

    @Override
    public void startOnceValidazioneFascicoli() throws EMFError {
        esegui(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name(), "Validazione fascicoli", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneIndiciAIPFascicoli schedulation">
    @Override
    public void startCreazioneIndiciAipFasc() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(), "Creazione indici AIP fascicoli", null,
                OPERAZIONE.START);
    }

    @Override
    public void stopCreazioneIndiciAipFasc() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(), "Creazione indici AIP fascicoli", null,
                OPERAZIONE.STOP);
    }

    @Override
    public void startOnceCreazioneIndiciAipFasc() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(), "Creazione indici AIP fascicoli", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CalcoloContenutoAggiornamentiMetadati
    // schedulation">
    @Override
    public void startCalcoloContenutoAgg() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(),
                "Calcolo contenuto aggiornamenti metadati", null, OPERAZIONE.START);
    }

    @Override
    public void stopCalcoloContenutoAgg() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(),
                "Calcolo contenuto aggiornamenti metadati", null, OPERAZIONE.STOP);
    }

    @Override
    public void startOnceCalcoloContenutoAgg() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(),
                "Calcolo contenuto aggiornamenti metadati", null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CalcoloConsistenza schedulation">
    @Override
    public void startCalcoloConsistenza() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(), "Calcolo consistenza", null, OPERAZIONE.START);
    }

    @Override
    public void stopCalcoloConsistenza() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(), "Calcolo consistenza", null, OPERAZIONE.STOP);
    }

    @Override
    public void startOnceCalcoloConsistenza() throws EMFError {
        esegui(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(), "Calcolo consistenza", null,
                OPERAZIONE.ESECUZIONE_SINGOLA);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage CreazioneElenchiIndiciAIPFascicoli schedulation">
    @Override
    public void startCreazioneElenchiIndiciAipFasc() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(), "Creazione elenchi indici AIP fascicoli",
                null, OPERAZIONE.START);
    }

    @Override
    public void stopCreazioneElenchiIndiciAipFasc() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(), "Creazione elenchi indici AIP fascicoli",
                null, OPERAZIONE.STOP);
    }

    @Override
    public void startOnceCreazioneElenchiIndiciAipFasc() throws EMFError {
        esegui(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(), "Creazione elenchi indici AIP fascicoli",
                null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Esecuzione di un job STANDARD">
    /**
     * Esegui una delle seguenti operazioni:
     * <ul>
     * <li>{@link OPERAZIONE#START}</li>
     * <li>{@link OPERAZIONE#ESECUZIONE_SINGOLA}</li>
     * <li>{@link OPERAZIONE#STOP}</li>
     * </ul>
     *
     * @param nomeJob
     *            nome del job
     * @param descrizioneJob
     *            descrizione (che comparirà sul LOG) del job
     * @param nomeApplicazione
     *            nome dell'applicazione. <b>Obbligatorio per i job che elaborano i LOG "PREMIS"</b>
     * @param operazione
     *            una delle tre operazioni dell'enum
     *
     * @throws EMFError
     *             Errore di esecuzione
     */
    private void esegui(String nomeJob, String descrizioneJob, String nomeApplicazione, OPERAZIONE operazione)
            throws EMFError {
        // Messaggio sul logger di sistema
        StringBuilder info = new StringBuilder(descrizioneJob);
        info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
        if (nomeApplicazione != null) {
            info.append("_").append(nomeApplicazione);
        }
        info.append("]");
        LOG.info(info.toString());

        String message = "Errore durante la schedulazione del job";

        switch (operazione) {
        case START:
            jbossTimerEjb.start(nomeJob, null);
            message = descrizioneJob + ": job correttamente schedulato";
            break;
        case ESECUZIONE_SINGOLA:
            jbossTimerEjb.esecuzioneSingola(nomeJob, null);
            message = descrizioneJob + ": job correttamente schedulato per esecuzione singola";
            break;
        case STOP:
            jbossTimerEjb.stop(nomeJob);
            message = descrizioneJob + ": schedulazione job annullata";
            break;
        }

        // Segnalo l'avvenuta operazione sul job
        getMessageBox().addMessage(new Message(MessageLevel.INF, message));
        getMessageBox().setViewMode(ViewMode.plain);
        // Risetto la pagina rilanciando l'initOnClick
        initOnClick();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="NUOVA GESTIONE JOB">
    @Secure(action = "Menu.Amministrazione.GestioneJobRicerca")
    public void gestioneJobRicercaPage() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneJobRicerca");
        getForm().getGestioneJobRicercaList().setTable(null);
        resetFiltriGestioneJobRicercaPage();
        popolaInformazioniJob();
        tabRicercaJobTabOnClick();
        getForm().getGestioneJobInfo().getSalvaFotoGestioneJob().setEditMode();
        getForm().getGestioneJobInfo().getDisabilitaAllJobs().setEditMode();
        getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setEditMode();
        getForm().getGestioneJobInfo().getRicaricaGestioneJob().setEditMode();
        getSession().removeAttribute("visualizzaRipristinaFoto");
        abilitaDisabilitaBottoniJob(!gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute("fotoSalvata") != null);
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void resetFiltriGestioneJobRicercaPage() throws EMFError {
        getForm().getGestioneJobRicercaFiltri().setEditMode();
        getForm().getGestioneJobRicercaFiltri().reset();
        getForm().getGestioneJobRicercaList().setTable(null);
        BaseTable ambitoTableBean = gestioneJobEjb.getAmbitoJob();
        getForm().getGestioneJobRicercaFiltri().getNm_ambito()
                .setDecodeMap(DecodeMap.Factory.newInstance(ambitoTableBean, "nm_ambito", "nm_ambito"));
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setDecodeMap(ComboGetter.getMappaTiStatoJob());
    }

    public String[] calcolaInformazioniJob() {
        BaseRow infoJobRowBean = gestioneJobEjb.getInfoJobRowBean();
        int niTotJobPresenti = infoJobRowBean.getBigDecimal("ni_tot_job_presenti") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_presenti").intValue() : 0;
        int niTotJobAttivi = infoJobRowBean.getBigDecimal("ni_tot_job_attivi") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_attivi").intValue() : 0;
        int niTotJobDisattivi = infoJobRowBean.getBigDecimal("ni_tot_job_disattivi") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_disattivi").intValue() : 0;

        String[] info = new String[3];
        info[0] = "" + niTotJobPresenti;
        info[1] = "" + niTotJobAttivi;
        info[2] = "" + niTotJobDisattivi;
        return info;
    }

    public void popolaInformazioniJob() {
        String[] info = calcolaInformazioniJob();
        getForm().getGestioneJobRicercaInfo().getNi_tot_job_presenti().setValue(info[0]);
        getForm().getGestioneJobRicercaInfo().getNi_tot_job_attivi().setValue(info[1]);
        getForm().getGestioneJobRicercaInfo().getNi_tot_job_disattivi().setValue(info[2]);
    }

    public void popolaInfoDecJobAmministrazioneJobTab() throws EMFError {
        String[] info = calcolaInformazioniJob();
        getForm().getGestioneJobInfo().getNi_tot_job_presenti().setValue(info[0]);
        getForm().getGestioneJobInfo().getNi_tot_job_attivi().setValue(info[1]);
        getForm().getGestioneJobInfo().getNi_tot_job_disattivi().setValue(info[2]);
    }

    @Override
    public void ricercaGestioneJob() throws EMFError {
        getForm().getGestioneJobRicercaFiltri().getRicercaGestioneJob().setDisableHourGlass(true);
        GestioneJobRicercaFiltri filtri = getForm().getGestioneJobRicercaFiltri();
        if (getRequest().getAttribute("fromLink") == null && (getRequest().getAttribute("fromListaPrinc") == null)) {
            filtri.post(getRequest());
        }
        popolaInformazioniJob();
        if (filtri.validate(getMessageBox())) {
            BaseTable jobTB = gestioneJobEjb.getDecJobTableBean(filtri);
            getForm().getGestioneJobRicercaList().setTable(jobTB);
            getForm().getGestioneJobRicercaList().getTable().setPageSize(100);
            getForm().getGestioneJobRicercaList().getTable().first();
        }
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void tabRicercaJobTabOnClick() throws EMFError {
        getForm().getGestioneJobTabs().setCurrentTab(getForm().getGestioneJobTabs().getRicercaJobTab());
        ricercaGestioneJob();
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void tabAmmJobTabOnClick() throws EMFError {
        getForm().getGestioneJobTabs().setCurrentTab(getForm().getGestioneJobTabs().getAmmJobTab());
        abilitaDisabilitaBottoniJob(!gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute("fotoSalvata") != null);

        decoraDatiTabAmmJobTab();

        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void decoraDatiTabAmmJobTab() throws EMFError {
        popolaInfoDecJobAmministrazioneJobTab();
        popolaInfoDecJobFotoAmministrazioneJobTab();

        BaseTable jobTB = gestioneJobEjb.getDecJobTableBeanPerAmm();
        getForm().getGestioneJobListPerAmm().setTable(jobTB);
        getForm().getGestioneJobListPerAmm().getTable().setPageSize(100);
        getForm().getGestioneJobListPerAmm().getTable().first();

        BaseTable jobFotoTB = gestioneJobEjb.getDecJobFotoTableBeanPerAmm();
        getForm().getGestioneJobFotoListPerAmm().setTable(jobFotoTB);
        getForm().getGestioneJobFotoListPerAmm().getTable().setPageSize(100);
        getForm().getGestioneJobFotoListPerAmm().getTable().first();

    }

    public void getNuoviJob() throws JSONException {
        JSONObject jso = new JSONObject();
        // Recupero i nomi dei nuovi JOB
        Object[] nmJobObj = gestioneJobEjb.getInfoJobFotoNomiJobRowBean();
        jso.put("nm_job_array_nuovi", (Set<String>) nmJobObj[0]);
        jso.put("nm_job_array_solo_foto", (Set<String>) nmJobObj[1]);
        redirectToAjax(jso);
    }

    public void popolaInfoDecJobFotoAmministrazioneJobTab() throws EMFError {
        BaseRow infoJobFotoRowBean = gestioneJobEjb.getInfoJobFotoRowBean();
        int niTotJobPresenti2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2").intValue() : 0;
        int niTotJobAttivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2").intValue() : 0;
        int niTotJobDisattivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2").intValue() : 0;
        int niTotJobNuovi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2").intValue() : 0;
        int niTotJobSoloFoto = infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto").intValue() : 0;

        Date dataLastFoto = infoJobFotoRowBean.getTimestamp("last_job_foto");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        getForm().getGestioneJobInfo2().getNi_tot_job_presenti2().setValue("" + niTotJobPresenti2);
        getForm().getGestioneJobInfo2().getNi_tot_job_attivi2().setValue("" + niTotJobAttivi2);
        getForm().getGestioneJobInfo2().getNi_tot_job_disattivi2().setValue("" + niTotJobDisattivi2);

        getForm().getGestioneJobInfo2().getNi_tot_job_nuovi2().setValue("" + niTotJobNuovi2);
        getForm().getGestioneJobInfo2().getNi_tot_job_solo_foto().setValue("" + niTotJobSoloFoto);

        getForm().getInfoJob2Section().setLegend("Foto dei job alla data " + df.format(dataLastFoto));

    }

    @Override
    public void startMassivoGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<Object[]>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<Object[]>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = new BaseRow();
                riga = tabella.getRow(i);
                if (riga.getString("job_selezionati").equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString("nm_job");
                    jobDaValutare[2] = riga.getString("ds_job");
                    if (riga.getString("stato_job").equals("DISATTIVO")) {
                        listaSelezionati.add(jobDaValutare);
                    } else {
                        listaNonSelezionati.add(jobDaValutare);
                    }
                }
            }

            if (almenoUnoSel) {// listaSelezionati

                String message = "";
                String jobSchedulatiString = "";
                for (Object[] obj : listaSelezionati) {
                    startGestioneJobOperation((int) obj[0], (String) obj[1], (String) obj[2]);
                    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobSchedulatiString.equals("")) {
                    message = "Sono stati schedulati i seguenti job: <br><br>" + jobSchedulatiString + "<br>";
                }

                String jobNonSchedulatiString = "";
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobNonSchedulatiString.equals("")) {
                    message = message + "<br>Non sono stati schedulati i seguenti job: <br><br>"
                            + jobNonSchedulatiString + "<br> in quanto in stato già ATTIVO o IN_ELABORAZIONE<br>";
                }
                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox()
                        .addInfo(message + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
            } else {
                getMessageBox().addInfo("Nessun job selezionato");
            }
        } else {
            getMessageBox().addInfo("Nessun job selezionato");
        }
        popolaInformazioniJob();
        ricercaGestioneJob();
    }

    @Override
    public void stopMassivoGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<Object[]>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<Object[]>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = new BaseRow();
                riga = tabella.getRow(i);
                if (riga.getString("job_selezionati").equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString("nm_job");
                    jobDaValutare[2] = riga.getString("ds_job");
                    if (riga.getString("stato_job").equals("ATTIVO")) {
                        listaSelezionati.add(jobDaValutare);
                    } else {
                        listaNonSelezionati.add(jobDaValutare);
                    }
                }
            }

            if (almenoUnoSel) {
                String jobSchedulatiString = "";

                String message = "";
                for (Object[] obj : listaSelezionati) {
                    stopGestioneJobOperation((int) obj[0], (String) obj[1], (String) obj[2]);
                    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobSchedulatiString.equals("")) {
                    message = "Sono stati stoppati i seguenti job: <br><br>" + jobSchedulatiString + "<br>";
                }

                String jobNonSchedulatiString = "";
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobNonSchedulatiString.equals("")) {
                    message = message + "<br>Non sono stati stoppati i seguenti job: <br><br>" + jobNonSchedulatiString
                            + "<br> in quanto in stato già DISATTIVO o IN_ESECUZIONE<br>";
                }

                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox()
                        .addInfo(message + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
            } else {
                getMessageBox().addInfo("Nessun job selezionato");
            }
        } else {
            getMessageBox().addInfo("Nessun job selezionato");
        }
        popolaInformazioniJob();
        ricercaGestioneJob();
    }

    @Override
    public void esecuzioneSingolaMassivaGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<Object[]>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<Object[]>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = new BaseRow();
                riga = tabella.getRow(i);
                if (riga.getString("job_selezionati").equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString("nm_job");
                    jobDaValutare[2] = riga.getString("ds_job");
                    if (riga.getString("stato_job").equals("DISATTIVO")) {
                        listaSelezionati.add(jobDaValutare);
                    } else {
                        listaNonSelezionati.add(jobDaValutare);
                    }
                }
            }

            if (almenoUnoSel) {
                String jobSchedulatiString = "";

                String message = "";
                for (Object[] obj : listaSelezionati) {
                    esecuzioneSingolaGestioneJobOperation((int) obj[0], (String) obj[1], (String) obj[2]);
                    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
                }

                if (!jobSchedulatiString.equals("")) {
                    message = "Sono stati attivati in esecuzione singola i seguenti job: <br><br>" + jobSchedulatiString
                            + "<br>";
                }

                String jobNonSchedulatiString = "";
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobNonSchedulatiString.equals("")) {
                    message = message + "<br>Non sono stati attivati in esecuzione singola i seguenti job: <br><br>"
                            + jobNonSchedulatiString + "<br> in quanto in stato già ATTIVO o IN_ESECUZIONE<br>";
                }

                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox()
                        .addInfo(message + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
            } else {
                getMessageBox().addInfo("Nessun job selezionato");
            }
        } else {
            getMessageBox().addInfo("Nessun job selezionato");
        }
        popolaInformazioniJob();
        ricercaGestioneJob();
    }

    @Override
    public void salvaFotoGestioneJob() throws EMFError {
        // Eseguo il salvataggio foto, solo se ho almeno 1 JOB attivo
        BaseTable tabella = (BaseTable) getForm().getGestioneJobListPerAmm().getTable();
        boolean trovatoAttivo = false;
        for (BaseRow riga : tabella) {
            if (riga.getString("stato_job").equals("ATTIVO")) {
                trovatoAttivo = true;
                break;
            }
        }
        if (trovatoAttivo) {
            gestioneJobEjb.salvaFoto();
            getSession().setAttribute("fotoSalvata", true);
            getMessageBox().addInfo("Foto JOB salvata con successo!");
        } else {
            getMessageBox().addInfo("Nessun JOB attivo trovato: non è stata salvata la foto!");
        }
        tabAmmJobTabOnClick();
        abilitaDisabilitaBottoniJob(!gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute("fotoSalvata") != null);
    }

    @Override
    public void ripristinaFotoGestioneJob() throws EMFError {
        gestioneJobEjb.ripristinaFotoGestioneJob();
        tabAmmJobTabOnClick();
        getMessageBox().addInfo(
                "Ripristino foto effettuato con successo! Attendere il minuto successivo per l'allineamento dei JOB eventualmente rischedulati");
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void ricaricaGestioneJob() throws EMFError {
        tabAmmJobTabOnClick();
    }

    public void abilitaDisabilitaBottoniJob(boolean abilitaRipristinaFoto, boolean abilitaDisabilitaAllJobs) {
        if (abilitaRipristinaFoto) {
            getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setReadonly(false);
            getSession().setAttribute("visualizzaRipristinaFoto", true);
        } else {
            getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setReadonly(true);
            getSession().removeAttribute("visualizzaRipristinaFoto");
        }

        if (abilitaDisabilitaAllJobs) {
            getForm().getGestioneJobInfo().getDisabilitaAllJobs().setReadonly(false);
        } else {
            getForm().getGestioneJobInfo().getDisabilitaAllJobs().setReadonly(true);
        }
    }

    public void apriVisualizzaSchedulazioniJob() throws EMFError {
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        String nmJob = ((BaseTable) getForm().getGestioneJobRicercaList().getTable()).getRow(riga).getString("nm_job");
        redirectToSchedulazioniJob(nmJob);
    }

    private void redirectToSchedulazioniJob(String nmJob) throws EMFError {
        MonitoraggioForm form = prepareRedirectToSchedulazioniJob(nmJob);
        redirectToPage(Application.Actions.MONITORAGGIO, form, form.getJobSchedulatiList().getName(),
                getForm().getGestioneJobRicercaList().getTable(), getNavigationEvent());
    }

    private MonitoraggioForm prepareRedirectToSchedulazioniJob(String nmJob) throws EMFError {
        MonitoraggioForm form = new MonitoraggioForm();
        /* Preparo la pagina di destinazione */
        form.getFiltriJobSchedulati().setEditMode();
        DecodeMap dec = ComboGetter.getMappaSortedGenericEnum("nm_job", JobConstants.JobEnum.values());
        dec.keySet().remove(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE.name());
        dec.keySet().remove(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE.name());
        form.getFiltriJobSchedulati().getNm_job().setDecodeMap(dec);
        /* Setto il valore del Job da cercare in Visualizzazione Job Schedulati */
        form.getFiltriJobSchedulati().getNm_job().setValue(nmJob);
        // Preparo la data di Schedulazione Da una settimana prima rispetto la data corrente
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        form.getFiltriJobSchedulati().getDt_reg_log_job_da().setValue(f.format(c.getTime()));
        getSession().setAttribute("fromRicercaJob", true);
        return form;
    }

    // redirectToPage
    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=ricercaJobSchedulatiDaGestioneJob", form);
    }

    public void startGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString("nm_job");
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString("ds_job");
        startGestioneJobOperation(riga, nmJob, dsJob);
        getRequest().setAttribute("fromListaPrinc", true);
        ricercaGestioneJob();

        // forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void startGestioneJobOperation(int riga, String nmJob, String dsJob) throws EMFError {
        // Eseguo lo start del job interessato
        setJobVBeforeOperation(nmJob, riga);
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.START);
    }

    public void stopGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString("nm_job");
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString("ds_job");
        stopGestioneJobOperation(riga, nmJob, dsJob);
        getRequest().setAttribute("fromListaPrinc", true);
        ricercaGestioneJob();
        // forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void stopGestioneJobOperation(int riga, String nmJob, String dsJob) throws EMFError {
        // Eseguo lo start del job interessato
        setJobVBeforeOperation(nmJob, riga);
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.STOP);
    }

    public void esecuzioneSingolaGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString("nm_job");
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString("ds_job");
        esecuzioneSingolaGestioneJobOperation(riga, nmJob, dsJob);
        getRequest().setAttribute("fromListaPrinc", true);
        ricercaGestioneJob();

        // forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void esecuzioneSingolaGestioneJobOperation(int riga, String nmJob, String dsJob) throws EMFError {
        // Eseguo lo start del job interessato
        setJobVBeforeOperation(nmJob, riga);
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void totJobOperation() throws EMFError {
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobAttiviOperation() throws EMFError {
        String[] attivi = new String[1];
        attivi[0] = "ATTIVO";
        getRequest().setAttribute("fromLink", true);
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(attivi);
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobDisattiviOperation() throws EMFError {
        String[] disattivi = new String[1];
        disattivi[0] = "DISATTIVO";
        getRequest().setAttribute("fromLink", true);
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(disattivi);
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void disabilitaAllJobs() throws EMFError {
        gestioneJobEjb.disabilitaAllJobs();
        tabAmmJobTabOnClick();
        getMessageBox().addInfo("Tutti i job disattivati con successo!");
        forwardToPublisher(getLastPublisher());
    }

    private void eseguiNuovo(String nomeJob, String descrizioneJob, String nomeApplicazione, OPERAZIONE operazione)
            throws EMFError {
        // Messaggio sul logger di sistema
        StringBuilder info = new StringBuilder(descrizioneJob);
        info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
        if (nomeApplicazione != null) {
            info.append("_").append(nomeApplicazione);
        }
        info.append("]");
        LOG.info(info.toString());

        String message = "Errore durante la schedulazione del job";

        switch (operazione) {
        case START:
            jbossTimerEjb.start(nomeJob, null);
            message = descrizioneJob
                    + ": job correttamente schedulato. L'operazione richiesta verrà schedulata correttamente entro il prossimo minuto.";
            break;
        case ESECUZIONE_SINGOLA:
            jbossTimerEjb.esecuzioneSingola(nomeJob, null);
            message = descrizioneJob
                    + ": job correttamente schedulato per esecuzione singola. L'operazione richiesta verrà effettuata entro il prossimo minuto.";
            break;
        case STOP:
            jbossTimerEjb.stop(nomeJob);
            message = descrizioneJob
                    + ": schedulazione job annullata. L'operazione richiesta diventerà effettiva entro il prossimo minuto.";
            break;
        }

        // Segnalo l'avvenuta operazione sul job
        getMessageBox().addMessage(new Message(MessageLevel.INF, message));
        getMessageBox().setViewMode(ViewMode.plain);
    }

    public void setJobVBeforeOperation(String nmJob, int riga) throws EMFError {
        Timestamp dataAttivazioneJob = getActivationDateJob(nmJob);
        StatoJob statoJob = new StatoJob(nmJob, null, null, null, null, null, null, null, dataAttivazioneJob);
        gestisciStatoJobNuovo(statoJob);
    }

    private boolean gestisciStatoJobNuovo(StatoJob statoJob) throws EMFError {
        // se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare nulla
        return jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

    }
    // </editor-fold>
}
