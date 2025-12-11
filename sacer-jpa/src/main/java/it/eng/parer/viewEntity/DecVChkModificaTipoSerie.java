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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the DEC_V_CHK_MODIFICA_TIPO_SERIE database table.
 *
 */
@Entity
@Table(name = "DEC_V_CHK_MODIFICA_TIPO_SERIE")

public class DecVChkModificaTipoSerie implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flModificaTipoSerie;
    private BigDecimal idTipoSerie;

    public DecVChkModificaTipoSerie() {/* Hibernate */
    }

    @Column(name = "FL_MODIFICA_TIPO_SERIE", columnDefinition = "char(1)")
    public String getFlModificaTipoSerie() {
        return this.flModificaTipoSerie;
    }

    public void setFlModificaTipoSerie(String flModificaTipoSerie) {
        this.flModificaTipoSerie = flModificaTipoSerie;
    }

    @Id
    @Column(name = "ID_TIPO_SERIE")
    public BigDecimal getIdTipoSerie() {
        return this.idTipoSerie;
    }

    public void setIdTipoSerie(BigDecimal idTipoSerie) {
        this.idTipoSerie = idTipoSerie;
    }

}
