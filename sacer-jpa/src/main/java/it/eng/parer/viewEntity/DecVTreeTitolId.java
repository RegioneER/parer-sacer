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
public class DecVTreeTitolId implements Serializable {

    private BigDecimal idValVoceTitol;
    private BigDecimal idVoceTitol;

    @Column(name = "ID_VAL_VOCE_TITOL")
    public BigDecimal getIdValVoceTitol() {
        return this.idValVoceTitol;
    }

    public void setIdValVoceTitol(BigDecimal idValVoceTitol) {
        this.idValVoceTitol = idValVoceTitol;
    }

    @Column(name = "ID_VOCE_TITOL")
    public BigDecimal getIdVoceTitol() {
        return this.idVoceTitol;
    }

    public void setIdVoceTitol(BigDecimal idVoceTitol) {
        this.idVoceTitol = idVoceTitol;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.idValVoceTitol);
        hash = 29 * hash + Objects.hashCode(this.idVoceTitol);
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
        final DecVTreeTitolId other = (DecVTreeTitolId) obj;
        if (!Objects.equals(this.idValVoceTitol, other.idValVoceTitol)) {
            return false;
        }
        if (!Objects.equals(this.idVoceTitol, other.idVoceTitol)) {
            return false;
        }
        return true;
    }

}
