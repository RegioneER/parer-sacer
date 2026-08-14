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
 * DTO per rappresentare un singolo passaggio di stato interno della richiesta di cancellazione,
 * letto dalla tabella DM_UD_DEL_STATO_RICHIESTA.
 */
public class StatoRichiestaDTO implements Serializable {

    private String tiStatoInternoRich;
    /**
     * Data e ora dell'assunzione dello stato, in millisecondi epoch (per la formattazione in JS).
     */
    private long dtRegStato;

    public StatoRichiestaDTO() {
    }

    public StatoRichiestaDTO(String tiStatoInternoRich, long dtRegStato) {
        this.tiStatoInternoRich = tiStatoInternoRich;
        this.dtRegStato = dtRegStato;
    }

    public String getTiStatoInternoRich() {
        return tiStatoInternoRich;
    }

    public void setTiStatoInternoRich(String tiStatoInternoRich) {
        this.tiStatoInternoRich = tiStatoInternoRich;
    }

    public long getDtRegStato() {
        return dtRegStato;
    }

    public void setDtRegStato(long dtRegStato) {
        this.dtRegStato = dtRegStato;
    }
}
