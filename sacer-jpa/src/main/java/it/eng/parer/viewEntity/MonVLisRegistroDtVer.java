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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the MON_V_LIS_REGISTRO_DT_VERS database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_REGISTRO_DT_VERS")

public class MonVLisRegistroDtVer implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date dtErog;
    private Date dtLastErog;
    private BigDecimal idRegistroUnitaDoc;

    public MonVLisRegistroDtVer() {
	/* hibernate */
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_EROG")
    public Date getDtErog() {
	return this.dtErog;
    }

    public void setDtErog(Date dtErog) {
	this.dtErog = dtErog;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_LAST_EROG")
    public Date getDtLastErog() {
	return this.dtLastErog;
    }

    public void setDtLastErog(Date dtLastErog) {
	this.dtLastErog = dtLastErog;
    }

    @Id
    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
	return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
	this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

}
