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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */
@Embeddable
public class MonVCntFascKoByTiFascId implements Serializable {

    private String tiDtCreazione;
    private String tiStatoFascKo;
    private BigDecimal idTipoFascicolo;

    @Column(name = "ID_TIPO_FASCICOLO")
    public BigDecimal getIdTipoFascicolo() {
        return this.idTipoFascicolo;
    }

    public void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
        this.idTipoFascicolo = idTipoFascicolo;
    }

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
        return this.tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
        this.tiDtCreazione = tiDtCreazione;
    }

    @Column(name = "TI_STATO_FASC_KO")
    public String getTiStatoFascKo() {
        return this.tiStatoFascKo;
    }

    public void setTiStatoFascKo(String tiStatoFascKo) {
        this.tiStatoFascKo = tiStatoFascKo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.tiDtCreazione);
        hash = 53 * hash + Objects.hashCode(this.tiStatoFascKo);
        hash = 53 * hash + Objects.hashCode(this.idTipoFascicolo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonVCntFascKoByTiFascId other = (MonVCntFascKoByTiFascId) obj;
        if (!Objects.equals(this.tiDtCreazione, other.tiDtCreazione)) {
            return false;
        }
        if (!Objects.equals(this.tiStatoFascKo, other.tiStatoFascKo)) {
            return false;
        }
        if (!Objects.equals(this.idTipoFascicolo, other.idTipoFascicolo)) {
            return false;
        }
        return true;
    }

}
