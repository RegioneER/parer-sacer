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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_TIPO_NOTA_SERIE database table.
 */
@Entity
@Table(name = "DEC_TIPO_NOTA_SERIE")

public class DecTipoNotaSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idTipoNotaSerie;

    private String cdTipoNotaSerie;

    private String dsTipoNotaSerie;

    private String flMolt;

    private String flObblig;

    private BigDecimal niOrd;

    private List<DecNotaTipoSerie> decNotaTipoSeries = new ArrayList<>();

    private List<SerNotaVerSerie> serNotaVerSeries = new ArrayList<>();

    public DecTipoNotaSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_TIPO_NOTA_SERIE")
    @GenericGenerator(name = "SDEC_TIPO_NOTA_SERIE_ID_TIPO_NOTA_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_TIPO_NOTA_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_TIPO_NOTA_SERIE_ID_TIPO_NOTA_SERIE_GENERATOR")
    public Long getIdTipoNotaSerie() {
	return this.idTipoNotaSerie;
    }

    public void setIdTipoNotaSerie(Long idTipoNotaSerie) {
	this.idTipoNotaSerie = idTipoNotaSerie;
    }

    @Column(name = "CD_TIPO_NOTA_SERIE")
    public String getCdTipoNotaSerie() {
	return this.cdTipoNotaSerie;
    }

    public void setCdTipoNotaSerie(String cdTipoNotaSerie) {
	this.cdTipoNotaSerie = cdTipoNotaSerie;
    }

    @Column(name = "DS_TIPO_NOTA_SERIE")
    public String getDsTipoNotaSerie() {
	return this.dsTipoNotaSerie;
    }

    public void setDsTipoNotaSerie(String dsTipoNotaSerie) {
	this.dsTipoNotaSerie = dsTipoNotaSerie;
    }

    @Column(name = "FL_MOLT", columnDefinition = "char(1)")
    public String getFlMolt() {
	return this.flMolt;
    }

    public void setFlMolt(String flMolt) {
	this.flMolt = flMolt;
    }

    @Column(name = "FL_OBBLIG", columnDefinition = "char(1)")
    public String getFlObblig() {
	return this.flObblig;
    }

    public void setFlObblig(String flObblig) {
	this.flObblig = flObblig;
    }

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
	return this.niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
	this.niOrd = niOrd;
    }

    @OneToMany(mappedBy = "decTipoNotaSerie")
    public List<DecNotaTipoSerie> getDecNotaTipoSeries() {
	return this.decNotaTipoSeries;
    }

    public void setDecNotaTipoSeries(List<DecNotaTipoSerie> decNotaTipoSeries) {
	this.decNotaTipoSeries = decNotaTipoSeries;
    }

    public DecNotaTipoSerie addDecNotaTipoSery(DecNotaTipoSerie decNotaTipoSery) {
	getDecNotaTipoSeries().add(decNotaTipoSery);
	decNotaTipoSery.setDecTipoNotaSerie(this);
	return decNotaTipoSery;
    }

    public DecNotaTipoSerie removeDecNotaTipoSery(DecNotaTipoSerie decNotaTipoSery) {
	getDecNotaTipoSeries().remove(decNotaTipoSery);
	decNotaTipoSery.setDecTipoNotaSerie(null);
	return decNotaTipoSery;
    }

    @OneToMany(mappedBy = "decTipoNotaSerie")
    public List<SerNotaVerSerie> getSerNotaVerSeries() {
	return this.serNotaVerSeries;
    }

    public void setSerNotaVerSeries(List<SerNotaVerSerie> serNotaVerSeries) {
	this.serNotaVerSeries = serNotaVerSeries;
    }

    public SerNotaVerSerie addSerNotaVerSery(SerNotaVerSerie serNotaVerSery) {
	getSerNotaVerSeries().add(serNotaVerSery);
	serNotaVerSery.setDecTipoNotaSerie(this);
	return serNotaVerSery;
    }

    public SerNotaVerSerie removeSerNotaVerSery(SerNotaVerSerie serNotaVerSery) {
	getSerNotaVerSeries().remove(serNotaVerSery);
	serNotaVerSery.setDecTipoNotaSerie(null);
	return serNotaVerSery;
    }

}
