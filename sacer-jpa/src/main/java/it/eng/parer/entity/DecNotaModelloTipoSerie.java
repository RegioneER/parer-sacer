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
 * The persistent class for the DEC_NOTA_MODELLO_TIPO_SERIE database table.
 */
@Entity
@Table(name = "DEC_NOTA_MODELLO_TIPO_SERIE")

public class DecNotaModelloTipoSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idNotaModelloTipoSerie;

    private String dsNotaTipoSerie;

    private Date dtNotaTipoSerie;

    private BigDecimal pgNotaTipoSerie;

    private DecModelloTipoSerie decModelloTipoSerie;

    private DecTipoNotaSerie decTipoNotaSerie;

    private IamUser iamUser;

    public DecNotaModelloTipoSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_NOTA_MODELLO_TIPO_SERIE")
    @GenericGenerator(name = "SDEC_NOTA_MODELLO_TIPO_SERIE_ID_NOTA_MODELLO_TIPO_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_NOTA_MODELLO_TIPO_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_NOTA_MODELLO_TIPO_SERIE_ID_NOTA_MODELLO_TIPO_SERIE_GENERATOR")
    public Long getIdNotaModelloTipoSerie() {
	return this.idNotaModelloTipoSerie;
    }

    public void setIdNotaModelloTipoSerie(Long idNotaModelloTipoSerie) {
	this.idNotaModelloTipoSerie = idNotaModelloTipoSerie;
    }

    @Column(name = "DS_NOTA_TIPO_SERIE")
    public String getDsNotaTipoSerie() {
	return this.dsNotaTipoSerie;
    }

    public void setDsNotaTipoSerie(String dsNotaTipoSerie) {
	this.dsNotaTipoSerie = dsNotaTipoSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_TIPO_SERIE")
    public Date getDtNotaTipoSerie() {
	return this.dtNotaTipoSerie;
    }

    public void setDtNotaTipoSerie(Date dtNotaTipoSerie) {
	this.dtNotaTipoSerie = dtNotaTipoSerie;
    }

    @Column(name = "PG_NOTA_TIPO_SERIE")
    public BigDecimal getPgNotaTipoSerie() {
	return this.pgNotaTipoSerie;
    }

    public void setPgNotaTipoSerie(BigDecimal pgNotaTipoSerie) {
	this.pgNotaTipoSerie = pgNotaTipoSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_TIPO_SERIE")
    public DecModelloTipoSerie getDecModelloTipoSerie() {
	return this.decModelloTipoSerie;
    }

    public void setDecModelloTipoSerie(DecModelloTipoSerie decModelloTipoSerie) {
	this.decModelloTipoSerie = decModelloTipoSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_NOTA_SERIE")
    public DecTipoNotaSerie getDecTipoNotaSerie() {
	return this.decTipoNotaSerie;
    }

    public void setDecTipoNotaSerie(DecTipoNotaSerie decTipoNotaSerie) {
	this.decTipoNotaSerie = decTipoNotaSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

}
