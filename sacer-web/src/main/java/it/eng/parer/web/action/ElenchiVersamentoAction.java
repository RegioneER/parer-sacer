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

import it.eng.hsm.beans.HSMUser;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.common.signature.Signature;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.FileTypeEnum;
import it.eng.parer.entity.constraint.ElvElencoVer;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.ejb.ElencoIndiciAipSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.ElencoSignatureSessionEjb;
import it.eng.parer.firma.crypto.sign.SignerHsmEjb;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.firma.crypto.sign.SigningResponse;
import static it.eng.parer.firma.crypto.sign.SigningResponse.ERROR_COMPLETAMENTO_FIRMA;
import static it.eng.parer.firma.crypto.sign.SigningResponse.OK;
import static it.eng.parer.firma.crypto.sign.SigningResponse.OK_SECONDA_FASE;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ElenchiVersamentoAbstractAction;
import it.eng.parer.slite.gen.form.ComponentiForm;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm.FiltriElenchiDaFirmare;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.*;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.*;
import it.eng.parer.web.util.ActionEnums;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.ComboUtil;
import it.eng.parer.web.validator.ElenchiVersamentoValidator;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.web.validator.VolumiValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.SuppressLogging;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Gilioli_P
 */
public class ElenchiVersamentoAction extends ElenchiVersamentoAbstractAction {

    private static Logger log = LoggerFactory.getLogger(ElenchiVersamentoAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ComponentiHelper")
    private ComponentiHelper componentiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/AggiornamentiHelper")
    private AggiornamentiHelper aggiornamentiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SignerHsmEjb")
    private SignerHsmEjb firmaHsmEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ElencoSignatureSessionEjb")
    private ElencoSignatureSessionEjb elencoSignSessionEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ElencoIndiciAipSignatureSessionEjb")
    private ElencoIndiciAipSignatureSessionEjb elencoIndiciAipSignSessionEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggruppamentoEjb")
    private CriteriRaggruppamentoEjb criteriRaggruppamentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    /* Getter di valori utilizzati all'interno della action */
    private BigDecimal getIdStrutCorrente() {
	return getUser().getIdOrganizzazioneFoglia();
    }

    private long getIdUtenteCorrente() {
	return getUser().getIdUtente();
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.ELENCHI_VERSAMENTO_RICERCA;
    }

    @Override
    public String getControllerName() {
	return Application.Actions.ELENCHI_VERSAMENTO;
    }

    /* METODI DI INIZIALIZZAZIONE */
    @Override
    public void initOnClick() throws EMFError {
    }

    private void initComboFiltriComponenti(BigDecimal idStrut) {
	if (idStrut == null) {
	    idStrut = getIdStrutCorrente();
	}
	getForm().getComponentiFiltri().reset();
	// Imposto i valori della combo TIPO STRUTTURA DOCUMENTO ricavati dalla tabella
	// DEC_TIPO_STRUT_DOC
	DecTipoStrutDocTableBean tmpTableBeanTipoStrutDoc = tipoStrutDocEjb
		.getDecTipoStrutDocTableBean(idStrut, false);
	DecodeMap mappaTipoStrutDoc = new DecodeMap();
	mappaTipoStrutDoc.populatedMap(tmpTableBeanTipoStrutDoc, "id_tipo_strut_doc",
		"nm_tipo_strut_doc");

	// Imposto i valori della combo FORMATO_FILE_DOC ricavati dalla tabella DEC_FORMATO_FILE_DOC
	DecFormatoFileDocTableBean tmpTableBeanFormatoFileDoc = formatoFileDocEjb
		.getDecFormatoFileDocTableBean(idStrut);
	DecodeMap mappaFormatoFileDoc = new DecodeMap();
	mappaFormatoFileDoc.populatedMap(tmpTableBeanFormatoFileDoc, "nm_formato_file_doc",
		"nm_formato_file_doc");

	// Setto i valori della combo TIPO REGISTRO ricavati dalla tabella DEC_REGISTRO_UNITA_DOC
	DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
		.getRegistriUnitaDocAbilitati(getIdUtenteCorrente(), idStrut);
	DecodeMap mappaRegistro = new DecodeMap();
	mappaRegistro.populatedMap(tmpTableBeanReg, "cd_registro_unita_doc",
		"cd_registro_unita_doc");

	// Imposto le varie combo dei FILTRI di ricerca Componenti
	getForm().getComponentiFiltri().getNm_tipo_strut_doc().setDecodeMap(mappaTipoStrutDoc);
	getForm().getComponentiFiltri().getNm_formato_file_vers().setDecodeMap(mappaFormatoFileDoc);
	getForm().getComponentiFiltri().getTi_esito_contr_conforme().setDecodeMap(ComboGetter
		.getMappaSortedGenericEnum("stato", VolumeEnums.ControlloConformitaEnum.values()));
	getForm().getComponentiFiltri().getTi_esito_verif_firme_vers()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme",
			VolumeEnums.StatoVerifica.values()));
	getForm().getComponentiFiltri().getFl_comp_firmato()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getComponentiFiltri().getCd_registro_key_unita_doc().setDecodeMap(mappaRegistro);
	getForm().getComponentiFiltri().getFl_forza_accettazione()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getComponentiFiltri().getFl_forza_conservazione()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getComponentiFiltri().getDs_algo_hash_file_calc()
		.setDecodeMap(ComboGetter.getMappaHashAlgorithm());
	getForm().getComponentiFiltri().getCd_encoding_hash_file_calc()
		.setDecodeMap(ComboGetter.getMappaHashEncoding());
	getForm().getComponentiFiltri().getTi_esito_contr_formato_file()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_formato_vers",
			VolumeEnums.StatoFormatoVersamento.values()));
    }

    private void initBottoniPaginaDettaglioElencoVersamento(BigDecimal idElencoVers,
	    String tiStatoElenco) {
	getForm().getDettaglioElenchiVersamentoButtonList().setViewMode();
	getForm().getDettaglioElenchiVersamentoButtonList().getListaOperazioniElencoButton()
		.setHidden(false);
	getForm().getDettaglioElenchiVersamentoButtonList().getListaOperazioniElencoButton()
		.setEditMode();
	getForm().getElenchiVersamentoList().setUserOperations(true, true, false, true);
	getForm().getListaComponentiButtonList().setViewMode();
	/* Se ud e doc sono eliminabili dall'elenco di versamento, mostro il bottone */
	if (evEjb.areUdDocDeletables(idElencoVers)) {
	    getForm().getListaComponentiButtonList().getEliminaAppartenenzaUdDocDaElenco()
		    .setHidden(false);
	    getForm().getListaComponentiButtonList().getEliminaAppartenenzaUdDocDaElenco()
		    .setEditMode();
	}
	/* Se upd sono eliminabili dall'elenco di versamento, mostro il bottone */
	if (evEjb.areUpdDeletables(idElencoVers)) {
	    getForm().getListaAggiornamentiButtonList().getEliminaAppartenenzaUpdDaElenco()
		    .setHidden(false);
	    getForm().getListaAggiornamentiButtonList().getEliminaAppartenenzaUpdDaElenco()
		    .setEditMode();
	}
	/* Se l'elenco è chiudibile, mostro il bottone */
	if (evEjb.isElencoClosable(idElencoVers)) {
	    getForm().getDettaglioElenchiVersamentoButtonList().getChiudiElencoButton()
		    .setHidden(false);
	    getForm().getDettaglioElenchiVersamentoButtonList().getChiudiElencoButton()
		    .setEditMode();
	}
	if (tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.CHIUSO.name())
		|| tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.FIRMA_IN_CORSO.name())
		|| tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.VALIDATO.name())
		|| tiStatoElenco
			.equals(ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS.name())
		|| tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP.name())
		|| tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name())
		|| tiStatoElenco
			.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name())
		|| tiStatoElenco.equals(
			ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name())
		|| tiStatoElenco
			.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name())
		|| tiStatoElenco
			.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name())
		|| tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())) {

	    // MEV #24534 se non esiste l'indice di versamento visualizza il pulsante "Genera"
	    // altrimenti "Scarica"
	    getForm().getDettaglioElenchiVersamentoButtonList().getScaricaIndiceElencoButton()
		    .setEditMode();
	    getForm().getDettaglioElenchiVersamentoButtonList().getGeneraIndiceElencoButton()
		    .setEditMode();
	    getForm().getDettaglioElenchiVersamentoButtonList().getGeneraIndiceElencoButton()
		    .setDisableHourGlass(true);
	    getForm().getDettaglioElenchiVersamentoButtonList().getScaricaIndiceElencoButton()
		    .setDisableHourGlass(true);

	    boolean esisteIndice = esisteIndiceVersamento(idElencoVers);
	    getForm().getDettaglioElenchiVersamentoButtonList().getScaricaIndiceElencoButton()
		    .setHidden(!esisteIndice);
	    getForm().getDettaglioElenchiVersamentoButtonList().getGeneraIndiceElencoButton()
		    .setHidden(esisteIndice);
	}

	if (tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name())
		|| tiStatoElenco.equals(
			ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name())
		|| tiStatoElenco
			.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name())
		|| tiStatoElenco
			.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name())
		|| (tiStatoElenco.equals(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())
			&& evEjb.retrieveFileIndiceElenco(idElencoVers.longValue(),
				ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name()) != null)) {
	    getForm().getDettaglioElenchiVersamentoButtonList().getScaricaElencoIndiciAipButton()
		    .setEditMode();
	    getForm().getDettaglioElenchiVersamentoButtonList().getScaricaElencoIndiciAipButton()
		    .setDisableHourGlass(true);
	}

	// MEV#32249 - Funzione per riportare indietro lo stato di un elenco per consentire la firma
	// dell'AIP
	if (evEjb.isPossibileMettereAipAllaFirma(idElencoVers)) {
	    getForm().getDettaglioElenchiVersamentoButtonList().getRiportaStatoIndietroButton()
		    .setEditMode();
	    getForm().getDettaglioElenchiVersamentoButtonList().getRiportaStatoIndietroButton()
		    .setHidden(false);
	} else {
	    getForm().getDettaglioElenchiVersamentoButtonList().getRiportaStatoIndietroButton()
		    .setViewMode();
	    getForm().getDettaglioElenchiVersamentoButtonList().getRiportaStatoIndietroButton()
		    .setHidden(true);
	}
	// ---------------------------------------------------------------------------------------------------
    }

    private boolean esisteIndiceVersamento(BigDecimal idElencoVers) {
	return evEjb.retrieveFileIndiceElenco(idElencoVers.longValue(),
		ElencoEnums.FileTypeEnum.INDICE.name()) != null
		|| evEjb.isIndiceElencoVersOnOs(idElencoVers.longValue());
    }

    ////////////
    /* PAGINE */
    ////////////
    @Secure(action = "Menu.ElenchiVersamento.ListaElenchiVersamentoDaFirmare")
    public void loadListaElenchiVersamentoDaFirmare() throws EMFError {
	/*
	 * Controllo lo stato della history di navigazione se non ci sono pagine precedenti, vuol
	 * dire che arrivo qui da un link del menu, se ci sono pagine allora devo passare alla jsp
	 * l'id della struttura
	 */
	boolean cleanList = false;
	if (getRequest().getParameter("cleanhistory") != null) {
	    getUser().getMenu().reset();
	    getUser().getMenu().select("Menu.ElenchiVersamento.ListaElenchiVersamentoDaFirmare");
	    // Rimuovo l'attributo perchè arrivo da un link del menu e non da una lista
	    getSession().removeAttribute("idStrutRif");
	    getSession().removeAttribute("isStrutNull");
	    cleanList = true;
	}

	/* Ricavo Ambiente, Ente e Struttura da visualizzare */
	BigDecimal idStrut;
	if (getRequest().getParameter("idStrut") != null) {
	    idStrut = new BigDecimal(getRequest().getParameter("idStrut"));
	    cleanList = true;
	} else if (getSession().getAttribute("idStrutRif") != null) {
	    idStrut = (BigDecimal) getSession().getAttribute("idStrutRif");
	} else if (getSession().getAttribute("isStrutNull") != null) {
	    idStrut = null;
	} else {
	    idStrut = getIdStrutCorrente();
	    cleanList = true;
	}

	boolean cleanFilter = true;
	if (getRequest().getParameter("cleanFilter") != null) {
	    cleanFilter = false;
	}

	if (idStrut != null && cleanFilter) {
	    OrgStrutRowBean strut = evEjb.getOrgStrutRowBeanWithAmbienteEnte(idStrut);
	    /* Inizializza le combo dei filtri ambiente/ente/struttura */
	    initFiltriElenchiDaFirmare(idStrut);

	    if (cleanList) {
		ElenchiVersamentoValidator elenchiValidator = new ElenchiVersamentoValidator(
			getMessageBox());
		elenchiValidator.validaTipoValidazione(
			getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().parse());
		// Valido i filtri data creazione elenco da - a restituendo le date comprensive di
		// orario
		Date[] dateCreazioneElencoValidate = elenchiValidator.validaDate(
			getForm().getFiltriElenchiDaFirmare().getDt_creazione_elenco_da().parse(),
			getForm().getFiltriElenchiDaFirmare().getOre_dt_creazione_elenco_da()
				.parse(),
			getForm().getFiltriElenchiDaFirmare().getMinuti_dt_creazione_elenco_da()
				.parse(),
			getForm().getFiltriElenchiDaFirmare().getDt_creazione_elenco_a().parse(),
			getForm().getFiltriElenchiDaFirmare().getOre_dt_creazione_elenco_a()
				.parse(),
			getForm().getFiltriElenchiDaFirmare().getMinuti_dt_creazione_elenco_a()
				.parse(),
			getForm().getFiltriElenchiDaFirmare().getDt_creazione_elenco_da()
				.getHtmlDescription(),
			getForm().getFiltriElenchiDaFirmare().getDt_creazione_elenco_a()
				.getHtmlDescription());
		if (!getMessageBox().hasError()) {
		    /*
		     * Carico la lista degli elenchi di versamento da firmare: quelli della
		     * struttura dell'utente e con stato CHIUSO
		     */
		    ArrayList<String> al = new ArrayList();
		    String appoggio = getForm().getFiltriElenchiDaFirmare().getTi_gest_elenco()
			    .parse();
		    if (appoggio != null) {
			al.add(appoggio);
		    }
		    ElvVLisElencoVersStatoTableBean elenchiTableBean = evEjb
			    .getElenchiDaFirmareTableBean(strut.getBigDecimal("id_ambiente"),
				    strut.getIdEnte(), strut.getIdStrut(),
				    getForm().getFiltriElenchiDaFirmare().getId_elenco_vers()
					    .parse(),
				    getForm().getFiltriElenchiDaFirmare().getElenchi_con_note()
					    .parse(),
				    getForm().getFiltriElenchiDaFirmare().getFl_elenco_fisc()
					    .parse(),
				    al, dateCreazioneElencoValidate, getUser().getIdUtente(),
				    ElencoEnums.ElencoStatusEnum.CHIUSO);
		    /*
		     * Filtro gli elenchi mediante tipo validazione = FIRMA/NO_FIRMA e modalità di
		     * validazione = MANUALE
		     */
		    Iterator<ElvVLisElencoVersStatoRowBean> iterator = elenchiTableBean.iterator();
		    while (iterator.hasNext()) {
			ElvVLisElencoVersStatoRowBean elencoRowBean = iterator.next();
			String tiValidElenco = elencoRowBean.getTiValidElenco();
			String tiModValidElenco = elencoRowBean.getTiModValidElenco();
			if (!getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().parse()
				.contains(tiValidElenco)
				|| (getForm().getFiltriElenchiDaFirmare().getTi_mod_valid_elenco()
					.parse() != null
					&& !getForm().getFiltriElenchiDaFirmare()
						.getTi_mod_valid_elenco().parse()
						.equals(tiModValidElenco))) {
			    iterator.remove();
			}
		    }
		    getForm().getElenchiVersamentoDaFirmareList().setTable(elenchiTableBean);
		    getForm().getElenchiVersamentoDaFirmareList().getTable().setPageSize(10);
		    getForm().getElenchiVersamentoDaFirmareList().getTable().first();
		    getForm().getElenchiVersamentoDaFirmareList().getTable()
			    .addSortingRule(getForm().getElenchiVersamentoDaFirmareList()
				    .getDt_creazione_elenco().getName(), SortingRule.ASC);

		    /* Inizializzo la lista degli elenchi di versamento selezionati */
		    getForm().getElenchiVersamentoSelezionatiList()
			    .setTable(new ElvVLisElencoVersStatoTableBean());
		    getForm().getElenchiVersamentoSelezionatiList().getTable().setPageSize(10);
		    getForm().getElenchiVersamentoSelezionatiList().getTable()
			    .addSortingRule(getForm().getElenchiVersamentoSelezionatiList()
				    .getDt_creazione_elenco().getName(), SortingRule.ASC);
		}
	    }
	}

	// Check if some signature session is active
	Future<Boolean> futureFirma = (Future<Boolean>) getSession()
		.getAttribute(Signature.FUTURE_ATTR_ELENCHI);
	/* Rendo visibili i bottoni delle operazioni sulla lista che mi interessano */
	getForm().getListaElenchiVersamentoDaFirmareButtonList().setEditMode();

	// MEV#31945 - Eliminare validazione elenco UD con firma
	//
	// Verifico su db la presenza della sessione di firma o di un oggetto future (di una
	// possibile sessione di firma
	// preesistente) in sessione
	// if (elencoSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente()) || futureFirma !=
	// null) {
	// // Se esistono delle sessioni bloccate per quell'utente le sblocco
	// if (elencoSignSessionEjb.hasUserBlockedSessions(getUser().getIdUtente())) {
	// // Sessione di firma bloccata
	// elencoSignSessionEjb.unlockBlockedSessions(getUser().getIdUtente());
	//
	// getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton().setReadonly(false);
	// getMessageBox().addInfo("\u00C8 stata sbloccata una sessione di firma bloccata");
	// getMessageBox().setViewMode(ViewMode.plain);
	// } else {
	// getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton().setReadonly(true);
	// // Sessione di firma attiva
	// getMessageBox().addInfo("Sessione di firma attiva");
	// getMessageBox().setViewMode(ViewMode.plain);
	// }
	// } else {
	// getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton().setReadonly(false);
	// }

	if (getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().parse()
		.equals(ElvElencoVer.TiValidElenco.FIRMA.name())) {
	    // MEV#31945 - Eliminare validazione elenco UD con firma
	    // getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton().setHidden(false);
	    getForm().getListaElenchiVersamentoDaFirmareButtonList().getValidaElenchiButton()
		    .setHidden(true);
	} else {
	    /* NO_FIRMA */
	    // MEV#31945 - Eliminare validazione elenco UD con firma
	    // getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton().setHidden(true);
	    getForm().getListaElenchiVersamentoDaFirmareButtonList().getValidaElenchiButton()
		    .setHidden(false);
	}
	getForm().getElenchiVersamentoList().setUserOperations(true, false, false, false);

	getSession().setAttribute("idStrutRif", idStrut);

	Button validaBtn = getForm().getListaElenchiVersamentoDaFirmareButtonList()
		.getValidaElenchiButton();
	if (validaBtn.isHidden()) {
	    getSession().setAttribute("tiValidElenco", ElvElencoVer.TiValidElenco.FIRMA.name());
	} else {
	    getSession().setAttribute("tiValidElenco", ElvElencoVer.TiValidElenco.NO_FIRMA.name());
	}

	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    }

    /**
     * Carica la pagina di "Ricerca elenchi di versamento"
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.ElenchiVersamento.RicercaElenchiVersamento")
    public void ricercaElenchiVersamento() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.ElenchiVersamento.RicercaElenchiVersamento");
	/* Azzero i filtri e la lista risultato della form di ricerca */
	getForm().getFiltriElenchiVersamento().reset();
	getForm().getComponentiFiltri().reset();
	getForm().getElenchiVersamentoList().clear();
	getForm().getElenchiVersamentoList().setUserOperations(true, true, false, true);
	/* Inizializzo le combo di ricerca */
	initComboRicercaElenchi();
	initComboFiltriComponenti(null);
	/* Imposto tutti i filtri in edit mode */
	getForm().getFiltriElenchiVersamento().setEditMode();
	getForm().getComponentiFiltri().setEditMode();
	/* Carico la pagina di ricerca */
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA);
    }

    /**
     * Creo le mappe coi valori e setto le combo presenti nella pagina di ricerca elenchi
     *
     * @throws EMFError errore generico
     */
    private void initComboRicercaElenchi() throws EMFError {
	// Ricavo id struttura, ente ed ambiente attuali
	BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
	BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
	BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

	// Inizializzo le combo settando la struttura corrente
	OrgAmbienteTableBean tmpTableBeanAmbiente = null;
	OrgEnteTableBean tmpTableBeanEnte = null;
	OrgStrutTableBean tmpTableBeanStruttura = null;
	try {
	    // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
	    tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

	    // Ricavo i valori della combo ENTE
	    tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
		    idAmbiente.longValue(), Boolean.TRUE);

	    // Ricavo i valori della combo STRUTTURA
	    tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
		    idEnte, Boolean.TRUE);

	} catch (ParerUserError ex) {
	    log.error("Errore in ricerca ambiente", ex);
	}

	DecodeMap mappaAmbiente = new DecodeMap();
	mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
	getForm().getFiltriElenchiVersamento().getId_ambiente().setDecodeMap(mappaAmbiente);
	getForm().getFiltriElenchiVersamento().getId_ambiente().setValue(idAmbiente.toString());

	DecodeMap mappaEnte = new DecodeMap();
	mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
	getForm().getFiltriElenchiVersamento().getId_ente().setDecodeMap(mappaEnte);
	getForm().getFiltriElenchiVersamento().getId_ente().setValue(idEnte.toString());

	DecodeMap mappaStrut = new DecodeMap();
	mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
	getForm().getFiltriElenchiVersamento().getId_strut().setDecodeMap(mappaStrut);
	getForm().getFiltriElenchiVersamento().getId_strut().setValue(idStrut.toString());

	// Imposto le varie combo/multiselect dei FILTRI di ricerca Elenchi di Versamento
	getForm().getFiltriElenchiVersamento().getTi_stato_elenco()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_elenco",
			ElencoEnums.ElencoStatusEnum.getComboMappaStatoElencoRicerca()));

	getForm().getFiltriElenchiVersamento().getTi_valid_elenco()
		.setDecodeMap(ComboGetter.getMappaTiValidElenco());
	getForm().getFiltriElenchiVersamento().getTi_mod_valid_elenco()
		.setDecodeMap(ComboGetter.getMappaTiModValidElenco());

	getForm().getFiltriElenchiVersamento().getTi_stato_conservazione()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_conservazione",
			CostantiDB.StatoConservazioneUnitaDocNonAnnullata.values()));

	boolean flSigilloAttivo = Boolean
		.parseBoolean(configurationHelper.getValoreParamApplicByAmb(
			CostantiDB.ParametroAppl.FL_ABILITA_SIGILLO, idAmbiente));
	getForm().getFiltriElenchiVersamento().getTi_gest_elenco()
		.setDecodeMap(ComboGetter.getMappaTiGestElencoCriterio(flSigilloAttivo));
	getForm().getFiltriElenchiVersamento().getFl_elenco_standard()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltriElenchiVersamento().getFl_elenco_firmato()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltriElenchiVersamento().getFl_elenco_fisc()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltriElenchiVersamento().getFl_elenco_indici_aip_creato()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

	// Imposto le combo Criteri Raggruppamento e Registro unità documentaria
	checkUniqueStrutInCombo(idStrut, ActionEnums.SezioneElenchi.RICERCA_ELENCHI);
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo struttura quando questo è l'unico
     * presente e settare di conseguenza le combo Registro unità documentaria e Criterio di
     * Raggruppamento
     *
     * @param idStrut id struttura
     * @param sezione enumerativo
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueStrutInCombo(BigDecimal idStrut, Enum sezione) throws EMFError {
	if (idStrut != null) {
	    // Imposto i valori della mappa CRITERIO_RAGGRUPPAMENTO ricavati dalla tabella
	    // DEC_CRITERIO_RAGGR
	    DecCriterioRaggrTableBean tmpTableBeanCriteri = criteriRaggruppamentoEjb
		    .getDecCriterioRaggrTableBean(null, null, idStrut, null);
	    DecodeMap mappaCriteri = new DecodeMap();
	    mappaCriteri.populatedMap(tmpTableBeanCriteri, "nm_criterio_raggr",
		    "nm_criterio_raggr");

	    // Setto i valori della mappa TIPO REGISTRO ricavati dalla tabella
	    // DEC_REGISTRO_UNITA_DOC
	    DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
		    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
	    DecodeMap mappaRegistro = new DecodeMap();
	    mappaRegistro.populatedMap(tmpTableBeanReg, "cd_registro_unita_doc",
		    "cd_registro_unita_doc");

	    if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		getForm().getFiltriElenchiVersamento().getNm_criterio_raggr()
			.setDecodeMap(mappaCriteri);
		getForm().getFiltriElenchiVersamento().getCd_registro_key_unita_doc()
			.setDecodeMap(mappaRegistro);
	    } else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		log.warn("Funzionalità non implementata per la sezione {}",
			ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	    }
	}
    }

    /* CARICA DETTAGLIO */
    @Override
    public void loadDettaglio() throws EMFError {
	/* Tabella considerata */
	String lista = getTableName();
	/* Azione considerata */
	String azione = getNavigationEvent();

	/*
	 * Il caricamento del dettaglio va effettuato in tutti i casi TRANNE che in fase di
	 * inserimento
	 */
	if (lista != null && azione != null && (azione.equals(ListAction.NE_DETTAGLIO_VIEW)
		|| azione.equals(ListAction.NE_DETTAGLIO_UPDATE)
		|| azione.equals(ListAction.NE_NEXT) || azione.equals(ListAction.NE_PREV))) {
	    /* Se ho cliccato sul DETTAGLIO della LISTA ELENCHI DI VERSAMENTO */
	    if (lista.equals(getForm().getElenchiVersamentoList().getName())) {
		BigDecimal idElencoVers = getForm().getElenchiVersamentoList().getTable()
			.getCurrentRow()
			.getBigDecimal(ElvVRicElencoVersTableDescriptor.COL_ID_ELENCO_VERS);
		dettaglioElencoVersamento(idElencoVers);
	    } else if (lista.equals(getForm().getElenchiVersamentoDaFirmareList().getName())) {
		BigDecimal idElencoVers = getForm().getElenchiVersamentoDaFirmareList().getTable()
			.getCurrentRow()
			.getBigDecimal(ElvVLisElencoVersStatoTableDescriptor.COL_ID_ELENCO_VERS);
		dettaglioElencoVersamento(idElencoVers);
	    } else if (lista.equals(getForm().getElenchiIndiciAipDaFirmareList().getName())) {
		BigDecimal idElencoVers = getForm().getElenchiIndiciAipDaFirmareList().getTable()
			.getCurrentRow()
			.getBigDecimal(ElvVLisElencoVersStatoTableDescriptor.COL_ID_ELENCO_VERS);
		dettaglioElencoVersamento(idElencoVers);
	    } else if (lista.equals(getForm().getElenchiVersamentoSelezionatiList().getName())) {
		BigDecimal idElencoVers = getForm().getElenchiVersamentoSelezionatiList().getTable()
			.getCurrentRow()
			.getBigDecimal(ElvVLisElencoVersStatoTableDescriptor.COL_ID_ELENCO_VERS);
		dettaglioElencoVersamento(idElencoVers);
	    } else if (lista.equals(getForm().getElenchiIndiciAipSelezionatiList().getName())) {
		BigDecimal idElencoVers = getForm().getElenchiIndiciAipSelezionatiList().getTable()
			.getCurrentRow()
			.getBigDecimal(ElvVLisElencoVersStatoTableDescriptor.COL_ID_ELENCO_VERS);
		dettaglioElencoVersamento(idElencoVers);
	    }
	}
    }

    public void dettaglioElencoVersamento(BigDecimal idElencoVers) throws EMFError {
	/* Carico i dati nel dettaglio nell'online */
	ElvElencoVerRowBean elencoVersRowBean = evEjb.getElvElencoVersRowBean(idElencoVers);
	getForm().getElenchiVersamentoDetail().getTi_gest_elenco()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_gest_elenco",
			ElencoEnums.GestioneElencoEnum.values()));
	getForm().getElenchiVersamentoDetail().getTi_valid_elenco().setDecodeMap(ComboGetter
		.getMappaSortedGenericEnum("ti_valid_elenco", ElvElencoVer.TiValidElenco.values()));
	getForm().getElenchiVersamentoDetail().getTi_mod_valid_elenco()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_mod_valid_elenco",
			ElvElencoVer.TiModValidElenco.values()));
	getForm().getElenchiVersamentoDetail().copyFromBean(elencoVersRowBean);

	/* Carico i bottoni della pagina di dettaglio elenco di versamento */
	initBottoniPaginaDettaglioElencoVersamento(elencoVersRowBean.getIdElencoVers(),
		elencoVersRowBean.getTiStatoElenco());

	/* Carico i valori dei filtri nel tab Filtri Componenti */
	initComboFiltriComponenti(elencoVersRowBean.getIdStrut());

	// MAC#35254 - Correzione delle anomalie nella fase di marcatura temporale embedded negli
	// elenchi indici aip UD
	// Se la data marca non è valorizzata la si fa scomparire del tutto!
	if (elencoVersRowBean.getDtMarcaElencoIxAip() == null) {
	    getForm().getElenchiVersamentoDetail().getDt_marca_elenco_ix_aip().setHidden(true);
	} else {
	    getForm().getElenchiVersamentoDetail().getDt_marca_elenco_ix_aip().setHidden(false);
	}

	/* Metto lista e dettaglio in viewMode e status view */
	getForm().getElenchiVersamentoDetail().setViewMode();
	getForm().getElenchiVersamentoList().setViewMode();
	getForm().getElenchiVersamentoDetail().setStatus(Status.view);
	getForm().getElenchiVersamentoList().setStatus(Status.view);
	getForm().getDettaglioElencoTabs()
		.setCurrentTab(getForm().getDettaglioElencoTabs().getDettaglioElencoTab());
	getForm().getUdVersateSection().setLoadOpened(true);
	getForm().getDocAggiuntiSection().setLoadOpened(true);
	getForm().getChiaveSection().setLoadOpened(true);

	/* Carico la lista componenti e la lista aggiornamenti relativi all'elenco di versamento */
	if ((getTableName() != null && (getTableName()
		.equals(getForm().getElenchiVersamentoList().getName())
		|| getTableName().equals(getForm().getElenchiVersamentoDaFirmareList().getName())
		|| getTableName().equals(getForm().getElenchiIndiciAipDaFirmareList().getName())))
		|| getTableName() == null) {
	    // Carico la lista degli Stati
	    ElvStatoElencoVerTableBean listaStati = evEjb
		    .getElvStatoElencoVersTableBean(idElencoVers);
	    getForm().getStatiElencoList().setTable(listaStati);
	    getForm().getStatiElencoList().getTable().setPageSize(10);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getStatiElencoList().getTable().first();
	    // Carico la lista dei Componenti
	    ElvVListaCompElvTableBean listaComp = componentiHelper
		    .getElvVListaCompElvViewBean(idElencoVers, elencoVersRowBean.getIdStrut());
	    getForm().getComponentiList().setTable(listaComp);
	    getForm().getComponentiList().getTable().setPageSize(10);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getComponentiList().getTable().first();
	    // Carico la lista degli Aggiornamenti
	    ElvVLisUpdUdTableBean listaUpd = aggiornamentiHelper.getElvVLisUpdUdViewBean(
		    idElencoVers, new ElenchiVersamentoForm.ComponentiFiltri());
	    getForm().getAggiornamentiList().setTable(listaUpd);
	    getForm().getAggiornamentiList().getTable().setPageSize(10);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getAggiornamentiList().getTable().first();
	}

	/* Posso selezionare i componenti e gli aggiornamenti in base allo stato dell'elenco */
	String statoElenco = getForm().getElenchiVersamentoDetail().getTi_stato_elenco().getValue();
	if ((getNavigationEvent() != null
		&& getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW))
		|| getLastPublisher().equals(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL)) {
	    if (statoElenco.equals(ElencoStatusEnum.APERTO.name())
		    || statoElenco.equals(ElencoStatusEnum.DA_CHIUDERE.name())) {
		/* Imposto la checkbox di "Selezione componenti da eliminare" visibile */
		getForm().getComponentiList().getSelect_comp().setHidden(false);
		getForm().getComponentiList().getSelect_comp().setEditMode();
		/* Imposto la checkbox di "Selezione componenti da eliminare" visibile */
		getForm().getAggiornamentiList().getSelect_upd().setHidden(false);
		getForm().getAggiornamentiList().getSelect_upd().setEditMode();
	    } else {
		getForm().getComponentiList().getSelect_comp().setHidden(true);
		getForm().getComponentiList().getSelect_comp().setViewMode();
		getForm().getAggiornamentiList().getSelect_upd().setHidden(true);
		getForm().getAggiornamentiList().getSelect_upd().setViewMode();
	    }
	}
	/*
	 * Inizializzo le liste fittizie nel caso si voglia visualizzare unità documentarie o
	 * documenti
	 */
	getForm().getUnitaDocumentarieList().setTable(new AroVRicUnitaDocTableBean());
	getForm().getUnitaDocumentarieUpdList().setTable(new AroVRicUnitaDocTableBean());
	getForm().getDocumentiList().setTable(new AroVVisDocIamTableBean());
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
	String lista = getTableName();
	String azione = getNavigationEvent();

	if (!azione.equals(ListAction.NE_DETTAGLIO_DELETE)) {
	    BigDecimal idStruttura = getForm().getElenchiVersamentoDetail().getId_strut().parse();
	    /* Ricavo i tipi dato cui l'utente è abilitato */
	    Set<Object> registriAbilitatiSet = getForm().getComponentiFiltri()
		    .getCd_registro_key_unita_doc().getDecodeMap().keySet();
	    Set<Object> tipiUdAbilitateSet = DecodeMap.Factory.newInstance(
		    tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getIdUtenteCorrente(), idStruttura),
		    "id_tipo_unita_doc", "nm_tipo_unita_doc").keySet();
	    Set<Object> tipiDocAbilitatiSet = DecodeMap.Factory.newInstance(
		    tipoDocumentoEjb.getTipiDocAbilitati(getIdUtenteCorrente(), idStruttura),
		    "id_tipo_doc", "nm_tipo_doc").keySet();
	    Set<Object> subStrutAbilitateSet = DecodeMap.Factory
		    .newInstance(subStrutEjb.getOrgSubStrutTableBeanAbilitate(getIdUtenteCorrente(),
			    idStruttura), "id_sub_strut", "nm_sub_strut")
		    .keySet();

	    /* Ho cliccato sulla lente per il dettaglio di Elenchi di Versamento */
	    if (lista.equals(getForm().getElenchiVersamentoList().getName())
		    || lista.equals(getForm().getElenchiVersamentoDaFirmareList().getName())
		    || lista.equals(getForm().getElenchiIndiciAipDaFirmareList().getName())
		    || lista.equals(getForm().getElenchiVersamentoSelezionatiList().getName())
		    || lista.equals(getForm().getElenchiIndiciAipSelezionatiList().getName())) {
		forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
	    } /* Dettaglio COMPONENTE */ else if (lista
		    .equals(getForm().getComponentiList().getName())) {
		BigDecimal idUnitaDoc = null;
		/* Verifico se l'utente può accedere al dettaglio componente */
		ElvVListaCompElvRowBean rigaComp = (ElvVListaCompElvRowBean) getForm()
			.getComponentiList().getTable().getCurrentRow();
		if (registriAbilitatiSet.contains(rigaComp.getCdRegistroKeyUnitaDoc())
			&& tipiUdAbilitateSet.contains(rigaComp.getIdTipoUnitaDoc())
			&& tipiDocAbilitatiSet.contains(rigaComp.getIdTipoDoc())
			&& subStrutAbilitateSet.contains(rigaComp.getIdSubStrut())) {
		    idUnitaDoc = rigaComp.getIdUnitaDoc();
		} else {
		    getMessageBox().addError(
			    "Utente non abilitato ad accedere al dettaglio del componente selezionato");
		    forwardToPublisher(getLastPublisher());
		}
		if (idUnitaDoc != null) {
		    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
			    .getAroVRicUnitaDocRowBean(idUnitaDoc, null, null);
		    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
			    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
			/* Preparo la LISTA COMPONENTI */
			ComponentiForm form = new ComponentiForm();
			form.getComponentiList().setTable(getForm().getComponentiList().getTable());
			redirectToAction(Application.Actions.COMPONENTI,
				"?operation=listNavigationOnClick&navigationEvent="
					+ ListAction.NE_DETTAGLIO_VIEW + "&table="
					+ ComponentiForm.ComponentiList.NAME + "&riga="
					+ getForm().getComponentiList().getTable()
						.getCurrentRowIndex(),
				form);
		    } else {
			getMessageBox().addError(
				"Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
			forwardToPublisher(getLastPublisher());
		    }
		}
	    } /* Dettaglio UNITA' DOCUMENTARIA DA LISTA COMPONENTI */ else if (lista
		    .equals(getForm().getUnitaDocumentarieList().getName())) {
		UnitaDocumentarieForm form = new UnitaDocumentarieForm();
		AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
		Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
		BigDecimal idUnitaDoc = null;
		/* Verifico se l'utente può accedere al dettaglio unità documentaria */
		ElvVListaCompElvRowBean rigaComp = ((ElvVListaCompElvRowBean) getForm()
			.getComponentiList().getTable().getRow(riga));
		if (rigaComp != null
			&& registriAbilitatiSet.contains(rigaComp.getCdRegistroKeyUnitaDoc())
			&& tipiUdAbilitateSet.contains(rigaComp.getIdTipoUnitaDoc())
			&& tipiDocAbilitatiSet.contains(rigaComp.getIdTipoDoc())
			&& subStrutAbilitateSet.contains(rigaComp.getIdSubStrut())) {
		    idUnitaDoc = rigaComp.getIdUnitaDoc();
		} else {
		    getMessageBox().addError(
			    "Utente non abilitato ad accedere al dettaglio dell'unità documentaria selezionata");
		    forwardToPublisher(getLastPublisher());
		}
		if (idUnitaDoc != null) {
		    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
			    .getAroVRicUnitaDocRowBean(idUnitaDoc, null, null);
		    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
			    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
			unitaDocTB.add(aroVRicUnitaDocRowBean);
			/* Preparo la LISTA UNITA' DOCUMENTARIE */
			form.getUnitaDocumentarieList().setTable(unitaDocTB);
			redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
				"?operation=listNavigationOnClick&navigationEvent="
					+ ListAction.NE_DETTAGLIO_VIEW + "&table="
					+ UnitaDocumentarieForm.UnitaDocumentarieList.NAME
					+ "&riga=0",
				form);
		    } else {
			getMessageBox().addError(
				"Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
			forwardToPublisher(getLastPublisher());
		    }
		}
	    } /* Dettaglio UNITA' DOCUMENTARIA DA LISTA AGGIORNAMENTI */ else if (lista
		    .equals(getForm().getUnitaDocumentarieUpdList().getName())) {
		UnitaDocumentarieForm form = new UnitaDocumentarieForm();
		AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
		Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
		BigDecimal idUnitaDoc = null;
		/* Verifico se l'utente può accedere al dettaglio unità documentaria */
		ElvVLisUpdUdRowBean rigaUpd = ((ElvVLisUpdUdRowBean) getForm()
			.getAggiornamentiList().getTable().getRow(riga));
		if (rigaUpd != null
			&& registriAbilitatiSet.contains(rigaUpd.getCdRegistroKeyUnitaDoc())) {
		    idUnitaDoc = rigaUpd.getIdUnitaDoc();
		} else {
		    getMessageBox().addError(
			    "Utente non abilitato ad accedere al dettaglio dell'unità documentaria selezionata");
		    forwardToPublisher(getLastPublisher());
		}
		if (idUnitaDoc != null) {
		    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
			    .getAroVRicUnitaDocRowBean(idUnitaDoc, null, null);
		    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
			    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
			unitaDocTB.add(aroVRicUnitaDocRowBean);
			/* Preparo la LISTA UNITA' DOCUMENTARIE */
			form.getUnitaDocumentarieList().setTable(unitaDocTB);
			redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
				"?operation=listNavigationOnClick&navigationEvent="
					+ ListAction.NE_DETTAGLIO_VIEW + "&table="
					+ UnitaDocumentarieForm.UnitaDocumentarieList.NAME
					+ "&riga=0",
				form);
		    } else {
			getMessageBox().addError(
				"Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
			forwardToPublisher(getLastPublisher());
		    }
		}
	    } /* Dettaglio DOCUMENTO */ else if (lista
		    .equals(getForm().getDocumentiList().getName())) {
		UnitaDocumentarieForm form = new UnitaDocumentarieForm();
		AroVLisDocTableBean docTB = new AroVLisDocTableBean();
		Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
		BigDecimal idUnitaDoc = null;
		/* Verifico se l'utente può accedere al dettaglio documento */
		ElvVListaCompElvRowBean rigaComp = ((ElvVListaCompElvRowBean) getForm()
			.getComponentiList().getTable().getRow(riga));
		if (registriAbilitatiSet.contains(rigaComp.getCdRegistroKeyUnitaDoc())
			&& tipiUdAbilitateSet.contains(rigaComp.getIdTipoUnitaDoc())
			&& tipiDocAbilitatiSet.contains(rigaComp.getIdTipoDoc())
			&& subStrutAbilitateSet.contains(rigaComp.getIdSubStrut())) {
		    idUnitaDoc = rigaComp.getIdUnitaDoc();
		} else {
		    getMessageBox().addError(
			    "Utente non abilitato ad accedere al dettaglio del documento selezionato");
		    forwardToPublisher(getLastPublisher());
		}
		if (idUnitaDoc != null) {
		    AroVRicUnitaDocRowBean aroVRicUnitaDocRowBean = udHelper
			    .getAroVRicUnitaDocRowBean(idUnitaDoc, null, null);
		    if (!aroVRicUnitaDocRowBean.getTiStatoConservazione()
			    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
			AroVLisDocRowBean row = new AroVLisDocRowBean();
			BigDecimal idDoc = rigaComp.getIdDoc();
			row.setIdDoc(idDoc);
			docTB.add(row);
			form.getDocumentiUDList().setTable(docTB);
			redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
				"?operation=listNavigationOnClick&navigationEvent="
					+ ListAction.NE_DETTAGLIO_VIEW + "&table="
					+ UnitaDocumentarieForm.DocumentiUDList.NAME + "&riga=0",
				form);
		    } else {
			getMessageBox().addError(
				"Operazione non possibile in quanto l'unità documentaria ha stato di conservazione = ANNULLATA");
			forwardToPublisher(getLastPublisher());
		    }
		}
	    }
	}
    }

    /* INSERIMENTO */
    @Override
    public void insertDettaglio() throws EMFError {
	throw new UnsupportedOperationException("Not supported yet."); // To change body of
								       // generated methods, choose
	// Tools | Templates.
    }

    /* MODIFICA */
    @Override
    public void updateElenchiVersamentoList() {
	String statoElenco = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getString("ti_stato_elenco");
	boolean editable = false;
	if (statoElenco.equals(ElencoStatusEnum.CHIUSO.name())
		|| statoElenco.equals(ElencoStatusEnum.VALIDATO.name())
		|| statoElenco.equals(ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS.name())
		|| statoElenco.equals(ElencoStatusEnum.IN_CODA_INDICE_AIP.name())
		|| statoElenco.equals(ElencoStatusEnum.INDICI_AIP_GENERATI.name())
		|| statoElenco.equals(ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name())
		|| statoElenco.equals(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name())
		|| statoElenco.equals(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name())) {
	    getForm().getElenchiVersamentoDetail().getNt_elenco_chiuso().setEditMode();
	    editable = true;
	} else if (statoElenco.equals(ElencoStatusEnum.APERTO.name())
		|| statoElenco.equals(ElencoStatusEnum.DA_CHIUDERE.name())) {
	    getForm().getElenchiVersamentoDetail().getNm_elenco().setEditMode();
	    getForm().getElenchiVersamentoDetail().getDs_elenco().setEditMode();
	    getForm().getElenchiVersamentoDetail().getNt_indice_elenco().setEditMode();
	    getForm().getElenchiVersamentoDetail().getNt_elenco_chiuso().setEditMode();
	    editable = true;
	}
	if (statoElenco.equals(ElencoStatusEnum.APERTO.name())
		|| statoElenco.equals(ElencoStatusEnum.CHIUSO.name())
		|| statoElenco.equals(ElencoStatusEnum.VALIDATO.name())
		|| statoElenco.equals(ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS.name())
		|| statoElenco.equals(ElencoStatusEnum.IN_CODA_INDICE_AIP.name())
		|| statoElenco.equals(ElencoStatusEnum.INDICI_AIP_GENERATI.name())) {
	    getForm().getElenchiVersamentoDetail().getTi_gest_elenco().setEditMode();
	    editable = true;
	}
	if (statoElenco.equals(ElencoStatusEnum.APERTO.name())
		|| statoElenco.equals(ElencoStatusEnum.CHIUSO.name())) {

	    // MEV#31945 - Eliminare validazione elenco UD con firma
	    // Quando la combo del tipoValidazione diventa editabile, viene tolto il valore FIRMA e
	    // se era stato scelto
	    // firma in
	    // precedenza il campo viene pulito per una nuova selezione
	    String valorePrecedente = getForm().getElenchiVersamentoDetail().getTi_valid_elenco()
		    .getValue();
	    getForm().getElenchiVersamentoDetail().getTi_valid_elenco()
		    .setDecodeMap(ComboUtil.getTipiValidazioneElencoSenzaFirma());
	    getForm().getElenchiVersamentoDetail().getTi_valid_elenco().setValue(valorePrecedente);
	    if (valorePrecedente.equals(ElvElencoVer.TiValidElenco.FIRMA.name())) {
		getForm().getElenchiVersamentoDetail().getTi_valid_elenco().setValue("");
	    }
	    getForm().getElenchiVersamentoDetail().getTi_valid_elenco().setEditMode();
	    getForm().getElenchiVersamentoDetail().getTi_mod_valid_elenco().setEditMode();
	    editable = true;
	}
	if (editable) {
	    getForm().getElenchiVersamentoList().setStatus(Status.update);
	    getForm().getElenchiVersamentoDetail().setStatus(Status.update);
	} else {
	    getMessageBox().addError("Modifica dell'elenco non permessa");
	}
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
    }

    /* CANCELLAZIONE */
    @Override
    public void deleteElenchiVersamentoList() throws EMFError {
	BigDecimal idElencoVers = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getBigDecimal("id_elenco_vers");
	/* Se non posso eliminare l'elenco, avverto l'utente... */
	if (!evEjb.isElencoDeletable(idElencoVers)) {
	    getMessageBox().addError(
		    "L'elenco di versamento è diverso da APERTO e da DA_CHIUDERE e da CHIUSO");
	} /* ...altrimenti procedo con la rimozione */ else {
	    try {
		evEjb.deleteElenco(getIdUtenteCorrente(), idElencoVers);
		getForm().getElenchiVersamentoList().getTable().remove();
		getMessageBox().addInfo("Elenco di versamento eliminato con successo");
	    } catch (Exception e) {
		getMessageBox().addError("Errore durante l'eliminazione dell'elenco di versamento");
	    } finally {
		String lastPublisher = getLastPublisher();
		if (Application.Publisher.ELENCHI_VERSAMENTO_DETAIL.equals(lastPublisher)) {
		    goBack();
		} else {
		    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA);
		}
	    }
	}
    }

    @Override
    public void deleteElenchiVersamentoDaFirmareList() throws EMFError {
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDaFirmareList().getTable()
		.getCurrentRow().getBigDecimal("id_elenco_vers");
	try {
	    evEjb.deleteElenco(getIdUtenteCorrente(), idElencoVers);
	    getForm().getElenchiVersamentoDaFirmareList().getTable().remove();
	    getMessageBox().addInfo("Elenco di versamento eliminato con successo");
	} catch (Exception e) {
	    getMessageBox()
		    .addError("Errore durante l'eliminazione dell'elenco di versamento da firmare");
	} finally {
	    String lastPublisher = getLastPublisher();
	    if (Application.Publisher.ELENCHI_VERSAMENTO_DETAIL.equals(lastPublisher)) {
		goBack();
	    } else {
		forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
	    }
	}
    }

    /* SALVA */
    @Override
    public void saveDettaglio() throws EMFError {
	/* Valori pre modifiche */
	BigDecimal idElencoVers = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getBigDecimal(ElvVLisElencoVersStatoTableDescriptor.COL_ID_ELENCO_VERS);
	String rowNmElenco = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getString(ElvVLisElencoVersStatoTableDescriptor.COL_NM_ELENCO);
	String rowDsElenco = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getString(ElvVLisElencoVersStatoTableDescriptor.COL_DS_ELENCO);
	String rowNtElencoChiuso = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getString(ElvVLisElencoVersStatoTableDescriptor.COL_NT_ELENCO_CHIUSO);
	String rowNtIndiceElenco = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getString(ElvVLisElencoVersStatoTableDescriptor.COL_NT_INDICE_ELENCO);
	String rowTiStatoElenco = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getString(ElvVLisElencoVersStatoTableDescriptor.COL_TI_STATO_ELENCO);
	String rowTiGestElenco = getForm().getElenchiVersamentoDetail().getTi_gest_elenco().parse();
	String rowTiValidElenco = getForm().getElenchiVersamentoDetail().getTi_valid_elenco()
		.parse();
	String rowTiModValidElenco = getForm().getElenchiVersamentoDetail().getTi_mod_valid_elenco()
		.parse();
	/* Valori post modifiche */
	getForm().getElenchiVersamentoDetail().post(getRequest());
	String nmElenco = getForm().getElenchiVersamentoDetail().getNm_elenco().parse();
	String dsElenco = getForm().getElenchiVersamentoDetail().getDs_elenco().parse();
	String ntIndiceElenco = getForm().getElenchiVersamentoDetail().getNt_indice_elenco()
		.parse();
	String ntElencoChiuso = getForm().getElenchiVersamentoDetail().getNt_elenco_chiuso()
		.parse();
	String tiGestElenco = getForm().getElenchiVersamentoDetail().getTi_gest_elenco().parse();
	String tiValidElenco = getForm().getElenchiVersamentoDetail().getTi_valid_elenco().parse();
	String tiModValidElenco = getForm().getElenchiVersamentoDetail().getTi_mod_valid_elenco()
		.parse();

	/* Controllo se il nome dell'elenco è stato modificato */
	/* Controllo che non esista su db per quella struttura un elenco con lo stesso nome */
	if (rowNmElenco != null && !rowNmElenco.equals(nmElenco)
		&& evEjb.existNomeElenco(nmElenco, getIdStrutCorrente())) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Nome elenco di versamento già esistente per la struttura utilizzata"));
	    /*
	     * Se non va bene, reimposto il valore precedente nella casella di testo del nome elenco
	     */
	    getForm().getElenchiVersamentoDetail().getNm_elenco().setValue(rowNmElenco);
	}

	/* Controllo se e cosa ho modificato */
	List<ElencoEnums.OpTypeEnum> operList = new ArrayList<>();
	if (!StringUtils.equals(rowNmElenco, nmElenco) || !StringUtils.equals(rowDsElenco, dsElenco)
		|| !StringUtils.equals(rowTiGestElenco, tiGestElenco)
		|| !StringUtils.equals(rowTiValidElenco, tiValidElenco)
		|| !StringUtils.equals(rowTiModValidElenco, tiModValidElenco)) {
	    operList.add(ElencoEnums.OpTypeEnum.MOD_ELENCO);
	}

	if (!StringUtils.equals(rowNtElencoChiuso, ntElencoChiuso)) {
	    operList.add(ElencoEnums.OpTypeEnum.DEF_NOTE_ELENCO_CHIUSO);
	} else if (!StringUtils.equals(rowNtIndiceElenco, ntIndiceElenco)) {
	    operList.add(ElencoEnums.OpTypeEnum.DEF_NOTE_INDICE_ELENCO);
	}
	if (getForm().getElenchiVersamentoDetail().validate(getMessageBox())) {
	    if (!getMessageBox().hasError()) {
		try {
		    evEjb.saveNomeDesNote(getIdUtenteCorrente(), idElencoVers, nmElenco, dsElenco,
			    ntIndiceElenco, ntElencoChiuso, operList, tiGestElenco, tiValidElenco,
			    tiModValidElenco);
		    getMessageBox().addInfo("Elenco di versamento modificato con successo");
		    initBottoniPaginaDettaglioElencoVersamento(idElencoVers, rowTiStatoElenco);
		    setElencoVersamentoListAndDetailViewMode();
		    // MAC#21555
		    /*
		     * Ricavo la lista delle strutture da cercare in base a come sono state
		     * impostate nei filtri di ricerca le combo Ambiente/Ente/Struttura
		     */
		    // end MAC#21555
		    reloadElenchiVersamentoList(getForm().getFiltriElenchiVersamento());
		    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
		} catch (EMFError e) {
		    getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getMessage()));
		}
	    }
	}
    }

    /* ANNULLA */
    @Override
    public void undoDettaglio() throws EMFError {
	dettaglioElencoVersamento(
		getForm().getElenchiVersamentoDetail().getId_elenco_vers().parse());
	setElencoVersamentoListAndDetailViewMode();
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
    }

    /* INDIETRO */
    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    /**
     *
     * @param publisherName la pagina cui deve andare a seguito della goBack()
     */
    @Override
    public void reloadAfterGoBack(String publisherName) {
	if (publisherName != null) {
	    String lista = getTableName();
	    /*
	     * Se tornando indietro devo andare nella pagina di Ricerca Elenchi di Versamento
	     * rilancio la ricerca in quanto posso aver modificato l'elenco esaminato
	     */
	    if (publisherName.equals(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA)
		    && (lista != null
			    && lista.equals(getForm().getElenchiVersamentoList().getName()))
		    || (getRequest().getParameter("mainNavTable") != null
			    && getRequest().getParameter("mainNavTable")
				    .equals(getForm().getElenchiVersamentoList().getName()))) {
		try {
		    // MAC#21555
		    /*
		     * Ricavo la lista delle strutture da cercare in base a come sono state
		     * impostate nei filtri di ricerca le combo Ambiente/Ente/Struttura
		     */
		    // end MAC#21555
		    reloadElenchiVersamentoList(getForm().getFiltriElenchiVersamento());
		} catch (EMFError ex) {
		    log.error(ex.getDescription(), ex);
		}
	    } else if (publisherName.equals(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL)) {
		try {
		    dettaglioElencoVersamento(
			    getForm().getElenchiVersamentoDetail().getId_elenco_vers().parse());
		} catch (EMFError ex) {
		    log.error(ex.getDescription(), ex);
		}
	    }
	}
    }

    /* TAB */
    /**
     * Attiva il tab del dettaglio elenco elenco di versamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabDettaglioElencoTabOnClick() throws EMFError {
	getForm().getDettaglioElencoTabs()
		.setCurrentTab(getForm().getDettaglioElencoTabs().getDettaglioElencoTab());
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
    }

    /**
     * Attiva il tab dei filtri sui componenti nel dettaglio elenco di versamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabFiltriRicercaComponentiTabOnClick() throws EMFError {
	getForm().getDettaglioElencoTabs()
		.setCurrentTab(getForm().getDettaglioElencoTabs().getFiltriRicercaComponentiTab());
	getForm().getComponentiFiltri().setEditMode();
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
    }

    /* TRIGGER */
    @Override
    public JSONObject triggerComponentiFiltriNm_tipo_strut_docOnTrigger() throws EMFError {
	/* Faccio il post dei filtri componenti e ricavo il valore che mi interessa */
	getForm().getComponentiFiltri().post(getRequest());
	BigDecimal idStrutDoc = getForm().getComponentiFiltri().getNm_tipo_strut_doc().parse();
	DecodeMap mappaTipoCompDoc = new DecodeMap();
	if (idStrutDoc != null) {
	    /*
	     * Setto i valori della combo TIPO COMPONENTE DOCUMENTO ricavati dalla tabella
	     * DEC_TIPO_STRUT_DOC
	     */
	    DecTipoCompDocTableBean decTipoCompDocTableBean = tipoStrutDocEjb
		    .getDecTipoCompDocTableBean(idStrutDoc, false);
	    mappaTipoCompDoc.populatedMap(decTipoCompDocTableBean, "id_tipo_comp_doc",
		    "nm_tipo_comp_doc");
	}
	getForm().getComponentiFiltri().getNm_tipo_comp_doc().setDecodeMap(mappaTipoCompDoc);
	return getForm().getComponentiFiltri().asJSON();
    }

    /* BOTTONI */
    /**
     * Metodo richiamato al click del bottone di ricerca elenchi di versamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaElenchiButton() throws EMFError {
	ElenchiVersamentoForm.FiltriElenchiVersamento filtriElenchiVersamento = getForm()
		.getFiltriElenchiVersamento();
	/* Esegue la post dei filtri compilati */
	filtriElenchiVersamento.post(getRequest());
	/* Valida i filtri per verificarne la correttezza sintattica e l'obbligatorietà */
	if (filtriElenchiVersamento.validate(getMessageBox())) {
	    /* Valida in maniera più specifica i dati, utilizzando il validator dei volumi */
	    VolumiValidator validator = new VolumiValidator(getMessageBox());
	    validator.validateDataVolumi(
		    filtriElenchiVersamento.getDt_creazione_elenco_da().parse(),
		    filtriElenchiVersamento.getDt_creazione_elenco_a().parse());
	    validator.validateDataVolumi(
		    filtriElenchiVersamento.getDt_validazione_elenco_da().parse(),
		    filtriElenchiVersamento.getDt_validazione_elenco_a().parse());
	    validator.validateDataVolumi(
		    filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_da().parse(),
		    filtriElenchiVersamento.getDt_creazione_elenco_ix_aip_a().parse());
	    validator.validateDataVolumi(
		    filtriElenchiVersamento.getDt_firma_elenco_ix_aip_da().parse(),
		    filtriElenchiVersamento.getDt_firma_elenco_ix_aip_a().parse());
	    Object[] chiavi = validator.validaChiaveUnitaDocVolumi(
		    filtriElenchiVersamento.getCd_registro_key_unita_doc().parse(),
		    filtriElenchiVersamento.getAa_key_unita_doc().parse(),
		    filtriElenchiVersamento.getCd_key_unita_doc().parse(),
		    filtriElenchiVersamento.getAa_key_unita_doc_da().parse(),
		    filtriElenchiVersamento.getAa_key_unita_doc_a().parse(),
		    filtriElenchiVersamento.getCd_key_unita_doc_da().parse(),
		    filtriElenchiVersamento.getCd_key_unita_doc_a().parse());
	    validator.validateCodaJMS(chiavi,
		    filtriElenchiVersamento.getHh_stato_elenco_in_coda_jms().parse(),
		    filtriElenchiVersamento.getTi_stato_elenco().parse());
	    /* Se la validazione più approfondita non ha riportato errori */
	    if (!getMessageBox().hasError()) {
		// MAC#21555
		/*
		 * Ricavo la lista delle strutture da cercare in base a come sono state impostate
		 * nei filtri di ricerca le combo Ambiente/Ente/Struttura
		 */
		// end MAC#21555

		/*
		 * Setto i filtri di chiavi unità documentaria impostando gli eventuali valori di
		 * default
		 */
		if (chiavi != null && chiavi.length == 5) {
		    filtriElenchiVersamento.getAa_key_unita_doc_da().setValue(
			    chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
		    filtriElenchiVersamento.getAa_key_unita_doc_a().setValue(
			    chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
		    filtriElenchiVersamento.getCd_key_unita_doc_da()
			    .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
		    filtriElenchiVersamento.getCd_key_unita_doc_a()
			    .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
		}

		/* Effettuo la ricerca */
		if (chiavi != null
			|| !filtriElenchiVersamento.getTi_stato_conservazione().parse().isEmpty()) {
		    // Se sono presenti i filtri dell'UD, devo controllare sia stata settata la
		    // struttura
		    if (filtriElenchiVersamento.getId_strut().parse() != null) {
			getForm().getElenchiVersamentoList().setTable(
				evEjb.getElvVRicElencoVersByUdTableBean(getUser().getIdUtente(),
					filtriElenchiVersamento));
		    } else {
			getMessageBox().addError(
				"Attenzione: sono stati selezionati uno o più filtri della chiave unità documentaria, è necessario impostare anche il filtro Struttura");
		    }
		} else {
		    // Se non sono presenti i filtri dell'UD
		    List<String> tiStatoElenco = filtriElenchiVersamento.getTi_stato_elenco()
			    .parse();
		    if (!tiStatoElenco.isEmpty() && !tiStatoElenco
			    .contains(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())) {
			// Se il filtro di stato elenco esiste e non contiene COMPLETATO
			getForm().getElenchiVersamentoList().setTable(
				evEjb.getElvVRicElencoVersByStatoTableBean(getUser().getIdUtente(),
					filtriElenchiVersamento));
		    } else {
			getForm().getElenchiVersamentoList().setTable(
				evEjb.getElvVRicElencoVersTableBean(getUser().getIdUtente(),
					filtriElenchiVersamento));
		    }
		}
		getForm().getElenchiVersamentoList().getTable().setPageSize(10);
		getForm().getElenchiVersamentoList().getTable().first();

		/*
		 * Imposto la sezione del filtro chiave unità documentaria aperto dopo la ricerca se
		 * ho inserito un valore nel campo registro
		 */
		if (!getForm().getFiltriElenchiVersamento().getCd_registro_key_unita_doc()
			.getValue().equals("")) {
		    getForm().getChiaveSection().setLoadOpened(true);
		}
	    }
	}
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA);
    }

    /**
     * Ricerca componenti + aggiornamenti all'interno del dettaglio elenco di versamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaComp() throws EMFError {
	ElenchiVersamentoForm.ComponentiFiltri compfiltri = getForm().getComponentiFiltri();
	compfiltri.post(getRequest());
	if (compfiltri.validate(getMessageBox())) {
	    VolumiValidator volumiValidator = new VolumiValidator(getMessageBox());
	    UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
	    Date[] dateAcquisizioneValidate = validator.validaDate(
		    compfiltri.getDt_creazione_da().parse(),
		    compfiltri.getOre_dt_creazione_da().parse(),
		    compfiltri.getMinuti_dt_creazione_da().parse(),
		    compfiltri.getDt_creazione_a().parse(),
		    compfiltri.getOre_dt_creazione_a().parse(),
		    compfiltri.getMinuti_dt_creazione_a().parse(),
		    compfiltri.getDt_creazione_da().getHtmlDescription(),
		    compfiltri.getDt_creazione_a().getHtmlDescription());
	    volumiValidator.validateDataVolumi(compfiltri.getDt_scad_firma_comp_da().parse(),
		    compfiltri.getDt_scad_firma_comp_a().parse());
	    validator.validaDimensioniKb(compfiltri.getNi_size_file_da().parse(),
		    compfiltri.getNi_size_file_a().parse());
	    Object[] chiavi = null;
	    if (!getMessageBox().hasError()) {
		/* Valida i campi di Range di chiavi unità documentaria */
		chiavi = validator.validaChiaviUnitaDoc(
			compfiltri.getCd_registro_key_unita_doc().getValue(),
			compfiltri.getAa_key_unita_doc().parse(),
			compfiltri.getCd_key_unita_doc().parse(),
			compfiltri.getAa_key_unita_doc_da().parse(),
			compfiltri.getAa_key_unita_doc_a().parse(),
			compfiltri.getCd_key_unita_doc_da().parse(),
			compfiltri.getCd_key_unita_doc_a().parse());
	    }

	    if (!getMessageBox().hasError()) {
		// La validazione non ha riportato errori.
		if (chiavi != null && chiavi.length == 5) {
		    compfiltri.getAa_key_unita_doc_da().setValue(
			    chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
		    compfiltri.getAa_key_unita_doc_a().setValue(
			    chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
		    compfiltri.getCd_key_unita_doc_da()
			    .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
		    compfiltri.getCd_key_unita_doc_a()
			    .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
		}
		// Carico la lista dei Componenti
		getForm().getComponentiList()
			.setTable(componentiHelper.getElvVListaCompElvViewBean(
				getForm().getElenchiVersamentoDetail().getId_elenco_vers().parse(),
				getForm().getElenchiVersamentoDetail().getId_strut().parse()));
		getForm().getComponentiList().getTable().setPageSize(10);
		getForm().getComponentiList().setUserOperations(true, false, false, false);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getComponentiList().getTable().first();
		// Carico la lista degli Aggiornamenti
		getForm().getAggiornamentiList()
			.setTable(aggiornamentiHelper.getElvVLisUpdUdViewBean(
				getForm().getElenchiVersamentoDetail().getId_elenco_vers().parse(),
				compfiltri));
		getForm().getAggiornamentiList().getTable().setPageSize(10);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getAggiornamentiList().getTable().first();
		// Imposto la sezione del filtro chiave unità documentaria aperto dopo la ricerca se
		// ho inserito un
		// valore nel campo registro
		if (!getForm().getComponentiFiltri().getCd_registro_key_unita_doc().getValue()
			.equals("")) {
		    getForm().getChiaveSection().setLoadOpened(true);
		}
	    }
	}
	// Workaround per evitare che il trigger scarichi la pagina HTML anziché visualizzarla sul
	// browser
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
    }

    @Override
    public void pulisciFiltriRicercaElenchiButton() throws EMFError {
	ricercaElenchiVersamento();
    }

    /**
     * Bottone di chiusura elenco di versamento. Se presente, significa che sono già soddisfatte le
     * condizioni per la chiusura dell'elenco (ovvero stato APERTO e con almeno una unità
     * documentaria o un documento aggiunto appartenente)
     *
     * @throws Throwable errore generico
     */
    @Override
    public void chiudiElencoButton() throws Throwable {
	getRequest().setAttribute("chiudiElencoBox", true);
	getRequest().setAttribute("nt_indice_elenco",
		getForm().getElenchiVersamentoDetail().getNt_indice_elenco().parse());
	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
    }

    public void confermaChiusuraElenco() throws EMFError {
	/* Recupero l'elenco da chiudere e le relative note su indice elenco */
	BigDecimal idElencoVers = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getBigDecimal("id_elenco_vers");
	/* Campo note prese da dettaglio */
	String noteDettaglio = getForm().getElenchiVersamentoDetail().getNt_indice_elenco().parse();
	/* Campo note preso da finestra pop-up */
	String notePopUp = getRequest().getParameter("Nt_indice_elenco");

	/* Verifico se ci sono state modifiche sulle note indice elenco */
	List<ElencoEnums.OpTypeEnum> modifica = new ArrayList<>();
	if (!StringUtils.equals(noteDettaglio, notePopUp)) {
	    modifica.add(ElencoEnums.OpTypeEnum.DEF_NOTE_INDICE_ELENCO);
	}

	try {
	    evEjb.manualClosingElenco(getIdUtenteCorrente(), idElencoVers, modifica, notePopUp);
	    String messageOk = "Elenco di versamento chiuso con successo!";
	    // MEV 24534 se il tipo validazione dell'elenco è NO_INDICE la chiusura non l'ha creato
	    ElvElencoVerRowBean elencoVers = evEjb.getElvElencoVersRowBean(idElencoVers);
	    boolean noIndice = ElvElencoVer.TiValidElenco.NO_INDICE.name()
		    .equals(elencoVers.getTiValidElenco());
	    if (noIndice) {
		messageOk = "Elenco di versamento chiuso con successo (senza generazione dell'indice).";
	    }

	    /*
	     * Se l'elenco ha tipo validazione = NO_FIRMA e modalità di validazione = AUTOMATICA; se
	     * i valori nell'elenco sono nulli leggo i valori dal criterio di raggruppamento.
	     */
	    boolean validElenco = evEjb.isElencoValidable(idElencoVers);
	    if (validElenco) {
		evEjb.validElenco(getIdUtenteCorrente(), idElencoVers);
		messageOk = "Elenco di versamento validato con successo!";
	    }

	    getMessageBox().addInfo(messageOk);

	} catch (ParerInternalError | ParerNoResultException | ParerUserError | IOException
		| NoSuchAlgorithmException | ParseException e) {
	    log.error("Eccezione", e);
	    getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
		    "Errore durante la chiusura/validazione dell'elenco"));
	} finally {
	    /*
	     * Ricarico il dettaglio elenco per caricare i dati modificati e visualizzare i bottoni
	     * corretti per il suo nuovo stato
	     */
	    dettaglioElencoVersamento(idElencoVers);
	    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
	}
    }

    @Override
    public void listaOperazioniElencoButton() throws Throwable {
	MonitoraggioForm form = new MonitoraggioForm();
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDetail().getId_elenco_vers()
		.parse();
	BigDecimal idStrut = getIdStrutCorrente();
	redirectToAction(Application.Actions.MONITORAGGIO,
		"?operation=ricercaOperazioniElenchiDaDettaglioElenco&idElencoPerMon="
			+ idElencoVers + "&idStrutPerMon=" + idStrut + "&eseguiForward=true",
		form);
    }

    @Override
    public void validaElenchiButton() throws Throwable {

	ElvElencoVerTableBean elenchiDaValidare = checkElenchiVersamentoToSign();
	int elenchiValidati = 0;
	int elenchiEliminati = 0;
	int daValidare = 0;
	List<BigDecimal> idElencoVersRigheTotali = new ArrayList<>();
	List<BigDecimal> idElencoVersRigheCancellate = new ArrayList<>();
	HashMap<BigDecimal, String> mapElenchiEliminati = new HashMap<BigDecimal, String>();

	// Se non ci sono problemi, procedo alla validazione
	if (!getMessageBox().hasError() && elenchiDaValidare != null) {

	    for (int i = 0; i < elenchiDaValidare.size(); i++) {
		ElvElencoVerRowBean elenco = elenchiDaValidare.getRow(i);

		BigDecimal idElencoVers = elenco.getIdElencoVers();
		idElencoVersRigheTotali.add(idElencoVers);

		if (evEjb.almenoUnaUdAnnul(idElencoVers)) {
		    // Se lo sono, annullo lo stato di generazione indice AIP di ud e doc ed elimino
		    // l'elenco
		    evEjb.manageElencoUdAnnulDaFirmaElenco(idElencoVers, getUser().getIdUtente());
		    idElencoVersRigheCancellate.add(idElencoVers);
		    mapElenchiEliminati.put(idElencoVers, elenco.getTiStatoElenco());
		    elenchiEliminati++;

		} else {
		    evEjb.validElenco(getIdUtenteCorrente(), idElencoVers);
		    elenchiValidati++;
		}
	    }
	}
	daValidare = getForm().getElenchiVersamentoSelezionatiList().getTable().size();
	if (!getMessageBox().hasError()) {
	    if (elenchiEliminati > 0) {
		// Elimino a video gli elenchi cancellati su DB in quanto contenenti solo ud
		// annullate
		idElencoVersRigheTotali.removeAll(idElencoVersRigheCancellate);
		ElvVLisElencoVersStatoTableBean elenchiRimanenti = evEjb
			.getElenchiDaFirmareTableBean(idElencoVersRigheTotali,
				getUser().getIdUtente());
		getForm().getElenchiVersamentoSelezionatiList().setTable(elenchiRimanenti);

		getMessageBox().setViewMode(ViewMode.plain);
		getMessageBox().addInfo("Sono stati eliminati " + elenchiEliminati
			+ " elenchi di versamento in quanto contenenti almeno una unità documentaria annullata");

		mapElenchiEliminati.entrySet().forEach(entry -> {
		    getMessageBox().addMessage(new Message(MessageLevel.WAR, "Eliminato elenco Id: "
			    + entry.getKey() + " stato: " + entry.getValue()));
		});
	    }
	}
	endValidaElenco(daValidare, elenchiValidati, mapElenchiEliminati);
    }

    public void endValidaElenco(int daValidare, int validati,
	    HashMap<BigDecimal, String> mapElenchiEliminati) throws EMFError {

	if (validati > 0) {
	    getMessageBox().setViewMode(ViewMode.plain);

	    /* Se ho validato tutti gli elenchi mostro una INFO */
	    if (validati == daValidare) {
		getMessageBox().addMessage(new Message(MessageLevel.INF,
			"Validazione eseguita correttamente: validati " + validati + " su "
				+ daValidare));
		/* Inizializzo la lista degli elenchi di versamento selezionati */
		getForm().getElenchiVersamentoSelezionatiList()
			.setTable(new ElvVLisElencoVersStatoTableBean());
		getForm().getElenchiVersamentoSelezionatiList().getTable().setPageSize(10);
		getForm().getElenchiVersamentoSelezionatiList().getTable().addSortingRule(getForm()
			.getElenchiVersamentoSelezionatiList().getDt_creazione_elenco().getName(),
			SortingRule.ASC);
	    } /* altrimenti mostro un WARNING */ else {
		getMessageBox().addMessage(new Message(MessageLevel.WAR,
			"Non tutti gli elenchi sono stati validati correttamente: validati "
				+ validati + " su " + daValidare));
	    }
	}

	// Eseguo la ricerca sui filtri pre-impostati
	ricercaElenchiDaFirmare(getForm().getFiltriElenchiDaFirmare());
    }

    /**
     * Bottone "+" della "Lista elenchi di versamento da firmare" per spostare un elenco da questa
     * lista a quella degli elenchi selezionati pronti per essere firmati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiVersamentoDaFirmareList() throws EMFError {
	/* Ricavo il record interessato della "Lista elenchi di versamento da firmare" */
	ElvVLisElencoVersStatoRowBean row = (ElvVLisElencoVersStatoRowBean) getForm()
		.getElenchiVersamentoDaFirmareList().getTable().getCurrentRow();
	int index = getForm().getElenchiVersamentoDaFirmareList().getTable().getCurrentRowIndex();
	/* Lo tolgo dalla lista elenchi di versamento da firmare */
	getForm().getElenchiVersamentoDaFirmareList().getTable().remove(index);
	/* "Refresho" la lista senza il record */
	int paginaCorrente = getForm().getElenchiVersamentoDaFirmareList().getTable()
		.getCurrentPageIndex();
	int inizio = getForm().getElenchiVersamentoDaFirmareList().getTable()
		.getFirstRowPageIndex();
	this.lazyLoadGoPage(getForm().getElenchiVersamentoDaFirmareList(), paginaCorrente);
	getForm().getElenchiVersamentoDaFirmareList().getTable().setCurrentRowIndex(inizio);
	/* Aggiungo il record nella lista degli elenchi di versamento selezionati */
	getForm().getElenchiVersamentoSelSection().setLoadOpened(true);
	getForm().getElenchiVersamentoSelezionatiList().add(row);
	getForm().getElenchiVersamentoSelezionatiList().getTable().addSortingRule(
		getForm().getElenchiVersamentoSelezionatiList().getDt_creazione_elenco().getName(),
		SortingRule.ASC);
	getForm().getElenchiVersamentoSelezionatiList().getTable().sort();
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    }

    /**
     * Bottone "-" della "Lista elenchi di versamento selezionati" per spostare un elenco da questa
     * lista a quella degli elenchi di versamento da firmare
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiVersamentoSelezionatiList() throws EMFError {
	/* Ricavo il record interessato della "Lista elenchi di versamento selezionati" */
	ElvVLisElencoVersStatoRowBean row = (ElvVLisElencoVersStatoRowBean) getForm()
		.getElenchiVersamentoSelezionatiList().getTable().getCurrentRow();
	int index = getForm().getElenchiVersamentoSelezionatiList().getTable().getCurrentRowIndex();
	/* Lo tolgo dalla lista elenchi di versamento selezionati */
	getForm().getElenchiVersamentoSelezionatiList().getTable().remove(index);
	/* "Refresho" la lista senza il record */
	int paginaCorrente = getForm().getElenchiVersamentoSelezionatiList().getTable()
		.getCurrentPageIndex();
	int inizio = getForm().getElenchiVersamentoSelezionatiList().getTable()
		.getFirstRowPageIndex();
	// Rieseguo la query se necessario
	this.lazyLoadGoPage(getForm().getElenchiVersamentoSelezionatiList(), paginaCorrente);
	// Ritorno alla pagina
	getForm().getElenchiVersamentoSelezionatiList().getTable().setCurrentRowIndex(inizio);
	// Pagina Volumi da firmare
	getForm().getElenchiVersamentoDaFirmareList().add(row);
	int paginaCorrenteVF = getForm().getElenchiVersamentoDaFirmareList().getTable()
		.getCurrentPageIndex();
	int inizioVF = getForm().getElenchiVersamentoDaFirmareList().getTable()
		.getFirstRowPageIndex();
	// Rieseguo la query se necessario
	this.lazyLoadGoPage(getForm().getElenchiVersamentoDaFirmareList(), paginaCorrenteVF);
	// Ritorno alla pagina
	getForm().getElenchiVersamentoDaFirmareList().getTable().setCurrentRowIndex(inizioVF);
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    }

    @Override
    public void eliminaAppartenenzaUdDocDaElenco() throws Throwable {
	/* Ottengo i componenti selezionati dalla lista */
	String[] componentiSelezionati = getRequest().getParameterValues("Select_comp");
	List<BigDecimal> idUnitaDocs = new ArrayList<>();
	Set<BigDecimal> idDocsToRemove = new HashSet<>();
	if (componentiSelezionati != null) {
	    for (String comp : componentiSelezionati) {
		if (StringUtils.isNumeric(comp)) {
		    idUnitaDocs.add(((ElvVListaCompElvRowBean) getForm().getComponentiList()
			    .getTable().getRow(Integer.parseInt(comp))).getIdUnitaDoc());
		    idDocsToRemove.add(((ElvVListaCompElvRowBean) getForm().getComponentiList()
			    .getTable().getRow(Integer.parseInt(comp))).getIdDoc());
		}
	    }

	    if (!idUnitaDocs.isEmpty()) {
		getSession().setAttribute("idDocsToRemove", idDocsToRemove);
		getForm().getDocDaRimuovereList()
			.setTable(udHelper.getAroVLisDocTableBeanByIdDoc(idDocsToRemove));
		getForm().getDocDaRimuovereList().getTable().setPageSize(10);
		getForm().getDocDaRimuovereList().getTable().first();
		getRequest().setAttribute("elenco",
			getForm().getElenchiVersamentoDetail().getNm_elenco().parse());

		/* Apre la pagina con la lista delle unità documentarie per la conferma rimozione */
		getForm().getListaDocDaRimuovereButtonList().setEditMode();
		forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DOC_DA_RIMUOVERE_LIST);
	    }
	} else {
	    getMessageBox().addInfo("Seleziona almeno un componente");
	    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
	}
    }

    @Override
    public void confermaRimozioneDocButton() throws Throwable {
	/* Ottengo l'idElencoVers */
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDetail().getId_elenco_vers()
		.parse();
	Set<BigDecimal> idDocsToRemove = (Set<BigDecimal>) getSession()
		.getAttribute("idDocsToRemove");
	try {
	    evEjb.deleteUdDocFromElencoVersamento(idElencoVers.longValue(), idDocsToRemove,
		    getIdUtenteCorrente());

	    if (evEjb.existNomeElenco(getForm().getElenchiVersamentoDetail().getNm_elenco().parse(),
		    getForm().getElenchiVersamentoDetail().getId_strut().parse())) {
		getMessageBox().addInfo("Unità documentarie eliminate con successo!");
		goBack();
	    } else {
		getMessageBox().addInfo("Unità documentarie eliminate con successo! "
			+ "In quanto rimasto privo di componenti, l'elenco di versamento "
			+ idElencoVers + " è stato eliminato!");
		setTableName(getForm().getElenchiVersamentoList().getName());
		goBackTo(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA);
	    }
	    getSession().removeAttribute("idDocsToRemove");
	    getMessageBox().setViewMode(ViewMode.plain);
	} catch (Exception e) {
	    getMessageBox().addError("Errore durante l'eliminazione delle unita' documentarie");
	    // messaggio
	    getMessageBox().setViewMode(ViewMode.plain);
	    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DOC_DA_RIMUOVERE_LIST);
	}
    }

    @Override
    public void annullaRimozioneDocButton() throws Throwable {
	goBack();
    }

    @Override
    public void eliminaAppartenenzaUpdDaElenco() throws Throwable {
	/* Ottengo gli aggiornamenti selezionati dalla lista */
	String[] aggiornamentiSelezionati = getRequest().getParameterValues("Select_upd");
	List<BigDecimal> idUnitaDocs = new ArrayList<>();
	Set<BigDecimal> idUpdsToRemove = new HashSet<>();
	if (aggiornamentiSelezionati != null) {
	    for (String upd : aggiornamentiSelezionati) {
		if (StringUtils.isNumeric(upd)) {
		    idUnitaDocs.add(((ElvVLisUpdUdRowBean) getForm().getAggiornamentiList()
			    .getTable().getRow(Integer.parseInt(upd))).getIdUnitaDoc());
		    idUpdsToRemove.add(((ElvVLisUpdUdRowBean) getForm().getAggiornamentiList()
			    .getTable().getRow(Integer.parseInt(upd))).getIdUpdUnitaDoc());
		}
	    }

	    if (!idUnitaDocs.isEmpty()) {
		getSession().setAttribute("idUpdsToRemove", idUpdsToRemove);
		getForm().getUpdDaRimuovereList()
			.setTable(udHelper.getElvVLisUpdUdTableBeanByIdUpd(idUpdsToRemove));
		getForm().getUpdDaRimuovereList().getTable().setPageSize(10);
		getForm().getUpdDaRimuovereList().getTable().first();
		getRequest().setAttribute("elenco",
			getForm().getElenchiVersamentoDetail().getNm_elenco().parse());

		/* Apre la pagina con la lista delle unità documentarie per la conferma rimozione */
		getForm().getListaUpdDaRimuovereButtonList().setEditMode();
		forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_UPD_DA_RIMUOVERE_LIST);
	    }
	} else {
	    getMessageBox().addInfo("Seleziona almeno un aggiornamento");
	    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_DETAIL);
	}
    }

    @Override
    public void confermaRimozioneUpdButton() throws Throwable {
	/* Ottengo l'idElencoVers */
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDetail().getId_elenco_vers()
		.parse();
	Set<BigDecimal> idUpdsToRemove = (Set<BigDecimal>) getSession()
		.getAttribute("idUpdsToRemove");
	try {
	    evEjb.deleteUpdFromElencoVersamento(idElencoVers.longValue(), idUpdsToRemove,
		    getIdUtenteCorrente());

	    if (evEjb.existNomeElenco(getForm().getElenchiVersamentoDetail().getNm_elenco().parse(),
		    getForm().getElenchiVersamentoDetail().getId_strut().parse())) {
		getMessageBox().addInfo("Unità documentarie eliminate con successo!");
		goBack();
	    } else {
		getMessageBox().addInfo("Unità documentarie eliminate con successo! "
			+ "In quanto rimasto privo di contenuti, l'elenco di versamento "
			+ idElencoVers + " è stato eliminato!");
		setTableName(getForm().getElenchiVersamentoList().getName());
		goBackTo(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA);
	    }
	    getSession().removeAttribute("idUpdsToRemove");
	    getMessageBox().setViewMode(ViewMode.plain);
	} catch (Exception e) {
	    getMessageBox().addError("Errore durante l'eliminazione delle unita' documentarie");
	    // messaggio
	    getMessageBox().setViewMode(ViewMode.plain);
	    forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_UPD_DA_RIMUOVERE_LIST);
	}
    }

    @Override
    public void annullaRimozioneUpdButton() throws Throwable {
	goBack();
    }

    @Override
    public void scaricaIndiceElencoButton() throws Throwable {
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDetail().getId_elenco_vers()
		.parse();
	String nmAmbiente = getForm().getElenchiVersamentoDetail().getNm_ambiente().parse();
	String nmEnte = getForm().getElenchiVersamentoDetail().getNm_ente().parse();
	String nmStrut = getForm().getElenchiVersamentoDetail().getNm_strut().parse();
	String filesSuffix = nmAmbiente + "_" + nmEnte + "_" + nmStrut + "_" + idElencoVers;
	/* Comincio a costruire lo zippone */
	String nomeZippone = "indice_elenco_"
		+ StringUtils.defaultString(filesSuffix).replace(" ", "_");
	getResponse().setContentType("application/zip");
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeZippone + ".zip");
	try (ZipOutputStream out = new ZipOutputStream(getServletOutputStream());) {
	    evEjb.streamOutFileIndiceElenco(out, "IndiceElencoVersamento_", filesSuffix,
		    idElencoVers.longValue(), FileTypeEnum.getIndiceFileTypes());
	    freeze();
	} catch (Exception e) {
	    log.error("Eccezione", e);
	    getMessageBox().addError("Errore nel recupero dei file delle prove di conservazione");
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void generaIndiceElencoButton() throws Throwable {
	// MEV #24534 pulsante di creazione manuale dell'indice.
	// forwardToPublisher(getLastPublisher());
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDetail().getId_elenco_vers()
		.parse();
	String nmAmbiente = getForm().getElenchiVersamentoDetail().getNm_ambiente().parse();
	String nmEnte = getForm().getElenchiVersamentoDetail().getNm_ente().parse();
	String nmStrut = getForm().getElenchiVersamentoDetail().getNm_strut().parse();

	String infoMessage = String.format(
		"Creazione manuale indice per l'elenco con id %s per la struttura %s dell'ente %s afferente all'ambiente %s ",
		String.valueOf(idElencoVers), nmStrut, nmEnte, nmAmbiente);
	log.info(infoMessage);

	// MEV #31947 - Eliminare il salvataggio degli elenchi di versamento UD
	String filesSuffix = nmAmbiente + "_" + nmEnte + "_" + nmStrut + "_" + idElencoVers;
	String nomeZippone = "indice_elenco_"
		+ StringUtils.defaultString(filesSuffix).replace(" ", "_");
	getResponse().setContentType("application/zip");
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeZippone + ".zip");
	try (ZipOutputStream out = new ZipOutputStream(getServletOutputStream());) {
	    byte[] xml = evEjb.generaIndice(idElencoVers.longValue());
	    evEjb.addEntryToZip(out, xml, "IndiceElencoVersamento_" + filesSuffix + ".xml");
	    out.flush();
	    freeze();
	} catch (Exception e) {
	    log.error("Eccezione", e);
	    getMessageBox().addError("Errore nel recupero dei file delle prove di conservazione");
	    forwardToPublisher(getLastPublisher());
	}
    }

    /* UTILITIES */
    private void setElencoVersamentoListAndDetailViewMode() {
	getForm().getElenchiVersamentoDetail().setViewMode();
	getForm().getElenchiVersamentoDetail().setStatus(Status.view);
	getForm().getElenchiVersamentoList().setViewMode();
	getForm().getElenchiVersamentoList().setStatus(Status.view);
    }

    public boolean areEquals(String str1, String str2) {
	return (str1 == null ? str2 == null : str1.equals(str2));
    }

    private void reloadElenchiVersamentoList(
	    ElenchiVersamentoForm.FiltriElenchiVersamento filtriElenchiVersamento) throws EMFError {
	int paginaCorrente = getForm().getElenchiVersamentoList().getTable().getCurrentPageIndex();
	int inizio = getForm().getElenchiVersamentoList().getTable().getFirstRowPageIndex();
	int pageSize = getForm().getElenchiVersamentoList().getTable().getPageSize();

	VolumiValidator validator = new VolumiValidator(getMessageBox());
	Object[] chiavi = validator.validaChiaveUnitaDocVolumi(
		filtriElenchiVersamento.getCd_registro_key_unita_doc().parse(),
		filtriElenchiVersamento.getAa_key_unita_doc().parse(),
		filtriElenchiVersamento.getCd_key_unita_doc().parse(),
		filtriElenchiVersamento.getAa_key_unita_doc_da().parse(),
		filtriElenchiVersamento.getAa_key_unita_doc_a().parse(),
		filtriElenchiVersamento.getCd_key_unita_doc_da().parse(),
		filtriElenchiVersamento.getCd_key_unita_doc_a().parse());

	if (chiavi != null
		|| !filtriElenchiVersamento.getTi_stato_conservazione().parse().isEmpty()) {
	    // Se sono presenti i filtri dell'UD
	    getForm().getElenchiVersamentoList().setTable(evEjb.getElvVRicElencoVersByUdTableBean(
		    getUser().getIdUtente(), filtriElenchiVersamento));
	} else {
	    // Se non sono presenti i filtri dell'UD
	    List<String> tiStatoElenco = filtriElenchiVersamento.getTi_stato_elenco().parse();
	    if (!tiStatoElenco.isEmpty()
		    && !tiStatoElenco.contains(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())) {
		// Se il filtro di stato elenco esiste e non contiene COMPLETATO
		getForm().getElenchiVersamentoList().setTable(
			evEjb.getElvVRicElencoVersByStatoTableBean(getUser().getIdUtente(),
				filtriElenchiVersamento));
	    } else {
		getForm().getElenchiVersamentoList().setTable(evEjb.getElvVRicElencoVersTableBean(
			getUser().getIdUtente(), filtriElenchiVersamento));
	    }
	}

	getForm().getElenchiVersamentoList().getTable().setPageSize(pageSize);
	getForm().getElenchiVersamentoList().getTable().first();
	// Rieseguo la query se necessario
	this.lazyLoadGoPage(getForm().getElenchiVersamentoList(), paginaCorrente);
	// Ritorno alla pagina
	getForm().getElenchiVersamentoList().getTable().setCurrentRowIndex(inizio);
	getForm().getElenchiVersamentoList().setUserOperations(true, true, false, true);
    }

    /* FINE UTILITIES */
    @Override
    public JSONObject triggerFiltriElenchiDaFirmareId_ambienteOnTrigger() throws EMFError {
	triggerAmbienteGenerico(getForm().getFiltriElenchiDaFirmare(),
		ActionEnums.SezioneElenchi.RICERCA_ELENCHI);
	return getForm().getFiltriElenchiDaFirmare().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiDaFirmareId_enteOnTrigger() throws EMFError {
	triggerEnteGenerico(getForm().getFiltriElenchiDaFirmare(),
		ActionEnums.SezioneElenchi.RICERCA_ELENCHI);
	return getForm().getFiltriElenchiDaFirmare().asJSON();
    }

    public Fields triggerAmbienteGenerico(Fields campi, ActionEnums.SezioneElenchi sezione)
	    throws EMFError {
	campi.post(getRequest());

	// Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
	ComboBox ambienteCombo = (ComboBox) campi.getComponent("id_ambiente");
	ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
	ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

	ComboBox registroUnitaDocCombo = null;
	ComboBox criterioRaggrCombo = null;
	if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
	    registroUnitaDocCombo = (ComboBox) campi.getComponent("cd_registro_key_unita_doc");
	    criterioRaggrCombo = (ComboBox) campi.getComponent("nm_criterio_raggr");
	} else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
	    log.warn("Funzionalità non implementata per la sezione {}",
		    ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	}

	// Azzero i valori preimpostati delle varie combo
	enteCombo.setValue("");
	strutCombo.setValue("");

	if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
	    if (registroUnitaDocCombo != null) {
		registroUnitaDocCombo.setValue("");
	    }
	    if (criterioRaggrCombo != null) {
		criterioRaggrCombo.setValue("");
	    }
	} else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
	    log.warn("Funzionalità non implementata per la sezione {}",
		    ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	}

	BigDecimal idAmbiente = (!ambienteCombo.getValue().equals("")
		? new BigDecimal(ambienteCombo.getValue())
		: null);
	if (idAmbiente != null) {
	    // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
	    OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
		    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
	    DecodeMap mappaEnte = new DecodeMap();
	    mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
	    enteCombo.setDecodeMap(mappaEnte);
	    // Se ho un solo ente lo setto già impostato nella combo
	    if (tmpTableBeanEnte.size() == 1) {
		enteCombo.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
		checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(), sezione);
	    } else {
		strutCombo.setDecodeMap(new DecodeMap());
		if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		    if (registroUnitaDocCombo != null) {
			registroUnitaDocCombo.setDecodeMap(new DecodeMap());
		    }
		    if (criterioRaggrCombo != null) {
			criterioRaggrCombo.setDecodeMap(new DecodeMap());
		    }
		} else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		    log.warn("Funzionalità non implementata per la sezione {}",
			    ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
		}
	    }
	} else {
	    enteCombo.setDecodeMap(new DecodeMap());
	    strutCombo.setDecodeMap(new DecodeMap());
	    if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		if (registroUnitaDocCombo != null) {
		    registroUnitaDocCombo.setDecodeMap(new DecodeMap());
		}
		if (criterioRaggrCombo != null) {
		    criterioRaggrCombo.setDecodeMap(new DecodeMap());
		}
	    } else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		log.warn("Funzionalità non implementata per la sezione {}",
			ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());

	    }
	}
	return campi;
    }

    public Fields triggerEnteGenerico(Fields campi, ActionEnums.SezioneElenchi sezione)
	    throws EMFError {
	campi.post(getRequest());

	// Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
	ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
	ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

	ComboBox registroUnitaDocCombo = null;
	ComboBox criterioRaggrCombo = null;
	if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
	    registroUnitaDocCombo = (ComboBox) campi.getComponent("cd_registro_key_unita_doc");
	    criterioRaggrCombo = (ComboBox) campi.getComponent("nm_criterio_raggr");
	} else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
	    log.warn("Funzionalità non implementata per la sezione {}",
		    ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	}

	// Azzero i valori preimpostati delle varie combo
	strutCombo.setValue("");

	if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
	    if (registroUnitaDocCombo != null) {
		registroUnitaDocCombo.setValue("");
	    }
	    if (criterioRaggrCombo != null) {
		criterioRaggrCombo.setValue("");
	    }
	} else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
	    log.warn("Funzionalità non implementata per la sezione {}",
		    ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	}

	BigDecimal idEnte = (!enteCombo.getValue().equals("") ? new BigDecimal(enteCombo.getValue())
		: null);
	if (idEnte != null) {
	    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
	    OrgStrutTableBean tmpTableBeanStrut = struttureEjb
		    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
	    DecodeMap mappaStrut = new DecodeMap();
	    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
	    strutCombo.setDecodeMap(mappaStrut);
	    // Se ho una sola struttura la setto già impostata nella combo
	    if (tmpTableBeanStrut.size() == 1) {
		strutCombo.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
		checkUniqueStrutInCombo(tmpTableBeanStrut.getRow(0).getIdStrut(), sezione);
	    } else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		if (registroUnitaDocCombo != null) {
		    registroUnitaDocCombo.setDecodeMap(new DecodeMap());
		}
		if (criterioRaggrCombo != null) {
		    criterioRaggrCombo.setDecodeMap(new DecodeMap());
		}
	    } else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		log.warn("Funzionalità non implementata per la sezione {}",
			ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	    }
	} else {
	    strutCombo.setDecodeMap(new DecodeMap());
	    if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		if (registroUnitaDocCombo != null) {
		    registroUnitaDocCombo.setDecodeMap(new DecodeMap());
		}
		if (criterioRaggrCombo != null) {
		    criterioRaggrCombo.setDecodeMap(new DecodeMap());
		}
	    } else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		log.warn("Funzionalità non implementata per la sezione {}",
			ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP.name());
	    }
	}
	return campi;
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ente quando questo è l'unico presente
     * e settare di conseguenza la combo struttura
     *
     * @param idEnte  id ente
     * @param sezione enumerativo
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueEnteInCombo(BigDecimal idEnte, Enum sezione) throws EMFError {
	if (idEnte != null) {
	    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
	    OrgStrutTableBean tmpTableBeanStrut = struttureEjb
		    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
	    DecodeMap mappaStrut = new DecodeMap();
	    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");

	    if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		getForm().getFiltriElenchiDaFirmare().getId_strut().setDecodeMap(mappaStrut);
	    } else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		getForm().getFiltriElenchiIndiciAipDaFirmare().getId_strut()
			.setDecodeMap(mappaStrut);
	    }

	    // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di
	    // essa
	    if (tmpTableBeanStrut.size() == 1) {
		if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI)) {
		    getForm().getFiltriElenchiDaFirmare().getId_strut()
			    .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
		} else if (sezione.equals(ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP)) {
		    getForm().getFiltriElenchiIndiciAipDaFirmare().getId_strut()
			    .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
		}
	    }
	}
    }

    /**
     * Inizializza i FILTRI DI LISTA ELENCHI DA FIRMARE in base alla struttura con la quale l'utente
     * è loggato
     *
     * @throws EMFError errore generico
     */
    private void initFiltriElenchiDaFirmare(BigDecimal idStruttura) {
	// Azzero i filtri
	getForm().getFiltriElenchiDaFirmare().reset();
	// Ricavo id struttura, ente ed ambiente attuali
	BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStruttura);
	BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

	// Inizializzo le combo settando la struttura corrente
	OrgAmbienteTableBean tmpTableBeanAmbiente = null;
	OrgEnteTableBean tmpTableBeanEnte = null;
	OrgStrutTableBean tmpTableBeanStruttura = null;
	try {
	    // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
	    tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

	    // Ricavo i valori della combo ENTE
	    tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
		    idAmbiente.longValue(), Boolean.TRUE);

	    // Ricavo i valori della combo STRUTTURA
	    tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
		    idEnte, Boolean.TRUE);

	} catch (Exception ex) {
	    log.error("Errore in ricerca ambiente", ex);
	}

	DecodeMap mappaAmbiente = new DecodeMap();
	mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
	getForm().getFiltriElenchiDaFirmare().getId_ambiente().setDecodeMap(mappaAmbiente);
	getForm().getFiltriElenchiDaFirmare().getId_ambiente().setValue(idAmbiente.toString());

	DecodeMap mappaEnte = new DecodeMap();
	mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
	getForm().getFiltriElenchiDaFirmare().getId_ente().setDecodeMap(mappaEnte);
	getForm().getFiltriElenchiDaFirmare().getId_ente().setValue(idEnte.toString());

	DecodeMap mappaStrut = new DecodeMap();
	mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
	getForm().getFiltriElenchiDaFirmare().getId_strut().setDecodeMap(mappaStrut);
	getForm().getFiltriElenchiDaFirmare().getId_strut().setValue(idStruttura.toString());

	// Combo elenchi con note, flag fiscale e gestione elenco indici aip
	getForm().getFiltriElenchiDaFirmare().getElenchi_con_note()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltriElenchiDaFirmare().getFl_elenco_fisc()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	boolean flSigilloAttivo = Boolean
		.parseBoolean(configurationHelper.getValoreParamApplicByAmb(
			CostantiDB.ParametroAppl.FL_ABILITA_SIGILLO, idAmbiente));
	flSigilloAttivo = false; // FORZATO PERCHE' NON è ancora implementata la modifica a questa
				 // funzione! (basterà
	// cancellare questa riga!
	getForm().getFiltriElenchiDaFirmare().getTi_gest_elenco()
		.setDecodeMap(ComboGetter.getMappaTiGestElencoCriterio(flSigilloAttivo));
	// Imposto la combo tipo validazione e, se vuota, il valore di default
	// MEV#31945 - Eliminare validazione elenco UD con firma
	getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco()
		.setDecodeMap(ComboUtil.getTipiValidazioneElencoSenzaFirma());
	// getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().setDecodeMap(ComboGetter.getMappaTiValidElenco());
	if (getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().getValues() == null
		|| getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().getValues()
			.isEmpty()) {
	    String[] values = {
		    ElvElencoVer.TiValidElenco.NO_FIRMA.name(),
		    ElvElencoVer.TiValidElenco.NO_INDICE.name() };
	    getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().setValues(values);
	}
	// Imposto la combo modalità di validazione e, se vuota, il valore di default
	getForm().getFiltriElenchiDaFirmare().getTi_mod_valid_elenco()
		.setDecodeMap(ComboGetter.getMappaTiModValidElenco());
	if (getForm().getFiltriElenchiDaFirmare().getTi_mod_valid_elenco().getValue() == null) {
	    getForm().getFiltriElenchiDaFirmare().getTi_mod_valid_elenco()
		    .setValue(ElvElencoVer.TiModValidElenco.MANUALE.name());
	}

	// Imposto i filtri in editMode
	getForm().getFiltriElenchiDaFirmare().setEditMode();

	// Imposto come visibile il bottone di ricerca criteri di raggruppamento e disabilito la
	// clessidra (per IE)
	getForm().getFiltriElenchiDaFirmare().getRicercaElenchiDaFirmareButton().setEditMode();
	getForm().getFiltriElenchiDaFirmare().getRicercaElenchiDaFirmareButton()
		.setDisableHourGlass(true);
    }

    public void ricercaElenchiDaFirmare(FiltriElenchiDaFirmare filtriElenchiDaFirmare)
	    throws EMFError {
	BigDecimal idAmbiente = getForm().getFiltriElenchiDaFirmare().getId_ambiente().parse();
	BigDecimal idEnte = getForm().getFiltriElenchiDaFirmare().getId_ente().parse();
	BigDecimal idStrut = getForm().getFiltriElenchiDaFirmare().getId_strut().parse();

	getSession().setAttribute("idStrutRif", idStrut);
	if (idStrut == null) {
	    // Rimuovo l'attributo idStrutRif. Se presente in sessione vuol dire che si riferisce ad
	    // una struttura
	    // selezionata precedentemente
	    getSession().removeAttribute("idStrutRif");
	    // Traccio in sessione un attributo specifico
	    getSession().setAttribute("isStrutNull", true);
	}

	BigDecimal idElencoVers = getForm().getFiltriElenchiDaFirmare().getId_elenco_vers().parse();
	String presenzaNote = getForm().getFiltriElenchiDaFirmare().getElenchi_con_note().parse();
	String flElencoFisc = getForm().getFiltriElenchiDaFirmare().getFl_elenco_fisc().parse();
	ArrayList<String> tiGestElenco = new ArrayList();
	String appoggio = getForm().getFiltriElenchiDaFirmare().getTi_gest_elenco().parse();
	if (appoggio != null) {
	    tiGestElenco.add(appoggio);
	}
	if (getForm().getFiltriElenchiDaFirmare().validate(getMessageBox())) {
	    if (!getMessageBox().hasError()) {
		ElenchiVersamentoValidator elenchiValidator = new ElenchiVersamentoValidator(
			getMessageBox());
		elenchiValidator.validaTipoValidazione(
			getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().parse());
		// Valido i filtri data creazione elenco da - a restituendo le date comprensive di
		// orario
		Date[] dateCreazioneElencoValidate = elenchiValidator.validaDate(
			filtriElenchiDaFirmare.getDt_creazione_elenco_da().parse(),
			filtriElenchiDaFirmare.getOre_dt_creazione_elenco_da().parse(),
			filtriElenchiDaFirmare.getMinuti_dt_creazione_elenco_da().parse(),
			filtriElenchiDaFirmare.getDt_creazione_elenco_a().parse(),
			filtriElenchiDaFirmare.getOre_dt_creazione_elenco_a().parse(),
			filtriElenchiDaFirmare.getMinuti_dt_creazione_elenco_a().parse(),
			filtriElenchiDaFirmare.getDt_creazione_elenco_da().getHtmlDescription(),
			filtriElenchiDaFirmare.getDt_creazione_elenco_a().getHtmlDescription());
		if (!getMessageBox().hasError()) {
		    /*
		     * Carico la lista degli elenchi di versamento da firmare: quelli della
		     * struttura dell'utente e con stato CHIUSO
		     */
		    ElvVLisElencoVersStatoTableBean elenchiTableBean = evEjb
			    .getElenchiDaFirmareTableBean(idAmbiente, idEnte, idStrut, idElencoVers,
				    presenzaNote, flElencoFisc, tiGestElenco,
				    dateCreazioneElencoValidate, getUser().getIdUtente(),
				    ElencoEnums.ElencoStatusEnum.CHIUSO);
		    /*
		     * Filtro gli elenchi mediante tipo validazione = FIRMA/NO_FIRMA e modalità di
		     * validazione = MANUALE/AUTOMATICA
		     */
		    Iterator<ElvVLisElencoVersStatoRowBean> iterator = elenchiTableBean.iterator();
		    while (iterator.hasNext()) {
			ElvVLisElencoVersStatoRowBean elencoRowBean = iterator.next();
			String tiValidElenco = elencoRowBean.getTiValidElenco();
			String tiModValidElenco = elencoRowBean.getTiModValidElenco();
			if (!getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().parse()
				.contains(tiValidElenco)
				|| (getForm().getFiltriElenchiDaFirmare().getTi_mod_valid_elenco()
					.parse() != null
					&& !getForm().getFiltriElenchiDaFirmare()
						.getTi_mod_valid_elenco().parse()
						.equals(tiModValidElenco))) {
			    iterator.remove();
			}
		    }
		    getForm().getElenchiVersamentoDaFirmareList().setTable(elenchiTableBean);
		    getForm().getElenchiVersamentoDaFirmareList().getTable().setPageSize(10);
		    getForm().getElenchiVersamentoDaFirmareList().getTable().first();
		    getForm().getElenchiVersamentoDaFirmareList().getTable()
			    .addSortingRule(getForm().getElenchiVersamentoDaFirmareList()
				    .getDt_creazione_elenco().getName(), SortingRule.ASC);
		    /* Inizializzo la lista degli elenchi di versamento selezionati */
		    getForm().getElenchiVersamentoSelezionatiList()
			    .setTable(new ElvVLisElencoVersStatoTableBean());
		    getForm().getElenchiVersamentoSelezionatiList().getTable().setPageSize(10);
		    getForm().getElenchiVersamentoSelezionatiList().getTable()
			    .addSortingRule(getForm().getElenchiVersamentoSelezionatiList()
				    .getDt_creazione_elenco().getName(), SortingRule.ASC);

		    if (getForm().getFiltriElenchiDaFirmare().getTi_valid_elenco().parse()
			    .equals(ElvElencoVer.TiValidElenco.FIRMA.name())) {
			// MEV#31945 - Eliminare validazione elenco UD con firma
			// getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton()
			// .setHidden(false);
			getForm().getListaElenchiVersamentoDaFirmareButtonList()
				.getValidaElenchiButton().setHidden(true);
		    } else {
			/* NO_FIRMA */
			// MEV#31945 - Eliminare validazione elenco UD con firma
			// getForm().getListaElenchiVersamentoDaFirmareButtonList().getFirmaElenchiHsmButton()
			// .setHidden(true);
			getForm().getListaElenchiVersamentoDaFirmareButtonList()
				.getValidaElenchiButton().setHidden(false);
		    }

		    /* Rengo visibili i bottoni delle operazioni sulla lista che mi interessano */
		    getForm().getListaElenchiVersamentoDaFirmareButtonList().setEditMode();
		    getForm().getElenchiVersamentoList().setUserOperations(true, false, false,
			    false);
		}
	    }
	}

	Button validaBtn = getForm().getListaElenchiVersamentoDaFirmareButtonList()
		.getValidaElenchiButton();
	if (validaBtn.isHidden()) {
	    getSession().setAttribute("tiValidElenco", ElvElencoVer.TiValidElenco.FIRMA.name());
	} else {
	    getSession().setAttribute("tiValidElenco", ElvElencoVer.TiValidElenco.NO_FIRMA.name());
	}

	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    }

    @Override
    public void ricercaElenchiDaFirmareButton() throws EMFError {
	FiltriElenchiDaFirmare filtriElenchiDaFirmare = getForm().getFiltriElenchiDaFirmare();
	filtriElenchiDaFirmare.post(getRequest());
	ricercaElenchiDaFirmare(filtriElenchiDaFirmare);
    }

    @Override
    public void selectAllElenchiButton() throws Throwable {
	ElvVLisElencoVersStatoTableBean elenchi = (ElvVLisElencoVersStatoTableBean) getForm()
		.getElenchiVersamentoDaFirmareList().getTable();
	for (ElvVLisElencoVersStatoRowBean elenco : elenchi) {
	    getForm().getElenchiVersamentoSelezionatiList().getTable().add(elenco);
	}
	elenchi.removeAll();
	getForm().getElenchiVersamentoSelSection().setLoadOpened(true);
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    }

    @Override
    public void deselectAllElenchiButton() throws Throwable {
	ElvVLisElencoVersStatoTableBean elenchi = (ElvVLisElencoVersStatoTableBean) getForm()
		.getElenchiVersamentoSelezionatiList().getTable();
	for (ElvVLisElencoVersStatoRowBean elenco : elenchi) {
	    getForm().getElenchiVersamentoDaFirmareList().getTable().add(elenco);
	}
	elenchi.removeAll();
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    }

    @Override
    public void selectHundredElenchiButton() throws Throwable {
	ElvVLisElencoVersStatoTableBean elenchi = (ElvVLisElencoVersStatoTableBean) getForm()
		.getElenchiVersamentoDaFirmareList().getTable();
	if (elenchi != null) {
	    if (elenchi.size() <= 100) {
		selectAllElenchiButton();
	    } else {
		for (int counter = 0; counter < 100; counter++) {
		    if (!elenchi.isEmpty()) {
			ElvVLisElencoVersStatoRowBean elenco = elenchi.getRow(0);
			getForm().getElenchiVersamentoSelezionatiList().getTable().add(elenco);
			elenchi.remove(0);
		    } else {
			break;
		    }
		}
		getForm().getElenchiVersamentoSelSection().setLoadOpened(true);
		forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
	    }
	}
    }

    // MEV#31945 - Eliminare validazione elenco UD con firma
    /**
     * Invoca il meccanismo di firma del HSM.
     *
     * @throws EMFError errore generico
     *
     */
    // @Override
    // public void firmaElenchiHsmButton() throws Throwable {
    // ElvElencoVerTableBean elenchiDaFirmare = checkElenchiVersamentoToSign();
    // int elenchiHsmEliminati = 0;
    //
    // /* Richiedo le credenziali del HSM utilizzando apposito popup */
    // if (!getMessageBox().hasError()) {
    // // Ricavo l'id ambiente da un qualsiasi record degli elenchi da firmare
    // // PS: non lo prendo dal filtro di ricerca perchè l'utente potrebbe cambiarlo dalla combo
    // senza fare la
    // // ricerca
    // // e così verrebbe preso un ambiente errato
    // BigDecimal idStrut = elenchiDaFirmare.getRow(0).getIdStrut();
    // OrgAmbienteRowBean ambienteRowBean = struttureEjb.getOrgAmbienteRowBeanByIdStrut(idStrut);
    // BigDecimal idAmbiente = ambienteRowBean.getIdAmbiente();
    // if (idAmbiente != null) {
    // // Ricavo il parametro HSM_USERNAME (parametro multiplo dell'ambiente) associato all'utente
    // corrente
    // String hsmUserName = amministrazioneEjb.getHsmUsername(getUser().getIdUtente(), idAmbiente);
    // if (hsmUserName != null) {
    //
    // List<BigDecimal> idElencoVersRigheTotali = new ArrayList<>();
    // List<BigDecimal> idElencoVersRigheCancellate = new ArrayList<>();
    //
    // for (int i = 0; i < elenchiDaFirmare.size(); i++) {
    // ElvElencoVerRowBean elenco = elenchiDaFirmare.getRow(i);
    // BigDecimal idElencoVers = elenco.getIdElencoVers();
    // idElencoVersRigheTotali.add(idElencoVers);
    // // EVO 19304: prima dei controlli sulla vista ELV_V_CHK_UNA_UD_ANNUL (evEjb.almenoUnaUdAnnul)
    // // e relativa gestione delle ud
    // // Il sistema registra lo stato dell’elenco creato
    // evEjb.registraStatoElencoVersamento(idElencoVers, "RICHIESTA_FIRMA_INDICE_ELENCO_VERS",
    // "Richiesta firma indice elenco di versamento",
    // ElvStatoElencoVer.TiStatoElenco.FIRMA_IN_CORSO, getUser().getUsername());
    //
    // if (evEjb.almenoUnaUdAnnul(idElencoVers)) {
    // // Se lo sono, annullo lo stato di generazione indice AIP di ud e doc ed elimino l'elenco
    // evEjb.manageElencoUdAnnulDaFirmaElenco(idElencoVers, getUser().getIdUtente());
    // idElencoVersRigheCancellate.add(idElencoVers);
    // elenchiHsmEliminati++;
    // }
    // }
    //
    // // Elimino a video gli elenchi cancellati su DB in quanto contenenti solo ud annullate
    // idElencoVersRigheTotali.removeAll(idElencoVersRigheCancellate);
    // ElvVLisElencoVersStatoTableBean elenchiRimanenti = evEjb
    // .getElenchiDaFirmareTableBean(idElencoVersRigheTotali, getUser().getIdUtente());
    // getForm().getElenchiVersamentoSelezionatiList().setTable(elenchiRimanenti);
    //
    // if (elenchiHsmEliminati > 0) {
    // getMessageBox().setViewMode(ViewMode.plain);
    // getMessageBox().addInfo("Sono stati eliminati " + elenchiHsmEliminati
    // + " elenchi di versamento in quanto contenenti almeno una unità documentaria annullata");
    // }
    //
    // if (getForm().getElenchiVersamentoSelezionatiList().getTable().size() > 0) {
    // getRequest().setAttribute("customElenchiVersamentoSelect", true);
    // getForm().getFiltriElenchiDaFirmare().getUser().setValue(hsmUserName);
    // getForm().getFiltriElenchiDaFirmare().getUser().setViewMode();
    // }
    // }
    // } else {
    // getMessageBox().addError("Utente non rientra tra i firmatari definiti sull’ambiente");
    // }
    // }
    //
    // forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERSAMENTO_SELECT);
    // }

    /**
     * Signs the list of file selected
     *
     * @throws EMFError errore generico
     *
     */
    public void firmaElenchiHsmJs() throws EMFError {
	List<String> errorList = new ArrayList<>();
	JSONObject result = new JSONObject();

	// Recupero informazioni riguardo all'Utente (idSacer e credenziali HSM)
	long idUtente = SessionManager.getUser(getSession()).getIdUtente();

	getForm().getFiltriElenchiDaFirmare().post(getRequest());
	String user = getForm().getFiltriElenchiDaFirmare().getUser().parse();
	char[] passwd = getForm().getFiltriElenchiDaFirmare().getPasswd().parse() != null
		? getForm().getFiltriElenchiDaFirmare().getPasswd().parse().toCharArray()
		: null;
	char[] otp = getForm().getFiltriElenchiDaFirmare().getOtp().parse() != null
		? getForm().getFiltriElenchiDaFirmare().getOtp().parse().toCharArray()
		: null;

	if (StringUtils.isBlank(user)) {
	    errorList.add("Il campo \"Utente\" non può essere vuoto.");
	}
	if (passwd == null || passwd.length == 0) {
	    errorList.add("Il campo \"Password\" non può essere vuoto.");
	}
	if (otp == null || otp.length == 0) {
	    errorList.add("Il campo \"OTP\" non può essere vuoto.");
	}

	if (elencoSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())) {
	    getMessageBox().addError("Sessione di firma attiva");
	}

	ElvElencoVerTableBean elenchiDaFirmare = checkElenchiVersamentoToSign();
	try {
	    if (errorList.isEmpty() && !getMessageBox().hasError() && elenchiDaFirmare != null) {
		SigningRequest request = new SigningRequest(idUtente);
		HSMUser userHSM = new HSMUser(user, passwd);
		userHSM.setOTP(otp);
		request.setUserHSM(userHSM);
		request.setType(TiSessioneFirma.ELENCHI);
		for (ElvElencoVerRowBean elenco : elenchiDaFirmare) {
		    BigDecimal idElenco = elenco.getIdElencoVers();
		    request.addFile(idElenco);
		}
		Future<SigningResponse> provaAsync = firmaHsmEjb.signP7MRequest(request);
		getSession().setAttribute(Signature.FUTURE_ATTR_ELENCHI, provaAsync);
	    }
	    if (errorList.isEmpty() && !result.has("status")) {
		result.put("info", "Sessione di firma avviata!");
	    } else if (!errorList.isEmpty()) {
		result.put("error", errorList);
	    }
	} catch (JSONException ex) {
	    log.error(
		    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma dei file",
		    ex);
	    getMessageBox().addError("Errore inatteso nel recupero e firma dei file");
	}
	if (!getMessageBox().hasError()) {
	    redirectToAjax(result);
	} else {
	    forwardToPublisher(getLastPublisher());
	}
    }

    private ElvElencoVerTableBean checkElenchiVersamentoToSign() {
	ElvElencoVerTableBean result = null;

	if (getForm().getElenchiVersamentoSelezionatiList().getTable().isEmpty()) {
	    Button validaBtn = getForm().getListaElenchiVersamentoDaFirmareButtonList()
		    .getValidaElenchiButton();
	    String tiValidMsg = "";
	    if (validaBtn.isHidden()) {
		tiValidMsg = "firmare";
	    } else {
		tiValidMsg = "validare";
	    }
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Selezionare almeno un elenco di versamento da " + tiValidMsg));
	} else {

	    List<String> organizNoPartitionList = new ArrayList<>();
	    ElvElencoVerTableBean elenchiDaFirmare = new ElvElencoVerTableBean();
	    /* Per ogni elenco di versamento da firmare selezionato, eseguo i controlli */
	    for (ElvVLisElencoVersStatoRowBean elencoVista : (ElvVLisElencoVersStatoTableBean) getForm()
		    .getElenchiVersamentoSelezionatiList().getTable()) {
		BigDecimal idElencoVers = elencoVista.getIdElencoVers();
		ElvElencoVerRowBean elencoVers = evEjb.getElvElencoVersRowBean(idElencoVers);
		elenchiDaFirmare.add(elencoVers);
		/* Verifico il valore di niUnitaDocModElenco */
		if (elencoVers.getNiUnitaDocModElenco().longValue() > 0) {
		    /*
		     * Controllo se tutti gli elenchi con data chiusura precedente non presenti nei
		     * selezionati sono stati validati
		     */
		    boolean areAllElenchiNonPresentiFirmati = evEjb.areAllElenchiNonPresentiFirmati(
			    (ElvVLisElencoVersStatoTableBean) getForm()
				    .getElenchiVersamentoSelezionatiList().getTable(),
			    elencoVers.getDtChius(), elencoVers.getIdStrut());
		    if (!areAllElenchiNonPresentiFirmati) {
			log.warn("Non tutti gli elenchi sono firmati!");

		    }
		}
	    }
	    // Se ho delle strutture la cui "verifica partizione" non è andata a buon fine per i
	    // file degli elenchi...
	    if (!organizNoPartitionList.isEmpty()) {
		StringBuilder errorMessage = new StringBuilder(
			"La partizione di tipo FILE_ELENCHI_VERS per la data corrente e le strutture: ");
		for (String organizNoPartition : organizNoPartitionList) {
		    errorMessage.append(organizNoPartition).append("<br>");
		}
		errorMessage.append("non è definita");
		getMessageBox().addMessage(new Message(MessageLevel.ERR, errorMessage.toString()));
	    }

	    // Se non ci sono problemi, procedo alla firma
	    if (!getMessageBox().hasError()) {
		result = elenchiDaFirmare;
	    }
	}
	return result;
    }

    private ElvElencoVerTableBean checkElenchiIndiciAipToSign() {
	ElvElencoVerTableBean result = null;
	if (getForm().getElenchiIndiciAipSelezionatiList().getTable().isEmpty()) {
	    getMessageBox().addError("Selezionare almeno un elenco indice AIP da firmare");
	} else {
	    List<String> organizNoPartitionList = new ArrayList<>();
	    ElvElencoVerTableBean elenchiDaFirmare = new ElvElencoVerTableBean();
	    /* Per ogni elenco di versamento da firmare selezionato, eseguo i controlli */
	    for (ElvVLisElencoVersStatoRowBean elencoVista : (ElvVLisElencoVersStatoTableBean) getForm()
		    .getElenchiIndiciAipSelezionatiList().getTable()) {
		BigDecimal idElencoVers = elencoVista.getIdElencoVers();
		ElvElencoVerRowBean elencoVers = evEjb.getElvElencoVersRowBean(idElencoVers);
		elenchiDaFirmare.add(elencoVers);
	    }
	    // Se ho delle strutture la cui "verifica partizione" non è andata a buon fine per i
	    // file degli elenchi...
	    if (!organizNoPartitionList.isEmpty()) {
		StringBuilder errorMessage = new StringBuilder(
			"La partizione di tipo FILE_ELENCHI_VERS per la data corrente e le strutture: ");
		for (String organizNoPartition : organizNoPartitionList) {
		    errorMessage.append(organizNoPartition).append("<br>");
		}
		errorMessage.append("non è definita");
		getMessageBox().addError(errorMessage.toString());
	    }

	    // Se non ci sono problemi, procedo alla firma
	    if (!getMessageBox().hasError()) {
		result = elenchiDaFirmare;
	    }
	}
	return result;
    }

    @SuppressLogging
    public void checkSignatureFuture() {
	Future<SigningResponse> futureObj = (Future<SigningResponse>) getSession()
		.getAttribute(Signature.FUTURE_ATTR_ELENCHI);
	try {
	    JSONObject result = new JSONObject();
	    result.put("status", "NO_SESSION");
	    if (futureObj != null) {
		if (futureObj.isDone()) {
		    SigningResponse resp = futureObj.get();
		    result.put("status", resp.name());
		    switch (resp) {
		    case ACTIVE_SESSION_YET:
		    case AUTH_WRONG:
		    case OTP_WRONG:
		    case OTP_EXPIRED:
		    case USER_BLOCKED:
		    case HSM_ERROR:
		    case UNKNOWN_ERROR:
			result.put("error", resp.getDescription());
			break;
		    case WARNING:
		    case OK:
			result.put("info", resp.getDescription());
			getForm().getElenchiVersamentoSelezionatiList().getTable().clear();
			break;
		    default:
			getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI);
			throw new AssertionError(resp.name());
		    }
		    getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI);
		} else {
		    result.put("status", "WORKING");
		}
	    }
	    redirectToAjax(result);
	} catch (InterruptedException ex) {
	    Thread.currentThread().interrupt(); // Ripristina lo stato di interruzione
	    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
	    forwardToPublisher(getLastPublisher());
	} catch (ExecutionException | JSONException ex) {
	    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
	    forwardToPublisher(getLastPublisher());
	}
    }

    @SuppressLogging
    public void checkSignatureIndiciAipFuture() {
	Future<SigningResponse> futureObj = (Future<SigningResponse>) getSession()
		.getAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
	try {
	    JSONObject result = new JSONObject();
	    result.put("status", "NO_SESSION");
	    if (futureObj != null) {
		if (futureObj.isDone()) {
		    SigningResponse resp = futureObj.get();
		    result.put("status", resp.name());
		    switch (resp) {
		    case ACTIVE_SESSION_YET:
		    case AUTH_WRONG:
		    case OTP_WRONG:
		    case OTP_EXPIRED:
		    case USER_BLOCKED:
		    case HSM_ERROR:
		    case UNKNOWN_ERROR:
			result.put("error", resp.getDescription());
			getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
			break;
		    case WARNING:
		    case OK:
			// MAC#35254 - Correzione delle anomalie nella fase di marcatura temporale
			// embedded negli
			// elenchi indici aip UD
			// result.put("info", resp.getDescription()
			// + "! Cliccare per completare il processo di firma (con l'eventuale
			// apposizione di marche
			// temporali, se prevista)");
			BigDecimal idAmbiente = getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getId_ambiente().parse();
			BigDecimal idEnte = getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getId_ente().parse();
			BigDecimal idStrut = getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getId_strut().parse();
			Future<SigningResponse> futureSecondaFase = null;
			try {
			    futureSecondaFase = evEjb.completaFirmaElenchiIndiciAipAsync(idAmbiente,
				    idEnte, idStrut, getUser().getIdUtente(), false);
			    getSession().setAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP,
				    futureSecondaFase);
			    result.put("status", "WORKING");
			} catch (NullPointerException ex) {
			    result.put("status", "ERROR");
			    result.put("error",
				    "Attenzione! Il completamento del processo di firma è terminato in errore a causa di un problema");
			    log.error("Errore imprevisto durante sessione di firma: ", ex);
			    getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
			}
			// FINE MAC ---
			// getForm().getElenchiIndiciAipSelezionatiList().getTable().clear();
			break;

		    case OK_SECONDA_FASE:
			result.put("status", "OK");
			result.put("info",
				"Il processo di firma (inclusa eventuale marcatura temporale) degli Elenchi Indici AIP è terminata correttamente");
			getForm().getElenchiIndiciAipSelezionatiList().getTable().clear();
			getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
			break;

		    case ERROR_COMPLETAMENTO_FIRMA:
			result.put("status", "ERROR");
			result.put("error", ERROR_COMPLETAMENTO_FIRMA.getDescription());
			getForm().getElenchiIndiciAipSelezionatiList().getTable().clear();
			getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
			break;

		    default:
			getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
			throw new AssertionError(resp.name());
		    }
		    // getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
		} else {
		    result.put("status", "WORKING");
		}
	    }
	    redirectToAjax(result);
	} catch (ExecutionException | JSONException | EMFError ex) {
	    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
	    forwardToPublisher(getLastPublisher());
	} catch (InterruptedException ex) {
	    getMessageBox().addError(ExceptionUtils.getRootCauseMessage(ex));
	    forwardToPublisher(getLastPublisher());
	    // come suggerito sa Sonar.
	    log.warn("Interrupted!", ex);
	    Thread.currentThread().interrupt();
	}
    }

    @Override
    public void scaricaElencoIndiciAipButton() throws Throwable {
	BigDecimal idElencoVers = getForm().getElenchiVersamentoDetail().getId_elenco_vers()
		.parse();
	String nmAmbiente = getForm().getElenchiVersamentoDetail().getNm_ambiente().parse();
	String nmEnte = getForm().getElenchiVersamentoDetail().getNm_ente().parse();
	String nmStrut = getForm().getElenchiVersamentoDetail().getNm_strut().parse();
	String filesSuffix = nmAmbiente + "_" + nmEnte + "_" + nmStrut + "_" + idElencoVers;
	/* Comincio a costruire lo zippone */
	String nomeZippone = "ElencoIndiciAIP_"
		+ StringUtils.defaultString(filesSuffix).replace(" ", "_");
	getResponse().setContentType("application/zip");
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeZippone + ".zip");
	try (ZipOutputStream out = new ZipOutputStream(getServletOutputStream())) {
	    evEjb.streamOutFileIndiceElenco(out, "", filesSuffix, idElencoVers.longValue(),
		    FileTypeEnum.getElencoIndiciFileTypes());
	    freeze();
	} catch (Exception e) {
	    log.error("Eccezione", e);
	    getMessageBox().addError("Errore nel recupero dei file delle prove di conservazione");
	}
    }

    @Secure(action = "Menu.ElenchiVersamento.ListaElenchiIndiciAipDaFirmare")
    public void loadListaElenchiIndiciAipDaFirmare() throws EMFError {
	/*
	 * Controllo lo stato della history di navigazione se non ci sono pagine precedenti, vuol
	 * dire che arrivo qui da un link del menu, se ci sono pagine allora devo passare alla jsp
	 * l'id della struttura
	 */
	boolean cleanList = false;
	if (getRequest().getParameter("cleanhistory") != null) {
	    getUser().getMenu().reset();
	    getUser().getMenu().select("Menu.ElenchiVersamento.ListaElenchiIndiciAipDaFirmare");
	    // Rimuovo l'attributo perchè arrivo da un link del menu e non da una lista
	    getSession().removeAttribute("idStrutRif");
	    getSession().removeAttribute("isStrutNull");
	    cleanList = true;
	}

	/* Ricavo Ambiente, Ente e Struttura da visualizzare */
	BigDecimal idStrut;
	if (getRequest().getParameter("idStrut") != null) {
	    idStrut = new BigDecimal(getRequest().getParameter("idStrut"));
	    cleanList = true;
	} else if (getSession().getAttribute("idStrutRif") != null) {
	    idStrut = (BigDecimal) getSession().getAttribute("idStrutRif");
	} else if (getSession().getAttribute("isStrutNull") != null) {
	    idStrut = null;
	} else {
	    idStrut = getIdStrutCorrente();
	    cleanList = true;
	}

	boolean cleanFilter = true;
	if (getRequest().getParameter("cleanFilter") != null) {
	    cleanFilter = false;
	}

	if (idStrut != null && cleanFilter) {
	    OrgStrutRowBean strut = evEjb.getOrgStrutRowBeanWithAmbienteEnte(idStrut);
	    /* Inizializza le combo dei filtri ambiente/ente/struttura */
	    initFiltriElenchiIndiciAipDaFirmare(strut.getIdStrut());

	    if (cleanList) {
		if (getForm().getFiltriElenchiIndiciAipDaFirmare().validate(getMessageBox())) {
		    if (!getMessageBox().hasError()) {
			ElenchiVersamentoValidator elenchiValidator = new ElenchiVersamentoValidator(
				getMessageBox());
			// Valido i filtri data creazione elenco indici aip da - a restituendo le
			// date comprensive di
			// orario
			Date[] dateCreazioneElencoIdxAipValidate = elenchiValidator.validaDate(
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getDt_creazione_elenco_idx_aip_da().parse(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getOre_dt_creazione_elenco_idx_aip_da().parse(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getMinuti_dt_creazione_elenco_idx_aip_da().parse(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getDt_creazione_elenco_idx_aip_a().parse(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getOre_dt_creazione_elenco_idx_aip_a().parse(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getMinuti_dt_creazione_elenco_idx_aip_a().parse(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getDt_creazione_elenco_idx_aip_da().getHtmlDescription(),
				getForm().getFiltriElenchiIndiciAipDaFirmare()
					.getDt_creazione_elenco_idx_aip_a().getHtmlDescription());
			if (!getMessageBox().hasError()) {
			    /*
			     * Carico la lista degli elenchi di versamento da firmare: quelli della
			     * struttura dell'utente e con stato ELENCO_INDICI_AIP_CREATO
			     */
			    List<String> tiGestElenco = getForm()
				    .getFiltriElenchiIndiciAipDaFirmare().getTi_gest_elenco()
				    .parse();
			    ElvVLisElencoVersStatoTableBean elenchiTableBean = evEjb
				    .getElenchiDaFirmareTableBean(
					    strut.getBigDecimal("id_ambiente"), strut.getIdEnte(),
					    strut.getIdStrut(), null,
					    getForm().getFiltriElenchiDaFirmare()
						    .getElenchi_con_note().parse(),
					    getForm().getFiltriElenchiDaFirmare()
						    .getFl_elenco_fisc().parse(),
					    tiGestElenco, dateCreazioneElencoIdxAipValidate,
					    getUser().getIdUtente(),
					    ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO);
			    getForm().getElenchiIndiciAipDaFirmareList().setTable(elenchiTableBean);
			    getForm().getElenchiIndiciAipDaFirmareList().getTable().setPageSize(10);
			    getForm().getElenchiIndiciAipDaFirmareList().getTable().first();
			    getForm().getElenchiIndiciAipDaFirmareList().getTable().addSortingRule(
				    getForm().getElenchiIndiciAipDaFirmareList()
					    .getDt_creazione_elenco_ix_aip().getName(),
				    SortingRule.ASC);

			    /* Inizializzo la lista degli elenchi di versamento selezionati */
			    getForm().getElenchiIndiciAipSelezionatiList()
				    .setTable(new ElvVLisElencoVersStatoTableBean());
			    getForm().getElenchiIndiciAipSelezionatiList().getTable()
				    .setPageSize(10);
			    getForm().getElenchiIndiciAipSelezionatiList().getTable()
				    .addSortingRule(
					    getForm().getElenchiIndiciAipSelezionatiList()
						    .getDt_creazione_elenco_ix_aip().getName(),
					    SortingRule.ASC);
			}
		    }
		}
	    }
	}

	getForm().getElenchiIndiciAipDaFirmareButtonList().setEditMode();

	// Check if some signature session is active
	Future<Boolean> futureFirma = (Future<Boolean>) getSession()
		.getAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP);
	// Verifico su db la presenza della sessione di firma o di un oggetto future (di una
	// possibile sessione di firma
	// preesistente) in sessione
	if (elencoIndiciAipSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())
		|| futureFirma != null) {
	    // Se esistono delle sessioni bloccate per quell'utente le sblocco
	    if (elencoIndiciAipSignSessionEjb.hasUserBlockedSessions(getUser().getIdUtente())) {
		// Sessione di firma bloccata
		elencoIndiciAipSignSessionEjb.unlockBlockedSessions(getUser().getIdUtente());

		getForm().getElenchiIndiciAipDaFirmareButtonList().getFirmaElenchiIndiciAipHsm()
			.setReadonly(false);
		getMessageBox().addInfo("\u00C8 stata sbloccata una sessione di firma bloccata");
		getMessageBox().setViewMode(ViewMode.plain);
	    } else {
		getForm().getElenchiIndiciAipDaFirmareButtonList().getFirmaElenchiIndiciAipHsm()
			.setReadonly(true);
		// Sessione di firma attiva
		getMessageBox().addInfo("Sessione di firma attiva");
		getMessageBox().setViewMode(ViewMode.plain);
	    }
	} else {
	    getForm().getElenchiIndiciAipDaFirmareButtonList().getFirmaElenchiIndiciAipHsm()
		    .setReadonly(false);
	}
	getForm().getElenchiIndiciAipDaFirmareButtonList().getFirmaElenchiIndiciAipHsm()
		.setHidden(false);

	getSession().setAttribute("idStrutRif", idStrut);
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    @Override
    public JSONObject triggerFiltriElenchiIndiciAipDaFirmareId_ambienteOnTrigger() throws EMFError {
	triggerAmbienteGenerico(getForm().getFiltriElenchiIndiciAipDaFirmare(),
		ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP);
	return getForm().getFiltriElenchiIndiciAipDaFirmare().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiIndiciAipDaFirmareId_enteOnTrigger() throws EMFError {
	triggerEnteGenerico(getForm().getFiltriElenchiIndiciAipDaFirmare(),
		ActionEnums.SezioneElenchi.RICERCA_ELENCHI_INDICI_AIP);
	return getForm().getFiltriElenchiIndiciAipDaFirmare().asJSON();
    }

    @Override
    public void ricercaElenchiIndiciAipDaFirmare() throws Throwable {
	getForm().getFiltriElenchiIndiciAipDaFirmare().post(getRequest());
	BigDecimal idAmbiente = getForm().getFiltriElenchiIndiciAipDaFirmare().getId_ambiente()
		.parse();
	BigDecimal idEnte = getForm().getFiltriElenchiIndiciAipDaFirmare().getId_ente().parse();
	BigDecimal idStrut = getForm().getFiltriElenchiIndiciAipDaFirmare().getId_strut().parse();
	BigDecimal idElencoVers = getForm().getFiltriElenchiIndiciAipDaFirmare().getId_elenco_vers()
		.parse();
	String flElencoFisc = getForm().getFiltriElenchiIndiciAipDaFirmare().getFl_elenco_fisc()
		.parse();
	List<String> tiGestElenco = getForm().getFiltriElenchiIndiciAipDaFirmare()
		.getTi_gest_elenco().parse();

	getSession().setAttribute("idStrutRif", idStrut);
	if (idStrut == null) {
	    // Rimuovo l'attributo idStrutRif se presente in sessione vuol dire che si riferisce ad
	    // una struttura
	    // selezionata precedentemente
	    getSession().removeAttribute("idStrutRif");
	    // Traccio in sessione un attributo specifico
	    getSession().setAttribute("isStrutNull", true);
	}

	if (getForm().getFiltriElenchiIndiciAipDaFirmare().validate(getMessageBox())) {
	    if (!getMessageBox().hasError()) {
		ElenchiVersamentoValidator elenchiValidator = new ElenchiVersamentoValidator(
			getMessageBox());
		// Valido i filtri data creazione elenco indici aip da - a restituendo le date
		// comprensive di orario
		Date[] dateCreazioneElencoIdxAipValidate = elenchiValidator.validaDate(
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getDt_creazione_elenco_idx_aip_da().parse(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getOre_dt_creazione_elenco_idx_aip_da().parse(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getMinuti_dt_creazione_elenco_idx_aip_da().parse(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getDt_creazione_elenco_idx_aip_a().parse(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getOre_dt_creazione_elenco_idx_aip_a().parse(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getMinuti_dt_creazione_elenco_idx_aip_a().parse(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getDt_creazione_elenco_idx_aip_da().getHtmlDescription(),
			getForm().getFiltriElenchiIndiciAipDaFirmare()
				.getDt_creazione_elenco_idx_aip_a().getHtmlDescription());
		if (!getMessageBox().hasError()) {
		    /*
		     * Carico la lista degli elenchi di versamento da firmare: quelli della
		     * struttura dell'utente e con stato ELENCO_INDICI_AIP_CREATO
		     */
		    ElvVLisElencoVersStatoTableBean elenchiTableBean = evEjb
			    .getElenchiDaFirmareTableBean(idAmbiente, idEnte, idStrut, idElencoVers,
				    null, flElencoFisc, tiGestElenco,
				    dateCreazioneElencoIdxAipValidate, getUser().getIdUtente(),
				    ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO);
		    getForm().getElenchiIndiciAipDaFirmareList().setTable(elenchiTableBean);
		    getForm().getElenchiIndiciAipDaFirmareList().getTable().setPageSize(10);
		    getForm().getElenchiIndiciAipDaFirmareList().getTable().first();
		    getForm().getElenchiIndiciAipDaFirmareList().getTable()
			    .addSortingRule(
				    getForm().getElenchiIndiciAipDaFirmareList()
					    .getDt_creazione_elenco_ix_aip().getName(),
				    SortingRule.ASC);

		    /* Inizializzo la lista degli elenchi di versamento selezionati */
		    getForm().getElenchiIndiciAipSelezionatiList()
			    .setTable(new ElvVLisElencoVersStatoTableBean());
		    getForm().getElenchiIndiciAipSelezionatiList().getTable().setPageSize(10);
		    getForm().getElenchiIndiciAipSelezionatiList().getTable()
			    .addSortingRule(
				    getForm().getElenchiIndiciAipSelezionatiList()
					    .getDt_creazione_elenco_ix_aip().getName(),
				    SortingRule.ASC);
		}
	    }
	}
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    @Override
    public void selectAllElenchiIndiciAip() throws Throwable {
	ElvVLisElencoVersStatoTableBean elenchi = (ElvVLisElencoVersStatoTableBean) getForm()
		.getElenchiIndiciAipDaFirmareList().getTable();
	for (ElvVLisElencoVersStatoRowBean elenco : elenchi) {
	    getForm().getElenchiIndiciAipSelezionatiList().getTable().add(elenco);
	}
	elenchi.removeAll();
	getForm().getElenchiIndiciAipSelSection().setLoadOpened(true);
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    @Override
    public void deselectAllElenchiIndiciAip() throws Throwable {
	ElvVLisElencoVersStatoTableBean elenchi = (ElvVLisElencoVersStatoTableBean) getForm()
		.getElenchiIndiciAipSelezionatiList().getTable();
	for (ElvVLisElencoVersStatoRowBean elenco : elenchi) {
	    getForm().getElenchiIndiciAipDaFirmareList().getTable().add(elenco);
	}
	elenchi.removeAll();
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    @Override
    public void selectHundredElenchiIndiciAip() throws Throwable {
	ElvVLisElencoVersStatoTableBean elenchi = (ElvVLisElencoVersStatoTableBean) getForm()
		.getElenchiIndiciAipDaFirmareList().getTable();
	if (elenchi != null) {
	    if (elenchi.size() <= 100) {
		selectAllElenchiIndiciAip();
	    } else {
		for (int counter = 0; counter < 100; counter++) {
		    if (!elenchi.isEmpty()) {
			ElvVLisElencoVersStatoRowBean elenco = elenchi.getRow(0);
			getForm().getElenchiIndiciAipSelezionatiList().getTable().add(elenco);
			elenchi.remove(0);
		    } else {
			break;
		    }
		}
		getForm().getElenchiIndiciAipSelSection().setLoadOpened(true);
		forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
	    }
	}
    }

    @Override
    public void firmaElenchiIndiciAipHsm() throws Throwable {
	checkElenchiIndiciAipToSign();

	/* Richiedo le credenziali del HSM utilizzando apposito popup */
	if (!getMessageBox().hasError()) {

	    // Ricavo l'id ambiente da un qualsiasi record degli elenchi da firmare
	    // PS: non lo prendo dal filtro di ricerca perchè l'utente potrebbe cambiarlo dalla
	    // combo senza fare la
	    // ricerca
	    // e così verrebbe preso un ambiente errato
	    BigDecimal idStrut = ((ElvVLisElencoVersStatoTableBean) getForm()
		    .getElenchiIndiciAipSelezionatiList().getTable()).getRow(0).getIdStrut();
	    OrgAmbienteRowBean ambienteRowBean = struttureEjb
		    .getOrgAmbienteRowBeanByIdStrut(idStrut);
	    BigDecimal idAmbiente = ambienteRowBean.getIdAmbiente();
	    if (idAmbiente != null) {
		// Ricavo il parametro HSM_USERNAME (parametro multiplo dell'ambiente) associato
		// all'utente corrente
		String hsmUserName = amministrazioneEjb.getHsmUsername(getUser().getIdUtente(),
			idAmbiente);
		if (hsmUserName != null) {

		    getRequest().setAttribute("customElenchiVersamentoSelect", true);

		    getForm().getFiltriElenchiIndiciAipDaFirmare().getUser().setValue(hsmUserName);
		    getForm().getFiltriElenchiIndiciAipDaFirmare().getUser().setViewMode();
		} else {
		    getMessageBox()
			    .addError("Utente non rientra tra i firmatari definiti sull’ambiente");
		}
	    }
	}
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    // Questo metodo può essere unificato a initFiltriElenchiDaFirmare, bisogna aspettare però il
    // rilascio del branch
    // hsm per non fare caos
    private void initFiltriElenchiIndiciAipDaFirmare(BigDecimal idStruttura) {
	// Azzero i filtri
	getForm().getFiltriElenchiIndiciAipDaFirmare().reset();
	// Ricavo id struttura, ente ed ambiente attuali
	BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStruttura);
	BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);

	// Inizializzo le combo settando la struttura corrente
	OrgAmbienteTableBean tmpTableBeanAmbiente = null;
	OrgEnteTableBean tmpTableBeanEnte = null;
	OrgStrutTableBean tmpTableBeanStruttura = null;
	try {
	    // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
	    tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

	    // Ricavo i valori della combo ENTE
	    tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
		    idAmbiente.longValue(), Boolean.TRUE);

	    // Ricavo i valori della combo STRUTTURA
	    tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
		    idEnte, Boolean.TRUE);

	} catch (Exception ex) {
	    log.error("Errore in ricerca ambiente", ex);
	}

	DecodeMap mappaAmbiente = new DecodeMap();
	mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
	getForm().getFiltriElenchiIndiciAipDaFirmare().getId_ambiente().setDecodeMap(mappaAmbiente);
	getForm().getFiltriElenchiIndiciAipDaFirmare().getId_ambiente()
		.setValue(idAmbiente.toString());

	DecodeMap mappaEnte = new DecodeMap();
	mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
	getForm().getFiltriElenchiIndiciAipDaFirmare().getId_ente().setDecodeMap(mappaEnte);
	getForm().getFiltriElenchiIndiciAipDaFirmare().getId_ente().setValue(idEnte.toString());

	DecodeMap mappaStrut = new DecodeMap();
	mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
	getForm().getFiltriElenchiIndiciAipDaFirmare().getId_strut().setDecodeMap(mappaStrut);
	getForm().getFiltriElenchiIndiciAipDaFirmare().getId_strut()
		.setValue(idStruttura.toString());

	// Combo flag fiscale e gestione elenco indici aip
	getForm().getFiltriElenchiIndiciAipDaFirmare().getFl_elenco_fisc()
		.setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	boolean flSigilloAttivo = true;
	getForm().getFiltriElenchiIndiciAipDaFirmare().getTi_gest_elenco()
		.setDecodeMap(ComboGetter.getMappaTiGestElencoCriterio(flSigilloAttivo));
	String[] appo = {
		CostantiDB.TiGestElencoCriterio.FIRMA.name(),
		CostantiDB.TiGestElencoCriterio.MARCA_FIRMA.name() };
	getForm().getFiltriElenchiIndiciAipDaFirmare().getTi_gest_elenco().setValues(appo);
	// Imposto i filtri in editMode
	getForm().getFiltriElenchiIndiciAipDaFirmare().setEditMode();
    }

    /**
     * Bottone "+" della "Lista elenchi di versamento da firmare" per spostare un elenco da questa
     * lista a quella degli elenchi selezionati pronti per essere firmati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiIndiciAipDaFirmareList() throws EMFError {
	/* Ricavo il record interessato della "Lista elenchi di versamento da firmare" */
	ElvVLisElencoVersStatoRowBean row = (ElvVLisElencoVersStatoRowBean) getForm()
		.getElenchiIndiciAipDaFirmareList().getTable().getCurrentRow();
	int index = getForm().getElenchiIndiciAipDaFirmareList().getTable().getCurrentRowIndex();
	/* Lo tolgo dalla lista elenchi di versamento da firmare */
	getForm().getElenchiIndiciAipDaFirmareList().getTable().remove(index);
	/* "Refresho" la lista senza il record */
	int paginaCorrente = getForm().getElenchiIndiciAipDaFirmareList().getTable()
		.getCurrentPageIndex();
	int inizio = getForm().getElenchiIndiciAipDaFirmareList().getTable().getFirstRowPageIndex();
	this.lazyLoadGoPage(getForm().getElenchiIndiciAipDaFirmareList(), paginaCorrente);
	getForm().getElenchiIndiciAipDaFirmareList().getTable().setCurrentRowIndex(inizio);
	/* Aggiungo il record nella lista degli elenchi di versamento selezionati */
	getForm().getElenchiIndiciAipSelSection().setLoadOpened(true);
	getForm().getElenchiIndiciAipSelezionatiList().add(row);
	getForm().getElenchiIndiciAipSelezionatiList().getTable().addSortingRule(getForm()
		.getElenchiIndiciAipSelezionatiList().getDt_creazione_elenco_ix_aip().getName(),
		SortingRule.ASC);
	getForm().getElenchiIndiciAipSelezionatiList().getTable().sort();
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    /**
     * Bottone "-" della "Lista elenchi di versamento selezionati" per spostare un elenco da questa
     * lista a quella degli elenchi di versamento da firmare
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiIndiciAipSelezionatiList() throws EMFError {
	/* Ricavo il record interessato della "Lista elenchi di versamento selezionati" */
	ElvVLisElencoVersStatoRowBean row = (ElvVLisElencoVersStatoRowBean) getForm()
		.getElenchiIndiciAipSelezionatiList().getTable().getCurrentRow();
	int index = getForm().getElenchiIndiciAipSelezionatiList().getTable().getCurrentRowIndex();
	/* Lo tolgo dalla lista elenchi di versamento selezionati */
	getForm().getElenchiIndiciAipSelezionatiList().getTable().remove(index);
	/* "Refresho" la lista senza il record */
	int paginaCorrente = getForm().getElenchiIndiciAipSelezionatiList().getTable()
		.getCurrentPageIndex();
	int inizio = getForm().getElenchiIndiciAipSelezionatiList().getTable()
		.getFirstRowPageIndex();
	// Rieseguo la query se necessario
	this.lazyLoadGoPage(getForm().getElenchiIndiciAipSelezionatiList(), paginaCorrente);
	// Ritorno alla pagina
	getForm().getElenchiIndiciAipSelezionatiList().getTable().setCurrentRowIndex(inizio);
	// Pagina Volumi da firmare
	getForm().getElenchiIndiciAipDaFirmareList().add(row);
	int paginaCorrenteVF = getForm().getElenchiIndiciAipDaFirmareList().getTable()
		.getCurrentPageIndex();
	int inizioVF = getForm().getElenchiIndiciAipDaFirmareList().getTable()
		.getFirstRowPageIndex();
	// Rieseguo la query se necessario
	this.lazyLoadGoPage(getForm().getElenchiIndiciAipDaFirmareList(), paginaCorrenteVF);
	// Ritorno alla pagina
	getForm().getElenchiIndiciAipDaFirmareList().getTable().setCurrentRowIndex(inizioVF);
	forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_SELECT);
    }

    public void firmaElenchiIndiciAipHsmJs() throws EMFError {
	List<String> errorList = new ArrayList<>();
	JSONObject result = new JSONObject();

	// Recupero informazioni riguardo all'Utente (idSacer e credenziali HSM)
	long idUtente = SessionManager.getUser(getSession()).getIdUtente();

	getForm().getFiltriElenchiIndiciAipDaFirmare().post(getRequest());
	String user = getForm().getFiltriElenchiIndiciAipDaFirmare().getUser().parse();
	char[] passwd = getForm().getFiltriElenchiIndiciAipDaFirmare().getPasswd().parse() != null
		? getForm().getFiltriElenchiIndiciAipDaFirmare().getPasswd().parse().toCharArray()
		: null;
	char[] otp = getForm().getFiltriElenchiIndiciAipDaFirmare().getOtp().parse() != null
		? getForm().getFiltriElenchiIndiciAipDaFirmare().getOtp().parse().toCharArray()
		: null;

	if (StringUtils.isBlank(user)) {
	    errorList.add("Il campo \"Utente\" non può essere vuoto.");
	}
	if (passwd == null || passwd.length == 0) {
	    errorList.add("Il campo \"Password\" non può essere vuoto.");
	}
	if (otp == null || otp.length == 0) {
	    errorList.add("Il campo \"OTP\" non può essere vuoto.");
	}

	if (elencoIndiciAipSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())) {
	    getMessageBox().addError("Sessione di firma attiva");
	}

	ElvElencoVerTableBean elenchiDaFirmare = checkElenchiIndiciAipToSign();
	try {
	    if (errorList.isEmpty() && !getMessageBox().hasError() && elenchiDaFirmare != null) {
		SigningRequest request = new SigningRequest(idUtente);
		HSMUser userHSM = new HSMUser(user, passwd);
		userHSM.setOTP(otp);
		request.setUserHSM(userHSM);
		request.setType(TiSessioneFirma.ELENCO_INDICI_AIP);
		for (ElvElencoVerRowBean elenco : elenchiDaFirmare) {
		    BigDecimal idElenco = elenco.getIdElencoVers();
		    if (evEjb.soloUdAnnul(idElenco)) {
			evEjb.manageElencoUdAnnulDaFirmaElencoIndiciAip(idElenco);
		    } else {
			// EVO 19304: prima dei controlli sulla vista ELV_V_CHK_UNA_UD_ANNUL
			// (evEjb.almenoUnaUdAnnul)
			// e relativa gestione delle ud
			// Il sistema registra lo stato dell’elenco creato
			evEjb.registraStatoElencoVersamento(idElenco,
				"RICHIESTA_FIRMA_ELENCO_INDICI_AIP",
				"Richiesta firma elenco indici AIP",
				ElvStatoElencoVer.TiStatoElenco.ELENCO_INDICI_AIP_FIRMA_IN_CORSO,
				getUser().getUsername());
			request.addFile(idElenco);
		    }
		}
		// MEV#15967 - Attivazione della firma Xades e XadesT
		// Estrae l'ambiente selezionato per la ricerca
		BigDecimal idAmbiente = getForm().getFiltriElenchiIndiciAipDaFirmare()
			.getId_ambiente().parse();
		Future<SigningResponse> provaAsync = null;
		it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma = amministrazioneEjb
			.getTipoFirmaPerAmbiente(idAmbiente);
		switch (tipoFirma) {
		case CADES:
		    provaAsync = firmaHsmEjb.signP7MRequest(request);
		    break;
		case XADES:
		    provaAsync = firmaHsmEjb.signXades(request);
		    break;
		}
		// VECCHIO CODICE ORIGINALE
		// Future<SigningResponse> provaAsync = firmaHsmEjb.signP7MRequest(request);
		getSession().setAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP, provaAsync);
	    }

	    if (errorList.isEmpty() && !result.has("status")) {
		result.put("info", "Sessione di firma avviata");
	    } else if (!errorList.isEmpty()) {
		result.put("error", errorList);
	    }
	} catch (JSONException ex) {
	    log.error(
		    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma dei file",
		    ex);
	    getMessageBox().addError("Errore inatteso nel recupero e firma dei file");
	}
	if (!getMessageBox().hasError()) {
	    redirectToAjax(result);
	} else {
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public JSONObject triggerFiltriElenchiVersamentoId_ambienteOnTrigger() throws EMFError {
	triggerAmbienteGenerico(getForm().getFiltriElenchiVersamento(),
		ActionEnums.SezioneElenchi.RICERCA_ELENCHI);
	return getForm().getFiltriElenchiVersamento().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiVersamentoId_enteOnTrigger() throws EMFError {
	triggerEnteGenerico(getForm().getFiltriElenchiVersamento(),
		ActionEnums.SezioneElenchi.RICERCA_ELENCHI);
	return getForm().getFiltriElenchiVersamento().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiVersamentoId_strutOnTrigger() throws EMFError {
	getForm().getFiltriElenchiVersamento().post(getRequest());
	if (getForm().getFiltriElenchiVersamento().getId_strut().parse() != null) {
	    checkUniqueStrutInCombo(getForm().getFiltriElenchiVersamento().getId_strut().parse(),
		    ActionEnums.SezioneElenchi.RICERCA_ELENCHI);
	} else {
	    getForm().getFiltriElenchiVersamento().getCd_registro_key_unita_doc()
		    .setDecodeMap(new DecodeMap());
	}
	return getForm().getFiltriElenchiVersamento().asJSON();
    }

    // MEV#32249 - Funzione per riportare indietro lo stato di un elenco per consentire la firma
    // dell'AIP
    @Override
    public void riportaStatoIndietroButton() throws Throwable {
	BigDecimal idElencoVers = getForm().getElenchiVersamentoList().getTable().getCurrentRow()
		.getBigDecimal(ElvVLisElencoVersStatoTableDescriptor.COL_ID_ELENCO_VERS);

	ElenchiVersamentoEjb.EsitoRiportaIndietroStatoVersamento esito = evEjb
		.riportaStatoVersamentoIndietro(idElencoVers, getUser().getUsername());

	switch (esito) {
	case CHECK_SOLO_UD_E_DOC_ANNULLATI:
	    getMessageBox().addInfo(
		    "Operazione non consentita perché tutte le unità documentarie e documenti sono annullati");
	    forwardToPublisher(getLastPublisher());
	    break;
	case ELENCO_CON_ALMENO_UNA_UD_SENZA_INDICE_AIP:
	    getMessageBox().addInfo(
		    "Operazione non consentita perché per almeno una unità documentaria non è definita la versione indice AIP generata da elenco");
	    forwardToPublisher(getLastPublisher());
	    break;
	case ELENCO_CON_UD_CON_TROPPE_VERSIONI_INDICE_AIP:
	    getMessageBox().addInfo(
		    "Operazione non consentita perché per almeno una unità documentaria è definita più di una versione di indice AIP generato da elenco");
	    forwardToPublisher(getLastPublisher());
	    break;
	case ESITO_OK:
	    getMessageBox().addInfo("L'AIP è stato riportato alla Firma.");
	    dettaglioElencoVersamento(idElencoVers);
	    forwardToPublisher(getLastPublisher());
	    break;
	default:
	    forwardToPublisher(getLastPublisher());
	    break;
	}

    }

    // MEV#34195 - Funzione per riportare indietro lo stato di una lista di elenchi per consentire
    // la firma dell'AIP
    @Override
    public void riportaStatoIndietroDaRicercaButton() throws EMFError {
	// Recupero gli elenchi restituiti dalla ricerca
	List<BigDecimal> idElencoVersList = new ArrayList<>();

	if (getForm().getElenchiVersamentoList().getTable() != null
		&& !getForm().getElenchiVersamentoList().getTable().isEmpty()) {

	    if (getForm().getElenchiVersamentoList().getTable().size() < 1000) {

		for (BaseRowInterface riga : getForm().getElenchiVersamentoList().getTable()) {
		    idElencoVersList.add(riga.getBigDecimal("id_elenco_vers"));
		}

		// Filtro gli elenchi, tenendo solo quelli idonei alla firma dell'AIP
		List<BigDecimal> idElencoVersIdoneiList = evEjb
			.isPossibileMettereAipAllaFirma(idElencoVersList);

		int countUdDocAnnul = 0;
		int countNoVerIndiceAip = 0;
		int countPiuVersioniIndiceAip = 0;
		int countOK = 0;

		// Per ogni elenco, tra quelli idonei, eseguo l'algoritmo per riportare indietro lo
		// stato
		for (BigDecimal idElencoVers : idElencoVersIdoneiList) {
		    ElenchiVersamentoEjb.EsitoRiportaIndietroStatoVersamento esito = evEjb
			    .riportaStatoVersamentoIndietro(idElencoVers, getUser().getUsername());

		    switch (esito) {
		    case CHECK_SOLO_UD_E_DOC_ANNULLATI:
			countUdDocAnnul++;
			break;
		    case ELENCO_CON_ALMENO_UNA_UD_SENZA_INDICE_AIP:
			countNoVerIndiceAip++;
			break;
		    case ELENCO_CON_UD_CON_TROPPE_VERSIONI_INDICE_AIP:
			countPiuVersioniIndiceAip++;
			break;
		    case ESITO_OK:
			countOK++;
			break;
		    default:
			break;
		    }
		}

		StringBuilder reportElenchiAllaFirma = new StringBuilder("Dalla ricerca risultano "
			+ idElencoVersIdoneiList.size()
			+ " elenchi di versamento idonei per mettere l'AIP alla firma<br><br>");
		final String OP_NO_CONS = "Operazione non consentita per ";

		if (countUdDocAnnul != 0) {
		    reportElenchiAllaFirma.append(OP_NO_CONS).append(countUdDocAnnul).append(
			    " elenchi perché tutte le unità documentarie e documenti sono annullati<br><br>");
		}
		if (countNoVerIndiceAip != 0) {
		    reportElenchiAllaFirma.append(OP_NO_CONS).append(countNoVerIndiceAip).append(
			    " elenchi perché per almeno una unità documentaria non è definita la versione indice AIP generata da elenco<br><br>");
		}
		if (countPiuVersioniIndiceAip != 0) {
		    reportElenchiAllaFirma.append(OP_NO_CONS).append(countPiuVersioniIndiceAip)
			    .append(" elenchi perché per almeno una unità documentaria è definita più di una versione di indice AIP generato da elenco<br><br>");
		}
		if (countOK != 0) {
		    reportElenchiAllaFirma.append("Per ").append(countOK)
			    .append(" elenchi l'AIP è stato riportato alla Firma");
		}

		getMessageBox().addInfo(reportElenchiAllaFirma.toString());
	    } else {
		getMessageBox().addWarning(
			"Il risultato della ricerca è superiore a 1000. Per utilizzare la funzionalità 'Metti AIP alla firma', ripetere la ricerca imponendo vincoli più restrittivi per diminuire il numero di risultati");
	    }

	} else {
	    getMessageBox().addWarning(
		    "La lista elenchi di versamento è vuota, impossibile eseguire l'operazione");
	}

	forwardToPublisher(Application.Publisher.ELENCHI_VERSAMENTO_RICERCA);

    }

}
