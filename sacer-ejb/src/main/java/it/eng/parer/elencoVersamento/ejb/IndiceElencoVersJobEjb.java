package it.eng.parer.elencoVersamento.ejb;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.ejb.StruttureEjb;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.FileTypeEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.OpTypeEnum;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.UdDocStatusEnum;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.ElvUrnElencoVers.TiUrnElenco;
import it.eng.parer.entity.constraint.ElvUrnFileElencoVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.ElvVLisAllUdByElenco;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoAplVGetValAppart;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Agati_D
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class IndiceElencoVersJobEjb {

    Logger log = LoggerFactory.getLogger(IndiceElencoVersJobEjb.class);
    @EJB
    private IndiceElencoVersXsdEjb indiceEjb;
    @EJB
    private StruttureEjb struttureEjb;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;

    @Resource
    private SessionContext context;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildIndex(LogJob logJob) throws Exception {
        log.info("Creazione automatica indici...");
        List<OrgStrut> strutture = elencoHelper.retrieveStrutture();

        for (OrgStrut struttura : strutture) {
            manageStrut(struttura.getIdStrut(), logJob.getIdLogJob());
        }
        jobHelper.writeLogJob(OpTypeEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(), OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    public void manageStrut(long idStruttura, long idLogJob) throws Exception {
        log.debug("manageStrut");
        BigDecimal idStrut = new BigDecimal(idStruttura);
        /*
         * Determino gli elenchi appartenenti alla struttura corrente, con stato DA_CHIUDERE (tabella
         * ELV_ELENCO_VERS_DA_ELAB)
         */
        List<Long> elenchiDaChiudere = elencoHelper.retrieveIdElenchiDaElaborare(idStrut,
                ElencoStatusEnum.DA_CHIUDERE.name());
        log.info("struttura id " + idStrut + ": trovati " + elenchiDaChiudere.size()
                + " elenchi DA_CHIUDERE da processare");

        /*
         * Se per la struttura versante la lista degli elenchi da chiudere non è vuota e se il parametro
         * VERIFICA_PARTIZIONI vale true
         */
        final String verificaPartizioni = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI, null, null, null, null, TipoAplVGetValAppart.APPLIC);
        if (!elenchiDaChiudere.isEmpty() && Boolean.parseBoolean(verificaPartizioni)
                && struttureEjb.checkPartizioni(new BigDecimal(idStruttura), new Date(),
                        CostantiDB.TiPartition.FILE_ELENCHI_VERS.name()).equals("0")) {
            OrgStrut strut = elencoHelper.retrieveOrgStrutByid(idStrut);
            throw new ParerUserError("La partizione di tipo FILE_ELENCHI_VERS per la data corrente e la struttura "
                    + strut.getOrgEnte().getOrgAmbiente().getNmAmbiente() + "-" + strut.getOrgEnte().getNmEnte() + "-"
                    + strut.getNmStrut() + " non è definita");
        }

        IndiceElencoVersJobEjb indiceElencoVersEjbRef1 = context.getBusinessObject(IndiceElencoVersJobEjb.class);
        for (Long idElenchi : elenchiDaChiudere) {
            indiceElencoVersEjbRef1.manageIndexAtomic(idElenchi, idStruttura, idLogJob);
        }

        /*
         * Determino gli elenchi appartenenti alla struttura corrente, con stato CHIUSO (tabella
         * ELV_ELENCO_VERS_DA_ELAB), il cui elenco preveda tipo validazione = NO_FIRMA o NO_INDICE e modalità di
         * validazione = AUTOMATICA. Se i valori nell'elenco sono nulli leggo i valori dal criterio di raggruppamento.
         */
        final String numMaxElenchiDaValidare = configurationHelper.getValoreParamApplic("NUM_MAX_ELENCHI_DA_VALIDARE",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        List<Long> elenchiDaValidare = new ArrayList<>();
        if (Integer.valueOf(numMaxElenchiDaValidare) > 0) {
            elenchiDaValidare = elencoHelper.retrieveIdElenchiDaValidare(idStrut, ElencoStatusEnum.CHIUSO.name(),
                    numMaxElenchiDaValidare);
        }
        log.info("struttura id " + idStrut + ": trovati " + elenchiDaValidare.size() + " elenchi CHIUSO da validare");

        IndiceElencoVersJobEjb indiceElencoVersEjbRef2 = context.getBusinessObject(IndiceElencoVersJobEjb.class);
        for (Long idElenchi : elenchiDaValidare) {
            indiceElencoVersEjbRef2.manageValidAtomic(idElenchi, idStruttura, idLogJob);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageIndexAtomic(long idElenco, long idStruttura, long idLogJob) throws Exception {
        log.debug("manageIndexAtomic - idElenco " + idElenco + " idStruttura " + idStruttura);
        LogJob logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        elencoHelper.lockElenco(elenco);
        elencoHelper.writeLogElencoVers(elenco, struttura, OpTypeEnum.CREA_INDICE_ELENCO.name(), logJob);
        manageIndex(elenco, struttura, logJob);
    }

    public void manageIndex(ElvElencoVer elenco, OrgStrut struttura, LogJob logJob) throws ParerUserError,
            ParseException, ParerInternalError, ParerNoResultException, NoSuchAlgorithmException, IOException {
        // EVO#16486
        // Determina le unità doc appartenenti all'elenco corrente (quelle versate, quelle per aggiunta documento e
        // quelle per aggiornamento metadati)
        log.debug("manageIndex");

        // MEV#26219
        // TODO: valutare ripristino logica urn pregressi dopo refactory job
        // List<BigDecimal> idUdList = elencoHelper.retrieveUdInElencoByElencoIdList(elenco.getIdElencoVers());
        // // Per ogni unità doc dell'elenco
        // for (BigDecimal idUd : idUdList) {
        // // Apro nuova transazione
        // context.getBusinessObject(IndiceElencoVersJobEjb.class).verificaUrnUdElenco(idUd.longValue(),
        // elenco.getIdElencoVers());
        // }
        // end MEV#26219

        // determina nome ente e struttura normalizzati e non
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Calcolo e persisto lo urn dell'elenco */
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);
        // end EVO#16486
        buildIndexFile(elenco);
        // v) assegno all'elenco stato = CHIUSO e lo lascio nella coda degli elenchi da elaborare assegnando stato =
        // CHIUSO
        elenco.setTiStatoElenco(ElencoStatusEnum.CHIUSO.name());
        ElvElencoVersDaElab elencoVersDaElab = elencoHelper.retrieveElencoInQueue(elenco);
        elencoVersDaElab.setTiStatoElenco(ElencoStatusEnum.CHIUSO.name());
        elencoVersDaElab.setTsStatoElenco(new Date());
        // vi) assegno ad ogni unità doc appartenente all'elenco stato = IN_ELENCO_CHIUSO
        elencoHelper.setUdsStatus(elenco, UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
        // vii) assegno ad ogni documento appartenente all'elenco stato = IN_ELENCO_CHIUSO
        elencoHelper.setDocsStatus(elenco, UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
        // viii) assegno ad ogni aggiornamento per unità doc appartenente all'elenco stato = IN_ELENCO_CHIUSO
        elencoHelper.setUpdsStatus(elenco, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO);
        elencoHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), OpTypeEnum.CHIUSURA_ELENCO.name(), logJob);
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                "CREAZIONE_INDICE_ELENCO_VERS", null, ElvStatoElencoVer.TiStatoElenco.CHIUSO, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaUrnUdElenco(long idUnitaDoc, long idElencoVers)
            throws ParerUserError, ParseException, ParerInternalError {
        log.debug("verificaUrnUdElenco - idUnitaDoc " + idUnitaDoc + " idElencoVers " + idElencoVers);
        AroUnitaDoc aroUnitaDoc = elencoHelper.findByIdWithLock(AroUnitaDoc.class, idUnitaDoc);
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        CSVersatore versatore = this.getVersatoreUd(aroUnitaDoc, sistemaConservazione);
        CSChiave chiave = this.getChiaveUd(aroUnitaDoc);

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
        log.debug("verificaUrnUdElenco - controllo e calcolo URN normalizzato");
        ElvVLisAllUdByElenco elvVLisAllUdByElenco = elencoHelper.retrieveElvVLisAllUdByElenco(idElencoVers, idUnitaDoc);
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
        log.debug("verificaUrnUdElenco - verifica pregresso");
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

        log.debug("verificaUrnUdElenco - ultima versione AIP");
        AroVerIndiceAipUd aroVerIndiceAipUd = elencoHelper.getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
        if (aroVerIndiceAipUd != null && !aroVerIndiceAipUd.getDtCreazione().after(dataInizio)) {
            // eseguo registra urn aip pregressi
            log.debug("verificaUrnUdElenco - eseguo registra urn aip pregressi");
            urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);
        }
    }

    public CSChiave getChiaveUd(AroUnitaDoc ud) {
        CSChiave csc = new CSChiave();
        csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
        csc.setNumero(ud.getCdKeyUnitaDoc());

        return csc;
    }

    public CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(ud.getOrgStrut().getNmStrut());
        csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        csv.setSistemaConservazione(sistemaConservazione);

        return csv;
    }

    public void calcolaUrnElenco(ElvElencoVer elenco, String nomeStruttura, String nomeStrutturaNorm, String nomeEnte,
            String nomeEnteNorm) {
        log.debug("calcolaUrnElenco");
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

    public void buildIndexFile(ElvElencoVer elenco)
            throws ParerNoResultException, NoSuchAlgorithmException, IOException {
        byte[] indexFile = null;
        log.debug("buildIndexFile");
        // creo il file indice_conservazione.xml
        log.info("creazione indice per elenco id '" + elenco.getIdElencoVers() + "' appartenente alla struttura '"
                + elenco.getOrgStrut().getIdStrut() + "'");
        indexFile = indiceEjb.createIndex(elenco, false);
        // registro il file indice_conservazione.xml (in ELV_FILE_ELENCO_VERS)
        ElvFileElencoVer elvFileElencoVers = elencoHelper.storeFileIntoElenco(elenco, indexFile,
                FileTypeEnum.INDICE.name());
        // EVO#16486
        /* Calcolo e persisto lo urn dell'indice dell'elenco */
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
        log.debug("buildIndexFile - salvo URN");
        urnHelper.salvaUrnElvFileElencoVers(
                elvFileElencoVers, MessaggiWSFormat.formattaUrnElencoIndice(tmpUrn,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvFileElencoVers(
                elvFileElencoVers, MessaggiWSFormat.formattaUrnElencoIndice(tmpUrnNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnFileElencoVers.TiUrnFileElenco.NORMALIZZATO);
        // end EVO#16486

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void manageValidAtomic(long idElenco, long idStruttura, long idLogJob) throws ParseException {
        log.debug("manageValidAtomic");
        LogJob logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        elencoHelper.lockElenco(elenco);

        // Controllo se almeno una unità doc appartenente all'elenco e' annullata
        boolean annullata = elencoHelper.checkUdAnnullataByElenco(elenco);
        if (!annullata) { // ud non annullata
            manageValid(elenco);
            elencoHelper.writeLogElencoVers(elenco, struttura, OpTypeEnum.VALIDAZIONE_ELENCO.name(), logJob);
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()),
                    "VALIDA_INDICE_ELENCO_VERS",
                    "Validazione indice elenco in cui non sono presenti unità documentarie annullate",
                    ElvStatoElencoVer.TiStatoElenco.VALIDATO, null);
        } else {
            log.debug("manageValidAtomic - chiamo evEjb.deleteElenco(idElenco) per elenco " + idElenco + " e struttura "
                    + idStruttura);
            evEjb.deleteElenco(idElenco);
        }
    }

    public void manageValid(ElvElencoVer elenco) {
        // iv) aggiorno l'elenco valorizzando la data di firma, il tipo di validazione e la modalità con i valori
        // definiti dall'elenco.
        log.debug("manageValid");
        elenco.setDtFirmaIndice(new Date());
        /* TODO 19304 DA TOGLIERE elenchiversamento1.15 pag. 27 */
        // se i valori nell'elenco sono nulli leggo i valori dal criterio di raggruppamento
        if (elenco.getTiValidElenco() == null || elenco.getTiModValidElenco() == null) {
            elenco.setTiValidElenco(TiValidElenco.valueOf(elenco.getDecCriterioRaggr().getTiValidElenco().name()));
            elenco.setTiModValidElenco(
                    TiModValidElenco.valueOf(elenco.getDecCriterioRaggr().getTiModValidElenco().name()));
        }
        // v) assegno all'elenco stato = VALIDATO e lo lascio nella coda degli elenchi da elaborare assegnando stato =
        // validato
        elenco.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
        ElvElencoVersDaElab elencoVersDaElab = elencoHelper.retrieveElencoInQueue(elenco);
        elencoVersDaElab.setTiStatoElenco(ElencoStatusEnum.VALIDATO.name());
        elencoVersDaElab.setTsStatoElenco(new Date());
        // vi) assegno ad ogni unità doc appartenente all'elenco stato = IN_ELENCO_VALIDATO
        elencoHelper.setUdsStatus(elenco, UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
        // vii) assegno ad ogni documento appartenente all'elenco stato = IN_ELENCO_VALIDATO
        elencoHelper.setDocsStatus(elenco, UdDocStatusEnum.IN_ELENCO_VALIDATO.name());
        // viii) assegno ad ogni aggiornamento per unità doc appartenente all'elenco stato = IN_ELENCO_VALIDATO
        elencoHelper.setUpdsStatus(elenco, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO);
    }
}
