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

/*
 * Attenzione Il codice di questo modulo software Ã¨ legato: al DBMS Oracle al driver JDBC
 * proprietario di Oracle (compatibile JDBC 4) alla gestione propria di Oracle delle colonne di tipo
 * BLOB
 *
 * Questo codice non Ã¨ direttamente usabile su altre architetture di database e presumibilmente
 * dovrÃ  essere riscritto in parte o del tutto per gestire la modalitÃ  di gestione delle colonne
 * di tipo BLOB impiegata da un eventuale altro DBMS.
 *
 * La scelta di utilizzare una modalitÃ  di accesso non portabile per scrivere su tabella nasce
 * dall'esigenza di dover leggere e scrivere colonne di tipo BLOB potenzialmente enormi e
 * dall'incapacitÃ  dell'architettura JPA di effettuare queste operazioni tramite stream.
 */
package it.eng.parer.ws.recupero.ejb.oracleBlb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ConnectionException;
import it.eng.parer.firma.crypto.verifica.CryptoInvoker;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.spagoCore.util.JpaUtils;

/**
 * @author Fioravanti_F
 */
@Stateless(mappedName = "RecBlbOracle")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecBlbOracle {

    @EJB
    private CryptoInvoker cryptoInvoker;

    private static final Logger log = LoggerFactory.getLogger(RecBlbOracle.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    private static final String QRY_CNT_VRS_CONTENUTO_FILE_KO = "SELECT count(*) FROM VRS_CONTENUTO_FILE_KO t "
            + "where t.ID_FILE_SESSIONE_KO = ?";
    //
    private static final String QRY_ARO_CONTENUTO_COMP = "SELECT BL_CONTEN_COMP FROM ARO_CONTENUTO_COMP t "
            + "where t.id_comp_strut_doc = ?";
    private static final String QRY_VRS_CONTENUTO_FILE_KO = "SELECT BL_CONTENUTO_FILE_SESSIONE FROM VRS_CONTENUTO_FILE_KO t "
            + "where t.ID_FILE_SESSIONE_KO = ?";
    private static final String QRY_ELV_FILE_ELENCO_VERS = "SELECT BL_FILE_ELENCO_VERS FROM ELV_FILE_ELENCO_VERS t "
            + "where t.id_file_elenco_vers = ?";
    private static final String QRY_ELV_FILE_ELENCO_VERS_FASC = "SELECT BL_FILE_ELENCO_VERS FROM ELV_FILE_ELENCO_VERS_FASC t "
            + "where t.id_file_elenco_vers_fasc = ?";
    private static final String QRY_SER_FILE_VER_SERIE_INDICE_AIP = "SELECT BL_FILE FROM SER_FILE_VER_SERIE t "
            + "where t.id_ver_serie = ? AND t.ti_file_ver_serie = ?";
    //
    private static final String QRY_FIR_REPORT_COMP = "SELECT BL_CONTENUTO_REPORT FROM FIR_REPORT t "
            + "where t.id_comp_doc = ?";
    private static final int BUFFERSIZE = 10 * 1024 * 1024; // 10 megabyte

    public enum TabellaBlob {
        BLOB, ERRORI_VERS, FIR_REPORT, ERRORI_VERS_TMP, ELV_FILE_ELENCO, ELV_FILE_ELENCO_FASC,
        SER_FILE_VER_SERIE
    }

    /**
     * @param idPadre id del record padre (relazione 1 a 0/1) dele righe da contare
     *
     * @return numero dei blob o EJBException
     */
    public long contaBlobErroriVers(long idPadre) {
        long result = 0L;
        BigDecimal tmpNum = new BigDecimal(0);
        ResultSet rs = null;
        try {
            java.sql.Connection conn = JpaUtils.provideConnectionFrom(entityManager);
            try (PreparedStatement pstmt = conn.prepareStatement(QRY_CNT_VRS_CONTENUTO_FILE_KO)) {
                log.debug(QRY_CNT_VRS_CONTENUTO_FILE_KO);
                pstmt.setLong(1, idPadre);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    tmpNum = rs.getBigDecimal(1);
                }
                result = tmpNum.intValueExact();

            } finally {
                if (rs != null) {
                    rs.close();
                }
                closeConnection(conn);
            }
        } catch (SQLException ex) {
            throw new EJBException(ex);
        }
        return result;
    }

    /**
     * @param idPadre              id del record padre (relazione 1 a 0/1) della riga da cui
     *                             recuperare il blob
     * @param outputStream         stream su cui scrivere (puÃ² essere uno ZipOutputStream o un
     *                             semplice FileOutputStream
     * @param tabellaBlobDaLeggere una delle due tabelle di Sacer da cui leggere il blob
     * @param dto                  dto recupero
     *
     * @return true se è andato tutto bene, false altrimenti
     */
    public boolean recuperaBlobCompSuStream(long idPadre, OutputStream outputStream,
            TabellaBlob tabellaBlobDaLeggere, RecuperoDocBean dto) {
        boolean rispostaControlli = false;
        Blob blob = null;
        ResultSet rs = null;
        String queryStr = null;
        byte[] buffer = new byte[BUFFERSIZE];

        switch (tabellaBlobDaLeggere) {
        case BLOB:
            queryStr = QRY_ARO_CONTENUTO_COMP;
            break;
        case ERRORI_VERS:
            queryStr = QRY_VRS_CONTENUTO_FILE_KO;
            break;
        case FIR_REPORT:
            queryStr = QRY_FIR_REPORT_COMP;
            break;
        case ELV_FILE_ELENCO:
            queryStr = QRY_ELV_FILE_ELENCO_VERS;
            break;
        case ELV_FILE_ELENCO_FASC:
            queryStr = QRY_ELV_FILE_ELENCO_VERS_FASC;
            break;
        case SER_FILE_VER_SERIE:
            queryStr = QRY_SER_FILE_VER_SERIE_INDICE_AIP;
            break;
        }
        try {
            java.sql.Connection conn = JpaUtils.provideConnectionFrom(entityManager);
            try (PreparedStatement pstmt = conn.prepareStatement(queryStr)) {
                log.info(queryStr);
                pstmt.setLong(1, idPadre);
                if (dto != null && dto.getTiFile() != null) {
                    pstmt.setString(2, dto.getTiFile());
                }
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    blob = rs.getBlob(1);
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                closeConnection(conn);
            }
            if (blob != null) {
                try (InputStream is = blob.getBinaryStream();) {
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, len);
                        log.debug("letto blob e scritto su stream...");
                    }
                }
                rispostaControlli = true;
            } else {
                log.error(
                        "Eccezione nella lettura della tabella dei dati componente: il blob è nullo");
            }
        } catch (SQLException | IOException ex) {

            log.error("Eccezione RecBlbOracle.recuperaBlobCompSuStream ", ex);
        }

        return rispostaControlli;
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                throw new ConnectionException(
                        "RecBlbOracle: Errore nella chiusura della connessione: "
                                + ex.getMessage());
            }
        }
    }
}
