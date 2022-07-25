/*
 Attenzione
 Il codice di questo modulo software è legato:
 * al DBMS Oracle
 * al driver JDBC proprietario di Oracle (compatibile JDBC 4)
 * alla gestione propria di Oracle delle colonne di tipo BLOB

 Questo codice non è direttamente usabile su altre architetture di database e presumibilmente
 dovrà essere riscritto in parte o del tutto per gestire la modalità di gestione delle colonne
 di tipo BLOB impiegata da un eventuale altro DBMS.

 La scelta di utilizzare una modalità di accesso non portabile per scrivere su tabella nasce
 dall'esigenza di dover leggere e scrivere colonne di tipo BLOB potenzialmente enormi e
 dall'incapacità dell'architettura JPA di effettuare queste operazioni tramite stream.
 */
package it.eng.parer.ws.recupero.ejb.oracleBlb;

import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "RecBlbOracle")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecBlbOracle {

    private static final Logger log = LoggerFactory.getLogger(RecBlbOracle.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    //
    private final static String QRY_CNT_ARO_CONTENUTO_COMP = "SELECT count(*) FROM ARO_CONTENUTO_COMP t "
            + "where t.id_comp_strut_doc = ?";
    private final static String QRY_CNT_VRS_CONTENUTO_FILE = "SELECT count(*) FROM VRS_CONTENUTO_FILE t "
            + "where t.ID_FILE_SESSIONE = ?";
    //
    private final static String QRY_ARO_CONTENUTO_COMP = "SELECT BL_CONTEN_COMP FROM ARO_CONTENUTO_COMP t "
            + "where t.id_comp_strut_doc = ?";
    private final static String QRY_VRS_CONTENUTO_FILE = "SELECT BL_CONTENUTO_FILE_SESSIONE FROM VRS_CONTENUTO_FILE t "
            + "where t.ID_FILE_SESSIONE = ?";
    //
    private final static String QRY_CNT_FIR_REPORT_COMP = "SELECT count(*) FROM FIR_REPORT t "
            + "where t.id_comp_doc = ?";
    private final static String QRY_FIR_REPORT_COMP = "SELECT BL_CONTENUTO_REPORT FROM FIR_REPORT t "
            + "where t.id_comp_doc = ?";
    private final static int BUFFERSIZE = 10 * 1024 * 1024; // 10 megabyte

    public enum TabellaBlob {
        BLOB, ERRORI_VERS, FIR_REPORT
    }

    /**
     *
     * @param idPadre
     *            id del record padre (relazione 1 a 0/1) dele righe da contare
     * @param tabellaBlobDaLeggere
     *            una delle due tabelle di Sacer su cui effettuare il conteggio
     * 
     * @return istanza di RispostaControlli contenente in Rboolean il valore true e in RLong il conteggio dei blob
     *         trovati oppure in CodErr e DsErr codice e descrizione dell'errore e in Rboolean il valore false
     */
    public RispostaControlli contaBlobComp(long idPadre, TabellaBlob tabellaBlobDaLeggere) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        BigDecimal tmpNum = new BigDecimal(0);
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        String queryStr = null;

        switch (tabellaBlobDaLeggere) {
        case BLOB:
            queryStr = QRY_CNT_ARO_CONTENUTO_COMP;
            break;
        case ERRORI_VERS:
            queryStr = QRY_CNT_VRS_CONTENUTO_FILE;
            break;
        case FIR_REPORT:
            queryStr = QRY_CNT_FIR_REPORT_COMP;
            break;
        }
        try {
            java.sql.Connection conn = entityManager.unwrap(java.sql.Connection.class);
            try {
                log.debug(queryStr);
                pstmt = (PreparedStatement) conn.prepareStatement(queryStr);
                pstmt.setLong(1, idPadre);
                rs = (ResultSet) pstmt.executeQuery();
                while (rs.next()) {
                    tmpNum = rs.getBigDecimal(1);
                }
                rispostaControlli.setrLong(tmpNum.intValueExact());
                rispostaControlli.setrBoolean(true);
            } finally {
                rs.close();
                pstmt.close();
            }
        } catch (SQLException ex) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione RecBlbOracle.contaBlobComp: " + ex.getMessage()));
            log.error("Eccezione RecBlbOracle.contaBlobComp ", ex);
        }
        return rispostaControlli;
    }

    /**
     *
     * @param idPadre
     *            id del record padre (relazione 1 a 0/1) della riga da cui recuperare il blob
     * @param outputStream
     *            stream su cui scrivere (può essere uno ZipOutputStream o un semplice FileOutputStream
     * @param tabellaBlobDaLeggere
     *            una delle due tabelle di Sacer da cui leggere il blob
     * 
     * @return istanza di RispostaControlli contenente in Rboolean il valore true oppure in CodErr e DsErr codice e
     *         descrizione dell'errore e in Rboolean il valore false
     */
    public RispostaControlli recuperaBlobCompSuStream(long idPadre, OutputStream outputStream,
            TabellaBlob tabellaBlobDaLeggere) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        Blob blob = null;
        ResultSet rs = null;
        String queryStr = null;
        InputStream is = null;
        byte[] buffer = new byte[BUFFERSIZE];

        switch (tabellaBlobDaLeggere) {
        case BLOB:
            queryStr = QRY_ARO_CONTENUTO_COMP;
            break;
        case ERRORI_VERS:
            queryStr = QRY_VRS_CONTENUTO_FILE;
            break;
        case FIR_REPORT:
            queryStr = QRY_FIR_REPORT_COMP;
            break;
        }
        try {
            java.sql.Connection conn = entityManager.unwrap(java.sql.Connection.class);
            try (PreparedStatement pstmt = conn.prepareStatement(queryStr)) {
                log.info(queryStr);
                pstmt.setLong(1, idPadre);
                rs = (ResultSet) pstmt.executeQuery();
                while (rs.next()) {
                    blob = rs.getBlob(1);
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
            if (blob != null) {
                is = blob.getBinaryStream();
                int len;
                while ((len = is.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                    log.debug("letto blob e scritto su stream...");
                }
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        "RecBlbOracle.recuperaBlobCompSuStream. Errore imprevisto: il blob è nullo"));
                log.error("Eccezione nella lettura della tabella dei dati componente: il blob è nullo");
            }
        } catch (SQLException | IOException ex) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione RecBlbOracle.recuperaBlobCompSuStream: " + ex.getMessage()));
            log.error("Eccezione RecBlbOracle.recuperaBlobCompSuStream ", ex);
        } finally {
            IOUtils.closeQuietly(is);
        }

        return rispostaControlli;
    }

}
