/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
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
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
        ExecutionHistory executionHistory = SessionManager.getLastExecutionHistory(getSession());
        if (executionHistory != null && executionHistory.isForward()
                && executionHistory.getName().equals(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL)) {
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
                    initMetadatiProfilo();
                    loadDettaglioXsdAaTipoFascicolo();
                    if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
                        getForm().getMetadatiProfiloFascicoloList().setStatus(Status.update);
                        getForm().getMetadatiProfiloDetail().setStatus(Status.update);
                        getForm().getMetadatiProfiloDetail().setEditMode();
                        getForm().getMetadatiProfiloDetail().getTi_modello_xsd().setReadonly(true);
                        getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setReadonly(true);
                        getForm().getMetadatiProfiloFascicoloList().setUserOperations(true, true, true, true);
                    } else if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)) {
                        getForm().getMetadatiProfiloFascicoloList().setStatus(Status.view);
                        getForm().getMetadatiProfiloFascicoloList().setUserOperations(true, true, true, false);
                        getForm().getMetadatiProfiloDetail().getTi_modello_xsd().setReadonly(true);
                        getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setReadonly(true);
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
                } else if (getTableName().equals(getForm().getParteNumeroFascicoloList().getName())) {
                    loadPartiNumeroFascicolo();
                    if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_UPDATE)) {
                        getForm().getParteNumeroFascicoloList().setStatus(Status.update);
                        getForm().getParteNumeroFascicoloDetail().setStatus(Status.update);
                        getForm().getParteNumeroFascicoloDetail().setEditMode();
                        getForm().getParteNumeroFascicoloList().setUserOperations(true, true, true, true);
                    } else {
                        getForm().getParteNumeroFascicoloList().setStatus(Status.view);
                        getForm().getParteNumeroFascicoloDetail().setViewMode();
                        getForm().getParteNumeroFascicoloList().setUserOperations(true, true, true, true);

                    }
                }
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getMessage());
        }
    }

    private void loadDettaglioTipoFascicolo(BigDecimal idTipoFascicolo) {
        try {
            // Caricamento dettaglio tipo fascicolo e parametri ultimo periodo validit\u00E0 
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
            // Caricamento dettaglio periodo validit\u00E0  tipo fascicolo
            DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                    .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);
            getForm().getAaTipoFascicoloDetail().copyFromBean(aaTipoFascicoloRowBean);

            // Carico le liste del dettaglio periodo validit\u00E0  tipo fascicolo
            loadDettaglioAaTipoFascicoloLists(idAaTipoFascicolo);

            // Parametri
            DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb
                    .getDecTipoFascicoloRowBean(aaTipoFascicoloRowBean.getIdTipoFascicolo());
            OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
            OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());

            // Parametri
            disabileAaTipoFascicoloParametersSections(false);

            // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
            // idAaTipoFascicolo, null, false, false, false, false);
            loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                    idAaTipoFascicolo, null, true, false,
                    getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
            loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                    idAaTipoFascicolo, null, true, false,
                    getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
            loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                    idAaTipoFascicolo, null, true, false,
                    getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());

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

            getSession().removeAttribute("provenienzaParametri");

            getForm().getMetadatiProfiloFascicoloList().setUserOperations(true, true, true, true);

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

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

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

    private void loadDettaglioXsdAaTipoFascicolo(BigDecimal idUsoModelloXsdFasc) throws EMFError {
        try {
            // Caricamento dettaglio xsd periodo validit\u00E0  tipo fascicolo
            DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = tipoFascicoloEjb
                    .getVersioneXsdMetadati(idUsoModelloXsdFasc);
            getForm().getMetadatiProfiloDetail().copyFromBean(usoModelloXsdFascRowBean);

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

        getForm().getAttribFascicoloList().setUserOperations(true, true, true, true);

        getForm().getAttribFascicoloList().setStatus(Status.view);

    }

    private void loadDettaglioXsdAaTipoFascicolo() throws EMFError {
        BigDecimal idUsoModelloXsdFasc = ((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloFascicoloList()
                .getTable()).getCurrentRow().getIdUsoModelloXsdFasc();

        loadDettaglioXsdAaTipoFascicolo(idUsoModelloXsdFasc);

        DecodeMap mappaModelliXsd = new DecodeMap();
        mappaModelliXsd.populatedMap(
                tipoFascicoloEjb.getDecModelloXsdFascicoloTableBeanByTiModelloXsd(
                        getForm().getMetadatiProfiloDetail().getTi_modello_xsd().parse()),
                "id_modello_xsd_fascicolo", "cd_xsd");
        getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setDecodeMap(mappaModelliXsd);
    }

    private void loadPartiNumeroFascicolo() throws EMFError {
        // Inizializzo le combo
        initParteNumeroFascicoloDetail();
        increaseNiNumeroFascicolo();
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
        // Periodi di validit\u00E0  del tipo fascicolo
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
        // Errori sul periodo di validit\u00E0  del tipo fascicolo
        DecErrAaTipoFascicoloTableBean errAaTipoFascicoloTableBean = tipoFascicoloEjb
                .getDecErrAaTipoFascicoloTableBeanPerIntervallo(idAaTipoFascicolo);
        getForm().getErrAaTipoFascicoloList().setTable(errAaTipoFascicoloTableBean);
        getForm().getErrAaTipoFascicoloList().getTable().first();
        getForm().getErrAaTipoFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        // Versioni XSD dei metadati di profilo fascicolo definiti sul periodo di validit\u00E0 
        DecUsoModelloXsdFascTableBean usoModelloXsdFascTableBean = tipoFascicoloEjb
                .getVersioniXsdMetadati(idAaTipoFascicolo);
        getForm().getMetadatiProfiloFascicoloList().setTable(usoModelloXsdFascTableBean);
        getForm().getMetadatiProfiloFascicoloList().getTable().first();
        getForm().getMetadatiProfiloFascicoloList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

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
            } else if (getTableName().equals(getForm().getMetadatiProfiloFascicoloList().getName())) {

                getForm().getMetadatiProfiloDetail().setStatus(Status.view);
                getForm().getMetadatiProfiloDetail().setViewMode();
                forwardToPublisher(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL);
            } else if (getTableName().equals(getForm().getParteNumeroFascicoloList().getName())) {

                getForm().getParteNumeroFascicoloList().setStatus(Status.view);
                getForm().getParteNumeroFascicoloDetail().setViewMode();
                forwardToPublisher(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL);
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
        } else if (getLastPublisher().equals(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL)
                && getForm().getAaTipoFascicoloList().getStatus() != null
                && getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)) {
            initAaTipoFascicoloDetail();
            BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                    .getCurrentRow().getIdAaTipoFascicolo();
            try {
                loadDettaglioAaTipoFascicolo(idAaTipoFascicolo);
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
            setAaTipoFascicoloStatus(Status.view);
            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        } else if (getLastPublisher().equals(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL)
                && getForm().getMetadatiProfiloFascicoloList().getStatus() != null
                && getForm().getMetadatiProfiloFascicoloList().getStatus().equals(Status.update)) {
            initMetadatiProfilo();
            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        } else if (getLastPublisher().equals(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL)
                && getForm().getParteNumeroFascicoloList().getStatus() != null
                && getForm().getParteNumeroFascicoloList().getStatus().equals(Status.update)) {
            initParteNumeroFascicoloDetail();
            increaseNiNumeroFascicolo();
            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        } else if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)
                && getForm().getAaTipoFascicoloDetail().getStatus() != null) {
            ricercaParametriAaTipoFascButton();
            getForm().getAaTipoFascicoloDetail().setStatus(Status.view);
            setViewModeListeParametri();
            forwardToPublisher(Application.Publisher.PARAMETRI_AA_TIPO_FASC);
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
            getForm().getAaTipoFascicoloDetail().reset();
            getForm().getAaTipoFascicoloDetail().setEditMode();

            // init combo
            // Azzero le liste
            getForm().getErrAaTipoFascicoloList().setTable(new DecErrAaTipoFascicoloTableBean());
            getForm().getParteNumeroFascicoloList().setTable(new DecParteNumeroFascicoloTableBean());
            getForm().getMetadatiProfiloFascicoloList().setTable(new DecUsoModelloXsdFascTableBean());

            // Apro le section
            getForm().getParametriControlloClassificazioneSection().setLoadOpened(true);
            getForm().getParametriControlloCollegamentiSection().setLoadOpened(true);
            getForm().getParametriControlloNumeroFascSection().setLoadOpened(true);

            disabileAaTipoFascicoloParametersSections(true);

            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        } else if (getTableName().equals(getForm().getMetadatiProfiloFascicoloList().getName())) {
            getForm().getMetadatiProfiloFascicoloList().setStatus(Status.insert);
            getForm().getMetadatiProfiloDetail().setStatus(Status.insert);

            // DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
            // getForm().getMetadatiProfiloDetail().copyFromBean(usoModelloXsdFascRowBean);
            getForm().getMetadatiProfiloDetail().reset();
            getForm().getMetadatiProfiloDetail().setEditMode();
            initMetadatiProfilo();
            getForm().getMetadatiProfiloDetail().getTi_modello_xsd().setReadonly(false);
            getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setReadonly(false);
            // getForm().getMetadatiProfiloFascicoloList().setUserOperations(true, true, true, true);
            forwardToPublisher(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL);
        } else if (getTableName().equals(getForm().getParteNumeroFascicoloList().getName())) {
            // MAC #34027 - controllo per proseguire o meno
            DecParteNumeroFascicoloTableBean tb = (DecParteNumeroFascicoloTableBean) getForm()
                    .getParteNumeroFascicoloList().getTable();
            // Recupero la colonna TiCharParte dal tablebean ed eseguo il controllo sul tipo GENERICO
            List<Object> tipiCarattere = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_TI_CHAR_PARTE);
            if (tipiCarattere.size() == 1 && tipiCarattere.contains(TiCharParte.GENERICO.name())) {
                getMessageBox().addError(
                        "Impossibile inserire ulteriori parti in quanto ne \u00E8 già presente una con il carattere GENERICO");
                forwardToPublisher(getLastPublisher());
            } else {
                getForm().getParteNumeroFascicoloList().setStatus(Status.insert);
                getForm().getParteNumeroFascicoloDetail().setStatus(Status.insert);

                getForm().getParteNumeroFascicoloDetail().reset();
                getForm().getParteNumeroFascicoloDetail().setEditMode();
                // Inizializzo le combo
                initParteNumeroFascicoloDetail();
                increaseNiNumeroFascicolo();
                forwardToPublisher(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL);
            }
        }
    }

    @Override
    public void updateTipoFascicoloList() throws EMFError {

        String nmTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList().getTable())
                .getCurrentRow().getNmTipoFascicolo();
        if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
            getMessageBox().addError("Attenzione: il tipo fascicolo sconosciuto non pu\u00F2 essere modificato");
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

        getForm().getAaTipoFascicoloDetail().reset();
        BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdAaTipoFascicolo();
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);

        // Recupero i dati di dettaglio del periodo tipo fascicolo
        getForm().getAaTipoFascicoloDetail().reset();
        getForm().getAaTipoFascicoloDetail().setEditMode();
        getForm().getAaTipoFascicoloDetail().copyFromBean(aaTipoFascicoloRowBean);

        // Apro le section
        getForm().getParametriControlloClassificazioneSection().setLoadOpened(true);
        getForm().getParametriControlloCollegamentiSection().setLoadOpened(true);
        getForm().getParametriControlloNumeroFascSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    @Override
    public void updateAaTipoFascicoloDetail() throws EMFError {
        if (getSession().getAttribute("provenienzaParametri") == null) {
            getForm().getAaTipoFascicoloList().setStatus(Status.update);
            getForm().getAaTipoFascicoloDetail().setStatus(Status.update);

            getForm().getAaTipoFascicoloDetail().reset();
            BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                    .getCurrentRow().getIdAaTipoFascicolo();
            DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = tipoFascicoloEjb
                    .getDecAaTipoFascicoloRowBean(idAaTipoFascicolo);

            // Recupero i dati di dettaglio del periodo tipo fascicolo
            getForm().getAaTipoFascicoloDetail().reset();
            getForm().getAaTipoFascicoloDetail().setEditMode();
            getForm().getAaTipoFascicoloDetail().copyFromBean(aaTipoFascicoloRowBean);

            // Apro le section
            getForm().getParametriControlloClassificazioneSection().setLoadOpened(true);
            getForm().getParametriControlloCollegamentiSection().setLoadOpened(true);
            getForm().getParametriControlloNumeroFascSection().setLoadOpened(true);
            forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        } else {
            if (getSession().getAttribute("provenienzaParametri") != null) {
                String provenienzaParametri = (String) getSession().getAttribute("provenienzaParametri");
                try {
                    if (provenienzaParametri.equals("amministrazione")) {

                        setEditModeParametriAmministrazione();

                    } else if (provenienzaParametri.equals("conservazione")) {
                        setEditModeParametriConservazione();
                    } else if (provenienzaParametri.equals("gestione")) {
                        setEditModeParametriGestione();
                    }
                    forwardToPublisher(Application.Publisher.PARAMETRI_AA_TIPO_FASC);
                } catch (Throwable ex) {
                    getMessageBox().addError("Errore durante il caricamento dei parametri");
                }
            }
        }
    }

    private void setEditModeParametriAmministrazione() {
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        getForm().getParametriAmministrazioneAaTipoFascList().setStatus(Status.update);
        getForm().getParametriConservazioneAaTipoFascList().setStatus(Status.update);
        getForm().getParametriGestioneAaTipoFascList().setStatus(Status.update);
        getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm()
                .setEditMode();
        getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                .setEditMode();
        getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest().setEditMode();
    }

    private void setEditModeParametriConservazione() {
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        getForm().getParametriConservazioneAaTipoFascList().setStatus(Status.update);
        getForm().getParametriGestioneAaTipoFascList().setStatus(Status.update);
        getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                .setEditMode();
        getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest().setEditMode();
    }

    private void setEditModeParametriGestione() {
        getForm().getAaTipoFascicoloDetail().setStatus(Status.update);
        getForm().getParametriGestioneAaTipoFascList().setStatus(Status.update);
        getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest().setEditMode();
    }

    private void setViewModeListeParametri() {
        getForm().getAaTipoFascicoloDetail().setStatus(Status.view);
        getForm().getAaTipoFascicoloDetail().setStatus(Status.view);
        getForm().getParametriConservazioneAaTipoFascList().setStatus(Status.view);
        getForm().getParametriGestioneAaTipoFascList().setStatus(Status.view);
        getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm()
                .setViewMode();
        getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                .setViewMode();
        getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest().setViewMode();
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
    public void updateMetadatiProfiloFascicoloList() throws EMFError {
        getForm().getMetadatiProfiloDetail().reset();
        getForm().getMetadatiProfiloDetail()
                .copyFromBean(((DecUsoModelloXsdFascTableBean) getForm().getMetadatiProfiloFascicoloList().getTable())
                        .getCurrentRow());
        initMetadatiProfilo();
        loadDettaglioXsdAaTipoFascicolo();
        forwardToPublisher(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL);
    }

    @Override
    public void saveDettaglio() throws EMFError {

        if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)) {
            salvaParametriAaTipoFasc();
        } else if (getLastPublisher().equals(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL)) {
            saveModelloXsdFascicolo();
        } else if (getLastPublisher().equals(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL)) {
            saveParteNumeroFascicolo();
        } else if (getTableName().equals(getForm().getTipoFascicoloList().getName())
                || getTableName().equals(getForm().getTipoFascicoloDetail().getName())) {
            saveTipoFascicolo();
        } else if (getTableName().equals(getForm().getAaTipoFascicoloList().getName())
                || getTableName().equals(getForm().getAaTipoFascicoloDetail().getName())) {
            savePeriodoValiditaFascicolo();
        }
    }

    private boolean saveModelloXsdFascicolo() throws EMFError {
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
        DecUsoModelloXsdFascRowBean metadatiProfiloFascicoloRowBean = new DecUsoModelloXsdFascRowBean();

        getForm().getMetadatiProfiloFascicoloList().post(getRequest());
        forwardToPublisher(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL);
        if (getForm().getMetadatiProfiloDetail().postAndValidate(getRequest(), getMessageBox())) {

            getForm().getAaTipoFascicoloDetail().copyToBean(aaTipoFascicoloRowBean);
            getForm().getMetadatiProfiloDetail().copyToBean(metadatiProfiloFascicoloRowBean);

            try {
                if (!checkModelloXsd()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                            SpagoliteLogUtil.getToolbarSave(
                                    getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();
                    tipoFascicoloEjb.saveDecModelloXsdFascicolo(param, idTipoFascicolo, aaTipoFascicoloRowBean,
                            metadatiProfiloFascicoloRowBean);
                    getMessageBox().addInfo("Modello Xsd salvato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    setAaTipoFascicoloStatus(Status.view);
                    setInitialStateSections();
                    goBackTo(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
                    forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError("Modello Xsd non pu\u00F2 essere salvato: " + ex.getDescription());
            } catch (Exception ex) {
                logger.error("Eccezione generica nel salvataggio del Modello Xsd", ex);
                getMessageBox().addError("Si \u00E8 verificata un'eccezione nel salvataggio del Modello Xsd");
            }
            postLoad();
        }
        return !getMessageBox().hasError();
    }

    private boolean saveParteNumeroFascicolo() throws EMFError {
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
        DecParteNumeroFascicoloRowBean partenumeroFascicoloRowBean = new DecParteNumeroFascicoloRowBean();

        getForm().getParteNumeroFascicoloList().post(getRequest());
        forwardToPublisher(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL);
        if (getForm().getParteNumeroFascicoloDetail().postAndValidate(getRequest(), getMessageBox())) {

            getForm().getAaTipoFascicoloDetail().copyToBean(aaTipoFascicoloRowBean);
            getForm().getParteNumeroFascicoloDetail().copyToBean(partenumeroFascicoloRowBean);

            try {
                /*
                 * Codice aggiuntivo per il logging...
                 */
                LogParam param = SpagoliteLogUtil.getLogParam(
                        configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarSave(
                                getForm().getParteNumeroFascicoloList().getStatus().equals(Status.update)));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();

                DecParteNumeroFascicoloTableBean tb = getDecParteNumeroFascicoloTableBeanPerControlli(
                        partenumeroFascicoloRowBean);
                tb.clearSortingRule();
                tb.addSortingRule(DecParteNumeroFascicoloTableDescriptor.COL_NI_PARTE_NUMERO);
                tb.sort();

                // Recupero la colonna TiCharParte dal tablebean ed eseguo il controllo sul tipo GENERICO
                if (!getMessageBox().hasError()) {
                    List<Object> tipiCarattere = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_TI_CHAR_PARTE);
                    if (tipiCarattere.size() > 1 && tipiCarattere.contains(TiCharParte.GENERICO.name())) {
                        getMessageBox().addError(
                                "Il carattere GENERICO \u00E8 ammesso solo se il tipo fascicolo si compone di una sola parte");
                    }
                }
                /* Controllo che i numeri d'ordine delle parti siano differenti */
                if (!getMessageBox().hasError()) {
                    List<Object> progressivi = tb.toList(DecParteNumeroFascicoloTableDescriptor.COL_NI_PARTE_NUMERO);
                    Set<Object> set = new HashSet<>(progressivi);
                    if (set.size() < progressivi.size()) {
                        getMessageBox().addError("Almeno un numero d'ordine \u00E8 definito su pi\u00F9 di una parte");
                    }
                }

                /*
                 * Controllo se vi \u00E8 pi\u00F9 di una parte in cui ni_max_char_parte non \u00E8 stato valorizzato e
                 * se riguarda l'ultima parte
                 */
                if (!getMessageBox().hasError()) {
                    List<Object> niMaxCharParti = tb.toList(
                            DecParteNumeroFascicoloTableDescriptor.COL_NI_MAX_CHAR_PARTE,
                            new SortingRule[] { SortingRule.getAscending(
                                    DecParteNumeroFascicoloRowBean.TABLE_DESCRIPTOR.COL_NI_PARTE_NUMERO) });
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
                            BigDecimal niMinCharParte = ((DecParteNumeroFascicoloRowBean) tb.getRow(i))
                                    .getNiMinCharParte();
                            BigDecimal niMaxCharParte = ((DecParteNumeroFascicoloRowBean) tb.getRow(i))
                                    .getNiMaxCharParte();
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
                                // Se la somma dei record vale pi\u00F9 di 1
                                getMessageBox().addError(
                                        "La coincidenza con il progressivo pu\u00F2 essere valorizzata solo su una parte del tipo fascicolo");
                                break;
                            }
                        }
                    }
                }

                if (!getMessageBox().hasError()) {
                    tipoFascicoloEjb.saveDecParteNumeroFascicolo(param, idTipoFascicolo, aaTipoFascicoloRowBean,
                            partenumeroFascicoloRowBean);

                    getMessageBox().addInfo("Parte numero fascicolo salvato con successo");
                    getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                    setAaTipoFascicoloStatus(Status.view);
                    setInitialStateSections();
                    goBackTo(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
                    forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError("Parte numero fascicolo non pu\u00F2 essere salvata: " + ex.getDescription());
            } catch (Exception ex) {
                logger.error("Eccezione generica nel salvataggio della Parte numero fascicolo", ex);
                getMessageBox()
                        .addError("Si \u00E8 verificata un'eccezione nel salvataggio della Parte numero fascicolo");
            }
            postLoad();
        }
        return !getMessageBox().hasError();
    }

    private DecParteNumeroFascicoloTableBean getDecParteNumeroFascicoloTableBeanPerControlli(
            DecParteNumeroFascicoloRowBean partenumeroFascicoloRowBean) {
        DecParteNumeroFascicoloTableBean tb = (DecParteNumeroFascicoloTableBean) getForm().getParteNumeroFascicoloList()
                .getTable();

        DecParteNumeroFascicoloTableBean tbForCheck = new DecParteNumeroFascicoloTableBean();
        tbForCheck.addAll(tb);
        // Id nullo, significa che il record è nuovo
        if (partenumeroFascicoloRowBean.getIdParteNumeroFascicolo() == null) {
            tbForCheck.add(partenumeroFascicoloRowBean);
        } // altrimenti il record già esiste e va "sostituito" con quello modificato per i controlli
        else {
            for (DecParteNumeroFascicoloRowBean rb : tbForCheck) {
                if (rb.getIdParteNumeroFascicolo().equals(partenumeroFascicoloRowBean.getIdParteNumeroFascicolo())) {
                    rb.copyFromBaseRow(partenumeroFascicoloRowBean);
                }
            }
        }
        return tbForCheck;
    }

    private boolean savePeriodoValiditaFascicolo() throws EMFError {
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
        BigDecimal idTipoFascicolo = getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse();
        getForm().getParametriAmministrazioneAaTipoFascList().post(getRequest());
        getForm().getParametriConservazioneAaTipoFascList().post(getRequest());
        getForm().getParametriGestioneAaTipoFascList().post(getRequest());
        if (getForm().getAaTipoFascicoloDetail().postAndValidate(getRequest(), getMessageBox())) {
            getForm().getAaTipoFascicoloDetail().copyToBean(aaTipoFascicoloRowBean);
            aaTipoFascicoloRowBean.setIdTipoFascicolo(idTipoFascicolo);
            // Prendo lo stesso parametro utilizzato per i periodi dei registri
            String annoValidoMinimo = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.REG_ANNO_VALID_MINIMO);

            BigDecimal ini = getForm().getAaTipoFascicoloDetail().getAa_ini_tipo_fascicolo().parse();
            BigDecimal fin = getForm().getAaTipoFascicoloDetail().getAa_fin_tipo_fascicolo().parse();
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

                if (!getMessageBox().hasError()) {

                    if (fin == null) {
                        fin = new BigDecimal(9999);
                    }

                    if (ini.compareTo(fin) > 0) {
                        getMessageBox().addError(
                                "Errore di compilazione: anno di inizio validit\u00E0  superiore ad anno di fine validit\u00E0 ");
                    }

                    if (tipoFascicoloEjb.existPeriodiValiditaSovrappostiFascicoli(
                            aaTipoFascicoloRowBean.getIdAaTipoFascicolo(), idTipoFascicolo, ini, fin)) {
                        getMessageBox().addError(
                                "Errore di compilazione: range inserito sovrapposto a range gi\u00E0  presenti");
                    }
                }

                if (!getMessageBox().hasError()) {
                    try {
                        /*
                         * Codice aggiuntivo per il logging...
                         */
                        LogParam param = SpagoliteLogUtil.getLogParam(
                                configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                                getUser().getUsername(), SpagoliteLogUtil.getPageName(this),
                                SpagoliteLogUtil.getToolbarSave(
                                        getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)));
                        param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                        if (getForm().getAaTipoFascicoloList().getStatus().equals(Status.update)) {
                            tipoFascicoloEjb.saveDecAaTipoFascicolo(param, aaTipoFascicoloRowBean);
                            tipoFascicoloEjb
                                    .checkFascicoliNelPeriodoValidita(aaTipoFascicoloRowBean.getIdAaTipoFascicolo());
                        } else {
                            Date dtIstituz = getForm().getTipoFascicoloDetail().getDt_istituz().parse();
                            Date dtSoppres = getForm().getTipoFascicoloDetail().getDt_soppres().parse();
                            BigDecimal idAaTipoFascicolo = tipoFascicoloEjb.saveDecAaTipoFascicolo(param,
                                    idTipoFascicolo, aaTipoFascicoloRowBean, dtIstituz, dtSoppres);
                            if (idAaTipoFascicolo != null) {
                                tipoFascicoloEjb.checkFascicoliNelPeriodoValidita(idAaTipoFascicolo);
                            } else {
                                throw new ParerUserError(
                                        "Errore inaspettato: periodo di validit\u00E0 non salvato correttamente");
                            }
                            loadDettaglioAaTipoFascicolo(idAaTipoFascicolo);
                        }
                        getMessageBox().addInfo("Periodo di validit\u00E0 tipo fascicolo salvato con successo");
                        getMessageBox().setViewMode(MessageBox.ViewMode.plain);
                        setAaTipoFascicoloStatus(Status.view);
                        setInitialStateSections();

                    } catch (ParerUserError ex) {
                        getMessageBox()
                                .addError("Il periodo di validit\u00E0 del tipo fascicolo non pu\u00F2 essere salvato: "
                                        + ex.getDescription());
                    } catch (Exception ex) {
                        logger.error(
                                "Eccezione generica nel salvataggio del periodo di validit\u00E0 del tipo fascicolo",
                                ex);
                        getMessageBox().addError(
                                "Si \u00E8 verificata un'eccezione nel salvataggio del periodo di validit\u00E0 del tipo fascicolo");
                    }
                }
            }
            postLoad();
        }
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        return !getMessageBox().hasError();
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
                            "La data di disattivazione non pu\u00F2 essere inferiore alla data di attivazione del tipo fascicolo");
                }
                if (getForm().getTipoFascicoloList().getStatus().equals(Status.insert)) {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, 0);
                    c.set(Calendar.MINUTE, 0);
                    c.set(Calendar.SECOND, 0);
                    c.set(Calendar.MILLISECOND, 0);
                    if (dtIstituz.before(c.getTime())) {
                        getMessageBox().addError(
                                "La data di attivazione non pu\u00F2 essere antecedente alla data di inserimento del tipo fascicolo nel Sistema");
                    }
                }

                if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
                    getMessageBox().addError(
                            "Attenzione: non \u00E8 possibile inserire un fascicolo di nome 'Tipo fascicolo sconosciuto'");
                }

                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
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

    private boolean checkModelloXsd() throws EMFError {
        if (getForm().getMetadatiProfiloDetail().postAndValidate(getRequest(), getMessageBox())) {
            List<Object> tiModelloXsdByList = ((DecUsoModelloXsdFascTableBean) getForm()
                    .getMetadatiProfiloFascicoloList().getTable()).toList("ti_modello_xsd");
            String cdXsdDaIns = getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().getDecodedValue()
                    .split("-")[0].trim();
            if (getForm().getMetadatiProfiloFascicoloList().getStatus().equals(Status.insert)) {
                if (tiModelloXsdByList.contains(getForm().getMetadatiProfiloDetail().getTi_modello_xsd().getValue())) {
                    for (DecUsoModelloXsdFascRowBean row : ((DecUsoModelloXsdFascTableBean) getForm()
                            .getMetadatiProfiloFascicoloList().getTable())) {
                        // MEV #27247: Eliminato il controllo sul campo “Standard”
                        // (Solo un xsd di metadati di profilo fascicolo pu\u00F2 essere definito come standard) poiché
                        // questa informazione è vincolata dalla
                        // versione

                        // Controllo non esista gi\u00E0  lo stesso cd xsd in lista
                        if (row.getString("ti_modello_xsd")
                                .equals(getForm().getMetadatiProfiloDetail().getTi_modello_xsd().getValue())
                                && row.getString("cd_xsd").equals(cdXsdDaIns)) {
                            getMessageBox()
                                    .addError("Esiste gi\u00E0 nella lista un xsd con codice versione " + cdXsdDaIns);
                            break;
                        }
                    }
                }
            }
        }
        return getMessageBox().hasError();
    }

    @Override
    public void deleteTipoFascicoloList() throws EMFError {
        BaseRowInterface currentRow = getForm().getTipoFascicoloList().getTable().getCurrentRow();
        BigDecimal idTipoFascicolo = currentRow.getBigDecimal("id_tipo_fascicolo");
        int riga = getForm().getTipoFascicoloList().getTable().getCurrentRowIndex();
        // Di bonniana memoria, della serie "la prudenza non \u00E8 mai troppa"...
        // "eseguo giusto un controllo per verificare che io stia prendendo la riga giusta se sono nel dettaglio"
        if (getLastPublisher().equals(Application.Publisher.TIPO_FASCICOLO_DETAIL)) {
            if (!idTipoFascicolo.equals(getForm().getTipoFascicoloDetail().getId_tipo_fascicolo().parse())) {
                getMessageBox().addError("Eccezione imprevista nell'eliminazione del tipo fascicolo");
            }
        }

        String nmTipoFascicolo = ((DecTipoFascicoloTableBean) getForm().getTipoFascicoloList().getTable())
                .getCurrentRow().getNmTipoFascicolo();
        if (nmTipoFascicolo.equals("Tipo fascicolo sconosciuto")) {
            getMessageBox().addError("Attenzione: il tipo fascicolo sconosciuto non pu\u00F2 essere eliminato");
        }

        if (!getMessageBox().hasError() && idTipoFascicolo != null) {
            try {
                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
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

                    goBackTo(Application.Publisher.CREA_STRUTTURA);
                }
            } catch (ParerUserError ex) {
                // In caso di errore: non se mi trovo in dettaglio tipo fascicolo (sar\u00E0  in dettaglio struttura),
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
        // Di bonniana memoria, della serie "la prudenza non \u00E8 mai troppa"...
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
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
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
        goBack();
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
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
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
        forwardToPublisher(Application.Publisher.TIPO_FASCICOLO_DETAIL);
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
        form.getOggettoDetail().getNmApp()
                .setValue(configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO);
        form.getOggettoDetail().getIdOggetto().setValue(riga.getIdTipoFascicolo().toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void deleteMetadatiProfiloFascicoloList() throws EMFError {
        BaseRowInterface currentRow = getForm().getMetadatiProfiloFascicoloList().getTable().getCurrentRow();

        BigDecimal idUsoModelloXsdFasc = currentRow.getBigDecimal("id_uso_modello_xsd_fasc");
        int riga = getForm().getMetadatiProfiloFascicoloList().getTable().getCurrentRowIndex();

        if (getLastPublisher().equals(Application.Publisher.XSD_AA_TIPO_FASCICOLO_DETAIL) && !idUsoModelloXsdFasc
                .equals(getForm().getMetadatiProfiloDetail().getId_uso_modello_xsd_fasc().parse())) {
            getMessageBox().addError("Eccezione imprevista nell'eliminazione della parte");
        }

        if (!getMessageBox().hasError() && idUsoModelloXsdFasc != null) {
            try {
                // Controllo se il modello \u00E8 stato utilizzato per il versamento di fascicoli prima di una
                // cancellazione o modifica
                if (tipoFascicoloEjb.checkModelliNelPeriodoValidita(idUsoModelloXsdFasc)) {
                    getMessageBox().addError("Il modello " + currentRow.getString("ds_xsd")
                            + " \u00E8 stato utilizzato per il versamento di fascicoli; l'associazione non pu\u00F2 essere eliminata");
                }

                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (Application.Publisher.TIPO_FASCICOLO_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                        StrutTipiFascicoloForm form = (StrutTipiFascicoloForm) SpagoliteLogUtil.getForm(this);
                        param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form,
                                form.getMetadatiProfiloFascicoloList()));
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    }

                    // Elimino il record da DB e dalla lista online
                    tipoFascicoloEjb.deleteDecUsoModelloXsdFasc(param, idUsoModelloXsdFasc);
                    getForm().getMetadatiProfiloFascicoloList().getTable().remove(riga);
                    getMessageBox().addInfo("Versione XSD eliminata con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }

        goBackTo(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    private void increaseNiNumeroFascicolo() {
        int sizeParti = getForm().getParteNumeroFascicoloList().getTable().size();
        String nextPg = String.valueOf(sizeParti + 1);
        getForm().getParteNumeroFascicoloDetail().getNi_parte_numero().setValue(nextPg);
        getForm().getParteNumeroFascicoloDetail().getNi_parte_numero().setViewMode();
    }

    private void initMetadatiProfilo() throws EMFError {
        // Combo tipo xsd
        getForm().getMetadatiProfiloDetail().getTi_modello_xsd()
                .setDecodeMap(ComboGetter.getMappaTiModelloXsdProfilo());
        // Combo versioni di xsd
        DecModelloXsdFascicoloTableBean modelloXsdFascicoloTableBean = tipoFascicoloEjb
                .getDecModelloXsdFascicoloTableBean(getForm().getStrutRif().getId_ambiente().parse(), new Date(), null,
                        CostantiDB.TiUsoModelloXsd.VERS.name());
        getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setDecodeMap(
                DecodeMap.Factory.newInstance(modelloXsdFascicoloTableBean, "id_modello_xsd_fascicolo", "cd_xsd"));

        getForm().getMetadatiProfiloDetail().getDt_istituz()
                .setValue(ActionUtils.getStringDate(Calendar.getInstance().getTime()));
        getForm().getMetadatiProfiloDetail().getDt_soppres().setValue(ActionUtils.getStringDate(null));
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
                message = "Esegue riempimento di 0 a sinistra fino al numero massimo di caratteri oppure fino a 20 caratteri se il numero massimo non \u00E8 definito";
                break;
            case RIEMPI_SPAZIO_DX:
                message = "Esegue riempimento di spazi a destra fino al numero massimo di caratteri oppure fino a 20 caratteri se il numero massimo non \u00E8 definito";
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
                            // Contiene pi\u00F9 di un valore
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

        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    @Override
    public void deleteParteNumeroFascicoloList() throws EMFError {
        BaseRowInterface currentRow = getForm().getParteNumeroFascicoloList().getTable().getCurrentRow();

        BigDecimal idParteNumeroFascicolo = currentRow.getBigDecimal("id_parte_numero_fascicolo");
        int riga = getForm().getParteNumeroFascicoloList().getTable().getCurrentRowIndex();

        if (getLastPublisher().equals(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL)
                && !idParteNumeroFascicolo
                        .equals(getForm().getParteNumeroFascicoloDetail().getId_parte_numero_fascicolo().parse())) {
            getMessageBox().addError("Eccezione imprevista nell'eliminazione della parte");
        }

        if (!getMessageBox().hasError() && idParteNumeroFascicolo != null) {
            try {
                if (!getMessageBox().hasError()) {
                    /*
                     * Codice aggiuntivo per il logging...
                     */
                    LogParam param = SpagoliteLogUtil.getLogParam(
                            configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                            getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
                    param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
                    if (Application.Publisher.TIPO_FASCICOLO_DETAIL.equalsIgnoreCase(param.getNomePagina())) {
                        StrutTipiFascicoloForm form = (StrutTipiFascicoloForm) SpagoliteLogUtil.getForm(this);
                        param.setNomeAzione(
                                SpagoliteLogUtil.getDetailActionNameDelete(form, form.getParteNumeroFascicoloList()));
                    } else {
                        param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
                    }

                    // Elimino il record da DB e dalla lista online
                    tipoFascicoloEjb.deleteDecParteNumeroFascicolo(param, idParteNumeroFascicolo);
                    getForm().getParteNumeroFascicoloList().getTable().remove(riga);
                    getMessageBox().addInfo("Parte eliminata con successo");
                    getMessageBox().setViewMode(ViewMode.plain);
                }
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
    }

    @Override
    public void updateParteNumeroFascicoloList() throws EMFError {
        getForm().getParteNumeroFascicoloList().setStatus(Status.update);
        getForm().getParteNumeroFascicoloDetail().setStatus(Status.update);

        getForm().getParteNumeroFascicoloDetail().setEditMode();

        forwardToPublisher(Application.Publisher.FORMATO_NUMERO_LISTA_PARTI_DETAIL);
    }

    @Override
    public void cleanParteNumeroFascicolo() throws EMFError {
        resetPartiInserimento();
        forwardToPublisher(Application.Publisher.AA_TIPO_FASCICOLO_DETAIL);
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
        // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
        // idAaTipoFascicolo, null, false, true, true, true);
        loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, true,
                getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
        loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, true,
                getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
        loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(), idAaTipoFascicolo,
                null, false, true, getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
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
        // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
        // idAaTipoFascicolo, null, false, false, true, true);
        loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, false,
                getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
        loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, true,
                getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
        loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(), idAaTipoFascicolo,
                null, false, true, getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
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
        // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
        // idAaTipoFascicolo, null, false, false, false, true);
        loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, false,
                getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
        loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                idAaTipoFascicolo, null, false, false,
                getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
        loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(), idAaTipoFascicolo,
                null, false, true, getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
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
                        configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                        getUser().getUsername(), SpagoliteLogUtil.getPageName(this), SpagoliteLogUtil.getToolbarSave(
                                getForm().getAaTipoFascicoloDetail().getStatus().equals(Status.update)));
                param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());

                tipoFascicoloEjb.saveParametriAaTipoFascicolo(param, parametriAmministrazione, parametriConservazione,
                        parametriGestione, idAaTipoFascicolo);
                getMessageBox().addInfo("Parametri periodo tipo fascicolo salvati con successo");
                getMessageBox().setViewMode(ViewMode.plain);
                getForm().getAaTipoFascicoloDetail().setViewMode();
                getForm().getAaTipoFascicoloDetail().setStatus(Status.view);
                setViewModeListeParametri();
                // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                // idAaTipoFascicolo, null, true, false, false, false);
                loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, funzione, false, false,
                        getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
                loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, funzione, false, false,
                        getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
                loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, funzione, false, false,
                        getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
            } catch (ParerUserError ex) {
                getMessageBox().addError(ex.getDescription());
            }
        }
        forwardToPublisher(getLastPublisher());
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
                    // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(),
                    // tipoFascicoloRowBean.getIdStrut(), idAaTipoFascicolo, funzione, false, true, true, true);
                    loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, true,
                            getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
                    loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, true,
                            getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
                    loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, true,
                            getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
                    if (getForm().getAaTipoFascicoloDetail().getStatus().equals(Status.update)) {
                        setEditModeParametriAmministrazione();
                    } else {
                        setViewModeListeParametri();
                    }
                } else if (provenzienzaParametri.equals("conservazione")) {
                    // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(),
                    // tipoFascicoloRowBean.getIdStrut(), idAaTipoFascicolo, funzione, false, false, true, true);
                    loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, false,
                            getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
                    loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, true,
                            getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
                    loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, true,
                            getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
                    if (getForm().getAaTipoFascicoloDetail().getStatus().equals(Status.update)) {
                        setEditModeParametriConservazione();
                    } else {
                        setViewModeListeParametri();
                    }
                } else if (provenzienzaParametri.equals("gestione")) {
                    // loadListeParametriPeriodoTipoFascicolo(enteRowBean.getIdAmbiente(),
                    // tipoFascicoloRowBean.getIdStrut(), idAaTipoFascicolo, funzione, false, false, false, true);
                    loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, false,
                            getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords());
                    loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, false,
                            getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords());
                    loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                            idAaTipoFascicolo, funzione, false, true,
                            getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords());
                    if (getForm().getAaTipoFascicoloDetail().getStatus().equals(Status.update)) {
                        setEditModeParametriGestione();
                    } else {
                        setViewModeListeParametri();
                    }
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

                if (rowBean.getString("ds_valore_param_applic_applic") != null) {
                    rowBean.setString("ds_valore_param_applic_applic", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_ambiente") != null) {
                    rowBean.setString("ds_valore_param_applic_ambiente", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_strut") != null) {
                    rowBean.setString("ds_valore_param_applic_strut", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_amm") != null) {
                    rowBean.setString("ds_valore_param_applic_aa_tipo_fascicolo_amm", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_gest") != null) {
                    rowBean.setString("ds_valore_param_applic_aa_tipo_fascicolo_gest", Constants.OBFUSCATED_STRING);
                }

                if (rowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_cons") != null) {
                    rowBean.setString("ds_valore_param_applic_aa_tipo_fascicolo_cons", Constants.OBFUSCATED_STRING);
                }

            }
        }

        return paramApplicTableBean;
    }

    @Override
    public void scaricaXsdModelliUdButton() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public JSONObject triggerMetadatiProfiloDetailTi_modello_xsdOnTrigger() throws EMFError {
        getForm().getMetadatiProfiloDetail().post(getRequest());
        String tiModXsd = getForm().getMetadatiProfiloDetail().getTi_modello_xsd().parse();
        if (tiModXsd != null) {
            // Recupero i modelli Xsd relativi al tipo fascicolo selezionato
            DecodeMap mappaModelliXsd = new DecodeMap();
            mappaModelliXsd.populatedMap(tipoFascicoloEjb.getDecModelloXsdFascicoloTableBeanByTiModelloXsd(tiModXsd),
                    "id_modello_xsd_fascicolo", "cd_xsd");
            getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setDecodeMap(mappaModelliXsd);

        } else {
            getForm().getMetadatiProfiloDetail().getId_modello_xsd_fascicolo().setDecodeMap(new DecodeMap());
        }
        return getForm().getMetadatiProfiloDetail().asJSON();
    }

    @Override
    public void visualizzaModelloXsdFascicolo() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
        // Tools | Templates.
    }

    @Override
    public void filterInactiveRecordsParametriAmministrazioneAaTipoFascList() throws EMFError {
        BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdAaTipoFascicolo();
        BigDecimal idTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdTipoFascicolo();
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb.getDecTipoFascicoloRowBean(idTipoFascicolo);
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        boolean filterValid = getForm().getParametriAmministrazioneAaTipoFascList().isFilterValidRecords();
        try {
            if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)) {
                loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, false, true, filterValid);
            } else {
                loadListaParametriAmministrazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, false, false, filterValid);
            }
        } catch (ParerUserError ex) {
            getMessageBox()
                    .addError("Errore durante il recupero dei parametri di amministrazione del periodo tipo fascicolo");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsParametriConservazioneAaTipoFascList() throws EMFError {
        BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdAaTipoFascicolo();
        BigDecimal idTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdTipoFascicolo();
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb.getDecTipoFascicoloRowBean(idTipoFascicolo);
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        boolean filterValid = getForm().getParametriConservazioneAaTipoFascList().isFilterValidRecords();
        try {
            if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)) {
                loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, false, true, filterValid);
            } else {
                loadListaParametriConservazioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, false, false, filterValid);
            }
        } catch (ParerUserError ex) {
            getMessageBox()
                    .addError("Errore durante il recupero dei parametri di conservazione del periodo tipo fascicolo");
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void filterInactiveRecordsParametriGestioneAaTipoFascList() throws EMFError {
        BigDecimal idAaTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdAaTipoFascicolo();
        BigDecimal idTipoFascicolo = ((DecAaTipoFascicoloTableBean) getForm().getAaTipoFascicoloList().getTable())
                .getCurrentRow().getIdTipoFascicolo();
        DecTipoFascicoloRowBean tipoFascicoloRowBean = tipoFascicoloEjb.getDecTipoFascicoloRowBean(idTipoFascicolo);
        OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(tipoFascicoloRowBean.getIdStrut());
        OrgEnteRowBean enteRowBean = struttureEjb.getOrgEnteRowBean(strutRowBean.getIdEnte());
        boolean filterValid = getForm().getParametriGestioneAaTipoFascList().isFilterValidRecords();
        try {
            if (getLastPublisher().equals(Application.Publisher.PARAMETRI_AA_TIPO_FASC)) {
                loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, false, true, filterValid);
            } else {
                loadListaParametriGestioneAaTipoFasc(enteRowBean.getIdAmbiente(), strutRowBean.getIdStrut(),
                        idAaTipoFascicolo, null, false, false, filterValid);
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore durante il recupero dei parametri di gestione del tipo ud");
        }
        forwardToPublisher(getLastPublisher());
    }

    private void loadListaParametriAmministrazioneAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean hideDeleteButtons,
            boolean editModeAmministrazione, boolean filterValid) throws ParerUserError {

        // MEV26587
        AplParamApplicTableBean parametriAmministrazione = amministrazioneEjb
                .getAplParamApplicAmministrazioneAaTipoFasc(idAmbiente, idStrut, idAaTipoFascicolo, funzione,
                        filterValid);

        if (!editModeAmministrazione) {
            parametriAmministrazione = obfuscatePasswordParamApplic(parametriAmministrazione);
        }

        getForm().getParametriAmministrazioneAaTipoFascList().setTable(parametriAmministrazione);
        getForm().getParametriAmministrazioneAaTipoFascList().getTable().setPageSize(300);
        getForm().getParametriAmministrazioneAaTipoFascList().getTable().first();
        getForm().getParametriAmministrazioneAaTipoFascList().setHideDeleteButton(hideDeleteButtons);
        if (editModeAmministrazione) {
            getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm()
                    .setEditMode();
            getForm().getParametriAmministrazioneAaTipoFascList().setStatus(Status.update);
        } else {
            getForm().getParametriAmministrazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_amm()
                    .setViewMode();
            getForm().getParametriAmministrazioneAaTipoFascList().setStatus(Status.view);
        }
    }

    private void loadListaParametriConservazioneAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean hideDeleteButtons,
            boolean editModeConservazione, boolean filterValid) throws ParerUserError {

        // MEV26587
        AplParamApplicTableBean parametriConservazione = amministrazioneEjb.getAplParamApplicConservazioneAaTipoFasc(
                idAmbiente, idStrut, idAaTipoFascicolo, funzione, filterValid);

        if (!editModeConservazione) {
            parametriConservazione = obfuscatePasswordParamApplic(parametriConservazione);
        }

        getForm().getParametriConservazioneAaTipoFascList().setTable(parametriConservazione);
        getForm().getParametriConservazioneAaTipoFascList().getTable().setPageSize(300);
        getForm().getParametriConservazioneAaTipoFascList().getTable().first();
        getForm().getParametriConservazioneAaTipoFascList().setHideDeleteButton(hideDeleteButtons);
        if (editModeConservazione) {
            getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                    .setEditMode();
            getForm().getParametriConservazioneAaTipoFascList().setStatus(Status.update);
        } else {
            getForm().getParametriConservazioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_cons()
                    .setViewMode();
            getForm().getParametriConservazioneAaTipoFascList().setStatus(Status.view);
        }
    }

    private void loadListaParametriGestioneAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean hideDeleteButtons, boolean editModeGestione,
            boolean filterValid) throws ParerUserError {

        // MEV26587
        AplParamApplicTableBean parametriGestione = amministrazioneEjb.getAplParamApplicGestioneAaTipoFasc(idAmbiente,
                idStrut, idAaTipoFascicolo, funzione, filterValid);

        if (!editModeGestione) {
            parametriGestione = obfuscatePasswordParamApplic(parametriGestione);
        }

        getForm().getParametriGestioneAaTipoFascList().setTable(parametriGestione);
        getForm().getParametriGestioneAaTipoFascList().getTable().setPageSize(300);
        getForm().getParametriGestioneAaTipoFascList().getTable().first();
        getForm().getParametriGestioneAaTipoFascList().setHideDeleteButton(hideDeleteButtons);
        if (editModeGestione) {
            getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest()
                    .setEditMode();
            getForm().getParametriGestioneAaTipoFascList().setStatus(Status.update);
        } else {
            getForm().getParametriGestioneAaTipoFascList().getDs_valore_param_applic_aa_tipo_fascicolo_gest()
                    .setViewMode();
            getForm().getParametriGestioneAaTipoFascList().setStatus(Status.view);
        }
    }

}
