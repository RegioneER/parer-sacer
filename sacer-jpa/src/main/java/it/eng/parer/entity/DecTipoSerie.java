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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_SERIE database table.
 */
@Entity
@Table(name = "DEC_TIPO_SERIE")

public class DecTipoSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoSerie;

    private BigDecimal aaFinCreaAutom;

    private BigDecimal aaIniCreaAutom;

    private String cdSerieDefault;

    private String dsSerieDefault;

    private String dsTipoSerie;

    private Date dtIstituz;

    private Date dtSoppres;

    private String flControlloConsistObblig;

    private String flCreaAutom;

    private String flTipoSeriePadre;

    private String ggCreaAutom;

    private BigDecimal niAaSelUd;

    private BigDecimal niAaSelUdSuc;

    private BigDecimal niAnniConserv;

    private BigDecimal niMmCreaAutom;

    private BigDecimal niUnitaDocVolume;

    private String nmTipoSerie;

    private String tiConservazioneSerie;

    private String tiCreaStandard;

    private String tiSelUd;

    private String tiStatoVerSerieAutom;

    private String tipoContenSerie;

    private List<DecCampoInpUd> decCampoInpUds = new ArrayList<>();

    private List<DecNotaTipoSerie> decNotaTipoSeries = new ArrayList<>();

    private DecTipoSerie decTipoSeriePadre;

    private List<DecTipoSerie> decTipoSeriePadres = new ArrayList<>();

    private OrgStrut orgStrut;

    private List<DecTipoSerieCreataAutom> decTipoSerieCreataAutoms = new ArrayList<>();

    private List<DecTipoSerieUd> decTipoSerieUds = new ArrayList<>();

    private List<SerSerie> serSeries = new ArrayList<>();

    private DecModelloTipoSerie decModelloTipoSerie;

    public DecTipoSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_SERIE")
    @GenericGenerator(name = "SDEC_TIPO_SERIE_ID_TIPO_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_SERIE_ID_TIPO_SERIE_GENERATOR")
    public Long getIdTipoSerie() {
        return this.idTipoSerie;
    }

    public void setIdTipoSerie(Long idTipoSerie) {
        this.idTipoSerie = idTipoSerie;
    }

    @Column(name = "AA_FIN_CREA_AUTOM")
    public BigDecimal getAaFinCreaAutom() {
        return this.aaFinCreaAutom;
    }

    public void setAaFinCreaAutom(BigDecimal aaFinCreaAutom) {
        this.aaFinCreaAutom = aaFinCreaAutom;
    }

    @Column(name = "AA_INI_CREA_AUTOM")
    public BigDecimal getAaIniCreaAutom() {
        return this.aaIniCreaAutom;
    }

    public void setAaIniCreaAutom(BigDecimal aaIniCreaAutom) {
        this.aaIniCreaAutom = aaIniCreaAutom;
    }

    @Column(name = "CD_SERIE_DEFAULT")
    public String getCdSerieDefault() {
        return this.cdSerieDefault;
    }

    public void setCdSerieDefault(String cdSerieDefault) {
        this.cdSerieDefault = cdSerieDefault;
    }

    @Column(name = "DS_SERIE_DEFAULT")
    public String getDsSerieDefault() {
        return this.dsSerieDefault;
    }

    public void setDsSerieDefault(String dsSerieDefault) {
        this.dsSerieDefault = dsSerieDefault;
    }

    @Column(name = "DS_TIPO_SERIE")
    public String getDsTipoSerie() {
        return this.dsTipoSerie;
    }

    public void setDsTipoSerie(String dsTipoSerie) {
        this.dsTipoSerie = dsTipoSerie;
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

    @Column(name = "FL_CONTROLLO_CONSIST_OBBLIG", columnDefinition = "char(1)")
    public String getFlControlloConsistObblig() {
        return this.flControlloConsistObblig;
    }

    public void setFlControlloConsistObblig(String flControlloConsistObblig) {
        this.flControlloConsistObblig = flControlloConsistObblig;
    }

    @Column(name = "FL_CREA_AUTOM", columnDefinition = "char(1)")
    public String getFlCreaAutom() {
        return this.flCreaAutom;
    }

    public void setFlCreaAutom(String flCreaAutom) {
        this.flCreaAutom = flCreaAutom;
    }

    @Column(name = "FL_TIPO_SERIE_PADRE", columnDefinition = "char(1)")
    public String getFlTipoSeriePadre() {
        return this.flTipoSeriePadre;
    }

    public void setFlTipoSeriePadre(String flTipoSeriePadre) {
        this.flTipoSeriePadre = flTipoSeriePadre;
    }

    @Column(name = "GG_CREA_AUTOM", columnDefinition = "char")
    public String getGgCreaAutom() {
        return this.ggCreaAutom;
    }

    public void setGgCreaAutom(String ggCreaAutom) {
        this.ggCreaAutom = ggCreaAutom;
    }

    @Column(name = "NI_AA_SEL_UD")
    public BigDecimal getNiAaSelUd() {
        return this.niAaSelUd;
    }

    public void setNiAaSelUd(BigDecimal niAaSelUd) {
        this.niAaSelUd = niAaSelUd;
    }

    @Column(name = "NI_AA_SEL_UD_SUC")
    public BigDecimal getNiAaSelUdSuc() {
        return this.niAaSelUdSuc;
    }

    public void setNiAaSelUdSuc(BigDecimal niAaSelUdSuc) {
        this.niAaSelUdSuc = niAaSelUdSuc;
    }

    @Column(name = "NI_ANNI_CONSERV")
    public BigDecimal getNiAnniConserv() {
        return this.niAnniConserv;
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
        this.niAnniConserv = niAnniConserv;
    }

    @Column(name = "NI_MM_CREA_AUTOM")
    public BigDecimal getNiMmCreaAutom() {
        return this.niMmCreaAutom;
    }

    public void setNiMmCreaAutom(BigDecimal niMmCreaAutom) {
        this.niMmCreaAutom = niMmCreaAutom;
    }

    @Column(name = "NI_UNITA_DOC_VOLUME")
    public BigDecimal getNiUnitaDocVolume() {
        return this.niUnitaDocVolume;
    }

    public void setNiUnitaDocVolume(BigDecimal niUnitaDocVolume) {
        this.niUnitaDocVolume = niUnitaDocVolume;
    }

    @Column(name = "NM_TIPO_SERIE")
    public String getNmTipoSerie() {
        return this.nmTipoSerie;
    }

    public void setNmTipoSerie(String nmTipoSerie) {
        this.nmTipoSerie = nmTipoSerie;
    }

    @Column(name = "TI_CONSERVAZIONE_SERIE")
    public String getTiConservazioneSerie() {
        return this.tiConservazioneSerie;
    }

    public void setTiConservazioneSerie(String tiConservazioneSerie) {
        this.tiConservazioneSerie = tiConservazioneSerie;
    }

    @Column(name = "TI_CREA_STANDARD")
    public String getTiCreaStandard() {
        return this.tiCreaStandard;
    }

    public void setTiCreaStandard(String tiCreaStandard) {
        this.tiCreaStandard = tiCreaStandard;
    }

    @Column(name = "TI_SEL_UD")
    public String getTiSelUd() {
        return this.tiSelUd;
    }

    public void setTiSelUd(String tiSelUd) {
        this.tiSelUd = tiSelUd;
    }

    @Column(name = "TI_STATO_VER_SERIE_AUTOM")
    public String getTiStatoVerSerieAutom() {
        return this.tiStatoVerSerieAutom;
    }

    public void setTiStatoVerSerieAutom(String tiStatoVerSerieAutom) {
        this.tiStatoVerSerieAutom = tiStatoVerSerieAutom;
    }

    @Column(name = "TIPO_CONTEN_SERIE")
    public String getTipoContenSerie() {
        return this.tipoContenSerie;
    }

    public void setTipoContenSerie(String tipoContenSerie) {
        this.tipoContenSerie = tipoContenSerie;
    }

    @OneToMany(mappedBy = "decTipoSerie", cascade = CascadeType.PERSIST)
    public List<DecCampoInpUd> getDecCampoInpUds() {
        return this.decCampoInpUds;
    }

    public void setDecCampoInpUds(List<DecCampoInpUd> decCampoInpUds) {
        this.decCampoInpUds = decCampoInpUds;
    }

    public DecCampoInpUd addDecCampoInpUd(DecCampoInpUd decCampoInpUd) {
        getDecCampoInpUds().add(decCampoInpUd);
        decCampoInpUd.setDecTipoSerie(this);
        return decCampoInpUd;
    }

    public DecCampoInpUd removeDecCampoInpUd(DecCampoInpUd decCampoInpUd) {
        getDecCampoInpUds().remove(decCampoInpUd);
        decCampoInpUd.setDecTipoSerie(null);
        return decCampoInpUd;
    }

    @OneToMany(mappedBy = "decTipoSerie", cascade = CascadeType.PERSIST)
    public List<DecNotaTipoSerie> getDecNotaTipoSeries() {
        return this.decNotaTipoSeries;
    }

    public void setDecNotaTipoSeries(List<DecNotaTipoSerie> decNotaTipoSeries) {
        this.decNotaTipoSeries = decNotaTipoSeries;
    }

    public DecNotaTipoSerie addDecNotaTipoSery(DecNotaTipoSerie decNotaTipoSery) {
        getDecNotaTipoSeries().add(decNotaTipoSery);
        decNotaTipoSery.setDecTipoSerie(this);
        return decNotaTipoSery;
    }

    public DecNotaTipoSerie removeDecNotaTipoSery(DecNotaTipoSerie decNotaTipoSery) {
        getDecNotaTipoSeries().remove(decNotaTipoSery);
        decNotaTipoSery.setDecTipoSerie(null);
        return decNotaTipoSery;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE_PADRE")
    public DecTipoSerie getDecTipoSeriePadre() {
        return this.decTipoSeriePadre;
    }

    public void setDecTipoSeriePadre(DecTipoSerie decTipoSeriePadre) {
        this.decTipoSeriePadre = decTipoSeriePadre;
    }

    @OneToMany(mappedBy = "decTipoSeriePadre", cascade = CascadeType.PERSIST)
    public List<DecTipoSerie> getDecTipoSeriePadres() {
        return this.decTipoSeriePadres;
    }

    public void setDecTipoSeriePadres(List<DecTipoSerie> decTipoSeriePadres) {
        this.decTipoSeriePadres = decTipoSeriePadres;
    }

    public DecTipoSerie addDecTipoSeriePadre(DecTipoSerie decTipoSerie) {
        getDecTipoSeriePadres().add(decTipoSerie);
        decTipoSerie.setDecTipoSeriePadre(this);
        return decTipoSerie;
    }

    public DecTipoSerie removeDecTipoSeriePadre(DecTipoSerie decTipoSerie) {
        getDecTipoSeriePadres().remove(decTipoSerie);
        decTipoSerie.setDecTipoSeriePadre(null);
        return decTipoSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "decTipoSerie", cascade = CascadeType.PERSIST)
    public List<DecTipoSerieCreataAutom> getDecTipoSerieCreataAutoms() {
        return this.decTipoSerieCreataAutoms;
    }

    public void setDecTipoSerieCreataAutoms(List<DecTipoSerieCreataAutom> decTipoSerieCreataAutoms) {
        this.decTipoSerieCreataAutoms = decTipoSerieCreataAutoms;
    }

    public DecTipoSerieCreataAutom addDecTipoSerieCreataAutom(DecTipoSerieCreataAutom decTipoSerieCreataAutom) {
        getDecTipoSerieCreataAutoms().add(decTipoSerieCreataAutom);
        decTipoSerieCreataAutom.setDecTipoSerie(this);
        return decTipoSerieCreataAutom;
    }

    public DecTipoSerieCreataAutom removeDecTipoSerieCreataAutom(DecTipoSerieCreataAutom decTipoSerieCreataAutom) {
        getDecTipoSerieCreataAutoms().remove(decTipoSerieCreataAutom);
        decTipoSerieCreataAutom.setDecTipoSerie(null);
        return decTipoSerieCreataAutom;
    }

    @OneToMany(mappedBy = "decTipoSerie", cascade = CascadeType.PERSIST)
    public List<DecTipoSerieUd> getDecTipoSerieUds() {
        return this.decTipoSerieUds;
    }

    public void setDecTipoSerieUds(List<DecTipoSerieUd> decTipoSerieUds) {
        this.decTipoSerieUds = decTipoSerieUds;
    }

    public DecTipoSerieUd addDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        getDecTipoSerieUds().add(decTipoSerieUd);
        decTipoSerieUd.setDecTipoSerie(this);
        return decTipoSerieUd;
    }

    public DecTipoSerieUd removeDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        getDecTipoSerieUds().remove(decTipoSerieUd);
        decTipoSerieUd.setDecTipoSerie(null);
        return decTipoSerieUd;
    }

    @OneToMany(mappedBy = "decTipoSerie", cascade = CascadeType.PERSIST)
    public List<SerSerie> getSerSeries() {
        return this.serSeries;
    }

    public void setSerSeries(List<SerSerie> serSeries) {
        this.serSeries = serSeries;
    }

    public SerSerie addSerSery(SerSerie serSery) {
        getSerSeries().add(serSery);
        serSery.setDecTipoSerie(this);
        return serSery;
    }

    public SerSerie removeSerSery(SerSerie serSery) {
        getSerSeries().remove(serSery);
        serSery.setDecTipoSerie(null);
        return serSery;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MODELLO_TIPO_SERIE")
    public DecModelloTipoSerie getDecModelloTipoSerie() {
        return this.decModelloTipoSerie;
    }

    public void setDecModelloTipoSerie(DecModelloTipoSerie decModelloTipoSerie) {
        this.decModelloTipoSerie = decModelloTipoSerie;
    }
}
