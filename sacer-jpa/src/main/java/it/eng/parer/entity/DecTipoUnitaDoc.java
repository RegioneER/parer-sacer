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
import javax.persistence.PrePersist;
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
 * The persistent class for the DEC_TIPO_UNITA_DOC database table.
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_TIPO_UNITA_DOC")
public class DecTipoUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoUnitaDoc;

    private String cdSerie;

    private String cdSerieDaCreare;

    private String dlNoteTipoUd;

    private String dsSerieDaCreare;

    private String dsTipoSerieDaCreare;

    private String dsTipoUnitaDoc;

    private Date dtIstituz;

    private Date dtSoppres;

    private String flCreaTipoSerieStandard;

    private String flVersManuale;

    private String nmTipoSerieDaCreare;

    private String nmTipoUnitaDoc;

    private String tiSaveFile;

    private List<AroUnitaDoc> aroUnitaDocs = new ArrayList<>();

    private List<DecAttribDatiSpec> decAttribDatiSpecs = new ArrayList<>();

    private List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiplos = new ArrayList<>();

    private List<DecTipoSerieUd> decTipoSerieUds = new ArrayList<>();

    private List<DecTipoStrutUnitaDoc> decTipoStrutUnitaDocs = new ArrayList<>();

    private DecCategTipoUnitaDoc decCategTipoUnitaDoc;

    private OrgStrut orgStrut;

    private List<DecTipoUnitaDocAmmesso> decTipoUnitaDocAmmessos = new ArrayList<>();

    private List<DecXsdDatiSpec> decXsdDatiSpecs = new ArrayList<>();

    private List<MonContaUdDocComp> monContaUdDocComps = new ArrayList<>();

    private List<OrgRegolaValSubStrut> orgRegolaValSubStruts = new ArrayList<>();

    private List<DecModelloTipoSerie> decModelloTipoSeries = new ArrayList<>();

    private DecModelloTipoSerie decModelloTipoSerie;

    private OrgTipoServizio orgTipoServizio;

    private OrgTipoServizio orgTipoServizioAttiv;

    private OrgTipoServizio orgTipoServAttivTipoUd;

    private OrgTipoServizio orgTipoServConservTipoUd;

    private List<MonTipoUnitaDocUserVers> monTipoUnitaDocUserVers = new ArrayList<>();

    private List<AplValoreParamApplic> aplValoreParamApplics = new ArrayList<>();
    private List<DecUsoModelloXsdUniDoc> decUsoModelloXsdUniDocs = new ArrayList<>();

    private BigDecimal niAaConserv;

    private String flConservUniforme;

    private String flConservIllimitata;

    public DecTipoUnitaDoc() {
        // hibernate
    }

    @Id

    @Column(name = "ID_TIPO_UNITA_DOC")
    @XmlID
    @GenericGenerator(name = "SDEC_TIPO_UNITA_DOC_ID_TIPO_UNITA_DOC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_UNITA_DOC"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_UNITA_DOC_ID_TIPO_UNITA_DOC_GENERATOR")
    public Long getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(Long idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "CD_SERIE")
    public String getCdSerie() {
        return this.cdSerie;
    }

    public void setCdSerie(String cdSerie) {
        this.cdSerie = cdSerie;
    }

    @Column(name = "CD_SERIE_DA_CREARE")
    public String getCdSerieDaCreare() {
        return this.cdSerieDaCreare;
    }

    public void setCdSerieDaCreare(String cdSerieDaCreare) {
        this.cdSerieDaCreare = cdSerieDaCreare;
    }

    @Column(name = "DL_NOTE_TIPO_UD")
    public String getDlNoteTipoUd() {
        return this.dlNoteTipoUd;
    }

    public void setDlNoteTipoUd(String dlNoteTipoUd) {
        this.dlNoteTipoUd = dlNoteTipoUd;
    }

    @Column(name = "DS_SERIE_DA_CREARE")
    public String getDsSerieDaCreare() {
        return this.dsSerieDaCreare;
    }

    public void setDsSerieDaCreare(String dsSerieDaCreare) {
        this.dsSerieDaCreare = dsSerieDaCreare;
    }

    @Column(name = "DS_TIPO_SERIE_DA_CREARE")
    public String getDsTipoSerieDaCreare() {
        return this.dsTipoSerieDaCreare;
    }

    public void setDsTipoSerieDaCreare(String dsTipoSerieDaCreare) {
        this.dsTipoSerieDaCreare = dsTipoSerieDaCreare;
    }

    @Column(name = "DS_TIPO_UNITA_DOC")
    public String getDsTipoUnitaDoc() {
        return this.dsTipoUnitaDoc;
    }

    public void setDsTipoUnitaDoc(String dsTipoUnitaDoc) {
        this.dsTipoUnitaDoc = dsTipoUnitaDoc;
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

    @Column(name = "FL_CREA_TIPO_SERIE_STANDARD")
    public String getFlCreaTipoSerieStandard() {
        return this.flCreaTipoSerieStandard;
    }

    public void setFlCreaTipoSerieStandard(String flCreaTipoSerieStandard) {
        this.flCreaTipoSerieStandard = flCreaTipoSerieStandard;
    }

    @Column(name = "FL_VERS_MANUALE", columnDefinition = "char(1)")
    public String getFlVersManuale() {
        return this.flVersManuale;
    }

    public void setFlVersManuale(String flVersManuale) {
        this.flVersManuale = flVersManuale;
    }

    @Column(name = "NM_TIPO_SERIE_DA_CREARE")
    public String getNmTipoSerieDaCreare() {
        return this.nmTipoSerieDaCreare;
    }

    public void setNmTipoSerieDaCreare(String nmTipoSerieDaCreare) {
        this.nmTipoSerieDaCreare = nmTipoSerieDaCreare;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "TI_SAVE_FILE")
    public String getTiSaveFile() {
        return this.tiSaveFile;
    }

    public void setTiSaveFile(String tiSaveFile) {
        this.tiSaveFile = tiSaveFile;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc")
    @XmlTransient
    public List<AroUnitaDoc> getAroUnitaDocs() {
        return this.aroUnitaDocs;
    }

    public void setAroUnitaDocs(List<AroUnitaDoc> aroUnitaDocs) {
        this.aroUnitaDocs = aroUnitaDocs;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<DecAttribDatiSpec> getDecAttribDatiSpecs() {
        return this.decAttribDatiSpecs;
    }

    public void setDecAttribDatiSpecs(List<DecAttribDatiSpec> decAttribDatiSpecs) {
        this.decAttribDatiSpecs = decAttribDatiSpecs;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc")
    @XmlIDREF
    public List<DecCriterioFiltroMultiplo> getDecCriterioFiltroMultiplos() {
        return this.decCriterioFiltroMultiplos;
    }

    public void setDecCriterioFiltroMultiplos(
            List<DecCriterioFiltroMultiplo> decCriterioFiltroMultiplos) {
        this.decCriterioFiltroMultiplos = decCriterioFiltroMultiplos;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = CascadeType.REMOVE)
    @XmlTransient
    public List<DecTipoSerieUd> getDecTipoSerieUds() {
        return this.decTipoSerieUds;
    }

    public void setDecTipoSerieUds(List<DecTipoSerieUd> decTipoSerieUds) {
        this.decTipoSerieUds = decTipoSerieUds;
    }

    public DecTipoSerieUd addDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        getDecTipoSerieUds().add(decTipoSerieUd);
        decTipoSerieUd.setDecTipoUnitaDoc(this);
        return decTipoSerieUd;
    }

    public DecTipoSerieUd removeDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        getDecTipoSerieUds().remove(decTipoSerieUd);
        decTipoSerieUd.setDecTipoUnitaDoc(null);
        return decTipoSerieUd;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    public List<DecTipoStrutUnitaDoc> getDecTipoStrutUnitaDocs() {
        return this.decTipoStrutUnitaDocs;
    }

    public void setDecTipoStrutUnitaDocs(List<DecTipoStrutUnitaDoc> decTipoStrutUnitaDocs) {
        this.decTipoStrutUnitaDocs = decTipoStrutUnitaDocs;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEG_TIPO_UNITA_DOC")
    public DecCategTipoUnitaDoc getDecCategTipoUnitaDoc() {
        return this.decCategTipoUnitaDoc;
    }

    public void setDecCategTipoUnitaDoc(DecCategTipoUnitaDoc decCategTipoUnitaDoc) {
        this.decCategTipoUnitaDoc = decCategTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    @XmlInverseReference(mappedBy = "decTipoUnitaDocs")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<DecTipoUnitaDocAmmesso> getDecTipoUnitaDocAmmessos() {
        return this.decTipoUnitaDocAmmessos;
    }

    public void setDecTipoUnitaDocAmmessos(List<DecTipoUnitaDocAmmesso> decTipoUnitaDocAmmessos) {
        this.decTipoUnitaDocAmmessos = decTipoUnitaDocAmmessos;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlIDREF
    public List<DecXsdDatiSpec> getDecXsdDatiSpecs() {
        return this.decXsdDatiSpecs;
    }

    public void setDecXsdDatiSpecs(List<DecXsdDatiSpec> decXsdDatiSpecs) {
        this.decXsdDatiSpecs = decXsdDatiSpecs;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc")
    @XmlTransient
    public List<MonContaUdDocComp> getMonContaUdDocComps() {
        return this.monContaUdDocComps;
    }

    public void setMonContaUdDocComps(List<MonContaUdDocComp> monContaUdDocComps) {
        this.monContaUdDocComps = monContaUdDocComps;
    }

    public MonContaUdDocComp addMonContaUdDocComp(MonContaUdDocComp monContaUdDocComp) {
        getMonContaUdDocComps().add(monContaUdDocComp);
        monContaUdDocComp.setDecTipoUnitaDoc(this);
        return monContaUdDocComp;
    }

    public MonContaUdDocComp removeMonContaUdDocComp(MonContaUdDocComp monContaUdDocComp) {
        getMonContaUdDocComps().remove(monContaUdDocComp);
        monContaUdDocComp.setDecTipoUnitaDoc(null);
        return monContaUdDocComp;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    public List<OrgRegolaValSubStrut> getOrgRegolaValSubStruts() {
        return this.orgRegolaValSubStruts;
    }

    public void setOrgRegolaValSubStruts(List<OrgRegolaValSubStrut> orgRegolaValSubStruts) {
        this.orgRegolaValSubStruts = orgRegolaValSubStruts;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc")
    @XmlTransient
    public List<DecModelloTipoSerie> getDecModelloTipoSeries() {
        return this.decModelloTipoSeries;
    }

    public void setDecModelloTipoSeries(List<DecModelloTipoSerie> decModelloTipoSeries) {
        this.decModelloTipoSeries = decModelloTipoSeries;
    }

    public DecModelloTipoSerie addDecModelloTipoSery(DecModelloTipoSerie decModelloTipoSery) {
        getDecModelloTipoSeries().add(decModelloTipoSery);
        decModelloTipoSery.setDecTipoUnitaDoc(this);
        return decModelloTipoSery;
    }

    public DecModelloTipoSerie removeDecModelloTipoSery(DecModelloTipoSerie decModelloTipoSery) {
        getDecModelloTipoSeries().remove(decModelloTipoSery);
        decModelloTipoSery.setDecTipoUnitaDoc(null);
        return decModelloTipoSery;
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
    @JoinColumn(name = "ID_TIPO_SERVIZIO")
    public OrgTipoServizio getOrgTipoServizio() {
        return this.orgTipoServizio;
    }

    public void setOrgTipoServizio(OrgTipoServizio orgTipoServizio) {
        this.orgTipoServizio = orgTipoServizio;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERVIZIO_ATTIV")
    public OrgTipoServizio getOrgTipoServizioAttiv() {
        return orgTipoServizioAttiv;
    }

    public void setOrgTipoServizioAttiv(OrgTipoServizio orgTipoServizioAttiv) {
        this.orgTipoServizioAttiv = orgTipoServizioAttiv;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERV_ATTIV_TIPO_UD")
    public OrgTipoServizio getOrgTipoServAttivTipoUd() {
        return this.orgTipoServAttivTipoUd;
    }

    public void setOrgTipoServAttivTipoUd(OrgTipoServizio orgTipoServAttivTipoUd) {
        this.orgTipoServAttivTipoUd = orgTipoServAttivTipoUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERV_CONSERV_TIPO_UD")
    public OrgTipoServizio getOrgTipoServConservTipoUd() {
        return orgTipoServConservTipoUd;
    }

    public void setOrgTipoServConservTipoUd(OrgTipoServizio orgTipoServConservTipoUd) {
        this.orgTipoServConservTipoUd = orgTipoServConservTipoUd;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc")
    @XmlTransient
    public List<MonTipoUnitaDocUserVers> getMonTipoUnitaDocUserVers() {
        return this.monTipoUnitaDocUserVers;
    }

    public void setMonTipoUnitaDocUserVers(List<MonTipoUnitaDocUserVers> monTipoUnitaDocUserVers) {
        this.monTipoUnitaDocUserVers = monTipoUnitaDocUserVers;
    }

    public MonTipoUnitaDocUserVers addMonTipoUnitaDocUserVer(
            MonTipoUnitaDocUserVers monTipoUnitaDocUserVers) {
        getMonTipoUnitaDocUserVers().add(monTipoUnitaDocUserVers);
        monTipoUnitaDocUserVers.setDecTipoUnitaDoc(this);
        return monTipoUnitaDocUserVers;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc")
    public List<AplValoreParamApplic> getAplValoreParamApplics() {
        return this.aplValoreParamApplics;
    }

    public void setAplValoreParamApplics(List<AplValoreParamApplic> aplValoreParamApplics) {
        this.aplValoreParamApplics = aplValoreParamApplics;
    }

    public AplValoreParamApplic addAplValoreParamApplic(AplValoreParamApplic aplValoreParamApplic) {
        getAplValoreParamApplics().add(aplValoreParamApplic);
        aplValoreParamApplic.setDecTipoUnitaDoc(this);
        return aplValoreParamApplic;
    }

    public AplValoreParamApplic removeAplValoreParamApplic(
            AplValoreParamApplic aplValoreParamApplic) {
        getAplValoreParamApplics().remove(aplValoreParamApplic);
        aplValoreParamApplic.setDecTipoUnitaDoc(null);
        return aplValoreParamApplic;
    }

    @OneToMany(mappedBy = "decTipoUnitaDoc", cascade = CascadeType.PERSIST)
    // @XmlTransient
    public List<DecUsoModelloXsdUniDoc> getDecUsoModelloXsdUniDocs() {
        return this.decUsoModelloXsdUniDocs;
    }

    public void setDecUsoModelloXsdUniDocs(List<DecUsoModelloXsdUniDoc> decUsoModelloXsdUniDocs) {
        this.decUsoModelloXsdUniDocs = decUsoModelloXsdUniDocs;
    }

    @Column(name = "NI_AA_CONSERV")
    public BigDecimal getNiAaConserv() {
        return this.niAaConserv;
    }

    public void setNiAaConserv(BigDecimal niAaConserv) {
        this.niAaConserv = niAaConserv;
    }

    @Column(name = "FL_CONSERV_UNIFORME", columnDefinition = "char(1)")
    public String getFlConservUniforme() {
        return this.flConservUniforme;
    }

    public void setFlConservUniforme(String flConservUniforme) {
        this.flConservUniforme = flConservUniforme;
    }

    @Column(name = "FL_CONSERV_ILLIMITATA", columnDefinition = "char(1)")
    public String getFlConservIllimitata() {
        return this.flConservIllimitata;
    }

    public void setFlConservIllimitata(String flConservIllimitata) {
        this.flConservIllimitata = flConservIllimitata;
    }

    /**
     * Gestione dei default. Risulta la migliore pratica in quanto è indipendente dal db utilizzato
     * e sfrutta diretta JPA quindi calabile sotto ogni contesto in termini di ORM
     *
     * ref. https://stackoverflow.com/a/13432234
     */
    @PrePersist
    void preInsert() {
        if (this.flVersManuale == null) {
            this.flVersManuale = "0";
        }
    }
}
