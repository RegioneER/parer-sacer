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

package it.eng.parer.web.ejb;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB dedicato ESCLUSIVAMENTE all'avvio di job Oracle tramite transazioni gestite manualmente (BMT
 * - Bean-Managed Transactions). Questo isola la logica transazionale speciale senza impattare altri
 * helper o EJB dell'applicazione.
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.BEAN) // <-- Dice a JBoss: "le transazioni qui le
// gestisco io"
public class JobCancellazioneFisicaStarterEjb {

    private static final Logger logger = LoggerFactory
            .getLogger(JobCancellazioneFisicaStarterEjb.class);

    /**
     * Avvia il job di cancellazione fisica PL/SQL usando JDBC puro.
     *
     * @param idUdDelRichiesta L'ID della richiesta da processare.
     * @param modCancellazione modalità di cancellazione, può assumere valore CAMPIONE o COMPLETA
     *
     * @return L'ID del job Oracle creato.
     */
    public long avviaJobCancellazioneFisica(BigDecimal idUdDelRichiesta, String modCancellazione) {
        logger.info("Avvio job di cancellazione per richiesta {} con JDBC puro.", idUdDelRichiesta);

        // USARE SEMPRE BLOCCHI try-with-resources PER CHIUDERE LE CONNESSIONI
        try (Connection conn = getJdbcConnection();
                CallableStatement cstmt = conn
                        .prepareCall("{call cancellazione_ud.avvia_job_cancellazione(?,?,?)}")) {

            conn.setAutoCommit(false); // Prendiamo noi il controllo esplicito

            cstmt.setBigDecimal(1, idUdDelRichiesta);
            cstmt.setString(2, modCancellazione);
            cstmt.registerOutParameter(3, Types.NUMERIC);

            cstmt.execute();

            long jobId = cstmt.getBigDecimal(3).longValue();

            // Il COMMIT nella procedura PL/SQL ha già salvato il lavoro.
            // Il commit qui serve solo se la procedura non avesse il suo. Per sicurezza, lo
            // lasciamo.
            // Se la procedura ha già fatto commit, questo non farà nulla di male.
            conn.commit();

            logger.info("Job di cancellazione avviato con successo via JDBC. JOB_ID: {}", jobId);
            return jobId;

        } catch (Exception e) {
            logger.error("Errore devastante durante l'avvio del job con JDBC puro.", e);
            // Non c'è bisogno di fare rollback qui perché try-with-resources chiuderà la
            // connessione
            // e una transazione non committata viene automaticamente annullata.
            throw new EJBException("Fallimento nell'avvio del job con JDBC", e);
        }
    }

    /**
     * Recupera una connessione JDBC diretta dal datasource configurato in JBoss.
     *
     * @return Una connessione JDBC.
     *
     * @throws Exception In caso di errore nel lookup JNDI o nel recupero della connessione.
     */
    private Connection getJdbcConnection() throws Exception {
        // Il nome JNDI del tuo datasource. Solitamente è in un file di configurazione
        // come standalone.xml o domain.xml. DEVI trovare quello giusto.
        // Esempi comuni: "java:jboss/datasources/YourDataSource", "java:/YourDataSource"
        String jndiName = "java:jboss/datasources/SacerJobDs"; // <--- CAMBIA QUESTO CON IL TUO NOME
        // JNDI REALE

        InitialContext ctx = new InitialContext();
        DataSource dataSource = (DataSource) ctx.lookup(jndiName);
        return dataSource.getConnection();
    }
}
