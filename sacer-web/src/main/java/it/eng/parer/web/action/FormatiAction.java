package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb.FormatoFileStandardEjb;
import static it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb.FormatoFileStandardEjb.VALUTAZIONE_ID;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.VerFormatiEnums;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.FormatiAbstractAction;
import it.eng.parer.slite.gen.form.FormatiForm;
import it.eng.parer.slite.gen.form.FormatiForm.EstensioneFile;
import it.eng.parer.slite.gen.form.FormatiForm.FormatoFileBusta;
import it.eng.parer.slite.gen.form.FormatiForm.FormatoFileStandard;
import it.eng.parer.slite.gen.form.FormatiForm.VisFormatoFileStandard;
import it.eng.parer.slite.gen.tablebean.DecEstensioneFileRowBean;
import it.eng.parer.slite.gen.tablebean.DecEstensioneFileTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileBustaRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileBustaTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.util.Iterator;
import javax.ejb.EJB;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormatiAction extends FormatiAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(FormatiAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileStandardEjb")
    private FormatoFileStandardEjb formatiEjb;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void inserisciFormatoFileStandard() {
        getForm().getFormatoFileStandard().clear();

        initFormatoComboBox();

        getForm().getFormatoFileStandard().setEditMode();
        getForm().getFormatoFileStandard().setStatus(Status.insert);
        getForm().getEstensioneFileList().clear();
        getForm().getFormatoFileStandardList().clear();
        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
    }

    public void initFormatoComboBox() {
        getForm().getFormatoFileStandard().getTi_esito_contr_formato().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_stato", VerFormatiEnums.IdoneitaFormato.values()));
        getForm().getFormatoFileStandard().getTi_esito_contr_formato()
                .setValue(VerFormatiEnums.IdoneitaFormato.IDONEO.name());
    }

    @Secure(action = "Menu.Amministrazione.GestioneFormatiFileStandard")
    public void ricercaFormatoFileStandard() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.GestioneFormatiFileStandard");

        getForm().getVisFormatoFileStandard().clear();

        getForm().getVisFormatoFileStandard().getNm_mimetype_file().setDecodeMap(DecodeMap.Factory
                .newInstance(formatiEjb.getMimetypeTableBean(), "nm_mimetype_file", "nm_mimetype_file"));

        getForm().getFormatoFileStandardList().setTable(formatiEjb.getDecFormatoFileStandardTableBean(null));
        getForm().getFormatoFileStandardList().getTable().first();
        getForm().getFormatoFileStandardList().getTable()
                .setPageSize(getForm().getFormatoFileStandardList().getTable().getPageSize() != 0
                        ? getForm().getFormatoFileStandardList().getTable().getPageSize()
                        : WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getFormatoFileStandardList().getTable().addSortingRule("nm_formato_file_standard", SortingRule.ASC);
        getForm().getFormatoFileStandardList().getTable().sort();

        getForm().getFormatoFileStandardList().setHideInsertButton(false);
        getForm().getFormatoFileStandardList().setHideUpdateButton(false);
        getForm().getFormatoFileStandardList().setHideDeleteButton(false);

        getForm().getVisFormatoFileStandard().setEditMode();
        getForm().getVisFormatoFileStandard().getVisFormatoButton().setEditMode();

        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_RICERCA);
    }

    @Secure(action = "Menu.AmministrazioneStrutture.GestioneRegistroFormati")
    public void visualizzaRegistroFormati() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneStrutture.GestioneRegistroFormati");

        getForm().getVisFormatoFileStandard().clear();

        getForm().getFormatoFileStandardList().setTable(formatiEjb.getDecFormatoFileStandardTableBean(null));
        getForm().getFormatoFileStandardList().getTable().first();
        getForm().getFormatoFileStandardList().getTable()
                .setPageSize(getForm().getFormatoFileStandardList().getTable().getPageSize() != 0
                        ? getForm().getFormatoFileStandardList().getTable().getPageSize()
                        : WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getFormatoFileStandardList().getTable().addSortingRule("nm_formato_file_standard", SortingRule.ASC);
        getForm().getFormatoFileStandardList().getTable().sort();

        getForm().getFormatoFileStandardList().setHideInsertButton(true);
        getForm().getFormatoFileStandardList().setHideUpdateButton(true);
        getForm().getFormatoFileStandardList().setHideDeleteButton(true);

        getForm().getVisFormatoFileStandard().setEditMode();
        getForm().getVisFormatoFileStandard().getVisFormatoButton().setEditMode();

        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_RICERCA);
    }

    @Override
    public void visFormatoButton() throws EMFError {

        VisFormatoFileStandard formatoFile;
        DecFormatoFileStandardRowBean fRowBean = new DecFormatoFileStandardRowBean();
        DecFormatoFileStandardTableBean formatoTableBean;

        formatoFile = getForm().getVisFormatoFileStandard();

        formatoFile.post(getRequest());
        formatoFile.copyToBean(fRowBean);

        formatoTableBean = formatiEjb.getDecFormatoFileStandardTableBean(fRowBean);

        getForm().getFormatoFileStandardList().setTable(formatoTableBean);
        getForm().getFormatoFileStandardList().getTable().first();
        getForm().getFormatoFileStandardList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getFormatoFileStandardList().getTable().addSortingRule("nm_formato_file_standard", SortingRule.ASC);
        getForm().getFormatoFileStandardList().getTable().sort();

        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_RICERCA);

    }

    @Override
    public void tabDecEstensioneFileOnClick() throws EMFError {

        getForm().getDecFormatoFileStandardTab()
                .setCurrentTab(getForm().getDecFormatoFileStandardTab().getDecEstensioneFile());

        DecEstensioneFileTableBean estensioneFileTable;
        DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();

        estensioneFileRowBean.setIdFormatoFileStandard(
                ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                        .getIdFormatoFileStandard());

        estensioneFileTable = formatiEjb.getDecEstensioneFileTableBean(estensioneFileRowBean);

        getForm().getEstensioneFileList().setTable(estensioneFileTable);
        getForm().getEstensioneFileList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getEstensioneFileList().getTable().first();

        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
    }

    @Override
    public void tabDecFormatoFileValutazioneOnClick() throws EMFError {

        getForm().getDecFormatoFileStandardTab()
                .setCurrentTab(getForm().getDecFormatoFileStandardTab().getDecFormatoFileValutazione());

        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
    }

    @Override
    public void tabDecFormatoFileBustaOnClick() throws EMFError {

        getForm().getDecFormatoFileStandardTab()
                .setCurrentTab(getForm().getDecFormatoFileStandardTab().getDecFormatoFileBusta());

        DecFormatoFileBustaTableBean formatoFileBustaTableBean;
        DecFormatoFileBustaRowBean formatoFileBustaRowBean = new DecFormatoFileBustaRowBean();

        formatoFileBustaRowBean.setIdFormatoFileStandard(
                ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                        .getIdFormatoFileStandard());

        formatoFileBustaTableBean = formatiEjb.getDecFormatoFileBustaTableBean(formatoFileBustaRowBean);

        getForm().getFormatoFileBustaList().setTable(formatoFileBustaTableBean);
        getForm().getFormatoFileBustaList().getTable().first();
        getForm().getFormatoFileBustaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        getForm().getFormatoFileBustaList()
                .setHideInsertButton(getForm().getFormatoFileStandardList().isHideInsertButton());
        getForm().getFormatoFileBustaList()
                .setHideUpdateButton(getForm().getFormatoFileStandardList().isHideUpdateButton());
        getForm().getFormatoFileBustaList()
                .setHideDeleteButton(getForm().getFormatoFileStandardList().isHideDeleteButton());

        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
    }

    @Override
    public void loadDettaglio() throws EMFError {

        String lista = getTableName();

        if (!getNavigationEvent().equals(NE_DETTAGLIO_INSERT)) {
            if (lista.equals(getForm().getFormatoFileStandardList().getName())
                    && (getForm().getFormatoFileStandardList().getTable() != null)) {

                getForm().getFormatoFileStandard().setViewMode();

                getForm().getFormatoFileStandard().setStatus(Status.view);
                getForm().getFormatoFileStandardList().setStatus(Status.view);

                getForm().getDecFormatoFileStandardTab()
                        .setCurrentTab(getForm().getDecFormatoFileStandardTab().getDecFormatoFileValutazione());

                BigDecimal idFormato = ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList()
                        .getTable().getCurrentRow()).getIdFormatoFileStandard();

                initFormatoComboBox();
                DecFormatoFileStandardRowBean formatoRowBean = formatiEjb.getDecFormatoFileStandardRowBean(idFormato);
                getForm().getFormatoFileStandard().copyFromBean(formatoRowBean);

                DecEstensioneFileTableBean estensioneFileTable;
                DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();
                estensioneFileRowBean.setIdFormatoFileStandard(idFormato);

                estensioneFileTable = formatiEjb.getDecEstensioneFileTableBean(estensioneFileRowBean);
                getForm().getEstensioneFileList().setTable(estensioneFileTable);
                getForm().getEstensioneFileList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getEstensioneFileList().getTable().first();

                getForm().getEstensioneFileList()
                        .setHideInsertButton(getForm().getFormatoFileStandardList().isHideInsertButton());
                getForm().getEstensioneFileList()
                        .setHideUpdateButton(getForm().getFormatoFileStandardList().isHideUpdateButton());
                getForm().getEstensioneFileList()
                        .setHideDeleteButton(getForm().getFormatoFileStandardList().isHideDeleteButton());
                getForm().getFormatoFileParametriValutazioneList()
                        .setTable(formatiEjb.getValutazioneFormatiTableBean(idFormato));
                getForm().getFormatoFileParametriValutazioneList()
                        .setHideUpdateButton(getForm().getFormatoFileStandardList().isHideUpdateButton());

            } else if (lista.equals(getForm().getEstensioneFileList().getName())
                    && (getForm().getEstensioneFileList().getTable() != null)
                    && (getForm().getEstensioneFileList().getTable().size() > 0)) {

                getForm().getEstensioneFile().setStatus(Status.view);
                getForm().getEstensioneFileList().setStatus(Status.view);

                BigDecimal idEstensioneFile = ((DecEstensioneFileRowBean) getForm().getEstensioneFileList().getTable()
                        .getCurrentRow()).getIdEstensioneFile();
                BigDecimal idFormatoFileStandard = ((DecFormatoFileStandardRowBean) getForm()
                        .getFormatoFileStandardList().getTable().getCurrentRow()).getIdFormatoFileStandard();

                DecEstensioneFileRowBean estensioneFileRowBean = formatiEjb
                        .getDecEstensioneFileRowBean(idEstensioneFile, idFormatoFileStandard);
                getForm().getEstensioneFile().copyFromBean(estensioneFileRowBean);

            } else if (lista.equals(getForm().getFormatoFileBustaList().getName())
                    && (getForm().getFormatoFileBustaList().getTable() != null)
                    && (getForm().getFormatoFileBustaList().getTable().size() > 0)) {

                getForm().getFormatoFileBusta().setStatus(Status.view);
                getForm().getFormatoFileBustaList().setStatus(Status.view);

                setFormatoFileBustaComboBox();

                BigDecimal idFormatoFileBusta = ((DecFormatoFileBustaRowBean) getForm().getFormatoFileBustaList()
                        .getTable().getCurrentRow()).getIdFormatoFileBusta();

                DecFormatoFileBustaRowBean formatoFileBustaRowBean = formatiEjb
                        .getDecFormatoFileBustaRowBean(idFormatoFileBusta);
                getForm().getFormatoFileBusta().copyFromBean(formatoFileBustaRowBean);

            } else if (lista.equals(getForm().getFormatoFileParametriValutazioneList().getName())
                    && (getForm().getFormatoFileParametriValutazioneList().getTable() != null)
                    && (getForm().getFormatoFileParametriValutazioneList().getTable().size() > 0)) {

                getForm().getParametroValutazione().setStatus(Status.view);
                getForm().getFormatoFileParametriValutazioneList().setStatus(Status.view);

                final BaseRow currentRow = (BaseRow) getForm().getFormatoFileParametriValutazioneList().getTable()
                        .getCurrentRow();
                initParametroValutazioneComboBox(
                        Long.class.cast(currentRow.getObject(formatiEjb.VALUTAZIONE_ID_GRP_PROP)));

                BaseRow parametroValutazioneRowBean = formatiEjb.getParametroValutazioneRowBean(currentRow);

                getForm().getParametroValutazione().copyFromBean(parametroValutazioneRowBean);
                getForm().getParametroValutazione().getId_proprieta()
                        .setValue("" + (Long) currentRow.getObject(FormatiForm.ParametroValutazione.id_proprieta));
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        getForm().getFormatoFileStandard().setViewMode();
        getForm().getFormatoFileStandardList().setStatus(Status.view);
        goBack();
    }

    @Override
    public void insertDettaglio() throws EMFError {
        String lista = getRequest().getParameter("table");

        if (lista.equals(getForm().getFormatoFileStandardList().getName())) {
            getForm().getEstensioneFileList().setTable(new DecEstensioneFileTableBean());
            getForm().getFormatoFileBustaList().setTable(new DecFormatoFileBustaTableBean());
            initFormatoComboBox();

            getForm().getFormatoFileStandard().clear();
            getForm().getFormatoFileStandard().setEditMode();
            getForm().getFormatoFileStandard().setStatus(Status.insert);
            getForm().getFormatoFileStandardList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getFormatoFileBustaList().getName())) {

            setFormatoFileBustaComboBox();
            getForm().getFormatoFileBusta().getTi_formato_firma_marca().setEditMode();
            getForm().getFormatoFileBusta().getTi_formato_firma_marca().clear();
            getForm().getFormatoFileBusta().setStatus(Status.insert);
            getForm().getFormatoFileBustaList().setStatus(Status.insert);

        } else if (lista.equals(getForm().getEstensioneFileList().getName())) {

            getForm().getEstensioneFile().getCd_estensione_file().setEditMode();
            getForm().getEstensioneFile().getCd_estensione_file().clear();
            getForm().getEstensioneFile().setStatus(Status.insert);
            getForm().getEstensioneFileList().setStatus(Status.insert);

        }
    }

    @Override
    public void saveDettaglio() throws EMFError {

        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL)) {
            salvaFormatoFileStandard();
        } else if (publisher.equals(Application.Publisher.ESTENSIONE_FILE_DETAIL)) {
            salvaEstensioneFile();
        } else if (publisher.equals(Application.Publisher.FORMATO_FILE_BUSTA_DETAIL)) {
            salvaFormatoFileBusta();
        } else if (publisher.equals(Application.Publisher.VALUTAZIONE_FILE_DETAIL)) {
            salvaParametroValutazione();
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {

        String lista = getRequest().getParameter("table");
        String action = getRequest().getParameter("navigationEvent");

        if (action != null && !action.equals(NE_DETTAGLIO_DELETE)) {

            if (lista.equals(getForm().getEstensioneFileList().getName())) {
                forwardToPublisher(Application.Publisher.ESTENSIONE_FILE_DETAIL);
            } else if (lista.equals(getForm().getFormatoFileBustaList().getName())) {
                forwardToPublisher(Application.Publisher.FORMATO_FILE_BUSTA_DETAIL);
            } else if (getForm().getFormatoFileStandardList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
            } else if (getForm().getFormatoFileParametriValutazioneList().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.VALUTAZIONE_FILE_DETAIL);
            }
        }

    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.FORMATO_FILE_STANDARD_RICERCA;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {

        if (getLastPublisher().equals(Application.Publisher.ESTENSIONE_FILE_DETAIL)) {

            DecEstensioneFileTableBean estensioneFileTableBean = new DecEstensioneFileTableBean();
            DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();

            estensioneFileRowBean.setIdFormatoFileStandard(
                    ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                            .getIdFormatoFileStandard());

            try {
                estensioneFileTableBean = formatiEjb.getDecEstensioneFileTableBean(estensioneFileRowBean);
            } catch (Exception ex) {
                logger.error("Eccezione", ex);
                getMessageBox().addFatal("Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
            }

            getForm().getEstensioneFileList().setTable(estensioneFileTableBean);
            getForm().getEstensioneFileList().getTable()
                    .setPageSize(getForm().getEstensioneFileList().getTable().getPageSize() != 0
                            ? getForm().getEstensioneFileList().getTable().getPageSize()
                            : WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getEstensioneFileList().setStatus(Status.view);

        } else if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_BUSTA_DETAIL)) {

            DecFormatoFileBustaTableBean formatoFileBustaTableBean = new DecFormatoFileBustaTableBean();
            DecFormatoFileBustaRowBean formatoFileBustaRowBean = new DecFormatoFileBustaRowBean();

            formatoFileBustaRowBean.setIdFormatoFileStandard(
                    ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                            .getIdFormatoFileStandard());

            try {
                formatoFileBustaTableBean = formatiEjb.getDecFormatoFileBustaTableBean(formatoFileBustaRowBean);
            } catch (Exception ex) {
                logger.error("Eccezione", ex);
                getMessageBox().addFatal("Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
            }

            getForm().getFormatoFileBustaList().setTable(formatoFileBustaTableBean);
            getForm().getFormatoFileBustaList().getTable()
                    .setPageSize(getForm().getFormatoFileBustaList().getTable().getPageSize() != 0
                            ? getForm().getFormatoFileBustaList().getTable().getPageSize()
                            : WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getFormatoFileBustaList().setStatus(Status.view);

        } else if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL)) {
            int paginaCorrente = getForm().getFormatoFileStandardList().getTable().getCurrentPageIndex();
            int inizio = getForm().getFormatoFileStandardList().getTable().getFirstRowPageIndex();
            int pageSize = getForm().getFormatoFileStandardList().getTable().getPageSize();
            getForm().getFormatoFileStandardList().setTable(formatiEjb.getDecFormatoFileStandardTableBean(null));
            getForm().getFormatoFileStandardList().getTable().addSortingRule("nm_formato_file_standard",
                    SortingRule.ASC);
            getForm().getFormatoFileStandardList().getTable().sort();
            getForm().getFormatoFileStandardList().getTable().setPageSize(pageSize);
            getForm().getFormatoFileStandardList().getTable().first();
            try {
                // Rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getFormatoFileStandardList(), paginaCorrente);
            } catch (EMFError ex) {
                logger.error("Eccezione", ex);
                getMessageBox().addFatal("Impossibile completare l'operazione, contattare l'assistenza tecnica", ex);
            }
            // Ritorno alla pagina
            getForm().getFormatoFileStandardList().getTable().setCurrentRowIndex(inizio);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.FORMATI;
    }

    private void setFormatoFileBustaComboBox() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        DecodeMap map = new DecodeMap();
        for (Enum e : WebConstants.SignerType.values()) {
            br.setString("ti_formato_firma_marca", e.name());
            bt.add(br);
        }

        map.populatedMap(bt, "ti_formato_firma_marca", "ti_formato_firma_marca");
        getForm().getFormatoFileBusta().getTi_formato_firma_marca().setDecodeMap(map);
    }

    private void initParametroValutazioneComboBox(Long idGruppoProprieta) {
        BaseTable proprietaTable = formatiEjb.getProprietaPerGruppo(idGruppoProprieta);
        DecodeMap map = new DecodeMap();
        map.populatedMap(proprietaTable, FormatiForm.ParametroValutazione.id_proprieta,
                formatiEjb.VALUTAZIONE_NM_PROPRIETA);
        getForm().getParametroValutazione().getId_proprieta().setDecodeMap(map);
    }

    private void salvaFormatoFileStandard() throws EMFError {
        getMessageBox().clear();

        FormatoFileStandard fileStandard = getForm().getFormatoFileStandard();
        fileStandard.post(getRequest());

        String[] fileConcatenabile = getRequest().getParameterValues("Fl_formato_concat");
        String flaggozzo = "0";
        // Se != da null, significa che ho spuntato il flag
        if (fileConcatenabile != null) {
            flaggozzo = "1";
        }

        // Imposto il flaggozzo nel front-end (in pratica ne faccio il post)
        getForm().getFormatoFileStandard().getFl_formato_concat().setChecked(flaggozzo.equals("1"));

        DecFormatoFileStandardRowBean fRowBean = new DecFormatoFileStandardRowBean();

        if (fileStandard.getNm_formato_file_standard().parse() == null) {
            getMessageBox().addError("Errore compilazione form: nome formato non inserito");

        } else {
            if (formatiEjb.getDecFormatoFileStandardRowBean(fileStandard.getNm_formato_file_standard().parse()) != null
                    && !(getForm().getFormatoFileStandard().getStatus().equals(Status.update))) {
                getMessageBox().addError("Errore compilazione form: nome formato già esistente");
            }
        }
        if (fileStandard.getDs_formato_file_standard().parse() == null) {
            getMessageBox().addError("Errore compilazione form: descrizione formato non inserito");
        }

        if (fileStandard.getCd_versione().parse() == null) {
            getMessageBox().addError("Errore compilazione form: codice versione formato non inserito");
        }

        if (fileStandard.getDs_copyright().parse() == null) {
            getMessageBox().addError("Errore compilazione form: descrizione copyright non inserita");
        }
        if (fileStandard.getNm_mimetype_file().parse() == null) {
            getMessageBox().addError("Errore compilazione form: nome mimetype non inserito");
        }
        if (fileStandard.getTi_esito_contr_formato().parse() == null) {
            getMessageBox().addError("Errore compilazione form: Tipo esito controllo formato non inserito");
        }
        try {
            fileStandard.getNi_punteggio_totale().parse();
        } catch (EMFError e) {
            getMessageBox().addError("Errore compilazione form: formato 'punteggio interoperabilità' non corretto");
        }

        if (getMessageBox().isEmpty()) {
            if (getForm().getFormatoFileStandard().getStatus().equals(Status.insert)) {

                fileStandard.copyToBean(fRowBean);
                String nmFormatoFileStandard = fRowBean.getNmFormatoFileStandard().toUpperCase();
                fRowBean.setNmFormatoFileStandard(nmFormatoFileStandard);
                formatiEjb.insertDecFormatoFileStandard(fRowBean);
                getMessageBox().addMessage(new Message(MessageLevel.INF, "Nuovo formato file salvato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);

                getForm().getFormatoFileStandardList().getTable().first();
                getForm().getFormatoFileStandardList().getTable().add(fRowBean);
                getForm().getFormatoFileParametriValutazioneList()
                        .setTable(formatiEjb.getValutazioneFormatiTableBean(fRowBean.getIdFormatoFileStandard()));
                getForm().getFormatoFileParametriValutazioneList()
                        .setHideUpdateButton(getForm().getFormatoFileStandardList().isHideUpdateButton());
            } else if (getForm().getFormatoFileStandard().getStatus().equals(Status.update)) {
                BigDecimal idFormato = ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList()
                        .getTable().getCurrentRow()).getIdFormatoFileStandard();

                fileStandard.copyToBean(fRowBean);
                formatiEjb.updateDecFormatoFileStandard(idFormato, fRowBean);
                getMessageBox().addMessage(new Message(MessageLevel.INF, "Formato file modificato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
            }
            getForm().getFormatoFileStandard().setViewMode();
            getForm().getFormatoFileStandard().setStatus(Status.view);
            getForm().getFormatoFileStandardList().setStatus(Status.view);
        }
        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
    }

    private void salvaEstensioneFile() throws EMFError {

        getMessageBox().clear();

        EstensioneFile estensioneFile = getForm().getEstensioneFile();
        String cdEstensioneFileOld = estensioneFile.getCd_estensione_file().parse();
        estensioneFile.post(getRequest());
        DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();
        estensioneFile.copyToBean(estensioneFileRowBean);

        if (estensioneFile.getCd_estensione_file().parse() == null) {
            getMessageBox().addError("Errore compilazione form: codice estensione non inserito");
        }

        if (getMessageBox().isEmpty()) {
            try {
                BigDecimal idFormatoFileStandard = null;
                idFormatoFileStandard = ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList()
                        .getTable().getCurrentRow()).getIdFormatoFileStandard();
                if (getForm().getEstensioneFile().getStatus().equals(Status.insert)) {

                    estensioneFileRowBean.setIdFormatoFileStandard(idFormatoFileStandard);
                    long idEstensioneFile = formatiEjb.insertDecEstensioneFile(estensioneFileRowBean);
                    estensioneFileRowBean.setIdEstensioneFile(BigDecimal.valueOf(idEstensioneFile));

                    getForm().getEstensioneFileList().getTable().last();
                    getForm().getEstensioneFileList().getTable().add(estensioneFileRowBean);

                    getMessageBox().addMessage(new Message(MessageLevel.INF, "Estensione file salvato con successo"));

                } else if (getForm().getEstensioneFile().getStatus().equals(Status.update)) {

                    estensioneFileRowBean.setIdFormatoFileStandard(idFormatoFileStandard);

                    BigDecimal idEstensioneFile = getForm().getEstensioneFile().getId_estensione_file().parse();

                    formatiEjb.updateDecEstensioneFile(idFormatoFileStandard, idEstensioneFile, cdEstensioneFileOld,
                            estensioneFile.getCd_estensione_file().parse());

                    getMessageBox()
                            .addMessage(new Message(MessageLevel.INF, "Estensione file modificata con successo"));
                }

                if (!getMessageBox().hasError()) {
                    getForm().getEstensioneFile().setStatus(Status.view);
                    getForm().getEstensioneFile().setViewMode();
                    getForm().getEstensioneFileList().setStatus(Status.view);
                    loadDettaglio();
                }
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.ESTENSIONE_FILE_DETAIL);
    }

    private void salvaFormatoFileBusta() throws EMFError {
        getMessageBox().clear();
        FormatoFileBusta formatoFileBusta = getForm().getFormatoFileBusta();
        formatoFileBusta.post(getRequest());
        DecFormatoFileBustaRowBean formatoFileBustaRowBean = new DecFormatoFileBustaRowBean();
        formatoFileBusta.copyToBean(formatoFileBustaRowBean);
        if (formatoFileBusta.getTi_formato_firma_marca().parse() == null) {
            getMessageBox().addError("Errore compilazione form: tipo firma marca non inserito");
        }

        if (getMessageBox().isEmpty()) {
            BigDecimal idFormatoFileStandard = null;
            idFormatoFileStandard = ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable()
                    .getCurrentRow()).getIdFormatoFileStandard();

            if (getForm().getFormatoFileBusta().getStatus().equals(Status.insert)) {
                try {
                    formatoFileBustaRowBean.setIdFormatoFileStandard(idFormatoFileStandard);

                    formatiEjb.insertDecFormatoFileBusta(formatoFileBustaRowBean);
                    getMessageBox().addMessage(new Message(MessageLevel.INF, "Estensione file salvato con successo"));
                } catch (ParerUserError e) {
                    getMessageBox().addError(e.getDescription());
                }
            }

            DecFormatoFileBustaTableBean formatoFileBustaTableBean;

            formatoFileBustaRowBean.setIdFormatoFileStandard(
                    ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                            .getIdFormatoFileStandard());

            formatoFileBustaTableBean = formatiEjb.getDecFormatoFileBustaTableBean(formatoFileBustaRowBean);

            getForm().getFormatoFileBustaList().setTable(formatoFileBustaTableBean);
            getForm().getFormatoFileBustaList().getTable().first();
            getForm().getFormatoFileBustaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

            getForm().getFormatoFileBusta().setStatus(Status.view);
            getForm().getFormatoFileBusta().setViewMode();
            getForm().getFormatoFileBustaList().setStatus(Status.view);

            getMessageBox().setViewMode(ViewMode.plain);
        }
        forwardToPublisher(Application.Publisher.FORMATO_FILE_BUSTA_DETAIL);
    }

    @Override
    public void deleteEstensioneFileList() throws EMFError {

        getMessageBox().clear();

        DecEstensioneFileRowBean estensioneFileRowBean;

        estensioneFileRowBean = ((DecEstensioneFileRowBean) getForm().getEstensioneFileList().getTable()
                .getCurrentRow());

        if (getMessageBox().isEmpty()) {
            formatiEjb.deleteDecEstensioneFile(estensioneFileRowBean);
        }

        forwardToPublisher(getLastPublisher());

        DecEstensioneFileTableBean estensioneFileTable;

        estensioneFileRowBean.setIdFormatoFileStandard(
                ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                        .getIdFormatoFileStandard());

        estensioneFileTable = formatiEjb.getDecEstensioneFileTableBean(estensioneFileRowBean);

        getForm().getEstensioneFileList().setTable(estensioneFileTable);
        getForm().getEstensioneFileList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getEstensioneFileList().getTable().first();

    }

    @Override
    public void deleteFormatoFileBustaList() throws EMFError {

        getMessageBox().clear();

        DecFormatoFileBustaRowBean formatoFileBustaRowBean;

        formatoFileBustaRowBean = ((DecFormatoFileBustaRowBean) getForm().getFormatoFileBustaList().getTable()
                .getCurrentRow());

        if (getMessageBox().isEmpty()) {
            formatiEjb.deleteDecFormatoFileBusta(formatoFileBustaRowBean);
        }

        forwardToPublisher(getLastPublisher());

        DecFormatoFileBustaTableBean formatoFileBustaTableBean;

        formatoFileBustaRowBean.setIdFormatoFileStandard(
                ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable().getCurrentRow())
                        .getIdFormatoFileStandard());

        formatoFileBustaTableBean = formatiEjb.getDecFormatoFileBustaTableBean(formatoFileBustaRowBean);

        getForm().getFormatoFileBustaList().setTable(formatoFileBustaTableBean);
        getForm().getFormatoFileBustaList().getTable().first();
        getForm().getFormatoFileBustaList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void updateEstensioneFileList() throws EMFError {
        getForm().getEstensioneFile().getCd_estensione_file().setEditMode();
        getForm().getEstensioneFile().setStatus(Status.update);
        getForm().getEstensioneFileList().setStatus(Status.update);
    }

    @Override
    public void updateFormatoFileBustaList() throws EMFError {

        getForm().getFormatoFileBusta().getTi_formato_firma_marca().setEditMode();
        getForm().getFormatoFileBusta().setStatus(Status.update);
        getForm().getFormatoFileBustaList().setStatus(Status.update);
    }

    @Override
    public void updateFormatoFileParametriValutazioneList() throws EMFError {

        getForm().getParametroValutazione().getNm_formato_gruppo_proprieta().setEditMode();
        getForm().getParametroValutazione().getNm_formato_gruppo_proprieta().setReadonly(true);

        getForm().getParametroValutazione().getId_proprieta().setEditMode();
        getForm().getParametroValutazione().getNi_punteggio().setEditMode();
        getForm().getFormatoFileParametriValutazioneList().setStatus(Status.update);
    }

    @Override
    public void updateFormatoFileStandardList() throws EMFError {

        getForm().getFormatoFileStandard().setEditMode();
        getForm().getFormatoFileStandard().getNm_formato_file_standard().setViewMode();
        getForm().getFormatoFileStandard().getNi_punteggio_totale().setViewMode();
        getForm().getFormatoFileStandard().setStatus(Status.update);
        getForm().getFormatoFileStandardList().setStatus(Status.update);

    }

    // controllare se serve
    public void reloadEstensioniList() {

        DecEstensioneFileTableBean estensioneTable;
        DecFormatoFileStandardRowBean formatoRowBean;
        formatoRowBean = (DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList().getTable()
                .getCurrentRow();

        DecEstensioneFileRowBean estensioneFileRowBean = new DecEstensioneFileRowBean();
        estensioneFileRowBean.setIdFormatoFileStandard(formatoRowBean.getIdFormatoFileStandard());

        estensioneTable = formatiEjb.getDecEstensioneFileTableBean(estensioneFileRowBean);

        getForm().getEstensioneFileList().setTable(estensioneTable);
        getForm().getEstensioneFileList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getEstensioneFileList().getTable().first();
    }

    @Override
    public void deleteFormatoFileStandardList() throws EMFError {
        getMessageBox().clear();
        DecFormatoFileStandardRowBean formatoFileStandardRowBean = (DecFormatoFileStandardRowBean) getForm()
                .getFormatoFileStandardList().getTable().getCurrentRow();
        if (getMessageBox().isEmpty()) {
            try {
                formatiEjb.deleteDecFormatoFileStandard(formatoFileStandardRowBean);
                int rowIndex = getForm().getFormatoFileStandardList().getTable().getCurrentRowIndex();
                getForm().getFormatoFileStandardList().getTable().remove(rowIndex);
                getMessageBox().addInfo("Formato file standard eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.FORMATO_FILE_STANDARD_RICERCA);
    }

    private void salvaParametroValutazione() throws EMFError {
        getMessageBox().clear();
        FormatiForm.ParametroValutazione parametroValutazione = getForm().getParametroValutazione();
        parametroValutazione.post(getRequest());
        if (!parametroValutazione.getNi_punteggio().check()) {
            getMessageBox().addError("Il punteggio dev'essere un numero  maggiore o uguale a 0");
        } else {
            // recuper l'id della valutazione dalla tabel, potrebbe essere nul se si tratta di un nuovo inserimento
            Long idValutazione = (Long) ((BaseRow) getForm().getFormatoFileParametriValutazioneList().getTable()
                    .getCurrentRow()).getObject(VALUTAZIONE_ID);

            BaseRow parametroValutazioneRow = new BaseRow();
            parametroValutazione.copyToBean(parametroValutazioneRow);
            /* CHECK PUNTEGGIO VALORIZZATO */
            BigDecimal punteggio = parametroValutazioneRow
                    .getBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio);
            BigDecimal idProprieta = parametroValutazioneRow
                    .getBigDecimal(FormatiForm.ParametroValutazione.id_proprieta);

            if (idProprieta != null) {
                if (punteggio == null) {
                    getMessageBox().addError("Il punteggio è obbligatorio");
                } else if (punteggio.longValue() < 0) {
                    getMessageBox().addError("Il punteggio dev'essere un numero  maggiore o uguale a 0");
                } else {
                    // il punteggio è valido, controllo che la somma con tutte le altre valutazioni sia <= 20

                    Iterator<BaseRow> iterator = (Iterator<BaseRow>) getForm().getFormatoFileParametriValutazioneList()
                            .getTable().iterator();

                    BigDecimal somma = BigDecimal.ZERO;
                    while (iterator.hasNext()) {
                        BaseRow rigaValutazione = iterator.next();
                        BigDecimal p = rigaValutazione
                                .getBigDecimal(FormatiForm.FormatoFileParametriValutazioneList.ni_punteggio);
                        if (p != null) {
                            Object idValutazioneCorrente = rigaValutazione.getObject(VALUTAZIONE_ID);
                            // se è il punteggio della valutazione che stiamo modificando non lo metto nella somma, lo
                            // considererò fuori dal ciclo
                            if (idValutazioneCorrente == null || idValutazione == null
                                    || !idValutazione.equals(idValutazioneCorrente)) {
                                somma = somma.add(p);
                            }
                        }
                    }
                    // il punteggio che stiamo per inserire non è nella somma
                    somma = somma.add(punteggio);
                    if (somma.compareTo(BigDecimal.valueOf(20)) > 0) {
                        getMessageBox().addError("Con questa valutazione il punteggio totale sarebbe "
                                + somma.intValue() + " mentre il massimo consentito è 20 ");
                    }
                }
            } else {
                if (punteggio != null) {
                    getMessageBox().addError("Il punteggio può essere inserito solo si seleziona anche la valutazione");
                }
            }

            if (getMessageBox().isEmpty()) {

                BigDecimal idFormatoFile = ((DecFormatoFileStandardRowBean) getForm().getFormatoFileStandardList()
                        .getTable().getCurrentRow()).getIdFormatoFileStandard();

                if (idValutazione != null) {

                    if (idProprieta == null) {
                        // DELETE
                        formatiEjb.deleteDecFormatoValutazione(idValutazione);
                    } else {
                        // UPDATE
                        formatiEjb.updateDecFormatoValutazione(idValutazione, idProprieta.longValue(), punteggio);
                    }
                } else {
                    if (idProprieta != null) {
                        // INSERT
                        formatiEjb.insertDecFormatoValutazione(idFormatoFile, idProprieta.longValue(), punteggio);
                    }
                }
                DecFormatoFileStandardRowBean formatoFileAggiornato = new DecFormatoFileStandardRowBean();
                getForm().getFormatoFileStandard().copyToBean(formatoFileAggiornato);
                formatoFileAggiornato.setObject(FormatiForm.FormatoFileStandard.ni_punteggio_totale,
                        formatiEjb.calcolaPunteggioInteroperabilita(((DecFormatoFileStandardRowBean) getForm()
                                .getFormatoFileStandardList().getTable().getCurrentRow()).getIdFormatoFileStandard()));
                getForm().getFormatoFileStandard().copyFromBean(formatoFileAggiornato);
                getMessageBox().addInfo("Valutazione salvata con successo");
                BaseTable table = formatiEjb.getValutazioneFormatiTableBean(idFormatoFile);
                getForm().getFormatoFileParametriValutazioneList().setTable(table);
                getForm().getFormatoFileParametriValutazioneList().getTable().first();
                getForm().getFormatoFileParametriValutazioneList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                getForm().getParametroValutazione().setStatus(Status.view);
                getForm().getParametroValutazione().setViewMode();
                getForm().getFormatoFileParametriValutazioneList().setStatus(Status.view);

                getMessageBox().setViewMode(ViewMode.plain);
                goBackTo(Application.Publisher.FORMATO_FILE_STANDARD_DETAIL);
            }
        }
        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.VALUTAZIONE_FILE_DETAIL);
        }
    }

    @Override
    public JSONObject triggerParametroValutazioneId_proprietaOnTrigger() throws EMFError {
        getForm().getParametroValutazione().post(getRequest());
        final BigDecimal idProprieta = getForm().getParametroValutazione().getId_proprieta().parse();
        String punteggioDefault = "";
        if (idProprieta != null) {
            BigDecimal punteggio = formatiEjb.getPunteggioDefault(idProprieta.longValue());
            if (punteggio != null) {
                punteggioDefault = punteggio.toString();
            }
        }
        getForm().getParametroValutazione().getNi_punteggio().setValue(punteggioDefault);

        return getForm().getParametroValutazione().asJSON();
    }
}
