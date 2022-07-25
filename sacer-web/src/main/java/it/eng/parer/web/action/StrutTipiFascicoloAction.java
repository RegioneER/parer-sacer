package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.entity.DecModelloXsdAttribFascicolo;
import it.eng.parer.entity.constraint.DecParteNumeroFascicolo;
import it.eng.parer.entity.constraint.DecParteNumeroFascicolo.TiCharParte;
import it.eng.parer.entity.constraint.DecParteNumeroFascicolo.TiParte;
import static it.eng.parer.entity.constraint.DecParteNumeroFascicolo.TiParte.ANNO;
import static it.eng.parer.entity.constraint.DecParteNumeroFascicolo.TiParte.CLASSIF;
import static it.eng.parer.entity.constraint.DecParteNumeroFascicolo.TiParte.PROGR_FASC;
import static it.eng.parer.entity.constraint.DecParteNumeroFascicolo.TiParte.PROGR_SUB_FASC;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.StrutTipiFascicoloAbstractAction;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm;
import it.eng.parer.slite.gen.form.StrutTipiFascicoloForm;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.DecAaTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecAaTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascTableBean;
import it.eng.parer.slite.gen.tablebean.DecErrAaTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdAttribFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroFascicoloTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascTableDescriptor;
import it.eng.parer.slite.gen.tablebean.OrgEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ActionUtils;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author gilioli_p
 */
public class StrutTipiFascicoloAction extends StrutTipiFascicoloAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(StrutTipiFascicoloAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    private static final String SEPARATORE = "^[A-Za-z0-9]?$";
    private static final String PARAMETER_ID_PARTI_ELIMINATE = "PARAMETER_ID_PARTI_ELIMINATE";
    private static final String PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI = "PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI";
    private static final String PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI = "PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI";
    private static final String PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI = "PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI";
    private static final String PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI = "PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI";
    private static final Pattern sepPattern = Pattern.compile(SEPARATORE);
    private static final String INSIEME_FAS = "^([A-Za-z0-9]+(,[A-Za-z0-9]+)*)?$";
    private static final String RANGE_FAS = "^((\\<(\\d+)\\>-\\<(\\d+)\\>))?$";
    private static final Pattern insiemePattern = Pattern.compile(INSIEME_FAS);
    private static final Pattern rangePattern = Pattern.compile(RANGE_FAS);

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL)) {
            goBackTo(Application.Publisher.TIPO_FASCICOLO_DETAIL);
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.TIPO_FASCICOLO_DETAIL;
    }

    private void initTipoFascicoloDetail() {
        initAaTipoFascicoloDetail();
        // Date
        Calendar c = Calendar.getInstance();
        DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        getForm().getTipoFascicoloDetail().getDt_istituz().setValue(formato.format(c.getTime()));
        c.set(2444, Calendar.DECEMBER, 31, 0, 0, 0);
        getForm().getTipoFascicoloDetail().getDt_soppres().setValue(formato.format(c.getTime()));
    }

    private void initAaTipoFascicoloDetail() {
        setInitialStateSections();
        // Gestione combo flags
        // setInitialAaFlComboLists();
    }

    private void setInitialStateSections() {
        // Hide/show
        getForm().getParametriControlloClassificazioneSection().setHidden(false);
        getForm().getParametriControlloCollegamentiSection().setHidden(false);
        getForm().getParametriControlloNumeroFascSection().setHidden(false);
        // Open/close
        getForm().getParametriControlloClassificazioneSection().setLoadOpened(false);
        getForm().getParametriControlloCollegamentiSection().setLoadOpened(false);
        getForm().getParametriControlloNumeroFascSection().setLoadOpened(false);
    }

    @Override
    public void loadDettaglio() throws EMFError {
        try {
            if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                    || getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)
                    || getNavigationEvent().equals(ListAction.NE_NEXT)
                    || getNavigationEvent().equals(ListAction.NE_PREV)) {
                if (getTableName().equals(getForm().getTipoFascicoloList().getName())) {
                    initTipoFascicoloDetail();
                    BigDecimal idTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList()
                            .getTable()).getCurrentRow().getIdTipoFascicolo();
                    loadDettaglioTipoFascicolo(idTipoFascicolo);

                    String cessato = (String) getRequest().getParameter("cessato");
                    if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                        getForm().getTipoFascicoloList().setUserOperations(true, false, false, false);
                    }
                } else if (getTableName().equals(getForm().getAaTipoFascicoloList().getName())) {
                    initAaTipoFascicoloDetail();
                    BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList()
                            .getTable()).getCurrentRow().getIdAaTipoFascicolo();
                    loadDettaglioAaTipoFascicolo(idAaTipoFascicolo);
                } else if (getTableName().equals(getForm().getMetadatiProfiloFascicoloList().getName())) {
                    if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
                        // "Carico" i dati dalla lista ai campi di dettaglio in quanto sto eseguendo una modifica di un
                        // record all'interno del wizard di un altro record! (periodo tipo fascicolo)
                        getForm().getMetadatiProfiloDetail().copyFromBean(
                                ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloFascicoloList().getTable())
                                        .getCurrentRow());
                    } else {
                        getForm().getMetadatiProfiloFascicoloList().setStatus(Status.view);
                        BigDecimal idUsoModelloXsdFasc = ((DecUsoModelloXsdFascTableBean) getForm()
                                .getMetadatiProfiloFascicoloList().getTable()).getCurrentRow().getIdUsoModelloXsdFasc();
                        loadDettaglioXsdAaTipoFascicolo(idUsoModelloXsdFasc);
                    }
                } else if (getTableName().equals(getForm().getMetadatiProfiloArkList().getName())) {
                    if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
                        // "Carico" i dati dalla lista ai campi di dettaglio in quanto sto eseguendo una modifica di un
                        // record all'interno del wizard di un altro record! (periodo tipo fascicolo)
                        getForm().getMetadatiProfiloDetail().copyFromBean(
                                ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloArkList().getTable())
                                        .getCurrentRow());
                    } else {
                        getForm().getMetadatiProfiloArkList().setStatus(Status.view);
                        BigDecimal idUsoModelloXsdFasc = ((DecUsoModelloXsdFascTableBean) getForm()
                                .getMetadatiProfiloArkList().getTable()).getCurrentRow().getIdUsoModelloXsdFasc();
                        loadDettaglioXsdAaTipoFascicolo(idUsoModelloXsdFasc);
                    }
                } else if (getTableName().equals(getForm().getAttribFascicoloList().getName())) {
                    if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)) {

                        BigDecimal idAttribFascicolo = ((DecAttribFascicoloRowBean) getForm().getAttribFascicoloList()
                                .getTable().getCurrentRow()).getIdAttribFascicolo();

                        loadDettaglioAttribFascicolo(idAttribFascicolo);
                    } else {
                        getForm().getAttribFascicoloList().setStatus(Status.view);
                        BigDecimal idAttribFascicolo = ((DecAttribFascicoloTableBean) getForm().getAttribFascicoloList()
                                .getTable()).getCurrentRow().getIdAttribFascicolo();

                        loadDettaglioAttribFascicolo(idAttribFascicolo);

                    }
                }
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getMessage());
        }
    }

    private void loadDettaglioTipoFascicolo(BigDecimal idTipoFascicolo) {
        try {
            // Caricamento dettaglio tipo fascicolo e parametri ultimo periodo validità
            DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb.getDecTipoFascicoloRowBean(idTipoFascicolo);
            DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                    .getLastDecAaTipoFascicoloRowBean(idTipoFascicolo);
            getForm().getTipoFascicoloDetail().copyFromBean(tipoFascicoloRowBean);
            getForm().getAaTipoFascicoloDetail().copyFromBean(aaTipoFascicoloRowBean);
            // Edit mode dei pulsanti
            getForm().getTipoFascicoloDetail().getLogEventiTipoFascicolo().setEditMode();

            // Carico le liste del dettaglio tipo fascicolo
            loadDettaglioTipoFascicoloLists(idTipoFascicolo);

        } catch (EMFError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void loadDettaglioAaTipoFascicolo(BigDecimal idAaTipoFascicolo) throws ParerUserError {
        try {
            // Caricamento dettaglio periodo validità tipo fascicolo
            DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                    .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
            getForm().getAaTipoFascicoloDetail().copyFromBean(aaTipoFascicoloRowBean);

            // Carico le liste del dettaglio periodo validità tipo fascicolo
            loadDettaglioAaTipoFascicoloLists(idAaTipoFascicolo);

            // Parametri
            DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                    .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
            OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
            OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());

            // Parametri
            disabileAaTipoFascicoloParametersSections(false);

            loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                    idAaTipoFascicolo, null, false, false, false, false);

            if ("1".equals(strutRowBean.getFlCessato())) {
                getForm().getParametriAaTipoFascButtonList().getParametriAmministrazioneAaTipoFascButton()
                        .setViewMode();
                getForm().getParametriAaTipoFascButtonList().getParametriConservazioneAaTipoFascButton().setViewMode();
                getForm().getParametriAaTipoFascButtonList().getParametriGestioneAaTipoFascButton().setViewMode();
            } else {
                getForm().getParametriAaTipoFascButtonList().getParametriAmministrazioneAaTipoFascButton()
                        .setEditMode();
                getForm().getParametriAaTipoFascButtonList().getParametriConservazioneAaTipoFascButton().setEditMode();
                getForm().getParametriAaTipoFascButtonList().getParametriGestioneAaTipoFascButton().setEditMode();
            }

            // // Section parametri chiuse
            // getForm().getParametriAmministrazioneSection().setLoadOpened(false);
            // getForm().getParametriConservazioneSection().setLoadOpened(false);
            // getForm().getParametriGestioneSection().setLoadOpened(false);

        } catch (EMFError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    private void disabileAaTipoFascicoloParametersSections(boolean disable) {
        getForm().getParametriAmministrazioneSection().setHidden(disable);
        getForm().getParametriConservazioneSection().setHidden(disable);
        getForm().getParametriGestioneSection().setHidden(disable);
    }

    private void loadListeParametriPeriodoTipoFascicolo(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean hideDeleteButtons,
            boolean editModeAmministrazione, boolean editModeConservazione, boolean editModeGestione)
            throws ParerUserError {
        Object[] parametriObj = amministrazioneEjb.getAplParamApplicAaTipoFasc(idAmbiente, idStrut, idAaTipoFascicolo,
                funzione);

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

        // getForm().getParametriAmministrazioneSection().setLoadOpened(true);
        // getForm().getParametriConservazioneSection().setLoadOpened(true);
        // getForm().getParametriGestioneSection().setLoadOpened(true);
        getForm().getParametriAmministrazioneAaTipoFascList()
                .setTable((AplParamApplicTableBean) parametriAmministrazione);
        getForm().getParametriAmministrazioneAaTipoFascList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneAaTipoFascList().getTable().first();
        getForm().getParametriGestioneAaTipoFascList().setTable((AplParamApplicTableBean) parametriGestione);
        getForm().getParametriGestioneAaTipoFascList().getTable().setPageSize(300);
        getForm().getParametriGestioneAaTipoFascList().getTable().first();
        getForm().getParametriConservazioneAaTipoFascList().setTable((AplParamApplicTableBean) parametriConservazione);
        getForm().getParametriConservazioneAaTipoFascList().getTable().setPageSize(300);
        getForm().getParametriConservazioneAaTipoFascList().getTable().first();
        getForm().getParametriAmministrazioneAaTipoFascList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriGestioneAaTipoFascList().setHideDeleteButton(hideDeleteButtons);
        getForm().getParametriConservazioneAaTipoFascList().setHideDeleteButton(hideDeleteButtons);
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm()
                    .setEditMode();
        } else {
            getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm()
                    .setViewMode();
        }

        if (editModeConservazione) {
            getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                    .setEditMode();
        } else {
            getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                    .setViewMode();
        }

        if (editModeGestione) {
            getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest()
                    .setEditMode();
        } else {
            getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest()
                    .setViewMode();
        }
    }

    // private void setInitialAaFlComboLists() {
    // getForm().getAaTipoFascicoloDetail().getFl_abilita_contr_classif().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    // getForm().getAaTipoFascicoloDetail().getFl_accetta_contr_classif_neg().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    // getForm().getAaTipoFascicoloDetail().getFl_forza_contr_classif().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    //
    // getForm().getAaTipoFascicoloDetail().getFl_abilita_contr_numero().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    // getForm().getAaTipoFascicoloDetail().getFl_accetta_contr_numero_neg().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    // getForm().getAaTipoFascicoloDetail().getFl_forza_contr_numero().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    //
    // getForm().getAaTipoFascicoloDetail().getFl_abilita_contr_colleg().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    // getForm().getAaTipoFascicoloDetail().getFl_accetta_contr_colleg_neg().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    // getForm().getAaTipoFascicoloDetail().getFl_forza_contr_colleg().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
    //
    // }
    private void loadDettaglioXsdAaTipoFascicolo(BigDecimal idUsoModelloXsdFasc) throws EMFError {
        try {
            // Caricamento dettaglio xsd periodo validità tipo fascicolo
            DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = tipoFascicoloEjb
                    .getVersioneXsdMetadati(idUsoModelloXsdFasc);
            getForm().getMetadatiProfiloDetail().copyFromBean(usoModelloXsdFascRowBean);

            loadDettaglioXsdAaTipoFascicoloList(usoModelloXsdFascRowBean);

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    /**
     * 
     * @param usoModelloXsdFascRowBean
     * 
     * @throws EMFError
     */
    private void loadDettaglioXsdAaTipoFascicoloList(DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean)
            throws EMFError {

        BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdAaTipoFascicolo();
        DecAttribFascicoloTableBean attribFascicoloTableBean = tipoFascicoloEjb.getDecAttribFascicoloTableBeanFromXsd(
                usoModelloXsdFascRowBean.getIdModelloXsdFascicolo(), idAaTipoFascicolo);
        getForm().getAttribFascicoloList().setTable(attribFascicoloTableBean);
        getForm().getAttribFascicoloList().getTable().first();

        for (DecAttribFascicoloRowBean row : attribFascicoloTableBean) {
            DecModelloXsdAttribFascicolo decModelloXsdAttribFascicolo = tipoFascicoloEjb
                    .getDecModelloXsdAttribFascicolo(row, usoModelloXsdFascRowBean);
            getForm().getAttribFascicoloList().getTable().getCurrentRow().setBigDecimal("ni_ord_attrib",
                    decModelloXsdAttribFascicolo.getNiOrdAttrib());
            getForm().getAttribFascicoloList().getTable().getCurrentRow().setBigDecimal("id_modello_xsd_fascicolo",
                    BigDecimal.valueOf(
                            decModelloXsdAttribFascicolo.getDecModelloXsdFascicolo().getIdModelloXsdFascicolo()));
            getForm().getAttribFascicoloList().getTable().next();
        }

        getForm().getAttribFascicoloList().getTable().addSortingRule("ni_ord_attrib", SortingRule.ASC);
        getForm().getAttribFascicoloList().getTable().sort();
        getForm().getAttribFascicoloList().getTable().first();
        getForm().getAttribFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        getForm().getAttribFascicoloList().setUserOperations(true, false, false, false);

        getForm().getAttribFascicoloList().setStatus(Status.view);

    }

    private void loadDettaglioXsdAaTipoFascicolo(BigDecimal idAaTipoFascicolo, BigDecimal idModelloXsdFascicolo)
            throws EMFError {
        try {
            // Caricamento dettaglio xsd periodo validità tipo fascicolo
            DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = tipoFascicoloEjb
                    .getVersioneXsdMetadati(idAaTipoFascicolo, idModelloXsdFascicolo);
            getForm().getMetadatiProfiloDetail().copyFromBean(usoModelloXsdFascRowBean);
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    /**
     * 
     * @param idAttribFascicolo
     * @param idModelloXsdFascicolo
     * 
     * @throws EMFError
     */
    private void loadDettaglioAttribFascicolo(BigDecimal idAttribFascicolo) throws EMFError {

        getForm().getAttribFascicolo().setViewMode();
        getForm().getAttribFascicoloList().setStatus(Status.view);

        DecAttribFascicoloRowBean attribFascicoloRowBean = tipoFascicoloEjb.getDecAttribFascRowBean(idAttribFascicolo);
        DecModelloXsdAttribFascicoloRowBean modelloXsdAttribFascicoloRowBean = tipoFascicoloEjb
                .getDecModelloXsdAttribFascicoloRowBeanByAttrib(idAttribFascicolo);

        DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = tipoFascicoloEjb
                .getDecModelloXsdFascicoloRowBean(modelloXsdAttribFascicoloRowBean.getIdModelloXsdFascicolo());

        getForm().getAttribFascicolo().copyFromBean(attribFascicoloRowBean);
        getForm().getAttribFascicolo().getNi_ord_attrib()
                .setValue(modelloXsdAttribFascicoloRowBean.getNiOrdAttrib().toString());

        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        Date dtIstituz = modelloXsdFascicoloRowBean.getDtIstituz();
        if (dtIstituz != null) {
            getForm().getAttribFascicolo().getDt_istituz().setValue(df.format(dtIstituz));
        }

        Date dtSoppres = modelloXsdFascicoloRowBean.getDtSoppres();
        if (dtSoppres != null) {
            getForm().getAttribFascicolo().getDt_soppres().setValue(df.format(dtSoppres));
        }

    }

    private void loadDettaglioTipoFascicoloLists(BigDecimal idTipoFascicolo) {
        // Periodi di validità del tipo fascicolo
        DecAaTipoFascicoloTableBean aaTipoFascicoloTableBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloTableBean(idTipoFascicolo);
        getForm().getAaTipoFascicoloList().setTable(aaTipoFascicoloTableBean);
        getForm().getAaTipoFascicoloList().getTable().first();
        getForm().getAaTipoFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        // Criteri di raggruppamento fascicoli del tipo fascicolo
        DecCriterioRaggrFascTableBean criterioRaggrFascTableBean = tipoFascicoloEjb
                .getDecCriterioRaggrFascTableBean(idTipoFascicolo);
        getForm().getCriteriRaggrFascicoloList().setTable(criterioRaggrFascTableBean);
        getForm().getCriteriRaggrFascicoloList().getTable().first();
        getForm().getCriteriRaggrFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        String cessato = (String) getRequest().getParameter("cessato");
        if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
            getForm().getAaTipoFascicoloList().setUserOperations(true, false, false, false);
            getForm().getCriteriRaggrFascicoloList().setUserOperations(true, false, false, false);
        }
    }

    private void loadDettaglioAaTipoFascicoloLists(BigDecimal idAaTipoFascicolo) {
        // Errori sul periodo di validità del tipo fascicolo
        DecErrAaTipoFascicoloTableBean errAaTipoFascicoloTableBean = tipoFascicoloEjb
                .getDecErrAaTipoFascicoloTableBeanPerIntervallo(idAaTipoFascicolo);
        getForm().getErrAaTipoFascicoloList().setTable(errAaTipoFascicoloTableBean);
        getForm().getErrAaTipoFascicoloList().getTable().first();
        getForm().getErrAaTipoFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Versioni XSD dei metadati di profilo fascicolo definiti sul periodo di validità
        DecUsoModelloXsdFascTableBean usoModelloXsdFascTableBean = tipoFascicoloEjb
                .getVersioniXsdMetadati(idAaTipoFascicolo, CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO);
        getForm().getMetadatiProfiloFascicoloList().setTable(usoModelloXsdFascTableBean);
        getForm().getMetadatiProfiloFascicoloList().getTable().first();
        getForm().getMetadatiProfiloFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Versioni XSD dei metadati di profilo archivistico definiti sul periodo di validità
        DecUsoModelloXsdFascTableBean usoModelloXsdArkTableBean = tipoFascicoloEjb
                .getVersioniXsdMetadati(idAaTipoFascicolo, CostantiDB.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO);
        getForm().getMetadatiProfiloArkList().setTable(usoModelloXsdArkTableBean);
        getForm().getMetadatiProfiloArkList().getTable().first();
        getForm().getMetadatiProfiloArkList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Parti
        DecParteNumeroFascicoloTableBean parteNumeroFascicoloTableBean = tipoFascicoloEjb
                .getDecParteNumeroFascicoloTableBean(idAaTipoFascicolo);
        getForm().getParteNumeroFascicoloList().setTable(parteNumeroFascicoloTableBean);
        getForm().getParteNumeroFascicoloList().getTable().first();
        getForm().getParteNumeroFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(getForm().getTipoFascicoloList().getName())) {
                setTipoFascicoloStatus(Status.view);
                forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
            } else if (getTableName().equals(getForm().getAaTipoFascicoloList().getName())) {
                setAaTipoFascicoloStatus(Status.view);
                forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
            } /* Dettaglio CRITERIO_RAGGR_FASC */ else if (getTableName()
                    .equals(getForm().getCriteriRaggrFascicoloList().getName())) {
                /* Preparo la LISTA CRITERI DI RAGGRUPPAMENTO FASCICOLI */
                CriteriRaggrFascicoliForm form = new CriteriRaggrFascicoliForm();
                form.getCriterioRaggrFascicoliList().setTable(getForm().getCriteriRaggrFascicoloList().getTable());
                redirectToAction(Application.Actions.CRITERI_RAGGR_FASCICOLI,
                        "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.NAME + "&riga="
                                + getForm().getCriteriRaggrFascicoloList().getTable().getCurrentRowIndex(),
                        form);
            } else if (getTableName().equals(getForm().getMetadatiProfiloFascicoloList().getName())
                    || getTableName().equals(getForm().getMetadatiProfiloArkList().getName())) {

                getForm().getMetadatiProfiloDetail().setStatus(Status.view);
                getForm().getMetadatiProfiloDetail().setViewMode();
                forwardToPublisher(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL);
            } else if (getTableName().equals(getForm().getAttribFascicoloList().getName())) {
                forwardToPublisher(Application.Publisher.ATTRIB_FASCICOLO_DETAIL);
            }
        }
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {

        try {
            // Se devo ricaricare dopo una goBack, ricarico il dettaglio in view mode...
            if (publisherName.equals(Application.Publisher.TIPO_FASCICOLO_DETAIL)) {
                BaseRowInterface currentRow = getForm().getTipoFascicoloList().getTable().getCurrentRow();
                BigDecimal idTipoFascicolo = currentRow.getBigDecimal("id_tipo_fascicolo");
                if (idTipoFascicolo != null) {
                    loadDettaglioTipoFascicolo(idTipoFascicolo);
                }
                setTipoFascicoloStatus(Status.view);
            } else if (publisherName.equals(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL)) {
                BaseRowInterface currentRow = getForm().getAaTipoFascicoloList().getTable().getCurrentRow();
                BigDecimal idAaTipoFascicolo = currentRow.getBigDecimal("id_aa_tipo_fascicolo");
                if (idAaTipoFascicolo != null) {
                    loadDettaglioAaTipoFascicolo(idAaTipoFascicolo);
                }
                setAaTipoFascicoloStatus(Status.view);
            } else if (publisherName.equals(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL)) {
                BaseRowInterface currentRowAttribFascicolo = getForm().getAttribFascicoloList().getTable()
                        .getCurrentRow();
                BigDecimal idAaTipoFascicolo = currentRowAttribFascicolo.getBigDecimal("id_aa_tipo_fascicolo");
                BigDecimal idModelloXsdFascicolo = currentRowAttribFascicolo.getBigDecimal("id_modello_xsd_fascicolo");
                DecUsoModelloXsdFascRowBean modelloXsdAttribFascicoloRowBean = tipoFascicoloEjb
                        .getDecUsoModelloXsdFascRowBeanByAttrib(idAaTipoFascicolo, idModelloXsdFascicolo);

                loadDettaglioXsdAaTipoFascicolo(modelloXsdAttribFascicoloRowBean.getIdUsoModelloXsdFasc());
            } else if (publisherName.equals(Application.Publisher.ATTRIB_FASCICOLO_DETAIL)) {
                BaseRowInterface currentRow = getForm().getAttribFascicoloList().getTable().getCurrentRow();
                BigDecimal idAttribFascicolo = currentRow.getBigDecimal("id_attrib_fascicolo");
                if (idAttribFascicolo != null) {
                    loadDettaglioAttribFascicolo(idAttribFascicolo);
                }
            }
            postLoad();
        } catch (Exception e) {
            logger.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
            forwardToPublisher(getLastPublisher());
        }
    }

    private void setTipoFascicoloStatus(Status status) {
        switch (status) {
        case insert:
        case update:
            getForm().getTipoFascicoloDetail().setEditMode();
            getForm().getTipoFascicoloDetail().getLogEventiTipoFascicolo().setViewMode();
            break;
        case view:
        default:
            getForm().getTipoFascicoloDetail().setViewMode();
            getForm().getTipoFascicoloDetail().getLogEventiTipoFascicolo().setEditMode();
            break;
        }
        getForm().getTipoFascicoloDetail().setStatus(status);
        getForm().getTipoFascicoloList().setStatus(status);
    }

    private void setAaTipoFascicoloStatus(Status status) {
        switch (status) {
        case insert:
        case update:
            getForm().getAaTipoFascicoloDetail().setEditMode();
            break;
        case view:
        default:
            getForm().getAaTipoFascicoloDetail().setViewMode();
            break;
        }
        getForm().getAaTipoFascicoloDetail().setStatus(status);
        getForm().getAaTipoFascicoloList().setStatus(status);
    }

    @Override
    public void undoDettaglio() throws EMFError {
        // Se devo annullare un update, rimango nella pagina attuale e ricarico il dettaglio in view mode...
        if (getLastPublisher().equals(Application.Publisher.TIPO_FASCICOLO_DETAIL)
                && getForm().getTipoFascicoloList().getStatus() != null
                && getForm().getTipoFascicoloList().getStatus().equals(Status.update)) {
            BaseRowInterface currentRow = getForm().getTipoFascicoloList().getTable().getCurrentRow();
            BigDecimal idTipoFascicolo = currentRow.getBigDecimal("id_tipo_fascicolo");
            if (idTipoFascicolo != null) {
                loadDettaglioTipoFascicolo(idTipoFascicolo);
            }
            setTipoFascicoloStatus(Status.view);
            forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
        } else if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)
                && getForm().getAaTipoFascicoloDetail().getStatus() != null
                && getForm().getAaTipoFascicoloDetail().getStatus().toString().equals("update")) {
            getForm().getAaTipoFascicoloDetail().setStatus(Status.view);
            getForm().getParametriAmministrazioneAaTipoFascList().setViewMode();
            getForm().getParametriConservazioneAaTipoFascList().setViewMode();
            getForm().getParametriGestioneAaTipoFascList().setViewMode();
            BigDecimal idAaTipoFascicolo = getForm().getAaTipoFascicoloDetail().getId_aa_tipo_fascicolo().parse();
            try {
                loadDettaglioAaTipoFascicolo(idAaTipoFascicolo);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
            goBack();
        } // ... altrimenti torno indietro
        else {
            goBack();
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        // try {
        if (getTableName().equals(getForm().getTipoFascicoloList().getName())) {
            getForm().getTipoFascicoloDetail().clear();
            initTipoFascicoloDetail();
            // Insert mode per lista e dettaglio
            getForm().getTipoFascicoloDetail().setEditMode();
            getForm().getTipoFascicoloDetail().setStatus(Status.insert);
            getForm().getTipoFascicoloList().setStatus(Status.insert);
            // Nascondo le sezioni con i parametri flag
            getForm().getParametriControlloClassificazioneSection().setHidden(true);
            getForm().getParametriControlloCollegamentiSection().setHidden(true);
            getForm().getParametriControlloNumeroFascSection().setHidden(true);
            // Nascondo il parametro Controllo formato numero
            getForm().getTipoFascicoloDetail().getControllo_formato_numero().setHidden(true);
            forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
        } else if (getTableName().equals(getForm().getAaTipoFascicoloList().getName())) {
            getForm().getAaTipoFascicoloList().setStatus(Status.insert);
            getForm().getAaTipoFascicoloDetail().setStatus(Status.insert);

            // Azzero attributi in sessione, pulisco i campi e li metto in edit mode
            getSession().removeAttribute(PARAMETER_ID_PARTI_ELIMINATE);
            getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI);
            getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI);
            getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI);
            getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI);
            getForm().getAaTipoFascicoloDetail().reset();
            getForm().getAaTipoFascicoloDetail().setEditMode();

            // init combo
            // setInitialAaFlComboLists();
            // Azzero le liste
            getForm().getErrAaTipoFascicoloList().setTable(new DecErrAaTipoFascicoloTableBean());
            getForm().getParteNumeroFascicoloList().setTable(new DecParteNumeroFascicoloTableBean());
            getForm().getMetadatiProfiloFascicoloList().setTable(new DecUsoModelloXsdFascTableBean());
            getForm().getMetadatiProfiloArkList().setTable(new DecUsoModelloXsdFascTableBean());

            // Apro le section
            getForm().getParametriControlloClassificazioneSection().setLoadOpened(true);
            getForm().getParametriControlloCollegamentiSection().setLoadOpened(true);
            getForm().getParametriControlloNumeroFascSection().setLoadOpened(true);

            // loadListeParametriPeriodoTipoFascicolo(null, null, null, true, true);
            disabileAaTipoFascicoloParametersSections(true);

            getForm().getInserimentoPeriodoValiditaWizard().reset();

            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_WIZARD);
        }
        // } catch (ParerUserError ex) {
        // getMessageBox().addError(ex.getDescription());
        // }
    }

    @Override
    public void updateTipoFascicoloList() throws EMFError {

        String nmTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList().getTable())
                .getCurrentRow().getNmTipoFascicolo();
        if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
            getMessageBox().addError("Attenzione: il tipo fascicolo sconosciuto non può essere modificato");
        }

        if (!getMessageBox().hasError()) {
            getForm().getTipoFascicoloList().setStatus(Status.update);
            getForm().getTipoFascicoloDetail().setStatus(Status.update);
            getForm().getTipoFascicoloDetail().setViewMode();
            BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();
            // Controllo utilizzo del fascicolo per rendere editabili tutti o solo alcuni campi
            if (tipoFascicoloEjb.existsFascicoloVersatoPerTipoFascicolo(idTipoFascicolo)) {
                getForm().getTipoFascicoloDetail().getDs_tipo_fascicolo().setEditMode();
                getForm().getTipoFascicoloDetail().getDt_soppres().setEditMode();
            } else {
                getForm().getTipoFascicoloDetail().setEditMode();
            }
            forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
        }
    }

    @Override
    public void updateAaTipoFascicoloList() throws EMFError {
        getForm().getAaTipoFascicoloList().setStatus(Status.update);
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        //
        getForm().getInserimentoPeriodoValiditaWizard().reset();
        // getForm().getAaTipoFascicoloDetail().reset();
        // BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean)
        // getForm().getAaTipoFascicoloList().getTable()).getCurrentRow().getIdAaTipoFascicolo();
        // DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean =
        // tipoFascicoloEjb.getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
        getSession().removeAttribute(PARAMETER_ID_PARTI_ELIMINATE);
        getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI);
        getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI);
        getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI);
        getSession().removeAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI);
        // Recupero i dati di dettaglio del periodo tipo fascicolo
        getForm().getAaTipoFascicoloDetail().setEditMode();
        // getForm().getAaTipoFascicoloDetail().copyFromBean(aaTipoFascicoloRowBean);
        // Recupero le liste dei metadati e delle parti
        // DecUsoModelloXsdFascTableBean metadatiFascicoloTableBean =
        // tipoFascicoloEjb.getVersioniXsdMetadati(idAaTipoFascicolo,
        // CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO);
        // DecUsoModelloXsdFascTableBean metadatiArchivisticoTableBean =
        // tipoFascicoloEjb.getVersioniXsdMetadati(idAaTipoFascicolo,
        // CostantiDB.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO);
        // DecParteNumeroFascicoloTableBean parti =
        // tipoFascicoloEjb.getDecParteNumeroFascicoloTableBean(idAaTipoFascicolo);
        // getForm().getMetadatiProfiloFascicoloList().setTable(metadatiFascicoloTableBean);
        // getForm().getMetadatiProfiloArkList().setTable(metadatiArchivisticoTableBean);
        // getForm().getParteNumeroFascicoloList().setTable(parti);

        // getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm().setEditMode();
        // getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons().setEditMode();
        // getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest().setEditMode();
        // getForm().getParametriAmministrazioneAaTipoFascList().setHideDeleteButton(true);
        // getForm().getParametriConservazioneAaTipoFascList().setHideDeleteButton(true);
        // getForm().getParametriGestioneAaTipoFascList().setHideDeleteButton(true);
        // Apro le section
        getForm().getParametriControlloClassificazioneSection().setLoadOpened(true);
        getForm().getParametriControlloCollegamentiSection().setLoadOpened(true);
        getForm().getParametriControlloNumeroFascSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_WIZARD);
    }

    @Override
    public void updateCriteriRaggrFascicoloList() throws EMFError {
        /* Preparo la LISTA CRITERI DI RAGGRUPPAMENTO FASCICOLI */
        CriteriRaggrFascicoliForm form = new CriteriRaggrFascicoliForm();
        form.getCriterioRaggrFascicoliList().setTable(getForm().getCriteriRaggrFascicoloList().getTable());
        redirectToAction(Application.Actions.CRITERI_RAGGR_FASCICOLI,
                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_UPDATE + "&table="
                        + CriteriRaggrFascicoliForm.CriterioRaggrFascicoliList.NAME + "&riga="
                        + getForm().getCriteriRaggrFascicoloList().getTable().getCurrentRowIndex(),
                form);
    }

    @Override
    public void saveDettaglio() throws EMFError {
        if (getTableName().equals(getForm().getTipoFascicoloList().getName())
                || getTableName().equals(getForm().getTipoFascicoloDetail().getName())) {
            saveTipoFascicolo();
        } else if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)) {
            salvaParametriAaTipoFasc();
        }
    }

    private void saveTipoFascicolo() throws EMFError {
        getForm().getParametriAmministrazioneAaTipoFascList().post(getRequest());
        getForm().getParametriConservazioneAaTipoFascList().post(getRequest());
        getForm().getParametriGestioneAaTipoFascList().post(getRequest());

        if (getForm().getTipoFascicoloDetail().postAndValidate(getRequest(), getMessageBox())) {
            try {
                BigDecimal idStrut = getForm().getStrutRif().getId_strut().parse();
                String nmTipoFascicolo = getForm().getTipoFascicoloDetail().getNm_tipo_fascicolo().parse();
                String dsTipoFascicolo = getForm().getTipoFascicoloDetail().getDs_tipo_fascicolo().parse();
                Date dtIstituz = getForm().getTipoFascicoloDetail().getDt_istituz().parse();
                Date dtSoppres = getForm().getTipoFascicoloDetail().getDt_soppres().parse();

                // Controlli sulle date
                if (dtSoppres.before(dtIstituz)) {
                    getMessageBox().addError(
                            "La data di disattivazione non può essere inferiore alla data di attivazione del tipo fascicolo");
                }
                if (getForm().getTipoFascicoloList().getStatus().equals(Status.insert)) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    if (dtIstituz.before(c.getTime())) {
                        getMessageBox().addError(
                                "La data di attivazione non può essere antecedente alla data di inserimento del tipo fascicolo nel Sistema");
                    }
                }

                if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
                    getMessageBox().addError(
                            "Attenzione: non è possibile inserire un fascicolo di nome 'Tipo fascicolo sconosciuto'");
                }

                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (getForm().getTipoFascicoloList().getStatus().equals(Status.insert)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
                        Long idTipoFascicolo = tipoFascicoloEjb.insertTipoFascicolo(param, idStrut, nmTipoFascicolo,
                                dsTipoFascicolo, dtIstituz, dtSoppres);
                        if (idTipoFascicolo != null) {
                            getForm().getTipoFascicoloDetail().getId_tipo_fascicolo()
                                    .setValue(idTipoFascicolo.toString());
                        }
                        // Aggiungo in fondo alla lista il tipo fascicolo appena creato
                        DecTipoFascicoloRowBean row = new DecTipoFascicoloRowBean();
                        getForm().getTipoFascicoloDetail().copyToBean(row);
                        getForm().getTipoFascicoloList().getTable().last();
                        getForm().getTipoFascicoloList().getTable().add(row);
                    } else if (getForm().getTipoFascicoloList().getStatus().equals(Status.update)) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
                        BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();
                        tipoFascicoloEjb.updateTipoFascicolo(param, idStrut, idTipoFascicolo, nmTipoFascicolo,
                                dsTipoFascicolo, dtIstituz, dtSoppres);
                    }
                    BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();

                    param.setNomeAzione("automa/creaCriterioRaggrFascicolo");
                    Long idCriterioRaggrFasc = tipoFascicoloEjb.saveCriterioRaggrFascStandard(param, idStrut,
                            nmTipoFascicolo, idTipoFascicolo);
                    if (idCriterioRaggrFasc != null) {
                        getMessageBox()
                                .addInfo("Eseguita la creazione automatica del criterio di raggruppamento standard");
                    }

                    if (idTipoFascicolo != null) {
                        loadDettaglioTipoFascicolo(idTipoFascicolo);
                    }
                    getForm().getTipoFascicoloDetail().setViewMode();
                    getForm().getTipoFascicoloDetail().getLogEventiTipoFascicolo().setEditMode();
                    getForm().getTipoFascicoloList().setStatus(Status.view);
                    getForm().getTipoFascicoloDetail().setStatus(Status.view);
                    getMessageBox().addInfo("Tipo fascicolo salvato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                }
            } catch (ParerWarningException ex) {
                getMessageBox().addWarning(ex.getDescription());
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        postLoad();
        forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
    }

    private boolean inValoriPossibili(String dsValoreParamApplicEnte, String dsListaValoriAmmessi) {
        String[] tokens = dsListaValoriAmmessi.split("\\|");
        Set<String> mySet = new HashSet<String>(Arrays.asList(tokens));
        return mySet.contains(dsValoreParamApplicEnte);
    }

    @Override
    public void deleteTipoFascicoloList() throws EMFError {
        BaseRowInterface currentRow = getForm().getTipoFascicoloList().getTable().getCurrentRow();
        BigDecimal idTipoFascicolo = currentRow.getBigDecimal("id_tipo_fascicolo");
        int riga = getForm().getTipoFascicoloList().getTable().getCurrentRowIndex();
        // Di bonniana memoria, della serie "la prudenza non è mai troppa"...
        // "eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono nel dettaglio"
        if (getLastPublisher().equals(Application.Publisher.TIPO_FASCICOLO_DETAIL)) {
            if (!idTipoFascicolo.equals(getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione del tipo fascicolo");
            }
        }

        String nmTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList().getTable())
                .getCurrentRow().getNmTipoFascicolo();
        if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
            getMessageBox().addError("Attenzione: il tipo fascicolo sconosciuto non può essere eliminato");
        }

        if (!getMessageBox().hasError() && idTipoFascicolo != null) {
            try {
                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (Application.Publisher.TIPO_FASCICOLO_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    } else {
                        StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                        param.setNomeAzione(
                                SpagoliteLogUtil.getDetailActionNameDelete(form, form.getTipoFascicoloList()));
                    }
                    // Elimino il record da DB e dalla lista online
                    tipoFascicoloEjb.deleteDecTipoFascicolo(param, idTipoFascicolo);

                    getForm().getTipoFascicoloList().getTable().remove(riga);
                    getMessageBox().addInfo("Tipo fascicolo eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);

                    // Gestione del ritorno dovuta alla provenienza da altra action
                    if (Application.Publisher.TIPO_FASCICOLO_DETAIL.equals(getLastPublisher())
                            || "".equals(getLastPublisher())) {
                        goBack();
                    }
                }
            } catch (ParerUserError ex) {
                // In caso di errore: non se mi trovo in dettaglio tipo fascicolo (sarà in dettaglio struttura),
                // fai goBack per tornare in dettaglio struttura dal momento che mi trovo qui per passaggio di action
                getMessageBox().addError(ex.getDescription());
                if (!getLastPublisher().equals(Application.Publisher.TIPO_FASCICOLO_DETAIL)) {
                    goBack();
                } // ... altrimenti, significa che sono in dettaglio tipo fascicolo e vi resti
                else {
                    forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
                }
            }
        }
    }

    @Override
    public void deleteAaTipoFascicoloList() throws EMFError {
        BaseRowInterface currentRow = getForm().getAaTipoFascicoloList().getTable().getCurrentRow();
        BigDecimal idAaTipoFascicolo = currentRow.getBigDecimal("id_aa_tipo_fascicolo");
        int riga = getForm().getAaTipoFascicoloList().getTable().getCurrentRowIndex();
        // Di bonniana memoria, della serie "la prudenza non è mai troppa"...
        // "eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono nel dettaglio"
        if (getLastPublisher().equals(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL)) {
            if (!idAaTipoFascicolo.equals(getForm().getAaTipoFascicoloDetail().getId_aa_tipo_fascicolo().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione del periodo tipo fascicolo");
            }
        }

        if (!getMessageBox().hasError() && idAaTipoFascicolo != null) {
            try {
                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (Application.Publisher.TIPO_FASCICOLO_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                        StrutTipiFascicoloForm form = (StrutTipiFascicoloForm) SpagoliteLogUtil.getForm(this);
                        param.setNomeAzione(
                                SpagoliteLogUtil.getDetailActionNameDelete(form, form.getAaTipoFascicoloList()));
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    }

                    // Elimino il record da DB e dalla lista online
                    tipoFascicoloEjb.deleteDecAaTipoFascicolo(param, idAaTipoFascicolo);
                    getForm().getAaTipoFascicoloList().getTable().remove(riga);
                    getMessageBox().addInfo("Periodo tipo fascicolo eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void deleteCriteriRaggrFascicoloList() throws EMFError {
        BaseRowInterface currentRow = getForm().getCriteriRaggrFascicoloList().getTable().getCurrentRow();
        BigDecimal idCriterioRaggrFasc = currentRow.getBigDecimal("id_criterio_raggr_fasc");
        int riga = getForm().getCriteriRaggrFascicoloList().getTable().getCurrentRowIndex();

        if (!getMessageBox().hasError() && idCriterioRaggrFasc != null) {
            try {
                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null,
                                    null, null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (Application.Publisher.TIPO_FASCICOLO_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                        StrutTipiFascicoloForm form = (StrutTipiFascicoloForm) SpagoliteLogUtil.getForm(this);
                        param.setNomeAzione(
                                SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriteriRaggrFascicoloList()));
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    }

                    // Elimino il record da DB e dalla lista online
                    tipoFascicoloEjb.deleteDecCriterioRaggrFasc(param, idCriterioRaggrFasc);
                    getForm().getCriteriRaggrFascicoloList().getTable().remove(riga);
                    getMessageBox().addInfo("Criterio di raggruppamento fascicoli eliminato con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        if (!getMessageBox().hasError() && getLastPublisher().equals(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL)) {
            goBack();
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.STRUT_TIPI_FASCICOLO;
    }

    @Override
    public void logEventiTipoFascicolo() throws EMFError {
        DecTipoFascicoloRowBean riga = (DecTipoFascicoloRowBean) getForm().getTipoFascicoloList().getTable()
                .getCurrentRow();
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO);
        form.getOggettoDetail().getIdOggetto().setValue(riga.getIdTipoFascicolo().toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    /* WIZARD INSERIMENTO PERIODO VALIDITA' FASCICOLO */
    @Override
    public boolean inserimentoPeriodoValiditaWizardOnSave() throws EMFError {
        // Ricavo la lista parti a video in questo momento dopo inserimenti/modifiche/cancellazioni
        DecParteNumeroFascicoloTableBean tb = (DecParteNumeroFascicoloTableBean) getForm().getParteNumeroFascicoloList()
                .getTable();
        tb.clearSortingRule();
        tb.addSortingRule(DecParteNumeroFascicoloTableDescriptor.COL_NI_PARTE_NUMERO);
        tb.sort();
        if (tb.isEmpty()) {
            getMessageBox().addError("Deve essere definita almeno una parte per il tipo fascicolo");
        }

        // Recupero la colonna TiCharParte dal tablebean ed eseguo il controllo sul tipo GENERICO
        if (!getMessageBox().hasError()) {
            List<Object> tipiCarattere = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_TI_CHAR_PARTE);
            if (tipiCarattere.size() > 1 && tipiCarattere.contains(TiCharParte.GENERICO.name())) {
                getMessageBox().addError(
                        "Il carattere GENERICO \u00E8 ammesso solo se il tipo fascicolo si compone di una sola parte");
            }
        }
        // String tmpML = this.getMaxLenNumeroChiaveFromSession();
        // int maxLenPossibile = 50;
        // if (tmpML != null && !tmpML.isEmpty()) {
        // maxLenPossibile = Integer.parseInt(tmpML);
        // }

        /*
         * CONTROLLO NI_MIN_CHAR_PARTE, DA FARE if (!getMessageBox().hasError()) { // Sommo tutte le lunghezze minime
         * delle parti inserite BigDecimal tmpMinCharParte =
         * tb.sum(DecParteNumeroFascicoloTableDescriptor.COL_NI_MIN_CHAR_PARTE); if (tmpMinCharParte != null) { int
         * sumNiMinCharParte = tmpMinCharParte.intValue();
         * 
         * // Integro nel conteggio anche la dimensione dei separatori List<Object> tiCharSeps =
         * tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_TI_CHAR_SEP); for (Object tmpobj : tiCharSeps) { if
         * (StringUtils.isNotEmpty((String) tmpobj)) { sumNiMinCharParte++; } }
         * 
         * if (sumNiMinCharParte > maxLenPossibile) { getMessageBox().addError(
         * "La somma delle dimensioni minime delle parti deve essere minore o uguale a " + maxLenPossibile +
         * " caratteri (la somma calcolata risulta essere " + sumNiMinCharParte + " caratteri)."); } } }
         */
        //
        /* Controllo che i numeri d'ordine delle parti siano differenti */
        if (!getMessageBox().hasError()) {
            List<Object> progressivi = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_NI_PARTE_NUMERO);
            Set<Object> set = new HashSet<>(progressivi);
            if (set.size() < progressivi.size()) {
                getMessageBox().addError("Almeno un numero d'ordine \u00E8 definito su pi\u00F9 di una parte");
            }
        }

        /*
         * Controllo se vi è più di una parte in cui ni_max_char_parte non è stato valorizzato e se riguarda l'ultima
         * parte
         */
        if (!getMessageBox().hasError()) {
            List<Object> niMaxCharParti = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_NI_MAX_CHAR_PARTE,
                    new SortingRule[] { SortingRule
                            .getAscending(DecParteNumeroFascicoloRowBean.TABLE_DESCRIPTOR.COL_NI_PARTE_NUMERO) });
            int numeroNulli = 0;
            for (Object niMaxObject : niMaxCharParti) {
                BigDecimal niMax = (BigDecimal) niMaxObject;
                if (niMax == null) {
                    numeroNulli++;
                }
            }
            if (numeroNulli > 1) {
                getMessageBox().addError(
                        "Il numero massimo di caratteri pu\u00F2 NON essere valorizzato solo sull'ultima parte del tipo fascicolo");
            } else if (numeroNulli > 0) {
                if (niMaxCharParti.get(niMaxCharParti.size() - 1) != null) {
                    getMessageBox().addError(
                            "Il numero massimo di caratteri pu\u00F2 NON essere valorizzato solo sull'ultima parte del tipo fascicolo");
                }
            }
        }

        /* Controllo che la parte con numero d'ordine maggiore non abbia ti_char_sep valorizzato */
        if (!getMessageBox().hasError()) {
            List<Object> tiCharSeps = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_TI_CHAR_SEP);
            boolean existingLastCharSep = false;
            boolean emptyNotLastCharSepOrWrongNi = false;
            for (int i = 0; i < tiCharSeps.size(); i++) {
                String tiCharSep = (String) tiCharSeps.get(i);
                if (i != (tiCharSeps.size() - 1)) {
                    BigDecimal niMinCharParte = ((DecParteNumeroFascicoloRowBean) tb.getRow(i)).getNiMinCharParte();
                    BigDecimal niMaxCharParte = ((DecParteNumeroFascicoloRowBean) tb.getRow(i)).getNiMaxCharParte();
                    if (StringUtils.isEmpty(tiCharSep) && niMinCharParte.compareTo(niMaxCharParte) != 0) {
                        emptyNotLastCharSepOrWrongNi = true;
                    }
                } else if (StringUtils.isNotEmpty(tiCharSep)) {
                    existingLastCharSep = true;
                }
            }
            if (existingLastCharSep) {
                getMessageBox().addError(
                        "Sull'ultima parte del tipo fascicolo non \u00E8 possibile inserire il carattere separatore");
            }
            if (emptyNotLastCharSepOrWrongNi) {
                getMessageBox().addError(
                        "Tutte le parti devono avere il carattere separatore valorizzato o l'indicazione del numero fisso di caratteri accettato (numero minimo di caratteri deve coincidere con numero massimo di caratteri)");
            }
        }

        /* Controllo parti con ti_parte PROGRESSIVO */
        if (!getMessageBox().hasError()) {
            List<Object> tiParteList = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_TI_PARTE);
            int contaTiParte = 0;
            for (Object tiParte : tiParteList) {
                if (tiParte != null) {
                    if (((String) tiParte).equals(TiParte.PROGR_FASC.name())) {
                        contaTiParte++;
                    }
                    if (contaTiParte > 1) {
                        // Se la somma dei record vale più di 1
                        getMessageBox().addError(
                                "La coincidenza con il progressivo può essere valorizzata solo su una parte del tipo fascicolo");
                        break;
                    }
                }
            }
        }

        if (!getMessageBox().hasError()) {
            // Dati dei passi del wizard precedenti
            BigDecimal min = getForm().getAaTipoFascicoloDetail().getAa_ini_tipo_fascicolo().parse();
            BigDecimal max = getForm().getAaTipoFascicoloDetail().getAa_fin_tipo_fascicolo().parse();

            DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
            getForm().getAaTipoFascicoloDetail().copyToBean(aaTipoFascicoloRowBean);

            DecUsoModelloXsdFascTableBean metadatiProfiloFascicolo = (DecUsoModelloXsdFascTableBean) getForm()
                    .getMetadatiProfiloFascicoloList().getTable();
            DecUsoModelloXsdFascTableBean metadatiProfiloArchivistico = (DecUsoModelloXsdFascTableBean) getForm()
                    .getMetadatiProfiloArkList().getTable();

            // AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean)
            // getForm().getParametriAmministrazioneAaTipoFascList().getTable();
            // AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean)
            // getForm().getParametriConservazioneAaTipoFascList().getTable();
            // AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean)
            // getForm().getParametriGestioneAaTipoFascList().getTable();
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil
                                .getToolbarSave(getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                if (getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)) {
                    DecAaTipoFascicoloRowBean row = (DecAaTipoFascicoloRowBean) getForm().getAaTipoFascicoloList()
                            .getTable().getCurrentRow();

                    Set<BigDecimal> idMetadatiFascicoloEliminati = (Set<BigDecimal>) getSession()
                            .getAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI);
                    Set<BigDecimal> idMetadatiArchivisticoEliminati = (Set<BigDecimal>) getSession()
                            .getAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI);
                    Set<BigDecimal> idPartiEliminate = (Set<BigDecimal>) getSession()
                            .getAttribute(PARAMETER_ID_PARTI_ELIMINATE);
                    tipoFascicoloEjb.saveDecAaTipoFascicolo(param, aaTipoFascicoloRowBean, metadatiProfiloFascicolo,
                            idMetadatiFascicoloEliminati, metadatiProfiloArchivistico, idMetadatiArchivisticoEliminati,
                            tb, idPartiEliminate);
                    tipoFascicoloEjb.checkFascicoliNelPeriodoValidita(row.getIdAaTipoFascicolo());
                } else {
                    BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();
                    BigDecimal idAaTipoFascicolo = tipoFascicoloEjb.saveDecAaTipoFascicolo(param, idTipoFascicolo,
                            aaTipoFascicoloRowBean, metadatiProfiloFascicolo, metadatiProfiloArchivistico, tb);
                    if (idAaTipoFascicolo != null) {
                        tipoFascicoloEjb.checkFascicoliNelPeriodoValidita(idAaTipoFascicolo);
                    } else {
                        throw new ParerUserError(
                                "Errore inaspettato: periodo di validit\u00E0 non salvato correttamente");
                    }
                }

                // long maxSizeNum = 0;
                // List<Object> maxSizes = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_NI_MAX_CHAR_PARTE);
                // for (int i = 0; i < maxSizes.size(); i++) {
                // Object tmpobj = maxSizes.get(i);
                // if (tmpobj != null) {
                // maxSizeNum += ((BigDecimal) tmpobj).longValue();
                // } else {
                // BigDecimal niMinCharParte = ((DecParteNumeroFascicoloRowBean) tb.getRow(i)).getNiMinCharParte();
                // String tipoPadding = ((DecParteNumeroFascicoloRowBean) tb.getRow(i)).getTiPadParte();
                // String tipoDato = ((DecParteNumeroFascicoloRowBean) tb.getRow(i)).getTiCharParte();
                // if ((StringUtils.isNotEmpty(tipoPadding)
                // && tipoPadding.equals(ConfigRegAnno.TipiPadding.RIEMPI_0_A_SX_LESS12.name()))
                // || tipoDato.equals(KeyOrdUtility.TipiCalcolo.NUMERICO.name())
                // || tipoDato.equals(KeyOrdUtility.TipiCalcolo.NUMERICO_GENERICO.name())) {
                // maxSizeNum += 12L;
                // } else {
                // maxSizeNum += niMinCharParte.longValue();
                // }
                // }
                // }
                // if (maxSizeNum > maxLenPossibile) {
                // getMessageBox().addWarning("Periodo di validit\u00E0 salvato con successo, ma la dimensione "
                // + "minima normalizzata del numero (" + maxSizeNum + ") supera " + maxLenPossibile + " caratteri. "
                // + "Il versamento potrà avvenire solo attraverso la forzatura");
                // } else {
                // getMessageBox().addInfo("Periodo di validit\u00E0 salvato con successo");
                // }
                getMessageBox().addInfo("Periodo di validit\u00E0 tipo fascicolo salvato con successo");
                getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                setAaTipoFascicoloStatus(Status.view);
                setInitialStateSections();
                goBackTo(Application.Publisher.TIPO_FASCICOLO_DETAIL);
            } catch (ParerUserError ex) {
                getMessageBox().addError("Il periodo di validit\u00E0 del tipo fascicolo non pu\u00F2 essere salvato: "
                        + ex.getDescription());
            } catch (Exception ex) {
                logger.error("Eccezione generica nel salvataggio del periodo di validit\u00E0 del tipo fascicolo", ex);
                getMessageBox().addError(
                        "Si \u00E8 verificata un'eccezione nel salvataggio del periodo di validit\u00E0 del tipo fascicolo");
            }
        }
        return !getMessageBox().hasError();
    }

    @Override
    public void inserimentoPeriodoValiditaWizardOnCancel() throws EMFError {
        setAaTipoFascicoloStatus(Status.view);
        // Chiudo le section
        getForm().getParametriControlloClassificazioneSection().setLoadOpened(false);
        getForm().getParametriControlloCollegamentiSection().setLoadOpened(false);
        getForm().getParametriControlloNumeroFascSection().setLoadOpened(false);
        goBack();
    }

    @Override
    public String getDefaultInserimentoPeriodoValiditaWizardPublisher() throws EMFError {
        return Application.Publisher.AA_TIPO_FASCICOLO_WIZARD;
    }

    /**
     * 1° STEP enter: Gestione del periodo di validità del tipo fascicolo
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void inserimentoPeriodoValiditaWizardDettaglioPeriodoStepOnEnter() throws EMFError {
        getForm().getAaTipoFascicoloDetail().setEditMode();

        // getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm().setEditMode();
        // getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons().setEditMode();
        // getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest().setEditMode();
        // getForm().getParametriAmministrazioneAaTipoFascList().setHideDeleteButton(true);
        // getForm().getParametriConservazioneAaTipoFascList().setHideDeleteButton(true);
        // getForm().getParametriGestioneAaTipoFascList().setHideDeleteButton(true);
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    /**
     * 1° STEP exit: Gestione del periodo di validità del tipo fascicolo
     *
     * @return true/false
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public boolean inserimentoPeriodoValiditaWizardDettaglioPeriodoStepOnExit() throws EMFError {

        // getForm().getParametriAmministrazioneAaTipoFascList().post(getRequest());
        // getForm().getParametriConservazioneAaTipoFascList().post(getRequest());
        // getForm().getParametriGestioneAaTipoFascList().post(getRequest());
        // forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
        if (getForm().getAaTipoFascicoloDetail().postAndValidate(getRequest(), getMessageBox())) {
            // Prendo lo stesso parametro utilizzato per i periodi dei registri
            String annoValidoMinimo = configurationHelper.getValoreParamApplic(
                    CostantiDB.ParametroAppl.REG_ANNO_VALID_MINIMO, null, null, null, null,
                    CostantiDB.TipoAplVGetValAppart.APPLIC);
            if (StringUtils.isBlank(annoValidoMinimo) || !StringUtils.isNumeric(annoValidoMinimo)) {
                getMessageBox().addError(
                        "Parametro di configurazione applicativo per l'anno minimo di validit\u00E0 non valido");
            } else {
                int dbAnnoMinimo = Integer.parseInt(annoValidoMinimo);

                if (StringUtils.isBlank(getForm().getAaTipoFascicoloDetail().getAa_ini_tipo_fascicolo().getValue())
                        || Integer.parseInt(getForm().getAaTipoFascicoloDetail().getAa_ini_tipo_fascicolo()
                                .getValue()) < dbAnnoMinimo) {
                    getMessageBox().addError(
                            "Errore di compilazione: anno di inizio validit\u00E0 inferiore a " + annoValidoMinimo);
                } else if (StringUtils
                        .isNotBlank(getForm().getAaTipoFascicoloDetail().getAa_fin_tipo_fascicolo().getValue())
                        && Integer.parseInt(getForm().getAaTipoFascicoloDetail().getAa_fin_tipo_fascicolo()
                                .getValue()) < Integer.parseInt(
                                        getForm().getAaTipoFascicoloDetail().getAa_ini_tipo_fascicolo().getValue())) {
                    getMessageBox().addError("Attenzione intervallo non valido");
                }

                // // Controllo valori possibili su tipo unità documentaria
                // AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean)
                // getForm().getParametriAmministrazioneAaTipoFascList().getTable();
                // AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean)
                // getForm().getParametriConservazioneAaTipoFascList().getTable();
                // AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean)
                // getForm().getParametriGestioneAaTipoFascList().getTable();
                // String error = amministrazioneEjb.checkParametriAmmessi("aa_tipo_fascicolo",
                // parametriAmministrazione, parametriConservazione, parametriGestione);
                // if (error != null) {
                // getMessageBox().addError(error);
                // }
                if (!getMessageBox().hasError()) {
                    BigDecimal ini = getForm().getAaTipoFascicoloDetail().getAa_ini_tipo_fascicolo().parse();
                    BigDecimal fin = getForm().getAaTipoFascicoloDetail().getAa_fin_tipo_fascicolo().parse();
                    BigDecimal idTipoFascicolo = null;
                    BigDecimal idAaTipoFascicolo = null;

                    if (fin == null) {
                        fin = new BigDecimal(9999);
                    }

                    if (ini.compareTo(fin) > 0) {
                        getMessageBox().addError(
                                "Errore di compilazione: anno di inizio validità superiore ad anno di fine validità");
                    }

                    if (getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)) {
                        DecAaTipoFascicoloRowBean row = (DecAaTipoFascicoloRowBean) getForm().getAaTipoFascicoloList()
                                .getTable().getCurrentRow();
                        idTipoFascicolo = row.getIdTipoFascicolo();
                        idAaTipoFascicolo = row.getIdAaTipoFascicolo();
                    } else {
                        idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();
                    }

                    if (tipoFascicoloEjb.existPeriodiValiditaSovrappostiFascicoli(idAaTipoFascicolo, idTipoFascicolo,
                            ini, fin)) {
                        getMessageBox()
                                .addError("Errore di compilazione: range inserito sovrapposto a range già presenti");
                    }
                }
            }
        }
        return !getMessageBox().hasError();
    }

    /**
     * 2° STEP enter: Gestione dei metadati profilo fascicolo
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void inserimentoPeriodoValiditaWizardXsdMetadatiProfiloFascStepOnEnter() throws EMFError {
        // Pulisco i campi di inserimento e azzero la lista metadati di tipo profilo
        getForm().getMetadatiProfilo().clear();
        getForm().getMetadatiProfilo().setEditMode();
        getForm().getMetadatiProfilo().getTi_modello_xsd().setViewMode();

        // Imposto i parametri nella lista
        getForm().getMetadatiProfiloFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getMetadatiProfiloFascicoloList().getTable().first();
        getForm().getMetadatiProfiloFascicoloList().setStatus(Status.insert);
        //
        initMetadatiProfilo(CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO);
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    /**
     * 2° STEP exit: Gestione dei metadati profilo fascicolo
     *
     * @return true/false
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public boolean inserimentoPeriodoValiditaWizardXsdMetadatiProfiloFascStepOnExit() throws EMFError {
        boolean result = true;

        List<Object> flStandardByList = ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloFascicoloList()
                .getTable()).toList(DecUsoModelloXsdFascTableDescriptor.COL_FL_STANDARD);
        int numberFlStandardTrue = Collections.frequency(flStandardByList, "1");
        if (numberFlStandardTrue > 1) {
            getMessageBox().addError("Solo un xsd di metadati di profilo fascicolo può essere definito come standard");
            result = false;
        }

        // Controllo che sia presente almeno un xsd per i metadati di profilo fascicolo
        if (getForm().getMetadatiProfiloFascicoloList().getTable().isEmpty()) {
            getMessageBox().addError("E' obbligatorio definire un xsd per i metadati di profilo fascicolo");
            result = false;
        }

        // Controllo se il modello è stato utilizzato per il versamento di fascicoli prima di una cancellazione o
        // modifica
        Set<BigDecimal> idMetadatiFascicoloPerVersFasc = new HashSet<>();
        idMetadatiFascicoloPerVersFasc
                .addAll(getModelliInVersamentoFascicoli(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI));
        idMetadatiFascicoloPerVersFasc
                .addAll(getModelliInVersamentoFascicoli(PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI));
        if (!idMetadatiFascicoloPerVersFasc.isEmpty()) {
            for (BigDecimal idMeta : idMetadatiFascicoloPerVersFasc) {
                String desModello = getModelloInUsoRowBean(idMeta).getDsXsd();
                getMessageBox().addError("Il modello " + desModello
                        + " è stato utilizzato per il versamento di fascicoli; l’associazione non può essere eliminata");
            }
            result = false;
        }

        // forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
        return result;
    }

    @Override
    public void addMetadatiProfiloFascicolo() throws EMFError {
        getForm().getMetadatiProfilo().post(getRequest());

        BigDecimal idModelloXsdFascicolo = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().parse();
        String flStandard = getForm().getMetadatiProfilo().getFl_standard_field().parse();

        Date dtIstituz = getForm().getMetadatiProfilo().getDt_istituz().parse();
        if (dtIstituz == null) {
            dtIstituz = Calendar.getInstance().getTime();
        }

        Date dtSoppres = getForm().getMetadatiProfilo().getDt_soppres().parse();
        if (dtSoppres == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            dtSoppres = calendar.getTime();
        }

        if (getForm().getMetadatiProfilo().validate(getMessageBox())) {
            DecUsoModelloXsdFascRowBean row = null;
            Integer rowIndex = null;

            // Se sono in update, prendo la RIGA che voglio modificare e INDICE da eliminare
            if (getForm().getMetadatiProfiloFascicoloList().getStatus().equals(Status.update)) {
                row = (DecUsoModelloXsdFascRowBean) getForm().getMetadatiProfiloFascicoloList().getTable()
                        .getCurrentRow();
                rowIndex = getForm().getMetadatiProfiloFascicoloList().getTable().getCurrentRowIndex();

                // Tengo un parametro in sessione come informazione riguardo i record da modificare su DB
                Set<BigDecimal> idUsi = (Set<BigDecimal>) getSession()
                        .getAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI);
                if (idUsi == null) {
                    idUsi = new HashSet<>();
                }
                // Controllo se la modifica riguarda una variazione del modello scelto
                BigDecimal idModelloXsdFascSelected = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo()
                        .parse();
                if (row.getIdUsoModelloXsdFasc() != null) {
                    BigDecimal idModelloXsdFascCurrent = getModelloInUsoRowBean(row.getIdUsoModelloXsdFasc())
                            .getIdModelloXsdFascicolo();
                    if (!idModelloXsdFascSelected.equals(idModelloXsdFascCurrent)) {
                        idUsi.add(row.getIdUsoModelloXsdFasc());
                    } else if (idUsi.contains(row.getIdUsoModelloXsdFasc())) {
                        idUsi.remove(row.getIdUsoModelloXsdFasc());
                    }

                    getSession().setAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_MODIFICATI, idUsi);
                }
            }
            /*
             * else { BigDecimal idModelloXsdFascSelected =
             * getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().parse(); DecUsoModelloXsdFascRowBean
             * usoModello = getUsoModelloCurrentRowBean(idModelloXsdFascSelected,
             * CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO);
             * 
             * if (usoModello != null) { Set<BigDecimal> idUsi = (Set<BigDecimal>)
             * getSession().getAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI); if (idUsi != null &&
             * !idUsi.isEmpty()) { if (idUsi.contains(usoModello.getIdUsoModelloXsdFasc())) {
             * idUsi.remove(usoModello.getIdUsoModelloXsdFasc()); } }
             * 
             * getSession().setAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI, idUsi); } }
             */

            // Controllo non esista già lo stesso cd xsd in lista
            List<Object> cdXsdByList = ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloFascicoloList()
                    .getTable()).toList("cd_xsd");
            String cdXsdDaIns = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().getDecodedValue()
                    .split("-")[0].trim();
            String dsXsdDaIns = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().getDecodedValue()
                    .split("-")[1].trim();

            if (getForm().getMetadatiProfiloFascicoloList().getStatus().equals(Status.insert)) {
                if (cdXsdByList.contains(cdXsdDaIns)) {
                    getMessageBox().addError("Esiste gi\u00E0 nella lista un xsd con codice versione " + cdXsdDaIns);
                }
            } else {
                String oldName = (row != null ? row.getString("cd_xsd") : null);
                if (!cdXsdDaIns.equals(oldName) && cdXsdByList.contains(cdXsdDaIns)) {
                    getMessageBox().addError("Esiste gi\u00E0 nella lista un xsd con codice versione " + cdXsdDaIns);
                }
            }

            if (!getMessageBox().hasError()) {

                DecUsoModelloXsdFascTableBean tb = (DecUsoModelloXsdFascTableBean) getForm()
                        .getMetadatiProfiloFascicoloList().getTable();

                DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = tipoFascicoloEjb
                        .getDecModelloXsdFascicoloRowBean(idModelloXsdFascicolo);
                DecUsoModelloXsdFascRowBean tmp = new DecUsoModelloXsdFascRowBean();
                tmp.setBigDecimal("id_uso_modello_xsd_fasc", row != null ? row.getIdUsoModelloXsdFasc() : null);
                tmp.setString("cd_xsd", cdXsdDaIns);
                tmp.setString("ds_xsd", dsXsdDaIns);
                tmp.setFlStandard(flStandard);
                tmp.setIdModelloXsdFascicolo(idModelloXsdFascicolo);
                tmp.setObject("dt_istituz", new Timestamp(dtIstituz.getTime()));
                tmp.setObject("dt_soppres", new Timestamp(dtSoppres.getTime()));
                if (modelloXsdFascicoloRowBean.getDtIstituz().before(new Date())
                        && modelloXsdFascicoloRowBean.getDtSoppres().after(new Date())) {
                    tmp.setString("fl_attivo", "1");
                } else {
                    tmp.setString("fl_attivo", "0");
                }

                if (getForm().getMetadatiProfiloFascicoloList().getStatus().equals(Status.insert)) {
                    tb.add(tmp);
                } else {
                    tb.remove(rowIndex);
                    tb.add(tmp);
                }
                tb.sort();
                initMetadatiProfilo(CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO);
                getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().clear();
                getForm().getMetadatiProfilo().getFl_standard_field().clear();
                // Riporto in status insert la lista per successiva eventuale aggiunta
                // (in caso di update, ci penserà la chiamata al metodo updateMetadatiProfiloFascicoloList a riportarla
                // in status update)
                getForm().getMetadatiProfiloFascicoloList().setStatus(Status.insert);
            }
        }
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    @Override
    public void deleteMetadatiProfiloFascicoloList() throws EMFError {
        DecUsoModelloXsdFascTableBean usoModelloXsdFascTableBean = (DecUsoModelloXsdFascTableBean) getForm()
                .getMetadatiProfiloFascicoloList().getTable();
        int index = usoModelloXsdFascTableBean.getCurrentRowIndex();
        DecUsoModelloXsdFascRowBean currentRow = (DecUsoModelloXsdFascRowBean) usoModelloXsdFascTableBean
                .getCurrentRow();
        // In caso il record sia salvato sul DB, mi salvo l'informazione sul suo id da eliminare
        if (currentRow.getIdUsoModelloXsdFasc() != null) {
            // Tengo un parametro in sessione come informazione riguardo i record da eliminare: mi facilita la gestione
            // in caso di
            // "cancellazioni" di record o già presenti su DB, o aggiunti nella videata e poi subito rimossi
            Set<BigDecimal> idUsi = (Set<BigDecimal>) getSession()
                    .getAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI);
            if (idUsi == null) {
                idUsi = new HashSet<>();
            }
            idUsi.add(currentRow.getIdUsoModelloXsdFasc());
            getSession().setAttribute(PARAMETER_ID_METADATI_PROFILO_FASC_ELIMINATI, idUsi);
        }
        // Rimuovo la riga dalla tabella
        getForm().getMetadatiProfiloFascicoloList().remove(index);

        // Riordino in base alla versione
        usoModelloXsdFascTableBean.clearSortingRule();
        usoModelloXsdFascTableBean.addSortingRule("cd_xsd");
        usoModelloXsdFascTableBean.sort();

        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    @Override
    public void updateMetadatiProfiloFascicoloList() throws EMFError {
        // Recupero i dati della riga che devo modificare per andarli poi a settare nelle caselle di testo di dettaglio
        DecUsoModelloXsdFascRowBean row = (DecUsoModelloXsdFascRowBean) getForm().getMetadatiProfiloFascicoloList()
                .getTable().getCurrentRow();
        getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo()
                .setValue(row.getIdModelloXsdFascicolo().toPlainString());
        getForm().getMetadatiProfilo().getFl_standard_field().setValue(row.getFlStandard());

        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getForm().getMetadatiProfilo().getDt_istituz().setValue(df.format(row.getDtIstituz()));
        getForm().getMetadatiProfilo().getDt_soppres().setValue(df.format(row.getDtSoppres()));

        getForm().getMetadatiProfiloFascicoloList().setStatus(Status.update);
    }

    /**
     * 3° STEP enter: Gestione dei metadati profilo archivistico
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void inserimentoPeriodoValiditaWizardXsdMetadatiProfiloArkStepOnEnter() throws EMFError {
        // Pulisco i campi di inserimento e azzero la lista metadati di tipo profilo
        getForm().getMetadatiProfilo().clear();
        getForm().getMetadatiProfilo().setEditMode();
        getForm().getMetadatiProfilo().getTi_modello_xsd().setViewMode();

        // Imposto i parametri nella lista
        getForm().getMetadatiProfiloArkList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getMetadatiProfiloArkList().getTable().first();
        getForm().getMetadatiProfiloArkList().setStatus(Status.insert);
        // Inizializzo le combo per inserire i dati
        initMetadatiProfilo(CostantiDB.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO);
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    /**
     * 3° STEP exit: Gestione dei metadati profilo archivistico
     *
     * @return true/false
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public boolean inserimentoPeriodoValiditaWizardXsdMetadatiProfiloArkStepOnExit() throws EMFError {
        boolean result = true;

        List<Object> flStandardByList = ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloArkList()
                .getTable()).toList(DecUsoModelloXsdFascTableDescriptor.COL_FL_STANDARD);
        int numberFlStandardTrue = Collections.frequency(flStandardByList, "1");
        if (numberFlStandardTrue > 1) {
            getMessageBox()
                    .addError("Solo un xsd di metadati di profilo archivistico può essere definito come standard");
            result = false;
        }

        // Controllo che sia presente almeno un xsd per i metadati di profilo fascicolo
        if (getForm().getMetadatiProfiloFascicoloList().getTable().isEmpty()) {
            getMessageBox().addError("E' obbligatorio definire un xsd per i metadati di profilo archivistico");
            result = false;
        }

        // Controllo se il modello è stato utilizzato per il versamento di fascicoli prima di una cancellazione o
        // modifica
        Set<BigDecimal> idMetadatiFascicoloPerVersFasc = new HashSet<>();
        idMetadatiFascicoloPerVersFasc
                .addAll(getModelliInVersamentoFascicoli(PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI));
        idMetadatiFascicoloPerVersFasc
                .addAll(getModelliInVersamentoFascicoli(PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI));
        if (!idMetadatiFascicoloPerVersFasc.isEmpty()) {
            for (BigDecimal idMeta : idMetadatiFascicoloPerVersFasc) {
                String desModello = getModelloInUsoRowBean(idMeta).getDsXsd();
                getMessageBox().addError("Il modello " + desModello
                        + " è stato utilizzato per il versamento di fascicoli; l’associazione non può essere eliminata");
            }
            result = false;
        }

        // forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
        return result;
    }

    @Override
    public void addMetadatiProfiloArk() throws EMFError {
        getForm().getMetadatiProfilo().post(getRequest());

        BigDecimal idModelloXsdFascicolo = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().parse();
        String flStandard = getForm().getMetadatiProfilo().getFl_standard_field().parse();

        Date dtIstituz = getForm().getMetadatiProfilo().getDt_istituz().parse();
        if (dtIstituz == null) {
            dtIstituz = Calendar.getInstance().getTime();
        }

        Date dtSoppres = getForm().getMetadatiProfilo().getDt_soppres().parse();
        if (dtSoppres == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(2444, 11, 31, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            dtSoppres = calendar.getTime();
        }

        if (getForm().getMetadatiProfilo().validate(getMessageBox())) {
            DecUsoModelloXsdFascRowBean row = null;
            Integer rowIndex = null;

            // Se sono in update, prendo la RIGA che voglio modificare e INDICE da eliminare
            if (getForm().getMetadatiProfiloArkList().getStatus().equals(Status.update)) {
                row = (DecUsoModelloXsdFascRowBean) getForm().getMetadatiProfiloArkList().getTable().getCurrentRow();
                rowIndex = getForm().getMetadatiProfiloArkList().getTable().getCurrentRowIndex();

                // Tengo un parametro in sessione come informazione riguardo i record da modificare su DB
                Set<BigDecimal> idUsi = (Set<BigDecimal>) getSession()
                        .getAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI);
                if (idUsi == null) {
                    idUsi = new HashSet<>();
                }
                // Controllo se la modifica riguarda una variazione del modello scelto
                BigDecimal idModelloXsdFascSelected = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo()
                        .parse();
                if (row.getIdUsoModelloXsdFasc() != null) {
                    BigDecimal idModelloXsdFascCurrent = getModelloInUsoRowBean(row.getIdUsoModelloXsdFasc())
                            .getIdModelloXsdFascicolo();
                    if (!idModelloXsdFascSelected.equals(idModelloXsdFascCurrent)) {
                        idUsi.add(row.getIdUsoModelloXsdFasc());
                    } else if (idUsi.contains(row.getIdUsoModelloXsdFasc())) {
                        idUsi.remove(row.getIdUsoModelloXsdFasc());
                    }

                    getSession().setAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_MODIFICATI, idUsi);
                }
            }

            // Controllo non esista già lo stesso cd xsd in lista
            List<Object> cdXsdByList = ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloArkList()
                    .getTable()).toList("cd_xsd");
            String cdXsdDaIns = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().getDecodedValue()
                    .split("-")[0].trim();
            String dsXsdDaIns = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().getDecodedValue()
                    .split("-")[1].trim();

            if (getForm().getMetadatiProfiloArkList().getStatus().equals(Status.insert)) {
                if (cdXsdByList.contains(cdXsdDaIns)) {
                    getMessageBox().addError("Esiste gi\u00E0 nella lista un xsd con codice versione " + cdXsdDaIns);
                }
            } else {
                String oldName = (row != null ? row.getString("cd_xsd") : null);
                if (!cdXsdDaIns.equals(oldName) && cdXsdByList.contains(cdXsdDaIns)) {
                    getMessageBox().addError("Esiste gi\u00E0 nella lista un xsd con codice versione " + cdXsdDaIns);
                }
            }

            if (!getMessageBox().hasError()) {

                DecUsoModelloXsdFascTableBean tb = (DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloArkList()
                        .getTable();

                DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = tipoFascicoloEjb
                        .getDecModelloXsdFascicoloRowBean(idModelloXsdFascicolo);
                DecUsoModelloXsdFascRowBean tmp = new DecUsoModelloXsdFascRowBean();
                tmp.setBigDecimal("id_uso_modello_xsd_fasc", row != null ? row.getIdUsoModelloXsdFasc() : null);
                tmp.setString("cd_xsd", cdXsdDaIns);
                tmp.setString("ds_xsd", dsXsdDaIns);
                tmp.setFlStandard(flStandard);
                tmp.setIdModelloXsdFascicolo(idModelloXsdFascicolo);
                tmp.setObject("dt_istituz", new Timestamp(dtIstituz.getTime()));
                tmp.setObject("dt_soppres", new Timestamp(dtSoppres.getTime()));
                if (modelloXsdFascicoloRowBean.getDtIstituz().before(new Date())
                        && modelloXsdFascicoloRowBean.getDtSoppres().after(new Date())) {
                    tmp.setString("fl_attivo", "1");
                } else {
                    tmp.setString("fl_attivo", "0");
                }

                if (getForm().getMetadatiProfiloArkList().getStatus().equals(Status.insert)) {
                    tb.add(tmp);
                } else {
                    tb.remove(rowIndex);
                    tb.add(tmp);
                }
                tb.sort();
                initMetadatiProfilo(CostantiDB.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO);
                getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().clear();
                getForm().getMetadatiProfilo().getFl_standard_field().clear();
                // Riporto in status insert la lista per successiva eventuale aggiunta
                // (in caso di update, ci penserà la chiamata al metodo updateMetadatiProfiloArkList a riportarla in
                // status update)
                getForm().getMetadatiProfiloArkList().setStatus(Status.insert);
            }
        }
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    @Override
    public void deleteMetadatiProfiloArkList() throws EMFError {
        DecUsoModelloXsdFascTableBean usoModelloXsdFascTableBean = (DecUsoModelloXsdFascTableBean) getForm()
                .getMetadatiProfiloArkList().getTable();
        int index = usoModelloXsdFascTableBean.getCurrentRowIndex();
        DecUsoModelloXsdFascRowBean currentRow = (DecUsoModelloXsdFascRowBean) usoModelloXsdFascTableBean
                .getCurrentRow();
        // In caso il record sia salvato sul DB, mi salvo l'informazione sul suo id da eliminare
        if (currentRow.getIdUsoModelloXsdFasc() != null) {
            // Tengo un parametro in sessione come informazione riguardo i record da eliminare: mi facilita la gestione
            // in caso di
            // "cancellazioni" di record o già presenti su DB, o aggiunti nella videata e poi subito rimossi
            Set<BigDecimal> idUsi = (Set<BigDecimal>) getSession()
                    .getAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI);
            if (idUsi == null) {
                idUsi = new HashSet<>();
            }
            idUsi.add(currentRow.getIdUsoModelloXsdFasc());
            getSession().setAttribute(PARAMETER_ID_METADATI_PROFILO_ARK_ELIMINATI, idUsi);
        }
        // Rimuovo la riga dalla tabella
        getForm().getMetadatiProfiloArkList().remove(index);

        // Riordino in base alla versione
        usoModelloXsdFascTableBean.clearSortingRule();
        usoModelloXsdFascTableBean.addSortingRule("cd_xsd");
        usoModelloXsdFascTableBean.sort();

        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    @Override
    public void updateMetadatiProfiloArkList() throws EMFError {
        // Recupero i dati della riga che devo modificare per andarli poi a settare nelle caselle di testo di dettaglio
        DecUsoModelloXsdFascRowBean row = (DecUsoModelloXsdFascRowBean) getForm().getMetadatiProfiloArkList().getTable()
                .getCurrentRow();
        getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo()
                .setValue(row.getIdModelloXsdFascicolo().toPlainString());
        getForm().getMetadatiProfilo().getFl_standard_field().setValue(row.getFlStandard());

        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getForm().getMetadatiProfilo().getDt_istituz().setValue(df.format(row.getDtIstituz()));
        getForm().getMetadatiProfilo().getDt_soppres().setValue(df.format(row.getDtSoppres()));

        getForm().getMetadatiProfiloArkList().setStatus(Status.update);
    }

    /**
     * 4° STEP enter: Gestione delle parti
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void inserimentoPeriodoValiditaWizardPartiStepOnEnter() throws EMFError {
        // Pulisco i campi di inserimento e azzero la lista metadati di tipo profilo
        getForm().getParteNumeroFascicoloDetail().clear();
        getForm().getParteNumeroFascicoloDetail().setEditMode();
        // Imposto i parametri nella lista
        getForm().getParteNumeroFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
        getForm().getParteNumeroFascicoloList().getTable().first();
        getForm().getParteNumeroFascicoloList().setStatus(Status.insert);
        // Inizializzo le combo
        initParteNumeroFascicoloDetail();
        increaseNiNumeroFascicolo();
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    /**
     * 4° STEP exit: Gestione delle parti
     *
     * @return true/false
     * 
     * @throws EMFError
     *             errore generico
     */
    @Override
    public boolean inserimentoPeriodoValiditaWizardPartiStepOnExit() throws EMFError {
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
        return true;
    }

    private void increaseNiNumeroFascicolo() {
        int sizeParti = getForm().getParteNumeroFascicoloList().getTable().size();
        String nextPg = String.valueOf(sizeParti + 1);
        getForm().getParteNumeroFascicoloDetail().getNi_parte_numero().setValue(nextPg);
        getForm().getParteNumeroFascicoloDetail().getNi_parte_numero().setViewMode();
    }

    private void initMetadatiProfilo(CostantiDB.TiModelloXsd tiModelloXsd) throws EMFError {
        // Combo tipo xsd
        getForm().getMetadatiProfilo().getTi_modello_xsd().setDecodeMap(ComboGetter.getMappaTiModelloXsd());
        getForm().getMetadatiProfilo().getTi_modello_xsd().setValue(tiModelloXsd.name());
        // Combo versioni di xsd
        DecModelloXsdFascicoloTableBean modelloXsdFascicoloTableBean = tipoFascicoloEjb
                .getDecModelloXsdFascicoloTableBean(getForm().getStrutRif().getId_ambiente().parse(), new Date(), null,
                        CostantiDB.TiUsoModelloXsd.VERS.name(), tiModelloXsd.name());
        getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().setDecodeMap(DecodeMap.Factory
                .newInstance(modelloXsdFascicoloTableBean, "id_modello_xsd_fascicolo", "codice_descrizione"));

        getForm().getMetadatiProfilo().getDt_istituz()
                .setValue(ActionUtils.getStringDate(Calendar.getInstance().getTime()));
        getForm().getMetadatiProfilo().getDt_soppres().setValue(ActionUtils.getStringDate(null));
    }

    private void initParteNumeroFascicoloDetail() {
        getForm().getParteNumeroFascicoloDetail().getTi_char_parte().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_char_parte", DecParteNumeroFascicolo.TiCharParte.values()));
        getForm().getParteNumeroFascicoloDetail().getTi_char_sep()
                .setDecodeMap(ComboGetter.getMappaSeparatori("ti_char_sep"));
        getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_pad_parte", DecParteNumeroFascicolo.TiPadParte.values()));
        getForm().getParteNumeroFascicoloDetail().getTi_parte().setDecodeMap(new DecodeMap());
    }

    @Override
    public JSONObject triggerParteNumeroFascicoloDetailTi_char_parteOnTrigger() throws EMFError {
        getForm().getParteNumeroFascicoloDetail().post(getRequest());
        String tiCharParte = getForm().getParteNumeroFascicoloDetail().getTi_char_parte().parse();
        List<DecParteNumeroFascicolo.TiParte> allowed = new ArrayList<>();

        if (StringUtils.isNotBlank(tiCharParte)) {
            DecParteNumeroFascicolo.TiCharParte tipo = DecParteNumeroFascicolo.TiCharParte.valueOf(tiCharParte);
            switch (tipo) {
            case NUMERICO:
            case NUMERICO_GENERICO:
                allowed = Arrays.asList(DecParteNumeroFascicolo.TiParte.values());
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_0_A_SX.name());
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_0_A_SX.name());
                break;
            case NUMERI_ROMANI:
                allowed.addAll(EnumSet.of(CLASSIF, PROGR_FASC, PROGR_SUB_FASC));
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_0_A_SX.name());
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_0_A_SX.name());
                break;
            case ALFABETICO:
            case ALFANUMERICO:
                allowed.addAll(EnumSet.of(CLASSIF));
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_SPAZIO_DX.name());
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_SPAZIO_DX.name());
                break;
            case GENERICO:
                allowed.addAll(EnumSet.of(ANNO, CLASSIF));
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_SPAZIO_DX.name());
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_SPAZIO_DX.name());
                break;
            case PARTE_GENERICO:
                allowed.addAll(EnumSet.of(ANNO, CLASSIF, PROGR_FASC, PROGR_SUB_FASC));
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_SPAZIO_DX.name());
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte()
                        .setValue(DecParteNumeroFascicolo.TiPadParte.RIEMPI_SPAZIO_DX.name());
                break;
            default:
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo().setValue(null);
                getForm().getParteNumeroFascicoloDetail().getTi_pad_parte().setValue(null);
                break;
            }

            // Gestione campo ti_parte ("Coincidenza con")
            BaseTable t = new BaseTable();
            DecodeMap mappa = new DecodeMap();
            for (DecParteNumeroFascicolo.TiParte all : allowed) {
                BaseRow r = new BaseRow();
                r.setString("ti_parte", all.name());
                t.add(r);
            }
            mappa.populatedMap(t, "ti_parte", "ti_parte");
            getForm().getParteNumeroFascicoloDetail().getTi_parte().setDecodeMap(mappa);
        }
        return getForm().getParteNumeroFascicoloDetail().asJSON();
    }

    @Override
    public JSONObject triggerParteNumeroFascicoloDetailTi_pad_parte_comboOnTrigger() throws EMFError {
        getForm().getParteNumeroFascicoloDetail().post(getRequest());
        String tipo = getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo().parse();
        getForm().getParteNumeroFascicoloDetail().getTi_pad_parte().setValue(tipo);

        String message = null;
        if (StringUtils.isNotBlank(tipo)) {
            DecParteNumeroFascicolo.TiPadParte tipoEnum = DecParteNumeroFascicolo.TiPadParte.valueOf(tipo);
            switch (tipoEnum) {
            case RIEMPI_0_A_SX:
                message = "Esegue riempimento di 0 a sinistra fino al numero massimo di caratteri oppure fino a 20 caratteri se il numero massimo non è definito";
                break;
            case RIEMPI_SPAZIO_DX:
                message = "Esegue riempimento di spazi a destra fino al numero massimo di caratteri oppure fino a 20 caratteri se il numero massimo non è definito";
                break;
            case NO_RIEMPI:
                message = "Non viene eseguito il riempimento";
                break;
            case FORMAT_CLASSIF:
                message = "Esegue la formattazione della classifica per consentirne l'ordinamento";
                break;
            }
            getForm().getParteNumeroFascicoloDetail().getDesc_pad_parte().setValue(message);
        }
        return getForm().getParteNumeroFascicoloDetail().asJSON();
    }

    @Override
    public void addParteNumeroFascicolo() throws EMFError {
        getForm().getParteNumeroFascicoloDetail().post(getRequest());
        String tipoRiempimento = getForm().getParteNumeroFascicoloDetail().getTi_pad_parte().parse();
        DecParteNumeroFascicoloRowBean row = null;
        Integer rowIndex = null;

        // Se sono in update, prendo la riga che voglio modificare
        if (getForm().getParteNumeroFascicoloList().getStatus().equals(Status.update)) {
            row = (DecParteNumeroFascicoloRowBean) getForm().getParteNumeroFascicoloList().getTable().getCurrentRow();
            rowIndex = getForm().getParteNumeroFascicoloList().getTable().getCurrentRowIndex();
        }

        if (getForm().getParteNumeroFascicoloDetail().validate(getMessageBox())) {

            // Ricavo i campi necessari ai controlli successivi
            BigDecimal min = getForm().getParteNumeroFascicoloDetail().getNi_min_char_parte().parse();
            BigDecimal max = getForm().getParteNumeroFascicoloDetail().getNi_max_char_parte().parse();
            String dlValoreParte = getForm().getParteNumeroFascicoloDetail().getDl_valori_parte().parse();

            // Controllo Carattere separatore: viene accettato anche lo SPAZIO
            String separatore = getForm().getParteNumeroFascicoloDetail().getTi_char_sep().getValue();
            if (StringUtils.isNotEmpty(separatore) && !separatore.equals("SPAZIO")) {
                Matcher matcher = sepPattern.matcher(separatore);
                if (matcher.matches()) {
                    getMessageBox().addError("Carattere non utilizzabile come elemento separatore");
                }
            }

            // Controllo esistenza nome della parte
            if (!getMessageBox().hasError()) {
                String nomeParte = getForm().getParteNumeroFascicoloDetail().getNm_parte_numero().parse();
                List<Object> nomi = ((DecParteNumeroFascicoloTableBean) getForm().getParteNumeroFascicoloList()
                        .getTable()).toList(DecParteNumeroFascicoloTableDescriptor.COL_NM_PARTE_NUMERO);
                if (getForm().getParteNumeroFascicoloList().getStatus().equals(Status.insert)) {
                    if (nomi.contains(nomeParte)) {
                        getMessageBox().addError("Esiste gi\u00E0 nella lista una parte con nome " + nomeParte);
                    }
                } else {
                    String oldName = (row != null ? row.getNmParteNumero() : null);
                    if (!nomeParte.equals(oldName) && nomi.contains(nomeParte)) {
                        getMessageBox().addError("Esiste gi\u00E0 nella lista una parte con nome " + nomeParte);
                    }
                }
            }

            // // Controllo AGGIUNTIVO, oltre al trigger, per Tipo di Riempimento
            // if (!getMessageBox().hasError()) {
            // if (StringUtils.isNotBlank(tipoRiempimento)) {
            // if (max != null) {
            // getMessageBox().addError("Tipo di riempimento non pu\u00F2 essere valorizzato nel caso in cui 'Numero
            // massimo di caratteri' sia valorizzato o 'Caratteri ammessi' sia diverso da ALFANUMERICO o ALFABETICO");
            // } else if (StringUtils.isBlank(tiCharParte) ||
            // (!tiCharParte.equals(DecParteNumeroFascicolo.TiCharParte.ALFABETICO.name()) &&
            // !tiCharParte.equals(DecParteNumeroFascicolo.TiCharParte.ALFANUMERICO.name()))) {
            // getMessageBox().addError("Tipo di riempimento non pu\u00F2 essere valorizzato nel caso in cui 'Numero
            // massimo di caratteri' sia valorizzato o 'Caratteri ammessi' sia diverso da ALFANUMERICO o ALFABETICO");
            // }
            // }
            // }
            // Controllo Valori Accettati (dl_valori_parte)
            if (!getMessageBox().hasError()) {
                if (StringUtils.isNotBlank(dlValoreParte)) {
                    String dlValoreCopy = StringUtils.deleteWhitespace(dlValoreParte);
                    Matcher insiemeMatcher = insiemePattern.matcher(dlValoreCopy);
                    Matcher rangeMatcher = rangePattern.matcher(dlValoreCopy);
                    BigDecimal realMax = max != null ? max : new BigDecimal(9999);
                    int minInt = min.intValue();
                    int maxInt = realMax.intValue();
                    if (insiemeMatcher.matches()) {
                        if (dlValoreCopy.contains(",")) {
                            // Contiene più di un valore
                            String[] valori = StringUtils.split(dlValoreCopy, ",");
                            for (String valoreStr : valori) {
                                if (valoreStr.length() > maxInt || valoreStr.length() < minInt) {
                                    getMessageBox().addError(
                                            "I valori accettati non sono coerenti con il numero di caratteri indicato");
                                    break;
                                }
                            }
                        } else // Contiene un solo valore
                        if (dlValoreCopy.length() > maxInt || dlValoreCopy.length() < minInt) {
                            getMessageBox().addError(
                                    "I valori accettati non sono coerenti con il numero di caratteri indicato");
                        }
                    } else if (rangeMatcher.matches()) {
                        Pattern singlePattern = Pattern.compile("<(\\d+)>");
                        Matcher matcher = singlePattern.matcher(dlValoreCopy);
                        BigDecimal[] valori = new BigDecimal[2];
                        int counter = 0;
                        while (matcher.find()) {
                            String valoreStr = matcher.group(1);
                            BigDecimal valore = new BigDecimal(valoreStr);
                            valori[counter++] = valore;
                            if (valoreStr.length() > maxInt || valoreStr.length() < minInt) {
                                getMessageBox().addError(
                                        "I valori accettati non sono coerenti con il numero di caratteri indicato");
                                break;
                            }
                        }
                        if (!getMessageBox().hasError()) {
                            if (valori[0].compareTo(valori[1]) > 0) {
                                getMessageBox().addError(
                                        "Se i valori accettati sono un insieme, ogni valore deve essere separato mediante \",\" senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;");
                            }
                        }
                    } else {
                        getMessageBox().addError(
                                "Se i valori accettati sono un insieme, ogni valore deve essere separato mediante \",\" senza spazi; se i valori accettati sono definiti da un range numerico, deve assumere formato &lt;valore minimo&gt;-&lt;valore massimo&gt;");
                    }
                }
            }

            // Se tutti i controlli sono andati a buon fine, inserisco/modifico il record nella lista a video
            if (!getMessageBox().hasError()) {
                DecParteNumeroFascicoloTableBean tb = (DecParteNumeroFascicoloTableBean) getForm()
                        .getParteNumeroFascicoloList().getTable();
                tb.clearSortingRule();
                tb.addSortingRule(DecParteNumeroFascicoloTableDescriptor.COL_NI_PARTE_NUMERO);

                if (getForm().getParteNumeroFascicoloList().getStatus().equals(Status.insert)) {
                    DecParteNumeroFascicoloRowBean tmp = new DecParteNumeroFascicoloRowBean();
                    getForm().getParteNumeroFascicoloDetail().copyToBean(tmp);
                    tb.add(tmp);
                } else {
                    getForm().getParteNumeroFascicoloDetail().copyToBean(row);
                    tb.remove(rowIndex);
                    tb.add(row);
                }

                tb.sort();
                resetPartiInserimento();
                getForm().getParteNumeroFascicoloList().setStatus(Status.insert);
            }
        }

        if (getMessageBox().hasError()) {
            getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo().setValue(tipoRiempimento);
            getForm().getParteNumeroFascicoloList().getTable().setCurrentRowIndex(rowIndex);
        }

        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    @Override
    public void deleteParteNumeroFascicoloList() throws EMFError {
        DecParteNumeroFascicoloTableBean partiTableBean = (DecParteNumeroFascicoloTableBean) getForm()
                .getParteNumeroFascicoloList().getTable();
        int index = partiTableBean.getCurrentRowIndex();
        DecParteNumeroFascicoloRowBean currentRow = (DecParteNumeroFascicoloRowBean) partiTableBean.getCurrentRow();
        if (currentRow.getIdParteNumeroFascicolo() != null) {
            Set<BigDecimal> idParti = (Set<BigDecimal>) getSession().getAttribute(PARAMETER_ID_PARTI_ELIMINATE);
            if (idParti == null) {
                idParti = new HashSet<>();
            }
            idParti.add(currentRow.getIdParteNumeroFascicolo());
            getSession().setAttribute(PARAMETER_ID_PARTI_ELIMINATE, idParti);
        }
        getForm().getParteNumeroFascicoloList().remove(index);

        index = 1;
        partiTableBean.clearSortingRule();
        partiTableBean.addSortingRule(DecParteNumeroFascicoloTableDescriptor.COL_NI_PARTE_NUMERO);
        partiTableBean.sort();
        for (DecParteNumeroFascicoloRowBean row : partiTableBean) {
            row.setNiParteNumero(new BigDecimal(index++));
        }
        getForm().getParteNumeroFascicoloDetail().getNi_parte_numero().setValue(String.valueOf(index));
        getForm().getParteNumeroFascicoloDetail().getNi_parte_numero().setViewMode();

        resetPartiInserimento();
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    @Override
    public void updateParteNumeroFascicoloList() throws EMFError {
        // Recupero i dati della riga che devo modificare per andarli posi a settare nelle caselle di testo di dettaglio
        DecParteNumeroFascicoloRowBean row = (DecParteNumeroFascicoloRowBean) getForm().getParteNumeroFascicoloList()
                .getTable().getCurrentRow();

        // Riempio la combo "Coincidenza con" solo con i valori ammessi in base al valore di "Caratteri ammessi"
        // (ti_char_parte)
        String tiCharParte = row.getTiCharParte();
        getForm().getParteNumeroFascicoloDetail().getTi_parte().setDecodeMap(new DecodeMap());
        List<DecParteNumeroFascicolo.TiParte> allowed = new ArrayList<>();
        if (StringUtils.isNotBlank(tiCharParte)) {
            DecParteNumeroFascicolo.TiCharParte tipo = DecParteNumeroFascicolo.TiCharParte.valueOf(tiCharParte);
            switch (tipo) {
            case NUMERICO:
            case NUMERICO_GENERICO:
                allowed = Arrays.asList(DecParteNumeroFascicolo.TiParte.values());
                break;
            case NUMERI_ROMANI:
                allowed.addAll(EnumSet.of(CLASSIF, PROGR_FASC, PROGR_SUB_FASC));
                break;
            case ALFABETICO:
            case ALFANUMERICO:
                allowed.addAll(EnumSet.of(CLASSIF));
            case GENERICO:
                allowed.addAll(EnumSet.of(ANNO, CLASSIF));
                break;
            case PARTE_GENERICO:
                allowed.addAll(EnumSet.of(ANNO, CLASSIF, PROGR_FASC, PROGR_SUB_FASC));
                break;
            default:
                break;
            }

            // Gestione campo ti_parte ("Coincidenza con")
            BaseTable t = new BaseTable();
            DecodeMap mappa = new DecodeMap();
            for (DecParteNumeroFascicolo.TiParte all : allowed) {
                BaseRow r = new BaseRow();
                r.setString("ti_parte", all.name());
                t.add(r);
            }
            mappa.populatedMap(t, "ti_parte", "ti_parte");
            getForm().getParteNumeroFascicoloDetail().getTi_parte().setDecodeMap(mappa);
        }

        getForm().getParteNumeroFascicoloDetail().copyFromBean(row);
        // Copio il valore di ti_pad_parte anche nella combo di appoggio
        getForm().getParteNumeroFascicoloDetail().getTi_pad_parte_combo().setValue(row.getTiPadParte());

        getForm().getParteNumeroFascicoloList().setStatus(Status.update);
    }

    @Override
    public void cleanParteNumeroFascicolo() throws EMFError {
        resetPartiInserimento();
        forwardToPublisher(getDefaultInserimentoPeriodoValiditaWizardPublisher());
    }

    private void resetPartiInserimento() {
        getForm().getParteNumeroFascicoloDetail().clear();
        getForm().getParteNumeroFascicoloDetail().setEditMode();
        getForm().getParteNumeroFascicoloList().setStatus(Status.insert);
        increaseNiNumeroFascicolo();
        initParteNumeroFascicoloDetail();
    }

    @Override
    protected void postLoad() {
        super.postLoad();
        Object ogg = getForm();
        if (ogg instanceof StrutTipiFascicoloForm) {
            StrutTipiFascicoloForm form = (StrutTipiFascicoloForm) ogg;

            if (form.getTipoFascicoloList().getStatus().equals(Status.view)) {
                // Visualizzo le sezioni con i parametri flag
                form.getParametriControlloClassificazioneSection().setHidden(false);
                form.getParametriControlloCollegamentiSection().setHidden(false);
                form.getParametriControlloNumeroFascSection().setHidden(false);
                //
                form.getTipoFascicoloDetail().getControllo_formato_numero().setHidden(false);
                //
                form.getTipoFascicoloDetail().getLogEventiTipoFascicolo().setHidden(false);
            } else {
                // Nascondo le sezioni con i parametri flag
                form.getParametriControlloClassificazioneSection().setHidden(true);
                form.getParametriControlloCollegamentiSection().setHidden(true);
                form.getParametriControlloNumeroFascSection().setHidden(true);
                //
                form.getTipoFascicoloDetail().getControllo_formato_numero().setHidden(true);
                //
                form.getTipoFascicoloDetail().getLogEventiTipoFascicolo().setHidden(true);
            }
        }
    }

    /**
     * Bottone utilizzato durante il wizard di creazione periodo di validità tipo fascicolo (in particolare nei due step
     * riguardanti l'inserimento/modifica/cancellazione di un "uso modello xsd" di profilo fascicolo o profilo
     * archivistico) per poter visualizzare un modello xsd
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void visualizzaModelloXsdFascicolo() throws EMFError {
        getForm().getMetadatiProfilo().post(getRequest());
        BigDecimal idModelloXsdFascicolo = getForm().getMetadatiProfilo().getId_modello_xsd_fascicolo().parse();
        if (idModelloXsdFascicolo != null) {
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = tipoFascicoloEjb
                    .getDecModelloXsdFascicoloRowBean(idModelloXsdFascicolo);
            getForm().getModelloXsdFascicoloDetail().copyFromBean(modelloXsdFascicoloRowBean);
            forwardToPublisher(Application.Publisher.MODELLO_XSD_FASCICOLO_DETAIL);
        } else {
            getMessageBox()
                    .addWarning("Attenzione: selezionare una versione di xsd per poterne visualizzare il dettaglio");
            forwardToPublisher(getLastPublisher());
        }
    }

    private Set<BigDecimal> getModelliInVersamentoFascicoli(String parametro) {
        Set<BigDecimal> idMetadatiFascicoloPerVersFasc = new HashSet<>();

        Set<BigDecimal> idMetadatiFascicoloVariati = (Set<BigDecimal>) getSession().getAttribute(parametro);
        if (idMetadatiFascicoloVariati != null && !idMetadatiFascicoloVariati.isEmpty()) {
            for (BigDecimal idMeta : idMetadatiFascicoloVariati) {
                if (tipoFascicoloEjb.checkModelliNelPeriodoValidita(idMeta)) {
                    idMetadatiFascicoloPerVersFasc.add(idMeta);
                }
            }
        }

        return idMetadatiFascicoloPerVersFasc;
    }

    private DecUsoModelloXsdFascRowBean getUsoModelloCurrentRowBean(BigDecimal idModelloXsdFascicolo,
            CostantiDB.TiModelloXsd tiModelloXsd) throws EMFError {

        DecUsoModelloXsdFascRowBean usoModello = null;

        DecAaTipoFascicoloRowBean row = (DecAaTipoFascicoloRowBean) getForm().getAaTipoFascicoloList().getTable()
                .getCurrentRow();
        DecUsoModelloXsdFascTableBean usoModelloXsdFascTableBean = tipoFascicoloEjb
                .getVersioniXsdMetadati(row.getIdAaTipoFascicolo(), tiModelloXsd);
        Iterator<DecUsoModelloXsdFascRowBean> it = usoModelloXsdFascTableBean.iterator();
        while (it.hasNext()) {
            DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = it.next();
            if (idModelloXsdFascicolo.equals(usoModelloXsdFascRowBean.getIdModelloXsdFascicolo())) {
                usoModello = usoModelloXsdFascRowBean;
                break;
            }
        }

        return usoModello;
    }

    private DecModelloXsdFascicoloRowBean getModelloInUsoRowBean(BigDecimal idUsoModelloXsdFasc) {

        DecUsoModelloXsdFascRowBean usoModello = tipoFascicoloEjb.getDecUsoModelloXsdFascRowBean(idUsoModelloXsdFasc);

        return tipoFascicoloEjb.getDecModelloXsdFascicoloRowBean(usoModello.getIdModelloXsdFascicolo());
    }

    /**
     * Elimina un parametro di amministrazione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriAmministrazioneAaTipoFascList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriAmministrazioneAaTipoFascList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriAmministrazioneAaTipoFascList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAaTipoFasc(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di amministrazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox()
                    .addWarning("Valore sul periodo tipo fascicolo non presente: nessuna cancellazione effettuata");
        }
        try {
            loadDettaglioAaTipoFascicolo(
                    ((DecAaTipoFascicoloRowBean) getForm().getAaTipoFascicoloList().getTable().getCurrentRow())
                            .getIdAaTipoFascicolo());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento del periodo tipo fascicolo");
        }
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    /**
     * Elimina un parametro di conservazione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriConservazioneAaTipoFascList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriConservazioneAaTipoFascList()
                .getTable().getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(getForm(),
                getForm().getParametriConservazioneAaTipoFascList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAaTipoFasc(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di conservazione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox()
                    .addWarning("Valore sul periodo tipo fascicolo non presente: nessuna cancellazione effettuata");
        }
        try {
            loadDettaglioAaTipoFascicolo(
                    ((DecAaTipoFascicoloRowBean) getForm().getAaTipoFascicoloList().getTable().getCurrentRow())
                            .getIdAaTipoFascicolo());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento del periodo tipo fascicolo");
        }
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    /**
     * Elimina un parametro di gestione dalla lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteParametriGestioneAaTipoFascList() throws EMFError {
        AplParamApplicRowBean row = (AplParamApplicRowBean) getForm().getParametriGestioneAaTipoFascList().getTable()
                .getCurrentRow();
        BigDecimal idValoreParamApplic = row.getBigDecimal("id_valore_param_applic");
        /*
         * Codice aggiuntivo per il logging...
         */
        LogParam param = SpagoliteLogUtil.getLogParam(configurationHelper.getParamApplicApplicationName(),
                getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        param.setNomeAzione(
                SpagoliteLogUtil.getDetailActionNameDelete(getForm(), getForm().getParametriGestioneAaTipoFascList()));
        if (idValoreParamApplic != null) {
            if (amministrazioneEjb.deleteParametroAaTipoFasc(param, idValoreParamApplic)) {
                getMessageBox().addInfo("Parametro di gestione eliminato con successo");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getMessageBox()
                    .addWarning("Valore sul periodo tipo fascicolo non presente: nessuna cancellazione effettuata");
        }
        try {
            loadDettaglioAaTipoFascicolo(
                    ((DecAaTipoFascicoloRowBean) getForm().getAaTipoFascicoloList().getTable().getCurrentRow())
                            .getIdAaTipoFascicolo());
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento del periodo tipo fascicolo");
        }
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    @Override
    public void parametriAmministrazioneAaTipoFascButton() throws Throwable {
        BigDecimal idAaTipoFascicolo = ((BaseRowInterface) getForm().getAaTipoFascicoloList().getTable()
                .getCurrentRow()).getBigDecimal("id_aa_tipo_fascicolo");
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, true, true, true);
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        getForm().getRicercaParametriAaTipoFasc().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriAaTipoFasc().getFunzione().reset();
        getForm().getRicercaParametriAaTipoFasc().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "amministrazione");
        forwardToPublisher(Application.Publisher.PARAMETRI_AA_TIPO_FASC);
    }

    @Override
    public void parametriConservazioneAaTipoFascButton() throws Throwable {
        BigDecimal idAaTipoFascicolo = ((BaseRowInterface) getForm().getAaTipoFascicoloList().getTable()
                .getCurrentRow()).getBigDecimal("id_aa_tipo_fascicolo");
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, false, true, true);
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        getForm().getRicercaParametriAaTipoFasc().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriAaTipoFasc().getFunzione().reset();
        getForm().getRicercaParametriAaTipoFasc().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "conservazione");
        forwardToPublisher(Application.Publisher.PARAMETRI_AA_TIPO_FASC);
    }

    @Override
    public void parametriGestioneAaTipoFascButton() throws Throwable {
        BigDecimal idAaTipoFascicolo = ((BaseRowInterface) getForm().getAaTipoFascicoloList().getTable()
                .getCurrentRow()).getBigDecimal("id_aa_tipo_fascicolo");
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, false, false, true);
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        getForm().getRicercaParametriAaTipoFasc().setEditMode();
        BaseTable tb = struttureEjb.getFunzioneParametriTableBean();
        getForm().getRicercaParametriAaTipoFasc().getFunzione().reset();
        getForm().getRicercaParametriAaTipoFasc().getFunzione()
                .setDecodeMap(DecodeMap.Factory.newInstance(tb, "funzione", "funzione"));
        getSession().setAttribute("provenienzaParametri", "gestione");
        forwardToPublisher(Application.Publisher.PARAMETRI_AA_TIPO_FASC);
    }

    private void salvaParametriAaTipoFasc() throws EMFError {
        getForm().getParametriAmministrazioneAaTipoFascList().post(getRequest());
        getForm().getParametriConservazioneAaTipoFascList().post(getRequest());
        getForm().getParametriGestioneAaTipoFascList().post(getRequest());

        BigDecimal idAaTipoFascicolo = ((BaseRowInterface) getForm().getAaTipoFascicoloList().getTable()
                .getCurrentRow()).getBigDecimal("id_aa_tipo_fascicolo");
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());

        // Controllo valori possibili su struttura
        AplParamApplicTableBean parametriAmministrazione = (AplParamApplicTableBean) getForm()
                .getParametriAmministrazioneAaTipoFascList().getTable();
        AplParamApplicTableBean parametriConservazione = (AplParamApplicTableBean) getForm()
                .getParametriConservazioneAaTipoFascList().getTable();
        AplParamApplicTableBean parametriGestione = (AplParamApplicTableBean) getForm()
                .getParametriGestioneAaTipoFascList().getTable();
        String error = amministrazioneEjb.checkParametriAmmessi("aa_tipo_fascicolo", parametriAmministrazione,
                parametriConservazione, parametriGestione);
        if (error != null) {
            getMessageBox().addError(error);
        }

        if (!getMessageBox().hasError()) {
            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null,
                                null, CostantiDB.TipoAplVGetValAppart.APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarSave(
                                getForm().getAaTipoFascicoloDetail().getStatus().equals(Status.update)));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                tipoFascicoloEjb.saveParametriAaTipoFascicolo(param, parametriAmministrazione, parametriConservazione,
                        parametriGestione, idAaTipoFascicolo);
                getMessageBox().addInfo("Parametri periodo tipo fascicolo salvati con successo");
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getAaTipoFascicoloDetail().setViewMode();
                getForm().getAaTipoFascicoloDetail().setStatus(Status.view);
                getForm().getParametriAmministrazioneAaTipoFascList().setViewMode();
                getForm().getParametriConservazioneAaTipoFascList().setViewMode();
                getForm().getParametriGestioneAaTipoFascList().setViewMode();
                loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, true, false, false, false);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);

        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void ricercaParametriAaTipoFascButton() throws EMFError {
        getForm().getRicercaParametriAaTipoFasc().post(getRequest());
        List<String> funzione = getForm().getRicercaParametriAaTipoFasc().getFunzione().parse();
        BigDecimal idAaTipoFascicolo = ((BaseRowInterface) getForm().getAaTipoFascicoloList().getTable()
                .getCurrentRow()).getBigDecimal("id_aa_tipo_fascicolo");
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        try {
            getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenzienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                if (provenzienzaParametri.equals("amministrazione")) {
                    loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(),
                            tipoFascicoloRowBean.getIdStrut(), idAaTipoFascicolo, funzione, false, true, true, true);
                } else if (provenzienzaParametri.equals("conservazione")) {
                    loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(),
                            tipoFascicoloRowBean.getIdStrut(), idAaTipoFascicolo, funzione, false, false, true, true);
                } else if (provenzienzaParametri.equals("gestione")) {
                    loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(),
                            tipoFascicoloRowBean.getIdStrut(), idAaTipoFascicolo, funzione, false, false, false, true);
                }
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il caricamento dei parametri periodo tipo fascicolo");
        }
        forwardToPublisher(Application.Publisher.PARAMETRI_AA_TIPO_FASC);
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

                if (rowBean.getString("ds_valore_param_applic_ambiente") != null)
                    rowBean.setString("ds_valore_param_applic_ambiente", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_strut") != null)
                    rowBean.setString("ds_valore_param_applic_strut", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_amm") != null)
                    rowBean.setString("ds_valore_param_applic_aa_tipo_fascicolo_amm", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_gest") != null)
                    rowBean.setString("ds_valore_param_applic_aa_tipo_fascicolo_gest", Constants.OBFUSCATED_STRING);

                if (rowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_cons") != null)
                    rowBean.setString("ds_valore_param_applic_aa_tipo_fascicolo_cons", Constants.OBFUSCATED_STRING);

            }
        }

        return paramApplicTableBean;
    }

}
