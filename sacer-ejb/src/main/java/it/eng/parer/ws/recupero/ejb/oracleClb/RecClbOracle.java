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
 Attenzione
 Il codice di questo modulo software è legato:
 * al DBMS Oracle
 * al driver JDBC proprietario di Oracle (compatibile JDBC 4)
 * alla gestione propria di Oracle delle colonne di tipo CLOB

 Questo codice non è direttamente usabile su altre architetture di database e presumibilmente
 dovrà essere riscritto in parte o del tutto per gestire la modalità di gestione delle colonne
 di tipo CLOB impiegata da un eventuale altro DBMS.

 La scelta di utilizzare una modalità di accesso non portabile per scrivere su tabella nasce
 dall'esigenza di dover leggere e scrivere colonne di tipo CLOB potenzialmente enormi e
 dall'incapacità dell'architettura JPA di effettuare queste operazioni tramite stream.
 */
package it.eng.parer.ws.recupero.ejb.oracleClb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import it.eng.parer.exception.ConnectionException;
import it.eng.spagoCore.util.JpaUtils;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "RecClbOracle")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecClbOracle {

    private static final Logger log = LoggerFactory.getLogger(RecClbOracle.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    private static final String QRY_ARO_FILE_VER_IX_AIP_UD = "SELECT BL_FILE_VER_INDICE_AIP FROM ARO_FILE_VER_INDICE_AIP_UD t "
            + "where t.id_ver_indice_aip = ?";
    private static final int BUFFERSIZE = 10 * 1024 * 1024; // 10 megabyte

    public enum TabellaClob {
        CLOB
    }

    /**
     * @param idPadre
     *            id del record padre (relazione 1 a 0/1) della riga da cui recuperare il clob
     * @param outputStream
     *            stream su cui scrivere (può essere uno ZipOutputStream o un semplice FileOutputStream
     * @param tabellaClobDaLeggere
     *            una delle tabelle di Sacer da cui leggere il clob
     *
     * @return true se è andato tutto bene, false altrimenti
     */
    public boolean recuperaClobDataSuStream(long idPadre, OutputStream outputStream, TabellaClob tabellaClobDaLeggere) {
        boolean rispostaControlli = false;
        Clob clob = null;
        ResultSet rs = null;
        String queryStr = null;

        switch (tabellaClobDaLeggere) {
        case CLOB:
            queryStr = QRY_ARO_FILE_VER_IX_AIP_UD;
            break;
        }
        try {
            java.sql.Connection conn = JpaUtils.provideConnectionFrom(entityManager);
            try (PreparedStatement pstmt = conn.prepareStatement(queryStr)) {
                log.info(queryStr);
                pstmt.setLong(1, idPadre);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    clob = rs.getClob(1);
                }
            } finally {
                if (rs != null) {
                    rs.close();
                }
                closeConnection(conn);
            }
            if (clob != null) {
                byte[] data = IOUtils.toByteArray(clob.getCharacterStream(), StandardCharsets.UTF_8);
                IOUtils.write(data, outputStream);
                log.debug("letto clob e scritto su stream...");
                rispostaControlli = true;
            } else {
                log.error("Eccezione nella lettura della tabella dei dati: il clob è nullo");
            }
        } catch (SQLException | IOException ex) {

            log.error("Eccezione RecClbOracle.recuperaClobDataSuStream ", ex);
        }

        return rispostaControlli;
    }

    private void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ex) {
                throw new ConnectionException(
                        "RecClbOracle: Errore nella chiusura della connessione: " + ex.getMessage());
            }
        }
    }
}
