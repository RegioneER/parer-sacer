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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.ejb.SistemaMigrazioneEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.slite.gen.form.GestioneLogEventiForm;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.sacerlog.util.web.SpagoliteLogUtil;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.CriteriRaggruppamentoAbstractAction;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm.CreaCriterioRaggr;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm.FiltriCriteriRaggr;
import it.eng.parer.slite.gen.form.StrutTipiForm;
import it.eng.parer.slite.gen.form.StruttureForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice;
import it.eng.parer.slite.gen.tablebean.DecCriterioFiltroMultiploRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioFiltroMultiploTableBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableDescriptor;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrRowBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrTableBean;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.dto.CriterioRaggrStandardBean;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ActionEnums;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.ComboUtil;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.CriteriRaggruppamentoValidator;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
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
 * @author Gilioli_P
 * @author Bonora_L
 */
public class CriteriRaggruppamentoAction extends CriteriRaggruppamentoAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(CriteriRaggruppamentoAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggrHelper")
    private CriteriRaggrHelper crHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggruppamentoEjb")
    private CriteriRaggruppamentoEjb criteriRaggruppamentoEjb;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SistemaMigrazioneEjb")
    private SistemaMigrazioneEjb sysMigrazioneEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;

    private DecodeMap mappaTipoUD;
    private DecodeMap mappaRegistro;
    private DecodeMap mappaTipoDoc;
    private DecodeMap mappaSisMig;
    private static final int CRITERIO_INSERT = 0;
    private static final int CRITERIO_EDIT = 1;

    @Override
    public void initOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {

        // Se ho cliccato sul dettaglio di un Criterio di Raggruppamento
        if (getTableName().equals(getForm().getCriterioRaggrList().getName())) {
            if (!getNavigationEvent().equals(NE_DETTAGLIO_INSERT)) {
                // Carica il rowBean del criterio
                BigDecimal id = getForm().getCriterioRaggrList().getTable().getCurrentRow()
                        .getBigDecimal("id_criterio_raggr");
                DecCriterioRaggrRowBean critRB = crHelper.getDecCriterioRaggrById(id);
                // MEV#31945 - Eliminare validazione elenco UD con firma
                // Se si è in modifica e nel tipo validazione c'era FIRMA lo svuota!
                if (getTableName().equals(getForm().getCriterioRaggrList().getName())
                        && getNavigationEvent().equals(NE_DETTAGLIO_UPDATE) && critRB.getTiValidElenco() != null
                        && critRB.getTiValidElenco().equals(
                                it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.FIRMA.name())) {
                    critRB.setTiValidElenco("");
                }

                getForm().getCreaCriterioRaggr().copyFromBean(critRB);

                // Inizializza le combo della form
                initCriterioRaggrCombo(critRB.getIdStrut());

                // Popola i valori delle combo in base ai dati
                populateComboFields(critRB);

                // Popola i valori delle combo ambiente/ente/struttura
                initComboAmbienteEnteStrutCreaCriteriRaggr(critRB.getIdStrut());
                caricaGestioneElencoCriterioByAmbiente(getForm().getCreaCriterioRaggr().getId_ambiente().parse());

                // Metto in viewMode anche lista e campi
                getForm().getCreaCriterioRaggr().setViewMode();
                getForm().getCreaCriterioRaggr().setStatus(Status.view);
                getForm().getCriterioRaggrList().setStatus(Status.view);

                // Aggiungo la versione "casella di testo" di ambiente/ente/struttura
                setAmbienteEnteStrutturaDesc(critRB.getIdStrut());

                String cessato = (String) getRequest().getParameter("cessato");
                if (StringUtils.isNotBlank(cessato) && "1".equals(cessato)) {
                    getForm().getCriterioRaggrList().setUserOperations(true, false, false, false);
                }
            }
        }
    }

    /**
     * Metodo di inizializzazione delle combo comuni a creazione criterio, ricerca unità documentarie e creazione volume
     *
     * @throws EMFError
     *             errore generico
     */
    private void initDecodeMapRegistroTipoUdTipoDocByIdStrut(BigDecimal idStrut) throws EMFError {
        if (idStrut != null) {
            // Setto i valori della combo TIPO UNITA DOC ricavati dalla tabella DEC_TIPO_UNITA_DOC
            DecTipoUnitaDocTableBean tmpTableBeanTipoUD = tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut);
            mappaTipoUD = new DecodeMap();
            mappaTipoUD.populatedMap(tmpTableBeanTipoUD, "id_tipo_unita_doc", "nm_tipo_unita_doc");

            // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
            DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(idStrut, null);
            mappaTipoDoc = new DecodeMap();
            mappaTipoDoc.populatedMap(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc");

            // Setto i valori della combo TIPO REGISTRO ricavati dalla tabella DEC_REGISTRO_UNITA_DOC
            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getDecRegistroUnitaDocTableBean(idStrut);
            mappaRegistro = new DecodeMap();
            mappaRegistro.populatedMap(tmpTableBeanReg, "id_registro_unita_doc", "cd_registro_unita_doc");

            // Setto i valori della combo SISTEMI DI MIGRAZIONE ricavati dalla tabella DEC_XSD_DATI_SPEC
            BaseTableInterface tmpTableBeanSisMig = sysMigrazioneEjb.getNmSistemaMigrazTableBean(idStrut);
            mappaSisMig = new DecodeMap();
            mappaSisMig.populatedMap(tmpTableBeanSisMig, "nm_sistema_migraz", "nm_sistema_migraz");
        }
    }

    /**
     * Metodo di inizializzazione combo di dettaglio, utilizzate sia nel dettaglio unità documentaria sia nel dettaglio
     * criteri
     *
     * @throws EMFError
     *             errore generico
     */
    private void initCriterioRaggrCombo(BigDecimal idStrut) throws EMFError {
        initDecodeMapRegistroTipoUdTipoDocByIdStrut(idStrut);
        getForm().getCreaCriterioRaggr().getTi_scad_chius_volume().setDecodeMap(ComboGetter
                .getMappaOrdinalGenericEnum("ti_scad_chius_volume", VolumeEnums.ExpirationTypeEnum.values()));
        getForm().getCreaCriterioRaggr().getTi_tempo_scad_chius().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_tempo_scad_chius", VolumeEnums.TimeTypeEnum.values()));
        getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setDecodeMap(mappaTipoUD);
        getForm().getCreaCriterioRaggr().getFl_unita_doc_firmato().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getCreaCriterioRaggr().getTi_esito_verif_firme().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_esito_verif_firme", VolumeEnums.StatoVerifica.values()));
        getForm().getCreaCriterioRaggr().getFl_forza_accettazione().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getCreaCriterioRaggr().getFl_forza_conservazione()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getCreaCriterioRaggr().getNm_tipo_doc().setDecodeMap(mappaTipoDoc);
        getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setDecodeMap(mappaRegistro);
        getForm().getCreaCriterioRaggr().getTi_conservazione().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_conservazione", VolumeEnums.TipoConservazione.values()));
        getForm().getCreaCriterioRaggr().getNm_sistema_migraz().setDecodeMap(mappaSisMig);

        // getForm().getCreaCriterioRaggr().getTi_gest_elenco_criterio().setDecodeMap(ComboGetter
        // .getMappaSortedGenericEnum("ti_gest_elenco_criterio", ElencoEnums.GestioneElencoEnum.values()));
        // boolean flSigilloAttivo = Boolean.parseBoolean(configurationHelper.getValoreParamApplic(
        // CostantiDB.ParametroAppl.FL_ABILITA_SIGILLO, getForm().getCreaCriterioRaggr().getId_ambiente().parse(),
        // null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE));
        //
        // getForm().getCreaCriterioRaggr().getTi_gest_elenco_criterio()
        // .setDecodeMap(ComboGetter.getMappaTiGestElencoCriterio(flSigilloAttivo));
    }

    private void caricaGestioneElencoCriterioByAmbiente(BigDecimal idAmbiente) {
        if (idAmbiente != null) {
            boolean flSigilloAttivo = Boolean.parseBoolean(configurationHelper
                    .getValoreParamApplicByAmb(CostantiDB.ParametroAppl.FL_ABILITA_SIGILLO, idAmbiente));
            getForm().getCreaCriterioRaggr().getTi_gest_elenco_criterio()
                    .setDecodeMap(ComboGetter.getMappaTiGestElencoCriterio(flSigilloAttivo));
        }
    }

    /**
     * Inizializza i FILTRI DI RICERCA CRITERI di raggruppamento in base alla struttura con la quale l'utente è loggato
     *
     * @throws EMFError
     *             errore generico
     */
    private void initFiltriCriteriRaggr() throws EMFError {
        // Azzero i filtri
        getForm().getFiltriCriteriRaggr().reset();
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
        getForm().getFiltriCriteriRaggr().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriCriteriRaggr().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriCriteriRaggr().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriCriteriRaggr().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriCriteriRaggr().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriCriteriRaggr().getId_strut().setValue(idStruttura.toString());

        // Imposto le combo "Standard", "Attivo", "Tipo validazione", "Modalità validazione" e "Tipo gestione elenchi
        // indici AIP"
        getForm().getFiltriCriteriRaggr().getFl_criterio_raggr_standard()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriCriteriRaggr().getFl_criterio_raggr_fisc()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriCriteriRaggr().getCriterio_attivo().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriCriteriRaggr().getTi_valid_elenco().setDecodeMap(ComboGetter.getMappaTiValidElenco());
        getForm().getFiltriCriteriRaggr().getTi_mod_valid_elenco().setDecodeMap(ComboGetter.getMappaTiModValidElenco());
        // Nella fase di ricerca i valori si mettono tutti indipendentemente dalla configurazione per ambiente
        // boolean flSigilloAttivo = Boolean
        // .parseBoolean(configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.FL_ABILITA_SIGILLO,
        // idAmbiente, null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE));
        boolean flSigilloAttivo = true;
        getForm().getFiltriCriteriRaggr().getTi_gest_elenco_criterio()
                .setDecodeMap(ComboGetter.getMappaTiGestElencoCriterio(flSigilloAttivo));

        // Imposto le combo di Tipo Registro, Tipo Unita Doc e Tipo Documento
        checkUniqueStrutInCombo(idStruttura, ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR);

        // Imposto i filtri in editMode
        getForm().getFiltriCriteriRaggr().setEditMode();

        // Imposto come visibile il bottone di ricerca criteri di raggruppamento e disabilito la clessidra (per IE)
        getForm().getFiltriCriteriRaggr().getRicercaCriteriRaggrButton().setEditMode();
        getForm().getFiltriCriteriRaggr().getRicercaCriteriRaggrButton().setDisableHourGlass(true);
    }

    /**
     * Inizializza le combo ambiente/ente/struttura del DETTAGLIO DI UN CRITERIO di raggruppamento, ricavando i valori
     * da una struttura impostata
     *
     */
    private void initComboAmbienteEnteStrutCreaCriteriRaggr(BigDecimal idStrut) {
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
        getForm().getCreaCriterioRaggr().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getCreaCriterioRaggr().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getCreaCriterioRaggr().getId_ente().setDecodeMap(mappaEnte);
        getForm().getCreaCriterioRaggr().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getCreaCriterioRaggr().getId_strut().setDecodeMap(mappaStrut);
        getForm().getCreaCriterioRaggr().getId_strut().setValue(idStrut.toString());

        // Popolo i campi riferiti all'ambiente
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setViewMode();
        String tiGestElencoNoStd = configurationHelper
                .getValoreParamApplicByStrut(CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD, idAmbiente, idStrut);
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setValue(tiGestElencoNoStd);
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setViewMode();
        String tiGestElencoStdFisc = configurationHelper
                .getValoreParamApplicByStrut(CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC, idAmbiente, idStrut);
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setValue(tiGestElencoStdFisc);
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setViewMode();
        String tiGestElencoStdNoFisc = configurationHelper
                .getValoreParamApplicByStrut(CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC, idAmbiente, idStrut);
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setValue(tiGestElencoStdNoFisc);
        getForm().getCreaCriterioRaggr().getTi_valid_elenco().setDecodeMap(ComboGetter.getMappaTiValidElenco());

        // MEV#31945 - Eliminare validazione elenco UD con firma
        if (getTableName().equals(getForm().getCriterioRaggrList().getName()) && getNavigationEvent() != null
                && (getNavigationEvent().equals(NE_DETTAGLIO_INSERT)
                        || getNavigationEvent().equals(NE_DETTAGLIO_UPDATE))) {
            getForm().getCreaCriterioRaggr().getTi_valid_elenco()
                    .setDecodeMap(ComboUtil.getTipiValidazioneCriteriRaggruppamentoSenzaFirma());
        } else {
            getForm().getCreaCriterioRaggr().getTi_valid_elenco().setDecodeMap(ComboGetter.getMappaTiValidElenco());
        }
        getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().setDecodeMap(ComboGetter.getMappaTiModValidElenco());

        // getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco()
        // .setDecodeMap(ComboUtil.getTipiValidazioneCriteriRaggruppamentoSenzaFirma());
        // getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().setDecodeMap(ComboGetter.getMappaTiModValidElenco());
        if (getForm().getCreaCriterioRaggr().getTi_valid_elenco().getValue() == null
                || getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().getValue() == null) {
            getForm().getCreaCriterioRaggr().getTi_valid_elenco().setValue(configurationHelper
                    .getValoreParamApplicByStrut(CostantiDB.ParametroAppl.TI_VALID_ELENCO, idAmbiente, idStrut));
            getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().setValue(configurationHelper
                    .getValoreParamApplicByStrut(CostantiDB.ParametroAppl.TI_MOD_VALID_ELENCO, idAmbiente, idStrut));
        }

    }

    /**
     * Metodo che popola le combobox/MultiSelect della form di dettaglio criterio con i dati forniti e i dati specifici
     * se presenti
     *
     * @param critRB
     *            Rowbean del criterio di raggruppamento
     *
     * @return il tableBean dei dati specifici
     *
     * @throws EMFError
     *             errore generico
     */
    private void populateComboFields(DecCriterioRaggrRowBean critRB) throws EMFError {
        String[] registroUniDoc = null;
        String[] tipoDoc = null;
        String[] tipoEsitoVerifFirme = null;
        String[] tipoUniDoc = null;
        String[] sisMigr;

        int counter = 0;
        if (critRB.getFlFiltroTipoDoc().equals("1")) {
            DecCriterioFiltroMultiploTableBean critMult = crHelper.getCriteriRaggrFiltri(critRB.getIdCriterioRaggr(),
                    ActionEnums.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
            tipoDoc = new String[critMult.size()];
            for (DecCriterioFiltroMultiploRowBean row : critMult) {
                tipoDoc[counter++] = row.getIdTipoDoc().toString();
            }
        }
        counter = 0;
        if (critRB.getFlFiltroTipoUnitaDoc().equals("1")) {
            DecCriterioFiltroMultiploTableBean critMult = crHelper.getCriteriRaggrFiltri(critRB.getIdCriterioRaggr(),
                    ActionEnums.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
            tipoUniDoc = new String[critMult.size()];
            for (DecCriterioFiltroMultiploRowBean row : critMult) {
                tipoUniDoc[counter++] = row.getIdTipoUnitaDoc().toString();
            }
        }
        counter = 0;
        {
            DecCriterioFiltroMultiploTableBean critMult = crHelper.getCriteriRaggrFiltri(critRB.getIdCriterioRaggr(),
                    ActionEnums.TipoFiltroMultiploCriteriRaggr.SISTEMA_MIGRAZ.name());
            sisMigr = new String[critMult.size()];
            for (DecCriterioFiltroMultiploRowBean row : critMult) {
                sisMigr[counter++] = row.getNmSistemaMigraz();
            }
        }
        counter = 0;
        if (critRB.getFlFiltroRegistroKey().equals("1")) {
            DecCriterioFiltroMultiploTableBean critMult = crHelper.getCriteriRaggrFiltri(critRB.getIdCriterioRaggr(),
                    ActionEnums.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
            registroUniDoc = new String[critMult.size()];
            for (DecCriterioFiltroMultiploRowBean row : critMult) {
                registroUniDoc[counter++] = row.getIdRegistroUnitaDoc().toString();
            }
        }
        counter = 0;
        if (critRB.getFlFiltroTiEsitoVerifFirme().equals("1")) {
            DecCriterioFiltroMultiploTableBean critMult = crHelper.getCriteriRaggrFiltri(critRB.getIdCriterioRaggr(),
                    ActionEnums.TipoFiltroMultiploCriteriRaggr.TIPO_ESITO_VERIF_FIRME.name());
            tipoEsitoVerifFirme = new String[critMult.size()];
            for (DecCriterioFiltroMultiploRowBean row : critMult) {
                tipoEsitoVerifFirme[counter++] = row.getTiEsitoVerifFirme();
            }
        }
        getForm().getCreaCriterioRaggr().getNm_tipo_doc().setValues(tipoDoc);
        getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setValues(tipoUniDoc);
        getForm().getCreaCriterioRaggr().getNm_sistema_migraz().setValues(sisMigr);
        getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setValues(registroUniDoc);
        getForm().getCreaCriterioRaggr().getTi_esito_verif_firme().setValues(tipoEsitoVerifFirme);
        getForm().getCreaCriterioRaggr().getFl_unita_doc_firmato().setValue(critRB.getFlUnitaDocFirmato());
        getForm().getCreaCriterioRaggr().getFl_forza_accettazione().setValue(critRB.getFlForzaAccettazione());
        getForm().getCreaCriterioRaggr().getFl_forza_conservazione().setValue(critRB.getFlForzaConservazione());
    }

    @Override
    public void undoDettaglio() throws EMFError {
        getSession().removeAttribute("insertActive");
        if (getForm().getCriterioRaggrList().getStatus().equals(Status.insert)) {
            goBack();
        } else {
            if (getLastPublisher().equals(Application.Publisher.CRITERIO_RAGGR_DETAIL)
                    && getForm().getCriterioRaggrList().getStatus().equals(Status.update)) {
                getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(false);
                loadDettaglio();
                getForm().getCreaCriterioRaggr().getLogEventiCriteriRaggruppamento().setEditMode();
            }
            forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        // MEV#31945 - Eliminare validazione elenco UD con firma
        // Corretto il bug che se si andava in inserimento senza aver selezionato una struttura da ricercare esplodeva.
        String id_strut = getForm().getFiltriCriteriRaggr().getId_strut().getValue();
        if (getForm().getFiltriCriteriRaggr().getId_strut().getValue() != null
                && getForm().getFiltriCriteriRaggr().getId_strut().getValue().equals("")) {
            getMessageBox().addInfo("Per inserire un nuovo criterio di raggruppamento selezionale una struttura.");
            forwardToPublisher(getLastPublisher());
        } else {
            // Richiamo il metodo per la creazione ex-novo di un criterio di raggruppamento
            creaCriterioRaggr();
            caricaGestioneElencoCriterioByAmbiente(getForm().getCreaCriterioRaggr().getId_ambiente().parse());
        }
    }

    @Override
    public void saveDettaglio() throws EMFError {
        // Se lo status della form è 'update', la form è in modifica,
        // Altrimenti è in modalità inserimento
        if (getForm().getCreaCriterioRaggr().getStatus().equals(Status.update)) {
            salvaCriterioRaggr(CRITERIO_EDIT);
        } else if (getForm().getCreaCriterioRaggr().getStatus().equals(Status.insert)) {
            salvaCriterioRaggr(CRITERIO_INSERT);
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getRequest().getParameter("table").equals(getForm().getCriterioRaggrList().getName())
                && !getNavigationEvent().equals(NE_DETTAGLIO_DELETE)) {
            // Lista criteri di raggruppamento
            getForm().getCreaCriterioRaggr().setViewMode();
            getForm().getCreaCriterioRaggr().setStatus(Status.view);
            getForm().getCriterioRaggrList().setStatus(Status.view);
            getForm().getCreaCriterioRaggr().getDuplicaCritButton().setEditMode();
            getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(false);
            getForm().getCreaCriterioRaggr().getLogEventiCriteriRaggruppamento().setEditMode();
            forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getLastPublisher().equals(Application.Publisher.CRITERIO_RAGGR_DETAIL)) {
            if (!getMessageBox().hasError()) {
                goBack();
            }
        } else {
            goBack();
        }
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.LISTA_CRITERI_RAGGR;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        if (publisherName.equals(Application.Publisher.LISTA_CRITERI_RAGGR)) {
            getSession().removeAttribute("insertActive");
            int paginaCorrenteDocNonVers = getForm().getCriterioRaggrList().getTable().getCurrentPageIndex();
            int inizioDocNonVers = getForm().getCriterioRaggrList().getTable().getFirstRowPageIndex();
            int pageSize = getForm().getCriterioRaggrList().getTable().getPageSize();

            try {
                DecVRicCriterioRaggrTableBean critRaggrTableBean = (DecVRicCriterioRaggrTableBean) crHelper
                        .getCriteriRaggr(getForm().getFiltriCriteriRaggr());
                getForm().getCriterioRaggrList().setTable(critRaggrTableBean);
                getForm().getCriterioRaggrList().getTable().setPageSize(pageSize);
                getForm().getCriterioRaggrList().getTable().first();
                getForm().getCriterioRaggrList().setStatus(Status.view);
                // Rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getCriterioRaggrList(), paginaCorrenteDocNonVers);
                // Ritorno alla pagina
                getForm().getCriterioRaggrList().getTable().setCurrentRowIndex(inizioDocNonVers);
                getForm().getCriterioRaggrList().setHideUpdateButton(false);
                getForm().getCriterioRaggrList().setUserOperations(true, true, true, true);
            } catch (EMFError ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.CRITERI_RAGGRUPPAMENTO;
    }

    /**
     * Metodo di caricamento della form di creazione dei criteri di raggruppamento
     *
     * @throws EMFError
     *             errore generico
     */
    public void creaCriterioRaggr() throws EMFError {
        // Apre pagina di creazione criterio
        getForm().getCreaCriterioRaggr().reset();
        getForm().getCreaCriterioRaggr().setEditMode();
        getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(true);
        getForm().getCreaCriterioRaggr().setStatus(Status.insert);
        getForm().getCriterioRaggrList().setStatus(Status.insert);
        getSession().setAttribute("insertActive", true);

        Fields<Field> filtri = null;
        BigDecimal idStrut = null;
        BigDecimal idRegistroUnitaDoc = null;
        BigDecimal idTipoUnitaDoc = null;
        BigDecimal idTipoDoc = null;

        // Se provengo da Ricerca Unità Documentaria...
        if (getRequest().getParameter("provenienza") != null
                && getRequest().getParameter("provenienza").equals("ricercaUnitaDoc")) {
            // Ricavo i valori dei filtri che ho compilato nella ricerca
            filtri = getSession().getAttribute("filtriUD") != null
                    ? (Fields<Field>) getSession().getAttribute("filtriUD")
                    : new UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice();
            // Imposto il filtro ambiente di "default" relativo all'organizzazione col quale mi sono loggato
            idStrut = getUser().getIdOrganizzazioneFoglia();
            initComboAmbienteEnteStrutCreaCriteriRaggr(idStrut);
        } // Significa che arrivo da dettaglio struttura/dettaglio registro/dettaglio tipo ud/detaglio tipo doc...
        else if (getForm().getIdFields().getId_strut() != null
                && getForm().getIdFields().getId_strut().parse() != null) {
            idStrut = getForm().getIdFields().getId_strut().parse();
            if (getForm().getIdFields().getId_registro_unita_doc() != null) {
                idRegistroUnitaDoc = getForm().getIdFields().getId_registro_unita_doc().parse();
            }
            if (getForm().getIdFields().getId_tipo_unita_doc() != null) {
                idTipoUnitaDoc = getForm().getIdFields().getId_tipo_unita_doc().parse();
            }
            if (getForm().getIdFields().getId_tipo_doc() != null) {
                idTipoDoc = getForm().getIdFields().getId_tipo_doc().parse();
            }
            initComboAmbienteEnteStrutCreaCriteriRaggr(idStrut);
        } // ... altrimenti se ho cliccato direttamente sulla pagina dei criteri
        else {
            // Setto ambiente/ente/struttura in base ai valori della pagina di ricerca criteri
            idStrut = initComboAmbienteEnteStrutturaCreaCriteriRaggrFromRicercaCriteri();
        }

        // Inizializzo le combo "standard" della pagina
        initCriterioRaggrCombo(idStrut);
        // Setto i filtri standard
        populateDefaultValues(filtri, idStrut);
        // Setto il numero massimo componenti con lo stesso valore della struttura impostata
        settaNumeroMassimoComponenti(getForm().getCreaCriterioRaggr().getId_strut().parse());

        // Se registro o tipo ud sono diversi da null, imposto i loro valori nella combo
        if (idRegistroUnitaDoc != null) {
            String[] idRegistroArray = { "" + idRegistroUnitaDoc };
            getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setValues(idRegistroArray);
        } else if (idTipoUnitaDoc != null) {
            String[] idTipoUdArray = { "" + idTipoUnitaDoc };
            getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setValues(idTipoUdArray);
        } else if (idTipoDoc != null) {
            String[] idTipoDocArray = { "" + idTipoDoc };
            getForm().getCreaCriterioRaggr().getNm_tipo_doc().setValues(idTipoDocArray);
        }

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
    }

    /**
     * Inizializza le combo ambiente/ente/struttura del DETTAGLIO DI UN CRITERIO di raggruppamento, ricavando i valori
     * dai filtri di ricerca criterio. Se impostato, restituisce il valore dell'id del filtro relativo alla struttura
     *
     * @return l'id della struttura impostata come filtro di ricerca
     */
    private BigDecimal initComboAmbienteEnteStrutturaCreaCriteriRaggrFromRicercaCriteri() {
        // Inizializzo le combo ambiente/ente/struttura in base ai valori
        // impostati nella pagina di ricerca criteri
        ComboBox ambienteCombo = getForm().getFiltriCriteriRaggr().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        ComboBox enteCombo = getForm().getFiltriCriteriRaggr().getId_ente();
        DecodeMapIF mappaEnte = enteCombo.getDecodeMap();

        ComboBox strutCombo = getForm().getFiltriCriteriRaggr().getId_strut();
        DecodeMapIF mappaStrut = strutCombo.getDecodeMap();

        getForm().getCreaCriterioRaggr().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getCreaCriterioRaggr().getId_ambiente()
                .setValue(getForm().getFiltriCriteriRaggr().getId_ambiente().getValue());

        getForm().getCreaCriterioRaggr().getId_ente().setDecodeMap(mappaEnte);
        getForm().getCreaCriterioRaggr().getId_ente()
                .setValue(getForm().getFiltriCriteriRaggr().getId_ente().getValue());

        getForm().getCreaCriterioRaggr().getId_strut().setDecodeMap(mappaStrut);
        getForm().getCreaCriterioRaggr().getId_strut()
                .setValue(getForm().getFiltriCriteriRaggr().getId_strut().getValue());

        // Popolo i campi riferiti all'ambiente
        OrgAmbienteRowBean ambienteRowBean = ambienteEjb
                .getOrgAmbienteRowBean(new BigDecimal(getForm().getFiltriCriteriRaggr().getId_ambiente().getValue()));
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setViewMode();
        String tiGestElencoNoStd = configurationHelper.getValoreParamApplicByStrut(
                CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD, ambienteRowBean.getIdAmbiente(),
                new BigDecimal(getForm().getFiltriCriteriRaggr().getId_strut().getValue()));
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setValue(tiGestElencoNoStd);
        String tiGestElencoStdFisc = configurationHelper.getValoreParamApplicByStrut(
                CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC, ambienteRowBean.getIdAmbiente(),
                new BigDecimal(getForm().getFiltriCriteriRaggr().getId_strut().getValue()));

        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setValue(tiGestElencoStdFisc);
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setViewMode();
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setViewMode();
        String tiGestElencoStdNofisc = configurationHelper.getValoreParamApplicByStrut(
                CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC, ambienteRowBean.getIdAmbiente(),
                new BigDecimal(getForm().getFiltriCriteriRaggr().getId_strut().getValue()));
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setValue(tiGestElencoStdNofisc);
        // MEV#31945 - Eliminare validazione elenco UD con firma
        if (getNavigationEvent().equals(NE_DETTAGLIO_INSERT)) {
            getForm().getCreaCriterioRaggr().getTi_valid_elenco()
                    .setDecodeMap(ComboUtil.getTipiValidazioneCriteriRaggruppamentoSenzaFirma());
        } else {
            getForm().getCreaCriterioRaggr().getTi_valid_elenco().setDecodeMap(ComboGetter.getMappaTiValidElenco());
        }
        getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().setDecodeMap(ComboGetter.getMappaTiModValidElenco());
        if (getForm().getCreaCriterioRaggr().getTi_valid_elenco().getValue() == null
                || getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().getValue() == null) {
            getForm().getCreaCriterioRaggr().getTi_valid_elenco()
                    .setValue(configurationHelper.getValoreParamApplicByStrut(CostantiDB.ParametroAppl.TI_VALID_ELENCO,
                            ambienteRowBean.getIdAmbiente(),
                            new BigDecimal(getForm().getFiltriCriteriRaggr().getId_strut().getValue())));
            getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco()
                    .setValue(configurationHelper.getValoreParamApplicByStrut(
                            CostantiDB.ParametroAppl.TI_MOD_VALID_ELENCO, ambienteRowBean.getIdAmbiente(),
                            new BigDecimal(getForm().getFiltriCriteriRaggr().getId_strut().getValue())));
        }

        return StringUtils.isNotBlank(getForm().getFiltriCriteriRaggr().getId_strut().getValue())
                ? new BigDecimal(getForm().getFiltriCriteriRaggr().getId_strut().getValue()) : null;
    }

    /**
     * Metodo invocato sul bottone di eliminazione nella lista criteri di raggruppamento o nel dettaglio criterio sulla
     * listNavBar
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteCriterioRaggrList() throws EMFError {
        try {
            DecVRicCriterioRaggrRowBean row = (DecVRicCriterioRaggrRowBean) getForm().getCriterioRaggrList().getTable()
                    .getCurrentRow();
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.REGISTRO_UNITA_DOC_DETAIL)) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriteriRaggruppamentoList()));
            } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_DOC_DETAIL)) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriteriRaggruppamentoList()));
            } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.TIPO_UNITA_DOC_DETAIL)) {
                StrutTipiForm form = (StrutTipiForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriteriRaggruppamentoList()));
            } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.CREA_STRUTTURA)) {
                StruttureForm form = (StruttureForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(
                        SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriteriRaggruppamentoList()));
            } else if (param.getNomePagina().equalsIgnoreCase(Application.Publisher.LISTA_CRITERI_RAGGR)) {
                // Correzione bug in cui mancava la gestione della cancellazione da lista criteri reggr
                CriteriRaggruppamentoForm form = (CriteriRaggruppamentoForm) SpagoliteLogUtil.getForm(this);
                param.setNomeAzione(SpagoliteLogUtil.getDetailActionNameDelete(form, form.getCriterioRaggrList()));
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarDelete());
            }

            if (crHelper.deleteDecCriterioRaggr(param, row.getIdStrut(), row.getNmCriterioRaggr())) {
                getMessageBox()
                        .addMessage(new Message(MessageLevel.INF, "Criterio di raggruppamento eliminato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                if (Application.Publisher.CRITERIO_RAGGR_DETAIL.equals(getLastPublisher())
                        || "".equals(getLastPublisher())) {
                    goBack();
                } else if (Application.Publisher.LISTA_CRITERI_RAGGR.equals(getLastPublisher())) {
                    reloadAfterGoBack(Application.Publisher.LISTA_CRITERI_RAGGR);
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
     * Metodo invocato sul bottone di eliminazione nel dettaglio di un criterio di raggruppamento
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void deleteCreaCriterioRaggr() throws EMFError {
        try {
            DecCriterioRaggrRowBean critRB = crHelper
                    .getDecCriterioRaggrById(getForm().getCreaCriterioRaggr().getId_criterio_raggr().parse());
            if (crHelper.deleteDecCriterioRaggr(null, critRB.getIdStrut(), critRB.getNmCriterioRaggr())) {
                getMessageBox()
                        .addMessage(new Message(MessageLevel.INF, "Criterio di raggruppamento eliminato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                loadListaCriteriRaggr();
            }
        } catch (ParerUserError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    /**
     * Metodo invocato sul bottone di modifica nella lista criteri di raggruppamento
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateCriterioRaggrList() throws EMFError {
        // Imposta la form di dettaglio in edit mode e con status update
        getForm().getCreaCriterioRaggr().setEditMode();
        getForm().getCreaCriterioRaggr().setStatus(Status.update);
        getForm().getCriterioRaggrList().setStatus(Status.update);

        getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(true);

        // I campi riferiti ad ambiente/ente/struttura rimangono in view mode
        getForm().getCreaCriterioRaggr().getId_ambiente().setViewMode();
        getForm().getCreaCriterioRaggr().getId_ente().setViewMode();
        getForm().getCreaCriterioRaggr().getId_strut().setViewMode();

        // I campi riferiti all'ambiente
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setViewMode();
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setViewMode();
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setViewMode();

        BigDecimal idCriterioRaggr = ((DecVRicCriterioRaggrTableBean) getForm().getCriterioRaggrList().getTable())
                .getCurrentRow().getIdCriterioRaggr();

        /*
         * Se il criterio (automatico o no) è già legato a qualche elenco, posso modificare solo data inizio e fine
         * validità, n° max componenti, n° max elenchi giornaliero, tipo scadenza chiusura, scadenza chiusura, flag
         * automatico, note
         */
        if (crHelper.existElvElencoVersPerCriterioRaggr(idCriterioRaggr)) {
            getForm().getCreaCriterioRaggr().setViewMode();
            getForm().getCreaCriterioRaggr().getDt_istituz().setEditMode();
            getForm().getCreaCriterioRaggr().getDt_soppres().setEditMode();
            getForm().getCreaCriterioRaggr().getNi_max_comp().setEditMode();
            getForm().getCreaCriterioRaggr().getNi_max_elenchi_by_gg().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_scad_chius_volume().setEditMode();
            getForm().getCreaCriterioRaggr().getNt_criterio_raggr().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_tempo_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggr().getNi_tempo_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_gest_elenco_criterio().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_valid_elenco().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().setEditMode();
        }

        getSession().setAttribute("nomeCriterio", getForm().getCreaCriterioRaggr().getNm_criterio_raggr().parse());
    }

    /**
     * Metodo invocato sul bottone di modifica nel dettaglio di un criterio di raggruppamento
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateCreaCriterioRaggr() throws EMFError {
        getForm().getCreaCriterioRaggr().setEditMode();
        getForm().getCreaCriterioRaggr().setStatus(Status.update);

        getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(true);
        // I campi riferiti ad ambiente/ente/struttura rimangono in view mode
        getForm().getCreaCriterioRaggr().getId_ambiente().setViewMode();
        getForm().getCreaCriterioRaggr().getId_ente().setViewMode();
        getForm().getCreaCriterioRaggr().getId_strut().setViewMode();
        // I campi riferiti all'ambiente
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setViewMode();
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setViewMode();
        getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setViewMode();

        // Salvo in sessione il nomeCriterio prima di eventuali modifiche per effettuare successivi controlli
        // N.B.: devo fare così per evitare che venga sovrascritto con il "post" dall'eventuale nuovo valore.
        getSession().setAttribute("nomeCriterio", getForm().getCreaCriterioRaggr().getNm_criterio_raggr().parse());

        BigDecimal idCriterioRaggr = ((DecVRicCriterioRaggrTableBean) getForm().getCriterioRaggrList().getTable())
                .getCurrentRow().getIdCriterioRaggr();

        /*
         * Se il criterio (automatico o no) è già legato a qualche elenco, posso modificare solo data inizio e fine
         * validità, n° max componenti, n° max elenchi giornaliero, tipo scadenza chiusura, scadenza chiusura, flag
         * automatico, note, tipo gestione elenchi AIP su criterio
         */
        if (crHelper.existElvElencoVersPerCriterioRaggr(idCriterioRaggr)) {
            getForm().getCreaCriterioRaggr().setViewMode();
            getForm().getCreaCriterioRaggr().getDt_istituz().setEditMode();
            getForm().getCreaCriterioRaggr().getDt_soppres().setEditMode();
            getForm().getCreaCriterioRaggr().getNi_max_comp().setEditMode();
            getForm().getCreaCriterioRaggr().getNi_max_elenchi_by_gg().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_scad_chius_volume().setEditMode();
            getForm().getCreaCriterioRaggr().getNt_criterio_raggr().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_tempo_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggr().getNi_tempo_scad_chius().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_gest_elenco_criterio().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_valid_elenco().setEditMode();
            getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco().setEditMode();
        }

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
    }

    /**
     * Metodo per il caricamento della lista dei criteri di raggruppamento da menu
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.CriteriRaggruppamento.ListaCriteriRaggr")
    public void loadListaCriteriRaggr() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.CriteriRaggruppamento.ListaCriteriRaggr");
        // Azzero tutto sugli IdFields che mi servono se provengo da amministrazione strutture
        getForm().getIdFields().clear();
        // Inizializzo i filtri
        initFiltriCriteriRaggr();
        // Eseguo la ricerca sui filtri pre-impostati
        ricercaCriteri(getForm().getFiltriCriteriRaggr());
    }

    public void ricercaCriteri(FiltriCriteriRaggr filtriCriteri) throws EMFError {
        if (filtriCriteri.validate(getMessageBox())) {
            DecVRicCriterioRaggrTableBean critRaggrTableBean = (DecVRicCriterioRaggrTableBean) crHelper
                    .getCriteriRaggr(filtriCriteri);

            getForm().getCriterioRaggrList().setTable(critRaggrTableBean);
            getForm().getCriterioRaggrList().getTable().setPageSize(10);
            getForm().getCriterioRaggrList().getTable().first();
            getForm().getCriterioRaggrList().setHideUpdateButton(false);
            getForm().getCriterioRaggrList().setUserOperations(true, true, true, true);
            getForm().getCriterioRaggrList().setStatus(Status.view);
            getSession().removeAttribute("listaDatiSpecOnLine");
            forwardToPublisher(Application.Publisher.LISTA_CRITERI_RAGGR);
        }
    }

    /**
     * Metodo per il salvataggio in creazione o modifica del criterio di raggruppamento
     *
     * @param status
     *            Lo stato della form, in modifica o creazione
     *
     * @throws EMFError
     *             errore generico
     */
    private void salvaCriterioRaggr(int status) throws EMFError {
        String standardOld = "0";
        String nome = null;
        String nomeCriterio = (String) getSession().getAttribute("nomeCriterio");
        if (status == CRITERIO_EDIT) {
            if (nomeCriterio != null) {
                nome = nomeCriterio;
            } else {
                nome = ((DecCriterioRaggrRowBean) getForm().getCriterioRaggrList().getTable().getCurrentRow())
                        .getNmCriterioRaggr();
            }
            standardOld = getForm().getCreaCriterioRaggr().getFl_criterio_raggr_standard().parse();
        }
        // Controllo su campi
        CriteriRaggruppamentoForm.CreaCriterioRaggr filtri = getForm().getCreaCriterioRaggr();

        filtri.post(getRequest());
        if (filtri.validate(getMessageBox())) {
            UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
            // Valida data creazione
            Timestamp dataDa = filtri.getDt_creazione_unita_doc_da().parse();
            Timestamp dataA = filtri.getDt_creazione_unita_doc_a().parse();
            Date[] dateCreazioneValidate = validator.validaDate(dataDa,
                    filtri.getOre_dt_creazione_unita_doc_da().parse(),
                    filtri.getMinuti_dt_creazione_unita_doc_da().parse(), dataA,
                    filtri.getOre_dt_creazione_unita_doc_a().parse(),
                    filtri.getMinuti_dt_creazione_unita_doc_a().parse(),
                    filtri.getDt_creazione_unita_doc_da().getHtmlDescription().replace(WebConstants.FORMATO_DATA, ""),
                    filtri.getDt_creazione_unita_doc_a().getHtmlDescription().replace(WebConstants.FORMATO_DATA, ""));
            validator.validaOrdineDateOrari(filtri.getDt_reg_unita_doc_da().parse(),
                    filtri.getDt_reg_unita_doc_a().parse(), filtri.getDt_reg_unita_doc_da().getHtmlDescription(),
                    filtri.getDt_reg_unita_doc_a().getHtmlDescription());
            // Valida Tipo scadenza
            validator.validaTipoScadenza(filtri.getTi_scad_chius_volume().parse(),
                    filtri.getNi_tempo_scad_chius().parse(), filtri.getTi_tempo_scad_chius().parse());
            // Valida Nome criterio
            if (filtri.getNm_criterio_raggr() == null) {
                getMessageBox()
                        .addMessage(new Message(MessageLevel.ERR, "Nome criterio di raggruppamento obbligatorio <br>"));
            } else if (status == CRITERIO_INSERT) {
                // Controllo che non esista su db per quella struttura un criterio con lo stesso nome
                if (crHelper.existNomeCriterio(filtri.getNm_criterio_raggr().parse(), filtri.getId_strut().parse())) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Nome criterio di raggruppamento già esistente per la struttura utilizzata <br>"));
                }
            } else if (status == CRITERIO_EDIT) {
                // Controllo che non esista su db per quella struttura un criterio con lo stesso nome, escluso esso
                // stesso naturalmente
                if (!StringUtils.equals(nome, filtri.getNm_criterio_raggr().parse()) && crHelper
                        .existNomeCriterio(filtri.getNm_criterio_raggr().parse(), filtri.getId_strut().parse())) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Nome criterio di raggruppamento già esistente per la struttura utilizzata <br>"));
                }
            }

            // Valida Descrizione criterio
            if (filtri.getDs_criterio_raggr() == null) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.ERR, "Descrizione criterio di raggruppamento obbligatorio <br>"));
            }
            // Valida Date istituzione e soppressione
            if (filtri.getDt_istituz() == null && filtri.getDt_soppres() == null) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.ERR, "Campi data istituzione e data soppressione obbligatori <br>"));
            } else {
                validator.validaOrdineDateOrari(filtri.getDt_istituz().parse(), filtri.getDt_soppres().parse(),
                        filtri.getDt_istituz().getHtmlDescription(), filtri.getDt_soppres().getHtmlDescription());
            }

            /* Verifica che siano stati selezionati solo tipi documento principali */
            List<BigDecimal> idTipiDocumentoList = filtri.getNm_tipo_doc().parse();
            if (!idTipiDocumentoList.isEmpty() && !crHelper.areAllTipiDocPrincipali(idTipiDocumentoList)) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Attenzione: si richiede di creare un criterio di raggruppamento ma sono presenti tipi documento NON principali! <br>"));
            }

            CriteriRaggruppamentoValidator valCriteri = new CriteriRaggruppamentoValidator(getMessageBox());
            valCriteri.validaFirmaAutomatica(filtri.getTi_valid_elenco().parse(),
                    filtri.getTi_mod_valid_elenco().parse());

            // Valida i campi delle chiavi unità documentaria
            String[] reg = new String[((Set<String>) filtri.getCd_registro_key_unita_doc().getValues()).size()];
            ((Set<String>) filtri.getCd_registro_key_unita_doc().getValues()).toArray(reg);
            validator.validaChiaviUnitaDoc(reg, filtri.getAa_key_unita_doc().parse(),
                    filtri.getCd_key_unita_doc().parse(), filtri.getAa_key_unita_doc_da().parse(),
                    filtri.getAa_key_unita_doc_a().parse(), filtri.getCd_key_unita_doc_da().parse(),
                    filtri.getCd_key_unita_doc_a().parse());

            if (filtri.getAa_key_unita_doc().parse() == null && filtri.getCd_key_unita_doc().parse() == null
                    && filtri.getCd_registro_key_unita_doc().parse().isEmpty()
                    && filtri.getAa_key_unita_doc_da().parse() == null && filtri.getAa_key_unita_doc_a().parse() == null
                    && filtri.getCd_key_unita_doc_da().parse() == null && filtri.getCd_key_unita_doc_a().parse() == null
                    && filtri.getDt_creazione_unita_doc_a().parse() == null
                    && filtri.getDt_creazione_unita_doc_da().parse() == null
                    && filtri.getFl_forza_accettazione().parse() == null
                    && filtri.getFl_forza_conservazione().parse() == null
                    && filtri.getFl_unita_doc_firmato().parse() == null && filtri.getNm_tipo_doc().parse().isEmpty()
                    && filtri.getNm_tipo_unita_doc().parse().isEmpty()
                    && filtri.getTi_esito_verif_firme().parse().isEmpty()
                    && filtri.getDt_reg_unita_doc_da().parse() == null && filtri.getDt_reg_unita_doc_a().parse() == null
                    && filtri.getDl_oggetto_unita_doc().parse() == null && filtri.getDs_autore_doc().parse() == null
                    && filtri.getDl_doc().parse() == null && filtri.getTi_conservazione().parse() == null) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Non è stato inserito alcun filtro per il criterio di raggruppamento oltre ai dati obbligatori <br>"));
            }

            if (!getMessageBox().hasError()) {
                BigDecimal numMaxPerWarning = new BigDecimal(configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_COMP_CRITERIO_RAGGR_WARN));
                BigDecimal numMaxPerErrore = new BigDecimal(configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_COMP_CRITERIO_RAGGR_ERR));
                BigDecimal niMaxComp = filtri.getNi_max_comp().parse();
                niMaxComp = niMaxComp != null ? niMaxComp : new BigDecimal("0");

                // Se il numero massimo di componenti è compreso tra numMaxPerWarning e numMaxPerErrore
                if (niMaxComp.compareTo(numMaxPerWarning) > 0 && niMaxComp.compareTo(numMaxPerErrore) <= 0) {
                    getForm().getCriterioCustomMessageButtonList().setEditMode();
                    Object[] sa = new Object[8];
                    sa[0] = filtri;
                    sa[1] = dateCreazioneValidate;
                    sa[2] = nome;
                    sa[3] = dataDa;
                    sa[4] = dataA;
                    sa[5] = new BigDecimal(status);
                    sa[6] = "controllo1";
                    sa[7] = standardOld;
                    getSession().setAttribute("salvataggioAttributes", sa);
                    getRequest().setAttribute("customBox", numMaxPerWarning);
                } else if (niMaxComp.compareTo(numMaxPerErrore) > 0) {
                    getMessageBox().addError("Attenzione: numero massimo componenti superiore a " + numMaxPerErrore
                            + ": impossibile eseguire il salvataggio");
                } else {
                    eseguiPrimoStepSalvataggioCriterioRaggruppamento(filtri, dateCreazioneValidate, nome, dataDa, dataA,
                            status, standardOld);
                }
            }
        }
        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
    }

    public void eseguiPrimoStepSalvataggioCriterioRaggruppamento(CriteriRaggruppamentoForm.CreaCriterioRaggr filtri,
            Date[] dateCreazioneValidate, String nome, Timestamp dataDa, Timestamp dataA, int status,
            String standardOld) throws EMFError {
        // Controllo se il criterio è STANDARD
        String criterioStandardNow = isCriterioRaggrStandard(filtri, dateCreazioneValidate) ? "1" : "0";

        // Se sono in UPDATE controllo l'eventuale variazione del flag "Criterio standard"
        if (status == CRITERIO_EDIT) {
            if (standardOld.equals("1") && criterioStandardNow.equals("0")) {
                getForm().getCriterioCustomMessageButtonList().setEditMode();
                Object[] sa = new Object[8];
                sa[0] = filtri;
                sa[1] = dateCreazioneValidate;
                sa[2] = nome;
                sa[3] = dataDa;
                sa[4] = dataA;
                sa[5] = new BigDecimal(status);
                sa[6] = "controllo2";
                sa[7] = criterioStandardNow;
                getSession().setAttribute("salvataggioAttributes", sa);
                getRequest().setAttribute("customBox2", true);
            } else {
                eseguiSalvataggioCriterioRaggruppamento(filtri, dateCreazioneValidate, nome, dataDa, dataA, status,
                        criterioStandardNow);
            }
        } // Altrimenti passo direttamente al salvataggio
        else {
            eseguiSalvataggioCriterioRaggruppamento(filtri, dateCreazioneValidate, nome, dataDa, dataA, status,
                    criterioStandardNow);
        }
    }

    public void eseguiSalvataggioCriterioRaggruppamento(CriteriRaggruppamentoForm.CreaCriterioRaggr filtri,
            Date[] dateCreazioneValidate, String nome, Timestamp dataDa, Timestamp dataA, int status,
            String criterioStandard) throws EMFError {
        try {
            // Salvataggio criterio
            long idCritRaggr;
            /*
             * Codice aggiuntivo per il logging...
             */
            LogParam param = SpagoliteLogUtil.getLogParam(
                    configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC),
                    getUser().getUsername(), SpagoliteLogUtil.getPageName(this));
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
            if (status == CRITERIO_EDIT) {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarUpdate());
            } else {
                param.setNomeAzione(SpagoliteLogUtil.getToolbarInsert());
            }

            if ((idCritRaggr = crHelper.saveCritRaggr(param, filtri, dateCreazioneValidate,
                    filtri.getId_strut().parse(), nome, criterioStandard)) > 0) {

                // Una volta salvato, reimposto i dati corretti nei filtri delle date
                SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_COMPACT_TYPE);
                if (dataDa != null) {
                    filtri.getDt_creazione_unita_doc_da().setValue(df.format(dataDa));
                }
                if (dataA != null) {
                    filtri.getDt_creazione_unita_doc_a().setValue(df.format(dataA));
                }

                // e del flag standard
                filtri.getFl_criterio_raggr_standard().setValue(criterioStandard);

                getMessageBox()
                        .addMessage(new Message(MessageLevel.INF, "Criterio di raggruppamento salvato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
                DecCriterioRaggrRowBean critRaggrTableBeanModificato = (DecCriterioRaggrRowBean) crHelper
                        .getDecCriterioRaggrById(new BigDecimal(idCritRaggr));
                if (status == CRITERIO_INSERT) {
                    // aggiungo il nuovo criterio alla lista
                    if (getForm().getCriterioRaggrList().getTable() == null) {
                        getForm().getCriterioRaggrList().setTable(new DecVRicCriterioRaggrTableBean());
                    }
                    getForm().getCriterioRaggrList().getTable().add().copyFromBaseRow(critRaggrTableBeanModificato);
                    getForm().getCriterioRaggrList().setStatus(Status.update);

                    // Devo popolare i campi ambiente/ente/struttura versione "casella di testo"...
                    getForm().getCreaCriterioRaggr().getNm_ambiente()
                            .setValue(getForm().getCreaCriterioRaggr().getId_ambiente().getDecodedValue());
                    getForm().getCreaCriterioRaggr().getNm_ente()
                            .setValue(getForm().getCreaCriterioRaggr().getId_ente().getDecodedValue());
                    getForm().getCreaCriterioRaggr().getNm_strut()
                            .setValue(getForm().getCreaCriterioRaggr().getId_strut().getDecodedValue());
                } else {
                    getForm().getCriterioRaggrList().setStatus(Status.view);
                    // Setto il nome nella lista in quanto potrebbe servirmi in fase di cancellazione
                    ((DecVRicCriterioRaggrTableBean) getForm().getCriterioRaggrList().getTable()).getCurrentRow()
                            .setNmCriterioRaggr(filtri.getNm_criterio_raggr().parse());
                }

                getForm().getCreaCriterioRaggr().getFl_criterio_raggr_fisc()
                        .setValue(critRaggrTableBeanModificato.getFlCriterioRaggrFisc());
                getForm().getCreaCriterioRaggr().getTi_valid_elenco()
                        .setValue(critRaggrTableBeanModificato.getTiValidElenco());
                getForm().getCreaCriterioRaggr().getTi_mod_valid_elenco()
                        .setValue(critRaggrTableBeanModificato.getTiModValidElenco());

                // Popolo i campi riferiti all'ambiente
                OrgAmbienteRowBean ambienteRowBean = ambienteEjb
                        .getOrgAmbienteRowBean(getForm().getCreaCriterioRaggr().getId_ambiente().parse());
                String tiGestElencoNoStd = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD, ambienteRowBean.getIdAmbiente(),
                        getForm().getCreaCriterioRaggr().getId_strut().parse());
                String tiGestElencoStdFisc = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC, ambienteRowBean.getIdAmbiente(),
                        getForm().getCreaCriterioRaggr().getId_strut().parse());
                String tiGestElencoStdNofisc = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC, ambienteRowBean.getIdAmbiente(),
                        getForm().getCreaCriterioRaggr().getId_strut().parse());
                getForm().getCreaCriterioRaggr().getTi_gest_elenco_nostd().setValue(tiGestElencoNoStd);
                getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_fisc().setValue(tiGestElencoStdFisc);
                getForm().getCreaCriterioRaggr().getTi_gest_elenco_std_nofisc().setValue(tiGestElencoStdNofisc);

                getForm().getCreaCriterioRaggr().setViewMode();
                getForm().getCreaCriterioRaggr().setStatus(Status.view);
                getForm().getCriterioRaggrList().setStatus(Status.view);

                getForm().getCreaCriterioRaggr().getDuplicaCritButton().setEditMode();
                getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(false);
                getForm().getCreaCriterioRaggr().getLogEventiCriteriRaggruppamento().setEditMode();
                // Nascondo i bottoni con javascript disattivato
                getForm().getCriterioCustomMessageButtonList().setViewMode();
            } else {
                // Errore
                getMessageBox().addMessage(
                        new Message(MessageLevel.ERR, "Errore nel salvataggio del criterio di raggruppamento"));
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } catch (ParerUserError e) {
            getMessageBox().addError(e.getDescription());
        }
    }

    /**
     * Metodo che inserisce i valori di default dei campi nella form di creazione criterio di raggruppamento
     *
     * @param type
     *            lista filtri di tipo {@link Field}
     *
     * @throws EMFError
     *             errore generico
     */
    private void populateDefaultValues(Fields<Field> filtri, BigDecimal idStrut) throws EMFError {
        OrgStrutRowBean struttura = null;
        if (idStrut != null) {
            struttura = struttureEjb.getOrgStrutRowBean(idStrut);
        }
        UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice filtriSemplice = null;
        UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata filtriAvanzata = null;
        if (filtri instanceof UnitaDocumentarieForm.FiltriUnitaDocumentarieSemplice) {
            filtriSemplice = (FiltriUnitaDocumentarieSemplice) filtri;
        } else if (filtri instanceof UnitaDocumentarieForm.FiltriUnitaDocumentarieAvanzata) {
            filtriAvanzata = (FiltriUnitaDocumentarieAvanzata) filtri;
        }
        if (struttura != null) {
            OrgEnteRowBean ente = struttureEjb.getOrgEnteRowBean(struttura.getIdEnte());
            String tiScadChiusVolume = configurationHelper.getValoreParamApplicByStrut(
                    CostantiDB.ParametroAppl.TI_SCAD_CHIUS_VOLUME, ente.getIdAmbiente(), idStrut);
            String niTempoScadChius = configurationHelper.getValoreParamApplicByStrut(
                    CostantiDB.ParametroAppl.NI_TEMPO_SCAD_CHIUS, ente.getIdAmbiente(), idStrut);
            String tiTempoScadChius = configurationHelper.getValoreParamApplicByStrut(
                    CostantiDB.ParametroAppl.TI_TEMPO_SCAD_CHIUS, ente.getIdAmbiente(), idStrut);

            if (tiScadChiusVolume != null) {
                getForm().getCreaCriterioRaggr().getTi_scad_chius_volume().setValue(tiScadChiusVolume);
            }
            if (niTempoScadChius != null) {
                getForm().getCreaCriterioRaggr().getNi_tempo_scad_chius().setValue(niTempoScadChius);
            }
            if (tiTempoScadChius != null) {
                getForm().getCreaCriterioRaggr().getTi_tempo_scad_chius().setValue(tiTempoScadChius);
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.set(2444, Calendar.DECEMBER, 31);
        SimpleDateFormat df = new SimpleDateFormat(WebConstants.DATE_FORMAT_DATE_TYPE);
        getForm().getCreaCriterioRaggr().getDt_istituz().setValue(df.format(Calendar.getInstance().getTime()));
        getForm().getCreaCriterioRaggr().getDt_soppres().setValue(df.format(cal.getTime()));

        String tmpField;
        if (filtriSemplice != null) {
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getNm_tipo_unita_doc().getValue())) {
                String[] valore = new String[1];
                valore[0] = tmpField;
                getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setValues(valore);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getCd_registro_key_unita_doc().getValue())) {
                String[] valore = new String[1];
                valore[0] = tmpField;
                getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setValues(valore);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getAa_key_unita_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getAa_key_unita_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getCd_key_unita_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getCd_key_unita_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getAa_key_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getAa_key_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getAa_key_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getAa_key_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getCd_key_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getCd_key_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getCd_key_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getCd_key_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getFl_unita_doc_firmato().getValue())) {
                getForm().getCreaCriterioRaggr().getFl_unita_doc_firmato().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getTi_esito_verif_firme().getValue())) {
                String[] valore = new String[1];
                valore[0] = tmpField;
                getForm().getCreaCriterioRaggr().getTi_esito_verif_firme().setValues(valore);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDt_acquisizione_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_creazione_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDt_acquisizione_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_creazione_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getFl_forza_accettazione().getValue())) {
                getForm().getCreaCriterioRaggr().getFl_forza_accettazione().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getFl_forza_conservazione().getValue())) {
                getForm().getCreaCriterioRaggr().getFl_forza_conservazione().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getNm_tipo_doc().getValue())) {
                String[] valore = new String[1];
                valore[0] = tmpField;
                getForm().getCreaCriterioRaggr().getNm_tipo_doc().setValues(valore);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getOre_dt_acquisizione_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getOre_dt_creazione_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getOre_dt_acquisizione_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getOre_dt_creazione_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getMinuti_dt_acquisizione_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getMinuti_dt_creazione_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getMinuti_dt_acquisizione_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getMinuti_dt_creazione_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDl_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getDl_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDl_oggetto_unita_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getDl_oggetto_unita_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDs_autore_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getDs_autore_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDt_reg_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_reg_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getDt_reg_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_reg_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriSemplice.getTi_conservazione().getValue())) {
                getForm().getCreaCriterioRaggr().getTi_conservazione().setValue(tmpField);
            }
        } else if (filtriAvanzata != null) {
            Set<String> tmpSet = new HashSet<>();
            if ((tmpSet = filtriAvanzata.getNm_tipo_unita_doc().getValues()) != null) {
                String[] set = new String[tmpSet.size()];
                getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setValues(tmpSet.toArray(set));
            }
            if ((tmpSet = filtriAvanzata.getCd_registro_key_unita_doc().getValues()) != null) {
                String[] set = new String[tmpSet.size()];
                getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setValues(tmpSet.toArray(set));
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getAa_key_unita_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getAa_key_unita_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getCd_key_unita_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getCd_key_unita_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getAa_key_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getAa_key_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getAa_key_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getAa_key_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getCd_key_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getCd_key_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getCd_key_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getCd_key_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getFl_unita_doc_firmato().getValue())) {
                getForm().getCreaCriterioRaggr().getFl_unita_doc_firmato().setValue(tmpField);
            }
            if ((tmpSet = filtriAvanzata.getTi_esito_verif_firme().getValues()) != null) {
                String[] set = new String[tmpSet.size()];
                getForm().getCreaCriterioRaggr().getTi_esito_verif_firme().setValues(tmpSet.toArray(set));
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDt_acquisizione_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_creazione_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDt_acquisizione_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_creazione_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getFl_forza_accettazione().getValue())) {
                getForm().getCreaCriterioRaggr().getFl_forza_accettazione().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getFl_forza_conservazione().getValue())) {
                getForm().getCreaCriterioRaggr().getFl_forza_conservazione().setValue(tmpField);
            }
            if ((tmpSet = filtriAvanzata.getNm_tipo_doc().getValues()) != null) {
                String[] set = new String[tmpSet.size()];
                getForm().getCreaCriterioRaggr().getNm_tipo_doc().setValues(tmpSet.toArray(set));
            }
            if ((tmpSet = filtriAvanzata.getNm_sistema_migraz().getValues()) != null) {
                String[] set = new String[tmpSet.size()];
                getForm().getCreaCriterioRaggr().getNm_sistema_migraz().setValues(tmpSet.toArray(set));
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getOre_dt_acquisizione_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getOre_dt_creazione_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getOre_dt_acquisizione_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getOre_dt_creazione_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getMinuti_dt_acquisizione_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getMinuti_dt_creazione_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getMinuti_dt_acquisizione_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getMinuti_dt_creazione_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDl_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getDl_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDl_oggetto_unita_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getDl_oggetto_unita_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDs_autore_doc().getValue())) {
                getForm().getCreaCriterioRaggr().getDs_autore_doc().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDt_reg_unita_doc_da().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_reg_unita_doc_da().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getDt_reg_unita_doc_a().getValue())) {
                getForm().getCreaCriterioRaggr().getDt_reg_unita_doc_a().setValue(tmpField);
            }
            if (StringUtils.isNotBlank(tmpField = filtriAvanzata.getTi_conservazione().getValue())) {
                getForm().getCreaCriterioRaggr().getTi_conservazione().setValue(tmpField);
            }
        }
    }

    /**
     * Metodo richiamato alla pressione del stato "SI", sia per conferma su controllo numero massimo componenti
     * ("controllo1") sia per conferma su controllo criterio standard ("controllo2"). Con JavaScript attivato il tasto è
     * quello presente nella finestra popup, con JavaScript disattivato invece il tasto (preventivamente allineato tra i
     * componenti) è che compare in alto nella pagina
     *
     * @throws Throwable
     *             errore generico
     */
    @Override
    public void confermaSalvataggioCriterio() throws Throwable {
        if (getSession().getAttribute("salvataggioAttributes") != null) {
            Object[] sa = (Object[]) getSession().getAttribute("salvataggioAttributes");
            String tipoControllo = (String) sa[6];
            if (tipoControllo != null && tipoControllo.equals("controllo1")) {
                eseguiPrimoStepSalvataggioCriterioRaggruppamento((CreaCriterioRaggr) sa[0], (Date[]) sa[1],
                        (String) sa[2], (Timestamp) sa[3], (Timestamp) sa[4], ((BigDecimal) sa[5]).intValue(),
                        (String) sa[7]);
            }
            if (tipoControllo != null && tipoControllo.equals("controllo2")) {
                eseguiSalvataggioCriterioRaggruppamento((CreaCriterioRaggr) sa[0], (Date[]) sa[1], (String) sa[2],
                        (Timestamp) sa[3], (Timestamp) sa[4], ((BigDecimal) sa[5]).intValue(), (String) sa[7]);
            }
        }
        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
    }

    @Override
    public void annullaSalvataggioCriterio() throws Throwable {
        // Nascondo i bottoni con javascript disattivato
        getForm().getCriterioCustomMessageButtonList().setViewMode();
        getSession().removeAttribute("salvataggioAttributes");
        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
    }

    @Override
    public void duplicaCritButton() throws EMFError {
        loadDuplicaCrit();
    }

    public void duplicaCrit() throws EMFError {
        setTableName(getForm().getCriterioRaggrList().getName());
        setRiga(getRequest().getParameter("riga"));
        getForm().getCriterioRaggrList().getTable().setCurrentRowIndex(Integer.parseInt(getRiga()));

        loadDuplicaCrit(((DecVRicCriterioRaggrTableBean) getForm().getCriterioRaggrList().getTable()).getCurrentRow()
                .getNmCriterioRaggr());
    }

    private void loadDuplicaCrit() throws EMFError {
        loadDuplicaCrit(null);
    }

    private void loadDuplicaCrit(String nome) throws EMFError {
        getForm().getCreaCriterioRaggr().setEditMode();
        getForm().getCreaCriterioRaggr().getDuplicaCritButton().setHidden(true);
        getForm().getCreaCriterioRaggr().setStatus(Status.insert);
        getForm().getCriterioRaggrList().setStatus(Status.insert);
        getSession().setAttribute("insertActive", true);

        // Nel caso il tab corrente sia quello dei dati specifici, nasconde i bottoni della barra
        getForm().getCriterioRaggrList().setHideUpdateButton(false);

        // I campi riferiti ad ambiente/ente/struttura rimangono in view mode
        getForm().getCreaCriterioRaggr().getId_ambiente().setViewMode();
        getForm().getCreaCriterioRaggr().getId_ente().setViewMode();
        getForm().getCreaCriterioRaggr().getId_strut().setViewMode();

        // Carica il rowBean del criterio
        if (nome == null) {
            nome = ((DecVRicCriterioRaggrRowBean) getForm().getCriterioRaggrList().getTable().getCurrentRow())
                    .getNmCriterioRaggr();
        }

        BigDecimal idStrut = getForm().getCreaCriterioRaggr().getId_strut().parse();
        if (idStrut == null) {
            idStrut = ((DecVRicCriterioRaggrRowBean) getForm().getCriterioRaggrList().getTable().getCurrentRow())
                    .getIdStrut();
        }

        DecCriterioRaggrRowBean critRB = criteriRaggruppamentoEjb.getDecCriterioRaggrRowBean(idStrut, nome);

        // Inizializza le combo della form
        initCriterioRaggrCombo(critRB.getIdStrut());

        // Popola i valori delle combo in base ai dati
        populateComboFields(critRB);

        critRB.setNmCriterioRaggr(null);
        critRB.setDsCriterioRaggr(null);
        critRB.setIdCriterioRaggr(null);
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
        critRB.setDtIstituz(new Timestamp(now.getTimeInMillis()));
        critRB.setDtSoppres(new Timestamp(cal.getTimeInMillis()));

        getForm().getCreaCriterioRaggr().copyFromBean(critRB);

        // Popola i valori delle combo ambiente/ente/struttura partendo dal valore preso da DB del criterio
        initComboAmbienteEnteStrutCreaCriteriRaggr(critRB.getIdStrut());

        forwardToPublisher(Application.Publisher.CRITERIO_RAGGR_DETAIL);
    }

    @Override
    public JSONObject triggerFiltriCriteriRaggrId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriCriteriRaggr(),
                ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR);
        return getForm().getFiltriCriteriRaggr().asJSON();
    }

    @Override
    public JSONObject triggerFiltriCriteriRaggrId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriCriteriRaggr(), ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR);
        return getForm().getFiltriCriteriRaggr().asJSON();
    }

    @Override
    public void ricercaCriteriRaggrButton() throws EMFError {
        FiltriCriteriRaggr filtriCriteri = getForm().getFiltriCriteriRaggr();
        filtriCriteri.post(getRequest());
        ricercaCriteri(filtriCriteri);
    }

    @Override
    public JSONObject triggerCreaCriterioRaggrId_ambienteOnTrigger() throws EMFError {
        getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
        getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setDecodeMap(new DecodeMap());
        getForm().getCreaCriterioRaggr().getNm_tipo_doc().setDecodeMap(new DecodeMap());
        triggerAmbienteGenerico(getForm().getCreaCriterioRaggr(),
                ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL);
        return getForm().getCreaCriterioRaggr().asJSON();
    }

    @Override
    public JSONObject triggerCreaCriterioRaggrId_enteOnTrigger() throws EMFError {
        getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
        getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setDecodeMap(new DecodeMap());
        getForm().getCreaCriterioRaggr().getNm_tipo_doc().setDecodeMap(new DecodeMap());
        getForm().getCreaCriterioRaggr().getNi_max_comp().setValue("");
        getForm().getCreaCriterioRaggr().getNi_max_elenchi_by_gg().setValue("");
        triggerEnteGenerico(getForm().getCreaCriterioRaggr(), ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL);
        return getForm().getCreaCriterioRaggr().asJSON();
    }

    @Override
    public JSONObject triggerCreaCriterioRaggrId_strutOnTrigger() throws EMFError {
        getForm().getCreaCriterioRaggr().post(getRequest());
        if (getForm().getCreaCriterioRaggr().getId_strut().parse() != null) {
            checkUniqueStrutInCombo(getForm().getCreaCriterioRaggr().getId_strut().parse(),
                    ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL);
            settaNumeroMassimoComponenti(getForm().getCreaCriterioRaggr().getId_strut().parse());
        } else {
            getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getCreaCriterioRaggr().getNm_tipo_doc().setDecodeMap(new DecodeMap());
            getForm().getCreaCriterioRaggr().getNi_max_comp().setValue("");
            getForm().getCreaCriterioRaggr().getNi_max_elenchi_by_gg().setValue("");
        }
        return getForm().getCreaCriterioRaggr().asJSON();

    }

    public Fields triggerAmbienteGenerico(Fields campi, ActionEnums.SezioneCriteriRaggr sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox ambienteCombo = (ComboBox) campi.getComponent("id_ambiente");
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");
        ComboBox registroCombo = null;
        ComboBox tipoUdCombo = null;
        ComboBox tipoDocCombo = null;
        Input niMaxComp = (Input) campi.getComponent("ni_max_comp");
        Input niMaxElenchiByGg = (Input) campi.getComponent("ni_max_elenchi_by_gg");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
            registroCombo = (ComboBox) campi.getComponent("id_registro_unita_doc");
            tipoUdCombo = (ComboBox) campi.getComponent("id_tipo_unita_doc");
            tipoDocCombo = (ComboBox) campi.getComponent("id_tipo_doc");
        }

        // Azzero i valori preimpostati delle varie combo
        enteCombo.setValue("");
        strutCombo.setValue("");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
            registroCombo.setValue("");
            tipoUdCombo.setValue("");
            tipoDocCombo.setValue("");
        } else if (sezione.equals(ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL)) {
            niMaxComp.setValue("");
            niMaxElenchiByGg.setValue("");
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
                if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                    registroCombo.setDecodeMap(new DecodeMap());
                    tipoUdCombo.setDecodeMap(new DecodeMap());
                    tipoDocCombo.setDecodeMap(new DecodeMap());
                }
            }
            // Ricarica il combo i base all'ambiente selezionato.
            caricaGestioneElencoCriterioByAmbiente(idAmbiente);
        } else {
            enteCombo.setDecodeMap(new DecodeMap());
            strutCombo.setDecodeMap(new DecodeMap());
            if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                registroCombo.setDecodeMap(new DecodeMap());
                tipoUdCombo.setDecodeMap(new DecodeMap());
                tipoDocCombo.setDecodeMap(new DecodeMap());
            }
        }
        return campi;
    }

    public Fields triggerEnteGenerico(Fields campi, ActionEnums.SezioneCriteriRaggr sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");
        ComboBox registroCombo = null;
        ComboBox tipoUdCombo = null;
        ComboBox tipoDocCombo = null;
        Input niMaxComp = (Input) campi.getComponent("ni_max_comp");
        Input niMaxElenchiByGg = (Input) campi.getComponent("ni_max_elenchi_by_gg");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
            registroCombo = (ComboBox) campi.getComponent("id_registro_unita_doc");
            tipoUdCombo = (ComboBox) campi.getComponent("id_tipo_unita_doc");
            tipoDocCombo = (ComboBox) campi.getComponent("id_tipo_doc");
        }

        // Azzero i valori preimpostati delle varie combo
        strutCombo.setValue("");
        if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
            registroCombo.setValue("");
            tipoUdCombo.setValue("");
            tipoDocCombo.setValue("");
        } else if (sezione.equals(ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL)) {
            niMaxComp.setValue("");
            niMaxElenchiByGg.setValue("");
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
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                registroCombo.setDecodeMap(new DecodeMap());
                tipoUdCombo.setDecodeMap(new DecodeMap());
                tipoDocCombo.setDecodeMap(new DecodeMap());
            }
        } else {
            strutCombo.setDecodeMap(new DecodeMap());
            if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                registroCombo.setDecodeMap(new DecodeMap());
                tipoUdCombo.setDecodeMap(new DecodeMap());
                tipoDocCombo.setDecodeMap(new DecodeMap());
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

            if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                getForm().getFiltriCriteriRaggr().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL)) {
                getForm().getCreaCriterioRaggr().getId_strut().setDecodeMap(mappaStrut);
            }

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di essa
            if (tmpTableBeanStrut.size() == 1) {
                if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                    getForm().getFiltriCriteriRaggr().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL)) {
                    getForm().getCreaCriterioRaggr().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                }
            }
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo struttura quando questo è l'unico presente e settare di
     * conseguenza le combo Tipo Registro, Tipo Unità Documentaria, Tipo Documento e numero massimo componenti
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
            // Ricavo tutti i Tipi Registro per la struttura passata in input
            DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getDecRegistroUnitaDocTableBean(idStrut);
            DecodeMap mappaReg = new DecodeMap();
            mappaReg.populatedMap(tmpTableBeanReg, "id_registro_unita_doc", "cd_registro_unita_doc");

            // Ricavo tutti i Tipi Unità Documentaria per la struttura passata in input
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb.getDecTipoUnitaDocTableBean(idStrut);
            DecodeMap mappaUD = new DecodeMap();
            mappaUD.populatedMap(tmpTableBeanTUD, "id_tipo_unita_doc", "nm_tipo_unita_doc");

            // Ricavo tutti i Tipi Documento per la struttura passata in input
            DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(idStrut, null);
            DecodeMap mappaTipoDocumento = DecodeMap.Factory.newInstance(tmpTableBeanTipoDoc, "id_tipo_doc",
                    "nm_tipo_doc");

            if (sezione.equals(ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR)) {
                getForm().getFiltriCriteriRaggr().getId_registro_unita_doc().setDecodeMap(mappaReg);
                getForm().getFiltriCriteriRaggr().getId_tipo_unita_doc().setDecodeMap(mappaUD);
                getForm().getFiltriCriteriRaggr().getId_tipo_doc().setDecodeMap(mappaTipoDocumento);
            } else if (sezione.equals(ActionEnums.SezioneCriteriRaggr.CRITERIO_RAGGR_DETAIL)) {
                getForm().getCreaCriterioRaggr().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
                getForm().getCreaCriterioRaggr().getNm_tipo_unita_doc().setDecodeMap(mappaUD);
                getForm().getCreaCriterioRaggr().getNm_tipo_doc().setDecodeMap(mappaTipoDocumento);
                settaNumeroMassimoComponenti(idStrut);
            }
        }
    }

    @Override
    public JSONObject triggerFiltriCriteriRaggrId_strutOnTrigger() throws EMFError {
        getForm().getFiltriCriteriRaggr().post(getRequest());
        if (getForm().getFiltriCriteriRaggr().getId_strut().parse() != null) {
            checkUniqueStrutInCombo(getForm().getFiltriCriteriRaggr().getId_strut().parse(),
                    ActionEnums.SezioneCriteriRaggr.FILTRI_CRITERI_RAGGR);
        } else {
            getForm().getFiltriCriteriRaggr().getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriCriteriRaggr().getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            getForm().getFiltriCriteriRaggr().getId_tipo_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriCriteriRaggr().asJSON();
    }

    private void setAmbienteEnteStrutturaDesc(BigDecimal idStrut) throws EMFError {
        String[] aes = struttureEjb.getAmbienteEnteStrutturaDesc(idStrut);
        getForm().getCreaCriterioRaggr().getNm_ambiente().setValue(aes[0]);
        getForm().getCreaCriterioRaggr().getNm_ente().setValue(aes[1]);
        getForm().getCreaCriterioRaggr().getNm_strut().setValue(aes[2]);
    }

    private boolean isCriterioRaggrStandard(CriteriRaggruppamentoForm.CreaCriterioRaggr filtri,
            Date[] dateCreazioneValidate) throws EMFError {
        String nmCriterioRaggr = filtri.getNm_criterio_raggr().parse();
        BigDecimal aaKeyUnitaDoc = filtri.getAa_key_unita_doc().parse();
        BigDecimal aaKeyUnitaDocDa = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal aaKeyUnitaDocA = filtri.getAa_key_unita_doc_a().parse();
        BigDecimal niTempoScadChius = filtri.getNi_tempo_scad_chius().parse();
        String tiTempoScadChius = filtri.getTi_tempo_scad_chius().parse();
        Set<String> reg = filtri.getCd_registro_key_unita_doc().getDecodedValues();
        Set<String> tipiUd = filtri.getNm_tipo_unita_doc().getDecodedValues();
        Set<String> tipiDoc = filtri.getNm_tipo_doc().getDecodedValues();
        BigDecimal niMaxComp = filtri.getNi_max_comp().parse();

        String dlOggettoUnitaDoc = filtri.getDl_oggetto_unita_doc().parse();
        Timestamp dtRegUnitaDocDa = filtri.getDt_reg_unita_doc_da().parse();
        Timestamp dtRegUnitaDocA = filtri.getDt_reg_unita_doc_a().parse();
        String dlDoc = filtri.getDl_doc().parse();
        String dsAutoreDoc = filtri.getDs_autore_doc().parse();
        Date dtCreazioneDa = dateCreazioneValidate != null ? dateCreazioneValidate[0] : null;
        Date dtCreazioneA = dateCreazioneValidate != null ? dateCreazioneValidate[1] : null;
        String tiConservazione = filtri.getTi_conservazione().parse();
        String flUnitaDocFirmato = filtri.getFl_unita_doc_firmato().parse();
        String flForzaAccettazione = filtri.getFl_forza_accettazione().parse();
        String flForzaConservazione = filtri.getFl_forza_conservazione().parse();
        List<String> tiEsitoVerifFirme = filtri.getTi_esito_verif_firme().parse();
        List<String> nmSistemaMigraz = filtri.getNm_sistema_migraz().parse();

        CriterioRaggrStandardBean bean = new CriterioRaggrStandardBean(nmCriterioRaggr, aaKeyUnitaDoc, aaKeyUnitaDocDa,
                aaKeyUnitaDocA, niTempoScadChius, tiTempoScadChius, reg, tipiUd, tipiDoc, niMaxComp, dlOggettoUnitaDoc,
                dtRegUnitaDocDa, dtRegUnitaDocA, dlDoc, dsAutoreDoc, dtCreazioneDa, dtCreazioneA, tiConservazione,
                flUnitaDocFirmato, flForzaAccettazione, flForzaConservazione, tiEsitoVerifFirme, nmSistemaMigraz);

        return criteriRaggruppamentoEjb.isCriterioRaggrStandard(bean);
    }

    private void settaNumeroMassimoComponenti(BigDecimal idStrut) {
        long numMaxComp = criteriRaggruppamentoEjb.getNumMaxCompDaStruttura(idStrut);
        getForm().getCreaCriterioRaggr().getNi_max_comp().setValue("" + numMaxComp);
    }

    @Override
    public void logEventiCriteriRaggruppamento() throws EMFError {
        GestioneLogEventiForm form = new GestioneLogEventiForm();
        form.getOggettoDetail().getNmApp()
                .setValue(configurationHelper.getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_APPLIC));
        form.getOggettoDetail().getNm_tipo_oggetto().setValue(SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO);
        DecVRicCriterioRaggrRowBean riga = (DecVRicCriterioRaggrRowBean) getForm().getCriterioRaggrList().getTable()
                .getCurrentRow();
        form.getOggettoDetail().getIdOggetto().setValue(riga.getIdCriterioRaggr().toString());
        redirectToAction(it.eng.parer.sacerlog.slite.gen.Application.Actions.GESTIONE_LOG_EVENTI,
                "?operation=inizializzaLogEventi", form);
    }
}
