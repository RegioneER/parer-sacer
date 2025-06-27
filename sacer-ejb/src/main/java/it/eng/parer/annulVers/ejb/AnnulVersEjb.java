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
package it.eng.parer.annulVers.ejb;

import com.csvreader.CsvReader;
import it.eng.parer.annulVers.dto.FascBean;
import it.eng.parer.annulVers.dto.RicercaRichAnnulVersBean;
import it.eng.parer.annulVers.dto.UnitaDocBean;
import it.eng.parer.annulVers.helper.AnnulVersHelper;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasStatoConservFascicolo.TiStatoConservazione;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.viewEntity.*;
import it.eng.parer.web.ejb.UnitaDocumentarieEjb;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto.InvioRichiestaAnnullamentoVersamentiExt;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.richAnnullVers.RichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.richAnnullVers.TipoVersamentoType;
import it.eng.parer.ws.xml.richAnnullVers.VersamentoDaAnnullareType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class AnnulVersEjb {

    private static final Logger logger = LoggerFactory.getLogger(AnnulVersEjb.class);
    private static final String LOG_MESSAGE_ANNULLA_UD = "Annullamento Versamenti Unit\u00E0 Documentarie --- ";

    @Resource
    private SessionContext context;
    @EJB
    private AnnulVersHelper helper;
    @EJB
    private UnitaDocumentarieHelper udHelper;
    @EJB
    private ComponentiHelper compHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private ElencoVersFascicoliHelper evfHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private FascicoliHelper fascicoliHelper;
    // MEV #31162
    @EJB
    private UnitaDocumentarieEjb udEjb;
    // end MEV #31162

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    // <editor-fold defaultstate="collapsed" desc="Creazione richiesta annullamento
    // versamenti">
    /**
     * Verifica l'esistenza degli header REGISTRO, ANNO, NUMERO all'interno del file inviato
     *
     * @param fileByteArray
     *            file byte array
     *
     * @return true se sono presenti tutti gli header richiesti
     *
     * @throws IOException
     *             eccezione di tipo IO
     */
    public boolean checkCsvHeaders(byte[] fileByteArray) throws IOException {
        boolean result = true;
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray), StandardCharsets.UTF_8);
        csvReader.setSkipEmptyRecords(true);
        try {
            if (csvReader.readHeaders()) {
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                for (String header : headers) {
                    if (header.equalsIgnoreCase(CostantiDB.NomeCampo.REGISTRO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                        set.add(header.toUpperCase());
                    }
                }
                if (set.size() < 3) {
                    result = false;
                }
            }
        } finally {
            csvReader.close();
        }
        return result;
    }

    /**
     * Verifica l'esistenza degli header ANNO e NUMERO all'interno del file inviato
     *
     * @param fileByteArray
     *            file byte array
     *
     * @return true se sono presenti tutti gli header richiesti
     *
     * @throws IOException
     *             eccezione di tipo IO
     */
    public boolean checkCsvHeadersFasc(byte[] fileByteArray) throws IOException {
        boolean result = true;
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray), StandardCharsets.UTF_8);
        csvReader.setSkipEmptyRecords(true);
        try {
            if (csvReader.readHeaders()) {
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                Set<String> set = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                for (String header : headers) {
                    if (header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                        set.add(header.toUpperCase());
                    }
                }
                if (set.size() < 2) {
                    result = false;
                }
            }
        } finally {
            csvReader.close();
        }
        return result;
    }

    /**
     * Verifica l'esistenza di una precedente richiesta di annullamento per codice richiesta
     *
     * @param cdRichAnnulVers
     *            codice richiesta annullamento
     * @param idStrut
     *            id struttura
     *
     * @return true se esiste gi\u00E0 una richiesta di annullamento con codice <code>cdRichAnnulVers</code>
     */
    public boolean checkCdRichAnnulVersExisting(String cdRichAnnulVers, BigDecimal idStrut) {
        return helper.isRichAnnulVersExisting(cdRichAnnulVers, idStrut);
    }

    /**
     * Esegue il salvataggio in transazione del nuovo record di richiesta annullamento versamento
     *
     * @param idUserIam
     *            utente che crea la richiesta di annullamento
     * @param cdRichAnnulVers
     *            codice richiesta
     * @param dsRichAnnulVers
     *            descrizione richiesta
     * @param ntRichAnnulVers
     *            nota richiesta
     * @param flImmediata
     *            flag di richiesta immediata
     * @param fileByteArray
     *            upload caricato dall'utente
     * @param idStrut
     *            la struttura per cui viene creata la serie
     * @param flForzaAnnul
     *            flag forzatura annullamento
     *
     * @param tiAnnullamento
     *            tipo di annullamento
     *
     * @param tiRichAnnulVers
     *            tipo richiesta annullamento
     *
     *
     * @return id richiesta
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveRichAnnulVers(long idUserIam, String cdRichAnnulVers, String dsRichAnnulVers,
            String ntRichAnnulVers, String flImmediata, byte[] fileByteArray, BigDecimal idStrut, String flForzaAnnul,
            String tiAnnullamento, String tiRichAnnulVers) throws ParerUserError {
        logger.info("Eseguo il salvataggio della richiesta di annullamento");
        Date now = Calendar.getInstance().getTime();
        Long idRich = null;
        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
            IamUser user = helper.findById(IamUser.class, idUserIam);
            if (user.getAroStatoRichAnnulVers() == null) {
                user.setAroStatoRichAnnulVers(new ArrayList<>());
            }

            // Preparo la richiesta da registrare
            AroRichAnnulVers rich = new AroRichAnnulVers();
            rich.setCdRichAnnulVers(cdRichAnnulVers);
            rich.setDsRichAnnulVers(dsRichAnnulVers);
            rich.setNtRichAnnulVers(ntRichAnnulVers);
            rich.setTiRichAnnulVers(tiRichAnnulVers);
            rich.setDtCreazioneRichAnnulVers(now);
            rich.setTiCreazioneRichAnnulVers(
                    fileByteArray != null ? CostantiDB.TipoCreazioneRichAnnulVers.UPLOAD_FILE.name()
                            : CostantiDB.TipoCreazioneRichAnnulVers.ON_LINE.name());
            rich.setFlImmediata(flImmediata);
            rich.setFlForzaAnnul(flForzaAnnul);
            rich.setTiAnnullamento(tiAnnullamento);
            rich.setFlRichPing("0");
            rich.setOrgStrut(strut);
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

            // Preparo lo stato da registrare
            String stato = (flImmediata.equals("1") && fileByteArray != null)
                    ? CostantiDB.StatoRichAnnulVers.CHIUSA.name() : CostantiDB.StatoRichAnnulVers.APERTA.name();
            AroStatoRichAnnulVers statoRichAnnulVers = context.getBusinessObject(AnnulVersEjb.class)
                    .createAroStatoRichAnnulVers(rich, stato, now, null, user);

            // Se è specificato il file, devo preparare il file da registrare in
            // ARO_FILE_RICH_ANNUL_VERS
            if (tiRichAnnulVers.equals("UNITA_DOC")) {
                if (fileByteArray != null) {
                    AroFileRichAnnulVers fileRich = new AroFileRichAnnulVers();
                    fileRich.setTiFile(CostantiDB.TipoFileRichAnnulVers.FILE_UD_ANNUL.name());
                    fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
                    rich.addAroFileRichAnnulVer(fileRich);

                    // Preparo gli item e gli eventuali errori in ARO_ITEM_RICH_ANNUL_VERS e
                    // ARO_ERR_RICH_ANNUL_VERS
                    context.getBusinessObject(AnnulVersEjb.class).handleCsvRecords(rich, fileByteArray, idUserIam);
                }
            } else if (tiRichAnnulVers.equals("FASCICOLI")) {
                if (fileByteArray != null) {
                    AroFileRichAnnulVers fileRich = new AroFileRichAnnulVers();
                    fileRich.setTiFile(CostantiDB.TipoFileRichAnnulVers.FILE_FASC_ANNUL.name());
                    fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
                    rich.addAroFileRichAnnulVer(fileRich);

                    // Preparo gli item e gli eventuali errori in ARO_ITEM_RICH_ANNUL_VERS e
                    // ARO_ERR_RICH_ANNUL_VERS
                    context.getBusinessObject(AnnulVersEjb.class).handleCsvRecordsFasc(rich, fileByteArray, idUserIam);
                }
            }

            helper.insertEntity(statoRichAnnulVers, true);

            // Aggiorno l’identificatore dello stato corrente della richiesta assegnando
            // l’identificatore dello stato
            // inserito
            rich.setIdStatoRichAnnulVersCor(new BigDecimal(statoRichAnnulVers.getIdStatoRichAnnulVers()));

            logger.info("Salvataggio della richiesta annullamento completato");
            idRich = rich.getIdRichAnnulVers();
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio della richiesta di annullamento del versamento : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di annullamento del versamento");
        }
        return idRich;
    }

    /**
     * Esegue il salvataggio in transazione del nuovo record di richiesta annullamento versamento
     *
     * @param idUserIam
     *            id user Iam
     * @param cdRichAnnulVers
     *            codice richiesta
     * @param dsRichAnnulVers
     *            descrizione richiesta
     * @param ntRichAnnulVers
     *            nota richiesta
     * @param tiRichAnnulVers
     *            tipo richiesta
     * @param flImmediata
     *            indicatore di annullamento immediato
     * @param dtCreazione
     *            data creazione
     * @param idStrut
     *            identificativo della struttura corrente
     * @param flForzaAnnul
     *            indicatore di forzatura annullamento
     * @param flRichiestaPing
     *            indicatore di richiesta annullamento da PreIngest
     *
     * @param tiAnnulRichAnnulVers
     *            tipo annullamento
     * @param ravExt
     *            invio richiesta annullamento
     *
     * @return id richiesta
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroRichAnnulVers insertRichAnnulVers(long idUserIam, Long idStrut, String cdRichAnnulVers,
            String dsRichAnnulVers, String ntRichAnnulVers, String tiRichAnnulVers, Date dtCreazione,
            boolean flImmediata, boolean flForzaAnnul, boolean flRichiestaPing, String tiAnnulRichAnnulVers,
            InvioRichiestaAnnullamentoVersamentiExt ravExt) throws ParerUserError {
        logger.info("Eseguo il salvataggio della richiesta annullamento");
        AroRichAnnulVers rich = new AroRichAnnulVers();
        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);

            // Inizializzo la richiesta creata dal WS
            rich = initAroRichAnnulVers(cdRichAnnulVers, dsRichAnnulVers, ntRichAnnulVers, tiRichAnnulVers, dtCreazione,
                    CostantiDB.TipoCreazioneRichAnnulVers.WEB_SERVICE.name(), flImmediata, flForzaAnnul,
                    flRichiestaPing, tiAnnulRichAnnulVers, strut);

            // Persisto la richiesta
            helper.insertEntity(rich, true);

            // Popolo gli item
            handleXmlRecords(rich, ravExt.getRichiestaAnnullamentoVersamenti().getVersamentiDaAnnullare(), idUserIam);

            logger.info("Salvataggio della richiesta annullamento completato dal WS ");
        } catch (Exception ex) {
            logger.error(
                    "Errore imprevisto durante il salvataggio della richiesta di annullamento del versamento da parte del WS: "
                            + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di annullamento del versamento da parte del WS");
        }
        return rich;
    }

    private AroRichAnnulVers initAroRichAnnulVers(String cdRichAnnulVers, String dsRichAnnulVers,
            String ntRichAnnulVers, String tiRichAnnulVers, Date now, String tiCreazione, boolean flImmediata,
            boolean flForzaAnnul, boolean flRichiestaPing, String tiAnnulRichAnnulVers, OrgStrut strut) {
        AroRichAnnulVers rich = new AroRichAnnulVers();
        rich.setCdRichAnnulVers(cdRichAnnulVers);
        rich.setDsRichAnnulVers(dsRichAnnulVers);
        rich.setNtRichAnnulVers(ntRichAnnulVers);
        rich.setTiRichAnnulVers(tiRichAnnulVers);
        rich.setDtCreazioneRichAnnulVers(now);
        rich.setTiCreazioneRichAnnulVers(tiCreazione);
        rich.setFlImmediata(flImmediata ? "1" : "0");
        rich.setFlForzaAnnul(flForzaAnnul ? "1" : "0");
        rich.setFlRichPing(flRichiestaPing ? "1" : "0");
        rich.setOrgStrut(strut);
        rich.setTiAnnullamento(tiAnnulRichAnnulVers);
        if (rich.getAroFileRichAnnulVers() == null) {
            rich.setAroFileRichAnnulVers(new ArrayList<>());
        }
        if (rich.getAroItemRichAnnulVers() == null) {
            rich.setAroItemRichAnnulVers(new ArrayList<>());
        }
        if (rich.getAroStatoRichAnnulVers() == null) {
            rich.setAroStatoRichAnnulVers(new ArrayList<>());
        }
        return rich;
    }

    /**
     * Legge il csv e crea gli oggetti AroItemRichAnnulVers da salvare alla richiesta di annullamento e gli eventuali
     * errori in AroErrRichAnnulVers
     *
     * @param rich
     *            la richiesta di annullamento
     * @param fileByteArray
     *            il file csv in byte[]
     * @param idUserIam
     *            id utente che ha creato la richiesta (ovvero colui che ha definito il primo stato)
     *
     * @throws IOException
     *             eccezione di tipo IO
     * @throws ParerUserError
     *             Errore gestito annullamento a ping
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleCsvRecords(AroRichAnnulVers rich, byte[] fileByteArray, long idUserIam)
            throws IOException, ParerUserError {
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray), StandardCharsets.UTF_8);
        csvReader.setSkipEmptyRecords(true);
        try {
            if (csvReader.readHeaders()) {
                logger.debug("Eseguo il parsing del file csv");
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                List<String> headersCorretti = new ArrayList<>();
                // Ottengo gli header corretti ignorando il case,
                // per cercare di produrre in lettura delle righe meno cicli possibili
                for (String header : headers) {
                    if (header.equalsIgnoreCase(CostantiDB.NomeCampo.REGISTRO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                        headersCorretti.add(header);
                    }
                }
                int progressivoItem = 1;
                Set<UnitaDocBean> udsInRich = new HashSet<>();

                /*
                 * PER OGNI RECORD PRESENTE NEL FILE CSV
                 */
                while (csvReader.readRecord()) {
                    String registro = null;
                    String annoString = null;
                    String numero = null;
                    for (String header : headersCorretti) {
                        String valoreCampo = csvReader.get(header);
                        if (header.equalsIgnoreCase(CostantiDB.NomeCampo.REGISTRO.name())) {
                            registro = valoreCampo;
                        } else if (header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())) {
                            annoString = valoreCampo;
                        } else if (header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                            numero = valoreCampo;
                        }
                    }

                    // Creo un record item per ogni riga del csv
                    BigDecimal anno = new BigDecimal(annoString);
                    BigDecimal idStrut = new BigDecimal(rich.getOrgStrut().getIdStrut());

                    // Bonnie ha deciso di inserire l'ud in un set per controllare l'univocità in
                    // maniera tale che se il
                    // record
                    // è già presente nel set non viene inserito, la funzione di add restituisce
                    // false e non viene
                    // eseguito il codice all'interno dell'if
                    if (udsInRich.add(new UnitaDocBean(idStrut, registro, anno, numero))) {
                        // Se l'item non era già presente nella richiesta, preparo il record da
                        // registrare in
                        // ARO_ITEM_RICH_ANNUL_VERS
                        AroItemRichAnnulVers item = createAroItemRichAnnulVers(rich, registro, anno, numero,
                                progressivoItem);
                        // Ricavo l'ud non annullata
                        Long idUnitaDoc = udHelper.getIdUnitaDocVersataNoAnnul(idStrut, registro, anno, numero);
                        logger.debug("Controlli item da annullare");
                        if (idUnitaDoc != null) {
                            // UD esistente e non annullata
                            AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
                            // Aggiungo l'UD all'item
                            item.setAroUnitaDoc(ud);

                            /* CONTROLLI ITEM DA ANNULLARE */
                            controlloItemDaAnnullare(item, idUserIam);

                            if (item.getTiStatoItem()
                                    .equals(CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_PING.name())) {
                                throw new ParerUserError(
                                        "La richiesta non pu\u00F2 essere definita con annullamento immediato, perch\u00E9 per almeno una unit\u00E0 documentaria definita nella richiesta \u00E8 necessario provvedere al suo annullamento preventivo in PreIngest");
                            }
                        } else {
                            // Se non esiste una ud non annullata, allora controllo se esiste in generale
                            if (udHelper.existAroUnitaDoc(idStrut, registro, anno, numero)) {
                                // UD esistente - creo record di errore
                                String dsErr = "L'unit\u00E0 documentaria " + registro + "-" + annoString + "-" + numero
                                        + " \u00E8 gi\u00E0 stata annullata";
                                createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                        CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                                        CostantiDB.TipoGravitaErrore.ERRORE.name());
                            } else {
                                // UD non esistente - creo diverso record di errore
                                String dsErr = "L'unit\u00E0 documentaria " + registro + "-" + annoString + "-" + numero
                                        + " non esiste";
                                createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                        CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                        CostantiDB.TipoGravitaErrore.ERRORE.name());
                            }
                        }
                        progressivoItem++;
                    }
                } // Fine per ogni record del csv
                if (progressivoItem == 1) {
                    // Il file non conteneva nemmeno una riga di ud, a parte l'intestazione
                    throw new ParerUserError("Il file non contiene alcuna unit\u00E0 documentaria da annullare");
                }
            } else {
                throw new IllegalStateException(
                        "Errore imprevisto nella lettura del file caricato per la creazione della richiesta di annullamento del versamento. Presumibilmente file non in formato csv.");
            }
        } finally {
            csvReader.close();
        }
    }

    /**
     * Legge il csv e crea gli oggetti AroItemRichAnnulVers da salvare alla richiesta di annullamento e gli eventuali
     * errori in AroErrRichAnnulVers riguardante i fascicoli
     *
     * @param rich
     *            la richiesta di annullamento
     * @param fileByteArray
     *            il file csv in byte[]
     * @param idUserIam
     *            id utente che ha creato la richiesta (ovvero colui che ha definito il primo stato)
     *
     * @throws IOException
     *             errore generico di tipo IO
     * @throws ParerUserError
     *             Errore gestito annullamento a ping
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleCsvRecordsFasc(AroRichAnnulVers rich, byte[] fileByteArray, long idUserIam)
            throws IOException, ParerUserError {
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray), StandardCharsets.UTF_8);
        csvReader.setSkipEmptyRecords(true);
        try {
            if (csvReader.readHeaders()) {
                logger.debug("Eseguo il parsing del file csv");
                List<String> headers = Arrays.asList(csvReader.getHeaders());
                List<String> headersCorretti = new ArrayList<>();
                // Ottengo gli header corretti ignorando il case,
                // per cercare di produrre in lettura delle righe meno cicli possibili
                for (String header : headers) {
                    if (header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())
                            || header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                        headersCorretti.add(header);
                    }
                }
                int progressivoItem = 1;
                Set<FascBean> fascInRich = new HashSet<>();

                /*
                 * PER OGNI RECORD PRESENTE NEL FILE CSV
                 */
                while (csvReader.readRecord()) {
                    String annoString = null;
                    String numero = null;
                    for (String header : headersCorretti) {
                        String valoreCampo = csvReader.get(header);
                        if (header.equalsIgnoreCase(CostantiDB.NomeCampo.ANNO.name())) {
                            annoString = valoreCampo;
                        } else if (header.equalsIgnoreCase(CostantiDB.NomeCampo.NUMERO.name())) {
                            numero = valoreCampo;
                        }
                    }

                    // Creo un record item per ogni riga del csv
                    BigDecimal anno = new BigDecimal(annoString);
                    BigDecimal idStrut = new BigDecimal(rich.getOrgStrut().getIdStrut());

                    // Bonnie ha deciso di inserire l'ud (fasc in questo caso) in un set per
                    // controllare l'univocità in
                    // maniera tale che se il record
                    // è già presente nel set non viene inserito, la funzione di add restituisce
                    // false e non viene
                    // eseguito il codice all'interno dell'if
                    if (fascInRich.add(new FascBean(idStrut, anno, numero))) {
                        // Se l'item non era già presente nella richiesta, preparo il record da
                        // registrare in
                        // ARO_ITEM_RICH_ANNUL_VERS
                        AroItemRichAnnulVers item = createAroItemRichAnnulVersFasc(rich, anno, numero, progressivoItem);
                        // Ricavo il fasicolo non annullato
                        Long idFascicolo = fascicoliHelper.getIdFascVersatoNoAnnul(idStrut, anno, numero);
                        logger.debug("Controlli item da annullare");
                        if (idFascicolo != null) {
                            // Fascicolo esistente e non annullato
                            FasFascicolo fasc = helper.findById(FasFascicolo.class, idFascicolo);
                            // Aggiungo il fascicolo all'item
                            item.setFasFascicolo(fasc);

                            /* CONTROLLI ITEM DA ANNULLARE */
                            controlloItemDaAnnullare(item, idUserIam);

                            if (item.getTiStatoItem()
                                    .equals(CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_PING.name())) {
                                throw new ParerUserError(
                                        "La richiesta non pu\u00F2 essere definita con annullamento immediato, perch\u00E9 per almeno un fascicolo definito nella richiesta \u00E8 necessario provvedere al suo annullamento preventivo in PreIngest");
                            }
                        } else {
                            // Se non esiste un fascicolo non annullato, allora controllo se esiste in
                            // generale
                            if (fascicoliHelper.existsFascicolo(idStrut, anno, numero)) {
                                // Fascicolo esistente - creo record di errore
                                String dsErr = "Il fascicolo " + annoString + "-" + numero
                                        + " \u00E8 gi\u00E0 stato annullato";
                                createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                        CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                                        CostantiDB.TipoGravitaErrore.ERRORE.name());
                            } else {
                                // Fascicolo non esistente - creo diverso record di errore
                                String dsErr = "Il fascicolo " + annoString + "-" + numero + " non esiste";
                                createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                        CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                        CostantiDB.TipoGravitaErrore.ERRORE.name());
                            }
                        }
                        progressivoItem++;
                    }
                } // Fine per ogni record del csv
                if (progressivoItem == 1) {
                    // Il file non conteneva nemmeno una riga di fascicoli, a parte l'intestazione
                    throw new ParerUserError("Il file non contiene alcun fascicolo da annullare");
                }
            } else {
                throw new IllegalStateException(
                        "Errore imprevisto nella lettura del file caricato per la creazione della richiesta di annullamento del versamento. Presumibilmente file non in formato csv.");
            }
        } finally {
            csvReader.close();
        }
    }

    /**
     * Legge la richiesta ottenuta dall'XML e crea gli oggetti AroItemRichAnnulVers da salvare alla richiesta di
     * annullamento insieme agli eventuali AroErrRichAnnulVers. Infine controlla le ud
     *
     * @param rich
     *            la richiesta di annullamento
     * @param versamentiDaAnnullare
     *            versamenti da annullare
     * @param idUserIam
     *            id user IAM
     *
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleXmlRecords(AroRichAnnulVers rich,
            RichiestaAnnullamentoVersamenti.VersamentiDaAnnullare versamentiDaAnnullare, long idUserIam) {
        int progressivoItem = 1;
        Set<UnitaDocBean> udsInRich = new HashSet<>();
        Set<FascBean> fascsInRich = new HashSet<>();
        logger.info("handleXmlRecords elabora {} elementi - INIZIO",
                versamentiDaAnnullare.getVersamentoDaAnnullare().size());
        for (VersamentoDaAnnullareType versamentoDaAnnullare : versamentiDaAnnullare.getVersamentoDaAnnullare()) {
            entityManager.flush();
            entityManager.clear();
            logger.debug(" {} - elaborazione [{}-{}-{}-{}] - INIZIO ***", progressivoItem,
                    versamentoDaAnnullare.getAnno(), versamentoDaAnnullare.getTipoVersamento(),
                    versamentoDaAnnullare.getTipoRegistro(), versamentoDaAnnullare.getNumero());
            String registro = versamentoDaAnnullare.getTipoRegistro();
            long annoInt = versamentoDaAnnullare.getAnno();
            String numero = versamentoDaAnnullare.getNumero();
            BigDecimal anno = new BigDecimal(annoInt);
            BigDecimal idStrut = new BigDecimal(rich.getOrgStrut().getIdStrut());

            // MEV#26446
            TipoVersamentoType tipoVersamento = versamentoDaAnnullare.getTipoVersamento();
            // end MEV#26446
            if (TipoVersamentoType.UNITA_DOCUMENTARIA.value().equals(tipoVersamento.value())) {
                /*
                 * Trucchetto bonnico che "sfrutta" una collection di natura "set" per verificare se un record è già
                 * presente nella richiesta. Se così fosse, il metodo add non aggiungerebbe il nuovo record e,
                 * restituendo esso false, verrebbe eseguita la porzione di codice presente nell'else
                 */
                if (udsInRich.add(new UnitaDocBean(idStrut, registro, anno, numero))) {
                    // Creo la AroItemRichAnnulVers figlia della AroRichAnnulVers
                    AroItemRichAnnulVers item = createAroItemRichAnnulVers(rich, registro, anno, numero,
                            progressivoItem, CostantiDB.TipiEntitaSacer.UNI_DOC.name(),
                            CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                    Long idUnitaDoc = udHelper.getIdUnitaDocVersataNoAnnul(idStrut, registro, anno, numero);
                    logger.debug("Controlli item da annullare");
                    if (idUnitaDoc != null) {
                        // UD esistente
                        AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
                        item.setAroUnitaDoc(ud);
                        // Già qui, controllo l'unità doc definita nella richiesta e che sto trattando
                        controlloItemDaAnnullare(item, idUserIam);
                    } else {
                        // Se non esiste una ud non annullata, allora controllo se esiste in generale
                        if (udHelper.existAroUnitaDoc(idStrut, registro, anno, numero)) {
                            // UD esistente - creo record di errore
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-" + annoInt + "-" + numero
                                    + " \u00E8 gi\u00E0 stata annullata";
                            createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else {
                            // UD non esistente - creo record di errore
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-" + annoInt + "-" + numero
                                    + " non esiste";
                            createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        }
                    }
                    progressivoItem++;
                } else {
                    // UD già presente nella richiesta, non la posso annullare due volte
                    AroItemRichAnnulVers item = createAroItemRichAnnulVers(rich, registro, anno, numero,
                            progressivoItem, CostantiDB.TipiEntitaSacer.UNI_DOC.name(),
                            CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                    String dsErr = "L'unit\u00E0 documentaria " + registro + "-" + annoInt + "-" + numero
                            + " \u00E8 gi\u00E0 presente nella richiesta";
                    createAroErrRichAnnulVers(item, BigDecimal.ONE,
                            CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_PRESENTE.name(), dsErr,
                            CostantiDB.TipoGravitaErrore.ERRORE.name());
                    progressivoItem++;
                }
            } else {
                // MEV#26446
                if (fascsInRich.add(new FascBean(idStrut, anno, numero))) {
                    AroItemRichAnnulVers item = createAroItemRichAnnulVersFasc(rich, anno, numero, progressivoItem);
                    Long idFasc = fascicoliHelper.getIdFascVersatoNoAnnul(idStrut, anno, numero);
                    logger.debug("Controlli item da annullare");
                    if (idFasc != null) {
                        // FASC esistente
                        FasFascicolo fasc = helper.findById(FasFascicolo.class, idFasc);
                        item.setFasFascicolo(fasc);
                        // Già qui, controllo il fascicolo definito nella richiesta e che sto trattando
                        controlloItemDaAnnullare(item, idUserIam);
                    } else {
                        // Se non esiste un fascicolo non annullato, allora controllo se esiste in generale
                        if (fascicoliHelper.existsFascicolo(idStrut, anno, numero)) {
                            // FASC esistente - creo record di errore
                            String dsErr = "Il fascicolo " + annoInt + "-" + numero
                                    + " \u00E8 gi\u00E0 stato annullato";
                            createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else {
                            // FASC non esistente - creo record di errore
                            String dsErr = "Il fascicolo " + annoInt + "-" + numero + " non esiste";
                            createAroErrRichAnnulVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        }
                    }
                    progressivoItem++;
                } else {
                    AroItemRichAnnulVers item = createAroItemRichAnnulVersFasc(rich, anno, numero, progressivoItem);
                    String dsErr = "Il fascicolo " + annoInt + "-" + numero
                            + " \u00E8 gi\u00E0 presente nella richiesta";
                    createAroErrRichAnnulVers(item, BigDecimal.ONE,
                            CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_PRESENTE.name(), dsErr,
                            CostantiDB.TipoGravitaErrore.ERRORE.name());
                    progressivoItem++;
                }
                // end MEV#26446
            }
            logger.debug(" {} - elaborazione [{}-{}-{}-{}] - FINE ***", progressivoItem,
                    versamentoDaAnnullare.getAnno(), versamentoDaAnnullare.getTipoVersamento(),
                    versamentoDaAnnullare.getTipoRegistro(), versamentoDaAnnullare.getNumero());

        }
        logger.info("handleXmlRecords - FINE");

    }

    /**
     * Controlli sull'item candidato ad essere annullato tenendo conto delle abilitazioni dell'utente passato in
     * ingresso
     *
     * @param item
     */
    private void controlloItemDaAnnullare(AroItemRichAnnulVers item, long idUserIam) {
        int progressivoErr = 1;
        AroUnitaDoc ud = item.getAroUnitaDoc();
        FasFascicolo fascicolo = item.getFasFascicolo();

        // Se item di tipo UNI_DOC e se è definito l'identificatore dell'unità doc
        if (ud != null && item.getTiItemRichAnnulVers().equals(CostantiDB.TipiEntitaSacer.UNI_DOC.name())) {

            // Controllo se esiste già un'altra richiesta, diversa da quella corrente, con
            // stato APERTA o CHIUSA che
            // contiene quella unita doc da annullare
            AroRichAnnulVers existingRich = helper.getAroRichAnnulVersContainingUd(ud.getIdUnitaDoc(),
                    item.getAroRichAnnulVers().getIdRichAnnulVers());
            if (existingRich != null) {
                // UD gi\u00E0 presente in un'altra richiesta diversa da quella corrente
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " \u00E8 in corso di annullamento nella richiesta " + existingRich.getCdRichAnnulVers();
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.ITEM_IN_CORSO_DI_ANNUL.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controllo se l'ud è usata come riferimento da almeno un componente
            if (compHelper.isAroUnitaDocReferredByOtherAroCompDocs(ud.getIdUnitaDoc(),
                    new BigDecimal(ud.getOrgStrut().getIdStrut()))) {
                // UD riferita da componenti
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " \u00E8 usata come riferimento da almeno un componente";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.ITEM_RIFERITO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            boolean forzaAnnul = item.getAroRichAnnulVers().getFlForzaAnnul().equals("1");
            if ((!forzaAnnul
                    && !ud.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE.name())
                    && !ud.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
                    && !ud.getTiStatoConservazione().equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())
                    && !ud.getTiStatoConservazione().equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name())
                    && !ud.getTiStatoConservazione()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name()))
                    || (forzaAnnul
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE.name())
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name())
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name())
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name())
                            && !ud.getTiStatoConservazione()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.IN_CUSTODIA.name()))) {
                // Stato conservazione errato
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha stato di conservazione pari a " + ud.getTiStatoConservazione()
                        + " e, quindi, non pu\u00F2 essere annullata";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.STATO_CONSERV_NON_AMMESSO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al
            // TIPO_UNITA_DOC, in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam, ud.getDecTipoUnitaDoc().getIdTipoUnitaDoc(),
                    Constants.TipoDato.TIPO_UNITA_DOC.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha tipo unit\u00E0 documentaria a cui l'utente non \u00E8 abilitato";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.TIPO_UNITA_DOC_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al REGISTRO,
            // in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam, ud.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    Constants.TipoDato.REGISTRO.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha registro a cui l'utente non \u00E8 abilitato";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.REGISTRO_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al TIPO_DOC
            // (tipo documento principale dell'ud), in caso negativo registro l'errore
            DecTipoDoc tipoDocPrinc = helper.getDecTipoDocPrincipale(ud.getIdUnitaDoc());
            if (!helper.isUserAbilitatoToTipoDato(idUserIam, tipoDocPrinc.getIdTipoDoc(),
                    Constants.TipoDato.TIPO_DOC.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha tipo documento principale a cui l'utente non \u00E8 abilitato";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.TIPO_DOC_PRINC_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'unità documentaria NON è stata versata mediante PreIngest, in
            // caso negativo registro il
            // warning
            if (helper.isUdFromPreIngest(ud.getIdUnitaDoc())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " dovrebbe essere annullata mediante annullamento dell'oggetto versato in PreIngest che la ha generata";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.DA_ANNULLARE_IN_PING.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.WARNING.name());
            }

            // Controlla se l'unità documentaria definita per item corrente non è annullata,
            // in caso negativo registro
            // l'errore
            if (helper.isUdAnnullata(ud.getIdUnitaDoc())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " \u00E8 gi\u00E0 stata annullata";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }
        } // Se item di tipo FASC e se è definito l'identificatore del fascicolo
        else if (fascicolo != null && item.getTiItemRichAnnulVers().equals(Constants.TipoEntitaSacer.FASC.name())) {
            // Controllo se esiste già un'altra richiesta, diversa da quella corrente, con
            // stato APERTA o CHIUSA che
            // contiene quel fascicolo
            AroRichAnnulVers existingRich = helper.getAroRichAnnulVersContainingFasc(fascicolo.getIdFascicolo(),
                    item.getAroRichAnnulVers().getIdRichAnnulVers());
            if (existingRich != null) {
                // Fascicolo gi\u00E0 presente in un'altra richiesta diversa da quella corrente
                String dsErr = "Il fascicolo " + fascicolo.getAaFascicolo() + "-" + fascicolo.getCdKeyFascicolo()
                        + " \u00E8 in corso di annullamento nella richiesta " + existingRich.getCdRichAnnulVers();
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.ITEM_IN_CORSO_DI_ANNUL.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            boolean forzaAnnul = item.getAroRichAnnulVers().getFlForzaAnnul().equals("1");
            if ((!forzaAnnul
                    && !fascicolo.getTiStatoConservazione().name()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
                    && !fascicolo.getTiStatoConservazione().name()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())
                    && !fascicolo.getTiStatoConservazione().name()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name()))
                    || (!fascicolo.getTiStatoConservazione().name()
                            .equals(CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
                            && !fascicolo.getTiStatoConservazione().name()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())
                            && !fascicolo.getTiStatoConservazione().name()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name())
                            && !fascicolo.getTiStatoConservazione().name()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name())
                            && !fascicolo.getTiStatoConservazione().name()
                                    .equals(CostantiDB.StatoConservazioneUnitaDoc.IN_CUSTODIA.name()))) {
                // Stato conservazione errato
                String dsErr = "Il fascicolo " + fascicolo.getAaFascicolo() + "-" + fascicolo.getCdKeyFascicolo()
                        + " ha stato di conservazione pari a " + fascicolo.getTiStatoConservazione()
                        + " e, quindi, non pu\u00F2 essere annullato";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.STATO_CONSERV_NON_AMMESSO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al
            // TIPO_FASCICOLO, in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam, fascicolo.getDecTipoFascicolo().getIdTipoFascicolo(),
                    Constants.TipoDato.TIPO_FASCICOLO.name())) {
                String dsErr = "Il fascicolo " + fascicolo.getAaFascicolo() + "-" + fascicolo.getCdKeyFascicolo()
                        + " ha tipo fascicolo a cui l'utente non \u00E8 abilitato";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.TIPO_FASCICOLO_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se il fascicolo definito per item corrente non è annullato, in caso
            // negativo registro l'errore
            if (helper.isFascicoloAnnullato(fascicolo.getIdFascicolo())) {
                String dsErr = "Il fascicolo " + fascicolo.getAaFascicolo() + "-" + fascicolo.getCdKeyFascicolo()
                        + " \u00E8 gi\u00E0 stato annullato";
                createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.ITEM_GIA_ANNULLATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }
        }

        // Se per l'item corrente non sono presenti errori con gravità ERRORE
        if (helper.getAroErrRichAnnulVersByGravity(item.getIdItemRichAnnulVers(),
                CostantiDB.TipoGravitaErrore.ERRORE.name()).isEmpty()) {
            item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_SACER.name());
        }

    }

    private void createAroErrRichAnnulVers(AroItemRichAnnulVers item, BigDecimal pgErr, String tiErr, String dsErr,
            String tiGravitaErr) {
        AroErrRichAnnulVers err = new AroErrRichAnnulVers();
        err.setPgErr(pgErr);
        err.setTiErr(tiErr);
        err.setDsErr(dsErr);
        err.setTiGravita(tiGravitaErr);
        item.addAroErrRichAnnulVers(err);
        helper.insertEntity(err, true);
    }

    private AroItemRichAnnulVers createAroItemRichAnnulVers(AroRichAnnulVers rich, String registro, BigDecimal anno,
            String numero, int progressivo) {
        // Dovrei aver ottenuto tutti i campi necessari per creare il nuovo record
        AroItemRichAnnulVers item = new AroItemRichAnnulVers();
        item.setCdRegistroKeyUnitaDoc(registro);
        item.setAaKeyUnitaDoc(anno);
        item.setCdKeyUnitaDoc(numero);
        item.setIdStrut(new BigDecimal(rich.getOrgStrut().getIdStrut()));
        item.setPgItemRichAnnulVers(new BigDecimal(progressivo));
        item.setTiItemRichAnnulVers(CostantiDB.TiItemRichAnnulVers.UNI_DOC.name());
        item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
        if (item.getAroErrRichAnnulVers() == null) {
            item.setAroErrRichAnnulVers(new ArrayList<>());
        }
        rich.addAroItemRichAnnulVers(item);
        helper.insertEntity(item, true);
        return item;
    }

    private AroItemRichAnnulVers createAroItemRichAnnulVersFasc(AroRichAnnulVers rich, BigDecimal anno, String numero,
            int progressivo) {
        // Dovrei aver ottenuto tutti i campi necessari per creare il nuovo record
        AroItemRichAnnulVers item = new AroItemRichAnnulVers();
        item.setAaFascicolo(anno);
        item.setCdKeyFascicolo(numero);
        item.setIdStrut(new BigDecimal(rich.getOrgStrut().getIdStrut()));
        item.setPgItemRichAnnulVers(new BigDecimal(progressivo));
        item.setTiItemRichAnnulVers(CostantiDB.TiItemRichAnnulVers.FASC.name());
        item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
        if (item.getAroErrRichAnnulVers() == null) {
            item.setAroErrRichAnnulVers(new ArrayList<>());
        }
        rich.addAroItemRichAnnulVers(item);
        helper.insertEntity(item, true);
        return item;
    }

    private AroItemRichAnnulVers createAroItemRichAnnulVers(AroRichAnnulVers rich, String registro, BigDecimal anno,
            String numero, int progressivo, String tiItemRichAnnulVers, String tiStatoItem) {
        // Dovrei aver ottenuto tutti i campi necessari per creare il nuovo record
        AroItemRichAnnulVers item = new AroItemRichAnnulVers();
        item.setCdRegistroKeyUnitaDoc(registro);
        item.setAaKeyUnitaDoc(anno);
        item.setCdKeyUnitaDoc(numero);
        item.setIdStrut(new BigDecimal(rich.getOrgStrut().getIdStrut()));
        item.setPgItemRichAnnulVers(new BigDecimal(progressivo));
        item.setTiItemRichAnnulVers(tiItemRichAnnulVers);
        item.setTiStatoItem(tiStatoItem);
        if (item.getAroErrRichAnnulVers() == null) {
            item.setAroErrRichAnnulVers(new ArrayList<>());
        }
        rich.addAroItemRichAnnulVers(item);
        helper.insertEntity(item, true);
        return item;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Annullamento versamenti">
    public void annullamentoVersamenti() {
        /*
         * Determino le richieste con stato CHIUSA per le quali non sia definito un item con stato DA_ANNULLARE_IN_PING
         * oppure le richieste con stato COMUNICATA A SACER, purché non immediate
         */
        List<AroRichAnnulVers> richiesteAnnullamento = helper.getRichiesteAnnullamentoVersamentoDaElab();
        logger.info("{} Sono state ricavate: {} richieste da elaborare", LOG_MESSAGE_ANNULLA_UD,
                richiesteAnnullamento.size());

        for (AroRichAnnulVers richiestaAnnullamento : richiesteAnnullamento) {
            // Determino l'utente che ha definito lo stato corrente della richiesta
            long idUserIam = getUserFirstStateRich(BigDecimal.valueOf(richiestaAnnullamento.getIdRichAnnulVers()));
            context.getBusinessObject(AnnulVersEjb.class)
                    .elaboraRichiestaAnnullamento(richiestaAnnullamento.getIdRichAnnulVers(), idUserIam);
        }

        /*
         * Scrivo in LogJob la fine corretta dell'esecuzione del job di Annullamento Versamenti Unit\u00E0 Documentarie
         */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        logger.info("{} Esecuzione job terminata con successo!", LOG_MESSAGE_ANNULLA_UD);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraRichiestaAnnullamento(long idRichAnnulVers, long idUserIam) {
        // Assumo lock esclusivo sulla richiesta
        AroRichAnnulVers richiestaAnnullamento = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        AroStatoRichAnnulVers statoRichAnnulVers = helper.findById(AroStatoRichAnnulVers.class,
                richiestaAnnullamento.getIdStatoRichAnnulVersCor());
        boolean richAnnulInPing = false;
        boolean proseguiAnnullamento = false;

        // Lock su item ud o item fascicolo
        if (richiestaAnnullamento.getTiRichAnnulVers().equals(CostantiDB.TiRichAnnulVers.UNITA_DOC.name())) {
            for (AroItemRichAnnulVers itemRichAnnulVers : richiestaAnnullamento.getAroItemRichAnnulVers()) {
                // Controllo che l'item esista per loccarlo
                if (itemRichAnnulVers.getAroUnitaDoc() != null) {
                    helper.findByIdWithLock(AroUnitaDoc.class, itemRichAnnulVers.getAroUnitaDoc().getIdUnitaDoc());
                }
            }
        } else if (richiestaAnnullamento.getTiRichAnnulVers().equals(CostantiDB.TiRichAnnulVers.FASCICOLI.name())) {
            for (AroItemRichAnnulVers itemRichAnnulVers : richiestaAnnullamento.getAroItemRichAnnulVers()) {
                // Controllo che l'item esista per loccarlo
                if (itemRichAnnulVers.getFasFascicolo() != null) {
                    helper.findByIdWithLock(FasFascicolo.class, itemRichAnnulVers.getFasFascicolo().getIdFascicolo());
                }
            }
        }

        // Se la richiesta ha stato corrente CHIUSA
        if (statoRichAnnulVers.getTiStatoRichAnnulVers().equals(CostantiDB.StatoRichAnnulVers.CHIUSA.name())) {
            proseguiAnnullamento = true;
            logger.debug("{} --- Annullamento Versamenti - Verifica della richiesta con stato CHIUSA",
                    AnnulVersEjb.class.getSimpleName());

            // Elimino tutti gli errori rilevati sugli item della richiesta, tranne quelli
            // di tipo ITEM_NON_ESISTE e
            // ITEM_GIA_PRESENTE e ITEM_GIA_ANNULLATO
            helper.deleteAroErrRichAnnulVers(idRichAnnulVers, CostantiDB.TipoErrRichAnnulVers.getStatiControlloItem());

            // Controllo gli item
            List<AroItemRichAnnulVers> itemRichAnnulVersList = richiestaAnnullamento.getAroItemRichAnnulVers();
            for (AroItemRichAnnulVers item : itemRichAnnulVersList) {
                // Assumo lock esclusivo sull'unità doc definita nell'item della richiesta
                item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());

                /*
                 * CONTROLLA ITEM DA ANNULLARE (senza cancellazione errori e assegnazione stato NON_ANNULLABILE)
                 */
                controlloItemDaAnnullare(item, idUserIam);

                // Se lo stato è DA_ANNULLARE_IN_PING
                if (item.getTiStatoItem().equals(CostantiDB.StatoItemRichAnnulVers.DA_ANNULLARE_IN_PING.name())) {
                    richAnnulInPing = true;
                }
            }
        }

        ////////////////////////
        // EVADO LA RICHIESTA //
        ////////////////////////
        // Se la richiesta era ancora chiusa (CONCORRENZA tra online e job) e nella
        //////////////////////// richiesta non ci sono item da
        //////////////////////// annullare in ping procedo
        if (proseguiAnnullamento && !richAnnulInPing) {
            context.getBusinessObject(AnnulVersEjb.class).evasioneRichiestaAnnullamento(richiestaAnnullamento,
                    idUserIam);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evasioneRichiestaAnnullamento(AroRichAnnulVers richiestaAnnullamento, long idUserIam) {
        richiestaAnnullamento = entityManager.find(AroRichAnnulVers.class, richiestaAnnullamento.getIdRichAnnulVers(),
                LockModeType.PESSIMISTIC_WRITE);
        if (richiestaAnnullamento.getTiRichAnnulVers().equals(CostantiDB.TiRichAnnulVers.UNITA_DOC.name())) {
            evadiAnnullamentoVersamentiUnitaDoc(richiestaAnnullamento, idUserIam);
        } else if (richiestaAnnullamento.getTiRichAnnulVers().equals(CostantiDB.TiRichAnnulVers.FASCICOLI.name())) {
            evadiAnnullamentoVersamentiFascicoli(richiestaAnnullamento, idUserIam);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evadiAnnullamentoVersamentiUnitaDoc(AroRichAnnulVers richiestaAnnullamento, long idUserIam) {
        long idRichAnnulVers = richiestaAnnullamento.getIdRichAnnulVers();
        logger.debug("{} Id richiesta annullamento versamento {}", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        AroStatoRichAnnulVers statoRichAnnulVers = helper.findById(AroStatoRichAnnulVers.class,
                richiestaAnnullamento.getIdStatoRichAnnulVersCor());
        logger.info("{} Evasione richiesta ID: {}", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        logger.debug("{} Procedo ad evadere la richiesta avente id:{}", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        logger.debug("{} tiStatoRichAnnulVers={} dtRegStatoRichAnnulVers={}", LOG_MESSAGE_ANNULLA_UD,
                statoRichAnnulVers.getTiStatoRichAnnulVers(), statoRichAnnulVers.getDtRegStatoRichAnnulVers());
        // Definisco come data di annullamento la data corrente
        Calendar cal = Calendar.getInstance();
        Date dataAnnullamento = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
        String dtString = df.format(dataAnnullamento);
        logger.debug("{} Gestione dei volumi di conservazione della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD,
                idRichAnnulVers);
        List<VolVLisVolumeUdAnnul> volumiList = helper.retrieveVolVLisVolumeUdAnnul(idRichAnnulVers);
        logger.debug("{} Trovati {} volumi", LOG_MESSAGE_ANNULLA_UD, volumiList.size());
        // Per ogni appartenenza dell'unit\u00E0 doc al volume
        for (VolVLisVolumeUdAnnul vols : volumiList) {
            logger.debug("{} Elaboro il volume con idRichAnnulVers={} idVolumeConserv={}", LOG_MESSAGE_ANNULLA_UD,
                    vols.getVolVLisVolumeUdAnnulId().getIdRichAnnulVers(),
                    vols.getVolVLisVolumeUdAnnulId().getIdVolumeConserv());
            VolVolumeConserv volume = helper.findById(VolVolumeConserv.class,
                    vols.getVolVLisVolumeUdAnnulId().getIdVolumeConserv());
            if (StringUtils.isBlank(vols.getNtVolumeChiuso()) || !vols.getNtVolumeChiuso().contains(
                    "Nel volume sono presenti unit\u00E0 documentarie di cui \u00E8 stato annullato il versamento")) {
                // Aggiorno la nota del volume
                volume.setNtVolumeChiuso(((StringUtils.isNotBlank(vols.getNtVolumeChiuso())
                        ? vols.getNtVolumeChiuso() + " " : "")
                        + "Nel volume sono presenti unit\u00E0 documentarie di cui \u00E8 stato annullato il versamento")
                                .trim());
            }
            /*
             * Determina le unità doc appartenenti al volume (sia come ud versate che come doc aggiunti) presenti come
             * item da annullare nella richiesta il sistema registra in VOL_VOLUME_VERS_UD_ANNUL l'insieme delle unità
             * doc in esso presenti ed annullate
             */
            List<VolVLisUdAnnulByVolume> uds = helper.retrieveVolVLisUdAnnulByVolume(idRichAnnulVers,
                    vols.getVolVLisVolumeUdAnnulId().getIdVolumeConserv().longValue());
            for (VolVLisUdAnnulByVolume udAnnulInVolume : uds) {
                VolVolumeVersUdAnnul volVolumeVersUdAnnul = new VolVolumeVersUdAnnul();
                AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, udAnnulInVolume.getIdUnitaDoc());
                volVolumeVersUdAnnul.setAroUnitaDoc(ud);
                volVolumeVersUdAnnul.setVolVolumeConserv(volume);
                volVolumeVersUdAnnul.setDsUrnUnitaDocAnnul(udAnnulInVolume.getDsUrnUnitaDocAnnul());
                helper.insertEntity(volVolumeVersUdAnnul, false);
            }

            for (VolAppartUnitaDocVolume volAppart : volume.getVolAppartUnitaDocVolumes()) {
                List<VolAppartDocVolume> appartDocVolumeList = volAppart.getVolAppartDocVolumes();
                for (VolAppartDocVolume appartDocVolume : appartDocVolumeList) {
                    appartDocVolume.setNtGenericheDoc(((StringUtils.isNotBlank(appartDocVolume.getNtGenericheDoc())
                            ? appartDocVolume.getNtGenericheDoc() + " " : "")
                            + "Versamento del documento annullato in data " + dtString).trim());
                }
            }
            logger.debug("{} Fine elaborazione del volume con idRichAnnulVers={} idVolumeConserv={}",
                    LOG_MESSAGE_ANNULLA_UD, vols.getVolVLisVolumeUdAnnulId().getIdRichAnnulVers(),
                    vols.getVolVLisVolumeUdAnnulId().getIdVolumeConserv());

        }
        logger.debug("{} Fine della gestione dei volumi di conservazione della richiesta avente id: {}",
                LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);

        logger.debug("{} Gestione degli item della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        /*
         * Ricavo gli item di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di
         * inclusione in un elenco vale IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, NON_SELEZ_SCHED
         */
        List<AroItemRichAnnulVers> aroItemRichAnnulVersList = helper.getItem(idRichAnnulVers,
                ElencoEnums.UdDocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name(),
                ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name(), ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        for (AroItemRichAnnulVers aroItemRichAnnulVers : aroItemRichAnnulVersList) {
            // Cancello l'ud dalla coda di costruzione elenchi
            logger.debug("{} AroItemRichAnnulVers con idItemRichAnnulVers={}", LOG_MESSAGE_ANNULLA_UD,
                    aroItemRichAnnulVers.getIdItemRichAnnulVers());
            logger.debug("{} Procedo alla cancellazione dalla coda di costruzione elenchi di idUnitaDoc={}",
                    LOG_MESSAGE_ANNULLA_UD, aroItemRichAnnulVers.getAroUnitaDoc().getIdUnitaDoc());
            helper.deleteElvUdVersDaElabElenco(aroItemRichAnnulVers.getAroUnitaDoc().getIdUnitaDoc());
            // Annullo lo stato relativo al processo di inclusione in un elenco dell'ud
            logger.debug("{} Metto a null tiStatoUdElencoVers di idItemRichAnnulVers={}", LOG_MESSAGE_ANNULLA_UD,
                    aroItemRichAnnulVers.getIdItemRichAnnulVers());
            aroItemRichAnnulVers.getAroUnitaDoc().setTiStatoUdElencoVers(null);
        }
        logger.debug("{} Gestione dei documenti aggiunti degli item della richiesta avente id: {}",
                LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        /*
         * Ricavo i documenti aggiunti degli item di tipo UNI_DOC (quindi sto considerando l'item essere un'unit\u00E0
         * doc e ne cerco i documenti aggiunti) con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di
         * inclusione in un elenco vale IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, NON_SELEZ_SCHED
         */
        List<AroDoc> docList = helper.getDocAggiunti(idRichAnnulVers,
                ElencoEnums.UdDocStatusEnum.IN_ATTESA_MEMORIZZAZIONE.name(),
                ElencoEnums.UdDocStatusEnum.IN_ATTESA_SCHED.name(), ElencoEnums.UdDocStatusEnum.NON_SELEZ_SCHED.name());
        logger.debug("{} - trovati {} AroDoc di documenti aggiunti ", LOG_MESSAGE_ANNULLA_UD, docList.size());
        for (AroDoc doc : docList) {
            // Cancello i documenti aggiunti alla coda di costruzione elenchi
            logger.debug("{} AroDoc con idDoc={}", LOG_MESSAGE_ANNULLA_UD, doc.getIdDoc());
            logger.debug("{} Procedo alla cancellazione dalla coda di costruzione elenchi di idDoc={}",
                    LOG_MESSAGE_ANNULLA_UD, doc.getIdDoc());
            helper.deleteElvDocAggDaElabElenco(doc.getIdDoc());
            // Annullo lo stato relativo al processo di inclusione in un elenco del
            // documento
            logger.debug("{} Metto a null tiStatoUdElencoVers idDoc={}", LOG_MESSAGE_ANNULLA_UD, doc.getIdDoc());
            doc.setTiStatoDocElencoVers(null);
        }

        logger.debug(
                "{} - Annullamento Aggiornamenti Versamenti Unit\u00E0 Documentarie - Gestione degli item della richiesta avente id: {}",
                LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);

        /*
         * il sistema determina gli aggiornamenti unità doc degli item (della richiesta corrente) di tipo UNI_DOC con
         * stato DA_ANNULLARE_IN_SACER, il cui stato relativo al processo di inclusione in un elenco vale
         * IN_ATTESA_SCHED, NON_SELEZ_SCHED
         */
        List<AroUpdUnitaDoc> aroUpdUnitaDocList = helper.getUpdItem(idRichAnnulVers,
                AroUpdUDTiStatoUpdElencoVers.IN_ATTESA_SCHED, AroUpdUDTiStatoUpdElencoVers.NON_SELEZ_SCHED);
        logger.debug("{} - trovati {} AroUpdUnitaDoc", LOG_MESSAGE_ANNULLA_UD, aroUpdUnitaDocList.size());
        for (AroUpdUnitaDoc aroUpdUnitaDoc : aroUpdUnitaDocList) {
            logger.debug("{} AroUpdUnitaDoc con idUpdUnitaDoc={}", LOG_MESSAGE_ANNULLA_UD,
                    aroUpdUnitaDoc.getIdUpdUnitaDoc());
            // Cancello l'ud dalla coda di costruzione elenchi
            logger.debug("{} Procedo alla cancellazione dalla coda di costruzione elenchi di idUpdUnitaDoc={}",
                    LOG_MESSAGE_ANNULLA_UD, aroUpdUnitaDoc.getIdUpdUnitaDoc());
            helper.deleteElvUpdUdDaElabElenco(aroUpdUnitaDoc.getIdUpdUnitaDoc());
            // Annullo lo stato relativo al processo di inclusione in un elenco dell'ud
            logger.debug("{} Metto a null tiStatoUpdElencoVers idUpdUnitaDoc={}", LOG_MESSAGE_ANNULLA_UD,
                    aroUpdUnitaDoc.getIdUpdUnitaDoc());
            aroUpdUnitaDoc.setTiStatoUpdElencoVers(null);
        }

        logger.debug("{} - Gestione degli item della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        /*
         * Ricavo gli item di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di
         * inclusione in un elenco vale IN_ELENCO_APERTO o IN_ELENCO_DA_CHIUDERE
         */
        List<AroItemRichAnnulVers> item2List = helper.getItem(idRichAnnulVers,
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_APERTO.name(),
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
        Set<Long> idElencoVersApertiODaChiudereSet = new HashSet<>();
        logger.debug("{} - trovati {} AroItemRichAnnulVers", LOG_MESSAGE_ANNULLA_UD, item2List.size());
        for (AroItemRichAnnulVers aroItemRichAnnulVers : item2List) {
            logger.debug("{} - AroItemRichAnnulVers con idItemRichAnnulVers={}", LOG_MESSAGE_ANNULLA_UD,
                    aroItemRichAnnulVers.getIdItemRichAnnulVers());
            // Registro l'elenco di appartenenza in una lista elenchi
            if (aroItemRichAnnulVers.getAroUnitaDoc().getElvElencoVer() == null) {
                logger.warn(
                        "{} - AroItemRichAnnulVers idItemRichAnnulVers={} ha idElvElencoVer nullo e tiStatoUdElencoVers={}, lo escludo dall'elaborazione",
                        LOG_MESSAGE_ANNULLA_UD, aroItemRichAnnulVers.getIdItemRichAnnulVers(),
                        aroItemRichAnnulVers.getAroUnitaDoc().getTiStatoUdElencoVers());
            } else {
                idElencoVersApertiODaChiudereSet
                        .add(aroItemRichAnnulVers.getAroUnitaDoc().getElvElencoVer().getIdElencoVers());
                // Cancello l'ud dall'elenco (annullo la FK verso l'elenco) ed annullo lo stato
                // relativo al processo di
                // inclusione in un elenco dell'unit\u00E0 doc.
                logger.debug("{} - cancello idUnitaDoc {} dall'elenco: metto a null ElvElencoVer e tiStatoUdElencoVers",
                        LOG_MESSAGE_ANNULLA_UD, aroItemRichAnnulVers.getAroUnitaDoc().getIdUnitaDoc());
                aroItemRichAnnulVers.getAroUnitaDoc().setElvElencoVer(null);
                aroItemRichAnnulVers.getAroUnitaDoc().setTiStatoUdElencoVers(null);
            }
        }
        logger.debug("{} - Gestione dei documenti aggiunti degli item della richiesta avente id: {}",
                LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        /*
         * Ricavo i documenti aggiunti degli item di tipo UNI_DOC (quindi sto considerando l'item essere un'unit\u00E0
         * doc e ne cerco i documenti aggiunti) con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di
         * inclusione in un elenco vale IN_ELENCO_APÈRTO
         */
        docList = helper.getDocAggiunti(idRichAnnulVers, ElencoEnums.DocStatusEnum.IN_ELENCO_APERTO.name(),
                ElencoEnums.DocStatusEnum.IN_ELENCO_DA_CHIUDERE.name());
        for (AroDoc doc : docList) {
            idElencoVersApertiODaChiudereSet.add(doc.getElvElencoVer().getIdElencoVers());
            // Cancello il doc dall'elenco (annullo la FK verso l'elenco) ed annullo lo
            // stato relativo al processo di
            // inclusione in un elenco del documento.
            doc.setElvElencoVer(null);
            doc.setTiStatoDocElencoVers(null);
        }

        logger.debug(
                "{} - Annullamento Aggiornamenti Versamenti Unit\u00E0 Documentarie - Gestione degli item aggiornati della richiesta avente id: {}",
                LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        /*
         * Il sistema determina gli aggiornamenti unità doc degli item (della richiesta corrente) di tipo UNI_DOC con
         * stato DA_ANNULLARE_IN_SACER, il cui stato relativo al processo di inclusione in un elenco vale
         * IN_ELENCO_APERTO o IN_ELENCO_DA_CHIUDERE
         */
        List<AroUpdUnitaDoc> itemUpd2List = helper.getUpdItem(idRichAnnulVers,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_APERTO, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_DA_CHIUDERE);
        for (AroUpdUnitaDoc item2 : itemUpd2List) {
            idElencoVersApertiODaChiudereSet.add(item2.getElvElencoVer().getIdElencoVers());
            // Cancello il doc dall'elenco (annullo la FK verso l'elenco) ed annullo lo
            // stato relativo al processo di
            // inclusione in un elenco del documento.
            item2.setElvElencoVer(null);
            item2.setTiStatoUpdElencoVers(null);
        }

        logger.debug("{} - Ricalcolo totali elenco della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD,
                idRichAnnulVers);
        // Per ogni elenco con stato APERTO definito nella lista, ricalcolo tutto
        for (Long idElencoVersAperto : idElencoVersApertiODaChiudereSet) {
            context.getBusinessObject(AnnulVersEjb.class).updateTotaliElenco(idElencoVersAperto,
                    statoRichAnnulVers.getIamUser().getIdUserIam(), dtString);
        }

        logger.debug("{} - Gestione elenchi della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        /*
         * Il sistema determina gli elenchi con stato = CHIUSO o VALIDATO a cui appartengono gli item (della richiesta
         * corrente) di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER, il cui stato relativo al processo di inclusione in
         * un elenco vale IN_ELENCO_CHIUSO o IN_ELENCO_VALIDATO, uniti agli elenchi a cui appartengono i documenti
         * aggiunti degli item (della richiesta corrente) di tipo UNI_DOC con stato DA_ANNULLARE_IN_SACER, il cui stato
         * relativo al processo di inclusione in un elenco vale IN_ELENCO_CHIUSO o IN_ELENCO_VALIDATO
         */
        List<ElvVLisElencoUdAnnul> elenchiItem = helper.retrieveElvVLisElencoUdAnnuls(idRichAnnulVers);
        for (ElvVLisElencoUdAnnul elencoItem : elenchiItem) {
            ElvElencoVer elenco = helper.findById(ElvElencoVer.class,
                    elencoItem.getElvVLisElencoUdAnnulId().getIdElencoVers());
            if (StringUtils.isBlank(elencoItem.getNtElencoChiuso()) || !elencoItem.getNtElencoChiuso().contains(
                    "Nell'elenco sono presenti unit\u00E0 documentarie o documenti aggiunti di cui \u00E8 stato annullato il versamento")) {
                elenco.setNtElencoChiuso(((StringUtils.isNotBlank(elencoItem.getNtElencoChiuso())
                        ? elencoItem.getNtElencoChiuso() + " " : "")
                        + "Nell'elenco sono presenti unit\u00E0 documentarie o documenti aggiunti di cui \u00E8 stato annullato il versamento")
                                .trim());
            }
            // il sistema registra in ELV_ELENCO_VERS_UD_ANNUL l'insieme delle unità doc in
            // esso presenti ed annullate
            List<ElvVLisUdAnnulByElenco> uds = helper.retrieveElvVLisUdAnnulByElenco(idRichAnnulVers,
                    elencoItem.getElvVLisElencoUdAnnulId().getIdElencoVers().longValue());
            for (ElvVLisUdAnnulByElenco udAnnulInElenco : uds) {
                ElvElencoVersUdAnnul elencoVersUdAnnul = new ElvElencoVersUdAnnul();
                AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, udAnnulInElenco.getIdUnitaDoc());
                elencoVersUdAnnul.setAroUnitaDoc(ud);
                elencoVersUdAnnul.setElvElencoVer(elenco);
                elencoVersUdAnnul.setDsUrnUnitaDocAnnul(udAnnulInElenco.getDsUrnUnitaDocAnnul());
                helper.insertEntity(elencoVersUdAnnul, false);
            }
        }

        logger.debug("{} - Modifica ud, doc e collegamenti della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD,
                idRichAnnulVers);

        // MEV #31162
        List<Long> idUnitaDocList = helper.getUnitaDocumentarieItem(idRichAnnulVers);
        // end MEV #31162

        // Modifico le ud corrispondenti agli item
        helper.updateUnitaDocumentarieItem(idRichAnnulVers, dataAnnullamento,
                CostantiDB.TipoAnnullamentoUnitaDoc.ANNULLAMENTO.name(),
                CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(), richiestaAnnullamento.getNtRichAnnulVers());

        // MEV #31162
        IamUser utente = helper.findById(IamUser.class, idUserIam);
        for (Long idUnitaDoc : idUnitaDocList) {
            udEjb.insertLogStatoConservUd(idUnitaDoc, utente.getNmUserid(),
                    Constants.EVASIONE_RICHIESTA_ANNULLAMENTO_UD,
                    CostantiDB.StatoConservazioneUnitaDoc.ANNULLATA.name(), Constants.ANNULLAMENTO_ONLINE);
        }
        // end MEV #31162

        // Modifico le upd corrispondenti agli item
        helper.updateUpdUnitaDocumentarieItem(idRichAnnulVers, dataAnnullamento,
                richiestaAnnullamento.getNtRichAnnulVers());
        // Modifico i collegamenti alle unita doc corrispondenti agli item
        helper.updateCollegamentiUd(idRichAnnulVers);
        // Modifico i documenti delle unit\u00E0 doc corrispondenti agli item
        helper.updateDocumentiUdItem(idRichAnnulVers, dataAnnullamento,
                CostantiDB.TipoAnnullamentoUnitaDoc.ANNULLAMENTO.name(), richiestaAnnullamento.getNtRichAnnulVers());

        logger.debug("{} Registro il nuovo stato della richiesta avente id: {}", LOG_MESSAGE_ANNULLA_UD,
                idRichAnnulVers);
        // Registra il nuovo stato della richiesta
        TypedQuery<AroStatoRichAnnulVers> query = entityManager.createQuery(
                "SELECT a FROM AroStatoRichAnnulVers a WHERE a.aroRichAnnulVers = :aroRichAnnulVers",
                AroStatoRichAnnulVers.class);
        query.setParameter("aroRichAnnulVers", richiestaAnnullamento);
        richiestaAnnullamento.setAroStatoRichAnnulVers(query.getResultList());
        AroStatoRichAnnulVers statoRichAnnulVersNew = context.getBusinessObject(AnnulVersEjb.class)
                .createAroStatoRichAnnulVers(richiestaAnnullamento, CostantiDB.StatoRichAnnulVers.EVASA.name(),
                        Calendar.getInstance().getTime(), null, statoRichAnnulVers.getIamUser());
        helper.insertEntity(statoRichAnnulVersNew, false);
        // Aggiorno l'identificatore dello stato corrente della richiesta assegnando
        // l'identificatore dello stato
        // inserito
        richiestaAnnullamento
                .setIdStatoRichAnnulVersCor(new BigDecimal(statoRichAnnulVersNew.getIdStatoRichAnnulVers()));
        /*
         * Aggiorna la versione serie ( con stato = VALIDATA o DA_FIRMARE o FIRMATA o IN_CUSTODIA nel cui contenuto
         * effettivo ci siano ud annullate degli item)settando l'indicatore che segnala che la serie deve essere
         * ricalcolata a causa di annullamento di almeno una unità documentaria
         */
        List<BigDecimal> idVerSeries = serieHelper.retrieveSerVLisVerserByRichann(idRichAnnulVers);
        for (BigDecimal idVerSerie : idVerSeries) {
            SerVerSerie verSerie = helper.findById(SerVerSerie.class, idVerSerie);
            verSerie.setFlUpdAnnulUnitaDoc("1");
        }
        /*
         * Aggiorna il fascicolo settando l’indicatore che segnala che il fascicolo deve essere aggiornato a causa di
         * annullamento di almeno una unità documentaria
         */
        List<BigDecimal> idFascicoli = fascicoliHelper.retrieveFasVLisFascByRichann(idRichAnnulVers);
        for (BigDecimal idFascicolo : idFascicoli) {
            FasFascicolo fascicolo = helper.findById(FasFascicolo.class, idFascicolo);
            fascicolo.setFlUpdAnnulUnitaDoc("1");
        }
        // Modifico gli item assegnando stato ANNULLATA
        helper.updateStatoItemList(idRichAnnulVers, CostantiDB.StatoItemRichAnnulVers.ANNULLATO.name());

        logger.debug("{} - richiesta avente id: {} elaborata con successo!", LOG_MESSAGE_ANNULLA_UD, idRichAnnulVers);
        logger.info("Fine evasione richiesta ID: {}", idRichAnnulVers);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evadiAnnullamentoVersamentiFascicoli(AroRichAnnulVers richiestaAnnullamento, long idUserIam) {
        long idRichAnnulVers = richiestaAnnullamento.getIdRichAnnulVers();
        AroStatoRichAnnulVers statoRichAnnulVers = helper.findById(AroStatoRichAnnulVers.class,
                richiestaAnnullamento.getIdStatoRichAnnulVersCor());
        logger.info("Evasione richiesta ID: {}", idRichAnnulVers);
        logger.debug("Annullamento Versamenti Fascicoli - Procedo ad evadere la richiesta avente id: {}",
                idRichAnnulVers);
        // Definisco come data di annullamento la data corrente
        Calendar cal = Calendar.getInstance();
        Date dataAnnullamento = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
        String dtString = df.format(dataAnnullamento);

        logger.debug("Annullamento Versamenti Fascicoli - Gestione degli item della richiesta avente id: {}",
                idRichAnnulVers);
        /*
         * Ricavo gli item (della richiesta corrente) di tipo FASC con stato DA_ANNULLARE_IN_SACER, il cui stato
         * relativo al processo di inclusione in un elenco vale IN_ATTESA_SCHED, NON_SELEZ_SCHED
         */
        List<AroItemRichAnnulVers> item1List = helper.getItemFasc(idRichAnnulVers,
                TiStatoFascElencoVers.IN_ATTESA_SCHED, TiStatoFascElencoVers.NON_SELEZ_SCHED);
        for (AroItemRichAnnulVers item1 : item1List) {
            // Cancello il fascicolo dalla coda di costruzione elenchi
            helper.deleteElvFascDaElabElenco(item1.getFasFascicolo().getIdFascicolo());
            // Annullo lo stato relativo al processo di inclusione in un elenco del
            // fascicolo
            item1.getFasFascicolo().setTiStatoFascElencoVers(null);
        }

        logger.debug("Annullamento Versamenti Fascicoli - Gestione degli item della richiesta avente id: {}",
                idRichAnnulVers);
        /*
         * Ricavo gli item di tipo FASC con stato DA_ANNULLARE_IN_SACER il cui stato relativo al processo di inclusione
         * in un elenco vale IN_ELENCO_APERTO o IN_ELENCO_DA_CHIUDERE
         */
        List<AroItemRichAnnulVers> item2List = helper.getItemFasc(idRichAnnulVers,
                TiStatoFascElencoVers.IN_ELENCO_APERTO, TiStatoFascElencoVers.IN_ELENCO_DA_CHIUDERE);
        Set<Long> idElencoVersFascApertiODaChiudereSet = new HashSet<>();
        for (AroItemRichAnnulVers item2 : item2List) {
            // Registro l'elenco di appartenenza in una lista elenchi
            idElencoVersFascApertiODaChiudereSet
                    .add(item2.getFasFascicolo().getElvElencoVersFasc().getIdElencoVersFasc());
            // Cancello il fascicolo dall'elenco (annullo la FK verso l'elenco) ed annullo
            // lo stato relativo al processo
            // di inclusione in un elenco del fascicolo.
            item2.getFasFascicolo().setElvElencoVersFasc(null);
            item2.getFasFascicolo().setTiStatoFascElencoVers(null);
        }

        logger.debug("Annullamento Versamenti Fascicoli - Ricalcolo totali elenco della richiesta avente id: {}",
                idRichAnnulVers);
        // Per ogni elenco con stato APERTO o DA_CHIUDERE definito nella lista,
        // ricalcolo tutto
        for (Long idElencoVersFascAperto : idElencoVersFascApertiODaChiudereSet) {
            context.getBusinessObject(AnnulVersEjb.class).updateTotaliElencoFasc(idElencoVersFascAperto,
                    statoRichAnnulVers.getIamUser().getIdUserIam(), dtString);
        }

        logger.debug("Annullamento Versamenti Fascicoli - Gestione elenchi della richiesta avente id: {} ",
                idRichAnnulVers);
        /*
         * Il sistema determina gli elenchi con stato = CHIUSO o FIRMA_IN_CORSO o FIRMATO o IN_CODA_CREAZIONE_AIP o
         * AIP_CREATI o ELENCO_INDICI_AIP_CREATO o ELENCO_INDICI_AIP_FIRMA_IN_CORSO o COMPLETATO, a cui appartengono gli
         * item (della richiesta corrente) di tipo FASC con stato DA_ANNULLARE_IN_SACER, il cui stato relativo al
         * processo di inclusione in un elenco vale IN_ELENCO_CHIUSO o IN_ELENCO_FIRMATO o
         * IN_ELENCO_IN_CODA_CREAZIONE_AIP o IN_ELENCO_CON_AIP_CREATO o IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO o
         * IN_ELENCO_COMPLETATO (vedi vista ELV_V_LIS_ELENCO_FASC_ANNUL)
         */
        List<ElvVLisElencoFascAnnul> elenchiItem = helper.retrieveElvVLisElencoFascAnnul(idRichAnnulVers);
        for (ElvVLisElencoFascAnnul elencoItem : elenchiItem) {
            ElvElencoVersFasc elenco = helper.findById(ElvElencoVersFasc.class,
                    elencoItem.getElvVLisElencoFascAnnulId().getIdElencoVersFasc());

            if (StringUtils.isBlank(elencoItem.getNtElencoChiuso()) || !elencoItem.getNtElencoChiuso()
                    .contains("Nell'elenco sono presenti fascicoli di cui \u00E8 stato annullato il versamento")) {
                elenco.setNtElencoChiuso(((StringUtils.isNotBlank(elencoItem.getNtElencoChiuso())
                        ? elencoItem.getNtElencoChiuso() + " " : "")
                        + "Nell'elenco sono presenti fascicoli di cui \u00E8 stato annullato il versamento").trim());
            }

            // Il sistema registra in ELV_ELENCO_VERS_FASC_ANNUL l’insieme dei fascicoli in
            // esso presenti ed annullati
            List<ElvVLisFascAnnulByElenco> fascAnnulInElenco = helper.retrieveElvVLisFascAnnulByElenco(idRichAnnulVers,
                    elencoItem.getElvVLisElencoFascAnnulId().getIdElencoVersFasc().longValue());
            for (ElvVLisFascAnnulByElenco fasc : fascAnnulInElenco) {
                ElvElencoVersFascAnnul elencoVersFascAnnul = new ElvElencoVersFascAnnul();
                FasFascicolo fascicolo = helper.findById(FasFascicolo.class, fasc.getIdFascicolo());
                elencoVersFascAnnul.setFasFascicolo(fascicolo);
                elencoVersFascAnnul.setElvElencoVersFasc(elenco);
                elencoVersFascAnnul.setDsUrnFascicoloAnnul(fasc.getDsUrnFascicoloAnnul());
                helper.insertEntity(elencoVersFascAnnul, false);
            }
        }

        /*
         * Il sistema registra un record in FAS_STATO_CONSERV_FASCICOLO per ogni fascicolo corrispondente agli item
         * (della richiesta corrente) con stato = DA_ANNULLARE_IN_SACER
         */
        List<AroItemRichAnnulVers> item3List = helper.getItemFasc(idRichAnnulVers);
        for (AroItemRichAnnulVers item : item3List) {
            context.getBusinessObject(AnnulVersEjb.class).createFasStatoConservFascicolo(item.getFasFascicolo(),
                    dataAnnullamento,
                    it.eng.parer.entity.constraint.FasStatoConservFascicolo.TiStatoConservazione.ANNULLATO,
                    statoRichAnnulVers.getIamUser());
        }

        /*
         * Modifico i fascicoli corrispondenti agli item (della richiesta corrente) con stato = DA_ANNULLARE_IN_SACER,
         * assegnando data di annullamento, stato conservazione = ANNULLATA, note su annullamento = causale definita
         * dalla richiesta
         */
        logger.debug(
                "Annullamento Versamenti Fascicoli - Modifica fascicoli e collegamenti della richiesta avente id: {}",
                idRichAnnulVers);
        // Modifico i fascicoli corrispondenti agli item
        helper.updateFascicoliItem(idRichAnnulVers, dataAnnullamento,
                it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione.ANNULLATO,
                richiestaAnnullamento.getNtRichAnnulVers());
        // Modifico i collegamenti ai fascicoli corrispondenti agli item
        helper.updateCollegamentiFasc(idRichAnnulVers);
        // MAC#22156
        logger.debug(
                "Annullamento Versamenti Fascicoli - Aggiorna le unit\u00E0 doc appartenenti ai fascicoli della richiesta, assegnando stato = AIP_GENERATO o AIP_FIRMATO, purch\u00E8 tali unit\u00E0 doc non appartengano ad altro fascicolo con stato = VERSAMENTO_IN_ARCHIVIO o IN_ARCHIVIO");

        // MEV #31162
        List<Long> idUnitaDocListAipGenerato = helper.getAroUnitaDocWithoutOtherFascicolos(idRichAnnulVers,
                Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
                        CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name()),
                "");

        List<Long> idUnitaDocListAipFirmato = helper.getAroUnitaDocWithoutOtherFascicolos(idRichAnnulVers,
                Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
                        CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name()),
                "NOT");
        // end MEV #31162

        helper.updateStatoConsAipGeneratoAroUnitaDocWithoutOtherFascicolos(idRichAnnulVers,
                Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
                        CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name()));
        helper.updateStatoConsAipFirmatoAroUnitaDocWithoutOtherFascicolos(idRichAnnulVers,
                Arrays.asList(CostantiDB.StatoConservazioneUnitaDoc.VERSAMENTO_IN_ARCHIVIO.name(),
                        CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name()));
        // end MAC#22156

        // MEV #31162
        IamUser utente = helper.findById(IamUser.class, idUserIam);
        for (Long idUnitaDoc : idUnitaDocListAipGenerato) {
            udEjb.insertLogStatoConservUd(idUnitaDoc, utente.getNmUserid(),
                    Constants.EVASIONE_RICHIESTA_ANNULLAMENTO_FASC,
                    CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name(), Constants.WS_ANNULLAMENTO);
        }

        for (Long idUnitaDoc : idUnitaDocListAipFirmato) {
            udEjb.insertLogStatoConservUd(idUnitaDoc, utente.getNmUserid(),
                    Constants.EVASIONE_RICHIESTA_ANNULLAMENTO_FASC,
                    CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name(), Constants.WS_ANNULLAMENTO);
        }
        // end MEV #31162

        logger.debug("Annullamento Versamenti Fascicoli - Registro il nuovo stato della richiesta avente id: {}",
                idRichAnnulVers);
        // Registra il nuovo stato della richiesta
        AroStatoRichAnnulVers statoRichAnnulVersNew = context.getBusinessObject(AnnulVersEjb.class)
                .createAroStatoRichAnnulVers(richiestaAnnullamento, CostantiDB.StatoRichAnnulVers.EVASA.name(),
                        Calendar.getInstance().getTime(), null, statoRichAnnulVers.getIamUser());
        helper.insertEntity(statoRichAnnulVersNew, false);
        // Aggiorno l'identificatore dello stato corrente della richiesta assegnando
        // l'identificatore dello stato
        // inserito
        richiestaAnnullamento
                .setIdStatoRichAnnulVersCor(new BigDecimal(statoRichAnnulVersNew.getIdStatoRichAnnulVers()));

        // Modifico gli item assegnando stato ANNULLATO
        helper.updateStatoItemList(idRichAnnulVers, CostantiDB.StatoItemRichAnnulVers.ANNULLATO.name());

        logger.debug("Annullamento Versamenti Fascicoli - richiesta avente id: {} elaborata con successo!",
                idRichAnnulVers);
        logger.info("Fine evasione richiesta ID: {}", idRichAnnulVers);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateTotaliElenco(long idElencoVers, long idUserIam, String dtString) {
        ElvElencoVer elenco = helper.findById(ElvElencoVer.class, idElencoVers);

        // Ricalcolo alcuni totali dell'elenco soggetti a verifiche
        long niUnitaDocVersElenco = evHelper.contaUdVersate(idElencoVers);
        long niDocAggElenco = evHelper.contaDocAggiunti(idElencoVers);
        long niUpdUnitaDoc = evHelper.contaUpdUd(idElencoVers);

        // Se questi tre totali sono a 0, cancello l'elenco e lo scrivo nel log
        if (niUnitaDocVersElenco == 0 && niDocAggElenco == 0 && niUpdUnitaDoc == 0) {
            /* Scrivo nel log l'avvenuta cancellazione */
            evHelper.writeLogElencoVers(elenco, elenco.getOrgStrut(), idUserIam,
                    ElencoEnums.OpTypeEnum.ELIMINA_ELENCO.name());

            /* Cancello l'elenco di versamento corrente */
            evHelper.deleteElvElencoVer(new BigDecimal(idElencoVers));

        } // altrimenti aggiorno i conteggi dell'elenco
        else {
            elenco.setNiUnitaDocVersElenco(new BigDecimal(niUnitaDocVersElenco));
            elenco.setNiDocVersElenco(new BigDecimal(evHelper.contaDocVersati(idElencoVers)));
            Object[] objVers = evHelper.contaCompVersati(idElencoVers);
            elenco.setNiCompVersElenco(new BigDecimal((Long) objVers[0]));
            elenco.setNiSizeVersElenco(objVers[1] != null ? (BigDecimal) objVers[1] : BigDecimal.ZERO);
            elenco.setNiUnitaDocModElenco(new BigDecimal(evHelper.contaUdModificatePerDocAggiunti(idElencoVers)));
            elenco.setNiDocAggElenco(new BigDecimal(niDocAggElenco));
            Object[] objAgg = evHelper.contaCompPerDocAggiunti(idElencoVers);
            elenco.setNiCompAggElenco(new BigDecimal((Long) objAgg[0]));
            elenco.setNiSizeAggElenco(objAgg[1] != null ? (BigDecimal) objAgg[1] : BigDecimal.ZERO);
        }

    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void updateTotaliElencoFasc(long idElencoVersFasc, long idUserIam, String dtString) {
        ElvElencoVersFasc elenco = helper.findById(ElvElencoVersFasc.class, idElencoVersFasc);
        elenco.setNiFascVersElenco(new BigDecimal(evHelper.contaFascVersati(idElencoVersFasc)));

        if (elenco.getNiFascVersElenco().compareTo(BigDecimal.ZERO) == 0) {
            /* Cancello l'elenco di versamento corrente */
            evHelper.deleteElvElencoVersFasc(new BigDecimal(idElencoVersFasc));
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroStatoRichAnnulVers createAroStatoRichAnnulVers(AroRichAnnulVers richAnnulVers,
            String tiStatoRichAnnulVers, Date dtRegStatoRichAnnulVers, String dsNotaRichAnnulVers, IamUser iamUser) {
        AroStatoRichAnnulVers statoRichAnnulVers = new AroStatoRichAnnulVers();
        statoRichAnnulVers.setPgStatoRichAnnulVers(
                helper.getUltimoProgressivoStatoRichiesta(richAnnulVers.getIdRichAnnulVers()).add(BigDecimal.ONE));
        statoRichAnnulVers.setTiStatoRichAnnulVers(tiStatoRichAnnulVers);
        statoRichAnnulVers.setDtRegStatoRichAnnulVers(dtRegStatoRichAnnulVers);
        statoRichAnnulVers.setDsNotaRichAnnulVers(dsNotaRichAnnulVers);
        statoRichAnnulVers.setIamUser(iamUser);
        richAnnulVers.addAroStatoRichAnnulVers(statoRichAnnulVers);
        return statoRichAnnulVers;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroStatoRichAnnulVers insertAroStatoRichAnnulVers(AroRichAnnulVers richAnnulVers,
            String tiStatoRichAnnulVers, Date dtRegStatoRichAnnulVers, String dsNotaRichAnnulVers, long idUser) {
        AroStatoRichAnnulVers statoRichAnnulVers = new AroStatoRichAnnulVers();
        logger.info("Eseguo il salvataggio dello stato richiesta annullamento pari a {}", tiStatoRichAnnulVers);
        statoRichAnnulVers.setPgStatoRichAnnulVers(
                helper.getUltimoProgressivoStatoRichiesta(richAnnulVers.getIdRichAnnulVers()).add(BigDecimal.ONE));
        statoRichAnnulVers.setTiStatoRichAnnulVers(tiStatoRichAnnulVers);
        statoRichAnnulVers.setDtRegStatoRichAnnulVers(dtRegStatoRichAnnulVers);
        statoRichAnnulVers.setDsNotaRichAnnulVers(dsNotaRichAnnulVers);
        statoRichAnnulVers.setIamUser(helper.findById(IamUser.class, idUser));
        richAnnulVers.addAroStatoRichAnnulVers(statoRichAnnulVers);
        helper.insertEntity(statoRichAnnulVers, true);
        richAnnulVers.setIdStatoRichAnnulVersCor(BigDecimal.valueOf(statoRichAnnulVers.getIdStatoRichAnnulVers()));
        helper.mergeEntity(richAnnulVers);
        helper.getEntityManager().flush();
        return statoRichAnnulVers;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroXmlRichAnnulVers createAroXmlRichAnnulVers(AroRichAnnulVers richAnnulVers, String tiXmlRichAnnulVers,
            String blXmlRichAnnulVers, String cdVersioneXml) {
        AroXmlRichAnnulVers xmlRichAnnulVers = new AroXmlRichAnnulVers();
        logger.info("Eseguo il salvataggio dell'xml " + tiXmlRichAnnulVers + " annullamento");
        xmlRichAnnulVers.setTiXmlRichAnnulVers(tiXmlRichAnnulVers);
        xmlRichAnnulVers.setBlXmlRichAnnulVers(blXmlRichAnnulVers);
        xmlRichAnnulVers.setCdVersioneXml(cdVersioneXml);
        richAnnulVers.addAroXmlRichAnnulVers(xmlRichAnnulVers);
        return xmlRichAnnulVers;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funzioni Online">
    /**
     * Ricerca online - Ritorna il tablebean di risultati dati i filtri richiesti
     *
     * @param idUser
     *            id utente che ha eseguito la ricerca
     * @param filtri
     *            parametri della richiesta
     *
     * @return tablebean
     */
    public AroVRicRichAnnvrsTableBean getAroVRicRichAnnvrsTableBean(long idUser, RicercaRichAnnulVersBean filtri) {
        AroVRicRichAnnvrsTableBean table = new AroVRicRichAnnvrsTableBean();
        List<AroVRicRichAnnvrs> list = helper.retrieveAroVRicRichAnnvrs(idUser, filtri);
        if (list != null && !list.isEmpty()) {
            try {
                for (AroVRicRichAnnvrs aroVRicRichAnnvrs : list) {
                    AroVRicRichAnnvrsRowBean row = (AroVRicRichAnnvrsRowBean) Transform
                            .entity2RowBean(aroVRicRichAnnvrs);
                    row.setString("amb_ente_strut", aroVRicRichAnnvrs.getNmAmbiente() + " - "
                            + aroVRicRichAnnvrs.getNmEnte() + " - " + aroVRicRichAnnvrs.getNmStrut());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle richieste di annullamento versamento "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Carica il dettaglio richiesta di annullamento versamenti dato l'id richiesta
     *
     * @param idRichAnnulVers
     *            id della richiesta
     *
     * @return rowBean della vista
     */
    public AroVVisRichAnnvrsRowBean getAroVVisRichAnnvrsRowBean(BigDecimal idRichAnnulVers) {
        AroVVisRichAnnvrs richiesta = helper.findViewById(AroVVisRichAnnvrs.class, idRichAnnulVers);
        AroVVisRichAnnvrsRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisRichAnnvrsRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della richiesta di annullamento versamenti "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della richiesta di annullamento versamenti");
            }
        }
        return row;
    }

    public AroVLisItemRichAnnvrsTableBean getAroVLisItemRichAnnvrsTableBean(BigDecimal idRichAnnulVers) {
        AroVLisItemRichAnnvrsTableBean table = new AroVLisItemRichAnnvrsTableBean();
        List<AroVLisItemRichAnnvrs> list = helper.getAroVLisItemRichAnnvrs(idRichAnnulVers);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisItemRichAnnvrsTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di versamenti della richiesta di annullamento versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di versamenti della richiesta di annullamento versamenti");
            }
        }
        return table;
    }

    public AroVLisStatoRichAnnvrsTableBean getAroVLisStatoRichAnnvrsTableBean(BigDecimal idRichAnnulVers) {
        AroVLisStatoRichAnnvrsTableBean table = new AroVLisStatoRichAnnvrsTableBean();
        List<AroVLisStatoRichAnnvrs> list = helper.getAroVLisStatoRichAnnvrs(idRichAnnulVers);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisStatoRichAnnvrsTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di stati della richiesta di annullamento versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di stati della richiesta di annullamento versamenti");
            }
        }
        return table;
    }

    /**
     * Esegue il controllo degli item della richiesta data come parametro
     *
     * @param idRichAnnulVers
     *            id della richiesta
     * @param idUserIam
     *            utente corrente
     *
     * @throws ParerUserError
     *             Errore imprevisto
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void controlloItemOnline(BigDecimal idRichAnnulVers, long idUserIam) throws ParerUserError {
        AroRichAnnulVers rich = helper.findById(AroRichAnnulVers.class, idRichAnnulVers);
        try {
            // Elimino tutti gli errori rilevati sugli item della richiesta, tranne quelli
            // di tipo ITEM_NON_ESISTE,
            // ITEM_GIA_PRESENTE e ITEM_GIA_ANNULLATO
            helper.deleteAroErrRichAnnulVers(idRichAnnulVers.longValue(),
                    CostantiDB.TipoErrRichAnnulVers.getStatiControlloItem());
            for (AroItemRichAnnulVers item : rich.getAroItemRichAnnulVers()) {
                item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                controlloItemDaAnnullare(item, idUserIam);
            }
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista durante il controllo dei versamenti ";
            ParerUserError parerUserError = new ParerUserError(messaggio);
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw parerUserError;
        }
    }

    /**
     * Esegue il controllo degli item della richiesta data come parametro
     *
     * @param richAnnulVers
     *            richiesta annullamento versamento
     * @param idUserIam
     *            utente corrente
     *
     * @throws ParerInternalError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void controlloItemValidazioneFascicoli(AroRichAnnulVers richAnnulVers, long idUserIam)
            throws ParerInternalError {
        try {
            for (AroItemRichAnnulVers item : richAnnulVers.getAroItemRichAnnulVers()) {
                item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                controlloItemDaAnnullare(item, idUserIam);
            }
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista durante il controllo dei fascicoli nel job di Validazione Fascicoli ";
            ParerInternalError parerInternalError = new ParerInternalError(messaggio);
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw parerInternalError;
        }
    }

    /**
     * Esegue il controllo degli item della richiesta passata al ws di invio richiesta annullamento
     *
     * @param idRichAnnulVers
     *            id della richiesta
     * @param idUserIam
     *            id utente presente nell'xml di richiesta
     * @param isFromPreIngest
     *            true o false a seconda che la richiesta provenga o meno da PreIngest
     *
     * @throws it.eng.parer.exception.ParerUserError
     *             Errore imprevisto
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void controlloItemWsRichiestaAnnul(BigDecimal idRichAnnulVers, long idUserIam, boolean isFromPreIngest)
            throws ParerUserError {
        // Assumo lock esclusivo sulla richiesta
        AroRichAnnulVers rich = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        int progressivo = 0;
        try {
            // Elimino tutti gli errori rilevati sugli item della richiesta, tranne quelli
            // di tipo ITEM_NON_ESISTE,
            // ITEM_GIA_PRESENTE ed ITEM_GIA_ANNULLATO
            helper.deleteAroErrRichAnnulVers(idRichAnnulVers.longValue(),
                    CostantiDB.TipoErrRichAnnulVers.getStatiControlloItem());
            for (AroItemRichAnnulVers item : rich.getAroItemRichAnnulVers()) {
                progressivo++;
                entityManager.flush();
                entityManager.clear();
                item = entityManager.merge(item);
                logger.debug("{} - elaborazione AroItemRichAnnulVers - INIZIO", progressivo);
                if (item.getAroUnitaDoc() != null) {
                    // Assumo lock esclusivo sull'unità doc definita nell'item della richiesta
                    evHelper.lockUnitaDoc(item.getAroUnitaDoc());

                    item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                    controlloItemDaAnnullare(item, idUserIam);

                    // Se la richiesta proviene da PreIngest
                    if (isFromPreIngest) {
                        helper.deleteAroErrRichAnnulVers(idRichAnnulVers.longValue(),
                                CostantiDB.TipoErrRichAnnulVers.DA_ANNULLARE_IN_PING.toString());
                    }
                } else if (item.getFasFascicolo() != null) {
                    // MEV#26446
                    // Assumo lock esclusivo sul fascicolo definito nell'item della richiesta
                    evfHelper.lockFasFascicolo(item.getFasFascicolo());

                    item.setTiStatoItem(CostantiDB.StatoItemRichAnnulVers.NON_ANNULLABILE.name());
                    controlloItemDaAnnullare(item, idUserIam);
                    // end MEV#26446
                }
                logger.debug("{} - elaborazione AroItemRichAnnulVers - FINE", progressivo);
            }
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista durante il controllo dei versamenti ";
            ParerUserError parerUserError = new ParerUserError(messaggio);
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw parerUserError;
        }
    }

    /**
     * Esegue la modifica della richiesta di annullamento versamenti
     *
     * @param idRichAnnulVers
     *            id della richiesta
     * @param cdRichAnnulVers
     *            nuovo valore del campo cdRichAnnulVers
     * @param dsRichAnnulVers
     *            nuovo valore del campo dsRichAnnulVers
     * @param ntRichAnnulVers
     *            nuovo valore del campo ntRichAnnulVers
     * @param flForzaAnnul
     *            flag 1/0 (true/false)
     * @param idStrut
     *            struttura di riferimento della richiesta
     *
     * @throws ParerUserError
     *             eccezione generazione
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRichAnnulVers(BigDecimal idRichAnnulVers, String cdRichAnnulVers, String dsRichAnnulVers,
            String ntRichAnnulVers, String flForzaAnnul, BigDecimal idStrut) throws ParerUserError {
        try {
            AroRichAnnulVers richAnnulVers = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
            if (!cdRichAnnulVers.equals(richAnnulVers.getCdRichAnnulVers())
                    && helper.isRichAnnulVersExisting(cdRichAnnulVers, idStrut)) {
                throw new ParerUserError(
                        "Nella struttura versante corrente \u00E8 gi\u00E0 presente una richiesta di annullamento versamenti con lo stesso codice");
            }
            // Verifica lo stato corrente della richiesta
            AroStatoRichAnnulVers statoCorrente = helper.findById(AroStatoRichAnnulVers.class,
                    richAnnulVers.getIdStatoRichAnnulVersCor());
            if (!statoCorrente.getTiStatoRichAnnulVers().equals(CostantiDB.StatoRichAnnulVers.APERTA.name())) {
                throw new ParerUserError(
                        "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA");
            }

            richAnnulVers.setCdRichAnnulVers(cdRichAnnulVers);
            richAnnulVers.setDsRichAnnulVers(dsRichAnnulVers);
            richAnnulVers.setNtRichAnnulVers(ntRichAnnulVers);
            richAnnulVers.setFlForzaAnnul(flForzaAnnul);
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio della richiesta di annullamento del versamento : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di annullamento del versamento");
        }
    }

    /**
     * Esegue la modifica della richiesta di annullamento versamenti, aggiungendo le ud da un file caricato
     *
     * @param idRichAnnulVers
     *            id richiesta annullamento
     * @param fileByteArray
     *            array file in byte
     * @param idUserIam
     *            id user Iam
     * @param tiRichAnnulVers
     *            tipo richiesta annullamento
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRichAnnulVers(BigDecimal idRichAnnulVers, byte[] fileByteArray, long idUserIam,
            String tiRichAnnulVers) throws ParerUserError {
        try {
            AroRichAnnulVers richAnnulVers = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
            richAnnulVers.setTiCreazioneRichAnnulVers(CostantiDB.TipoCreazioneRichAnnulVers.UPLOAD_FILE.name());
            if (fileByteArray != null) {
                if (tiRichAnnulVers.equals("UNITA_DOC")) {
                    AroFileRichAnnulVers fileRich = new AroFileRichAnnulVers();
                    fileRich.setTiFile(CostantiDB.TipoFileRichAnnulVers.FILE_UD_ANNUL.name());
                    fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
                    richAnnulVers.addAroFileRichAnnulVer(fileRich);

                    context.getBusinessObject(AnnulVersEjb.class).handleCsvRecords(richAnnulVers, fileByteArray,
                            idUserIam);
                } else if (tiRichAnnulVers.equals("FASCICOLI")) {
                    AroFileRichAnnulVers fileRich = new AroFileRichAnnulVers();
                    fileRich.setTiFile(CostantiDB.TipoFileRichAnnulVers.FILE_FASC_ANNUL.name());
                    fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
                    richAnnulVers.addAroFileRichAnnulVer(fileRich);

                    context.getBusinessObject(AnnulVersEjb.class).handleCsvRecordsFasc(richAnnulVers, fileByteArray,
                            idUserIam);
                }
            }
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio della richiesta di annullamento del versamento : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di annullamento del versamento");
        }
    }

    /**
     * Esegue l'eliminazione della richiesta di annullamento versamenti
     *
     * @param idRichAnnulVers
     *            id della richiesta
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteRichAnnulVers(BigDecimal idRichAnnulVers) throws ParerUserError {
        AroRichAnnulVers richAnnulVers = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        // Verifica lo stato corrente della richiesta
        AroStatoRichAnnulVers statoCorrente = helper.findById(AroStatoRichAnnulVers.class,
                richAnnulVers.getIdStatoRichAnnulVersCor());
        if (!statoCorrente.getTiStatoRichAnnulVers().equals(CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            throw new ParerUserError(
                    "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA");
        }
        helper.removeEntity(richAnnulVers, false);
    }

    /**
     * Esegue l'eliminazione di un item della richiesta di annullamento versamenti
     *
     * @param idRichAnnulVers
     *            id della richiesta
     * @param idItemRichAnnulVers
     *            id dell'item da eliminare
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteItemRichAnnulVers(BigDecimal idRichAnnulVers, BigDecimal idItemRichAnnulVers)
            throws ParerUserError {
        AroRichAnnulVers richAnnulVers = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        AroStatoRichAnnulVers statoCorrente = helper.findById(AroStatoRichAnnulVers.class,
                richAnnulVers.getIdStatoRichAnnulVersCor());
        // Verifica lo stato corrente della richiesta
        if (!statoCorrente.getTiStatoRichAnnulVers().equals(CostantiDB.StatoRichAnnulVers.APERTA.name())) {
            throw new ParerUserError(
                    "Il versamento non \u00E8 eliminabile perch\u00E9 la richiesta ha stato corrente diverso da APERTA");
        }
        AroItemRichAnnulVers item = helper.findById(AroItemRichAnnulVers.class, idItemRichAnnulVers);
        helper.removeEntity(item, false);
    }

    /**
     * Esegue il cambio di stato per una specifica richiesta
     *
     * @param idUserIam
     *            id dell'utente che esegue il cambio stato
     * @param idRichAnnulVers
     *            id della richiesta
     * @param tiStatoRichAnnulVersOld
     *            stato attuale della richiesta
     * @param tiStatoRichAnnulVersNew
     *            stato da assumere
     * @param dsNotaRichAnnulVers
     *            nota utente
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cambiaStato(long idUserIam, BigDecimal idRichAnnulVers, String tiStatoRichAnnulVersOld,
            String tiStatoRichAnnulVersNew, String dsNotaRichAnnulVers) throws ParerUserError {
        AroRichAnnulVers richAnnulVers = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        AroStatoRichAnnulVers statoCorrente = helper.findById(AroStatoRichAnnulVers.class,
                richAnnulVers.getIdStatoRichAnnulVersCor());
        IamUser iamUser = helper.findById(IamUser.class, idUserIam);
        // Verifica lo stato corrente della richiesta
        if (!statoCorrente.getTiStatoRichAnnulVers().equals(tiStatoRichAnnulVersOld)) {
            throw new ParerUserError("La richiesta ha cambiato stato");
        }

        AroStatoRichAnnulVers statoRichAnnulVers = context.getBusinessObject(AnnulVersEjb.class)
                .createAroStatoRichAnnulVers(richAnnulVers, tiStatoRichAnnulVersNew, Calendar.getInstance().getTime(),
                        dsNotaRichAnnulVers, iamUser);
        helper.insertEntity(statoRichAnnulVers, false);
        richAnnulVers.setIdStatoRichAnnulVersCor(new BigDecimal(statoRichAnnulVers.getIdStatoRichAnnulVers()));
    }
    // </editor-fold>

    /**
     * Verifica che la richiesta abbia uno degli stati elencati
     *
     * @param idRichAnnulVers
     *            id della richiesta
     * @param statiRichiesta
     *            stati da verificare
     *
     * @return true se la richiesta ha stato uguale a uno di quelli in elenco
     */
    public boolean checkStatoRichiesta(BigDecimal idRichAnnulVers, String... statiRichiesta) {
        boolean result = false;

        AroRichAnnulVers rich = helper.findById(AroRichAnnulVers.class, idRichAnnulVers);
        if (rich != null) {
            AroStatoRichAnnulVers ultimoStato = helper.findById(AroStatoRichAnnulVers.class,
                    rich.getIdStatoRichAnnulVersCor());
            if (statiRichiesta != null) {
                for (String stato : statiRichiesta) {
                    if (ultimoStato.getTiStatoRichAnnulVers().equals(stato)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public boolean checkStatoItems(BigDecimal idRichAnnulVers, String... statiItems) {
        Long count = helper.countAroItemRichAnnulVers(idRichAnnulVers, statiItems);
        return count > 0L;
    }

    /**
     * Ritorna il numero di items all'interno della richiesta con id <code>idRichAnnulVers</code> con gli stati elencati
     *
     * @param idRichAnnulVers
     *            id della richiesta di annullamento
     * @param statiItems
     *            stati da controllare
     *
     * @return il numero di items
     */
    public Long countItemsInRichAnnulVers(BigDecimal idRichAnnulVers, String... statiItems) {
        return helper.countAroItemRichAnnulVers(idRichAnnulVers, statiItems);
    }

    /**
     * Ritorna il numero di items all'interno della richiesta con id <code>idRichAnnulVers</code>
     *
     * @param idRichAnnulVers
     *            id richiesta annullamento
     *
     * @return il numero di items
     */
    public Long countItemsInRichAnnulVers(BigDecimal idRichAnnulVers) {
        return helper.countAroItemRichAnnulVers(idRichAnnulVers);
    }

    public boolean isUdInRichAnnulVers(BigDecimal idUnitaDoc) {
        boolean result = false;
        AroRichAnnulVers existingRich = helper.getAroRichAnnulVersContainingUd(idUnitaDoc.longValue(), null);
        if (existingRich != null) {
            result = true;
        }
        return result;
    }

    public boolean isFascInRichAnnulVers(BigDecimal idFascicolo) {
        boolean result = false;
        AroRichAnnulVers existingRich = helper.getAroRichAnnulVersContainingFasc(idFascicolo.longValue(), null);
        if (existingRich != null) {
            result = true;
        }
        return result;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addUnitaDocToRichAnnulVers(BigDecimal idRichAnnulVers, String registro, BigDecimal anno, String numero,
            int progressivo, long idUserIam) throws ParerUserError {
        AroRichAnnulVers rich = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        BigDecimal idStrut = new BigDecimal(rich.getOrgStrut().getIdStrut());

        AroItemRichAnnulVers item = createAroItemRichAnnulVers(rich, registro, anno, numero, progressivo);
        Long idUnitaDoc = udHelper.getIdUnitaDocVersataNoAnnul(idStrut, registro, anno, numero);
        logger.debug("Controlli item da annullare");
        if (idUnitaDoc != null) {
            // UD esistente
            AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
            item.setAroUnitaDoc(ud);
            controlloItemDaAnnullare(item, idUserIam);
        } else {
            throw new ParerUserError(
                    "Errore inaspettato nell'inserimento dell'unit\u00E0 documentaria alla richiesta: Impossibile recuperare l'unit\u00E0 documentaria "
                            + registro + "-" + anno.toPlainString() + "-" + numero + " per la struttura "
                            + rich.getOrgStrut().getNmStrut());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addFascicoloToRichAnnulVers(BigDecimal idRichAnnulVers, BigDecimal anno, String numero, int progressivo,
            long idUserIam) throws ParerUserError {
        AroRichAnnulVers rich = helper.findByIdWithLock(AroRichAnnulVers.class, idRichAnnulVers);
        BigDecimal idStrut = new BigDecimal(rich.getOrgStrut().getIdStrut());

        AroItemRichAnnulVers item = createAroItemRichAnnulVersFasc(rich, anno, numero, progressivo);
        Long idFascicolo = fascicoliHelper.getIdFascVersatoNoAnnul(idStrut, anno, numero);
        logger.debug("Controlli item da annullare");
        if (idFascicolo != null) {
            // Fascicolo esistente
            FasFascicolo fasc = helper.findById(FasFascicolo.class, idFascicolo);
            item.setFasFascicolo(fasc);
            controlloItemDaAnnullare(item, idUserIam);
        } else {
            throw new ParerUserError(
                    "Errore inaspettato nell'inserimento del fascicolo alla richiesta: Impossibile recuperare il fascicolo "
                            + anno.toPlainString() + "-" + numero + " per la struttura "
                            + rich.getOrgStrut().getNmStrut());
        }
    }

    public BigDecimal getUltimoProgressivoItemRichiesta(BigDecimal idRichAnnulVers) {
        return helper.getUltimoProgressivoItemRichiesta(idRichAnnulVers.longValue());
    }

    public AroVVisStatoRichAnnvrsRowBean geAroVVisStatoRichAnnvrsRowBean(BigDecimal idStatoRichAnnvrs) {
        AroVVisStatoRichAnnvrs richiesta = helper.findViewById(AroVVisStatoRichAnnvrs.class, idStatoRichAnnvrs);
        AroVVisStatoRichAnnvrsRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisStatoRichAnnvrsRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dello stato della richiesta di annullamento versamenti "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero dello stato della richiesta di annullamento versamenti");
            }
        }
        return row;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveStatoRichAnnulVers(BigDecimal idStatoRichAnnulVers, String dsNotaRichAnnulVers)
            throws ParerUserError {
        logger.debug("Eseguo il salvataggio dello stato");
        try {
            AroStatoRichAnnulVers stato = helper.findById(AroStatoRichAnnulVers.class, idStatoRichAnnulVers);
            stato.setDsNotaRichAnnulVers(dsNotaRichAnnulVers);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio dello stato ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    public String marshallaEsitoRichiestaAnnullamentoVersamenti(EsitoRichiestaAnnullamentoVersamenti esito)
            throws JAXBException {
        StringWriter sw = new StringWriter();
        Marshaller marshaller = xmlContextCache.getEsitoAnnVersCtx_EsitoRichiestaAnnullamentoVersamenti()
                .createMarshaller();
        marshaller.marshal(esito, sw);
        return sw.toString();
    }

    public long getUserFirstStateRich(BigDecimal idRichAnnulVers) {
        return helper.getIdUserIamFirstStateRich(idRichAnnulVers);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public FasStatoConservFascicolo createFasStatoConservFascicolo(FasFascicolo fascicolo, Date data,
            TiStatoConservazione tiStatoConservazione, IamUser iamUser) {
        FasStatoConservFascicolo statoConservFascicolo = new FasStatoConservFascicolo();
        statoConservFascicolo.setFasFascicolo(fascicolo);
        statoConservFascicolo.setTsStato(data);
        statoConservFascicolo.setTiStatoConservazione(tiStatoConservazione);
        statoConservFascicolo.setIamUser(iamUser);
        if (fascicolo.getFasStatoConservFascicoloElencos() == null) {
            fascicolo.setFasStatoConservFascicoloElencos(new ArrayList<>());
        }
        fascicolo.getFasStatoConservFascicoloElencos().add(statoConservFascicolo);
        return statoConservFascicolo;
    }

    public String getXmlRichAnnulVersByTipo(BigDecimal idRichAnnulVers,
            CostantiDB.TiXmlRichAnnulVers tiXmlRichAnnulVers) {
        return helper.getXmlRichAnnulVersByTipo(idRichAnnulVers.longValue(), tiXmlRichAnnulVers);
    }
}
