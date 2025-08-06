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
 * The persistent class for the FAS_VER_AIP_FASCICOLO database table.
 */
@Entity
@Table(name = "FAS_VER_AIP_FASCICOLO")
public class FasVerAipFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVerAipFascicolo;

    private String cdVerAip;

    private String dsCausale;

    private Date dtCreazione;

    private BigDecimal pgVerAipFascicolo;

    private List<FasContenVerAipFascicolo> fasContenVerAipFascicolos = new ArrayList<>();

    private List<FasMetaVerAipFascicolo> fasMetaVerAipFascicolos = new ArrayList<>();

    private List<FasSipVerAipFascicolo> fasSipVerAipFascicolos = new ArrayList<>();

    private FasFascicolo fasFascicolo;

    private ElvElencoVersFasc elvElencoVersFasc;

    private BigDecimal idEnteConserv;

    private String dsUrnAipFascicolo;

    private String dsUrnNormalizAipFascicolo;

    public FasVerAipFascicolo() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_VER_AIP_FASCICOLO")
    @GenericGenerator(name = "SFAS_VER_AIP_FASCICOLO_ID_VER_AIP_FASCICOLO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SFAS_VER_AIP_FASCICOLO"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SFAS_VER_AIP_FASCICOLO_ID_VER_AIP_FASCICOLO_GENERATOR")
    public Long getIdVerAipFascicolo() {
	return this.idVerAipFascicolo;
    }

    public void setIdVerAipFascicolo(Long idVerAipFascicolo) {
	this.idVerAipFascicolo = idVerAipFascicolo;
    }

    @Column(name = "CD_VER_AIP")
    public String getCdVerAip() {
	return this.cdVerAip;
    }

    public void setCdVerAip(String cdVerAip) {
	this.cdVerAip = cdVerAip;
    }

    @Column(name = "DS_CAUSALE")
    public String getDsCausale() {
	return this.dsCausale;
    }

    public void setDsCausale(String dsCausale) {
	this.dsCausale = dsCausale;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
	return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
	this.dtCreazione = dtCreazione;
    }

    @Column(name = "PG_VER_AIP_FASCICOLO")
    public BigDecimal getPgVerAipFascicolo() {
	return this.pgVerAipFascicolo;
    }

    public void setPgVerAipFascicolo(BigDecimal pgVerAipFascicolo) {
	this.pgVerAipFascicolo = pgVerAipFascicolo;
    }

    @OneToMany(mappedBy = "fasVerAipFascicolo", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<FasContenVerAipFascicolo> getFasContenVerAipFascicolos() {
	return this.fasContenVerAipFascicolos;
    }

    public void setFasContenVerAipFascicolos(
	    List<FasContenVerAipFascicolo> fasContenVerAipFascicolos) {
	this.fasContenVerAipFascicolos = fasContenVerAipFascicolos;
    }

    @OneToMany(mappedBy = "fasVerAipFascicolo", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<FasMetaVerAipFascicolo> getFasMetaVerAipFascicolos() {
	return this.fasMetaVerAipFascicolos;
    }

    public void setFasMetaVerAipFascicolos(List<FasMetaVerAipFascicolo> fasMetaVerAipFascicolos) {
	this.fasMetaVerAipFascicolos = fasMetaVerAipFascicolos;
    }

    @OneToMany(mappedBy = "fasVerAipFascicolo", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH })
    public List<FasSipVerAipFascicolo> getFasSipVerAipFascicolos() {
	return this.fasSipVerAipFascicolos;
    }

    public void setFasSipVerAipFascicolos(List<FasSipVerAipFascicolo> fasSipVerAipFascicolos) {
	this.fasSipVerAipFascicolos = fasSipVerAipFascicolos;
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

    @Column(name = "ID_ENTE_CONSERV")
    public BigDecimal getIdEnteConserv() {
	return this.idEnteConserv;
    }

    public void setIdEnteConserv(BigDecimal idEnteConserv) {
	this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "DS_URN_AIP_FASCICOLO")
    public String getDsUrnAipFascicolo() {
	return this.dsUrnAipFascicolo;
    }

    public void setDsUrnAipFascicolo(String dsUrnAipFascicolo) {
	this.dsUrnAipFascicolo = dsUrnAipFascicolo;
    }

    @Column(name = "DS_URN_NORMALIZ_AIP_FASCICOLO")
    public String getDsUrnNormalizAipFascicolo() {
	return this.dsUrnNormalizAipFascicolo;
    }

    public void setDsUrnNormalizAipFascicolo(String dsUrnNormalizAipFascicolo) {
	this.dsUrnNormalizAipFascicolo = dsUrnNormalizAipFascicolo;
    }
}
