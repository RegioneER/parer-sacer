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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Cacheable;
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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_DOC database table.
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_TIPO_DOC")
public class DecTipoDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoDoc;

    private String dlNoteTipoDoc;

    private String dsPeriodicitaVers;

    private String dsTipoDoc;

    private Date dtIstituz;

    private Date dtSoppres;

    private String flTipoDocPrincipale;

    private String nmTipoDoc;

    private List<AroDoc> aroDocs = new ArrayList<>();

    private List<DecAttribDatiSpec> decAttribDatiSpecs = new ArrayList<>();

    private List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiplos = new ArrayList<>();

    private List<DecFiltroSelUd> decFiltroSelUds = new ArrayList<>();

    private OrgStrut orgStrut;
    private List<VrsSesUpdUnitaDocKo> vrsSesUpdUnitaDocKos;
    private List<VrsUpdUnitaDocKo> vrsUpdUnitaDocKos;

    private List<DecTipoDocAmmesso> decTipoDocAmmessos = new ArrayList<>();

    private List<DecTipoStrutDocAmmesso> decTipoStrutDocAmmessos = new ArrayList<>();

    private List<DecXsdDatiSpec> decXsdDatiSpecs = new ArrayList<>();

    private List<MonContaUdDocComp> monContaUdDocComps = new ArrayList<>();

    private List<OrgRegolaValSubStrut> orgRegolaValSubStruts = new ArrayList<>();

    private List<DecModelloTipoSerie> decModelloTipoSeries = new ArrayList<>();
    private List<DecUsoModelloXsdDoc> decUsoModelloXsdDocs = new ArrayList<>();

    public DecTipoDoc() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_DOC")
    @XmlID
    @GenericGenerator(name = "SDEC_TIPO_DOC_ID_TIPO_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_DOC"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_DOC_ID_TIPO_DOC_GENERATOR")
    public Long getIdTipoDoc() {
	return this.idTipoDoc;
    }

    public void setIdTipoDoc(Long idTipoDoc) {
	this.idTipoDoc = idTipoDoc;
    }

    @Column(name = "DL_NOTE_TIPO_DOC")
    public String getDlNoteTipoDoc() {
	return this.dlNoteTipoDoc;
    }

    public void setDlNoteTipoDoc(String dlNoteTipoDoc) {
	this.dlNoteTipoDoc = dlNoteTipoDoc;
    }

    @Column(name = "DS_PERIODICITA_VERS")
    public String getDsPeriodicitaVers() {
	return this.dsPeriodicitaVers;
    }

    public void setDsPeriodicitaVers(String dsPeriodicitaVers) {
	this.dsPeriodicitaVers = dsPeriodicitaVers;
    }

    @Column(name = "DS_TIPO_DOC")
    public String getDsTipoDoc() {
	return this.dsTipoDoc;
    }

    public void setDsTipoDoc(String dsTipoDoc) {
	this.dsTipoDoc = dsTipoDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
	return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
	this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
	return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
	this.dtSoppres = dtSoppres;
    }

    @Column(name = "FL_TIPO_DOC_PRINCIPALE", columnDefinition = "char(1)")
    public String getFlTipoDocPrincipale() {
	return this.flTipoDocPrincipale;
    }

    public void setFlTipoDocPrincipale(String flTipoDocPrincipale) {
	this.flTipoDocPrincipale = flTipoDocPrincipale;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
	return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
	this.nmTipoDoc = nmTipoDoc;
    }

    @OneToMany(mappedBy = "decTipoDoc")
    @XmlTransient
    public List<AroDoc> getAroDocs() {
	return this.aroDocs;
    }

    public void setAroDocs(List<AroDoc> aroDocs) {
	this.aroDocs = aroDocs;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<DecAttribDatiSpec> getDecAttribDatiSpecs() {
	return this.decAttribDatiSpecs;
    }

    public void setDecAttribDatiSpecs(List<DecAttribDatiSpec> decAttribDatiSpecs) {
	this.decAttribDatiSpecs = decAttribDatiSpecs;
    }

    @OneToMany(mappedBy = "decTipoDoc")
    @XmlIDREF
    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiplos() {
	return this.decCriterioFiltroMultiplos;
    }

    public void setDecCriterioFiltroMultiplos(
	    List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiplos) {
	this.decCriterioFiltroMultiplos = decCriterioFiltroMultiplos;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    @XmlInverseReference(mappedBy = "decTipoDocs")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = CascadeType.PERSIST)
    public List<DecTipoDocAmmesso> getDecTipoDocAmmessos() {
	return this.decTipoDocAmmessos;
    }

    public void setDecTipoDocAmmessos(List<DecTipoDocAmmesso> decTipoDocAmmessos) {
	this.decTipoDocAmmessos = decTipoDocAmmessos;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.REMOVE })
    public List<DecTipoStrutDocAmmesso> getDecTipoStrutDocAmmessos() {
	return this.decTipoStrutDocAmmessos;
    }

    public void setDecTipoStrutDocAmmessos(List<DecTipoStrutDocAmmesso> decTipoStrutDocAmmessos) {
	this.decTipoStrutDocAmmessos = decTipoStrutDocAmmessos;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<DecXsdDatiSpec> getDecXsdDatiSpecs() {
	return this.decXsdDatiSpecs;
    }

    public void setDecXsdDatiSpecs(List<DecXsdDatiSpec> decXsdDatiSpecs) {
	this.decXsdDatiSpecs = decXsdDatiSpecs;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = {
	    CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<OrgRegolaValSubStrut> getOrgRegolaValSubStruts() {
	return this.orgRegolaValSubStruts;
    }

    public void setOrgRegolaValSubStruts(List<OrgRegolaValSubStrut> orgRegolaValSubStruts) {
	this.orgRegolaValSubStruts = orgRegolaValSubStruts;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = CascadeType.REMOVE)
    @XmlTransient
    public List<DecFiltroSelUd> getDecFiltroSelUds() {
	return this.decFiltroSelUds;
    }

    public void setDecFiltroSelUds(List<DecFiltroSelUd> decFiltroSelUds) {
	this.decFiltroSelUds = decFiltroSelUds;
    }

    @OneToMany(mappedBy = "decTipoDoc")
    @XmlTransient
    public List<MonContaUdDocComp> getMonContaUdDocComps() {
	return this.monContaUdDocComps;
    }

    public void setMonContaUdDocComps(List<MonContaUdDocComp> monContaUdDocComps) {
	this.monContaUdDocComps = monContaUdDocComps;
    }

    @OneToMany(mappedBy = "decTipoDoc")
    @XmlTransient
    public List<DecModelloTipoSerie> getDecModelloTipoSeries() {
	return this.decModelloTipoSeries;
    }

    public void setDecModelloTipoSeries(List<DecModelloTipoSerie> decModelloTipoSeries) {
	this.decModelloTipoSeries = decModelloTipoSeries;
    }

    public DecModelloTipoSerie addDecModelloTipoSery(DecModelloTipoSerie decModelloTipoSery) {
	getDecModelloTipoSeries().add(decModelloTipoSery);
	decModelloTipoSery.setDecTipoDoc(this);
	return decModelloTipoSery;
    }

    public DecModelloTipoSerie removeDecModelloTipoSery(DecModelloTipoSerie decModelloTipoSery) {
	getDecModelloTipoSeries().remove(decModelloTipoSery);
	decModelloTipoSery.setDecTipoDoc(null);
	return decModelloTipoSery;
    }

    @OneToMany(mappedBy = "decTipoDoc", cascade = CascadeType.PERSIST)
    public List<DecUsoModelloXsdDoc> getDecUsoModelloXsdDocs() {
	return this.decUsoModelloXsdDocs;
    }

    public void setDecUsoModelloXsdDocs(List<DecUsoModelloXsdDoc> decUsoModelloXsdDocs) {
	this.decUsoModelloXsdDocs = decUsoModelloXsdDocs;
    }

    @OneToMany(mappedBy = "decTipoDocPrinc")
    @XmlTransient
    public List<VrsSesUpdUnitaDocKo> getVrsSesUpdUnitaDocKos() {
	return this.vrsSesUpdUnitaDocKos;
    }

    public void setVrsSesUpdUnitaDocKos(List<VrsSesUpdUnitaDocKo> vrsSesUpdUnitaDocKos) {
	this.vrsSesUpdUnitaDocKos = vrsSesUpdUnitaDocKos;
    }

    @OneToMany(mappedBy = "decTipoDocPrincLast")
    @XmlTransient
    public List<VrsUpdUnitaDocKo> getVrsUpdUnitaDocKos() {
	return this.vrsUpdUnitaDocKos;
    }

    public void setVrsUpdUnitaDocKos(List<VrsUpdUnitaDocKo> vrsUpdUnitaDocKos) {
	this.vrsUpdUnitaDocKos = vrsUpdUnitaDocKos;
    }

}
