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
package it.eng.parer.scarto.ejb;

import com.csvreader.CsvReader;
import it.eng.parer.scarto.dto.RicercaRichScartoVersBean;
import it.eng.parer.annulVers.dto.UnitaDocBean;
import it.eng.parer.entity.*;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.viewbean.*;
import it.eng.parer.viewEntity.*;
import it.eng.parer.web.ejb.DataMartEjb;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import it.eng.parer.entity.constraint.AplValoreParamApplic;
import it.eng.parer.scarto.helper.ScartoHelper;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class ScartoEjb {

    private static final Logger logger = LoggerFactory.getLogger(ScartoEjb.class);
    private static final String LOG_MESSAGE_SCARTO = "Scarto Versamenti Unit\u00E0 Documentarie --- ";

    @Resource
    private SessionContext context;
    @EJB
    private ScartoHelper helper;
    @EJB
    private UnitaDocumentarieHelper udHelper;
    // MEV #30725
    @EJB
    private DataMartEjb dataMartEjb;
    // end MEV #30725
    @EJB
    private ConfigurationHelper configurationHelper;

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    // <editor-fold defaultstate="collapsed" desc="Creazione richiesta annullamento
    // versamenti">
    /**
     * Verifica l'esistenza degli header REGISTRO, ANNO, NUMERO all'interno del file inviato
     *
     * @param fileByteArray file byte array
     *
     * @return true se sono presenti tutti gli header richiesti
     *
     * @throws IOException eccezione di tipo IO
     */
    public boolean checkCsvHeaders(byte[] fileByteArray) throws IOException {
        boolean result = true;
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray),
                StandardCharsets.UTF_8);
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
     * Verifica l'esistenza di una precedente richiesta di scarto per codice richiesta
     *
     * @param cdRichScartoVers codice richiesta scarto
     * @param idStrut          id struttura
     *
     * @return true se esiste gi\u00E0 una richiesta di scarto con codice
     *         <code>cdRichScartoVers</code>
     */
    public boolean checkCdRichScartoVersExisting(String cdRichScartoVers, BigDecimal idStrut) {
        return helper.isRichScartoVersExisting(cdRichScartoVers, idStrut);
    }

    /**
     * Esegue il salvataggio in transazione del nuovo record di richiesta scarto versamento
     *
     * @param idUserIam        utente che crea la richiesta di scarto
     * @param cdRichScartoVers codice richiesta
     * @param dsRichScartoVers descrizione richiesta
     * @param ntRichScartoVers nota richiesta
     * @param fileByteArray    upload caricato dall'utente
     * @param idStrut          la struttura per cui viene creata la serie
     *
     * @return id richiesta
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveRichScartoVers(long idUserIam, String cdRichScartoVers, String dsRichScartoVers,
            String ntRichScartoVers, byte[] fileByteArray, BigDecimal idStrut)
            throws ParerUserError {
        logger.info("Eseguo il salvataggio della richiesta di scarto");
        Date now = Calendar.getInstance().getTime();
        Long idRich = null;
        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
            IamUser user = helper.findById(IamUser.class, idUserIam);
            if (user.getAroStatoRichScartoVers() == null) {
                user.setAroStatoRichScartoVers(new ArrayList<>());
            }

            // Preparo la richiesta da registrare
            AroRichScartoVers rich = new AroRichScartoVers();
            rich.setCdRichScartoVers(cdRichScartoVers);
            rich.setDsRichScartoVers(dsRichScartoVers);
            rich.setNtRichScartoVers(ntRichScartoVers);
            rich.setDtCreazioneRichScartoVers(now);
            rich.setTiCreazioneRichScartoVers(
                    CostantiDB.TipoCreazioneRichScartoVers.UPLOAD_FILE.name());
            rich.setOrgStrut(strut);
            if (rich.getAroFileRichScartoVers() == null) {
                rich.setAroFileRichScartoVers(new ArrayList<>());
            }
            if (rich.getAroItemRichScartoVers() == null) {
                rich.setAroItemRichScartoVers(new ArrayList<>());
            }
            if (rich.getAroStatoRichScartoVers() == null) {
                rich.setAroStatoRichScartoVers(new ArrayList<>());
            }

            helper.insertEntity(rich, true);

            // Preparo lo stato da registrare
            String stato = (fileByteArray != null) ? CostantiDB.StatoRichScartoVers.CHIUSA.name()
                    : CostantiDB.StatoRichScartoVers.APERTA.name();
            AroStatoRichScartoVers statoRichScartoVers = context.getBusinessObject(ScartoEjb.class)
                    .createAroStatoRichScartoVers(rich, stato, now, null, user);

            // Se è specificato il file, devo preparare il file da registrare in
            // ARO_FILE_RICH_SCARTO_VERS
            if (fileByteArray != null) {
                AroFileRichScartoVers fileRich = new AroFileRichScartoVers();
                fileRich.setTiFile(CostantiDB.TipoFileRichScartoVers.FILE_UD_SCARTO.name());
                fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
                rich.addAroFileRichScartoVers(fileRich);

                // Preparo gli item e gli eventuali errori in ARO_ITEM_RICH_SCARTO_VERS e
                // ARO_ERR_RICH_SCARTO_VERS
                context.getBusinessObject(ScartoEjb.class).handleCsvRecordsScarto(rich,
                        fileByteArray, idUserIam);
            }

            helper.insertEntity(statoRichScartoVers, true);

            // Aggiorno l’identificatore dello stato corrente della richiesta assegnando
            // l’identificatore dello stato
            // inserito
            rich.setIdStatoRichScartoVersCor(
                    new BigDecimal(statoRichScartoVers.getIdStatoRichScartoVers()));

            logger.info("Salvataggio della richiesta scarto completato");
            idRich = rich.getIdRichScartoVers();
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error(
                    "Errore imprevisto durante il salvataggio della richiesta di scarto del versamento : "
                            + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di scarto del versamento");
        }
        return idRich;
    }

    /**
     * Legge il csv e crea gli oggetti AroItemRichScartoVers da salvare alla richiesta di scarto e
     * gli eventuali errori in AroErrRichScartoVers
     *
     * @param rich          la richiesta di scarto
     * @param fileByteArray il file csv in byte[]
     * @param idUserIam     id utente che ha creato la richiesta (ovvero colui che ha definito il
     *                      primo stato)
     *
     * @throws IOException    eccezione di tipo IO
     * @throws ParerUserError Errore
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void handleCsvRecordsScarto(AroRichScartoVers rich, byte[] fileByteArray, long idUserIam)
            throws IOException, ParerUserError {
        /* Recupero il CSVReader */
        CsvReader csvReader = new CsvReader(new ByteArrayInputStream(fileByteArray),
                StandardCharsets.UTF_8);
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

                    if (udsInRich.add(new UnitaDocBean(idStrut, registro, anno, numero))) {
                        // Ricavo l'ud
                        Long idUnitaDoc = udHelper.getIdUnitaDocVersata(idStrut, registro, anno,
                                numero);
                        // Verifico che l'ud non sia già stata scartata (ATTUALMENTE CONTROLLO IL
                        // RECORD IN eventuali altre AroItemRichScartoVers
                        // in quanto non ho un'informazione diretta su AroUnitaDoc) o già annullata
                        boolean udGiaScartata = false;
                        boolean udGiaAnnullata = false;

                        if (idUnitaDoc != null) {
                            udGiaScartata = helper.isUdScartata(idUnitaDoc);
                            udGiaAnnullata = helper.isUdAnnullata(idUnitaDoc);
                        }

                        // Se l'item non era già presente nella richiesta, preparo il record da
                        // registrare in ARO_ITEM_RICH_SCARTO_VERS
                        // Questo metodo imposta di default lo stato a NON_SCARTABILE
                        AroItemRichScartoVers item = createAroItemRichScartoVers(rich, registro,
                                anno, numero, progressivoItem);

                        logger.debug("Controlli item da scartare");
                        if (idUnitaDoc == null) {
                            // UD non esistente - creo diverso record di errore
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-"
                                    + annoString + "-" + numero + " non esiste";
                            createAroErrRichScartoVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else if (udGiaAnnullata) {
                            // CASO 2: UD esistente ma già annullata
                            // Deve avere errore ITEM_NON_ESISTE e restare NON_SCARTABILE
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-"
                                    + annoString + "-" + numero + " è già stata annullata";

                            createAroErrRichScartoVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichAnnulVers.ITEM_NON_ESISTE.name(), dsErr,
                                    CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else if (udGiaScartata) {
                            // CASO 3: UD esistente ma già scartata
                            String dsErr = "L'unit\u00E0 documentaria " + registro + "-"
                                    + annoString + "-" + numero + " \u00E8 gi\u00E0 stata scartata";
                            createAroErrRichScartoVers(item, BigDecimal.ONE,
                                    CostantiDB.TipoErrRichScartoVers.ITEM_GIA_SCARTATO.name(),
                                    dsErr, CostantiDB.TipoGravitaErrore.ERRORE.name());
                        } else {
                            // CASO 4: UD esistente e valida per controlli successivi
                            AroUnitaDoc ud = helper.findById(AroUnitaDoc.class, idUnitaDoc);
                            // Aggiungo l'UD all'item
                            item.setAroUnitaDoc(ud);

                            /*
                             * CONTROLLI ITEM DA SCARTARE Se questi controlli passano, lo stato
                             * verrà aggiornato a DA_SCARTARE altrimenti resterà NON_SCARTABILE
                             * (default)
                             */
                            controlloItemDaScartare(item, idUserIam);
                        }

                        progressivoItem++;
                    }
                } // Fine per ogni record del csv
                if (progressivoItem == 1) {
                    // Il file non conteneva nemmeno una riga di ud, a parte l'intestazione
                    throw new ParerUserError(
                            "Il file non contiene alcuna unit\u00E0 documentaria da scartare");
                }
            } else {
                throw new IllegalStateException(
                        "Errore imprevisto nella lettura del file caricato per la creazione della richiesta di scarto del versamento. Presumibilmente file non in formato csv.");
            }
        } finally {
            csvReader.close();
        }
    }

    /**
     * Controlli sull'item candidato ad essere scartato tenendo conto delle abilitazioni dell'utente
     * passato in ingresso
     *
     * @param item
     */
    private void controlloItemDaScartare(AroItemRichScartoVers item, long idUserIam) {
        int progressivoErr = 1;
        AroUnitaDoc ud = item.getAroUnitaDoc();

        // TODO DA VERIFICARE PER LO SCARTO

        // Controllo se esiste già un'altra richiesta, diversa da quella corrente, con
        // stato APERTA o CHIUSA che
        // contiene quella unita doc da scartare
        if (ud != null) {
            AroRichScartoVers existingRich = helper.getAroRichScartoVersContainingUd(
                    ud.getIdUnitaDoc(), item.getAroRichScartoVers().getIdRichScartoVers());
            if (existingRich != null) {
                // UD gi\u00E0 presente in un'altra richiesta diversa da quella corrente
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " \u00E8 in corso di scarto nella richiesta "
                        + existingRich.getCdRichScartoVers();
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichScartoVers.ITEM_IN_CORSO_DI_SCARTO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // TODO DA VERIFICARE PER LO SCARTO
            // Controllo se l'ud è usata come riferimento da almeno un componente
            // if (compHelper.isAroUnitaDocReferredByOtherAroCompDocs(ud.getIdUnitaDoc(),
            // new BigDecimal(ud.getOrgStrut().getIdStrut()))) {
            // // UD riferita da componenti
            // String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
            // + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
            // + " \u00E8 usata come riferimento da almeno un componente";
            // createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
            // CostantiDB.TipoErrRichAnnulVers.ITEM_RIFERITO.name(), dsErr,
            // CostantiDB.TipoGravitaErrore.ERRORE.name());
            // }

            // TODO DA VERIFICARE PER LO SCARTO
            // boolean forzaAnnul = item.getAroRichAnnulVers().getFlForzaAnnul().equals("1");
            // if ((!forzaAnnul
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE.name())
            // && !ud.getTiStatoConservazione()
            // .equals(CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
            // && !ud.getTiStatoConservazione()
            // .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())
            // && !ud.getTiStatoConservazione()
            // .equals(CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO.name()))
            // || (forzaAnnul && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.IN_VOLUME_DI_CONSERVAZIONE.name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.PRESA_IN_CARICO.name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.AIP_GENERATO.name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.AIP_FIRMATO.name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.AIP_IN_AGGIORNAMENTO
            // .name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.IN_ARCHIVIO.name())
            // && !ud.getTiStatoConservazione().equals(
            // CostantiDB.StatoConservazioneUnitaDoc.IN_CUSTODIA.name()))) {
            // // Stato conservazione errato
            // String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
            // + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
            // + " ha stato di conservazione pari a " + ud.getTiStatoConservazione()
            // + " e, quindi, non pu\u00F2 essere annullata";
            // createAroErrRichAnnulVers(item, new BigDecimal(progressivoErr++),
            // CostantiDB.TipoErrRichAnnulVers.STATO_CONSERV_NON_AMMESSO.name(), dsErr,
            // CostantiDB.TipoGravitaErrore.ERRORE.name());
            // }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al
            // TIPO_UNITA_DOC, in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam,
                    ud.getDecTipoUnitaDoc().getIdTipoUnitaDoc(),
                    Constants.TipoDato.TIPO_UNITA_DOC.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha tipo unit\u00E0 documentaria a cui l'utente non \u00E8 abilitato";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichScartoVers.TIPO_UNITA_DOC_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }

            // Controlla se l'utente in input è abilitato al tipo dato (IAM_ABIL_TIPO_DATO)
            // corrispondente al REGISTRO,
            // in caso negativo registro l'errore
            if (!helper.isUserAbilitatoToTipoDato(idUserIam,
                    ud.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                    Constants.TipoDato.REGISTRO.name())) {
                String dsErr = "L'unit\u00E0 documentaria " + ud.getCdRegistroKeyUnitaDoc() + "-"
                        + ud.getAaKeyUnitaDoc().toPlainString() + "-" + ud.getCdKeyUnitaDoc()
                        + " ha registro a cui l'utente non \u00E8 abilitato";
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
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
                createAroErrRichScartoVers(item, new BigDecimal(progressivoErr++),
                        CostantiDB.TipoErrRichAnnulVers.TIPO_DOC_PRINC_NON_ABILITATO.name(), dsErr,
                        CostantiDB.TipoGravitaErrore.ERRORE.name());
            }
        }

        // Se per l'item corrente non sono presenti errori con gravità ERRORE
        if (helper.getAroErrRichScartoVersByGravity(item.getIdItemRichScartoVers(),
                CostantiDB.TipoGravitaErrore.ERRORE.name()).isEmpty()) {
            item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.DA_SCARTARE.name());
        }

    }

    private void createAroErrRichScartoVers(AroItemRichScartoVers item, BigDecimal pgErr,
            String tiErr, String dsErr, String tiGravitaErr) {
        AroErrRichScartoVers err = new AroErrRichScartoVers();
        err.setPgErr(pgErr);
        err.setTiErr(tiErr);
        err.setDsErr(dsErr);
        err.setTiGravita(tiGravitaErr);
        item.addAroErrRichScartoVers(err);
        helper.insertEntity(err, true);
    }

    private AroItemRichScartoVers createAroItemRichScartoVers(AroRichScartoVers rich,
            String registro, BigDecimal anno, String numero, int progressivo) {
        // Dovrei aver ottenuto tutti i campi necessari per creare il nuovo record
        AroItemRichScartoVers item = new AroItemRichScartoVers();
        item.setCdRegistroKeyUnitaDoc(registro);
        item.setAaKeyUnitaDoc(anno);
        item.setCdKeyUnitaDoc(numero);
        item.setIdStrut(new BigDecimal(rich.getOrgStrut().getIdStrut()));
        item.setPgItemRichScartoVers(new BigDecimal(progressivo));
        item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.NON_SCARTABILE.name());
        if (item.getAroErrRichScartoVers() == null) {
            item.setAroErrRichScartoVers(new ArrayList<>());
        }
        rich.addAroItemRichScartoVers(item);
        helper.insertEntity(item, true);
        return item;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Scarto versamenti">
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraRichiestaScarto(long idRichScartoVers, long idUserIam, String modalita) {
        // Assumo lock esclusivo sulla richiesta
        AroRichScartoVers richiestaScarto = helper.findByIdWithLock(AroRichScartoVers.class,
                idRichScartoVers);
        AroStatoRichScartoVers statoRichScartoVers = helper.findById(AroStatoRichScartoVers.class,
                richiestaScarto.getIdStatoRichScartoVersCor());
        boolean proseguiScarto = false;

        // Lock su item ud
        for (AroItemRichScartoVers itemRichScartoVers : richiestaScarto
                .getAroItemRichScartoVers()) {
            // Controllo che l'item esista per loccarlo
            if (itemRichScartoVers.getAroUnitaDoc() != null) {
                helper.findByIdWithLock(AroUnitaDoc.class,
                        itemRichScartoVers.getAroUnitaDoc().getIdUnitaDoc());
            }
        }

        // Se la richiesta ha stato corrente CHIUSA
        if (statoRichScartoVers.getTiStatoRichScartoVers()
                .equals(CostantiDB.StatoRichScartoVers.CHIUSA.name())) {
            proseguiScarto = true;
            logger.debug("{} --- Scarto Versamenti - Verifica della richiesta con stato CHIUSA",
                    ScartoEjb.class.getSimpleName());

            // Elimino tutti gli errori rilevati sugli item della richiesta, tranne quelli
            // di tipo ITEM_NON_ESISTE e ITEM_GIA_PRESENTE e ITEM_GIA_SCARTATO
            helper.deleteAroErrRichScartoVers(idRichScartoVers,
                    CostantiDB.TipoErrRichScartoVers.getStatiControlloItem());

            // Controllo gli item
            List<AroItemRichScartoVers> itemRichScartoVersList = richiestaScarto
                    .getAroItemRichScartoVers();
            for (AroItemRichScartoVers item : itemRichScartoVersList) {
                // Assumo lock esclusivo sull'unità doc definita nell'item della richiesta
                item.setTiStatoItemScarto(CostantiDB.StatoItemRichScartoVers.NON_SCARTABILE.name());
                /*
                 * CONTROLLA ITEM DA SCARTARE (senza cancellazione errori e assegnazione stato
                 * NON_SCARTABILE)
                 */
                controlloItemDaScartare(item, idUserIam);

            }
        }

        ////////////////////////
        // EVADO LA RICHIESTA //
        ////////////////////////
        // Se la richiesta era ancora chiusa (CONCORRENZA tra online e job)
        if (proseguiScarto) {
            context.getBusinessObject(ScartoEjb.class).evasioneRichiestaScarto(richiestaScarto,
                    idUserIam, modalita);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evasioneRichiestaScarto(AroRichScartoVers richiestaScarto, long idUserIam,
            String modalita) {
        richiestaScarto = entityManager.find(AroRichScartoVers.class,
                richiestaScarto.getIdRichScartoVers(), LockModeType.PESSIMISTIC_WRITE);
        evadiScartoVersamentiUnitaDoc(richiestaScarto, idUserIam, modalita);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void evadiScartoVersamentiUnitaDoc(AroRichScartoVers richiestaScarto, long idUserIam,
            String modalita) {
        long idRichScartoVers = richiestaScarto.getIdRichScartoVers();
        logger.debug("{} Id richiesta scarto versamento {}", LOG_MESSAGE_SCARTO, idRichScartoVers);
        AroStatoRichScartoVers statoRichScartoVers = helper.findById(AroStatoRichScartoVers.class,
                richiestaScarto.getIdStatoRichScartoVersCor());
        logger.info("{} Evasione richiesta scarto ID: {}", LOG_MESSAGE_SCARTO, idRichScartoVers);
        logger.debug("{} Procedo ad evadere la richiesta di scarto avente id:{}",
                LOG_MESSAGE_SCARTO, idRichScartoVers);
        logger.debug("{} tiStatoRichScartoVers={} dtRegStatoRichScartoVers={}", LOG_MESSAGE_SCARTO,
                statoRichScartoVers.getTiStatoRichScartoVers(),
                statoRichScartoVers.getDtRegStatoRichScartoVers());
        // Definisco come data di scarto la data corrente
        Calendar cal = Calendar.getInstance();
        Date dataScarto = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT_TIMESTAMP_TYPE);
        String dtString = df.format(dataScarto);

        // TODO EVENTUALI OPERAZIONI DA EFFETTUARE SUGLI ELEMENTI COLLEGATI ALLE UD COME
        // NELL'ANNULLAMENTO
        logger.debug("{} Registro il nuovo stato della richiesta di scarto avente id: {}",
                LOG_MESSAGE_SCARTO, idRichScartoVers);
        // Registra il nuovo stato della richiesta di scarto
        TypedQuery<AroStatoRichScartoVers> query = entityManager.createQuery(
                "SELECT a FROM AroStatoRichScartoVers a WHERE a.aroRichScartoVers = :aroRichScartoVers",
                AroStatoRichScartoVers.class);
        query.setParameter("aroRichScartoVers", richiestaScarto);
        richiestaScarto.setAroStatoRichScartoVers(query.getResultList());
        AroStatoRichScartoVers statoRichScartoVersNew = context.getBusinessObject(ScartoEjb.class)
                .createAroStatoRichScartoVers(richiestaScarto,
                        CostantiDB.StatoRichAnnulVers.EVASA.name(),
                        Calendar.getInstance().getTime(), null, statoRichScartoVers.getIamUser());
        helper.insertEntity(statoRichScartoVersNew, false);
        // Aggiorno l'identificatore dello stato corrente della richiesta assegnando
        // l'identificatore dello stato inserito
        richiestaScarto.setIdStatoRichScartoVersCor(
                new BigDecimal(statoRichScartoVersNew.getIdStatoRichScartoVers()));

        // Modifico gli item assegnando stato SCARTATO
        helper.updateStatoItemScartoList(idRichScartoVers,
                CostantiDB.StatoItemRichScartoVers.SCARTATO.name());

        // MEV #30725: registro nel centro stella del DataMart
        String tipoCancellazione = configurationHelper.getAplValoreParamApplic(
                CostantiDB.ParametroAppl.TI_CANCELLAZIONE_MS_UD_DEL,
                AplValoreParamApplic.TiAppart.APPLIC.name(), null, null, null, null);
        int totaliDataMart = dataMartEjb.insertUdDataMartScartoVersCentroStella(idRichScartoVers,
                richiestaScarto.getCdRichScartoVers(), CostantiDB.TiMotCancellazione.S.name(),
                tipoCancellazione);
        logger.info("Gestione DataMart Scarto Versamenti: inserite {} ud in DM_UD_DEL",
                totaliDataMart);
        // fine MEV #30725: registro nel DataMart

        logger.debug("{} - richiesta avente id: {} elaborata con successo!", LOG_MESSAGE_SCARTO,
                idRichScartoVers);
        logger.info("Fine evasione richiesta ID: {}", idRichScartoVers);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroStatoRichScartoVers createAroStatoRichScartoVers(AroRichScartoVers richScartoVers,
            String tiStatoRichScartoVers, Date dtRegStatoRichScartoVers,
            String dsNotaRichScartoVers, IamUser iamUser) {
        AroStatoRichScartoVers statoRichScartoVers = new AroStatoRichScartoVers();
        statoRichScartoVers.setPgStatoRichScartoVers(helper
                .getUltimoProgressivoStatoRichiestaScarto(richScartoVers.getIdRichScartoVers())
                .add(BigDecimal.ONE));
        statoRichScartoVers.setTiStatoRichScartoVers(tiStatoRichScartoVers);
        statoRichScartoVers.setDtRegStatoRichScartoVers(dtRegStatoRichScartoVers);
        statoRichScartoVers.setDsNotaRichScartoVers(dsNotaRichScartoVers);
        statoRichScartoVers.setIamUser(iamUser);
        richScartoVers.addAroStatoRichScartoVers(statoRichScartoVers);
        return statoRichScartoVers;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public AroXmlRichAnnulVers createAroXmlRichAnnulVers(AroRichAnnulVers richAnnulVers,
            String tiXmlRichAnnulVers, String blXmlRichAnnulVers, String cdVersioneXml) {
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
     * @param idUser id utente che ha eseguito la ricerca
     * @param filtri parametri della richiesta
     *
     * @return tablebean
     */
    public AroVRicRichScartoTableBean getAroVRicRichScartoTableBean(long idUser,
            RicercaRichScartoVersBean filtri) {
        AroVRicRichScartoTableBean table = new AroVRicRichScartoTableBean();
        List<AroVRicRichScarto> list = helper.retrieveAroVRicRichScarto(idUser, filtri);
        if (list != null && !list.isEmpty()) {
            try {
                for (AroVRicRichScarto aroVRicRichScarto : list) {
                    AroVRicRichScartoRowBean row = (AroVRicRichScartoRowBean) Transform
                            .entity2RowBean(aroVRicRichScarto);
                    row.setString("amb_ente_strut",
                            aroVRicRichScarto.getNmAmbiente() + " - "
                                    + aroVRicRichScarto.getNmEnte() + " - "
                                    + aroVRicRichScarto.getNmStrut());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero delle richieste di scarto versamento "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Carica il dettaglio richiesta di scarto versamenti dato l'id richiesta
     *
     * @param idRichScartoVers id della richiesta
     *
     * @return rowBean della vista
     */
    public AroVVisRichScartoRowBean getAroVVisRichScartoRowBean(BigDecimal idRichScartoVers) {
        AroVVisRichScarto richiesta = helper.findViewById(AroVVisRichScarto.class,
                idRichScartoVers);
        AroVVisRichScartoRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisRichScartoRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della richiesta di scarto versamenti "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della richiesta di scarto versamenti");
            }
        }
        return row;
    }

    public AroVLisItemRichScartoTableBean getAroVLisItemRichScartoTableBean(
            BigDecimal idRichScartoVers) {
        AroVLisItemRichScartoTableBean table = new AroVLisItemRichScartoTableBean();
        List<AroVLisItemRichScarto> list = helper.getAroVLisItemRichScarto(idRichScartoVers);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisItemRichScartoTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di versamenti della richiesta di scarto versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di versamenti della richiesta di scartoversamenti");
            }
        }
        return table;
    }

    public AroVLisStatoRichScartoTableBean getAroVLisStatoRichScartoTableBean(
            BigDecimal idRichScartoVers) {
        AroVLisStatoRichScartoTableBean table = new AroVLisStatoRichScartoTableBean();
        List<AroVLisStatoRichScarto> list = helper.getAroVLisStatoRichScarto(idRichScartoVers);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AroVLisStatoRichScartoTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di stati della richiesta di scarto versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di stati della richiesta di scarto versamenti");
            }
        }
        return table;
    }

    /**
     * Esegue la modifica della richiesta di scarto versamenti, aggiungendo le ud da un file
     * caricato
     *
     * @param idRichScartoVers id richiesta scarto
     * @param fileByteArray    array file in byte
     * @param idUserIam        id user Iam
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRichScartoVers(BigDecimal idRichScartoVers, byte[] fileByteArray,
            long idUserIam) throws ParerUserError {
        // try {
        // AroRichScartoVers richScartoVers = helper.findByIdWithLock(AroRichScartoVers.class,
        // idRichScartoVers);
        // richScartoVers.setTiCreazioneRichScartoVers(
        // CostantiDB.TipoCreazioneRichScartoVers.UPLOAD_FILE.name());
        // if (fileByteArray != null) {
        // AroFileRichScartoVers fileRich = new AroFileRichScartoVers();
        // fileRich.setTiFile(CostantiDB.TipoFileRichAnnulVers.FILE_UD_SCARTO.name());
        // fileRich.setBlFile(new String(fileByteArray, StandardCharsets.UTF_8));
        // richScartoVers.addAroFileRichAnnulVer(fileRich);
        //
        // context.getBusinessObject(ScartoEjb.class).handleCsvRecords(richScartoVers,
        // fileByteArray, idUserIam);
        // }
        // } catch (ParerUserError ex) {
        // throw ex;
        // } catch (Exception ex) {
        // logger.error(
        // "Errore imprevisto durante il salvataggio della richiesta di scarto del versamento : "
        // + ExceptionUtils.getRootCauseMessage(ex),
        // ex);
        // throw new ParerUserError(
        // "Eccezione imprevista durante il salvataggio della richiesta di scarto del versamento");
        // }
    }

    /**
     * Esegue l'eliminazione della richiesta di scarto versamenti
     *
     * @param idRichScartoVers id della richiesta
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteRichScartoVers(BigDecimal idRichScartoVers) throws ParerUserError {
        AroRichScartoVers richScartoVers = helper.findByIdWithLock(AroRichScartoVers.class,
                idRichScartoVers);
        // Verifica lo stato corrente della richiesta
        AroStatoRichScartoVers statoCorrente = helper.findById(AroStatoRichScartoVers.class,
                richScartoVers.getIdStatoRichScartoVersCor());
        if (!statoCorrente.getTiStatoRichScartoVers()
                .equals(CostantiDB.StatoRichScartoVers.APERTA.name())
                && !statoCorrente.getTiStatoRichScartoVers()
                        .equals(CostantiDB.StatoRichScartoVers.CHIUSA.name())) {
            throw new ParerUserError(
                    "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da APERTA o CHIUSA");
        }
        helper.removeEntity(richScartoVers, false);
    }

    /**
     * Esegue l'eliminazione di un item della richiesta di scarto versamenti
     *
     * @param idRichScartoVers     id della richiesta
     * @param idItemRichScartoVers id dell'item da eliminare
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteItemRichScartoVers(BigDecimal idRichScartoVers,
            BigDecimal idItemRichScartoVers) throws ParerUserError {
        AroRichScartoVers richScartoVers = helper.findByIdWithLock(AroRichScartoVers.class,
                idRichScartoVers);
        AroStatoRichScartoVers statoCorrente = helper.findById(AroStatoRichScartoVers.class,
                richScartoVers.getIdStatoRichScartoVersCor());
        // Verifica lo stato corrente della richiesta
        if (!statoCorrente.getTiStatoRichScartoVers()
                .equals(CostantiDB.StatoRichScartoVers.APERTA.name())) {
            throw new ParerUserError(
                    "Il versamento non \u00E8 eliminabile perch\u00E9 la richiesta ha stato corrente diverso da APERTA");
        }
        AroItemRichScartoVers item = helper.findById(AroItemRichScartoVers.class,
                idItemRichScartoVers);
        helper.removeEntity(item, false);
    }
    // </editor-fold>

    /**
     * Verifica che la richiesta abbia uno degli stati elencati
     *
     * @param idRichScartoVers id della richiesta
     * @param statiRichiesta   stati da verificare
     *
     * @return true se la richiesta ha stato uguale a uno di quelli in elenco
     */
    public boolean checkStatoRichiestaScarto(BigDecimal idRichScartoVers,
            String... statiRichiesta) {
        boolean result = false;

        AroRichScartoVers rich = helper.findById(AroRichScartoVers.class, idRichScartoVers);
        if (rich != null) {
            AroStatoRichScartoVers ultimoStato = helper.findById(AroStatoRichScartoVers.class,
                    rich.getIdStatoRichScartoVersCor());
            if (statiRichiesta != null) {
                for (String stato : statiRichiesta) {
                    if (ultimoStato.getTiStatoRichScartoVers().equals(stato)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public AroVVisStatoRichScartoRowBean geAroVVisStatoRichScartoRowBean(
            BigDecimal idStatoRichScarto) {
        AroVVisStatoRichScarto richiesta = helper.findViewById(AroVVisStatoRichScarto.class,
                idStatoRichScarto);
        AroVVisStatoRichScartoRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisStatoRichScartoRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero dello stato della richiesta di scarto versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero dello stato della richiesta di scarto versamenti");
            }
        }
        return row;
    }

    public AroVVisStatoRichAnnvrsRowBean geAroVVisStatoRichAnnvrsRowBean(
            BigDecimal idStatoRichAnnvrs) {
        AroVVisStatoRichAnnvrs richiesta = helper.findViewById(AroVVisStatoRichAnnvrs.class,
                idStatoRichAnnvrs);
        AroVVisStatoRichAnnvrsRowBean row = null;
        if (richiesta != null) {
            try {
                row = (AroVVisStatoRichAnnvrsRowBean) Transform.entity2RowBean(richiesta);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero dello stato della richiesta di annullamento versamenti "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
                throw new IllegalStateException(
                        "Errore durante il recupero dello stato della richiesta di annullamento versamenti");
            }
        }
        return row;
    }
}
