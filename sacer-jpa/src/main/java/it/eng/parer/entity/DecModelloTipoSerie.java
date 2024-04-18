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
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_MODELLO_TIPO_SERIE database table.
 */
@Entity
@Table(name = "DEC_MODELLO_TIPO_SERIE")

public class DecModelloTipoSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idModelloTipoSerie;

    private BigDecimal aaFinCreaAutom;

    private BigDecimal aaIniCreaAutom;

    private String cdSerieDaCreare;

    private String dsModelloTipoSerie;

    private String dsSerieDaCreare;

    private String dsTipoSerieDaCreare;

    private Date dtIstituz;

    private Date dtSoppres;

    private String flControlloConsistObblig;

    private String flCreaAutom;

    private String ggCreaAutom;

    private BigDecimal niAaSelUd;

    private BigDecimal niAaSelUdSuc;

    private BigDecimal niAnniConserv;

    private BigDecimal niMmCreaAutom;

    private BigDecimal niUnitaDocVolume;

    private String nmModelloTipoSerie;

    private String nmTipoSerieDaCreare;

    private String tiConservazioneSerie;

    private String tiRglAnniConserv;

    private String tiRglCdSerie;

    private String tiRglConservazioneSerie;

    private String tiRglDsSerie;

    private String tiRglDsTipoSerie;

    private String tiRglFiltroTiDoc;

    private String tiRglNmTipoSerie;

    private String tiRglRangeAnniCreaAutom;

    private String tiSelUd;

    private String tiStatoVerSerieAutom;

    private List<DecModelloCampoInpUd> decModelloCampoInpUds = new ArrayList<>();

    private List<DecModelloFiltroSelUdattb> decModelloFiltroSelUdattbs = new ArrayList<>();

    private List<DecModelloFiltroTiDoc> decModelloFiltroTiDocs = new ArrayList<>();

    private List<DecModelloOutSelUd> decModelloOutSelUds = new ArrayList<>();

    private DecTipoDoc decTipoDoc;

    private DecTipoUnitaDoc decTipoUnitaDoc;

    private OrgAmbiente orgAmbiente;

    private List<DecNotaModelloTipoSerie> decNotaModelloTipoSeries = new ArrayList<>();

    private List<DecRegistroUnitaDoc> decRegistroUnitaDocs = new ArrayList<>();

    private List<DecTipoSerie> decTipoSeries = new ArrayList<>();

    private List<DecTipoUnitaDoc> decTipoUnitaDocs = new ArrayList<>();

    private List<DecUsoModelloTipoSerie> decUsoModelloTipoSeries = new ArrayList<>();

    public DecModelloTipoSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MODELLO_TIPO_SERIE")
    @XmlID
    @GenericGenerator(name = "SDEC_MODELLO_TIPO_SERIE_ID_MODELLO_TIPO_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_MODELLO_TIPO_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_MODELLO_TIPO_SERIE_ID_MODELLO_TIPO_SERIE_GENERATOR")
    public Long getIdModelloTipoSerie() {
        return this.idModelloTipoSerie;
    }

    public void setIdModelloTipoSerie(Long idModelloTipoSerie) {
        this.idModelloTipoSerie = idModelloTipoSerie;
    }

    @XmlTransient
    @Column(name = "AA_FIN_CREA_AUTOM")
    public BigDecimal getAaFinCreaAutom() {
        return this.aaFinCreaAutom;
    }

    public void setAaFinCreaAutom(BigDecimal aaFinCreaAutom) {
        this.aaFinCreaAutom = aaFinCreaAutom;
    }

    @XmlTransient
    @Column(name = "AA_INI_CREA_AUTOM")
    public BigDecimal getAaIniCreaAutom() {
        return this.aaIniCreaAutom;
    }

    public void setAaIniCreaAutom(BigDecimal aaIniCreaAutom) {
        this.aaIniCreaAutom = aaIniCreaAutom;
    }

    @XmlTransient
    @Column(name = "CD_SERIE_DA_CREARE")
    public String getCdSerieDaCreare() {
        return this.cdSerieDaCreare;
    }

    public void setCdSerieDaCreare(String cdSerieDaCreare) {
        this.cdSerieDaCreare = cdSerieDaCreare;
    }

    @XmlTransient
    @Column(name = "DS_MODELLO_TIPO_SERIE")
    public String getDsModelloTipoSerie() {
        return this.dsModelloTipoSerie;
    }

    public void setDsModelloTipoSerie(String dsModelloTipoSerie) {
        this.dsModelloTipoSerie = dsModelloTipoSerie;
    }

    @XmlTransient
    @Column(name = "DS_SERIE_DA_CREARE")
    public String getDsSerieDaCreare() {
        return this.dsSerieDaCreare;
    }

    public void setDsSerieDaCreare(String dsSerieDaCreare) {
        this.dsSerieDaCreare = dsSerieDaCreare;
    }

    @XmlTransient
    @Column(name = "DS_TIPO_SERIE_DA_CREARE")
    public String getDsTipoSerieDaCreare() {
        return this.dsTipoSerieDaCreare;
    }

    public void setDsTipoSerieDaCreare(String dsTipoSerieDaCreare) {
        this.dsTipoSerieDaCreare = dsTipoSerieDaCreare;
    }

    @XmlTransient
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
        return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @XmlTransient
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @XmlTransient
    @Column(name = "FL_CONTROLLO_CONSIST_OBBLIG", columnDefinition = "char(1)")
    public String getFlControlloConsistObblig() {
        return this.flControlloConsistObblig;
    }

    public void setFlControlloConsistObblig(String flControlloConsistObblig) {
        this.flControlloConsistObblig = flControlloConsistObblig;
    }

    @XmlTransient
    @Column(name = "FL_CREA_AUTOM", columnDefinition = "char(1)")
    public String getFlCreaAutom() {
        return this.flCreaAutom;
    }

    public void setFlCreaAutom(String flCreaAutom) {
        this.flCreaAutom = flCreaAutom;
    }

    @XmlTransient
    @Column(name = "GG_CREA_AUTOM", columnDefinition = "char")
    public String getGgCreaAutom() {
        return this.ggCreaAutom;
    }

    public void setGgCreaAutom(String ggCreaAutom) {
        this.ggCreaAutom = ggCreaAutom;
    }

    @XmlTransient
    @Column(name = "NI_AA_SEL_UD")
    public BigDecimal getNiAaSelUd() {
        return this.niAaSelUd;
    }

    public void setNiAaSelUd(BigDecimal niAaSelUd) {
        this.niAaSelUd = niAaSelUd;
    }

    @XmlTransient
    @Column(name = "NI_AA_SEL_UD_SUC")
    public BigDecimal getNiAaSelUdSuc() {
        return this.niAaSelUdSuc;
    }

    public void setNiAaSelUdSuc(BigDecimal niAaSelUdSuc) {
        this.niAaSelUdSuc = niAaSelUdSuc;
    }

    @XmlTransient
    @Column(name = "NI_ANNI_CONSERV")
    public BigDecimal getNiAnniConserv() {
        return this.niAnniConserv;
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
        this.niAnniConserv = niAnniConserv;
    }

    @XmlTransient
    @Column(name = "NI_MM_CREA_AUTOM")
    public BigDecimal getNiMmCreaAutom() {
        return this.niMmCreaAutom;
    }

    public void setNiMmCreaAutom(BigDecimal niMmCreaAutom) {
        this.niMmCreaAutom = niMmCreaAutom;
    }

    @XmlTransient
    @Column(name = "NI_UNITA_DOC_VOLUME")
    public BigDecimal getNiUnitaDocVolume() {
        return this.niUnitaDocVolume;
    }

    public void setNiUnitaDocVolume(BigDecimal niUnitaDocVolume) {
        this.niUnitaDocVolume = niUnitaDocVolume;
    }

    @Column(name = "NM_MODELLO_TIPO_SERIE")
    public String getNmModelloTipoSerie() {
        return this.nmModelloTipoSerie;
    }

    public void setNmModelloTipoSerie(String nmModelloTipoSerie) {
        this.nmModelloTipoSerie = nmModelloTipoSerie;
    }

    @XmlTransient
    @Column(name = "NM_TIPO_SERIE_DA_CREARE")
    public String getNmTipoSerieDaCreare() {
        return this.nmTipoSerieDaCreare;
    }

    public void setNmTipoSerieDaCreare(String nmTipoSerieDaCreare) {
        this.nmTipoSerieDaCreare = nmTipoSerieDaCreare;
    }

    @XmlTransient
    @Column(name = "TI_CONSERVAZIONE_SERIE")
    public String getTiConservazioneSerie() {
        return this.tiConservazioneSerie;
    }

    public void setTiConservazioneSerie(String tiConservazioneSerie) {
        this.tiConservazioneSerie = tiConservazioneSerie;
    }

    @XmlTransient
    @Column(name = "TI_RGL_ANNI_CONSERV")
    public String getTiRglAnniConserv() {
        return this.tiRglAnniConserv;
    }

    public void setTiRglAnniConserv(String tiRglAnniConserv) {
        this.tiRglAnniConserv = tiRglAnniConserv;
    }

    @XmlTransient
    @Column(name = "TI_RGL_CD_SERIE")
    public String getTiRglCdSerie() {
        return this.tiRglCdSerie;
    }

    public void setTiRglCdSerie(String tiRglCdSerie) {
        this.tiRglCdSerie = tiRglCdSerie;
    }

    @XmlTransient
    @Column(name = "TI_RGL_CONSERVAZIONE_SERIE")
    public String getTiRglConservazioneSerie() {
        return this.tiRglConservazioneSerie;
    }

    public void setTiRglConservazioneSerie(String tiRglConservazioneSerie) {
        this.tiRglConservazioneSerie = tiRglConservazioneSerie;
    }

    @XmlTransient
    @Column(name = "TI_RGL_DS_SERIE")
    public String getTiRglDsSerie() {
        return this.tiRglDsSerie;
    }

    public void setTiRglDsSerie(String tiRglDsSerie) {
        this.tiRglDsSerie = tiRglDsSerie;
    }

    @XmlTransient
    @Column(name = "TI_RGL_DS_TIPO_SERIE")
    public String getTiRglDsTipoSerie() {
        return this.tiRglDsTipoSerie;
    }

    public void setTiRglDsTipoSerie(String tiRglDsTipoSerie) {
        this.tiRglDsTipoSerie = tiRglDsTipoSerie;
    }

    @XmlTransient
    @Column(name = "TI_RGL_FILTRO_TI_DOC")
    public String getTiRglFiltroTiDoc() {
        return this.tiRglFiltroTiDoc;
    }

    public void setTiRglFiltroTiDoc(String tiRglFiltroTiDoc) {
        this.tiRglFiltroTiDoc = tiRglFiltroTiDoc;
    }

    @XmlTransient
    @Column(name = "TI_RGL_NM_TIPO_SERIE")
    public String getTiRglNmTipoSerie() {
        return this.tiRglNmTipoSerie;
    }

    public void setTiRglNmTipoSerie(String tiRglNmTipoSerie) {
        this.tiRglNmTipoSerie = tiRglNmTipoSerie;
    }

    @XmlTransient
    @Column(name = "TI_RGL_RANGE_ANNI_CREA_AUTOM")
    public String getTiRglRangeAnniCreaAutom() {
        return this.tiRglRangeAnniCreaAutom;
    }

    public void setTiRglRangeAnniCreaAutom(String tiRglRangeAnniCreaAutom) {
        this.tiRglRangeAnniCreaAutom = tiRglRangeAnniCreaAutom;
    }

    @XmlTransient
    @Column(name = "TI_SEL_UD")
    public String getTiSelUd() {
        return this.tiSelUd;
    }

    public void setTiSelUd(String tiSelUd) {
        this.tiSelUd = tiSelUd;
    }

    @XmlTransient
    @Column(name = "TI_STATO_VER_SERIE_AUTOM")
    public String getTiStatoVerSerieAutom() {
        return this.tiStatoVerSerieAutom;
    }

    public void setTiStatoVerSerieAutom(String tiStatoVerSerieAutom) {
        this.tiStatoVerSerieAutom = tiStatoVerSerieAutom;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecModelloCampoInpUd> getDecModelloCampoInpUds() {
        return this.decModelloCampoInpUds;
    }

    public void setDecModelloCampoInpUds(List<DecModelloCampoInpUd> decModelloCampoInpUds) {
        this.decModelloCampoInpUds = decModelloCampoInpUds;
    }

    public DecModelloCampoInpUd addDecModelloCampoInpUd(DecModelloCampoInpUd decModelloCampoInpUd) {
        getDecModelloCampoInpUds().add(decModelloCampoInpUd);
        decModelloCampoInpUd.setDecModelloTipoSerie(this);
        return decModelloCampoInpUd;
    }

    public DecModelloCampoInpUd removeDecModelloCampoInpUd(DecModelloCampoInpUd decModelloCampoInpUd) {
        getDecModelloCampoInpUds().remove(decModelloCampoInpUd);
        decModelloCampoInpUd.setDecModelloTipoSerie(null);
        return decModelloCampoInpUd;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecModelloFiltroSelUdattb> getDecModelloFiltroSelUdattbs() {
        return this.decModelloFiltroSelUdattbs;
    }

    public void setDecModelloFiltroSelUdattbs(List<DecModelloFiltroSelUdattb> decModelloFiltroSelUdattbs) {
        this.decModelloFiltroSelUdattbs = decModelloFiltroSelUdattbs;
    }

    public DecModelloFiltroSelUdattb addDecModelloFiltroSelUdattb(DecModelloFiltroSelUdattb decModelloFiltroSelUdattb) {
        getDecModelloFiltroSelUdattbs().add(decModelloFiltroSelUdattb);
        decModelloFiltroSelUdattb.setDecModelloTipoSerie(this);
        return decModelloFiltroSelUdattb;
    }

    public DecModelloFiltroSelUdattb removeDecModelloFiltroSelUdattb(
            DecModelloFiltroSelUdattb decModelloFiltroSelUdattb) {
        getDecModelloFiltroSelUdattbs().remove(decModelloFiltroSelUdattb);
        decModelloFiltroSelUdattb.setDecModelloTipoSerie(null);
        return decModelloFiltroSelUdattb;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecModelloFiltroTiDoc> getDecModelloFiltroTiDocs() {
        return this.decModelloFiltroTiDocs;
    }

    public void setDecModelloFiltroTiDocs(List<DecModelloFiltroTiDoc> decModelloFiltroTiDocs) {
        this.decModelloFiltroTiDocs = decModelloFiltroTiDocs;
    }

    public DecModelloFiltroTiDoc addDecModelloFiltroTiDoc(DecModelloFiltroTiDoc decModelloFiltroTiDoc) {
        getDecModelloFiltroTiDocs().add(decModelloFiltroTiDoc);
        decModelloFiltroTiDoc.setDecModelloTipoSerie(this);
        return decModelloFiltroTiDoc;
    }

    public DecModelloFiltroTiDoc removeDecModelloFiltroTiDoc(DecModelloFiltroTiDoc decModelloFiltroTiDoc) {
        getDecModelloFiltroTiDocs().remove(decModelloFiltroTiDoc);
        decModelloFiltroTiDoc.setDecModelloTipoSerie(null);
        return decModelloFiltroTiDoc;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecModelloOutSelUd> getDecModelloOutSelUds() {
        return this.decModelloOutSelUds;
    }

    public void setDecModelloOutSelUds(List<DecModelloOutSelUd> decModelloOutSelUds) {
        this.decModelloOutSelUds = decModelloOutSelUds;
    }

    public DecModelloOutSelUd addDecModelloOutSelUd(DecModelloOutSelUd decModelloOutSelUd) {
        getDecModelloOutSelUds().add(decModelloOutSelUd);
        decModelloOutSelUd.setDecModelloTipoSerie(this);
        return decModelloOutSelUd;
    }

    public DecModelloOutSelUd removeDecModelloOutSelUd(DecModelloOutSelUd decModelloOutSelUd) {
        getDecModelloOutSelUds().remove(decModelloOutSelUd);
        decModelloOutSelUd.setDecModelloTipoSerie(null);
        return decModelloOutSelUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DOC_DATI_SPEC")
    @XmlTransient
    public DecTipoDoc getDecTipoDoc() {
        return this.decTipoDoc;
    }

    public void setDecTipoDoc(DecTipoDoc decTipoDoc) {
        this.decTipoDoc = decTipoDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC_DATI_SPEC")
    @XmlTransient
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
        return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        this.decTipoUnitaDoc = decTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE")
    @XmlInverseReference(mappedBy = "decModelloTipoSerie")
    public OrgAmbiente getOrgAmbiente() {
        return this.orgAmbiente;
    }

    public void setOrgAmbiente(OrgAmbiente orgAmbiente) {
        this.orgAmbiente = orgAmbiente;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecNotaModelloTipoSerie> getDecNotaModelloTipoSeries() {
        return this.decNotaModelloTipoSeries;
    }

    public void setDecNotaModelloTipoSeries(List<DecNotaModelloTipoSerie> decNotaModelloTipoSeries) {
        this.decNotaModelloTipoSeries = decNotaModelloTipoSeries;
    }

    public DecNotaModelloTipoSerie addDecNotaModelloTipoSery(DecNotaModelloTipoSerie decNotaModelloTipoSery) {
        getDecNotaModelloTipoSeries().add(decNotaModelloTipoSery);
        decNotaModelloTipoSery.setDecModelloTipoSerie(this);
        return decNotaModelloTipoSery;
    }

    public DecNotaModelloTipoSerie removeDecNotaModelloTipoSery(DecNotaModelloTipoSerie decNotaModelloTipoSery) {
        getDecNotaModelloTipoSeries().remove(decNotaModelloTipoSery);
        decNotaModelloTipoSery.setDecModelloTipoSerie(null);
        return decNotaModelloTipoSery;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecRegistroUnitaDoc> getDecRegistroUnitaDocs() {
        return this.decRegistroUnitaDocs;
    }

    public void setDecRegistroUnitaDocs(List<DecRegistroUnitaDoc> decRegistroUnitaDocs) {
        this.decRegistroUnitaDocs = decRegistroUnitaDocs;
    }

    public DecRegistroUnitaDoc addDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
        getDecRegistroUnitaDocs().add(decRegistroUnitaDoc);
        decRegistroUnitaDoc.setDecModelloTipoSerie(this);
        return decRegistroUnitaDoc;
    }

    public DecRegistroUnitaDoc removeDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
        getDecRegistroUnitaDocs().remove(decRegistroUnitaDoc);
        decRegistroUnitaDoc.setDecModelloTipoSerie(null);
        return decRegistroUnitaDoc;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecTipoSerie> getDecTipoSeries() {
        return this.decTipoSeries;
    }

    public void setDecTipoSeries(List<DecTipoSerie> decTipoSeries) {
        this.decTipoSeries = decTipoSeries;
    }

    public DecTipoSerie addDecTipoSery(DecTipoSerie decTipoSery) {
        getDecTipoSeries().add(decTipoSery);
        decTipoSery.setDecModelloTipoSerie(this);
        return decTipoSery;
    }

    public DecTipoSerie removeDecTipoSery(DecTipoSerie decTipoSery) {
        getDecTipoSeries().remove(decTipoSery);
        decTipoSery.setDecModelloTipoSerie(null);
        return decTipoSery;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie")
    @XmlTransient
    public List<DecTipoUnitaDoc> getDecTipoUnitaDocs() {
        return this.decTipoUnitaDocs;
    }

    public void setDecTipoUnitaDocs(List<DecTipoUnitaDoc> decTipoUnitaDocs) {
        this.decTipoUnitaDocs = decTipoUnitaDocs;
    }

    public DecTipoUnitaDoc addDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        getDecTipoUnitaDocs().add(decTipoUnitaDoc);
        decTipoUnitaDoc.setDecModelloTipoSerie(this);
        return decTipoUnitaDoc;
    }

    public DecTipoUnitaDoc removeDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        getDecTipoUnitaDocs().remove(decTipoUnitaDoc);
        decTipoUnitaDoc.setDecModelloTipoSerie(null);
        return decTipoUnitaDoc;
    }

    @OneToMany(mappedBy = "decModelloTipoSerie", cascade = CascadeType.PERSIST)
    @XmlTransient
    public List<DecUsoModelloTipoSerie> getDecUsoModelloTipoSeries() {
        return this.decUsoModelloTipoSeries;
    }

    public void setDecUsoModelloTipoSeries(List<DecUsoModelloTipoSerie> decUsoModelloTipoSeries) {
        this.decUsoModelloTipoSeries = decUsoModelloTipoSeries;
    }

    public DecUsoModelloTipoSerie addDecUsoModelloTipoSery(DecUsoModelloTipoSerie decUsoModelloTipoSery) {
        getDecUsoModelloTipoSeries().add(decUsoModelloTipoSery);
        decUsoModelloTipoSery.setDecModelloTipoSerie(this);
        return decUsoModelloTipoSery;
    }

    public DecUsoModelloTipoSerie removeDecUsoModelloTipoSery(DecUsoModelloTipoSerie decUsoModelloTipoSery) {
        getDecUsoModelloTipoSeries().remove(decUsoModelloTipoSery);
        decUsoModelloTipoSery.setDecModelloTipoSerie(null);
        return decUsoModelloTipoSery;
    }
}
