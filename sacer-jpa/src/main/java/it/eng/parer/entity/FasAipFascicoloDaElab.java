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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the FAS_AIP_FASCICOLO_DA_ELAB database table.
 */
@Entity
@Table(name = "FAS_AIP_FASCICOLO_DA_ELAB")
public class FasAipFascicoloDaElab implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idAipFascicoloDaElab;

    private String dsCausale;

    private Date dtCreazioneDaElab;

    private BigDecimal pgCreazioneDaElab;

    private String tiCreazione;

    private List<FasUdAipFascicoloDaElab> fasUdAipFascicoloDaElabs = new ArrayList<>();

    private FasFascicolo fasFascicolo;

    private ElvElencoVersFasc elvElencoVersFasc;

    public FasAipFascicoloDaElab() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_AIP_FASCICOLO_DA_ELAB")
    @GenericGenerator(name = "SFAS_AIP_FASCICOLO_DA_ELAB_ID_AIP_FASCICOLO_DA_ELAB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SFAS_AIP_FASCICOLO_DA_ELAB"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SFAS_AIP_FASCICOLO_DA_ELAB_ID_AIP_FASCICOLO_DA_ELAB_GENERATOR")
    public Long getIdAipFascicoloDaElab() {
	return this.idAipFascicoloDaElab;
    }

    public void setIdAipFascicoloDaElab(Long idAipFascicoloDaElab) {
	this.idAipFascicoloDaElab = idAipFascicoloDaElab;
    }

    @Column(name = "DS_CAUSALE")
    public String getDsCausale() {
	return this.dsCausale;
    }

    public void setDsCausale(String dsCausale) {
	this.dsCausale = dsCausale;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_DA_ELAB")
    public Date getDtCreazioneDaElab() {
	return this.dtCreazioneDaElab;
    }

    public void setDtCreazioneDaElab(Date dtCreazioneDaElab) {
	this.dtCreazioneDaElab = dtCreazioneDaElab;
    }

    @Column(name = "PG_CREAZIONE_DA_ELAB")
    public BigDecimal getPgCreazioneDaElab() {
	return this.pgCreazioneDaElab;
    }

    public void setPgCreazioneDaElab(BigDecimal pgCreazioneDaElab) {
	this.pgCreazioneDaElab = pgCreazioneDaElab;
    }

    @Column(name = "TI_CREAZIONE")
    public String getTiCreazione() {
	return this.tiCreazione;
    }

    public void setTiCreazione(String tiCreazione) {
	this.tiCreazione = tiCreazione;
    }

    @OneToMany(mappedBy = "fasAipFascicoloDaElab")
    public List<FasUdAipFascicoloDaElab> getFasUdAipFascicoloDaElabs() {
	return this.fasUdAipFascicoloDaElabs;
    }

    public void setFasUdAipFascicoloDaElabs(
	    List<FasUdAipFascicoloDaElab> fasUdAipFascicoloDaElabs) {
	this.fasUdAipFascicoloDaElabs = fasUdAipFascicoloDaElabs;
    }

    public FasUdAipFascicoloDaElab addFasUdAipFascicoloDaElab(
	    FasUdAipFascicoloDaElab fasUdAipFascicoloDaElab) {
	getFasUdAipFascicoloDaElabs().add(fasUdAipFascicoloDaElab);
	fasUdAipFascicoloDaElab.setFasAipFascicoloDaElab(this);
	return fasUdAipFascicoloDaElab;
    }

    public FasUdAipFascicoloDaElab removeFasUdAipFascicoloDaElab(
	    FasUdAipFascicoloDaElab fasUdAipFascicoloDaElab) {
	getFasUdAipFascicoloDaElabs().remove(fasUdAipFascicoloDaElab);
	fasUdAipFascicoloDaElab.setFasAipFascicoloDaElab(null);
	return fasUdAipFascicoloDaElab;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FASCICOLO")
    public FasFascicolo getFasFascicolo() {
	return this.fasFascicolo;
    }

    public void setFasFascicolo(FasFascicolo fasFascicolo) {
	this.fasFascicolo = fasFascicolo;
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
