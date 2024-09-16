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

package it.eng.parer.ws.recuperoreportvf.ejb;

import static it.eng.parer.ws.utils.Costanti.UKNOWN_EXT;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecReportServizioVerificaCompDoc;
import it.eng.parer.entity.DecServizioVerificaCompDoc;
import it.eng.parer.entity.FirReport;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.DecReportServizioVerificaCompDoc.TiReportServizioVerificaCompDoc;
import it.eng.parer.entity.constraint.FiUrnReport.TiUrnReport;
import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.SacerException;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants.TiEntitaSacerObjectStorage;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recuperoreportvf.dto.ComponenteReportvfRec;
import it.eng.parer.ws.recuperoreportvf.utils.ReportvfHelper;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versamento.dto.FileBinario;

@Stateless(mappedName = "ControlliReportvf")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliReportvf {

    private static final Logger LOG = LoggerFactory.getLogger(ControlliReportvf.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;

    @EJB
    private RecuperoDocumento recuperoDocumento;

    /**
     * Verifica di esistenza del report di verifica firma per il componente
     *
     * @param idCompDoc
     *            id del componente da verificare
     *
     * @return risposta al controllo effetto (vedi {@link RispostaControlli})
     */
    public RispostaControlli checkReportvfExistenceAndGetZipName(long idCompDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            //
            String queryStr = "select t from FirReport t " + "where t.aroCompDoc.idCompDoc = :idCompDoc";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idCompDoc", idCompDoc);

            FirReport firReport = (FirReport) query.getSingleResult();
            // file name report
            rispostaControlli.setrString(generaNomeFileCompReportvf(firReport));
            // report
            rispostaControlli.setrObject(firReport);
            rispostaControlli.setrBoolean(true);

        } catch (NoResultException e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.COMP_006_003);
            rispostaControlli
                    .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.COMP_006_003, generaNomeFileComp(idCompDoc)));
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliReportvf.checkReportvfExistenceAndGetZipName " + e.getMessage()));
            LOG.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }

    private String generaNomeFileCompReportvf(FirReport tmpReport) {
        // urn
        String reportUrnOrig = new ArrayList<>(tmpReport.getFirUrnReports()).stream()
                .filter(c -> c.getTiUrn().equals(TiUrnReport.ORIGINALE)).collect(Collectors.toList()).get(0).getDsUrn();

        String reportUrnNorm = new ArrayList<>(tmpReport.getFirUrnReports()).stream()
                .filter(c -> c.getTiUrn().equals(TiUrnReport.NORMALIZZATO)).collect(Collectors.toList()).get(0)
                .getDsUrn();

        // dto
        ComponenteReportvfRec tmpReportvfRec = new ComponenteReportvfRec(reportUrnNorm, reportUrnOrig);
        tmpReportvfRec.setIdCompDoc(tmpReport.getAroCompDoc().getIdCompDoc());
        tmpReportvfRec.setIdReportvf(tmpReport.getIdFirReport());

        //
        return generaNomeFile(tmpReport.getAroCompDoc(), tmpReportvfRec);
    }

    private String generaNomeFileComp(long idComp) {
        AroCompDoc tmpCmp = entityManager.find(AroCompDoc.class, idComp);
        // urn
        String urnCompletoIniz = null;
        //
        String urnCompleto = null;
        AroCompUrnCalc tmpCompUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO);
        if (tmpCompUrnCalc != null) {
            urnCompleto = tmpCompUrnCalc.getDsUrn();
        } else {
            urnCompleto = tmpCmp.getDsUrnCompCalc();
        }
        // urn iniz
        tmpCompUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
        if (tmpCompUrnCalc != null) {
            urnCompletoIniz = tmpCompUrnCalc.getDsUrn();
        }
        // dto
        ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
        tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
        // Gestisco il formato
        String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
        if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
            tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
        } else {
            if (tmpCmp.getDecFormatoFileDoc() != null) {
                tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
            } else {
                tmpCRec.setEstensioneFile(UKNOWN_EXT);
            }
        }
        return generaNomeFile(tmpCmp, tmpCRec);
    }

    private String generaNomeFile(AroCompDoc acd, ComponenteRec reportvf) {
        StringBuilder tmpString = new StringBuilder();
        AroUnitaDoc aud = acd.getAroStrutDoc().getAroDoc().getAroUnitaDoc();
        tmpString.append(aud.getCdRegistroKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getAaKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getCdKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(reportvf.getNomeFileBreve());
        return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    /**
     * Generazione del report di verifica firma (recuperato o da object storage o su database come BLOB)
     *
     * @param recupero
     *            classe di appoggio su vengono gestite le informazioni / metadati legate al recupero
     * @param zipDaScaricare
     *            oggetto contenente il "puntatore a file" con lo zip scaricabile del report verifica firma
     *
     * @throws SacerException
     *             eccezione generica
     * @throws IOException
     *             eccezione generica
     */
    public void generateReportvf(RecuperoExt recupero, FileBinario zipDaScaricare) throws SacerException, IOException {
        // blob on file (file di appoggio per scarico report)
        Path tmpElabReportDir = Files.createTempDirectory("genreportvf");
        File tmpReportvfZipFromObjstgOrBlob = File.createTempFile("reportvfzip_", ".tmp", tmpElabReportDir.toFile());

        try (FileOutputStream tmpOutputStream = new FileOutputStream(zipDaScaricare.getFileSuDisco());
                ZipArchiveOutputStream tmpZipOutputStream = new ZipArchiveOutputStream(tmpOutputStream);
                FileOutputStream tmpFileWriter = new FileOutputStream(tmpReportvfZipFromObjstgOrBlob)) {

            // get blob report (from object storage or db)
            // build dto per recupero
            RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.REPORTVF,
                    recupero.getParametriRecupero().getIdComponente(), tmpFileWriter,
                    RecBlbOracle.TabellaBlob.FIR_REPORT);
            // recupero
            boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
            if (!esitoRecupero) {
                throw new SacerException("Errore non gestito nel recupero del file", SacerErrorCategory.INTERNAL_ERROR);
            }
            // get decserviziovf
            AroCompDoc tmpCompDoc = entityManager.find(AroCompDoc.class,
                    recupero.getParametriRecupero().getIdComponente());
            DecServizioVerificaCompDoc tmpServizioVerificaCompDoc = tmpCompDoc.getDecServizioVerificaCompDoc();
            if (tmpServizioVerificaCompDoc == null) {
                throw new SacerException("Servizio di verifica firma non presente o non recuperato correttamente",
                        SacerErrorCategory.INTERNAL_ERROR);
            }
            // read file zip
            parseReportvfAsZip(tmpElabReportDir, tmpReportvfZipFromObjstgOrBlob, tmpZipOutputStream,
                    tmpServizioVerificaCompDoc);
        } finally {
            FileUtils.deleteDirectory(tmpElabReportDir.toFile());
        }
    }

    private void parseReportvfAsZip(Path tmpElabReportDir, File tmpReportvfZipFromObjstgOrBlob,
            ZipArchiveOutputStream tmpZipOutputStream, DecServizioVerificaCompDoc tmpServizioVerificaCompDoc)
            throws IOException, SacerException {
        //
        try (ZipFile tmpReportvfZip = new ZipFile(tmpReportvfZipFromObjstgOrBlob)) {
            final Enumeration<? extends ZipEntry> entries = tmpReportvfZip.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                // read xml and copy on file
                File tmpReportvfEntry = File.createTempFile("reportvf_", ".tmp", tmpElabReportDir.toFile());
                FileUtils.copyInputStreamToFile(tmpReportvfZip.getInputStream(entry), tmpReportvfEntry);

                Map<String, DecReportServizioVerificaCompDoc> mapreportvfFilenameAndReportS = new HashMap<>();
                /*
                 * Questa condizione è sempre verificata in quanto, se una versione di servizio non è censita, viene
                 * assegnato un default "NO_VERSION". Al momento la logica di lettura coincide con la versione 1 del
                 * report zip
                 *
                 * Parsing report versione v 1.0
                 */
                // filter by extension (only xml files)
                boolean isXmlReport = entry.getName().matches("(?i:.*xml.*)");
                if (isXmlReport) {
                    if (CostantiDB.ReportvfZipVersion.V_10.equals(CostantiDB.ReportvfZipVersion.getByServiceAndVersion(
                            tmpServizioVerificaCompDoc.getCdServizio().name(),
                            tmpServizioVerificaCompDoc.getNmVersione()))) {

                        mapreportvfFilenameAndReportS = elabReportTypeAndFilenamePrsr10(tmpServizioVerificaCompDoc,
                                entry.getName());
                    }

                    // Controllo di sicurezza (dipende dal parsing sopra, una versione non censita del report può non
                    // generare
                    // la trasformazione del report)
                    if (!mapreportvfFilenameAndReportS.isEmpty()) {
                        // decReportServizioVerificaCompDoc (contiene configurazioni per trasformazione)
                        DecReportServizioVerificaCompDoc tmpReportServizioVerificaCompDoc = new ArrayList<>(
                                mapreportvfFilenameAndReportS.values()).get(0);

                        // transform xml
                        Map<String, File> mapReportvfExtAndFilefTransf = transformReportApplyXslt(
                                tmpReportServizioVerificaCompDoc, tmpReportvfEntry,
                                tmpElabReportDir.toAbsolutePath().toString());

                        String tmpReportvfFilename = new ArrayList<>(mapreportvfFilenameAndReportS.keySet()).get(0);
                        //
                        File tmpReportvfTransformed = new ArrayList<>(mapReportvfExtAndFilefTransf.values()).get(0);
                        String tmpReportvfExtension = new ArrayList<>(mapReportvfExtAndFilefTransf.keySet()).get(0);

                        // add zip entry
                        ReportvfHelper.addReportAsZipEntry(tmpZipOutputStream, tmpReportvfTransformed,
                                tmpReportvfFilename, tmpReportvfExtension);
                    }

                    // add xml as zip entry
                    ReportvfHelper.addOriginalReportAsZipEntry(tmpZipOutputStream, tmpReportvfEntry, entry.getName());
                }
            }
        }
    }

    private Map<String, DecReportServizioVerificaCompDoc> elabReportTypeAndFilenamePrsr10(
            DecServizioVerificaCompDoc tmpServizioVerificaCompDoc, String reportvfEntryName) {
        // exclude directory from entry name
        // get report type
        String tiReport = TiReportServizioVerificaCompDoc.ORIG.name(); // default
        Pattern pReportType = Pattern.compile("\\" + File.separatorChar + "(.*)_urn:");
        Matcher mReportType = pReportType.matcher(reportvfEntryName);
        if (mReportType.find()) {
            tiReport = mReportType.group(1);
        } else {
            LOG.warn(
                    "ControlliReportvf.elabReportTypeAndFilenamePrsr01 errore in fase di parsing, il tipo report non è stato individuato, assegnato valore di default {}",
                    tiReport);
        }

        final String finalReportType = tiReport;
        // report name
        String reportName = Costanti.UrnFormatter.SPATH_FILENAME_STANDARD_ERR; // default
        Pattern pReportName = Pattern.compile("urn:[^.]*");
        Matcher mReportName = pReportName.matcher(reportvfEntryName);
        if (mReportName.find()) {
            reportName = MessaggiWSFormat.bonificaUrnPerNomeFile(mReportName.group(0));
        } else {
            LOG.warn(
                    "ControlliReportvf.elabReportTypeAndFilenamePrsr01 errore in fase di parsing, il nome del report non è stato individuato, assegnato valore di default {}",
                    reportName);
        }
        //
        DecReportServizioVerificaCompDoc tmpReportServizioVerificaCompDoc = null;

        if (new ArrayList<>(tmpServizioVerificaCompDoc.getDecReportServizioVerificaCompDocs()).stream()
                .filter(r -> r.getTiReport().name().equalsIgnoreCase(finalReportType)).count() != 0) {
            tmpReportServizioVerificaCompDoc = new ArrayList<>(
                    tmpServizioVerificaCompDoc.getDecReportServizioVerificaCompDocs()).stream()
                            .filter(r -> r.getTiReport().name().equalsIgnoreCase(finalReportType))
                            .collect(Collectors.toList()).get(0);
        } else {
            // original source
            tmpReportServizioVerificaCompDoc = tmpServizioVerificaCompDoc.getDecReportServizioVerificaCompDocs()
                    .stream().filter(r -> r.getTiReport().equals(TiReportServizioVerificaCompDoc.ORIG))
                    .collect(Collectors.toList()).get(0);
        }

        // final result
        // immutable map
        return Collections.singletonMap(reportName, tmpReportServizioVerificaCompDoc);
    }

    private Map<String, File> transformReportApplyXslt(DecReportServizioVerificaCompDoc reportServizio,
            File tmpReportFromClob, String outputPath) throws SacerException, IOException {
        File tmpReportGenerated = null;
        String extension = null;

        switch (reportServizio.getTiFormato()) {
        case PDF:
            tmpReportGenerated = ReportvfHelper.generatePdfReport(tmpReportFromClob, reportServizio.getBlXsltReport(),
                    outputPath);
            extension = "pdf";
            break;
        case HTML:
            tmpReportGenerated = ReportvfHelper.generateGenericFromXslt(tmpReportFromClob,
                    reportServizio.getBlXsltReport(), outputPath);
            extension = "html";
            break;

        default:
            /* XML */
            tmpReportGenerated = ReportvfHelper.generateGenericFromXslt(tmpReportFromClob,
                    reportServizio.getBlXsltReport(), outputPath);
            extension = "xml";
            break;
        }

        // final result
        // immutable map
        return Collections.singletonMap(extension, tmpReportGenerated);
    }

    /*
     * TOFIX: da verificare la naming convetion dei singoli file all'interno dello zip. Attualmente viene utilizzato
     * l'URN normalizzato dei singoli report
     */
    @Deprecated
    private String elabZipEntryName(String reportName, DecReportServizioVerificaCompDoc reportServizio) {
        String zipEntryFilename = null;
        switch (reportServizio.getTiReport()) {
        case SIMPLE:
            zipEntryFilename = "ReportVerificaFirmaEidasSintesi";
            break;
        case DETAILED:
            zipEntryFilename = "ReportVerificaFirmaEidasDettaglio";
            break;
        case DIAG_DATA:
            zipEntryFilename = "ReportVerificaFirmaEidas";
            break;
        case CRYPTO:
            zipEntryFilename = "ReportVerificaFirmeCrypto";
            break;
        default:
            zipEntryFilename = "ReportVerificaFirme";
            break;
        }
        return reportName.concat("_" + zipEntryFilename);// TOFIX: da verificare
    }
}
