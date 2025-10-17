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

package it.eng.parer.firma.crypto.sign;

/**
 *
 * @author Moretti_Lu
 */
public enum SigningResponse {
    ACTIVE_SESSION_YET("Sessione di firma gi\u00E0 attiva"),
    AUTH_WRONG("Errore di credenziali - Autenticazione errata"),
    USER_BLOCKED("Errore di credenziali - Utente bloccato"),
    OTP_WRONG("Errore di credenziali - Codice OTP errato"),
    OTP_EXPIRED("Errore di credenziali - Codice OTP scaduto"),
    HSM_ERROR(
	    "Errore con l'applicativo HSM - L'applicativo non risponde o non permette la creazione di una sessione di firma"),
    UNKNOWN_ERROR("Errore sconosciuto nella procedura di firma"),
    ERROR_COMPLETAMENTO_FIRMA("Errore nel completamento nella procedura di firma"),
    WARNING("Sessione di firma terminata con warning - Non \u00E8 stato possibile firmare alcuni file"),
    OK("Sessione di firma terminata con successo"),
    // WARNING_SECONDA_FASE("Sessione di firma terminata con warning - Non \u00E8 stato possibile
    // firmare alcuni file"),
    OK_SECONDA_FASE("Sessione di firma terminata con successo");

    private final String description;

    private SigningResponse(String description) {
	this.description = description;
    }

    public String getDescription() {
	return this.description;
    }
}
