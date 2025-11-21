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

package it.eng.parer.web.ejb;

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

import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.ejb.IndiceElencoVersXsdEjb;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.FileTypeEnum;
import static it.eng.parer.elencoVersamento.utils.ElencoEnums.GestioneElencoEnum.FIRMA;
import static it.eng.parer.elencoVersamento.utils.ElencoEnums.GestioneElencoEnum.MARCA_FIRMA;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecTiEveStatoElencoVers;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.ElvStatoElencoVer;
import it.eng.parer.entity.ElvUrnElencoVers;
import it.eng.parer.entity.HsmElencoSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco;
import it.eng.parer.entity.constraint.ElvUpdUdDaElabElenco.ElvUpdUdDaElabTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvUrnElencoVers.TiUrnElenco;
import it.eng.parer.entity.constraint.ElvUrnFileElencoVers;
import it.eng.parer.entity.constraint.HsmElencoSessioneFirma.TiEsitoFirmaElenco;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.sign.SigningResponse;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import it.eng.parer.helper.GenericHelper;
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
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import java.util.concurrent.Future;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class ElenchiVersamentoEjb {

    private static final Logger log = LoggerFactory.getLogger(ElenchiVersamentoEjb.class);
    public static final String DATE_FORMAT = "yyyyMMdd";

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
    private AmministrazioneEjb amministrazioneEjb;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private CryptoInvoker cryptoInvoker;
    @EJB
    private UserHelper userHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    @EJB
    private GenericHelper genericHelper;
    // MEV#30397
    @EJB
    private ObjectStorageService objectStorageService;
    // end MEV#30397
    // MEV #31162
    @EJB
    private UnitaDocumentarieEjb udEjb;
    // end MEV #31162

    public ElvElencoVerRowBean getElvElencoVersRowBean(BigDecimal idElencoVers) {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers.longValue());
        DecCriterioRaggr criterio = evWebHelper.findById(DecCriterioRaggr.class,
                elenco.getDecCriterioRaggr().getIdCriterioRaggr());
        List<ElvFileElencoVer> elencoIndice = evHelper
                .retrieveFileIndiceElenco(idElencoVers.longValue(), new String[] {
                        FileTypeEnum.INDICE.name() });
        List<ElvFileElencoVer> elencoIndiceAip = evHelper
                .retrieveFileIndiceElenco(idElencoVers.longValue(), new String[] {
                        FileTypeEnum.ELENCO_INDICI_AIP.name() });
        List<ElvUrnElencoVers> elencoUrn = evHelper
                .retrieveUrnElencoVersList(idElencoVers.longValue());
        ElvElencoVerRowBean elencoRowBean = new ElvElencoVerRowBean();
        try {
            elencoRowBean = (ElvElencoVerRowBean) Transform.entity2RowBean(elenco);
            // MEV#15967 - Attivazione della firma Xades e XadesT
            List<ElvFileElencoVer> elencoIndiceAipFirmati = evHelper
                    .retrieveFileIndiceElenco(idElencoVers.longValue(), new String[] {
                            FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name() });
            if (elencoIndiceAipFirmati != null && !elencoIndiceAipFirmati.isEmpty()) {
                elencoRowBean.setTiFirma(elencoIndiceAipFirmati.get(0).getTiFirma());
            }
            //

            final OrgAmbiente orgAmbiente = elenco.getOrgStrut().getOrgEnte().getOrgAmbiente();
            elencoRowBean.setString("nm_criterio_raggr",
                    elenco.getDecCriterioRaggr().getNmCriterioRaggr());
            elencoRowBean.setString("ds_criterio_raggr",
                    elenco.getDecCriterioRaggr().getDsCriterioRaggr());
            elencoRowBean.setString("ti_gest_elenco_criterio",
                    elenco.getDecCriterioRaggr().getTiGestElencoCriterio());
            boolean elencoStandard = elenco.getFlElencoStandard().equals("1");
            boolean elencoFiscale = elenco.getFlElencoFisc().equals("1");
            String tiGestioneAmb;
            if (!elencoStandard && !elencoFiscale) {
                tiGestioneAmb = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD,
                        BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
                        BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
            } else if (elencoFiscale) {
                tiGestioneAmb = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC,
                        BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
                        BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
            } else {
                tiGestioneAmb = configurationHelper.getValoreParamApplicByStrut(
                        CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC,
                        BigDecimal.valueOf(orgAmbiente.getIdAmbiente()),
                        BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));
            }
            elencoRowBean.setString("ti_gest_elenco_amb", tiGestioneAmb);
            elencoRowBean.setBigDecimal("num_comp",
                    elenco.getNiCompAggElenco().add(elenco.getNiCompVersElenco()));
            elencoRowBean.setBigDecimal("dim_bytes",
                    elenco.getNiSizeVersElenco().add(elenco.getNiSizeAggElenco()));
            final String nmAmbiente = orgAmbiente.getNmAmbiente();
            final String nmEnte = elenco.getOrgStrut().getOrgEnte().getNmEnte();
            final String nmStrut = elenco.getOrgStrut().getNmStrut();
            elencoRowBean.setString("nm_ambiente", nmAmbiente);
            elencoRowBean.setString("nm_ente", nmEnte);
            elencoRowBean.setString("nm_strut", nmStrut);
            elencoRowBean.setString("amb_ente_strut",
                    nmAmbiente + " / " + nmEnte + " / " + nmStrut);
            if (elencoIndiceAip != null && !elencoIndiceAip.isEmpty()) {
                elencoRowBean.setString("cd_versione_xsd",
                        elencoIndiceAip.get(0).getCdVerXsdFile());
            } else {
                elencoRowBean.setString("cd_versione_xsd", null);
            }
            if (elenco.getTiValidElenco() == null || elenco.getTiModValidElenco() == null) {
                elencoRowBean.setString("ti_valid_elenco",
                        elenco.getDecCriterioRaggr().getTiValidElenco().name());
                elencoRowBean.setString("ti_mod_valid_elenco",
                        elenco.getDecCriterioRaggr().getTiModValidElenco().name());
            }
            if (isElencoFirmato(elenco, criterio)) {
                elencoRowBean.setObject("fl_elenco_firmato", "1");
            } else {
                elencoRowBean.setObject("fl_elenco_firmato", "0");
            }
            if (elenco.getIamUserFirmaIndice() != null) {
                elencoRowBean.setString("nm_cognome_nome_user",
                        elenco.getIamUserFirmaIndice().getNmCognomeUser() + " "
                                + elenco.getIamUserFirmaIndice().getNmNomeUser());
            }
            if (elencoIndice != null && !elencoIndice.isEmpty()) {
                elencoRowBean.setString("cd_versione_indice",
                        elencoIndice.get(0).getCdVerXsdFile());
            } else {
                elencoRowBean.setString("cd_versione_indice", null);
            }
            // EVO#16486
            if (elencoUrn != null && !elencoUrn.isEmpty()) {
                // Recupero lo urn ORIGINALE
                ElvUrnElencoVers urnElencoVers = (ElvUrnElencoVers) CollectionUtils.find(elencoUrn,
                        new Predicate() {
                            @Override
                            public boolean evaluate(final Object object) {
                                return ((ElvUrnElencoVers) object).getTiUrn()
                                        .equals(TiUrnElenco.ORIGINALE);
                            }
                        });
                if (urnElencoVers != null) {
                    elencoRowBean.setString("urn_originale", urnElencoVers.getDsUrn());
                }
                // Recupero lo urn NORMALIZZATO
                urnElencoVers = (ElvUrnElencoVers) CollectionUtils.find(elencoUrn, new Predicate() {
                    @Override
                    public boolean evaluate(final Object object) {
                        return ((ElvUrnElencoVers) object).getTiUrn()
                                .equals(TiUrnElenco.NORMALIZZATO);
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
            log.error("Errore durante il recupero dell'elenco di versamento "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return elencoRowBean;
    }

    public DecCriterioRaggrRowBean getDecCriterioRaggrRowBean(BigDecimal idCriterioRaggr)
            throws EMFError {
        DecCriterioRaggr criterio = evWebHelper.findById(DecCriterioRaggr.class,
                idCriterioRaggr.longValue());
        DecCriterioRaggrRowBean criterioRowBean = new DecCriterioRaggrRowBean();
        try {
            criterioRowBean = (DecCriterioRaggrRowBean) Transform.entity2RowBean(criterio);
        } catch (Exception ex) {
            throw new EMFError("Errore durante il recupero del criterio di raggruppamento", ex);
        }
        return criterioRowBean;
    }

    public ElvVRicElencoVersTableBean getElvVRicElencoVersTableBean(long idUserIam,
            FiltriElenchiVersamento filtri) throws EMFError {
        List<ElvVRicElencoVers> listaElenchiVersamento = evWebHelper
                .retrieveElvVRicElencoVersList(idUserIam, filtri);
        ElvVRicElencoVersTableBean elenchiVersTableBean = new ElvVRicElencoVersTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVRicElencoVers elencoVers : listaElenchiVersamento) {
                    ElvVRicElencoVersRowBean elenchiVersRowBean = (ElvVRicElencoVersRowBean) Transform
                            .entity2RowBean(elencoVers);
                    elenchiVersRowBean.setString("amb_ente_strut",
                            elenchiVersRowBean.getNmAmbiente() + " / "
                                    + elenchiVersRowBean.getNmEnte() + " / "
                                    + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("num_comp", elenchiVersRowBean
                            .getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersRowBean.setBigDecimal("dimensione_byte", elenchiVersRowBean
                            .getNiSizeVersElenco().add(elenchiVersRowBean.getNiSizeAggElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
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
                    elenchiVersRowBean.setString("amb_ente_strut",
                            elenchiVersRowBean.getNmAmbiente() + " / "
                                    + elenchiVersRowBean.getNmEnte() + " / "
                                    + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("num_comp", elenchiVersRowBean
                            .getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersRowBean.setBigDecimal("dimensione_byte", elenchiVersRowBean
                            .getNiSizeVersElenco().add(elenchiVersRowBean.getNiSizeAggElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento", e);
        }
        return elenchiVersTableBean;
    }

    public ElvVRicElencoVersByUdTableBean getElvVRicElencoVersByUdTableBean(long idUserIam,
            FiltriElenchiVersamento filtri) throws EMFError {
        List<ElvVRicElencoVersByUd> listaElenchiVersamento = evWebHelper
                .retrieveElvVRicElencoVersByUdList(idUserIam, filtri);
        ElvVRicElencoVersByUdTableBean elenchiVersTableBean = new ElvVRicElencoVersByUdTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVRicElencoVersByUd elencoVers : listaElenchiVersamento) {
                    ElvVRicElencoVersByUdRowBean elenchiVersRowBean = (ElvVRicElencoVersByUdRowBean) Transform
                            .entity2RowBean(elencoVers);
                    elenchiVersRowBean.setString("amb_ente_strut",
                            elenchiVersRowBean.getNmAmbiente() + " / "
                                    + elenchiVersRowBean.getNmEnte() + " / "
                                    + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("num_comp", elenchiVersRowBean
                            .getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersRowBean.setBigDecimal("dimensione_byte", elenchiVersRowBean
                            .getNiSizeVersElenco().add(elenchiVersRowBean.getNiSizeAggElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento", e);
        }
        return elenchiVersTableBean;
    }

    public ElvVLisElencoVersStatoTableBean getElenchiDaFirmareTableBean(BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idElencoVers, String note,
            String flElencoFisc, List<String> tiGestElenco, Date[] dateCreazioneElencoValidate,
            long idUserIam, ElencoStatusEnum... statiElenco) throws EMFError {
        List<ElvVLisElencoVersStato> listaElenchiVersamento = evWebHelper.getListaElenchiDaFirmare(
                idAmbiente, idEnte, idStrut, idElencoVers, note, flElencoFisc, tiGestElenco,
                dateCreazioneElencoValidate, idUserIam,
                ElencoStatusEnum.getStringEnumsList(statiElenco));
        ElvVLisElencoVersStatoTableBean elenchiVersTableBean = new ElvVLisElencoVersStatoTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVLisElencoVersStato elenco : listaElenchiVersamento) {
                    ElvVLisElencoVersStatoRowBean elenchiVersRowBean = (ElvVLisElencoVersStatoRowBean) Transform
                            .entity2RowBean(elenco);
                    elenchiVersRowBean.setString("amb_ente_strut",
                            elenchiVersRowBean.getNmAmbiente() + " / "
                                    + elenchiVersRowBean.getNmEnte() + " / "
                                    + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("dimensione_byte", elenchiVersRowBean
                            .getNiSizeAggElenco().add(elenchiVersRowBean.getNiSizeVersElenco()));
                    elenchiVersRowBean.setBigDecimal("ni_comp_elenco", elenchiVersRowBean
                            .getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            throw new EMFError(
                    "Errore nel recupero della lista degli elenchi di versamento con stato "
                            + Arrays.toString(statiElenco),
                    e);
        }
        return elenchiVersTableBean;
    }

    public ElvVLisElencoVersStatoTableBean getElenchiDaFirmareTableBean(
            List<BigDecimal> idElencoVersList, long idUserIam) throws EMFError {
        List<ElvVLisElencoVersStato> listaElenchiVersamento = evWebHelper
                .getListaElenchiDaFirmare(idElencoVersList, idUserIam);
        ElvVLisElencoVersStatoTableBean elenchiVersTableBean = new ElvVLisElencoVersStatoTableBean();
        try {
            if (listaElenchiVersamento != null && !listaElenchiVersamento.isEmpty()) {
                for (ElvVLisElencoVersStato elenco : listaElenchiVersamento) {
                    ElvVLisElencoVersStatoRowBean elenchiVersRowBean = (ElvVLisElencoVersStatoRowBean) Transform
                            .entity2RowBean(elenco);
                    elenchiVersRowBean.setString("amb_ente_strut",
                            elenchiVersRowBean.getNmAmbiente() + " / "
                                    + elenchiVersRowBean.getNmEnte() + " / "
                                    + elenchiVersRowBean.getNmStrut());
                    elenchiVersRowBean.setBigDecimal("dimensione_byte", elenchiVersRowBean
                            .getNiSizeAggElenco().add(elenchiVersRowBean.getNiSizeVersElenco()));
                    elenchiVersRowBean.setBigDecimal("ni_comp_elenco", elenchiVersRowBean
                            .getNiCompAggElenco().add(elenchiVersRowBean.getNiCompVersElenco()));
                    elenchiVersTableBean.add(elenchiVersRowBean);
                }
            }
        } catch (Exception e) {
            throw new EMFError(
                    "Errore nel recupero della lista degli elenchi di versamento da firmare", e);
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
                aroUnitaDoc.setTiStatoUdElencoVers(UdDocStatusEnum.NON_SELEZ_SCHED.name());
                /* Registra l'UD nella coda delle UD da elaborare */
                evHelper.insertUdCodaUdDaElab(aroUnitaDoc.getIdUnitaDoc(),
                        UdDocStatusEnum.NON_SELEZ_SCHED);
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
            if (aroDoc.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo.getTime()
                    .getTime()) {
                // MAC#27493
                /* Assegna stato NON_SELEZ_SCHED al documento */
                aroDoc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name());
                /* Registra il documento nella coda dei documenti da elaborare */
                evHelper.insertDocCodaDocDaElab(aroDoc.getIdDoc(),
                        ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED);
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
            if (aroUpdUnitaDoc.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo.getTime()
                    .getTime()) {
                // MAC#27493
                /* Assegna stato NON_SELEZ_SCHED all'aggiornamento metadati */
                aroUpdUnitaDoc
                        .setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                /*
                 * Registra l'aggiornamento metadati nella coda degli aggiornamenti metadati da
                 * elaborare
                 */
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

        /*
         * Elimino lo storico delle sessioni di firma in errore per l'elenco (tabella
         * HSM_ELENCO_SESSIONE_FIRMA)
         */
        List<HsmElencoSessioneFirma> listElencoSessioneFirmaHsm = evHelper
                .retrieveListaElencoInError(elenco, TiEsitoFirmaElenco.IN_ERRORE);
        for (HsmElencoSessioneFirma elencoSessioneFirmaHsm : listElencoSessioneFirmaHsm) {
            genericHelper.removeEntity(elencoSessioneFirmaHsm, true);
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
            throws ParseException, ParerInternalError {
        AroUnitaDoc aroUnitaDoc = genericHelper.findByIdWithLock(AroUnitaDoc.class, idUnitaDoc);
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
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
        String dataInizioParam = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN);
        Date dataInizio = dateFormat.parse(dataInizioParam);
        // controllo e calcolo URN normalizzato
        ElvVLisAllUdByElenco elvVLisAllUdByElenco = evHelper
                .retrieveElvVLisAllUdByElenco(idElencoVers, idUnitaDoc);
        if (!elvVLisAllUdByElenco.getDtVersMax().after(dataInizio)
                && StringUtils.isBlank(elvVLisAllUdByElenco.getCdKeyUnitaDocNormaliz())) {
            // calcola e verifica la chiave normalizzata
            String cdKeyNormalized = MessaggiWSFormat
                    .normalizingKey(aroUnitaDoc.getCdKeyUnitaDoc()); // base
            if (urnHelper.existsCdKeyNormalized(
                    aroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    aroUnitaDoc.getAaKeyUnitaDoc(), aroUnitaDoc.getCdKeyUnitaDoc(),
                    cdKeyNormalized)) {
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

        AroVerIndiceAipUd aroVerIndiceAipUd = evHelper
                .getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
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

    private void calcolaUrnElenco(ElvElencoVer elenco, String nomeStruttura,
            String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm) {
        // sistema (new URN)
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnte, nomeStruttura,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                TiUrnElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnteNorm,
                        nomeStrutturaNorm, sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                TiUrnElenco.NORMALIZZATO);
    }

    /**
     * Esegue la chiusura manuale dell'elenco di versamento passato come parametro in input.
     *
     * @param idUserIam    id user Iam
     * @param idElencoVers id elenco versamenti
     * @param modifica     lista modifiche di tipo ElencoEnums.OpTypeEnum
     * @param note         contenuto note
     *
     * @throws IOException              errore generico
     * @throws NoSuchAlgorithmException errore generico
     * @throws ParerNoResultException   errore generico
     * @throws ParerUserError           errore generico
     * @throws ParseException           errore generico
     * @throws ParerInternalError       errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manualClosingElenco(long idUserIam, BigDecimal idElencoVers,
            List<ElencoEnums.OpTypeEnum> modifica, String note)
            throws NoSuchAlgorithmException, IOException, ParerNoResultException, ParerUserError,
            ParseException, ParerInternalError {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);

        log.info("Inizio processo di chiusura manuale elenco di versamento avente id {}",
                elenco.getIdElencoVers());
        /* Scrivo il motivo di chiusura */
        elenco.setDlMotivoChius("Elenco di versamento chiuso manualmente");
        /* Se ci sono state modifiche sulle note indice elenco, le salvo e scrivo nel log */
        if (!modifica.isEmpty()) {
            evWebHelper.saveNomeDesNote(idUserIam, idElencoVers, elenco.getNmElenco(),
                    elenco.getDsElenco(), note, elenco.getNtElencoChiuso(), modifica);
        }
        /* Imposto la data di chiusura */
        elenco.setDtChius(new Date());
        /* Creo l'indice */
        List<BigDecimal> idUdList = evHelper
                .retrieveUdInElencoByElencoIdList(elenco.getIdElencoVers());
        // Per ogni unità doc dell'elenco
        for (BigDecimal idUd : idUdList) {
            // Apro nuova transazione
            context.getBusinessObject(ElenchiVersamentoEjb.class)
                    .verificaUrnUdElenco(idUd.longValue(), elenco.getIdElencoVers());
        }
        // determina nome ente e struttura normalizzati e non
        OrgEnte ente = elenco.getOrgStrut().getOrgEnte();
        String nomeStruttura = elenco.getOrgStrut().getNmStrut();
        String nomeStrutturaNorm = elenco.getOrgStrut().getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Calcolo e persisto lo urn dell'elenco */
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        log.info("Creazione indice per elenco di versamento avente id '{}'",
                elenco.getIdElencoVers());

        /* Setto l'elenco a stato chiuso */
        elenco.setTiStatoElenco(ElencoStatusEnum.CHIUSO.name());

        // MEV #24534 non devo creare l'indice se TI_VALID_ELENCO = NO_INDICE
        if (!TiValidElenco.NO_INDICE.equals(elenco.getTiValidElenco())) {
            creaIndice(elenco);
        }
        /* Imposto l'utente che ha lanciato la chiusura manuale */
        elenco.setIamUserChiusoElenco(evWebHelper.findById(IamUser.class, idUserIam));

        /* Lo lascio nella coda degli elenchi da elaborare */
        ElvElencoVersDaElab elencoDaElab = evHelper
                .getElvElencoVersDaElabByIdElencoVers(idElencoVers.longValue());
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.CHIUSO.name());
        /* Per ogni unità documentaria appartenente all'elenco */
        for (AroUnitaDoc aroUnitaDoc : elenco.getAroUnitaDocs()) {
            /* Assegna stato IN_ELENCO_CHIUSO all'UD */
            aroUnitaDoc.setTiStatoUdElencoVers(UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
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
        log.info("Fine processo di chiusura manuale elenco di versamento avente id {}",
                elenco.getIdElencoVers());
    }

    /**
     * Crea puntualmente l'indice elenco di versamento per l'elenco passato in input.
     *
     * Il metodo è stato estratto e reso pubblico da
     * {@link #manualClosingElenco(long, BigDecimal, List, String) } per la MEV #24534
     *
     * @param idElencoVers id elenco di versamento
     *
     * @throws ParerNoResultException   se non viene restituito alcun risultato da
     *                                  {@link IndiceElencoVersXsdEjb#createIndex(ElvElencoVer, boolean) }
     * @throws NoSuchAlgorithmException se l'algoritmo di HASH per il calcolo dell'URN non è
     *                                  supportato dalla JVM
     * @throws IOException              in caso di errore nel salvataggio del bytearray dell'indice
     *                                  su DB.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaIndice(long idElencoVers)
            throws ParerNoResultException, NoSuchAlgorithmException, IOException {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        creaIndice(elenco);
    }

    // MEV #31947 - Eliminare il salvataggio degli elenchi di versamento UD
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public byte[] generaIndice(long idElencoVers)
            throws ParerNoResultException, NoSuchAlgorithmException, IOException {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        return iejEjb.createIndex(elenco, true);
    }

    private void creaIndice(ElvElencoVer elenco)
            throws ParerNoResultException, NoSuchAlgorithmException, IOException {
        byte[] indexFile = iejEjb.createIndex(elenco, true);
        /* Registro nella tabella ElvFileElencoVers */
        ElvFileElencoVer elvFileElencoVers = evHelper.storeFileIntoElenco(elenco, indexFile,
                FileTypeEnum.INDICE.name());
        /* Setto i campi relativi all'hash */
        // Calcolo l'hash SHA-256 del file indiceElencoVers.xml
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(elenco.getOrgStrut().getNmStrut());
        csv.setEnte(elenco.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(elenco.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // Aggiorno l'elenco definendo l'hash dell'indice, l'algoritmo usato per il calcolo hash
        // (=SHA-256),
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        csv.setSistemaConservazione(sistemaConservazione);
        // l'encoding del hash (=hexBinary) e la versione del XSD (=2.0) con cui è creato l'indice
        // dell'elenco
        String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(csv);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(csv, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        urnHelper.salvaUrnElvFileElencoVers(elvFileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndice(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(elvFileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndice(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
    }

    /**
     * Esegue la validazione dell'elenco di versamento passato come parametro in input, a seguito
     * della chiusura manuale.
     *
     * @param idUserIam    id user iam
     * @param idElencoVers id elenco versamento
     *
     * @throws ParseException errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void validElenco(long idUserIam, BigDecimal idElencoVers) throws ParseException {
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        log.info("Inizio processo di validazione elenco di versamento avente id {}",
                elenco.getIdElencoVers());
        /* Assumo lock esclusivo sull'elenco */
        evHelper.lockElenco(elenco);
        /* Controllo se almeno una unità doc appartenente all'elenco e' annullata */
        boolean annullata = evHelper.checkUdAnnullataByElenco(elenco);
        if (!annullata) { // ud non annullata
            IamUser user = evWebHelper.findById(IamUser.class, idUserIam);
            /*
             * Aggiorno l'elenco valorizzando la data di firma, il tipo di validazione e la modalità
             * con i valori definiti dall'elenco.
             */
            elenco.setDtFirmaIndice(new Date());
            /* Se i valori nell'elenco sono nulli leggo i valori dal criterio di raggruppamento */
            if (elenco.getTiValidElenco() == null || elenco.getTiModValidElenco() == null) {
                elenco.setTiValidElenco(TiValidElenco
                        .valueOf(elenco.getDecCriterioRaggr().getTiValidElenco().name()));
                elenco.setTiModValidElenco(TiModValidElenco
                        .valueOf(elenco.getDecCriterioRaggr().getTiModValidElenco().name()));
            }

            // EVO 19304
            registraStatoElencoVersamento(idElencoVers, "VALIDA_INDICE_ELENCO_VERS",
                    "Validazione indice elenco in cui non sono presenti unità documentarie annullate",
                    TiStatoElenco.VALIDATO, user.getNmUserid());
            /*
             * Assegno all'elenco stato = VALIDATO e lo lascio nella coda degli elenchi da elaborare
             * assegnando stato = validato
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

        log.info("Fine processo di validazione elenco di versamento avente id {}",
                elenco.getIdElencoVers());
    }

    public OrgStrutRowBean getOrgStrutRowBeanWithAmbienteEnte(BigDecimal idStrut) {
        OrgStrut strut = evWebHelper.findById(OrgStrut.class, idStrut.longValue());
        OrgStrutRowBean rb = new OrgStrutRowBean();
        if (strut != null) {
            try {
                rb = (OrgStrutRowBean) Transform.entity2RowBean(strut);
                rb.setBigDecimal("id_ambiente",
                        new BigDecimal(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente()));
                rb.setString("nm_ambiente", strut.getOrgEnte().getOrgAmbiente().getNmAmbiente());
                rb.setString("nm_ente", strut.getOrgEnte().getNmEnte());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return rb;
    }

    /**
     * Data una lista di elenchi di versamento ed una dataChiusura impostati restituisce true o
     * false a seconda che tutti gli elenchi non presenti nella lista passata, con data chiusura
     * inferiore a quella passata, siano firmati.
     *
     * @param elencoTableBean bean ElvVLisElencoVersStatoTableBean
     * @param dataChiusura    data chiusura
     * @param idStrut         id struttura
     *
     * @return true/false
     */
    public boolean areAllElenchiNonPresentiFirmati(ElvVLisElencoVersStatoTableBean elencoTableBean,
            Date dataChiusura, BigDecimal idStrut) {
        List<BigDecimal> idElencoVersSelezionatiList = new ArrayList<>();
        for (ElvVLisElencoVersStatoRowBean elencoRowBean : elencoTableBean) {
            idElencoVersSelezionatiList.add(elencoRowBean.getIdElencoVers());
        }
        return evWebHelper.areAllElenchiNonPresentiFirmati(idElencoVersSelezionatiList,
                dataChiusura, idStrut);
    }

    /**
     * Controlla se su DB esiste già, per quella struttura, un elenco che abbia il nome passato come
     * parametro
     *
     * @param nmElenco nome elenco
     * @param idStrut  id struttura
     *
     * @return true/false
     */
    public boolean existNomeElenco(String nmElenco, BigDecimal idStrut) {
        return evWebHelper.existNomeElenco(nmElenco, idStrut);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNomeDesNote(Long idUserIam, BigDecimal idElencoVers, String nmElenco,
            String dsElenco, String ntIndiceElenco, String ntElencoChiuso,
            List<ElencoEnums.OpTypeEnum> operList, String tiGestElenco, String tiValidElenco,
            String tiModValidElenco) {
        evWebHelper.saveNomeDesNote(idUserIam, idElencoVers, nmElenco, dsElenco, ntIndiceElenco,
                ntElencoChiuso, operList);
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers.longValue());
        elenco.setTiGestElenco(tiGestElenco);
        elenco.setTiValidElenco(TiValidElenco.valueOf(tiValidElenco));
        elenco.setTiModValidElenco(TiModValidElenco.valueOf(tiModValidElenco));
    }

    /**
     * Rimuove l'appartenenza di unità documentarie/documenti aggiunti all'elenco di versamento in
     * questione
     *
     * @param idElencoVers   id elenco versamento
     * @param idDocsToRemove id documento da rimuovere
     * @param idUserIam      id user Iam
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteUdDocFromElencoVersamento(Long idElencoVers, Set<BigDecimal> idDocsToRemove,
            long idUserIam) {
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
            if (documento.getTiCreazione()
                    .equals(CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name())) {
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
            if (documentoPerAggiuntaDocumento.getAroUnitaDoc().getDtAnnul()
                    .getTime() == fineDelMondo.getTime().getTime()) {
                // MAC#27493
                /* Assegno stato NON_SELEZ_SCHED al documento */
                documentoPerAggiuntaDocumento
                        .setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED.name());
                /* Registro il documento nella coda dei documenti da elaborare */
                evHelper.insertDocCodaDocDaElab(documentoPerAggiuntaDocumento.getIdDoc(),
                        ElencoEnums.DocStatusEnum.NON_SELEZ_SCHED);
                // end MAC#27493
            }

            /* Aggiorno l'elenco di versamento */
            log.info("Dimensione elenco di versamento {} pari a {} KB", elenco.getIdElencoVers(),
                    elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()));
            /*
             * Calcolo la somma delle dimensioni dei componenti inclusi nel documento per
             * AGGIUNTA_DOCUMENTO
             */
            BigDecimal compDocSize = new BigDecimal(0);
            for (AroStrutDoc strutDoc : documentoPerAggiuntaDocumento.getAroStrutDocs()) {
                for (AroCompDoc compDoc : strutDoc.getAroCompDocs()) {
                    compDocSize = compDocSize
                            .add(compDoc.getNiSizeFileCalc() != null ? compDoc.getNiSizeFileCalc()
                                    : new BigDecimal("0"));
                }
            }

            log.info("Dimensioni totali del documento {} da rimuovere pari a {} KB",
                    documentoPerAggiuntaDocumento.getIdDoc(), compDocSize);

            /* Ricalcolo i valori */
            elenco.setNiUnitaDocModElenco(
                    new BigDecimal(evHelper.contaUdModificatePerDocAggiunti(idElencoVers)));
            elenco.setNiDocAggElenco(new BigDecimal(evHelper.contaDocAggiunti(idElencoVers)));
            Object[] obj = evHelper.contaCompPerDocAggiunti(idElencoVers);
            elenco.setNiCompAggElenco(new BigDecimal((Long) obj[0]));
            elenco.setNiSizeAggElenco(obj[1] != null ? (BigDecimal) obj[1] : new BigDecimal("0"));

            log.info("NUOVA dimensione elenco di versamento {} pari a {} KB",
                    elenco.getIdElencoVers(),
                    elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()));

            /* Registro sul log delle operazioni */
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.RIMUOVI_DOC_ELENCO.name(), documentoPerAggiuntaDocumento,
                    null);
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
                    unitaDoc.setTiStatoUdElencoVers(UdDocStatusEnum.NON_SELEZ_SCHED.name());
                    /*
                     * Registro l'unità documentaria nella coda delle unità documentarie da
                     * elaborare
                     */
                    evHelper.insertUdCodaUdDaElab(unitaDoc.getIdUnitaDoc(),
                            UdDocStatusEnum.NON_SELEZ_SCHED);
                    // end MAC#27493
                }

                /* Aggiorno l'elenco di versamento */
                log.info("Dimensione elenco di versamento {} pari a {} KB",
                        elenco.getIdElencoVers(),
                        elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()));

                /*
                 * Calcolo la somma delle dimensioni dei componenti inclusi nell'unità documentaria
                 * per VERSAMENTO_UNITA_DOC
                 */
                BigDecimal compUdVersamentoSize = new BigDecimal(0);
                /*
                 * Considero tutti e soli i documenti di tipo VERSAMENTO_UNITA_DOC dell'unità
                 * documentaria
                 */
                for (AroDoc docVersamento : unitaDoc.getAroDocs()) {
                    if (docVersamento.getTiCreazione()
                            .equals(CostantiDB.TipoCreazioneDoc.VERSAMENTO_UNITA_DOC.name())) {
                        for (AroStrutDoc strutDoc : docVersamento.getAroStrutDocs()) {
                            for (AroCompDoc compDoc : strutDoc.getAroCompDocs()) {
                                compUdVersamentoSize = compUdVersamentoSize
                                        .add(compDoc.getNiSizeFileCalc() != null
                                                ? compDoc.getNiSizeFileCalc()
                                                : new BigDecimal("0"));
                            }
                        }
                    }
                }

                log.info("Dimensioni totali dell'unità documentaria {} da rimuovere pari a {} KB",
                        unitaDoc.getIdUnitaDoc(), compUdVersamentoSize);

                /* Ricalcolo i valori */
                elenco.setNiUnitaDocVersElenco(
                        new BigDecimal(evHelper.contaUdVersate(idElencoVers)));
                elenco.setNiDocVersElenco(new BigDecimal(evHelper.contaDocVersati(idElencoVers)));
                Object[] obj = evHelper.contaCompVersati(idElencoVers);
                elenco.setNiCompVersElenco(new BigDecimal((Long) obj[0]));
                elenco.setNiSizeVersElenco(
                        obj[1] != null ? (BigDecimal) obj[1] : new BigDecimal("0"));

                log.info("NUOVA dimensione elenco di versamento {} pari a {} KB",
                        elenco.getIdElencoVers(),
                        elenco.getNiSizeAggElenco().add(elenco.getNiSizeVersElenco()));

                /* Registro sul log delle operazioni */
                evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                        ElencoEnums.OpTypeEnum.RIMUOVI_UD_ELENCO.name(), (AroDoc) null, unitaDoc);

                /* Inserisco l'UD tra quelle già trattate */
                idUnitaDocsTrattate.add(documentoPerVersamentoUd.getAroUnitaDoc().getIdUnitaDoc());
            }
        }
        /*
         * Se l'elenco non contiene più elementi (componenti e/o aggiornamenti), lo elimino e scrivo
         * nel log
         */
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
     * @param idElencoVers   id elenco di versamento
     * @param idUpdsToRemove lista elementi da rimuovere
     * @param idUserIam      id user Iam
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteUpdFromElencoVersamento(Long idElencoVers, Set<BigDecimal> idUpdsToRemove,
            long idUserIam) {
        log.info("Rimuovo gli aggiornamenti dall'elenco di versamento...");
        ElvElencoVer elenco = evWebHelper.findById(ElvElencoVer.class, idElencoVers);
        List<AroUpdUnitaDoc> aggiornamentiMetadati = new ArrayList<>();
        /* Setto la data di unità doc non annullata */
        Calendar fineDelMondo = Calendar.getInstance();
        fineDelMondo.set(2444, 11, 31, 0, 0, 0);
        fineDelMondo.set(Calendar.MILLISECOND, 0);
        /* Diversifico i documenti considerati */
        for (BigDecimal idUpdToRemove : idUpdsToRemove) {
            AroUpdUnitaDoc aggiornamento = evWebHelper.findById(AroUpdUnitaDoc.class,
                    idUpdToRemove);
            if (aggiornamento.getTipoUpdUnitaDoc()
                    .equals(CostantiDB.TipoUpdUnitaDoc.METADATI.name())) {
                aggiornamentiMetadati.add(aggiornamento);
            }
        }

        for (AroUpdUnitaDoc aggiornamentoMetadati : aggiornamentiMetadati) {
            /* Rimuovo l'appartenza dell'aggiornamento dall'elenco di versamento */
            aggiornamentoMetadati.setElvElencoVer(null);
            /* Se l'aggiornamento appartiene ad una unità documentaria non annullata */
            if (aggiornamentoMetadati.getAroUnitaDoc().getDtAnnul().getTime() == fineDelMondo
                    .getTime().getTime()) {
                // MAC#27493
                /* Assegno stato NON_SELEZ_SCHED all'aggiornamento */
                aggiornamentoMetadati
                        .setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
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

        /*
         * Se l'elenco non contiene più elementi (componenti e/o aggiornamenti), lo elimino e scrivo
         * nel log
         */
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

    public boolean isIndiceElencoVersOnOs(long idElencoVers) {
        List<ElvFileElencoVer> elvFileList = evHelper.retrieveFileIndiceElenco2(idElencoVers,
                new String[] {
                        "INDICE" });
        if (!elvFileList.isEmpty()) {
            return objectStorageService
                    .isIndiceElencoOnOs(elvFileList.get(0).getIdFileElencoVers());
        } else {
            return false;
        }

    }

    public void streamOutFileIndiceElenco(ZipOutputStream out, String fileNamePrefix,
            String fileNameSuffix, long idElencoVers, FileTypeEnum... fileTypes)
            throws IOException {
        List<ElvFileElencoVer> retrieveFileIndiceElenco = evHelper.retrieveFileIndiceElenco2(
                idElencoVers, FileTypeEnum.getStringEnumsList(fileTypes));
        for (ElvFileElencoVer elvFileElencoVer : retrieveFileIndiceElenco) {
            FileTypeEnum fileType = FileTypeEnum.valueOf(elvFileElencoVer.getTiFileElencoVers());
            fileNamePrefix = StringUtils.defaultString(fileNamePrefix).replace(" ", "_");
            fileNameSuffix = StringUtils.defaultString(fileNameSuffix).replace(" ", "_");

            boolean requiresDifferentRead = true;
            // MEV#15967 - Attivazione della firma Xades e XadesT
            String fileExtension = null;
            String tiFirma = elvFileElencoVer.getTiFirma();
            if (tiFirma != null && tiFirma.equals(ElencoEnums.TipoFirma.XADES.name())) {
                fileExtension = ".xml";
            } else {
                fileExtension = fileType.getFileExtension();
            }
            //
            switch (fileType) {
            case INDICE:
            case INDICE_FIRMATO:
            case MARCA_INDICE:
            case FIRMA:
                // Niente da fare, lo metto per gestire tutti i tipi
                fileNameSuffix = StringUtils.defaultString(fileNameSuffix).replace("_firma", "");
                break;
            case MARCA_FIRMA:
                fileNameSuffix += "_firma";
                break;
            case ELENCO_INDICI_AIP:
                requiresDifferentRead = false;
                fileNamePrefix = "ElencoIndiciAIP_";
                break;
            case FIRMA_ELENCO_INDICI_AIP:
                requiresDifferentRead = false;
                fileNamePrefix = "FirmaElencoIndiciAIP_";
                break;
            case MARCA_FIRMA_ELENCO_INDICI_AIP:
                requiresDifferentRead = false;
                fileNamePrefix = "MarcaElencoIndiciAIP_";
                break;
            default:
                throw new AssertionError(fileType.name());
            }

            byte[] blFileElencoVers = elvFileElencoVer.getBlFileElencoVers();
            if (blFileElencoVers == null) {
                if (requiresDifferentRead) {
                    blFileElencoVers = objectStorageService
                            .getObjectElencoIndici(elvFileElencoVer.getIdFileElencoVers());
                } else {
                    blFileElencoVers = objectStorageService
                            .getObjectElencoIndiciAip(elvFileElencoVer.getIdFileElencoVers());
                }
            }

            addEntryToZip(out, blFileElencoVers, fileNamePrefix + fileNameSuffix + fileExtension);
        }
        out.flush();
    }

    public void addEntryToZip(ZipOutputStream out, byte[] file, String filename)
            throws IOException {
        byte[] data = new byte[1024];
        try (InputStream bis = new ByteArrayInputStream(file)) {
            int count;
            out.putNextEntry(new ZipEntry(filename));
            while ((count = bis.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            out.closeEntry();
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
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        csv.setSistemaConservazione(sistemaConservazione);
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaUrnPartVersatore(csv);
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(csv, true,
                Costanti.UrnFormatter.VERS_FMT_STRING);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        urnHelper.salvaUrnElvFileElencoVers(elvFileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndiceFirmato(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(elvFileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndiceFirmato(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
        /* DA RIMUOVERE 19304 */
        IamUser user = evWebHelper.findById(IamUser.class, idUtente);
        elenco.setIamUserFirmaIndice(user);
        elenco.setDtFirmaIndice(signatureDate);
        /* DA RIMUOVERE 19304 */

        // EVO 19304
        registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "FIRMA_INDICE_ELENCO_VERS",
                "Firma indice elenco in cui non sono presenti unità documentarie annullate",
                TiStatoElenco.VALIDATO, user.getNmUserid());

        elenco.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
        /* Cambio stato a unità documentarie e documenti associati all'elenco */
        for (AroUnitaDoc aroUnitaDoc : elenco.getAroUnitaDocs()) {
            aroUnitaDoc.setTiStatoUdElencoVers(UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
        }
        for (AroDoc aroDoc : elenco.getAroDocs()) {
            aroDoc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_VALIDATO.name());
        }
        for (AroUpdUnitaDoc aroUpdUnitaDoc : elenco.getAroUpdUnitaDocs()) {
            aroUpdUnitaDoc.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO);
        }
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersDaElab elencoDaElab = evHelper
                .getElvElencoVersDaElabByIdElencoVers(idElencoVers);
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
        elencoDaElab.setTsStatoElenco(new Date());
        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUtente,
                ElencoEnums.OpTypeEnum.VALIDAZIONE_ELENCO.name());
    }

    /**
     * Metodo di salvataggio file firmato dell'elenco indici AIP sul database
     *
     * @param idElencoVers    id elenco di versamento
     * @param fileFirmato     byte[] file firmato
     * @param signatureDate   data firma
     * @param idUtente        id utente
     * @param backendMetadata tipo backend
     * @param tipoFirma       tipo firma (XADES o CADES)
     *
     * @return ElvFileElencoVer
     *
     * @throws IOException              errore generico
     * @throws NoSuchAlgorithmException errore generico
     */
    public ElvFileElencoVer storeFirmaElencoIndiceAip(Long idElencoVers, byte[] fileFirmato,
            Date signatureDate, long idUtente, BackendStorage backendMetadata,
            ElencoEnums.TipoFirma tipoFirma) throws NoSuchAlgorithmException, IOException {
        ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        evHelper.lockElenco(elenco);

        final OrgStrut orgStrut = elenco.getOrgStrut();
        final OrgEnte orgEnte = orgStrut.getOrgEnte();
        final String nmStrut = orgStrut.getNmStrut();
        final String nmEnte = orgEnte.getNmEnte();
        final String nmAmbiente = orgEnte.getOrgAmbiente().getNmAmbiente();
        String hash = new HashCalculator().calculateHashSHAX(fileFirmato, TipiHash.SHA_256)
                .toHexBinary();
        final String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);

        ElvFileElencoVer fileElencoVers = new ElvFileElencoVer();
        fileElencoVers.setCdVerXsdFile(Costanti.VERSIONE_ELENCO_INDICE_AIP);
        fileElencoVers.setTiFileElencoVers(FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());
        // MEV#30397
        if (backendMetadata.isDataBase()) {
            fileElencoVers.setBlFileElencoVers(fileFirmato);
        }
        // end MEV#30397
        fileElencoVers.setTiFirma(tipoFirma.name());
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
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndiciAIPFirmati(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
                MessaggiWSFormat.formattaUrnElencoIndiciAIPFirmati(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);

        elenco.setDtFirmaElencoIxAip(signatureDate);
        elenco.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name());

        // MEV#30206
        if (elenco.getTiGestElenco() == null) {
            if (elenco.getDecCriterioRaggr().getTiGestElencoCriterio() != null) {
                elenco.setTiGestElenco(elenco.getDecCriterioRaggr().getTiGestElencoCriterio());
            } else {
                boolean elencoStandard = elenco.getFlElencoStandard().equals("1");
                boolean elencoFiscale = elenco.getFlElencoFisc().equals("1");
                if (!elencoStandard && !elencoFiscale) {
                    elenco.setTiGestElenco(configurationHelper.getValoreParamApplicByStrut(
                            CostantiDB.ParametroAppl.TI_GEST_ELENCO_NOSTD,
                            BigDecimal.valueOf(orgEnte.getOrgAmbiente().getIdAmbiente()),
                            BigDecimal.valueOf(orgStrut.getIdStrut())));
                } else if (elencoFiscale) {
                    elenco.setTiGestElenco(configurationHelper.getValoreParamApplicByStrut(
                            CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_FISC,
                            BigDecimal.valueOf(orgEnte.getOrgAmbiente().getIdAmbiente()),
                            BigDecimal.valueOf(orgStrut.getIdStrut())));
                } else {
                    elenco.setTiGestElenco(configurationHelper.getValoreParamApplicByStrut(
                            CostantiDB.ParametroAppl.TI_GEST_ELENCO_STD_NOFISC,
                            BigDecimal.valueOf(orgEnte.getOrgAmbiente().getIdAmbiente()),
                            BigDecimal.valueOf(orgStrut.getIdStrut())));
                }
            }
        }
        // end MEV#30206

        // EVO 19304
        IamUser user = userHelper.findUserById(idUtente);
        registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "FIRMA_ELENCO_INDICI_AIP",
                "Firma elenco indice AIP", TiStatoElenco.ELENCO_INDICI_AIP_FIRMATO,
                user.getNmUserid());

        /*
         * Cambio stato a unità documentarie e documenti associati all'elenco e aggiornamenti
         * metadati
         */
        List<String> statiUdDocDaPortareAipFirmato = new ArrayList<>(
                Arrays.asList(UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO.name()));
        List<AroUpdUDTiStatoUpdElencoVers> statiUpdDaPortareAipFirmato = new ArrayList<>(
                Arrays.asList(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO));
        elabElencoIndiceAipEjb.updateUnitaDocElencoIndiceAIP(idElencoVers,
                statiUdDocDaPortareAipFirmato,
                UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name());
        elabElencoIndiceAipEjb.updateDocumentiElencoIndiceAIP(idElencoVers,
                statiUdDocDaPortareAipFirmato,
                UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name());
        elabElencoIndiceAipEjb.updateAggiornamentiElencoIndiceAIP(idElencoVers,
                statiUpdDaPortareAipFirmato,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO);
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersDaElab elencoDaElab = evHelper
                .getElvElencoVersDaElabByIdElencoVers(idElencoVers);
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name());
        elencoDaElab.setTsStatoElenco(new Date());

        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, orgStrut, idUtente,
                ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());

        return fileElencoVers;
    }

    @Asynchronous
    public Future<SigningResponse> completaFirmaElenchiIndiciAipAsync(BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, long idUtente, boolean isSoloSigillo) {

        SigningResponse result = SigningResponse.OK_SECONDA_FASE;
        try {
            completaFirmaElenchiIndiciAip(idAmbiente, idEnte, idStrut, idUtente, isSoloSigillo);
        } catch (ParerUserError ex) {
            result = SigningResponse.ERROR_COMPLETAMENTO_FIRMA;
        }
        return new AsyncResult<>(result);
    }

    /**
     * Metodo di marcatura file firmati di elenchi indici AIP, dati i parametri utilizzati in fase
     * di firma
     *
     * @param idAmbiente    id ambiente
     * @param idEnte        id ente
     * @param idStrut       id struttura
     * @param idUtente      id utente
     * @param isSoloSigillo Gestione solo sigillo o firma e sigillo insieme
     *
     * @throws ParerUserError errore generico
     */
    public void completaFirmaElenchiIndiciAip(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, long idUtente, boolean isSoloSigillo) throws ParerUserError {
        try {
            /*
             * MODIFICATO PER IL SIGILLO: MEV#27824 - Introduzione del JOB per l'apposizione del
             * sigillo elettronico
             *
             * La logica prevede che se viene chiamato dal job sigillo vuol dire che si vogliono
             * processare soltanto gli elenchi con gestione SIGILLO o SIGILLO_MARCA, altrimenti se
             * richiamati da interfaccia utente per la firma manuale li processa tutti i tipi di
             * gestione, sia di SIGILLO che di FIRMA
             */
            ArrayList<String> tipiGestione = new ArrayList<>();
            tipiGestione.add(ElencoEnums.GestioneElencoEnum.SIGILLO.name());
            if (!isSoloSigillo) {
                tipiGestione.add(ElencoEnums.GestioneElencoEnum.FIRMA.name());
            }
            List<ElvVLisElencoDaMarcare> elenchiCompletati = evHelper
                    .retrieveElenchiIndiciAipDaMarcare(idAmbiente, idEnte, idStrut, idUtente,
                            tipiGestione);
            log.info("Elenchi da completare [{}]", elenchiCompletati.size());
            gestioneCompletamentoFirmaElenchiIndiciAip(null, elenchiCompletati, idUtente);
            Set<Long> struts = new HashSet<>();
            // MODIFICATO PER IL SIGILLO: MEV#27824 - Introduzione del JOB per l'apposizione del
            // sigillo elettronico
            tipiGestione.clear();
            tipiGestione.add(ElencoEnums.GestioneElencoEnum.MARCA_SIGILLO.name());
            if (!isSoloSigillo) {
                tipiGestione.add(ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name());
            }
            List<ElvVLisElencoDaMarcare> elenchiDaMarcare = evHelper
                    .retrieveElenchiIndiciAipDaMarcare(idAmbiente, idEnte, idStrut, idUtente,
                            tipiGestione);
            log.info("Elenchi da completare [{}]", elenchiDaMarcare.size());
            gestioneCompletamentoFirmaElenchiIndiciAip(struts, elenchiDaMarcare, idUtente);
            // Presente un requisito che richiede la marcatura di tutti Elenchi Indici AIP nello
            // stato ERR_MARCA
            List<ElvVLisElencoVersStato> listaElenchiErrati = evWebHelper.getListaElenchiDaFirmare(
                    null, null, null, null, null, null, null, null, idUtente,
                    ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());
            log.info("Elenchi errati da completare [{}]", listaElenchiErrati.size());
            gestioneCompletamentoElenchiIndiciAipErrati(struts, listaElenchiErrati, idUtente);
        } catch (ParerInternalError ex) {
            log.error("Errore durante marcatura: ", ex);
            throw new ParerUserError("Errore durante la fase di completamento della firma");
        }
    }

    private void gestioneCompletamentoElenchiIndiciAipErrati(Set<Long> struts,
            List<ElvVLisElencoVersStato> elenchiDaMarcare, long idUtente)
            throws ParerInternalError {
        for (ElvVLisElencoVersStato elvVLisElencoDaMarcare : elenchiDaMarcare) {
            try {
                if (struts != null) {
                    struts.add(elvVLisElencoDaMarcare.getIdStrut().longValue());
                }
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .gestioneCompletamentoElenchiIndiciAip(
                                elvVLisElencoDaMarcare.getIdElencoVers().longValue(),
                                ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name(), idUtente);
            } catch (ParerInternalError ex) {
                // Errore di acquisizione marca temporale, deve essere intercettata e gestita, poi
                // terminare lo use case
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .saveErroreMarcaElencoIndiceAip(
                                elvVLisElencoDaMarcare.getIdElencoVers().longValue(), idUtente);
                throw ex;
            }
        }
    }

    private void gestioneCompletamentoFirmaElenchiIndiciAip(Set<Long> struts,
            List<ElvVLisElencoDaMarcare> elenchiDaMarcare, long idUtente)
            throws ParerInternalError {
        for (ElvVLisElencoDaMarcare elvVLisElencoDaMarcare : elenchiDaMarcare) {
            try {
                if (struts != null) {
                    struts.add(elvVLisElencoDaMarcare.getIdStrut().longValue());
                }
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .gestioneCompletamentoElenchiIndiciAip(
                                elvVLisElencoDaMarcare.getIdElencoVers().longValue(),
                                elvVLisElencoDaMarcare.getTiGestElenco(), idUtente);
            } catch (ParerInternalError ex) {
                // Errore di acquisizione marca temporale, deve essere intercettata e gestita, poi
                // terminare lo use case
                context.getBusinessObject(ElenchiVersamentoEjb.class)
                        .saveErroreMarcaElencoIndiceAip(
                                elvVLisElencoDaMarcare.getIdElencoVers().longValue(), idUtente);
                throw ex;
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestioneCompletamentoElenchiIndiciAip(long idElencoVers, String tiGestElenco,
            long idUtente) throws ParerInternalError {
        final ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        evHelper.lockElenco(elenco);

        ElencoEnums.GestioneElencoEnum tiGestioneEnum = ElencoEnums.GestioneElencoEnum
                .valueOf(tiGestElenco);
        switch (tiGestioneEnum) {
        // MODIFICATO PER IL SIGILLO: MEV#27824 - Introduzione del JOB per l'apposizione del sigillo
        // elettronico
        // FIRMA: --> solo firma online
        case FIRMA:
            // SIGILLO: --> solo firma tramite Sigillo
        case SIGILLO:
            /*
             * Aggiunto un controllo per determinare se l'elenco indice AIP si trova realmente nello
             * stato desiderato In caso negativo scrive un log di warning
             */
            if (elenco.getTiStatoElenco()
                    .equals(ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name())) {
                List<String> statiUdDocDaCompletare = new ArrayList<>(Arrays
                        .asList(UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name()));
                elabElencoIndiceAipEjb.setCompletato(elenco, statiUdDocDaCompletare, idUtente,
                        tiGestioneEnum, tiGestioneEnum.equals(FIRMA) ? Constants.FUNZIONALITA_ONLINE
                                : Constants.NM_AGENTE_JOB_SACER);
                // EVO 19304
                evWebEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                        "MARCA_ELENCO_INDICI_AIP", "Gestione elenco = " + tiGestioneEnum.name(),
                        TiStatoElenco.COMPLETATO, null);
            } else {
                log.warn(
                        "Impossibile completare l'elenco indice AIP con id {}, NON è in stato ELENCO_INDICI_AIP_FIRMATO",
                        elenco.getIdElencoVers());
            }
            break;
        // MODIFICATO PER IL SIGILLO: MEV#27824 - Introduzione del JOB per l'apposizione del sigillo
        // elettronico
        // MARCA_FIRMA: --> marcatura online
        case MARCA_FIRMA:
            // MARCA_SIGILLO: --> marcatura tramite Sigillo
        case MARCA_SIGILLO:
            /*
             * Aggiunto un controllo per determinare se l'elenco indice AIP si trova realmente in
             * uno degli stati desiderati In caso negativo scrive un log di warning
             */
            if (isElencoToMark(elenco)) {
                // MEV#30397
                BackendStorage backendIndiciAip = objectStorageService
                        .lookupBackendElenchiIndiciAip(elenco.getOrgStrut().getIdStrut());

                ElvFileElencoVer firmaElencoIxAip = evHelper.getFileIndiceElenco(idElencoVers,
                        FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());

                byte[] firmaElencoIndiciAip = firmaElencoIxAip.getBlFileElencoVers();
                if (firmaElencoIndiciAip == null) {
                    firmaElencoIndiciAip = objectStorageService
                            .getObjectElencoIndiciAip(firmaElencoIxAip.getIdFileElencoVers());
                }
                // end MEV#30397

                // MEV#15967 - Attivazione della firma Xades e XadesT
                /*
                 * Nel caso la firma sia di tipo XADES non è richiesta la marca temporale in quanto
                 * già inclusa nell'XML firmato.
                 */
                if (amministrazioneEjb
                        .getTipoFirmaPerStruttura(new BigDecimal(elenco.getOrgStrut().getIdStrut()))
                        .equals(ElencoEnums.TipoFirma.XADES)) {
                    log.debug("Marca detatched non necessaria per Xades");
                    // MEV#15967 - Attivazione della firma Xades e XadesT
                    impostaStatoCompletatoElencoIndiceAip(elenco, idUtente, tiGestioneEnum,
                            tiGestioneEnum.equals(MARCA_FIRMA) ? Constants.FUNZIONALITA_ONLINE
                                    : Constants.NM_AGENTE_JOB_SACER);
                    //
                } else {
                    log.debug("Marca detatched non necessaria per Cades");
                    // MAC#35254 - Correzione delle anomalie nella fase di marcatura temporale
                    // embedded negli elenchi
                    // indici aip UD
                    impostaStatoCompletatoElencoIndiceAip(elenco, idUtente, tiGestioneEnum,
                            tiGestioneEnum.equals(MARCA_FIRMA) ? Constants.FUNZIONALITA_ONLINE
                                    : Constants.NM_AGENTE_JOB_SACER);
                    // MAC#35254 - Correzione delle anomalie nella fase di marcatura temporale
                    // embedded negli elenchi
                    // indici aip UD
                    // Eliminato tutto il codice che richiamava il CryptoInvoker
                }
            } else {
                log.warn(
                        "Impossibile completare l'elenco indice AIP con id {}, NON è nello stato di quelli marcabili",
                        elenco.getIdElencoVers());
            }
            break;

        case NO_FIRMA:
            break;
        default:
            throw new AssertionError(tiGestioneEnum.name());
        }
    }

    private boolean isElencoToMark(ElvElencoVer elenco) {
        String elencoFirmato = ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name();
        String elencoErroreMarca = ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name();
        return elenco.getTiStatoElenco().equals(elencoFirmato)
                || elenco.getTiStatoElenco().equals(elencoErroreMarca);
    }

    private boolean isElencoFirmato(ElvElencoVer elenco, DecCriterioRaggr criterio) {
        String elencoFirmaInCorso = ElencoStatusEnum.FIRMA_IN_CORSO.name();
        String elencoValidato = ElencoStatusEnum.VALIDATO.name();
        String elencoFirmeVerificateDtVers = ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS.name();
        String elencoInCodaIndiceAip = ElencoStatusEnum.IN_CODA_INDICE_AIP.name();
        String elencoIndiciAipGenerati = ElencoStatusEnum.INDICI_AIP_GENERATI.name();
        String elencoElencoIndiciAipCreato = ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name();
        String elencoElencoIndiciAipFirmaInCorso = ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMA_IN_CORSO
                .name();
        String elencoElencoIndiciAipFirmato = ElencoStatusEnum.ELENCO_INDICI_AIP_FIRMATO.name();
        String elencoElencoIndiciAipErrMarca = ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name();
        String elencoCompletato = ElencoStatusEnum.COMPLETATO.name();

        boolean firma = (elenco.getTiValidElenco() != null)
                ? elenco.getTiValidElenco().name()
                        .equals(ElencoEnums.GestioneElencoEnum.FIRMA.name())
                : criterio.getTiValidElenco().name().equals(TiValidElenco.FIRMA.name());

        return (elenco.getTiStatoElenco().equals(elencoFirmaInCorso)
                || elenco.getTiStatoElenco().equals(elencoValidato)
                || elenco.getTiStatoElenco().equals(elencoFirmeVerificateDtVers)
                || elenco.getTiStatoElenco().equals(elencoInCodaIndiceAip)
                || elenco.getTiStatoElenco().equals(elencoIndiciAipGenerati)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipCreato)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipFirmaInCorso)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipFirmato)
                || elenco.getTiStatoElenco().equals(elencoElencoIndiciAipErrMarca)
                || elenco.getTiStatoElenco().equals(elencoCompletato)) && firma;
    }

    private ElvFileElencoVer saveMarcaElencoIndiceAip(ElvElencoVer elenco, byte[] marcaTemporale,
            long idUtente, BackendStorage backendMetadata,
            ElencoEnums.GestioneElencoEnum tiGestioneEnum, String modalitaLog) {
        final OrgStrut orgStrut = elenco.getOrgStrut();
        final OrgEnte orgEnte = orgStrut.getOrgEnte();
        final String nmStrut = orgStrut.getNmStrut();
        final String nmEnte = orgEnte.getNmEnte();
        final String nmAmbiente = orgEnte.getOrgAmbiente().getNmAmbiente();
        String hash = DigestUtils.sha256Hex(marcaTemporale);
        final String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);

        ElvFileElencoVer fileElencoVers = new ElvFileElencoVer();
        fileElencoVers.setTiFileElencoVers(FileTypeEnum.MARCA_FIRMA_ELENCO_INDICI_AIP.name());
        // MEV#30397
        if (backendMetadata.isDataBase()) {
            fileElencoVers.setBlFileElencoVers(marcaTemporale);
        }
        // end MEV#30397
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
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
                MessaggiWSFormat.formattaUrnMarcaElencoIndiciAIP(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(fileElencoVers,
                MessaggiWSFormat.formattaUrnMarcaElencoIndiciAIP(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()),
                        Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);

        elenco.setDtMarcaElencoIxAip(fileElencoVers.getDtCreazioneFile());
        // MEV#15967 - Attivazione della firma Xades e XadesT
        impostaStatoCompletatoElencoIndiceAip(elenco, idUtente, tiGestioneEnum, modalitaLog);
        return fileElencoVers;
    }

    // MEV#15967 - Attivazione della firma Xades e XadesT
    private void impostaStatoCompletatoElencoIndiceAip(ElvElencoVer elenco, long idUtente,
            ElencoEnums.GestioneElencoEnum tiGestioneEnum, String modalitaLog) {
        final OrgStrut orgStrut = elenco.getOrgStrut();
        List<String> statiUdDocDaCompletare = new ArrayList<>(
                Arrays.asList(UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name(),
                        UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA.name()));
        elabElencoIndiceAipEjb.setCompletato(elenco, statiUdDocDaCompletare, idUtente,
                tiGestioneEnum, modalitaLog);
        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, orgStrut, idUtente,
                ElencoEnums.OpTypeEnum.MARCA_ELENCO_INDICI_AIP.name());
        IamUser user = genericHelper.findById(IamUser.class, idUtente);
        // EVO 19304
        registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                "MARCA_ELENCO_INDICI_AIP",
                "Marca assunta con successo; gestione elenco = MARCA_FIRMA",
                TiStatoElenco.COMPLETATO, user.getNmUserid());
        log.debug("Impostazione stato elenco a COMPLETATO.");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveErroreMarcaElencoIndiceAip(long idElencoVers, long idUtente) {
        ElvElencoVer elenco = evHelper.retrieveElencoById(idElencoVers);
        evHelper.lockElenco(elenco);
        elenco.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());
        /* Cambio stato a unità documentarie e documenti associati all'elenco */
        List<String> statiUdDocDaPortareErrMarca = new ArrayList<>(
                Arrays.asList(UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO.name()));
        List<AroUpdUDTiStatoUpdElencoVers> statiUpdDaPortareErrMarca = new ArrayList<>(Arrays
                .asList(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO));
        elabElencoIndiceAipEjb.updateUnitaDocElencoIndiceAIP(idElencoVers,
                statiUdDocDaPortareErrMarca,
                UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA.name());
        elabElencoIndiceAipEjb.updateDocumentiElencoIndiceAIP(idElencoVers,
                statiUdDocDaPortareErrMarca,
                UdDocStatusEnum.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA.name());
        elabElencoIndiceAipEjb.updateAggiornamentiElencoIndiceAIP(idElencoVers,
                statiUpdDaPortareErrMarca,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA);
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersDaElab elencoDaElab = evHelper
                .getElvElencoVersDaElabByIdElencoVers(idElencoVers);
        elencoDaElab.setTiStatoElenco(ElencoStatusEnum.ELENCO_INDICI_AIP_ERR_MARCA.name());
        elencoDaElab.setTsStatoElenco(new Date());
        /* Registro sul log delle operazioni */
        evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUtente,
                ElencoEnums.OpTypeEnum.MARCA_ELENCO_INDICI_AIP_FALLITA.name());
        // EVO 19304
        IamUser user = genericHelper.findById(IamUser.class, idUtente);
        registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "MARCA_ELENCO_INDICI_AIP",
                "Errore nell’assunzione della marca; gestione elenco = MARCA_FIRMA",
                TiStatoElenco.ELENCO_INDICI_AIP_ERR_MARCA, user.getNmUserid());
    }

    /**
     * Returns <code>true</code> if there is at least one ElencoIndiceAIP to mark otherwise
     * <code>false</code>.
     *
     * @param idUser id utente
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
        ElvVChkSoloUdAnnul soloUdAnnul = genericHelper.findViewById(ElvVChkSoloUdAnnul.class,
                idElencoVers);
        return soloUdAnnul.getFlSoloUdAnnul() != null && soloUdAnnul.getFlSoloUdAnnul().equals("1")
                && soloUdAnnul.getFlSoloDocAnnul() != null
                && soloUdAnnul.getFlSoloDocAnnul().equals("1")
                && soloUdAnnul.getFlSoloUpdUdAnnul() != null
                && soloUdAnnul.getFlSoloUpdUdAnnul().equals("1");
    }

    public boolean almenoUnaUdAnnul(BigDecimal idElencoVers) {
        ElvVChkUnaUdAnnul unaUdAnnul = genericHelper.findViewById(ElvVChkUnaUdAnnul.class,
                idElencoVers);
        return unaUdAnnul.getFlUnaUdVersAnnul().equals("1")
                || unaUdAnnul.getFlUnaUdDocAggAnnul().equals("1")
                || unaUdAnnul.getFlUnaUdUpdUdAnnul().equals("1");
    }

    public void manageElencoUdAnnulDaFirmaElenco(BigDecimal idElencoVers, long idUserIam) {
        ElvElencoVer elencoVers = genericHelper.findById(ElvElencoVer.class, idElencoVers);

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
                unitaDocElenco.setTiStatoUdElencoVers(UdDocStatusEnum.NON_SELEZ_SCHED.name());
                // Registro l'ud nella coda delle ud da elaborare (tabella
                // ELV_UD_VERS_DA_ELAB_ELENCO)
                evHelper.insertUdCodaUdDaElab(unitaDocElenco.getIdUnitaDoc(),
                        UdDocStatusEnum.NON_SELEZ_SCHED);
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
                docAggiuntoElenco.setTiStatoDocElencoVers(UdDocStatusEnum.NON_SELEZ_SCHED.name());
                // Registro il doc nella coda dei doc da elaborare (tabella
                // ELV_DOC_AGG_DA_ELAB_ELENCO)
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
                updMetadatiElenco
                        .setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
                // Registro la upd nella coda degli aggiornamenti da elaborare (tabella
                // ELV_UPD_UD_DA_ELAB_ELENCO)
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
        genericHelper.removeEntity(elencoDaElab, true);

        /*
         * Elimino lo storico delle sessioni di firma in errore per l'elenco (tabella
         * HSM_ELENCO_SESSIONE_FIRMA)
         */
        List<HsmElencoSessioneFirma> listElencoSessioneFirmaHsm = evHelper
                .retrieveListaElencoInError(elencoVers, TiEsitoFirmaElenco.IN_ERRORE);
        for (HsmElencoSessioneFirma elencoSessioneFirmaHsm : listElencoSessioneFirmaHsm) {
            genericHelper.removeEntity(elencoSessioneFirmaHsm, true);
        }

        // Cancello l'elenco di versamento corrente
        evHelper.deleteElvElencoVer(idElencoVers);

        // Scrivo nel log l'avvenuta cancellazione
        evHelper.writeLogElencoVers(elencoVers, elencoVers.getOrgStrut(), idUserIam,
                ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
    }

    public void manageElencoUdAnnulDaFirmaElencoIndiciAip(BigDecimal idElencoVers) {
        ElvElencoVer elencoVers = genericHelper.findById(ElvElencoVer.class, idElencoVers);
        // Modifico alcuni parametri dell'elenco, lo elimino da quelli da elaborare ed elimino il
        // file relativo ad
        // elenco indici aip
        elencoVers.setTiStatoElenco(ElencoStatusEnum.COMPLETATO.name());
        elencoVers.setTiGestElenco(ElencoEnums.GestioneElencoEnum.NO_FIRMA.name());
        String nota = elencoVers.getNtElencoChiuso();
        elencoVers.setNtElencoChiuso((StringUtils.isNotBlank(nota) ? nota + ";" : "")
                + "L'elenco contiene solo versamenti annullati");
        // Elimina l’elenco dalla coda degli elenchi da elaborare
        ElvElencoVersDaElab elencoDaElab = evHelper.retrieveElencoInQueue(elencoVers);
        genericHelper.removeEntity(elencoDaElab, true);
        // Elimina il record relativo al file di tipo ELENCO_INDICI_AIP
        ElvFileElencoVer fileElencoVer = evHelper.getFileIndiceElenco(idElencoVers.longValue(),
                FileTypeEnum.ELENCO_INDICI_AIP.name());
        genericHelper.removeEntity(fileElencoVer, true);
    }

    public ElvStatoElencoVerTableBean getElvStatoElencoVersTableBean(BigDecimal idElencoVers)
            throws EMFError {
        List<ElvStatoElencoVer> listaStatiElencoVers = evHelper
                .retrieveStatiElencoByElencoVers(idElencoVers);
        ElvStatoElencoVerTableBean statiElencoVersTableBean = new ElvStatoElencoVerTableBean();
        try {
            if (listaStatiElencoVers != null && !listaStatiElencoVers.isEmpty()) {
                for (ElvStatoElencoVer statoElencoVers : listaStatiElencoVers) {
                    ElvStatoElencoVerRowBean statoElencoVersRowBean = (ElvStatoElencoVerRowBean) Transform
                            .entity2RowBean(statoElencoVers);
                    if (statoElencoVers.getIamUser() != null) {
                        statoElencoVersRowBean.setString("nm_userid",
                                statoElencoVers.getIamUser().getNmUserid());
                    }
                    statoElencoVersRowBean.setString("cd_ti_eve_stato_elenco_vers",
                            (genericHelper.findById(DecTiEveStatoElencoVers.class,
                                    statoElencoVers.getIdTiEveStatoElencoVers()))
                                    .getCdTiEveStatoElencoVers());
                    statiElencoVersTableBean.add(statoElencoVersRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError(
                    "Errore nel recupero della lista degli stati assunti dall'elenco di versamento ",
                    e);
        }
        return statiElencoVersTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public long registraStatoElencoVersamento(BigDecimal idElencoVers, String cdTiEveElencoVers,
            String dsCondStatoElencoVers, TiStatoElenco tiStatoElenco, String nmUserid) {
        ElvStatoElencoVer statoElencoVers = new ElvStatoElencoVer();
        statoElencoVers.setTiStatoElenco(tiStatoElenco);
        statoElencoVers.setPgStatoElencoVers(
                evHelper.getPgStatoElencoVers(idElencoVers).add(BigDecimal.ONE));
        if (nmUserid != null) {
            IamUser user = userHelper.findIamUser(nmUserid);
            statoElencoVers.setIamUser(user);
        }
        statoElencoVers.setDsCondStatoElencoVers(dsCondStatoElencoVers);
        BigDecimal idTiEveStatoElencoVers = evHelper.getIdTiEveStatoElencoVers(cdTiEveElencoVers);
        statoElencoVers.setIdTiEveStatoElencoVers(idTiEveStatoElencoVers);// setElvElencoVer
        ElvElencoVer elencoVer = genericHelper.findById(ElvElencoVer.class, idElencoVers);
        statoElencoVers.setElvElencoVer(elencoVer);
        statoElencoVers.setTsStatoElencoVers(new Date());
        genericHelper.getEntityManager().persist(statoElencoVers);
        genericHelper.getEntityManager().flush();
        // persist
        elencoVer.setIdStatoElencoVersCor(
                BigDecimal.valueOf(statoElencoVers.getIdStatoElencoVers()));
        return statoElencoVers.getIdStatoElencoVers();
    }

    public List<ElvElencoVer> getElenchiFiscaliByStrutturaAperti(long idStrut, int anno) {
        // MAC#28509
        List<ElvElencoVer> list = new ArrayList<>();

        List<ElvElencoVer> l1 = evHelper.getElenchiFiscaliByStrutturaAperti(idStrut, anno);
        list.addAll(l1);
        List<ElvElencoVer> l2 = evHelper.getElenchiFiscaliSoloDocAggMdByStrutturaAperti(idStrut,
                anno);
        list.addAll(l2);

        return list;
        // MAC#28509
    }

    public boolean isStatoElencoCorrente(long idElencoVers, TiStatoElenco tiStatoElenco) {
        return evHelper.isStatoElencoCorrente(idElencoVers, tiStatoElenco);
    }

    // MEV#32249 - Funzione per riportare indietro lo stato di un elenco per consentire la firma
    // dell'AIP
    public boolean isPossibileMettereAipAllaFirma(BigDecimal idElencoVers) {
        boolean ret = false;
        if (evHelper.isPossibileMettereAipAllaFirma(idElencoVers)) {
            ret = true;
        }
        return ret;
    }

    // MEV#34195 - Funzione per riportare indietro lo stato di una lista di elenchi per consentire
    // la firma dell'AIP
    // Viene passata una lista di elenchi che verrà "filtrata" restituendo solo gli elenchi idonei
    // per consentire la
    // firma dell'AIP
    public List<BigDecimal> isPossibileMettereAipAllaFirma(List<BigDecimal> idElencoVersList) {
        return evHelper.isPossibileMettereAipAllaFirma(idElencoVersList);
    }

    // MEV#32249 - Funzione per riportare indietro lo stato di un elenco per consentire la firma
    // dell'AIP
    public EsitoRiportaIndietroStatoVersamento riportaStatoVersamentoIndietro(
            BigDecimal idElencoVers, String userName) {
        EsitoRiportaIndietroStatoVersamento ret = null;
        if (soloUdAnnul(idElencoVers)) {
            return EsitoRiportaIndietroStatoVersamento.CHECK_SOLO_UD_E_DOC_ANNULLATI;
        } else {
            boolean elencoConAlmenoUnaUdSenzaIndiceAip = evHelper
                    .isElencoConAlmenoUnaUdSenzaIndiceAip(idElencoVers);
            boolean elencoConUdConTroppeVersioniIndiceAip = evHelper
                    .isElencoConUdConTroppeVersioniIndiceAip(idElencoVers);
            if (!elencoConAlmenoUnaUdSenzaIndiceAip && !elencoConUdConTroppeVersioniIndiceAip) {
                /// MODIFICA LO STATO DEL VERSAMENTO
                List<AroUnitaDoc> laro = evHelper.findUdPerStatoElencoEConservazioneStatoDiverso(
                        idElencoVers,
                        it.eng.parer.entity.constraint.AroUnitaDoc.TiStatoUdElencoVers.IN_ELENCO_COMPLETATO,
                        "ANNULLATA");
                for (AroUnitaDoc aroUnitaDoc : laro) {
                    log.debug("Modifico ud {}", aroUnitaDoc.getIdUnitaDoc());
                    aroUnitaDoc.setTiStatoUdElencoVers(
                            it.eng.parer.entity.constraint.AroUnitaDoc.TiStatoUdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI
                                    .name());
                }
                List<AroDoc> ldoc = evHelper.findAroDocPerStatoElencoSenzaUdAnnullate(idElencoVers,
                        "IN_ELENCO_COMPLETATO");
                for (AroDoc aroDoc : ldoc) {
                    log.debug("Modifico doc {}", aroDoc.getIdDoc());
                    aroDoc.setTiStatoDocElencoVers("IN_ELENCO_CON_INDICI_AIP_GENERATI");
                }
                ElvElencoVer elenco = evHelper.getEntityManager().find(ElvElencoVer.class,
                        idElencoVers.longValueExact());
                log.debug("Modifico ElvElencoVer {}", idElencoVers);
                elenco.setTiStatoElenco("INDICI_AIP_GENERATI");
                ElvElencoVersDaElab daElab = new ElvElencoVersDaElab();
                daElab.setElvElencoVer(elenco);
                daElab.setAaKeyUnitaDoc(elenco.getAaKeyUnitaDoc());
                daElab.setIdCriterioRaggr(
                        new BigDecimal(elenco.getDecCriterioRaggr().getIdCriterioRaggr()));
                daElab.setIdStrut(new BigDecimal(elenco.getOrgStrut().getIdStrut()));
                daElab.setTiStatoElenco(elenco.getTiStatoElenco());
                evHelper.getEntityManager().persist(daElab);

                registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers.longValueExact()),
                        "ESEGUITA_CREAZIONE_INDICE_AIP",
                        "AIP rimesso alla firma dall'apposita funzione di interfaccia",
                        TiStatoElenco.INDICI_AIP_GENERATI, userName);
                evHelper.flush();
                ret = EsitoRiportaIndietroStatoVersamento.ESITO_OK;

            } else {
                if (elencoConAlmenoUnaUdSenzaIndiceAip) {
                    ret = EsitoRiportaIndietroStatoVersamento.ELENCO_CON_ALMENO_UNA_UD_SENZA_INDICE_AIP;
                    return ret;
                }
                if (elencoConUdConTroppeVersioniIndiceAip) {
                    ret = EsitoRiportaIndietroStatoVersamento.ELENCO_CON_UD_CON_TROPPE_VERSIONI_INDICE_AIP;
                }
            }
        }
        return ret;
    }

    public enum EsitoRiportaIndietroStatoVersamento {
        CHECK_SOLO_UD_E_DOC_ANNULLATI, ELENCO_CON_ALMENO_UNA_UD_SENZA_INDICE_AIP,
        ELENCO_CON_UD_CON_TROPPE_VERSIONI_INDICE_AIP, ESITO_OK;
    }

}
