package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.async.ejb.CalcoloEstrazioneAsync;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.restArch.dto.RicercaRichRestArchBean;
import it.eng.parer.restArch.ejb.RestituzioneArchivioEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.RestituzioneArchivioAbstractAction;
import it.eng.parer.slite.gen.tablebean.AroRichiestaRaRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRaTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichRaRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichRaTableBean;
import it.eng.parer.viewEntity.AroVChkRaUd;
import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.ejb.EJB;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author DiLorenzo_F
 */
public class RestituzioneArchivioAction extends RestituzioneArchivioAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(RestituzioneArchivioAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/RestituzioneArchivioEjb")
    private RestituzioneArchivioEjb restArchEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CalcoloEstrazioneAsync")
    private CalcoloEstrazioneAsync calcoloEstrazioneAsync;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getRichRestArchList().getName())) {
                AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) getForm().getRichRestArchList().getTable()
                        .getCurrentRow();
                BigDecimal idRichRestArch = row.getIdRichiestaRa();

                loadDettaglioRichiesta(idRichRestArch, row.getIdStrut());
                getForm().getRichRestArchDetail().setViewMode();
                getForm().getRichRestArchList().setStatus(Status.view);
            }
        }
    }

    private void loadDettaglioRichiesta(BigDecimal idRichRestArch, BigDecimal idStrut) throws EMFError {
        AroRichiestaRaRowBean detailRow = restArchEjb.getAroRichiestaRaRowBean(idRichRestArch, idStrut);
        getForm().getRichRestArchDetail().copyFromBean(detailRow);

        getForm().getRichRestArchDetail().getPriorita_combo().setDecodeMap(ComboGetter.getSortedMapByStringList(
                "priorita_combo", Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")));
        getForm().getRichRestArchDetail().getPriorita_combo().setValue(detailRow.getPriorita().toPlainString());

        AroVLisItemRaTableBean listaItem = restArchEjb.getAroVLisItemRaTableBean(idRichRestArch, idStrut);
        getForm().getItemList().setTable(listaItem);
        getForm().getItemList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getItemList().getTable().first();

        getForm().getRichRestArchList().setHideUpdateButton(true);
        // getForm().getRichRestArchList().setHideDeleteButton(true);

        getForm().getRichRestArchDetailButtonList().setEditMode();
        getForm().getRichRestArchDetailButtonList().hideAll();

        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.CALCOLO_AIP_IN_CORSO,
                AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE, AroRichiestaTiStato.ESTRAZIONE_IN_CORSO,
                AroRichiestaTiStato.ERRORE, AroRichiestaTiStato.ESTRATTO, AroRichiestaTiStato.VERIFICATO)) {
            getForm().getRichRestArchDetailButtonList().getAnnullaRichiesta().setHidden(false);
        }

        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE)) {
            getForm().getRichRestArchList().setHideUpdateButton(false);
            // getForm().getRichRestArchList().setHideDeleteButton(false);
        }

        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.ESTRATTO)) {
            getForm().getRichRestArchDetailButtonList().getVerificaRichiesta().setHidden(false);
        }

        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.ERRORE)) {
            // se nella richiesta non sono presenti item con stato = DA_ELABORARE ed è presente almeno un item con stato
            // = ERRORE
            if (!restArchEjb.checkStatoItems(idRichRestArch, TiStatoAroAipRa.DA_ELABORARE)
                    && restArchEjb.checkStatoItems(idRichRestArch, TiStatoAroAipRa.ERRORE)) {
                getForm().getRichRestArchDetailButtonList().getRielaboraRichiesta().setHidden(false);
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.RICH_REST_ARCH_DETAIL)) {
            AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) getForm().getRichRestArchList().getTable()
                    .getCurrentRow();
            BigDecimal idRichRestArch = row.getIdRichiestaRa();
            if (idRichRestArch != null) {
                loadDettaglioRichiesta(idRichRestArch, row.getIdStrut());
            }
            getForm().getRichRestArchDetail().setViewMode();
            getForm().getRichRestArchList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.RICH_REST_ARCH_DETAIL);
        } else {
            goBack();
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getRichRestArchList().getName())) {
            if (getForm().getRichRestArchDetail().postAndValidate(getRequest(), getMessageBox())) {
                BigDecimal idStrut = getForm().getRichRestArchDetail().getId_strut().parse();
                BigDecimal idRichRestArch = getForm().getRichRestArchDetail().getId_richiesta_ra().parse();
                String priorita = getForm().getRichRestArchDetail().getPriorita_combo().parse();

                try {
                    restArchEjb.saveRichRestArch(idRichRestArch, priorita, idStrut);
                    getMessageBox().addInfo("Richiesta modificata con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);

                    loadDettaglioRichiesta(idRichRestArch, idStrut);
                    getForm().getRichRestArchList().setStatus(BaseElements.Status.view);
                    getForm().getRichRestArchDetail().setViewMode();
                } catch (ParerUserError ex) {
                    logger.error(ex.getDescription(), ex);
                    getMessageBox().addError(ex.getDescription());
                }
                forwardToPublisher(Application.Publisher.RICH_REST_ARCH_DETAIL);
            }
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getRichRestArchList().getName())) {
                forwardToPublisher(Application.Publisher.RICH_REST_ARCH_DETAIL);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.RICH_REST_ARCH_DETAIL)) {
            goBackTo(getDefaultPublsherName());
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.RICERCA_RICH_REST_ARCH;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.RICERCA_RICH_REST_ARCH)) {
                int rowIndex;
                int pageSize;
                RicercaRichRestArchBean filtri = new RicercaRichRestArchBean(getForm().getFiltriRicercaRichRestArch());
                if (!getMessageBox().hasError()) {
                    AroVRicRichRaTableBean table = restArchEjb.getAroVRicRichRaTableBean(getUser().getIdUtente(),
                            filtri);
                    if (getForm().getRichRestArchList().getTable() != null) {
                        rowIndex = getForm().getRichRestArchList().getTable().getCurrentRowIndex();
                        pageSize = getForm().getRichRestArchList().getTable().getPageSize();
                    } else {
                        rowIndex = 0;
                        pageSize = WebConstants.DEFAULT_PAGE_SIZE;
                    }
                    getForm().getRichRestArchList().setTable(table);
                    getForm().getRichRestArchList().getTable().setPageSize(pageSize);
                    getForm().getRichRestArchList().getTable().setCurrentRowIndex(rowIndex);
                }
                getForm().getRichRestArchList().setHideUpdateButton(false);
                // getForm().getRichRestArchList().setHideDeleteButton(false);
            } else if (publisherName.equals(Application.Publisher.RICH_REST_ARCH_DETAIL)) {
                BigDecimal idRichRestArch = getForm().getRichRestArchDetail().getId_richiesta_ra().parse();
                BigDecimal idStrut = getForm().getRichRestArchDetail().getId_strut().parse();
                if (idRichRestArch != null && idStrut != null) {
                    loadDettaglioRichiesta(idRichRestArch, idStrut);
                }
                getForm().getRichRestArchDetail().setViewMode();
                getForm().getRichRestArchList().setStatus(Status.view);
            }
        } catch (EMFError e) {
            logger.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.RESTITUZIONE_ARCHIVIO;
    }

    // @Override
    // public void deleteRichRestArchList() throws EMFError {
    // AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) getForm().getRichRestArchList().getTable().getCurrentRow();
    // int riga = getForm().getRichRestArchList().getTable().getCurrentRowIndex();
    // BigDecimal idRichRestArch = row.getIdRichiestaRa();
    // if (!restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.ANNULLATO,
    // AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE)) {
    // getMessageBox().addError("La richiesta non \u00E8 eliminabile perch\u00E9 ha stato corrente diverso da "
    // + AroRichiestaTiStato.ANNULLATO.name() + ", " + AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name());
    // } else {
    // // Elimina richiesta
    // try {
    // restArchEjb.deleteRichRestArch(idRichRestArch);
    //
    // getForm().getRichRestArchList().getTable().remove(riga);
    // getMessageBox().addInfo("Richiesta eliminata con successo");
    // } catch (ParerUserError ex) {
    // getMessageBox().addError(ex.getDescription());
    // }
    // }
    // if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.RICH_REST_ARCH_DETAIL)) {
    // goBackTo(Application.Publisher.RICERCA_RICH_REST_ARCH);
    // } else if (!getMessageBox().hasError() &&
    // getLastPublisher().equals(Application.Publisher.RICERCA_RICH_REST_ARCH)) {
    // reloadAfterGoBack(Application.Publisher.RICERCA_RICH_REST_ARCH);
    // } else {
    // getForm().getRichRestArchList().setHideUpdateButton(false);
    // //getForm().getRichRestArchList().setHideDeleteButton(false);
    //
    // forwardToPublisher(getLastPublisher());
    // }
    // }

    @Override
    public void updateRichRestArchList() throws EMFError {
        AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) getForm().getRichRestArchList().getTable().getCurrentRow();
        BigDecimal idRichRestArch = row.getIdRichiestaRa();
        if (!restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE)) {
            getForm().getRichRestArchList().setHideUpdateButton(false);
            // getForm().getRichRestArchList().setHideDeleteButton(false);

            getMessageBox().addError("La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da "
                    + AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name());
            forwardToPublisher(getLastPublisher());
        } else {
            // Modifica richiesta
            getForm().getRichRestArchList().setStatus(Status.update);

            getForm().getRichRestArchDetail().getPriorita_combo().setEditMode();

            getForm().getRichRestArchDetailButtonList().hideAll();
            // getForm().getItemList().setHideDeleteButton(true);
            getForm().getItemList().setHideDetailButton(true);

            forwardToPublisher(Application.Publisher.RICH_REST_ARCH_DETAIL);
        }
    }

    // <editor-fold defaultstate="expand" desc="UI Creazione richiesta restituzione archivio">
    @Override
    public void creaRichRestArchBtn() throws EMFError {
        getForm().getFiltriRicercaRichRestArch().postAndValidate(getRequest(), getMessageBox());

        BigDecimal idStrut = getForm().getFiltriRicercaRichRestArch().getId_strut().parse();
        if (!getMessageBox().hasError()) {
            if (idStrut == null) {
                getMessageBox().addError("Per poter creare la richiesta \u00E8 necessario selezionare una struttura");
            }
        }

        if (!getMessageBox().hasError()) {
            getRequest().setAttribute("creaRichRestArchBox", true);

            if (getSession().getAttribute("tiRichRestArch") != null) {
                if (((String) getSession().getAttribute("tiRichRestArch")).equals("UNITA_DOC")) {
                    getForm().getCreazioneRichRestArch().getTi_rich_rest_arch().setValue("UNITA_DOC");
                } else if (((String) getSession().getAttribute("tiRichRestArch")).equals("SERIE")) {
                    getForm().getCreazioneRichRestArch().getTi_rich_rest_arch().setValue("SERIE");
                } else if (((String) getSession().getAttribute("tiRichRestArch")).equals("FASCICOLI")) {
                    getForm().getCreazioneRichRestArch().getTi_rich_rest_arch().setValue("FASCICOLI");
                }
            }
        }

        forwardToPublisher(Application.Publisher.RICERCA_RICH_REST_ARCH);
    }

    public void creaRichRestArch() throws EMFError {
        getForm().getCreazioneRichRestArch().postAndValidate(getRequest(), getMessageBox());
        try {
            // // NOTA: essendo il bottone attualmente associato solo alla richieste di restituzione archivio delle ud,
            // per ora il parametro è sempre UNITA_DOC
            // final String tiRichRestArch = CostantiDB.TiRichRestArch.UNITA_DOC.name();
            final String tiRichRestArch = getForm().getCreazioneRichRestArch().getTi_rich_rest_arch().parse();
            /* Combo priorita preso da finestra pop-up */
            final String prioritaPopUp = (String) getRequest().getParameter("Priorita");

            BigDecimal idStrut = getForm().getFiltriRicercaRichRestArch().getId_strut().parse();

            if (!getMessageBox().hasError()) {
                if (!restArchEjb.checkEnteConvenzExisting(idStrut)) {
                    getMessageBox()
                            .addError("Non \u00E8 presente nessun ente convenzionato in capo alla struttura corrente");
                }
            }
            if (!getMessageBox().hasError()) {
                if (restArchEjb.checkRichRestArchExisting(idStrut)) {
                    getMessageBox().addError(
                            "Per l'ente convenzionato della struttura corrente \u00E8 gi\u00E0 presente una richiesta di restituzione archivio");
                }
            }
            if (!getMessageBox().hasError()) {
                if (!restArchEjb.checkDirectoriesRa(idStrut)) {
                    getMessageBox().addError(
                            "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi in lettura/scrittura");
                }
            }
            if (!getMessageBox().hasError()) {
                // Gestisco le richieste scadute
                restArchEjb.elaboraRichRestArchExpired(idStrut);
                // Salvo la richiesta di restituzione archivio
                Long idRichRestArch = restArchEjb.saveRichRestArch(getUser().getIdUtente(), idStrut, prioritaPopUp,
                        tiRichRestArch);
                //
                if (tiRichRestArch.equals("UNITA_DOC")) {
                    AroVChkRaUd chkRaUdView = restArchEjb.retrieveChkRaUnitaDoc(idStrut);
                    if (chkRaUdView.getFlUdNonInElenco().equals("1")) {
                        restArchEjb.setDaAnnullareAtomic(
                                "Ci sono unità documentarie non inserite in un elenco di versamento", idRichRestArch,
                                "RICHIESTA ANNULLATA");
                        getMessageBox().addError(
                                "Ci sono unità documentarie non inserite in un elenco di versamento - richiesta annullata");
                    } else if (chkRaUdView.getFlElencoNonCompletato().equals("1")) {
                        restArchEjb.setDaAnnullareAtomic(
                                "Il processo di conservazione delle unità documentarie presenti in archivio non è ancora completato",
                                idRichRestArch, "RICHIESTA ANNULLATA");
                        getMessageBox().addError(
                                "Il processo di conservazione delle unità documentarie presenti in archivio non è ancora completato - richiesta annullata");
                    } else if (chkRaUdView.getFlElencoNonFirmato().equals("1")) {
                        restArchEjb.setDaAnnullareAtomic(
                                "Il processo di conservazione delle unità documentarie presenti in archivio non è ancora completato",
                                idRichRestArch, "RICHIESTA ANNULLATA");
                        getMessageBox().addError(
                                "Il processo di conservazione delle unità documentarie presenti in archivio non è ancora completato - richiesta annullata");
                    } else if (chkRaUdView.getFlAipNonFirmato().equals("1")) {
                        restArchEjb.setDaAnnullareAtomic(
                                "Il processo di conservazione delle unità documentarie presenti in archivio non è ancora completato",
                                idRichRestArch, "RICHIESTA ANNULLATA");
                        getMessageBox().addError(
                                "Il processo di conservazione delle unità documentarie presenti in archivio non è ancora completato - richiesta annullata");
                    }
                }
                if (tiRichRestArch.equals("SERIE")) {
                    // TODO
                }
                if (tiRichRestArch.equals("FASCICOLI")) {
                    // TODO
                }
                if (!getMessageBox().hasError()) {
                    if (idRichRestArch != null) {
                        calcoloEstrazioneAsync.creaRichiestaEstrazioneAsync(idRichRestArch, idStrut.longValue());
                    }
                    if (idRichRestArch != null) {
                        AroVRicRichRaRowBean tmpRow = new AroVRicRichRaRowBean();
                        tmpRow.setIdRichiestaRa(new BigDecimal(idRichRestArch));
                        if (getForm().getRichRestArchList().getTable() == null
                                || getForm().getRichRestArchList().getTable().isEmpty()) {
                            getForm().getRichRestArchList().setTable(new AroVRicRichRaTableBean());
                            getForm().getRichRestArchList().getTable().add(tmpRow);
                        } else {
                            getForm().getRichRestArchList().getTable().last();
                            getForm().getRichRestArchList().getTable().add(tmpRow);
                        }
                        loadDettaglioRichiesta(new BigDecimal(idRichRestArch), idStrut);
                    }
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        } catch (Exception ex) {
            logger.error("Eccezione generica nella creazione della richiesta di restituzione archivio: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError(
                    "Si \u00E8 verificata un'eccezione nella creazione della richiesta di restituzione archivio");
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.RICERCA_RICH_REST_ARCH);
        } else {
            forwardToPublisher(Application.Publisher.RICH_REST_ARCH_DETAIL);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="expand" desc="UI Ricerca restituzione archivio">
    @Secure(action = "Menu.RestituzioneArchivio.RicercaRestArchUd")
    public void loadRicercaRestArchUd() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.RestituzioneArchivio.RicercaRestArchUd");
        caricaContenutoPaginaRicercaRestArch("UNITA_DOC");

        RicercaRichRestArchBean filtri = new RicercaRichRestArchBean(getForm().getFiltriRicercaRichRestArch());
        if (!getMessageBox().hasError()) {
            AroVRicRichRaTableBean table = restArchEjb.getAroVRicRichRaTableBean(getUser().getIdUtente(), filtri);
            getForm().getRichRestArchList().setTable(table);
            getForm().getRichRestArchList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getRichRestArchList().getTable().first();
        }

        forwardToPublisher(Application.Publisher.RICERCA_RICH_REST_ARCH);
    }

    @Secure(action = "Menu.RestituzioneArchivio.RicercaRestArchSer")
    public void loadRicercaRestArchSer() throws EMFError {
        // getUser().getMenu().reset();
        // getUser().getMenu().select("Menu.RestituzioneArchivio.RicercaRestArchSer");
        // caricaContenutoPaginaRicercaRestArch("SERIE");
    }

    @Secure(action = "Menu.RestituzioneArchivio.RicercaRestArchFasc")
    public void loadRicercaRestArchFasc() throws EMFError {
        // getUser().getMenu().reset();
        // getUser().getMenu().select("Menu.RestituzioneArchivio.RicercaRestArchFasc");
        // caricaContenutoPaginaRicercaRestArch("FASCICOLI");
    }

    private void caricaContenutoPaginaRicercaRestArch(String tiRichRestArch) throws EMFError {
        getForm().getFiltriRicercaRichRestArch().reset();
        getForm().getFiltriRicercaRichRestArch().setEditMode();

        initFiltriStrut(getForm().getFiltriRicercaRichRestArch().getName());

        getForm().getRichRestArchList().setTable(new AroVRicRichRaTableBean());

        getForm().getFiltriRicercaRichRestArch().getTi_stato_rich_rest_arch_cor().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_stato_rich_rest_arch", AroRichiestaTiStato.values()));

        getSession().setAttribute("tiRichRestArch", tiRichRestArch);
        getForm().getFiltriRicercaRichRestArch().getTi_rich_rest_arch().setValue(tiRichRestArch);

        if (tiRichRestArch.equals("UNITA_DOC")) {
            // TODO
        } else if (tiRichRestArch.equals("SERIE")) {
            // TODO
        } else {
            // TODO
        }

        // forwardToPublisher(Application.Publisher.RICERCA_RICH_REST_ARCH);
    }

    private void initFiltriStrut(String fieldSetName) throws EMFError {
        /* Ricavo Ambiente, Ente e Struttura CORRENTI */
        OrgStrutRowBean strutWithAmbienteEnte = evEjb
                .getOrgStrutRowBeanWithAmbienteEnte(getUser().getIdOrganizzazioneFoglia());
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idAmbiente = strutWithAmbienteEnte.getBigDecimal("id_ambiente");
        BigDecimal idEnte = strutWithAmbienteEnte.getIdEnte();
        BigDecimal idStrut = strutWithAmbienteEnte.getIdStrut();

        // Inizializzo le combo settando la struttura corrente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        OrgEnteTableBean tmpTableBeanEnte = null;
        OrgStrutTableBean tmpTableBeanStruttura = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(), idAmbiente.longValue(),
                    Boolean.FALSE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.FALSE);

        } catch (Exception ex) {
            logger.error("Errore durante il recupero dei filtri Ambiente - Ente - Struttura", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        ComboBox<BigDecimal> comboAmbiente = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm()
                .getComponent(fieldSetName)).getComponent("Id_ambiente"));
        comboAmbiente.setDecodeMap(mappaAmbiente);
        comboAmbiente.setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        ComboBox<BigDecimal> comboEnte = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm().getComponent(fieldSetName))
                .getComponent("Id_ente"));
        comboEnte.setDecodeMap(mappaEnte);
        comboEnte.setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        ComboBox<BigDecimal> comboStrut = ((ComboBox<BigDecimal>) ((Fields<Field>) getForm().getComponent(fieldSetName))
                .getComponent("Id_strut"));
        comboStrut.setDecodeMap(mappaStrut);
        comboStrut.setValue(idStrut.toString());
    }

    @Override
    public JSONObject triggerFiltriRicercaRichRestArchId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichRestArch().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaRichRestArch().getId_ente().setValue("");
        getForm().getFiltriRicercaRichRestArch().getId_strut().setValue("");

        BigDecimal idAmbiente = getForm().getFiltriRicercaRichRestArch().getId_ambiente().parse();
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.FALSE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            getForm().getFiltriRicercaRichRestArch().getId_ente().setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                getForm().getFiltriRicercaRichRestArch().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                BigDecimal idEnte = tmpTableBeanEnte.getRow(0).getIdEnte();
                if (idEnte != null) {
                    // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
                    OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                            idEnte, Boolean.FALSE);
                    DecodeMap mappaStrut = new DecodeMap();
                    mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
                    getForm().getFiltriRicercaRichRestArch().getId_strut().setDecodeMap(mappaStrut);

                    // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di essa
                    if (tmpTableBeanStrut.size() == 1) {
                        getForm().getFiltriRicercaRichRestArch().getId_strut()
                                .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    }
                }
            } else {
                getForm().getFiltriRicercaRichRestArch().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaRichRestArch().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaRichRestArch().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichRestArch().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaRichRestArchId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaRichRestArch().post(getRequest());

        // Azzero i valori preimpostati delle varie combo
        getForm().getFiltriRicercaRichRestArch().getId_strut().setValue("");

        BigDecimal idEnte = getForm().getFiltriRicercaRichRestArch().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.FALSE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            getForm().getFiltriRicercaRichRestArch().getId_strut().setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaRichRestArch().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
            }
        } else {
            getForm().getFiltriRicercaRichRestArch().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaRichRestArch().asJSON();
    }

    @Override
    public void ricercaRichRestArch() throws EMFError {
        if (getForm().getFiltriRicercaRichRestArch().postAndValidate(getRequest(), getMessageBox())) {
            /* Imposto il filtro tiRichRestArch in base alla mia provenienza iniziale */
            if ((String) getSession().getAttribute("tiRichRestArch") != null) {
                getForm().getFiltriRicercaRichRestArch().getTi_rich_rest_arch()
                        .setValue((String) getSession().getAttribute("tiRichRestArch"));
            }
            RicercaRichRestArchBean filtri = new RicercaRichRestArchBean(getForm().getFiltriRicercaRichRestArch());
            if (!getMessageBox().hasError()) {
                AroVRicRichRaTableBean table = restArchEjb.getAroVRicRichRaTableBean(getUser().getIdUtente(), filtri);
                getForm().getRichRestArchList().setTable(table);
                getForm().getRichRestArchList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                getForm().getRichRestArchList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.RICERCA_RICH_REST_ARCH);
    }
    // </editor-fold>

    // <editor-fold defaultstate="expand" desc="UI parziale Dettaglio restituzione archivio">
    @Override
    public void annullaRichiesta() throws Throwable {
        AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) getForm().getRichRestArchList().getTable().getCurrentRow();
        BigDecimal idRichRestArch = row.getIdRichiestaRa();
        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.CALCOLO_AIP_IN_CORSO,
                AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE, AroRichiestaTiStato.ESTRAZIONE_IN_CORSO,
                AroRichiestaTiStato.ESTRATTO, AroRichiestaTiStato.ERRORE, AroRichiestaTiStato.VERIFICATO)) {
            cambiaStato(idRichRestArch, AroRichiestaTiStato.ANNULLATO);
        } else {
            getMessageBox().addInfo("Modifica non possibile in quanto la richiesta ha cambiato lo stato");
            goBackTo(Application.Publisher.RICERCA_RICH_REST_ARCH);
        }
    }

    @Override
    public void rielaboraRichiesta() throws Throwable {
        final BigDecimal idRichRestArch = getForm().getRichRestArchDetail().getId_richiesta_ra().parse();
        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.ERRORE)) {
            try {
                // Ricavo la struttura che ha creato la richiesta (dalla richiesta stessa)
                Long idStrut = restArchEjb.getStrutFirstStateRich(idRichRestArch);
                restArchEjb.controlloItemOnline(idRichRestArch);
                restArchEjb.cambiaStato(getUser().getIdUtente(), idRichRestArch, AroRichiestaTiStato.ERRORE.name(),
                        AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name(), "RICHIESTA RECUPERATA");
                if (idRichRestArch != null) {
                    loadDettaglioRichiesta(idRichRestArch, BigDecimal.valueOf(idStrut));
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        } else {
            getMessageBox().addError("Controllo non possibile in quanto la richiesta non ha stato ERRORE");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void verificaRichiesta() throws Throwable {
        AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) getForm().getRichRestArchList().getTable().getCurrentRow();
        BigDecimal idRichRestArch = row.getIdRichiestaRa();
        if (restArchEjb.checkStatoRichiesta(idRichRestArch, AroRichiestaTiStato.ESTRATTO)) {
            // se nella richiesta non sono presenti item con stato = ERRORE o con stato = DA_ELABORARE
            if (!restArchEjb.checkStatoItems(idRichRestArch, TiStatoAroAipRa.ERRORE)
                    && !restArchEjb.checkStatoItems(idRichRestArch, TiStatoAroAipRa.DA_ELABORARE)) {
                cambiaStato(idRichRestArch, AroRichiestaTiStato.VERIFICATO);
            } else {
                getMessageBox().addError(
                        "Modifica non possibile in quanto nella richiesta sono presenti versamenti con stato DA_ELABORARE o con stato ERRORE");
            }
        } else {
            getMessageBox().addError("Modifica non possibile in quanto la richiesta non ha stato ESTRATTO");
        }
    }

    private void cambiaStato(BigDecimal idRichRestArch, AroRichiestaTiStato tiStatoRichRestArch) {
        getForm().getCambioStatoRichiesta().getId_rich_rest_arch().setValue(idRichRestArch.toPlainString());
        getForm().getCambioStatoRichiesta().getDs_nota_rich_rest_arch().setEditMode();
        getForm().getCambioStatoRichiesta().getDs_nota_rich_rest_arch().clear();

        getForm().getCambioStatoRichiesta().getTi_stato_rich_rest_arch().setValue(tiStatoRichRestArch.name());
        getForm().getCambioStatoRichiesta().getConfermaCambioStato().setEditMode();

        // if (tiStatoRichRestArch.equals(AroRichiestaTiStato.ANNULLATO)) {
        // Long numeroItems = restArchEjb.countItemsInRichRestArch(idRichRestArch);
        // Long numeroItemsInErrore = restArchEjb.countItemsInRichRestArch(idRichRestArch, TiStatoAroAipRa.ERRORE);
        //
        // if (numeroItems > 0L && numeroItemsInErrore == 0L) {
        // getRequest().setAttribute("tuttiItemEstratti", true);
        // } else if (numeroItems > 0L && numeroItemsInErrore > 0L) {
        // getRequest().setAttribute("itemInErrore", true);
        // }
        // }

        if (!getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.CAMBIO_STATO_RICH_REST_ARCH);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void confermaCambioStato() throws EMFError {
        getForm().getCambioStatoRichiesta().getDs_nota_rich_rest_arch().post(getRequest());
        BigDecimal idRichRestArch = getForm().getCambioStatoRichiesta().getId_rich_rest_arch().parse();
        String dsNotaRichRestArch = getForm().getCambioStatoRichiesta().getDs_nota_rich_rest_arch().parse();
        String tiStatoRichRestArch = getForm().getCambioStatoRichiesta().getTi_stato_rich_rest_arch().parse();
        String tiStatoRichRestArchOld = getForm().getRichRestArchDetail().getTi_stato().parse();
        try {
            restArchEjb.cambiaStato(getUser().getIdUtente(), idRichRestArch, tiStatoRichRestArchOld,
                    tiStatoRichRestArch, dsNotaRichRestArch);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        goBack();
    }
    // </editor-fold>
}
