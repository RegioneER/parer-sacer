/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ORG_STRUT database table.
 */
@Entity
@XmlRootElement
@Cacheable(true)
@Table(name = "ORG_STRUT")
public class OrgStrut implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStrut;

    private String cdIpa;

    private String dlNoteStrut;

    private String dsStrut;

    private String cdStrutNormaliz;

    private Date dtIniVal;

    private Date dtFineVal;

    private Date dtIniValStrut;

    private Date dtFineValStrut;

    private String flTemplate;

    private BigDecimal idEnteConvenz;

    private String nmStrut;

    private List<AroRichAnnulVers> aroRichAnnulVers = new ArrayList<>();

    private List<AroUnitaDoc> aroUnitaDocs = new ArrayList<>();

    private List<DecAttribDatiSpec> decAttribDatiSpecs = new ArrayList<>();

    private List<DecCriterioRaggr> decCriterioRaggrs = new ArrayList<>();

    private List<DecCriterioRaggrFasc> decCriterioRaggrFascs = new ArrayList<>();

    private List<DecFormatoFileDoc> decFormatoFileDocs = new ArrayList<>();

    private List<DecRegistroUnitaDoc> decRegistroUnitaDocs = new ArrayList<>();

    private List<DecTipoDoc> decTipoDocs = new ArrayList<>();

    private List<DecTipoRapprComp> decTipoRapprComps = new ArrayList<>();

    private List<DecTipoSerie> decTipoSeries = new ArrayList<>();

    private List<DecTipoStrutDoc> decTipoStrutDocs = new ArrayList<>();

    private List<DecTipoUnitaDoc> decTipoUnitaDocs = new ArrayList<>();

    private List<DecTitol> decTitols = new ArrayList<>();

    private List<DecXsdDatiSpec> decXsdDatiSpecs = new ArrayList<>();

    private List<ElvElencoVer> elvElencoVers = new ArrayList<>();

    private List<ElvLogElencoVer> elvLogElencoVers = new ArrayList<>();

    private List<LogJob> logJobs = new ArrayList<>();

    private List<LogLockElab> logLockElabs = new ArrayList<>();

    private List<LogOper> logOpers = new ArrayList<>();

    private List<OrgOperTitol> orgOperTitols = new ArrayList<>();

    private List<OrgPartitionStrut> orgPartitionStruts = new ArrayList<>();

    private OrgCategStrut orgCategStrut;

    private OrgEnte orgEnte;

    private List<DecTipoFascicolo> decTipoFascicolos = new ArrayList<>();

    private List<OrgSubStrut> orgSubStruts = new ArrayList<>();

    private List<OrgUsoSistemaMigraz> orgUsoSistemaMigrazs = new ArrayList<>();

    private List<SerSerie> serSeries = new ArrayList<>();

    private List<VolVolumeConserv> volVolumeConservs = new ArrayList<>();

    private List<VrsDocNonVer> vrsDocNonVers = new ArrayList<>();

    private List<VrsSessioneVers> vrsSessioneVers = new ArrayList<>();

    private List<VrsUnitaDocNonVer> vrsUnitaDocNonVers = new ArrayList<>();

    private List<DecUsoModelloTipoSerie> decUsoModelloTipoSeries = new ArrayList<>();

    private List<AplValoreParamApplic> aplValoreParamApplics = new ArrayList<>();

    private String flCessato;

    private String flArchivioRestituito;

    public OrgStrut() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_STRUT")
    @GenericGenerator(name = "SORG_STRUT_ID_STRUT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_STRUT"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_STRUT_ID_STRUT_GENERATOR")
    public Long getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(Long idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "CD_IPA")
    public String getCdIpa() {
        return cdIpa;
    }

    public void setCdIpa(String cdIpa) {
        this.cdIpa = cdIpa;
    }

    @Column(name = "DL_NOTE_STRUT")
    public String getDlNoteStrut() {
        return this.dlNoteStrut;
    }

    public void setDlNoteStrut(String dlNoteStrut) {
        this.dlNoteStrut = dlNoteStrut;
    }

    @Column(name = "DS_STRUT")
    public String getDsStrut() {
        return this.dsStrut;
    }

    public void setDsStrut(String dsStrut) {
        this.dsStrut = dsStrut;
    }

    @Column(name = "CD_STRUT_NORMALIZ")
    public String getCdStrutNormaliz() {
        return this.cdStrutNormaliz;
    }

    public void setCdStrutNormaliz(String cdStrutNormaliz) {
        this.cdStrutNormaliz = cdStrutNormaliz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
        return dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
        this.dtFineVal = dtFineVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_STRUT")
    public Date getDtIniValStrut() {
        return dtIniValStrut;
    }

    public void setDtIniValStrut(Date dtIniValStrut) {
        this.dtIniValStrut = dtIniValStrut;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL_STRUT")
    public Date getDtFineValStrut() {
        return dtFineValStrut;
    }

    public void setDtFineValStrut(Date dtFineValStrut) {
        this.dtFineValStrut = dtFineValStrut;
    }

    @Column(name = "FL_TEMPLATE", columnDefinition = "char(1)")
    public String getFlTemplate() {
        return this.flTemplate;
    }

    public void setFlTemplate(String flTemplate) {
        this.flTemplate = flTemplate;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public BigDecimal getIdEnteConvenz() {
        return idEnteConvenz;
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
        this.idEnteConvenz = idEnteConvenz;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "FL_CESSATO", columnDefinition = "char")
    public String getFlCessato() {
        return this.flCessato;
    }

    public void setFlCessato(String flCessato) {
        this.flCessato = flCessato;
    }

    @Column(name = "FL_ARCHIVIO_RESTITUITO", columnDefinition = "char")
    public String getFlArchivioRestituito() {
        return this.flArchivioRestituito;
    }

    public void setFlArchivioRestituito(String flArchivioRestituito) {
        this.flArchivioRestituito = flArchivioRestituito;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<AroRichAnnulVers> getAroRichAnnulVers() {
        return this.aroRichAnnulVers;
    }

    public void setAroRichAnnulVers(List<AroRichAnnulVers> aroRichAnnulVers) {
        this.aroRichAnnulVers = aroRichAnnulVers;
    }

    public AroRichAnnulVers addAroRichAnnulVer(AroRichAnnulVers aroRichAnnulVers) {
        getAroRichAnnulVers().add(aroRichAnnulVers);
        aroRichAnnulVers.setOrgStrut(this);
        return aroRichAnnulVers;
    }

    public AroRichAnnulVers removeAroRichAnnulVer(AroRichAnnulVers aroRichAnnulVers) {
        getAroRichAnnulVers().remove(aroRichAnnulVers);
        aroRichAnnulVers.setOrgStrut(null);
        return aroRichAnnulVers;
    }

    @OneToMany(mappedBy = "orgStrut", fetch = FetchType.LAZY)
    @XmlTransient
    public List<AroUnitaDoc> getAroUnitaDocs() {
        return this.aroUnitaDocs;
    }

    public void setAroUnitaDocs(List<AroUnitaDoc> aroUnitaDocs) {
        this.aroUnitaDocs = aroUnitaDocs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecAttribDatiSpec> getDecAttribDatiSpecs() {
        return this.decAttribDatiSpecs;
    }

    public void setDecAttribDatiSpecs(List<DecAttribDatiSpec> decAttribDatiSpecs) {
        this.decAttribDatiSpecs = decAttribDatiSpecs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecCriterioRaggr> getDecCriterioRaggrs() {
        return this.decCriterioRaggrs;
    }

    public void setDecCriterioRaggrs(List<DecCriterioRaggr> decCriterioRaggrs) {
        this.decCriterioRaggrs = decCriterioRaggrs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecCriterioRaggrFasc> getDecCriterioRaggrFascs() {
        return this.decCriterioRaggrFascs;
    }

    public void setDecCriterioRaggrFascs(List<DecCriterioRaggrFasc> decCriterioRaggrFascs) {
        this.decCriterioRaggrFascs = decCriterioRaggrFascs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    public List<DecFormatoFileDoc> getDecFormatoFileDocs() {
        return this.decFormatoFileDocs;
    }

    public void setDecFormatoFileDocs(List<DecFormatoFileDoc> decFormatoFileDocs) {
        this.decFormatoFileDocs = decFormatoFileDocs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    public List<DecRegistroUnitaDoc> getDecRegistroUnitaDocs() {
        return this.decRegistroUnitaDocs;
    }

    public void setDecRegistroUnitaDocs(List<DecRegistroUnitaDoc> decRegistroUnitaDocs) {
        this.decRegistroUnitaDocs = decRegistroUnitaDocs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecTipoDoc> getDecTipoDocs() {
        return this.decTipoDocs;
    }

    public void setDecTipoDocs(List<DecTipoDoc> decTipoDocs) {
        this.decTipoDocs = decTipoDocs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecTipoRapprComp> getDecTipoRapprComps() {
        return this.decTipoRapprComps;
    }

    public void setDecTipoRapprComps(List<DecTipoRapprComp> decTipoRapprComps) {
        this.decTipoRapprComps = decTipoRapprComps;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<DecTipoSerie> getDecTipoSeries() {
        return this.decTipoSeries;
    }

    public void setDecTipoSeries(List<DecTipoSerie> decTipoSeries) {
        this.decTipoSeries = decTipoSeries;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecTipoStrutDoc> getDecTipoStrutDocs() {
        return this.decTipoStrutDocs;
    }

    public void setDecTipoStrutDocs(List<DecTipoStrutDoc> decTipoStrutDocs) {
        this.decTipoStrutDocs = decTipoStrutDocs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecTipoUnitaDoc> getDecTipoUnitaDocs() {
        return this.decTipoUnitaDocs;
    }

    public void setDecTipoUnitaDocs(List<DecTipoUnitaDoc> decTipoUnitaDocs) {
        this.decTipoUnitaDocs = decTipoUnitaDocs;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    @XmlTransient
    public List<DecTitol> getDecTitols() {
        return this.decTitols;
    }

    public void setDecTitols(List<DecTitol> decTitols) {
        this.decTitols = decTitols;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecXsdDatiSpec> getDecXsdDatiSpecs() {
        return this.decXsdDatiSpecs;
    }

    public void setDecXsdDatiSpecs(List<DecXsdDatiSpec> decXsdDatiSpecs) {
        this.decXsdDatiSpecs = decXsdDatiSpecs;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<ElvElencoVer> getElvElencoVers() {
        return this.elvElencoVers;
    }

    public void setElvElencoVers(List<ElvElencoVer> elvElencoVers) {
        this.elvElencoVers = elvElencoVers;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<ElvLogElencoVer> getElvLogElencoVers() {
        return this.elvLogElencoVers;
    }

    public void setElvLogElencoVers(List<ElvLogElencoVer> elvLogElencoVers) {
        this.elvLogElencoVers = elvLogElencoVers;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<LogJob> getLogJobs() {
        return this.logJobs;
    }

    public void setLogJobs(List<LogJob> logJobs) {
        this.logJobs = logJobs;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<LogLockElab> getLogLockElabs() {
        return this.logLockElabs;
    }

    public void setLogLockElabs(List<LogLockElab> logLockElabs) {
        this.logLockElabs = logLockElabs;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<LogOper> getLogOpers() {
        return this.logOpers;
    }

    public void setLogOpers(List<LogOper> logOpers) {
        this.logOpers = logOpers;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<OrgOperTitol> getOrgOperTitols() {
        return this.orgOperTitols;
    }

    public void setOrgOperTitols(List<OrgOperTitol> orgOperTitols) {
        this.orgOperTitols = orgOperTitols;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.REMOVE)
    @XmlTransient
    public List<OrgPartitionStrut> getOrgPartitionStruts() {
        return this.orgPartitionStruts;
    }

    public void setOrgPartitionStruts(List<OrgPartitionStrut> orgPartitionStruts) {
        this.orgPartitionStruts = orgPartitionStruts;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CATEG_STRUT")
    public OrgCategStrut getOrgCategStrut() {
        return this.orgCategStrut;
    }

    public void setOrgCategStrut(OrgCategStrut orgCategStrut) {
        this.orgCategStrut = orgCategStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE")
    @XmlTransient
    public OrgEnte getOrgEnte() {
        return this.orgEnte;
    }

    public void setOrgEnte(OrgEnte orgEnte) {
        this.orgEnte = orgEnte;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<DecTipoFascicolo> getDecTipoFascicolos() {
        return this.decTipoFascicolos;
    }

    public void setDecTipoFascicolos(List<DecTipoFascicolo> decTipoFascicolos) {
        this.decTipoFascicolos = decTipoFascicolos;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public List<OrgSubStrut> getOrgSubStruts() {
        return this.orgSubStruts;
    }

    public void setOrgSubStruts(List<OrgSubStrut> orgSubStruts) {
        this.orgSubStruts = orgSubStruts;
    }

    @OneToMany(mappedBy = "orgStrut")
    public List<OrgUsoSistemaMigraz> getOrgUsoSistemaMigrazs() {
        return this.orgUsoSistemaMigrazs;
    }

    public void setOrgUsoSistemaMigrazs(List<OrgUsoSistemaMigraz> orgUsoSistemaMigrazs) {
        this.orgUsoSistemaMigrazs = orgUsoSistemaMigrazs;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<SerSerie> getSerSeries() {
        return this.serSeries;
    }

    public void setSerSeries(List<SerSerie> serSeries) {
        this.serSeries = serSeries;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<VolVolumeConserv> getVolVolumeConservs() {
        return this.volVolumeConservs;
    }

    public void setVolVolumeConservs(List<VolVolumeConserv> volVolumeConservs) {
        this.volVolumeConservs = volVolumeConservs;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<VrsDocNonVer> getVrsDocNonVers() {
        return this.vrsDocNonVers;
    }

    public void setVrsDocNonVers(List<VrsDocNonVer> vrsDocNonVers) {
        this.vrsDocNonVers = vrsDocNonVers;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<VrsSessioneVers> getVrsSessioneVers() {
        return this.vrsSessioneVers;
    }

    public void setVrsSessioneVers(List<VrsSessioneVers> vrsSessioneVers) {
        this.vrsSessioneVers = vrsSessioneVers;
    }

    @OneToMany(mappedBy = "orgStrut")
    @XmlTransient
    public List<VrsUnitaDocNonVer> getVrsUnitaDocNonVers() {
        return this.vrsUnitaDocNonVers;
    }

    public void setVrsUnitaDocNonVers(List<VrsUnitaDocNonVer> vrsUnitaDocNonVers) {
        this.vrsUnitaDocNonVers = vrsUnitaDocNonVers;
    }

    @OneToMany(mappedBy = "orgStrut", cascade = CascadeType.PERSIST)
    public // @XmlTransient
    List<DecUsoModelloTipoSerie> getDecUsoModelloTipoSeries() {
        return this.decUsoModelloTipoSeries;
    }

    public void setDecUsoModelloTipoSeries(List<DecUsoModelloTipoSerie> decUsoModelloTipoSeries) {
        this.decUsoModelloTipoSeries = decUsoModelloTipoSeries;
    }

    public DecUsoModelloTipoSerie addDecUsoModelloTipoSery(DecUsoModelloTipoSerie decUsoModelloTipoSery) {
        getDecUsoModelloTipoSeries().add(decUsoModelloTipoSery);
        decUsoModelloTipoSery.setOrgStrut(this);
        return decUsoModelloTipoSery;
    }

    public DecUsoModelloTipoSerie removeDecUsoModelloTipoSery(DecUsoModelloTipoSerie decUsoModelloTipoSery) {
        getDecUsoModelloTipoSeries().remove(decUsoModelloTipoSery);
        decUsoModelloTipoSery.setOrgStrut(null);
        return decUsoModelloTipoSery;
    }

    @OneToMany(mappedBy = "orgStrut")
    public List<AplValoreParamApplic> getAplValoreParamApplics() {
        return this.aplValoreParamApplics;
    }

    public void setAplValoreParamApplics(List<AplValoreParamApplic> aplValoreParamApplics) {
        this.aplValoreParamApplics = aplValoreParamApplics;
    }

    public AplValoreParamApplic addAplValoreParamApplic(AplValoreParamApplic aplValoreParamApplic) {
        getAplValoreParamApplics().add(aplValoreParamApplic);
        aplValoreParamApplic.setOrgStrut(this);
        return aplValoreParamApplic;
    }

    public AplValoreParamApplic removeAplValoreParamApplic(AplValoreParamApplic aplValoreParamApplic) {
        getAplValoreParamApplics().remove(aplValoreParamApplic);
        aplValoreParamApplic.setOrgStrut(null);
        return aplValoreParamApplic;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (int) (this.idStrut ^ (this.idStrut >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OrgStrut other = (OrgStrut) obj;
        return this.idStrut == other.idStrut;
    }

    @PrePersist
    void preInsert() {
        if (this.flCessato == null) {
            this.flCessato = "0";
        }
        if (this.flArchivioRestituito == null) {
            this.flArchivioRestituito = "0";
        }
    }
}
