package it.eng.parer.job.indiceAip.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAip.elenchi.ElaborazioneElencoIndiceAip;
import it.eng.parer.job.indiceAip.helper.CreazioneIndiceAipHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.ElvVChkIxAipUdGen;
import it.eng.parer.viewEntity.OrgVLisStrutPerEle;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipEjb {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceAipEjb.class);
    @EJB
    private CreazioneIndiceAipHelper ciaHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElaborazioneRigaIndiceAipDaElab elaborazione;
    @EJB
    private ElaborazioneElencoIndiceAip elabElencoIndiciAip;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private CreazioneIndiceAipEjb me;
    @EJB
    private ElenchiVersamentoEjb evEjb;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneIndiceAip() throws ParerInternalError, ParseException {
        /* Il sistema apre una nuova transazione */
        log.debug("Creazione Indice AIP - Inizio transazione di creazione indice");
        // MEV#26288
        // List<OrgVLisStrutPerEle> strutture = elencoHelper.retrieveStrutturePerEle();
        // for (OrgVLisStrutPerEle struttura : strutture) {
        // // Verifica elenchi con stato IN_CODA_JMS_VERIFICA_FIRME_DT_VERS
        // elaboraStrutturaFase1(struttura.getIdStrut().longValue(),
        // ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_VERIFICA_FIRME_DT_VERS);
        // // Verifica elenchi con stato IN_CODA_JMS_INDICE_AIP_DA_ELAB
        // elaboraStrutturaFase2(struttura.getIdStrut().longValue(),
        // ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB);
        // }
        // end MEV#26288
        /* Reupero gli indici da elaborare */
        log.debug("Creazione Indice AIP - Recupero gli AroIndiceAipUdDaElab ");
        List<Long> udDaElabList = ciaHelper.getIndexAplIndiceAipUdDaElab();
        log.info("Creazione Indice AIP - Ottenuti " + udDaElabList.size() + " indici AIP da elaborare");
        /* Per ogni unità documentaria presente nella coda */
        for (Long udDaElab : udDaElabList) {
            elaborazione.gestisciIndiceAipDaElaborareNelJob(udDaElab);
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP - Chiusura transazione di creazione indice");
    }

    // MEV#26288
    // private void elaboraStrutturaFase1(long idStruttura, ElencoEnums.ElencoStatusEnum statoElenco) {
    // // il sistema determina gli elenchi della struttura versante con stato = IN_CODA_JMS_VERIFICA_FIRME_DT_VERS
    // // (vedi ELV_ELENCO_VERS_DA_ELAB)
    // List<ElvElencoVersDaElab> elenchiDaVerificare = elencoHelper.retrieveElenchi(idStruttura, statoElenco);
    // log.info("Creazione Indice AIP" + " - struttura id " + idStruttura + ": trovati " + elenchiDaVerificare.size()
    // + " elenchi da processare con stato IN_CODA_JMS_VERIFICA_FIRME_DT_VERS");
    //
    // // elaboro gli elenchi
    // for (ElvElencoVersDaElab elencoDaElab : elenchiDaVerificare) {
    // if (elencoHelper.checkStatoAllUdInElencoPerLeFasi(elencoDaElab.getElvElencoVer().getIdElencoVers(),
    // ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name())) {
    // me.aggiornaElencoFase1(elencoDaElab.getElvElencoVer().getIdElencoVers(),
    // elencoDaElab.getIdElencoVersDaElab());
    // }
    // }
    // log.info("Creazione Indice AIP" + " - struttura id " + idStruttura + ": processati "
    // + elenchiDaVerificare.size() + " elenchi con stato IN_CODA_JMS_VERIFICA_FIRME_DT_VERS");
    // }

    // /**
    // * Aggiornamento elenco <strong>fase 1</strong>.
    // *
    // * <ol>
    // * <li>Il sistema aggiorna elenco corrente (ELV_ELENCO_VERS) assegnando stato = FIRME_VERIFICATE_DT_VERS</li>
    // * <li>Il sistema aggiorna elenco da elaborare corrente (ELV_ELENCO_VERS_DA_ELAB) assegnando stato =
    // * FIRME_VERIFICATE_DT_VERS</li>
    // * </ol>
    // *
    // * @param idElencoVers
    // * - elenco di cui deve essere aggiornato lo stato
    // * @param idElencoVersDaElab
    // * - elencoDaElab di cui aggiornare lo stato
    // */
    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // public void aggiornaElencoFase1(long idElencoVers, long idElencoVersDaElab) {
    // elencoHelper.aggiornaElencoCorrente(idElencoVers, ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS);
    // elencoHelper.aggiornaElencoDaElabCorrente(idElencoVersDaElab,
    // ElencoEnums.ElencoStatusEnum.FIRME_VERIFICATE_DT_VERS);
    // // EVO 19304
    // evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "IN_CODA_VERIFICA_FIRMA_DT_VERS",
    // "Tutte le unità documentarie non annullate sono IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS",
    // ElvStatoElencoVer.TiStatoElenco.FIRME_VERIFICATE_DT_VERS, null);
    // }

    // private void elaboraStrutturaFase2(long idStruttura, ElencoEnums.ElencoStatusEnum statoElenco) {
    // // il sistema determina gli elenchi della struttura versante con stato = IN_CODA_JMS_INDICE_AIP_DA_ELAB (vedi
    // // ELV_ELENCO_VERS_DA_ELAB)
    // List<ElvElencoVersDaElab> elenchiDaVerificare = elencoHelper.retrieveElenchi(idStruttura, statoElenco);
    // log.info("Creazione Indice AIP" + " - struttura id " + idStruttura + ": trovati " + elenchiDaVerificare.size()
    // + " elenchi da processare con stato IN_CODA_JMS_INDICE_AIP_DA_ELAB");
    //
    // // elaboro gli elenchi
    // for (ElvElencoVersDaElab elencoDaElab : elenchiDaVerificare) {
    // if (elencoHelper.checkStatoAllUdInElencoPerLeFasi(elencoDaElab.getElvElencoVer().getIdElencoVers(),
    // ElencoEnums.UdDocStatusEnum.IN_ELENCO_IN_CODA_INDICE_AIP.name(),
    // ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name())) {
    // me.aggiornaElencoFase2(elencoDaElab.getElvElencoVer().getIdElencoVers(),
    // elencoDaElab.getIdElencoVersDaElab());
    // }
    // }
    // log.info("Creazione Indice AIP" + " - struttura id " + idStruttura + ": processati "
    // + elenchiDaVerificare.size() + " elenchi con stato IN_CODA_JMS_INDICE_AIP_DA_ELAB");
    // }

    // /**
    // * Aggiornamento elenco <strong>fase 2</strong>.
    // *
    // * <ol>
    // * <li>Il sistema aggiorna elenco corrente (ELV_ELENCO_VERS) assegnando stato = IN_CODA_INDICE_AIP</li>
    // * <li>Il sistema elimina da ELV_ELENCO_VERS_DA_ELAB l'elenco corrente</li>
    // * </ol>
    // *
    // * @param idElencoVers
    // * - elenco di cui deve essere aggiornato lo stato
    // * @param idElencoVersDaElab
    // * - elencoDaElab da eliminare
    // */
    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    // public void aggiornaElencoFase2(long idElencoVers, long idElencoVersDaElab) {
    // elencoHelper.aggiornaElencoCorrente(idElencoVers, ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP);
    // elencoHelper.aggiornaElencoDaElabCorrente(idElencoVersDaElab, ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP);
    // // EVO 19304
    // evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElencoVers), "ESEGUITA_VERIFICA_FIRMA_DT_VERS",
    // "Tutte le unità documentarie non annullate sono IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS",
    // ElvStatoElencoVer.TiStatoElenco.IN_CODA_INDICE_AIP, null);
    // }
    // end MEV#26288

    /*
     * Questo codice è stato messo in una nuova transazione altrimentirimaneva loccatoil record dell elenco MAC#16424
     * 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setIndiciAipGeneratiInNewTransaction(long idElenco) {
        ElvElencoVer elenco = ciaHelper.findById(ElvElencoVer.class, idElenco);
        // Vista per verificare che tutte le ud e doc aggiunti e aggiornamenti metadati appartenenti all'indice aip
        // abbiano stato IN_ELENCO_CON_INDICI_AIP_GENERATI
        ElvVChkIxAipUdGen view = ciaHelper.findViewById(ElvVChkIxAipUdGen.class, BigDecimal.valueOf(idElenco));
        if (view.getFlIxAipUdGenOk().equals("1")) {
            log.debug(
                    "Creazione Indice AIP - per l'elenco tutte le ud e i doc aggiunti hanno stato IN_ELENCO_CON_INDICI_AIP_GENERATI - aggiorno l'elenco");
            elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name());

            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElenco), "ESEGUITA_CREAZIONE_INDICE_AIP",
                    "Tutte unità documentarie non annullate hanno stato = IN_ELENCO_CON_INDICI_AIP_GENERATI",
                    ElvStatoElencoVer.TiStatoElenco.INDICI_AIP_GENERATI, null);

            ElvElencoVersDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);
            if (elencoDaElab != null) {
                elencoDaElab.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name());
                elencoDaElab.setTsStatoElenco(new Date());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneElenchiIndiceAip(long idLogJob)
            throws ParerInternalError, IOException, JAXBException, NoSuchAlgorithmException, ParseException {
        // MAC#16424
        List<Long> idElenchiInCoda = elencoHelper
                .retrieveElenchiIndiciAipDaProcessare(ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP.name());
        if (!idElenchiInCoda.isEmpty()) {
            for (Long idElenco : idElenchiInCoda) {
                me.setIndiciAipGeneratiInNewTransaction(idElenco);
            }
        }
        // Fine MAC#16424

        List<Long> idElenchi = elencoHelper
                .retrieveElenchiIndiciAipDaProcessare(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name());
        Set<Long> struttureVerificate = new HashSet<>();
        if (!idElenchi.isEmpty()) {
            for (Long idElenco : idElenchi) {
                ElvElencoVer elenco = ciaHelper.findById(ElvElencoVer.class, idElenco);
                if (struttureVerificate.add(elenco.getOrgStrut().getIdStrut())) {
                    String checkPartizioni = struttureHelper.checkPartizioni(
                            BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()), new Date(),
                            CostantiDB.TiPartition.FILE_ELENCHI_VERS.name());
                    String verificaPartizioni = configurationHelper.getValoreParamApplic(
                            CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI, null, null, null, null,
                            CostantiDB.TipoAplVGetValAppart.APPLIC);
                    if (checkPartizioni.equals("0") && Boolean.parseBoolean(verificaPartizioni)) {
                        OrgStrut strut = elenco.getOrgStrut();
                        OrgEnte ente = strut.getOrgEnte();
                        OrgAmbiente ambiente = ente.getOrgAmbiente();
                        String strutComposita = ambiente.getNmAmbiente() + "-" + ente.getNmEnte() + "-"
                                + strut.getNmStrut();
                        throw new ParerInternalError("La partizione di tipo "
                                + CostantiDB.TiPartition.FILE_ELENCHI_VERS.name()
                                + " per la data corrente e la struttura " + strutComposita + " non \u00E8 definita");
                    }
                }
                elabElencoIndiciAip.creaElencoIndiciAIP(idElenco, idLogJob);
            }
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione elenchi indici AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP - Chiusura transazione di creazione elenchi indici AIP");
    }

}
