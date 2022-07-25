package it.eng.parer.web.ejb;

import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.DecCriterioFiltroMultiplo;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio;
import it.eng.parer.entity.constraint.DecCriterioRaggr.TiModValidElencoCriterio;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrRowBean;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrTableBean;
import it.eng.parer.slite.gen.viewbean.DecVRicCriterioRaggrTableBean;
import it.eng.parer.viewEntity.DecVCreaCritRaggrRegistro;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoDoc;
import it.eng.parer.viewEntity.DecVCreaCritRaggrTipoUd;
import it.eng.parer.web.dto.CriterioRaggrStandardBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.CriteriRaggrHelper;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;
import java.lang.reflect.InvocationTargetException;
import it.eng.parer.web.util.Constants.TipoDato;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
 *
 * @author gilioli_p
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class CriteriRaggruppamentoEjb {

    @Resource
    private SessionContext context;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private CriteriRaggrHelper crHelper;

    private static final Logger logger = LoggerFactory.getLogger(CriteriRaggruppamentoEjb.class);

    public boolean isCriterioRaggrStandard(CriterioRaggrStandardBean bean) throws EMFError {
        int numeroCondizioniSoddisfatte = 0;
        /*
         * ATTENZIONE: IL NUMERO DI CONDIZIONI NECESSARIE DIPENDE DA QUANTE CONDIZIONI DEVONO ESSERE SODDISFATTE
         * ALL'INTERNO DEL METODO
         */
        int numeroCondizioniNecessarie = 6;

        // 1) Controllo che sia presente un solo filtro tra registri, tipiUd e tipiDoc
        if ((bean.getReg().size() == 1 && bean.getTipiUd().isEmpty() && bean.getTipiDoc().isEmpty())
                || (bean.getReg().isEmpty() && bean.getTipiUd().size() == 1 && bean.getTipiDoc().isEmpty())
                || (bean.getReg().isEmpty() && bean.getTipiUd().isEmpty() && bean.getTipiDoc().size() == 1)) {
            numeroCondizioniSoddisfatte++;

            // Finchè sono qui controllo la sesta condizione:
            // 6) Controllo che il nome criterio sia pari a: nome tipo ud (o registro o tipo doc a seconda del caso)
            String nomeNecessario = (bean.getReg().size() == 1 ? bean.getReg().toArray(new String[1])[0]
                    : bean.getTipiUd().size() == 1 ? bean.getTipiUd().toArray(new String[1])[0]
                            : bean.getTipiDoc().size() == 1 ? bean.getTipiDoc().toArray(new String[1])[0] : "");
            if (bean.getNmCriterioRaggr().equals(nomeNecessario)) {
                numeroCondizioniSoddisfatte++;
            }
        }

        // 2) Controllo che non sia presente il range di anni o il singolo anno
        if (bean.getAaKeyUnitaDoc() == null && bean.getAaKeyUnitaDocDa() == null && bean.getAaKeyUnitaDocA() == null) {
            numeroCondizioniSoddisfatte++;
        }

        // // 3) Controllo che se il flag automatico è true, l'anno sia impostato con l'anno corrente - Se è false,
        // l'anno deve essere diverso dall'anno corrente
        // if (bean.getAaKeyUnitaDoc() != null) {
        // if ((bean.getFlCriterioRaggrAutom().equals("1")
        // && bean.getAaKeyUnitaDoc().compareTo(new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR))) ==
        // 0)
        // || (bean.getFlCriterioRaggrAutom().equals("0")
        // && bean.getAaKeyUnitaDoc().compareTo(new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR))) !=
        // 0)) {
        // numeroCondizioniSoddisfatte++;
        // }
        // }
        // 4) Controlla che la scadenza chiusura sia impostata a 30 GIORNI
        if (bean.getNiTempoScadChius() != null && bean.getNiTempoScadChius().compareTo(new BigDecimal("30")) == 0
                && bean.getTiTempoScadChius() != null && bean.getTiTempoScadChius().equals("GIORNI")) {
            numeroCondizioniSoddisfatte++;
        }

        String numMaxCompCriterioRaggrWarn = configurationHelper.getValoreParamApplic(
                "NUM_MAX_COMP_CRITERIO_RAGGR_WARN", null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        if (bean.getNiMaxComp() != null
                && bean.getNiMaxComp().compareTo(new BigDecimal(numMaxCompCriterioRaggrWarn)) == 0) {
            numeroCondizioniSoddisfatte++;
        }
        /*
         * 6) Controllo che tutti i campi da oggetto unità documentaria in giù nella form siano blank
         * DL_OGGETTO_UNITA_DOC, DT_REG_UNITA_DOC_DA, DT_REG_UNITA_DOC_A, DL_DOC, DS_AUTORE_DOC,
         * DT_CREAZIONE_UNITA_DOC_DA, ORE_DT_CREAZIONE_UNITA_DOC_DA, MINUTI_DT_CREAZIONE_UNITA_DOC_DA,
         * DT_CREAZIONE_UNITA_DOC_A, ORE_DT_CREAZIONE_UNITA_DOC_A, MINUTI_DT_CREAZIONE_UNITA_DOC_A, TI_CONSERVAZIONE,
         * FL_UNITA_DOC_FIRMATO, FL_FORZA_ACCETTAZIONE, FL_FORZA_CONSERVAZIONE, TI_ESITO_VERIF_FIRME,
         * TI_STATO_CONSERVAZIONE, NM_SISTEMA_MIGRAZ
         */
        boolean campoCompilato = false;
        if (StringUtils.isNotBlank(bean.getDlOggettoUnitaDoc())) {
            campoCompilato = true;
        } else if (bean.getDtRegUnitaDocDa() != null) {
            campoCompilato = true;
        } else if (bean.getDtRegUnitaDocA() != null) {
            campoCompilato = true;
        } else if (StringUtils.isNotBlank(bean.getDlDoc())) {
            campoCompilato = true;
        } else if (StringUtils.isNotBlank(bean.getDsAutoreDoc())) {
            campoCompilato = true;
        } else if (bean.getDtCreazioneDa() != null) {
            campoCompilato = true;
        } else if (bean.getDtCreazioneA() != null) {
            campoCompilato = true;
        } else if (StringUtils.isNotBlank(bean.getTiConservazione())) {
            campoCompilato = true;
        } else if (StringUtils.isNotBlank(bean.getFlUnitaDocFirmato())) {
            campoCompilato = true;
        } else if (StringUtils.isNotBlank(bean.getFlForzaAccettazione())) {
            campoCompilato = true;
        } else if (StringUtils.isNotBlank(bean.getFlForzaConservazione())) {
            campoCompilato = true;
        } else if (bean.getTiEsitoVerifFirme() != null && !bean.getTiEsitoVerifFirme().isEmpty()) {
            campoCompilato = true;
        } else if (bean.getNmSistemaMigraz() != null && !bean.getNmSistemaMigraz().isEmpty()) {
            campoCompilato = true;
        }

        if (!campoCompilato) {
            numeroCondizioniSoddisfatte++;
        }

        return numeroCondizioniSoddisfatte == numeroCondizioniNecessarie;
    }

    public long getNumMaxCompDaStruttura(BigDecimal idStrut) {
        OrgStrut strut = crHelper.findById(OrgStrut.class, idStrut);
        BigDecimal idAmbiente = BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente());
        return Integer.parseInt(configurationHelper.getValoreParamApplic("NUM_MAX_COMP_CRITERIO_RAGGR", idAmbiente,
                idStrut, null, null, CostantiDB.TipoAplVGetValAppart.STRUT));
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrByIdStrut(BigDecimal idStrut, boolean isFilterValid) {
        DecVRicCriterioRaggrTableBean tableBean = new DecVRicCriterioRaggrTableBean();
        try {
            tableBean = crHelper.getCriteriRaggrFromStruttura(idStrut, isFilterValid);
        } catch (Exception ex) {
            logger.error("Errore durante il recupero dei criteri di raggruppamento "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrDaRegistro(BigDecimal idStrut, BigDecimal idRegistroUnitaDoc) {
        DecVRicCriterioRaggrTableBean tableBean = new DecVRicCriterioRaggrTableBean();
        try {
            tableBean = crHelper.getCriteriRaggrFromRegistroNoRange(idStrut, idRegistroUnitaDoc);
        } catch (Exception ex) {
            logger.error("Errore durante il recupero dei criteri di raggruppamento del registro avente id "
                    + idRegistroUnitaDoc + " - " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrDaTipoUnitaDoc(BigDecimal idStrut, BigDecimal idTipoUnitaDoc) {
        DecVRicCriterioRaggrTableBean tableBean = new DecVRicCriterioRaggrTableBean();
        try {
            tableBean = crHelper.getCriteriRaggrFromTipoUnitaDoc(idStrut, idTipoUnitaDoc);
        } catch (Exception ex) {
            logger.error(
                    "Errore durante il recupero dei criteri di raggruppamento del tipo unita' documentaria avente id "
                            + idTipoUnitaDoc + " - " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
        }
        return tableBean;
    }

    public DecVRicCriterioRaggrTableBean getCriteriRaggrDaTipoDoc(BigDecimal idStrut, BigDecimal idTipoDoc) {
        DecVRicCriterioRaggrTableBean tableBean = new DecVRicCriterioRaggrTableBean();
        try {
            tableBean = crHelper.getCriteriRaggrFromTipoDoc(idStrut, idTipoDoc);
        } catch (Exception ex) {
            logger.error("Errore durante il recupero dei criteri di raggruppamento del tipo documento avente id "
                    + idTipoDoc + " - " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return tableBean;
    }

    public String getCriterioStandardPerTipoDatoAnno(long idTipoDato, TipoDato tipoDato) {
        return crHelper.getCriterioStandardPerTipoDatoAnno(idTipoDato, tipoDato);
    }

    public boolean existsCriterioStandardPerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, "1", null);
    }

    public boolean existsCriterioNonStandardPerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, "0", null);
    }

    public boolean existsCriterioPerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, null, null);
    }

    public boolean existsCriterioStandardNonFiscalePerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, "1", "0");
    }

    public boolean existsCriterioStandardFiscalePerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, "1", "1");
    }

    public boolean existsCriterioNonFiscalePerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, null, "0");
    }

    public boolean existsCriterioFiscalePerTipoDato(long idTipoDato, TipoDato tipoDato) {
        return crHelper.existsCriterioPerTipoDato(idTipoDato, tipoDato, null, "1");
    }

    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // public Map<String, List<String>> creaCriteriRaggruppamentoDaPeriodoDiValidita(LogParam param, BigDecimal idStrut,
    // BigDecimal idRegistroUnitaDoc, int aaMinRegistroUnitaDoc, int aaMaxRegistroUnitaDoc) throws ParerUserError {
    // Map<String, List<String>> risultato = new HashMap();
    // List<String> anniCreati = new ArrayList();
    // List<String> anniNonCreati = new ArrayList();
    // int annoCorrente = Calendar.getInstance().get(Calendar.YEAR);
    // for (int i = aaMinRegistroUnitaDoc; i <= aaMaxRegistroUnitaDoc; i++) {
    // // Se non sono presenti criteri di raggruppamento standard per quel registro per l'anno i
    // String exists = getCriterioStandardPerTipoDatoAnno(idRegistroUnitaDoc.longValue(), "REGISTRO_UD", i);
    // if (exists.equals("0") || exists.equals("2")) {
    // DecVCreaCritRaggrRegistro creaCritRegistro =
    // crHelper.getDecVCreaCritRaggrRegistro(idRegistroUnitaDoc.longValue());
    // String nmCriterioMod = creaCritRegistro.getNmCriterioRaggr().replace("<anno da definire>", "" + i);
    // if (crHelper.existNomeCriterio(nmCriterioMod, idStrut)) {
    // anniNonCreati.add(Integer.toString(i));
    // } else {
    // context.getBusinessObject(CriteriRaggruppamentoEjb.class).salvataggioAutomaticoCriterioRaggrRegistro(param,
    // creaCritRegistro, i, i == annoCorrente ? "1" : "0");
    // anniCreati.add(Integer.toString(i) + (i == annoCorrente ? "(AUTOMATICO)" : ""));
    // }
    // }
    // }
    // risultato.put("anniCreati", anniCreati);
    // risultato.put("anniNonCreati", anniNonCreati);
    // return risultato;
    // }
    private Date getNow() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    private Date get2444() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public DecCriterioRaggr salvataggioAutomaticoCriterioRaggrStdNoAutomRegistro(LogParam param,
            DecVCreaCritRaggrRegistro creaCritRaggrRegistro) {
        DecCriterioRaggr criterioDaSalvare = new DecCriterioRaggr();
        criterioDaSalvare.setNmCriterioRaggr(creaCritRaggrRegistro.getNmCriterioRaggr());
        criterioDaSalvare.setDsCriterioRaggr(creaCritRaggrRegistro.getDsCriterioRaggr());
        criterioDaSalvare.setDtIstituz(getNow());
        criterioDaSalvare.setDtSoppres(get2444());
        criterioDaSalvare.setFlFiltroRangeRegistroKey(creaCritRaggrRegistro.getFlFiltroRangeRegistroKey());
        criterioDaSalvare.setFlFiltroRegistroKey(creaCritRaggrRegistro.getFlFiltroRegistroKey());
        criterioDaSalvare.setFlFiltroSistemaMigraz(creaCritRaggrRegistro.getFlFiltroSistemaMigraz());
        criterioDaSalvare.setFlFiltroTiEsitoVerifFirme(creaCritRaggrRegistro.getFlFiltroTiEsitoVerifFirme());
        criterioDaSalvare.setFlFiltroTipoDoc(creaCritRaggrRegistro.getFlFiltroTipoDoc());
        criterioDaSalvare.setFlFiltroTipoUnitaDoc(creaCritRaggrRegistro.getFlFiltroTipoUnitaDoc());
        criterioDaSalvare.setDecCriterioFiltroMultiplos(new ArrayList<DecCriterioFiltroMultiplo>());
        OrgStrut orgStrut = crHelper.findById(OrgStrut.class, creaCritRaggrRegistro.getIdStrut().longValue());
        criterioDaSalvare.setOrgStrut(orgStrut);
        // Salvataggio del filtro multiplo relativo al registro
        DecRegistroUnitaDoc decRegistroUnitaDoc = crHelper.findById(DecRegistroUnitaDoc.class,
                creaCritRaggrRegistro.getIdRegistroUnitaDoc().longValue());
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(criterioDaSalvare);
        filtro.setDecRegistroRangeUnitaDoc(null);
        filtro.setDecRegistroUnitaDoc(decRegistroUnitaDoc);
        filtro.setDecTipoDoc(null);
        filtro.setDecTipoUnitaDoc(null);
        filtro.setTiEsitoVerifFirme(null);
        filtro.setTiFiltroMultiplo(ApplEnum.TipoFiltroMultiploCriteriRaggr.REGISTRO_UNI_DOC.name());
        criterioDaSalvare.getDecCriterioFiltroMultiplos().add(filtro);
        criterioDaSalvare.setNiMaxComp(creaCritRaggrRegistro.getNiMaxComp());
        criterioDaSalvare.setNiTempoScadChius(creaCritRaggrRegistro.getNiTempoScadChius());
        criterioDaSalvare.setTiTempoScadChius(creaCritRaggrRegistro.getTiTempoScadChius());
        criterioDaSalvare.setFlCriterioRaggrStandard("1");
        criterioDaSalvare.setFlCriterioRaggrFisc(creaCritRaggrRegistro.getFlCriterioRaggrFisc());
        criterioDaSalvare.setTiValidElenco(creaCritRaggrRegistro.getTiValidElenco() != null
                ? TiValidElencoCriterio.valueOf(creaCritRaggrRegistro.getTiValidElenco()) : null);
        criterioDaSalvare.setTiModValidElenco(creaCritRaggrRegistro.getTiModValidElenco() != null
                ? TiModValidElencoCriterio.valueOf(creaCritRaggrRegistro.getTiModValidElenco()) : null);
        crHelper.insertEntity(criterioDaSalvare, true);
        sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                new BigDecimal(criterioDaSalvare.getIdCriterioRaggr()), param.getNomePagina());
        return criterioDaSalvare;
    }

    public DecCriterioRaggr salvataggioAutomaticoCriterioRaggrStdNoAutomTipoUd(
            DecVCreaCritRaggrTipoUd creaCritRaggrTipoUd) {
        DecCriterioRaggr criterioDaSalvare = new DecCriterioRaggr();
        criterioDaSalvare.setNmCriterioRaggr(creaCritRaggrTipoUd.getNmCriterioRaggr());
        criterioDaSalvare.setDsCriterioRaggr(creaCritRaggrTipoUd.getDsCriterioRaggr());
        criterioDaSalvare.setDtIstituz(getNow());
        criterioDaSalvare.setDtSoppres(get2444());
        criterioDaSalvare.setFlFiltroRangeRegistroKey(creaCritRaggrTipoUd.getFlFiltroRangeRegistroKey());
        criterioDaSalvare.setFlFiltroRegistroKey(creaCritRaggrTipoUd.getFlFiltroRegistroKey());
        criterioDaSalvare.setFlFiltroSistemaMigraz(creaCritRaggrTipoUd.getFlFiltroSistemaMigraz());
        criterioDaSalvare.setFlFiltroTiEsitoVerifFirme(creaCritRaggrTipoUd.getFlFiltroTiEsitoVerifFirme());
        criterioDaSalvare.setFlFiltroTipoDoc(creaCritRaggrTipoUd.getFlFiltroTipoDoc());
        criterioDaSalvare.setFlFiltroTipoUnitaDoc(creaCritRaggrTipoUd.getFlFiltroTipoUnitaDoc());
        criterioDaSalvare.setDecCriterioFiltroMultiplos(new ArrayList<DecCriterioFiltroMultiplo>());
        OrgStrut orgStrut = crHelper.findById(OrgStrut.class, creaCritRaggrTipoUd.getIdStrut().longValue());
        criterioDaSalvare.setOrgStrut(orgStrut);
        DecTipoUnitaDoc decTipoUnitaDoc = crHelper.findById(DecTipoUnitaDoc.class,
                creaCritRaggrTipoUd.getIdTipoUnitaDoc().longValue());
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(criterioDaSalvare);
        filtro.setDecRegistroRangeUnitaDoc(null);
        filtro.setDecRegistroUnitaDoc(null);
        filtro.setDecTipoDoc(null);
        filtro.setDecTipoUnitaDoc(decTipoUnitaDoc);
        filtro.setTiEsitoVerifFirme(null);
        filtro.setTiFiltroMultiplo(ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_UNI_DOC.name());
        criterioDaSalvare.getDecCriterioFiltroMultiplos().add(filtro);
        criterioDaSalvare.setNiMaxComp(creaCritRaggrTipoUd.getNiMaxComp());
        criterioDaSalvare.setNiTempoScadChius(creaCritRaggrTipoUd.getNiTempoScadChius());
        criterioDaSalvare.setTiTempoScadChius(creaCritRaggrTipoUd.getTiTempoScadChius());
        criterioDaSalvare.setFlCriterioRaggrStandard("1");
        criterioDaSalvare.setFlCriterioRaggrFisc(creaCritRaggrTipoUd.getFlCriterioRaggrFisc());
        criterioDaSalvare.setTiValidElenco(creaCritRaggrTipoUd.getTiValidElenco() != null
                ? TiValidElencoCriterio.valueOf(creaCritRaggrTipoUd.getTiValidElenco()) : null);
        criterioDaSalvare.setTiModValidElenco(creaCritRaggrTipoUd.getTiModValidElenco() != null
                ? TiModValidElencoCriterio.valueOf(creaCritRaggrTipoUd.getTiModValidElenco()) : null);
        crHelper.insertEntity(criterioDaSalvare, true);
        return criterioDaSalvare;
    }

    public DecCriterioRaggr salvataggioAutomaticoCriterioRaggrStdNoAutomTipoDoc(
            DecVCreaCritRaggrTipoDoc creaCritRaggrTipoDoc) {
        DecCriterioRaggr criterioDaSalvare = new DecCriterioRaggr();
        criterioDaSalvare.setNmCriterioRaggr(creaCritRaggrTipoDoc.getNmCriterioRaggr());
        criterioDaSalvare.setDsCriterioRaggr(creaCritRaggrTipoDoc.getDsCriterioRaggr());
        criterioDaSalvare.setDtIstituz(getNow());
        criterioDaSalvare.setDtSoppres(get2444());
        criterioDaSalvare.setFlFiltroRangeRegistroKey(creaCritRaggrTipoDoc.getFlFiltroRangeRegistroKey());
        criterioDaSalvare.setFlFiltroRegistroKey(creaCritRaggrTipoDoc.getFlFiltroRegistroKey());
        criterioDaSalvare.setFlFiltroSistemaMigraz(creaCritRaggrTipoDoc.getFlFiltroSistemaMigraz());
        criterioDaSalvare.setFlFiltroTiEsitoVerifFirme(creaCritRaggrTipoDoc.getFlFiltroTiEsitoVerifFirme());
        criterioDaSalvare.setFlFiltroTipoDoc(creaCritRaggrTipoDoc.getFlFiltroTipoDoc());
        criterioDaSalvare.setFlFiltroTipoUnitaDoc(creaCritRaggrTipoDoc.getFlFiltroTipoUnitaDoc());
        criterioDaSalvare.setDecCriterioFiltroMultiplos(new ArrayList<DecCriterioFiltroMultiplo>());
        OrgStrut orgStrut = crHelper.findById(OrgStrut.class, creaCritRaggrTipoDoc.getIdStrut().longValue());
        criterioDaSalvare.setOrgStrut(orgStrut);
        DecTipoDoc decTipoDoc = crHelper.findById(DecTipoDoc.class, creaCritRaggrTipoDoc.getIdTipoDoc().longValue());
        DecCriterioFiltroMultiplo filtro = new DecCriterioFiltroMultiplo();
        filtro.setDecCriterioRaggr(criterioDaSalvare);
        filtro.setDecRegistroRangeUnitaDoc(null);
        filtro.setDecRegistroUnitaDoc(null);
        filtro.setDecTipoDoc(decTipoDoc);
        filtro.setDecTipoUnitaDoc(null);
        filtro.setTiEsitoVerifFirme(null);
        filtro.setTiFiltroMultiplo(ApplEnum.TipoFiltroMultiploCriteriRaggr.TIPO_DOC.name());
        criterioDaSalvare.getDecCriterioFiltroMultiplos().add(filtro);
        criterioDaSalvare.setNiMaxComp(creaCritRaggrTipoDoc.getNiMaxComp());
        criterioDaSalvare.setNiTempoScadChius(creaCritRaggrTipoDoc.getNiTempoScadChius());
        criterioDaSalvare.setTiTempoScadChius(creaCritRaggrTipoDoc.getTiTempoScadChius());
        criterioDaSalvare.setFlCriterioRaggrStandard("1");
        criterioDaSalvare.setFlCriterioRaggrFisc(creaCritRaggrTipoDoc.getFlCriterioRaggrFisc());
        criterioDaSalvare.setTiValidElenco(creaCritRaggrTipoDoc.getTiValidElenco() != null
                ? TiValidElencoCriterio.valueOf(creaCritRaggrTipoDoc.getTiValidElenco()) : null);
        criterioDaSalvare.setTiModValidElenco(creaCritRaggrTipoDoc.getTiModValidElenco() != null
                ? TiModValidElencoCriterio.valueOf(creaCritRaggrTipoDoc.getTiModValidElenco()) : null);
        crHelper.insertEntity(criterioDaSalvare, true);
        return criterioDaSalvare;
    }

    public DecCriterioRaggr salvataggioAutomaticoCriterioRaggrStdFiscNoAutomTipoUd(
            DecVCreaCritRaggrTipoUd creaCritRaggrTipoUd) {
        DecCriterioRaggr criterioDaSalvare = salvataggioAutomaticoCriterioRaggrStdNoAutomTipoUd(creaCritRaggrTipoUd);
        // Devo imporre che sia fiscale
        criterioDaSalvare.setFlCriterioRaggrFisc("1");
        return criterioDaSalvare;
    }

    public DecCriterioRaggrRowBean getDecCriterioRaggrRowBean(BigDecimal idStrut, String nmCriterioRaggr) {
        DecCriterioRaggr crit = crHelper.getDecCriterioRaggr(idStrut, nmCriterioRaggr);
        DecCriterioRaggrRowBean row = null;
        if (crit != null) {
            try {
                row = (DecCriterioRaggrRowBean) Transform.entity2RowBean(crit);
                // Info ambiente
                String tiGestElencoNoStd = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_NOSTD",
                        BigDecimal.valueOf(crit.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()), idStrut,
                        null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
                String tiGestElencoStdFisc = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_STD_FISC",
                        BigDecimal.valueOf(crit.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()), idStrut,
                        null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
                String tiGestElencoStdNofisc = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_STD_NOFISC",
                        BigDecimal.valueOf(crit.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()), idStrut,
                        null, null, CostantiDB.TipoAplVGetValAppart.STRUT);
                row.setString("ti_gest_elenco_std_nofisc", tiGestElencoStdNofisc);
                row.setString("ti_gest_elenco_nostd", tiGestElencoNoStd);
                row.setString("ti_gest_elenco_std_fisc", tiGestElencoStdFisc);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero del criterio di raggruppamento "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero del criterio di raggruppamento");
            }
        }
        return row;
    }

    public DecCriterioRaggrTableBean getDecCriterioRaggrTableBean(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, String nmCriterioRaggr) {
        DecCriterioRaggrTableBean table = new DecCriterioRaggrTableBean();
        List<DecCriterioRaggr> list = crHelper.retrieveDecCriterioRaggrList(idAmbiente, idEnte, idStrut,
                nmCriterioRaggr);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecCriterioRaggrTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei criteri di raggruppamento "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dei criteri di raggruppamento");
            }
        }
        return table;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaCriteriRaggruppamentoDaBottone(LogParam param, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc) throws ParerUserError {
        try {
            if (idRegistroUnitaDoc != null) {
                DecVCreaCritRaggrRegistro creaCritRegistro = crHelper
                        .getDecVCreaCritRaggrRegistro(idRegistroUnitaDoc.longValue());
                context.getBusinessObject(CriteriRaggruppamentoEjb.class)
                        .salvataggioAutomaticoCriterioRaggrStdNoAutomRegistro(param, creaCritRegistro);
            } else if (idTipoUnitaDoc != null) {
                DecVCreaCritRaggrTipoUd creaCritTipoUd = crHelper
                        .getDecVCreaCritRaggrTipoUd(idTipoUnitaDoc.longValue());
                DecCriterioRaggr criterioSalvato = context.getBusinessObject(CriteriRaggruppamentoEjb.class)
                        .salvataggioAutomaticoCriterioRaggrStdNoAutomTipoUd(creaCritTipoUd);
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                        new BigDecimal(criterioSalvato.getIdCriterioRaggr()), param.getNomePagina());
            } else if (idTipoDoc != null) {
                DecVCreaCritRaggrTipoDoc creaCritTipoDoc = crHelper.getDecVCreaCritRaggrTipoDoc(idTipoDoc.longValue());
                DecCriterioRaggr criterioSalvato = context.getBusinessObject(CriteriRaggruppamentoEjb.class)
                        .salvataggioAutomaticoCriterioRaggrStdNoAutomTipoDoc(creaCritTipoDoc);
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                        new BigDecimal(criterioSalvato.getIdCriterioRaggr()), param.getNomePagina());
            }
        } catch (IllegalStateException ex) {
            logger.error("Errore durante la creazione del criterio di raggruppamento: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante la creazione del criterio di raggruppamento");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long creaCriteriRaggruppamentoRegistroDaBottone(LogParam param, BigDecimal idRegistroUnitaDoc)
            throws ParerUserError {
        Long idCriterioRaggr = null;
        try {
            if (idRegistroUnitaDoc != null) {
                DecVCreaCritRaggrRegistro creaCritRegistro = crHelper
                        .getDecVCreaCritRaggrRegistro(idRegistroUnitaDoc.longValue());
                // Controllo che un criterio (anche non standard) esista già con quel nome su DB
                if (crHelper.existNomeCriterio(creaCritRegistro.getNmCriterioRaggr(), creaCritRegistro.getIdStrut())) {
                    throw new ParerUserError("Attenzione: impossibile creare il criterio standard "
                            + creaCritRegistro.getNmCriterioRaggr() + " in quanto per "
                            + "la struttura considerata esiste già un criterio con questo nome");
                }
                DecCriterioRaggr criterioSalvato = context.getBusinessObject(CriteriRaggruppamentoEjb.class)
                        .salvataggioAutomaticoCriterioRaggrStdNoAutomRegistro(param, creaCritRegistro);
                idCriterioRaggr = criterioSalvato.getIdCriterioRaggr();
            }
            return idCriterioRaggr;
        } catch (IllegalStateException ex) {
            logger.error("Errore durante la creazione del criterio di raggruppamento per il registro: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante la creazione del criterio di raggruppamento");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long creaCriteriRaggruppamentoTipoUdDaBottone(LogParam param, BigDecimal idTipoUnitaDoc)
            throws ParerUserError {
        Long idCriterioRaggr = null;
        try {
            if (idTipoUnitaDoc != null) {
                DecVCreaCritRaggrTipoUd creaCritTipoUd = crHelper
                        .getDecVCreaCritRaggrTipoUd(idTipoUnitaDoc.longValue());
                // Controllo che un criterio (anche non standard) esista già con quel nome su DB
                if (crHelper.existNomeCriterio(creaCritTipoUd.getNmCriterioRaggr(), creaCritTipoUd.getIdStrut())) {
                    throw new ParerUserError("Attenzione: impossibile creare il criterio standard "
                            + creaCritTipoUd.getNmCriterioRaggr() + " in quanto per "
                            + "la struttura considerata esiste già un criterio con questo nome");
                }
                DecCriterioRaggr criterioSalvato = context.getBusinessObject(CriteriRaggruppamentoEjb.class)
                        .salvataggioAutomaticoCriterioRaggrStdNoAutomTipoUd(creaCritTipoUd);
                idCriterioRaggr = criterioSalvato.getIdCriterioRaggr();
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                        new BigDecimal(idCriterioRaggr), param.getNomePagina());

                // Ora che ho aggiunto un criterio standard, ne verifico la coerenza
                // Recupero la lista degli eventuali criteri non coerenti
                List<String> criteriNonCoerenti = crHelper.getCriteriNonCoerenti(idTipoUnitaDoc);
                if (!criteriNonCoerenti.isEmpty()) {
                    String errorMessage = "Poiché al tipo di unità documentaria sono associati registri fiscali e non fiscali, non è possibile creare il criterio ";
                    throw new ParerUserError(errorMessage);
                }
            }
            return idCriterioRaggr;
        } catch (IllegalStateException ex) {
            logger.error("Errore durante la creazione del criterio di raggruppamento per il tipo unità documentaria: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante la creazione del criterio di raggruppamento");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long creaCriteriRaggruppamentoTipoDocDaBottone(LogParam param, BigDecimal idTipoDoc) throws ParerUserError {
        Long idCriterioRaggr = null;
        try {
            if (idTipoDoc != null) {
                DecVCreaCritRaggrTipoDoc creaCritTipoDoc = crHelper.getDecVCreaCritRaggrTipoDoc(idTipoDoc.longValue());
                // Controllo che un criterio (anche non standard) esista già con quel nome su DB
                if (crHelper.existNomeCriterio(creaCritTipoDoc.getNmCriterioRaggr(), creaCritTipoDoc.getIdStrut())) {
                    throw new ParerUserError("Attenzione: impossibile creare il criterio standard "
                            + creaCritTipoDoc.getNmCriterioRaggr() + " in quanto per "
                            + "la struttura considerata esiste già un criterio con questo nome");

                }
                DecCriterioRaggr criterioSalvato = context.getBusinessObject(CriteriRaggruppamentoEjb.class)
                        .salvataggioAutomaticoCriterioRaggrStdNoAutomTipoDoc(creaCritTipoDoc);
                idCriterioRaggr = criterioSalvato.getIdCriterioRaggr();
                sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                        param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_CRITERIO_RAGGRUPPAMENTO,
                        new BigDecimal(idCriterioRaggr), param.getNomePagina());
            }
            return idCriterioRaggr;
        } catch (IllegalStateException ex) {
            logger.error("Errore durante la creazione del criterio di raggruppamento per il tipo documento: "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Errore durante la creazione del criterio di raggruppamento");
        }
    }

    public boolean flCriterioRaggrFiscByRegistriTipiUdFiscali(List<BigDecimal> idRegistroKeyUnitaDocList,
            List<BigDecimal> idTipoUnitaDocList, boolean flCriterioRaggrStandard) throws ParerUserError {
        // Se non sono presenti registri e tipi ud, restituisco false
        if (idRegistroKeyUnitaDocList.isEmpty() && idTipoUnitaDocList.isEmpty()) {
            return false;
        } // Se entro nell'else, significa che ho almeno un registro e/o un tipo ud
        else {

            boolean allRegistriFiscali = false;
            boolean allRegistriAssociatiFiscali = false;

            // Controllo che tutti gli eventuali registri presenti siano fiscali
            if (!idRegistroKeyUnitaDocList.isEmpty()) {
                allRegistriFiscali = checkAllRegistriFiscali(idRegistroKeyUnitaDocList);
            }

            // Controllo che tutti gli eventuali tipi ud presenti abbiano associati registri fiscali
            if (!idTipoUnitaDocList.isEmpty()) {
                allRegistriAssociatiFiscali = checkAllRegistriAssociatiFiscali(idTipoUnitaDocList);
            }

            if (allRegistriFiscali && allRegistriAssociatiFiscali) {
                if (flCriterioRaggrStandard) {
                    return true;
                } else {
                    throw new ParerUserError("Almeno uno dei registri associati al tipo di unità documentaria presente "
                            + "nel criterio è fiscale e il criterio che si vuole salvare non è standard. "
                            + "Per procedere occorre modificare il presente criterio per renderlo standard o modificare "
                            + "la configurazione dei registri per renderli non fiscali");
                }
            } else {
                return false;
            }
        }
    }

    public boolean checkAllRegistriFiscali(List<BigDecimal> idRegistroKeyUnitaDocList) throws ParerUserError {
        return crHelper.areAllRegistriFiscali(idRegistroKeyUnitaDocList);
    }

    public boolean checkAllRegistriAssociatiFiscali(List<BigDecimal> idTipoUnitaDocList) throws ParerUserError {
        return crHelper.areAllRegistriAssociatiFiscali(idTipoUnitaDocList);
    }

    public boolean checkAllRegistriAssociatiFiscali(BigDecimal idTipoUnitaDoc) throws ParerUserError {
        List<BigDecimal> idTipoUnitaDocList = new ArrayList<>();
        idTipoUnitaDocList.add(idTipoUnitaDoc);
        return crHelper.areAllRegistriAssociatiFiscali(idTipoUnitaDocList);
    }

}
