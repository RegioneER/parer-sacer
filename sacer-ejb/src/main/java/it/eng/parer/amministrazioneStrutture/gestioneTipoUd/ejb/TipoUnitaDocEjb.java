package it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper.DatiSpecificiHelper;
import it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper.RegistroHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.AmbienteEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper.TipoUnitaDocHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplSistemaVersante;
import it.eng.parer.entity.AplValoreParamApplic;
import it.eng.parer.entity.DecCategTipoUnitaDoc;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoDocAmmesso;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoStrutUdReg;
import it.eng.parer.entity.DecTipoStrutUdSisVer;
import it.eng.parer.entity.DecTipoStrutUdXsd;
import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDocAmmesso;
import it.eng.parer.entity.DecUsoModelloXsdUniDoc;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgRegolaValSubStrut;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgTipoServizio;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.serie.helper.ModelliSerieHelper;
import it.eng.parer.serie.helper.TipoSerieHelper;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.AplSistemaVersanteRowBean;
import it.eng.parer.slite.gen.tablebean.AplSistemaVersanteTableBean;
import it.eng.parer.slite.gen.tablebean.DecCategTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecCategTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoDocAmmessoTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUdSisVersTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoStrutUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocAmmessoRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.OrgTipoServizioTableBean;
import it.eng.parer.slite.gen.viewbean.DecVCalcTiServOnTipoUdRowBean;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsRowBean;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsTableBean;
import it.eng.parer.viewEntity.DecVCalcTiServOnTipoUd;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoUd;
import it.eng.parer.viewEntity.DecVLisTiUniDocAms;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei tipi di unità documentaria
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneTipoUd}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class TipoUnitaDocEjb {

    private static final Logger logger = LoggerFactory.getLogger(TipoUnitaDocEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private StruttureEjb struttureEjb;
    // FIXME: Eliminare appena corretto il metodo saveDecTipoUnitaDoc
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private CriteriRaggruppamentoEjb critEjb;
    @EJB
    private CriteriRaggrHelper crHelper;
    @EJB
    private TipoUnitaDocHelper helper;
    @EJB
    private TipoSerieEjb tipoSerieEjb;
    @EJB
    private TipoSerieHelper tipoSerieHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB
    private AmbienteEjb ambienteEjb;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private DatiSpecificiHelper datiSpecHelper;
    @EJB
    private UnitaDocumentarieHelper unitaDocHelper;
    @EJB
    private ModelliSerieHelper modelliSerieHelper;
    @EJB
    private RegistroHelper registroHelper;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;

    /**
     * Verifica se almeno un registro associato al tipo ud ha “fl_crea_tipo_serie_standard” = SI
     *
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * 
     * @return true se esiste un registro con flCreaTipoSerieStandard a TRUE
     */
    public boolean checkTipoSerieStandardForRegistriAmmessi(BigDecimal idTipoUnitaDoc) {
        boolean result = false;
        DecTipoUnitaDoc tipoUd = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        for (DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso : tipoUd.getDecTipoUnitaDocAmmessos()) {
            DecRegistroUnitaDoc registro = tipoUnitaDocAmmesso.getDecRegistroUnitaDoc();
            if (registro.getFlCreaTipoSerieStandard() != null && registro.getFlCreaTipoSerieStandard().equals("1")) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Metodo di salvataggio e replica su IAM di un nuovo tipo di unit\u00E0 documentaria
     *
     * @param param
     *            parametri per il logging
     * @param tipoUnitaDocRowBean
     *            bean contenente i dati del tipo ud
     * @param criterioAutomTipoUd
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void insertDecTipoUnitaDoc(LogParam param, DecTipoUnitaDocRowBean tipoUnitaDocRowBean,
            String criterioAutomTipoUd) throws ParerUserError {
        TipoUnitaDocEjb me = context.getBusinessObject(TipoUnitaDocEjb.class);
        IamOrganizDaReplic replic = me.saveDecTipoUnitaDoc(param, null, tipoUnitaDocRowBean, StruttureEjb.TipoOper.INS,
                criterioAutomTipoUd);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    /**
     * Metodo di modifica e replica su IAM di un nuovo tipo di unit\u00E0 documentaria
     *
     * @param param
     *            parametri per il logging
     * @param idTipoUnita
     *            id del tipo di ud da modificare
     * @param tipoUnitaDocRowBean
     *            bean contenente i dati del tipo ud
     * @param criterioAutomTipoUd
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void updateDecTipoUnitaDoc(LogParam param, BigDecimal idTipoUnita,
            DecTipoUnitaDocRowBean tipoUnitaDocRowBean, String criterioAutomTipoUd) throws ParerUserError {
        TipoUnitaDocEjb me = context.getBusinessObject(TipoUnitaDocEjb.class);
        IamOrganizDaReplic replic = me.saveDecTipoUnitaDoc(param, idTipoUnita, tipoUnitaDocRowBean,
                StruttureEjb.TipoOper.MOD, criterioAutomTipoUd);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    /**
     * Metodo che esegue il salvataggio della tipologia di unità documentaria su database dati i parametri
     *
     * @param param
     *            bean con i dati per il logging
     * @param idTipoUnitaDoc
     *            id del tipo ud da modificare (se != null)
     * @param tipoUnitaDocRowBean
     *            bean contenente i dati del tipo ud
     * @param tipoOper
     *            tipo di operazione eseguita
     * @param criterioAutomTipoUd
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * 
     * @return l'entity IamOrganizDaReplic con cui eseguire la replica su IAM
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveDecTipoUnitaDoc(LogParam param, BigDecimal idTipoUnitaDoc,
            DecTipoUnitaDocRowBean tipoUnitaDocRowBean, StruttureEjb.TipoOper tipoOper, String criterioAutomTipoUd)
            throws ParerUserError {
        OrgStrut struttura = helper.findById(OrgStrut.class, tipoUnitaDocRowBean.getIdStrut());
        DecTipoUnitaDoc tipoUnitaDoc = null;
        boolean modificatiNomeDescrizione = false;

        /* INSERIMENTO TIPO UNITA' DOCUMENTARIA */
        if (tipoOper.name().equals((StruttureEjb.TipoOper.INS.name()))) {

            if (struttura.getDecTipoUnitaDocs() == null) {
                struttura.setDecTipoUnitaDocs(new ArrayList<DecTipoUnitaDoc>());
            }

            if (helper.getDecTipoUnitaDocByName((tipoUnitaDocRowBean.getNmTipoUnitaDoc()),
                    tipoUnitaDocRowBean.getIdStrut()) != null) {
                throw new ParerUserError(
                        "Tipo Unit\u00E0 Documentaria gi\u00E0 esistente all'interno della struttura</br>");
            }

            BigDecimal idModelloTipoSerie = tipoUnitaDocRowBean.getIdModelloTipoSerie();

            tipoUnitaDoc = (DecTipoUnitaDoc) Transform.rowBean2Entity(tipoUnitaDocRowBean);
            tipoUnitaDoc.setOrgStrut(struttura);
            if (idModelloTipoSerie != null) {
                DecModelloTipoSerie modelloTipoSerieNew = helper.findById(DecModelloTipoSerie.class,
                        idModelloTipoSerie);
                tipoUnitaDoc.setDecModelloTipoSerie(modelloTipoSerieNew);
            }

            if (tipoUnitaDoc.getOrgRegolaValSubStruts() == null) {
                tipoUnitaDoc.setOrgRegolaValSubStruts(new ArrayList<OrgRegolaValSubStrut>());
            }
            helper.insertEntity(tipoUnitaDoc, true);
            struttura.getDecTipoUnitaDocs().add(tipoUnitaDoc);

            // Calcolo servizi erogati
            boolean esitoOK = struttureEjb.calcoloServiziErogati(struttura.getIdEnteConvenz());
            if (!esitoOK) {
                throw new ParerUserError(
                        "Errore durante il calcolo dei servizi erogati a seguito di inserimento nuova tipo ud</br>");
            }

            // // Gestione parametri
            // for (AplParamApplicRowBean paramApplicRowBean :
            // parametriAmministrazioneTipoUd) {
            // if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_amm") !=
            // null &&
            // !paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_amm").equals(""))
            // {
            // amministrazioneEjb.insertAplValoreParamApplic(null, null, tipoUnitaDoc, null,
            // paramApplicRowBean.getIdParamApplic(), "TIPO_UNITA_DOC",
            // paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_amm"));
            // }
            // }
            // for (AplParamApplicRowBean paramApplicRowBean : parametriConservazioneTipoUd)
            // {
            // if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_cons") !=
            // null &&
            // !paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_cons").equals(""))
            // {
            // amministrazioneEjb.insertAplValoreParamApplic(null, null, tipoUnitaDoc, null,
            // paramApplicRowBean.getIdParamApplic(), "TIPO_UNITA_DOC",
            // paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_cons"));
            // }
            // }
            // for (AplParamApplicRowBean paramApplicRowBean : parametriGestioneTipoUd) {
            // if (paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_gest") !=
            // null &&
            // !paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_gest").equals(""))
            // {
            // amministrazioneEjb.insertAplValoreParamApplic(null, null, tipoUnitaDoc, null,
            // paramApplicRowBean.getIdParamApplic(), "TIPO_UNITA_DOC",
            // paramApplicRowBean.getString("ds_valore_param_applic_tipo_ud_gest"));
            // }
            // }
            modificatiNomeDescrizione = true;
        } else if (tipoOper.name().equals((StruttureEjb.TipoOper.MOD.name()))) {
            /* MODIFICA TIPO UNITA' DOCUMENTARIA */
            DecTipoUnitaDoc dbTipoUnitaDoc = helper.getDecTipoUnitaDocByName(tipoUnitaDocRowBean.getNmTipoUnitaDoc(),
                    tipoUnitaDocRowBean.getIdStrut());
            if (dbTipoUnitaDoc != null && dbTipoUnitaDoc.getIdTipoUnitaDoc() != idTipoUnitaDoc.longValue()) {
                throw new ParerUserError(
                        "Nome Tipo Unit\u00E0 Documentaria gi\u00E0 associato a questa struttura all'interno del database</br>");
            }

            /* Controllo se sono stati modificati nome e/o descrizione */
            tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
            if (!tipoUnitaDoc.getNmTipoUnitaDoc().equals(tipoUnitaDocRowBean.getNmTipoUnitaDoc())
                    || !tipoUnitaDoc.getDsTipoUnitaDoc().equals(tipoUnitaDocRowBean.getDsTipoUnitaDoc())) {
                modificatiNomeDescrizione = true;
            }

            BigDecimal idModelloTipoSerie = tipoUnitaDocRowBean.getIdModelloTipoSerie();
            if (idModelloTipoSerie != null) {
                DecModelloTipoSerie modelloTipoSerieNew = helper.findById(DecModelloTipoSerie.class,
                        idModelloTipoSerie);
                if (tipoUnitaDoc.getDecModelloTipoSerie() != null) {
                    if (tipoUnitaDoc.getDecModelloTipoSerie().getIdModelloTipoSerie() != idModelloTipoSerie
                            .longValue()) {

                        DecModelloTipoSerie modelloTipoSerieOld = tipoUnitaDoc.getDecModelloTipoSerie();
                        List<DecTipoSerie> tipiSerieVecchioModello = tipoSerieHelper.retrieveDecTipoSerieList(
                                tipoUnitaDoc.getOrgStrut().getIdStrut(), modelloTipoSerieOld.getIdModelloTipoSerie());

                        boolean presenzaTiCreaStandard = false;
                        // Se il modello che si intende dismettere NON è collegato a tipologie di serie
                        // attive che hanno
                        // il flag "ti_crea_standard" = "BASATA_SU_TIPO_UNITA_DOC" la modifica è
                        // consentita
                        for (DecTipoSerie tipoSerie : tipiSerieVecchioModello) {
                            if (tipoSerie.getTiCreaStandard()
                                    .equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_TIPO_UNITA_DOC.name())) {
                                presenzaTiCreaStandard = true;
                                break;
                            }
                        }
                        if (presenzaTiCreaStandard) {
                            throw new ParerUserError(
                                    "Il modello associato al tipo ud \u00E8 associato a tipi di serie basate sul tipo ud; modifica del modello non consentita");
                        }
                    }
                }
                tipoUnitaDoc.setDecModelloTipoSerie(modelloTipoSerieNew);
            }

            // Se il modello è stato tolto
            if (tipoUnitaDoc.getDecModelloTipoSerie() != null && idModelloTipoSerie == null) {
                tipoUnitaDoc.setDecModelloTipoSerie(null);
            }

            // Se ho modificato il Tipo Servizio, allora controllo se la struttura è
            // riferita ad un ente convenzionato
            // String nmApplic = configurationHelper.getValoreParamApplic("NM_APPLIC");
            String nmApplic = configurationHelper.getValoreParamApplic("NM_APPLIC", null, null, null, null,
                    CostantiDB.TipoAplVGetValAppart.APPLIC);
            String nmEnteConvenz = ambienteEjb.getNmEnteConvenz(nmApplic, "STRUTTURA",
                    tipoUnitaDocRowBean.getIdStrut());

            // Controllo se ho modificato il Tipo Servizio
            if (tipoUnitaDoc.getOrgTipoServizio() != null) {
                String idTipoServizioDB = "" + tipoUnitaDoc.getOrgTipoServizio().getIdTipoServizio();
                String idTipoServizioOnline = "" + tipoUnitaDocRowBean.getIdTipoServizio();
                if (modificati(idTipoServizioDB, idTipoServizioOnline)) {
                    if (nmEnteConvenz != null) {
                        // Controllo se sono presenti servizi erogati
                        if (helper.existsServiziErogatiByStrutAndTipoServizio(
                                tipoUnitaDoc.getOrgTipoServizio().getIdTipoServizio(),
                                tipoUnitaDocRowBean.getIdStrut())) {
                            throw new ParerUserError(
                                    "Esiste almeno un servizio erogato riferito al tipo di servizio: impossibile eseguire il cambiamento del tipo di servizio");
                        }
                    }
                }
            }

            Long idTipoServAttivTipoUdDB = null;
            Long idTipoServConservTipoUdDB = null;
            BigDecimal idTipoServAttivTipoUdRowBean = null;
            BigDecimal idTipoServConservTipoUdRowBean = null;
            if (tipoUnitaDoc.getOrgTipoServAttivTipoUd() == null) {
                idTipoServAttivTipoUdDB = 0L;
            } else {
                idTipoServAttivTipoUdDB = tipoUnitaDoc.getOrgTipoServAttivTipoUd().getIdTipoServizio();
            }
            if (tipoUnitaDoc.getOrgTipoServConservTipoUd() == null) {
                idTipoServConservTipoUdDB = 0L;
            } else {
                idTipoServConservTipoUdDB = tipoUnitaDoc.getOrgTipoServConservTipoUd().getIdTipoServizio();
            }
            if (tipoUnitaDocRowBean.getIdTipoServAttivTipoUd() == null) {
                idTipoServAttivTipoUdRowBean = BigDecimal.ZERO;
            } else {
                idTipoServAttivTipoUdRowBean = tipoUnitaDocRowBean.getIdTipoServAttivTipoUd();
            }
            if (tipoUnitaDocRowBean.getIdTipoServConservTipoUd() == null) {
                idTipoServConservTipoUdRowBean = BigDecimal.ZERO;
            } else {
                idTipoServConservTipoUdRowBean = tipoUnitaDocRowBean.getIdTipoServConservTipoUd();
            }

            boolean ricalcolaServiziErogati = idTipoServAttivTipoUdDB != idTipoServAttivTipoUdRowBean.intValue()
                    || idTipoServConservTipoUdDB != idTipoServConservTipoUdRowBean.intValue();

            tipoUnitaDoc.setNmTipoUnitaDoc(tipoUnitaDocRowBean.getNmTipoUnitaDoc());
            tipoUnitaDoc.setDsTipoUnitaDoc(tipoUnitaDocRowBean.getDsTipoUnitaDoc());
            tipoUnitaDoc.setDlNoteTipoUd(tipoUnitaDocRowBean.getDlNoteTipoUd());
            // tipoUnitaDoc.setFlForzaCollegamento(tipoUnitaDocRowBean.getFlForzaCollegamento());
            tipoUnitaDoc.setDtIstituz(tipoUnitaDocRowBean.getDtIstituz());
            tipoUnitaDoc.setDtSoppres(tipoUnitaDocRowBean.getDtSoppres());
            tipoUnitaDoc.setTiSaveFile(tipoUnitaDocRowBean.getTiSaveFile());
            DecCategTipoUnitaDoc categTipoUnitaDoc = helper.findById(DecCategTipoUnitaDoc.class,
                    tipoUnitaDocRowBean.getIdCategTipoUnitaDoc());
            tipoUnitaDoc.setDecCategTipoUnitaDoc(categTipoUnitaDoc);
            // Aggiungo i nuovi campi sulla Configurazione Serie
            tipoUnitaDoc.setFlCreaTipoSerieStandard(tipoUnitaDocRowBean.getFlCreaTipoSerieStandard());
            tipoUnitaDoc.setNmTipoSerieDaCreare(tipoUnitaDocRowBean.getNmTipoSerieDaCreare());
            tipoUnitaDoc.setDsTipoSerieDaCreare(tipoUnitaDocRowBean.getDsTipoSerieDaCreare());
            tipoUnitaDoc.setCdSerieDaCreare(tipoUnitaDocRowBean.getCdSerieDaCreare());
            tipoUnitaDoc.setDsSerieDaCreare(tipoUnitaDocRowBean.getDsSerieDaCreare());

            if (tipoUnitaDocRowBean.getIdTipoServizio() != null) {
                OrgTipoServizio tipoServizio = helper.findById(OrgTipoServizio.class,
                        tipoUnitaDocRowBean.getIdTipoServizio());
                tipoUnitaDoc.setOrgTipoServizio(tipoServizio);
            } else {
                tipoUnitaDoc.setOrgTipoServizio(null);
            }
            if (tipoUnitaDocRowBean.getIdTipoServizioAttiv() != null) {
                OrgTipoServizio tipoServizioAttiv = helper.findById(OrgTipoServizio.class,
                        tipoUnitaDocRowBean.getIdTipoServizioAttiv());
                tipoUnitaDoc.setOrgTipoServizioAttiv(tipoServizioAttiv);
            } else {
                tipoUnitaDoc.setOrgTipoServizioAttiv(null);
            }
            if (tipoUnitaDocRowBean.getIdTipoServConservTipoUd() != null) {
                OrgTipoServizio tipoServConservTipoUd = helper.findById(OrgTipoServizio.class,
                        tipoUnitaDocRowBean.getIdTipoServConservTipoUd());
                tipoUnitaDoc.setOrgTipoServConservTipoUd(tipoServConservTipoUd);
            } else {
                tipoUnitaDoc.setOrgTipoServConservTipoUd(null);
            }
            if (tipoUnitaDocRowBean.getIdTipoServAttivTipoUd() != null) {
                OrgTipoServizio tipoServAttivTipoUd = helper.findById(OrgTipoServizio.class,
                        tipoUnitaDocRowBean.getIdTipoServAttivTipoUd());
                tipoUnitaDoc.setOrgTipoServAttivTipoUd(tipoServAttivTipoUd);
            } else {
                tipoUnitaDoc.setOrgTipoServAttivTipoUd(null);
            }
            helper.getEntityManager().flush();

            if (ricalcolaServiziErogati) {
                boolean esitoOK = struttureEjb.calcoloServiziErogati(struttura.getIdEnteConvenz());
                if (!esitoOK) {
                    throw new ParerUserError(
                            "Errore durante il calcolo dei servizi erogati a seguito di modifica tipo ud</br>");
                }
            }
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                new BigDecimal(tipoUnitaDoc.getIdTipoUnitaDoc()), param.getNomePagina());
        // Se il flag relativo al criterio di raggruppamento \u00E8 stato impostato,
        // procedo alla creazione automatica
        if (criterioAutomTipoUd != null && criterioAutomTipoUd.equals("1")) {
            DecVCreaCritRaggrTipoUd creaCritTipoUD = crHelper
                    .getDecVCreaCritRaggrTipoUd(tipoUnitaDoc.getIdTipoUnitaDoc());
            if (crHelper.existNomeCriterio(creaCritTipoUD.getNmCriterioRaggr(),
                    new BigDecimal(struttura.getIdStrut()))) {
                throw new ParerUserError(
                        "Attenzione: non \u00E8 possibile terminare l'operazione in quanto si richiedere la creazione "
                                + "in automatico di un criterio di raggruppamento il cui nome, per questa struttura, \u00E8 gi\u00E0 presente nel DB");
            }
            DecCriterioRaggr criterioSalvato = critEjb
                    .salvataggioAutomaticoCriterioRaggrStdNoAutomTipoUd(creaCritTipoUD);
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                    new BigDecimal(criterioSalvato.getIdCriterioRaggr()), param.getNomePagina());
        }

        /*
         * La replica va fatta solo se la tipologia unit\u00E0 documentaria appartiene ad una struttura non appartenente
         * ad un ente di tipo template e se ho modificato nome e/o descrizione
         */
        IamOrganizDaReplic replic = null;
        if (modificatiNomeDescrizione && (struttura.getOrgEnte().getTipoDefTemplateEnte()
                .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
                || struttura.getOrgEnte().getTipoDefTemplateEnte()
                        .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))) {
            replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, ApplEnum.TiOperReplic.MOD);
        }
        return replic;
    }

    public void saveParametriTipoUd(LogParam param, AplParamApplicTableBean parametriAmministrazioneTipoUd,
            AplParamApplicTableBean parametriConservazioneTipoUd, AplParamApplicTableBean parametriGestioneTipoUd,
            BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        gestioneParametriTipoUd(parametriAmministrazioneTipoUd, parametriConservazioneTipoUd, parametriGestioneTipoUd,
                tipoUnitaDoc);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                new BigDecimal(tipoUnitaDoc.getIdTipoUnitaDoc()), param.getNomePagina());
    }

    private void gestioneParametriTipoUd(AplParamApplicTableBean parametriAmministrazioneTipoUd,
            AplParamApplicTableBean parametriConservazioneTipoUd, AplParamApplicTableBean parametriGestioneTipoUd,
            DecTipoUnitaDoc tipoUnitaDoc) {
        // Gestione parametri amministrazione
        manageParametriPerTipoUd(parametriAmministrazioneTipoUd, "ds_valore_param_applic_tipo_ud_amm", tipoUnitaDoc);
        // Gestione parametri conservazione
        manageParametriPerTipoUd(parametriConservazioneTipoUd, "ds_valore_param_applic_tipo_ud_cons", tipoUnitaDoc);
        // Gestione parametri gestione
        manageParametriPerTipoUd(parametriGestioneTipoUd, "ds_valore_param_applic_tipo_ud_gest", tipoUnitaDoc);
    }

    private boolean modificati(String s1, String s2) {
        return !StringUtils.equals(s1, s2);
    }

    private void manageParametriPerTipoUd(AplParamApplicTableBean paramApplicTableBean,
            String nomeCampoValoreParamApplic, DecTipoUnitaDoc tipoUnitaDoc) {
        for (AplParamApplicRowBean paramApplicRowBean : paramApplicTableBean) {
            // Cancello il parametro se eliminato
            if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && (paramApplicRowBean.getString(nomeCampoValoreParamApplic) == null
                            || paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals(""))) {
                AplValoreParamApplic parametro = helper.findById(AplValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                helper.removeEntity(parametro, true);
            } // Modifico il parametro se modificato
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") != null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                AplValoreParamApplic parametro = helper.findById(AplValoreParamApplic.class,
                        paramApplicRowBean.getBigDecimal("id_valore_param_applic"));
                parametro.setDsValoreParamApplic(paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            } // Inserisco il parametro se nuovo
            else if (paramApplicRowBean.getBigDecimal("id_valore_param_applic") == null
                    && paramApplicRowBean.getString(nomeCampoValoreParamApplic) != null
                    && !paramApplicRowBean.getString(nomeCampoValoreParamApplic).equals("")) {
                amministrazioneEjb.insertAplValoreParamApplic(null, null, tipoUnitaDoc, null,
                        paramApplicRowBean.getBigDecimal("id_param_applic"), "TIPO_UNITA_DOC",
                        paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    /**
     * Metodo di salvataggio di una nuova associazione tra registri e tipi di unit\u00E0 documentarie
     *
     * @param param
     *            parametri per logging
     * @param azioneInsCriterio
     *            inserimento criterio
     * @param azioneModCriterio
     *            modifica criterio
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * 
     * @throws ParerUserError
     *             errore generico
     * @throws ParerWarningException
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoUnitaDocAmmesso(LogParam param, String azioneInsCriterio, String azioneModCriterio,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) throws ParerUserError, ParerWarningException {

        DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso = new DecTipoUnitaDocAmmesso();
        DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        if (tipoUnitaDoc.getDecTipoUnitaDocAmmessos() == null) {
            tipoUnitaDoc.setDecTipoUnitaDocAmmessos(new ArrayList<DecTipoUnitaDocAmmesso>());
        }
        DecRegistroUnitaDoc registroUnitaDoc = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        if (registroUnitaDoc.getDecTipoUnitaDocAmmessos() == null) {
            registroUnitaDoc.setDecTipoUnitaDocAmmessos(new ArrayList<DecTipoUnitaDocAmmesso>());
        }
        tipoSerieEjb.checkTipoSerieStandardDaRegistroOTipoUd(registroUnitaDoc, tipoUnitaDoc);

        tipoUnitaDocAmmesso.setDecRegistroUnitaDoc(registroUnitaDoc);
        tipoUnitaDocAmmesso.setDecTipoUnitaDoc(tipoUnitaDoc);
        tipoUnitaDoc.getDecTipoUnitaDocAmmessos().add(tipoUnitaDocAmmesso);
        registroUnitaDoc.getDecTipoUnitaDocAmmessos().add(tipoUnitaDocAmmesso);
        helper.insertEntity(tipoUnitaDocAmmesso, false);
        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                param.getNomePagina());

        /*
         * Dopo il salvataggio dell'associazione, verifico la coerenza i criteri di raggruppamento
         */
        String warningMessage = null;
        // Se il registro è fiscale...
        if (registroUnitaDoc.getFlRegistroFisc().equals("1")) {
            boolean areAllRegistriFiscali = critEjb.checkAllRegistriAssociatiFiscali(idTipoUnitaDoc);
            // ...verifico se esiste un criterio valido che usa il tipo ud dell'associazione
            // Se NON esiste
            if (!critEjb.existsCriterioPerTipoDato(idTipoUnitaDoc.longValue(), TipoDato.TIPO_UNITA_DOC)) {
                // ...ed il tipo unita doc è associato solo a registri fiscale
                if (areAllRegistriFiscali) {
                    // Verifico se esiste un criterio valido che usa il registro dell'associazione
                    // Se NON esiste
                    if (!critEjb.existsCriterioPerTipoDato(idRegistroUnitaDoc.longValue(), TipoDato.REGISTRO)) {
                        // NOTA: verrà settato già il flag fiscale in quanto la vista adibita
                        // DEC_V_CREA_CRIT_RAGGR_TIPO_UD agisce sull'associazione, già creata pocanzi
                        param.setNomeAzione(azioneInsCriterio);
                        creaCriterioSulTipoUnitaDoc(param, idTipoUnitaDoc);
                        warningMessage = "Poiché per il tipo di unità documentaria associata al registro non è stato rilevato alcun criterio e neanche per il registro è stato rilevato alcun criterio, ed il registro è fiscale, si è provveduto a creare un criterio standard e fiscale per il tipo unità documentaria.";
                    }
                }
            } // Se esiste
            else {
                // Determino l'eventuale criterio standard e non fiscale
                List<DecCriterioRaggr> criteriEsistenti = crHelper.getCriteriPerTipoDato(idTipoUnitaDoc.longValue(),
                        TipoDato.TIPO_UNITA_DOC, "1", "0");
                if (!criteriEsistenti.isEmpty()) {
                    param.setNomeAzione(azioneModCriterio);
                    criteriEsistenti.get(0).setFlCriterioRaggrFisc("1");
                    helper.getEntityManager().flush();
                    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(),
                            SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                            new BigDecimal(criteriEsistenti.get(0).getIdCriterioRaggr()), param.getNomePagina());
                    warningMessage = "Poiché per il tipo di unità documentaria è stato rilevato un criterio standard e non fiscale ed il registro associato è fiscale, si è provveduto a modificare il criterio definendolo fiscale.";
                }
            }
        }

        // Ora che ho aggiunto/modificato dei criteri, ne verifico la coerenza
        // Recupero la lista degli eventuali criteri non coerenti
        List<String> criteriNonCoerenti = crHelper.getCriteriNonCoerenti(idTipoUnitaDoc);
        if (!criteriNonCoerenti.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Poiché il registro associato al tipo di unità documentaria è ");
            errorMessage.append(registroUnitaDoc.getFlRegistroFisc().equals("1") ? "fiscale " : "non fiscale ");
            errorMessage.append(
                    "i seguenti criteri non sono coerenti con l’associazione fra il tipo di unità documentaria ed il registro: ");
            for (String criterioNonCoerente : criteriNonCoerenti) {
                errorMessage.append("<br>").append(criterioNonCoerente);
            }
            throw new ParerUserError(errorMessage.toString());
        }

        if (warningMessage != null) {
            // Lancia eccezione warning senza rolbeccare
            throw new ParerWarningException(warningMessage);
        }
    }

    public DecTipoUnitaDocAmmesso getDecTipoUnitaDocAmmessoByParentId(BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        return helper.getDecTipoUnitaDocAmmessoByParentId(idRegistroUnitaDoc, idTipoUnitaDoc);
    }

    public DecRegistroUnitaDocTableBean getRegistriByTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        List<DecTipoUnitaDocAmmesso> tipoUnitaDocAmmessoList = helper
                .getDecTipoUnitaDocAmmessoByTipoUnitaDoc(idTipoUnitaDoc);
        DecRegistroUnitaDocTableBean registroUnitaDocTableBean = new DecRegistroUnitaDocTableBean();
        try {
            for (DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso : tipoUnitaDocAmmessoList) {
                DecRegistroUnitaDocRowBean registroUnitaDocRowBean = (DecRegistroUnitaDocRowBean) Transform
                        .entity2RowBean(tipoUnitaDocAmmesso.getDecRegistroUnitaDoc());
                registroUnitaDocTableBean.add(registroUnitaDocRowBean);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            logger.error("Errore durante il recupero dei registri associati al tipo unita doc "
                    + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore durante il recupero dei registri associati al tipo unita doc");
        }
        return registroUnitaDocTableBean;
    }

    public DecXsdDatiSpecTableBean getDecXsdDatiSpecTableBean(BigDecimal idTipoUnitaDoc) {
        List<DecXsdDatiSpec> xsdDatiSpecList = helper.getDecXsdDatiSpecByTipoUnitaDoc(idTipoUnitaDoc);
        DecXsdDatiSpecTableBean xsdDatiSpecTableBean = new DecXsdDatiSpecTableBean();
        try {
            for (DecXsdDatiSpec xsdDatiSpec : xsdDatiSpecList) {
                DecXsdDatiSpecRowBean xsdDatiSpecRowBean = (DecXsdDatiSpecRowBean) Transform
                        .entity2RowBean(xsdDatiSpec);
                String dsVersioneXsd = xsdDatiSpecRowBean.getDsVersioneXsd() != null
                        ? " - " + xsdDatiSpecRowBean.getDsVersioneXsd() : "";
                xsdDatiSpecRowBean.setString("descrizione", xsdDatiSpecRowBean.getCdVersioneXsd() + dsVersioneXsd);
                xsdDatiSpecTableBean.add(xsdDatiSpecRowBean);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            logger.error("Errore durante il recupero degli xsd associati al tipo unita doc "
                    + ExceptionUtils.getRootCauseMessage(e), e);
            throw new IllegalStateException("Errore durante il recupero degli xsd associati al tipo unita doc");
        }
        return xsdDatiSpecTableBean;
    }

    public void creaCriterioSulTipoUnitaDoc(LogParam param, BigDecimal idTipoUnitaDoc) {
        DecVCreaCritRaggrTipoUd creaCritTipoUD = crHelper.getDecVCreaCritRaggrTipoUd(idTipoUnitaDoc.longValue());
        DecCriterioRaggr criterioCreato = critEjb
                .salvataggioAutomaticoCriterioRaggrStdFiscNoAutomTipoUd(creaCritTipoUD);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                new BigDecimal(criterioCreato.getIdCriterioRaggr()), param.getNomePagina());
    }

    public DecTipoUnitaDocAmmessoRowBean getDecTipoUnitaDocAmmessoRowBean(BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        DecTipoUnitaDocAmmessoRowBean tipoUnitaDocAmmessoRowBean = null;
        DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso = helper.getDecTipoUnitaDocAmmessoByParentId(idRegistroUnitaDoc,
                idTipoUnitaDoc);

        if (tipoUnitaDocAmmesso != null) {
            try {
                tipoUnitaDocAmmessoRowBean = (DecTipoUnitaDocAmmessoRowBean) Transform
                        .entity2RowBean(tipoUnitaDocAmmesso);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                logger.error("Errore durante il recupero della tipologia unità documentarie ammesse "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException(
                        "Errore durante il recupero della tipologia unità documentarie ammesse");
            }
        }

        return tipoUnitaDocAmmessoRowBean;
    }

    /**
     * Metodo di salvataggio modifica di una nuova associazione tra registri e tipi di unit\u00E0 documentarie
     *
     * @param param
     *            parametri per logging
     * @param idTipoUnitaDocAmmesso
     *            id tipo unita doc ammessa
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoUnitaDocAmmesso(LogParam param, BigDecimal idTipoUnitaDocAmmesso,
            BigDecimal idRegistroUnitaDoc, BigDecimal idTipoUnitaDoc) throws ParerUserError {
        if (helper.getDecTipoUnitaDocAmmessoByParentId(idRegistroUnitaDoc, idTipoUnitaDoc) != null) {
            throw new ParerUserError("Associazione gi\u00E0 esistente all'interno della struttura</br>");
        }
        DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso = helper.findById(DecTipoUnitaDocAmmesso.class,
                idTipoUnitaDocAmmesso);
        DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        DecRegistroUnitaDoc registroUnitaDoc = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);

        tipoSerieEjb.checkTipoSerieStandardDaRegistroOTipoUd(registroUnitaDoc, tipoUnitaDoc);

        tipoUnitaDocAmmesso.setDecRegistroUnitaDoc(registroUnitaDoc);
        tipoUnitaDocAmmesso.setDecTipoUnitaDoc(tipoUnitaDoc);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                param.getNomePagina());
    }

    /**
     * Metodo di eliminazione di una nuova associazione tra registri e tipi di unit\u00E0 documentarie
     *
     * @param param
     *            parametri per logging
     * @param azioneModCriterioPerDeleteAssociazione
     *            modifica criterio per cancellazione associazione
     * @param nmPaginaPerCriteri
     *            numero pagina per criteri
     * @param idTipoUnitaDocAmmesso
     *            id tipo unita doc ammesso
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecTipoUnitaDocAmmesso(LogParam param, String azioneModCriterioPerDeleteAssociazione,
            String nmPaginaPerCriteri, BigDecimal idTipoUnitaDocAmmesso) throws ParerUserError {
        DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso = helper.findById(DecTipoUnitaDocAmmesso.class,
                idTipoUnitaDocAmmesso);
        BigDecimal idTipoUnitaDoc = new BigDecimal(tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc());
        helper.removeEntity(tipoUnitaDocAmmesso, false);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                param.getNomePagina());

        List<DecCriterioRaggr> criteriAssociazione = crHelper.getCriteriPerTipoDato(
                tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc(), TipoDato.TIPO_UNITA_DOC, null, null);
        for (DecCriterioRaggr criterioAssociazione : criteriAssociazione) {
            // Gestione del flag fiscale
            String flCriterioRaggrFiscMessage = crHelper
                    .getFlCriterioRaggrFiscMessage(new BigDecimal(criterioAssociazione.getIdCriterioRaggr()));
            if (flCriterioRaggrFiscMessage.equals(ApplEnum.FlagFiscaleMessage.FISCALE.getDescrizione())) {
                if (criterioAssociazione.getFlCriterioRaggrFisc().equals("0")) {
                    param.setNomeAzione(azioneModCriterioPerDeleteAssociazione);
                    criterioAssociazione.setFlCriterioRaggrFisc("1");
                    helper.getEntityManager().flush();
                    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(),
                            SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                            BigDecimal.valueOf(criterioAssociazione.getIdCriterioRaggr()), nmPaginaPerCriteri);
                }
            } else if (flCriterioRaggrFiscMessage.equals(ApplEnum.FlagFiscaleMessage.NON_FISCALE.getDescrizione())) {
                if (criterioAssociazione.getFlCriterioRaggrFisc().equals("1")) {
                    param.setNomeAzione(azioneModCriterioPerDeleteAssociazione);
                    criterioAssociazione.setFlCriterioRaggrFisc("0");
                    helper.getEntityManager().flush();
                    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(),
                            SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                            BigDecimal.valueOf(criterioAssociazione.getIdCriterioRaggr()), nmPaginaPerCriteri);
                }
            } else {
                throw new ParerUserError(
                        "Il criterio è errato perchè è standard ed il tipo di unità documentaria usato dal criterio è associata"
                                + " a registri fiscali ed a registri non fiscali");
            }
        }
    }

    /**
     * Metodo per ottenere i tipi unita documentarie abilitati per l'utente sulla struttura
     *
     * @param idUtente
     *            id utente
     * @param idStruttura
     *            id struttura
     * 
     * @return il tableBean con la lista di tipi
     */
    public DecTipoUnitaDocTableBean getTipiUnitaDocAbilitati(long idUtente, BigDecimal idStruttura) {
        DecTipoUnitaDocTableBean table = new DecTipoUnitaDocTableBean();
        List<DecTipoUnitaDoc> list = helper.getTipiUnitaDocAbilitati(idUtente, idStruttura);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecTipoUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista tipologie ud abilitate "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
            }
        }
        return table;
    }

    public DecTipoUnitaDocRowBean getDecTipoUnitaDocRowBean(String nmTipoUnitaDoc, BigDecimal idStrut) {
        DecTipoUnitaDocRowBean tipoUnitaDocRowBean = null;
        DecTipoUnitaDoc tipoUnitaDoc = helper.getDecTipoUnitaDocByName(nmTipoUnitaDoc, idStrut);
        if (tipoUnitaDoc != null) {
            try {
                tipoUnitaDocRowBean = (DecTipoUnitaDocRowBean) Transform.entity2RowBean(tipoUnitaDoc);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                logger.error("Errore durante il recupero della tipologia unità documentarie "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new IllegalStateException("Errore durante il recupero della tipologia unità documentarie ");
            }
        }
        return tipoUnitaDocRowBean;
    }

    public boolean existDecTipoUnitaDocForIdModello(BigDecimal idModelloTipoSerie) {
        return helper.countDecTipoUnitaDoc(idModelloTipoSerie) > 0L;
    }

    public DecTipoUnitaDocRowBean getDecTipoUnitaDocRowBean(BigDecimal idTipoUnita, BigDecimal idStrut) {
        DecTipoUnitaDocRowBean tipoUnitaDocRowBean = getDecTipoUnitaDoc(idTipoUnita, null, idStrut);
        return tipoUnitaDocRowBean;
    }

    public DecTipoUnitaDocRowBean getDecTipoUnitaDoc(BigDecimal idTipoUnitaDoc, String nmTipoUnitaDoc,
            BigDecimal idStrut) {

        DecTipoUnitaDocRowBean tipoUnitaDocRowBean = null;
        DecTipoUnitaDoc tipoUnitaDoc = null;

        if (idTipoUnitaDoc == BigDecimal.ZERO && nmTipoUnitaDoc != null) {
            tipoUnitaDoc = helper.getDecTipoUnitaDocByName(nmTipoUnitaDoc, idStrut);
        }
        if (nmTipoUnitaDoc == null && idTipoUnitaDoc != BigDecimal.ZERO) {
            tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        }

        if (tipoUnitaDoc != null) {
            try {
                tipoUnitaDocRowBean = (DecTipoUnitaDocRowBean) Transform.entity2RowBean(tipoUnitaDoc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tipoUnitaDocRowBean;
    }

    public DecTipoUnitaDocTableBean getDecTipoUnitaDocTableBean(BigDecimal idStrut, boolean isFilterValid) {

        DecTipoUnitaDocTableBean tipoUnitaTableBean = new DecTipoUnitaDocTableBean();
        List<DecTipoUnitaDoc> list = helper.getDecTipoUnitaDocList(idStrut, isFilterValid);

        // Creo il campo relativo al criterio di raggruppamento standard
        Calendar calendar = GregorianCalendar.getInstance();
        int anno = calendar.get(Calendar.YEAR);

        try {
            if (!list.isEmpty()) {
                for (DecTipoUnitaDoc tipoUnitaDoc : list) {
                    DecTipoUnitaDocRowBean tipoUnitaRow = (DecTipoUnitaDocRowBean) Transform
                            .entity2RowBean(tipoUnitaDoc);
                    if (tipoUnitaDoc.getDtIstituz().before(new Date())
                            && tipoUnitaDoc.getDtSoppres().after(new Date())) {
                        tipoUnitaRow.setObject("fl_attivo", "1");
                    } else {
                        tipoUnitaRow.setObject("fl_attivo", "0");
                    }

                    DecXsdDatiSpec lastXsd = datiSpecHelper.getLastDecXsdDatiSpecForTipoUnitaDoc(idStrut.longValue(),
                            tipoUnitaDoc.getIdTipoUnitaDoc());
                    if (lastXsd != null) {
                        tipoUnitaRow.setObject("cd_versione_xsd", lastXsd.getCdVersioneXsd());
                    } else {
                        tipoUnitaRow.setObject("cd_versione_xsd", null);
                    }

                    List<DecTipoUnitaDocAmmesso> listatipiunitadocAmmessi = tipoUnitaDoc.getDecTipoUnitaDocAmmessos();
                    StringBuilder tmpBuilder = new StringBuilder();
                    if (listatipiunitadocAmmessi != null && !listatipiunitadocAmmessi.isEmpty()) {
                        for (int index = 0; index < listatipiunitadocAmmessi.size(); index++) {
                            DecTipoUnitaDocAmmesso row = listatipiunitadocAmmessi.get(index);
                            tmpBuilder.append(row.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc());
                            if (index < (listatipiunitadocAmmessi.size() - 1)) {
                                tmpBuilder.append("; ");
                            }
                        }
                    }

                    tipoUnitaRow.setString("registri_ammessi", tmpBuilder.toString());

                    // xsd_modelli_ammessi
                    tmpBuilder = new StringBuilder();
                    List<DecUsoModelloXsdUniDoc> usoModelloXsdUniDoc = tipoUnitaDoc.getDecUsoModelloXsdUniDocs();
                    if (usoModelloXsdUniDoc != null && !usoModelloXsdUniDoc.isEmpty()) {
                        Set<String> dsXsdUn = new HashSet<>();
                        for (int index = 0; index < usoModelloXsdUniDoc.size(); index++) {
                            DecUsoModelloXsdUniDoc row = usoModelloXsdUniDoc.get(index);
                            dsXsdUn.add(row.getDecModelloXsdUd().getTiModelloXsd().toString() + "_v"
                                    + row.getDecModelloXsdUd().getCdXsd());
                        }
                        tmpBuilder.append(dsXsdUn.stream().distinct().collect(Collectors.joining("; ")));
                    }

                    tipoUnitaRow.setString("xsd_modelli_ammessi", tmpBuilder.toString());

                    // Nm_tipo_strut_unita_doc
                    tmpBuilder = new StringBuilder();
                    List<DecTipoStrutUnitaDoc> listaTipoStrutUnitaDoc = tipoUnitaDoc.getDecTipoStrutUnitaDocs();
                    if (listaTipoStrutUnitaDoc != null && !listaTipoStrutUnitaDoc.isEmpty()) {
                        for (int index = 0; index < listaTipoStrutUnitaDoc.size(); index++) {
                            DecTipoStrutUnitaDoc row = listaTipoStrutUnitaDoc.get(index);
                            tmpBuilder.append(row.getNmTipoStrutUnitaDoc());
                            if (index < (listaTipoStrutUnitaDoc.size() - 1)) {
                                tmpBuilder.append("; ");
                            }
                        }
                    }
                    tipoUnitaRow.setString("nm_tipo_strut_unita_doc", tmpBuilder.toString());

                    String flagStandard = critEjb.getCriterioStandardPerTipoDatoAnno(tipoUnitaDoc.getIdTipoUnitaDoc(),
                            TipoDato.TIPO_UNITA_DOC);
                    tipoUnitaRow.setString("flag_criterio_standard", flagStandard);
                    String a = null;

                    // Recupero i Sistemi versanti
                    tipoUnitaRow.setString("nm_sistema_versante", helper
                            .getAplSistemiVersantiSeparatiPerTipoUd(new BigDecimal(tipoUnitaDoc.getIdTipoUnitaDoc())));

                    // Sistemi versanti
                    AplSistemaVersanteTableBean listaSistemiVersanti = getAplSistemaVersanteTableBean(
                            tipoUnitaRow.getIdTipoUnitaDoc());
                    AplSistemaVersanteRowBean rigaSistemaVersante = listaSistemiVersanti.getRow(0);
                    if (rigaSistemaVersante != null && rigaSistemaVersante.getObject("dt_first_vers") != null) {
                        Date d = (Date) rigaSistemaVersante.getObject("dt_first_vers");
                        tipoUnitaRow.setObject("dt_first_vers", d);
                    }

                    tipoUnitaTableBean.add(tipoUnitaRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoUnitaTableBean;
    }

    public DecTipoUnitaDocTableBean getDecTipoUnitaDocTableBean(BigDecimal idStrut) {
        List<DecTipoUnitaDoc> tipiUnitaDoc = helper.retrieveDecTipoUnitaDoc(idStrut);
        DecTipoUnitaDocTableBean tableBean = new DecTipoUnitaDocTableBean();
        try {
            if (tipiUnitaDoc != null && !tipiUnitaDoc.isEmpty()) {
                tableBean = (DecTipoUnitaDocTableBean) Transform.entities2TableBean(tipiUnitaDoc);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei tipi unit\u00E0 documentaria "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public DecTipoStrutUnitaDocRowBean getDecTipoStrutUnitaDocRowBean(BigDecimal idTipoStrutUnitaDoc) {
        DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = getDecTipoStrutUnitaDoc(idTipoStrutUnitaDoc, null, null);
        return tipoStrutUnitaDocRowBean;
    }

    public DecTipoStrutUnitaDocRowBean getDecTipoStrutUnitaDocRowBean(String nmTipoStrutUnitaDoc,
            BigDecimal idTipoUnitaDoc) {

        DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = getDecTipoStrutUnitaDoc(BigDecimal.ZERO,
                nmTipoStrutUnitaDoc, idTipoUnitaDoc);
        return tipoStrutUnitaDocRowBean;

    }

    public DecTipoStrutUnitaDocRowBean getDecTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc,
            String nmTipoStrutUnitaDoc, BigDecimal idTipoUnitaDoc) {

        DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean = null;
        DecTipoStrutUnitaDoc tipoStrutUnitaDoc = null;

        if (idTipoStrutUnitaDoc == BigDecimal.ZERO && nmTipoStrutUnitaDoc != null) {
            tipoStrutUnitaDoc = helper.getDecTipoStrutUnitaDocByName(nmTipoStrutUnitaDoc, idTipoUnitaDoc);
        }
        if (nmTipoStrutUnitaDoc == null && idTipoStrutUnitaDoc != BigDecimal.ZERO) {
            tipoStrutUnitaDoc = helper.findById(DecTipoStrutUnitaDoc.class, idTipoStrutUnitaDoc);
        }

        if (tipoStrutUnitaDoc != null) {
            try {
                tipoStrutUnitaDocRowBean = (DecTipoStrutUnitaDocRowBean) Transform.entity2RowBean(tipoStrutUnitaDoc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tipoStrutUnitaDocRowBean;
    }

    public DecVLisTiUniDocAmsTableBean getDecVLisTiUniDocAmsTableBean(
            DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmessoRowBean) {
        DecVLisTiUniDocAmsTableBean tipoUnitaDocAmmessoTableBean = new DecVLisTiUniDocAmsTableBean();
        List<DecVLisTiUniDocAms> list = helper.getDecVLisTiUniDocAmList(tipoUnitaDocAmmessoRowBean);
        try {
            if (!list.isEmpty()) {
                tipoUnitaDocAmmessoTableBean = (DecVLisTiUniDocAmsTableBean) Transform.entities2TableBean(list);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoUnitaDocAmmessoTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoStrutUnitaDoc(LogParam param, DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean,
            List<BigDecimal> idSistemaVersanteList, List<BigDecimal> idRegistroUnitaDocList,
            List<BigDecimal> idXsdDatiSpecList) throws ParerUserError {
        if (helper.getDecTipoStrutUnitaDocByName(tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc(),
                tipoStrutUnitaDocRowBean.getIdTipoUnitaDoc()) != null) {
            throw new ParerUserError("Tipo struttura gi\u00E0 esistente all'interno della struttura</br>");
        }

        DecTipoStrutUnitaDoc tipoStrutUnitaDoc = new DecTipoStrutUnitaDoc();
        DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class,
                tipoStrutUnitaDocRowBean.getIdTipoUnitaDoc());
        tipoStrutUnitaDoc.setDecTipoUnitaDoc(tipoUnitaDoc);
        tipoStrutUnitaDoc.setNmTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDtIstituz(tipoStrutUnitaDocRowBean.getDtIstituz());
        tipoStrutUnitaDoc.setDtSoppres(tipoStrutUnitaDocRowBean.getDtSoppres());

        tipoStrutUnitaDoc.setDsAnnoTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsAnnoTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsDataTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsDataTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsNumeroTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsNumeroTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsOggTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsOggTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsRifTempTipoStrutUd(tipoStrutUnitaDocRowBean.getDsRifTempTipoStrutUd());
        tipoStrutUnitaDoc.setDsCollegamentiUd(tipoStrutUnitaDocRowBean.getDsCollegamentiUd());
        tipoStrutUnitaDoc.setDsPeriodicitaVers(tipoStrutUnitaDocRowBean.getDsPeriodicitaVers());
        tipoStrutUnitaDoc.setDsFirma(tipoStrutUnitaDocRowBean.getDsFirma());

        tipoStrutUnitaDoc.setAaMaxTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getAaMaxTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setAaMinTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getAaMinTipoStrutUnitaDoc());

        helper.insertEntity(tipoStrutUnitaDoc, true);

        if (idSistemaVersanteList != null && !idSistemaVersanteList.isEmpty()) {
            for (BigDecimal idSistemaVersante : idSistemaVersanteList) {
                DecTipoStrutUdSisVer tipoStrutUdSisVers = new DecTipoStrutUdSisVer();
                // tipoStrutUdSisVers.setIdSistemaVersante(idSistemaVersante);
                tipoStrutUdSisVers.setAplSistemaVersante(helper.findById(AplSistemaVersante.class, idSistemaVersante));
                tipoStrutUdSisVers.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
                helper.insertEntity(tipoStrutUdSisVers, true);
            }
        }

        if (idRegistroUnitaDocList != null && !idRegistroUnitaDocList.isEmpty()) {
            for (BigDecimal idRegistroUnitaDoc : idRegistroUnitaDocList) {
                DecTipoStrutUdReg tipoStrutUdReg = new DecTipoStrutUdReg();
                tipoStrutUdReg.setDecRegistroUnitaDoc(helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc));
                tipoStrutUdReg.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
                helper.insertEntity(tipoStrutUdReg, true);
            }
        }

        if (idXsdDatiSpecList != null && !idXsdDatiSpecList.isEmpty()) {
            for (BigDecimal idXsdDatiSpec : idXsdDatiSpecList) {
                DecTipoStrutUdXsd tipoStrutUdXsd = new DecTipoStrutUdXsd();
                tipoStrutUdXsd.setDecXsdDatiSpec(helper.findById(DecXsdDatiSpec.class, idXsdDatiSpec));
                tipoStrutUdXsd.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
                helper.insertEntity(tipoStrutUdXsd, true);
            }
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                tipoStrutUnitaDocRowBean.getIdTipoUnitaDoc(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoStrutUnitaDoc(LogParam param, BigDecimal idTipoStrutUnitaDoc,
            DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean, List<BigDecimal> idSistemaVersanteList,
            List<BigDecimal> idRegistroUnitaDocList, List<BigDecimal> idXsdDatiSpecList) throws ParerUserError {
        DecTipoStrutUnitaDoc dbTipoStrutUnitaDoc = helper.getDecTipoStrutUnitaDocByName(
                tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc(), tipoStrutUnitaDocRowBean.getIdTipoUnitaDoc());
        if (dbTipoStrutUnitaDoc != null
                && dbTipoStrutUnitaDoc.getIdTipoStrutUnitaDoc() != idTipoStrutUnitaDoc.longValue()) {
            throw new ParerUserError(
                    "Nome tipo struttura unit\u00E0 documentaria gi\u00E0 associato a questa struttura all'interno del database</br>");
        }

        DecTipoStrutUnitaDoc tipoStrutUnitaDoc = struttureHelper.findById(DecTipoStrutUnitaDoc.class,
                idTipoStrutUnitaDoc);
        tipoStrutUnitaDoc.setNmTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getNmTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDtIstituz(tipoStrutUnitaDocRowBean.getDtIstituz());
        tipoStrutUnitaDoc.setDtSoppres(tipoStrutUnitaDocRowBean.getDtSoppres());

        tipoStrutUnitaDoc.setDsAnnoTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsAnnoTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsDataTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsDataTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsNumeroTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsNumeroTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsOggTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getDsOggTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setDsRifTempTipoStrutUd(tipoStrutUnitaDocRowBean.getDsRifTempTipoStrutUd());
        tipoStrutUnitaDoc.setDsCollegamentiUd(tipoStrutUnitaDocRowBean.getDsCollegamentiUd());
        tipoStrutUnitaDoc.setDsPeriodicitaVers(tipoStrutUnitaDocRowBean.getDsPeriodicitaVers());
        tipoStrutUnitaDoc.setDsFirma(tipoStrutUnitaDocRowBean.getDsFirma());

        tipoStrutUnitaDoc.setAaMaxTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getAaMaxTipoStrutUnitaDoc());
        tipoStrutUnitaDoc.setAaMinTipoStrutUnitaDoc(tipoStrutUnitaDocRowBean.getAaMinTipoStrutUnitaDoc());

        // Gestione registri e sistemi versanti
        if (idSistemaVersanteList != null && !idSistemaVersanteList.isEmpty()) {
            // Cancello quanto tolto
            for (DecTipoStrutUdSisVer tipoStrutUdSisVer : tipoStrutUnitaDoc.getDecTipoStrutUdSisVers()) {
                boolean trovato = false;
                for (BigDecimal idSistemaVersante : idSistemaVersanteList) {

                    if (idSistemaVersante.compareTo(BigDecimal
                            .valueOf(tipoStrutUdSisVer.getAplSistemaVersante().getIdSistemaVersante())) == 0) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    helper.deleteDecTipoStrutUdSisVers(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc(),
                            BigDecimal.valueOf(tipoStrutUdSisVer.getAplSistemaVersante().getIdSistemaVersante()));
                }
            }

            // Inserisco quel che è nuovo
            for (BigDecimal idSistemaVersante : idSistemaVersanteList) {
                boolean trovato = false;
                for (DecTipoStrutUdSisVer tipoStrutUdSisVer : tipoStrutUnitaDoc.getDecTipoStrutUdSisVers()) {
                    if (idSistemaVersante.compareTo(BigDecimal
                            .valueOf(tipoStrutUdSisVer.getAplSistemaVersante().getIdSistemaVersante())) == 0) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    insertDecTipoStrutUdSisVers(tipoStrutUnitaDoc, idSistemaVersante);
                }
            }
        } else {
            // Cancello tutto
            helper.bulkDeleteDecTipoStrutUdSisVers(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc());
        }

        if (idRegistroUnitaDocList != null && !idRegistroUnitaDocList.isEmpty()) {
            // Cancello quanto tolto
            for (DecTipoStrutUdReg tipoStrutUdReg : tipoStrutUnitaDoc.getDecTipoStrutUdRegs()) {
                boolean trovato = false;
                for (BigDecimal idRegistroUnitaDoc : idRegistroUnitaDocList) {

                    if (idRegistroUnitaDoc.compareTo(
                            BigDecimal.valueOf(tipoStrutUdReg.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc())) == 0) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    helper.deleteDecTipoStrutUdReg(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc(),
                            tipoStrutUdReg.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc());
                }
            }

            // Inserisco quel che è nuovo
            for (BigDecimal idRegistroUnitaDoc : idRegistroUnitaDocList) {
                boolean trovato = false;
                for (DecTipoStrutUdReg tipoStrutUdReg : tipoStrutUnitaDoc.getDecTipoStrutUdRegs()) {
                    if (idRegistroUnitaDoc.compareTo(
                            BigDecimal.valueOf(tipoStrutUdReg.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc())) == 0) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    insertDecTipoStrutUdReg(tipoStrutUnitaDoc, idRegistroUnitaDoc);
                }
            }
        } else {
            // Cancello tutto
            helper.bulkDeleteDecTipoStrutUdReg(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc());
        }

        if (idXsdDatiSpecList != null && !idXsdDatiSpecList.isEmpty()) {
            // Cancello quanto tolto
            for (DecTipoStrutUdXsd tipoStrutUdXsd : tipoStrutUnitaDoc.getDecTipoStrutUdXsds()) {
                boolean trovato = false;
                for (BigDecimal idXsdDatiSpec : idXsdDatiSpecList) {

                    if (idXsdDatiSpec.compareTo(
                            BigDecimal.valueOf(tipoStrutUdXsd.getDecXsdDatiSpec().getIdXsdDatiSpec())) == 0) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    helper.deleteDecTipoStrutUdXsd(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc(),
                            tipoStrutUdXsd.getDecXsdDatiSpec().getIdXsdDatiSpec());
                }
            }

            // Inserisco quel che è nuovo
            for (BigDecimal idXsdDatiSpec : idXsdDatiSpecList) {
                boolean trovato = false;
                for (DecTipoStrutUdXsd tipoStrutUdXsd : tipoStrutUnitaDoc.getDecTipoStrutUdXsds()) {
                    if (idXsdDatiSpec.compareTo(
                            BigDecimal.valueOf(tipoStrutUdXsd.getDecXsdDatiSpec().getIdXsdDatiSpec())) == 0) {
                        trovato = true;
                    }
                }
                if (!trovato) {
                    insertDecTipoStrutUdXsd(tipoStrutUnitaDoc, idXsdDatiSpec);
                }
            }
        } else {
            // Cancello tutto
            helper.bulkDeleteDecTipoStrutUdXsd(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc());
        }

        struttureHelper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                tipoStrutUnitaDocRowBean.getIdTipoUnitaDoc(), param.getNomePagina());
    }

    public void insertDecTipoStrutUdSisVers(DecTipoStrutUnitaDoc tipoStrutUnitaDoc, BigDecimal idSistemaVersante) {
        DecTipoStrutUdSisVer tipoStrutUdSisVers = new DecTipoStrutUdSisVer();
        // tipoStrutUdSisVers.setIdSistemaVersante(idSistemaVersante);
        tipoStrutUdSisVers.setAplSistemaVersante(helper.findById(AplSistemaVersante.class, idSistemaVersante));
        tipoStrutUdSisVers.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
        helper.insertEntity(tipoStrutUdSisVers, true);
    }

    public void insertDecTipoStrutUdReg(DecTipoStrutUnitaDoc tipoStrutUnitaDoc, BigDecimal idRegistroUnitaDoc) {
        DecTipoStrutUdReg tipoStrutUdReg = new DecTipoStrutUdReg();
        tipoStrutUdReg.setDecRegistroUnitaDoc(helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc));
        tipoStrutUdReg.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
        helper.insertEntity(tipoStrutUdReg, true);
    }

    public void insertDecTipoStrutUdXsd(DecTipoStrutUnitaDoc tipoStrutUnitaDoc, BigDecimal idXsdDatiSpec) {
        DecTipoStrutUdXsd tipoStrutUdXsd = new DecTipoStrutUdXsd();
        tipoStrutUdXsd.setDecXsdDatiSpec(helper.findById(DecXsdDatiSpec.class, idXsdDatiSpec));
        tipoStrutUdXsd.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
        helper.insertEntity(tipoStrutUdXsd, true);
    }

    public void deleteDecTipoUnitaDoc(LogParam param, long idTipoUnitaDoc) throws ParerUserError {
        TipoUnitaDocEjb me = context.getBusinessObject(TipoUnitaDocEjb.class);
        IamOrganizDaReplic replic = me.deleteTipoUnitaDocPuntuale(param, idTipoUnitaDoc);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamOrganizDaReplic deleteTipoUnitaDocFromStruttura(LogParam param, long idTipoUnitaDoc)
            throws ParerUserError {
        return eseguiDeleteTipoUnitaDoc(param, idTipoUnitaDoc, true);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteTipoUnitaDocPuntuale(LogParam param, long idTipoUnitaDoc) throws ParerUserError {
        return eseguiDeleteTipoUnitaDoc(param, idTipoUnitaDoc, false);
    }

    /**
     * Business method per la cancellazione di un tipo unità documentaria di una determinata struttura. Il metodo esegue
     * le seguenti istruzioni: 1) Controlla i legami con Tipologia Serie e Unità documentarie; 2) Controllo i legami con
     * criteri di raggruppamento e, se può, li elimina 3) Esegue la cancellazione del Tipo Ud con cascade 4) Scrive il
     * record dell'organizzazione da replicare
     *
     * @param idTipoUnitaDoc
     * @param isFromDeleteStruttura
     * 
     * @return il record dell'organizzazione da replicare
     * 
     * @throws ParerUserError
     */
    private IamOrganizDaReplic eseguiDeleteTipoUnitaDoc(LogParam param, long idTipoUnitaDoc,
            boolean isFromDeleteStruttura) throws ParerUserError {
        // FIXME
        DecTipoUnitaDoc tipoUnitaDoc = helper.findById(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        String nmTipoUnitaDoc = tipoUnitaDoc.getNmTipoUnitaDoc();
        long idStrut = tipoUnitaDoc.getOrgStrut().getIdStrut();

        // boolean existsRelationsWithTipiSerie =
        // struttureHelper.existsRelationsWithTipiSerieForDecTipoUnitaDoc(tipoUnitaDoc.getIdTipoUnitaDoc());
        boolean existsRelationsWithTipiSerie = tipoSerieHelper
                .existsRelationsWithTipiSerie(tipoUnitaDoc.getIdTipoUnitaDoc(), Constants.TipoDato.TIPO_UNITA_DOC);
        if (existsRelationsWithTipiSerie) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo UD: esiste almeno una tipologia di serie associata a una serie</br>");
        }

        // boolean existsRelationsWithUnitaDoc =
        // struttureHelper.existsRelationsWithUnitaDocForDecTipoUnitaDoc(tipoUnitaDoc.getIdTipoUnitaDoc());
        boolean existsRelationsWithUnitaDoc = unitaDocHelper
                .existsRelationsWithUnitaDoc(tipoUnitaDoc.getIdTipoUnitaDoc(), Constants.TipoDato.TIPO_UNITA_DOC);
        if (existsRelationsWithUnitaDoc) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo UD: esiste almeno un'unità documentaria versata con tale tipologia</br>");
        }

        boolean existsRelationsWithModello = modelliSerieHelper
                .existsRelationsWithModello(tipoUnitaDoc.getIdTipoUnitaDoc(), Constants.TipoDato.TIPO_UNITA_DOC);
        if (existsRelationsWithModello) {
            throw new ParerUserError(
                    "La tipologia di unità documentaria è definita come filtro su almeno un modello</br>");
        }

        // // Il sistema controlla se per quel tipo ud esistono dei servizi erogati (in
        // base alla data
        // // di primo versamento del tipo ud
        // boolean existsRelationsWithServiziErogatiServAttivTipoUd = false;
        // boolean existsRelationsWithServiziErogatiServConservTipoUd = false;
        // boolean existsRelationsWithServiziErogati = false;
        // boolean existsRelationsWithServiziErogatiAttiv = false;
        // List<Long> listaFatture = new ArrayList<Long>();
        // if (dtFirstVers != null) {
        // if (tipoUnitaDoc.getOrgTipoServAttivTipoUd() != null) {
        // existsRelationsWithServiziErogatiServAttivTipoUd = helper
        // .existsRelationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServAttivTipoUd().getIdTipoServizio());
        // if (existsRelationsWithServiziErogatiServAttivTipoUd) {
        // listaFatture.addAll(helper
        // .relationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServAttivTipoUd().getIdTipoServizio()));
        // }
        // }
        // if (tipoUnitaDoc.getOrgTipoServConservTipoUd() != null) {
        // existsRelationsWithServiziErogatiServConservTipoUd = helper
        // .existsRelationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServConservTipoUd().getIdTipoServizio());
        // if (existsRelationsWithServiziErogatiServConservTipoUd) {
        // listaFatture.addAll(helper
        // .relationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServConservTipoUd().getIdTipoServizio()));
        // }
        //
        // }
        // if (tipoUnitaDoc.getOrgTipoServizio() != null) {
        // existsRelationsWithServiziErogati = helper
        // .existsRelationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServizio().getIdTipoServizio());
        // if (existsRelationsWithServiziErogati) {
        // listaFatture.addAll(
        // helper.relationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServizio().getIdTipoServizio()));
        // }
        //
        // }
        // if (tipoUnitaDoc.getOrgTipoServizioAttiv() != null) {
        // existsRelationsWithServiziErogatiAttiv = helper
        // .existsRelationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServizioAttiv().getIdTipoServizio());
        // if (existsRelationsWithServiziErogatiAttiv) {
        // listaFatture.addAll(
        // helper.relationsWithServiziErogati(tipoUnitaDoc.getOrgTipoServizioAttiv().getIdTipoServizio()));
        // }
        // }
        // if (existsRelationsWithServiziErogatiServAttivTipoUd ||
        // existsRelationsWithServiziErogatiServConservTipoUd
        // || existsRelationsWithServiziErogati ||
        // existsRelationsWithServiziErogatiAttiv) {
        // throw new ParerUserError(
        // "Impossibile eliminare il tipo UD: Esiste almeno un servizio fatturato
        // associato al tipo di unità
        // documentaria</br>"
        // + "In particolare nelle seguenti fatture: " + listaFatture.toString());
        // }
        // }

        List<ObjectsToLogBefore> listaBefore = sacerLogEjb.logBefore(param.getTransactionLogContext(),
                param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
                SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, new BigDecimal(idTipoUnitaDoc),
                param.getNomePagina());
        List<ObjectsToLogBefore> listaBeforeDeletion = ObjectsToLogBefore.filterObjectsForDeletion(listaBefore);
        List<ObjectsToLogBefore> listaBeforeModifying = ObjectsToLogBefore.filterObjectsForModifying(listaBefore);
        /*
         * In questo caso gli oggetti vengono fotografati prima perché spariranno completamente
         */
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaBeforeDeletion, param.getNomePagina());

        // List<DecCriterioFiltroMultiplo> criteriAssociati =
        // struttureHelper.getRelationsWithCriteriRaggruppamentoForDecTipoUnitaDoc(tipoUnitaDoc.getIdTipoUnitaDoc());
        List<DecCriterioFiltroMultiplo> criteriAssociati = struttureHelper.getRelationsWithCriteriRaggruppamento(
                tipoUnitaDoc.getIdTipoUnitaDoc(), Constants.TipoDato.TIPO_UNITA_DOC);
        if (!criteriAssociati.isEmpty()) {
            List<Long> criteriRaggrDaEliminare = new ArrayList<>();
            for (DecCriterioFiltroMultiplo criterioAssociato : criteriAssociati) {
                long idCriterioRaggr = criterioAssociato.getDecCriterioRaggr().getIdCriterioRaggr();
                boolean existsRelationsWithElenchiVolumiForCriterioRaggruppamento = struttureHelper
                        .existsRelationsWithElenchiVolumiForCriterioRaggruppamento(idCriterioRaggr);
                if (existsRelationsWithElenchiVolumiForCriterioRaggruppamento) {
                    throw new ParerUserError(
                            "Il criterio è collegato a dei volumi od a degli elenchi di versamento</br>");
                }
                criteriRaggrDaEliminare.add(idCriterioRaggr);
            }
            // Se sono arrivato fin qui, quindi non è scattata l'eccezione, posso eliminare
            // i criteri
            crHelper.bulkDeleteCriteriRaggr(criteriRaggrDaEliminare);
        }
        // Rimuovo il tipo unità documentarie ed in cascata le associazioni
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                new BigDecimal(idTipoUnitaDoc), param.getNomePagina());
        helper.removeEntity(tipoUnitaDoc, true);
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        // Calcolo servizi erogati
        boolean esitoOK = struttureEjb.calcoloServiziErogati(struttura.getIdEnteConvenz());
        if (!esitoOK) {
            throw new ParerUserError(
                    "Errore durante il calcolo dei servizi erogati a seguito di cancellazione tipo ud</br>");
        }
        /* Foto dopo eliminazione di eventuali disassociazioni */
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaBeforeModifying, param.getNomePagina());

        IamOrganizDaReplic replic = null;
        /*
         * La replica va fatta solo se la tipologia unità documentaria apparteneva ad una struttura non appartenente ad
         * un ente di tipo template
         */
        if ((struttura.getOrgEnte().getTipoDefTemplateEnte()
                .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
                || struttura.getOrgEnte().getTipoDefTemplateEnte()
                        .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))
                && !isFromDeleteStruttura) {
            replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, ApplEnum.TiOperReplic.MOD);
        }
        logger.info("Cancellazione tipo unità documentaria " + nmTipoUnitaDoc + " della struttura " + idStrut
                + " avvenuta con successo!");
        return replic;
    }

    public DecTipoDocAmmessoTableBean getDecTipoDocAmmessoTableBean(BigDecimal idTipoStrutUnitaDoc) {

        DecTipoDocAmmessoTableBean tipoDocAmmessoTableBean = new DecTipoDocAmmessoTableBean();
        List<DecTipoDocAmmesso> list = helper.getDecTipoDocAmmessoList(idTipoStrutUnitaDoc);

        try {
            if (!list.isEmpty()) {
                for (DecTipoDocAmmesso row : list) {
                    DecTipoDocAmmessoRowBean rowBean = (DecTipoDocAmmessoRowBean) Transform.entity2RowBean(row);
                    rowBean.setString("nm_tipo_doc", row.getDecTipoDoc().getNmTipoDoc());
                    tipoDocAmmessoTableBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return tipoDocAmmessoTableBean;
    }

    public DecTipoDocAmmessoRowBean getDecTipoDocAmmessoRowBean(BigDecimal idTipoDocAmmesso, BigDecimal idTipoDoc,
            BigDecimal idTipoStrutUnitaDoc) {
        DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean = null;
        DecTipoDocAmmesso tipoDocAmmesso = null;

        if (idTipoDoc != BigDecimal.ZERO && idTipoStrutUnitaDoc != BigDecimal.ZERO) {
            tipoDocAmmesso = helper.getDecTipoDocAmmesso(idTipoDoc, idTipoStrutUnitaDoc);
        } else if (idTipoDocAmmesso != BigDecimal.ZERO) {
            tipoDocAmmesso = helper.findById(DecTipoDocAmmesso.class, idTipoDocAmmesso);
        }
        if (tipoDocAmmesso != null) {
            try {
                tipoDocAmmessoRowBean = (DecTipoDocAmmessoRowBean) Transform.entity2RowBean(tipoDocAmmesso);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tipoDocAmmessoRowBean;
    }

    public DecTipoDocAmmessoRowBean getDecTipoDocAmmessoRowBean(BigDecimal idTipoDocAmmesso, BigDecimal idTipoDoc,
            BigDecimal idTipoStrutUnitaDoc, String tiDoc) {
        DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean = null;
        List<DecTipoDocAmmesso> tipoDocAmmessoList = helper.getDecTipoDocAmmessoList(idTipoDocAmmesso, idTipoDoc,
                idTipoStrutUnitaDoc, tiDoc);
        if (!tipoDocAmmessoList.isEmpty()) {
            try {
                tipoDocAmmessoRowBean = (DecTipoDocAmmessoRowBean) Transform.entity2RowBean(tipoDocAmmessoList.get(0));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return tipoDocAmmessoRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecTipoDocAmmesso(LogParam param, DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean) {
        DecTipoStrutUnitaDoc tipoStrutUnitaDoc = helper.findById(DecTipoStrutUnitaDoc.class,
                tipoDocAmmessoRowBean.getIdTipoStrutUnitaDoc());
        DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, tipoDocAmmessoRowBean.getIdTipoDoc());

        DecTipoDocAmmesso tipoDocAmmesso = new DecTipoDocAmmesso();
        tipoDocAmmesso.setTiDoc(tipoDocAmmessoRowBean.getTiDoc());
        tipoDocAmmesso.setFlObbl(tipoDocAmmessoRowBean.getFlObbl());
        tipoDocAmmesso.setDecTipoStrutUnitaDoc(tipoStrutUnitaDoc);
        tipoDocAmmesso.setDecTipoDoc(tipoDoc);

        helper.insertEntity(tipoDocAmmesso, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                new BigDecimal(tipoDocAmmesso.getDecTipoStrutUnitaDoc().getDecTipoUnitaDoc().getIdTipoUnitaDoc()),
                param.getNomePagina());
    }

    public boolean existsDecTipoDocAmmesso(BigDecimal idTipoDocAmmesso, BigDecimal idTipoDoc,
            BigDecimal idTipoStrutUnitaDoc, String tipoElemento) {
        return helper.existsDecTipoDocAmmesso(idTipoDocAmmesso, idTipoDoc, idTipoStrutUnitaDoc, tipoElemento);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecTipoDocAmmesso(LogParam param, BigDecimal idTipoDocAmmesso,
            DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean, String tiDocIniziale) throws ParerUserError {
        DecTipoDoc tipoDoc = helper.findById(DecTipoDoc.class, tipoDocAmmessoRowBean.getIdTipoDoc());
        if (!tipoDocAmmessoRowBean.getTiDoc().equals(tiDocIniziale)
                && helper.existsDecTipoDocAmmesso(idTipoDocAmmesso, tipoDocAmmessoRowBean.getIdTipoDoc(),
                        tipoDocAmmessoRowBean.getIdTipoStrutUnitaDoc(), tipoDocAmmessoRowBean.getTiDoc())) {
            throw new ParerUserError(
                    "Associazione gi\u00E0 esistente all'interno della struttura per il tipo documento "
                            + tipoDoc.getNmTipoDoc() + " elemento " + tipoDocAmmessoRowBean.getTiDoc() + "</br>");
        }

        DecTipoDocAmmesso tipoDocAmmesso = helper.findById(DecTipoDocAmmesso.class, idTipoDocAmmesso);
        tipoDocAmmesso.setFlObbl(tipoDocAmmessoRowBean.getFlObbl());
        tipoDocAmmesso.setTiDoc(tipoDocAmmessoRowBean.getTiDoc());

        helper.getEntityManager().flush();
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                new BigDecimal(tipoDocAmmesso.getDecTipoStrutUnitaDoc().getDecTipoUnitaDoc().getIdTipoUnitaDoc()),
                param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecTipoStrutUnitaDoc(LogParam param, DecTipoStrutUnitaDocRowBean tipoStrutUnitaDocRowBean)
            throws ParerUserError {
        DecTipoStrutUnitaDoc tipoStrutUnitaDoc = helper.findById(DecTipoStrutUnitaDoc.class,
                tipoStrutUnitaDocRowBean.getIdTipoStrutUnitaDoc());
        boolean isEmptyRelation = helper
                .checkRelationsAreEmptyForDecTipoStrutUnitaDoc(tipoStrutUnitaDoc.getIdTipoStrutUnitaDoc());
        if (isEmptyRelation) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo struttura unit\u00E0 doc: esiste almeno un tipo documento ammesso associato ad esso</br>");
        }
        helper.removeEntity(tipoStrutUnitaDoc, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA,
                tipoStrutUnitaDocRowBean.getIdTipoUnitaDoc(), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecTipoDocAmmesso(LogParam param, DecTipoDocAmmessoRowBean tipoDocAmmessoRowBean) {
        DecTipoDocAmmesso tipoDocAmmesso = helper.findById(DecTipoDocAmmesso.class,
                tipoDocAmmessoRowBean.getIdTipoDocAmmesso());
        BigDecimal idTipoUnitaDoc = new BigDecimal(
                tipoDocAmmesso.getDecTipoStrutUnitaDoc().getDecTipoUnitaDoc().getIdTipoUnitaDoc());
        helper.removeEntity(tipoDocAmmesso, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, idTipoUnitaDoc,
                param.getNomePagina());
    }

    public DecCategTipoUnitaDocTableBean getDecCategTipoUnitaDocTableBean(Boolean firstLevel) {
        DecCategTipoUnitaDocTableBean tableBean = new DecCategTipoUnitaDocTableBean();
        List<DecCategTipoUnitaDoc> list = helper.getDecCategTipoUnitaDocList(firstLevel);
        if (list != null) {
            try {
                tableBean = (DecCategTipoUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return tableBean;
    }

    public DecCategTipoUnitaDocTableBean getDecCategTipoUnitaDocChildTableBean(BigDecimal idCategStrut) {
        DecCategTipoUnitaDocTableBean tableBean = new DecCategTipoUnitaDocTableBean();
        List<DecCategTipoUnitaDoc> list = helper.getDecCategTipoUnitaDocChildList(idCategStrut);
        if (list != null) {
            try {
                tableBean = (DecCategTipoUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return tableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertDecCategTipoUnitaDoc(DecCategTipoUnitaDocRowBean categTipoUnitaDocRowBean) throws ParerUserError {
        if (categTipoUnitaDocRowBean != null) {
            if (helper.getDecCategTipoUnitaDocByCodeLike(categTipoUnitaDocRowBean.getCdCategTipoUnitaDoc()) != null) {
                throw new ParerUserError("Codice categoria gi\u00E0 utilizzato all'interno del database.");
            }
        }
        DecCategTipoUnitaDoc categTipoUnitaDoc = (DecCategTipoUnitaDoc) Transform
                .rowBean2Entity(categTipoUnitaDocRowBean);
        helper.insertEntity(categTipoUnitaDoc, true);
    }

    public DecCategTipoUnitaDocRowBean getDecCategTipoUnitaDocRowBean(String nodeName) {
        DecCategTipoUnitaDocRowBean categTipoUnitaDocRowBean = new DecCategTipoUnitaDocRowBean();
        DecCategTipoUnitaDoc categTipoUnitaDoc = helper.getDecCategTipoUnitaDocByCodeLike(nodeName);
        try {
            categTipoUnitaDocRowBean = (DecCategTipoUnitaDocRowBean) Transform.entity2RowBean(categTipoUnitaDoc);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Eccezione", ex);
        }

        return categTipoUnitaDocRowBean;
    }

    public DecCategTipoUnitaDocRowBean getDecCategTipoUnitaDocRowBean(BigDecimal idNode) {
        DecCategTipoUnitaDocRowBean categTipoUnitaDocRowBean = new DecCategTipoUnitaDocRowBean();
        DecCategTipoUnitaDoc categTipoUnitaDoc = helper.findById(DecCategTipoUnitaDoc.class, idNode);
        try {
            categTipoUnitaDocRowBean = (DecCategTipoUnitaDocRowBean) Transform.entity2RowBean(categTipoUnitaDoc);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Eccezione", ex);
        }

        return categTipoUnitaDocRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecCategTipoUnitaDoc(String cdCategTipoUnitaDoc) throws ParerUserError {
        DecCategTipoUnitaDoc categToRemove = helper.getDecCategTipoUnitaDocByCodeLike(cdCategTipoUnitaDoc);
        BigDecimal idCategTipoUnitaDoc = new BigDecimal(categToRemove.getIdCategTipoUnitaDoc());

        if (nodeDecCategTipoUnitaDocHasChild(cdCategTipoUnitaDoc, idCategTipoUnitaDoc)) {
            throw new ParerUserError("Il nodo selezionato \u00E8 collegato ad altri nodi. Impossibile cancellare");
        }
        if (helper.getListDecCategTipoUnitaDocInUse(categToRemove.getIdCategTipoUnitaDoc()) != null) {
            throw new ParerUserError("Nodo utilizzato da Tipi Unita Documentarie. Impossibile cancellare");
        }

        helper.removeEntity(categToRemove, true);
    }

    private boolean nodeDecCategTipoUnitaDocHasChild(String cdCategTipoUnitaDoc, BigDecimal idToRemove) {
        if (idToRemove == null) {
            idToRemove = new BigDecimal(
                    helper.getDecCategTipoUnitaDocByCodeLike(cdCategTipoUnitaDoc).getIdCategTipoUnitaDoc());
        }
        if (helper.getDecCategTipoUnitaDocChildList(idToRemove) != null) {
            return true;
        }

        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateDecCategTipoUnitaDoc(BigDecimal idToUpdate, DecCategTipoUnitaDocRowBean categTipoUnitaDocRowBean)
            throws ParerUserError {
        DecCategTipoUnitaDoc categDB = helper
                .getDecCategTipoUnitaDocByCodeLike(categTipoUnitaDocRowBean.getCdCategTipoUnitaDoc());
        if ((categDB != null) && (categDB.getIdCategTipoUnitaDoc() != idToUpdate.longValue())) {
            throw new ParerUserError("Codice categoria gi\u00E0 utilizzato all'interno del database.");
        }

        DecCategTipoUnitaDoc categTipoUnitaDoc = struttureHelper.findById(DecCategTipoUnitaDoc.class, idToUpdate);
        DecCategTipoUnitaDoc categTipoUnitaDocPadre = (categTipoUnitaDocRowBean.getIdCategTipoUnitaDocPadre() != null
                ? struttureHelper.findById(DecCategTipoUnitaDoc.class,
                        categTipoUnitaDocRowBean.getIdCategTipoUnitaDocPadre())
                : null);
        categTipoUnitaDoc.setCdCategTipoUnitaDoc(categTipoUnitaDocRowBean.getCdCategTipoUnitaDoc());
        categTipoUnitaDoc.setDsCategTipoUnitaDoc(categTipoUnitaDocRowBean.getDsCategTipoUnitaDoc());
        categTipoUnitaDoc.setDecCategTipoUnitaDoc(categTipoUnitaDocPadre);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void moveDecCategTipoUDNode(BigDecimal nodeId, BigDecimal nodeDestId) throws ParerUserError {
        DecCategTipoUnitaDoc categDB = helper.findById(DecCategTipoUnitaDoc.class, nodeId);
        DecCategTipoUnitaDoc categDest = helper.findById(DecCategTipoUnitaDoc.class, nodeDestId);

        // se non vado a inserire nel nodo radice
        if (categDest != null) {
            // se \u00E8 nodo di secondo livello, non posso inserire oltre
            if (categDest.getDecCategTipoUnitaDoc() != null) {
                throw new ParerUserError("Impossibile inserire un nodo sotto questo livello.");
            }
            // se nodo destinazione di primo livello, controllo che il nodo selezionato non
            // abbia nodi figli
            // in quel caso non \u00E8 compatibile con l'operazione
            if (helper.getDecCategTipoUnitaDocChildList(nodeId) != null) {
                throw new ParerUserError("Nodo destinazione non compatibile con nodo selezionato");
            }

        }
        categDB.setDecCategTipoUnitaDoc(categDest);
    }

    public OrgTipoServizioTableBean getOrgTipoServizioTableBean(String tiClasseTipoServizio) {
        OrgTipoServizioTableBean tipoServizioTableBean = new OrgTipoServizioTableBean();
        try {
            // Recupero i tipi servizio in base alla classe
            List<OrgTipoServizio> tipiServizioList = helper.retrieveOrgTipoServizioList(tiClasseTipoServizio);

            if (!tipiServizioList.isEmpty()) {
                tipoServizioTableBean = (OrgTipoServizioTableBean) Transform.entities2TableBean(tipiServizioList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return tipoServizioTableBean;
    }

    public DecVCalcTiServOnTipoUdRowBean getDecVCalcTiServOnTipoUd(BigDecimal idStrut, BigDecimal idCategTipoUnitaDoc,
            String cdAlgoTariffario) {
        DecVCalcTiServOnTipoUdRowBean tipoServizioOnTipoUdRowBean = null;
        try {
            // Recupero il tipo servizio in base alla struttura e alla categoria tipo ud
            DecVCalcTiServOnTipoUd tipoServizioOnTipoUd = helper.getDecVCalcTiServOnTipoUd(idStrut, idCategTipoUnitaDoc,
                    cdAlgoTariffario);
            if (tipoServizioOnTipoUd != null) {
                tipoServizioOnTipoUdRowBean = (DecVCalcTiServOnTipoUdRowBean) Transform
                        .entity2RowBean(tipoServizioOnTipoUd);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return tipoServizioOnTipoUdRowBean;
    }

    public AplSistemaVersanteTableBean getAplSistemaVersanteTableBean(BigDecimal idTipoUnitaDoc) {
        AplSistemaVersanteTableBean sistemaVersanteTableBean = new AplSistemaVersanteTableBean();
        AplSistemaVersanteRowBean sistemaVersanteRowBean = new AplSistemaVersanteRowBean();
        try {
            List<Object[]> sistemiVersantiList = helper.retrieveAplSistemaVersanteListPerTipoUd(idTipoUnitaDoc);
            for (Object[] obj : sistemiVersantiList) {
                sistemaVersanteRowBean = (AplSistemaVersanteRowBean) Transform
                        .entity2RowBean((AplSistemaVersante) obj[0]);
                /*
                 * LS: su jboss le entity restitiscono sempre delle java.util.Date, non c'è più alcuna mediazione da
                 * parte di annotazioni come la "ORACLE_DATE" fatta su glassfish. I rowbean necessitano dei
                 * java.sql.Timestamp.
                 */
                Object dtFirstVers = obj[1];
                if (dtFirstVers != null && dtFirstVers instanceof java.util.Date) {
                    dtFirstVers = new Timestamp(((java.util.Date) dtFirstVers).getTime());
                }

                sistemaVersanteRowBean.setObject("dt_first_vers", dtFirstVers);
                sistemaVersanteTableBean.add(sistemaVersanteRowBean);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sistemaVersanteTableBean;
    }

    public AplSistemaVersanteTableBean getAplSistemaVersanteTableBean() {
        AplSistemaVersanteTableBean sistemaVersanteTableBean = new AplSistemaVersanteTableBean();
        try {
            List<AplSistemaVersante> sistemiVersantiList = helper.retrieveAplSistemaVersanteList();

            if (!sistemiVersantiList.isEmpty()) {
                sistemaVersanteTableBean = (AplSistemaVersanteTableBean) Transform
                        .entities2TableBean(sistemiVersantiList);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return sistemaVersanteTableBean;
    }

    public List<String> getIdSistemaVersanteListByTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        DecTipoStrutUnitaDoc tipiStrutUnitaDoc = helper.findById(DecTipoStrutUnitaDoc.class, idTipoStrutUnitaDoc);
        List<String> idSistemaVersanteList = new ArrayList<>();
        for (DecTipoStrutUdSisVer tipoStrutUdSisVer : tipiStrutUnitaDoc.getDecTipoStrutUdSisVers()) {
            idSistemaVersanteList.add("" + tipoStrutUdSisVer.getAplSistemaVersante().getIdSistemaVersante());
        }
        return idSistemaVersanteList;
    }

    public List<String> getIdRegistroUnitaDocListByTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        DecTipoStrutUnitaDoc tipiStrutUnitaDoc = helper.findById(DecTipoStrutUnitaDoc.class, idTipoStrutUnitaDoc);
        List<String> idRegistroUnitaDocList = new ArrayList<>();
        for (DecTipoStrutUdReg tipoStrutUdReg : tipiStrutUnitaDoc.getDecTipoStrutUdRegs()) {
            idRegistroUnitaDocList.add("" + tipoStrutUdReg.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc());
        }
        return idRegistroUnitaDocList;
    }

    public List<String> getIdXsdDatiSpecListByTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        DecTipoStrutUnitaDoc tipiStrutUnitaDoc = helper.findById(DecTipoStrutUnitaDoc.class, idTipoStrutUnitaDoc);
        List<String> idXsdDatiSpecList = new ArrayList<>();
        for (DecTipoStrutUdXsd tipoStrutUdXsd : tipiStrutUnitaDoc.getDecTipoStrutUdXsds()) {
            idXsdDatiSpecList.add("" + tipoStrutUdXsd.getDecXsdDatiSpec().getIdXsdDatiSpec());
        }
        return idXsdDatiSpecList;
    }

    public boolean isAccordoPerCampiNuovaFatturazione(BigDecimal idEnteConvenz) {
        return helper.isAccordoPerNuovaFatturazione(idEnteConvenz);
    }

}
