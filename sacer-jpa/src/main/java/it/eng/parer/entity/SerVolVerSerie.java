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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_VOL_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_VOL_VER_SERIE")

public class SerVolVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVolVerSerie;

    private BigDecimal idFirstUdAppartVol;

    private BigDecimal idLastUdAppartVol;

    private BigDecimal niUnitaDocVol;

    private BigDecimal pgVolVerSerie;

    private List<AroUdAppartVerSerie> aroUdAppartVerSeries = new ArrayList<>();

    private SerVerSerie serVerSerie;

    private List<SerIxVolVerSerie> serIxVolVerSeries = new ArrayList<>();

    public SerVolVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_VOL_VER_SERIE")
    @GenericGenerator(name = "SSER_VOL_VER_SERIE_ID_VOL_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_VOL_VER_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_VOL_VER_SERIE_ID_VOL_VER_SERIE_GENERATOR")
    public Long getIdVolVerSerie() {
        return this.idVolVerSerie;
    }

    public void setIdVolVerSerie(Long idVolVerSerie) {
        this.idVolVerSerie = idVolVerSerie;
    }

    @Column(name = "ID_FIRST_UD_APPART_VOL")
    public BigDecimal getIdFirstUdAppartVol() {
        return this.idFirstUdAppartVol;
    }

    public void setIdFirstUdAppartVol(BigDecimal idFirstUdAppartVol) {
        this.idFirstUdAppartVol = idFirstUdAppartVol;
    }

    @Column(name = "ID_LAST_UD_APPART_VOL")
    public BigDecimal getIdLastUdAppartVol() {
        return this.idLastUdAppartVol;
    }

    public void setIdLastUdAppartVol(BigDecimal idLastUdAppartVol) {
        this.idLastUdAppartVol = idLastUdAppartVol;
    }

    @Column(name = "NI_UNITA_DOC_VOL")
    public BigDecimal getNiUnitaDocVol() {
        return this.niUnitaDocVol;
    }

    public void setNiUnitaDocVol(BigDecimal niUnitaDocVol) {
        this.niUnitaDocVol = niUnitaDocVol;
    }

    @Column(name = "PG_VOL_VER_SERIE")
    public BigDecimal getPgVolVerSerie() {
        return this.pgVolVerSerie;
    }

    public void setPgVolVerSerie(BigDecimal pgVolVerSerie) {
        this.pgVolVerSerie = pgVolVerSerie;
    }

    @OneToMany(mappedBy = "serVolVerSerie")
    public List<AroUdAppartVerSerie> getAroUdAppartVerSeries() {
        return this.aroUdAppartVerSeries;
    }

    public void setAroUdAppartVerSeries(List<AroUdAppartVerSerie> aroUdAppartVerSeries) {
        this.aroUdAppartVerSeries = aroUdAppartVerSeries;
    }

    public AroUdAppartVerSerie addAroUdAppartVerSery(AroUdAppartVerSerie aroUdAppartVerSery) {
        getAroUdAppartVerSeries().add(aroUdAppartVerSery);
        aroUdAppartVerSery.setSerVolVerSerie(this);
        return aroUdAppartVerSery;
    }

    public AroUdAppartVerSerie removeAroUdAppartVerSery(AroUdAppartVerSerie aroUdAppartVerSery) {
        getAroUdAppartVerSeries().remove(aroUdAppartVerSery);
        aroUdAppartVerSery.setSerVolVerSerie(null);
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

    @OneToMany(mappedBy = "serVolVerSerie")
    public List<SerIxVolVerSerie> getSerIxVolVerSeries() {
        return this.serIxVolVerSeries;
    }

    public void setSerIxVolVerSeries(List<SerIxVolVerSerie> serIxVolVerSeries) {
        this.serIxVolVerSeries = serIxVolVerSeries;
    }

    public SerIxVolVerSerie addSerIxVolVerSery(SerIxVolVerSerie serIxVolVerSery) {
        getSerIxVolVerSeries().add(serIxVolVerSery);
        serIxVolVerSery.setSerVolVerSerie(this);
        return serIxVolVerSery;
    }

    public SerIxVolVerSerie removeSerIxVolVerSery(SerIxVolVerSerie serIxVolVerSery) {
        getSerIxVolVerSeries().remove(serIxVolVerSery);
        serIxVolVerSery.setSerVolVerSerie(null);
        return serIxVolVerSery;
    }

}
