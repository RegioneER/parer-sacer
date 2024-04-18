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
public class OstVLisStrutMmBlobId implements Serializable {

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.idStrut);
        hash = 17 * hash + Objects.hashCode(this.idSubPartition);
        hash = 17 * hash + Objects.hashCode(this.mmVers);
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
        final OstVLisStrutMmBlobId other = (OstVLisStrutMmBlobId) obj;
        if (!Objects.equals(this.idStrut, other.idStrut)) {
            return false;
        }
        if (!Objects.equals(this.idSubPartition, other.idSubPartition)) {
            return false;
        }
        if (!Objects.equals(this.mmVers, other.mmVers)) {
            return false;
        }
        return true;
    }

    private BigDecimal idStrut;

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    private BigDecimal idSubPartition;

    @Column(name = "ID_SUB_PARTITION")
    public BigDecimal getIdSubPartition() {
        return idSubPartition;
    }

    public void setIdSubPartition(BigDecimal idSubPartition) {
        this.idSubPartition = idSubPartition;
    }

    private BigDecimal mmVers;

    @Column(name = "MM_VERS")
    public BigDecimal getMmVers() {
        return mmVers;
    }

    public void setMmVers(BigDecimal mmVers) {
        this.mmVers = mmVers;
    }
}
