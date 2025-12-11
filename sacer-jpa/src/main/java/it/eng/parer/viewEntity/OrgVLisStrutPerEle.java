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
 * The persistent class for the ORG_V_LIS_STRUT_PER_ELE database table.
 *
 */
@Entity
@Table(name = "ORG_V_LIS_STRUT_PER_ELE")

public class OrgVLisStrutPerEle implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idStrut;
    private String flPresenzaElencoFisc;

    public OrgVLisStrutPerEle() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "FL_PRESENZA_ELENCO_FISC", columnDefinition = "char(1)")
    public String getFlPresenzaElencoFisc() {
        return this.flPresenzaElencoFisc;
    }

    public void setFlPresenzaElencoFisc(String flPresenzaElencoFisc) {
        this.flPresenzaElencoFisc = flPresenzaElencoFisc;
    }

}
