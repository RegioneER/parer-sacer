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

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package it.eng.parer.ws.recuperoFasc.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.FasFileMetaVerAipFasc;
import it.eng.parer.entity.FasXmlFascicolo;
import it.eng.parer.entity.FasXmlVersFascicolo;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.RecuperoWeb;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recupero.dto.RispostaWSRecupero;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recuperoFasc.dto.ContenutoRec;
import it.eng.parer.ws.recuperoFasc.dto.RecuperoFascExt;
import it.eng.parer.ws.recuperoFasc.dto.RispostaWSRecuperoFasc;
import it.eng.parer.ws.recuperoFasc.ejb.ControlliRecuperoFasc;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.parer.ws.xml.versReqStatoFasc.VersatoreFascType;
import it.eng.parerxml.xsd.FileXSD;
import it.eng.parerxml.xsd.FileXSDUtil;
import it.eng.spagoLite.security.User;
import javax.xml.datatype.DatatypeConfigurationException;
import java.util.Map;
import java.util.Map;
import javax.ejb.EJB;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings("unchecked")
public class RecuperoZipFascGen {

    private static final Logger log = LoggerFactory.getLogger(RecuperoZipFascGen.class);
    private RispostaWSRecupero rispostaWs;
    private RispostaWSRecuperoFasc rispostaWsFasc;
    private RispostaControlli rispostaControlli;
    // stateless ejb per la lettura di informazioni relative ai dati da recuperare
    ControlliRecuperoFasc controlliRecuperoFasc = null;
    //
    FascicoliHelper fasHelper = null;
    //
    ObjectStorageService objectStorageService = null;
    @EJB
    private RecuperoDocumento recuperoDocumento;

    private static final String DIRECTORY_REC_FASC_METADATI = "metadati";
    private static final String DIRECTORY_REC_FASC_CONTEN = "contenuto";
    private static final String DIRECTORY_REC_FASC_CONTEN_UD = "unitadocumentarie";

    private static final String DIRECTORY_SIP_AIPV2 = "sip";
    private static final String DIRECTORY_PIX_AIPV2 = "pindexsource";
    private static final String DIRECTORY_XSD_AIPV2 = "xmlschema";

    // The ZipArchiveEntry.setXxxTime() methods write the time taking into account the local time zone,
    // so we must first convert the desired timestamp value in the local time zone to have the
    // same timestamps in the ZIP file when the project is built on another computer in a
    // different time zone.
    private static final long DEFAULT_ZIP_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    // MEV#29726
    /*
     * Determino la modalità per effettuare il recupero del pacchetto aip (default: FALSE)
     */
    private static final Boolean STRICT_MODE = Boolean.FALSE;
    /*
     * Determino la versione Unisincro di riferimento per la quale effettuare il recupero del pacchetto aip (default:
     * v2.0)
     */
    private static final String UNISINCRO_V2_REF = "2.0";
    /*
     * Determino le versioni del servizio di versamento fascicolo per le quali forzare la generazione del contenuto
     * della cartella xmlschema nel pacchetto AIP conforme alla versione Unisincro di riferimento (default: v1.0 e v1.1)
     */
    private static final List<String> FORZA_VERSIONI_XML_NOT_STRICT = Arrays.asList("1.0", "1.1");
    // end MEV#29726

    public RispostaWSRecupero getRispostaWs() {
        return rispostaWs;
    }

    public RispostaWSRecuperoFasc getRispostaWsFasc() {
        return rispostaWsFasc;
    }

    public RecuperoZipFascGen(RispostaWSRecuperoFasc risp) throws NamingException {
        rispostaWsFasc = risp;
        rispostaControlli = new RispostaControlli();

        // recupera l'ejb per la lettura di informazioni, se possibile
        controlliRecuperoFasc = (ControlliRecuperoFasc) new InitialContext()
                .lookup("java:module/ControlliRecuperoFasc");

        fasHelper = (FascicoliHelper) new InitialContext().lookup("java:module/FascicoliHelper");
        objectStorageService = (ObjectStorageService) new InitialContext()
                .lookup("java:app/Parer-ejb/ObjectStorageService");

        recuperoDocumento = (RecuperoDocumento) new InitialContext().lookup("java:app/Parer-ejb/RecuperoDocumento");
    }

    public void generaZipOggettoFasc(String outputPath, RecuperoFascExt recuperoFasc) throws Exception {
        List<ContenutoRec> lstConten = null;
        String prefisso = null;
        String nomeFileZip = null;
        boolean includiSessFileVersamento = false;
        boolean includiFirmaMarcaElencoIndiceAIP = false;
        boolean includiFileIndiceAIPV2 = false;
        boolean includiFileMetadatiAIPV2 = false;
        boolean includiFileXsdAIPV2 = false;

        FileBinario zipDaScaricare = null;
        FileOutputStream tmpOutputStream = null;
        ZipArchiveOutputStream tmpZipOutputStream = null;

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            switch (recuperoFasc.getParametriRecuperoFasc().getTipoEntitaSacer()) {

            case FASC_UNISYNCRO:
                rispostaControlli = controlliRecuperoFasc
                        .leggiContVerAipUdInFASCAIPV2(recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
                prefisso = "";
                includiFileIndiceAIPV2 = true;
                includiFileMetadatiAIPV2 = true;
                includiFileXsdAIPV2 = true;
                includiSessFileVersamento = true;
                includiFirmaMarcaElencoIndiceAIP = true;
                break;
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsFascError();
                rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstConten = (List<ContenutoRec>) rispostaControlli.getrObject();
                nomeFileZip = rispostaControlli.getrString();
                rispostaWsFasc.setNomeFile(prefisso + nomeFileZip + ".zip");
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            zipDaScaricare = new FileBinario();
            try {
                zipDaScaricare.setFileSuDisco(File.createTempFile("output_", ".zip", new File(outputPath)));
                tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);

                if (includiSessFileVersamento && rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiXMLVersamentoFasc(tmpZipOutputStream, recuperoFasc, TipiLetturaXml.FASC_COMPLETO,
                            TipiXmlDaIncludere.TUTTI);
                }

                if (includiFileXsdAIPV2 && rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni degli XSD
                    this.aggiungiFileXmlSchemaFasc(tmpZipOutputStream,
                            recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
                }

                this.aggiungiFileContenutoFasc(tmpZipOutputStream, recuperoFasc, lstConten);

                if (includiFileIndiceAIPV2 && rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni dell'indice AIP Unisincro di Sacer
                    this.aggiungiIndiciAipFasc(tmpZipOutputStream,
                            recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
                }

                if (includiFileMetadatiAIPV2 && rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                    // aggiunge se necessario le varie versioni del file dei metadati di Sacer
                    this.aggiungiMetadatiFascV2(tmpZipOutputStream,
                            recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
                }

                if (includiFirmaMarcaElencoIndiceAIP && rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                    this.aggiungiElencoIndiciAipFasc(tmpZipOutputStream,
                            recuperoFasc.getParametriRecuperoFasc().getIdFascicolo(),
                            it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP
                                    .name(),
                            it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.OpTypeEnum.ELENCO_INDICI_AIP.name());
                    // this.aggiungiElencoIndiciAipUd(tmpZipOutputStream,
                    // recuperoFasc.getParametriRecuperoFasc().getIdFascicolo(),
                    // it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.MARCA_FIRMA_ELENCO_INDICI_AIP
                    // .name(),
                    // null);
                }

                tmpZipOutputStream.flush();

            } catch (IOException ex) {
                rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
                rispostaWsFasc.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                        "Errore nella generazione dello zip da scaricare  " + ex.getMessage());
                log.error("Errore nella generazione dello zip da scaricare ", ex);
            } finally {
                IOUtils.closeQuietly(tmpZipOutputStream);
                IOUtils.closeQuietly(tmpOutputStream);
                tmpZipOutputStream = null;
                tmpOutputStream = null;
                if (rispostaWsFasc.getSeverity() == SeverityEnum.ERROR && zipDaScaricare.getFileSuDisco() != null) {
                    zipDaScaricare.getFileSuDisco().delete();
                    zipDaScaricare.setFileSuDisco(null);
                }
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaWsFasc.setRifFileBinario(zipDaScaricare);
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

    private void aggiungiFileContenutoFasc(ZipArchiveOutputStream zipOutputStream, RecuperoFascExt recuperoFasc,
            List<ContenutoRec> lstConten) throws IOException, Exception {

        for (ContenutoRec tmpConten : lstConten) {

            gestisciRecuperoAipUdArchiveEntry(zipOutputStream, tmpConten, recuperoFasc);

            zipOutputStream.closeArchiveEntry();
            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsError();
                rispostaWs.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                break; // esce dal ciclo for, se incontra un errore: ormai lo zip è condannato
            }
        }
    }

    private void gestisciRecuperoAipUdArchiveEntry(ZipArchiveOutputStream zipOutputStream, ContenutoRec tmpConten,
            RecuperoFascExt recuperoFasc) throws IOException, Exception {

        AroVerIndiceAipUd verIndiceAipUd = fasHelper.findById(AroVerIndiceAipUd.class, tmpConten.getIdVerIndiceAipUd());

        /* Definisco la folder relativa al sistema di conservazione */
        String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_REC_FASC_CONTEN,
                DIRECTORY_REC_FASC_CONTEN_UD, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
        /* Definisco il nome e l'estensione del file */
        String fileName = it.eng.parer.async.utils.IOUtils.getFilename(tmpConten.getNomeFileCompleto(),
                it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.ZIP.getFileExt());
        /* Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione */
        String pathAipUd = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

        rispostaWs = recuperaAipUdUnisincro(verIndiceAipUd, recuperoFasc);

        if (rispostaWs.getSeverity() == SeverityEnum.OK) {
            ZipArchiveEntry zae = new ZipArchiveEntry(pathAipUd);
            this.filterZipEntry(zae);
            zipOutputStream.putArchiveEntry(zae);
            zipOutputStream.write(FileUtils.readFileToByteArray(rispostaWs.getRifFileBinario().getFileSuDisco()));
        }
    }

    /**
     * Metodo per fare il download del pacchetto aip di una determinata unita documentaria in un file zip
     *
     * @param verIndiceAipUd
     *            entity AroVerIndiceAipUd
     * @param recuperoFasc
     *            bean RecuperoFascExt
     *
     * @return RispostaWSRecupero oggetto con risposta recupero
     *
     * @throws Exception
     *             errore generico
     */
    public RispostaWSRecupero recuperaAipUdUnisincro(AroVerIndiceAipUd verIndiceAipUd, RecuperoFascExt recuperoFasc)
            throws Exception {
        AroUnitaDoc ud = verIndiceAipUd.getAroIndiceAipUd().getAroUnitaDoc();

        User utente = recuperoFasc.getParametriRecuperoFasc().getUtente();
        VersatoreFascType versatoreFasc = recuperoFasc.getStrutturaRecuperoFasc().getVersatore();

        Recupero recupero = new Recupero();
        recupero.setVersione("Web");
        // Versatore
        recupero.setVersatore(new VersatoreType());
        recupero.getVersatore().setAmbiente(versatoreFasc.getAmbiente());
        recupero.getVersatore().setEnte(versatoreFasc.getEnte());
        recupero.getVersatore().setStruttura(versatoreFasc.getStruttura());
        recupero.getVersatore().setUserID(versatoreFasc.getUserID());
        // Chiave
        recupero.setChiave(new ChiaveType());
        recupero.getChiave().setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        recupero.getChiave().setAnno(ud.getAaKeyUnitaDoc().toBigInteger());
        recupero.getChiave().setNumero(ud.getCdKeyUnitaDoc());

        BigDecimal idUnitaDoc = BigDecimal.valueOf(ud.getIdUnitaDoc());
        String tipoSaveFile = ud.getDecTipoUnitaDoc().getTiSaveFile();
        CostantiDB.TipoSalvataggioFile tipoSalvataggioFile = CostantiDB.TipoSalvataggioFile.valueOf(tipoSaveFile);
        CostantiDB.TipiEntitaRecupero tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO;

        // In questa fase in cui si sta richiedendo il
        // recupero per la generazione del pacchetto AIP,
        // è necessario gestire coerentemente il parametro
        // del servizio di recupero in base alla versione Unisincro con cui
        // è stato prodotto l'ultimo Indice AIP (se presente), perchè il pacchetto AIP viene generato in
        // modo differente impostando il valore
        // UNI_DOC_UNISYNCRO (versioni 0.X) o UNI_DOC_UNISYNCRO_V2 (versioni 1.X).
        // Scompatto il campo cdVerIndiceAip
        String[] numbers = verIndiceAipUd.getCdVerIndiceAip().split("[.]");
        int majorNumber = Integer.parseInt(numbers[0]);
        if (majorNumber > 0) {
            tipoEntitaRecupero = CostantiDB.TipiEntitaRecupero.UNI_DOC_UNISYNCRO_V2;
        }

        RecuperoWeb recuperoUD = new RecuperoWeb(recupero, utente, idUnitaDoc, tipoSalvataggioFile, tipoEntitaRecupero);

        return recuperoUD.recuperaOggetto();
    }

    private void aggiungiElencoIndiciAipFasc(ZipArchiveOutputStream zipOutputStream, long idFascicolo,
            String tiFileElencoVersFasc1, String tiFileElencoVersFasc2)
            throws IOException, DatatypeConfigurationException {
        ElvFileElencoVersFasc fileElencoVersFasc = null;
        String prefisso = null;
        String estensione = null;
        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecuperoFasc.leggiElvFileElencoVersFasc(idFascicolo, tiFileElencoVersFasc1,
                    tiFileElencoVersFasc2);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsFascError();
                rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                fileElencoVersFasc = rispostaControlli.getrObject() != null
                        ? (ElvFileElencoVersFasc) rispostaControlli.getrObject() : null;
                prefisso = tiFileElencoVersFasc1.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())
                        ? "ElencoIndiceAIP" : "MarcaElencoIndiceAIP";
                estensione = tiFileElencoVersFasc1.equals(ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP.name())
                        ? ".xml.p7m" : ".tsr";
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            if (fileElencoVersFasc != null) {
                controlliRecuperoFasc.loadElvElencoVersFasc(fileElencoVersFasc);
                String nmAmbiente = fileElencoVersFasc.getElvElencoVersFasc().getOrgStrut().getOrgEnte()
                        .getOrgAmbiente().getNmAmbiente();
                String nmEnte = fileElencoVersFasc.getElvElencoVersFasc().getOrgStrut().getOrgEnte().getNmEnte();
                String nmStrut = fileElencoVersFasc.getElvElencoVersFasc().getOrgStrut().getNmStrut();
                String fileName = prefisso + "-FA:" + nmAmbiente + ":" + nmEnte + ":" + nmStrut + ":"
                        + fileElencoVersFasc.getElvElencoVersFasc().getIdElencoVersFasc();
                ZipArchiveEntry zae = new ZipArchiveEntry(fileName + estensione);
                this.filterZipEntry(zae);
                zipOutputStream.putArchiveEntry(zae);
                // MEV#30399
                // recupero documento blob vs obj storage
                // build dto per recupero
                RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(
                        Constants.TiEntitaSacerObjectStorage.ELENCO_INDICI_AIP_FASC,
                        fileElencoVersFasc.getIdFileElencoVersFasc(), zipOutputStream,
                        RecBlbOracle.TabellaBlob.ELV_FILE_ELENCO_FASC);
                // recupero
                boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
                rispostaControlli.setrBoolean(esitoRecupero);
                if (!esitoRecupero) {
                    throw new IOException("Errore non gestito nel recupero del file");
                }
                // end MEV#30399
                // zipOutputStream.write(fileElencoVersFasc.getBlFileElencoVers());
                zipOutputStream.closeArchiveEntry();
            }
        }
    }

    private enum TipiLetturaXml {
        FASC_COMPLETO
    }

    private enum TipiXmlDaIncludere {
        TUTTI
    }

    private void aggiungiXMLVersamentoFasc(ZipArchiveOutputStream zipOutputStream, RecuperoFascExt recuperoFasc,
            TipiLetturaXml tlx, TipiXmlDaIncludere xmlDaIncludere) throws IOException {
        List<FasXmlVersFascicolo> lstXmlVersFasc = null;
        String fileName;

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            if (tlx == TipiLetturaXml.FASC_COMPLETO) {
                rispostaControlli = controlliRecuperoFasc
                        .leggiXMLSessioneversFasc(recuperoFasc.getParametriRecuperoFasc().getIdFascicolo());
            }

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsFascError();
                rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstXmlVersFasc = (List<FasXmlVersFascicolo>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            if (lstXmlVersFasc != null && !lstXmlVersFasc.isEmpty()) {
                int contaRequest = 1;
                int contaResponse = 1;
                int contaIndiceMM = 1;
                int contaRappVers = 1;
                int contaBoh = 1;

                switch (recuperoFasc.getParametriRecuperoFasc().getTipoEntitaSacer()) {
                case FASC_UNISYNCRO:
                    boolean closeEntry = false;
                    for (FasXmlVersFascicolo tmpXml : lstXmlVersFasc) {
                        // urn
                        // Recupero lo urn ORIGINALE
                        String urnXmlVers = tmpXml.getDsUrnXmlVers();
                        /* Definisco la folder relativa al sistema di conservazione */
                        String tmpPath = urnXmlVers
                                .replaceAll(it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnXmlVers), "SIP-FA");
                        String path = it.eng.parer.async.utils.IOUtils.extractPartUrnName(tmpPath, true);
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
                            // Recupero XML Richiesta Fascicolo salvato su OS
                            String blFile = tmpXml.getBlXmlVers();
                            if (blFile == null) {
                                Map<String, String> xmls = objectStorageService
                                        .getObjectXmlVersFascicolo(tmpXml.getFasFascicolo().getIdFascicolo());
                                // recupero oggetti da O.S. (se presenti)
                                if (!xmls.isEmpty()) {
                                    blFile = xmls.get(tmpXml.getTiXmlVers());
                                }
                            }
                            zipOutputStream.write(blFile.getBytes(StandardCharsets.UTF_8));
                            // End recupero XML richiesta

                            // zipOutputStream.write(tmpXml.getBlXmlVers().getBytes(StandardCharsets.UTF_8));
                            closeEntry = true;
                        }
                    }

                    if (closeEntry) {
                        zipOutputStream.closeArchiveEntry();
                    }
                    break;
                default:
                    for (FasXmlVersFascicolo tmpXml : lstXmlVersFasc) {
                        fileName = null;
                        switch (tmpXml.getTiXmlVers()) {
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
                            zipOutputStream.write(tmpXml.getBlXmlVers().getBytes(StandardCharsets.UTF_8));
                            zipOutputStream.closeArchiveEntry();
                        }
                    }
                    break;
                }
            }
        }
    }

    private void aggiungiFileXmlSchemaFasc(ZipArchiveOutputStream zipOutputStream, long idFascicolo)
            throws IOException {

        List<FasXmlVersFascicolo> lstXmlVersFascicolo = null;
        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecuperoFasc.leggiXMLSessioneversFasc(idFascicolo);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsFascError();
                rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstXmlVersFascicolo = (List<FasXmlVersFascicolo>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            if (lstXmlVersFascicolo != null && !lstXmlVersFascicolo.isEmpty()) {
                boolean closeEntry = false;

                FasXmlVersFascicolo xmlVersFascicolo = lstXmlVersFascicolo.stream()
                        .filter(xml -> xml.getTiXmlVers().equals("RICHIESTA")).findAny().orElse(null);

                // MEV#29726
                // Se la modalità strict non è attiva la logica forza la generazione del contenuto della cartella
                // xmlschema nel pacchetto AIP conforme alla versione
                // Unisincro specificata dalla costante UNISINCRO_V2_REF
                // per le versioni del servizio di versamento fascicolo specificate dalla costante
                // FORZA_VERSIONI_XML_NOT_STRICT
                String codiceVersioneXsd = "";
                if (STRICT_MODE.equals(Boolean.FALSE)
                        && FORZA_VERSIONI_XML_NOT_STRICT.contains(xmlVersFascicolo.getCdVersioneXml())
                        && UNISINCRO_V2_REF.compareTo(xmlVersFascicolo.getCdVersioneXml()) > 0) {
                    codiceVersioneXsd = "2.0";
                } else {
                    codiceVersioneXsd = xmlVersFascicolo.getCdVersioneXml();
                }

                FileXSD xsdPIndexFileSource = (!"2.0".equals(codiceVersioneXsd)) ? FileXSD.AIP_FASC_UNISINCRO_2_XSD
                        : FileXSD.AIP_FASC_UNISINCRO_2_XSD_V2;
                FileXSD xsdMoreInfoSelfDescSource = (!"2.0".equals(codiceVersioneXsd)) ? FileXSD.AIP_FASC_SELF_DESC_XSD
                        : FileXSD.AIP_FASC_SELF_DESC_XSD_V2;
                // end MEV#29726

                FileXSD xsdMoreInfoPVolumeSource = (!"2.0".equals(xmlVersFascicolo.getCdVersioneXml()))
                        ? FileXSD.AIP_FASC_PROF_XSD : FileXSD.AIP_FASC_PROF_XSD_V2;

                File xsdMoreInfoSelfDesc = FileXSDUtil.getFileXSD(xsdMoreInfoSelfDescSource);
                if (xsdMoreInfoSelfDesc != null) {
                    ZipArchiveEntry zae = new ZipArchiveEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                            DIRECTORY_XSD_AIPV2, xsdMoreInfoSelfDesc.getName(),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);
                    zipOutputStream
                            .write(FileUtils.readFileToString(xsdMoreInfoSelfDesc, StandardCharsets.UTF_8).getBytes());
                    closeEntry = true;
                }

                File xsdMoreInfoPVolume = FileXSDUtil.getFileXSD(xsdMoreInfoPVolumeSource);
                if (xsdMoreInfoPVolume != null) {
                    ZipArchiveEntry zae = new ZipArchiveEntry(it.eng.parer.async.utils.IOUtils.getAbsolutePath(
                            DIRECTORY_XSD_AIPV2, xsdMoreInfoPVolume.getName(),
                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);
                    zipOutputStream
                            .write(FileUtils.readFileToString(xsdMoreInfoPVolume, StandardCharsets.UTF_8).getBytes());
                    closeEntry = true;
                }

                File xsdPIndexFile = FileXSDUtil.getFileXSD(xsdPIndexFileSource);
                if (xsdPIndexFile != null) {
                    ZipArchiveEntry zae = new ZipArchiveEntry(
                            it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2,
                                    xsdPIndexFile.getName(), it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);
                    zipOutputStream.write(FileUtils.readFileToString(xsdPIndexFile, StandardCharsets.UTF_8).getBytes());
                    closeEntry = true;
                }

                if ("2.0".equals(xmlVersFascicolo.getCdVersioneXml())) {
                    List<FasXmlFascicolo> lstXmlFascicolo = null;
                    if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                        rispostaControlli.reset();
                        rispostaControlli = controlliRecuperoFasc.leggiXMLProfiloFascList(idFascicolo);

                        if (!rispostaControlli.isrBoolean()) {
                            setRispostaWsFascError();
                            rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
                        } else {
                            lstXmlFascicolo = (List<FasXmlFascicolo>) rispostaControlli.getrObject();
                        }
                    }

                    if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
                        for (FasXmlFascicolo xmlFascicolo : lstXmlFascicolo) {
                            // Recupero lo urn ORIGINALE
                            String profilo = xmlFascicolo.getTiModelloXsd().name();

                            /* Definisco il nome e l'estensione del file */
                            String fileName = it.eng.parer.async.utils.IOUtils
                                    .getFilename(CaseUtils.toCamelCase(profilo, true, '_') + "-"
                                            + xmlFascicolo.getDecModelloXsdFascicolo().getCdXsd(), "xsd");

                            ZipArchiveEntry zae = new ZipArchiveEntry(
                                    it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_XSD_AIPV2, fileName,
                                            it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR));
                            this.filterZipEntry(zae);
                            zipOutputStream.putArchiveEntry(zae);
                            zipOutputStream.write(xmlFascicolo.getDecModelloXsdFascicolo().getBlXsd()
                                    .getBytes(StandardCharsets.UTF_8));
                            closeEntry = true;
                        }
                    }
                }

                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }
    }

    private void aggiungiIndiciAipFasc(ZipArchiveOutputStream zipOutputStream, long idFascicolo) throws IOException {
        List<FasFileMetaVerAipFasc> lstFileMetaIndice = null;
        String fileName;

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecuperoFasc.leggiXMLIndiceAIPFasc(idFascicolo);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsFascError();
                rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstFileMetaIndice = (List<FasFileMetaVerAipFasc>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            if (lstFileMetaIndice != null && !lstFileMetaIndice.isEmpty()) {
                boolean closeEntry = false;
                FasFileMetaVerAipFasc fileMetaIndiceLastVers = lstFileMetaIndice.remove(0);
                if (fileMetaIndiceLastVers.getFasMetaVerAipFascicolo() != null) {
                    // Recupero lo urn ORIGINALE
                    // fileName = fileMetaIndiceLastVers.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo();

                    // MEV#27035
                    /* Definisco il nome e l'estensione del file */
                    fileName = "PIndexFA.xml";
                    // end MEV#27035

                    ZipArchiveEntry zae = new ZipArchiveEntry(fileName);
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);

                    // MEV #30398
                    String blFile = fileMetaIndiceLastVers.getBlFileVerIndiceAip();
                    if (fileMetaIndiceLastVers.getBlFileVerIndiceAip() == null) {
                        Map<String, String> xmls = objectStorageService.getObjectXmlIndiceAipFasc(fileMetaIndiceLastVers
                                .getFasMetaVerAipFascicolo().getFasVerAipFascicolo().getIdVerAipFascicolo());
                        // recupero oggetti da O.S. (se presenti)
                        if (!xmls.isEmpty()) {
                            blFile = xmls.get("INDICE");
                        }
                    }
                    // end MEV #30398

                    zipOutputStream.write(blFile.getBytes(StandardCharsets.UTF_8));
                    closeEntry = true;
                }

                for (FasFileMetaVerAipFasc fileMetaIndicePrecVers : lstFileMetaIndice) {
                    if (fileMetaIndicePrecVers.getFasMetaVerAipFascicolo() != null) {

                        // Recupero lo urn ORIGINALE
                        String urnIxAip = fileMetaIndicePrecVers.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo();

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
                        zipOutputStream
                                .write(fileMetaIndicePrecVers.getBlFileVerIndiceAip().getBytes(StandardCharsets.UTF_8));
                        closeEntry = true;
                    }
                }
                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }
    }

    private void aggiungiMetadatiFascV2(ZipArchiveOutputStream zipOutputStream, long idFascicolo) throws IOException {
        List<FasFileMetaVerAipFasc> lstFasFileMetaFasc = null;
        String fileName;

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            rispostaControlli.reset();
            rispostaControlli = controlliRecuperoFasc.leggiXMLMetadatiFasc(idFascicolo);

            if (!rispostaControlli.isrBoolean()) {
                setRispostaWsFascError();
                rispostaWsFasc.setEsitoWsError(rispostaControlli.getCodErr(), rispostaControlli.getDsErr());
            } else {
                lstFasFileMetaFasc = (List<FasFileMetaVerAipFasc>) rispostaControlli.getrObject();
            }
        }

        if (rispostaWsFasc.getSeverity() == SeverityEnum.OK) {
            if (lstFasFileMetaFasc != null && !lstFasFileMetaFasc.isEmpty()) {
                boolean closeEntry = false;
                FasFileMetaVerAipFasc fileMetaIndiceLastVers = lstFasFileMetaFasc.remove(0);
                if (fileMetaIndiceLastVers.getFasMetaVerAipFascicolo() != null) {
                    // Recupero lo urn ORIGINALE
                    String urnMetaFasc = fileMetaIndiceLastVers.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo();

                    /* Definisco il nome e l'estensione del file */
                    fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                            it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnMetaFasc, true),
                            it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                    /*
                     * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                     */
                    String path = it.eng.parer.async.utils.IOUtils.getAbsolutePath(DIRECTORY_REC_FASC_METADATI,
                            fileName, it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                    /* Definisco il nome e l'estensione del file */
                    // fileName = "Fascicolo.xml";
                    ZipArchiveEntry zae = new ZipArchiveEntry(path);
                    this.filterZipEntry(zae);
                    zipOutputStream.putArchiveEntry(zae);

                    // MEV #30398
                    String blFile = fileMetaIndiceLastVers.getBlFileVerIndiceAip();
                    if (fileMetaIndiceLastVers.getBlFileVerIndiceAip() == null) {
                        Map<String, String> xmls = objectStorageService.getObjectXmlIndiceAipFasc(fileMetaIndiceLastVers
                                .getFasMetaVerAipFascicolo().getFasVerAipFascicolo().getIdVerAipFascicolo());
                        // recupero oggetti da O.S. (se presenti)
                        if (!xmls.isEmpty()) {
                            blFile = xmls.get("FASCICOLO");
                        }
                    }
                    // end MEV #30398

                    zipOutputStream.write(blFile.getBytes(StandardCharsets.UTF_8));

                    closeEntry = true;
                }

                for (FasFileMetaVerAipFasc fileMetaIndicePrecVers : lstFasFileMetaFasc) {
                    if (fileMetaIndicePrecVers.getFasMetaVerAipFascicolo() != null) {

                        // Recupero lo urn ORIGINALE
                        String urnMetaFasc = fileMetaIndicePrecVers.getFasMetaVerAipFascicolo().getDsUrnMetaFascicolo();

                        /* Definisco la folder relativa al sistema di conservazione */
                        String folder = it.eng.parer.async.utils.IOUtils.getPath(DIRECTORY_PIX_AIPV2,
                                StringUtils.capitalize(Constants.SACER.toLowerCase()),
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);
                        /* Definisco il nome e l'estensione del file */
                        fileName = it.eng.parer.async.utils.IOUtils.getFilename(
                                it.eng.parer.async.utils.IOUtils.extractPartUrnName(urnMetaFasc, true),
                                it.eng.parer.async.utils.IOUtils.CONTENT_TYPE.XML.getFileExt());
                        /*
                         * Definisco il percorso relativo del file rispetto alla posizione dell'indice di conservazione
                         */
                        String pathPIndexSource = it.eng.parer.async.utils.IOUtils.getAbsolutePath(folder, fileName,
                                it.eng.parer.async.utils.IOUtils.UNIX_FILE_SEPARATOR);

                        ZipArchiveEntry zae = new ZipArchiveEntry(pathPIndexSource);
                        this.filterZipEntry(zae);
                        zipOutputStream.putArchiveEntry(zae);
                        zipOutputStream
                                .write(fileMetaIndicePrecVers.getBlFileVerIndiceAip().getBytes(StandardCharsets.UTF_8));
                        closeEntry = true;
                    }
                }
                if (closeEntry) {
                    zipOutputStream.closeArchiveEntry();
                }
            }
        }
    }

    private void setRispostaWsError() {
        rispostaWs.setSeverity(SeverityEnum.ERROR);
        rispostaWs.setErrorCode(rispostaControlli.getCodErr());
        rispostaWs.setErrorMessage(rispostaControlli.getDsErr());
    }

    private void setRispostaWsFascError() {
        rispostaWsFasc.setSeverity(SeverityEnum.ERROR);
        rispostaWsFasc.setErrorCode(rispostaControlli.getCodErr());
        rispostaWsFasc.setErrorMessage(rispostaControlli.getDsErr());
    }

}
