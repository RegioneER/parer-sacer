package it.eng.parer.job.indiceAip.helper;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompIndiceAipDaElab;
import it.eng.parer.entity.AroCompVerIndiceAipUd;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
import it.eng.parer.entity.AroIndiceAipUd;
import it.eng.parer.entity.AroIndiceAipUdDaElab;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.AroVLisLinkUnitaDoc;
import it.eng.parer.viewEntity.ElvVLisaipudUrndacalcByele;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.MessaggiWSFormat;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipHelper.class);
    /**
     * Numero massimo di record da estrarre. Definito su <strong>APL_PARAM_APPLIC</strong>.
     */
    // private static final String MAX_FETCH_INDICE_AIP = "MAX_FETCH_INDICE_AIP";

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @Resource
    private SessionContext context;
    @EJB
    private ComponentiHelper compHelper;
    @EJB
    private VolumeHelper volHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;

    /**
     * Recupera la lista dei record da elaborare per creare l'indice AIP
     *
     * @return la lista da elaborare
     */
    @Deprecated
    public List<AroIndiceAipUdDaElab> getAplIndiceAipUdDaElab() {
        List<AroIndiceAipUdDaElab> udDaElabList;
        String queryStr = "SELECT u FROM AroIndiceAipUdDaElab u "
                + "ORDER BY u.dtCreazioneDaElab, u.aroUnitaDoc.idUnitaDoc, u.pgCreazioneDaElab ";
        // DA TOGLIERE, USATO QUESTA UD SOLO PER PROVA
        // + "WHERE u.idIndiceAipDaElab = :idIndiceAipDaElab ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        // query.setParameter("idIndiceAipDaElab", 21);
        udDaElabList = (List<AroIndiceAipUdDaElab>) query.getResultList();
        return udDaElabList;
    }

    /**
     * Recupera la lista degli id dei record da elaborare per creare l'indice AIP
     *
     * @return la lista da elaborare
     */
    public List<Long> getIndexAplIndiceAipUdDaElab() {
        String queryStr = "SELECT u.idIndiceAipDaElab FROM AroIndiceAipUdDaElab u " + "WHERE u.flInCoda = '0' "
                + "ORDER BY u.dtCreazioneDaElab, u.aroUnitaDoc.idUnitaDoc, u.pgCreazioneDaElab ";
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        query.setMaxResults(
                Integer.parseInt(configurationHelper.getValoreParamApplic("NUM_MAX_UNITA_DOC_IN_CODA_INDICE_AIP", null,
                        null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC)));
        return query.getResultList();
    }

    public boolean existsIndexAplIndiceAipInCodaPrgMinore(AroUnitaDoc ud, long progressivoIndiceAip) {
        String queryStr = "SELECT u FROM AroIndiceAipUdDaElab u " + "WHERE u.aroUnitaDoc = :ud "
                + "AND u.pgCreazioneDaElab < :prg " + "AND u.flInCoda = '1' ";
        TypedQuery<AroIndiceAipUdDaElab> query = entityManager.createQuery(queryStr, AroIndiceAipUdDaElab.class);
        query.setParameter("ud", ud);
        query.setParameter("prg", progressivoIndiceAip);
        query.setMaxResults(1);
        List<AroIndiceAipUdDaElab> l = query.getResultList();
        return l.size() > 0 ? true : false;
    }

    /*
     * Ottieni il numero massimo di righe da recuperare.
     *
     * @param maxResultString
     * 
     * @return numero massimo di righe da recuperare
     */
    /*
     * private static int getMaxFetchIndiceAip(String maxResultString) { final int defaultMaxIndice = 100000; if
     * (maxResultString == null || maxResultString.isEmpty()) {
     * log.warn("Creazione Indice AIP - Parametro di configurazione " + MAX_FETCH_INDICE_AIP + " uso il default.");
     * return defaultMaxIndice; } int maxResult = 0; try { maxResult = Integer.parseInt(maxResultString); } catch
     * (NumberFormatException e) {
     * log.warn("Creazione Indice AIP - Eccezione durante la conversione del parametro di configurazione " +
     * MAX_FETCH_INDICE_AIP + " uso il default."); } return maxResult == 0 ? defaultMaxIndice : maxResult; }
     */

    /**
     * Restituisce il valore del progressivo versione indice AIP di tipo UNISINCRO
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return il progressivo versione oppure 0 se questo ancora non esiste
     */
    public int getProgressivoVersione(Long idUnitaDoc) {
        List<AroVerIndiceAipUd> aroVerIndiceAipList;
        String queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        aroVerIndiceAipList = (List<AroVerIndiceAipUd>) query.getResultList();
        if (aroVerIndiceAipList != null && !aroVerIndiceAipList.isEmpty()) {
            return aroVerIndiceAipList.get(0).getPgVerIndiceAip().intValue();
        } else {
            return 0;
        }
    }

    /**
     * Ricava la nuova versione dell'indice AIP che mi appresto a creare
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param tiCreazione
     *            tipo creazione
     * 
     * @return la nuova versione
     * 
     * @throws ParerInternalError
     *             errore generico
     */
    public String getVersioneAIP(Long idUnitaDoc, String tiCreazione) throws ParerInternalError {
        String versione = null;
        try {
            String queryStr;
            Query query;
            if (tiCreazione.equals("ANTICIPATO")) {
                // Ricavo l'ultima versione dell'AIP di tipo "ANTICIPATO" (cdVerIndiceAip che comincia per 0.)"
                queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                        + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "AND u.cdVerIndiceAip LIKE '0.%' "
                        + "ORDER BY u.pgVerIndiceAip DESC ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idUnitaDoc", idUnitaDoc);
                List<AroVerIndiceAipUd> aroVerIndiceAipUdList = query.getResultList();
                if (!aroVerIndiceAipUdList.isEmpty()) {
                    // Scompatto il campo cdVerIndiceAip
                    String[] numbers = aroVerIndiceAipUdList.get(0).getCdVerIndiceAip().split("[.]");
                    int minorNumber = Integer.parseInt(numbers[1]);
                    versione = "0.".concat(Integer.toString(++minorNumber));
                } else {
                    // Se non ho risultati significa che sto inserendo la prima versione
                    versione = "0.1";
                }
            } else {
                // Ricavo l'ultima versione dell'AIP di tipo "ARCHIVIO" (cdVerIndiceAip che comincia per 1.)"
                queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                        + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.cdVerIndiceAip LIKE '1.%' " + "ORDER BY u.pgVerIndiceAip DESC ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("idUnitaDoc", idUnitaDoc);
                List<AroVerIndiceAipUd> aroVerIndiceAipUdList = query.getResultList();
                if (!aroVerIndiceAipUdList.isEmpty()) {
                    // Scompatto il campo cdVerIndiceAip
                    String[] numbers = aroVerIndiceAipUdList.get(0).getCdVerIndiceAip().split("[.]");
                    int minorNumber = Integer.parseInt(numbers[1]);
                    versione = "1.".concat(Integer.toString(++minorNumber));
                } else {
                    // Se non ho risultati significa che sto inserendo la prima versione
                    versione = "1.0";
                }
            }
        } catch (NumberFormatException e) {
            log.error("Eccezione durante il recupero della versione AIP " + e);
            throw new ParerInternalError(e);
        }
        return versione;
    }

    // EVO#20792
    /**
     * Ricava la nuova versione dell'indice AIP v2.0 che mi appresto a creare
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param tiCreazione
     *            tipo creazione
     * 
     * @return la nuova versione
     * 
     * @throws ParerInternalError
     *             errore generico
     */
    public String getVersioneIndiceAIPV2(Long idUnitaDoc, String tiCreazione) throws ParerInternalError {
        String versione = null;
        try {
            String queryStr;
            Query query;
            if (tiCreazione.equals("ANTICIPATO")) {
                // Ricavo l'ultima versione dell'AIP di tipo "ANTICIPATO" (cdVerIndiceAip che comincia per 1.)"
                queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                        + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "AND u.cdVerIndiceAip LIKE '1.%' "
                        + "ORDER BY u.pgVerIndiceAip DESC ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idUnitaDoc", idUnitaDoc);
                List<AroVerIndiceAipUd> aroVerIndiceAipUdList = query.getResultList();
                if (!aroVerIndiceAipUdList.isEmpty()) {
                    // Scompatto il campo cdVerIndiceAip
                    String[] numbers = aroVerIndiceAipUdList.get(0).getCdVerIndiceAip().split("[.]");
                    int minorNumber = Integer.parseInt(numbers[1]);
                    versione = "1.".concat(Integer.toString(++minorNumber));
                } else {
                    // Se non ho risultati significa che sto inserendo la prima versione
                    versione = "1.0";
                }
            }
        } catch (NumberFormatException e) {
            log.error("Eccezione durante il recupero della versione AIP v2.0" + e);
            throw new ParerInternalError(e);
        }
        return versione;
    }
    // end EVO#20792

    /**
     * Ricava la nuova versione dell'indice AIP che mi appresto a creare
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return la nuova versione
     * 
     * @throws ParerInternalError
     *             errore generico
     */
    public String getVersioneXSDIndiceAIP(Long idUnitaDoc) throws ParerInternalError {
        String versione = null;
        String queryStr;
        Query query;
        List<AroVerIndiceAipUd> aroVerIndiceAipUdList = null;
        try {

            queryStr = "SELECT u FROM AroVerIndiceAipUd u "
                    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' "
                    + "AND u.pgVerIndiceAip =(select MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d "
                    + "WHERE d.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDocD )";

            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setParameter("idUnitaDocD", idUnitaDoc);
            aroVerIndiceAipUdList = query.getResultList();
            if (aroVerIndiceAipUdList != null && !aroVerIndiceAipUdList.isEmpty()) {
                // Scompatto il campo cdVerIndiceAip
                versione = aroVerIndiceAipUdList.get(0).getCdVerXsdIndiceAip();

            } else {
                // Se non ho risultati significa che sto inserendo la prima versione
                versione = "";
            }
        } catch (NumberFormatException e) {
            log.error("Eccezione durante il recupero della versione XSD indice AIP " + e);
            throw new ParerInternalError(e);
        }
        return versione;
    }

    /**
     * Ricava l'indice AIP di tipo UNISYNCRO di una determinata unita documentaria
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return il record dell'indice AIP, null se non esiste
     */
    public AroIndiceAipUd getAroIndiceAipUd(Long idUnitaDoc) {
        List<AroIndiceAipUd> aroIndiceAipUdList;
        String queryStr = "SELECT u FROM AroIndiceAipUd u " + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND u.tiFormatoIndiceAip = 'UNISYNCRO' ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        aroIndiceAipUdList = (List<AroIndiceAipUd>) query.getResultList();
        if (aroIndiceAipUdList != null && !aroIndiceAipUdList.isEmpty()) {
            return aroIndiceAipUdList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Metodo transazionale per la creazione dell'indice AIP attraverso la memorizzazione dei record nelle apposite
     * entity.
     *
     * @param udDaElab
     *            ud da elaborare
     * @param annoMese
     *            anno e mese
     * @param progressivoVersione
     *            progressivo versione
     * @param codiceVersione
     *            codice versione
     * @param hash
     *            valore
     * @param xml
     *            valore
     * @param versatore
     *            valore
     * @param chiave
     *            ud (anno/registro/numero)
     * 
     * @return entity AroVerIndiceAipUd
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroVerIndiceAipUd creaAIP(AroIndiceAipUdDaElab udDaElab, String annoMese, int progressivoVersione,
            String codiceVersione, String hash, String xml, CSVersatore versatore, CSChiave chiave) {
        AroIndiceAipUd indiceAip = context.getBusinessObject(CreazioneIndiceAipHelper.class)
                .getAroIndiceAipUd(udDaElab.getAroUnitaDoc().getIdUnitaDoc());
        // Chiamata locale, è giusto che partecipi alla stessa transazione. Non occorre che passi dal proxy EJB.
        boolean persistiPadre = false;
        // Se non esiste gia una versione, creo la prima
        if (indiceAip == null) {
            persistiPadre = true;
            indiceAip = new AroIndiceAipUd();
            indiceAip.setAroUnitaDoc(udDaElab.getAroUnitaDoc());
            indiceAip.setAroVerIndiceAipUds(new ArrayList<>());
            indiceAip.setTiFormatoIndiceAip("UNISYNCRO");
        }

        /* Inserisco ARO_VER_INDICE_AIP_UD */
        AroVerIndiceAipUd verIndiceAip = new AroVerIndiceAipUd();
        verIndiceAip.setAroIndiceAipUd(indiceAip);
        verIndiceAip.setAroFileVerIndiceAipUds(new ArrayList<>());
        verIndiceAip.setAroCompVerIndiceAipUds(new ArrayList<>());
        verIndiceAip.setAroUrnVerIndiceAipUds(new ArrayList<>());
        verIndiceAip.setPgVerIndiceAip(new BigDecimal(progressivoVersione));
        verIndiceAip.setCdVerIndiceAip(codiceVersione);
        verIndiceAip.setDtCreazione(new Date());
        // EVO#16486
        verIndiceAip.setDsHashIndiceAip(hash);
        verIndiceAip.setDsAlgoHashIndiceAip(TipiHash.SHA_256.descrivi());
        verIndiceAip.setCdEncodingHashIndiceAip(TipiEncBinari.HEX_BINARY.descrivi());
        // end EVO#16486
        verIndiceAip.setDsCausale(udDaElab.getDsCausale());
        verIndiceAip.setCdVerXsdIndiceAip(Costanti.VERSIONE_XSD_INDICE_AIP);
        verIndiceAip.setElvElencoVer(udDaElab.getElvElencoVer());
        verIndiceAip.setIdEnteConserv(
                indiceAip.getAroUnitaDoc().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdEnteConserv());
        indiceAip.getAroVerIndiceAipUds().add(verIndiceAip);

        /* Inserisco ARO_FILE_VER_INDICE_AIP_UD */
        AroFileVerIndiceAipUd fileVerIndice = new AroFileVerIndiceAipUd();
        fileVerIndice.setBlFileVerIndiceAip(xml);
        fileVerIndice.setMmCreazione(new BigDecimal(annoMese));
        fileVerIndice.setIdStrut(new BigDecimal(udDaElab.getAroUnitaDoc().getOrgStrut().getIdStrut()));
        fileVerIndice.setAroVerIndiceAipUd(verIndiceAip);
        verIndiceAip.getAroFileVerIndiceAipUds().add(fileVerIndice);

        /* Inserisco ARO_COMP_VER_INDICE_AIP_UD */
        for (AroCompIndiceAipDaElab compDocDaElab : udDaElab.getAroCompIndiceAipDaElabs()) {
            AroCompVerIndiceAipUd compVerIndice = new AroCompVerIndiceAipUd();
            compVerIndice.setAroCompDoc(compDocDaElab.getAroCompDoc());
            compVerIndice.setAroVerIndiceAipUd(verIndiceAip);
            verIndiceAip.getAroCompVerIndiceAipUds().add(compVerIndice);
        }

        // EVO#16486
        /* Inserisco ARO_URN_VER_INDICE_AIP_UD */
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnUnitaDoc(MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave));
        // salvo ORIGINALE
        AroUrnVerIndiceAipUd aroUrnVerIndiceAipUd = new AroUrnVerIndiceAipUd();
        aroUrnVerIndiceAipUd.setDsUrn(MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrn, verIndiceAip.getCdVerIndiceAip(),
                Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2));
        aroUrnVerIndiceAipUd.setTiUrn(TiUrnVerIxAipUd.ORIGINALE);
        aroUrnVerIndiceAipUd.setAroVerIndiceAipUd(verIndiceAip);
        verIndiceAip.getAroUrnVerIndiceAipUds().add(aroUrnVerIndiceAipUd);

        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true, Costanti.UrnFormatter.UD_FMT_STRING));
        // salvo NORMALIZZATO
        aroUrnVerIndiceAipUd = new AroUrnVerIndiceAipUd();
        aroUrnVerIndiceAipUd.setDsUrn(MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrnNorm,
                verIndiceAip.getCdVerIndiceAip(), Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2));
        aroUrnVerIndiceAipUd.setTiUrn(TiUrnVerIxAipUd.NORMALIZZATO);
        aroUrnVerIndiceAipUd.setAroVerIndiceAipUd(verIndiceAip);
        verIndiceAip.getAroUrnVerIndiceAipUds().add(aroUrnVerIndiceAipUd);
        // end EVO#16486

        /* Persisto a seconda che l'elemento di AroIndiceAipUd sia gia presente o meno */
        if (persistiPadre) {
            entityManager.persist(indiceAip);
        } else if (verIndiceAip.getIdVerIndiceAip() == 0L) {
            entityManager.persist(verIndiceAip);
        }
        return verIndiceAip;
    }

    /**
     * Metodo transazionale per la creazione dell'indice AIP attraverso la memorizzazione dei record nelle apposite
     * entity.
     *
     * @param ud
     *            entity AroUnitaDoc
     * @param annoMese
     *            anno e mese
     * @param progressivoVersione
     *            valore progressivo
     * @param codiceVersione
     *            codice versione
     * @param hash
     *            valore
     * @param xml
     *            valore
     * @param versatore
     *            valore
     * @param chiave
     *            chiave ud (anno/registro/numero)
     * 
     * @return entity AroVerIndiceAipUd
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroVerIndiceAipUd creaAIP(AroUnitaDoc ud, String annoMese, int progressivoVersione, String codiceVersione,
            String hash, String xml, CSVersatore versatore, CSChiave chiave) {
        AroIndiceAipUd indiceAip = context.getBusinessObject(CreazioneIndiceAipHelper.class)
                .getAroIndiceAipUd(ud.getIdUnitaDoc());
        // Chiamata locale, è giusto che partecipi alla stessa transazione. Non occorre che passi dal proxy EJB.
        boolean persistiPadre = false;
        // Se non esiste gia una versione, creo la prima
        if (indiceAip == null) {
            persistiPadre = true;
            indiceAip = new AroIndiceAipUd();
            indiceAip.setAroUnitaDoc(ud);
            indiceAip.setAroVerIndiceAipUds(new ArrayList<>());
            indiceAip.setTiFormatoIndiceAip("UNISYNCRO");
        }

        VolVolumeConserv vol = volHelper.getVolInfo(ud.getIdUnitaDoc());
        DateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);

        /* Inserisco ARO_VER_INDICE_AIP_UD */
        AroVerIndiceAipUd verIndiceAip = new AroVerIndiceAipUd();
        verIndiceAip.setAroIndiceAipUd(indiceAip);
        verIndiceAip.setAroFileVerIndiceAipUds(new ArrayList<>());
        verIndiceAip.setAroCompVerIndiceAipUds(new ArrayList<>());
        verIndiceAip.setAroUrnVerIndiceAipUds(new ArrayList<>());
        verIndiceAip.setPgVerIndiceAip(new BigDecimal(progressivoVersione));
        verIndiceAip.setCdVerIndiceAip(codiceVersione);
        verIndiceAip.setDtCreazione(new Date());
        // EVO#16486
        verIndiceAip.setDsHashIndiceAip(hash);
        verIndiceAip.setDsAlgoHashIndiceAip(TipiHash.SHA_256.descrivi());
        verIndiceAip.setCdEncodingHashIndiceAip(TipiEncBinari.HEX_BINARY.descrivi());
        // end EVO#16486
        verIndiceAip.setDsCausale("Aggiornamento alle nuove modalit\u00E0 di conservazione "
                + "di unit\u00E0 documentaria gi\u00E0 presente nel Volume di conservazione " + "n. "
                + String.valueOf(vol.getIdVolumeConserv()) + " del " + df.format(vol.getTmMarcaIndice()));
        verIndiceAip.setCdVerXsdIndiceAip(Costanti.VERSIONE_XSD_INDICE_AIP);
        verIndiceAip.setIdEnteConserv(
                indiceAip.getAroUnitaDoc().getOrgStrut().getOrgEnte().getOrgAmbiente().getIdEnteConserv());
        indiceAip.getAroVerIndiceAipUds().add(verIndiceAip);

        /* Inserisco ARO_FILE_VER_INDICE_AIP_UD */
        AroFileVerIndiceAipUd fileVerIndice = new AroFileVerIndiceAipUd();
        fileVerIndice.setBlFileVerIndiceAip(xml);
        fileVerIndice.setMmCreazione(new BigDecimal(annoMese));
        fileVerIndice.setIdStrut(new BigDecimal(ud.getOrgStrut().getIdStrut()));
        fileVerIndice.setAroVerIndiceAipUd(verIndiceAip);
        verIndiceAip.getAroFileVerIndiceAipUds().add(fileVerIndice);

        /* Inserisco ARO_COMP_VER_INDICE_AIP_UD */
        List<AroCompDoc> aroCompDocs = compHelper.getAroCompDocsByIdUnitaDoc(ud.getIdUnitaDoc());
        for (AroCompDoc compDoc : aroCompDocs) {
            AroCompVerIndiceAipUd compVerIndice = new AroCompVerIndiceAipUd();
            compVerIndice.setAroCompDoc(compDoc);
            compVerIndice.setAroVerIndiceAipUd(verIndiceAip);
            verIndiceAip.getAroCompVerIndiceAipUds().add(compVerIndice);
        }

        // EVO#16486
        /* Inserisco ARO_URN_VER_INDICE_AIP_UD */
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnUnitaDoc(MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave));
        // salvo ORIGINALE
        AroUrnVerIndiceAipUd aroUrnVerIndiceAipUd = new AroUrnVerIndiceAipUd();
        aroUrnVerIndiceAipUd.setDsUrn(MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrn, verIndiceAip.getCdVerIndiceAip(),
                Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2));
        aroUrnVerIndiceAipUd.setTiUrn(TiUrnVerIxAipUd.ORIGINALE);
        aroUrnVerIndiceAipUd.setAroVerIndiceAipUd(verIndiceAip);
        verIndiceAip.getAroUrnVerIndiceAipUds().add(aroUrnVerIndiceAipUd);

        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true, Costanti.UrnFormatter.UD_FMT_STRING));
        // salvo NORMALIZZATO
        aroUrnVerIndiceAipUd = new AroUrnVerIndiceAipUd();
        aroUrnVerIndiceAipUd.setDsUrn(MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrnNorm,
                verIndiceAip.getCdVerIndiceAip(), Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2));
        aroUrnVerIndiceAipUd.setTiUrn(TiUrnVerIxAipUd.NORMALIZZATO);
        aroUrnVerIndiceAipUd.setAroVerIndiceAipUd(verIndiceAip);
        verIndiceAip.getAroUrnVerIndiceAipUds().add(aroUrnVerIndiceAipUd);
        // end EVO#16486

        /* Persisto a seconda che l'elemento di AroIndiceAipUd sia gia presente o meno */
        if (persistiPadre) {
            entityManager.persist(indiceAip);
        } else if (verIndiceAip.getIdVerIndiceAip() == 0L) {
            entityManager.persist(verIndiceAip);
        }
        return verIndiceAip;
    }

    /**
     * Controllo l'esistenza della partizione per la struttura in questione
     *
     * @param idStrut
     *            id struttura
     * 
     * @return l'id della partizione se esiste
     */
    public Long checkPartizioneStruttura(Long idStrut) {
        Long idPartition = null;
        List<Long> idPartitionList;
        String queryStr = "SELECT u.orgPartition.idPartition FROM OrgPartitionStrut u "
                + "WHERE u.orgStrut.idStrut = :idStrut " + "AND u.tiPartition = 'AIP_UD' ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        idPartitionList = (List<Long>) query.getResultList();
        if (idPartitionList != null && !idPartitionList.isEmpty()) {
            idPartition = idPartitionList.get(0);
        }
        return idPartition;
    }

    /**
     * Controllo l'esistenza della sotto-partizione della partizione in questione relativa ad un determinato anno e mese
     *
     * @param idPartition
     *            id partizione
     * @param annoMese
     *            anno e mese
     * 
     * @return l'id della sotto-partizione se esiste
     */
    public Long checkSottopartizioneAnnoMese(Long idPartition, String annoMese) {
        Long idSottoPartition = null;
        List<Long> idSottoPartitionList;
        String queryStr = "SELECT u.orgSubPartition.idSubPartition FROM OrgValSubPartition u "
                + "WHERE u.orgPartition.idPartition = :idPartition " + "AND u.cdValSubPartition = :annoMese ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idPartition", idPartition);
        query.setParameter("annoMese", annoMese);
        idSottoPartitionList = (List<Long>) query.getResultList();
        if (idSottoPartitionList != null && !idSottoPartitionList.isEmpty()) {
            idSottoPartition = idSottoPartitionList.get(0);
        }
        return idSottoPartition;
    }

    public void eliminaIndiceAipDaElab(AroIndiceAipUdDaElab udDaElab) {
        entityManager.remove(udDaElab);
        entityManager.flush();
    }

    public AroIndiceAipUdDaElab findAroIndiceAipUdDaElab(long idAroIndiceAipUdDaElab) {
        return entityManager.find(AroIndiceAipUdDaElab.class, idAroIndiceAipUdDaElab);
    }

    @Deprecated
    public boolean checkComponentiPresenti(long idUnitaDoc, long idVerIndiceAip) {
        String queryStr = "SELECT compDoc FROM AroCompDoc compDoc "
                + "WHERE compDoc.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND compDoc.aroStrutDoc.aroDoc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                + "AND compDoc.idCompDoc NOT IN (SELECT compVerIndiceAipUd.aroCompDoc.idCompDoc FROM AroCompVerIndiceAipUd compVerIndiceAipUd WHERE compVerIndiceAipUd.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip) ";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idVerIndiceAip", idVerIndiceAip);
        return !query.getResultList().isEmpty();
    }

    /**
     * Ottienti i componenti presenti tramite una count (non più effettuando una fetch).
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idVerIndiceAip
     *            id versamento indice aip
     * 
     * @return true se esiste almeno un componente che soddisfa i requisti. false altrimenti.
     */
    public boolean checkComponentiPresentiCount(long idUnitaDoc, long idVerIndiceAip) {
        String queryStr = "SELECT COUNT(compDoc) FROM AroCompDoc compDoc "
                + "WHERE compDoc.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                + "AND compDoc.aroStrutDoc.aroDoc.tiCreazione = 'AGGIUNTA_DOCUMENTO' "
                + "AND compDoc.idCompDoc NOT IN (SELECT compVerIndiceAipUd.aroCompDoc.idCompDoc "
                + "FROM AroCompVerIndiceAipUd compVerIndiceAipUd WHERE compVerIndiceAipUd.aroVerIndiceAipUd.idVerIndiceAip = :idVerIndiceAip) ";
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("idVerIndiceAip", idVerIndiceAip);
        return query.getSingleResult().intValue() > 0;
    }

    /**
     * Esegue una bulk update per aggiornare lo stato dei documenti aggiunti nell'elenco il cui elenco coincide con
     * quello a cui si riferisce l'indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     * 
     * @return int pk documento aggiunto su elenco indice aip
     */
    public int updateDocumentiAggiuntiElencoIndiceAIP(long idUnitaDoc, long idElencoVers) {
        Query query = entityManager.createQuery(
                "UPDATE AroDoc doc SET doc.tiStatoDocElencoVers = 'IN_ELENCO_CON_INDICI_AIP_GENERATI' WHERE doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc AND doc.tiCreazione = :tiCreazione AND doc.elvElencoVer.idElencoVers = :idElencoVers");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("tiCreazione", CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name());
        query.setParameter("idElencoVers", idElencoVers);
        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato dei documenti nell'elenco il cui elenco coincide con quello a cui
     * si riferisce l'indice AIP
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoElenco
     *            tipo stato elenco
     * 
     * @return il numero di record aggiornati
     */
    public int updateDocumentiElencoIndiceAIP(long idElencoVers, String tiStatoElenco) {
        Query query = entityManager.createQuery(
                "UPDATE AroDoc doc SET doc.tiStatoDocElencoVers = :tiStatoElenco WHERE doc.elvElencoVer.idElencoVers = :idElencoVers");
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoElenco", tiStatoElenco);
        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato di gestione dei documenti appartenenti all'elenco e aventi un
     * insieme di stati passati in input
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoDocElencoVersOld
     *            se null, porta tutte i doc nel nuovo stato di gestione elenco
     * @param tiStatoDocElencoVersNew
     *            tipo stato nuovo documento versato
     * 
     * @return il numero di record aggiornati
     */
    public int updateDocumentiElencoIndiceAIP(long idElencoVers, List<String> tiStatoDocElencoVersOld,
            String tiStatoDocElencoVersNew) {
        StringBuilder queryStr = new StringBuilder(
                "UPDATE AroDoc doc SET doc.tiStatoDocElencoVers = :tiStatoDocElencoVersNew "
                        + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers ");

        if (tiStatoDocElencoVersOld != null && !tiStatoDocElencoVersOld.isEmpty()) {
            queryStr.append("AND doc.tiStatoDocElencoVers IN :tiStatoDocElencoVersOld ");
        }

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoDocElencoVersNew", tiStatoDocElencoVersNew);

        if (tiStatoDocElencoVersOld != null && !tiStatoDocElencoVersOld.isEmpty()) {
            query.setParameter("tiStatoDocElencoVersOld", tiStatoDocElencoVersOld);
        }

        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato degli aggiornamenti metadati nell'elenco il cui elenco coincide
     * con quello a cui si riferisce l'indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     * @param idElencoVers
     *            id elenco versamento
     * @param stato
     *            entity AroUpdUDTiStatoUpdElencoVers
     * 
     * @return int numero aggiornati
     */
    public int updateAggiornamentiMetadatiElencoIndiceAIP(long idUnitaDoc, long idElencoVers,
            AroUpdUDTiStatoUpdElencoVers stato) {
        TypedQuery<AroUpdUnitaDoc> query = entityManager.createQuery(
                "SELECT upd " + "FROM AroUpdUnitaDoc upd " + "WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND upd.elvElencoVer.idElencoVers = :idElencoVers",
                AroUpdUnitaDoc.class);
        query.setParameter("idUnitaDoc", idUnitaDoc).setParameter("idElencoVers", idElencoVers);
        List<AroUpdUnitaDoc> lstUpd = query.getResultList();

        for (AroUpdUnitaDoc aroUpdUnitaDoc : lstUpd) {
            // aroUpdUnitaDoc.setTiStatoUpdElencoVers(AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI);
            aroUpdUnitaDoc.setTiStatoUpdElencoVers(stato);
            entityManager.persist(aroUpdUnitaDoc);
        }

        return lstUpd.size();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato di gestione degli aggiornamenti metadati appartenenti all'elenco e
     * aventi un insieme di stati passati in input
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoUpdElencoVersOld
     *            tipo stato elenco aggiornato corrente
     * @param tiStatoUpdElencoVersNew
     *            tipo stato elenco aggiornato nuovo
     * 
     * @return il numero di record aggiornati
     */
    public int updateAggiornamentiElencoIndiceAIP(long idElencoVers,
            List<AroUpdUDTiStatoUpdElencoVers> tiStatoUpdElencoVersOld,
            AroUpdUDTiStatoUpdElencoVers tiStatoUpdElencoVersNew) {
        StringBuilder queryStr = new StringBuilder(
                "UPDATE AroUpdUnitaDoc upd SET upd.tiStatoUpdElencoVers = :tiStatoUpdElencoVersNew "
                        + "WHERE upd.elvElencoVer.idElencoVers = :idElencoVers ");

        if (tiStatoUpdElencoVersOld != null && !tiStatoUpdElencoVersOld.isEmpty()) {
            queryStr.append("AND upd.tiStatoUpdElencoVers IN :tiStatoUpdElencoVersOld ");
        }

        Query query = entityManager.createQuery(queryStr.toString());
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoUpdElencoVersNew", tiStatoUpdElencoVersNew);

        if (tiStatoUpdElencoVersOld != null && !tiStatoUpdElencoVersOld.isEmpty()) {
            query.setParameter("tiStatoUpdElencoVersOld", tiStatoUpdElencoVersOld);
        }

        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato dei documenti nell'elenco il cui stato di conservazione della
     * relativa ud è DIVERSO da quello passato in input e stato relativo ad elenco UGUALE a quello passato in input
     *
     * @param idElencoVers
     *            id elenco di versamento
     * @param tiStatoConservazione
     *            se null, porta tutte i doc nel nuovo stato di gestione elenco
     * @param tiStatoDocElencoVersCor
     *            tipo stato docoumento in elenco
     * @param tiStatoElenco
     *            tipo stato elenco
     * 
     * @return il numero di record aggiornati
     */
    public int updateDocumentiElencoIndiceAIPStatoConservDiverso(long idElencoVers, String tiStatoConservazione,
            String tiStatoDocElencoVersCor, String tiStatoElenco) {
        StringBuilder queryStr = new StringBuilder("UPDATE AroDoc doc SET doc.tiStatoDocElencoVers = :tiStatoElenco "
                + "WHERE doc.elvElencoVer.idElencoVers = :idElencoVers ");

        if (tiStatoConservazione != null) {
            queryStr.append("AND doc.aroUnitaDoc.tiStatoConservazione != :tiStatoConservazione ");
        }

        if (tiStatoDocElencoVersCor != null) {
            queryStr.append("AND doc.tiStatoDocElencoVers = :tiStatoDocElencoVersCor ");
        }

        Query query = entityManager.createQuery(queryStr.toString());

        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoElenco", tiStatoElenco);

        if (tiStatoConservazione != null) {
            query.setParameter("tiStatoConservazione", tiStatoConservazione);
        }

        if (tiStatoDocElencoVersCor != null) {
            query.setParameter("tiStatoDocElencoVersCor", tiStatoDocElencoVersCor);
        }

        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato degli aggiornamenti nell'elenco il cui stato di conservazione
     * della relativa ud è DIVERSO da quello passato in input e stato relativo ad elenco UGUALE a quello passato in
     * input
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoConservazione
     *            se null, porta tutte i doc nel nuovo stato di gestione elenco
     * @param tiStatoUpdElencoVersCor
     *            tipo stato elenco aggiornato su versamento corrente
     * @param tiStatoElenco
     *            tipo stato elenco
     * 
     * @return il numero di record aggiornati
     */
    public int updateAggiornamentiElencoIndiceAIPStatoConservDiverso(long idElencoVers, String tiStatoConservazione,
            AroUpdUDTiStatoUpdElencoVers tiStatoUpdElencoVersCor, AroUpdUDTiStatoUpdElencoVers tiStatoElenco) {
        StringBuilder queryStr = new StringBuilder(
                "UPDATE AroUpdUnitaDoc upd SET upd.tiStatoUpdElencoVers = :tiStatoElenco "
                        + "WHERE upd.elvElencoVer.idElencoVers = :idElencoVers ");

        if (tiStatoConservazione != null) {
            queryStr.append("AND upd.aroUnitaDoc.tiStatoConservazione != :tiStatoConservazione ");
        }

        if (tiStatoUpdElencoVersCor != null) {
            queryStr.append("AND upd.tiStatoUpdElencoVers = :tiStatoUpdElencoVersCor ");
        }

        Query query = entityManager.createQuery(queryStr.toString());

        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoElenco", tiStatoElenco);

        if (tiStatoConservazione != null) {
            query.setParameter("tiStatoConservazione", tiStatoConservazione);
        }

        if (tiStatoUpdElencoVersCor != null) {
            query.setParameter("tiStatoUpdElencoVersCor", tiStatoUpdElencoVersCor);
        }

        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato delle unità documentarie nell'elenco il cui elenco coincide con
     * quello a cui si riferisce l'indice AIP
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoElenco
     *            tipo stato elenco
     * 
     * @return il numero di record aggiornati
     */
    public int updateUnitaDocElencoIndiceAIP(long idElencoVers, String tiStatoElenco) {
        Query query = entityManager.createQuery(
                "UPDATE AroUnitaDoc ud SET ud.tiStatoUdElencoVers = :tiStatoElenco WHERE ud.elvElencoVer.idElencoVers = :idElencoVers");
        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoElenco", tiStatoElenco);
        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato di gestione delle unità documentarie appartenenti all'elenco e
     * aventi un insieme di stati passati in input
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoUdElencoVersOld
     *            se null, porta tutte le ud nel nuovo stato di gestione elenco
     * @param tiStatoUdElencoVersNew
     *            tipo stato elenco ud nuovo versamento
     * 
     * @return il numero di record aggiornati
     */
    public int updateUnitaDocElencoIndiceAIP(long idElencoVers, List<String> tiStatoUdElencoVersOld,
            String tiStatoUdElencoVersNew) {
        StringBuilder queryStr = new StringBuilder(
                "UPDATE AroUnitaDoc ud SET ud.tiStatoUdElencoVers = :tiStatoUdElencoVersNew "
                        + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers ");

        if (tiStatoUdElencoVersOld != null && !tiStatoUdElencoVersOld.isEmpty()) {
            queryStr.append("AND ud.tiStatoUdElencoVers IN :tiStatoUdElencoVersOld ");
        }
        Query query = entityManager.createQuery(queryStr.toString());

        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoUdElencoVersNew", tiStatoUdElencoVersNew);

        if (tiStatoUdElencoVersOld != null && !tiStatoUdElencoVersOld.isEmpty()) {
            query.setParameter("tiStatoUdElencoVersOld", tiStatoUdElencoVersOld);
        }

        return query.executeUpdate();
    }

    /**
     * Esegue una bulk update per aggiornare lo stato delle unità documentarie nell'elenco il cui stato di conservazione
     * è DIVERSO con quello passato in input e stato relativo ad elenco UGUALE a quello passato in input
     *
     * @param idElencoVers
     *            id elenco versamento
     * @param tiStatoConservazione
     *            tipo stato conservazione
     * @param tiStatoUdElencoVersCor
     *            tipo stato ud in elenco versamento corrente
     * @param tiStatoElenco
     *            tipo stato elenco
     * 
     * @return il numero di record aggiornati
     */
    public int updateUnitaDocElencoIndiceAIPStatoConvervDiverso(long idElencoVers, String tiStatoConservazione,
            String tiStatoUdElencoVersCor, String tiStatoElenco) {
        StringBuilder queryStr = new StringBuilder("UPDATE AroUnitaDoc ud SET ud.tiStatoUdElencoVers = :tiStatoElenco "
                + "WHERE ud.elvElencoVer.idElencoVers = :idElencoVers ");

        if (tiStatoConservazione != null) {
            queryStr.append("AND ud.tiStatoConservazione != :tiStatoConservazione ");
        }

        if (tiStatoUdElencoVersCor != null) {
            queryStr.append("AND ud.tiStatoUdElencoVers = :tiStatoUdElencoVersCor ");
        }

        Query query = entityManager.createQuery(queryStr.toString());

        query.setParameter("idElencoVers", idElencoVers);
        query.setParameter("tiStatoElenco", tiStatoElenco);

        if (tiStatoConservazione != null) {
            query.setParameter("tiStatoConservazione", tiStatoConservazione);
        }

        if (tiStatoUdElencoVersCor != null) {
            query.setParameter("tiStatoUdElencoVersCor", tiStatoUdElencoVersCor);
        }

        return query.executeUpdate();
    }

    public List<AroVerIndiceAipUd> retrieveAroVerIndiceAipUdOrdered(long idElencoVers) {
        Query query = entityManager.createQuery("SELECT verIndiceAipUd FROM AroVerIndiceAipUd verIndiceAipUd "
                + "JOIN verIndiceAipUd.aroIndiceAipUd indiceAipUd " + "JOIN indiceAipUd.aroUnitaDoc ud "
                + "WHERE verIndiceAipUd.elvElencoVer.idElencoVers = :idElencoVers "
                + "AND ud.tiStatoConservazione != 'ANNULLATA' "
                + "ORDER BY ud.dsKeyOrd, verIndiceAipUd.pgVerIndiceAip");
        query.setParameter("idElencoVers", idElencoVers);
        return query.getResultList();
    }

    public List<SerVerSerie> getVersioniSerieCorrentiContenEffettivoByUdAndStato(long idUnitaDoc,
            String... statiSerie) {
        Query query = getEntityManager()
                .createQuery("SELECT DISTINCT v FROM AroUdAppartVerSerie u, SerStatoVerSerie stato "
                        + "JOIN u.serContenutoVerSerie c JOIN c.serVerSerie v JOIN v.serSerie s "
                        + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "AND c.tiContenutoVerSerie = 'EFFETTIVO' "
                        + "AND v.pgVerSerie = (SELECT MAX(versCorr.pgVerSerie) FROM SerVerSerie versCorr WHERE versCorr.serSerie.idSerie = s.idSerie) "
                        + "AND v.idStatoVerSerieCor = stato.idStatoVerSerie "
                        + "AND ((stato.tiStatoVerSerie IN :statiSerie) OR (stato.tiStatoVerSerie = 'DA_CONTROLLARE' AND c.tiStatoContenutoVerSerie = 'CONTROLLO_CONSIST_IN_CORSO')) "
                        + "AND s.dtAnnul = :defaultAnnul");

        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("statiSerie", Arrays.asList(statiSerie));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);
        query.setParameter("defaultAnnul", c.getTime());

        return query.getResultList();
    }

    public List<FasFascicolo> getFascicoliByUdAndStato(long idUnitaDoc, TiStatoFascElencoVers... statiFascicolo) {
        Query query = getEntityManager()
                .createQuery("SELECT DISTINCT f FROM FasUnitaDocFascicolo u JOIN u.fasFascicolo f "
                        + "WHERE u.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND f.tiStatoFascElencoVers IN :statiFascicolo " + "AND f.dtAnnull = :defaultAnnul");

        query.setParameter("idUnitaDoc", idUnitaDoc);
        query.setParameter("statiFascicolo", Arrays.asList(statiFascicolo));

        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.YEAR, 2444);
        c.set(Calendar.MONTH, Calendar.DECEMBER);
        c.set(Calendar.DATE, 31);
        query.setParameter("defaultAnnul", c.getTime());

        return query.getResultList();
    }

    /**
     * Ricavo la lista delle unità doc collegate risolte
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return lista oggetti di tipo {@link AroVLisLinkUnitaDoc}
     */
    public List<AroVLisLinkUnitaDoc> getAroVLisLinkUnitaDoc(long idUnitaDoc) {
        String queryStr = "SELECT u FROM AroVLisLinkUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc "
                // MAC#23706
                + "AND u.flRisolto = '1' "
                // end MAC#23706
                + "ORDER BY u.cdKeyUnitaDocLink ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        return query.getResultList();
    }

    /**
     * Ricavo il progressivo più alto della versione indice AIP
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return entity di tipo AroVerIndiceAipUd
     */
    public AroVerIndiceAipUd getUltimaVersioneIndiceAip(long idUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT u FROM AroVerIndiceAipUd u " + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                        + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ");
        q.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVerIndiceAipUd> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public AroVDtVersMaxByUnitaDoc getAroVDtVersMaxByUd(long idUnitaDoc) {
        String queryStr = "SELECT aro FROM AroVDtVersMaxByUnitaDoc aro WHERE aro.idUnitaDoc = :idUnitaDoc ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVDtVersMaxByUnitaDoc> lista = query.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    public List<ElvVLisaipudUrndacalcByele> getElvVLisaipudUrndacalcByele(long idElencoVers) {
        String queryStr = "SELECT elv FROM ElvVLisaipudUrndacalcByele elv WHERE elv.idElencoVers = :idElencoVers ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idElencoVers", idElencoVers);
        return query.getResultList();
    }

    // MEV#17709
    public boolean riparaCollegamentiUdNonRisolti(AroUnitaDoc ud) {
        boolean tmpReturn = true;
        /*
         * aggiusto gli eventuali collegamenti di unità documentarie non risolti e forzati durante il versamento. Cerco
         * tutti i collegamenti che puntano all'UD appena inserita (li cerco per la tupla che definisce la chiave) e in
         * ognuno di essi valorizzo il campo aroUnitaDocLink con il valore dell'UD appena creata.
         */
        long numCollAggiustati;
        String queryStr = "update AroLinkUnitaDoc al " + "set al.aroUnitaDocLink = :aroUnitaDocLinkIn "
                + "where al.aroUnitaDocLink is null " + "and al.idStrut = :idStrutIn "
                + "and al.aaKeyUnitaDocLink = :aaKeyUnitaDocLinkIn "
                + "and al.cdKeyUnitaDocLink = :cdKeyUnitaDocLinkIn "
                + "and al.cdRegistroKeyUnitaDocLink = :cdRegistroKeyUnitaDocLinkIn ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("aroUnitaDocLinkIn", ud);
        query.setParameter("idStrutIn", new BigDecimal(ud.getOrgStrut().getIdStrut()));
        query.setParameter("cdRegistroKeyUnitaDocLinkIn", ud.getCdRegistroKeyUnitaDoc());
        query.setParameter("aaKeyUnitaDocLinkIn", ud.getAaKeyUnitaDoc());
        query.setParameter("cdKeyUnitaDocLinkIn", ud.getCdKeyUnitaDoc());
        try {
            numCollAggiustati = query.executeUpdate();
            if (numCollAggiustati > 0) {
                log.debug(String.format("Sono stati connessi %s collegamenti forzati da precedenti versamenti",
                        numCollAggiustati));
            }
        } catch (RuntimeException re) {
            /// logga l'errore e blocca tutto
            log.error("Eccezione nell'aggiornamento della tabella AroLinkUnitaDoc ", re);
            tmpReturn = false;
        }

        return tmpReturn;
    }
    // end MEV#17709
}
