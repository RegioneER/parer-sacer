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
 * The persistent class for the ELV_V_CHK_ADD_DOC_AGG_NO_ELE_COR database table.
 *
 */
@Entity
@Table(name = "ELV_V_CHK_ADD_DOC_AGG_NO_ELE_COR")

@NamedQuery(name = "ElvVChkAddDocAggNoEleCor.findByIdDoc", query = "SELECT e FROM ElvVChkAddDocAggNoEleCor e WHERE e.idDoc=:idDoc")
public class ElvVChkAddDocAggNoEleCor implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idDoc;
    private String flAddDocUdOk;
    private String flAllAddDocPrecOk;

    public ElvVChkAddDocAggNoEleCor() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
	return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
	this.idDoc = idDoc;
    }

    @Column(name = "FL_ADD_DOC_UD_OK", columnDefinition = "char(1)")
    public String getFlAddDocUdOk() {
	return this.flAddDocUdOk;
    }

    public void setFlAddDocUdOk(String flAddDocUdOk) {
	this.flAddDocUdOk = flAddDocUdOk;
    }

    @Column(name = "FL_ALL_ADD_DOC_PREC_OK", columnDefinition = "char(1)")
    public String getFlAllAddDocPrecOk() {
	return this.flAllAddDocPrecOk;
    }

    public void setFlAllAddDocPrecOk(String flAllAddDocPrecOk) {
	this.flAllAddDocPrecOk = flAllAddDocPrecOk;
    }

}
