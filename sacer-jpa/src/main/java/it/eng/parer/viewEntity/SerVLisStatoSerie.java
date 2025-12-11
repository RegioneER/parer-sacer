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
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the SER_V_LIS_STATO_SERIE database table.
 *
 */
@Entity
@Table(name = "SER_V_LIS_STATO_SERIE")

public class SerVLisStatoSerie implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dsAzione;
    private String dsNotaAzione;
    private Date dtRegStatoVerSerie;
    private BigDecimal idStatoVerSerie;
    private BigDecimal idUserIam;
    private BigDecimal idVerSerie;
    private String nmUserid;
    private BigDecimal pgStatoVerSerie;
    private String tiStatoVerSerie;

    public SerVLisStatoSerie() {/* Hibernate */
    }

    @Column(name = "DS_AZIONE")
    public String getDsAzione() {
        return this.dsAzione;
    }

    public void setDsAzione(String dsAzione) {
        this.dsAzione = dsAzione;
    }

    @Column(name = "DS_NOTA_AZIONE")
    public String getDsNotaAzione() {
        return this.dsNotaAzione;
    }

    public void setDsNotaAzione(String dsNotaAzione) {
        this.dsNotaAzione = dsNotaAzione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_VER_SERIE")
    public Date getDtRegStatoVerSerie() {
        return this.dtRegStatoVerSerie;
    }

    public void setDtRegStatoVerSerie(Date dtRegStatoVerSerie) {
        this.dtRegStatoVerSerie = dtRegStatoVerSerie;
    }

    @Id
    @Column(name = "ID_STATO_VER_SERIE")
    public BigDecimal getIdStatoVerSerie() {
        return this.idStatoVerSerie;
    }

    public void setIdStatoVerSerie(BigDecimal idStatoVerSerie) {
        this.idStatoVerSerie = idStatoVerSerie;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "PG_STATO_VER_SERIE")
    public BigDecimal getPgStatoVerSerie() {
        return this.pgStatoVerSerie;
    }

    public void setPgStatoVerSerie(BigDecimal pgStatoVerSerie) {
        this.pgStatoVerSerie = pgStatoVerSerie;
    }

    @Column(name = "TI_STATO_VER_SERIE")
    public String getTiStatoVerSerie() {
        return this.tiStatoVerSerie;
    }

    public void setTiStatoVerSerie(String tiStatoVerSerie) {
        this.tiStatoVerSerie = tiStatoVerSerie;
    }

}
