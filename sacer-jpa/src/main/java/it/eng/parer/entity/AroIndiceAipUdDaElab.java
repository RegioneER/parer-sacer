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

import javax.persistence.CascadeType;
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
 * The persistent class for the ARO_INDICE_AIP_UD_DA_ELAB database table.
 */
@Entity
@Table(name = "ARO_INDICE_AIP_UD_DA_ELAB")
public class AroIndiceAipUdDaElab implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idIndiceAipDaElab;

    private String dsCausale;

    private Date dtCreazioneDaElab;

    private BigDecimal pgCreazioneDaElab;

    private String tiCreazione;

    private List<AroCompIndiceAipDaElab> aroCompIndiceAipDaElabs = new ArrayList<>();

    private List<AroUpdUdIndiceAipDaElab> aroUpdUdIndiceAipDaElabs = new ArrayList<>();

    private AroUnitaDoc aroUnitaDoc;

    private ElvElencoVer elvElencoVer;

    private String flInCoda;

    private Date tsInCoda;

    public AroIndiceAipUdDaElab() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_INDICE_AIP_DA_ELAB")
    @GenericGenerator(name = "SARO_INDICE_AIP_UD_DA_ELAB_ID_INDICE_AIP_DA_ELAB_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_INDICE_AIP_UD_DA_ELAB"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_INDICE_AIP_UD_DA_ELAB_ID_INDICE_AIP_DA_ELAB_GENERATOR")
    public Long getIdIndiceAipDaElab() {
	return this.idIndiceAipDaElab;
    }

    public void setIdIndiceAipDaElab(Long idIndiceAipDaElab) {
	this.idIndiceAipDaElab = idIndiceAipDaElab;
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

    @OneToMany(mappedBy = "aroIndiceAipUdDaElab", cascade = CascadeType.REMOVE)
    public List<AroCompIndiceAipDaElab> getAroCompIndiceAipDaElabs() {
	return this.aroCompIndiceAipDaElabs;
    }

    public void setAroCompIndiceAipDaElabs(List<AroCompIndiceAipDaElab> aroCompIndiceAipDaElabs) {
	this.aroCompIndiceAipDaElabs = aroCompIndiceAipDaElabs;
    }

    public AroCompIndiceAipDaElab addAroCompIndiceAipDaElab(
	    AroCompIndiceAipDaElab aroCompIndiceAipDaElab) {
	getAroCompIndiceAipDaElabs().add(aroCompIndiceAipDaElab);
	aroCompIndiceAipDaElab.setAroIndiceAipUdDaElab(this);
	return aroCompIndiceAipDaElab;
    }

    public AroCompIndiceAipDaElab removeAroCompIndiceAipDaElab(
	    AroCompIndiceAipDaElab aroCompIndiceAipDaElab) {
	getAroCompIndiceAipDaElabs().remove(aroCompIndiceAipDaElab);
	aroCompIndiceAipDaElab.setAroIndiceAipUdDaElab(null);
	return aroCompIndiceAipDaElab;
    }

    @OneToMany(mappedBy = "aroIndiceAipUdDaElab", cascade = CascadeType.REMOVE)
    public List<AroUpdUdIndiceAipDaElab> getAroUpdUdIndiceAipDaElabs() {
	return this.aroUpdUdIndiceAipDaElabs;
    }

    public void setAroUpdUdIndiceAipDaElabs(
	    List<AroUpdUdIndiceAipDaElab> aroUpdUdIndiceAipDaElabs) {
	this.aroUpdUdIndiceAipDaElabs = aroUpdUdIndiceAipDaElabs;
    }

    public AroUpdUdIndiceAipDaElab addAroUpdUdIndiceAipDaElab(
	    AroUpdUdIndiceAipDaElab aroUpdUdIndiceAipDaElab) {
	getAroUpdUdIndiceAipDaElabs().add(aroUpdUdIndiceAipDaElab);
	aroUpdUdIndiceAipDaElab.setAroIndiceAipUdDaElab(this);
	return aroUpdUdIndiceAipDaElab;
    }

    public AroUpdUdIndiceAipDaElab removeAroUpdUdIndiceAipDaElab(
	    AroUpdUdIndiceAipDaElab aroUpdUdIndiceAipDaElab) {
	getAroUpdUdIndiceAipDaElabs().remove(aroUpdUdIndiceAipDaElab);
	aroUpdUdIndiceAipDaElab.setAroIndiceAipUdDaElab(null);
	return aroUpdUdIndiceAipDaElab;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
	return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
	this.aroUnitaDoc = aroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVer() {
	return this.elvElencoVer;
    }

    public void setElvElencoVer(ElvElencoVer elvElencoVer) {
	this.elvElencoVer = elvElencoVer;
    }

    @Column(name = "FL_IN_CODA", columnDefinition = "char(1)")
    public String getFlInCoda() {
	return flInCoda;
    }

    public void setFlInCoda(String flInCoda) {
	this.flInCoda = flInCoda;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_IN_CODA")
    public Date getTsInCoda() {
	return tsInCoda;
    }

    public void setTsInCoda(Date tsInCoda) {
	this.tsInCoda = tsInCoda;
    }
}
