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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;

/**
 * The persistent class for the ELV_ELENCO_VERS_FASC_DA_ELAB database table.
 */
@Entity
@Table(name = "ELV_ELENCO_VERS_FASC_DA_ELAB")

public class ElvElencoVersFascDaElab implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idElencoVersDaElab;

    private BigDecimal aaFascicolo;

    private BigDecimal idCriterioRaggrFasc;

    private BigDecimal idStrut;

    private TiStatoElencoFascDaElab tiStato;

    private ElvElencoVersFasc elvElencoVersFasc;

    public ElvElencoVersFascDaElab() {/* Hibernate */
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

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
	return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
	this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "ID_CRITERIO_RAGGR_FASC")
    public BigDecimal getIdCriterioRaggrFasc() {
	return this.idCriterioRaggrFasc;
    }

    public void setIdCriterioRaggrFasc(BigDecimal idCriterioRaggrFasc) {
	this.idCriterioRaggrFasc = idCriterioRaggrFasc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_STATO")
    public TiStatoElencoFascDaElab getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(TiStatoElencoFascDaElab tiStato) {
	this.tiStato = tiStato;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS_FASC")
    public ElvElencoVersFasc getElvElencoVersFasc() {
	return this.elvElencoVersFasc;
    }

    public void setElvElencoVersFasc(ElvElencoVersFasc elvElencoVersFasc) {
	this.elvElencoVersFasc = elvElencoVersFasc;
    }
}
