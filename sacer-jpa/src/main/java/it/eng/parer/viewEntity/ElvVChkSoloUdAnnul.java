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
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the ELV_V_CHK_SOLO_UD_ANNUL database table.
 *
 */
@Entity
@Table(name = "ELV_V_CHK_SOLO_UD_ANNUL")

public class ElvVChkSoloUdAnnul implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flSoloDocAnnul;
    private String flSoloUdAnnul;
    private String flSoloUpdUdAnnul;
    private BigDecimal idElencoVers;

    public ElvVChkSoloUdAnnul() {/* Hibernate */
    }

    @Column(name = "FL_SOLO_DOC_ANNUL", columnDefinition = "char(1)")
    public String getFlSoloDocAnnul() {
        return this.flSoloDocAnnul;
    }

    public void setFlSoloDocAnnul(String flSoloDocAnnul) {
        this.flSoloDocAnnul = flSoloDocAnnul;
    }

    @Column(name = "FL_SOLO_UD_ANNUL", columnDefinition = "char(1)")
    public String getFlSoloUdAnnul() {
        return this.flSoloUdAnnul;
    }

    public void setFlSoloUdAnnul(String flSoloUdAnnul) {
        this.flSoloUdAnnul = flSoloUdAnnul;
    }

    @Column(name = "FL_SOLO_UPD_UD_ANNUL", columnDefinition = "char(1)")
    public String getFlSoloUpdUdAnnul() {
        return flSoloUpdUdAnnul;
    }

    public void setFlSoloUpdUdAnnul(String flSoloUpdUdAnnul) {
        this.flSoloUpdUdAnnul = flSoloUpdUdAnnul;
    }

    @Id
    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

}
