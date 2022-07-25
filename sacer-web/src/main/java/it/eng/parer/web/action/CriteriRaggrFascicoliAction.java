package it.eng.parer.web.action;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.ejb.SistemaMigrazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb.StrutTitolariEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.ejb.FascicoliEjb;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.CriteriRaggrFascicoliAbstractAction;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecTitolTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableDescriptor;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolRowBean;
import it.eng.parer.slite.gen.viewbean.DecVTreeTitolTableBean;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ActionEnums;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.FascicoliValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;

/**
 *
 * @author DiLorenzo_F
 */
public class CriteriRaggrFascicoliAction extends CriteriRaggrFascicoliAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(CriteriRaggrFascicoliAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SistemaMigrazioneEjb")
    private SistemaMigrazioneEjb sysMigrazioneEjb;

    @EJB(mappedName = "java:app/Parer-ejb/FascicoliEjb")
    private FascicoliEjb fascicoliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StrutTitolariEjb")
    private StrutTitolariEjb titolariEjb;

    private DecodeMap mappaTipoFascicolo;
    private DecodeMap mappaTitol;
    private DecodeMap mappaSisMig;

    private BaseTable listaTitolari;

    private static final int CRITERIO_INSERT = 0;
    private static final int CRITERIO_EDIT = 1;

    private static final int NAME_MAX_LENGTH = 100;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {
        // Se ho cliccato sul dettaglio di un Criterio di Raggruppamento fascicoli
        if (getTableName().equals(getForm().getCriterioRaggrFascicoliList().getName())
                && !getNavigationEvent().equals(NE_DETTAGLIO_INSERT)) {
            // Carica il rowBean del criterio tramite BaseRow
            BigDecimal id = getForm().getCriterioRaggrFascicoliList().getTable().getCurrentRow()
                    .getBigDecimal("id_criterio_raggr_fasc");
            BaseRow critRB = fascicoliEjb.getDettaglioCriterioRaggrFascicolo(id);
            getForm().getCreaCriterioRaggrFascicoli().copyFromBean(critRB);

            // Inizializza le combo della form
            initCriterioRaggrFascCombo(critRB.getBigDecimal("id_strut"));

            // Inizializza le liste della form
            initCriterioRaggrFascLists(critRB.getBigDecimal("id_criterio_raggr_fasc"));

            // Popola i valori delle liste in base ai dati
            loadLists(critRB, false);

            // Popola i valori delle combo in base ai dati
            populateComboFields(critRB);

            // Popola i valori delle combo ambiente/ente/struttura
            initComboAmbienteEnteStrutCreaCriteriRaggrFasc(critRB.getBigDecimal("id_strut"));

            // Metto in viewMode anche lista e campi
            getForm().getCreaCriterioRaggrFascicoli().setViewMode();
            getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.view);
            getForm().getCriterioRaggrFascicoliList().setStatus(Status.view);
            getForm().getTitolariList().setStatus(Status.view);
            getForm().getTitolarioDetail().setStatus(Status.view);

            // Aggiungo la versione "casella di testo" di ambiente/ente/struttura
            setAmbienteEnteStrutturaDesc(critRB.getBigDecimal("id_strut"));
        }
    }

    /**
     * Inizializza i FILTRI DI RICERCA CRITERI di raggruppamento fascicoli in base alla struttura con la quale l'utente
     * è loggato
     *
     * @throws EMFError
     *             errore generico
     */
    private void initFiltriCriteriRaggrFasc() throws EMFError {
        // Azzero i filtri
        getForm().getFiltriCriteriRaggrFascicoli().reset();
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idStruttura = getUser().getIdOrganizzazioneFoglia();
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
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(), idAmbiente.longValue(),
                    Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            logger.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriCriteriRaggrFascicoli().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriCriteriRaggrFascicoli().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriCriteriRaggrFascicoli().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriCriteriRaggrFascicoli().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriCriteriRaggrFascicoli().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriCriteriRaggrFascicoli().getId_strut().setValue(idStruttura.toString());

        // Imposto le combo "Standard" e "Attivo"
        getForm().getFiltriCriteriRaggrFascicoli().getFl_criterio_raggr_standard()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriCriteriRaggrFascicoli().getCriterio_attivo()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // Imposto la combo Tipo Fascicolo
        checkUniqueStrutInCombo(idStruttura, ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC);

        // Imposto i filtri in editMode
        getForm().getFiltriCriteriRaggrFascicoli().setEditMode();

        // Imposto come visibile il bottone di ricerca criteri di raggruppamento fascicoli e disabilito la clessidra
        // (per IE)
        getForm().getFiltriCriteriRaggrFascicoli().getRicercaCriteriRaggrFascicoliButton().setEditMode();
        getForm().getFiltriCriteriRaggrFascicoli().getRicercaCriteriRaggrFascicoliButton().setDisableHourGlass(true);
    }

    private void setAmbienteEnteStrutturaDesc(BigDecimal idStrut) throws EMFError {
        String[] aes = struttureEjb.getAmbienteEnteStrutturaDesc(idStrut);
        getForm().getCreaCriterioRaggrFascicoli().getNm_ambiente().setValue(aes[0]);
        getForm().getCreaCriterioRaggrFascicoli().getNm_ente().setValue(aes[1]);
        getForm().getCreaCriterioRaggrFascicoli().getNm_strut().setValue(aes[2]);
    }

    private boolean isCriterioRaggrFascicoliStandard(CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli filtri,
            Object[] anniFascicoliValidati, List<BigDecimal> voceTitolList) throws EMFError {
        BigDecimal idStrut = filtri.getId_strut().parse();
        String nmCriterioRaggr = filtri.getNm_criterio_raggr().parse();
        BigDecimal aaFascicolo = filtri.getAa_fascicolo().parse();
        BigDecimal aaFascicoloDa = filtri.getAa_fascicolo_da().parse();
        BigDecimal aaFascicoloA = filtri.getAa_fascicolo_a().parse();
        BigDecimal niTempoScadChius = filtri.getNi_tempo_scad_chius().parse();
        String tiTempoScadChius = filtri.getTi_tempo_scad_chius().parse();
        Set<String> tipiFascicolo = filtri.getNm_tipo_fascicolo().getDecodedValues();
        BigDecimal niMaxFasc = filtri.getNi_max_fasc().parse();

        // TODO: verificare
        /*
         * String tiConservazione = filtri.getTi_conservazione().parse(); List<String> nmSistemaMigraz =
         * filtri.getNm_sistema_migraz().parse();
         */

        return fascicoliEjb.isCriterioRaggrFascStandard(idStrut, nmCriterioRaggr, aaFascicolo, aaFascicoloDa,
                aaFascicoloA, niTempoScadChius, tiTempoScadChius, tipiFascicolo, voceTitolList, niMaxFasc);
    }

    /**
     * Inizializza le combo ambiente/ente/struttura del DETTAGLIO DI UN CRITERIO di raggruppamento fascicoli, ricavando
     * i valori da una struttura impostata
     *
     * @param idStrut
     *            id struttura
     */
    private void initComboAmbienteEnteStrutCreaCriteriRaggrFasc(BigDecimal idStrut) {
        // Ricavo id struttura, ente ed ambiente attuali
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
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(), idAmbiente.longValue(),
                    Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            /*
             * Inserisco tra i risultati la struttura in questione: questo perchè in caso di operazioni PRIMA della
             * replica organizzazioni non la vedrei!
             */
            OrgStrutRowBean strutRowBean = struttureEjb.getOrgStrutRowBean(idStrut);
            if (!tmpTableBeanStruttura.contains(strutRowBean)) {
                tmpTableBeanStruttura.add(strutRowBean);
                tmpTableBeanStruttura.addSortingRule(OrgStrutTableDescriptor.COL_NM_STRUT, SortingRule.ASC);
                tmpTableBeanStruttura.sort();
            }

        } catch (Exception ex) {
            logger.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getCreaCriterioRaggrFascicoli().getId_ente().setDecodeMap(mappaEnte);
        getForm().getCreaCriterioRaggrFascicoli().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getCreaCriterioRaggrFascicoli().getId_strut().setDecodeMap(mappaStrut);
        getForm().getCreaCriterioRaggrFascicoli().getId_strut().setValue(idStrut.toString());
    }

    /**
     * Metodo che popola le liste della form di dettaglio criterio con i dati forniti e i dati specifici se presenti
     *
     * @param critRB
     *            BaseRow del criterio di raggruppamento fascicoli
     * 
     * @throws EMFError
     *             errore generico
     */
    private void loadLists(BaseRow critRB, boolean isFirst) {
        if ("1".equals(critRB.getString("fl_filtro_voce_titol"))) {
            BigDecimal idCriterioRaggrFasc = critRB.getBigDecimal("id_criterio_raggr_fasc");
            if (idCriterioRaggrFasc != null) {

                HashMap<String, Integer> indMap = new HashMap();

                if (!isFirst) {
                    // Imposto gli indici di riga correnti da mantenere
                    if (getForm().getTitolariList().getTable() != null) {
                        indMap.put("titolari", getForm().getTitolariList().getTable().getCurrentRowIndex());
                        indMap.put("titolariPS", getForm().getTitolariList().getTable().getPageSize());
                    }
                }

                // Lista titolari
                getForm().getTitolariList()
                        .setTable(titolariEjb.getDecVoceTitolsTableBean(idCriterioRaggrFasc,
                                ActionEnums.TipoSelCriteriRaggrFasc.VOCE_TITOL.name(),
                                getForm().getTitolariList().isFilterValidRecords()));
                getForm().getTitolariList().getTable().addSortingRule("dt_istituz", SortingRule.DESC);
                getForm().getTitolariList().getTable().sort();

                getForm().getTitolariList().getTable().first();
                getForm().getTitolariList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

                // Apro le prime section
                getForm().getTitolarioSection().setLoadOpened(true);

                if (!indMap.isEmpty()) {
                    if (getForm().getTitolariList().getTable() != null && indMap.containsKey("titolari")) {
                        getForm().getTitolariList().getTable().setCurrentRowIndex(indMap.get("titolari"));
                        getForm().getTitolariList().getTable().setPageSize(indMap.get("titolariPS"));
                    }
                }
            }
        }
    }

    /**
     * Metodo che popola le combobox/MultiSelect della form di dettaglio criterio con i dati forniti e i dati specifici
     * se presenti
     *
     * @param critRB
     *            BaseRow del criterio di raggruppamento fascicoli
     * 
     * @throws EMFError
     *             errore generico
     */
    private void populateComboFields(BaseRow critRB) throws EMFError {
        String[] tipoFasc = null;
        // String[] sisMigr = null;

        int counter = 0;
        if ("1".equals(critRB.getString("fl_filtro_tipo_fascicolo"))) {
            BaseTable critMult = fascicoliEjb.ricercaSelCriterioRaggrFascicoli(
                    critRB.getBigDecimal("id_criterio_raggr_fasc"),
                    ActionEnums.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
            tipoFasc = new String[critMult.size()];
            for (BaseRow row : critMult) {
                tipoFasc[counter++] = row.getBigDecimal("id_tipo_fascicolo").toString();
            }
        }

        // TODO: verificare
        // counter = 0;
        // if ("1".equals(critRB.getString("fl_filtro_sistema_migraz"))) {
        // BaseTable critMult =
        // fascicoliEjb.ricercaSelCriterioRaggrFascicoli(critRB.getBigDecimal("id_criterio_raggr_fasc"),
        // ActionEnums.TipoSelCriteriRaggrFasc.SISTEMA_MIGRAZ.name());
        // sisMigr = new String[critMult.size()];
        // for (BaseRow row : critMult) {
        // sisMigr[counter++] = row.getBigDecimal("id_sistema_migraz").toString();
        // }
        // }

        getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setValues(tipoFasc);
        // getForm().getCreaCriterioRaggrFascicoli().getNm_sistema_migraz().setValues(sisMigr);
    }

    /**
     * Metodo di inizializzazione delle combo comuni a creazione criterio e ricerca fascicoli
     *
     * @param idStrut
     *            id struttura
     * 
     * @throws EMFError
     *             errore generico
     */
    private void initDecodeMapTitolTipoFascByIdStrut(BigDecimal idStrut) throws EMFError {
        if (idStrut != null) {
            // Setto i valori della combo TIPO FASCICOLO ricavati dalla tabella DEC_TIPO_FASCICOLO
            DecTipoFascicoloTableBean tmpTableBeanTipoFascicolo = tipoFascicoloEjb.getDecTipoFascicoloTableBean(idStrut,
                    false);
            mappaTipoFascicolo = new DecodeMap();
            mappaTipoFascicolo.populatedMap(tmpTableBeanTipoFascicolo, "id_tipo_fascicolo", "nm_tipo_fascicolo");

            // Setto i valori della combo TITOLARIO ricavati dalla tabella DEC_TITOL
            DecTitolTableBean tmpTableBeanTitol = titolariEjb.getDecTitolTableBean(idStrut, false); // TODO: verificare
            mappaTitol = new DecodeMap();
            mappaTitol.populatedMap(tmpTableBeanTitol, "id_titol", "nm_titol");

            // TODO
            // Setto i valori della combo SISTEMI DI MIGRAZIONE ricavati dalla tabella APL_SISTEMA_MIGRAZ
            // BaseTableInterface tmpTableBeanSisMig = sysMigrazioneEjb.getNmSistemaMigrazTableBean(idStrut);
            // mappaSisMig = new DecodeMap();
            // mappaSisMig.populatedMap(tmpTableBeanSisMig, "id_sistema_migraz", "nm_sistema_migraz");
        }
    }

    /**
     * Metodo di inizializzazione delle liste
     *
     * @param idCriterioRaggrFasc
     *            id criterio raggruppamento fascicoli
     * 
     * @throws EMFError
     *             errore generico
     */
    private void initTableBeanTitolListByIdCritRaggrFasc(BigDecimal idCriterioRaggrFasc) throws EMFError {
        if (idCriterioRaggrFasc != null) {
            // Setto i valori della LISTA TITOLARI ricavati dalla tabella DEC_SEL_CRITERIO_RAGGR_FASC
            listaTitolari = titolariEjb.getDecVoceTitolsTableBean(idCriterioRaggrFasc,
                    ActionEnums.TipoSelCriteriRaggrFasc.VOCE_TITOL.name(),
                    getForm().getTitolariList().isFilterValidRecords());
        }
    }

    /**
     * Metodo di inizializzazione combo di dettaglio, utilizzate sia nel dettaglio criteri sia nella scelta indice
     * classificazione
     *
     * @param idStrut
     *            id struttura
     * 
     * @throws EMFError
     */
    private void initCriterioRaggrFascCombo(BigDecimal idStrut) throws EMFError {
        initDecodeMapTitolTipoFascByIdStrut(idStrut);
        getForm().getCreaCriterioRaggrFascicoli().getTi_scad_chius().setDecodeMap(
                ComboGetter.getMappaOrdinalGenericEnum("ti_scad_chius", VolumeEnums.ExpirationTypeEnum.values()));
        getForm().getCreaCriterioRaggrFascicoli().getTi_tempo_scad_chius().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_tempo_scad_chius", VolumeEnums.TimeTypeEnum.values()));
        getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setDecodeMap(mappaTipoFascicolo);
        getForm().getCreaCriterioRaggrFascicoli().getTi_conservazione().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_conservazione", VolumeEnums.TipoConservazioneFasc.values()));

        getForm().getTitolarioDetail().reset();
        getForm().getTitolarioDetail().getId_titol().setDecodeMap(mappaTitol);
    }

    /**
     * Metodo di inizializzazione liste di dettaglio, utilizzate nel dettaglio criteri di raggruppamento fascicoli
     *
     * @param idCriterioRaggrFasc
     *            id criterio raggruppamento fascicoli
     * 
     * @throws EMFError
     *             errore generico
     */
    private void initCriterioRaggrFascLists(BigDecimal idCriterioRaggrFasc) throws EMFError {
        initTableBeanTitolListByIdCritRaggrFasc(idCriterioRaggrFasc);
        getForm().getTitolariList().setTable(listaTitolari);
    }

    @Override
    public void undoDettaglio() throws EMFError {
        getSession().removeAttribute("insertActive");
        if (getForm().getCriterioRaggrFascicoliList().getStatus().equals(Status.insert)) {
            goBackTo(Application.Publisher.LISTA_CRITERI_RAGGR_FASC);
        } else {
            if (getLastPublisher().equals(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL)
                    && getForm().getCriterioRaggrFascicoliList().getStatus().equals(Status.update)) {
                getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(false);
                getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(false);
                getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(true);
                loadDettaglio();
                getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setEditMode();
                getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setEditMode();
            }
            forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        // Richiamo il metodo per la creazione ex-novo di un criterio di raggruppamento fascicoli
        creaCriterioRaggrFascicoli();
    }

    @Override
    public void saveDettaglio() throws EMFError {
        // Se lo status della form è 'update', la form è in modifica,
        // Altrimenti è in modalità inserimento
        if (getForm().getCreaCriterioRaggrFascicoli().getStatus().equals(Status.update)) {
            salvaCriterioRaggrFascicoli(CRITERIO_EDIT);
        } else if (getForm().getCreaCriterioRaggrFascicoli().getStatus().equals(Status.insert)) {
            salvaCriterioRaggrFascicoli(CRITERIO_INSERT);
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {

        if (getRequest().getParameter("table").equals(getForm().getCriterioRaggrFascicoliList().getName())
                && !getNavigationEvent().equals(NE_DETTAGLIO_DELETE)) {
            // Lista criteri di raggruppamento
            getForm().getCreaCriterioRaggrFascicoli().setViewMode();
            getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.view);
            getForm().getCriterioRaggrFascicoliList().setStatus(Status.view);
            getForm().getTitolariList().setStatus(Status.view);
            getForm().getTitolarioDetail().setStatus(Status.view);

            getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(false);
            getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(false);
            getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setViewMode();
            getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(true);
            forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL)) {
            if (!getMessageBox().hasError()) {
                // goBackTo(Application.Publisher.LISTA_CRITERI_RAGGR_FASC);
                goBack();
            }
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.LISTA_CRITERI_RAGGR_FASC;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        if (publisherName.equals(Application.Publisher.LISTA_CRITERI_RAGGR_FASC)) {
            getSession().removeAttribute("insertActive");
            int paginaCorrenteDocNonVers = getForm().getCriterioRaggrFascicoliList().getTable().getCurrentPageIndex();
            int inizioDocNonVers = getForm().getCriterioRaggrFascicoliList().getTable().getFirstRowPageIndex();
            int pageSize = getForm().getCriterioRaggrFascicoliList().getTable().getPageSize();

            try {
                BaseTable critRaggrFascTableBean = fascicoliEjb.ricercaCriteriRaggrFascicoli(
                        getForm().getFiltriCriteriRaggrFascicoli(),
                        getForm().getCriterioRaggrFascicoliList().isFilterValidRecords());
                getForm().getCriterioRaggrFascicoliList().setTable(critRaggrFascTableBean);
                getForm().getCriterioRaggrFascicoliList().getTable().setPageSize(pageSize);
                getForm().getCriterioRaggrFascicoliList().getTable().first();
                getForm().getCriterioRaggrFascicoliList().setStatus(Status.view);
                // Rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getCriterioRaggrFascicoliList(), paginaCorrenteDocNonVers);
                // Ritorno alla pagina
                getForm().getCriterioRaggrFascicoliList().getTable().setCurrentRowIndex(inizioDocNonVers);
                getForm().getCriterioRaggrFascicoliList().setHideUpdateButton(false);
                getForm().getCriterioRaggrFascicoliList().setUserOperations(true, true, true, true);
            } catch (EMFError ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.CRITERI_RAGGR_FASCICOLI;
    }

    /**
     * Metodo di caricamento della form di creazione dei criteri di raggruppamento fascicoli
     *
     * @throws EMFError
     *             errore generico
     */
    public void creaCriterioRaggrFascicoli() throws EMFError {
        // Apre pagina di creazione criterio
        getForm().getCreaCriterioRaggrFascicoli().reset();
        getForm().getCreaCriterioRaggrFascicoli().setEditMode();
        getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(false);
        getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.insert);
        getForm().getCriterioRaggrFascicoliList().setStatus(Status.insert);
        getForm().getTitolarioDetail().setStatus(Status.insert);
        getForm().getTitolariList().setStatus(Status.insert); // TODO: verificare
        getSession().setAttribute("insertActive", true);

        Fields<Field> filtri = null;
        BigDecimal idStrut = null;
        BigDecimal idTipoFascicolo = null;
        BigDecimal idVoceTitol = null;

        // Setto ambiente/ente/struttura in base ai valori della pagina di ricerca criteri
        idStrut = initComboAmbienteEnteStrutturaCreaCriteriRaggrFascFromRicercaCriteri();

        // Inizializzo le combo "standard" della pagina
        initCriterioRaggrFascCombo(idStrut);
        // Setto i filtri e le liste standard
        populateDefaultValues(filtri, idStrut);
        // Setto il numero massimo fascicoli con lo stesso valore della struttura impostata
        settaNumeroMassimoFascicoli(getForm().getCreaCriterioRaggrFascicoli().getId_strut().parse());
        // Setto il numero di giorni di scadenza chiusura con il valore definito sulla struttura corrente
        // (NI_GG_SCAD_CRITERIO di ORG_STRUT_CONFIG_FASCICOLO)
        settaNumeroGiorniScadChius(getForm().getCreaCriterioRaggrFascicoli().getId_strut().parse());

        // Se tipo fascicolo è diverso da null, imposto i valori nella combo
        if (idTipoFascicolo != null) {
            String[] idTipoFascicoloArray = { "" + idTipoFascicolo };
            getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setValues(idTipoFascicoloArray);
        }

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    /**
     * Inizializza le combo ambiente/ente/struttura del DETTAGLIO DI UN CRITERIO di raggruppamento fascicoli, ricavando
     * i valori dai filtri di ricerca criterio. Se impostato, restituisce il valore dell'id del filtro relativo alla
     * struttura
     *
     * @return l'id della struttura impostata come filtro di ricerca
     */
    private BigDecimal initComboAmbienteEnteStrutturaCreaCriteriRaggrFascFromRicercaCriteri() {
        // Inizializzo le combo ambiente/ente/struttura in base ai valori
        // impostati nella pagina di ricerca criteri
        ComboBox ambienteCombo = getForm().getFiltriCriteriRaggrFascicoli().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        ComboBox enteCombo = getForm().getFiltriCriteriRaggrFascicoli().getId_ente();
        DecodeMapIF mappaEnte = enteCombo.getDecodeMap();

        ComboBox strutCombo = getForm().getFiltriCriteriRaggrFascicoli().getId_strut();
        DecodeMapIF mappaStrut = strutCombo.getDecodeMap();

        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente()
                .setValue(getForm().getFiltriCriteriRaggrFascicoli().getId_ambiente().getValue());

        getForm().getCreaCriterioRaggrFascicoli().getId_ente().setDecodeMap(mappaEnte);
        getForm().getCreaCriterioRaggrFascicoli().getId_ente()
                .setValue(getForm().getFiltriCriteriRaggrFascicoli().getId_ente().getValue());

        getForm().getCreaCriterioRaggrFascicoli().getId_strut().setDecodeMap(mappaStrut);
        getForm().getCreaCriterioRaggrFascicoli().getId_strut()
                .setValue(getForm().getFiltriCriteriRaggrFascicoli().getId_strut().getValue());

        return StringUtils.isNotBlank(getForm().getFiltriCriteriRaggrFascicoli().getId_strut().getValue())
                ? new BigDecimal(getForm().getFiltriCriteriRaggrFascicoli().getId_strut().getValue()) : null;
    }

    /**
     * Metodo che inserisce i valori di default dei campi nella form di creazione criterio di raggruppamento fascicoli
     *
     * @param type
     *            lista filtri di tipo {@link Field}
     * @param idStrut
     *            id struttura
     * 
     * @throws EMFError
     *             errore generico
     */
    private void populateDefaultValues(Fields<Field> filtri, BigDecimal idStrut) throws EMFError {
        OrgStrutRowBean struttura = null;
        if (idStrut != null) {
            struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        }

        // if (struttura != null) {
        // if (struttura.getTiScadChiusVolume() != null) {
        // // TODO: verificare se bisogna leggere il valore dalla struttura
        // // getForm().getCreaCriterioRaggrFascicoli().getTi_scad_chius().setValue(struttura.getTiScadChiusVolume());
        // }
        // if (struttura.getNiTempoScadChius() != null) {
        // // TODO: verificare se bisogna leggere il valore dalla struttura o da orgStrutConfigFascicolo
        // //
        // getForm().getCreaCriterioRaggrFascicoli().getNi_tempo_scad_chius().setValue(struttura.getNiTempoScadChius().toString());
        // }
        // if (struttura.getTiTempoScadChius() != null) {
        // // TODO: verificare se bisogna leggere il valore dalla struttura
        // //
        // getForm().getCreaCriterioRaggrFascicoli().getTi_tempo_scad_chius().setValue(struttura.getTiTempoScadChius());
        // }
        // }

        getForm().getCreaCriterioRaggrFascicoli().getTi_tempo_scad_chius()
                .setValue(VolumeEnums.TimeTypeEnum.GIORNI.name());

        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31);
        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getForm().getCreaCriterioRaggrFascicoli().getDt_istituz().setValue(df.format(Calendar.getInstance().getTime()));
        getForm().getCreaCriterioRaggrFascicoli().getDt_soppres().setValue(df.format(cal.getTime()));

        getForm().getTitolariList().setTable(new BaseTable());

    }

    /**
     * Metodo invocato sul bottone di eliminazione nella lista criteri di raggruppamento fascicoli o nel dettaglio
     * criterio sulla listNavBar
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteCriterioRaggrFascicoliList() throws EMFError {
        try {
            // Carica il rowBean del criterio tramite BaseRow
            BigDecimal id = getForm().getCriterioRaggrFascicoliList().getTable().getCurrentRow()
                    .getBigDecimal("id_criterio_raggr_fasc");
            BaseRow row = fascicoliEjb.getDettaglioCriterioRaggrFascicolo(id);
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.LISTA_CRITERI_RAGGR_FASC)) {
                // Gestione della cancellazione da lista criteri raggr fasc
                CriteriRaggrFascicoliForm form = (CriteriRaggrFascicoliForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriterioRaggrFascicoliList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }

            if (fascicoliEjb.deleteDecCriterioRaggrFascicoli(param, row.getBigDecimal("id_strut"),
                    row.getString("nm_criterio_raggr"))) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.INF, "Criterio di raggruppamento fascicoli eliminato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                if (Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL.equals(getLastPublisher())
                        || "".equals(getLastPublisher())) {
                    goBackTo(Application.Publisher.LISTA_CRITERI_RAGGR_FASC);
                } else if (Application.Publisher.LISTA_CRITERI_RAGGR_FASC.equals(getLastPublisher())) {
                    reloadAfterGoBack(Application.Publisher.LISTA_CRITERI_RAGGR_FASC);
                    forwardToPublisher(getLastPublisher());
                } else {
                    goBack();
                }
            }

        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
            if ("".equals(getLastPublisher())) {
                goBack();
            } else {
                forwardToPublisher(getLastPublisher());
            }
        }
    }

    /**
     * Metodo invocato sul bottone di eliminazione nel dettaglio di un criterio di raggruppamento fascicoli
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteCreaCriterioRaggrFascicoli() throws EMFError {

        try {
            // Carica il rowBean del criterio tramite BaseRow
            BigDecimal id = getForm().getCreaCriterioRaggrFascicoli().getId_criterio_raggr_fasc().parse();
            BaseRow critRB = fascicoliEjb.getDettaglioCriterioRaggrFascicolo(id);
            // TODO
            // DA FINIRE !!!!!!!!!!!!
            // TOGLIERE IL NULL
            if (fascicoliEjb.deleteDecCriterioRaggrFascicoli(null, critRB.getBigDecimal("id_strut"),
                    critRB.getString("nm_criterio_raggr"))) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.INF, "Criterio di raggruppamento fascicoli eliminato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                ricercaCriteriRaggrFascicoli();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }

    }

    /**
     * Metodo invocato sul bottone di modifica nella lista criteri di raggruppamento fascicoli
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateCriterioRaggrFascicoliList() throws EMFError {
        // Imposta la form di dettaglio in edit mode e con status update
        getForm().getCreaCriterioRaggrFascicoli().setEditMode();
        getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.update);
        getForm().getCriterioRaggrFascicoliList().setStatus(Status.update);
        getForm().getTitolariList().setStatus(Status.update);
        getForm().getTitolarioDetail().setStatus(Status.update);

        getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(false);

        // I campi riferiti ad ambiente/ente/struttura rimangono in view mode
        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().setViewMode();
        getForm().getCreaCriterioRaggrFascicoli().getId_ente().setViewMode();
        getForm().getCreaCriterioRaggrFascicoli().getId_strut().setViewMode();

        BigDecimal idCriterioRaggrFasc = (getForm().getCriterioRaggrFascicoliList().getTable()).getCurrentRow()
                .getBigDecimal("id_criterio_raggr_fasc");

        /*
         * Se il criterio è già legato a qualche elenco, posso modificare solo data inizio e fine validità, n° max
         * fascicoli, tipo scadenza chiusura, scadenza chiusura, note
         */

        if (fascicoliEjb.existElvElencoVersPerCriterioRaggrFascicoli(idCriterioRaggrFasc)) {
            getForm().getCreaCriterioRaggrFascicoli().setViewMode();
            getForm().getCreaCriterioRaggrFascicoli().getDt_istituz().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getDt_soppres().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getNi_max_fasc().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getTi_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getNt_criterio_raggr().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getTi_tempo_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getNi_tempo_scad_chius().setEditMode();
            getForm().getTitolariList().setViewMode();
            getForm().getTitolariList().setStatus(Status.view);
        }

        getSession().setAttribute("nomeCriterio",
                getForm().getCreaCriterioRaggrFascicoli().getNm_criterio_raggr().parse());
    }

    /**
     * Metodo invocato sul bottone di modifica nel dettaglio di un criterio di raggruppamento fascicoli
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateCreaCriterioRaggrFascicoli() throws EMFError {
        getForm().getCreaCriterioRaggrFascicoli().setEditMode();
        getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.update);
        getForm().getTitolariList().setStatus(Status.update);
        getForm().getTitolarioDetail().setStatus(Status.update);

        getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(false);

        // I campi riferiti ad ambiente/ente/struttura rimangono in view mode
        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().setViewMode();
        getForm().getCreaCriterioRaggrFascicoli().getId_ente().setViewMode();
        getForm().getCreaCriterioRaggrFascicoli().getId_strut().setViewMode();

        // Salvo in sessione il nomeCriterio prima di eventuali modifiche per effettuare successivi controlli
        // N.B.: devo fare così per evitare che venga sovrascritto con il "post" dall'eventuale nuovo valore.
        getSession().setAttribute("nomeCriterio",
                getForm().getCreaCriterioRaggrFascicoli().getNm_criterio_raggr().parse());

        BigDecimal idCriterioRaggrFasc = (getForm().getCriterioRaggrFascicoliList().getTable()).getCurrentRow()
                .getBigDecimal("id_criterio_raggr_fasc");

        /*
         * Se il criterio è già legato a qualche elenco, posso modificare solo data inizio e fine validità, n° max
         * fascicoli, tipo scadenza chiusura, scadenza chiusura, note
         */

        if (fascicoliEjb.existElvElencoVersPerCriterioRaggrFascicoli(idCriterioRaggrFasc)) {
            getForm().getCreaCriterioRaggrFascicoli().setViewMode();
            getForm().getCreaCriterioRaggrFascicoli().getDt_istituz().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getDt_soppres().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getNi_max_fasc().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getTi_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getNt_criterio_raggr().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getTi_tempo_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggrFascicoli().getNi_tempo_scad_chius().setEditMode();
            getForm().getTitolariList().setViewMode();
            getForm().getTitolariList().setStatus(Status.view);
        }

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    /**
     * Metodo per il caricamento della lista dei criteri di raggruppamento fascicoli da menu
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.Fascicoli.ListaCriteriRaggrFasc")
    public void ricercaCriteriRaggrFascicoli() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Fascicoli.ListaCriteriRaggrFasc");
        // TODO: verificare, Azzero tutto sugli IdFields che mi servono se provengo da amministrazione strutture
        getForm().getIdFields().clear();
        // Inizializzo i filtri
        initFiltriCriteriRaggrFasc();
        // Eseguo la ricerca sui filtri pre-impostati
        ricercaCriteriFasc(getForm().getFiltriCriteriRaggrFascicoli());
    }

    public void ricercaCriteriFasc(CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli filtriCriteriRaggrFasc)
            throws EMFError {
        if (filtriCriteriRaggrFasc.validate(getMessageBox())) {
            BaseTable critRaggrFascTableBean = fascicoliEjb.ricercaCriteriRaggrFascicoli(filtriCriteriRaggrFasc,
                    getForm().getCriterioRaggrFascicoliList().isFilterValidRecords());
            getForm().getCriterioRaggrFascicoliList().setTable(critRaggrFascTableBean);
            getForm().getCriterioRaggrFascicoliList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
            getForm().getCriterioRaggrFascicoliList().getTable().first();

            getForm().getCriterioRaggrFascicoliList().setHideUpdateButton(false);
            getForm().getCriterioRaggrFascicoliList().setUserOperations(true, true, true, true);
            getForm().getCriterioRaggrFascicoliList().setStatus(Status.view);

            forwardToPublisher(Application.Publisher.LISTA_CRITERI_RAGGR_FASC);
        }
    }

    /**
     * Metodo per il salvataggio in creazione o modifica del criterio di raggruppamento fascicoli
     *
     * @param status
     *            Lo stato della form, in modifica o creazione
     * 
     * @throws EMFError
     *             errore generico
     */
    private void salvaCriterioRaggrFascicoli(int status) throws EMFError {
        String standardOld = "0";
        String nome = null;
        String nomeCriterio = (String) getSession().getAttribute("nomeCriterio");
        if (status == CRITERIO_EDIT) {
            if (nomeCriterio != null) {
                nome = nomeCriterio;
                nomeCriterio = null;
            } else {
                nome = ((BaseRow) getForm().getCriterioRaggrFascicoliList().getTable().getCurrentRow())
                        .getString("nm_criterio_raggr");
            }
            standardOld = getForm().getCreaCriterioRaggrFascicoli().getFl_criterio_raggr_standard().parse();
        }

        List<BigDecimal> voceTitolList = new ArrayList<>();
        for (BaseRowInterface row : (BaseTableInterface<?>) getForm().getTitolariList().getTable()) {
            voceTitolList.add(row.getBigDecimal("id_voce_titol"));
        }

        // Controllo su campi
        CreaCriterioRaggrFascicoli filtri = getForm().getCreaCriterioRaggrFascicoli();

        filtri.post(getRequest());
        if (filtri.validate(getMessageBox())) {
            FascicoliValidator validator = new FascicoliValidator(getMessageBox());
            // TODO: verificare, Valida data creazione
            /*
             * Timestamp dataDa = filtri.getDt_().parse(); Timestamp dataA = filtri.getDt_().parse(); Date[]
             * dateCreazioneValidate = validator.validaDate(dataDa, filtri.getOre_dt_creazione_unita_doc_da().parse(),
             * filtri.getMinuti_dt_creazione_unita_doc_da().parse(), dataA,
             * filtri.getOre_dt_creazione_unita_doc_a().parse(), filtri.getMinuti_dt_creazione_unita_doc_a().parse(),
             * filtri.getDt_creazione_unita_doc_da().getHtmlDescription().replace(WebConstants.FORMATO_DATA, ""),
             * filtri.getDt_creazione_unita_doc_a().getHtmlDescription().replace(WebConstants.FORMATO_DATA, ""));
             * validator.validaOrdineDateOrari(filtri.getAa_fascicolo_da().parse(), filtri.getAa_fascicolo_a().parse(),
             * filtri.getAa_fascicolo_da().getHtmlDescription(), filtri.getAa_fascicolo_a().getHtmlDescription());
             */
            // Valida Tipo scadenza
            validator.validaTipoScadenza(filtri.getTi_scad_chius().parse(), filtri.getNi_tempo_scad_chius().parse(),
                    filtri.getTi_tempo_scad_chius().parse());
            // Valida Nome criterio
            if (filtri.getNm_criterio_raggr() == null) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.ERR, "Nome criterio di raggruppamento fascicoli obbligatorio <br>"));
            } else if (filtri.getNm_criterio_raggr().parse().length() > NAME_MAX_LENGTH) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Nome criterio di raggruppamento fascicoli con dimensione maggiore di 100 caratteri <br>"));
            } else if (status == CRITERIO_INSERT) {
                // Controllo che non esista su db per quella struttura un criterio con lo stesso nome
                if (fascicoliEjb.existNomeCriterio(filtri.getNm_criterio_raggr().parse(),
                        filtri.getId_strut().parse())) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Nome criterio di raggruppamento fascicoli già esistente per la struttura utilizzata <br>"));
                }
            } else if (status == CRITERIO_EDIT) {
                // Controllo che non esista su db per quella struttura un criterio con lo stesso nome, escluso esso
                // stesso naturalmente
                if (!filtri.getNm_criterio_raggr().parse().equals(nome) && fascicoliEjb
                        .existNomeCriterio(filtri.getNm_criterio_raggr().parse(), filtri.getId_strut().parse())) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Nome criterio di raggruppamento fascicoli già esistente per la struttura utilizzata <br>"));
                }
            }
            // Valida Descrizione criterio
            if (filtri.getDs_criterio_raggr() == null) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Descrizione criterio di raggruppamento fascicoli obbligatorio <br>"));
            }
            // Valida Date istituzione e soppressione
            if (filtri.getDt_istituz() == null && filtri.getDt_soppres() == null) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.ERR, "Campi data istituzione e data soppressione obbligatori <br>"));
            } else {
                validator.validaOrdineDateOrari(filtri.getDt_istituz().parse(), filtri.getDt_soppres().parse(),
                        filtri.getDt_istituz().getHtmlDescription(), filtri.getDt_soppres().getHtmlDescription());
            }

            // Valida i campi anno fascicolo
            BigDecimal annoDa = filtri.getAa_fascicolo_da().parse();
            BigDecimal annoA = filtri.getAa_fascicolo_a().parse();
            Object[] anniFascicoliValidati = validator.validaAnniFascicoli(filtri.getAa_fascicolo().parse(),
                    filtri.getAa_fascicolo_da().parse(), filtri.getAa_fascicolo_a().parse());

            // Verifica se presente almeno un tipo fascicolo o la voce di classificazione
            if (filtri.getNm_tipo_fascicolo().parse().isEmpty() && getForm().getTitolariList().getTable().isEmpty())
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "E’ obbligatorio indicare il filtro sul tipo fascicolo o sull’indice di classificazione <br>"));

            // TODO: verificare
            /*
             * if (filtri.getAa_fascicolo().parse() == null && filtri.getAa_fascicolo().parse() == null &&
             * filtri.getAa_fascicolo_da().parse() == null && filtri.getAa_fascicolo_a().parse() == null &&
             * filtri.getNm_tipo_fascicolo().parse().isEmpty() && filtri.getTi_conservazione().parse() == null) {
             * getMessageBox().addMessage(new Message(MessageLevel.ERR,
             * "Non è stato inserito alcun filtro per il criterio di raggruppamento fascicoli oltre ai dati obbligatori <br>"
             * )); }
             */

            if (!getMessageBox().hasError()) {
                /*
                 * // TODO: verificare BigDecimal numMaxPerWarning = new
                 * BigDecimal(configurationHelper.getValoreParamApplic("NUM_MAX_COMP_CRITERIO_RAGGR_WARN")); BigDecimal
                 * numMaxPerErrore = new
                 * BigDecimal(configurationHelper.getValoreParamApplic("NUM_MAX_COMP_CRITERIO_RAGGR_ERR")); BigDecimal
                 * niMaxFasc = filtri.getNi_max_fasc().parse(); niMaxFasc = niMaxFasc != null ? niMaxFasc : new
                 * BigDecimal("0");
                 * 
                 * // TODO: verificare, Se il numero massimo di fascicoli è compreso tra numMaxPerWarning e
                 * numMaxPerErrore if (niMaxFasc.compareTo(numMaxPerWarning) > 0 && niMaxFasc.compareTo(numMaxPerErrore)
                 * <= 0) { getForm().getCriterioCustomMessageButtonList().setEditMode(); Object[] sa = new Object[9];
                 * sa[0] = filtri; sa[1] = anniFascicoliValidati; sa[2] = nome; sa[3] = annoDa; sa[4] = annoA; sa[5] =
                 * new BigDecimal(status); sa[6] = "controllo1"; sa[7] = standardOld; sa[8] = voceTitolList;
                 * getSession().setAttribute("salvataggioAttributes", sa); getRequest().setAttribute("customBox",
                 * numMaxPerWarning); } else if (niMaxFasc.compareTo(numMaxPerErrore) > 0) {
                 * getMessageBox().addError("Attenzione: numero massimo fascicoli superiore a " + numMaxPerErrore +
                 * ": impossibile eseguire il salvataggio"); } else {
                 * eseguiPrimoStepSalvataggioCriterioRaggrFascicoli(filtri, anniFascicoliValidati, nome, annoDa, annoA,
                 * status, standardOld, voceTitolList); }
                 */
                eseguiPrimoStepSalvataggioCriterioRaggrFascicoli(filtri, anniFascicoliValidati, nome, annoDa, annoA,
                        status, standardOld, voceTitolList);
            }
        }
        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    @Override
    public void ricercaCriteriRaggrFascicoliButton() throws EMFError {
        CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli filtriCriteriRaggrFasc = getForm()
                .getFiltriCriteriRaggrFascicoli();
        filtriCriteriRaggrFasc.post(getRequest());
        ricercaCriteriFasc(filtriCriteriRaggrFasc);
    }

    public Fields triggerAmbienteGenerico(Fields campi, ActionEnums.SezioneCriteriRaggrFasc sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox ambienteCombo = (ComboBox) campi.getComponent("id_ambiente");
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");
        ComboBox tipoFascicoloCombo = null;
        // ComboBox titolarioCombo = null;
        Input niMaxFasc = (Input) campi.getComponent("ni_max_fasc");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
            tipoFascicoloCombo = (ComboBox) campi.getComponent("id_tipo_fascicolo");
        } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
            // titolarioCombo = (ComboBox) campi.getComponent("id_titol");
        }

        // Azzero i valori preimpostati delle varie combo
        enteCombo.setValue("");
        strutCombo.setValue("");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
            tipoFascicoloCombo.setValue("");
        } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
            niMaxFasc.setValue("");
            // titolarioCombo.setValue("");
        }

        BigDecimal idAmbiente = (!ambienteCombo.getValue().equals("") ? new BigDecimal(ambienteCombo.getValue())
                : null);
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            enteCombo.setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                enteCombo.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(), sezione);
            } else {
                strutCombo.setDecodeMap(new DecodeMap());
                if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                    tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                    // titolarioCombo.setDecodeMap(new DecodeMap());
                    // TODO altre combo
                }
            }
        } else {
            enteCombo.setDecodeMap(new DecodeMap());
            strutCombo.setDecodeMap(new DecodeMap());
            if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                tipoFascicoloCombo.setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                // titolarioCombo.setDecodeMap(new DecodeMap());
                // TODO altre combo
            }
        }
        return campi;
    }

    public Fields triggerEnteGenerico(Fields campi, ActionEnums.SezioneCriteriRaggrFasc sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");
        ComboBox tipoFascicoloCombo = null;
        // ComboBox titolarioCombo = null;
        Input niMaxFasc = (Input) campi.getComponent("ni_max_fasc");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
            tipoFascicoloCombo = (ComboBox) campi.getComponent("id_tipo_fascicolo");
        } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
            // titolarioCombo = (ComboBox) campi.getComponent("id_titol");
        }

        // Azzero i valori preimpostati delle varie combo
        strutCombo.setValue("");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
            tipoFascicoloCombo.setValue("");
        } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
            niMaxFasc.setValue("");
            // titolarioCombo.setValue("");
        }

        BigDecimal idEnte = (!enteCombo.getValue().equals("") ? new BigDecimal(enteCombo.getValue()) : null);
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            strutCombo.setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                strutCombo.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                checkUniqueStrutInCombo(tmpTableBeanStrut.getRow(0).getIdStrut(), sezione);
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                // TODO altre combo
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                // titolarioCombo.setDecodeMap(new DecodeMap());
                // TODO altre combo
            }
        } else {
            strutCombo.setDecodeMap(new DecodeMap());
            if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                // TODO altre combo
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                // titolarioCombo.setDecodeMap(new DecodeMap());
                // TODO altre combo
            }
        }
        return campi;
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ente quando questo è l'unico presente e settare di
     * conseguenza la combo struttura
     *
     * @param idEnte
     *            id ente
     * @param sezione
     *            enumerativo
     * 
     * @throws EMFError
     *             errore generico
     */
    public void checkUniqueEnteInCombo(BigDecimal idEnte, Enum sezione) throws EMFError {
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");

            if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                getForm().getFiltriCriteriRaggrFascicoli().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                getForm().getCreaCriterioRaggrFascicoli().getId_strut().setDecodeMap(mappaStrut);
            }

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di essa
            if (tmpTableBeanStrut.size() == 1) {
                if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                    getForm().getFiltriCriteriRaggrFascicoli().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                    getForm().getCreaCriterioRaggrFascicoli().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                }
            }
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo struttura quando questo è l'unico presente e settare di
     * conseguenza le combo Tipo Fascicolo e numero massimo componenti
     *
     * @param idStrut
     *            id struttura
     * @param sezione
     *            enumerativo
     * 
     * @throws EMFError
     *             errore generico
     */
    public void checkUniqueStrutInCombo(BigDecimal idStrut, Enum sezione) throws EMFError {
        if (idStrut != null) {
            // Ricavo tutti i Tipi Fascicolo per la struttura passata in input
            DecTipoFascicoloTableBean tmpTableBeanTipoFascicolo = tipoFascicoloEjb.getDecTipoFascicoloTableBean(idStrut,
                    false);
            DecodeMap mappaTipoFascicolo = DecodeMap.Factory.newInstance(tmpTableBeanTipoFascicolo, "id_tipo_fascicolo",
                    "nm_tipo_fascicolo");

            // Ricavo tutti i Titolari per la struttura passata in input
            DecTitolTableBean tmpTableBeanTitol = titolariEjb.getDecTitolTableBean(idStrut, false); // TODO: verificare
            DecodeMap mappaTitol = DecodeMap.Factory.newInstance(tmpTableBeanTitol, "id_titol", "nm_titol");

            if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC)) {
                getForm().getFiltriCriteriRaggrFascicoli().getId_tipo_fascicolo().setDecodeMap(mappaTipoFascicolo);
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL)) {
                getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setDecodeMap(mappaTipoFascicolo);
                getForm().getTitolarioDetail().getId_titol().setDecodeMap(mappaTitol);
                settaNumeroMassimoFascicoli(idStrut);
                settaNumeroGiorniScadChius(idStrut);
            }
        }
    }

    @Override
    public JSONObject triggerFiltriCriteriRaggrFascicoliId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriCriteriRaggrFascicoli(),
                ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC);
        return getForm().getFiltriCriteriRaggrFascicoli().asJSON();
    }

    @Override
    public JSONObject triggerFiltriCriteriRaggrFascicoliId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriCriteriRaggrFascicoli(),
                ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC);
        return getForm().getFiltriCriteriRaggrFascicoli().asJSON();
    }

    @Override
    public JSONObject triggerFiltriCriteriRaggrFascicoliId_strutOnTrigger() throws EMFError {
        getForm().getFiltriCriteriRaggrFascicoli().post(getRequest());
        if (getForm().getFiltriCriteriRaggrFascicoli().getId_strut().parse() != null) {
            checkUniqueStrutInCombo(getForm().getFiltriCriteriRaggrFascicoli().getId_strut().parse(),
                    ActionEnums.SezioneCriteriRaggrFasc.FILTRI_CRITERI_RAGGR_FASC);
        } else {
            getForm().getFiltriCriteriRaggrFascicoli().getId_tipo_fascicolo().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriCriteriRaggrFascicoli().asJSON();
    }

    @Override
    public void filterInactiveRecordsCriterioRaggrFascicoliList() throws EMFError {
        int rowIndex = 0;
        int pageSize = WebConstants.DEFAULT_PAGE_SIZE;
        if (getForm().getCriterioRaggrFascicoliList().getTable() != null) {
            rowIndex = getForm().getCriterioRaggrFascicoliList().getTable().getCurrentRowIndex();
            pageSize = getForm().getCriterioRaggrFascicoliList().getTable().getPageSize();
        }

        BaseTable critRaggrFascTableBean = fascicoliEjb.ricercaCriteriRaggrFascicoli(
                getForm().getFiltriCriteriRaggrFascicoli(),
                getForm().getCriterioRaggrFascicoliList().isFilterValidRecords());
        getForm().getCriterioRaggrFascicoliList().setTable(critRaggrFascTableBean);
        getForm().getCriterioRaggrFascicoliList().getTable().setCurrentRowIndex(rowIndex);
        getForm().getCriterioRaggrFascicoliList().getTable().setPageSize(pageSize);

        getForm().getCriterioRaggrFascicoliList().setHideUpdateButton(false);
        getForm().getCriterioRaggrFascicoliList().setUserOperations(true, true, true, true);
        getForm().getCriterioRaggrFascicoliList().setStatus(Status.view);

        forwardToPublisher(getLastPublisher());
    }

    private void settaNumeroMassimoFascicoli(BigDecimal idStrut) {
        long numMaxFasc = fascicoliEjb.getNumFascCriterioStd(idStrut);
        getForm().getCreaCriterioRaggrFascicoli().getNi_max_fasc().setValue("" + numMaxFasc);
    }

    private void settaNumeroGiorniScadChius(BigDecimal idStrut) {
        long numGgScadChius = fascicoliEjb.getNumGgScadCriterioFascStd(idStrut);
        getForm().getCreaCriterioRaggrFascicoli().getNi_tempo_scad_chius().setValue("" + numGgScadChius);
    }

    @Override
    public JSONObject triggerCreaCriterioRaggrFascicoliId_ambienteOnTrigger() throws EMFError {
        getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setDecodeMap(new DecodeMap());
        getForm().getTitolarioDetail().getId_titol().setDecodeMap(new DecodeMap());
        triggerAmbienteGenerico(getForm().getCreaCriterioRaggrFascicoli(),
                ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL);
        return getForm().getCreaCriterioRaggrFascicoli().asJSON();
    }

    @Override
    public JSONObject triggerCreaCriterioRaggrFascicoliId_enteOnTrigger() throws EMFError {
        getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setDecodeMap(new DecodeMap());
        getForm().getCreaCriterioRaggrFascicoli().getNi_max_fasc().setValue("");
        getForm().getTitolarioDetail().getId_titol().setDecodeMap(new DecodeMap());
        triggerEnteGenerico(getForm().getCreaCriterioRaggrFascicoli(),
                ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL);
        return getForm().getCreaCriterioRaggrFascicoli().asJSON();
    }

    @Override
    public JSONObject triggerCreaCriterioRaggrFascicoliId_strutOnTrigger() throws EMFError {
        getForm().getCreaCriterioRaggrFascicoli().post(getRequest());
        if (getForm().getCreaCriterioRaggrFascicoli().getId_strut().parse() != null) {
            checkUniqueStrutInCombo(getForm().getCreaCriterioRaggrFascicoli().getId_strut().parse(),
                    ActionEnums.SezioneCriteriRaggrFasc.CRITERIO_RAGGR_FASC_DETAIL);
        } else {
            getForm().getCreaCriterioRaggrFascicoli().getNm_tipo_fascicolo().setDecodeMap(new DecodeMap());
            getForm().getCreaCriterioRaggrFascicoli().getNi_max_fasc().setValue("");
            getForm().getTitolarioDetail().getId_titol().setDecodeMap(new DecodeMap());
        }
        return getForm().getCreaCriterioRaggrFascicoli().asJSON();
    }

    @Override
    public void duplicaCritButton() throws EMFError {
        loadDuplicaCrit();
    }

    public void duplicaCrit() throws EMFError {
        setTableName(getForm().getCriterioRaggrFascicoliList().getName());
        setRiga(getRequest().getParameter("riga"));
        getForm().getCriterioRaggrFascicoliList().getTable().setCurrentRowIndex(Integer.parseInt(getRiga()));

        loadDuplicaCrit(
                (getForm().getCriterioRaggrFascicoliList().getTable()).getCurrentRow().getString("nm_criterio_raggr"));
    }

    private void loadDuplicaCrit() throws EMFError {
        loadDuplicaCrit(null);
    }

    private void loadDuplicaCrit(String nome) throws EMFError {
        // Carica il rowBean del criterio
        if (nome == null) {
            nome = (getForm().getCriterioRaggrFascicoliList().getTable()).getCurrentRow()
                    .getString("nm_criterio_raggr");
        }
        BaseRow critRB = fascicoliEjb
                .getDecCriterioRaggrFascRowBean(getForm().getCreaCriterioRaggrFascicoli().getId_strut().parse(), nome);

        getSession().setAttribute("insertActive", true);

        // Inizializza le combo della form
        initCriterioRaggrFascCombo(critRB.getBigDecimal("id_strut"));

        // Inizializza le liste della form
        initCriterioRaggrFascLists(critRB.getBigDecimal("id_criterio_raggr_fasc"));

        // Popola i valori delle liste in base ai dati
        loadLists(critRB, false);

        // Popola i valori delle combo in base ai dati
        populateComboFields(critRB);

        critRB.setString("nm_criterio_raggr", null);
        critRB.setString("ds_criterio_raggr", null);
        critRB.setBigDecimal("id_criterio_raggr_fasc", null);

        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        critRB.setTimestamp("dt_istituz", new Timestamp(now.getTimeInMillis()));
        critRB.setTimestamp("dt_soppres", new Timestamp(cal.getTimeInMillis()));

        getForm().getCreaCriterioRaggrFascicoli().copyFromBean(critRB);

        // Popola i valori delle combo ambiente/ente/struttura partendo dal valore preso da DB del criterio
        initComboAmbienteEnteStrutCreaCriteriRaggrFasc(critRB.getBigDecimal("id_strut"));
        // Nel caso il tab corrente sia quello dei dati specifici, nasconde i bottoni della barra
        getForm().getCriterioRaggrFascicoliList().setHideUpdateButton(false);

        getForm().getCreaCriterioRaggrFascicoli().setEditMode();
        getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(true);
        getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(false);
        getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.insert);
        getForm().getCriterioRaggrFascicoliList().setStatus(Status.insert);
        getForm().getTitolariList().setStatus(Status.insert);
        getForm().getTitolarioDetail().setStatus(Status.insert);

        // I campi riferiti ad ambiente/ente/struttura rimangono in view mode
        getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().setViewMode();
        getForm().getCreaCriterioRaggrFascicoli().getId_ente().setViewMode();
        getForm().getCreaCriterioRaggrFascicoli().getId_strut().setViewMode();

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    @Override
    public void logEventiCriteriRaggruppamento() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp().setValue(configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC);
        BaseRow riga = (BaseRow) getForm().getCriterioRaggrFascicoliList().getTable().getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(riga.getBigDecimal("id_criterio_raggr_fasc").toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }

    @Override
    public void inserisciVoceClassificazione() throws EMFError {
        getForm().getCreaCriterioRaggrFascicoli().post(getRequest());
        redirectToInserisciVoceClassificazionePage();
    }

    /**
     * Metodo richiamato alla pressione del stato "SI", sia per conferma su controllo numero massimo fascicoli
     * ("controllo1") sia per conferma su controllo criterio standard ("controllo2"). Con JavaScript attivato il tasto è
     * quello presente nella finestra popup, con JavaScript disattivato invece il tasto (preventivamente allineato tra i
     * componenti) è quello che compare in alto nella pagina
     *
     * @throws Throwable
     *             errore generico
     */
    @Override
    public void confermaSalvataggioCriterio() throws Throwable {
        if (getSession().getAttribute("salvataggioAttributes") != null) {
            Object[] sa = (Object[]) getSession().getAttribute("salvataggioAttributes");
            String tipoControllo = (String) sa[6];
            // TODO: verificare rimozione controllo1
            if (tipoControllo != null && tipoControllo.equals("controllo1")) {
                eseguiPrimoStepSalvataggioCriterioRaggrFascicoli((CreaCriterioRaggrFascicoli) sa[0], (Object[]) sa[1],
                        (String) sa[2], (BigDecimal) sa[3], (BigDecimal) sa[4], ((BigDecimal) sa[5]).intValue(),
                        (String) sa[7], (List<BigDecimal>) sa[8]);
            }
            if (tipoControllo != null && tipoControllo.equals("controllo2")) {
                eseguiSalvataggioCriterioRaggrFascicoli((CreaCriterioRaggrFascicoli) sa[0], (Object[]) sa[1],
                        (String) sa[2], (BigDecimal) sa[3], (BigDecimal) sa[4], ((BigDecimal) sa[5]).intValue(),
                        (String) sa[7], (List<BigDecimal>) sa[8]);
            }
        }
        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    @Override
    public void annullaSalvataggioCriterio() throws Throwable {
        // Nascondo i bottoni con javascript disattivato
        getForm().getCriterioCustomMessageButtonList().setViewMode();
        getSession().removeAttribute("salvataggioAttributes");
        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    public void eseguiPrimoStepSalvataggioCriterioRaggrFascicoli(CreaCriterioRaggrFascicoli filtri,
            Object[] anniFascicoliValidati, String nome, BigDecimal annoDa, BigDecimal annoA, int status,
            String standardOld, List<BigDecimal> voceTitolList) throws EMFError {
        // Controllo se il criterio è STANDARD
        String criterioStandardNow = isCriterioRaggrFascicoliStandard(filtri, anniFascicoliValidati, voceTitolList)
                ? "1" : "0";

        // Se sono in UPDATE controllo l'eventuale variazione del flag "Criterio standard"
        if (status == CRITERIO_EDIT) {
            if (standardOld.equals("1") && criterioStandardNow.equals("0")) {
                getForm().getCriterioCustomMessageButtonList().setEditMode();
                Object[] sa = new Object[9];
                sa[0] = filtri;
                sa[1] = anniFascicoliValidati;
                sa[2] = nome;
                sa[3] = annoDa;
                sa[4] = annoA;
                sa[5] = new BigDecimal(status);
                sa[6] = "controllo2";
                sa[7] = criterioStandardNow;
                sa[8] = voceTitolList;
                getSession().setAttribute("salvataggioAttributes", sa);
                getRequest().setAttribute("customBox2", true);
            } else {
                eseguiSalvataggioCriterioRaggrFascicoli(filtri, anniFascicoliValidati, nome, annoDa, annoA, status,
                        criterioStandardNow, voceTitolList);
            }
        } // Altrimenti passo direttamente al salvataggio
        else {
            eseguiSalvataggioCriterioRaggrFascicoli(filtri, anniFascicoliValidati, nome, annoDa, annoA, status,
                    criterioStandardNow, voceTitolList);
        }
    }

    public void eseguiSalvataggioCriterioRaggrFascicoli(CreaCriterioRaggrFascicoli filtri,
            Object[] anniFascicoliValidati, String nome, BigDecimal annoDa, BigDecimal annoA, int status,
            String criterioStandard, List<BigDecimal> voceTitolList) throws EMFError {
        try {
            // Salvataggio criterio
            long idCritRaggrFasc;
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_APPLIC, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (status == CRITERIO_EDIT) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
            }

            if ((idCritRaggrFasc = fascicoliEjb.saveCriterioRaggrFascicoli(param, filtri, anniFascicoliValidati,
                    filtri.getId_strut().parse(), nome, criterioStandard, voceTitolList)) > 0) {
                // Una volta salvato, reimposto i dati corretti nei filtri degli anni
                // if (annoDa != null) {
                // filtri.getAa_fascicolo_da().setValue(annoDa.toString());
                // }
                // if (annoA != null) {
                // filtri.getAa_fascicolo_a().setValue(annoA.toString());
                // }

                // e del flag standard
                filtri.getFl_criterio_raggr_standard().setValue(criterioStandard);

                getMessageBox().addMessage(
                        new Message(MessageLevel.INF, "Criterio di raggruppamento fascicoli salvato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                BaseRow critRBModificato = (BaseRow) fascicoliEjb
                        .getDettaglioCriterioRaggrFascicolo(new BigDecimal(idCritRaggrFasc));

                if (status == CRITERIO_INSERT) {
                    // aggiungo il nuovo criterio alla lista
                    if (getForm().getCriterioRaggrFascicoliList().getTable() == null) {
                        getForm().getCriterioRaggrFascicoliList().setTable(new BaseTable());
                    }
                    getForm().getCriterioRaggrFascicoliList().getTable().add().copyFromBaseRow(critRBModificato);
                    getForm().getCriterioRaggrFascicoliList().setStatus(Status.update);
                    getForm().getTitolariList().setStatus(Status.update);

                    // Devo popolare i campi ambiente/ente/struttura versione "casella di testo"...
                    getForm().getCreaCriterioRaggrFascicoli().getNm_ambiente()
                            .setValue(getForm().getCreaCriterioRaggrFascicoli().getId_ambiente().getDecodedValue());
                    getForm().getCreaCriterioRaggrFascicoli().getNm_ente()
                            .setValue(getForm().getCreaCriterioRaggrFascicoli().getId_ente().getDecodedValue());
                    getForm().getCreaCriterioRaggrFascicoli().getNm_strut()
                            .setValue(getForm().getCreaCriterioRaggrFascicoli().getId_strut().getDecodedValue());
                } else {
                    // Setto il nome nella lista in quanto potrebbe servirmi in fase di cancellazione
                    (getForm().getCriterioRaggrFascicoliList().getTable()).getCurrentRow()
                            .setString("nm_criterio_raggr", filtri.getNm_criterio_raggr().parse());
                }

                getForm().getCreaCriterioRaggrFascicoli().setViewMode();
                getForm().getCreaCriterioRaggrFascicoli().setStatus(Status.view);
                getForm().getCriterioRaggrFascicoliList().setStatus(Status.view);
                getForm().getTitolariList().setStatus(Status.view);
                getForm().getTitolarioDetail().setStatus(Status.view);

                getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setEditMode();
                getForm().getCreaCriterioRaggrFascicoli().getDuplicaCritButton().setHidden(false);
                getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setEditMode();
                getForm().getCreaCriterioRaggrFascicoli().getLogEventiCriteriRaggruppamento().setHidden(false);
                getForm().getCreaCriterioRaggrFascicoli().getInserisciVoceClassificazione().setHidden(true);

                // Nascondo i bottoni con javascript disattivato
                getForm().getCriterioCustomMessageButtonList().setViewMode();
            } else {
                // Errore
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Errore nel salvataggio del criterio di raggruppamento fascicoli"));
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
    }

    private void redirectToInserisciVoceClassificazionePage() throws EMFError {
        getForm().getIdFields().getId_strut()
                .setValue("" + getForm().getCreaCriterioRaggrFascicoli().getId_strut().parse());

        if (((ComboBox) getForm().getTitolarioDetail().getComponent("id_titol")).getValue() == null) {
            getForm().getTitolariTree().setTable(new BaseTable());
        }

        getForm().getTitolarioDetail().setEditMode();
        getForm().getTitolarioDetail().getCd_composito_voce_titol().setViewMode();
        getForm().getTitolarioDetail().getSelezionaTitolario().setEditMode();

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_VOCE_TITOL);
    }

    public void loadTitolarioTree(BigDecimal idTitol) throws EMFError {
        DecVTreeTitolTableBean treeTableBean = titolariEjb.getDecVociTreeTableBean(idTitol,
                Calendar.getInstance().getTime(), true);
        getForm().getTitolariTree().setTable(treeTableBean);
    }

    @Override
    public void selezionaTitolario() throws EMFError {
        selectIndiceClassificazione();
    }

    /**
     * Metodo per la selezione in creazione o modifica dell'indice classificazione
     *
     * @param status
     *            Lo stato della form, in modifica o creazione
     * 
     * @throws EMFError
     *             errore generico
     */
    private void selectIndiceClassificazione() throws EMFError {
        BigDecimal idTitol = getForm().getTitolarioDetail().getId_titol().parse();
        BigDecimal idVoceTitol = getForm().getTitolarioDetail().getId_voce_titol().parse();

        // Controllo su campi
        if (idTitol == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Titolario obbligatorio <br>"));
        } else if (idVoceTitol == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Indice classificazione obbligatorio: valorizzare con il codice composito "
                            + "della voce selezionata dall'albero di titolario <br>"));
        } else {

            List<BigDecimal> voceTitolList = new ArrayList<>();
            for (BaseRowInterface row : (BaseTableInterface<?>) getForm().getTitolariList().getTable()) {
                voceTitolList.add(row.getBigDecimal("id_voce_titol"));
            }

            // Controllo che non esista per quel criterio di raggruppamento fascicolo uno stesso indice classificazione
            if (!voceTitolList.contains(idVoceTitol)) {
                // Aggiungo il record nella lista degli indici di classificazione selezionati
                DecVTreeTitolRowBean row = new DecVTreeTitolRowBean();
                getForm().getTitolarioDetail().copyToBean(row);
                getForm().getTitolariList().add(row);
                getForm().getTitolariList().getTable()
                        .addSortingRule(getForm().getTitolariList().getNm_titol().getName(), SortingRule.ASC);
                getForm().getTitolariList().getTable().sort();
            } else {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Indice classificazione già esistente per il criterio di raggruppamento fascicoli <br>"));
            }
        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_VOCE_TITOL);
        } else {
            forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
        }
    }

    /**
     * Bottone "-" della "Lista dei titolari selezionati" per rimuovere una voce da questa lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void selectTitolariList() throws EMFError {
        getForm().getCreaCriterioRaggrFascicoli().post(getRequest());
        /* Ricavo l'indice del record interessato della "Lista dei titolari selezionati" */
        int index = getForm().getTitolariList().getTable().getCurrentRowIndex();
        /* Lo tolgo dalla lista dei titolari selezionati */
        getForm().getTitolariList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getTitolariList().getTable().getCurrentPageIndex();
        int inizio = getForm().getTitolariList().getTable().getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getTitolariList(), paginaCorrente);
        // Ritorno alla pagina
        getForm().getTitolariList().getTable().setCurrentRowIndex(inizio);

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_FASC_DETAIL);
    }

    public void triggerTitolarioDetailId_titolOnTrigger() throws EMFError {
        if (getForm().getIdFields().getId_strut() != null && getForm().getIdFields().getId_strut().parse() != null
                && getRequest().getParameter("Id_titol") != null && !getRequest().getParameter("Id_titol").isEmpty()) {
            String idTitol = getRequest().getParameter("Id_titol");
            loadTitolarioTree(new BigDecimal(idTitol));
            getForm().getTitolarioDetail().clear();
            ((ComboBox) getForm().getTitolarioDetail().getComponent("id_titol")).setValue(idTitol);
        } else {
            getForm().getTitolariTree().setTable(new BaseTable());
            getForm().getTitolarioDetail().clear();
        }

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_VOCE_TITOL);
    }

    public void triggerTitolarioDetailCd_composito_voce_titolOnTrigger() throws EMFError {
        BaseRow row = titolariEjb.getDecVoceTitolRowBean(new BigDecimal(getRequest().getParameter("Id_voce_titol")));
        if (row != null) {
            getForm().getTitolarioDetail().copyFromBean(row);
        } else {
            String idTitol = getForm().getTitolarioDetail().getId_titol().getValue();
            getForm().getTitolarioDetail().clear();
            ((ComboBox) getForm().getTitolarioDetail().getComponent("id_titol")).setValue(idTitol);
        }

        redirectToAjax(getForm().getTitolarioDetail().asJSON());
    }

}
