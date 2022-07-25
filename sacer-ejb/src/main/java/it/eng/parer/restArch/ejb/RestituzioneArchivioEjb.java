package it.eng.parer.restArch.ejb;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.entity.AroAipRestituzioneArchivio;
import it.eng.parer.entity.AroRichiestaRa;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;
import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import static it.eng.parer.job.calcoloEstrazione.RestituzioneArchivioJob.createEmptyDir;
import it.eng.parer.restArch.dto.RicercaRichRestArchBean;
import it.eng.parer.restArch.helper.RestituzioneArchivioHelper;
import it.eng.parer.slite.gen.tablebean.AroRichiestaRaRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRaRowBean;
import it.eng.parer.slite.gen.viewbean.AroVLisItemRaTableBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichRaRowBean;
import it.eng.parer.slite.gen.viewbean.AroVRicRichRaTableBean;
import it.eng.parer.viewEntity.AroVChkRaUd;
import it.eng.parer.viewEntity.AroVLisItemRa;
import it.eng.parer.viewEntity.AroVRicRichRa;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class RestituzioneArchivioEjb {

    private static final Logger logger = LoggerFactory.getLogger(RestituzioneArchivioEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private RestituzioneArchivioHelper helper;
    @EJB
    private ConfigurationHelper configurationHelper;

    // <editor-fold defaultstate="collapsed" desc="Creazione richiesta restituzione archivio">
    /**
     * Vista di verifica delle ud appartenenti all'ente convenzionato in capo alla struttura passata in input
     *
     * @param idStrut
     *            id struttura
     *
     * @return entity AroVChkRaUd
     *
     * @throws IOException
     *             errore generico
     */
    public AroVChkRaUd retrieveChkRaUnitaDoc(BigDecimal idStrut) throws IOException {
        // ricavo la struttura
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        return helper.findViewById(AroVChkRaUd.class, struttura.getIdEnteConvenz());
    }

    /**
     * Verifica l'esistenza e i permessi in lettura/scrittura delle cartelle per l’estrazione degli AIP
     *
     * @param idStrut
     *            id struttura
     *
     * @return true se sono presenti tutte le cartelle e i permessi
     *
     * @throws IOException
     *             errore generico di tipo IO
     */
    public boolean checkDirectoriesRa(BigDecimal idStrut) throws IOException {
        boolean result = false;
        /* Recupero la directory root $ROOT_FOLDER_EC_RA */
        String rootFolderEcRaPath = configurationHelper.getValoreParamApplic("ROOT_FOLDER_EC_RA", null, null, null,
                null, CostantiDB.TipoAplVGetValAppart.APPLIC);

        // MEV #26985 - Creazione automatica in FTP della cartella creazione archivio
        // Creo la directory root
        // createEmptyDir(rootFolderEcRaPath);
        // String rootFolderEcRaPath = "/tmp";
        if (IOUtils.exists(rootFolderEcRaPath)) {
            // ricavo la struttura
            OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
            // ricavo l'ente convenzionato
            SIOrgEnteSiam enteConvenz = helper.findById(SIOrgEnteSiam.class, struttura.getIdEnteConvenz());
            /* Percorso della directory $NM_ENTE_CONVENZIONATO, figlia di $ROOT_FOLDER_EC_RA */
            // MEV #26987 - Normalizzazione nome ente: portato in maiuscolo,
            // tolti gli accenti, sostituiti gli spazi e i caratteri speciali con "_"
            String nmEnteSiamNormalizzato = StringUtils.stripAccents(enteConvenz.getNmEnteSiam().toUpperCase())
                    .replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9]", "_");
            String childFolderEcRaPath = IOUtils.getPath(rootFolderEcRaPath, nmEnteSiamNormalizzato);
            // MEV #26985 - Creazione automatica in FTP della cartella creazione archivio
            // Creo la directory childFolder
            createEmptyDir(childFolderEcRaPath);

            if (IOUtils.isFileReady(childFolderEcRaPath)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Verifica l'esistenza dell'ente convenzionato in capo alla struttura
     *
     * @param idStrut
     *            id struttura
     *
     * @return true se esiste
     */
    public boolean checkEnteConvenzExisting(BigDecimal idStrut) {
        /* Ricavo la struttura */
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);

        return struttura.getIdEnteConvenz() != null;
    }

    /**
     * Verifica l'esistenza di una precedente richiesta di restituzione archivio
     *
     * @param idStrut
     *            id struttura
     *
     * @return true se esiste gi\u00E0 una richiesta di resituzione archivio
     */
    public boolean checkRichRestArchExisting(BigDecimal idStrut) {
        // ricavo la struttura
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        return helper.isRichRestArchExisting(struttura.getIdEnteConvenz());
    }

    /**
     * Verifica l'esistenza di una richiesta di restituzione archivio avente uno degli stati passati in ingresso
     *
     * @param idStrut
     *            id struttura
     * @param tiStato
     *            lista degli stati della richiesta di restituzione archivio
     *
     * @return true se esiste gi\u00E0 una richiesta di resituzione archivio negli stati passati in ingresso
     */
    public boolean checkRichRestArchByStatoExisting(BigDecimal idStrut, List<AroRichiestaTiStato> tiStato) {
        // ricavo la struttura
        OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
        return helper.isRichRestArchByStatoExisting(struttura.getIdEnteConvenz(), tiStato);
    }

    /**
     * Esegue il controllo in transazione delle richieste di restituzione archivio scadute
     *
     * @param idStrut
     *            la struttura per cui viene creata la richiesta
     *
     * @throws ParerUserError
     *             errore generico
     */
    public void elaboraRichRestArchExpired(BigDecimal idStrut) throws ParerUserError {
        logger.info("Gestione delle richieste di restituzione archivio scadute...");
        try {
            // ricavo la struttura
            OrgStrut struttura = helper.findById(OrgStrut.class, idStrut);
            // Determino le richieste con stato diverso da ANNULLATO appartenenti all'ente convenzionato della struttura
            // corrente,
            // la cui occorrenza sulla ARO_RICHIESTA_RA sia con dt_fine == null e dt_inizio + 24h sia antecedente
            // all'istante corrente
            List<Long> richiesteScaduteDaProcessare = helper
                    .retrieveRichRestArchExpiredToProcess(struttura.getIdEnteConvenz());
            RestituzioneArchivioEjb newRestituzioneArchivioEjbRef1 = context
                    .getBusinessObject(RestituzioneArchivioEjb.class);
            logger.info("trovate {} richieste di restituzione archivio scadute da settare in stato = ANNULLATO",
                    richiesteScaduteDaProcessare.size());
            for (Long idRichiesta : richiesteScaduteDaProcessare) {
                logger.debug("trovata richiesta {} scaduta da settare in stato = ANNULLATO", idRichiesta);
                newRestituzioneArchivioEjbRef1.setDaAnnullareAtomic("", idRichiesta, "RICHIESTA SCADUTA");
            }

            logger.info("Aggiornamento delle richieste restituzione archivio scadute completato");
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante l'aggiornamento delle richieste restituzione archivio scadute : {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante l'aggiornamento delle richieste restituzione archivio scadute");
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaAnnullareAtomic(String cdErrore, Long idRichiestaRa, String annullReason) throws ParerUserError {
        logger.debug("setDaAnnullareAtomic...");
        AroRichiestaRa richiesta = helper.findById(AroRichiestaRa.class, idRichiestaRa);
        setAnnullata(cdErrore, richiesta, annullReason);
    }

    private void setAnnullata(String cdErrore, AroRichiestaRa richiesta, String annullReason) throws ParerUserError {
        logger.debug("setAnnullata...");
        // il sistema assegna alla richiesta stato = ANNULLATO nella tabella ARO_RICHIESTA_RA
        richiesta.setTiStato(AroRichiestaTiStato.ANNULLATO);
        logger.debug("Richiesta id = {} settata con stato {} per '{}'", richiesta.getIdRichiestaRa(),
                AroRichiestaTiStato.ANNULLATO, annullReason);
        // il sistema definisce sulla richiesta la data di fine ed il motivo di annullamento pari a "Richiesta scaduta"
        Date systemDate = new Date();
        richiesta.setTsFine(systemDate);
        richiesta.setCdErrore(cdErrore);
        richiesta.setNote(annullReason);
    }

    /**
     * Esegue il salvataggio in transazione del nuovo record di richiesta di restituzione archivio
     *
     * @param idUserIam
     *            utente che crea la richiesta di restituzione archivio
     * @param idStrut
     *            la struttura per cui viene creata la richiesta
     * @param priorita
     *            la priorità con cui eseguire l'operazione
     * @param tiRichRestArch
     *            tipo richiesta archiviazione
     *
     * @return id richiesta
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveRichRestArch(long idUserIam, BigDecimal idStrut, String priorita, String tiRichRestArch)
            throws ParerUserError {
        logger.info("Eseguo il salvataggio della richiesta di restituzione archivio");
        Date now = Calendar.getInstance().getTime();
        Long idRich = null;
        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
            IamUser user = helper.findById(IamUser.class, idUserIam);

            // Preparo la richiesta da registrare
            AroRichiestaRa rich = new AroRichiestaRa();
            rich.setTsInizio(now);
            rich.setOrgStrut(strut);
            rich.setTiStato(AroRichiestaTiStato.CALCOLO_AIP_IN_CORSO);
            rich.setPriorita(BigDecimal.valueOf(Long.parseLong(priorita)));
            rich.setIamUser(user);

            helper.insertEntity(rich, true);

            if (tiRichRestArch.equals("UNITA_DOC")) {
                // TODO
            } else if (tiRichRestArch.equals("SERIE")) {
                // TODO
            } else if (tiRichRestArch.equals("FASCICOLI")) {
                // TODO
            }

            logger.info("Salvataggio della richiesta di restituzione archivio completato");
            idRich = rich.getIdRichiestaRa();
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio della richiesta di restituzione archivio : {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta di restituzione archivio");
        }
        return idRich;
    }
    // </editor-fold>

    // <editor-fold defaultstate="expand" desc="Funzioni Online">
    /**
     * Ricerca online - Ritorna il tablebean di risultati dati i filtri richiesti
     *
     * @param idUser
     *            id utente che ha eseguito la ricerca
     * @param filtri
     *            parametri della richiesta
     *
     * @return tablebean AroVRicRichRaTableBean
     */
    public AroVRicRichRaTableBean getAroVRicRichRaTableBean(long idUser, RicercaRichRestArchBean filtri) {
        AroVRicRichRaTableBean table = new AroVRicRichRaTableBean();
        try {
            List<Long> idEnteConvenzList = helper.retrieveOrgEnteSiamList(filtri);
            if (idEnteConvenzList != null && !idEnteConvenzList.isEmpty()) {
                List<AroVRicRichRa> list = helper.retrieveAroVRicRichRa(filtri, idEnteConvenzList);
                if (list != null && !list.isEmpty()) {
                    for (AroVRicRichRa record : list) {
                        AroVRicRichRaRowBean row = (AroVRicRichRaRowBean) Transform.entity2RowBean(record);
                        table.add(row);
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            logger.error("Errore durante il recupero delle richieste di restituzione archivio {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
        }
        return table;
    }

    /**
     * Carica il dettaglio richiesta di restituzione archivio dato l'id richiesta
     *
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     * @param idStrut
     *            id struttura
     *
     * @return rowBean della vista
     */
    public AroRichiestaRaRowBean getAroRichiestaRaRowBean(BigDecimal idRichRestArch, BigDecimal idStrut) {
        AroRichiestaRa richiesta = helper.findById(AroRichiestaRa.class, idRichRestArch);
        OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
        AroRichiestaRaRowBean row = null;
        if (richiesta != null) {
            SIOrgEnteSiam enteConvenz = helper.findById(SIOrgEnteSiam.class,
                    richiesta.getOrgStrut().getIdEnteConvenz());
            try {
                row = (AroRichiestaRaRowBean) Transform.entity2RowBean(richiesta);
                row.setString("nm_ente_convenz", enteConvenz.getNmEnteSiam());
                row.setString("nm_ente_strut",
                        richiesta.getOrgStrut().getOrgEnte().getNmEnte() + " - " + strut.getNmStrut());
                if (richiesta.getIamUser() != null) {
                    row.setString("nm_userid", richiesta.getIamUser().getNmUserid());
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della richiesta di restituzione archivio {}",
                        ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero della richiesta di restituzione archivio");
            }
        }
        return row;
    }

    public AroVLisItemRaTableBean getAroVLisItemRaTableBean(BigDecimal idRichRestArch, BigDecimal idStrut) {
        AroVLisItemRaTableBean table = new AroVLisItemRaTableBean();
        List<AroVLisItemRa> list = helper.getAroVLisItemRa(idRichRestArch, idStrut);
        if (list != null && !list.isEmpty()) {
            try {
                for (AroVLisItemRa record : list) {
                    AroVLisItemRaRowBean row = (AroVLisItemRaRowBean) Transform.entity2RowBean(record);
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di item della richiesta di restituzione archivio {}",
                        ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della lista di item della richiesta di restituzione archivio");
            }
        }
        return table;
    }

    /**
     * Esegue il controllo degli item della richiesta data come parametro
     *
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     *
     * @throws ParerUserError
     *             Errore imprevisto
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void controlloItemOnline(BigDecimal idRichRestArch) throws ParerUserError {
        AroRichiestaRa rich = helper.findById(AroRichiestaRa.class, idRichRestArch);
        try {
            // Aggiorno tutti gli errori rilevati sugli item della richiesta
            List<AroAipRestituzioneArchivio> itemsRichRestArchList = helper
                    .retrieveAroErrItemRestArch(idRichRestArch.longValue());
            for (AroAipRestituzioneArchivio item : itemsRichRestArchList) {
                item.setTiStato(TiStatoAroAipRa.DA_ELABORARE);
            }
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista durante il controllo degli item della richiesta ";
            ParerUserError parerUserError = new ParerUserError(messaggio);
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw parerUserError;
        }
    }

    /**
     * Esegue la modifica della richiesta di restituzione archivio
     *
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     * @param priorita
     *            valore priorita
     * @param idStrut
     *            struttura di riferimento della richiesta
     *
     * @throws ParerUserError
     *             errore imprevisto
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveRichRestArch(BigDecimal idRichRestArch, String priorita, BigDecimal idStrut) throws ParerUserError {
        try {
            AroRichiestaRa richRestArch = helper.findByIdWithLock(AroRichiestaRa.class, idRichRestArch);
            // Verifica lo stato corrente della richiesta
            if (!richRestArch.getTiStato().equals(AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE)) {
                throw new ParerUserError(
                        "La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da "
                                + AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name());
            }
            richRestArch.setPriorita(BigDecimal.valueOf(Long.parseLong(priorita)));
        } catch (ParerUserError ex) {
            throw ex;
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio della richiesta di restituzione archivio : {}",
                    ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio della richiesta restituzione archivio");
        }
    }

    /**
     * Esegue l'eliminazione della richiesta di restituzione archivio
     *
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     *
     * @throws ParerUserError
     *             errore imprevisto
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteRichRestArch(BigDecimal idRichRestArch) throws ParerUserError {
        AroRichiestaRa richRestArch = helper.findByIdWithLock(AroRichiestaRa.class, idRichRestArch);
        // Verifica lo stato corrente della richiesta
        if (!richRestArch.getTiStato().equals(AroRichiestaTiStato.ANNULLATO)
                && !richRestArch.getTiStato().equals(AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE)) {
            throw new ParerUserError("La richiesta non \u00E8 modificabile perch\u00E9 ha stato corrente diverso da "
                    + AroRichiestaTiStato.ANNULLATO.name() + ", " + AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name());
        }
        helper.removeEntity(richRestArch, false);
    }

    /**
     * Esegue il cambio di stato per una specifica richiesta
     *
     * @param idUserIam
     *            id dell'utente che esegue il cambio stato
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     * @param tiStatoRichRestArchOld
     *            stato attuale della richiesta
     * @param tiStatoRichRestArchNew
     *            stato da assumere
     * @param dsNotaRichRestArch
     *            nota
     *
     * @throws ParerUserError
     *             errore imprevisto
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void cambiaStato(long idUserIam, BigDecimal idRichRestArch, String tiStatoRichRestArchOld,
            String tiStatoRichRestArchNew, String dsNotaRichRestArch) throws ParerUserError {
        AroRichiestaRa richRestArch = helper.findByIdWithLock(AroRichiestaRa.class, idRichRestArch);
        IamUser iamUser = helper.findById(IamUser.class, idUserIam);

        // Verifica lo stato corrente della richiesta
        // if (!richRestArch.getTiStato().name().equals(tiStatoRichRestArchOld)) {
        // throw new ParerUserError("La richiesta ha cambiato stato");
        // }
        richRestArch.setTiStato(AroRichiestaTiStato.valueOf(tiStatoRichRestArchNew));
        logger.debug("Richiesta id = {} settata con stato {}", richRestArch.getIdRichiestaRa(), tiStatoRichRestArchNew);
        // il sistema definisce sulla richiesta la data di fine e la nota relativa al cambio stato
        Date systemDate = new Date();
        richRestArch.setTsFine(systemDate);
        richRestArch.setNote(dsNotaRichRestArch);
    }
    // </editor-fold>

    /**
     * Verifica che la richiesta abbia uno degli stati elencati
     *
     * @param idRichRestArch
     *            id della richiesta
     * @param statiRichiesta
     *            stati da verificare
     *
     * @return true se la richiesta ha stato uguale a uno di quelli in elenco
     */
    public boolean checkStatoRichiesta(BigDecimal idRichRestArch, AroRichiestaTiStato... statiRichiesta) {
        boolean result = false;

        AroRichiestaRa rich = helper.findById(AroRichiestaRa.class, idRichRestArch);
        if (rich != null) {
            if (statiRichiesta != null) {
                for (AroRichiestaTiStato stato : statiRichiesta) {
                    if (rich.getTiStato().equals(stato)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public boolean checkStatoItems(BigDecimal idRichRestArch, TiStatoAroAipRa... statiItems) {
        Long count = helper.countAroItemRichRestArch(idRichRestArch, statiItems);
        return count > 0L;
    }

    /**
     * Ritorna il numero di items all'interno della richiesta con id <code>idRichRestArch</code> con gli stati elencati
     *
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     * @param statiItems
     *            stati da controllare
     *
     * @return il numero di items
     */
    public Long countItemsInRichRestArch(BigDecimal idRichRestArch, TiStatoAroAipRa... statiItems) {
        Long count = helper.countAroItemRichRestArch(idRichRestArch, statiItems);
        return count;
    }

    /**
     * Ritorna il numero di items all'interno della richiesta con id <code>idRichRestArch</code>
     *
     * @param idRichRestArch
     *            id della richiesta di restituzione archivio
     *
     * @return il numero di items
     */
    public Long countItemsInRichRestArch(BigDecimal idRichRestArch) {
        Long count = helper.countAroItemRichRestArch(idRichRestArch, new TiStatoAroAipRa[] {});
        return count;
    }

    public long getStrutFirstStateRich(BigDecimal idRichRestArch) {
        return helper.getIdStrutFirstStateRich(idRichRestArch);
    }
}
