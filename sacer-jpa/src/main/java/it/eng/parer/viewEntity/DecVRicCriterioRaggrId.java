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
public class DecVRicCriterioRaggrId implements Serializable {

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.idCriterioRaggr);
        hash = 41 * hash + Objects.hashCode(this.idRegistroRangeUnitaDoc);
        hash = 41 * hash + Objects.hashCode(this.idRegistroUnitaDoc);
        hash = 41 * hash + Objects.hashCode(this.idTipoDoc);
        hash = 41 * hash + Objects.hashCode(this.idTipoUnitaDoc);
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
        final DecVRicCriterioRaggrId other = (DecVRicCriterioRaggrId) obj;
        if (!Objects.equals(this.idCriterioRaggr, other.idCriterioRaggr)) {
            return false;
        }
        if (!Objects.equals(this.idRegistroRangeUnitaDoc, other.idRegistroRangeUnitaDoc)) {
            return false;
        }
        if (!Objects.equals(this.idRegistroUnitaDoc, other.idRegistroUnitaDoc)) {
            return false;
        }
        if (!Objects.equals(this.idTipoDoc, other.idTipoDoc)) {
            return false;
        }
        if (!Objects.equals(this.idTipoUnitaDoc, other.idTipoUnitaDoc)) {
            return false;
        }
        return true;
    }

    private BigDecimal idCriterioRaggr;

    @Column(name = "ID_CRITERIO_RAGGR")
    public BigDecimal getIdCriterioRaggr() {
        return idCriterioRaggr;
    }

    public void setIdCriterioRaggr(BigDecimal idCriterioRaggr) {
        this.idCriterioRaggr = idCriterioRaggr;
    }

    private BigDecimal idRegistroRangeUnitaDoc;

    @Column(name = "ID_REGISTRO_RANGE_UNITA_DOC")
    public BigDecimal getIdRegistroRangeUnitaDoc() {
        return idRegistroRangeUnitaDoc;
    }

    public void setIdRegistroRangeUnitaDoc(BigDecimal idRegistroRangeUnitaDoc) {
        this.idRegistroRangeUnitaDoc = idRegistroRangeUnitaDoc;
    }

    private BigDecimal idRegistroUnitaDoc;

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    private BigDecimal idTipoDoc;

    @Column(name = "ID_TIPO_DOC")
    public BigDecimal getIdTipoDoc() {
        return idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    private BigDecimal idTipoUnitaDoc;

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }
}
