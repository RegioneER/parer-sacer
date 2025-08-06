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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_CNT_AGG_ENTE_B30 database table.
 */
@Entity
@Table(name = "MON_V_CNT_AGG_ENTE_B30")

public class MonVCntAggEnteB30 implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal niAgg;

    public MonVCntAggEnteB30() {
	/* hibernate */
    }

    @Column(name = "NI_AGG")
    public BigDecimal getNiAgg() {
	return this.niAgg;
    }

    public void setNiAgg(BigDecimal niAgg) {
	this.niAgg = niAgg;
    }

    private MonVCntAggEnteB30Id monVCntAggEnteB30Id;

    @EmbeddedId()
    public MonVCntAggEnteB30Id getMonVCntAggEnteB30Id() {
	return monVCntAggEnteB30Id;
    }

    public void setMonVCntAggEnteB30Id(MonVCntAggEnteB30Id monVCntAggEnteB30Id) {
	this.monVCntAggEnteB30Id = monVCntAggEnteB30Id;
    }
}
