package it.eng.parer.web.ejb;

import java.math.BigDecimal;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import it.eng.parer.web.helper.DataMartHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Watchdog notturno per la finalizzazione delle cancellazioni datamart. Gira una volta al giorno
 * per chiudere le richieste che hanno terminato la cancellazione fisica ma non sono state innescate
 * dal polling degli utenti.
 */
@Singleton
@Startup
public class DataMartWatchdogEjb {

    private static final Logger logger = LoggerFactory.getLogger(DataMartWatchdogEjb.class);

    @EJB
    private DataMartEjb dataMartEjb;

    @EJB
    private DataMartHelper dataMartHelper;

    /**
     * Esecuzione pianificata: Ogni giorno alle ore 02:00:00. persistent = false: evita che il
     * container persista il timer su DB.
     */
    @Schedule(hour = "2", minute = "0", second = "0", persistent = false)
    public void puliziaNotturnaRichiesteDimenticate() {
        logger.info("Watchdog Datamart: Avvio ciclo di pulizia notturna (ore 02:00)...");

        try {
            // 1. Recupero le richieste in stato IN_CODA_CANCELLAZIONE o IN_CANCELLAZIONE_FISICA
            List<BigDecimal> richiesteAttive = dataMartHelper.getRichiesteFisicheAttive();

            if (richiesteAttive == null || richiesteAttive.isEmpty()) {
                logger.info("Watchdog Datamart: Nessuna richiesta attiva trovata.");
                return;
            }

            logger.info("Watchdog Datamart: Trovate {} richieste da controllare.",
                    richiesteAttive.size());

            for (BigDecimal idUdDelRichiesta : richiesteAttive) {
                try {
                    // Chiamiamo il metodo del polling.
                    // Se la cancellazione fisica (PL/SQL) è finita, questo metodo innescherà
                    // la pulizia asincrona delle sessioni KO (per RA) o chiuderà la pratica (altri
                    // casi).
                    dataMartEjb.calcolaStatoAvanzamentoCancellazioneFisica(idUdDelRichiesta);

                } catch (Exception ex) {
                    logger.error("Watchdog Datamart: Errore nel processing della richiesta "
                            + idUdDelRichiesta, ex);
                }
            }
        } catch (Exception ex) {
            logger.error(
                    "Watchdog Datamart: Errore grave durante il recupero delle richieste attive",
                    ex);
        }

        logger.info("Watchdog Datamart: Ciclo di pulizia notturna terminato.");
    }
}