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

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.MonitoraggioTpiAbstractAction;
import it.eng.parer.slite.gen.form.MonitoraggioTpiForm;
import it.eng.parer.slite.gen.tablebean.TpiSchedJobRowBean;
import it.eng.parer.slite.gen.tablebean.VrsDtVersRowBean;
import it.eng.parer.slite.gen.tablebean.VrsDtVersTableBean;
import it.eng.parer.slite.gen.tablebean.VrsPathDtVersRowBean;
import it.eng.parer.slite.gen.viewbean.TpiVLisDtSchedRowBean;
import it.eng.parer.web.helper.MonitoraggioTpiHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.menu.MenuEntry;
import it.eng.tpi.dto.EsitoConnessione;
import it.eng.tpi.dto.RichiestaTpi;
import it.eng.tpi.dto.RichiestaTpiInput;
import it.eng.tpi.util.RichiestaWSTpi;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import javax.ejb.EJB;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
public class MonitoraggioTpiAction extends MonitoraggioTpiAbstractAction {

    private static Logger log = LoggerFactory.getLogger(MonitoraggioAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioTpiHelper")
    private MonitoraggioTpiHelper monitoraggioTpiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/JobHelper")
    private JobHelper jobHelper;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
	if (getRequest().getParameter("table")
		.equals(getForm().getDateVersamentoList().getName())) {

	    getForm().getDateVersamentoDetail().getFl_ark()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getDateVersamentoDetail().getFl_ark_secondario()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getDateVersamentoDetail().getFl_file_no_ark()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getDateVersamentoDetail().getFl_file_no_ark_secondario()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

	    BigDecimal idDtVers = ((VrsDtVersRowBean) getForm().getDateVersamentoList().getTable()
		    .getCurrentRow()).getIdDtVers();
	    // Aggiorno i dati nel caso abbiano subito modifiche
	    VrsDtVersRowBean currentRow = monitoraggioTpiHelper.getDataVersamentoArk(idDtVers);
	    getForm().getDateVersamentoDetail().copyFromBean(currentRow);

	    getForm().getDateVersamentoDetailPathList()
		    .setTable(monitoraggioTpiHelper.getPathsDateVersamentoArk(idDtVers));
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getDateVersamentoDetailPathList().getTable().first();
	    getForm().getDateVersamentoDetailPathList().getTable().setPageSize(10);

	    if (getForm().getDateVersamentoDetail().getTi_stato_dt_vers().parse()
		    .equals(JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name())) {
		getForm().getDateVersamentoDetail().getCallRiArk().setEditMode();
	    } else {
		getForm().getDateVersamentoDetail().getCallRiArk().setViewMode();
	    }
	} else if (getRequest().getParameter("table")
		.equals(getForm().getDateVersamentoDetailPathList().getName())) {
	    VrsPathDtVersRowBean currentPath = ((VrsPathDtVersRowBean) getForm()
		    .getDateVersamentoDetailPathList().getTable().getCurrentRow());

	    getForm().getPathVersamentoDetail().getFl_path_ark()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getPathVersamentoDetail().getFl_path_ark_secondario()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getPathVersamentoDetail().getFl_path_file_no_ark()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	    getForm().getPathVersamentoDetail().getFl_path_file_no_ark_secondario()
		    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

	    getForm().getPathVersamentoDetail().copyFromBean(currentPath);

	    getForm().getFileNoArkPrimarioList().setTable(monitoraggioTpiHelper.getFilesNoArkPath(
		    currentPath.getIdPathDtVers(), JobConstants.ArkPath.PRIMARIO.name()));
	    getForm().getFileNoArkPrimarioList().getTable().first();
	    getForm().getFileNoArkPrimarioList().getTable().setPageSize(10);

	    getForm().getFileNoArkSecondarioList().setTable(monitoraggioTpiHelper.getFilesNoArkPath(
		    currentPath.getIdPathDtVers(), JobConstants.ArkPath.SECONDARIO.name()));
	    getForm().getFileNoArkSecondarioList().getTable().first();
	    getForm().getFileNoArkSecondarioList().getTable().setPageSize(10);
	} else if (getRequest().getParameter("table")
		.equals(getForm().getDateSchedJobTpiList().getName())) {
	    BigDecimal currentSched = ((TpiVLisDtSchedRowBean) getForm().getDateSchedJobTpiList()
		    .getTable().getCurrentRow()).getIdDtSched();
	    getForm().getDataSchedDetail()
		    .copyFromBean(monitoraggioTpiHelper.getDataSchedulazioneTpi(currentSched));

	    getForm().getJobFileVersatiPrimarioList().setTable(monitoraggioTpiHelper.getJobList(
		    currentSched, JobConstants.DB_FALSE, JobConstants.ArkPath.PRIMARIO.name()));
	    getForm().getJobFileVersatiPrimarioList().getTable().first();
	    getForm().getJobFileVersatiPrimarioList().getTable().setPageSize(10);

	    getForm().getJobFileMigratiPrimarioList().setTable(monitoraggioTpiHelper.getJobList(
		    currentSched, JobConstants.DB_TRUE, JobConstants.ArkPath.PRIMARIO.name()));
	    getForm().getJobFileMigratiPrimarioList().getTable().first();
	    getForm().getJobFileMigratiPrimarioList().getTable().setPageSize(10);

	    getForm().getJobFileVersatiSecondarioList().setTable(monitoraggioTpiHelper.getJobList(
		    currentSched, JobConstants.DB_FALSE, JobConstants.ArkPath.SECONDARIO.name()));
	    getForm().getJobFileVersatiSecondarioList().getTable().first();
	    getForm().getJobFileVersatiSecondarioList().getTable().setPageSize(10);

	    getForm().getJobFileMigratiSecondarioList().setTable(monitoraggioTpiHelper.getJobList(
		    currentSched, JobConstants.DB_TRUE, JobConstants.ArkPath.SECONDARIO.name()));
	    getForm().getJobFileMigratiSecondarioList().getTable().first();
	    getForm().getJobFileMigratiSecondarioList().getTable().setPageSize(10);

	}
    }

    @Override
    public void undoDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void insertDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
	if (getRequest().getParameter("table")
		.equals(getForm().getDateVersamentoList().getName())) {
	    forwardToPublisher(Application.Publisher.MONITORAGGIO_DATE_VERS_DETAIL);
	} else if (getRequest().getParameter("table")
		.equals(getForm().getDateVersamentoDetailPathList().getName())) {
	    forwardToPublisher(Application.Publisher.MONITORAGGIO_PATH_VERS_DETAIL);
	} else if (getRequest().getParameter("table")
		.equals(getForm().getDateSchedJobTpiList().getName())) {
	    forwardToPublisher(Application.Publisher.MONITORAGGIO_DATE_SCHED_JOB_TPI_DETAIL);
	}
    }

    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.MONITORAGGIO_DATE_VERS_RICERCA;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
	try {
	    User u = (User) getRequest().getSession().getAttribute("###_USER_CONTAINER");
	    int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
	    String lastMenuEntry = ((MenuEntry) u.getMenu().getSelectedPath("").get(lastIndex))
		    .getCodice();
	    if (lastMenuEntry.contains("RicercaDateVersamento")) {
		// Ricarica dati ricerca date versamento
		switch (publisherName) {
		case Application.Publisher.MONITORAGGIO_DATE_VERS_RICERCA: {
		    int paginaCorrenteDtVers = getForm().getDateVersamentoList().getTable()
			    .getCurrentPageIndex();
		    int inizioDtVers = getForm().getDateVersamentoList().getTable()
			    .getFirstRowPageIndex();
		    int pageSize = getForm().getDateVersamentoList().getTable().getPageSize();
		    Date dataDa = getForm().getFiltriRicercaDateVers().getDt_vers_da().parse();
		    Date dataA = getForm().getFiltriRicercaDateVers().getDt_vers_a().parse();
		    getForm().getDateVersamentoList()
			    .setTable(monitoraggioTpiHelper.getDateVersamentoArk(dataDa, dataA,
				    getForm().getFiltriRicercaDateVers().getFl_migraz().parse(),
				    getForm().getFiltriRicercaDateVers().getTi_stato_dt_vers()
					    .parse()));
		    // Workaround in modo che la lista punti al primo record, non all'ultimo
		    getForm().getDateVersamentoList().getTable().first();
		    getForm().getDateVersamentoList().getTable().setPageSize(pageSize);
		    this.lazyLoadGoPage(getForm().getDateVersamentoList(), paginaCorrenteDtVers);
		    // Ritorno alla pagina
		    getForm().getDateVersamentoList().getTable().setCurrentRowIndex(inizioDtVers);
		    break;
		}
		case Application.Publisher.MONITORAGGIO_DATE_VERS_DETAIL: {
		    int paginaCorrentePath = getForm().getDateVersamentoDetailPathList().getTable()
			    .getCurrentPageIndex();
		    int inizioPath = getForm().getDateVersamentoDetailPathList().getTable()
			    .getFirstRowPageIndex();
		    int pageSize = getForm().getDateVersamentoDetailPathList().getTable()
			    .getPageSize();
		    getForm().getDateVersamentoDetailPathList()
			    .setTable(monitoraggioTpiHelper.getPathsDateVersamentoArk(
				    getForm().getDateVersamentoDetail().getId_dt_vers().parse()));
		    // Workaround in modo che la lista punti al primo record, non all'ultimo
		    getForm().getDateVersamentoDetailPathList().getTable().first();
		    getForm().getDateVersamentoDetailPathList().getTable().setPageSize(pageSize);
		    this.lazyLoadGoPage(getForm().getDateVersamentoDetailPathList(),
			    paginaCorrentePath);
		    // Ritorno alla pagina
		    getForm().getDateVersamentoDetailPathList().getTable()
			    .setCurrentRowIndex(inizioPath);
		    break;
		}
		}
	    }
	} catch (EMFError ex) {
	    log.error("Eccezione", ex);
	    getMessageBox().addFatal("Errore fatale nell'applicativo. Contattare l'assistenza");
	}
    }

    @Override
    public String getControllerName() {
	return Application.Actions.MONITORAGGIO_TPI;
    }

    /*
     * GESTIONE DATE VERSAMENTO ARK
     */
    @Secure(action = "Menu.Monitoraggio.RicercaDateVersamento")
    public void loadRicercaDateVers() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Monitoraggio.RicercaDateVersamento");

	getForm().getFiltriRicercaDateVers().reset();
	getForm().getFiltriRicercaDateVers().getTi_stato_dt_vers()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("statoDtVers",
			JobConstants.ArkStatusEnum.getComboRicercaDtVers()));
	getForm().getFiltriRicercaDateVers().getFl_migraz()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltriRicercaDateVers().setEditMode();

	getForm().getDateVersamentoList().setTable(new VrsDtVersTableBean());

	forwardToPublisher(Application.Publisher.MONITORAGGIO_DATE_VERS_RICERCA);
    }

    @Override
    public void ricercaDateVers() throws EMFError {
	MonitoraggioTpiForm.FiltriRicercaDateVers filtri = getForm().getFiltriRicercaDateVers();
	if (filtri.postAndValidate(getRequest(), getMessageBox())) {
	    Date dataDa = filtri.getDt_vers_da().parse();
	    Date dataA = filtri.getDt_vers_a().parse();

	    if (dataDa == null && dataA == null) {
		log.debug("Date nulle - almeno una deve essere valorizzata");
		getMessageBox().addError("Selezionare almeno una data di versamento");
	    } else if (dataDa != null && dataA != null && dataA.before(dataDa)) {
		log.debug(filtri.getDt_vers_da().getHtmlDescription() + " superiore a "
			+ filtri.getDt_vers_a().getHtmlDescription());
		getMessageBox().addError(filtri.getDt_vers_da().getHtmlDescription()
			+ " superiore a " + filtri.getDt_vers_a().getHtmlDescription());
	    }

	    if (!getMessageBox().hasError()) {
		// La validazione non ha riportato errori. Carico la tabella con i filtri impostati
		getForm().getDateVersamentoList()
			.setTable(monitoraggioTpiHelper.getDateVersamentoArk(dataDa, dataA,
				filtri.getFl_migraz().parse(),
				filtri.getTi_stato_dt_vers().parse()));
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getDateVersamentoList().getTable().first();
		getForm().getDateVersamentoList().getTable().setPageSize(10);
	    }
	}
	forwardToPublisher(Application.Publisher.MONITORAGGIO_DATE_VERS_RICERCA);
    }

    @Override
    public void callRiArk() throws EMFError {
	// Controllo per sicurezza che lo stato sia corretto
	if (getForm().getDateVersamentoDetail().getTi_stato_dt_vers().parse()
		.equals(JobConstants.ArkStatusEnum.ARCHIVIATA_ERR.name())) {
	    SimpleDateFormat requestDateFormat = new SimpleDateFormat("ddMMyyyy");
	    String dateString = requestDateFormat
		    .format(getForm().getDateVersamentoDetail().getDt_vers().parse());
	    log.info("Chiamo il servizio di ri archiviazione per la data " + dateString);
	    String tipoRiArk;
	    if (getForm().getDateVersamentoDetail().getFl_file_no_ark().parse()
		    .equals(JobConstants.DB_TRUE)
		    && getForm().getDateVersamentoDetail().getFl_file_no_ark_secondario().parse()
			    .equals(JobConstants.DB_TRUE)) {
		tipoRiArk = RichiestaTpi.TipoRiArk.ENTRAMBI.name();
	    } else if (getForm().getDateVersamentoDetail().getFl_file_no_ark().parse()
		    .equals(JobConstants.DB_TRUE)) {
		tipoRiArk = RichiestaTpi.TipoRiArk.LOCALE.name();
	    } else {
		tipoRiArk = RichiestaTpi.TipoRiArk.SECONDARIO.name();
	    }
	    try {
		Map<String, String> params = jobHelper.getParamMap();
		String urlRequest = params.get(CostantiDB.ParametroAppl.TPI_TPI_HOST_URL)
			+ params.get(CostantiDB.ParametroAppl.TPI_URL_REGISTRACARTELLARIARK);
		Integer timeout = Integer
			.parseInt(params.get(CostantiDB.ParametroAppl.TPI_TIMEOUT));

		RichiestaTpiInput inputParams = new RichiestaTpiInput(
			RichiestaTpi.TipoRichiesta.REGISTRA_CARTELLA_RI_ARK, urlRequest, timeout,
			new BasicNameValuePair(RichiestaTpi.NM_USER,
				params.get(CostantiDB.ParametroAppl.TPI_NM_USER_TPI)),
			new BasicNameValuePair(RichiestaTpi.CD_PSW,
				params.get(CostantiDB.ParametroAppl.TPI_CD_PSW_TPI)),
			new BasicNameValuePair(RichiestaTpi.FL_CARTELLA_MIGRAZ,
				String.valueOf(getForm().getDateVersamentoDetail().getFl_migraz()
					.parse().equals(JobConstants.DB_TRUE))),
			new BasicNameValuePair(RichiestaTpi.DT_VERS, dateString),
			new BasicNameValuePair(RichiestaTpi.TI_RI_ARK, tipoRiArk));

		EsitoConnessione esitoConn = RichiestaWSTpi.callWs(inputParams);
		String codiceErrore = esitoConn.getCodiceErrore();
		String codiceEsito = esitoConn.getCodiceEsito();
		String messaggioErrore = esitoConn.getMessaggioErrore();

		if (esitoConn.isErroreConnessione()) {
		    log.error("Servizio RiArkCartella --- " + esitoConn.getDescrErrConnessione());
		    // Il servizio non ha risposto per un errore di connessione
		    // Registro l'errore
		    getMessageBox().addError("Il servizio RegistraCartellaRiArk non risponde");
		}
		if (!getMessageBox().hasError()) {
		    if (codiceEsito.equals(EsitoConnessione.Esito.KO.name())) {
			log.error("Servizio RiArkCartella --- " + codiceErrore + " - "
				+ messaggioErrore);
			// se il risultato è stato inaspettatamente NEGATIVO registro la sessione
			// con stato di errore e
			// chiudo il job
			getMessageBox().addError(
				"Il servizio RegistraCartellaRiArk ha restituito l'errore: "
					+ codiceErrore + " - " + messaggioErrore);
		    } else {
			// risultato OK! Non verifico nemmeno la risposta in quanto non contiene
			// nessun dato utile
			log.debug("Servizio RiArkCartella OK");
			BigDecimal idDtVers = getForm().getDateVersamentoDetail().getId_dt_vers()
				.parse();
			monitoraggioTpiHelper.setDataVersamentoArkStatus(idDtVers,
				JobConstants.ArkStatusEnum.DA_RI_ARCHIVIARE.name());
			getForm().getDateVersamentoDetail().getTi_stato_dt_vers()
				.setValue(JobConstants.ArkStatusEnum.DA_RI_ARCHIVIARE.name());
			getMessageBox()
				.addInfo("Richiesta di riarchiviazione eseguita con successo");

			getForm().getDateVersamentoDetail().getCallRiArk().setViewMode();
		    }
		}
	    } catch (ParerInternalError ex) {
		getMessageBox().addError(
			"Impossibile ottenere i parametri di richiesta riarchiviazione - Richiesta non eseguibile");
	    }
	} else {
	    getMessageBox().addError(
		    "Impossibile eseguire la richiesta di riarchiviazione - Data con stato diverso da ARCHIVIATA_ERR");
	}
	forwardToPublisher(getLastPublisher());
    }

    /*
     * GESTIONE SCHEDULAZIONI JOB TPI
     */
    @Secure(action = "Menu.Monitoraggio.DateSchedJobTpi")
    public void loadRicercaDateSchedJobTpi() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Monitoraggio.DateSchedJobTpi");

	getForm().getFiltriRicercaDateSchedJobTpi().reset();
	getForm().getFiltriRicercaDateSchedJobTpi().getTi_stato_dt_sched().setDecodeMap(ComboGetter
		.getMappaSortedGenericEnum("statoDtSched", JobConstants.StatoSchedJob.values()));
	getForm().getFiltriRicercaDateSchedJobTpi().getFl_anomalia()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

	getForm().getFiltriRicercaDateSchedJobTpi().setEditMode();

	getForm().getDateSchedJobTpiList().setTable(new BaseTable());

	forwardToPublisher(Application.Publisher.MONITORAGGIO_DATE_SCHED_JOB_TPI_RICERCA);
    }

    @Override
    public void ricercaDateSchedJobTpi() throws EMFError {
	MonitoraggioTpiForm.FiltriRicercaDateSchedJobTpi filtri = getForm()
		.getFiltriRicercaDateSchedJobTpi();
	if (filtri.postAndValidate(getRequest(), getMessageBox())) {
	    Date dataDa = filtri.getDt_sched_da().parse();
	    Date dataA = filtri.getDt_sched_a().parse();

	    if (dataDa == null && dataA == null) {
		log.debug("Date nulle - almeno una deve essere valorizzata");
		getMessageBox().addError("Selezionare almeno una data di schedulazione");
	    } else if (dataDa != null && dataA != null && dataA.before(dataDa)) {
		log.debug(filtri.getDt_sched_da().getHtmlDescription() + " superiore a "
			+ filtri.getDt_sched_a().getHtmlDescription());
		getMessageBox().addError(filtri.getDt_sched_da().getHtmlDescription()
			+ " superiore a " + filtri.getDt_sched_a().getHtmlDescription());
	    }

	    if (!getMessageBox().hasError()) {
		// La validazione non ha riportato errori. Carico la tabella con i filtri impostati
		getForm().getDateSchedJobTpiList()
			.setTable(monitoraggioTpiHelper.getDateSchedulazioniTpi(dataDa, dataA,
				filtri.getFl_anomalia().parse(),
				filtri.getTi_stato_dt_sched().parse()));
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getDateSchedJobTpiList().getTable().first();
		getForm().getDateSchedJobTpiList().getTable().setPageSize(10);
	    }
	}
	forwardToPublisher(Application.Publisher.MONITORAGGIO_DATE_SCHED_JOB_TPI_RICERCA);
    }

    public void showErrArkVersatiPrimarioList() throws EMFError {
	showErrArkList(getForm().getJobFileVersatiPrimarioList().getName());
    }

    public void showErrArkVersatiSecondarioList() throws EMFError {
	showErrArkList(getForm().getJobFileVersatiSecondarioList().getName());
    }

    public void showErrArkMigratiPrimarioList() throws EMFError {
	showErrArkList(getForm().getJobFileMigratiPrimarioList().getName());
    }

    public void showErrArkMigratiSecondarioList() throws EMFError {
	showErrArkList(getForm().getJobFileMigratiSecondarioList().getName());
    }

    public void showPathElabVersatiPrimarioList() throws EMFError {
	showPathElabList(getForm().getJobFileVersatiPrimarioList().getName());
    }

    public void showPathElabVersatiSecondarioList() throws EMFError {
	showPathElabList(getForm().getJobFileVersatiSecondarioList().getName());
    }

    public void showPathElabMigratiPrimarioList() throws EMFError {
	showPathElabList(getForm().getJobFileMigratiPrimarioList().getName());
    }

    public void showPathElabMigratiSecondarioList() throws EMFError {
	showPathElabList(getForm().getJobFileMigratiSecondarioList().getName());
    }

    public void showErrArkList(String table) throws EMFError {
	setTableName(table);
	setRiga(getRequest().getParameter("riga"));

	TpiSchedJobRowBean row = (TpiSchedJobRowBean) ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) getForm()
		.getComponent(getTableName())).getTable().getRow(Integer.parseInt(getRiga()));
	if (row.getNmJob().startsWith("BACKUP")) {
	    getRequest().setAttribute("tsm_operation", "backup");
	} else if (row.getNmJob().startsWith("MIGRATE")) {
	    getRequest().setAttribute("tsm_operation", "migrate");
	} else {
	    getRequest().setAttribute("tsm_operation", "archiviazione");
	}

	getForm().getJobSchedDetail().copyFromBean(row);
	getForm().getDateSchedJobTpiDetailErrArkList()
		.setTable(monitoraggioTpiHelper.getErrArkJobList(row.getIdSchedJob()));
	getForm().getDateSchedJobTpiDetailErrArkList().getTable().first();
	getForm().getDateSchedJobTpiDetailErrArkList().getTable().setPageSize(10);

	forwardToPublisher(Application.Publisher.MONITORAGGIO_ERR_ARK_LIST);

    }

    public void showPathElabList(String table) throws EMFError {
	setTableName(table);
	setRiga(getRequest().getParameter("riga"));

	TpiSchedJobRowBean row = (TpiSchedJobRowBean) ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) getForm()
		.getComponent(getTableName())).getTable().getRow(Integer.parseInt(getRiga()));

	getForm().getJobSchedDetail().copyFromBean(row);
	getForm().getPathElabJobTpiDetailList()
		.setTable(monitoraggioTpiHelper.getPathElabJobList(row.getIdSchedJob()));
	getForm().getPathElabJobTpiDetailList().getTable().first();
	getForm().getPathElabJobTpiDetailList().getTable().setPageSize(10);

	forwardToPublisher(Application.Publisher.MONITORAGGIO_PATH_ELAB_LIST);
    }

}
