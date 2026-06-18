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

package it.eng.parer.ws.recupero.ejb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasUnitaDocFascicolo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.VolFileVolumeConserv;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.VrsSessioneVers;
import it.eng.parer.entity.VrsUrnXmlSessioneVers;
import it.eng.parer.entity.VrsXmlDatiSessioneVers;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.FasMetaVerAipFascicolo;
import it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers;
import it.eng.parer.exception.SacerException;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.viewEntity.AroVLisaipudSistemaMigraz;
import it.eng.parer.volume.utils.VolumeEnums.FileTypeEnum;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.BlobObject;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Constants.TiEntitaSacerObjectStorage;
import it.eng.parer.web.util.XmlPrettyPrintFormatter;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recupero.ejb.oracleClb.RecClbOracle;
import it.eng.parer.ws.recupero.utils.RecuperoTxtGen;
import it.eng.parer.ws.recuperoDip.dto.DatiRecuperoDip;
import it.eng.parer.ws.recuperoDip.ejb.ControlliRecDip;
import it.eng.parer.ws.recuperoDip.utils.GestSessRecDip;
import it.eng.parer.ws.recuperoTpi.ejb.RecuperoCompFS;
import it.eng.parer.ws.recuperoreportvf.ejb.ControlliReportvf;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.TokenFileNameType;
import it.eng.parerxml.xsd.FileXSD;
import it.eng.parerxml.xsd.FileXSDUtil;

/**
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "RecuperoZipGen")
@LocalBean
public class RecuperoZipGen {

    private static final Logger log = LoggerFactory.getLogger(RecuperoZipGen.class);
    private static final String DIRECTORY_OUT_DIP = "FileTrasformati";
    private static final String DIRECTORY_REC = "FileVersati";
    private static final String DIRECTORY_AGGR = "Aggregazioni";
    private static final String DIRECTORY_SERIE = "Serie";
    private static final String DIRECTORY_FASC = "Fascicoli";

    // EVO#20972
    private static final String DIRECTORY_REC_AIPV2 = "file";
    private static final String DIRECTORY_SIP_AIPV2 = "sip";
    private static final String DIRECTORY_PIX_AIPV2 = "pindexsource";
    private static final String DIRECTORY_XSD_AIPV2 = "xmlschema";
    private static final String DIRECTORY_AGGR_AIPV2 = "aggregazioni";
    private static final String DIRECTORY_SERIE_AIPV2 = "serie";
    private static final String DIRECTORY_FASC_AIPV2 = "fascicoli";
    // end EVO#20972

    // The ZipEntry.setTime() method writes the time taking into account
    // the local time zone,
    // so we must first convert the desired timestamp value in the local time zone
    // to have the
    // same timestamps in the ZIP file when the project is built on another computer
    // in a
    // different time zone.
    private static final long DEFAULT_ZIP_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    private static final String ERRORE_GENERAZIONE_ZIP = "Errore nella generazione dello zip da scaricare  ";
    private static final String ERRORE_SERIALIZZAZIONE_XML = "Errore nella serializzazione dell'XML  ";

    @EJB
    private ControlliRecupero controlliRecupero;
    @EJB
    private RecuperoCompFS recuperoCompFS;
    @EJB
    private XmlContextCache xmlContextCache;
    @EJB
    private UnitaDocumentarieHelper udHelper;
    @EJB
    private ComponentiHelper componentiHelper;
    @EJB
    private ControlliRecDip controlliRecDip;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private FascicoliHelper fasHelper;
    @EJB
    private ElencoVersFascicoliHelper elencoVersFascHelper;
    @EJB
    private AmbientiHelper ambientiHelper;
    @EJB
    private RecuperoDocumento recuperoDocumento;
    @EJB
    private ControlliReportvf controlliReportvf;
    @EJB
    private ObjectStorageService objectStorageService;

    public File getZip(String outputPath, RecuperoExt recupero, boolean tentaRecuperoDip,
            RispostaWSRecupero rispostaWs) throws IOException {
        generaZipOggetto(outputPath, recupero, tentaRecuperoDip, rispostaWs);

        if (rispostaWs.getRifFileBinario() != null
                && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            return rispostaWs.getRifFileBinario().getFileSuDisco();
        } else {
            return null;
        }

    }

    public void generaZipOggetto(String outputPath, RecuperoExt recupero, boolean tentaRecuperoDip,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<ComponenteRec> lstComp = null;
        String prefisso = null;
        String nomeFileZip = null;
        boolean includiSessFileVersamento = false;
        boolean includiRapportoVersamento = false;
        boolean includiFileIndiceAIP = false;
        boolean recuperaDip = tentaRecuperoDip;
        boolean recuperaDipEsibizione = false;
        boolean includiFirmaMarcaElencoIndiceAIP = false;
        boolean includiIndiceVolumeAIPSerie = false;
        boolean includiIndiceFascicoli = false;
        // EVO#20972
        boolean includiSessFileVersamentoV2 = false;
        boolean includiSessFileVersUpd = false;
        boolean includiFileIndiceAIPV2 = false;
        boolean includiFileIndiceAIPExt = false;
        boolean includiFileIndiceAIPVol = false;
        boolean includiFileXsdAIPV2 = false;
        // end EVO#20972
        FileBinario zipDaScaricare = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        // legge l'elenco dei componenti di tipo file nell'UD, per estrarre i relativi
        // blob
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
            case UNI_DOC:
                if (recupero.getParametriRecupero().getIdTipoDoc() != null) {
                    rispostaControlli = controlliRecupero.leggiCompFileInUDByTipoDoc(
                            recupero.getParametriRecupero().getIdUnitaDoc(),
                            recupero.getParametriRecupero().getIdTipoDoc());
                } else {
                    rispostaControlli = controlliRecupero.leggiCompFileInUD(
                            recupero.getParametriRecupero().getIdUnitaDoc(), false);
                }
                prefisso = "UD_";
                break;
            case DOC:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInDoc(recupero.getParametriRecupero().getIdDocumento());
                prefisso = "DOC_";
                break;
            case COMP:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInComp(recupero.getParametriRecupero().getIdComponente());
                prefisso = "COMP_";
                break;
            case SUB_COMP:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInComp(recupero.getParametriRecupero().getIdComponente());
                prefisso = "SUBCOMP_";
                recuperaDip = false; // per il sottocomponente non si includono mai i file
                // convertiti
                break;
            //
            case UNI_DOC_UNISYNCRO:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInUD(recupero.getParametriRecupero().getIdUnitaDoc(), true);
                prefisso = "AIP_";
                includiFileIndiceAIP = true;
                includiSessFileVersamento = true;
                recuperaDip = false; // per il recupero AIP non si includono mai i file convertiti
                includiFirmaMarcaElencoIndiceAIP = true;
                includiIndiceVolumeAIPSerie = true;
                includiIndiceFascicoli = true;
                break;
            //
            // EVO#20972
            case UNI_DOC_UNISYNCRO_V2:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInUDAIPV2(recupero.getParametriRecupero().getIdUnitaDoc());
                prefisso = "";
                includiFileIndiceAIPV2 = true;
                includiFileIndiceAIPExt = true;
                includiFileIndiceAIPVol = true;
                includiFileXsdAIPV2 = true;
                includiSessFileVersamentoV2 = true;
                includiSessFileVersUpd = true;
                recuperaDip = false; // per il recupero AIP non si includono mai i file convertiti
                includiFirmaMarcaElencoIndiceAIP = true;
                includiIndiceVolumeAIPSerie = true;
                includiIndiceFascicoli = true;
                break;
            // end EVO#20972
            //
            case UNI_DOC_DIP_ESIBIZIONE:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInUD(recupero.getParametriRecupero().getIdUnitaDoc(), false);
                prefisso = "DIP_UD_";
                includiRapportoVersamento = true;
                recuperaDip = false; // per il recupero DIP per esibizione non si includono mai i
                // file convertiti
                recuperaDipEsibizione = true;
                break;
            case DOC_DIP_ESIBIZIONE:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInDoc(recupero.getParametriRecupero().getIdDocumento());
                prefisso = "DIP_DOC_";
                includiRapportoVersamento = true;
                recuperaDip = false; // per il recupero DIP per esibizione non si includono mai i
                // file convertiti
                recuperaDipEsibizione = true;
                break;
            case COMP_DIP_ESIBIZIONE:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInComp(recupero.getParametriRecupero().getIdComponente());
                prefisso = "DIP_COMP_";
                includiRapportoVersamento = true;
                recuperaDip = false; // per il recupero DIP per esibizione non si includono mai i
                // file convertiti
                recuperaDipEsibizione = true;
                break;
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstComp = (List<ComponenteRec>) rispostaControlli.getrObject();
                nomeFileZip = rispostaControlli.getrString();
                rispostaWs.setNomeFile(prefisso + nomeFileZip + ".zip");
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            File tmpOutput = File.createTempFile("output_", ".zip", new File(outputPath));
            log.info("NOME DEL FILE TEMPORANEO: {}", tmpOutput.getPath());
            // set tmp file before try so the finally block can always clean up on failure
            zipDaScaricare.setFileSuDisco(tmpOutput);

            try (FileOutputStream tmpOutputStream = new FileOutputStream(tmpOutput);
                    ZipOutputStream tmpZipOutputStream = new ZipOutputStream(tmpOutputStream)) {

                tmpZipOutputStream.setLevel(Deflater.DEFAULT_COMPRESSION);
                tmpZipOutputStream.setMethod(ZipOutputStream.DEFLATED);

                if (recuperaDip && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiComponentiDIP(tmpZipOutputStream, recupero, rispostaWs);
                }

                if (recuperaDipEsibizione && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiDipEsibizione(tmpZipOutputStream, recupero, rispostaWs);
                }

                if (includiSessFileVersamento && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // MAC #34838
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero,
                            TipiLetturaXml.RECUPERO_PER_ZIP_AIP, TipiXmlDaIncludere.TUTTI,
                            rispostaWs);
                }

                // MAC#30890
                if (includiSessFileVersamentoV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero,
                            TipiLetturaXml.RECUPERO_PER_ZIP_AIP, TipiXmlDaIncludere.TUTTI,
                            rispostaWs);
                }
                // end MAC#30890

                // EVO#20972
                if (includiSessFileVersUpd && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUpd(tmpZipOutputStream, recupero, rispostaWs);
                }

                if (includiFileXsdAIPV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni degli XSD
                    this.aggiungiFileXmlSchema(tmpZipOutputStream, rispostaWs);
                }
                // end EVO#20972

                if (includiRapportoVersamento && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero,
                            TipiLetturaXml.SOLO_FILE_RECUPERATI,
                            TipiXmlDaIncludere.SOLO_RAPPORTO_VERSAMENTO, rispostaWs);
                }

                this.aggiungiFileComponenti(tmpZipOutputStream, recupero, lstComp, rispostaWs);

                if (includiFileIndiceAIP && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP
                    this.aggiungiIndiciAipUdOs(tmpZipOutputStream,
                            recupero.getParametriRecupero().getIdUnitaDoc(), rispostaWs);
                }

                // EVO#20972:MEV#20971
                if (includiFileIndiceAIPV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // MEV#30395
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di Sacer
                    this.aggiungiIndiciAipUdV2Os(tmpZipOutputStream,
                            recupero.getParametriRecupero().getIdUnitaDoc(), rispostaWs);
                    // end MEV#30395
                }

                if (includiFileIndiceAIPExt && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di altri
                    // conservatori
                    this.aggiungiIndiciAipUdExt(tmpZipOutputStream,
                            recupero.getParametriRecupero().getIdUnitaDoc(), rispostaWs);
                }

                if (includiFileIndiceAIPVol && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice dei Volumi di
                    // conservazione di Sacer
                    this.aggiungiIndiciAipUdVol(tmpZipOutputStream,
                            recupero.getParametriRecupero().getIdUnitaDoc(), rispostaWs);
                }
                // end EVO#20972:MEV#20971

                if (includiFirmaMarcaElencoIndiceAIP
                        && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiElencoIndiciAipUd(tmpZipOutputStream,
                            recupero.getParametriRecupero().getIdUnitaDoc(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.FIRMA_ELENCO_INDICI_AIP
                                    .name(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.ELENCO_INDICI_AIP
                                    .name(),
                            rispostaWs);
                    this.aggiungiElencoIndiciAipUd(tmpZipOutputStream,
                            recupero.getParametriRecupero().getIdUnitaDoc(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.MARCA_FIRMA_ELENCO_INDICI_AIP
                                    .name(),
                            null, rispostaWs);
                }

                if (includiIndiceVolumeAIPSerie && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiIndiciSerie(tmpZipOutputStream, recupero, rispostaWs);
                }

                if (includiIndiceFascicoli && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiIndiceFascicoli(tmpZipOutputStream, recupero, rispostaWs);
                }

                tmpZipOutputStream.flush();
                tmpZipOutputStream.finish();

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR
                        && zipDaScaricare.getFileSuDisco() != null) {
                    boolean result = FileUtils.deleteQuietly(zipDaScaricare.getFileSuDisco());
                    log.info("CANCELLAZIONE FILE TEMPORANEO: {}, con esito={}",
                            zipDaScaricare.getFileSuDisco().getAbsolutePath(),
                            result ? "POSITIVO" : "NEGATIVO");
                    zipDaScaricare.setFileSuDisco(null);
                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    /**
     * Configura uno ZipEntry con timestamp standardizzati. IMPORTANTE: Utilizza solo setTime() che
     * è compatibile con ZIP64.
     *
     * @param entry Lo ZipEntry da configurare
     */
    private void configureZipEntry(ZipEntry entry) {
        entry.setTime(DEFAULT_ZIP_TIMESTAMP);
    }

    /**
     * Crea un nuovo ZipEntry configurato con timestamp standard.
     *
     * @param name Nome dell'entry
     * @return ZipEntry configurato
     */
    private ZipEntry createZipEntry(String name) {
        ZipEntry entry = new ZipEntry(name);
        configureZipEntry(entry);
        return entry;
    }

    /**
     * Aggiunge un entry allo zip gestendo i conflitti di nomi duplicati.
     *
     * @param zipOutputStream  Stream ZIP di destinazione
     * @param path             Percorso dell'entry nello ZIP
     * @param entryGiaInserite Set dei nomi già utilizzati
     * @param checkEntry       Se true verifica e gestisce duplicati
     * @throws IOException In caso di errore di scrittura o superamento massimo iterazioni
     */
    private void addZipEntry(ZipOutputStream zipOutputStream, String path,
            Set<String> entryGiaInserite, boolean checkEntry) throws IOException {
        final int MAX_ITERAZIONI = 1000;
        int i = 0;
        int contatoreCopie = 0;
        String nuovoNomeFile = path;

        for (i = 0; i < MAX_ITERAZIONI; i++) {
            if (checkEntry && entryGiaInserite.contains(nuovoNomeFile)) {
                contatoreCopie++;
                int posPunto = path.lastIndexOf(".");
                if (posPunto < 0) {
                    nuovoNomeFile = path + "(" + contatoreCopie + ")";
                } else {
                    nuovoNomeFile = path.substring(0, posPunto) + "(" + contatoreCopie + ")"
                            + path.substring(posPunto, path.length());
                }
            } else {
                ZipEntry zipEntry = createZipEntry(nuovoNomeFile);
                zipOutputStream.putNextEntry(zipEntry);
                entryGiaInserite.add(nuovoNomeFile);
                break;
            }
        }
        if (i == MAX_ITERAZIONI) {
            throw new IOException(
                    "Raggiunto il numero massimo di iterazioni per gestire uno stesso nome file: ["
                            + path + "]");
        }
    }

    // EVO#20972

    public File getZipProveCons(String outputPath, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        if (recupero.getParametriRecupero()
                .getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.PROVE_CONSERV_AIPV2) {
            generaZipProveConsAIPV2(outputPath, recupero, rispostaWs);
        } else {
            generaZipProveCons(outputPath, recupero, rispostaWs);
        }

        if (rispostaWs.getRifFileBinario() != null
                && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            return rispostaWs.getRifFileBinario().getFileSuDisco();
        } else {
            return null;
        }
    }

    public void generaZipProveConsAIPV2(String outputPath, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        VolVolumeConserv volVolumeConserv = null;
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipOutputStream tmpZipOutputStream = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiVolumeConserv(recupero.getParametriRecupero().getIdVolume());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                volVolumeConserv = (VolVolumeConserv) rispostaControlli.getrObject();
            }

        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(
                        File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipOutputStream(tmpOutputStream);

                if (volVolumeConserv != null) {
                    this.aggiungiProveConsUd(tmpZipOutputStream, volVolumeConserv);
                }

                tmpZipOutputStream.flush();
                tmpZipOutputStream.finish();

            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR
                        && zipDaScaricare.getFileSuDisco() != null) {
                    zipDaScaricare.getFileSuDisco().delete();
                    zipDaScaricare.setFileSuDisco(null);
                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }
    // end EVO#20972

    public void generaZipProveCons(String outputPath, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        rispostaWs.getIstanzaEsito();
        rispostaWs.getAvanzamento();
        List<VolVolumeConserv> volVolumeConserv = null;
        FileBinario zipDaScaricare = null;
        byte[] tmpXmlByteArrProveC = null;
        FileOutputStream tmpOutputStream = null;
        ZipOutputStream tmpZipOutputStream = null;
        ZipEntry tmpEntry = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiVolumiUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                volVolumeConserv = (List<VolVolumeConserv>) rispostaControlli.getrObject();
            }

        }

        // costruisce la stringa xml relativa all'indice xml delle prove cons
        // costruisce il byte array ben formattato relativo all'indice xml
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            tmpXmlByteArrProveC = this.preparaXMLIndicePC(rispostaWs);
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(
                        File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipOutputStream(tmpOutputStream);

                tmpEntry = createZipEntry("IndiceProveConservazione.xml");
                tmpZipOutputStream.putNextEntry(tmpEntry);
                tmpZipOutputStream.write(tmpXmlByteArrProveC);
                tmpZipOutputStream.closeEntry();

                if (volVolumeConserv != null && !volVolumeConserv.isEmpty()) {
                    for (VolVolumeConserv tmpVolConserv : volVolumeConserv) {
                        this.aggiungiProveConsUd(tmpZipOutputStream, tmpVolConserv);
                    }
                }

                tmpZipOutputStream.flush();
                tmpZipOutputStream.finish();

            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                File file = zipDaScaricare.getFileSuDisco();
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR && file != null) {
                    if (file != null && !file.delete()) {
                        log.warn("Impossibile cancellare il file temporaneo: "
                                + file.getAbsolutePath());
                    }
                    // reset file
                    // su disco in caso di errore
                    zipDaScaricare.setFileSuDisco(null);

                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    public void generaZipRapportiVers(String outputPath, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        rispostaWs.getAvanzamento();
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipOutputStream tmpZipOutputStream = null;
        Map<Long, Map<String, String>> xmlVersamentoOs = new HashMap<>();

        Recupero parsedUnitaDoc = recupero.getStrutturaRecupero();
        CSChiave tmpCSChiave = new CSChiave();
        tmpCSChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
        tmpCSChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
        tmpCSChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());
        rispostaWs.setNomeFile("RV-" + this.calcolaNomeFileZipRV(tmpCSChiave));

        zipDaScaricare = new FileBinario();
        RispostaControlli rispostaControlli = new RispostaControlli();
        try {
            List<VrsXmlDatiSessioneVers> lstVrsXml = null;
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiXMLSessioneversUd(recupero.getParametriRecupero().getIdUnitaDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVrsXml = (List<VrsXmlDatiSessioneVers>) rispostaControlli.getrObject();
            }
            // load from O.S.
            if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                //
                rispostaControlli = aggiungiXmlDaObjectStorageOnMap(lstVrsXml);

                if (!rispostaControlli.isrBoolean()) {
                    setRispostaWsError(rispostaWs, rispostaControlli);
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                            rispostaControlli.getDsErr());
                } else {
                    xmlVersamentoOs = (Map<Long, Map<String, String>>) rispostaControlli
                            .getrObject();
                }
            }
            //
            if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                int fileTrovati = 0;
                if (lstVrsXml != null && !lstVrsXml.isEmpty()) {
                    //
                    zipDaScaricare.setFileSuDisco(
                            File.createTempFile("output_", ".zip", new File(outputPath)));
                    tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                    tmpZipOutputStream = new ZipOutputStream(tmpOutputStream);
                    String fileName;
                    int contaRappVers = 1;
                    for (VrsXmlDatiSessioneVers tmpXml : lstVrsXml) {
                        fileName = null;
                        if (CostantiDB.TipiXmlDati.RAPP_VERS.equals(tmpXml.getTiXmlDati())) {
                            fileTrovati++;
                            if (tmpXml.getDsUrnXmlVers() != null
                                    && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                fileName = tmpXml.getDsUrnXmlVers();
                            } else {
                                fileName = String.format("RapportoVersamento%05d", contaRappVers);
                                contaRappVers++;
                            }
                        } else {
                            fileName = null;
                        }
                        if (fileName != null) {
                            ZipEntry zipEntry = createZipEntry(
                                    ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml");
                            tmpZipOutputStream.putNextEntry(zipEntry);
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero
                                    .findIdSessVersByXmlDatiSessVers(tmpXml);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                                        rispostaControlli.getDsErr());
                            } else {
                                long idSessioneVers = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoOs.containsKey(idSessioneVers)
                                        && tmpXml.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoOs
                                            .get(idSessioneVers);
                                    String blXml = allXml.get(tmpXml.getTiXmlDati());
                                    tmpZipOutputStream
                                            .write(blXml.getBytes(StandardCharsets.UTF_8));
                                } else {
                                    tmpZipOutputStream.write(
                                            tmpXml.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }

                            tmpZipOutputStream.closeEntry();
                        }
                    }
                    tmpZipOutputStream.flush();
                    tmpZipOutputStream.finish();
                }
                if (fileTrovati == 0) {
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.UD_005_004,
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpCSChiave));
                }
            }
        } catch (Exception ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    ERRORE_GENERAZIONE_ZIP + ex.getMessage());
            log.error("Errore nella generazione dello zip da scaricare ", ex);
        } finally {
            IOUtils.closeQuietly(tmpZipOutputStream);
            IOUtils.closeQuietly(tmpOutputStream);
            tmpZipOutputStream = null;
            tmpOutputStream = null;
            File file = zipDaScaricare.getFileSuDisco();
            if (rispostaWs.getSeverity() == SeverityEnum.ERROR && file != null) {
                if (!file.delete()) {
                    log.warn(
                            "Impossibile cancellare il file temporaneo: " + file.getAbsolutePath());
                }
                zipDaScaricare.setFileSuDisco(null);
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    public void generaZipSip(String outputPath, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        List<ComponenteRec> lstComp = null;
        String prefisso = null;
        String nomeFileZip = null;
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipOutputStream tmpZipOutputStream = null;

        // legge l'elenco dei componenti di tipo file nell'UD, per estrarre i relativi
        // blob
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
            case UNI_DOC:
                rispostaControlli = controlliRecupero.leggiCompFileInUDVersamentoUd(
                        recupero.getParametriRecupero().getIdUnitaDoc());
                prefisso = "SIP_UD_";
                break;
            case DOC:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInDoc(recupero.getParametriRecupero().getIdDocumento());
                prefisso = "SIP_DOC_";
                break;
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstComp = (List<ComponenteRec>) rispostaControlli.getrObject();
                nomeFileZip = rispostaControlli.getrString();
                rispostaWs.setNomeFile(prefisso + nomeFileZip + ".zip");
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(
                        File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipOutputStream(tmpOutputStream);

                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero,
                            TipiLetturaXml.RECUPERO_PER_ZIP_SIP, TipiXmlDaIncludere.TUTTI,
                            rispostaWs);
                }

                this.aggiungiFileComponenti(tmpZipOutputStream, recupero, lstComp, rispostaWs);

                tmpZipOutputStream.flush();
                tmpZipOutputStream.finish();

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                File file = zipDaScaricare.getFileSuDisco();
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR && file != null) {
                    if (!file.delete()) {
                        log.warn("Impossibile cancellare il file temporaneo: "
                                + file.getAbsolutePath());
                    }
                    zipDaScaricare.setFileSuDisco(null);
                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    private String calcolaNomeFileZipRV(CSChiave chiave) {
        StringBuilder tmpString = new StringBuilder();

        tmpString.append("UD_");
        tmpString.append(chiave.getTipoRegistro());
        tmpString.append("-");
        tmpString.append(chiave.getAnno());
        tmpString.append("-");
        tmpString.append(chiave.getNumero());
        tmpString.append(".zip");

        return tmpString.toString().replace(':', '_');
    }

    private byte[] preparaXMLIndicePC(RispostaWSRecupero rispostaWs) {
        String tmpStringXml = null;
        byte[] tmpXmlByteArr = null;

        // costruisce la stringa xml relativa all'indice xml delle prove cons.
        StringWriter tmpStringWriter = new StringWriter();
        try {
            Marshaller marshaller = xmlContextCache.getVersRespStatoCtx_IndiceProveCons()
                    .createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(rispostaWs.getIndiceProveConservazione(), tmpStringWriter);

            tmpStringXml = tmpStringWriter.toString();
        } catch (Exception ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    ERRORE_SERIALIZZAZIONE_XML + ex.getMessage());
            log.error(ERRORE_SERIALIZZAZIONE_XML, ex);
        }

        // costruisce il byte array ben formattato relativo all'indice xml
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                XmlPrettyPrintFormatter tmpFormatter = new XmlPrettyPrintFormatter();
                tmpStringXml = tmpFormatter.prettyPrintWithDOM3LS(tmpStringXml);
                tmpXmlByteArr = tmpStringXml.getBytes(StandardCharsets.UTF_8);
            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        ERRORE_SERIALIZZAZIONE_XML + ex.getMessage());
                log.error(ERRORE_SERIALIZZAZIONE_XML, ex);
            }
        }
        return tmpXmlByteArr;
    }

    private void aggiungiFileComponenti(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            List<ComponenteRec> lstComp, RispostaWSRecupero rispostaWs) throws IOException {
        RispostaControlli rispostaControlli = new RispostaControlli();
        Set<String> entryGiaInserite = new HashSet<>();
        TokenFileNameType tipoNomeFile = recupero.getStrutturaRecupero().getParametri() != null
                ? recupero.getStrutturaRecupero().getParametri().getTipoNomeFile()
                : null;
        for (ComponenteRec tmpCmp : lstComp) {
            // define file name
            String archiveEntryFinalName = null;
            // check single entry
            boolean checkEntry = true; // verify or not zip entry on HashSet entryGiaInserite
            // Se viene passato NIENTE oppure NOME_FILE_URN_SACER si vuole il vecchio
            // comportamento
            if (tipoNomeFile == null || tipoNomeFile.value().trim().equals("")
                    || tipoNomeFile.equals(TokenFileNameType.NOME_FILE_URN_SACER)) {
                // EVO#20972
                if (!recupero.getParametriRecupero().getTipoEntitaSacer()
                        .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2)) {
                    // Vecchio comportamento
                    // elab file name
                    archiveEntryFinalName = DIRECTORY_REC + "/" + tmpCmp.getNomeFilePerZip();
                } else {
                    // elab file name
                    archiveEntryFinalName = DIRECTORY_REC_AIPV2 + "/"
                            + it.eng.parer.async.utils.IOUtils.getFilename(
                                    it.eng.parer.async.utils.IOUtils
                                            .extractPartUrnName(tmpCmp.getUrnCompleto(), true),
                                    tmpCmp.getEstensioneFile());
                }
                // end EVO#20972
                checkEntry = false;
            } else if (tipoNomeFile.equals(TokenFileNameType.NOME_FILE_VERSATO)) {
                // Nuovo comportamento
                String nomeFileOriginaleVersato = tmpCmp.getNomeFileOriginaleVersato();
                String urnVersato = tmpCmp.getUrnOriginaleVersata();
                String pathCompleto = "";
                /*
                 * Se il nome del file originario non dovesse esserci ci si mette il nome del file
                 * che si metteva in precedenza cioè quello con l'URN
                 */
                if (nomeFileOriginaleVersato == null
                        || nomeFileOriginaleVersato.trim().equals("")) {
                    nomeFileOriginaleVersato = tmpCmp.getNomeFilePerZip();
                }
                if (nomeFileOriginaleVersato == null) {
                    nomeFileOriginaleVersato = "";
                }
                if (urnVersato == null) {
                    urnVersato = "";
                }
                /*
                 * Se non si è riusciti a determinare il nome file originale allora prende l'urn
                 * originale versato
                 */
                if (nomeFileOriginaleVersato.equals("")) {
                    // MEV#23698 - Servizi di recupero: problema con urn definito nel nome
                    // componente
                    pathCompleto = aggiungiConSlashInMezzo("", urnVersato);
                } else {
                    pathCompleto = aggiungiConSlashInMezzo("", nomeFileOriginaleVersato);
                }
                pathCompleto = eliminaEventualiDoppiSlash(pathCompleto);
                // elab file name
                archiveEntryFinalName = DIRECTORY_REC
                        + MessaggiWSFormat.normalizingFileName(pathCompleto);
            } else if (tipoNomeFile.equals(TokenFileNameType.NOME_FILE_URN_VERSATO)) {
                // Nuovo comportamento
                String nomeFileOriginaleVersato = tmpCmp.getNomeFileOriginaleVersato();
                String urnVersato = tmpCmp.getUrnOriginaleVersata();
                /*
                 * Se il nome del file originario non dovesse esserci ci si mette il nome del file
                 * che si metteva in precedenza cioè quello con l'URN
                 */
                if (nomeFileOriginaleVersato == null
                        || nomeFileOriginaleVersato.trim().equals("")) {
                    nomeFileOriginaleVersato = tmpCmp.getNomeFilePerZip();
                }
                if (nomeFileOriginaleVersato == null) {
                    nomeFileOriginaleVersato = "";
                }
                if (urnVersato == null) {
                    urnVersato = "";
                }
                // dopo aver eliminato tutti i possibili NULL...determiniamo il path completo.
                String pathCompleto = "";
                if (urnVersato.trim().equals("")) {
                    pathCompleto = "/" + nomeFileOriginaleVersato;
                    // MEV#23698 - Servizi di recupero: problema con urn definito nel nome
                    // componente
                    pathCompleto = aggiungiConSlashInMezzo("", nomeFileOriginaleVersato);
                } else {
                    /*
                     * Controllo che il nome del file originario non sia presente nell'urn versato,
                     * se esiste si prende direttamente l'urn versato completo (che contiene già il
                     * nome del file)
                     */
                    if (!nomeFileOriginaleVersato.trim().equals("")) {
                        if (urnVersato.endsWith(nomeFileOriginaleVersato)) {
                            // Se l'urn versato inizia per "/" non lo aggiunge al path
                            // MEV#23698 - Servizi di recupero: problema con urn definito nel nome
                            // componente
                            pathCompleto = aggiungiConSlashInMezzo("", urnVersato);
                        } else {
                            // Se l'urn versato inizia per "/" non lo aggiunge al path
                            // MEV#23698 - Servizi di recupero: problema con urn definito nel nome
                            // componente
                            if (urnVersato.startsWith("/")) {
                                pathCompleto = aggiungiConSlashInMezzo(urnVersato,
                                        nomeFileOriginaleVersato);
                            } else {
                                pathCompleto = aggiungiConSlashInMezzo("/" + urnVersato,
                                        nomeFileOriginaleVersato);
                            }
                        }
                    } else {
                        if (urnVersato.startsWith("/")) {
                            pathCompleto = urnVersato;
                        } else {
                            pathCompleto = "/" + urnVersato;
                        }
                    }
                }
                pathCompleto = eliminaEventualiDoppiSlash(pathCompleto);
                archiveEntryFinalName = DIRECTORY_REC
                        + MessaggiWSFormat.normalizingFileName(pathCompleto);
            }

            // MEV#34239 (define zip entry)
            boolean invokeUnsignedDocService = recupero.getStrutturaRecupero()
                    .getParametri() != null
                    && recupero.getStrutturaRecupero().getParametri().isFileSbustato() != null
                    && recupero.getStrutturaRecupero().getParametri().isFileSbustato();
            if (invokeUnsignedDocService) {
                // MEV#39147 - Modifica delle modalità di calcolo dell'estensione nel recupero di
                // file sbustati
                archiveEntryFinalName = archiveEntryFinalName
                        + FilenameUtils.EXTENSION_SEPARATOR_STR
                        + tmpCmp.getNomeFormatoComponenteSbustato();
            }
            // create zip entry
            addZipEntry(zipOutputStream, archiveEntryFinalName, entryGiaInserite, checkEntry);

            if (recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE
                    && recupero.isTpiAbilitato()) {
                // su file system
                rispostaControlli = recuperoCompFS.recuperaFileCompSuStream(tmpCmp, zipOutputStream,
                        recupero);
            } else {
                boolean esitoRecupero = false;

                // recupero documento blob vs obj storage
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                        TiEntitaSacerObjectStorage.COMP_DOC, tmpCmp.getIdCompDoc(), zipOutputStream,
                        RecBlbOracle.TabellaBlob.valueOf(recupero.getTipoSalvataggioFile().name()));

                /* MEV#34239 - Estensione servizio per recupero file sbustati */
                // Se richiesto viene aggiunto allo zip finale la componente "unsigned" (originale)
                // a partire dal documento firmato, se l'esisto del recupero è negativo viene
                // gestito il recupero della componente "standard"
                // Nota : gestitione esclusiva dei soli p7m
                if (invokeUnsignedDocService) {
                    // get unsigned p7m
                    esitoRecupero = recuperoDocumento
                            .callRecuperoOriginalDocFromSignedSuStream(csRecuperoDoc);
                }
                // recupero componente standard
                if (!invokeUnsignedDocService || !esitoRecupero) {
                    esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                }
                //
                rispostaControlli.setrBoolean(esitoRecupero);
                if (!esitoRecupero) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                            "Errore nel recupero dei file per lo zip"));
                }
            }
            zipOutputStream.closeEntry();
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
                break; // esce dal ciclo for, se incontra un errore: ormai lo zip è condannato
            }
        }

    }

    private String eliminaEventualiDoppiSlash(String path) {
        if (path != null) {
            path = path.replaceAll("//", "/");
        }
        return path;
    }

    /*
     * Aggiunge a var1 il contenuto di var2 gestendo correttamente l'eventuale presenza dello slash
     * iniziale in var2. Per evitare che i path si costruiscano col doppio slash che quindi provoca
     * strane alberature nello ZIP
     */
    private String aggiungiConSlashInMezzo(String var1, String var2) {
        // MEV#23698 - Servizi di recupero: problema con urn definito nel nome componente
        // Se var1 (es.:una cartella) è presente in var2 la ignora
        if (var2.indexOf(var1) > -1) {
            var1 = "";
        }
        int ultimo = var1.lastIndexOf("/");
        if (ultimo != -1) { // Se non ha trovato slash bypassa! Altrimenti esplode.
            String porzioneIniziale = var1.substring(0, ultimo);
            // Se in var2 (nome originale) è presente la porzione (es.:una cartella) azzera var1 e
            // quindi considera solo
            // var2
            if (var2.indexOf(porzioneIniziale) > -1) {
                var1 = "";
            }
        }
        // --
        if (var2.startsWith("/")) {
            return var1 + var2;
        } else {
            return var1 + "/" + var2;
        }
    }

    private void aggiungiIndiciAipUdOs(ZipOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {

        RispostaControlli rispostaControlli = new RispostaControlli();
        List<AroVerIndiceAipUd> lstVrsIndice = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPOs(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVrsIndice = (List<AroVerIndiceAipUd>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsIndice != null && !lstVrsIndice.isEmpty()) {
                for (AroVerIndiceAipUd verIndiceLastVers : lstVrsIndice) {
                    // EVO#16486
                    String fileName = "";

                    if (verIndiceLastVers.getAroUrnVerIndiceAipUds() != null
                            && !verIndiceLastVers.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = IterableUtils.find(
                                verIndiceLastVers.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE));
                        if (urnVerIndiceAipUd != null) {
                            fileName = urnVerIndiceAipUd.getDsUrn();
                        }
                    } else {
                        fileName = verIndiceLastVers.getDsUrn();
                    }

                    // end EVO#16486
                    ZipEntry zae = createZipEntry(
                            ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml");
                    zipOutputStream.putNextEntry(zae);
                    // recupero documento blob vs obj storage
                    // build dto per recupero
                    RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                            TiEntitaSacerObjectStorage.INDICE_AIP,
                            verIndiceLastVers.getIdVerIndiceAip(), zipOutputStream,
                            RecClbOracle.TabellaClob.CLOB);
                    // recupero
                    boolean esitoRecupero = recuperoDocumento
                            .callRecuperoDocSuStream(csRecuperoDoc);
                    rispostaControlli.setrBoolean(esitoRecupero);
                    if (!esitoRecupero) {
                        throw new IOException("Errore non gestito nel recupero del file");
                    }
                    zipOutputStream.closeEntry();
                }
            }
        }
    }

    // MEV#30395
    private void aggiungiIndiciAipUdV2Os(ZipOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroVerIndiceAipUd> lstVerIndiceAipUd = null;
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPV2Os(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVerIndiceAipUd = (List<AroVerIndiceAipUd>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVerIndiceAipUd != null && !lstVerIndiceAipUd.isEmpty()) {
                AroVerIndiceAipUd verIndiceLastVers = lstVerIndiceAipUd.remove(0);

                // MEV#27035
                /* Definisco il nome e l'estensione del file */
                fileName = "PIndexUD.xml";
                // end MEV#27035

                ZipEntry verIndiceLastVersZae = createZipEntry(fileName);
                zipOutputStream.putNextEntry(verIndiceLastVersZae);

                // recupero documento blob vs obj storage
                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                        TiEntitaSacerObjectStorage.INDICE_AIP,
                        verIndiceLastVers.getIdVerIndiceAip(), zipOutputStream,
                        RecClbOracle.TabellaClob.CLOB);
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                rispostaControlli.setrBoolean(esitoRecupero);
                if (!esitoRecupero) {
                    throw new IOException("Errore non gestito nel recupero del file");
                }
                zipOutputStream.closeEntry();

                // MEV#20971
                for (AroVerIndiceAipUd verIndicePrecVers : lstVerIndiceAipUd) {
                    String urnIxAip;
                    if (verIndicePrecVers.getAroUrnVerIndiceAipUds() != null
                            && !verIndicePrecVers.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = IterableUtils.find(
                                verIndicePrecVers.getAroUrnVerIndiceAipUds(),
                                object -> (object).getTiUrn().equals(TiUrnVerIxAipUd.ORIGINALE));
                        urnIxAip = urnVerIndiceAipUd.getDsUrn();
                    } else {
                        urnIxAip = verIndicePrecVers.getDsUrn();
                    }

                    /* Definisco la folder relativa al sistema di conservazione */
                    String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_PIX_AIPV2,
                            StringUtils.capitalize(Constants.SACER.toLowerCase()),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                            it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnIxAip, true),
                            it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                    /*
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice
                     * di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                            folder, fileName, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                    ZipEntry verIndicePrecVersZae = createZipEntry(pathPIndexSource);
                    zipOutputStream.putNextEntry(verIndicePrecVersZae);
                    // recupero documento blob vs obj storage
                    // build dto per recupero
                    csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.INDICE_AIP,
                            verIndicePrecVers.getIdVerIndiceAip(), zipOutputStream,
                            RecClbOracle.TabellaClob.CLOB);
                    // recupero
                    esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                    rispostaControlli.setrBoolean(esitoRecupero);
                    if (!esitoRecupero) {
                        throw new IOException("Errore non gestito nel recupero del file");
                    }
                    zipOutputStream.closeEntry();
                }
                // end MEV#20971
            }
        }
    }
    // end MEV#30395

    // EVO#20972:MEV#20971
    private void aggiungiIndiciAipUdExt(ZipOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroVLisaipudSistemaMigraz> lstVrsFileIndiceExt = null;
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPExternal(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndiceExt = (List<AroVLisaipudSistemaMigraz>) rispostaControlli
                        .getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndiceExt != null && !lstVrsFileIndiceExt.isEmpty()) {
                for (AroVLisaipudSistemaMigraz fileIndicePrecVersExt : lstVrsFileIndiceExt) {
                    // Calcolo chiave dell'UD con l'indice unisincro di altri conservatori
                    CSChiave chiaveUDAIPExt = new CSChiave();
                    chiaveUDAIPExt.setAnno(fileIndicePrecVersExt.getAaKeyUnitaDocAip().longValue());
                    chiaveUDAIPExt.setNumero(fileIndicePrecVersExt.getCdKeyUnitaDocAip());
                    chiaveUDAIPExt
                            .setTipoRegistro(fileIndicePrecVersExt.getCdRegistroKeyUnitaDocAip());
                    String partChiaveUDAIPExt = MessaggiWSFormat
                            .formattaUrnPartUnitaDoc(chiaveUDAIPExt);
                    /* Definisco la folder relativa al sistema di conservazione */
                    String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_PIX_AIPV2,
                            fileIndicePrecVersExt.getNmSistemaMigraz(),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    fileName = it.eng.parer.async.utils.IOUtils.getFilename(partChiaveUDAIPExt,
                            it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.ZIP.getFileExt());
                    /*
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice
                     * di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                            folder, fileName, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    //
                    rispostaControlli.reset();
                    rispostaControlli = controlliRecupero.leggiCompFileInUD(
                            fileIndicePrecVersExt.getIdUnitaDocAip().longValue(), true);
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError(rispostaWs, rispostaControlli);
                        rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                                rispostaControlli.getDsErr());
                    } else {
                        List<ComponenteRec> lstComp = (List<ComponenteRec>) rispostaControlli
                                .getrObject();
                        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                            File tmpFile = Files.createTempFile("output_", ".zip").toFile();
                            try {
                                try (FileOutputStream fos = new FileOutputStream(tmpFile);
                                        ZipOutputStream zaos = new ZipOutputStream(fos)) {
                                    Set<String> tmpEntrySet = new HashSet<>();
                                    for (ComponenteRec tmpCmp : lstComp) {
                                        addZipEntry(zaos,
                                                DIRECTORY_REC + "/" + tmpCmp.getNomeFilePerZip(),
                                                tmpEntrySet, false);

                                        // recupero documento blob vs obj storage
                                        // build dto per recupero
                                        RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                                                TiEntitaSacerObjectStorage.COMP_DOC,
                                                tmpCmp.getIdCompDoc(), zaos,
                                                RecBlbOracle.TabellaBlob.BLOB);
                                        // recupero
                                        boolean esitoRecupero = recuperoDocumento
                                                .callRecuperoDocSuStream(csRecuperoDoc);
                                        rispostaControlli.setrBoolean(esitoRecupero);
                                        zaos.closeEntry();
                                        if (!esitoRecupero) {
                                            setRispostaWsError(rispostaWs, rispostaControlli);
                                            rispostaWs.setEsitoWsError(MessaggiWSBundle.ERR_666,
                                                    MessaggiWSBundle.getString(
                                                            MessaggiWSBundle.ERR_666,
                                                            "Errore nel recupero dei file per lo zip"));
                                            break; // esce dal ciclo for, se incontra un errore:
                                                   // ormai lo zip è condannato
                                        }
                                    }
                                    zaos.flush();
                                    zaos.finish();
                                } catch (IOException ex) {
                                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                            "Errore nella generazione dello zip da includere  "
                                                    + ex.getMessage());
                                    log.error("Errore nella generazione dello zip da includere ",
                                            ex);
                                }

                                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                                    ZipEntry zae = createZipEntry(pathPIndexSource);
                                    zipOutputStream.putNextEntry(zae);
                                    zipOutputStream.write(FileUtils.readFileToByteArray(tmpFile));
                                    zipOutputStream.closeEntry();
                                }
                            } finally {
                                FileUtils.deleteQuietly(tmpFile);
                            }
                        }
                    }
                }
            }
        }
    }

    private void aggiungiIndiciAipUdVol(ZipOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<VolVolumeConserv> lstVrsFileIndiceVol = null;
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumiUnitaDoc(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndiceVol = (List<VolVolumeConserv>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndiceVol != null && !lstVrsFileIndiceVol.isEmpty()) {
                for (VolVolumeConserv fileIndicePrecVersVol : lstVrsFileIndiceVol) {
                    // Calcolo urn di tipo NORMALIZZATO dell'indice del volume di conservazione
                    CSVersatore csVersatoreVol = new CSVersatore();
                    csVersatoreVol.setAmbiente(fileIndicePrecVersVol.getOrgStrut().getOrgEnte()
                            .getOrgAmbiente().getNmAmbiente());
                    csVersatoreVol
                            .setEnte(fileIndicePrecVersVol.getOrgStrut().getOrgEnte().getNmEnte());
                    csVersatoreVol.setStruttura(fileIndicePrecVersVol.getOrgStrut().getNmStrut());
                    String urnIxVolCons = MessaggiWSFormat.formattaUrnIndiceVolumeConserv(
                            MessaggiWSFormat.formattaUrnPartVersatore(csVersatoreVol),
                            Long.toString(fileIndicePrecVersVol.getIdVolumeConserv()));
                    /* Definisco la folder relativa al sistema di conservazione */
                    String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_PIX_AIPV2,
                            StringUtils.capitalize(Constants.SACER.toLowerCase()),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                            it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnIxVolCons, true),
                            it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.ZIP.getFileExt());
                    /*
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice
                     * di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                            folder, fileName, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    //
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        File tmpFile = Files.createTempFile("output_", ".zip").toFile();
                        try {
                            try (FileOutputStream fos = new FileOutputStream(tmpFile);
                                    ZipOutputStream zaos = new ZipOutputStream(fos)) {

                                this.aggiungiProveConsUd(zaos, fileIndicePrecVersVol);
                                zaos.flush();
                                zaos.finish();

                            } catch (Exception ex) {
                                rispostaWs.setSeverity(SeverityEnum.ERROR);
                                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                        "Errore nella generazione dello zip da includere  "
                                                + ex.getMessage());
                                log.error("Errore nella generazione dello zip da includere ", ex);
                            }

                            if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                                ZipEntry zae = createZipEntry(pathPIndexSource);
                                zipOutputStream.putNextEntry(zae);
                                zipOutputStream.write(FileUtils.readFileToByteArray(tmpFile));
                                zipOutputStream.closeEntry();
                            }
                        } finally {
                            FileUtils.deleteQuietly(tmpFile);
                        }
                    }
                }
            }
        }
    }
    // end EVO#20972:MEV#20971

    // EVO#20972
    private void aggiungiFileXmlSchema(ZipOutputStream zipOutputStream,
            RispostaWSRecupero rispostaWs) throws IOException {
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {

            File xsdMoreInfoSelfDesc = FileXSDUtil
                    .getFileXSD(FileXSD.AIP_UD_SELF_DESCRIPTION_XSD_V2);
            if (xsdMoreInfoSelfDesc != null) {
                ZipEntry zae = createZipEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                        DIRECTORY_XSD_AIPV2, xsdMoreInfoSelfDesc.getName(),
                        it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                zipOutputStream.putNextEntry(zae);
                zipOutputStream.write(FileUtils
                        .readFileToString(xsdMoreInfoSelfDesc, StandardCharsets.UTF_8).getBytes());
                zipOutputStream.closeEntry();
            }

            File xsdMoreInfoPVolume = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_PVOLUME_XSD_V2);
            if (xsdMoreInfoPVolume != null) {
                ZipEntry zae = createZipEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                        DIRECTORY_XSD_AIPV2, xsdMoreInfoPVolume.getName(),
                        it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                zipOutputStream.putNextEntry(zae);
                zipOutputStream.write(FileUtils
                        .readFileToString(xsdMoreInfoPVolume, StandardCharsets.UTF_8).getBytes());
                zipOutputStream.closeEntry();
            }

            File xsdMoreInfoDoc = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_DOC_XSD);
            if (xsdMoreInfoDoc != null) {
                ZipEntry zae = createZipEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                        DIRECTORY_XSD_AIPV2, xsdMoreInfoDoc.getName(),
                        it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                zipOutputStream.putNextEntry(zae);
                zipOutputStream.write(FileUtils
                        .readFileToString(xsdMoreInfoDoc, StandardCharsets.UTF_8).getBytes());
                zipOutputStream.closeEntry();
            }

            File xsdMoreInfoFile = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_FILE_XSD);
            if (xsdMoreInfoFile != null) {
                ZipEntry zae = createZipEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                        DIRECTORY_XSD_AIPV2, xsdMoreInfoFile.getName(),
                        it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                zipOutputStream.putNextEntry(zae);
                zipOutputStream.write(FileUtils
                        .readFileToString(xsdMoreInfoFile, StandardCharsets.UTF_8).getBytes());
                zipOutputStream.closeEntry();
            }

            // MEV#25921
            File xsdPIndexFile = FileXSDUtil.getFileXSD(FileXSD.UNISINCRO_2_XSD_V2);
            if (xsdPIndexFile != null) {
                ZipEntry zae = createZipEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                        DIRECTORY_XSD_AIPV2, xsdPIndexFile.getName(),
                        it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                zipOutputStream.putNextEntry(zae);
                zipOutputStream.write(FileUtils
                        .readFileToString(xsdPIndexFile, StandardCharsets.UTF_8).getBytes());
                zipOutputStream.closeEntry();
            }
            // end MEV#25921
        }
    }
    // end EVO#20972

    private void aggiungiElencoIndiciAipUd(ZipOutputStream zipOutputStream, long idUnitaDoc,
            String tiFileElencoVers1, String tiFileElencoVers2, RispostaWSRecupero rispostaWs)
            throws IOException {
        ElvFileElencoVer fileElencoVers = null;
        String prefisso = null;
        String estensione = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiElvFileElencoVers(idUnitaDoc,
                    tiFileElencoVers1, tiFileElencoVers2);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                fileElencoVers = rispostaControlli.getrObject() != null
                        ? (ElvFileElencoVer) rispostaControlli.getrObject()
                        : null;
                prefisso = tiFileElencoVers1.equals(
                        ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name()) ? "ElencoIndiceAIP"
                                : "MarcaElencoIndiceAIP";

                // MEV#15967 - Attivazione della firma Xades e XadesT
                if (fileElencoVers != null && fileElencoVers.getTiFirma() != null
                        && fileElencoVers.getTiFirma().equals(ElencoEnums.TipoFirma.XADES.name())) {
                    estensione = ".xml";
                } else {
                    estensione = tiFileElencoVers1.equals(
                            ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name()) ? ".xml.p7m"
                                    : ".tsr";
                }

            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (fileElencoVers != null) {
                controlliRecupero.loadElvElencoVer(fileElencoVers);
                String nmAmbiente = fileElencoVers.getElvElencoVer().getOrgStrut().getOrgEnte()
                        .getOrgAmbiente().getNmAmbiente();
                String nmEnte = fileElencoVers.getElvElencoVer().getOrgStrut().getOrgEnte()
                        .getNmEnte();
                String nmStrut = fileElencoVers.getElvElencoVer().getOrgStrut().getNmStrut();
                String fileName = prefisso + "-UD_" + nmAmbiente + "_" + nmEnte + "_" + nmStrut
                        + "_" + fileElencoVers.getElvElencoVer().getIdElencoVers();
                ZipEntry zae = createZipEntry(fileName + estensione);
                zipOutputStream.putNextEntry(zae);

                // MEV#30397
                // recupero documento blob vs obj storage
                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                        TiEntitaSacerObjectStorage.ELENCO_INDICI_AIP,
                        fileElencoVers.getIdFileElencoVers(), zipOutputStream,
                        RecBlbOracle.TabellaBlob.ELV_FILE_ELENCO);
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                rispostaControlli.setrBoolean(esitoRecupero);
                if (!esitoRecupero) {
                    throw new IOException("Errore non gestito nel recupero del file");
                }
                // end MEV#30397
                zipOutputStream.closeEntry();
            }
        }
    }

    private void aggiungiIndiciSerie(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroUdAppartVerSerie> udAppartVerSerieList = null;
        String prefisso = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiAroUdAppartVerSerie(recupero.getParametriRecupero().getIdUnitaDoc());

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {

                udAppartVerSerieList = (List<AroUdAppartVerSerie>) rispostaControlli.getrObject();

                // Se ho almeno una appartenenza
                if (udAppartVerSerieList != null && !udAppartVerSerieList.isEmpty()) {

                    for (AroUdAppartVerSerie udAppartVerSerie : udAppartVerSerieList) {
                        // MAC #29539 FIX Hibernate: per evitare l'errore
                        // EJBTransactionRolledbackException: - could not
                        // initialize proxy [it.eng.parer.entity.<ENTITY>] - no Session
                        // recupero nuovamente l'entity AroUdAppartVerSerie per recuperare i dati da
                        // AroUnitaDoc,
                        // SerVolVerSerie, SerContenutoVerSerie
                        AroUdAppartVerSerie udAppartInSessione = udHelper.findById(
                                AroUdAppartVerSerie.class,
                                udAppartVerSerie.getIdUdAppartVerSerie());

                        // Creo una cartella con l'urn del codice serie...
                        String codiceSerie = udAppartInSessione.getSerContenutoVerSerie()
                                .getSerVerSerie().getSerSerie().getCdCompositoSerie();
                        // EVO#20972
                        String urnZipArchive = (!recupero.getParametriRecupero()
                                .getTipoEntitaSacer()
                                .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2))
                                        ? DIRECTORY_AGGR + "/" + DIRECTORY_SERIE + "/" + codiceSerie
                                                + "/"
                                        : DIRECTORY_AGGR_AIPV2 + "/" + DIRECTORY_SERIE_AIPV2 + "/"
                                                + codiceSerie + "/";
                        // end EVO#20972

                        String ambiente = udAppartInSessione.getAroUnitaDoc().getOrgStrut()
                                .getOrgEnte().getOrgAmbiente().getNmAmbiente();
                        String ente = udAppartInSessione.getAroUnitaDoc().getOrgStrut().getOrgEnte()
                                .getNmEnte();
                        String struttura = udAppartInSessione.getAroUnitaDoc().getOrgStrut()
                                .getNmStrut();

                        // ... all'interno della quale piazzo il file con l'xml dell'indice del
                        // volume
                        // della serie...
                        if (udAppartInSessione.getSerVolVerSerie() != null) {
                            String urnVol = "IndiceVolumeSerie-" + ambiente + "_" + ente + "_"
                                    + struttura + "_" + codiceSerie + ".xml";
                            ZipEntry zaeVol = createZipEntry(urnZipArchive + urnVol);
                            zipOutputStream.putNextEntry(zaeVol);
                            zipOutputStream.write(
                                    udAppartInSessione.getSerVolVerSerie().getSerIxVolVerSeries()
                                            .get(0).getBlIxVol().getBytes(StandardCharsets.UTF_8));
                            zipOutputStream.closeEntry();
                        }

                        // ... e l'indice aip della serie
                        if (udAppartInSessione.getSerContenutoVerSerie() != null) {
                            byte[] ix = serieEjb.getSerFileVerSerieBlob(
                                    udAppartInSessione.getSerContenutoVerSerie().getSerVerSerie()
                                            .getIdVerSerie(),
                                    CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO);
                            if (ix != null) {
                                prefisso = "IndiceAIPSerie-"
                                        + udAppartInSessione.getSerContenutoVerSerie()
                                                .getSerVerSerie().getCdVerSerie()
                                        + "_" + ambiente + "_" + ente + "_" + struttura + "_"
                                        + codiceSerie;
                                ZipEntry zaeAip = createZipEntry(
                                        urnZipArchive + prefisso + ".xml.p7m");
                                zipOutputStream.putNextEntry(zaeAip);
                                // MAC 32341
                                zipOutputStream.write(ix);
                                zipOutputStream.closeEntry();
                            }
                        }

                        // Se per la versione serie è presente la marca
                        byte[] marcaBlobbo = serieEjb.getSerFileVerSerieBlob(
                                udAppartInSessione.getSerContenutoVerSerie().getSerVerSerie()
                                        .getIdVerSerie(),
                                CostantiDB.TipoFileVerSerie.MARCA_IX_AIP_UNISINCRO);
                        if (marcaBlobbo != null) {
                            ZipEntry zaeMarca = createZipEntry(urnZipArchive + prefisso + ".tsr");
                            zipOutputStream.putNextEntry(zaeMarca);
                            zipOutputStream.write(marcaBlobbo);
                            zipOutputStream.closeEntry();
                        }
                    }
                }
            }
        }
    }

    private void aggiungiIndiceFascicoli(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<FasUnitaDocFascicolo> unitaDocFascicoloList = null;
        String prefisso = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiFasUnitaDocFascicolo(recupero.getParametriRecupero().getIdUnitaDoc());

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {

                unitaDocFascicoloList = (List<FasUnitaDocFascicolo>) rispostaControlli.getrObject();

                // Se ho almeno una appartenenza
                if (unitaDocFascicoloList != null && !unitaDocFascicoloList.isEmpty()) {
                    for (FasUnitaDocFascicolo unitaDocFascicolo : unitaDocFascicoloList) {
                        // Creo una cartella con l'urn del fascicolo...
                        String annoNumeroFascicolo = unitaDocFascicolo.getFasFascicolo()
                                .getAaFascicolo() + "-"
                                + unitaDocFascicolo.getFasFascicolo().getCdKeyFascicolo();
                        annoNumeroFascicolo = MessaggiWSFormat
                                .bonificaUrnPerNomeFile(annoNumeroFascicolo);
                        // EVO#20972
                        String urnZipArchive = (!recupero.getParametriRecupero()
                                .getTipoEntitaSacer()
                                .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2))
                                        ? DIRECTORY_AGGR + "/" + DIRECTORY_FASC + "/"
                                                + annoNumeroFascicolo + "/"
                                        : DIRECTORY_AGGR_AIPV2 + "/" + DIRECTORY_FASC_AIPV2 + "/"
                                                + annoNumeroFascicolo + "/";
                        // end EVO#20972
                        OrgStrut strut = ambientiHelper.findOrgStrutById(
                                unitaDocFascicolo.getFasFascicolo().getOrgStrut().getIdStrut());
                        String ambiente = strut.getOrgEnte().getOrgAmbiente().getNmAmbiente();
                        String ente = strut.getOrgEnte().getNmEnte();
                        String struttura = strut.getNmStrut();
                        String numero = MessaggiWSFormat.bonificaUrnPerNomeFile(
                                unitaDocFascicolo.getFasFascicolo().getCdKeyFascicolo());

                        // Indice aip del fascicolo
                        FasFileMetaVerAipFasc meta = fasHelper.getFasFileMetaVerAipFasc(
                                unitaDocFascicolo.getFasFascicolo().getIdFascicolo(),
                                FasMetaVerAipFascicolo.TiMeta.INDICE.name());

                        if (meta != null) {
                            BigDecimal versione = meta.getFasMetaVerAipFascicolo()
                                    .getFasVerAipFascicolo().getPgVerAipFascicolo();
                            String urnMeta = "PIndexFA";
                            ZipEntry zaeMeta = createZipEntry(urnZipArchive + urnMeta + ".xml");
                            zipOutputStream.putNextEntry(zaeMeta);

                            // MEV 30398
                            String blFile = meta.getBlFileVerIndiceAip();
                            if (blFile == null) {
                                Map<String, String> xmls = objectStorageService
                                        .getObjectXmlIndiceAipFasc(meta.getFasMetaVerAipFascicolo()
                                                .getFasVerAipFascicolo().getIdVerAipFascicolo());
                                // recupero oggetti da O.S. (se presenti)
                                if (!xmls.isEmpty()) {
                                    blFile = xmls.get("INDICE");
                                }
                            }
                            // end MEV 30398
                            if (blFile != null) {
                                zipOutputStream.write(blFile.getBytes());
                            } else {
                                log.warn(
                                        "blFile è null, impossibile scrivere nel file ZIP per il fascicolo con ID: {}",
                                        meta.getFasMetaVerAipFascicolo().getFasVerAipFascicolo()
                                                .getIdVerAipFascicolo());
                            }
                            zipOutputStream.closeEntry();
                        }

                        // Controllo se il fascicolo è inserito in un elenco di versamento fascicoli
                        if (unitaDocFascicolo.getFasFascicolo().getElvElencoVersFasc() != null) {
                            List<ElvFileElencoVersFasc> fileFirma = elencoVersFascHelper
                                    .retrieveFileIndiceElenco(
                                            unitaDocFascicolo.getFasFascicolo()
                                                    .getElvElencoVersFasc().getIdElencoVersFasc(),
                                            new String[] {
                                                    it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.FileTypeEnum.FIRMA_ELENCO_INDICI_AIP
                                                            .name() });

                            if (!fileFirma.isEmpty()) {
                                long idElencoVersFasc = unitaDocFascicolo.getFasFascicolo()
                                        .getElvElencoVersFasc().getIdElencoVersFasc();

                                prefisso = "ElencoIndiciAIP-Fasc_" + ambiente + "_" + ente + "_"
                                        + struttura + "_" + idElencoVersFasc;
                                ZipEntry zaeElenco = createZipEntry(
                                        urnZipArchive + prefisso + ".xml.p7m");
                                zipOutputStream.putNextEntry(zaeElenco);

                                // MEV #30399
                                byte[] blFileElencoVers = fileFirma.get(0).getBlFileElencoVers();
                                long idFileElencoVersFasc = fileFirma.get(0)
                                        .getIdFileElencoVersFasc();
                                if (blFileElencoVers == null) {
                                    blFileElencoVers = objectStorageService
                                            .getObjectElencoIndiciAipFasc(idFileElencoVersFasc);
                                }
                                // end MEV #30399

                                zipOutputStream.write(blFileElencoVers);
                                zipOutputStream.closeEntry();
                            }
                        }
                    }
                }
            }
        }
    }

    private void aggiungiProveConsUd(ZipOutputStream zipOutputStream, VolVolumeConserv volume)
            throws IOException {

        long idVolume = volume.getIdVolumeConserv();
        // ricavo la lista dei blobbi CRL e Certif
        List[] blobbi = componentiHelper.getBlobboByteList(BigDecimal.valueOf(idVolume));
        List<BlobObject> blobbiByteCRLList = blobbi[0];
        List<BlobObject> blobbiByteCertifList = blobbi[1];
        String prefissoVolume = "proveConservazione_vol-" + idVolume + "/";
        String fileName;

        if (blobbiByteCRLList != null) {
            for (BlobObject tmpObject : blobbiByteCRLList) {
                if (tmpObject != null) {
                    fileName = "CRL/CRL_" + tmpObject.id + ".crl";
                    ZipEntry zae = createZipEntry(prefissoVolume + fileName);
                    zipOutputStream.putNextEntry(zae);
                    zipOutputStream.write(tmpObject.blobbo);
                    zipOutputStream.closeEntry();
                }
            }
        }

        if (blobbiByteCertifList != null) {
            for (BlobObject tmpObject : blobbiByteCertifList) {
                if (tmpObject != null) {
                    fileName = "Certificati-Trusted/Certif_Ca_" + tmpObject.id + ".cer";
                    ZipEntry zae = createZipEntry(prefissoVolume + fileName);
                    zipOutputStream.putNextEntry(zae);
                    zipOutputStream.write(tmpObject.blobbo);
                    zipOutputStream.closeEntry();
                }
            }
        }

        byte[] indiceConservXml;

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.INDICE);
        if (indiceConservXml != null) {
            fileName = "indice_conservazione.xml";
            ZipEntry zae = createZipEntry(prefissoVolume + fileName);
            zipOutputStream.putNextEntry(zae);
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.MARCA_INDICE);
        if (indiceConservXml != null) {
            fileName = "indice_conservazione.tsr";
            ZipEntry zae = createZipEntry(prefissoVolume + fileName);
            zipOutputStream.putNextEntry(zae);
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.FIRMA);
        if (indiceConservXml != null) {
            fileName = "firma_indice_conservazione.tsr.p7m";
            ZipEntry zae = createZipEntry(prefissoVolume + fileName);
            zipOutputStream.putNextEntry(zae);
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.MARCA_FIRMA);
        if (indiceConservXml != null) {
            fileName = "firma_indice_conservazione.tsr";
            ZipEntry zae = createZipEntry(prefissoVolume + fileName);
            zipOutputStream.putNextEntry(zae);
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeEntry();
        }
    }

    private byte[] recuperaIndiceCons(VolVolumeConserv volume, FileTypeEnum fileType) {
        byte[] file = null;
        for (VolFileVolumeConserv volFile : controlliRecupero.findVolFileVolumeConserv(volume)) {
            if (fileType.toString().equals(volFile.getTiFileVolumeConserv())) {
                file = volFile.getBlFileVolumeConserv();
            }
        }
        return file;
    }

    private enum TipiLetturaXml {
        UD_COMPLETA, // recupera tutti i file XML dell'UD
        SOLO_FILE_RECUPERATI, // recupera solo gli XML relativi ai file scaricati (tutti per UD,
        // solo DOC per
        // DOC e
        // COMP)
        RECUPERO_PER_ZIP_SIP, // specifico per recupero ZIP SIP
        // MAC#30890
        RECUPERO_PER_ZIP_AIP // specifico per recupero ZIP AIP
        // end MAC#30890
    }

    private enum TipiXmlDaIncludere {
        TUTTI, SOLO_RAPPORTO_VERSAMENTO
    }

    private void aggiungiXMLVersamentoUd(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            TipiLetturaXml tlx, TipiXmlDaIncludere xmlDaIncludere, RispostaWSRecupero rispostaWs)
            throws IOException {
        List<VrsXmlDatiSessioneVers> lstVrsXml = null;
        Map<Long, Map<String, String>> xmlVersamentoOs = new HashMap<>();
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            if (tlx == TipiLetturaXml.UD_COMPLETA) {
                rispostaControlli = controlliRecupero
                        .leggiXMLSessioneversUd(recupero.getParametriRecupero().getIdUnitaDoc());
            } else if (tlx == TipiLetturaXml.SOLO_FILE_RECUPERATI) {
                if (recupero.getParametriRecupero().getIdComponente() != null) {
                    rispostaControlli = controlliRecupero.leggiXMLSessioneversComp(
                            recupero.getParametriRecupero().getIdComponente());
                } else if (recupero.getParametriRecupero().getIdDocumento() != null) {
                    rispostaControlli = controlliRecupero.leggiXMLSessioneversDoc(
                            recupero.getParametriRecupero().getIdDocumento());
                } else {
                    rispostaControlli = controlliRecupero.leggiXMLSessioneversUd(
                            recupero.getParametriRecupero().getIdUnitaDoc());
                }
            } else if (tlx == TipiLetturaXml.RECUPERO_PER_ZIP_AIP) {
                // MAC#30890
                rispostaControlli = controlliRecupero.leggiXmlSessioniVersamentiAip(
                        recupero.getParametriRecupero().getIdUnitaDoc());
                // end MAC#30890
            } else {
                if (recupero.getParametriRecupero().getIdDocumento() != null) {
                    rispostaControlli = controlliRecupero.leggiXMLSessioneVersDocAggiunto(
                            recupero.getParametriRecupero().getIdDocumento());
                } else {
                    rispostaControlli = controlliRecupero.leggiXMLSessioneVersUdPrincipale(
                            recupero.getParametriRecupero().getIdUnitaDoc());
                }
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVrsXml = (List<VrsXmlDatiSessioneVers>) rispostaControlli.getrObject();
            }
        }
        // load from O.S.
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            //
            rispostaControlli = aggiungiXmlDaObjectStorageOnMap(lstVrsXml);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                xmlVersamentoOs = (Map<Long, Map<String, String>>) rispostaControlli.getrObject();
            }
        }
        //
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsXml != null && !lstVrsXml.isEmpty()) {
                int contaRequest = 1;
                int contaResponse = 1;
                int contaIndiceMM = 1;
                int contaRappVers = 1;
                int contaBoh = 1;

                // EVO#20972
                switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
                case UNI_DOC_UNISYNCRO_V2:
                    for (VrsXmlDatiSessioneVers tmpXmlPre : lstVrsXml) {
                        // Recupero l'oggetto VrsXmlDatiSessioneVers per avere "in pancia" anche
                        // eventuali figli della
                        // stessa transazione
                        // inseriti nel caso di Validazione Fascicolo
                        VrsXmlDatiSessioneVers tmpXml = udHelper.findById(
                                VrsXmlDatiSessioneVers.class, tmpXmlPre.getIdXmlDatiSessioneVers());

                        // VrsUrnXmlSessioneVers urnXmlSessioneVers = controlliRecupero
                        // .findVrsUrnXmlSessioneVersByTiUrn(tmpXml,
                        // TiUrnXmlSessioneVers.ORIGINALE);
                        // String urnXmlVers = (urnXmlSessioneVers != null) ?
                        // urnXmlSessioneVers.getDsUrn()
                        // : tmpXml.getDsUrnXmlVers();

                        // urn
                        // Recupero lo urn ORIGINALE dalla tabella VRS_URN_XML_SESSIONE_VERS
                        // MAC 35187
                        String urnXmlVers = "";
                        boolean trovato = false;
                        for (VrsUrnXmlSessioneVers urn : tmpXml.getVrsUrnXmlSessioneVers()) {
                            if (urn.getTiUrn().equals(TiUrnXmlSessioneVers.ORIGINALE)) {
                                urnXmlVers = urn.getDsUrn();
                                trovato = true;
                            }
                        }
                        if (!trovato) {
                            urnXmlVers = tmpXml.getDsUrnXmlVers();
                        }

                        /* Definisco la folder relativa al sistema di conservazione */
                        Constants.TipoSessione tipoSessione = controlliRecupero
                                .getTipoSessioneFrom(tmpXml);
                        String tmpPath = (tipoSessione.equals(Constants.TipoSessione.VERSAMENTO))
                                ? urnXmlVers.replaceAll(it.eng.parer.async.utils.IOUtils
                                        .extractPartUrnName(urnXmlVers), "SIP-UD")
                                : urnXmlVers.replaceAll(it.eng.parer.async.utils.IOUtils
                                        .extractPartUrnName(urnXmlVers), "SIP-AGGIUNTA_DOC");
                        String path = (tipoSessione.equals(Constants.TipoSessione.VERSAMENTO))
                                ? it.eng.parer.async.utils.IOUtils.extractPartUrnName(tmpPath, true)
                                : MessaggiWSFormat.normalizingKey(tmpPath
                                        .substring(tmpPath.lastIndexOf(":DOC")).substring(1));
                        String folder = it.eng.parer.async.utils.IOUtils.getPath(
                                DIRECTORY_SIP_AIPV2, path,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                        /* Definisco il nome e l'estensione del file */
                        fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                                it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnXmlVers,
                                        true),
                                it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                        /*
                         * Definisco il percorso relativo del file rispetto alla posizione
                         * dell'indice di conservazione
                         */
                        String pathSip = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder,
                                fileName, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                        if (pathSip != null) {
                            ZipEntry zipArchiveEntry = createZipEntry(pathSip);
                            zipOutputStream.putNextEntry(zipArchiveEntry);
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero
                                    .findIdSessVersByXmlDatiSessVers(tmpXml);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                                        rispostaControlli.getDsErr());
                            } else {
                                long idSessioneVers = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoOs.containsKey(idSessioneVers)
                                        && tmpXml.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoOs
                                            .get(idSessioneVers);
                                    String blXml = allXml.get(tmpXml.getTiXmlDati());
                                    if (blXml != null) {
                                        zipOutputStream
                                                .write(blXml.getBytes(StandardCharsets.UTF_8));
                                    }
                                } else {
                                    zipOutputStream.write(
                                            tmpXml.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                            zipOutputStream.closeEntry();
                        }
                    }
                    break;
                default:
                    for (VrsXmlDatiSessioneVers tmpXml : lstVrsXml) {
                        fileName = null;
                        switch (tmpXml.getTiXmlDati()) {
                        case CostantiDB.TipiXmlDati.RICHIESTA:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                if (tmpXml.getDsUrnXmlVers() != null
                                        && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                    fileName = tmpXml.getDsUrnXmlVers();
                                } else {
                                    fileName = String.format("IndiceSIP%05d", contaRequest);
                                    contaRequest++;
                                }
                            }
                            break;
                        case CostantiDB.TipiXmlDati.RISPOSTA:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                if (tmpXml.getDsUrnXmlVers() != null
                                        && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                    fileName = tmpXml.getDsUrnXmlVers();
                                } else {
                                    fileName = String.format("EsitoVersamento%05d", contaResponse);
                                    contaResponse++;
                                }
                            }
                            break;
                        case CostantiDB.TipiXmlDati.INDICE_FILE:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                if (tmpXml.getDsUrnXmlVers() != null
                                        && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                    fileName = tmpXml.getDsUrnXmlVers();
                                } else {
                                    fileName = String.format("IndicePI_SIP%05d", contaIndiceMM);
                                    contaIndiceMM++;
                                }
                            }
                            break;
                        case CostantiDB.TipiXmlDati.RAPP_VERS:
                            if (tmpXml.getDsUrnXmlVers() != null
                                    && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                fileName = tmpXml.getDsUrnXmlVers();
                            } else {
                                fileName = String.format("RapportoVersamento%05d", contaRappVers);
                                contaRappVers++;
                            }
                            break;
                        default:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                fileName = String.format("FileXML%05d", contaBoh);
                                contaBoh++;
                            }
                        }

                        if (fileName != null) {
                            ZipEntry zipArchiveEntry = createZipEntry(
                                    ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml");
                            zipOutputStream.putNextEntry(zipArchiveEntry);
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero
                                    .findIdSessVersByXmlDatiSessVers(tmpXml);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                                        rispostaControlli.getDsErr());
                            } else {
                                long idSessioneVers = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoOs.containsKey(idSessioneVers)
                                        && tmpXml.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoOs
                                            .get(idSessioneVers);
                                    String blXml = allXml.get(tmpXml.getTiXmlDati());
                                    if (blXml != null) {
                                        zipOutputStream
                                                .write(blXml.getBytes(StandardCharsets.UTF_8));
                                    }
                                } else {
                                    zipOutputStream.write(
                                            tmpXml.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                            zipOutputStream.closeEntry();
                        }
                    }
                    break;
                }
                // end EVO#20972
            }
        }
    }

    /**
     * Esegui <strong> una volta sola</strong> il caricamento di tutti gli xml legati alle sessioni
     * di versamento identificate dalla lista di VRS_XML_DATI_SESSIONE_VERS.
     *
     * La Map risultante conterrà tutti gli xml identificati dal loro id di sessione.
     *
     * Nell'economina computazionale totale della funziona risulterà necessario scorrere due volte
     * la lista di VRS_XML_DATI_SESSIONE_VERS ma, fortunatamente, sono tutti dati già presenti in
     * memoria.
     *
     * @param vrsXmlDatiSessioneVers Lista di VRS_XML_DATI_SESSIONE_VERS ottenuti dalla query
     *                               precedente
     *
     * @return file xml (come stringa) identificati dall'id della sessione
     */
    private RispostaControlli aggiungiXmlDaObjectStorageOnMap(
            List<VrsXmlDatiSessioneVers> vrsXmlDatiSessioneVers) {
        // filter
        RispostaControlli rispostaControlli = controlliRecupero
                .findAllSessVersByXmlDatiSessVers(vrsXmlDatiSessioneVers);
        if (rispostaControlli.isrBoolean()) {
            Map<Long, Map<String, String>> xmlMapByVrsSessioneVers = new HashMap<>();

            List<VrsSessioneVers> vrsSessioneVerss = (List<VrsSessioneVers>) rispostaControlli
                    .getrObject();
            // filter on Constants.TipoSessione.VERSAMENTO
            vrsSessioneVerss.stream().filter(
                    vrs -> vrs.getTiSessioneVers().equals(Constants.TipoSessione.VERSAMENTO.name())
                            && vrs.getAroUnitaDoc() != null)
                    .forEach(vrs -> // put on map
                    xmlMapByVrsSessioneVers.put(vrs.getIdSessioneVers(), objectStorageService
                            .getObjectSipUnitaDoc(vrs.getAroUnitaDoc().getIdUnitaDoc())));

            // filter on Constants.TipoSessione.AGGIUNGI_DOCUMENTO
            vrsSessioneVerss.stream()
                    .filter(vrs -> vrs.getTiSessioneVers()
                            .equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO.name())
                            && vrs.getAroDoc() != null)
                    .forEach(vrs -> // put on map
                    xmlMapByVrsSessioneVers.put(vrs.getIdSessioneVers(),
                            objectStorageService.getObjectSipDoc(vrs.getAroDoc().getIdDoc())));
            // OK
            rispostaControlli.setrBoolean(true);
            rispostaControlli.setrObject(xmlMapByVrsSessioneVers);
        }
        return rispostaControlli;
    }

    // MEV#29089
    private RispostaControlli aggiungiXmlUpdUdDaObjectStorageOnMap(
            List<AroXmlUpdUnitaDoc> aroXmlUpdUnitaDocs) {
        // filter
        RispostaControlli rispostaControlli = controlliRecupero
                .findAllUpdUnitaDocByXmlUpdUnitaDoc(aroXmlUpdUnitaDocs);
        if (rispostaControlli.isrBoolean()) {
            Map<Long, Map<String, String>> xmlMapByAroUpdUnitaDoc = new HashMap<>();

            List<AroUpdUnitaDoc> aroUpdUnitaDocs = (List<AroUpdUnitaDoc>) rispostaControlli
                    .getrObject();

            // filter on Constants.TipoSessione.VERSAMENTO
            aroUpdUnitaDocs.stream().forEach(upd ->
            // put on map
            xmlMapByAroUpdUnitaDoc.put(upd.getIdUpdUnitaDoc(),
                    objectStorageService.getObjectXmlVersAggMd(upd.getIdUpdUnitaDoc())));

            // OK
            rispostaControlli.setrBoolean(true);
            rispostaControlli.setrObject(xmlMapByAroUpdUnitaDoc);
        }
        return rispostaControlli;
    }
    // end MEV#29089

    // EVO#20972
    private void aggiungiXMLVersamentoUpd(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroXmlUpdUnitaDoc> lstVrsXmlUpd = null;
        Map<Long, Map<String, String>> xmlVersamentoUpdOs = new HashMap<>();
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXmlVersamentiAipUpdDaUnitaDoc(
                    recupero.getParametriRecupero().getIdUnitaDoc());

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                lstVrsXmlUpd = (List<AroXmlUpdUnitaDoc>) rispostaControlli.getrObject();
            }
        }

        // load from O.S.
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsXmlUpd != null && !lstVrsXmlUpd.isEmpty()) {
                rispostaControlli = aggiungiXmlUpdUdDaObjectStorageOnMap(lstVrsXmlUpd);

                if (!rispostaControlli.isrBoolean()) {
                    setRispostaWsError(rispostaWs, rispostaControlli);
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                            rispostaControlli.getDsErr());
                } else {
                    xmlVersamentoUpdOs = (Map<Long, Map<String, String>>) rispostaControlli
                            .getrObject();
                }
            }
        }
        //

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsXmlUpd != null && !lstVrsXmlUpd.isEmpty()) {
                switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
                case UNI_DOC_UNISYNCRO_V2:
                    for (AroXmlUpdUnitaDoc tmpXmlUpd : lstVrsXmlUpd) {
                        // MAC#25915
                        boolean replaceWithAggMd = MessaggiWSFormat.isUrnMatchesSafely(
                                tmpXmlUpd.getDsUrnXml(), Costanti.UrnFormatter.SPATH_AGG_MD_REGEXP);
                        String strReplacement = (replaceWithAggMd) ? "AGG_MD" : "AGGIORNAMENTO_UPD";
                        String matchReplacement = (replaceWithAggMd) ? ":AGG_MD" : ":UPD";
                        int beginIndexReplacement = (replaceWithAggMd) ? 6 : 3;
                        // end MAC#25915
                        /* Definisco la folder relativa al sistema di conservazione */
                        // urn:<...>:UPD00001:SIP-AGGIORNAMENTO_UPD ||
                        // urn:<...>:AGG_MD00001:SIP-AGG_MD
                        String tmpPath = tmpXmlUpd.getDsUrnXml()
                                .replaceAll(it.eng.parer.async.utils.IOUtils.extractPartUrnName(
                                        tmpXmlUpd.getDsUrnXml()), "SIP-" + strReplacement);
                        String[] tmpPathSplit = tmpPath
                                .substring(tmpPath.lastIndexOf(matchReplacement)).substring(1)
                                .split(":");
                        // SIP-AGGIORNAMENTO_UPD00001 || SIP-AGG_MD00001
                        String path = tmpPathSplit[1]
                                + tmpPathSplit[0].substring(beginIndexReplacement);
                        String folder = it.eng.parer.async.utils.IOUtils.getPath(
                                DIRECTORY_SIP_AIPV2, path,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                        /* Definisco il nome e l'estensione del file */
                        fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                                it.eng.parer.async.utils.IOUtils
                                        .extractPartUrnName(tmpXmlUpd.getDsUrnXml(), true),
                                it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                        /*
                         * Definisco il percorso relativo del file rispetto alla posizione
                         * dell'indice di conservazione
                         */
                        String pathSip = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder,
                                fileName, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                        if (pathSip != null) {
                            ZipEntry zipArchiveEntry = createZipEntry(pathSip);
                            zipOutputStream.putNextEntry(zipArchiveEntry);

                            // MEV#29089
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero
                                    .findIdUpdUnitaDocByXmlUpdUnitaDoc(tmpXmlUpd);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                                        rispostaControlli.getDsErr());
                            } else {
                                long idUpdUnitaDoc = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoUpdOs.containsKey(idUpdUnitaDoc)
                                        && tmpXmlUpd.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoUpdOs
                                            .get(idUpdUnitaDoc);
                                    String blXml = allXml
                                            .get(tmpXmlUpd.getTiXmlUpdUnitaDoc().name());
                                    zipOutputStream.write(blXml.getBytes(StandardCharsets.UTF_8));
                                } else {
                                    zipOutputStream.write(
                                            tmpXmlUpd.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                            // end MEV#29089
                            zipOutputStream.closeEntry();
                        }
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }
    // end EVO#20972

    private void aggiungiComponentiDIP(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            RispostaControlli rispostaControlli = controlliRecDip
                    .contaComponenti(recupero.getParametriRecupero());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else if (rispostaControlli.getrLong() > 0) {
                if (rispostaWs.getDatiRecuperoDip() == null) {
                    rispostaWs.setDatiRecuperoDip(new DatiRecuperoDip());
                }
                rispostaWs.getDatiRecuperoDip()
                        .setNumeroElementiTrovati(rispostaControlli.getrLong());
                //
                gestSessRecDip.caricaParametri(recupero);
                //
                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                    gestSessRecDip.caricaListaComponenti(recupero);
                }

                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                    try {
                        gestSessRecDip.recuperaConvInZip(zipOutputStream, recupero,
                                rispostaWs.getDatiRecuperoDip().getElementiTrovati(),
                                DIRECTORY_OUT_DIP);
                    } catch (Exception e) {
                        rispostaWs.setSeverity(SeverityEnum.ERROR);
                        rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                "Errore nella fase di aggiunta DIP del EJB " + e.getMessage());
                        log.error("Errore nella fase di aggiunta DIP del EJB ", e);
                    }
                }
            }
        }
    }

    private void aggiungiDipEsibizione(ZipOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        RecuperoTxtGen recuperoTxtGen = new RecuperoTxtGen(rispostaWs);

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                recuperoTxtGen.generaDipEsibizione(zipOutputStream, recupero);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella fase di aggiunta DIP per esibizione del EJB "
                                + e.getMessage());
                log.error("Errore nella fase di aggiunta DIP per esibizione del EJB ", e);
            }
        }
    }

    public void generaZipReportFirma(String outputPath, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        FileBinario zipDaScaricare = new FileBinario();
        Recupero parsedUnitaDoc = recupero.getStrutturaRecupero();
        CSChiave tmpCSChiave = new CSChiave();
        tmpCSChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
        tmpCSChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
        tmpCSChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            // check report
            rispostaControlli = controlliReportvf.checkReportvfExistenceAndGetZipName(
                    recupero.getParametriRecupero().getIdComponente());
            //
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                        rispostaControlli.getDsErr());
            } else {
                String fileReportvfZipName = rispostaControlli.getrString();
                try {
                    // create final zip
                    zipDaScaricare.setFileSuDisco(
                            File.createTempFile("output_", ".zip", new File(outputPath)));
                    // manage report
                    controlliReportvf.generateReportvf(recupero, zipDaScaricare);

                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        // mimetype reponse
                        rispostaWs.setMimeType("application/zip");
                        // filename
                        rispostaWs.setNomeFile(fileReportvfZipName + ".zip");
                    }
                } catch (IOException | SacerException ex) {
                    rispostaWs.setSeverity(SeverityEnum.ERROR);
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                            "Errore nella generazione del report da scaricare  " + ex.getMessage());
                    log.error("Errore nella generazione del report da scaricare ", ex);
                } finally {
                    //
                    if (rispostaWs.getSeverity() == SeverityEnum.ERROR
                            && zipDaScaricare.getFileSuDisco() != null) {
                        try {
                            Files.delete(zipDaScaricare.getFileSuDisco().toPath());
                        } catch (IOException e) {
                            rispostaWs.setSeverity(SeverityEnum.ERROR);
                            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                    "Errore durante cancellazione report generato");
                        }
                        zipDaScaricare.setFileSuDisco(null);
                    }
                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    private void setRispostaWsError(RispostaWSRecupero rispostaWs,
            RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }
}
