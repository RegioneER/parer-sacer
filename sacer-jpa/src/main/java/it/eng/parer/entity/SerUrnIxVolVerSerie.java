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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.SerUrnIxVolVerSerie.TiUrnIxVolVerSerie;

/**
 * The persistent class for the SER_URN_IX_VOL_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_URN_IX_VOL_VER_SERIE")

public class SerUrnIxVolVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUrnIxVolVerSerie;

    private String dsUrn;

    private TiUrnIxVolVerSerie tiUrn;

    private SerIxVolVerSerie serIxVolVerSerie;

    public SerUrnIxVolVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_URN_IX_VOL_VER_SERIE")
    @GenericGenerator(name = "SSER_URN_IX_VOL_VER_SERIE_ID_URN_IX_VOL_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_URN_IX_VOL_VER_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_URN_IX_VOL_VER_SERIE_ID_URN_IX_VOL_VER_SERIE_GENERATOR")
    public Long getIdUrnIxVolVerSerie() {
	return this.idUrnIxVolVerSerie;
    }

    public void setIdUrnIxVolVerSerie(Long idUrnIxVolVerSerie) {
	this.idUrnIxVolVerSerie = idUrnIxVolVerSerie;
    }

    @Column(name = "DS_URN")
    public String getDsUrn() {
	return this.dsUrn;
    }

    public void setDsUrn(String dsUrn) {
	this.dsUrn = dsUrn;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_URN")
    public TiUrnIxVolVerSerie getTiUrn() {
	return this.tiUrn;
    }

    public void setTiUrn(TiUrnIxVolVerSerie tiUrn) {
	this.tiUrn = tiUrn;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_IX_VOL_VER_SERIE")
    public SerIxVolVerSerie getSerIxVolVerSerie() {
	return this.serIxVolVerSerie;
    }

    public void setSerIxVolVerSerie(SerIxVolVerSerie serIxVolVerSerie) {
	this.serIxVolVerSerie = serIxVolVerSerie;
    }

}
