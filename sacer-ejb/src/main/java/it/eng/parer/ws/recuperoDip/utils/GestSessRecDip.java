/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.utils;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.web.util.Constants.TiEntitaSacerObjectStorage;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recuperoDip.dto.CompRecDip;
import it.eng.parer.ws.recuperoDip.ejb.ControlliRecDip;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.FileBinario;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
public class GestSessRecDip {

    private static final Logger log = LoggerFactory.getLogger(GestSessRecDip.class);
    private RispostaWSRecupero rispostaWs;
    private HashCalculator hashCalculator;
    // EJB:
    //
    ControlliRecDip controlliRecDip = null;
    // stateless ejb per la lettura di blob tramite Oracle JDBC
    RecBlbOracle recBlbOracle = null;
    // stateless ejb per i controlli sul db
    ControlliSemantici controlliSemantici = null;
    // EJB
    RecuperoDocumento recuperoDocumento;

    //
    private final static String MIMETYPE_PDF = "application/pdf";

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public GestSessRecDip(RispostaWSRecupero risp) {
        rispostaWs = risp;
        hashCalculator = new HashCalculator();

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                controlliRecDip = (ControlliRecDip) new InitialContext().lookup("java:module/ControlliRecDip");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecDip Errore nel recupero dell'EJB dei controlli recupero DIP  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli  recupero DIP ", ex);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                // recupera l'ejb per la lettura di blob tramite Oracle JDBC
                recBlbOracle = (RecBlbOracle) new InitialContext().lookup("java:module/RecBlbOracle");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecDip Errore nel recupero dell'EJB RecBlbOracle  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB RecBlbOracle ", ex);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                controlliSemantici = (ControlliSemantici) new InitialContext().lookup("java:module/ControlliSemantici");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecupero Errore nel recupero dell'EJB dei controlli semantici  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli semantici ", ex);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            try {
                recuperoDocumento = (RecuperoDocumento) new InitialContext().lookup("java:module/RecuperoDocumento");
            } catch (NamingException ex) {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "GestSessRecDip Errore nel recupero dell'EJB del recupero documento  " + ex.getMessage());
                log.error("Errore nel recupero dell'EJB dei controlli  recupero DIP ", ex);
            }
        }
    }

    public void caricaListaComponenti(RecuperoExt recupero) {
        RispostaControlli rispostaControlli;
        rispostaControlli = controlliRecDip.caricaComponenti(recupero.getParametriRecupero());
        if (rispostaControlli.isrBoolean()) {
            LinkedHashMap<Long, CompRecDip> compRecDips = (LinkedHashMap<Long, CompRecDip>) rispostaControlli
                    .getrObject();
            for (CompRecDip comp : compRecDips.values()) {
                if (comp.isErroreFormatoContenuto()) {
                    comp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    comp.setErrorMessage("Il formato del file versato non è corretto per il convertitore");
                } else if (comp.getTipoAlgoritmoRappresentazione() == CostantiDB.TipoAlgoritmoRappr.ALTRO) {
                    comp.setSeverity(IRispostaWS.SeverityEnum.WARNING);
                    comp.setErrorMessage("Il formato di rappresentazione non prevede una trasformazione.");
                } else {
                    try {
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        // recupero documento blob vs obj storage
                        // build dto per recupero
                        RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.COMP_DOC,
                                comp.getIdCompConvertitore(), bos, RecBlbOracle.TabellaBlob.BLOB);
                        // recupero
                        RispostaControlli rc = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                        /*
                         * RispostaControlli rc = recBlbOracle.recuperaBlobCompSuStream(comp.getIdCompConvertitore(),
                         * bos, RecBlbOracle.TabellaBlob.BLOB);
                         */
                        if (!rc.isrBoolean()) {
                            throw new ParerInternalError(rc.getDsErr());
                        }
                        // String hash = hashCalculator.calculateHash(bos.toByteArray()).toHexBinary();
                        String hash = hashCalculator.calculateHashSHAX(bos.toByteArray(),
                                TipiHash.evaluateByDesc(comp.getDsAlgoHashFileCalc())).toHexBinary();
                        rispostaControlli = controlliRecDip.cercaInsConvertitore(comp, hash, bos.toByteArray());
                        if (!rispostaControlli.isrBoolean()) {
                            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                            break;
                        }
                        comp.setSeverity(IRispostaWS.SeverityEnum.OK);
                    } catch (Exception e) {
                        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                        rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                "GestSessRecDip: Errore caricaListaComponenti DIP  " + e.getMessage());
                        log.error(" Errore caricaListaComponenti DIP ", e);
                    }
                }
            }
            if (rispostaWs.getSeverity() != IRispostaWS.SeverityEnum.ERROR) {
                rispostaWs.getDatiRecuperoDip().setElementiTrovati(compRecDips);
            }
        } else {
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        }
    }

    public void caricaParametri(RecuperoExt recupero) {
        RispostaControlli rispostaControlli;
        HashMap<String, String> imgDefaults = null;

        rispostaControlli = controlliSemantici.caricaDefaultDaDBParametriApplic(CostantiDB.TipoParametroAppl.IMAGE);
        if (rispostaControlli.isrBoolean() == false) {
            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
        } else {
            imgDefaults = (HashMap<String, String>) rispostaControlli.getrObject();
            recupero.setDipRootImg(imgDefaults.get(CostantiDB.ParametroAppl.IMAGE_ROOT_IMAGE_TRASFORM));
        }
    }

    public void recuperaCompConvertito(String outputPath, RecuperoExt recupero,
            LinkedHashMap<Long, CompRecDip> compRecDips) {
        RispostaControlli rispostaControlli;
        CompRecDip comp = compRecDips.get(recupero.getParametriRecupero().getIdComponente());
        FileBinario fileDaScaricare = new FileBinario();
        FileOutputStream tmpOutputStream = null;
        try {
            if (comp.isErroreFormatoContenuto()) {
                comp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                comp.setErrorMessage("Il formato del file versato non è corretto per il convertitore");
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_001_003, comp.getNomeFormatoRappresentazione(),
                        comp.getNomeFileBreve(), comp.getDsFormatoContReale(), comp.getDsFormatoContAtteso());
            } else if (comp.getTipoAlgoritmoRappresentazione() == CostantiDB.TipoAlgoritmoRappr.ALTRO) {
                comp.setSeverity(IRispostaWS.SeverityEnum.WARNING);
                comp.setErrorMessage("Il formato di rappresentazione non prevede una trasformazione.");
                /*
                 * Non sono stati individuati componenti il cui formato di rappresentazione sia compatibile con il
                 * recupero DIP
                 */
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_001_001);
            } else {
                rispostaWs.setNomeFile(comp.getNomeFileBreve() + "." + comp.getEstensioneFile());
                rispostaWs.setMimeType(comp.getMimeType());
                fileDaScaricare.setFileSuDisco(
                        File.createTempFile("output_", "." + comp.getEstensioneFile(), new File(outputPath)));
                tmpOutputStream = new FileOutputStream(fileDaScaricare.getFileSuDisco());
                ///
                rispostaControlli = this.converti(comp, compRecDips, tmpOutputStream, recupero.getDipRootImg());
                if (!rispostaControlli.isrBoolean()) {
                    if (rispostaControlli.getCodErr() == null) {
                        /**
                         * Il convertitore {0} del formato di rappresentazione {1} relativo al componente {2} ha reso un
                         * errore di conversione ed è stato marcato come ERRATO: {3}
                         */
                        rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_002_001,
                                comp.getNomeConvertitore() + "." + comp.getVersioneConvertitore(),
                                comp.getNomeFormatoRappresentazione(), comp.getNomeFileBreve(),
                                rispostaControlli.getDsErr());
                    } else {
                        rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                    }
                }
            }
        } catch (IOException ex) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella generazione del file da scaricare  " + ex.getMessage());
            log.error("Errore nella generazione del file da scaricare ", ex);
        } finally {
            IOUtils.closeQuietly(tmpOutputStream);
            tmpOutputStream = null;
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.ERROR
                    && fileDaScaricare.getFileSuDisco() != null) {
                fileDaScaricare.getFileSuDisco().delete();
                fileDaScaricare.setFileSuDisco(null);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(fileDaScaricare);
        }
    }

    public void recuperaZip(String outputPath, RecuperoExt recupero, LinkedHashMap<Long, CompRecDip> compRecDips) {
        RispostaControlli rispostaControlli;
        FileBinario fileDaScaricare = new FileBinario();
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
        ZipArchiveEntry tmpEntry = null;
        boolean hoConvertitodeiFile = false;

        try {
            rispostaControlli = controlliRecDip.calcolaNomeFileZip(recupero.getParametriRecupero(), compRecDips);
            rispostaWs.setNomeFile("DIP_" + rispostaControlli.getrString() + ".zip");
            fileDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
            tmpOutputStream = new FileOutputStream(fileDaScaricare.getFileSuDisco());
            tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);

            for (CompRecDip comp : compRecDips.values()) {
                if (comp.isErroreFormatoContenuto()) {
                    comp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    comp.setErrorMessage("Il formato del file versato non è corretto per il convertitore");
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_001_003,
                            comp.getNomeFormatoRappresentazione(), comp.getNomeFileBreve(),
                            comp.getDsFormatoContReale(), comp.getDsFormatoContAtteso());
                    break; // esce dal ciclo for, se incontra un errore: ormai lo zip è condannato
                } else if (comp.getTipoAlgoritmoRappresentazione() == CostantiDB.TipoAlgoritmoRappr.ALTRO) {
                    comp.setSeverity(IRispostaWS.SeverityEnum.WARNING);
                    comp.setErrorMessage("Il formato di rappresentazione non prevede una trasformazione.");
                } else {
                    tmpEntry = new ZipArchiveEntry(comp.getNomeFilePerZip());
                    tmpZipOutputStream.putArchiveEntry(tmpEntry);
                    rispostaControlli = this.converti(comp, compRecDips, tmpZipOutputStream, recupero.getDipRootImg());
                    tmpZipOutputStream.closeArchiveEntry();
                    hoConvertitodeiFile = true;
                    if (!rispostaControlli.isrBoolean()) {
                        if (rispostaControlli.getCodErr() == null) {
                            /**
                             * Il convertitore {0} del formato di rappresentazione {1} relativo al componente {2} ha
                             * reso un errore di conversione ed è stato marcato come ERRATO: {3}
                             */
                            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_002_001,
                                    comp.getNomeConvertitore() + "." + comp.getVersioneConvertitore(),
                                    comp.getNomeFormatoRappresentazione(), comp.getNomeFileBreve(),
                                    rispostaControlli.getDsErr());
                        } else {
                            rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                        }
                        break; // esce dal ciclo for, se incontra un errore: ormai lo zip è condannato
                    }
                }
            }
            tmpZipOutputStream.flush();
            //
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK && (!hoConvertitodeiFile)) {
                /*
                 * Non sono stati individuati componenti il cui formato di rappresentazione sia compatibile con il
                 * recupero DIP
                 */
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_001_001);
            }

        } catch (IOException ex) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella generazione del file da scaricare  " + ex.getMessage());
            log.error("Errore nella generazione del file da scaricare ", ex);
        } finally {
            IOUtils.closeQuietly(tmpZipOutputStream);
            IOUtils.closeQuietly(tmpOutputStream);
            tmpZipOutputStream = null;
            tmpOutputStream = null;
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.ERROR
                    && fileDaScaricare.getFileSuDisco() != null) {
                fileDaScaricare.getFileSuDisco().delete();
                fileDaScaricare.setFileSuDisco(null);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(fileDaScaricare);
        }
    }

    public void recuperaConvInZip(ZipArchiveOutputStream tmpZipOutputStream, RecuperoExt recupero,
            LinkedHashMap<Long, CompRecDip> compRecDips, String prefisso) {
        RispostaControlli rispostaControlli;
        ZipArchiveEntry tmpEntry = null;
        String tmpSuffisso = "";
        String SUFF_ERRORE = ".err.txt";
        String SUFF_WARNING = ".warn.txt";

        try {
            for (CompRecDip comp : compRecDips.values()) {
                tmpSuffisso = "";
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                if (comp.isErroreFormatoContenuto()) {
                    String tmpString = MessaggiWSBundle.getString(MessaggiWSBundle.RECDIP_001_003,
                            comp.getNomeFormatoRappresentazione(), comp.getNomeFileBreve(),
                            comp.getDsFormatoContReale(), comp.getDsFormatoContAtteso());
                    bos.reset();
                    bos.write(tmpString.getBytes("UTF-8"));
                    tmpSuffisso = SUFF_ERRORE;
                } else if (comp.getStatoFileTrasform() == CostantiDB.StatoFileTrasform.ERRATO) {
                    String tmpString = MessaggiWSBundle.getString(MessaggiWSBundle.RECDIP_001_002,
                            comp.getNomeConvertitore() + "." + comp.getVersioneConvertitore(),
                            comp.getNomeFormatoRappresentazione(), comp.getNomeFileBreve());
                    bos.reset();
                    bos.write(tmpString.getBytes("UTF-8"));
                    tmpSuffisso = SUFF_ERRORE;
                } else if (comp.getTipoAlgoritmoRappresentazione() == CostantiDB.TipoAlgoritmoRappr.ALTRO) {
                    bos.write("Il formato di rappresentazione non prevede una trasformazione.".getBytes("UTF-8"));
                    tmpSuffisso = SUFF_WARNING;
                } else {
                    rispostaControlli = this.converti(comp, compRecDips, bos, recupero.getDipRootImg());
                    if (!rispostaControlli.isrBoolean()) {
                        tmpSuffisso = SUFF_ERRORE;
                        if (rispostaControlli.getCodErr() == null) {
                            String tmpString = MessaggiWSBundle.getString(MessaggiWSBundle.RECDIP_002_001,
                                    comp.getNomeConvertitore() + "." + comp.getVersioneConvertitore(),
                                    comp.getNomeFormatoRappresentazione(), comp.getNomeFileBreve(),
                                    rispostaControlli.getDsErr());
                            bos.reset();
                            bos.write(tmpString.getBytes("UTF-8"));
                        } else {
                            throw new RuntimeException(rispostaControlli.getDsErr());
                        }
                    }
                }
                tmpEntry = new ZipArchiveEntry(prefisso + "/" + comp.getNomeFilePerZip() + tmpSuffisso);
                tmpZipOutputStream.putArchiveEntry(tmpEntry);
                tmpZipOutputStream.write(bos.toByteArray());
                tmpZipOutputStream.closeArchiveEntry();
                bos.reset();
            }
            tmpZipOutputStream.flush();
            //
        } catch (IOException ex) {
            log.error("Errore nella generazione del file da scaricare ", ex);
            throw new RuntimeException(ex);
        }
    }

    /*
     * se coderr è valorizzato, è un errore applicativo grave (di norma è 666) se coderr NON è valorizzato è un errore
     * di conversione del componente e va riportato sulla tabella DecTrasformTipoRappr come ERRATO
     */
    private RispostaControlli converti(CompRecDip comp, LinkedHashMap<Long, CompRecDip> compRecDips, OutputStream out,
            String rootImgDip) {
        RispostaControlli rispostaControlli;
        //
        RispostaControlli risposta;
        risposta = new RispostaControlli();
        risposta.setrBoolean(false);
        ICompTransformer compTransformer = null;

        if (null != comp.getTipoAlgoritmoRappresentazione()) {
            switch (comp.getTipoAlgoritmoRappresentazione()) {
            case XSLT:
                if (comp.getMimeType().equalsIgnoreCase(MIMETYPE_PDF)) {
                    compTransformer = new XmlToPdfTransform();
                } else {
                    compTransformer = new XsltTransform();
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo algoritmo non supportato");
            }
        }

        // recupero le immagini...
        rispostaControlli = controlliRecDip.estraiImmaginiConv(comp.getIdFileTrasform(), rootImgDip);
        if (rispostaControlli.isrBoolean()) {
            rispostaControlli = controlliRecDip.convertiXml(compTransformer, comp, out);
            if (rispostaControlli.getCodErr() == null) {
                /*
                 * se coderr è valorizzato, è un errore applicativo grave (di norma è 666) se coderr NON è valorizzato
                 * significa che la conversione è riuscita oppure è un errore di conversione del componente e va
                 * riportato su tutte le righe CompRecDip con lo stesso convertitore
                 */
                for (CompRecDip compRecDip : compRecDips.values()) {
                    if (compRecDip.getIdFileTrasform() == comp.getIdFileTrasform()) {
                        compRecDip.setStatoFileTrasform(comp.getStatoFileTrasform());
                        compRecDip.setSeverity(comp.getSeverity());
                        compRecDip.setErrorMessage(comp.getErrorMessage());
                    }
                }
            }
        }
        if (rispostaControlli.isrBoolean()) {
            risposta.setrBoolean(true);
        } else {
            risposta.setCodErr(rispostaControlli.getCodErr());
            risposta.setDsErr(rispostaControlli.getDsErr());
        }

        return risposta;
    }

    public void collaudaConvertitore(String outputPath, RecuperoExt recupero, CompRecDip comp) {
        RispostaControlli rispostaControlli;
        FileBinario fileDaScaricare = new FileBinario();
        FileOutputStream tmpOutputStream = null;
        try {
            if (comp.getTipoAlgoritmoRappresentazione() == CostantiDB.TipoAlgoritmoRappr.ALTRO) {
                comp.setSeverity(IRispostaWS.SeverityEnum.WARNING);
                comp.setErrorMessage("Il formato di rappresentazione non prevede una trasformazione.");
                /*
                 * Non sono stati individuati componenti il cui formato di rappresentazione sia compatibile con il
                 * recupero DIP
                 */
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.RECDIP_001_001);
            } else {
                rispostaWs.setNomeFile(comp.getNomeFileBreve() + "." + comp.getEstensioneFile());
                rispostaWs.setMimeType(comp.getMimeType());
                fileDaScaricare.setFileSuDisco(
                        File.createTempFile("output_", "." + comp.getEstensioneFile(), new File(outputPath)));
                tmpOutputStream = new FileOutputStream(fileDaScaricare.getFileSuDisco());
                ///
                rispostaControlli = this.convertiPerCollaudo(comp, tmpOutputStream, recupero.getDipRootImg());
                if (!rispostaControlli.isrBoolean()) {
                    if (rispostaControlli.getCodErr() == null) {
                        String tmpString = "Il convertitore ha reso un errore di conversione "
                                + "ed è stato marcato come ERRATO, errore: " + rispostaControlli.getDsErr();
                        rispostaWs.setEsitoWsError("---", tmpString);
                    } else {
                        rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                    }
                }
            }
        } catch (IOException ex) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella generazione del file da scaricare  " + ex.getMessage());
            log.error("Errore nella generazione del file da scaricare ", ex);
        } finally {
            IOUtils.closeQuietly(tmpOutputStream);
            tmpOutputStream = null;
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.ERROR
                    && fileDaScaricare.getFileSuDisco() != null) {
                fileDaScaricare.getFileSuDisco().delete();
                fileDaScaricare.setFileSuDisco(null);
            }
        }

        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(fileDaScaricare);
        }

    }

    /*
     * se coderr è valorizzato, è un errore applicativo grave (di norma è 666) se coderr NON è valorizzato è un errore
     * di conversione del componente e va riportato sulla tabella DecTrasformTipoRappr come ERRATO
     */
    private RispostaControlli convertiPerCollaudo(CompRecDip comp, OutputStream out, String rootImgDip) {
        RispostaControlli rispostaControlli;
        //
        RispostaControlli risposta;
        risposta = new RispostaControlli();
        risposta.setrBoolean(false);
        ICompTransformer compTransformer = null;

        if (null != comp.getTipoAlgoritmoRappresentazione()) {
            switch (comp.getTipoAlgoritmoRappresentazione()) {
            case XSLT:
                if (comp.getMimeType().equalsIgnoreCase(MIMETYPE_PDF)) {
                    compTransformer = new XmlToPdfTransform();
                } else {
                    compTransformer = new XsltTransform();
                }
                break;
            default:
                throw new IllegalArgumentException("Tipo algoritmo non supportato");
            }
        }

        // recupero le immagini...
        rispostaControlli = controlliRecDip.estraiImmaginiConv(comp.getIdFileTrasform(), rootImgDip);
        if (rispostaControlli.isrBoolean()) {
            rispostaControlli = controlliRecDip.convertiXml(compTransformer, comp, out);
        }
        if (rispostaControlli.isrBoolean()) {
            risposta.setrBoolean(true);
        } else {
            risposta.setCodErr(rispostaControlli.getCodErr());
            risposta.setDsErr(rispostaControlli.getDsErr());
        }

        return risposta;
    }

}
