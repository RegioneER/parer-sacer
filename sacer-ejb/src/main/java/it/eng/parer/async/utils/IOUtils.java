package it.eng.parer.async.utils;

import it.eng.parer.async.utils.io.AllFileFilter;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fornisce servizi di utilità per gestire l'IO.
 * 
 * 
 * @author DiLorenzo_F
 * 
 */
public final class IOUtils {

    private static final Logger LOG = LoggerFactory.getLogger(IOUtils.class);

    /** Proprietà di sistema */
    public static final String FILE_SEPARATOR = System.getProperty("file.separator");
    /** Proprietà di sistema */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    /** Definizione del <code>current-path</code> */
    public static final String CURRENT_DIR = String.format(".%s", FILE_SEPARATOR);
    /** Definizione del <code>current-path</code> */
    public static final String PARENT_DIR = String.format("..%s", FILE_SEPARATOR);

    /** Dimensione del buffer usasto dalle operazioni eseguite su una istanza di <code>OutputStream</code> */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /** Carattere utilizzato per individuare il separatore di file (ambiente UNIX) */
    private static final char UNIX_FILE_SEP = '/';
    /** Carattere utilizzato per individuare il separatore di file (ambiente WINDOWS) */
    private static final char WINDOWS_FILE_SEP = '\\';
    /** Carattere utilizzato per individuare il separatore di estensione */
    private static final char FILE_EXT_SEP = '.';

    /** Proprietà di sistema (ambiente UNIX) */
    public static final String UNIX_FILE_SEPARATOR = String.valueOf(UNIX_FILE_SEP);
    /** Proprietà di sistema (ambiente WINDOWS) */
    public static final String WINDOWS_FILE_SEPARATOR = String.valueOf(WINDOWS_FILE_SEP);

    /** Definizione dei contenuti gestiti */
    public enum CONTENT_TYPE {
        DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "WORD", "doc,docx"),
        PDF("application/pdf", "PDF", "pdf"), RTF("application/rtf", "RTF", "rtf"), TXT("text/plain", "TXT", "txt"),
        XLS("application/vnd.ms-excel", "EXCEL 2003", "xls"),
        XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "EXCEL", "xlsx"),
        ZIP("application/zip", "ZIP", "zip"), XML("application/xml;charset=UTF-8", "XML", "xml");

        /** <code>content-type</code> associato al formato di output del documento */
        private final String mContentType;
        /** Descrizione del formato di output del documento */
        private final String mDescription;
        /** Estensione (<code>multi-value</code>) del formato di output del documento */
        private final String mFileExt;

        /**
         * Costruttore.
         * 
         * @param pContentType
         *            <code>content-type</code> associato al formato di output del documento
         * @param pDescription
         *            Descrizione del formato di output del documento
         */
        CONTENT_TYPE(final String pContentType, final String pDescription, final String pFileExt) {
            mContentType = pContentType;
            mDescription = pDescription;
            mFileExt = pFileExt;
        }

        /**
         * Ritorna il <code>content-type</code> associato al formato di output del documento.
         * 
         * @return <code>content-type</code> associato al formato di output del documento.
         */
        public String getContentType() {
            return mContentType;
        }

        /**
         * Ritorna la descrizione del formato di output del documento.
         * 
         * @return Descrizione del formato di output del documento.
         */
        public String getDescription() {
            return mDescription;
        }

        /**
         * Ritorna l'estensione (<code>multi-value</code>) del formato di output del documento.
         * 
         * @return Estensione (<code>multi-value</code>) del formato di output del documento
         */
        public String getFileExt() {
            return mFileExt;
        }

        /**
         * Ritorna la definizione delle tipologia di output identificata dal parametro fornito.
         * 
         * @param pFileExt
         *            path file esterno
         * 
         * @return efinizione delle tipologia di output identificata dal parametro fornito
         */
        public static CONTENT_TYPE getFromFileExt(final String pFileExt) {
            CONTENT_TYPE lContentType = null;

            for (CONTENT_TYPE lTmpContentType : CONTENT_TYPE.values()) {
                String lFileExt = lTmpContentType.getFileExt();
                String[] lAvailableExt = lFileExt.split(",");

                for (String lExt : lAvailableExt) {
                    if (lExt.equalsIgnoreCase(pFileExt)) {
                        lContentType = lTmpContentType;

                        break;
                    }
                }
            }

            if (lContentType == null) {
                throw new RuntimeException(String.format("[%s] file extention not supported", pFileExt));
            }

            return lContentType;
        }
    }

    /**
     * Costruttore.
     * 
     */
    private IOUtils() {
    }

    /**
     * Indica se un file esiste.
     * 
     * @param pFile
     *            File per cui eseguire l'operazione
     * 
     * @return True se il file esiste, false in caso contrario.
     */
    public static boolean exists(final String pFile) {
        File lFile = getFile(pFile);
        boolean lExists = lFile.exists();

        return lExists;
    }

    /**
     * Ritorna il nome del file fornito, senza estensione.
     * 
     * @param pFileName
     *            Nome del file per cui eseguire l'operazione
     * 
     * @return Nome del file fornito, senza estensione.
     */
    public static String extractBaseFileName(final String pFileName) {
        String lBaseFileName = pFileName;

        if (lBaseFileName != null) {
            String lFileExt = extractFileExtension(pFileName, true);

            if (lFileExt != null) {
                lBaseFileName = lBaseFileName.replace(lFileExt, "");
            }

            int lUnixSep = lBaseFileName.lastIndexOf(UNIX_FILE_SEP);
            int lWindowsSep = lBaseFileName.lastIndexOf(WINDOWS_FILE_SEP);
            int lSep = Math.max(lUnixSep, lWindowsSep);

            if (lSep != -1) {
                lBaseFileName = lBaseFileName.substring(++lSep);
            }
        }

        return lBaseFileName;
    }

    // EVO#20792
    /**
     * Ritorna il nome relativo dall'urn fornito.
     * 
     * @param pUrn
     *            Urn per cui eseguire l'operazione
     * 
     * @return Nome relativo dall'urn fornito.
     */
    public static String extractPartUrnName(final String pUrn) {
        return extractPartUrnName(pUrn, false);
    }

    /**
     * Ritorna il nome relativo dall'urn fornito.
     * 
     * @param pUrn
     *            Urn per cui eseguire l'operazione
     * @param pNormalize
     *            True se è richiesta la normalizzazione, false in caso contrario
     * 
     * @return Nome relativo dall'urn fornito.
     */
    public static String extractPartUrnName(final String pUrn, final boolean pNormalize) {
        String lBaseUrnName = pUrn;

        if (lBaseUrnName != null) {
            Pattern p = Pattern.compile("[:-]+([a-zA-Z]+[-[A-Z]+]*[:-[0-9]+]*[.[0-9]+]*)$");
            Matcher m = p.matcher(lBaseUrnName);
            if (m.find()) {
                lBaseUrnName = m.group(1);

                if (pNormalize) {
                    lBaseUrnName = MessaggiWSFormat.normalizingKey(lBaseUrnName);
                }
            }
        }

        return lBaseUrnName;
    }
    // EVO#20792

    /**
     * Ritorna l'estensione del file fornito.
     * 
     * @param pFileName
     *            Nome del file per cui eseguire l'operazione
     * 
     * @return Estensione del file fornito.
     */
    public static String extractFileExtension(final String pFileName) {
        return extractFileExtension(pFileName, false);
    }

    /**
     * Ritorna l'estensione del file fornito.
     * 
     * @param pFileName
     *            Nome del file per cui eseguire l'operazione
     * @param pWithDot
     *            True se è richiesta l'estensione con il separatore, false in caso contrario
     * 
     * @return Estensione del file fornito.
     */
    public static String extractFileExtension(final String pFileName, final Boolean pWithDot) {
        String lFileExt = null;

        if (pFileName != null) {
            int lIdx = pFileName.lastIndexOf(FILE_EXT_SEP);

            if (lIdx != -1) {
                if (!pWithDot) {
                    lIdx++;
                }

                lFileExt = pFileName.substring(lIdx);
            }
        }

        return lFileExt;
    }

    /**
     * Ritorna il percorso contenuto nel nome dei file fornito.
     * 
     * @param pFilename
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Percorso contenuto nel nome dei file fornito.
     */
    public static String extractPath(final String pFilename) {
        File lFile = getFile(pFilename);

        return extractPath(lFile);
    }

    /**
     * Ritorna il percorso che contiene il file fornito.
     * 
     * @param pFile
     *            Istanza <code>File</code> per cui eseguire l'operazione
     * 
     * @return Percorso che contiene il file fornito
     */
    public static String extractPath(final File pFile) {
        String lPath = pFile.getParentFile().getAbsolutePath();

        return lPath;
    }

    /**
     * Ritorna il percorso assoluto per i parametri forniti.
     * 
     * @param pFilePath
     *            Percorso del file con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Percorso assoluto per i parametri forniti.
     */
    public static String getAbsolutePath(final String pFilePath, final String pFileName) {
        return getAbsolutePath(pFilePath, pFileName, FILE_SEPARATOR);
    }

    /**
     * Ritorna il percorso assoluto per i parametri forniti.
     * 
     * @param pFilePath
     *            Percorso del file con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * @param pFileSeparator
     *            Separatore del file con cui eseguire l'operazione
     * 
     * @return Percorso assoluto per i parametri forniti.
     */
    public static String getAbsolutePath(final String pFilePath, final String pFileName, final String pFileSeparator) {
        String lPath;

        if (pFilePath.endsWith(pFileSeparator)) {
            lPath = pFilePath.concat(pFileName);
        } else {
            lPath = pFilePath.concat(pFileSeparator).concat(pFileName);
        }

        return lPath;
    }

    /**
     * Indica se il percorso fornito è relativo.
     * 
     * @param pFilePath
     *            Percorso per cui eseguire l'operazione
     * 
     * @return True se il percorso fornito è relativo, false in caso contrario.
     */
    public static boolean isRelativePath(final String pFilePath) {
        boolean lIsRelative = (pFilePath.startsWith(CURRENT_DIR) || pFilePath.startsWith(PARENT_DIR));

        return lIsRelative;
    }

    /**
     * Ritorna il nome del file ottenuto dalla concatenazione dei parametri forniti.
     * 
     * @param pFileName
     *            Nome del file
     * @param pFileExt
     *            Estensione del file
     * 
     * @return Nome del file ottenuto dalla concatenazione dei parametri forniti.
     */
    public static String getFilename(final String pFileName, final String pFileExt) {
        String lFormat = pFileExt.startsWith(".") ? "%s%s" : "%s.%s";

        return String.format(lFormat, pFileName, pFileExt);
    }

    /**
     * Ritorna il nome del file (<code>last-name</code>) contenuto nel parametro fornito.
     * 
     * @param pAbsoluteFileName
     *            Nome del file (<code>abstlute-file-path</code>) per cui eseguire l'operazione
     * 
     * @return Nome del file (<code>last-name</code>) contenuto nel parametro fornito.
     */
    public static String getFilename(final String pAbsoluteFileName) {
        // TODO: DiLorenzo_F, usare getFile
        File lFile = new File(pAbsoluteFileName);

        return lFile.getName();
    }

    /**
     * Ritorna l'istanza <code>File</code> per la risorsa identificata dal parametro fornito.
     * <p>
     * Se il nome del file non è fornito nel formato <code>absolute-path</code>, il file viene cercato nella directory
     * corrente.
     * 
     * @see <code>CURRENT_DIR</code>
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return <code>File</code> per la risorsa identificata dal parametro fornito.
     */
    public static File getFile(final String pFile) {
        String lDir = CURRENT_DIR;
        String lName = pFile;

        if (pFile.charAt(0) == UNIX_FILE_SEP || pFile.charAt(0) == WINDOWS_FILE_SEP) {
            int lUnixSep = lName.lastIndexOf(UNIX_FILE_SEP);
            int lWindowsSep = lName.lastIndexOf(WINDOWS_FILE_SEP);
            int lLastFileSep = Math.max(lUnixSep, lWindowsSep);

            lDir = pFile.substring(0, lLastFileSep);
            lName = lName.substring(lLastFileSep);
        }

        // TIP: DiLorenzo_F, path in formato WINDOWS
        if (pFile.charAt(1) == ':') {
            lDir = null;
        }

        File lFile = getFile(lDir, lName);

        return lFile;
    }

    /**
     * Ritorna l'istanza <code>File</code> per la risorsa identificata dai parametri forniti.
     * 
     * @param pFilePath
     *            Percorso con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return <code>File</code> per la risorsa identificata dai parametri forniti.
     */
    public static File getFile(final String pFilePath, final String pFileName) {
        File lFile = new File(pFilePath, pFileName);

        return lFile;
    }

    /**
     * Ritorna l'istanza <code>FileInputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @param pFile
     *            Istanza <code>File</code> con cui eseguire l'operazione
     * 
     * @return <code>FileInputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @throws FileNotFoundException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static FileInputStream getFileInputStream(final File pFile) throws FileNotFoundException {
        FileInputStream lFileInputStream = new FileInputStream(pFile);

        return lFileInputStream;
    }

    /**
     * Ritorna l'istanza <code>FileInputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return <code>FileInputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @throws FileNotFoundException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static FileInputStream getFileInputStream(final String pFile) throws FileNotFoundException {
        FileInputStream lFileInputStream = new FileInputStream(pFile);

        return lFileInputStream;
    }

    /**
     * Ritorna l'istanza <code>FileInputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @param pFilePath
     *            Percorso con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Istanza <code>FileInputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @throws FileNotFoundException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static FileInputStream getFileInputStream(final String pFilePath, final String pFileName)
            throws FileNotFoundException {
        String lFile = getAbsolutePath(pFilePath, pFileName);

        return getFileInputStream(lFile);
    }

    /**
     * Ritorna l'istanza <code>FileOutputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @param pFile
     *            Istanza <code>File</code> con cui eseguire l'operazione
     * 
     * @return <code>FileOutputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @throws FileNotFoundException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static FileOutputStream getFileOutputStream(final File pFile) throws FileNotFoundException {
        FileOutputStream lFileOutputStream = new FileOutputStream(pFile);

        return lFileOutputStream;
    }

    /**
     * Ritorna l'istanza <code>FileOutputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return <code>FileOutputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @throws FileNotFoundException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static FileOutputStream getFileOutputStream(final String pFile) throws FileNotFoundException {
        FileOutputStream lFileOutputStream = new FileOutputStream(pFile);

        return lFileOutputStream;
    }

    /**
     * Ritorna l'istanza <code>FileOutputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @param pFilePath
     *            Percorso con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Istanza <code>FileOutputStream</code> per la risorsa identificata dal parametro fornito.
     * 
     * @throws FileNotFoundException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static FileOutputStream getFileOutputStream(final String pFilePath, final String pFileName)
            throws FileNotFoundException {
        String lFile = getAbsolutePath(pFilePath, pFileName);

        return getFileOutputStream(lFile);
    }

    /**
     * Calcola e ritorna il percorso ottenuto come concatenazione dei parametri forniti.
     * 
     * @param pRoot
     *            Percorso <code>root</code> con cui eseguire l'operazione
     * @param pPath
     *            Percorso con cui eseguire l'operazione
     * 
     * @return Percorso ottenuto come concatenazione dei parametri forniti.
     */
    public static String getPath(final String pRoot, final String pPath) {
        return getPath(pRoot, pPath, FILE_SEPARATOR);
    }

    /**
     * Calcola e ritorna il percorso ottenuto come concatenazione dei parametri forniti.
     * 
     * @param pRoot
     *            Percorso <code>root</code> con cui eseguire l'operazione
     * @param pPath
     *            Percorso con cui eseguire l'operazione
     * @param pFileSeparator
     *            Separatore con cui eseguire l'operazione
     * 
     * @return Percorso ottenuto come concatenazione dei parametri forniti.
     */
    public static String getPath(final String pRoot, final String pPath, final String pFileSeparator) {
        String lRoot = (pRoot.endsWith(pFileSeparator)) ? pRoot : pRoot.concat(pFileSeparator);
        String lPath = (pPath.endsWith(pFileSeparator)) ? pPath.substring(0, pPath.length() - pFileSeparator.length())
                : pPath;

        return lRoot.concat(lPath);
    }

    /**
     * Ritorna il nome della cartella temporanea.
     * 
     * @return Nome della cartella temporanea.
     */
    public static String getTempDirName() {
        String lTempDir = System.getProperty("java.io.tmpdir");

        return lTempDir;
    }

    /**
     * Crea e ritorna l'istanza <code>File</code> per i parametri forniti.
     * 
     * @param pFilePath
     *            Percorso con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return <code>File</code> per la risorsa identificata dai parametri forniti.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static File newFile(final String pFilePath, final String pFileName) throws IOException {
        return newFile(pFilePath, pFileName, false);
    }

    /**
     * Crea e ritorna l'istanza <code>File</code> per i parametri forniti.
     * 
     * @param pFilePath
     *            Percorso con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * @param pMkDir
     *            True se richiesto di creare eventualmente la directory, false in caso contrario
     * 
     * @return <code>File</code> per la risorsa identificata dai parametri forniti.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static File newFile(final String pFilePath, final String pFileName, final boolean pMkDir)
            throws IOException {
        if (pMkDir) {
            newDirectory(pFilePath);
        }

        File lFile = getFile(pFilePath, pFileName);

        if (!lFile.createNewFile()) {
            throw new RuntimeException(String.format("Creating [%s] new file", lFile.getAbsolutePath()));
        }

        return lFile;
    }

    /**
     * Ritorna l'istanza <code>File</code> contenente gli estremi della directory temporanea identificata dal parametro
     * fornito.
     * 
     * @param pDirName
     *            Nome della directory con cui eseguire l'operazione
     * 
     * @return <code>File</code> contenente gli estremi della directory temporanea identificata dal parametro fornito.
     */
    public static File newTempDirectory(final String pDirName) {
        String lTempDir = getTempDirName();
        String lDirName = pDirName;

        if (!lTempDir.endsWith(FILE_SEPARATOR)) {
            lTempDir = lTempDir.concat(FILE_SEPARATOR);
        }

        if (lDirName.startsWith(FILE_SEPARATOR)) {
            lDirName = lDirName.substring(1);
        }

        lTempDir = lTempDir.concat(lDirName);

        return newDirectory(lTempDir);
    }

    /**
     * Ritorna l'istanza <code>File</code> contenente gli estremi della directory identificata dal parametro fornito.
     * 
     * @param pDirName
     *            Nome della directory con cui eseguire l'operazione
     * 
     * @return <code>File</code> contenente gli estremi della directory identificata dal parametro fornito.
     */
    public static File newDirectory(final String pDirName) {
        File lDir = new File(pDirName);

        if (!lDir.exists() && !lDir.mkdirs()) {
            throw new RuntimeException(String.format("Creating [%s] new directory", lDir.getAbsolutePath()));
        }

        return lDir;
    }

    /**
     * Ritorna il nome del file temporaneo.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Nome del file temporaneo.
     */
    public static String getTempFilename(final String pFile) {
        String lTempFile = getTempDirName();

        if (!lTempFile.endsWith(FILE_SEPARATOR)) {
            lTempFile = lTempFile.concat(FILE_SEPARATOR);
        }

        lTempFile = lTempFile.concat(pFile);

        return lTempFile;
    }

    /**
     * Ritorna l'istanza <code>File</code> per la risorsa temporanea identificata dal parametro fornito.
     * 
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return <code>File</code> per la risorsa temporanea identificata dal parametro fornito.
     */
    public static File getTempFile(final String pFileName) {
        String lFilePath = getTempDirName();

        return new File(lFilePath, pFileName);
    }

    /**
     * Indica se un file presente e disponibile ad essere letto/scritto.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return True se un file è presente e disponibile ad essere letto/scritto, false in caso contrario.
     */
    public static boolean isFileReady(final String pFile) {
        boolean isReady = false;

        File file = new File(pFile);

        if (file.exists() && file.canWrite() && file.canRead()) {
            isReady = true;
        }

        return isReady;
    }

    /**
     * Ritorna la lista dei file (<code>absolute-path</code>) cancellati, filtrati dai criteri forniti.
     * 
     * @param pFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pFileFilter
     *            Istanza <code>FileFilter</code> con cui eseguire l'operazione
     * @param pDeeply
     *            True se eseguire l'operazione in modo ricorsivo sulle cartelle eventualmente presenti, false in caso
     *            contrario
     * 
     * @return Lista dei file (<code>absolute-path</code>) cancellati, filtrati dai criteri forniti.
     */
    public static List<String> delete(final File pFilePath, final FileFilter pFileFilter, final boolean pDeeply) {
        return delete(pFilePath, pFileFilter, pDeeply, true);
    }

    /**
     * Ritorna la lista contenente i nomi dei file (<code>absolute-path</code>) cancellati, filtrati dai criteri
     * forniti.
     * 
     * @param pFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pFileFilter
     *            Istanza <code>FileFilter</code> con cui eseguire l'operazione
     * @param pDeeply
     *            True se eseguire l'operazione in modo ricorsivo sulle cartelle eventualmente presenti, false in caso
     *            contrario
     * @param pSmooth
     *            True se NON richiesto di sollevare una eccezione se la classe non viene trovata, false in caso
     *            contrario
     * 
     * @return Lista contenente i nomi dei file (<code>absolute-path</code>) cancellati, filtrati dai criteri forniti.
     */
    public static List<String> delete(final File pFilePath, final FileFilter pFileFilter, final boolean pDeeply,
            final boolean pSmooth) {
        List<File> lFilesToDelete = new ArrayList<File>();
        lFilesToDelete = recursivelyList(pFilePath, pFileFilter, pDeeply, lFilesToDelete);
        List<String> lDeletedWithSuccess = new ArrayList<String>();

        for (File lFile : lFilesToDelete) {
            if (delete(lFile, pSmooth)) {
                lDeletedWithSuccess.add(lFile.getAbsolutePath());
            }
        }

        return lDeletedWithSuccess;
    }

    /**
     * Cancella il file.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return True se l'operzione è stata eseguita correttamente, false in caso contrario
     * 
     */
    public static boolean delete(final String pFile) {
        return delete(pFile, false);
    }

    /**
     * Cancella il file.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * @param pSmooth
     *            True se NON è richiesto di sollevare una eccezione se la classe non viene trovata, false in caso
     *            contrario
     * 
     * @return True se l'operzione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean delete(final String pFile, final boolean pSmooth) {
        return delete(new File(pFile), pSmooth);
    }

    /**
     * Cancella il file.
     * 
     * @param pFile
     *            <code>File</code> per cui eseguire l'operazione
     * 
     * @return True se l'operzione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean delete(final File pFile) {
        return delete(pFile, false);
    }

    /**
     * Cancella il file.
     * 
     * @param pFile
     *            <code>File</code> per cui eseguire l'operazione
     * @param pSmooth
     *            True se NON è richiesto di sollevare una eccezione se la classe non viene trovata, false in caso
     *            contrario
     * 
     * @return True se l'operzione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean delete(final File pFile, final boolean pSmooth) {
        boolean lDeleted = false;
        boolean lIsTempDir = false;
        String lMessage = null;
        boolean lIsDirectory = pFile.isDirectory();
        String lFileRef = (lIsDirectory) ? "directory" : "file";

        if (pFile.exists()) {
            if (lIsDirectory) {
                String lTempDirName = getTempDirName();
                String lFilePath = pFile.getAbsolutePath()
                        .concat((lTempDirName.endsWith(FILE_SEPARATOR) ? FILE_SEPARATOR : ""));
                lIsTempDir = lTempDirName.equals(lFilePath);

                if (lIsTempDir) {
                    lMessage = String.format("Can't delete [%s] tempory directory", lTempDirName);
                    LOG.warn(lMessage);
                }
            }

            if (!lIsTempDir) {
                lDeleted = pFile.delete();

                if (lDeleted) {
                    LOG.debug(String.format("[%s] %s deleted", pFile, lFileRef));
                } else {
                    lMessage = String.format("[%s] %s was not deleted", pFile, lFileRef);
                    LOG.warn(lMessage);
                }

                if (!lDeleted && !pSmooth) {
                    throw new RuntimeException(lMessage);
                }
            }
        } else {
            lMessage = String.format("[%s] %s does not exist", pFile.getAbsolutePath(), lFileRef);
            LOG.warn(lMessage);

            if (!pSmooth) {
                throw new RuntimeException(lMessage);
            }
        }

        return lDeleted;
    }

    /**
     * Cancella la directory identificata dal parametro fornito.
     * 
     * @param pDirName
     *            Nome della directory per cui eseguire l'operazione
     * 
     * @return True se l'operzione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean deleteDir(final String pDirName) {
        return deleteDir(pDirName, false, false);
    }

    /**
     * Cancella la directory identificata dal parametro fornito.
     * 
     * @param pDirName
     *            Nome della directory per cui eseguire l'operazione
     * @param pDeleteContent
     *            True se è richiesta anche la cancellazione dei file eventualmente contenuti, false in caso contrario
     * 
     * @return True se l'operzione stata eseguita correttamente, false in caso contrario
     */
    public static boolean deleteDir(final String pDirName, final boolean pDeleteContent) {
        return deleteDir(pDirName, pDeleteContent, false);
    }

    /**
     * Cancella la directory identificata dal parametro fornito.
     * 
     * @param pDirName
     *            Nome della directory per cui eseguire l'operazione
     * @param pDeleteContent
     *            True se è richiesta anche la cancellazione dei file eventualmente contenuti, false in caso contrario
     * @param pSmooth
     *            True se NON è richiesto di sollevare una eccezione se la classe non viene trovata, false in caso
     *            contrario
     * 
     * @return True se l'operazione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean deleteDir(final String pDirName, final boolean pDeleteContent, final boolean pSmooth) {
        File lDirFile = new File(pDirName);
        boolean lDeleted = true;

        if (pDeleteContent) {
            List<String> lFiles = list(lDirFile, AllFileFilter.getInstance(), false);

            for (String lFile : lFiles) {
                File lEntry = new File(lFile);
                if (lEntry.isDirectory()) {
                    lDeleted = (lDeleted) ? deleteDir(lFile, pDeleteContent, pSmooth) : lDeleted;
                } else {
                    lDeleted = (lDeleted) ? delete(lFile, pSmooth) : lDeleted;
                }
            }
        }

        if (lDeleted) {
            lDeleted = delete(lDirFile, pSmooth);
        }

        return lDeleted;
    }

    /**
     * Ritorna la lista dei file (<code>absolute-path</code>) filtrati dai criteri forniti.
     * 
     * @param pFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pFileFilter
     *            Istanza <code>FileFilter</code> con cui eseguire l'operazione
     * @param pDeeply
     *            True se eseguire l'operazione in modo ricorsivo sulle cartelle eventualmente presenti, false in caso
     *            contrario
     * 
     * @return Lista dei file (<code>absolute-path</code>) filtrati dai criteri forniti.
     */
    public static List<String> list(final String pFilePath, final FileFilter pFileFilter, final boolean pDeeply) {
        File lFilePath = getFile(pFilePath);

        return list(lFilePath, pFileFilter, pDeeply);
    }

    /**
     * Ritorna la lista dei file (<code>absolute-path</code>) filtrati dai criteri forniti.
     * 
     * @param pFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pFileFilter
     *            Istanza <code>FileFilter</code> con cui eseguire l'operazione
     * @param pDeeply
     *            True se eseguire l'operazione in modo ricorsivo sulle cartelle eventualmente presenti, false in caso
     *            contrario
     * 
     * @return Lista dei file (<code>absolute-path</code>) filtrati dai criteri forniti.
     */
    public static List<String> list(final File pFilePath, final FileFilter pFileFilter, final boolean pDeeply) {
        List<File> lFileList = new ArrayList<>();
        lFileList = recursivelyList(pFilePath, pFileFilter, pDeeply, lFileList);
        List<String> lFileNames = new ArrayList<>();

        for (File file : lFileList) {
            lFileNames.add(file.getAbsolutePath());
        }

        return lFileNames;
    }

    /**
     * Ritorna la lista dei file (<code>absolute-path</code>) filtrati dai criteri forniti.
     * 
     * @param pFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pFileFilter
     *            Istanza <code>FileFilter</code> con cui eseguire l'operazione
     * @param pDeeply
     *            True se eseguire l'operazione in modo ricorsivo sulle cartelle eventualmente presenti, false in caso
     *            contrario
     * @param pFileList
     *            Lista in cui accodare gli estremi dei file filtrati
     * 
     * @return Lista dei file (<code>absolute-path</code>) filtrati dai criteri forniti.
     */
    private static List<File> recursivelyList(final File pFilePath, final FileFilter pFileFilter, final boolean pDeeply,
            final List<File> pFileList) {
        if (pFilePath.isDirectory()) {
            if (pDeeply) {
                for (File child : pFilePath.listFiles()) {
                    recursivelyList(child, pFileFilter, pDeeply, pFileList);
                }
            } else {
                for (File child : pFilePath.listFiles(pFileFilter)) {
                    pFileList.add(child);
                }
            }
        } else if (pFileFilter.accept(pFilePath)) {
            pFileList.add(pFilePath);
        }

        return pFileList;
    }

    /**
     * Ritorna il contenuto di un file.
     * 
     * @param pInputStream
     *            Istanza <code>InputStream</code> con cui eseguire l'operazione
     * 
     * @return Contenuto di un file.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static byte[] readAsBytes(final InputStream pInputStream) throws IOException {
        byte[] lBuffer;

        lBuffer = new byte[pInputStream.available()];
        pInputStream.read(lBuffer);
        pInputStream.close();

        return lBuffer;
    }

    /**
     * Ritorna il contenuto di un file.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Contenuto di un file.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static byte[] readFileAsBytes(final String pFile) throws IOException {
        FileInputStream lFileInputStream = new FileInputStream(pFile);

        return readAsBytes(lFileInputStream);
    }

    /**
     * Ritorna il contenuto di un file.
     * 
     * @param pFilePath
     *            Percorso del file con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return Contenuto di un file.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static byte[] readFileAsBytes(final String pFilePath, final String pFileName) throws IOException {
        String lFile = getAbsolutePath(pFilePath, pFileName);

        return readFileAsBytes(lFile);
    }

    /**
     * Scrive il contenuto su un file.
     * 
     * @param pFilePath
     *            Percorso del file con cui eseguire l'operazione
     * @param pFileName
     *            Nome del file con cui eseguire l'operazione
     * @param pBuffer
     *            Array di <code>byte</code> con cui eseguire l'operazione
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static void writeAsByteArray(final String pFilePath, final String pFileName, final byte[] pBuffer)
            throws IOException {
        String lFile = getAbsolutePath(pFilePath, pFileName);

        writeAsByteArray(lFile, pBuffer);
    }

    /**
     * Scrive il contenuto su un file.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * @param pBuffer
     *            Array di <code>byte</code> con cui eseguire l'operazione
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static void writeAsByteArray(final String pFile, final byte[] pBuffer) throws IOException {
        File lFile = getFile(pFile);

        writeAsByteArray(lFile, pBuffer);
    }

    /**
     * Scrive il contenuto su un file
     * 
     * @param pFile
     *            <code>File</code> con cui eseguire l'operazione
     * @param pBuffer
     *            Array di <code>byte</code> con cui eseguire l'operazione
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static void writeAsByteArray(final File pFile, final byte[] pBuffer) throws IOException {
        FileOutputStream lFileOutputStream;

        lFileOutputStream = getFileOutputStream(pFile);
        lFileOutputStream.write(pBuffer);
        lFileOutputStream.close();
    }

    /**
     * Scrive il contenuto su un file.
     * 
     * @param pFile
     *            Nome del file con cui eseguire l'operazione
     * @param pContent
     *            Contenuto con cui eseguire l'operazione
     * @param pAppend
     *            True se è richiesto di andare in append al file eventualmente gi presente, false in caso contrario
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static void writeFile(final String pFile, final String pContent, final boolean pAppend) throws IOException {
        File lFile = new File(pFile);

        if (!lFile.exists()) {
            lFile.createNewFile();
        }

        FileWriter lFileWriter = new FileWriter(lFile.getAbsoluteFile(), pAppend);
        BufferedWriter lBufferedWriter = new BufferedWriter(lFileWriter);
        lBufferedWriter.write(pContent);
        lBufferedWriter.close();
    }

    /**
     * Ritorna l'array di byte valorizzato con il contenuto della istanza <code>InputStream</code>.
     * 
     * @param pInputStream
     *            <code>InputStream</code> per cui eseguire l'operazione
     * 
     * @return Array di byte valorizzato con il contenuto della istanza <code>InputStream</code>.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static byte[] toByteArray(final InputStream pInputStream) throws IOException {
        ByteArrayOutputStream lOutputStream = new ByteArrayOutputStream();
        copy(pInputStream, lOutputStream);

        return lOutputStream.toByteArray();
    }

    /**
     * Copia il contenuto del file identificato dai parametri forniti.
     * 
     * @param pSourceFileCompletePath
     *            Nome del file con cui eseguire l'operazione
     * @param pDestFileCompletePath
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return True se l'operazione stata eseguita correttamente, false in caso contrario.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static boolean copyFile(final String pSourceFileCompletePath, final String pDestFileCompletePath)
            throws IOException {
        File lSource = getFile(pSourceFileCompletePath);
        File lDest = getFile(pDestFileCompletePath);

        return copyFile(lSource, lDest);
    }

    /**
     * Copia il contenuto del file identificato dai parametri forniti.
     * 
     * @param pSourceFile
     *            Istanza <code>File</code> con cui eseguire l'operazione
     * @param pDestFile
     *            Istanza <code>File</code> con cui eseguire l'operazione
     * 
     * @return True se l'operazione stata eseguita correttamente, false in caso contrario.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static boolean copyFile(final File pSourceFile, final File pDestFile) throws IOException {
        InputStream lInputStream = getFileInputStream(pSourceFile);
        OutputStream lOutputStream = getFileOutputStream(pDestFile);

        int lCount = copy(lInputStream, lOutputStream);
        boolean lIsCopyOk = (lCount > 0) ? true : false;

        lInputStream.close();
        lOutputStream.close();

        return lIsCopyOk;
    }

    /**
     * Copia il contenuto del file identificato dai parametri forniti.
     * 
     * @param pSourceFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pSourceFileName
     *            Nome del file per cui eseguire l'operazione
     * @param pDestFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pDestFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return True se l'operazione è stata eseguita correttamente, false in caso contrario.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static boolean copyFile(final String pSourceFilePath, final String pSourceFileName,
            final String pDestFilePath, final String pDestFileName) throws IOException {
        File lSource = getFile(pSourceFilePath, pSourceFileName);
        InputStream lInputStream = getFileInputStream(lSource);

        File lDest = getFile(pDestFilePath, pDestFileName);
        OutputStream lOutputStream = getFileOutputStream(lDest);

        int lCount = copy(lInputStream, lOutputStream);
        boolean lIsCopyOk = (lCount > 0) ? true : false;

        lInputStream.close();
        lOutputStream.close();

        return lIsCopyOk;
    }

    /**
     * Copia il contenuto del file identificato dai parametri forniti.
     * 
     * @param pFilePath
     *            Percorso in cui eseguire l'operazione
     * @param pSourceFileName
     *            Nome del file per cui eseguire l'operazione
     * @param pDestFileName
     *            Nome del file con cui eseguire l'operazione
     * 
     * @return True se l'operazione stata eseguita correttamente, false in caso contrario.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static boolean copyFile(final String pFilePath, final String pSourceFileName, final String pDestFileName)
            throws IOException {
        return copyFile(pFilePath, pSourceFileName, pFilePath, pDestFileName);
    }

    /**
     * Copia il contenuto della istanza <code>InputStream</code>.
     * 
     * @param pInputStream
     *            <code>InputStream</code> per cui eseguire l'operazione
     * @param pOutputStream
     *            <code>OutputStream</code> con cui eseguire l'operazione
     * 
     * @return Numero di byte copiati.
     * 
     * @throws IOException
     *             Eccezione sollevata dalle primitive utilizzate.
     */
    public static int copy(final InputStream pInputStream, final OutputStream pOutputStream) throws IOException {
        byte[] lBuffer = new byte[DEFAULT_BUFFER_SIZE];
        int lCount = 0;
        int lBufferSize;

        while (-1 != (lBufferSize = pInputStream.read(lBuffer))) {
            pOutputStream.write(lBuffer, 0, lBufferSize);
            lCount += lBufferSize;
        }

        return lCount;
    }

    /**
     * Crea le directory per il parametro fornito.
     * 
     * @param pFile
     *            Istanza <code>File</code> con cui eseguire l'operazione
     */
    public static void mkDirs(final File pFile) {
        if (pFile != null && !pFile.exists() && !pFile.mkdirs()) {
            throw new RuntimeException(String.format("Creating [%s] directory", pFile.getAbsolutePath()));
        }
    }

    /**
     * Crea le directory per il parametro fornito.
     * 
     * @param pFilePath
     *            Percorso con cui eseguire l'operazione
     */
    public static void mkDirs(final String pFilePath) {
        File lFile = new File(pFilePath);

        mkDirs(lFile);
    }

    /**
     * Indica se una directory è vuota.
     * 
     * @param pFilePath
     *            Istanza <code>File</code> per cui eseguire l'operazione
     * 
     * @return True se una directory è vuota, false in caso contrario
     */
    public static boolean isEmpty(final File pFilePath) {
        if (!pFilePath.isDirectory()) {
            throw new RuntimeException(String.format("[%s] file is not a directory", pFilePath.getAbsolutePath()));
        }

        File[] lFiles = pFilePath.listFiles(AllFileFilter.getInstance());

        return lFiles.length == 0;
    }

    /**
     * Rinomina il file presente nella directory, accodando il timestamp al nome del file.
     * <p>
     * Il formato del timestamp utilizzato <code>yyyy-MM-dd_HHmmss</code>.
     * 
     * @param pFilePath
     *            Percorso contenente il file per cui eseguire l'operazione
     * @param pFileName
     *            Nome del file per cui eseguire l'operazione
     * 
     * @return True se l'operazione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean renameWithTimestamp(final String pFilePath, final String pFileName) {
        File lFile = getFile(pFilePath, pFileName);
        String lFileName = lFile.getName();
        boolean lSuccess = true;

        if (lFile.exists()) {
            Date lDate = new Date();

            Calendar lCal = Calendar.getInstance();
            lCal.setTime(lDate);

            SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            String lStrDate = lSdf.format(lCal.getTime());

            String lFileNameWithoutExt = extractBaseFileName(lFileName);
            String lExtension = extractFileExtension(lFileName);
            String lNewFileName = String.format("%s_%s.%s", lFileNameWithoutExt, lStrDate, lExtension);
            File lNewFile = IOUtils.getFile(pFilePath, lNewFileName);

            lSuccess = lFile.renameTo(lNewFile);
        } else {
            lSuccess = true;
        }

        return lSuccess;
    }

    /**
     * Rinomina la directory, accodando il timestamp al nome della directory.
     * <p>
     * Il formato del timestamp utilizzato <code>dd-MM-yyyy_HHmmss</code>.
     * 
     * @param pDirPath
     *            Percorso contenente la directory per cui eseguire l'operazione
     * 
     * @return True se l'operazione è stata eseguita correttamente, false in caso contrario
     */
    public static boolean renameDirWithTimestamp(final String pDirPath) {
        String lParentDir = IOUtils.extractPath(pDirPath);
        File lFile = getFile(pDirPath);
        String lDirName = lFile.getName();
        boolean lSuccess = true;

        if (lFile.exists()) {
            Date lDate = new Date();

            Calendar lCal = Calendar.getInstance();
            lCal.setTime(lDate);

            SimpleDateFormat lSdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            String lStrDate = lSdf.format(lCal.getTime());

            String lNewDirName = String.format("%s_%s", lDirName, lStrDate);
            File lNewDir = IOUtils.getFile(lParentDir, lNewDirName);

            lSuccess = lFile.renameTo(lNewDir);
        } else {
            lSuccess = true;
        }

        return lSuccess;
    }

    /**
     * Ritorna il percorso, dopo aver accodato un progressivo al nome della directory.
     * <p>
     * 
     * @param pDirPath
     *            Percorso contenente la directory per cui eseguire l'operazione
     * @param pMapDirPrg
     *            Istanza <code>Map</code> con cui eseguire l'operazione
     * 
     * @return Percorso con cui eseguire l'operazione
     */
    public static String getDirWithProgressive(final String pDirPath, final Map<String, Integer> pMapDirPrg) {
        String lParentDir = IOUtils.extractPath(pDirPath);
        File lFile = getFile(pDirPath);
        String lDirName = lFile.getName();

        if (lFile.exists() && pMapDirPrg.containsKey(lDirName)) {
            String lNewDirName = String.format("%s_%s", lDirName, pMapDirPrg.get(lDirName));

            return IOUtils.getPath(lParentDir, lNewDirName);
        } else {
            return pDirPath;
        }
    }
}
