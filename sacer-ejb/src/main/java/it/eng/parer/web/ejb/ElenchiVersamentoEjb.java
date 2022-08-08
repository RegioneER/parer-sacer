package it.eng.parer.web.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.crypto.model.ParerTST;
import it.eng.parer.elencoVersamento.ejb.IndiceElencoVersXsdEjb;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.FileTypeEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecTiEveStatoElencoVers;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.ElvUrnElencoVers;
import it.eng.parer.entity.ElvStatoElencoVer;
import it.eng.parer.entity.HsmElencoSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco;
import it.eng.parer.entity.constraint.ElvUpdUdDaElabElenco.ElvUpdUdDaElabTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvUrnElencoVers.TiUrnElenco;
import it.eng.parer.entity.constraint.ElvUrnFileElencoVers;
import it.eng.parer.entity.constraint.HsmElencoSessioneFirma.TiEsitoFirmaElenco;
import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.exception.ParerWarningException;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.job.indiceAip.elenchi.ElaborazioneElencoIndiceAip;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm.FiltriElenchiVersamento;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrRowBean;
import it.eng.parer.slite.gen.tablebean.ElvElencoVerRowBean;
import it.eng.parer.slite.gen.tablebean.ElvStatoElencoVerRowBean;
import it.eng.parer.slite.gen.tablebean.ElvStatoElencoVerTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisElencoVersStatoRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisElencoVersStatoTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersByStatoRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersByStatoTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersByUdRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersByUdTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoVersTableBean;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.ElvVChkSoloUdAnnul;
import it.eng.parer.viewEntity.ElvVChkUnaUdAnnul;
import it.eng.parer.viewEntity.ElvVLisAllUdByElenco;
import it.eng.parer.viewEntity.ElvVLisElencoDaMarcare;
import it.eng.parer.viewEntity.ElvVLisElencoVersStato;
import it.eng.parer.viewEntity.ElvVRicElencoVers;
import it.eng.parer.viewEntity.ElvVRicElencoVersByStato;
import it.eng.parer.viewEntity.ElvVRicElencoVersByUd;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.ElenchiVersamentoHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.CostantiDB.TipoAplVGetValAppart;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class ElenchiVersamentoEjb {

    private static final Logger log = LoggerFactory.getLogger(ElenchiVersamentoEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private ElenchiVersamentoEjb evWebEjb;
    @EJB
    private ElenchiVersamentoHelper evWebHelper;
    @EJB
    private IndiceElencoVersXsdEjb iejEjb;
    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private ElaborazioneElencoIndiceAip elabElencoIndiceAipEjb;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private CryptoInvoker cryptoInvoker;
    @EJB
    private UserHelper userHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;

    public ElvElencoVerRowBean getElvElencoVersRowBean(BigDecimal idElencoVers) {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers.longValue());
        DecCriterioRaggr criterio = evWebHelper.findById(DecCriterioRaggr.class,
                elenco.getDecCriterioRaggr().getIdCriterioRaggr());
        List<ElvFileElencoVer> elencoIndice = evHelper.retrieveFileIndiceElenco(idElencoVers.longValue(),
                new String[] { ElencoEnums.FileTypeEnum.INDICE.name() });
        List<ElvFileElencoVer> elencoIndiceAip = evHelper.retrieveFileIndiceElenco(idElencoVers.longValue(),
                new String[] { ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name() });
        List<ElvUrnElencoVers> elencoUrn = evHelper.retrieveUrnElencoVersList(idElencoVers.longValue());
        ElvElencoVerRowBean elencoRowBean = new ElvElencoVerRowBean();
        try {
            elencoRowBean = (ElvElencoVerRowBean) Transform.entity2RowBean(elenco);
            final OrgAmbiente orgAmbiente = elenco.getOrgStrut().getOrgEnte().getOrgAmbiente();
            elencoRowBean.setString("nm_criterio_raggr", elenco.getDecCriterioRaggr().getNmCriterioRaggr());
            elencoRowBean.setString("ds_criterio_raggr", elenco.getDecCriterioRaggr().getDsCriterioRaggr());
            elencoRowBean.setString("ti_gest_elenco_criterio", elenco.getDecCriterioRaggr().getTiGestElencoCriterio());
            boolean elencoStandard = elenco.getFlElencoStandard().equals("1");
            boolean elencoFiscale = elenco.getFlElencoFisc().equals("1");
            String tiGestioneAmb;
            if (!elencoStandard && !elencoFiscale) {
                tiGestioneAmb = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_NOSTD",
                        BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
                        BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()), null, null,
                        CostantiDB.TipoAplVGetValAppart.STRUT);
            } else if (elencoFiscale) {
                tiGestioneAmb = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_STD_FISC",
                        BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
                        BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()), null, null,
                        CostantiDB.TipoAplVGetValAppart.STRUT);
            } else {
                tiGestioneAmb = configurationHelper.getValoreParamApplic("TI_GEST_ELENCO_STD_NOFISC",
                        BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
                        BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()), null, null,
                        CostantiDB.TipoAplVGetValAppart.STRUT);
            }
            elencoRowBean.setString("ti_gest_elenco_amb", tiGestioneAmb);
            elencoRowBean.setBigDecimal("num_comp", elenco.getNiCompAggElenco().add(elenco.getNiCompVersElenco()));
            elencoRowBean.setBigDecimal("dim_bytes", elenco.getNiSizeVersElenco().add(elenco.getNiSizeAggElenco()));
            final String nmAmbiente = orgAmbiente.getNmAmbiente();
            final String nmEnte = elenco.getOrgStrut().getOrgEnte().getNmEnte();
            final String nmStrut = elenco.getOrgStrut().getNmStrut();
            elencoRowBean.setString("nm_ambiente", nmAmbiente);
            elencoRowBean.setString("nm_ente", nmEnte);
            elencoRowBean.setString("nm_strut", nmStrut);
            elencoRowBean.setString("amb_ente_strut", nmAmbiente + " / " + nmEnte + " / " + nmStrut);
            if (elencoIndiceAip != null && !elencoIndiceAip.isEmpty()) {
                elencoRowBean.setString("cd_versione_xsd", elencoIndiceAip.get(0).getCdVerXsdFile());
            } else {
                elencoRowBean.setString("cd_versione_xsd", null);
            }
            if (elenco.getTiValidElenco() == null || elenco.getTiModValidElenco() == null) {
                elencoRowBean.setString("ti_valid_elenco", elenco.getDecCriterioRaggr().getTiValidElenco().name());
                elencoRowBean.setString("ti_mod_valid_elenco",
                        elenco.getDecCriterioRaggr().getTiModValidElenco().name());
            }
            if (isElencoFirmato(elenco, criterio)) {
                elencoRowBean.setObject("fl_elenco_firmato", "1");
            } else {
                elencoRowBean.setObject("fl_elenco_firmato", "0");
            }
            if (elenco.getIamUserFirmaIndice() != null) {
                elencoRowBean.setString("nm_cognome_nome_user", elenco.getIamUserFirmaIndice().getNmCognomeUser() + " "
                        + elenco.getIamUserFirmaIndice().getNmNomeUser());
            }
            if (elencoIndice != null && !elencoIndice.isEmpty()) {
                elencoRowBean.setString("cd_versione_indice", elencoIndice.get(0).getCdVerXsdFile());
            } else {
                elencoRowBean.setString("cd_versione_indice", null);
            }
            // EVO#16486
            if (elencoUrn != null && !elencoUrn.isEmpty()) {
                // Recupero lo urn ORIGINALE
                ElvUrnElencoVers urnElencoVers = (ElvUrnElencoVers) CollectionUtils.find(elencoUrn, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ((ElvUrnElencoVers) object).getTiUrn().equals(TiUrnElenco.ORIGINALE);
                    }
                });
                if (urnElencoVers != null) {
                    elencoRowBean.setString("urn_originale", urnElencoVers.getDsUrn());
                }
                // Recupero lo urn NORMALIZZATO
                urnElencoVers = (ElvUrnElencoVers) CollectionUtils.find(elencoUrn, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ((ElvUrnElencoVers) object).getTiUrn().equals(TiUrnElenco.NORMALIZZATO);
                    }
                });
                if (urnElencoVers != null) {
                    elencoRowBean.setString("urn_normalizzato", urnElencoVers.getDsUrn());
                }
                // Recupero lo urn INIZIALE
                urnElencoVers = (ElvUrnElencoVers) CollectionUtils.find(elencoUrn, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ((ElvUrnElencoVers) object).getTiUrn().equals(TiUrnElenco.INIZIALE);
                    }
                });
                if (urnElencoVers != null) {
                    elencoRowBean.setString("urn_iniziale", urnElencoVers.getDsUrn());
                }
            }
        } catch (Exception ex) {
            log.error("Errore durante il recupero dell'elenco di versamento " + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
        }
        return elencoRowBean;
    }

    public DecCriterioRaggrRowBean getDecCriterioRaggrRowBean(BigDecimal idCriterioRaggr) throws EMFError {
        DecCriterioRaggr criterio = evWebHelper.findById(DecCriterioRaggr.class, idCriterioRaggr.longValue());
        DecCriterioRaggrRowBean criterioRowBean = new DecCriterioRaggrRowBean();
        try {
            criterioRowBean = (DecCriterioRaggrRowBean) Transform.entity2RowBean(criterio);
        } catch (Exception ex) {
            log.error("Errore durante il recupero del criterio di raggruppamento "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new EMFError("Errore durante il recupero del criterio di raggruppamento", ex);
        }
        return criterioRowBean;
    }

    public ElvVRicElencoVersTableBean getElvVRicElencoVersTableBean(long idUserIam, FiltriElenchiVersamento filtri)
            throws EMFError {
        List<ElvVRicElencoVers> listaElenchiVersamento = evWebHelper.retrieveElvVRicElencoVersList(idUserIam, filtri);
        ElvVRicElencoVersTableBean elenchiVersTableBean = new ElvVRicElencoVersTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVRicElencoVers elencoVers : listaElenchiVersamento) {
                    ElvVRicElencoVersRowBean elenchiVersRowBean = (ElvVRicElencoVersRowBean) Transform
                            .entity2RowBean(elencoVers);
                    elenchiVersRowBean.setString("amb_ente_strut", elenchiVersRowBean.getNmAmbiente() + " / "
                            + elenchiVersRowBean.getNmEnte() + " / " + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("num_comp",
                            elenchiVersRowBean.getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersRowBean.setBigDecimal("dimensione_byte",
                            elenchiVersRowBean.getNiSizeVersElenco().add(elenchiVersRowBean.getNiSizeAggElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento", e);
        }
        return elenchiVersTableBean;
    }

    public ElvVRicElencoVersByStatoTableBean getElvVRicElencoVersByStatoTableBean(long idUserIam,
            FiltriElenchiVersamento filtri) throws EMFError {
        List<ElvVRicElencoVersByStato> listaElenchiVersamento = evWebHelper
                .retrieveElvVRicElencoVersByStatoList(idUserIam, filtri);
        ElvVRicElencoVersByStatoTableBean elenchiVersTableBean = new ElvVRicElencoVersByStatoTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVRicElencoVersByStato elencoVers : listaElenchiVersamento) {
                    ElvVRicElencoVersByStatoRowBean elenchiVersRowBean = (ElvVRicElencoVersByStatoRowBean) Transform
                            .entity2RowBean(elencoVers);
                    elenchiVersRowBean.setString("amb_ente_strut", elenchiVersRowBean.getNmAmbiente() + " / "
                            + elenchiVersRowBean.getNmEnte() + " / " + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("num_comp",
                            elenchiVersRowBean.getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersRowBean.setBigDecimal("dimensione_byte",
                            elenchiVersRowBean.getNiSizeVersElenco().add(elenchiVersRowBean.getNiSizeAggElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento", e);
        }
        return elenchiVersTableBean;
    }

    public ElvVRicElencoVersByUdTableBean getElvVRicElencoVersByUdTableBean(long idUserIam,
            FiltriElenchiVersamento filtri) throws EMFError {
        List<ElvVRicElencoVersByUd> listaElenchiVersamento = evWebHelper.retrieveElvVRicElencoVersByUdList(idUserIam,
                filtri);
        ElvVRicElencoVersByUdTableBean elenchiVersTableBean = new ElvVRicElencoVersByUdTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVRicElencoVersByUd elencoVers : listaElenchiVersamento) {
                    ElvVRicElencoVersByUdRowBean elenchiVersRowBean = (ElvVRicElencoVersByUdRowBean) Transform
                            .entity2RowBean(elencoVers);
                    elenchiVersRowBean.setString("amb_ente_strut", elenchiVersRowBean.getNmAmbiente() + " / "
                            + elenchiVersRowBean.getNmEnte() + " / " + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("num_comp",
                            elenchiVersRowBean.getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersRowBean.setBigDecimal("dimensione_byte",
                            elenchiVersRowBean.getNiSizeVersElenco().add(elenchiVersRowBean.getNiSizeAggElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento", e);
        }
        return elenchiVersTableBean;
    }

    public ElvVLisElencoVersStatoTableBean getElenchiDaFirmareTableBean(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, BigDecimal idElencoVers, String note, String flElencoFisc, String tiGestElenco,
            Date[] dateCreazioneElencoValidate, long idUserIam, ElencoEnums.ElencoStatusEnum... statiElenco)
            throws EMFError {
        List<ElvVLisElencoVersStato> listaElenchiVersamento = evWebHelper.getListaElenchiDaFirmare(idAmbiente, idEnte,
                idStrut, idElencoVers, note, flElencoFisc, tiGestElenco, dateCreazioneElencoValidate, idUserIam,
                ElencoEnums.ElencoStatusEnum.getStringEnumsList(statiElenco));
        ElvVLisElencoVersStatoTableBean elenchiVersTableBean = new ElvVLisElencoVersStatoTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVLisElencoVersStato elenco : listaElenchiVersamento) {
                    ElvVLisElencoVersStatoRowBean elenchiVersRowBean = new ElvVLisElencoVersStatoRowBean();
                    elenchiVersRowBean = (ElvVLisElencoVersStatoRowBean) Transform.entity2RowBean(elenco);
                    elenchiVersRowBean.setString("amb_ente_strut", elenchiVersRowBean.getNmAmbiente() + " / "
                            + elenchiVersRowBean.getNmEnte() + " / " + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("dimensione_byte",
                            elenchiVersRowBean.getNiSizeAggElenco().add(elenchiVersRowBean.getNiSizeVersElenco()));
                    elenchiVersRowBean.setBigDecimal("ni_comp_elenco",
                            elenchiVersRowBean.getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento con stato "
                    + Arrays.toString(statiElenco), e);
        }
        return elenchiVersTableBean;
    }

    public ElvVLisElencoVersStatoTableBean getElenchiDaFirmareTableBean(List<BigDecimal> idElencoVersList,
            long idUserIam) throws EMFError {
        List<ElvVLisElencoVersStato> listaElenchiVersamento = evWebHelper.getListaElenchiDaFirmare(idElencoVersList,
                idUserIam);
        ElvVLisElencoVersStatoTableBean elenchiVersTableBean = new ElvVLisElencoVersStatoTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVLisElencoVersStato elenco : listaElenchiVersamento) {
                    ElvVLisElencoVersStatoRowBean elenchiVersRowBean = new ElvVLisElencoVersStatoRowBean();
                    elenchiVersRowBean = (ElvVLisElencoVersStatoRowBean) Transform.entity2RowBean(elenco);
                    elenchiVersRowBean.setString("amb_ente_strut", elenchiVersRowBean.getNmAmbiente() + " / "
                            + elenchiVersRowBean.getNmEnte() + " / " + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("dimensione_byte",
                            elenchiVersRowBean.getNiSizeAggElenco().add(elenchiVersRowBean.getNiSizeVersElenco()));
                    elenchiVersRowBean.setBigDecimal("ni_comp_elenco",
                            elenchiVersRowBean.getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento da firmare", e);
        }
        return elenchiVersTableBean;
    }

    public boolean isElencoDeletable(BigDecimal idElencoVers) {
        return evWebHelper.isElencoDeletable(idElencoVers);
    }

    public boolean isElencoClosable(BigDecimal idElencoVers) {
        return evWebHelper.isElencoClosable(idElencoVers);
    }

    public boolean isElencoValidable(BigDecimal idElencoVers) {
        return evWebHelper.isElencoValidable(idElencoVers);
    }

    public boolean areUdDocDeletables(BigDecimal idElencoVers) {
        return evWebHelper.areUdDocDeletables(idElencoVers);
    }

    public boolean areUpdDeletables(BigDecimal idElencoVers) {
        return evWebHelper.areUpdDeletables(idElencoVers);
    }

    public void deleteElenco(long idElencoVers) {
        deleteElenco(null, new BigDecimal(idElencoVers));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteElenco(Long idUserIam, BigDecimal idElencoVers) {
        /* Recupero l'elenco */
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        /* Setto la data di unità doc non annullata */
        Calendar fineDelMondo = Calendar.getInstance();
        fineDelMondo.set(2444, 11, 31, 0, 0, 0);
        fineDelMondo.set(Calendar.MILLISECOND, 0);

        /* Per ogni unità documentaria appartenente all'elenco */
        for (AroUnitaDoc aroUnitaDoc : elenco.getAroUnitaDocs()) {
            /* Rimuovo l'appartenenza dell'UD all'elenco */
            aroUnitaDoc.setElvElencoVer(null);
            /* Se l'unità doc non è annullata */
            if (aroUnitaDoc.getDtAnnul().getTime() == fineDelMondo.getTime().getTime()) {
                // MAC#27493
                /* Assegna stato NON_SELEZ_SCHED all'UD */
                aroUnitaDoc.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
                /* Registra l'UD nella coda delle UD da elaborare */
                evHelper.insertUdCodaUdDaElab(aroUnitaDoc.getIdUnitaDoc(), ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                /* Assegna stato di generazione indice AIP = nullo */
                aroUnitaDoc.setTiStatoUdElencoVers(null);
            }
        }

        /* Per ogni documento aggiunto appartenente all'elenco */
        for (AroDoc aroDoc : elenco.getAroDocs()) {
            /* Rimuovo l'appartenenza del documento all'elenco */
            aroDoc.setElvElencoVer(null);
            /* Se il documento appartiene ad una unità documentaria non annullata */
            if (aroDoc.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo.getTime().getTime()) {
                // MAC#27493
                /* Assegna stato NON_SELEZ_SCHED al documento */
                aroDoc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name());
                /* Registra il documento nella coda dei documenti da elaborare */
                evHelper.insertDocCodaDocDaElab(aroDoc.getIdDoc(), ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                /* Assegna stato di generazione indice AIP = nullo */
                aroDoc.setTiStatoDocElencoVers(null);
            }
        }

        // MAC#22942
        /* Per ogni aggiornamento metadati appartenente all'elenco */
        for (AroUpdUnitaDoc aroUpdUnitaDoc : elenco.getAroUpdUnitaDocs()) {
            /* Rimuovo l'appartenenza dell'aggiornamento metadati all'elenco */
            aroUpdUnitaDoc.setElvElencoVer(null);
            /* Se l'aggiornamento metadati appartiene ad una unità documentaria non annullata */
            if (aroUpdUnitaDoc.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo.getTime().getTime()) {
                // MAC#27493
                /* Assegna stato NON_SELEZ_SCHED all'aggiornamento metadati */
                aroUpdUnitaDoc.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                /* Registra l'aggiornamento metadati nella coda degli aggiornamenti metadati da elaborare */
                evHelper.insertUpdCodaUpdDaElab(aroUpdUnitaDoc.getIdUpdUnitaDoc(),
                        ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                /* Assegna stato di generazione indice AIP = nullo */
                aroUpdUnitaDoc.setTiStatoUpdElencoVers(null);
            }
        }
        // end MAC#22942

        /* Elimino l'elenco di versamento dalla coda degli elenchi in laborazione */
        for (ElvElencoVersDaElab e : elenco.getElvElencoVersDaElabs()) {
            evHelper.deleteElencoVersDaElab(e.getIdElencoVersDaElab());
        }

        /* Elimino lo storico delle sessioni di firma in errore per l'elenco (tabella HSM_ELENCO_SESSIONE_FIRMA) */
        List<HsmElencoSessioneFirma> listElencoSessioneFirmaHsm = evHelper.retrieveListaElencoInError(elenco,
                TiEsitoFirmaElenco.IN_ERRORE);
        for (HsmElencoSessioneFirma elencoSessioneFirmaHsm : listElencoSessioneFirmaHsm) {
            evHelper.removeEntity(elencoSessioneFirmaHsm, true);
        }

        /* Cancello l'elenco di versamento corrente */
        evHelper.deleteElvElencoVer(idElencoVers);

        /* Scrivo nel log l'avvenuta cancellazione */
        evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
    }

    // EVO#16486
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaUrnUdElenco(long idUnitaDoc, long idElencoVers)
            throws ParerUserError, ParseException, ParerInternalError {
        AroUnitaDoc aroUnitaDoc = evHelper.findByIdWithLock(AroUnitaDoc.class, idUnitaDoc);
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        CSVersatore versatore = getVersatoreUd(aroUnitaDoc, sistemaConservazione);
        CSChiave chiave = getChiaveUd(aroUnitaDoc);

        /*
         * 
         * Gestione KEY NORMALIZED / URN PREGRESSI
         * 
         * 
         */
        // 1. se il numero normalizzato sull’unità doc nel DB è nullo ->
        // il sistema aggiorna ARO_UNITA_DOC
        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        String dataInizioParam = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        Date dataInizio = dateFormat.parse(dataInizioParam);
        // controllo e calcolo URN normalizzato
        ElvVLisAllUdByElenco elvVLisAllUdByElenco = evHelper.retrieveElvVLisAllUdByElenco(idElencoVers, idUnitaDoc);
        if (!elvVLisAllUdByElenco.getDtVersMax().after(dataInizio)
                && StringUtils.isBlank(elvVLisAllUdByElenco.getCdKeyUnitaDocNormaliz())) {
            // calcola e verifica la chiave normalizzata
            String cdKeyNormalized = MessaggiWSFormat.normalizingKey(aroUnitaDoc.getCdKeyUnitaDoc()); // base
            if (urnHelper.existsCdKeyNormalized(aroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    aroUnitaDoc.getAaKeyUnitaDoc(), aroUnitaDoc.getCdKeyUnitaDoc(), cdKeyNormalized)) {
                // urn normalizzato già presente su sistema
                throw new ParerInternalError("Il numero normalizzato per l'unità documentaria "
                        + MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave) + " è già presente ");
            } else {
                // cd key normalized (se calcolato)
                if (StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
                    aroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
                }
            }
        }
        // 2. verifica pregresso
        // A. check data massima versamento recuperata in precedenza rispetto parametro
        // su db
        if (!elvVLisAllUdByElenco.getDtVersMax().after(dataInizio)) {
            // B. eseguo registra urn comp pregressi
            urnHelper.scriviUrnCompPreg(aroUnitaDoc, versatore, chiave);
            // C. eseguo registra urn sip pregressi
            // C.1. eseguo registra urn sip pregressi ud
            urnHelper.scriviUrnSipUdPreg(aroUnitaDoc, versatore, chiave);
            // C.2. eseguo registra urn sip pregressi documenti aggiunti
            urnHelper.scriviUrnSipDocAggPreg(aroUnitaDoc, versatore, chiave);
            // C.3. eseguo registra urn pregressi upd
            urnHelper.scriviUrnSipUpdPreg(aroUnitaDoc, versatore, chiave);
        }

        AroVerIndiceAipUd aroVerIndiceAipUd = evHelper.getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
        if (aroVerIndiceAipUd != null && !aroVerIndiceAipUd.getDtCreazione().after(dataInizio)) {
            // eseguo registra urn aip pregressi
            urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);
        }
    }

    private CSChiave getChiaveUd(AroUnitaDoc ud) {
        CSChiave csc = new CSChiave();
        csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
        csc.setNumero(ud.getCdKeyUnitaDoc());

        return csc;
    }

    private CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(ud.getOrgStrut().getNmStrut());
        csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        csv.setSistemaConservazione(sistemaConservazione);

        return csv;
    }

    private void calcolaUrnElenco(ElvElencoVer elenco, String nomeStruttura, String nomeStrutturaNorm, String nomeEnte,
            String nomeEnteNorm) {
        // sistema (new URN)
        String sistema = configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE,
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnte, nomeStruttura,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                TiUrnElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnteNorm, nomeStrutturaNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                TiUrnElenco.NORMALIZZATO);
    }

    /**
     * Esegue la chiusura manuale dell'elenco di versamento passato come parametro in input.
     *
     * @param idUserIam
     *            id user Iam
     * @param idElencoVers
     *            id elenco versamenti
     * @param modifica
     *            lista modifiche di tipo ElencoEnums.OpTypeEnum
     * @param note
     *            contenuto note
     * 
     * @throws IOException
     *             errore generico
     * @throws NoSuchAlgorithmException
     *             errore generico
     * @throws ParerNoResultException
     *             errore generico
     * @throws ParerUserError
     *             errore generico
     * @throws ParseException
     *             errore generico
     * @throws ParerInternalError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manualClosingElenco(long idUserIam, BigDecimal idElencoVers, List<ElencoEnums.OpTypeEnum> modifica,
            String note) throws NoSuchAlgorithmException, IOException, ParerNoResultException, ParerUserError,
            ParseException, ParerInternalError {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);

        log.info("Inizio processo di chiusura manuale elenco di versamento avente id " + elenco.getIdElencoVers());
        /* Scrivo il motivo di chiusura */
        elenco.setDlMotivoChius("Elenco di versamento chiuso manualmente");
        /* Se ci sono state modifiche sulle note indice elenco, le salvo e scrivo nel log */
        if (!modifica.isEmpty()) {
            evWebHelper.saveNomeDesNote(idUserIam, idElencoVers, elenco.getNmElenco(), elenco.getDsElenco(), note,
                    elenco.getNtElencoChiuso(), modifica);
        }
        /* Imposto la data di chiusura */
        elenco.setDtChius(new Date());
        /* Creo l'indice */
        List<BigDecimal> idUdList = evHelper.retrieveUdInElencoByElencoIdList(elenco.getIdElencoVers());
        // Per ogni unità doc dell'elenco
        for (BigDecimal idUd : idUdList) {
            // Apro nuova transazione
            context.getBusinessObject(ElenchiVersamentoEjb.class).verificaUrnUdElenco(idUd.longValue(),
                    elenco.getIdElencoVers());
        }
        // determina nome ente e struttura normalizzati e non
        OrgEnte ente = elenco.getOrgStrut().getOrgEnte();
        String nomeStruttura = elenco.getOrgStrut().getNmStrut();
        String nomeStrutturaNorm = elenco.getOrgStrut().getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Calcolo e persisto lo urn dell'elenco */
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        log.info("Creazione indice per elenco di versamento avente id '" + elenco.getIdElencoVers());

        /* Setto l'elenco a stato chiuso */
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.CHIUSO.name());

        // MEV #24534 non devo creare l'indice se TI_VALID_ELENCO = NO_INDICE
        if (!it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE.equals(elenco.getTiValidElenco())) {
            creaIndice(elenco);
        }
        /* Imposto l'utente che ha lanciato la chiusura manuale */
        elenco.setIamUserChiusoElenco(evWebHelper.findById(IamUser.class, idUserIam));

        /* Lo lascio nella coda degli elenchi da elaborare */
        ElvElencoVersDaElab elencoDaElab = evHelper.getElvElencoVersDaElabByIdElencoVers(idElencoVers.longValue());
        elencoDaElab.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.CHIUSO.name());
        /* Per ogni unità documentaria appartenente all'elenco */
        for (AroUnitaDoc aroUnitaDoc : elenco.getAroUnitaDocs()) {
            /* Assegna stato IN_ELENCO_CHIUSO all'UD */
            aroUnitaDoc.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
            /* Elimina l'UD dalla coda delle UD da elaborare */
            evHelper.deleteUdDocFromQueue(aroUnitaDoc);
        }

        /* Per ogni documento aggiunto appartenente all'elenco */
        for (AroDoc aroDoc : elenco.getAroDocs()) {
            /* Assegna stato IN_ELENCO_CHIUSO al documento */
            aroDoc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_CHIUSO.name());
            /* Elimina il documento dalla coda dei documenti da elaborare */
            evHelper.deleteDocFromQueue(aroDoc);
        }

        /* Per ogni aggiornamento appartenente all'elenco */
        for (AroUpdUnitaDoc aroUpdUnitaDoc : elenco.getAroUpdUnitaDocs()) {
            /* Assegna stato IN_ELENCO_CHIUSO all'aggiornamento */
            aroUpdUnitaDoc.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO);
            /* Elimina l'aggiornamento dalla coda degli aggiornamenti da elaborare */
            evHelper.deleteUpdFromQueue(aroUpdUnitaDoc);
        }

        /* Scrivo nel log l'avvenuta chiusura */
        evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                ElencoEnums.OpTypeEnum.CHIUSURA_ELENCO.name());
        log.info("Fine processo di chiusura manuale elenco di versamento avente id " + elenco.getIdElencoVers());
    }

    /**
     * Crea puntualmente l'indice elenco di versamento per l'elenco passato in input.
     *
     * Il metodo è stato estratto e reso pubblico da
     * {@link #manualClosingElenco(long, java.math.BigDecimal, java.util.List, java.lang.String) } per la MEV #24534
     *
     * @param idElencoVers
     *            id elenco di versamento
     * 
     * @throws ParerNoResultException
     *             se non viene restituito alcun risultato da
     *             {@link IndiceElencoVersXsdEjb#createIndex(it.eng.parer.entity.ElvElencoVer, boolean) }
     * @throws NoSuchAlgorithmException
     *             se l'algoritmo di HASH per il calcolo dell'URN non è supportato dalla JVM
     * @throws IOException
     *             in caso di errore nel salvataggio del bytearray dell'indice su DB.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaIndice(long idElencoVers) throws ParerNoResultException, NoSuchAlgorithmException, IOException {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        creaIndice(elenco);
    }

    private void creaIndice(ElvElencoVer elenco) throws ParerNoResultException, NoSuchAlgorithmException, IOException {
        /* Registro nella tabella ElvFileElencoVers */
        byte[] indexFile = iejEjb.createIndex(elenco, true);
        /* Registro nella tabella ElvFileElencoVers */
        ElvFileElencoVer elvFileElencoVers = evHelper.storeFileIntoElenco(elenco, indexFile,
                ElencoEnums.FileTypeEnum.INDICE.name());
        /* Setto i campi relativi all'hash */
        // Calcolo l'hash SHA-256 del file indiceElencoVers.xml
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(elenco.getOrgStrut().getNmStrut());
        csv.setEnte(elenco.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(elenco.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // Aggiorno l'elenco definendo l'hash dell'indice, l'algoritmo usato per il calcolo hash (=SHA-256),
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        csv.setSistemaConservazione(sistemaConservazione);
        // l'encoding del hash (=hexBinary) e la versione del XSD (=2.0) con cui è creato l'indice dell'elenco
        String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(csv);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(csv, true, Costanti.UrnFormatter.VERS_FMT_STRING);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvFileElencoVers(
                elvFileElencoVers, MessaggiWSFormat.formattaUrnElencoIndice(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(
                elvFileElencoVers, MessaggiWSFormat.formattaUrnElencoIndice(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
    }

    /**
     * Esegue la validazione dell'elenco di versamento passato come parametro in input, a seguito della chiusura
     * manuale.
     *
     * @param idUserIam
     *            id user iam
     * @param idElencoVers
     *            id elenco versamento
     * 
     * @throws ParseException
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void validElenco(long idUserIam, BigDecimal idElencoVers) throws ParseException {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        log.info("Inizio processo di validazione elenco di versamento avente id " + elenco.getIdElencoVers());
        /* Assumo lock esclusivo sull'elenco */
        evHelper.lockElenco(elenco);
        /* Controllo se almeno una unità doc appartenente all'elenco e' annullata */
        boolean annullata = evHelper.checkUdAnnullataByElenco(elenco);
        if (!annullata) { // ud non annullata
            IamUser user = evWebHelper.findById(IamUser.class, idUserIam);
            /*
             * Aggiorno l'elenco valorizzando la data di firma, il tipo di validazione e la modalità con i valori
             * definiti dall'elenco.
             */
            elenco.setDtFirmaIndice(new Date());
            /* Se i valori nell'elenco sono nulli leggo i valori dal criterio di raggruppamento */
            if (elenco.getTiValidElenco() == null || elenco.getTiModValidElenco() == null) {
                elenco.setTiValidElenco(TiValidElenco.valueOf(elenco.getDecCriterioRaggr().getTiValidElenco().name()));
                elenco.setTiModValidElenco(
                        TiModValidElenco.valueOf(elenco.getDecCriterioRaggr().getTiModValidElenco().name()));
            }

            // EVO 19304
            registraStatoElencoVersamento(idElencoVers, "VALIDA_INDICE_ELENCO_VERS",
                    "Validazione indice elenco in cui non sono presenti unità documentarie annullate",
                    TiStatoElenco.VALIDATO, user.getNmUserid());
            /*
             * Assegno all'elenco stato = VALIDATO e lo lascio nella coda degli elenchi da elaborare assegnando stato =
             * validato
             */
            elenco.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
            /* Imposto l'utente che ha lanciato la validazione */
            elenco.setIamUserFirmaIndice(user);
            ElvElencoVersDaElab elencoVersDaElab = evHelper.retrieveElencoInQueue(elenco);
            elencoVersDaElab.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
            elencoVersDaElab.setTsStatoElenco(new Date());
            /* Assegno ad ogni unità doc appartenente all'elenco stato = IN_ELENCO_VALIDATO */
            evHelper.setUdsStatus(elenco, UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
            /* Assegno ad ogni documento appartenente all'elenco stato = IN_ELENCO_VALIDATO */
            evHelper.setDocsStatus(elenco, UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
            /* Assegno ad ogni aggiornamento appartenente all'elenco stato = IN_ELENCO_VALIDATO */
            evHelper.setUpdsStatus(elenco, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO);
            /* Registro sul log delle operazioni l'apposizione della firma */
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.VALIDAZIONE_ELENCO.name());
        } else {
            evWebEjb.deleteElenco(idUserIam, idElencoVers);
        }

        log.info("Fine processo di validazione elenco di versamento avente id " + elenco.getIdElencoVers());
    }

    public OrgStrutRowBean getOrgStrutRowBeanWithAmbienteEnte(BigDecimal idStrut) {
        OrgStrut strut = evWebHelper.findById(OrgStrut.class, idStrut.longValue());
        OrgStrutRowBean rb = new OrgStrutRowBean();
        if (strut != null) {
            try {
                rb = (OrgStrutRowBean) Transform.entity2RowBean(strut);
                rb.setBigDecimal("id_ambiente", new BigDecimal(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()));
                rb.setString("nm_ambiente", strut.getOrgEnte().getOrgAmbiente().getNmAmbiente());
                rb.setString("nm_ente", strut.getOrgEnte().getNmEnte());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return rb;
    }

    /**
     * Data una lista di elenchi di versamento ed una dataChiusura impostati restituisce true o false a seconda che
     * tutti gli elenchi non presenti nella lista passata, con data chiusura inferiore a quella passata, siano firmati.
     *
     * @param elencoTableBean
     *            bean ElvVLisElencoVersStatoTableBean
     * @param dataChiusura
     *            data chiusura
     * @param idStrut
     *            id struttura
     * 
     * @return true/false
     */
    public boolean areAllElenchiNonPresentiFirmati(ElvVLisElencoVersStatoTableBean elencoTableBean, Date dataChiusura,
            BigDecimal idStrut) {
        List<BigDecimal> idElencoVersSelezionatiList = new ArrayList<>();
        for (ElvVLisElencoVersStatoRowBean elencoRowBean : elencoTableBean) {
            idElencoVersSelezionatiList.add(elencoRowBean.getIdElencoVers());
        }
        return evWebHelper.areAllElenchiNonPresentiFirmati(idElencoVersSelezionatiList, dataChiusura, idStrut);
    }

    /**
     * Controlla se su DB esiste già, per quella struttura, un elenco che abbia il nome passato come parametro
     *
     * @param nmElenco
     *            nome elenco
     * @param idStrut
     *            id struttura
     * 
     * @return true/false
     */
    public boolean existNomeElenco(String nmElenco, BigDecimal idStrut) {
        return evWebHelper.existNomeElenco(nmElenco, idStrut);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNomeDesNote(Long idUserIam, BigDecimal idElencoVers, String nmElenco, String dsElenco,
            String ntIndiceElenco, String ntElencoChiuso, List<ElencoEnums.OpTypeEnum> operList, String tiGestElenco,
            String tiValidElenco, String tiModValidElenco) {
        evWebHelper.saveNomeDesNote(idUserIam, idElencoVers, nmElenco, dsElenco, ntIndiceElenco, ntElencoChiuso,
                operList);
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers.longValue());
        elenco.setTiGestElenco(tiGestElenco);
        elenco.setTiValidElenco(TiValidElenco.valueOf(tiValidElenco));
        elenco.setTiModValidElenco(TiModValidElenco.valueOf(tiModValidElenco));
    }

    /**
     * Rimuove l'appartenenza di unità documentarie/documenti aggiunti all'elenco di versamento in questione
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param idDocsToRemove
     *            id documento da rimuovere
     * @param idUserIam
     *            id user Iam
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteUdDocFromElencoVersamento(Long idElencoVers, Set<BigDecimal> idDocsToRemove, long idUserIam) {
        log.info("Rimuovo i documenti dall'elenco di versamento...");
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        List<AroDoc> documentiPerVersamentoUd = new ArrayList<>();
        List<AroDoc> documentiPerAggiuntaDocumento = new ArrayList<>();
        /* Setto la data di unità doc non annullata */
        Calendar fineDelMondo = Calendar.getInstance();
        fineDelMondo.set(2444, 11, 31, 0, 0, 0);
        fineDelMondo.set(Calendar.MILLISECOND, 0);
        /* Diversifico i documenti considerati */
        for (BigDecimal idDocToRemove : idDocsToRemove) {
            AroDoc documento = evWebHelper.findById(AroDoc.class, idDocToRemove);
            if (documento.getTiCreazione().equals(CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name())) {
                documentiPerVersamentoUd.add(documento);
            } else {
                documentiPerAggiuntaDocumento.add(documento);
            }
        }

        /////////////////////////////////////////////////
        /* Elaboro prima i documenti per AGGIUNTA_DOCUMENTO */
        /////////////////////////////////////////////////
        for (AroDoc documentoPerAggiuntaDocumento : documentiPerAggiuntaDocumento) {
            /* Rimuovo l'appartenza del documento all'elenco di versamento */
            documentoPerAggiuntaDocumento.setElvElencoVer(null);
            /* Se il documento appartiene ad una unità documentaria non annullata */
            if (documentoPerAggiuntaDocumento.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo.getTime()
                    .getTime()) {
                // MAC#27493
                /* Assegno stato NON_SELEZ_SCHED al documento */
                documentoPerAggiuntaDocumento.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name());
                /* Registro il documento nella coda dei documenti da elaborare */
                evHelper.insertDocCodaDocDaElab(documentoPerAggiuntaDocumento.getIdDoc(),
                        ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED);
                // end MAC#27493
            }

            /* Aggiorno l'elenco di versamento */
            log.info("Dimensione elenco di versamento " + elenco.getIdElencoVers() + " pari a "
                    + elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()) + " KB");
            /* Calcolo la somma delle dimensioni dei componenti inclusi nel documento per AGGIUNTA_DOCUMENTO */
            BigDecimal compDocSize = new BigDecimal(0);
            for (AroStrutDoc strutDoc : documentoPerAggiuntaDocumento.getAroStrutDocs()) {
                for (AroCompDoc compDoc : strutDoc.getAroCompDocs()) {
                    compDocSize = compDocSize.add(
                            compDoc.getNiSizeFileCalc() != null ? compDoc.getNiSizeFileCalc() : new BigDecimal("0"));
                }
            }

            log.info("Dimensioni totali del documento " + documentoPerAggiuntaDocumento.getIdDoc()
                    + " da rimuovere pari a " + compDocSize + " KB");

            /* Ricalcolo i valori */
            elenco.setNiUnitaDocModElenco(new BigDecimal(evHelper.contaUdModificatePerDocAggiunti(idElencoVers)));
            elenco.setNiDocAggElenco(new BigDecimal(evHelper.contaDocAggiunti(idElencoVers)));
            Object[] obj = evHelper.contaCompPerDocAggiunti(idElencoVers);
            elenco.setNiCompAggElenco(new BigDecimal((Long) obj[0]));
            elenco.setNiSizeAggElenco(obj[1] != null ? (BigDecimal) obj[1] : new BigDecimal("0"));

            log.info("NUOVA dimensione elenco di versamento " + elenco.getIdElencoVers() + " pari a "
                    + elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()) + " KB");

            /* Registro sul log delle operazioni */
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.RIMUOVI_DOC_ELENCO.name(), documentoPerAggiuntaDocumento, null);
        }

        //////////////////////////////////////////////////
        /* Elaboro i documenti per VERSAMENTO_UNITA_DOC */
        //////////////////////////////////////////////////
        Set<Long> idUnitaDocsTrattate = new HashSet<>();
        for (AroDoc documentoPerVersamentoUd : documentiPerVersamentoUd) {
            /* Ricavo l'UD */
            /* Se l'UD non è già stata trattata, procedo */
            AroUnitaDoc unitaDoc = documentoPerVersamentoUd.getAroUnitaDoc();
            if (!idUnitaDocsTrattate.contains(unitaDoc.getIdUnitaDoc())) {
                /* Rimuovo l'appartenza dell'unità documentaria all'elenco di versamento */
                unitaDoc.setElvElencoVer(null);
                /* Se l'unità documentaria non è annullata */
                if (unitaDoc.getDtAnnul().getTime() == fineDelMondo.getTime().getTime()) {
                    // MAC#27493
                    /* Assegno stato NON_SELEZ_SCHED all'unità documentaria */
                    unitaDoc.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
                    /* Registro l'unità documentaria nella coda delle unità documentarie da elaborare */
                    evHelper.insertUdCodaUdDaElab(unitaDoc.getIdUnitaDoc(),
                            ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED);
                    // end MAC#27493
                }

                /* Aggiorno l'elenco di versamento */
                log.info("Dimensione elenco di versamento " + elenco.getIdElencoVers() + " pari a "
                        + elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()) + " KB");

                /*
                 * Calcolo la somma delle dimensioni dei componenti inclusi nell'unità documentaria per
                 * VERSAMENTO_UNITA_DOC
                 */
                BigDecimal compUdVersamentoSize = new BigDecimal(0);
                /* Considero tutti e soli i documenti di tipo VERSAMENTO_UNITA_DOC dell'unità documentaria */
                for (AroDoc docVersamento : unitaDoc.getAroDocs()) {
                    if (docVersamento.getTiCreazione()
                            .equals(CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name())) {
                        for (AroStrutDoc strutDoc : docVersamento.getAroStrutDocs()) {
                            for (AroCompDoc compDoc : strutDoc.getAroCompDocs()) {
                                compUdVersamentoSize = compUdVersamentoSize.add(compDoc.getNiSizeFileCalc() != null
                                        ? compDoc.getNiSizeFileCalc() : new BigDecimal("0"));
                            }
                        }
                    }
                }

                log.info("Dimensioni totali dell'unità documentaria " + unitaDoc.getIdUnitaDoc()
                        + " da rimuovere pari a " + compUdVersamentoSize + " KB");

                /* Ricalcolo i valori */
                elenco.setNiUnitaDocVersElenco(new BigDecimal(evHelper.contaUdVersate(idElencoVers)));
                elenco.setNiDocVersElenco(new BigDecimal(evHelper.contaDocVersati(idElencoVers)));
                Object[] obj = evHelper.contaCompVersati(idElencoVers);
                elenco.setNiCompVersElenco(new BigDecimal((Long) obj[0]));
                elenco.setNiSizeVersElenco(obj[1] != null ? (BigDecimal) obj[1] : new BigDecimal("0"));

                log.info("NUOVA dimensione elenco di versamento " + elenco.getIdElencoVers() + " pari a "
                        + elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()) + " KB");

                /* Registro sul log delle operazioni */
                evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                        ElencoEnums.OpTypeEnum.RIMUOVI_UD_ELENCO.name(), (AroDoc) null, unitaDoc);

                /* Inserisco l'UD tra quelle già trattate */
                idUnitaDocsTrattate.add(documentoPerVersamentoUd.getAroUnitaDoc().getIdUnitaDoc());
            }
        }
        /* Se l'elenco non contiene più elementi (componenti e/o aggiornamenti), lo elimino e scrivo nel log */
        if (elenco.getAroUnitaDocs().isEmpty() && elenco.getAroDocs().isEmpty()
                && elenco.getAroUpdUnitaDocs().isEmpty()) {
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
            evHelper.deleteElvElencoVer(new BigDecimal(elenco.getIdElencoVers()));
        }
    }

    /**
     * Rimuove l'appartenenza di unità aggiornamenti dall'elenco di versamento in questione
     *
     * @param idElencoVers
     *            id elenco di versamento
     * @param idUpdsToRemove
     *            lista elementi da rimuovere
     * @param idUserIam
     *            id user Iam
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteUpdFromElencoVersamento(Long idElencoVers, Set<BigDecimal> idUpdsToRemove, long idUserIam) {
        log.info("Rimuovo gli aggiornamenti dall'elenco di versamento...");
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        List<AroUpdUnitaDoc> aggiornamentiMetadati = new ArrayList<>();
        /* Setto la data di unità doc non annullata */
        Calendar fineDelMondo = Calendar.getInstance();
        fineDelMondo.set(2444, 11, 31, 0, 0, 0);
        fineDelMondo.set(Calendar.MILLISECOND, 0);
        /* Diversifico i documenti considerati */
        for (BigDecimal idUpdToRemove : idUpdsToRemove) {
            AroUpdUnitaDoc aggiornamento = evWebHelper.findById(AroUpdUnitaDoc.class, idUpdToRemove);
            if (aggiornamento.getTipoUpdUnitaDoc().equals(CostantiDB.TipoUpdUnitaDoc.METADATI.name())) {
                aggiornamentiMetadati.add(aggiornamento);
            }
        }

        for (AroUpdUnitaDoc aggiornamentoMetadati : aggiornamentiMetadati) {
            /* Rimuovo l'appartenza dell'aggiornamento dall'elenco di versamento */
            aggiornamentoMetadati.setElvElencoVer(null);
            /* Se l'aggiornamento appartiene ad una unità documentaria non annullata */
            if (aggiornamentoMetadati.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo.getTime().getTime()) {
                // MAC#27493
                /* Assegno stato NON_SELEZ_SCHED all'aggiornamento */
                aggiornamentoMetadati.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                /* Registro l'aggiornamento nella coda degli aggiornamenti da elaborare */
                evHelper.insertUpdCodaUpdDaElab(aggiornamentoMetadati.getIdUpdUnitaDoc(),
                        ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                /* Annullo lo stato di gestione dell’elenco sull’aggiornamento */
                aggiornamentoMetadati.setTiStatoUpdElencoVers(null);
            }

            /* Ricalcolo i valori */
            elenco.setNiUnitaDocModElenco(
                    new BigDecimal(evHelper.contaUdModificatePerByDocAggiuntiByUpd(idElencoVers)));
            elenco.setNiUpdUnitaDoc(new BigDecimal(evHelper.contaUpdUd(idElencoVers)));

            /* Registro sul log delle operazioni */
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.RIMUOVI_UPD_UD_ELENCO.name(), aggiornamentoMetadati,
                    aggiornamentoMetadati.getAroUnitaDoc());
        }

        /* Se l'elenco non contiene più elementi (componenti e/o aggiornamenti), lo elimino e scrivo nel log */
        if (elenco.getAroUnitaDocs().isEmpty() && elenco.getAroDocs().isEmpty()
                && elenco.getAroUpdUnitaDocs().isEmpty()) {
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
            evHelper.deleteElvElencoVer(new BigDecimal(elenco.getIdElencoVers()));
        }
    }

    public byte[] retrieveFileIndiceElenco(long idElencoVers, String tiFileElencoVers) {
        return evHelper.retrieveFileIndiceElenco(idElencoVers, tiFileElencoVers);
    }

    public void streamOutFileIndiceElenco(ZipOutputStream out, String fileNamePrefix, String fileNameSuffix,
            long idElencoVers, FileTypeEnum... fileTypes) throws IOException {
        List<ElvFileElencoVer> retrieveFileIndiceElenco = evHelper.retrieveFileIndiceElenco(idElencoVers,
                FileTypeEnum.getStringEnumsList(fileTypes));
        for (ElvFileElencoVer elvFileElencoVer : retrieveFileIndiceElenco) {
            FileTypeEnum fileType = ElencoEnums.FileTypeEnum.valueOf(elvFileElencoVer.getTiFileElencoVers());
            fileNamePrefix = StringUtils.defaultString(fileNamePrefix).replaceAll(" ", "_");
            fileNameSuffix = StringUtils.defaultString(fileNameSuffix).replaceAll(" ", "_");
            String fileExtension = fileType.getFileExtension();
            switch (fileType) {
            case INDICE:
            case INDICE_FIRMATO:
            case MARCA_INDICE:
            case FIRMA:
                // Niente da fare, lo metto per gestire tutti i tipi
                fileNameSuffix = StringUtils.defaultString(fileNameSuffix).replaceAll("_firma", "");
                break;
            case MARCA_FIRMA:
                fileNameSuffix += "_firma";
                break;
            case ELENCO_INDICI_AIP:
                fileNamePrefix = "ElencoIndiciAIP_";
                break;
            case FIRMA_ELENCO_INDICI_AIP:
                fileNamePrefix = "FirmaElencoIndiciAIP_";
                break;
            case MARCA_FIRMA_ELENCO_INDICI_AIP:
                fileNamePrefix = "MarcaElencoIndiciAIP_";
                break;
            default:
                throw new AssertionError(fileType.name());
            }
            addEntryToZip(out, elvFileElencoVer.getBlFileElencoVers(), fileNamePrefix + fileNameSuffix + fileExtension);
        }
        out.flush();
    }

    private void addEntryToZip(ZipOutputStream out, byte[] file, String filename) throws IOException {
        byte[] data = new byte[1024];
        InputStream bis = null;
        try {
            bis = new ByteArrayInputStream(file);
            int count;
            out.putNextEntry(new ZipEntry(filename));
            while ((count = bis.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            out.closeEntry();
        } finally {
            IOUtils.closeQuietly(bis);
        }
    }

    public void storeFirma(Long idElencoVers, byte[] fileFirmato, Date signatureDate, long idUtente)
            throws NoSuchAlgorithmException, IOException {
        ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        // Registro in ElvFileElencoVers l'INDICE_FIRMATO
        ElvFileElencoVer elvFileElencoVers = evHelper.storeFileIntoElenco(elenco, fileFirmato,
                FileTypeEnum.INDICE_FIRMATO.name());

        // EVO#16492
        /* Calcolo e persisto lo urn dell'indice dell'elenco firmato */
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(elenco.getOrgStrut().getNmStrut());
        csv.setEnte(elenco.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(elenco.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        csv.setSistemaConservazione(sistemaConservazione);
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(csv);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(csv, true, Costanti.UrnFormatter.VERS_FMT_STRING);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvFileElencoVers(
                elvFileElencoVers, MessaggiWSFormat.formattaUrnElencoIndiceFirmato(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(
                elvFileElencoVers, MessaggiWSFormat.formattaUrnElencoIndiceFirmato(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
        /* DA RIMUOVERE 19304 */
        IamUser user = evWebHelper.findById(IamUser.class, idUtente);
        elenco.setIamUserFirmaIndice(user);
        elenco.setDtFirmaIndice(signatureDate);
        /* DA RIMUOVERE 19304 */

        // EVO 19304
        registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "FIRMA_INDICE_ELENCO_VERS",
                "Firma indice elenco in cui non sono presenti unità documentarie annullate", TiStatoElenco.VALIDATO,
                user.getNmUserid());

        elenco.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
        /* Cambio stato a unità documentarie e documenti associati all'elenco */
        for (AroUnitaDoc aroUnitaDoc : elenco.getAroUnitaDocs()) {
            aroUnitaDoc.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
        }
        for (AroDoc aroDoc : elenco.getAroDocs()) {
            aroDoc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_VALIDATO.name());
        }
        for (AroUpdUnitaDoc aroUpdUnitaDoc : elenco.getAroUpdUnitaDocs()) {
            aroUpdUnitaDoc.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO);
        }
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersDaElab elencoDaElab = evHelper.getElvElencoVersDaElabByIdElencoVers(idElencoVers);
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
        elencoDaElab.setTsStatoElenco(new Date());
        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUtente,
                ElencoEnums.OpTypeEnum.VALIDAZIONE_ELENCO.name());
    }

    /**
     * Metodo di salvataggio file firmato dell'elenco indici AIP sul database
     *
     * @param idElencoVers
     *            id elenco di versamento
     * @param fileFirmato
     *            byte[] file firmato
     * @param signatureDate
     *            data firma
     * @param idUtente
     *            id utente
     * 
     * @throws IOException
     *             errore generico
     * @throws NoSuchAlgorithmException
     *             errore generico
     */
    public void storeFirmaElencoIndiceAip(Long idElencoVers, byte[] fileFirmato, Date signatureDate, long idUtente)
            throws NoSuchAlgorithmException, IOException {
        ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        evHelper.lockElenco(elenco);

        final OrgStrut orgStrut = elenco.getOrgStrut();
        final OrgEnte orgEnte = orgStrut.getOrgEnte();
        final String nmStrut = orgStrut.getNmStrut();
        final String nmEnte = orgEnte.getNmEnte();
        final String nmAmbiente = orgEnte.getOrgAmbiente().getNmAmbiente();
        String hash = new HashCalculator().calculateHashSHAX(fileFirmato, TipiHash.SHA_256).toHexBinary();
        final String sistema = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);

        ElvFileElencoVer fileElencoVers = new ElvFileElencoVer();
        fileElencoVers.setCdVerXsdFile(Costanti.VERSIONE_ELENCO_INDICE_AIP);
        fileElencoVers.setTiFileElencoVers(ElencoEnums.FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());
        fileElencoVers.setBlFileElencoVers(fileFirmato);
        fileElencoVers.setIdStrut(BigDecimal.valueOf(orgStrut.getIdStrut()));
        fileElencoVers.setDtCreazioneFile(new Date());
        fileElencoVers.setDsHashFile(hash);
        fileElencoVers.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
        fileElencoVers.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
        if (elenco.getElvFileElencoVers() == null) {
            elenco.setElvFileElencoVers(new ArrayList<>());
        }
        elenco.addElvFileElencoVer(fileElencoVers);

        // EVO#16486
        /* Calcolo e persisto lo urn dell'indice dell'elenco indici AIP firmato */
        CSVersatore versatore = new CSVersatore();
        versatore.setAmbiente(nmAmbiente);
        versatore.setEnte(nmEnte);
        versatore.setStruttura(nmStrut);
        // sistema (new URN)
        versatore.setSistemaConservazione(sistema);
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(versatore);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvFileElencoVers(
                fileElencoVers, MessaggiWSFormat.formattaUrnElencoIndiciAIPFirmati(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndiciAIPFirmati(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);

        elenco.setDtFirmaElencoIxAip(signatureDate);
        elenco.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name());

        // EVO 19304
        IamUser user = userHelper.findUserById(idUtente);
        registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "FIRMA_ELENCO_INDICI_AIP",
                "Firma elenco indice AIP", TiStatoElenco.ELENCO_INDICI_AIP_FIRMATO, user.getNmUserid());

        /* Cambio stato a unità documentarie e documenti associati all'elenco e aggiornamenti metadati */
        List<String> statiUdDocDaPortareAipFirmato = new ArrayList<>(
                Arrays.asList(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO.name()));
        List<AroUpdUDTiStatoUpdElencoVers> statiUpdDaPortareAipFirmato = new ArrayList<>(
                Arrays.asList(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO));
        elabElencoIndiceAipEjb.updateUnitaDocElencoIndiceAIP(idElencoVers, statiUdDocDaPortareAipFirmato,
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name());
        elabElencoIndiceAipEjb.updateDocumentiElencoIndiceAIP(idElencoVers, statiUdDocDaPortareAipFirmato,
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name());
        elabElencoIndiceAipEjb.updateAggiornamentiElencoIndiceAIP(idElencoVers, statiUpdDaPortareAipFirmato,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO);
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersDaElab elencoDaElab = evHelper.getElvElencoVersDaElabByIdElencoVers(idElencoVers);
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name());
        elencoDaElab.setTsStatoElenco(new Date());

        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, orgStrut, idUtente, ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());
    }

    /**
     * Metodo di marcatura file firmati di elenchi indici AIP, dati i parametri utilizzati in fase di firma
     *
     * @param idAmbiente
     *            id ambiente
     * @param idEnte
     *            id ente
     * @param idStrut
     *            id struttura
     * @param idUtente
     *            id utente
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public void marcaturaFirmaElenchiIndiciAip(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            long idUtente) throws ParerUserError {
        try {
            List<ElvVLisElencoDaMarcare> elenchiCompletati = evHelper.retrieveElenchiIndiciAipDaMarcare(idAmbiente,
                    idEnte, idStrut, idUtente, ElencoEnums.GestioneElencoEnum.FIRMA.name());
            gestioneMarcaturaElenchiIndiciAip(null, elenchiCompletati, idUtente);
            Set<Long> struts = new HashSet<>();
            List<ElvVLisElencoDaMarcare> elenchiDaMarcare = evHelper.retrieveElenchiIndiciAipDaMarcare(idAmbiente,
                    idEnte, idStrut, idUtente, ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name());
            gestioneMarcaturaElenchiIndiciAip(struts, elenchiDaMarcare, idUtente);
            // Presente un requisito che richiede la marcatura di tutti Elenchi Indici AIP nello stato ERR_MARCA
            List<ElvVLisElencoVersStato> listaElenchiErrati = evWebHelper.getListaElenchiDaFirmare(null, null, null,
                    null, null, null, null, null, idUtente, ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());
            gestioneMarcaturaElenchiIndiciAipErrati(struts, listaElenchiErrati, idUtente);
        } catch (ParerInternalError ex) {
            log.error("Errore durante marcatura: ", ex);
            throw new ParerUserError("Errore durante la fase di marcatura");
        }
    }

    private void gestioneMarcaturaElenchiIndiciAipErrati(Set<Long> struts,
            List<ElvVLisElencoVersStato> elenchiDaMarcare, long idUtente) throws ParerInternalError {
        for (ElvVLisElencoVersStato elvVLisElencoDaMarcare : elenchiDaMarcare) {
            try {
                if (struts != null) {
                    boolean verificaPartizioni = Boolean.parseBoolean(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI, null,
                                    null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
                    // List<ElvVLisElencoVersStato> listaElenchiDaChiudere = evWebHelper.getListaElenchiDaFirmare(null,
                    // null, elvVLisElencoDaMarcare.getIdStrut(), null, null, null, ElencoStatusEnum.DA_CHIUDERE, null,
                    // idUtente);
                    // boolean verificaPartizioni =
                    // Boolean.parseBoolean(configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI,
                    // null, null, null, null));
                    List<ElvVLisElencoVersStato> listaElenchiDaChiudere = evWebHelper.getListaElenchiDaFirmare(null,
                            null, elvVLisElencoDaMarcare.getIdStrut(), null, null, null, null, null, idUtente,
                            ElencoStatusEnum.DA_CHIUDERE.name());
                    if (struts.add(elvVLisElencoDaMarcare.getIdStrut().longValue()) && verificaPartizioni
                            && !listaElenchiDaChiudere.isEmpty()) {
                        if (struttureEjb.checkPartizioni(elvVLisElencoDaMarcare.getIdStrut(), new Date(),
                                CostantiDB.TiPartition.FILE_ELENCHI_VERS.name()).equals("0")) {
                            log.error(
                                    "Partizione di tipo FILE_ELENCHI_VERS non definita per la data corrente e la struttura "
                                            + elvVLisElencoDaMarcare.getNmAmbiente() + "-"
                                            + elvVLisElencoDaMarcare.getNmEnte() + "-"
                                            + elvVLisElencoDaMarcare.getNmStrut());
                            throw new ParerWarningException();
                        }
                    }
                }
                context.getBusinessObject(ElenchiVersamentoEjb.class).gestioneMarcaturaElenchiIndiciAip(
                        elvVLisElencoDaMarcare.getIdElencoVers().longValue(),
                        ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name(), idUtente);
            } catch (ParerWarningException ex) {
                // Errore di partizione assente, deve essere intercettata per continuare il ciclo
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .saveErroreMarcaElencoIndiceAip(elvVLisElencoDaMarcare.getIdElencoVers().longValue(), idUtente);
            } catch (ParerInternalError ex) {
                // Errore di acquisizione marca temporale, deve essere intercettata e gestita, poi terminare lo use case
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .saveErroreMarcaElencoIndiceAip(elvVLisElencoDaMarcare.getIdElencoVers().longValue(), idUtente);
                throw ex;
            }
        }
    }

    private void gestioneMarcaturaElenchiIndiciAip(Set<Long> struts, List<ElvVLisElencoDaMarcare> elenchiDaMarcare,
            long idUtente) throws ParerInternalError {
        for (ElvVLisElencoDaMarcare elvVLisElencoDaMarcare : elenchiDaMarcare) {
            try {
                if (struts != null) {
                    boolean verificaPartizioni = Boolean.parseBoolean(
                            configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI, null,
                                    null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
                    // List<ElvVLisElencoVersStato> listaElenchiDaChiudere = evWebHelper.getListaElenchiDaFirmare(null,
                    // null, elvVLisElencoDaMarcare.getIdStrut(), null, null, null, ElencoStatusEnum.DA_CHIUDERE, null,
                    // idUtente);
                    // boolean verificaPartizioni =
                    // Boolean.parseBoolean(configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI,
                    // null, null, null, null));
                    List<ElvVLisElencoVersStato> listaElenchiDaChiudere = evWebHelper.getListaElenchiDaFirmare(null,
                            null, elvVLisElencoDaMarcare.getIdStrut(), null, null, null, null, null, idUtente,
                            ElencoStatusEnum.DA_CHIUDERE.name());
                    if (struts.add(elvVLisElencoDaMarcare.getIdStrut().longValue()) && verificaPartizioni
                            && !listaElenchiDaChiudere.isEmpty()) {
                        if (struttureEjb.checkPartizioni(elvVLisElencoDaMarcare.getIdStrut(), new Date(),
                                CostantiDB.TiPartition.FILE_ELENCHI_VERS.name()).equals("0")) {
                            log.error(
                                    "Partizione di tipo FILE_ELENCHI_VERS non definita per la data corrente e la struttura "
                                            + elvVLisElencoDaMarcare.getNmAmbiente() + "-"
                                            + elvVLisElencoDaMarcare.getNmEnte() + "-"
                                            + elvVLisElencoDaMarcare.getNmStrut());
                            throw new ParerWarningException();
                        }
                    }
                }
                context.getBusinessObject(ElenchiVersamentoEjb.class).gestioneMarcaturaElenchiIndiciAip(
                        elvVLisElencoDaMarcare.getIdElencoVers().longValue(), elvVLisElencoDaMarcare.getTiGestElenco(),
                        idUtente);
            } catch (ParerWarningException ex) {
                // Errore di partizione assente, deve essere intercettata e gestita, poi continuare il ciclo
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .saveErroreMarcaElencoIndiceAip(elvVLisElencoDaMarcare.getIdElencoVers().longValue(), idUtente);
            } catch (ParerInternalError ex) {
                // Errore di acquisizione marca temporale, deve essere intercettata e gestita, poi terminare lo use case
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .saveErroreMarcaElencoIndiceAip(elvVLisElencoDaMarcare.getIdElencoVers().longValue(), idUtente);
                throw ex;
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestioneMarcaturaElenchiIndiciAip(long idElencoVers, String tiGestElenco, long idUtente)
            throws ParerInternalError {
        final ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        evHelper.lockElenco(elenco);

        ElencoEnums.GestioneElencoEnum tiGestioneEnum = ElencoEnums.GestioneElencoEnum.valueOf(tiGestElenco);
        switch (tiGestioneEnum) {
        case FIRMA:
            /*
             * Aggiunto un controllo per determinare se l'elenco indice AIP si trova realmente nello stato desiderato In
             * caso negativo scrive un log di warning
             */
            if (elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name())) {
                List<String> statiUdDocDaCompletare = new ArrayList<>(
                        Arrays.asList(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name()));
                elabElencoIndiceAipEjb.setCompletato(elenco, statiUdDocDaCompletare);
                // EVO 19304
                evWebEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                        "MARCA_ELENCO_INDICI_AIP", "Gestione elenco = FIRMA",
                        it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco.COMPLETATO, null);
            } else {
                log.warn("Impossibile completare l'elenco indice AIP con id " + elenco.getIdElencoVers()
                        + ", NON è in stato ELENCO_INDICI_AIP_FIRMATO");
            }
            break;
        case MARCA_FIRMA:
            /*
             * Aggiunto un controllo per determinare se l'elenco indice AIP si trova realmente in uno degli stati
             * desiderati In caso negativo scrive un log di warning
             */
            if (isElencoToMark(elenco)) {
                byte[] firmaElencoIndiciAip = evHelper.retrieveFileIndiceElenco(idElencoVers,
                        ElencoEnums.FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());
                try {
                    /* Richiedo la marca per il file firmato */
                    // TimeStampToken tsToken = cryptoInvoker.requestTST(firmaElencoIndiciAip);
                    ParerTST tsToken = cryptoInvoker.requestTST(firmaElencoIndiciAip);
                    byte[] marcaTemporale = tsToken.getEncoded();
                    /* Verifico l'avvenuta acquisizione della marcatura temporale */
                    if (marcaTemporale != null) {
                        log.info("Marca temporale valida");
                        saveMarcaElencoIndiceAip(elenco, marcaTemporale, idUtente);
                    } else {
                        throw new ParerInternalError("Acquisizione marca temporale fallita");
                    }
                } catch (ParerInternalError ex) {
                    throw ex;
                    // } catch (IOException | NoSuchAlgorithmException | NoSuchProviderException | TSPException ex) {
                } catch (CryptoParerException ex) {
                    log.error("Errore di acquisizione della marca temporale", ex);
                    throw new ParerInternalError(ParerErrorSeverity.ERROR, ExceptionUtils.getRootCauseMessage(ex), ex);
                }
            } else {
                log.warn("Impossibile completare l'elenco indice AIP con id " + elenco.getIdElencoVers()
                        + ", NON è nello stato di quelli marcabili");
            }
            break;
        case NO_FIRMA:
            break;
        default:
            throw new AssertionError(tiGestioneEnum.name());
        }
    }

    private boolean isElencoToMark(ElvElencoVer elenco) {
        String elencoFirmato = ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name();
        String elencoErroreMarca = ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name();

        return elenco.getTiStatoElenco().equals(elencoFirmato) || elenco.getTiStatoElenco().equals(elencoErroreMarca);
    }

    private boolean isElencoFirmato(ElvElencoVer elenco, DecCriterioRaggr criterio) {
        String elencoFirmaInCorso = ElencoEnums.ElencoStatusEnum.FIRMA_IN_CORSO.name();
        String elencoValidato = ElencoEnums.ElencoStatusEnum.VALIDATO.name();
        String elencoFirmeVerificateDtVers = ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS.name();
        String elencoInCodaIndiceAip = ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP.name();
        String elencoIndiciAipGenerati = ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name();
        String elencoElencoIndiciAipCreato = ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name();
        String elencoElencoIndiciAipFirmaInCorso = ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO.name();
        String elencoElencoIndiciAipFirmato = ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name();
        String elencoElencoIndiciAipErrMarca = ElencoEnums.ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name();
        String elencoCompletato = ElencoEnums.ElencoStatusEnum.COMPLETATO.name();

        boolean firma = (elenco.getTiValidElenco() != null)
                ? elenco.getTiValidElenco().name().equals(ElencoEnums.GestioneElencoEnum.FIRMA.name())
                : criterio.getTiValidElenco().name()
                        .equals(it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.FIRMA.name());

        return (elenco.getTiStatoElenco().equals(elencoFirmaInCorso) || elenco.getTiStatoElenco().equals(elencoValidato)
                || elenco.getTiStatoElenco().equals(elencoFirmeVerificateDtVers)
                || elenco.getTiStatoElenco().equals(elencoInCodaIndiceAip)
                || elenco.getTiStatoElenco().equals(elencoIndiciAipGenerati)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipCreato)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipFirmaInCorso)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipFirmato)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipErrMarca)
                || elenco.getTiStatoElenco().equals(elencoCompletato)) && firma;
    }

    private void saveMarcaElencoIndiceAip(ElvElencoVer elenco, byte[] marcaTemporale, long idUtente) {
        final OrgStrut orgStrut = elenco.getOrgStrut();
        final OrgEnte orgEnte = orgStrut.getOrgEnte();
        final String nmStrut = orgStrut.getNmStrut();
        final String nmEnte = orgEnte.getNmEnte();
        final String nmAmbiente = orgEnte.getOrgAmbiente().getNmAmbiente();
        String hash = DigestUtils.sha256Hex(marcaTemporale);
        final String sistema = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null, TipoAplVGetValAppart.APPLIC);

        ElvFileElencoVer fileElencoVers = new ElvFileElencoVer();
        fileElencoVers.setTiFileElencoVers(ElencoEnums.FileTypeEnum.MARCA_FIRMA_ELENCO_INDICI_AIP.name());
        fileElencoVers.setBlFileElencoVers(marcaTemporale);
        fileElencoVers.setIdStrut(BigDecimal.valueOf(orgStrut.getIdStrut()));
        fileElencoVers.setDtCreazioneFile(new Date());
        fileElencoVers.setDsHashFile(hash);
        fileElencoVers.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
        fileElencoVers.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
        if (elenco.getElvFileElencoVers() == null) {
            elenco.setElvFileElencoVers(new ArrayList<>());
        }
        elenco.addElvFileElencoVer(fileElencoVers);

        // EVO#16486
        /* Calcolo e persisto lo urn della marca dell'elenco indici AIP firmato */
        CSVersatore versatore = new CSVersatore();
        versatore.setAmbiente(nmAmbiente);
        versatore.setEnte(nmEnte);
        versatore.setStruttura(nmStrut);
        // sistema (new URN)
        versatore.setSistemaConservazione(sistema);
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(versatore);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvFileElencoVers(
                fileElencoVers, MessaggiWSFormat.formattaUrnMarcaElencoIndiciAIP(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(
                fileElencoVers, MessaggiWSFormat.formattaUrnMarcaElencoIndiciAIP(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);

        elenco.setDtMarcaElencoIxAip(fileElencoVers.getDtCreazioneFile());
        // elabElencoIndiceAipEjb.setElencoCompletatoTxReq(elenco.getIdElencoVers());
        List<String> statiUdDocDaCompletare = new ArrayList<>(
                Arrays.asList(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name(),
                        ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA.name()));
        elabElencoIndiceAipEjb.setCompletato(elenco, statiUdDocDaCompletare);
        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, orgStrut, idUtente, ElencoEnums.OpTypeEnum.MARCA_ELENCO_INDICI_AIP.name());
        IamUser user = evHelper.findById(IamUser.class, idUtente);
        // EVO 19304
        registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "MARCA_ELENCO_INDICI_AIP",
                "Marca assunta con successo; gestione elenco = MARCA_FIRMA",
                it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco.COMPLETATO, user.getNmUserid());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveErroreMarcaElencoIndiceAip(long idElencoVers, long idUtente) {
        ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        evHelper.lockElenco(elenco);
        elenco.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());
        /* Cambio stato a unità documentarie e documenti associati all'elenco */
        List<String> statiUdDocDaPortareErrMarca = new ArrayList<>(
                Arrays.asList(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name()));
        List<AroUpdUDTiStatoUpdElencoVers> statiUpdDaPortareErrMarca = new ArrayList<>(
                Arrays.asList(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO));
        elabElencoIndiceAipEjb.updateUnitaDocElencoIndiceAIP(idElencoVers, statiUdDocDaPortareErrMarca,
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA.name());
        elabElencoIndiceAipEjb.updateDocumentiElencoIndiceAIP(idElencoVers, statiUdDocDaPortareErrMarca,
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA.name());
        elabElencoIndiceAipEjb.updateAggiornamentiElencoIndiceAIP(idElencoVers, statiUpdDaPortareErrMarca,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA);
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersDaElab elencoDaElab = evHelper.getElvElencoVersDaElabByIdElencoVers(idElencoVers);
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());
        elencoDaElab.setTsStatoElenco(new Date());
        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUtente,
                ElencoEnums.OpTypeEnum.MARCA_ELENCO_INDICI_AIP_FALLITA.name());
        // EVO 19304
        IamUser user = evHelper.findById(IamUser.class, idUtente);
        registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "MARCA_ELENCO_INDICI_AIP",
                "Errore nell’assunzione della marca; gestione elenco = MARCA_FIRMA",
                TiStatoElenco.ELENCO_INDICI_AIP_ERR_MARCA, user.getNmUserid());
    }

    /**
     * Returns <code>true</code> if there is at least one ElencoIndiceAIP to mark otherwise <code>false</code>.
     *
     * @param idUser
     *            id utente
     * 
     * @return true/false
     */
    public boolean getElenchiIndiciAipToMark(long idUser) {
        List<String> elenchiStates = new ArrayList<>();
        elenchiStates.add(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name());
        elenchiStates.add(ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());

        return evWebHelper.countElencIndiciAipInStates(idUser, elenchiStates) != 0;
    }

    public boolean soloUdAnnul(BigDecimal idElencoVers) {
        ElvVChkSoloUdAnnul soloUdAnnul = evHelper.findViewById(ElvVChkSoloUdAnnul.class, idElencoVers);
        return soloUdAnnul.getFlSoloUdAnnul() != null && soloUdAnnul.getFlSoloUdAnnul().equals("1")
                && soloUdAnnul.getFlSoloDocAnnul() != null && soloUdAnnul.getFlSoloDocAnnul().equals("1")
                && soloUdAnnul.getFlSoloUpdUdAnnul() != null && soloUdAnnul.getFlSoloUpdUdAnnul().equals("1");
    }

    public boolean almenoUnaUdAnnul(BigDecimal idElencoVers) {
        ElvVChkUnaUdAnnul unaUdAnnul = evHelper.findViewById(ElvVChkUnaUdAnnul.class, idElencoVers);
        return unaUdAnnul.getFlUnaUdVersAnnul().equals("1") || unaUdAnnul.getFlUnaUdDocAggAnnul().equals("1")
                || unaUdAnnul.getFlUnaUdUpdUdAnnul().equals("1");
    }

    // public void manageElencoUdAnnulDaFirmaElenco(BigDecimal idElencoVers) {
    // ElvElencoVer elencoVers = evHelper.findById(ElvElencoVer.class, idElencoVers);
    // // Aggiorno le unità doc / doc aggiunti appartenenti all’elenco assegnando stato di generazione indice AIP =
    // nullo
    // elabElencoIndiceAipEjb.updateUnitaDocElencoIndiceAIP(idElencoVers.longValue(), null, null);
    // elabElencoIndiceAipEjb.updateDocumentiElencoIndiceAIP(idElencoVers.longValue(), null, null);
    // // Elimina l’elenco di versamento
    // evHelper.removeEntity(elencoVers, true);
    // }
    public void manageElencoUdAnnulDaFirmaElenco(BigDecimal idElencoVers, long idUserIam) {
        ElvElencoVer elencoVers = evHelper.findById(ElvElencoVer.class, idElencoVers);

        List<AroUnitaDoc> unitaDocElencoList = evHelper.getUnitaDocVersateElenco(idElencoVers);
        List<AroDoc> docAggiuntiElencoList = evHelper.getDocAggiuntiElenco(idElencoVers);
        List<AroUpdUnitaDoc> updMetadatiElencoList = evHelper.getUpdMetadatiElenco(idElencoVers);

        // Per ogni unità doc appartenente all'elenco
        for (AroUnitaDoc unitaDocElenco : unitaDocElencoList) {
            unitaDocElenco.setElvElencoVer(null);
            if (!unitaDocElenco.getTiStatoConservazione()
                    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
                // MAC#27493
                // Assegno stato ud nell'elenco di vers uguale a NON_SELEZ_SCHED
                unitaDocElenco.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
                // Registro l'ud nella coda delle ud da elaborare (tabella ELV_UD_VERS_DA_ELAB_ELENCO)
                evHelper.insertUdCodaUdDaElab(unitaDocElenco.getIdUnitaDoc(),
                        ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                // Assegno stato ud nell'elenco di vers uguale a null
                unitaDocElenco.setTiStatoUdElencoVers(null);
            }
        }

        // Per ogni doc aggiunto appartenente all'elenco
        for (AroDoc docAggiuntoElenco : docAggiuntiElencoList) {
            docAggiuntoElenco.setElvElencoVer(null);
            if (!docAggiuntoElenco.getAroUnitaDoc().getTiStatoConservazione()
                    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
                // MAC#27493
                // Assegno stato doc nell'elenco di vers uguale a NON_SELEZ_SCHED
                docAggiuntoElenco.setTiStatoDocElencoVers(ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
                // Registro il doc nella coda dei doc da elaborare (tabella ELV_DOC_AGG_DA_ELAB_ELENCO)
                evHelper.insertDocCodaDocDaElab(docAggiuntoElenco.getIdDoc(),
                        ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                // Assegno stato ud nell'elenco di vers uguale a null
                docAggiuntoElenco.setTiStatoDocElencoVers(null);
            }
        }

        // Per ogni upd metadati appartenente all'elenco
        for (AroUpdUnitaDoc updMetadatiElenco : updMetadatiElencoList) {
            updMetadatiElenco.setElvElencoVer(null);
            if (!updMetadatiElenco.getAroUnitaDoc().getTiStatoConservazione()
                    .equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
                // MAC#27493
                // Assegno stato upd nell'elenco di vers uguale a NON_SELEZ_SCHED
                updMetadatiElenco.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                // Registro la upd nella coda degli aggiornamenti da elaborare (tabella ELV_UPD_UD_DA_ELAB_ELENCO)
                evHelper.insertUpdCodaUpdDaElab(updMetadatiElenco.getIdUpdUnitaDoc(),
                        ElvUpdUdDaElabTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                // end MAC#27493
            } else {
                // Assegno stato ud nell'elenco di vers uguale a null
                updMetadatiElenco.setTiStatoUpdElencoVers(null);
            }
        }

        // Elimino l'elenco di versamento dalla coda degli elenchi in elaborazione
        ElvElencoVersDaElab elencoDaElab = evHelper.retrieveElencoInQueue(elencoVers);
        evHelper.removeEntity(elencoDaElab, true);

        /* Elimino lo storico delle sessioni di firma in errore per l'elenco (tabella HSM_ELENCO_SESSIONE_FIRMA) */
        List<HsmElencoSessioneFirma> listElencoSessioneFirmaHsm = evHelper.retrieveListaElencoInError(elencoVers,
                TiEsitoFirmaElenco.IN_ERRORE);
        for (HsmElencoSessioneFirma elencoSessioneFirmaHsm : listElencoSessioneFirmaHsm) {
            evHelper.removeEntity(elencoSessioneFirmaHsm, true);
        }

        // Cancello l'elenco di versamento corrente
        evHelper.deleteElvElencoVer(idElencoVers);

        // Scrivo nel log l'avvenuta cancellazione
        evHelper.writeLogElencoVers(elencoVers, elencoVers.getOrgStrut(), idUserIam,
                ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
    }

    public void manageElencoUdAnnulDaFirmaElencoIndiciAip(BigDecimal idElencoVers) {
        ElvElencoVer elencoVers = evHelper.findById(ElvElencoVer.class, idElencoVers);
        // Modifico alcuni parametri dell'elenco, lo elimino da quelli da elaborare ed elimino il file relativo ad
        // elenco indici aip
        elencoVers.setTiStatoElenco(ElencoStatusEnum.COMPLETATO.name());
        elencoVers.setTiGestElenco(ElencoEnums.GestioneElencoEnum.NO_FIRMA.name());
        String nota = elencoVers.getNtElencoChiuso();
        elencoVers.setNtElencoChiuso(
                (StringUtils.isNotBlank(nota) ? nota + ";" : "") + "L'elenco contiene solo versamenti annullati");
        // Elimina l’elenco dalla coda degli elenchi da elaborare
        ElvElencoVersDaElab elencoDaElab = evHelper.retrieveElencoInQueue(elencoVers);
        evHelper.removeEntity(elencoDaElab, true);
        // Elimina il record relativo al file di tipo ELENCO_INDICI_AIP
        ElvFileElencoVer fileElencoVer = evHelper.getFileIndiceElenco(idElencoVers.longValue(),
                FileTypeEnum.ELENCO_INDICI_AIP.name());
        evHelper.removeEntity(fileElencoVer, true);
    }

    public ElvStatoElencoVerTableBean getElvStatoElencoVersTableBean(BigDecimal idElencoVers) throws EMFError {
        List<ElvStatoElencoVer> listaStatiElencoVers = evHelper.retrieveStatiElencoByElencoVers(idElencoVers);
        ElvStatoElencoVerTableBean statiElencoVersTableBean = new ElvStatoElencoVerTableBean();
        try {
            if (listaStatiElencoVers != null && !listaStatiElencoVers.isEmpty()) {
                for (ElvStatoElencoVer statoElencoVers : listaStatiElencoVers) {
                    ElvStatoElencoVerRowBean statoElencoVersRowBean = (ElvStatoElencoVerRowBean) Transform
                            .entity2RowBean(statoElencoVers);
                    if (statoElencoVers.getIamUser() != null) {
                        statoElencoVersRowBean.setString("nm_userid", statoElencoVers.getIamUser().getNmUserid());
                    }
                    statoElencoVersRowBean.setString("cd_ti_eve_stato_elenco_vers",
                            (evHelper.findById(DecTiEveStatoElencoVers.class,
                                    statoElencoVers.getIdTiEveStatoElencoVers())).getCdTiEveStatoElencoVers());
                    statiElencoVersTableBean.add(statoElencoVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli stati assunti dall'elenco di versamento ", e);
        }
        return statiElencoVersTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long registraStatoElencoVersamento(BigDecimal idElencoVers, String cdTiEveElencoVers,
            String dsCondStatoElencoVers, TiStatoElenco tiStatoElenco, String nmUserid) {
        ElvStatoElencoVer statoElencoVers = new ElvStatoElencoVer();
        statoElencoVers.setTiStatoElenco(tiStatoElenco);
        statoElencoVers.setPgStatoElencoVers(evHelper.getPgStatoElencoVers(idElencoVers).add(BigDecimal.ONE));
        if (nmUserid != null) {
            IamUser user = userHelper.findIamUser(nmUserid);
            statoElencoVers.setIamUser(user);
        }
        statoElencoVers.setDsCondStatoElencoVers(dsCondStatoElencoVers);
        BigDecimal idTiEveStatoElencoVers = evHelper.getIdTiEveStatoElencoVers(cdTiEveElencoVers);
        statoElencoVers.setIdTiEveStatoElencoVers(idTiEveStatoElencoVers);// setElvElencoVer
        ElvElencoVer elencoVer = evHelper.findById(ElvElencoVer.class, idElencoVers);
        statoElencoVers.setElvElencoVer(elencoVer);
        statoElencoVers.setTsStatoElencoVers(new Date());
        evHelper.getEntityManager().persist(statoElencoVers);
        evHelper.getEntityManager().flush();
        // persist
        elencoVer.setIdStatoElencoVersCor(BigDecimal.valueOf(statoElencoVers.getIdStatoElencoVers()));
        return statoElencoVers.getIdStatoElencoVers();
    }

    public List<ElvVLisElencoVersStato> getElenchiFiscaliStrutturaAperti(long idStrut) {
        return evHelper.getElenchiFiscaliStrutturaAperti(idStrut, 2010);
    }

    public List<ElvElencoVer> getElenchiFiscaliByStrutturaAperti(long idStrut, int anno) {
        return evHelper.getElenchiFiscaliByStrutturaAperti(idStrut, anno);
    }

    public boolean isStatoElencoCorrente(long idElencoVers,
            it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco tiStatoElenco) {
        return evHelper.isStatoElencoCorrente(idElencoVers, tiStatoElenco);
    }

}
