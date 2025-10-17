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
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable()
public class MonVCntUdAnnulEnteId implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idEnte;

    private BigDecimal idUserIam;

    private String tiStatoAnnul;

    private String tiDtCreazione;

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
	return idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }

    @Column(name = "TI_STATO_ANNUL")
    public String getTiStatoAnnul() {
	return tiStatoAnnul;
    }

    public void setTiStatoAnnul(String tiStatoAnnul) {
	this.tiStatoAnnul = tiStatoAnnul;
    }

    @Column(name = "TI_DT_CREAZIONE")
    public String getTiDtCreazione() {
	return tiDtCreazione;
    }

    public void setTiDtCreazione(String tiDtCreazione) {
	this.tiDtCreazione = tiDtCreazione;
    }

    @Override
    public int hashCode() {
	int hash = 0;
	hash += (idEnte != null ? idEnte.hashCode() : 0);
	hash += (idUserIam != null ? idUserIam.hashCode() : 0);
	hash += (tiStatoAnnul != null ? tiStatoAnnul.hashCode() : 0);
	hash += (tiDtCreazione != null ? tiDtCreazione.hashCode() : 0);
	return hash;
    }

    @Override
    public boolean equals(Object object) {
	if (!(object instanceof MonVCntUdAnnulEnteId)) {
	    return false;
	}
	MonVCntUdAnnulEnteId other = (MonVCntUdAnnulEnteId) object;
	if ((this.idEnte == null && other.idEnte != null)
		|| (this.idEnte != null && !this.idEnte.equals(other.idEnte))) {
	    return false;
	}
	if ((this.idUserIam == null && other.idUserIam != null)
		|| (this.idUserIam != null && !this.idUserIam.equals(other.idUserIam))) {
	    return false;
	}
	if ((this.tiStatoAnnul == null && other.tiStatoAnnul != null)
		|| (this.tiStatoAnnul != null && !this.tiStatoAnnul.equals(other.tiStatoAnnul))) {
	    return false;
	}
	if ((this.tiDtCreazione == null && other.tiDtCreazione != null)
		|| (this.tiDtCreazione != null
			&& !this.tiDtCreazione.equals(other.tiDtCreazione))) {
	    return false;
	}
	return true;
    }
}
