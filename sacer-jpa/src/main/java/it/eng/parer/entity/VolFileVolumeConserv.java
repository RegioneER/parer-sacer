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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the VOL_FILE_VOLUME_CONSERV database table.
 */
@Entity
@Table(name = "VOL_FILE_VOLUME_CONSERV")
public class VolFileVolumeConserv implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFileVolumeConserv;

    private byte[] blFileVolumeConserv;

    private String tiFileVolumeConserv;

    private VolVolumeConserv volVolumeConserv;

    public VolFileVolumeConserv() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_FILE_VOLUME_CONSERV")
    @GenericGenerator(name = "SVOL_FILE_VOLUME_CONSERV_ID_FILE_VOLUME_CONSERV_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SVOL_FILE_VOLUME_CONSERV"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVOL_FILE_VOLUME_CONSERV_ID_FILE_VOLUME_CONSERV_GENERATOR")
    public Long getIdFileVolumeConserv() {
        return this.idFileVolumeConserv;
    }

    public void setIdFileVolumeConserv(Long idFileVolumeConserv) {
        this.idFileVolumeConserv = idFileVolumeConserv;
    }

    @Lob()
    @Column(name = "BL_FILE_VOLUME_CONSERV")
    public byte[] getBlFileVolumeConserv() {
        return this.blFileVolumeConserv;
    }

    public void setBlFileVolumeConserv(byte[] blFileVolumeConserv) {
        this.blFileVolumeConserv = blFileVolumeConserv;
    }

    @Column(name = "TI_FILE_VOLUME_CONSERV")
    public String getTiFileVolumeConserv() {
        return this.tiFileVolumeConserv;
    }

    public void setTiFileVolumeConserv(String tiFileVolumeConserv) {
        this.tiFileVolumeConserv = tiFileVolumeConserv;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
            CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VOLUME_CONSERV")
    public VolVolumeConserv getVolVolumeConserv() {
        return this.volVolumeConserv;
    }

    public void setVolVolumeConserv(VolVolumeConserv volVolumeConserv) {
        this.volVolumeConserv = volVolumeConserv;
    }

}
