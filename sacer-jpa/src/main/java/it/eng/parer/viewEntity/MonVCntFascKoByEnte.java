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
 * The persistent class for the MON_V_CNT_FASC_KO_BY_ENTE database table.
 */
@Entity
@Table(name = "MON_V_CNT_FASC_KO_BY_ENTE")
@NamedQuery(name = "MonVCntFascKoByEnte.findByEnteUser", query = "SELECT m FROM MonVCntFascKoByEnte m WHERE m.monVCntFascKoByEnteId.idEnte = :idEnte AND m.monVCntFascKoByEnteId.idUserIam = :idUser")
public class MonVCntFascKoByEnte implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal niFascKo;

    public MonVCntFascKoByEnte() {/* Hibernate */
    }

    @Column(name = "NI_FASC_KO")
    public BigDecimal getNiFascKo() {
	return this.niFascKo;
    }

    public void setNiFascKo(BigDecimal niFascKo) {
	this.niFascKo = niFascKo;
    }

    private MonVCntFascKoByEnteId monVCntFascKoByEnteId;

    @EmbeddedId()
    public MonVCntFascKoByEnteId getMonVCntFascKoByEnteId() {
	return monVCntFascKoByEnteId;
    }

    public void setMonVCntFascKoByEnteId(MonVCntFascKoByEnteId monVCntFascKoByEnteId) {
	this.monVCntFascKoByEnteId = monVCntFascKoByEnteId;
    }
}
