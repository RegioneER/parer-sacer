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

@Embeddable()
public class VolVListaCompVolId implements Serializable {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.idAppartCompVolume);
        hash = 97 * hash + Objects.hashCode(this.idFirmaComp);
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
        final VolVListaCompVolId other = (VolVListaCompVolId) obj;
        if (!Objects.equals(this.idAppartCompVolume, other.idAppartCompVolume)) {
            return false;
        }
        if (!Objects.equals(this.idFirmaComp, other.idFirmaComp)) {
            return false;
        }
        return true;
    }

    private BigDecimal idAppartCompVolume;

    @Column(name = "ID_APPART_COMP_VOLUME")
    public BigDecimal getIdAppartCompVolume() {
        return idAppartCompVolume;
    }

    public void setIdAppartCompVolume(BigDecimal idAppartCompVolume) {
        this.idAppartCompVolume = idAppartCompVolume;
    }

    private BigDecimal idFirmaComp;

    @Column(name = "ID_FIRMA_COMP")
    public BigDecimal getIdFirmaComp() {
        return idFirmaComp;
    }

    public void setIdFirmaComp(BigDecimal idFirmaComp) {
        this.idFirmaComp = idFirmaComp;
    }
}
