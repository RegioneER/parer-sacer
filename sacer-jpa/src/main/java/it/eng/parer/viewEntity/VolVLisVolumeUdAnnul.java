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

package it.eng.parer.viewEntity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the VOL_V_LIS_VOLUME_UD_ANNUL database table.
 */
@Entity
@Table(name = "VOL_V_LIS_VOLUME_UD_ANNUL")

public class VolVLisVolumeUdAnnul implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ntVolumeChiuso;

    public VolVLisVolumeUdAnnul() {/* Hibernate */
    }

    @Column(name = "NT_VOLUME_CHIUSO")
    public String getNtVolumeChiuso() {
        return this.ntVolumeChiuso;
    }

    public void setNtVolumeChiuso(String ntVolumeChiuso) {
        this.ntVolumeChiuso = ntVolumeChiuso;
    }

    private VolVLisVolumeUdAnnulId volVLisVolumeUdAnnulId;

    @EmbeddedId()
    public VolVLisVolumeUdAnnulId getVolVLisVolumeUdAnnulId() {
        return volVLisVolumeUdAnnulId;
    }

    public void setVolVLisVolumeUdAnnulId(VolVLisVolumeUdAnnulId volVLisVolumeUdAnnulId) {
        this.volVLisVolumeUdAnnulId = volVLisVolumeUdAnnulId;
    }
}
