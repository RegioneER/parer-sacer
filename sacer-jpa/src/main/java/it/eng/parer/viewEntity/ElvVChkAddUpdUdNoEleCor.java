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
import javax.persistence.NamedQuery;

import javax.persistence.Table;

/**
 * The persistent class for the ELV_V_CHK_ADD_UPD_UD_NO_ELE_COR database table.
 *
 */
@Entity
@Table(name = "ELV_V_CHK_ADD_UPD_UD_NO_ELE_COR")

@NamedQuery(name = "ElvVChkAddUpdUdNoEleCor.findByIdUpd", query = "SELECT e FROM ElvVChkAddUpdUdNoEleCor e WHERE e.idUpdUnitaDoc=:idUpdUnitaDoc")
public class ElvVChkAddUpdUdNoEleCor implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idUpdUnitaDoc;
    private String flAddUpdUdOk;
    private String flAllAddUpdDocOk;
    private String flAllUpdPrecOk;

    public ElvVChkAddUpdUdNoEleCor() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_UPD_UNITA_DOC")
    public BigDecimal getIdUpdUnitaDoc() {
	return this.idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
	this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "FL_ADD_UPD_UD_OK", columnDefinition = "char(1)")
    public String getFlAddUpdUdOk() {
	return this.flAddUpdUdOk;
    }

    public void setFlAddUpdUdOk(String flAddUpdUdOk) {
	this.flAddUpdUdOk = flAddUpdUdOk;
    }

    @Column(name = "FL_ALL_ADD_UPD_DOC_OK", columnDefinition = "char(1)")
    public String getFlAllAddUpdDocOk() {
	return this.flAllAddUpdDocOk;
    }

    public void setFlAllAddUpdDocOk(String flAllAddUpdDocOk) {
	this.flAllAddUpdDocOk = flAllAddUpdDocOk;
    }

    @Column(name = "FL_ALL_UPD_PREC_OK", columnDefinition = "char(1)")
    public String getFlAllUpdPrecOk() {
	return this.flAllUpdPrecOk;
    }

    public void setFlAllUpdPrecOk(String flAllUpdPrecOk) {
	this.flAllUpdPrecOk = flAllUpdPrecOk;
    }

}
