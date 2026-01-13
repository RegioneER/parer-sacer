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

import javax.persistence.*;

/**
 * The persistent class for the MON_V_CNT_FASC_KO_BY_STRUT database table.
 *
 */
@Entity
@Table(name = "MON_V_CNT_FASC_KO_BY_STRUT")
@NamedQuery(name = "MonVCntFascKoByStrut.findByStrutUserId", query = "SELECT m FROM MonVCntFascKoByStrut m WHERE m.monVCntFascKoByStrutId.idStrut=:idStrut AND m.monVCntFascKoByStrutId.idUserIam=:idUserIam")
public class MonVCntFascKoByStrut implements Serializable {

    private static final long serialVersionUID = 1L;
    private MonVCntFascKoByStrutId monVCntFascKoByStrutId;
    private BigDecimal niFascKo;

    public MonVCntFascKoByStrut() {/* Hibernate */
    }

    @EmbeddedId
    public MonVCntFascKoByStrutId getMonVCntFascKoByStrutId() {
        return monVCntFascKoByStrutId;
    }

    public void setMonVCntFascKoByStrutId(MonVCntFascKoByStrutId monVCntFascKoByStrutId) {
        this.monVCntFascKoByStrutId = monVCntFascKoByStrutId;
    }

    @Column(name = "NI_FASC_KO")
    public BigDecimal getNiFascKo() {
        return this.niFascKo;
    }

    public void setNiFascKo(BigDecimal niFascKo) {
        this.niFascKo = niFascKo;
    }

}
