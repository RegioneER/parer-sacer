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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the DEC_V_LIS_SIS_VERS_BY_TIPO_UD database table.
 *
 */
@Entity
@Table(name = "DEC_V_LIS_SIS_VERS_BY_TIPO_UD")

public class DecVLisSisVersByTipoUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private Date dtErog;
    private Date dtLastErog;
    private String nmSistemaVersante;
    private DecVLisSisVersByTipoUdId decVLisSisVersByTipoUdId;

    public DecVLisSisVersByTipoUd() {/* Hibernate */
    }

    @EmbeddedId
    public DecVLisSisVersByTipoUdId getDecVLisSisVersByTipoUdId() {
        return decVLisSisVersByTipoUdId;
    }

    public void setDecVLisSisVersByTipoUdId(DecVLisSisVersByTipoUdId decVLisSisVersByTipoUdId) {
        this.decVLisSisVersByTipoUdId = decVLisSisVersByTipoUdId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_EROG")
    public Date getDtErog() {
        return this.dtErog;
    }

    public void setDtErog(Date dtErog) {
        this.dtErog = dtErog;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_EROG")
    public Date getDtLastErog() {
        return this.dtLastErog;
    }

    public void setDtLastErog(Date dtLastErog) {
        this.dtLastErog = dtLastErog;
    }

    @Column(name = "NM_SISTEMA_VERSANTE")
    public String getNmSistemaVersante() {
        return this.nmSistemaVersante;
    }

    public void setNmSistemaVersante(String nmSistemaVersante) {
        this.nmSistemaVersante = nmSistemaVersante;
    }

}
