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

package it.eng.parer.amministrazioneStrutture.gestioneRegistro.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper.RegistroHelper;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoUd.ejb.TipoUnitaDocEjb;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecErrAaRegistroUnitaDoc;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.parer.entity.DecParteNumeroRegistro;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoSerieUd;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDocAmmesso;
import it.eng.parer.entity.DecWarnAaRegistroUd;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.parer.entity.SerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.serie.dto.IntervalliSerieAutomBean;
import it.eng.parer.serie.dto.SerieAutomBean;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.ejb.TipoSerieEjb;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.serie.helper.TipoSerieHelper;
import it.eng.parer.slite.gen.tablebean.DecAaRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecAaRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecErrAaRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecErrAaRegistroUnitaDocTableBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroRegistroRowBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroRegistroTableBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocRowBean;
import it.eng.parer.slite.gen.tablebean.DecRegistroUnitaDocTableBean;
import it.eng.parer.viewEntity.DecVCreaCritRaggrRegistro;
import it.eng.parer.viewEntity.DecVLisTiUniDocAms;
import it.eng.parer.web.ejb.CriteriRaggruppamentoEjb;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.KeyOrdUtility;
import it.eng.parer.ws.utils.KeySizeUtility;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
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
 * EJB di gestione dei registri
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneRegistro}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class RegistroEjb {

    private static final Logger logger = LoggerFactory.getLogger(RegistroEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private CriteriRaggruppamentoEjb critEjb;
    @EJB
    private CriteriRaggrHelper crHelper;
    @EJB
    private RegistroHelper helper;
    @EJB
    private TipoUnitaDocEjb tipoUnitaDocEjb;
    @EJB
    private TipoSerieEjb tipoSerieEjb;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private TipoSerieHelper tipoSerieHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB
    private UnitaDocumentarieHelper unitaDocHelper;

    /**
     * Verifica se almeno un tipo ud associato al registro ha “fl_crea_tipo_serie_standard” = SI
     *
     * @param idRegistroUnitaDoc
     *            registro unita doc
     * 
     * @return true se esiste una tipologia di unità documentaria con flCreaTipoSerieStandard a TRUE
     */
    public boolean checkTipoSerieStandardForTipiUdAmmessi(BigDecimal idRegistroUnitaDoc) {
        boolean result = false;
        DecRegistroUnitaDoc registro = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        for (DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso : registro.getDecTipoUnitaDocAmmessos()) {
            if (tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getFlCreaTipoSerieStandard() != null
                    && tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getFlCreaTipoSerieStandard().equals("1")) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Calcola la massima dimesione possibile per la componente Numero della chiave UD legata a questo registro, in
     * funzione della chiave di ordinamento calcolata e dell'URN UD (per praticità non viene presa in considerazione la
     * lunghezza dell'eventuale path di memorizzazione su nastro, valida solo per i versamenti salvati su Tivoli).
     *
     * @param bean
     *            raw bean con dati registro
     * 
     * @return la dimensione massima calcolata
     */
    public String calcolaMaxLenNumeroChiave(DecRegistroUnitaDocRowBean bean) {
        CSVersatore csv = helper.getVersatoreRegistroUd(bean.getIdRegistroUnitaDoc());
        CSChiave csc = new CSChiave();
        csc.setAnno(1971L); // basta che sia lungo 4 cifre
        csc.setTipoRegistro(bean.getCdRegistroUnitaDoc());
        csc.setNumero("dummy"); // questa informazione verrà ignorata
        // fornisco null come parametro per i dati relativi al TPI,
        // Non posso sapere se su questo registro verranno caricate UD
        // avente tipoUD con salvataggio su file.
        KeySizeUtility ksu = new KeySizeUtility(csv, csc, null);

        return String.valueOf(ksu.getMaxLenNumero());
    }

    /**
     * Metodo di salvataggio e replica su IAM di un nuovo registro
     *
     * @param param
     *            parametro
     * @param registroUnitaDocRowBean
     *            bean contenente i dati del registro
     * @param criterioAutomRegistro
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void insertDecRegistroUnitaDoc(LogParam param, DecRegistroUnitaDocRowBean registroUnitaDocRowBean,
            String criterioAutomRegistro) throws ParerUserError {
        RegistroEjb me = context.getBusinessObject(RegistroEjb.class);
        try {
            IamOrganizDaReplic replic = me.saveDecRegistroUnitaDoc(param, null, registroUnitaDocRowBean,
                    StruttureEjb.TipoOper.INS, criterioAutomRegistro, null, null, null, null);
            if (replic != null) {
                struttureEjb.replicateToIam(replic);
            }
        } catch (ParerWarningException ex) {
            // Fittizio, non ci entrerà mai per il caso di inserimento. BonnieApproved per presa visione.
        }
    }

    /**
     * Metodo di duplicazione e replica su IAM di un nuovo registro
     *
     * @param param
     *            parametro
     * @param registroUnitaDocRowBean
     *            bean contenente i dati del registro
     * @param criterioAutomRegistro
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void duplicaRegistroUnitaDoc(LogParam param, DecRegistroUnitaDocRowBean registroUnitaDocRowBean,
            String criterioAutomRegistro) throws ParerUserError {
        RegistroEjb me = context.getBusinessObject(RegistroEjb.class);
        try {
            IamOrganizDaReplic replic = me.saveDecRegistroUnitaDoc(param, null, registroUnitaDocRowBean,
                    StruttureEjb.TipoOper.DUPLICA_REGISTRO, criterioAutomRegistro, null, null, null, null);
            struttureEjb.replicateToIam(replic);
        } catch (ParerWarningException ex) {
            // Fittizio, non ci entrerà mai per il caso di inserimento. BonnieApproved per presa visione.
        }
    }

    /**
     * Metodo di modifica e replica su IAM di un nuovo registro
     *
     * @param param
     *            parametro
     * @param azioneInsCriterio
     *            azione da inserire su criterio
     * @param azioneModCriterio
     *            azione da modificare su criterio
     * @param idRegistroUnitaDoc
     *            id del registro da modificare
     * @param registroUnitaDocRowBean
     *            bean contenente i dati del registro
     * @param criterioAutomRegistro
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * 
     * @throws ParerUserError
     *             errore generico
     * @throws ParerWarningException
     *             errore generico
     */
    public void updateDecRegistroUnitaDoc(LogParam param, String azioneInsCriterio, String azioneModCriterio,
            BigDecimal idRegistroUnitaDoc, DecRegistroUnitaDocRowBean registroUnitaDocRowBean,
            String criterioAutomRegistro) throws ParerUserError, ParerWarningException {
        RegistroEjb me = context.getBusinessObject(RegistroEjb.class);
        // Ricavo il flag fiscale del registro prima delle modifiche che andrò ad apportare
        DecRegistroUnitaDoc registroUnitaDocDB = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        String flRegistroFiscaleDB = registroUnitaDocDB.getFlRegistroFisc();
        List<DecTipoUnitaDocAmmesso> tipoUnitaDocAmmessoList = registroUnitaDocDB.getDecTipoUnitaDocAmmessos();

        IamOrganizDaReplic replic = me.saveDecRegistroUnitaDoc(param, idRegistroUnitaDoc, registroUnitaDocRowBean,
                StruttureEjb.TipoOper.MOD, criterioAutomRegistro, azioneInsCriterio, azioneModCriterio,
                flRegistroFiscaleDB, tipoUnitaDocAmmessoList);

        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }

    }

    /**
     * Metodo che esegue il salvataggio del registro su database dati i parametri
     *
     * @param param
     *            parametro
     * @param idRegistroUnitaDoc
     *            id del registro da modificare (se != null)
     * @param registroUnitaDocRowBean
     *            bean contenente i dati del registro
     * @param tipoOper
     *            tipo di operazione eseguita
     * @param criterioAutomRegistro
     *            flag relativo alla creazione automatica del criterio di raggruppamento
     * @param azioneInsCriterio
     *            azione da inserire su criterio
     * @param azioneModCriterio
     *            azione da mofificare su creterio
     * @param flRegistroFiscaleDB
     *            si/no
     * @param tipoUnitaDocAmmessoList
     *            lista unita doc ammessa
     * 
     * @return l'entity IamOrganizDaReplic con cui eseguire la replica su IAM
     * 
     * @throws ParerUserError
     *             errore generico
     * @throws ParerWarningException
     *             errore generico
     */
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveDecRegistroUnitaDoc(LogParam param, BigDecimal idRegistroUnitaDoc,
            DecRegistroUnitaDocRowBean registroUnitaDocRowBean, StruttureEjb.TipoOper tipoOper,
            String criterioAutomRegistro, String azioneInsCriterio, String azioneModCriterio,
            String flRegistroFiscaleDB, List<DecTipoUnitaDocAmmesso> tipoUnitaDocAmmessoList)
            throws ParerUserError, ParerWarningException {
        OrgStrut struttura = new OrgStrut();
        DecRegistroUnitaDoc registroUnitaDoc = new DecRegistroUnitaDoc();
        ApplEnum.TiOperReplic tiOper = null;
        boolean modificatiNomeDescrizione = false;
        int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
        /* INSERIMENTO REGISTRO */
        if (tipoOper.name().equals(StruttureEjb.TipoOper.INS.name())) {
            struttura = helper.findById(OrgStrut.class, registroUnitaDocRowBean.getIdStrut());
            if (struttura.getDecRegistroUnitaDocs() == null) {
                struttura.setDecRegistroUnitaDocs(new ArrayList<DecRegistroUnitaDoc>());
            }
            if (helper.getDecRegistroUnitaDocByName(registroUnitaDocRowBean.getCdRegistroUnitaDoc(),
                    registroUnitaDocRowBean.getIdStrut()) != null) {
                throw new ParerUserError("Registro gi\u00e0 esistente all'interno della struttura</br>");
            }

            while (helper.existsCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz(),
                    registroUnitaDocRowBean.getIdStrut(), null)) {
                // throw new ParerUserError("Il tipo registro normalizzato non è univoco</br>");
                registroUnitaDocRowBean
                        .setCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz().concat("_"));
            }

            BigDecimal idModelloTipoSerie = registroUnitaDocRowBean.getIdModelloTipoSerie();

            registroUnitaDoc = (DecRegistroUnitaDoc) Transform.rowBean2Entity(registroUnitaDocRowBean);
            registroUnitaDoc.setOrgStrut(struttura);
            if (idModelloTipoSerie != null) {
                DecModelloTipoSerie modelloTipoSerieNew = helper.findById(DecModelloTipoSerie.class,
                        idModelloTipoSerie);
                registroUnitaDoc.setDecModelloTipoSerie(modelloTipoSerieNew);
            }
            helper.insertEntity(registroUnitaDoc, true);
            tiOper = ApplEnum.TiOperReplic.MOD;
            // Inserisco range di validit\u00E0
            DecAaRegistroUnitaDoc aaRegistroUnitaDoc = new DecAaRegistroUnitaDoc();
            aaRegistroUnitaDoc.setAaMinRegistroUnitaDoc(new BigDecimal(annoCorrente));
            aaRegistroUnitaDoc.setDecRegistroUnitaDoc(registroUnitaDoc);
            aaRegistroUnitaDoc.setFlUpdFmtNumero("0");
            aaRegistroUnitaDoc.setCdFormatoNumero("DEFAULT");
            aaRegistroUnitaDoc.setDsFormatoNumero("DEFAULT");
            helper.insertEntity(aaRegistroUnitaDoc, true);
            struttura.getDecRegistroUnitaDocs().add(registroUnitaDoc);
            modificatiNomeDescrizione = true;
            DecParteNumeroRegistro parteDefault = new DecParteNumeroRegistro();
            parteDefault.setDecAaRegistroUnitaDoc(aaRegistroUnitaDoc);
            parteDefault.setNiParteNumeroRegistro(new BigDecimal(1));
            parteDefault.setNmParteNumeroRegistro("DEFAULT");
            parteDefault.setDsParteNumeroRegistro("DEFAULT");
            parteDefault.setTiCharParte("NUMERICO");
            parteDefault.setNiMinCharParte(new BigDecimal(1));
            parteDefault.setTiPadSxParte("STANDARD");
            helper.insertEntity(parteDefault, true);
            aaRegistroUnitaDoc.getDecParteNumeroRegistros().add(parteDefault);
            /*
             * TODO: Inserire anche la parte di default associata al periodo di validit\u00E0 creato al punto precedente
             * con le seguenti caratteristiche: a. Numero d?ordine (ni_parte_numero_registro) = 1 b. Nome
             * (nm_parte_numero_registro)= DEFAULT c. Descrizione = DEFAULT d. Caratteri ammessi (ti_char_parte) =
             * NUMERICO e. Numero minimo di caratteri della parte (ni_min_char_parte) = 1 f. Numero massimo di caratteri
             * della parte (ni_max_char_parte) = Non definito g. Carattere separatore (ti_char_sep) = Non definito h.
             * Tipo di riempimento (ti_pad_sx_parte) = STANDARD i. Valori accettati (dl_valori_parte) = Non definiti j.
             * Coincidenza con l?anno della chiave dell?unit\u00E0 documentaria (flag fl_aa_key_unita_doc) = Flag Non
             * selezionato k. Parte da utilizzare per i controlli di consecutivit\u00E0 (flag fl_parte_progr) = Flag Non
             * selezionato
             */

            // Inserito per loggare la foto del registro
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                    new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()), param.getNomePagina());

        } else if (tipoOper.name().equals(StruttureEjb.TipoOper.MOD.name())) {
            /* MODIFICA REGISTRO */
            DecRegistroUnitaDoc dbRegistroUnitaDoc = helper.getDecRegistroUnitaDocByName(
                    registroUnitaDocRowBean.getCdRegistroUnitaDoc(), registroUnitaDocRowBean.getIdStrut());
            if (dbRegistroUnitaDoc != null
                    && dbRegistroUnitaDoc.getIdRegistroUnitaDoc() != idRegistroUnitaDoc.longValue()) {
                throw new ParerUserError(
                        "Nome Registro gi\u00e0 associato a questa struttura all'interno del database</br>");
            }
            while (helper.existsCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz(),
                    registroUnitaDocRowBean.getIdStrut(), idRegistroUnitaDoc)) {
                // throw new ParerUserError("Il tipo registro normalizzato non è univoco</br>");
                registroUnitaDocRowBean
                        .setCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz().concat("_"));
            }
            struttura = helper.findById(OrgStrut.class, registroUnitaDocRowBean.getIdStrut());
            /* Controllo se sono stati modificati nome e/o descrizione */
            registroUnitaDoc = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
            boolean modificaNome;
            if ((modificaNome = !registroUnitaDoc.getCdRegistroUnitaDoc()
                    .equals(registroUnitaDocRowBean.getCdRegistroUnitaDoc()))
                    || !registroUnitaDoc.getDsRegistroUnitaDoc()
                            .equals(registroUnitaDocRowBean.getDsRegistroUnitaDoc())) {
                modificatiNomeDescrizione = true;
            }
            if (modificaNome) {
                // Se è stato modificato il nome, devo modificare di conseguenza i tipi serie da modello associati (e le
                // sue serie, se hanno un contenuto pari a 0)
                List<DecTipoSerie> retrieveDecTipoSerieForRegistro = tipoSerieHelper
                        .retrieveDecTipoSerieForRegistro(registroUnitaDocRowBean.getIdStrut(), idRegistroUnitaDoc);
                for (DecTipoSerie tipoSerie : retrieveDecTipoSerieForRegistro) {
                    DecModelloTipoSerie modelloTipoSerie;
                    if ((modelloTipoSerie = tipoSerie.getDecModelloTipoSerie()) != null) {
                        for (DecTipoSerieUd tipoSerieUd : tipoSerie.getDecTipoSerieUds()) {
                            DecTipoUnitaDoc tipoUnitaDoc = tipoSerieUd.getDecTipoUnitaDoc();
                            String nmTipoSerie = tipoSerieEjb.getNmTipoSerieForModello(modelloTipoSerie,
                                    registroUnitaDoc, tipoUnitaDoc, tipoSerie.getTiCreaStandard(),
                                    registroUnitaDocRowBean.getCdRegistroUnitaDoc());
                            // Verifico che non esista già un tipo serie con questo nome
                            if (StringUtils.isNotBlank(nmTipoSerie) && tipoSerieHelper
                                    .getDecTipoSerieByName(nmTipoSerie, struttura.getIdStrut()) == null) {
                                String dsTipoSerie = tipoSerieEjb.getDsTipoSerieForModello(modelloTipoSerie,
                                        registroUnitaDoc, tipoUnitaDoc, tipoSerie.getTiCreaStandard(),
                                        registroUnitaDocRowBean.getCdRegistroUnitaDoc());
                                String cdSerie = tipoSerieEjb.getCdSerieForModello(modelloTipoSerie, registroUnitaDoc,
                                        tipoUnitaDoc, tipoSerie.getTiCreaStandard(),
                                        registroUnitaDocRowBean.getCdRegistroUnitaDoc());
                                String dsSerie = tipoSerieEjb.getDsSerieForModello(modelloTipoSerie, registroUnitaDoc,
                                        tipoUnitaDoc, tipoSerie.getTiCreaStandard(),
                                        registroUnitaDocRowBean.getCdRegistroUnitaDoc());
                                tipoSerie.setNmTipoSerie(nmTipoSerie);
                                tipoSerie.setDsTipoSerie(dsTipoSerie);
                                tipoSerie.setCdSerieDefault(cdSerie);
                                tipoSerie.setDsSerieDefault(dsSerie);

                                List<SerVerSerie> versioniSerieCorrentiByTipoSerie = serieHelper
                                        .getVersioniSerieCorrentiByTipoSerie(tipoSerie.getIdTipoSerie());
                                // Verifico se la serie è annuale o infraAnnuale
                                boolean serieAnnuale = (tipoSerie.getNiMmCreaAutom() == null);
                                Set<BigDecimal> anniCalcolati = new HashSet<>();

                                SerieAutomBean creaAutomBean = null;
                                for (SerVerSerie verSerie : versioniSerieCorrentiByTipoSerie) {
                                    SerSerie serie = verSerie.getSerSerie();

                                    BigDecimal niMesiCreazioneSerie = tipoSerie.getNiMmCreaAutom();

                                    if (anniCalcolati.add(serie.getAaSerie())) {
                                        creaAutomBean = serieEjb.generateIntervalliSerieAutom(niMesiCreazioneSerie,
                                                tipoSerie.getCdSerieDefault(), tipoSerie.getDsSerieDefault(),
                                                serie.getAaSerie().intValue(), tipoSerie.getTiSelUd());
                                    }
                                    if (!serieAnnuale) {
                                        IntervalliSerieAutomBean tmpBean = new IntervalliSerieAutomBean(
                                                verSerie.getDtInizioSelSerie(), verSerie.getDtFineSelSerie(), null,
                                                null);
                                        IntervalliSerieAutomBean newInterval;
                                        int indexInterval;
                                        if (creaAutomBean != null && (indexInterval = creaAutomBean.getIntervalli()
                                                .indexOf(tmpBean)) != -1) {
                                            newInterval = creaAutomBean.getIntervalli().get(indexInterval);

                                            serie.setCdCompositoSerie(newInterval.getCdSerie());
                                            serie.setDsSerie(newInterval.getDsSerie());
                                        } else {
                                            throw new ParerUserError(
                                                    "Possibile inconsistenza delle serie create rispetto al tipo serie. Potrebbe essere necessario un ricalcolo delle stesse prima di modificare il registro");
                                        }
                                    } else {
                                        IntervalliSerieAutomBean newInterval = creaAutomBean.getIntervalli().get(0);
                                        serie.setCdCompositoSerie(newInterval.getCdSerie());
                                        serie.setDsSerie(newInterval.getDsSerie());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            BigDecimal idModelloTipoSerie = registroUnitaDocRowBean.getIdModelloTipoSerie();
            if (idModelloTipoSerie != null) {
                DecModelloTipoSerie modelloTipoSerieNew = helper.findById(DecModelloTipoSerie.class,
                        idModelloTipoSerie);
                if (registroUnitaDoc.getDecModelloTipoSerie() != null) {
                    if (registroUnitaDoc.getDecModelloTipoSerie().getIdModelloTipoSerie() != idModelloTipoSerie
                            .longValue()) {
                        DecModelloTipoSerie modelloTipoSerieOld = registroUnitaDoc.getDecModelloTipoSerie();
                        List<DecTipoSerie> tipiSerieVecchioModello = tipoSerieHelper.retrieveDecTipoSerieList(
                                registroUnitaDoc.getOrgStrut().getIdStrut(),
                                modelloTipoSerieOld.getIdModelloTipoSerie());

                        boolean presenzaTiCreaStandard = false;
                        // Se il modello che si intende dismettere NON è collegato a tipologie di serie attive che hanno
                        // il flag "ti_crea_standard" = "BASATA_SU_REGISTRO" la modifica è consentita
                        for (DecTipoSerie tipoSerie : tipiSerieVecchioModello) {
                            if (tipoSerie.getTiCreaStandard()
                                    .equals(CostantiDB.TipoSerieCreaStandard.BASATA_SU_REGISTRO.name())) {
                                presenzaTiCreaStandard = true;
                                break;
                            }
                        }
                        if (presenzaTiCreaStandard) {
                            throw new ParerUserError(
                                    "Il modello associato al registro \u00E8 associato a tipi di serie basate sul registro; modifica del modello non consentita");
                        }
                    }
                }
                registroUnitaDoc.setDecModelloTipoSerie(modelloTipoSerieNew);
            }

            // Se il modello è stato tolto
            if (registroUnitaDoc.getDecModelloTipoSerie() != null && idModelloTipoSerie == null) {
                registroUnitaDoc.setDecModelloTipoSerie(null);
            }

            registroUnitaDoc.setCdRegistroUnitaDoc(registroUnitaDocRowBean.getCdRegistroUnitaDoc());
            registroUnitaDoc.setCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz());
            registroUnitaDoc.setDsRegistroUnitaDoc(registroUnitaDocRowBean.getDsRegistroUnitaDoc());
            registroUnitaDoc.setDtIstituz(registroUnitaDocRowBean.getDtIstituz());
            registroUnitaDoc.setDtSoppres(registroUnitaDocRowBean.getDtSoppres());
            registroUnitaDoc.setFlRegistroFisc(registroUnitaDocRowBean.getFlRegistroFisc());
            registroUnitaDoc.setFlCreaSerie(registroUnitaDocRowBean.getFlCreaSerie());
            registroUnitaDoc.setFlCreaTipoSerieStandard(registroUnitaDocRowBean.getFlCreaTipoSerieStandard());
            registroUnitaDoc.setNmTipoSerieDaCreare(registroUnitaDocRowBean.getNmTipoSerieDaCreare());
            registroUnitaDoc.setDsTipoSerieDaCreare(registroUnitaDocRowBean.getDsTipoSerieDaCreare());
            registroUnitaDoc.setCdSerieDaCreare(registroUnitaDocRowBean.getCdSerieDaCreare());
            registroUnitaDoc.setDsSerieDaCreare(registroUnitaDocRowBean.getDsSerieDaCreare());
            registroUnitaDoc.setNiAnniConserv(registroUnitaDocRowBean.getNiAnniConserv());

            tiOper = ApplEnum.TiOperReplic.MOD;

            // Inserito per loggare la foto del registro
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO, idRegistroUnitaDoc,
                    param.getNomePagina());

            // Gestione fiscalità
            List<DecCriterioRaggr> criteri = crHelper.getCriteriPerTipoDato(idRegistroUnitaDoc.longValue(),
                    TipoDato.REGISTRO, "1", null);
            if (!criteri.isEmpty()) {
                // Se il criterio standard sul registro esiste, e l'indicatore registro fiscale è stato modificato
                // aggiorno il criterio con la nuova fiscalità se è cambiata
                if (!criteri.get(0).getFlCriterioRaggrFisc().equals(registroUnitaDocRowBean.getFlRegistroFisc())) {
                    param.setNomeAzione(azioneModCriterio);
                    criteri.get(0).setFlCriterioRaggrFisc(registroUnitaDocRowBean.getFlRegistroFisc());
                    sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                            param.getNomeUtente(), param.getNomeAzione(),
                            SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                            BigDecimal.valueOf(criteri.get(0).getIdCriterioRaggr()), param.getNomePagina());
                    helper.getEntityManager().flush();
                }
            }

            StringBuilder warningMessage = new StringBuilder();
            // Se il registro è diventato FISCALE (prima era non fiscale...)
            if (flRegistroFiscaleDB.equals("0") && registroUnitaDocRowBean.getFlRegistroFisc().equals("1")) {
                // Per ogni tipo unita doc associato al registro
                for (DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso : tipoUnitaDocAmmessoList) {
                    long idTipoUnitaDoc = tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc();
                    boolean areAllRegistriFiscali = critEjb
                            .checkAllRegistriAssociatiFiscali(BigDecimal.valueOf(idTipoUnitaDoc));
                    // Verifico se non esiste un criterio valido per il tipo unita doc...
                    if (!critEjb.existsCriterioPerTipoDato(idTipoUnitaDoc, TipoDato.TIPO_UNITA_DOC)) {
                        // ...ed il tipo unita doc è associato solo a registri fiscale
                        if (areAllRegistriFiscali) {
                            // Verifico se esiste un criterio valido per il registro
                            // Se NON esiste
                            if (!critEjb.existsCriterioPerTipoDato(idRegistroUnitaDoc.longValue(), TipoDato.REGISTRO)) {
                                param.setNomeAzione(azioneInsCriterio);
                                if (tipoUnitaDocEjb.creaCriterioSulTipoUnitaDoc(param,
                                        BigDecimal.valueOf(idTipoUnitaDoc))) {
                                    warningMessage.append("Poiché per il tipo di unità documentaria ");
                                    warningMessage.append(tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                                    warningMessage.append(
                                            " associata al registro non è stato rilevato alcun criterio e neanche per il registro è stato rilevato alcun criterio, ");
                                    warningMessage.append(
                                            "ed il registro è fiscale, si è provveduto a creare un criterio standard e fiscale per il tipo unità documentaria.");
                                    warningMessage.append("<br>");
                                }
                            }
                        }
                    } // Se esiste
                    else {
                        // Determino l'eventuale criterio standard e non fiscale
                        List<DecCriterioRaggr> criteriEsistenti = crHelper.getCriteriPerTipoDato(idTipoUnitaDoc,
                                TipoDato.TIPO_UNITA_DOC, "1", "0");
                        if (!criteriEsistenti.isEmpty()) {
                            param.setNomeAzione(azioneModCriterio);
                            criteriEsistenti.get(0).setFlCriterioRaggrFisc("1");
                            helper.getEntityManager().flush();
                            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                                    param.getNomeUtente(), param.getNomeAzione(),
                                    SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                                    BigDecimal.valueOf(criteriEsistenti.get(0).getIdCriterioRaggr()),
                                    param.getNomePagina());
                            warningMessage.append("Poiché per il tipo di unità documentaria ");
                            warningMessage.append(tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                            warningMessage.append(
                                    " è stato rilevato un criterio standard e non fiscale ed il registro associato è fiscale, ");
                            warningMessage.append("si è provveduto a modificare il criterio definendolo fiscale.");
                            warningMessage.append("<br>");
                        }
                    }
                }
            }

            // Di nuovo, per ogni tipo unità doc associato al registro
            // Per ogni tipo unita doc associato al registro
            for (DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso : tipoUnitaDocAmmessoList) {
                long idTipoUnitaDoc = tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getIdTipoUnitaDoc();
                // Ricavo gli eventuali criteri non coerenti
                List<String> criteriNonCoerenti = crHelper.getCriteriNonCoerenti(BigDecimal.valueOf(idTipoUnitaDoc));
                if (!criteriNonCoerenti.isEmpty()) {
                    StringBuilder errorMessage = new StringBuilder();
                    errorMessage.append("Poiché il registro è ");
                    errorMessage.append(
                            registroUnitaDocRowBean.getFlRegistroFisc().equals("1") ? "fiscale " : "non fiscale ");
                    errorMessage.append(
                            "i seguenti criteri non sono coerenti con l’associazione fra il tipo di unità documentaria ");
                    errorMessage.append(tipoUnitaDocAmmesso.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    for (String criterioNonCoerente : criteriNonCoerenti) {
                        errorMessage.append("<br>").append(criterioNonCoerente);
                    }
                    // Lancia eccezione e rolbecca
                    throw new ParerUserError(errorMessage.toString());
                }
            }

            if (warningMessage.length() > 0) {
                // Lancia eccezione warning senza rolbeccare
                throw new ParerWarningException(warningMessage.toString(), "warningModificaRegistro");
            }
        } else if (tipoOper.name().equals(StruttureEjb.TipoOper.DUPLICA_REGISTRO.name())) {
            // DUPLICA REGISTRO
            if (helper.getDecRegistroUnitaDocByName(registroUnitaDocRowBean.getCdRegistroUnitaDoc(),
                    registroUnitaDocRowBean.getIdStrut()) != null) {
                throw new ParerUserError(
                        "Registro gi\u00e0 presente all'interno del database per questa struttura. Operazione annullata!");
            }

            while (helper.existsCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz(),
                    registroUnitaDocRowBean.getIdStrut(), null)) {
                // throw new ParerUserError("Il tipo registro normalizzato non è univoco</br>");
                registroUnitaDocRowBean
                        .setCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz().concat("_"));
            }

            try {
                // Recupero il registro "originale"
                DecRegistroUnitaDoc registroOriginale = helper.findById(DecRegistroUnitaDoc.class,
                        registroUnitaDocRowBean.getIdRegistroUnitaDoc().longValue());
                // Duplico la entity
                registroUnitaDoc = duplicaRegistroEntity(registroOriginale);
                // Inserisco nell'entity duplicata i valori dei campi eventualmente cambiati nell'online
                registroUnitaDoc.setCdRegistroNormaliz(registroUnitaDocRowBean.getCdRegistroNormaliz());
                registroUnitaDoc.setDsRegistroUnitaDoc(registroUnitaDocRowBean.getDsRegistroUnitaDoc());
                registroUnitaDoc.setFlRegistroFisc(registroUnitaDocRowBean.getFlRegistroFisc());
                registroUnitaDoc.setFlCreaSerie(registroUnitaDocRowBean.getFlCreaSerie());
                registroUnitaDoc.setNiAnniConserv(registroUnitaDocRowBean.getNiAnniConserv());
                registroUnitaDoc.setDtIstituz(registroUnitaDocRowBean.getDtIstituz());
                registroUnitaDoc.setDtSoppres(registroUnitaDocRowBean.getDtSoppres());
                registroUnitaDoc.setCdRegistroUnitaDoc(registroUnitaDocRowBean.getCdRegistroUnitaDoc());
                registroUnitaDoc.setFlTipoSerieMult("0");

                for (DecAaRegistroUnitaDoc aaReg : registroUnitaDoc.getDecAaRegistroUnitaDocs()) {
                    for (DecParteNumeroRegistro parte : aaReg.getDecParteNumeroRegistros()) {
                        if (parte.getTiCharSep() != null && parte.getTiCharSep().isEmpty()) {
                            parte.setTiCharSep(" ");
                        }
                    }
                }
                helper.insertEntity(registroUnitaDoc, true);

                // Inserito per loggare la foto del registro
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                        new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()), param.getNomePagina());

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new ParerUserError("Errore durante la duplicazione del registro");
            }
            tiOper = ApplEnum.TiOperReplic.MOD;
            modificatiNomeDescrizione = true;
            registroUnitaDocRowBean.setIdRegistroUnitaDoc(new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()));
            struttura = helper.findById(OrgStrut.class, registroUnitaDoc.getOrgStrut().getIdStrut());
        }

        /*
         * Se l’utente ha selezionato il check “Creazione in automatico del criterio di raggruppamento sul registro” il
         * sistema esegue la creazione in automatico del criterio di raggruppamento standard NON automatico
         */
        if (criterioAutomRegistro != null && criterioAutomRegistro.equals("1")) {
            // FIXME: Non necessario questo metodo, basta fare una findById
            // PG: ma io non sono così sicuro che sulla vista tu possa fare la find, caro adc, cmq prova quando vuoi...
            DecVCreaCritRaggrRegistro creaCritRegistro = crHelper
                    .getDecVCreaCritRaggrRegistro(registroUnitaDoc.getIdRegistroUnitaDoc());
            if (crHelper.existNomeCriterio(creaCritRegistro.getNmCriterioRaggr(),
                    new BigDecimal(struttura.getIdStrut()))) {
                throw new ParerUserError(
                        "Attenzione: non \u00e8 possibile terminare l'operazione in quanto si richiedere la creazione in automatico di un criterio di raggruppamento il cui nome, per questa struttura, \u00e8 gi\u00e0 presente nel DB");
            }
            critEjb.salvataggioAutomaticoCriterioRaggrStdNoAutomRegistro(param, creaCritRegistro);
        }
        /*
         * La replica va fatta solo se il registro appartiene ad una struttura non appartenente ad un ente di tipo
         * template e se sono stati modificati nome e/o descrizione
         */
        IamOrganizDaReplic replic = null;
        if (modificatiNomeDescrizione && (struttura.getOrgEnte().getTipoDefTemplateEnte()
                .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
                || struttura.getOrgEnte().getTipoDefTemplateEnte()
                        .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))) {
            replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, tiOper);
        }

        return replic;
    }
    //
    // private DecRegistroUnitaDoc duplicaRegistroEntity(DecRegistroUnitaDoc registroOriginale)
    // throws NoSuchFieldException {
    // CopyGroup group = new CopyGroup();
    // // Imposto il reset delle chiavi primarie
    // group.setShouldResetPrimaryKey(true);
    // // Aggiungo al CopyGroup i parametri da duplicare
    // group.addAttribute("cdRegistroUnitaDoc");
    // group.addAttribute("dsRegistroUnitaDoc");
    // group.addAttribute("dtIstituz");
    // group.addAttribute("dtSoppres");
    // group.addAttribute("flCreaSerie");
    // group.addAttribute("flRegistroFisc");
    // group.addAttribute("niAnniConserv");
    // group.addAttribute("flCreaSerie");
    // group.addAttribute("flCreaTipoSerieStandard");
    // group.addAttribute("decModelloTipoSerie");
    // group.addAttribute("nmTipoSerieDaCreare");
    // group.addAttribute("dsTipoSerieDaCreare");
    // group.addAttribute("cdSerieDaCreare");
    // group.addAttribute("dsSerieDaCreare");
    //
    // // Attributi di decAaRegistroUnitaDoc da duplicare (esclusa la chiave)
    // group.addAttribute("decAaRegistroUnitaDocs.aaMaxRegistroUnitaDoc");
    // group.addAttribute("decAaRegistroUnitaDocs.aaMinRegistroUnitaDoc");
    // group.addAttribute("decAaRegistroUnitaDocs.flUpdFmtNumero");
    // group.addAttribute("decAaRegistroUnitaDocs.decRegistroUnitaDoc");
    // group.addAttribute("decAaRegistroUnitaDocs.decErrAaRegistroUnitaDocs");
    // group.addAttribute("decAaRegistroUnitaDocs.decWarnAaRegistroUds");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros");
    // // Attributi di decParteNumeroRegistros da duplicare (esclusa la chiave)
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.dlValoriParte");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.dsParteNumeroRegistro");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.niMaxCharParte");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.niMinCharParte");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.niParteNumeroRegistro");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.nmParteNumeroRegistro");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.tiCharParte");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.tiCharSep");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.tiPadSxParte");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.tiParte");
    // group.addAttribute("decAaRegistroUnitaDocs.decParteNumeroRegistros.decAaRegistroUnitaDoc");
    // // Attributi di decTipoUnitaDocAmmessos da duplicare (esclusa la chiave)
    // group.addAttribute("decTipoUnitaDocAmmessos.decRegistroUnitaDoc");
    // group.addAttribute("decTipoUnitaDocAmmessos.decTipoUnitaDoc");
    //
    // group.addAttribute("orgStrut");
    // // Imposto la "profondita'" CASCADE_TREE in maniera tale che vengano considerati
    // // i parametri precedentemente impostati
    // group.setDepth(CopyGroup.CASCADE_TREE);
    // DecRegistroUnitaDoc copy = (DecRegistroUnitaDoc) helper.getEntityManager().unwrap(JpaEntityManager.class)
    // .copy(registroOriginale, group);
    // return copy;
    // }

    private DecRegistroUnitaDoc duplicaRegistroEntity(DecRegistroUnitaDoc registroOriginale)
            throws NoSuchFieldException {
        DecRegistroUnitaDoc copy = new DecRegistroUnitaDoc();
        // Campi base di DecRegistroUnitaDoc
        copy.setCdRegistroUnitaDoc(registroOriginale.getCdRegistroUnitaDoc());
        copy.setDsRegistroUnitaDoc(registroOriginale.getDsRegistroUnitaDoc());
        copy.setDtIstituz(registroOriginale.getDtIstituz());
        copy.setDtSoppres(registroOriginale.getDtSoppres());
        copy.setFlCreaSerie(registroOriginale.getFlCreaSerie());
        copy.setFlRegistroFisc(registroOriginale.getFlRegistroFisc());
        copy.setNiAnniConserv(registroOriginale.getNiAnniConserv());
        copy.setFlCreaSerie(registroOriginale.getFlCreaSerie());
        copy.setFlCreaTipoSerieStandard(registroOriginale.getFlCreaTipoSerieStandard());
        copy.setDecModelloTipoSerie(registroOriginale.getDecModelloTipoSerie());
        copy.setNmTipoSerieDaCreare(registroOriginale.getNmTipoSerieDaCreare());
        copy.setDsTipoSerieDaCreare(registroOriginale.getDsTipoSerieDaCreare());
        copy.setCdSerieDaCreare(registroOriginale.getCdSerieDaCreare());
        copy.setDsSerieDaCreare(registroOriginale.getDsSerieDaCreare());

        // Copio OrgStrut
        copy.setOrgStrut(registroOriginale.getOrgStrut());

        // DecAaRegistroUnitaDoc
        final List<DecAaRegistroUnitaDoc> decAaRegistroUnitaDocs = registroOriginale.getDecAaRegistroUnitaDocs();
        if (decAaRegistroUnitaDocs != null && decAaRegistroUnitaDocs.size() > 0) {
            copy.setDecAaRegistroUnitaDocs(new ArrayList<>());
            for (DecAaRegistroUnitaDoc u : decAaRegistroUnitaDocs) {
                DecAaRegistroUnitaDoc ucopy = new DecAaRegistroUnitaDoc();
                ucopy.setAaMaxRegistroUnitaDoc(u.getAaMaxRegistroUnitaDoc());
                ucopy.setAaMinRegistroUnitaDoc(u.getAaMinRegistroUnitaDoc());
                ucopy.setFlUpdFmtNumero(u.getFlUpdFmtNumero());
                ucopy.setCdFormatoNumero(u.getCdFormatoNumero());
                ucopy.setDsFormatoNumero(u.getDsFormatoNumero());
                ucopy.setDecRegistroUnitaDoc(copy);

                final List<DecErrAaRegistroUnitaDoc> decErrAaRegistroUnitaDocs = u.getDecErrAaRegistroUnitaDocs();
                if (decErrAaRegistroUnitaDocs != null && decErrAaRegistroUnitaDocs.size() > 0) {
                    ucopy.setDecErrAaRegistroUnitaDocs(new ArrayList<>());
                    for (DecErrAaRegistroUnitaDoc er : decErrAaRegistroUnitaDocs) {
                        DecErrAaRegistroUnitaDoc cer = new DecErrAaRegistroUnitaDoc();
                        cer.setDecAaRegistroUnitaDoc(ucopy);
                        cer.setAaRegistroUnitaDoc(er.getAaRegistroUnitaDoc());
                        cer.setDsErrFmtNumero(er.getDsErrFmtNumero());
                        cer.setIdUnitaDocErrFmtNumero(er.getIdUnitaDocErrFmtNumero());
                        ucopy.getDecErrAaRegistroUnitaDocs().add(cer);
                    }
                }

                final List<DecWarnAaRegistroUd> decWarnAaRegistroUd = u.getDecWarnAaRegistroUds();
                if (decWarnAaRegistroUd != null && decWarnAaRegistroUd.size() > 0) {
                    ucopy.setDecWarnAaRegistroUds(new ArrayList<>());
                    for (DecWarnAaRegistroUd w : decWarnAaRegistroUd) {
                        DecWarnAaRegistroUd cw = new DecWarnAaRegistroUd();
                        cw.setDecAaRegistroUnitaDoc(ucopy);
                        cw.setAaRegistroUnitaDoc(w.getAaRegistroUnitaDoc());
                        cw.setFlWarnAaRegistroUnitaDoc(w.getFlWarnAaRegistroUnitaDoc());
                        ucopy.getDecWarnAaRegistroUds().add(cw);
                    }
                }

                final List<DecParteNumeroRegistro> decParteNumeroRegistros = u.getDecParteNumeroRegistros();
                if (decParteNumeroRegistros != null && decParteNumeroRegistros.size() > 0) {
                    ucopy.setDecParteNumeroRegistros(new ArrayList<>());
                    for (DecParteNumeroRegistro pr : decParteNumeroRegistros) {
                        DecParteNumeroRegistro cpr = new DecParteNumeroRegistro();
                        cpr.setDecAaRegistroUnitaDoc(ucopy);
                        cpr.setDlValoriParte(pr.getDlValoriParte());
                        cpr.setDsParteNumeroRegistro(pr.getDsParteNumeroRegistro());
                        cpr.setNiMaxCharParte(pr.getNiMaxCharParte());
                        cpr.setNiMinCharParte(pr.getNiMinCharParte());
                        cpr.setNiParteNumeroRegistro(pr.getNiParteNumeroRegistro());
                        cpr.setNmParteNumeroRegistro(pr.getNmParteNumeroRegistro());
                        cpr.setTiCharParte(pr.getTiCharParte());
                        cpr.setTiCharSep(pr.getTiCharSep());
                        cpr.setTiPadSxParte(pr.getTiPadSxParte());
                        cpr.setTiParte(pr.getTiParte());
                        ucopy.getDecParteNumeroRegistros().add(cpr);
                    }
                }

                copy.getDecAaRegistroUnitaDocs().add(ucopy);
            }
        }

        // DecTipoUnitaDocAmmesso
        final List<DecTipoUnitaDocAmmesso> decTipoUnitaDocAmmessos = registroOriginale.getDecTipoUnitaDocAmmessos();
        if (decTipoUnitaDocAmmessos != null && decTipoUnitaDocAmmessos.size() > 0) {
            copy.setDecTipoUnitaDocAmmessos(new ArrayList<>());
            for (DecTipoUnitaDocAmmesso u : decTipoUnitaDocAmmessos) {
                DecTipoUnitaDocAmmesso cu = new DecTipoUnitaDocAmmesso();
                cu.setDecRegistroUnitaDoc(copy);
                cu.setDecTipoUnitaDoc(u.getDecTipoUnitaDoc());
                copy.getDecTipoUnitaDocAmmessos().add(cu);
            }
        }

        return copy;
    }

    /**
     * Ritorna il tableBean contenente la lista dei periodi di validità del registro dato come parametro
     *
     * @param idRegistro
     *            id del registro
     * 
     * @return il tablebean
     */
    public DecAaRegistroUnitaDocTableBean getDecAARegistroUnitaDocTableBean(BigDecimal idRegistro) {
        DecAaRegistroUnitaDocTableBean tableBean = new DecAaRegistroUnitaDocTableBean();
        List<DecAaRegistroUnitaDoc> list = helper.getDecAARegistroUnitaDocList(idRegistro);
        if (list != null) {
            try {
                for (DecAaRegistroUnitaDoc row : list) {
                    DecAaRegistroUnitaDocRowBean rowBean = (DecAaRegistroUnitaDocRowBean) Transform.entity2RowBean(row);
                    List<DecParteNumeroRegistro> parti = helper
                            .getDecParteNumeroRegistroList(row.getIdAaRegistroUnitaDoc());
                    rowBean.setString("aa_max_registro_unita_doc_descr", calcolaDescrizione(parti));
                    rowBean.setString("aa_max_registro_unita_doc_esempio", calcolaEsempio(parti));
                    rowBean.setString("controllo_formato_da_list",
                            struttureHelper.getDecVChkFmtNumeroForPeriodo(row.getIdAaRegistroUnitaDoc()));
                    tableBean.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        }

        return tableBean;
    }

    /**
     * Ritorna il tableBean contenente la lista di parti di registro per il periodo di validità dato come parametro
     *
     * @param idAaRegistroUnitaDoc
     *            id anno registro unita doc
     * 
     * @return il tablebean
     */
    public DecParteNumeroRegistroTableBean getDecParteNumeroRegistroTableBean(BigDecimal idAaRegistroUnitaDoc) {
        DecParteNumeroRegistroTableBean tableBean = new DecParteNumeroRegistroTableBean();
        List<DecParteNumeroRegistro> parti = helper.getDecParteNumeroRegistroList(idAaRegistroUnitaDoc.longValue());
        if (parti != null && !parti.isEmpty()) {
            try {
                for (DecParteNumeroRegistro parte : parti) {
                    DecParteNumeroRegistroRowBean parteRow = (DecParteNumeroRegistroRowBean) Transform
                            .entity2RowBean(parte);
                    if (parte.getTiCharSep() != null && StringUtils.isBlank(parte.getTiCharSep())) {
                        parteRow.setTiCharSep("SPAZIO");
                    }
                    tableBean.add(parteRow);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        }
        return tableBean;
    }

    private String calcolaDescrizione(List<DecParteNumeroRegistro> parti) {
        String descr = "";
        for (DecParteNumeroRegistro parte : parti) {
            descr += parte.getDsParteNumeroRegistro();
            descr += ",";
        }

        return StringUtils.chop(descr);
    }

    private String calcolaEsempio(List<DecParteNumeroRegistro> parti) {
        String esempio = "";
        int index = 0;
        for (DecParteNumeroRegistro parte : parti) {
            index++;
            if (!StringUtils.isEmpty(parte.getDlValoriParte())) {
                // TODO: Chiedere a Paola quale pu\u00F2 essere il contenuto della colonna e inserire solo il primo dei
                // caratteri possibili.
                String valoriAmmessi = parte.getDlValoriParte();
                StringTokenizer st = null;
                if (valoriAmmessi.contains(">") || valoriAmmessi.contains("<")) {
                    st = new StringTokenizer(valoriAmmessi, "-");
                    if (st.hasMoreTokens()) {
                        String lowBound = st.nextToken();
                        lowBound = lowBound.replace("<", "");
                        lowBound = lowBound.replace(">", "");
                        int min = Integer.parseInt(lowBound);
                        min++;
                        esempio += min;
                    }
                } else if (valoriAmmessi.contains(",")) {
                    st = new StringTokenizer(valoriAmmessi, ",");
                    if (st.hasMoreTokens()) {
                        esempio += st.nextToken();
                    }
                } else {
                    esempio += valoriAmmessi;
                }
            } else if (!StringUtils.isEmpty(parte.getTiParte()) && parte.getTiParte().equals("ANNO")) {
                esempio += parte.getDecAaRegistroUnitaDoc().getAaMinRegistroUnitaDoc();
            } else if (!StringUtils.isEmpty(parte.getTiParte()) && parte.getTiParte().equals("REGISTRO")) {
                esempio += parte.getDecAaRegistroUnitaDoc().getDecRegistroUnitaDoc().getCdRegistroUnitaDoc();
            } else {
                int nMed = 0;
                if (parte.getNiMaxCharParte() != null) {
                    nMed = (int) (parte.getNiMinCharParte().intValue() + parte.getNiMaxCharParte().intValue()) / 2;
                } else {
                    nMed = (int) parte.getNiMinCharParte().intValue();
                }
                KeyOrdUtility.TipiCalcolo tipo = KeyOrdUtility.TipiCalcolo.valueOf(parte.getTiCharParte());
                switch (tipo) {
                case ALFABETICO:
                    for (int j = 0; j < nMed; j++) {
                        esempio += "A";
                    }
                    break;
                case ALFANUMERICO:
                    for (int j = 0; j < nMed; j++) {
                        if (j % 2 == 0) {
                            esempio += "A";
                        } else {
                            esempio += "1";
                        }
                    }
                    break;
                case NUMERICO:
                    for (int j = 0; j < nMed; j++) {
                        esempio += (int) (Math.random() * 10);
                    }
                    break;
                case NUMERI_ROMANI:
                    for (int j = 0; j < nMed; j++) {
                        esempio += "I";
                    }
                    break;
                case PARTE_GENERICO:
                    for (int j = 0; j < nMed; j++) {
                        if (j % 3 == 0) {
                            esempio += "A";
                        } else if (j % 3 == 1) {
                            esempio += "1";
                        } else {
                            esempio += "_";
                        }
                    }
                    break;
                case NUMERICO_GENERICO:
                    esempio = "#";
                    for (int j = 1; j < nMed; j++) {
                        esempio += "1";
                    }
                    break;
                }
            }

            if (parte.getTiCharSep() != null && index < parti.size()) {
                String separatore = StringUtils.isNotBlank(parte.getTiCharSep()) ? parte.getTiCharSep() : " ";
                esempio += separatore;
            }
        }
        return esempio;
    }

    public DecRegistroUnitaDocTableBean getRegistriUnitaDocAbilitati(long idUtente, BigDecimal idStruttura) {
        DecRegistroUnitaDocTableBean table = new DecRegistroUnitaDocTableBean();
        List<DecRegistroUnitaDoc> list = helper.getRegistriUnitaDocAbilitati(idUtente, idStruttura);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecRegistroUnitaDocTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista registri abilitati "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
            }
        }
        return table;
    }

    public void checkPeriodiValiditaProgressivo(BigDecimal idRegistroUnitaDoc) throws ParerUserError {
        Long countPeriodiNoConsec = helper.countPeriodiValiditaConControlloConsec(idRegistroUnitaDoc);
        if (countPeriodiNoConsec != null) {
            if (countPeriodiNoConsec != 0L) {
                List<DecAaRegistroUnitaDoc> decAARegistroUnitaDocList = helper
                        .getDecAARegistroUnitaDocList(idRegistroUnitaDoc);
                if (countPeriodiNoConsec.intValue() == decAARegistroUnitaDocList.size()) {
                    throw new ParerUserError(
                            "Non esiste nessun periodo di validit\u00E0 per il registro selezionato con definita una parte di tipo NUMERICO");
                } else {
                    throw new ParerUserError(ParerErrorSeverity.WARNING, null, null);
                }
            }
        } else {
            throw new ParerUserError("Errore inaspettato nel controllo dei periodi di validit\u00E0");
        }
    }

    public boolean existDecRegistroUnitaDocForIdModello(BigDecimal idModelloTipoSerie) {
        return helper.countDecRegistroUnitaDoc(idModelloTipoSerie) > 0L;
    }

    public String checkEditRegistroForTipiSerie(BigDecimal idStrut, BigDecimal idRegistroUnitaDoc,
            String cdRegistroUnitaDocNew) throws ParerUserError {
        StringBuilder message = new StringBuilder();
        DecRegistroUnitaDoc registroUnitaDoc = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        List<DecTipoSerie> retrieveDecTipoSerieForRegistro = tipoSerieHelper.retrieveDecTipoSerieForRegistro(idStrut,
                idRegistroUnitaDoc);
        for (DecTipoSerie tipoSerie : retrieveDecTipoSerieForRegistro) {
            DecModelloTipoSerie modelloTipoSerie;
            if ((modelloTipoSerie = tipoSerie.getDecModelloTipoSerie()) != null) {
                boolean intestazione = false;
                for (DecTipoSerieUd tipoSerieUd : tipoSerie.getDecTipoSerieUds()) {
                    DecTipoUnitaDoc tipoUnitaDoc = tipoSerieUd.getDecTipoUnitaDoc();
                    String nmTipoSerie = tipoSerieEjb.getNmTipoSerieForModello(modelloTipoSerie, registroUnitaDoc,
                            tipoUnitaDoc, tipoSerie.getTiCreaStandard(), cdRegistroUnitaDocNew);
                    // Verifico che non esista già un tipo serie con questo nome
                    if (StringUtils.isNotBlank(nmTipoSerie)) {
                        if (tipoSerieHelper.getDecTipoSerieByName(nmTipoSerie, idStrut.longValue()) == null) {
                            String dsTipoSerie = tipoSerieEjb.getDsTipoSerieForModello(modelloTipoSerie,
                                    registroUnitaDoc, tipoUnitaDoc, tipoSerie.getTiCreaStandard(),
                                    cdRegistroUnitaDocNew);
                            String cdSerie = tipoSerieEjb.getCdSerieForModello(modelloTipoSerie, registroUnitaDoc,
                                    tipoUnitaDoc, tipoSerie.getTiCreaStandard(), cdRegistroUnitaDocNew);
                            String dsSerie = tipoSerieEjb.getDsSerieForModello(modelloTipoSerie, registroUnitaDoc,
                                    tipoUnitaDoc, tipoSerie.getTiCreaStandard(), cdRegistroUnitaDocNew);
                            if (!intestazione) {
                                message.append(
                                        "Attenzione: modificando la denominazione del registro verranno aggiornati i seguenti dati:#");
                                intestazione = true;
                            }
                            message.append("\tLa denominazione del tipo serie [").append(tipoSerie.getNmTipoSerie())
                                    .append("] diventer\u00E0 [").append(nmTipoSerie).append("]#");
                            message.append("\tLa descrizione del tipo serie [").append(tipoSerie.getDsTipoSerie())
                                    .append("] diventer\u00E0 [").append(dsTipoSerie).append("]#");
                            message.append("\tIl codice serie [").append(tipoSerie.getCdSerieDefault())
                                    .append("] diventer\u00E0 [").append(cdSerie).append("]#");
                            message.append("\tLa descrizione della serie [").append(tipoSerie.getDsSerieDefault())
                                    .append("] diventer\u00E0 [").append(dsSerie).append("]#");
                            message.append("\tLe serie associate al tipo serie verranno modificate di conseguenza#");
                        } else {
                            message.append(
                                    "Attenzione: la modifica del nome del registro non consentir\u00E0 di modificare il tipo serie [")
                                    .append(tipoSerie.getNmTipoSerie())
                                    .append("] per adeguarlo alla nuova denominazione del registro in quanto la nuova denominazione del tipo serie ([")
                                    .append(nmTipoSerie).append("]) \u00E8 gi\u00E0 presente nel sistema#");
                        }
                    }
                }
            }
        }
        return message.toString();
    }

    public DecRegistroUnitaDocRowBean getDecRegistroUnitaDocRowBean(BigDecimal idRegistroUnitaDoc, BigDecimal idStrut) {
        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = getDecRegistroUnitaDoc(idRegistroUnitaDoc, null, idStrut);
        return registroUnitaDocRowBean;
    }

    public DecRegistroUnitaDocRowBean getDecRegistroUnitaDocRowBean(String cdRegistroUnitaDoc, BigDecimal idStrut) {
        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = getDecRegistroUnitaDoc(BigDecimal.ZERO, cdRegistroUnitaDoc,
                idStrut);
        return registroUnitaDocRowBean;
    }

    /**
     * Ritorna il valore massimo del campo niAnniConserv contenuto nei registri legati al tipo serie dato in input
     *
     * @param idTipoSerie
     *            tipo serie
     * 
     * @return BigDecimal
     */
    public BigDecimal getMaxAnniConserv(BigDecimal idTipoSerie) {
        return helper.getMaxAnniConserv(idTipoSerie);
    }

    private DecRegistroUnitaDocRowBean getDecRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc, String cdRegistroUnitadoc,
            BigDecimal idStrut) {
        DecRegistroUnitaDoc registroUnitaDoc = null;
        DecRegistroUnitaDocRowBean registroUnitaDocRowBean = null;
        if (idRegistroUnitaDoc == BigDecimal.ZERO && cdRegistroUnitadoc != null) {
            registroUnitaDoc = helper.getDecRegistroUnitaDocByName(cdRegistroUnitadoc, idStrut);
        }
        if (cdRegistroUnitadoc == null && idRegistroUnitaDoc != BigDecimal.ZERO) {
            registroUnitaDoc = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        }
        if (registroUnitaDoc != null) {
            try {
                registroUnitaDocRowBean = (DecRegistroUnitaDocRowBean) Transform.entity2RowBean(registroUnitaDoc);
                registroUnitaDocRowBean.setString("controllo_formato",
                        helper.getDecVChkFmtNumeroForRegistro(registroUnitaDoc.getIdRegistroUnitaDoc()));

                // Data primo e ultimo versamento
                Object[] dateFirstLastVers = helper
                        .retrieveDateFirstLastVersRegistro(registroUnitaDoc.getIdRegistroUnitaDoc());
                if (dateFirstLastVers != null) {
                    Date d = (Date) dateFirstLastVers[0];
                    registroUnitaDocRowBean.setTimestamp("dt_first_vers", new Timestamp(d.getTime()));
                    Date dLast = (Date) dateFirstLastVers[1];
                    registroUnitaDocRowBean.setTimestamp("dt_last_vers", new Timestamp(dLast.getTime()));//
                }

                registroUnitaDocRowBean.setString("controllo_formato",
                        helper.getDecVChkFmtNumeroForRegistro(registroUnitaDoc.getIdRegistroUnitaDoc()));

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return registroUnitaDocRowBean;
    }

    public DecRegistroUnitaDocTableBean getDecRegistroUnitaDocTableBean(BigDecimal idStrut, boolean filterValid) {
        DecRegistroUnitaDocTableBean registroUnitaDocTableBean = new DecRegistroUnitaDocTableBean();
        List<DecRegistroUnitaDoc> list = helper.retrieveDecRegistroUnitaDocList(idStrut.longValue(), filterValid);

        try {
            if (!list.isEmpty()) {
                for (DecRegistroUnitaDoc registro : list) {
                    DecRegistroUnitaDocRowBean registroRow = (DecRegistroUnitaDocRowBean) Transform
                            .entity2RowBean(registro);
                    if (registro.getDtIstituz().before(new Date()) && registro.getDtSoppres().after(new Date())) {
                        registroRow.setObject("fl_attivo", "1");
                    } else {
                        registroRow.setObject("fl_attivo", "0");
                    }
                    List<BigDecimal> idRegistro = new ArrayList<>();
                    idRegistro.add(registroRow.getIdRegistroUnitaDoc());
                    List<DecVLisTiUniDocAms> tipoUnitaDocAssociati = helper
                            .getDecVLisTiUniDocAmsByStrutByRegistriList(idRegistro);

                    StringBuilder nm_tipo_unita_doc = new StringBuilder();
                    for (int index = 0; index < tipoUnitaDocAssociati.size(); index++) {
                        DecVLisTiUniDocAms row = tipoUnitaDocAssociati.get(index);
                        nm_tipo_unita_doc.append(row.getNmTipoUnitaDoc());
                        if (index < (tipoUnitaDocAssociati.size() - 1)) {
                            nm_tipo_unita_doc.append("; ");
                        }
                    }
                    registroRow.setString("nm_tipo_unita_doc", nm_tipo_unita_doc.toString());

                    // Creo il campo relativo ai periodi di validit\u00E0
                    StringBuilder periodi = new StringBuilder();
                    int numeroPeriodi = registro.getDecAaRegistroUnitaDocs().size();
                    for (int index = 0; index < numeroPeriodi; index++) {
                        DecAaRegistroUnitaDoc anno = registro.getDecAaRegistroUnitaDocs().get(index);
                        periodi.append(anno.getAaMinRegistroUnitaDoc());
                        if (anno.getAaMaxRegistroUnitaDoc() != null) {
                            periodi.append(" - ").append(anno.getAaMaxRegistroUnitaDoc());
                        }
                        if (index < (numeroPeriodi - 1)) {
                            periodi.append("; ");
                        }

                    }
                    registroRow.setString("periodi", periodi.toString());

                    // Criterio standard
                    String flagStandard = crHelper.getCriterioStandardPerTipoDatoAnno(registro.getIdRegistroUnitaDoc(),
                            TipoDato.REGISTRO);
                    registroRow.setString("flag_criterio_standard", flagStandard);

                    // Controllo formato numero
                    String controlloFormato = helper.getDecVChkFmtNumeroForRegistro(registro.getIdRegistroUnitaDoc());
                    registroRow.setString("controllo_formato", controlloFormato);

                    // Data primo e ultimo versamento
                    Object[] dateFirstLastVers = helper
                            .retrieveDateFirstLastVersRegistro(registro.getIdRegistroUnitaDoc());
                    if (dateFirstLastVers != null) {
                        Date d = (Date) dateFirstLastVers[0];
                        registroRow.setTimestamp("dt_first_vers", new Timestamp(d.getTime()));
                        Date dLast = (Date) dateFirstLastVers[1];
                        registroRow.setTimestamp("dt_last_vers", new Timestamp(dLast.getTime()));//
                    }

                    registroUnitaDocTableBean.add(registroRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return registroUnitaDocTableBean;
    }

    public DecRegistroUnitaDocTableBean getDecRegistroUnitaDocTableBean(BigDecimal idStrut) {
        List<DecRegistroUnitaDoc> registri = helper.retrieveDecRegistroUnitaDocList(idStrut.longValue(), false);
        DecRegistroUnitaDocTableBean tableBean = new DecRegistroUnitaDocTableBean();
        try {
            if (registri != null && !registri.isEmpty()) {
                tableBean = (DecRegistroUnitaDocTableBean) Transform.entities2TableBean(registri);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei registri " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    /**
     * Business method per la cancellazione di un singolo registro passato come parametro. Il metodo chiama a sua volta
     * il metodo "deleteRegistroUnitaDoc" all'interno dello stesso contesto transazionale. In caso ci siano problemi, si
     * rilancia l'eccezione e l'annotation
     *
     * Sul metodo procede ad eseguire il rollback. Se invece il metodo non genera eccezioni viene chiamata la replica su
     * Iam. In questo caso, dovesse verificarsi un errore, la rollback verrebbe gestita tramite IncoherenceException
     * (del metodo replicateToIam) ed essendo stato un creato un nuovo contesto transazionale (REQUIRES_NEW) la rollback
     * avrebbe effetto solo sulla replica (non voglio rollbackare tutto...)
     *
     * @param param
     *            parametro
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void deleteDecRegistroUnitaDoc(LogParam param, long idRegistroUnitaDoc) throws ParerUserError {
        RegistroEjb me = context.getBusinessObject(RegistroEjb.class);
        IamOrganizDaReplic replic = me.deleteRegistroUnitaDocPuntuale(param, idRegistroUnitaDoc);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic deleteRegistroUnitaDocPuntuale(LogParam param, long idRegistroUnitaDoc)
            throws ParerUserError {
        // LOG BEFORE PER IL TIPO UD
        /*
         * Se il TransactionContext è già valorizzato usa quello altrimenti ne ottiene uno nuovo e lo valorizza su
         * logParam per usi successivi nel caso in cui tutto dovesse essere loggato nello stesso contesto transazionale
         * logico del logging.
         */
        if (!param.isTransactionActive()) {
            param.setTransactionLogContext(sacerLogEjb.getNewTransactionLogContext());
        }
        // Lo fa dentro la eseguiXXX()
        // List<ObjectsToLogBefore> listaOggettiDaLoggare = sacerLogEjb.logBefore(param.getTransactionLogContext(),
        // param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
        // SacerLogConstants.TIPO_OGGETTO_REGISTRO, new BigDecimal(idRegistroUnitaDoc), param.getNomePagina());
        IamOrganizDaReplic replic = eseguiDeleteRegistroUnitaDoc(param, idRegistroUnitaDoc, false);
        // sacerLogEjb.logAfter(param.getTransactionLogContext(),param.getNomeApplicazione(), param.getNomeUtente(),
        // param.getNomeAzione(), listaOggettiDaLoggare, param.getNomePagina());
        return replic;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public IamOrganizDaReplic deleteRegistroUnitaDocFromStruttura(LogParam param, long idRegistroUnitaDoc)
            throws ParerUserError {
        return eseguiDeleteRegistroUnitaDoc(param, idRegistroUnitaDoc, true);
    }

    /**
     * Business method per la cancellazione di un registro di una determinata struttura. Il metodo esegue le seguenti
     * istruzioni: 1) Controlla i legami con Tipologia Serie e Unità documentarie; 2) Controllo i legami con criteri di
     * raggruppamento e, se può, li elimina 3) Esegue la cancellazione del Registro con cascade 4) Scrive il record
     * dell'organizzazione da replicare
     *
     * @param param
     *            parametro
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * 
     * @return il record dell'organizzazione da replicare
     * 
     * @throws ParerUserError
     *             errore generico
     */
    private IamOrganizDaReplic eseguiDeleteRegistroUnitaDoc(LogParam param, long idRegistroUnitaDoc,
            boolean isFromDeleteStruttura) throws ParerUserError {
        DecRegistroUnitaDoc registroUnitaDoc = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        String cdRegistroUnitaDoc = registroUnitaDoc.getCdRegistroUnitaDoc();
        long idStrut = registroUnitaDoc.getOrgStrut().getIdStrut();

        // boolean relationsWithTipiSerie =
        // struttureHelper.existsRelationsWithTipiSerieForDecRegistroUnitaDoc(registroUnitaDoc.getIdRegistroUnitaDoc());
        boolean relationsWithTipiSerie = tipoSerieHelper
                .existsRelationsWithTipiSerie(registroUnitaDoc.getIdRegistroUnitaDoc(), Constants.TipoDato.REGISTRO);
        if (relationsWithTipiSerie) {
            throw new ParerUserError(
                    "Impossibile eliminare il registro: esiste almeno una tipologia di serie associata a una serie</br>");
        }

        // boolean relationsWithUnitaDoc =
        // struttureHelper.existsRelationsWithUnitaDocForDecRegistroUnitaDoc(registroUnitaDoc.getIdRegistroUnitaDoc());
        boolean relationsWithUnitaDoc = unitaDocHelper
                .existsRelationsWithUnitaDoc(registroUnitaDoc.getIdRegistroUnitaDoc(), Constants.TipoDato.REGISTRO);
        if (relationsWithUnitaDoc) {
            throw new ParerUserError(
                    "Impossibile eliminare il registro: esiste almeno una unità documentaria versata con il registro</br>");
        }

        List<ObjectsToLogBefore> listaBefore = sacerLogEjb.logBefore(param.getTransactionLogContext(),
                param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
                SacerLogConstants.TIPO_OGGETTO_REGISTRO, new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()),
                param.getNomePagina());
        List<ObjectsToLogBefore> listaBeforeDeletion = ObjectsToLogBefore.filterObjectsForDeletion(listaBefore);
        List<ObjectsToLogBefore> listaBeforeModifying = ObjectsToLogBefore.filterObjectsForModifying(listaBefore);
        /* In questo caso gli oggetti vengono fotografati prima perché spariranno completamente */
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaBeforeDeletion, param.getNomePagina());

        // List<DecCriterioFiltroMultiplo> criteriAssociati =
        // struttureHelper.getRelationsWithCriteriRaggruppamentoForDecRegistroUnitaDoc(registroUnitaDoc.getIdRegistroUnitaDoc());
        List<DecCriterioFiltroMultiplo> criteriAssociati = struttureHelper.getRelationsWithCriteriRaggruppamento(
                registroUnitaDoc.getIdRegistroUnitaDoc(), Constants.TipoDato.REGISTRO);
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
            // Se sono arrivato fin qui, quindi non è scattata l'eccezione, posso eliminare i criteri
            crHelper.bulkDeleteCriteriRaggr(criteriRaggrDaEliminare);
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                new BigDecimal(registroUnitaDoc.getIdRegistroUnitaDoc()), param.getNomePagina());
        helper.removeEntity(registroUnitaDoc, true);
        /* Foto dopo eliminazione di eventuali disassociazioni */
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaBeforeModifying, param.getNomePagina());
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        IamOrganizDaReplic replic = null;
        /*
         * La replica va fatta solo se il registro appartiene ad una struttura non appartenente ad un ente di tipo
         * template
         */
        if ((struttura.getOrgEnte().getTipoDefTemplateEnte()
                .equals(CostantiDB.TipoDefTemplateEnte.TEMPLATE_DEF_ENTE.name())
                || struttura.getOrgEnte().getTipoDefTemplateEnte()
                        .equals(CostantiDB.TipoDefTemplateEnte.NO_TEMPLATE.name()))
                && !isFromDeleteStruttura) {
            replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, ApplEnum.TiOperReplic.MOD);
        }

        logger.info("Cancellazione registro " + cdRegistroUnitaDoc + " della struttura " + idStrut
                + " avvenuta con successo!");
        return replic;
    }

    public DecAaRegistroUnitaDocRowBean getDecAaRegistroUnitaDocRowBean(BigDecimal idAaRegistroUnitaDoc) {
        DecAaRegistroUnitaDocRowBean decAaRegistroUnitaDocRowBean = new DecAaRegistroUnitaDocRowBean();
        DecAaRegistroUnitaDoc decAaRegistroUnitaDoc = helper.getDecAARegistroUnitaDoc(idAaRegistroUnitaDoc);
        if (decAaRegistroUnitaDoc != null) {
            try {
                decAaRegistroUnitaDocRowBean = (DecAaRegistroUnitaDocRowBean) Transform
                        .entity2RowBean(decAaRegistroUnitaDoc);
                decAaRegistroUnitaDocRowBean
                        .setFlUpdFmtNumero(decAaRegistroUnitaDoc.getFlUpdFmtNumero().equals("1") ? "0" : "1");
                decAaRegistroUnitaDocRowBean.setString("controllo_formato",
                        struttureHelper.getDecVChkFmtNumeroForPeriodo(decAaRegistroUnitaDoc.getIdAaRegistroUnitaDoc()));
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        }

        return decAaRegistroUnitaDocRowBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecAaRegistroUnitaDoc(LogParam param, DecAaRegistroUnitaDocRowBean aaRegistroUnitaDocRowBean)
            throws ParerUserError {
        BigDecimal annoCorrente = new BigDecimal(Calendar.getInstance().get(Calendar.YEAR));
        DecAaRegistroUnitaDoc aaRegistroUnitaDoc = helper
                .getDecAARegistroUnitaDoc(aaRegistroUnitaDocRowBean.getIdAaRegistroUnitaDoc());
        BigDecimal aaMinRegistroUnitaDoc = aaRegistroUnitaDoc.getAaMinRegistroUnitaDoc();
        // Se aaMax non è presente, prendi l'anno corrente. Se però aaMin > anno corrente, allora come aaMax assegna lo
        // stesso valore di aaMin (onde evitare di avere aaMin>aaMax)
        BigDecimal aaMaxRegistroUnitaDoc = aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc() != null
                ? aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc()
                : (aaMinRegistroUnitaDoc.compareTo(annoCorrente) > 0 ? aaMinRegistroUnitaDoc : annoCorrente);

        long mon = helper.getMonAaUdRegistroNumber(aaRegistroUnitaDocRowBean.getAaMinRegistroUnitaDoc(),
                aaRegistroUnitaDocRowBean.getAaMaxRegistroUnitaDoc(),
                aaRegistroUnitaDocRowBean.getIdRegistroUnitaDoc());
        if (mon > 0) {
            throw new ParerUserError(
                    "Nel periodo di validit\u00E0 del registro risultano versate delle unit\u00E0 documentarie. Impossibile cancellare. </br>");
        }

        // Controlli per eliminazione criteri di raggruppamento
        for (int i = aaMinRegistroUnitaDoc.intValue(); i <= aaMaxRegistroUnitaDoc.intValue(); i++) {
            // Riocavo i criteri di raggruppamento standard per quel registro per l'anno i
            List<DecCriterioRaggr> criterioRaggrList = crHelper.getDecCriterioRaggrRegistroOTipiUdAssociatiList(
                    new BigDecimal(aaRegistroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()), i);
            for (DecCriterioRaggr criterioRaggr : criterioRaggrList) {
                // Verifico se il criterio che ho ricavato è stato utilizzato per un elenco di versamento
                // Se non è associato, lo elimino
                if (!crHelper.existElvElencoVersPerCriterioRaggr(new BigDecimal(criterioRaggr.getIdCriterioRaggr()))) {
                    crHelper.deleteDecCriterioRaggr(param, new BigDecimal(criterioRaggr.getOrgStrut().getIdStrut()),
                            criterioRaggr.getNmCriterioRaggr());
                }
            }
        }

        helper.removeEntity(aaRegistroUnitaDoc, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                aaRegistroUnitaDocRowBean.getIdRegistroUnitaDoc(), param.getNomePagina());
    }

    public void checkDecAaRegistroUnitaDoc(BigDecimal idAaRegistroUnitaDoc, BigDecimal idRegistroUnitaDoc,
            BigDecimal aaMinRegistroUnitaDoc, BigDecimal aaMaxRegistroUnitaDoc) throws ParerUserError {
        logger.info("Verifico che il periodo indicato non si sovrapponga ad altri periodi");
        boolean periodiSovrapposti = helper.checkRangeDecAaRegistroUnitaDoc(idAaRegistroUnitaDoc, idRegistroUnitaDoc,
                aaMinRegistroUnitaDoc, aaMaxRegistroUnitaDoc);
        if (periodiSovrapposti) {
            throw new ParerUserError("Errore di compilazione: range inserito sovrapposto a range gi\u00E0 presenti");
        }
    }

    private void cloneDecAaRegistroUnitaDoc(DecRegistroUnitaDoc registro, DecAaRegistroUnitaDoc row,
            BigDecimal aaMinRegistroUnitaDoc, BigDecimal aaMaxRegistroUnitaDoc) {
        DecAaRegistroUnitaDoc newAaRegistroUnitaDoc = new DecAaRegistroUnitaDoc();
        newAaRegistroUnitaDoc.setAaMinRegistroUnitaDoc(aaMinRegistroUnitaDoc);
        newAaRegistroUnitaDoc.setAaMaxRegistroUnitaDoc(aaMaxRegistroUnitaDoc);
        newAaRegistroUnitaDoc.setFlUpdFmtNumero(row.getFlUpdFmtNumero());
        newAaRegistroUnitaDoc.setDecRegistroUnitaDoc(registro);
        registro.getDecAaRegistroUnitaDocs().add(newAaRegistroUnitaDoc);
        if (newAaRegistroUnitaDoc.getDecParteNumeroRegistros() == null) {
            newAaRegistroUnitaDoc.setDecParteNumeroRegistros(new ArrayList<DecParteNumeroRegistro>());
        }

        for (DecParteNumeroRegistro parte : row.getDecParteNumeroRegistros()) {
            DecParteNumeroRegistro clone = new DecParteNumeroRegistro();
            clone.setDlValoriParte(parte.getDlValoriParte());
            clone.setDsParteNumeroRegistro(parte.getDsParteNumeroRegistro());
            clone.setNiMaxCharParte(parte.getNiMaxCharParte());
            clone.setNiMinCharParte(parte.getNiMinCharParte());
            clone.setNiParteNumeroRegistro(parte.getNiParteNumeroRegistro());
            clone.setNmParteNumeroRegistro(parte.getNmParteNumeroRegistro());
            clone.setTiCharParte(parte.getTiCharParte());
            clone.setTiCharSep(parte.getTiCharSep());
            clone.setTiPadSxParte(parte.getTiPadSxParte());
            clone.setTiParte(parte.getTiParte());

            newAaRegistroUnitaDoc.addDecParteNumeroRegistro(clone);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveDecAaRegistroUnitaDoc(LogParam param, BigDecimal idRegistroUnitaDoc,
            BigDecimal aaMinRegistroUnitaDoc, BigDecimal aaMaxRegistroUnitaDoc, DecParteNumeroRegistroTableBean parti,
            String cdFormatoNumero, String dsFormatoNumero) throws ParerUserError {
        DecAaRegistroUnitaDoc aaRegistroUnitaDoc = new DecAaRegistroUnitaDoc();
        DecRegistroUnitaDoc registro = helper.findById(DecRegistroUnitaDoc.class, idRegistroUnitaDoc);
        if (registro.getDecAaRegistroUnitaDocs() == null) {
            registro.setDecAaRegistroUnitaDocs(new ArrayList<DecAaRegistroUnitaDoc>());
        }

        logger.info("Salvo il periodo di validit\u00E0");
        aaRegistroUnitaDoc.setAaMinRegistroUnitaDoc(aaMinRegistroUnitaDoc);
        aaRegistroUnitaDoc.setAaMaxRegistroUnitaDoc(aaMaxRegistroUnitaDoc);
        aaRegistroUnitaDoc.setCdFormatoNumero(cdFormatoNumero);
        aaRegistroUnitaDoc.setDsFormatoNumero(dsFormatoNumero);
        aaRegistroUnitaDoc.setFlUpdFmtNumero("1");

        aaRegistroUnitaDoc.setDecRegistroUnitaDoc(registro);
        registro.getDecAaRegistroUnitaDocs().add(aaRegistroUnitaDoc);
        logger.info("Inserisco o aggiorno le parti");
        if (aaRegistroUnitaDoc.getDecParteNumeroRegistros() == null) {
            aaRegistroUnitaDoc.setDecParteNumeroRegistros(new ArrayList<DecParteNumeroRegistro>());
        }

        for (DecParteNumeroRegistroRowBean row : parti) {
            DecParteNumeroRegistro parte = new DecParteNumeroRegistro();
            updateParteFields(parte, row);

            aaRegistroUnitaDoc.addDecParteNumeroRegistro(parte);
        }
        helper.insertEntity(aaRegistroUnitaDoc, true);
        // Logging
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO, idRegistroUnitaDoc,
                param.getNomePagina());
        return new BigDecimal(aaRegistroUnitaDoc.getIdAaRegistroUnitaDoc());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecAaRegistroUnitaDoc(LogParam param, BigDecimal idAaRegistroUnitaDoc,
            BigDecimal aaMinRegistroUnitaDoc, BigDecimal aaMaxRegistroUnitaDoc, DecParteNumeroRegistroTableBean parti,
            Set<BigDecimal> idPartiEliminate, String cdFormatoNumero, String dsFormatoNumero) throws ParerUserError {
        DecAaRegistroUnitaDoc aaRegistroUnitaDoc = helper.findById(DecAaRegistroUnitaDoc.class, idAaRegistroUnitaDoc);
        DecRegistroUnitaDoc registro = aaRegistroUnitaDoc.getDecRegistroUnitaDoc();
        List<Long> subStruts = new ArrayList<>();
        for (OrgSubStrut subStrut : registro.getOrgStrut().getOrgSubStruts()) {
            subStruts.add(subStrut.getIdSubStrut());
        }
        BigDecimal oldRegMaxValid = aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc() != null
                ? aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc() : new BigDecimal(9999);
        BigDecimal newRegMaxValid = aaMaxRegistroUnitaDoc != null ? aaMaxRegistroUnitaDoc : new BigDecimal(9999);

        checkDecAaRegistroUnitaDoc(idAaRegistroUnitaDoc, new BigDecimal(registro.getIdRegistroUnitaDoc()),
                aaMinRegistroUnitaDoc, newRegMaxValid);
        if (aaRegistroUnitaDoc.getAaMinRegistroUnitaDoc().compareTo(aaMinRegistroUnitaDoc) < 0) {
            logger.info(
                    "\u00e8 stato modificato l'inizio validit\u00E0, verifico che non esistano UD nel range escluso attualmente");
            boolean udInRange = helper.checkUnitaDocInDecAaRegUnitaDoc(idAaRegistroUnitaDoc,
                    aaRegistroUnitaDoc.getAaMinRegistroUnitaDoc(), aaMinRegistroUnitaDoc.subtract(BigDecimal.ONE),
                    registro.getIdRegistroUnitaDoc(), registro.getCdRegistroUnitaDoc(), subStruts);
            if (udInRange) {
                cloneDecAaRegistroUnitaDoc(registro, aaRegistroUnitaDoc, aaRegistroUnitaDoc.getAaMinRegistroUnitaDoc(),
                        aaMinRegistroUnitaDoc.subtract(BigDecimal.ONE));
            }
        }
        if (oldRegMaxValid.compareTo(newRegMaxValid) > 0) {
            logger.info(
                    "\u00e8 stata modificata la fine validit\u00E0, verifico che non esistano UD nel range escluso attualmente");
            BigDecimal max = aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc() != null
                    ? aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc()
                    : new BigDecimal(Calendar.getInstance().get(Calendar.YEAR));
            boolean udInRange = helper.checkUnitaDocInDecAaRegUnitaDoc(idAaRegistroUnitaDoc,
                    newRegMaxValid.add(BigDecimal.ONE), max, registro.getIdRegistroUnitaDoc(),
                    registro.getCdRegistroUnitaDoc(), subStruts);
            if (udInRange) {
                cloneDecAaRegistroUnitaDoc(registro, aaRegistroUnitaDoc, newRegMaxValid.add(BigDecimal.ONE),
                        aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc());
            }
        }
        if (idPartiEliminate != null && !idPartiEliminate.isEmpty()) {
            logger.info("Elimino le parti eliminate");
            for (BigDecimal idParte : idPartiEliminate) {
                DecParteNumeroRegistro parte = helper.findById(DecParteNumeroRegistro.class, idParte);
                helper.removeEntity(parte, true);
            }
        }

        // Salvo la modifica del range di validit\u00E0
        logger.info("Salvo il periodo di validit\u00E0");
        aaRegistroUnitaDoc.setAaMinRegistroUnitaDoc(aaMinRegistroUnitaDoc);
        aaRegistroUnitaDoc.setAaMaxRegistroUnitaDoc(aaMaxRegistroUnitaDoc);
        aaRegistroUnitaDoc.setCdFormatoNumero(cdFormatoNumero);
        aaRegistroUnitaDoc.setDsFormatoNumero(dsFormatoNumero);
        aaRegistroUnitaDoc.setFlUpdFmtNumero("1");

        logger.info("Inserisco o aggiorno le parti");
        for (DecParteNumeroRegistroRowBean row : parti) {
            if (row.getIdParteNumeroRegistro() != null) {
                DecParteNumeroRegistro parte = helper.findById(DecParteNumeroRegistro.class,
                        row.getIdParteNumeroRegistro());
                updateParteFields(parte, row);
            } else {
                DecParteNumeroRegistro parte = new DecParteNumeroRegistro();
                updateParteFields(parte, row);

                aaRegistroUnitaDoc.addDecParteNumeroRegistro(parte);
            }
        }
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_REGISTRO,
                new BigDecimal(registro.getIdRegistroUnitaDoc()), param.getNomePagina());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void checkUdNelPeriodoValidita(BigDecimal idAaRegistroUnitaDoc) {
        logger.info(
                "Verifico che nel periodo modificato non esistano UD, nel qual caso sblocco il periodo per permettere i versamenti");
        DecAaRegistroUnitaDoc aaRegistroUnitaDoc = helper.findById(DecAaRegistroUnitaDoc.class, idAaRegistroUnitaDoc);
        DecRegistroUnitaDoc registro = aaRegistroUnitaDoc.getDecRegistroUnitaDoc();
        List<Long> subStruts = new ArrayList<>();
        for (OrgSubStrut subStrut : registro.getOrgStrut().getOrgSubStruts()) {
            subStruts.add(subStrut.getIdSubStrut());
        }
        BigDecimal max = aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc() != null
                ? aaRegistroUnitaDoc.getAaMaxRegistroUnitaDoc()
                : new BigDecimal(Calendar.getInstance().get(Calendar.YEAR));
        boolean udInRange = helper.checkUnitaDocInDecAaRegUnitaDoc(idAaRegistroUnitaDoc,
                aaRegistroUnitaDoc.getAaMinRegistroUnitaDoc(), max, registro.getIdRegistroUnitaDoc(),
                registro.getCdRegistroUnitaDoc(), subStruts);
        if (!udInRange) {
            logger.info("Nessuna UD rilevata, sblocco il periodo");
            aaRegistroUnitaDoc.setFlUpdFmtNumero("0");
        }
    }

    private void updateParteFields(DecParteNumeroRegistro parte, DecParteNumeroRegistroRowBean row) {
        parte.setNmParteNumeroRegistro(row.getNmParteNumeroRegistro());
        parte.setNiParteNumeroRegistro(row.getNiParteNumeroRegistro());
        parte.setTiCharParte(row.getTiCharParte());
        parte.setNiMinCharParte(row.getNiMinCharParte());
        parte.setNiMaxCharParte(row.getNiMaxCharParte());
        parte.setTiPadSxParte(row.getTiPadSxParte());
        parte.setTiParte(row.getTiParte());
        parte.setDlValoriParte(row.getDlValoriParte());
        parte.setDsParteNumeroRegistro(row.getDsParteNumeroRegistro());

        String separatore = row.getTiCharSep();
        if (separatore != null && separatore.equals("SPAZIO")) {
            separatore = " ";
        }
        parte.setTiCharSep(separatore);
    }

    public DecErrAaRegistroUnitaDocTableBean getDecErrAaRegistroUnitaDocTableBean(BigDecimal idAaRegistroUnitaDoc) {
        DecErrAaRegistroUnitaDocTableBean tableBean = new DecErrAaRegistroUnitaDocTableBean();
        List<DecErrAaRegistroUnitaDoc> errori = helper.getDecErrAaRegistroUnitaDocList(idAaRegistroUnitaDoc);
        if (errori != null && !errori.isEmpty()) {
            try {
                for (DecErrAaRegistroUnitaDoc errore : errori) {
                    DecErrAaRegistroUnitaDocRowBean row = (DecErrAaRegistroUnitaDocRowBean) Transform
                            .entity2RowBean(errore);
                    BigDecimal idUnitaDoc = row.getIdUnitaDocErrFmtNumero();
                    AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
                    if (ud != null) {
                        row.setString("cd_registro_key_unita_doc", ud.getCdRegistroKeyUnitaDoc());
                        row.setBigDecimal("aa_key_unita_doc", ud.getAaKeyUnitaDoc());
                        row.setString("cd_key_unita_doc", ud.getCdKeyUnitaDoc());
                    }

                    tableBean.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Eccezione", ex);
            }
        }
        return tableBean;
    }
}
