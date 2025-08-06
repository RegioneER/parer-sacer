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
 * The persistent class for the MON_V_CNT_FASC_BY_ENTE database table.
 */
@Entity
@Table(name = "MON_V_CNT_FASC_BY_ENTE")
@NamedQuery(name = "MonVCntFascByEnte.findByEnteUser", query = "SELECT m FROM MonVCntFascByEnte m WHERE m.monVCntFascByEnteId.idEnte = :idEnte AND m.monVCntFascByEnteId.idUserIam = :idUser")
public class MonVCntFascByEnte implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal niFasc;

    public MonVCntFascByEnte() {/* Hibernate */
    }

    @Column(name = "NI_FASC")
    public BigDecimal getNiFasc() {
	return this.niFasc;
    }

    public void setNiFasc(BigDecimal niFasc) {
	this.niFasc = niFasc;
    }

    private MonVCntFascByEnteId monVCntFascByEnteId;

    @EmbeddedId()
    public MonVCntFascByEnteId getMonVCntFascByEnteId() {
	return monVCntFascByEnteId;
    }

    public void setMonVCntFascByEnteId(MonVCntFascByEnteId monVCntFascByEnteId) {
	this.monVCntFascByEnteId = monVCntFascByEnteId;
    }
}
