package it.eng.parer.web.action;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.MonitoraggioIndiceAIPAbstractAction;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.OrgAmbienteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutTableBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUnitaDocIamRowBean;
import it.eng.parer.slite.gen.viewbean.AroVVisUnitaDocIamTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersTableBean;
import it.eng.parer.web.ejb.MonitoraggioIndiceAIPEjb;
import it.eng.parer.web.util.ComboGetter;
import it.eng.parer.web.util.WebConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;
import javax.ejb.EJB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioIndiceAIPAction extends MonitoraggioIndiceAIPAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(MonitoraggioIndiceAIPAbstractAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioIndiceAIPEjb")
    private MonitoraggioIndiceAIPEjb monitIndiceAIPEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Secure(action = "Menu.Monitoraggio.RiepilogoProcessoGenerazioneIndiceAIP")
    public void loadRiepilogoProcessoGenerazioneIndiceAIP() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.RiepilogoProcessoGenerazioneIndiceAIP");

        // Resetto tutti i campi di ricerca
        getForm().getFiltriMonitoraggioIndiceAIP().reset();

        /* Inizializzo il campo di ricerca ambiente, l'unico obbligatorio */
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            logger.error("Errore nel recupero ambiente", ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().setDecodeMap(mappaAmbiente);
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente()
                    .setValue("" + tmpTableBeanAmbiente.getRow(0).getIdAmbiente());
        }

        getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().setDecodeMap(new DecodeMap());
        getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());
        getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().setValue("");

        // Imposto i filtri in editMode
        getForm().getFiltriMonitoraggioIndiceAIP().setEditMode();

        // Calcolo subito la lista senza filtri
        calcolaRiepilogoProcessoGenerazioneIndiceAIP(null, null, null, null);

        // Eseguo forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_RIEPILOGO_GENERAZIONE_INDICE_AIP);
    }

    private void calcolaRiepilogoProcessoGenerazioneIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, BigDecimal niGgStato) {
        BaseTable tabella = monitIndiceAIPEjb.calcolaRiepilogoProcessoGenerazioneIndiceAIP(idAmbiente, idEnte, idStrut,
                niGgStato);
        getForm().getMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getMonitoraggioIndiceAIPList().getTable().first();
        getForm().getMonitoraggioIndiceAIPList().getTable().addSortingRule(
                getForm().getMonitoraggioIndiceAIPList().getCd_ti_eve_stato_elenco_vers().getName(), SortingRule.ASC);
    }

    @Override
    public JSONObject triggerFiltriMonitoraggioIndiceAIPId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriMonitoraggioIndiceAIP().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();

        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().setValue("");
        } else {
            getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().setValue("");
        }
        return getForm().getFiltriMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public JSONObject triggerFiltriMonitoraggioIndiceAIPId_enteOnTrigger() throws EMFError {
        getForm().getFiltriMonitoraggioIndiceAIP().post(getRequest());

        BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().setValue("");
        } else {
            getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().setValue("");
        }
        return getForm().getFiltriMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public void generaMonitoraggioIndiceAIPButton() throws EMFError {
        getForm().getFiltriMonitoraggioIndiceAIP().postAndValidate(getRequest(), getMessageBox());
        if (!getMessageBox().hasError()) {
            BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
            BigDecimal idStrut = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().parse();
            BigDecimal niGgStato = getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().parse();
            // Calcolo subito la lista senza filtri
            calcolaRiepilogoProcessoGenerazioneIndiceAIP(idAmbiente, idEnte, idStrut, niGgStato);
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_RIEPILOGO_GENERAZIONE_INDICE_AIP);
    }

    public void linkLoadListaStrutture() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().parse();
        BigDecimal niGgStato = getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal numElenchiTotali = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("ni_elenchi_total");
        String cdTiEveStatoElencoVers = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getString("cd_ti_eve_stato_elenco_vers");

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiTotali.longValue() != 0 && idStrut == null) {
            loadListaStrutture(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers);
            // Setto i parametri in quanto si tratta di un Link
            setNavigationEvent(NE_DETTAGLIO_VIEW);
            setTableName(getForm().getStrutMonitoraggioIndiceAIPList().getName());
            setRiga("" + riga);

            forwardToPublisher(Application.Publisher.MONITORAGGIO_STRUTTURE_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile cliccare sullo stato in quanto il numero di elenchi è pari a 0 oppure è stato inserito il filtro struttura");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void loadListaStrutture(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal niGgStato,
            String cdTiEveStatoElencoVers) throws EMFError {
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().reset();
        // Creo la combo degli stati elenco e setto il valore
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setDecodeMap(DecodeMap.Factory.newInstance(monitIndiceAIPEjb.getStatiElenco(),
                        "cd_ti_eve_stato_elenco_vers", "cd_ti_eve_stato_elenco_vers"));
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setValue(cdTiEveStatoElencoVers);
        // Popolo i filtri sulla base di quelli inseriti nella pagina precedente
        ComboBox ambienteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        ComboBox enteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente();
        DecodeMapIF mappaEnte = enteCombo.getDecodeMap();

        getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte);

        getForm().getFiltriStruttureMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setValue(cdTiEveStatoElencoVers);
        if (niGgStato != null) {
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getNi_gg_stato().setValue("" + niGgStato);
        }
        if (idAmbiente != null) {
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().setValue("" + idAmbiente);
        }
        if (idEnte != null) {
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().setValue("" + idEnte);
        }

        BaseTable tabella = monitIndiceAIPEjb.calcolaTotaliListaStruttureIndiceAIP(idAmbiente, idEnte, niGgStato,
                cdTiEveStatoElencoVers);
        getForm().getStrutMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getStrutMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getStrutMonitoraggioIndiceAIPList().getTable().first();
        getForm().getStrutMonitoraggioIndiceAIPList().getTable()
                .addSortingRule(getForm().getStrutMonitoraggioIndiceAIPList().getNm_strut().getName(), SortingRule.ASC);
    }

    public void linkLoadListaElenchiFiscali() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().parse();
        BigDecimal niGgStato = getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal numElenchiFiscali = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("ni_elenchi_fisc");
        String cdTiEveStatoElencoVers = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getString("cd_ti_eve_stato_elenco_vers");

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiFiscali.longValue() != 0) {
            loadListaElenchi(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers, "1");
            forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista degli elenchi in quanto il numero di elenchi fiscali è pari a 0");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaElenchiNoFiscali() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().parse();
        BigDecimal niGgStato = getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal numElenchiNoFiscali = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("ni_elenchi_no_fisc");
        String cdTiEveStatoElencoVers = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getString("cd_ti_eve_stato_elenco_vers");

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiNoFiscali.longValue() != 0) {
            loadListaElenchi(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers, "0");
            forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista degli elenchi in quanto il numero di elenchi non fiscali è pari a 0");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaElenchiTotali() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().parse();
        BigDecimal niGgStato = getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal numElenchiTotali = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("ni_elenchi_total");
        String cdTiEveStatoElencoVers = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getString("cd_ti_eve_stato_elenco_vers");
        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiTotali.longValue() != 0) {
            loadListaElenchi(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers, null);
            forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista degli elenchi in quanto il numero di elenchi totali è pari a 0");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaElenchiFiscaliDaStrut() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().parse();
        BigDecimal niGgStato = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        String cdTiEveStatoElencoVers = getForm().getFiltriStruttureMonitoraggioIndiceAIP()
                .getCd_ti_eve_stato_elenco_vers().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal idStrut = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_strut");
        BigDecimal numElenchiFiscali = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable())
                .getRow(riga).getBigDecimal("ni_elenchi_fisc");

        // Se ambiente e/o ente non sono valorizzati in quanto i filtri della pagina precedente non lo erano,
        // li ricavo dal ricord in corrispondenza del link
        if (idAmbiente == null) {
            idAmbiente = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                    .getBigDecimal("id_ambiente");
        }
        if (idEnte == null) {
            idEnte = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                    .getBigDecimal("id_ente");
        }

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiFiscali.longValue() != 0) {
            loadListaElenchi(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers, "1");
            forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista degli elenchi in quanto il numero di elenchi fiscali è pari a 0");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaElenchiNoFiscaliDaStrut() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().parse();
        String cdTiEveStatoElencoVers = getForm().getFiltriStruttureMonitoraggioIndiceAIP()
                .getCd_ti_eve_stato_elenco_vers().parse();
        BigDecimal niGgStato = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal numElenchiNoFiscali = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable())
                .getRow(riga).getBigDecimal("ni_elenchi_no_fisc");
        BigDecimal idStrut = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_strut");

        // Se ambiente e/o ente non sono valorizzati in quanto i filtri della pagina precedente non lo erano,
        // li ricavo dal ricord in corrispondenza del link
        if (idAmbiente == null) {
            idAmbiente = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                    .getBigDecimal("id_ambiente");
        }
        if (idEnte == null) {
            idEnte = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                    .getBigDecimal("id_ente");
        }

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiNoFiscali.longValue() != 0) {
            loadListaElenchi(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers, "0");
            forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista degli elenchi in quanto il numero di elenchi non fiscali è pari a 0");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaElenchiTotaliDaStrut() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().parse();
        String cdTiEveStatoElencoVers = getForm().getFiltriStruttureMonitoraggioIndiceAIP()
                .getCd_ti_eve_stato_elenco_vers().parse();
        BigDecimal niGgStato = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal numElenchiTotali = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable())
                .getRow(riga).getBigDecimal("ni_elenchi_total");
        BigDecimal idStrut = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_strut");

        // Se ambiente e/o ente non sono valorizzati in quanto i filtri della pagina precedente non lo erano,
        // li ricavo dal ricord in corrispondenza del link
        if (idAmbiente == null) {
            idAmbiente = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                    .getBigDecimal("id_ambiente");
        }
        if (idEnte == null) {
            idEnte = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                    .getBigDecimal("id_ente");
        }

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (numElenchiTotali.longValue() != 0) {
            loadListaElenchi(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers, null);
            forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista degli elenchi in quanto il numero di elenchi totali è pari a 0");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void loadListaElenchi(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal niGgStato,
            String cdTiEveStatoElencoVers, String fiscali) throws EMFError {
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().reset();
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().setEditMode();
        // Creo la combo degli stati elenco e setto il valore
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setDecodeMap(DecodeMap.Factory.newInstance(monitIndiceAIPEjb.getStatiElenco(),
                        "cd_ti_eve_stato_elenco_vers", "cd_ti_eve_stato_elenco_vers"));
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setValue(cdTiEveStatoElencoVers);
        // Popolo i filtri sulla base di quelli inseriti nella pagina precedente
        ComboBox ambienteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        ComboBox enteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente();
        DecodeMapIF mappaEnte = enteCombo.getDecodeMap();

        ComboBox strutCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut();
        DecodeMapIF mappaStrut = strutCombo.getDecodeMap();

        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getFl_elenco_fisc()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setValue(cdTiEveStatoElencoVers);
        if (niGgStato != null) {
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getNi_gg_stato().setValue("" + niGgStato);
        }
        if (idAmbiente != null) {
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ambiente().setValue("" + idAmbiente);
        }
        if (idEnte != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte1 = new DecodeMap();
            mappaEnte1.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte1);

            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().setValue("" + idEnte);
        }
        if (idStrut != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut1 = new DecodeMap();
            mappaStrut1.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut().setDecodeMap(mappaStrut1);

            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut().setValue("" + idStrut);
        }
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().getFl_elenco_fisc().setValue(fiscali);

        BaseTable tabella = monitIndiceAIPEjb.calcolaTotaliListaElenchiIndiceAIP(idAmbiente, idEnte, idStrut, niGgStato,
                cdTiEveStatoElencoVers, fiscali);
        getForm().getElenchiMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getElenchiMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getElenchiMonitoraggioIndiceAIPList().getTable().first();
    }

    public void linkLoadListaUd() throws EMFError {
        BigDecimal idAmbiente = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut().parse();
        BigDecimal niGgStato = getForm().getFiltriMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // BigDecimal numElenchiTotali = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
        // .getBigDecimal("ni_elenchi_total");
        String cdTiEveStatoElencoVers = ((BaseTable) getForm().getMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getString("cd_ti_eve_stato_elenco_vers");

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (cdTiEveStatoElencoVers != null && (cdTiEveStatoElencoVers.equals("IN_CODA_VERIFICA_FIRMA_DT_VERS")
                || cdTiEveStatoElencoVers.equals("IN_CODA_INDICE_AIP_DA_ELAB")
                || cdTiEveStatoElencoVers.equals("ESEGUITA_VERIFICA_FIRMA_DT_VERS"))) {
            loadListaUd(idAmbiente, idEnte, idStrut, null, null, null, niGgStato, cdTiEveStatoElencoVers, null, null,
                    null, Provenienza.GENERALE);
            forwardToPublisher(Application.Publisher.MONITORAGGIO_UD_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista delle unità documentarie in quanto lo stato è diverso da IN_CODA_VERIFICA_FIRMA_DT_VERS, IN_CODA_INDICE_AIP_DA_ELAB o ESEGUITA_VERIFICA_FIRMA_DT_VERS");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaUdDaStrut() throws EMFError {
        BigDecimal niGgStato = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        String cdTiEveStatoElencoVers = getForm().getFiltriStruttureMonitoraggioIndiceAIP()
                .getCd_ti_eve_stato_elenco_vers().parse();
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().setEditMode();
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal idAmbiente = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_ambiente");
        BigDecimal idEnte = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_ente");

        BigDecimal idStrut = ((BaseTable) getForm().getStrutMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_strut");

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (cdTiEveStatoElencoVers != null && (cdTiEveStatoElencoVers.equals("IN_CODA_VERIFICA_FIRMA_DT_VERS")
                || cdTiEveStatoElencoVers.equals("IN_CODA_INDICE_AIP_DA_ELAB")
                || cdTiEveStatoElencoVers.equals("ESEGUITA_VERIFICA_FIRMA_DT_VERS"))) {
            loadListaUd(idAmbiente, idEnte, idStrut, null, null, null, niGgStato, cdTiEveStatoElencoVers, null, null,
                    null, Provenienza.STRUTTURA);
            forwardToPublisher(Application.Publisher.MONITORAGGIO_UD_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista delle unità documentarie in quanto lo stato è diverso da IN_CODA_VERIFICA_FIRMA_DT_VERS, IN_CODA_INDICE_AIP_DA_ELAB o ESEGUITA_VERIFICA_FIRMA_DT_VERS");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void linkLoadListaUdDaElenco() throws EMFError {
        BigDecimal niGgStato = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getNi_gg_stato().parse();
        String cdTiEveStatoElencoVers = getForm().getFiltriElenchiMonitoraggioIndiceAIP()
                .getCd_ti_eve_stato_elenco_vers().parse();
        String fiscale = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getFl_elenco_fisc().parse();
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().setEditMode();
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal idElencoVers = ((BaseTable) getForm().getElenchiMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_elenco_vers");

        BigDecimal idAmbiente = ((BaseTable) getForm().getElenchiMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_ambiente");
        BigDecimal idEnte = ((BaseTable) getForm().getElenchiMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_ente");

        BigDecimal idStrut = ((BaseTable) getForm().getElenchiMonitoraggioIndiceAIPList().getTable()).getRow(riga)
                .getBigDecimal("id_strut");

        // Controllo se il numero di elenchi è diverso da 0 e il filtro struttura non è stato inserito
        if (cdTiEveStatoElencoVers.equals("IN_CODA_VERIFICA_FIRMA_DT_VERS")
                || cdTiEveStatoElencoVers.equals("IN_CODA_INDICE_AIP_DA_ELAB")
                || cdTiEveStatoElencoVers.equals("ESEGUITA_VERIFICA_FIRMA_DT_VERS")) {
            loadListaUd(idAmbiente, idEnte, idStrut, null, null, null, niGgStato, cdTiEveStatoElencoVers, fiscale,
                    idElencoVers, null, Provenienza.ELENCO);
            forwardToPublisher(Application.Publisher.MONITORAGGIO_UD_INDICE_AIP);
        } else {
            getMessageBox().addError(
                    "Non è possibile accedere alla lista delle unità documentarie in quanto lo stato è diverso da IN_CODA_VERIFICA_FIRMA_DT_VERS, IN_CODA_INDICE_AIP_DA_ELAB o ESEGUITA_VERIFICA_FIRMA_DT_VERS");
            forwardToPublisher(getLastPublisher());
        }
    }

    private enum Provenienza {
        GENERALE, ELENCO, STRUTTURA
    }

    public void loadListaUd(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal niGgStato, String cdTiEveStatoElencoVers,
            String fiscali, BigDecimal idElencoVers, String tiStatoUdElencoVers, Provenienza provenienza)
            throws EMFError {
        getForm().getFiltriUdMonitoraggioIndiceAIP().reset();
        getForm().getUdMonitoraggioIndiceAIPList().setTable(new BaseTable());
        getForm().getConteggioMonitoraggioIndiceAIPList().setTable(new BaseTable());
        getForm().getFiltriUdMonitoraggioIndiceAIP().setEditMode();
        // Creo la combo degli stati elenco e setto il valore
        getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setDecodeMap(DecodeMap.Factory.newInstance(monitIndiceAIPEjb.getStatiElenco(),
                        "cd_ti_eve_stato_elenco_vers", "cd_ti_eve_stato_elenco_vers"));
        getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers().setValue(cdTiEveStatoElencoVers);
        // Popolo i filtri sulla base di quelli inseriti nella pagina precedente
        ComboBox ambienteCombo = null;
        ComboBox enteCombo = null;
        ComboBox strutCombo = null;

        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));

        switch (provenienza) {
        case GENERALE:
            ambienteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente();
            enteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente();
            strutCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut();
            break;
        case ELENCO:
            ambienteCombo = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ambiente();
            enteCombo = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente();
            strutCombo = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut();
            break;
        case STRUTTURA:
            ambienteCombo = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente();
            enteCombo = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente();
            strutCombo = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_strut();
            break;
        default:
            ambienteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ambiente();
            enteCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_ente();
            strutCombo = getForm().getFiltriMonitoraggioIndiceAIP().getId_strut();
            break;
        }

        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();
        DecodeMapIF mappaEnte = enteCombo.getDecodeMap();
        DecodeMapIF mappaStrut = strutCombo.getDecodeMap();

        getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ambiente().setDecodeMap(mappaAmbiente);

        // Ambiente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            logger.error("Errore nel recupero ambiente", ex);
        }
        DecodeMap mappaAmbiente1 = new DecodeMap();
        mappaAmbiente1.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ambiente().setDecodeMap(mappaAmbiente1);
        getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ambiente().setValue("" + idAmbiente);

        // Ente
        // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
        if (idAmbiente != null) {
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte1 = new DecodeMap();
            mappaEnte1.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte1);
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().setValue("" + idEnte);
        }

        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut()
                    .setDecodeMap(DecodeMap.Factory.newInstance(strutTableBean, "id_strut", "nm_strut"));
            // }
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().setValue("" + idStrut);
        }

        getForm().getFiltriUdMonitoraggioIndiceAIP().getFl_elenco_fisc()
                .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriUdMonitoraggioIndiceAIP().getTi_stato_ud_elenco_vers()
                .setDecodeMap(ComboGetter.getMappaTiStatoUdElencoVers());

        getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers().setValue(cdTiEveStatoElencoVers);
        if (niGgStato != null) {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getNi_gg_stato().setValue("" + niGgStato);
        }
        if (idAmbiente != null) {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ambiente().setValue("" + idAmbiente);
        }
        if (idEnte != null) {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().setValue("" + idEnte);
        }
        if (idStrut != null) {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().setValue("" + idStrut);
        }
        if (idElencoVers != null) {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_elenco_vers().setValue("" + idElencoVers);
        }

        if (strutCombo.getDecodeMap() != null && !strutCombo.getDecodeMap().isEmpty() && idStrut != null) {
            DecRegistroUnitaDocTableBean registroTableBean = registroEjb.getDecRegistroUnitaDocTableBean(idStrut);
            getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_registro_key_unita_doc().setDecodeMap(
                    DecodeMap.Factory.newInstance(registroTableBean, "cd_registro_unita_doc", "cd_registro_unita_doc"));
        }

        getForm().getFiltriUdMonitoraggioIndiceAIP().getFl_elenco_fisc().setValue(fiscali);

        BaseTable tabella = monitIndiceAIPEjb.calcolaTotaliListaUdIndiceAIP(idAmbiente, idEnte, idStrut,
                cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, niGgStato, cdTiEveStatoElencoVers, fiscali,
                idElencoVers, tiStatoUdElencoVers);

        getForm().getUdMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getUdMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getUdMonitoraggioIndiceAIPList().getTable().first();
    }

    @Override
    public void loadDettaglio() throws EMFError {

    }

    @Override
    public void undoDettaglio() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
    }

    @Override
    public void saveDettaglio() throws EMFError {
    }

    @Override
    public void dettaglioOnClick() throws EMFError {

        if (getTableName().equals(getForm().getUdMonitoraggioIndiceAIPList().getName())) {
            // VISUALIZZA UNITA' DOCUMENTARIA DA LISTA UNITA' DOCUMENTARIE PER PROCESSO GENERAZIONE INDICE AIP
            UnitaDocumentarieForm form = new UnitaDocumentarieForm();
            AroVVisUnitaDocIamTableBean udTableBean = new AroVVisUnitaDocIamTableBean();
            Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
            BigDecimal idUnitaDoc = getForm().getUdMonitoraggioIndiceAIPList().getTable().getRow(riga)
                    .getBigDecimal("id_unita_doc");

            AroVVisUnitaDocIamRowBean udRowBean = new AroVVisUnitaDocIamRowBean();
            udRowBean.setIdUnitaDoc(idUnitaDoc);

            if (!getMessageBox().hasError()) {
                udTableBean.add(udRowBean);
                form.getUnitaDocumentarieList().setTable(udTableBean);
                /*
                 * Setto il pageSize ad almeno 1, in quanto il framework, per inserire i tasti Annulla e Salva nel
                 * dettaglio dell'elenco controlla che il pageSize sia diverso da 0
                 */
                form.getUnitaDocumentarieList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                        "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=0",
                        form);
            }
        } else if (getTableName().equals(getForm().getElenchiMonitoraggioIndiceAIPList().getName())) {
            // VISUALIZZA ELENCO DA LISTA ELENCHI PER PROCESSO GENERAZIONE INDICE AIP
            ElenchiVersamentoForm form = new ElenchiVersamentoForm();
            ElvVRicElencoVersTableBean elencoTableBean = new ElvVRicElencoVersTableBean();
            Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
            BigDecimal idElencoVers = getForm().getElenchiMonitoraggioIndiceAIPList().getTable().getRow(riga)
                    .getBigDecimal("id_elenco_vers");

            ElvVRicElencoVersRowBean elencoRowBean = new ElvVRicElencoVersRowBean();
            elencoRowBean.setIdElencoVers(idElencoVers);

            if (!getMessageBox().hasError()) {
                elencoTableBean.add(elencoRowBean);
                form.getElenchiVersamentoList().setTable(elencoTableBean);
                /*
                 * Setto il pageSize ad almeno 1, in quanto il framework, per inserire i tasti Annulla e Salva nel
                 * dettaglio dell'elenco controlla che il pageSize sia diverso da 0
                 */
                form.getElenchiVersamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                redirectToAction(Application.Actions.ELENCHI_VERSAMENTO,
                        "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW + "&table="
                                + ElenchiVersamentoForm.ElenchiVersamentoList.NAME + "&riga=0",
                        form);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MONITORAGGIO_RIEPILOGO_GENERAZIONE_INDICE_AIP; // Tools | Templates.
    }

    @Override
    public void reloadAfterGoBack(String string) {
    }

    @Override
    public String getControllerName() {
        return Application.Actions.MONITORAGGIO_INDICE_AIP;
    }

    @Override
    public JSONObject triggerFiltriStruttureMonitoraggioIndiceAIPId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().parse();

        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                    .setDecodeMap(new DecodeMap());

        } else {
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                    .setDecodeMap(new DecodeMap());

        }

        // Creo la combo degli stati elenco e setto il valore
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .setDecodeMap(DecodeMap.Factory.newInstance(monitIndiceAIPEjb.getStatiElenco(),
                        "cd_ti_eve_stato_elenco_vers", "cd_ti_eve_stato_elenco_vers"));
        return getForm().getFiltriStruttureMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public JSONObject triggerFiltriStruttureMonitoraggioIndiceAIPId_enteOnTrigger() throws EMFError {
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().post(getRequest());

        BigDecimal idEnte = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_strut().setDecodeMap(mappaStrut);

        } else {
            getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());

        }
        return getForm().getFiltriStruttureMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiMonitoraggioIndiceAIPId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ambiente().parse();

        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                    .setDecodeMap(new DecodeMap());

        } else {
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                    .setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriElenchiMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public JSONObject triggerFiltriElenchiMonitoraggioIndiceAIPId_enteOnTrigger() throws EMFError {
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().post(getRequest());

        BigDecimal idEnte = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut().setDecodeMap(mappaStrut);
        } else {
            getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriElenchiMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdMonitoraggioIndiceAIPId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriUdMonitoraggioIndiceAIP().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ambiente().parse();

        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, null, null, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().setDecodeMap(mappaEnte);

        } else {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriUdMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdMonitoraggioIndiceAIPId_enteOnTrigger() throws EMFError {
        getForm().getFiltriUdMonitoraggioIndiceAIP().post(getRequest());

        BigDecimal idEnte = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().setDecodeMap(mappaStrut);

        } else {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().setDecodeMap(new DecodeMap());

        }
        return getForm().getFiltriUdMonitoraggioIndiceAIP().asJSON();
    }

    @Override
    public void generaStrutMonIndiceAIPButton() throws EMFError {
        getForm().getFiltriStruttureMonitoraggioIndiceAIP().postAndValidate(getRequest(), getMessageBox());
        if (!getMessageBox().hasError()) {
            BigDecimal idAmbiente = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getId_ente().parse();
            BigDecimal niGgStato = getForm().getFiltriStruttureMonitoraggioIndiceAIP().getNi_gg_stato().parse();
            String cdTiEveStatoElencoVers = getForm().getFiltriStruttureMonitoraggioIndiceAIP()
                    .getCd_ti_eve_stato_elenco_vers().parse();
            // Calcolo subito la lista senza filtri
            calcolaStruttureProcessoGenerazioneIndiceAIP(idAmbiente, idEnte, niGgStato, cdTiEveStatoElencoVers);
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_STRUTTURE_INDICE_AIP);
    }

    private void calcolaStruttureProcessoGenerazioneIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal niGgStato, String cdTiEveStatoElencoVers) {
        BaseTable tabella = monitIndiceAIPEjb.calcolaTotaliListaStruttureIndiceAIP(idAmbiente, idEnte, niGgStato,
                cdTiEveStatoElencoVers);
        getForm().getStrutMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getStrutMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getStrutMonitoraggioIndiceAIPList().getTable().first();
        getForm().getStrutMonitoraggioIndiceAIPList().getTable()
                .addSortingRule(getForm().getStrutMonitoraggioIndiceAIPList().getNm_strut().getName(), SortingRule.ASC);
    }

    @Override
    public void generaElenchiMonIndiceAIPButton() throws EMFError {
        getForm().getFiltriElenchiMonitoraggioIndiceAIP().postAndValidate(getRequest(), getMessageBox());
        if (!getMessageBox().hasError()) {
            BigDecimal idAmbiente = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_ente().parse();
            BigDecimal idStrut = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getId_strut().parse();
            BigDecimal niGgStato = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getNi_gg_stato().parse();
            String cdTiEveStatoElencoVers = getForm().getFiltriElenchiMonitoraggioIndiceAIP()
                    .getCd_ti_eve_stato_elenco_vers().parse();
            String flElencoFisc = getForm().getFiltriElenchiMonitoraggioIndiceAIP().getFl_elenco_fisc().parse();
            // Calcolo subito la lista senza filtri
            calcolaElenchiProcessoGenerazioneIndiceAIP(idAmbiente, idEnte, idStrut, niGgStato, cdTiEveStatoElencoVers,
                    flElencoFisc);
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_ELENCHI_INDICE_AIP);
    }

    private void calcolaElenchiProcessoGenerazioneIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, BigDecimal niGgStato, String cdTiEveStatoElencoVers, String flElencoFisc) {
        BaseTable tabella = monitIndiceAIPEjb.calcolaTotaliListaElenchiIndiceAIP(idAmbiente, idEnte, idStrut, niGgStato,
                cdTiEveStatoElencoVers, flElencoFisc);
        getForm().getElenchiMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getElenchiMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getElenchiMonitoraggioIndiceAIPList().getTable().first();
        getForm().getElenchiMonitoraggioIndiceAIPList().getTable().addSortingRule(
                getForm().getElenchiMonitoraggioIndiceAIPList().getNm_elenco().getName(), SortingRule.ASC);
    }

    @Override
    public void generaUdMonIndiceAIPButton() throws EMFError {
        getForm().getFiltriUdMonitoraggioIndiceAIP().postAndValidate(getRequest(), getMessageBox());
        if (!getMessageBox().hasError()) {
            BigDecimal idAmbiente = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ambiente().parse();
            BigDecimal idEnte = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_ente().parse();
            BigDecimal idStrut = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().parse();
            String cdRegistroKeyUnitaDoc = getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_registro_key_unita_doc()
                    .parse();
            BigDecimal aaKeyUnitaDoc = getForm().getFiltriUdMonitoraggioIndiceAIP().getAa_key_unita_doc().parse();
            String cdKeyUnitaDoc = getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_key_unita_doc().parse();
            BigDecimal niGgStato = getForm().getFiltriUdMonitoraggioIndiceAIP().getNi_gg_stato().parse();
            String cdTiEveStatoElencoVers = getForm().getFiltriUdMonitoraggioIndiceAIP()
                    .getCd_ti_eve_stato_elenco_vers().parse();
            String flElencoFisc = getForm().getFiltriUdMonitoraggioIndiceAIP().getFl_elenco_fisc().parse();
            BigDecimal idElencoVers = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_elenco_vers().parse();
            String tiStatoUdElencoVers = getForm().getFiltriUdMonitoraggioIndiceAIP().getTi_stato_ud_elenco_vers()
                    .parse();

            // Calcolo subito la lista senza filtri
            if (cdTiEveStatoElencoVers != null && (!cdTiEveStatoElencoVers.equals("IN_CODA_VERIFICA_FIRMA_DT_VERS")
                    && !cdTiEveStatoElencoVers.equals("IN_CODA_INDICE_AIP_DA_ELAB")
                    && !cdTiEveStatoElencoVers.equals("ESEGUITA_VERIFICA_FIRMA_DT_VERS"))) {
                if (idAmbiente == null || idEnte == null || idStrut == null || idElencoVers == null) {
                    getMessageBox().addError(
                            "I campi Ambiente, Ente, Struttura ed ID elenco sono obbligatori in quanto lo stato elenco è diverso da IN_CODA_VERIFICA_FIRMA_DT_VERS, IN_CODA_INDICE_AIP_DA_ELAB e ESEGUITA_VERIFICA_FIRMA_DT_VERS");
                } else {
                    calcolaUdProcessoGenerazioneIndiceAIP(idAmbiente, idEnte, idStrut, cdRegistroKeyUnitaDoc,
                            aaKeyUnitaDoc, cdKeyUnitaDoc, niGgStato, cdTiEveStatoElencoVers, flElencoFisc, idElencoVers,
                            tiStatoUdElencoVers);
                }
            } else {
                calcolaUdProcessoGenerazioneIndiceAIP(idAmbiente, idEnte, idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
                        cdKeyUnitaDoc, niGgStato, cdTiEveStatoElencoVers, flElencoFisc, idElencoVers,
                        tiStatoUdElencoVers);
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_UD_INDICE_AIP);
    }

    private void calcolaUdProcessoGenerazioneIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal niGgStato,
            String cdTiEveStatoElencoVers, String flElencoFisc, BigDecimal idElencoVers, String tiStatoUdElencoVers) {
        BaseTable tabella = monitIndiceAIPEjb.calcolaTotaliListaUdIndiceAIP(idAmbiente, idEnte, idStrut,
                cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, niGgStato, cdTiEveStatoElencoVers, flElencoFisc,
                idElencoVers, tiStatoUdElencoVers);
        getForm().getUdMonitoraggioIndiceAIPList().setTable(tabella);
        getForm().getUdMonitoraggioIndiceAIPList().getTable().setPageSize(10);
        getForm().getUdMonitoraggioIndiceAIPList().getTable().first();
        getForm().getUdMonitoraggioIndiceAIPList().getTable().addSortingRule(
                getForm().getUdMonitoraggioIndiceAIPList().getCd_registro_key_unita_doc().getName(), SortingRule.ASC);
        getForm().getUdMonitoraggioIndiceAIPList().getTable().addSortingRule(
                getForm().getUdMonitoraggioIndiceAIPList().getAa_key_unita_doc().getName(), SortingRule.ASC);
        getForm().getUdMonitoraggioIndiceAIPList().getTable().addSortingRule(
                getForm().getUdMonitoraggioIndiceAIPList().getCd_key_unita_doc().getName(), SortingRule.ASC);
    }

    @Override
    public void contaUdMonIndiceAIPButton() throws EMFError {
        getForm().getFiltriUdMonitoraggioIndiceAIP().post(getRequest());
        String cdTiEveStatoElencoVers = getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_ti_eve_stato_elenco_vers()
                .parse();
        if (cdTiEveStatoElencoVers != null && (cdTiEveStatoElencoVers.equals("IN_CODA_INDICE_AIP_DA_ELAB")
                || cdTiEveStatoElencoVers.equals("ESEGUITA_VERIFICA_FIRMA_DT_VERS"))) {
            BaseTable tabella = monitIndiceAIPEjb.contaTotaliListaUdIndiceAIP();
            getForm().getConteggioMonitoraggioIndiceAIPList().setTable(tabella);
            getForm().getConteggioMonitoraggioIndiceAIPList().getTable().setPageSize(10);
            getForm().getConteggioMonitoraggioIndiceAIPList().getTable().first();
        } else {
            getMessageBox().addError(
                    "Non è possibile eseguire il conteggio in quanto lo stato elenco non è IN_CODA_INDICE_AIP_DA_ELAB o ESEGUITA_VERIFICA_FIRMA_DT_VERS");
            getForm().getConteggioMonitoraggioIndiceAIPList().setTable(null);
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_UD_INDICE_AIP);
    }

    @Override
    public JSONObject triggerFiltriUdMonitoraggioIndiceAIPId_strutOnTrigger() throws EMFError {
        getForm().getFiltriUdMonitoraggioIndiceAIP().post(getRequest());
        BigDecimal idStrut = getForm().getFiltriUdMonitoraggioIndiceAIP().getId_strut().parse();
        if (idStrut != null) {
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecodeMap mappaRegistro = new DecodeMap();
            mappaRegistro.populatedMap(registroUnitaDocTableBean, "cd_registro_unita_doc", "cd_registro_unita_doc");
            getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_registro_key_unita_doc().setDecodeMap(mappaRegistro);
        } else {
            getForm().getFiltriUdMonitoraggioIndiceAIP().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriUdMonitoraggioIndiceAIP().asJSON();
    }
}
