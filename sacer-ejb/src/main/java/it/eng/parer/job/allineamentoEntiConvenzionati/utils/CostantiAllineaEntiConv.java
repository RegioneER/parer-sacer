package it.eng.parer.job.allineamentoEntiConvenzionati.utils;

/**
 *
 * @author Gilioli_P
 */
public class CostantiAllineaEntiConv {

    // COSTANTI DI ERRORE MANDATI IN RISPOSTA DAL WS (SacerIAM)
    public static final String SERVIZI_ENTE_001 = "SERVIZI-ENTE-001"; // Errore nella creazione del client per la
                                                                      // chiamata al WS di AllineamentoEnteConvenzionato
    public static final String SERVIZI_ENTE_002 = "SERVIZI-ENTE-002"; // Utente non riconosciuto o non abilitato
    public static final String ALLINEA_ENTE_001 = "ALLINEA-ENTE-001"; // Non risponde o errore sistema
    public static final String ERR_666 = "666";

    public enum TiOperAllinea {
        ALLINEA
    }

    public enum TiStatoAllinea {
        DA_ALLINEARE, ALLINEA_OK, ALLINEA_IN_ERRORE, ALLINEA_NON_POSSIBILE, ALLINEA_IN_TIMEOUT
    }

    public enum EsitoServizio {
        OK, KO, NO_RISPOSTA
    }

}
