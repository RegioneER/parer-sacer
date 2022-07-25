package it.eng.parer.job.indiceAip.ejb;

import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_FIRSTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_LASTNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVATION_MNGR_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_FORMALNAME;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_PRESERVER_TAXCODE;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_HOLDER_RELEVANTDOCUMENT;
import static it.eng.parer.ws.utils.CostantiDB.ParametroAppl.AGENT_SUBMITTER_RELEVANTDOCUMENT;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroIndiceAipUd;
import it.eng.parer.entity.AroIndiceAipUdDaElab;
import it.eng.parer.entity.AroNotaUnitaDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.indiceAip.helper.CreazioneIndiceAipHelper;
import it.eng.parer.job.indiceAip.utils.CreazioneIndiceAipUtil;
import it.eng.parer.job.indiceAip.utils.CreazioneIndiceAipUtilV2;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.AroVLisLinkUnitaDoc;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.ParamIamHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.xml.usmainResp.IdCType;
import it.eng.parer.ws.xml.usmainRespV2.PIndex;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceAipDaElab")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceAipDaElab {

    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceAipDaElab.class);
    @EJB
    private CreazioneIndiceAipHelper ciaHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private ElenchiVersamentoEjb evEjb;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    @EJB
    private ParamIamHelper paramIamHelper;

    @Resource(mappedName = "jms/ProducerConnectionFactory")
    private ConnectionFactory connectionFactory;
    @Resource(mappedName = "jms/IndiceAipUnitaDocQueue")
    private Queue queue;

    /* Ricavo i valori degli Agent dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParam = Arrays.asList(AGENT_PRESERVER_FORMALNAME, AGENT_PRESERVER_TAXCODE,
            AGENT_PRESERVATION_MNGR_TAXCODE, AGENT_PRESERVATION_MNGR_LASTNAME, AGENT_PRESERVATION_MNGR_FIRSTNAME);

    /* Ricavo i valori degli Agent v2.0 dalla tabella APL_PARAM_APPLIC */
    private static final List<String> agentParamV2 = Arrays.asList();

    /* Ricavo i valori degli Agent v2.0 dalla tabella IAM_PARAM_APPLIC */
    private static final List<String> agentParamIamV2 = Arrays.asList(AGENT_HOLDER_RELEVANTDOCUMENT,
            AGENT_SUBMITTER_RELEVANTDOCUMENT);

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciIndiceAipDaElaborareNelJob(long idUdDaElab) throws ParerInternalError, ParseException {
        AroIndiceAipUdDaElab entity = ciaHelper.findByIdWithLock(AroIndiceAipUdDaElab.class, idUdDaElab);
        if (entity != null) {
            AroUnitaDoc ud = ciaHelper.findByIdWithLock(AroUnitaDoc.class, entity.getAroUnitaDoc().getIdUnitaDoc());
            if (entity.getPgCreazioneDaElab().longValueExact() > 1) {
                // Se non esiste...
                if (ciaHelper.existsIndexAplIndiceAipInCodaPrgMinore(ud,
                        entity.getPgCreazioneDaElab().longValueExact()) == false) {
                    // EVO#16486
                    verificaUrnUd(ud.getIdUnitaDoc());
                    // end EVO#16486
                    // MEV#17709
                    ciaHelper.riparaCollegamentiUdNonRisolti(ud);
                    // end MEV#17709
                    inviaMessaggio(entity);
                } // Altrimenti non fa nulla...
            } else {
                // EVO#16486
                verificaUrnUd(ud.getIdUnitaDoc());
                // end EVO#16486
                // MEV#17709
                ciaHelper.riparaCollegamentiUdNonRisolti(ud);
                // end MEV#17709
                inviaMessaggio(entity);
            }
        }
    }

    private void inviaMessaggio(AroIndiceAipUdDaElab entity) throws EJBException {
        entity.setFlInCoda("1");
        entity.setTsInCoda(new Date());
        // METTO IN CODA l'elenco appena estratto
        MessageProducer messageProducer = null;
        Connection connection = null;
        Session session = null;
        TextMessage textMessage = null;
        long idIndiceAipDaElab = entity.getIdIndiceAipDaElab();
        try {
            connection = connectionFactory.createConnection();
            log.debug("Creo la connessione alla coda per l'indiceAIP da elaborare {}", idIndiceAipDaElab);
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
            textMessage = session.createTextMessage();
            // app selector
            textMessage.setStringProperty(Costanti.JMSMsgProperties.MSG_K_APP, Constants.SACER);
            textMessage.setStringProperty("tipoPayload", "CodaIndiceAipUnitaDoc");
            textMessage.setText(Long.toString(idIndiceAipDaElab));
            messageProducer.send(textMessage);
            log.debug("Messaggio inviato per l'indiceAIP da elaborare {}", idIndiceAipDaElab);
        } catch (JMSException ex) {
            throw new EJBException(
                    String.format("ERRORE nell'invio del messaggio per l'indiceAIP da elaborare %s", idIndiceAipDaElab),
                    ex);
        } finally {
            try {
                if (messageProducer != null) {
                    messageProducer.close();
                }
                if (session != null) {
                    session.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception ex) {
            }
        }
    }

    // EVO#16486
    public void verificaUrnUd(long idUnitaDoc) throws ParerInternalError, ParseException {
        AroUnitaDoc aroUnitaDoc = ciaHelper.findById(AroUnitaDoc.class, idUnitaDoc);
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        CSVersatore versatore = this.getVersatoreUd(aroUnitaDoc, sistemaConservazione);
        CSChiave chiave = this.getChiaveUd(aroUnitaDoc);

        DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
        String dataInizioParam = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        Date dataInizio = dateFormat.parse(dataInizioParam);

        // MEV#26219
        // controllo : dtChiusura <= dataInizioCalcNuoviUrn
        // controllo e calcolo URN normalizzato
        // if (!aroUnitaDoc.getElvElencoVer().getDtChius().after(dataInizio)) {
        // Gestione KEY NORMALIZED / URN PREGRESSI
        this.sistemaUrnUnitaDoc(aroUnitaDoc, dataInizio, versatore, chiave);
        // }
        // end MEV#26219

        // Sistema URN INDICI AIP PREGRESSI
        AroVerIndiceAipUd aroVerIndiceAipUd = ciaHelper.getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
        if (aroVerIndiceAipUd != null && !aroVerIndiceAipUd.getDtCreazione().after(dataInizio)) {
            // eseguo registra urn aip pregressi
            urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);
        }
        // Sistema URN UNITÀ DOC COLLEGATE
        List<AroVLisLinkUnitaDoc> lisLinkUnitaDoc = ciaHelper.getAroVLisLinkUnitaDoc(aroUnitaDoc.getIdUnitaDoc());
        for (AroVLisLinkUnitaDoc linkUnitaDoc : lisLinkUnitaDoc) {
            AroUnitaDoc ud = ciaHelper.findByIdWithLock(AroUnitaDoc.class, linkUnitaDoc.getIdUnitaDocColleg());
            if (!ud.getTiStatoConservazione().equals(CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name())) {
                CSVersatore versatoreUdColleg = this.getVersatoreUd(ud, sistemaConservazione);
                CSChiave chiaveUdColleg = this.getChiaveUd(ud);
                // Gestione KEY NORMALIZED / URN PREGRESSI
                this.sistemaUrnUnitaDoc(ud, dataInizio, versatoreUdColleg, chiaveUdColleg);
                // Sistema URN INDICI AIP PREGRESSI
                AroVerIndiceAipUd aroVerIndiceAipUdColleg = ciaHelper.getUltimaVersioneIndiceAip(ud.getIdUnitaDoc());
                if (aroVerIndiceAipUdColleg != null && !aroVerIndiceAipUdColleg.getDtCreazione().after(dataInizio)) {
                    // eseguo registra urn aip pregressi
                    urnHelper.scriviUrnAipUdPreg(ud, versatoreUdColleg, chiaveUdColleg);
                }
            }
        }
    }

    public void sistemaUrnUnitaDoc(AroUnitaDoc aroUnitaDoc, Date dataInizio, CSVersatore versatore, CSChiave chiave)
            throws ParerInternalError {
        // 1. se il numero normalizzato sull’unità doc nel DB è nullo ->
        // il sistema aggiorna ARO_UNITA_DOC
        // controllo : dtVersMax <= dataInizioCalcNuoviUrn
        AroVDtVersMaxByUnitaDoc aroVDtVersMaxByUd = ciaHelper.getAroVDtVersMaxByUd(aroUnitaDoc.getIdUnitaDoc());
        if (!aroVDtVersMaxByUd.getDtVersMax().after(dataInizio)
                && StringUtils.isBlank(aroUnitaDoc.getCdKeyUnitaDocNormaliz())) {
            // calcola e verifica la chiave normalizzata
            String cdKeyNormalized = MessaggiWSFormat.normalizingKey(aroUnitaDoc.getCdKeyUnitaDoc()); // base
            if (urnHelper.existsCdKeyNormalized(aroUnitaDoc.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    aroUnitaDoc.getAaKeyUnitaDoc(), aroUnitaDoc.getCdKeyUnitaDoc(), cdKeyNormalized)) {
                // urn normalizzato già presente su sistema
                throw new ParerInternalError("Il numero normalizzato per l'unità documentaria "
                        + MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave) + " della struttura " + versatore.getEnte()
                        + "/" + versatore.getStruttura() + " è già presente");
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
        if (!aroVDtVersMaxByUd.getDtVersMax().after(dataInizio)) {
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
    // end EVO#16486

    // EVO#20972
    public void gestisciIndiceAipDaElab(long idUdDaElab) throws ParerInternalError, NamingException,
            NoSuchAlgorithmException, IOException, ValidationException, JAXBException {
        AroIndiceAipUdDaElab udDaElab = ciaHelper.findByIdWithLock(AroIndiceAipUdDaElab.class, idUdDaElab);
        // Se non trova il record esce e non fa nulla!
        if (udDaElab != null) {
            BigDecimal idAmbiente = BigDecimal
                    .valueOf(udDaElab.getAroUnitaDoc().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente());
            String sincroVersion = configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.UNISINCRO_VERSION,
                    idAmbiente, null, null, null, CostantiDB.TipoAplVGetValAppart.AMBIENTE);
            gestisciIndiceAipDaElab(udDaElab, sincroVersion);
        }
    }
    // end EVO#20972

    public void gestisciIndiceAipDaElab(AroIndiceAipUdDaElab udDaElab, String sincroVersion)
            throws ParerInternalError, NamingException, NoSuchAlgorithmException, IOException, MarshalException,
            ValidationException, JAXBException {
        // EVO#20972
        String desJobMessage = "Creazione Indice AIP v" + sincroVersion;
        // end EVO#20972

        AroUnitaDoc unitaDoc = ciaHelper.findByIdWithLock(AroUnitaDoc.class, udDaElab.getAroUnitaDoc().getIdUnitaDoc());

        // EVO#20972
        List<String> agentParamList = (!"2.0".equals(sincroVersion)) ? agentParam : agentParamV2;
        Map<String, String> mappaAgent = configurationHelper.getParamApplicMapValue(agentParamList,
                BigDecimal.valueOf(unitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                BigDecimal.valueOf(unitaDoc.getOrgStrut().getIdStrut()), null, null,
                CostantiDB.TipoAplVGetValAppart.STRUT);
        // end EVO#20972

        // MEV#25903
        if ("2.0".equals(sincroVersion)) {
            SIOrgEnteSiam orgEnteConvenz = null;
            if (unitaDoc.getOrgStrut().getIdEnteConvenz() != null) {
                orgEnteConvenz = ciaHelper.findById(SIOrgEnteSiam.class, unitaDoc.getOrgStrut().getIdEnteConvenz());
            }
            mappaAgent.putAll(paramIamHelper.getParamApplicMapValue(agentParamIamV2,
                    BigDecimal.valueOf(orgEnteConvenz.getSiOrgAmbienteEnteConvenz().getIdAmbienteEnteConvenz()),
                    BigDecimal.valueOf(orgEnteConvenz.getIdEnteSiam()), CostantiDB.TipoIamVGetValAppart.ENTECONVENZ));
        }
        // end MEV#25903

        /* Recupero l'unità documentaria da elaborare */
        log.debug("Creazione Indice AIP - Elaboro l'unità documentaria {}", udDaElab.getAroUnitaDoc().getIdUnitaDoc());

        // Ricavo il parametro da AplParamApplic relativo alla verifica delle partizioni
        boolean verificaPartizioni = Boolean
                .parseBoolean(configurationHelper.getValoreParamApplic(CostantiDB.ParametroAppl.VERIFICA_PARTIZIONI,
                        null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        String annoMese = "";
        if (verificaPartizioni) {
            log.debug(desJobMessage + " - Check partition");
            annoMese = checkPartition(unitaDoc);
        } else {
            Calendar c = Calendar.getInstance();
            String anno = "" + c.get(Calendar.YEAR);
            int meseNum = c.get(Calendar.MONTH) + 1;
            String mese = meseNum < 10 ? "0" + meseNum : "" + meseNum;
            annoMese = anno + mese;
        }

        /* Determino il progressivo di versione dell'indice AIP e lo aumento di 1 */
        log.debug(desJobMessage + " - Ottengo il progressivo versione");
        int progressivoVersione = ciaHelper.getProgressivoVersione(unitaDoc.getIdUnitaDoc());
        progressivoVersione++;

        /* Determino il codice di versione dell'AIP */
        // EVO#20972
        String tiCreazione = udDaElab.getTiCreazione();
        log.debug(desJobMessage + " - Ottengo il progressivo versione AIP");
        String codiceVersione = (!"2.0".equals(sincroVersion))
                ? ciaHelper.getVersioneAIP(unitaDoc.getIdUnitaDoc(), tiCreazione)
                : ciaHelper.getVersioneIndiceAIPV2(unitaDoc.getIdUnitaDoc(), tiCreazione);
        // end EVO#20972

        /* Determino il sistema di conservazione */
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        /* Determino il versatore */
        CSVersatore versatore = getVersatoreUd(unitaDoc, sistemaConservazione);
        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper.getValoreParamApplic("CREATING_APPLICATION_PRODUCER",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        /* Determino la chiave */
        CSChiave chiave = getChiaveUd(unitaDoc);

        /* Crea Indice AIP */
        // EVO#20972
        StringWriter tmpWriter = null;
        log.debug(desJobMessage + " - Genero l'indice AIP");
        if (!"2.0".equals(sincroVersion)) {
            CreazioneIndiceAipUtil creazione = new CreazioneIndiceAipUtil();
            IdCType idc = creazione.generaIndiceAIP(udDaElab, codiceVersione, Costanti.VERSIONE_XSD_INDICE_AIP,
                    sistemaConservazione, mappaAgent, creatingApplicationProducer);

            log.debug(desJobMessage + " - Eseguo il marshalling dell'idc");
            tmpWriter = marshallIdC(idc);
        } else {
            CreazioneIndiceAipUtilV2 creazioneV2 = new CreazioneIndiceAipUtilV2();
            PIndex pindex = creazioneV2.generaIndiceAIPV2(udDaElab, codiceVersione, Costanti.VERSIONE_XSD_INDICE_AIP_V2,
                    sistemaConservazione, mappaAgent, creatingApplicationProducer);

            log.debug(desJobMessage + " - Eseguo il marshalling del pindex");
            tmpWriter = marshallPIndex(pindex);
        }
        // end EVO#20972

        /* Calcolo l'hash */
        log.debug(desJobMessage + " - Calcolo l'hash");
        String hash = new HashCalculator().calculateHashSHAX(tmpWriter.toString(), TipiHash.SHA_256).toHexBinary();

        /* Persisto nelle varie tabelle di creazione dell'indice AIP */
        log.debug(desJobMessage + " - Creo l'indice AIP");
        AroVerIndiceAipUd lastVer = ciaHelper.creaAIP(udDaElab, annoMese, progressivoVersione, codiceVersione, hash,
                tmpWriter.toString(), versatore, chiave);

        /* Recupero l'entity della tabella ARO_INDICE_AIP_UD e setto l'idVerIndiceAipLast */
        AroIndiceAipUd aroIndice = lastVer.getAroIndiceAipUd();
        aroIndice.setIdVerIndiceAipLast(new BigDecimal(lastVer.getIdVerIndiceAip()));
        long idUnitaDoc = aroIndice.getAroUnitaDoc().getIdUnitaDoc();

        log.debug(desJobMessage + " - Ottengo, byId, AroUnitaDoc");
        AroUnitaDoc aroUnitaDoc = ciaHelper.findById(AroUnitaDoc.class, idUnitaDoc);

        /**
         * Se la UD cui l’AIP si riferisce ha stato di conservazione ANNULLATO, lo stato rimane ANNULLATO, altrimenti se
         * ha stato PRESA_IN_CARICO, AIP_IN_AGGIORNAMENTO o AIP_DA_GENERARE, controllo sui componenti...
         */
        if (aroUnitaDoc.getTiStatoConservazione().equals(CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
                || aroUnitaDoc.getTiStatoConservazione()
                        .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name())
                || aroUnitaDoc.getTiStatoConservazione()
                        .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_DA_GENERARE.name())) {
            log.debug(desJobMessage + " - Controllo i componenti presenti");
            if (ciaHelper.checkComponentiPresentiCount(idUnitaDoc, lastVer.getIdVerIndiceAip())) {
                aroUnitaDoc.setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name());
            } else {
                aroUnitaDoc.setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name());
            }
        }

        if (aroUnitaDoc.getElvElencoVer().getIdElencoVers() == udDaElab.getElvElencoVer().getIdElencoVers()) {
            log.debug(desJobMessage + " - Aggiorno lo stato ud a IN_ELENCO_CON_INDICI_AIP_GENERATI");
            aroUnitaDoc.setTiStatoUdElencoVers(ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_INDICI_AIP_GENERATI.name());
        }

        // BULK_UPDATE con idUnitaDoc di aroUnitaDoc e idElencoVers di udDaElab dei documenti aggiunti
        int updateDoc = ciaHelper.updateDocumentiAggiuntiElencoIndiceAIP(aroUnitaDoc.getIdUnitaDoc(),
                udDaElab.getElvElencoVer().getIdElencoVers());
        log.debug(
                "Creazione Indice AIP - Aggiorno lo stato doc aggiunti a IN_ELENCO_CON_INDICI_AIP_GENERATI - aggiornati {} records",
                updateDoc);

        // BULK_UPDATE con idUnitaDoc di aroUnitaDoc e idElencoVers di udDaElab degli aggiornamenti metadati
        int updateUpd = ciaHelper.updateAggiornamentiMetadatiElencoIndiceAIP(aroUnitaDoc.getIdUnitaDoc(),
                udDaElab.getElvElencoVer().getIdElencoVers(),
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI);
        log.debug(
                "Creazione Indice AIP - Aggiorno lo stato upd metadati a IN_ELENCO_CON_INDICI_AIP_GENERATI - aggiornati {} records",
                updateUpd);

        // Aggiorno le versioni serie settando l’indicatore che segnala se la serie deve essere ricalcolata a causa
        // di aggiornamento di almeno una unità documentaria
        setFlagVerSerieDaAggiornare(aroUnitaDoc.getIdUnitaDoc());

        // Aggiorno i fascicoli settando l’indicatore che segnala se il fascicolo deve essere ricalcolato a causa di
        // aggiornamento di almeno una unità documentaria
        setFlagFasFascicoliDaAggiornare(aroUnitaDoc.getIdUnitaDoc());

        // MEV#24597
        // Aggiorno le note dell'unità documentaria settando l’identificativo che segnala la prima versione
        // dell'indice aip in cui è contenuta la nota
        for (AroNotaUnitaDoc aroNotaUnitaDoc : aroUnitaDoc.getAroNotaUnitaDocs()) {
            if (aroNotaUnitaDoc.getAroVerIndiceAipUd() == null) {
                aroNotaUnitaDoc.setAroVerIndiceAipUd(lastVer);
            }
        }
        // end MEV#24597

        log.debug(desJobMessage + " - Elimino l'indice AIP dalla coda di elaborazione");
        /* Elimino il record da quelli da elaborare */
        ciaHelper.eliminaIndiceAipDaElab(udDaElab);
        log.debug(desJobMessage + " - Operazione di inserimento completata con successo");
    }

    // EVO#20972
    private StringWriter marshallPIndex(PIndex pindex) throws ValidationException, JAXBException, MarshalException {
        it.eng.parer.ws.xml.usmainRespV2.ObjectFactory objFctPIndex = new it.eng.parer.ws.xml.usmainRespV2.ObjectFactory();
        JAXBElement<PIndex> elementPIndex = objFctPIndex.createPIndex(pindex);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getVersRespUniSincroCtx_PIndex_Ud().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfVersRespUniSincroV2());
        tmpMarshaller.marshal(elementPIndex, tmpWriter);
        return tmpWriter;
    }
    // end EVO#20972

    private StringWriter marshallIdC(IdCType idc) throws ValidationException, JAXBException, MarshalException {
        it.eng.parer.ws.xml.usmainResp.ObjectFactory objFct_IdCType = new it.eng.parer.ws.xml.usmainResp.ObjectFactory();
        JAXBElement<IdCType> element_IdCType = objFct_IdCType.createIdC(idc);

        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getVersRespUniSincroCtx_IdC_Ud().createMarshaller();
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfVersRespUniSincro());
        tmpMarshaller.marshal(element_IdCType, tmpWriter);
        return tmpWriter;
    }

    private String checkPartition(AroUnitaDoc unitaDoc) throws ParerInternalError {
        /*
         * Verifica la presenza della partizione per la struttura e l'esistenza della sottopartizione per l'anno ed il
         * mese di creazione
         */
        Long idStrut = unitaDoc.getOrgStrut().getIdStrut();
        Long idPartition = ciaHelper.checkPartizioneStruttura(idStrut);
        String annoMese = "";
        if (idPartition == null) {
            String errore = "Creazione Indice AIP - Per la struttura " + idStrut
                    + " non è definita la partizione di tipo AIP_UD";
            log.error(errore);
            throw new ParerInternalError(errore);
        } else {
            Calendar c = Calendar.getInstance();
            String anno = "" + c.get(Calendar.YEAR);
            int meseNum = c.get(Calendar.MONTH) + 1;
            String mese = meseNum < 10 ? "0" + meseNum : "" + meseNum;
            annoMese = anno + mese;
            idPartition = ciaHelper.checkSottopartizioneAnnoMese(idPartition, annoMese);
            if (idPartition == null) {
                String errore = "Creazione Indice AIP - Per la struttura " + idStrut
                        + " non è definita la sotto-partizione di tipo AIP_UD " + "relativa al mese " + annoMese
                        + " di creazione indice AIP";
                log.error(errore);
                throw new ParerInternalError(errore);
            }
        }
        return annoMese;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gestisciIndiceAip(long idUnitaDoc) throws ParerInternalError, NamingException, ValidationException,
            NoSuchAlgorithmException, IOException, MarshalException, JAXBException {
        log.debug("Creazione Indice AIP - Elaboro l'unit\u00E0 documentaria {}", idUnitaDoc);
        // Map<String, String> mappaAgent = configurationHelper.getParamApplicMapValue(agentParam);
        AroUnitaDoc unitaDoc = ciaHelper.findById(AroUnitaDoc.class, idUnitaDoc);
        Map<String, String> mappaAgent = configurationHelper.getParamApplicMapValue(agentParam,
                BigDecimal.valueOf(unitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                BigDecimal.valueOf(unitaDoc.getOrgStrut().getIdStrut()), null, null,
                CostantiDB.TipoAplVGetValAppart.STRUT);
        String annoMese = checkPartition(unitaDoc);
        /* Determino il progressivo di versione dell'indice AIP e lo aumento di 1 */
        int progressivoVersione = ciaHelper.getProgressivoVersione(unitaDoc.getIdUnitaDoc());
        progressivoVersione++;

        /* Determino il codice di versione dell'AIP */
        String codiceVersione = ciaHelper.getVersioneAIP(unitaDoc.getIdUnitaDoc(),
                CostantiDB.TipoCreazioneIndiceAip.ANTICIPATO.name());
        /* Determino il sistema di conservazione */
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        /* Determino il versatore */
        CSVersatore versatore = getVersatoreUd(unitaDoc, sistemaConservazione);
        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper.getValoreParamApplic("CREATING_APPLICATION_PRODUCER",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        /* Determino la chiave */
        CSChiave chiave = getChiaveUd(unitaDoc);

        /* Crea AIP */
        CreazioneIndiceAipUtil creazione = new CreazioneIndiceAipUtil();
        IdCType idc = creazione.generaIndiceAIP(unitaDoc, codiceVersione, Costanti.VERSIONE_XSD_INDICE_AIP,
                sistemaConservazione, mappaAgent, creatingApplicationProducer);

        StringWriter tmpWriter = marshallIdC(idc);

        /* Calcolo l'hash */
        // String hash = new HashCalculator().calculateHash(tmpWriter.toString()).toHexBinary();
        String hash = new HashCalculator().calculateHashSHAX(tmpWriter.toString(), TipiHash.SHA_256).toHexBinary();

        /* Persisto nelle varie tabelle di creazione dell'indice AIP */
        AroVerIndiceAipUd lastVer = ciaHelper.creaAIP(unitaDoc, annoMese, progressivoVersione, codiceVersione, hash,
                tmpWriter.toString(), versatore, chiave);

        /* Recupero l'entity della tabella ARO_INDICE_AIP_UD e setto l'idVerIndiceAipLast */
        AroIndiceAipUd aroIndice = lastVer.getAroIndiceAipUd();
        aroIndice.setIdVerIndiceAipLast(new BigDecimal(lastVer.getIdVerIndiceAip()));
        unitaDoc.setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name());
        log.debug("Creazione Indice AIP - Operazione di inserimento completata con successo");
    }

    // EVO#20972
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void gestisciIndiceAipV2(long idUnitaDoc) throws ParerInternalError, NamingException, ValidationException,
            NoSuchAlgorithmException, IOException, MarshalException, JAXBException {
        log.debug("Creazione Indice AIP v2.0 - Elaboro l'unit\u00E0 documentaria " + String.valueOf(idUnitaDoc));
        AroUnitaDoc unitaDoc = ciaHelper.findById(AroUnitaDoc.class, idUnitaDoc);
        Map<String, String> mappaAgent = configurationHelper.getParamApplicMapValue(agentParamV2,
                BigDecimal.valueOf(unitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente()),
                BigDecimal.valueOf(unitaDoc.getOrgStrut().getIdStrut()), null, null,
                CostantiDB.TipoAplVGetValAppart.STRUT);
        String annoMese = checkPartition(unitaDoc);
        /* Determino il progressivo di versione dell'indice AIP e lo aumento di 1 */
        int progressivoVersione = ciaHelper.getProgressivoVersione(unitaDoc.getIdUnitaDoc());
        progressivoVersione++;

        /* Determino il codice di versione dell'AIP */
        String codiceVersione = ciaHelper.getVersioneIndiceAIPV2(unitaDoc.getIdUnitaDoc(),
                CostantiDB.TipoCreazioneIndiceAip.ANTICIPATO.name());
        /* Determino il sistema di conservazione */
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        /* Determino il versatore */
        CSVersatore versatore = getVersatoreUd(unitaDoc, sistemaConservazione);
        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper.getValoreParamApplic("CREATING_APPLICATION_PRODUCER",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC);
        /* Determino la chiave */
        CSChiave chiave = getChiaveUd(unitaDoc);

        /* Crea AIP */
        CreazioneIndiceAipUtilV2 creazioneV2 = new CreazioneIndiceAipUtilV2();
        PIndex pindex = creazioneV2.generaIndiceAIPV2(unitaDoc, codiceVersione, Costanti.VERSIONE_XSD_INDICE_AIP_V2,
                sistemaConservazione, mappaAgent, creatingApplicationProducer);

        StringWriter tmpWriter = marshallPIndex(pindex);

        /* Calcolo l'hash */
        String hash = new HashCalculator().calculateHashSHAX(tmpWriter.toString(), TipiHash.SHA_256).toHexBinary();

        /* Persisto nelle varie tabelle di creazione dell'indice AIP */
        AroVerIndiceAipUd lastVer = ciaHelper.creaAIP(unitaDoc, annoMese, progressivoVersione, codiceVersione, hash,
                tmpWriter.toString(), versatore, chiave);

        /* Recupero l'entity della tabella ARO_INDICE_AIP_UD e setto l'idVerIndiceAipLast */
        AroIndiceAipUd aroIndice = lastVer.getAroIndiceAipUd();
        aroIndice.setIdVerIndiceAipLast(new BigDecimal(lastVer.getIdVerIndiceAip()));
        unitaDoc.setTiStatoConservazione(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name());
        log.debug("Creazione Indice AIP v2.0 - Operazione di inserimento completata con successo");
    }
    // end EVO#20972

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setFlagVerSerieDaAggiornare(long idUnitaDoc) {
        List<SerVerSerie> verSeries = ciaHelper.getVersioniSerieCorrentiContenEffettivoByUdAndStato(idUnitaDoc,
                CostantiDB.StatoVersioneSerie.CONTROLLATA.name(),
                CostantiDB.StatoVersioneSerie.VALIDAZIONE_IN_CORSO.name(),
                CostantiDB.StatoVersioneSerie.VALIDATA.name(), CostantiDB.StatoVersioneSerie.DA_FIRMARE.name(),
                CostantiDB.StatoVersioneSerie.FIRMATA.name(), CostantiDB.StatoVersioneSerie.IN_CUSTODIA.name());

        for (SerVerSerie verSerie : verSeries) {
            verSerie.setFlUpdModifUnitaDoc("1");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setFlagFasFascicoliDaAggiornare(long idUnitaDoc) {
        List<FasFascicolo> fasFascicolos = ciaHelper.getFascicoliByUdAndStato(idUnitaDoc,
                TiStatoFascElencoVers.IN_ELENCO_IN_CODA_CREAZIONE_AIP, TiStatoFascElencoVers.IN_ELENCO_CON_AIP_CREATO,
                TiStatoFascElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
                TiStatoFascElencoVers.IN_ELENCO_COMPLETATO);

        for (FasFascicolo fasFascicolo : fasFascicolos) {
            fasFascicolo.setFlUpdModifUnitaDoc("1");
        }
    }
}
