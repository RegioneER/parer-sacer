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
 * The persistent class for the ELV_ELENCO_VERS_DA_ELAB database table.
 */
@Entity
@Table(name = "ELV_ELENCO_VERS_DA_ELAB")

public class ElvElencoVersDaElab implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idElencoVersDaElab;

    private BigDecimal aaKeyUnitaDoc;

    private BigDecimal idCriterioRaggr;

    private BigDecimal idStrut;

    private String tiStatoElenco;

    private ElvElencoVer elvElencoVer;

    private Date tsStatoElenco;

    public ElvElencoVersDaElab() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ELENCO_VERS_DA_ELAB")
    @GenericGenerator(name = "SELV_ELENCO_VERS_DA_ELAB_ID_ELENCO_VERS_DA_ELAB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_ELENCO_VERS_DA_ELAB"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_ELENCO_VERS_DA_ELAB_ID_ELENCO_VERS_DA_ELAB_GENERATOR")
    public Long getIdElencoVersDaElab() {
	return this.idElencoVersDaElab;
    }

    public void setIdElencoVersDaElab(Long idElencoVersDaElab) {
	this.idElencoVersDaElab = idElencoVersDaElab;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
	return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "ID_CRITERIO_RAGGR")
    public BigDecimal getIdCriterioRaggr() {
	return this.idCriterioRaggr;
    }

    public void setIdCriterioRaggr(BigDecimal idCriterioRaggr) {
	this.idCriterioRaggr = idCriterioRaggr;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "TI_STATO_ELENCO")
    public String getTiStatoElenco() {
	return this.tiStatoElenco;
    }

    public void setTiStatoElenco(String tiStatoElenco) {
	this.tiStatoElenco = tiStatoElenco;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVer() {
	return this.elvElencoVer;
    }

    public void setElvElencoVer(ElvElencoVer elvElencoVer) {
	this.elvElencoVer = elvElencoVer;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_STATO_ELENCO")
    public Date getTsStatoElenco() {
	return tsStatoElenco;
    }

    public void setTsStatoElenco(Date tsStatoElenco) {
	this.tsStatoElenco = tsStatoElenco;
    }
}
