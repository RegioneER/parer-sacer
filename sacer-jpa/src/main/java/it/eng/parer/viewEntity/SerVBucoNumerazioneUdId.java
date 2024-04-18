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
public class SerVBucoNumerazioneUdId implements Serializable {

    private BigDecimal pgUdSerIniBuco;
    private BigDecimal idContenutoVerSerie;

    @Column(name = "ID_CONTENUTO_VER_SERIE")
    public BigDecimal getIdContenutoVerSerie() {
        return this.idContenutoVerSerie;
    }

    public void setIdContenutoVerSerie(BigDecimal idContenutoVerSerie) {
        this.idContenutoVerSerie = idContenutoVerSerie;
    }

    @Column(name = "PG_UD_SER_INI_BUCO")
    public BigDecimal getPgUdSerIniBuco() {
        return this.pgUdSerIniBuco;
    }

    public void setPgUdSerIniBuco(BigDecimal pgUdSerIniBuco) {
        this.pgUdSerIniBuco = pgUdSerIniBuco;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.pgUdSerIniBuco);
        hash = 13 * hash + Objects.hashCode(this.idContenutoVerSerie);
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
        final SerVBucoNumerazioneUdId other = (SerVBucoNumerazioneUdId) obj;
        if (!Objects.equals(this.pgUdSerIniBuco, other.pgUdSerIniBuco)) {
            return false;
        }
        if (!Objects.equals(this.idContenutoVerSerie, other.idContenutoVerSerie)) {
            return false;
        }
        return true;
    }

}
