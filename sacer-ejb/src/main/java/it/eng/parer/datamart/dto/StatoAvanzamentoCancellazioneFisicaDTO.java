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

package it.eng.parer.datamart.dto;

import java.io.Serializable;

/**
 * DTO (Data Transfer Object) per trasportare lo stato di avanzamento del processo di CANCELLAZIONE
 * FISICA dal backend al frontend.
 *
 * Contiene sia lo stato visibile all'utente sia lo stato tecnico interno che guida la logica
 * dell'applicazione (es. il polling).
 *
 * @author paogilio (revisionato per architettura a doppio stato)
 */
public class StatoAvanzamentoCancellazioneFisicaDTO implements Serializable {

    /**
     * Lo stato di alto livello visibile all'utente. Es: "DA_EVADERE", "EVASA".
     */
    private String statoRichiesta;

    /**
     * Lo stato tecnico dettagliato che guida la logica dell'applicazione. Es:
     * "PRONTA_PER_CANCELLAZIONE", "IN_CANCELLAZIONE_FISICA", "ERRORE_FISICO_PARZIALE".
     */
    private String statoInternoRichiesta;

    /**
     * Il numero totale di Unità Documentarie incluse in questa richiesta di cancellazione.
     * Rappresenta il 100% del lavoro da fare.
     */
    private long totali;

    /**
     * Il numero di Unità Documentarie già cancellate fisicamente. Rappresenta il progresso attuale.
     */
    private long cancellate;

    private java.util.List<it.eng.parer.datamart.dto.ConteggioStatoUdDto> conteggiDettagliati;

    /**
     * Costruttore di default.
     */
    public StatoAvanzamentoCancellazioneFisicaDTO() {
        // Costruttore vuoto necessario per la serializzazione/deserializzazione
    }

    public String getStatoRichiesta() {
        return statoRichiesta;
    }

    public void setStatoRichiesta(String statoRichiesta) {
        this.statoRichiesta = statoRichiesta;
    }

    public String getStatoInternoRichiesta() {
        return statoInternoRichiesta;
    }

    public void setStatoInternoRichiesta(String statoInternoRichiesta) {
        this.statoInternoRichiesta = statoInternoRichiesta;
    }

    public long getTotali() {
        return totali;
    }

    public void setTotali(long totali) {
        this.totali = totali;
    }

    public long getCancellate() {
        return cancellate;
    }

    public void setCancellate(long cancellate) {
        this.cancellate = cancellate;
    }

    public java.util.List<it.eng.parer.datamart.dto.ConteggioStatoUdDto> getConteggiDettagliati() {
        return conteggiDettagliati;
    }

    public void setConteggiDettagliati(
            java.util.List<it.eng.parer.datamart.dto.ConteggioStatoUdDto> conteggiDettagliati) {
        this.conteggiDettagliati = conteggiDettagliati;
    }

}
