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

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb.RegistroEjb;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.ejb.SottoStruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.ejb.TipoDocumentoEjb;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.async.ejb.CalcoloMonitoraggioAsync;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.jboss.timer.service.JbossTimerEjb;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.Application;
import it.eng.parer.slite.gen.action.MonitoraggioAbstractAction;
import it.eng.parer.slite.gen.form.*;
import it.eng.parer.slite.gen.form.MonitoraggioForm.*;
import it.eng.parer.slite.gen.tablebean.*;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.dto.MonitoraggioAttributiVersFallitiDaDocNonVersati;
import it.eng.parer.web.dto.MonitoraggioFiltriListaDocBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiDistintiDocBean;
import it.eng.parer.web.ejb.CaricaErrori;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.ejb.GestioneJobEjb;
import it.eng.parer.web.ejb.MonitoraggioEjb;
import it.eng.parer.web.ejb.MonitoraggioSinteticoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.helper.VolumiHelper;
import it.eng.parer.web.util.*;
import it.eng.parer.web.util.ActionEnums.SezioneMonitoraggio;
import it.eng.parer.web.validator.MonitoraggioValidator;
import it.eng.parer.web.validator.TypeValidator;
import it.eng.parer.web.validator.UnitaDocumentarieValidator;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import it.eng.spagoLite.security.User;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import javax.ejb.EJB;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.codehaus.jettison.json.JSONObject;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblLayoutType;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblLayoutType;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioAction extends MonitoraggioAbstractAction {

    public static final String FROM_GESTIONE_JOB = "fromGestioneJob";
    private static final String ERRORE_RECUPERO_AMBIENTE = "Errore nel recupero ambiente";
    private static Logger log = LoggerFactory.getLogger(MonitoraggioAction.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/UnitaDocumentarieHelper")
    private UnitaDocumentarieHelper udHelper;
    @EJB(mappedName = "java:app/Parer-ejb/VolumiHelper")
    private VolumiHelper volumiHelper;
    @EJB(mappedName = "java:app/Parer-ejb/ElenchiVersamentoEjb")
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/AmbienteEjb")
    private AmbienteEjb ambienteEjb;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;
    @EJB(mappedName = "java:app/Parer-ejb/CaricaErrori")
    private CaricaErrori caricaErrori;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioEjb")
    private MonitoraggioEjb monitoraggioEjb;
    @EJB(mappedName = "java:app/Parer-ejb/CalcoloMonitoraggioAsync")
    private CalcoloMonitoraggioAsync calcoloAsync;
    @EJB(mappedName = "java:app/Parer-ejb/TipoUnitaDocEjb")
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB(mappedName = "java:app/Parer-ejb/RegistroEjb")
    private RegistroEjb registroEjb;
    @EJB(mappedName = "java:app/Parer-ejb/TipoDocumentoEjb")
    private TipoDocumentoEjb tipoDocumentoEjb;
    @EJB(mappedName = "java:app/Parer-ejb/StruttureEjb")
    private StruttureEjb struttureEjb;
    @EJB(mappedName = "java:app/Parer-ejb/SottoStruttureEjb")
    private SottoStruttureEjb subStrutEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioSinteticoEjb")
    private MonitoraggioSinteticoEjb monitSintEjb;

    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb jbossTimerEjb;

    @EJB(mappedName = "java:app/Parer-ejb/GestioneJobEjb")
    private GestioneJobEjb gestioneJobEjb;

    @Override
    public void initOnClick() throws EMFError {
        // not implemented yet
    }

    @Override
    public void process() throws EMFError {
        // not implemented yet
    }

    /**
     * Ritorna la pagina di default della sezione Monitoraggio
     *
     * @return Application.Publisher.MONITORAGGIO_RIEPILOGO_VERS
     */
    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.MONITORAGGIO_RIEPILOGO_STRUTTURA;
    }

    /**
     * Ritorna la classe action associata alla sezione Monitoraggio
     *
     * @return Application.Actions.MONITORAGGIO
     */
    @Override
    public String getControllerName() {
        return Application.Actions.MONITORAGGIO;
    }

    /**
     * Metodo di inizializzazione form di Riepilogo Struttura
     *
     */
    @Secure(action = "Menu.Monitoraggio.RiepilogoStruttura")
    public void riepilogoStruttura() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.RiepilogoStruttura");
        // Resetto tutti i campi di riepilogo versamenti (filtri)
        getForm().getRiepilogoStruttura().reset();

        // Inizializzo la combo Ambiente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error(ERRORE_RECUPERO_AMBIENTE, ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getRiepilogoStruttura().getId_ambiente().setDecodeMap(mappaAmbiente);

        /*
         * Se ho un solo ambiente lo setto gi\u00e0  impostato nella combo e procedo con i controlli successivi
         */
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getRiepilogoStruttura().getId_ambiente()
                    .setValue(tmpTableBeanAmbiente.getRow(0).getIdAmbiente().toString());
        }
        /*
         * altrimenti imposto la combo ambiente con i diversi valori ma senza averne selezionato uno in particolare e
         * imposto vuote le altre combo
         */

        // Setto editabile il bottone di ricerca, da utilizzare in caso di javascript disattivato
        getForm().getRiepilogoStruttura().getRicercaRiepilogoStrutturaButton().setEditMode();
        // Imposto le combo in editMode
        getForm().getRiepilogoStruttura().setEditMode();
    }

    @Override
    public void ricercaRiepilogoStrutturaButton() throws EMFError {
        MonitoraggioForm.RiepilogoStruttura filtri = getForm().getRiepilogoStruttura();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        // Valida i filtri per verificare quelli obbligatori
        if (filtri.validate(getMessageBox())) {
            // Setto il filtro in sessione perch\u00e0¨ se torno indietro in "Riepilogo Struttura" devo avere impostata
            // la ricerca con quest'ultimo
            getSession().setAttribute("filtriRiepilogoStruttura", filtri.getId_ambiente().parse().intValue());
            // Carico i dati nella pagina Riepilogo Struttura
            String maxResultStandard = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
            MonVRiepStrutIamTableBean listaMon = monitoraggioHelper.getMonVRiepStrutIamViewBean(getUser().getIdUtente(),
                    Integer.parseInt(maxResultStandard), filtri.getId_ambiente().parse().intValue());
            getForm().getRiepilogoStrutturaList().setTable(listaMon);
            getForm().getRiepilogoStrutturaList().getTable().setPageSize(10);
            getForm().getRiepilogoStrutturaList().getTable().first();
        }
        // Eseguo il forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_RIEPILOGO_STRUTTURA);
    }

    /**
     * Metodo di inizializzazione form di Sessioni Errate
     *
     */
    @Secure(action = "Menu.Logging.SessioniErrate")
    public void sessioniErrate() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Logging.SessioniErrate");

        // Preparo la combo "Verificato"
        getForm().getFiltriSessione().getSessione_ses_err_verif().reset();
        getForm().getFiltriSessione().getSessione_ses_err_verif().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        getForm().getFiltriSessione().getSessione_ses_err_verif().setEditMode();

        // Setto editabile il bottone di ricerca, da utilizzare in caso di javascript disattivato
        getForm().getFiltriSessione().getCercaSessioniErrate().setEditMode();

        // Ricavo la lista delle sessioni errate senza filtraggio per flag verificato
        String maxResultSessioniErrate = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_SESSIONI_ERRATE);
        VrsSessioneVersKoTableBean listaSessErr = monitoraggioHelper.getSessioniErrateListTB(null,
                Integer.parseInt(maxResultSessioniErrate));
        getForm().getSessioniErrateList().setTable(listaSessErr);
        getForm().getSessioniErrateList().getTable().setPageSize(10);
        getForm().getSessioniErrateList().getTable().first();

        /*
         * Rendo visibile il bottone per impostare la verifica della sessione e quello per calcolare la struttura
         * versante se la lista delle sessioni errate non \u00e0¨ vuota
         */
        if (listaSessErr.size() != 0) {
            getForm().getSalvaVerificaButtonList().getSalvaVerificaSessione().setEditMode();
            getForm().getFiltriSessione().getCalcolaStrutturaVersante().setEditMode();
        } else {
            getForm().getSalvaVerificaButtonList().getSalvaVerificaSessione().setViewMode();
            getForm().getFiltriSessione().getCalcolaStrutturaVersante().setViewMode();
        }

        // Eseguo il forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
    }

    /**
     * Metodo di inizializzazione form di Esame Operazioni Volumi
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.Monitoraggio.EsameOperazioniVolumi")
    public void operazioniVolumi() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.EsameOperazioniVolumi");

        // Inizializzo le liste fittizie nel caso si voglia visualizzare unit\u00e0  documentaria o volume
        getForm().getUnitaDocumentariaOutputList().setTable(new AroVRicUnitaDocTableBean());
        getForm().getVolumeOutputList().setTable(new VolVRicVolumeTableBean());

        // Setto vuote le liste risultato
        getForm().getOutputAnaliticoCronologicoList().setTable(null);
        getForm().getOutputAggregatoList().setTable(null);

        // Resetto i campi
        getForm().getFiltriOperazioniVolumi().reset();

        // Inizializzo la combo Ambiente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error(ERRORE_RECUPERO_AMBIENTE, ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriOperazioniVolumi().getId_ambiente().setDecodeMap(mappaAmbiente);

        /*
         * Se ho un solo ambiente lo setto gi\u00e0  impostato nella combo e procedo con i controlli successivi
         */
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getFiltriOperazioniVolumi().getId_ambiente()
                    .setValue(tmpTableBeanAmbiente.getRow(0).getIdAmbiente().toString());
            BigDecimal idAmbiente = tmpTableBeanAmbiente.getRow(0).getIdAmbiente();
            checkUniqueAmbienteInCombo(idAmbiente, ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI);
        } /*
           * altrimenti imposto la combo ambiente con i diversi valori ma senza averne selezionato uno in particolare e
           * imposto vuote le altre combo
           */ else {
            getForm().getFiltriOperazioniVolumi().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriOperazioniVolumi().getId_strut().setDecodeMap(new DecodeMap());
        }

        // Inizializzo le combo Modalit\u00e0  operazione e Tipo output
        getForm().getFiltriOperazioniVolumi().getTi_mod_oper().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("modalita", VolumeEnums.ModalitaOperazioni.values()));
        getForm().getFiltriOperazioniVolumi().getTi_output().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_output", VolumeEnums.TipoOutputMonitoraggio.values()));

        // Di default imposto checkate le varie checkbox
        getForm().getFiltriOperazioniVolumi().getFl_oper_crea_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_recupera_volume_aperto().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_aggiungi_doc_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_recupera_volume_scaduto().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_set_volume_da_chiudere().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_set_volume_aperto().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_inizio_crea_indice().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_recupera_volume_in_errore().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_crea_indice_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_marca_indice_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_set_volume_in_errore().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_inizio_verif_firme().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_chiusura_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_err_verif_firme().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_rimuovi_doc_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_elimina_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_modifica_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_firma_no_marca_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_firma_volume().setChecked(true);

        // Imposto in editMode tutti i campi
        getForm().getFiltriOperazioniVolumi().setEditMode();

        // Eseguo il forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_OPERAZIONI_VOLUMI_RICERCA);
    }

    /**
     * Metodo di inizializzazione form di Esame Operazioni Elenchi di versamento
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.Monitoraggio.EsameOperazioniElenchiVersamento")
    public void operazioniElenchiVersamento() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.EsameOperazioniElenchiVersamento");

        /* Inizializzo le liste fittizie nel caso si voglia visualizzare l'elenco di versamento */
        getForm().getElencoVersamentoOutputList().setTable(new ElvVRicElencoVersTableBean());

        /* Setto vuote le liste risultato */
        getForm().getOutputAnaliticoCronologicoListElenchi().setTable(null);
        getForm().getOutputAggregatoListElenchi().setTable(null);

        /* Resetto i filtri */
        resetFiltriOperazioniElenchiVersamento();

        forwardToPublisher(Application.Publisher.MONITORAGGIO_OPERAZIONI_ELENCHI_RICERCA);
    }

    /**
     * Metodo di inizializzazione pagina di Esame Contenuto Sacer
     *
     */
    @Secure(action = "Menu.Monitoraggio.EsameContenutoSacer")
    public void contenutoSacer() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.EsameContenutoSacer");

        /* Resetto i campi e il risultato della ricerca */
        getForm().getFiltriContenutoSacer().reset();
        getForm().getContenutoSacerTotaliUdDocComp().clear();
        getForm().getContenutoSacerList().clear();

        /* Inizializzo i campi di ricerca */
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error(ERRORE_RECUPERO_AMBIENTE, ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriContenutoSacer().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        /* Ambito territoriale */
        // Recupera gli ambiti territoriali di 1° livello
        OrgAmbitoTerritTableBean ambitoTerrit = ambienteEjb
                .getOrgAmbitoTerritTableBean(WebConstants.TipoAmbitoTerritoriale.REGIONE_STATO.getNome());
        DecodeMap mappaAmbitoTerrit = new DecodeMap();
        mappaAmbitoTerrit.populatedMap(ambitoTerrit, "id_ambito_territ", "cd_ambito_territ");
        getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_1().setDecodeMap(mappaAmbitoTerrit);
        // Categoria e sottocategoria
        DecCategTipoUnitaDocTableBean table = tipoUnitaDocEjb.getDecCategTipoUnitaDocTableBean(true);
        DecodeMap mappaTiCateg = new DecodeMap();
        mappaTiCateg.populatedMap(table, "id_categ_tipo_unita_doc", "cd_categ_tipo_unita_doc");
        getForm().getFiltriContenutoSacer().getId_categ_tipo_unita_doc().setDecodeMap(mappaTiCateg);
        // Categoria ente
        OrgCategEnteTableBean categEnte = ambienteEjb.getOrgCategEnteTableBean(null);
        DecodeMap mappaCategEnte = new DecodeMap();
        mappaCategEnte.populatedMap(categEnte, "id_categ_ente", "cd_categ_ente");
        getForm().getFiltriContenutoSacer().getId_categ_ente().setDecodeMap(mappaCategEnte);
        // Categoria struttura
        OrgCategStrutTableBean categStrut = ambienteEjb.getOrgCategStrutTableBean();
        DecodeMap mappaCategStrut = new DecodeMap();
        mappaCategStrut.populatedMap(categStrut, "id_categ_strut", "cd_categ_strut");
        getForm().getFiltriContenutoSacer().getId_categ_strut().setDecodeMap(mappaCategStrut);

        // Range di date
        getForm().getFiltriContenutoSacer().getDt_rif_da().setValue("01/12/2011");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        getForm().getFiltriContenutoSacer().getDt_rif_a().setValue(df.format(cal.getTime()));

        getForm().getFiltriContenutoSacer().setEditMode();
        forwardToPublisher(Application.Publisher.MONITORAGGIO_CONTENUTO_SACER_RICERCA);
    }

    /**
     * Metodo di inizializzazione pagina di Esame Consistenza Sacer
     */
    @Secure(action = "Menu.Amministrazione.ConsistenzaSacer")
    public void consistenzaSacer() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Amministrazione.ConsistenzaSacer");

        /* Resetto i campi e il risultato della ricerca */
        getForm().getFiltriConsistenzaSacer().reset();
        getForm().getConsistenzaSacerTotaliUdDocComp().reset();
        getForm().getConsistenzaSacerList().clear();
        getForm().getMostraNascondiIdButtonList().setEditMode();
        getForm().getMostraNascondiIdButtonList().getMostraIdConsistenzaButton().setDescription("Mostra id");

        getSession().removeAttribute(APRI_DELTA);
        getSession().removeAttribute(PROBLEMI_DELTA);

        /* Metto inizialmente invisibili le colonne con gli id */
        setConsistenzaColonneIdInvisibili(true);

        /* Inizializzo i campi di ricerca */
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error(ERRORE_RECUPERO_AMBIENTE, ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriConsistenzaSacer().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriConsistenzaSacer().getId_ente().setDecodeMap(new DecodeMap());
        getForm().getFiltriConsistenzaSacer().getId_strut().setDecodeMap(new DecodeMap());

        getForm().getFiltriConsistenzaSacer().getDifferenza_zero().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());

        // Range di date
        getForm().getFiltriConsistenzaSacer().getDt_rif_da().setValue("01/12/2011");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        getForm().getFiltriConsistenzaSacer().getDt_rif_a().setValue(df.format(cal.getTime()));

        getForm().getFiltriConsistenzaSacer().setEditMode();
        postLoad();
        forwardToPublisher(Application.Publisher.MONITORAGGIO_CONSISTENZA_SACER_RICERCA);
    }

    private void setConsistenzaColonneIdInvisibili(boolean rendiInvisibili) {
        getForm().getConsistenzaSacerProblemsList().getId_registro_unita_doc().setHidden(rendiInvisibili);
        getForm().getConsistenzaSacerProblemsList().getId_strut().setHidden(rendiInvisibili);
        getForm().getConsistenzaSacerProblemsList().getId_sub_strut().setHidden(rendiInvisibili);
        getForm().getConsistenzaSacerProblemsList().getId_tipo_unita_doc().setHidden(rendiInvisibili);
    }

    /**
     * Metodo di inizializzazione form di Esame Job Schedulati
     *
     * @throws EMFError
     *             errore generico
     */
    @Secure(action = "Menu.Monitoraggio.EsameJobSchedulati")
    public void jobSchedulati() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.EsameJobSchedulati");

        // Setto vuota la lista risultato
        getForm().getJobSchedulatiList().setTable(null);

        // Setto preimpostata la data di ricerca da
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        getForm().getFiltriJobSchedulati().getDt_reg_log_job_da().setValue(df.format(cal.getTime()));

        // Resetto i valori delle label
        getForm().getInformazioniJob().reset();

        // Reimposto le combo
        DecodeMap dec = ComboGetter.getMappaSortedGenericEnum("nm_job", JobConstants.JobEnum.values());
        dec.keySet().remove(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE.name());
        dec.keySet().remove(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE.name());
        getForm().getFiltriJobSchedulati().getNm_job().setDecodeMap(dec);
        getForm().getFiltriJobSchedulati().setEditMode();
        getForm().getInformazioniJob().setViewMode();
        getForm().getFiltriJobSchedulati().getId_ambiente().setDecodeMap(new DecodeMap());
        getForm().getFiltriJobSchedulati().getId_ente().setDecodeMap(new DecodeMap());
        getForm().getFiltriJobSchedulati().getId_strut().setDecodeMap(new DecodeMap());

        getForm().getFiltriJobSchedulati().post(getRequest());
        getForm().getInformazioniJob().post(getRequest());
        String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
        if (nmJob != null) {
            setStatoJob(nmJob);
        }

        // Eseguo il forward alla stessa pagina
        forwardToPublisher(Application.Publisher.MONITORAGGIO_JOB_SCHEDULATI_RICERCA);
    }

    /**
     * Impostazione del caricamento dei dettagli di ogni pagina del Monitoraggio
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void dettaglioOnClick() throws EMFError {
        // Controllo per quale tabella \u00e0¨ stato invocato il metodo
        if (getRequest().getParameter("table") != null) {
            // Se ho cliccato sul dettaglio di un record di Riepilogo Struttura
            if ((getRequest().getParameter("table").equals(getForm().getRiepilogoStrutturaList().getName()))) {
                BigDecimal idStruttura = getForm().getRiepilogoStrutturaList().getTable().getCurrentRow()
                        .getBigDecimal("id_strut");
                MonitoraggioSinteticoForm form = new MonitoraggioSinteticoForm();
                redirectToAction(Application.Actions.MONITORAGGIO_SINTETICO,
                        "?operation=loadRiepilogoVersamentiSinteticoByStrut&idStrut=" + idStruttura, form);

            } // Se ho cliccato sull'icona del "Visualizza unità  documentaria" di Lista Documenti
            else if ((getRequest().getParameter("table").equals(getForm().getUnitaDocumentariaList().getName()))) {
                // VISUALIZZA UNITA' DOCUMENTARIA DA LISTA DOCUMENTI / LISTA DOCUMENTI ANNULLATI
                Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
                BaseRowInterface currentRowBean = null;
                BigDecimal idStrut = null;
                if (getLastPublisher().equals(Application.Publisher.MONITORAGGIO_DOC_LIST)) {
                    currentRowBean = getForm().getDocumentiList().getTable().getRow(riga);
                    idStrut = currentRowBean.getBigDecimal("id_strut_unita_doc");
                } else if (getLastPublisher().equals(Application.Publisher.MONITORAGGIO_DOC_ANNULLATI_LIST)) {
                    currentRowBean = getForm().getDocumentiAnnullatiList().getTable().getRow(riga);
                    idStrut = currentRowBean.getBigDecimal("id_strut");
                } else {
                    getMessageBox().addError("Errore nell'apertura dell'unit\u00e0 documentaria selezionata");
                }

                if (!getMessageBox().hasError() && idStrut != null) {
                    boolean abilitatoUD = false;
                    boolean abilitatoReg = false;
                    boolean abilitatoSubStrut = false;
                    boolean abilitatoTipoDoc = false;
                    // Ricavo il TableBean relativo ai tipi di unit\u00e0  doc in base alle abilitazioni
                    DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                            .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                    if (tmpTableBeanTUD.size() != 0) {
                        for (int i = 0; i < tmpTableBeanTUD.size(); i++) {
                            DecTipoUnitaDocRowBean rb = tmpTableBeanTUD.getRow(i);
                            if (rb.getNmTipoUnitaDoc().equals(currentRowBean.getString("nm_tipo_unita_doc"))) {
                                abilitatoUD = true;
                                break;
                            }
                        }
                    }

                    if (abilitatoUD) {
                        DecRegistroUnitaDocTableBean tmpRegUdTb = registroEjb
                                .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                        if (!tmpRegUdTb.isEmpty()) {
                            for (DecRegistroUnitaDocRowBean row : tmpRegUdTb) {
                                if (row.getCdRegistroUnitaDoc()
                                        .equals(currentRowBean.getString("cd_registro_key_unita_doc"))) {
                                    abilitatoReg = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (abilitatoUD && abilitatoReg) {
                        OrgSubStrutTableBean tmpOrgSubStrutTb = subStrutEjb
                                .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), idStrut);
                        if (!tmpOrgSubStrutTb.isEmpty()) {
                            for (OrgSubStrutRowBean row : tmpOrgSubStrutTb) {
                                if (row.getIdSubStrut().compareTo(currentRowBean.getBigDecimal("id_sub_strut")) == 0) {
                                    abilitatoSubStrut = true;
                                    break;
                                }
                            }
                        }
                    }

                    if (abilitatoUD && abilitatoReg && abilitatoSubStrut) {
                        DecTipoDocTableBean tmpDecTipoDocTb = tipoDocumentoEjb
                                .getTipiDocAbilitati(getUser().getIdUtente(), idStrut);
                        if (!tmpDecTipoDocTb.isEmpty()) {
                            for (DecTipoDocRowBean row : tmpDecTipoDocTb) {
                                if (row.getIdTipoDoc().compareTo(currentRowBean.getBigDecimal("id_tipo_doc")) == 0) {
                                    abilitatoTipoDoc = true;
                                    break;
                                }
                            }
                        }
                    }

                    // Se il tipo unit\u00e0  documentaria non \u00e0¨ tra quelli abilitati per l'utente, non devo
                    // visualizzare il dettaglio unit\u00e0  documentaria
                    if (abilitatoUD && abilitatoReg && abilitatoSubStrut && abilitatoTipoDoc) {
                        UnitaDocumentarieForm form = new UnitaDocumentarieForm();
                        AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
                        BigDecimal idUnitaDoc = currentRowBean.getBigDecimal("id_unita_doc");
                        unitaDocTB.add(udHelper.getAroVRicUnitaDocRowBean(idUnitaDoc, idStrut, null));
                        form.getUnitaDocumentarieList().setTable(unitaDocTB);
                        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW
                                        + "&table=" + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=0",
                                form);
                    } else {
                        getMessageBox().addMessage(new Message(MessageLevel.ERR,
                                "Utente non abilitato ad accedere al dettaglio dell'unit\u00e0 documentaria selezionata"));
                    }
                }
            } // Se ho cliccato sull'icona del "Visualizza volume" di Lista Documenti
            else if ((getRequest().getParameter("table").equals(getForm().getVolumeList().getName()))) {
                // VISUALIZZA VOLUME DA LISTA DOCUMENTI
                // Se lo stato_doc \u00e0¨ NON_SELEZ_SCHED o IN_ATTESA_SCHED non devo visualizzare il dettaglio volume
                Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
                BaseRowInterface documentoRow = getForm().getDocumentiList().getTable().getRow(riga);
                String tiStatoDocElencoVers = documentoRow.getString("ti_stato_doc_elenco_vers");
                if (tiStatoDocElencoVers.equals(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name())
                        || tiStatoDocElencoVers.equals(ElencoEnums.DocStatusEnum.IN_ATTESA_SCHED.name())
                        || tiStatoDocElencoVers.equals(ElencoEnums.DocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name())) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Non \u00e0 possibile visualizzare il dettaglio di un volume con stato documento NON_SELEZ_SCHED o IN_ATTESA_SCHED o IN_ATTESA_MEMORIZZAZIONE"));
                } else {
                    VolumiForm form = new VolumiForm();
                    VolVRicVolumeTableBean volumeTB = new VolVRicVolumeTableBean();
                    BigDecimal idVol = getForm().getDocumentiList().getTable().getRow(riga)
                            .getBigDecimal("id_volume_conserv");
                    BigDecimal idStrut = getForm().getDocumentiList().getTable().getRow(riga)
                            .getBigDecimal("id_strut_unita_doc");
                    VolVRicVolumeRowBean row = new VolVRicVolumeRowBean();
                    row.setIdVolumeConserv(idVol);
                    row.setIdStrutVolume(idStrut);
                    volumeTB.add(row);
                    form.getVolumiList().setTable(volumeTB);
                    /*
                     * Setto il pageSize ad almeno 1, in quanto il framework, per inserire i tasti Annulla e Salva nel
                     * dettaglio del volume controlla che il pageSize sia diverso da 0
                     */
                    form.getVolumiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    redirectToAction(
                            Application.Actions.VOLUMI, "?operation=listNavigationOnClick&navigationEvent="
                                    + ListAction.NE_DETTAGLIO_VIEW + "&table=" + VolumiForm.VolumiList.NAME + "&riga=0",
                            form);
                }
            } // Se ho cliccato sull'icona del "Visualizza unità  documentaria" di Operazioni Volumi
            else if ((getRequest().getParameter("table")
                    .equals(getForm().getUnitaDocumentariaOutputList().getName()))) {
                // VISUALIZZA UNITA' DOCUMENTARIA DA OPERAZIONI VOLUMI
                UnitaDocumentarieForm form = new UnitaDocumentarieForm();
                AroVRicUnitaDocTableBean unitaDocTB = new AroVRicUnitaDocTableBean();
                Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
                BigDecimal idUnitaDoc = getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga)
                        .getBigDecimal("id_unita_doc");
                BigDecimal idStrut = getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga)
                        .getBigDecimal("id_strut");

                // Se il tipo di operazione \u00e0 diverso da AGGIUNGI_DOC_VOLUME o RIMUOVI_DOC_VOLUME non posso
                // visualizzare il dettaglio dell'unit\u00e0  documentaria
                if (!getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga).getString("ti_oper")
                        .equals("AGGIUNGI_DOC_VOLUME")
                        && !getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga).getString("ti_oper")
                                .equals("RIMUOVI_DOC_VOLUME")) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Impossibile visualizzare l'unit\u00e0 documentaria se tipo operazione diverso da AGGIUNGI_DOC_VOLUME e RIMUOVI_DOC_VOLUME"));
                } // controllo che l'id dell'unit\u00e0  documentaria non sia nullo
                else if (idUnitaDoc == null) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Errore nella visualizzazione dell'unit\u00e0 documentaria: identificativo nullo"));
                } // Altrimenti procedo a controllare i permessi
                else if (idStrut != null) {
                    // Ricavo il TableBean relativo ai tipi di unit\u00e0  doc in base alle abilitazioni
                    DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb
                            .getTipiUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
                    boolean abilitato = false;
                    if (tmpTableBeanTUD.size() != 0) {
                        for (int i = 0; i < tmpTableBeanTUD.size(); i++) {
                            DecTipoUnitaDocRowBean rb = tmpTableBeanTUD.getRow(i);
                            if (rb.getIdStrut().compareTo(idStrut) == 0) {
                                abilitato = true;
                            }
                        }
                    }

                    // Se il tipo unit\u00e0  documentaria non \u00e0¨ tra quelli abilitati per l'utente, non devo
                    // visualizzare il dettaglio unit\u00e0  documentaria
                    if (abilitato) {
                        unitaDocTB.add(udHelper.getAroVRicUnitaDocRowBean(idUnitaDoc, idStrut, null));
                        form.getUnitaDocumentarieList().setTable(unitaDocTB);
                        redirectToAction(Application.Actions.UNITA_DOCUMENTARIE,
                                "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW
                                        + "&table=" + UnitaDocumentarieForm.UnitaDocumentarieList.NAME + "&riga=0",
                                form);
                    } else {
                        getMessageBox().addMessage(new Message(MessageLevel.ERR,
                                "Utente non abilitato ad accedere al dettaglio dell'unit\u00e0 documentaria selezionata"));
                    }
                }
            } // Se ho cliccato sull'icona del "Visualizza volume" di Operazioni Volumi
            else if ((getRequest().getParameter("table").equals(getForm().getVolumeOutputList().getName()))) {
                // VISUALIZZA VOLUME DA OPERAZIONI VOLUMI
                VolumiForm form = new VolumiForm();
                VolVRicVolumeTableBean volumeTB = new VolVRicVolumeTableBean();
                Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
                BigDecimal idVol = getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga)
                        .getBigDecimal("id_volume_conserv");
                BigDecimal idStrut = getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga)
                        .getBigDecimal("id_strut");

                // Controllo se il tipo operazione \u00e0¨ uguale a ELIMINA_VOLUME
                if (getForm().getOutputAnaliticoCronologicoList().getTable().getRow(riga).getString("ti_oper")
                        .equals("ELIMINA_VOLUME")) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Impossibile visualizzare il dettaglio di un volume con tipo operazione uguale a ELIMINA_VOLUME"));
                }

                // Controllo che il volume esista
                VolVRicVolumeRowBean r = volumiHelper.findVolVRicVolume(idVol);
                if (r.getIdVolumeConserv() == null) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Impossibile visualizzare il dettaglio del volume poich\u00e0¨ lo stesso \u00e0¨ stato eliminato"));
                }

                if (!getMessageBox().hasError()) {
                    VolVRicVolumeRowBean row = new VolVRicVolumeRowBean();
                    row.setIdVolumeConserv(idVol);
                    row.setIdStrutVolume(idStrut);
                    volumeTB.add(row);
                    form.getVolumiList().setTable(volumeTB);
                    // Setto il pageSize ad almeno 1, in quanto il framework, per inserire i tasti Annulla e Salva nel
                    // dettaglio del volume
                    // controlla che il pageSize sia diverso da 0
                    form.getVolumiList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    redirectToAction(
                            Application.Actions.VOLUMI, "?operation=listNavigationOnClick&navigationEvent="
                                    + ListAction.NE_DETTAGLIO_VIEW + "&table=" + VolumiForm.VolumiList.NAME + "&riga=0",
                            form);
                }
            } // Se ho cliccato sull'icona del "Visualizza elenco di versamento" di Operazioni Elenchi di Versamento
            else if ((getRequest().getParameter("table").equals(getForm().getElencoVersamentoOutputList().getName()))) {
                // VISUALIZZA ELENCO DI VERSAMENTO DA LISTA OPERAZIONI ELENCHI
                ElenchiVersamentoForm form = new ElenchiVersamentoForm();
                ElvVRicElencoVersTableBean elencoVersTableBean = new ElvVRicElencoVersTableBean();
                Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
                BigDecimal idElencoVers = getForm().getOutputAnaliticoCronologicoListElenchi().getTable().getRow(riga)
                        .getBigDecimal("id_elenco_vers");
                String nmElenco = getForm().getOutputAnaliticoCronologicoListElenchi().getTable().getRow(riga)
                        .getString("nm_elenco");
                BigDecimal idStrut = getForm().getOutputAnaliticoCronologicoListElenchi().getTable().getRow(riga)
                        .getBigDecimal("id_strut");

                /* Controllo che l'elenco di versamento esista */
                ElvVRicElencoVersRowBean elencoRowBean = new ElvVRicElencoVersRowBean();
                elencoRowBean.setIdElencoVers(idElencoVers);
                if (getForm().getOutputAnaliticoCronologicoListElenchi().getTable().getRow(riga).getString("ti_oper")
                        .equals(ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name())) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Impossibile visualizzare il dettaglio dell'elenco di versamento poich\u00e8 il tipo operazione \u00e8 ELIMINA_ELENCO"));
                } else if (!evEjb.existNomeElenco(nmElenco, idStrut)) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR,
                            "Impossibile visualizzare il dettaglio dell'elenco di versamento poich\u00e8 lo stesso non \u00e8 più presente"));
                }

                if (!getMessageBox().hasError()) {
                    elencoVersTableBean.add(elencoRowBean);
                    form.getElenchiVersamentoList().setTable(elencoVersTableBean);
                    /*
                     * Setto il pageSize ad almeno 1, in quanto il framework, per inserire i tasti Annulla e Salva nel
                     * dettaglio dell'elenco controlla che il pageSize sia diverso da 0
                     */
                    form.getElenchiVersamentoList().getTable().setPageSize(WebConstants.DEFAULT_PAGE_SIZE);
                    redirectToAction(Application.Actions.ELENCHI_VERSAMENTO,
                            "?operation=listNavigationOnClick&navigationEvent=" + ListAction.NE_DETTAGLIO_VIEW
                                    + "&table=" + ElenchiVersamentoForm.ElenchiVersamentoList.NAME + "&riga=0",
                            form);
                }
            } // Se ho cliccato sul dettaglio di un Versamento Fallito
            else if ((getRequest().getParameter("table").equals(getForm().getVersamentiFallitiList().getName()))) {
                // Setto tutti i parametri relativi a visualizzazione bottoni, clessidra e status
                getForm().getScaricaFileXMLButtonList().getScaricaFileXMLVersamento().setDisableHourGlass(true);
                if (getForm().getVersamentiFallitiList().getFl_sessione_err_verif().isReadonly()) {
                    getForm().getVersamentiFallitiList().setUserOperations(true, false, false, false);
                } else {
                    getForm().getVersamentiFallitiList().setUserOperations(true, true, true, false);
                }
                getForm().getVersamentiFallitiDetail().setViewMode();
                getForm().getVersamentiFallitiList().setStatus(Status.view);
                getForm().getVersamentiFallitiTabs().getInfoVersamento().setCurrent(true);
                getForm().getScaricaFileXMLButtonList().getScaricaFileXMLVersamento().setEditMode();
                forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
            } // Se ho cliccato sul dettaglio di una Sessione Errata
            else if ((getRequest().getParameter("table").equals(getForm().getSessioniErrateList().getName()))) {
                // Setto tutti i parametri relativi a visualizzazione bottoni, clessidra e status
                getForm().getScaricaFileXMLButtonList().getScaricaFileXMLSessione().setDisableHourGlass(true);
                getForm().getSessioniErrateList().setUserOperations(true, true, true, false);
                getForm().getSessioniErrateDetail().setViewMode();
                getForm().getSessioniErrateList().setStatus(Status.view);
                getForm().getSessioniErrateTabs().getInfoSessione().setCurrent(true);
                getForm().getScaricaFileXMLButtonList().getScaricaFileXMLSessione().setEditMode();
                forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
            } // Se ho cliccato sul dettaglio di un'Unit\u00e0  doc. / Documento Non Versato
            else if ((getRequest().getParameter("table")
                    .equals(getForm().getDocumentiDerivantiDaVersFallitiList().getName()))) {
                // Setto tutti i parametri relativi a visualizzazione bottoni, clessidra e status
                getForm().getDocumentiDerivantiDaVersFallitiList().setUserOperations(true, true, true, false);
                getForm().getDocumentiDerivantiDaVersFallitiDetail().setViewMode();
                getForm().getDocumentiDerivantiDaVersFallitiList().setStatus(Status.view);
                getForm().getSalvaVerificaButtonList().getSalvaVerificaVersamentoDaDocDerVersFalliti().setEditMode();
                forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_DETAIL);
            } // Se ho cliccato su una Sessione della Lista Sessioni nel dettaglio di un'unit\u00e0  doc. / documento
              // non versato
            else if ((getRequest().getParameter("table").equals(getForm().getSessioniList().getName()))) {
                // VISUALIZZA VERSAMENTO FALLITO DA LISTA SESSIONI (STESSA ACTION)
                // Setto tutti i parametri relativi a visualizzazione bottoni, clessidra e status
                getForm().getScaricaFileXMLButtonList().getScaricaFileXMLVersamento().setDisableHourGlass(true);
                getForm().getSessioniList().setUserOperations(true, true, true, false);
                getForm().getVersamentiFallitiDetail().setViewMode();
                getForm().getSessioniList().setStatus(Status.view);
                getForm().getVersamentiFallitiTabs().getInfoVersamento().setCurrent(true);
                getForm().getScaricaFileXMLButtonList().getScaricaFileXMLVersamento().setEditMode();
                forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
            }
        }
    }

    public void getListaElenchiVersamentiFirma() {
        // VISUALIZZA LISTA ELENCHI DI VERSAMENTO DA FIRMARE DA RIEPILOGO PER STRUTTURA
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        BigDecimal idStrut = getForm().getRiepilogoStrutturaList().getTable().getRow(riga).getBigDecimal("id_strut");
        ElenchiVersamentoForm form = new ElenchiVersamentoForm();
        redirectToAction(Application.Actions.ELENCHI_VERSAMENTO,
                "?operation=loadListaElenchiVersamentoDaFirmare&riga=0&idStrut=" + idStrut, form);
    }

    /**
     * Caricamento dei dettagli di ogni pagina del Monitoraggio
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void loadDettaglio() throws EMFError {
        String lista = getRequest().getParameter("table");
        if (lista != null) {
            // Se ho cliccato sul dettaglio di un VERSAMENTO FALLITO
            if ((lista.equals(getForm().getVersamentiFallitiList().getName()))
                    || (lista.equals(getForm().getSessioniList().getName()))) {

                BigDecimal idSessioneVers = null;
                // Ottengo l'id sessione a seconda della provenienza
                // (VersamentiFallitiList o SessioniList (che son sempre versamenti...)
                if (lista.equals(getForm().getVersamentiFallitiList().getName())) {
                    idSessioneVers = getMonVLisVersErrRowBean().getIdSessioneVers();
                } else {
                    // Modifica LUCA : mi trovo in SessioniList, che viene popolata con due tipi diversi di table bean
                    idSessioneVers = getForm().getSessioniList().getTable().getCurrentRow()
                            .getBigDecimal("id_sessione_vers");
                }
                // Salvo l'id sessione in sessione
                getSession().setAttribute("idSessioneVers", idSessioneVers);
                // Carico il rowbean corrispondente all'id ottenuto
                MonVVisVersErrIamRowBean versErrRB = monitoraggioHelper.getMonVVisVersErrIamRowBean(idSessioneVers);
                // Copio nella form di dettaglio i dati del rowbean
                getForm().getVersamentiFallitiDetail().copyFromBean(versErrRB);
                // Formatto gli xml di richiesta e risposta in modo tale che compaiano on-line in versione
                // "pretty-print"

                String xmlrich = versErrRB.getBlXmlRich();
                String xmlindex = versErrRB.getBlXmlIndex();
                String xmlrisp = versErrRB.getBlXmlRisp();

                // Nascondo di default i tab XML
                getForm().getVersamentiFallitiTabs().getVersamentoXMLRich().setHidden(true);
                getForm().getVersamentiFallitiTabs().getVersamentoXMLRisp().setHidden(true);
                getForm().getVersamentiFallitiTabs().getVersamentoXMLIndex().setHidden(true);
                if (xmlrich != null && xmlrisp != null) {
                    XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                    xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
                    xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
                    getForm().getVersamentiFallitiDetail().getBl_xml_rich().setValue(xmlrich);
                    getForm().getVersamentiFallitiDetail().getBl_xml_risp().setValue(xmlrisp);
                    // show
                    getForm().getVersamentiFallitiTabs().getVersamentoXMLRich().setHidden(false);
                    getForm().getVersamentiFallitiTabs().getVersamentoXMLRisp().setHidden(false);
                    if (xmlindex != null) {
                        xmlindex = formatter.prettyPrintWithDOM3LS(xmlindex);
                        getForm().getVersamentiFallitiDetail().getBl_xml_index().setValue(xmlindex);
                        getForm().getVersamentiFallitiTabs().getVersamentoXMLIndex().setHidden(false);
                    }
                }
                // Carico la lista file
                VrsFileSessioneKoTableBean fileListTB = monitoraggioHelper.getFileListTableBean(idSessioneVers);
                getForm().getFileList().setTable(fileListTB);
                getForm().getFileList().getTable().setPageSize(10);
                getForm().getFileList().getTable().first();
            } // Se ho cliccato sul dettaglio di una SESSIONE ERRATA
            else if ((lista.equals(getForm().getSessioniErrateList().getName()))) {
                // Ottengo l'id sessione e lo salvo in getSession()
                BigDecimal idSessioneVers = getVrsSessioneVersRowBean().getIdSessioneVers();
                getSession().setAttribute("idSessioneVers", idSessioneVers);
                // Carico il rowbean corrispondente all'id ottenuto
                MonVVisSesErrIamRowBean sesErrRB = monitoraggioHelper.getMonVVisSesErrIamRowBean(idSessioneVers);
                // Copio nella form di dettaglio i dati del rowbean
                getForm().getSessioniErrateDetail().copyFromBean(sesErrRB);
                // Formatto gli xml di richiesta e risposta in modo tale che compaiano on-line in versione
                // "pretty-print"

                String xmlrich = sesErrRB.getBlXmlRich();
                String xmlindex = sesErrRB.getBlXmlIndex();
                String xmlrisp = sesErrRB.getBlXmlRisp();

                // Nascondo di default il tab sull'XML di indice
                getForm().getSessioniErrateTabs().getSessioneXMLIndex().setHidden(true);
                if (xmlrich != null && xmlrisp != null) {
                    XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                    xmlrich = formatter.prettyPrintWithDOM3LS(xmlrich);
                    xmlrisp = formatter.prettyPrintWithDOM3LS(xmlrisp);
                    getForm().getSessioniErrateDetail().getBl_xml_rich().setValue(xmlrich);
                    getForm().getSessioniErrateDetail().getBl_xml_risp().setValue(xmlrisp);
                    if (xmlindex != null) {
                        xmlindex = formatter.prettyPrintWithDOM3LS(xmlindex);
                        getForm().getSessioniErrateDetail().getBl_xml_index().setValue(xmlindex);
                        getForm().getSessioniErrateTabs().getSessioneXMLIndex().setHidden(false);
                    }
                }
                // Carico la lista file
                VrsFileSessioneKoTableBean fileListTB = monitoraggioHelper.getFileListTableBean(idSessioneVers);
                getForm().getFileList().setTable(fileListTB);
                getForm().getFileList().getTable().setPageSize(10);
                getForm().getFileList().getTable().first();
            } // Se ho cliccato sul dettaglio di una DOCUMENTO DERIVANTE DA VERSAMENTI FALLITI
            else if ((lista.equals(getForm().getDocumentiDerivantiDaVersFallitiList().getName()))) {
                // Carico il rowbean corrispondente al documento non versato
                BigDecimal idStrut = getForm().getDocumentiDerivantiDaVersFallitiList().getTable().getCurrentRow()
                        .getBigDecimal("id_strut");
                String registro = getForm().getDocumentiDerivantiDaVersFallitiList().getTable().getCurrentRow()
                        .getString("cd_registro_key_unita_doc");
                BigDecimal anno = getForm().getDocumentiDerivantiDaVersFallitiList().getTable().getCurrentRow()
                        .getBigDecimal("aa_key_unita_doc");
                String numero = getForm().getDocumentiDerivantiDaVersFallitiList().getTable().getCurrentRow()
                        .getString("cd_key_unita_doc");
                String docVers = null;
                // Se ho cliccato su un'Unit\u00e0  Documentaria
                AbstractBaseTable<?> sessioniListTB;
                String tipoVers;
                if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse().equals("UNITA_DOC")) {
                    MonVVisUdNonVersRowBean nonVersRB = monitoraggioHelper.getMonVVisUdNonVersRowBean(idStrut, registro,
                            anno, numero);
                    // Copio nella form di dettaglio i dati del rowbean
                    getForm().getDocumentiDerivantiDaVersFallitiDetail().copyFromBean(nonVersRB);
                    getForm().getDocumentiDerivantiDaVersFallitiDetail().getTipo_versamento().setValue("VERSAMENTO");

                    tipoVers = getForm().getDocumentiDerivantiDaVersFallitiDetail().getTipo_versamento().getValue();

                    sessioniListTB = monitoraggioHelper.getMonVLisVersUdNonVersViewBean(idStrut, registro, anno,
                            numero);
                } // Se ho cliccato su un Documento Aggiunto
                else {
                    docVers = getForm().getDocumentiDerivantiDaVersFallitiList().getTable().getCurrentRow()
                            .getString("cd_key_doc_vers");
                    MonVVisDocNonVersRowBean nonVersRB = monitoraggioHelper.getMonVVisDocNonVersRowBean(idStrut,
                            registro, anno, numero, docVers);
                    // Copio nella form di dettaglio i dati del rowbean
                    getForm().getDocumentiDerivantiDaVersFallitiDetail().copyFromBean(nonVersRB);
                    getForm().getDocumentiDerivantiDaVersFallitiDetail().getTipo_versamento()
                            .setValue("AGGIUNGI_DOCUMENTO");

                    tipoVers = getForm().getDocumentiDerivantiDaVersFallitiDetail().getTipo_versamento().getValue();

                    sessioniListTB = monitoraggioHelper.getMonVLisVersDocNonVersViewBean(idStrut, registro, anno,
                            numero, docVers);
                }

                // Carico la lista sessioni (che sono dei versamenti falliti)
                getForm().getSessioniList().setTable(sessioniListTB);
                getForm().getSessioniList().getTable().setPageSize(10);
                getForm().getSessioniList().getTable().first();

                // Salvo in sessione questi valori relativi all'unit\u00e0  documentaria / documento
                // Mi serviranno in fase di "ritorno" da Dettaglio Versamento Fallito a Dettaglio Unit\u00e0  Doc. /
                // Documento non versato
                MonitoraggioAttributiVersFallitiDaDocNonVersati mon = new MonitoraggioAttributiVersFallitiDaDocNonVersati();
                mon.setIdStrut(idStrut);
                mon.setCdRegistroKeyUnitaDoc(registro);
                mon.setAaKeyUnitaDoc(anno);
                mon.setCdKeyUnitaDoc(numero);
                mon.setCdKeyDocVers(docVers);
                mon.setTipoVers(tipoVers);
                getSession().setAttribute("attributiVersFallitiDaDocNonVersati", mon);
            }
        }
    }

    /**
     * Metodo invocato in dettaglio Versamenti Falliti o Sessioni Errate quando si conferma la modifica del valore del
     * flag riferito allo stato del versamento/sessione (verificato o meno)
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void saveDettaglio() throws EMFError {
        // Se mi trovo nel dettaglio di una SESSIONE ERRATA
        if (getSession().getAttribute("provenienza").equals("SE")) {
            // Ricavo l'id della sessione errata
            BigDecimal idSes = ((VrsSessioneVersKoRowBean) getForm().getSessioniErrateList().getTable().getCurrentRow())
                    .getIdSessioneVers();

            /*
             * Salvo i valori dei campi del dettaglio sessione errata tra cui il flaggozzo modificato
             */
            getForm().getSessioniErrateDetail().post(getRequest());
            String[] verificato = getRequest().getParameterValues("Fl_sessione_err_verif");
            String flaggozzo = "0";
            // Se != da null, significa che ho spuntato il flag
            if (verificato != null) {
                flaggozzo = "1";
            }

            // Imposto il flaggozzo nel front-end (in pratica ne faccio il post)
            getForm().getSessioniErrateDetail().getFl_sessione_err_verif().setChecked(flaggozzo.equals("1"));

            try {
                // Valida i campi sulla correttezza formale
                if (getForm().getSessioniErrateDetail().validate(getMessageBox())) {
                    /*
                     * Controllo che, se ho inserito un ambiente ed un ente per modificare l'id strut in
                     * VrsSessioneVers, sia valorizzato anche il campo struttura
                     */
                    String nmAmbientePerCalcolo = getForm().getSessioniErrateDetail().getId_ambiente().getValue()
                            .equals("") ? null : getForm().getSessioniErrateDetail().getId_ambiente().getDecodedValue();
                    String nmEntePerCalcolo = getForm().getSessioniErrateDetail().getId_ente().getValue().equals("")
                            ? null : getForm().getSessioniErrateDetail().getId_ente().getDecodedValue();
                    String nmStrutPerCalcolo = getForm().getSessioniErrateDetail().getId_strut().getValue().equals("")
                            ? null : getForm().getSessioniErrateDetail().getId_strut().getDecodedValue();
                    BigDecimal idStrutPerCalcolo = getForm().getSessioniErrateDetail().getId_strut().parse();
                    if (nmStrutPerCalcolo == null && nmAmbientePerCalcolo != null) {
                        getMessageBox().addMessage(
                                new Message(MessageLevel.ERR, "ATTENZIONE: campi ente e/o struttura non valorizzati"));
                    }

                    // Ricavo la chiave unit\u00e0  documentaria e la chiave documento
                    String registroUD = getForm().getSessioniErrateDetail().getCd_registro_key_unita_doc().getValue()
                            .equals("") ? null
                                    : getForm().getSessioniErrateDetail().getCd_registro_key_unita_doc().getValue();
                    BigDecimal annoUD = getForm().getSessioniErrateDetail().getAa_key_unita_doc().getValue().equals("")
                            ? null
                            : new BigDecimal(getForm().getSessioniErrateDetail().getAa_key_unita_doc().getValue());
                    String numUD = getForm().getSessioniErrateDetail().getCd_key_unita_doc().getValue().equals("")
                            ? null : getForm().getSessioniErrateDetail().getCd_key_unita_doc().getValue();
                    String chiaveDoc = getForm().getSessioniErrateDetail().getCd_key_doc_vers().getValue();

                    // Valida i campi di chiave unita doc e chiave doc
                    MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
                    validator.validaChiaveUnitaDoc(registroUD, annoUD, numUD);

                    if (!getMessageBox().hasError()) {
                        /*
                         * La validazione non ha riportato errori. Salvo il flaggozzo su DB e setto la checkbox spuntata
                         * o meno nel front-end
                         */
                        monitoraggioHelper.salvaDettaglio(idSes, flaggozzo, null, nmAmbientePerCalcolo,
                                nmEntePerCalcolo, nmStrutPerCalcolo, idStrutPerCalcolo, registroUD, annoUD, numUD,
                                chiaveDoc);
                        if (flaggozzo.equals("1")) {
                            getForm().getSessioniErrateDetail().getFl_sessione_err_verif().setChecked(true);
                        } else {
                            getForm().getSessioniErrateDetail().getFl_sessione_err_verif().setChecked(false);
                        }

                        // Rimetto in viewmode i campi
                        getForm().getSessioniErrateDetail().getFl_sessione_err_verif().setViewMode();
                        getForm().getSessioniErrateDetail().getCd_registro_key_unita_doc().setViewMode();
                        getForm().getSessioniErrateDetail().getAa_key_unita_doc().setViewMode();
                        getForm().getSessioniErrateDetail().getCd_key_unita_doc().setViewMode();
                        getForm().getSessioniErrateDetail().getId_ambiente().setViewMode();
                        getForm().getSessioniErrateDetail().getId_ente().setViewMode();
                        getForm().getSessioniErrateDetail().getId_strut().setViewMode();
                        getForm().getSessioniErrateList().setStatus(Status.view);

                        /*
                         * Rieseguo la query per avere la lista Sessioni Errate tenuto conto delle modifiche apportate
                         * (flaggozzo, oppure ho inserito ambiente, ente e struttura e dunque ho trasformato la sessione
                         * errata in un versamento fallito)
                         */
                        int inizio = getForm().getSessioniErrateList().getTable().getFirstRowPageIndex();
                        int pageSize = getForm().getSessioniErrateList().getTable().getPageSize();
                        int paginaCorrente = getForm().getSessioniErrateList().getTable().getCurrentPageIndex();
                        getForm().getSessioniErrateList().getFl_sessione_err_verif().setEditMode();
                        String maxResultSessioniErrate = configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_SESSIONI_ERRATE);
                        getForm().getSessioniErrateList().setTable(monitoraggioHelper.getSessioniErrateListTB(null,
                                Integer.parseInt(maxResultSessioniErrate)));
                        getForm().getSessioniErrateList().getTable().setPageSize(pageSize);
                        // rieseguo la query se necessario
                        this.lazyLoadGoPage(getForm().getSessioniErrateList(), paginaCorrente);
                        // ritorno alla pagina
                        getForm().getSessioniErrateList().getTable().setCurrentRowIndex(inizio);
                        getForm().getSessioniErrateList().setUserOperations(true, true, true, false);
                        // Segnalo l'avvenuta modifica andata a buon fine
                        getMessageBox()
                                .addMessage(new Message(MessageLevel.INF, "Aggiornamento effettuato con successo"));
                        getMessageBox().setViewMode(ViewMode.plain);

                        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
                    }
                }
            } catch (Exception e) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getMessage()));
            }
        } // Se mi trovo nel dettaglio di un VERSAMENTO FALLITO
        else if (getSession().getAttribute("provenienza").equals("VFdaVFList")
                || getSession().getAttribute("provenienza").equals("VFdaSessioniList")) {
            // Ricavo l'id del versamento fallito a seconda della lista dalla quale provengo (VersamentiFallitiList o
            // SessioniList)
            BigDecimal idSes = null;
            if (getSession().getAttribute("provenienza").equals("VFdaVFList")) {
                idSes = ((MonVLisVersErrIamRowBean) getForm().getVersamentiFallitiList().getTable().getCurrentRow())
                        .getIdSessioneVers();
            }

            if (getSession().getAttribute("provenienza").equals("VFdaSessioniList")) {
                idSes = ((MonVLisVersUdNonVersRowBean) getForm().getSessioniList().getTable().getCurrentRow())
                        .getIdSessioneVers();
            }

            /*
             * Salvo i valori dei campi del dettaglio versamento fallito tra cui i flaggozzi modificati (che sono gli
             * unici campi che posso modificare)
             */
            getForm().getVersamentiFallitiDetail().post(getRequest());
            String[] verificato = getRequest().getParameterValues("Fl_sessione_err_verif");
            String[] nonRisolubile = getRequest().getParameterValues("Fl_sessione_err_non_risolub");

            String flaggozzoVerificato = "0";
            String flaggozzoNonRisolubile = "0";
            // Se != da null, significa che ho spuntato il flag
            if (verificato != null) {
                flaggozzoVerificato = "1";
            }
            if (nonRisolubile != null) {
                flaggozzoNonRisolubile = "1";
            }

            // Imposto i flaggozzi nel front-end (in pratica ne faccio il post)
            getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif()
                    .setChecked(flaggozzoVerificato.equals("1"));
            getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub()
                    .setChecked(flaggozzoNonRisolubile.equals("1"));

            try {
                // Valida i campi sulla correttezza formale
                if (getForm().getVersamentiFallitiDetail().validate(getMessageBox())) {
                    // Ricavo la chiave unit\u00e0  documentaria e la chiave documento
                    String registroUD = getForm().getVersamentiFallitiDetail().getCd_registro_key_unita_doc().getValue()
                            .equals("") ? null
                                    : getForm().getVersamentiFallitiDetail().getCd_registro_key_unita_doc().getValue();
                    BigDecimal annoUD = getForm().getVersamentiFallitiDetail().getAa_key_unita_doc().getValue()
                            .equals("") ? null
                                    : new BigDecimal(
                                            getForm().getVersamentiFallitiDetail().getAa_key_unita_doc().getValue());
                    String numUD = getForm().getVersamentiFallitiDetail().getCd_key_unita_doc().getValue().equals("")
                            ? null : getForm().getVersamentiFallitiDetail().getCd_key_unita_doc().getValue();
                    String chiaveDoc = getForm().getVersamentiFallitiDetail().getCd_key_doc_vers().getValue().equals("")
                            ? null : getForm().getVersamentiFallitiDetail().getCd_key_doc_vers().getValue();

                    // Valida i campi di chiave unita doc e chiave doc
                    MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
                    validator.validaChiaveUnitaDoc(registroUD, annoUD, numUD);
                    validator.validaFlagVerificatoNonRisolubile(flaggozzoVerificato, flaggozzoNonRisolubile);

                    if (!getMessageBox().hasError()) {
                        /*
                         * La validazione non ha riportato errori. Salvo i flaggozzi su DB e setto le checkbox spuntata
                         * o meno nel front-end
                         */
                        monitoraggioHelper.salvaDettaglio(idSes, flaggozzoVerificato, flaggozzoNonRisolubile, null,
                                null, null, null, registroUD, annoUD, numUD, chiaveDoc);
                        if (flaggozzoVerificato.equals("1")) {
                            getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif().setChecked(true);
                        } else {
                            getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif().setChecked(false);
                        }

                        if (flaggozzoNonRisolubile.equals("1")) {
                            getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub().setChecked(true);
                        } else {
                            getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub().setChecked(false);
                        }

                        // Rimetto in viewmode i flaggozzi
                        getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif().setViewMode();
                        getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub().setViewMode();

                        if (getRequest().getParameter("table") != null && getRequest().getParameter("table")
                                .equals(getForm().getVersamentiFallitiList().getName())) {
                            getForm().getVersamentiFallitiList().setStatus(Status.view);
                            getForm().getVersamentiFallitiList().setUserOperations(true, true, true, false);
                        } else {
                            getForm().getSessioniList().setStatus(Status.view);
                            getForm().getSessioniList().setUserOperations(true, true, true, false);
                        }
                    }
                }
            } catch (Exception e) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getMessage()));
            }
            forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
        }
    }

    /**
     * Metodo richiamato alla pressione del tasto Annulla durante le operazioni di modifica ad un versamento fallito o
     * sessione errata
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void undoDettaglio() throws EMFError {
        if (getSession().getAttribute("provenienza") != null) {
            // Se sono nel dettaglio Sessioni Errate e annullo la modifica del flag verifica sessioni errate
            loadDettaglio();
            if (getSession().getAttribute("provenienza").equals("SE")) {
                getForm().getSessioniErrateDetail().setViewMode();
                getForm().getSessioniErrateList().setStatus(Status.view);
                forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
            } /*
               * Se sono nel dettaglio Versamenti Falliti e annullo la modifica dei flag verifica versamento fallito e
               * non risolubile (sia che provenga da Lista Versamenti Falliti sia che provenga da Lista Sessioni
               */ else {
                getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif().setViewMode();
                getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub().setViewMode();
                getForm().getVersamentiFallitiList().setStatus(Status.view);
                getForm().getSessioniList().setStatus(Status.view);
                forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
            }
        }
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Metodo utilizzato per richiamare la funzione goBack() al momento del click sul tasto "Indietro" delle barre di
     * navigazione delle pagine jsp
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void elencoOnClick() throws EMFError {
        // Controllo per ritorno in Riepilogo Versamenti dal dettaglio di un totale
        if (getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue() != null
                && !getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue().equals("")) {
            getRequest().setAttribute("tipoud", true);
        }
        goBack();
    }

    /**
     * Metodo che calcola tutti i totali della pagina Riepilogo Versamenti e li piazza nei relativi campi
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void riepilogoVersamentiButton() throws EMFError {
        // Eseguo la post dei filtri compilati
        getForm().getRiepilogoVersamenti().post(getRequest());
        BigDecimal idAmbiente = getForm().getRiepilogoVersamenti().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getRiepilogoVersamenti().getId_ente().parse();
        BigDecimal idStruttura = getForm().getRiepilogoVersamenti().getId_strut().parse();
        BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().parse();
        // Eseguo il calcolo dei totali
        calcolaTotaliRiepilogoVersamenti(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
    }

    public void calcolaTotaliRiepilogoVersamenti(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStruttura,
            BigDecimal idTipoUnitaDoc) {
        // Rendo visibili i totali
        visualizzaTotaliRiepilogoVersamenti();

        // Creazione variabili di appoggio per calcolare i totali
        Integer udVersateOggi = 0;
        Integer udVersateUltimi6 = 0;
        Integer udVersateUltimi7 = 0;
        Integer udVersatePrecUltimi6 = 0;
        Integer udVersateTot = 0;
        Integer docVersatiOggi = 0;
        Integer docVersatiUltimi6 = 0;
        Integer docVersatiUltimi7 = 0;
        Integer docVersatiPrecUltimi6 = 0;
        Integer docVersatiTot = 0;

        Integer udVersateOggiTot = 0;
        Integer udVersateUltimi7Tot = 0;
        Integer udVersateTotTot = 0;
        Integer docVersatiOggiTot = 0;
        Integer docVersatiUltimi7Tot = 0;
        Integer docVersatiTotTot = 0;

        Integer udVersateSuccessoOggi = 0;
        Integer udVersateSuccessoUltimi6 = 0;
        Integer udVersateSuccessoUltimi7 = 0;
        Integer udVersateSuccessoPrecUltimi6 = 0;
        Integer udVersateSuccessoTot = 0;
        Integer docVersatiSuccessoOggi = 0;
        Integer docVersatiSuccessoUltimi6 = 0;
        Integer docVersatiSuccessoUltimi7 = 0;
        Integer docVersatiSuccessoPrecUltimi6 = 0;
        Integer docVersatiSuccessoTot = 0;

        // Creo due array contenenti i valori possibili per stato documento e stato volume
        String[] statoDoc = { "IN_ATTESA_SCHED", "NON_SELEZ_SCHED", "IN_VOLUME_APERTO", "IN_VOLUME_IN_ERRORE",
                "IN_VOLUME_CHIUSO", "IN_VOLUME_DA_CHIUDERE", "IN_ATTESA_MEMORIZZAZIONE" };
        String[] statoVol = { "CHIUSO", "FIRMATO", "FIRMATO_NO_MARCA", "DA_VERIFICARE" };

        // Se non ci sono errori, comincia a calcolare i totali
        if (getForm().getRiepilogoVersamenti().validate(getMessageBox())) {

            // Ottengo i tablebean in base alla presenza o meno del filtro tipo unit\u00e0  documentaria
            if (idTipoUnitaDoc != null) {
                AroVDocTiUdRangeDtTableBean contaDocUdTB = monitoraggioHelper.contaDocUd(getUser().getIdUtente(),
                        idTipoUnitaDoc, idAmbiente, idEnte, idStruttura);
                AroVDocVolTiUdRangeDtTableBean contaDocStatoVolUdTB = monitoraggioHelper
                        .contaDocStatoVolUd(getUser().getIdUtente(), idTipoUnitaDoc, idAmbiente, idEnte, idStruttura);

                // Calcolo i totali
                for (int i = 0; i < contaDocUdTB.size(); i++) {
                    AroVDocTiUdRangeDtRowBean rb = contaDocUdTB.getRow(i);
                    if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("CORR")) {
                        udVersateSuccessoOggi = udVersateSuccessoOggi
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        udVersateSuccessoUltimi6 = udVersateSuccessoUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")) {
                        udVersateSuccessoPrecUltimi6 = udVersateSuccessoPrecUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDtCreazione().equals("CORR")) {
                        docVersatiSuccessoOggi = docVersatiSuccessoOggi
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        docVersatiSuccessoUltimi6 = docVersatiSuccessoUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")) {
                        docVersatiSuccessoPrecUltimi6 = docVersatiSuccessoPrecUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }
                }

                for (int j = 0; j < statoDoc.length; j++) {
                    for (int i = 0; i < contaDocUdTB.size(); i++) {
                        AroVDocTiUdRangeDtRowBean rb = contaDocUdTB.getRow(i);
                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            udVersateOggi = udVersateOggi
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            udVersateUltimi6 = udVersateUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            udVersatePrecUltimi6 = udVersatePrecUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("CORR") && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            docVersatiOggi = docVersatiOggi
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("6_GG_PREC_CORR") && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            docVersatiUltimi6 = docVersatiUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            docVersatiPrecUltimi6 = docVersatiPrecUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }
                    }

                    udVersateUltimi7 = udVersateOggi + udVersateUltimi6;
                    docVersatiUltimi7 = docVersatiOggi + docVersatiUltimi6;
                    udVersateTot = udVersateOggi + udVersateUltimi6 + udVersatePrecUltimi6;
                    docVersatiTot = docVersatiOggi + docVersatiUltimi6 + docVersatiPrecUltimi6;

                    if (statoDoc[j].equals("IN_ATTESA_SCHED")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_corr().setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_7().setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_tot().setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("NON_SELEZ_SCHED")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_corr().setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_7().setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_corr().setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_7().setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_tot().setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_VOLUME_APERTO")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_VOLUME_IN_ERRORE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_VOLUME_DA_CHIUDERE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_ATTESA_MEMORIZZAZIONE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_tot()
                                .setValue(docVersatiTot.toString());
                    }

                    udVersateOggiTot = udVersateOggiTot + udVersateOggi;
                    udVersateUltimi7Tot = udVersateUltimi7Tot + udVersateUltimi7;
                    udVersateTotTot = udVersateTotTot + udVersateTot;
                    docVersatiOggiTot = docVersatiOggiTot + docVersatiOggi;
                    docVersatiUltimi7Tot = docVersatiUltimi7Tot + docVersatiUltimi7;
                    docVersatiTotTot = docVersatiTotTot + docVersatiTot;

                    udVersateOggi = 0;
                    udVersateUltimi6 = 0;
                    udVersateUltimi7 = 0;
                    udVersatePrecUltimi6 = 0;
                    udVersateTot = 0;
                    docVersatiOggi = 0;
                    docVersatiUltimi6 = 0;
                    docVersatiUltimi7 = 0;
                    docVersatiPrecUltimi6 = 0;
                    docVersatiTot = 0;
                }

                for (int k = 0; k < statoVol.length; k++) {
                    for (int l = 0; l < contaDocStatoVolUdTB.size(); l++) {
                        AroVDocVolTiUdRangeDtRowBean rb = contaDocStatoVolUdTB.getRow(l);
                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            udVersateOggi = udVersateOggi + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }
                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            udVersateUltimi6 = udVersateUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            udVersatePrecUltimi6 = udVersatePrecUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("CORR") && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            docVersatiOggi = docVersatiOggi + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            docVersatiUltimi6 = docVersatiUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            docVersatiPrecUltimi6 = docVersatiPrecUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }
                    }

                    udVersateUltimi7 = udVersateOggi + udVersateUltimi6;
                    docVersatiUltimi7 = docVersatiOggi + docVersatiUltimi6;
                    udVersateTot = udVersateOggi + udVersateUltimi6 + udVersatePrecUltimi6;
                    docVersatiTot = docVersatiOggi + docVersatiUltimi6 + docVersatiPrecUltimi6;

                    if (statoVol[k].equals("CHIUSO")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoVol[k].equals("FIRMATO")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_corr().setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_7().setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_tot().setValue(docVersatiTot.toString());
                    } else if (statoVol[k].equals("FIRMATO_NO_MARCA")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoVol[k].equals("DA_VERIFICARE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_tot()
                                .setValue(docVersatiTot.toString());
                    }

                    udVersateOggi = 0;
                    udVersateUltimi6 = 0;
                    udVersateUltimi7 = 0;
                    udVersatePrecUltimi6 = 0;
                    udVersateTot = 0;
                    docVersatiOggi = 0;
                    docVersatiUltimi6 = 0;
                    docVersatiUltimi7 = 0;
                    docVersatiPrecUltimi6 = 0;
                    docVersatiTot = 0;
                }
            } else {
                AroVDocRangeDtTableBean contaDocNoUdTB = monitoraggioHelper.contaDocNoUd(getUser().getIdUtente(),
                        idAmbiente, idEnte, idStruttura);
                AroVDocVolRangeDtTableBean contaDocStatoVolNoUdTB = monitoraggioHelper
                        .contaDocStatoVolNoUd(getUser().getIdUtente(), idAmbiente, idEnte, idStruttura);

                // Calcolo i totali
                for (int i = 0; i < contaDocNoUdTB.size(); i++) {
                    AroVDocRangeDtRowBean rb = contaDocNoUdTB.getRow(i);
                    if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("CORR")) {
                        udVersateSuccessoOggi = udVersateSuccessoOggi
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        udVersateSuccessoUltimi6 = udVersateSuccessoUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")) {
                        udVersateSuccessoPrecUltimi6 = udVersateSuccessoPrecUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }
                    if (rb.getTiDtCreazione().equals("CORR")) {
                        docVersatiSuccessoOggi = docVersatiSuccessoOggi
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        docVersatiSuccessoUltimi6 = docVersatiSuccessoUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }

                    if (rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")) {
                        docVersatiSuccessoPrecUltimi6 = docVersatiSuccessoPrecUltimi6
                                + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                    }
                }

                for (int j = 0; j < statoDoc.length; j++) {
                    for (int i = 0; i < contaDocNoUdTB.size(); i++) {
                        AroVDocRangeDtRowBean rb = contaDocNoUdTB.getRow(i);

                        // Calcolo in base allo stato del documento
                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            udVersateOggi = udVersateOggi
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            udVersateUltimi6 = udVersateUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            udVersatePrecUltimi6 = udVersatePrecUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("CORR") && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            docVersatiOggi = docVersatiOggi
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("6_GG_PREC_CORR") && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            docVersatiUltimi6 = docVersatiUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoDoc().equals(statoDoc[j])) {
                            docVersatiPrecUltimi6 = docVersatiPrecUltimi6
                                    + (rb.getString("ni_doc") != null ? Integer.parseInt(rb.getString("ni_doc")) : 0);
                        }
                    }

                    udVersateUltimi7 = udVersateOggi + udVersateUltimi6;
                    docVersatiUltimi7 = docVersatiOggi + docVersatiUltimi6;
                    udVersateTot = udVersateOggi + udVersateUltimi6 + udVersatePrecUltimi6;
                    docVersatiTot = docVersatiOggi + docVersatiUltimi6 + docVersatiPrecUltimi6;

                    if (statoDoc[j].equals("IN_ATTESA_SCHED")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_corr().setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_7().setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_tot().setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_ATTESA_MEMORIZZAZIONE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("NON_SELEZ_SCHED")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_corr().setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_7().setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_corr().setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_7().setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_tot().setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_VOLUME_APERTO")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_VOLUME_IN_ERRORE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoDoc[j].equals("IN_VOLUME_DA_CHIUDERE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_tot()
                                .setValue(docVersatiTot.toString());
                    }

                    udVersateOggiTot = udVersateOggiTot + udVersateOggi;
                    udVersateUltimi7Tot = udVersateUltimi7Tot + udVersateUltimi7;
                    udVersateTotTot = udVersateTotTot + udVersateTot;
                    docVersatiOggiTot = docVersatiOggiTot + docVersatiOggi;
                    docVersatiUltimi7Tot = docVersatiUltimi7Tot + docVersatiUltimi7;
                    docVersatiTotTot = docVersatiTotTot + docVersatiTot;

                    udVersateOggi = 0;
                    udVersateUltimi6 = 0;
                    udVersateUltimi7 = 0;
                    udVersatePrecUltimi6 = 0;
                    udVersateTot = 0;
                    docVersatiOggi = 0;
                    docVersatiUltimi6 = 0;
                    docVersatiUltimi7 = 0;
                    docVersatiPrecUltimi6 = 0;
                    docVersatiTot = 0;
                }

                for (int k = 0; k < statoVol.length; k++) {
                    for (int l = 0; l < contaDocStatoVolNoUdTB.size(); l++) {
                        AroVDocVolRangeDtRowBean rb = contaDocStatoVolNoUdTB.getRow(l);
                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            udVersateOggi = udVersateOggi + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }
                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            udVersateUltimi6 = udVersateUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDoc().equals("PRINCIPALE") && rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            udVersatePrecUltimi6 = udVersatePrecUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("CORR") && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            docVersatiOggi = docVersatiOggi + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            docVersatiUltimi6 = docVersatiUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }

                        if (rb.getTiDtCreazione().equals("PREC_6_GG_PREC_CORR")
                                && rb.getTiStatoVolumeConserv().equals(statoVol[k])) {
                            docVersatiPrecUltimi6 = docVersatiPrecUltimi6 + (rb.getString("ni_doc_chiuso") != null
                                    ? Integer.parseInt(rb.getString("ni_doc_chiuso")) : 0);
                        }
                    }

                    udVersateUltimi7 = udVersateOggi + udVersateUltimi6;
                    docVersatiUltimi7 = docVersatiOggi + docVersatiUltimi6;
                    udVersateTot = udVersateOggi + udVersateUltimi6 + udVersatePrecUltimi6;
                    docVersatiTot = docVersatiOggi + docVersatiUltimi6 + docVersatiPrecUltimi6;

                    if (statoVol[k].equals("CHIUSO")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoVol[k].equals("FIRMATO")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_corr().setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_7().setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_tot().setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_tot().setValue(docVersatiTot.toString());
                    } else if (statoVol[k].equals("FIRMATO_NO_MARCA")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_tot()
                                .setValue(docVersatiTot.toString());
                    } else if (statoVol[k].equals("DA_VERIFICARE")) {
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_corr()
                                .setValue(udVersateOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_7()
                                .setValue(udVersateUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_tot()
                                .setValue(udVersateTot.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_corr()
                                .setValue(docVersatiOggi.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_7()
                                .setValue(docVersatiUltimi7.toString());
                        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_tot()
                                .setValue(docVersatiTot.toString());
                    }

                    udVersateOggi = 0;
                    udVersateUltimi6 = 0;
                    udVersateUltimi7 = 0;
                    udVersatePrecUltimi6 = 0;
                    udVersateTot = 0;
                    docVersatiOggi = 0;
                    docVersatiUltimi6 = 0;
                    docVersatiUltimi7 = 0;
                    docVersatiPrecUltimi6 = 0;
                    docVersatiTot = 0;
                }
            }

            // Gestisco il monitoraggio delle sessioni
            VrsVSessioneVersRisoltaTableBean contaSesVrsTB = monitoraggioHelper
                    .contaSessioniVersRisVer(getUser().getIdUtente(), idAmbiente, idEnte, idStruttura);
            VrsVSessioneAggRisoltaTableBean contaSesAggTB = monitoraggioHelper
                    .contaSessioniAggRisVer(getUser().getIdUtente(), idAmbiente, idEnte, idStruttura);

            Integer sesRisVerCorr = 0;
            Integer sesRisVer6 = 0;
            Integer sesRisVerPrec6 = 0;
            Integer sesRisNoVerCorr = 0;
            Integer sesRisNoVer6 = 0;
            Integer sesRisNoVerPrec6 = 0;
            Integer sesNoRisVerCorr = 0;
            Integer sesNoRisVer6 = 0;
            Integer sesNoRisVerPrec6 = 0;
            Integer sesNoRisNoVerCorr = 0;
            Integer sesNoRisNoVer6 = 0;
            Integer sesNoRisNoVerPrec6 = 0;
            Integer sesRisVer7 = 0;
            Integer sesRisNoVer7 = 0;
            Integer sesNoRisVer7 = 0;
            Integer sesNoRisNoVer7 = 0;
            Integer sesRisVerTot = 0;
            Integer sesRisNoVerTot = 0;
            Integer sesNoRisVerTot = 0;
            Integer sesNoRisNoVerTot = 0;
            Integer sesNonRisolubCorr = 0;
            Integer sesNonRisolub6 = 0;
            Integer sesNonRisolubPrec6 = 0;
            Integer sesNonRisolub7 = 0;
            Integer sesNonRisolubTot = 0;
            Integer totCorr = 0;
            Integer tot6 = 0;
            Integer totPrec6 = 0;

            for (int i = 0; i < contaSesVrsTB.size(); i++) {
                VrsVSessioneVersRisoltaRowBean rb = contaSesVrsTB.getRow(i);

                // Calcolo totali falliti
                if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                    totCorr = totCorr + Integer.parseInt(rb.getString("ni_ses_vers"));
                } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                    tot6 = tot6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                } else {
                    totPrec6 = totPrec6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                }

                // Risolta
                if (rb.getFlSesRisolta() != null && rb.getFlSesRisolta().equals("1")) {
                    // Verificata
                    if (rb.getFlVerif() != null && rb.getFlVerif().equals("1")) {
                        if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                            sesRisVerCorr = sesRisVerCorr + Integer.parseInt(rb.getString("ni_ses_vers"));
                        } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                            sesRisVer6 = sesRisVer6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                        } else {
                            sesRisVerPrec6 = sesRisVerPrec6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                        }
                    } // Non verificata
                    else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                        sesRisNoVerCorr = sesRisNoVerCorr + Integer.parseInt(rb.getString("ni_ses_vers"));
                    } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        sesRisNoVer6 = sesRisNoVer6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                    } else {
                        sesRisNoVerPrec6 = sesRisNoVerPrec6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                    }
                } // il flag \u00e0¨ uguale a 0
                  // Non Risolta
                else {
                    // Verificata
                    if (rb.getFlVerif() != null && rb.getFlVerif().equals("1")) {
                        if (rb.getFlSesNonRisolub() != null && rb.getFlSesNonRisolub().equals("0")) {
                            if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                                sesNoRisVerCorr = sesNoRisVerCorr + Integer.parseInt(rb.getString("ni_ses_vers"));
                            } else if (rb.getTiDtCreazione() != null
                                    && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                                sesNoRisVer6 = sesNoRisVer6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                            } else {
                                sesNoRisVerPrec6 = sesNoRisVerPrec6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                            }
                        }
                    } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                        sesNoRisNoVerCorr = sesNoRisNoVerCorr + Integer.parseInt(rb.getString("ni_ses_vers"));
                    } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        sesNoRisNoVer6 = sesNoRisNoVer6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                    } else {
                        sesNoRisNoVerPrec6 = sesNoRisNoVerPrec6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                    }

                    // Conto i non risolubili
                    if (rb.getFlSesNonRisolub() != null && rb.getFlSesNonRisolub().equals("1")) {
                        if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                            sesNonRisolubCorr = sesNonRisolubCorr + Integer.parseInt(rb.getString("ni_ses_vers"));
                        } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                            sesNonRisolub6 = sesNonRisolub6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                        } else {
                            sesNonRisolubPrec6 = sesNonRisolubPrec6 + Integer.parseInt(rb.getString("ni_ses_vers"));
                        }
                    }
                }
            }

            sesRisVer7 = sesRisVerCorr + sesRisVer6;
            sesRisNoVer7 = sesRisNoVerCorr + sesRisNoVer6;
            sesNonRisolub7 = sesNonRisolubCorr + sesNonRisolub6;
            sesNoRisVer7 = sesNoRisVerCorr + sesNoRisVer6;
            sesNoRisNoVer7 = sesNoRisNoVerCorr + sesNoRisNoVer6;
            sesRisVerTot = sesRisVer7 + sesRisVerPrec6;
            sesRisNoVerTot = sesRisNoVer7 + sesRisNoVerPrec6;
            sesNoRisVerTot = sesNoRisVer7 + sesNoRisVerPrec6;
            sesNoRisNoVerTot = sesNoRisNoVer7 + sesNoRisNoVerPrec6;
            sesNonRisolubTot = sesNonRisolub7 + sesNonRisolubPrec6;

            getForm().getRiepilogoVersamenti().getSes_vrs_corr().setValue(Integer.toString(totCorr));
            getForm().getRiepilogoVersamenti().getSes_vrs_7().setValue(Integer.toString(totCorr + tot6));
            getForm().getRiepilogoVersamenti().getSes_vrs_tot().setValue(Integer.toString(totCorr + tot6 + totPrec6));
            getForm().getRiepilogoVersamenti().getSes_vrs_ris_ver_corr().setValue(sesRisVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_ris_ver_7().setValue(sesRisVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_ris_ver_tot().setValue(sesRisVerTot.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_ris_no_ver_corr().setValue(sesRisNoVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_ris_no_ver_7().setValue(sesRisNoVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_ris_no_ver_tot().setValue(sesRisNoVerTot.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_non_risolub_corr().setValue(sesNonRisolubCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_non_risolub_7().setValue(sesNonRisolub7.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_non_risolub_tot().setValue(sesNonRisolubTot.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_ver_corr().setValue(sesNoRisVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_ver_7().setValue(sesNoRisVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_ver_tot().setValue(sesNoRisVerTot.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_no_ver_corr().setValue(sesNoRisNoVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_no_ver_7().setValue(sesNoRisNoVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_no_ver_tot().setValue(sesNoRisNoVerTot.toString());

            sesRisVerCorr = 0;
            sesRisVer6 = 0;
            sesRisVerPrec6 = 0;
            sesRisNoVerCorr = 0;
            sesRisNoVer6 = 0;
            sesRisNoVerPrec6 = 0;
            sesNonRisolubCorr = 0;
            sesNonRisolub6 = 0;
            sesNonRisolubPrec6 = 0;
            sesNoRisVerCorr = 0;
            sesNoRisVer6 = 0;
            sesNoRisVerPrec6 = 0;
            sesNoRisNoVerCorr = 0;
            sesNoRisNoVer6 = 0;
            sesNoRisNoVerPrec6 = 0;
            totCorr = 0;
            tot6 = 0;
            totPrec6 = 0;

            for (int i = 0; i < contaSesAggTB.size(); i++) {
                VrsVSessioneAggRisoltaRowBean rb = contaSesAggTB.getRow(i);

                // Calcolo totali falliti
                if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                    totCorr = totCorr + Integer.parseInt(rb.getString("ni_ses_agg"));
                } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                    tot6 = tot6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                } else {
                    totPrec6 = totPrec6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                }

                // Risolta
                if (rb.getFlSesRisolta() != null && rb.getFlSesRisolta().equals("1")) {
                    // Verificata
                    if (rb.getFlVerif() != null && rb.getFlVerif().equals("1")) {
                        if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                            sesRisVerCorr = sesRisVerCorr + Integer.parseInt(rb.getString("ni_ses_agg"));
                        } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                            sesRisVer6 = sesRisVer6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                        } else {
                            sesRisVerPrec6 = sesRisVerPrec6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                        }
                    } // Non verificata
                    else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                        sesRisNoVerCorr = sesRisNoVerCorr + Integer.parseInt(rb.getString("ni_ses_agg"));
                    } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        sesRisNoVer6 = sesRisNoVer6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                    } else {
                        sesRisNoVerPrec6 = sesRisNoVerPrec6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                    }
                } // il flag \u00e0¨ uguale a 0
                  // Non Risolta
                else {
                    // Verificata
                    if (rb.getFlVerif() != null && rb.getFlVerif().equals("1")) {
                        if (rb.getFlSesNonRisolub() != null && rb.getFlSesNonRisolub().equals("0")) {
                            if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                                sesNoRisVerCorr = sesNoRisVerCorr + Integer.parseInt(rb.getString("ni_ses_agg"));
                            } else if (rb.getTiDtCreazione() != null
                                    && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                                sesNoRisVer6 = sesNoRisVer6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                            } else {
                                sesNoRisVerPrec6 = sesNoRisVerPrec6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                            }
                        }
                    } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                        sesNoRisNoVerCorr = sesNoRisNoVerCorr + Integer.parseInt(rb.getString("ni_ses_agg"));
                    } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                        sesNoRisNoVer6 = sesNoRisNoVer6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                    } else {
                        sesNoRisNoVerPrec6 = sesNoRisNoVerPrec6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                    }

                    // Conto i non risolubili
                    if (rb.getFlSesNonRisolub() != null && rb.getFlSesNonRisolub().equals("1")) {
                        if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("CORR")) {
                            sesNonRisolubCorr = sesNonRisolubCorr + Integer.parseInt(rb.getString("ni_ses_agg"));
                        } else if (rb.getTiDtCreazione() != null && rb.getTiDtCreazione().equals("6_GG_PREC_CORR")) {
                            sesNonRisolub6 = sesNonRisolub6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                        } else {
                            sesNonRisolubPrec6 = sesNonRisolubPrec6 + Integer.parseInt(rb.getString("ni_ses_agg"));
                        }
                    }
                }
            }

            sesRisVer7 = sesRisVerCorr + sesRisVer6;
            sesRisNoVer7 = sesRisNoVerCorr + sesRisNoVer6;
            sesNonRisolub7 = sesNonRisolubCorr + sesNonRisolub6;
            sesNoRisVer7 = sesNoRisVerCorr + sesNoRisVer6;
            sesNoRisNoVer7 = sesNoRisNoVerCorr + sesNoRisNoVer6;
            sesRisVerTot = sesRisVer7 + sesRisVerPrec6;
            sesRisNoVerTot = sesRisNoVer7 + sesRisNoVerPrec6;
            sesNoRisVerTot = sesNoRisVer7 + sesNoRisVerPrec6;
            sesNoRisNoVerTot = sesNoRisNoVer7 + sesNoRisNoVerPrec6;
            sesNonRisolubTot = sesNonRisolub7 + sesNonRisolubPrec6;

            getForm().getRiepilogoVersamenti().getSes_agg_corr().setValue(Integer.toString(totCorr));
            getForm().getRiepilogoVersamenti().getSes_agg_7().setValue(Integer.toString(totCorr + tot6));
            getForm().getRiepilogoVersamenti().getSes_agg_tot().setValue(Integer.toString(totCorr + tot6 + totPrec6));
            getForm().getRiepilogoVersamenti().getSes_agg_ris_ver_corr().setValue(sesRisVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_ris_ver_7().setValue(sesRisVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_ris_ver_tot().setValue(sesRisVerTot.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_ris_no_ver_corr().setValue(sesRisNoVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_ris_no_ver_7().setValue(sesRisNoVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_ris_no_ver_tot().setValue(sesRisNoVerTot.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_non_risolub_corr().setValue(sesNonRisolubCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_non_risolub_7().setValue(sesNonRisolub7.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_non_risolub_tot().setValue(sesNonRisolubTot.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_no_ris_ver_corr().setValue(sesNoRisVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_no_ris_ver_7().setValue(sesNoRisVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_no_ris_ver_tot().setValue(sesNoRisVerTot.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_no_ris_no_ver_corr().setValue(sesNoRisNoVerCorr.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_no_ris_no_ver_7().setValue(sesNoRisNoVer7.toString());
            getForm().getRiepilogoVersamenti().getSes_agg_no_ris_no_ver_tot().setValue(sesNoRisNoVerTot.toString());

            // Setto i parametri generali
            udVersateSuccessoUltimi7 = udVersateSuccessoOggi + udVersateSuccessoUltimi6;
            udVersateSuccessoTot = udVersateSuccessoOggi + udVersateSuccessoUltimi6 + udVersateSuccessoPrecUltimi6;
            docVersatiSuccessoUltimi7 = docVersatiSuccessoOggi + docVersatiSuccessoUltimi6;
            docVersatiSuccessoTot = docVersatiSuccessoOggi + docVersatiSuccessoUltimi6 + docVersatiSuccessoPrecUltimi6;
            getForm().getRiepilogoVersamenti().getNi_ud_corr().setValue(udVersateSuccessoOggi.toString());
            getForm().getRiepilogoVersamenti().getNi_ud_7().setValue(udVersateSuccessoUltimi7.toString());
            getForm().getRiepilogoVersamenti().getNi_ud_tot().setValue(udVersateSuccessoTot.toString());
            getForm().getRiepilogoVersamenti().getNi_doc_corr().setValue(docVersatiSuccessoOggi.toString());
            getForm().getRiepilogoVersamenti().getNi_doc_7().setValue(docVersatiSuccessoUltimi7.toString());
            getForm().getRiepilogoVersamenti().getNi_doc_tot().setValue(docVersatiSuccessoTot.toString());

            /*
             * ***********************************************************************
             */
            /* Gestisco il monitoraggio dei documenti derivanti da versamenti falliti */
            /*
             * ***********************************************************************
             */

            /* Versamenti falliti distinti per unit\u00e0  documentarie */
            Long distintiFalliti = 0L;
            Long distintiFallitiNonRisolub = 0L;
            Long distintiFallitiVerif = 0L;
            Long distintiFallitiNonVerif = 0L;
            List<Object[]> oList = monitoraggioHelper.contaVersFallitiDistintiUD(getUser().getIdUtente(), idAmbiente,
                    idEnte, idStruttura);
            for (Object[] o : oList) {
                if (((String) o[2]).equals("0") && ((String) o[3]) == null) {
                    distintiFallitiNonVerif += (Long) o[0];
                } else if (((String) o[2]).equals("1") && ((String) o[3]).equals("1")) {
                    distintiFallitiNonRisolub += (Long) o[0];
                } else if (((String) o[2]).equals("1") && ((String) o[3]).equals("0")) {
                    distintiFallitiVerif += (Long) o[0];
                }
                distintiFalliti += (Long) o[0];
            }

            getForm().getRiepilogoVersamenti().getVers_ud_falliti().setValue(distintiFalliti.toString());
            getForm().getRiepilogoVersamenti().getVers_ud_falliti_non_risolub()
                    .setValue(distintiFallitiNonRisolub.toString());
            getForm().getRiepilogoVersamenti().getVers_ud_falliti_verif().setValue(distintiFallitiVerif.toString());
            getForm().getRiepilogoVersamenti().getVers_ud_falliti_non_verif()
                    .setValue(distintiFallitiNonVerif.toString());

            /* Versamenti falliti distinti per documenti */
            distintiFalliti = 0L;
            distintiFallitiNonRisolub = 0L;
            distintiFallitiVerif = 0L;
            distintiFallitiNonVerif = 0L;
            oList = monitoraggioHelper.contaVersFallitiDistintiDoc(getUser().getIdUtente(), idAmbiente, idEnte,
                    idStruttura);
            for (Object[] o : oList) {
                if (((String) o[2]).equals("0") && ((String) o[3]) == null) {
                    distintiFallitiNonVerif += (Long) o[0];
                } else if (((String) o[2]).equals("1") && ((String) o[3]).equals("1")) {
                    distintiFallitiNonRisolub += (Long) o[0];
                } else if (((String) o[2]).equals("1") && ((String) o[3]).equals("0")) {
                    distintiFallitiVerif += (Long) o[0];
                }
                distintiFalliti += (Long) o[0];
            }

            getForm().getRiepilogoVersamenti().getVers_doc_falliti().setValue(distintiFalliti.toString());
            getForm().getRiepilogoVersamenti().getVers_doc_falliti_non_risolub()
                    .setValue(distintiFallitiNonRisolub.toString());
            getForm().getRiepilogoVersamenti().getVers_doc_falliti_verif().setValue(distintiFallitiVerif.toString());
            getForm().getRiepilogoVersamenti().getVers_doc_falliti_non_verif()
                    .setValue(distintiFallitiNonVerif.toString());

            /*
             * Salvo in request il parametro tipoud per gestire la visualizzazione di alcuni elementi in
             * monitoraggioRiepilogoVers.jsp
             */
            if (idTipoUnitaDoc != null) {
                getRequest().setAttribute("tipoud", true);
            }
        }
    }

    /**
     * Metodo invocato quando viene cliccato uno dei totali della pagina "Riepilogo Versamenti" e che mi rimanda
     *
     * @throws EMFError
     *             errore generico
     */
    public void monitoraggioListe() throws EMFError {
        // Inizializzo le liste fittizie nel caso si voglia visualizzare unit\u00e0  documentaria o volume
        getForm().getUnitaDocumentariaList().setTable(new AroVRicUnitaDocTableBean());
        getForm().getVolumeList().setTable(new VolVRicVolumeTableBean());

        // Mi creo i bean locali per gestire i filtri
        MonitoraggioFiltriListaDocBean filtriListaDoc = new MonitoraggioFiltriListaDocBean();
        MonitoraggioFiltriListaVersFallitiBean filtriSes = new MonitoraggioFiltriListaVersFallitiBean();
        MonitoraggioFiltriListaVersFallitiDistintiDocBean filtriListaVersFallitiDistintiDoc = new MonitoraggioFiltriListaVersFallitiDistintiDocBean();

        /*
         * Azzero i filtri delle pagine "Lista Documenti" e "Lista Versamenti Falliti" che sono quelle alle quali posso
         * arrivare
         */
        getForm().getFiltriVersamenti().reset();
        getForm().getFiltriVersamentiUlteriori().reset();
        getForm().getFiltriDocumenti().reset();
        getForm().getFiltriUdDocDerivantiDaVersFalliti().reset();
        getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().reset();
        getForm().getFiltriDocumentiAnnullati().reset();

        /*
         * Setto i filtri "generici" della pagina nella quale sto per essere ridirezionato per non eseguire i controlli
         * due volte
         */
        if (getForm().getRiepilogoVersamenti().getId_ambiente().getValue() != null
                && !getForm().getRiepilogoVersamenti().getId_ambiente().getValue().equals("")) {
            filtriListaDoc
                    .setIdAmbiente(new BigDecimal(getForm().getRiepilogoVersamenti().getId_ambiente().getValue()));
            filtriSes.setIdAmbiente(new BigDecimal(getForm().getRiepilogoVersamenti().getId_ambiente().getValue()));
            filtriListaVersFallitiDistintiDoc
                    .setIdAmbiente(new BigDecimal(getForm().getRiepilogoVersamenti().getId_ambiente().getValue()));
        }
        if (getForm().getRiepilogoVersamenti().getId_ente().getValue() != null
                && !getForm().getRiepilogoVersamenti().getId_ente().getValue().equals("")) {
            filtriListaDoc.setIdEnte(new BigDecimal(getForm().getRiepilogoVersamenti().getId_ente().getValue()));
            filtriSes.setIdEnte(new BigDecimal(getForm().getRiepilogoVersamenti().getId_ente().getValue()));
            filtriListaVersFallitiDistintiDoc
                    .setIdEnte(new BigDecimal(getForm().getRiepilogoVersamenti().getId_ente().getValue()));
        }
        if (getForm().getRiepilogoVersamenti().getId_strut().getValue() != null
                && !getForm().getRiepilogoVersamenti().getId_strut().getValue().equals("")) {
            filtriListaDoc.setIdStrut(new BigDecimal(getForm().getRiepilogoVersamenti().getId_strut().getValue()));
            filtriSes.setIdStrut(new BigDecimal(getForm().getRiepilogoVersamenti().getId_strut().getValue()));
            filtriListaVersFallitiDistintiDoc
                    .setIdStrut(new BigDecimal(getForm().getRiepilogoVersamenti().getId_strut().getValue()));
        }
        if (getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue() != null
                && !getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue().equals("")) {
            filtriListaDoc.setIdTipoUnitaDoc(
                    new BigDecimal(getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue()));
            filtriSes.setIdTipoUnitaDoc(
                    new BigDecimal(getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue()));
        }

        if (getRequest().getParameter("periodo") != null) {
            if (getRequest().getParameter("periodo").equals("OGGI")) {
                getForm().getFiltriVersamenti().getPeriodo_vers().setValue("Oggi");
                getForm().getFiltriDocumenti().getPeriodo_vers().setValue("Oggi");
            } else if (getRequest().getParameter("periodo").equals("ULTIMI7")) {
                getForm().getFiltriVersamenti().getPeriodo_vers().setValue("Ultimi 7 giorni");
                getForm().getFiltriDocumenti().getPeriodo_vers().setValue("Ultimi 7 giorni");
            } else {
                getForm().getFiltriVersamenti().getPeriodo_vers().setValue("Tutti");
                getForm().getFiltriDocumenti().getPeriodo_vers().setValue("Tutti");
            }
        }

        // Inizializzo le combo settando la struttura corrente
        ComboBox<BigDecimal> ambienteCombo = getForm().getRiepilogoVersamenti().getId_ambiente();
        DecodeMapIF mappaAmbiente = ambienteCombo.getDecodeMap();

        ComboBox<BigDecimal> enteCombo = getForm().getRiepilogoVersamenti().getId_ente();
        DecodeMapIF mappaEnte = enteCombo.getDecodeMap();

        ComboBox<BigDecimal> strutCombo = getForm().getRiepilogoVersamenti().getId_strut();
        DecodeMapIF mappaStrut = strutCombo.getDecodeMap();

        ComboBox<BigDecimal> tipoUnitaDocCombo = getForm().getRiepilogoVersamenti().getId_tipo_unita_doc();
        DecodeMapIF mappaUD = tipoUnitaDocCombo.getDecodeMap();

        // MOSTRA LISTA DOCUMENTI
        if (getRequest().getParameter("pagina").equals("1")) {
            // Setto diversi valori dei filtri presi come parametri passati dalla request
            filtriListaDoc.setTipoDoc(getRequest().getParameter("tipoDoc"));
            filtriListaDoc.setPeriodoVers(getRequest().getParameter("periodo"));
            if (getRequest().getParameter("statoDoc") != null) {
                filtriListaDoc.setStatoDoc(getRequest().getParameter("statoDoc"));
            }
            if (getRequest().getParameter("statoVol") != null) {
                filtriListaDoc.setStatoVol(getRequest().getParameter("statoVol"));
            }
            // Setto i permessi dell'utente
            filtriListaDoc.setIdUserIam(new BigDecimal(getUser().getIdUtente()));

            if (getRequest().getParameter("tiCreazione") != null) {
                Calendar calDa = Calendar.getInstance();
                Calendar calA = Calendar.getInstance();
                calDa.set(Calendar.HOUR_OF_DAY, 0);
                calDa.set(Calendar.MINUTE, 0);
                calDa.set(Calendar.SECOND, 0);
                calDa.set(Calendar.MILLISECOND, 0);

                calA.set(Calendar.HOUR_OF_DAY, 23);
                calA.set(Calendar.MINUTE, 59);
                calA.set(Calendar.SECOND, 59);
                calA.set(Calendar.MILLISECOND, 999);

                SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
                switch (getRequest().getParameter("tiCreazione")) {
                case WebConstants.PARAMETER_CREAZIONE_OGGI:

                    getForm().getFiltriDocumenti().getGiorno_vers_da().setValue(df.format(calDa.getTime()));
                    getForm().getFiltriDocumenti().getOre_vers_da().setValue("0");
                    getForm().getFiltriDocumenti().getMinuti_vers_da().setValue("0");
                    getForm().getFiltriDocumenti().getGiorno_vers_a().setValue(df.format(calA.getTime()));
                    getForm().getFiltriDocumenti().getOre_vers_a().setValue("23");
                    getForm().getFiltriDocumenti().getMinuti_vers_a().setValue("59");

                    filtriListaDoc.setGiornoVersDaValidato(calDa.getTime());
                    filtriListaDoc.setGiornoVersAValidato(calA.getTime());
                    break;
                case WebConstants.PARAMETER_CREAZIONE_30GG:
                    calDa.add(Calendar.DAY_OF_MONTH, -30);
                    calA.add(Calendar.DAY_OF_MONTH, -1);

                    getForm().getFiltriDocumenti().getGiorno_vers_da().setValue(df.format(calDa.getTime()));
                    getForm().getFiltriDocumenti().getOre_vers_da().setValue("0");
                    getForm().getFiltriDocumenti().getMinuti_vers_da().setValue("0");
                    getForm().getFiltriDocumenti().getGiorno_vers_a().setValue(df.format(calA.getTime()));
                    getForm().getFiltriDocumenti().getOre_vers_a().setValue("23");
                    getForm().getFiltriDocumenti().getMinuti_vers_a().setValue("59");

                    filtriListaDoc.setGiornoVersDaValidato(calDa.getTime());
                    filtriListaDoc.setGiornoVersAValidato(calA.getTime());
                    break;
                case WebConstants.PARAMETER_CREAZIONE_B30:
                    calDa.set(2000, 0, 1);
                    calA.add(Calendar.DAY_OF_MONTH, -31);

                    getForm().getFiltriDocumenti().getGiorno_vers_da().setValue(df.format(calDa.getTime()));
                    getForm().getFiltriDocumenti().getOre_vers_da().setValue("0");
                    getForm().getFiltriDocumenti().getMinuti_vers_da().setValue("0");
                    getForm().getFiltriDocumenti().getGiorno_vers_a().setValue(df.format(calA.getTime()));
                    getForm().getFiltriDocumenti().getOre_vers_a().setValue("23");
                    getForm().getFiltriDocumenti().getMinuti_vers_a().setValue("59");

                    filtriListaDoc.setGiornoVersDaValidato(calDa.getTime());
                    filtriListaDoc.setGiornoVersAValidato(calA.getTime());
                    break;
                }

                if (filtriListaDoc.getTipoDoc().equals("1")) {
                    filtriListaDoc.setTipoCreazione(CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name());
                } else {
                    filtriListaDoc.setTipoCreazione(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name());
                }
            }

            // Salvo in sessione i filtri
            getSession().setAttribute("filtriListaDoc", filtriListaDoc);
            // Setto la lista dei documenti
            String maxResultStandard = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
            BaseTableInterface<?> monVLisDocViewBean = monitoraggioHelper.getMonVLisDocViewBean(
                    (MonitoraggioFiltriListaDocBean) getSession().getAttribute("filtriListaDoc"),
                    Integer.parseInt(maxResultStandard));
            getForm().getDocumentiList().setTable(monVLisDocViewBean);
            getForm().getDocumentiList().getTable().setPageSize(10);
            getForm().getDocumentiList().setUserOperations(false, false, false, false);
            // Workaround in modo che la lista punti al primo record, non all'ultimo
            getForm().getDocumentiList().getTable().first();
            getForm().getFiltriDocumenti().setEditMode();

            getForm().getFiltriDocumenti().getId_ambiente().setDecodeMap(mappaAmbiente);
            getForm().getFiltriDocumenti().getId_ambiente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ambiente().getValue());

            getForm().getFiltriDocumenti().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriDocumenti().getId_ente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ente().getValue());

            getForm().getFiltriDocumenti().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriDocumenti().getId_strut()
                    .setValue(getForm().getRiepilogoVersamenti().getId_strut().getValue());

            getForm().getFiltriDocumenti().getId_tipo_unita_doc().setDecodeMap(mappaUD);
            getForm().getFiltriDocumenti().getId_tipo_unita_doc()
                    .setValue(getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue());

            BigDecimal idStrut = getForm().getRiepilogoVersamenti().getId_strut().parse();
            // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
            DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(
                    (idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()), false, false);
            getForm().getFiltriDocumenti().getId_tipo_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc"));

            // Preparo la combo "Registro"
            getForm().getFiltriDocumenti().getCd_registro_key_unita_doc()
                    .setDecodeMap(getMappaRegistro(idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()));
            getForm().getFiltriDocumenti().getCd_registro_key_unita_doc().setEditMode();

            // Setto il flag "Doc Principale"
            if (getRequest().getParameter("tipoDoc") != null) {
                getForm().getFiltriDocumenti().getFl_doc_principale().setValue(getRequest().getParameter("tipoDoc"));
            }

            // Preparo la combo "Stato Doc."
            getForm().getFiltriDocumenti().getTi_stato_doc_elenco_vers().setDecodeMap(
                    ComboGetter.getMappaSortedGenericEnum("ti_stato_doc", ElencoEnums.DocStatusEnum.getFilterEnums()));
            getForm().getFiltriDocumenti().getTi_stato_doc_elenco_vers()
                    .setValue(getRequest().getParameter("statoDoc"));
            getForm().getFiltriDocumenti().getTi_stato_doc_elenco_vers().setEditMode();

            // Preparo la combo "periodo versamento"
            getForm().getFiltriDocumenti().getPeriodo_vers().setDecodeMap(ComboGetter.getMappaPeriodoVers());
            getForm().getFiltriDocumenti().getPeriodo_vers().setValue(getRequest().getParameter("periodo"));
            getForm().getFiltriDocumenti().getPeriodo_vers().setEditMode();

            forwardToPublisher(Application.Publisher.MONITORAGGIO_DOC_LIST);
        } // MOSTRA LISTA VERSAMENTI FALLITI
        else if (getRequest().getParameter("pagina").equals("2")) {
            // Setto diversi valori dei filtri presi come parametri passati dalla request
            filtriSes.setPeriodoVers(getRequest().getParameter("periodo"));
            if (getRequest().getParameter("tipoSes") != null) {
                filtriSes.setTipoSes(getRequest().getParameter("tipoSes"));
            }
            // Parametro RISOLTI
            if (getRequest().getParameter("risolti") != null) {
                filtriSes.setRisolto(getRequest().getParameter("risolti"));
            } else {
                getForm().getFiltriVersamenti().getFl_risolto().setValue("");
            }
            // Parametro VERIFICATI
            if (getRequest().getParameter("verificati") != null) {
                filtriSes.setVerificato(getRequest().getParameter("verificati"));
            }
            // Parametro NON RISOLUBILI
            if (getRequest().getParameter("nonrisolubili") != null) {
                filtriSes.setNonRisolubile(getRequest().getParameter("nonrisolubili"));
            }

            if (getRequest().getParameter("tiCreazione") != null) {
                Calendar calDa = Calendar.getInstance();
                Calendar calA = Calendar.getInstance();
                calDa.set(Calendar.HOUR_OF_DAY, 0);
                calDa.set(Calendar.MINUTE, 0);
                calDa.set(Calendar.SECOND, 0);
                calDa.set(Calendar.MILLISECOND, 0);

                calA.set(Calendar.HOUR_OF_DAY, 23);
                calA.set(Calendar.MINUTE, 59);
                calA.set(Calendar.SECOND, 59);
                calA.set(Calendar.MILLISECOND, 999);

                SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
                switch (getRequest().getParameter("tiCreazione")) {
                case WebConstants.PARAMETER_CREAZIONE_OGGI:

                    getForm().getFiltriVersamenti().getGiorno_vers_da().setValue(df.format(calDa.getTime()));
                    getForm().getFiltriVersamenti().getOre_vers_da().setValue("0");
                    getForm().getFiltriVersamenti().getMinuti_vers_da().setValue("0");
                    getForm().getFiltriVersamenti().getGiorno_vers_a().setValue(df.format(calA.getTime()));
                    getForm().getFiltriVersamenti().getOre_vers_a().setValue("23");
                    getForm().getFiltriVersamenti().getMinuti_vers_a().setValue("59");

                    filtriSes.setGiornoVersDaValidato(calDa.getTime());
                    filtriSes.setGiornoVersAValidato(calA.getTime());
                    break;
                case WebConstants.PARAMETER_CREAZIONE_30GG:
                    calDa.add(Calendar.DAY_OF_MONTH, -30);
                    calA.add(Calendar.DAY_OF_MONTH, -1);

                    getForm().getFiltriVersamenti().getGiorno_vers_da().setValue(df.format(calDa.getTime()));
                    getForm().getFiltriVersamenti().getOre_vers_da().setValue("0");
                    getForm().getFiltriVersamenti().getMinuti_vers_da().setValue("0");
                    getForm().getFiltriVersamenti().getGiorno_vers_a().setValue(df.format(calA.getTime()));
                    getForm().getFiltriVersamenti().getOre_vers_a().setValue("23");
                    getForm().getFiltriVersamenti().getMinuti_vers_a().setValue("59");

                    filtriSes.setGiornoVersDaValidato(calDa.getTime());
                    filtriSes.setGiornoVersAValidato(calA.getTime());
                    break;
                case WebConstants.PARAMETER_CREAZIONE_B30:
                    calDa.set(2000, 0, 1);
                    calA.add(Calendar.DAY_OF_MONTH, -31);

                    getForm().getFiltriVersamenti().getGiorno_vers_da().setValue(df.format(calDa.getTime()));
                    getForm().getFiltriVersamenti().getOre_vers_da().setValue("0");
                    getForm().getFiltriVersamenti().getMinuti_vers_da().setValue("0");
                    getForm().getFiltriVersamenti().getGiorno_vers_a().setValue(df.format(calA.getTime()));
                    getForm().getFiltriVersamenti().getOre_vers_a().setValue("23");
                    getForm().getFiltriVersamenti().getMinuti_vers_a().setValue("59");

                    filtriSes.setGiornoVersDaValidato(calDa.getTime());
                    filtriSes.setGiornoVersAValidato(calA.getTime());
                    break;
                }
            }
            // Setto i permessi dell'utente
            filtriSes.setIdUserIam(new BigDecimal(getUser().getIdUtente()));

            // Salvo i filtri in sessione
            getSession().setAttribute("filtriSes", filtriSes);
            String maxResultStandard = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
            // Setto la lista dei versamenti falliti
            MonVLisVersErrIamTableBean versErrTableBean = monitoraggioHelper.getMonVLisVersErrIamViewBean(filtriSes,
                    Integer.parseInt(maxResultStandard));
            getForm().getVersamentiFallitiList().setTable(versErrTableBean);
            getForm().getVersamentiFallitiList().getTable().setPageSize(10);
            getForm().getVersamentiFallitiList().setUserOperations(true, false, false, false);
            // Workaround in modo che la lista punti al primo record, non all'ultimo
            getForm().getVersamentiFallitiList().getTable().first();
            // Imposto editabili tutti i filtri
            getForm().getFiltriVersamenti().setEditMode();

            getForm().getFiltriVersamenti().getId_ambiente().setDecodeMap(mappaAmbiente);
            getForm().getFiltriVersamenti().getId_ambiente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ambiente().getValue());

            getForm().getFiltriVersamenti().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriVersamenti().getId_ente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ente().getValue());

            getForm().getFiltriVersamenti().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriVersamenti().getId_strut()
                    .setValue(getForm().getRiepilogoVersamenti().getId_strut().getValue());

            // Preparo la combo "Tipo Sessione"
            getForm().getFiltriVersamenti().getTi_sessione_vers().setDecodeMap(
                    ComboGetter.getMappaSortedGenericEnum("ti_sessione_vers", Constants.TipoSessione.values()));
            getForm().getFiltriVersamenti().getTi_sessione_vers().setValue(getRequest().getParameter("tipoSes"));
            getForm().getFiltriVersamenti().getTi_sessione_vers().setEditMode();

            // Preparo la combo "Periodo versamento"
            getForm().getFiltriVersamenti().getPeriodo_vers().setDecodeMap(ComboGetter.getMappaPeriodoVers());
            getForm().getFiltriVersamenti().getPeriodo_vers().setValue(getRequest().getParameter("periodo"));
            getForm().getFiltriVersamenti().getPeriodo_vers().setEditMode();

            // Preparo la combo "Risolto"
            getForm().getFiltriVersamenti().getFl_risolto().setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getFiltriVersamenti().getFl_risolto().setEditMode();

            if (getRequest().getParameter("risolti") != null) {
                if (getRequest().getParameter("risolti").equals("1")) {
                    getForm().getFiltriVersamenti().getFl_risolto().setValue("1");
                } else {
                    getForm().getFiltriVersamenti().getFl_risolto().setValue("0");
                }
            }

            // Preparo la combo "Verificato"
            getForm().getFiltriVersamenti().getVersamento_ses_err_verif()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getFiltriVersamenti().getVersamento_ses_err_verif().setEditMode();
            boolean abilitaNonRisolubile = false;
            if (getRequest().getParameter("verificati") != null) {
                if (getRequest().getParameter("verificati").equals("1")) {
                    getForm().getFiltriVersamenti().getVersamento_ses_err_verif().setValue("1");
                    abilitaNonRisolubile = true;
                } else {
                    getForm().getFiltriVersamenti().getVersamento_ses_err_verif().setValue("0");
                }
            }

            // Preparo la combo "Non risolubile", solo se la combo "Verificato"
            // \u00e0¨ diversa da NO
            getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub().setEditMode();
            if (abilitaNonRisolubile) {
                getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub()
                        .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
                if (getRequest().getParameter("nonrisolubili") != null) {
                    if (getRequest().getParameter("nonrisolubili").equals("1")) {
                        getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub().setValue("1");
                    } else {
                        getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub().setValue("0");
                    }
                }
            } else {
                getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub().setDecodeMap(new DecodeMap());
            }

            // Preparo la combo "Classe errore"
            getForm().getFiltriVersamenti().getClasse_errore().setDecodeMap(caricaErrori.getMappaClasseErrore());
            getForm().getFiltriVersamenti().getClasse_errore().setEditMode();

            // Preparo la combo "SottoClasse errore"
            getForm().getFiltriVersamenti().getSottoclasse_errore().setDecodeMap(new DecodeMap());
            getForm().getFiltriVersamenti().getSottoclasse_errore().setEditMode();

            // Preparo la combo "Codice errore"
            getForm().getFiltriVersamenti().getCodice_errore().setDecodeMap(new DecodeMap());
            getForm().getFiltriVersamenti().getCodice_errore().setEditMode();

            // Preparo la Multiselect "Registro" ricavando i valori dalla lista
            // dei versamenti falliti totali
            DecodeMap mappaReg = getMappaRegistroFromTotaleMonVLisVersErr(
                    getForm().getRiepilogoVersamenti().getId_ambiente().parse(),
                    getForm().getRiepilogoVersamenti().getId_ente().parse(),
                    getForm().getRiepilogoVersamenti().getId_strut().parse(), getUser().getIdUtente());
            getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
            getForm().getFiltriVersamentiUlteriori().setEditMode();

            /*
             * Metto inizialmente in edit mode le checkbox Verificati e Non Risolubile della lista risultato
             */
            getForm().getVersamentiFallitiList().getFl_sessione_err_verif().setReadonly(false);
            getForm().getVersamentiFallitiList().getFl_sessione_err_non_risolub().setReadonly(false);

            if (filtriSes.getRisolto() != null && filtriSes.getRisolto().equals("1")) {
                /*
                 * Se \u00e0¨ "risolto", mette in view mode le checkbox Verificati e Non Risolubile
                 */
                getForm().getVersamentiFallitiList().getFl_sessione_err_verif().setReadonly(true);
                getForm().getVersamentiFallitiList().getFl_sessione_err_non_risolub().setReadonly(true);
            }

            /* Gestione visibilità bottoni */
            mostraNascondiBottoniOperazioniVersamentiFalliti(versErrTableBean.size());

            /*
             * Rendo visibile il bottone di "Verifica automatica" se la struttura \u00e0¨ impostata nei filtri di
             * ricerca
             */
            if (filtriSes.getIdStrut() != null) {
                getForm().getFiltriVersamenti().getVerificaAutomatica().setEditMode();
            } else {
                getForm().getFiltriVersamenti().getVerificaAutomatica().setViewMode();
            }

            // Setto il tab nel quale presentare la videata
            getForm().getFiltriRicercaVersamentiFallitiTabs()
                    .setCurrentTab(getForm().getFiltriRicercaVersamentiFallitiTabs().getFiltriGenerali());
            forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
        } // MOSTRA LISTA RIEPILOGO VERSAMENTI FALLITI DISTINTI PER DOCUMENTI --> (UNITÀ DOCUMENTARIE DERIVANTI DA
          // VERSAMENTI FALLITI)
        else if (getRequest().getParameter("pagina").equals("3")) {
            // Setto diversi valori dei filtri presi come parametri passati dalla request
            filtriListaVersFallitiDistintiDoc.setTipoLista(getRequest().getParameter("tipoLista"));
            if (getRequest().getParameter("flVerificato") != null) {
                filtriListaVersFallitiDistintiDoc.setFlVerificato(getRequest().getParameter("flVerificato"));
            }
            if (getRequest().getParameter("flNonRisolub") != null) {
                filtriListaVersFallitiDistintiDoc.setFlNonRisolub(getRequest().getParameter("flNonRisolub"));
            }
            // Setto i permessi dell'utente
            filtriListaVersFallitiDistintiDoc.setIdUserIam(new BigDecimal(getUser().getIdUtente()));

            Calendar calDa = Calendar.getInstance();
            Calendar calA = Calendar.getInstance();
            calDa.set(Calendar.YEAR, 2011);
            calDa.set(Calendar.MONTH, 11);
            calDa.set(Calendar.DAY_OF_MONTH, 1);
            calDa.set(Calendar.HOUR_OF_DAY, 0);
            calDa.set(Calendar.MINUTE, 0);
            calDa.set(Calendar.SECOND, 0);
            calDa.set(Calendar.MILLISECOND, 0);

            calA.set(Calendar.HOUR_OF_DAY, 23);
            calA.set(Calendar.MINUTE, 59);
            calA.set(Calendar.SECOND, 59);
            calA.set(Calendar.MILLISECOND, 999);
            SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getGiorno_first_vers_da()
                    .setValue(df.format(calDa.getTime()));
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getOre_first_vers_da().setValue("0");
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getMinuti_first_vers_da().setValue("0");
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getGiorno_first_vers_a()
                    .setValue(df.format(calA.getTime()));
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getOre_first_vers_a().setValue("23");
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getMinuti_first_vers_a().setValue("59");

            getForm().getFiltriUdDocDerivantiDaVersFalliti().getGiorno_last_vers_da()
                    .setValue(df.format(calDa.getTime()));
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getOre_last_vers_da().setValue("0");
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getMinuti_last_vers_da().setValue("0");
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getGiorno_last_vers_a()
                    .setValue(df.format(calA.getTime()));
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getOre_last_vers_a().setValue("23");
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getMinuti_last_vers_a().setValue("59");

            filtriListaVersFallitiDistintiDoc.setGiornoFirstVersDaValidato(calDa.getTime());
            filtriListaVersFallitiDistintiDoc.setGiornoFirstVersAValidato(calA.getTime());

            filtriListaVersFallitiDistintiDoc.setGiornoLastVersDaValidato(calDa.getTime());
            filtriListaVersFallitiDistintiDoc.setGiornoLastVersAValidato(calA.getTime());

            // Salvo in sessione i filtri
            getSession().setAttribute("filtriListaVersFallitiDistintiDoc", filtriListaVersFallitiDistintiDoc);

            // Setto la lista dei documenti non versati
            if (filtriListaVersFallitiDistintiDoc.getTipoLista().equals("UNITA_DOC")) {
                MonVLisUdNonVersIamTableBean monVLisUdNonVersTableBean = monitoraggioHelper
                        .getMonVLisUdNonVersIamViewBeanScaricaContenuto(
                                (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                        .getAttribute("filtriListaVersFallitiDistintiDoc"),
                                null);
                getForm().getDocumentiDerivantiDaVersFallitiList().setTable(monVLisUdNonVersTableBean);
            } else {
                MonVLisDocNonVersIamTableBean monVLisDocNonVersTableBean = monitoraggioHelper
                        .getMonVLisDocNonVersIamViewBeanScaricaContenuto(
                                (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                        .getAttribute("filtriListaVersFallitiDistintiDoc"),
                                null);
                getForm().getDocumentiDerivantiDaVersFallitiList().setTable(monVLisDocNonVersTableBean);
            }

            getForm().getDocumentiDerivantiDaVersFallitiList().getTable().setPageSize(10);
            getForm().getDocumentiDerivantiDaVersFallitiList().setUserOperations(true, false, false, false);
            // Workaround in modo che la lista punti al primo record, non all'ultimo
            getForm().getDocumentiDerivantiDaVersFallitiList().getTable().first();
            // Imposto tutti i filtri in edit mode
            getForm().getFiltriUdDocDerivantiDaVersFalliti().setEditMode();

            getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ambiente().setDecodeMap(mappaAmbiente);
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ambiente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ambiente().getValue());

            getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ente().getValue());

            getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut()
                    .setValue(getForm().getRiepilogoVersamenti().getId_strut().getValue());

            // Preparo la combo "Tipo Lista"
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista()
                    .setValue(getRequest().getParameter("tipoLista"));
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().setViewMode();

            // preparo la combo "Verificato"
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_verif()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_verif().setEditMode();

            if (getRequest().getParameter("flVerificato") != null) {
                if (getRequest().getParameter("flVerificato").equals("1")) {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_verif().setValue("1");
                } else {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_verif().setValue("0");
                }
            }

            // preparo la combo "Non risolubile"
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_non_risolub()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_non_risolub().setEditMode();

            if (getRequest().getParameter("flNonRisolub") != null) {
                if (getRequest().getParameter("flNonRisolub").equals("1")) {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_non_risolub().setValue("1");
                } else {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getFl_non_risolub().setValue("0");
                }
            }

            // Preparo la combo "Classe errore"
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getClasse_errore()
                    .setDecodeMap(caricaErrori.getMappaClasseErrore());
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getClasse_errore().setEditMode();

            // Preparo la combo "SottoClasse errore"
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getSottoclasse_errore().setDecodeMap(new DecodeMap());
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getSottoclasse_errore().setEditMode();

            // Preparo la combo "Codice errore"
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getCodice_errore().setDecodeMap(new DecodeMap());
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getCodice_errore().setEditMode();
            if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut() != null) {
                getForm().getFiltriUdDocDerivantiDaVersFalliti().getVerificaVersamentiFalliti().setEditMode();
            } else {
                getForm().getFiltriUdDocDerivantiDaVersFalliti().getVerificaVersamentiFalliti().setViewMode();
            }

            // Preparo le Multiselect "Registro" ricavando i valori dalla lista
            // dei versamenti falliti totali
            DecodeMap mappaReg = getMappaRegistroFromTotaleMonVLisUdNonVers(
                    getForm().getRiepilogoVersamenti().getId_ambiente().parse(),
                    getForm().getRiepilogoVersamenti().getId_ente().parse(),
                    getForm().getRiepilogoVersamenti().getId_strut().parse(), getUser().getIdUtente());
            getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_registro_key_unita_doc_ult()
                    .setDecodeMap(mappaReg);
            getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().setEditMode();

            getForm().getFiltriUdDocDerivantiDaVersFalliti().getDownloadContenuto().setEditMode();
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getDownloadContenuto().setDisableHourGlass(true);

            // Setto il tab nel quale presentare la videata
            getForm().getFiltriListaDocumentiDerivantiVersFallitiTabs().setCurrentTab(
                    getForm().getFiltriListaDocumentiDerivantiVersFallitiTabs().getFiltriGeneraliDerivanti());

            forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_RICERCA);
        } // MOSTRA LISTA VERSAMENTI DOCUMENTI ANNULLATI
        else if (getRequest().getParameter("pagina").equals("4")) {
            String stato = getRequest().getParameter("tipoStato");
            String tipoVers = getRequest().getParameter("tipoVers");

            getForm().getFiltriDocumentiAnnullati().setEditMode();

            getForm().getFiltriDocumentiAnnullati().getId_ambiente().setDecodeMap(mappaAmbiente);
            getForm().getFiltriDocumentiAnnullati().getId_ambiente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ambiente().getValue());

            getForm().getFiltriDocumentiAnnullati().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriDocumentiAnnullati().getId_ente()
                    .setValue(getForm().getRiepilogoVersamenti().getId_ente().getValue());

            getForm().getFiltriDocumentiAnnullati().getId_strut().setDecodeMap(mappaStrut);
            getForm().getFiltriDocumentiAnnullati().getId_strut()
                    .setValue(getForm().getRiepilogoVersamenti().getId_strut().getValue());

            getForm().getFiltriDocumentiAnnullati().getId_tipo_unita_doc().setDecodeMap(mappaUD);
            getForm().getFiltriDocumentiAnnullati().getId_tipo_unita_doc()
                    .setValue(getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().getValue());

            BigDecimal idStrut = getForm().getRiepilogoVersamenti().getId_strut().parse();
            // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
            DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(
                    (idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()), false, false);
            getForm().getFiltriDocumentiAnnullati().getId_tipo_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc"));

            // Preparo la combo "Registro"
            getForm().getFiltriDocumentiAnnullati().getCd_registro_key_unita_doc()
                    .setDecodeMap(getMappaRegistro(idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()));
            getForm().getFiltriDocumentiAnnullati().getTi_vers_annul().setDecodeMap(
                    ComboGetter.getMappaSortedGenericEnum("ti_vers_annul", VolumeEnums.TipoVersAnnul.values()));
            getForm().getFiltriDocumentiAnnullati().getTi_vers_annul().setValue(tipoVers);
            getForm().getFiltriDocumentiAnnullati().getTi_stato_annul()
                    .setDecodeMap(ComboGetter.getMappaTiStatoAnnul());
            getForm().getFiltriDocumentiAnnullati().getTi_stato_annul().setValue(stato);

            // Setto la lista dei documenti
            String maxResultStandard = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
            MonVLisUniDocDaAnnulTableBean monVLisDocTableBean = monitoraggioEjb.getMonVLisUniDocDaAnnul(
                    getUser().getIdUtente(), getForm().getFiltriDocumentiAnnullati(),
                    Integer.parseInt(maxResultStandard));
            getForm().getDocumentiAnnullatiList().setTable(monVLisDocTableBean);
            getForm().getDocumentiAnnullatiList().getTable().setPageSize(10);
            // Workaround in modo che la lista punti al primo record, non all'ultimo
            getForm().getDocumentiAnnullatiList().getTable().first();

            forwardToPublisher(Application.Publisher.MONITORAGGIO_DOC_ANNULLATI_LIST);
        }
    }

    /**
     * Metodo attivato al momento della pressione del tasto di ricerca nella pagina Lista Documenti raggiunta quando si
     * clicca su un totale della pagina Riepilogo Versamenti
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaDoc() throws EMFError {
        MonitoraggioForm.FiltriDocumenti filtri = getForm().getFiltriDocumenti();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());

        // Valida i filtri per verificare quelli obbligatori
        if (filtri.validate(getMessageBox())) {
            // Aggiorno i filtri in sessione
            MonitoraggioFiltriListaDocBean filtriListaDoc = (MonitoraggioFiltriListaDocBean) getSession()
                    .getAttribute("filtriListaDoc");
            filtriListaDoc.setIdAmbiente(filtri.getId_ambiente().parse());
            filtriListaDoc.setIdEnte(filtri.getId_ente().parse());
            filtriListaDoc.setIdStrut(filtri.getId_strut().parse());
            filtriListaDoc.setIdTipoUnitaDoc(filtri.getId_tipo_unita_doc().parse());
            filtriListaDoc.setCdRegistroKeyUnitaDoc(filtri.getCd_registro_key_unita_doc().parse());
            filtriListaDoc.setAaKeyUnitaDoc(filtri.getAa_key_unita_doc().parse());
            filtriListaDoc.setCdKeyUnitaDoc(filtri.getCd_key_unita_doc().parse());
            filtriListaDoc.setPeriodoVers(filtri.getPeriodo_vers().parse());
            filtriListaDoc.setGiornoVersDa(filtri.getGiorno_vers_da().parse());
            filtriListaDoc.setOreVersDa(filtri.getOre_vers_da().parse());
            filtriListaDoc.setMinutiVersDa(filtri.getMinuti_vers_da().parse());
            filtriListaDoc.setGiornoVersA(filtri.getGiorno_vers_a().parse());
            filtriListaDoc.setOreVersA(filtri.getOre_vers_a().parse());
            filtriListaDoc.setMinutiVersA(filtri.getMinuti_vers_a().parse());

            filtriListaDoc.setIdTipoDoc(filtri.getId_tipo_doc().parse());

            filtriListaDoc.setStatoDoc(filtri.getTi_stato_doc_elenco_vers().parse());

            // Gestione del flag "Documento principale"
            String[] docPrincipale = getRequest().getParameterValues("Fl_doc_principale");
            String flaggozzo = "0";
            // Se != da null, significa che ho spuntato il flag
            if (docPrincipale != null) {
                flaggozzo = "1";
            }
            // Imposto il flaggozzo nel front-end (in pratica ne faccio il post)
            getForm().getFiltriDocumenti().getFl_doc_principale().setChecked(flaggozzo.equals("1"));
            filtriListaDoc.setTipoDoc(flaggozzo);
            getSession().setAttribute("filtriListaDoc", filtriListaDoc);

            /*
             * Controllo su campi periodo versamento e giorno versamento: solo uno dei due pu\u00e0² essere valorizzato
             */
            MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
            // Prima validazione: controllo che siano stati compilati o l'uno (periodo versamento) o gli altri (range
            // giorno versamento)
            validator.validaSceltaPeriodoGiornoVersamento(filtri.getPeriodo_vers().parse(),
                    filtri.getGiorno_vers_da().parse(), filtri.getOre_vers_da().parse(),
                    filtri.getMinuti_vers_da().parse(), filtri.getGiorno_vers_a().parse(),
                    filtri.getOre_vers_a().parse(), filtri.getMinuti_vers_a().parse());

            // Seconda validazione: controllo che il range di giorno versamento sia corretto e setto gli eventuali
            // valori di default
            Date[] dateValidate = validator.validaDate(filtri.getGiorno_vers_da().parse(),
                    filtri.getOre_vers_da().parse(), filtri.getMinuti_vers_da().parse(),
                    filtri.getGiorno_vers_a().parse(), filtri.getOre_vers_a().parse(),
                    filtri.getMinuti_vers_a().parse(), filtri.getGiorno_vers_da().getHtmlDescription(),
                    filtri.getGiorno_vers_a().getHtmlDescription());

            // Valida i campi di ricerca
            UnitaDocumentarieValidator udValidator = new UnitaDocumentarieValidator(getMessageBox());
            udValidator.controllaPresenzaAnno(filtri.getAa_key_unita_doc().parse(),
                    filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse());

            Object[] chiavi = null;
            if (!getMessageBox().hasError()) {
                // Valida i campi di Range di chiavi unit\u00e0  documentaria
                chiavi = udValidator.validaChiaviUnitaDoc(filtri.getCd_registro_key_unita_doc().getValue(),
                        filtri.getAa_key_unita_doc().parse(), filtri.getCd_key_unita_doc().parse(),
                        filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse(),
                        filtri.getCd_key_unita_doc_da().parse(), filtri.getCd_key_unita_doc_a().parse());
            }

            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori.
                if (chiavi != null && chiavi.length == 5) {
                    filtriListaDoc.setAaKeyUnitaDocDa(chiavi[1] != null ? ((BigDecimal) chiavi[1]) : null);
                    filtriListaDoc.setAaKeyUnitaDocA(chiavi[2] != null ? ((BigDecimal) chiavi[2]) : null);
                    filtriListaDoc.setCdKeyUnitaDocDa(chiavi[3] != null ? (String) chiavi[3] : null);
                    filtriListaDoc.setCdKeyUnitaDocA(chiavi[4] != null ? (String) chiavi[4] : null);
                }
                // Le eventuali date riferite al giorno di versamento vengono salvate in sessione
                filtriListaDoc.setGiornoVersDaValidato(null);
                filtriListaDoc.setGiornoVersAValidato(null);
                if (dateValidate != null) {
                    filtriListaDoc.setGiornoVersDaValidato(dateValidate[0]);
                    filtriListaDoc.setGiornoVersAValidato(dateValidate[1]);
                    getSession().setAttribute("filtriListaDoc", filtriListaDoc);
                }

                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                // La validazione non ha riportato errori. Carico la tabella con i filtri impostati
                BaseTableInterface<?> monVLisDocTableBean = monitoraggioHelper.getMonVLisDocViewBean(
                        (MonitoraggioFiltriListaDocBean) getSession().getAttribute("filtriListaDoc"),
                        Integer.parseInt(maxResultStandard));
                getForm().getDocumentiList().setTable(monVLisDocTableBean);
                getForm().getDocumentiList().getTable().setPageSize(10);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getDocumentiList().getTable().first();
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_DOC_LIST);
    }

    @Override
    public void ricercaVers() throws EMFError {
        MonitoraggioForm.FiltriVersamenti filtri = getForm().getFiltriVersamenti();
        MonitoraggioForm.FiltriVersamentiUlteriori filtriUlteriori = getForm().getFiltriVersamentiUlteriori();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        if (filtri.validate(getMessageBox())) {
            // recupero i filtri dalla sessione e setto i filtri con il nuovo valore
            MonitoraggioFiltriListaVersFallitiBean filtriSes = (MonitoraggioFiltriListaVersFallitiBean) getSession()
                    .getAttribute("filtriSes");
            filtriSes.setIdAmbiente(filtri.getId_ambiente().parse());
            filtriSes.setIdEnte(filtri.getId_ente().parse());
            filtriSes.setIdStrut(filtri.getId_strut().parse());
            filtriSes.setTipoSes(filtri.getTi_sessione_vers().parse());
            filtriSes.setRisolto(filtri.getFl_risolto().parse());
            filtriSes.setPeriodoVers(filtri.getPeriodo_vers().parse());
            filtriSes.setGiornoVersDa(filtri.getGiorno_vers_da().parse());
            filtriSes.setOreVersDa(filtri.getOre_vers_da().parse());
            filtriSes.setMinutiVersDa(filtri.getMinuti_vers_da().parse());
            filtriSes.setGiornoVersA(filtri.getGiorno_vers_a().parse());
            filtriSes.setOreVersA(filtri.getOre_vers_a().parse());
            filtriSes.setMinutiVersA(filtri.getMinuti_vers_a().parse());
            filtriSes.setVerificato(filtri.getVersamento_ses_err_verif().parse());
            filtriSes.setNonRisolubile(filtri.getVersamento_ses_err_non_risolub().parse());
            filtriSes.setCodiceErrore(filtri.getCodice_errore().parse());
            filtriSes.setClasseErrore(filtri.getClasse_errore().parse());
            filtriSes.setSottoClasseErrore(filtri.getSottoclasse_errore().parse());

            filtriSes.setRegistro(filtriUlteriori.getCd_registro_key_unita_doc().getDecodedValues());
            filtriSes.setAnno(filtriUlteriori.getAa_key_unita_doc().parse());
            filtriSes.setNumero(filtriUlteriori.getCd_key_unita_doc().parse());
            filtriSes.setAnno_range_da(filtriUlteriori.getAa_key_unita_doc_da().parse());
            filtriSes.setNumero_range_da(filtriUlteriori.getCd_key_unita_doc_da().parse());
            filtriSes.setAnno_range_a(filtriUlteriori.getAa_key_unita_doc_a().parse());
            filtriSes.setNumero_range_a(filtriUlteriori.getCd_key_unita_doc_a().parse());
            getSession().setAttribute("filtriSes", filtriSes);

            /*
             * Controllo su campi periodo versamento e giorno versamento: solo uno dei due pu\u00e0² essere valorizzato
             */
            MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
            // Prima validazione: controllo che siano stati compilati o l'uno (periodo versamento) o gli altri (range
            // giorno versamento)
            validator.validaSceltaPeriodoGiornoVersamento(filtri.getPeriodo_vers().parse(),
                    filtri.getGiorno_vers_da().parse(), filtri.getOre_vers_da().parse(),
                    filtri.getMinuti_vers_da().parse(), filtri.getGiorno_vers_a().parse(),
                    filtri.getOre_vers_a().parse(), filtri.getMinuti_vers_a().parse());

            // Seconda validazione: controllo che il range di giorno versamento sia corretto e setto gli eventuali
            // valori di default
            Date[] dateValidate = validator.validaDate(filtri.getGiorno_vers_da().parse(),
                    filtri.getOre_vers_da().parse(), filtri.getMinuti_vers_da().parse(),
                    filtri.getGiorno_vers_a().parse(), filtri.getOre_vers_a().parse(),
                    filtri.getMinuti_vers_a().parse(), filtri.getGiorno_vers_da().getHtmlDescription(),
                    filtri.getGiorno_vers_a().getHtmlDescription());

            // Valida i filtri per verificare quelli obbligatori
            if (!getMessageBox().hasError()) {
                // Le eventuali date riferite al giorno di versamento vengono salvate in sessione
                if (dateValidate != null) {
                    filtriSes.setGiornoVersDaValidato(dateValidate[0]);
                    filtriSes.setGiornoVersAValidato(dateValidate[1]);
                    getSession().setAttribute("filtriSes", filtriSes);
                }

                // Setto la lista dei versamenti falliti
                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                MonVLisVersErrIamTableBean monVLisVersErrTableBean = monitoraggioHelper.getMonVLisVersErrIamViewBean(
                        (MonitoraggioFiltriListaVersFallitiBean) getSession().getAttribute("filtriSes"),
                        Integer.parseInt(maxResultStandard));
                getForm().getVersamentiFallitiList().setTable(monVLisVersErrTableBean);
                getForm().getVersamentiFallitiList().getTable().setPageSize(10);
                getForm().getVersamentiFallitiList().setUserOperations(true, false, false, false);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getVersamentiFallitiList().getTable().first();
            }

            // Se la lista risultato ha degli elementi
            if (getForm().getVersamentiFallitiList().getTable().size() > 0) {
                /*
                 * Metto inizialmente in edit mode le checkbox Verificati e Non Risolubile
                 */
                getForm().getVersamentiFallitiList().getFl_sessione_err_verif().setReadonly(false);
                getForm().getVersamentiFallitiList().getFl_sessione_err_non_risolub().setReadonly(false);

                if (filtriSes.getRisolto() != null && filtriSes.getRisolto().equals("1")) {
                    /*
                     * Se \u00e0¨ "risolto", mette in view mode le checkbox Verificati e Non Risolubile
                     */
                    getForm().getVersamentiFallitiList().getFl_sessione_err_verif().setReadonly(true);
                    getForm().getVersamentiFallitiList().getFl_sessione_err_non_risolub().setReadonly(true);
                }
            }

            /* Gestione visibilità bottoni */
            mostraNascondiBottoniOperazioniVersamentiFalliti(getForm().getVersamentiFallitiList().getTable().size());

            // Se i filtri hanno settata una specifica struttura,
            // visualizzo il tasto di "VerificaVersamenti"
            if (filtriSes.getIdStrut() != null) {
                getForm().getFiltriVersamenti().getVerificaAutomatica().setHidden(false);
            } else {
                getForm().getFiltriVersamenti().getVerificaAutomatica().setHidden(true);
            }

        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
    }

    /**
     * A seconda del numero di record della lista risultato, mostra o nasconde i bottoni relativi al calcolo chiave
     * unità documentaria e verifica versamento
     *
     * @param numRecord
     *            numero record
     */
    private void mostraNascondiBottoniOperazioniVersamentiFalliti(int numRecord) {
        if (numRecord != 0) {
            getForm().getSalvaVerificaButtonList().getSalvaVerificaVersamento().setEditMode();
        } else {
            getForm().getSalvaVerificaButtonList().getSalvaVerificaVersamento().setViewMode();
        }
    }

    @Override
    public void ricercaDocumentiDerivantiDaVersFalliti() throws EMFError {
        MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti filtri = getForm().getFiltriUdDocDerivantiDaVersFalliti();
        MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori filtriUlteriori = getForm()
                .getFiltriUdDocDerivantiDaVersFallitiUlteriori();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        // Recupero i filtri dalla sessione e setto i filtri con il nuovo valore
        MonitoraggioFiltriListaVersFallitiDistintiDocBean filtriListaVersFallitiDistintiDoc = (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                .getAttribute("filtriListaVersFallitiDistintiDoc");
        filtriListaVersFallitiDistintiDoc.setIdAmbiente(filtri.getId_ambiente().parse());
        filtriListaVersFallitiDistintiDoc.setIdEnte(filtri.getId_ente().parse());
        filtriListaVersFallitiDistintiDoc.setIdStrut(filtri.getId_strut().parse());
        filtriListaVersFallitiDistintiDoc.setTipoLista(filtri.getTipo_lista().parse());
        filtriListaVersFallitiDistintiDoc.setFlVerificato(filtri.getFl_verif().parse());
        filtriListaVersFallitiDistintiDoc.setFlNonRisolub(filtri.getFl_non_risolub().parse());
        filtriListaVersFallitiDistintiDoc.setClasseErrore(filtri.getClasse_errore().parse());
        filtriListaVersFallitiDistintiDoc.setSottoClasseErrore(filtri.getSottoclasse_errore().parse());
        filtriListaVersFallitiDistintiDoc.setCodiceErrore(filtri.getCodice_errore().parse());

        filtriListaVersFallitiDistintiDoc
                .setRegistro(filtriUlteriori.getCd_registro_key_unita_doc_ult().getDecodedValues());
        filtriListaVersFallitiDistintiDoc.setAnno(filtriUlteriori.getAa_key_unita_doc_ult().parse());
        filtriListaVersFallitiDistintiDoc.setNumero(filtriUlteriori.getCd_key_unita_doc_ult().parse());
        filtriListaVersFallitiDistintiDoc.setAnno_range_da(filtriUlteriori.getAa_key_unita_doc_da_ult().parse());
        filtriListaVersFallitiDistintiDoc.setNumero_range_da(filtriUlteriori.getCd_key_unita_doc_da_ult().parse());
        filtriListaVersFallitiDistintiDoc.setAnno_range_a(filtriUlteriori.getAa_key_unita_doc_a_ult().parse());
        filtriListaVersFallitiDistintiDoc.setNumero_range_a(filtriUlteriori.getCd_key_unita_doc_a_ult().parse());

        MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());

        // Controllo che i range di giorno versamento sia corretto e setto gli eventuali
        // valori di default
        Date[] dateValidateFirst = validator.validaDate(filtri.getGiorno_first_vers_da().parse(),
                filtri.getOre_first_vers_da().parse(), filtri.getMinuti_first_vers_da().parse(),
                filtri.getGiorno_first_vers_a().parse(), filtri.getOre_first_vers_a().parse(),
                filtri.getMinuti_first_vers_a().parse(), filtri.getGiorno_first_vers_da().getHtmlDescription(),
                filtri.getGiorno_first_vers_a().getHtmlDescription());

        Date[] dateValidateLast = validator.validaDate(filtri.getGiorno_last_vers_da().parse(),
                filtri.getOre_last_vers_da().parse(), filtri.getMinuti_last_vers_da().parse(),
                filtri.getGiorno_last_vers_a().parse(), filtri.getOre_last_vers_a().parse(),
                filtri.getMinuti_last_vers_a().parse(), filtri.getGiorno_last_vers_da().getHtmlDescription(),
                filtri.getGiorno_last_vers_a().getHtmlDescription());

        // Le eventuali date riferite al giorno di versamento vengono salvate in sessione
        if (dateValidateFirst != null) {
            filtriListaVersFallitiDistintiDoc.setGiornoFirstVersDaValidato(dateValidateFirst[0]);
            filtriListaVersFallitiDistintiDoc.setGiornoFirstVersAValidato(dateValidateFirst[1]);
        }
        if (dateValidateLast != null) {
            filtriListaVersFallitiDistintiDoc.setGiornoLastVersDaValidato(dateValidateLast[0]);
            filtriListaVersFallitiDistintiDoc.setGiornoLastVersAValidato(dateValidateLast[1]);
        }

        getSession().setAttribute("filtriListaVersFallitiDistintiDoc", filtri);

        // Li risetto perch\u00e0¨ se torno indietro in "Riepilogo Versamenti" devo avere impostata la ricerca con
        // questi ultimi
        getSession().setAttribute("filtriListaVersFallitiDistintiDoc", filtriListaVersFallitiDistintiDoc);

        // Valida i filtri per verificare quelli obbligatori
        if (filtri.validate(getMessageBox())) {
            if (!getMessageBox().hasError()) {
                if (filtriListaVersFallitiDistintiDoc.getTipoLista().equals("UNITA_DOC")) {
                    // Setto la lista delle unit\u00e0  documentarie non versate
                    MonVLisUdNonVersIamTableBean monVLiUdNonVersTableBean = monitoraggioHelper
                            .getMonVLisUdNonVersIamViewBeanScaricaContenuto(
                                    (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                            .getAttribute("filtriListaVersFallitiDistintiDoc"),
                                    null);
                    getForm().getDocumentiDerivantiDaVersFallitiList().setTable(monVLiUdNonVersTableBean);
                } else {
                    // Setto la lista delle unit\u00e0  documentarie non versate
                    MonVLisDocNonVersIamTableBean monVLisDocNonVersTableBean = monitoraggioHelper
                            .getMonVLisDocNonVersIamViewBeanScaricaContenuto(
                                    (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                            .getAttribute("filtriListaVersFallitiDistintiDoc"),
                                    null);
                    getForm().getDocumentiDerivantiDaVersFallitiList().setTable(monVLisDocNonVersTableBean);
                }
                getForm().getDocumentiDerivantiDaVersFallitiList().getTable().setPageSize(10);
                getForm().getDocumentiDerivantiDaVersFallitiList().setUserOperations(true, false, false, false);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getDocumentiDerivantiDaVersFallitiList().getTable().first();
                if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().parse() != null) {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getVerificaVersamentiFalliti().setEditMode();
                } else {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getVerificaVersamentiFalliti().setViewMode();
                }

            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_RICERCA);
    }

    public void ricercaOperazioniVolumiDaDettaglioVolume() throws EMFError {
        /*
         * Mi salvo in sessione l'id del volume e la struttura perch\u00e0¨ se sono qui significa che arrivo da
         * Dettaglio Volume
         */
        if (getRequest().getParameter("idVolumePerMon") != null) {
            getSession().setAttribute("idVolumePerMon", getRequest().getParameter("idVolumePerMon"));
        }
        if (getRequest().getParameter("idStrutPerMon") != null) {
            getSession().setAttribute("idStrutPerMon", getRequest().getParameter("idStrutPerMon"));
        }

        boolean eseguiForward = false;
        if (getRequest().getParameter("eseguiForward") != null) {
            eseguiForward = Boolean.parseBoolean(getRequest().getParameter("eseguiForward"));
        }

        BigDecimal idVol = new BigDecimal((String) getSession().getAttribute("idVolumePerMon"));
        BigDecimal idStrut = new BigDecimal((String) getSession().getAttribute("idStrutPerMon"));

        BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
        BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);
        getForm().getFiltriOperazioniVolumi().getTi_output().setValue("CRONOLOGICO");
        getForm().getFiltriOperazioniVolumi().getId_volume_conserv().setValue(idVol.toString());

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
            log.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriOperazioniVolumi().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriOperazioniVolumi().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriOperazioniVolumi().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriOperazioniVolumi().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriOperazioniVolumi().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriOperazioniVolumi().getId_strut().setValue(idStrut.toString());

        // Imposto le combo in editMode
        getForm().getFiltriOperazioniVolumi().getId_ambiente().setEditMode();
        getForm().getFiltriOperazioniVolumi().getId_ente().setEditMode();
        getForm().getFiltriOperazioniVolumi().getId_strut().setEditMode();

        // Imposto i flag gi\u00e0  ceccati
        getForm().getFiltriOperazioniVolumi().getFl_oper_crea_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_recupera_volume_aperto().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_aggiungi_doc_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_recupera_volume_scaduto().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_set_volume_da_chiudere().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_set_volume_aperto().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_inizio_crea_indice().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_recupera_volume_in_errore().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_crea_indice_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_marca_indice_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_set_volume_in_errore().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_inizio_verif_firme().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_chiusura_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_err_verif_firme().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_rimuovi_doc_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_elimina_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_modifica_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_firma_no_marca_volume().setChecked(true);
        getForm().getFiltriOperazioniVolumi().getFl_oper_firma_volume().setChecked(true);
        // Inizializzo le combo Modalit\u00e0  operazione e Tipo output
        getForm().getFiltriOperazioniVolumi().getTi_mod_oper().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("modalita", VolumeEnums.ModalitaOperazioni.values()));
        getForm().getFiltriOperazioniVolumi().getTi_output().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_output", VolumeEnums.TipoOutputMonitoraggio.values()));
        getForm().getFiltriOperazioniVolumi().setEditMode();
        // Inizializzo le liste fittizie nel caso si voglia visualizzare unit\u00e0  documentaria o volume
        getForm().getUnitaDocumentariaOutputList().setTable(new AroVRicUnitaDocTableBean());
        getForm().getVolumeOutputList().setTable(new VolVRicVolumeTableBean());
        eseguiRicercaOperazioniVolumi(false);
        if (eseguiForward) {
            forwardToPublisher(Application.Publisher.MONITORAGGIO_OPERAZIONI_VOLUMI_RICERCA);
        }
    }

    public void ricercaOperazioniElenchiDaDettaglioElenco() throws EMFError {
        /*
         * Mi salvo in sessione l'id dell'elenco e la struttura perch\u00e0 se sono qui significa che arrivo da
         * Dettaglio Elenco di Versamento
         */
        if (getRequest().getParameter("idElencoPerMon") != null) {
            getSession().setAttribute("idElencoPerMon", getRequest().getParameter("idElencoPerMon"));
        }
        if (getRequest().getParameter("idStrutPerMon") != null) {
            getSession().setAttribute("idStrutPerMon", getRequest().getParameter("idStrutPerMon"));
        }

        boolean eseguiForward = false;
        if (getRequest().getParameter("eseguiForward") != null) {
            eseguiForward = Boolean.parseBoolean(getRequest().getParameter("eseguiForward"));
        }

        BigDecimal idElencoVers = new BigDecimal((String) getSession().getAttribute("idElencoPerMon"));
        BigDecimal idStrut = new BigDecimal((String) getSession().getAttribute("idStrutPerMon"));

        BigDecimal idEnte = monitoraggioHelper.getIdEnte(idStrut);
        BigDecimal idAmbiente = monitoraggioHelper.getIdAmbiente(idEnte);
        getForm().getFiltriOperazioniElenchiVersamento().getTi_output().setValue("CRONOLOGICO");
        getForm().getFiltriOperazioniElenchiVersamento().getId_elenco_vers().setValue(idElencoVers.toString());

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
            log.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriOperazioniElenchiVersamento().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriOperazioniElenchiVersamento().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriOperazioniElenchiVersamento().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriOperazioniElenchiVersamento().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriOperazioniElenchiVersamento().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriOperazioniElenchiVersamento().getId_strut().setValue(idStrut.toString());

        // Imposto le combo in editMode
        getForm().getFiltriOperazioniElenchiVersamento().getId_ambiente().setEditMode();
        getForm().getFiltriOperazioniElenchiVersamento().getId_ente().setEditMode();
        getForm().getFiltriOperazioniElenchiVersamento().getId_strut().setEditMode();

        /* Di default imposto checkate le varie checkbox */
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_chiusura_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_crea_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_crea_indice_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_def_note_elenco_chiuso().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_def_note_indice_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_elimina_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_in_corso().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_in_corso_fallita().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_mod_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_recupera_elenco_aperto().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_recupera_elenco_scaduto().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_rimuovi_doc_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_rimuovi_ud_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_set_elenco_aperto().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_set_elenco_da_chiudere().setChecked(true);

        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_start_crea_elenco_indici_aip().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_end_crea_elenco_indici_aip().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_elenco_indici_aip().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_elenco_indici_aip_in_corso().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_elenco_indici_aip_fallita().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_marca_elenco_indici_aip().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_marca_elenco_indici_aip_fallita().setChecked(true);
        // Inizializzo le combo Modalit\u00e0  operazione e Tipo output
        getForm().getFiltriOperazioniElenchiVersamento().getTi_mod_oper().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("modalita", VolumeEnums.ModalitaOperazioni.values()));
        getForm().getFiltriOperazioniElenchiVersamento().getTi_output().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_output", VolumeEnums.TipoOutputMonitoraggio.values()));
        getForm().getFiltriOperazioniElenchiVersamento().setEditMode();
        // Inizializzo le liste fittizie nel caso si voglia visualizzare unit\u00e0  documentaria o elenco di versamento
        getForm().getUnitaDocumentariaOutputList().setTable(new AroVRicUnitaDocTableBean());
        getForm().getElencoVersamentoOutputList().setTable(new ElvVRicElencoVersTableBean());
        eseguiRicercaOperazioniElenchiVersamento(false);
        if (eseguiForward) {
            forwardToPublisher(Application.Publisher.MONITORAGGIO_OPERAZIONI_ELENCHI_RICERCA);
        }
    }

    /**
     * Metodo attivato al momento della pressione del tasto di ricerca nella pagina Operazioni Volumi Viene richiamato
     * il metodo appropriato cui viene passato il parametro per l'esecuzione del post sui filtri
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaOperazioniVolumi() throws EMFError {
        boolean eseguiPost = true;
        eseguiRicercaOperazioniVolumi(eseguiPost);
        forwardToPublisher(Application.Publisher.MONITORAGGIO_OPERAZIONI_VOLUMI_RICERCA);
    }

    /**
     * Esegue la ricerca delle operazioni sui volumi
     *
     * @param eseguiPost
     *            per eseguire o meno il post dei filtri di ricerca
     *
     * @throws EMFError
     *             errore generico
     */
    public void eseguiRicercaOperazioniVolumi(boolean eseguiPost) throws EMFError {
        // Disabilito la clessidra
        getForm().getFiltriOperazioniVolumi().getRicercaOperazioniVolumi().setDisableHourGlass(true);

        FiltriOperazioniVolumi filtri = getForm().getFiltriOperazioniVolumi();

        if (eseguiPost) {
            filtri.post(getRequest());
            /*
             * Setto le varie checkbox dei filtri spuntate o meno a seconda dei valori presi dalla request questo
             * perch\u00e0¨ non riesco direttamente a ricavare il valore del filtro checkbox
             */
            Map<String, String[]> cecchini = getRequest().getParameterMap();
            filtri.getFl_oper_crea_volume().setChecked(cecchini.get("Fl_oper_crea_volume") != null);
            filtri.getFl_oper_recupera_volume_aperto()
                    .setChecked(cecchini.get("Fl_oper_recupera_volume_aperto") != null);
            filtri.getFl_oper_aggiungi_doc_volume().setChecked(cecchini.get("Fl_oper_aggiungi_doc_volume") != null);
            filtri.getFl_oper_recupera_volume_scaduto()
                    .setChecked(cecchini.get("Fl_oper_recupera_volume_scaduto") != null);
            filtri.getFl_oper_set_volume_da_chiudere()
                    .setChecked(cecchini.get("Fl_oper_set_volume_da_chiudere") != null);
            filtri.getFl_oper_set_volume_aperto().setChecked(cecchini.get("Fl_oper_set_volume_aperto") != null);
            filtri.getFl_oper_inizio_crea_indice().setChecked(cecchini.get("Fl_oper_inizio_crea_indice") != null);
            filtri.getFl_oper_recupera_volume_in_errore()
                    .setChecked(cecchini.get("Fl_oper_recupera_volume_in_errore") != null);
            filtri.getFl_oper_crea_indice_volume().setChecked(cecchini.get("Fl_oper_crea_indice_volume") != null);
            filtri.getFl_oper_marca_indice_volume().setChecked(cecchini.get("Fl_oper_marca_indice_volume") != null);
            filtri.getFl_oper_set_volume_in_errore().setChecked(cecchini.get("Fl_oper_set_volume_in_errore") != null);
            filtri.getFl_oper_inizio_verif_firme().setChecked(cecchini.get("Fl_oper_inizio_verif_firme") != null);
            filtri.getFl_oper_chiusura_volume().setChecked(cecchini.get("Fl_oper_chiusura_volume") != null);
            filtri.getFl_oper_err_verif_firme().setChecked(cecchini.get("Fl_oper_err_verif_firme") != null);
            filtri.getFl_oper_rimuovi_doc_volume().setChecked(cecchini.get("Fl_oper_rimuovi_doc_volume") != null);
            filtri.getFl_oper_elimina_volume().setChecked(cecchini.get("Fl_oper_elimina_volume") != null);
            filtri.getFl_oper_modifica_volume().setChecked(cecchini.get("Fl_oper_modifica_volume") != null);
            filtri.getFl_oper_firma_no_marca_volume().setChecked(cecchini.get("Fl_oper_firma_no_marca_volume") != null);
            filtri.getFl_oper_firma_volume().setChecked(cecchini.get("Fl_oper_firma_volume") != null);
        }

        // Controllo che ci sia almeno un cecchino checkato
        if (!filtri.getFl_oper_crea_volume().isChecked() && !filtri.getFl_oper_recupera_volume_aperto().isChecked()
                && !filtri.getFl_oper_aggiungi_doc_volume().isChecked()
                && !filtri.getFl_oper_recupera_volume_scaduto().isChecked()
                && !filtri.getFl_oper_set_volume_da_chiudere().isChecked()
                && !filtri.getFl_oper_set_volume_aperto().isChecked()
                && !filtri.getFl_oper_inizio_crea_indice().isChecked()
                && !filtri.getFl_oper_recupera_volume_in_errore().isChecked()
                && !filtri.getFl_oper_crea_indice_volume().isChecked()
                && !filtri.getFl_oper_marca_indice_volume().isChecked()
                && !filtri.getFl_oper_set_volume_in_errore().isChecked()
                && !filtri.getFl_oper_inizio_verif_firme().isChecked()
                && !filtri.getFl_oper_chiusura_volume().isChecked() && !filtri.getFl_oper_err_verif_firme().isChecked()
                && !filtri.getFl_oper_rimuovi_doc_volume().isChecked()
                && !filtri.getFl_oper_elimina_volume().isChecked() && !filtri.getFl_oper_modifica_volume().isChecked()
                && !filtri.getFl_oper_firma_no_marca_volume().isChecked()
                && !filtri.getFl_oper_firma_volume().isChecked()) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Attenzione: \u00e0¨ necessario spuntare almeno un tipo di operazione volume"));
        }

        // Valida i filtri per verificare quelli obbligatori
        if (filtri.validate(getMessageBox())) {
            // Valida in maniera pi\u00e0¹ specifica i dati
            Date datada = filtri.getDt_oper_da().parse();
            Date dataa = filtri.getDt_oper_a().parse();
            BigDecimal oreda = filtri.getOre_dt_oper_da().parse();
            BigDecimal orea = filtri.getOre_dt_oper_a().parse();
            BigDecimal minutida = filtri.getMinuti_dt_oper_da().parse();
            BigDecimal minutia = filtri.getMinuti_dt_oper_a().parse();
            String descrizioneDataDa = filtri.getDt_oper_da().getHtmlDescription();
            String descrizioneDataA = filtri.getDt_oper_a().getHtmlDescription();

            // Valida i campi di ricerca
            MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
            Date[] dateValidate = validator.validaDate(datada, oreda, minutida, dataa, orea, minutia, descrizioneDataDa,
                    descrizioneDataA);

            if (!getMessageBox().hasError()) {
                /*
                 * La validazione non ha riportato errori. Carico la tabella con i filtri impostati. Se l'output scelto
                 * da visualizzare \u00e0¨ ANALITICO o CRONOLOGICO
                 */
                if (filtri.getTi_output().parse().equals("ANALITICO")
                        || filtri.getTi_output().parse().equals("CRONOLOGICO")) {
                    int outputPaginaCorrente = 1;
                    int inizioOutput = 0;
                    int outputPageSize = 10;
                    if (getForm().getOutputAnaliticoCronologicoList().getTable() != null) {
                        outputPaginaCorrente = getForm().getOutputAnaliticoCronologicoList().getTable()
                                .getCurrentPageIndex();
                        inizioOutput = getForm().getOutputAnaliticoCronologicoList().getTable().getFirstRowPageIndex();
                        outputPageSize = getForm().getOutputAnaliticoCronologicoList().getTable().getPageSize();
                    }
                    String maxResultStandard = configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                    getForm().getOutputAnaliticoCronologicoList().setTable(monitoraggioHelper
                            .getMonVLisOperVolIamViewBean(filtri, dateValidate, Integer.parseInt(maxResultStandard)));
                    getForm().getOutputAnaliticoCronologicoList().getTable().setPageSize(outputPageSize);
                    // Workaround in modo che la lista punti al primo record, non all'ultimo
                    getForm().getOutputAnaliticoCronologicoList().getTable().first();
                    this.lazyLoadGoPage(getForm().getOutputAnaliticoCronologicoList(), outputPaginaCorrente);
                    getForm().getOutputAnaliticoCronologicoList().getTable().setCurrentRowIndex(inizioOutput);
                    // Setto in request un parametro che servir\u00e0  alla jsp per sapere quale tabella visualizzare
                    getRequest().setAttribute("output", "analitico");
                } // Altrimenti significa che ho scelto di visualizzare output AGGREGATO
                else {
                    getForm().getOutputAggregatoList()
                            .setTable(monitoraggioHelper.getMonVLisOperVolOutputAggregato(filtri, dateValidate));
                    getForm().getOutputAggregatoList().getTable().setPageSize(16);
                    // Workaround in modo che la lista punti al primo record, non all'ultimo
                    getForm().getOutputAggregatoList().getTable().first();
                    // Setto in request un parametro che servir\u00e0  alla jsp per sapere quale tabella visualizzare
                    getRequest().setAttribute("output", "aggregato");
                }
            }
        }
    }

    /**
     * Azzera i filtri di ricerca di Operazioni Volumi richiamando la pagina stessa
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void pulisciOperazioniVolumi() throws EMFError {
        try {
            // Ricarica la form di ricerca
            this.operazioniVolumi();
        } catch (Exception ex) {
            log.error("Eccezione", ex);
        }
    }

    @Override
    public void ricercaOperazioniElenchiVersamento() throws EMFError {
        boolean eseguiPost = true;
        eseguiRicercaOperazioniElenchiVersamento(eseguiPost);
        forwardToPublisher(Application.Publisher.MONITORAGGIO_OPERAZIONI_ELENCHI_RICERCA);
    }

    public void eseguiRicercaOperazioniElenchiVersamento(boolean eseguiPost) throws EMFError {
        // Disabilito la clessidra
        getForm().getFiltriOperazioniElenchiVersamento().getRicercaOperazioniElenchiVersamento()
                .setDisableHourGlass(true);

        FiltriOperazioniElenchiVersamento filtri = getForm().getFiltriOperazioniElenchiVersamento();

        if (eseguiPost) {
            filtri.post(getRequest());
        }

        // Controllo che ci sia almeno un cecchino checkato
        if (!filtri.getFl_oper_chiusura_elenco().isChecked() && !filtri.getFl_oper_crea_elenco().isChecked()
                && !filtri.getFl_oper_crea_indice_elenco().isChecked()
                && !filtri.getFl_oper_def_note_elenco_chiuso().isChecked()
                && !filtri.getFl_oper_def_note_indice_elenco().isChecked()
                && !filtri.getFl_oper_elimina_elenco().isChecked() && !filtri.getFl_oper_firma_elenco().isChecked()
                && !filtri.getFl_oper_mod_elenco().isChecked()
                && !filtri.getFl_oper_recupera_elenco_aperto().isChecked()
                && !filtri.getFl_oper_recupera_elenco_scaduto().isChecked()
                && !filtri.getFl_oper_rimuovi_doc_elenco().isChecked()
                && !filtri.getFl_oper_rimuovi_ud_elenco().isChecked()
                && !filtri.getFl_oper_set_elenco_aperto().isChecked()
                && !filtri.getFl_oper_set_elenco_da_chiudere().isChecked()
                && !filtri.getFl_oper_firma_in_corso().isChecked()
                && !filtri.getFl_oper_firma_in_corso_fallita().isChecked()
                && !filtri.getFl_oper_start_crea_elenco_indici_aip().isChecked()
                && !filtri.getFl_oper_end_crea_elenco_indici_aip().isChecked()
                && !filtri.getFl_oper_firma_elenco_indici_aip().isChecked()
                && !filtri.getFl_oper_firma_elenco_indici_aip_in_corso().isChecked()
                && !filtri.getFl_oper_firma_elenco_indici_aip_fallita().isChecked()
                && !filtri.getFl_oper_marca_elenco_indici_aip().isChecked()
                && !filtri.getFl_oper_marca_elenco_indici_aip_fallita().isChecked()) {
            getMessageBox().addError(
                    "Attenzione: \u00e8 necessario spuntare almeno un tipo di operazione elenchi di versamento");
        }

        /* Valida i filtri per verificare quelli obbligatori */
        if (filtri.validate(getMessageBox())) {
            /* Valida in maniera pi\u00e0 specifica i dati */
            MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
            Date datada = filtri.getTm_oper_da().parse();
            Date dataa = filtri.getTm_oper_a().parse();
            BigDecimal oreda = filtri.getOre_tm_oper_da().parse();
            BigDecimal orea = filtri.getOre_tm_oper_a().parse();
            BigDecimal minutida = filtri.getMinuti_tm_oper_da().parse();
            BigDecimal minutia = filtri.getMinuti_tm_oper_a().parse();
            String descrizioneDataDa = filtri.getTm_oper_da().getHtmlDescription();
            String descrizioneDataA = filtri.getTm_oper_a().getHtmlDescription();
            /* Validazione date */
            Date[] dateValidate = validator.validaDate(datada, oreda, minutida, dataa, orea, minutia, descrizioneDataDa,
                    descrizioneDataA);

            if (!getMessageBox().hasError()) {
                /*
                 * La validazione non ha riportato errori. Carico la tabella con i filtri impostati. Se l'output scelto
                 * da visualizzare \u00e0 ANALITICO o CRONOLOGICO
                 */
                if (filtri.getTi_output().parse().equals(ElencoEnums.TipoOutputMonitoraggio.ANALITICO.name()) || filtri
                        .getTi_output().parse().equals(ElencoEnums.TipoOutputMonitoraggio.CRONOLOGICO.name())) {
                    int outputPaginaCorrente = 0;
                    int inizioOutput = 0;
                    int outputPageSize = 10;
                    if (getForm().getOutputAnaliticoCronologicoListElenchi().getTable() != null) {
                        outputPaginaCorrente = getForm().getOutputAnaliticoCronologicoListElenchi().getTable()
                                .getCurrentPageIndex();
                        inizioOutput = getForm().getOutputAnaliticoCronologicoListElenchi().getTable()
                                .getFirstRowPageIndex();
                        outputPageSize = getForm().getOutputAnaliticoCronologicoListElenchi().getTable().getPageSize();
                    }
                    ElvVLisLogOperTableBean operazioniTableBean = monitoraggioHelper.getElvVLisLogOperViewBean(filtri,
                            dateValidate);
                    getForm().getOutputAnaliticoCronologicoListElenchi().setTable(operazioniTableBean);
                    getForm().getOutputAnaliticoCronologicoListElenchi().getTable().setPageSize(outputPageSize);
                    getForm().getOutputAnaliticoCronologicoListElenchi().getTable().first();
                    this.lazyLoadGoPage(getForm().getOutputAnaliticoCronologicoListElenchi(), outputPaginaCorrente);
                    getForm().getOutputAnaliticoCronologicoListElenchi().getTable().setCurrentRowIndex(inizioOutput);

                    getRequest().setAttribute("output", "analitico");
                } // Altrimenti significa che ho scelto di visualizzare output AGGREGATO
                else {
                    getForm().getOutputAggregatoListElenchi()
                            .setTable(monitoraggioHelper.getElvVLisLogOperOutputAggregato(filtri, dateValidate));
                    getForm().getOutputAggregatoListElenchi().getTable().setPageSize(16);
                    // Workaround in modo che la lista punti al primo record, non all'ultimo
                    getForm().getOutputAggregatoListElenchi().getTable().first();
                    // Setto in request un parametro che servir\u00e0  alla jsp per sapere quale tabella visualizzare
                    getRequest().setAttribute("output", "aggregato");
                }
            }
        }
    }

    /**
     * Metodo eseguito al momento della pressione del tasto di ricerca della pagina Esame Contenuto Sacer.
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaContenutoSacer() throws EMFError {
        getForm().getFiltriContenutoSacer().getRicercaContenutoSacer().setDisableHourGlass(true);
        FiltriContenutoSacer filtri = getForm().getFiltriContenutoSacer();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        // Ricavo i valori da utilizzare per ricercare l'ambito territoriale
        List<BigDecimal> idAmbitoTerrit1Livello = filtri.getId_ambito_territ_livello_1().parse();
        List<BigDecimal> idAmbitoTerrit2Livello = filtri.getId_ambito_territ_livello_2().parse();
        List<BigDecimal> idAmbitoTerrit3Livello = filtri.getId_ambito_territ_livello_3().parse();
        List<BigDecimal> idAmbitoTerritList = struttureEjb.getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello,
                idAmbitoTerrit2Livello, idAmbitoTerrit3Livello);
        // Verifico i campi e le date
        if (filtri.validate(getMessageBox())) {
            MonitoraggioValidator validatore = new MonitoraggioValidator(getMessageBox());
            validatore.validaDateCalcoloContenutoSacer(filtri.getDt_rif_da().parse(), filtri.getDt_rif_a().parse());
            if (!getMessageBox().hasError()) {
                /* Calcolo i totali */
                BaseRowInterface totali = monitoraggioHelper.getTotaliUdDocComp(filtri, idAmbitoTerritList,
                        getUser().getIdUtente());
                getForm().getContenutoSacerTotaliUdDocComp().copyFromBean(totali);
                /* Ricavo la lista risultato */
                BaseTableInterface<?> totSacer = monitoraggioHelper.getMonTotSacerTable(filtri, idAmbitoTerritList,
                        getUser().getIdUtente());
                getForm().getContenutoSacerList().setTable(totSacer);
                getForm().getContenutoSacerList().getTable().first();
                getForm().getContenutoSacerList().getTable().setPageSize(10);
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_CONTENUTO_SACER_RICERCA);
    }

    /**
     * Azzera i filtri di ricerca di Esame Contenuto Sacer richiamando la pagina stessa
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void pulisciContenutoSacer() throws EMFError {
        try {
            this.contenutoSacer();
        } catch (Exception ex) {
            log.error("Eccezione", ex);
        }
    }

    /**
     * Metodo eseguito al momento della pressione del tasto di ricerca della pagina Esame Contenuto Sacer.
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void ricercaJobSchedulati() throws EMFError {
        getForm().getFiltriJobSchedulati().getRicercaJobSchedulati().setDisableHourGlass(true);
        FiltriJobSchedulati filtri = getForm().getFiltriJobSchedulati();

        // Esegue la post dei filtri compilati
        if (getSession().getAttribute(FROM_GESTIONE_JOB) != null) {
            getSession().removeAttribute(FROM_GESTIONE_JOB);
        } else {
            filtri.post(getRequest());
        }

        // Valida i filtri per verificare quelli obbligatori
        if (filtri.validate(getMessageBox())) {
            // Valida in maniera pi\u00e0¹ specifica i dati
            String nmJob = filtri.getNm_job().parse();
            BigDecimal idAmbiente = filtri.getId_ambiente().parse();
            BigDecimal idEnte = filtri.getId_ente().parse();
            BigDecimal idStrut = filtri.getId_strut().parse();
            Date datada = filtri.getDt_reg_log_job_da().parse();
            Date dataa = filtri.getDt_reg_log_job_a().parse();
            BigDecimal oreda = filtri.getOre_dt_reg_log_job_da().parse();
            BigDecimal orea = filtri.getOre_dt_reg_log_job_a().parse();
            BigDecimal minutida = filtri.getMinuti_dt_reg_log_job_da().parse();
            BigDecimal minutia = filtri.getMinuti_dt_reg_log_job_a().parse();
            String descrizioneDataDa = filtri.getDt_reg_log_job_da().getHtmlDescription();
            String descrizioneDataA = filtri.getDt_reg_log_job_a().getHtmlDescription();

            // Valida i campi di ricerca
            MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());
            Date[] dateValidate = validator.validaDate(datada, oreda, minutida, dataa, orea, minutia, descrizioneDataDa,
                    descrizioneDataA);
            validator.validaStruttura(nmJob, idAmbiente, idEnte, idStrut);

            if (!getMessageBox().hasError()) {
                // Setta la lista dei job in base ai filtri di ricerca
                if (idStrut != null) {
                    getForm().getJobSchedulatiList()
                            .setTable(monitoraggioHelper.getLogVLisSchedStrutViewBean(filtri, dateValidate));
                } else {
                    getForm().getJobSchedulatiList()
                            .setTable(monitoraggioHelper.getLogVLisSchedViewBean(filtri, dateValidate));
                }

                getForm().getJobSchedulatiList().getTable().setPageSize(10);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getJobSchedulatiList().getTable().first();

                // Setto i campi di "Stato Job"
                setStatoJob(nmJob);
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_JOB_SCHEDULATI_RICERCA);
    }

    public void ricercaJobSchedulatiDaGestioneJob() throws EMFError {
        triggerFiltriJobSchedulatiNm_jobOnTrigger();
        ricercaJobSchedulati();
    }

    /**
     * Azzera i filtri di ricerca di Esame Job Schedulati richiamando la pagina stessa
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void pulisciJobSchedulati() throws EMFError {
        try {
            this.jobSchedulati();
        } catch (Exception ex) {
            log.error("Eccezione", ex);
        }
    }

    /**
     * Metodo invocato quando viene cliccato sul tasto "Imposta Verifica Versamento" nella pagina della Lista Versamenti
     * Falliti per settare i flag "Verificato" e "Non risolubile" della lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void salvaVerificaVersamento() throws Throwable {
        getForm().getFiltriVersamenti().post(getRequest());
        String flaggozzo = getForm().getFiltriVersamenti().getVersamento_ses_err_verif().parse();
        /* Ottengo i componenti selezionati del campo "Verificato" dalla lista risultato */
        String[] indiceAssolutoVerificatiSettati = getRequest().getParameterValues("Fl_sessione_err_verif");
        /* Ottengo i componenti selezionati del campo "Non risolubile" dalla lista risultato */
        String[] indiceAssolutoNonRisolubiliSettati = getRequest().getParameterValues("Fl_sessione_err_non_risolub");

        int totVerificati = indiceAssolutoVerificatiSettati != null ? indiceAssolutoVerificatiSettati.length : 0;
        int totNonRisolubili = indiceAssolutoNonRisolubiliSettati != null ? indiceAssolutoNonRisolubiliSettati.length
                : 0;

        Set<BigDecimal> idSessioniVerificateHS = new HashSet<>();
        Set<BigDecimal> idSessioniNonRisolubHS = new HashSet<>();

        /* Ricavo dei valori utili al fine del ricaricamento della lista */
        int paginaCorrente = getForm().getVersamentiFallitiList().getTable().getCurrentPageIndex();
        int inizio = getForm().getVersamentiFallitiList().getTable().getFirstRowPageIndex();
        int pageSize = getForm().getVersamentiFallitiList().getTable().getPageSize();
        int fine = getForm().getVersamentiFallitiList().getTable().getFirstRowPageIndex()
                + getForm().getVersamentiFallitiList().getTable().getPageSize();
        int ultimaPagina = getForm().getVersamentiFallitiList().getTable().getPages();

        /* Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della tabella */
        if (paginaCorrente == ultimaPagina) {
            fine = getForm().getVersamentiFallitiList().getTable().size();
        }

        ////////////////////////////////////////////////////////////////////////////
        ///// Ricavo i valori delle checkbox "Verificati" e "Non Risolubili" ///////
        ///// PRIMA e DOPO eventuali modifiche. ////////////////////////////////////
        ///// Quindi li confronto per vedere se ci sono state modifiche ////////////
        ////////////////////////////////////////////////////////////////////////////
        int numRecordPerPagina = fine - inizio;
        MonVLisVersErrIamTableBean monVersErr = (MonVLisVersErrIamTableBean) getForm().getVersamentiFallitiList()
                .getTable();
        String[] verificatiPre = new String[numRecordPerPagina];
        String[] verificatiPost = new String[numRecordPerPagina];
        String[] nonRisolubiliPre = new String[numRecordPerPagina];
        String[] nonRisolubiliPost = new String[numRecordPerPagina];
        /*
         * Contiene gli indici assoluti dei record in cui sono stati modificati il flag "Verificato" o il flag
         * "Non Risolubile" o entrambi
         */
        Set<Integer> verificatiNonRisolubiliModificati = new HashSet<>();
        Set<BigDecimal> idSesModificate;

        /* 1) Inserisco tutti i valori dei PRE e tutti "0" nei POST */
        int count = 0;
        for (int i = inizio; i < fine; i++) {
            verificatiPre[count] = monVersErr.getRow(i).getFlSessioneErrVerif() != null
                    ? monVersErr.getRow(i).getFlSessioneErrVerif() : "0";
            nonRisolubiliPre[count] = monVersErr.getRow(i).getFlSessioneErrNonRisolub() != null
                    ? monVersErr.getRow(i).getFlSessioneErrNonRisolub() : "0";
            verificatiPost[count] = "0";
            nonRisolubiliPost[count] = "0";
            count++;
        }

        /* 2) Ora nei POST metto gli "1" dove vanno messi */
        for (int j = 0; j < totVerificati; j++) {
            if (StringUtils.isNumeric(indiceAssolutoVerificatiSettati[j])) {
                int posizione = Integer.parseInt(indiceAssolutoVerificatiSettati[j]);
                verificatiPost[posizione - inizio] = "1";
            }
        }

        /*
         * 3) Ora che ho i record prima e dopo le eventuali modifiche, ricavo gli indici dei record "Verificati"
         * modificati
         */
        for (int k = 0; k < verificatiPre.length; k++) {
            if (!verificatiPre[k].equals(verificatiPost[k])) {
                verificatiNonRisolubiliModificati.add(k + inizio);
            }
        }

        /* Idem con patate per i "Non risolubili" */
        for (int j = 0; j < totNonRisolubili; j++) {
            if (StringUtils.isNumeric(indiceAssolutoNonRisolubiliSettati[j])) {
                int posizione = Integer.parseInt(indiceAssolutoNonRisolubiliSettati[j]);
                nonRisolubiliPost[posizione - inizio] = "1";
            }
        }

        /* Idem con patate per i "Non risolubili" */
        for (int k = 0; k < nonRisolubiliPre.length; k++) {
            if (!nonRisolubiliPre[k].equals(nonRisolubiliPost[k])) {
                verificatiNonRisolubiliModificati.add(k + inizio);
            }
        }

        // Valida i campi di chiave unita doc e chiave doc
        MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());

        /* CONTROLLI DI COERENZA SOLO SUGLI ELEMENTI MODIFICATI */
        /* Tra i "Verificati" e "Non Risolubili */
        for (int j = 0; j < verificatiPost.length; j++) {
            if (verificatiNonRisolubiliModificati.contains(Integer.valueOf(j + inizio))) {
                validator.validaFlagVerificatoNonRisolubile(verificatiPost[j], nonRisolubiliPost[j]);
                if (getMessageBox().hasError()) {
                    break;
                }
            }
        }

        if (!getMessageBox().hasError()) {
            /* Se ci sono state modifiche, le salvo su DB */
            if (!verificatiNonRisolubiliModificati.isEmpty()) {
                /* Ottengo gli id sessione dei record "verificati" MODIFICATI */
                if (indiceAssolutoVerificatiSettati != null) {
                    for (String comp : indiceAssolutoVerificatiSettati) {
                        if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
                            idSessioniVerificateHS.add(getForm().getVersamentiFallitiList().getTable()
                                    .getRow(Integer.parseInt(comp)).getBigDecimal("id_sessione_vers"));
                        }
                    }
                }

                // Ottengo gli id sessione dei record "non risolubili" (flag spuntato)
                if (indiceAssolutoNonRisolubiliSettati != null) {
                    for (String comp : indiceAssolutoNonRisolubiliSettati) {
                        if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
                            idSessioniNonRisolubHS.add(getForm().getVersamentiFallitiList().getTable()
                                    .getRow(Integer.parseInt(comp)).getBigDecimal("id_sessione_vers"));
                        }
                    }
                }

                try {
                    idSesModificate = monitoraggioEjb.aggiornaSessioni(verificatiNonRisolubiliModificati,
                            idSessioniVerificateHS, idSessioniNonRisolubHS,
                            (MonVLisVersErrIamTableBean) getForm().getVersamentiFallitiList().getTable());

                    // Aggiorno i filtri in sessione e rifaccio la query tenendo conto del filtro flaggozzo
                    MonitoraggioFiltriListaVersFallitiBean filtriSes = (MonitoraggioFiltriListaVersFallitiBean) getSession()
                            .getAttribute("filtriSes");
                    filtriSes.setVerificato(flaggozzo);
                    getSession().setAttribute("filtriSes", filtriSes);
                    String maxResultStandard = configurationHelper
                            .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                    MonVLisVersErrIamTableBean listaVersErr = monitoraggioHelper.getMonVLisVersErrIamViewBean(
                            (MonitoraggioFiltriListaVersFallitiBean) getSession().getAttribute("filtriSes"),
                            Integer.parseInt(maxResultStandard));
                    getForm().getVersamentiFallitiList().setTable(listaVersErr);
                    getForm().getVersamentiFallitiList().getTable().setPageSize(pageSize);
                    getForm().getVersamentiFallitiList().getTable().first();
                    this.lazyLoadGoPage(getForm().getVersamentiFallitiList(), paginaCorrente);
                    getForm().getVersamentiFallitiList().getTable().setCurrentRowIndex(inizio);

                    // Rieseguo anche la quesy di Riepilogo Versamenti
                    calcolaTotaliRiepilogoVersamenti(filtriSes.getIdAmbiente(), filtriSes.getIdEnte(),
                            filtriSes.getIdStrut(), filtriSes.getIdTipoUnitaDoc());

                    // Segnalo l'avvenuta impostazione dei flaggozzi andata a buon fine
                    String modify = idSesModificate.size() == 1 ? "modificato" : "modificati";
                    String ses = idSesModificate.size() == 1 ? "versamento fallito" : "versamenti falliti";
                    String messageOK = "Aggiornamento effettuato con successo: " + modify + " " + idSesModificate.size()
                            + " " + ses;
                    getMessageBox().addMessage(new Message(MessageLevel.INF, messageOK));
                    getMessageBox().setViewMode(ViewMode.plain);
                } catch (ParerUserError e) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getDescription()));
                } finally {
                    forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
                }
            } // Altrimenti segnalo il fatto di non aver apportato modifiche sul DB
            else {
                getMessageBox().addMessage(new Message(MessageLevel.WAR,
                        "Aggiornamento non effettuato in quanto non sono state apportate modifiche ai record"));
                getMessageBox().setViewMode(ViewMode.plain);
                forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
    }

    @Override
    public void salvaVerificaVersamentoDaDocDerVersFalliti() throws Throwable {
        /* Ottengo i componenti selezionati del campo "Verificato" dalla lista risultato */
        String[] indiceAssolutoVerificatiSettati = getRequest().getParameterValues("Fl_sessione_err_verif");
        /* Ottengo i componenti selezionati del campo "Non risolubile" dalla lista risultato */
        String[] indiceAssolutoNonRisolubiliSettati = getRequest().getParameterValues("Fl_sessione_err_non_risolub");
        int totVerificati = indiceAssolutoVerificatiSettati != null ? indiceAssolutoVerificatiSettati.length : 0;
        int totNonRisolubili = indiceAssolutoNonRisolubiliSettati != null ? indiceAssolutoNonRisolubiliSettati.length
                : 0;

        Set<BigDecimal> idSessioniVerificateHS = new HashSet<>();
        Set<BigDecimal> idSessioniNonRisolubHS = new HashSet<>();

        /* Ricavo dei valori utili al fine del ricaricamento della lista */
        int inizio = getForm().getSessioniList().getTable().getFirstRowPageIndex();
        int fine = getForm().getSessioniList().getTable().getFirstRowPageIndex()
                + getForm().getSessioniList().getTable().getPageSize();
        int paginaCorrente = getForm().getSessioniList().getTable().getCurrentPageIndex();
        int ultimaPagina = getForm().getSessioniList().getTable().getPages();

        // Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della tabella
        if (paginaCorrente == ultimaPagina) {
            fine = getForm().getSessioniList().getTable().size();
        }

        ////////////////////////////////////////////////////////////////////////////
        ///// Ricavo i valori delle checkbox "Verificati" e "Non Risolubili" ///////
        ///// PRIMA e DOPO eventuali modifiche. ////////////////////////////////////
        ///// Quindi li confronto per vedere se ci sono state modifiche ////////////
        ////////////////////////////////////////////////////////////////////////////
        int numRecordPerPagina = fine - inizio;

        String[] verificatiPre = new String[numRecordPerPagina];
        String[] verificatiPost = new String[numRecordPerPagina];
        String[] nonRisolubiliPre = new String[numRecordPerPagina];
        String[] nonRisolubiliPost = new String[numRecordPerPagina];
        /*
         * Contiene gli indici assoluti dei record in cui sono stati modificati il flag "Verificato" o il flag
         * "Non Risolubile" o entrambi
         */
        Set<Integer> verificatiNonRisolubiliModificati = new HashSet<>();
        Set<BigDecimal> idSesModificate;

        if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse().equals("UNITA_DOC")) {
            MonVLisVersUdNonVersTableBean monVersErr = (MonVLisVersUdNonVersTableBean) getForm().getSessioniList()
                    .getTable();
            /* 1) Inserisco tutti i valori dei PRE e tutti "0" nei POST */
            int count = 0;
            for (int i = inizio; i < fine; i++) {
                verificatiPre[count] = monVersErr.getRow(i).getFlSessioneErrVerif() != null
                        ? monVersErr.getRow(i).getFlSessioneErrVerif() : "0";
                nonRisolubiliPre[count] = monVersErr.getRow(i).getFlSessioneErrNonRisolub() != null
                        ? monVersErr.getRow(i).getFlSessioneErrNonRisolub() : "0";
                verificatiPost[count] = "0";
                nonRisolubiliPost[count] = "0";
                count++;
            }
        } else {
            MonVLisVersDocNonVersTableBean monVersErr = (MonVLisVersDocNonVersTableBean) getForm().getSessioniList()
                    .getTable();
            /* 1) Inserisco tutti i valori dei PRE e tutti "0" nei POST */
            int count = 0;
            for (int i = inizio; i < fine; i++) {
                verificatiPre[count] = monVersErr.getRow(i).getFlSessioneErrVerif() != null
                        ? monVersErr.getRow(i).getFlSessioneErrVerif() : "0";
                nonRisolubiliPre[count] = monVersErr.getRow(i).getFlSessioneErrNonRisolub() != null
                        ? monVersErr.getRow(i).getFlSessioneErrNonRisolub() : "0";
                verificatiPost[count] = "0";
                nonRisolubiliPost[count] = "0";
                count++;
            }
        }

        /* 2) Ora nei POST metto gli "1" dove vanno messi */
        for (int j = 0; j < totVerificati; j++) {
            if (StringUtils.isNumeric(indiceAssolutoVerificatiSettati[j])) {
                int posizione = Integer.parseInt(indiceAssolutoVerificatiSettati[j]);
                verificatiPost[posizione - inizio] = "1";
            }
        }

        /*
         * 3) Ora che ho i record prima e dopo le eventuali modifiche, ricavo gli indici dei record "Verificati"
         * modificati
         */
        for (int k = 0; k < verificatiPre.length; k++) {
            if (!verificatiPre[k].equals(verificatiPost[k])) {
                verificatiNonRisolubiliModificati.add(k + inizio);
            }
        }

        /* Idem con patate per i "Non risolubili" */
        for (int j = 0; j < totNonRisolubili; j++) {
            if (StringUtils.isNumeric(indiceAssolutoNonRisolubiliSettati[j])) {
                int posizione = Integer.parseInt(indiceAssolutoNonRisolubiliSettati[j]);
                nonRisolubiliPost[posizione - inizio] = "1";
            }
        }

        /* Idem con patate per i "Non risolubili" */
        for (int k = 0; k < nonRisolubiliPre.length; k++) {
            if (!nonRisolubiliPre[k].equals(nonRisolubiliPost[k])) {
                verificatiNonRisolubiliModificati.add(k + inizio);
            }
        }

        // Valida i campi di chiave unita doc e chiave doc
        MonitoraggioValidator validator = new MonitoraggioValidator(getMessageBox());

        /* CONTROLLI DI COERENZA SOLO SUGLI ELEMENTI MODIFICATI */
        /* Tra i "Verificati" e "Non Risolubili */
        for (int j = 0; j < verificatiPost.length; j++) {
            if (verificatiNonRisolubiliModificati.contains(Integer.valueOf(j + inizio))) {
                validator.validaFlagVerificatoNonRisolubile(verificatiPost[j], nonRisolubiliPost[j]);
                if (getMessageBox().hasError()) {
                    break;
                }
            }
        }

        if (!getMessageBox().hasError()) {
            /* Se ci sono state modifiche, le salvo su DB */
            if (!verificatiNonRisolubiliModificati.isEmpty()) {
                /* Ottengo gli id sessione dei record "verificati" MODIFICATI */
                if (indiceAssolutoVerificatiSettati != null) {
                    for (String comp : indiceAssolutoVerificatiSettati) {
                        if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
                            idSessioniVerificateHS.add(getForm().getSessioniList().getTable()
                                    .getRow(Integer.parseInt(comp)).getBigDecimal("id_sessione_vers"));
                        }
                    }
                }

                // Ottengo gli id sessione dei record "non risolubili" (flag spuntato)
                if (indiceAssolutoNonRisolubiliSettati != null) {
                    for (String comp : indiceAssolutoNonRisolubiliSettati) {
                        if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
                            idSessioniNonRisolubHS.add(getForm().getSessioniList().getTable()
                                    .getRow(Integer.parseInt(comp)).getBigDecimal("id_sessione_vers"));
                        }
                    }
                }

                try {
                    if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse().equals("UNITA_DOC")) {
                        idSesModificate = monitoraggioEjb.aggiornaSessioni(verificatiNonRisolubiliModificati,
                                idSessioniVerificateHS, idSessioniNonRisolubHS,
                                (MonVLisVersUdNonVersTableBean) getForm().getSessioniList().getTable());
                    } else {
                        idSesModificate = monitoraggioEjb.aggiornaSessioni(verificatiNonRisolubiliModificati,
                                idSessioniVerificateHS, idSessioniNonRisolubHS,
                                (MonVLisVersDocNonVersTableBean) getForm().getSessioniList().getTable());
                    }

                    // Da rieseguire una volta effettuato il salvataggio nella pagina di dettaglio documenti derivanti
                    // da versamenti falliti
                    if (getForm().getDocumentiDerivantiDaVersFallitiDetail().getId_strut().getValue() != null) {
                        // Rieseguo la query in modo tale da portare a video le eventuali modifiche
                        BigDecimal idStrut = getForm().getDocumentiDerivantiDaVersFallitiDetail().getId_strut().parse();
                        String registro = getForm().getDocumentiDerivantiDaVersFallitiDetail()
                                .getCd_registro_key_unita_doc().parse();
                        BigDecimal anno = getForm().getDocumentiDerivantiDaVersFallitiDetail().getAa_key_unita_doc()
                                .parse();
                        String numero = getForm().getDocumentiDerivantiDaVersFallitiDetail().getCd_key_unita_doc()
                                .parse();
                        String docVers = getForm().getDocumentiDerivantiDaVersFallitiDetail().getCd_key_doc_vers()
                                .parse();
                        int pageSize = getForm().getSessioniList().getTable().getPageSize();
                        if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse()
                                .equals("UNITA_DOC")) {
                            MonVLisVersUdNonVersTableBean listaVersErr = monitoraggioHelper
                                    .getMonVLisVersUdNonVersViewBean(idStrut, registro, anno, numero);
                            getForm().getSessioniList().setTable(listaVersErr);
                            getForm().getSessioniList().getTable().setPageSize(pageSize);
                            getForm().getSessioniList().getTable().first();
                        } else {
                            MonVLisVersDocNonVersTableBean listaVersErr = monitoraggioHelper
                                    .getMonVLisVersDocNonVersViewBean(idStrut, registro, anno, numero, docVers);
                            getForm().getSessioniList().setTable(listaVersErr);
                            getForm().getSessioniList().getTable().setPageSize(pageSize);
                            getForm().getSessioniList().getTable().first();
                        }

                        // rieseguo la query se necessario
                        this.lazyLoadGoPage(getForm().getSessioniList(), paginaCorrente);
                        // ritorno alla pagina
                        getForm().getSessioniList().getTable().setCurrentRowIndex(inizio);

                        if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse() != null) {
                            /*
                             * Ricarico anche la parte di dettaglio in quanto anche su di questa le modifiche possono
                             * essere andate ad impattare
                             */
                            if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse()
                                    .equals("UNITA_DOC")) {
                                MonVVisUdNonVersRowBean nonVersRB = monitoraggioHelper
                                        .getMonVVisUdNonVersRowBean(idStrut, registro, anno, numero);
                                // Copio nella form di dettaglio i dati del rowbean
                                getForm().getDocumentiDerivantiDaVersFallitiDetail().copyFromBean(nonVersRB);
                            } else {
                                MonVVisDocNonVersRowBean nonVersRB = monitoraggioHelper
                                        .getMonVVisDocNonVersRowBean(idStrut, registro, anno, numero, docVers);
                                // Copio nella form di dettaglio i dati del rowbean
                                getForm().getDocumentiDerivantiDaVersFallitiDetail().copyFromBean(nonVersRB);
                            }
                        }
                    }

                    // Segnalo l'avvenuta impostazione dei flaggozzi andata a buon fine
                    String modify = idSesModificate.size() == 1 ? "modificata" : "modificate";
                    String ses = idSesModificate.size() == 1 ? "sessione" : "sessioni";
                    String messageOK = "Aggiornamento effettuato con successo: " + modify + " " + idSesModificate.size()
                            + " " + ses;
                    getMessageBox().addMessage(new Message(MessageLevel.INF, messageOK));
                    getMessageBox().setViewMode(ViewMode.plain);
                } catch (ParerUserError e) {
                    getMessageBox().addMessage(new Message(MessageLevel.ERR, e.getDescription()));
                } finally {
                    forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_DETAIL);
                }
            } // Altrimenti segnalo il fatto di non aver apportato modifiche sul DB
            else {
                getMessageBox().addMessage(new Message(MessageLevel.WAR,
                        "Aggiornamento non effettuato in quanto non sono state apportate modifiche ai record"));
                getMessageBox().setViewMode(ViewMode.plain);
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_DETAIL);
    }

    /**
     * Metodo invocato quando viene cliccato sul tasto "Imposta Verifica Sessione" nella pagina della Lista Sessioni
     * Fallite per settare i flag "Verificato" della lista
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void salvaVerificaSessione() throws Throwable {
        getForm().getFiltriSessione().post(getRequest());
        String flaggozzo = getForm().getFiltriSessione().getSessione_ses_err_verif().parse();
        // Ottengo i riferimenti ai componenti selezionati (spuntati) dalla lista
        String[] verificati = getRequest().getParameterValues("Fl_sessione_err_verif");
        int totVerificati = verificati != null ? verificati.length : 0;
        HashSet<BigDecimal> idSessioneHS = new HashSet<>();
        // Ricavo dei valori utili al fine della memorizzazione su DB dei flag
        int riga = 0;
        int inizio = getForm().getSessioniErrateList().getTable().getFirstRowPageIndex();
        int fine = getForm().getSessioniErrateList().getTable().getFirstRowPageIndex()
                + getForm().getSessioniErrateList().getTable().getPageSize();
        int pageSize = getForm().getSessioniErrateList().getTable().getPageSize();
        int paginaCorrente = getForm().getSessioniErrateList().getTable().getCurrentPageIndex();
        int ultimaPagina = getForm().getSessioniErrateList().getTable().getPages();

        // Se mi trovo nell'ultima pagina, setto il valore di "fine" con le dimensioni della tabella
        if (paginaCorrente == ultimaPagina) {
            fine = getForm().getSessioniErrateList().getTable().size();
        }

        /*
         * Ricavo i valori delle checkbox prima di eventuali modifiche su di essi e li confronto per vedere se ci sono
         * state modifiche
         */
        VrsSessioneVersKoTableBean monSesErr = (VrsSessioneVersKoTableBean) getForm().getSessioniErrateList()
                .getTable();
        String[] verificatiPre = new String[fine - inizio];
        String[] verificatiPost = new String[fine - inizio];

        // Ricavo i valori delle checkbox "verificate" prima e dopo eventuali modifiche
        int count = 0;
        for (int i = inizio; i < fine; i++) {
            verificatiPre[count] = monSesErr.getRow(i).getFlSessioneErrVerif();
            verificatiPost[count] = "0";
            count++;
        }
        for (int j = 0; j < totVerificati; j++) {
            if (StringUtils.isNumeric(verificati[j])) {
                int posizione = Integer.parseInt(verificati[j]);
                verificatiPost[posizione - inizio] = "1";
            }
        }
        boolean modificati = false;
        for (int k = 0; k < verificatiPre.length; k++) {
            if (!verificatiPre[k].equals(verificatiPost[k])) {
                modificati = true;
                break;
            }
        }

        // Se ci sono state modifiche, le salvo su DB
        if (modificati) {
            // Ottengo gli id sessione dei record "verificati" con il flag spuntato (dopo eventuale modifica)
            if (verificati != null) {
                for (String comp : verificati) {
                    if (StringUtils.isNotBlank(comp) && StringUtils.isNumeric(comp)) {
                        idSessioneHS.add(getForm().getSessioniErrateList().getTable().getRow(Integer.parseInt(comp))
                                .getBigDecimal("id_sessione_vers"));
                    }
                }
            }

            // Risetto i flag in base alle modifiche
            for (int i = 0; i < fine - inizio; i++) {
                riga = inizio + i;
                BigDecimal idSesErr = getForm().getSessioniErrateList().getTable().getRow(riga)
                        .getBigDecimal("id_sessione_vers");

                if (idSessioneHS.contains(idSesErr)) {
                    // metti il flag a 1 nella tabella VrsSessioneVers
                    monitoraggioHelper.saveFlVerificati(idSesErr, "1");
                    log.debug("Ho impostato il flaggozzo 'verificata' di sessione a 1");
                } else {
                    // metti il flag a 0
                    monitoraggioHelper.saveFlVerificati(idSesErr, "0");
                    log.debug("Ho impostato il flaggozzo 'verificata' di sessione a 0");
                }
            }

            try {
                /*
                 * Se non avevo il filtro impostato (su s\u00e0¬ o no, e dunque posso restare dov'ero) rieseguo la query
                 */
                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                VrsSessioneVersKoTableBean listaSessErr = monitoraggioHelper.getSessioniErrateListTB(flaggozzo,
                        Integer.parseInt(maxResultStandard));
                getForm().getSessioniErrateList().setTable(listaSessErr);
                getForm().getSessioniErrateList().getTable().setPageSize(pageSize);
                getForm().getSessioniErrateList().getTable().first();
                // rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getSessioniErrateList(), paginaCorrente);
                // ritorno alla pagina
                getForm().getSessioniErrateList().getTable().setCurrentRowIndex(inizio);
                // Segnalo l'avvenuta impostazione dei flaggozzi andata a buon fine
                getMessageBox().addMessage(new Message(MessageLevel.INF, "Aggiornamento effettuato con successo"));
                getMessageBox().setViewMode(ViewMode.plain);
            } catch (Exception e) {
                log.error("Errore nell'impostazione del flag verificato");
                getMessageBox()
                        .addMessage(new Message(MessageLevel.ERR, "Errore nell'impostazione del flag verificato"));
            } finally {
                forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
            }
        } // Altrimenti segnalo il fatto di non aver apportato modifiche sul DB
        else {
            getMessageBox().addMessage(new Message(MessageLevel.WAR,
                    "Aggiornamento non effettuato in quanto non sono state apportate modifiche ai record"));
            getMessageBox().setViewMode(ViewMode.plain);
            forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
        }
    }

    /**
     * Metodo attivato alla pressione del tasto modifica nel dettaglio di una sessione errata
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateSessioniErrateList() throws EMFError {
        // Setto in edit mode il campo del flag sessione errata verificata
        getForm().getSessioniErrateDetail().getFl_sessione_err_verif().setEditMode();
        // Se la sessione errata \u00e0¨ di tipo VERSAMENTO posso inserire manualmente la chiave unit\u00e0 
        // documentaria
        if (getForm().getSessioniErrateDetail().getTi_sessione_vers().getValue().contains("VERSAMENTO")) {
            getForm().getSessioniErrateDetail().getCd_registro_key_unita_doc().setEditMode();
            getForm().getSessioniErrateDetail().getAa_key_unita_doc().setEditMode();
            getForm().getSessioniErrateDetail().getCd_key_unita_doc().setEditMode();
        } // Se la sessione errata \u00e0¨ di tipo AGGIUNGI_DOC posso inserire manualmente la chiave unit\u00e0 
          // documentaria e la chiave documento
        else if (getForm().getSessioniErrateDetail().getTi_sessione_vers().getValue().contains("AGGIUNGI_DOC")) {
            getForm().getSessioniErrateDetail().getCd_registro_key_unita_doc().setEditMode();
            getForm().getSessioniErrateDetail().getAa_key_unita_doc().setEditMode();
            getForm().getSessioniErrateDetail().getCd_key_unita_doc().setEditMode();
            getForm().getSessioniErrateDetail().getCd_key_doc_vers().setEditMode();
        }

        /*
         * Aggiungo la possibilit\u00e0  di impostare ambiente, ente e struttura per calcolare poi l'id strut e
         * convertire cos\u00e0¬ una sessione errata in un versamento fallito
         */
        // Imposto le combo in editMode
        getForm().getSessioniErrateDetail().getId_ambiente().setEditMode();
        getForm().getSessioniErrateDetail().getId_ente().setEditMode();
        getForm().getSessioniErrateDetail().getId_strut().setEditMode();

        // Inizializzo le combo
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getSessioniErrateDetail().getId_ambiente().setDecodeMap(mappaAmbiente);

        /*
         * Se ho un solo ambiente lo setto gi\u00e0  impostato nella combo e procedo con i controlli successivi
         */
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getSessioniErrateDetail().getId_ambiente()
                    .setValue(tmpTableBeanAmbiente.getRow(0).getIdAmbiente().toString());
            BigDecimal idAmbiente = tmpTableBeanAmbiente.getRow(0).getIdAmbiente();
            checkUniqueAmbienteInCombo(idAmbiente, ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE);
        } /*
           * altrimenti imposto la combo ambiente con i diversi valori ma senza averne selezionato uno in particolare e
           * imposto vuote le altre combo
           */ else {
            getForm().getSessioniErrateDetail().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getSessioniErrateDetail().getId_strut().setDecodeMap(new DecodeMap());
        }

        getForm().getSessioniErrateList().setStatus(Status.update);
        // Imposto in sessione l'info sulla provenienza: mi servir\u00e0  in fase di conferma salvataggio o annullamento
        getSession().setAttribute("provenienza", "SE");
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
    }

    /**
     * Metodo attivato alla pressione del tasto modifica nel dettaglio di un versamento fallito
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void updateVersamentiFallitiList() throws EMFError {
        // Setto in edit mode i campi del flag versamento fallito verificato e non risolubile
        getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif().setEditMode();
        getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub().setEditMode();
        getForm().getVersamentiFallitiList().setStatus(Status.update);
        // Imposto in sessione l'info sula provenienza: mi servir\u00e0  in fase di conferma salvataggio o annullamento
        getSession().setAttribute("provenienza", "VFdaVFList");
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
    }

    @Override
    public void updateSessioniList() throws EMFError {
        // Setto in edit mode i campi del flag versamento fallito verificato e non risolubile
        getForm().getVersamentiFallitiDetail().getFl_sessione_err_verif().setEditMode();
        getForm().getVersamentiFallitiDetail().getFl_sessione_err_non_risolub().setEditMode();
        getForm().getSessioniList().setStatus(Status.update);
        // Imposto in sessione l'info sula provenienza: mi servir\u00e0  in fase di conferma salvataggio o annullamento
        getSession().setAttribute("provenienza", "VFdaSessioniList");
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
    }

    /**
     * Metodo invocato alla pressione del tasto Calcola Struttura Versante della pagina Lista Sessioni Errate, per
     * eseguire l'update della tabella VRS_SESSIONE_VERS
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void calcolaStrutturaVersante() throws EMFError {
        getMessageBox().addMessage(new Message(MessageLevel.WAR,
                "Eseguire il calcolo della struttura versante e della chiave unit\u00e0  doc.?"));
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
    }

    @Override
    public void confermaCalcolo() throws Throwable {
        boolean success = calcoloAsync.calcolaStruttura();
        if (success) {
            getMessageBox().addInfo("Calcolo della struttura versante lanciato con successo");
        } else {
            getMessageBox().addError("Calcolo della struttura versante gi\u00E0 in corso");
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
    }

    @Override
    public void annullaCalcolo() throws Throwable {
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
    }

    /**
     * Trigger sul filtro nome ambiente di Riepilogo Versamenti: selezionando un valore della combo box viene popolata
     * la combo relativa al nome ente, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerRiepilogoVersamentiId_ambienteOnTrigger() throws EMFError {
        // Azzero i totali della tabella riepilogo versamenti
        triggerAmbienteGenerico(getForm().getRiepilogoVersamenti(),
                ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
        return getForm().getRiepilogoVersamenti().asJSON();
    }

    /**
     * Trigger sul filtro nome ente di Riepilogo Versamenti: selezionando un valore della combo box viene popolata la
     * combo relativa al nome struttura, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerRiepilogoVersamentiId_enteOnTrigger() throws EMFError {
        // // Azzero i totali della tabella riepilogo versamenti
        triggerEnteGenerico(getForm().getRiepilogoVersamenti(), ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
        return getForm().getRiepilogoVersamenti().asJSON();
    }

    /**
     * Trigger sul filtro nome struttura di Riepilogo Versamenti: selezionando un valore della combo box viene popolata
     * la combo relativa al nome tipo unit\u00e0 doc, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerRiepilogoVersamentiId_strutOnTrigger() throws EMFError {
        // // Azzero i totali della tabella riepilogo versamenti
        triggerStrutGenerico(getForm().getRiepilogoVersamenti());
        return getForm().getRiepilogoVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerSessioniErrateDetailId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getSessioniErrateDetail(), ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE);
        return getForm().getSessioniErrateDetail().asJSON();
    }

    @Override
    public JSONObject triggerSessioniErrateDetailId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getSessioniErrateDetail(), ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE);
        return getForm().getSessioniErrateDetail().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdDocDerivantiDaVersFallitiId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriUdDocDerivantiDaVersFalliti(),
                ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS);
        return getForm().getFiltriUdDocDerivantiDaVersFalliti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdDocDerivantiDaVersFallitiId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriUdDocDerivantiDaVersFalliti(),
                ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS);
        return getForm().getFiltriUdDocDerivantiDaVersFalliti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdDocDerivantiDaVersFallitiId_strutOnTrigger() throws EMFError {
        getForm().getFiltriUdDocDerivantiDaVersFalliti().post(getRequest());
        if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().parse() != null) {
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getVerificaVersamentiFalliti().setEditMode();
        } else {
            getForm().getFiltriUdDocDerivantiDaVersFalliti().getVerificaVersamentiFalliti().setViewMode();
        }
        return getForm().getFiltriUdDocDerivantiDaVersFalliti().asJSON();
    }

    /**
     * Trigger sul filtro "Sessione Errata Verificata" della pagina Lista Sessioni Errate: selezionando un valore della
     * combo box viene rieseguita la ricerca con il nuovo filtro impostato
     *
     * @throws EMFError
     *             errore generico
     */
    public void filtraSessioniVerificate() throws EMFError {
        getForm().getFiltriSessione().post(getRequest());
        // Setto la lista delle sessioni fallite
        String maxResultStandard = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
        VrsSessioneVersKoTableBean listaSessErr = monitoraggioHelper.getSessioniErrateListTB(
                getForm().getFiltriSessione().getSessione_ses_err_verif().parse(), Integer.parseInt(maxResultStandard));
        getForm().getSessioniErrateList().setTable(listaSessErr);
        getForm().getSessioniErrateList().getTable().setPageSize(10);
        getForm().getSessioniErrateList().getTable().first();
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_RICERCA);
    }

    /**
     * Trigger sul filtro nome ambiente di Esame Operazioni Volumi: selezionando un valore della combo box viene
     * popolata la combo relativa al nome ente, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriOperazioniVolumiId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriOperazioniVolumi(),
                ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI);
        return getForm().getFiltriOperazioniVolumi().asJSON();
    }

    /**
     * Trigger sul filtro nome ente di Esame Operazioni Volumi: selezionando un valore della combo box viene popolata la
     * combo relativa al nome struttura, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriOperazioniVolumiId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriOperazioniVolumi(), ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI);
        return getForm().getFiltriOperazioniVolumi().asJSON();
    }

    /**
     * Trigger sul filtro nome ambiente di Esame Contenuto Sacer: selezionando un valore della combo box viene popolata
     * la combo relativa al nome ente, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriContenutoSacerId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());
        // Ricavo gli eventuali enti già in precedenza settati
        List<BigDecimal> idEnteList = getForm().getFiltriContenutoSacer().getId_ente().parse();
        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit1Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_1()
                .parse();
        List<BigDecimal> idAmbitoTerrit2Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2()
                .parse();
        List<BigDecimal> idAmbitoTerrit3Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3()
                .parse();
        List<BigDecimal> idAmbitoTerrit = struttureEjb.getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello,
                idAmbitoTerrit2Livello, idAmbitoTerrit3Livello);

        List<BigDecimal> idAmbiente = getForm().getFiltriContenutoSacer().getId_ambiente().parse();
        List<BigDecimal> idCategEnte = getForm().getFiltriContenutoSacer().getId_categ_ente().parse();
        // SOLO SE ALMENO UNO DEI FILTRI CHE VINCOLANO IL FILTRO ENTE E' STATO POPOLATO, ALLORA MI PREOCCUPA TI TRATTARE
        // IL FILTRO ENTE
        if (!idAmbiente.isEmpty() || !idAmbitoTerrit.isEmpty() || !idCategEnte.isEmpty()) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, idAmbitoTerrit, idCategEnte, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(mappaEnte);
            // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
            // territoriale
            if (!enteTableBean.isEmpty()) {
                if (!idEnteList.isEmpty()) {
                    String[] enteArray = new String[idEnteList.size()];
                    for (int i = 0; i < idEnteList.size(); i++) {
                        enteArray[i] = "" + idEnteList.get(i);
                    }
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(enteArray);
                } else {
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(new String[0]);
                    getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriContenutoSacer().asJSON();
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_ambito_territ_livello_1OnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());
        // Ricavo gli eventuali enti già in precedenza settati
        List<BigDecimal> idEnteList = getForm().getFiltriContenutoSacer().getId_ente().parse();
        List<BigDecimal> idAmbitoTerrit2LivelloList = getForm().getFiltriContenutoSacer()
                .getId_ambito_territ_livello_2().parse();
        List<BigDecimal> idAmbitoTerrit3LivelloList = getForm().getFiltriContenutoSacer()
                .getId_ambito_territ_livello_3().parse();
        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit1Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_1()
                .parse();
        List<BigDecimal> idAmbiente = getForm().getFiltriContenutoSacer().getId_ambiente().parse();
        List<BigDecimal> idCategEnte = getForm().getFiltriContenutoSacer().getId_categ_ente().parse();

        // Ricavo il TableBean relativo agli ambiti territoriali di secondo livello
        OrgAmbitoTerritTableBean ambito2LivelloTableBean = ambienteEjb
                .getOrgAmbitoTerritChildTableBean(idAmbitoTerrit1Livello);
        DecodeMap mappaAmbitoTerrit2Livello = new DecodeMap();
        mappaAmbitoTerrit2Livello.populatedMap(ambito2LivelloTableBean, "id_ambito_territ", "cd_ambito_territ");
        getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2().setDecodeMap(mappaAmbitoTerrit2Livello);
        // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
        // territoriale
        // GESTIONE 2° LIVELLO
        if (!mappaAmbitoTerrit2Livello.isEmpty()) {
            if (!idAmbitoTerrit2LivelloList.isEmpty()) {
                String[] ambitoArray = new String[idAmbitoTerrit2LivelloList.size()];
                for (int i = 0; i < idAmbitoTerrit2LivelloList.size(); i++) {
                    ambitoArray[i] = "" + idAmbitoTerrit2LivelloList.get(i);
                }
                getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2().setValues(ambitoArray);

                // Gestione 3° livello
                List<BigDecimal> id2LivelloSelezionatiNuovi = new ArrayList<>();
                for (OrgAmbitoTerritRowBean ambito2LivelloRowBean : ambito2LivelloTableBean) {
                    for (String ambito : ambitoArray) {
                        if (ambito2LivelloRowBean.getIdAmbitoTerrit().compareTo(new BigDecimal(ambito)) == 0) {
                            id2LivelloSelezionatiNuovi.add(ambito2LivelloRowBean.getIdAmbitoTerrit());
                        }
                    }
                }
                check3LivelloAmbitoTerritoriale(id2LivelloSelezionatiNuovi, idAmbitoTerrit3LivelloList);
            } else {
                getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2().setValues(new String[0]);
                getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2().setDecodeMap(new DecodeMap());
            getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().setDecodeMap(new DecodeMap());
        }

        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit2Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2()
                .parse();
        List<BigDecimal> idAmbitoTerrit3Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3()
                .parse();

        List<BigDecimal> idAmbitoTerrit = struttureEjb.getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello,
                idAmbitoTerrit2Livello, idAmbitoTerrit3Livello);

        // SOLO SE ALMENO UNO DEI FILTRI CHE VINCOLANO IL FILTRO ENTE E' STATO POPOLATO, ALLORA MI PREOCCUPA TI TRATTARE
        // IL FILTRO ENTE
        if (!idAmbiente.isEmpty() || !idAmbitoTerrit.isEmpty() || !idCategEnte.isEmpty()) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambito territoriale scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, idAmbitoTerrit, idCategEnte, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(mappaEnte);
            // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
            // territoriale
            if (!enteTableBean.isEmpty()) {
                if (!idEnteList.isEmpty()) {
                    String[] enteArray = new String[idEnteList.size()];
                    for (int i = 0; i < idEnteList.size(); i++) {
                        enteArray[i] = "" + idEnteList.get(i);
                    }
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(enteArray);
                } else {
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(new String[0]);
                    getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriContenutoSacer().asJSON();
    }

    /**
     * Dati in input gli ambiti di 2° livello "nuovi" (a seguito delle modifiche dell'utente), bisogna ricavare i nuovi
     * ambiti di terzo livello e riselezionare gli eventuali ambiti di 3° livello precedentemente selezionati, se ancora
     * validi
     *
     * @param idAmbitoTerrit2Livello
     *            quelli SELEZIONATI ONLINE "NUOVI" di secondo livello
     * @param idAmbitoTerrit3LivelloList
     *            quelli SELEZIONATI ONLINE "VECCHI" di terzo livello
     *
     * @throws EMFError
     *             errore generico
     */
    private void check3LivelloAmbitoTerritoriale(List<BigDecimal> idAmbitoTerrit2Livello,
            List<BigDecimal> idAmbitoTerrit3LivelloList) {

        // Ricavo il TableBean relativo agli ambiti territoriali di terzo livello in base ai selezionati di secondo
        // livello
        OrgAmbitoTerritTableBean ambito3LivelloTableBean = ambienteEjb
                .getOrgAmbitoTerritChildTableBean(idAmbitoTerrit2Livello);
        DecodeMap mappaAmbitoTerrit3Livello = new DecodeMap();
        mappaAmbitoTerrit3Livello.populatedMap(ambito3LivelloTableBean, "id_ambito_territ", "cd_ambito_territ");
        getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().setDecodeMap(mappaAmbitoTerrit3Livello);
        // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
        // territoriale
        if (!mappaAmbitoTerrit3Livello.isEmpty()) {
            if (!idAmbitoTerrit3LivelloList.isEmpty()) {
                String[] ambitoArray = new String[idAmbitoTerrit3LivelloList.size()];
                for (int i = 0; i < idAmbitoTerrit3LivelloList.size(); i++) {
                    ambitoArray[i] = "" + idAmbitoTerrit3LivelloList.get(i);
                }
                getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().setValues(ambitoArray);
            } else {
                getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().setValues(new String[0]);
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().setDecodeMap(new DecodeMap());
        }
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_ambito_territ_livello_2OnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());
        // Ricavo gli eventuali enti già in precedenza settati
        List<BigDecimal> idEnteList = getForm().getFiltriContenutoSacer().getId_ente().parse();
        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit2Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2()
                .parse();
        List<BigDecimal> idAmbiente = getForm().getFiltriContenutoSacer().getId_ambiente().parse();
        List<BigDecimal> idCategEnte = getForm().getFiltriContenutoSacer().getId_categ_ente().parse();

        check3LivelloAmbitoTerritoriale(idAmbitoTerrit2Livello,
                getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3().parse());

        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit1Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_1()
                .parse();
        List<BigDecimal> idAmbitoTerrit3Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3()
                .parse();

        List<BigDecimal> idAmbitoTerrit = struttureEjb.getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello,
                idAmbitoTerrit2Livello, idAmbitoTerrit3Livello);

        // SOLO SE ALMENO UNO DEI FILTRI CHE VINCOLANO IL FILTRO ENTE E' STATO POPOLATO, ALLORA MI PREOCCUPA TI TRATTARE
        // IL FILTRO ENTE
        if (!idAmbiente.isEmpty() || !idAmbitoTerrit.isEmpty() || !idCategEnte.isEmpty()) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambito territoriale scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, idAmbitoTerrit, idCategEnte, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(mappaEnte);
            // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
            // territoriale
            if (!enteTableBean.isEmpty()) {
                if (!idEnteList.isEmpty()) {
                    String[] enteArray = new String[idEnteList.size()];
                    for (int i = 0; i < idEnteList.size(); i++) {
                        enteArray[i] = "" + idEnteList.get(i);
                    }
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(enteArray);
                } else {
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(new String[0]);
                    getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriContenutoSacer().asJSON();
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_ambito_territ_livello_3OnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());
        // Ricavo gli eventuali enti già in precedenza settati
        List<BigDecimal> idEnteList = getForm().getFiltriContenutoSacer().getId_ente().parse();
        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit3Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3()
                .parse();
        List<BigDecimal> idAmbiente = getForm().getFiltriContenutoSacer().getId_ambiente().parse();
        List<BigDecimal> idCategEnte = getForm().getFiltriContenutoSacer().getId_categ_ente().parse();

        List<BigDecimal> idAmbitoTerrit1Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_1()
                .parse();
        List<BigDecimal> idAmbitoTerrit2Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2()
                .parse();

        List<BigDecimal> idAmbitoTerrit = struttureEjb.getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello,
                idAmbitoTerrit2Livello, idAmbitoTerrit3Livello);

        // SOLO SE ALMENO UNO DEI FILTRI CHE VINCOLANO IL FILTRO ENTE E' STATO POPOLATO, ALLORA MI PREOCCUPA TI TRATTARE
        // IL FILTRO ENTE
        if (!idAmbiente.isEmpty() || !idAmbitoTerrit.isEmpty() || !idCategEnte.isEmpty()) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambito territoriale scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, idAmbitoTerrit, idCategEnte, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(mappaEnte);
            // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
            // territoriale
            if (!enteTableBean.isEmpty()) {
                if (!idEnteList.isEmpty()) {
                    String[] enteArray = new String[idEnteList.size()];
                    for (int i = 0; i < idEnteList.size(); i++) {
                        enteArray[i] = "" + idEnteList.get(i);
                    }
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(enteArray);
                } else {
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(new String[0]);
                    getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriContenutoSacer().asJSON();
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_categ_enteOnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());
        // Ricavo gli eventuali enti già in precedenza selezionati
        List<BigDecimal> idEnteList = getForm().getFiltriContenutoSacer().getId_ente().parse();
        // Ricavo i filtri
        List<BigDecimal> idAmbitoTerrit1Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_1()
                .parse();
        List<BigDecimal> idAmbitoTerrit2Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_2()
                .parse();
        List<BigDecimal> idAmbitoTerrit3Livello = getForm().getFiltriContenutoSacer().getId_ambito_territ_livello_3()
                .parse();

        List<BigDecimal> idAmbitoTerrit = struttureEjb.getIdAmbitoTerritorialePerRicerca(idAmbitoTerrit1Livello,
                idAmbitoTerrit2Livello, idAmbitoTerrit3Livello);

        List<BigDecimal> idAmbiente = getForm().getFiltriContenutoSacer().getId_ambiente().parse();
        List<BigDecimal> idCategEnte = getForm().getFiltriContenutoSacer().getId_categ_ente().parse();

        // SOLO SE ALMENO UNO DEI FILTRI CHE VINCOLANO IL FILTRO ENTE E' STATO POPOLATO, ALLORA MI PREOCCUPA TI TRATTARE
        // IL FILTRO ENTE
        if (!idAmbiente.isEmpty() || !idAmbitoTerrit.isEmpty() || !idCategEnte.isEmpty()) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambito territoriale scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitatiAmbitoCateg(getUser().getIdUtente(),
                    idAmbiente, idAmbitoTerrit, idCategEnte, Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(mappaEnte);
            // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
            // territoriale
            if (!enteTableBean.isEmpty()) {
                if (!idEnteList.isEmpty()) {
                    String[] enteArray = new String[idEnteList.size()];
                    for (int i = 0; i < idEnteList.size(); i++) {
                        enteArray[i] = "" + idEnteList.get(i);
                    }
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(enteArray);
                } else {
                    getForm().getFiltriContenutoSacer().getId_ente().setValues(new String[0]);
                    getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriContenutoSacer().asJSON();
    }

    /**
     * Trigger sul filtro nome ente di Esame Contenuto Sacer: selezionando un valore della combo box viene popolata la
     * combo relativa al nome struttura, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriContenutoSacerId_enteOnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());

        Set<BigDecimal> idEnte = (!getForm().getFiltriContenutoSacer().getId_ente().getValues().isEmpty()
                ? new HashSet<BigDecimal>(getForm().getFiltriContenutoSacer().getId_ente().parse()) : null);
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(mappaStrut);
        } else {
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriContenutoSacer().asJSON();
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_categ_strutOnTrigger() throws EMFError {
        getForm().getFiltriContenutoSacer().post(getRequest());
        // Ricavo le eventuali strutture già in precedenza settate
        List<BigDecimal> idStrutList = getForm().getFiltriContenutoSacer().getId_strut().parse();
        // Ricavo i filtri
        List<BigDecimal> idCategStrut = getForm().getFiltriContenutoSacer().getId_categ_strut().parse();
        List<BigDecimal> idEnte = getForm().getFiltriContenutoSacer().getId_ente().parse();
        OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                idCategStrut);
        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
        getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(mappaStrut);
        // Setto i valori precedentemente settati, se ancora possibile dopo l'applicazione del nuovo filtro ambito
        // territoriale
        if (!strutTableBean.isEmpty()) {
            if (!idStrutList.isEmpty()) {
                String[] strutArray = new String[idStrutList.size()];
                for (int i = 0; i < idStrutList.size(); i++) {
                    strutArray[i] = "" + idStrutList.get(i);
                }
                getForm().getFiltriContenutoSacer().getId_strut().setValues(strutArray);
            } else {
                getForm().getFiltriContenutoSacer().getId_strut().setValues(new String[0]);
            }
        } else {
            getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriContenutoSacer().asJSON();
    }

    public Fields<?> triggerAmbienteGenerico(Fields<?> campi, SezioneMonitoraggio sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox<?> ambienteCombo = (ComboBox<?>) campi.getComponent("id_ambiente");
        ComboBox<?> enteCombo = (ComboBox<?>) campi.getComponent("id_ente");
        ComboBox<?> strutCombo = (ComboBox<?>) campi.getComponent("id_strut");
        ComboBox<?> tipoUnitaDocCombo = (ComboBox<?>) campi.getComponent("id_tipo_unita_doc");

        // Azzero i valori preimpostati delle varie combo
        enteCombo.setValue("");
        strutCombo.setValue("");
        if (tipoUnitaDocCombo != null) {
            tipoUnitaDocCombo.setValue("");
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
            // Se ho un solo ente lo setto gi\u00e0 impostato nella combo
            if (tmpTableBeanEnte.size() == 1) {
                enteCombo.setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(), sezione);
            } else {
                strutCombo.setDecodeMap(new DecodeMap());
                /*
                 * Siccome non \u00e0¨ sempre sempre presente, controllo che la combo di tipo unita doc sia diversa da
                 * null
                 */
                if (tipoUnitaDocCombo != null) {
                    tipoUnitaDocCombo.setDecodeMap(new DecodeMap());
                }
            }
        } else {
            enteCombo.setDecodeMap(new DecodeMap());
            strutCombo.setDecodeMap(new DecodeMap());
            if (tipoUnitaDocCombo != null) {
                tipoUnitaDocCombo.setDecodeMap(new DecodeMap());
            }
        }
        return campi;
    }

    public Fields<?> triggerEnteGenerico(Fields<?> campi, SezioneMonitoraggio sezione) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox<?> enteCombo = (ComboBox<?>) campi.getComponent("id_ente");
        ComboBox<?> strutCombo = (ComboBox<?>) campi.getComponent("id_strut");
        ComboBox<?> tipoUnitaDocCombo = (ComboBox<?>) campi.getComponent("id_tipo_unita_doc");

        // Azzero i valori preimpostati delle varie combo
        strutCombo.setValue("");
        if (tipoUnitaDocCombo != null) {
            tipoUnitaDocCombo.setValue("");
        }

        BigDecimal idEnte = (!enteCombo.getValue().equals("") ? new BigDecimal(enteCombo.getValue()) : null);
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");
            strutCombo.setDecodeMap(mappaStrut);
            // Se ho una sola struttura la setto gi\u00e0 impostata nella combo
            if (tmpTableBeanStrut.size() == 1) {
                strutCombo.setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                checkUniqueStrutInCombo(tmpTableBeanStrut.getRow(0).getIdStrut(), sezione);
            } else if (tipoUnitaDocCombo != null) {
                tipoUnitaDocCombo.setDecodeMap(new DecodeMap());
            }
        } else {
            strutCombo.setDecodeMap(new DecodeMap());
            if (tipoUnitaDocCombo != null) {
                tipoUnitaDocCombo.setDecodeMap(new DecodeMap());
            }
        }
        return campi;
    }

    public Fields<?> triggerStrutGenerico(Fields<?> campi) throws EMFError {
        campi.post(getRequest());

        // Passaggio per riferimento del "campo"; le modifiche avranno effetto sui "Fields"
        ComboBox<?> strutCombo = (ComboBox<?>) campi.getComponent("id_strut");
        ComboBox<?> tipoUnitaDocCombo = (ComboBox<?>) campi.getComponent("id_tipo_unita_doc");
        ComboBox<?> tipoDocCombo = (ComboBox<?>) campi.getComponent("id_tipo_doc");
        ComboBox<?> regUnitaDocCombo = (ComboBox<?>) campi.getComponent("cd_registro_key_unita_doc");

        /*
         * Azzero i valori preimpostati delle varie combo In questo caso non devo fare controlli su tipoUnitaDoc
         * perch\u00e0¨ se sono entrato in questo trigger vuol dire che la combo esiste
         */
        tipoUnitaDocCombo.setValue("");

        BigDecimal idStrut = (!strutCombo.getValue().equals("") ? new BigDecimal(strutCombo.getValue()) : null);
        if (idStrut != null) {
            // Ricavo il TableBean relativo ai tipi di unit\u00e0 doc dall'ente scelto
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                    idStrut);
            DecodeMap mappaUD = new DecodeMap();
            mappaUD.populatedMap(tmpTableBeanTUD, "id_tipo_unita_doc", "nm_tipo_unita_doc");
            tipoUnitaDocCombo.setDecodeMap(mappaUD);

            if (regUnitaDocCombo != null) {
                regUnitaDocCombo.setDecodeMap(
                        getMappaRegistro(idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()));
            }
            if (tipoDocCombo != null) {
                // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
                DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(
                        idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia(), false, false);
                tipoDocCombo
                        .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc"));
            }
        } else {
            tipoUnitaDocCombo.setDecodeMap(new DecodeMap());
            if (regUnitaDocCombo != null) {
                regUnitaDocCombo.setDecodeMap(new DecodeMap());
            }
            if (tipoDocCombo != null) {
                tipoDocCombo.setDecodeMap(new DecodeMap());
            }
        }
        return campi;
    }

    @Override
    public JSONObject triggerFiltriDocumentiId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriDocumenti(), ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI);
        return getForm().getFiltriDocumenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriDocumentiId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriDocumenti(), ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI);
        return getForm().getFiltriDocumenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriDocumentiId_strutOnTrigger() throws EMFError {
        triggerStrutGenerico(getForm().getFiltriDocumenti());
        return getForm().getFiltriDocumenti().asJSON();
    }

    /**
     * Trigger sul filtro stato documento della lista documenti: selezionando il valore IN_VOLUME_CHIUSO della combo box
     * viene popolata la combo relativa allo stato volume
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     *
     * @throws EMFError
     *             errore generico
     *
     *             public JSONObject triggerFiltriDocumentiTi_stato_docOnTrigger() throws EMFError { // Eseguo la post
     *             del filtro getForm().getFiltriDocumenti().post(getRequest()); String statoDoc =
     *             getForm().getFiltriDocumenti().getTi_stato_doc_elenco_vers().getValue(); if (statoDoc != null) { //
     *             Preparo la combo "Stato volume" if (!statoDoc.equals("IN_VOLUME_CHIUSO")) {
     *             getForm().getFiltriDocumenti().getTi_stato_volume().reset();
     *             getForm().getFiltriDocumenti().getTi_stato_volume().setDecodeMap(new DecodeMap()); } else {
     *             getForm().getFiltriDocumenti().getTi_stato_volume().setDecodeMap(ComboGetter.getMappaStatoVol());
     *             getForm().getFiltriDocumenti().getTi_stato_volume().setValue("CHIUSO"); } } return
     *             getForm().getFiltriDocumenti().asJSON(); }
     */
    @Override
    public JSONObject triggerFiltriVersamentiId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriVersamenti(), ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI);
        // Ricavo nuovamente i valori per la combo "Registro" del tab "Ulteriori filtri"
        // Preparo la combo "Registro", ricavando i valori dalla lista
        // dei versamenti falliti totali
        BigDecimal idAmbiente = getForm().getFiltriVersamenti().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriVersamenti().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriVersamenti().getId_strut().parse();

        DecodeMap mappaReg = getMappaRegistroFromTotaleMonVLisVersErr(idAmbiente, idEnte, idStrut,
                getUser().getIdUtente());
        getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
        getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setEditMode();
        return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriVersamentiId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriVersamenti(), ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI);
        // Ricavo nuovamente i valori per la combo "Registro" del tab "Ulteriori filtri"
        // Preparo la combo "Registro", ricavando i valori dalla lista
        // dei versamenti falliti totali
        BigDecimal idAmbiente = getForm().getFiltriVersamenti().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriVersamenti().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriVersamenti().getId_strut().parse();

        DecodeMap mappaReg = getMappaRegistroFromTotaleMonVLisVersErr(idAmbiente, idEnte, idStrut,
                getUser().getIdUtente());
        getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
        getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setEditMode();
        return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriVersamentiId_strutOnTrigger() throws EMFError {
        getForm().getFiltriVersamenti().post(getRequest());
        // Ricavo nuovamente i valori per la combo "Registro" del tab "Ulteriori filtri"
        // Preparo la combo "Registro", ricavando i valori dalla lista
        // dei versamenti falliti totali
        BigDecimal idAmbiente = getForm().getFiltriVersamenti().getId_ambiente().parse();
        BigDecimal idEnte = getForm().getFiltriVersamenti().getId_ente().parse();
        BigDecimal idStrut = getForm().getFiltriVersamenti().getId_strut().parse();

        DecodeMap mappaReg = getMappaRegistroFromTotaleMonVLisVersErr(idAmbiente, idEnte, idStrut,
                getUser().getIdUtente());
        getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
        getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().setEditMode();
        return getForm().getFiltriVersamenti().asJSON();
    }

    /**
     * Metodo attivato alla pressione del tasto relativo al download dei file xml nel dettaglio di una sessione errata
     *
     * @throws Throwable
     *             errore generico
     */
    @Override
    public void scaricaFileXMLSessione() throws Throwable {
        BigDecimal idSessioneVersKo = (BigDecimal) getSession().getAttribute("idSessioneVers");
        // create tmp file
        File tmpDwnFileXMLSessione = File.createTempFile("dwnxmlsess_idSessioneVers", ".tmp");
        // Ricavo la lista dei blobbi dei file da scaricare
        List<BlobObject> blobbiFileList = monitoraggioHelper.getBlobboByteList(idSessioneVersKo);
        try {
            // Ricavo lo stream di output
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmpDwnFileXMLSessione))) {
                String filename = "";

                // Caccio dentro nello zippone i blobbi
                addBlobObjectsOnZip(blobbiFileList, out);

                byte[] xmlRichiesta = null;
                byte[] xmlRisposta = null;
                if (getForm().getSessioniErrateDetail().getBl_xml_rich().getValue().length() < 1000000000
                        && getForm().getSessioniErrateDetail().getBl_xml_index().getValue().length() < 1000000000) {
                    xmlRichiesta = getForm().getSessioniErrateDetail().getBl_xml_rich().getValue().getBytes();
                    xmlRisposta = getForm().getSessioniErrateDetail().getBl_xml_risp().getValue().getBytes();
                } else {
                    // Carico il rowbean corrispondente all'id ottenuto
                    Object[] xmls = monitoraggioHelper.getXmlsSesErr(idSessioneVersKo);
                    xmlRichiesta = (byte[]) xmls[0];
                    xmlRisposta = (byte[]) xmls[1];
                }
                // Caccio dentro lo zippone il file xml di richiesta
                if (xmlRichiesta != null && xmlRichiesta.length > 0) {
                    filename = "vers_" + idSessioneVersKo + "_richiesta.xml";
                    zippaBlobbo(out, filename, xmlRichiesta);
                }
                // Caccio dentro lo zippone il file xml di indice
                byte[] xmlIndice = getForm().getSessioniErrateDetail().getBl_xml_index().getValue().getBytes();
                if (xmlIndice != null && xmlIndice.length > 0) {
                    filename = "vers_" + idSessioneVersKo + "_indice.xml";
                    zippaBlobbo(out, filename, xmlIndice);
                }
                // Caccio dentro lo zippone il file xml di risposta
                if (xmlRisposta != null && xmlRisposta.length > 0) {
                    filename = "vers_" + idSessioneVersKo + "_risposta.xml";
                    zippaBlobbo(out, filename, xmlRisposta);
                }
                out.flush();
                freeze();
            } catch (Exception e) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR, "Errore durante elaborazione file"));
                log.error("Eccezione", e);
                forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
            }
            // no error
            if (!getMessageBox().hasError()) {
                // prepare anche check final zip file
                try (FileInputStream in = new FileInputStream(tmpDwnFileXMLSessione);
                        ZipFile zipFile = new ZipFile(tmpDwnFileXMLSessione)) {
                    if (zipFile.size() == 0) { // zip file no entries
                        getMessageBox().addMessage(new Message(MessageLevel.WAR, "File non disponibile al download"));
                        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
                    } else {
                        String nomeZippone = "sessioneErrata-" + idSessioneVersKo;
                        getResponse().setContentType("application/zip");
                        getResponse().setHeader("Content-Disposition",
                                "attachment; filename=\"" + nomeZippone + ".zip");
                        getResponse().setContentLengthLong(Files.size(tmpDwnFileXMLSessione.toPath()));
                        IOUtils.copy(in, getServletOutputStream());
                    }
                }
            }
        } finally {
            Files.delete(tmpDwnFileXMLSessione.toPath());
        }
    }

    /*
     * Elabora una lista di BlobObject per aggiungerli come single entry su file zip
     */
    private void addBlobObjectsOnZip(List<BlobObject> blobbiFileList, ZipOutputStream out) throws IOException {
        if (blobbiFileList != null) {
            for (int i = 0; i < blobbiFileList.size(); i++) {
                BlobObject tempBlobbo = blobbiFileList.get(i);
                if (tempBlobbo != null) {

                    long count = monitoraggioEjb.contaComponentiErroreVersamento(tempBlobbo.id);
                    // create tmp file
                    File tmpFileBlobObject = File.createTempFile("dwn_blobobj", ".tmp");
                    try {
                        if (count > 0) {
                            boolean result = false;
                            try (FileOutputStream outEntry = new FileOutputStream(tmpFileBlobObject)) {
                                result = monitoraggioEjb.salvaStreamComponenteDaErroreVersamento(tempBlobbo.id,
                                        outEntry);
                            }
                            if (result) {
                                out.putNextEntry(new ZipEntry(tempBlobbo.name));
                                try (FileInputStream inEntry = new FileInputStream(tmpFileBlobObject)) {
                                    IOUtils.copyLarge(inEntry, out);
                                }
                                out.closeEntry();
                            }
                        }
                    } finally {
                        Files.delete(tmpFileBlobObject.toPath());
                    }
                }
            }
        }
    }

    /*
     * Metodo addetto alla compressione nel file zip del byte array contenente il file originale
     */
    private void zippaBlobbo(ZipOutputStream out, String filename, byte[] blobbo) throws IOException {
        // Ricavo lo stream di input
        InputStream is = new ByteArrayInputStream(blobbo);
        byte[] data = new byte[1024];
        int count;
        out.putNextEntry(new ZipEntry(filename));
        while ((count = is.read(data, 0, 1024)) != -1) {
            out.write(data, 0, count);
        }
        out.closeEntry();
        is.close();
    }

    /**
     * Metodo attivato alla pressione del tasto relativo al download dei file xml e della lista file nel dettaglio di un
     * versamento fallito
     *
     * @throws Throwable
     *             errore generico
     */
    @Override
    public void scaricaFileXMLVersamento() throws Throwable {
        BigDecimal idSessioneVers = (BigDecimal) getSession().getAttribute("idSessioneVers");
        // create tmp file
        File tmpDwnFileXMLVersamento = File.createTempFile("dwnxmlversamento_idSessioneVers", ".tmp");
        // Ricavo la lista dei blobbi dei file da scaricare
        List<BlobObject> blobbiFileList = monitoraggioHelper.getBlobboByteList(idSessioneVers);

        try {
            // Ricavo lo stream di output
            try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(tmpDwnFileXMLVersamento))) {
                String filename = "";

                // Caccio dentro nello zippone i blobbi
                addBlobObjectsOnZip(blobbiFileList, out);

                byte[] xmlRichiesta = null;
                byte[] xmlRisposta = null;
                if (getForm().getVersamentiFallitiDetail().getBl_xml_rich().getValue().length() < 1000000000
                        && getForm().getVersamentiFallitiDetail().getBl_xml_risp().getValue().length() < 1000000000) {
                    xmlRichiesta = getForm().getVersamentiFallitiDetail().getBl_xml_rich().getValue().getBytes();
                    xmlRisposta = getForm().getVersamentiFallitiDetail().getBl_xml_risp().getValue().getBytes();
                } else {
                    // Carico il rowbean corrispondente all'id ottenuto
                    Object[] xmls = monitoraggioHelper.getXmlsVersErr(idSessioneVers);
                    xmlRichiesta = (byte[]) xmls[0];
                    xmlRisposta = (byte[]) xmls[1];
                }
                // Caccio dentro lo zippone il file xml di richiesta
                if (xmlRichiesta != null && xmlRichiesta.length > 0) {
                    filename = "vers_" + idSessioneVers + "_richiesta.xml";
                    zippaBlobbo(out, filename, xmlRichiesta);
                }
                // Caccio dentro lo zippone il file xml di indice
                byte[] xmlIndice = getForm().getVersamentiFallitiDetail().getBl_xml_index().getValue().getBytes();
                if (xmlIndice != null && xmlIndice.length > 0) {
                    filename = "vers_" + idSessioneVers + "_indice.xml";
                    zippaBlobbo(out, filename, xmlIndice);
                }
                // Caccio dentro lo zippone il file xml di risposta
                if (xmlRisposta != null && xmlRisposta.length > 0) {
                    filename = "vers_" + idSessioneVers + "_risposta.xml";
                    zippaBlobbo(out, filename, xmlRisposta);
                }
                out.flush();
                freeze();
            } catch (Exception e) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR, "Errore durante elaborazione file"));
                log.error("Eccezione", e);
                forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
            }
            // no error
            if (!getMessageBox().hasError()) {
                // prepare anche check final zip file
                try (FileInputStream in = new FileInputStream(tmpDwnFileXMLVersamento);
                        ZipFile zipFile = new ZipFile(tmpDwnFileXMLVersamento)) {
                    if (zipFile.size() == 0) { // zip file no entries
                        getMessageBox().addMessage(new Message(MessageLevel.WAR, "File non disponibile al download"));
                        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
                    } else {
                        String nomeZippone = "versamentoFallito-" + idSessioneVers;
                        getResponse().setContentType("application/zip");
                        getResponse().setHeader("Content-Disposition",
                                "attachment; filename=\"" + nomeZippone + ".zip");
                        getResponse().setContentLengthLong(Files.size(tmpDwnFileXMLVersamento.toPath()));
                        IOUtils.copyLarge(in, getServletOutputStream());
                    }
                }
            }
        } finally {
            Files.delete(tmpDwnFileXMLVersamento.toPath());
        }
    }

    // Gestione DETTAGLIO SESSIONI ERRATE TABS
    /**
     * Attiva il tab Info Sessione nel dettaglio di una sessione errata
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabInfoSessioneOnClick() throws EMFError {
        getForm().getSessioniErrateTabs().setCurrentTab(getForm().getSessioniErrateTabs().getInfoSessione());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
    }

    /**
     * Attiva il tab XML di richiesta nel dettaglio di una sessione errata
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabSessioneXMLRichOnClick() throws EMFError {
        // Se provengo dal tab con le info principali, me le salvo
        if (getForm().getSessioniErrateTabs().getInfoSessione().isCurrent()) {
            getForm().getSessioniErrateDetail().post(getRequest());
        }

        getForm().getSessioniErrateTabs().setCurrentTab(getForm().getSessioniErrateTabs().getSessioneXMLRich());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
    }

    @Override
    public void tabSessioneXMLIndexOnClick() throws EMFError {
        // Se provengo dal tab con le info principali, me le salvo
        if (getForm().getSessioniErrateTabs().getInfoSessione().isCurrent()) {
            getForm().getSessioniErrateDetail().post(getRequest());
        }

        getForm().getSessioniErrateTabs().setCurrentTab(getForm().getSessioniErrateTabs().getSessioneXMLIndex());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
    }

    /**
     * Attiva il tab XML di risposta nel dettaglio di una sessione errata
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabSessioneXMLRispOnClick() throws EMFError {
        // Se provengo dal tab con le info principali, me le salvo
        if (getForm().getSessioniErrateTabs().getInfoSessione().isCurrent()) {
            getForm().getSessioniErrateDetail().post(getRequest());
        }

        getForm().getSessioniErrateTabs().setCurrentTab(getForm().getSessioniErrateTabs().getSessioneXMLRisp());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_ERRATE_DETAIL);
    }

    /**
     * Attiva il tab Info Versamento nel dettaglio di un versamento fallito
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabInfoVersamentoOnClick() throws EMFError {
        getForm().getVersamentiFallitiTabs().setCurrentTab(getForm().getVersamentiFallitiTabs().getInfoVersamento());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
    }

    /**
     * Attiva il tab XML di richiesta nel dettaglio di un versamento fallito
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabVersamentoXMLRichOnClick() throws EMFError {
        // Se provengo dal tab con le info principali, me le salvo
        if (getForm().getVersamentiFallitiTabs().getInfoVersamento().isCurrent()) {
            getForm().getVersamentiFallitiDetail().post(getRequest());
        }

        getForm().getVersamentiFallitiTabs().setCurrentTab(getForm().getVersamentiFallitiTabs().getVersamentoXMLRich());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
    }

    @Override
    public void tabVersamentoXMLIndexOnClick() throws EMFError {
        // Se provengo dal tab con le info principali, me le salvo
        if (getForm().getVersamentiFallitiTabs().getInfoVersamento().isCurrent()) {
            getForm().getVersamentiFallitiDetail().post(getRequest());
        }

        getForm().getVersamentiFallitiTabs()
                .setCurrentTab(getForm().getVersamentiFallitiTabs().getVersamentoXMLIndex());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
    }

    /**
     * Attiva il tab XML di risposta nel dettaglio di un versamento fallito
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void tabVersamentoXMLRispOnClick() throws EMFError {
        // Se provengo dal tab con le info principali, me le salvo
        if (getForm().getVersamentiFallitiTabs().getInfoVersamento().isCurrent()) {
            getForm().getVersamentiFallitiDetail().post(getRequest());
        }

        getForm().getVersamentiFallitiTabs().setCurrentTab(getForm().getVersamentiFallitiTabs().getVersamentoXMLRisp());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_DETAIL);
    }

    @Override
    public void tabFiltriGeneraliOnClick() throws EMFError {
        getForm().getFiltriVersamentiUlteriori().post(getRequest());
        UnitaDocumentarieValidator validatoreUD = new UnitaDocumentarieValidator(getMessageBox());

        // Valida i campi di Range di chiavi unit\u00e0 documentaria
        String[] registro = Arrays.copyOf(
                getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().getDecodedValues().toArray(),
                getForm().getFiltriVersamentiUlteriori().getCd_registro_key_unita_doc().getDecodedValues()
                        .toArray().length,
                String[].class);
        Object[] chiavi = validatoreUD.validaChiaviUnitaDoc(registro,
                getForm().getFiltriVersamentiUlteriori().getAa_key_unita_doc().parse(),
                getForm().getFiltriVersamentiUlteriori().getCd_key_unita_doc().parse(),
                getForm().getFiltriVersamentiUlteriori().getAa_key_unita_doc_da().parse(),
                getForm().getFiltriVersamentiUlteriori().getAa_key_unita_doc_a().parse(),
                getForm().getFiltriVersamentiUlteriori().getCd_key_unita_doc_da().parse(),
                getForm().getFiltriVersamentiUlteriori().getCd_key_unita_doc_a().parse());

        // Valida i filtri per verificare quelli obbligatori
        if (!getMessageBox().hasError()) {
            // La validazione non ha riportato errori.
            // Setto i filtri di chiavi unit\u00e0 documentaria impostando gli eventuali valori di default
            if (chiavi != null && chiavi.length == 5) {
                getForm().getFiltriVersamentiUlteriori().getAa_key_unita_doc_da()
                        .setValue(chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                getForm().getFiltriVersamentiUlteriori().getAa_key_unita_doc_a()
                        .setValue(chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                getForm().getFiltriVersamentiUlteriori().getCd_key_unita_doc_da()
                        .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                getForm().getFiltriVersamentiUlteriori().getCd_key_unita_doc_a()
                        .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                // Se \u00e0¨ valorizzato qualche campo lascio aperta la section del Range di chiavi unit\u00e0
                // documentaria
                getForm().getFiltriVersamentiChiaveSection().setLoadOpened(true);
            }
            getForm().getFiltriRicercaVersamentiFallitiTabs()
                    .setCurrentTab(getForm().getFiltriRicercaVersamentiFallitiTabs().getFiltriGenerali());
            forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
        } else {
            forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
        }
    }

    @Override
    public void tabFiltriUlterioriOnClick() throws EMFError {
        getForm().getFiltriVersamenti().post(getRequest());
        getForm().getFiltriRicercaVersamentiFallitiTabs()
                .setCurrentTab(getForm().getFiltriRicercaVersamentiFallitiTabs().getFiltriUlteriori());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
    }

    @Override
    public void tabFiltriGeneraliDerivantiOnClick() throws EMFError {
        getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().post(getRequest());
        UnitaDocumentarieValidator validatoreUD = new UnitaDocumentarieValidator(getMessageBox());

        if (getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().validate(getMessageBox())) {
            // Valida i campi di Range di chiavi unit\u00e0 documentaria
            String[] registro = Arrays.copyOf(
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_registro_key_unita_doc_ult()
                            .getDecodedValues().toArray(),
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_registro_key_unita_doc_ult()
                            .getDecodedValues().toArray().length,
                    String[].class);
            Object[] chiavi = validatoreUD.validaChiaviUnitaDoc(registro,
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getAa_key_unita_doc_ult().parse(),
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_key_unita_doc_ult().parse(),
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getAa_key_unita_doc_da_ult().parse(),
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getAa_key_unita_doc_a_ult().parse(),
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_key_unita_doc_da_ult().parse(),
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_key_unita_doc_a_ult().parse());

            // Valida i filtri per verificare quelli obbligatori
            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori.
                // Setto i filtri di chiavi unit\u00e0 documentaria impostando gli eventuali valori di default
                if (chiavi != null && chiavi.length == 5) {
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getAa_key_unita_doc_da_ult()
                            .setValue(chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getAa_key_unita_doc_a_ult()
                            .setValue(chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_key_unita_doc_da_ult()
                            .setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                    getForm().getFiltriUdDocDerivantiDaVersFallitiUlteriori().getCd_key_unita_doc_a_ult()
                            .setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                    // Se \u00e0¨ valorizzato qualche campo lascio aperta la section del Range di chiavi unit\u00e0
                    // documentaria
                    getForm().getFiltriVersamentiChiaveSection().setLoadOpened(true);
                }
                getForm().getFiltriListaDocumentiDerivantiVersFallitiTabs().setCurrentTab(
                        getForm().getFiltriListaDocumentiDerivantiVersFallitiTabs().getFiltriGeneraliDerivanti());
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_RICERCA);
    }

    @Override
    public void tabFiltriUlterioriDerivantiOnClick() throws EMFError {
        getForm().getFiltriUdDocDerivantiDaVersFalliti().post(getRequest());
        getForm().getFiltriListaDocumentiDerivantiVersFallitiTabs().setCurrentTab(
                getForm().getFiltriListaDocumentiDerivantiVersFallitiTabs().getFiltriUlterioriDerivanti());
        forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_RICERCA);
    }

    /**
     * Metodo richiamato alla pressione del tasto "Indietro" delle barre di navigazione delle pagine jsp ed utilizzato
     * per rieffettuare le ricerche
     *
     * @param publisherName
     *            nome publisher
     */
    @Override
    public void reloadAfterGoBack(String publisherName) {
        /*
         * Quando torno indietro da un'altra action (volumi) a seconda di dove provengo, rieseguo la ricerca visto che
         * potrei aver modificato un volume
         */
        User u = (User) getRequest().getSession().getAttribute("###_USER_CONTAINER");
        int lastIndex = u.getMenu().getSelectedPath("").size() - 1;
        String lastMenuEntry = (u.getMenu().getSelectedPath("").get(lastIndex)).getCodice();
        try {
            if (lastMenuEntry.contains("OperazioniVolumi") || lastMenuEntry.contains("RiepilogoVersamenti")) {

                if (getForm().getFiltriOperazioniVolumi().getId_ambiente().getValue() != null) {
                    eseguiRicercaOperazioniVolumi(false);
                }

                if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ambiente().getValue() != null
                        && getLastPublisher() != null
                        && !getLastPublisher().equals("/monitoraggioDocumentiDerivantiDaVersFallitiDetail")) {
                    BigDecimal idAmbiente = getForm().getRiepilogoVersamenti().getId_ambiente().parse();
                    BigDecimal idEnte = getForm().getRiepilogoVersamenti().getId_ente().parse();
                    BigDecimal idStruttura = getForm().getRiepilogoVersamenti().getId_strut().parse();
                    BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().parse();
                    calcolaTotaliRiepilogoVersamenti(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);
                }

                if (getForm().getVersamentiFallitiDetail().getId_sessione_vers().getValue() != null) {
                    BigDecimal idAmbiente = getForm().getRiepilogoVersamenti().getId_ambiente().parse();
                    BigDecimal idEnte = getForm().getRiepilogoVersamenti().getId_ente().parse();
                    BigDecimal idStruttura = getForm().getRiepilogoVersamenti().getId_strut().parse();
                    BigDecimal idTipoUnitaDoc = getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().parse();
                    calcolaTotaliRiepilogoVersamenti(idAmbiente, idEnte, idStruttura, idTipoUnitaDoc);

                    if (getForm().getVersamentiFallitiList().getTable() != null
                            && getLastPublisher().equals("/monitoraggioVersFallitiDetail")) {
                        // Rieseguo la ricerca della pagina precedente che avr\u00e0 anch'essa subito modifiche
                        int paginaCorrenteDocNonVers = getForm().getVersamentiFallitiList().getTable()
                                .getCurrentPageIndex();
                        int inizioDocNonVers = getForm().getVersamentiFallitiList().getTable().getFirstRowPageIndex();
                        int pageSize = getForm().getVersamentiFallitiList().getTable().getPageSize();
                        String maxResultStandard = configurationHelper
                                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                        MonVLisVersErrIamTableBean versErrTableBean = monitoraggioHelper.getMonVLisVersErrIamViewBean(
                                (MonitoraggioFiltriListaVersFallitiBean) getSession().getAttribute("filtriSes"),
                                Integer.parseInt(maxResultStandard));
                        getForm().getVersamentiFallitiList().setTable(versErrTableBean);
                        getForm().getVersamentiFallitiList().setUserOperations(true, true, true, false);
                        // Workaround in modo che la lista punti al primo record, non all'ultimo
                        getForm().getVersamentiFallitiList().getTable().first();
                        getForm().getVersamentiFallitiList().getTable().setPageSize(pageSize);
                        getSession().removeAttribute("versamentiFallitiListPageSize");
                        // Rieseguo la query se necessario
                        this.lazyLoadGoPage(getForm().getVersamentiFallitiList(), paginaCorrenteDocNonVers);
                        // Ritorno alla pagina
                        getForm().getVersamentiFallitiList().getTable().setCurrentRowIndex(inizioDocNonVers);

                        /* Gestione visibilità bottoni */
                        mostraNascondiBottoniOperazioniVersamentiFalliti(versErrTableBean.size());
                    }
                }

                if (getForm().getDocumentiDerivantiDaVersFallitiDetail().getId_strut().getValue() != null) {
                    // Rieseguo la query in modo tale da portare a video le eventuali modifiche
                    BigDecimal idStrut = getForm().getDocumentiDerivantiDaVersFallitiDetail().getId_strut().parse();
                    String registro = getForm().getDocumentiDerivantiDaVersFallitiDetail()
                            .getCd_registro_key_unita_doc().parse();
                    BigDecimal anno = getForm().getDocumentiDerivantiDaVersFallitiDetail().getAa_key_unita_doc()
                            .parse();
                    String numero = getForm().getDocumentiDerivantiDaVersFallitiDetail().getCd_key_unita_doc().parse();
                    String docVers = getForm().getDocumentiDerivantiDaVersFallitiDetail().getCd_key_doc_vers().parse();

                    /*
                     * Ricarico anche la parte di dettaglio in quanto anche su di questa le modifiche possono essere
                     * andate ad impattare
                     */
                    if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse() != null) {
                        AbstractBaseTable<?> sessioniListTB;
                        if (getForm().getFiltriUdDocDerivantiDaVersFalliti().getTipo_lista().parse()
                                .equals("UNITA_DOC")) {
                            MonVVisUdNonVersRowBean nonVersRB = monitoraggioHelper.getMonVVisUdNonVersRowBean(idStrut,
                                    registro, anno, numero);
                            // Copio nella form di dettaglio i dati del rowbean
                            getForm().getDocumentiDerivantiDaVersFallitiDetail().copyFromBean(nonVersRB);
                            sessioniListTB = monitoraggioHelper.getMonVLisVersUdNonVersViewBean(idStrut, registro, anno,
                                    numero);
                        } else {
                            MonVVisDocNonVersRowBean nonVersRB = monitoraggioHelper.getMonVVisDocNonVersRowBean(idStrut,
                                    registro, anno, numero, docVers);
                            // Copio nella form di dettaglio i dati del rowbean
                            getForm().getDocumentiDerivantiDaVersFallitiDetail().copyFromBean(nonVersRB);
                            sessioniListTB = monitoraggioHelper.getMonVLisVersDocNonVersViewBean(idStrut, registro,
                                    anno, numero, docVers);
                        }

                        int pageSize = getForm().getSessioniList().getTable().getPageSize();
                        int paginaCorrente = getForm().getSessioniList().getTable().getCurrentPageIndex();
                        int inizio = getForm().getSessioniList().getTable().getFirstRowPageIndex();
                        getForm().getSessioniList().setTable(sessioniListTB);
                        getForm().getSessioniList().getTable().setPageSize(pageSize);
                        getSession().removeAttribute("sessioniListPageSize");
                        getForm().getSessioniList().getTable().first();
                        // rieseguo la query se necessario
                        this.lazyLoadGoPage(getForm().getSessioniList(), paginaCorrente);
                        // ritorno alla pagina
                        getForm().getSessioniList().getTable().setCurrentRowIndex(inizio);
                    }
                }

                if (getForm().getDocumentiDerivantiDaVersFallitiList().getTable() != null
                        && getLastPublisher().equals("/monitoraggioDocumentiDerivantiDaVersFallitiDetail")) {
                    // Rieseguo la ricerca della pagina precedente che avr\u00e0 anch'essa subito modifiche
                    int paginaCorrenteDocNonVers = getForm().getDocumentiDerivantiDaVersFallitiList().getTable()
                            .getCurrentPageIndex();
                    int inizioDocNonVers = getForm().getDocumentiDerivantiDaVersFallitiList().getTable()
                            .getFirstRowPageIndex();
                    int pageSize = getForm().getDocumentiDerivantiDaVersFallitiList().getTable().getPageSize();
                    MonitoraggioFiltriListaVersFallitiDistintiDocBean filtriListaDocNonVers = (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                            .getAttribute("filtriListaVersFallitiDistintiDoc");
                    if (filtriListaDocNonVers.getTipoLista().equals("UNITA_DOC")) {
                        // Setto la lista delle unit\u00e0 documentarie non versate
                        MonVLisUdNonVersIamTableBean monVLisVersErrTableBean = monitoraggioHelper
                                .getMonVLisUdNonVersIamViewBeanScaricaContenuto(
                                        (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                                .getAttribute("filtriListaVersFallitiDistintiDoc"),
                                        null);
                        getForm().getDocumentiDerivantiDaVersFallitiList().setTable(monVLisVersErrTableBean);
                    } else {
                        // Setto la lista delle unit\u00e0 documentarie non versate
                        MonVLisDocNonVersIamTableBean monVLisDocNonVersTableBean = monitoraggioHelper
                                .getMonVLisDocNonVersIamViewBeanScaricaContenuto(
                                        (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                                .getAttribute("filtriListaVersFallitiDistintiDoc"),
                                        null);
                        getForm().getDocumentiDerivantiDaVersFallitiList().setTable(monVLisDocNonVersTableBean);
                    }
                    getForm().getDocumentiDerivantiDaVersFallitiList().getTable().setPageSize(pageSize);
                    getSession().removeAttribute("documentiDerivantiDaVersFallitiListPageSize");
                    getForm().getDocumentiDerivantiDaVersFallitiList().setUserOperations(true, false, false, false);
                    // Workaround in modo che la lista punti al primo record, non all'ultimo
                    getForm().getDocumentiDerivantiDaVersFallitiList().getTable().first();
                    // Rieseguo la query se necessario
                    this.lazyLoadGoPage(getForm().getDocumentiDerivantiDaVersFallitiList(), paginaCorrenteDocNonVers);
                    // Ritorno alla pagina
                    getForm().getDocumentiDerivantiDaVersFallitiList().getTable().setCurrentRowIndex(inizioDocNonVers);
                }

            } else if (lastMenuEntry.contains("RiepilogoStruttura")) {
                int paginaCorrenteDocNonVers = getForm().getRiepilogoStrutturaList().getTable().getCurrentPageIndex();
                int inizioDocNonVers = getForm().getRiepilogoStrutturaList().getTable().getFirstRowPageIndex();
                // cancello l'id struttura non necessario
                getSession().removeAttribute("idStrutRif");
                // Ricarico i dati nella pagina Riepilogo Struttura
                int pageSize = getForm().getRiepilogoStrutturaList().getTable().getPageSize();
                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                MonVRiepStrutIamTableBean listaMon = monitoraggioHelper.getMonVRiepStrutIamViewBean(
                        getUser().getIdUtente(), Integer.parseInt(maxResultStandard),
                        (Integer) getSession().getAttribute("filtriRiepilogoStruttura"));
                getForm().getRiepilogoStrutturaList().setTable(listaMon);
                getForm().getRiepilogoStrutturaList().getTable().setPageSize(pageSize);
                getSession().removeAttribute("riepilogoStrutturaListPageSize");

                getForm().getRiepilogoStrutturaList().getTable().first();
                // Rieseguo la query se necessario
                this.lazyLoadGoPage(getForm().getRiepilogoStrutturaList(), paginaCorrenteDocNonVers);
                // Ritorno alla pagina
                getForm().getRiepilogoStrutturaList().getTable().setCurrentRowIndex(inizioDocNonVers);
            } else if (lastMenuEntry.contains("GestioneVolumi")) {
                ricercaOperazioniVolumiDaDettaglioVolume();
            } else if (lastMenuEntry.contains("SessioniRecupero")) {
                int paginaCorrente = getForm().getMonitSessioniRecupList().getTable().getCurrentPageIndex();
                int inizio = getForm().getMonitSessioniRecupList().getTable().getFirstRowPageIndex();
                int pageSize = getForm().getMonitSessioniRecupList().getTable().getPageSize();
                UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());

                Date[] dateApertureValidate = validator.validaDate(
                        getForm().getFiltriRicercaSessioniRecupero().getDt_oper_da().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getOre_dt_oper_da().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getMinuti_dt_oper_da().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getDt_oper_a().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getOre_dt_oper_a().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getMinuti_dt_oper_a().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getDt_oper_da().getHtmlDescription(),
                        getForm().getFiltriRicercaSessioniRecupero().getDt_oper_a().getHtmlDescription());
                // Validazione chiavi unita doc
                String[] registro = Arrays.copyOf(
                        getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc().getDecodedValues()
                                .toArray(),
                        getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc().getDecodedValues()
                                .toArray().length,
                        String[].class);
                Object[] chiavi = validator.validaChiaviUnitaDoc(registro,
                        getForm().getFiltriRicercaSessioniRecupero().getAa_key_unita_doc().parse(),
                        getForm().getFiltriRicercaSessioniRecupero().getCd_key_unita_doc().parse(), null, null, null,
                        null);

                BigDecimal idAmbiente = getForm().getFiltriRicercaSessioniRecupero().getId_ambiente().parse();
                BigDecimal idEnte = getForm().getFiltriRicercaSessioniRecupero().getId_ente().parse();
                BigDecimal idStrut = getForm().getFiltriRicercaSessioniRecupero().getId_strut().parse();
                String nmUserid = getForm().getFiltriRicercaSessioniRecupero().getNm_userid().parse();

                if (!getMessageBox().hasError()) {
                    // La validazione non ha riportato errori. Carico la tabella con i filtri impostati
                    getForm().getMonitSessioniRecupList()
                            .setTable(monitoraggioHelper.getSessioniRecupero(idAmbiente, idEnte, idStrut, nmUserid,
                                    dateApertureValidate, chiavi,
                                    getForm().getFiltriRicercaSessioniRecupero().getTi_stato().parse(),
                                    getForm().getFiltriRicercaSessioniRecupero().getTi_sessione().parse()));
                    // Workaround in modo che la lista punti al primo record, non all'ultimo
                    getForm().getMonitSessioniRecupList().getTable().first();
                    getForm().getMonitSessioniRecupList().getTable().setPageSize(pageSize);
                    this.lazyLoadGoPage(getForm().getMonitSessioniRecupList(), paginaCorrente);
                    // Ritorno alla pagina
                    getForm().getMonitSessioniRecupList().getTable().setCurrentRowIndex(inizio);
                }
            } else if (lastMenuEntry.contains("RicercaElenchiVersamento")) {
                ricercaOperazioniElenchiDaDettaglioElenco();
            } else if (lastMenuEntry.contains("EsameOperazioniElenchiVersamento")) {
                if (getForm().getFiltriOperazioniElenchiVersamento().getId_ambiente().getValue() != null) {
                    eseguiRicercaOperazioniElenchiVersamento(false);
                }
            } else if (lastMenuEntry.contains("EsameJobSchedulati")) {
                if (getForm().getFiltriJobSchedulati().getNm_job().parse() != null) {
                    ricercaJobSchedulati();
                }
            }
        } catch (EMFError ex) {
            log.error("Eccezione", ex);
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ambiente quando questo \u00e0¨ l'unico presente e settare
     * di conseguenza la combo ente
     *
     * @param idAmbiente
     *            id ambiente
     * @param sezione
     *            enumerativo
     *
     * @throws EMFError
     *             errore generico
     */
    public void checkUniqueAmbienteInCombo(BigDecimal idAmbiente, Enum<?> sezione) throws EMFError {
        if (idAmbiente != null) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");

            if (sezione.equals(ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
                getForm().getRiepilogoVersamenti().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.CONTENUTO_SACER)) {
                getForm().getFiltriContenutoSacer().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI)) {
                getForm().getFiltriOperazioniVolumi().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO)) {
                getForm().getFiltriOperazioniElenchiVersamento().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI)) {
                getForm().getFiltriJobSchedulati().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE)) {
                getForm().getSessioniErrateDetail().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI)) {
                getForm().getFiltriDocumenti().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI)) {
                getForm().getFiltriVersamenti().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS)) {
                getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ente().setDecodeMap(mappaEnte);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.REPLICA_ORG)) {
                getForm().getFiltriReplicaOrg().getId_ente().setDecodeMap(mappaEnte);
            }

            // Se la combo ente ha un solo valore presente, lo imposto e faccio controllo su di essa
            if (tmpTableBeanEnte.size() == 1) {
                if (sezione.equals(ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
                    getForm().getRiepilogoVersamenti().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.CONTENUTO_SACER)) {
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.CONTENUTO_SACER);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI)) {
                    getForm().getFiltriOperazioniVolumi().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO)) {
                    getForm().getFiltriOperazioniElenchiVersamento().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI)) {
                    getForm().getFiltriJobSchedulati().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE)) {
                    getForm().getSessioniErrateDetail().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI)) {
                    getForm().getFiltriDocumenti().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI)) {
                    getForm().getFiltriVersamenti().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS)) {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.REPLICA_ORG)) {
                    getForm().getFiltriReplicaOrg().getId_ente()
                            .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                    checkUniqueEnteInCombo(tmpTableBeanEnte.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.REPLICA_ORG);
                }
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
                getForm().getRiepilogoVersamenti().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.CONTENUTO_SACER)) {
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI)) {
                getForm().getFiltriOperazioniVolumi().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO)) {
                getForm().getFiltriOperazioniElenchiVersamento().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI)) {
                getForm().getFiltriJobSchedulati().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE)) {
                getForm().getSessioniErrateDetail().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI)) {
                getForm().getFiltriDocumenti().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI)) {
                getForm().getFiltriVersamenti().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS)) {
                getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().setDecodeMap(new DecodeMap());
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.REPLICA_ORG)) {
                getForm().getFiltriReplicaOrg().getId_strut().setDecodeMap(new DecodeMap());
            }
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo ente quando questo \u00e0¨ l'unico presente e settare di
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
    public void checkUniqueEnteInCombo(BigDecimal idEnte, Enum<?> sezione) throws EMFError {
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(tmpTableBeanStrut, "id_strut", "nm_strut");

            if (sezione.equals(ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
                getForm().getRiepilogoVersamenti().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE)) {
                getForm().getSessioniErrateDetail().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.CONTENUTO_SACER)) {
                getForm().getFiltriContenutoSacer().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI)) {
                getForm().getFiltriOperazioniVolumi().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO)) {
                getForm().getFiltriOperazioniElenchiVersamento().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI)) {
                getForm().getFiltriJobSchedulati().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI)) {
                getForm().getFiltriDocumenti().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI)) {
                getForm().getFiltriVersamenti().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS)) {
                getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().setDecodeMap(mappaStrut);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_ANNUL)) {
                getForm().getFiltriDocumentiAnnullati().getId_strut().setDecodeMap(mappaStrut);
            }

            // Se la combo struttura ha un solo valore presente, lo imposto e faccio controllo su di essa
            if (tmpTableBeanStrut.size() == 1) {
                if (sezione.equals(ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
                    getForm().getRiepilogoVersamenti().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    checkUniqueStrutInCombo(tmpTableBeanStrut.getRow(0).getIdEnte(),
                            ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI);
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.SESSIONI_ERRATE)) {
                    getForm().getSessioniErrateDetail().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.CONTENUTO_SACER)) {
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_VOLUMI)) {
                    getForm().getFiltriOperazioniVolumi().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO)) {
                    getForm().getFiltriOperazioniElenchiVersamento().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI)) {
                    getForm().getFiltriJobSchedulati().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI)) {
                    getForm().getFiltriDocumenti().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_VERSAMENTI)) {
                    getForm().getFiltriVersamenti().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_NON_VERS)) {
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_ANNUL)) {
                    getForm().getFiltriDocumentiAnnullati().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                }
            }
        }
    }

    /**
     * Metodo utilizzato per controllare il valore nella combo struttura quando questo \u00e0¨ l'unico presente e
     * settare di conseguenza la combo tipo unit\u00e0 doc
     *
     * @param idStrut
     *            id struttura
     * @param sezione
     *            enumerativo
     *
     * @throws EMFError
     *             errore generico
     */
    public void checkUniqueStrutInCombo(BigDecimal idStrut, Enum<?> sezione) throws EMFError {
        if (idStrut != null) {
            // Ricavo il TableBean relativo ai tipi di unit\u00e0 doc dalla struttura scelta
            DecTipoUnitaDocTableBean tmpTableBeanTUD = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                    idStrut);
            DecodeMap mappaUD = new DecodeMap();
            mappaUD.populatedMap(tmpTableBeanTUD, "id_tipo_unita_doc", "nm_tipo_unita_doc");

            DecodeMap mappaReg = getMappaRegistro(idStrut);

            // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
            DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(idStrut, false, false);

            DecodeMap mappaTipoDoc = DecodeMap.Factory.newInstance(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc");

            if (sezione.equals(ActionEnums.SezioneMonitoraggio.RIEPILOGO_VERSAMENTI)) {
                getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().setDecodeMap(mappaUD);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI)) {
                getForm().getFiltriDocumenti().getId_tipo_unita_doc().setDecodeMap(mappaUD);
                getForm().getFiltriDocumenti().getId_tipo_doc().setDecodeMap(mappaTipoDoc);
                getForm().getFiltriDocumenti().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
            } else if (sezione.equals(ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_ANNUL)) {
                getForm().getFiltriDocumentiAnnullati().getId_tipo_unita_doc().setDecodeMap(mappaUD);
                getForm().getFiltriDocumentiAnnullati().getId_tipo_doc().setDecodeMap(mappaTipoDoc);
                getForm().getFiltriDocumentiAnnullati().getCd_registro_key_unita_doc().setDecodeMap(mappaReg);
            }
        }
    }

    /**
     * Metodo di utility che viene eseguito dopo ogni click sull'ordinamento delle colonne o la navigazione per pagine.
     */
    @Override
    protected void postLoad() {
        super.postLoad();
        Object ogg = getForm();

        if (ogg instanceof MonitoraggioForm) {
            MonitoraggioForm form = getForm();
            form.getFiltriConsistenzaSacer().getScaricaReport().setEditMode();
            form.getFiltriConsistenzaSacer().getScaricaReport().setDisableHourGlass(true);

            if (form.getConsistenzaSacerList().getTable() != null
                    && form.getConsistenzaSacerList().getTable().size() > 0) {
                form.getFiltriConsistenzaSacer().getScaricaReport().setHidden(false);
            } else {
                form.getFiltriConsistenzaSacer().getScaricaReport().setHidden(true);
            }

            try {
                if (form.getFiltriConsistenzaSacer().getId_ambiente().parse() != null
                        && form.getFiltriConsistenzaSacer().getId_ambiente().parse().isEmpty()
                        && form.getConsistenzaSacerList().getTable() != null
                        && form.getConsistenzaSacerList().getTable().size() > 0) {
                    form.getFiltriConsistenzaSacer().getScaricaReportSintetico().setHidden(false);
                    form.getFiltriConsistenzaSacer().getScaricaReportSintetico().setDisableHourGlass(true);
                } else {
                    form.getFiltriConsistenzaSacer().getScaricaReportSintetico().setHidden(true);
                }
            } catch (EMFError ex) {
                getMessageBox().addError("Errore inatteso nel caricamento della pagina di esame consistenza Sacer");
            }

        }

        if (getRequest().getParameter("table") != null) {
            /*
             * Ricarico l'attributo "output" con i valori che mi servono a seconda che debba rivisualizzare un tipo di
             * lista oppure l'altro (gestito tramite hidden nella jsp)
             */
            if (getRequest().getParameter("table").equals("OutputAnaliticoCronologicoList")
                    || getRequest().getParameter("table").equals("OutputAnaliticoCronologicoListElenchi")) {
                getRequest().setAttribute("output", "ANALITICO");
            } else if (getRequest().getParameter("table").equals("OutputAggregatoList")
                    || getRequest().getParameter("table").equals("OutputAggregatoListElenchi")) {
                getRequest().setAttribute("output", "AGGREGATO");
            } else if (getRequest().getParameter("table").equals("UnitaDocumentariaOutputList")
                    || getRequest().getParameter("table").equals("VolumeOutputList")
                    || getRequest().getParameter("table").equals("ElencoVersamentoOutputList")) {
                getRequest().setAttribute("output", "ANALITICO");
            }
        }
    }

    /**
     * Restituisce un record Versamento Fallito sotto forma di row bean preso dalla lista dei versamenti falliti
     *
     * @return entity bean MonVLisVersErrRowBean
     */
    private MonVLisVersErrIamRowBean getMonVLisVersErrRowBean() {
        return (MonVLisVersErrIamRowBean) getForm().getVersamentiFallitiList().getTable().getCurrentRow();

    }

    /**
     * Restituisce un record Sessione Errata sotto forma di row bean
     *
     * @return entity bean VrsSessioneVersRowBean
     */
    private VrsSessioneVersKoRowBean getVrsSessioneVersRowBean() {
        return (VrsSessioneVersKoRowBean) getForm().getSessioniErrateList().getTable().getCurrentRow();
    }

    private void visualizzaTotaliRiepilogoVersamenti() {
        // Azzeramento voci ud
        getForm().getRiepilogoVersamenti().getNi_ud_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_mem_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_in_attesa_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_non_sel_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_aperto_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_chiuso_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_chiudere_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_da_verificare_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_firma_no_marca_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_ud_vol_in_err_tot().setHidden(false);

        // Azzeramento voci documenti
        getForm().getRiepilogoVersamenti().getNi_doc_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_mem_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_in_attesa_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_non_sel_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_aperto_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_chiuso_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_chiudere_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_da_verificare_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_firma_no_marca_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getNi_doc_vol_in_err_tot().setHidden(false);

        // Azzeramento voci sessioni di aggiunta
        getForm().getRiepilogoVersamenti().getSes_agg_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_agg_ris_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_ris_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_ris_ver_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_agg_ris_no_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_ris_no_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_ris_no_ver_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_agg_non_risolub_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_non_risolub_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_non_risolub_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_agg_no_ris_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_no_ris_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_no_ris_ver_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_agg_no_ris_no_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_no_ris_no_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_agg_no_ris_no_ver_tot().setHidden(false);

        // Azzeramento voci sessioni di versamento
        getForm().getRiepilogoVersamenti().getSes_vrs_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_vrs_ris_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_ris_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_ris_ver_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_vrs_ris_no_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_ris_no_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_ris_no_ver_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_vrs_non_risolub_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_non_risolub_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_non_risolub_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_ver_tot().setHidden(false);

        getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_no_ver_corr().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_no_ver_7().setHidden(false);
        getForm().getRiepilogoVersamenti().getSes_vrs_no_ris_no_ver_tot().setHidden(false);

        // Nascondo i valori di Riepilogo versamenti falliti distinti per documenti
        getForm().getRiepilogoVersamenti().getVers_ud_falliti().setHidden(false);
        getForm().getRiepilogoVersamenti().getVers_ud_falliti_non_risolub().setHidden(false);
        getForm().getRiepilogoVersamenti().getVers_ud_falliti_verif().setHidden(false);
        getForm().getRiepilogoVersamenti().getVers_ud_falliti_non_verif().setHidden(false);

        getForm().getRiepilogoVersamenti().getVers_doc_falliti().setHidden(false);
        getForm().getRiepilogoVersamenti().getVers_doc_falliti_non_risolub().setHidden(false);
        getForm().getRiepilogoVersamenti().getVers_doc_falliti_verif().setHidden(false);
        getForm().getRiepilogoVersamenti().getVers_doc_falliti_non_verif().setHidden(false);
    }

    public void azzeraSetValueRiepilogoVersamenti() {
        // Azzero i valori preimpostati delle varie combo
        getForm().getRiepilogoVersamenti().getId_ambiente().setValue("");
        getForm().getRiepilogoVersamenti().getId_ente().setValue("");
        getForm().getRiepilogoVersamenti().getId_strut().setValue("");
        getForm().getRiepilogoVersamenti().getId_tipo_unita_doc().setValue("");
        getForm().getFiltriDocumenti().getId_ambiente().setValue("");
        getForm().getFiltriDocumenti().getId_ente().setValue("");
        getForm().getFiltriDocumenti().getId_strut().setValue("");
        getForm().getFiltriDocumenti().getId_tipo_unita_doc().setValue("");
    }

    /**
     * Metodo utilizzato per popolare le combo "Registro" nei filtri di ricerca che utilizzano la Chiave Unit\u00e0
     * Documentaria
     *
     * @param idStrut
     *            id struttura
     *
     * @return entity {@link DecodeMap}
     *
     * @throws EMFError
     *             errore generico
     */
    public DecodeMap getMappaRegistro(BigDecimal idStrut) throws EMFError {
        // Setto i valori della combo TIPO REGISTRO ricavati dalla tabella DEC_REGISTRO_UNITA_DOC
        DecRegistroUnitaDocTableBean tmpTableBeanReg = registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                idStrut);
        DecodeMap mappaRegistro = new DecodeMap();
        mappaRegistro.populatedMap(tmpTableBeanReg, "cd_registro_unita_doc", "cd_registro_unita_doc");
        return mappaRegistro;
    }

    @Override
    public void cercaSessioniErrate() throws EMFError {
        filtraSessioniVerificate();
    }

    private DecodeMap getMappaRegistroFromTotaleMonVLisVersErr(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, Long idUserIam) {
        BaseTable registri = monitoraggioHelper.getRegistriFromTotaleMonVLisVersErr(idAmbiente, idEnte, idStrut,
                idUserIam);
        DecodeMap mappaRegistro = new DecodeMap();
        mappaRegistro.populatedMap(registri, "registro", "registro");
        return mappaRegistro;
    }

    private DecodeMap getMappaRegistroFromTotaleMonVLisUdNonVers(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, Long idUserIam) {
        BaseTable registri = monitoraggioHelper.getRegistriFromTotaleMonVLisUdNonVers(idAmbiente, idEnte, idStrut,
                idUserIam);
        DecodeMap mappaRegistro = new DecodeMap();
        mappaRegistro.populatedMap(registri, "registro", "registro");
        return mappaRegistro;
    }

    @Override
    public JSONObject triggerFiltriVersamentiClasse_erroreOnTrigger() throws EMFError {
        getForm().getFiltriVersamenti().post(getRequest());
        String classe = getForm().getFiltriVersamenti().getClasse_errore().parse();
        DecodeMap mappaSottoClasse = caricaErrori.filtraSottoclasse(classe);
        getForm().getFiltriVersamenti().getSottoclasse_errore().setDecodeMap(mappaSottoClasse);
        getForm().getFiltriVersamenti().getCodice_errore().setDecodeMap(new DecodeMap());
        return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriVersamentiSottoclasse_erroreOnTrigger() throws EMFError {
        getForm().getFiltriVersamenti().post(getRequest());
        String sottoClasse = getForm().getFiltriVersamenti().getSottoclasse_errore().parse();
        DecodeMap mappaCodice = caricaErrori.filtraCodice(sottoClasse);
        getForm().getFiltriVersamenti().getCodice_errore().setDecodeMap(mappaCodice);
        return getForm().getFiltriVersamenti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriVersamentiVersamento_ses_err_verifOnTrigger() throws EMFError {
        getForm().getFiltriVersamenti().post(getRequest());
        if (getForm().getFiltriVersamenti().getVersamento_ses_err_verif() != null) {
            if (getForm().getFiltriVersamenti().getVersamento_ses_err_verif().getValue().equals("0")) {
                getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub().setDecodeMap(new DecodeMap());
            } else {
                getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub()
                        .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
            }
        } else {
            getForm().getFiltriVersamenti().getVersamento_ses_err_non_risolub()
                    .setDecodeMap(ComboGetter.getMappaGenericFlagSiNo());
        }
        return getForm().getFiltriVersamenti().asJSON();
    }

    @Secure(action = "Menu.Monitoraggio.SessioniRecupero")
    public void loadSessioniRecupero() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.SessioniRecupero");

        getForm().getFiltriRicercaSessioniRecupero().reset();
        getForm().getFiltriRicercaSessioniRecupero().getTi_stato().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("statoRecup", JobConstants.StatoSessioniRecupEnum.values()));
        getForm().getFiltriRicercaSessioniRecupero().getTi_sessione().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("tipoRecup", JobConstants.TipoSessioniRecupEnum.values()));
        try {
            // Ricavo id struttura, ente ed ambiente attuali
            BigDecimal idStruttura = getUser().getIdOrganizzazioneFoglia();
            BigDecimal idEnte = monitSintEjb.getEnte(idStruttura);
            BigDecimal idAmbiente = monitSintEjb.getAmbiente(idEnte);

            // Inizializzo le combo settando la struttura corrente
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            OrgAmbienteTableBean tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());

            // Ricavo i valori della combo ENTE
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);

            // Ricavo i valori della combo STRUTTURA
            OrgStrutTableBean tmpTableBeanStruttura = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);

            getForm().getFiltriRicercaSessioniRecupero().getId_ambiente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente"));
            getForm().getFiltriRicercaSessioniRecupero().getId_ente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));
            getForm().getFiltriRicercaSessioniRecupero().getId_strut()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStruttura, "id_strut", "nm_strut"));

            getForm().getFiltriRicercaSessioniRecupero().getId_ambiente().setValue(idAmbiente.toString());
            getForm().getFiltriRicercaSessioniRecupero().getId_ente().setValue(idEnte.toString());
            getForm().getFiltriRicercaSessioniRecupero().getId_strut().setValue(idStruttura.toString());

            getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc()
                    .setDecodeMap(DecodeMap.Factory.newInstance(
                            registroEjb.getRegistriUnitaDocAbilitati(getUser().getIdUtente(),
                                    getUser().getIdOrganizzazioneFoglia()),
                            "id_registro_unita_doc", "cd_registro_unita_doc"));
            getForm().getFiltriRicercaSessioniRecupero().setEditMode();
        } catch (ParerUserError ex) {
            getMessageBox().addError("Errore inatteso nel caricamento della pagina");
        }

        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_RECUP_RICERCA);
    }

    @Override
    public void ricercaSessioniRecup() throws EMFError {
        MonitoraggioForm.FiltriRicercaSessioniRecupero filtri = getForm().getFiltriRicercaSessioniRecupero();
        if (filtri.postAndValidate(getRequest(), getMessageBox())) {
            UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());
            // Validazione date
            Date[] dateApertureValidate = validator.validaDate(filtri.getDt_oper_da().parse(),
                    filtri.getOre_dt_oper_da().parse(), filtri.getMinuti_dt_oper_da().parse(),
                    filtri.getDt_oper_a().parse(), filtri.getOre_dt_oper_a().parse(),
                    filtri.getMinuti_dt_oper_a().parse(), filtri.getDt_oper_da().getHtmlDescription(),
                    filtri.getDt_oper_a().getHtmlDescription());
            // Validazione chiavi unita doc
            String[] registro = Arrays.copyOf(filtri.getCd_registro_key_unita_doc().getDecodedValues().toArray(),
                    filtri.getCd_registro_key_unita_doc().getDecodedValues().toArray().length, String[].class);
            Object[] chiavi = validator.validaChiaviUnitaDoc(registro, filtri.getAa_key_unita_doc().parse(),
                    filtri.getCd_key_unita_doc().parse(), null, null, null, null);

            BigDecimal idAmbiente = filtri.getId_ambiente().parse();
            BigDecimal idEnte = filtri.getId_ente().parse();
            BigDecimal idStrut = filtri.getId_strut().parse();
            String nmUserid = filtri.getNm_userid().parse();

            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori. Carico la tabella con i filtri impostati
                getForm().getMonitSessioniRecupList()
                        .setTable(monitoraggioHelper.getSessioniRecupero(idAmbiente, idEnte, idStrut, nmUserid,
                                dateApertureValidate, chiavi, filtri.getTi_stato().parse(),
                                filtri.getTi_sessione().parse()));
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getMonitSessioniRecupList().getTable().first();
                getForm().getMonitSessioniRecupList().getTable().setPageSize(10);
            }
        }
        forwardToPublisher(Application.Publisher.MONITORAGGIO_SESSIONI_RECUP_RICERCA);
    }

    @Secure(action = "detail/MonitoraggioForm#MonitSessioniRecupList/downloadFileSessioneRecupList")
    public void downloadFileSessioneRecupList() throws EMFError {
        setTableName(getRequest().getParameter("table"));
        setRiga(getRequest().getParameter("riga"));
        getForm().getMonitSessioniRecupList().getTable().setCurrentRowIndex(Integer.parseInt(getRiga()));

        MonVLisSesRecupRowBean row = (MonVLisSesRecupRowBean) getForm().getMonitSessioniRecupList().getTable()
                .getCurrentRow();

        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore()
                .setAmbiente(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.AMBIENTE.name()));
        recupero.getVersatore().setEnte(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.ENTE.name()));
        recupero.getVersatore()
                .setStruttura(getUser().getOrganizzazioneMap().get(WebConstants.Organizzazione.STRUTTURA.name()));
        recupero.getVersatore().setUserID(getUser().getIdUtente() + "");

        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(row.getCdRegistroKeyUnitaDoc());
        recupero.getChiave().setAnno(BigInteger.valueOf(row.getAaKeyUnitaDoc().longValue()));
        recupero.getChiave().setNumero(row.getCdKeyUnitaDoc());

        RecuperoWeb recuperoWeb = null;
        switch (CostantiDB.TipiEntitaRecupero.valueOf(row.getTiOutputRecup())) {
        case COMP:
        case COMP_DIP_ESIBIZIONE:
        case SUB_COMP:
            recuperoWeb = new RecuperoWeb(recupero, getUser(), row.getIdUnitaDoc(), row.getIdCompDoc(),
                    CostantiDB.TipoSalvataggioFile.FILE, CostantiDB.TipiEntitaRecupero.valueOf(row.getTiOutputRecup()));
            break;
        case DOC:
        case DOC_DIP_ESIBIZIONE:
            recuperoWeb = new RecuperoWeb(recupero, getUser(), row.getIdUnitaDoc(), row.getIdDoc(),
                    CostantiDB.TipoSalvataggioFile.FILE, CostantiDB.TipiEntitaRecupero.valueOf(row.getTiOutputRecup()));
            break;
        case UNI_DOC:
        case UNI_DOC_UNISYNCRO:
            // EVO#20972
        case UNI_DOC_UNISYNCRO_V2:
            // end EVO#20972
        case UNI_DOC_DIP_ESIBIZIONE:
            recuperoWeb = new RecuperoWeb(recupero, getUser(), row.getIdUnitaDoc(), CostantiDB.TipoSalvataggioFile.FILE,
                    CostantiDB.TipiEntitaRecupero.valueOf(row.getTiOutputRecup()));
            break;
        default:
            throw new EMFError(EMFError.BLOCKING, "Tipo di output per il recupero non gestito");
        }
        RispostaWSRecupero rispostaWs = recuperoWeb.recuperaOggetto();
        switch (rispostaWs.getSeverity()) {
        case OK:
            getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), rispostaWs.getNomeFile());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(),
                    rispostaWs.getRifFileBinario().getFileSuDisco().getPath());
            getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(), Boolean.toString(true));
            break;
        case WARNING:
            getMessageBox().addInfo(rispostaWs.getIstanzaEsito().getEsitoGenerale().getMessaggioErrore());
            break;
        case ERROR:
            getMessageBox().addError(rispostaWs.getIstanzaEsito().getEsitoGenerale().getMessaggioErrore());
            break;
        }
        if (!getMessageBox().isEmpty()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }
    }

    public void download() throws EMFError, IOException {
        String filename = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        String path = (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        Boolean deleteFile = Boolean.parseBoolean(
                (String) getSession().getAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name()));
        if (path != null && filename != null) {
            File fileToDownload = new File(path);
            if (fileToDownload.exists()) {
                /*
                 * Definiamo l'output previsto che sar\u00e0 un file in formato zip di cui si occuper\u00e0 la servlet
                 * per fare il download
                 */
                getResponse().setContentType("application/zip");
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                try (OutputStream outUD = getServletOutputStream();
                        FileInputStream inputStream = new FileInputStream(fileToDownload);) {

                    getResponse().setHeader("Content-Length", String.valueOf(fileToDownload.length()));
                    byte[] bytes = new byte[8000];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(bytes)) != -1) {
                        outUD.write(bytes, 0, bytesRead);
                    }
                    outUD.flush();
                } catch (IOException e) {
                    log.error("Eccezione nel recupero del documento ", e);
                    getMessageBox().addError("Eccezione nel recupero del documento");
                } finally {
                    freeze();
                }
                // Nel caso sia stato richiesto, elimina il file
                if (deleteFile.booleanValue()) {
                    FileUtils.deleteQuietly(fileToDownload);
                }
            } else {
                getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
                forwardToPublisher(getLastPublisher());
            }
        } else {
            getMessageBox().addError("Errore durante il tentativo di download. File non trovato");
            forwardToPublisher(getLastPublisher());
        }
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name());
        getSession().removeAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name());
    }

    @Override
    public JSONObject triggerFiltriReplicaOrgId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriReplicaOrg(), ActionEnums.SezioneMonitoraggio.REPLICA_ORG);
        return getForm().getFiltriReplicaOrg().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdDocDerivantiDaVersFallitiClasse_erroreOnTrigger() throws EMFError {
        getForm().getFiltriUdDocDerivantiDaVersFalliti().post(getRequest());
        String classe = getForm().getFiltriUdDocDerivantiDaVersFalliti().getClasse_errore().parse();
        DecodeMap mappaSottoClasse = caricaErrori.filtraSottoclasse(classe);
        getForm().getFiltriUdDocDerivantiDaVersFalliti().getSottoclasse_errore().setDecodeMap(mappaSottoClasse);
        getForm().getFiltriUdDocDerivantiDaVersFalliti().getCodice_errore().setDecodeMap(new DecodeMap());
        return getForm().getFiltriUdDocDerivantiDaVersFalliti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriUdDocDerivantiDaVersFallitiSottoclasse_erroreOnTrigger() throws EMFError {
        getForm().getFiltriUdDocDerivantiDaVersFalliti().post(getRequest());
        String sottoClasse = getForm().getFiltriUdDocDerivantiDaVersFalliti().getSottoclasse_errore().parse();
        DecodeMap mappaCodice = caricaErrori.filtraCodice(sottoClasse);
        getForm().getFiltriUdDocDerivantiDaVersFalliti().getCodice_errore().setDecodeMap(mappaCodice);
        return getForm().getFiltriUdDocDerivantiDaVersFalliti().asJSON();
    }

    @Override
    public JSONObject triggerFiltriReplicaOrgId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriReplicaOrg(), ActionEnums.SezioneMonitoraggio.REPLICA_ORG);
        return getForm().getFiltriReplicaOrg().asJSON();
    }

    @Override
    public void ricercaRepliche() throws EMFError {
        getForm().getFiltriReplicaOrg().getRicercaRepliche().setDisableHourGlass(true);
        FiltriReplicaOrg filtri = getForm().getFiltriReplicaOrg();
        filtri.post(getRequest());
        if (filtri.validate(getMessageBox())) {

            IamVLisOrganizDaReplicTableBean organizReplicaTB = monitoraggioHelper.getIamVLisOrganizDaReplicTableBean(
                    filtri, ActionUtils.getQueryMaxResults(getUser().getConfigurazione(),
                            ActionEnums.Configuration.MAX_RESULT_REPLICA_ORGANIZ.name()));
            getForm().getReplicaOrgList().setTable(organizReplicaTB);
            getForm().getReplicaOrgList().getTable().setPageSize(10);
            getForm().getReplicaOrgList().getTable().first();
        }
        forwardToPublisher(Application.Publisher.REPLICA_ORG_LIST);
    }

    @Override
    public void pulisciRepliche() throws EMFError {
        resetReplicaOrganizzazioniPage();
        forwardToPublisher(Application.Publisher.REPLICA_ORG_LIST);
    }

    @Secure(action = "Menu.Monitoraggio.VisualizzaReplicheOrganizzazioni")
    public void replicaOrganizzazioniPage() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Monitoraggio.VisualizzaReplicheOrganizzazioni");
        resetReplicaOrganizzazioniPage();
        forwardToPublisher(Application.Publisher.REPLICA_ORG_LIST);
    }

    public void resetReplicaOrganizzazioniPage() throws EMFError {
        getForm().getFiltriReplicaOrg().setEditMode();
        getForm().getFiltriReplicaOrg().reset();
        // Inizializzo la combo Ambiente
        OrgAmbienteTableBean tmpTableBeanAmbiente = new OrgAmbienteTableBean();
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error(ERRORE_RECUPERO_AMBIENTE, ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriReplicaOrg().getId_ambiente().setDecodeMap(mappaAmbiente);

        /*
         * Se ho un solo ambiente lo setto gi\u00e0 impostato nella combo e procedo con i controlli successivi
         */
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getFiltriReplicaOrg().getId_ambiente()
                    .setValue(tmpTableBeanAmbiente.getRow(0).getIdAmbiente().toString());
            BigDecimal idAmbiente = tmpTableBeanAmbiente.getRow(0).getIdAmbiente();
            checkUniqueAmbienteInCombo(idAmbiente, ActionEnums.SezioneMonitoraggio.REPLICA_ORG);
        } /*
           * altrimenti imposto la combo ambiente con i diversi valori ma senza averne selezionato uno in particolare e
           * imposto vuote le altre combo
           */ else {
            getForm().getFiltriReplicaOrg().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriReplicaOrg().getId_strut().setDecodeMap(new DecodeMap());
        }

        // Popolo le combo "Tipo operatore" e "Stato replica"
        getForm().getFiltriReplicaOrg().getTi_oper_replic()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_oper", Constants.TiOperReplic.values()));
        getForm().getFiltriReplicaOrg().getTi_stato_replic()
                .setDecodeMap(ComboGetter.getMappaSortedGenericEnum("ti_stato", Constants.TiStatoReplic.values()));

        getForm().getReplicaOrgList().clear();
    }

    /*
     * MONITORAGGIO VERSAMENTI ANNULLATI
     */
    @Override
    public JSONObject triggerFiltriDocumentiAnnullatiId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriDocumentiAnnullati(),
                ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_ANNUL);
        return getForm().getFiltriDocumentiAnnullati().asJSON();
    }

    @Override
    public JSONObject triggerFiltriDocumentiAnnullatiId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriDocumentiAnnullati(),
                ActionEnums.SezioneMonitoraggio.FILTRI_DOCUMENTI_ANNUL);
        return getForm().getFiltriDocumentiAnnullati().asJSON();
    }

    @Override
    public JSONObject triggerFiltriDocumentiAnnullatiId_strutOnTrigger() throws EMFError {
        triggerStrutGenerico(getForm().getFiltriDocumentiAnnullati());

        BigDecimal idStrut = getForm().getFiltriDocumentiAnnullati().getId_strut().parse();
        getForm().getFiltriDocumentiAnnullati().getCd_registro_key_unita_doc()
                .setDecodeMap(getMappaRegistro(idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()));

        // Setto i valori della combo TIPO DOC ricavati dalla tabella DEC_TIPO_DOC
        DecTipoDocTableBean tmpTableBeanTipoDoc = tipoDocumentoEjb.getDecTipoDocTableBean(
                (idStrut != null ? idStrut : getUser().getIdOrganizzazioneFoglia()), false, false);
        getForm().getFiltriDocumentiAnnullati().getId_tipo_doc()
                .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanTipoDoc, "id_tipo_doc", "nm_tipo_doc"));
        return getForm().getFiltriDocumentiAnnullati().asJSON();
    }

    @Override
    public void ricercaDocAnnul() throws EMFError {
        MonitoraggioForm.FiltriDocumentiAnnullati filtri = getForm().getFiltriDocumentiAnnullati();
        // Valida i filtri per verificare quelli obbligatori
        if (filtri.postAndValidate(getRequest(), getMessageBox())) {
            UnitaDocumentarieValidator validator = new UnitaDocumentarieValidator(getMessageBox());

            // Seconda validazione: controllo che il range di giorno versamento sia corretto e setto gli eventuali
            // valori di default
            Date[] dateValidate = validator.validaDate(filtri.getGiorno_vers_da().parse(),
                    filtri.getOre_vers_da().parse(), filtri.getMinuti_vers_da().parse(),
                    filtri.getGiorno_vers_a().parse(), filtri.getOre_vers_a().parse(),
                    filtri.getMinuti_vers_a().parse(), filtri.getGiorno_vers_da().getHtmlDescription(),
                    filtri.getGiorno_vers_a().getHtmlDescription());

            if (!getMessageBox().hasError()) {
                validator.controllaPresenzaAnno(filtri.getAa_key_unita_doc().parse(),
                        filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse());
            }

            Object[] chiavi = null;
            if (!getMessageBox().hasError()) {
                // Valida i campi di Range di chiavi unit\u00e0 documentaria
                chiavi = validator.validaChiaviUnitaDoc(filtri.getCd_registro_key_unita_doc().getValue(),
                        filtri.getAa_key_unita_doc().parse(), filtri.getCd_key_unita_doc().parse(),
                        filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse(),
                        filtri.getCd_key_unita_doc_da().parse(), filtri.getCd_key_unita_doc_a().parse());
            }

            if (!getMessageBox().hasError()) {
                // La validazione non ha riportato errori.
                if (chiavi != null && chiavi.length == 5) {
                    filtri.getAa_key_unita_doc_da()
                            .setValue(chiavi[1] != null ? ((BigDecimal) chiavi[1]).toString() : null);
                    filtri.getAa_key_unita_doc_a()
                            .setValue(chiavi[2] != null ? ((BigDecimal) chiavi[2]).toString() : null);
                    filtri.getCd_key_unita_doc_da().setValue(chiavi[3] != null ? (String) chiavi[3] : null);
                    filtri.getCd_key_unita_doc_a().setValue(chiavi[4] != null ? (String) chiavi[4] : null);
                }
                // Le eventuali date riferite al giorno di versamento vengono salvate in sessione
                if (dateValidate != null) {
                    SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
                    filtri.getGiorno_vers_da_validato().setValue(df.format(dateValidate[0]));
                    filtri.getGiorno_vers_a_validato().setValue(df.format(dateValidate[1]));
                }

                // La validazione non ha riportato errori. Carico la tabella con i filtri impostati
                String maxResultStandard = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_RESULT_STANDARD);
                MonVLisUniDocDaAnnulTableBean monVLisDocTableBean = monitoraggioEjb
                        .getMonVLisUniDocDaAnnul(getUser().getIdUtente(), filtri, Integer.parseInt(maxResultStandard));
                getForm().getDocumentiAnnullatiList().setTable(monVLisDocTableBean);
                getForm().getDocumentiAnnullatiList().getTable().setPageSize(10);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getDocumentiAnnullatiList().getTable().first();
            }
        }
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void verificaAutomatica() throws EMFError {
        getForm().getFiltriVerificaVersamenti().setEditMode();
        /* Riporto ambiente, ente e struttura che sto trattando */
        getForm().getFiltriVerificaVersamenti().getNm_ambiente()
                .setValue(getForm().getFiltriVersamenti().getId_ambiente().getDecodedValue());
        getForm().getFiltriVerificaVersamenti().getNm_ente()
                .setValue(getForm().getFiltriVersamenti().getId_ente().getDecodedValue());
        getForm().getFiltriVerificaVersamenti().getNm_strut()
                .setValue(getForm().getFiltriVersamenti().getId_strut().getDecodedValue());
        BigDecimal idStrut = getForm().getFiltriVersamenti().getId_strut().parse();
        getForm().getFiltriVerificaVersamenti().getId_strut().setValue(idStrut.toPlainString());
        if (getForm().getFiltriVersamenti().getId_strut().parse() != null) {
            /* Calcolo l'istante pi\u00e0¹ recente di registrazione INIZIO_SCHEDULAZIONE in LogJob */
            Date ultimaRegistrazione = calcoloAsync.getUltimaRegistrazione(
                    JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
                    getForm().getFiltriVersamenti().getId_strut().parse().longValue());
            SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
            getForm().getFiltriVerificaVersamenti().getData_registrazione().setValue(df.format(ultimaRegistrazione));
            df.applyPattern("HH");
            getForm().getFiltriVerificaVersamenti().getOre_registrazione().setValue(df.format(ultimaRegistrazione));
            df.applyPattern("mm");
            getForm().getFiltriVerificaVersamenti().getMinuti_registrazione().setValue(df.format(ultimaRegistrazione));
            forwardToPublisher(Application.Publisher.MONITORAGGIO_IMPOSTA_DATA_VERIFICA_VERS);
        } else {
            getMessageBox()
                    .addError("Attenzione: nessuna struttura selezionata per effettuare la verifica versamenti!");
            forwardToPublisher(Application.Publisher.MONITORAGGIO_VERS_FALLITI_RICERCA);
        }
    }

    @Override
    public JSONObject triggerFiltriJobSchedulatiId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriJobSchedulati(), ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI);
        return getForm().getFiltriJobSchedulati().asJSON();
    }

    @Override
    public JSONObject triggerFiltriJobSchedulatiId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriJobSchedulati(), ActionEnums.SezioneMonitoraggio.JOB_SCHEDULATI);
        return getForm().getFiltriJobSchedulati().asJSON();
    }

    @Override
    public JSONObject triggerFiltriJobSchedulatiNm_jobOnTrigger() throws EMFError {
        if (getSession().getAttribute(FROM_GESTIONE_JOB) == null) {
            getForm().getFiltriJobSchedulati().post(getRequest());
        }
        String nomeJob = getForm().getFiltriJobSchedulati().getNm_job().parse();

        getForm().getFiltriJobSchedulati().getId_ambiente().setDecodeMap(new DecodeMap());
        getForm().getFiltriJobSchedulati().getId_ente().setDecodeMap(new DecodeMap());
        getForm().getFiltriJobSchedulati().getId_strut().setDecodeMap(new DecodeMap());

        if (nomeJob != null && (nomeJob.equals(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name())
                || nomeJob.equals(JobConstants.JobEnum.CALCOLA_CHIAVE_UD_DOC.name()))) {
            populateFiltriJobSchedulati();
        }
        return getForm().getFiltriJobSchedulati().asJSON();
    }

    public void populateFiltriJobSchedulati() {
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
            log.error("Errore in ricerca ambiente", ex);
        }

        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriJobSchedulati().getId_ambiente().setDecodeMap(mappaAmbiente);
        getForm().getFiltriJobSchedulati().getId_ambiente().setValue(idAmbiente.toString());

        DecodeMap mappaEnte = new DecodeMap();
        mappaEnte.populatedMap(tmpTableBeanEnte, "id_ente", "nm_ente");
        getForm().getFiltriJobSchedulati().getId_ente().setDecodeMap(mappaEnte);
        getForm().getFiltriJobSchedulati().getId_ente().setValue(idEnte.toString());

        DecodeMap mappaStrut = new DecodeMap();
        mappaStrut.populatedMap(tmpTableBeanStruttura, "id_strut", "nm_strut");
        getForm().getFiltriJobSchedulati().getId_strut().setDecodeMap(mappaStrut);
        getForm().getFiltriJobSchedulati().getId_strut().setValue(idStruttura.toString());
    }

    @Override
    public void confermaVerificaAutomatica() throws EMFError {
        getForm().getFiltriVerificaVersamenti().post(getRequest());
        if (getForm().getFiltriVerificaVersamenti().validate(getMessageBox())) {
            Date dataRegistrazione = getForm().getFiltriVerificaVersamenti().getData_registrazione().parse();
            int ore = getForm().getFiltriVerificaVersamenti().getOre_registrazione().parse().intValue();
            int minuti = getForm().getFiltriVerificaVersamenti().getMinuti_registrazione().parse().intValue();
            // Controllo che i campi degli orari e minuti siano validi (23 ore 59 minuti)
            TypeValidator validatore = new TypeValidator(getMessageBox());
            validatore.isTimeValid(new BigDecimal(ore), new BigDecimal(minuti), "Campo ora");
            if (!getMessageBox().hasError()) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataRegistrazione);
                cal.set(Calendar.HOUR_OF_DAY, ore);
                cal.set(Calendar.MINUTE, minuti);
                // MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti.
                BigDecimal idStrut = getForm().getFiltriVerificaVersamenti().getId_strut().parse();
                boolean esitoPositivo = calcoloAsync.verificaVersamentiFallitiAsincrono(idStrut, cal.getTime());
                if (esitoPositivo) {
                    getMessageBox().addInfo(
                            "Chiamata asincrona per la verifica automatica versamenti falliti per la struttura "
                                    + getForm().getFiltriVersamenti().getId_strut().getDecodedValue()
                                    + " eseguita con successo!");
                } else {
                    getMessageBox().addError(
                            "Attenzione: errore sulla chiamata asincrona per la verifica automatica versamenti falliti per la struttura!");
                }
                goBack();
            } else {
                forwardToPublisher(Application.Publisher.MONITORAGGIO_IMPOSTA_DATA_VERIFICA_VERS);
            }
        } else {
            forwardToPublisher(Application.Publisher.MONITORAGGIO_IMPOSTA_DATA_VERIFICA_VERS);
        }
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_categ_tipo_unita_docOnTrigger() throws EMFError {
        FiltriContenutoSacer filtri = getForm().getFiltriContenutoSacer();
        // Eseguo la post del filtri
        filtri.post(getRequest());
        List<BigDecimal> idCategTipoUnitaDoc = getForm().getFiltriContenutoSacer().getId_categ_tipo_unita_doc().parse();
        if (!idCategTipoUnitaDoc.isEmpty()) {
            // Ricavo il TableBean relativo alle sottocategorie dipendenti dalla categoria scelta
            DecCategTipoUnitaDocTableBean sottoCategTipoUnitaDocTableBean = tipoUnitaDocEjb
                    .getDecCategTipoUnitaDocChildTableBean(idCategTipoUnitaDoc);
            DecodeMap mappaSottocagorie = new DecodeMap();
            mappaSottocagorie.populatedMap(sottoCategTipoUnitaDocTableBean, "id_categ_tipo_unita_doc",
                    "cd_categ_tipo_unita_doc");
            getForm().getFiltriContenutoSacer().getId_sottocateg_tipo_unita_doc().setDecodeMap(mappaSottocagorie);
        } else {
            getForm().getFiltriContenutoSacer().getId_sottocateg_tipo_unita_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriContenutoSacer().asJSON();
    }

    @Override
    public JSONObject triggerFiltriContenutoSacerId_strutOnTrigger() throws EMFError {
        FiltriContenutoSacer filtri = getForm().getFiltriContenutoSacer();
        // Eseguo la post del filtri
        filtri.post(getRequest());
        // Ricavo l'elenco delle strutture selezionate
        List<BigDecimal> strutSelezionate = filtri.getId_strut().parse();
        if (strutSelezionate.size() == 1) {
            BigDecimal idStrut = strutSelezionate.get(0);
            // Ricavo i tablebean
            OrgSubStrutTableBean subStrutTableBean = subStrutEjb
                    .getOrgSubStrutTableBeanAbilitate(getUser().getIdUtente(), idStrut);
            DecRegistroUnitaDocTableBean registroUnitaDocTableBean = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            DecTipoUnitaDocTableBean tipoUDTableBean = tipoUnitaDocEjb.getTipiUnitaDocAbilitati(getUser().getIdUtente(),
                    idStrut);
            DecTipoDocTableBean tipoDocTableBean = tipoDocumentoEjb.getDecTipoDocTableBean(idStrut, true, false);

            // Creo le mappe
            DecodeMap mappaSottoStrutture = new DecodeMap();
            mappaSottoStrutture.populatedMap(subStrutTableBean, "id_sub_strut", "nm_sub_strut");
            DecodeMap mappaRegistro = new DecodeMap();
            mappaRegistro.populatedMap(registroUnitaDocTableBean, "id_registro_unita_doc", "cd_registro_unita_doc");
            DecodeMap mappaTipoUD = new DecodeMap();
            mappaTipoUD.populatedMap(tipoUDTableBean, "id_tipo_unita_doc", "nm_tipo_unita_doc");
            DecodeMap mappaTipoDoc = new DecodeMap();
            mappaTipoDoc.populatedMap(tipoDocTableBean, "id_tipo_doc", "nm_tipo_doc");
            // Setto le combo
            filtri.getId_sub_strut().setDecodeMap(mappaSottoStrutture);
            filtri.getId_registro_unita_doc().setDecodeMap(mappaRegistro);
            filtri.getId_tipo_unita_doc().setDecodeMap(mappaTipoUD);
            filtri.getId_tipo_doc().setDecodeMap(mappaTipoDoc);
            /* Se ho una sola sottostruttura la setto gi\u00e0 impostata nella multiselect */
            if (subStrutTableBean.size() == 1) {
                filtri.getId_sub_strut()
                        .setValues(new String[] { subStrutTableBean.getRow(0).getIdSubStrut().toString() });
            }
        } else {
            filtri.getId_sub_strut().setDecodeMap(new DecodeMap());
            filtri.getId_registro_unita_doc().setDecodeMap(new DecodeMap());
            filtri.getId_tipo_unita_doc().setDecodeMap(new DecodeMap());
            filtri.getId_tipo_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriContenutoSacer().asJSON();
    }

    @Override
    public JSONObject triggerFiltriOperazioniElenchiVersamentoId_ambienteOnTrigger() throws EMFError {
        triggerAmbienteGenerico(getForm().getFiltriOperazioniElenchiVersamento(),
                ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO);
        return getForm().getFiltriOperazioniElenchiVersamento().asJSON();
    }

    @Override
    public JSONObject triggerFiltriOperazioniElenchiVersamentoId_enteOnTrigger() throws EMFError {
        triggerEnteGenerico(getForm().getFiltriOperazioniElenchiVersamento(),
                ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO);
        return getForm().getFiltriOperazioniElenchiVersamento().asJSON();
    }

    @Override
    public void pulisciOperazioniElenchiVersamento() throws EMFError {
        try {
            // Ricarica la form di ricerca
            this.operazioniElenchiVersamento();
        } catch (Exception ex) {
            log.error("Eccezione", ex);
        }
    }

    private void resetFiltriOperazioniElenchiVersamento() throws EMFError {
        getForm().getFiltriOperazioniElenchiVersamento().reset();

        // Inizializzo la combo Ambiente
        OrgAmbienteTableBean tmpTableBeanAmbiente = null;
        try {
            // Ricavo i valori della combo AMBIENTE dalla tabella ORG_AMBIENTE
            tmpTableBeanAmbiente = ambienteEjb.getAmbientiAbilitati(getUser().getIdUtente());
        } catch (Exception ex) {
            log.error(ERRORE_RECUPERO_AMBIENTE, ex);
        }
        DecodeMap mappaAmbiente = new DecodeMap();
        mappaAmbiente.populatedMap(tmpTableBeanAmbiente, "id_ambiente", "nm_ambiente");
        getForm().getFiltriOperazioniElenchiVersamento().getId_ambiente().setDecodeMap(mappaAmbiente);

        /*
         * Se ho un solo ambiente lo setto gi\u00e0 impostato nella combo e procedo con i controlli successivi
         */
        if (tmpTableBeanAmbiente.size() == 1) {
            getForm().getFiltriOperazioniElenchiVersamento().getId_ambiente()
                    .setValue(tmpTableBeanAmbiente.getRow(0).getIdAmbiente().toString());
            BigDecimal idAmbiente = tmpTableBeanAmbiente.getRow(0).getIdAmbiente();
            checkUniqueAmbienteInCombo(idAmbiente, ActionEnums.SezioneMonitoraggio.OPERAZIONI_ELENCHI_VERSAMENTO);
        } /*
           * altrimenti imposto la combo ambiente con i diversi valori ma senza averne selezionato uno in particolare e
           * imposto vuote le altre combo
           */ else {
            getForm().getFiltriOperazioniElenchiVersamento().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriOperazioniElenchiVersamento().getId_strut().setDecodeMap(new DecodeMap());
        }

        /* Inizializzo le combo Modalit\u00e0 operazione e Tipo output */
        getForm().getFiltriOperazioniElenchiVersamento().getTi_mod_oper().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("modalita", VolumeEnums.ModalitaOperazioni.values()));
        getForm().getFiltriOperazioniElenchiVersamento().getTi_output().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum("ti_output", VolumeEnums.TipoOutputMonitoraggio.values()));

        /* Di default imposto checkate le varie checkbox */
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_chiusura_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_crea_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_crea_indice_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_def_note_elenco_chiuso().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_def_note_indice_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_elimina_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_firma_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_mod_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_recupera_elenco_aperto().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_recupera_elenco_scaduto().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_rimuovi_doc_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_rimuovi_ud_elenco().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_set_elenco_aperto().setChecked(true);
        getForm().getFiltriOperazioniElenchiVersamento().getFl_oper_set_elenco_da_chiudere().setChecked(true);

        // Imposto in editMode tutti i campi
        getForm().getFiltriOperazioniElenchiVersamento().setEditMode();
    }

    @Override
    public void verificaVersamentiFalliti() throws EMFError {
        getForm().getFiltriVerificaVersamenti().setEditMode();
        /* Riporto ambiente, ente e struttura che sto trattando */
        getForm().getFiltriVerificaVersamenti().getNm_ambiente()
                .setValue(getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ambiente().getDecodedValue());
        getForm().getFiltriVerificaVersamenti().getNm_ente()
                .setValue(getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_ente().getDecodedValue());
        getForm().getFiltriVerificaVersamenti().getNm_strut()
                .setValue(getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().getDecodedValue());
        BigDecimal idStrut = getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().parse();
        getForm().getFiltriVerificaVersamenti().getId_strut().setValue(idStrut.toPlainString());

        if (idStrut != null) {
            /* Calcolo l'istante pi\u00e0¹ recente di registrazione INIZIO_SCHEDULAZIONE in LogJob */
            Date ultimaRegistrazione = calcoloAsync.getUltimaRegistrazione(
                    JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
                    getForm().getFiltriUdDocDerivantiDaVersFalliti().getId_strut().parse().longValue());
            SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
            getForm().getFiltriVerificaVersamenti().getData_registrazione().setValue(df.format(ultimaRegistrazione));
            df.applyPattern("HH");
            getForm().getFiltriVerificaVersamenti().getOre_registrazione().setValue(df.format(ultimaRegistrazione));
            df.applyPattern("mm");
            getForm().getFiltriVerificaVersamenti().getMinuti_registrazione().setValue(df.format(ultimaRegistrazione));
            forwardToPublisher(Application.Publisher.MONITORAGGIO_IMPOSTA_DATA_VERIFICA_VERS);
        } else {
            getMessageBox()
                    .addError("Attenzione: nessuna struttura selezionata per effettuare la verifica versamenti!");
            forwardToPublisher(Application.Publisher.MONITORAGGIO_DOCUMENTI_DERIVANTI_DA_VERS_FALLITI_RICERCA);
        }
    }

    @Override
    public JSONObject triggerFiltriRicercaSessioniRecuperoId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSessioniRecupero().post(getRequest());
        BigDecimal idAmbiente = getForm().getFiltriRicercaSessioniRecupero().getId_ambiente().parse();
        if (idAmbiente != null) {
            OrgEnteTableBean tmpTableBeanEnte = ambienteEjb.getEntiAbilitatiNoTemplate(getUser().getIdUtente(),
                    idAmbiente.longValue(), Boolean.TRUE);
            getForm().getFiltriRicercaSessioniRecupero().getId_ente()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanEnte, "id_ente", "nm_ente"));

            if (tmpTableBeanEnte.size() == 1) {
                // Esiste solo un ente, la setto immediatamente e verifico le strutture
                getForm().getFiltriRicercaSessioniRecupero().getId_ente()
                        .setValue(tmpTableBeanEnte.getRow(0).getIdEnte().toString());
                OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(),
                        tmpTableBeanEnte.getRow(0).getIdEnte(), Boolean.TRUE);
                getForm().getFiltriRicercaSessioniRecupero().getId_strut()
                        .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStrut, "id_strut", "nm_strut"));
                if (tmpTableBeanStrut.size() == 1) {
                    getForm().getFiltriRicercaSessioniRecupero().getId_strut()
                            .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                    DecRegistroUnitaDocTableBean tmpTableBeanRUD = registroEjb.getRegistriUnitaDocAbilitati(
                            getUser().getIdUtente(), tmpTableBeanStrut.getRow(0).getIdStrut());
                    getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc()
                            .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanRUD, "id_registro_unita_doc",
                                    "cd_registro_unita_doc"));
                } else {
                    getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc()
                            .setDecodeMap(new DecodeMap());
                }
            } else {
                getForm().getFiltriRicercaSessioniRecupero().getId_strut().setDecodeMap(new DecodeMap());
                getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaSessioniRecupero().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSessioniRecupero().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriRicercaSessioniRecupero().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSessioniRecuperoId_enteOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSessioniRecupero().post(getRequest());
        BigDecimal idEnte = getForm().getFiltriRicercaSessioniRecupero().getId_ente().parse();
        if (idEnte != null) {
            OrgStrutTableBean tmpTableBeanStrut = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            getForm().getFiltriRicercaSessioniRecupero().getId_strut()
                    .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanStrut, "id_strut", "nm_strut"));
            if (tmpTableBeanStrut.size() == 1) {
                getForm().getFiltriRicercaSessioniRecupero().getId_strut()
                        .setValue(tmpTableBeanStrut.getRow(0).getIdStrut().toString());
                DecRegistroUnitaDocTableBean tmpTableBeanRUD = registroEjb.getRegistriUnitaDocAbilitati(
                        getUser().getIdUtente(), tmpTableBeanStrut.getRow(0).getIdStrut());
                getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc()
                        .setDecodeMap(DecodeMap.Factory.newInstance(tmpTableBeanRUD, "id_registro_unita_doc",
                                "cd_registro_unita_doc"));
            } else {
                getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc()
                        .setDecodeMap(new DecodeMap());
            }
        } else {
            getForm().getFiltriRicercaSessioniRecupero().getId_strut().setDecodeMap(new DecodeMap());
            getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
        }

        return getForm().getFiltriRicercaSessioniRecupero().asJSON();
    }

    @Override
    public JSONObject triggerFiltriRicercaSessioniRecuperoId_strutOnTrigger() throws EMFError {
        getForm().getFiltriRicercaSessioniRecupero().post(getRequest());
        BigDecimal idStrut = getForm().getFiltriRicercaSessioniRecupero().getId_strut().parse();
        if (idStrut != null) {
            DecRegistroUnitaDocTableBean tmpTableBeanRUD = registroEjb
                    .getRegistriUnitaDocAbilitati(getUser().getIdUtente(), idStrut);
            getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc().setDecodeMap(
                    DecodeMap.Factory.newInstance(tmpTableBeanRUD, "id_registro_unita_doc", "cd_registro_unita_doc"));
        } else {
            getForm().getFiltriRicercaSessioniRecupero().getCd_registro_key_unita_doc().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriRicercaSessioniRecupero().asJSON();
    }

    @Override
    public void ricercaConsistenzaSacer() throws EMFError {
        getForm().getFiltriConsistenzaSacer().getRicercaConsistenzaSacer().setDisableHourGlass(true);
        FiltriConsistenzaSacer filtri = getForm().getFiltriConsistenzaSacer();
        getSession().removeAttribute(APRI_DELTA);
        getSession().removeAttribute(PROBLEMI_DELTA);
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        // Verifico i campi e le date
        if (filtri.validate(getMessageBox())) {
            MonitoraggioValidator validatore = new MonitoraggioValidator(getMessageBox());
            validatore.validaDataCalcoloConsistenzaSacer(filtri.getDt_rif_da().parse(), filtri.getDt_rif_a().parse());
            if (!getMessageBox().hasError()) {
                Object[] risultato = monitoraggioHelper.getListaTotaliConsistenzaComp(filtri);
                BaseTableInterface<?> totSacer = (BaseTableInterface<?>) risultato[0];
                BaseRowInterface rigaTotali = (BaseRowInterface) risultato[1];
                // Setto la lista con i conteggi
                getForm().getConsistenzaSacerList().setTable(totSacer);
                getForm().getConsistenzaSacerList().getTable().first();
                getForm().getConsistenzaSacerList().getTable().setPageSize(10);
                // Setto i totali
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_aip_generato()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_aip_generato"));
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_aip_in_aggiorn()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_aip_in_aggiorn"));
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_annul()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_annul"));
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_delta()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_delta"));
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_in_volume()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_in_volume"));
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_presa_in_carico()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_presa_in_carico"));
                getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_vers()
                        .setValue("" + rigaTotali.getBigDecimal("totale_ni_comp_vers"));

                // Setto i filtri utilizzati
                String filtriUtilizzati = "";
                if (getForm().getFiltriConsistenzaSacer().getId_ambiente().parse() != null
                        && !getForm().getFiltriConsistenzaSacer().getId_ambiente().parse().isEmpty()) {
                    filtriUtilizzati = filtriUtilizzati + "Ambiente: "
                            + getForm().getFiltriConsistenzaSacer().getId_ambiente().getDecodedValues() + "; ";
                }
                if (getForm().getFiltriConsistenzaSacer().getId_ente().parse() != null) {
                    filtriUtilizzati = filtriUtilizzati + "Ente: "
                            + getForm().getFiltriConsistenzaSacer().getId_ente().getDecodedValue() + "; ";
                }
                if (getForm().getFiltriConsistenzaSacer().getId_strut().parse() != null) {
                    filtriUtilizzati = filtriUtilizzati + "Struttura: "
                            + getForm().getFiltriConsistenzaSacer().getId_strut().getDecodedValue() + "; ";
                }
                if (getForm().getFiltriConsistenzaSacer().getDifferenza_zero().parse() != null) {
                    filtriUtilizzati = filtriUtilizzati + "Differenza 0: "
                            + getForm().getFiltriConsistenzaSacer().getDifferenza_zero().getDecodedValue() + "; ";
                }
                if (getForm().getFiltriConsistenzaSacer().getDt_rif_da().parse() != null) {
                    filtriUtilizzati = filtriUtilizzati + "Data riferimento da: "
                            + getForm().getFiltriConsistenzaSacer().getDt_rif_da().getDecodedValue() + "; ";
                }
                if (getForm().getFiltriConsistenzaSacer().getDt_rif_a().parse() != null) {
                    filtriUtilizzati = filtriUtilizzati + "Data riferimento a: "
                            + getForm().getFiltriConsistenzaSacer().getDt_rif_a().getDecodedValue() + "; ";
                }
                getForm().getConsistenzaSacerTotaliUdDocComp().getFiltri_utilizzati().setValue(filtriUtilizzati);
            }
        }
        postLoad();
        forwardToPublisher(Application.Publisher.MONITORAGGIO_CONSISTENZA_SACER_RICERCA);
    }

    @Override
    public void pulisciConsistenzaSacer() throws EMFError {
        try {
            this.consistenzaSacer();
        } catch (Exception ex) {
            log.error("Eccezione", ex);
        }
    }

    /**
     * Trigger sul filtro nome ambiente di Esame Consistenza Sacer: selezionando un valore della combobox viene popolata
     * la MultiSelect relativa all'ente, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriConsistenzaSacerId_ambienteOnTrigger() throws EMFError {
        getForm().getFiltriConsistenzaSacer().post(getRequest());
        List<BigDecimal> idAmbiente = getForm().getFiltriConsistenzaSacer().getId_ambiente().parse();
        if (idAmbiente != null && !idAmbiente.isEmpty()) {
            // Ricavo il TableBean relativo agli enti dipendenti dall'ambiente scelto
            OrgEnteTableBean enteTableBean = ambienteEjb.getEntiAbilitati(getUser().getIdUtente(), idAmbiente,
                    Boolean.TRUE);
            DecodeMap mappaEnte = new DecodeMap();
            mappaEnte.populatedMap(enteTableBean, "id_ente", "nm_ente");
            getForm().getFiltriConsistenzaSacer().getId_ente().setDecodeMap(mappaEnte);
            getForm().getFiltriConsistenzaSacer().getId_strut().setDecodeMap(new DecodeMap());
        } else {
            getForm().getFiltriConsistenzaSacer().getId_ente().setDecodeMap(new DecodeMap());
            getForm().getFiltriConsistenzaSacer().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriConsistenzaSacer().asJSON();
    }

    /**
     * Trigger sul filtro nome ente di Esame Consistenza Sacer: selezionando un valore della combobox viene popolata la
     * MultiSelect relativa alla struttura, se esiste almeno un valore per essa
     *
     * @return JSONObject
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public JSONObject triggerFiltriConsistenzaSacerId_enteOnTrigger() throws EMFError {
        getForm().getFiltriConsistenzaSacer().post(getRequest());
        BigDecimal idEnte = getForm().getFiltriConsistenzaSacer().getId_ente().parse();
        if (idEnte != null) {
            // Ricavo il TableBean relativo alle strutture dipendenti dall'ente scelto
            OrgStrutTableBean strutTableBean = struttureEjb.getOrgStrutTableBean(getUser().getIdUtente(), idEnte,
                    Boolean.TRUE);
            DecodeMap mappaStrut = new DecodeMap();
            mappaStrut.populatedMap(strutTableBean, "id_strut", "nm_strut");
            getForm().getFiltriConsistenzaSacer().getId_strut().setDecodeMap(mappaStrut);
        } else {
            getForm().getFiltriConsistenzaSacer().getId_strut().setDecodeMap(new DecodeMap());
        }
        return getForm().getFiltriConsistenzaSacer().asJSON();
    }

    private static final String APRI_DELTA = "apriDelta";
    private static final String PROBLEMI_DELTA = "problemiDelta";
    private static final String PROBLEMI_DELTA_FRASE1 = "Nell'intervallo considerato per la struttura ";
    private static final String PROBLEMI_DELTA_FRASE2 = " sono state trovate le seguenti differenze: ";

    // @Secure(action = "detail/MonitoraggioForm#ConsistenzaSacerList/apriPopUpDelta")
    public void apriListaDelta() throws EMFError {
        getSession().setAttribute(APRI_DELTA, true);
        Integer riga1 = Integer.parseInt(getRequest().getParameter("riga"));
        String nmStrut = getForm().getConsistenzaSacerList().getTable().getRow(riga1).getString("nm_strut");
        BigDecimal idStrut = getForm().getConsistenzaSacerList().getTable().getRow(riga1).getBigDecimal("id_strut");
        Date dtRifDa = getForm().getFiltriConsistenzaSacer().getDt_rif_da().parse();
        Date dtRifA = getForm().getFiltriConsistenzaSacer().getDt_rif_a().parse();
        getSession().setAttribute(PROBLEMI_DELTA, PROBLEMI_DELTA_FRASE1 + nmStrut + PROBLEMI_DELTA_FRASE2);

        BaseTableInterface<?> t = monitoraggioHelper.getListaDifferenzaConsistenzaVsCalcoloSacer2(idStrut, dtRifDa,
                dtRifA);
        getForm().getConsistenzaSacerProblemsList().setTable(t);
        getForm().getConsistenzaSacerProblemsList().getTable().first();
        getForm().getConsistenzaSacerProblemsList().getTable().setPageSize(10);

        forwardToPublisher(Application.Publisher.MONITORAGGIO_CONSISTENZA_SACER_RICERCA);
    }

    @Override
    public void mostraIdConsistenzaButton() throws Throwable {
        String nmStrut = getForm().getConsistenzaSacerProblemsList().getTable().getRow(0).getString("nm_strut");
        boolean isHiddenIdStrut = getForm().getConsistenzaSacerProblemsList().getId_strut().isHidden();
        if (isHiddenIdStrut) {
            setConsistenzaColonneIdInvisibili(false);
            getForm().getMostraNascondiIdButtonList().getMostraIdConsistenzaButton().setDescription("Nascondi id");
        } else {
            setConsistenzaColonneIdInvisibili(true);
            getForm().getMostraNascondiIdButtonList().getMostraIdConsistenzaButton().setDescription("Mostra id");
        }
        getSession().setAttribute(APRI_DELTA, true);
        getSession().setAttribute(PROBLEMI_DELTA, PROBLEMI_DELTA_FRASE1 + nmStrut + PROBLEMI_DELTA_FRASE2);
        forwardToPublisher(getLastPublisher());
    }

    public void eseguiScaricaReport() throws Throwable {
        try (XWPFDocument doc = new XWPFDocument()) {
            // Creo il primo paragrafo
            XWPFParagraph p1Titolo = doc.createParagraph();
            p1Titolo.setAlignment(ParagraphAlignment.LEFT);
            // set font
            XWPFRun r1Titolo = p1Titolo.createRun();
            r1Titolo.setBold(true);
            r1Titolo.setFontSize(14);
            r1Titolo.setFontFamily("Verdana");
            Date dateDa = getForm().getFiltriConsistenzaSacer().getDt_rif_da().parse();
            Date dateA = getForm().getFiltriConsistenzaSacer().getDt_rif_a().parse();
            String stringDateDa = new SimpleDateFormat("dd/MM/yyyy").format(dateDa);
            String stringDateA = new SimpleDateFormat("dd/MM/yyyy").format(dateA);
            r1Titolo.setText("Controllo della consistenza dell’archivio");
            // Filtri utilizzati
            XWPFParagraph p0TitoloFiltri = doc.createParagraph();
            XWPFRun r0TitoloFiltri = p0TitoloFiltri.createRun();
            r0TitoloFiltri.setBold(true);
            r0TitoloFiltri.setFontSize(12);
            r0TitoloFiltri.setFontFamily("Verdana");
            r0TitoloFiltri.setText("Filtri utilizzati: ");

            String[] filtriArray = getForm().getConsistenzaSacerTotaliUdDocComp().getFiltri_utilizzati().getValue()
                    .trim().split(";");

            XWPFParagraph p0Descrizione = doc.createParagraph();
            p0Descrizione.setAlignment(ParagraphAlignment.LEFT);
            p0Descrizione.setIndentFromLeft(1000);

            XWPFRun r0Descrizione = p0Descrizione.createRun();

            XWPFRun r0Valore = p0Descrizione.createRun();

            for (String filtri : filtriArray) {
                String[] filtro = filtri.split(":");
                // set font
                r0Descrizione = p0Descrizione.createRun();
                r0Descrizione.setFontSize(10);
                r0Descrizione.setFontFamily("Verdana");

                r0Valore = p0Descrizione.createRun();
                r0Valore.setFontSize(10);
                r0Valore.setFontFamily("Verdana");
                r0Valore.setBold(true);

                r0Descrizione.setText(filtro[0].trim() + ": ");
                r0Valore.setText(filtro[1].trim() + ";");
                r0Valore.addBreak();
            }

            XWPFParagraph p1Descrizione = doc.createParagraph();
            p1Descrizione.setAlignment(ParagraphAlignment.LEFT);
            // set font
            XWPFRun r1Descrizione = p1Descrizione.createRun();
            r1Descrizione.setFontSize(10);
            r1Descrizione.setFontFamily("Verdana");

            XWPFRun r1Valore = p1Descrizione.createRun();
            r1Valore.setFontSize(10);
            r1Valore.setFontFamily("Verdana");
            r1Valore.setBold(true);

            // set font
            XWPFRun r1bisDescrizione = p1Descrizione.createRun();
            r1bisDescrizione.setFontSize(10);
            r1bisDescrizione.setFontFamily("Verdana");

            String esito1 = "";
            String esito2 = "";
            // Ricavo l'info se tutte le differenze sono 0
            boolean differenzeDaZero = new BigDecimal(
                    getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_delta().getValue())
                            .compareTo(BigDecimal.ZERO) != 0;
            if (differenzeDaZero) {
                esito1 = "Il controllo della consistenza dell’archivio è stato svolto nell'intervallo di date ";

                esito2 = " e ha avuto esito negativo, essendosi riscontrate differenze tra i componenti versati (al netto dei "
                        + "componenti contenuti nei versamenti annullati) e i componenti elaborati o in corso di elaborazione nel "
                        + "Sistema. "
                        + "Nella tabella che segue è riportato il dettaglio del controllo suddiviso per Struttura versante.";
            } else {
                esito1 = "Il controllo della consistenza dell’archivio è stato svolto nell'intervallo di date ";

                esito2 = " e ha avuto esito positivo, non essendosi riscontrate differenze tra i componenti versati (al netto "
                        + "dei componenti contenuti nei versamenti annullati) e i componenti elaborati o in corso di elaborazione "
                        + "nel Sistema. "
                        + "Nella tabella che segue è riportato il dettaglio del controllo suddiviso per Struttura versante.";
            }
            r1Descrizione.setText(esito1);
            r1Valore.setText(stringDateDa + " - " + stringDateA);
            r1bisDescrizione.setText(esito2);
            r1bisDescrizione.addBreak();

            // Creo il secondo paragrafo, con la LEGENDA
            // Titolo
            XWPFParagraph p2TitoloLegenda = doc.createParagraph();
            XWPFRun r2TitoloLegenda = p2TitoloLegenda.createRun();
            r2TitoloLegenda.setBold(true);
            r2TitoloLegenda.setFontSize(12);
            r2TitoloLegenda.setFontFamily("Verdana");
            r2TitoloLegenda.setText("Legenda");
            r2TitoloLegenda.addBreak();

            // Dal terzo paragrafo, le definizioni della legenda
            XWPFParagraph p3 = doc.createParagraph();
            XWPFRun r3 = p3.createRun();
            r3.setBold(true);
            r3.setFontSize(10);
            r3.setFontFamily("Verdana");
            r3.setText("Ambiente-Ente-Struttura: ");

            XWPFRun r3Descrizione = p3.createRun();
            r3Descrizione.setFontSize(10);
            r3Descrizione.setFontFamily("Verdana");
            r3Descrizione.setText("identificano la singola Struttura versante");
            r3Descrizione.addBreak();
            r3Descrizione.addBreak();

            XWPFRun r4 = p3.createRun();
            r4.setBold(true);
            r4.setFontSize(10);
            r4.setFontFamily("Verdana");
            r4.setText("Numero componenti nei SIP: ");

            XWPFRun r4Descrizione = p3.createRun();
            r4Descrizione.setFontSize(10);
            r4Descrizione.setFontFamily("Verdana");
            r4Descrizione.setText(
                    "numero dei componenti presenti nei SIP di versamento unità documentaria e aggiunta documento. Rappresenta il totale dei componenti ricevuti dal Sistema. ");
            r4Descrizione.addBreak();
            r4Descrizione.addBreak();

            XWPFRun r5 = p3.createRun();
            r5.setBold(true);
            r5.setFontSize(10);
            r5.setFontFamily("Verdana");
            r5.setText("Numero dei componenti in SIP annullati: ");

            XWPFRun r5Descrizione = p3.createRun();
            r5Descrizione.setFontSize(10);
            r5Descrizione.setFontFamily("Verdana");
            r5Descrizione.setText(
                    "numero dei componenti presenti nei SIP di cui è stato annullato il versamento. Vanno sottratti dai componenti presenti nei SIP versati. L’annullamento del versamento comporta la cancellazione logica degli oggetti versati. ");
            r5Descrizione.addBreak();
            r5Descrizione.addBreak();

            XWPFRun r6 = p3.createRun();
            r6.setBold(true);
            r6.setFontSize(10);
            r6.setFontFamily("Verdana");
            r6.setText("Numero componenti presi in carico ancora da elaborare: ");

            XWPFRun r6Descrizione = p3.createRun();
            r6Descrizione.setFontSize(10);
            r6Descrizione.setFontFamily("Verdana");
            r6Descrizione.setText(
                    "numero dei componenti presenti nei SIP di versamento unità documentaria per i quali ancora non è stato generato il relativo AIP (stato di conservazione: PRESO IN CARICO). ");
            r6Descrizione.addBreak();
            r6Descrizione.addBreak();

            XWPFRun r7 = p3.createRun();
            r7.setBold(true);
            r7.setFontSize(10);
            r7.setFontFamily("Verdana");
            r7.setText("Numero componenti negli AIP in aggiornamento: ");

            XWPFRun r7Descrizione = p3.createRun();
            r7Descrizione.setFontSize(10);
            r7Descrizione.setFontFamily("Verdana");
            r7Descrizione.setText(
                    "numero dei componenti presenti nei SIP di aggiunta documento per i quali ancora non è stato aggiornato il relativo AIP	(stato di conservazione: AIP IN AGGIORNAMENTO). ");
            r7Descrizione.addBreak();
            r7Descrizione.addBreak();

            XWPFRun r8 = p3.createRun();
            r8.setBold(true);
            r8.setFontSize(10);
            r8.setFontFamily("Verdana");
            r8.setText("Numero componenti negli AIP: ");

            XWPFRun r8Descrizione = p3.createRun();
            r8Descrizione.setFontSize(10);
            r8Descrizione.setFontFamily("Verdana");
            r8Descrizione.setText(
                    "numero dei componenti presenti negli AIP (stato di conservazione: AIP GENERATO, AIP FIRMATO, IN ARCHIVIO).");
            r8Descrizione.addBreak();
            r8Descrizione.addBreak();

            XWPFRun r9 = p3.createRun();
            r9.setBold(true);
            r9.setFontSize(10);
            r9.setFontFamily("Verdana");
            r9.setText("Numero componenti nei Volumi di conservazione: ");

            XWPFRun r9Descrizione = p3.createRun();
            r9Descrizione.setFontSize(10);
            r9Descrizione.setFontFamily("Verdana");
            r9Descrizione.setText(
                    "numero dei componenti presenti nei Volumi di conservazione (stato di conservazione: IN VOLUME DI CONSERVAZIONE).");
            r9Descrizione.addBreak();
            r9Descrizione.addBreak();

            XWPFRun r10 = p3.createRun();
            r10.setBold(true);
            r10.setFontSize(10);
            r10.setFontFamily("Verdana");
            r10.setText("Differenza tra componenti ricevuti e componenti conservati: ");

            XWPFRun r10Descrizione = p3.createRun();
            r10Descrizione.setFontSize(10);
            r10Descrizione.setFontFamily("Verdana");
            r10Descrizione.setText(
                    "i componenti ricevuti sono calcolati sottraendo i componenti nei versamenti annullati al totale dei componenti versati; i componenti conservati sono calcolati sommando i componenti non ancora elaborati a quelli presenti negli AIP o nei Volumi di conservazione. Un valore è diverso da zero indica una possibile perdita di dati.");

            // Recupero il file del "template" della tabella già formattata in maniera tale
            // da averla già personalizzata
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("report/Tabella.docx");
            try (XWPFDocument doc1 = new XWPFDocument(is)) {

                // Cambio orientamento per la tabella passo in LANDSCAPE (orizzontale)
                changeOrientation(doc, "landscape");

                int totA = 0;
                int totB = 0;
                // totB
                int totC = 0;
                int totD = 0;
                int totE = 0;
                int totF = 0;
                int totG = 0;

                // Ricavo la tabella dal documento "template", in particolare prendo la tabella
                // in posizione 0 (m'unica presente)
                XWPFTable table = doc1.getTableArray(0);
                // MAC #27233 - Fisso i margini del report word
                CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
                type.setType(STTblLayoutType.FIXED);

                // Ricavo la riga in posizione k, la inserisco in posizione k+1
                // (saranno le righe dei record ricavati dalla ricerca)
                // e me la faccio restituire come nuova riga su cui poi lavorarci su
                BaseTableInterface<?> tabellaConsistenza = getForm().getConsistenzaSacerList().getTable();
                for (int k = 2; k < tabellaConsistenza.size() + 2; k++) {

                    XWPFTableRow oldRow = table.getRow(k);
                    table.insertNewTableRow(k + 1);
                    XWPFTableRow newRow = table.getRow(k + 1);

                    // Oggetto CELLA
                    XWPFTableCell cell;

                    // Scorro tutte le "celle" vecchie per copiarle con le stesse caratteristiche
                    // in quelle "nuove" della nuova riga
                    for (int i = 0; i < oldRow.getTableCells().size(); i++) {

                        // Creo la cella e la gestisco come CTTc
                        cell = newRow.createCell();

                        // Credo di star aggiungendo proprietà width alla "nuova" cella, prendendole dalla "vecchia"
                        CTTcPr ctTcPr = cell.getCTTc().addNewTcPr();

                        CTTblWidth cellWidth = ctTcPr.addNewTcW();
                        cellWidth.setType(oldRow.getCell(i).getCTTc().getTcPr().getTcW().getType()); // sets type of
                        // width
                        // MAC#31701 - Risoluzione errore riscontrato nella verifica formato di file MPP
                        BigInteger width = (BigInteger) oldRow.getCell(i).getCTTc().getTcPr().getTcW().getW();
                        cellWidth.setW(width); // sets width

                        // Se comincio ad elaborare le colonne "numeriche", i numeri devono essere allineati a destra
                        if (i >= 3) {
                            cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
                        }

                        // Se la vecchia riga era divisa in griglie con formato particolare, copiarla uguale
                        if (oldRow.getCell(i).getCTTc().getTcPr().getGridSpan() != null) {
                            ctTcPr.setGridSpan(oldRow.getCell(i).getCTTc().getTcPr().getGridSpan()); // sets grid span
                            // if any
                        }

                        // Inserisco il singolo totale nella singola cella della singola struttura
                        XWPFRun run = cell.getParagraphs().get(0).createRun();
                        BaseRowInterface rigaConsistenza = tabellaConsistenza.getRow(k - 2);
                        DecimalFormat df = new DecimalFormat("#,###.##");
                        // switch
                        // Devo partire dalla quarta cella (indice parte da 0) per inserire i vari totali
                        switch (i) {
                        case 0:
                            run.setText(rigaConsistenza.getString("nm_ambiente"));
                            break;
                        case 1:
                            run.setText(rigaConsistenza.getString("nm_ente"));
                            break;
                        case 2:
                            run.setText(rigaConsistenza.getString("nm_strut"));
                            break;
                        case 3:

                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_vers")));
                            totA = totA + rigaConsistenza.getBigDecimal("ni_comp_vers").intValue();
                            break;
                        case 4:// aggB
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_annul")));
                            Integer aggB = rigaConsistenza.getBigDecimal("ni_comp_annul") != null
                                    ? rigaConsistenza.getBigDecimal("ni_comp_annul").intValue() : 0;
                            totB = totB + aggB;
                            break;
                        case 5:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico")));
                            Integer aggC = rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico") != null
                                    ? rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico").intValue() : 0;
                            totC = totC + aggC;
                            break;
                        case 6:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn")));
                            Integer aggD = rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn") != null
                                    ? rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn").intValue() : 0;
                            totD = totD + aggD;
                            break;
                        case 7:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_aip_generato")));
                            Integer aggE = rigaConsistenza.getBigDecimal("ni_comp_aip_generato") != null
                                    ? rigaConsistenza.getBigDecimal("ni_comp_aip_generato").intValue() : 0;
                            totE = totE + aggE;
                            break;
                        case 8:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_in_volume")));
                            Integer aggF = rigaConsistenza.getBigDecimal("ni_comp_in_volume") != null
                                    ? rigaConsistenza.getBigDecimal("ni_comp_in_volume").intValue() : 0;
                            totF = totF + aggF;
                            break;
                        case 9:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_delta")));
                            Integer aggG = rigaConsistenza.getBigDecimal("ni_comp_delta") != null
                                    ? rigaConsistenza.getBigDecimal("ni_comp_delta").intValue() : 0;
                            totG = totG + aggG;
                            break;
                        default:
                            break;
                        }
                    }

                    // Coloro le righe con totali diversi da 0
                    String rgb = "FEF5C2";
                    int i = 0;
                    boolean toColor = false;
                    for (XWPFTableCell cellToColor : newRow.getTableCells()) {
                        i++;
                        for (XWPFParagraph paragraph : cellToColor.getParagraphs()) {
                            if (i == 10) {
                                // Cella col totale diverso da 0
                                String testoNumericoDaControllare = paragraph.getText().replace(".", "");
                                if (Integer.parseInt(testoNumericoDaControllare) != 0) {
                                    toColor = true;
                                    differenzeDaZero = true;
                                }
                            }
                        }
                    }

                    if (toColor) {
                        for (XWPFTableCell cellToColor : newRow.getTableCells()) {
                            cellToColor.getCTTc().addNewTcPr().addNewShd().setFill(rgb);
                        }
                    }

                }

                // ULTIMA RIGA: ASSEGNO I TOTALI
                XWPFTableRow totaliRow = table.getRow(table.getNumberOfRows() - 1);
                // Scorro le celle dell'utima riga, quella dei totali. La prima cella con
                // la scritta TOTALE non la considero.
                for (int m = 1; m < 8; m++) {
                    // Prendo la cella in base alla posizione sulla riga
                    XWPFTableCell cell = totaliRow.getCell(m);
                    DecimalFormat df = new DecimalFormat("#,###.##");
                    // Inserisco i totali calcolati in precedenza
                    XWPFRun run = cell.getParagraphs().get(0).createRun();
                    switch (m) {
                    case 1:

                        run.setText(df.format(totA));
                        break;
                    case 2:
                        run.setText(df.format(totB));
                        break;
                    case 3:
                        run.setText(df.format(totC));
                        break;
                    case 4:
                        run.setText(df.format(totD));
                        break;
                    case 5:
                        run.setText(df.format(totE));
                        break;
                    case 6:
                        run.setText(df.format(totF));
                        break;
                    case 7:
                        run.setText(df.format(totG));
                        break;
                    default:
                        break;
                    }
                }

                // CREO E SALVO LA TABELLA PER IL DOCUMENTO FINALE
                doc.createTable();
                doc.setTable(0, table);

                try {
                    getResponse()
                            .setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    getResponse().setHeader("Content-Disposition", "attachment; filename=report.docx");

                    OutputStream out = getServletOutputStream();
                    doc.write(out);
                    out.flush();
                    out.close();

                } catch (EMFError | IOException e) {
                    throw new EMFError("Errore durante la schedulazione del job", e);
                } finally {
                    freeze();
                }

            }

        }
        // forwardToPublisher(Application.Publisher.MONITORAGGIO_CONSISTENZA_SACER_RICERCA);
    }

    public void eseguiScaricaReportSintetico() throws Throwable {
        try (XWPFDocument doc = new XWPFDocument()) {
            // Creo il primo paragrafo
            XWPFParagraph p1Titolo = doc.createParagraph();
            p1Titolo.setAlignment(ParagraphAlignment.LEFT);
            // set font
            XWPFRun r1Titolo = p1Titolo.createRun();
            r1Titolo.setBold(true);
            r1Titolo.setFontSize(14);
            r1Titolo.setFontFamily("Verdana");
            Date dateDa = getForm().getFiltriConsistenzaSacer().getDt_rif_da().parse();
            Date dateA = getForm().getFiltriConsistenzaSacer().getDt_rif_a().parse();
            String stringDateDa = new SimpleDateFormat("dd/MM/yyyy").format(dateDa);
            String stringDateA = new SimpleDateFormat("dd/MM/yyyy").format(dateA);
            r1Titolo.setText("Controllo della consistenza dell’archivio");

            // Filtri utilizzati
            XWPFParagraph p0TitoloFiltri = doc.createParagraph();
            XWPFRun r0TitoloFiltri = p0TitoloFiltri.createRun();
            r0TitoloFiltri.setBold(true);
            r0TitoloFiltri.setFontSize(12);
            r0TitoloFiltri.setFontFamily("Verdana");
            r0TitoloFiltri.setText("Filtri utilizzati: ");

            String[] filtriArray = getForm().getConsistenzaSacerTotaliUdDocComp().getFiltri_utilizzati().getValue()
                    .trim().split(";");

            XWPFParagraph p0Descrizione = doc.createParagraph();
            p0Descrizione.setAlignment(ParagraphAlignment.LEFT);
            p0Descrizione.setIndentFromLeft(1000);

            // Ambiente già settato
            XWPFRun r0Descrizione = p0Descrizione.createRun();
            r0Descrizione.setFontSize(10);
            r0Descrizione.setFontFamily("Verdana");

            XWPFRun r0Valore = p0Descrizione.createRun();
            r0Valore.setFontSize(10);
            r0Valore.setFontFamily("Verdana");
            r0Valore.setBold(true);

            r0Descrizione.setText("Ambiente: ");
            r0Valore.setText("tutti;");
            r0Valore.addBreak();

            for (int i = 1; i < filtriArray.length; i++) {
                String[] filtro = filtriArray[i].split(":");

                // set font
                r0Descrizione = p0Descrizione.createRun();
                r0Descrizione.setFontSize(10);
                r0Descrizione.setFontFamily("Verdana");

                r0Valore = p0Descrizione.createRun();
                r0Valore.setFontSize(10);
                r0Valore.setFontFamily("Verdana");
                r0Valore.setBold(true);

                r0Descrizione.setText(filtro[0].trim() + ": ");
                r0Valore.setText(filtro[1].trim() + ";");
                r0Valore.addBreak();

            }

            XWPFParagraph p1Descrizione = doc.createParagraph();
            p1Descrizione.setAlignment(ParagraphAlignment.LEFT);
            // set font
            XWPFRun r1Descrizione = p1Descrizione.createRun();
            r1Descrizione.setFontSize(10);
            r1Descrizione.setFontFamily("Verdana");

            XWPFRun r1Valore = p1Descrizione.createRun();
            r1Valore.setFontSize(10);
            r1Valore.setFontFamily("Verdana");
            r1Valore.setBold(true);

            // set font
            XWPFRun r1bisDescrizione = p1Descrizione.createRun();
            r1bisDescrizione.setFontSize(10);
            r1bisDescrizione.setFontFamily("Verdana");

            String esito1 = "";
            String esito2 = "";
            // Ricavo l'info se tutte le differenze sono 0
            boolean differenzeDaZero = new BigDecimal(
                    getForm().getConsistenzaSacerTotaliUdDocComp().getTotale_ni_comp_delta().getValue())
                            .compareTo(BigDecimal.ZERO) != 0;
            if (differenzeDaZero) {
                esito1 = "Il controllo della consistenza dell’archivio è stato svolto nell'intervallo di date ";

                esito2 = " e ha avuto esito negativo, essendosi riscontrate differenze tra i componenti versati (al netto dei "
                        + "componenti contenuti nei versamenti annullati) e i componenti elaborati o in corso di elaborazione nel "
                        + "Sistema. "
                        + "Nella tabella che segue è riportato il dettaglio del controllo suddiviso per Struttura versante.";
            } else {
                esito1 = "Il controllo della consistenza dell’archivio è stato svolto nell'intervallo di date ";

                esito2 = " e ha avuto esito positivo, non essendosi riscontrate differenze tra i componenti versati (al netto "
                        + "dei componenti contenuti nei versamenti annullati) e i componenti elaborati o in corso di elaborazione "
                        + "nel Sistema. "
                        + "Nella tabella che segue è riportato il dettaglio del controllo suddiviso per Struttura versante.";
            }
            r1Descrizione.setText(esito1);
            r1Valore.setText(stringDateDa + " - " + stringDateA);
            r1bisDescrizione.setText(esito2);
            r1bisDescrizione.addBreak();

            // Creo il secondo paragrafo, con la LEGENDA
            // Titolo
            XWPFParagraph p2TitoloLegenda = doc.createParagraph();
            XWPFRun r2TitoloLegenda = p2TitoloLegenda.createRun();
            r2TitoloLegenda.setBold(true);
            r2TitoloLegenda.setFontSize(12);
            r2TitoloLegenda.setFontFamily("Verdana");
            r2TitoloLegenda.setText("Legenda");
            r2TitoloLegenda.addBreak();

            // Dal terzo paragrafo, le definizioni della legenda
            XWPFParagraph p3 = doc.createParagraph();
            XWPFRun r3 = p3.createRun();
            r3.setBold(true);
            r3.setFontSize(10);
            r3.setFontFamily("Verdana");
            r3.setText("Ambiente-Ente-Struttura: ");

            XWPFRun r3Descrizione = p3.createRun();
            r3Descrizione.setFontSize(10);
            r3Descrizione.setFontFamily("Verdana");
            r3Descrizione.setText("identificano la singola Struttura versante");
            r3Descrizione.addBreak();
            r3Descrizione.addBreak();

            XWPFRun r4 = p3.createRun();
            r4.setBold(true);
            r4.setFontSize(10);
            r4.setFontFamily("Verdana");
            r4.setText("Numero componenti nei SIP: ");

            XWPFRun r4Descrizione = p3.createRun();
            r4Descrizione.setFontSize(10);
            r4Descrizione.setFontFamily("Verdana");
            r4Descrizione.setText(
                    "numero dei componenti presenti nei SIP di versamento unità documentaria e aggiunta documento. Rappresenta il totale dei componenti ricevuti dal Sistema. ");
            r4Descrizione.addBreak();
            r4Descrizione.addBreak();

            XWPFRun r5 = p3.createRun();
            r5.setBold(true);
            r5.setFontSize(10);
            r5.setFontFamily("Verdana");
            r5.setText("Numero dei componenti in SIP annullati: ");

            XWPFRun r5Descrizione = p3.createRun();
            r5Descrizione.setFontSize(10);
            r5Descrizione.setFontFamily("Verdana");
            r5Descrizione.setText(
                    "numero dei componenti presenti nei SIP di cui è stato annullato il versamento. Vanno sottratti dai componenti presenti nei SIP versati. L’annullamento del versamento comporta la cancellazione logica degli oggetti versati. ");
            r5Descrizione.addBreak();
            r5Descrizione.addBreak();

            XWPFRun r6 = p3.createRun();
            r6.setBold(true);
            r6.setFontSize(10);
            r6.setFontFamily("Verdana");
            r6.setText("Numero componenti presi in carico ancora da elaborare: ");

            XWPFRun r6Descrizione = p3.createRun();
            r6Descrizione.setFontSize(10);
            r6Descrizione.setFontFamily("Verdana");
            r6Descrizione.setText(
                    "numero dei componenti presenti nei SIP di versamento unità documentaria per i quali ancora non è stato generato il relativo AIP (stato di conservazione: PRESO IN CARICO). ");
            r6Descrizione.addBreak();
            r6Descrizione.addBreak();

            XWPFRun r7 = p3.createRun();
            r7.setBold(true);
            r7.setFontSize(10);
            r7.setFontFamily("Verdana");
            r7.setText("Numero componenti negli AIP in aggiornamento: ");

            XWPFRun r7Descrizione = p3.createRun();
            r7Descrizione.setFontSize(10);
            r7Descrizione.setFontFamily("Verdana");
            r7Descrizione.setText(
                    "numero dei componenti presenti nei SIP di aggiunta documento per i quali ancora non è stato aggiornato il relativo AIP	(stato di conservazione: AIP IN AGGIORNAMENTO). ");
            r7Descrizione.addBreak();
            r7Descrizione.addBreak();

            XWPFRun r8 = p3.createRun();
            r8.setBold(true);
            r8.setFontSize(10);
            r8.setFontFamily("Verdana");
            r8.setText("Numero componenti negli AIP: ");

            XWPFRun r8Descrizione = p3.createRun();
            r8Descrizione.setFontSize(10);
            r8Descrizione.setFontFamily("Verdana");
            r8Descrizione.setText(
                    "numero dei componenti presenti negli AIP (stato di conservazione: AIP GENERATO, AIP FIRMATO, IN ARCHIVIO).");
            r8Descrizione.addBreak();
            r8Descrizione.addBreak();

            XWPFRun r9 = p3.createRun();
            r9.setBold(true);
            r9.setFontSize(10);
            r9.setFontFamily("Verdana");
            r9.setText("Numero componenti nei Volumi di conservazione: ");

            XWPFRun r9Descrizione = p3.createRun();
            r9Descrizione.setFontSize(10);
            r9Descrizione.setFontFamily("Verdana");
            r9Descrizione.setText(
                    "numero dei componenti presenti nei Volumi di conservazione (stato di conservazione: IN VOLUME DI CONSERVAZIONE).");
            r9Descrizione.addBreak();
            r9Descrizione.addBreak();

            XWPFRun r10 = p3.createRun();
            r10.setBold(true);
            r10.setFontSize(10);
            r10.setFontFamily("Verdana");
            r10.setText("Differenza tra componenti ricevuti e componenti conservati: ");

            XWPFRun r10Descrizione = p3.createRun();
            r10Descrizione.setFontSize(10);
            r10Descrizione.setFontFamily("Verdana");
            r10Descrizione.setText(
                    "i componenti ricevuti sono calcolati sottraendo i componenti nei versamenti annullati al totale dei componenti versati; i componenti conservati sono calcolati sommando i componenti non ancora elaborati a quelli presenti negli AIP o nei Volumi di conservazione. Un valore è diverso da zero indica una possibile perdita di dati.");

            // Recupero il file del "template" della tabella già formattata in maniera tale
            // da averla già personalizzata
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("report/Tabella_sintetica.docx");
            try (XWPFDocument doc1 = new XWPFDocument(is)) {

                // Cambio orientamento per la tabella passo in LANDSCAPE (orizzontale)
                changeOrientation(doc, "landscape");

                int totA = 0;
                int totB = 0;
                int totC = 0;
                int totD = 0;
                int totE = 0;
                int totF = 0;
                int totG = 0;

                // Ricavo la tabella dal documento "template", in particolare prendo la tabella
                // in posizione 0 (l'unica presente)
                XWPFTable table = doc1.getTableArray(0);
                // MAC #27233 - Fisso i margini del report word
                CTTblLayoutType type = table.getCTTbl().getTblPr().addNewTblLayout();
                type.setType(STTblLayoutType.FIXED);

                AbstractBaseTable<?> tabellaConsistenzaTmp = new BaseTable();

                /* PRIMA DEVO RIELABORARE LA LISTA RISULTATO "RAGGRUPPANDO" PER AMBIENTI */
                Map<String, BigDecimal[]> totaliAmbienti = new HashMap<>();
                for (BaseRowInterface rigaConsistenza : getForm().getConsistenzaSacerList().getTable()) {
                    if (totaliAmbienti.containsKey(rigaConsistenza.getString("nm_ambiente"))) {
                        BigDecimal[] totali = totaliAmbienti.get(rigaConsistenza.getString("nm_ambiente"));
                        totali[0] = totali[0].add(rigaConsistenza.getBigDecimal("ni_comp_vers"));
                        totali[1] = totali[1].add(rigaConsistenza.getBigDecimal("ni_comp_annul"));
                        totali[2] = totali[2].add(rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico"));
                        totali[3] = totali[3].add(rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn"));
                        totali[4] = totali[4].add(rigaConsistenza.getBigDecimal("ni_comp_aip_generato"));
                        totali[5] = totali[5].add(rigaConsistenza.getBigDecimal("ni_comp_in_volume"));
                        totali[6] = totali[6].add(rigaConsistenza.getBigDecimal("ni_comp_delta"));
                    } else {
                        BigDecimal[] totali = new BigDecimal[7];
                        totali[0] = rigaConsistenza.getBigDecimal("ni_comp_vers");
                        totali[1] = rigaConsistenza.getBigDecimal("ni_comp_annul");
                        totali[2] = rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico");
                        totali[3] = rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn");
                        totali[4] = rigaConsistenza.getBigDecimal("ni_comp_aip_generato");
                        totali[5] = rigaConsistenza.getBigDecimal("ni_comp_in_volume");
                        totali[6] = rigaConsistenza.getBigDecimal("ni_comp_delta");
                        totaliAmbienti.put(rigaConsistenza.getString("nm_ambiente"), totali);
                    }
                }

                for (Map.Entry<String, BigDecimal[]> entry : totaliAmbienti.entrySet()) {
                    String chiave = entry.getKey();
                    BigDecimal[] valore = entry.getValue();

                    BaseRowInterface row = new BaseRow();
                    row.setString("nm_ambiente", chiave);
                    row.setBigDecimal("ni_comp_vers", valore[0]);
                    row.setBigDecimal("ni_comp_annul", valore[1]);
                    row.setBigDecimal("ni_comp_presa_in_carico", valore[2]);
                    row.setBigDecimal("ni_comp_aip_in_aggiorn", valore[3]);
                    row.setBigDecimal("ni_comp_aip_generato", valore[4]);
                    row.setBigDecimal("ni_comp_in_volume", valore[5]);
                    row.setBigDecimal("ni_comp_delta", valore[6]);

                    tabellaConsistenzaTmp.add(row);
                }

                tabellaConsistenzaTmp.addSortingRule("nm_ambiente");
                tabellaConsistenzaTmp.sort();

                for (int k = 2; k < tabellaConsistenzaTmp.size() + 2; k++) {

                    XWPFTableRow oldRow = table.getRow(k);
                    table.insertNewTableRow(k + 1);
                    XWPFTableRow newRow = table.getRow(k + 1);

                    // Oggetto CELLA
                    XWPFTableCell cell;

                    // Scorro tutte le "celle" vecchie per copiarle con le stesse caratteristiche
                    // in quelle "nuove" della nuova riga
                    for (int i = 0; i < oldRow.getTableCells().size(); i++) {

                        // Creo la cella e la gestisco come CTTc
                        cell = newRow.createCell();

                        // Credo di star aggiungendo proprietà width alla "nuova" cella, prendendole dalla "vecchia"
                        CTTcPr ctTcPr = cell.getCTTc().addNewTcPr();

                        CTTblWidth cellWidth = ctTcPr.addNewTcW();
                        cellWidth.setType(oldRow.getCell(i).getCTTc().getTcPr().getTcW().getType()); // sets type of
                        // width
                        // MAC#31701 - Risoluzione errore riscontrato nella verifica formato di file MPP
                        BigInteger width = (BigInteger) oldRow.getCell(i).getCTTc().getTcPr().getTcW().getW();
                        cellWidth.setW(width); // sets width

                        // Se comincio ad elaborare le colonne "numeriche", i numeri devono essere allineati a destra
                        if (i >= 1) {
                            cell.getParagraphs().get(0).setAlignment(ParagraphAlignment.RIGHT);
                        }

                        // Se la vecchia riga era divisa in griglie con formato particolare, copiarla uguale
                        if (oldRow.getCell(i).getCTTc().getTcPr().getGridSpan() != null) {
                            ctTcPr.setGridSpan(oldRow.getCell(i).getCTTc().getTcPr().getGridSpan()); // sets grid span
                            // if any
                        }

                        // Inserisco il singolo totale nella singola cella della singola struttura
                        XWPFRun run = cell.getParagraphs().get(0).createRun();
                        BaseRowInterface rigaConsistenza = tabellaConsistenzaTmp.getRow(k - 2);
                        DecimalFormat df = new DecimalFormat("#,###.##");
                        // switch
                        // Devo partire dalla seconda cella (indice parte da 0) per inserire i vari totali
                        switch (i) {
                        case 0:
                            run.setText(rigaConsistenza.getString("nm_ambiente"));
                            break;
                        case 1:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_vers")));
                            totA = totA + rigaConsistenza.getBigDecimal("ni_comp_vers").intValue();
                            break;
                        case 2:// aggB
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_annul")));
                            totB = totB + rigaConsistenza.getBigDecimal("ni_comp_annul").intValue();
                            break;
                        case 3:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico")));
                            totC = totC + rigaConsistenza.getBigDecimal("ni_comp_presa_in_carico").intValue();
                            break;
                        case 4:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn")));
                            totD = totD + rigaConsistenza.getBigDecimal("ni_comp_aip_in_aggiorn").intValue();
                            break;
                        case 5:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_aip_generato")));
                            totE = totE + rigaConsistenza.getBigDecimal("ni_comp_aip_generato").intValue();
                            break;
                        case 6:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_in_volume")));
                            totF = totF + rigaConsistenza.getBigDecimal("ni_comp_in_volume").intValue();
                            break;
                        case 7:
                            run.setText(df.format(rigaConsistenza.getBigDecimal("ni_comp_delta")));
                            totG = totG + rigaConsistenza.getBigDecimal("ni_comp_delta").intValue();
                            break;
                        default:
                            break;
                        }
                    }

                    // Coloro le righe con totali diversi da 0
                    String rgb = "FEF5C2";
                    int i = 0;
                    boolean toColor = false;
                    for (XWPFTableCell cellToColor : newRow.getTableCells()) {
                        i++;
                        for (XWPFParagraph paragraph : cellToColor.getParagraphs()) {
                            if (i == 8) {
                                // Cella col totale diverso da 0
                                String testoPerConfronto = paragraph.getText().replace(".", "");
                                if (Integer.parseInt(testoPerConfronto) != 0) {
                                    toColor = true;
                                    differenzeDaZero = true;
                                }
                            }
                        }
                    }

                    if (toColor) {
                        for (XWPFTableCell cellToColor : newRow.getTableCells()) {
                            cellToColor.getCTTc().addNewTcPr().addNewShd().setFill(rgb);
                        }
                    }

                }

                // ULTIMA RIGA: ASSEGNO I TOTALI
                XWPFTableRow totaliRow = table.getRow(table.getNumberOfRows() - 1);
                // Scorro le celle dell'utima riga, quella dei totali. La prima cella con
                // la scritta TOTALE non la considero.
                for (int m = 1; m < 8; m++) {
                    // Prendo la cella in base alla posizione sulla riga
                    XWPFTableCell cell = totaliRow.getCell(m);
                    DecimalFormat df = new DecimalFormat("#,###.##");
                    // Inserisco i totali calcolati in precedenza
                    XWPFRun run = cell.getParagraphs().get(0).createRun();
                    switch (m) {
                    case 1:
                        run.setText(df.format(totA));
                        break;
                    case 2:
                        run.setText(df.format(totB));
                        break;
                    case 3:
                        run.setText(df.format(totC));
                        break;
                    case 4:
                        run.setText(df.format(totD));
                        break;
                    case 5:
                        run.setText(df.format(totE));
                        break;
                    case 6:
                        run.setText(df.format(totF));
                        break;
                    case 7:
                        run.setText(df.format(totG));
                        break;
                    default:
                        break;
                    }
                }

                // CREO E SALVO LA TABELLA PER IL DOCUMENTO FINALE
                doc.createTable();
                doc.setTable(0, table);

                try {
                    getResponse()
                            .setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    getResponse().setHeader("Content-Disposition", "attachment; filename=reportSintetico.docx");

                    OutputStream out = getServletOutputStream();
                    doc.write(out);
                    out.flush();
                    out.close();

                } catch (EMFError | IOException e) {
                    throw new EMFError("Errore durante la schedulazione del job", e);
                } finally {
                    freeze();
                }

            }

        }
        // forwardToPublisher(Application.Publisher.MONITORAGGIO_CONSISTENZA_SACER_RICERCA);
    }

    private void changeOrientation(XWPFDocument document, String orientation) {
        CTDocument1 doc = document.getDocument();
        CTBody body = doc.getBody();
        CTSectPr section = body.addNewSectPr();

        XWPFParagraph para = document.createParagraph();
        CTP ctp = para.getCTP();
        CTPPr br = ctp.addNewPPr();
        br.setSectPr(section);
        CTPageSz pageSize = section.isSetPgSz() ? section.getPgSz() : section.addNewPgSz();
        if (orientation.equals("landscape")) {
            pageSize.setOrient(STPageOrientation.LANDSCAPE);
            pageSize.setW(BigInteger.valueOf(16840));
            pageSize.setH(BigInteger.valueOf(12240));
        } else {
            pageSize.setOrient(STPageOrientation.PORTRAIT);
            pageSize.setH(BigInteger.valueOf(16840));
            pageSize.setW(BigInteger.valueOf(12240));
        }
    }

    @Override
    public void scaricaReport() throws EMFError {
        try {
            eseguiScaricaReport();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void scaricaReportSintetico() throws EMFError {
        try {
            eseguiScaricaReportSintetico();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    private Timestamp getActivationDateJob(String jobName) {
        Timestamp res = null;
        LogVVisLastSchedRowBean rb = monitoraggioHelper.getLogVVisLastSchedRowBean(jobName);

        if (rb.getFlJobAttivo() != null) {
            if (rb.getFlJobAttivo().equals("1")) {
                res = rb.getDtRegLogJobIni();
            }
        }

        return res;
    }

    @Override
    public void startJobSchedulati() throws EMFError {
        // Eseguo lo start del job interessato
        getForm().getFiltriJobSchedulati().post(getRequest());
        String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
        if (nmJob != null) {
            String dsJob = gestioneJobEjb.getDsJob(nmJob);
            startGestioneJobOperation(nmJob, dsJob);
        } else {
            getMessageBox().addWarning("Attenzione: nessun JOB selezionato");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void stopJobSchedulati() throws EMFError {
        // Eseguo lo start del job interessato
        getForm().getFiltriJobSchedulati().post(getRequest());
        String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
        if (nmJob != null) {
            String dsJob = gestioneJobEjb.getDsJob(nmJob);
            stopGestioneJobOperation(nmJob, dsJob);
        } else {
            getMessageBox().addWarning("Attenzione: nessun JOB selezionato");
            forwardToPublisher(getLastPublisher());
        }
    }

    @Override
    public void esecuzioneSingolaJobSchedulati() throws EMFError {
        // Eseguo lo start del job interessato
        getForm().getFiltriJobSchedulati().post(getRequest());
        String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
        if (nmJob != null) {
            String dsJob = gestioneJobEjb.getDsJob(nmJob);
            esecuzioneSingolaGestioneJobOperation(nmJob, dsJob);
        } else {
            getMessageBox().addWarning("Attenzione: nessun JOB selezionato");
            forwardToPublisher(getLastPublisher());
        }
    }

    public void startGestioneJobOperation(String nmJob, String dsJob) {
        // Se il JOB è di tipo NO_TIMER in ogni caso il tasto di START va inibito
        if (gestioneJobEjb.isNoTimerJob(nmJob)) {
            getMessageBox().addWarning(
                    "Attenzione: si sta tentando di schedulare un JOB di tipo NO_TIMER. Operazione non consentita");
            forwardToPublisher(getLastPublisher());
        } else {
            eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.START);
            setStatoJob(nmJob);
        }
    }

    public void stopGestioneJobOperation(String nmJob, String dsJob) {
        // Se il JOB è di tipo NO_TIMER in ogni caso il tasto di STOP va inibito
        if (gestioneJobEjb.isNoTimerJob(nmJob)) {
            getMessageBox().addWarning(
                    "Attenzione: si sta tentando di stoppare un JOB di tipo NO_TIMER. Operazione non consentita");
            forwardToPublisher(getLastPublisher());
        } else {
            eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.STOP);
            setStatoJob(nmJob);
        }
    }

    public void esecuzioneSingolaGestioneJobOperation(String nmJob, String dsJob) {
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.ESECUZIONE_SINGOLA);
        setStatoJob(nmJob);
    }

    @Override
    public void gestioneJobPage() throws EMFError {
        GestioneJobForm form = new GestioneJobForm();
        form.getGestioneJobRicercaFiltri().setEditMode();
        form.getGestioneJobRicercaFiltri().reset();
        form.getGestioneJobRicercaList().setTable(null);
        BaseTable ambitoTableBean = gestioneJobEjb.getAmbitoJob();
        form.getGestioneJobRicercaFiltri().getNm_ambito()
                .setDecodeMap(DecodeMap.Factory.newInstance(ambitoTableBean, "nm_ambito", "nm_ambito"));
        form.getGestioneJobRicercaFiltri().getTi_stato_job().setDecodeMap(ComboGetter.getMappaTiStatoJob());
        getForm().getFiltriJobSchedulati().post(getRequest());
        String nmJob = getForm().getFiltriJobSchedulati().getNm_job().parse();
        if (nmJob != null) {
            String dsJob = gestioneJobEjb.getDsJob(nmJob);
            form.getGestioneJobRicercaFiltri().getDs_job().setValue(dsJob);
        }
        getSession().setAttribute("fromSchedulazioniJob", true);
        form.getGestioneJobRicercaFiltri().setEditMode();
        redirectToAction(Application.Actions.GESTIONE_JOB, "?operation=ricercaGestioneJob", form);

    }

    public void setJobVBeforeOperation(String nmJob) {
        // Setto i campi di "Stato Job"
        Date proxAttivazione = jbossTimerEjb.getDataProssimaAttivazione(nmJob);

        boolean dataAccurata = jbossTimerEjb.isDataProssimaAttivazioneAccurata(nmJob);
        getForm().getInformazioniJob().getFl_data_accurata().setValue(dataAccurata ? "1" : "0");

        LogVVisLastSchedRowBean rb = monitoraggioHelper.getLogVVisLastSchedRowBean(nmJob);
        String formattata = "";
        DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
        if (proxAttivazione != null) {
            formattata = formato.format(proxAttivazione);
        }
        getForm().getInformazioniJob().getDt_prossima_attivazione().setValue(formattata);
        if (rb.getFlJobAttivo() != null) {
            if (rb.getFlJobAttivo().equals("1")) {
                getForm().getInformazioniJob().getAttivo().setChecked(true);
                formattata = formato.format(rb.getDtRegLogJobIni());
                getForm().getInformazioniJob().getDt_reg_log_job_ini().setValue(formattata);
            } else {
                getForm().getInformazioniJob().getAttivo().setChecked(false);
                getForm().getInformazioniJob().getDt_reg_log_job_ini().setValue(null);
            }
        } else {
            getForm().getInformazioniJob().getAttivo().setChecked(false);
            getForm().getInformazioniJob().getDt_reg_log_job_ini().setValue(null);
        }
    }

    private void setStatoJob(String nmJob) {
        Timestamp dataAttivazioneJob = getActivationDateJob(nmJob);
        StatoJob job = new StatoJob(nmJob, getForm().getInformazioniJob().getFl_data_accurata(),
                getForm().getInformazioniJob().getStartJobSchedulati(),
                getForm().getInformazioniJob().getEsecuzioneSingolaJobSchedulati(),
                getForm().getInformazioniJob().getStopJobSchedulati(),
                getForm().getInformazioniJob().getDt_prossima_attivazione(), getForm().getInformazioniJob().getAttivo(),
                getForm().getInformazioniJob().getDt_reg_log_job_ini(), dataAttivazioneJob);

        gestisciStatoJobNuovo(job);
        forwardToPublisher(Application.Publisher.MONITORAGGIO_JOB_SCHEDULATI_RICERCA);
    }

    private void gestisciStatoJobNuovo(StatoJob statoJob) {
        // se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare nulla
        boolean operazioneInCorso = jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

        statoJob.getFlagDataAccurata().setViewMode();
        statoJob.getFlagDataAccurata().setValue("L'operazione richiesta verrà effettuata entro il prossimo minuto.");
        statoJob.getFlagDataAccurata().setHidden(!operazioneInCorso);

        // Posso operare sulla pagina
        Date nextActivation = jbossTimerEjb.getDataProssimaAttivazione(statoJob.getNomeJob());
        boolean dataAccurata = jbossTimerEjb.isDataProssimaAttivazioneAccurata(statoJob.getNomeJob());
        DateFormat formato = new SimpleDateFormat(WebConstants.DATE_FORMAT_TIMESTAMP_TYPE);

        /*
         * Se il job è già schedulato o in esecuzione singola nascondo il pulsante Start/esecuzione singola, mostro Stop
         * e visualizzo la prossima attivazione. Viceversa se è fermo mostro Start e nascondo Stop
         */
        if (nextActivation != null) {
            statoJob.getStart().setViewMode();
            statoJob.getEsecuzioneSingola().setViewMode();
            statoJob.getStop().setEditMode();
            statoJob.getStart().setHidden(true);
            statoJob.getEsecuzioneSingola().setHidden(true);
            statoJob.getStop().setHidden(false);
            statoJob.getDataProssimaAttivazione().setValue(formato.format(nextActivation));
        } else {
            statoJob.getStart().setEditMode();
            statoJob.getEsecuzioneSingola().setEditMode();
            statoJob.getStop().setViewMode();
            statoJob.getStart().setHidden(false);
            statoJob.getEsecuzioneSingola().setHidden(false);
            statoJob.getStop().setHidden(true);
            statoJob.getDataProssimaAttivazione().setValue(null);
        }

        boolean flagHidden = nextActivation == null || dataAccurata;
        // se la data c'è ma non è accurata non visualizzare la "data prossima attivazione"
        statoJob.getDataProssimaAttivazione().setHidden(!flagHidden);

        if (statoJob.getDataAttivazione() != null) {
            statoJob.getCheckAttivo().setChecked(true);
            statoJob.getDataRegistrazioneJob()
                    .setValue(formato.format(new Date(statoJob.getDataAttivazione().getTime())));
        } else {
            statoJob.getCheckAttivo().setChecked(false);
            statoJob.getDataRegistrazioneJob().setValue(null);
        }

        // Se il JOB è di tipo NO_TIMER in ogni caso il tasto di START va inibito
        if (gestioneJobEjb.isNoTimerJob(statoJob.getNomeJob())) {
            statoJob.getStart().setViewMode();
            statoJob.getStart().setHidden(true);
        }

    }

    private enum OPERAZIONE {
        START("lancio il timer"), ESECUZIONE_SINGOLA("esecuzione singola"), STOP("stop");

        protected String desc;

        OPERAZIONE(String desc) {
            this.desc = desc;
        }

        public String description() {
            return desc;
        }
    }

    private void eseguiNuovo(String nomeJob, String descrizioneJob, String nomeApplicazione, OPERAZIONE operazione) {
        // Messaggio sul logger di sistema
        StringBuilder info = new StringBuilder(descrizioneJob);
        info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
        if (nomeApplicazione != null) {
            info.append("_").append(nomeApplicazione);
        }
        info.append("]");
        log.info(info.toString());

        String message = "Errore durante la schedulazione del job";

        switch (operazione) {
        case START:
            jbossTimerEjb.start(nomeJob, null);
            message = descrizioneJob
                    + ": job correttamente schedulato. L'operazione richiesta verrà schedulata correttamente entro il prossimo minuto.";
            break;
        case ESECUZIONE_SINGOLA:
            jbossTimerEjb.esecuzioneSingola(nomeJob, null);
            message = descrizioneJob
                    + ": job correttamente schedulato per esecuzione singola. L'operazione richiesta verrà effettuata entro il prossimo minuto.";
            break;
        case STOP:
            jbossTimerEjb.stop(nomeJob);
            message = descrizioneJob
                    + ": schedulazione job annullata. L'operazione richiesta diventerà effettiva entro il prossimo minuto.";
            break;
        }

        // Segnalo l'avvenuta operazione sul job
        getMessageBox().addMessage(new Message(MessageLevel.INF, message));
        getMessageBox().setViewMode(ViewMode.plain);
    }

    // <editor-fold defaultstate="collapsed" desc="Classe che mappa lo stato dei job">
    /**
     * Astrazione dei componenti della pagina Schedulazioni Job
     *
     */
    public static final class StatoJob {

        private final String nomeJob;
        private final Input<String> flagDataAccurata;
        private final Button<String> start;
        private final Button<String> esecuzioneSingola;
        private final Button<String> stop;
        private final Input<Timestamp> dataProssimaAttivazione;
        private final CheckBox<String> checkAttivo;
        private final Input<Timestamp> dataRegistrazioneJob;
        private final Timestamp dataAttivazione;

        // Mi serve per evitare una null pointer Exception
        private static final Button<String> NULL_BUTTON = new Button<>(null, "EMPTY_BUTTON", "Pulsante vuoto", null,
                null, null, false, true, true, false);

        public StatoJob(String nomeJob, Input<String> flagDataAccurata, Button<String> start,
                Button<String> esecuzioneSingola, Button<String> stop, Input<Timestamp> dataProssimaAttivazione,
                CheckBox<String> checkAttivo, Input<Timestamp> dataRegistrazioneJob, Timestamp dataAttivazione) {
            this.nomeJob = nomeJob;
            this.flagDataAccurata = flagDataAccurata;
            this.start = start;
            this.esecuzioneSingola = esecuzioneSingola;
            this.stop = stop;
            this.dataProssimaAttivazione = dataProssimaAttivazione;
            this.checkAttivo = checkAttivo;
            this.dataRegistrazioneJob = dataRegistrazioneJob;
            this.dataAttivazione = dataAttivazione;
        }

        public String getNomeJob() {
            return nomeJob;
        }

        public Input<String> getFlagDataAccurata() {
            return flagDataAccurata;
        }

        public Button<String> getStart() {
            if (start == null) {
                return NULL_BUTTON;
            }
            return start;
        }

        public Button<String> getEsecuzioneSingola() {
            return esecuzioneSingola;
        }

        public Button<String> getStop() {
            if (stop == null) {
                return NULL_BUTTON;
            }
            return stop;
        }

        public Input<Timestamp> getDataProssimaAttivazione() {
            return dataProssimaAttivazione;
        }

        public CheckBox<String> getCheckAttivo() {
            return checkAttivo;
        }

        public Input<Timestamp> getDataRegistrazioneJob() {
            return dataRegistrazioneJob;
        }

        public Timestamp getDataAttivazione() {
            return dataAttivazione;
        }
    }
    // </editor-fold>

    @Override
    public void downloadContenuto() {
        MonitoraggioForm.FiltriUdDocDerivantiDaVersFalliti filtri = getForm().getFiltriUdDocDerivantiDaVersFalliti();
        MonitoraggioForm.FiltriUdDocDerivantiDaVersFallitiUlteriori filtriUlteriori = getForm()
                .getFiltriUdDocDerivantiDaVersFallitiUlteriori();
        // Esegue la post dei filtri compilati
        filtri.post(getRequest());
        // Recupero i filtri dalla sessione e setto i filtri con il nuovo valore
        MonitoraggioFiltriListaVersFallitiDistintiDocBean filtriListaVersFallitiDistintiDoc = (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                .getAttribute("filtriListaVersFallitiDistintiDoc");
        try {
            filtriListaVersFallitiDistintiDoc.setIdAmbiente(filtri.getId_ambiente().parse());

            filtriListaVersFallitiDistintiDoc.setIdEnte(filtri.getId_ente().parse());
            filtriListaVersFallitiDistintiDoc.setIdStrut(filtri.getId_strut().parse());
            filtriListaVersFallitiDistintiDoc.setTipoLista(filtri.getTipo_lista().parse());
            filtriListaVersFallitiDistintiDoc.setFlVerificato(filtri.getFl_verif().parse());
            filtriListaVersFallitiDistintiDoc.setFlNonRisolub(filtri.getFl_non_risolub().parse());
            filtriListaVersFallitiDistintiDoc.setClasseErrore(filtri.getClasse_errore().parse());
            filtriListaVersFallitiDistintiDoc.setSottoClasseErrore(filtri.getSottoclasse_errore().parse());
            filtriListaVersFallitiDistintiDoc.setCodiceErrore(filtri.getCodice_errore().parse());

            filtriListaVersFallitiDistintiDoc
                    .setRegistro(filtriUlteriori.getCd_registro_key_unita_doc_ult().getDecodedValues());
            filtriListaVersFallitiDistintiDoc.setAnno(filtriUlteriori.getAa_key_unita_doc_ult().parse());
            filtriListaVersFallitiDistintiDoc.setNumero(filtriUlteriori.getCd_key_unita_doc_ult().parse());
            filtriListaVersFallitiDistintiDoc.setAnno_range_da(filtriUlteriori.getAa_key_unita_doc_da_ult().parse());
            filtriListaVersFallitiDistintiDoc.setNumero_range_da(filtriUlteriori.getCd_key_unita_doc_da_ult().parse());
            filtriListaVersFallitiDistintiDoc.setAnno_range_a(filtriUlteriori.getAa_key_unita_doc_a_ult().parse());
            filtriListaVersFallitiDistintiDoc.setNumero_range_a(filtriUlteriori.getCd_key_unita_doc_a_ult().parse());
        } catch (EMFError ex) {
            getMessageBox().addError("Errore inatteso nella preparazione del download riguardante i filtri di ricerca");
        }
        // Li risetto perch\u00e0¨ se torno indietro in "Riepilogo Versamenti" devo avere impostata la ricerca con
        // questi ultimi
        getSession().setAttribute("filtriListaVersFallitiDistintiDoc", filtriListaVersFallitiDistintiDoc);

        if (filtri.validate(getMessageBox())) {

            // Effettuo la ricerca per la visualizzazione oppure per il download dei contenuti
            // Setto la lista dei documenti non versati
            if (filtriListaVersFallitiDistintiDoc.getTipoLista().equals("UNITA_DOC")) {
                MonVLisUdNonVersIamTableBean tb = monitoraggioHelper.getMonVLisUdNonVersIamViewBeanScaricaContenuto(
                        (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                .getAttribute("filtriListaVersFallitiDistintiDoc"),
                        null);
                getForm().getDocumentiDerivantiDaVersFallitiList().setTable(tb);
            } else {
                MonVLisDocNonVersIamTableBean tb = monitoraggioHelper.getMonVLisDocNonVersIamViewBeanScaricaContenuto(
                        (MonitoraggioFiltriListaVersFallitiDistintiDocBean) getSession()
                                .getAttribute("filtriListaVersFallitiDistintiDoc"),
                        null);
                getForm().getDocumentiDerivantiDaVersFallitiList().setTable(tb);
            }

            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
            if (!getMessageBox().hasError()) {
                File tmpFile = new File(System.getProperty("java.io.tmpdir"),
                        "lista_versamenti_" + df.format(new Date()) + ".csv");
                try {
                    ActionUtils.buildCsvString(getForm().getDocumentiDerivantiDaVersFallitiList(),
                            /* (BaseTableInterface<? extends BaseRowInterface>) */ getForm()
                                    .getDocumentiDerivantiDaVersFallitiList().getTable(),
                            MonVLisUdNonVersIamTableBean.TABLE_DESCRIPTOR, tmpFile);
                    getRequest().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_ACTION.name(), getControllerName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILENAME.name(), tmpFile.getName());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_FILEPATH.name(), tmpFile.getPath());
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_DELETEFILE.name(),
                            Boolean.toString(true));
                    getSession().setAttribute(WebConstants.DOWNLOAD_ATTRS.DOWNLOAD_CONTENTTYPE.name(), "text/csv");
                } catch (IOException ex) {
                    getMessageBox().addError("Errore inatteso nella preparazione del download");
                }
            }

        }

        if (getMessageBox().hasError()) {
            forwardToPublisher(getLastPublisher());
        } else {
            forwardToPublisher(Application.Publisher.DOWNLOAD_PAGE);
        }

    }

}
