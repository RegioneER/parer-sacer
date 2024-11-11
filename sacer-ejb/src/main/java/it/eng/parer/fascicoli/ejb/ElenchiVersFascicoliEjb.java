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

package it.eng.parer.fascicoli.ejb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersFascicoli.ejb.IndiceElencoVersFascJobEjb;
import it.eng.parer.elencoVersFascicoli.ejb.IndiceElencoVersFascXsdEjb;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.FileTypeEnum;
import it.eng.parer.entity.DecCriterioRaggrFasc;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasStatoConservFascicolo;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.FasUnitaDocFascicolo;
import it.eng.parer.entity.HsmElencoFascSesFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvFascDaElabElenco.TiStatoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.entity.constraint.HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc;
import it.eng.parer.fascicoli.helper.ElenchiVersFascicoliHelper;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.slite.gen.form.ElenchiVersFascicoliForm.FiltriElenchiVersFascicoli;
import it.eng.parer.slite.gen.tablebean.DecCriterioRaggrFascRowBean;
import it.eng.parer.slite.gen.tablebean.ElvElencoVersFascRowBean;
import it.eng.parer.slite.gen.tablebean.ElvStatoElencoVersFascRowBean;
import it.eng.parer.slite.gen.tablebean.ElvStatoElencoVersFascTableBean;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByFasRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByFasTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByStatoRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascByStatoTableBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVRicElencoFascTableBean;
import it.eng.parer.viewEntity.ElvVChkSoloFascAnnul;
import it.eng.parer.viewEntity.ElvVRicElencoFasc;
import it.eng.parer.viewEntity.ElvVRicElencoFascByFas;
import it.eng.parer.viewEntity.ElvVRicElencoFascByStato;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.spagoCore.error.EMFError;
import java.util.logging.Level;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@Stateless
@LocalBean
public class ElenchiVersFascicoliEjb {

    private static final Logger log = LoggerFactory.getLogger(ElenchiVersFascicoliEjb.class);

    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private ElenchiVersFascicoliHelper evfWebHelper;
    @EJB
    private ElencoVersFascicoliHelper evfHelper;
    @EJB
    private IndiceElencoVersFascXsdEjb iefxEjb;
    @EJB
    private IndiceElencoVersFascJobEjb iefjEjb;
    // MEV#30399
    @EJB
    private ObjectStorageService objectStorageService;
    // end MEV#30399

    public ElvElencoVersFascRowBean getElvElencoVersFascRowBean(BigDecimal idElencoVersFasc) {
        ElvElencoVersFasc elenco = evfWebHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc.longValue());
        ElvStatoElencoVersFasc statoElenco = evfWebHelper.findById(ElvStatoElencoVersFasc.class,
                elenco.getIdStatoElencoVersFascCor());
        List<ElvStatoElencoVersFasc> listaStatiElenco = evfWebHelper
                .retrieveStatiElencoByElencoVersFasc(idElencoVersFasc);
        List<ElvFileElencoVersFasc> elencoIndiceAip = evfHelper.retrieveFileIndiceElenco(idElencoVersFasc.longValue(),
                new String[] { ElencoEnums.FileTypeEnum.INDICE_ELENCO.name() });
        ElvElencoVersFascRowBean elencoRowBean = new ElvElencoVersFascRowBean();
        try {
            elencoRowBean = (ElvElencoVersFascRowBean) Transform.entity2RowBean(elenco);
            final OrgAmbiente orgAmbiente = elenco.getOrgStrut().getOrgEnte().getOrgAmbiente();

            // MEV#15967 - Attivazione della firma Xades e XadesT
            List<ElvFileElencoVersFasc> elencoIndiceAipFirmato = evfHelper.retrieveFileIndiceElenco(
                    idElencoVersFasc.longValue(),
                    new String[] { ElencoEnums.FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name() });
            if (elencoIndiceAipFirmato != null && !elencoIndiceAipFirmato.isEmpty()) {
                elencoRowBean.setString("ti_firma", elencoIndiceAipFirmato.get(0).getTiFirma());
            }
            //

            elencoRowBean.setString("nm_criterio_raggr", elenco.getDecCriterioRaggrFasc().getNmCriterioRaggr());
            elencoRowBean.setString("ds_criterio_raggr", elenco.getDecCriterioRaggrFasc().getDsCriterioRaggr());
            elencoRowBean.setString("ti_stato", statoElenco.getTiStato().name());
            for (ElvStatoElencoVersFasc stato : listaStatiElenco) {
                Timestamp tsStato = new Timestamp(stato.getTsStato().getTime());
                if (stato.getTiStato().equals(TiStatoElencoFasc.CHIUSO)) {
                    elencoRowBean.setTimestamp("ts_stato_chiuso", tsStato);
                } else if (stato.getTiStato().equals(TiStatoElencoFasc.FIRMATO)) {
                    elencoRowBean.setTimestamp("ts_stato_firmato", tsStato);
                }
            }
            elencoRowBean.setBigDecimal("num_fasc", elenco.getNiFascVersElenco().add(elenco.getNiFascVersElenco()));
            final String nmAmbiente = orgAmbiente.getNmAmbiente();
            final String nmEnte = elenco.getOrgStrut().getOrgEnte().getNmEnte();
            final String nmStrut = elenco.getOrgStrut().getNmStrut();
            elencoRowBean.setString("nm_ambiente", nmAmbiente);
            elencoRowBean.setString("nm_ente", nmEnte);
            elencoRowBean.setString("nm_strut", nmStrut);
            elencoRowBean.setString("amb_ente_strut", nmAmbiente + " / " + nmEnte + " / " + nmStrut);
            if (elencoIndiceAip != null && !elencoIndiceAip.isEmpty()) {
                elencoRowBean.setString("cd_ver_xsd_file", elencoIndiceAip.get(0).getCdVerXsdFile());
            } else {
                elencoRowBean.setString("cd_ver_xsd_file", null);
            }
        } catch (Exception ex) {
            log.error("Errore durante il recupero dell'elenco di versamento fascicoli "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return elencoRowBean;
    }

    public ElvStatoElencoVersFascTableBean getElvStatiElencoVersFascTableBean(BigDecimal idElencoVersFasc)
            throws EMFError {
        List<ElvStatoElencoVersFasc> listaStatiElencoVersFascicoli = evfWebHelper
                .retrieveStatiElencoByElencoVersFasc(idElencoVersFasc);
        ElvStatoElencoVersFascTableBean statiElencoVersFascTableBean = new ElvStatoElencoVersFascTableBean();
        try {
            if (listaStatiElencoVersFascicoli != null && !listaStatiElencoVersFascicoli.isEmpty()) {
                for (ElvStatoElencoVersFasc statoElencoVersFasc : listaStatiElencoVersFascicoli) {
                    ElvStatoElencoVersFascRowBean statoElencoVersFascRowBean = (ElvStatoElencoVersFascRowBean) Transform
                            .entity2RowBean(statoElencoVersFasc);
                    if (statoElencoVersFasc.getIamUser() != null) {
                        statoElencoVersFascRowBean.setString("nm_userid",
                                statoElencoVersFasc.getIamUser().getNmUserid());
                    }
                    statiElencoVersFascTableBean.add(statoElencoVersFascRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError(
                    "Errore nel recupero della lista degli stati assunti dall'elenco di versamento fascicoli", e);
        }
        return statiElencoVersFascTableBean;
    }

    public ElvStatoElencoVersFascRowBean getElvStatoElencoVersFascRowBean(BigDecimal idStatoElencoVersFasc) {
        ElvStatoElencoVersFasc statoElenco = evfWebHelper.findById(ElvStatoElencoVersFasc.class,
                idStatoElencoVersFasc.longValue());
        ElvStatoElencoVersFascRowBean statoElencoRowBean = new ElvStatoElencoVersFascRowBean();
        try {
            statoElencoRowBean = (ElvStatoElencoVersFascRowBean) Transform.entity2RowBean(statoElenco);
            if (statoElenco.getIamUser() != null) {
                statoElencoRowBean.setString("nm_userid", statoElenco.getIamUser().getNmUserid());
            }
        } catch (Exception ex) {
            log.error("Errore durante il recupero dello stato dell'elenco di versamento fascicoli "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return statoElencoRowBean;
    }

    public DecCriterioRaggrFascRowBean getDecCriterioRaggrFascRowBean(BigDecimal idCriterioRaggrFasc) throws EMFError {
        DecCriterioRaggrFasc criterio = evfWebHelper.findById(DecCriterioRaggrFasc.class,
                idCriterioRaggrFasc.longValue());
        DecCriterioRaggrFascRowBean criterioRowBean = new DecCriterioRaggrFascRowBean();
        try {
            criterioRowBean = (DecCriterioRaggrFascRowBean) Transform.entity2RowBean(criterio);
        } catch (Exception ex) {
            log.error("Errore durante il recupero del criterio di raggruppamento fascicolo "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new EMFError("Errore durante il recupero del criterio di raggruppamento fascicolo", ex);
        }
        return criterioRowBean;
    }

    public ElvVRicElencoFascTableBean getElvVRicElencoFascTableBean(long idUserIam, FiltriElenchiVersFascicoli filtri)
            throws EMFError {
        List<ElvVRicElencoFasc> listaElenchiVersFascicoli = evfWebHelper.retrieveElvVRicElencoFascList(idUserIam,
                filtri);
        ElvVRicElencoFascTableBean elenchiVersFascTableBean = new ElvVRicElencoFascTableBean();
        try {
            if (listaElenchiVersFascicoli != null && !listaElenchiVersFascicoli.isEmpty()) {
                for (ElvVRicElencoFasc elencoVersFasc : listaElenchiVersFascicoli) {
                    ElvVRicElencoFascRowBean elenchiVersFascRowBean = (ElvVRicElencoFascRowBean) Transform
                            .entity2RowBean(elencoVersFasc);
                    elenchiVersFascRowBean.setString("nm_ente_nm_strut",
                            elenchiVersFascRowBean.getNmEnte() + " - " + elenchiVersFascRowBean.getNmStrut());
                    elenchiVersFascTableBean.add(elenchiVersFascRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento fascicoli", e);
        }
        return elenchiVersFascTableBean;
    }

    public ElvVRicElencoFascByStatoTableBean getElvVRicElencoFascByStatoTableBean(long idUserIam,
            FiltriElenchiVersFascicoli filtri) throws EMFError {
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = evfWebHelper
                .retrieveElvVRicElencoFascByStatoList(idUserIam, filtri);
        ElvVRicElencoFascByStatoTableBean elenchiVersFascTableBean = new ElvVRicElencoFascByStatoTableBean();
        try {
            if (listaElenchiVersFascicoli != null && !listaElenchiVersFascicoli.isEmpty()) {
                for (ElvVRicElencoFascByStato elencoVersFasc : listaElenchiVersFascicoli) {
                    ElvVRicElencoFascByStatoRowBean elenchiVersFascRowBean = (ElvVRicElencoFascByStatoRowBean) Transform
                            .entity2RowBean(elencoVersFasc);
                    elenchiVersFascRowBean.setString("nm_ente_nm_strut",
                            elenchiVersFascRowBean.getNmEnte() + " - " + elenchiVersFascRowBean.getNmStrut());
                    elenchiVersFascTableBean.add(elenchiVersFascRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento fascicoli", e);
        }
        return elenchiVersFascTableBean;
    }

    public ElvVRicElencoFascByFasTableBean getElvVRicElencoFascByFasTableBean(long idUserIam,
            FiltriElenchiVersFascicoli filtri) throws EMFError {
        List<ElvVRicElencoFascByFas> listaElenchiVersFascicoli = evfWebHelper
                .retrieveElvVRicElencoFascByFasList(idUserIam, filtri);
        ElvVRicElencoFascByFasTableBean elenchiVersFascTableBean = new ElvVRicElencoFascByFasTableBean();
        try {
            if (listaElenchiVersFascicoli != null && !listaElenchiVersFascicoli.isEmpty()) {
                for (ElvVRicElencoFascByFas elencoVersFasc : listaElenchiVersFascicoli) {
                    ElvVRicElencoFascByFasRowBean elenchiVersFascRowBean = (ElvVRicElencoFascByFasRowBean) Transform
                            .entity2RowBean(elencoVersFasc);
                    elenchiVersFascRowBean.setString("nm_ente_nm_strut",
                            elenchiVersFascRowBean.getNmEnte() + " - " + elenchiVersFascRowBean.getNmStrut());
                    elenchiVersFascTableBean.add(elenchiVersFascRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento fascicoli", e);
        }
        return elenchiVersFascTableBean;
    }

    public ElvVRicElencoFascByStatoTableBean getElenchiVersFascicoliDaFirmareTableBean(BigDecimal idAmbiente,
            BigDecimal idEnte, BigDecimal idStrut, BigDecimal idElencoVersFasc, String note,
            ElencoEnums.ElencoStatusEnum tiStato, Date[] dateCreazioneElencoValidate, long idUserIam) throws EMFError {
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = evfWebHelper.getListaElenchiVersFascicoliDaFirmare(
                idAmbiente, idEnte, idStrut, idElencoVersFasc, note, tiStato, dateCreazioneElencoValidate, idUserIam);
        ElvVRicElencoFascByStatoTableBean elenchiVersFascicoliTableBean = new ElvVRicElencoFascByStatoTableBean();
        try {
            if (listaElenchiVersFascicoli != null && !listaElenchiVersFascicoli.isEmpty()) {
                for (ElvVRicElencoFascByStato elenco : listaElenchiVersFascicoli) {
                    ElvVRicElencoFascByStatoRowBean elenchiVersFascicoliRowBean = new ElvVRicElencoFascByStatoRowBean();
                    elenchiVersFascicoliRowBean = (ElvVRicElencoFascByStatoRowBean) Transform.entity2RowBean(elenco);
                    elenchiVersFascicoliRowBean.setString("amb_ente_strut",
                            elenchiVersFascicoliRowBean.getNmAmbiente() + " / "
                                    + elenchiVersFascicoliRowBean.getNmEnte() + " / "
                                    + elenchiVersFascicoliRowBean.getNmStrut());
                    elenchiVersFascicoliRowBean.setString("vers_fascicoli_annull",
                            elenchiVersFascicoliRowBean.getFlAnnull());
                    elenchiVersFascicoliTableBean.add(elenchiVersFascicoliRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError(
                    "Errore nel recupero della lista degli elenchi di versamento fascicoli con stato " + tiStato.name(),
                    e);
        }
        return elenchiVersFascicoliTableBean;
    }

    public ElvVRicElencoFascByStatoTableBean getElenchiVersFascicoliDaFirmareTableBean(
            List<BigDecimal> idElencoVersFascList, long idUserIam) throws EMFError {
        List<ElvVRicElencoFascByStato> listaElenchiVersFascicoli = evfWebHelper
                .getListaElenchiVersFascicoliDaFirmare(idElencoVersFascList, idUserIam);
        ElvVRicElencoFascByStatoTableBean elenchiVersFascicoliTableBean = new ElvVRicElencoFascByStatoTableBean();
        try {
            if (listaElenchiVersFascicoli != null && !listaElenchiVersFascicoli.isEmpty()) {
                for (ElvVRicElencoFascByStato elenco : listaElenchiVersFascicoli) {
                    ElvVRicElencoFascByStatoRowBean elenchiVersFascicoliRowBean = new ElvVRicElencoFascByStatoRowBean();
                    elenchiVersFascicoliRowBean = (ElvVRicElencoFascByStatoRowBean) Transform.entity2RowBean(elenco);
                    elenchiVersFascicoliRowBean.setString("amb_ente_strut",
                            elenchiVersFascicoliRowBean.getNmAmbiente() + " / "
                                    + elenchiVersFascicoliRowBean.getNmEnte() + " / "
                                    + elenchiVersFascicoliRowBean.getNmStrut());
                    elenchiVersFascicoliRowBean.setString("vers_fascicoli_annull",
                            elenchiVersFascicoliRowBean.getFlAnnull());
                    elenchiVersFascicoliTableBean.add(elenchiVersFascicoliRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EMFError("Errore nel recupero della lista degli elenchi di versamento fascicoli", e);
        }
        return elenchiVersFascicoliTableBean;
    }

    public boolean isElencoDeletable(BigDecimal idElencoVersFasc) {
        return evfWebHelper.isElencoDeletable(idElencoVersFasc);
    }

    public boolean isElencoClosable(BigDecimal idElencoVersFasc) {
        return evfWebHelper.isElencoClosable(idElencoVersFasc);
    }

    // TIP: fdilorenzo, test criteria api con metamodels
    public boolean isElencoClosable2(BigDecimal idElencoVersFasc) {
        return evfWebHelper.isElencoClosable2(idElencoVersFasc);
    }

    public boolean areFascDeletables(BigDecimal idElencoVersFasc) {
        return evfWebHelper.areFascDeletables(idElencoVersFasc);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteElenco(long idUserIam, BigDecimal idElencoVersFasc) {
        /* Recupero l'elenco */
        ElvElencoVersFasc elenco = evfWebHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);
        /* Setto la data di fascicolo non annullato */
        Calendar fineDelMondo = Calendar.getInstance();
        fineDelMondo.set(2444, 11, 31, 0, 0, 0);
        fineDelMondo.set(Calendar.MILLISECOND, 0);

        /* Per ogni fascicolo appartenente all'elenco */
        for (FasFascicolo fasFascicolo : elenco.getFasFascicoli()) {
            /* Rimuovo l'appartenenza del fascicolo all'elenco */
            fasFascicolo.setElvElencoVersFasc(null);
            /* Se il fascicolo non è annullato */
            if (fasFascicolo.getDtAnnull().getTime() == fineDelMondo.getTime().getTime()) {
                // Registra un nuovo stato pari a IN_ATTESA_SCHED per il fascicolo
                FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
                statoFascicoloElenco.setFasFascicolo(fasFascicolo);
                statoFascicoloElenco.setTsStato(new Date());
                statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ATTESA_SCHED);

                fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);
                // Assegna stato IN_ATTESA_SCHED al fascicolo
                fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ATTESA_SCHED);
                // Registra il fascicolo nella coda dei fascicoli da elaborare
                evfHelper.insertFascCodaFascDaElab(fasFascicolo.getIdFascicolo(), TiStatoFascDaElab.IN_ATTESA_SCHED);
            }
        }

        /* Elimino l'elenco di versamento fascicoli dalla coda degli elenchi in elaborazione */
        for (ElvElencoVersFascDaElab e : elenco.getElvElencoVersFascDaElabs()) {
            evfHelper.deleteElencoVersFascDaElab(e.getIdElencoVersDaElab());
        }

        /*
         * Elimino lo storico delle sessioni di firma in errore per l'elenco di versamento fascicoli (tabella
         * HSM_ELENCO_FASC_SES_FIRMA)
         */
        List<HsmElencoFascSesFirma> listElencoFascSesFirmaHsm = evfHelper.retrieveListaElencoInError(elenco,
                TiEsitoFirmaElencoFasc.IN_ERRORE);
        for (HsmElencoFascSesFirma elencoFascSesFirmaHsm : listElencoFascSesFirmaHsm) {
            evfHelper.removeEntity(elencoFascSesFirmaHsm, true);
        }

        /* Cancello l'elenco di versamento fascicoli corrente */
        evfHelper.deleteElvElencoVersFasc(idElencoVersFasc);

        /* TODO: verificare, Scrivo nel log l'avvenuta cancellazione */
        // evfHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
        // ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
    }

    /**
     * Esegue la chiusura manuale dell'elenco di versamento fascicoli passato come parametro in input.
     *
     * @param idUserIam
     *            id user Iam
     * @param idElencoVersFasc
     *            id elenco versamento fascicolo
     * @param modifica
     *            lista modifiche di tipo OpTypeEnum
     * @param note
     *            valore
     *
     * @throws DatatypeConfigurationException
     *             errore generico
     * @throws IOException
     *             eccezione di tipo IO
     * @throws NoSuchAlgorithmException
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manualClosingElenco(long idUserIam, BigDecimal idElencoVersFasc, List<ElencoEnums.OpTypeEnum> modifica,
            String note) throws DatatypeConfigurationException, NoSuchAlgorithmException, IOException {
        ElvElencoVersFasc elenco = evfWebHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);
        log.info("Inizio processo di chiusura manuale elenco di versamento fascicoli avente id "
                + elenco.getIdElencoVersFasc());
        /* Scrivo il motivo di chiusura */
        elenco.setDlMotivoChius("Elenco di versamento fascicoli chiuso manualmente");
        /* Se ci sono state modifiche sulle note indice elenco, le salvo e scrivo nel log */
        if (!modifica.isEmpty()) {
            evfWebHelper.saveNote(idUserIam, idElencoVersFasc, note, elenco.getNtElencoChiuso(), modifica);
        }
        // determina nome ente e struttura normalizzati e non
        OrgStrut struttura = elenco.getOrgStrut();
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Costruisco l'urn originale e normalizzato dell'elenco
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        /* Creo l'indice */
        byte[] indexFile = null;
        log.info("Creazione indice per elenco di versamento fascicoli avente id '" + elenco.getIdElencoVersFasc());
        indexFile = iefxEjb.createIndex(elenco, true);
        // Calcolo l'hash SHA-256 del file .xml
        String hashXmlIndice = new HashCalculator().calculateHashSHAX(indexFile, TipiHash.SHA_256).toHexBinary();
        // Costruisco l'urn originale dell'indice
        String urnXmlIndice = buildUrnIndiceElencoVersFascicoli(elenco, nomeStruttura, null, nomeEnte, null);
        // Costruisco l'urn normalizzato dell'indice
        String urnXmlIndiceNormaliz = buildUrnIndiceElencoVersFascicoli(elenco, null, nomeStrutturaNorm, null,
                nomeEnteNorm);
        // Registro nella tabella ElvFileElencoVersFasc:
        // Setto l'hash dell'indice, l'algoritmo usato per il calcolo hash (=SHA-256),
        // l'encoding del hash (=hexBinary), la versione del XSD (=1.0) con cui è creato l'indice dell'elenco e
        // l'urn dell’indice “urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<data creazione>-<id
        // elenco>:Indice”
        evfHelper.storeFileIntoElenco(elenco, indexFile, ElencoEnums.FileTypeEnum.INDICE_ELENCO.name(), new Date(),
                hashXmlIndice, TipiHash.SHA_256.descrivi(), TipiEncBinari.HEX_BINARY.descrivi(), urnXmlIndice,
                urnXmlIndiceNormaliz, ElencoEnums.ElencoInfo.VERSIONE_ELENCO.message(), null);

        // Registro un nuovo stato = CHIUSO e lo lascio nella coda degli elenchi da elaborare assegnando stato = CHIUSO
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        /* Imposto la data di chiusura */
        statoElencoVersFasc.setTsStato(new Date());
        /* Setto l'elenco a stato chiuso */
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.CHIUSO);
        /* Imposto l'utente che ha lanciato la chiusura manuale */
        statoElencoVersFasc.setIamUser(evfWebHelper.findById(IamUser.class, idUserIam));
        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        /* Lo lascio nella coda degli elenchi da elaborare */
        ElvElencoVersFascDaElab elencoDaElab = evfHelper
                .getElvElencoVersFascDaElabByIdElencoVersFasc(idElencoVersFasc.longValue());
        elencoDaElab.setTiStato(TiStatoElencoFascDaElab.CHIUSO);
        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = evfHelper
                .getStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.CHIUSO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        /* Per ogni fascicolo appartenente all'elenco */
        for (FasFascicolo fasFascicolo : elenco.getFasFascicoli()) {
            // Registra un nuovo stato pari a IN_ELENCO_CHIUSO per il fascicolo
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(fasFascicolo);
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_CHIUSO);

            fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

            // Assegna stato IN_ELENCO_CHIUSO al fascicolo
            fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_CHIUSO);

            // Elimina il fascicolo dalla coda dei fascicoli da elaborare
            evfHelper.deleteFasFascicoloFromQueue(fasFascicolo);
        }

        /* TODO: verificare, Scrivo nel log l'avvenuta chiusura */
        // evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
        // ElencoEnums.OpTypeEnum.CHIUSURA_ELENCO.name());
        log.info("Fine processo di chiusura manuale elenco di versamento fascicoli avente id "
                + elenco.getIdElencoVersFasc());
    }

    public OrgStrutRowBean getOrgStrutRowBeanWithAmbienteEnte(BigDecimal idStrut) {
        OrgStrut strut = evfWebHelper.findById(OrgStrut.class, idStrut.longValue());
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

    /* DA TESTARE */
    public <T> T findRowBeanById(Class<T> rowBeanClass, BigDecimal id) throws Exception {
        log.debug("Getting instance of class " + rowBeanClass.getSimpleName() + " with id: " + id);
        try {
            Class entityClass = (rowBeanClass.getSimpleName().substring(0, rowBeanClass.getSimpleName().length() - 4))
                    .getClass();
            entityClass = (Class) evfWebHelper.findById(entityClass, id);
            T row = (T) Transform.entity2RowBean(entityClass);
            log.debug("Get successful");
            return row;
        } catch (Exception e) {
            log.error("Get failed", e);
            throw e;
        }
    }

    /**
     * Data una lista di elenchi di versamento fascicoli ed una dataChiusura impostati restituisce true o false a
     * seconda che tutti gli elenchi non presenti nella lista passata, con data chiusura inferiore a quella passata,
     * siano firmati.
     *
     * @param elencoTableBean
     *            entity bean ElvVRicElencoFascByStato
     * @param dataChiusura
     *            data chiusura
     * @param idStrut
     *            id struttura
     *
     * @return true/false
     */
    public boolean areAllElenchiNonPresentiFirmati(ElvVRicElencoFascByStatoTableBean elencoTableBean, Date dataChiusura,
            BigDecimal idStrut) {
        List<BigDecimal> idElencoVersFascSelezionatiList = new ArrayList<>();
        for (ElvVRicElencoFascByStatoRowBean elencoRowBean : elencoTableBean) {
            idElencoVersFascSelezionatiList.add(elencoRowBean.getIdElencoVersFasc());
        }
        return evfWebHelper.areAllElenchiNonPresentiFirmati(idElencoVersFascSelezionatiList, dataChiusura, idStrut);
    }

    /**
     * Controlla se su DB esiste già, per quella struttura, un elenco che abbia l'id passato come parametro
     *
     * @param idElenco
     *            id elenco
     * @param idStrut
     *            id struttura
     *
     * @return true/false
     */
    public boolean existIdElenco(BigDecimal idElenco, BigDecimal idStrut) {
        return evfWebHelper.existIdElenco(idElenco, idStrut);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveNote(Long idUserIam, BigDecimal idElencoVersFasc, String ntIndiceElenco, String ntElencoChiuso,
            List<ElencoEnums.OpTypeEnum> operList) {
        evfWebHelper.saveNote(idUserIam, idElencoVersFasc, ntIndiceElenco, ntElencoChiuso, operList);
        // ElvElencoVersFasc elenco = evfWebHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc.longValue());
    }

    /**
     * Rimuove l'appartenenza dei fascicoli aggiunti all'elenco di versamento fascicoli in questione
     *
     * @param idElencoVersFasc
     *            id elenco versamento fascicolo
     * @param idFascToRemove
     *            id fascicolo da rimuovere
     * @param idUserIam
     *            id user Iam
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteFascFromElencoVersFascicoli(Long idElencoVersFasc, Set<BigDecimal> idFascToRemove,
            long idUserIam) {
        log.info("Rimuovo i fascicoli dall'elenco di versamento fascicoli...");
        ElvElencoVersFasc elenco = evfWebHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);

        /* Setto la data di fascicolo non annullata */
        Calendar fineDelMondo = Calendar.getInstance();
        fineDelMondo.set(2444, 11, 31, 0, 0, 0);
        fineDelMondo.set(Calendar.MILLISECOND, 0);

        Set<Long> idFascicoliTrattati = new HashSet<>();
        for (BigDecimal idFf : idFascToRemove) {
            /* Ricavo il fascicolo */
            /* Se il fascicolo non è già stato trattato, procedo */
            FasFascicolo fasFascicolo = evfWebHelper.findById(FasFascicolo.class, idFf);
            if (!idFascicoliTrattati.contains(fasFascicolo.getIdFascicolo())) {
                /* Rimuovo l'appartenza del fascicolo all'elenco di versamento fascicoli */
                fasFascicolo.setElvElencoVersFasc(null);
                /* Se il fascicolo non è annullato */
                if (fasFascicolo.getDtAnnull().getTime() == fineDelMondo.getTime().getTime()) {
                    // Registra un nuovo stato pari a IN_ATTESA_SCHED per il fascicolo
                    FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
                    statoFascicoloElenco.setFasFascicolo(fasFascicolo);
                    statoFascicoloElenco.setTsStato(new Date());
                    statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ATTESA_SCHED);

                    fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);
                    // Assegno stato IN_ATTESA_SCHED al fascicolo
                    fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ATTESA_SCHED);
                    // Registro il fascicolo nella coda dei fascicoli da elaborare
                    evfHelper.insertFascCodaFascDaElab(fasFascicolo.getIdFascicolo(),
                            TiStatoFascDaElab.IN_ATTESA_SCHED);
                }

                /* Ricalcolo i valori */
                elenco.setNiFascVersElenco(new BigDecimal(evfHelper.contaFascVersati(idElencoVersFasc)));

                /* TODO: verificare, Registro sul log delle operazioni */
                // evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                // ElencoEnums.OpTypeEnum.RIMUOVI_UD_ELENCO.name(), null, unitaDoc);

                /* Inserisco il fascicolo tra quelli già trattati */
                idFascicoliTrattati.add(fasFascicolo.getIdFascicolo());
            }
        }
        /* Se l'elenco non contiene più elementi, lo elimino e scrivo nel log */
        if (elenco.getFasFascicoli().isEmpty()) {
            // TODO: verificare, evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
            // ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
            evfHelper.deleteElvElencoVersFasc(new BigDecimal(elenco.getIdElencoVersFasc()));
        }
    }

    public ElvFileElencoVersFasc retrieveElvFileElencoVersFasc(long idElencoVersFasc, String tiFileElencoVers) {
        return evfHelper.retrieveElvFileElencoVersFasc(idElencoVersFasc, tiFileElencoVers);
    }

    public byte[] retrieveFileIndiceElenco(long idElencoVersFasc, String tiFileElencoVers) {
        return evfHelper.retrieveFileIndiceElenco(idElencoVersFasc, tiFileElencoVers);
    }

    // MEV#31922 - Introduzione modalità NO FIRMA nella validazione degli elenchi di versamento dei fascicoli
    public void streamOutFileIndiceElencoNoFirma(ZipOutputStream out, String fileNamePrefix, String fileNameSuffix,
            long idElencoVersFasc) throws IOException, DatatypeConfigurationException {
        ElvElencoVersFasc elenco = evfHelper.retrieveElencoById(idElencoVersFasc);
        byte[] dati = iefxEjb.createIndex(elenco, false);
        fileNamePrefix = StringUtils.defaultString(fileNamePrefix);
        fileNameSuffix = StringUtils.defaultString(fileNameSuffix);
        addEntryToZip(out, dati, fileNamePrefix + fileNameSuffix + FileTypeEnum.INDICE_ELENCO.getFileExtension());
        out.flush();
    }

    public void streamOutFileIndiceElenco(ZipOutputStream out, String fileNamePrefix, String fileNameSuffix,
            long idElencoVersFasc, FileTypeEnum... fileTypes) throws IOException {
        List<ElvFileElencoVersFasc> retrieveFileIndiceElenco = evfHelper.retrieveFileIndiceElenco(idElencoVersFasc,
                FileTypeEnum.getStringEnumsList(fileTypes));
        for (ElvFileElencoVersFasc elvFileElencoVersFasc : retrieveFileIndiceElenco) {
            FileTypeEnum fileType = ElencoEnums.FileTypeEnum.valueOf(elvFileElencoVersFasc.getTiFileElencoVers());
            fileNamePrefix = StringUtils.defaultString(fileNamePrefix);
            fileNameSuffix = StringUtils.defaultString(fileNameSuffix);

            // MEV#15967 - Attivazione della firma Xades e XadesT
            String fileExtension = null;
            String tiFirma = elvFileElencoVersFasc.getTiFirma();
            if (tiFirma != null
                    && tiFirma.equals(it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.XADES.name())) {
                fileExtension = ".xml";
            } else {
                fileExtension = fileType.getFileExtension();
            }
            //
            switch (fileType) {
            case INDICE_ELENCO:
            case FIRMA_INDICE_ELENCO:
                // Niente da fare, lo metto per gestire tutti i tipi
                break;
            case ELENCO_INDICI_AIP:
                fileNameSuffix = "_IndiceNonFirmato";
                break;
            case FIRMA_ELENCO_INDICI_AIP:
                fileNameSuffix = "_Indice";
                break;
            default:
                throw new AssertionError(fileType.name());
            }

            // MEV#30399
            byte[] blFileElencoVers = elvFileElencoVersFasc.getBlFileElencoVers();
            if (blFileElencoVers == null) {
                blFileElencoVers = objectStorageService
                        .getObjectElencoIndiciAipFasc(elvFileElencoVersFasc.getIdFileElencoVersFasc());
            }
            // end MEV#30399

            addEntryToZip(out, blFileElencoVers, fileNamePrefix + fileNameSuffix + fileExtension);
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

    public void storeFirma(Long idElencoVersFasc, byte[] fileFirmato, Date signatureDate, long idUtente,
            it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma)
            throws NoSuchAlgorithmException, IOException {
        ElvElencoVersFasc elenco = evfHelper.retrieveElencoById(idElencoVersFasc);
        // calcolo l'hash SHA-256 del file .p7m
        String hashXmlIndice = new HashCalculator().calculateHashSHAX(fileFirmato, TipiHash.SHA_256).toHexBinary();
        // determina nome ente e struttura normalizzati e non
        OrgStrut struttura = elenco.getOrgStrut();
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Costruisco l'urn originale dell'indice
        String urnXmlIndice = buildUrnIndiceElencoVersFascicoliFirmati(elenco, nomeStruttura, null, nomeEnte, null);
        // Costruisco l'urn normalizzato dell'indice
        String urnXmlIndiceNormaliz = buildUrnIndiceElencoVersFascicoliFirmati(elenco, null, nomeStrutturaNorm, null,
                nomeEnteNorm);
        // Registro il file .p7m (in ELV_FILE_ELENCO_VERS_FASC)
        // definendo l'hash dell'indice, l'algoritmo usato per il calcolo hash (=SHA-256),
        // l'encoding del hash (=hexBinary), la versione del XSD (=1.0) con cui è creato l'indice dell'elenco e
        // l'urn dell’indice “urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<data creazione>-<id
        // elenco>:IndiceFirmato”
        evfHelper.storeFileIntoElenco(elenco, fileFirmato, ElencoEnums.FileTypeEnum.FIRMA_INDICE_ELENCO.name(),
                signatureDate, hashXmlIndice, TipiHash.SHA_256.descrivi(), TipiEncBinari.HEX_BINARY.descrivi(),
                urnXmlIndice, urnXmlIndiceNormaliz, ElencoEnums.ElencoInfo.VERSIONE_ELENCO.message(), tipoFirma);

        /* Registro un nuovo stato = FIRMATO */
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        /* Imposto la data di firma */
        statoElencoVersFasc.setTsStato(signatureDate);
        /* Setto l'elenco a stato FIRMATO */
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.FIRMATO);
        /* Imposto l'utente firmatario */
        statoElencoVersFasc.setIamUser(evfWebHelper.findById(IamUser.class, idUtente));
        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = evfHelper
                .getStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.FIRMATO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        /* Per ogni fascicolo appartenente all'elenco */
        for (FasFascicolo fasFascicolo : elenco.getFasFascicoli()) {
            // Registra un nuovo stato pari a IN_ELENCO_FIRMATO per il fascicolo
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(fasFascicolo);
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_FIRMATO);

            fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

            // Assegna stato IN_ELENCO_FIRMATO al fascicolo
            fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_FIRMATO);

            /* TODO: verificare, Elimina il fascicolo dalla coda dei fascicoli da elaborare */
            /*
             * evfHelper.deleteFasFascicoloFromQueue(fasFascicolo);
             */
        }

        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersFascDaElab elencoDaElab = evfHelper.getElvElencoVersFascDaElabByIdElencoVersFasc(idElencoVersFasc);
        elencoDaElab.setTiStato(TiStatoElencoFascDaElab.FIRMATO);

        /* TODO: verificare, Registro sul log delle operazioni */
        /*
         * evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUtente,
         * ElencoEnums.OpTypeEnum.VALIDAZIONE_ELENCO.name());
         */
    }

    // MEV#31922 - Introduzione modalità NO FIRMA nella validazione degli elenchi di versamento dei fascicoli
    /*
     * Nuovo metodo per validare gli elenchi fascicolo
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void validElenco(long idUserIam, BigDecimal idElencoVersFasc) {
        ElvElencoVersFasc elenco = evfHelper.retrieveElencoById(idElencoVersFasc.longValueExact());
        log.info("Inizio processo di validazione elenco di versamento fascicoli avente id {}",
                elenco.getIdElencoVersFasc());
        /* Assumo lock esclusivo sull'elenco */
        evfHelper.lockElenco(elenco);
        /* Controllo se almeno una unità doc appartenente all'elenco e' annullata */
        IamUser user = evfHelper.findById(IamUser.class, idUserIam);
        /* Registro un nuovo stato = VALIDATO */
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        /* Imposto la data di firma */
        statoElencoVersFasc.setTsStato(new Date());
        /* Setto l'elenco a stato VALIDATO */
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.VALIDATO);
        /* Imposto l'utente che valida */
        statoElencoVersFasc.setIamUser(user);
        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = evfHelper
                .getStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.VALIDATO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        /* Per ogni fascicolo appartenente all'elenco */
        for (FasFascicolo fasFascicolo : elenco.getFasFascicoli()) {
            // Registra un nuovo stato pari a IN_ELENCO_VALIDATO per il fascicolo
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(fasFascicolo);
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_VALIDATO);
            fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);
            // Assegna stato IN_ELENCO_VALIDATO al fascicolo
            fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_VALIDATO);
        }
        /* Cambio lo stato dell'elenco nella coda di elaborazione */
        ElvElencoVersFascDaElab elencoDaElab = evfHelper
                .getElvElencoVersFascDaElabByIdElencoVersFasc(idElencoVersFasc.longValueExact());
        elencoDaElab.setTiStato(TiStatoElencoFascDaElab.VALIDATO);
        log.info("Fine processo di validazione elenco di versamento fascicoli avente id {}",
                elenco.getIdElencoVersFasc());
    }

    /**
     * Metodo di salvataggio file firmato dell'elenco indici AIP fascicoli sul database
     *
     * @param idElencoVersFasc
     *            id elenco versamento fascicolo
     * @param fileFirmato
     *            array di byte file firmato
     * @param signatureDate
     *            data firma
     * @param idUtente
     *            id utente
     * @param backendMetadata
     *            tipo backend
     * @param tipoFirma
     *            tipo firma (XADES o CADES)
     *
     * @return ElvFileElencoVersFasc
     */
    public ElvFileElencoVersFasc storeFirmaElencoIndiceAipFasc(Long idElencoVersFasc, byte[] fileFirmato,
            Date signatureDate, long idUtente, BackendStorage backendMetadata,
            it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma) {
        ElvElencoVersFasc elenco = evfHelper.retrieveElencoById(idElencoVersFasc);
        evfHelper.lockElenco(elenco);

        final OrgStrut orgStrut = elenco.getOrgStrut();
        final OrgEnte orgEnte = orgStrut.getOrgEnte();
        final OrgAmbiente orgAmbiente = orgEnte.getOrgAmbiente();
        final String nmStrut = orgStrut.getNmStrut();
        final String nmEnte = orgEnte.getNmEnte();
        final String nmAmbiente = orgAmbiente.getNmAmbiente();
        String hash = DigestUtils.sha256Hex(fileFirmato);
        // EVO#16486
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        CSVersatore versatore = new CSVersatore();
        versatore.setAmbiente(nmAmbiente);
        versatore.setEnte(nmEnte);
        versatore.setStruttura(nmStrut);
        // sistema (new URN)
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        versatore.setSistemaConservazione(sistema);
        // salvo ORIGINALE
        final String urnElencoIndiceAIPFirmato = MessaggiWSFormat.formattaUrnElencoIndiciAIPFascicoliFirmati(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore), Long.toString(idElencoVersFasc),
                sdf.format(elenco.getTsCreazioneElenco()));
        // salvo NORMALIZZATO
        final String urnNormalizElencoIndiceAIPFirmato = MessaggiWSFormat.formattaUrnElencoIndiciAIPFascicoliFirmati(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                Long.toString(idElencoVersFasc), sdf.format(elenco.getTsCreazioneElenco()));
        // end EVO#16486
        ElvFileElencoVersFasc fileElencoVersFasc = new ElvFileElencoVersFasc();
        fileElencoVersFasc.setCdVerXsdFile(Costanti.VERSIONE_ELENCO_INDICE_AIP_FASC);
        fileElencoVersFasc.setTiFileElencoVers(ElencoEnums.FileTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());
        // MEV#30399
        if (backendMetadata.isDataBase()) {
            fileElencoVersFasc.setBlFileElencoVers(fileFirmato);
        }
        // end MEV#30399
        fileElencoVersFasc.setIdStrut(BigDecimal.valueOf(orgStrut.getIdStrut()));
        fileElencoVersFasc.setDtCreazioneFile(new Date());
        fileElencoVersFasc.setDsHashFile(hash);
        fileElencoVersFasc.setDsUrnFile(urnElencoIndiceAIPFirmato);
        fileElencoVersFasc.setDsUrnNormalizFile(urnNormalizElencoIndiceAIPFirmato);
        fileElencoVersFasc.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
        fileElencoVersFasc.setTiFirma(tipoFirma == null ? null : tipoFirma.name());
        fileElencoVersFasc.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
        if (elenco.getElvFileElencoVersFasc() == null) {
            elenco.setElvFileElencoVersFasc(new ArrayList<>());
        }
        elenco.addElvFileElencoVersFasc(fileElencoVersFasc);

        /* Registro un nuovo stato = COMPLETATO */
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        /* Imposto la data di firma */
        statoElencoVersFasc.setTsStato(signatureDate);
        /* Setto l'elenco a stato COMPLETATO */
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.COMPLETATO);
        /* Imposto l'utente firmatario */
        statoElencoVersFasc.setIamUser(evfWebHelper.findById(IamUser.class, idUtente));
        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = evfHelper
                .getStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.COMPLETATO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        // Determino i fascicoli appartenenti all'elenco con stato = IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO
        List<FasFascicolo> fasFascicoliFiltered = elenco.getFasFascicoli();
        CollectionUtils.filter(fasFascicoliFiltered, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ((FasFascicolo) object).getTiStatoFascElencoVers()
                        .equals(TiStatoFascElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO);
            }
        });
        /* Per ogni fascicolo appartenente all'elenco */
        for (FasFascicolo fasFascicolo : fasFascicoliFiltered) {
            // Registra un nuovo stato pari a IN_ELENCO_COMPLETATO per il fascicolo
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(fasFascicolo);
            statoFascicoloElenco.setIamUser(evfWebHelper.findById(IamUser.class, idUtente));
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_COMPLETATO);

            fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

            // Assegna stato IN_ELENCO_COMPLETATO al fascicolo
            fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_COMPLETATO);

            if (!fasFascicolo.getTiStatoConservazione().equals(TiStatoConservazione.ANNULLATO)) {
                // Aggiorno lo stato corrente di conservazione del fascicolo assegnando IN_ARCHIVIO
                fasFascicolo.setTiStatoConservazione(TiStatoConservazione.IN_ARCHIVIO);

                // Registra un nuovo stato di conservazione del fascicolo specificando stato = IN_ARCHIVIO e user quello
                // corrente
                FasStatoConservFascicolo statoConservFascicolo = new FasStatoConservFascicolo();
                statoConservFascicolo.setFasFascicolo(fasFascicolo);
                statoConservFascicolo.setIamUser(evfWebHelper.findById(IamUser.class, idUtente));
                statoConservFascicolo.setTsStato(new Date());
                statoConservFascicolo.setTiStatoConservazione(
                        it.eng.parer.entity.constraint.FasStatoConservFascicolo.TiStatoConservazione.IN_ARCHIVIO);

                fasFascicolo.getFasStatoConservFascicoloElencos().add(statoConservFascicolo);

                for (FasUnitaDocFascicolo unitaDocFascicolo : fasFascicolo.getFasUnitaDocFascicolos()) {
                    if (unitaDocFascicolo.getAroUnitaDoc().getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name())) {
                        unitaDocFascicolo.getAroUnitaDoc()
                                .setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name());
                    }
                }
            }

            /* TODO: verificare, Elimina il fascicolo dalla coda dei fascicoli da elaborare */
            /*
             * evfHelper.deleteFasFascicoloFromQueue(fasFascicolo);
             */
        }

        /* Elimino l'elenco di versamento fascicoli dalla coda degli elenchi in elaborazione */
        ElvElencoVersFascDaElab elencoDaElab = evfHelper.getElvElencoVersFascDaElabByIdElencoVersFasc(idElencoVersFasc);
        evfHelper.removeEntity(elencoDaElab, true);

        /* Registro sul log delle operazioni */
        // evHelper.writeLogElencoVers(elenco, orgStrut, idUtente,
        // ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name());
        return fileElencoVersFasc;
    }

    public void manageElencoFascAnnulDaFirmaElencoFasc(BigDecimal idElencoVersFasc, long idUserIam) {
        ElvElencoVersFasc elencoVersFasc = evfHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);

        List<FasFascicolo> fascicoloElencoList = evfHelper.getFasFascicoloVersatiElenco(idElencoVersFasc);

        // Per ogni fascicolo appartenente all'elenco
        for (FasFascicolo fascicoloElenco : fascicoloElencoList) {
            fascicoloElenco.setElvElencoVersFasc(null);
            // Assegno stato fascicolo nell'elenco di vers fasc uguale a IN_ATTESA_SCHED
            fascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ATTESA_SCHED);
            if (!fascicoloElenco.getTiStatoConservazione().equals(TiStatoConservazione.ANNULLATO)) {
                // Registro il fascicolo nella coda dei fascicoli da elaborare (tabella ELV_FASC_DA_ELAB_ELENCO)
                evfHelper.insertFascCodaFascDaElab(fascicoloElenco.getIdFascicolo(), TiStatoFascDaElab.IN_ATTESA_SCHED);
            }
        }

        // Elimino l'elenco di versamento fascicoli dalla coda degli elenchi in elaborazione
        ElvElencoVersFascDaElab elencoVersFascDaElab = evfHelper.retrieveElencoInQueue(elencoVersFasc);
        evfHelper.removeEntity(elencoVersFascDaElab, true);

        /*
         * Elimino lo storico delle sessioni di firma in errore per l'elenco di versamento fascicoli (tabella
         * HSM_ELENCO_FASC_SES_FIRMA)
         */
        List<HsmElencoFascSesFirma> listElencoFascSesFirmaHsm = evfHelper.retrieveListaElencoInError(elencoVersFasc,
                TiEsitoFirmaElencoFasc.IN_ERRORE);
        for (HsmElencoFascSesFirma elencoFascSesFirmaHsm : listElencoFascSesFirmaHsm) {
            evfHelper.removeEntity(elencoFascSesFirmaHsm, true);
        }

        // Cancello l'elenco di versamento fascicoli corrente
        evfHelper.deleteElvElencoVersFasc(idElencoVersFasc);

        // TODO: verificare, Scrivo nel log l'avvenuta cancellazione
        // evfHelper.writeLogElencoVers(elencoVersFasc, elencoVersFasc.getOrgStrut(), idUserIam,
        // ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());
    }

    public boolean soloFascAnnul(BigDecimal idElencoVersFasc) {
        ElvVChkSoloFascAnnul soloFascAnnul = evfHelper.findViewById(ElvVChkSoloFascAnnul.class, idElencoVersFasc);
        return soloFascAnnul.getFlSoloFascAnnul() != null && soloFascAnnul.getFlSoloFascAnnul().equals("1");
    }

    public boolean almenoUnFascAnnul(BigDecimal idElencoVersFasc) {
        return evfHelper.existFascVersAnnullati(idElencoVersFasc);
    }

    public void manageElencoFascAnnulDaFirmaElencoIndiciAipFasc(BigDecimal idElencoVersFasc, long idUserIam) {
        ElvElencoVersFasc elenco = evfHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);
        /* Registro un nuovo stato = COMPLETATO */
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        /* Imposto la data di firma */
        statoElencoVersFasc.setTsStato(new Date());
        /* Setto l'elenco a stato COMPLETATO */
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.COMPLETATO);
        /* Imposto l'utente firmatario */
        statoElencoVersFasc.setIamUser(evfWebHelper.findById(IamUser.class, idUserIam));

        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = evfHelper
                .getStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(), TiStatoElencoFasc.COMPLETATO)
                .getIdStatoElencoVersFasc();
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));
        // Modifico alcuni parametri dell'elenco, lo elimino da quelli da elaborare ed elimino il file relativo ad
        // elenco indici aip fascicoli
        String nota = elenco.getNtElencoChiuso();
        elenco.setNtElencoChiuso(
                (StringUtils.isNotBlank(nota) ? nota + ";" : "") + "L'elenco contiene solo versamenti annullati");

        // Determino i fascicoli appartenenti all'elenco con stato = IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO
        List<FasFascicolo> fasFascicoliFiltered = elenco.getFasFascicoli();
        CollectionUtils.filter(fasFascicoliFiltered, new Predicate() {
            @Override
            public boolean evaluate(final Object object) {
                return ((FasFascicolo) object).getTiStatoFascElencoVers()
                        .equals(TiStatoFascElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO);
            }
        });
        /* Per ogni fascicolo appartenente all'elenco */
        for (FasFascicolo fasFascicolo : fasFascicoliFiltered) {
            // Registra un nuovo stato pari a IN_ELENCO_COMPLETATO per il fascicolo
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(fasFascicolo);
            statoFascicoloElenco.setIamUser(evfWebHelper.findById(IamUser.class, idUserIam));
            statoFascicoloElenco.setTsStato(new Date());
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_COMPLETATO);

            fasFascicolo.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

            // Assegna stato IN_ELENCO_COMPLETATO al fascicolo
            fasFascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_COMPLETATO);

            /* TODO: verificare, Elimina il fascicolo dalla coda dei fascicoli da elaborare */
            /*
             * evfHelper.deleteFasFascicoloFromQueue(fasFascicolo);
             */
        }

        // Elimina l’elenco dalla coda degli elenchi da elaborare
        ElvElencoVersFascDaElab elencoDaElab = evfHelper.retrieveElencoInQueue(elenco);
        evfHelper.removeEntity(elencoDaElab, true);
        // Elimina il record relativo al file di tipo ELENCO_INDICI_AIP
        ElvFileElencoVersFasc fileElencoVersFasc = evfHelper.getFileIndiceElenco(idElencoVersFasc.longValue(),
                FileTypeEnum.ELENCO_INDICI_AIP.name());
        evfHelper.removeEntity(fileElencoVersFasc, true);
    }

    public String buildUrnIndiceElencoVersFascicoli(ElvElencoVersFasc elenco, String nomeStruttura,
            String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        String ente = (nomeEnte == null) ? nomeEnteNorm : nomeEnte;
        String struttura = (nomeStruttura == null) ? nomeStrutturaNorm : nomeStruttura;
        return MessaggiWSFormat.formattaUrnIndiceElencoVersFascicoli(sistema, ente, struttura,
                sdf.format(elenco.getTsCreazioneElenco()), String.format("%d", elenco.getIdElencoVersFasc()));
    }

    public String buildUrnIndiceElencoVersFascicoliFirmati(ElvElencoVersFasc elenco, String nomeStruttura,
            String nomeStrutturaNorm, String nomeEnte, String nomeEnteNorm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        String ente = (nomeEnte == null) ? nomeEnteNorm : nomeEnte;
        String struttura = (nomeStruttura == null) ? nomeStrutturaNorm : nomeStruttura;
        return MessaggiWSFormat.formattaUrnIndiceElencoVersFascicoliFirmati(sistema, ente, struttura,
                sdf.format(elenco.getTsCreazioneElenco()), String.format("%d", elenco.getIdElencoVersFasc()));
    }

    public void calcolaUrnElenco(ElvElencoVersFasc elenco, String nomeStruttura, String nomeStrutturaNorm,
            String nomeEnte, String nomeEnteNorm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String nomeSistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // Calcola URN originale
        String urnOriginale = MessaggiWSFormat.formattaUrnElencoVersFascicoli(nomeSistema, nomeEnte, nomeStruttura,
                sdf.format(elenco.getTsCreazioneElenco()), String.format("%d", elenco.getIdElencoVersFasc()));
        elenco.setDsUrnElenco(urnOriginale);
        // Calcola URN normalizzato
        String urnNormalizzato = MessaggiWSFormat.formattaUrnElencoVersFascicoli(nomeSistema, nomeEnteNorm,
                nomeStrutturaNorm, sdf.format(elenco.getTsCreazioneElenco()),
                String.format("%d", elenco.getIdElencoVersFasc()));
        elenco.setDsUrnNormalizElenco(urnNormalizzato);
    }
}
