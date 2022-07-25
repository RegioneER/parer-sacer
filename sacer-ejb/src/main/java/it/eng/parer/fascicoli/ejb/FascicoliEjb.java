package it.eng.parer.fascicoli.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.DecClasseErrSacer;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.DecErrSacer;
import it.eng.parer.entity.DecSelCriterioRaggrFasc;
import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.FasAmminPartec;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasLinkFascicolo;
import it.eng.parer.entity.FasRespFascicolo;
import it.eng.parer.entity.FasSogFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.MonContaFascicoliKo;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VrsErrSesFascicoloKo;
import it.eng.parer.entity.VrsFascicoloKo;
import it.eng.parer.entity.VrsSesFascicoloErr;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.entity.VrsSesFascicoloKo;
import it.eng.parer.entity.VrsXmlSesFascicoloErr;
import it.eng.parer.entity.VrsXmlSesFascicoloKo;
import it.eng.parer.entity.constraint.FasMetaVerAipFascicolo;
import it.eng.parer.fascicoli.dto.RicercaFascicoliBean;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm;
import it.eng.parer.slite.gen.form.CriteriRaggrFascicoliForm.CreaCriterioRaggrFascicoli;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascRowBean;
import it.eng.parer.slite.gen.tablebean.DecTipoFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasAmminPartecTableBean;
import it.eng.parer.slite.gen.tablebean.FasLinkFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.FasLinkFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasRespFascicoloRowBean_ext;
import it.eng.parer.slite.gen.tablebean.FasRespFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasSogFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.FasUniOrgRespFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.VrsSesFascicoloKoRowBean;
import it.eng.parer.slite.gen.tablebean.VrsSesFascicoloKoTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByFasRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByFasTableBean;
import it.eng.parer.slite.gen.viewbean.FasVLisUdInFascRowBean;
import it.eng.parer.slite.gen.viewbean.FasVLisUdInFascTableBean;
import it.eng.parer.slite.gen.viewbean.FasVRicFascicoliRowBean;
import it.eng.parer.slite.gen.viewbean.FasVRicFascicoliTableBean;
import it.eng.parer.slite.gen.viewbean.FasVVisFascicoloRowBean;
import it.eng.parer.slite.gen.viewbean.MonVChkCntFascRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascKoRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascKoTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisFascTableBean;
import it.eng.parer.viewEntity.ElvVRicElencoFascByFas;
import it.eng.parer.viewEntity.FasVLisUdInFasc;
import it.eng.parer.viewEntity.FasVRicFascicoli;
import it.eng.parer.viewEntity.FasVVisFascicolo;
import it.eng.parer.viewEntity.LogVVisLastSched;
import it.eng.parer.viewEntity.MonVCntFascByAmb;
import it.eng.parer.viewEntity.MonVCntFascByEnte;
import it.eng.parer.viewEntity.MonVCntFascByStrut;
import it.eng.parer.viewEntity.MonVCntFascByTiFasc;
import it.eng.parer.viewEntity.MonVCntFascKoByAmb;
import it.eng.parer.viewEntity.MonVCntFascKoByEnte;
import it.eng.parer.viewEntity.MonVCntFascKoByStrut;
import it.eng.parer.viewEntity.MonVCntFascKoByTiFasc;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.viewEntity.OrgVChkPartitionFascByAa;
import it.eng.parer.viewEntity.VrsVUpdFascicoloKo;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.CostantiDB.TipoAplVGetValAppart;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class FascicoliEjb {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FascicoliEjb.class);

    public static enum fieldSetToPopulate {
        FASCICOLI_VERSATI, FASCICOLI_FALLITI, FASCICOLI_VERSATI_B30, FASCICOLI_FALLITI_B30
    }

    public static enum statoConservazioneFascicoliVersati {
        PRESA_IN_CARICO, AIP_GENERATO, AIP_IN_AGGIORNAMENTO, ANNULLATA
    }

    public static enum statoFascicoliVersFalliti {
        NON_RISOLUBILE, VERIFICATO, NON_VERIFICATO
    }

    public static enum statoSessioneFascicoliErrata {
        NON_RISOLUBILE, VERIFICATO, NON_VERIFICATO
    }

    public static enum statoSessioneFascicoloKo {
        NON_RISOLUBILE, VERIFICATO, NON_VERIFICATO, RISOLTO
    }

    public static enum tipoUsoClasseErrore {
        GENERICO, VERS_FASCICOLO, VERS_UNITA_DOC, RECUP_UNITA_DOC, ANNULL_UNITA_DOC, SERVIZI_UTENTE
    }

    public static enum tipoXmlSessioneFascicoloErrata {
        RICHIESTA, RISPOSTA
    }

    /*
     * Costanti per il monitoraggio fascicoli
     */
    public static final String STATO_MONITOR_FASC_TOTALE = "TOTALE";
    public static final String STATO_MONITOR_FASC_NON_SELEZ_SCHED = "NON_SELEZ_SCHED";
    public static final String STATO_MONITOR_FASC_IN_ATTESA_SCHED = "IN_ATTESA_SCHED";

    public static final String STATO_MONITOR_FASC_KO_FALLITO = "FALLITO";
    public static final String STATO_MONITOR_FASC_KO_VERIFICATO = "VERIFICATO";
    public static final String STATO_MONITOR_FASC_KO_NON_RISOLUBILE = "NON_RISOLUBILE";
    public static final String STATO_MONITOR_FASC_KO_NON_VERIFICATO = "NON_VERIFICATO";

    public static final String TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI = "OGGI";
    public static final String TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG = "before30gg";
    public static final String TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG = "30gg";

    public static final String DATO_NON_DISPONIBILE = "N/A";

    @EJB
    private FascicoliHelper fascicoliHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private MonitoraggioHelper monitoraggioHelper;
    @EJB
    private StruttureHelper struttureHelper;

    public FasVRicFascicoliTableBean ricercaFascicoli(RicercaFascicoliBean bean, BigDecimal idStruct, long userId) {
        FasVRicFascicoliTableBean result = new FasVRicFascicoliTableBean();
        List<FasVRicFascicoli> list = fascicoliHelper.retrieveFascicoli(bean, idStruct, userId);

        if (list != null && !list.isEmpty()) {
            try {
                result = (FasVRicFascicoliTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }

        }
        return result;
    }

    public FasVRicFascicoliTableBean ricercaFascicoliAnnullati(RicercaFascicoliBean bean, BigDecimal idStruct,
            long userId) {
        FasVRicFascicoliTableBean result = new FasVRicFascicoliTableBean();
        List<FasVRicFascicoli> list = fascicoliHelper.retrieveFascicoliAnnullati(bean, idStruct, userId);

        if (list != null && !list.isEmpty()) {
            try {
                for (FasVRicFascicoli fasc : list) {
                    FasVRicFascicoliRowBean rowBean = (FasVRicFascicoliRowBean) Transform.entity2RowBean(fasc);

                    FasFascicolo ff = fascicoliHelper.findById(FasFascicolo.class, fasc.getIdFascicolo());
                    if (ff.getDtAnnull() != null) {
                        rowBean.setTimestamp("dt_annul", new Timestamp(ff.getDtAnnull().getTime()));
                    }
                    rowBean.setString("nt_annul", ff.getNtAnnul());

                    // Ricavo l'identificativo e il tipo della richiesta di annullamento con stato EVASA cui
                    // l'annullamento si riferisce
                    AroRichAnnulVers aroRichAnnulVers = fascicoliHelper
                            .getAroRichAnnulVersFasc(fasc.getIdFascicolo().longValue());
                    if (aroRichAnnulVers != null) {
                        rowBean.setBigDecimal("id_rich_annul_vers",
                                BigDecimal.valueOf(aroRichAnnulVers.getIdRichAnnulVers()));
                        rowBean.setString("ti_creazione_rich_annul_vers",
                                aroRichAnnulVers.getTiCreazioneRichAnnulVers());
                    }
                    result.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero dei fascicoli annullati" + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
            }
        }
        return result;
    }

    public FasVVisFascicoloRowBean retrieveFasVVisFascicolo(BigDecimal idFascicolo) {
        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
        FasVVisFascicoloRowBean result = new FasVVisFascicoloRowBean();
        FasVVisFascicolo dettaglio = fascicoliHelper.retrieveFasVVisFascicolo(idFascicolo.longValue());

        if (dettaglio != null) {
            try {
                result = (FasVVisFascicoloRowBean) Transform.entity2RowBean(dettaglio);

                if (StringUtils.isNotBlank(result.getCdRegKeyUnitaDocFirst()) && result.getAaKeyUnitaDocFirst() != null
                        && StringUtils.isNotBlank(result.getCdKeyUnitaDocFirst())) {
                    result.setString("unita_doc_first", result.getCdRegKeyUnitaDocFirst() + " - "
                            + result.getAaKeyUnitaDocFirst() + " - " + result.getCdKeyUnitaDocFirst());
                }

                if (StringUtils.isNotBlank(result.getCdRegKeyUnitaDocLast()) && result.getAaKeyUnitaDocLast() != null
                        && StringUtils.isNotBlank(result.getCdKeyUnitaDocLast())) {
                    result.setString("unita_doc_last", result.getCdRegKeyUnitaDocLast() + " - "
                            + result.getAaKeyUnitaDocLast() + " - " + result.getCdKeyUnitaDocLast());
                }

                if (StringUtils.isNotBlank(result.getCdIpaAmminTitol())
                        && StringUtils.isNotBlank(result.getDsAmminTitol())) {
                    result.setString("ammin_titol", result.getCdIpaAmminTitol() + " - " + result.getDsAmminTitol());
                }

                if (StringUtils.isNotBlank(result.getCdProcAmmin())
                        && StringUtils.isNotBlank(result.getDsProcAmmin())) {
                    result.setString("proc_ammin", result.getCdProcAmmin() + " - " + result.getDsProcAmmin());
                }

                String blXmlSegnatura = result.getBlXmlSegnatura();
                if (blXmlSegnatura != null) {
                    result.setBlXmlSegnatura(formatter.prettyPrintWithDOM3LS(blXmlSegnatura));
                }

                String blXmlVersProfilo = result.getBlXmlVersProfilo();
                if (blXmlVersProfilo != null) {
                    result.setBlXmlVersProfilo(formatter.prettyPrintWithDOM3LS(blXmlVersProfilo));
                }

                String blXmlVersRapp = result.getBlXmlVersRapp();
                if (blXmlVersRapp != null) {
                    result.setBlXmlVersRapp(formatter.prettyPrintWithDOM3LS(blXmlVersRapp));
                }

                String blXmlVersSip = result.getBlXmlVersSip();
                if (blXmlVersSip != null) {
                    result.setBlXmlVersSip(formatter.prettyPrintWithDOM3LS(blXmlVersSip));
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return result;
    }

    public FasVRicFascicoliRowBean retrieveFasVRicFascicoli(BigDecimal idFascicolo) {
        FasVRicFascicoliRowBean result = new FasVRicFascicoliRowBean();
        FasVRicFascicoli dettaglio = fascicoliHelper.retrieveFasVRicFascicoli(idFascicolo.longValue());

        if (dettaglio != null) {
            try {
                result = (FasVRicFascicoliRowBean) Transform.entity2RowBean(dettaglio);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return result;
    }

    public BaseRow retrieveFasFileMetaVerAipFasc(BigDecimal idFascicolo, String tiMeta) {
        BaseRow row = null;

        XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();

        FasFileMetaVerAipFasc result = fascicoliHelper.getFasFileMetaVerAipFasc(idFascicolo.longValue(), tiMeta);
        if (result != null) {
            row = new BaseRow();
            if (FasMetaVerAipFascicolo.TiMeta.valueOf(tiMeta).equals(FasMetaVerAipFascicolo.TiMeta.INDICE)) {
                row.setString("ds_urn_aip_fascicolo", result.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo());
                row.setString("ds_urn_normaliz_aip_fascicolo",
                        result.getFasMetaVerAipFascicolo().getDsUrnNormalizMetaFascicolo());
            } else if (FasMetaVerAipFascicolo.TiMeta.valueOf(tiMeta).equals(FasMetaVerAipFascicolo.TiMeta.FASCICOLO)) {
                row.setString("ds_urn_file_fascicolo", result.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo());
                row.setString("ds_urn_normaliz_file_fascicolo",
                        result.getFasMetaVerAipFascicolo().getDsUrnNormalizMetaFascicolo());
            }
            row.setString("ds_hash_file", result.getFasMetaVerAipFascicolo().getDsHashFile());
            row.setString("ds_algo_hash_file", result.getFasMetaVerAipFascicolo().getDsAlgoHashFile());
            row.setString("cd_encoding_hash_file", result.getFasMetaVerAipFascicolo().getCdEncodingHashFile());
            row.setString("bl_file_ver_indice_aip", formatter.prettyPrintWithDOM3LS(result.getBlFileVerIndiceAip()));
            if (result.getFasMetaVerAipFascicolo().getFasVerAipFascicolo().getIdEnteConserv() != null) {
                row.setString("nm_ente_conserv",
                        fascicoliHelper
                                .findById(SIOrgEnteSiam.class,
                                        result.getFasMetaVerAipFascicolo().getFasVerAipFascicolo().getIdEnteConserv())
                                .getNmEnteSiam());
            }
        }

        return row;
    }

    public FasVLisUdInFascTableBean retrieveFasVLisUdInFasc(BigDecimal idFascicolo, long userId) {
        FasVLisUdInFascTableBean result = new FasVLisUdInFascTableBean();
        List<FasVLisUdInFasc> list = fascicoliHelper.retrieveFasVLisUdInFasc(idFascicolo.longValue(), userId);

        if (list != null && !list.isEmpty()) {
            try {
                for (FasVLisUdInFasc l : list) {
                    FasVLisUdInFascRowBean rowBean = (FasVLisUdInFascRowBean) Transform.entity2RowBean(l);
                    String stato = rowBean.getTiStatoConservazione();

                    if (StringUtils.isNotBlank(stato)) {
                        if (it.eng.parer.viewEntity.constants.FasVLisUdInFasc.TiStatoConservazione.ANNULLATA.name()
                                .equals(stato)) {
                            rowBean.setString("annullata", "1");
                        } else {
                            rowBean.setString("annullata", "0");
                        }
                    }
                    result.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return result;
    }

    public FasAmminPartecTableBean retrieveFasAmminPartec(BigDecimal idFascicolo) {
        FasAmminPartecTableBean result = new FasAmminPartecTableBean();
        List<FasAmminPartec> list = fascicoliHelper.retrieveFasAmminPartec(idFascicolo.longValue());

        if (list != null && !list.isEmpty()) {
            try {
                result = (FasAmminPartecTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return result;
    }

    public FasSogFascicoloTableBean retrieveFasSogFascicolo(BigDecimal idFascicolo) {
        FasSogFascicoloTableBean result = new FasSogFascicoloTableBean();
        List<FasSogFascicolo> list = fascicoliHelper.retrieveFasSogFascicolo(idFascicolo.longValue());

        if (list != null && !list.isEmpty()) {
            try {
                result = (FasSogFascicoloTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return result;
    }

    public FasRespFascicoloTableBean retrieveFasRespFascicolo(BigDecimal idFascicolo) {
        FasRespFascicoloTableBean result = new FasRespFascicoloTableBean();
        List<FasRespFascicolo> list = fascicoliHelper.retrieveFasRespFascicolo(idFascicolo.longValue());
        if (list != null && !list.isEmpty()) {
            for (FasRespFascicolo l : list) {
                FasRespFascicoloRowBean_ext row = new FasRespFascicoloRowBean_ext();
                row.entityToRowBean(l);
                result.add(row);
            }
        }
        return result;
    }

    public FasUniOrgRespFascicoloTableBean retrieveFasUniOrgRespFascicolo(BigDecimal idFascicolo) {
        FasUniOrgRespFascicoloTableBean result = new FasUniOrgRespFascicoloTableBean();
        List<FasUniOrgRespFascicolo> list = fascicoliHelper.retrieveFasUniOrgRespFascicolo(idFascicolo.longValue());
        if (list != null && !list.isEmpty()) {
            try {
                result = (FasUniOrgRespFascicoloTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return result;
    }

    public FasLinkFascicoloTableBean retrieveFasLinkFascicolo(BigDecimal idFascicolo) {
        FasLinkFascicoloTableBean result = new FasLinkFascicoloTableBean();
        List<FasLinkFascicolo> list = fascicoliHelper.retrieveFasLinkFascicolo(idFascicolo.longValue());
        List<FasLinkFascicolo> listparent = fascicoliHelper.retrieveFasLinkFascicoloParent(idFascicolo.longValue());
        try {
            buildMyLinks(result, list);
            // parent
            buildParentLinks(result, listparent);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | InvocationTargetException ex) {
            logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return result;
    }

    public ElvVRicElencoFascByFasTableBean retrieveFasElvFascicolo(BigDecimal idFascicolo) {
        ElvVRicElencoFascByFasTableBean result = new ElvVRicElencoFascByFasTableBean();
        List<ElvVRicElencoFascByFas> list = fascicoliHelper.retrieveFasElvFascicolo(idFascicolo.longValue());

        if (list != null && !list.isEmpty()) {
            try {
                for (ElvVRicElencoFascByFas l : list) {
                    ElvVRicElencoFascByFasRowBean rowBean = (ElvVRicElencoFascByFasRowBean) Transform.entity2RowBean(l);
                    rowBean.setString("nm_ente_nm_strut", rowBean.getNmEnte() + " - " + rowBean.getNmStrut());
                    result.add(rowBean);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei fascicoli " + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }

        return result;
    }

    private void buildParentLinks(FasLinkFascicoloTableBean result, List<FasLinkFascicolo> listparent)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        if (listparent != null && !listparent.isEmpty()) {
            FasLinkFascicoloRowBean rowBean;

            for (FasLinkFascicolo ent : listparent) {
                rowBean = (FasLinkFascicoloRowBean) Transform.entity2RowBean(ent);
                // recupero il fasciolo dall'id del "padre"
                FasFascicolo fasFascicolo = fascicoliHelper.getEntityManager().find(FasFascicolo.class,
                        rowBean.getIdFascicolo().longValue());
                rowBean.setAaFascicoloLink(fasFascicolo.getAaFascicolo());
                rowBean.setCdKeyFascicoloLink(fasFascicolo.getCdKeyFascicolo());
                rowBean.setIdFascicoloLink(BigDecimal.valueOf(fasFascicolo.getIdFascicolo()));
                // fixed
                rowBean.setString("fl_link", "1");
                result.add(rowBean);
            }
        }
    }

    private void buildMyLinks(FasLinkFascicoloTableBean result, List<FasLinkFascicolo> list)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        if (list != null && !list.isEmpty()) {
            FasLinkFascicoloRowBean rowBean;

            for (FasLinkFascicolo ent : list) {
                rowBean = (FasLinkFascicoloRowBean) Transform.entity2RowBean(ent);
                if (ent.getFasFascicoloLink() != null) {
                    rowBean.setString("fl_link", "1");
                } else {
                    rowBean.setString("fl_link", "0");
                }
                result.add(rowBean);
            }
        }
    }

    public MonVChkCntFascRowBean calcolaTotFascicoliVersati(MonVChkCntFascRowBean rowBean, BigDecimal idUser,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo) {
        rowBean.resetCountersVersati();
        List l = null;
        if (idTipoFascicolo != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliByTipoFascicolo(idTipoFascicolo);
        } else if (idStrut != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliByStrutUserId(idStrut, idUser);
        } else if (idEnte != null && idUser != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliByEnteUser(idEnte, idUser);
        } else if (idAmbiente != null && idUser != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliByAmbUser(idAmbiente, idUser);
        } else {
            throw new RuntimeException(
                    "Errore parametri nella chiamata al metodo calcolaTotFascicoliVersati() dell ejb!");
        }

        if (l != null && !l.isEmpty()) {
            String tipoStato = null, tipoData = null;
            BigDecimal niFasc = null;
            for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                Object next = iterator.next();
                tipoStato = getTiStatoFascFromCntFasc(next);
                tipoData = getTiDtCreazioneFromCntFasc(next);
                niFasc = getNiFascFromCntFasc(next);
                switch (tipoStato) {
                case STATO_MONITOR_FASC_TOTALE:
                    switch (tipoData) {
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI:
                        rowBean.setNiFascCorr(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG:
                        rowBean.setNiFascB30gg(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG:
                        rowBean.setNiFasc30gg(niFasc);
                        break;
                    }
                    break;
                case STATO_MONITOR_FASC_NON_SELEZ_SCHED:
                    switch (tipoData) {
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI:
                        rowBean.setNiFascNoselSchedCorr(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG:
                        rowBean.setNiFascNoselSchedB30gg(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG:
                        rowBean.setNiFascNoselSched30gg(niFasc);
                        break;
                    }
                    break;
                case STATO_MONITOR_FASC_IN_ATTESA_SCHED:
                    switch (tipoData) {
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI:
                        rowBean.setNiFascAttesaSchedCorr(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG:
                        rowBean.setNiFascAttesaSchedB30gg(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG:
                        rowBean.setNiFascAttesaSched30gg(niFasc);
                        break;
                    }
                    break;
                }
            }
        }
        return rowBean;
    }

    public MonVChkCntFascRowBean calcolaTotFascicoliFalliti(MonVChkCntFascRowBean rowBean, BigDecimal idUser,
            BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo) {
        rowBean.resetCountersFalliti();
        List l = null;
        if (idTipoFascicolo != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliKoByTipoFascicolo(idTipoFascicolo);
        } else if (idStrut != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliKoByStrutUserId(idStrut, idUser);
        } else if (idEnte != null && idUser != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliKoByEnteUser(idEnte, idUser);
        } else if (idAmbiente != null && idUser != null) {
            l = fascicoliHelper.retrieveCntMonFascicoliKoByAmbUser(idAmbiente, idUser);
        } else {
            throw new RuntimeException(
                    "Errore parametri nella chiamata al metodo calcolaTotFascicoliFalliti() dell ejb!");
        }

        if (l != null && !l.isEmpty()) {
            String tipoStato = null, tipoData = null;
            BigDecimal niFasc = null;
            for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                Object next = iterator.next();
                tipoStato = getTiStatoFascFromCntFasc(next);
                tipoData = getTiDtCreazioneFromCntFasc(next);
                niFasc = getNiFascFromCntFasc(next);
                switch (tipoStato) {
                case STATO_MONITOR_FASC_KO_NON_RISOLUBILE:
                    switch (tipoData) {
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI:
                        rowBean.setNiFascKoNonRisolubCorr(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG:
                        rowBean.setNiFascKoNonRisolubB30gg(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG:
                        rowBean.setNiFascKoNonRisolub30gg(niFasc);
                        break;
                    }
                    break;
                case STATO_MONITOR_FASC_KO_VERIFICATO:
                    switch (tipoData) {
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI:
                        rowBean.setNiFascKoVerifCorr(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG:
                        rowBean.setNiFascKoVerifB30gg(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG:
                        rowBean.setNiFascKoVerif30gg(niFasc);
                        break;
                    }
                    break;
                case STATO_MONITOR_FASC_KO_NON_VERIFICATO:
                    switch (tipoData) {
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_OGGI:
                        rowBean.setNiFascKoNonVerifCorr(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_B30GG:
                        rowBean.setNiFascKoNonVerifB30gg(niFasc);
                        break;
                    case TI_DT_CREAZIONE_MONITORAGGIO_FASC_30GG:
                        rowBean.setNiFascKoNonVerif30gg(niFasc);
                        break;
                    }
                    break;
                }
            }
            // Falliti è calcolato come la somma delle altre righe
            BigDecimal tot = rowBean.getNiFascKoVerifCorr().add(rowBean.getNiFascKoNonVerifCorr())
                    .add(rowBean.getNiFascKoNonRisolubCorr());
            rowBean.setNiFascKoFallCorr(tot);
            tot = rowBean.getNiFascKoVerif30gg().add(rowBean.getNiFascKoNonVerif30gg())
                    .add(rowBean.getNiFascKoNonRisolub30gg());
            rowBean.setNiFascKoFall30gg(tot);
            tot = rowBean.getNiFascKoVerifB30gg().add(rowBean.getNiFascKoNonVerifB30gg())
                    .add(rowBean.getNiFascKoNonRisolubB30gg());
            rowBean.setNiFascKoFallB30gg(tot);
        }
        return rowBean;
    }

    public MonVChkCntFascRowBean calcolaRiepilogo(BigDecimal idUser, BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, BigDecimal idTipoFascicolo) {
        MonVChkCntFascRowBean rowBean = new MonVChkCntFascRowBean();
        List l = null, l2 = null;
        if (idTipoFascicolo != null) {
            l = fascicoliHelper.retrieveMonFascicoliByTipoFascicolo(idTipoFascicolo);
            l2 = fascicoliHelper.retrieveMonFascicoliKoByTipoFascicolo(idTipoFascicolo);
        } else if (idStrut != null) {
            l = fascicoliHelper.retrieveMonFascicoliByStrutUser(idStrut, idUser);
            l2 = fascicoliHelper.retrieveMonFascicoliKoByStrutUser(idStrut, idUser);
        } else if (idEnte != null && idUser != null) {
            l = fascicoliHelper.retrieveMonFascicoliByEnteUser(idEnte, idUser);
            l2 = fascicoliHelper.retrieveMonFascicoliKoByEnteUser(idEnte, idUser);
        } else if (idAmbiente != null && idUser != null) {
            l = fascicoliHelper.retrieveMonFascicoliByAmbUser(idAmbiente, idUser);
            l2 = fascicoliHelper.retrieveMonFascicoliKoByAmbUser(idAmbiente, idUser);
        } else {
            throw new RuntimeException("Errore parametri nella chiamata al metodo calcolaRiepilogo() dell'ejb!");
        }
        rowBean.resetCounters();
        if (l != null && !l.isEmpty()) {
            rowBean.entityToRowBean(l.iterator().next());
        }
        if (l2 != null && !l2.isEmpty()) {
            rowBean.entityToRowBean(l2.iterator().next());
        }

        return rowBean;
    }

    private String getTiStatoFascFromCntFasc(Object obj) {
        if (obj instanceof MonVCntFascByTiFasc) {
            return ((MonVCntFascByTiFasc) obj).getTiStatoFasc();
        } else if (obj instanceof MonVCntFascByStrut) {
            return ((MonVCntFascByStrut) obj).getTiStatoFasc();
        } else if (obj instanceof MonVCntFascByEnte) {
            return ((MonVCntFascByEnte) obj).getTiStatoFasc();
        } else if (obj instanceof MonVCntFascByAmb) {
            return ((MonVCntFascByAmb) obj).getTiStatoFasc();
        }
        if (obj instanceof MonVCntFascKoByTiFasc) {
            return ((MonVCntFascKoByTiFasc) obj).getTiStatoFascKo();
        } else if (obj instanceof MonVCntFascKoByStrut) {
            return ((MonVCntFascKoByStrut) obj).getTiStatoFascKo();
        } else if (obj instanceof MonVCntFascKoByEnte) {
            return ((MonVCntFascKoByEnte) obj).getTiStatoFascKo();
        } else if (obj instanceof MonVCntFascKoByAmb) {
            return ((MonVCntFascKoByAmb) obj).getTiStatoFascKo();
        } else {
            throw new RuntimeException("Tipo di istanza non prevista per il metodo getTiStatoFascFromCntFasc()");
        }
    }

    private String getTiDtCreazioneFromCntFasc(Object obj) {
        if (obj instanceof MonVCntFascByTiFasc) {
            return ((MonVCntFascByTiFasc) obj).getTiDtCreazione();
        } else if (obj instanceof MonVCntFascByStrut) {
            return ((MonVCntFascByStrut) obj).getTiDtCreazione();
        } else if (obj instanceof MonVCntFascByEnte) {
            return ((MonVCntFascByEnte) obj).getTiDtCreazione();
        } else if (obj instanceof MonVCntFascByAmb) {
            return ((MonVCntFascByAmb) obj).getTiDtCreazione();
        }
        if (obj instanceof MonVCntFascKoByTiFasc) {
            return ((MonVCntFascKoByTiFasc) obj).getTiDtCreazione();
        } else if (obj instanceof MonVCntFascKoByStrut) {
            return ((MonVCntFascKoByStrut) obj).getTiDtCreazione();
        } else if (obj instanceof MonVCntFascKoByEnte) {
            return ((MonVCntFascKoByEnte) obj).getTiDtCreazione();
        } else if (obj instanceof MonVCntFascKoByAmb) {
            return ((MonVCntFascKoByAmb) obj).getTiDtCreazione();
        } else {
            throw new RuntimeException("Tipo di istanza non prevista per il metodo getTiDtCreazioneFromCntFasc()");
        }
    }

    private BigDecimal getNiFascFromCntFasc(Object obj) {
        if (obj instanceof MonVCntFascByTiFasc) {
            return ((MonVCntFascByTiFasc) obj).getNiFasc();
        } else if (obj instanceof MonVCntFascByStrut) {
            return ((MonVCntFascByStrut) obj).getNiFasc();
        } else if (obj instanceof MonVCntFascByEnte) {
            return ((MonVCntFascByEnte) obj).getNiFasc();
        } else if (obj instanceof MonVCntFascByAmb) {
            return ((MonVCntFascByAmb) obj).getNiFasc();
        }
        if (obj instanceof MonVCntFascKoByTiFasc) {
            return ((MonVCntFascKoByTiFasc) obj).getNiFascKo();
        } else if (obj instanceof MonVCntFascKoByStrut) {
            return ((MonVCntFascKoByStrut) obj).getNiFascKo();
        } else if (obj instanceof MonVCntFascKoByEnte) {
            return ((MonVCntFascKoByEnte) obj).getNiFascKo();
        } else if (obj instanceof MonVCntFascKoByAmb) {
            return ((MonVCntFascKoByAmb) obj).getNiFascKo();
        } else {
            throw new RuntimeException("Tipo di istanza non prevista per il metodo getTiDtCreazioneFromCntFasc()");
        }
    }

    public MonVLisFascTableBean ricercaFascicoliPerMonitoraggio(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa, String rangeNumeroA,
            String statoIndiceAip, Set<String> statiConservazione, String flSessioneFascicoloKo) {
        MonVLisFascTableBean t = new MonVLisFascTableBean();
        List l = fascicoliHelper.retrieveVLisFasc(idUser, idAmbiente, idEnte, idStrut, idTipoFascicolo, dateValidate,
                rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoIndiceAip, statiConservazione,
                flSessioneFascicoloKo);
        if (l != null && !l.isEmpty()) {
            MonVLisFascRowBean rb = null;
            for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                Object ogg = iterator.next();
                rb = new MonVLisFascRowBean();
                rb.entityToRowBean(ogg);
                rb.setString("nm_ente_struttura", rb.getString("nm_ente") + " / " + rb.getString("nm_strut"));
                t.add(rb);
            }
        }
        return t;
    }

    public MonVLisFascKoTableBean ricercaFascicoliKoPerMonitoraggio(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa, String rangeNumeroA,
            String statoSessione, String cdClasseErr, String cdErr) {
        return ricercaFascicoliKoPerMonitoraggioInternal(idUser, idAmbiente, idEnte, idStrut, idTipoFascicolo,
                dateValidate, rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, cdClasseErr, cdErr);
    }

    public MonVLisFascKoTableBean ricercaFascicoliKoPerMonitoraggioErr(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa, String rangeNumeroA,
            String statoSessione, String cdClasseErr, String cdErr) {
        return ricercaFascicoliKoPerMonitoraggioInternal(idUser, idAmbiente, idEnte, idStrut, idTipoFascicolo,
                dateValidate, rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, cdClasseErr, cdErr);
    }

    private MonVLisFascKoTableBean ricercaFascicoliKoPerMonitoraggioInternal(BigDecimal idUser, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, Date[] dateValidate,
            BigDecimal rangeAnnoDa, BigDecimal rangeAnnoA, String rangeNumeroDa, String rangeNumeroA,
            String statoSessione, String cdClasseErr, String cdErr) {
        MonVLisFascKoTableBean t = new MonVLisFascKoTableBean();
        List l = fascicoliHelper.retrieveVLisFascKo(idUser, idAmbiente, idEnte, idStrut, idTipoFascicolo, dateValidate,
                rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, cdClasseErr, cdErr);
        if (l != null && !l.isEmpty()) {
            MonVLisFascKoRowBean rb = null;
            for (Iterator iterator = l.iterator(); iterator.hasNext();) {
                Object ogg = iterator.next();
                rb = new MonVLisFascKoRowBean();
                rb.entityToRowBean(ogg);
                rb.setString("nm_ente_struttura", rb.getString("nm_ente") + " / " + rb.getString("nm_strut"));
                t.add(rb);
            }
        }
        return t;
    }

    public VrsSesFascicoloKoTableBean ricercaVersamentiFascicoliKo(BigDecimal idFascicoloKo) {
        VrsSesFascicoloKoTableBean t = new VrsSesFascicoloKoTableBean();
        List<VrsSesFascicoloKo> l = fascicoliHelper.retrieveSessioniFalliteByIdFascKo(idFascicoloKo);
        if (l != null && !l.isEmpty()) {
            VrsSesFascicoloKoRowBean rb = null;
            for (Iterator<VrsSesFascicoloKo> iterator = l.iterator(); iterator.hasNext();) {
                VrsSesFascicoloKo ogg = iterator.next();
                rb = new VrsSesFascicoloKoRowBean();
                rb.entityToRowBean(ogg);
                rb.setString("nm_ente_struttura", rb.getString("nm_ente") + " / " + rb.getString("nm_strut"));
                DecErrSacer err = ogg.getDecErrSacer();
                if (err != null) {
                    rb.setString("cd_err", err.getCdErr());
                }
                t.add(rb);
            }
        }
        return t;
    }

    public DettaglioVersamentoFascicoloKo getDettaglioVersamentoFascicoloKo(BigDecimal idSesFascicoloKo) {
        DettaglioVersamentoFascicoloKo ret = null;
        VrsSesFascicoloKo ses = fascicoliHelper.getSessioneFallitaByIdSess(idSesFascicoloKo);
        if (ses != null) {
            ret = new DettaglioVersamentoFascicoloKo();
            BaseRow dett = new BaseRow();
            ret.setDettaglioVersamentoRB(dett);
            dett.setBigDecimal("id_ses_fascicolo_ko", idSesFascicoloKo);
            dett.setTimestamp("ts_ini_ses", new Timestamp(ses.getTsIniSes().getTime()));
            dett.setTimestamp("ts_fine_ses", new Timestamp(ses.getTsFineSes().getTime()));
            dett.setBigDecimal("aa_fascicolo", ses.getAaFascicolo());
            dett.setString("cd_key_fascicolo", ses.getVrsFascicoloKo().getCdKeyFascicolo());
            dett.setString("nm_tipo_fascicolo", ses.getDecTipoFascicolo().getNmTipoFascicolo());
            dett.setString("cd_err_princ", ses.getDecErrSacer().getCdErr());
            dett.setString("ds_err_princ", ses.getDsErrPrinc());
            dett.setString("ti_stato_ses", ses.getTiStatoSes());
            List<VrsXmlSesFascicoloKo> l = ses.getVrsXmlSesFascicoloKos();
            if (l != null && !l.isEmpty()) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                for (Iterator<VrsXmlSesFascicoloKo> iterator = l.iterator(); iterator.hasNext();) {
                    VrsXmlSesFascicoloKo xml = iterator.next();
                    if (xml.getTiXml().equals(tipoXmlSessioneFascicoloErrata.RICHIESTA.name())) {
                        String valXml = xml.getBlXml();
                        dett.setString("bl_xml_sip", valXml == null ? null : formatter.prettyPrintWithDOM3LS(valXml));
                    } else if (xml.getTiXml().equals(tipoXmlSessioneFascicoloErrata.RISPOSTA.name())) {
                        String valXml = xml.getBlXml();
                        dett.setString("bl_xml_rapp_vers",
                                valXml == null ? null : formatter.prettyPrintWithDOM3LS(valXml));
                    }
                }
            }
            IamUser user = ses.getIamUser();
            if (user != null) {
                dett.setBigDecimal("id_user_iam", new BigDecimal(user.getIdUserIam()));
                dett.setString("nm_utente", user.getNmUserid());
            }
            // ERRORI
            List<VrsErrSesFascicoloKo> lerr = ses.getVrsErrSesFascicoloKos();
            if (lerr != null && !lerr.isEmpty()) {
                BaseTable t = new BaseTable();
                ret.setListaErroriTB(t);
                BaseRow r = null;
                // Inserisce tutti gli errori tranne quello principale
                for (Iterator<VrsErrSesFascicoloKo> iterator = lerr.iterator(); iterator.hasNext();) {
                    VrsErrSesFascicoloKo err = iterator.next();
                    if (!err.getFlErrPrinc().equals("1")) {
                        r = new BaseRow();
                        r.setBigDecimal("pg_err", err.getPgErr());
                        r.setString("ti_err", err.getTiErr());
                        r.setString("cd_err", err.getDecErrSacer().getCdErr());
                        r.setString("ds_err", err.getDsErr());
                        t.add(r);
                    }
                }
            }
        }
        return ret;
    }

    public DecodeMap getClasseErrSacerByTipiUsoDecodeMap(List<String> tipiUso) {
        DecodeMap errori = new DecodeMap();
        BaseTable tabella = new BaseTable();
        BaseRow riga = null;
        List<DecClasseErrSacer> l = fascicoliHelper.retrieveClasseErrSacerByTipiUso(tipiUso);
        for (DecClasseErrSacer ogg : l) {
            riga = new BaseRow();
            riga.setString("cd_classe_err", ogg.getCdClasseErrSacer());
            riga.setString("ds_classe_err", ogg.getCdClasseErrSacer() + " - " + ogg.getDsClasseErrSacer());
            tabella.add(riga);
        }
        errori.populatedMap(tabella, "cd_classe_err", "ds_classe_err");
        return errori;
    }

    public DecodeMap getClasseErrSacerDecodeMap(List<String> tipiUso) {
        return getClasseErrSacerByTipiUsoDecodeMap(tipiUso);
    }

    public DecodeMap getErrSacerByCodClasseDecodeMap(String codClasse) {
        DecodeMap errori = new DecodeMap();
        BaseTable tabella = new BaseTable();
        BaseRow riga = null;
        List<DecErrSacer> l = fascicoliHelper.retrieveErrSacerByCodClasse(codClasse);
        for (DecErrSacer ogg : l) {
            riga = new BaseRow();
            riga.setString("cd_err", ogg.getCdErr());
            riga.setString("ds_err", ogg.getCdErr() + " - " + ogg.getDsErrFiltro());
            tabella.add(riga);
        }
        errori.populatedMap(tabella, "cd_err", "ds_err");
        return errori;
    }

    public BaseTable ricercaSessFascErrate(Date dataDa, Date dataA, String statoSessione, String cdClasseErr,
            String cdErr) {
        BaseTable t = new BaseTable();
        BaseRow r = null;
        List<VrsSesFascicoloErr> l = fascicoliHelper.retrieveSessFascErrate(dataDa, dataA, statoSessione, cdClasseErr,
                cdErr);
        if (l != null && !l.isEmpty()) {
            for (Iterator<VrsSesFascicoloErr> iterator = l.iterator(); iterator.hasNext();) {
                VrsSesFascicoloErr ogg = iterator.next();
                r = new BaseRow();
                r.setBigDecimal("id_ses_fascicolo_err", new BigDecimal(ogg.getIdSesFascicoloErr()));
                r.setTimestamp("ts_ini_ses", new Timestamp(ogg.getTsIniSes().getTime()));
                r.setString("nm_userid_ws", ogg.getNmUseridWs());
                r.setString("nm_ambiente", ogg.getNmAmbiente());
                r.setString("nm_ente", ogg.getNmEnte());
                r.setString("nm_strut", ogg.getNmStrut());
                r.setString("calc_ambiente",
                        (ogg.getNmAmbiente() != null ? ogg.getNmAmbiente() : "")
                                + (ogg.getNmEnte() != null ? (" / " + ogg.getNmEnte()) : "")
                                + (ogg.getNmStrut() != null ? (" / " + ogg.getNmStrut()) : ""));
                r.setString("nm_tipo_fascicolo", ogg.getNmTipoFascicolo());
                r.setBigDecimal("aa_fascicolo", ogg.getAaFascicolo());
                r.setString("cd_key_fascicolo", ogg.getCdKeyFascicolo());
                r.setString("calc_fascicolo", (ogg.getAaFascicolo() != null ? ogg.getAaFascicolo() : "")
                        + (ogg.getCdKeyFascicolo() != null ? (" / " + ogg.getCdKeyFascicolo()) : ""));
                r.setString("cd_err", ogg.getDecErrSacer().getCdErr());
                r.setString("ds_err", ogg.getDsErr());
                r.setString("ti_stato_ses", ogg.getTiStatoSes());
                t.add(r);
            }
        }
        return t;
    }

    public BaseRow caricaDettaglioSessFascErrata(BigDecimal idSess) {
        BaseRow r = new BaseRow();
        VrsSesFascicoloErr sess = fascicoliHelper.retrieveDettSessFascErr(idSess);
        if (sess != null) {
            r.setBigDecimal("id_ses_fascicolo_err", idSess);
            r.setTimestamp("ts_ini_ses", new Timestamp(sess.getTsIniSes().getTime()));
            r.setTimestamp("ts_fine_ses", new Timestamp(sess.getTsFineSes().getTime()));
            r.setString("nm_userid_ws", sess.getNmUseridWs());
            r.setString("nm_ambiente", sess.getNmAmbiente());
            r.setString("nm_ente", sess.getNmEnte());
            r.setString("nm_strut", sess.getNmStrut());
            r.setString("fl_esiste_struttura", sess.getOrgStrut() == null ? "0" : "1");
            r.setBigDecimal("aa_fascicolo", sess.getAaFascicolo());
            r.setString("cd_key_fascicolo", sess.getCdKeyFascicolo());
            DecTipoFascicolo tf = sess.getDecTipoFascicolo();
            if (tf != null) {
                r.setBigDecimal("id_tipo_fascicolo", new BigDecimal(tf.getIdTipoFascicolo()));
            }
            r.setString("fl_esiste_tipo_fascicolo", sess.getDecTipoFascicolo() == null ? "0" : "1");
            DecErrSacer des = sess.getDecErrSacer();
            if (des != null) {
                r.setBigDecimal("id_err_sacer", new BigDecimal(des.getIdErrSacer()));
                r.setString("cd_err", des.getCdErr());
            }
            r.setString("ds_err", sess.getDsErr());
            r.setString("ti_stato_ses", sess.getTiStatoSes());
            List<VrsXmlSesFascicoloErr> l = sess.getVrsXmlSesFascicoloErrs();
            if (l != null) {
                XmlPrettyPrintFormatter formatter = new XmlPrettyPrintFormatter();
                for (Iterator<VrsXmlSesFascicoloErr> iterator = l.iterator(); iterator.hasNext();) {
                    VrsXmlSesFascicoloErr xml = iterator.next();

                    if (xml.getTiXml().equals(tipoXmlSessioneFascicoloErrata.RICHIESTA.name())) {
                        String valXml = xml.getBlXml();
                        r.setString("bl_xml_sip", valXml == null ? null : formatter.prettyPrintWithDOM3LS(valXml));
                    } else if (xml.getTiXml().equals(tipoXmlSessioneFascicoloErrata.RISPOSTA.name())) {
                        String valXml = xml.getBlXml();
                        r.setString("bl_xml_rapp_vers",
                                valXml == null ? null : formatter.prettyPrintWithDOM3LS(valXml));
                    }
                }
            }
        }
        return r;
    }

    /*
     * Salva i dati di dettaglio della sessione fascicolo errato eventualmente modificati E verifica se si può
     * trasformare una Sessione errata in un versamento fallito
     */
    public SalvaDettaSessErrDto salvaDettaglioSessioneFascicoloErr(BigDecimal id, BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idTipoFascicolo, BigDecimal annoFascicolo,
            String numFascicolo) throws EMFError {
        SalvaDettaSessErrDto ret = new SalvaDettaSessErrDto();
        VrsSesFascicoloErr sess = fascicoliHelper.findById(VrsSesFascicoloErr.class, id);
        VrsSesFascicoloKo sessFasKo = null;
        OrgAmbiente amb = null;
        if (idAmbiente != null) {
            amb = fascicoliHelper.findById(OrgAmbiente.class, idAmbiente);
        }
        OrgEnte ente = null;
        if (idEnte != null) {
            ente = fascicoliHelper.findById(OrgEnte.class, idEnte);
        }
        OrgStrut str = null;
        if (idStrut != null) {
            str = fascicoliHelper.findById(OrgStrut.class, idStrut);
        }
        DecTipoFascicolo tip = null;
        if (idTipoFascicolo != null) {
            tip = fascicoliHelper.findById(DecTipoFascicolo.class, idTipoFascicolo);
        }
        sess.setNmAmbiente(amb != null ? amb.getNmAmbiente() : null);
        sess.setNmEnte(ente != null ? ente.getNmEnte() : null);
        sess.setNmStrut(str != null ? str.getNmStrut() : null);
        sess.setOrgStrut(str != null ? str : null);
        sess.setDecTipoFascicolo(tip != null ? tip : null);
        sess.setNmTipoFascicolo(tip != null ? tip.getNmTipoFascicolo() : null);
        sess.setAaFascicolo(annoFascicolo);
        sess.setCdKeyFascicolo(numFascicolo);
        // SE TUTTI I CONTROLLI HANNO SUCCESSO SI TRASFORMA UNA SESSIONE ERRATA IN VERSAMENTO FALLITO
        // Punto 1 dell'analisi
        if (amb != null && ente != null && str != null && tip != null && annoFascicolo != null
                && (numFascicolo != null || (!numFascicolo.equals("")))) {
            // Controllo partizionamento
            List<OrgVChkPartitionFascByAa> lp = fascicoliHelper.retrieveOrgVChkPartitionFascByAaByStrutAnno(idStrut,
                    annoFascicolo);
            if (lp != null && lp.size() > 0 && lp.iterator().next().getFlPartFascOk().equals("1")) {
                // Partizionamento ok, FLUSSO ALTERNATIVO DI TRASFORMAZIONE SESSIONE
                List<FasFascicolo> fascicoli = fascicoliHelper.retrieveFasFascicoloByStrutAnnoNumValid(str,
                        annoFascicolo.longValueExact(), numFascicolo);
                // SE IL FASCICOLO ESISTE
                // Punto 2 dell'analisi
                if (fascicoli != null && fascicoli.size() > 0) {
                    FasFascicolo fs = fascicoli.get(0);
                    fs.setFlSesFascicoloKo("1");
                    sessFasKo = new VrsSesFascicoloKo();
                    fascicoliHelper.getEntityManager().persist(sessFasKo);
                    sessFasKo.setFasFascicolo(fs);
                    sessFasKo.setDecTipoFascicolo(tip);
                    sessFasKo.setTsIniSes(new Timestamp(sess.getTsIniSes().getTime()));
                    sessFasKo.setTsFineSes(new Timestamp(sess.getTsFineSes().getTime()));
                    sessFasKo.setCdVersioneWs(sess.getCdVersioneWs());
                    List<IamUser> lUsr = null;
                    if (sess.getNmUseridWs() != null) {
                        lUsr = userHelper.findIamUserList(sess.getNmUseridWs());
                        if (lUsr != null && lUsr.size() > 0) {
                            sessFasKo.setIamUser(lUsr.get(0));
                        }
                    }
                    sessFasKo.setDecErrSacer(sess.getDecErrSacer());
                    sessFasKo.setDsErrPrinc(sess.getDecErrSacer().getDsErr());
                    sessFasKo.setTiStatoSes(statoSessioneFascicoloKo.RISOLTO.name());
                    sessFasKo.setOrgStrut(sess.getOrgStrut());
                    sessFasKo.setAaFascicolo(annoFascicolo);
                    sessFasKo.setCdIndIpClient(sess.getCdIndIpClient());
                    sessFasKo.setCdIndServer(sess.getCdIndServer());
                    // SE IL FASCICOLO NON ESISTE
                    // Punto 3 dell'analisi
                } else {
                    List<VrsFascicoloKo> fasKo = fascicoliHelper.retrieveFasNonVersatoByStrutAnnoNum(str,
                            annoFascicolo.longValueExact(), numFascicolo, true); // RICHIEDE LOCK PESSIMISTICO
                    // SE ESISTE UN FASCICOLO NON VERSATO (punto b dell'analisi)
                    if (fasKo != null && fasKo.size() > 0) {
                        VrsFascicoloKo fko = fasKo.get(0);
                        sessFasKo = new VrsSesFascicoloKo();
                        sessFasKo.setVrsFascicoloKo(fko);
                        sessFasKo.setDecTipoFascicolo(tip);
                        sessFasKo.setTsIniSes(new Timestamp(sess.getTsIniSes().getTime()));
                        sessFasKo.setTsFineSes(new Timestamp(sess.getTsFineSes().getTime()));
                        sessFasKo.setCdVersioneWs(sess.getCdVersioneWs());
                        List<IamUser> lUsr = null;
                        if (sess.getNmUseridWs() != null) {
                            lUsr = userHelper.findIamUserList(sess.getNmUseridWs());
                            if (lUsr != null && lUsr.size() > 0) {
                                sessFasKo.setIamUser(lUsr.get(0));
                            }
                        }
                        sessFasKo.setDecErrSacer(sess.getDecErrSacer());
                        sessFasKo.setDsErrPrinc(sess.getDecErrSacer().getDsErr());
                        sessFasKo.setTiStatoSes(sess.getTiStatoSes());
                        sessFasKo.setOrgStrut(sess.getOrgStrut());
                        sessFasKo.setAaFascicolo(annoFascicolo);
                        sessFasKo.setCdIndIpClient(sess.getCdIndIpClient());
                        sessFasKo.setCdIndServer(sess.getCdIndServer());
                        fascicoliHelper.getEntityManager().persist(sessFasKo);
                        // Aggiorna contatore
                        // Punto b) III
                        List<MonContaFascicoliKo> cntKo = fascicoliHelper.retrieveMonContaFascicoliKoByChiaveTotaliz(
                                fko.getTsIniLastSes(), str, fko.getTiStatoFascicoloKo(),
                                fko.getAaFascicolo().longValueExact(), fko.getDecTipoFascicolo(), true); // ASSSUME IL
                                                                                                         // LOCK
                                                                                                         // PESSIMISTICO
                        if (cntKo != null && cntKo.size() > 0) {
                            MonContaFascicoliKo monCnt = cntKo.get(0);
                            // Punto b) IV
                            monCnt.setNiFascicoliKo(monCnt.getNiFascicoliKo().subtract(BigDecimal.ONE));
                        }
                        // Punto b) V
                        List<VrsVUpdFascicoloKo> listaVrs = fascicoliHelper
                                .retrieveVrsVUpdFascicoloKoByFascKo(fko.getIdFascicoloKo());
                        if (listaVrs != null && listaVrs.size() > 0) {
                            VrsVUpdFascicoloKo vrsUpd = listaVrs.get(0);
                            fko.setTsIniFirstSes(vrsUpd.getTsIniFirstSes());
                            fko.setTsIniLastSes(vrsUpd.getTsIniLastSes());
                            fko.setIdSesFascicoloKoFirst(vrsUpd.getIdSesFascicoloKoFirst());
                            fko.setIdSesFascicoloKoLast(vrsUpd.getIdSesFascicoloKoLast());
                            fko.setDecTipoFascicolo(
                                    fascicoliHelper.findById(DecTipoFascicolo.class, vrsUpd.getIdTipoFascicoloLast()));
                            fko.setTiStatoFascicoloKo(vrsUpd.getTiStatoFascicoloKo());
                            BigDecimal idErrPrinc = vrsUpd.getIdErrSacerPrinc();
                            if (idErrPrinc != null) {
                                DecErrSacer errSac = fascicoliHelper.findById(DecErrSacer.class,
                                        vrsUpd.getIdErrSacerPrinc());
                                fko.setDecErrSacer(errSac);
                                fko.setDsErrPrinc(errSac.getDsErr());
                            }
                        }
                        // Punto b) VI
                        List<LogVVisLastSched> logVs = monitoraggioHelper
                                .getLogVVisLastSched(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name());
                        if (logVs != null && logVs.size() > 0
                                && fko.getTsIniLastSes().before(logVs.get(0).getDtRegLogJobIni())) {
                            // SE DATA MINORE...
                            List<MonContaFascicoliKo> cntKo2 = fascicoliHelper
                                    .retrieveMonContaFascicoliKoByChiaveTotaliz(fko.getTsIniLastSes(), str,
                                            fko.getTiStatoFascicoloKo(), fko.getAaFascicolo().longValueExact(),
                                            fko.getDecTipoFascicolo(), true); // ASSSUME IL LOCK PESSIMISTICO
                            if (cntKo2 != null && cntKo2.size() > 0) {
                                MonContaFascicoliKo mcfk = cntKo2.get(0);
                                mcfk.setNiFascicoliKo(mcfk.getNiFascicoliKo().add(BigDecimal.ONE));
                            } else {
                                MonContaFascicoliKo mcfk = new MonContaFascicoliKo();
                                mcfk.setAaFascicolo(fko.getAaFascicolo());
                                mcfk.setDecTipoFascicolo(fko.getDecTipoFascicolo());
                                mcfk.setDtRifConta(fko.getTsIniLastSes());
                                mcfk.setOrgStrut(str);
                                mcfk.setTiStatoFascicoloKo(fko.getTiStatoFascicoloKo());
                                mcfk.setNiFascicoliKo(BigDecimal.ONE);
                                fascicoliHelper.getEntityManager().persist(mcfk);
                            }
                        }
                    } else {
                        // SE NON ESISTE UN FASCICOLO NON VERSATO, Registra un fascicolo non versato
                        // punto C)
                        VrsFascicoloKo vrsFasKo = new VrsFascicoloKo();
                        vrsFasKo.setOrgStrut(sess.getOrgStrut());
                        vrsFasKo.setAaFascicolo(sess.getAaFascicolo());
                        vrsFasKo.setCdKeyFascicolo(sess.getCdKeyFascicolo());
                        vrsFasKo.setTsIniFirstSes(new Timestamp(sess.getTsIniSes().getTime()));
                        vrsFasKo.setIdSesFascicoloKoFirst(new BigDecimal(sess.getIdSesFascicoloErr()));
                        vrsFasKo.setTsIniLastSes(new Timestamp(sess.getTsIniSes().getTime()));
                        vrsFasKo.setIdSesFascicoloKoLast(new BigDecimal(sess.getIdSesFascicoloErr()));
                        vrsFasKo.setDecErrSacer(sess.getDecErrSacer());
                        vrsFasKo.setDsErrPrinc(sess.getDsErr());
                        vrsFasKo.setDecTipoFascicolo(sess.getDecTipoFascicolo());
                        vrsFasKo.setTiStatoFascicoloKo(sess.getTiStatoSes());
                        fascicoliHelper.getEntityManager().persist(vrsFasKo);
                        // Registra un fascicolo non versato
                        // Punto c) II
                        sessFasKo = new VrsSesFascicoloKo();
                        sessFasKo.setVrsFascicoloKo(vrsFasKo);
                        sessFasKo.setDecTipoFascicolo(sess.getDecTipoFascicolo());
                        sessFasKo.setTsIniSes(new Timestamp(sess.getTsIniSes().getTime()));
                        sessFasKo.setTsFineSes(new Timestamp(sess.getTsFineSes().getTime()));
                        String cdVersWs = sess.getCdVersioneWs();
                        // Se la versione non è valorizzata imposta "N/A"
                        if (cdVersWs != null && !cdVersWs.trim().equals("")) {
                            sessFasKo.setCdVersioneWs(cdVersWs);
                        } else {
                            sessFasKo.setCdVersioneWs(DATO_NON_DISPONIBILE);
                        }
                        List<IamUser> lUsr = null;
                        if (sess.getNmUseridWs() != null) {
                            lUsr = userHelper.findIamUserList(sess.getNmUseridWs());
                            if (lUsr != null && lUsr.size() > 0) {
                                sessFasKo.setIamUser(lUsr.get(0));
                            }
                        }
                        sessFasKo.setDecErrSacer(sess.getDecErrSacer());
                        sessFasKo.setDsErrPrinc(sess.getDsErr());
                        sessFasKo.setTiStatoSes(sess.getTiStatoSes());
                        sessFasKo.setOrgStrut(sess.getOrgStrut());
                        sessFasKo.setAaFascicolo(sess.getAaFascicolo());
                        sessFasKo.setCdIndIpClient(sess.getCdIndIpClient());
                        sessFasKo.setCdIndServer(sess.getCdIndServer());
                        fascicoliHelper.getEntityManager().persist(sessFasKo);
                        // Aggiorna i contatori
                        // Punto c) III
                        List<MonContaFascicoliKo> cntKo3 = fascicoliHelper.retrieveMonContaFascicoliKoByChiaveTotaliz(
                                vrsFasKo.getTsIniLastSes(), vrsFasKo.getOrgStrut(), vrsFasKo.getTiStatoFascicoloKo(),
                                vrsFasKo.getAaFascicolo().longValueExact(), vrsFasKo.getDecTipoFascicolo(), true); // ASSSUME
                                                                                                                   // IL
                                                                                                                   // LOCK
                                                                                                                   // PESSIMISTICO
                        if (cntKo3 != null && cntKo3.size() > 0) {
                            MonContaFascicoliKo ogg = cntKo3.get(0);
                            ogg.setNiFascicoliKo(ogg.getNiFascicoliKo().add(BigDecimal.ONE));
                        } else {
                            MonContaFascicoliKo mcfk = new MonContaFascicoliKo();
                            mcfk.setAaFascicolo(vrsFasKo.getAaFascicolo());
                            mcfk.setDecTipoFascicolo(vrsFasKo.getDecTipoFascicolo());
                            mcfk.setDtRifConta(vrsFasKo.getTsIniLastSes());
                            mcfk.setOrgStrut(vrsFasKo.getOrgStrut());
                            mcfk.setTiStatoFascicoloKo(vrsFasKo.getTiStatoFascicoloKo());
                            mcfk.setNiFascicoliKo(BigDecimal.ONE);
                            fascicoliHelper.getEntityManager().persist(mcfk);
                        }
                    }
                }
                // Registrazione degli XML di richiesta e risposta
                // Punto 4 dell'analisi
                List<VrsXmlSesFascicoloErr> xmls = sess.getVrsXmlSesFascicoloErrs();
                if (xmls != null) {
                    for (Iterator<VrsXmlSesFascicoloErr> iterator = xmls.iterator(); iterator.hasNext();) {
                        VrsXmlSesFascicoloErr xmlErr = iterator.next();
                        VrsXmlSesFascicoloKo xmlKo = new VrsXmlSesFascicoloKo();
                        xmlKo.setVrsSesFascicoloKo(sessFasKo);
                        xmlKo.setTiXml(xmlErr.getTiXml());
                        xmlKo.setCdVersioneXml(xmlErr.getCdVersioneXml());
                        xmlKo.setBlXml(xmlErr.getBlXml());
                        // xmlKo.setDsHashXml(xmlErr.getDsHashXml());
                        // xmlKo.setDsAlgoHashXml(xmlErr.getDsAlgoHashXml());
                        // xmlKo.setDsUrnXml(xmlErr.getDsUrnXml());
                        xmlKo.setIdStrut(new BigDecimal(sess.getOrgStrut().getIdStrut()));
                        xmlKo.setDtRegXmlSesKo(new Date());
                        // xmlKo.setCdEncodingHashXml(xmlErr.getCdEncodingHashXml());
                        fascicoliHelper.getEntityManager().persist(xmlKo);
                    }
                }
                // Punto nuovo dell'analisi: 4.5
                // Copia l'unico errore della sessione errata nella sessione KO
                if (sessFasKo != null) {
                    VrsErrSesFascicoloKo errSess = new VrsErrSesFascicoloKo();
                    errSess.setVrsSesFascicoloKo(sessFasKo);
                    errSess.setPgErr(BigDecimal.ONE);
                    errSess.setFlErrPrinc("1");
                    errSess.setTiErr(sess.getDecErrSacer().getTiErrSacer());
                    errSess.setDecErrSacer(sess.getDecErrSacer());
                    errSess.setDsErr(sess.getDsErr());
                    fascicoliHelper.getEntityManager().persist(errSess);
                }
                // Punto 5 dell'analisi
                // Elimina la sessiojne errata
                fascicoliHelper.removeEntity(sess, false);
                ret.setSessioneCancellata(true);
            } // Fine controllo partizionamento
        } // FINE delle verifiche iniziali
        return ret;
    }

    public class SalvaDettaSessErrDto {

        private String msg;
        private boolean sessioneCancellata;

        public SalvaDettaSessErrDto() {
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public boolean isSessioneCancellata() {
            return sessioneCancellata;
        }

        public void setSessioneCancellata(boolean sessioneCancellata) {
            this.sessioneCancellata = sessioneCancellata;
        }
    }

    public class DettaglioVersamentoFascicoloKo {

        private BaseRow dettaglioVersamentoRB;
        private BaseTable listaErroriTB;

        public DettaglioVersamentoFascicoloKo() {
        }

        public BaseRow getDettaglioVersamentoRB() {
            return dettaglioVersamentoRB;
        }

        public void setDettaglioVersamentoRB(BaseRow dettaglioVersamentoRB) {
            this.dettaglioVersamentoRB = dettaglioVersamentoRB;
        }

        public BaseTable getListaErroriTB() {
            return listaErroriTB;
        }

        public void setListaErroriTB(BaseTable listaErroriTB) {
            this.listaErroriTB = listaErroriTB;
        }

    }

    public BaseTable ricercaCriteriRaggrFascicoli(
            CriteriRaggrFascicoliForm.FiltriCriteriRaggrFascicoli filtriCriteriRaggrFasc, boolean filterValid)
            throws EMFError {
        BaseTable t = new BaseTable();
        BaseRow r = null;

        List<DecCriterioRaggrFasc> l = fascicoliHelper.retrieveCriteriRaggrFascicoli(filtriCriteriRaggrFasc,
                filterValid);

        if (l != null && !l.isEmpty()) {
            for (Iterator<DecCriterioRaggrFasc> iterator = l.iterator(); iterator.hasNext();) {
                DecCriterioRaggrFasc ogg = iterator.next();
                r = new BaseRow();
                r.setBigDecimal("id_criterio_raggr_fasc", new BigDecimal(ogg.getIdCriterioRaggrFasc()));
                r.setString("nm_ente_nm_strut",
                        ogg.getOrgStrut().getOrgEnte().getNmEnte() + " - " + ogg.getOrgStrut().getNmStrut());
                r.setString("nm_criterio_raggr", ogg.getNmCriterioRaggr());
                r.setString("fl_criterio_raggr_standard", ogg.getFlCriterioRaggrStandard());
                r.setString("nm_tipo_fascicolo",
                        populateValueField(new BigDecimal(ogg.getIdCriterioRaggrFasc()), "TIPO_FASCICOLO"));
                String aaFascicolo = null;
                if (ogg.getAaFascicolo() != null) {
                    aaFascicolo = "" + ogg.getAaFascicolo();
                } else if (ogg.getAaFascicoloDa() != null) {
                    aaFascicolo = ogg.getAaFascicoloDa() + " - " + ogg.getAaFascicoloA();
                }
                r.setString("aa_fascicolo", aaFascicolo);
                r.setString("cd_composito_voce_titol",
                        populateValueField(new BigDecimal(ogg.getIdCriterioRaggrFasc()), "VOCE_TITOL"));
                r.setBigDecimal("ni_max_fasc", ogg.getNiMaxFasc());
                r.setString("ti_scad_chius", ogg.getTiScadChius());
                if (ogg.getDtIstituz().before(new Date()) && ogg.getDtSoppres().after(new Date())) {
                    r.setString("fl_attivo", "1");
                } else {
                    r.setString("fl_attivo", "0");
                }
                r.setTimestamp("dt_istituz", new Timestamp(ogg.getDtIstituz().getTime()));
                r.setTimestamp("dt_soppres", new Timestamp(ogg.getDtSoppres().getTime()));
                t.add(r);
            }
        }

        return t;
    }

    public BaseTable ricercaSelCriterioRaggrFascicoli(BigDecimal idCriterioRaggrFasc, String tiSel) {
        BaseTable t = new BaseTable();
        BaseRow r = null;

        List<DecSelCriterioRaggrFasc> l = fascicoliHelper.retrieveSelCriterioRaggrFascicoli(idCriterioRaggrFasc, tiSel);

        if (l != null && !l.isEmpty()) {
            for (Iterator<DecSelCriterioRaggrFasc> iterator = l.iterator(); iterator.hasNext();) {
                DecSelCriterioRaggrFasc ogg = iterator.next();
                r = new BaseRow();
                r.setBigDecimal("id_sel_criterio_raggr_fasc", new BigDecimal(ogg.getIdSelCriterioRaggrFasc()));
                r.setBigDecimal("id_criterio_raggr_fasc",
                        new BigDecimal(ogg.getDecCriterioRaggrFasc().getIdCriterioRaggrFasc()));
                r.setString("ti_sel", ogg.getTiSel());
                if (ogg.getAplSistemaMigraz() != null) {
                    r.setBigDecimal("id_sistema_migraz",
                            new BigDecimal(ogg.getAplSistemaMigraz().getIdSistemaMigraz()));
                }
                if (ogg.getDecTipoFascicolo() != null) {
                    r.setBigDecimal("id_tipo_fascicolo",
                            new BigDecimal(ogg.getDecTipoFascicolo().getIdTipoFascicolo()));
                }
                if (ogg.getDecVoceTitol() != null) {
                    r.setBigDecimal("id_voce_titol", new BigDecimal(ogg.getDecVoceTitol().getIdVoceTitol()));
                }
                // if (ogg.getDecVoceTitol().getDecTitol() != null)
                // r.setBigDecimal("id_titol", new BigDecimal(ogg.getDecVoceTitol().getDecTitol().getIdTitol()));
                t.add(r);
            }
        }

        return t;
    }

    public BaseRow getDettaglioCriterioRaggrFascicolo(BigDecimal idCriterioRaggrFasc) {
        BaseRow r = null;

        DecCriterioRaggrFasc decCriterioRaggrFasc = fascicoliHelper.getDecCriterioRaggrFascById(idCriterioRaggrFasc);

        if (decCriterioRaggrFasc != null) {
            r = new BaseRow();
            r.setBigDecimal("id_criterio_raggr_fasc", new BigDecimal(decCriterioRaggrFasc.getIdCriterioRaggrFasc()));
            r.setBigDecimal("id_strut", new BigDecimal(decCriterioRaggrFasc.getOrgStrut().getIdStrut()));
            r.setString("nm_criterio_raggr", decCriterioRaggrFasc.getNmCriterioRaggr());
            r.setString("ds_criterio_raggr", decCriterioRaggrFasc.getDsCriterioRaggr());
            r.setString("fl_criterio_raggr_standard", decCriterioRaggrFasc.getFlCriterioRaggrStandard());
            r.setBigDecimal("ni_max_fasc", decCriterioRaggrFasc.getNiMaxFasc());
            r.setString("ti_scad_chius", decCriterioRaggrFasc.getTiScadChius());
            r.setBigDecimal("ni_tempo_scad_chius", decCriterioRaggrFasc.getNiTempoScadChius());
            r.setString("ti_scad_chius", decCriterioRaggrFasc.getTiScadChius());
            r.setString("ti_tempo_scad_chius", decCriterioRaggrFasc.getTiTempoScadChius());
            r.setTimestamp("dt_istituz", new Timestamp(decCriterioRaggrFasc.getDtIstituz().getTime()));
            r.setTimestamp("dt_soppres", new Timestamp(decCriterioRaggrFasc.getDtSoppres().getTime()));
            r.setString("fl_filtro_tipo_fascicolo", decCriterioRaggrFasc.getFlFiltroTipoFascicolo());
            r.setString("fl_filtro_voce_titol", decCriterioRaggrFasc.getFlFiltroVoceTitol());
            r.setString("fl_filtro_sistema_migraz", decCriterioRaggrFasc.getFlFiltroSistemaMigraz());
            r.setString("ti_conservazione", decCriterioRaggrFasc.getTiConservazione());
            r.setString("nt_criterio_raggr", decCriterioRaggrFasc.getNtCriterioRaggr());
            r.setBigDecimal("aa_fascicolo", decCriterioRaggrFasc.getAaFascicolo());
            r.setBigDecimal("aa_fascicolo_da", decCriterioRaggrFasc.getAaFascicoloDa());
            r.setBigDecimal("aa_fascicolo_a", decCriterioRaggrFasc.getAaFascicoloA());
        }

        return r;
    }

    public BaseRow getDecCriterioRaggrFascRowBean(BigDecimal idStrut, String nmCriterioRaggr) {
        BaseRow row = null;

        DecCriterioRaggrFasc crit = fascicoliHelper.getDecCriterioRaggrFasc(idStrut, nmCriterioRaggr);

        if (crit != null) {
            row = new BaseRow();
            row.setBigDecimal("id_criterio_raggr_fasc", new BigDecimal(crit.getIdCriterioRaggrFasc()));
            row.setBigDecimal("id_strut", new BigDecimal(crit.getOrgStrut().getIdStrut()));
            row.setString("nm_criterio_raggr", crit.getNmCriterioRaggr());
            row.setString("ds_criterio_raggr", crit.getDsCriterioRaggr());
            row.setString("fl_criterio_raggr_standard", crit.getFlCriterioRaggrStandard());
            row.setBigDecimal("ni_max_fasc", crit.getNiMaxFasc());
            row.setString("ti_scad_chius", crit.getTiScadChius());
            row.setBigDecimal("ni_tempo_scad_chius", crit.getNiTempoScadChius());
            row.setString("ti_scad_chius", crit.getTiScadChius());
            row.setString("ti_tempo_scad_chius", crit.getTiTempoScadChius());
            row.setTimestamp("dt_istituz", new Timestamp(crit.getDtIstituz().getTime()));
            row.setTimestamp("dt_soppres", new Timestamp(crit.getDtSoppres().getTime()));
            row.setString("fl_filtro_tipo_fascicolo", crit.getFlFiltroTipoFascicolo());
            row.setString("fl_filtro_voce_titol", crit.getFlFiltroVoceTitol());
            row.setString("fl_filtro_sistema_migraz", crit.getFlFiltroSistemaMigraz());
            row.setString("ti_conservazione", crit.getTiConservazione());
            row.setString("nt_criterio_raggr", crit.getNtCriterioRaggr());
            row.setBigDecimal("aa_fascicolo", crit.getAaFascicolo());
            row.setBigDecimal("aa_fascicolo_da", crit.getAaFascicoloDa());
            row.setBigDecimal("aa_fascicolo_a", crit.getAaFascicoloA());
        }

        return row;
    }

    public long getNumFascCriterioStd(BigDecimal idStrut) {
        OrgStrut strut = fascicoliHelper.findById(OrgStrut.class, idStrut);
        String numFascCriterioStd = configurationHelper.getValoreParamApplic("NUM_FASC_CRITERIO_STD",
                BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()), idStrut, null, null,
                TipoAplVGetValAppart.STRUT);
        return new Long(numFascCriterioStd);
    }

    public long getNumGgScadCriterioFascStd(BigDecimal idStrut) {
        OrgStrut strut = fascicoliHelper.findById(OrgStrut.class, idStrut);
        String numGgScadCriterioFascStd = configurationHelper.getValoreParamApplic("NUM_GG_SCAD_CRITERIO_FASC_STD",
                BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()), idStrut, null, null,
                TipoAplVGetValAppart.STRUT);
        return new Long(numGgScadCriterioFascStd);
    }

    public long saveCriterioRaggrFascicoli(LogParam param, CreaCriterioRaggrFascicoli filtri,
            Object[] anniFascicoliValidati, BigDecimal idStruttura, String nome, String criterioStandard,
            List<BigDecimal> voceTitolList) throws EMFError, ParerUserError {
        return fascicoliHelper.saveCritRaggrFasc(param, filtri, anniFascicoliValidati, filtri.getId_strut().parse(),
                nome, criterioStandard, voceTitolList);
    }

    public boolean deleteDecCriterioRaggrFascicoli(LogParam param, BigDecimal idStrut, String nmCriterioRaggr)
            throws ParerUserError {
        return fascicoliHelper.deleteCritRaggrFasc(param, idStrut, nmCriterioRaggr);
    }

    public boolean existNomeCriterio(String nome, BigDecimal idStruttura) {
        return fascicoliHelper.existNomeCriterio(nome, idStruttura);
    }

    public boolean existElvElencoVersPerCriterioRaggrFascicoli(BigDecimal idCriterioRaggrFasc) {
        return fascicoliHelper.existElvElencoVersPerCriterioRaggrFasc(idCriterioRaggrFasc);
    }

    public boolean isCriterioRaggrFascStandard(BigDecimal idStrut, String nmCriterioRaggr, BigDecimal aaFascicolo,
            BigDecimal aaFascicoloDa, BigDecimal aaFascicoloA, BigDecimal niTempoScadChius, String tiTempoScadChius,
            Set<String> tipiFascicolo, List<BigDecimal> voceTitolList, BigDecimal niMaxFasc) throws EMFError {
        int numeroCondizioniSoddisfatte = 0;
        /*
         * ATTENZIONE: IL NUMERO DI CONDIZIONI NECESSARIE DIPENDE DA QUANTE CONDIZIONI DEVONO ESSERE SODDISFATTE
         * ALL'INTERNO DEL METODO
         */
        int numeroCondizioniNecessarie = 4;

        // 1) Controllo che sia presente un solo filtro per tipiFascicolo O un solo indice di classificazione O sia un
        // tipo fascicolo che un indice di classificazione
        if (tipiFascicolo.size() == 1 && voceTitolList.size() == 1
                || tipiFascicolo.size() == 1 && voceTitolList.isEmpty()
                || tipiFascicolo.isEmpty() && voceTitolList.size() == 1) {
            numeroCondizioniSoddisfatte++;
        }

        // 2) Controllo che NON sia presente il range di anni o il singolo anno
        if (aaFascicolo == null && aaFascicoloDa == null && aaFascicoloA == null) {
            numeroCondizioniSoddisfatte++;
        }

        // 3) Controlla che la scadenza chiusura sia impostata a un numero di giorni pari a quelli indicati nel
        // parametro NI_GG_SCAD_CRITERIO in ORG_STRUT_CONFIG_FASCICOLO
        if (niTempoScadChius != null
                && niTempoScadChius.compareTo(new BigDecimal(getNumGgScadCriterioFascStd(idStrut))) == 0
                && tiTempoScadChius != null && tiTempoScadChius.equals("GIORNI")) {
            numeroCondizioniSoddisfatte++;
        }

        // 4) Controlla che il numero massimo di fascicoli sia pari al valore del parametro NUM_FASC_CRITERIO_STD
        if (niMaxFasc != null && niMaxFasc.compareTo(new BigDecimal(getNumFascCriterioStd(idStrut))) == 0) {
            numeroCondizioniSoddisfatte++;
        }

        return numeroCondizioniSoddisfatte == numeroCondizioniNecessarie;
    }

    private String populateValueField(BigDecimal idCriterioRaggrFasc, String tiSel) {
        String val = "";

        List<DecSelCriterioRaggrFasc> l = fascicoliHelper.retrieveSelCriterioRaggrFascicoli(idCriterioRaggrFasc, tiSel);

        if (l != null && !l.isEmpty()) {
            if (l.size() > 1) {
                val = "Diversi";
            } else if ("TIPO_FASCICOLO".equals(tiSel)) {
                val = l.get(0).getDecTipoFascicolo().getNmTipoFascicolo();
            } else if ("VOCE_TITOL".equals(tiSel)) {
                val = l.get(0).getDecVoceTitol().getCdCompositoVoceTitol();
            }
        }

        return val;
    }

    public DecTipoFascicoloTableBean getTipiFascicoloAbilitati(long idUtente, BigDecimal idStruttura) {
        DecTipoFascicoloTableBean table = new DecTipoFascicoloTableBean();
        List<DecTipoFascicolo> list = fascicoliHelper.getTipiFascicoloAbilitati(idUtente, idStruttura);
        if (list != null && !list.isEmpty()) {
            try {
                table = (DecTipoFascicoloTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista fascicoli abilitati "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
            }
        }
        return table;
    }

    public DecCriterioRaggrFascRowBean getDecCriterioRaggrFascRowBean(BigDecimal idCriterioRaggrFasc,
            BigDecimal idStrut) {
        DecCriterioRaggrFascRowBean criterioRaggrFascRowBean = getDecCriterioRaggrFasc(idCriterioRaggrFasc, null,
                idStrut);
        return criterioRaggrFascRowBean;
    }

    private DecCriterioRaggrFascRowBean getDecCriterioRaggrFasc(BigDecimal idCriterioRaggrFasc, String nmCriterioRaggr,
            BigDecimal idStrut) {

        DecCriterioRaggrFascRowBean criterioRaggrDocRowBean = null;
        DecCriterioRaggrFasc criterioRaggrFasc = null;

        if (idCriterioRaggrFasc == BigDecimal.ZERO && nmCriterioRaggr != null) {
            criterioRaggrFasc = fascicoliHelper.getDecCriterioRaggrFasc(idStrut, nmCriterioRaggr);
        }
        if (nmCriterioRaggr == null && idCriterioRaggrFasc != BigDecimal.ZERO) {
            criterioRaggrFasc = fascicoliHelper.findById(DecCriterioRaggrFasc.class, idCriterioRaggrFasc);
        }

        if (criterioRaggrFasc != null) {
            try {
                criterioRaggrDocRowBean = (DecCriterioRaggrFascRowBean) Transform.entity2RowBean(criterioRaggrFasc);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return criterioRaggrDocRowBean;
    }
}
