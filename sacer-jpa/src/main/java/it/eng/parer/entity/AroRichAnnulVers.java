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
 * The persistent class for the ARO_RICH_ANNUL_VERS database table.
 */
@Entity
@Table(name = "ARO_RICH_ANNUL_VERS")

public class AroRichAnnulVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idRichAnnulVers;

    private String cdRichAnnulVers;

    private String dsRichAnnulVers;

    private Date dtCreazioneRichAnnulVers;

    private String flForzaAnnul;

    private String flImmediata;

    private String flRichPing;

    private BigDecimal idStatoRichAnnulVersCor;

    private String ntRichAnnulVers;

    private String tiCreazioneRichAnnulVers;

    private String tiRichAnnulVers;

    private List<AroFileRichAnnulVers> aroFileRichAnnulVers = new ArrayList<>();

    private List<AroItemRichAnnulVers> aroItemRichAnnulVers = new ArrayList<>();

    private OrgStrut orgStrut;

    private List<AroStatoRichAnnulVers> aroStatoRichAnnulVers = new ArrayList<>();

    private List<AroXmlRichAnnulVers> aroXmlRichAnnulVers = new ArrayList<>();

    private String tiAnnullamento;

    public AroRichAnnulVers() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_RICH_ANNUL_VERS")
    @GenericGenerator(name = "SARO_RICH_ANNUL_VERS_ID_RICH_ANNUL_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_RICH_ANNUL_VERS"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_RICH_ANNUL_VERS_ID_RICH_ANNUL_VERS_GENERATOR")
    public Long getIdRichAnnulVers() {
	return this.idRichAnnulVers;
    }

    public void setIdRichAnnulVers(Long idRichAnnulVers) {
	this.idRichAnnulVers = idRichAnnulVers;
    }

    @Column(name = "CD_RICH_ANNUL_VERS")
    public String getCdRichAnnulVers() {
	return this.cdRichAnnulVers;
    }

    public void setCdRichAnnulVers(String cdRichAnnulVers) {
	this.cdRichAnnulVers = cdRichAnnulVers;
    }

    @Column(name = "DS_RICH_ANNUL_VERS")
    public String getDsRichAnnulVers() {
	return this.dsRichAnnulVers;
    }

    public void setDsRichAnnulVers(String dsRichAnnulVers) {
	this.dsRichAnnulVers = dsRichAnnulVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_RICH_ANNUL_VERS")
    public Date getDtCreazioneRichAnnulVers() {
	return this.dtCreazioneRichAnnulVers;
    }

    public void setDtCreazioneRichAnnulVers(Date dtCreazioneRichAnnulVers) {
	this.dtCreazioneRichAnnulVers = dtCreazioneRichAnnulVers;
    }

    @Column(name = "FL_FORZA_ANNUL", columnDefinition = "char(1)")
    public String getFlForzaAnnul() {
	return this.flForzaAnnul;
    }

    public void setFlForzaAnnul(String flForzaAnnul) {
	this.flForzaAnnul = flForzaAnnul;
    }

    @Column(name = "FL_IMMEDIATA", columnDefinition = "char(1)")
    public String getFlImmediata() {
	return this.flImmediata;
    }

    public void setFlImmediata(String flImmediata) {
	this.flImmediata = flImmediata;
    }

    @Column(name = "FL_RICH_PING", columnDefinition = "char(1)")
    public String getFlRichPing() {
	return this.flRichPing;
    }

    public void setFlRichPing(String flRichPing) {
	this.flRichPing = flRichPing;
    }

    @Column(name = "ID_STATO_RICH_ANNUL_VERS_COR")
    public BigDecimal getIdStatoRichAnnulVersCor() {
	return this.idStatoRichAnnulVersCor;
    }

    public void setIdStatoRichAnnulVersCor(BigDecimal idStatoRichAnnulVersCor) {
	this.idStatoRichAnnulVersCor = idStatoRichAnnulVersCor;
    }

    @Column(name = "NT_RICH_ANNUL_VERS")
    public String getNtRichAnnulVers() {
	return this.ntRichAnnulVers;
    }

    public void setNtRichAnnulVers(String ntRichAnnulVers) {
	this.ntRichAnnulVers = ntRichAnnulVers;
    }

    @Column(name = "TI_CREAZIONE_RICH_ANNUL_VERS")
    public String getTiCreazioneRichAnnulVers() {
	return this.tiCreazioneRichAnnulVers;
    }

    public void setTiCreazioneRichAnnulVers(String tiCreazioneRichAnnulVers) {
	this.tiCreazioneRichAnnulVers = tiCreazioneRichAnnulVers;
    }

    @Column(name = "TI_RICH_ANNUL_VERS")
    public String getTiRichAnnulVers() {
	return this.tiRichAnnulVers;
    }

    public void setTiRichAnnulVers(String tiRichAnnulVers) {
	this.tiRichAnnulVers = tiRichAnnulVers;
    }

    @OneToMany(mappedBy = "aroRichAnnulVers", cascade = CascadeType.PERSIST)
    public List<AroFileRichAnnulVers> getAroFileRichAnnulVers() {
	return this.aroFileRichAnnulVers;
    }

    public void setAroFileRichAnnulVers(List<AroFileRichAnnulVers> aroFileRichAnnulVers) {
	this.aroFileRichAnnulVers = aroFileRichAnnulVers;
    }

    public AroFileRichAnnulVers addAroFileRichAnnulVer(AroFileRichAnnulVers aroFileRichAnnulVers) {
	getAroFileRichAnnulVers().add(aroFileRichAnnulVers);
	aroFileRichAnnulVers.setAroRichAnnulVers(this);
	return aroFileRichAnnulVers;
    }

    public AroFileRichAnnulVers removeAroFileRichAnnulVer(
	    AroFileRichAnnulVers aroFileRichAnnulVers) {
	getAroFileRichAnnulVers().remove(aroFileRichAnnulVers);
	aroFileRichAnnulVers.setAroRichAnnulVers(null);
	return aroFileRichAnnulVers;
    }

    @OneToMany(mappedBy = "aroRichAnnulVers", cascade = CascadeType.PERSIST)
    public List<AroItemRichAnnulVers> getAroItemRichAnnulVers() {
	return this.aroItemRichAnnulVers;
    }

    public void setAroItemRichAnnulVers(List<AroItemRichAnnulVers> aroItemRichAnnulVers) {
	this.aroItemRichAnnulVers = aroItemRichAnnulVers;
    }

    public AroItemRichAnnulVers addAroItemRichAnnulVers(AroItemRichAnnulVers aroItemRichAnnulVers) {
	getAroItemRichAnnulVers().add(aroItemRichAnnulVers);
	aroItemRichAnnulVers.setAroRichAnnulVers(this);
	return aroItemRichAnnulVers;
    }

    public AroItemRichAnnulVers removeAroItemRichAnnulVer(
	    AroItemRichAnnulVers aroItemRichAnnulVers) {
	getAroItemRichAnnulVers().remove(aroItemRichAnnulVers);
	aroItemRichAnnulVers.setAroRichAnnulVers(null);
	return aroItemRichAnnulVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "aroRichAnnulVers", cascade = CascadeType.PERSIST)
    public List<AroStatoRichAnnulVers> getAroStatoRichAnnulVers() {
	return this.aroStatoRichAnnulVers;
    }

    public void setAroStatoRichAnnulVers(List<AroStatoRichAnnulVers> aroStatoRichAnnulVers) {
	this.aroStatoRichAnnulVers = aroStatoRichAnnulVers;
    }

    public AroStatoRichAnnulVers addAroStatoRichAnnulVers(
	    AroStatoRichAnnulVers aroStatoRichAnnulVer) {
	getAroStatoRichAnnulVers().add(aroStatoRichAnnulVer);
	aroStatoRichAnnulVer.setAroRichAnnulVers(this);
	return aroStatoRichAnnulVer;
    }

    public AroStatoRichAnnulVers removeAroStatoRichAnnulVers(
	    AroStatoRichAnnulVers aroStatoRichAnnulVer) {
	getAroStatoRichAnnulVers().remove(aroStatoRichAnnulVer);
	aroStatoRichAnnulVer.setAroRichAnnulVers(null);
	return aroStatoRichAnnulVer;
    }

    @OneToMany(mappedBy = "aroRichAnnulVers", cascade = CascadeType.PERSIST)
    public List<AroXmlRichAnnulVers> getAroXmlRichAnnulVers() {
	return this.aroXmlRichAnnulVers;
    }

    public void setAroXmlRichAnnulVers(List<AroXmlRichAnnulVers> aroXmlRichAnnulVers) {
	this.aroXmlRichAnnulVers = aroXmlRichAnnulVers;
    }

    public AroXmlRichAnnulVers addAroXmlRichAnnulVers(AroXmlRichAnnulVers aroXmlRichAnnulVers) {
	getAroXmlRichAnnulVers().add(aroXmlRichAnnulVers);
	aroXmlRichAnnulVers.setAroRichAnnulVers(this);
	return aroXmlRichAnnulVers;
    }

    public AroXmlRichAnnulVers removeAroXmlRichAnnulVers(AroXmlRichAnnulVers aroXmlRichAnnulVers) {
	getAroXmlRichAnnulVers().remove(aroXmlRichAnnulVers);
	aroXmlRichAnnulVers.setAroRichAnnulVers(null);
	return aroXmlRichAnnulVers;
    }

    @Column(name = "TI_ANNULLAMENTO")
    public String getTiAnnullamento() {
	return this.tiAnnullamento;
    }

    public void setTiAnnullamento(String tiAnnullamento) {
	this.tiAnnullamento = tiAnnullamento;
    }

}
