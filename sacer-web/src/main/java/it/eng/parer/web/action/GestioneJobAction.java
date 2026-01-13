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
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ComboGetter;
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

    public static final String FROM_SCHEDULAZIONI_JOB = "fromSchedulazioniJob";

    private final Logger LOG = LoggerFactory.getLogger(GestioneJobAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

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
     * @param jobName the job name
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
        return Application.Publisher.GESTIONE_JOB_RICERCA;
    }

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

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
        goBack();
    }

    @Override
    public void process() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            ricercaGestioneJob();
        } catch (EMFError ex) {
            LOG.error(ex.getDescription());
            getMessageBox().addError("Errore durante il caricamento dell'elenco dei JOB");
        }

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
        abilitaDisabilitaBottoniJob(
                !gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute("fotoSalvata") != null);
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void resetFiltriGestioneJobRicercaPage() throws EMFError {
        getForm().getGestioneJobRicercaFiltri().setEditMode();
        getForm().getGestioneJobRicercaFiltri().reset();
        getForm().getGestioneJobRicercaList().setTable(null);
        BaseTable ambitoTableBean = gestioneJobEjb.getAmbitoJob();
        getForm().getGestioneJobRicercaFiltri().getNm_ambito().setDecodeMap(
                DecodeMap.Factory.newInstance(ambitoTableBean, "nm_ambito", "nm_ambito"));
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job()
                .setDecodeMap(ComboGetter.getMappaTiStatoJob());
    }

    public String[] calcolaInformazioniJob() {
        BaseRow infoJobRowBean = gestioneJobEjb.getInfoJobRowBean();
        int niTotJobPresenti = infoJobRowBean.getBigDecimal("ni_tot_job_presenti") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_presenti").intValue()
                : 0;
        int niTotJobAttivi = infoJobRowBean.getBigDecimal("ni_tot_job_attivi") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_attivi").intValue()
                : 0;
        int niTotJobDisattivi = infoJobRowBean.getBigDecimal("ni_tot_job_disattivi") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_disattivi").intValue()
                : 0;

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

        if (getRequest().getAttribute("fromLink") == null
                && getRequest().getAttribute("fromListaPrinc") == null) {
            if (getSession().getAttribute(FROM_SCHEDULAZIONI_JOB) != null) {
                getSession().removeAttribute(FROM_SCHEDULAZIONI_JOB);
            } else {
                filtri.post(getRequest());
            }
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
        getForm().getGestioneJobTabs()
                .setCurrentTab(getForm().getGestioneJobTabs().getRicercaJobTab());
        ricercaGestioneJob();
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void tabAmmJobTabOnClick() throws EMFError {
        getForm().getGestioneJobTabs().setCurrentTab(getForm().getGestioneJobTabs().getAmmJobTab());
        abilitaDisabilitaBottoniJob(
                !gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
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
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2").intValue()
                : 0;
        int niTotJobAttivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2").intValue()
                : 0;
        int niTotJobDisattivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2").intValue()
                : 0;
        int niTotJobNuovi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2").intValue()
                : 0;
        int niTotJobSoloFoto = infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto").intValue()
                : 0;

        Date dataLastFoto = infoJobFotoRowBean.getTimestamp("last_job_foto");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        getForm().getGestioneJobInfo2().getNi_tot_job_presenti2().setValue("" + niTotJobPresenti2);
        getForm().getGestioneJobInfo2().getNi_tot_job_attivi2().setValue("" + niTotJobAttivi2);
        getForm().getGestioneJobInfo2().getNi_tot_job_disattivi2()
                .setValue("" + niTotJobDisattivi2);

        getForm().getGestioneJobInfo2().getNi_tot_job_nuovi2().setValue("" + niTotJobNuovi2);
        getForm().getGestioneJobInfo2().getNi_tot_job_solo_foto().setValue("" + niTotJobSoloFoto);

        getForm().getInfoJob2Section()
                .setLegend("Foto dei job alla data " + df.format(dataLastFoto));

    }

    @Override
    public void startMassivoGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNoTimer = new ArrayList<>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = tabella.getRow(i);
                if (riga.getString("job_selezionati").equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString("nm_job");
                    jobDaValutare[2] = riga.getString("ds_job");
                    if (riga.getString("stato_job").equals("DISATTIVO")) {
                        if (riga.getString("ti_sched_job").equals("NO_TIMER")) {
                            listaNoTimer.add(jobDaValutare);
                        } else {
                            listaSelezionati.add(jobDaValutare);
                        }
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
                    message = "Sono stati schedulati i seguenti job: <br><br>" + jobSchedulatiString
                            + "<br>";
                }

                String jobNonSchedulatiString = "";
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobNonSchedulatiString.equals("")) {
                    message = message + "<br>Non sono stati schedulati i seguenti job: <br><br>"
                            + jobNonSchedulatiString
                            + "<br> in quanto in stato già ATTIVO o IN_ELABORAZIONE<br>";
                }

                String jobNoTimerString = "";
                for (Object[] obj : listaNoTimer) {
                    jobNoTimerString = jobNoTimerString + (String) obj[1] + "<br>";
                }
                if (!jobNoTimerString.equals("")) {
                    message = message + "<br>Non sono stati schedulati i seguenti job: <br><br>"
                            + jobNoTimerString
                            + "<br> in quanto di tipo NO_TIMER. Per essi è possibile lanciare solo l'ESECUZIONE SINGOLA<br>";
                }

                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox().addInfo(message
                        + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
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
            ArrayList<Object[]> listaSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNoTimer = new ArrayList<>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = tabella.getRow(i);
                if (riga.getString("job_selezionati").equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString("nm_job");
                    jobDaValutare[2] = riga.getString("ds_job");
                    if (riga.getString("stato_job").equals("ATTIVO")) {
                        if (riga.getString("ti_sched_job").equals("NO_TIMER")) {
                            listaNoTimer.add(jobDaValutare);
                        } else {
                            listaSelezionati.add(jobDaValutare);
                        }
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
                    message = "Sono stati stoppati i seguenti job: <br><br>" + jobSchedulatiString
                            + "<br>";
                }

                String jobNonSchedulatiString = "";
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobNonSchedulatiString.equals("")) {
                    message = message + "<br>Non sono stati stoppati i seguenti job: <br><br>"
                            + jobNonSchedulatiString
                            + "<br> in quanto in stato già DISATTIVO o IN_ESECUZIONE<br>";
                }

                String jobNoTimerString = "";
                for (Object[] obj : listaNoTimer) {
                    jobNoTimerString = jobNoTimerString + (String) obj[1] + "<br>";
                }
                if (!jobNoTimerString.equals("")) {
                    message = message + "<br>Non sono stati stoppati i seguenti job: <br><br>"
                            + jobNoTimerString + "<br> in quanto di tipo NO_TIMER<br>";
                }

                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox().addInfo(message
                        + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
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
            ArrayList<Object[]> listaSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = tabella.getRow(i);
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
                    esecuzioneSingolaGestioneJobOperation((int) obj[0], (String) obj[1],
                            (String) obj[2]);
                    jobSchedulatiString = jobSchedulatiString + (String) obj[1] + "<br>";
                }

                if (!jobSchedulatiString.equals("")) {
                    message = "Sono stati attivati in esecuzione singola i seguenti job: <br><br>"
                            + jobSchedulatiString + "<br>";
                }

                String jobNonSchedulatiString = "";
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString = jobNonSchedulatiString + (String) obj[1] + "<br>";
                }
                if (!jobNonSchedulatiString.equals("")) {
                    message = message
                            + "<br>Non sono stati attivati in esecuzione singola i seguenti job: <br><br>"
                            + jobNonSchedulatiString
                            + "<br> in quanto in stato già ATTIVO o IN_ESECUZIONE<br>";
                }

                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox().addInfo(message
                        + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
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
        abilitaDisabilitaBottoniJob(
                !gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
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

    public void abilitaDisabilitaBottoniJob(boolean abilitaRipristinaFoto,
            boolean abilitaDisabilitaAllJobs) {
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
        String nmJob = ((BaseTable) getForm().getGestioneJobRicercaList().getTable()).getRow(riga)
                .getString("nm_job");
        redirectToSchedulazioniJob(nmJob);
    }

    private void redirectToSchedulazioniJob(String nmJob) throws EMFError {
        MonitoraggioForm form = prepareRedirectToSchedulazioniJob(nmJob);
        redirectToPage(Application.Actions.MONITORAGGIO, form,
                form.getJobSchedulatiList().getName(),
                getForm().getGestioneJobRicercaList().getTable(), getNavigationEvent());
    }

    private MonitoraggioForm prepareRedirectToSchedulazioniJob(String nmJob) throws EMFError {
        MonitoraggioForm form = new MonitoraggioForm();
        /* Preparo la pagina di destinazione */
        form.getFiltriJobSchedulati().setEditMode();
        DecodeMap dec = ComboGetter.getMappaSortedGenericEnum("nm_job",
                JobConstants.JobEnum.values());
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
        getSession().setAttribute("fromGestioneJob", true);
        return form;
    }

    // redirectToPage
    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
            BaseTableInterface<?> table, String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate))
                .setTable(table);
        redirectToAction(action, "?operation=ricercaJobSchedulati", form);
    }

    // public
    public void startGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
                .getString("nm_job");
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
                .getString("ds_job");
        startGestioneJobOperation(riga, nmJob, dsJob);
        getRequest().setAttribute("fromListaPrinc", true);
        ricercaGestioneJob();
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
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
                .getString("nm_job");
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
                .getString("ds_job");
        stopGestioneJobOperation(riga, nmJob, dsJob);
        getRequest().setAttribute("fromListaPrinc", true);
        ricercaGestioneJob();
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
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
                .getString("nm_job");
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga)
                .getString("ds_job");
        esecuzioneSingolaGestioneJobOperation(riga, nmJob, dsJob);
        getRequest().setAttribute("fromListaPrinc", true);
        ricercaGestioneJob();
    }

    public void esecuzioneSingolaGestioneJobOperation(int riga, String nmJob, String dsJob)
            throws EMFError {
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
        getForm().getGestioneJobRicercaFiltri().getDs_job().setValue("");
        getForm().getGestioneJobRicercaFiltri().getNm_ambito().setValue("");
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(attivi);
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobDisattiviOperation() throws EMFError {
        String[] disattivi = new String[1];
        disattivi[0] = "DISATTIVO";
        getRequest().setAttribute("fromLink", true);
        getForm().getGestioneJobRicercaFiltri().getDs_job().setValue("");
        getForm().getGestioneJobRicercaFiltri().getNm_ambito().setValue("");
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

    private void eseguiNuovo(String nomeJob, String descrizioneJob, String nomeApplicazione,
            OPERAZIONE operazione) throws EMFError {
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
        StatoJob statoJob = new StatoJob(nmJob, null, null, null, null, null, null, null,
                dataAttivazioneJob);
        gestisciStatoJobNuovo(statoJob);
    }

    private boolean gestisciStatoJobNuovo(StatoJob statoJob) throws EMFError {
        // se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare
        // nulla
        return jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="UI Classe che mappa lo stato dei job">
    /**
     * Astrazione dei componenti della pagina utilizzati per i "box" dei job.
     *
     * @author Snidero_L
     */
    public static final class StatoJob {

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
        private static final Button<String> NULL_BUTTON = new Button<String>(null, "EMPTY_BUTTON",
                "Pulsante vuoto", null, null, null, false, true, true, false);

        public StatoJob(String nomeJob, Input<String> flagDataAccurata, Button<String> start,
                Button<String> esecuzioneSingola, Button<String> stop,
                Input<Timestamp> dataProssimaAttivazione, CheckBox<String> checkAttivo,
                Input<Timestamp> dataRegistrazioneJob, Timestamp dataAttivazione) {
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
}
