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
 * The persistent class for the SER_SERIE database table.
 */
@Entity
@Table(name = "SER_SERIE")

public class SerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSerie;

    private BigDecimal aaSerie;

    private String cdCompositoSerie;

    private String dsSerie;

    private Date dtAnnul;

    private BigDecimal idStatoSerieCor;

    private BigDecimal niAnniConserv;

    private String cdSerieNormaliz;

    private DecTipoSerie decTipoSerie;

    private OrgStrut orgStrut;

    private SerSerie serSeriePadre;

    private List<SerSerie> serSeries = new ArrayList<>();

    private List<SerVerSerie> serVerSeries = new ArrayList<>();

    private List<SerStatoSerie> serStatoSeries = new ArrayList<>();

    public SerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SERIE")
    @GenericGenerator(name = "SSER_SERIE_ID_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_SERIE_ID_SERIE_GENERATOR")
    public Long getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(Long idSerie) {
        this.idSerie = idSerie;
    }

    @Column(name = "AA_SERIE")
    public BigDecimal getAaSerie() {
        return this.aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
        this.aaSerie = aaSerie;
    }

    @Column(name = "CD_COMPOSITO_SERIE")
    public String getCdCompositoSerie() {
        return this.cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        this.cdCompositoSerie = cdCompositoSerie;
    }

    @Column(name = "DS_SERIE")
    public String getDsSerie() {
        return this.dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ANNUL")
    public Date getDtAnnul() {
        return this.dtAnnul;
    }

    public void setDtAnnul(Date dtAnnul) {
        this.dtAnnul = dtAnnul;
    }

    @Column(name = "ID_STATO_SERIE_COR")
    public BigDecimal getIdStatoSerieCor() {
        return this.idStatoSerieCor;
    }

    public void setIdStatoSerieCor(BigDecimal idStatoSerieCor) {
        this.idStatoSerieCor = idStatoSerieCor;
    }

    @Column(name = "NI_ANNI_CONSERV")
    public BigDecimal getNiAnniConserv() {
        return this.niAnniConserv;
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
        this.niAnniConserv = niAnniConserv;
    }

    @Column(name = "CD_SERIE_NORMALIZ")
    public String getCdSerieNormaliz() {
        return this.cdSerieNormaliz;
    }

    public void setCdSerieNormaliz(String cdSerieNormaliz) {
        this.cdSerieNormaliz = cdSerieNormaliz;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE")
    public DecTipoSerie getDecTipoSerie() {
        return this.decTipoSerie;
    }

    public void setDecTipoSerie(DecTipoSerie decTipoSerie) {
        this.decTipoSerie = decTipoSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SERIE_PADRE")
    public SerSerie getSerSeriePadre() {
        return this.serSeriePadre;
    }

    public void setSerSeriePadre(SerSerie serSeriePadre) {
        this.serSeriePadre = serSeriePadre;
    }

    @OneToMany(mappedBy = "serSeriePadre", cascade = CascadeType.PERSIST)
    public List<SerSerie> getSerSeriePadres() {
        return this.serSeries;
    }

    public void setSerSeriePadres(List<SerSerie> serSeries) {
        this.serSeries = serSeries;
    }

    public SerSerie addSerSeriePadre(SerSerie serSeriePadre) {
        getSerSeriePadres().add(serSeriePadre);
        serSeriePadre.setSerSeriePadre(this);
        return serSeriePadre;
    }

    public SerSerie removeSerSeriePadre(SerSerie serSery) {
        getSerSeriePadres().remove(serSery);
        serSery.setSerSeriePadre(null);
        return serSery;
    }

    @OneToMany(mappedBy = "serSerie", cascade = CascadeType.PERSIST)
    public List<SerVerSerie> getSerVerSeries() {
        return this.serVerSeries;
    }

    public void setSerVerSeries(List<SerVerSerie> serVerSeries) {
        this.serVerSeries = serVerSeries;
    }

    public SerVerSerie addSerVerSery(SerVerSerie serVerSery) {
        getSerVerSeries().add(serVerSery);
        serVerSery.setSerSerie(this);
        return serVerSery;
    }

    public SerVerSerie removeSerVerSery(SerVerSerie serVerSery) {
        getSerVerSeries().remove(serVerSery);
        serVerSery.setSerSerie(null);
        return serVerSery;
    }

    @OneToMany(mappedBy = "serSerie", cascade = CascadeType.PERSIST)
    public List<SerStatoSerie> getSerStatoSeries() {
        return this.serStatoSeries;
    }

    public void setSerStatoSeries(List<SerStatoSerie> serStatoSeries) {
        this.serStatoSeries = serStatoSeries;
    }

    public SerStatoSerie addSerStatoSery(SerStatoSerie serStatoSery) {
        getSerStatoSeries().add(serStatoSery);
        serStatoSery.setSerSerie(this);
        return serStatoSery;
    }

    public SerStatoSerie removeSerStatoSery(SerStatoSerie serStatoSery) {
        getSerStatoSeries().remove(serStatoSery);
        serStatoSery.setSerSerie(null);
        return serStatoSery;
    }
}
