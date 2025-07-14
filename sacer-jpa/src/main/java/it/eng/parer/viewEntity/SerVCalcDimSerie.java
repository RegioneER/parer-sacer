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
 * The persistent class for the SER_V_CALC_DIM_SERIE database table.
 *
 */
@Entity
@Table(name = "SER_V_CALC_DIM_SERIE")

public class SerVCalcDimSerie implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idContenutoVerSerie;
    private BigDecimal niMbSizeContenutoEff;

    public SerVCalcDimSerie() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_CONTENUTO_VER_SERIE")
    public BigDecimal getIdContenutoVerSerie() {
	return this.idContenutoVerSerie;
    }

    public void setIdContenutoVerSerie(BigDecimal idContenutoVerSerie) {
	this.idContenutoVerSerie = idContenutoVerSerie;
    }

    @Column(name = "NI_MB_SIZE_CONTENUTO_EFF")
    public BigDecimal getNiMbSizeContenutoEff() {
	return this.niMbSizeContenutoEff;
    }

    public void setNiMbSizeContenutoEff(BigDecimal niMbSizeContenutoEff) {
	this.niMbSizeContenutoEff = niMbSizeContenutoEff;
    }

}
