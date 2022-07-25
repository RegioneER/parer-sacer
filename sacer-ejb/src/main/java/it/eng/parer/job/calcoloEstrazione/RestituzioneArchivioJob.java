package it.eng.parer.job.calcoloEstrazione;

import it.eng.parer.async.ejb.CalcoloEstrazioneAsync;
import it.eng.parer.async.helper.CalcoloEstrazioneHelper;
import it.eng.parer.async.utils.IOUtils;
import it.eng.parer.async.utils.UdSerFascObj;
import it.eng.parer.async.utils.io.AllFileFilter;
import it.eng.parer.entity.AroAipRestituzioneArchivio;
import it.eng.parer.entity.AroIndiceAipUd;
import it.eng.parer.entity.AroRichiestaRa;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;
import it.eng.parer.entity.constraint.AroAipRestituzioneArchivio.TiStatoAroAipRa;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.AroVChkAipRestArchUd;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.RecuperoWeb;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoLite.security.User;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class RestituzioneArchivioJob {

    @Resource
    private SessionContext context;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private CalcoloEstrazioneAsync calcoloAsync;
    @EJB
    private CalcoloEstrazioneHelper calcoloHelper;
    @EJB
    private ConfigurationHelper configurationHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestituzioneArchivioJob.class);

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");

    public void restituzioneArchivio(LogJob logJob) throws ParerInternalError, Exception {
        // boolean success = calcoloAsync.eseguiRichiestaAsync();
        // if (success) {
        // LOGGER.info(JobConstants.JobEnum.CALCOLA_ESTRAZIONE_JOB.name() + " --- Richiesta OK");
        // } else {
        // LOGGER.info(JobConstants.JobEnum.CALCOLA_ESTRAZIONE_JOB.name() + " --- Richiesta KO");
        // //throw new ParerInternalError("Impossibile effettuare le richieste");
        // }

        /* Determino la directory root $ROOT_FOLDER_EC_RA */
        String rootFolderEcRaPath = configurationHelper.getValoreParamApplic("ROOT_FOLDER_EC_RA", null, null, null,
                null, CostantiDB.TipoAplVGetValAppart.APPLIC);

        // MEV #26985 - Creazione automatica in FTP della cartella creazione archivio
        // Creo la directory root
        createEmptyDir(rootFolderEcRaPath);

        // String rootFolderEcRaPath = "/tmp";
        /* Determino il numero massimo di AIP che si possono processare per ogni esecuzione del job */
        int maxUd2procRa = Integer.parseInt(configurationHelper.getValoreParamApplic("MAX_UD2PROC_RA", null, null, null,
                null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        /* Inizializzo numero massimo di AIP da processare per ogni esecuzione del job a 0 */
        int totAipEstratti = 0;
        /* Determino il numero massimo di file che si possono copiare in una cartella */
        int numMaxFileFolderRa = Integer.parseInt(configurationHelper.getValoreParamApplic("NUM_MAX_FILE_FOLDER_RA",
                null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        /* Inizializzo numero massimo di file copiati per ogni cartella a 0 */
        int totFileCopiati = 0;
        /* Inizializzo la mappa dei numeri progressivi da concatenare alla cartella da rinominare */
        Map<String, Integer> mappaProgressivi = new HashMap<>();
        /* Verifico la directory $ROOT_FOLDER_EC_RA */
        if (!IOUtils.exists(rootFolderEcRaPath)) {
            LOGGER.debug("Cartella per l’estrazione degli AIP non definita");
            /* Ritorno un messaggio di errore che dice che la directory non esiste */
            throw new ParerInternalError("Cartella per l’estrazione degli AIP non definita");
        }
        /*
         * Determino le richieste di estrazione con maggiore priorità di esecuzione, con stato ESTRAZIONE_IN_CORSO o
         * IN_ATTESA_ESTRAZIONE; le richieste sono selezionate in ordine di priorita DESC e data fine ASC
         */
        if (totAipEstratti < maxUd2procRa) {
            List<AroRichiestaRa> richiesteRa = calcoloHelper.retrieveRichiesteRaDaElab();
            for (AroRichiestaRa richiesta : richiesteRa) {
                manageRichiestaEstrazioneJob(richiesta.getIdRichiestaRa(), rootFolderEcRaPath, totFileCopiati,
                        mappaProgressivi, numMaxFileFolderRa, maxUd2procRa, logJob);
            }
        }

        LOGGER.info(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name() + " --- Fine schedulazione job");
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    public static void createEmptyDir(String fullPath) throws IOException {
        Path dirPath = Paths.get(fullPath);
        File directory = dirPath.toFile();
        if (directory.exists()) {
            LOGGER.debug("La cartella {} esiste, la dobbiamo svuotare", fullPath);
            File[] files = directory.listFiles((dir, name) -> {
                boolean toDelete = !name.matches("\\.nfs.+");
                LOGGER.debug("File {} lo devo cancellare? {}", name, toDelete);
                return toDelete;
            });
            if (files != null) {
                for (File file : files) {
                    LOGGER.debug("Procedo alla cancellazione di {}", file.getAbsolutePath());
                    FileUtils.forceDelete(file);
                }
            }
        } else {
            LOGGER.debug("La cartella {} non esiste, la creo", fullPath);
            Files.createDirectories(dirPath);
        }
    }

    public void manageRichiestaEstrazioneJob(long idRichiestaRa, String rootFolderEcRaPath, int totFileCopiati,
            Map<String, Integer> mappaProgressivi, int numMaxFileFolderRa, int maxUd2procRa, LogJob logJob)
            throws Exception {
        boolean isAnnullata = false;

        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        // gestisco le altre estrazioni in corso
        elaboraEstrazioniInCorso(richiesta.getOrgStrut().getIdStrut(), logJob.getIdLogJob());

        LOGGER.debug(
                "Richiesta della struttura '" + richiesta.getOrgStrut().getNmStrut() + "' trovata: stato richiesta = '"
                        + richiesta.getTiStato().name() + "' (id richiesta = '" + richiesta.getIdRichiestaRa() + "')");

        // ricavo l'ente convenzionato e la directory di destinazione
        SIOrgEnteSiam enteConvenz = calcoloHelper
                .retrieveOrgEnteConvenzById(richiesta.getOrgStrut().getIdEnteConvenz());
        // MEV #26987 - Normalizzazione nome ente: portato in maiuscolo,
        // tolti gli accenti, sostituiti gli spazi e i caratteri speciali con "_"
        String nmEnteSiamNormalizzato = StringUtils.stripAccents(enteConvenz.getNmEnteSiam().toUpperCase())
                .replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9]", "_");
        String childFolderEcRaPath = IOUtils.getPath(rootFolderEcRaPath, nmEnteSiamNormalizzato);
        // MEV #26985 - Creazione automatica in FTP della cartella creazione archivio
        // Creo la directory childFolder
        createEmptyDir(childFolderEcRaPath);
        if (!IOUtils.isFileReady(childFolderEcRaPath)) {
            LOGGER.debug(
                    "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi necessari in lettura/scrittura");
            /*
             * Ritorno un messaggio di errore che dice che la directory non esiste o non si hanno i permessi necessari
             * in lettura/scrittura
             */
            throw new ParerInternalError(
                    "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi necessari in lettura/scrittura");
        }

        /* Tip: Per ottimizzare l'estrazione bisogna ordinare il result set. Per ora gestisco i file in ordine sparso */
        // Comparator comp = new UdSerFascObjComparatorDtCreazione();

        /* Determino le Unità Documentarie, le Serie e i Fascicoli che soddisfano la richiesta corrente */
        List<UdSerFascObj> udSerFascObjectList = calcoloHelper.retrieveAipUdSerFascByRichiesta(richiesta, maxUd2procRa);
        LOGGER.debug("Trovati " + udSerFascObjectList.size() + " oggetti da estrarre per l'ente convenzionato '"
                + enteConvenz.getNmEnteSiam() + "'");

        /* Tip: Per ottimizzare l'estrazione bisogna ordinare il result set. Per ora gestisco i file in ordine sparso */
        // Collections.sort(udSerFascObjectList, comp);

        RestituzioneArchivioJob newCalcoloEstrazioneJobRef1 = context.getBusinessObject(RestituzioneArchivioJob.class);
        // aggiorno stato della richiesta di restituzione archivio corrente in ESTRAZIONE_IN_CORSO
        newCalcoloEstrazioneJobRef1.setStatoRichiestaRaAtomic(idRichiestaRa, logJob.getIdLogJob());
        // pulisco la directory prima di iniziare una nuova estrazione
        AroVChkAipRestArchUd chkAipRestArchUd = calcoloHelper.findViewById(AroVChkAipRestArchUd.class,
                BigDecimal.valueOf(richiesta.getIdRichiestaRa()));
        if (chkAipRestArchUd.getFlAllDaElab().equals("1")) {
            List<String> files = IOUtils.list(childFolderEcRaPath, AllFileFilter.getInstance(), false);
            for (String file : files) {
                File entry = new File(file);
                if (entry.isDirectory()) {
                    IOUtils.deleteDir(file, true);
                } else {
                    // a questo livello non dovrebbero esistere file ma solo directory
                    // IOUtils.delete(entry);
                }
            }
        }

        boolean isTheFirst = true;
        try {
            // Itero l'insieme
            Iterator i = udSerFascObjectList.iterator();
            while (i.hasNext()) {
                // Recupera l'elemento e sposta il cursore all'elemento successivo
                UdSerFascObj o = (UdSerFascObj) i.next();
                // Nota: il controllo sull'iteratore (!i.hasNext(), "se non ho altri elementi"), mi serve per capire se
                // è l'ultimo elemento
                totFileCopiati = newCalcoloEstrazioneJobRef1.manageUdSerFascObjFase2(richiesta.getIdRichiestaRa(),
                        enteConvenz.getIdEnteSiam(), logJob.getIdLogJob(), o, !i.hasNext(), isTheFirst,
                        rootFolderEcRaPath, totFileCopiati, mappaProgressivi, numMaxFileFolderRa);
                if (totFileCopiati > numMaxFileFolderRa) {
                    totFileCopiati = totFileCopiati % numMaxFileFolderRa; // calcolo il modulo
                }
                /* Verifico se la richiesta corrente è stata annullata */
                isAnnullata = newCalcoloEstrazioneJobRef1.checkRichiestaAnnullata(richiesta.getIdRichiestaRa());
                if (isAnnullata) {
                    // pulisco la directory se l'estrazione viene annullata
                    // List<String> files = IOUtils.list(childFolderEcRaPath, AllFileFilter.getInstance(), false);
                    // for (String file : files) {
                    // File entry = new File( file );
                    // if (entry.isDirectory()) {
                    // IOUtils.deleteDir(file, true);
                    // } else {
                    // //a questo livello non dovrebbero esistere file ma solo directory
                    // //IOUtils.delete(entry);
                    // }
                    // }
                    break;
                }
                isTheFirst = false;
            }
        } catch (ParerInternalError ex) {
            LOGGER.warn("Attenzione: possibile errore. Salto alla richiesta di restituzione archivio successiva");
        }

        if (!isAnnullata) {
            /* Cambio stato alla richiesta corrente */
            newCalcoloEstrazioneJobRef1.setStatoRichiestaRaAtomic(idRichiestaRa, logJob.getIdLogJob());
        }

        // TODO: elaboraFolderVuote(idStruttura, logJob.getIdLogJob());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Integer manageUdSerFascObjFase2(long idRichiestaRa, long idEnteConvenz, long idLogJob,
            UdSerFascObj udSerFascObj, boolean isTheLast, boolean isTheFirst, String rootFolderEcRaPath,
            int totFileCopiati, Map<String, Integer> mappaProgressivi, int numMaxFileFolderRa) throws Exception {
        Integer totFileCopiatiTmp = null;

        switch (udSerFascObj.getTiEntitaSacer()) {
        case UNI_DOC:
            totFileCopiatiTmp = manageUdFase2(udSerFascObj.getId(), idRichiestaRa, idEnteConvenz, idLogJob, isTheLast,
                    isTheFirst, rootFolderEcRaPath, totFileCopiati, mappaProgressivi, numMaxFileFolderRa);
            break;
        case SER:
            // totFileCopiatiTmp = manageSerFase2(udSerFascObj.getId(), udSerFascObj.getDtCreazione(), idEnte, idLogJob,
            // isTheLast, isTheFirst);
            break;
        case FASC:
            // totFileCopiatiTmp = manageFascFase2(udSerFascObj.getId(), udSerFascObj.getDtCreazione(), idEnte,
            // idLogJob, isTheLast, isTheFirst);
            break;
        }

        return totFileCopiatiTmp;
    }

    public Integer manageUdFase2(BigDecimal idAipRestArchivio, long idRichiestaRa, long idEnteConvenz, long idLogJob,
            boolean isTheLast, boolean isTheFirst, String rootFolderEcRaPath, int totFileCopiati,
            Map<String, Integer> mappaProgressivi, int numMaxFileFolderRa) throws Exception {
        File folder = null;
        String codiceErrore;
        LogJob logJob = calcoloHelper.retrieveLogJobById(idLogJob);
        AroRichiestaRa richiestaRa = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        AroAipRestituzioneArchivio aipRestArchivio = calcoloHelper
                .retrieveAroAipRestituzioneArchivioById(idAipRestArchivio.longValue());
        AroIndiceAipUd indiceAipUd = calcoloHelper
                .retrieveIndiceAipUdById(aipRestArchivio.getAroIndiceAipUd().getIdIndiceAip());
        AroUnitaDoc ud = calcoloHelper.retrieveUnitaDocById(indiceAipUd.getAroUnitaDoc().getIdUnitaDoc());
        SIOrgEnteSiam enteConvenz = calcoloHelper.retrieveOrgEnteConvenzById(BigDecimal.valueOf(idEnteConvenz));

        BigDecimal aaKeyUnitaDoc = ud.getAaKeyUnitaDoc();

        /* Prendo il LOCK esclusivo su ud e sull'AIP in elaborazione */
        calcoloHelper.lockUnitaDoc(ud);
        calcoloHelper.lockIndiceAipUd(indiceAipUd);

        // ricavo la struttura
        OrgStrut struttura = calcoloHelper.retrieveOrgStrutById(BigDecimal.valueOf(ud.getOrgStrut().getIdStrut()));
        LOGGER.debug("processo struttura: " + struttura.getIdStrut());
        /* Determino la directory $NM_ENTE_CONVENZIONATO, figlia di $ROOT_FOLDER_EC_RA */
        String childFolderEcRaPath = IOUtils.getPath(rootFolderEcRaPath,
                enteConvenz.getNmEnteSiam().toUpperCase().replaceAll(" ", "_"));
        if (!IOUtils.isFileReady(childFolderEcRaPath)) {
            LOGGER.debug(
                    "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi in lettura/scrittura");
            /*
             * Ritorno un messaggio di errore che dice che la directory non esiste o non si hanno i permessi in
             * lettura/scrittura
             */
            throw new ParerInternalError(
                    "Cartella per l’estrazione degli AIP non definita o non si hanno i permessi in lettura/scrittura");
        }
        /* Definisco la folder relativa all'ente versante */
        String pathByEnte = IOUtils.getPath(childFolderEcRaPath, struttura.getOrgEnte().getNmEnte());
        /* Definisco la folder relativa alla struttura */
        String pathByStrut = IOUtils.getPath(pathByEnte, (struttura.getCdStrutNormaliz() != null)
                ? struttura.getCdStrutNormaliz() : struttura.getNmStrut().toUpperCase().replaceAll(" ", "_"));
        /* Definisco la folder relativa al tipo oggetto */
        String pathByTipo = IOUtils.getPath(pathByStrut, "Unita_documentarie"); // TODO: definire una classe di costanti
                                                                                // (CostantiRA.UD)
        /* Definisco la folder corrente per la scrittura */
        String pathByAnno = IOUtils.getPath(pathByTipo, aaKeyUnitaDoc.toString());
        /* Definisco folder corrente */
        String path = IOUtils.getDirWithProgressive(pathByAnno, mappaProgressivi);

        if (!IOUtils.exists(path)) {
            // non c'è la folder ne creo una nuova e azzero il contatore dei file copiati
            folder = IOUtils.newDirectory(path);
            totFileCopiati = 0;
        } else {
            folder = IOUtils.getFile(path);
            // Gestendo i file in ordine sparso devo ricalcolare il numero di file della folder corrente
            totFileCopiati = IOUtils.list(folder, AllFileFilter.getInstance(), false).size();
        }

        /* Tip: Per ottimizzare l'estrazione bisogna ordinare il result set. Per ora gestisco i file in ordine sparso */
        // if (isTheFirst && !IOUtils.isEmpty(folder)) {
        // // calcolo il numero di file della folder corrente
        // totFileCopiati = IOUtils.list(folder, AllFileFilter.getInstance(), false).size();
        // }

        // ATTENZIONE: verifico se il numero di file della folder corrente,
        // è inferiore o uguale al numero di file che la folder corrente può ancora includere
        // (tale numero è definito dal parametro per il numero massimo di file previsto per ogni folder a cui si
        // vuole aggiungere il file derivante dall'elaborazione della richiesta per la struttura corrente)
        LOGGER.debug("aggiungo il pacchetto dell'indice aip unità documentaria '" + indiceAipUd.getIdIndiceAip()
                + "' nella folder corrente");
        // Scarico pacchetto indice aip
        RispostaWSRecupero rispostaWs = scarica_xml_unisincro(richiestaRa, ud);
        switch (rispostaWs.getSeverity()) {
        case OK:
            boolean isCopied = false;
            String srcFilePath = rispostaWs.getRifFileBinario().getFileSuDisco().getPath();
            String fileName = rispostaWs.getNomeFile();

            while (!isCopied) {
                if (totFileCopiati < numMaxFileFolderRa) { // il pacchetto aip ud va nella folder: aggiungo.
                    LOGGER.debug("aggiungo il pacchetto dell'indice aip unità documentaria '"
                            + indiceAipUd.getIdIndiceAip() + "' nella folder corrente");
                    /* Copio pacchetto indice aip nella folder corrente */
                    isCopied = copyFile(srcFilePath, fileName, folder.getPath(), fileName, true);
                } else {
                    /* Aggiungo il progressivo alla mappa */
                    Integer prg = mappaProgressivi.getOrDefault(IOUtils.getFilename(pathByAnno), 0);
                    mappaProgressivi.put(IOUtils.getFilename(pathByAnno), ++prg);
                    /* Definisco folder corrente */
                    path = IOUtils.getDirWithProgressive(pathByAnno, mappaProgressivi);

                    if (!IOUtils.exists(path)) {
                        // non c'è la folder quindi ne creo una nuova
                        folder = IOUtils.newDirectory(path);
                        totFileCopiati = 0;
                    } else {
                        folder = IOUtils.getFile(path);
                        // devo ricalcolare il numero di file della folder corrente
                        totFileCopiati = IOUtils.list(folder, AllFileFilter.getInstance(), false).size();
                    }
                }
            }
            if (isCopied) {
                totFileCopiati++;
                BigDecimal dim = new BigDecimal(IOUtils.readFileAsBytes(folder.getPath(), fileName).length);
                // Assegno all'oggetto aip restituzione archivio in elaborazione stato = ESTRATTO definendo anche la
                // data di estrazione e la dimensione del pacchetto salvato in byte
                setStatoAipRestArchivio(aipRestArchivio, TiStatoAroAipRa.ESTRATTO, dim, new Date());
            } else {
                codiceErrore = "Errore durante il tentativo di copia. File non trovato";
                LOGGER.warn(codiceErrore);
                setErrore(codiceErrore, aipRestArchivio, struttura, logJob);
            }

            break;
        case WARNING:
            codiceErrore = rispostaWs.getErrorMessage();
            LOGGER.warn(codiceErrore);
            setErrore(codiceErrore, aipRestArchivio, struttura, logJob);
            break;
        case ERROR:
            codiceErrore = rispostaWs.getErrorMessage();
            LOGGER.warn(codiceErrore);
            setErrore(codiceErrore, aipRestArchivio, struttura, logJob);
            break;
        }

        // TODO: Se l'elemento corrente è l'ultimo e se la folder corrente è vuota
        // if (isTheLast && IOUtils.isEmpty(folder)) {
        // IOUtils.delete(folder);
        // }

        return totFileCopiati;
    }

    /**
     * Metodo per fare il download dei componenti di tipo file di un determinato documento in un file zip
     * 
     * @param richiesta
     *            entity AroRichiestaRa
     * @param ud
     *            entity AroUnitaDoc
     * 
     * @return RispostaWSRecupero oggetto con risposta recupero
     * 
     * @throws Exception
     *             errore generico
     */
    public RispostaWSRecupero scarica_xml_unisincro(AroRichiestaRa richiesta, AroUnitaDoc ud) throws Exception {
        User utente = new User();
        utente.setUsername(richiesta.getIamUser().getNmUserid());
        utente.setIdUtente(richiesta.getIamUser().getIdUserIam());

        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore().setAmbiente(richiesta.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        recupero.getVersatore().setEnte(richiesta.getOrgStrut().getOrgEnte().getNmEnte());
        recupero.getVersatore().setStruttura(richiesta.getOrgStrut().getNmStrut());
        recupero.getVersatore().setUserID(utente.getIdUtente() + "");
        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        recupero.getChiave().setAnno(ud.getAaKeyUnitaDoc().toBigInteger());
        recupero.getChiave().setNumero(ud.getCdKeyUnitaDoc());

        BigDecimal idUnitaDoc = BigDecimal.valueOf(ud.getIdUnitaDoc());
        String tipoSaveFile = calcoloHelper
                .getTipoSaveFile(BigDecimal.valueOf(ud.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
        CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile.valueOf(tipoSaveFile);
        CostantiDB.TipiEntitaRecupero tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO;
        // EVO#20972
        // In questa fase in cui si sta richiedendo il
        // recupero per la generazione del pacchetto AIP,
        // è necessario gestire coerentemente il parametro
        // del servizio di recupero in base alla versione Unisincro con cui
        // è stato prodotto l'ultimo Indice AIP (se presente), perchè il pacchetto AIP viene generato in
        // modo differente impostando il valore
        // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X).
        AroVerIndiceAipUd verIndiceAipUd = calcoloHelper.retrieveLastVerIndiceAIPByIdUd(ud.getIdUnitaDoc());
        if (verIndiceAipUd != null) {
            // Scompatto il campo cdVerIndiceAip
            String[] numbers = verIndiceAipUd.getCdVerIndiceAip().split("[.]");
            int majorNumber = Integer.parseInt(numbers[0]);
            if (majorNumber > 0) {
                tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2;
            }
        }
        // end EVO#20972
        RecuperoWeb recuperoUD = new RecuperoWeb(recupero, utente, idUnitaDoc, tipoSalvataggioFile, tipoEntitaRecupero);
        RispostaWSRecupero rispostaWs = recuperoUD.recuperaOggetto();

        return rispostaWs;
    }

    public boolean copyFile(String srcFilePath, String srcFileName, String destFilePath, String destFileName,
            boolean isDeleteFile) throws Exception {
        boolean isCopied = false;

        if (srcFilePath != null && srcFileName != null) {
            if (IOUtils.exists(srcFilePath)) {
                if (IOUtils.copyFile(IOUtils.extractPath(srcFilePath), IOUtils.getFilename(srcFilePath), destFilePath,
                        destFileName)) {
                    // Nel caso sia stato richiesto, elimina il file
                    if (isDeleteFile) {
                        File fileToDownload = IOUtils.getFile(srcFilePath);
                        IOUtils.delete(fileToDownload, true);
                    }
                    isCopied = true;
                }
            }
        }
        return isCopied;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setStatoRichiestaRaAtomic(long idRichiestaRa, long idLogJob) throws Exception {
        LOGGER.debug("setStatoRichiestaRaAtomic...");
        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        LogJob logJob = calcoloHelper.retrieveLogJobById(idLogJob);
        setStatoRichiestaRa(richiesta, logJob);
    }

    private void setStatoRichiestaRa(AroRichiestaRa richiesta, LogJob logJob) throws Exception {
        LOGGER.debug("setStatoRichiestaRa...");
        Date systemDate = new Date();
        richiesta.setTsFine(systemDate);
        AroVChkAipRestArchUd chkAipRestArchUd = calcoloHelper.findViewById(AroVChkAipRestArchUd.class,
                BigDecimal.valueOf(richiesta.getIdRichiestaRa()));
        if (chkAipRestArchUd.getFlEstratto().equals("1")) {
            // il sistema assegna alla richiesta stato = ESTRATTO nella tabella ARO_RICHIESTA_RA
            richiesta.setTiStato(AroRichiestaTiStato.ESTRATTO);
        } else if (chkAipRestArchUd.getFlErrore().equals("1")) {
            // il sistema assegna alla richiesta stato = ERRORE nella tabella ARO_RICHIESTA_RA
            richiesta.setTiStato(AroRichiestaTiStato.ERRORE);
        } else if (chkAipRestArchUd.getFlDaElaborare().equals("1")) {
            // il sistema assegna alla richiesta stato = ESTRAZIONE_IN_CORSO nella tabella ARO_RICHIESTA_RA
            richiesta.setTiStato(AroRichiestaTiStato.ESTRAZIONE_IN_CORSO);
        }
    }

    public void elaboraEstrazioniInCorso(long idStrut, long idLogJob) throws Exception {
        LOGGER.debug("Controllo se ci sono altre richieste di estrazione in corso");
        // determino le altre richieste di estrazioni appartenenti ad altre strutture (per la struttura corrente non
        // dovrebbero esistere),
        // le cui occorrenze sulla ARO_RICHIESTA_RA siano con stato ESTRAZIONE_IN_CORSO
        List<Long> richiesteEstrazioniInCorso = calcoloHelper.retrieveRichiesteEstrazioniInCorso(idStrut);
        RestituzioneArchivioJob newCalcoloEstrazioneJobRef1 = context.getBusinessObject(RestituzioneArchivioJob.class);

        LOGGER.info("trovate " + richiesteEstrazioniInCorso.size()
                + " richieste di estrazioni in corso da settare in stato = IN_ATTESA_ESTRAZIONE");
        for (Long richiestaId : richiesteEstrazioniInCorso) {
            LOGGER.debug("trovata richiesta " + richiestaId + " in corso da settare in stato = IN_ATTESA_ESTRAZIONE");
            newCalcoloEstrazioneJobRef1.setDaSospendereAtomic("RICHIESTA SOSPESA", richiestaId, idStrut, idLogJob);
        }
    }

    // controllo se la richiesta è stata annullata
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean checkRichiestaAnnullata(long idRichiestaRa) {
        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        Date actualDate = new Date();
        LOGGER.debug("Verifico se la richiesta '" + richiesta.getIdRichiestaRa() + "' con data inizio "
                + dateToString(richiesta.getTsInizio()) + " risulta annullata all'istante corrente ("
                + dateToString(actualDate) + ")");
        if (richiesta.getTiStato().equals(AroRichiestaTiStato.ANNULLATO)) {
            LOGGER.debug("Richiesta annullata");
            return true;
        } else {
            LOGGER.debug("Richiesta non annullata");
            return false;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDaSospendereAtomic(String waitReason, Long idRichiestaRa, long idStrut, long idLogJob)
            throws Exception {
        LOGGER.debug("setInAttesaAtomic...");
        AroRichiestaRa richiesta = calcoloHelper.retrieveRichiestaById(idRichiestaRa);
        OrgStrut struttura = calcoloHelper.retrieveOrgStrutById(new BigDecimal(idStrut));
        LogJob logJob = calcoloHelper.retrieveLogJobById(idLogJob);
        // calcoloHelper.writeLogElencoVers(elenco, struttura, "", logJob);
        setSospesa(waitReason, richiesta, struttura, logJob);
    }

    private void setSospesa(String waitReason, AroRichiestaRa richiesta, OrgStrut struttura, LogJob logJob)
            throws Exception {
        LOGGER.debug("setSospesa...");
        // il sistema assegna alla richiesta stato = IN_ATTESA_ESTRAZIONE nella tabella ARO_RICHIESTA_RA
        richiesta.setTiStato(AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE);
        LOGGER.debug("Richiesta id = " + richiesta.getIdRichiestaRa() + " settata con stato "
                + AroRichiestaTiStato.IN_ATTESA_ESTRAZIONE.name() + " per '" + waitReason + "'");
        // il sistema definisce sulla richiesta la data di fine (???) ed il motivo dell'attesa pari a "Richiesta
        // sospesa"
        Date systemDate = new Date();
        richiesta.setTsFine(systemDate);
        richiesta.setNote(waitReason);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setStatoAipRestArchivioAtomic(Long idAipRestArchivio, TiStatoAroAipRa tiStato, BigDecimal dim,
            Date dtEstrazione) throws Exception {
        LOGGER.debug("setStatoAipRestArchivioAtomic...");
        AroAipRestituzioneArchivio aipRestArchivio = calcoloHelper
                .retrieveAroAipRestituzioneArchivioById(idAipRestArchivio);
        setStatoAipRestArchivio(aipRestArchivio, tiStato, dim, dtEstrazione);
    }

    public void setStatoAipRestArchivio(AroAipRestituzioneArchivio aroAipRestArchivio, TiStatoAroAipRa tiStato,
            BigDecimal dim, Date dtEstrazione) {
        LOGGER.debug("setStatoAipRestArchivio...");
        aroAipRestArchivio.setTiStato(tiStato);
        LOGGER.debug("Aip restituzione archivio id = " + aroAipRestArchivio.getIdAipRestArchivio()
                + " settato con stato " + TiStatoAroAipRa.ESTRATTO.name());
        aroAipRestArchivio.setDim(dim);
        aroAipRestArchivio.setDtEstrazione(dtEstrazione);
        LOGGER.debug("Data estrazione: " + dateToString(dtEstrazione) + "; dim: " + dim);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setInErroreAtomic(String cdErrore, Long idAipRestArchivio, long idStrut, long idLogJob)
            throws Exception {
        LOGGER.debug("setInErroreAtomic...");
        AroAipRestituzioneArchivio aipRestArchivio = calcoloHelper
                .retrieveAroAipRestituzioneArchivioById(idAipRestArchivio);
        OrgStrut struttura = calcoloHelper.retrieveOrgStrutById(new BigDecimal(idStrut));
        LogJob logJob = calcoloHelper.retrieveLogJobById(idLogJob);
        // calcoloHelper.writeLogElencoVers(elenco, struttura, ElencoEnums.OpTypeEnum.RECUPERA_ELENCO_SCADUTO.name(),
        // logJob);
        setErrore(cdErrore, aipRestArchivio, struttura, logJob);
    }

    private void setErrore(String cdErrore, AroAipRestituzioneArchivio aipRestArchivio, OrgStrut struttura,
            LogJob logJob) throws Exception {
        LOGGER.debug("setErrore...");
        aipRestArchivio.setTiStato(TiStatoAroAipRa.ERRORE);
        LOGGER.debug("Aip restituzione archivio id = " + aipRestArchivio.getIdAipRestArchivio() + " settata con stato "
                + TiStatoAroAipRa.ERRORE.name() + " per '" + cdErrore + "'");
        // il sistema definisce sul record il codice errore
        aipRestArchivio.setCdErrore(cdErrore);
    }

    private String dateToString(Date date) {
        return dateFormat.format(date);
    }

}
