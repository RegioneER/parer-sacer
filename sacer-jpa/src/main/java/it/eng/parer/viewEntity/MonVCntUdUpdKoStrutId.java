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
 * The primary key class for the MON_V_CNT_UD_UPD_KO_STRUT database table.
 */
@Embeddable
public class MonVCntUdUpdKoStrutId implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idUserIam;
    private BigDecimal idStrut;
    private String tiStatoSesUpdKo;

    public MonVCntUdUpdKoStrutId() {/* Hibernate */
    }

    public MonVCntUdUpdKoStrutId(BigDecimal idUserIam, BigDecimal idStrut, String tiStatoSesUpdKo) {
        this.idUserIam = idUserIam;
        this.idStrut = idStrut;
        this.tiStatoSesUpdKo = tiStatoSesUpdKo;
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

    @Column(name = "TI_STATO_SES_UPD_KO")
    public String getTiStatoSesUpdKo() {
        return this.tiStatoSesUpdKo;
    }

    public void setTiStatoSesUpdKo(String tiStatoSesUpdKo) {
        this.tiStatoSesUpdKo = tiStatoSesUpdKo;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MonVCntUdUpdKoStrutId)) {
            return false;
        }
        MonVCntUdUpdKoStrutId castOther = (MonVCntUdUpdKoStrutId) other;
        return ((this.idUserIam == castOther.idUserIam) || ((this.idUserIam != null)
                && (castOther.idUserIam != null) && this.idUserIam.equals(castOther.idUserIam)))
                && ((this.idStrut == castOther.idStrut) || ((this.idStrut != null)
                        && (castOther.idStrut != null) && this.idStrut.equals(castOther.idStrut)))
                && ((this.tiStatoSesUpdKo == castOther.tiStatoSesUpdKo)
                        || ((this.tiStatoSesUpdKo != null) && (castOther.tiStatoSesUpdKo != null)
                                && this.tiStatoSesUpdKo.equals(castOther.tiStatoSesUpdKo)));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + ((this.idUserIam != null) ? this.idUserIam.hashCode() : 0);
        hash = hash * prime + ((this.idStrut != null) ? this.idStrut.hashCode() : 0);
        hash = hash * prime
                + ((this.tiStatoSesUpdKo != null) ? this.tiStatoSesUpdKo.hashCode() : 0);
        return hash;
    }
}