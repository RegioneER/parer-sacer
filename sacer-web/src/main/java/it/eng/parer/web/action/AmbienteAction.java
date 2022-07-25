package it.eng.parer.web.action;

import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.AmbienteAbstractAction;
import it.eng.parer.slite.gen.form.AmbienteForm.InsAmbiente;
import it.eng.parer.slite.gen.form.AmbienteForm.InsEnte;
import it.eng.parer.slite.gen.form.AmbienteForm.VisEnte;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableDescriptor;
import it.eng.parer.slite.gen.tablebean.OrgCategEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableDescriptor;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicAmbienteRowBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicAmbienteTableBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicEnteRowBean;
import it.eng.parer.slite.gen.viewbean.OrgVRicEnteTableBean;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.entity.constraint.ElvElencoVer;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStoricoEnteAmbienteTableBean;
import it.eng.parer.util.Utils;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AmbienteAction extends AmbienteAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(AmbienteAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    // Pattern per l'inserimento del nome ambiente/ente conforme al set di caratteri ammessi
    private static final String NOME_AMB_ENTE = "^[A-Za-z0-9_][A-Za-z0-9\\. _-]*$";
    private static final Pattern ambEntePattern = Pattern.compile(NOME_AMB_ENTE);
    private static final String AMMINISTRAZIONE = "amministrazione";
    private static final String CONSERVAZIONE = "conservazione";
    private static final String GESTIONE = "gestione";

    @Override
    public void initOnClick() throws EMFError {
    }

    /**
     * Metodo richiamato con il tasto "inserisci" all'interno di una lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void insertDettaglio() throws EMFError {
        // try {
        String lista = getTableName();

        if (lista.equals(getForm().getEntiList().getName())) {

            getForm().getInsEnte().setEditMode();
            getForm().getInsEnte().clear();

            String nmApplic = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                    CostantiDB.TipoAplVGetValAppart.APPLIC);
            BaseTableInterface ambienti = ambienteEjb.getAmbientiAbilitatiPerEnte(getUser().getIdUtente(), nmApplic);
            DecodeMap mappaAmbienti = new DecodeMap();
            ambienti.addSortingRule("nm_ambiente", SortingRule.ASC);
            ambienti.sort();
            mappaAmbienti.populatedMap(ambienti, "id_ambiente", "nm_ambiente");
            getForm().getInsEnte().getId_ambiente().setDecodeMap(mappaAmbienti);

            OrgCategEnteTableBean categTable = ambienteEjb.getOrgCategEnteTableBean(null);
            DecodeMap mappaCateg = new DecodeMap();
            mappaCateg.populatedMap(categTable, "id_categ_ente", "cd_categ_ente");
            getForm().getInsEnte().getId_categ_ente().setDecodeMap(mappaCateg);

            // TipoDefTemplateEnte
            getForm().getInsEnte().getTipo_def_template_ente()
                    .setDecodeMap(ComboGetter.getMappaTipoDefTemplateEnte(CostantiDB.TipoDefTemplateEnte.values()));
            getForm().getInsEnte().getTipo_def_template_ente()
                    .setValue(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name());

            // Date precompilate
            String dataOdierna = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            getForm().getInsEnte().getDt_ini_val_appart_ambiente().setValue(dataOdierna);
            getForm().getInsEnte().getDt_ini_val().setValue(dataOdierna);
            Calendar cal = Calendar.getInstance();
            cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            String dataFine = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
            getForm().getInsEnte().getDt_fin_val_appart_ambiente().setValue(dataFine);
            getForm().getInsEnte().getDt_fine_val().setValue(dataFine);

            getForm().getEntiList().setStatus(Status.insert);
            getForm().getInsEnte().setStatus(Status.insert);

        } else if (lista.equals(getForm().getAmbientiList().getName())) {
            getForm().getInsAmbiente().setEditMode();
            getForm().getInsAmbiente().clear();

            initAmbienteCombo();

            // Date precompilate
            String dataOdierna = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            getForm().getInsAmbiente().getDt_ini_val().setValue(dataOdierna);
            Calendar cal = Calendar.getInstance();
            cal.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
            String dataFine = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
            getForm().getInsAmbiente().getDt_fin_val().setValue(dataFine);

            getForm().getInsAmbiente().setStatus(Status.insert);
            getForm().getAmbientiList().setStatus(Status.insert);
        }
    }

    private void loadListeParametriAmbiente(BigDecimal idAmbiente, List<String> funzione, boolean hideDeleteButtons,
            boolean editModeAmministrazione, boolean editModeConservazione, boolean editModeGestione,
            boolean editModeMultipli) throws ParerUserError {
        // Parametri
        Object[] parametriObj = amministrazioneEjb.getAplParamApplicAmbiente(idAmbiente, funzione);

        // MEV26587
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) parametriObj[0];
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) parametriObj[1];
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) parametriObj[2];

        if (!editModeAmministrazione)
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);

        if (!editModeGestione)
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);

        if (!editModeConservazione)
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);

        getForm().getParametriAmministrazioneAmbienteList()
                .setTable((AplParamApplicTableBean) parametriAmministrazione);
        getForm().getParametriAmministrazioneAmbienteList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneAmbienteList().getTable().first();
        getForm().getParametriGestioneAmbienteList().setTable((AplParamApplicTableBean) parametriGestione);
        getForm().getParametriGestioneAmbienteList().getTable().setPageSize(300);
        getForm().getParametriGestioneAmbienteList().getTable().first();
        getForm().getParametriConservazioneAmbienteList().setTable((AplParamApplicTableBean) parametriConservazione);
        getForm().getParametriConservazioneAmbienteList().getTable().setPageSize(300);
        getForm().getParametriConservazioneAmbienteList().getTable().first();
        // Parametri multipli
        AplParamApplicTableBean parametriMultipli = amministrazioneEjb.getAplParamApplicMultiAmbiente(idAmbiente);
        getForm().getParametriMultipliAmbienteList().setTable(parametriMultipli);
        getForm().getParametriMultipliAmbienteList().getTable().setPageSize(300);
        getForm().getParametriMultipliAmbienteList().getTable().first();
        getForm().getParametriAmministrazioneAmbienteList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriGestioneAmbienteList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriConservazioneAmbienteList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriMultipliAmbienteList().setHideDeleteButton(hideDeleteButtons);
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneAmbienteList().getDs_valore_param_applic_ambiente_amm().setEditMode();
        } else {
            getForm().getParametriAmministrazioneAmbienteList().getDs_valore_param_applic_ambiente_amm().setViewMode();
        }
        if (editModeConservazione) {
            getForm().getParametriConservazioneAmbienteList().getDs_valore_param_applic_ambiente_cons().setEditMode();
        } else {
            getForm().getParametriConservazioneAmbienteList().getDs_valore_param_applic_ambiente_cons().setViewMode();
        }
        if (editModeGestione) {
            getForm().getParametriGestioneAmbienteList().getDs_valore_param_applic_ambiente_gest().setEditMode();
        } else {
            getForm().getParametriGestioneAmbienteList().getDs_valore_param_applic_ambiente_gest().setViewMode();
        }
        if (editModeMultipli) {
            getForm().getParametriMultipliAmbienteList().getDs_valore_param_applic_multi().setEditMode();
        } else {
            getForm().getParametriMultipliAmbienteList().getDs_valore_param_applic_multi().setViewMode();
        }
    }

    private void initAmbienteCombo() {
        BaseTable ambienteEnteTable = ambienteEjb
                .getUsrVAbilAmbEnteConvenzTableBean(new BigDecimal(getUser().getIdUtente()));
        DecodeMap mappaAmbienteEnte = new DecodeMap();
        mappaAmbienteEnte.populatedMap(ambienteEnteTable, "id_ambiente_ente_convenz", "nm_ambiente_ente_convenz");
        getForm().getInsAmbiente().getId_ambiente_ente_convenz().setDecodeMap(mappaAmbienteEnte);
    }

    /**
     * Metodo richiamato da ogni operazione della lista per caricare i dati di un record
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                || getNavigationEvent().equals(ListAction.NE_NEXT) || getNavigationEvent().equals(ListAction.NE_PREV)) {
            try {
                String lista = getTableName();

                if (lista.equals(getForm().getEntiList().getName()) && (getForm().getEntiList().getTable() != null)
                        && (getForm().getEntiList().getTable().size() > 0)) {

                    getForm().getInsEnte().setViewMode();
                    getForm().getEntiList().setStatus(Status.view);
                    getForm().getInsEnte().setStatus(Status.view);

                    BigDecimal idEnte = ((OrgVRicEnteRowBean) getForm().getEntiList().getTable().getCurrentRow())
                            .getIdEnte();
                    // popolo combo
                    String nmApplic = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC);
                    BaseTableInterface ambienti = ambienteEjb.getAmbientiAbilitatiPerEnte(getUser().getIdUtente(),
                            nmApplic);
                    DecodeMap mappaAmbienti = new DecodeMap();
                    mappaAmbienti.populatedMap(ambienti, "id_ambiente", "nm_ambiente");
                    getForm().getInsEnte().getId_ambiente().setDecodeMap(mappaAmbienti);

                    OrgCategEnteTableBean categTable = ambienteEjb.getOrgCategEnteTableBean(null);
                    DecodeMap mappaCateg = new DecodeMap();
                    mappaCateg.populatedMap(categTable, "id_categ_ente", "cd_categ_ente");
                    getForm().getInsEnte().getId_categ_ente().setDecodeMap(mappaCateg);

                    // TipoDefTemplateEnte
                    getForm().getInsEnte().getTipo_def_template_ente().setDecodeMap(
                            ComboGetter.getMappaTipoDefTemplateEnte(CostantiDB.TipoDefTemplateEnte.values()));
                    getForm().getInsEnte().getTipo_def_template_ente()
                            .setValue(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name());

                    // Carico il rowbean corrispondente all'id ottenuto
                    OrgEnteRowBean enteRowBean = ambienteEjb.getOrgEnteRowBean(idEnte);
                    getForm().getInsEnte().copyFromBean(enteRowBean);

                    getForm().getInsEnte().setStatus(Status.view);
                    reloadStruttureStoricoList(idEnte);
                } else if (lista.equals(getForm().getAmbientiList().getName())
                        && (getForm().getAmbientiList().getTable() != null)
                        && (getForm().getAmbientiList().getTable().size() > 0)) {

                    initAmbienteCombo();
                    BigDecimal idAmbiente = ((OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable()
                            .getCurrentRow()).getIdAmbiente();

                    loadDettaglioAmbiente(idAmbiente);

                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    private void loadDettaglioAmbiente(BigDecimal idAmbiente) throws EMFError, ParerUserError {
        // Carico il rowbean corrispondente all'id ottenuto
        OrgAmbienteRowBean ambienteRowBean = ambienteEjb.getOrgAmbienteRowBean(idAmbiente);
        getForm().getInsAmbiente().copyFromBean(ambienteRowBean);

        if (ambienteRowBean.getIdEnteGestore() != null) {
            // Ricavo il valore dell'ambiente ente gestore e lo setto nella combo
            BigDecimal idAmbienteEnteConvenz = ambienteEjb.getIdAmbienteEnteConvenz(ambienteRowBean.getIdEnteGestore());
            getForm().getInsAmbiente().getId_ambiente_ente_convenz().setValue("" + idAmbienteEnteConvenz);

            // Popolo di conseguenza la combo ENTE GESTORE
            getForm().getInsAmbiente().getId_ente_gestore().reset();
            getForm().getInsAmbiente().getId_ente_gestore().clear();
            BaseTable enteConvenzTable = ambienteEjb.getEntiGestoreAbilitatiTableBean(
                    new BigDecimal(getUser().getIdUtente()),
                    getForm().getInsAmbiente().getId_ambiente_ente_convenz().parse());
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteConvenzTable, "id_ente_gestore", "nm_ente_gestore");
            getForm().getInsAmbiente().getId_ente_gestore().setDecodeMap(mappaEnte);
            getForm().getInsAmbiente().getId_ente_gestore().setValue("" + ambienteRowBean.getIdEnteGestore());

            BaseTable tabella = ambienteEjb.getEntiConservatori(getUser().getIdUtente(),
                    ambienteRowBean.getIdEnteGestore());
            getForm().getInsAmbiente().getId_ente_conserv()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tabella, "id_ente_siam", "nm_ente_siam"));
            getForm().getInsAmbiente().getId_ente_conserv().setValue("" + ambienteRowBean.getIdEnteConserv());
        }

        // Carico la lista degli enti dell'ambiente
        OrgEnteRowBean temp = new OrgEnteRowBean();
        temp.setIdAmbiente(ambienteRowBean.getIdAmbiente());
        reloadEntiList(temp);

        getForm().getInsAmbiente().setViewMode();
        getForm().getInsAmbiente().setStatus(Status.view);
        getForm().getAmbientiList().setStatus(Status.view);

        // Parametri
        loadListeParametriAmbiente(idAmbiente, null, false, false, false, false, false);
        getForm().getParametriAmbienteButtonList().getParametriAmministrazioneAmbienteButton().setEditMode();
        getForm().getParametriAmbienteButtonList().getParametriConservazioneAmbienteButton().setEditMode();
        getForm().getParametriAmbienteButtonList().getParametriGestioneAmbienteButton().setEditMode();
        getForm().getParametriAmbienteButtonList().getParametriMultipliAmbienteButton().setEditMode();
    }

    /**
     * Metodo richiamato dal tasto "annulla" nella NavBar
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void undoDettaglio() throws EMFError {
        String publisher = getLastPublisher();
        if (publisher.equals(Application.Publisher.CREA_AMBIENTE)) {

            getForm().getAmbientiList().setStatus(Status.view);
            getForm().getInsAmbiente().setStatus(Status.view);
            getForm().getInsAmbiente().setViewMode();

            goBack();
        } else if (publisher.equals(Application.Publisher.CREA_ENTE)) {

            getForm().getEntiList().setStatus(Status.view);
            getForm().getInsEnte().setStatus(Status.view);
            getForm().getInsEnte().setViewMode();

            goBack();
        } else if (publisher.equals(Application.Publisher.PARAMETRI_AMBIENTE)
                && getForm().getInsAmbiente().getStatus() != null
                && getForm().getInsAmbiente().getStatus().toString().equals("update")) {
            getForm().getInsAmbiente().setStatus(Status.view);
            getForm().getParametriAmministrazioneAmbienteList().setViewMode();
            getForm().getParametriConservazioneAmbienteList().setViewMode();
            getForm().getParametriGestioneAmbienteList().setViewMode();
            getForm().getParametriMultipliAmbienteList().setViewMode();
            loadDettaglio();
            goBack();
        }
    }

    /**
     * Metodo richiamato dal tasto "Salva" della NavBar
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void saveDettaglio() throws EMFError {

        String publisher = getLastPublisher();
        if (Application.Publisher.CREA_AMBIENTE.equals(publisher)) {
            salvaAmbiente();
        } else if (Application.Publisher.CREA_ENTE.equals(publisher)) {
            salvaEnte();
        } else if (Application.Publisher.PARAMETRI_AMBIENTE.equals(publisher)) {
            salvaParametriAmbiente();
        }
    }

    /**
     *
     * Metodo che salva le modifiche ad un ambiente nel database
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaAmbiente() throws EMFError {

        getForm().getParametriConservazioneAmbienteList().post(getRequest());
        getMessageBox().clear();
        InsAmbiente ambiente = getForm().getInsAmbiente();

        ambiente.post(getRequest());
        OrgAmbienteRowBean ambienteRowBean = new OrgAmbienteRowBean();
        ambiente.copyToBean(ambienteRowBean);

        try {
            if (ambiente.validate(getMessageBox())) {

                // Controllo di unicità del nome ambiente nel database
                if (ambiente.getNm_ambiente().parse() == null) {
                    getMessageBox().addError("Errore di compilazione form: Nome ambiente non inserito</br>");
                } else {
                    // Controllo che il nome struttura rispetti
                    Matcher m = ambEntePattern.matcher(ambiente.getNm_ambiente().parse());
                    if (!m.matches()) {
                        getMessageBox().addError(
                                "Errore di compilazione form: Nome ambiente contenente caratteri non permessi");
                    }
                }

                if (ambiente.getDs_ambiente().parse() == null) {
                    getMessageBox().addError("Errore di compilazione form: descrizione ambiente non inserito</br>");
                }
                if (ambiente.getDt_fin_val().parse().before(ambiente.getDt_ini_val().parse())) {
                    getMessageBox().addError(
                            "La data di inizio validità deve essere minore o uguale alla data di fine validità</br>");
                }
                // // Controllo valori possibili su ambiente
                AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                        .getParametriConservazioneAmbienteList().getTable();

                if (parametriConservazione != null) {
                    String tiValidElenco = "";
                    String tiModValidElenco = "";
                    for (AplParamApplicRowBean row : parametriConservazione) {
                        if (row.getNmParamApplic().equals("TI_VALID_ELENCO")) {
                            tiValidElenco = row.getString("ds_valore_param_applic_ambiente_cons");
                        }
                        if (row.getNmParamApplic().equals("TI_MOD_VALID_ELENCO")) {
                            tiModValidElenco = row.getString("ds_valore_param_applic_ambiente_cons");
                        }
                    }
                    if (ElvElencoVer.TiValidElenco.FIRMA.name().equals(tiValidElenco)
                            && ElvElencoVer.TiModValidElenco.AUTOMATICA.name().equals(tiModValidElenco)) {
                        getMessageBox().addError(
                                "La combinazione tipo validazione = FIRMA e modalità validazione = AUTOMATICA al momento non e' ammessa</br>");
                    }
                }

                if (getMessageBox().isEmpty()) {

                    OrgEnteRowBean temp = new OrgEnteRowBean();
                    BigDecimal idAmb = null;

                    if (getForm().getAmbientiList().getStatus().equals(Status.update)) {
                        idAmb = ((OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable().getCurrentRow())
                                .getIdAmbiente();
                        checkAndSaveModificaAmbiente(idAmb, ambienteRowBean);
                    } else if (getForm().getAmbientiList().getStatus().equals(Status.insert)) {
                        ambiente.copyToBean(ambienteRowBean);
                        idAmb = ambienteEjb.insertOrgAmbiente(ambienteRowBean);
                        getMessageBox().addInfo("Nuovo ambiente salvato con successo");
                        getMessageBox().setViewMode(ViewMode.plain);
                        OrgVRicAmbienteTableBean table = new OrgVRicAmbienteTableBean();
                        table.add(ambienteRowBean);
                        table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                        table.setCurrentRowIndex(0);
                        getForm().getAmbientiList().setTable(table);
                        temp.setIdAmbiente(ambienteRowBean.getIdAmbiente());

                        reloadEntiList(temp);
                        loadListeParametriAmbiente(idAmb, null, false, false, false, false, false);
                        getForm().getInsAmbiente().setViewMode();
                        getForm().getInsAmbiente().setStatus(Status.view);
                        getForm().getAmbientiList().setStatus(Status.view);

                    }
                }
            }
            forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
        }
    }

    private void checkAndSaveModificaAmbiente(BigDecimal idAmb, OrgAmbienteRowBean ambienteRowBean)
            throws ParerUserError, EMFError {
        Date dataCorrente = new Date();
        // Se l'ambiente non è più valido
        if (!(ambienteRowBean.getDtIniVal().compareTo(dataCorrente) < 0
                && ambienteRowBean.getDtFinVal().compareTo(dataCorrente) > 0)) {
            boolean esistenzaEntiConvenzionati = ambienteEjb.existsEntiValidiAmbienteTableBean(idAmb);
            boolean esistenzaUtentiUnAmbiente = ambienteEjb.existsUtentiAttiviAbilitatiAdAmbienteTableBean(idAmb);
            if (esistenzaEntiConvenzionati || esistenzaUtentiUnAmbiente) {
                Object[] attributiSalvataggioModificaAmbiente = new Object[2];
                attributiSalvataggioModificaAmbiente[0] = idAmb;
                attributiSalvataggioModificaAmbiente[1] = ambienteRowBean;
                getSession().setAttribute("attributiSalvataggioModificaAmbiente", attributiSalvataggioModificaAmbiente);
                getRequest().setAttribute("customModificaAmbiente", true);
                String messaggio = "Attenzione: esiste almeno un ente convenzionato valido appartenente all’ambiente "
                        + "oppure esiste almeno un utente attivo con abilitazione all’ambiente. "
                        + "Confermare la modifica dell’ambiente?";
                getRequest().setAttribute("messaggioModificaAmbiente", messaggio);
                getMessageBox().setViewMode(ViewMode.alert);
            } else {
                eseguiSalvataggioModificaAmbiente(idAmb, ambienteRowBean);
            }
        } else {
            eseguiSalvataggioModificaAmbiente(idAmb, ambienteRowBean);
        }
    }

    public void eseguiSalvataggioModificaAmbiente(BigDecimal idAmb, OrgAmbienteRowBean ambienteRowBean)
            throws ParerUserError, EMFError {

        OrgEnteRowBean temp = new OrgEnteRowBean();
        temp.setIdAmbiente(idAmb);

        ambienteEjb.updateOrgAmbiente(idAmb, ambienteRowBean);
        getMessageBox().addInfo("Update ambiente effettuato con successo");
        getMessageBox().setViewMode(ViewMode.plain);

        reloadEntiList(temp);

        loadListeParametriAmbiente(idAmb, null, false, false, false, false, false);
        getForm().getInsAmbiente().setViewMode();
        getForm().getInsAmbiente().setStatus(Status.view);
        getForm().getAmbientiList().setStatus(Status.view);

        // Rimuovo dalla sessione eventuali attributi
        getSession().removeAttribute("attributiSalvataggioModificaAmbiente");
        forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
    }

    public void confermaSalvataggioModificaAmbiente() {
        if (getSession().getAttribute("attributiSalvataggioModificaAmbiente") != null) {
            Object[] attributiSalvataggioModificaAmbiente = (Object[]) getSession()
                    .getAttribute("attributiSalvataggioModificaAmbiente");
            BigDecimal idAmb = (BigDecimal) attributiSalvataggioModificaAmbiente[0];
            OrgAmbienteRowBean ambienteRowBean = (OrgAmbienteRowBean) attributiSalvataggioModificaAmbiente[1];
            try {
                eseguiSalvataggioModificaAmbiente(idAmb, ambienteRowBean);
            } catch (ParerUserError ex) {
                logger.error("Errore nel salvataggio della modifica ambiente", ex);
                getMessageBox().addError("Errore nel salvataggio della modifica ambiente");
            } catch (EMFError ex) {
                logger.error(ex.getMessage());
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    public void annullaSalvataggioModificaAmbiente() {
        getSession().removeAttribute("attributiSalvataggioModificaAmbiente");
        forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
    }

    /**
     *
     * Metodo che salva le modifiche agli enti all'interno del database
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaEnte() throws EMFError {
        getMessageBox().clear();

        InsEnte ente = getForm().getInsEnte();
        ente.post(getRequest());
        OrgEnteRowBean enteRowBean = new OrgEnteRowBean();
        ente.copyToBean(enteRowBean);

        try {
            if (ente.validate(getMessageBox())) {
                Matcher m = ambEntePattern.matcher(ente.getNm_ente().parse());
                if (!m.matches()) {
                    getMessageBox()
                            .addError("Errore di compilazione form: Nome ente contenente caratteri non permessi");
                }

                // Controllo sulle date
                if (ente.getDt_fin_val_appart_ambiente().parse().before(ente.getDt_ini_val_appart_ambiente().parse())) {
                    getMessageBox().addError(
                            "Data di inizio validità appartenenza ambiente superiore alla data di fine validità");
                }
                if (ente.getDt_fine_val().parse().before(ente.getDt_ini_val().parse())) {
                    getMessageBox().addError("Data di inizio validità superiore alla data di fine validità");
                }
                if (!ambienteEjb.checkDateAmbiente(ente.getId_ambiente().parse(),
                        ente.getDt_ini_val_appart_ambiente().parse(), ente.getDt_fin_val_appart_ambiente().parse())) {
                    getMessageBox().addError(
                            "La data di inizio e di fine validità dell’appartenenza dell’ente all’ambiente Sacer devono essere incluse nella validità dell’ambiente");
                }

                // Controllo sull'ambiente in caso di modifica
                if (!(ente.getDt_ini_val().parse().compareTo(ente.getDt_ini_val_appart_ambiente().parse()) <= 0
                        && ente.getDt_fine_val().parse().compareTo(ente.getDt_ini_val_appart_ambiente().parse()) >= 0
                        && ente.getDt_ini_val().parse().compareTo(ente.getDt_fin_val_appart_ambiente().parse()) <= 0
                        && ente.getDt_fine_val().parse()
                                .compareTo(ente.getDt_fin_val_appart_ambiente().parse()) >= 0)) {
                    getMessageBox().addError(
                            "La data di inizio e di fine validità dell’appartenenza dell’ente all’ambiente Sacer devono essere incluse nella validità dell’ente");
                }

                BigDecimal idCategEnte = ente.getId_categ_ente().parse();
                if (idCategEnte != null) {
                    enteRowBean.setIdCategEnte(idCategEnte);
                }

                if (getMessageBox().isEmpty()) {
                    BigDecimal idEnte = null;
                    if (getForm().getEntiList().getStatus().equals(Status.update)) {
                        // Ricavo l'info di quante storicizzazioni ho finora
                        idEnte = ente.getId_ente().parse();
                        checkAndSaveModificaEnte(idEnte, enteRowBean);

                    } else if (getForm().getEntiList().getStatus().equals(Status.insert)) {
                        Map<String, String[]> map = getRequest().getParameterMap();
                        Iterator<Entry<String, String[]>> iterator = map.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Entry<String, String[]> next = iterator.next();
                            String key = next.getKey();
                            String[] values = next.getValue();
                            if (key.contains("Fl_") && (values.length > 0 && "-1".equals(values[0]))) {
                                ((CheckBox<String>) ente.getComponent(key)).setChecked(true);
                                enteRowBean.setObject(key, "1");
                            }
                        }

                        ente.copyToBean(enteRowBean);
                        ambienteEjb.insertOrgEnte(enteRowBean);

                        getMessageBox().addInfo("Nuovo ente salvato con successo");
                        getMessageBox().setViewMode(ViewMode.plain);

                        idEnte = enteRowBean.getIdEnte();
                        ente.getId_ente().setValue(idEnte.toPlainString());

                        getForm().getInsEnte().setViewMode();
                        getForm().getEntiList().setStatus(Status.view);
                        getForm().getInsEnte().setStatus(Status.view);

                        reloadEntiList(enteRowBean);
                        reloadStruttureStoricoList(idEnte);
                    }
                }
            }
            forwardToPublisher(Application.Publisher.CREA_ENTE);
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
            forwardToPublisher(Application.Publisher.CREA_ENTE);
        }
    }

    private void checkAndSaveModificaEnte(BigDecimal idEnte, OrgEnteRowBean enteRowBean)
            throws ParerUserError, EMFError {
        Date dataCorrente = new Date();
        // Se l'ente non è più valido
        if (!(enteRowBean.getDtIniVal().compareTo(dataCorrente) < 0
                && enteRowBean.getDtFineVal().compareTo(dataCorrente) > 0)) {
            boolean esistenzaUtentiUnEnte = ambienteEjb.existsUtentiAttiviAbilitatiAdEnteTableBean(idEnte);
            if (esistenzaUtentiUnEnte) {
                Object[] attributiSalvataggioModificaEnte = new Object[2];
                attributiSalvataggioModificaEnte[0] = idEnte;
                attributiSalvataggioModificaEnte[1] = enteRowBean;
                getSession().setAttribute("attributiSalvataggioModificaEnte", attributiSalvataggioModificaEnte);
                getRequest().setAttribute("customModificaEnte", true);
                String messaggio = "Attenzione: esiste almeno un utente attivo con abilitazione all’ente. Confermare la modifica dell’ente?";
                getRequest().setAttribute("messaggioModificaEnte", messaggio);
            } else {
                eseguiSalvataggioModificaEnte(idEnte, enteRowBean);
            }
        } else {
            eseguiSalvataggioModificaEnte(idEnte, enteRowBean);
        }
    }

    public void eseguiSalvataggioModificaEnte(BigDecimal idEnte, OrgEnteRowBean enteRowBean)
            throws ParerUserError, EMFError {
        InsEnte ente = getForm().getInsEnte();
        // Controllo sull'ambiente
        ambienteEjb.isAmbienteModificato(ente.getId_ambiente().parse(), ente.getId_ente().parse(),
                ente.getDt_ini_val_appart_ambiente().parse(), ente.getDt_fin_val_appart_ambiente().parse());
        // Ricavo l'info di quante storicizzazioni ho finora
        int numStoricizzazioniPreSalvataggio = ambienteEjb.getNumStoricizzazioni(idEnte);
        ambienteEjb.updateOrgEnte(idEnte, enteRowBean);
        int numStoricizzazioniPostSalvataggio = ambienteEjb.getNumStoricizzazioni(idEnte);
        if (numStoricizzazioniPreSalvataggio == numStoricizzazioniPostSalvataggio) {
            getMessageBox().addWarning(
                    "Le date di appartenenza dell’ente all’ambiente si sovrappongono con quelle indicate sul precedente ambiente: è stata eseguita la modifica dell’ente senza eseguire la storicizzazione");
        } else {
            getMessageBox().addInfo("Update dell'ente effettuato con successo");
        }
        getMessageBox().setViewMode(ViewMode.plain);
        getForm().getVisEnte().clear();
        getForm().getVisEnte().getNm_ente().setValue(enteRowBean.getNmEnte());
        getForm().getVisEnte().getId_ambiente().setValue(enteRowBean.getIdAmbiente().toString());

        getForm().getInsEnte().setViewMode();
        getForm().getEntiList().setStatus(Status.view);
        getForm().getInsEnte().setStatus(Status.view);

        reloadEntiList(enteRowBean);
        reloadStruttureStoricoList(idEnte);

        // Rimuovo dalla sessione eventuali attributi
        getSession().removeAttribute("attributiSalvataggioModificaEnte");
        forwardToPublisher(Application.Publisher.CREA_ENTE);
    }

    public void confermaSalvataggioModificaEnte() {
        if (getSession().getAttribute("attributiSalvataggioModificaEnte") != null) {
            Object[] attributiSalvataggioModificaEnte = (Object[]) getSession()
                    .getAttribute("attributiSalvataggioModificaEnte");
            BigDecimal idAmb = (BigDecimal) attributiSalvataggioModificaEnte[0];
            OrgEnteRowBean enteRowBean = (OrgEnteRowBean) attributiSalvataggioModificaEnte[1];
            try {
                eseguiSalvataggioModificaEnte(idAmb, enteRowBean);
            } catch (ParerUserError ex) {
                logger.error("Errore nel salvataggio della modifica ente", ex);
                getMessageBox().addError("Errore nel salvataggio della modifica ente");
            } catch (EMFError ex) {
                logger.error(ex.getMessage());
                getMessageBox().addError(ex.getDescription());
            }
        }
    }

    public void annullaSalvataggioModificaEnte() {
        getSession().removeAttribute("attributiSalvataggioModificaEnte");
        forwardToPublisher(Application.Publisher.CREA_ENTE);
    }

    /**
     *
     * Metodo richiamato ad ogni operazione sulla lista subito dopo la LoadDettaglio
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        String action = getNavigationEvent();
        if (lista.equals(getForm().getAmbientiList().getName()) && (getForm().getAmbientiList().getTable() != null)) {
            forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
        } else if ((lista.equals(getForm().getEntiList().getName()) && (getForm().getEntiList().getTable() != null))
                || lista.equals(getForm().getInsEnte().getName())) {

            boolean abilitato = true;
            if ((action.equals(NE_DETTAGLIO_UPDATE) || action.equals(NE_DETTAGLIO_DELETE))
                    && !getForm().getEntiList().getTable().isEmpty()) {
                String nmEnte = ((OrgVRicEnteRowBean) getForm().getEntiList().getTable().getCurrentRow()).getNmEnte();
                String nmApplic = configHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                        CostantiDB.TipoAplVGetValAppart.APPLIC);
                if (ambienteEjb.getEntiAbilitatiPerStrut(getUser().getIdUtente(), nmApplic, nmEnte, null, null)
                        .isEmpty()) {
                    abilitato = false;
                }
            }
            if (abilitato) {
                forwardToPublisher(Application.Publisher.CREA_ENTE);
            } else {
                getMessageBox().addError("Utente non abilitato per l"
                        + (getNavigationEvent().equals(NE_DETTAGLIO_UPDATE) ? "a modifica" : "'eliminazione")
                        + " del record");
                forwardToPublisher(getLastPublisher());
            }
        } else if (lista.equals(getForm().getStruttureList().getName())
                && (getForm().getStruttureList().getTable() != null)) {
            forwardToPublisher(Application.Publisher.CREA_STRUTTURA);
        }
    }

    /**
     *
     * Metodo invocato alla pressione del tasto "Indietro"
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.CREA_AMBIENTE)) {
            goBackTo(Application.Publisher.AMBIENTE_RICERCA);
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {

        return Application.Publisher.AMBIENTE_RICERCA;

    }

    @Override
    public void process() throws EMFError {
    }

    /**
     * Metodo che corrisponde all'entry nel menu: "Gestione Ambienti"
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.Amministrazione.Ambienti")
    public void ricercaAmbiente() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.Ambienti");
        getForm().getVisAmbiente().setEditMode();
        getForm().getAmbientiList().setHideInsertButton(
                !ambienteEjb.isCreaAmbienteActive(getUser().getIdUtente(), configHelper.getValoreParamApplic(
                        "NM_APPLIC", null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC)));
        getForm().getAmbientiList().setHideUpdateButton(
                !ambienteEjb.isCreaAmbienteActive(getUser().getIdUtente(), configHelper.getValoreParamApplic(
                        "NM_APPLIC", null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC)));
        getForm().getAmbientiList().setHideDeleteButton(
                !ambienteEjb.isCreaAmbienteActive(getUser().getIdUtente(), configHelper.getValoreParamApplic(
                        "NM_APPLIC", null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC)));
        visAmbienteButton();
    }

    /**
     * Metodo corrispondente alla entry nel menu "Gestione Enti"
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.AmministrazioneStrutture.Enti")
    // Corrisponde a enteRicerca.jsp
    public void ricercaEnte() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneStrutture.Enti");

        getForm().getEntiList().clear();

        OrgVRicAmbienteTableBean ambienti = new OrgVRicAmbienteTableBean();
        try {
            ambienti = ambienteEjb.getAmbientiAbilitatiPerRicerca(getUser().getIdUtente(), null);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        DecodeMap mappaAmbienti = new DecodeMap();
        ambienti.addSortingRule("nm_ambiente", SortingRule.ASC);
        ambienti.sort();
        mappaAmbienti.populatedMap(ambienti, "id_ambiente", "nm_ambiente");

        getForm().getVisEnte().getId_ambiente().setDecodeMap(mappaAmbienti);
        getForm().getVisEnte().getTipo_def_template_ente()
                .setDecodeMap(ComboGetter.getMappaTipoDefTemplateEnte(CostantiDB.TipoDefTemplateEnte.values()));

        OrgCategEnteTableBean categTable = ambienteEjb.getOrgCategEnteTableBean(null);
        DecodeMap mappaCateg = new DecodeMap();
        mappaCateg.populatedMap(categTable, "id_categ_ente", "cd_categ_ente");
        getForm().getInsEnte().getId_categ_ente().setDecodeMap(mappaCateg);
        OrgEnteTableBean table = new OrgEnteTableBean();
        table.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        table.first();
        getForm().getEntiList().setTable(table);
        getForm().getVisEnte().setEditMode();

        forwardToPublisher(Application.Publisher.ENTE_RICERCA);

    }

    @Override
    public String getControllerName() {
        return Application.Actions.AMBIENTE;
    }

    /*
     * 
     * METODI DI RICERCA AMBIENTE/ENTE/STRUTTURA
     * 
     */
    @Override
    public void visAmbienteButton() throws EMFError {
        // raccoglie i dati richiesti nel form
        // se i dati non sono inseriti, i campi sono null
        getForm().getVisAmbiente().post(getRequest());

        OrgVRicAmbienteTableBean ambienteTableBean = new OrgVRicAmbienteTableBean();
        try {
            ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerRicerca(getUser().getIdUtente(),
                    getForm().getVisAmbiente().getNm_ambiente().parse());
            ambienteTableBean.addSortingRule(OrgAmbienteTableDescriptor.COL_NM_AMBIENTE, SortingRule.ASC);
            ambienteTableBean.sort();
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

        getForm().getAmbientiList().setTable(ambienteTableBean);
        getForm().getAmbientiList().getTable().setPageSize(10);
        getForm().getAmbientiList().getTable().first();

        forwardToPublisher(Application.Publisher.AMBIENTE_RICERCA);
    }

    @Override
    public void visEnteButton() throws EMFError {
        VisEnte enteForm = getForm().getVisEnte();

        OrgEnteRowBean enteRowBean = new OrgEnteRowBean();
        enteForm.post(getRequest());
        enteForm.copyToBean(enteRowBean);

        OrgVRicEnteTableBean enteTableBean = ambienteEjb.getOrgEnteTableBean(enteRowBean, getUser().getIdUtente());

        getForm().getEntiList().setTable(enteTableBean);
        getForm().getEntiList().getTable().first();

        getForm().getEntiList().getTable().setPageSize(10);
        getForm().getEntiList().getTable().first();
        getForm().getEntiList().getTable().addSortingRule("nm_ambiente", SortingRule.ASC);
        getForm().getEntiList().getTable().addSortingRule(OrgEnteTableDescriptor.COL_NM_ENTE, SortingRule.ASC);
        getForm().getEntiList().getTable().sort();

        getForm().getVisEnte().setStatus(null);

        // Visualizzo il bottone di inserimento solo se l’ente convenzionato di appartenenza dell’utente ha
        // ti_ente_convenz = AMMINISTRATORE o CONSERVATORE o GESTORE
        if (ambienteEjb.showInsertButton(getUser().getIdUtente())) {
            getForm().getEntiList().setHideInsertButton(false);
        } else {
            getForm().getEntiList().setHideInsertButton(true);
        }

        forwardToPublisher(Application.Publisher.ENTE_RICERCA);

    }

    private void reloadEntiList(OrgEnteRowBean enteRowBean) throws EMFError {

        OrgAmbienteRowBean ambienteTemp;
        OrgVRicEnteTableBean enteTableBean = ambienteEjb.getOrgEnteTableBean(enteRowBean, getUser().getIdUtente());

        for (OrgVRicEnteRowBean row : enteTableBean) {
            ambienteTemp = ambienteEjb.getOrgAmbienteRowBean(row.getIdAmbiente());
            row.setString("nm_ambiente", ambienteTemp.getNmAmbiente());
        }

        enteTableBean.addSortingRule("nm_ambiente", SortingRule.ASC);
        enteTableBean.addSortingRule(OrgEnteTableDescriptor.COL_NM_ENTE, SortingRule.ASC);
        enteTableBean.sort();

        int inizio = 0;
        int pageSize = 10;
        int paginaCorrente = 1;
        if (getForm().getEntiList().getTable() != null) {
            inizio = getForm().getEntiList().getTable().getFirstRowPageIndex();
            paginaCorrente = getForm().getEntiList().getTable().getCurrentPageIndex();
        }

        getForm().getEntiList().setTable(enteTableBean);
        getForm().getEntiList().getTable().setPageSize(pageSize);
        this.lazyLoadGoPage(getForm().getEntiList(), paginaCorrente);
        getForm().getEntiList().getTable().setCurrentRowIndex(inizio);

        getForm().getVisEnte().setStatus(null);
    }

    private void reloadAmbientiList() throws EMFError {
        OrgVRicAmbienteTableBean ambienteTableBean = new OrgVRicAmbienteTableBean();

        try {
            ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerRicerca(getUser().getIdUtente(), null);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
        ambienteTableBean.addSortingRule(OrgAmbienteTableDescriptor.COL_NM_AMBIENTE, SortingRule.ASC);
        ambienteTableBean.sort();
        ambienteTableBean.first();

        // Ricarico la lista degli ambienti con i settaggi sul paginatore
        int inizio = getForm().getAmbientiList().getTable().getFirstRowPageIndex();
        int pageSize = getForm().getAmbientiList().getTable().getPageSize();
        int paginaCorrente = getForm().getAmbientiList().getTable().getCurrentPageIndex();
        getForm().getAmbientiList().setTable(ambienteTableBean);
        getForm().getAmbientiList().getTable().setPageSize(pageSize);
        this.lazyLoadGoPage(getForm().getAmbientiList(), paginaCorrente);
        getForm().getAmbientiList().getTable().setCurrentRowIndex(inizio);
    }

    private void reloadStruttureStoricoList(BigDecimal idEnte) throws EMFError {
        OrgStrutTableBean strutTableBean;

        OrgEnteRowBean enteTemp;
        OrgAmbienteRowBean ambienteTemp;

        strutTableBean = struttureEjb.getOrgStrutTableBean(null, idEnte, null, null);

        getForm().getStruttureList().setTable(strutTableBean);

        getForm().getStruttureList().getTable().setPageSize(10);

        getForm().getStruttureList().getTable().first();

        // Storico
        OrgStoricoEnteAmbienteTableBean storicoEnteAmbienteTableBean = ambienteEjb
                .getOrgStoricoEnteAmbienteTableBean(idEnte);
        getForm().getStoricoEnteAmbienteList().setTable(storicoEnteAmbienteTableBean);
        getForm().getStoricoEnteAmbienteList().getTable().first();
        getForm().getStoricoEnteAmbienteList().getTable().setPageSize(10);
    }

    /*
     * Metodi per update Ambienti / Enti / Strutture
     * 
     */
    @Override
    public void updateAmbientiList() throws EMFError {
        OrgAmbienteRowBean ambienteRowBean = ambienteEjb.getOrgAmbienteRowBean(
                ((OrgVRicAmbienteRowBean) (getForm().getAmbientiList().getTable().getCurrentRow())).getNmAmbiente());

        OrgEnteRowBean enteRowBean = new OrgEnteRowBean();
        enteRowBean.setIdAmbiente(ambienteRowBean.getIdAmbiente());

        if (ambienteEjb.getOrgEnteTableBean(enteRowBean, getUser().getIdUtente()).isEmpty()) {
            getForm().getInsAmbiente().setEditMode();
        } else {
            getForm().getInsAmbiente().setViewMode();
            getForm().getInsAmbiente().getDs_ambiente().setEditMode();
            getForm().getInsAmbiente().getDs_note().setEditMode();
            getForm().getInsAmbiente().getDt_fin_val().setEditMode();
        }

        getForm().getInsAmbiente().setStatus(Status.update);
        getForm().getAmbientiList().setStatus(Status.update);
        getForm().getEntiList().clear();
    }

    @Override
    public void updateEntiList() throws EMFError {
        if (!getMessageBox().hasError()) {
            InsEnte insEnte = getForm().getInsEnte();
            BigDecimal idAmbiente = ((OrgVRicEnteRowBean) (getForm().getEntiList().getTable().getCurrentRow()))
                    .getIdAmbiente();
            String nm = ((OrgVRicEnteRowBean) (getForm().getEntiList().getTable().getCurrentRow())).getNmEnte();
            OrgEnteRowBean enteRowBean = ambienteEjb.getOrgEnteRowBean(nm, idAmbiente);
            OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
            strutRowBean.setIdEnte(enteRowBean.getIdEnte());

            insEnte.setStatus(Status.update);
            getForm().getEntiList().setStatus(Status.update);

            BaseTableInterface ambienti = ambienteEjb.getAmbientiAbilitatiPerEnte(getUser().getIdUtente(), configHelper
                    .getValoreParamApplic("NM_APPLIC", null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
            DecodeMap mappaAmbienti = new DecodeMap();

            mappaAmbienti.populatedMap(ambienti, "id_ambiente", "nm_ambiente");

            insEnte.getId_ambiente().setDecodeMap(mappaAmbienti);

            OrgCategEnteTableBean categTable = ambienteEjb.getOrgCategEnteTableBean(null);
            DecodeMap mappaCateg = new DecodeMap();
            mappaCateg.populatedMap(categTable, "id_categ_ente", "cd_categ_ente");
            insEnte.getId_categ_ente().setDecodeMap(mappaCateg);

            insEnte.getId_ambiente().setValue(idAmbiente.toPlainString());

            if (!struttureEjb.isEnteWithStruts(enteRowBean.getIdEnte())) {
                insEnte.setEditMode();
            } else {
                insEnte.setEditMode();
                insEnte.getNm_ente().setViewMode();
                insEnte.getTipo_def_template_ente().setViewMode();
                insEnte.getCd_ente_normaliz().setViewMode();
            }

            insEnte.getFl_cessato().setViewMode();
        }
    }

    @Override
    public void updateInsEnte() throws EMFError {
        if (!getMessageBox().hasError()) {
            InsEnte insEnte = getForm().getInsEnte();
            BigDecimal idAmbiente = insEnte.getId_ambiente().parse();
            String nm = insEnte.getNm_ente().parse();
            OrgEnteRowBean enteRowBean = ambienteEjb.getOrgEnteRowBean(nm, idAmbiente);
            OrgStrutRowBean strutRowBean = new OrgStrutRowBean();
            strutRowBean.setIdEnte(enteRowBean.getIdEnte());

            insEnte.setStatus(Status.update);
            getForm().getEntiList().setStatus(Status.update);

            BaseTableInterface ambienti = ambienteEjb.getAmbientiAbilitatiPerEnte(getUser().getIdUtente(), configHelper
                    .getValoreParamApplic("NM_APPLIC", null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
            DecodeMap mappaAmbienti = new DecodeMap();

            mappaAmbienti.populatedMap(ambienti, "id_ambiente", "nm_ambiente");

            insEnte.getId_ambiente().setDecodeMap(mappaAmbienti);

            OrgCategEnteTableBean categTable = ambienteEjb.getOrgCategEnteTableBean(null);
            DecodeMap mappaCateg = new DecodeMap();
            mappaCateg.populatedMap(categTable, "id_categ_ente", "cd_categ_ente");
            insEnte.getId_categ_ente().setDecodeMap(mappaCateg);

            insEnte.getId_ambiente().setValue(idAmbiente.toPlainString());

            if (!struttureEjb.isEnteWithStruts(enteRowBean.getIdEnte())) {
                insEnte.setViewMode();
                insEnte.getId_ambiente().setEditMode();
                insEnte.getTipo_def_template_ente().setEditMode();
                insEnte.getCd_ente_normaliz().setEditMode();
            } else {
                insEnte.setEditMode();
            }

            insEnte.getFl_cessato().setViewMode();
        }
    }

    /*
     * Metodi per cancellazione Ambienti / Enti / Strutture
     * 
     */
    @Override
    public void deleteAmbientiList() throws EMFError {

        getMessageBox().clear();

        OrgVRicAmbienteRowBean ambienteRowBean = (OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable()
                .getCurrentRow();
        BigDecimal idAmbiente = ambienteRowBean.getIdAmbiente();

        OrgEnteRowBean enteRowBean = new OrgEnteRowBean();
        enteRowBean.setIdAmbiente(idAmbiente);

        OrgVRicEnteTableBean enteTableBean = ambienteEjb.getOrgEnteTableBean(enteRowBean, getUser().getIdUtente());

        if (!enteTableBean.isEmpty()) {
            getMessageBox().addError("Ambiente con enti al suo interno \n Cancellazione non effettuata");
        }

        if (getMessageBox().isEmpty()) {
            try {
                ambienteEjb.deleteOrgAmbiente(idAmbiente);
                reloadAmbientiList();
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());

            }
        }
        SessionManager.clearActionHistory(getSession());
        forwardToPublisher(Application.Publisher.AMBIENTE_RICERCA);

    }

    @Override
    public void deleteEntiList() throws EMFError {
        if (!getMessageBox().hasError()) {
            OrgStrutRowBean strutRowBean = new OrgStrutRowBean();

            OrgVRicEnteRowBean enteRowBean = (OrgVRicEnteRowBean) getForm().getEntiList().getTable().getCurrentRow();

            strutRowBean.setIdEnte(enteRowBean.getIdEnte());
            if (struttureEjb.isEnteWithStruts(enteRowBean.getIdEnte())) {
                getMessageBox().addError("Ente con strutture al suo interno \n Cancellazione non effettuata");
            }

            if (getMessageBox().isEmpty()) {
                try {
                    ambienteEjb.deleteOrgEnte(enteRowBean.getIdEnte());

                    OrgEnteRowBean temp = new OrgEnteRowBean();
                    temp.setIdAmbiente(getForm().getVisEnte().getId_ambiente().parse());
                    temp.setNmEnte(getForm().getVisEnte().getNm_ente().parse());
                    reloadEntiList(temp);
                    getMessageBox().addInfo("Cancellazione effettuata con successo");
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            }
            SessionManager.clearActionHistory(getSession());
            forwardToPublisher(Application.Publisher.ENTE_RICERCA);
        }
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (getLastPublisher().equals(Application.Publisher.CREA_AMBIENTE)) {
                try {
                    // raccoglie i dati richiesti nel form
                    // se i dati non sono inseriti, i campi sono null
                    OrgVRicAmbienteTableBean ambienteTableBean = ambienteEjb.getAmbientiAbilitatiPerRicerca(
                            getUser().getIdUtente(), getForm().getVisAmbiente().getNm_ambiente().parse());
                    ambienteTableBean.addSortingRule(OrgAmbienteTableDescriptor.COL_NM_AMBIENTE, SortingRule.ASC);
                    ambienteTableBean.sort();

                    getForm().getAmbientiList().setTable(ambienteTableBean);
                    getForm().getAmbientiList().getTable().setPageSize(10);
                    getForm().getAmbientiList().getTable().first();
                } catch (ParerUserError ex) {
                    getMessageBox().addError(ex.getDescription());
                }
            } else if (getLastPublisher().equals(Application.Publisher.CREA_ENTE)) {
                OrgEnteRowBean ricEnte = new OrgEnteRowBean();
                ricEnte.setNmEnte(getForm().getVisEnte().getNm_ente().parse());
                BigDecimal idAmbiente = getForm().getVisEnte().getId_ambiente().parse();
                if (idAmbiente != null) {
                    ricEnte.setIdAmbiente(idAmbiente);
                }
                reloadEntiList(ricEnte);

                getForm().getEntiList().setStatus(Status.view);
            }
        } catch (EMFError ex) {
            logger.error("Errore inatteso " + ExceptionUtils.getRootCauseMessage(ex), ex);
            getMessageBox().addError("Errore inatteso in fase di caricamento dei dati");
        }
    }

    @Override
    public JSONObject triggerInsEnteNm_enteOnTrigger() throws EMFError {
        getForm().getInsEnte().post(getRequest());
        String nmEnte = getForm().getInsEnte().getNm_ente().parse();
        if (nmEnte != null) {
            String cdEnteNormaliz = Utils.getNormalizedUDCode(nmEnte);
            getForm().getInsEnte().getCd_ente_normaliz().setValue(cdEnteNormaliz);
        }
        return getForm().getInsEnte().asJSON();
    }

    /**
     * Elimina un parametro di amministrazione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriAmministrazioneAmbienteList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriAmministrazioneAmbienteList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAmbiente(idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sull'ambiente non presente: nessuna cancellazione effettuata");
        }
        try {
            loadDettaglioAmbiente(
                    ((OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable().getCurrentRow()).getIdAmbiente());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente");
        }
        forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
    }

    /**
     * Elimina un parametro di conservazione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriConservazioneAmbienteList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriConservazioneAmbienteList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAmbiente(idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sull'ambiente non presente: nessuna cancellazione effettuata");
        }
        try {
            loadDettaglioAmbiente(
                    ((OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable().getCurrentRow()).getIdAmbiente());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente");
        }
        forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
    }

    /**
     * Elimina un parametro di gestione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriGestioneAmbienteList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriGestioneAmbienteList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAmbiente(idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox().addWarning("Valore sull'ambiente non presente: nessuna cancellazione effettuata");
        }
        try {
            loadDettaglioAmbiente(
                    ((OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable().getCurrentRow()).getIdAmbiente());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente");
        }
        forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
    }

    @Override
    public JSONObject triggerInsAmbienteId_ambiente_ente_convenzOnTrigger() throws EMFError {
        getForm().getInsAmbiente().post(getRequest());

        if (getForm().getInsAmbiente().getId_ambiente_ente_convenz().parse() != null) {
            BaseTable enteConvenzTable = ambienteEjb.getEntiGestoreAbilitatiTableBean(
                    new BigDecimal(getUser().getIdUtente()),
                    getForm().getInsAmbiente().getId_ambiente_ente_convenz().parse());
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteConvenzTable, "id_ente_gestore", "nm_ente_gestore");
            getForm().getInsAmbiente().getId_ente_gestore().setDecodeMap(mappaEnte);
        } else {
            DecodeMap map = new DecodeMap();
            getForm().getInsAmbiente().getId_ente_gestore().setDecodeMap(map);
            getForm().getInsAmbiente().getId_ente_conserv().setValue(null);
        }

        return getForm().getInsAmbiente().asJSON();
    }

    @Override
    public JSONObject triggerInsAmbienteId_ente_gestoreOnTrigger() throws EMFError {
        getForm().getInsAmbiente().post(getRequest());

        if (getForm().getInsAmbiente().getId_ente_gestore().parse() != null) {
            BaseTable tabella = ambienteEjb.getEntiConservatori(getUser().getIdUtente(),
                    getForm().getInsAmbiente().getId_ente_gestore().parse());
            getForm().getInsAmbiente().getId_ente_conserv()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tabella, "id_ente_siam", "nm_ente_siam"));
            getForm().getInsAmbiente().getId_ente_conserv().setValue("");
        } else {
            getForm().getInsAmbiente().getId_ente_conserv().setDecodeMap(new DecodeMap());
        }

        return getForm().getInsAmbiente().asJSON();
    }

    /**
     * Elimina un parametro multiplo dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriMultipliAmbienteList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriMultipliAmbienteList().getTable()
                .getCurrentRow();
        BigDecimal idParamApplic = row.getBigDecimal("id_param_applic");
        OrgVRicAmbienteRowBean ambienteRowBean = (OrgVRicAmbienteRowBean) getForm().getAmbientiList().getTable()
                .getCurrentRow();
        BigDecimal idAmbiente = ambienteRowBean.getIdAmbiente();
        if (idParamApplic != null) {
            if (amministrazioneEjb.deleteParametroMultiploAmbiente(idParamApplic, idAmbiente)) {
                getMessageBox().addInfo("Parametro multiplo eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        }

        if (row.getString("ds_valore_param_applic_multi").equals("")) {
            getMessageBox().clear();
            getMessageBox().addWarning("Valore sull'ambiente non presente: nessuna cancellazione effettuata");
        }

        try {
            loadDettaglioAmbiente(idAmbiente);
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dell'ambiente");
        }
        forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
    }

    @Override
    public void parametriMultipliAmbienteButton() throws Throwable {
        parametriAmministrazioneAmbienteButton();
    }

    @Override
    public void parametriAmministrazioneAmbienteButton() throws Throwable {
        BigDecimal idAmbiente = ((BaseRowInterface) getForm().getAmbientiList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente");
        loadListeParametriAmbiente(idAmbiente, null, true, true, true, true, true);
        prepareRicercaParametriAmbiente(AMMINISTRAZIONE);
    }

    @Override
    public void parametriConservazioneAmbienteButton() throws Throwable {
        BigDecimal idAmbiente = ((BaseRowInterface) getForm().getAmbientiList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente");
        loadListeParametriAmbiente(idAmbiente, null, true, false, true, true, false);
        prepareRicercaParametriAmbiente(CONSERVAZIONE);
    }

    @Override
    public void parametriGestioneAmbienteButton() throws Throwable {
        BigDecimal idAmbiente = ((BaseRowInterface) getForm().getAmbientiList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente");
        loadListeParametriAmbiente(idAmbiente, null, true, false, false, true, false);
        prepareRicercaParametriAmbiente(GESTIONE);
    }

    private void prepareRicercaParametriAmbiente(String tipo) {
        getSession().setAttribute("provenienzaParametri", tipo);
        getForm().getInsAmbiente().setStatus(Status.update);
        getForm().getRicercaParametriAmbiente().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriAmbiente().getFunzione().reset();
        getForm().getRicercaParametriAmbiente().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        forwardToPublisher(Application.Publisher.PARAMETRI_AMBIENTE);
    }

    @Override
    public void ricercaParametriAmbienteButton() throws EMFError {
        getForm().getRicercaParametriAmbiente().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriAmbiente().getFunzione().parse();
        BigDecimal idAmbiente = ((BaseRowInterface) getForm().getAmbientiList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente");
        try {
            getForm().getInsAmbiente().setStatus(Status.update);
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                switch (provenzienzaParametri) {
                case AMMINISTRAZIONE:
                    loadListeParametriAmbiente(idAmbiente, funzione, false, true, true, true, true);
                    break;
                case CONSERVAZIONE:
                    loadListeParametriAmbiente(idAmbiente, funzione, false, false, true, true, true);
                    break;
                case GESTIONE:
                    loadListeParametriAmbiente(idAmbiente, funzione, false, false, false, true, true);
                    break;
                default:
                    break;
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri ambiente");
        }
        forwardToPublisher(Application.Publisher.PARAMETRI_AMBIENTE);
    }

    private void salvaParametriAmbiente() throws EMFError {
        getForm().getParametriAmministrazioneAmbienteList().post(getRequest());
        getForm().getParametriConservazioneAmbienteList().post(getRequest());
        getForm().getParametriGestioneAmbienteList().post(getRequest());
        getForm().getParametriMultipliAmbienteList().post(getRequest());

        BigDecimal idAmbiente = ((BaseRowInterface) getForm().getAmbientiList().getTable().getCurrentRow())
                .getBigDecimal("id_ambiente");

        // Controllo valori possibili su ambiente
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) getForm()
                .getParametriAmministrazioneAmbienteList().getTable();
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                .getParametriConservazioneAmbienteList().getTable();
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) getForm()
                .getParametriGestioneAmbienteList().getTable();
        AplParamApplicTableBean parametriMultipli = (AplParamApplicTableBean) getForm()
                .getParametriMultipliAmbienteList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("ambiente", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (!getMessageBox().hasError()) {
            ambienteEjb.saveParametriAmbiente(parametriAmministrazione, parametriConservazione, parametriGestione,
                    parametriMultipli, idAmbiente);
            getMessageBox().addInfo("Parametri ambiente salvati con successo");
            getMessageBox().setViewMode(ViewMode.plain);
            getForm().getInsAmbiente().setViewMode();
            getForm().getInsAmbiente().setStatus(Status.view);
            getForm().getParametriAmministrazioneAmbienteList().setViewMode();
            getForm().getParametriConservazioneAmbienteList().setViewMode();
            getForm().getParametriGestioneAmbienteList().setViewMode();
            try {
                loadDettaglioAmbiente(idAmbiente);
                forwardToPublisher(Application.Publisher.CREA_AMBIENTE);
            } catch (ParerUserError e) {
                getMessageBox().addError(e.getDescription());
            }
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    private AplParamApplicTableBean obfuscatePasswordParamApplic(AplParamApplicTableBean paramApplicTableBean) {
        // MEV25687 - offusca le password
        Iterator<AplParamApplicRowBean> rowIt = paramApplicTableBean.iterator();
        while (rowIt.hasNext()) {
            AplParamApplicRowBean rowBean = rowIt.next();
            if (rowBean.getTiValoreParamApplic().equals(Constants.ComboValueParamentersType.PASSWORD.name())) {
                rowBean.setString("ds_valore_param_applic", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_applic") != null)
                    rowBean.setString("ds_valore_param_applic_applic", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_ambiente_amm") != null)
                    rowBean.setString("ds_valore_param_applic_ambiente_amm", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_ambiente_gest") != null)
                    rowBean.setString("ds_valore_param_applic_ambiente_gest", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_ambiente_cons") != null)
                    rowBean.setString("ds_valore_param_applic_ambiente_cons", Constants.OBFUSCATED_STRING);
            }
        }

        return paramApplicTableBean;
    }

}
