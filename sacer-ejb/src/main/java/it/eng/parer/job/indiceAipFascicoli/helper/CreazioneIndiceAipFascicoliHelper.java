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

package it.eng.parer.job.indiceAipFascicoli.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.FasAipFascicoloDaElab;
import it.eng.parer.entity.FasContenVerAipFascicolo;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasMetaVerAipFascicolo;
import it.eng.parer.entity.FasSipVerAipFascicolo;
import it.eng.parer.entity.FasUdAipFascicoloDaElab;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.entity.FasXsdMetaVerAipFasc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.viewEntity.ElvVLisIxAipFascByEle;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({ "unchecked" })
@Stateless(mappedName = "CreazioneIndiceAipFascicoliHelper")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipFascicoliHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(CreazioneIndiceAipFascicoliHelper.class);

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

    /**
     * Recupera la lista degli id dei record da elaborare per creare l'indice AIP
     *
     * @return la lista da elaborare
     */
    public List<Long> getIndexFasAipFascicoloDaElab() {
        String queryStr = "SELECT u.idAipFascicoloDaElab FROM FasAipFascicoloDaElab u "
                + "ORDER BY u.dtCreazioneDaElab, u.fasFascicolo.idFascicolo, u.pgCreazioneDaElab ";
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        String maxResultString = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.MAX_FETCH_INDICE_AIP_FASC);
        query.setMaxResults(getMaxFetchIndiceAipFascicolo(maxResultString));

        return query.getResultList();
    }

    /**
     * Ottieni il numero massimo di righe da recuperare.
     *
     * @param maxResultString
     *
     * @return numero massimo di righe da recuperare
     */
    private static int getMaxFetchIndiceAipFascicolo(String maxResultString) {
        final int defaultMaxIndice = 100000;
        if (maxResultString == null || maxResultString.isEmpty()) {
            log.warn("Creazione Indice AIP fascicolo - Parametro di configurazione "
                    + CostantiDB.ParametroAppl.MAX_FETCH_INDICE_AIP_FASC + " uso il default.");
            return defaultMaxIndice;
        }
        int maxResult = 0;
        try {
            maxResult = Integer.parseInt(maxResultString);
        } catch (NumberFormatException e) {
            log.warn(
                    "Creazione Indice AIP fascicolo - Eccezione durante la conversione del parametro di configurazione "
                            + CostantiDB.ParametroAppl.MAX_FETCH_INDICE_AIP_FASC + " uso il default.");
        }
        return maxResult == 0 ? defaultMaxIndice : maxResult;
    }

    /**
     * Restituisce il valore del progressivo versione indice AIP di tipo UNISYNCRO
     *
     * @param idFascicolo
     *            id fascicolo
     *
     * @return il progressivo versione oppure 0 se questo ancora non esiste
     */
    public int getProgressivoVersione(Long idFascicolo) {
        List<FasVerAipFascicolo> fasVerAipFascicoloList;
        String queryStr = "SELECT u FROM FasVerAipFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo "
                + "ORDER BY u.pgVerAipFascicolo DESC ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        fasVerAipFascicoloList = query.getResultList();
        if (fasVerAipFascicoloList != null && !fasVerAipFascicoloList.isEmpty()) {
            return fasVerAipFascicoloList.get(0).getPgVerAipFascicolo().intValue();
        } else {
            return 0;
        }
    }

    /**
     * Ricava la nuova versione dell'indice AIP che mi appresto a creare
     *
     * @param idFascicolo
     *            id fascicolo
     * @param tiCreazione
     *            tipo creazione
     *
     * @return la nuova versione
     *
     * @throws ParerInternalError
     *             errore generico
     */
    public String getVersioneAIP(Long idFascicolo, String tiCreazione) throws ParerInternalError {
        String versione = null;
        try {
            String queryStr;
            Query query;
            List<FasVerAipFascicolo> fasVerAipFascicoloList;
            if (tiCreazione.equals("ANTICIPATO")) {
                // Ricavo l'ultima versione dell'AIP di tipo "ANTICIPATO" (cdVerAip che comincia per 0.)"
                queryStr = "SELECT u FROM FasVerAipFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo "
                        + "AND u.cdVerAip LIKE '0.%' " + "ORDER BY u.pgVerAipFascicolo DESC ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idFascicolo", idFascicolo);
                fasVerAipFascicoloList = query.getResultList();
                if (!fasVerAipFascicoloList.isEmpty()) {
                    // Scompatto il campo cdVerAip
                    String[] numbers = fasVerAipFascicoloList.get(0).getCdVerAip().split("[.]");
                    int minorNumber = Integer.parseInt(numbers[1]);
                    versione = "0.".concat(Integer.toString(++minorNumber));
                } else {
                    // Se non ho risultati significa che sto inserendo la prima versione
                    versione = "0.1";
                }
            } else {
                // Ricavo l'ultima versione dell'AIP di tipo "ARCHIVIO" (cdVerAip che comincia per 1.)"
                queryStr = "SELECT u FROM FasVerAipFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo "
                        + "AND u.cdVerAip LIKE '1.%' " + "ORDER BY u.pgVerAipFascicolo DESC ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("idFascicolo", idFascicolo);
                fasVerAipFascicoloList = query.getResultList();
                if (!fasVerAipFascicoloList.isEmpty()) {
                    // Scompatto il campo cdVerAip
                    String[] numbers = fasVerAipFascicoloList.get(0).getCdVerAip().split("[.]");
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

    // MEV#26576
    /**
     * Ricava la nuova versione dell'indice AIP v2.0 che mi appresto a creare
     *
     * @param idFascicolo
     *            id unita doc
     * @param tiCreazione
     *            tipo creazione
     *
     * @return la nuova versione
     *
     * @throws ParerInternalError
     *             errore generico
     */
    @Deprecated
    public String getVersioneIndiceAIPV2(Long idFascicolo, String tiCreazione) throws ParerInternalError {
        String versione = null;
        try {
            String queryStr;
            Query query;
            List<FasVerAipFascicolo> fasVerAipFascicoloList;
            // if (tiCreazione.equals("ARCHIVIO")) {
            // Ricavo l'ultima versione dell'AIP di tipo "ARCHIVIO" (cdVerAip che comincia per 2.)"
            queryStr = "SELECT u FROM FasVerAipFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo "
                    + "AND u.cdVerAip LIKE '2.%' " + "ORDER BY u.pgVerAipFascicolo DESC ";
            query = entityManager.createQuery(queryStr);
            query.setParameter("idFascicolo", idFascicolo);
            fasVerAipFascicoloList = query.getResultList();
            if (!fasVerAipFascicoloList.isEmpty()) {
                // Scompatto il campo cdVerAip
                String[] numbers = fasVerAipFascicoloList.get(0).getCdVerAip().split("[.]");
                int minorNumber = Integer.parseInt(numbers[1]);
                versione = "2.".concat(Integer.toString(++minorNumber));
            } else {
                // Se non ho risultati significa che sto inserendo la prima versione
                versione = "2.0";
            }
            // }
        } catch (NumberFormatException e) {
            log.error("Eccezione durante il recupero della versione AIP v2.0", e);
            throw new ParerInternalError(e);
        }
        return versione;
    }

    /**
     * Recupera xml del tipo passato relativo al versamento del fascicolo
     *
     * @param idFascicolo
     *            id fascicolo
     * @param tiXmlVers
     *            tipo xml versamento
     *
     * @return xml di versamento per AIP fascicolo
     */
    public FasXmlVersFascicolo getFasXmlVersFascicolo(Long idFascicolo, String tiXmlVers) {

        String queryStr = "SELECT u FROM FasXmlVersFascicolo u "
                + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo AND u.tiXmlVers = :tiXmlVers";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idFascicolo", idFascicolo);
        query.setParameter("tiXmlVers", tiXmlVers);

        return (FasXmlVersFascicolo) query.getSingleResult();
    }
    // end MEV#26576

    // MEV#29589
    /**
     * Ricava la nuova versione dei metadati del fascicolo AIP v2.0 che mi appresto a creare
     *
     * @param idFascicolo
     *            id unita doc
     * @param tiCreazione
     *            tipo creazione
     *
     * @return la nuova versione
     *
     * @throws ParerInternalError
     *             errore generico
     */
    public String getVersioneMetadatiAIPV2(Long idFascicolo, String tiCreazione) throws ParerInternalError {
        String versione = null;
        try {
            String queryStr;
            Query query;
            List<FasVerAipFascicolo> fasVerAipFascicoloList;
            // if (tiCreazione.equals("ARCHIVIO")) {
            // Ricavo l'ultima versione dell'AIP di tipo "ARCHIVIO" (cdVerAip che comincia per 1.)"
            queryStr = "SELECT u FROM FasVerAipFascicolo u " + "WHERE u.fasFascicolo.idFascicolo = :idFascicolo "
                    + "AND u.cdVerAip LIKE '1.%' " + "ORDER BY u.pgVerAipFascicolo DESC ";
            query = entityManager.createQuery(queryStr);
            query.setParameter("idFascicolo", idFascicolo);
            fasVerAipFascicoloList = query.getResultList();
            if (!fasVerAipFascicoloList.isEmpty()) {
                // Scompatto il campo cdVerAip
                String[] numbers = fasVerAipFascicoloList.get(0).getCdVerAip().split("[.]");
                int minorNumber = Integer.parseInt(numbers[1]);
                versione = "2.".concat(Integer.toString(++minorNumber));
            } else {
                // Se non ho risultati significa che sto inserendo la prima versione dei metadati del fascicolo
                versione = "2.0";
            }
            // }
        } catch (NumberFormatException e) {
            log.error("Eccezione durante il recupero della versione metadati AIP v2.0", e);
            throw new ParerInternalError(e);
        }
        return versione;
    }
    // end MEV#29589

    /**
     * Metodo transazionale per la registrazione dell'indice AIP attraverso la memorizzazione dei record nelle apposite
     * entity.
     *
     * @param fascDaElab
     *            entity FasAipFascicoloDaElab
     * @param progressivoVersione
     *            progressivo
     * @param codiceVersione
     *            codice versione
     * @param sistemaConservazione
     *            sistema conservazione
     *
     * @return entity FasVerAipFascicolo
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FasVerAipFascicolo registraAIP(FasAipFascicoloDaElab fascDaElab, int progressivoVersione,
            String codiceVersione, String sistemaConservazione) {
        // Chiamata locale, è giusto che partecipi alla stessa transazione. Non occorre che passi dal proxy EJB.
        FasFascicolo fasFascicolo = entityManager.find(FasFascicolo.class,
                fascDaElab.getFasFascicolo().getIdFascicolo());
        ElvElencoVersFasc elenco = entityManager.find(ElvElencoVersFasc.class,
                fasFascicolo.getElvElencoVersFasc().getIdElencoVersFasc());

        CSVersatore versatore = new CSVersatore();
        versatore.setSistemaConservazione(sistemaConservazione);
        versatore.setAmbiente(fasFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        versatore.setEnte(fasFascicolo.getOrgStrut().getOrgEnte().getNmEnte());
        versatore.setStruttura(fasFascicolo.getOrgStrut().getNmStrut());

        CSChiaveFasc chiaveFasc = new CSChiaveFasc();
        chiaveFasc.setAnno(fasFascicolo.getAaFascicolo().intValue());
        chiaveFasc.setNumero(fasFascicolo.getCdKeyFascicolo());

        /* Inserisco FAS_VER_AIP_FASCICOLO */
        FasVerAipFascicolo verAipFascicolo = new FasVerAipFascicolo();
        verAipFascicolo.setFasFascicolo(fasFascicolo);
        verAipFascicolo.setFasContenVerAipFascicolos(new ArrayList<>());
        verAipFascicolo.setFasMetaVerAipFascicolos(new ArrayList<>());
        verAipFascicolo.setPgVerAipFascicolo(new BigDecimal(progressivoVersione));
        verAipFascicolo.setCdVerAip(codiceVersione);
        verAipFascicolo.setDtCreazione(new Date());
        verAipFascicolo.setDsCausale(fascDaElab.getDsCausale());
        verAipFascicolo.setElvElencoVersFasc(elenco);
        verAipFascicolo.setFasSipVerAipFascicolos(new ArrayList<>());
        verAipFascicolo.setIdEnteConserv(fasFascicolo.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdEnteConserv());
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnFascicolo(MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc));
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnFascicolo(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc, true, Costanti.UrnFormatter.FASC_FMT_STRING));
        // salvo ORIGINALE
        verAipFascicolo.setDsUrnAipFascicolo(MessaggiWSFormat.formattaUrnAipFascicolo(tmpUrn));
        // salvo NORMALIZZATO
        verAipFascicolo.setDsUrnNormalizAipFascicolo(MessaggiWSFormat.formattaUrnAipFascicolo(tmpUrnNorm));
        fasFascicolo.getFasVerAipFascicolos().add(verAipFascicolo);

        /* Inserisco FAS_CONTEN_VER_AIP_FASCICOLO */
        for (FasUdAipFascicoloDaElab udAipFascicoloDaElab : fascDaElab.getFasUdAipFascicoloDaElabs()) {
            FasContenVerAipFascicolo contenVerAipFascicolo = new FasContenVerAipFascicolo();
            contenVerAipFascicolo.setFasVerAipFascicolo(verAipFascicolo);
            // TODO: verificare, datoVerAipFascicolo.setFasVerAipFascicoloFiglio(verAipFascicolo);
            // Composizione nome contenuto
            CSChiave chiave = new CSChiave();
            chiave.setTipoRegistro(udAipFascicoloDaElab.getAroVerIndiceAipUd().getAroIndiceAipUd().getAroUnitaDoc()
                    .getCdRegistroKeyUnitaDoc());
            chiave.setAnno(udAipFascicoloDaElab.getAroVerIndiceAipUd().getAroIndiceAipUd().getAroUnitaDoc()
                    .getAaKeyUnitaDoc().longValue());
            chiave.setNumero(udAipFascicoloDaElab.getAroVerIndiceAipUd().getAroIndiceAipUd().getAroUnitaDoc()
                    .getCdKeyUnitaDoc());
            // <sistemaconservazione>_<ente>_<struttura>_<registro>-<anno>-<numero>_IndiceAIP-UD-<versione>.xml
            contenVerAipFascicolo.setNmConten(MessaggiWSFormat.formattaChiaveUdFull(versatore, chiave)
                    + "_IndiceAIP-UD-" + udAipFascicoloDaElab.getAroVerIndiceAipUd().getCdVerIndiceAip()
                    + ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.getFileExtension());
            contenVerAipFascicolo.setTiConten("AIP_UNITA_DOC");
            contenVerAipFascicolo.setAroVerIndiceAipUd(udAipFascicoloDaElab.getAroVerIndiceAipUd());
            verAipFascicolo.getFasContenVerAipFascicolos().add(contenVerAipFascicolo);
        }

        // Determino lo xml di richiesta del versamento del fascicolo
        FasXmlVersFascicolo fasXmlVersFascicoloRich = IterableUtils.find(fasFascicolo.getFasXmlVersFascicolos(),
                object -> (object).getTiXmlVers().equals("RICHIESTA"));

        // Determino lo xml di risposta del versamento del fascicolo
        FasXmlVersFascicolo fasXmlVersFascicoloRisp = IterableUtils.find(fasFascicolo.getFasXmlVersFascicolos(),
                object -> (object).getTiXmlVers().equals("RISPOSTA"));

        /* Inserisco FAS_SIP_VER_AIP_FASCICOLO */
        FasSipVerAipFascicolo sipVerFascicolo = new FasSipVerAipFascicolo();
        sipVerFascicolo.setFasVerAipFascicolo(verAipFascicolo);
        sipVerFascicolo.setNmSip("SIP_VERSAMENTO");
        sipVerFascicolo.setTiSip("VERSAMENTO_FASCICOLO");
        sipVerFascicolo.setFasXmlVersFascicoloRich(fasXmlVersFascicoloRich);
        sipVerFascicolo.setFasXmlVersFascicoloRisp(fasXmlVersFascicoloRisp);
        verAipFascicolo.getFasSipVerAipFascicolos().add(sipVerFascicolo);

        entityManager.persist(verAipFascicolo);

        return verAipFascicolo;
    }

    public void eliminaIndiceAipDaElab(FasAipFascicoloDaElab fascDaElab) {
        entityManager.remove(fascDaElab);
        entityManager.flush();
    }

    public FasAipFascicoloDaElab findFasAipFascicoloDaElab(long idFasAipFascicoloDaElab) {
        return entityManager.find(FasAipFascicoloDaElab.class, idFasAipFascicoloDaElab);
    }

    public List<ElvVLisIxAipFascByEle> retrieveElvVLisIxAipFascByEleOrdered(long idElencoVersFasc) {
        Query query = entityManager.createQuery("SELECT lisIxAipFascByEle FROM ElvVLisIxAipFascByEle lisIxAipFascByEle "
                + "WHERE lisIxAipFascByEle.idElencoVersFasc = :idElencoVersFasc "
                + "ORDER BY lisIxAipFascByEle.cdKeyOrd");
        query.setParameter("idElencoVersFasc", bigDecimalFromLong(idElencoVersFasc));
        return query.getResultList();
    }

    public void lockFascicolo(FasFascicolo fascicolo) {
        entityManager.lock(fascicolo, LockModeType.PESSIMISTIC_WRITE);
    }

    /**
     * Ricava il modello xsd attivo per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene e per
     * il tipo in input
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello xsd
     *
     * @return lista oggetti di tipo {@link DecModelloXsdFascicolo}
     */
    public List<DecModelloXsdFascicolo> retrieveIdModelliDaElaborare(long idAmbiente, String tiModelloXsd) {
        String queryStr = "SELECT modello " + "FROM DecModelloXsdFascicolo modello "
                + "WHERE modello.orgAmbiente.idAmbiente = :idAmbiente " + "AND modello.tiModelloXsd = :tiModelloXsd "
                + "AND modello.dtIstituz <= :filterDate AND modello.dtSoppres >= :filterDate";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idAmbiente", idAmbiente);
        q.setParameter("tiModelloXsd", TiModelloXsd.valueOf(tiModelloXsd));
        q.setParameter("filterDate", Calendar.getInstance().getTime());
        return q.getResultList();
    }

    // MEV#26576
    /**
     * Ricava il modello xsd attivo per l'ambiente di appartenenza della struttura a cui il fascicolo appartiene e per
     * la versione del modello xsd corrispondente a quella del servizio di versamento fascicolo, con il tipo
     * "AIP_UNISYNCRO"
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello
     * @param cdVersioneXml
     *            versione del servizio di versamento fascicolo
     *
     * @return lista oggetti di tipo {@link DecModelloXsdFascicolo}
     */
    public List<DecModelloXsdFascicolo> retrieveIdModelliDaElaborareV2(long idAmbiente, String tiModelloXsd,
            String cdVersioneXml) {
        String queryStr = "SELECT modello " + "FROM DecModelloXsdFascicolo modello "
                + "WHERE modello.orgAmbiente.idAmbiente = :idAmbiente " + "AND modello.tiModelloXsd = :tiModelloXsd "
                + "AND modello.dtIstituz <= :filterDate AND modello.dtSoppres >= :filterDate "
                + "AND modello.cdXsd = :cdVersioneXml";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idAmbiente", idAmbiente);
        q.setParameter("tiModelloXsd", TiModelloXsd.valueOf(tiModelloXsd));
        q.setParameter("filterDate", Calendar.getInstance().getTime());
        q.setParameter("cdVersioneXml", cdVersioneXml);
        return q.getResultList();
    }
    // end MEV#26576

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FasMetaVerAipFascicolo registraFasMetaVerAipFascicolo(FasVerAipFascicolo verAipFascicolo, String hash,
            String algoHash, String encodingHash, String codiceVersione, CSVersatore versatore,
            CSChiaveFasc chiaveFasc) {
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = new FasMetaVerAipFascicolo();
        fasMetaVerAipFascicolo.setFasVerAipFascicolo(verAipFascicolo);
        fasMetaVerAipFascicolo.setNmMeta("IndiceAIP-FA-" + verAipFascicolo.getCdVerAip());
        fasMetaVerAipFascicolo.setTiMeta("INDICE");
        fasMetaVerAipFascicolo.setTiFormatoMeta("UNISYNCRO");
        fasMetaVerAipFascicolo.setDsHashFile(hash);
        fasMetaVerAipFascicolo.setDsAlgoHashFile(algoHash);
        fasMetaVerAipFascicolo.setCdEncodingHashFile(encodingHash);
        // calcolo parte urn ORIGINALE
        String tmpUrn = MessaggiWSFormat.formattaBaseUrnFascicolo(MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc));
        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnFascicolo(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartFasc(chiaveFasc, true, Costanti.UrnFormatter.FASC_FMT_STRING));
        // salvo ORIGINALE
        fasMetaVerAipFascicolo.setDsUrnMetaFascicolo(MessaggiWSFormat.formattaUrnIndiceAipFascicoli(tmpUrn,
                codiceVersione, Costanti.UrnFormatter.URN_INDICE_AIP_FASC_FMT_STRING_V2));
        // salvo NORMALIZZATO
        fasMetaVerAipFascicolo.setDsUrnNormalizMetaFascicolo(MessaggiWSFormat.formattaUrnIndiceAipFascicoli(tmpUrnNorm,
                codiceVersione, Costanti.UrnFormatter.URN_INDICE_AIP_FASC_FMT_STRING_V2));
        getEntityManager().persist(fasMetaVerAipFascicolo);
        getEntityManager().flush();
        if (verAipFascicolo.getFasMetaVerAipFascicolos() == null) {
            verAipFascicolo.setFasMetaVerAipFascicolos(new ArrayList<>());
        }
        verAipFascicolo.getFasMetaVerAipFascicolos().add(fasMetaVerAipFascicolo);
        return fasMetaVerAipFascicolo;
    }

    public FasFileMetaVerAipFasc registraFasFileMetaVerAipFasc(long idMetaVerAipFascicolo, String file, OrgStrut strut,
            Date dtCreazione, BackendStorage backendMetadata, Map<String, String> indiceAipFascicoloBlob) {
        FasFileMetaVerAipFasc fileMetaVerAipFasc = new FasFileMetaVerAipFasc();
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = getEntityManager().find(FasMetaVerAipFascicolo.class,
                idMetaVerAipFascicolo);
        fileMetaVerAipFasc.setFasMetaVerAipFascicolo(fasMetaVerAipFascicolo);
        // MEV#30398
        if (backendMetadata.isDataBase()) {
            // clob contenente lo XML in input (canonicalizzato)
            fileMetaVerAipFasc.setBlFileVerIndiceAip(file);
        } else {
            indiceAipFascicoloBlob.put(it.eng.parer.entity.constraint.FasMetaVerAipFascicolo.TiMeta.INDICE.name(),
                    file);
        }
        // end MEV#30398
        fileMetaVerAipFasc.setOrgStrut(strut);
        fileMetaVerAipFasc.setDtCreazione(dtCreazione);
        getEntityManager().persist(fileMetaVerAipFasc);
        getEntityManager().flush();
        if (fasMetaVerAipFascicolo.getFasFileMetaVerAipFascs() == null) {
            fasMetaVerAipFascicolo.setFasFileMetaVerAipFascs(new ArrayList<>());
        }
        fasMetaVerAipFascicolo.getFasFileMetaVerAipFascs().add(fileMetaVerAipFasc);
        return fileMetaVerAipFasc;
    }

    public FasXsdMetaVerAipFasc registraFasXsdMetaVerAipFasc(long idMetaVerAipFascicolo, long idModelloXsdFascicolo,
            String nmXsd) {
        FasXsdMetaVerAipFasc xsdMetaVerAipFasc = new FasXsdMetaVerAipFasc();
        FasMetaVerAipFascicolo fasMetaVerAipFascicolo = getEntityManager().find(FasMetaVerAipFascicolo.class,
                idMetaVerAipFascicolo);
        DecModelloXsdFascicolo decModelloXsdFascicolo = getEntityManager().find(DecModelloXsdFascicolo.class,
                idModelloXsdFascicolo);
        xsdMetaVerAipFasc.setFasMetaVerAipFascicolo(fasMetaVerAipFascicolo);
        xsdMetaVerAipFasc.setDecModelloXsdFascicolo(decModelloXsdFascicolo);
        xsdMetaVerAipFasc.setNmXsd(nmXsd);
        getEntityManager().persist(xsdMetaVerAipFasc);
        getEntityManager().flush();
        if (fasMetaVerAipFascicolo.getFasXsdMetaVerAipFascs() == null) {
            fasMetaVerAipFascicolo.setFasXsdMetaVerAipFascs(new ArrayList<>());
        }
        fasMetaVerAipFascicolo.getFasXsdMetaVerAipFascs().add(xsdMetaVerAipFasc);

        if (decModelloXsdFascicolo.getFasXsdMetaVerAipFascs() == null) {
            decModelloXsdFascicolo.setFasXsdMetaVerAipFascs(new ArrayList<>());
        }
        decModelloXsdFascicolo.getFasXsdMetaVerAipFascs().add(xsdMetaVerAipFasc);

        return xsdMetaVerAipFasc;
    }

    public List<FasVerAipFascicolo> retrieveFasVerAipFascicoloOrdered(long idElencoVersFasc) {
        Query query = entityManager.createQuery("SELECT verAipFascicolo FROM FasVerAipFascicolo verAipFascicolo "
                + "JOIN verAipFascicolo.fasFascicolo ff "
                + "WHERE verAipFascicolo.elvElencoVersFasc.idElencoVersFasc = :idElencoVersFasc "
                + "AND ff.tiStatoConservazione != :tiStatoConservazione "
                + "ORDER BY ff.cdKeyOrd, verAipFascicolo.pgVerAipFascicolo");
        query.setParameter("idElencoVersFasc", idElencoVersFasc);
        query.setParameter("tiStatoConservazione", TiStatoConservazione.ANNULLATO);
        return query.getResultList();
    }

    public DecAaTipoFascicolo retrieveDecAaTipoFascicoloCorrente(long idTipoFascicolo) {
        Query query = entityManager.createQuery("SELECT aaTipoFascicolo FROM DecAaTipoFascicolo aaTipoFascicolo "
                + "WHERE aaTipoFascicolo.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo "
                + "AND aaTipoFascicolo.aaIniTipoFascicolo <= :aaCorrente AND aaTipoFascicolo.aaFinTipoFascicolo >= :aaCorrente ");
        query.setParameter("idTipoFascicolo", idTipoFascicolo);
        query.setParameter("aaCorrente", BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        List<DecAaTipoFascicolo> lista = query.getResultList();
        return lista.get(0);
    }
}
