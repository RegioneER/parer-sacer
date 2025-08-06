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

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.ejb.DatiSpecificiEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb.FormatoFileStandardEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.CopiaStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.ejb.TipoRappresentazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.ejb.TipoStrutturaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutTipoStrutAbstractAction;
import it.eng.parer.slite.gen.form.StrutDatiSpecForm;
import it.eng.parer.slite.gen.form.StrutTipiForm;
import it.eng.parer.slite.gen.form.StrutTipoStrutForm;
import it.eng.parer.slite.gen.form.StrutTipoStrutForm.TipoCompDoc;
import it.eng.parer.slite.gen.form.StrutTipoStrutForm.TipoStrutDoc;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoCompDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoRapprCompTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StrutTipoStrutAction extends StrutTipoStrutAbstractAction {

    private static Logger log = LoggerFactory.getLogger(StrutDatiSpecAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/DatiSpecificiEjb")
    private DatiSpecificiEjb datiSpecEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoStrutturaDocEjb")
    private TipoStrutturaDocEjb tipoStrutDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoRappresentazioneEjb")
    private TipoRappresentazioneEjb tipoRapprEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileStandardEjb")
    private FormatoFileStandardEjb formatoFileStandardEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {

	String lista = getTableName();
	String action = getNavigationEvent();

	if (lista != null && (action != null && !action.equals(NE_DETTAGLIO_INSERT))) {

	    if (lista.equals(getForm().getTipoStrutDocList().getName())
		    && (getForm().getTipoStrutDocList().getTable() != null)
		    && (getForm().getTipoStrutDocList().getTable().size() > 0)) {

		getForm().getTipoStrutDoc().setViewMode();
		getForm().getTipoStrutDoc().setStatus(Status.view);
		getForm().getTipoStrutDocList().setStatus(Status.view);
		getForm().getTipoStrutDoc().getLogEventiTipoStrutDoc().setEditMode();

		BigDecimal idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm()
			.getTipoStrutDocList().getTable().getCurrentRow()).getIdTipoStrutDoc();

		DecTipoStrutDocRowBean tipoStrutDocRowBean = tipoStrutDocEjb
			.getDecTipoStrutDocRowBean(idTipoStrutDoc, null);
		getForm().getTipoStrutDoc().copyFromBean(tipoStrutDocRowBean);

		if (getForm().getTipoStrutDocList().getTable().size() > 0) {
		    /* Carico la lista dei "Tipi componente ammessi" */
		    DecTipoCompDocTableBean tipoCompDocTableBean = tipoStrutDocEjb
			    .getDecTipoCompDocTableBean(idTipoStrutDoc,
				    getForm().getTipoCompDocList().isFilterValidRecords());

		    getForm().getTipoCompDocList().setTable(tipoCompDocTableBean);
		    getForm().getTipoCompDocList().getTable().first();
		    getForm().getTipoCompDocList().getTable()
			    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

		    /* Carico la lista dei "Tipi documenti ammessi" */
		    // Lista tipo struttura documento ammessa
		    DecTipoStrutDocAmmessoTableBean tipoStrutDocTableBean = tipoDocEjb
			    .getDecTipoStrutDocAmmessoTableBeanByIdTipoStrutDoc(idTipoStrutDoc);
		    getForm().getTipoDocAmmessoDaTipoStrutDocList().setTable(tipoStrutDocTableBean);
		    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable().first();
		    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable()
			    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

		}

		String cessato = (String) getRequest().getParameter("cessato");
		if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
		    getForm().getTipoStrutDocList().setUserOperations(true, false, false, false);
		    getForm().getTipoCompDocList().setUserOperations(true, false, false, false);
		    getForm().getTipoDocAmmessoDaTipoStrutDocList().setUserOperations(true, false,
			    false, false);
		}
	    }
	    if (lista.equals(getForm().getTipoCompDocList().getName())
		    && (getForm().getTipoCompDocList().getTable() != null)) {
		BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
		OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
		if ("1".equals(struttura.getFlCessato())) {
		    getRequest().setAttribute("cessato", true);
		}

		setTipoCompDocComboBox();
		getForm().getTipoCompDoc().setViewMode();
		getForm().getTipoCompDoc().setStatus(Status.view);
		getForm().getTipoCompDocList().setStatus(Status.view);

		if (getForm().getTipoCompDocList().getTable().size() > 0) {
		    BigDecimal idTipoCompDoc = ((DecTipoCompDocRowBean) getForm()
			    .getTipoCompDocList().getTable().getCurrentRow()).getIdTipoCompDoc();

		    DecTipoCompDocRowBean tipoCompDocRowBean = tipoStrutDocEjb
			    .getDecTipoCompDocRowBean(idTipoCompDoc);
		    getForm().getTipoCompDoc().copyFromBean(tipoCompDocRowBean);

		    loadTipoCompDoclists(true);
		}

	    }
	}
	if (lista.equals(getForm().getFormatoFileAmmessoList().getName())) {
	    getForm().getFormatoFileAmmesso().setViewMode();
	    if (getNavigationEvent().equals(NE_DETTAGLIO_INSERT)) {
		//
		BigDecimal idStrut = ((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
			.getTable().getCurrentRow()).getIdStrut();

		BigDecimal idTipoCompDoc = getForm().getTipoCompDoc().getId_tipo_comp_doc().parse();
		String flGestiti = getForm().getTipoCompDoc().getFl_gestiti().parse();
		String flIdonei = getForm().getTipoCompDoc().getFl_idonei().parse();
		String flDeprecati = getForm().getTipoCompDoc().getFl_deprecati().parse();

		getForm().getFiltriFormatoFileDoc().clear();
		getForm().getFiltriFormatoFileDoc().setEditMode();
		getForm().getFiltriFormatoFileDoc().getNm_mimetype_file()
			.setDecodeMap(DecodeMap.Factory.newInstance(
				formatoFileStandardEjb.getMimetypeTableBean(), "nm_mimetype_file",
				"nm_mimetype_file"));

		/*
		 * Carico il tableBean dei formati file doc ancora da inserire in base ai flag
		 * GESTITI, IDONEI E ALTRI
		 */
		Set<String> recordDaControllare = new HashSet<>();
		DecFormatoFileDocTableBean formatoTableBean = formatoFileDocEjb
			.getDecFormatoFileAmmessoNotInList(idTipoCompDoc, idStrut, flGestiti,
				flIdonei, flDeprecati, null, null, recordDaControllare);
		getForm().getFormatoFileDocList().setTable(formatoTableBean);
		getForm().getFormatoFileDocList().getTable()
			.setPageSize(WebConstants.FORMATI_PAGE_SIZE);
		getForm().getFormatoFileDocList().getTable().addSortingRule(
			DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
		getForm().getFormatoFileDocList().getTable().sort();
		getForm().getFormatoFileDocList().getTable().first();

		// Devo caricare nella lista dei selezionati tutti i formati doc già inseriti,
		// ed escludere gli stessi dalla lista sopra
		DecFormatoFileDocTableBean formatiSelTableBean = formatoFileDocEjb
			.getDecFormatoFileAmmessoTableBean(
				getForm().getTipoCompDoc().getId_tipo_comp_doc().parse());
		//
		getForm().getSelectFormatoFileAmmessoList().setTable(formatiSelTableBean);
		getForm().getSelectFormatoFileAmmessoList().getTable()
			.setPageSize(WebConstants.FORMATI_PAGE_SIZE);
		getForm().getSelectFormatoFileAmmessoList().getTable().addSortingRule(
			DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
		getForm().getSelectFormatoFileAmmessoList().getTable().sort();
	    }
	}

	// Lista "Tipo Documento Ammesso"
	if (lista.equals(getForm().getTipoDocAmmessoDaTipoStrutDocList().getName())) {
	    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

	    DecTipoStrutDocAmmessoRowBean currentRow = (DecTipoStrutDocAmmessoRowBean) getForm()
		    .getTipoDocAmmessoDaTipoStrutDocList().getTable().getCurrentRow();
	    DecTipoDocTableBean table = tipoDocEjb.getDecTipoDocTableBean(idStrut, new Date());
	    DecodeMap mappaTipoStrutDoc = new DecodeMap();
	    mappaTipoStrutDoc.populatedMap(table, "id_tipo_doc", "nm_tipo_doc");
	    getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso()
		    .setDecodeMap(mappaTipoStrutDoc);
	    if (currentRow != null) {
		getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso()
			.setValue(currentRow.getIdTipoDoc().toString());
	    }
	    getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso().setViewMode();

	    getForm().getTipoDocAmmessoDaTipoStrutDoc().setStatus(Status.view);
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().setStatus(Status.view);
	}

	// Lista "Tipo Rappresentazione Componente"
	if (lista != null
		&& (lista.equals(getForm().getTipoRapprCompAmmessoDaTipoCompList().getName()))) {
	    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	    DecTipoRapprAmmessoRowBean currentRow = (DecTipoRapprAmmessoRowBean) getForm()
		    .getTipoRapprCompAmmessoDaTipoCompList().getTable().getCurrentRow();
	    DecTipoRapprCompTableBean table = tipoStrutDocEjb
		    .getDecTipoRapprCompTableBeanByIdStrut(idStrut, new Date());
	    DecodeMap mappaTipoStrutDoc = new DecodeMap();
	    mappaTipoStrutDoc.populatedMap(table, "id_tipo_rappr_comp", "nm_tipo_rappr_comp");
	    getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp()
		    .setDecodeMap(mappaTipoStrutDoc);
	    if (currentRow != null) {
		getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp()
			.setValue(currentRow.getIdTipoRapprComp().toString());
	    }
	    getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp().setViewMode();
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().setStatus(Status.view);
	}
    }

    @Override
    public void undoDettaglio() throws EMFError {

	String publisher = getLastPublisher();

	if (publisher.equals(Application.Publisher.TIPO_STRUT_DOC_DETAIL)
		&& getForm().getTipoStrutDoc().getStatus() != null
		&& getForm().getTipoStrutDoc().getStatus().toString().equals("insert")) {
	    goBack();
	} else if (publisher.equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)
		&& getForm().getTipoCompDoc().getStatus() != null
		&& getForm().getTipoCompDoc().getStatus().toString().equals("insert")) {
	    goBack();
	} else if (publisher.equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)
		&& getForm().getFormatoFileAmmesso().getStatus() != null
		&& getForm().getFormatoFileAmmesso().getStatus().toString().equals("insert")) {
	    goBack();
	} else if (publisher.equals(Application.Publisher.FORMATI_CONCATENABILI_DETAIL)
		&& getForm().getFormatoFileAmmesso().getStatus() != null
		&& getForm().getFormatoFileAmmesso().getStatus().toString().equals("update")) {
	    goBack();
	} else if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_STRUT_DOC_TIPO_DOC)
		&& getForm().getTipoDocAmmessoDaTipoStrutDoc().getStatus() != null
		&& getForm().getTipoDocAmmessoDaTipoStrutDoc().getStatus().toString()
			.equals("insert")) {
	    goBack();
	} else if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_COMP_TIPO_RAPPR_COMP)
		&& getForm().getTipoRapprCompAmmessoDaTipoComp().getStatus() != null
		&& getForm().getTipoRapprCompAmmessoDaTipoComp().getStatus().toString()
			.equals("insert")) {
	    goBack();
	} else {
	    loadDettaglio();
	}

    }

    @Override
    public void insertDettaglio() throws EMFError {

	String lista = getRequest().getParameter("table");
	Calendar data = Calendar.getInstance();
	SimpleDateFormat formatter = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
	Date today = data.getTime();
	String stringToday = formatter.format(today);

	if (lista.equals(getForm().getTipoCompDocList().getName())) {

	    getForm().getTipoCompDoc().setEditMode();
	    getForm().getTipoCompDoc().clear();
	    getForm().getTipoCompDoc().getDt_istituz().setValue(stringToday);
	    setTipoCompDocComboBox();
	    getForm().getTipoCompDoc().setStatus(Status.insert);
	    getForm().getTipoCompDocList().setStatus(Status.insert);

	} else if (lista.equals(getForm().getTipoStrutDocList().getName())) {
	    getForm().getTipoStrutDoc().setEditMode();
	    getForm().getTipoStrutDoc().clear();

	    getForm().getTipoStrutDoc().setStatus(Status.insert);
	    getForm().getTipoStrutDocList().setStatus(Status.insert);

	} else if (lista.equals(getForm().getAttribTipoCompList().getName())) {

	    getForm().getAttribTipoComp().setEditMode();
	    getForm().getAttribTipoComp().clear();
	    getForm().getAttribTipoComp().getDt_istituz().setValue(stringToday);

	    getForm().getAttribTipoComp().setStatus(Status.insert);
	    getForm().getAttribTipoCompList().setStatus(Status.insert);

	} else if (lista.equals(getForm().getTipoDocAmmessoDaTipoStrutDocList().getName())) {
	    getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso().clear();
	    getForm().getTipoDocAmmessoDaTipoStrutDoc().setEditMode();
	    getForm().getTipoDocAmmessoDaTipoStrutDoc().setStatus(Status.insert);
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().setStatus(Status.insert);
	} else if (lista.equals(getForm().getTipoRapprCompAmmessoDaTipoCompList().getName())) {
	    /* Pulisco la bombo */
	    getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp().clear();
	    getForm().getTipoRapprCompAmmessoDaTipoComp().setEditMode();
	    getForm().getTipoRapprCompAmmessoDaTipoComp().setStatus(Status.insert);
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().setStatus(Status.insert);
	}
	if (lista.equals(getForm().getFormatoFileAmmessoList().getName())) {
	    getForm().getSelectFormatoFileAmmessoList().getCd_estensione_file_busta()
		    .setHidden(true);
	    getForm().getSelectButtonList().setEditMode();
	    getForm().getFormatoFileAmmesso().setStatus(Status.insert);
	    getForm().getFormatoFileAmmessoList().setStatus(Status.insert);

	}

    }

    @Override
    public void saveDettaglio() throws EMFError {

	String publisher = getLastPublisher();

	if (publisher.equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)) {

	    salvaTipoCompDoc();

	}
	if (publisher.equals(Application.Publisher.TIPO_STRUT_DOC_DETAIL)) {
	    salvaTipoStrutDoc();

	}
	if (publisher.equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)) {
	    salvaFormatoFileAmmesso();

	}
	if (publisher.equals(Application.Publisher.FORMATI_CONCATENABILI_DETAIL)) {
	    salvaFormatoFileAmmessoDup();

	}
	if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_COMP_TIPO_RAPPR_COMP)) {
	    salvaTipoRapprComp();

	}

	if (publisher.equals(Application.Publisher.ASSOCIAZIONE_TIPO_STRUT_DOC_TIPO_DOC)) {
	    salvaTipoDocAmmesso();

	}
    }

    @Override
    public void dettaglioOnClick() throws EMFError {

	String lista = getTableName();
	String action = getNavigationEvent();

	getSession().setAttribute("lista", lista);

	if (getForm().getTipoStrutDocList().getName().equals(lista)) {

	    forwardToPublisher(Application.Publisher.TIPO_STRUT_DOC_DETAIL);

	} else if (getForm().getXsdDatiSpecList().getName().equals(lista)) {
	    DecTipoCompDocRowBean riga = (DecTipoCompDocRowBean) getForm().getTipoCompDocList()
		    .getTable().getCurrentRow();
	    boolean isNotContenuto = !riga.getTiUsoCompDoc()
		    .equals(CostantiDB.TipoUsoComponente.CONTENUTO);
	    if (NE_DETTAGLIO_INSERT.equals(action) && isNotContenuto) {
		// Solo i Componenti di tipo "CONTENUTO" posso avere dei dati specifici
		getMessageBox().addWarning(
			"Il tipo componente è diverso da CONTENUTO. \n Non è possibile associare un xsd.");
	    } else {
		/*
		 * Se l'evento non è delete allora fa la redirect
		 */
		boolean redirect = false;
		if (getNavigationEvent().equals(NE_DETTAGLIO_DELETE)) {
		    DecXsdDatiSpecRowBean xsdDatiSpecRowBean = ((DecXsdDatiSpecRowBean) getForm()
			    .getXsdDatiSpecList().getTable().getCurrentRow());
		    getMessageBox().clear();
		    Date dtSoppres = xsdDatiSpecRowBean.getDtSoppres();
		    Date today = Calendar.getInstance().getTime();
		    if (dtSoppres.compareTo(today) < 0) {
			getMessageBox().addError("Versione XSD gi\u00E0 disattivata in precedenza");
			forwardToPublisher(getLastPublisher());
		    } else {
			boolean isInUse = datiSpecEjb.isXsdDatiSpecInUse(xsdDatiSpecRowBean);
			boolean isInUseOnCampiRegole = datiSpecEjb.isXsdDatiSpecInUseOnCampi(
				xsdDatiSpecRowBean.getIdXsdDatiSpec(), "DATO_SPEC_UNI_DOC",
				"DATO_SPEC_DOC_PRINC");
			// se in uso non posso cancellare, ma posso disattivare
			if (isInUse || isInUseOnCampiRegole) {
			    // Mostra messaggio di disattivazione
			    getRequest().setAttribute("confermaDisattivazioneXsd", true);
			    forwardToPublisher(getLastPublisher());
			} else {
			    redirect = true;
			}
		    }
		} else {
		    redirect = true;
		}
		if (redirect) {
		    StrutDatiSpecForm form = new StrutDatiSpecForm();
		    Integer row = getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex();

		    StringBuilder string = new StringBuilder(
			    "?operation=listNavigationOnClick&navigationEvent=" + action + "&table="
				    + StrutDatiSpecForm.XsdDatiSpecList.NAME + "&riga="
				    + row.toString());
		    form.getXsdDatiSpecList().setTable(getForm().getXsdDatiSpecList().getTable());
		    /*
		     * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova
		     * form che porterò nella nuova action
		     */

		    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
		    form.getIdList().getId_strut().setValue(idStrut.toString());

		    if (getLastPublisher().equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)) {
			string.append("&idTipoCompDoc=")
				.append(((DecTipoCompDocRowBean) getForm().getTipoCompDocList()
					.getTable().getCurrentRow()).getIdTipoCompDoc().intValue());
			form.getIdList().getId_tipo_comp_doc()
				.setValue(((DecTipoCompDocRowBean) getForm().getTipoCompDocList()
					.getTable().getCurrentRow()).getIdTipoCompDoc().toString());
			getSession().setAttribute("lastPage", "tipoCompDoc");
			form.getTipoCompDocRif().getNm_tipo_comp_doc()
				.setValue(((DecTipoCompDocRowBean) getForm().getTipoCompDocList()
					.getTable().getCurrentRow()).getNmTipoCompDoc());
			form.getTipoStrutDocRif().getNm_tipo_strut_doc()
				.setValue(((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
					.getTable().getCurrentRow()).getNmTipoStrutDoc());
		    }

		    form.getStrutRif().getStruttura()
			    .setValue(getForm().getStrutRif().getStruttura().parse());
		    form.getStrutRif().getId_ente()
			    .setValue(getForm().getStrutRif().getId_ente().getDecodedValue());

		    // fisso a false tutte le autorizzazioni a procedere con le altre fasi della
		    // listNavigationOnClick
		    // in questa action
		    this.setInsertAction(false);
		    this.setEditAction(false);
		    this.setDeleteAction(false);
		    redirectToAction(Application.Actions.STRUT_DATI_SPEC, string.toString(), form);
		}
	    }
	} else if (!action.equals(NE_DETTAGLIO_DELETE)) {

	    if (getForm().getTipoCompDocList().getName().equals(lista)) {

		forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);

	    } else if (getForm().getFormatoFileAmmessoList().getName().equals(lista)) {

		getForm().getFormatoFileAmmesso().setViewMode();
		getForm().getFormatoFileAmmesso().setStatus(Status.view);
		getForm().getFormatoFileAmmessoList().setStatus(Status.view);

		forwardToPublisher(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL);
	    } else if (getForm().getTipoDocAmmessoDaTipoStrutDocList().getName().equals(lista)) {

		forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_STRUT_DOC_TIPO_DOC);

	    } else if (getForm().getTipoRapprCompAmmessoDaTipoCompList().getName().equals(lista)) {

		forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_COMP_TIPO_RAPPR_COMP);

	    }

	}
    }

    public void confermaDisattivazioneXsd() throws EMFError {
	StrutDatiSpecForm form = new StrutDatiSpecForm();
	Integer row = getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex();

	form.getXsdDatiSpecList().setTable(getForm().getXsdDatiSpecList().getTable());
	form.getXsdDatiSpecList().getTable().setCurrentRowIndex(row);
	/*
	 * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova form che
	 * porterò nella nuova action
	 */

	BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	form.getIdList().getId_strut().setValue(idStrut.toString());

	if (getLastPublisher().equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)) {
	    form.getIdList().getId_tipo_comp_doc()
		    .setValue(((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable()
			    .getCurrentRow()).getIdTipoCompDoc().toString());
	    getSession().setAttribute("lastPage", "tipoCompDoc");
	    form.getTipoCompDocRif().getNm_tipo_comp_doc()
		    .setValue(((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable()
			    .getCurrentRow()).getNmTipoCompDoc());
	    form.getTipoStrutDocRif().getNm_tipo_strut_doc()
		    .setValue(((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList().getTable()
			    .getCurrentRow()).getNmTipoStrutDoc());
	}

	form.getStrutRif().getStruttura().setValue(getForm().getStrutRif().getStruttura().parse());
	form.getStrutRif().getId_ente()
		.setValue(getForm().getStrutRif().getId_ente().getDecodedValue());

	// fisso a false tutte le autorizzazioni a procedere con le altre fasi della
	// listNavigationOnClick in questa
	// action
	this.setInsertAction(false);
	this.setEditAction(false);
	this.setDeleteAction(false);
	redirectToAction(Application.Actions.STRUT_DATI_SPEC, "?operation=confermaDisattivazione",
		form);
    }

    @Override
    public void elencoOnClick() throws EMFError {
	goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
	return Application.Publisher.TIPO_STRUT_DOC_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {

	if (publisherName.equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)) {
	    loadTipoCompDoclists(false);
	    getForm().getXsdDatiSpecList().setStatus(Status.view);
	    getForm().getFormatoFileAmmessoList().setStatus(Status.view);

	} else if (getLastPublisher().equals(Application.Publisher.TIPO_COMP_DOC_DETAIL)) {
	    final BigDecimal idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm()
		    .getTipoStrutDocList().getTable().getCurrentRow()).getIdTipoStrutDoc();
	    DecTipoCompDocTableBean tipoCompDocTableBean = tipoStrutDocEjb
		    .getDecTipoCompDocTableBean(idTipoStrutDoc,
			    getForm().getTipoCompDocList().isFilterValidRecords());
	    getForm().getTipoCompDocList().setTable(tipoCompDocTableBean);
	    getForm().getTipoCompDocList().getTable().first();
	    getForm().getTipoCompDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	    getForm().getTipoCompDocList().setStatus(Status.view);
	} else if (publisherName.equals(Application.Publisher.TIPO_STRUT_DOC_DETAIL)) {
	    DecTipoStrutDocRowBean tipoStrutDocRB = (DecTipoStrutDocRowBean) getForm()
		    .getTipoStrutDocList().getTable().getCurrentRow();
	    /* Carico la lista dei "Tipi documenti ammessi" */
	    // Lista tipo struttura documento ammessa
	    DecTipoStrutDocAmmessoTableBean tipoStrutDocTableBean = tipoDocEjb
		    .getDecTipoStrutDocAmmessoTableBeanByIdTipoStrutDoc(
			    tipoStrutDocRB.getIdTipoStrutDoc());
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().setTable(tipoStrutDocTableBean);
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable().first();
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable()
		    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	}
    }

    @Override
    public String getControllerName() {
	return Application.Actions.STRUT_TIPO_STRUT;
    }

    @Override
    public void tabDecFormatoFileAmmessoOnClick() throws EMFError {
	getForm().getDecTipoCompDocTab()
		.setCurrentTab(getForm().getDecTipoCompDocTab().getDecFormatoFileAmmesso());

	forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);
    }

    @Override
    public void tabDecTipoCompDocXsdDatiSpecOnClick() throws EMFError {
	getForm().getDecTipoCompDocTab()
		.setCurrentTab(getForm().getDecTipoCompDocTab().getDecTipoCompDocXsdDatiSpec());

	DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
	DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
	xsdDatiSpecRowBean.setIdTipoCompDoc(
		((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable().getCurrentRow())
			.getIdTipoCompDoc());
	xsdDatiSpecRowBean.setIdStrut(
		((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable().getCurrentRow())
			.getIdTipoCompDoc());
	xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
	xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.COMP.name());
	xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

	getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
	getForm().getXsdDatiSpecList().getTable().first();
	getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	getForm().getXsdDatiSpecList().setStatus(Status.view);

	forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);
    }

    @Override
    public void selectAmmissibili() throws Throwable {
	/* Ottengo i record spuntati */
	String[] indiciAssolutiFormatiAmmissibili = getRequest()
		.getParameterValues("Fl_formato_ammissibile");

	if (indiciAssolutiFormatiAmmissibili != null
		&& indiciAssolutiFormatiAmmissibili.length > 0) {
	    int indice = 0;
	    for (String comp : indiciAssolutiFormatiAmmissibili) {
		if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
		    DecFormatoFileDocRowBean row = (DecFormatoFileDocRowBean) getForm()
			    .getFormatoFileDocList().getTable()
			    .getRow((Integer.parseInt(comp) - indice));
		    getForm().getFormatoFileDocList().getTable()
			    .remove(Integer.parseInt(comp) - indice);
		    getForm().getSelectFormatoFileAmmessoList().add(row);
		    indice++;
		}
	    }
	    getForm().getSelectFormatoFileAmmessoList().getTable().sort();
	    getForm().getFormatoFileDocList().getTable().sort();
	}

	forwardToPublisher(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL);
    }

    @Override
    public void deselectAmmessi() throws Throwable {
	/* Ottengo i record spuntati */
	String[] indiciAssolutiFormatiAmmessi = getRequest()
		.getParameterValues("Fl_formato_ammesso");

	if (indiciAssolutiFormatiAmmessi != null && indiciAssolutiFormatiAmmessi.length > 0) {
	    int indice = 0;
	    for (String comp : indiciAssolutiFormatiAmmessi) {
		if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
		    DecFormatoFileDocRowBean row = (DecFormatoFileDocRowBean) getForm()
			    .getSelectFormatoFileAmmessoList().getTable()
			    .getRow((Integer.parseInt(comp) - indice));
		    getForm().getSelectFormatoFileAmmessoList().getTable()
			    .remove(Integer.parseInt(comp) - indice);
		    getForm().getFormatoFileDocList().add(row);
		    indice++;
		}
	    }
	    getForm().getSelectFormatoFileAmmessoList().getTable().sort();
	    getForm().getFormatoFileDocList().getTable().sort();
	}
	forwardToPublisher(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL);
    }

    /**
     * Bottone che serve per aggiungere tutti i formati file da una lista all'altra, nelle pagine di
     * inserimento e modifica formati file doc
     *
     * @throws Throwable errore generico
     */
    @Override
    public void select_all() throws Throwable {

	if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)) {
	    // Recupero i formati selezionati già salvati sul tipo componente, che avranno anche
	    // settato
	    // id_formato_file_ammesso
	    DecFormatoFileDocTableBean tb = new DecFormatoFileDocTableBean();
	    tb.addAll(getForm().getSelectFormatoFileAmmessoList().getTable());

	    // Aggiungo tutti i formati "ammissibili", che avranno id_formato_file_ammesso a null
	    DecFormatoFileDocTableBean tbAmmissibili = (DecFormatoFileDocTableBean) getForm()
		    .getFormatoFileDocList().getTable();
	    for (DecFormatoFileDocRowBean rbAmmissibili : tbAmmissibili) {
		getForm().getSelectFormatoFileAmmessoList().getTable().add(rbAmmissibili);
	    }

	    getForm().getSelectFormatoFileAmmessoList().getTable()
		    .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
	    getForm().getSelectFormatoFileAmmessoList().getTable().addSortingRule(
		    DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
	    getForm().getSelectFormatoFileAmmessoList().getTable().sort();
	    getForm().getSelectFormatoFileAmmessoList().getTable().first();
	    getForm().getFormatoFileDocList().getTable().removeAll();
	    forwardToPublisher(getLastPublisher());
	}
    }

    /**
     * Bottone che serve per rimuovere tutti i formati file dalla lista, nelle pagine di inserimento
     * e modifica formati file doc
     *
     * @throws Throwable errore generico
     */
    @Override
    public void deselect_all() throws Throwable {
	if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)) {
	    BigDecimal idTipoCompDoc = getForm().getTipoCompDoc().getId_tipo_comp_doc().parse();

	    DecFormatoFileDocTableBean formatoTableBean = formatoFileDocEjb
		    .getDecFormatoFileAmmessoTableBean(idTipoCompDoc);
	    getForm().getFormatoFileDocList().setTable(formatoTableBean);
	    getForm().getFormatoFileDocList().getTable()
		    .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
	    getForm().getFormatoFileDocList().getTable().addSortingRule(
		    DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
		    SortingRule.ASC);
	    getForm().getFormatoFileDocList().getTable().sort();
	    getForm().getFormatoFileDocList().getTable().first();
	    getForm().getSelectFormatoFileAmmessoList().getTable().removeAll();
	    forwardToPublisher(getLastPublisher());
	}
    }

    /**
     * Metodo per popolare la combobox relativa alla form corrispondente alla tabella DecTipoCompDoc
     */
    private void setTipoCompDocComboBox() {

	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	DecodeMap map = new DecodeMap();

	for (Enum e : WebConstants.TipoCompDocCombo.values()) {
	    br.setString("ti_uso_comp_doc", e.name());
	    bt.add(br);
	}
	map.populatedMap(bt, "ti_uso_comp_doc", "ti_uso_comp_doc");
	getForm().getTipoCompDoc().getTi_uso_comp_doc().setDecodeMap(map);

    }

    /**
     * Metodo per il salvataggio o la modifica di un'entità DecTipoCompDoc
     *
     * @throws EMFError errore generico
     */
    private void salvaTipoCompDoc() throws EMFError {
	getMessageBox().clear();
	DecTipoCompDocRowBean tipoCompDocRowBean = new DecTipoCompDocRowBean();

	TipoCompDoc tipoCompDoc = new TipoCompDoc();
	tipoCompDoc = getForm().getTipoCompDoc();
	tipoCompDoc.post(getRequest());
	BigDecimal idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
		.getTable().getCurrentRow()).getIdTipoStrutDoc();
	//
	if (tipoCompDoc.validate(getMessageBox())) {

	    if (tipoCompDoc.getNm_tipo_comp_doc().parse() == null) {
		getMessageBox().addError(
			"Errore di compilazione form: nome tipo componente non inserito</br>");
	    }
	    if (tipoCompDoc.getDs_tipo_comp_doc().parse() == null) {
		getMessageBox().addError(
			"Errore di compilazione form: descrizione tipo componente file non inserito</br>");
	    }
	    if (tipoCompDoc.getTi_uso_comp_doc().parse() == null) {
		getMessageBox().addError("Errore di compilazione form: tipo uso non inserito</br>");
	    }
	    if (tipoCompDoc.getDt_istituz().parse() == null) {
		getMessageBox().addError(
			"Errore di compilazione form: data istituzione non inserito</br>");
	    }
	    if (tipoCompDoc.getDt_soppres().parse() == null) {
		tipoCompDoc.getDt_soppres().setValue(getDefaultDate());
	    }
	    if (tipoCompDoc.getDt_istituz().parse() != null
		    && tipoCompDoc.getDt_istituz().parse() != null && tipoCompDoc.getDt_istituz()
			    .parse().after(tipoCompDoc.getDt_soppres().parse())) {
		getMessageBox().addError(
			"Errore di compilazione form: data disattivazione precedente a data istituzione</br>");
	    }

	    if (tipoStrutDocEjb.getDecTipoCompDocRowBean(tipoCompDoc.getNm_tipo_comp_doc().parse(),
		    idTipoStrutDoc) != null
		    && getForm().getTipoCompDoc().getStatus().equals(Status.insert)) {
		getMessageBox().addError(
			"Errore di compilazione form: nome tipo comp doc già presente nel database </br>");
	    }
	    try {
		if (getMessageBox().isEmpty()) {

		    tipoCompDoc.copyToBean(tipoCompDocRowBean);
		    tipoCompDocRowBean.setIdTipoStrutDoc(idTipoStrutDoc);
		    /*
		     * Codice aggiuntivo per il logging...
		     */
		    LogParam param = SpagoliteLogUtil.getLogParam(
			    configurationHelper.getValoreParamApplicByApplic(
				    CostantiDB.ParametroAppl.NM_APPLIC),
			    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
		    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
		    if (getForm().getTipoCompDoc().getStatus().equals(Status.insert)) {
			param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
			tipoStrutDocEjb.insertDecTipoCompDoc(param, tipoCompDocRowBean);
			getForm().getFormatoFileAmmessoList().clear();
			getMessageBox().addMessage(new Message(MessageLevel.INF,
				"Nuovo tipo componente documento salvato con successo"));
			getMessageBox().setViewMode(ViewMode.plain);
			tipoCompDocRowBean.setIdTipoStrutDoc(idTipoStrutDoc);
			getForm().getTipoCompDocList().getTable().last();
			getForm().getTipoCompDocList().getTable().add(tipoCompDocRowBean);
			getForm().getTipoCompDoc().getId_tipo_comp_doc()
				.setValue("" + tipoCompDocRowBean.getIdTipoCompDoc());
			loadTipoCompDoclists(true);
		    } else if (getForm().getTipoCompDoc().getStatus().equals(Status.update)) {
			param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
			BigDecimal idTipoCompDoc = ((DecTipoCompDocRowBean) getForm()
				.getTipoCompDocList().getTable().getCurrentRow())
				.getIdTipoCompDoc();
			tipoStrutDocEjb.updateDecTipoCompDoc(param, idTipoCompDoc,
				tipoCompDocRowBean);
			loadTipoCompDoclists(true);
		    }
		    getForm().getTipoCompDocList().getTable()
			    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
		    getForm().getTipoCompDoc().setViewMode();
		    getForm().getTipoCompDoc().setStatus(Status.view);
		    getForm().getTipoCompDocList().setStatus(Status.view);

		    getMessageBox().setViewMode(ViewMode.plain);
		}
		forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);
	    } catch (ParerUserError e) {
		getMessageBox().addError(e.getDescription());
		forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);
	    }
	}
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entità DecTipoStrutDoc
     *
     * @throws EMFError errore generico
     */
    private void salvaTipoStrutDoc() throws EMFError {

	getMessageBox().clear();

	DecTipoStrutDocRowBean tipoStrutDocRowBean = new DecTipoStrutDocRowBean();

	TipoStrutDoc tipoStrutDoc = new TipoStrutDoc();

	tipoStrutDoc = getForm().getTipoStrutDoc();
	tipoStrutDoc.post(getRequest());

	if (tipoStrutDoc.validate(getMessageBox())) {

	    // i flag devono essere inizializzati
	    if (tipoStrutDoc.getNm_tipo_strut_doc().parse() == null) {
		getMessageBox().addError(
			"Errore di compilazione form: nome tipo struttura non inserito</br>");
	    }
	    if (tipoStrutDoc.getDs_tipo_strut_doc().parse() == null) {
		getMessageBox().addError(
			"Errore di compilazione form: descrizione tipo struttura non inserito</br>");
	    }
	    if (tipoStrutDoc.getDt_istituz().parse() == null) {
		getMessageBox().addError(
			"Errore di compilazione form: data istituzione non inserita</br>");
	    }
	    if (tipoStrutDoc.getDt_soppres().parse() == null) {
		tipoStrutDoc.getDt_soppres().setValue(getDefaultDate());
	    }
	    if (tipoStrutDoc.getDt_istituz().parse() != null
		    && tipoStrutDoc.getDt_soppres().parse() != null && tipoStrutDoc.getDt_istituz()
			    .parse().after(tipoStrutDoc.getDt_soppres().parse())) {
		getMessageBox().addError(
			"Errore di compilazione form: data disattivazione precedente a data istituzione</br>");
	    }

	    try {
		if (getMessageBox().isEmpty()) {
		    tipoStrutDoc.copyToBean(tipoStrutDocRowBean);
		    BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
		    tipoStrutDocRowBean.setIdStrut(idStrut);
		    /*
		     * Codice aggiuntivo per il logging...
		     */
		    LogParam param = SpagoliteLogUtil.getLogParam(
			    configurationHelper.getValoreParamApplicByApplic(
				    CostantiDB.ParametroAppl.NM_APPLIC),
			    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
		    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
		    if (getForm().getTipoStrutDoc().getStatus().equals(Status.insert)) {
			param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
			tipoStrutDocEjb.insertDecTipoStrutDoc(param, tipoStrutDocRowBean);
			DecTipoStrutDocTableBean tcdTable = new DecTipoStrutDocTableBean();
			tcdTable.add(tipoStrutDocRowBean);
			getForm().getTipoStrutDocList().clear();
			getForm().getTipoStrutDocList().setTable(tcdTable);
			getForm().getTipoStrutDocList().getTable()
				.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
			getForm().getTipoStrutDocList().getTable().first();
			DecTipoCompDocTableBean tipoCompDocTableBean = new DecTipoCompDocTableBean();
			getForm().getTipoCompDocList().setTable(tipoCompDocTableBean);
			getForm().getTipoCompDocList().getTable().first();
			getForm().getTipoCompDocList().getTable()
				.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
			getMessageBox().addMessage(new Message(MessageLevel.INF,
				"Nuova struttura documento salvata con successo"));
		    } else if (getForm().getTipoStrutDoc().getStatus().equals(Status.update)) {
			// Da capire se va gestito o meno ...
			param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
			BigDecimal idTipoStrutDoc = null;
			idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
				.getTable().getCurrentRow()).getIdTipoStrutDoc();
			tipoStrutDocEjb.updateDecTipoStrutDoc(param, idTipoStrutDoc,
				tipoStrutDocRowBean);
			getForm().getTipoStrutDocList().getTable().setCurrentRowIndex(
				getForm().getTipoStrutDocList().getTable().getCurrentRowIndex());
		    }
		    getForm().getTipoStrutDoc().setViewMode();
		    getForm().getTipoStrutDoc().setStatus(Status.view);
		    getForm().getTipoStrutDocList().setStatus(Status.view);
		    getForm().getTipoStrutDoc().getLogEventiTipoStrutDoc().setEditMode();
		    getMessageBox().setViewMode(ViewMode.plain);
		}
		forwardToPublisher(Application.Publisher.TIPO_STRUT_DOC_DETAIL);
	    } catch (ParerUserError e) {
		getMessageBox().addError(e.getDescription());
		forwardToPublisher(Application.Publisher.TIPO_STRUT_DOC_DETAIL);
	    }
	}
    }

    private void salvaFormatoFileAmmesso() throws EMFError {
	List<BigDecimal> listaFormatiPreModifiche = new ArrayList<>();
	List<BigDecimal> listaFormatiPresentiOraDaEliminare = new ArrayList<>();
	List<BigDecimal> listaFormatiDaInserire = new ArrayList<>();

	// Ricavo la lista dei formati file che erano già salvati su DB
	for (DecFormatoFileDocRowBean row : (DecFormatoFileDocTableBean) getForm()
		.getFormatoFileAmmessoList().getTable()) {
	    listaFormatiPreModifiche.add(row.getBigDecimal("id_formato_file_ammesso"));
	}

	/* Elimino i formati che erano presenti ed ora ho tolto */
	// Scorro i formati inizialmente presenti
	for (BigDecimal idFormatoFileAmmessoPreModifiche : listaFormatiPreModifiche) {
	    boolean presente = false;
	    // Scorro i formati post modifiche e controllo quelli che non ci sono più
	    for (DecFormatoFileDocRowBean row : (DecFormatoFileDocTableBean) getForm()
		    .getSelectFormatoFileAmmessoList().getTable()) {
		// Controllo se il formato c'è ancora...
		if (row.getBigDecimal("id_formato_file_ammesso") != null
			&& idFormatoFileAmmessoPreModifiche != null
			&& idFormatoFileAmmessoPreModifiche
				.compareTo(row.getBigDecimal("id_formato_file_ammesso")) == 0) {
		    presente = true;
		    break;
		}
	    }
	    // Se non c'è più lo inserisco tra quelli da eliminare
	    if (!presente) {
		listaFormatiPresentiOraDaEliminare.add(idFormatoFileAmmessoPreModifiche);
	    }
	}

	// Quelli che ho trovato non esserci più, li elimino
	/*
	 * spostato nel nuovo metodo atomico if (!listaFormatiPresentiOraDaEliminare.isEmpty()) {
	 * struttureEjb.deleteDecFormatoFileAmmessoList(listaFormatiPresentiOraDaEliminare); }
	 */
	// Inserisco su DB i nuovi
	// Ricavo la lista dei "nuovi" da aggiungere
	BigDecimal idTipoCompDoc = ((DecTipoCompDocRowBean) getForm().getTipoCompDocList()
		.getTable().getCurrentRow()).getIdTipoCompDoc();
	for (DecFormatoFileDocRowBean row : (DecFormatoFileDocTableBean) getForm()
		.getSelectFormatoFileAmmessoList().getTable()) {
	    // Verifico che il row non sia già un formato salvato
	    if (row.getBigDecimal("id_formato_file_ammesso") == null) {
		listaFormatiDaInserire.add(row.getIdFormatoFileDoc());
	    }
	}
	// }
	/*
	 * Codice aggiuntivo per il logging...
	 */
	LogParam param = SpagoliteLogUtil.getLogParam(
		configurationHelper.getValoreParamApplicByApplic(
			CostantiDB.ParametroAppl.NM_APPLIC),
		getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
		SpagoliteLogUtil.getToolbarInsert());
	param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	DecTipoStrutDocRowBean bean = (DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
		.getTable().getCurrentRow();
	BigDecimal idTipoStrutDoc = bean.getIdTipoStrutDoc();
	tipoStrutDocEjb.deleteAndInsertDecFormatoFileAmmesso(param,
		listaFormatiPresentiOraDaEliminare, idTipoStrutDoc, idTipoCompDoc,
		listaFormatiDaInserire);
	// /*
	// * Aggiorno i flag (spuntati o no) nel dettaglio tipo componente in base al fatto che
	// siano presenti o meno
	// * tutti formati
	// */
	getMessageBox().addMessage(
		new Message(MessageLevel.INF, "Formati file ammessi salvati con successo"));
	getMessageBox().setViewMode(ViewMode.plain);
	DecFormatoFileDocTableBean formatoFileAmmessoTableBean = formatoFileDocEjb
		.getDecFormatoFileAmmessoTableBean(idTipoCompDoc);
	formatoFileAmmessoTableBean.addSortingRule(
		DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
	formatoFileAmmessoTableBean.sort();
	getForm().getFormatoFileAmmessoList().setTable(formatoFileAmmessoTableBean);
	getForm().getFormatoFileAmmessoList().getTable().first();
	getForm().getFormatoFileAmmessoList().getTable()
		.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

	// Ricarico il dettaglio
	DecTipoCompDocRowBean tipoCompDocRowBean = tipoStrutDocEjb
		.getDecTipoCompDocRowBean(idTipoCompDoc);
	getForm().getTipoCompDoc().copyFromBean(tipoCompDocRowBean);
	goBack();
    }

    private String getDefaultDate() {
	Calendar calendar = Calendar.getInstance();
	calendar.set(2444, 11, 31, 0, 0, 0);

	Date dtSoppress = null;
	dtSoppress = calendar.getTime();
	DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);

	return formato.format(dtSoppress);
    }

    @Override
    public void deleteFormatoFileAmmessoList() throws EMFError {
	DecFormatoFileDocRowBean formatoFileAmmessoRowBean = (DecFormatoFileDocRowBean) getForm()
		.getFormatoFileAmmessoList().getTable().getCurrentRow();
	int index = getForm().getFormatoFileAmmessoList().getTable().getCurrentRowIndex();
	BigDecimal idFormatoFileAmmesso = formatoFileAmmessoRowBean
		.getBigDecimal("id_formato_file_ammesso");
	String tiEsitoContrFormato = formatoFileAmmessoRowBean.getString("ti_esito_contr_formato");
	try {
	    /*
	     * Codice aggiuntivo per il logging...
	     */
	    StrutTipoStrutForm form = (StrutTipoStrutForm) SpagoliteLogUtil.getForm(this);
	    LogParam param = SpagoliteLogUtil.getLogParam(
		    configurationHelper
			    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
		    getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil
			    .getDetailActionNameDelete(form, form.getFormatoFileAmmessoList()));
	    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	    BigDecimal idTipoStrutDoc = getForm().getTipoStrutDoc().getId_tipo_strut_doc().parse();
	    String messaggio = tipoStrutDocEjb.deleteDecFormatoFileAmmesso(param,
		    idFormatoFileAmmesso, idTipoStrutDoc, tiEsitoContrFormato);
	    getForm().getFormatoFileAmmessoList().getTable().remove(index);
	    getMessageBox().addInfo("Formato ammesso eliminato con successo! <br><br>");
	    if (messaggio != null) {
		getMessageBox().addInfo(messaggio);
	    }
	    SessionManager.removeLastExecutionHistory(getSession());

	    /*
	     * TODO RICARICARE DETTAGLIO TIPO COMP DOC DOPO AVER CANCELLATO UN FORMATO AMMESSO PER
	     * VEDERE SE CAMBIANO I FLAG
	     */
	    BigDecimal idTipoCompDoc = ((DecTipoCompDocRowBean) getForm().getTipoCompDocList()
		    .getTable().getCurrentRow()).getIdTipoCompDoc();
	    //
	    DecTipoCompDocRowBean tipoCompDocRowBean = tipoStrutDocEjb
		    .getDecTipoCompDocRowBean(idTipoCompDoc);
	    getForm().getTipoCompDoc().copyFromBean(tipoCompDocRowBean);
	} catch (Exception e) {
	    getMessageBox().addError("Errore durante l'eliminazione del formato");
	}
	if (!getMessageBox().hasError()
		&& getLastPublisher().equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)) {
	    goBackTo(Application.Publisher.TIPO_COMP_DOC_DETAIL);
	} else {
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void deleteTipoCompDocList() throws EMFError {
	getMessageBox().clear();
	String lastPublisher = getLastPublisher();
	DecTipoCompDocRowBean tipoCompDocRowBean = (DecTipoCompDocRowBean) getForm()
		.getTipoCompDocList().getTable().getCurrentRow();
	try {
	    /*
	     * Codice aggiuntivo per il logging...
	     */
	    StrutTipoStrutForm form = (StrutTipoStrutForm) SpagoliteLogUtil.getForm(this);
	    LogParam param = SpagoliteLogUtil.getLogParam(
		    configurationHelper
			    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
		    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
	    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	    if (Application.Publisher.TIPO_COMP_DOC_DETAIL.equals(lastPublisher)) {
		param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
	    } else {
		param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
			form.getTipoCompDocList()));
	    }
	    tipoStrutDocEjb.deleteDecTipoCompDoc(param, tipoCompDocRowBean);
	    getMessageBox().addMessage(new Message(MessageLevel.INF,
		    "Tipo Componente documento eliminato con successo"));
	    if (Application.Publisher.TIPO_COMP_DOC_DETAIL.equals(lastPublisher)) {
		goBack();
	    }
	} catch (Exception e) {
	    getMessageBox().addError(e.getLocalizedMessage(), e);
	    if (!Application.Publisher.TIPO_COMP_DOC_DETAIL.equals(lastPublisher)
		    && !Application.Publisher.TIPO_STRUT_DOC_DETAIL.equals(lastPublisher)) {
		goBack();

	    } else if (Application.Publisher.TIPO_COMP_DOC_DETAIL.equals(lastPublisher)) {
		forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);
	    }

	}

	if (!getMessageBox().hasError()) {
	    final BigDecimal idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm()
		    .getTipoStrutDocList().getTable().getCurrentRow()).getIdTipoStrutDoc();
	    DecTipoCompDocTableBean tipoCompDocTableBean = tipoStrutDocEjb
		    .getDecTipoCompDocTableBean(idTipoStrutDoc,
			    getForm().getTipoCompDocList().isFilterValidRecords());
	    getForm().getTipoCompDocList().setTable(tipoCompDocTableBean);
	    getForm().getTipoCompDocList().getTable().first();
	    getForm().getTipoCompDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	}
    }

    /**
     * Metodo che cancella l'entità DecTipoStrutDoc corrispondente al record della lista selezionato
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteTipoStrutDocList() throws EMFError {
	getMessageBox().clear();
	String lastPublisher = getLastPublisher();

	DecTipoStrutDocRowBean tipoStrutDocRowBean = new DecTipoStrutDocRowBean();
	DecTipoCompDocRowBean tipoCompDocRowBean = new DecTipoCompDocRowBean();
	tipoStrutDocRowBean = (DecTipoStrutDocRowBean) getForm().getTipoStrutDocList().getTable()
		.getCurrentRow();

	tipoCompDocRowBean.setIdTipoStrutDoc(tipoCompDocRowBean.getIdTipoStrutDoc());
	try {
	    /*
	     * Codice aggiuntivo per il logging...
	     */
	    LogParam param = SpagoliteLogUtil.getLogParam(
		    configurationHelper
			    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
		    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
	    if (Application.Publisher.CREA_STRUTTURA.equalsIgnoreCase(param.getNomePagina())) {
		StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
		param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
			form.getTipoStrutDocList()));
	    } else {
		param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
	    }
	    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	    tipoStrutDocEjb.deleteDecTipoStrutDoc(param,
		    tipoStrutDocRowBean.getIdTipoStrutDoc().longValue());
	    getMessageBox().addMessage(
		    new Message(MessageLevel.INF, "Struttura documento eliminata con successo"));
	    final BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	    DecTipoStrutDocTableBean tipoStrutDocTableBean = tipoStrutDocEjb
		    .getDecTipoStrutDocTableBean(idStrut,
			    (getForm().getTipoStrutDocList().isFilterValidRecords() == null ? false
				    : getForm().getTipoStrutDocList().isFilterValidRecords()));
	    getForm().getTipoStrutDocList().setTable(tipoStrutDocTableBean);
	    getForm().getTipoStrutDocList().getTable().first();
	    getForm().getTipoStrutDocList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	    if (Application.Publisher.TIPO_STRUT_DOC_DETAIL.equals(lastPublisher)
		    || "".equals(lastPublisher)) {
		goBack();
	    }
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	    if (!Application.Publisher.TIPO_STRUT_DOC_DETAIL.equals(lastPublisher)
		    || "".equals(lastPublisher)) {
		goBack();
	    } else {
		forwardToPublisher(Application.Publisher.TIPO_STRUT_DOC_DETAIL);
	    }
	}
    }

    /**
     * Metodo che visualizza la form associata a DecTipoCompDoc in status update
     *
     * @throws EMFError errore generico
     */
    @Override
    public void updateTipoCompDocList() throws EMFError {
	getForm().getTipoCompDoc().setEditMode();
	getForm().getTipoCompDoc().setStatus(Status.update);
	getForm().getTipoCompDocList().setStatus(Status.update);
    }

    /**
     * Metodo che visualizza la form associata a DecTipoStrutDoc in status update
     *
     * @throws EMFError errore generico
     */
    @Override
    public void updateTipoStrutDocList() throws EMFError {
	getForm().getTipoStrutDoc().setEditMode();
	getForm().getTipoStrutDoc().setStatus(Status.update);
	getForm().getTipoStrutDocList().setStatus(Status.update);
    }

    private void loadTipoCompDoclists(boolean isFirst) {

	HashMap<String, Integer> indMap = new HashMap(6);

	if (!isFirst) {

	    indMap.put("tipoFormatoFileAmmesso",
		    getForm().getFormatoFileAmmessoList().getTable().getCurrentRowIndex());
	    indMap.put("xsdDatiSpec",
		    getForm().getXsdDatiSpecList().getTable().getCurrentRowIndex());
	    indMap.put("tipoRapprComp", getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable()
		    .getCurrentRowIndex());
	}

	// Lista xsd
	DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
	DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
	try {
	    xsdDatiSpecRowBean.setIdStrut(getForm().getIdList().getId_strut().parse());
	} catch (EMFError ex) {
	    log.error(ex.getMessage(), ex);
	}
	DecTipoCompDocRowBean tipocompDocBean = (DecTipoCompDocRowBean) getForm()
		.getTipoCompDocList().getTable().getCurrentRow();
	if (tipocompDocBean != null) {
	    xsdDatiSpecRowBean.setIdTipoCompDoc(tipocompDocBean.getIdTipoCompDoc());
	    xsdDatiSpecRowBean.setTiUsoXsd(CostantiDB.TipiUsoDatiSpec.VERS.name());
	    xsdDatiSpecRowBean.setTiEntitaSacer(CostantiDB.TipiEntitaSacer.COMP.name());
	    xsdDatiSpecTableBean = datiSpecEjb.getDecXsdDatiSpecTableBean(xsdDatiSpecRowBean);

	    getForm().getXsdDatiSpecList().setTable(xsdDatiSpecTableBean);
	    getForm().getXsdDatiSpecList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	}
	// Lista formati file ammesso
	BigDecimal idTipoCompDoc = ((DecTipoCompDocRowBean) getForm().getTipoCompDocList()
		.getTable().getCurrentRow()).getIdTipoCompDoc();

	DecFormatoFileDocTableBean formatoFileAmmessoTableBean = formatoFileDocEjb
		.getDecFormatoFileAmmessoTableBean(idTipoCompDoc);

	getForm().getFormatoFileAmmessoList().setTable(formatoFileAmmessoTableBean);
	getForm().getFormatoFileAmmessoList().getTable()
		.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

	// Lista tipo rappresentazione componente
	DecTipoRapprAmmessoTableBean tipoRapprAmmessoTableBean = tipoStrutDocEjb
		.getDecTipoRapprAmmessoTableBeanByIdTipoCompDoc(idTipoCompDoc);
	getForm().getTipoRapprCompAmmessoDaTipoCompList().setTable(tipoRapprAmmessoTableBean);
	getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable()
		.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

	if (isFirst) {

	    getForm().getFormatoFileAmmessoList().getTable().first();
	    getForm().getXsdDatiSpecList().getTable().first();
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable().first();

	} else {
	    getForm().getFormatoFileAmmessoList().getTable()
		    .setCurrentRowIndex(indMap.get("tipoFormatoFileAmmesso"));
	    getForm().getXsdDatiSpecList().getTable().setCurrentRowIndex(indMap.get("xsdDatiSpec"));
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable()
		    .setCurrentRowIndex(indMap.get("tipoRapprComp"));
	}

	String cessato = getRequest().getParameter("cessato");
	if ((StringUtils.isNotBlank(cessato) && "1".equals(cessato))
		|| getRequest().getAttribute("cessato") != null) {
	    getForm().getXsdDatiSpecList().setUserOperations(true, false, false, false);
	    getForm().getFormatoFileAmmessoList().setUserOperations(true, false, false, false);
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().setUserOperations(true, false, false,
		    false);
	}
    }

    public void loadXsdTipoCompDoc() throws EMFError {

	StrutDatiSpecForm form = new StrutDatiSpecForm();

	String riga = getRequest().getParameter("riga");
	Integer nr = Integer.parseInt(riga);
	getForm().getTipoCompDocList().getTable().setCurrentRowIndex(nr);

	/*
	 * Propago l'idStruttura che ho salvato in memoria, per passarlo con la nuova form che
	 * porterò nella nuova action
	 *
	 */
	BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	form.getIdList().getId_strut().setValue(idStrut.toString());

	StringBuilder string = new StringBuilder(
		"?operation=listNavigationOnClick&navigationEvent=dettaglioView&table="
			+ getForm().getXsdDatiSpecList().getName() + "&riga=0");

	DecXsdDatiSpecRowBean xsdRow = datiSpecEjb.getLastDecXsdDatiSpecRowBean(idStrut, null, null,
		((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable().getCurrentRow())
			.getIdTipoCompDoc());
	DecXsdDatiSpecTableBean xsdTable = new DecXsdDatiSpecTableBean();
	xsdTable.add(xsdRow);
	xsdTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	form.getXsdDatiSpecList().setTable(xsdTable);

	string.append("&idTipoCompDoc=").append(
		((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable().getCurrentRow())
			.getIdTipoCompDoc().intValue());
	form.getIdList().getId_tipo_comp_doc().setValue(
		((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable().getCurrentRow())
			.getIdTipoCompDoc().toString());
	getSession().setAttribute("lastPage", "tipoCompDoc");
	form.getTipoCompDocRif().getNm_tipo_comp_doc().setValue(
		((DecTipoCompDocRowBean) getForm().getTipoCompDocList().getTable().getCurrentRow())
			.getNmTipoCompDoc());
	form.getTipoStrutDocRif().getNm_tipo_strut_doc()
		.setValue(((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList().getTable()
			.getCurrentRow()).getNmTipoStrutDoc());

	form.getStrutRif().getNm_strut().setValue(getForm().getStrutRif().getNm_strut().parse());
	form.getStrutRif().getDs_strut().setValue(getForm().getStrutRif().getDs_strut().parse());
	form.getStrutRif().getId_ente()
		.setValue(getForm().getStrutRif().getId_ente().getDecodedValue());

	this.setInsertAction(false);
	this.setEditAction(false);
	this.setDeleteAction(false);
	redirectToAction(Application.Actions.STRUT_DATI_SPEC, string.toString(), form);

    }

    @Override
    public void filterInactiveRecordsTipoCompDocList() throws EMFError {
	int rowIndex = 0;
	int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
	if (getForm().getTipoCompDocList().getTable() != null) {
	    rowIndex = getForm().getTipoCompDocList().getTable().getCurrentRowIndex();
	    pageSize = getForm().getTipoCompDocList().getTable().getPageSize();
	}

	final BigDecimal idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
		.getTable().getCurrentRow()).getIdTipoStrutDoc();
	DecTipoCompDocTableBean tipoCompDocTableBean = tipoStrutDocEjb.getDecTipoCompDocTableBean(
		idTipoStrutDoc, getForm().getTipoCompDocList().isFilterValidRecords());

	getForm().getTipoCompDocList().setTable(tipoCompDocTableBean);

	getForm().getTipoCompDocList().getTable().setCurrentRowIndex(rowIndex);
	getForm().getTipoCompDocList().getTable().setPageSize(pageSize);
	forwardToPublisher(getLastPublisher());
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' TipoStrutDocAmmesso
     *
     * @throws EMFError errore generico
     */
    private void salvaTipoRapprComp() throws EMFError {
	getForm().getTipoRapprCompAmmessoDaTipoComp().post(getRequest());
	if (getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp().parse() == null) {
	    getMessageBox().addError(
		    "Errore di compilazione form: tipo rappresentazione componente non inserito<br/>");
	}
	try {
	    if (getMessageBox().isEmpty()) {
		/*
		 * Codice aggiuntivo per il logging...
		 */
		LogParam param = SpagoliteLogUtil.getLogParam(
			configurationHelper
				.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
			getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
		param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
		if (getForm().getTipoRapprCompAmmessoDaTipoComp().getStatus()
			.equals(Status.insert)) {
		    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
		    tipoStrutDocEjb.insertDecTipoRapprAmmesso(param,
			    getForm().getTipoCompDoc().getId_tipo_comp_doc().parse(),
			    getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp()
				    .parse());
		    getMessageBox().addMessage(new Message(MessageLevel.INF,
			    "Tipo rappresentazione componente inserito con successo!"));
		    /* Inserisco la riga in fondo alla tabella */
		    DecTipoRapprAmmessoRowBean row = tipoStrutDocEjb.getDecTipoRapprAmmessoRowBean(
			    getForm().getTipoCompDoc().getId_tipo_comp_doc().parse(),
			    getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp()
				    .parse());
		    getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable().last();
		    getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable().add(row);
		} else if (getForm().getTipoRapprCompAmmessoDaTipoComp().getStatus()
			.equals(Status.update)) {
		    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
		    /*
		     * Ricavo il valore dell'idTipoStrutDoc già memorizzato su DB e di quello che
		     * sto modificando
		     */
		    BigDecimal idTipoRapprAmmessoDB = ((DecTipoRapprAmmessoRowBean) getForm()
			    .getTipoRapprCompAmmessoDaTipoCompList().getTable().getCurrentRow())
			    .getIdTipoRapprAmmesso();
		    BigDecimal idTipoCompDocDB = ((DecTipoRapprAmmessoRowBean) getForm()
			    .getTipoRapprCompAmmessoDaTipoCompList().getTable().getCurrentRow())
			    .getIdTipoCompDoc();
		    BigDecimal idTipoRapprInModifica = getForm().getTipoRapprCompAmmessoDaTipoComp()
			    .getId_tipo_rappr_comp().parse();
		    tipoStrutDocEjb.updateDecTipoRapprAmmesso(param, idTipoRapprAmmessoDB,
			    idTipoCompDocDB, idTipoRapprInModifica);
		    getMessageBox().addMessage(new Message(MessageLevel.INF,
			    "Tipo rappresentazione componente modificato con successo!"));
		    /*
		     * Setto sulla lista (ancora da ricaricare) il nuovo valore di idTipoStrutDoc,
		     * in maniera tale che in caso di immediata ri-modifica prenda su, dalla
		     * loadDettaglio, il valore corretto
		     */
		    ((DecTipoRapprAmmessoRowBean) getForm().getTipoRapprCompAmmessoDaTipoCompList()
			    .getTable().getCurrentRow()).setIdTipoRapprComp(idTipoRapprInModifica);
		}
		getMessageBox().setViewMode(ViewMode.plain);
		getForm().getTipoRapprCompAmmessoDaTipoComp().setViewMode();
		getForm().getTipoRapprCompAmmessoDaTipoCompList().setViewMode();
		getForm().getTipoRapprCompAmmessoDaTipoComp().setStatus(Status.view);
		getForm().getTipoRapprCompAmmessoDaTipoCompList().setStatus(Status.view);
		getMessageBox().setViewMode(ViewMode.plain);
	    }
	    forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_COMP_TIPO_RAPPR_COMP);
	} catch (ParerUserError e) {
	    getMessageBox().addError(e.getDescription());
	    forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_COMP_TIPO_RAPPR_COMP);
	}
    }

    @Override
    public void updateTipoRapprCompAmmessoDaTipoCompList() throws EMFError {
	/* Imposto il valore nella combo */
	BigDecimal idTipoRapprAmmesso = getForm().getTipoRapprCompAmmessoDaTipoComp()
		.getId_tipo_rappr_comp().parse();
	getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp()
		.setValue(idTipoRapprAmmesso.toString());

	getForm().getTipoRapprCompAmmessoDaTipoComp().getId_tipo_rappr_comp().setEditMode();
	getForm().getTipoRapprCompAmmessoDaTipoCompList().setStatus(Status.update);
	getForm().getTipoRapprCompAmmessoDaTipoComp().setStatus(Status.update);
    }

    @Override
    public void deleteTipoRapprCompAmmessoDaTipoCompList() {

	try {
	    BigDecimal idTipoRapprAmmesso = ((DecTipoRapprAmmessoTableBean) getForm()
		    .getTipoRapprCompAmmessoDaTipoCompList().getTable()).getCurrentRow()
		    .getIdTipoRapprAmmesso();
	    /*
	     * Codice aggiuntivo per il logging...
	     */
	    LogParam param = SpagoliteLogUtil.getLogParam(
		    configurationHelper
			    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
		    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
	    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	    if (Application.Publisher.TIPO_COMP_DOC_DETAIL
		    .equalsIgnoreCase(param.getNomePagina())) {
		StrutTipoStrutForm form = (StrutTipoStrutForm) SpagoliteLogUtil.getForm(this);
		param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
			form.getTipoRapprCompAmmessoDaTipoCompList()));
	    } else {
		param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
	    }
	    BigDecimal idTipoStrutDoc = ((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
		    .getTable().getCurrentRow()).getIdTipoStrutDoc();
	    tipoStrutDocEjb.deleteDecTipoRapprAmmesso(param, idTipoRapprAmmesso, idTipoStrutDoc);
	    getMessageBox().addMessage(new Message(MessageLevel.INF,
		    "Tipo rappresentazione componente eliminato con successo"));
	    BigDecimal idTipoCompDoc = getForm().getTipoCompDoc().getId_tipo_comp_doc().parse();
	    DecTipoRapprAmmessoTableBean tipoRapprAmmessoTableBean = tipoStrutDocEjb
		    .getDecTipoRapprAmmessoTableBeanByIdTipoCompDoc(idTipoCompDoc);
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().setTable(tipoRapprAmmessoTableBean);
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable().first();
	    getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable()
		    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	} catch (Exception e) {
	    getMessageBox().addError(e.getMessage());

	} finally {
	    if (Application.Publisher.ASSOCIAZIONE_TIPO_COMP_TIPO_RAPPR_COMP
		    .equals(getLastPublisher())) {
		goBack();
	    } else {
		forwardToPublisher(Application.Publisher.TIPO_COMP_DOC_DETAIL);

	    }
	}
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entita' TipoStrutDocAmmesso
     *
     * @throws EMFError errore generico
     */
    private void salvaTipoDocAmmesso() throws EMFError {
	getForm().getTipoDocAmmessoDaTipoStrutDoc().post(getRequest());
	if (getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso().parse() == null) {
	    getMessageBox()
		    .addError("Errore di compilazione form: tipo documento non inserito<br/>");
	}
	try {
	    if (getMessageBox().isEmpty()) {
		/*
		 * Codice aggiuntivo per il logging...
		 */
		LogParam param = SpagoliteLogUtil.getLogParam(
			configurationHelper
				.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
			getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
		param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
		if (getForm().getTipoDocAmmessoDaTipoStrutDoc().getStatus().equals(Status.insert)) {
		    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
		    /* Inserisco su DB */
		    tipoDocEjb.insertDecTipoStrutDocAmmesso(param,
			    getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso()
				    .parse(),
			    getForm().getTipoStrutDoc().getId_tipo_strut_doc().parse());
		    getMessageBox().addMessage(new Message(MessageLevel.INF,
			    "Tipo documento ammesso inserito con successo!"));
		    /* Inserisco la riga in fondo alla tabella */
		    DecTipoStrutDocAmmessoRowBean row = tipoDocEjb.getDecTipoStrutDocAmmessoRowBean(
			    getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso()
				    .parse(),
			    getForm().getTipoStrutDoc().getId_tipo_strut_doc().parse());
		    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable().last();
		    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable().add(row);
		} else if (getForm().getTipoDocAmmessoDaTipoStrutDoc().getStatus()
			.equals(Status.update)) {
		    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
		    /* Modifico su DB */
		    BigDecimal idTipoStrutDocAmmessoDB = ((DecTipoStrutDocAmmessoRowBean) getForm()
			    .getTipoDocAmmessoDaTipoStrutDocList().getTable().getCurrentRow())
			    .getIdTipoStrutDocAmmesso();
		    BigDecimal idTipoStrutDocDB = ((DecTipoStrutDocAmmessoRowBean) getForm()
			    .getTipoDocAmmessoDaTipoStrutDocList().getTable().getCurrentRow())
			    .getIdTipoStrutDoc();
		    BigDecimal idTipoDocInModifica = getForm().getTipoDocAmmessoDaTipoStrutDoc()
			    .getId_tipo_doc_ammesso().parse();
		    tipoDocEjb.updateDecTipoStrutDocAmmessoFromTipoStrutDoc(param,
			    idTipoStrutDocAmmessoDB, idTipoDocInModifica, idTipoStrutDocDB);
		    getMessageBox().addMessage(new Message(MessageLevel.INF,
			    "Tipo documento ammesso modificato con successo!"));
		    /*
		     * Setto sulla lista (ancora da ricaricare) il nuovo valore di idTipoStrutDoc,
		     * in maniera tale che in caso di immediata ri-modifica prenda su, dalla
		     * loadDettaglio, il valore corretto
		     */
		    ((DecTipoStrutDocAmmessoRowBean) getForm().getTipoDocAmmessoDaTipoStrutDocList()
			    .getTable().getCurrentRow()).setIdTipoDoc(idTipoDocInModifica);
		}
		getMessageBox().setViewMode(ViewMode.plain);
		getForm().getTipoDocAmmessoDaTipoStrutDoc().setViewMode();
		getForm().getTipoDocAmmessoDaTipoStrutDocList().setViewMode();
		getForm().getTipoDocAmmessoDaTipoStrutDoc().setStatus(Status.view);
		getForm().getTipoDocAmmessoDaTipoStrutDocList().setStatus(Status.view);
		getMessageBox().setViewMode(ViewMode.plain);
	    }
	    forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_STRUT_DOC_TIPO_DOC);
	} catch (ParerUserError e) {
	    getMessageBox().addError(e.getDescription());
	    forwardToPublisher(Application.Publisher.ASSOCIAZIONE_TIPO_STRUT_DOC_TIPO_DOC);
	}

    }

    @Override
    public void updateTipoDocAmmessoDaTipoStrutDocList() throws EMFError {
	/* Imposto il valore nella combo */
	BigDecimal idTipoDocAmmesso = getForm().getTipoDocAmmessoDaTipoStrutDoc()
		.getId_tipo_doc_ammesso().parse();
	getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso()
		.setValue(idTipoDocAmmesso.toString());

	getForm().getTipoDocAmmessoDaTipoStrutDoc().getId_tipo_doc_ammesso().setEditMode();
	getForm().getTipoDocAmmessoDaTipoStrutDocList().setStatus(Status.update);
	getForm().getTipoDocAmmessoDaTipoStrutDoc().setStatus(Status.update);
    }

    @Override
    public void deleteTipoDocAmmessoDaTipoStrutDocList() {

	try {
	    /*
	     * Codice aggiuntivo per il logging...
	     */
	    LogParam param = SpagoliteLogUtil.getLogParam(
		    configurationHelper
			    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
		    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
	    if (Application.Publisher.TIPO_STRUT_DOC_DETAIL
		    .equalsIgnoreCase(param.getNomePagina())) {
		StrutTipoStrutForm form = (StrutTipoStrutForm) SpagoliteLogUtil.getForm(this);
		param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
			form.getTipoDocAmmessoDaTipoStrutDocList()));
	    } else {
		param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
	    }
	    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	    BigDecimal idTipoStrutDocAmmesso = ((DecTipoStrutDocAmmessoTableBean) getForm()
		    .getTipoDocAmmessoDaTipoStrutDocList().getTable()).getCurrentRow()
		    .getIdTipoStrutDocAmmesso();
	    BigDecimal idTipoStrutDoc = getForm().getTipoStrutDoc().getId_tipo_strut_doc().parse();
	    BigDecimal idTipoDoc = ((DecTipoStrutDocAmmessoTableBean) getForm()
		    .getTipoDocAmmessoDaTipoStrutDocList().getTable()).getCurrentRow()
		    .getIdTipoDoc();
	    tipoDocEjb.deleteDecTipoStrutDocAmmesso(param, idTipoStrutDocAmmesso, idTipoDoc);
	    getMessageBox().addMessage(
		    new Message(MessageLevel.INF, "Tipo documento ammesso eliminato con successo"));
	    DecTipoStrutDocAmmessoTableBean tipoStrutDocAmmessoTableBean = tipoDocEjb
		    .getDecTipoDocAmmessoTableBeanByIdTipoStrutDoc(idTipoStrutDoc);
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().setTable(tipoStrutDocAmmessoTableBean);
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable().first();
	    getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable()
		    .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

	} catch (Exception e) {
	    getMessageBox().addError(e.getMessage());
	} finally {
	    if (Application.Publisher.ASSOCIAZIONE_TIPO_STRUT_DOC_TIPO_DOC
		    .equals(getLastPublisher())) {
		goBack();
	    } else {
		forwardToPublisher(Application.Publisher.TIPO_STRUT_DOC_DETAIL);

	    }

	}
    }

    private void redirectToTipoDocPage() throws EMFError {
	StrutTipiForm form = prepareRedirectToStrutTipi();
	redirectToPage(Application.Actions.STRUT_TIPI, form, form.getTipoDocList().getName(),
		getForm().getTipoDocAmmessoDaTipoStrutDocList().getTable(), getNavigationEvent());
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
	    BaseTableInterface<?> table, String event) throws EMFError {
	BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	OrgStrutRowBean struttura = struttureEjb.getOrgStrutRowBean(idStrut);
	((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate))
		.setTable(table);
	redirectToAction(action,
		"?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
			+ listToPopulate + "&riga=" + table.getCurrentRowIndex() + "&cessato="
			+ struttura.getFlCessato(),
		form);
    }

    private StrutTipiForm prepareRedirectToStrutTipi() throws EMFError {
	StrutTipiForm form = new StrutTipiForm();

	BigDecimal idStrut = getForm().getIdList().getId_strut().parse();

	// salvo l'idStrut in modo da poterlo propagare più avanti se necessario
	form.getIdList().getId_strut().setValue(idStrut.toString());

	form.getStrutRif().getNm_strut().setValue(getForm().getStrutRif().getNm_strut().parse());
	form.getStrutRif().getDs_strut().setValue(getForm().getStrutRif().getDs_strut().parse());
	form.getStrutRif().getId_ente().setValue(getForm().getStrutRif().getId_ente().parse());
	form.getStrutRif().getStruttura().setValue(getForm().getStrutRif().getStruttura().parse());
	return form;
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio Tipo Documento
     *
     * @throws EMFError errore generico
     */
    public void loadDettaglioTipoDoc() throws EMFError {
	BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	String riga = getRequest().getParameter("riga");
	BigDecimal numberRiga = BigDecimal.ZERO;
	if (StringUtils.isNotBlank(riga)) {
	    numberRiga = new BigDecimal(riga);
	}
	// Recupero il tipo doc
	BigDecimal idTipoDoc = ((DecTipoStrutDocAmmessoTableBean) getForm()
		.getTipoDocAmmessoDaTipoStrutDocList().getTable()).getRow(numberRiga.intValue())
		.getIdTipoDoc();
	DecTipoDocRowBean tipoDocRow = tipoDocEjb.getDecTipoDocRowBean(idTipoDoc, idStrut);
	// Setto la tabella dei tipi documento aggiungendo solo quella recuperata
	DecTipoDocTableBean tipoDocTable = new DecTipoDocTableBean();
	tipoDocTable.add(tipoDocRow);
	tipoDocTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	getForm().getTipoDocAmmessoDaTipoStrutDocList().setTable(tipoDocTable);
	setTableName(getForm().getTipoDocAmmessoDaTipoStrutDocList().getName());
	setNavigationEvent(NE_DETTAGLIO_VIEW);
	redirectToTipoDocPage();
    }

    public void loadDettaglioTipoRapprComp() throws EMFError {
	BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
	String riga = getRequest().getParameter("riga");
	BigDecimal numberRiga = BigDecimal.ZERO;
	if (StringUtils.isNotBlank(riga)) {
	    numberRiga = new BigDecimal(riga);
	}
	// Recupero il tipo rappr comp
	BigDecimal idTipoRapprComp = ((DecTipoRapprAmmessoTableBean) getForm()
		.getTipoRapprCompAmmessoDaTipoCompList().getTable()).getRow(numberRiga.intValue())
		.getIdTipoRapprComp();
	DecTipoRapprCompRowBean tipoRapprCompRow = tipoRapprEjb
		.getDecTipoRapprCompRowBean(idTipoRapprComp, idStrut);
	// Setto la tabella dei tipi rappresentazione componente aggiungendo solo quella recuperata
	DecTipoRapprCompTableBean tipoRapprCompTable = new DecTipoRapprCompTableBean();
	tipoRapprCompTable.add(tipoRapprCompRow);
	tipoRapprCompTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
	getForm().getTipoRapprCompAmmessoDaTipoCompList().setTable(tipoRapprCompTable);
	setTableName(getForm().getTipoRapprCompAmmessoDaTipoCompList().getName());
	setNavigationEvent(NE_DETTAGLIO_VIEW);
	redirectToTipoRapprCompPage();
    }

    private void redirectToTipoRapprCompPage() throws EMFError {
	StruttureForm form = prepareRedirectToStrutture();
	redirectToPage(Application.Actions.STRUTTURE, form, form.getTipoRapprCompList().getName(),
		getForm().getTipoRapprCompAmmessoDaTipoCompList().getTable(), getNavigationEvent());
    }

    private StruttureForm prepareRedirectToStrutture() throws EMFError {
	StruttureForm form = new StruttureForm();
	ArrayList<ExecutionHistory> executionHistory = SessionManager
		.getExecutionHistory(getSession());
	for (ExecutionHistory history : executionHistory) {
	    if (history.getName().equals(Application.Actions.STRUTTURE)) {
		form = (StruttureForm) history.getForm();
	    }
	}
	return form;
    }

    @Override
    public void logEventiTipoStrutDoc() throws EMFError {
	DecTipoStrutDocRowBean bean = (DecTipoStrutDocRowBean) getForm().getTipoStrutDocList()
		.getTable().getCurrentRow();
	logEventiCommon(SacerLogConstants.TIPO_OGGETTO_TIPO_STRUTTURA_DOCUMENTO,
		bean.getIdTipoStrutDoc());
    }

    private void logEventiCommon(String tipoOggetto, BigDecimal idOggetto) throws EMFError {
	GestioneLogEventiForm form = new GestioneLogEventiForm();
	form.getOggettoDetail().getNmApp().setValue(configurationHelper
		.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
	form.getOggettoDetail().getNm_tipo_oggetto().setValue(tipoOggetto);
	form.getOggettoDetail().getIdOggetto().setValue(idOggetto.toString());
	redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
		"?operation=inizializzaLogEventi", form);
    }

    //
    public void duplicaFormatoTipoCompOperation() throws EMFError {
	getForm().getFormatoFileAmmessoList().setStatus(Status.update);
	getForm().getFormatoFileAmmesso().setStatus(Status.update);
	getForm().getSelectConcatButtonList().setEditMode();

	String riga = getRequest().getParameter("riga");
	Integer nr = Integer.parseInt(riga);

	getForm().getFormatoFileAmmessoList().getTable().setCurrentRowIndex(nr);

	DecFormatoFileDocRowBean formatoAmmessoRB = (DecFormatoFileDocRowBean) getForm()
		.getFormatoFileAmmessoList().getTable().getCurrentRow();
	getForm().getFormatoFileDoc().copyFromBean(formatoAmmessoRB);

	Set<String> formatiSalvati = new HashSet<>();

	if (formatoAmmessoRB.getNmFormatoFileDoc().contains(".")) {

	    String[] formati = formatoAmmessoRB.getNmFormatoFileDoc().split("[.]");
	    formatiSalvati = new HashSet<>(Arrays.asList(formati));
	}

	// // Escludo anche i formati concatenabili non ammessi sulla struttura
	// List<String> formatiContenabiliSullaStruttura = formatoFileDocEjb
	// .getFormatiFileDocSingoliConcatenabili(getForm().getIdList().getId_strut().parse());
	// List<String> formatiStandardConcatenabili =
	// formatoFileStandardEjb.getFormatiFileStandardConcatenabili();
	// formatiStandardConcatenabili.removeAll(formatiContenabiliSullaStruttura);
	//
	// formatiSalvati.addAll(formatiStandardConcatenabili);
	//
	DecFormatoFileStandardTableBean concatenazioniMancantiTableBean = formatoFileStandardEjb
		.getDecFormatoFileStandardNotInList(formatiSalvati, Status.update);
	getForm().getFormatoFileStandardToCompList().setTable(concatenazioniMancantiTableBean);
	getForm().getFormatoFileStandardToCompList().getTable()
		.setPageSize(WebConstants.FORMATI_PAGE_SIZE);
	getForm().getFormatoFileStandardToCompList().getTable().addSortingRule(
		DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
		SortingRule.ASC);
	getForm().getFormatoFileStandardToCompList().getTable().sort();
	getForm().getFormatoFileStandardToCompList().getTable().first();

	getForm().getSelectFormatoFileStandardCompList()
		.setTable(new DecFormatoFileStandardTableBean());
	// Nascondo il campo cd_estensione_file e visualizzo il cd_entensione_file_busta
	getForm().getSelectFormatoFileStandardCompList().getCd_estensione_file().setHidden(true);
	getForm().getSelectFormatoFileStandardCompList().getCd_estensione_file_busta()
		.setHidden(false);
	getForm().getSelectFormatoFileStandardCompList().getTable()
		.setPageSize(WebConstants.FORMATI_PAGE_SIZE);
	getForm().getSelectFormatoFileStandardCompList().getTable().addSortingRule(
		DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
		SortingRule.ASC);
	getForm().getSelectFormatoFileStandardCompList().getTable().sort();

	forwardToPublisher(Application.Publisher.FORMATI_CONCATENABILI_DETAIL);
    }

    @Override
    public void selectConcatAmmissibili() throws Throwable {
	String[] indiciAssolutiFormatiAmmissibili = getRequest()
		.getParameterValues("Fl_formato_ammissibile");
	if (indiciAssolutiFormatiAmmissibili != null
		&& indiciAssolutiFormatiAmmissibili.length > 0) {
	    int indice = 0;
	    for (String comp : indiciAssolutiFormatiAmmissibili) {
		if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
		    DecFormatoFileStandardRowBean currentRow = (DecFormatoFileStandardRowBean) getForm()
			    .getFormatoFileStandardToCompList().getTable()
			    .getRow((Integer.parseInt(comp) - indice));

		    StringBuilder formato = new StringBuilder(
			    getForm().getFormatoFileDoc().getNm_formato_file_doc().parse());
		    formato.append(".").append(currentRow.getString("nm_formato_file_standard"));
		    getForm().getFormatoFileDoc().getNm_formato_file_doc()
			    .setValue(formato.toString());

		    getForm().getFormatoFileStandardToCompList().getTable()
			    .remove(Integer.parseInt(comp) - indice);

		    getForm().getSelectFormatoFileStandardCompList().add(currentRow);
		    reloadSelectFormatoFileStandardCompList();
		    indice++;
		}
	    }
	    getForm().getSelectFormatoFileStandardCompList().getTable().sort();
	    getForm().getFormatoFileStandardToCompList().getTable().sort();
	}
	forwardToPublisher(Application.Publisher.FORMATI_CONCATENABILI_DETAIL);
    }

    public void reloadSelectFormatoFileStandardCompList() throws EMFError {
	int inizio = getForm().getSelectFormatoFileStandardCompList().getTable()
		.getFirstRowPageIndex();
	int pageSize = getForm().getSelectFormatoFileStandardCompList().getTable().getPageSize();
	int paginaCorrente = getForm().getSelectFormatoFileStandardCompList().getTable()
		.getCurrentPageIndex();
	getForm().getSelectFormatoFileStandardCompList().getTable().sort();
	getForm().getSelectFormatoFileStandardCompList().getTable().setPageSize(pageSize);
	this.lazyLoadGoPage(getForm().getSelectFormatoFileStandardCompList(), paginaCorrente);
	getForm().getSelectFormatoFileStandardCompList().getTable().setCurrentRowIndex(inizio);
    }

    @Override
    public void deselectConcatAmmessi() throws Throwable {
	/* Ottengo i record spuntati */
	String[] indiciAssolutiFormatiAmmessi = getRequest()
		.getParameterValues("Fl_formato_ammesso");

	if (indiciAssolutiFormatiAmmessi != null && indiciAssolutiFormatiAmmessi.length > 0) {
	    int indice = 0;
	    for (String comp : indiciAssolutiFormatiAmmessi) {
		if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
		    DecFormatoFileStandardRowBean row = (DecFormatoFileStandardRowBean) getForm()
			    .getSelectFormatoFileStandardCompList().getTable()
			    .getRow((Integer.parseInt(comp) - indice));
		    getForm().getSelectFormatoFileStandardCompList().getTable()
			    .remove(Integer.parseInt(comp) - indice);
		    indice++;

		    HashSet<String> hs = new HashSet<>();
		    hs.add(row.getNmFormatoFileStandard());

		    getForm().getFormatoFileStandardToCompList().add(row);
		    String formato = getForm().getFormatoFileDoc().getNm_formato_file_doc().parse();
		    List<String> list = new ArrayList<>(Arrays.asList(formato.split("[.]")));
		    list.remove(row.getString("nm_formato_file_standard"));
		    getForm().getFormatoFileDoc().getNm_formato_file_doc()
			    .setValue(StringUtils.join(list, "."));

		}
	    }
	    getForm().getSelectFormatoFileStandardCompList().getTable().sort();
	    getForm().getFormatoFileStandardToCompList().getTable().sort();
	}
	forwardToPublisher(Application.Publisher.FORMATI_CONCATENABILI_DETAIL);
    }

    private void salvaFormatoFileAmmessoDup() throws EMFError {
	getMessageBox().clear();
	//
	BigDecimal idTipoCompDoc = getForm().getTipoCompDoc().getId_tipo_comp_doc().parse();
	/*
	 * Codice aggiuntivo per il logging...
	 */
	LogParam param = SpagoliteLogUtil.getLogParam(
		configurationHelper
			.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
		getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
	param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
	param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());

	DecFormatoFileDocRowBean formato = (DecFormatoFileDocRowBean) getForm()
		.getFormatoFileAmmessoList().getTable().getCurrentRow();

	Set<String> standard = new HashSet<>();
	standard.add(formato.getNmFormatoFileDoc().toUpperCase());
	DecFormatoFileStandardTableBean formatiStandard = new DecFormatoFileStandardTableBean();

	// se voglio duplicareformatiNonEliminati
	formato = new DecFormatoFileDocRowBean();
	formato.setIdStrut(getForm().getIdList().getId_strut().parse());

	formato.setNmFormatoFileDoc(
		getForm().getFormatoFileDoc().getNm_formato_file_doc().parse().toUpperCase());
	formato.setDsFormatoFileDoc(getForm().getFormatoFileDoc().getDs_formato_file_doc().parse());
	formato.setCdVersione(getForm().getFormatoFileDoc().getCd_versione().parse());

	Calendar calendar = Calendar.getInstance();
	calendar.set(2011, Calendar.JANUARY, 1, 0, 0, 0);
	formato.setDtIstituz(new Timestamp(calendar.getTimeInMillis()));
	calendar.set(2444, 11, 31, 0, 0, 0);
	formato.setDtSoppres(new Timestamp(calendar.getTimeInMillis()));
	// Devo ricreare tutti i record di usoFormatiStandard, perciò aggiungo alla lista anche il
	// primo
	// dovrebbe essere passato Status.update, ma usandolo non carica i record giusti
	// FIXMEPLEASE: Per prendere un record, mi pare una roba terribile.
	formatiStandard.add(formatoFileStandardEjb.getDecFormatoFileStandardInList(standard,
		Status.insert, getForm().getIdList().getId_strut().parse()).getRow(0));

	for (DecFormatoFileStandardRowBean row : (DecFormatoFileStandardTableBean) getForm()
		.getSelectFormatoFileStandardCompList().getTable()) {
	    formatiStandard.add(row);
	}
	try {
	    formatoFileDocEjb.addFormatoFileDocTipoComp(param, formato, formatiStandard, true,
		    idTipoCompDoc);
	} catch (ParerUserError ex) {
	    getMessageBox().addError(ex.getDescription());
	}

	if (!getMessageBox().hasError()) {
	    getForm().getFormatoFileDocList().setStatus(Status.view);
	    getForm().getFormatoFileDoc().setStatus(Status.view);
	    getMessageBox().addInfo("Formato salvato con successo!");
	    getMessageBox().setViewMode(ViewMode.plain);
	    goBack();
	} else {
	    forwardToPublisher(getLastPublisher());
	}
    }

    @Override
    public void ricercaFormatoButton() throws EMFError {
	getForm().getFiltriFormatoFileDoc().post(getRequest());
	BigDecimal idStrut = ((DecTipoStrutDocRowBean) getForm().getTipoStrutDocList().getTable()
		.getCurrentRow()).getIdStrut();

	BigDecimal idTipoCompDoc = getForm().getTipoCompDoc().getId_tipo_comp_doc().parse();
	String flGestiti = getForm().getTipoCompDoc().getFl_gestiti().parse();
	String flIdonei = getForm().getTipoCompDoc().getFl_idonei().parse();
	String flDeprecati = getForm().getTipoCompDoc().getFl_deprecati().parse();

	String nome = getForm().getFiltriFormatoFileDoc().getNm_formato_file_standard().parse();
	String mimetype = getForm().getFiltriFormatoFileDoc().getNm_mimetype_file().parse();

	Set<String> recordPresenti = new HashSet<>();

	// Ricavo in un set l'insieme dei record già ammessi
	for (DecFormatoFileDocRowBean row : (DecFormatoFileDocTableBean) getForm()
		.getSelectFormatoFileAmmessoList().getTable()) {
	    recordPresenti.add(row.getNmFormatoFileDoc());
	}

	DecFormatoFileDocTableBean formatoTableBean = formatoFileDocEjb
		.getDecFormatoFileAmmessoNotInList(idTipoCompDoc, idStrut, flGestiti, flIdonei,
			flDeprecati, nome, mimetype, recordPresenti);
	getForm().getFormatoFileDocList().setTable(formatoTableBean);
	getForm().getFormatoFileDocList().getTable().setPageSize(WebConstants.FORMATI_PAGE_SIZE);
	getForm().getFormatoFileDocList().getTable().addSortingRule(
		DecFormatoFileDocTableDescriptor.COL_NM_FORMATO_FILE_DOC, SortingRule.ASC);
	getForm().getFormatoFileDocList().getTable().sort();
	getForm().getFormatoFileDocList().getTable().first();

	forwardToPublisher(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL);
    }

}
