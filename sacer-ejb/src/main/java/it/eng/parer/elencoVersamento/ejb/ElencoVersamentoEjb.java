package it.eng.parer.elencoVersamento.ejb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
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

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObj;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObjComparatorAnnoDtCreazione;
import it.eng.parer.elencoVersamento.utils.UpdDocUdObjComparatorDtCreazione;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.ElvUrnElencoVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.ElvVChkAddDocAgg;
import it.eng.parer.viewEntity.ElvVChkAddUpdUd;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import javax.interceptor.ExcludeClassInterceptors;

/**
 *
 * @author Agati_D
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElencoVersamentoEjb {

    Logger log = LoggerFactory.getLogger(ElencoVersamentoEjb.class);
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
    @Resource
    private SessionContext context;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configHelper;

    public ElencoVersamentoEjb() {
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void buildElencoVersamento(LogJob logJob) throws Exception {
        log.info("CAE - Creazione automatica elenchi versamento...");

        /* MEV 15631 PER OTTENERE I DATI PER REGISTRARE */
        // Recupero la lista degli ambienti
        List<OrgAmbiente> ambienti = elencoHelper.retrieveAmbienti();
        for (OrgAmbiente ambiente : ambienti) {
            BigDecimal idAmbiente = BigDecimal.valueOf(ambiente.getIdAmbiente());

            // Ricavo i parametri
            String DATA_SCAD_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplic("DATA_SCAD_CHIUSURA_ELV_FISC",
                    idAmbiente, null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE);
            String NI_GG_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplic("NI_GG_CHIUSURA_ELV_FISC", idAmbiente,
                    null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE);
            String ORARIO_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplic("ORARIO_CHIUSURA_ELV_FISC", idAmbiente,
                    null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE);
            String ANNO_CHIUSURA_ELV_FISC = configHelper.getValoreParamApplic("ANNO_CHIUSURA_ELV_FISC", idAmbiente,
                    null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE);
            // Anno e Orario corrente
            Calendar calCorrente = Calendar.getInstance();
            int annoCorrente = calCorrente.get(Calendar.YEAR);
            int oraCorrente = calCorrente.get(Calendar.HOUR_OF_DAY);
            int minutoCorrente = calCorrente.get(Calendar.MINUTE);

            int niGg = Integer.parseInt(NI_GG_CHIUSURA_ELV_FISC);
            int anno = Integer.parseInt(ANNO_CHIUSURA_ELV_FISC);

            // Data scad
            String dataScadChiusuraElvFisc = DATA_SCAD_CHIUSURA_ELV_FISC.concat("_" + annoCorrente).replace("_", "/");
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            cal.setTime(sdf.parse(dataScadChiusuraElvFisc));
            Date dataScadChiusuraElvFiscDate = cal.getTime();
            // Data scad - gg
            Calendar calMenoGg = Calendar.getInstance();
            calMenoGg.setTime(sdf.parse(dataScadChiusuraElvFisc));
            calMenoGg.add(Calendar.DATE, -niGg);
            Date dataScadChiusuraElvFiscDateMenoGg = calMenoGg.getTime();

            Date dataCorrente = new Date();
            // Se data corrente rientra nel range di date determinato
            if (dataCorrente.after(dataScadChiusuraElvFiscDateMenoGg)
                    && dataCorrente.before(dataScadChiusuraElvFiscDate)) {
                String[] orarioChiusuraElvFisc = ORARIO_CHIUSURA_ELV_FISC.split("_");
                int oraChiusura = Integer.parseInt(orarioChiusuraElvFisc[0]);
                int minutoChiusura = Integer.parseInt(orarioChiusuraElvFisc[1]);

                if (oraCorrente >= oraChiusura && minutoCorrente >= minutoChiusura) {
                    // ricavo le strutture List<OrgStrut>
                    List<OrgStrut> struttureByAmb = elencoHelper.retrieveStruttureByAmb(idAmbiente);

                    for (OrgStrut strut : struttureByAmb) {
                        List<ElvElencoVer> elenchi = evEjb.getElenchiFiscaliByStrutturaAperti(strut.getIdStrut(), anno);
                        for (ElvElencoVer elenco : elenchi) {
                            ElencoVersamentoEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);
                            log.debug("CAV - trovato elenco " + elenco.getIdElencoVers()
                                    + " fiscale da settare stato = DA_CHIUDERE");
                            newElencoEjbRef1.setDaChiudereFiscAtomic(
                                    ElencoEnums.MotivazioneChiusura.ELENCO_CHIUSURA_ANTICIP.message(),
                                    elenco.getIdElencoVers(), strut.getIdStrut(), logJob.getIdLogJob());
                        }
                    }
                }
            }
        }
        // ricavo le strutture
        List<OrgStrut> strutture = elencoHelper.retrieveStrutture();

        log.debug("numero strutture trovate = " + strutture.size());
        for (OrgStrut struttura : strutture) {
            log.debug("CAE - processo struttura: " + struttura.getIdStrut());
            manageStrut(struttura.getIdStrut(), logJob);
        }
        jobHelper.writeLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(),
                ElencoEnums.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    public void manageStrut(long idStruttura, LogJob logJob) throws Exception {
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // gestisco gli elenchi scaduti
        log.info("CAE - Struttura: id ='" + idStruttura + "' nome = '" + struttura.getNmStrut() + "'");
        elaboraElenchiScaduti(idStruttura, logJob.getIdLogJob());
        /*
         * determino tutti i criteri di raggruppamento appartenenti alla struttura versante corrente, il cui intervallo
         * (data istituzione - data soppressione) includa la data corrente (con estremi compresi); i criteri sono
         * selezionati in ordine di data istituzione
         */
        List<DecCriterioRaggr> criteriRaggr = elencoHelper.retrieveCriterioByStrut(struttura, logJob.getDtRegLogJob());
        for (DecCriterioRaggr criterio : criteriRaggr) {
            log.debug("CAE - Criterio della struttura '" + struttura.getNmStrut() + "' trovato: nome criterio = '"
                    + criterio.getNmCriterioRaggr() + "' (id = '" + criterio.getIdCriterioRaggr() + "')");

            /* Definisco numero elenchi creati in un giorno nullo */
            Long numElenchi = null;
            /* Determino se per il criterio il numero massimo di elenchi che si può creare in un giorno e' non nullo */
            if (criterio.getNiMaxElenchiByGg() != null) {
                long countElenchiNonAperti = elencoHelper
                        .countElenchiGgByCritNonAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                long countElenchiAperti = elencoHelper
                        .countElenchiGgByCritAperti(new BigDecimal(criterio.getIdCriterioRaggr()));
                if (countElenchiNonAperti >= criterio.getNiMaxElenchiByGg().longValue()) {
                    continue;
                } else {
                    numElenchi = countElenchiNonAperti + countElenchiAperti;
                }
            }

            Comparator<UpdDocUdObj> comp = new UpdDocUdObjComparatorDtCreazione();
            if (criterio.getAaKeyUnitaDoc() == null && criterio.getAaKeyUnitaDocDa() == null
                    && criterio.getAaKeyUnitaDocA() == null) {
                comp = new UpdDocUdObjComparatorAnnoDtCreazione();
            }

            /*
             * Determino le Unità Documentarie, i Documenti Aggiunti e gli Aggiornamenti Metadati che soddisfano il
             * criterio corrente
             */
            List<UpdDocUdObj> updDocUdObjectList = elencoHelper.retrieveUpdDocUdToProcess(criterio);
            log.debug("CAE - Trovati " + updDocUdObjectList.size() + " oggetti versati relativi al criterio '"
                    + criterio.getNmCriterioRaggr() + "'");

            Collections.sort(updDocUdObjectList, comp);

            ElencoVersamentoEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);
            boolean isTheFirst = true;
            try {
                // Itero l'insieme
                Iterator<UpdDocUdObj> i = updDocUdObjectList.iterator();
                while (i.hasNext()) {
                    // Recupera l'elemento e sposta il cursore all'elemento successivo
                    UpdDocUdObj o = (UpdDocUdObj) i.next();
                    // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"), mi serve per capire
                    // se è l'ultimo elemento
                    numElenchi = newElencoEjbRef1.manageUpdDocUdObj(criterio.getIdCriterioRaggr(),
                            struttura.getIdStrut(), logJob.getIdLogJob(), o, !i.hasNext(), isTheFirst, numElenchi);
                    if (numElenchi != null && numElenchi > criterio.getNiMaxElenchiByGg().longValue()) {
                        // Passa al criterio successivo
                        break;
                    }
                    isTheFirst = false;
                }
            } catch (ParerInternalError ex) {
                log.warn("Attenzione: possibile errore nella configurazione del criterio. Salto a quello successivo");
            }
        }
        elaboraElenchiVuoti(idStruttura, logJob.getIdLogJob());
        // MEV#27169
        /* Cambio stato alle ud/doc/upd della struttura corrente, non selezionate dai criteri */
        // elencoHelper.atomicSetNonElabSched(struttura, logJob);
        // end MEV#27169
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long manageUpdDocUdObj(long idCriterio, long idStruttura, Long idLogJob, UpdDocUdObj updDocUdObj,
            boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        Long numElenchiTmp = null;
        switch (updDocUdObj.getTiEntitaSacer()) {
        case UNI_DOC:
            numElenchiTmp = manageUd(updDocUdObj.getId(), updDocUdObj.getAaKeyUnitaDoc(), idCriterio, idStruttura,
                    idLogJob, isTheLast, isTheFirst, numElenchi);
            break;
        case DOC:
            numElenchiTmp = manageDoc(updDocUdObj.getId(), updDocUdObj.getAaKeyUnitaDoc(), idCriterio, idStruttura,
                    idLogJob, isTheLast, isTheFirst, numElenchi);
            break;
        case UPD:
            numElenchiTmp = manageUpd(updDocUdObj.getId(), updDocUdObj.getAaKeyUnitaDoc(), idCriterio, idStruttura,
                    idLogJob, isTheLast, isTheFirst, numElenchi);
            break;
        }

        return numElenchiTmp;
    }

    // MEV#27169
    @ExcludeClassInterceptors
    public void manageUdJms(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio, long idStruttura, Long idLogJob,
            boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        this.manageUd(udId, aaKeyUnitaDoc, idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst, numElenchi);
    }
    // end MEV#27169

    private Long manageUd(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio, long idStruttura, Long idLogJob,
            boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        boolean isExpired = false;
        ElvElencoVer elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // MEV#27169
        LogJob logJob = null;
        if (idLogJob != null) {
            logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        }
        // end MEV#27169
        DecCriterioRaggr criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        AroUnitaDoc ud = elencoHelper.retrieveUnitaDocById(udId.longValue());

        /* a) Prendo il LOCK esclusivo su ud */
        elencoHelper.lockUnitaDoc(ud);
        /* b) Definisco elenco corrente */
        if (numElenchi != null) {
            // per il criterio il numero elenchi creati in un giorno e' non nullo
            elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
            if (elenco == null) {
                // non ci sono elenchi aperti quindi se aggiungendo un elenco non si supera il numero massimo di elenchi
                // che si può creare in un giorno ne creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura, logJob);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            }
        } else {
            elenco = retrieveElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        }
        /* d) Aggiungo ud corrente all'elenco corrente */
        // calcolo il numero di documenti, il numero di componenti e la somma dei byte dei componenti, della ud
        // corrente,
        // relativamente ai soli documenti con tipo creazione = VERSAMENTO_UNITA_DOC
        long numDocs = elencoHelper.countDocsInUnitaDocCustom(new BigDecimal(ud.getIdUnitaDoc()));
        Object[] numSizeArray = (Object[]) elencoHelper
                .numCompsAndSizeInUnitaDocCustom(new BigDecimal(ud.getIdUnitaDoc()));
        long numComps = (long) numSizeArray[0];
        BigDecimal sizeComps = (BigDecimal) numSizeArray[1];
        // ATTENZIONE: verifico se il numero di componenti della unità documentaria corrente,
        // è inferiore o uguale al numero di componenti che l'elenco corrente può ancora includere
        // (tale numero è definito dal numero massimo di componenti previsto dall'elenco a cui si
        // sottrae il numero di componenti derivanti da unità doc versate già incluse nell'elenco)
        boolean firstCheckUdOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
        if (firstCheckUdOk) { // l'ud sta nell'elenco: aggiungo.
            log.debug("aggiungo l'unita documentaria '" + ud.getIdUnitaDoc() + "' all'elenco");
            /* Aggiunta ud */
            addUnitaDocIntoElenco(ud, elenco, numDocs, numComps, sizeComps);
        } else {
            /* Chiusura elenco esaurito */
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                    "L’aggiunta di una unità documentaria o di un documento o di un aggiornamento metadati provoca il superamento del numero massimo di componenti",
                    tiStatoelenco, null);
            /* Creazione elenco per criterio */
            if (numElenchi != null) {
                // per il criterio il numero elenchi creati in un giorno e' non nullo
                // se aggiungendo un elenco non si supera il numero massimo di elenchi che si può creare in un giorno ne
                // creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            } else {
                elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob); // questo volume è managed
            }
            // Aggiugo unità doc ad elenco sono dopo aver controllato se ci sta. Se non ci sta è un problema di
            // configurazione del criterio
            boolean secondCheckUdOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
            if (secondCheckUdOk) { // l'ud sta nell'elenco: aggiugo.
                log.debug("aggiungo l'unita documentaria '" + ud.getIdUnitaDoc() + "' all'elenco");
                addUnitaDocIntoElenco(ud, elenco, numDocs, numComps, sizeComps);
            } else {
                log.warn("ATTENZIONE non è possibile aggiungere l'ud '" + ud.getIdUnitaDoc()
                        + "' all'elenco. Possibile errore nella definizione del criterio");
                throw new ParerInternalError("ATTENZIONE non è possibile aggiungere l'ud '" + ud.getIdUnitaDoc()
                        + "' all'elenco. Possibile errore nella definizione del criterio");
            }
        }
        /* f) Verifico se l'elenco corrente è scaduto */
        isExpired = checkElencoExpired(elenco);
        if (isExpired) {
            // vedere se dare a closeReason scope piu ampio
            String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                    "Elenco scaduto", tiStatoelenco, null);
        }
        /* h) Se l'elemento corrente è l'ultimo e se l'elenco corrente ha stato = APERTO */
        if (isTheLast && elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.APERTO.name())) {
            manageLast(elenco, struttura, logJob);
        }

        return numElenchi;
    }

    // MEV#27169
    @ExcludeClassInterceptors
    public void manageDocJms(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio, long idStruttura,
            Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        this.manageDoc(udId, aaKeyUnitaDoc, idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst, numElenchi);
    }
    // end MEV#27169

    private Long manageDoc(BigDecimal idDoc, BigDecimal aaKeyUnitaDoc, long idCriterio, long idStruttura, Long idLogJob,
            boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        boolean isExpired = false;
        ElvElencoVer elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // MEV#27169
        LogJob logJob = null;
        if (idLogJob != null) {
            logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        }
        // end MEV#27169
        DecCriterioRaggr criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        AroDoc doc = elencoHelper.retrieveDocById(idDoc.longValue());
        /* a) prendo LOCK escusivo su doc */
        elencoHelper.lockDoc(doc);
        /* b) Definisco elenco corrente */
        if (numElenchi != null) {
            // per il criterio il numero elenchi creati in un giorno e' non nullo
            elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
            if (elenco == null) {
                // non ci sono elenchi aperti quindi se aggiungendo un elenco non si supera il numero massimo di elenchi
                // che si può creare in un giorno ne creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura, logJob);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            }
        } else {
            elenco = retrieveElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        }
        /* d) Aggiungo doc corrente all'elenco corrente */
        // calcolo il numero di componenti e la somma dei byte dei componenti, del documento corrente
        Object[] numSizeArray = (Object[]) elencoHelper.numCompsAndSizeInDoc(new BigDecimal(doc.getIdDoc()));
        long numComps = (long) numSizeArray[0];
        BigDecimal sizeComps = (BigDecimal) numSizeArray[1];
        // ATTENZIONE: verifico se il numero di componenti del documento corrente, è inferiore o uguale al numero di
        // componenti che l'elenco può ancora includere
        // (tale numero è definito dal numero massimo di componenti previsto dal volume a cui si sottrae
        // il numero di componenti derivanti da unità doc versate già incluse nell'elenco, il numero di componenti
        // derivanti da documenti già inclusi nell'elenco
        // ed il numero di aggiornamento metadati per unità doc già inclusi nell’elenco)
        boolean firstCheckDocOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
        if (firstCheckDocOk) { // il doc sta nell'elenco: aggiungo.
            log.debug("aggiungo documento '" + doc.getIdDoc() + "' all'elenco");
            /* Aggiunta doc */
            addDocIntoElenco(doc, elenco, numComps, sizeComps);
        } else {
            /* Chiusura elenco esaurito */
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco, struttura, logJob);
            // EVO 19304

            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                    "L’aggiunta di una unità documentaria o di un documento o di un aggiornamento metadati provoca il superamento del numero massimo di componenti",
                    ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE, null);
            /* Creazione elenco per criterio */
            if (numElenchi != null) {
                // per il criterio il numero elenchi creati in un giorno e' non nullo
                // se aggiungendo un elenco non si supera il numero massimo di elenchi che si può creare in un giorno ne
                // creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            } else {
                elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob); // questo volume è managed
            }
            // Aggiungo il doc ad elenco solo dopo aver controllato se ci sta. Se non ci sta è un problema di
            // configurazione del criterio
            boolean secondCheckDocOk = elencoHelper.checkFreeSpaceElenco(elenco, numComps);
            if (secondCheckDocOk) { // il doc sta nell'elenco: aggiungo.
                log.debug("aggiungo documento '" + doc.getIdDoc() + "' all'elenco");
                addDocIntoElenco(doc, elenco, numComps, sizeComps);
            } else {
                log.warn("ATTENZIONE non è possibile aggiungere il doc '" + doc.getIdDoc()
                        + "' all'elenco. Possibile errore nella definizione del criterio");
                throw new ParerInternalError("ATTENZIONE non è possibile aggiungere il doc '" + doc.getIdDoc()
                        + "' all'elenco. Possibile errore nella definizione del criterio");
            }
        }

        /* f) Verifico se l'elenco corrente è scaduto */
        isExpired = checkElencoExpired(elenco);
        if (isExpired) {
            // vedere se dare a closeReason scope piu ampio
            String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                    "Elenco scaduto", tiStatoelenco, null);
        }
        /* h) Se l'elemento corrente è l'ultimo e se l'elenco corrente ha stato = APERTO */
        if (isTheLast && elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.APERTO.name())) {
            manageLast(elenco, struttura, logJob);
        }

        return numElenchi;
    }

    // MEV#27169
    @ExcludeClassInterceptors
    public void manageUpdJms(BigDecimal udId, BigDecimal aaKeyUnitaDoc, long idCriterio, long idStruttura,
            Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        this.manageUpd(udId, aaKeyUnitaDoc, idCriterio, idStruttura, idLogJob, isTheLast, isTheFirst, numElenchi);
    }
    // end MEV#27169

    private Long manageUpd(BigDecimal idUpdUnitaDoc, BigDecimal aaKeyUnitaDoc, long idCriterio, long idStruttura,
            Long idLogJob, boolean isTheLast, boolean isTheFirst, Long numElenchi) throws Exception {
        boolean isExpired = false;
        ElvElencoVer elenco = null;
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        // MEV#27169
        LogJob logJob = null;
        if (idLogJob != null) {
            logJob = elencoHelper.retrieveLogJobByid(idLogJob);
        }
        // end MEV#27169
        DecCriterioRaggr criterio = elencoHelper.retrieveCriterioByid(idCriterio);
        AroUpdUnitaDoc upd = elencoHelper.retrieveUpdById(idUpdUnitaDoc.longValue());
        /* a) prendo LOCK escusivo su upd */
        elencoHelper.lockUpd(upd);
        /* b) Definisco elenco corrente */
        if (numElenchi != null) {
            // per il criterio il numero elenchi creati in un giorno e' non nullo
            elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
            if (elenco == null) {
                // non ci sono elenchi aperti quindi se aggiungendo un elenco non si supera il numero massimo di elenchi
                // che si può creare in un giorno ne creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura, logJob);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            }
        } else {
            elenco = retrieveElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        }
        /* d) Aggiungo upd corrente all'elenco corrente */
        // ATTENZIONE: verifico se il numero di componenti che l'elenco può ancora includere
        // (tale numero è definito dal numero massimo di componenti previsto dal volume a cui si sottrae
        // il numero di componenti derivanti da unità doc versate già incluse nell'elenco, il numero di componenti
        // derivanti da documenti già inclusi nell'elenco
        // ed il numero di aggiornamento metadati per unità doc già inclusi nell’elenco) e' maggiore o uguale ad 1
        boolean firstCheckUpdOk = elencoHelper.checkFreeSpaceElenco(elenco, 1L);
        if (firstCheckUpdOk) { // la upd sta nell'elenco: aggiungo.
            log.debug("aggiungo aggiornamento metadati'" + upd.getIdUpdUnitaDoc() + "' all'elenco");
            /* Aggiunta upd */
            addUpdIntoElenco(upd, elenco);
        } else {
            /* Chiusura elenco esaurito */
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(ElencoEnums.MotivazioneChiusura.ELENCO_FULL.message(), elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                    "L’aggiunta di una unità documentaria o di un documento o di un aggiornamento metadati provoca il superamento del numero massimo di componenti",
                    tiStatoelenco, null);
            /* Creazione elenco per criterio */
            if (numElenchi != null) {
                // per il criterio il numero elenchi creati in un giorno e' non nullo
                // se aggiungendo un elenco non si supera il numero massimo di elenchi che si può creare in un giorno ne
                // creo uno nuovo
                numElenchi++;
                if (numElenchi <= criterio.getNiMaxElenchiByGg().longValue()) {
                    elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob);
                } else {
                    // se elenco corrente non e' definito, passa al criterio successivo
                    return numElenchi;
                }
            } else {
                elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob); // questo volume è managed
            }
            // Aggiungo la upd ad elenco solo dopo aver controllato se ci sta. Se non ci sta è un problema di
            // configurazione del criterio
            boolean secondCheckUpdOk = elencoHelper.checkFreeSpaceElenco(elenco, 1L);
            if (secondCheckUpdOk) { // la upd sta nell'elenco: aggiungo.
                log.debug("aggiungo aggiornamento metadati '" + upd.getIdUpdUnitaDoc() + "' all'elenco");
                addUpdIntoElenco(upd, elenco);
            } else {
                log.warn("ATTENZIONE non è possibile aggiungere la upd '" + upd.getIdUpdUnitaDoc()
                        + "' all'elenco. Possibile errore nella definizione del criterio");
                throw new ParerInternalError("ATTENZIONE non è possibile aggiungere la upd '" + upd.getIdUpdUnitaDoc()
                        + "' all'elenco. Possibile errore nella definizione del criterio");
            }
        }

        /* f) Verifico se l'elenco corrente è scaduto */
        isExpired = checkElencoExpired(elenco);
        if (isExpired) {
            // vedere se dare a closeReason scope piu ampio
            String closeReason = ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message();
            // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
            gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
            // MAC 26737
            ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                    .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                            : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                    "Elenco scaduto", tiStatoelenco, null);
        }
        /* h) Se l'elemento corrente è l'ultimo e se l'elenco corrente ha stato = APERTO */
        if (isTheLast && elenco.getTiStatoElenco().equals(ElencoEnums.ElencoStatusEnum.APERTO.name())) {
            manageLast(elenco, struttura, logJob);
        }

        return numElenchi;
    }

    public ElvElencoVer retrieveElenco(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc, OrgStrut struttura,
            LogJob logJob, boolean isTheFirst) throws Exception {
        ElvElencoVer elenco = findOpenedElenco(criterio, aaKeyUnitaDoc, struttura, logJob, isTheFirst);
        if (elenco == null) {
            // non ci sono elenchi aperti quindi ne creo uno nuovo
            elenco = createNewElenco(criterio, aaKeyUnitaDoc, struttura, logJob);
        }
        return elenco;
    }

    public ElvElencoVer findOpenedElenco(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc, OrgStrut struttura,
            LogJob logJob, boolean isTheFirst) throws Exception {
        ElvElencoVer elenco = null;
        // Recupero l'elenco aperto per il criterio corrente
        try {
            elenco = elencoHelper.retrieveElencoByCriterio(criterio, aaKeyUnitaDoc, struttura); // questo elenco è
            // managed
            BigDecimal numCompVers = elenco.getNiCompVersElenco();
            BigDecimal numCompAgg = elenco.getNiCompAggElenco();
            BigDecimal numUpdUd = elenco.getNiUpdUnitaDoc();
            int sommaCompAggCompVersUpd = numCompVers.intValue() + numCompAgg.intValue() + numUpdUd.intValue();
            log.debug("CAE - Elenco aperto trovato: nome = " + elenco.getNmElenco() + "; data di scadenza = "
                    + dateToString(elenco.getDtScadChius()) + "; numero componenti versati = "
                    + elenco.getNiCompVersElenco() + "; numero componenti aggiunti= " + elenco.getNiCompAggElenco()
                    + "; numero aggiornamenti metadati= " + elenco.getNiUpdUnitaDoc() + "; per un totale di "
                    + sommaCompAggCompVersUpd);
            // Registro nel log solo se è il primo elemento, non ogni volta che passo di qua
            if (isTheFirst) {
                elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.RECUPERA_ELENCO_APERTO.name(),
                        logJob);
            }
        } catch (ParerNoResultException ex) {
            elenco = null;
        }
        return elenco;
    }

    public ElvElencoVer createNewElenco(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc, OrgStrut struttura,
            LogJob logJob) throws Exception {
        ElvElencoVer elenco;
        log.debug("CAE - Nessun elenco aperto trovato. Ne creo uno nuovo");
        elenco = createElencoByCriterio(criterio, aaKeyUnitaDoc, struttura, logJob); // questo volume è managed
        return elenco;
    }

    private ElvElencoVer createElencoByCriterio(DecCriterioRaggr criterio, BigDecimal aaKeyUnitaDoc, OrgStrut struttura,
            LogJob logJob) {
        log.debug("CEC - Crea elenco da criterio");
        Date systemDate = new Date();
        ElvElencoVer elenco = new ElvElencoVer();
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.APERTO.name());
        // se il nome dell'elenco è troppo lungo lo tronco per evitare problemi con il DB
        String nome = null;
        String descrizione = null;
        int nameLimit = 66;
        int descLimit = 250;
        boolean tuttiAnniKeyUnitaDocNulli = criterio.getAaKeyUnitaDoc() == null && criterio.getAaKeyUnitaDocDa() == null
                && criterio.getAaKeyUnitaDocA() == null;
        // Eventualmente tronca onde evitare problemi di scrittura sul DB per nomi troppo lunghi
        if (tuttiAnniKeyUnitaDocNulli) {
            if (criterio.getNmCriterioRaggr().length() > nameLimit) {
                nome = criterio.getNmCriterioRaggr().substring(0, nameLimit) + "_" + aaKeyUnitaDoc + "_"
                        + dateToString(systemDate);
            } else {
                nome = criterio.getNmCriterioRaggr() + "_" + aaKeyUnitaDoc + "_" + dateToString(systemDate);
            }
            if (criterio.getDsCriterioRaggr().length() > descLimit) {
                descrizione = criterio.getDsCriterioRaggr().substring(0, descLimit) + "_" + aaKeyUnitaDoc;
            } else {
                descrizione = criterio.getDsCriterioRaggr() + "_" + aaKeyUnitaDoc;
            }
        } else {
            nameLimit = 70;
            if (criterio.getNmCriterioRaggr().length() > nameLimit) {
                nome = criterio.getNmCriterioRaggr().substring(0, nameLimit) + "_" + dateToString(systemDate);
            } else {
                nome = criterio.getNmCriterioRaggr() + "_" + dateToString(systemDate);
            }
            descrizione = criterio.getDsCriterioRaggr();
            elenco.setAaKeyUnitaDoc(aaKeyUnitaDoc);
        }

        elenco.setNmElenco(nome);
        elenco.setDsElenco(descrizione);

        elenco.setOrgStrut(struttura);
        elenco.setDtCreazioneElenco(systemDate);

        elenco.setNiMaxComp(criterio.getNiMaxComp());

        elenco.setTiScadChius(criterio.getTiScadChiusVolume());
        elenco.setNiTempoScadChius(criterio.getNiTempoScadChius());
        elenco.setTiTempoScadChius(criterio.getTiTempoScadChius());
        elenco.setDecCriterioRaggr(criterio);
        elenco.setNiSizeVersElenco(BigDecimal.ZERO);
        elenco.setNiUnitaDocVersElenco(BigDecimal.ZERO);
        elenco.setNiDocVersElenco(BigDecimal.ZERO);
        elenco.setNiCompVersElenco(BigDecimal.ZERO);
        elenco.setNiUpdUnitaDoc(BigDecimal.ZERO);
        elenco.setNiSizeAggElenco(BigDecimal.ZERO);
        elenco.setNiUnitaDocModElenco(BigDecimal.ZERO);
        elenco.setNiDocAggElenco(BigDecimal.ZERO);
        elenco.setNiCompAggElenco(BigDecimal.ZERO);

        // Calcola la data di scadenza dell'elenco
        Date expirationDate = calculateExpirationDate(elenco);
        elenco.setDtScadChius(expirationDate);
        // indicazione di elenco standard pari al valore specificato dal criterio
        elenco.setFlElencoStandard(criterio.getFlCriterioRaggrStandard());
        // indicazione di elenco fiscale pari al valore specificato dal criterio
        elenco.setFlElencoFisc(criterio.getFlCriterioRaggrFisc());

        // MEV 24534 Imposto subito lo stato di validazione a NO_INDICE per gestire la chiusura
        if (it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_INDICE
                .equals(criterio.getTiValidElenco())) {
            elenco.setTiValidElenco(it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE);
        }

        /* Registro l'elenco creato nella coda degli elenchi da elaborare ELV_ELENCO_VERS_DA_ELAB */
        ElvElencoVersDaElab elencoVersDaElab = new ElvElencoVersDaElab();
        elencoVersDaElab.setElvElencoVer(elenco);
        elencoVersDaElab.setIdCriterioRaggr(new BigDecimal(criterio.getIdCriterioRaggr()));
        elencoVersDaElab.setIdStrut(new BigDecimal(struttura.getIdStrut()));
        elencoVersDaElab.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.APERTO.name());
        elencoVersDaElab.setTsStatoElenco(new Date());
        if (tuttiAnniKeyUnitaDocNulli) {
            elencoVersDaElab.setAaKeyUnitaDoc(aaKeyUnitaDoc);
        }

        List<ElvElencoVersDaElab> elencoVersDaElabList = new ArrayList<>();
        elencoVersDaElabList.add(elencoVersDaElab);
        elenco.setElvElencoVersDaElabs(elencoVersDaElabList);

        elenco = elencoHelper.writeNewElenco(elenco);
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "CREAZIONE_ELENCO_VERS", null,
                ElvStatoElencoVer.TiStatoElenco.APERTO, null);

        elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.CREA_ELENCO.name(), logJob);
        log.debug("CVC - Creato nuovo elenco: nome = " + elenco.getNmElenco() + "; data scadenza = "
                + dateToString(elenco.getDtScadChius()));
        return elenco;
    }

    private void addUnitaDocIntoElenco(AroUnitaDoc ud, ElvElencoVer elenco, long numDocs, long numComp,
            BigDecimal sizeComps) {
        log.debug("Num doc da agg: " + numDocs + "; num comp da agg: " + numComp + "; sizeComps: " + sizeComps);
        // aggiorno l'elenco corrente, incrementando di 1 il numero delle unità doc versate incluse nell'elenco
        elenco.setNiUnitaDocVersElenco(elenco.getNiUnitaDocVersElenco().add(BigDecimal.ONE));
        // aggiorno elenco incrementando il numero documenti, il numero componenti ed il numero di byte versati
        elenco.setNiDocVersElenco(elenco.getNiDocVersElenco().add(new BigDecimal(numDocs)));
        elenco.setNiCompVersElenco(elenco.getNiCompVersElenco().add(new BigDecimal(numComp)));
        if (sizeComps == null) {
            sizeComps = BigDecimal.ZERO;
        }
        elenco.setNiSizeVersElenco(elenco.getNiSizeVersElenco().add(sizeComps));
        // aggiorno l'unità documentaria corrente, assegnando stato = IN_ELENCO_APERTO e valorizzando la FK all'elenco
        // corrente
        ud.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_APERTO.name());
        ud.setElvElencoVer(elenco);
        elenco.getAroUnitaDocs().add(ud);
        // elimino l'unità documentaria corrente dalla coda delle unità documentarie da elaborare per gli elenchi
        elencoHelper.deleteUdDocFromQueue(ud);
    }

    private void addDocIntoElenco(AroDoc doc, ElvElencoVer elenco, long numComps, BigDecimal sizeComps) {
        /* (i), (ii) */
        if (checkAddDocAgg(elenco, doc)) {
            log.debug("Num comps da agg: " + numComps + "; sizeComps: " + sizeComps);
            // aggiorno l'elenco, incrementando di 1 il numero documenti aggiunti dell'elenco
            elenco.setNiDocAggElenco(elenco.getNiDocAggElenco().add(BigDecimal.ONE));
            // aggiorno l'elenco, incrementando il numero componenti ed il numero di byte aggiunti dell'elenco
            elenco.setNiCompAggElenco(elenco.getNiCompAggElenco().add(new BigDecimal(numComps)));
            // la dimensione può essere nulla quando inserisco un componente con tipo supporto = metadati
            if (sizeComps == null) {
                sizeComps = BigDecimal.ZERO;
            }
            elenco.setNiSizeAggElenco(elenco.getNiSizeAggElenco().add(sizeComps));
            // aggiorno il documento, assegnando stato = IN_ELENCO_APERTO e valorizzando la FK all'elenco corrente
            doc.setTiStatoDocElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_APERTO.name());
            doc.setElvElencoVer(elenco);
            elenco.getAroDocs().add(doc);
            // elimino il documento corrente dalla coda dei documenti da elaborare per gli elenchi
            elencoHelper.deleteDocFromQueue(doc);
        }
    }

    private void addUpdIntoElenco(AroUpdUnitaDoc upd, ElvElencoVer elenco) {
        /* (ii), (iii), (iv) */
        if (checkAddUpdUd(elenco, upd)) {
            // aggiorno l'elenco, incrementando di 1 il numero aggiornamenti metadati inclusi nell’elenco
            elenco.setNiUpdUnitaDoc(elenco.getNiUpdUnitaDoc().add(BigDecimal.ONE));
            // aggiorno l'aggiornamento metadati corrente, assegnando stato = IN_ELENCO_APERTO e valorizzando la FK
            // all'elenco corrente
            upd.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_APERTO);
            upd.setElvElencoVer(elenco);
            elenco.getAroUpdUnitaDocs().add(upd);
            // elimino l'aggiornamento metadati corrente dalla coda degli aggiornamenti metadati da elaborare per gli
            // elenchi
            elencoHelper.deleteUpdFromQueue(upd);
        }
    }

    public void elaboraElenchiScaduti(long idStruttura, long logJobId) throws Exception {
        log.debug("CAV - controllo se ci sono elenchi di versamento scaduti");
        // determino gli elenchi con stato APERTO appartenenti alla struttura corrente,
        // la cui scadenza di chiusura sia antecedente all'istante corrente
        List<Long> elenchiScadutiDaProcessare = elencoHelper.retrieveElenchiScadutiDaProcessare(idStruttura);
        ElencoVersamentoEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);

        log.info("CAV - trovati " + elenchiScadutiDaProcessare.size()
                + " elenchi di versamento scaduti da settare stato = DA_CHIUDERE");
        for (Long elencoId : elenchiScadutiDaProcessare) {
            log.debug("CAV - trovato elenco " + elencoId + " scaduto da settare stato = DA_CHIUDERE");
            newElencoEjbRef1.setDaChiudereAtomic(ElencoEnums.MotivazioneChiusura.ELENCO_EXPIRED.message(), elencoId,
                    idStruttura, logJobId);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaChiudereAtomic(String closeReason, Long idElenco, long idStruttura, long logJobId)
            throws Exception {
        log.debug("CAV - setDaChiudereAtomic...");
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        LogJob logJob = elencoHelper.retrieveLogJobByid(logJobId);
        elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.RECUPERA_ELENCO_SCADUTO.name(),
                logJob);
        // MEV_24534 gestione chiusura elenco a seconda dello stato di validazione
        gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
        // MAC 26737
        ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                        : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(elenco.getIdElencoVers()), "ELENCO_VERS_DA_CHIUDERE",
                "Elenco scaduto", tiStatoelenco, null);
    }

    /**
     * Nuovo metodo introdotto con la mev 24534: se l'elenco ha come tipo validazione <em>NO_INDICE</em> non ne verrà
     * creato l'indice di versamento.
     *
     * @param closeReason
     *            ragione per cui viene chiuso l'elenco
     * @param elenco
     *            elenco oggetto di chiusura
     * @param struttura
     *            struttura a cui l'elenco afferisce
     * @param logJob
     *            tracciamento dell'operazione
     *
     * @throws Exception
     *             in caso di errore non gestito
     */
    private void gestisciChiusuraElenco(String closeReason, ElvElencoVer elenco, OrgStrut struttura, LogJob logJob)
            throws Exception {
        // MEV #24534 creazione automatica indice solo con tipo di validazione diversa da NO_INDICE
        if (it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE.equals(elenco.getTiValidElenco())) {
            setChiuso(closeReason, elenco, struttura, logJob);
        } else {
            setDaChiudere(closeReason, elenco, struttura, logJob);
        }

    }

    private void setChiuso(String closeReason, ElvElencoVer elenco, OrgStrut struttura, LogJob logJob)
            throws Exception {
        log.debug("CAE - setChiuso...");

        // determina nome ente e struttura normalizzati e non
        OrgEnte ente = struttura.getOrgEnte();
        String nomeStruttura = struttura.getNmStrut();
        String nomeStrutturaNorm = struttura.getCdStrutNormaliz();
        String nomeEnte = ente.getNmEnte();
        String nomeEnteNorm = ente.getCdEnteNormaliz();
        // Calcolo e persisto lo urn dell'elenco */
        calcolaUrnElenco(elenco, nomeStruttura, nomeStrutturaNorm, nomeEnte, nomeEnteNorm);

        /*
         * Non aggiorno più l'elenco con il numero delle unità doc modificate incluse nell'elenco, lo faccio nella
         * addDocIntoElenco
         */
        // il sistema assegna all'elenco stato = CHIUSO sia nella tabella ELV_ELENCO_VERS, che nella tabella
        // ELV_ELENCO_VERS_DA_ELAB
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.CHIUSO.name());
        (elenco.getElvElencoVersDaElabs().get(0)).setTiStatoElenco(ElencoEnums.ElencoStatusEnum.CHIUSO.name());
        log.debug("CAE - Elenco id = " + elenco.getIdElencoVers() + " impostato con stato "
                + ElencoEnums.ElencoStatusEnum.CHIUSO.name() + " per '" + closeReason + "'");
        // il sistema definisce sull'elenco la data di chiusura ed il motivo di chiusura pari a "Elenco scaduto"
        Date systemDate = new Date();
        elenco.setDtChius(systemDate);
        elenco.setDlMotivoChius(closeReason);
        // il sistema aggiorna l'elenco con il numero delle unità documentarie modificate incluse nell'elenco a causa di
        // documenti aggiunti
        // e/o di aggiornamenti metadati unità doc mediante la vista ELV_V_COUNT_UD_MODIF
        elenco.setNiUnitaDocModElenco(
                new BigDecimal(elencoHelper.contaUdModificatePerByDocAggiuntiByUpd(elenco.getIdElencoVers())));
        // il sistema assegna ad ogni unità documentaria appartenente all'elenco stato = IN_ELENCO_CHIUSO
        List<AroUnitaDoc> udDocList = elencoHelper.retrieveUdDocsInElenco(elenco);
        for (AroUnitaDoc ud : udDocList) {
            ud.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
            log.debug("CAV - Assegnato alla ud '" + ud.getIdUnitaDoc() + "' lo stato "
                    + ElencoEnums.UdDocStatusEnum.IN_ELENCO_CHIUSO.name());
        }
        // il sistema assegna ad ogni documento appartenente all'elenco stato = IN_ELENCO_CHIUSO
        List<AroDoc> docList = elencoHelper.retrieveDocsInElenco(elenco);
        for (AroDoc doc : docList) {
            doc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_CHIUSO.name());
            log.debug("CAV - Assegnato al doc '" + doc.getIdDoc() + "' lo stato "
                    + ElencoEnums.DocStatusEnum.IN_ELENCO_CHIUSO.name());
        }
        // il sistema assegna ad ogni aggiornamento unità doc appartenente all'elenco stato = IN_ELENCO_CHIUSO
        List<AroUpdUnitaDoc> updList = elencoHelper.retrieveUpdsInElenco(elenco);
        for (AroUpdUnitaDoc upd : updList) {
            upd.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO);
            log.debug("CAV - Assegnato alla upd '" + upd.getIdUpdUnitaDoc() + "' lo stato "
                    + AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CHIUSO.name());
        }
        // il sistema registra sul log delle operazioni
        elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.CHIUSURA_ELENCO.name(), logJob);

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaChiudereFiscAtomic(String closeReason, Long idElenco, long idStruttura, long logJobId)
            throws Exception {
        log.debug("CAV - setDaChiudereAtomic...");
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        OrgStrut struttura = elencoHelper.retrieveOrgStrutByid(new BigDecimal(idStruttura));
        LogJob logJob = elencoHelper.retrieveLogJobByid(logJobId);
        elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.SET_ELENCO_DA_CHIUDERE.name(),
                logJob);
        // MEV#26734
        gestisciChiusuraElenco(closeReason, elenco, struttura, logJob);
        // end MEV#26734
        // MAC 26737
        ElvStatoElencoVer.TiStatoElenco tiStatoelenco = it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE
                .equals(elenco.getTiValidElenco()) ? ElvStatoElencoVer.TiStatoElenco.CHIUSO
                        : ElvStatoElencoVer.TiStatoElenco.DA_CHIUDERE;
        // EVO 19304
        evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElenco), "ELENCO_VERS_DA_CHIUDERE",
                "Chiusura anticipata per scadenza termini conservazione fiscale.", tiStatoelenco, null);
    }

    private void setDaChiudere(String closeReason, ElvElencoVer elenco, OrgStrut struttura, LogJob logJob)
            throws Exception {
        log.debug("CAE - setDaChiudere...");
        /*
         * Non aggiorno più l'elenco con il numero delle unità doc modificate incluse nell'elenco, lo faccio nella
         * addDocIntoElenco
         */
        // il sistema assegna all'elenco stato = DA_CHIUDERE sia nella tabella ELV_ELENCO_VERS, che nella tabella
        // ELV_ELENCO_VERS_DA_ELAB
        elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
        (elenco.getElvElencoVersDaElabs().get(0)).setTiStatoElenco(ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name());
        log.debug("CAE - Elenco id = " + elenco.getIdElencoVers() + " settato con stato "
                + ElencoEnums.ElencoStatusEnum.DA_CHIUDERE.name() + " per '" + closeReason + "'");
        // il sistema definisce sull'elenco la data di chiusura ed il motivo di chiusura pari a "Elenco scaduto"
        Date systemDate = new Date();
        /* TODO DA TOGLIERE 19304 documento JobElencoVersamento1.15 pag. 15 */
        elenco.setDtChius(systemDate);
        elenco.setDlMotivoChius(closeReason);
        /* TODO DA TOGLIERE 19304 documento JobElencoVersamento1.15 pag. 15 */
        // il sistema aggiorna l'elenco con il numero delle unità documentarie modificate incluse nell'elenco a causa di
        // documenti aggiunti
        // e/o di aggiornamenti metadati unità doc mediante la vista ELV_V_COUNT_UD_MODIF
        elenco.setNiUnitaDocModElenco(
                new BigDecimal(elencoHelper.contaUdModificatePerByDocAggiuntiByUpd(elenco.getIdElencoVers())));
        // il sistema assegna ad ogni unità documentaria appartenente all'elenco stato = IN_ELENCO_DA_CHIUDERE
        List<AroUnitaDoc> udDocList = elencoHelper.retrieveUdDocsInElenco(elenco);
        for (AroUnitaDoc ud : udDocList) {
            ud.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
            log.debug("CAV - Assegnato alla ud '" + ud.getIdUnitaDoc() + "' lo stato "
                    + ElencoEnums.UdDocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
        }
        // il sistema assegna ad ogni documento appartenente all'elenco stato = IN_ELENCO_DA_CHIUDERE
        List<AroDoc> docList = elencoHelper.retrieveDocsInElenco(elenco);
        for (AroDoc doc : docList) {
            doc.setTiStatoDocElencoVers(ElencoEnums.DocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
            log.debug("CAV - Assegnato al doc '" + doc.getIdDoc() + "' lo stato "
                    + ElencoEnums.DocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
        }
        // il sistema assegna ad ogni aggiornamento unità doc appartenente all’elenco stato = IN_ELENCO_DA_CHIUDERE
        List<AroUpdUnitaDoc> updList = elencoHelper.retrieveUpdsInElenco(elenco);
        for (AroUpdUnitaDoc upd : updList) {
            upd.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_DA_CHIUDERE);
            log.debug("CAV - Assegnato alla upd '" + upd.getIdUpdUnitaDoc() + "' lo stato "
                    + AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_DA_CHIUDERE.name());
        }
        // il sistema registra sul log delle operazioni
        elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.SET_ELENCO_DA_CHIUDERE.name(),
                logJob);
    }

    private Date calculateExpirationDate(ElvElencoVer elenco) {
        Date expirationDate = null;
        Date creationDate = elenco.getDtCreazioneElenco();
        log.debug("CEC - Data di creazione " + dateToString(creationDate));

        if (elenco.getTiScadChius() != null) {
            String tiScadChiusElenco = elenco.getTiScadChius();
            expirationDate = adjustElencoDateByTiScadChius(creationDate, tiScadChiusElenco);
        } else {
            String tiTempoScadChius = elenco.getTiTempoScadChius();
            BigDecimal niTempoScadChius = elenco.getNiTempoScadChius();
            expirationDate = adjustElencoDate(creationDate, tiTempoScadChius, niTempoScadChius,
                    ElencoEnums.ModeEnum.ADD.name());
        }
        log.debug("CVC - Data di scadenza " + dateToString(expirationDate));
        return expirationDate;
    }

    private Date adjustElencoDateByTiScadChius(Date creationDate, String tiScadChiusVolume) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(creationDate);
        Date expirationDate = null;
        log.debug("Data di creazione " + creationDate.toString());
        if (ElencoEnums.ExpirationTypeEnum.GIORNALIERA.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere del giorno di creazione
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);

            String newdate = dateformat.format(cal.getTime());
            log.debug(ElencoEnums.ExpirationTypeEnum.GIORNALIERA.name() + " " + newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.SETTIMANALE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere della settimana di creazione
            // Logica per evitare problemi con i LOCALE dei diversi ambienti
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            int days = Calendar.SUNDAY - weekday;
            if (days < 0) {
                // this will usually be the case since Calendar.SUNDAY is the smallest
                days += 7;
            }
            cal.add(Calendar.DAY_OF_YEAR, days);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            String newdate = dateformat.format(cal.getTime());
            log.debug(ElencoEnums.ExpirationTypeEnum.SETTIMANALE.name() + " " + newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.QUINDICINALE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere della settimana successiva a quella di creazione
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            // Logica per evitare problemi con i LOCALE dei diversi ambienti
            int weekday = cal.get(Calendar.DAY_OF_WEEK);
            int days = Calendar.SUNDAY - weekday;
            if (days < 0) {
                // this will usually be the case since Calendar.SUNDAY is the smallest
                days += 7;
            }
            cal.add(Calendar.DAY_OF_WEEK, days);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            String newdate = dateformat.format(cal.getTime());
            log.debug(ElencoEnums.ExpirationTypeEnum.QUINDICINALE.name() + " " + newdate);
        }
        if (ElencoEnums.ExpirationTypeEnum.MENSILE.name().equals(tiScadChiusVolume)) {
            // Chiudo allo scadere del mese di creazione
            int actualMaximum = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            cal.set(Calendar.DAY_OF_MONTH, actualMaximum);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 00);
            String newdate = dateformat.format(cal.getTime());
            log.debug(ElencoEnums.ExpirationTypeEnum.MENSILE.name() + " " + newdate);
        }
        expirationDate = cal.getTime();
        log.debug("CVC - Nuova data di scadenza " + expirationDate.toString());
        return expirationDate;
    }

    private Date adjustElencoDate(Date plainDate, String tiTempo, BigDecimal niTempo, String opType) {
        int tempo = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(plainDate);
        if (tiTempo != null && niTempo != null) {
            if (ElencoEnums.ModeEnum.ADD.name().equals(opType)) {
                tempo = niTempo.intValue();
            } else if (ElencoEnums.ModeEnum.SUB.name().equals(opType)) {
                tempo = -(niTempo.intValue());
            }
            if (ElencoEnums.TimeTypeEnum.MINUTI.name().equals(tiTempo)) {
                cal.add(Calendar.MINUTE, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug("CAE - Aumento di " + tempo + " " + ElencoEnums.TimeTypeEnum.MINUTI.name() + ". Scadenza = "
                        + newdate);
            }
            if (ElencoEnums.TimeTypeEnum.ORE.name().equals(tiTempo)) {
                cal.add(Calendar.HOUR_OF_DAY, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug("CAE - Aumento di " + tempo + " " + ElencoEnums.TimeTypeEnum.ORE.name() + ". Scadenza = "
                        + newdate);
            }
            if (ElencoEnums.TimeTypeEnum.GIORNI.name().equals(tiTempo)) {
                cal.add(Calendar.DAY_OF_WEEK, tempo);
                String newdate = dateformat.format(cal.getTime());
                log.debug("CAE - Aumento di " + tempo + " " + ElencoEnums.TimeTypeEnum.GIORNI.name() + ". Scadenza = "
                        + newdate);
            }
        }
        return cal.getTime();
    }

    private String dateToString(Date date) {
        return dateformat.format(date);
    }

    // controllo se la data scadenza elenco è <= della sysdate
    private boolean checkElencoExpired(ElvElencoVer elenco) {
        Date actualDate = new Date();
        log.debug("CAV - Verifico se l'elenco '" + elenco.getNmElenco() + "' con data scadenza "
                + dateToString(elenco.getDtScadChius()) + " è scaduto all'istante corrente (" + dateToString(actualDate)
                + ")");
        if (actualDate.after(elenco.getDtScadChius())) {
            log.debug("CAV - Elenco scaduto");
            return true;
        } else {
            log.debug("CAV - Elenco non scaduto");
            return false;
        }
    }

    private void manageLast(ElvElencoVer elenco, OrgStrut struttura, LogJob logJob) {
        if (elenco.getNiUnitaDocVersElenco().intValue() > 0 || elenco.getNiDocAggElenco().intValue() > 0
                || elenco.getNiUpdUnitaDoc().intValue() > 0) {
            /*
             * Aggiorno l'elenco con il numero delle unità documentarie modificate incluse nell'elenco a causa di
             * documenti aggiunti e/o di aggiornamenti metadati unità doc mediante la vista ELV_V_COUNT_UD_MODIF
             */
            elenco.setNiUnitaDocModElenco(
                    new BigDecimal(elencoHelper.contaUdModificatePerByDocAggiuntiByUpd(elenco.getIdElencoVers())));
            /*
             * Registro sul log delle operazioni che l'elenco rimane aperto (tipo operazione = SET_ELENCO_APERTO)
             * specificando il nome e l'id dell'elenco, e riferendo l'entrata del log alla registrazione di log di
             * inizio job di creazione automatica degli elenchi
             */
            elencoHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.SET_ELENCO_APERTO.name(), logJob);
        } else {
            // se l'elenco corrente è vuoto lo elimino
            elencoHelper.removeEntity(elenco, true);
        }
    }

    public void elaboraElenchiVuoti(long idStruttura, Long logJobId) throws Exception {
        log.debug("CAV - controllo se ci sono elenchi di versamento vuoti");
        // determino gli elenchi vuoti con stato APERTO appartenenti alla struttura corrente
        List<Long> elenchiVuotiDaProcessare = elencoHelper.retrieveElenchiVuotiDaProcessare(idStruttura);
        ElencoVersamentoEjb newElencoEjbRef1 = context.getBusinessObject(ElencoVersamentoEjb.class);

        log.info("CAV - trovati " + elenchiVuotiDaProcessare.size() + " elenchi di versamento vuoti da rimuovere");
        for (Long elencoId : elenchiVuotiDaProcessare) {
            log.debug("CAV - trovato elenco " + elencoId + " vuoto da rimuovere");
            newElencoEjbRef1.deleteElenchiAtomic(elencoId);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteElenchiAtomic(Long idElenco) throws Exception {
        log.debug("CAV - deleteElenchiAtomic...");
        ElvElencoVer elenco = elencoHelper.retrieveElencoById(idElenco);
        elencoHelper.deleteElvElencoVer(new BigDecimal(elenco.getIdElencoVers()));
    }

    /*
     * Metodo che controlla che: 1) la unità doc a cui appartiene il documento 2) i documenti aggiunti dell’unità doc a
     * cui appartiene il documento versati prima del documento aggiunto siano presenti nell’elenco corrente oppure per
     * essi sia definita la FK ad un altro elenco e che tale elenco abbia stato INDICI_AIP_GENERATI o
     * ELENCO_INDICI_AIP_CREATO o ELENCO_INDICI_AIP_FIRMATO o ELENCO_INDICI_AIP_ERR_MARCA o COMPLETATO; se controllo
     * fallisce, l’elemento viene scartato e si passa al successivo
     */
    private boolean checkAddDocAgg(ElvElencoVer elenco, AroDoc doc) {
        boolean isAddDocAggOk = false;
        ElvVChkAddDocAgg chkAddDocAgg = elencoHelper.retrieveElvVChkAddDocAggByIdDocAggByIdElenco(doc.getIdDoc(),
                elenco.getIdElencoVers());
        if ("1".equals(chkAddDocAgg.getFlAddDocUdOk()) && "1".equals(chkAddDocAgg.getFlAllAddDocPrecOk())) {
            isAddDocAggOk = true;
        }
        return isAddDocAggOk;
    }

    /*
     * Metodo che controlla che: 1) la unità doc a cui appartiene l'aggiornamento 2) tutti i documenti aggiunti presenti
     * nell'aggiornamento 3) tutti gli aggiornamenti precedenti (con progressivo minore di quello corrente) siano
     * presenti nell'elenco corrente oppure per essi sia definita la FK ad un altro elenco e che tale elenco abbia stato
     * INDICI_AIP_GENERATI o ELENCO_INDICI_AIP_CREATO o ELENCO_INDICI_AIP_FIRMATO o ELENCO_INDICI_AIP_ERR_MARCA o
     * COMPLETATO; se controllo fallisce, l'elemento viene scartato e si passa al successivo
     */
    private boolean checkAddUpdUd(ElvElencoVer elenco, AroUpdUnitaDoc upd) {
        boolean isAddUpdUdOk = false;
        ElvVChkAddUpdUd chkAddUpdUd = elencoHelper.retrieveElvVChkAddUpdUdByIdUpdUdByIdElenco(upd.getIdUpdUnitaDoc(),
                elenco.getIdElencoVers());
        if ("1".equals(chkAddUpdUd.getFlAddUpdUdOk()) && "1".equals(chkAddUpdUd.getFlAllAddUpdDocOk())
                && "1".equals(chkAddUpdUd.getFlAllUpdPrecOk())) {
            isAddUpdUdOk = true;
        }
        return isAddUpdUdOk;
    }

    public void calcolaUrnElenco(ElvElencoVer elenco, String nomeStruttura, String nomeStrutturaNorm, String nomeEnte,
            String nomeEnteNorm) {
        log.debug("CAV - calcolaUrnElenco");
        // sistema (new URN)
        String sistema = configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE,
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        // salvo ORIGINALE
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnte, nomeStruttura,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnElencoVers.TiUrnElenco.ORIGINALE);
        // salvo NORMALIZZATO
        urnHelper.salvaUrnElvElencoVers(elenco,
                MessaggiWSFormat.formattaUrnElencoVersamento(sistema, nomeEnteNorm, nomeStrutturaNorm,
                        sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers())),
                ElvUrnElencoVers.TiUrnElenco.NORMALIZZATO);
    }
}
