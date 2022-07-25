package it.eng.parer.firma.crypto.sign;

/**
 *
 * @author Moretti_Lu
 */
public enum SigningResponse {
    ACTIVE_SESSION_YET("Sessione di firma gi\u00E0 attiva"),
    AUTH_WRONG("Errore di credenziali - Autenticazione errata"),
    USER_BLOCKED("Errore di credenziali - Utente bloccato"), OTP_WRONG("Errore di credenziali - Codice OTP errato"),
    OTP_EXPIRED("Errore di credenziali - Codice OTP scaduto"),
    HSM_ERROR(
            "Errore con l'applicativo HSM - L'applicativo non risponde o non permette la creazione di una sessione di firma"),
    UNKNOWN_ERROR("Errore sconosciuto nella procedura di firma"),
    WARNING("Sessione di firma terminata con warning - Non \u00E8 stato possibile firmare alcuni file"),
    OK("Sessione di firma terminata con successo");

    private final String description;

    private SigningResponse(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
