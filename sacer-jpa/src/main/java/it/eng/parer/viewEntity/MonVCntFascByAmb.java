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
 * The persistent class for the MON_V_CNT_FASC_BY_AMB database table.
 */
@Entity
@Table(name = "MON_V_CNT_FASC_BY_AMB")
@NamedQuery(name = "MonVCntFascByAmb.findByAmbUser", query = "SELECT m FROM MonVCntFascByAmb m WHERE m.monVCntFascByAmbId.idAmbiente = :idAmbiente AND m.monVCntFascByAmbId.idUserIam = :idUser")
public class MonVCntFascByAmb implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal niFasc;

    public MonVCntFascByAmb() {/* Hibernate */
    }

    @Column(name = "NI_FASC")
    public BigDecimal getNiFasc() {
	return this.niFasc;
    }

    public void setNiFasc(BigDecimal niFasc) {
	this.niFasc = niFasc;
    }

    private MonVCntFascByAmbId monVCntFascByAmbId;

    @EmbeddedId()
    public MonVCntFascByAmbId getMonVCntFascByAmbId() {
	return monVCntFascByAmbId;
    }

    public void setMonVCntFascByAmbId(MonVCntFascByAmbId monVCntFascByAmbId) {
	this.monVCntFascByAmbId = monVCntFascByAmbId;
    }
}
