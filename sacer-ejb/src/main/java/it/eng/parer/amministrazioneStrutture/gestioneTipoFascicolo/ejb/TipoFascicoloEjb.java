package it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.amministrazioneStrutture.gestioneTipoFascicolo.helper.TipoFascicoloHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplValoreParamApplic;
import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecAttribFascicolo;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecErrAaTipoFascicolo;
import it.eng.parer.entity.DecModelloXsdAttribFascicolo;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecParteNumeroFascicolo;
import it.eng.parer.entity.DecSelCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.DecUsoModelloXsdFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.IamOrganizDaReplic;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.fascicoli.ejb.FascicoliEjb;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.ejb.util.ObjectsToLogBefore;
import it.eng.parer.sacerlog.ejb.util.PremisEnums;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.DecAaTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecAaTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecAttribFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascTableBean;
import it.eng.parer.slite.gen.tablebean.DecErrAaTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecErrAaTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdAttribFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecParteNumeroFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascTableBean;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.web.ejb.AmministrazioneEjb;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.KeyOrdUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author gilioli_p
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class TipoFascicoloEjb {

    private static final Logger logger = LoggerFactory.getLogger(TipoFascicoloEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private TipoFascicoloHelper helper;
    @EJB
    private FascicoliHelper fascicoliHelper;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogEjb")
    private SacerLogEjb sacerLogEjb;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private FascicoliEjb fascicoliEjb;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    @EJB
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private ConfigurationHelper configurationHelper;

    /**
     * Ritorna il tableBean dei tipi fascicolo di una determinata struttura
     *
     * @param idStrut
     *            id della struttura
     * @param isFilterValid
     *            true ricerca solo i tipi validi alla data odierna
     *
     * @return DecTipoFascicoloTableBean
     */
    public DecTipoFascicoloTableBean getDecTipoFascicoloTableBean(BigDecimal idStrut, boolean isFilterValid) {
        DecTipoFascicoloTableBean tipoFascicoloTableBean = new DecTipoFascicoloTableBean();
        List<DecTipoFascicolo> list = helper.getDecTipoFascicoloList(idStrut, isFilterValid);
        try {
            if (!list.isEmpty()) {
                for (DecTipoFascicolo tipo : list) {
                    DecTipoFascicoloRowBean tipoDocRow = (DecTipoFascicoloRowBean) Transform.entity2RowBean(tipo);
                    if (tipo.getDtIstituz().before(new Date()) && tipo.getDtSoppres().after(new Date())) {
                        tipoDocRow.setObject("fl_attivo", "1");
                    } else {
                        tipoDocRow.setObject("fl_attivo", "0");
                    }
                    tipoFascicoloTableBean.add(tipoDocRow);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero dei tipi fascicolo");
        }
        return tipoFascicoloTableBean;
    }

    /**
     * Ritorna il tableBean dei tipi fascicolo abilitati ad un utente di una determinata struttura
     *
     * @param idUtente
     *            id utente di cui controllare le abilitazioni
     * @param idStrut
     *            id della struttura
     * @param isFilterValid
     *            true ricerca solo i tipi validi alla data odierna
     *
     * @return DecTipoFascicoloTableBean
     */
    public DecTipoFascicoloTableBean getTipiFascicoloAbilitati(long idUtente, BigDecimal idStrut,
            boolean isFilterValid) {
        DecTipoFascicoloTableBean tipoFascicoloTableBean = new DecTipoFascicoloTableBean();
        List<DecTipoFascicolo> list = helper.getTipiFascicoloAbilitati(idUtente, idStrut, isFilterValid);
        try {
            if (!list.isEmpty()) {
                for (DecTipoFascicolo tipo : list) {
                    DecTipoFascicoloRowBean tipoDocRow = (DecTipoFascicoloRowBean) Transform.entity2RowBean(tipo);
                    if (tipo.getDtIstituz().before(new Date()) && tipo.getDtSoppres().after(new Date())) {
                        tipoDocRow.setObject("fl_attivo", "1");
                    } else {
                        tipoDocRow.setObject("fl_attivo", "0");
                    }
                    tipoFascicoloTableBean.add(tipoDocRow);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero dei tipi fascicolo");
        }
        return tipoFascicoloTableBean;
    }

    /**
     * Ritorna il rowBean del tipo fascicolo dato in input il suo id
     *
     * @param idTipoFascicolo
     *            id del tipo fascicolo
     *
     * @return DecTipoFascicoloRowBean
     */
    public DecTipoFascicoloRowBean getDecTipoFascicoloRowBean(BigDecimal idTipoFascicolo) {
        DecTipoFascicoloRowBean tipoFascicoloRowBean = new DecTipoFascicoloRowBean();
        DecTipoFascicolo tipoFascicolo = helper.findById(DecTipoFascicolo.class, idTipoFascicolo);
        try {
            if (tipoFascicolo != null) {
                tipoFascicoloRowBean = (DecTipoFascicoloRowBean) Transform.entity2RowBean(tipoFascicolo);
                tipoFascicoloRowBean.setString("controllo_formato_numero",
                        checkControlloFormatoNumero(idTipoFascicolo));
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero dei dati di dettaglio del tipo fascicolo");
        }
        return tipoFascicoloRowBean;
    }

    public String checkControlloFormatoNumero(BigDecimal idTipoFascicolo) {
        List<DecAaTipoFascicolo> aaTipoFascicoloList = helper.getDecAaTipoFascicoloList(idTipoFascicolo);
        Set<String> chkSet = new HashSet<>();
        for (DecAaTipoFascicolo aaTipoFascicolo : aaTipoFascicoloList) {
            chkSet.add(helper
                    .getDecVChkFmtNumeroFascForPeriodo(BigDecimal.valueOf(aaTipoFascicolo.getIdAaTipoFascicolo())));
        }
        if (!chkSet.isEmpty()) {
            if (chkSet.contains("2")) {
                return "2";
            } else if (chkSet.contains("0")) {
                return "0";
            } else {
                return "1";
            }
        } else {
            return "2";
        }
    }

    /**
     * Ritorna il rowBean del periodo di validità più recente del tipo fascicolo passato in input
     *
     * @param idTipoFascicolo
     *            id del tipo fascicolo
     *
     * @return DecAaTipoFascicoloRowBean
     */
    public DecAaTipoFascicoloRowBean getLastDecAaTipoFascicoloRowBean(BigDecimal idTipoFascicolo) {
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
        DecAaTipoFascicolo aaTipoFascicolo = helper.getLastDecAaTipoFascicolo(idTipoFascicolo);
        try {
            if (aaTipoFascicolo != null) {
                aaTipoFascicoloRowBean = (DecAaTipoFascicoloRowBean) Transform.entity2RowBean(aaTipoFascicolo);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(
                    "Errore durante il recupero dei valori dei parametri definiti sul periodo di validità più recente del tipo accordo");
        }
        return aaTipoFascicoloRowBean;
    }

    /**
     * Ritorna il tableBean dei periodi di validità del tipo fascicolo passato in input
     *
     * @param idTipoFascicolo
     *            id del tipo fascicolo
     *
     * @return DecAaTipoFascicoloTableBean
     */
    public DecAaTipoFascicoloTableBean getDecAaTipoFascicoloTableBean(BigDecimal idTipoFascicolo) {
        DecAaTipoFascicoloTableBean aaTipoFascicoloTableBean = new DecAaTipoFascicoloTableBean();
        List<DecAaTipoFascicolo> aaTipoFascicoloList = helper.getDecAaTipoFascicoloList(idTipoFascicolo);
        try {
            for (DecAaTipoFascicolo aaTipoFascicolo : aaTipoFascicoloList) {
                DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
                aaTipoFascicoloRowBean = (DecAaTipoFascicoloRowBean) Transform.entity2RowBean(aaTipoFascicolo);
                List<DecParteNumeroFascicolo> parti = helper
                        .getDecParteNumeroFascicoloList(aaTipoFascicoloRowBean.getIdAaTipoFascicolo());
                // Calcolo delle colonne Formato numero, Esempio ed Esito controllo formato numero
                aaTipoFascicoloRowBean.setString("nm_parte_numero", calcolaDescrizione(parti));
                aaTipoFascicoloRowBean.setString("aa_tipo_fascicolo_esempio", calcolaEsempio(parti));
                aaTipoFascicoloRowBean.setString("controllo_formato_da_list",
                        helper.getDecVChkFmtNumeroFascForPeriodo(aaTipoFascicoloRowBean.getIdAaTipoFascicolo()));
                aaTipoFascicoloTableBean.add(aaTipoFascicoloRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(
                    "Errore durante il recupero dei dati dei periodi di validità del tipo fascicolo");
        }
        return aaTipoFascicoloTableBean;
    }

    /**
     * Ritorna il tableBean dei criteri di raggruppamento fascicoli del tipo fascicolo passato in input
     *
     * @param idTipoFascicolo
     *            id del tipo fascicolo
     *
     * @return DecCriterioRaggrFascTableBean
     */
    public DecCriterioRaggrFascTableBean getDecCriterioRaggrFascTableBean(BigDecimal idTipoFascicolo) {
        DecCriterioRaggrFascTableBean criterioRaggrFascTableBean = new DecCriterioRaggrFascTableBean();
        List<DecCriterioRaggrFasc> criterioRaggrFascList = helper.getDecCriterioRaggrFascList(idTipoFascicolo);
        try {
            for (DecCriterioRaggrFasc criterioRaggrFasc : criterioRaggrFascList) {
                DecCriterioRaggrFascRowBean criterioRaggrFascRowBean = new DecCriterioRaggrFascRowBean();
                criterioRaggrFascRowBean = (DecCriterioRaggrFascRowBean) Transform.entity2RowBean(criterioRaggrFasc);
                criterioRaggrFascTableBean.add(criterioRaggrFascRowBean);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(
                    "Errore durante il recupero dei dati dei criteri di raggruppamento fascicoli del tipo fascicolo");
        }
        return criterioRaggrFascTableBean;
    }

    /**
     * Ritorna il rowBean del periodo di validità passato in input
     *
     * @param idAaTipoFascicolo
     *            id del periodo tipo fascicolo
     *
     * @return DecAaTipoFascicoloRowBean
     */
    public DecAaTipoFascicoloRowBean getDecAaTipoFascicoloRowBean(BigDecimal idAaTipoFascicolo) {
        DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean = new DecAaTipoFascicoloRowBean();
        DecAaTipoFascicolo aaTipoFascicolo = helper.findById(DecAaTipoFascicolo.class, idAaTipoFascicolo);
        try {
            aaTipoFascicoloRowBean = (DecAaTipoFascicoloRowBean) Transform.entity2RowBean(aaTipoFascicolo);
            aaTipoFascicoloRowBean.setString("controllo_formato",
                    helper.getDecVChkFmtNumeroFascForPeriodo(aaTipoFascicoloRowBean.getIdAaTipoFascicolo()));
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero del periodo di validità del tipo fascicolo");
        }
        return aaTipoFascicoloRowBean;
    }

    /**
     * Ritorna il tableBean "personalizzato" degli errori sul periodo di validità del tipo fascicolo nell'intervallo
     * degli anni del periodo considerando il cdKeyFascicolo del fascicolo
     *
     * @param idAaTipoFascicolo
     *            id periodo di validità del tipo fascicolo
     *
     * @return DecErrAaTipoFascicoloTableBean
     */
    public DecErrAaTipoFascicoloTableBean getDecErrAaTipoFascicoloTableBeanPerIntervallo(BigDecimal idAaTipoFascicolo) {
        DecErrAaTipoFascicoloTableBean errAaTipoFascicoloTableBean = new DecErrAaTipoFascicoloTableBean();
        DecAaTipoFascicolo aaTipoFascicolo = helper.findById(DecAaTipoFascicolo.class, idAaTipoFascicolo);

        for (int aaFascicolo = aaTipoFascicolo.getAaIniTipoFascicolo().intValue(); aaFascicolo <= aaTipoFascicolo
                .getAaFinTipoFascicolo().intValue(); aaFascicolo++) {
            DecErrAaTipoFascicolo errAaTipoFascicolo = helper.getDecErrAaTipoFascicolo(idAaTipoFascicolo, aaFascicolo);
            if (errAaTipoFascicolo != null) {
                DecErrAaTipoFascicoloRowBean errAaTipoFascicoloRowBean = new DecErrAaTipoFascicoloRowBean();
                errAaTipoFascicoloRowBean.setAaFascicolo(errAaTipoFascicolo.getAaFascicolo());
                errAaTipoFascicoloRowBean.setString("cd_key_fascicolo",
                        helper.findById(FasFascicolo.class, errAaTipoFascicolo.getIdFascicoloErrFmtNumero())
                                .getCdKeyFascicolo());
                errAaTipoFascicoloRowBean.setDsErrFmtNumero(errAaTipoFascicolo.getDsErrFmtNumero());
                errAaTipoFascicoloTableBean.add(errAaTipoFascicoloRowBean);
            }
        }
        return errAaTipoFascicoloTableBean;
    }

    /**
     * Metodo di salvataggio e replica su IAM di un nuovo tipo fasicolo
     *
     * @param param
     *            parametri per il logging
     * @param idStrut
     *            id struttura
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     * @param dsTipoFascicolo
     *            descrizione fascicolo
     * @param dtIstituz
     *            data istituzione
     * @param dtSoppres
     *            data soppressione
     *
     * @return Long pk fascicolo inserito
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Long insertTipoFascicolo(LogParam param, BigDecimal idStrut, String nmTipoFascicolo, String dsTipoFascicolo,
            Date dtIstituz, Date dtSoppres) throws ParerUserError {
        TipoFascicoloEjb me = context.getBusinessObject(TipoFascicoloEjb.class);
        // Eseguo il salvataggio del tipo fascicolo in transazione separata
        Object[] obj = me.saveTipoFascicolo(param, idStrut, nmTipoFascicolo, dsTipoFascicolo, dtIstituz, dtSoppres);

        Long idTipoFascicolo = (Long) obj[0];
        IamOrganizDaReplic replic = (IamOrganizDaReplic) obj[1];
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
        // Restituisco l'id del record appena creato
        return idTipoFascicolo;
    }

    /**
     * Metodo di salvataggio di un nuovo criterio di raggruppamento fascicoli standard
     *
     * @param param
     *            parametri per il logging
     * @param idStrut
     *            id struttura
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     * @param idTipoFascicolo
     *            id tipo fascicolo
     *
     * @return Long pk fascicolo inserito
     *
     * @throws ParerUserError
     *             errore generico
     * @throws ParerWarningException
     *             errore generico
     */
    public Long insertCriterioRaggrFascicoloStandard(LogParam param, BigDecimal idStrut, String nmTipoFascicolo,
            BigDecimal idTipoFascicolo) throws ParerUserError, ParerWarningException {
        // Eseguo il salvataggio del criterio raggruppamento fascicoli standard
        long idCriterioRaggrFasc = saveCriterioRaggrFascStandard(param, idStrut, nmTipoFascicolo, idTipoFascicolo);

        // Restituisco l'id del record appena creato
        return idCriterioRaggrFasc;
    }

    /**
     * Metodo di insert di un nuovo tipo fascicolo
     *
     * @param param
     *            parametri per il logging
     * @param idStrut
     *            id struttura
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     * @param dsTipoFascicolo
     *            descrizione tipo fascicolo
     * @param dtIstituz
     *            data istituzione
     * @param dtSoppres
     *            data soppressione
     *
     * @return id del nuovo tipo fascicolo
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Object[] saveTipoFascicolo(LogParam param, BigDecimal idStrut, String nmTipoFascicolo,
            String dsTipoFascicolo, Date dtIstituz, Date dtSoppres) throws ParerUserError {
        logger.debug("Eseguo il salvataggio del tipo fascicolo");
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        // Controllo esistenza denominazione
        if (helper.existsDecTipoFascicoloCaseInsensitive(idStrut, nmTipoFascicolo)) {
            throw new ParerUserError("La tipologia " + nmTipoFascicolo + " è già presente nella struttura");
        }

        Long idTipoFascicolo = null;
        try {
            DecTipoFascicolo tipoFascicolo = insertDecTipoFascicolo(idStrut, nmTipoFascicolo, dsTipoFascicolo,
                    dtIstituz, dtSoppres);

            // Controllo esistenza del Tipo Fascicolo di Sistema ed in caso procedo all'inserimento dello stesso
            if (!helper.existsDecTipoFascicoloCaseInsensitive(idStrut, "Tipo fascicolo sconosciuto")) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, 0);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                c.add(Calendar.DATE, -1);
                Date dtSoppresFascScon = c.getTime();
                c.add(Calendar.DATE, -1);
                Date dtIstituzFascScon = c.getTime();
                insertDecTipoFascicolo(idStrut, "Tipo fascicolo sconosciuto", "Tipo fascicolo sconosciuto",
                        dtIstituzFascScon, dtSoppresFascScon);
            }

            // Salvo in automatico il periodo di validità
            // OrgStrutConfigFascicolo strutConfigFascicolo = helper.getOrgStrutConfigFascicoloByIdStrut(idStrut);
            // if (strutConfigFascicolo != null) {
            DecAaTipoFascicolo aaTipoFascicolo = new DecAaTipoFascicolo();
            aaTipoFascicolo.setAaIniTipoFascicolo(BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
            aaTipoFascicolo.setAaFinTipoFascicolo(BigDecimal.valueOf(2444));

            // aaTipoFascicolo.setFlAbilitaContrClassif(strutConfigFascicolo.getFlAbilitaContrClassif());
            // aaTipoFascicolo.setFlAbilitaContrColleg(strutConfigFascicolo.getFlAbilitaContrColleg());
            // aaTipoFascicolo.setFlAbilitaContrNumero(strutConfigFascicolo.getFlAbilitaContrNumero());
            // aaTipoFascicolo.setFlAccettaContrClassifNeg(strutConfigFascicolo.getFlAccettaContrClassifNeg());
            // aaTipoFascicolo.setFlAccettaContrCollegNeg(strutConfigFascicolo.getFlAccettaContrCollegNeg());
            // aaTipoFascicolo.setFlAccettaContrNumeroNeg(strutConfigFascicolo.getFlAccettaContrNumeroNeg());
            // aaTipoFascicolo.setFlForzaContrClassif(strutConfigFascicolo.getFlForzaContrClassif());
            // aaTipoFascicolo.setFlForzaContrColleg(strutConfigFascicolo.getFlForzaContrColleg());
            // aaTipoFascicolo.setFlForzaContrNumero(strutConfigFascicolo.getFlForzaContrNumero());
            aaTipoFascicolo.setFlUpdFmtNumero("0");
            aaTipoFascicolo.setNiCharPadParteClassif(BigDecimal.valueOf(3));
            aaTipoFascicolo.setDecTipoFascicolo(tipoFascicolo);
            aaTipoFascicolo.setDecParteNumeroFascicolos(new ArrayList<DecParteNumeroFascicolo>());
            helper.insertEntity(aaTipoFascicolo, true);
            tipoFascicolo.getDecAaTipoFascicolos().add(aaTipoFascicolo);

            insertValoriParametriPeriodoFascicolo(
                    BigDecimal.valueOf(tipoFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                    BigDecimal.valueOf(tipoFascicolo.getOrgStrut().getIdStrut()),
                    BigDecimal.valueOf(aaTipoFascicolo.getIdAaTipoFascicolo()));

            // Salva in automatico la parte di default
            DecParteNumeroFascicolo parteNumeroFascicolo = new DecParteNumeroFascicolo();
            parteNumeroFascicolo.setNiParteNumero(BigDecimal.ONE);
            parteNumeroFascicolo.setNmParteNumero("DEFAULT");
            parteNumeroFascicolo.setDsParteNumero("DEFAULT");
            parteNumeroFascicolo.setTiCharParte("GENERICO");
            parteNumeroFascicolo.setNiMinCharParte(BigDecimal.ONE);
            parteNumeroFascicolo.setTiPadParte("NO_RIEMPI");
            parteNumeroFascicolo.setDecAaTipoFascicolo(aaTipoFascicolo);
            helper.insertEntity(parteNumeroFascicolo, true);
            aaTipoFascicolo.getDecParteNumeroFascicolos().add(parteNumeroFascicolo);

            long idAmbiente = tipoFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();
            // Ricerca modello PROFILO_GENERALE_FASCICOLO nel repository
            List<DecModelloXsdFascicolo> modelloXsdFascicoloList = helper.retrieveDecModelloXsdFascicolo(
                    BigDecimal.valueOf(idAmbiente), new Date(), "1", CostantiDB.TiUsoModelloXsd.VERS.name(),
                    CostantiDB.TiModelloXsd.PROFILO_GENERALE_FASCICOLO.name());
            if (!modelloXsdFascicoloList.isEmpty()) {
                if (modelloXsdFascicoloList.size() == 1) {
                    // Salva in automatico l'associazione tra il periodo di validità creato e il modello xsd trovato
                    insertDecUsoModelloXsdFasc(aaTipoFascicolo, modelloXsdFascicoloList.get(0), "1", dtIstituz,
                            dtSoppres);
                } else {
                    throw new ParerUserError(
                            "Errore: nell'ambiente sono stati trovati più modelli xsd di default per i metadati di profilo documento");
                }
            } else {
                throw new ParerUserError(
                        "Attenzione: sull'ambiente non è stato definito l'xsd di default per i metadati di profilo documento");
            }

            // Ricerca modello PROFILO_ARCHIVISTICO_FASCICOLO nel repository
            modelloXsdFascicoloList = helper.retrieveDecModelloXsdFascicolo(BigDecimal.valueOf(idAmbiente), new Date(),
                    "1", CostantiDB.TiUsoModelloXsd.VERS.name(),
                    CostantiDB.TiModelloXsd.PROFILO_ARCHIVISTICO_FASCICOLO.name());
            if (!modelloXsdFascicoloList.isEmpty()) {
                if (modelloXsdFascicoloList.size() == 1) {
                    // Salva in automatico l'associazione tra il periodo di validità creato e il modello xsd trovato
                    insertDecUsoModelloXsdFasc(aaTipoFascicolo, modelloXsdFascicoloList.get(0), "1", dtIstituz,
                            dtSoppres);
                } else {
                    throw new ParerUserError(
                            "Errore: nell'ambiente sono stati trovati più modelli xsd di default per i metadati di profilo archivistico");
                }
            }
            // }

            logger.debug("Salvataggio del tipo fascicolo completato");
            idTipoFascicolo = tipoFascicolo.getIdTipoFascicolo();
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO,
                    new BigDecimal(idTipoFascicolo), param.getNomePagina());
            IamOrganizDaReplic replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura,
                    ApplEnum.TiOperReplic.MOD);
            Object[] idTipoAndReplic = new Object[2];
            idTipoAndReplic[0] = idTipoFascicolo;
            idTipoAndReplic[1] = replic;
            return idTipoAndReplic;
        } catch (ParerUserError ex) {
            logger.error("Errore imprevisto durante il salvataggio del tipo fascicolo : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(ex.getDescription());
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio del tipo fascicolo : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore imprevisto durante il salvataggio del tipo fascicolo");
        }
    }

    /**
     * Metodo di insert di un nuovo criterio di raggruppamento fascicoli standard
     *
     * @param param
     *            parametri per il logging
     * @param idStrut
     *            id struttura
     * @param idTipoFascicolo
     *            id tipo fascicolo
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     *
     * @return id del nuovo criterio di raggruppamento fascicoli
     *
     * @throws ParerUserError
     *             errore generico
     * @throws ParerWarningException
     *             errore generico
     */
    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public Long saveCriterioRaggrFascStandard(LogParam param, BigDecimal idStrut, String nmTipoFascicolo,
            BigDecimal idTipoFascicolo) throws ParerUserError, ParerWarningException {
        logger.debug("Eseguo il salvataggio automatico del criterio di raggruppamento standard");
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        DecTipoFascicolo tipoFascicolo = helper.findById(DecTipoFascicolo.class, idTipoFascicolo);

        // Controllo esistenza criterio di raggruppamento
        String warnMsg = "creazione automatica del criterio di raggruppamento standard non eseguita";
        if (helper.existsDecCriterioRaggrFascStandard(idStrut, idTipoFascicolo)) {
            throw new ParerWarningException("Attenzione: un criterio di raggruppamento standard per il tipo fascicolo '"
                    + nmTipoFascicolo + "' è già presente nella struttura; " + warnMsg);
        }

        if (fascicoliEjb.existNomeCriterio(nmTipoFascicolo, idStrut)) {
            throw new ParerWarningException("Attenzione: un criterio di raggruppamento denominato '" + nmTipoFascicolo
                    + "' è già presente nella struttura; " + warnMsg);
        }

        Long idCriterioRaggrFasc = null;
        try {
            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR, 0);
            now.set(Calendar.MINUTE, 0);
            now.set(Calendar.SECOND, 0);
            now.set(Calendar.MILLISECOND, 0);
            Date dtIstituzCritRaggr = now.getTime();
            Calendar cal = Calendar.getInstance();
            cal.set(2444, Calendar.DECEMBER, 31);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            Date dtSoppresCritRaggr = cal.getTime();

            // Setto il numero di giorni di scadenza chiusura con il valore definito sulla struttura corrente
            long numGgScadChius = fascicoliEjb.getNumGgScadCriterioFascStd(idStrut);
            // Setto il numero massimo fascicoli
            long numMaxFasc = fascicoliEjb.getNumFascCriterioStd(idStrut);

            DecCriterioRaggrFasc criterioRaggrFasc = new DecCriterioRaggrFasc();
            criterioRaggrFasc.setOrgStrut(struttura);
            criterioRaggrFasc.setNmCriterioRaggr(nmTipoFascicolo);
            criterioRaggrFasc.setDsCriterioRaggr("Tipologia di fascicolo " + nmTipoFascicolo);
            criterioRaggrFasc.setDtIstituz(dtIstituzCritRaggr);
            criterioRaggrFasc.setDtSoppres(dtSoppresCritRaggr);
            criterioRaggrFasc.setNiTempoScadChius(new BigDecimal(numGgScadChius));
            criterioRaggrFasc.setTiTempoScadChius(VolumeEnums.TimeTypeEnum.GIORNI.name());
            criterioRaggrFasc.setNiMaxFasc(new BigDecimal(numMaxFasc));
            criterioRaggrFasc.setFlFiltroTipoFascicolo("1");
            criterioRaggrFasc.setFlCriterioRaggrStandard("1");
            criterioRaggrFasc.setFlFiltroVoceTitol("0");
            criterioRaggrFasc.setFlFiltroSistemaMigraz("0");
            criterioRaggrFasc.setDecSelCriterioRaggrFascicoli(new ArrayList<DecSelCriterioRaggrFasc>());
            helper.insertEntity(criterioRaggrFasc, true);

            DecSelCriterioRaggrFasc decSelCriterioRaggrFasc = new DecSelCriterioRaggrFasc();
            decSelCriterioRaggrFasc.setDecTipoFascicolo(tipoFascicolo);
            decSelCriterioRaggrFasc.setDecCriterioRaggrFasc(criterioRaggrFasc);
            decSelCriterioRaggrFasc.setTiSel(ApplEnum.TipoSelCriteriRaggrFasc.TIPO_FASCICOLO.name());
            helper.insertEntity(decSelCriterioRaggrFasc, true);
            criterioRaggrFasc.getDecSelCriterioRaggrFascicoli().add(decSelCriterioRaggrFasc);

            idCriterioRaggrFasc = criterioRaggrFasc.getIdCriterioRaggrFasc();

            logger.debug("Salvataggio automatico del criterio di raggruppamento standard per il tipo fascicolo "
                    + nmTipoFascicolo + " completato");
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC,
                    new BigDecimal(idCriterioRaggrFasc), param.getNomePagina());

            return idCriterioRaggrFasc;
        } catch (Exception ex) {
            logger.error(
                    "Errore imprevisto durante il salvataggio automatico del criterio di raggruppamento standard : "
                            + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
            throw new ParerUserError(
                    "Errore imprevisto durante il salvataggio automatico del criterio di raggruppamento standard");
        }
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRED)
    public DecTipoFascicolo insertDecTipoFascicolo(BigDecimal idStrut, String nmTipoFascicolo, String dsTipoFascicolo,
            Date dtIstituz, Date dtSoppres) {
        DecTipoFascicolo tipoFascicolo = new DecTipoFascicolo();
        tipoFascicolo.setNmTipoFascicolo(nmTipoFascicolo);
        tipoFascicolo.setDsTipoFascicolo(dsTipoFascicolo);
        tipoFascicolo.setDtIstituz(dtIstituz);
        tipoFascicolo.setDtSoppres(dtSoppres);
        OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
        tipoFascicolo.setOrgStrut(strut);
        tipoFascicolo.setDecAaTipoFascicolos(new ArrayList<DecAaTipoFascicolo>());
        helper.insertEntity(tipoFascicolo, true);
        strut.getDecTipoFascicolos().add(tipoFascicolo);
        return tipoFascicolo;
    }

    /**
     * Metodo di modifica e replica su IAM di un tipo "fasicolo" (pronuncia alla bolognese...)
     *
     * @param param
     *            parametri per il logging
     * @param idStrut
     *            id struttura
     * @param idTipoFascicolo
     *            id tipo fascicolo
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     * @param dtIstituz
     *            data istituzione
     * @param dsTipoFascicolo
     *            descrizione tipo fascicolo
     * @param dtSoppres
     *            data soppressione
     *
     * @throws ParerUserError
     *             errore generico
     */
    public void updateTipoFascicolo(LogParam param, BigDecimal idStrut, BigDecimal idTipoFascicolo,
            String nmTipoFascicolo, String dsTipoFascicolo, Date dtIstituz, Date dtSoppres) throws ParerUserError {
        TipoFascicoloEjb me = context.getBusinessObject(TipoFascicoloEjb.class);
        // Eseguo il salvataggio del tipo fascicolo in transazione separata
        IamOrganizDaReplic replic = me.saveTipoFascicolo(param, idStrut, idTipoFascicolo, nmTipoFascicolo,
                dsTipoFascicolo, dtIstituz, dtSoppres);
        if (replic != null) {
            struttureEjb.replicateToIam(replic);
        }
    }

    /**
     * Metodo di update di un tipo fascicolo
     *
     * @param param
     *            parametri per il logging
     * @param idStrut
     *            id struttura
     * @param idTipoFascicolo
     *            id tipo fascicolo
     * @param nmTipoFascicolo
     *            nome tipo fascicolo
     * @param dsTipoFascicolo
     *            descrizione tipo fascicolo
     * @param dtIstituz
     *            data istituzione
     * @param dtSoppres
     *            data soppressione
     *
     * @return IamOrganizDaReplic entity
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IamOrganizDaReplic saveTipoFascicolo(LogParam param, BigDecimal idStrut, BigDecimal idTipoFascicolo,
            String nmTipoFascicolo, String dsTipoFascicolo, Date dtIstituz, Date dtSoppres) throws ParerUserError {
        logger.debug("Eseguo il salvataggio del tipo fascicolo");
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        DecTipoFascicolo tipoFascicolo = helper.findById(DecTipoFascicolo.class, idTipoFascicolo);

        // Controllo esistenza denominazione
        if (!tipoFascicolo.getNmTipoFascicolo().equals(nmTipoFascicolo)
                && helper.existsDecTipoFascicoloCaseInsensitive(idStrut, nmTipoFascicolo)) {
            throw new ParerUserError("La tipologia " + nmTipoFascicolo + " è già presente nella struttura");
        }

        tipoFascicolo.setNmTipoFascicolo(nmTipoFascicolo);
        tipoFascicolo.setDsTipoFascicolo(dsTipoFascicolo);
        tipoFascicolo.setDtIstituz(dtIstituz);
        tipoFascicolo.setDtSoppres(dtSoppres);
        OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
        tipoFascicolo.setOrgStrut(strut);
        tipoFascicolo.setDecAaTipoFascicolos(new ArrayList<DecAaTipoFascicolo>());

        logger.debug("Salvataggio del tipo fascicolo completato");
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO,
                new BigDecimal(tipoFascicolo.getIdTipoFascicolo()), param.getNomePagina());

        IamOrganizDaReplic replic = struttureEjb.insertStrutIamOrganizDaReplic(struttura, ApplEnum.TiOperReplic.MOD);

        return replic;
    }

    public Long insertDecUsoModelloXsdFasc(DecAaTipoFascicolo aaTipoFascicolo,
            DecModelloXsdFascicolo modelloXsdFascicolo, String flStandard, Date dtIstituz, Date dtSoppres) {
        DecUsoModelloXsdFasc usoModelloXsdFasc = new DecUsoModelloXsdFasc();
        usoModelloXsdFasc.setDecAaTipoFascicolo(aaTipoFascicolo);
        usoModelloXsdFasc.setDecModelloXsdFascicolo(modelloXsdFascicolo);
        usoModelloXsdFasc.setFlStandard(flStandard);
        usoModelloXsdFasc.setDtIstituz(dtIstituz);
        usoModelloXsdFasc.setDtSoppres(dtSoppres);
        if (aaTipoFascicolo.getDecUsoModelloXsdFascs() == null) {
            aaTipoFascicolo.setDecUsoModelloXsdFascs(new ArrayList<DecUsoModelloXsdFasc>());
        }
        aaTipoFascicolo.getDecUsoModelloXsdFascs().add(usoModelloXsdFasc);
        if (modelloXsdFascicolo.getDecUsoModelloXsdFascs() == null) {
            modelloXsdFascicolo.setDecUsoModelloXsdFascs(new ArrayList<DecUsoModelloXsdFasc>());
        }
        modelloXsdFascicolo.getDecUsoModelloXsdFascs().add(usoModelloXsdFasc);
        helper.insertEntity(usoModelloXsdFasc, true);
        return usoModelloXsdFasc.getIdUsoModelloXsdFasc();
    }

    public boolean existsFascicoloVersatoPerTipoFascicolo(BigDecimal idTipoFascicolo) {
        return fascicoliHelper.existFascicoliVersatiPerTipoFascicolo(idTipoFascicolo, null, null);
    }

    public boolean existsFascicoloVersatoPerStruttura(BigDecimal idStrut) {
        return fascicoliHelper.existFascicoliVersatiPerStruttura(idStrut);
    }

    /**
     * Metodo di eliminazione di un tipo fascicolo
     *
     * @param param
     *            parametri per il logging
     * @param idTipoFascicolo
     *            id tipo fascicolo
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDecTipoFascicolo(LogParam param, BigDecimal idTipoFascicolo) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione del tipo fascicolo " + idTipoFascicolo);

        DecTipoFascicolo tipoFascicolo = helper.findById(DecTipoFascicolo.class, idTipoFascicolo);

        if (existsFascicoloVersatoPerTipoFascicolo(idTipoFascicolo)) {
            throw new ParerUserError(
                    "Impossibile eliminare il tipo fascicolo: esiste almeno un fascicolo versato con questa tipologia");
        }

        List<ObjectsToLogBefore> listaBefore = sacerLogEjb.logBefore(param.getTransactionLogContext(),
                param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
                SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO, idTipoFascicolo, param.getNomePagina());
        List<ObjectsToLogBefore> listaBeforeDeletion = ObjectsToLogBefore.filterObjectsForDeletion(listaBefore);
        List<ObjectsToLogBefore> listaBeforeModifying = filterObjectsForModifying(listaBefore, listaBeforeDeletion);

        /* In questo caso gli oggetti vengono fotografati prima perché spariranno completamente */
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaBeforeDeletion, param.getNomePagina());

        List<DecSelCriterioRaggrFasc> criteriAssociati = struttureHelper
                .getRelationsWithCriteriRaggrFascicolo(idTipoFascicolo.longValue(), Constants.TipoDato.TIPO_FASCICOLO);
        if (!criteriAssociati.isEmpty()) {
            List<Long> criteriRaggrFascDaEliminare = new ArrayList<>();
            List<Long> criteriRaggrFascDaModificare = new ArrayList<>();
            for (DecSelCriterioRaggrFasc criterioAssociato : criteriAssociati) {
                long idCriterioRaggrFasc = criterioAssociato.getDecCriterioRaggrFasc().getIdCriterioRaggrFasc();
                boolean existsRelationsWithElenchiForCriterioRaggrFasc = struttureHelper
                        .existsRelationsWithElenchiForCriterioRaggrFasc(idCriterioRaggrFasc);
                if (existsRelationsWithElenchiForCriterioRaggrFasc) {
                    throw new ParerUserError("Il criterio è collegato a degli elenchi di versamento fascicoli</br>");
                }
                if ((criterioAssociato.getDecCriterioRaggrFasc().getFlFiltroVoceTitol().equals("1")
                        || criterioAssociato.getDecCriterioRaggrFasc().getFlFiltroSistemaMigraz().equals("1")) // TODO:
                        // da
                        // verificare
                        // SISTEMA_MIGRAZ)
                        && fascicoliHelper.countSelCriteriRaggrFascPerTipo(new BigDecimal(idCriterioRaggrFasc),
                                Constants.TipoDato.TIPO_FASCICOLO.name()) == 1) {
                    criteriRaggrFascDaModificare.add(idCriterioRaggrFasc);
                } else {
                    criteriRaggrFascDaEliminare.add(idCriterioRaggrFasc);
                }
            }
            // Se sono arrivato fin qui, quindi non è scattata l'eccezione e i criteri non sono associati ad altri
            // filtri, posso eliminare i criteri
            fascicoliHelper.bulkDeleteCriteriRaggrFasc(criteriRaggrFascDaEliminare);
            fascicoliHelper.bulkUpdateCriteriRaggrFasc(criteriRaggrFascDaModificare);
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO, idTipoFascicolo,
                param.getNomePagina());
        // Rimozione a cascata
        helper.removeEntity(tipoFascicolo, true);
        /* Foto dopo eliminazione di eventuali disassociazioni */
        sacerLogEjb.logAfter(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), listaBeforeModifying, param.getNomePagina());
    }

    /**
     * Metodo che filtra gli oggetti aventi come classe evento "MODIFICA" escludendo, se presenti, gli oggetti aventi
     * anche la classe evento "CANCELLATI"
     *
     * @param sourceList,
     *            lista contenente tutti i criteri coinvolti a seguito dell'eliminazione del tipo fascicolo
     * @param listaBeforeDeletion,
     *            lista filtrata per contenere i criteri da eliminare a seguito dell'eliminazione del tipo fascicolo
     *
     * @throws ParerUserError
     *             errore generico
     */
    private List<ObjectsToLogBefore> filterObjectsForModifying(List<ObjectsToLogBefore> sourceList,
            List<ObjectsToLogBefore> listaBeforeDeletion) {
        ArrayList<ObjectsToLogBefore> destList = null;
        if (sourceList != null) {
            destList = new ArrayList();
            for (ObjectsToLogBefore source : sourceList) {
                if (source.getClasseEvento().equals(PremisEnums.TipoClasseEvento.MODIFICA.name())) {
                    for (ObjectsToLogBefore deletion : listaBeforeDeletion) {
                        source.getIdOggetto().removeAll(deletion.getIdOggetto());
                    }
                    if (!source.getIdOggetto().isEmpty()) {
                        destList.add(source);
                    }
                }
            }
        }
        return destList;
    }

    private String calcolaDescrizione(List<DecParteNumeroFascicolo> parti) {
        String descr = "";
        for (DecParteNumeroFascicolo parte : parti) {
            descr += parte.getDsParteNumero();
            descr += ",";
        }
        return StringUtils.chop(descr);
    }

    private String calcolaEsempio(List<DecParteNumeroFascicolo> parti) {
        String esempio = "";
        int index = 0;
        for (DecParteNumeroFascicolo parte : parti) {
            index++;
            if (!StringUtils.isEmpty(parte.getDlValoriParte())) {
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
                        switch (j % 3) {
                        case 0:
                            esempio += "A";
                            break;
                        case 1:
                            esempio += "1";
                            break;
                        default:
                            esempio += "_";
                            break;
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

    public DecUsoModelloXsdFascTableBean getVersioniXsdMetadati(BigDecimal idAaTipoFascicolo,
            CostantiDB.TiModelloXsd tiModelloXsd) {
        DecUsoModelloXsdFascTableBean usoModelloXsdFascTableBean = new DecUsoModelloXsdFascTableBean();
        List<DecUsoModelloXsdFasc> usoModelloXsdFascList = helper.getDecUsoModelloXsdFascList(idAaTipoFascicolo, null,
                null);
        for (DecUsoModelloXsdFasc usoModelloXsdFasc : usoModelloXsdFascList) {
            if (usoModelloXsdFasc.getDecModelloXsdFascicolo().getTiModelloXsd().equals(tiModelloXsd.name())) {
                DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
                usoModelloXsdFascRowBean.setBigDecimal("id_uso_modello_xsd_fasc",
                        BigDecimal.valueOf(usoModelloXsdFasc.getIdUsoModelloXsdFasc()));
                usoModelloXsdFascRowBean.setBigDecimal("id_modello_xsd_fascicolo",
                        BigDecimal.valueOf(usoModelloXsdFasc.getDecModelloXsdFascicolo().getIdModelloXsdFascicolo()));
                usoModelloXsdFascRowBean.setBigDecimal("id_aa_tipo_fascicolo",
                        BigDecimal.valueOf(usoModelloXsdFasc.getDecAaTipoFascicolo().getIdAaTipoFascicolo()));
                usoModelloXsdFascRowBean.setString("cd_xsd", usoModelloXsdFasc.getDecModelloXsdFascicolo().getCdXsd());
                usoModelloXsdFascRowBean.setString("ds_xsd", usoModelloXsdFasc.getDecModelloXsdFascicolo().getDsXsd());
                usoModelloXsdFascRowBean.setString("ti_modello_xsd",
                        usoModelloXsdFasc.getDecModelloXsdFascicolo().getTiModelloXsd());
                usoModelloXsdFascRowBean.setFlStandard(usoModelloXsdFasc.getFlStandard());
                usoModelloXsdFascRowBean.setObject("dt_istituz",
                        new Timestamp(usoModelloXsdFasc.getDtIstituz().getTime()));
                usoModelloXsdFascRowBean.setObject("dt_soppres",
                        new Timestamp(usoModelloXsdFasc.getDtSoppres().getTime()));
                if (usoModelloXsdFasc.getDecModelloXsdFascicolo().getDtIstituz().before(new Date())
                        && usoModelloXsdFasc.getDecModelloXsdFascicolo().getDtSoppres().after(new Date())) {
                    usoModelloXsdFascRowBean.setString("fl_attivo", "1");
                } else {
                    usoModelloXsdFascRowBean.setString("fl_attivo", "0");
                }

                usoModelloXsdFascTableBean.add(usoModelloXsdFascRowBean);
            }
        }
        return usoModelloXsdFascTableBean;
    }

    public DecUsoModelloXsdFascRowBean getVersioneXsdMetadati(BigDecimal idUsoModelloXsdFasc) throws ParerUserError {
        DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
        DecUsoModelloXsdFasc usoModelloXsdFasc = helper.findById(DecUsoModelloXsdFasc.class, idUsoModelloXsdFasc);
        usoModelloXsdFascRowBean = getDecUsoModelloXsdFascRowBean(usoModelloXsdFasc);
        return usoModelloXsdFascRowBean;
    }

    public DecUsoModelloXsdFascRowBean getVersioneXsdMetadati(BigDecimal idAaTipoFascicolo,
            BigDecimal idModelloXsdFascicolo) throws ParerUserError {
        DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
        List<DecUsoModelloXsdFasc> usoModelloXsdFascList = helper.getDecUsoModelloXsdFascList(idAaTipoFascicolo,
                idModelloXsdFascicolo, null);
        DecUsoModelloXsdFasc usoModelloXsdFasc = usoModelloXsdFascList.get(0);
        usoModelloXsdFascRowBean = getDecUsoModelloXsdFascRowBean(usoModelloXsdFasc);
        return usoModelloXsdFascRowBean;
    }

    private DecUsoModelloXsdFascRowBean getDecUsoModelloXsdFascRowBean(DecUsoModelloXsdFasc usoModelloXsdFasc)
            throws ParerUserError {
        try {
            DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
            usoModelloXsdFascRowBean = (DecUsoModelloXsdFascRowBean) Transform.entity2RowBean(usoModelloXsdFasc);
            usoModelloXsdFascRowBean.setString("cd_xsd", usoModelloXsdFasc.getDecModelloXsdFascicolo().getCdXsd());
            usoModelloXsdFascRowBean.setString("ds_xsd", usoModelloXsdFasc.getDecModelloXsdFascicolo().getDsXsd());
            usoModelloXsdFascRowBean.setString("ti_modello_xsd",
                    usoModelloXsdFasc.getDecModelloXsdFascicolo().getTiModelloXsd());
            usoModelloXsdFascRowBean.setObject("dt_istituz",
                    new Timestamp(usoModelloXsdFasc.getDecModelloXsdFascicolo().getDtIstituz().getTime()));
            usoModelloXsdFascRowBean.setObject("dt_soppres",
                    new Timestamp(usoModelloXsdFasc.getDecModelloXsdFascicolo().getDtSoppres().getTime()));
            XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
            String xmlFormatted = formatter
                    .prettyPrintWithDOM3LS(usoModelloXsdFasc.getDecModelloXsdFascicolo().getBlXsd());
            usoModelloXsdFascRowBean.setString("bl_xsd", xmlFormatted);
            return usoModelloXsdFascRowBean;
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException ex) {
            logger.error("Errore durante il caricamento del dettaglio di un XSD di metadati profilo" + ex.getMessage(),
                    ex);
            throw new ParerUserError("Errore durante il caricamento del dettaglio di un XSD di metadati profilo");
        }
    }

    /**
     * Ritorna il tableBean delle parti di un periodo di validità di un tipo fascicolo
     *
     * @param idAaTipoFascicolo
     *            id anno tipo fascicolo
     *
     * @return DecParteNumeroFascicoloTableBean bean parte numero fascicolo
     */
    public DecParteNumeroFascicoloTableBean getDecParteNumeroFascicoloTableBean(BigDecimal idAaTipoFascicolo) {
        DecParteNumeroFascicoloTableBean parteNumeroFascicoloTableBean = new DecParteNumeroFascicoloTableBean();
        List<DecParteNumeroFascicolo> list = helper.getDecParteNumeroFascicoloList(idAaTipoFascicolo);
        try {
            if (!list.isEmpty()) {
                parteNumeroFascicoloTableBean = (DecParteNumeroFascicoloTableBean) Transform.entities2TableBean(list);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException(
                    "Errore durante il recupero delle parti del periodo di validità del tipo fascicolo");
        }
        return parteNumeroFascicoloTableBean;
    }

    public boolean existPeriodiValiditaSovrappostiFascicoli(BigDecimal idAaTipoFascicolo, BigDecimal idTipoFascicolo,
            BigDecimal aaIniTipoFascicolo, BigDecimal aaFinTipoFascicolo) {
        logger.info("Verifico che il periodo indicato non si sovrapponga ad altri periodi");
        return helper.existPeriodiValiditaSovrappostiFascicoli(idAaTipoFascicolo, idTipoFascicolo, aaIniTipoFascicolo,
                aaFinTipoFascicolo);
    }

    public DecModelloXsdFascicoloTableBean getDecModelloXsdFascicoloTableBean(BigDecimal idAmbiente, Date data,
            String flDefault, String tiUsoModelloXsd, String tiModelloXsd) {
        DecModelloXsdFascicoloTableBean modelloXsdFascicoloTableBean = new DecModelloXsdFascicoloTableBean();
        List<DecModelloXsdFascicolo> modelloXsdFascicoloList = helper.retrieveDecModelloXsdFascicolo(idAmbiente, data,
                flDefault, tiUsoModelloXsd, tiModelloXsd);
        try {
            for (DecModelloXsdFascicolo modelloXsdFascicolo : modelloXsdFascicoloList) {
                DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = new DecModelloXsdFascicoloRowBean();
                modelloXsdFascicoloRowBean = (DecModelloXsdFascicoloRowBean) Transform
                        .entity2RowBean(modelloXsdFascicolo);
                modelloXsdFascicoloRowBean.setString("codice_descrizione",
                        modelloXsdFascicolo.getCdXsd() + " - " + modelloXsdFascicolo.getDsXsd());
                modelloXsdFascicoloTableBean.add(modelloXsdFascicoloRowBean);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero dei modelli xsd fascicolo");
        }
        return modelloXsdFascicoloTableBean;
    }

    public DecModelloXsdFascicoloRowBean getDecModelloXsdFascicoloRowBean(BigDecimal idModelloXsdFascicolo) {
        DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean = new DecModelloXsdFascicoloRowBean();
        DecModelloXsdFascicolo modelloXsdFascicolo = helper.findById(DecModelloXsdFascicolo.class,
                idModelloXsdFascicolo);
        try {
            if (modelloXsdFascicolo != null) {
                modelloXsdFascicoloRowBean = (DecModelloXsdFascicoloRowBean) Transform
                        .entity2RowBean(modelloXsdFascicolo);
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                String xmlFormatted = formatter.prettyPrintWithDOM3LS(modelloXsdFascicoloRowBean.getBlXsd());
                modelloXsdFascicoloRowBean.setString("nm_ambiente",
                        modelloXsdFascicolo.getOrgAmbiente().getNmAmbiente());
                modelloXsdFascicoloRowBean.setBlXsd(xmlFormatted);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero del modello xsd fascicolo");
        }
        return modelloXsdFascicoloRowBean;
    }

    public DecUsoModelloXsdFascRowBean getDecUsoModelloXsdFascRowBean(BigDecimal idUsoModelloXsdFasc) {
        DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
        DecUsoModelloXsdFasc usoModelloXsdFasc = helper.findById(DecUsoModelloXsdFasc.class, idUsoModelloXsdFasc);
        try {
            if (usoModelloXsdFasc != null) {
                usoModelloXsdFascRowBean = (DecUsoModelloXsdFascRowBean) Transform.entity2RowBean(usoModelloXsdFasc);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new IllegalStateException("Errore durante il recupero del modello xsd fascicolo in uso");
        }
        return usoModelloXsdFascRowBean;
    }

    /**
     * Metodo di eliminazione di un periodo tipo fascicolo
     *
     * @param param
     *            parametri per il logging
     * @param idAaTipoFascicolo
     *            id anno tipo fascicolo
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecAaTipoFascicolo(LogParam param, BigDecimal idAaTipoFascicolo) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione del periodo tipo fascicolo " + idAaTipoFascicolo);

        DecAaTipoFascicolo aaTipoFascicolo = helper.findById(DecAaTipoFascicolo.class, idAaTipoFascicolo);
        Long idTipoFascicolo = aaTipoFascicolo.getDecTipoFascicolo().getIdTipoFascicolo();

        if (fascicoliHelper.existFascicoliVersatiPerTipoFascicolo(BigDecimal.valueOf(idTipoFascicolo),
                aaTipoFascicolo.getAaIniTipoFascicolo(), aaTipoFascicolo.getAaFinTipoFascicolo())) {
            throw new ParerUserError("Nel range di validità risultano versati dei fascicoli. Impossibile cancellare");
        }

        // Rimozione a cascata
        helper.removeEntity(aaTipoFascicolo, true);

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO,
                BigDecimal.valueOf(idTipoFascicolo), param.getNomePagina());
    }

    /**
     * Metodo di eliminazione di un criterio di raggruppamento fascicoli
     *
     * @param param
     *            parametri per il logging
     * @param idCriterioRaggrFasc
     *            id criterio raggruppamente fascicolo
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecCriterioRaggrFasc(LogParam param, BigDecimal idCriterioRaggrFasc) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione del criterio di raggruppamento fascicoli " + idCriterioRaggrFasc);

        DecCriterioRaggrFasc critRaggrFasc = helper.findById(DecCriterioRaggrFasc.class, idCriterioRaggrFasc);

        if (fascicoliHelper.existElvElencoVersPerCriterioRaggrFasc(idCriterioRaggrFasc)) {
            throw new ParerUserError(
                    "Per il criterio di raggruppamento fascicoli risultano versati dei fascicoli. Impossibile cancellare");
        }

        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGR_FASC, idCriterioRaggrFasc,
                param.getNomePagina());

        // Rimozione a cascata
        helper.removeEntity(critRaggrFasc, true);
    }

    /**
     * Modifica periodo validità tipo fascicolo
     *
     * @param param
     *            parametri per il logging
     * @param aaTipoFascicoloRowBean
     *            bean riga anno tipo fascicolo
     * @param metadatiProfiloFascicoloTableBean
     *            bean metadati profilo fascicolo
     * @param metadatiProfiloArchivisticoTableBean
     *            bean metadati profilo archivistico
     * @param idMetadatiFascicoloEliminati
     *            id metadati fascicolo eliminati
     * @param idMetadatiArchivisticoEliminati
     *            id metadata profilo archivistico eliminato
     * @param parti
     *            decodifica parte
     * @param idPartiEliminate
     *            id parte eliminata
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveDecAaTipoFascicolo(LogParam param, DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean,
            DecUsoModelloXsdFascTableBean metadatiProfiloFascicoloTableBean,
            Set<BigDecimal> idMetadatiFascicoloEliminati,
            DecUsoModelloXsdFascTableBean metadatiProfiloArchivisticoTableBean,
            Set<BigDecimal> idMetadatiArchivisticoEliminati, DecParteNumeroFascicoloTableBean parti,
            Set<BigDecimal> idPartiEliminate) throws ParerUserError {
        //
        DecAaTipoFascicolo aaTipoFascicolo = helper.findById(DecAaTipoFascicolo.class,
                aaTipoFascicoloRowBean.getIdAaTipoFascicolo());
        DecTipoFascicolo tipoFascicolo = aaTipoFascicolo.getDecTipoFascicolo();
        //
        if (aaTipoFascicoloRowBean.getAaFinTipoFascicolo() == null) {
            aaTipoFascicoloRowBean.setAaFinTipoFascicolo(new BigDecimal(2444));
        }

        // Se ho ristretto da SINISTRA il range del periodo di validità (quindi l'anno iniziale è aumentato...)
        if (aaTipoFascicolo.getAaIniTipoFascicolo().compareTo(aaTipoFascicoloRowBean.getAaIniTipoFascicolo()) < 0) {
            logger.info(
                    "\u00e8 stato modificato l'inizio validit\u00E0, verifico che non esistano fascicoli versati nel range escluso attualmente");
            if (fascicoliHelper.existFascicoliVersatiPerTipoFascicolo(
                    BigDecimal.valueOf(tipoFascicolo.getIdTipoFascicolo()), aaTipoFascicolo.getAaIniTipoFascicolo(),
                    aaTipoFascicoloRowBean.getAaIniTipoFascicolo().subtract(BigDecimal.ONE))) {
                cloneDecAaTipoFascicolo(tipoFascicolo, aaTipoFascicolo, aaTipoFascicolo.getAaIniTipoFascicolo(),
                        aaTipoFascicoloRowBean.getAaIniTipoFascicolo().subtract(BigDecimal.ONE));
            }
        }

        // Se ho ristretto da DESTRA il range del periodo di validità (quindi l'anno finale è diminuito...)
        if (aaTipoFascicolo.getAaFinTipoFascicolo().compareTo(aaTipoFascicoloRowBean.getAaFinTipoFascicolo()) > 0) {
            logger.info(
                    "\u00e8 stata modificata la fine validit\u00E0, verifico che non esistano fascicoli nel range escluso attualmente");
            if (fascicoliHelper.existFascicoliVersatiPerTipoFascicolo(
                    BigDecimal.valueOf(tipoFascicolo.getIdTipoFascicolo()),
                    aaTipoFascicoloRowBean.getAaFinTipoFascicolo().add(BigDecimal.ONE),
                    aaTipoFascicolo.getAaFinTipoFascicolo())) {
                cloneDecAaTipoFascicolo(tipoFascicolo, aaTipoFascicolo,
                        aaTipoFascicoloRowBean.getAaFinTipoFascicolo().add(BigDecimal.ONE),
                        aaTipoFascicolo.getAaFinTipoFascicolo());
            }
        }

        /* Elimino i metadati e le parti da eliminare */
        if (idMetadatiFascicoloEliminati != null && !idMetadatiFascicoloEliminati.isEmpty()) {
            logger.info("Elimino i metadati profilo fascicolo eliminati");
            for (BigDecimal idMeta : idMetadatiFascicoloEliminati) {
                DecUsoModelloXsdFasc meta = helper.findById(DecUsoModelloXsdFasc.class, idMeta);
                helper.removeEntity(meta, true);
            }
        }

        if (idMetadatiArchivisticoEliminati != null && !idMetadatiArchivisticoEliminati.isEmpty()) {
            logger.info("Elimino i metadati profilo archivistico eliminati");
            for (BigDecimal idMeta : idMetadatiArchivisticoEliminati) {
                DecUsoModelloXsdFasc meta = helper.findById(DecUsoModelloXsdFasc.class, idMeta);
                helper.removeEntity(meta, true);
            }
        }

        if (idPartiEliminate != null && !idPartiEliminate.isEmpty()) {
            logger.info("Elimino le parti eliminate");
            for (BigDecimal idParte : idPartiEliminate) {
                DecParteNumeroFascicolo parte = helper.findById(DecParteNumeroFascicolo.class, idParte);
                helper.removeEntity(parte, true);
            }
        }

        // Salvo la modifica del range di validit\u00E0 del tipo fascicolo
        logger.info("Salvo il periodo di validit\u00E0 del tipo fascicolo");
        aaTipoFascicolo.setAaIniTipoFascicolo(aaTipoFascicoloRowBean.getAaIniTipoFascicolo());
        aaTipoFascicolo.setAaFinTipoFascicolo(aaTipoFascicoloRowBean.getAaFinTipoFascicolo());
        aaTipoFascicolo.setFlUpdFmtNumero("1");
        aaTipoFascicolo.setNiCharPadParteClassif(aaTipoFascicoloRowBean.getNiCharPadParteClassif());
        // aaTipoFascicolo.setFlAbilitaContrClassif(aaTipoFascicoloRowBean.getFlAbilitaContrClassif());
        // aaTipoFascicolo.setFlAbilitaContrColleg(aaTipoFascicoloRowBean.getFlAbilitaContrColleg());
        // aaTipoFascicolo.setFlAbilitaContrNumero(aaTipoFascicoloRowBean.getFlAbilitaContrNumero());
        // aaTipoFascicolo.setFlAccettaContrClassifNeg(aaTipoFascicoloRowBean.getFlAccettaContrClassifNeg());
        // aaTipoFascicolo.setFlAccettaContrCollegNeg(aaTipoFascicoloRowBean.getFlAccettaContrCollegNeg());
        // aaTipoFascicolo.setFlAccettaContrNumeroNeg(aaTipoFascicoloRowBean.getFlAccettaContrNumeroNeg());
        // aaTipoFascicolo.setFlForzaContrClassif(aaTipoFascicoloRowBean.getFlForzaContrClassif());
        // aaTipoFascicolo.setFlForzaContrColleg(aaTipoFascicoloRowBean.getFlForzaContrColleg());
        // aaTipoFascicolo.setFlForzaContrNumero(aaTipoFascicoloRowBean.getFlForzaContrNumero());

        logger.info("Inserisco o aggiorno i metadati di tipo profilo fascicolo del periodo considerato");
        for (DecUsoModelloXsdFascRowBean row : metadatiProfiloFascicoloTableBean) {
            if (row.getIdUsoModelloXsdFasc() != null) {
                DecUsoModelloXsdFasc meta = helper.findById(DecUsoModelloXsdFasc.class, row.getIdUsoModelloXsdFasc());
                meta.setFlStandard(row.getFlStandard());
                meta.setDecModelloXsdFascicolo(
                        helper.findById(DecModelloXsdFascicolo.class, row.getIdModelloXsdFascicolo()));
                meta.setDtIstituz(row.getDtIstituz());
                meta.setDtSoppres(row.getDtSoppres());

                aaTipoFascicolo.addDecUsoModelloXsdFasc(meta);

                saveXsdAttribList(meta, row);
            } else {
                inserisciMetadatiProfilo(row, aaTipoFascicolo);
            }
        }

        logger.info("Inserisco o aggiorno i metadati di tipo profilo archivistico del periodo considerato");
        for (DecUsoModelloXsdFascRowBean row : metadatiProfiloArchivisticoTableBean) {
            if (row.getIdUsoModelloXsdFasc() != null) {
                DecUsoModelloXsdFasc meta = helper.findById(DecUsoModelloXsdFasc.class, row.getIdUsoModelloXsdFasc());
                meta.setFlStandard(row.getFlStandard());
                meta.setDecModelloXsdFascicolo(
                        helper.findById(DecModelloXsdFascicolo.class, row.getIdModelloXsdFascicolo()));
                meta.setDtIstituz(row.getDtIstituz());
                meta.setDtSoppres(row.getDtSoppres());

                aaTipoFascicolo.addDecUsoModelloXsdFasc(meta);

                saveXsdAttribList(meta, row);
            } else {
                inserisciMetadatiProfilo(row, aaTipoFascicolo);
            }
        }

        logger.info("Inserisco o aggiorno le parti");
        for (DecParteNumeroFascicoloRowBean row : parti) {
            if (row.getIdParteNumeroFascicolo() != null) {
                DecParteNumeroFascicolo parte = helper.findById(DecParteNumeroFascicolo.class,
                        row.getIdParteNumeroFascicolo());
                updateParteNumeroFascicolo(parte, row);
            } else {
                DecParteNumeroFascicolo parte = new DecParteNumeroFascicolo();
                updateParteNumeroFascicolo(parte, row);

                aaTipoFascicolo.addDecParteNumeroFascicolo(parte);
            }
        }

        // // Gestione parametri amministrazione
        // manageParametriPerAaTipoFascicolo(parametriAmministrazioneAaTipoFasc,
        // "ds_valore_param_applic_aa_tipo_fascicolo_amm", aaTipoFascicolo);
        // // Gestione parametri conservazione
        // manageParametriPerAaTipoFascicolo(parametriConservazioneAaTipoFasc,
        // "ds_valore_param_applic_aa_tipo_fascicolo_cons", aaTipoFascicolo);
        // // Gestione parametri gestione
        // manageParametriPerAaTipoFascicolo(parametriGestioneAaTipoFasc,
        // "ds_valore_param_applic_aa_tipo_fascicolo_gest", aaTipoFascicolo);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO,
                new BigDecimal(tipoFascicolo.getIdTipoFascicolo()), param.getNomePagina());
    }

    private void manageParametriPerAaTipoFascicolo(AplParamApplicTableBean paramApplicTableBean,
            String nomeCampoValoreParamApplic, DecAaTipoFascicolo aaTipoFascicolo) {
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
                amministrazioneEjb.insertAplValoreParamApplic(null, null, null, aaTipoFascicolo,
                        paramApplicRowBean.getBigDecimal("id_param_applic"), "PERIODO_TIPO_FASC",
                        paramApplicRowBean.getString(nomeCampoValoreParamApplic));
            }
        }
    }

    /**
     * Inserimento periodo validità tipo fascicolo
     *
     * @param param
     *            bean metadati profilo fascicolo
     * @param idTipoFascicolo
     *            id tipo fascicolo
     * @param aaTipoFascicoloRowBean
     *            bean con anno tipo fascicolo
     * @param metadatiProfiloArchivistico
     *            decodifica metadati profilo archivistico
     * @param metadatiProfiloFascicolo
     *            decodifica metadati profilo fascicolo
     * @param parti
     *            decodifica parti numero fascicolo
     *
     * @return BigDecimal pk anno tipo fascicolo
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public BigDecimal saveDecAaTipoFascicolo(LogParam param, BigDecimal idTipoFascicolo,
            DecAaTipoFascicoloRowBean aaTipoFascicoloRowBean, DecUsoModelloXsdFascTableBean metadatiProfiloFascicolo,
            DecUsoModelloXsdFascTableBean metadatiProfiloArchivistico, DecParteNumeroFascicoloTableBean parti)
            throws ParerUserError {
        DecAaTipoFascicolo aaTipoFascicolo = new DecAaTipoFascicolo();
        DecTipoFascicolo tipoFascicolo = helper.findById(DecTipoFascicolo.class, idTipoFascicolo);

        if (tipoFascicolo.getDecAaTipoFascicolos() == null) {
            tipoFascicolo.setDecAaTipoFascicolos(new ArrayList<DecAaTipoFascicolo>());
        }

        logger.info("Salvo il periodo di validit\u00E0 del tipo fascicolo");
        aaTipoFascicolo.setAaIniTipoFascicolo(aaTipoFascicoloRowBean.getAaIniTipoFascicolo());
        aaTipoFascicolo.setAaFinTipoFascicolo(aaTipoFascicoloRowBean.getAaFinTipoFascicolo());
        aaTipoFascicolo.setFlUpdFmtNumero("1");
        aaTipoFascicolo.setNiCharPadParteClassif(aaTipoFascicoloRowBean.getNiCharPadParteClassif());
        // aaTipoFascicolo.setFlAbilitaContrClassif(aaTipoFascicoloRowBean.getFlAbilitaContrClassif());
        // aaTipoFascicolo.setFlAbilitaContrColleg(aaTipoFascicoloRowBean.getFlAbilitaContrColleg());
        // aaTipoFascicolo.setFlAbilitaContrNumero(aaTipoFascicoloRowBean.getFlAbilitaContrNumero());
        // aaTipoFascicolo.setFlAccettaContrClassifNeg(aaTipoFascicoloRowBean.getFlAccettaContrClassifNeg());
        // aaTipoFascicolo.setFlAccettaContrCollegNeg(aaTipoFascicoloRowBean.getFlAccettaContrCollegNeg());
        // aaTipoFascicolo.setFlAccettaContrNumeroNeg(aaTipoFascicoloRowBean.getFlAccettaContrNumeroNeg());
        // aaTipoFascicolo.setFlForzaContrClassif(aaTipoFascicoloRowBean.getFlForzaContrClassif());
        // aaTipoFascicolo.setFlForzaContrColleg(aaTipoFascicoloRowBean.getFlForzaContrColleg());
        // aaTipoFascicolo.setFlForzaContrNumero(aaTipoFascicoloRowBean.getFlForzaContrNumero());

        aaTipoFascicolo.setDecTipoFascicolo(tipoFascicolo);
        tipoFascicolo.getDecAaTipoFascicolos().add(aaTipoFascicolo);

        logger.info("Inserisco i metadati di tipo profilo fascicolo e archivistico del periodo considerato");
        if (aaTipoFascicolo.getDecUsoModelloXsdFascs() == null) {
            aaTipoFascicolo.setDecUsoModelloXsdFascs(new ArrayList<DecUsoModelloXsdFasc>());
        }

        logger.info("Inserisco le parti");
        if (aaTipoFascicolo.getDecParteNumeroFascicolos() == null) {
            aaTipoFascicolo.setDecParteNumeroFascicolos(new ArrayList<DecParteNumeroFascicolo>());
        }

        for (DecParteNumeroFascicoloRowBean row : parti) {
            DecParteNumeroFascicolo parte = new DecParteNumeroFascicolo();
            updateParteNumeroFascicolo(parte, row);

            aaTipoFascicolo.addDecParteNumeroFascicolo(parte);
        }
        helper.insertEntity(aaTipoFascicolo, true);

        for (DecUsoModelloXsdFascRowBean row : metadatiProfiloFascicolo) {
            inserisciMetadatiProfilo(row, aaTipoFascicolo);
        }

        for (DecUsoModelloXsdFascRowBean row : metadatiProfiloArchivistico) {
            inserisciMetadatiProfilo(row, aaTipoFascicolo);
        }

        // // Gestione parametri
        // for (AplParamApplicRowBean paramApplicRowBean : parametriAmministrazioneAaTipoFasc) {
        // if (paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_amm") != null &&
        // !paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_amm").equals("")) {
        // amministrazioneEjb.insertAplValoreParamApplic(null, null, null, aaTipoFascicolo,
        // paramApplicRowBean.getIdParamApplic(), "PERIODO_TIPO_FASC",
        // paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_amm"));
        // }
        // }
        // for (AplParamApplicRowBean paramApplicRowBean : parametriConservazioneAaTipoFasc) {
        // if (paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_cons") != null &&
        // !paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_cons").equals("")) {
        // amministrazioneEjb.insertAplValoreParamApplic(null, null, null, aaTipoFascicolo,
        // paramApplicRowBean.getIdParamApplic(), "PERIODO_TIPO_FASC",
        // paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_cons"));
        // }
        // }
        // for (AplParamApplicRowBean paramApplicRowBean : parametriGestioneAaTipoFasc) {
        // if (paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_gest") != null &&
        // !paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_gest").equals("")) {
        // amministrazioneEjb.insertAplValoreParamApplic(null, null, null, aaTipoFascicolo,
        // paramApplicRowBean.getIdParamApplic(), "PERIODO_TIPO_FASC",
        // paramApplicRowBean.getString("ds_valore_param_applic_aa_tipo_fascicolo_gest"));
        // }
        // }
        // Logging
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO, idTipoFascicolo,
                param.getNomePagina());
        return new BigDecimal(aaTipoFascicolo.getIdAaTipoFascicolo());
    }

    private void inserisciMetadatiProfilo(DecUsoModelloXsdFascRowBean row, DecAaTipoFascicolo aaTipoFascicolo)
            throws ParerUserError {
        DecUsoModelloXsdFasc meta = new DecUsoModelloXsdFasc();
        meta.setFlStandard(row.getFlStandard());
        meta.setDecModelloXsdFascicolo(helper.findById(DecModelloXsdFascicolo.class, row.getIdModelloXsdFascicolo()));
        meta.setDtIstituz(row.getDtIstituz());
        meta.setDtSoppres(row.getDtSoppres());

        aaTipoFascicolo.addDecUsoModelloXsdFasc(meta);

        saveXsdAttribList(meta, row);
    }

    private void cloneDecAaTipoFascicolo(DecTipoFascicolo tipoFascicolo, DecAaTipoFascicolo row,
            BigDecimal aaIniTipoFascicolo, BigDecimal aaFinTipoFascicolo) {
        DecAaTipoFascicolo newAaTipoFascicolo = new DecAaTipoFascicolo();
        newAaTipoFascicolo.setAaIniTipoFascicolo(aaIniTipoFascicolo);
        newAaTipoFascicolo.setAaFinTipoFascicolo(aaFinTipoFascicolo);
        newAaTipoFascicolo.setFlUpdFmtNumero(row.getFlUpdFmtNumero());
        newAaTipoFascicolo.setNiCharPadParteClassif(row.getNiCharPadParteClassif());
        // newAaTipoFascicolo.setFlAbilitaContrClassif(row.getFlAbilitaContrClassif());
        // newAaTipoFascicolo.setFlAbilitaContrColleg(row.getFlAbilitaContrColleg());
        // newAaTipoFascicolo.setFlAbilitaContrNumero(row.getFlAbilitaContrNumero());
        // newAaTipoFascicolo.setFlAccettaContrClassifNeg(row.getFlAccettaContrClassifNeg());
        // newAaTipoFascicolo.setFlAccettaContrCollegNeg(row.getFlAccettaContrCollegNeg());
        // newAaTipoFascicolo.setFlAccettaContrNumeroNeg(row.getFlAccettaContrNumeroNeg());
        // newAaTipoFascicolo.setFlForzaContrClassif(row.getFlForzaContrClassif());
        // newAaTipoFascicolo.setFlForzaContrColleg(row.getFlForzaContrColleg());
        // newAaTipoFascicolo.setFlForzaContrNumero(row.getFlForzaContrNumero());
        newAaTipoFascicolo.setDecTipoFascicolo(tipoFascicolo);
        tipoFascicolo.getDecAaTipoFascicolos().add(newAaTipoFascicolo);
        if (newAaTipoFascicolo.getDecParteNumeroFascicolos() == null) {
            newAaTipoFascicolo.setDecParteNumeroFascicolos(new ArrayList<DecParteNumeroFascicolo>());
        }

        for (DecParteNumeroFascicolo parte : row.getDecParteNumeroFascicolos()) {
            DecParteNumeroFascicolo clone = new DecParteNumeroFascicolo();
            clone.setNmParteNumero(parte.getNmParteNumero());
            clone.setDsParteNumero(parte.getDsParteNumero());
            clone.setNiParteNumero(parte.getNiParteNumero());
            clone.setTiCharParte(parte.getTiCharParte());
            clone.setNiMinCharParte(parte.getNiMinCharParte());
            clone.setNiMaxCharParte(parte.getNiMaxCharParte());
            clone.setTiCharSep(parte.getTiCharSep());
            clone.setDlValoriParte(parte.getDlValoriParte());
            clone.setTiParte(parte.getTiParte());
            clone.setTiPadParte(parte.getTiPadParte());
            newAaTipoFascicolo.addDecParteNumeroFascicolo(clone);
        }
    }

    private void updateParteNumeroFascicolo(DecParteNumeroFascicolo parte, DecParteNumeroFascicoloRowBean row) {
        parte.setNmParteNumero(row.getNmParteNumero());
        parte.setDsParteNumero(row.getDsParteNumero());
        parte.setNiParteNumero(row.getNiParteNumero());
        parte.setTiCharParte(row.getTiCharParte());
        parte.setNiMinCharParte(row.getNiMinCharParte());
        parte.setNiMaxCharParte(row.getNiMaxCharParte());
        parte.setDlValoriParte(row.getDlValoriParte());
        parte.setTiParte(row.getTiParte());
        parte.setTiPadParte(row.getTiPadParte());

        String separatore = row.getTiCharSep();
        if (separatore != null && separatore.equals("SPAZIO")) {
            separatore = " ";
        }
        parte.setTiCharSep(separatore);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void checkFascicoliNelPeriodoValidita(BigDecimal idAaTipoFascicolo) {
        logger.info("Verifico che nel periodo modificato non esistano fascicoli, nel qual caso sblocco il periodo");
        DecAaTipoFascicolo aaTipoFascicolo = helper.findById(DecAaTipoFascicolo.class, idAaTipoFascicolo);
        DecTipoFascicolo fascicolo = aaTipoFascicolo.getDecTipoFascicolo();
        if (!fascicoliHelper.existFascicoliVersatiPerTipoFascicolo(BigDecimal.valueOf(fascicolo.getIdTipoFascicolo()),
                aaTipoFascicolo.getAaIniTipoFascicolo(), aaTipoFascicolo.getAaFinTipoFascicolo())) {
            logger.info("Nessun fascicolo rilevato, sblocco il periodo");
            aaTipoFascicolo.setFlUpdFmtNumero("0");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean checkModelliNelPeriodoValidita(BigDecimal idUsoModelloXsdFasc) {
        // controllo se nella tabella FAS_XML_FASCICOLO esiste almeno un record con id_modello_xsd_fascicolo = modello
        // da eliminare per la struttura corrente
        // e il tipo fascicolo
        logger.info("Verifico se tale modello è stato utilizzato per eseguire versamenti di fascicoli");
        DecUsoModelloXsdFasc usoModello = helper.findById(DecUsoModelloXsdFasc.class, idUsoModelloXsdFasc);
        return fascicoliHelper.existFascicoliVersatiPerModelloFascicolo(
                BigDecimal.valueOf(usoModello.getDecModelloXsdFascicolo().getIdModelloXsdFascicolo()),
                BigDecimal.valueOf(usoModello.getDecAaTipoFascicolo().getDecTipoFascicolo().getIdTipoFascicolo()));
    }

    public void insertValoriParametriPeriodoFascicolo(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo) {
        DecAaTipoFascicolo periodo = struttureHelper.findById(DecAaTipoFascicolo.class, idAaTipoFascicolo);

        AplParamApplic flAbilitaContrClassif = configurationHelper.getParamApplic("FL_ABILITA_CONTR_CLASSIF");
        // String valoreFlAbilitaContrClassif = configurationHelper.getValoreParamApplic("FL_ABILITA_CONTR_CLASSIF",
        // idAmbiente, idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlAbilitaContrClassif = configurationHelper.getAplValoreParamApplic("FL_ABILITA_CONTR_CLASSIF",
                "STRUT", null, idStrut, null, null);

        AplParamApplic flAbilitaContrColleg = configurationHelper.getParamApplic("FL_ABILITA_CONTR_COLLEG");
        // String valoreFlAbilitaContrColleg = configurationHelper.getValoreParamApplic("FL_ABILITA_CONTR_COLLEG",
        // idAmbiente, idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlAbilitaContrColleg = configurationHelper.getAplValoreParamApplic("FL_ABILITA_CONTR_COLLEG",
                "STRUT", null, idStrut, null, null);

        AplParamApplic flAbilitaContrNumero = configurationHelper.getParamApplic("FL_ABILITA_CONTR_NUMERO");
        // String valoreFlAbilitaContrNumero = configurationHelper.getValoreParamApplic("FL_ABILITA_CONTR_NUMERO",
        // idAmbiente, idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlAbilitaContrNumero = configurationHelper.getAplValoreParamApplic("FL_ABILITA_CONTR_NUMERO",
                "STRUT", null, idStrut, null, null);

        AplParamApplic flAccettaContrClassifNeg = configurationHelper.getParamApplic("FL_ACCETTA_CONTR_CLASSIF_NEG");
        // String valoreFlAccettaContrClassifNeg =
        // configurationHelper.getValoreParamApplic("FL_ACCETTA_CONTR_CLASSIF_NEG", idAmbiente, idStrut, null, null,
        // CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlAccettaContrClassifNeg = configurationHelper
                .getAplValoreParamApplic("FL_ACCETTA_CONTR_CLASSIF_NEG", "STRUT", null, idStrut, null, null);

        AplParamApplic flAccettaContrCollegfNeg = configurationHelper.getParamApplic("FL_ACCETTA_CONTR_COLLEG_NEG_FAS");
        // String valorelAccettaContrCollegfNeg =
        // configurationHelper.getValoreParamApplic("FL_ACCETTA_CONTR_COLLEG_NEG", idAmbiente, idStrut, null, null,
        // CostantiDB.TipoAplVGetValAppart.STRUT);
        String valorelAccettaContrCollegfNeg = configurationHelper
                .getAplValoreParamApplic("FL_ACCETTA_CONTR_COLLEG_NEG_FAS", "STRUT", null, idStrut, null, null);

        AplParamApplic flAccettaContrNumerofNeg = configurationHelper.getParamApplic("FL_ACCETTA_CONTR_NUMERO_NEG");
        // String valorelAccettaContrNumerofNeg =
        // configurationHelper.getValoreParamApplic("FL_ACCETTA_CONTR_NUMERO_NEG", idAmbiente, idStrut, null, null,
        // CostantiDB.TipoAplVGetValAppart.STRUT);
        String valorelAccettaContrNumerofNeg = configurationHelper
                .getAplValoreParamApplic("FL_ACCETTA_CONTR_NUMERO_NEG", "STRUT", null, idStrut, null, null);

        AplParamApplic flForzaContrClassif = configurationHelper.getParamApplic("FL_FORZA_CONTR_CLASSIF");
        // String valoreFlForzaContrClassif = configurationHelper.getValoreParamApplic("FL_FORZA_CONTR_CLASSIF",
        // idAmbiente, idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlForzaContrClassif = configurationHelper.getAplValoreParamApplic("FL_FORZA_CONTR_CLASSIF",
                "STRUT", null, idStrut, null, null);

        AplParamApplic flForzaContrColleg = configurationHelper.getParamApplic("FL_FORZA_CONTR_COLLEG");
        // String valoreFlForzaContrColleg = configurationHelper.getValoreParamApplic("FL_FORZA_CONTR_COLLEG",
        // idAmbiente, idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlForzaContrColleg = configurationHelper.getAplValoreParamApplic("FL_FORZA_CONTR_COLLEG", "STRUT",
                null, idStrut, null, null);

        AplParamApplic flForzaContrNumero = configurationHelper.getParamApplic("FL_FORZA_CONTR_NUMERO");
        // String valoreFlForzaContrNumero = configurationHelper.getValoreParamApplic("FL_FORZA_CONTR_NUMERO",
        // idAmbiente, idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
        String valoreFlForzaContrNumero = configurationHelper.getAplValoreParamApplic("FL_FORZA_CONTR_NUMERO", "STRUT",
                null, idStrut, null, null);

        if (valoreFlAbilitaContrClassif != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flAbilitaContrClassif.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlAbilitaContrClassif);
        }
        if (valoreFlAbilitaContrColleg != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flAbilitaContrColleg.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlAbilitaContrColleg);
        }
        if (valoreFlAbilitaContrNumero != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flAbilitaContrNumero.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlAbilitaContrNumero);
        }
        if (valoreFlAccettaContrClassifNeg != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flAccettaContrClassifNeg.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlAccettaContrClassifNeg);
        }
        if (valorelAccettaContrCollegfNeg != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flAccettaContrCollegfNeg.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valorelAccettaContrCollegfNeg);
        }
        if (valorelAccettaContrNumerofNeg != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flAccettaContrNumerofNeg.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valorelAccettaContrNumerofNeg);
        }
        if (valoreFlForzaContrClassif != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flForzaContrClassif.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlForzaContrClassif);
        }
        if (valoreFlForzaContrColleg != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flForzaContrColleg.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlForzaContrColleg);
        }
        if (valoreFlForzaContrNumero != null) {
            amministrazioneEjb.insertAplValoreParamApplic(null, null, null, periodo,
                    BigDecimal.valueOf(flForzaContrNumero.getIdParamApplic()), "PERIODO_TIPO_FASC",
                    valoreFlForzaContrNumero);
        }
    }

    public void saveParametriAaTipoFascicolo(LogParam param, AplParamApplicTableBean parametriAmministrazioneAaTipoFasc,
            AplParamApplicTableBean parametriConservazioneAaTipoFasc,
            AplParamApplicTableBean parametriGestioneAaTipoFasc, BigDecimal idAaTipoFascicolo) {
        DecAaTipoFascicolo aaTipoFascicolo = struttureHelper.findById(DecAaTipoFascicolo.class, idAaTipoFascicolo);
        gestioneParametriAaTipoFascicolo(parametriAmministrazioneAaTipoFasc, parametriConservazioneAaTipoFasc,
                parametriGestioneAaTipoFasc, aaTipoFascicolo);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO,
                new BigDecimal(aaTipoFascicolo.getDecTipoFascicolo().getIdTipoFascicolo()), param.getNomePagina());
    }

    private void gestioneParametriAaTipoFascicolo(AplParamApplicTableBean parametriAmministrazioneAaTipoFasc,
            AplParamApplicTableBean parametriConservazioneAaTipoFasc,
            AplParamApplicTableBean parametriGestioneAaTipoFasc, DecAaTipoFascicolo aaTipoFascicolo) {
        // Gestione parametri amministrazione
        manageParametriPerAaTipoFascicolo(parametriAmministrazioneAaTipoFasc,
                "ds_valore_param_applic_aa_tipo_fascicolo_amm", aaTipoFascicolo);
        // Gestione parametri conservazione
        manageParametriPerAaTipoFascicolo(parametriConservazioneAaTipoFasc,
                "ds_valore_param_applic_aa_tipo_fascicolo_cons", aaTipoFascicolo);
        // Gestione parametri gestione
        manageParametriPerAaTipoFascicolo(parametriGestioneAaTipoFasc, "ds_valore_param_applic_aa_tipo_fascicolo_gest",
                aaTipoFascicolo);
    }

    private void saveXsdAttribList(DecUsoModelloXsdFasc meta, DecUsoModelloXsdFascRowBean xsdFascRowBean)
            throws ParerUserError {

        List<String> attributes = parseStringaXsd(meta.getDecModelloXsdFascicolo().getTiUsoModelloXsd(),
                meta.getDecModelloXsdFascicolo().getBlXsd());
        if (attributes.isEmpty()) {
            throw new ParerUserError("File Xsd non contenente attributi.</br>");
        }

        int order = 1;
        List<String> dbAttributes = helper.getNmAttribFascList(xsdFascRowBean.getIdAaTipoFascicolo(), null);
        /*
         * Salvo la lista degli attributi uno a uno Controllo che l'attributo non ci sia gi\u00E0, se c'\u00E8 non
         * importa inserirlo, devo solo inserire il nuovo riferimento in DecXsdAttribFascicolo
         */
        List<String> controlList = new ArrayList<String>();

        for (String attr : attributes) {
            BigDecimal idAttribFascicolo = null;
            if (!dbAttributes.contains(attr)) {
                idAttribFascicolo = salvaAttribFascicolo(attr, meta);
            } else {
                dbAttributes.remove(attr);
            }

            // inserisco nella lista dei riferimenti
            if (idAttribFascicolo != null) {
                insertDecModelloXsdAttribFasc(meta, idAttribFascicolo, order);
            }
            controlList.add(attr);
            order = order + 5;
        }
    }

    private BigDecimal salvaAttribFascicolo(String attribute, DecUsoModelloXsdFasc meta) {

        BigDecimal idAttribFascicolo;

        idAttribFascicolo = insertDecAttribFascicolo(attribute, meta);

        return idAttribFascicolo;
    }

    public BigDecimal insertDecAttribFascicolo(String attribute, DecUsoModelloXsdFasc meta) {

        DecAttribFascicolo attribFascicolo = new DecAttribFascicolo();
        BigDecimal idAttribFascicolo = null;

        attribFascicolo.setDecAaTipoFascicolo(meta.getDecAaTipoFascicolo());
        attribFascicolo.setDecTipoFascicolo(meta.getDecAaTipoFascicolo().getDecTipoFascicolo());
        attribFascicolo.setTiUsoAttrib(meta.getDecModelloXsdFascicolo().getTiUsoModelloXsd());
        attribFascicolo.setNmAttribFascicolo(attribute);
        attribFascicolo.setDsAttribFascicolo(attribute);

        helper.insertEntity(attribFascicolo, true);
        idAttribFascicolo = new BigDecimal(attribFascicolo.getIdAttribFascicolo());

        return idAttribFascicolo;

    }

    private void insertDecModelloXsdAttribFasc(DecUsoModelloXsdFasc meta, BigDecimal idAttribFascicolo, int order) {

        DecModelloXsdAttribFascicolo xsdAttribFascicolo = new DecModelloXsdAttribFascicolo();

        DecAttribFascicolo attribFascicolo = helper.findById(DecAttribFascicolo.class, idAttribFascicolo);

        xsdAttribFascicolo.setDecModelloXsdFascicolo(meta.getDecModelloXsdFascicolo());
        xsdAttribFascicolo.setDecAttribFascicolo(attribFascicolo);
        xsdAttribFascicolo.setNiOrdAttrib(new BigDecimal(order));

        helper.insertEntity(xsdAttribFascicolo, true);
    }

    /**
     *
     * @param tiUsoModelloXsd
     * @param stringaFile
     *
     * @return List xsd attributes
     *
     * @throws ParerUserError
     */
    private List<String> parseStringaXsd(String tiUsoModelloXsd, String stringaFile) throws ParerUserError {

        List<String> attributes = new ArrayList<String>();

        ByteArrayInputStream bais = null;

        if (!stringaFile.isEmpty()) {
            bais = new ByteArrayInputStream(stringaFile.getBytes());

        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db;
            db = dbf.newDocumentBuilder();

            Document doc;

            doc = db.parse(bais);
            String tagName = (CostantiDB.TiModelloXsd.AIP_UNISYNCRO.name().equals(tiUsoModelloXsd)) ? "schema"
                    : "element";
            NodeList nl = doc.getElementsByTagNameNS(XMLConstants.W3C_XML_SCHEMA_NS_URI, tagName);

            for (int it = 0; it < nl.getLength(); it++) {
                Node n = nl.item(it);

                NamedNodeMap map = n.getAttributes();
                if (!CostantiDB.TiModelloXsd.AIP_UNISYNCRO.name().equals(tiUsoModelloXsd)) {
                    if (!(map.getNamedItem("name").getNodeValue().equals("DatiSpecifici"))
                            && !(map.getNamedItem("name").getNodeValue().equals("Fascicolo"))
                            && !(map.getNamedItem("name").getNodeValue().equals("MetadatiIntegratiSelfDescription"))
                            && (map.getNamedItem("name").getNodeValue().equals("ProfiloGeneraleFascicolo") || map
                                    .getNamedItem("name").getNodeValue().equals("ProfiloArchivisticoFascicolo"))) {
                        NodeList nodecl = ((Element) n).getElementsByTagName("xs:complexType");
                        if (nodecl != null) {
                            Node nodec = nodecl.item(0);
                            NodeList nc = nodec.getChildNodes();
                            Element cE = (Element) nc;
                            NodeList eL = cE.getElementsByTagName("xs:element");
                            for (int c = 0; c < eL.getLength(); c++) {
                                Node child = eL.item(c);

                                NamedNodeMap mapchild = child.getAttributes();
                                String tmpAttrname = mapchild.getNamedItem("name").getNodeValue();
                                if (tmpAttrname.trim().equals(tmpAttrname)) {
                                    attributes.add(tmpAttrname);
                                } else {
                                    throw new ParerUserError("Operazione non effettuata: il tag [" + tmpAttrname
                                            + "] del documento XSD che si sta cercando di caricare "
                                            + "non può iniziare o terminare con caratteri di spaziatura</br>");
                                }
                            }
                        }
                    }
                } else {
                    if (!(map.getNamedItem("targetNamespace").getNodeValue()
                            .equals("http://www.uni.com/U3011/sincro/"))) {
                        String tmpAttrname = map.getNamedItem("targetNamespace").getNodeValue();
                        if (tmpAttrname.trim().equals(tmpAttrname)) {
                            attributes.add(tmpAttrname);
                        } else {
                            throw new ParerUserError("Operazione non effettuata: il tag [" + tmpAttrname
                                    + "] del documento XSD che si sta cercando di caricare "
                                    + "non può iniziare o terminare con caratteri di spaziatura</br>");
                        }
                    }
                }
            }
        } catch (SAXException e) {
            throw new ParerUserError("Operazione non effettuata: file non ben formato " + e.toString() + "</br>");
        } catch (IOException e) {
            throw new ParerUserError("Errore IO - Operazione non effettuata: " + e.toString() + "</br>");
        } catch (ParserConfigurationException e) {
            throw new ParerUserError(
                    "Errore ParserConfiguration - Operazione non effettuata: " + e.toString() + "</br>");
        }

        return attributes;
    }

    /**
     *
     * @param idXsdFascicolo
     *            id Xsd fascicolo
     * @param idAaTipoFascicolo
     *            id Aa tipo fascicolo
     *
     * @return DecAttribFascicoloTableBean
     */
    public DecAttribFascicoloTableBean getDecAttribFascicoloTableBeanFromXsd(BigDecimal idXsdFascicolo,
            BigDecimal idAaTipoFascicolo) {
        DecAttribFascicoloTableBean attribFascicoloTableBean = new DecAttribFascicoloTableBean();

        List<DecAttribFascicolo> list = helper.retrieveDecAttribFascicoloList(idXsdFascicolo, idAaTipoFascicolo);
        try {
            if (!list.isEmpty()) {
                for (DecAttribFascicolo attrib : list) {
                    DecAttribFascicoloRowBean attribRow = (DecAttribFascicoloRowBean) Transform.entity2RowBean(attrib);

                    attribFascicoloTableBean.add(attribRow);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return attribFascicoloTableBean;
    }

    /**
     *
     * @param row
     *            DecAttribFascicolo rowbean
     * @param xsdFascicoloRowBean
     *            DecUsoModelloXsdFasc rowbean
     *
     * @return DecModelloXsdAttribFascicolo
     */
    public DecModelloXsdAttribFascicolo getDecModelloXsdAttribFascicolo(DecAttribFascicoloRowBean row,
            DecUsoModelloXsdFascRowBean xsdFascicoloRowBean) {

        DecModelloXsdAttribFascicolo attrib = helper.getDecXsdAttribFascicoloByAttrib(row.getIdAttribFascicolo(),
                xsdFascicoloRowBean.getIdModelloXsdFascicolo());

        return attrib;
    }

    /**
     *
     * @param idAttribFascicolo
     *            id Attributo fascicolo
     *
     * @return row bean DecAttribFascicolo
     */
    public DecAttribFascicoloRowBean getDecAttribFascRowBean(BigDecimal idAttribFascicolo) {
        DecAttribFascicolo attrib = helper.findById(DecAttribFascicolo.class, idAttribFascicolo);
        DecAttribFascicoloRowBean row = new DecAttribFascicoloRowBean();
        if (attrib != null) {
            try {
                row = (DecAttribFascicoloRowBean) Transform.entity2RowBean(attrib);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dell'attributo dati specifici "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dell'attributo dati specifici");
            }
        }
        return row;
    }

    /**
     *
     * @param idAttribFascicolo
     *            id Attributo fascicolo
     *
     * @return DecModelloXsdAttribFascicoloRowBean
     */
    public DecModelloXsdAttribFascicoloRowBean getDecModelloXsdAttribFascicoloRowBeanByAttrib(
            BigDecimal idAttribFascicolo) {
        DecModelloXsdAttribFascicoloRowBean modelloXsdAttribFascicoloRowBean = new DecModelloXsdAttribFascicoloRowBean();
        DecModelloXsdAttribFascicolo xsdAttribFascicolo = helper
                .getDecModelloXsdAttribFascicoloByAttrib(idAttribFascicolo);

        try {
            modelloXsdAttribFascicoloRowBean = (DecModelloXsdAttribFascicoloRowBean) Transform
                    .entity2RowBean(xsdAttribFascicolo);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return modelloXsdAttribFascicoloRowBean;
    }

    /**
     *
     * @param idAaTipoFascicolo
     *            id periodo validita fascicolo
     * @param idModelloXsdFascicolo
     *            id modello xsd fascicolo
     *
     * @return DecUsoModelloXsdFascRowBean
     */
    public DecUsoModelloXsdFascRowBean getDecUsoModelloXsdFascRowBeanByAttrib(BigDecimal idAaTipoFascicolo,
            BigDecimal idModelloXsdFascicolo) {
        DecUsoModelloXsdFascRowBean usoModelloXsdFascRowBean = new DecUsoModelloXsdFascRowBean();
        DecUsoModelloXsdFasc usoModelloXsdFasc = helper.getDecUsoModelloXsdFascicoloByAttrib(idAaTipoFascicolo,
                idModelloXsdFascicolo);

        try {
            usoModelloXsdFascRowBean = (DecUsoModelloXsdFascRowBean) Transform.entity2RowBean(usoModelloXsdFasc);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return usoModelloXsdFascRowBean;
    }
}
