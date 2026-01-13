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

public class StatoAvanzamentoCancellazioneLogicaDTO implements Serializable {
    private static final long serialVersionUID = 3L; // Aggiornata versione

    // Stati per la UI
    private String statoRichiesta;
    private String statoInternoRichiesta;

    // Dati per la progress bar
    private long totali;
    private long elaborati;

    private java.util.List<it.eng.parer.datamart.dto.ConteggioStatoUdDto> conteggiDettagliati;

    public StatoAvanzamentoCancellazioneLogicaDTO() {
        // Costruttore vuoto
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

    public long getElaborati() {
        return elaborati;
    }

    public void setElaborati(long elaborati) {
        this.elaborati = elaborati;
    }

    public java.util.List<it.eng.parer.datamart.dto.ConteggioStatoUdDto> getConteggiDettagliati() {
        return conteggiDettagliati;
    }

    public void setConteggiDettagliati(
            java.util.List<it.eng.parer.datamart.dto.ConteggioStatoUdDto> conteggiDettagliati) {
        this.conteggiDettagliati = conteggiDettagliati;
    }
}
