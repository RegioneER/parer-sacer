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

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_NOTA_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_NOTA_VER_SERIE")

public class SerNotaVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idNotaVerSerie;

    private String dsNotaVerSerie;

    private Date dtNotaVerSerie;

    private BigDecimal pgNotaVerSerie;

    private IamUser iamUser;

    private SerVerSerie serVerSerie;

    private DecTipoNotaSerie decTipoNotaSerie;

    public SerNotaVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_NOTA_VER_SERIE")
    @GenericGenerator(name = "SSER_NOTA_VER_SERIE_ID_NOTA_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_NOTA_VER_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_NOTA_VER_SERIE_ID_NOTA_VER_SERIE_GENERATOR")
    public Long getIdNotaVerSerie() {
	return this.idNotaVerSerie;
    }

    public void setIdNotaVerSerie(Long idNotaVerSerie) {
	this.idNotaVerSerie = idNotaVerSerie;
    }

    @Column(name = "DS_NOTA_VER_SERIE")
    public String getDsNotaVerSerie() {
	return this.dsNotaVerSerie;
    }

    public void setDsNotaVerSerie(String dsNotaVerSerie) {
	this.dsNotaVerSerie = dsNotaVerSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_VER_SERIE")
    public Date getDtNotaVerSerie() {
	return this.dtNotaVerSerie;
    }

    public void setDtNotaVerSerie(Date dtNotaVerSerie) {
	this.dtNotaVerSerie = dtNotaVerSerie;
    }

    @Column(name = "PG_NOTA_VER_SERIE")
    public BigDecimal getPgNotaVerSerie() {
	return this.pgNotaVerSerie;
    }

    public void setPgNotaVerSerie(BigDecimal pgNotaVerSerie) {
	this.pgNotaVerSerie = pgNotaVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
	return this.serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
	this.serVerSerie = serVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_NOTA_SERIE")
    public DecTipoNotaSerie getDecTipoNotaSerie() {
	return this.decTipoNotaSerie;
    }

    public void setDecTipoNotaSerie(DecTipoNotaSerie decTipoNotaSerie) {
	this.decTipoNotaSerie = decTipoNotaSerie;
    }

}
