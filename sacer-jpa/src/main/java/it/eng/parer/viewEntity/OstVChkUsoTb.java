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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the OST_V_CHK_USO_TBS database table.
 *
 */
@Entity
@Table(name = "OST_V_CHK_USO_TBS")

public class OstVChkUsoTb implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flOk;
    private String nmTablespace;

    public OstVChkUsoTb() {/* Hibernate */
    }

    @Column(name = "FL_OK", columnDefinition = "char(1)")
    public String getFlOk() {
	return this.flOk;
    }

    public void setFlOk(String flOk) {
	this.flOk = flOk;
    }

    @Id
    @Column(name = "NM_TABLESPACE")
    public String getNmTablespace() {
	return this.nmTablespace;
    }

    public void setNmTablespace(String nmTablespace) {
	this.nmTablespace = nmTablespace;
    }

}
