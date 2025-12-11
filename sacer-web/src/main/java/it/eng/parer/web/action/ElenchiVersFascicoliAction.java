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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.hsm.beans.HSMUser;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb.TipoFascicoloEjb;
import it.eng.parer.common.signature.Signature;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.FileTypeEnum;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;
import it.eng.parer.fascicoli.dto.RicercaFascicoliBean;
import it.eng.parer.fascicoli.ejb.CriteriRaggrFascicoliEjb;
import it.eng.parer.fascicoli.ejb.ElenchiVersFascicoliEjb;
import it.eng.parer.fascicoli.ejb.FascicoliEjb;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.firma.crypto.ejb.ElencoFascSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.ElencoIndiciAipFascSignatureSessionEjb;
import it.eng.parer.firma.crypto.sign.SignerHsmEjb;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.firma.crypto.sign.SigningResponse;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.ElenchiVersFascicoliAbstractAction;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm;
import it.eng.parer.slite.gen.form.FascicoliForm;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.ElvElencoVersFascRowBean;
import it.eng.parer.slite.gen.tablebean.ElvElencoVersFascTableBean;
import it.eng.parer.slite.gen.tablebean.ElvStatoElencoVersFascRowBean;
import it.eng.parer.slite.gen.tablebean.ElvStatoElencoVersFascTableBean;
import it.eng.parer.slite.gen.tablebean.FasFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.FasFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteRowBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByStatoRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByStatoTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByStatoTableDescriptor;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascTableDescriptor;
import it.eng.parer.slite.gen.viewbean.FasVRicFascicoliRowBean;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.ActionEnums;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.parer.web.validator.FascicoliValidator;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.SuppressLogging;

/**
 *
 * @author DiLorenzo_F
 */
public class ElenchiVersFascicoliAction extends ElenchiVersFascicoliAbstractAction {

    private static final String ERRORE_RICERCA_AMBIENTE = "Errore in ricerca ambiente";
    private static final String ECCEZIONE_GENERICA = "Eccezione";
    private static Logger log = LoggerFactory.getLogger(ElenchiVersFascicoliAction.class.getName());
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SignerHsmEjb")
    private SignerHsmEjb firmaHsmEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ElencoFascSignatureSessionEjb")
    private ElencoFascSignatureSessionEjb elencoFascSignSessionEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ElencoIndiciAipFascSignatureSessionEjb")
    private ElencoIndiciAipFascSignatureSessionEjb elencoIndiciAipFascSignSessionEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CriteriRaggrFascicoliEjb")
    private CriteriRaggrFascicoliEjb criteriRaggrFascicoliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FascicoliEjb")
    private FascicoliEjb fascicoliEjb;
    @EJB(mappedName = "java:app/Parer-ejb/FascicoliHelper")
    private FascicoliHelper fascicoliHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersFascicoliEjb")
    private ElenchiVersFascicoliEjb evfEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoFascicoloEjb")
    private TipoFascicoloEjb tipoFascicoloEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmministrazioneEjb")
    private AmministrazioneEjb amministrazioneEjb;

    /* Getter di valori utilizzati all'interno della action */
    private BigDecimal getIdStrutCorrente() {
        return getUser().getIdOrganizzazioneFoglia();
    }

    private long getIdUtenteCorrente() {
        return getUser().getIdUtente();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.ELENCHI_VERS_FASCICOLI_RICERCA;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.ELENCHI_VERS_FASCICOLI;
    }

    /* METODI DI INIZIALIZZAZIONE */
    @Override
    public void initOnClick() throws EMFError {
    }

    private void initComboFiltriFascicoli(BigDecimal idStrut) {
        if (idStrut == null) {
            idStrut = getIdStrutCorrente();
        }
        getForm().getFascicoliFiltri().reset();
        // Imposto i valori della combo TIPO FASCICOLO ricavati dalla tabella DEC_TIPO_FASCICOLO
        DecTipoFascicoloTableBean tmpTableBeanTipoFasc = fascicoliEjb
                .getTipiFascicoloAbilitati(getUser().getIdUtente(), idStrut);
        DecodeMap mappaTipoFascicolo = DecodeMap.Factory.newInstance(tmpTableBeanTipoFasc,
                "id_tipo_fascicolo", "nm_tipo_fascicolo");

        // TODO: verificare, Imposto le varie combo dei FILTRI di ricerca Fascicoli
        getForm().getFascicoliFiltri().getId_tipo_fascicolo().setDecodeMap(mappaTipoFascicolo);
    }

    private void initBottoniPaginaDettaglioElencoVersFascicoli(BigDecimal idElencoVersFasc,
            String tiStato) {
        getForm().getDettaglioElenchiVersFascicoliButtonList().setViewMode();
        getForm().getElenchiVersFascicoliList().setUserOperations(true, true, false, true);
        getForm().getListaFascicoliButtonList().setViewMode();
        /* Se i fascicoli sono eliminabili dall'elenco di versamento, mostro il bottone */
        if (evfEjb.areFascDeletables(idElencoVersFasc)) {
            getForm().getListaFascicoliButtonList().getEliminaAppartenenzaFascDaElenco()
                    .setHidden(false);
            getForm().getListaFascicoliButtonList().getEliminaAppartenenzaFascDaElenco()
                    .setEditMode();
        }
        /* Se l'elenco è chiudibile, mostro il bottone */
        // TIP: fdilorenzo, test criteria api con metamodels
        if (evfEjb.isElencoClosable2(idElencoVersFasc)) {
            getForm().getDettaglioElenchiVersFascicoliButtonList().getChiudiElencoButton()
                    .setHidden(false);
            getForm().getDettaglioElenchiVersFascicoliButtonList().getChiudiElencoButton()
                    .setEditMode();
        }

        if (tiStato.equals(ElencoEnums.ElencoStatusEnum.CHIUSO.name())
                || tiStato.equals(ElencoEnums.ElencoStatusEnum.FIRMA_IN_CORSO.name())
                || tiStato.equals(ElencoEnums.ElencoStatusEnum.FIRMATO.name())
                || tiStato.equals(ElencoEnums.ElencoStatusEnum.IN_CODA_CREAZIONE_AIP.name())
                || tiStato.equals(ElencoEnums.ElencoStatusEnum.AIP_CREATI.name())
                || tiStato.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name())
                || tiStato.equals(
                        ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name())
                || tiStato.equals(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())) {
            getForm().getDettaglioElenchiVersFascicoliButtonList().getScaricaIndiceElencoButton()
                    .setEditMode();
            getForm().getDettaglioElenchiVersFascicoliButtonList().getScaricaIndiceElencoButton()
                    .setDisableHourGlass(true);
        }
        if (tiStato.equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name())
                || tiStato.equals(
                        ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name())
                || (tiStato.equals(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())
                        && evfEjb.retrieveFileIndiceElenco(idElencoVersFasc.longValue(),
                                ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name()) != null)) {
            getForm().getDettaglioElenchiVersFascicoliButtonList().getScaricaElencoIdxAipFascBtn()
                    .setEditMode();
            getForm().getDettaglioElenchiVersFascicoliButtonList().getScaricaElencoIdxAipFascBtn()
                    .setDisableHourGlass(true);
        }

    }

    ////////////
    /* PAGINE */
    ////////////
    @Secure(action = "Menu.Fascicoli.ListaElenchiVersFascicoliDaValidare")
    public void loadListaElenchiVersFascicoliDaValidare() throws EMFError {
        /*
         * Controllo lo stato della history di navigazione se non ci sono pagine precedenti, vuol
         * dire che arrivo qui da un link del menu, se ci sono pagine allora devo passare alla jsp
         * l'id della struttura
         */
        boolean cleanList = false;
        if (getRequest().getParameter("cleanhistory") != null) {
            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.Fascicoli.ListaElenchiVersFascicoliDaFirmare");
            // Rimuovo l'attributo perchè arrivo da un link del menu e non da una lista
            getSession().removeAttribute("idStrutRif");
            getSession().removeAttribute("isStrutNull");
            cleanList = true;
        }

        /* Ricavo Ambiente, Ente e Struttura da visualizzare */
        BigDecimal idStrut;
        if (getRequest().getParameter("idStrut") != null) {
            idStrut = new BigDecimal(getRequest().getParameter("idStrut"));
            cleanList = true;
        } else if (getSession().getAttribute("idStrutRif") != null) {
            idStrut = (BigDecimal) getSession().getAttribute("idStrutRif");
        } else if (getSession().getAttribute("isStrutNull") != null) {
            idStrut = null;
        } else {
            idStrut = getIdStrutCorrente();
            cleanList = true;
        }

        boolean cleanFilter = true;
        if (getRequest().getParameter("cleanFilter") != null) {
            cleanFilter = false;
        }

        if (idStrut != null && cleanFilter) {
            OrgStrutRowBean strut = evfEjb.getOrgStrutRowBeanWithAmbienteEnte(idStrut);
            /* Inizializza le combo dei filtri ambiente/ente/struttura */
            initFiltriElenchiVersFascicoliDaFirmare(strut.getIdStrut());

            if (cleanList) {
                FascicoliValidator fascicoliValidator = new FascicoliValidator(getMessageBox());
                // Valido i filtri data creazione elenco fascicoli da - a restituendo le date
                // comprensive di orario
                Date[] dateCreazioneElencoFascValidate = fascicoliValidator.validaDate(
                        getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_da()
                                .parse(),
                        getForm().getFiltriElenchiVersFascDaFirmare()
                                .getOre_ts_creazione_elenco_da().parse(),
                        getForm().getFiltriElenchiVersFascDaFirmare()
                                .getMinuti_ts_creazione_elenco_da().parse(),
                        getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_a()
                                .parse(),
                        getForm().getFiltriElenchiVersFascDaFirmare().getOre_ts_creazione_elenco_a()
                                .parse(),
                        getForm().getFiltriElenchiVersFascDaFirmare()
                                .getMinuti_ts_creazione_elenco_a().parse(),
                        getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_da()
                                .getHtmlDescription(),
                        getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_a()
                                .getHtmlDescription());
                if (!getMessageBox().hasError()) {
                    /*
                     * Carico la lista degli elenchi di versamento fascicoli da firmare: quelli
                     * della struttura dell'utente e con stato CHIUSO
                     */
                    ElvVRicElencoFascByStatoTableBean elenchiVersFascicoliTableBean = evfEjb
                            .getElenchiVersFascicoliDaFirmareTableBean(
                                    strut.getBigDecimal("id_ambiente"), strut.getIdEnte(),
                                    strut.getIdStrut(),
                                    getForm().getFiltriElenchiVersFascDaFirmare()
                                            .getId_elenco_vers_fasc().parse(),
                                    getForm().getFiltriElenchiVersFascDaFirmare()
                                            .getElenchi_con_note().parse(),
                                    ElencoEnums.ElencoStatusEnum.CHIUSO,
                                    dateCreazioneElencoFascValidate, getUser().getIdUtente());
                    getForm().getElenchiVersFascicoliDaFirmareList()
                            .setTable(elenchiVersFascicoliTableBean);
                    getForm().getElenchiVersFascicoliDaFirmareList().getTable().setPageSize(10);
                    getForm().getElenchiVersFascicoliDaFirmareList().getTable().first();
                    getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                            .addSortingRule(getForm().getElenchiVersFascicoliDaFirmareList()
                                    .getTs_creazione_elenco().getName(), SortingRule.ASC);

                    /* Inizializzo la lista degli elenchi di versamento fascicoli selezionati */
                    getForm().getElenchiVersFascicoliSelezionatiList()
                            .setTable(new ElvVRicElencoFascByStatoTableBean());
                    getForm().getElenchiVersFascicoliSelezionatiList().getTable().setPageSize(10);
                    getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                            .addSortingRule(getForm().getElenchiVersFascicoliSelezionatiList()
                                    .getTs_creazione_elenco().getName(), SortingRule.ASC);
                }
            }
        }

        // Check if some signature session is active
        Future<Boolean> futureFirma = (Future<Boolean>) getSession()
                .getAttribute(Signature.FUTURE_ATTR_ELENCHI_FASC);
        /* Rendo visibili i bottoni delle operazioni sulla lista che mi interessano */
        getForm().getListaElenchiVersFascDaFirmareButtonList().setEditMode();
        // Verifico su db la presenza della sessione di firma o di un oggetto future (di una
        // possibile sessione di firma
        // preesistente) in sessione
        if (elencoFascSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())
                || futureFirma != null) {
            // Se esistono delle sessioni bloccate per quell'utente le sblocco
            if (elencoFascSignSessionEjb.hasUserBlockedSessions(getUser().getIdUtente())) {
                // Sessione di firma bloccata
                elencoFascSignSessionEjb.unlockBlockedSessions(getUser().getIdUtente());

                getForm().getListaElenchiVersFascDaFirmareButtonList().getValidaElenchiButton()
                        .setReadonly(false);
                getMessageBox().addInfo("\u00C8 stata sbloccata una sessione di firma bloccata");
                getMessageBox().setViewMode(ViewMode.plain);
            } else {
                getForm().getListaElenchiVersFascDaFirmareButtonList().getValidaElenchiButton()
                        .setReadonly(true);
                // Sessione di firma attiva
                getMessageBox().addInfo("Sessione di firma attiva");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getForm().getListaElenchiVersFascDaFirmareButtonList().getValidaElenchiButton()
                    .setReadonly(false);
        }
        getForm().getListaElenchiVersFascDaFirmareButtonList().getValidaElenchiButton()
                .setHidden(false);
        getForm().getElenchiVersFascicoliList().setUserOperations(true, false, false, false);

        getSession().setAttribute("idStrutRif", idStrut);
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    /**
     * Carica la pagina di "Ricerca elenchi di versamento fascicoli"
     *
     * @throws EMFError errore generico
     */
    @Secure(action = "Menu.Fascicoli.RicercaElenchiVersFascicoli")
    public void ricercaElenchiVersFascicoli() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Fascicoli.RicercaElenchiVersFascicoli");
        /* Azzero i filtri e la lista risultato della form di ricerca */
        getForm().getFiltriElenchiVersFascicoli().reset();
        getForm().getElenchiVersFascicoliList().clear();
        getForm().getElenchiVersFascicoliList().setUserOperations(true, true, false, true);
        /* Inizializzo le combo di ricerca */
        initComboRicercaElenchi();
        /* Imposto tutti i filtri in edit mode */
        getForm().getFiltriElenchiVersFascicoli().setEditMode();
        getForm().getFascicoliFiltri().setEditMode();
        /* Carico la pagina di ricerca */
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_RICERCA);
    }

    /**
     * Creo le mappe coi valori e setto le combo presenti nella pagina di ricerca elenchi
     *
     * @throws EMFError errore generico
     */
    private void initComboRicercaElenchi() throws EMFError {
        // Ricavo id struttura, ente ed ambiente attuali
        BigDecimal idStrut = getUser().getIdOrganizzazioneFoglia();
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
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            log.error(ERRORE_RICERCA_AMBIENTE, ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriElenchiVersFascicoli().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriElenchiVersFascicoli().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriElenchiVersFascicoli().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriElenchiVersFascicoli().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriElenchiVersFascicoli().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriElenchiVersFascicoli().getId_strut().setValue(idStrut.toString());

        // Imposto le combo "Standard" e "Stato"
        getForm().getFiltriElenchiVersFascicoli().getTi_stato()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato",
                        ElencoEnums.ElencoStatusEnum.getComboMappaStatoElencoRicerca()));
        getForm().getFiltriElenchiVersFascicoli().getFl_elenco_standard()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // Imposto la combo Criteri Raggruppamento Fascicoli e Tipo Fascicolo
        checkUniqueStrutInCombo(idStrut,
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC);
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo struttura quando questo è l'unico
     * presente e settare di conseguenza le combo Tipo Fascicolo e Criterio di Raggruppamento
     * Fascicoli
     *
     * @param idStrut id struttura
     * @param sezione enumerativo
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueStrutInCombo(BigDecimal idStrut,
            Enum<ActionEnums.SezioneElenchiVersFascicoli> sezione) throws EMFError {
        if (idStrut != null) {
            // Ricavo tutti i Criteri di Raggruppamento Fascicoli per la struttura passata in input
            DecCriterioRaggrFascTableBean tmpTableBeanCriteri = criteriRaggrFascicoliEjb
                    .getDecCriterioRaggrFascTableBean(null, null, idStrut, null);
            DecodeMap mappaCriteri = DecodeMap.Factory.newInstance(tmpTableBeanCriteri,
                    "id_criterio_raggr_fasc", "nm_criterio_raggr");

            // Ricavo tutti i Tipi Fascicolo abilitati per la struttura passata in input
            DecTipoFascicoloTableBean tmpTableBeanTipoFasc = fascicoliEjb
                    .getTipiFascicoloAbilitati(getUser().getIdUtente(), idStrut);
            DecodeMap mappaTipoFascicolo = DecodeMap.Factory.newInstance(tmpTableBeanTipoFasc,
                    "id_tipo_fascicolo", "nm_tipo_fascicolo");

            if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                getForm().getFiltriElenchiVersFascicoli().getId_criterio_raggr_fasc()
                        .setDecodeMap(mappaCriteri);
                getForm().getFiltriElenchiVersFascicoli().getId_tipo_fascicolo()
                        .setDecodeMap(mappaTipoFascicolo);
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                // TODO, verificare
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
                // TODO, verificare
            }
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ente quando questo è l'unico presente
     * e settare di conseguenza la combo struttura
     *
     * @param idEnte  id ente
     * @param sezione enumerativo
     *
     * @throws EMFError errore generico
     */
    public void checkUniqueEnteInCombo(BigDecimal idEnte,
            Enum<ActionEnums.SezioneElenchiVersFascicoli> sezione) throws EMFError {
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");

            if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                getForm().getFiltriElenchiVersFascicoli().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                getForm().getFiltriElenchiVersFascDaFirmare().getId_strut()
                        .setDecodeMap(mappaStrut);
            }

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di
            // essa
            if (tmpTableBeanStrut.size() == 1) {
                if (sezione.equals(
                        ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                    getForm().getFiltriElenchiVersFascicoli().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(
                        ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                    getForm().getFiltriElenchiVersFascDaFirmare().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                }
            }
        }
    }

    public Fields triggerAmbienteGenerico(Fields campi,
            ActionEnums.SezioneElenchiVersFascicoli sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox ambienteCombo = (ComboBox) campi.getComponent("id_ambiente");
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

        ComboBox tipoFascicoloCombo = null;
        ComboBox criterioRaggrCombo = null;
        if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
            tipoFascicoloCombo = (ComboBox) campi.getComponent("id_tipo_fascicolo");
            criterioRaggrCombo = (ComboBox) campi.getComponent("id_criterio_raggr_fasc");
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
            // TODO, verificare
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
            // TODO, verificare
        }

        // Azzero i valori preimpostati delle varie combo
        enteCombo.setValue("");
        strutCombo.setValue("");

        if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
            if (tipoFascicoloCombo != null) {
                tipoFascicoloCombo.setValue("");
            }
            if (criterioRaggrCombo != null) {
                criterioRaggrCombo.setValue("");
            }
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
            // TODO, verificare
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
            // TODO, verificare
        }

        BigDecimal idAmbiente = (!ambienteCombo.getValue().equals("")
                ? new BigDecimal(ambienteCombo.getValue())
                : null);
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(
                    getUser().getIdUtente(), idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
            enteCombo.setDecodeMap(mappaEnte);
            // Se ho un solo ente lo setto già impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                enteCombo.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(), sezione);
            } else {
                strutCombo.setDecodeMap(new DecodeMap());
                if (sezione.equals(
                        ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                    if (tipoFascicoloCombo != null) {
                        tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                    }
                    if (criterioRaggrCombo != null) {
                        criterioRaggrCombo.setDecodeMap(new DecodeMap());
                    }
                } else if (sezione.equals(
                        ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                    strutCombo.setDecodeMap(new DecodeMap());
                } else if (sezione.equals(
                        ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
                    // TODO: verificare
                }
            }
        } else {
            enteCombo.setDecodeMap(new DecodeMap());
            strutCombo.setDecodeMap(new DecodeMap());
            if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                if (tipoFascicoloCombo != null) {
                    tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                }
                if (criterioRaggrCombo != null) {
                    criterioRaggrCombo.setDecodeMap(new DecodeMap());
                }
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                // TODO: verificare
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
                // TODO: verificare
            }
        }
        return campi;
    }

    public Fields triggerEnteGenerico(Fields campi, ActionEnums.SezioneElenchiVersFascicoli sezione)
            throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox enteCombo = (ComboBox) campi.getComponent("id_ente");
        ComboBox strutCombo = (ComboBox) campi.getComponent("id_strut");

        ComboBox tipoFascicoloCombo = null;
        ComboBox criterioRaggrCombo = null;
        if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
            tipoFascicoloCombo = (ComboBox) campi.getComponent("id_tipo_fascicolo");
            criterioRaggrCombo = (ComboBox) campi.getComponent("id_criterio_raggr_fasc");
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
            // TODO, verificare
        }

        // Azzero i valori preimpostati delle varie combo
        strutCombo.setValue("");

        if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
            if (tipoFascicoloCombo != null) {
                tipoFascicoloCombo.setValue("");
            }
            if (criterioRaggrCombo != null) {
                criterioRaggrCombo.setValue("");
            }
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
            // TODO, verificare
        } else if (sezione.equals(
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
            // TODO, verificare
        }

        BigDecimal idEnte = (!enteCombo.getValue().equals("") ? new BigDecimal(enteCombo.getValue())
                : null);
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb
                    .getOrgStrutTableBean(getUser().getIdUtente(), idEnte, Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            strutCombo.setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto già impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                strutCombo.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                checkUniqueStrutInCombo(tmpTableBeanStrut.getRow(0).getIdStrut(), sezione);
            } else if (sezione
                    .equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                if (tipoFascicoloCombo != null) {
                    tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                }
                if (criterioRaggrCombo != null) {
                    criterioRaggrCombo.setDecodeMap(new DecodeMap());
                }
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                // TODO, verificare
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
                // TODO, verificare
            }
        } else {
            strutCombo.setDecodeMap(new DecodeMap());
            if (sezione.equals(ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC)) {
                if (tipoFascicoloCombo != null) {
                    tipoFascicoloCombo.setDecodeMap(new DecodeMap());
                }
                if (criterioRaggrCombo != null) {
                    criterioRaggrCombo.setDecodeMap(new DecodeMap());
                }
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE)) {
                // TODO, verificare
            } else if (sezione.equals(
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP)) {
                // TODO, verificare
            }
        }
        return campi;
    }

    /* CARICA DETTAGLIO */
    @Override
    public void loadDettaglio() throws EMFError {
        /* Tabella considerata */
        String lista = getTableName();
        /* Azione considerata */
        String azione = getNavigationEvent();

        /*
         * Il caricamento del dettaglio va effettuato in tutti i casi TRANNE che in fase di
         * inserimento
         */
        if (lista != null && azione != null && !azione.equals(NE_DETTAGLIO_INSERT)) {
            /* Se ho cliccato sul DETTAGLIO della LISTA ELENCHI DI VERSAMENTO FASCICOLI */
            if (lista.equals(getForm().getElenchiVersFascicoliList().getName())) {
                BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliList().getTable()
                        .getCurrentRow()
                        .getBigDecimal(ElvVRicElencoFascTableDescriptor.COL_ID_ELENCO_VERS_FASC);
                dettaglioElencoVersFascicoli(idElencoVersFasc);
            } else if (lista.equals(getForm().getElenchiVersFascicoliDaFirmareList().getName())) {
                BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliDaFirmareList()
                        .getTable().getCurrentRow().getBigDecimal(
                                ElvVRicElencoFascByStatoTableDescriptor.COL_ID_ELENCO_VERS_FASC);
                dettaglioElencoVersFascicoli(idElencoVersFasc);
            } else if (lista.equals(getForm().getElenchiIndiciAipFascDaFirmareList().getName())) {
                BigDecimal idElencoVersFasc = getForm().getElenchiIndiciAipFascDaFirmareList()
                        .getTable().getCurrentRow().getBigDecimal(
                                ElvVRicElencoFascByStatoTableDescriptor.COL_ID_ELENCO_VERS_FASC);
                dettaglioElencoVersFascicoli(idElencoVersFasc);
            } else if (lista.equals(getForm().getElenchiVersFascicoliSelezionatiList().getName())) {
                BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliSelezionatiList()
                        .getTable().getCurrentRow().getBigDecimal(
                                ElvVRicElencoFascByStatoTableDescriptor.COL_ID_ELENCO_VERS_FASC);
                dettaglioElencoVersFascicoli(idElencoVersFasc);
            } else if (lista.equals(getForm().getElenchiIndiciAipFascSelezionatiList().getName())) {
                BigDecimal idElencoVersFasc = getForm().getElenchiIndiciAipFascSelezionatiList()
                        .getTable().getCurrentRow().getBigDecimal(
                                ElvVRicElencoFascByStatoTableDescriptor.COL_ID_ELENCO_VERS_FASC);
                dettaglioElencoVersFascicoli(idElencoVersFasc);
            }
        }
    }

    public void dettaglioElencoVersFascicoli(BigDecimal idElencoVersFasc) throws EMFError {
        /* Carico i dati nel dettaglio nell'online */
        ElvElencoVersFascRowBean elencoVersFascRowBean = evfEjb
                .getElvElencoVersFascRowBean(idElencoVersFasc);
        ElvStatoElencoVersFascRowBean statoElencoVersFascRowBean = evfEjb
                .getElvStatoElencoVersFascRowBean(
                        elencoVersFascRowBean.getIdStatoElencoVersFascCor());
        getForm().getElenchiVersFascicoliDetail().copyFromBean(elencoVersFascRowBean);

        /* Carico la lista degli stati assunti dall'elenco di versamento fascicoli */
        ElvStatoElencoVersFascTableBean statiElencoVersFascTableBean = evfEjb
                .getElvStatiElencoVersFascTableBean(idElencoVersFasc);
        getForm().getStatiElencoVersFascicoliList().setTable(statiElencoVersFascTableBean);
        getForm().getStatiElencoVersFascicoliList().setUserOperations(false, false, false, false);
        getForm().getStatiElencoVersFascicoliList().getTable().addSortingRule(
                getForm().getStatiElencoVersFascicoliList().getTs_stato().getName(),
                SortingRule.ASC);
        getForm().getStatiElencoVersFascicoliList().getTable().setPageSize(20);

        /* Carico i bottoni della pagina di dettaglio elenco di versamento fascicoli */
        initBottoniPaginaDettaglioElencoVersFascicoli(elencoVersFascRowBean.getIdElencoVersFasc(),
                statoElencoVersFascRowBean.getTiStato());

        /* Carico i valori dei filtri nel tab Filtri Fascicoli */
        initComboFiltriFascicoli(elencoVersFascRowBean.getIdStrut());

        /* Metto lista e dettaglio in viewMode e status view */
        getForm().getElenchiVersFascicoliDetail().setViewMode();
        getForm().getElenchiVersFascicoliList().setViewMode();
        getForm().getElenchiVersFascicoliDetail().setStatus(Status.view);
        getForm().getElenchiVersFascicoliList().setStatus(Status.view);
        getForm().getDettaglioElencoTabs()
                .setCurrentTab(getForm().getDettaglioElencoTabs().getDettaglioElencoTab());
        getForm().getFascicoloSection().setLoadOpened(true);

        /* Carico la lista fascicoli relativi all'elenco di versamento fascicoli */
        if ((getTableName() != null && (getTableName()
                .equals(getForm().getElenchiVersFascicoliList().getName())
                || getTableName().equals(getForm().getElenchiVersFascicoliDaFirmareList().getName())
                || getTableName()
                        .equals(getForm().getElenchiIndiciAipFascDaFirmareList().getName())))
                || getTableName() == null) {
            /* Gestione dei tipi dato soggetti alle abilitazioni */
            DecTipoFascicoloTableBean tmpTableBeanTipoFasc = fascicoliEjb.getTipiFascicoloAbilitati(
                    getUser().getIdUtente(), elencoVersFascRowBean.getIdStrut());
            FasFascicoloTableBean listaFasc = fascicoliHelper.getListaFasFascicoloElvViewBean(
                    idElencoVersFasc, new ElenchiVersFascicoliForm.FascicoliFiltri(),
                    tmpTableBeanTipoFasc);
            getForm().getFascicoliList().setTable(listaFasc);
            getForm().getFascicoliList().getTable().setPageSize(10);
            // Workaround in modo che la lista punti al primo record, non all'ultimo
            getForm().getFascicoliList().getTable().first();
        }

        /* Posso selezionare i fascicoli in base allo stato dell'elenco */
        String statoElenco = getForm().getElenchiVersFascicoliDetail().getTi_stato().getValue();
        if ((getNavigationEvent() != null
                && getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW))
                || getLastPublisher().equals(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL)) {
            if (statoElenco.equals(ElencoStatusEnum.APERTO.name())
                    || statoElenco.equals(ElencoStatusEnum.DA_CHIUDERE.name())) {
                /* Imposto la checkbox di "Selezione fascicoli da eliminare" visibile */
                getForm().getFascicoliList().getSelect_fasc().setHidden(false);
                getForm().getFascicoliList().getSelect_fasc().setEditMode();
            } else {
                getForm().getFascicoliList().getSelect_fasc().setHidden(true);
                getForm().getFascicoliList().getSelect_fasc().setViewMode();
            }
        }
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        String azione = getNavigationEvent();

        BigDecimal idStruttura = getForm().getElenchiVersFascicoliDetail().getId_strut().parse();

        if (!azione.equals(ListAction.NE_DETTAGLIO_DELETE)) {
            /* Ricavo i tipi dato cui l'utente è abilitato */
            Set<Object> tipiFascicoloAbilitatiSet = DecodeMap.Factory
                    .newInstance(tipoFascicoloEjb.getTipiFascicoloAbilitati(getIdUtenteCorrente(),
                            idStruttura, true), "id_tipo_fascicolo", "nm_tipo_fascicolo")
                    .keySet();

            /* Ho cliccato sulla lente per il dettaglio di Elenchi di Versamento Fascicoli */
            if (lista.equals(getForm().getElenchiVersFascicoliList().getName())
                    || lista.equals(getForm().getElenchiVersFascicoliDaFirmareList().getName())
                    || lista.equals(getForm().getElenchiIndiciAipFascDaFirmareList().getName())
                    || lista.equals(getForm().getElenchiIndiciAipFascSelezionatiList().getName())) {
                forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
            } /* Dettaglio FASCICOLO */ else if (lista
                    .equals(getForm().getFascicoliList().getName())) {
                BigDecimal idFascicolo = null;
                /* Verifico se l'utente può accedere al dettaglio fascicolo */
                FasFascicoloRowBean rigaFasc = (FasFascicoloRowBean) getForm().getFascicoliList()
                        .getTable().getCurrentRow();
                if (tipiFascicoloAbilitatiSet.contains(rigaFasc.getIdTipoFascicolo())) {
                    idFascicolo = rigaFasc.getIdFascicolo();
                } else {
                    getMessageBox().addError(
                            "Utente non abilitato ad accedere al dettaglio del fascicolo selezionato");
                    forwardToPublisher(getLastPublisher());
                }
                if (idFascicolo != null) {
                    FasVRicFascicoliRowBean fasVRicFascicoliRowBean = fascicoliEjb
                            .retrieveFasVRicFascicoli(idFascicolo);
                    if (!fasVRicFascicoliRowBean.getTiStatoConservazione()
                            .equals(TiStatoConservazione.ANNULLATO.name())) {
                        /* Preparo la LISTA FASCICOLI */
                        FascicoliForm form = new FascicoliForm();
                        form.getFascicoliList().setTable(getForm().getFascicoliList().getTable());
                        redirectToAction(Application.Actions.FASCICOLI,
                                "?operation=listNavigationOnClick&navigationEvent="
                                        + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                        + FascicoliForm.FascicoliList.NAME + "&riga=" + getForm()
                                                .getFascicoliList().getTable().getCurrentRowIndex(),
                                form);
                    } else {
                        getMessageBox().addError(
                                "Operazione non possibile in quanto il fascicolo ha stato di conservazione = ANNULLATO");
                        forwardToPublisher(getLastPublisher());
                    }
                }
            }
        }
    }

    /* INSERIMENTO */
    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of
        // generated methods, choose
        // Tools | Templates.
    }

    /* MODIFICA */
    @Override
    public void updateElenchiVersFascicoliList() {
        String statoElenco = getForm().getElenchiVersFascicoliList().getTable().getCurrentRow()
                .getString("ti_stato");
        boolean editable = false;
        if (statoElenco.equals(ElencoStatusEnum.CHIUSO.name())
                || statoElenco.equals(ElencoStatusEnum.FIRMATO.name())
                || statoElenco.equals(ElencoStatusEnum.IN_CODA_CREAZIONE_AIP.name())
                || statoElenco.equals(ElencoStatusEnum.AIP_CREATI.name())
                || statoElenco.equals(ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name())
                || statoElenco.equals(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name())
                || statoElenco.equals(ElencoStatusEnum.COMPLETATO.name())) {
            getForm().getElenchiVersFascicoliDetail().getNt_elenco_chiuso().setEditMode();
            editable = true;
        } else if (statoElenco.equals(ElencoStatusEnum.APERTO.name())
                || statoElenco.equals(ElencoStatusEnum.DA_CHIUDERE.name())) {
            getForm().getElenchiVersFascicoliDetail().getNt_indice_elenco().setEditMode();
            getForm().getElenchiVersFascicoliDetail().getNt_elenco_chiuso().setEditMode();
            editable = true;
        }

        if (editable) {
            getForm().getElenchiVersFascicoliList().setStatus(Status.update);
            getForm().getElenchiVersFascicoliDetail().setStatus(Status.update);
        } else {
            getMessageBox().addError("Modifica dell'elenco di versamento fascicoli non permessa");
        }
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    /* CANCELLAZIONE */
    @Override
    public void deleteElenchiVersFascicoliList() throws EMFError {
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliList().getTable()
                .getCurrentRow().getBigDecimal("id_elenco_vers_fasc");
        /* Se non posso eliminare l'elenco, avverto l'utente... */
        if (!evfEjb.isElencoDeletable(idElencoVersFasc)) {
            getMessageBox().addError(
                    "L'elenco di versamento fascicoli è stato firmato: eliminazione non consentita");
        } /* ...altrimenti procedo con la rimozione */ else {
            try {
                evfEjb.deleteElenco(getIdUtenteCorrente(), idElencoVersFasc);
                getForm().getElenchiVersFascicoliList().getTable().remove();
                getMessageBox().addInfo("Elenco di versamento fascicoli eliminato con successo");
            } catch (Exception e) {
                getMessageBox().addError(
                        "Errore durante l'eliminazione dell'elenco di versamento fascicoli");
            } finally {
                String lastPublisher = getLastPublisher();
                if (Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL.equals(lastPublisher)) {
                    goBack();
                } else {
                    forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_RICERCA);
                }
            }
        }
    }

    @Override
    public void deleteElenchiVersFascicoliDaFirmareList() throws EMFError {
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                .getCurrentRow().getBigDecimal("id_elenco_vers_fasc");
        try {
            evfEjb.deleteElenco(getIdUtenteCorrente(), idElencoVersFasc);
            getForm().getElenchiVersFascicoliDaFirmareList().getTable().remove();
            getMessageBox().addInfo("Elenco di versamento fascicoli eliminato con successo");
        } catch (Exception e) {
            getMessageBox().addError(
                    "Errore durante l'eliminazione dell'elenco di versamento fascicoli da firmare");
        } finally {
            String lastPublisher = getLastPublisher();
            if (Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL.equals(lastPublisher)) {
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
            }
        }
    }

    /* SALVA */
    @Override
    public void saveDettaglio() throws EMFError {
        /* Valori pre modifiche */
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliList().getTable()
                .getCurrentRow().getBigDecimal("id_elenco_vers_fasc");
        String rowNtElencoChiuso = getForm().getElenchiVersFascicoliList().getTable()
                .getCurrentRow().getString("nt_elenco_chiuso");
        String rowNtIndiceElenco = getForm().getElenchiVersFascicoliList().getTable()
                .getCurrentRow().getString("nt_indice_elenco");
        String rowTiStato = getForm().getElenchiVersFascicoliList().getTable().getCurrentRow()
                .getString("ti_stato");

        /* Valori post modifiche */
        getForm().getElenchiVersFascicoliDetail().post(getRequest());
        String ntIndiceElenco = getForm().getElenchiVersFascicoliDetail().getNt_indice_elenco()
                .parse();
        String ntElencoChiuso = getForm().getElenchiVersFascicoliDetail().getNt_elenco_chiuso()
                .parse();

        // Controllo se e cosa ho modificato
        List<ElencoEnums.OpTypeEnum> operList = new ArrayList<>();

        if (!StringUtils.equals(rowNtElencoChiuso, ntElencoChiuso)) {
            operList.add(ElencoEnums.OpTypeEnum.DEF_NOTE_ELENCO_CHIUSO);
        } else if (!StringUtils.equals(rowNtIndiceElenco, ntIndiceElenco)) {
            operList.add(ElencoEnums.OpTypeEnum.DEF_NOTE_INDICE_ELENCO);
        }

        if (getForm().getElenchiVersFascicoliDetail().validate(getMessageBox())
                && !getMessageBox().hasError()) {
            try {
                evfEjb.saveNote(getIdUtenteCorrente(), idElencoVersFasc, ntIndiceElenco,
                        ntElencoChiuso, operList);
                getMessageBox().addInfo("Elenco di versamento fascicoli modificato con successo");
                initBottoniPaginaDettaglioElencoVersFascicoli(idElencoVersFasc, rowTiStato);
                setElencoVersFascicoliListAndDetailViewMode();

                reloadElenchiVersFascicoliList(getForm().getFiltriElenchiVersFascicoli());
                forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
            } catch (Exception e) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getMessage()));
            }
        }

    }

    /* ANNULLA */
    @Override
    public void undoDettaglio() throws EMFError {
        dettaglioElencoVersFascicoli(
                getForm().getElenchiVersFascicoliDetail().getId_elenco_vers_fasc().parse());
        setElencoVersFascicoliListAndDetailViewMode();
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    /* INDIETRO */
    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    /**
     *
     * @param publisherName la pagina cui deve andare a seguito della goBack()
     */
    @Override
    public void reloadAfterGoBack(String publisherName) {
        if (publisherName != null) {
            String lista = getTableName();
            /*
             * Se tornando indietro devo andare nella pagina di Ricerca Elenchi di Versamento
             * Fascicoli rilancio la ricerca in quanto posso aver modificato l'elenco esaminato
             */
            if (publisherName.equals(Application.Publisher.ELENCHI_VERS_FASCICOLI_RICERCA)
                    && (lista != null
                            && lista.equals(getForm().getElenchiVersFascicoliList().getName()))
                    || (getRequest().getParameter("mainNavTable") != null
                            && getRequest().getParameter("mainNavTable")
                                    .equals(getForm().getElenchiVersFascicoliList().getName()))) {
                try {
                    reloadElenchiVersFascicoliList(getForm().getFiltriElenchiVersFascicoli());
                } catch (EMFError ex) {
                    log.error(ex.getDescription(), ex);
                }
            } else if (publisherName.equals(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL)) {
                try {
                    dettaglioElencoVersFascicoli(getForm().getElenchiVersFascicoliDetail()
                            .getId_elenco_vers_fasc().parse());
                } catch (EMFError ex) {
                    log.error(ex.getDescription(), ex);
                }
            }
        }
    }

    /* TAB */
    /**
     * Attiva il tab del dettaglio elenco elenco di versamento fascicoli
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabDettaglioElencoTabOnClick() throws EMFError {
        getForm().getDettaglioElencoTabs()
                .setCurrentTab(getForm().getDettaglioElencoTabs().getDettaglioElencoTab());
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    /**
     * Attiva il tab dei filtri sui fascicoli nel dettaglio elenco di versamento
     *
     * @throws EMFError errore generico
     */
    @Override
    public void tabFiltriRicercaFascicoliTabOnClick() throws EMFError {
        getForm().getDettaglioElencoTabs()
                .setCurrentTab(getForm().getDettaglioElencoTabs().getFiltriRicercaFascicoliTab());
        getForm().getFascicoliFiltri().setEditMode();
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    /* BOTTONI */
    /**
     * Metodo richiamato al click del bottone di ricerca elenchi di versamento fascicoli
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaElenchiButton() throws EMFError {
        ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli filtriElenchiVersFascicoli = getForm()
                .getFiltriElenchiVersFascicoli();
        /* Esegue la post dei filtri compilati */
        filtriElenchiVersFascicoli.post(getRequest());
        /* Valida i filtri per verificarne la correttezza sintattica e l'obbligatorietà */
        if (filtriElenchiVersFascicoli.validate(getMessageBox())) {
            /* Valida in maniera più specifica i dati, utilizzando il validator dei fascicoli */
            RicercaFascicoliBean result = new RicercaFascicoliBean();
            FascicoliValidator validator = new FascicoliValidator(getMessageBox());
            validator.validaOrdineDateOrari(filtriElenchiVersFascicoli.getTs_creazione_elenco_da(),
                    filtriElenchiVersFascicoli.getTs_creazione_elenco_a());
            validator.validaChiaviFascicoliElenchi(result,
                    filtriElenchiVersFascicoli.getAa_fascicolo(),
                    filtriElenchiVersFascicoli.getAa_fascicolo_da(),
                    filtriElenchiVersFascicoli.getAa_fascicolo_a(),
                    filtriElenchiVersFascicoli.getCd_key_fascicolo(),
                    filtriElenchiVersFascicoli.getCd_key_fascicolo_da(),
                    filtriElenchiVersFascicoli.getCd_key_fascicolo_a());
            /* Se la validazione più approfondita non ha riportato errori */
            if (!getMessageBox().hasError()) {
                boolean chiave = false;
                boolean range = false;

                /*
                 * Controllo dove sono stati inseriti i filtri tra la chiave fascicolo singola e la
                 * chiave fascicolo per range
                 */
                if (result.getAa_fascicolo() != null || result.getCd_key_fascicolo() != null) {
                    chiave = true;
                }
                if (result.getAa_fascicolo_da() != null || result.getAa_fascicolo_a() != null
                        || result.getCd_key_fascicolo_da() != null
                        || result.getCd_key_fascicolo_a() != null) {
                    range = true;
                }

                /* Setto i filtri di chiavi fascicolo impostando gli eventuali valori di default */
                if (range) {
                    filtriElenchiVersFascicoli.getAa_fascicolo_da()
                            .setValue(result.getAa_fascicolo_da() != null
                                    ? result.getAa_fascicolo_da().toString()
                                    : null);
                    filtriElenchiVersFascicoli.getAa_fascicolo_a()
                            .setValue(result.getAa_fascicolo_a() != null
                                    ? result.getAa_fascicolo_a().toString()
                                    : null);
                    filtriElenchiVersFascicoli.getCd_key_fascicolo_da()
                            .setValue(result.getCd_key_fascicolo_da() != null
                                    ? result.getCd_key_fascicolo_da()
                                    : null);
                    filtriElenchiVersFascicoli.getCd_key_fascicolo_a()
                            .setValue(result.getCd_key_fascicolo_a() != null
                                    ? result.getCd_key_fascicolo_a()
                                    : null);
                }

                /*
                 * Controllo se sono stati inseriti i filtri tra tipo fascicolo indice di
                 * classificazione
                 */
                BigDecimal idTipoFascicolo = filtriElenchiVersFascicoli.getId_tipo_fascicolo()
                        .parse();
                String cdCompositoVoceTitol = filtriElenchiVersFascicoli
                        .getCd_composito_voce_titol().parse();
                String tiStato = filtriElenchiVersFascicoli.getTi_stato().parse();

                /* Effettuo la ricerca */
                if (chiave || range || idTipoFascicolo != null
                        || StringUtils.isNotBlank(cdCompositoVoceTitol)
                        || StringUtils.isBlank(tiStato)) {
                    // Se sono presenti i filtri del fascicolo
                    getForm().getElenchiVersFascicoliList().setTable(
                            evfEjb.getElvVRicElencoFascByFasTableBean(getUser().getIdUtente(),
                                    filtriElenchiVersFascicoli));
                } else {
                    // Se non sono presenti i filtri del fascicolo
                    if (!tiStato.equals(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())) {
                        // Se il filtro di stato elenco è != COMPLETATO
                        getForm().getElenchiVersFascicoliList().setTable(
                                evfEjb.getElvVRicElencoFascByStatoTableBean(getUser().getIdUtente(),
                                        filtriElenchiVersFascicoli));
                    } else {
                        getForm().getElenchiVersFascicoliList().setTable(
                                evfEjb.getElvVRicElencoFascTableBean(getUser().getIdUtente(),
                                        filtriElenchiVersFascicoli));
                    }
                }
                getForm().getElenchiVersFascicoliList().getTable().setPageSize(10);
                getForm().getElenchiVersFascicoliList().getTable().first();

                /*
                 * Imposto la sezione del filtro chiave fascicolo aperto dopo la ricerca se ho
                 * inserito un valore nel campo tipo fascicolo
                 */
                if (!getForm().getFiltriElenchiVersFascicoli().getId_tipo_fascicolo().getValue()
                        .equals("")) {
                    getForm().getFascicoloSection().setLoadOpened(true);
                }
            }
        }
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_RICERCA);
    }

    /**
     * Ricerca fascicoli all'interno del dettaglio elenco di versamento fascicoli
     *
     * @throws EMFError errore generico
     */
    @Override
    public void ricercaFasc() throws EMFError {
        ElenchiVersFascicoliForm.FascicoliFiltri fascfiltri = getForm().getFascicoliFiltri();
        fascfiltri.post(getRequest());
        if (fascfiltri.validate(getMessageBox())) {

            RicercaFascicoliBean result = new RicercaFascicoliBean();
            FascicoliValidator validator = new FascicoliValidator(getMessageBox());
            validator.validaOrdineDateOrari(fascfiltri.getTs_ini_ses_da(),
                    fascfiltri.getTs_ini_ses_a());
            validator.validaOrdineDateOrari(fascfiltri.getDt_ape_fascicolo_da(),
                    fascfiltri.getDt_ape_fascicolo_a());
            validator.validaOrdineDateOrari(fascfiltri.getDt_chiu_fascicolo_da(),
                    fascfiltri.getDt_chiu_fascicolo_a());

            if (!getMessageBox().hasError()) {
                /* Valida i campi di Range di chiavi fascicolo */
                validator.validaChiaviFascicoliElenchi(result, fascfiltri.getAa_fascicolo(),
                        fascfiltri.getAa_fascicolo_da(), fascfiltri.getAa_fascicolo_a(),
                        fascfiltri.getCd_key_fascicolo(), fascfiltri.getCd_key_fascicolo_da(),
                        fascfiltri.getCd_key_fascicolo_a());
            }

            /* Gestione dei tipi dato soggetti alle abilitazioni */
            DecTipoFascicoloTableBean tmpTableBeanTipoFascicolo = fascicoliEjb
                    .getTipiFascicoloAbilitati(getUser().getIdUtente(), getIdStrutCorrente());

            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori.
                boolean range = false;

                /*
                 * Controllo dove sono stati inseriti i filtri tra la chiave fascicolo singola e la
                 * chiave fascicolo per range
                 */
                if (result.getAa_fascicolo_da() != null || result.getAa_fascicolo_a() != null
                        || result.getCd_key_fascicolo_da() != null
                        || result.getCd_key_fascicolo_a() != null) {
                    range = true;
                }

                /* Setto i filtri di chiavi fascicolo impostando gli eventuali valori di default */
                if (range) {
                    fascfiltri.getAa_fascicolo_da()
                            .setValue(result.getAa_fascicolo_da() != null
                                    ? result.getAa_fascicolo_da().toString()
                                    : null);
                    fascfiltri.getAa_fascicolo_a()
                            .setValue(result.getAa_fascicolo_a() != null
                                    ? result.getAa_fascicolo_a().toString()
                                    : null);
                    fascfiltri.getCd_key_fascicolo_da()
                            .setValue(result.getCd_key_fascicolo_da() != null
                                    ? result.getCd_key_fascicolo_da()
                                    : null);
                    fascfiltri.getCd_key_fascicolo_a()
                            .setValue(result.getCd_key_fascicolo_a() != null
                                    ? result.getCd_key_fascicolo_a()
                                    : null);
                }
                getForm().getFascicoliList()
                        .setTable(
                                fascicoliHelper.getListaFasFascicoloElvViewBean(
                                        getForm().getElenchiVersFascicoliDetail()
                                                .getId_elenco_vers_fasc().parse(),
                                        fascfiltri, tmpTableBeanTipoFascicolo));
                getForm().getFascicoliList().getTable().setPageSize(10);
                getForm().getFascicoliList().setUserOperations(true, false, false, false);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getFascicoliList().getTable().first();
                // Imposto la sezione del filtro chiave fascicolo aperto dopo la ricerca se ho
                // inserito un valore nel
                // campo tipo fascicolo
                if (getForm().getFascicoliFiltri().getId_tipo_fascicolo().getValue() != null) {
                    getForm().getFascicoloSection().setLoadOpened(true);
                }
            }
        }
        // Workaround per evitare che il trigger scarichi la pagina HTML anziche' visualizzarla sul
        // browser
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    @Override
    public void pulisciFiltriRicercaElenchiButton() throws EMFError {
        ricercaElenchiVersFascicoli();
    }

    /**
     * Bottone di chiusura elenco di versamento fascicolo. Se presente, significa che sono già
     * soddisfatte le condizioni per la chiusura dell'elenco (ovvero stato APERTO e con almeno un
     * fascicolo appartenente)
     *
     * @throws Throwable errore generico
     */
    @Override
    public void chiudiElencoButton() throws Throwable {
        getRequest().setAttribute("chiudiElencoBox", true);
        getRequest().setAttribute("nt_indice_elenco",
                getForm().getElenchiVersFascicoliDetail().getNt_indice_elenco().parse());
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    public void confermaChiusuraElenco() throws EMFError {
        /* Recupero l'elenco da chiudere e le relative note su indice elenco */
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliList().getTable()
                .getCurrentRow().getBigDecimal("id_elenco_vers_fasc");
        /* Campo note prese da dettaglio */
        String noteDettaglio = getForm().getElenchiVersFascicoliDetail().getNt_indice_elenco()
                .parse();
        /* Campo note preso da finestra pop-up */
        String notePopUp = getRequest().getParameter("Nt_indice_elenco");

        /* Verifico se ci sono state modifiche sulle note indice elenco */
        List<ElencoEnums.OpTypeEnum> modifica = new ArrayList<>();
        if (!StringUtils.equals(noteDettaglio, notePopUp)) {
            modifica.add(ElencoEnums.OpTypeEnum.DEF_NOTE_INDICE_ELENCO);
        }

        try {
            evfEjb.manualClosingElenco(getIdUtenteCorrente(), idElencoVersFasc, modifica,
                    notePopUp);
            getMessageBox().addInfo("Elenco di versamento fascicolo chiuso con successo!");
        } catch (Exception e) {
            log.error(ECCEZIONE_GENERICA, e);
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "Errore durante la chiusura dell'elenco"));
        } finally {
            /*
             * Ricarico il dettaglio elenco per caricare i dati modificati e visualizzare i bottoni
             * corretti per il suo nuovo stato
             */
            dettaglioElencoVersFascicoli(idElencoVersFasc);
            forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
        }
    }

    /**
     * Bottone "+" della "Lista elenchi di versamento fascicoli da firmare" per spostare un elenco
     * da questa lista a quella degli elenchi selezionati pronti per essere firmati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiVersFascicoliDaFirmareList() throws EMFError {
        /* Ricavo il record interessato della "Lista elenchi di versamento fascicoli da firmare" */
        ElvVRicElencoFascByStatoRowBean row = (ElvVRicElencoFascByStatoRowBean) getForm()
                .getElenchiVersFascicoliDaFirmareList().getTable().getCurrentRow();
        int index = getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                .getCurrentRowIndex();
        /* Lo tolgo dalla lista elenchi di versamento fascicoli da firmare */
        getForm().getElenchiVersFascicoliDaFirmareList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                .getCurrentPageIndex();
        int inizio = getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                .getFirstRowPageIndex();
        this.lazyLoadGoPage(getForm().getElenchiVersFascicoliDaFirmareList(), paginaCorrente);
        getForm().getElenchiVersFascicoliDaFirmareList().getTable().setCurrentRowIndex(inizio);
        /* Aggiungo il record nella lista degli elenchi di versamento fascicoli selezionati */
        getForm().getElenchiVersFascicoliSelSection().setLoadOpened(true);
        getForm().getElenchiVersFascicoliSelezionatiList().add(row);
        getForm().getElenchiVersFascicoliSelezionatiList().getTable().addSortingRule(getForm()
                .getElenchiVersFascicoliSelezionatiList().getTs_creazione_elenco().getName(),
                SortingRule.ASC);
        getForm().getElenchiVersFascicoliSelezionatiList().getTable().sort();
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    /**
     * Bottone "-" della "Lista elenchi di versamento fascicoli selezionati" per spostare un elenco
     * da questa lista a quella degli elenchi di versamento fascicoli da firmare
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiVersFascicoliSelezionatiList() throws EMFError {
        /* Ricavo il record interessato della "Lista elenchi di versamento fascicoli selezionati" */
        ElvVRicElencoFascByStatoRowBean row = (ElvVRicElencoFascByStatoRowBean) getForm()
                .getElenchiVersFascicoliSelezionatiList().getTable().getCurrentRow();
        int index = getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                .getCurrentRowIndex();
        /* Lo tolgo dalla lista elenchi di versamento fascicoli selezionati */
        getForm().getElenchiVersFascicoliSelezionatiList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                .getCurrentPageIndex();
        int inizio = getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                .getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getElenchiVersFascicoliSelezionatiList(), paginaCorrente);
        // Ritorno alla pagina
        getForm().getElenchiVersFascicoliSelezionatiList().getTable().setCurrentRowIndex(inizio);
        // Pagina Elenchi da firmare
        getForm().getElenchiVersFascicoliDaFirmareList().add(row);
        int paginaCorrenteVF = getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                .getCurrentPageIndex();
        int inizioVF = getForm().getElenchiVersFascicoliDaFirmareList().getTable()
                .getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getElenchiVersFascicoliDaFirmareList(), paginaCorrenteVF);
        // Ritorno alla pagina
        getForm().getElenchiVersFascicoliDaFirmareList().getTable().setCurrentRowIndex(inizioVF);
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    @Override
    public void eliminaAppartenenzaFascDaElenco() throws Throwable {
        getForm().getFascicoliList().post(getRequest());
        /* Ottengo i fascicoli selezionati dalla lista */
        String[] fascicoliSelezionati = getRequest().getParameterValues("Select_fasc");
        List<BigDecimal> idFascicoli = new ArrayList<>();
        Set<BigDecimal> idFascToRemove = new HashSet<>();
        if (fascicoliSelezionati != null) {
            for (String fasc : fascicoliSelezionati) {
                if (StringUtils.isNumeric(fasc)) {
                    idFascicoli.add(((FasFascicoloRowBean) getForm().getFascicoliList().getTable()
                            .getRow(Integer.parseInt(fasc))).getIdFascicolo());
                    idFascToRemove.add(((FasFascicoloRowBean) getForm().getFascicoliList()
                            .getTable().getRow(Integer.parseInt(fasc))).getIdFascicolo());
                }
            }

            if (!idFascicoli.isEmpty()) {
                getSession().setAttribute("idFascToRemove", idFascToRemove);
                getRequest().setAttribute("idElenco",
                        getForm().getElenchiVersFascicoliDetail().getId_elenco_vers_fasc().parse());

                /* Apre il custombox per la conferma rimozione */
                getRequest().setAttribute("customBox", true);
                forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
            }
        } else {
            getMessageBox().addInfo("Seleziona almeno un fascicolo");
            forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
        }
    }

    @Override
    public void scaricaIndiceElencoButton() throws Throwable {
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliDetail()
                .getId_elenco_vers_fasc().parse();
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        String nmEnte = getForm().getElenchiVersFascicoliDetail().getNm_ente().parse();
        String nmStrut = getForm().getElenchiVersFascicoliDetail().getNm_strut().parse();
        String filesPrefix = sistema + "_" + nmEnte.replaceAll(" ", "_") + "_"
                + nmStrut.replaceAll(" ", "_") + "_ElencoVers-FA-" + idElencoVersFasc;
        String filesSuffix = ":Indice";
        /* Comincio a costruire lo zip */
        String nomeZip = filesPrefix;
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + nomeZip + ".zip");
        ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
        try {
            // MEV#31922 - Introduzione modalità NO FIRMA nella validazione degli elenchi di
            // versamento dei fascicoli
            evfEjb.streamOutFileIndiceElencoNoFirma(out, filesPrefix, filesSuffix,
                    idElencoVersFasc.longValue());
            // evfEjb.streamOutFileIndiceElenco(out, filesPrefix, filesSuffix,
            // idElencoVersFasc.longValue(),
            // FileTypeEnum.getIndiceFileTypes());
            out.close();
            freeze();
        } catch (Exception e) {
            log.error(ECCEZIONE_GENERICA, e);
            getMessageBox().addError("Errore nel recupero dei file delle prove di conservazione");
            forwardToPublisher(getLastPublisher());
        }
    }

    /* UTILITIES */
    private void setElencoVersFascicoliListAndDetailViewMode() {
        getForm().getElenchiVersFascicoliDetail().setViewMode();
        getForm().getElenchiVersFascicoliDetail().setStatus(Status.view);
        getForm().getElenchiVersFascicoliList().setViewMode();
        getForm().getElenchiVersFascicoliList().setStatus(Status.view);
    }

    public boolean areEquals(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    private void reloadElenchiVersFascicoliList(
            ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli filtriElenchiVersFascicoli)
            throws EMFError {
        int paginaCorrente = getForm().getElenchiVersFascicoliList().getTable()
                .getCurrentPageIndex();
        getForm().getElenchiVersFascicoliList().getTable().getFirstRowPageIndex();
        int pageSize = getForm().getElenchiVersFascicoliList().getTable().getPageSize();

        boolean chiave = false;
        boolean range = false;

        RicercaFascicoliBean result = new RicercaFascicoliBean();
        FascicoliValidator validator = new FascicoliValidator(getMessageBox());
        validator.validaChiaviFascicoliElenchi(result, filtriElenchiVersFascicoli.getAa_fascicolo(),
                filtriElenchiVersFascicoli.getAa_fascicolo_da(),
                filtriElenchiVersFascicoli.getAa_fascicolo_a(),
                filtriElenchiVersFascicoli.getCd_key_fascicolo(),
                filtriElenchiVersFascicoli.getCd_key_fascicolo_da(),
                filtriElenchiVersFascicoli.getCd_key_fascicolo_a());

        /*
         * Controllo dove sono stati inseriti i filtri tra la chiave fascicolo singola e la chiave
         * fascicolo per range
         */
        if (result.getAa_fascicolo() != null || result.getCd_key_fascicolo() != null) {
            chiave = true;
        }
        if (result.getAa_fascicolo_da() != null || result.getAa_fascicolo_a() != null
                || result.getCd_key_fascicolo_da() != null
                || result.getCd_key_fascicolo_a() != null) {
            range = true;
        }
        /* Controllo se sono stati inseriti i filtri tra tipo fascicolo indice di classificazione */
        BigDecimal idTipoFascicolo = filtriElenchiVersFascicoli.getId_tipo_fascicolo().parse();
        String cdCompositoVoceTitol = filtriElenchiVersFascicoli.getCd_composito_voce_titol()
                .parse();
        String tiStato = filtriElenchiVersFascicoli.getTi_stato().parse();

        /* Effettuo la ricerca */
        if (chiave || range || idTipoFascicolo != null
                || StringUtils.isNotBlank(cdCompositoVoceTitol) || StringUtils.isBlank(tiStato)) {
            // Se sono presenti i filtri del fascicolo
            getForm().getElenchiVersFascicoliList()
                    .setTable(evfEjb.getElvVRicElencoFascByFasTableBean(getUser().getIdUtente(),
                            filtriElenchiVersFascicoli));
        } else {
            // Se non sono presenti i filtri del fascicolo
            if (!tiStato.equals(ElencoEnums.ElencoStatusEnum.COMPLETATO.name())) {
                // Se il filtro di stato elenco è != COMPLETATO
                getForm().getElenchiVersFascicoliList().setTable(
                        evfEjb.getElvVRicElencoFascByStatoTableBean(getUser().getIdUtente(),
                                filtriElenchiVersFascicoli));
            } else {
                getForm().getElenchiVersFascicoliList()
                        .setTable(evfEjb.getElvVRicElencoFascTableBean(getUser().getIdUtente(),
                                filtriElenchiVersFascicoli));
            }
        }

        getForm().getElenchiVersFascicoliList().getTable().setPageSize(pageSize);
        getForm().getElenchiVersFascicoliList().getTable().first();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getElenchiVersFascicoliList(), paginaCorrente);
        // Ritorno alla pagina
        getForm().getElenchiVersFascicoliList().getTable().setCurrentRowIndex(paginaCorrente);
        getForm().getElenchiVersFascicoliList().setUserOperations(true, true, false, true);
    }

    /* FINE UTILITIES */
    @Override
    public JSONObject triggerFiltriElenchiVersFascDaFirmareId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriElenchiVersFascDaFirmare(),
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE);
        return getForm().getFiltriElenchiVersFascDaFirmare().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiVersFascDaFirmareId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriElenchiVersFascDaFirmare(),
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_DA_FIRMARE);
        return getForm().getFiltriElenchiVersFascDaFirmare().asJSON();
    }

    /**
     * Inizializza i FILTRI DI LISTA ELENCHI VERSAMENTO FASCICOLI DA FIRMARE in base alla struttura
     * con la quale l'utente è loggato
     *
     * @param idStruttura id struttura
     *
     * @throws EMFError errore generico
     */
    private void initFiltriElenchiVersFascicoliDaFirmare(BigDecimal idStruttura) {
        // Azzero i filtri
        getForm().getFiltriElenchiVersFascDaFirmare().reset();
        // Ricavo id struttura, ente ed ambiente attuali
        getUser().getIdOrganizzazioneFoglia();
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
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            log.error(ERRORE_RICERCA_AMBIENTE, ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriElenchiVersFascDaFirmare().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriElenchiVersFascDaFirmare().getId_ambiente()
                .setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriElenchiVersFascDaFirmare().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriElenchiVersFascDaFirmare().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriElenchiVersFascDaFirmare().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriElenchiVersFascDaFirmare().getId_strut()
                .setValue(idStruttura.toString());

        // Combo elenchi con note
        getForm().getFiltriElenchiVersFascDaFirmare().getElenchi_con_note()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // Imposto i filtri in editMode
        getForm().getFiltriElenchiVersFascDaFirmare().setEditMode();

        // Imposto come visibile il bottone di ricerca criteri di raggruppamento fascicoli e
        // disabilito la clessidra
        // (per IE)
        getForm().getFiltriElenchiVersFascDaFirmare().getRicercaElenchiVersFascDaFirmareButton()
                .setEditMode();
        getForm().getFiltriElenchiVersFascDaFirmare().getRicercaElenchiVersFascDaFirmareButton()
                .setDisableHourGlass(true);
    }

    @Override
    public void ricercaElenchiVersFascDaFirmareButton() throws EMFError {
        getForm().getFiltriElenchiVersFascDaFirmare().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriElenchiVersFascDaFirmare().getId_ambiente()
                .parse();
        BigDecimal idEnte = getForm().getFiltriElenchiVersFascDaFirmare().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriElenchiVersFascDaFirmare().getId_strut().parse();
        BigDecimal idElencoVersFasc = getForm().getFiltriElenchiVersFascDaFirmare()
                .getId_elenco_vers_fasc().parse();
        String presenzaNote = getForm().getFiltriElenchiVersFascDaFirmare().getElenchi_con_note()
                .parse();

        getSession().setAttribute("idStrutRif", idStrut);
        if (idStrut == null) {
            // Rimuovo l'attributo idStrutRif se presente in sessione vuol dire che si riferisce ad
            // una struttura
            // selezionata precedentemente
            getSession().removeAttribute("idStrutRif");
            // Traccio in sessione un attributo specifico
            getSession().setAttribute("isStrutNull", true);
        }

        if (getForm().getFiltriElenchiVersFascDaFirmare().validate(getMessageBox())
                && !getMessageBox().hasError()) {
            FascicoliValidator fascicoliValidator = new FascicoliValidator(getMessageBox());
            // Valido i filtri data creazione elenco fascicoli da - a restituendo le date
            // comprensive di orario
            Date[] dateCreazioneElencoFascValidate = fascicoliValidator.validaDate(
                    getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_da()
                            .parse(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getOre_ts_creazione_elenco_da()
                            .parse(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getMinuti_ts_creazione_elenco_da()
                            .parse(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_a()
                            .parse(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getOre_ts_creazione_elenco_a()
                            .parse(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getMinuti_ts_creazione_elenco_a()
                            .parse(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_da()
                            .getHtmlDescription(),
                    getForm().getFiltriElenchiVersFascDaFirmare().getTs_creazione_elenco_a()
                            .getHtmlDescription());
            if (!getMessageBox().hasError()) {
                /*
                 * Carico la lista degli elenchi di versamento fascicoli da firmare: quelli della
                 * struttura dell'utente e con stato CHIUSO
                 */
                ElvVRicElencoFascByStatoTableBean elenchiVersFascicoliTableBean = evfEjb
                        .getElenchiVersFascicoliDaFirmareTableBean(idAmbiente, idEnte, idStrut,
                                idElencoVersFasc, presenzaNote, ElencoEnums.ElencoStatusEnum.CHIUSO,
                                dateCreazioneElencoFascValidate, getUser().getIdUtente());
                getForm().getElenchiVersFascicoliDaFirmareList()
                        .setTable(elenchiVersFascicoliTableBean);
                getForm().getElenchiVersFascicoliDaFirmareList().getTable().setPageSize(10);
                getForm().getElenchiVersFascicoliDaFirmareList().getTable().first();
                getForm().getElenchiVersFascicoliDaFirmareList().getTable().addSortingRule(getForm()
                        .getElenchiVersFascicoliDaFirmareList().getTs_creazione_elenco().getName(),
                        SortingRule.ASC);
                /* Inizializzo la lista degli elenchi di versamento fascicoli selezionati */
                getForm().getElenchiVersFascicoliSelezionatiList()
                        .setTable(new ElvVRicElencoFascByStatoTableBean());
                getForm().getElenchiVersFascicoliSelezionatiList().getTable().setPageSize(10);
                getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                        .addSortingRule(getForm().getElenchiVersFascicoliSelezionatiList()
                                .getTs_creazione_elenco().getName(), SortingRule.ASC);
                /* Rendo visibili i bottoni delle operazioni sulla lista che mi interessano */
                getForm().getListaElenchiVersFascDaFirmareButtonList().setEditMode();
            }
        }

        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    @Override
    public void selectAllElenchiButton() throws Throwable {
        ElvVRicElencoFascByStatoTableBean elenchi = (ElvVRicElencoFascByStatoTableBean) getForm()
                .getElenchiVersFascicoliDaFirmareList().getTable();
        for (ElvVRicElencoFascByStatoRowBean elenco : elenchi) {
            getForm().getElenchiVersFascicoliSelezionatiList().getTable().add(elenco);
        }
        elenchi.removeAll();
        getForm().getElenchiVersFascicoliSelSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    @Override
    public void deselectAllElenchiButton() throws Throwable {
        ElvVRicElencoFascByStatoTableBean elenchi = (ElvVRicElencoFascByStatoTableBean) getForm()
                .getElenchiVersFascicoliSelezionatiList().getTable();
        for (ElvVRicElencoFascByStatoRowBean elenco : elenchi) {
            getForm().getElenchiVersFascicoliDaFirmareList().getTable().add(elenco);
        }
        elenchi.removeAll();
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    @Override
    public void selectHundredElenchiButton() throws Throwable {
        ElvVRicElencoFascByStatoTableBean elenchi = (ElvVRicElencoFascByStatoTableBean) getForm()
                .getElenchiVersFascicoliDaFirmareList().getTable();
        if (elenchi != null) {
            if (elenchi.size() <= 100) {
                selectAllElenchiButton();
            } else {
                for (int counter = 0; counter < 100; counter++) {
                    if (!elenchi.isEmpty()) {
                        ElvVRicElencoFascByStatoRowBean elenco = elenchi.getRow(0);
                        getForm().getElenchiVersFascicoliSelezionatiList().getTable().add(elenco);
                        elenchi.remove(0);
                    } else {
                        break;
                    }
                }
                getForm().getElenchiVersFascicoliSelSection().setLoadOpened(true);
                forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
            }
        }
    }

    /**
     * Invoca il meccanismo di firma del HSM.
     *
     * @throws Throwable errore generico
     */
    @Override
    // public void firmaElenchiHsmButton() throws Throwable {
    public void validaElenchiButton() throws Throwable {
        ElvElencoVersFascTableBean elenchiDaFirmare = checkElenchiVersFascicoliToValidate();
        int elenchiHsmEliminati = 0;

        if (!getMessageBox().hasError() && elenchiDaFirmare != null
                && elenchiDaFirmare.size() > 0) {
            // Controllo se l'utente è tra i firmatari definiti sull'ambiente
            int elenchiValidati = 0;
            List<BigDecimal> idElencoVersFascRigheTotali = new ArrayList<>();
            List<BigDecimal> idElencoVersFascRigheCancellate = new ArrayList<>();

            for (int i = 0; i < elenchiDaFirmare.size(); i++) {
                ElvElencoVersFascRowBean elenco = elenchiDaFirmare.getRow(i);
                BigDecimal idElencoVersFasc = elenco.getIdElencoVersFasc();
                idElencoVersFascRigheTotali.add(idElencoVersFasc);
                if (evfEjb.almenoUnFascAnnul(idElencoVersFasc)) {
                    evfEjb.manageElencoFascAnnulDaFirmaElencoFasc(idElencoVersFasc,
                            getUser().getIdUtente());
                    idElencoVersFascRigheCancellate.add(idElencoVersFasc);
                    elenchiHsmEliminati++;
                }
            }

            // Elimino a video gli elenchi di versamento fascicoli cancellati su DB in quanto
            // contenenti almeno
            // un fascicolo annullato
            idElencoVersFascRigheTotali.removeAll(idElencoVersFascRigheCancellate);
            ElvVRicElencoFascByStatoTableBean elenchiRimanenti = evfEjb
                    .getElenchiVersFascicoliDaFirmareTableBean(idElencoVersFascRigheTotali,
                            getUser().getIdUtente());
            getForm().getElenchiVersFascicoliSelezionatiList().setTable(elenchiRimanenti);

            if (elenchiHsmEliminati > 0) {
                getMessageBox().setViewMode(ViewMode.plain);
                getMessageBox().addInfo("Sono stati eliminati " + elenchiHsmEliminati
                        + " elenchi di versamento fascicoli in quanto contenenti almeno un fascicolo annullato");
            }

            if (getForm().getElenchiVersFascicoliSelezionatiList().getTable().size() > 0) {
                /* Richiedo le credenziali del HSM utilizzando apposito popup */
                // getRequest().setAttribute("customElenchiVersFascicoliSelect", true);
                // getForm().getFiltriElenchiVersFascDaFirmare().getUser().setValue(hsmUserName);
                // getForm().getFiltriElenchiVersFascDaFirmare().getUser().setViewMode();

                for (int i = 0; i < elenchiRimanenti.size(); i++) {
                    ElvVRicElencoFascByStatoRowBean riga = elenchiRimanenti.getRow(i);
                    evfEjb.validElenco(getUser().getIdUtente(), riga.getIdElencoVersFasc());
                    elenchiValidati++;
                }
            }
            // } else {
            // getMessageBox().addError("Utente non rientra tra i firmatari definiti
            // sull’ambiente");
            // }
            // }
            endValidaElenco(elenchiValidati);
        }

        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_VERS_FASCICOLI_SELECT);
    }

    public void endValidaElenco(int validati) throws EMFError {

        if (validati > 0) {
            getMessageBox().setViewMode(ViewMode.plain);

            int daValidare = getForm().getElenchiVersFascicoliSelezionatiList().getTable().size();
            /* Se ho validato tutti gli elenchi mostro una INFO */
            if (validati == daValidare) {
                getMessageBox().addMessage(new Message(MessageLevel.INF,
                        "Validazione eseguita correttamente: validati " + validati + " su "
                                + getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                                        .size()));
                /* Inizializzo la lista degli elenchi di versamento selezionati */
                // getForm().getElenchiVersamentoSelezionatiList().setTable(new
                // ElvVLisElencoVersStatoTableBean());
                // getForm().getElenchiVersamentoSelezionatiList().getTable().setPageSize(10);
                // getForm().getElenchiVersamentoSelezionatiList().getTable().addSortingRule(
                // getForm().getElenchiVersamentoSelezionatiList().getDt_creazione_elenco().getName(),
                // SortingRule.ASC);
            } /* altrimenti mostro un WARNING */ else {
                getMessageBox().addMessage(new Message(MessageLevel.WAR,
                        "Non tutti gli elenchi sono stati validati correttamente: validati "
                                + validati + " su "
                                + getForm().getElenchiVersFascicoliSelezionatiList().getTable()
                                        .size()));
            }
        }

        // Eseguo la ricerca sui filtri pre-impostati
        // ricercaElenchiDaFirmare(getForm().getFiltriElenchiDaFirmare());
        ricercaElenchiVersFascDaFirmareButton();
    }

    /**
     * Signs the list of file selected
     *
     * @throws EMFError errore generico
     *
     */
    public void firmaElenchiHsmJs() throws EMFError {
        List<String> errorList = new ArrayList<>();
        JSONObject result = new JSONObject();

        // Recupero informazioni riguardo all'Utente (idSacer e credenziali HSM)
        long idUtente = SessionManager.getUser(getSession()).getIdUtente();

        getForm().getFiltriElenchiVersFascDaFirmare().post(getRequest());
        String user = getForm().getFiltriElenchiVersFascDaFirmare().getUser().parse();
        char[] passwd = getForm().getFiltriElenchiVersFascDaFirmare().getPasswd().parse() != null
                ? getForm().getFiltriElenchiVersFascDaFirmare().getPasswd().parse().toCharArray()
                : null;
        char[] otp = getForm().getFiltriElenchiVersFascDaFirmare().getOtp().parse() != null
                ? getForm().getFiltriElenchiVersFascDaFirmare().getOtp().parse().toCharArray()
                : null;

        if (StringUtils.isBlank(user)) {
            errorList.add("Il campo \"Utente\" non può essere vuoto.");
        }
        if (passwd == null || passwd.length == 0) {
            errorList.add("Il campo \"Password\" non può essere vuoto.");
        }
        if (otp == null || otp.length == 0) {
            errorList.add("Il campo \"OTP\" non può essere vuoto.");
        }

        if (elencoFascSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())) {
            getMessageBox().addError("Sessione di firma attiva");
        }

        ElvElencoVersFascTableBean elenchiDaFirmare = checkElenchiVersFascicoliToValidate();
        try {
            if (errorList.isEmpty() && !getMessageBox().hasError() && elenchiDaFirmare != null) {
                SigningRequest request = new SigningRequest(idUtente);
                HSMUser userHSM = new HSMUser(user, passwd);
                userHSM.setOTP(otp);
                request.setUserHSM(userHSM);
                request.setType(TiSessioneFirma.ELENCHI_FASC);
                for (ElvElencoVersFascRowBean elenco : elenchiDaFirmare) {
                    BigDecimal idElenco = elenco.getIdElencoVersFasc();
                    request.addFile(idElenco);
                }

                // MEV#15967 - Attivazione della firma Xades e XadesT
                Future<SigningResponse> provaAsync = null;
                it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma = amministrazioneEjb
                        .getTipoFirmaPerStruttura(getIdStrutCorrente());
                switch (tipoFirma) {
                case CADES:
                    provaAsync = firmaHsmEjb.signP7MRequest(request);
                    break;
                case XADES:
                    provaAsync = firmaHsmEjb.signXades(request);
                    break;
                }
                // VECCHIO CODICE ORIGINALE
                // Future<SigningResponse> provaAsync = firmaHsmEjb.signP7MRequest(request);
                getSession().setAttribute(Signature.FUTURE_ATTR_ELENCHI_FASC, provaAsync);
            }

            if (errorList.isEmpty() && !result.has("status")) {
                result.put("info", "Sessione di firma avviata");
            } else if (!errorList.isEmpty()) {
                result.put("error", errorList);
            }
        } catch (JSONException ex) {
            log.error(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma dei file",
                    ex);
            getMessageBox().addError("Errore inatteso nel recupero e firma dei file");
        }
        if (!getMessageBox().hasError()) {
            redirectToAjax(result);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    private ElvElencoVersFascTableBean checkElenchiVersFascicoliToValidate() {
        ElvElencoVersFascTableBean result = null;

        if (getForm().getElenchiVersFascicoliSelezionatiList().getTable().isEmpty()) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Selezionare almeno un elenco di versamento fascicoli da validare"));
        } else {
            List<String> organizNoPartitionList = new ArrayList<>();
            ElvElencoVersFascTableBean elenchiDaFirmare = new ElvElencoVersFascTableBean();
            /* Per ogni elenco di versamento fascicoli da firmare selezionato, eseguo i controlli */
            for (ElvVRicElencoFascByStatoRowBean elencoVista : (ElvVRicElencoFascByStatoTableBean) getForm()
                    .getElenchiVersFascicoliSelezionatiList().getTable()) {
                BigDecimal idElencoVersFasc = elencoVista.getIdElencoVersFasc();
                ElvElencoVersFascRowBean elencoVersFasc = evfEjb
                        .getElvElencoVersFascRowBean(idElencoVersFasc);
                elenchiDaFirmare.add(elencoVersFasc);
                /* Verifico il valore di niFascVersElenco */
                if (elencoVersFasc.getNiFascVersElenco().longValue() > 0) {
                    /*
                     * Controllo se tutti gli elenchi di versamento fascicoli con data chiusura
                     * precedente non presenti nei selezionati sono stati firmati
                     */
                    evfEjb.areAllElenchiNonPresentiFirmati(
                            (ElvVRicElencoFascByStatoTableBean) getForm()
                                    .getElenchiVersFascicoliSelezionatiList().getTable(),
                            elencoVersFasc.getDtScadChius(), elencoVersFasc.getIdStrut());
                }
            }
            // Se ho delle strutture la cui "verifica partizione" non è andata a buon fine per i
            // file degli elenchi di
            // versamento fascicoli...
            if (!organizNoPartitionList.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder(
                        "La partizione di tipo FILE_ELENCO_VERS_FASC per la data corrente e le strutture: ");
                for (String organizNoPartition : organizNoPartitionList) {
                    errorMessage.append(organizNoPartition).append("<br>");
                }
                errorMessage.append("non è definita");
                getMessageBox().addMessage(new Message(MessageLevel.ERR, errorMessage.toString()));
            }

            // Se non ci sono problemi, procedo alla firma
            if (!getMessageBox().hasError()) {
                result = elenchiDaFirmare;
            }
        }
        return result;
    }

    private ElvElencoVersFascTableBean checkElenchiIndiciAipFascToSign() {
        ElvElencoVersFascTableBean result = null;
        if (getForm().getElenchiIndiciAipFascSelezionatiList().getTable().isEmpty()) {
            getMessageBox()
                    .addError("Selezionare almeno un elenco indice AIP fascicolo da firmare");
        } else {
            List<String> organizNoPartitionList = new ArrayList<>();
            ElvElencoVersFascTableBean elenchiDaFirmare = new ElvElencoVersFascTableBean();
            /* Per ogni elenco di versamento fascicolo da firmare selezionato, eseguo i controlli */
            for (ElvVRicElencoFascByStatoRowBean elencoVista : (ElvVRicElencoFascByStatoTableBean) getForm()
                    .getElenchiIndiciAipFascSelezionatiList().getTable()) {
                BigDecimal idElencoVersFasc = elencoVista.getIdElencoVersFasc();
                ElvElencoVersFascRowBean elencoVersFasc = evfEjb
                        .getElvElencoVersFascRowBean(idElencoVersFasc);
                elenchiDaFirmare.add(elencoVersFasc);
                /* Verifico il valore di niFascVersElenco */
                if (elencoVersFasc.getNiFascVersElenco().longValue() > 0) {
                    /*
                     * Controllo se tutti gli elenchi con data chiusura precedente non presenti nei
                     * selezionati sono stati firmati
                     */
                    evfEjb.areAllElenchiNonPresentiFirmati(
                            (ElvVRicElencoFascByStatoTableBean) getForm()
                                    .getElenchiIndiciAipFascSelezionatiList().getTable(),
                            elencoVersFasc.getDtScadChius(), elencoVersFasc.getIdStrut());
                }
            }
            // Se ho delle strutture la cui "verifica partizione" non è andata a buon fine per i
            // file degli elenchi...
            if (!organizNoPartitionList.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder(
                        "La partizione di tipo FILE_ELENCHI_VERS_FASC per la data corrente e le strutture: ");
                for (String organizNoPartition : organizNoPartitionList) {
                    errorMessage.append(organizNoPartition).append("<br>");
                }
                errorMessage.append("non è definita");
                getMessageBox().addError(errorMessage.toString());
            }

            // Se non ci sono problemi, procedo alla firma
            if (!getMessageBox().hasError()) {
                result = elenchiDaFirmare;
            }
        }
        return result;
    }

    @SuppressLogging
    public void checkSignatureFuture() {
        Future<SigningResponse> futureObj = (Future<SigningResponse>) getSession()
                .getAttribute(Signature.FUTURE_ATTR_ELENCHI_FASC);
        try {
            JSONObject result = new JSONObject();
            result.put("status", "NO_SESSION");
            if (futureObj != null) {
                if (futureObj.isDone()) {
                    SigningResponse resp = futureObj.get();
                    result.put("status", resp.name());
                    switch (resp) {
                    case ACTIVE_SESSION_YET:
                    case AUTH_WRONG:
                    case OTP_WRONG:
                    case OTP_EXPIRED:
                    case USER_BLOCKED:
                    case HSM_ERROR:
                    case UNKNOWN_ERROR:
                        result.put("error", resp.getDescription());
                        break;
                    case WARNING:
                    case OK:
                        result.put("info", resp.getDescription());
                        getForm().getElenchiVersFascicoliSelezionatiList().getTable().clear();
                        break;
                    default:
                        getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_FASC);
                        throw new AssertionError(resp.name());
                    }
                    getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_FASC);
                } else {
                    result.put("status", "WORKING");
                }
            }
            redirectToAjax(result);
        } catch (ExecutionException | JSONException ex) {
            log.error(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma degli elenchi di versamento fascicoli",
                    ex);
            getMessageBox().addError(
                    "Errore inatteso nel recupero e firma degli elenchi di versamento fascicoli");
            forwardToPublisher(getLastPublisher());
        } catch (InterruptedException ex) {
            log.error("Thread interrupted while waiting for signature future", ex);
            Thread.currentThread().interrupt(); // Restore interrupted status
            getMessageBox().addError(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma degli elenchi di versamento fascicoli");
            forwardToPublisher(getLastPublisher());
        }
    }

    @SuppressLogging
    public void checkSignatureIndiciAipFascFuture() {
        Future<SigningResponse> futureObj = (Future<SigningResponse>) getSession()
                .getAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP_FASC);
        try {
            JSONObject result = new JSONObject();
            result.put("status", "NO_SESSION");
            if (futureObj != null) {
                if (futureObj.isDone()) {
                    SigningResponse resp = futureObj.get();
                    result.put("status", resp.name());
                    switch (resp) {
                    case ACTIVE_SESSION_YET:
                    case AUTH_WRONG:
                    case OTP_WRONG:
                    case OTP_EXPIRED:
                    case USER_BLOCKED:
                    case HSM_ERROR:
                    case UNKNOWN_ERROR:
                        result.put("error", resp.getDescription());
                        break;
                    case WARNING:
                    case OK:
                        result.put("info", resp.getDescription() + "!");
                        getForm().getElenchiIndiciAipFascSelezionatiList().getTable().clear();
                        break;

                    default:
                        getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP_FASC);
                        throw new AssertionError(resp.name());
                    }
                    getSession().removeAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP_FASC);
                } else {
                    result.put("status", "WORKING");
                }
            }
            redirectToAjax(result);
        } catch (ExecutionException | JSONException ex) {
            log.error(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma degli indici AIP fascicoli",
                    ex);
            getMessageBox()
                    .addError("Errore inatteso nel recupero e firma degli indici AIP fascicoli");
            forwardToPublisher(getLastPublisher());
        } catch (InterruptedException ex) {
            log.error("Thread interrupted while waiting for signature future", ex);
            Thread.currentThread().interrupt(); // Restore interrupted status
            getMessageBox().addError(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma degli indici AIP fascicoli");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void scaricaElencoIdxAipFascBtn() throws Throwable {
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliDetail()
                .getId_elenco_vers_fasc().parse();
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        String nmEnte = getForm().getElenchiVersFascicoliDetail().getNm_ente().parse();
        String nmStrut = getForm().getElenchiVersFascicoliDetail().getNm_strut().parse();
        String filesPrefix = sistema + "_" + nmEnte.replaceAll(" ", "_") + "_"
                + nmStrut.replaceAll(" ", "_") + "_ElencoIndiciAIP-FA-" + idElencoVersFasc;
        String filesSuffix = "_Indice";
        // Comincio a costruire lo zip
        String nomeZip = filesPrefix;
        getResponse().setContentType("application/zip");
        getResponse().setHeader("Content-Disposition",
                "attachment; filename=\"" + nomeZip + ".zip");
        ZipOutputStream out = new ZipOutputStream(getServletOutputStream());
        try {
            evfEjb.streamOutFileIndiceElenco(out, filesPrefix, filesSuffix,
                    idElencoVersFasc.longValue(), FileTypeEnum.getElencoIndiciFileTypes());
            out.close();
            freeze();
        } catch (Exception e) {
            log.error(ECCEZIONE_GENERICA, e);
            getMessageBox().addError("Errore nel recupero dei file degli indici AIP fascicoli");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Secure(action = "Menu.Fascicoli.ListaElenchiIndiciAipFascDaFirmare")
    public void loadListaElenchiIndiciAipFascDaFirmare() throws EMFError {
        /*
         * Controllo lo stato della history di navigazione se non ci sono pagine precedenti, vuol
         * dire che arrivo qui da un link del menu, se ci sono pagine allora devo passare alla jsp
         * l'id della struttura
         */
        boolean cleanList = false;
        if (getRequest().getParameter("cleanhistory") != null) {
            getUser().getMenu().reset();
            getUser().getMenu().select("Menu.Fascicoli.ListaElenchiIndiciAipFascDaFirmare");
            // Rimuovo l'attributo perchè arrivo da un link del menu e non da una lista
            getSession().removeAttribute("idStrutRif");
            getSession().removeAttribute("isStrutNull");
            cleanList = true;
        }

        /* Ricavo Ambiente, Ente e Struttura da visualizzare */
        BigDecimal idStrut;
        if (getRequest().getParameter("idStrut") != null) {
            idStrut = new BigDecimal(getRequest().getParameter("idStrut"));
            cleanList = true;
        } else if (getSession().getAttribute("idStrutRif") != null) {
            idStrut = (BigDecimal) getSession().getAttribute("idStrutRif");
        } else if (getSession().getAttribute("isStrutNull") != null) {
            idStrut = null;
        } else {
            idStrut = getIdStrutCorrente();
            cleanList = true;
        }

        boolean cleanFilter = true;
        if (getRequest().getParameter("cleanFilter") != null) {
            cleanFilter = false;
        }

        if (idStrut != null && cleanFilter) {
            OrgStrutRowBean strut = evfEjb.getOrgStrutRowBeanWithAmbienteEnte(idStrut);
            /* Inizializza le combo dei filtri ambiente/ente/struttura */
            initFiltriElenchiIndiciAipFascDaFirmare(strut.getIdStrut());

            if (cleanList) {
                if (getForm().getFiltriElenchiIndiciAipFascDaFirmare().validate(getMessageBox())) {
                    if (!getMessageBox().hasError()) {
                        FascicoliValidator fascicoliValidator = new FascicoliValidator(
                                getMessageBox());
                        // Valido i filtri data creazione elenco indici aip fascicoli da - a
                        // restituendo le date
                        // comprensive di orario
                        Date[] dateCreazioneElencoIdxAipFascValidate = fascicoliValidator
                                .validaDate(
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getTs_creazione_elenco_idx_aip_da().parse(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getOre_ts_creazione_elenco_idx_aip_da().parse(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getMinuti_ts_creazione_elenco_idx_aip_da().parse(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getTs_creazione_elenco_idx_aip_a().parse(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getOre_ts_creazione_elenco_idx_aip_a().parse(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getMinuti_ts_creazione_elenco_idx_aip_a().parse(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getTs_creazione_elenco_idx_aip_da()
                                                .getHtmlDescription(),
                                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                                .getTs_creazione_elenco_idx_aip_a()
                                                .getHtmlDescription());
                        if (!getMessageBox().hasError()) {
                            /*
                             * Carico la lista degli elenchi di versamento fascicoli da firmare:
                             * quelli della struttura dell'utente e con stato
                             * ELENCO_INDICI_AIP_CREATO
                             */
                            ElvVRicElencoFascByStatoTableBean elenchiTableBean = evfEjb
                                    .getElenchiVersFascicoliDaFirmareTableBean(
                                            strut.getBigDecimal("id_ambiente"), strut.getIdEnte(),
                                            strut.getIdStrut(), null, null,
                                            ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO,
                                            dateCreazioneElencoIdxAipFascValidate,
                                            getUser().getIdUtente());
                            getForm().getElenchiIndiciAipFascDaFirmareList()
                                    .setTable(elenchiTableBean);
                            getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                                    .setPageSize(10);
                            getForm().getElenchiIndiciAipFascDaFirmareList().getTable().first();
                            getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                                    .addSortingRule(
                                            getForm().getElenchiIndiciAipFascDaFirmareList()
                                                    .getDt_creazione_elenco_ix_aip().getName(),
                                            SortingRule.ASC);

                            /*
                             * Inizializzo la lista degli elenchi di versamento fascicoli
                             * selezionati
                             */
                            getForm().getElenchiIndiciAipFascSelezionatiList()
                                    .setTable(new ElvVRicElencoFascByStatoTableBean());
                            getForm().getElenchiIndiciAipFascSelezionatiList().getTable()
                                    .setPageSize(10);
                            getForm().getElenchiIndiciAipFascSelezionatiList().getTable()
                                    .addSortingRule(
                                            getForm().getElenchiIndiciAipFascSelezionatiList()
                                                    .getDt_creazione_elenco_ix_aip().getName(),
                                            SortingRule.ASC);
                        }
                    }
                }
            }
        }

        getForm().getElenchiIdxAipFascDaFirmareBtnList().setEditMode();

        // Check if some signature session is active
        Future<Boolean> futureFirma = (Future<Boolean>) getSession()
                .getAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP_FASC);
        // Verifico su db la presenza della sessione di firma o di un oggetto future (di una
        // possibile sessione di firma
        // preesistente) in sessione
        if (elencoIndiciAipFascSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())
                || futureFirma != null) {
            // Se esistono delle sessioni bloccate per quell'utente le sblocco
            if (elencoIndiciAipFascSignSessionEjb.hasUserBlockedSessions(getUser().getIdUtente())) {
                // Sessione di firma bloccata
                elencoIndiciAipFascSignSessionEjb.unlockBlockedSessions(getUser().getIdUtente());

                getForm().getElenchiIdxAipFascDaFirmareBtnList().getFirmaElenchiIndiciAipFascHsm()
                        .setReadonly(false);
                getMessageBox().addInfo("\u00C8 stata sbloccata una sessione di firma bloccata");
                getMessageBox().setViewMode(ViewMode.plain);
            } else {
                getForm().getElenchiIdxAipFascDaFirmareBtnList().getFirmaElenchiIndiciAipFascHsm()
                        .setReadonly(true);
                // Sessione di firma attiva
                getMessageBox().addInfo("Sessione di firma attiva");
                getMessageBox().setViewMode(ViewMode.plain);
            }
        } else {
            getForm().getElenchiIdxAipFascDaFirmareBtnList().getFirmaElenchiIndiciAipFascHsm()
                    .setReadonly(false);
        }
        getForm().getElenchiIdxAipFascDaFirmareBtnList().getFirmaElenchiIndiciAipFascHsm()
                .setHidden(false);

        getSession().setAttribute("idStrutRif", idStrut);
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }

    @Override
    public JSONObject triggerFiltriElenchiIndiciAipFascDaFirmareId_ambienteOnTrigger()
            throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriElenchiIndiciAipFascDaFirmare(),
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP);
        return getForm().getFiltriElenchiIndiciAipFascDaFirmare().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiIndiciAipFascDaFirmareId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriElenchiIndiciAipFascDaFirmare(),
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC_INDICI_AIP);
        return getForm().getFiltriElenchiIndiciAipFascDaFirmare().asJSON();
    }

    @Override
    public void ricercaElenchiIdxAipFascDaFirmare() throws Throwable {
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_ambiente()
                .parse();
        BigDecimal idEnte = getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_strut()
                .parse();
        BigDecimal idElencoVersFasc = getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                .getId_elenco_vers_fasc().parse();

        getSession().setAttribute("idStrutRif", idStrut);
        if (idStrut == null) {
            // Rimuovo l'attributo idStrutRif se presente in sessione vuol dire che si riferisce ad
            // una struttura
            // selezionata precedentemente
            getSession().removeAttribute("idStrutRif");
            // Traccio in sessione un attributo specifico
            getSession().setAttribute("isStrutNull", true);
        }

        if (getForm().getFiltriElenchiIndiciAipFascDaFirmare().validate(getMessageBox())) {
            if (!getMessageBox().hasError()) {
                FascicoliValidator fascicoliValidator = new FascicoliValidator(getMessageBox());
                // Valido i filtri data creazione elenco indici aip fascicoli da - a restituendo le
                // date comprensive di
                // orario
                Date[] dateCreazioneElencoIdxAipFascValidate = fascicoliValidator.validaDate(
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getTs_creazione_elenco_idx_aip_da().parse(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getOre_ts_creazione_elenco_idx_aip_da().parse(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getMinuti_ts_creazione_elenco_idx_aip_da().parse(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getTs_creazione_elenco_idx_aip_a().parse(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getOre_ts_creazione_elenco_idx_aip_a().parse(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getMinuti_ts_creazione_elenco_idx_aip_a().parse(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getTs_creazione_elenco_idx_aip_da().getHtmlDescription(),
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare()
                                .getTs_creazione_elenco_idx_aip_a().getHtmlDescription());
                if (!getMessageBox().hasError()) {
                    /*
                     * Carico la lista degli elenchi di versamento fascicoli da firmare: quelli
                     * della struttura dell'utente e con stato ELENCO_INDICI_AIP_CREATO
                     */
                    ElvVRicElencoFascByStatoTableBean elenchiTableBean = evfEjb
                            .getElenchiVersFascicoliDaFirmareTableBean(idAmbiente, idEnte, idStrut,
                                    idElencoVersFasc, null,
                                    ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO,
                                    dateCreazioneElencoIdxAipFascValidate, getUser().getIdUtente());
                    getForm().getElenchiIndiciAipFascDaFirmareList().setTable(elenchiTableBean);
                    getForm().getElenchiIndiciAipFascDaFirmareList().getTable().setPageSize(10);
                    getForm().getElenchiIndiciAipFascDaFirmareList().getTable().first();
                    getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                            .addSortingRule(
                                    getForm().getElenchiIndiciAipFascDaFirmareList()
                                            .getDt_creazione_elenco_ix_aip().getName(),
                                    SortingRule.ASC);

                    /* Inizializzo la lista degli elenchi di versamento fascicoli selezionati */
                    getForm().getElenchiIndiciAipFascSelezionatiList()
                            .setTable(new ElvVRicElencoFascByStatoTableBean());
                    getForm().getElenchiIndiciAipFascSelezionatiList().getTable().setPageSize(10);
                    getForm().getElenchiIndiciAipFascSelezionatiList().getTable()
                            .addSortingRule(
                                    getForm().getElenchiIndiciAipFascSelezionatiList()
                                            .getDt_creazione_elenco_ix_aip().getName(),
                                    SortingRule.ASC);
                }
            }
        }

        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }

    @Override
    public void selectAllElenchiIndiciAipFasc() throws Throwable {
        ElvVRicElencoFascByStatoTableBean elenchi = (ElvVRicElencoFascByStatoTableBean) getForm()
                .getElenchiIndiciAipFascDaFirmareList().getTable();
        for (ElvVRicElencoFascByStatoRowBean elenco : elenchi) {
            getForm().getElenchiIndiciAipFascSelezionatiList().getTable().add(elenco);
        }
        elenchi.removeAll();
        getForm().getElenchiIndiciAipFascSelSection().setLoadOpened(true);
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }

    @Override
    public void deselectAllElenchiIndiciAipFasc() throws Throwable {
        ElvVRicElencoFascByStatoTableBean elenchi = (ElvVRicElencoFascByStatoTableBean) getForm()
                .getElenchiIndiciAipFascSelezionatiList().getTable();
        for (ElvVRicElencoFascByStatoRowBean elenco : elenchi) {
            getForm().getElenchiIndiciAipFascDaFirmareList().getTable().add(elenco);
        }
        elenchi.removeAll();
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }

    @Override
    public void selectHundredElenchiIndiciAipFasc() throws Throwable {
        ElvVRicElencoFascByStatoTableBean elenchi = (ElvVRicElencoFascByStatoTableBean) getForm()
                .getElenchiIndiciAipFascDaFirmareList().getTable();
        if (elenchi != null) {
            if (elenchi.size() <= 100) {
                selectAllElenchiIndiciAipFasc();
            } else {
                for (int counter = 0; counter < 100; counter++) {
                    if (!elenchi.isEmpty()) {
                        ElvVRicElencoFascByStatoRowBean elenco = elenchi.getRow(0);
                        getForm().getElenchiIndiciAipFascSelezionatiList().getTable().add(elenco);
                        elenchi.remove(0);
                    } else {
                        break;
                    }
                }
                getForm().getElenchiIndiciAipFascSelSection().setLoadOpened(true);
                forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
            }
        }
    }

    @Override
    public void firmaElenchiIndiciAipFascHsm() throws Throwable {
        ElvElencoVersFascTableBean elenchiDaFirmare = checkElenchiIndiciAipFascToSign();
        int elenchiHsmEliminati = 0;

        if (!getMessageBox().hasError() && elenchiDaFirmare != null) {
            // Ricavo l'id ambiente da un qualsiasi record degli elenchi da firmare
            // PS: non lo prendo dal filtro di ricerca perchè l'utente potrebbe cambiarlo dalla
            // combo senza fare la
            // ricerca
            // e così verrebbe preso un ambiente errato
            BigDecimal idStrut = elenchiDaFirmare.getRow(0).getIdStrut();
            OrgAmbienteRowBean ambienteRowBean = struttureEjb
                    .getOrgAmbienteRowBeanByIdStrut(idStrut);
            BigDecimal idAmbiente = ambienteRowBean.getIdAmbiente();
            if (idAmbiente != null) {
                // Ricavo il parametro HSM_USERNAME (parametro multiplo dell'ambiente) associato
                // all'utente corrente
                String hsmUserName = amministrazioneEjb.getHsmUsername(getUser().getIdUtente(),
                        idAmbiente);
                if (hsmUserName != null) {

                    List<BigDecimal> idElencoVersFascRigheTotali = new ArrayList<>();
                    List<BigDecimal> idElencoVersFascRigheCancellate = new ArrayList<>();

                    for (int i = 0; i < elenchiDaFirmare.size(); i++) {
                        ElvElencoVersFascRowBean elenco = elenchiDaFirmare.getRow(i);
                        BigDecimal idElencoVersFasc = elenco.getIdElencoVersFasc();
                        idElencoVersFascRigheTotali.add(idElencoVersFasc);
                        if (evfEjb.soloFascAnnul(idElencoVersFasc)) {
                            evfEjb.manageElencoFascAnnulDaFirmaElencoIndiciAipFasc(idElencoVersFasc,
                                    getUser().getIdUtente());
                            idElencoVersFascRigheCancellate.add(idElencoVersFasc);
                            elenchiHsmEliminati++;
                        }
                    }

                    // Elimino a video gli elenchi indici AIP fascicoli cancellati su DB in quanto
                    // contenenti solo
                    // fascicoli annullati
                    idElencoVersFascRigheTotali.removeAll(idElencoVersFascRigheCancellate);
                    ElvVRicElencoFascByStatoTableBean elenchiRimanenti = evfEjb
                            .getElenchiVersFascicoliDaFirmareTableBean(idElencoVersFascRigheTotali,
                                    getUser().getIdUtente());
                    getForm().getElenchiIndiciAipFascSelezionatiList().setTable(elenchiRimanenti);

                    if (elenchiHsmEliminati > 0) {
                        getMessageBox().setViewMode(ViewMode.plain);
                        getMessageBox().addInfo("Sono stati eliminati " + elenchiHsmEliminati
                                + " elenchi di versamento fascicoli in quanto contenenti solo fascicoli annullati");
                    }

                    if (getForm().getElenchiIndiciAipFascSelezionatiList().getTable().size() > 0) {
                        /* Richiedo le credenziali del HSM utilizzando apposito popup */
                        getRequest().setAttribute("customElenchiVersFascicoliSelect", true);
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getUser()
                                .setValue(hsmUserName);
                        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getUser().setViewMode();
                    }
                } else {
                    getMessageBox()
                            .addError("Utente non rientra tra i firmatari definiti sull’ambiente");
                }
            }
        }
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }
    // TODO: Questo metodo può essere unificato a initFiltriElenchiDaFirmare, bisogna aspettare però
    // il rilascio del
    // branch hsm per non fare caos

    private void initFiltriElenchiIndiciAipFascDaFirmare(BigDecimal idStruttura) {
        // Azzero i filtri
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().reset();
        // Ricavo id struttura, ente ed ambiente attuali
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
            tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                    idEnte, Boolean.TRUE);

        } catch (Exception ex) {
            log.error(ERRORE_RICERCA_AMBIENTE, ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_ambiente()
                .setDecodeMap(mappaAmbiente);
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_ambiente()
                .setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().getId_strut()
                .setValue(idStruttura.toString());

        // Imposto i filtri in editMode
        getForm().getFiltriElenchiIndiciAipFascDaFirmare().setEditMode();
    }

    /**
     * Bottone "+" della "Lista elenchi indici AIP fascicoli da firmare" per spostare un elenco da
     * questa lista a quella degli elenchi selezionati pronti per essere firmati
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiIndiciAipFascDaFirmareList() throws EMFError {
        /* Ricavo il record interessato della "Lista elenchi indici AIP fascicoli da firmare" */
        ElvVRicElencoFascByStatoRowBean row = (ElvVRicElencoFascByStatoRowBean) getForm()
                .getElenchiIndiciAipFascDaFirmareList().getTable().getCurrentRow();
        int index = getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                .getCurrentRowIndex();
        /* Lo tolgo dalla lista elenchi indici AIP fascicoli da firmare */
        getForm().getElenchiIndiciAipFascDaFirmareList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                .getCurrentPageIndex();
        int inizio = getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                .getFirstRowPageIndex();
        this.lazyLoadGoPage(getForm().getElenchiIndiciAipFascDaFirmareList(), paginaCorrente);
        getForm().getElenchiIndiciAipFascDaFirmareList().getTable().setCurrentRowIndex(inizio);
        /* Aggiungo il record nella lista degli elenchi indici AIP fascicoli selezionati */
        getForm().getElenchiIndiciAipFascSelSection().setLoadOpened(true);
        getForm().getElenchiIndiciAipFascSelezionatiList().add(row);
        getForm().getElenchiIndiciAipFascSelezionatiList().getTable().addSortingRule(getForm()
                .getElenchiIndiciAipFascSelezionatiList().getDt_creazione_elenco_ix_aip().getName(),
                SortingRule.ASC);
        getForm().getElenchiIndiciAipFascSelezionatiList().getTable().sort();
        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }

    /**
     * Bottone "-" della "Lista elenchi indici AIP fascicoli selezionati" per spostare un elenco da
     * questa lista a quella degli elenchi indici AIP fascicoli da firmare
     *
     * @throws EMFError errore generico
     */
    @Override
    public void selectElenchiIndiciAipFascSelezionatiList() throws EMFError {
        /* Ricavo il record interessato della "Lista elenchi indici AIP fascicoli selezionati" */
        ElvVRicElencoFascByStatoRowBean row = (ElvVRicElencoFascByStatoRowBean) getForm()
                .getElenchiIndiciAipFascSelezionatiList().getTable().getCurrentRow();
        int index = getForm().getElenchiIndiciAipFascSelezionatiList().getTable()
                .getCurrentRowIndex();
        /* Lo tolgo dalla lista elenchi indici AIP fascicoli selezionati */
        getForm().getElenchiIndiciAipFascSelezionatiList().getTable().remove(index);
        /* "Refresho" la lista senza il record */
        int paginaCorrente = getForm().getElenchiIndiciAipFascSelezionatiList().getTable()
                .getCurrentPageIndex();
        int inizio = getForm().getElenchiIndiciAipFascSelezionatiList().getTable()
                .getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getElenchiIndiciAipFascSelezionatiList(), paginaCorrente);
        // Ritorno alla pagina
        getForm().getElenchiIndiciAipFascSelezionatiList().getTable().setCurrentRowIndex(inizio);
        // Pagina Elenchi da firmare
        getForm().getElenchiIndiciAipFascDaFirmareList().add(row);
        int paginaCorrenteVF = getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                .getCurrentPageIndex();
        int inizioVF = getForm().getElenchiIndiciAipFascDaFirmareList().getTable()
                .getFirstRowPageIndex();
        // Rieseguo la query se necessario
        this.lazyLoadGoPage(getForm().getElenchiIndiciAipFascDaFirmareList(), paginaCorrenteVF);
        // Ritorno alla pagina
        getForm().getElenchiIndiciAipFascDaFirmareList().getTable().setCurrentRowIndex(inizioVF);

        forwardToPublisher(Application.Publisher.LISTA_ELENCHI_INDICI_AIP_FASC_SELECT);
    }

    public void firmaElenchiIndiciAipFascHsmJs() throws EMFError {
        List<String> errorList = new ArrayList<>();
        JSONObject result = new JSONObject();

        // Recupero informazioni riguardo all'Utente (idSacer e credenziali HSM)
        long idUtente = SessionManager.getUser(getSession()).getIdUtente();

        getForm().getFiltriElenchiIndiciAipFascDaFirmare().post(getRequest());
        String user = getForm().getFiltriElenchiIndiciAipFascDaFirmare().getUser().parse();
        char[] passwd = getForm().getFiltriElenchiIndiciAipFascDaFirmare().getPasswd()
                .parse() != null
                        ? getForm().getFiltriElenchiIndiciAipFascDaFirmare().getPasswd().parse()
                                .toCharArray()
                        : null;
        char[] otp = getForm().getFiltriElenchiIndiciAipFascDaFirmare().getOtp().parse() != null
                ? getForm().getFiltriElenchiIndiciAipFascDaFirmare().getOtp().parse().toCharArray()
                : null;

        if (StringUtils.isBlank(user)) {
            errorList.add("Il campo \"Utente\" non può essere vuoto.");
        }
        if (passwd == null || passwd.length == 0) {
            errorList.add("Il campo \"Password\" non può essere vuoto.");
        }
        if (otp == null || otp.length == 0) {
            errorList.add("Il campo \"OTP\" non può essere vuoto.");
        }

        if (elencoIndiciAipFascSignSessionEjb.hasUserActiveSessions(getUser().getIdUtente())) {
            getMessageBox().addError("Sessione di firma attiva");
        }

        ElvElencoVersFascTableBean elenchiDaFirmare = checkElenchiIndiciAipFascToSign();
        try {
            if (errorList.isEmpty() && !getMessageBox().hasError() && elenchiDaFirmare != null) {
                SigningRequest request = new SigningRequest(idUtente);
                HSMUser userHSM = new HSMUser(user, passwd);
                userHSM.setOTP(otp);
                request.setUserHSM(userHSM);
                request.setType(TiSessioneFirma.ELENCHI_INDICI_AIP_FASC);
                for (ElvElencoVersFascRowBean elenco : elenchiDaFirmare) {
                    BigDecimal idElenco = elenco.getIdElencoVersFasc();
                    request.addFile(idElenco);
                }
                // MEV#15967 - Attivazione della firma Xades e XadesT
                Future<SigningResponse> provaAsync = null;
                it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma = amministrazioneEjb
                        .getTipoFirmaPerStruttura(getIdStrutCorrente());
                switch (tipoFirma) {
                case CADES:
                    provaAsync = firmaHsmEjb.signP7MRequest(request);
                    break;
                case XADES:
                    provaAsync = firmaHsmEjb.signXades(request);
                    break;
                }
                // VECCHIO CODICE ORIGINALE
                // Future<SigningResponse> provaAsync = firmaHsmEjb.signP7MRequest(request);
                getSession().setAttribute(Signature.FUTURE_ATTR_ELENCHI_INDICI_AIP_FASC,
                        provaAsync);
            }

            if (errorList.isEmpty() && !result.has("status")) {
                result.put("info", "Sessione di firma avviata");
            } else if (!errorList.isEmpty()) {
                result.put("error", errorList);
            }
        } catch (JSONException ex) {
            log.error(
                    "Errore inatteso nella gestione del metodo asincrono per il recupero e la firma dei file",
                    ex);
            getMessageBox().addError("Errore inatteso nel recupero e firma dei file");
        }
        if (!getMessageBox().hasError()) {
            redirectToAjax(result);
        } else {
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public JSONObject triggerFiltriElenchiVersFascicoliId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriElenchiVersFascicoli(),
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC);
        return getForm().getFiltriElenchiVersFascicoli().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiVersFascicoliId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriElenchiVersFascicoli(),
                ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC);
        return getForm().getFiltriElenchiVersFascicoli().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiVersFascicoliId_strutOnTrigger() throws EMFError {
        getForm().getFiltriElenchiVersFascicoli().post(getRequest());
        if (getForm().getFiltriElenchiVersFascicoli().getId_strut().parse() != null) {
            checkUniqueStrutInCombo(getForm().getFiltriElenchiVersFascicoli().getId_strut().parse(),
                    ActionEnums.SezioneElenchiVersFascicoli.RICERCA_ELENCHI_VERS_FASC);
        } else {
            getForm().getFiltriElenchiVersFascicoli().getId_tipo_fascicolo()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriElenchiVersFascicoli().asJSON();
    }

    @Override
    public void confermaRimozioneFascButton() throws Throwable {
        /* Ottengo l'idElencoVersFasc */
        BigDecimal idElencoVersFasc = getForm().getElenchiVersFascicoliDetail()
                .getId_elenco_vers_fasc().parse();
        Set<BigDecimal> idFascToRemove = (Set<BigDecimal>) getSession()
                .getAttribute("idFascToRemove");
        try {
            evfEjb.deleteFascFromElencoVersFascicoli(idElencoVersFasc.longValue(), idFascToRemove,
                    getIdUtenteCorrente());

            if (evfEjb.existIdElenco(
                    getForm().getElenchiVersFascicoliDetail().getId_elenco_vers_fasc().parse(),
                    getIdStrutCorrente())) {
                getMessageBox().addInfo("Fascicoli eliminati con successo!");
                ricercaFasc();
            } else {
                getMessageBox().addInfo("Fascicoli eliminati con successo! "
                        + "In quanto rimasto privo di fascicoli, l'elenco di versamento fascicoli "
                        + idElencoVersFasc + " è stato eliminato!");
                setTableName(getForm().getElenchiVersFascicoliList().getName());
                goBackTo(Application.Publisher.ELENCHI_VERS_FASCICOLI_RICERCA);
            }

            getSession().removeAttribute("idFascToRemove");
            getMessageBox().setViewMode(ViewMode.plain);
        } catch (Exception e) {
            getMessageBox().addError("Errore durante l'eliminazione dei fascicoli");
            getMessageBox().setViewMode(ViewMode.plain);
            forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
        }
    }

    @Override
    public void annullaRimozioneFascButton() throws Throwable {
        // Nascondo i bottoni con javascript disattivato
        getForm().getFascDaRimuovereCustomMessageButtonList().setViewMode();
        getSession().removeAttribute("idFascToRemove");
        forwardToPublisher(Application.Publisher.ELENCHI_VERS_FASCICOLI_DETAIL);
    }

    /**
     * Metodo richiamato dal link per accedere alla pagina di dettaglio Criterio di Raggruppamento
     * Fascicoli
     *
     * @throws EMFError errore generico
     */
    public void loadDettaglioCritRaggrFasc() throws EMFError {
        BigDecimal idStrut = getForm().getFiltriElenchiVersFascicoli().getId_strut().parse();
        String riga = getRequest().getParameter("riga");
        BigDecimal numberRiga = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(riga)) {
            numberRiga = new BigDecimal(riga);
        }
        // Recupero il criterio di raggruppamento fascicoli
        BigDecimal idCriterioRaggrFasc = ((BaseTableInterface) getForm()
                .getElenchiVersFascicoliList().getTable()).getRow(numberRiga.intValue())
                .getBigDecimal("id_criterio_raggr_fasc");
        DecCriterioRaggrFascRowBean critRaggrFascRow = fascicoliEjb
                .getDecCriterioRaggrFascRowBean(idCriterioRaggrFasc, idStrut);
        // Setto la tabella dei criteri di raggruppamento fascicoli aggiungendo solo quella
        // recuperata
        DecCriterioRaggrFascTableBean criterioRaggrFascTable = new DecCriterioRaggrFascTableBean();
        criterioRaggrFascTable.add(critRaggrFascRow);
        criterioRaggrFascTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        setNavigationEvent(NE_DETTAGLIO_VIEW);

        CriteriRaggrFascicoliForm form = new CriteriRaggrFascicoliForm();
        redirectToPage(Application.Actions.CRITERI_RAGGR_FASCICOLI, form,
                form.getCriterioRaggrFascicoliList().getName(), criterioRaggrFascTable,
                getNavigationEvent());
    }

    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
            BaseTableInterface<?> table, String event) {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate))
                .setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event
                + "&table=" + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }

    @Override
    public void loadCritRaggrFasc() throws EMFError {
        BigDecimal idStrut = getForm().getElenchiVersFascicoliDetail().getId_strut().parse();

        // Recupero il criterio di raggruppamento fascicoli
        BigDecimal idCriterioRaggrFasc = getForm().getElenchiVersFascicoliDetail()
                .getId_criterio_raggr_fasc().parse();
        DecCriterioRaggrFascRowBean critRaggrFascRow = fascicoliEjb
                .getDecCriterioRaggrFascRowBean(idCriterioRaggrFasc, idStrut);
        // Setto la tabella dei criteri di raggruppamento fascicoli aggiungendo solo quella
        // recuperata
        DecCriterioRaggrFascTableBean criterioRaggrFascTable = new DecCriterioRaggrFascTableBean();
        criterioRaggrFascTable.add(critRaggrFascRow);
        criterioRaggrFascTable.setPageSize(WebConstants.DEFAULT_PAGE_SIZE);

        setNavigationEvent(NE_DETTAGLIO_VIEW);

        CriteriRaggrFascicoliForm form = new CriteriRaggrFascicoliForm();
        setTableName(form.getCriterioRaggrFascicoliList().getName());

        redirectToPage(Application.Actions.CRITERI_RAGGR_FASCICOLI, form,
                form.getCriterioRaggrFascicoliList().getName(), criterioRaggrFascTable,
                getNavigationEvent());
    }

}
