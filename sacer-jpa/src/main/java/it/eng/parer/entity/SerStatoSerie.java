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
 * The persistent class for the SER_STATO_SERIE database table.
 */
@Entity
@Table(name = "SER_STATO_SERIE")

public class SerStatoSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStatoSerie;

    private String dsAzione;

    private String dsNotaAzione;

    private Date dtRegStatoSerie;

    private BigDecimal pgStatoSerie;

    private String tiStatoSerie;

    private IamUser iamUser;

    private SerSerie serSerie;

    public SerStatoSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_STATO_SERIE")
    @GenericGenerator(name = "SSER_STATO_SERIE_ID_STATO_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_STATO_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_STATO_SERIE_ID_STATO_SERIE_GENERATOR")
    public Long getIdStatoSerie() {
	return this.idStatoSerie;
    }

    public void setIdStatoSerie(Long idStatoSerie) {
	this.idStatoSerie = idStatoSerie;
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
    @Column(name = "DT_REG_STATO_SERIE")
    public Date getDtRegStatoSerie() {
	return this.dtRegStatoSerie;
    }

    public void setDtRegStatoSerie(Date dtRegStatoSerie) {
	this.dtRegStatoSerie = dtRegStatoSerie;
    }

    @Column(name = "PG_STATO_SERIE")
    public BigDecimal getPgStatoSerie() {
	return this.pgStatoSerie;
    }

    public void setPgStatoSerie(BigDecimal pgStatoSerie) {
	this.pgStatoSerie = pgStatoSerie;
    }

    @Column(name = "TI_STATO_SERIE")
    public String getTiStatoSerie() {
	return this.tiStatoSerie;
    }

    public void setTiStatoSerie(String tiStatoSerie) {
	this.tiStatoSerie = tiStatoSerie;
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
    @JoinColumn(name = "ID_SERIE")
    public SerSerie getSerSerie() {
	return this.serSerie;
    }

    public void setSerSerie(SerSerie serSerie) {
	this.serSerie = serSerie;
    }

}
