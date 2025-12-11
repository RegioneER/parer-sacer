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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ELV_V_SEL_UD_DOC_UPD_BY_CRIT database table.
 */
@Entity
@Table(name = "ELV_V_SEL_UD_DOC_UPD_BY_CRIT")

public class ElvVSelUdDocUpdByCrit implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal aaKeyUnitaDoc;

    private Date dtCreazione;

    private String tiEle;

    private BigDecimal idStrut;

    public ElvVSelUdDocUpdByCrit() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Column(name = "TI_ELE")
    public String getTiEle() {
        return this.tiEle;
    }

    public void setTiEle(String tiEle) {
        this.tiEle = tiEle;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    private ElvVSelUdDocUpdByCritId elvVSelUdDocUpdByCritId;

    @EmbeddedId()
    public ElvVSelUdDocUpdByCritId getElvVSelUdDocUpdByCritId() {
        return elvVSelUdDocUpdByCritId;
    }

    public void setElvVSelUdDocUpdByCritId(ElvVSelUdDocUpdByCritId elvVSelUdDocUpdByCritId) {
        this.elvVSelUdDocUpdByCritId = elvVSelUdDocUpdByCritId;
    }
}
