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
 * The persistent class for the MON_V_CNT_DOC_STRUT_B30 database table.
 */
@Entity
@Table(name = "MON_V_CNT_DOC_STRUT_B30")

public class MonVCntDocStrutB30 implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal niDoc;

    public MonVCntDocStrutB30() {
        /* hibernate */
    }

    @Column(name = "NI_DOC")
    public BigDecimal getNiDoc() {
        return this.niDoc;
    }

    public void setNiDoc(BigDecimal niDoc) {
        this.niDoc = niDoc;
    }

    private MonVCntDocStrutB30Id monVCntDocStrutB30Id;

    @EmbeddedId()
    public MonVCntDocStrutB30Id getMonVCntDocStrutB30Id() {
        return monVCntDocStrutB30Id;
    }

    public void setMonVCntDocStrutB30Id(MonVCntDocStrutB30Id monVCntDocStrutB30Id) {
        this.monVCntDocStrutB30Id = monVCntDocStrutB30Id;
    }
}
