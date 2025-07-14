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
 * The persistent class for the SER_V_LIS_NOTA_SERIE database table.
 *
 */
@Entity
@Table(name = "SER_V_LIS_NOTA_SERIE")

public class SerVLisNotaSerie implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdTipoNotaSerie;
    private String dsNotaVerSerie;
    private String dsTipoNotaSerie;
    private Date dtNotaVerSerie;
    private BigDecimal idNotaVerSerie;
    private BigDecimal idTipoNotaSerie;
    private BigDecimal idUserIam;
    private BigDecimal idVerSerie;
    private BigDecimal niOrd;
    private String nmUserid;
    private BigDecimal pgNotaVerSerie;

    public SerVLisNotaSerie() {/* Hibernate */
    }

    @Column(name = "CD_TIPO_NOTA_SERIE")
    public String getCdTipoNotaSerie() {
	return this.cdTipoNotaSerie;
    }

    public void setCdTipoNotaSerie(String cdTipoNotaSerie) {
	this.cdTipoNotaSerie = cdTipoNotaSerie;
    }

    @Column(name = "DS_NOTA_VER_SERIE")
    public String getDsNotaVerSerie() {
	return this.dsNotaVerSerie;
    }

    public void setDsNotaVerSerie(String dsNotaVerSerie) {
	this.dsNotaVerSerie = dsNotaVerSerie;
    }

    @Column(name = "DS_TIPO_NOTA_SERIE")
    public String getDsTipoNotaSerie() {
	return this.dsTipoNotaSerie;
    }

    public void setDsTipoNotaSerie(String dsTipoNotaSerie) {
	this.dsTipoNotaSerie = dsTipoNotaSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_VER_SERIE")
    public Date getDtNotaVerSerie() {
	return this.dtNotaVerSerie;
    }

    public void setDtNotaVerSerie(Date dtNotaVerSerie) {
	this.dtNotaVerSerie = dtNotaVerSerie;
    }

    @Id
    @Column(name = "ID_NOTA_VER_SERIE")
    public BigDecimal getIdNotaVerSerie() {
	return this.idNotaVerSerie;
    }

    public void setIdNotaVerSerie(BigDecimal idNotaVerSerie) {
	this.idNotaVerSerie = idNotaVerSerie;
    }

    @Column(name = "ID_TIPO_NOTA_SERIE")
    public BigDecimal getIdTipoNotaSerie() {
	return this.idTipoNotaSerie;
    }

    public void setIdTipoNotaSerie(BigDecimal idTipoNotaSerie) {
	this.idTipoNotaSerie = idTipoNotaSerie;
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

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
	return this.niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
	this.niOrd = niOrd;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
	return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
	this.nmUserid = nmUserid;
    }

    @Column(name = "PG_NOTA_VER_SERIE")
    public BigDecimal getPgNotaVerSerie() {
	return this.pgNotaVerSerie;
    }

    public void setPgNotaVerSerie(BigDecimal pgNotaVerSerie) {
	this.pgNotaVerSerie = pgNotaVerSerie;
    }

}
