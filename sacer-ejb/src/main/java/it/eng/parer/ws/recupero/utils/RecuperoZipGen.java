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

package it.eng.parer.ws.recupero.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.Marshaller;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelper;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
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
import it.eng.parer.ws.recupero.ejb.ControlliRecupero;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recupero.ejb.oracleClb.RecClbOracle;
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

    // The ZipArchiveEntry.setXxxTime() methods write the time taking into account
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
            RispostaWSRecupero rispostaWs) {
        generaZipOggetto(outputPath, recupero, tentaRecuperoDip, rispostaWs);

        if (rispostaWs.getRifFileBinario() != null && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            return rispostaWs.getRifFileBinario().getFileSuDisco();
        } else {
            return null;
        }

    }

    public void generaZipOggetto(String outputPath, RecuperoExt recupero, boolean tentaRecuperoDip,
            RispostaWSRecupero rispostaWs) {
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
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
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
                    rispostaControlli = controlliRecupero
                            .leggiCompFileInUD(recupero.getParametriRecupero().getIdUnitaDoc());
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
                recuperaDip = false; // per il sottocomponente non si includono mai i file convertiti
                break;
            //
            case UNI_DOC_UNISYNCRO:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInUD(recupero.getParametriRecupero().getIdUnitaDoc());
                prefisso = "AIP_";
                includiFileIndiceAIPV2 = true;
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
                        .leggiCompFileInUD(recupero.getParametriRecupero().getIdUnitaDoc());
                prefisso = "DIP_UD_";
                includiRapportoVersamento = true;
                recuperaDip = false; // per il recupero DIP per esibizione non si includono mai i file convertiti
                recuperaDipEsibizione = true;
                break;
            case DOC_DIP_ESIBIZIONE:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInDoc(recupero.getParametriRecupero().getIdDocumento());
                prefisso = "DIP_DOC_";
                includiRapportoVersamento = true;
                recuperaDip = false; // per il recupero DIP per esibizione non si includono mai i file convertiti
                recuperaDipEsibizione = true;
                break;
            case COMP_DIP_ESIBIZIONE:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInComp(recupero.getParametriRecupero().getIdComponente());
                prefisso = "DIP_COMP_";
                includiRapportoVersamento = true;
                recuperaDip = false; // per il recupero DIP per esibizione non si includono mai i file convertiti
                recuperaDipEsibizione = true;
                break;
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstComp = (List<ComponenteRec>) rispostaControlli.getrObject();
                nomeFileZip = rispostaControlli.getrString();
                rispostaWs.setNomeFile(prefisso + nomeFileZip + ".zip");
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);

                if (recuperaDip && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiComponentiDIP(tmpZipOutputStream, recupero, rispostaWs);
                }

                if (recuperaDipEsibizione && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiDipEsibizione(tmpZipOutputStream, recupero, rispostaWs);
                }

                if (includiSessFileVersamento && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero, TipiLetturaXml.SOLO_FILE_RECUPERATI,
                            TipiXmlDaIncludere.TUTTI, rispostaWs);
                }

                // MAC#30890
                if (includiSessFileVersamentoV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero, TipiLetturaXml.RECUPERO_PER_ZIP_AIP,
                            TipiXmlDaIncludere.TUTTI, rispostaWs);
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
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero, TipiLetturaXml.SOLO_FILE_RECUPERATI,
                            TipiXmlDaIncludere.SOLO_RAPPORTO_VERSAMENTO, rispostaWs);
                }

                this.aggiungiFileComponenti(tmpZipOutputStream, recupero, lstComp, rispostaWs);

                if (includiFileIndiceAIP && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP
                    this.aggiungiIndiciAipUd(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            rispostaWs);
                }

                // EVO#20972:MEV#20971
                if (includiFileIndiceAIPV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // MEV#30395
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di Sacer
                    this.aggiungiIndiciAipUdV2Os(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            rispostaWs);
                    // end MEV#30395
                }

                if (includiFileIndiceAIPExt && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di altri
                    // conservatori
                    this.aggiungiIndiciAipUdExt(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            rispostaWs);
                }

                if (includiFileIndiceAIPVol && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice dei Volumi di
                    // conservazione di Sacer
                    this.aggiungiIndiciAipUdVol(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            rispostaWs);
                }
                // end EVO#20972:MEV#20971

                if (includiFirmaMarcaElencoIndiceAIP && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiElencoIndiciAipUd(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.FIRMA_ELENCO_INDICI_AIP
                                    .name(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.ELENCO_INDICI_AIP.name(),
                            rispostaWs);
                    this.aggiungiElencoIndiciAipUd(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
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

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
                    zipDaScaricare.getFileSuDisco().delete();
                    zipDaScaricare.setFileSuDisco(null);
                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    private ZipArchiveEntry filterZipEntry(ZipArchiveEntry entry) {
        // Set times
        entry.setCreationTime(FileTime.fromMillis(DEFAULT_ZIP_TIMESTAMP));
        entry.setLastAccessTime(FileTime.fromMillis(DEFAULT_ZIP_TIMESTAMP));
        entry.setLastModifiedTime(FileTime.fromMillis(DEFAULT_ZIP_TIMESTAMP));
        entry.setTime(DEFAULT_ZIP_TIMESTAMP);
        // Remove extended timestamps
        for (ZipExtraField field : entry.getExtraFields()) {
            if (field instanceof X5455_ExtendedTimestamp) {
                entry.removeExtraField(field.getHeaderId());
            }
        }
        return entry;
    }
    // EVO#20972

    public File getZipProveCons(String outputPath, RecuperoExt recupero, RispostaWSRecupero rispostaWs) {
        if (recupero.getParametriRecupero().getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.PROVE_CONSERV_AIPV2) {
            generaZipProveConsAIPV2(outputPath, recupero, rispostaWs);
        } else {
            generaZipProveCons(outputPath, recupero, rispostaWs);
        }

        if (rispostaWs.getRifFileBinario() != null && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            return rispostaWs.getRifFileBinario().getFileSuDisco();
        } else {
            return null;
        }
    }

    public void generaZipProveConsAIPV2(String outputPath, RecuperoExt recupero, RispostaWSRecupero rispostaWs) {
        VolVolumeConserv volVolumeConserv = null;
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumeConserv(recupero.getParametriRecupero().getIdVolume());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                volVolumeConserv = (VolVolumeConserv) rispostaControlli.getrObject();
            }

        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);

                if (volVolumeConserv != null) {
                    this.aggiungiProveConsUd(tmpZipOutputStream, volVolumeConserv);
                }

                tmpZipOutputStream.flush();

            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
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

    public void generaZipProveCons(String outputPath, RecuperoExt recupero, RispostaWSRecupero rispostaWs) {
        rispostaWs.getIstanzaEsito();
        rispostaWs.getAvanzamento();
        List<VolVolumeConserv> volVolumeConserv = null;
        FileBinario zipDaScaricare = null;
        byte[] tmpXmlByteArrProveC = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
        ZipArchiveEntry tmpEntry = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumiUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
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
                zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);

                tmpEntry = new ZipArchiveEntry("IndiceProveConservazione.xml");
                tmpZipOutputStream.putArchiveEntry(tmpEntry);
                tmpZipOutputStream.write(tmpXmlByteArrProveC);
                tmpZipOutputStream.closeArchiveEntry();

                if (volVolumeConserv != null && !volVolumeConserv.isEmpty()) {
                    for (VolVolumeConserv tmpVolConserv : volVolumeConserv) {
                        this.aggiungiProveConsUd(tmpZipOutputStream, tmpVolConserv);
                    }
                }

                tmpZipOutputStream.flush();

            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
                    zipDaScaricare.getFileSuDisco().delete();
                    zipDaScaricare.setFileSuDisco(null);
                }
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    public void generaZipRapportiVers(String outputPath, RecuperoExt recupero, RispostaWSRecupero rispostaWs) {
        rispostaWs.getAvanzamento();
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
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
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsXml = (List<VrsXmlDatiSessioneVers>) rispostaControlli.getrObject();
            }
            // load from O.S.
            if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                //
                rispostaControlli = aggiungiXmlDaObjectStorageOnMap(lstVrsXml);

                if (!rispostaControlli.isrBoolean()) {
                    setRispostaWsError(rispostaWs, rispostaControlli);
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                } else {
                    xmlVersamentoOs = (Map<Long, Map<String, String>>) rispostaControlli.getrObject();
                }
            }
            //
            if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                int fileTrovati = 0;
                if (lstVrsXml != null && !lstVrsXml.isEmpty()) {
                    //
                    zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                    tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                    tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);
                    String fileName;
                    int contaRappVers = 1;
                    for (VrsXmlDatiSessioneVers tmpXml : lstVrsXml) {
                        fileName = null;
                        if (CostantiDB.TipiXmlDati.RAPP_VERS.equals(tmpXml.getTiXmlDati())) {
                            fileTrovati++;
                            if (tmpXml.getDsUrnXmlVers() != null && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                fileName = tmpXml.getDsUrnXmlVers();
                            } else {
                                fileName = String.format("RapportoVersamento%05d", contaRappVers);
                                contaRappVers++;
                            }
                        } else {
                            fileName = null;
                        }
                        if (fileName != null) {
                            tmpZipOutputStream.putArchiveEntry(
                                    new ZipArchiveEntry(ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml"));
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero.findIdSessVersByXmlDatiSessVers(tmpXml);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                            } else {
                                long idSessioneVers = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoOs.containsKey(idSessioneVers) && tmpXml.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoOs.get(idSessioneVers);
                                    String blXml = allXml.get(tmpXml.getTiXmlDati());
                                    tmpZipOutputStream.write(blXml.getBytes(StandardCharsets.UTF_8));
                                } else {
                                    tmpZipOutputStream.write(tmpXml.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }

                            tmpZipOutputStream.closeArchiveEntry();
                        }
                    }
                    tmpZipOutputStream.flush();
                }
                if (fileTrovati == 0) {
                    rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.UD_005_004,
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpCSChiave));
                }
            }
        } catch (Exception ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_GENERAZIONE_ZIP + ex.getMessage());
            log.error("Errore nella generazione dello zip da scaricare ", ex);
        } finally {
            IOUtils.closeQuietly(tmpZipOutputStream);
            IOUtils.closeQuietly(tmpOutputStream);
            tmpZipOutputStream = null;
            tmpOutputStream = null;
            if (rispostaWs.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
                zipDaScaricare.getFileSuDisco().delete();
                zipDaScaricare.setFileSuDisco(null);
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaWs.setRifFileBinario(zipDaScaricare);
        }
    }

    public void generaZipSip(String outputPath, RecuperoExt recupero, RispostaWSRecupero rispostaWs) {
        List<ComponenteRec> lstComp = null;
        String prefisso = null;
        String nomeFileZip = null;
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;

        // legge l'elenco dei componenti di tipo file nell'UD, per estrarre i relativi
        // blob
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
            case UNI_DOC:
                rispostaControlli = controlliRecupero
                        .leggiCompFileInUDVersamentoUd(recupero.getParametriRecupero().getIdUnitaDoc());
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
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstComp = (List<ComponenteRec>) rispostaControlli.getrObject();
                nomeFileZip = rispostaControlli.getrString();
                rispostaWs.setNomeFile(prefisso + nomeFileZip + ".zip");
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);

                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero, TipiLetturaXml.RECUPERO_PER_ZIP_SIP,
                            TipiXmlDaIncludere.TUTTI, rispostaWs);
                }

                this.aggiungiFileComponenti(tmpZipOutputStream, recupero, lstComp, rispostaWs);

                tmpZipOutputStream.flush();

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_GENERAZIONE_ZIP + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                if (rispostaWs.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
                    zipDaScaricare.getFileSuDisco().delete();
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
            Marshaller marshaller = xmlContextCache.getVersRespStatoCtx_IndiceProveCons().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(rispostaWs.getIndiceProveConservazione(), tmpStringWriter);

            tmpStringXml = tmpStringWriter.toString();
        } catch (Exception ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_SERIALIZZAZIONE_XML + ex.getMessage());
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
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, ERRORE_SERIALIZZAZIONE_XML + ex.getMessage());
                log.error(ERRORE_SERIALIZZAZIONE_XML, ex);
            }
        }
        return tmpXmlByteArr;
    }

    private void aggiungiFileComponenti(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            List<ComponenteRec> lstComp, RispostaWSRecupero rispostaWs) throws IOException {
        RispostaControlli rispostaControlli = new RispostaControlli();
        Set<String> entryGiaInserite = new HashSet<>();
        TokenFileNameType tipoNomeFile = recupero.getStrutturaRecupero().getChiave().getTipoNomeFile();
        for (ComponenteRec tmpCmp : lstComp) {
            ZipArchiveEntry zae = null;
            // Se viene passato NIENTE oppure NOME_FILE_URN_SACER si vuole il vecchio
            // comportamento
            if (tipoNomeFile == null || tipoNomeFile.value().trim().equals("")
                    || tipoNomeFile.equals(TokenFileNameType.NOME_FILE_URN_SACER)) {
                // EVO#20972
                if (!recupero.getParametriRecupero().getTipoEntitaSacer()
                        .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2)) {
                    // Vecchio comportamento
                    zae = new ZipArchiveEntry(DIRECTORY_REC + "/" + tmpCmp.getNomeFilePerZip());
                } else {
                    zae = new ZipArchiveEntry(DIRECTORY_REC_AIPV2 + "/"
                            + it.eng.parer.async.utils.IOUtils.getFilename(
                                    it.eng.parer.async.utils.IOUtils.extractPartUrnName(tmpCmp.getUrnCompleto(), true),
                                    tmpCmp.getEstensioneFile()));
                }
                // end EVO#20972
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
            } else if (tipoNomeFile.equals(TokenFileNameType.NOME_FILE_VERSATO)) {
                // Nuovo comportamento
                String nomeFileOriginaleVersato = tmpCmp.getNomeFileOriginaleVersato();
                String urnVersato = tmpCmp.getUrnOriginaleVersata();
                String pathCompleto = "";
                /*
                 * Se il nome del file originario non dovesse esserci ci si mette il nome del file che si metteva in
                 * precedenza cioè quello con l'URN
                 */
                if (nomeFileOriginaleVersato == null || nomeFileOriginaleVersato.trim().equals("")) {
                    nomeFileOriginaleVersato = tmpCmp.getNomeFilePerZip();
                }
                if (nomeFileOriginaleVersato == null) {
                    nomeFileOriginaleVersato = "";
                }
                if (urnVersato == null) {
                    urnVersato = "";
                }
                /*
                 * Se non si è riusciti a determinare il nome file originale allora prende l'urn originale versato
                 */
                if (nomeFileOriginaleVersato.equals("")) {
                    // MEV#23698 - Servizi di recupero: problema con urn definito nel nome componente
                    pathCompleto = aggiungiConSlashInMezzo("", urnVersato);
                } else {
                    pathCompleto = aggiungiConSlashInMezzo("", nomeFileOriginaleVersato);
                }
                pathCompleto = eliminaEventualiDoppiSlash(pathCompleto);
                provaAdInserireEntry(zipOutputStream,
                        DIRECTORY_REC + MessaggiWSFormat.normalizingFileName(pathCompleto), entryGiaInserite);

            } else if (tipoNomeFile.equals(TokenFileNameType.NOME_FILE_URN_VERSATO)) {
                // Nuovo comportamento
                String nomeFileOriginaleVersato = tmpCmp.getNomeFileOriginaleVersato();
                String urnVersato = tmpCmp.getUrnOriginaleVersata();
                /*
                 * Se il nome del file originario non dovesse esserci ci si mette il nome del file che si metteva in
                 * precedenza cioè quello con l'URN
                 */
                if (nomeFileOriginaleVersato == null || nomeFileOriginaleVersato.trim().equals("")) {
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
                    // MEV#23698 - Servizi di recupero: problema con urn definito nel nome componente
                    pathCompleto = aggiungiConSlashInMezzo("", nomeFileOriginaleVersato);
                } else {
                    /*
                     * Controllo che il nome del file originario non sia presente nell'urn versato, se esiste si prende
                     * direttamente l'urn versato completo (che contiene già il nome del file)
                     */
                    if (!nomeFileOriginaleVersato.trim().equals("")) {
                        if (urnVersato.endsWith(nomeFileOriginaleVersato)) {
                            // Se l'urn versato inizia per "/" non lo aggiunge al path
                            // MEV#23698 - Servizi di recupero: problema con urn definito nel nome componente
                            pathCompleto = aggiungiConSlashInMezzo("", urnVersato);
                        } else {
                            // Se l'urn versato inizia per "/" non lo aggiunge al path
                            // MEV#23698 - Servizi di recupero: problema con urn definito nel nome componente
                            if (urnVersato.startsWith("/")) {
                                pathCompleto = aggiungiConSlashInMezzo(urnVersato, nomeFileOriginaleVersato);
                            } else {
                                pathCompleto = aggiungiConSlashInMezzo("/" + urnVersato, nomeFileOriginaleVersato);
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
                provaAdInserireEntry(zipOutputStream,
                        DIRECTORY_REC + MessaggiWSFormat.normalizingFileName(pathCompleto), entryGiaInserite);
            }

            if (recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE && recupero.isTpiAbilitato()) {
                // su file system
                rispostaControlli = recuperoCompFS.recuperaFileCompSuStream(tmpCmp, zipOutputStream, recupero);
            } else {
                // recupero documento blob vs obj storage

                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.COMP_DOC,
                        tmpCmp.getIdCompDoc(), zipOutputStream,
                        RecBlbOracle.TabellaBlob.valueOf(recupero.getTipoSalvataggioFile().name()));
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                rispostaControlli.setrBoolean(esitoRecupero);
                if (!esitoRecupero) {
                    rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                            "Errore nel recupero dei file per lo zip"));
                }

            }
            zipOutputStream.closeArchiveEntry();
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
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
     * Aggiunge a var1 il contenuto di var2 gestendo correttamente l'eventuale presenza dello slash iniziale in var2.
     * Per evitare che i path si costruiscano col doppio slash che quindi provoca strane alberature nello ZIP
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
            // Se in var2 (nome originale) è presente la porzione (es.:una cartella) azzera var1 e quindi considera solo
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

    private void provaAdInserireEntry(ZipArchiveOutputStream zipStream, String path, Set<String> entryGiaInserite)
            throws IOException {
        final int MAX_ITERAZIONI = 1000;
        int i = 0;
        int contatoreCopie = 0;
        String nuovoNomeFile = path;
        for (i = 0; i < MAX_ITERAZIONI; i++) {
            if (entryGiaInserite.contains(nuovoNomeFile)) {
                contatoreCopie++;
                int posPunto = path.lastIndexOf(".");
                if (posPunto < 0) {
                    nuovoNomeFile = path + "(" + contatoreCopie + ")";
                } else {
                    nuovoNomeFile = path.substring(0, posPunto) + "(" + contatoreCopie + ")"
                            + path.substring(posPunto, path.length());
                }
            } else {
                ZipArchiveEntry zipEntry = new ZipArchiveEntry(nuovoNomeFile);
                this.filterZipEntry(zipEntry);
                zipStream.putArchiveEntry(zipEntry);
                entryGiaInserite.add(nuovoNomeFile);
                break;
            }
        }
        if (i == MAX_ITERAZIONI) {
            throw new IOException(
                    "Raggiunto il numero massimo di iterazioni per gestire uno stesso nome file: [" + path + "]");
        }

    }

    private void aggiungiIndiciAipUd(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {

        RispostaControlli rispostaControlli = new RispostaControlli();
        List<AroFileVerIndiceAipUd> lstVrsFileIndice = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIP(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndice = (List<AroFileVerIndiceAipUd>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndice != null && !lstVrsFileIndice.isEmpty()) {
                for (AroFileVerIndiceAipUd fileIndice : lstVrsFileIndice) {
                    // EVO#16486
                    String fileName = "";
                    if (fileIndice.getAroVerIndiceAipUd() != null) {
                        if (fileIndice.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds() != null
                                && !fileIndice.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds().isEmpty()) {
                            // Recupero lo urn ORIGINALE
                            AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils.find(
                                    fileIndice.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds(), new Predicate() {
                                        @Override
                                        public boolean evaluate(final Object object) {
                                            return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                    .equals(TiUrnVerIxAipUd.ORIGINALE);
                                        }
                                    });
                            if (urnVerIndiceAipUd != null) {
                                fileName = urnVerIndiceAipUd.getDsUrn();
                            }
                        } else {
                            fileName = fileIndice.getAroVerIndiceAipUd().getDsUrn();
                        }
                    }
                    // end EVO#16486
                    ZipArchiveEntry zae = new ZipArchiveEntry(ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml");
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);
                    zipOutputStream.write(fileIndice.getBlFileVerIndiceAip().getBytes());
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }
    }

    // MEV#30395
    private void aggiungiIndiciAipUdV2Os(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroVerIndiceAipUd> lstVerIndiceAipUd = null;
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPV2Os(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVerIndiceAipUd = (List<AroVerIndiceAipUd>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVerIndiceAipUd != null && !lstVerIndiceAipUd.isEmpty()) {
                boolean closeEntry = false;
                AroVerIndiceAipUd verIndiceLastVers = lstVerIndiceAipUd.remove(0);

                // MEV#27035
                /* Definisco il nome e l'estensione del file */
                fileName = "PIndexUD.xml";
                // end MEV#27035

                ZipArchiveEntry verIndiceLastVersZae = new ZipArchiveEntry(fileName);
                this.filterZipEntry(verIndiceLastVersZae);
                zipOutputStream.putArchiveEntry(verIndiceLastVersZae);

                // recupero documento blob vs obj storage
                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.INDICE_AIP,
                        verIndiceLastVers.getIdVerIndiceAip(), zipOutputStream, RecClbOracle.TabellaClob.CLOB);
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                rispostaControlli.setrBoolean(esitoRecupero);
                closeEntry = true;
                if (!esitoRecupero) {
                    throw new IOException("Errore non gestito nel recupero del file");
                }

                // MEV#20971
                for (AroVerIndiceAipUd verIndicePrecVers : lstVerIndiceAipUd) {
                    String urnIxAip;
                    if (verIndicePrecVers.getAroUrnVerIndiceAipUds() != null
                            && !verIndicePrecVers.getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils
                                .find(verIndicePrecVers.getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.ORIGINALE);
                                    }
                                });
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
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                    ZipArchiveEntry verIndicePrecVersZae = new ZipArchiveEntry(pathPIndexSource);
                    this.filterZipEntry(verIndicePrecVersZae);
                    zipOutputStream.putArchiveEntry(verIndicePrecVersZae);
                    // recupero documento blob vs obj storage
                    // build dto per recupero
                    csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.INDICE_AIP,
                            verIndicePrecVers.getIdVerIndiceAip(), zipOutputStream, RecClbOracle.TabellaClob.CLOB);
                    // recupero
                    esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                    rispostaControlli.setrBoolean(esitoRecupero);
                    closeEntry = true;
                    if (!esitoRecupero) {
                        throw new IOException("Errore non gestito nel recupero del file");
                    }

                }
                // end MEV#20971
                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }
    }
    // end MEV#30395

    // EVO#20972:MEV#20971
    private void aggiungiIndiciAipUdExt(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroVLisaipudSistemaMigraz> lstVrsFileIndiceExt = null;
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPExternal(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndiceExt = (List<AroVLisaipudSistemaMigraz>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndiceExt != null && !lstVrsFileIndiceExt.isEmpty()) {
                boolean closeEntry = false;

                for (AroVLisaipudSistemaMigraz fileIndicePrecVersExt : lstVrsFileIndiceExt) {
                    // Calcolo chiave dell'UD con l'indice unisincro di altri conservatori
                    CSChiave chiaveUDAIPExt = new CSChiave();
                    chiaveUDAIPExt.setAnno(fileIndicePrecVersExt.getAaKeyUnitaDocAip().longValue());
                    chiaveUDAIPExt.setNumero(fileIndicePrecVersExt.getCdKeyUnitaDocAip());
                    chiaveUDAIPExt.setTipoRegistro(fileIndicePrecVersExt.getCdRegistroKeyUnitaDocAip());
                    String partChiaveUDAIPExt = MessaggiWSFormat.formattaUrnPartUnitaDoc(chiaveUDAIPExt);
                    /* Definisco la folder relativa al sistema di conservazione */
                    String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_PIX_AIPV2,
                            fileIndicePrecVersExt.getNmSistemaMigraz(),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    /* Definisco il nome e l'estensione del file */
                    fileName = it.eng.parer.async.utils.IOUtils.getFilename(partChiaveUDAIPExt,
                            it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.ZIP.getFileExt());
                    /*
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    //
                    rispostaControlli.reset();
                    rispostaControlli = controlliRecupero
                            .leggiCompFileInUD(fileIndicePrecVersExt.getIdUnitaDocAip().longValue());
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError(rispostaWs, rispostaControlli);
                        rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                    } else {
                        List<ComponenteRec> lstComp = (List<ComponenteRec>) rispostaControlli.getrObject();
                        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                            FileBinario zipDaIncludere = new FileBinario();
                            File tmpFile = Files.createTempFile("output_", ".zip").toFile();
                            zipDaIncludere.setFileSuDisco(tmpFile);

                            try (FileOutputStream fos = new FileOutputStream(zipDaIncludere.getFileSuDisco());
                                    ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(fos)) {

                                for (ComponenteRec tmpCmp : lstComp) {
                                    ZipArchiveEntry zae = new ZipArchiveEntry(
                                            DIRECTORY_REC + "/" + tmpCmp.getNomeFilePerZip());
                                    this.filterZipEntry(zae);
                                    zaos.putArchiveEntry(zae);

                                    // recupero documento blob vs obj storage
                                    // build dto per recupero
                                    RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                                            TiEntitaSacerObjectStorage.COMP_DOC, tmpCmp.getIdCompDoc(), zaos,
                                            RecBlbOracle.TabellaBlob.BLOB);
                                    // recupero
                                    boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                                    rispostaControlli.setrBoolean(esitoRecupero);
                                    zaos.closeArchiveEntry();
                                    if (!esitoRecupero) {
                                        setRispostaWsError(rispostaWs, rispostaControlli);
                                        rispostaWs.setEsitoWsError(MessaggiWSBundle.ERR_666, MessaggiWSBundle.getString(
                                                MessaggiWSBundle.ERR_666, "Errore nel recupero dei file per lo zip"));
                                        break; // esce dal ciclo for, se incontra un errore: ormai lo zip è condannato
                                    }
                                }

                                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                                    rispostaWs.setRifFileBinario(zipDaIncludere);
                                }
                            } catch (IOException ex) {
                                rispostaWs.setSeverity(SeverityEnum.ERROR);
                                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                        "Errore nella generazione dello zip da includere  " + ex.getMessage());
                                log.error("Errore nella generazione dello zip da includere ", ex);
                            }
                        }
                    }
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        ZipArchiveEntry zae = new ZipArchiveEntry(pathPIndexSource);
                        this.filterZipEntry(zae);
                        zipOutputStream.putArchiveEntry(zae);
                        zipOutputStream
                                .write(FileUtils.readFileToByteArray(rispostaWs.getRifFileBinario().getFileSuDisco()));
                        closeEntry = true;
                    }
                }
                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }

        }
    }

    private void aggiungiIndiciAipUdVol(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<VolVolumeConserv> lstVrsFileIndiceVol = null;
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumiUnitaDoc(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndiceVol = (List<VolVolumeConserv>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndiceVol != null && !lstVrsFileIndiceVol.isEmpty()) {
                boolean closeEntry = false;

                for (VolVolumeConserv fileIndicePrecVersVol : lstVrsFileIndiceVol) {
                    // Calcolo urn di tipo NORMALIZZATO dell'indice del volume di conservazione
                    CSVersatore csVersatoreVol = new CSVersatore();
                    csVersatoreVol.setAmbiente(
                            fileIndicePrecVersVol.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
                    csVersatoreVol.setEnte(fileIndicePrecVersVol.getOrgStrut().getOrgEnte().getNmEnte());
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
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                     */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    //
                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        FileBinario zipDaIncludere = new FileBinario();
                        File tmpFile = Files.createTempFile("output_", ".zip").toFile();
                        zipDaIncludere.setFileSuDisco(tmpFile);

                        try (FileOutputStream fos = new FileOutputStream(zipDaIncludere.getFileSuDisco());
                                ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(fos)) {

                            this.aggiungiProveConsUd(zaos, fileIndicePrecVersVol);

                        } catch (Exception ex) {
                            rispostaWs.setSeverity(SeverityEnum.ERROR);
                            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                    "Errore nella generazione dello zip da includere  " + ex.getMessage());
                            log.error("Errore nella generazione dello zip da includere ", ex);
                        }

                        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                            rispostaWs.setRifFileBinario(zipDaIncludere);
                        }
                    }

                    if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                        ZipArchiveEntry zae = new ZipArchiveEntry(pathPIndexSource);
                        this.filterZipEntry(zae);
                        zipOutputStream.putArchiveEntry(zae);
                        zipOutputStream
                                .write(FileUtils.readFileToByteArray(rispostaWs.getRifFileBinario().getFileSuDisco()));
                        closeEntry = true;
                    }
                }
                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }

        }
    }
    // end EVO#20972:MEV#20971

    // EVO#20972
    private void aggiungiFileXmlSchema(ZipArchiveOutputStream zipOutputStream, RispostaWSRecupero rispostaWs)
            throws IOException {
        boolean closeEntry = false;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {

            File xsdMoreInfoSelfDesc = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_SELF_DESCRIPTION_XSD_V2);
            if (xsdMoreInfoSelfDesc != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2,
                                xsdMoreInfoSelfDesc.getName(), it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream
                        .write(FileUtils.readFileToString(xsdMoreInfoSelfDesc, StandardCharsets.UTF_8).getBytes());
                closeEntry = true;
            }

            File xsdMoreInfoPVolume = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_PVOLUME_XSD_V2);
            if (xsdMoreInfoPVolume != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2,
                                xsdMoreInfoPVolume.getName(), it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream
                        .write(FileUtils.readFileToString(xsdMoreInfoPVolume, StandardCharsets.UTF_8).getBytes());
                closeEntry = true;
            }

            File xsdMoreInfoDoc = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_DOC_XSD);
            if (xsdMoreInfoDoc != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2, xsdMoreInfoDoc.getName(),
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdMoreInfoDoc, StandardCharsets.UTF_8).getBytes());
                closeEntry = true;
            }

            File xsdMoreInfoFile = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_FILE_XSD);
            if (xsdMoreInfoFile != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2, xsdMoreInfoFile.getName(),
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdMoreInfoFile, StandardCharsets.UTF_8).getBytes());
                closeEntry = true;
            }

            // MEV#25921
            File xsdPIndexFile = FileXSDUtil.getFileXSD(FileXSD.UNISINCRO_2_XSD_V2);
            if (xsdPIndexFile != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2, xsdPIndexFile.getName(),
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdPIndexFile, StandardCharsets.UTF_8).getBytes());
                closeEntry = true;
            }
            // end MEV#25921
        }

        if (closeEntry) {
            zipOutputStream.closeArchiveEntry();
        }
    }
    // end EVO#20972

    private void aggiungiElencoIndiciAipUd(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc,
            String tiFileElencoVers1, String tiFileElencoVers2, RispostaWSRecupero rispostaWs) throws IOException {
        ElvFileElencoVer fileElencoVers = null;
        String prefisso = null;
        String estensione = null;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiElvFileElencoVers(idUnitaDoc, tiFileElencoVers1,
                    tiFileElencoVers2);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                fileElencoVers = rispostaControlli.getrObject() != null
                        ? (ElvFileElencoVer) rispostaControlli.getrObject() : null;
                prefisso = tiFileElencoVers1.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())
                        ? "ElencoIndiceAIP" : "MarcaElencoIndiceAIP";

                // MEV#15967 - Attivazione della firma Xades e XadesT
                if (fileElencoVers != null && fileElencoVers.getTiFirma() != null
                        && fileElencoVers.getTiFirma().equals(ElencoEnums.TipoFirma.XADES.name())) {
                    estensione = ".xml";
                } else {
                    estensione = tiFileElencoVers1.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())
                            ? ".xml.p7m" : ".tsr";
                }

            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (fileElencoVers != null) {
                controlliRecupero.loadElvElencoVer(fileElencoVers);
                String nmAmbiente = fileElencoVers.getElvElencoVer().getOrgStrut().getOrgEnte().getOrgAmbiente()
                        .getNmAmbiente();
                String nmEnte = fileElencoVers.getElvElencoVer().getOrgStrut().getOrgEnte().getNmEnte();
                String nmStrut = fileElencoVers.getElvElencoVer().getOrgStrut().getNmStrut();
                String fileName = prefisso + "-UD:" + nmAmbiente + ":" + nmEnte + ":" + nmStrut + ":"
                        + fileElencoVers.getElvElencoVer().getIdElencoVers();
                ZipArchiveEntry zae = new ZipArchiveEntry(fileName + estensione);
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);

                // MEV#30397
                // recupero documento blob vs obj storage
                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.ELENCO_INDICI_AIP,
                        fileElencoVers.getIdFileElencoVers(), zipOutputStream,
                        RecBlbOracle.TabellaBlob.ELV_FILE_ELENCO);
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                rispostaControlli.setrBoolean(esitoRecupero);
                if (!esitoRecupero) {
                    throw new IOException("Errore non gestito nel recupero del file");
                }
                // end MEV#30397
                zipOutputStream.closeArchiveEntry();
            }
        }
    }

    private void aggiungiIndiciSerie(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
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
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {

                udAppartVerSerieList = (List<AroUdAppartVerSerie>) rispostaControlli.getrObject();

                // Se ho almeno una appartenenza
                if (udAppartVerSerieList != null && !udAppartVerSerieList.isEmpty()) {

                    for (AroUdAppartVerSerie udAppartVerSerie : udAppartVerSerieList) {
                        boolean closeEntry = false;
                        // MAC #29539 FIX Hibernate: per evitare l'errore EJBTransactionRolledbackException: - could not
                        // initialize proxy [it.eng.parer.entity.<ENTITY>] - no Session
                        // recupero nuovamente l'entity AroUdAppartVerSerie per recuperare i dati da AroUnitaDoc,
                        // SerVolVerSerie, SerContenutoVerSerie
                        AroUdAppartVerSerie udAppartInSessione = udHelper.findById(AroUdAppartVerSerie.class,
                                udAppartVerSerie.getIdUdAppartVerSerie());

                        // Creo una cartella con l'urn del codice serie...
                        String codiceSerie = udAppartInSessione.getSerContenutoVerSerie().getSerVerSerie().getSerSerie()
                                .getCdCompositoSerie();
                        // EVO#20972
                        String urnZipArchive = (!recupero.getParametriRecupero().getTipoEntitaSacer()
                                .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2))
                                        ? DIRECTORY_AGGR + "/" + DIRECTORY_SERIE + "/" + codiceSerie + "/"
                                        : DIRECTORY_AGGR_AIPV2 + "/" + DIRECTORY_SERIE_AIPV2 + "/" + codiceSerie + "/";
                        // end EVO#20972

                        String ambiente = udAppartInSessione.getAroUnitaDoc().getOrgStrut().getOrgEnte()
                                .getOrgAmbiente().getNmAmbiente();
                        String ente = udAppartInSessione.getAroUnitaDoc().getOrgStrut().getOrgEnte().getNmEnte();
                        String struttura = udAppartInSessione.getAroUnitaDoc().getOrgStrut().getNmStrut();

                        // ... all'interno della quale piazzo il file con l'xml dell'indice del volume
                        // della serie...
                        if (udAppartInSessione.getSerVolVerSerie() != null) {
                            String urnVol = "IndiceVolumeSerie-" + ambiente + "_" + ente + "_" + struttura + "_"
                                    + codiceSerie + ".xml";
                            ZipArchiveEntry zaeVol = new ZipArchiveEntry(urnZipArchive + urnVol);
                            this.filterZipEntry(zaeVol);
                            zipOutputStream.putArchiveEntry(zaeVol);
                            zipOutputStream.write(udAppartInSessione.getSerVolVerSerie().getSerIxVolVerSeries().get(0)
                                    .getBlIxVol().getBytes(StandardCharsets.UTF_8));
                            closeEntry = true;
                        }

                        // ... e l'indice aip della serie
                        if (udAppartInSessione.getSerContenutoVerSerie() != null) {
                            byte[] ix = serieEjb.getSerFileVerSerieBlob(
                                    udAppartInSessione.getSerContenutoVerSerie().getSerVerSerie().getIdVerSerie(),
                                    CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO);
                            if (ix != null) {
                                prefisso = "IndiceAIPSerie-"
                                        + udAppartInSessione.getSerContenutoVerSerie().getSerVerSerie().getCdVerSerie()
                                        + "_" + ambiente + "_" + ente + "_" + struttura + "_" + codiceSerie;
                                ZipArchiveEntry zaeAip = new ZipArchiveEntry(urnZipArchive + prefisso + ".xml.p7m");
                                this.filterZipEntry(zaeAip);
                                zipOutputStream.putArchiveEntry(zaeAip);
                                // MAC 32341
                                zipOutputStream.write(ix);
                                closeEntry = true;
                            }
                        }

                        // Se per la versione serie è presente la marca
                        byte[] marcaBlobbo = serieEjb.getSerFileVerSerieBlob(
                                udAppartInSessione.getSerContenutoVerSerie().getSerVerSerie().getIdVerSerie(),
                                CostantiDB.TipoFileVerSerie.MARCA_IX_AIP_UNISINCRO);
                        if (marcaBlobbo != null) {
                            ZipArchiveEntry zaeMarca = new ZipArchiveEntry(urnZipArchive + prefisso + ".tsr");
                            this.filterZipEntry(zaeMarca);
                            zipOutputStream.putArchiveEntry(zaeMarca);
                            zipOutputStream.write(marcaBlobbo);
                            closeEntry = true;
                        }
                        if (closeEntry) {
                            zipOutputStream.closeArchiveEntry();
                        }
                    }
                }
            }
        }
    }

    private void aggiungiIndiceFascicoli(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
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
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {

                unitaDocFascicoloList = (List<FasUnitaDocFascicolo>) rispostaControlli.getrObject();

                // Se ho almeno una appartenenza
                if (unitaDocFascicoloList != null && !unitaDocFascicoloList.isEmpty()) {
                    for (FasUnitaDocFascicolo unitaDocFascicolo : unitaDocFascicoloList) {
                        boolean closeEntry = false;

                        // Creo una cartella con l'urn del fascicolo...
                        String annoNumeroFascicolo = unitaDocFascicolo.getFasFascicolo().getAaFascicolo() + "-"
                                + unitaDocFascicolo.getFasFascicolo().getCdKeyFascicolo();
                        annoNumeroFascicolo = MessaggiWSFormat.bonificaUrnPerNomeFile(annoNumeroFascicolo);
                        // EVO#20972
                        String urnZipArchive = (!recupero.getParametriRecupero().getTipoEntitaSacer()
                                .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2))
                                        ? DIRECTORY_AGGR + "/" + DIRECTORY_FASC + "/" + annoNumeroFascicolo + "/"
                                        : DIRECTORY_AGGR_AIPV2 + "/" + DIRECTORY_FASC_AIPV2 + "/" + annoNumeroFascicolo
                                                + "/";
                        // end EVO#20972
                        OrgStrut strut = ambientiHelper
                                .findOrgStrutById(unitaDocFascicolo.getFasFascicolo().getOrgStrut().getIdStrut());
                        String ambiente = strut.getOrgEnte().getOrgAmbiente().getNmAmbiente();
                        String ente = strut.getOrgEnte().getNmEnte();
                        String struttura = strut.getNmStrut();
                        BigDecimal anno = unitaDocFascicolo.getFasFascicolo().getAaFascicolo();
                        String numero = MessaggiWSFormat
                                .bonificaUrnPerNomeFile(unitaDocFascicolo.getFasFascicolo().getCdKeyFascicolo());

                        // Indice aip del fascicolo
                        FasFileMetaVerAipFasc meta = fasHelper.getFasFileMetaVerAipFasc(
                                unitaDocFascicolo.getFasFascicolo().getIdFascicolo(),
                                FasMetaVerAipFascicolo.TiMeta.INDICE.name());

                        if (meta != null) {
                            BigDecimal versione = meta.getFasMetaVerAipFascicolo().getFasVerAipFascicolo()
                                    .getPgVerAipFascicolo();
                            String urnMeta = "IndiceAIPFascicolo-" + versione + ":" + ambiente + ":" + ente + ":"
                                    + struttura + ":" + anno + ":" + numero;
                            ZipArchiveEntry zaeMeta = new ZipArchiveEntry(urnZipArchive + urnMeta + ".xml");
                            this.filterZipEntry(zaeMeta);
                            zipOutputStream.putArchiveEntry(zaeMeta);

                            // MEV 30398
                            String blFile = meta.getBlFileVerIndiceAip();
                            if (blFile == null) {
                                Map<String, String> xmls = objectStorageService.getObjectXmlIndiceAipFasc(meta
                                        .getFasMetaVerAipFascicolo().getFasVerAipFascicolo().getIdVerAipFascicolo());
                                // recupero oggetti da O.S. (se presenti)
                                if (!xmls.isEmpty()) {
                                    blFile = xmls.get("INDICE");
                                }
                            }
                            // end MEV 30398

                            zipOutputStream.write(blFile.getBytes());
                            closeEntry = true;
                        }

                        // Controllo se il fascicolo è inserito in un elenco di versamento fascicoli
                        if (unitaDocFascicolo.getFasFascicolo().getElvElencoVersFasc() != null) {
                            List<ElvFileElencoVersFasc> fileFirma = elencoVersFascHelper.retrieveFileIndiceElenco(
                                    unitaDocFascicolo.getFasFascicolo().getElvElencoVersFasc().getIdElencoVersFasc(),
                                    new String[] {
                                            it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.FileTypeEnum.FIRMA_ELENCO_INDICI_AIP
                                                    .name() });

                            if (!fileFirma.isEmpty()) {
                                long idElencoVersFasc = unitaDocFascicolo.getFasFascicolo().getElvElencoVersFasc()
                                        .getIdElencoVersFasc();

                                prefisso = "ElencoIndiciAIP-Fasc:" + ambiente + ":" + ente + ":" + struttura + ":"
                                        + idElencoVersFasc;
                                ZipArchiveEntry zaeElenco = new ZipArchiveEntry(urnZipArchive + prefisso + ".xml.p7m");
                                this.filterZipEntry(zaeElenco);
                                zipOutputStream.putArchiveEntry(zaeElenco);

                                // MEV #30399
                                byte[] blFileElencoVers = fileFirma.get(0).getBlFileElencoVers();
                                long idFileElencoVersFasc = fileFirma.get(0).getIdFileElencoVersFasc();
                                if (blFileElencoVers == null) {
                                    blFileElencoVers = objectStorageService
                                            .getObjectElencoIndiciAipFasc(idFileElencoVersFasc);
                                }
                                // end MEV #30399

                                zipOutputStream.write(blFileElencoVers);

                                // zipOutputStream.write(fileFirma.get(0).getBlFileElencoVers());
                                closeEntry = true;
                            }
                        }

                        if (closeEntry) {
                            zipOutputStream.closeArchiveEntry();
                        }
                    }
                }
            }
        }
    }

    private void aggiungiProveConsUd(ZipArchiveOutputStream zipOutputStream, VolVolumeConserv volume)
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
                    zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
                    zipOutputStream.write(tmpObject.blobbo);
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }

        if (blobbiByteCertifList != null) {
            for (BlobObject tmpObject : blobbiByteCertifList) {
                if (tmpObject != null) {
                    fileName = "Certificati-Trusted/Certif_Ca_" + tmpObject.id + ".cer";
                    zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
                    zipOutputStream.write(tmpObject.blobbo);
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }

        byte[] indiceConservXml;

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.INDICE);
        if (indiceConservXml != null) {
            fileName = "indice_conservazione.xml";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.MARCA_INDICE);
        if (indiceConservXml != null) {
            fileName = "indice_conservazione.tsr";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.FIRMA);
        if (indiceConservXml != null) {
            fileName = "firma_indice_conservazione.tsr.p7m";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.MARCA_FIRMA);
        if (indiceConservXml != null) {
            fileName = "firma_indice_conservazione.tsr";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write(indiceConservXml);
            zipOutputStream.closeArchiveEntry();
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
        SOLO_FILE_RECUPERATI, // recupera solo gli XML relativi ai file scaricati (tutti per UD, solo DOC per
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

    private void aggiungiXMLVersamentoUd(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            TipiLetturaXml tlx, TipiXmlDaIncludere xmlDaIncludere, RispostaWSRecupero rispostaWs) throws IOException {
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
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneversComp(recupero.getParametriRecupero().getIdComponente());
                } else if (recupero.getParametriRecupero().getIdDocumento() != null) {
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneversDoc(recupero.getParametriRecupero().getIdDocumento());
                } else {
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneversUd(recupero.getParametriRecupero().getIdUnitaDoc());
                }
            } else if (tlx == TipiLetturaXml.RECUPERO_PER_ZIP_AIP) {
                // MAC#30890
                rispostaControlli = controlliRecupero
                        .leggiXmlSessioniVersamentiAip(recupero.getParametriRecupero().getIdUnitaDoc());
                // end MAC#30890
            } else {
                if (recupero.getParametriRecupero().getIdDocumento() != null) {
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneVersDocAggiunto(recupero.getParametriRecupero().getIdDocumento());
                } else {
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneVersUdPrincipale(recupero.getParametriRecupero().getIdUnitaDoc());
                }
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
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
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
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
                    boolean closeEntry = false;
                    for (VrsXmlDatiSessioneVers tmpXml : lstVrsXml) {
                        // urn
                        // Recupero lo urn ORIGINALE dalla tabella VRS_URN_XML_SESSIONE_VERS
                        VrsUrnXmlSessioneVers urnXmlSessioneVers = controlliRecupero
                                .findVrsUrnXmlSessioneVersByTiUrn(tmpXml, TiUrnXmlSessioneVers.ORIGINALE);
                        String urnXmlVers = (urnXmlSessioneVers != null) ? urnXmlSessioneVers.getDsUrn()
                                : tmpXml.getDsUrnXmlVers();
                        /* Definisco la folder relativa al sistema di conservazione */
                        Constants.TipoSessione tipoSessione = controlliRecupero.getTipoSessioneFrom(tmpXml);
                        String tmpPath = (tipoSessione.equals(Constants.TipoSessione.VERSAMENTO))
                                ? urnXmlVers.replaceAll(it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnXmlVers),
                                        "SIP-UD")
                                : urnXmlVers.replaceAll(it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnXmlVers),
                                        "SIP-AGGIUNTA_DOC");
                        String path = (tipoSessione.equals(Constants.TipoSessione.VERSAMENTO))
                                ? it.eng.parer.async.utils.IOUtils.extractPartUrnName(tmpPath, true) : MessaggiWSFormat
                                        .normalizingKey(tmpPath.substring(tmpPath.lastIndexOf(":DOC")).substring(1));
                        String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_SIP_AIPV2, path,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                        /* Definisco il nome e l'estensione del file */
                        fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                                it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnXmlVers, true),
                                it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                        /*
                         * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                         */
                        String pathSip = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                        if (pathSip != null) {
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(pathSip);
                            this.filterZipEntry(zipArchiveEntry);
                            zipOutputStream.putArchiveEntry(zipArchiveEntry);
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero.findIdSessVersByXmlDatiSessVers(tmpXml);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                            } else {
                                long idSessioneVers = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoOs.containsKey(idSessioneVers) && tmpXml.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoOs.get(idSessioneVers);
                                    String blXml = allXml.get(tmpXml.getTiXmlDati());
                                    if (blXml != null) {
                                        zipOutputStream.write(blXml.getBytes(StandardCharsets.UTF_8));
                                    }
                                } else {
                                    zipOutputStream.write(tmpXml.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }

                            closeEntry = true;
                        }
                    }

                    if (closeEntry) {
                        zipOutputStream.closeArchiveEntry();
                    }
                    break;
                default:
                    for (VrsXmlDatiSessioneVers tmpXml : lstVrsXml) {
                        fileName = null;
                        switch (tmpXml.getTiXmlDati()) {
                        case CostantiDB.TipiXmlDati.RICHIESTA:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                if (tmpXml.getDsUrnXmlVers() != null && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                    fileName = tmpXml.getDsUrnXmlVers();
                                } else {
                                    fileName = String.format("IndiceSIP%05d", contaRequest);
                                    contaRequest++;
                                }
                            }
                            break;
                        case CostantiDB.TipiXmlDati.RISPOSTA:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                if (tmpXml.getDsUrnXmlVers() != null && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                    fileName = tmpXml.getDsUrnXmlVers();
                                } else {
                                    fileName = String.format("EsitoVersamento%05d", contaResponse);
                                    contaResponse++;
                                }
                            }
                            break;
                        case CostantiDB.TipiXmlDati.INDICE_FILE:
                            if (xmlDaIncludere == TipiXmlDaIncludere.TUTTI) {
                                if (tmpXml.getDsUrnXmlVers() != null && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                    fileName = tmpXml.getDsUrnXmlVers();
                                } else {
                                    fileName = String.format("IndicePI_SIP%05d", contaIndiceMM);
                                    contaIndiceMM++;
                                }
                            }
                            break;
                        case CostantiDB.TipiXmlDati.RAPP_VERS:
                            if (tmpXml.getDsUrnXmlVers() != null && !tmpXml.getDsUrnXmlVers().isEmpty()) {
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
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(
                                    ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml");
                            this.filterZipEntry(zipArchiveEntry);
                            zipOutputStream.putArchiveEntry(zipArchiveEntry);
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero.findIdSessVersByXmlDatiSessVers(tmpXml);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                            } else {
                                long idSessioneVers = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoOs.containsKey(idSessioneVers) && tmpXml.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoOs.get(idSessioneVers);
                                    String blXml = allXml.get(tmpXml.getTiXmlDati());
                                    if (blXml != null) {
                                        zipOutputStream.write(blXml.getBytes(StandardCharsets.UTF_8));
                                    }
                                } else {
                                    zipOutputStream.write(tmpXml.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }

                            zipOutputStream.closeArchiveEntry();
                        }
                    }
                    break;
                }
                // end EVO#20972
            }
        }
    }

    /**
     * Esegui <strong> una volta sola</strong> il caricamento di tutti gli xml legati alle sessioni di versamento
     * identificate dalla lista di VRS_XML_DATI_SESSIONE_VERS.
     *
     * La Map risultante conterrà tutti gli xml identificati dal loro id di sessione.
     *
     * Nell'economina computazionale totale della funziona risulterà necessario scorrere due volte la lista di
     * VRS_XML_DATI_SESSIONE_VERS ma, fortunatamente, sono tutti dati già presenti in memoria.
     *
     * @param vrsXmlDatiSessioneVers
     *            Lista di VRS_XML_DATI_SESSIONE_VERS ottenuti dalla query precedente
     *
     * @return file xml (come stringa) identificati dall'id della sessione
     */
    private RispostaControlli aggiungiXmlDaObjectStorageOnMap(List<VrsXmlDatiSessioneVers> vrsXmlDatiSessioneVers) {
        // filter
        RispostaControlli rispostaControlli = controlliRecupero
                .findAllSessVersByXmlDatiSessVers(vrsXmlDatiSessioneVers);
        if (rispostaControlli.isrBoolean()) {
            Map<Long, Map<String, String>> xmlMapByVrsSessioneVers = new HashMap<>();

            List<VrsSessioneVers> vrsSessioneVerss = (List<VrsSessioneVers>) rispostaControlli.getrObject();
            // filter on Constants.TipoSessione.VERSAMENTO
            vrsSessioneVerss.stream()
                    .filter(vrs -> vrs.getTiSessioneVers().equals(Constants.TipoSessione.VERSAMENTO.name())
                            && vrs.getAroUnitaDoc() != null)
                    .forEach(vrs -> // put on map
                    xmlMapByVrsSessioneVers.put(vrs.getIdSessioneVers(),
                            objectStorageService.getObjectSipUnitaDoc(vrs.getAroUnitaDoc().getIdUnitaDoc())));

            // filter on Constants.TipoSessione.AGGIUNGI_DOCUMENTO
            vrsSessioneVerss.stream()
                    .filter(vrs -> vrs.getTiSessioneVers().equals(Constants.TipoSessione.AGGIUNGI_DOCUMENTO.name())
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
    private RispostaControlli aggiungiXmlUpdUdDaObjectStorageOnMap(List<AroXmlUpdUnitaDoc> aroXmlUpdUnitaDocs) {
        // filter
        RispostaControlli rispostaControlli = controlliRecupero.findAllUpdUnitaDocByXmlUpdUnitaDoc(aroXmlUpdUnitaDocs);
        if (rispostaControlli.isrBoolean()) {
            Map<Long, Map<String, String>> xmlMapByAroUpdUnitaDoc = new HashMap<>();

            List<AroUpdUnitaDoc> aroUpdUnitaDocs = (List<AroUpdUnitaDoc>) rispostaControlli.getrObject();

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
    private void aggiungiXMLVersamentoUpd(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) throws IOException {
        List<AroXmlUpdUnitaDoc> lstVrsXmlUpd = null;
        Map<Long, Map<String, String>> xmlVersamentoUpdOs = new HashMap<>();
        String fileName;
        RispostaControlli rispostaControlli = new RispostaControlli();
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiXmlVersamentiAipUpdDaUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
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
                    rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                } else {
                    xmlVersamentoUpdOs = (Map<Long, Map<String, String>>) rispostaControlli.getrObject();
                }
            }
        }
        //

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsXmlUpd != null && !lstVrsXmlUpd.isEmpty()) {
                switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
                case UNI_DOC_UNISYNCRO_V2:
                    boolean closeEntry = false;
                    for (AroXmlUpdUnitaDoc tmpXmlUpd : lstVrsXmlUpd) {
                        // MAC#25915
                        boolean replaceWithAggMd = MessaggiWSFormat.isUrnMatchesSafely(tmpXmlUpd.getDsUrnXml(),
                                Costanti.UrnFormatter.SPATH_AGG_MD_REGEXP);
                        String strReplacement = (replaceWithAggMd) ? "AGG_MD" : "AGGIORNAMENTO_UPD";
                        String matchReplacement = (replaceWithAggMd) ? ":AGG_MD" : ":UPD";
                        int beginIndexReplacement = (replaceWithAggMd) ? 6 : 3;
                        // end MAC#25915
                        /* Definisco la folder relativa al sistema di conservazione */
                        // urn:<...>:UPD00001:SIP-AGGIORNAMENTO_UPD || urn:<...>:AGG_MD00001:SIP-AGG_MD
                        String tmpPath = tmpXmlUpd.getDsUrnXml().replaceAll(
                                it.eng.parer.async.utils.IOUtils.extractPartUrnName(tmpXmlUpd.getDsUrnXml()),
                                "SIP-" + strReplacement);
                        String[] tmpPathSplit = tmpPath.substring(tmpPath.lastIndexOf(matchReplacement)).substring(1)
                                .split(":");
                        // SIP-AGGIORNAMENTO_UPD00001 || SIP-AGG_MD00001
                        String path = tmpPathSplit[1] + tmpPathSplit[0].substring(beginIndexReplacement);
                        String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_SIP_AIPV2, path,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                        /* Definisco il nome e l'estensione del file */
                        fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                                it.eng.parer.async.utils.IOUtils.extractPartUrnName(tmpXmlUpd.getDsUrnXml(), true),
                                it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                        /*
                         * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                         */
                        String pathSip = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                        if (pathSip != null) {
                            ZipArchiveEntry zipArchiveEntry = new ZipArchiveEntry(pathSip);
                            this.filterZipEntry(zipArchiveEntry);
                            zipOutputStream.putArchiveEntry(zipArchiveEntry);

                            // MEV#29089
                            // load XML from O.S. vs DB
                            rispostaControlli = controlliRecupero.findIdUpdUnitaDocByXmlUpdUnitaDoc(tmpXmlUpd);
                            if (!rispostaControlli.isrBoolean()) {
                                setRispostaWsError(rispostaWs, rispostaControlli);
                                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                            } else {
                                long idUpdUnitaDoc = rispostaControlli.getrLong();
                                // verify if xml on O.S.
                                if (xmlVersamentoUpdOs.containsKey(idUpdUnitaDoc) && tmpXmlUpd.getBlXml() == null) {
                                    Map<String, String> allXml = xmlVersamentoUpdOs.get(idUpdUnitaDoc);
                                    String blXml = allXml.get(tmpXmlUpd.getTiXmlUpdUnitaDoc().name());
                                    zipOutputStream.write(blXml.getBytes(StandardCharsets.UTF_8));
                                } else {
                                    zipOutputStream.write(tmpXmlUpd.getBlXml().getBytes(StandardCharsets.UTF_8));
                                }
                            }
                            // end MEV#29089

                            closeEntry = true;
                        }
                    }

                    if (closeEntry) {
                        zipOutputStream.closeArchiveEntry();
                    }
                    break;
                default:
                    break;
                }
            }
        }
    }
    // end EVO#20972

    private void aggiungiComponentiDIP(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            RispostaControlli rispostaControlli = controlliRecDip.contaComponenti(recupero.getParametriRecupero());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else if (rispostaControlli.getrLong() > 0) {
                if (rispostaWs.getDatiRecuperoDip() == null) {
                    rispostaWs.setDatiRecuperoDip(new DatiRecuperoDip());
                }
                rispostaWs.getDatiRecuperoDip().setNumeroElementiTrovati(rispostaControlli.getrLong());
                //
                gestSessRecDip.caricaParametri(recupero);
                //
                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                    gestSessRecDip.caricaListaComponenti(recupero);
                }

                if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                    try {
                        gestSessRecDip.recuperaConvInZip(zipOutputStream, recupero,
                                rispostaWs.getDatiRecuperoDip().getElementiTrovati(), DIRECTORY_OUT_DIP);
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

    private void aggiungiDipEsibizione(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            RispostaWSRecupero rispostaWs) {
        RecuperoTxtGen recuperoTxtGen = new RecuperoTxtGen(rispostaWs);

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                recuperoTxtGen.generaDipEsibizione(zipOutputStream, recupero);
            } catch (Exception e) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella fase di aggiunta DIP per esibizione del EJB " + e.getMessage());
                log.error("Errore nella fase di aggiunta DIP per esibizione del EJB ", e);
            }
        }
    }

    public void generaZipReportFirma(String outputPath, RecuperoExt recupero, RispostaWSRecupero rispostaWs) {
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
            rispostaControlli = controlliReportvf
                    .checkReportvfExistenceAndGetZipName(recupero.getParametriRecupero().getIdComponente());
            //
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError(rispostaWs, rispostaControlli);
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                String fileReportvfZipName = rispostaControlli.getrString();
                try {
                    // create final zip
                    zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
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
                    if (rispostaWs.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
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

    private void setRispostaWsError(RispostaWSRecupero rispostaWs, RispostaControlli rispostaControlli) {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }
}
