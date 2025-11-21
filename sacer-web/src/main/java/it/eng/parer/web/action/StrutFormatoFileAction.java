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

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.ejb.FormatoFileDocEjb;
import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.ejb.FormatoFileStandardEjb;
import org.apache.commons.lang3.StringUtils;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutFormatoFileAbstractAction;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileDocTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardRowBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableBean;
import it.eng.parer.slite.gen.tablebean.DecFormatoFileStandardTableDescriptor;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import javax.ejb.EJB;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StrutFormatoFileAction extends StrutFormatoFileAbstractAction {

    private static Logger log = LoggerFactory.getLogger(StrutDatiSpecAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileStandardEjb")
    private FormatoFileStandardEjb formatoFileStandardEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FormatoFileDocEjb")
    private FormatoFileDocEjb formatoFileDocEjb;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void process() throws EMFError {
        /* empty */
    }

    @Override
    public void loadDettaglio() throws EMFError {

        String lista = getTableName();
        if (lista != null) {
            if (lista.equals(getForm().getFormatoFileDocList().getName())) {

                getForm().getDuplicaFormato().setViewMode();

                if (getNavigationEvent().equals(NE_DETTAGLIO_INSERT)) {
                    Set<String> formatiSalvati = new HashSet<>();
                    for (DecFormatoFileDocRowBean row : (DecFormatoFileDocTableBean) getForm()
                            .getFormatoFileDocList().getTable()) {
                        if (row != null && row.getNmFormatoFileDoc() != null
                                && !row.getNmFormatoFileDoc().contains(".")) {
                            // Se il nome non contiene un '.' allora è un formato standard e non
                            // concatenato
                            formatiSalvati.add(row.getNmFormatoFileDoc().toUpperCase());
                        }
                    }

                    DecFormatoFileStandardTableBean formatoTableBean = formatoFileStandardEjb
                            .getDecFormatoFileStandardNotInList(formatiSalvati, Status.insert);
                    getForm().getFormatoFileStandardToDocList().setTable(formatoTableBean);
                    getForm().getFormatoFileStandardToDocList().getTable()
                            .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
                    getForm().getFormatoFileStandardToDocList().getTable()
                            .addSortingRule("cd_estensione_file", SortingRule.ASC);
                    getForm().getFormatoFileStandardToDocList().getTable().sort();
                    getForm().getFormatoFileStandardToDocList().getTable().first();

                    // Devo caricare nella lista dei selezionati tutti i formati doc già inseriti,
                    // ed escludere gli stessi dalla lista dei formati file standard
                    /*
                     * MODIFICA: questa lista non deve più comparire al momento della load
                     * dettaglio, ma deve solo servire per la singola operazione di inserimento come
                     * promemoria dei formati da inserire
                     */
                    DecFormatoFileStandardTableBean formatiSelTableBean = new DecFormatoFileStandardTableBean();
                    getForm().getSelectFormatoFileStandardList().setTable(formatiSelTableBean);
                    // Nascondo il campo cd_estensione_file_busta e visualizzo cd_estensione_file
                    getForm().getSelectFormatoFileStandardList().getCd_estensione_file_busta()
                            .setHidden(true);
                    getForm().getSelectFormatoFileStandardList().getCd_estensione_file()
                            .setHidden(false);
                    getForm().getSelectFormatoFileStandardList().getTable()
                            .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
                    getForm().getSelectFormatoFileStandardList().getTable()
                            .addSortingRule("cd_estensione_file", SortingRule.ASC);
                    getForm().getSelectFormatoFileStandardList().getTable().sort();
                    getSession().setAttribute("duplicaRecord", true);
                } else if (getNavigationEvent().equals(NE_DETTAGLIO_UPDATE)) {
                    // Se sono in modifica devo mostrare la lista formati concatenabili, per
                    // permettere la
                    // concatenazione di formati
                    // FIXME : Bisogna correggere il db per poter richiedere solo i formati
                    // "concatenabili"
                    DecFormatoFileDocRowBean formato = (DecFormatoFileDocRowBean) getForm()
                            .getFormatoFileDocList().getTable().getCurrentRow();
                    getForm().getFormatoFileDoc().copyFromBean(formato);

                    // 17-10
                    DecFormatoFileStandardTableBean formatiSelTableBean = new DecFormatoFileStandardTableBean();
                    List<String> formatiSalvati = new ArrayList<>();

                    // E' già un formato concatenato, devo popolare la lista dei formati già
                    // concatenati
                    if (formato.getNmFormatoFileDoc().contains(".")) {

                        formatiSalvati = formatoFileStandardEjb
                                .getDecFormatoFileStandardNameList(formato.getIdFormatoFileDoc());

                        // 17-10
                        formatiSelTableBean = formatoFileStandardEjb
                                .getDecFormatoFileStandardInListByName(formatiSalvati);
                        getSession().setAttribute("duplicaRecord", null);

                    } else {
                        // E' un formato standard - mostro la messageBox per richiedere all'utente
                        // se vuole duplicare il record o modificarlo direttamente
                        getForm().getDuplicaFormato().setEditMode();
                        getRequest().setAttribute("customBox", true);
                    }

                    DecFormatoFileStandardTableBean formatoTableBean = formatoFileStandardEjb
                            .getDecFormatoFileStandardNotInList(formatiSalvati, Status.update);

                    getForm().getFormatoFileStandardToDocList().setTable(formatoTableBean);
                    getForm().getFormatoFileStandardToDocList().getTable()
                            .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
                    getForm().getFormatoFileStandardToDocList().getTable().addSortingRule(
                            DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
                            SortingRule.ASC);
                    getForm().getFormatoFileStandardToDocList().getTable().sort();
                    getForm().getFormatoFileStandardToDocList().getTable().first();

                    getForm().getSelectFormatoFileStandardList().setTable(formatiSelTableBean);
                    getForm().getSelectFormatoFileStandardList().getTable()
                            .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
                    getForm().getSelectFormatoFileStandardList().getTable().addSortingRule(
                            DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
                            SortingRule.ASC);
                    getForm().getSelectFormatoFileStandardList().getTable().sort();

                    // Nascondo il campo cd_estensione_file_busta e visualizzo cd_estensione_file
                    getForm().getSelectFormatoFileStandardList().getCd_estensione_file_busta()
                            .setHidden(false);
                    getForm().getSelectFormatoFileStandardList().getCd_estensione_file()
                            .setHidden(true);
                    getForm().getFormatoFileDoc().getLogEventiFormatoFileDoc().setEditMode();

                    // -----------
                } else if (getNavigationEvent().equals(NE_DETTAGLIO_VIEW)
                        || getNavigationEvent().equals(NE_NEXT)
                        || getNavigationEvent().equals(NE_PREV)
                        || getNavigationEvent().equals(NE_DETTAGLIO_CANCEL)) {

                    DecFormatoFileDocRowBean formato = (DecFormatoFileDocRowBean) getForm()
                            .getFormatoFileDocList().getTable().getCurrentRow();
                    getForm().getFormatoFileDoc().copyFromBean(formato);
                    getForm().getFormatoFileDoc().setViewMode();
                    getForm().getFormatoFileDoc().setStatus(Status.view);
                    getForm().getFormatoFileDocList().setStatus(Status.view);
                    getForm().getFormatoFileDoc().getLogEventiFormatoFileDoc().setEditMode();
                }

                String cessato = getRequest().getParameter("cessato");
                if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                    getForm().getFormatoFileDocList().setUserOperations(true, false, false, false);
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {

        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.FORMATO_FILE_DOC_DETAIL)
                && getForm().getFormatoFileDoc().getStatus() != null
                && (getForm().getFormatoFileDoc().getStatus().toString().equals("insert")
                        || getForm().getFormatoFileDoc().getStatus().toString().equals("update"))) {
            goBack();
        } else {
            loadDettaglio();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        String lista = getRequest().getParameter("table");

        if (lista.equals(getForm().getFormatoFileDocList().getName())) {
            getForm().getSelectButtonList().setEditMode();
            getForm().getFormatoFileDoc().clear();
            getForm().getFormatoFileDoc().setStatus(Status.insert);
            getForm().getFormatoFileDocList().setStatus(Status.insert);
            getSession().setAttribute("inUse", false);
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {

        String publisher = getLastPublisher();

        if (publisher.equals(Application.Publisher.FORMATO_FILE_DOC_DETAIL)) {
            salvaFormatoFileDoc();
        }
    }

    /**
     * Metodo per il salvataggio o la modifica di un'entità DecFormatoFileDoc
     *
     * @throws EMFError errore generico
     */
    private void salvaFormatoFileDoc() throws EMFError {
        getMessageBox().clear();
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(
                configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

        if (getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
            if (getForm().getSelectFormatoFileStandardList().getTable().isEmpty()) {
                getMessageBox().addError(
                        "Non è stato selezionato nessun formato file standard da aggiungere alla lista</br>");

            } else {
                List<String> formatiListPresentiOnline = new ArrayList<>();
                // Per ogni riga della lista vado ad eseguire la insert dei formati file doc
                // con i dati di default
                for (DecFormatoFileStandardRowBean row : (DecFormatoFileStandardTableBean) getForm()
                        .getSelectFormatoFileStandardList().getTable()) {
                    formatiListPresentiOnline
                            .add(row.getString("cd_estensione_file").toUpperCase());
                    // Costruisco il nuovo decFormatoFileDocRowBean con i dati dei formati standard
                    DecFormatoFileDocRowBean formato = new DecFormatoFileDocRowBean();
                    formato.setIdStrut(getForm().getIdList().getId_strut().parse());
                    formato.setNmFormatoFileDoc(
                            row.getString("nm_formato_file_standard").toUpperCase());
                    formato.setDsFormatoFileDoc(row.getDsFormatoFileStandard());

                    formato.setCdVersione(row.getCdVersione());
                    Calendar calendar = Calendar.getInstance();
                    formato.setDtIstituz(new Timestamp(calendar.getTimeInMillis()));
                    calendar.set(2444, 11, 31, 0, 0, 0);
                    formato.setDtSoppres(new Timestamp(calendar.getTimeInMillis()));
                    DecFormatoFileStandardTableBean formatiStandard = new DecFormatoFileStandardTableBean();
                    formatiStandard.add(row);

                    try {
                        formatoFileDocEjb.addFormatoFileDoc(param, formato, formatiStandard,
                                (Boolean) getSession().getAttribute("duplicaRecord"));
                    } catch (ParerUserError ex) {
                        getMessageBox().addError(ex.getDescription());
                    } catch (ParerWarningException ex) {
                        // Caso di formato già esistente eventualmente da attivare su richiesta
                        // dell'utente
                        getRequest().setAttribute("warningAttivazioneFormatoEsistente",
                                ex.getDescription());
                        Object[] attributiAttivazioneFormatoEsistente = new Object[2];
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        attributiAttivazioneFormatoEsistente[0] = param;
                        // Recupero l'id formato tipo doc
                        attributiAttivazioneFormatoEsistente[1] = ex.getAdditionalInfo();
                        getSession().setAttribute("attributiAttivazioneFormatoEsistente",
                                attributiAttivazioneFormatoEsistente);
                        getRequest().setAttribute("customAttivazioneFormatoEsistenteMessageBox",
                                true);
                    }
                }
            }
        } else if (getForm().getFormatoFileDocList().getStatus().equals(Status.update)) {
            param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            getForm().getFormatoFileDoc().post(getRequest());

            if (getForm().getFormatoFileDoc().validate(getMessageBox())) {
                DecFormatoFileDocRowBean formato = (DecFormatoFileDocRowBean) getForm()
                        .getFormatoFileDocList().getTable().getCurrentRow();
                Boolean duplica = (Boolean) getSession().getAttribute("duplicaRecord");
                if (duplica) {
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                }
                Set<String> standard = new HashSet<>();
                standard.add(formato.getNmFormatoFileDoc().toUpperCase());
                DecFormatoFileStandardTableBean formatiStandard = new DecFormatoFileStandardTableBean();

                // se voglio duplicareformatiNonEliminati
                if (duplica != null && duplica) {

                    formato = new DecFormatoFileDocRowBean();
                    formato.setIdStrut(getForm().getIdList().getId_strut().parse());
                    formato.setNmFormatoFileDoc(getForm().getFormatoFileDoc()
                            .getNm_formato_file_doc().parse().toUpperCase());
                    formato.setDsFormatoFileDoc(
                            getForm().getFormatoFileDoc().getDs_formato_file_doc().parse());
                    formato.setCdVersione(getForm().getFormatoFileDoc().getCd_versione().parse());
                    Calendar calendar = Calendar.getInstance();
                    formato.setDtIstituz(new Timestamp(calendar.getTimeInMillis()));
                    calendar.set(2444, 11, 31, 0, 0, 0);
                    formato.setDtSoppres(new Timestamp(calendar.getTimeInMillis()));
                    // Devo ricreare tutti i record di usoFormatiStandard, perciò aggiungo alla
                    // lista anche il primo
                    // dovrebbe essere passato Status.update, ma usandolo non carica i record giusti
                    // FIXMEPLEASE: Per prendere un record, mi pare una roba terribile.
                    formatiStandard.add(formatoFileStandardEjb
                            .getDecFormatoFileStandardInList(standard, Status.insert,
                                    getForm().getIdList().getId_strut().parse())
                            .getRow(0));
                } else {
                    formato.setNmFormatoFileDoc(getForm().getFormatoFileDoc()
                            .getNm_formato_file_doc().parse().toUpperCase());
                    formato.setDsFormatoFileDoc(
                            getForm().getFormatoFileDoc().getDs_formato_file_doc().parse());
                    formato.setDtSoppres(getForm().getFormatoFileDoc().getDt_soppres().parse());
                }
                for (DecFormatoFileStandardRowBean row : (DecFormatoFileStandardTableBean) getForm()
                        .getSelectFormatoFileStandardList().getTable()) {
                    formatiStandard.add(row);
                }
                try {
                    formatoFileDocEjb.addFormatoFileDoc(param, formato, formatiStandard,
                            (Boolean) getSession().getAttribute("duplicaRecord"));
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                } catch (ParerWarningException ex) {
                    // Caso di formato già esistente eventualmente da attivare su richiesta
                    // dell'utente
                    getRequest().setAttribute("warningAttivazioneFormatoEsistente",
                            ex.getDescription());
                    Object[] attributiAttivazioneFormatoEsistente = new Object[2];
                    param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                    attributiAttivazioneFormatoEsistente[0] = param;
                    // Recupero l'id formato tipo doc
                    attributiAttivazioneFormatoEsistente[1] = ex.getAdditionalInfo();
                    getSession().setAttribute("attributiAttivazioneFormatoEsistente",
                            attributiAttivazioneFormatoEsistente);
                    getRequest().setAttribute("customAttivazioneFormatoEsistenteMessageBox", true);
                }
            }
        }

        // Esito di INSERT o UPDATE
        if (getMessageBox().hasError() || getRequest()
                .getAttribute("customAttivazioneFormatoEsistenteMessageBox") != null) {
            forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
        } else if (getMessageBox().hasWarning()) {
            getForm().getFormatoFileDocList().setStatus(Status.view);
            getForm().getFormatoFileDoc().setStatus(Status.view);
            getSession().removeAttribute("duplicaRecord");
            goBackToDettaglioStruttura();
            goBack();
        } else {
            getForm().getFormatoFileDocList().setStatus(Status.view);
            getForm().getFormatoFileDoc().setStatus(Status.view);
            getMessageBox().addInfo("Formati salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            getSession().removeAttribute("duplicaRecord");
            goBackToDettaglioStruttura();
            goBack();
        }
    }

    public void confermaAttivazioneFormatoEsistente() throws ParerUserError {
        if (getSession().getAttribute("attributiAttivazioneFormatoEsistente") != null) {
            Object[] attributiAttivazioneFormatoEsistente = (Object[]) getSession()
                    .getAttribute("attributiAttivazioneFormatoEsistente");
            LogParam param = (LogParam) attributiAttivazioneFormatoEsistente[0];
            Long idFormatoFileDoc = (Long) attributiAttivazioneFormatoEsistente[1];
            formatoFileDocEjb.activateDecFormatoFileDoc(param, idFormatoFileDoc);

            getForm().getFormatoFileDocList().setStatus(Status.view);
            getForm().getFormatoFileDoc().setStatus(Status.view);
            getMessageBox().addInfo("Formati salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            getSession().removeAttribute("duplicaRecord");
            getSession().removeAttribute("attributiAttivazioneFormatoEsistente");
            goBackToDettaglioStruttura();
            goBack();
        }
    }

    public void annullaAttivazioneFormatoEsistente() {
        getSession().removeAttribute("attributiSalvataggioFormato");
        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
    }

    private void goBackToDettaglioStruttura() {

        List<ExecutionHistory> esecuzioni = SessionManager.getExecutionHistory(getSession());
        for (ExecutionHistory exec : esecuzioni) {
            System.out.println("Publisher : " + exec.getPublisherName() + " \nBackParameter: "
                    + exec.getBackParameter() + " isAction :" + exec.isAction());
        }

    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        getSession().setAttribute("lista", lista);

        if (getForm().getFormatoFileDocList().getName().equals(lista)) {

            forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);

        } else if (getForm().getFormatoFileAmmessoList().getName().equals(lista)) {
            getForm().getFormatoFileAmmesso().setViewMode();
            getForm().getFormatoFileAmmesso().setStatus(Status.view);
            getForm().getFormatoFileAmmessoList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.FORMATO_FILE_DOC_DETAIL;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        /**
         * empty *
         */
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUT_FORMATO_FILE;
    }

    /**
     * Metodo che cancella l'entità DecFormatoFileDoc corrispondente al record della lista
     * selezionato
     *
     * @throws EMFError errore generico
     */
    @Override
    public void deleteFormatoFileDocList() throws EMFError {
        DecFormatoFileDocRowBean formatoFileDocRowBean = (DecFormatoFileDocRowBean) getForm()
                .getFormatoFileDocList().getTable().getCurrentRow();
        if (getMessageBox().isEmpty()) {
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (param.getNomePagina()
                    .equalsIgnoreCase(Application.Publisher.FORMATO_FILE_DOC_DETAIL)) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            } else {
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
                        form.getFormatoFileDocList()));
            }
            try {
                formatoFileDocEjb.deleteDecFormatoFileDoc(param,
                        formatoFileDocRowBean.getIdFormatoFileDoc().longValue());
                getMessageBox().addMessage(new Message(Message.MessageLevel.INF,
                        "Formato ammesso eliminato con successo"));
                DecFormatoFileDocTableBean formatoFileDocTableBean = formatoFileDocEjb
                        .getDecFormatoFileDocTableBean(formatoFileDocRowBean.getIdStrut(),
                                getForm().getFormatoFileDocList().isFilterValidRecords());

                getForm().getFormatoFileDocList().setTable(formatoFileDocTableBean);
                getForm().getFormatoFileDocList().getTable().first();
                getForm().getFormatoFileDocList().getTable()
                        .setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                goBack();
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
                if (!Application.Publisher.FORMATO_FILE_DOC_DETAIL.equals(getLastPublisher())) {
                    goBack();
                } else {
                    forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
                }
            }
        }
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
                    DecFormatoFileStandardRowBean currentRow = (DecFormatoFileStandardRowBean) getForm()
                            .getFormatoFileStandardToDocList().getTable()
                            .getRow((Integer.parseInt(comp) - indice));

                    if (getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
                        getForm().getFormatoFileStandardToDocList().getTable()
                                .remove(Integer.parseInt(comp) - indice);
                    } // Sono entrato nel caso dei CONCATENABILI
                    else {
                        StringBuilder formato = new StringBuilder(
                                getForm().getFormatoFileDoc().getNm_formato_file_doc().parse());
                        formato.append(".")
                                .append(currentRow.getString("nm_formato_file_standard"));
                        getForm().getFormatoFileDoc().getNm_formato_file_doc()
                                .setValue(formato.toString());

                        getForm().getFormatoFileStandardToDocList().getTable()
                                .remove(Integer.parseInt(comp) - indice);
                    }

                    getForm().getSelectFormatoFileStandardList().add(currentRow);

                    if (!getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
                        getForm().getSelectFormatoFileStandardList().getTable().getCurrentRow()
                                .setString("cd_estensione_file_busta",
                                        currentRow.getString("cd_estensione_file"));
                        getForm().getSelectFormatoFileStandardList().getCd_estensione_file()
                                .setHidden(true);
                    } else {
                        reloadSelectFormatoFileStandardList();
                    }

                    indice++;
                }
            }
            getForm().getSelectFormatoFileStandardList().getTable().sort();
            getForm().getFormatoFileStandardToDocList().getTable().sort();
        }

        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
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
                    DecFormatoFileStandardRowBean row = (DecFormatoFileStandardRowBean) getForm()
                            .getSelectFormatoFileStandardList().getTable()
                            .getRow((Integer.parseInt(comp) - indice));
                    getForm().getSelectFormatoFileStandardList().getTable()
                            .remove(Integer.parseInt(comp) - indice);
                    indice++;

                    HashSet<String> hs = new HashSet<>();
                    hs.add(row.getNmFormatoFileStandard());

                    if (getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
                        getForm().getFormatoFileStandardToDocList().add(row);
                        // Riordino la prima lista, riposizionandomi dove mi trovavo
                        reloadFormatoFileStandardToDocList();
                    } else {
                        getForm().getFormatoFileStandardToDocList().add(row);
                        String formato = getForm().getFormatoFileDoc().getNm_formato_file_doc()
                                .parse();
                        List<String> list = new ArrayList<>(Arrays.asList(formato.split("[.]")));
                        list.remove(row.getString("cd_estensione_file"));
                        getForm().getFormatoFileDoc().getNm_formato_file_doc()
                                .setValue(StringUtils.join(list, "."));
                    }
                }
            }
            getForm().getSelectFormatoFileStandardList().getTable().sort();
            getForm().getFormatoFileStandardToDocList().getTable().sort();
        }
        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
    }

    /**
     * Bottone che serve per aggiungere tutti i formati file da una lista all'altra, nelle pagine di
     * inserimento e modifica formati file doc
     *
     * @throws Throwable errore generico
     */
    @Override
    public void select_all() throws Throwable {
        if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_DOC_DETAIL)) {
            // Ricavo tutti i formati ancora disponibili
            DecFormatoFileStandardTableBean tabella = (DecFormatoFileStandardTableBean) getForm()
                    .getFormatoFileStandardToDocList().getTable();
            // Li inserisco nei selezionati
            for (DecFormatoFileStandardRowBean riga : tabella) {
                getForm().getSelectFormatoFileStandardList().getTable().add(riga);
            }
            // Li rimuovo dai disponibili
            getForm().getFormatoFileStandardToDocList().getTable().removeAll();

            getForm().getSelectFormatoFileStandardList().getTable()
                    .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
            getForm().getSelectFormatoFileStandardList().getTable().addSortingRule(
                    DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
                    SortingRule.ASC);
            getForm().getSelectFormatoFileStandardList().getTable().sort();
            getForm().getSelectFormatoFileStandardList().getTable().first();
            forwardToPublisher(getLastPublisher());
        } else if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)) {
            BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
            getForm().getSelectFormatoFileAmmessoList().setTable(
                    formatoFileDocEjb.getDecFormatoFileAmmessoNotInList(new HashSet<>(), idStrut));
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
        if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_DOC_DETAIL)) {
            // Ricavo tutti i formati selezionati
            DecFormatoFileStandardTableBean tabella = (DecFormatoFileStandardTableBean) getForm()
                    .getSelectFormatoFileStandardList().getTable();
            // Li inserisco nei disponibili
            for (DecFormatoFileStandardRowBean riga : tabella) {
                getForm().getFormatoFileStandardToDocList().getTable().add(riga);
            }
            // Li rimuovo dai selezionati
            getForm().getSelectFormatoFileStandardList().getTable().removeAll();

            getForm().getFormatoFileStandardToDocList().getTable()
                    .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
            getForm().getFormatoFileStandardToDocList().getTable().addSortingRule(
                    DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
                    SortingRule.ASC);
            getForm().getFormatoFileStandardToDocList().getTable().sort();
            getForm().getFormatoFileStandardToDocList().getTable().first();
            forwardToPublisher(getLastPublisher());
        } else if (getLastPublisher().equals(Application.Publisher.FORMATO_FILE_AMMESSO_DETAIL)) {
            BigDecimal idStrut = getForm().getIdList().getId_strut().parse();
            getForm().getFormatoFileDocList().setTable(
                    formatoFileDocEjb.getDecFormatoFileAmmessoNotInList(new HashSet<>(), idStrut));
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
     * Gestisce il bottone di select della gestione di inserimento formati file doc. Inserisce il
     * formato file standard selezionato tra i formati file doc della struttura
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectFormatoFileStandardToDocList() throws EMFError {
        DecFormatoFileStandardRowBean currentRow = (DecFormatoFileStandardRowBean) getForm()
                .getFormatoFileStandardToDocList().getTable().getCurrentRow();
        getForm().getFormatoFileDoc().post(getRequest());
        int index = getForm().getFormatoFileStandardToDocList().getTable().getCurrentRowIndex();

        if (getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
            getForm().getFormatoFileStandardToDocList().getTable().remove(index);
        } else {
            StringBuilder formato = new StringBuilder(
                    getForm().getFormatoFileDoc().getNm_formato_file_doc().parse());
            formato.append(".").append(currentRow.getString("cd_estensione_file"));
            getForm().getFormatoFileDoc().getNm_formato_file_doc().setValue(formato.toString());

            getForm().getFormatoFileStandardToDocList().getTable().remove(index);
        }

        getForm().getSelectFormatoFileStandardList().add(currentRow);

        if (!getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
            getForm().getSelectFormatoFileStandardList().getTable().getCurrentRow().setString(
                    "cd_estensione_file_busta", currentRow.getString("cd_estensione_file"));
            getForm().getSelectFormatoFileStandardList().getCd_estensione_file().setHidden(true);
        } else {
            reloadSelectFormatoFileStandardList();
        }
        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
    }

    /**
     * Gestisce il bottone di select della gestione di inserimento formati file doc. Rimuove il
     * formato file standard selezionato dai formati file doc della struttura
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectSelectFormatoFileStandardList() throws EMFError {

        DecFormatoFileStandardRowBean row = (DecFormatoFileStandardRowBean) getForm()
                .getSelectFormatoFileStandardList().getTable().getCurrentRow();
        getForm().getFormatoFileDoc().post(getRequest());
        int index = getForm().getSelectFormatoFileStandardList().getTable().getCurrentRowIndex();
        getForm().getSelectFormatoFileStandardList().getTable().remove(index);

        HashSet<String> hs = new HashSet<>();
        hs.add(row.getNmFormatoFileStandard());

        if (getForm().getFormatoFileDocList().getStatus().equals(Status.insert)) {
            getForm().getFormatoFileStandardToDocList().add(row);
            // Riordino la prima lista, riposizionandomi dove mi trovavo
            reloadFormatoFileStandardToDocList();
        } else {
            getForm().getFormatoFileStandardToDocList().add(row);
            String formato = getForm().getFormatoFileDoc().getNm_formato_file_doc().parse();
            List<String> list = new ArrayList<>(Arrays.asList(formato.split("[.]")));
            list.remove(row.getString("cd_estensione_file"));

            getForm().getFormatoFileDoc().getNm_formato_file_doc()
                    .setValue(StringUtils.join(list, "."));
        }
        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
    }

    public void reloadFormatoFileStandardToDocList() throws EMFError {
        int inizio = getForm().getFormatoFileStandardToDocList().getTable().getFirstRowPageIndex();
        int pageSize = getForm().getFormatoFileStandardToDocList().getTable().getPageSize();
        int paginaCorrente = getForm().getFormatoFileStandardToDocList().getTable()
                .getCurrentPageIndex();
        getForm().getFormatoFileStandardToDocList().getTable().sort();
        getForm().getFormatoFileStandardToDocList().getTable().setPageSize(pageSize);
        this.lazyLoadGoPage(getForm().getFormatoFileStandardToDocList(), paginaCorrente);
        getForm().getFormatoFileStandardToDocList().getTable().setCurrentRowIndex(inizio);
    }

    public void reloadSelectFormatoFileStandardList() throws EMFError {
        int inizio = getForm().getSelectFormatoFileStandardList().getTable().getFirstRowPageIndex();
        int pageSize = getForm().getSelectFormatoFileStandardList().getTable().getPageSize();
        int paginaCorrente = getForm().getSelectFormatoFileStandardList().getTable()
                .getCurrentPageIndex();
        getForm().getSelectFormatoFileStandardList().getTable().sort();
        getForm().getSelectFormatoFileStandardList().getTable().setPageSize(pageSize);
        this.lazyLoadGoPage(getForm().getSelectFormatoFileStandardList(), paginaCorrente);
        getForm().getSelectFormatoFileStandardList().getTable().setCurrentRowIndex(inizio);
    }

    @Override
    public void updateFormatoFileDocList() throws EMFError {

        getForm().getFormatoFileDoc().setStatus(Status.update);
        getForm().getFormatoFileDocList().setStatus(Status.update);
        getForm().getFormatoFileDoc().setViewMode();
        getForm().getSelectButtonList().setViewMode();
        getForm().getSelectButtonList().getSelectAmmissibili().setEditMode();
        getForm().getSelectButtonList().getDeselectAmmessi().setEditMode();
        getForm().getFormatoFileDoc().getDt_soppres().setEditMode();
        getForm().getSelectFormatoFileStandardList().getCd_estensione_file().setHidden(true);
        getSession().setAttribute("duplicaRecord", false);

        DecFormatoFileDocRowBean formatoFileDocRowBean = (DecFormatoFileDocRowBean) getForm()
                .getFormatoFileDocList().getTable().getCurrentRow();
        getSession().setAttribute("inUse",
                formatoFileDocEjb.isDecFormatoFileDocInUse(formatoFileDocRowBean));
    }

    @Override
    public void duplica() throws Throwable {
        getSession().setAttribute("duplicaRecord", true);
        getForm().getFormatoFileDoc().getDt_soppres().setViewMode();
        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);

    }

    @Override
    public void modifica() throws Throwable {
        getSession().setAttribute("duplicaRecord", false);
        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
    }

    @Override
    public void annulla() throws Throwable {
        getSession().removeAttribute("duplicaRecord");
        goBack();
    }

    public void duplicaFormato() throws EMFError {

        getForm().getFormatoFileDocList().setStatus(Status.update);
        getForm().getSelectButtonList().setViewMode();
        getForm().getSelectButtonList().getSelectAmmissibili().setEditMode();
        getForm().getSelectButtonList().getDeselectAmmessi().setEditMode();
        getSession().setAttribute("duplicaRecord", true);
        getSession().setAttribute("lista", "FormatoFileDocList");

        DecFormatoFileDocRowBean formato = (DecFormatoFileDocRowBean) getForm()
                .getFormatoFileDocList().getTable().getCurrentRow();
        getForm().getFormatoFileDoc().copyFromBean(formato);

        Set<String> formatiSalvati = new HashSet<>();

        if (formato.getNmFormatoFileDoc().contains(".")) {

            String[] formati = formato.getNmFormatoFileDoc().split("[.]");
            formatiSalvati = new HashSet<>(Arrays.asList(formati));
        }

        DecFormatoFileStandardTableBean formatoTableBean = formatoFileStandardEjb
                .getDecFormatoFileStandardNotInList(formatiSalvati, Status.update);
        getForm().getFormatoFileStandardToDocList().setTable(formatoTableBean);
        getForm().getFormatoFileStandardToDocList().getTable()
                .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
        getForm().getFormatoFileStandardToDocList().getTable().addSortingRule(
                DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
                SortingRule.ASC);
        getForm().getFormatoFileStandardToDocList().getTable().sort();
        getForm().getFormatoFileStandardToDocList().getTable().first();

        getForm().getSelectFormatoFileStandardList()
                .setTable(new DecFormatoFileStandardTableBean());
        // Nascondo il campo cd_estensione_file e visualizzo il cd_entensione_file_busta
        getForm().getSelectFormatoFileStandardList().getCd_estensione_file().setHidden(true);
        getForm().getSelectFormatoFileStandardList().getCd_estensione_file_busta().setHidden(false);
        getForm().getSelectFormatoFileStandardList().getTable()
                .setPageSize(WebConstants.FORMATI_PAGE_SIZE);
        getForm().getSelectFormatoFileStandardList().getTable().addSortingRule(
                DecFormatoFileStandardTableDescriptor.COL_NM_FORMATO_FILE_STANDARD,
                SortingRule.ASC);
        getForm().getSelectFormatoFileStandardList().getTable().sort();

        getSession().setAttribute("inUse", false);

        forwardToPublisher(Application.Publisher.FORMATO_FILE_DOC_DETAIL);
    }

    @Override
    public void logEventiFormatoFileDoc() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto()
                .setValue(SacerLogConstants.TIPO_OGGETTO_FORMATO_AMMESSO);
        DecFormatoFileDocRowBean riga = (DecFormatoFileDocRowBean) getForm().getFormatoFileDocList()
                .getTable().getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(riga.getIdFormatoFileDoc().toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

}
