/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.ValidationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.DecReportServizioVerificaCompDoc;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasUnitaDocFascicolo;
import it.eng.parer.entity.VolFileVolumeConserv;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.VrsUrnXmlSessioneVers;
import it.eng.parer.entity.VrsXmlDatiSessioneVers;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers;
import it.eng.parer.entity.constraint.FasMetaVerAipFascicolo;
import it.eng.parer.exception.SacerException;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.viewEntity.AroVLisaipudSistemaMigraz;
import it.eng.parer.volume.utils.VolumeEnums.FileTypeEnum;
import it.eng.parer.web.helper.ComponentiHelper;
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
import it.eng.parer.ws.recuperoDip.dto.DatiRecuperoDip;
import it.eng.parer.ws.recuperoDip.ejb.ControlliRecDip;
import it.eng.parer.ws.recuperoDip.utils.GestSessRecDip;
import it.eng.parer.ws.recuperoTpi.ejb.RecuperoCompFS;
import it.eng.parer.ws.recuperoreportvf.ejb.ControlliReportvf;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.TokenFileNameType;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;
import it.eng.parerxml.xsd.FileXSD;
import it.eng.parerxml.xsd.FileXSDUtil;
import it.eng.spagoCore.error.EMFError;
import java.time.ZoneId;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Fioravanti_F
 */
public class RecuperoZipGen {

    private static final Logger log = LoggerFactory.getLogger(RecuperoZipGen.class);
    private RispostaWSRecupero rispostaWs;
    private RispostaControlli rispostaControlli;
    private Object object;
    // l'istanza della request decodificata dall'XML di versamento
    Recupero parsedUnitaDoc = null;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliRecupero controlliRecupero = null;
    // stateless ejb per la lettura di blob tramite Oracle JDBC
    RecBlbOracle recBlbOracle = null;
    // stateless ejb per la lettura di file estratti da Tivoli
    RecuperoCompFS recuperoCompFS = null;
    // singleton ejb - cache dei JAXBContext
    XmlContextCache xmlContextCache = null;
    // ejb per recuperare i dati relativi alle prove di conservazione
    ComponentiHelper componentiHelper = null;
    // ejb per gestire il recupero DIP dei componenti
    ControlliRecDip controlliRecDip = null;
    //
    FascicoliHelper fasHelper = null;
    //
    SerieEjb serieEjb = null;
    //
    ElencoVersFascicoliHelper elencoVersFascHelper = null;
    // stateless ejb per il recupero documento
    RecuperoDocumento recuperoDocumento = null;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliReportvf controlliReportvf = null;

    private static final String DIRECTORY_OUT_DIP = "FileTrasformati";
    private static final String DIRECTORY_REC = "FileVersati";
    private static final String DIRECTORY_AGGR = "Aggregazioni";
    private static final String DIRECTORY_SERIE = "Serie";
    private static final String DIRECTORY_FASC = "Fascicoli";
    private static final String DIRECTORY_REPORT = "ReportVerificaFirma";

    // EVO#20972
    private static final String DIRECTORY_REC_AIPV2 = "file";
    private static final String DIRECTORY_SIP_AIPV2 = "sip";
    private static final String DIRECTORY_PIX_AIPV2 = "pindexsource";
    private static final String DIRECTORY_XSD_AIPV2 = "xmlschema";
    private static final String DIRECTORY_AGGR_AIPV2 = "aggregazioni";
    private static final String DIRECTORY_SERIE_AIPV2 = "serie";
    private static final String DIRECTORY_FASC_AIPV2 = "fascicoli";
    // end EVO#20972

    // The ZipArchiveEntry.setXxxTime() methods write the time taking into account the local time zone,
    // so we must first convert the desired timestamp value in the local time zone to have the
    // same timestamps in the ZIP file when the project is built on another computer in a
    // different time zone.
    private static final long DEFAULT_ZIP_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public RecuperoZipGen(RispostaWSRecupero risp) throws NamingException {
        rispostaWs = risp;
        rispostaControlli = new RispostaControlli();

        // recupera l'ejb per la lettura di informazioni, se possibile
        controlliRecupero = (ControlliRecupero) new InitialContext().lookup("java:module/ControlliRecupero");

        // recupera l'ejb per la lettura di blob tramite Oracle JDBC
        recBlbOracle = (RecBlbOracle) new InitialContext().lookup("java:module/RecBlbOracle");

        // recupera l'ejb per la lettura di file estratti da Tivoli
        recuperoCompFS = (RecuperoCompFS) new InitialContext().lookup("java:module/RecuperoCompFS");

        // recupera l'ejb singleton, se possibile
        xmlContextCache = (XmlContextCache) new InitialContext().lookup("java:module/XmlContextCache");

        // recupera l'ejb relativo alle prove di conservazione
        componentiHelper = (ComponentiHelper) new InitialContext().lookup("java:module/ComponentiHelper");

        // recupera l'ejb per gestire il recupero DIP dei componenti
        controlliRecDip = (ControlliRecDip) new InitialContext().lookup("java:module/ControlliRecDip");

        serieEjb = (SerieEjb) new InitialContext().lookup("java:module/SerieEjb");

        fasHelper = (FascicoliHelper) new InitialContext().lookup("java:module/FascicoliHelper");

        elencoVersFascHelper = (ElencoVersFascicoliHelper) new InitialContext()
                .lookup("java:module/ElencoVersFascicoliHelper");

        // recupera l'ejb per la da Object Storage
        recuperoDocumento = (RecuperoDocumento) new InitialContext().lookup("java:module/RecuperoDocumento");

        // recupera l'ejb per la lettura di informazioni, se possibile
        controlliReportvf = (ControlliReportvf) new InitialContext().lookup("java:module/ControlliReportvf");
    }

    public File getZip(String outputPath, RecuperoExt recupero, boolean tentaRecuperoDip) {
        generaZipOggetto(outputPath, recupero, tentaRecuperoDip);

        if (rispostaWs.getRifFileBinario() != null && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            return rispostaWs.getRifFileBinario().getFileSuDisco();
        } else {
            return null;
        }

    }

    public void generaZipOggetto(String outputPath, RecuperoExt recupero, boolean tentaRecuperoDip) {
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
        boolean includiSessFileVersUpd = false;
        boolean includiFileIndiceAIPV2 = false;
        boolean includiFileIndiceAIPExt = false;
        boolean includiFileIndiceAIPVol = false;
        boolean includiFileXsdAIPV2 = false;
        // end EVO#20972
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;

        // legge l'elenco dei componenti di tipo file nell'UD, per estrarre i relativi blob
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
                includiSessFileVersamento = true;
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
                setRispostaWsError();
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
                    this.aggiungiComponentiDIP(tmpZipOutputStream, recupero);
                }

                if (recuperaDipEsibizione && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiDipEsibizione(tmpZipOutputStream, recupero);
                }

                if (includiSessFileVersamento && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero, TipiLetturaXml.SOLO_FILE_RECUPERATI,
                            TipiXmlDaIncludere.TUTTI);
                }

                // EVO#20972
                if (includiSessFileVersUpd && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUpd(tmpZipOutputStream, recupero);
                }

                if (includiFileXsdAIPV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni degli XSD
                    this.aggiungiFileXmlSchema(tmpZipOutputStream);
                }
                // end EVO#20972

                if (includiRapportoVersamento && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoUd(tmpZipOutputStream, recupero, TipiLetturaXml.SOLO_FILE_RECUPERATI,
                            TipiXmlDaIncludere.SOLO_RAPPORTO_VERSAMENTO);
                }

                this.aggiungiFileComponenti(tmpZipOutputStream, recupero, lstComp);

                if (includiFileIndiceAIP && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP
                    this.aggiungiIndiciAipUd(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc());
                }

                // EVO#20972:MEV#20971
                if (includiFileIndiceAIPV2 && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di Sacer
                    this.aggiungiIndiciAipUdV2(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc());
                }

                if (includiFileIndiceAIPExt && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di altri
                    // conservatori
                    this.aggiungiIndiciAipUdExt(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc());
                }

                if (includiFileIndiceAIPVol && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice dei Volumi di conservazione di Sacer
                    this.aggiungiIndiciAipUdVol(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc());
                }
                // end EVO#20972:MEV#20971

                if (includiFirmaMarcaElencoIndiceAIP && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiElencoIndiciAipUd(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.FIRMA_ELENCO_INDICI_AIP
                                    .name(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.ELENCO_INDICI_AIP.name());
                    this.aggiungiElencoIndiciAipUd(tmpZipOutputStream, recupero.getParametriRecupero().getIdUnitaDoc(),
                            it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.MARCA_FIRMA_ELENCO_INDICI_AIP
                                    .name(),
                            null);
                }

                if (includiIndiceVolumeAIPSerie && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiIndiciSerie(tmpZipOutputStream, recupero);
                }

                if (includiIndiceFascicoli && rispostaWs.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiIndiceFascicoli(tmpZipOutputStream, recupero);
                }

                tmpZipOutputStream.flush();

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
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
    public File getZipProveCons(String outputPath, RecuperoExt recupero) {
        switch (recupero.getParametriRecupero().getTipoEntitaSacer()) {
        case PROVE_CONSERV_AIPV2:
            generaZipProveConsAIPV2(outputPath, recupero);
            break;
        default:
            generaZipProveCons(outputPath, recupero);
            break;
        }

        if (rispostaWs.getRifFileBinario() != null && rispostaWs.getRifFileBinario().getFileSuDisco() != null) {
            return rispostaWs.getRifFileBinario().getFileSuDisco();
        } else {
            return null;
        }
    }

    public void generaZipProveConsAIPV2(String outputPath, RecuperoExt recupero) {
        VolVolumeConserv volVolumeConserv = null;
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumeConserv(recupero.getParametriRecupero().getIdVolume());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
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

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
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

    public void generaZipProveCons(String outputPath, RecuperoExt recupero) {
        StatoConservazione myEsito = rispostaWs.getIstanzaEsito();
        AvanzamentoWs myAvanzamentoWs = rispostaWs.getAvanzamento();
        List<VolVolumeConserv> volVolumeConserv = null;
        FileBinario zipDaScaricare = null;
        byte[] tmpXmlByteArrProveC = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
        ZipArchiveEntry tmpEntry = null;

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumiUnitaDoc(recupero.getParametriRecupero().getIdUnitaDoc());
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                volVolumeConserv = (List<VolVolumeConserv>) rispostaControlli.getrObject();
            }

        }

        // costruisce la stringa xml relativa all'indice xml delle prove cons
        // costruisce il byte array ben formattato relativo all'indice xml
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            tmpXmlByteArrProveC = this.preparaXMLIndicePC(myEsito);
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

                if (volVolumeConserv != null && volVolumeConserv.size() > 0) {
                    for (VolVolumeConserv tmpVolConserv : volVolumeConserv) {
                        this.aggiungiProveConsUd(tmpZipOutputStream, tmpVolConserv);
                    }
                }

                tmpZipOutputStream.flush();

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
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

    public void generaZipRapportiVers(String outputPath, RecuperoExt recupero) {
        AvanzamentoWs myAvanzamentoWs = rispostaWs.getAvanzamento();
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;

        parsedUnitaDoc = recupero.getStrutturaRecupero();
        CSChiave tmpCSChiave = new CSChiave();
        tmpCSChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
        tmpCSChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
        tmpCSChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());
        rispostaWs.setNomeFile("RV-" + this.calcolaNomeFileZipRV(tmpCSChiave));

        zipDaScaricare = new FileBinario();
        try {
            List<VrsXmlDatiSessioneVers> lstVrsXml = null;
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiXMLSessioneversUd(recupero.getParametriRecupero().getIdUnitaDoc());
            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsXml = (List<VrsXmlDatiSessioneVers>) rispostaControlli.getrObject();
            }
            if (rispostaWs.getSeverity() == SeverityEnum.OK) {
                int fileTrovati = 0;
                if (lstVrsXml != null && lstVrsXml.size() > 0) {
                    zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                    tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                    tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);
                    String fileName;
                    int contaRappVers = 1;
                    for (VrsXmlDatiSessioneVers tmpXml : lstVrsXml) {
                        fileName = null;
                        switch (tmpXml.getTiXmlDati()) {
                        case CostantiDB.TipiXmlDati.RAPP_VERS:
                            fileTrovati++;
                            if (tmpXml.getDsUrnXmlVers() != null && !tmpXml.getDsUrnXmlVers().isEmpty()) {
                                fileName = tmpXml.getDsUrnXmlVers();
                            } else {
                                fileName = String.format("RapportoVersamento%05d", contaRappVers);
                                contaRappVers++;
                            }
                            break;
                        default:
                            fileName = null;
                        }
                        if (fileName != null) {
                            tmpZipOutputStream.putArchiveEntry(
                                    new ZipArchiveEntry(ComponenteRec.estraiNomeFileCompleto(fileName) + ".xml"));
                            tmpZipOutputStream.write((byte[]) tmpXml.getBlXml().getBytes("UTF-8"));
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
        } catch (IOException ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
            log.error("Errore nella generazione dello zip da scaricare ", ex);
        } catch (Exception ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
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

    public void generaZipSip(String outputPath, RecuperoExt recupero) {
        List<ComponenteRec> lstComp = null;
        String prefisso = null;
        String nomeFileZip = null;
        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;
        ZipArchiveEntry tmpEntry = null;

        // legge l'elenco dei componenti di tipo file nell'UD, per estrarre i relativi
        // blob
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

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
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
                            TipiXmlDaIncludere.TUTTI);
                }

                this.aggiungiFileComponenti(tmpZipOutputStream, recupero, lstComp);

                tmpZipOutputStream.flush();

            } catch (IOException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
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

    private byte[] preparaXMLIndicePC(StatoConservazione myEsito) {
        String tmpStringXml = null;
        byte[] tmpXmlByteArr = null;

        // costruisce la stringa xml relativa all'indice xml delle prove cons.
        StringWriter tmpStringWriter = new StringWriter();
        try {
            Marshaller marshaller = xmlContextCache.getVersRespStatoCtx_IndiceProveCons().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(rispostaWs.getIndiceProveConservazione(), tmpStringWriter);

            tmpStringXml = tmpStringWriter.toString();
        } catch (MarshalException ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella serializzazione dell'XML  " + ex.getMessage());
            log.error("Errore nella serializzazione dell'XML  ", ex);
        } catch (ValidationException ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella serializzazione dell'XML  " + ex.getMessage());
            log.error("Errore nella serializzazione dell'XML  ", ex);
        } catch (Exception ex) {
            rispostaWs.setSeverity(SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore nella serializzazione dell'XML  " + ex.getMessage());
            log.error("Errore nella serializzazione dell'XML  ", ex);
        }

        // costruisce il byte array ben formattato relativo all'indice xml
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            try {
                XmlPrettyPrintFormatter tmpFormatter = new XmlPrettyPrintFormatter();
                tmpStringXml = tmpFormatter.prettyPrintWithDOM3LS(tmpStringXml);
                tmpXmlByteArr = tmpStringXml.getBytes("UTF-8");
            } catch (UnsupportedEncodingException ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella serializzazione dell'XML  " + ex.getMessage());
                log.error("Errore nella serializzazione dell'XML  ", ex);
            } catch (Exception ex) {
                rispostaWs.setSeverity(SeverityEnum.ERROR);
                rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella serializzazione dell'XML  " + ex.getMessage());
                log.error("Errore nella serializzazione dell'XML  ", ex);
            }
        }
        return tmpXmlByteArr;
    }

    private void aggiungiFileComponenti(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            List<ComponenteRec> lstComp) throws IOException {

        HashMap entryGiaInserite = new HashMap();
        TokenFileNameType tipoNomeFile = recupero.getStrutturaRecupero().getChiave().getTipoNomeFile();
        for (ComponenteRec tmpCmp : lstComp) {
            ZipArchiveEntry zae = null;
            // Se viene passato NIENTE oppure NOME_FILE_URN_SACER si vuole il vecchio comportamento
            if (tipoNomeFile == null || tipoNomeFile.value().trim().equals("")
                    || tipoNomeFile.equals(TokenFileNameType.NOME_FILE_URN_SACER)) {
                // EVO#20972
                if (!recupero.getParametriRecupero().getTipoEntitaSacer()
                        .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2)) {
                    // Vecchio comportamento
                    zae = new ZipArchiveEntry(DIRECTORY_REC + "/" + tmpCmp.getNomeFilePerZip());
                } else {
                    // zae = new ZipArchiveEntry(DIRECTORY_REC_AIPV2 + "/" + tmpCmp.getNomeFilePerZipAIPV2());
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
                    if (urnVersato.startsWith("/")) {
                        pathCompleto = urnVersato;
                    } else {
                        pathCompleto = "/" + urnVersato;
                    }
                } else {
                    pathCompleto = "/" + nomeFileOriginaleVersato;
                }
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
                } else {
                    /*
                     * Controllo che il nome del file originario non sia presente nell'urn versato, se esiste si prende
                     * direttamente l'urn versato completo (che contiene già il nome del file)
                     */
                    if (!nomeFileOriginaleVersato.trim().equals("")) {
                        if (urnVersato.endsWith(nomeFileOriginaleVersato)) {
                            // Se l'urn versato inizia per "/" no lo aggiunge al path
                            if (urnVersato.startsWith("/")) {
                                pathCompleto = urnVersato;
                            } else {
                                pathCompleto = "/" + urnVersato;
                            }
                        } else {
                            // Se l'urn versato inizia per "/" no lo aggiunge al path
                            if (urnVersato.startsWith("/")) {
                                pathCompleto = urnVersato + "/" + nomeFileOriginaleVersato;
                            } else {
                                pathCompleto = "/" + urnVersato + "/" + nomeFileOriginaleVersato;
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
                rispostaControlli = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
            }
            zipOutputStream.closeArchiveEntry();
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                break; // esce dal ciclo for, se incontra un errore: ormai lo zip è condannato
            }
        }

    }

    private void provaAdInserireEntry(ZipArchiveOutputStream zipStream, String path, HashMap entryGiaInserite)
            throws IOException {
        final int MAX_ITERAZIONI = 1000;
        int i = 0;
        int contatoreCopie = 0;
        String nuovoNomeFile = path;
        for (i = 0; i < MAX_ITERAZIONI; i++) {
            if (entryGiaInserite.containsKey(nuovoNomeFile)) {
                contatoreCopie++;
                int posPunto = path.indexOf(".");
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
                entryGiaInserite.put(nuovoNomeFile, null);
                break;
            }
        }
        if (i == MAX_ITERAZIONI) {
            throw new IOException(
                    "Raggiunto il numero massimo di iterazioni per gestire uno stesso nome file: [" + path + "]");
        }

    }

    private void aggiungiIndiciAipUd(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc) throws IOException {
        List<AroFileVerIndiceAipUd> lstVrsFileIndice = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIP(idUnitaDoc);

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndice = (List<AroFileVerIndiceAipUd>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndice != null && lstVrsFileIndice.size() > 0) {
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

    // EVO#20972
    private void aggiungiIndiciAipUdV2(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc) throws IOException {
        List<AroFileVerIndiceAipUd> lstVrsFileIndice = null;
        String fileName;

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPV2(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndice = (List<AroFileVerIndiceAipUd>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndice != null && lstVrsFileIndice.size() > 0) {
                boolean closeEntry = false;
                AroFileVerIndiceAipUd fileIndiceLastVers = lstVrsFileIndice.remove(0);
                if (fileIndiceLastVers.getAroVerIndiceAipUd() != null) {
                    if (fileIndiceLastVers.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds() != null
                            && !fileIndiceLastVers.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds().isEmpty()) {
                        // Recupero lo urn ORIGINALE
                        AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils.find(
                                fileIndiceLastVers.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                .equals(TiUrnVerIxAipUd.ORIGINALE);
                                    }
                                });
                        fileName = urnVerIndiceAipUd.getDsUrn();
                    } else {
                        fileName = fileIndiceLastVers.getAroVerIndiceAipUd().getDsUrn();
                    }

                    // MEV#25871
                    // /* Definisco il nome e l'estensione del file */
                    // fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                    // it.eng.parer.async.utils.IOUtils.extractPartUrnName(fileName, true),
                    // it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                    // // end MEV#25871

                    // MEV#27035
                    /* Definisco il nome e l'estensione del file */
                    fileName = "PIndexUD.xml";
                    // end MEV#27035

                    ZipArchiveEntry zae = new ZipArchiveEntry(fileName);
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);
                    zipOutputStream.write(fileIndiceLastVers.getBlFileVerIndiceAip().getBytes("UTF-8"));
                    closeEntry = true;
                }
                // MEV#20971
                for (AroFileVerIndiceAipUd fileIndicePrecVers : lstVrsFileIndice) {
                    if (fileIndicePrecVers.getAroVerIndiceAipUd() != null) {
                        String urnIxAip;
                        if (fileIndicePrecVers.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds() != null
                                && !fileIndicePrecVers.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds().isEmpty()) {
                            // Recupero lo urn ORIGINALE
                            AroUrnVerIndiceAipUd urnVerIndiceAipUd = (AroUrnVerIndiceAipUd) CollectionUtils.find(
                                    fileIndicePrecVers.getAroVerIndiceAipUd().getAroUrnVerIndiceAipUds(),
                                    new Predicate() {
                                        @Override
                                        public boolean evaluate(final Object object) {
                                            return ((AroUrnVerIndiceAipUd) object).getTiUrn()
                                                    .equals(TiUrnVerIxAipUd.ORIGINALE);
                                        }
                                    });
                            urnIxAip = urnVerIndiceAipUd.getDsUrn();
                        } else {
                            urnIxAip = fileIndicePrecVers.getAroVerIndiceAipUd().getDsUrn();
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

                        ZipArchiveEntry zae = new ZipArchiveEntry(pathPIndexSource);
                        this.filterZipEntry(zae);
                        zipOutputStream.putArchiveEntry(zae);
                        zipOutputStream.write(fileIndicePrecVers.getBlFileVerIndiceAip().getBytes("UTF-8"));
                        closeEntry = true;
                    }
                }
                // end MEV#20971
                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }
    }
    // end EVO#20972

    // EVO#20972:MEV#20971
    private void aggiungiIndiciAipUdExt(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc) throws IOException {
        List<AroVLisaipudSistemaMigraz> lstVrsFileIndiceExt = null;
        String fileName;

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiXMLIndiceAIPExternal(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndiceExt = (List<AroVLisaipudSistemaMigraz>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndiceExt != null && lstVrsFileIndiceExt.size() > 0) {
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
                    /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
                    String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                    //
                    rispostaControlli.reset();
                    rispostaControlli = controlliRecupero
                            .leggiCompFileInUD(fileIndicePrecVersExt.getIdUnitaDocAip().longValue());
                    if (!rispostaControlli.isrBoolean()) {
                        setRispostaWsError();
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
                                    rispostaControlli = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);

                                    zaos.closeArchiveEntry();

                                    if (!rispostaControlli.isrBoolean()) {
                                        setRispostaWsError();
                                        rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(),
                                                rispostaControlli.getDsErr());
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

    private void aggiungiIndiciAipUdVol(ZipArchiveOutputStream zipOutputStream, long idUnitaDoc) throws IOException {
        List<VolVolumeConserv> lstVrsFileIndiceVol = null;
        String fileName;

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiVolumiUnitaDoc(idUnitaDoc);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsFileIndiceVol = (List<VolVolumeConserv>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsFileIndiceVol != null && lstVrsFileIndiceVol.size() > 0) {
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
                    /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
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

                        } catch (IOException ex) {
                            rispostaWs.setSeverity(SeverityEnum.ERROR);
                            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                                    "Errore nella generazione dello zip da includere  " + ex.getMessage());
                            log.error("Errore nella generazione dello zip da includere ", ex);
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
    private void aggiungiFileXmlSchema(ZipArchiveOutputStream zipOutputStream) throws IOException {
        boolean closeEntry = false;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {

            File xsdMoreInfoSelfDesc = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_SELF_DESCRIPTION_XSD_V2);
            if (xsdMoreInfoSelfDesc != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2,
                                xsdMoreInfoSelfDesc.getName(), it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdMoreInfoSelfDesc, "UTF-8").getBytes());
                closeEntry = true;
            }

            File xsdMoreInfoPVolume = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_PVOLUME_XSD_V2);
            if (xsdMoreInfoPVolume != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2,
                                xsdMoreInfoPVolume.getName(), it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdMoreInfoPVolume, "UTF-8").getBytes());
                closeEntry = true;
            }

            File xsdMoreInfoDoc = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_DOC_XSD);
            if (xsdMoreInfoDoc != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2, xsdMoreInfoDoc.getName(),
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdMoreInfoDoc, "UTF-8").getBytes());
                closeEntry = true;
            }

            File xsdMoreInfoFile = FileXSDUtil.getFileXSD(FileXSD.AIP_UD_FILE_XSD);
            if (xsdMoreInfoFile != null) {
                ZipArchiveEntry zae = new ZipArchiveEntry(
                        it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2, xsdMoreInfoFile.getName(),
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(FileUtils.readFileToString(xsdMoreInfoFile, "UTF-8").getBytes());
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
                zipOutputStream.write(FileUtils.readFileToString(xsdPIndexFile, "UTF-8").getBytes());
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
            String tiFileElencoVers1, String tiFileElencoVers2) throws IOException {
        ElvFileElencoVer fileElencoVers = null;
        String prefisso = null;
        String estensione = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero.leggiElvFileElencoVers(idUnitaDoc, tiFileElencoVers1,
                    tiFileElencoVers2);

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                fileElencoVers = rispostaControlli.getrObject() != null
                        ? (ElvFileElencoVer) rispostaControlli.getrObject() : null;
                prefisso = tiFileElencoVers1.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())
                        ? "ElencoIndiceAIP" : "MarcaElencoIndiceAIP";
                estensione = tiFileElencoVers1.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())
                        ? ".xml.p7m" : ".tsr";
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (fileElencoVers != null) {
                String nmAmbiente = fileElencoVers.getElvElencoVer().getOrgStrut().getOrgEnte().getOrgAmbiente()
                        .getNmAmbiente();
                String nmEnte = fileElencoVers.getElvElencoVer().getOrgStrut().getOrgEnte().getNmEnte();
                String nmStrut = fileElencoVers.getElvElencoVer().getOrgStrut().getNmStrut();
                String fileName = prefisso + "-UD:" + nmAmbiente + ":" + nmEnte + ":" + nmStrut + ":"
                        + fileElencoVers.getElvElencoVer().getIdElencoVers();
                ZipArchiveEntry zae = new ZipArchiveEntry(fileName + estensione);
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                zipOutputStream.write(fileElencoVers.getBlFileElencoVers());
                zipOutputStream.closeArchiveEntry();
            }
        }
    }

    private void aggiungiIndiciSerie(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero) throws IOException {
        List<AroUdAppartVerSerie> udAppartVerSerieList = null;
        String prefisso = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiAroUdAppartVerSerie(recupero.getParametriRecupero().getIdUnitaDoc());

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {

                udAppartVerSerieList = (List<AroUdAppartVerSerie>) rispostaControlli.getrObject();

                // Se ho almeno una appartenenza
                if (udAppartVerSerieList != null && !udAppartVerSerieList.isEmpty()) {

                    for (AroUdAppartVerSerie udAppartVerSerie : udAppartVerSerieList) {
                        boolean closeEntry = false;

                        // Creo una cartella con l'urn del codice serie...
                        String codiceSerie = udAppartVerSerie.getSerContenutoVerSerie().getSerVerSerie().getSerSerie()
                                .getCdCompositoSerie();
                        // EVO#20972
                        String urnZipArchive = (!recupero.getParametriRecupero().getTipoEntitaSacer()
                                .equals(CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2))
                                        ? DIRECTORY_AGGR + "/" + DIRECTORY_SERIE + "/" + codiceSerie + "/"
                                        : DIRECTORY_AGGR_AIPV2 + "/" + DIRECTORY_SERIE_AIPV2 + "/" + codiceSerie + "/";
                        // end EVO#20972
                        String ambiente = udAppartVerSerie.getAroUnitaDoc().getOrgStrut().getOrgEnte().getOrgAmbiente()
                                .getNmAmbiente();
                        String ente = udAppartVerSerie.getAroUnitaDoc().getOrgStrut().getOrgEnte().getNmEnte();
                        String struttura = udAppartVerSerie.getAroUnitaDoc().getOrgStrut().getNmStrut();

                        // ... all'interno della quale piazzo il file con l'xml dell'indice del volume
                        // della serie...
                        if (udAppartVerSerie.getSerVolVerSerie() != null) {
                            String urnVol = "IndiceVolumeSerie-" + ambiente + "_" + ente + "_" + struttura + "_"
                                    + codiceSerie + ".xml";
                            ZipArchiveEntry zaeVol = new ZipArchiveEntry(urnZipArchive + urnVol);
                            this.filterZipEntry(zaeVol);
                            zipOutputStream.putArchiveEntry(zaeVol);
                            zipOutputStream.write(udAppartVerSerie.getSerVolVerSerie().getSerIxVolVerSeries().get(0)
                                    .getBlIxVol().getBytes());
                            closeEntry = true;
                        }

                        // ... e l'indice aip della serie
                        if (udAppartVerSerie.getSerContenutoVerSerie() != null) {
                            byte[] ix = serieEjb.getSerFileVerSerieBlob(
                                    udAppartVerSerie.getSerContenutoVerSerie().getSerVerSerie().getIdVerSerie(),
                                    CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO_FIRMATO);
                            if (ix != null) {
                                prefisso = "IndiceAIPSerie-"
                                        + udAppartVerSerie.getSerContenutoVerSerie().getSerVerSerie().getCdVerSerie()
                                        + "_" + ambiente + "_" + ente + "_" + struttura;
                                ZipArchiveEntry zaeAip = new ZipArchiveEntry(urnZipArchive + prefisso + ".xml.p7m");
                                this.filterZipEntry(zaeAip);
                                zipOutputStream.putArchiveEntry(zaeAip);
                                zipOutputStream.write(ix);
                                closeEntry = true;
                            }
                        }

                        // Se per la versione serie è presente la marca
                        byte[] marcaBlobbo = serieEjb.getSerFileVerSerieBlob(
                                udAppartVerSerie.getSerContenutoVerSerie().getSerVerSerie().getIdVerSerie(),
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

    private void aggiungiIndiceFascicoli(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero)
            throws IOException {
        List<FasUnitaDocFascicolo> unitaDocFascicoloList = null;
        String prefisso = null;
        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiFasUnitaDocFascicolo(recupero.getParametriRecupero().getIdUnitaDoc());

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
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
                        String ambiente = unitaDocFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte()
                                .getOrgAmbiente().getNmAmbiente();
                        String ente = unitaDocFascicolo.getFasFascicolo().getOrgStrut().getOrgEnte().getNmEnte();
                        String struttura = unitaDocFascicolo.getFasFascicolo().getOrgStrut().getNmStrut();
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
                            zipOutputStream.write(meta.getBlFileVerIndiceAip().getBytes());
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
                                zipOutputStream.write(fileFirma.get(0).getBlFileElencoVers());
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
            throws EMFError, IOException {

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
                    zipOutputStream.write((byte[]) tmpObject.blobbo);
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }

        if (blobbiByteCertifList != null) {
            for (BlobObject tmpObject : blobbiByteCertifList) {
                if (tmpObject != null) {
                    fileName = "Certificati-Trusted/Certif_Ca_" + tmpObject.id + ".cer";
                    zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
                    zipOutputStream.write((byte[]) tmpObject.blobbo);
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }

        byte[] indiceConservXml;

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.INDICE);
        if (indiceConservXml != null) {
            fileName = "indice_conservazione.xml";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write((byte[]) indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.MARCA_INDICE);
        if (indiceConservXml != null) {
            fileName = "indice_conservazione.tsr";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write((byte[]) indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.FIRMA);
        if (indiceConservXml != null) {
            fileName = "firma_indice_conservazione.tsr.p7m";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write((byte[]) indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }

        indiceConservXml = this.recuperaIndiceCons(volume, FileTypeEnum.MARCA_FIRMA);
        if (indiceConservXml != null) {
            fileName = "firma_indice_conservazione.tsr";
            zipOutputStream.putArchiveEntry(new ZipArchiveEntry(prefissoVolume + fileName));
            zipOutputStream.write((byte[]) indiceConservXml);
            zipOutputStream.closeArchiveEntry();
        }
    }

    private byte[] recuperaIndiceCons(VolVolumeConserv volume, FileTypeEnum fileType) {
        byte[] file = null;
        for (VolFileVolumeConserv volFile : volume.getVolFileVolumeConservs()) {
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
        RECUPERO_PER_ZIP_SIP // specifico per recupero ZIP SIP
    }

    private enum TipiXmlDaIncludere {
        TUTTI, SOLO_RAPPORTO_VERSAMENTO
    }

    private void aggiungiXMLVersamentoUd(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero,
            TipiLetturaXml tlx, TipiXmlDaIncludere xmlDaIncludere) throws IOException {
        List<VrsXmlDatiSessioneVers> lstVrsXml = null;
        String fileName;

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
            } else {
                if (recupero.getParametriRecupero().getIdDocumento() != null) {
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneVersDocAggiunto(recupero.getParametriRecupero().getIdDocumento());
                } else {
                    rispostaControlli = controlliRecupero
                            .leggiXMLSessioneVersUdPrincipale(recupero.getParametriRecupero().getIdUnitaDoc());
                }
            }

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsXml = (List<VrsXmlDatiSessioneVers>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsXml != null && lstVrsXml.size() > 0) {
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
                        VrsUrnXmlSessioneVers urnXmlSessioneVers = (VrsUrnXmlSessioneVers) CollectionUtils
                                .find(tmpXml.getVrsUrnXmlSessioneVers(), new Predicate() {
                                    @Override
                                    public boolean evaluate(final Object object) {
                                        return ((VrsUrnXmlSessioneVers) object).getTiUrn()
                                                .equals(TiUrnXmlSessioneVers.ORIGINALE);
                                    }
                                });
                        String urnXmlVers = (urnXmlSessioneVers != null) ? urnXmlSessioneVers.getDsUrn()
                                : tmpXml.getDsUrnXmlVers();
                        /* Definisco la folder relativa al sistema di conservazione */
                        Constants.TipoSessione tipoSessione = Constants.TipoSessione
                                .valueOf(tmpXml.getVrsDatiSessioneVers().getVrsSessioneVers().getTiSessioneVers());
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
                            zipOutputStream.write((byte[]) tmpXml.getBlXml().getBytes("UTF-8"));
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
                            zipOutputStream.write((byte[]) tmpXml.getBlXml().getBytes("UTF-8"));
                            zipOutputStream.closeArchiveEntry();
                        }
                    }
                    break;
                }
                // end EVO#20972
            }
        }
    }

    // EVO#20972
    private void aggiungiXMLVersamentoUpd(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero)
            throws IOException {
        List<AroXmlUpdUnitaDoc> lstVrsXmlUpd = null;
        String fileName;

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecupero
                    .leggiXMLSessioneversUpd(recupero.getParametriRecupero().getIdUnitaDoc());

            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstVrsXmlUpd = (List<AroXmlUpdUnitaDoc>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            if (lstVrsXmlUpd != null && lstVrsXmlUpd.size() > 0) {
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
                            zipOutputStream.write((byte[]) tmpXmlUpd.getBlXml().getBytes("UTF-8"));
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

    private void aggiungiComponentiDIP(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero)
            throws IOException {
        GestSessRecDip gestSessRecDip = new GestSessRecDip(rispostaWs);

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli = controlliRecDip.contaComponenti(recupero.getParametriRecupero());
            if (rispostaControlli.isrBoolean() == false) {
                setRispostaWsError();
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

    private void aggiungiDipEsibizione(ZipArchiveOutputStream zipOutputStream, RecuperoExt recupero) {
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

    public void generaZipReportFirma(String outputPath, RecuperoExt recupero) {
        FileBinario zipDaScaricare = new FileBinario();
        parsedUnitaDoc = recupero.getStrutturaRecupero();
        CSChiave tmpCSChiave = new CSChiave();
        tmpCSChiave.setAnno(parsedUnitaDoc.getChiave().getAnno().longValue());
        tmpCSChiave.setNumero(parsedUnitaDoc.getChiave().getNumero());
        tmpCSChiave.setTipoRegistro(parsedUnitaDoc.getChiave().getTipoRegistro());

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            // check report
            rispostaControlli = controlliReportvf
                    .checkReportvfExistenceAndGetZipName(recupero.getParametriRecupero().getIdComponente());
            //
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
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

    private void setRispostaWsError() {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }

}
