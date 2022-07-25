package it.eng.parer.job.validazioneFascicoli.ejb;

import it.eng.parer.annulVers.ejb.*;
import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.annulVers.helper.AnnulVersHelper;
import it.eng.parer.entity.AroItemRichAnnulVers;
import it.eng.parer.entity.AroRichAnnulVers;
import it.eng.parer.entity.AroStatoRichAnnulVers;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.FasAipFascicoloDaElab;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasUdAipFascicoloDaElab;
import it.eng.parer.entity.FasUnitaDocFascicolo;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasAipFascicoloDaElab.TiCreazione;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAip.ejb.ElaborazioneRigaIndiceAipDaElab;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.job.validazioneFascicoli.helper.ValidazioneFascicoliHelper;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.FasVLisUdByFasc;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.utils.RecuperoZipGen;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.TokenFileNameType;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoLite.security.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ValidazioneFascicoliEjb {

    private static final Logger logger = LoggerFactory.getLogger(ValidazioneFascicoliEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private AnnulVersHelper helper;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private ConfigurationHelper confHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ValidazioneFascicoliHelper vfHelper;
    @EJB
    private AnnulVersEjb avEjb;
    @EJB
    private ElaborazioneRigaIndiceAipDaElab elabIndiceAip;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;

    // <editor-fold defaultstate="collapsed" desc="Validazione fascicoli">
    public void validazioneFascicoli() throws ParerInternalError, ParerUserError, IOException, NoSuchAlgorithmException,
            NamingException, JAXBException, ParseException {
        logger.info(ValidazioneFascicoliEjb.class.getSimpleName() + " --- Validazione fascicoli --- Inizio job");
        /* Determino le strutture versanti in ordine alfabetico */
        List<OrgStrut> strutList = struttureHelper.retrieveOrgStrutList();
        logger.info(ValidazioneFascicoliEjb.class.getSimpleName() + " --- Validazione fascicoli - Sono state ricavate: "
                + strutList.size() + " strutture versanti da elaborare");

        for (OrgStrut strut : strutList) {
            // Determina gli elenchi con stato FIRMATO appartenenti alla struttura corrente, in ordine di data di firma
            // crescente
            List<ElvElencoVersFascDaElab> elencoVersFascDaElabList = vfHelper
                    .getElvElencoVersFascDaElab(strut.getIdStrut(), TiStatoElencoFascDaElab.FIRMATO);
            logger.debug(ValidazioneFascicoliEjb.class.getSimpleName()
                    + " --- Validazione fascicoli - Per la struttura " + strut.getNmStrut() + " sono stati ricavati "
                    + elencoVersFascDaElabList.size() + " elenchi con stato FIRMATO da elaborare");

            /* Per ogni elenco FIRMATO */
            for (ElvElencoVersFascDaElab elencoVersFascDaElab : elencoVersFascDaElabList) {
                // Apro nuova transazione
                context.getBusinessObject(ValidazioneFascicoliEjb.class).elaboraElencoFirmato(strut.getIdStrut(),
                        elencoVersFascDaElab.getIdElencoVersDaElab());
            }
        }

        /* Scrivo in LogJob la fine corretta dell'esecuzione del job di validazione fascicoli */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        logger.info(ValidazioneFascicoliEjb.class.getSimpleName()
                + " --- Validazione fascicoli - Esecuzione job terminata con successo!");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraElencoFirmato(long idStrut, long idElencoVersDaElab) throws ParerUserError, ParerInternalError,
            IOException, NoSuchAlgorithmException, NamingException, JAXBException, ParseException {

        ElvElencoVersFascDaElab elencoVersFascDaElab = helper.findById(ElvElencoVersFascDaElab.class,
                idElencoVersDaElab);

        // Determina i fascicoli contenuti nell'elenco NON ANNULLATI in ordine di anno e numero fascicolo
        List<FasFascicolo> fascicoli = vfHelper
                .getFascicoliInElencoNonAnnullati(elencoVersFascDaElab.getElvElencoVersFasc().getIdElencoVersFasc());

        logger.debug(ValidazioneFascicoliEjb.class.getSimpleName() + " --- Validazione fascicoli - Per l'elenco "
                + elencoVersFascDaElab.getElvElencoVersFasc().getIdElencoVersFasc() + " sono stati ricavati "
                + fascicoli.size() + " fascicoli non annullati da elaborare");

        OrgStrut strut = vfHelper.findById(OrgStrut.class, elencoVersFascDaElab.getIdStrut());
        BigDecimal idAmbiente = BigDecimal.valueOf(strut.getOrgEnte().getOrgAmbiente().getIdAmbiente());

        Long idUserIam = userHelper
                .findIamUser(confHelper.getValoreParamApplic("USERID_CREAZIONE_IX_AIP_SERIE", idAmbiente,
                        elencoVersFascDaElab.getIdStrut(), null, null, CostantiDB.TipoAplVGetValAppart.STRUT))
                .getIdUserIam();

        // Per ogni fascicolo
        for (FasFascicolo fascicolo : fascicoli) {
            // Apro nuova transazione
            context.getBusinessObject(ValidazioneFascicoliEjb.class).verificaFascicolo(idUserIam, idStrut,
                    fascicolo.getIdFascicolo(), elencoVersFascDaElab.getElvElencoVersFasc().getIdElencoVersFasc());
        }

        // Assumo lock esclusivo sull'elenco
        ElvElencoVersFasc elencoVersFasc = helper.findByIdWithLock(ElvElencoVersFasc.class,
                elencoVersFascDaElab.getElvElencoVersFasc().getIdElencoVersFasc());

        // Determino se nell'elenco tutti i fascicoli sono ANNULLATI
        if (vfHelper.allFascicoliAnnullati(elencoVersFasc.getIdElencoVersFasc())) {
            // Registro un nuovo stato per l'elenco, assegnando stato COMPLETATO
            long idStatoElencoVersFasc = insertElvStatoElencoVersFasc(elencoVersFasc.getIdElencoVersFasc(), idUserIam,
                    TiStatoElencoFasc.COMPLETATO);
            // Aggiorno l'elenco con lo stato corrente appena creato e aggiorno la note
            elencoVersFasc.setIdStatoElencoVersFascCor(BigDecimal.valueOf(idStatoElencoVersFasc));
            String noteTemp = elencoVersFascDaElab.getElvElencoVersFasc().getNtElencoChiuso() != null
                    ? elencoVersFascDaElab.getElvElencoVersFasc().getNtElencoChiuso() + ". " : "";
            String ntElencoChiuso = noteTemp + "L'elenco contiene solo versamenti annullati.";
            elencoVersFasc.setNtElencoChiuso(ntElencoChiuso);
            // Elimina l’elenco dalla coda degli elenchi da elaborare
            vfHelper.removeEntity(elencoVersFascDaElab, true);
        } else {
            if (vfHelper.allAipFascInCoda(elencoVersFasc.getIdElencoVersFasc())) {
                // Registro un nuovo stato per l'elenco, assegnando stato IN_CODA_CREAZIONE_AIP
                long idStatoElencoVersFasc = insertElvStatoElencoVersFasc(
                        elencoVersFascDaElab.getElvElencoVersFasc().getIdElencoVersFasc(), idUserIam,
                        TiStatoElencoFasc.IN_CODA_CREAZIONE_AIP);
                // Aggiorno l'elenco con lo stato corrente appena creato
                elencoVersFascDaElab.getElvElencoVersFasc()
                        .setIdStatoElencoVersFascCor(BigDecimal.valueOf(idStatoElencoVersFasc));
                // Aggiorno l'elenco da elaborare assegnando stato IN_CODA_CREAZIONE_AIP
                elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.IN_CODA_CREAZIONE_AIP);
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaFascicolo(long idUserIam, long idStrut, long idFascicolo, long idElencoVersFasc)
            throws ParerUserError, ParerInternalError, IOException, NoSuchAlgorithmException, NamingException,
            JAXBException, ParseException {
        // Assumo lock esclusivo sul fascicolo e sull'elenco
        ElvElencoVersFasc elencoVersFasc = helper.findByIdWithLock(ElvElencoVersFasc.class, idElencoVersFasc);
        FasFascicolo fascicolo = helper.findByIdWithLock(FasFascicolo.class, idFascicolo);

        // Verifico se il fascicolo è annullato. L'ho già fatto prima, ma siccome non avevo effettuato LOCK, eseguo un
        // ulteriore controllo
        if (fascicolo.getTiStatoConservazione().equals(TiStatoConservazione.ANNULLATO)) {
            throw new ParerInternalError("Prima che il fascicolo " + fascicolo.getIdFascicolo()
                    + " venisse lockato, ne è stato modificato lo stato: validazione annullata ");
        }

        // Assumo lock esclusivo su tutte le unità doc del fascicolo (dovrebbe verificarsi una SELECT FOR UPDATE...)
        for (FasUnitaDocFascicolo unitaDocFascicolo : fascicolo.getFasUnitaDocFascicolos()) {
            helper.findByIdWithLock(AroUnitaDoc.class, unitaDocFascicolo.getAroUnitaDoc().getIdUnitaDoc());
        }

        // Se nel fascicolo è presente almeno una unità doc annullata
        if (vfHelper.existsUdFascicoloByStatoCons(idFascicolo,
                CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
            annullaFascicoloPerUdAnnullata(idStrut, idFascicolo, fascicolo.getAaFascicolo(),
                    fascicolo.getCdKeyFascicolo());
        } // Se non è presente...
        else {
            validaFascicolo(idUserIam, idFascicolo, idElencoVersFasc);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void annullaFascicoloPerUdAnnullata(long idStrutCorrente, long idFascicolo, BigDecimal aaFascicolo,
            String cdKeyFascicolo) throws ParerInternalError {
        // Determino l’insieme delle unità doc appartenenti al fascicolo con stato di conservazione = ANNULLATA
        // e preparo una stringa con la concatenazione degli estremi delle ud annullate contenute nel fascicolo
        List<AroUnitaDoc> udAnnullateFascicolo = vfHelper.getUdFascicoloByStatoCons(idFascicolo,
                CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name());
        if (!udAnnullateFascicolo.isEmpty()) {
            Date dataCorrente = new Date();
            List<Long> idUdAnn = new ArrayList<>();

            // Concateno gli estremi
            String estremiConcatenati = "";
            for (AroUnitaDoc udAnnullataFascicolo : udAnnullateFascicolo) {
                idUdAnn.add(udAnnullataFascicolo.getIdUnitaDoc());
                estremiConcatenati = estremiConcatenati + udAnnullataFascicolo.getCdRegistroKeyUnitaDoc() + " - "
                        + udAnnullataFascicolo.getAaKeyUnitaDoc() + " - " + udAnnullataFascicolo.getCdKeyUnitaDoc()
                        + ";\n ";
            }

            // Ricavo l'utente che ha definito la più recente richiesta di annullamento con cui sono state annullate le
            // ud
            // (lo ricavo tramite ARO_STATO_RICH_ANNUL_VERS con stato APERTA)
            long idUserIam = helper.getStatoRichiestaRecenteAnnulUd(idUdAnn).getIamUser().getIdUserIam();
            IamUser user = helper.findById(IamUser.class, idUserIam);

            // Registro la richiesta di annullamento versamenti da registrare
            AroRichAnnulVers rich = new AroRichAnnulVers();
            String msg = "Annullamento fascicolo " + aaFascicolo + " - " + cdKeyFascicolo
                    + " a causa di unità documentarie annullate dopo il versamento";
            String note = "Nel fascicolo sono presenti le seguenti unità documentarie annullate: " + estremiConcatenati;
            rich.setCdRichAnnulVers(msg);
            rich.setDsRichAnnulVers(msg);
            rich.setNtRichAnnulVers(note);
            rich.setTiRichAnnulVers(CostantiDB.TiRichAnnulVers.FASCICOLI.name());
            rich.setDtCreazioneRichAnnulVers(dataCorrente);
            rich.setTiCreazioneRichAnnulVers(CostantiDB.TipoCreazioneRichAnnulVers.ON_LINE.name());
            rich.setFlImmediata("1");
            rich.setFlForzaAnnul("0");
            rich.setFlRichPing("0");
            OrgStrut strut = vfHelper.findById(OrgStrut.class, idStrutCorrente);
            rich.setOrgStrut(strut);
            if (strut.getAroRichAnnulVers() == null) {
                strut.setAroRichAnnulVers(new ArrayList<>());
            }
            strut.getAroRichAnnulVers().add(rich);
            //
            if (rich.getAroFileRichAnnulVers() == null) {
                rich.setAroFileRichAnnulVers(new ArrayList<>());
            }
            if (rich.getAroItemRichAnnulVers() == null) {
                rich.setAroItemRichAnnulVers(new ArrayList<>());
            }
            if (rich.getAroStatoRichAnnulVers() == null) {
                rich.setAroStatoRichAnnulVers(new ArrayList<>());
            }

            helper.insertEntity(rich, true);

            // Registro l'item della richiesta di annullamento versamenti da registrare
            AroItemRichAnnulVers item = new AroItemRichAnnulVers();
            item.setPgItemRichAnnulVers(BigDecimal.ONE);
            item.setTiItemRichAnnulVers(CostantiDB.TiItemRichAnnulVers.FASC.name());
            item.setIdStrut(new BigDecimal(rich.getOrgStrut().getIdStrut()));
            item.setAaFascicolo(aaFascicolo);
            item.setCdKeyFascicolo(cdKeyFascicolo);
            item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
            FasFascicolo fascicolo = vfHelper.findById(FasFascicolo.class, idFascicolo);
            if (fascicolo.getAroItemRichAnnulVers() == null) {
                fascicolo.setAroItemRichAnnulVers(new ArrayList<>());
            }
            fascicolo.getAroItemRichAnnulVers().add(item);
            item.setFasFascicolo(fascicolo);
            //
            if (item.getAroErrRichAnnulVers() == null) {
                item.setAroErrRichAnnulVers(new ArrayList<>());
            }
            //
            rich.addAroItemRichAnnulVers(item);
            item.setAroRichAnnulVers(rich);

            helper.insertEntity(item, true);

            // Registro lo stato della richiesta di annullamento versamenti
            AroStatoRichAnnulVers statoRichAnnulVers = new AroStatoRichAnnulVers();
            statoRichAnnulVers.setPgStatoRichAnnulVers(BigDecimal.ONE);
            statoRichAnnulVers.setTiStatoRichAnnulVers(CostantiDB.StatoRichAnnulVers.CHIUSA.name());
            statoRichAnnulVers.setDtRegStatoRichAnnulVers(dataCorrente);
            //
            if (user.getAroStatoRichAnnulVers() == null) {
                user.setAroStatoRichAnnulVers(new ArrayList<>());
            }
            user.getAroStatoRichAnnulVers().add(statoRichAnnulVers);
            statoRichAnnulVers.setIamUser(user);
            //
            statoRichAnnulVers.setAroRichAnnulVers(rich);
            rich.addAroStatoRichAnnulVers(statoRichAnnulVers);

            helper.insertEntity(statoRichAnnulVers, true);

            // Aggiorno l’identificatore dello stato corrente della richiesta assegnando l’identificatore dello stato
            // inserito
            rich.setIdStatoRichAnnulVersCor(new BigDecimal(statoRichAnnulVers.getIdStatoRichAnnulVers()));

            // Controllo richiesta di annullamento
            avEjb.controlloItemValidazioneFascicoli(rich, idUserIam);

            // Evasione richiesta
            avEjb.evasioneRichiestaAnnullamento(rich);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void validaFascicolo(long idUserIam, long idFascicolo, long idElencoVersFasc) throws ParerInternalError,
            IOException, NoSuchAlgorithmException, NamingException, JAXBException, ParerUserError, ParseException {
        /*
         * Determino se tutte le unità doc hanno stato di conservazione = AIP_GENERATO o AIP_FIRMATO (per assicurare che
         * non siano in corso modifiche) oppure IN_ARCHIVIO o VERSAMENTO_IN_ARCHIVIO (caso di ud appartenente ad altro
         * fascicolo / serie che sta entrando in archivio o e’ in archivio) oppure IN_VOLUME_DI_CONSERVAZIONE (stato
         * delle ud in volumi firmati e, quindi, senza indice AIP per cui si deve creare l’AIP)
         */
        List<String> statiConservazione = Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name(),
                CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name(),
                CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name(),
                CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
                CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE.name());
        if (vfHelper.allUdFascicoloStatiConservazione(idFascicolo, statiConservazione)) {
            Date dataCorrente = new Date();
            /*
             * Aggiorna le unità doc appartenenti al fascicolo con stato di conservazione = AIP_GENERATO o AIP_FIRMATO o
             * IN_ARCHIVIO, assegnando stato di conservazione = VERSAMENTO_IN_ARCHIVIO
             */
            List<String> statiUdDocDaCambiare = new ArrayList<>(
                    Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name(),
                            CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name(),
                            CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name()));
            String statoUdNuovo = CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name();
            vfHelper.updateStatoConservazioneUdFascicolo(idFascicolo, statiUdDocDaCambiare, statoUdNuovo);

            /*
             * Aggiorna le unità doc appartenenti al fascicolo con stato di conservazione = IN_VOLUME_DI_CONSERVAZIONE,
             * assegnando stato di conservazione = AIP_DA_GENERARE
             */
            statiUdDocDaCambiare = new ArrayList<>(
                    Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE.name()));
            statoUdNuovo = CostantiDB.StatoConservazioneUnitaDoc.AIP_DA_GENERARE.name();
            vfHelper.updateStatoConservazioneUdFascicolo(idFascicolo, statiUdDocDaCambiare, statoUdNuovo);

            // Determina le unità doc del fascicolo con stato di conservazione = AIP_DA_GENERARE
            List<AroUnitaDoc> udAipDaGenerareList = vfHelper.getUdFascicoloByStatoCons(idFascicolo,
                    CostantiDB.StatoConservazioneUnitaDoc.AIP_DA_GENERARE.name());

            // Per ogni unità doc genero l'indice aip ud in volume
            for (AroUnitaDoc udAipDaGenerare : udAipDaGenerareList) {
                generaIndiceAipUdVolume(udAipDaGenerare.getIdUnitaDoc());
            }

            // Registro in FAS_AIP_FASCICOLO_DA_ELAB il fascicolo per cui si deve generare l'indice AIP
            FasAipFascicoloDaElab aipFascicoloDaElab = new FasAipFascicoloDaElab();
            FasFascicolo fascicolo = vfHelper.findById(FasFascicolo.class, idFascicolo);
            aipFascicoloDaElab.setFasFascicolo(fascicolo);
            BigDecimal pgCreazioneDaElab = BigDecimal.valueOf(vfHelper.getLastPgFascicoloCoda(idFascicolo))
                    .add(BigDecimal.ONE);
            aipFascicoloDaElab.setPgCreazioneDaElab(pgCreazioneDaElab);
            aipFascicoloDaElab.setDtCreazioneDaElab(dataCorrente);
            aipFascicoloDaElab.setDsCausale(
                    "Completamento dei controlli sul SIP preso in carico, attestato mediante validazione dell’Elenco di versamento "
                            + idElencoVersFasc);
            aipFascicoloDaElab.setTiCreazione(TiCreazione.ARCHIVIO.name());
            ElvElencoVersFasc elencoVersFasc = vfHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);
            if (elencoVersFasc.getFasAipFascicoloDaElabs() == null) {
                elencoVersFasc.setFasAipFascicoloDaElabs(new ArrayList<>());
            }
            elencoVersFasc.getFasAipFascicoloDaElabs().add(aipFascicoloDaElab);
            aipFascicoloDaElab.setElvElencoVersFasc(elencoVersFasc);
            vfHelper.insertEntity(aipFascicoloDaElab, true);

            // Aggiorno il fascicolo assegnando stato nell'elenco pari a IN_ELENCO_IN_CODA_CREAZIONE_AIP
            fascicolo.setTiStatoFascElencoVers(TiStatoFascElencoVers.IN_ELENCO_IN_CODA_CREAZIONE_AIP);

            // Registro un nuovo stato nell'elenco del fascicolo specificando stato = IN_ELENCO_IN_CODA_CREAZIONE_AIP
            FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
            statoFascicoloElenco.setFasFascicolo(fascicolo);
            IamUser user = vfHelper.findById(IamUser.class, idUserIam);
            statoFascicoloElenco.setIamUser(user);
            statoFascicoloElenco.setTiStatoFascElencoVers(TiStatoFascElenco.IN_ELENCO_IN_CODA_CREAZIONE_AIP);
            statoFascicoloElenco.setTsStato(dataCorrente);
            vfHelper.insertEntity(statoFascicoloElenco, true);
            List<BigDecimal> idUdList = vfHelper.getUdFascicoloByFascIdList(idFascicolo);
            // Per ogni unità doc del fascicolo
            for (BigDecimal idUd : idUdList) {
                // Apro nuova transazione
                // context.getBusinessObject(ValidazioneFascicoliEjb.class).verificaUrnUdFascicolo(idUd.longValue(),
                // idFascicolo);
                // Deve stare nella stessa transazione
                verificaUrnUdFascicolo(idUd.longValue(), idFascicolo);
            }

            // Determina le unità doc del fascicolo (avranno tutte stato di conservazione pari a VERSAMENTO_IN_ARCHIVIO)
            List<String> verificaStato = Arrays
                    .asList(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());
            // Controllo aggiuntivo: che tutti gli stati siano tutti pari a VERSAMENTO_IN_ARCHIVIO
            if (vfHelper.allUdFascicoloStatiConservazione(idFascicolo, verificaStato)) {
                // Ricavo le ud
                List<AroUnitaDoc> udList = vfHelper.getUdFascicoloByStatoCons(idFascicolo,
                        CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());
                // Per ogni unità doc del fascicolo
                for (AroUnitaDoc ud : udList) {
                    // Determino la versione più recente dell'indice AIP dell'unità doc (quella con progressivo
                    // maggiore)
                    AroVerIndiceAipUd ultimaVersione = vfHelper.getUltimaVersioneIndiceAip(ud.getIdUnitaDoc());
                    if (ultimaVersione != null) {
                        if (ultimaVersione.getDsHashAip() == null) {
                            // Calcola l'hash SHA-256 del file contenente l'AIP della unità doc
                            String hashAipFile = getHashAIPPerValidazioneFascicoli(user.getIdUserIam(),
                                    ud.getIdUnitaDoc(), ud.getCdRegistroKeyUnitaDoc(), ud.getAaKeyUnitaDoc(),
                                    ud.getCdKeyUnitaDoc(), ultimaVersione.getCdVerIndiceAip());
                            ultimaVersione.setDsHashAip(hashAipFile);
                            ultimaVersione.setCdEncodingHashAip(TipiEncBinari.HEX_BINARY.descrivi()); // hexBinary
                            ultimaVersione.setDsAlgoHashAip(TipiHash.SHA_256.descrivi()); // SHA-256
                        }

                        // Registro in FAS_UD_AIP_FASCICOLO_DA_ELAB la versione indice aip dell'unità doc usata dal
                        // fascicolo in coda per generare aip
                        FasUdAipFascicoloDaElab udAipFascicoloDaElab = new FasUdAipFascicoloDaElab();
                        udAipFascicoloDaElab.setAroVerIndiceAipUd(ultimaVersione);
                        udAipFascicoloDaElab.setFasAipFascicoloDaElab(aipFascicoloDaElab);
                        vfHelper.insertEntity(udAipFascicoloDaElab, true);
                    }
                }
            } else {
                throw new ParerInternalError(
                        "Errore durante la validazione dei fascicoli: non tutte le unità documentarie erano in stato VERSAMENTO_IN_ARCHIVIO, operazione annullata");
            }
        }
    }

    private void generaIndiceAipUdVolume(long idUnitaDoc)
            throws ParerInternalError, NamingException, NoSuchAlgorithmException, IOException, JAXBException {
        VolVolumeConserv volumeConserv = vfHelper.getVolumeUnitaDocPerDataMarcatura(idUnitaDoc);
        AroUnitaDoc unitaDoc = vfHelper.findById(AroUnitaDoc.class, idUnitaDoc);
        String causale = "Aggiornamento alle nuove modalità di conservazione di unità documentaria già presente nel Volume di conservazione "
                + "n. " + volumeConserv.getIdVolumeConserv() + " del " + volumeConserv.getTmMarcaIndice();
        // Crea la prima versione dell'indice aip dell'ud in formato UNISYNCRO
        creaPrimoIndiceAipUniSincro(unitaDoc.getIdUnitaDoc(), causale);
        // Aggiorno lo stato dell'ud
        unitaDoc.setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name());
    }

    public void creaPrimoIndiceAipUniSincro(Long idUnitaDoc, String causale)
            throws ParerInternalError, NamingException, NoSuchAlgorithmException, IOException, JAXBException {
        logger.debug(ValidazioneFascicoliEjb.class.getSimpleName()
                + " --- Validazione fascicoli - Crea la prima versione dell'indice AIP dell'unit\u00E0 doc "
                + idUnitaDoc + " in formato UNISYNCRO");
        AroUnitaDoc ud = helper.findByIdWithLock(AroUnitaDoc.class, idUnitaDoc);
        logger.debug("Richiamo metodo di creazione prima versione dell'indice AIP dell'unita doc in formato UNISINCRO");
        elabIndiceAip.gestisciIndiceAip(ud.getIdUnitaDoc());
    }

    // @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void verificaUrnUdFascicolo(long idUnitaDoc, long idFascicolo) throws ParerUserError, ParerInternalError,
            IOException, NoSuchAlgorithmException, NamingException, JAXBException, ParseException {
        AroUnitaDoc aroUnitaDoc = helper.findById(AroUnitaDoc.class, idUnitaDoc);
        String sistemaConservazione = confHelper.getValoreParamApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE,
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
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
        String dataInizioParam = confHelper.getValoreParamApplic(CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN,
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        Date dataInizio = dateFormat.parse(dataInizioParam);
        // controllo e calcolo URN normalizzato
        FasVLisUdByFasc fasVLisUdByFasc = vfHelper.getFasVLisUdByFasc(idFascicolo, idUnitaDoc);
        if (!fasVLisUdByFasc.getDtVersMax().after(dataInizio)
                && StringUtils.isBlank(fasVLisUdByFasc.getCdKeyUnitaDocNormaliz())) {
            // calcola e verifica la chiave normalizzata
            String cdKeyNormalized = MessaggiWSFormat.normalizingKey(aroUnitaDoc.getCdKeyUnitaDoc()); // base
            if (urnHelper.existsCdKeyNormalized(aroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    aroUnitaDoc.getAaKeyUnitaDoc(), aroUnitaDoc.getCdKeyUnitaDoc(), cdKeyNormalized)) {
                // urn normalizzato già presente su sistema
                throw new ParerInternalError("Il numero normalizzato per l'unità documentaria "
                        + MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave)
                        + " contenuta in un fascicolo è già presente ");
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
        if (!fasVLisUdByFasc.getDtVersMax().after(dataInizio)) {
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

        AroVerIndiceAipUd aroVerIndiceAipUd = vfHelper.getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
        if (aroVerIndiceAipUd != null && !aroVerIndiceAipUd.getDtCreazione().after(dataInizio)) {
            // eseguo registra urn aip pregressi
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

    /*
     * calcolo l'hash in streaming, lento ma mi tutela da eventuali out of memory
     */
    private String calculateHash(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        DigestInputStream dis = null;
        int ch;
        int BUFFER_SIZE = 10 * 1024 * 1024; // 10 MB

        try {
            logger.debug("Provider " + md.getProvider());
            dis = new DigestInputStream(is, md);
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((ch = dis.read(buffer)) != -1) {
                logger.trace("Letti " + ch + " bytes");
            }
        } finally {
            IOUtils.closeQuietly(dis);
        }

        byte[] pwdHash = md.digest();
        return toHexBinary(pwdHash);
    }

    private String toHexBinary(byte[] dati) {
        if (dati != null) {
            StringBuilder sb = new StringBuilder();
            for (byte b : dati) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public long insertElvStatoElencoVersFasc(long idElencoVersFasc, long idUserIam, TiStatoElencoFasc tiStato) {
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        //
        ElvElencoVersFasc elencoVersFasc = vfHelper.findById(ElvElencoVersFasc.class, idElencoVersFasc);
        if (elencoVersFasc.getElvStatoElencoVersFascicoli() == null) {
            elencoVersFasc.setElvStatoElencoVersFascicoli(new ArrayList<ElvStatoElencoVersFasc>());
        }
        elencoVersFasc.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);
        statoElencoVersFasc.setElvElencoVersFasc(elencoVersFasc);
        //
        IamUser user = vfHelper.findById(IamUser.class, idUserIam);
        if (user.getElvStatoElencoVersFascs() == null) {
            user.setElvStatoElencoVersFascs(new ArrayList<ElvStatoElencoVersFasc>());
        }
        user.getElvStatoElencoVersFascs().add(statoElencoVersFasc);
        statoElencoVersFasc.setIamUser(user);
        //
        statoElencoVersFasc.setTiStato(tiStato);
        statoElencoVersFasc.setTsStato(new Date());
        vfHelper.insertEntity(statoElencoVersFasc, true);
        return statoElencoVersFasc.getIdStatoElencoVersFasc();
    }

    /**
     * Calcola l'hash SHA-256 dello zip contenente l'aip
     *
     * @param idUserIam
     *            id user Iam
     * @param idUnitaDoc
     *            id unita doc
     * @param cdRegistroKeyUnitaDoc
     *            codice registro unita doc
     * @param aaKeyUnitaDoc
     *            anno unita doc
     * @param cdKeyUnitaDoc
     *            numero unita doc
     * @param cdVerIndiceAip
     *            versione indice aip
     * 
     * @return hash calcolato
     * 
     * @throws IOException
     *             errore generico di tipo IO
     * @throws NoSuchAlgorithmException
     *             errore generico
     * @throws ParerInternalError
     *             errore generico
     * @throws NamingException
     *             errore generico
     */
    public String getHashAIPPerValidazioneFascicoli(long idUserIam, long idUnitaDoc, String cdRegistroKeyUnitaDoc,
            BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, String cdVerIndiceAip)
            throws IOException, NoSuchAlgorithmException, ParerInternalError, NamingException {
        IamUser user = vfHelper.findById(IamUser.class, idUserIam);
        AroUnitaDoc ud = vfHelper.findById(AroUnitaDoc.class, idUnitaDoc);

        User utente = new User();
        utente.setUsername(user.getNmUserid());
        utente.setIdUtente(user.getIdUserIam());

        RecuperoExt recupero = new RecuperoExt();
        recupero.setParametriRecupero(new ParametriRecupero());
        // EVO#20972
        // In questa fase in cui si sta richiedendo il
        // recupero per la generazione del pacchetto AIP,
        // è necessario gestire coerentemente il parametro
        // del servizio di recupero in base alla versione Unisincro con cui
        // è stato prodotto l'ultimo Indice AIP (se presente), perchè il pacchetto AIP viene generato in
        // modo differente impostando il valore
        // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X).

        // Scompatto il campo cdVerIndiceAip
        String[] numbers = cdVerIndiceAip.split("[.]");
        int majorNumber = Integer.parseInt(numbers[0]);
        CostantiDB.TipiEntitaRecupero tipoEntitaRecupero = (majorNumber < 1)
                ? CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO : CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2;
        recupero.getParametriRecupero().setTipoEntitaSacer(tipoEntitaRecupero);
        // end EVO#20972
        recupero.getParametriRecupero().setIdUnitaDoc(idUnitaDoc);
        recupero.setTipoSalvataggioFile(CostantiDB.TipoSalvataggioFile.BLOB);
        recupero.setTpiAbilitato(false);

        // MAC#23726
        Recupero recXml = new Recupero();
        recXml.setChiave(new ChiaveType());
        recXml.setVersatore(new VersatoreType());
        recXml.setVersione(Costanti.VERSIONE_XML_RECUP_UD);

        recXml.getVersatore().setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        recXml.getVersatore().setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
        recXml.getVersatore().setStruttura(ud.getOrgStrut().getNmStrut());
        recXml.getVersatore().setUserID(utente.getIdUtente() + "");
        recXml.getVersatore().setUtente(utente.getUsername());

        recXml.getChiave().setAnno(aaKeyUnitaDoc.toBigInteger());
        recXml.getChiave().setNumero(cdKeyUnitaDoc);
        recXml.getChiave().setTipoRegistro(cdRegistroKeyUnitaDoc);
        // MEV#22921 Parametrizzazione servizi di recupero
        recXml.getChiave().setTipoNomeFile(TokenFileNameType.NOME_FILE_URN_VERSATO);

        recupero.setStrutturaRecupero(recXml);
        // end MAC#23726

        RecuperoZipGen zipGen = new RecuperoZipGen(new RispostaWSRecupero());
        File zippo = zipGen.getZip(System.getProperty("java.io.tmpdir"), recupero, true);
        try (FileInputStream is = (new FileInputStream(zippo))) {
            return new HashCalculator().calculateSHAX(is, TipiHash.SHA_256).toHexBinary();
        }
    }

    // </editor-fold>
}
