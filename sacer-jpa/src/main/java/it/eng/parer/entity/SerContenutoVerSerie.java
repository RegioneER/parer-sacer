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
 * The persistent class for the SER_CONTENUTO_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_CONTENUTO_VER_SERIE")

public class SerContenutoVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idContenutoVerSerie;

    private Date dtStatoContenutoVerSerie;

    private String flTipoSerieUpd;

    private BigDecimal idFirstUdAppartVerSerie;

    private BigDecimal idLastUdAppartVerSerie;

    private BigDecimal niUdContenutoVerSerie;

    private String tiContenutoVerSerie;

    private String tiStatoContenutoVerSerie;

    private List<AroUdAppartVerSerie> aroUdAppartVerSeries = new ArrayList<>();

    private SerVerSerie serVerSerie;

    private List<SerErrContenutoVerSerie> serErrContenutoVerSeries = new ArrayList<>();

    private List<SerQueryContenutoVerSerie> serQueryContenutoVerSeries = new ArrayList<>();

    public SerContenutoVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CONTENUTO_VER_SERIE")
    @GenericGenerator(name = "SSER_CONTENUTO_VER_SERIE_ID_CONTENUTO_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_CONTENUTO_VER_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_CONTENUTO_VER_SERIE_ID_CONTENUTO_VER_SERIE_GENERATOR")
    public Long getIdContenutoVerSerie() {
	return this.idContenutoVerSerie;
    }

    public void setIdContenutoVerSerie(Long idContenutoVerSerie) {
	this.idContenutoVerSerie = idContenutoVerSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_CONTENUTO_VER_SERIE")
    public Date getDtStatoContenutoVerSerie() {
	return this.dtStatoContenutoVerSerie;
    }

    public void setDtStatoContenutoVerSerie(Date dtStatoContenutoVerSerie) {
	this.dtStatoContenutoVerSerie = dtStatoContenutoVerSerie;
    }

    @Column(name = "FL_TIPO_SERIE_UPD", columnDefinition = "char(1)")
    public String getFlTipoSerieUpd() {
	return this.flTipoSerieUpd;
    }

    public void setFlTipoSerieUpd(String flTipoSerieUpd) {
	this.flTipoSerieUpd = flTipoSerieUpd;
    }

    @Column(name = "ID_FIRST_UD_APPART_VER_SERIE")
    public BigDecimal getIdFirstUdAppartVerSerie() {
	return this.idFirstUdAppartVerSerie;
    }

    public void setIdFirstUdAppartVerSerie(BigDecimal idFirstUdAppartVerSerie) {
	this.idFirstUdAppartVerSerie = idFirstUdAppartVerSerie;
    }

    @Column(name = "ID_LAST_UD_APPART_VER_SERIE")
    public BigDecimal getIdLastUdAppartVerSerie() {
	return this.idLastUdAppartVerSerie;
    }

    public void setIdLastUdAppartVerSerie(BigDecimal idLastUdAppartVerSerie) {
	this.idLastUdAppartVerSerie = idLastUdAppartVerSerie;
    }

    @Column(name = "NI_UD_CONTENUTO_VER_SERIE")
    public BigDecimal getNiUdContenutoVerSerie() {
	return this.niUdContenutoVerSerie;
    }

    public void setNiUdContenutoVerSerie(BigDecimal niUdContenutoVerSerie) {
	this.niUdContenutoVerSerie = niUdContenutoVerSerie;
    }

    @Column(name = "TI_CONTENUTO_VER_SERIE")
    public String getTiContenutoVerSerie() {
	return this.tiContenutoVerSerie;
    }

    public void setTiContenutoVerSerie(String tiContenutoVerSerie) {
	this.tiContenutoVerSerie = tiContenutoVerSerie;
    }

    @Column(name = "TI_STATO_CONTENUTO_VER_SERIE")
    public String getTiStatoContenutoVerSerie() {
	return this.tiStatoContenutoVerSerie;
    }

    public void setTiStatoContenutoVerSerie(String tiStatoContenutoVerSerie) {
	this.tiStatoContenutoVerSerie = tiStatoContenutoVerSerie;
    }

    @OneToMany(mappedBy = "serContenutoVerSerie", cascade = CascadeType.PERSIST)
    public List<AroUdAppartVerSerie> getAroUdAppartVerSeries() {
	return this.aroUdAppartVerSeries;
    }

    public void setAroUdAppartVerSeries(List<AroUdAppartVerSerie> aroUdAppartVerSeries) {
	this.aroUdAppartVerSeries = aroUdAppartVerSeries;
    }

    public AroUdAppartVerSerie addAroUdAppartVerSery(AroUdAppartVerSerie aroUdAppartVerSery) {
	getAroUdAppartVerSeries().add(aroUdAppartVerSery);
	aroUdAppartVerSery.setSerContenutoVerSerie(this);
	return aroUdAppartVerSery;
    }

    public AroUdAppartVerSerie removeAroUdAppartVerSery(AroUdAppartVerSerie aroUdAppartVerSery) {
	getAroUdAppartVerSeries().remove(aroUdAppartVerSery);
	aroUdAppartVerSery.setSerContenutoVerSerie(null);
	return aroUdAppartVerSery;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
	return this.serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
	this.serVerSerie = serVerSerie;
    }

    @OneToMany(mappedBy = "serContenutoVerSerie", cascade = CascadeType.PERSIST)
    public List<SerErrContenutoVerSerie> getSerErrContenutoVerSeries() {
	return this.serErrContenutoVerSeries;
    }

    public void setSerErrContenutoVerSeries(
	    List<SerErrContenutoVerSerie> serErrContenutoVerSeries) {
	this.serErrContenutoVerSeries = serErrContenutoVerSeries;
    }

    public SerErrContenutoVerSerie addSerErrContenutoVerSery(
	    SerErrContenutoVerSerie serErrContenutoVerSery) {
	getSerErrContenutoVerSeries().add(serErrContenutoVerSery);
	serErrContenutoVerSery.setSerContenutoVerSerie(this);
	return serErrContenutoVerSery;
    }

    public SerErrContenutoVerSerie removeSerErrContenutoVerSery(
	    SerErrContenutoVerSerie serErrContenutoVerSery) {
	getSerErrContenutoVerSeries().remove(serErrContenutoVerSery);
	serErrContenutoVerSery.setSerContenutoVerSerie(null);
	return serErrContenutoVerSery;
    }

    @OneToMany(mappedBy = "serContenutoVerSerie", cascade = CascadeType.PERSIST)
    public List<SerQueryContenutoVerSerie> getSerQueryContenutoVerSeries() {
	return this.serQueryContenutoVerSeries;
    }

    public void setSerQueryContenutoVerSeries(
	    List<SerQueryContenutoVerSerie> serQueryContenutoVerSeries) {
	this.serQueryContenutoVerSeries = serQueryContenutoVerSeries;
    }

    public SerQueryContenutoVerSerie addSerQueryContenutoVerSery(
	    SerQueryContenutoVerSerie serQueryContenutoVerSery) {
	getSerQueryContenutoVerSeries().add(serQueryContenutoVerSery);
	serQueryContenutoVerSery.setSerContenutoVerSerie(this);
	return serQueryContenutoVerSery;
    }

    public SerQueryContenutoVerSerie removeSerQueryContenutoVerSery(
	    SerQueryContenutoVerSerie serQueryContenutoVerSery) {
	getSerQueryContenutoVerSeries().remove(serQueryContenutoVerSery);
	serQueryContenutoVerSery.setSerContenutoVerSerie(null);
	return serQueryContenutoVerSery;
    }

}
