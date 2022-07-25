package it.eng.parer.ws.recuperoreportvf.utils;

import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

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
import org.apache.commons.compress.archivers.zip.X5455_ExtendedTimestamp;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;

import it.eng.parer.exception.ParerErrorCategory.SacerErrorCategory;
import it.eng.parer.exception.SacerException;

public class ReportvfHelper {

    /*
     * La stessa directory viene utilizzata nel report zip "confezinato" al versamento e anche alla generazione stesssa
     * 
     * Report v0 e v1
     */
    public static final String DIRECTORY_REPORT = "FileTrasformati";

    private ReportvfHelper() {
        throw new IllegalStateException("Utility class");
    }

    // The ZipArchiveEntry.setXxxTime() methods write the time taking into account
    // the local time zone,
    // so we must first convert the desired timestamp value in the local time zone
    // to have the
    // same timestamps in the ZIP file when the project is built on another computer
    // in a
    // different time zone.
    private static final long DEFAULT_ZIP_TIMESTAMP = LocalDateTime.of(2000, 1, 1, 0, 0, 0, 0)
            .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    /**
     * Effettua trasformazione report (xml) in PDF
     * 
     * @param report
     *            file del singolo report
     * @param xslt
     *            foglio di stile
     * @param outputPath
     *            path di generazione
     * 
     * @return trasformazione (se riuscita) del report in formato PDF
     * 
     * @throws SacerException
     *             eccezione generica
     * @throws IOException
     *             eccezione generica
     */
    public static File generatePdfReport(File report, String xslt, String outputPath)
            throws SacerException, IOException {
        File tmpPdf = File.createTempFile("output_", ".pdf", new File(outputPath));
        try (FileOutputStream out = new FileOutputStream(tmpPdf)) {
            DefaultConfigurationBuilder cfgBuilder = new DefaultConfigurationBuilder();
            Configuration cfg = cfgBuilder
                    .build(ReportvfHelper.class.getClassLoader().getResourceAsStream("META-INF/fop.xconf"));
            FopFactory fopFactory = FopFactory.newInstance();
            fopFactory.setUserConfig(cfg);
            Fop fop = fopFactory.newFop(MIME_PDF, out);
            TransformerFactory factory = TransformerFactory
                    .newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
            Templates templates = factory.newTemplates(new StreamSource(new StringReader(xslt)));
            Source src = new StreamSource(new FileInputStream(report));
            Result res = new SAXResult(fop.getDefaultHandler());
            Transformer transformer = templates.newTransformer();
            transformer.transform(src, res);
        } catch (TransformerException | ConfigurationException | SAXException ex) {
            Files.delete(tmpPdf.toPath()); // delete file
            tmpPdf = null;
            throw new SacerException(ex, SacerErrorCategory.INTERNAL_ERROR);
        }
        return tmpPdf;
    }

    /**
     * "Generica" trasformazione del file report applicando un determinato foglio di stile
     * 
     * @param report
     *            file del singolo report
     * @param xslt
     *            foglio di stile
     * @param outputPath
     *            path di generazione
     * 
     * @return trasformazione (se riuscita) del report
     * 
     * @throws SacerException
     *             eccezione generica
     * @throws IOException
     *             eccezione generica
     */
    public static File generateGenericFromXslt(File report, String xslt, String outputPath)
            throws SacerException, IOException {
        File tmpResult = File.createTempFile("output_", ".tmp", new File(outputPath));
        try (FileOutputStream out = new FileOutputStream(tmpResult)) {
            TransformerFactory factory = TransformerFactory
                    .newInstance("org.apache.xalan.processor.TransformerFactoryImpl", null);
            Templates templates = factory.newTemplates(new StreamSource(new StringReader(xslt)));
            Source src = new StreamSource(new FileInputStream(report));
            Transformer transformer = templates.newTransformer();
            transformer.transform(src, new StreamResult(out));
        } catch (TransformerException ex) {
            Files.delete(tmpResult.toPath()); // delete file
            tmpResult = null;
            throw new SacerException(ex, SacerErrorCategory.INTERNAL_ERROR);
        }
        return tmpResult;
    }

    /**
     * Aggiunta entry su file ZIP del report generato (file trasformato)
     *
     * @param tmpZipOutputStream
     *            zip outputstream
     * @param reportvfTransformed
     *            file report da aggiungere
     * @param reportvfFilename
     *            nome file report
     * @param reportvfExtension
     *            estensione
     * 
     * @throws IOException
     *             eccezione generica
     */
    public static void addReportAsZipEntry(ZipArchiveOutputStream tmpZipOutputStream, File reportvfTransformed,
            String reportvfFilename, String reportvfExtension) throws IOException {
        //
        addReportAsZipEntry(tmpZipOutputStream, reportvfTransformed, Optional.of(DIRECTORY_REPORT), reportvfFilename,
                Optional.of(reportvfExtension));
    }

    /**
     * Aggiunta entry su file ZIP del report generato (file originale)
     * 
     * @param tmpZipOutputStream
     *            zip outputstream
     * @param xmlFile
     *            file report da aggiungere
     * @param fileName
     *            nome file report
     * 
     * @throws IOException
     *             eccezione generica
     */
    public static void addOriginalReportAsZipEntry(ZipArchiveOutputStream tmpZipOutputStream, File xmlFile,
            String fileName) throws IOException {
        //
        addReportAsZipEntry(tmpZipOutputStream, xmlFile, Optional.empty(), fileName, Optional.empty());
    }

    private static void addReportAsZipEntry(ZipArchiveOutputStream tmpZipOutputStream, File reportvfTransformed,
            Optional<String> dirName, String fileName, Optional<String> ext) throws IOException {
        final String entryName = (dirName.isPresent() ? dirName.get() + "/" : "").concat(fileName)
                .concat(ext.isPresent() ? "." + ext.get() : "");
        ZipArchiveEntry zae = new ZipArchiveEntry(entryName);
        filterZipEntry(zae);
        tmpZipOutputStream.putArchiveEntry(zae);

        try (FileInputStream fis = new FileInputStream(reportvfTransformed)) {
            IOUtils.copy(fis, tmpZipOutputStream);
            tmpZipOutputStream.closeArchiveEntry();
        } finally {
            Files.delete(reportvfTransformed.toPath());
        }
    }

    private static ZipArchiveEntry filterZipEntry(ZipArchiveEntry entry) {
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

}
