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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;

import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.VolumiAbstractAction;
import it.eng.parer.slite.gen.form.ComponentiForm;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.form.VolumiForm;
import it.eng.parer.slite.gen.form.VolumiForm.ComponentiFiltri;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgSubStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicUnitaDocTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisDocIamTableBean;
import it.eng.parer.slite.gen.viewbean.VolVListaCompVolRowBean;
import it.eng.parer.slite.gen.viewbean.VolVListaCompVolTableBean;
import it.eng.parer.slite.gen.viewbean.VolVRicVolumeRowBean;
import it.eng.parer.volume.ejb.VolumeEjb;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.volume.utils.VolumeEnums.FileTypeEnum;
import it.eng.parer.volume.utils.VolumeEnums.VolStatusEnum;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.helper.VolumiHelper;
import it.eng.parer.web.util.ActionEnums.VolumiAttributes;
import it.eng.parer.web.util.BlobObject;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.web.validator.VolumiValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;

/**
 *
 * @author Gilioli_P
 */
public class VolumiAction extends VolumiAbstractAction {

    private static Logger log = LoggerFactory.getLogger(VolumiAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/VolumiHelper")
    private VolumiHelper volumiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ComponentiHelper")
    private ComponentiHelper componentiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/VolumeEjb")
    private VolumeEjb volumeEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggruppamentoEjb")
    private CriteriRaggruppamentoEjb criteriRaggruppamentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;

    private BigDecimal getIdStrut() {
	return getUser().getIdOrganizzazioneFoglia();
    }

    /**
     * Metodo di inizializzazione form di ricerca volumi
     *
     * @throws EMFError errore generico
     */
    @Override
    @Secure(action = "Menu.Volumi.GestioneVolumi")
    public void initOnClick() throws EMFError {
	getUser().getMenu().reset();
	getUser().getMenu().select("Menu.Volumi.GestioneVolumi");
	// Pulisco i filtri della form di ricerca
	getForm().getFiltri().reset();
	// Inizializzo le combo di supporto
	// Imposto i valori della combo CRITERIO_RAGGRUPPAMENTO ricavati dalla tabella
	// DEC_CRITERIO_RAGGR
	DecCriterioRaggrTableBean tmpTableBeanCriteri = criteriRaggruppamentoEjb
		.getDecCriterioRaggrTableBean(null, null, getIdStrut(), null);
	DecodeMap mappaCriteri = new DecodeMap();
	mappaCriteri.populatedMap(tmpTableBeanCriteri, "nm_criterio_raggr", "nm_criterio_raggr");

	// Setto i valori della combo TIPO REGISTRO ricavati dalla tabella DEC_REGISTRO_UNITA_DOC
	DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
		.getRegistriUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
	DecodeMap mappaRegistro = new DecodeMap();
	mappaRegistro.populatedMap(tmpTableBeanReg, "cd_registro_unita_doc",
		"cd_registro_unita_doc");

	// Imposto le varie combo dei FILTRI di ricerca Volumi
	getForm().getFiltri().getTi_stato_volume_conserv()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato_volume",
			VolumeEnums.VolStatusEnum.getComboMappaStatoVolRicerca()));
	getForm().getFiltri().getCreato_man().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
	getForm().getFiltri().getNm_criterio_raggr().setDecodeMap(mappaCriteri);
	getForm().getFiltri().getTi_presenza_firme()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("presenza_firme",
			VolumeEnums.SignatureInfoEnum.getComboPresenzaFirme()));
	getForm().getFiltri().getTi_val_firme().setDecodeMap(ComboGetter.getMappaSortedGenericEnum(
		"validita_firme", VolumeEnums.SignatureInfoEnum.getComboValiditaFirme()));
	getForm().getFiltri().getCd_registro_key_unita_doc().setDecodeMap(mappaRegistro);

	// Carico la pagina di ricerca
	forwardToPublisher(Application.Publisher.VOLUMI_RICERCA);
	// Imposto i filtri in edit mode
	getForm().getFiltri().setEditMode();
	getForm().getComponentiFiltri().clear();

	/*
	 * Rimuovo gli attributi di sessione che potrebbero essere rimasti in caso di navigazione su
	 * menu con una operazione in corso
	 */
	getSession().removeAttribute("addUdToVolume");
	getSession().removeAttribute(VolumiAttributes.TIPOLISTA.name());
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.VOLUMI_RICERCA;
    }

    @Override
    public String getControllerName() {
	return Application.Actions.VOLUMI;
    }

    @Override
    public void process() throws EMFError {
    }

    /**
     * Metodo scatenato al click del bottone di ricerca all'interno della pagina di ricerca volumi
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricerca() throws EMFError {
	VolumiForm.Filtri filtri = getForm().getFiltri();
	// Esegue la post dei filtri compilati
	filtri.post(getRequest());
	// Valida i filtri per verificare quelli obbligatori
	if (filtri.validate(getMessageBox())) {
	    // Valida i campi di ricerca
	    VolumiValidator validator = new VolumiValidator(getMessageBox());
	    // Valida in maniera più specifica i dati
	    validator.validateDataVolumi(filtri.getDt_creazione_da().parse(),
		    filtri.getDt_creazione_a().parse());
	    Object[] chiavi = validator.validaChiaveUnitaDocVolumi(
		    filtri.getCd_registro_key_unita_doc().parse(),
		    filtri.getAa_key_unita_doc().parse(), filtri.getCd_key_unita_doc().parse(),
		    filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse(),
		    filtri.getCd_key_unita_doc_da().parse(),
		    filtri.getCd_key_unita_doc_a().parse());
	    if (!getMessageBox().hasError()) {
		// La validazione non ha riportato errori.
		// Setto i filtri di chiavi unità documentaria impostando gli eventuali valori di
		// default
		if (chiavi != null && chiavi.length == 5) {
		    filtri.getAa_key_unita_doc_da().setValue(
			    chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
		    filtri.getAa_key_unita_doc_a().setValue(
			    chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
		    filtri.getCd_key_unita_doc_da()
			    .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
		    filtri.getCd_key_unita_doc_a()
			    .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
		}

		// La validazione non ha riportato errori. carico la tabella con i filtri impostati
		String maxResultStandard = configurationHelper
			.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
		getForm().getVolumiList().setTable(volumiHelper.getVolVRicVolumeViewBean(
			getIdStrut(), filtri, Integer.parseInt(maxResultStandard)));
		getForm().getVolumiList().getTable().setPageSize(10);
		getForm().getVolumiList().setUserOperations(true, false, false, false);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getVolumiList().getTable().first();
		// Imposto la sezione del filtro chiave unità documentaria aperto dopo la ricerca se
		// ho inserito un
		// valore nel campo registro
		if (!getForm().getFiltri().getCd_registro_key_unita_doc().getValue().equals("")) {
		    getForm().getChiaveSection().setLoadOpened(true);
		}
	    }
	}
    }

    /**
     * Metodo invocato dal bottone omonimo dei filtri ricerca volumi per ripulire i filtri
     *
     * @throws EMFError errore generico
     */
    @Override
    public void pulisci() throws EMFError {
	this.initOnClick();
    }

    /**
     * Metodo invocato sul bottone di dettaglio/modifica di una riga della lista volumi, esegue il
     * caricamento della pagina per visualizzare il dettaglio, in seguito alla loadDettaglio
     *
     * @throws EMFError errore generico
     */
    @Override
    public void dettaglioOnClick() throws EMFError {
	if (getRequest().getParameter("table") != null) {
	    // Controllo per quale tabella è stato invocato il metodo
	    Set<Object> regSet = getForm().getComponentiFiltri().getCd_registro_key_unita_doc()
		    .getDecodeMap().keySet();
	    Set<Object> tipiUdSet = DecodeMap.Factory.newInstance(
		    tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
			    getUser().getIdOrganizzazioneFoglia()),
		    "id_tipo_unita_doc", "nm_tipo_unita_doc").keySet();

	    if (getRequest().getParameter("table")
		    .equals(getForm().getComponentiList().getName())) {
		BigDecimal idUnitaDoc = null;
		// Ottengo i tipi UD per cui l'utente è abilitato
		VolVListaCompVolRowBean row = (VolVListaCompVolRowBean) getForm()
			.getComponentiList().getTable().getCurrentRow();
		if (regSet.contains(row.getCdRegistroKeyUnitaDoc())
			&& tipiUdSet.contains(row.getIdTipoUnitaDoc())) {
		    idUnitaDoc = row.getIdUnitaDoc();
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
			// LISTA COMPONENTI
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
	    } else if (getRequest().getParameter("table")
		    .equals(getForm().getUnitaDocumentarieList().getName())) {
		// LISTA UNITA' DOCUMENTARIE
		UnitaDocumentarieForm form = new UnitaDocumentarieForm();
		AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
		Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
		BigDecimal idUnitaDoc = null;
		VolVListaCompVolRowBean row = ((VolVListaCompVolRowBean) getForm()
			.getComponentiList().getTable().getRow(riga));
		if (regSet.contains(row.getCdRegistroKeyUnitaDoc())
			&& tipiUdSet.contains(row.getIdTipoUnitaDoc())) {
		    idUnitaDoc = row.getIdUnitaDoc();
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
			unitaDocTB.add(udHelper.getAroVRicUnitaDocRowBean(idUnitaDoc, null, null));
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
	    } else if (getRequest().getParameter("table")
		    .equals(getForm().getDocumentoList().getName())) {
		// DOCUMENTO
		UnitaDocumentarieForm form = new UnitaDocumentarieForm();
		AroVLisDocTableBean docTB = new AroVLisDocTableBean();
		Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
		BigDecimal idUnitaDoc = null;
		VolVListaCompVolRowBean rowComp = ((VolVListaCompVolRowBean) getForm()
			.getComponentiList().getTable().getRow(riga));
		if (regSet.contains(rowComp.getCdRegistroKeyUnitaDoc())
			&& tipiUdSet.contains(rowComp.getIdTipoUnitaDoc())) {
		    idUnitaDoc = rowComp.getIdUnitaDoc();
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
			BigDecimal idDoc = rowComp.getIdDoc();
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
	    } else {
		// LISTA VOLUMI
		getForm().getVolumiList().setStatus(Status.view);
		getForm().getVolumiTabs()
			.setCurrentTab(getForm().getVolumiTabs().getDettaglioVol());
		forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
	    }
	}
    }

    private void initCombo() throws EMFError {
	// Imposto i valori della combo TIPO STRUTTURA DOCUMENTO ricavati dalla tabella
	// DEC_TIPO_STRUT_DOC
	DecTipoStrutDocTableBean tmpTableBeanTipoStrutDoc = tipoStrutDocEjb
		.getDecTipoStrutDocTableBean(getIdStrut(), false);
	DecodeMap mappaTipoStrutDoc = new DecodeMap();
	mappaTipoStrutDoc.populatedMap(tmpTableBeanTipoStrutDoc, "id_tipo_strut_doc",
		"nm_tipo_strut_doc");

	// Imposto i valori della combo FORMATO_FILE_DOC ricavati dalla tabella DEC_FORMATO_FILE_DOC
	DecFormatoFileDocTableBean tmpTableBeanFormatoFileDoc = formatoFileDocEjb
		.getDecFormatoFileDocTableBean(getIdStrut());
	DecodeMap mappaFormatoFileDoc = new DecodeMap();
	mappaFormatoFileDoc.populatedMap(tmpTableBeanFormatoFileDoc, "nm_formato_file_doc",
		"nm_formato_file_doc");

	// Setto i valori della combo TIPO REGISTRO ricavati dalla tabella DEC_REGISTRO_UNITA_DOC
	DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb
		.getRegistriUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
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
	getForm().getComponentiFiltri().getTi_esito_verif_firme_chius()
		.setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme_chius",
			VolumeEnums.StatoVerifica.getComboEsitoVerifFirmeChius()));
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

    /**
     * Metodo utilizzato per nascondore tutti i bottoni da dettaglio volume
     *
     */
    private void nascondiBottoni() {
	getForm().getVolumiDetail().getRicercaUdSemplice().setHidden(true);
	getForm().getVolumiDetail().getRicercaUdAvanzata().setHidden(true);
	getForm().getComponentiList().getSelect_comp().setHidden(true);
	getForm().getComponentiList().getSelect_comp().setViewMode();
    }

    private void visualizzaBottoni(String statoVolume, Date dtScadChius) {
	if (statoVolume.equals(VolStatusEnum.CHIUSO.name())
		|| statoVolume.equals(VolStatusEnum.FIRMATO_NO_MARCA.name())) {
	    getForm().getVolumiList().setUserOperations(true, true, false, false);
	} else if (statoVolume.equals(VolStatusEnum.FIRMATO.name())) {
	    getForm().getVolumiList().setUserOperations(true, true, false, false);
	} else if (statoVolume.equals(VolStatusEnum.APERTO.name())) {
	    if (dtScadChius.after(Calendar.getInstance().getTime())) {
		getForm().getVolumiDetail().getRicercaUdSemplice().setHidden(false);
		getForm().getVolumiDetail().getRicercaUdAvanzata().setHidden(false);
	    }
	    // Imposto la checkbox di "selezione unità documentarie da eliminare" visibile
	    getForm().getComponentiList().getSelect_comp().setHidden(false);
	    getForm().getComponentiList().getSelect_comp().setEditMode();
	    getForm().getVolumiList().setUserOperations(true, true, false, true);
	} else if (statoVolume.equals(VolStatusEnum.IN_ERRORE.name())) {
	    getForm().getVolumiList().setUserOperations(true, false, false, true);
	}
    }

    /**
     * Metodo utilizzato dal framework quando clicco sul tasto "Indietro" nella barra di scorrimento
     * del dettaglio di un record
     *
     * @throws EMFError errore generico
     */
    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    @Override
    public void insertDettaglio() throws EMFError {
    }

    /**
     * Metodo invocato sul bottone di dettaglio/modifica su una riga delle liste seguenti, esegue il
     * caricamento dei dati della riga selezionata per visualizzare il dettaglio
     *
     * @throws EMFError errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {
	VolVRicVolumeRowBean vv;
	boolean volDetail = false;
	BigDecimal idvol = (BigDecimal) getSession().getAttribute("idvol");
	if (getTableName() != null) {
	    if (getTableName().equals(getForm().getVolumiList().getName())) {
		// LISTA VOLUMI
		idvol = getForm().getVolumiList().getTable().getCurrentRow()
			.getBigDecimal("id_volume_conserv");
		getSession().setAttribute("idvol", idvol);
		volDetail = true;
	    }
	}

	// Carico il dettaglio di un volume
	vv = volumiHelper.findVolVRicVolume(idvol);
	getSession().removeAttribute("idStrut");
	// Se non provengo dal dettaglio documento il volume selezionato è ora quello corrente
	if (volDetail) {
	    if (!getLastPublisher()
		    .equals(Application.Publisher.DOCUMENTI_UNITA_DOCUMENTARIE_DETAIL)) {
		getSession().setAttribute("volCorrente", idvol.longValue());
		if (!getLastPublisher().equals("")) {
		    getSession().setAttribute("volCreato", false);
		}
	    }
	}
	// Carico la pagina di dettaglio dei Volumi
	getForm().getVolumiDetail().copyFromBean(vv);

	/* Gestione dei tipi dato soggetti alle abilitazioni */
	DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
		.getTipiUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
	DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb
		.getTipiDocAbilitati(getUser().getIdUtente(), getIdStrut());
	OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
		.getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());

	if ((getTableName() != null && (getTableName().equals(getForm().getVolumiList().getName())))
		|| getTableName() == null) {
	    // Controllo atto a permettere la navigazione nella navbar dei componenti
	    String maxResultCompVol = configurationHelper
		    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_COMP_VOL);
	    VolVListaCompVolTableBean listaComp = componentiHelper.getVolVListaCompVolViewBean(
		    idvol, new ComponentiFiltri(), null, tmpTableBeanTipoUD, tmpTableBeanTipoDoc,
		    tmpSubStrutsTableBean, Integer.parseInt(maxResultCompVol));
	    getForm().getComponentiList().setTable(listaComp);
	    getForm().getComponentiList().getTable().setPageSize(10);
	    // Workaround in modo che la lista punti al primo record, non all'ultimo
	    getForm().getComponentiList().getTable().first();
	}

	// Inizializzo le liste fittizie nel caso si voglia visualizzare unità documentarie o
	// documenti
	getForm().getUnitaDocumentarieList().setTable(new AroVRicUnitaDocTableBean());
	getForm().getDocumentoList().setTable(new AroVVisDocIamTableBean());

	// Dato che il dettaglio è in view mode, devo impostare i bottoni in edit mode singolarmente
	getForm().getVolumiDetail().getRicercaUdSemplice().setEditMode();
	getForm().getVolumiDetail().getRicercaUdAvanzata().setEditMode();
	getForm().getVolumiDetail().getListaOperazioniVolume().setEditMode();

	nascondiBottoni();

	getForm().getChiaveSection().setLoadOpened(true);

	String statoVolume = getForm().getVolumiDetail().getTi_stato_volume_conserv().getValue();
	// Imposto i bottoni visibili in base allo stato del volume
	if ((getNavigationEvent() != null
		&& getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW))
		|| getLastPublisher().equals(Application.Publisher.VOLUMI_DETAIL)) {
	    if (statoVolume.equals(VolStatusEnum.CHIUSO.name())
		    || statoVolume.equals(VolStatusEnum.FIRMATO_NO_MARCA.name())
		    || statoVolume.equals(VolStatusEnum.FIRMATO.name())) {
		getForm().getVolumiList().setUserOperations(true, true, false, false);
		if (!statoVolume.equals(VolStatusEnum.CHIUSO.name())) {
		    getForm().getVolumiDetail().getDownloadProveConservazione().setEditMode();
		    getForm().getVolumiDetail().getDownloadProveConservazione()
			    .setDisableHourGlass(true);
		}
	    } else if (statoVolume.equals(VolStatusEnum.APERTO.name())) {
		if (vv.getDtScadChius().after(Calendar.getInstance().getTime())) {
		    getForm().getVolumiDetail().getRicercaUdSemplice().setHidden(false);
		    getForm().getVolumiDetail().getRicercaUdAvanzata().setHidden(false);
		}
		// Imposto la checkbox di "selezione unità documentarie da eliminare" visibile
		getForm().getComponentiList().getSelect_comp().setHidden(false);
		getForm().getComponentiList().getSelect_comp().setEditMode();
		getForm().getVolumiList().setUserOperations(true, true, false, true);
	    } else if (statoVolume.equals(VolStatusEnum.IN_ERRORE.name())) {
		getForm().getVolumiList().setUserOperations(true, false, false, true);
	    }
	}
	initCombo();
    }

    /**
     * Metodo invocato al salvataggio dei dati nella form di dettaglio volume per la modifica dei
     * campi nome, descrizione e note per indice e volume chiuso
     *
     * @throws EMFError errore generico
     */
    @Override
    public void saveDettaglio() throws EMFError {
	VolVRicVolumeRowBean vv = getVolVRicVolumeRowBean();
	BigDecimal idvol = vv.getIdVolumeConserv();

	getForm().getVolumiDetail().post(getRequest());

	String nomeVol = getForm().getVolumiDetail().getNm_volume_conserv().parse();
	String descrVol = getForm().getVolumiDetail().getDs_volume_conserv().parse();
	String notaVol = getForm().getVolumiDetail().getNt_indice_volume().parse();
	String notaVolChiuso = getForm().getVolumiDetail().getNt_volume_chiuso().parse();

	// Controllo se il nome del volume è stato modificato
	if (vv.getNmVolumeConserv() != null) {
	    if (!vv.getNmVolumeConserv().equals(nomeVol)) {
		// Controllo che non esista su db per quella struttura un volume con lo stesso nome
		if (volumiHelper.existNomeVolume(nomeVol, getIdStrut())) {
		    getMessageBox().addMessage(new Message(MessageLevel.ERR,
			    "Nome volume già  esistente per la struttura utilizzata"));
		    // se non va bene, reimposto il valore precedente nella casella di testo del
		    // nome volume
		    getForm().getVolumiDetail().getNm_volume_conserv()
			    .setValue(vv.getNmVolumeConserv());
		}
	    }
	}

	if (!getMessageBox().hasError()) {
	    try {
		volumiHelper.saveNomeDesNote(getUser().getIdUtente(), idvol, nomeVol, descrVol,
			notaVol, notaVolChiuso);
		undoDettaglio();
		getMessageBox().addInfo("Volume modificato con successo");
		getMessageBox().setViewMode(ViewMode.plain);
		getForm().getVolumiList().setUserOperations(true, true, true, true);
	    } catch (Exception e) {
		getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getMessage()));
	    }
	}

	visualizzaBottoni(getForm().getVolumiDetail().getTi_stato_volume_conserv().parse(),
		getForm().getVolumiDetail().getDt_scad_chius().parse());
	forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
    }

    /**
     * Metodo invocato al click del tasto "Annulla" alla modifica nel dettaglio del volume
     *
     * @throws EMFError errore generico
     */
    @Override
    public void undoDettaglio() throws EMFError {
	getForm().getVolumiDetail().getNm_volume_conserv().setViewMode();
	getForm().getVolumiDetail().getDs_volume_conserv().setViewMode();
	getForm().getVolumiDetail().getNt_indice_volume().setViewMode();
	getForm().getVolumiDetail().getNt_volume_chiuso().setViewMode();
	getForm().getVolumiList().setStatus(Status.view);
	visualizzaBottoni(getForm().getVolumiDetail().getTi_stato_volume_conserv().parse(),
		getForm().getVolumiDetail().getDt_scad_chius().parse());
	forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
    }

    /**
     * Metodo invocato al click del tasto "Modifica" nel dettaglio del volume
     *
     * @throws EMFError errore generico
     */
    @Override
    public void updateVolumiList() throws EMFError {
	String statoVolume = getForm().getVolumiDetail().getTi_stato_volume_conserv().getValue();
	if (statoVolume.equals(VolStatusEnum.CHIUSO.name())
		|| statoVolume.equals(VolStatusEnum.FIRMATO_NO_MARCA.name())
		|| statoVolume.equals(VolStatusEnum.FIRMATO.name())
		|| statoVolume.equals(VolStatusEnum.DA_CHIUDERE.name())
		|| statoVolume.equals(VolStatusEnum.DA_VERIFICARE.name())) {
	    getForm().getVolumiDetail().getNt_volume_chiuso().setEditMode();
	} else if (statoVolume.equals(VolStatusEnum.APERTO.name())) {
	    getForm().getVolumiDetail().getNm_volume_conserv().setEditMode();
	    getForm().getVolumiDetail().getDs_volume_conserv().setEditMode();
	    getForm().getVolumiDetail().getNt_indice_volume().setEditMode();
	}
	getForm().getVolumiList().setStatus(Status.update);
	nascondiBottoni();
	forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
    }

    private VolVRicVolumeRowBean getVolVRicVolumeRowBean() {
	return (VolVRicVolumeRowBean) getForm().getVolumiList().getTable().getCurrentRow();
    }

    /**
     * Trigger sul filtro nome tipo struttura documento: selezionando un valore della combo box
     * viene popolata la combo relativa al tipo componente documento
     *
     * @return oggetto di tipo {@link JSONObject}
     *
     * @throws EMFError errore generico
     */
    @Override
    public JSONObject triggerComponentiFiltriNm_tipo_strut_docOnTrigger() throws EMFError {
	ComponentiFiltri cf = getForm().getComponentiFiltri();
	cf.post(getRequest());
	BigDecimal idTipoStrutDoc = cf.getNm_tipo_strut_doc().parse();
	DecodeMap mappaTipoCompDoc = new DecodeMap();
	if (idTipoStrutDoc != null) {
	    // Setto i valori della combo TIPO COMPONENTE DOCUMENTO ricavati dalla tabella
	    // DEC_TIPO_STRUT_DOC
	    DecTipoCompDocTableBean tmpTableBeanTipoCompDoc = tipoStrutDocEjb
		    .getDecTipoCompDocTableBean(idTipoStrutDoc, false);
	    mappaTipoCompDoc.populatedMap(tmpTableBeanTipoCompDoc, "id_tipo_comp_doc",
		    "nm_tipo_comp_doc");
	}
	getForm().getComponentiFiltri().getNm_tipo_comp_doc().setDecodeMap(mappaTipoCompDoc);
	return getForm().getComponentiFiltri().asJSON();
    }

    /**
     * Metodo richiamato per la ricerca dei componenti all'interno di un dettaglio volume
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaComp() throws EMFError {
	VolumiForm.ComponentiFiltri compfiltri = getForm().getComponentiFiltri();
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
		// Valida i campi di Range di chiavi unità documentaria
		chiavi = validator.validaChiaviUnitaDoc(
			compfiltri.getCd_registro_key_unita_doc().getValue(),
			compfiltri.getAa_key_unita_doc().parse(),
			compfiltri.getCd_key_unita_doc().parse(),
			compfiltri.getAa_key_unita_doc_da().parse(),
			compfiltri.getAa_key_unita_doc_a().parse(),
			compfiltri.getCd_key_unita_doc_da().parse(),
			compfiltri.getCd_key_unita_doc_a().parse());
	    }
	    /* Gestione dei tipi dato soggetti alle abilitazioni */
	    DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
		    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
	    DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb
		    .getTipiDocAbilitati(getUser().getIdUtente(), getIdStrut());
	    OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
		    .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), getIdStrut());

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
		String maxResultCompVol = configurationHelper
			.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_COMP_VOL);
		getForm().getComponentiList()
			.setTable(componentiHelper.getVolVListaCompVolViewBean(
				(BigDecimal) getSession().getAttribute("idvol"), compfiltri,
				dateAcquisizioneValidate, tmpTableBeanTipoUD, tmpTableBeanTipoDoc,
				tmpSubStrutsTableBean, Integer.parseInt(maxResultCompVol)));
		getForm().getComponentiList().getTable().setPageSize(10);
		getForm().getComponentiList().setUserOperations(true, false, false, false);
		// Workaround in modo che la lista punti al primo record, non all'ultimo
		getForm().getComponentiList().getTable().first();
		// Imposto la sezione del filtro chiave unità documentaria aperto dopo la ricerca se
		// ho inserito un
		// valore nel campo registro
		if (!getForm().getComponentiFiltri().getCd_registro_key_unita_doc().getValue()
			.equals("")) {
		    getForm().getChiaveSection().setLoadOpened(true);
		}
	    }
	}
	// Workaround per evitare che il trigger scarichi la pagina HTML anzichÃ© visualizzarla sul
	// browser
	forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
    }

    /**
     * Attiva il tab del dettaglio volume
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabDettaglioVolOnClick() throws EMFError {
	getForm().getVolumiTabs().setCurrentTab(getForm().getVolumiTabs().getDettaglioVol());
	forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
    }

    /**
     * Attiva il tab dei filtri sui componenti nel dettaglio volume
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabFiltriCompOnClick() throws EMFError {
	getForm().getVolumiTabs().setCurrentTab(getForm().getVolumiTabs().getFiltriComp());
	getForm().getComponentiFiltri().setEditMode();
	forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
    }

    @Override
    public void ricercaUdSemplice() throws EMFError {
	if (getForm().getVolumiDetail().getDt_scad_chius().parse()
		.after(Calendar.getInstance().getTime())) {
	    UnitaDocumentarieForm form = new UnitaDocumentarieForm();
	    getSession().setAttribute("addUdToVolume", true);
	    redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
		    "?operation=unitaDocumentarieRicercaSemplice&back=true", form);
	} else {
	    getMessageBox().addError(
		    "Volume scaduto - Impossibile aggiungere nuove unità documentarie al volume");
	    forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
	}
    }

    @Override
    public void ricercaUdAvanzata() throws EMFError {
	if (getForm().getVolumiDetail().getDt_scad_chius().parse()
		.after(Calendar.getInstance().getTime())) {
	    UnitaDocumentarieForm form = new UnitaDocumentarieForm();
	    getSession().setAttribute("addUdToVolume", true);
	    redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
		    "?operation=unitaDocumentarieRicercaAvanzata&back=true", form);
	} else {
	    getMessageBox().addError(
		    "Volume scaduto - Impossibile aggiungere nuove unità documentarie al volume");
	    forwardToPublisher(Application.Publisher.VOLUMI_DETAIL);
	}
    }

    @Override
    public void downloadProveConservazione() throws EMFError {
	BigDecimal idVol = getForm().getVolumiDetail().getId_volume_conserv().parse();
	String statoVol = getForm().getVolumiDetail().getTi_stato_volume_conserv().parse();
	downloadProveConservazione(idVol, statoVol);
    }

    /**
     * Metodo invocato alla pressione del relativo bottone per scaricare in un file zip CRL,
     * certificati e file di conservazione di un volume chiuso o firmato no marca
     *
     * @param idvol    id volume
     * @param statoVol stato volume
     *
     * @throws EMFError errore generico
     */
    public void downloadProveConservazione(BigDecimal idvol, String statoVol) throws EMFError {
	// ricavo la lista dei blobbi CRL e Certif
	List[] blobbi = componentiHelper.getBlobboByteList(idvol);
	List<BlobObject> blobbiByteCRLList = blobbi[0];
	List<BlobObject> blobbiByteCertifList = blobbi[1];

	// comincio a costruire lo zippone
	String nomeZippone = "proveConservazione_vol-" + idvol;
	getResponse().setContentType("application/zip");
	getResponse().setHeader("Content-Disposition",
		"attachment; filename=\"" + nomeZippone + ".zip");
	ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
	String filename = null;

	try {
	    // caccio dentro nello zippone i blobbi CRL
	    if (blobbiByteCRLList != null) {
		for (int i = 0; i < blobbiByteCRLList.size(); i++) {
		    BlobObject tempBlobbo = blobbiByteCRLList.get(i);
		    if (tempBlobbo != null) {
			filename = "CRL/CRL_" + tempBlobbo.id + ".crl";
			zippaBlobbo(out, filename, tempBlobbo.blobbo);
		    }
		}
	    }

	    // caccio dentro nello zippone i blobbi Certif
	    if (blobbiByteCertifList != null) {
		for (int i = 0; i < blobbiByteCertifList.size(); i++) {
		    BlobObject tempBlobbo = blobbiByteCertifList.get(i);
		    if (tempBlobbo != null) {
			filename = "Certificati-Trusted/Certif_Ca_" + tempBlobbo.id + ".cer";
			zippaBlobbo(out, filename, tempBlobbo.blobbo);
		    }
		}
	    }

	    // caccio dentro lo zippone il file indice_conservazione.xml
	    byte[] indiceConservXml = volumeEjb.retrieveFileByIdVolume(idvol.longValue(),
		    FileTypeEnum.INDICE.name());
	    if (indiceConservXml != null) {
		filename = "indice_conservazione.xml";
		zippaBlobbo(out, filename, indiceConservXml);
	    }

	    // caccio dentro lo zippone il file indice_conservazione.tsr
	    byte[] indiceConservTsr = volumeEjb.retrieveFileByIdVolume(idvol.longValue(),
		    FileTypeEnum.MARCA_INDICE.name());
	    if (indiceConservTsr != null) {
		filename = "indice_conservazione.tsr";
		zippaBlobbo(out, filename, indiceConservTsr);
	    }

	    if (statoVol.equals("FIRMATO_NO_MARCA") || statoVol.equals("FIRMATO")) {
		// caccio dentro lo zippone il file firma_indice_conservazione.tsr.p7m
		byte[] firmaIndiceConservTsrP7m = volumeEjb
			.retrieveFileByIdVolume(idvol.longValue(), FileTypeEnum.FIRMA.name());
		if (firmaIndiceConservTsrP7m != null) {
		    filename = "firma_indice_conservazione.tsr.p7m";
		    zippaBlobbo(out, filename, firmaIndiceConservTsrP7m);
		}
	    }

	    if (statoVol.equals("FIRMATO")) {
		// caccio dentro lo zippone il file firma_indice_conservazione.tsr
		byte[] firmaIndiceConservTsr = volumeEjb.retrieveFileByIdVolume(idvol.longValue(),
			FileTypeEnum.MARCA_FIRMA.name());
		if (firmaIndiceConservTsr != null) {
		    filename = "firma_indice_conservazione.tsr";
		    zippaBlobbo(out, filename, firmaIndiceConservTsr);
		}
	    }

	    out.flush();
	    out.close();
	    freeze();
	} catch (Exception e) {
	    getMessageBox().addMessage(new Message(MessageLevel.ERR,
		    "Errore nel recupero dei file delle prove di conservazione"));
	    log.error("Eccezione", e);
	}
    }

    /**
     * Metodo utilizzato per inserire in un file zip (stream di output) un file passato sotto forma
     * di bytearray
     *
     * @param out      output stream {@link ZipOutputStream}
     * @param filename nome file
     * @param blobbo   file in byte array
     *
     * @throws EMFError    errore generico
     * @throws IOException errore generico di tipo IO
     *
     */
    public void zippaBlobbo(ZipOutputStream out, String filename, byte[] blobbo)
	    throws EMFError, IOException {
	try (InputStream is = new ByteArrayInputStream(blobbo)) {
	    byte[] data = new byte[1024];
	    int count;
	    out.putNextEntry(new ZipEntry(filename));
	    while ((count = is.read(data, 0, 1024)) != -1) {
		out.write(data, 0, count);
	    }
	    out.closeEntry();
	}
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
	if (publisherName.equalsIgnoreCase(Application.Publisher.VOLUMI_DETAIL)
		&& (getSession().getAttribute("addUdToVolume") != null)) {
	    // Se sono tornato indietro dalla ricerca UD ho probabilmente aggiunto un UD,
	    // altrimenti se sono tornato indietro dalla lista UD da rimuovere, ne ho probabilmente
	    // tolto uno.
	    // In entrambi i casi la lista viene ricaricata
	    getSession().removeAttribute("addUdToVolume");
	    Long idVolume = (Long) getSession().getAttribute("volCorrente");
	    if (idVolume != null) {
		try {
		    VolVRicVolumeRowBean vv = volumiHelper
			    .findVolVRicVolume(new BigDecimal(idVolume));
		    // Carico la pagina di dettaglio dei Volumi
		    getForm().getVolumiDetail().copyFromBean(vv);
		    /* Gestione dei tipi dato soggetti alle abilitazioni */
		    DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb
			    .getTipiUnitaDocAbilitati(getUser().getIdUtente(), getIdStrut());
		    DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb
			    .getTipiDocAbilitati(getUser().getIdUtente(), getIdStrut());
		    OrgSubStrutTableBean tmpSubStrutsTableBean = subStrutEjb
			    .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(),
				    getIdStrut());

		    String maxResultCompVol = configurationHelper.getValoreParamApplicByApplic(
			    CostantiDB.ParametroAppl.MAX_RESULT_COMP_VOL);
		    VolVListaCompVolTableBean listaComp = componentiHelper
			    .getVolVListaCompVolViewBean(new BigDecimal(idVolume),
				    getForm().getComponentiFiltri() != null
					    ? getForm().getComponentiFiltri()
					    : new ComponentiFiltri(),
				    null, tmpTableBeanTipoUD, tmpTableBeanTipoDoc,
				    tmpSubStrutsTableBean, Integer.parseInt(maxResultCompVol));
		    getForm().getComponentiList().getSelect_comp().reset();
		    getForm().getComponentiList().setTable(listaComp);
		    getForm().getComponentiList().getTable().setPageSize(10);
		    // Workaround in modo che la lista punti al primo record, non all'ultimo
		    getForm().getComponentiList().getTable().first();
		} catch (EMFError ex) {
		    log.error(ex.getDescription(), ex);
		}
	    }
	}

	// Se torno indietro dal dettaglio volume, rilancio la ricerca in quanto posso aver
	// modificato il volume
	// esaminato
	if (getLastPublisher().equals(Application.Publisher.VOLUMI_DETAIL)
		&& ((getRequest().getParameter("table") != null && getRequest()
			.getParameter("table").equals(getForm().getVolumiList().getName()))
			|| (getRequest().getParameter("mainNavTable") != null
				&& getRequest().getParameter("mainNavTable")
					.equals(getForm().getVolumiList().getName())))) {
	    try {
		int paginaCorrente = getForm().getVolumiList().getTable().getCurrentPageIndex();
		int inizio = getForm().getVolumiList().getTable().getFirstRowPageIndex();
		int pageSize = getForm().getVolumiList().getTable().getPageSize();
		String maxResultStandard = configurationHelper
			.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
		getForm().getVolumiList().setTable(volumiHelper.getVolVRicVolumeViewBean(
			getIdStrut(), getForm().getFiltri(), Integer.parseInt(maxResultStandard)));
		getForm().getVolumiList().getTable().setPageSize(pageSize);
		getForm().getVolumiList().getTable().first();
		// Rieseguo la query se necessario
		this.lazyLoadGoPage(getForm().getVolumiList(), paginaCorrente);
		// Ritorno alla pagina
		getForm().getVolumiList().getTable().setCurrentRowIndex(inizio);
		getForm().getVolumiList().setUserOperations(true, false, false, false);
	    } catch (EMFError ex) {
		log.error(ex.getDescription(), ex);
	    }
	}
    }

    /**
     * Bottone che rimanda alla Lista Operazioni Volume per il determinato volume
     *
     * @throws EMFError errore generico
     */
    @Override
    public void listaOperazioniVolume() throws EMFError {
	// LISTA OPERAZIONI VOLUME DA DETTAGLIO VOLUME
	MonitoraggioForm form = new MonitoraggioForm();
	BigDecimal idVol = getForm().getVolumiDetail().getId_volume_conserv().parse();
	BigDecimal idStrut = getForm().getVolumiDetail().getId_strut_volume().parse();
	redirectToAction(Application.Actions.MONITORAGGIO,
		"?operation=ricercaOperazioniVolumiDaDettaglioVolume&idVolumePerMon=" + idVol
			+ "&idStrutPerMon=" + idStrut + "&eseguiForward=true",
		form);
    }
}
