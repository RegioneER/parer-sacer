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
import javax.persistence.Embeddable;

/**
 * The primary key class for the MON_V_CNT_UPD_UD_STRUT database table.
 */
@Embeddable
public class MonVCntUpdUdStrutId implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idUserIam;
    private BigDecimal idStrut;
    private String tiStatoUpdUd;
    private String tiDtCreazione;

    public MonVCntUpdUdStrutId() {/* Hibernate */
    }

    public MonVCntUpdUdStrutId(BigDecimal idUserIam, BigDecimal idStrut, String tiStatoUpdUd,
	    String tiDtCreazione) {
	this.idUserIam = idUserIam;
	this.idStrut = idStrut;
	this.tiStatoUpdUd = tiStatoUpdUd;
	this.tiDtCreazione = tiDtCreazione;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
	return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "TI_STATO_UDP_UD")
    public String getTiStatoUpdUd() {
	return this.tiStatoUpdUd;
    }

    public void setTiStatoUpdUd(String tiStatoUpdUd) {
	this.tiStatoUpdUd = tiStatoUpdUd;
    }

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
	return this.tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
	this.tiDtCreazione = tiDtCreazione;
    }

    @Override
    public boolean equals(Object other) {
	if (this == other) {
	    return true;
	}
	if (!(other instanceof MonVCntUpdUdStrutId)) {
	    return false;
	}
	MonVCntUpdUdStrutId castOther = (MonVCntUpdUdStrutId) other;
	return ((this.idUserIam == castOther.idUserIam) || ((this.idUserIam != null)
		&& (castOther.idUserIam != null) && this.idUserIam.equals(castOther.idUserIam)))
		&& ((this.idStrut == castOther.idStrut) || ((this.idStrut != null)
			&& (castOther.idStrut != null) && this.idStrut.equals(castOther.idStrut)))
		&& ((this.tiStatoUpdUd == castOther.tiStatoUpdUd)
			|| ((this.tiStatoUpdUd != null) && (castOther.tiStatoUpdUd != null)
				&& this.tiStatoUpdUd.equals(castOther.tiStatoUpdUd)))
		&& ((this.tiDtCreazione == castOther.tiDtCreazione)
			|| ((this.tiDtCreazione != null) && (castOther.tiDtCreazione != null)
				&& this.tiDtCreazione.equals(castOther.tiDtCreazione)));
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int hash = 17;
	hash = hash * prime + ((this.idUserIam != null) ? this.idUserIam.hashCode() : 0);
	hash = hash * prime + ((this.idStrut != null) ? this.idStrut.hashCode() : 0);
	hash = hash * prime + ((this.tiStatoUpdUd != null) ? this.tiStatoUpdUd.hashCode() : 0);
	hash = hash * prime + ((this.tiDtCreazione != null) ? this.tiDtCreazione.hashCode() : 0);
	return hash;
    }
}