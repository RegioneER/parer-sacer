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

import it.eng.parer.entity.constraint.ElvUrnElencoVers.TiUrnElenco;

/**
 * The persistent class for the ELV_URN_ELENCO_VERS database table.
 */
@Entity
@Table(name = "ELV_URN_ELENCO_VERS")

public class ElvUrnElencoVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUrnElencoVers;

    private String dsUrn;

    private TiUrnElenco tiUrn;

    private ElvElencoVer elvElencoVers;

    public ElvUrnElencoVers() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_URN_ELENCO_VERS")
    @GenericGenerator(name = "SELV_URN_ELENCO_VERS_ID_URN_ELENCO_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_URN_ELENCO_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_URN_ELENCO_VERS_ID_URN_ELENCO_VERS_GENERATOR")
    public Long getIdUrnElencoVers() {
        return this.idUrnElencoVers;
    }

    public void setIdUrnElencoVers(Long idUrnElencoVers) {
        this.idUrnElencoVers = idUrnElencoVers;
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
    public TiUrnElenco getTiUrn() {
        return this.tiUrn;
    }

    public void setTiUrn(TiUrnElenco tiUrn) {
        this.tiUrn = tiUrn;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVers() {
        return this.elvElencoVers;
    }

    public void setElvElencoVers(ElvElencoVer elvElencoVers) {
        this.elvElencoVers = elvElencoVers;
    }

}
