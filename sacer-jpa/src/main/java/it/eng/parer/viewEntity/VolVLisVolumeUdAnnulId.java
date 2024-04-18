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
public class VolVLisVolumeUdAnnulId implements Serializable {

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.idRichAnnulVers);
        hash = 23 * hash + Objects.hashCode(this.idVolumeConserv);
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
        final VolVLisVolumeUdAnnulId other = (VolVLisVolumeUdAnnulId) obj;
        if (!Objects.equals(this.idRichAnnulVers, other.idRichAnnulVers)) {
            return false;
        }
        if (!Objects.equals(this.idVolumeConserv, other.idVolumeConserv)) {
            return false;
        }
        return true;
    }

    private BigDecimal idRichAnnulVers;

    @Column(name = "ID_RICH_ANNUL_VERS")
    public BigDecimal getIdRichAnnulVers() {
        return idRichAnnulVers;
    }

    public void setIdRichAnnulVers(BigDecimal idRichAnnulVers) {
        this.idRichAnnulVers = idRichAnnulVers;
    }

    private BigDecimal idVolumeConserv;

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
        return idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
        this.idVolumeConserv = idVolumeConserv;
    }
}
