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
 * The persistent class for the SER_V_CHK_DEF_UD_NOVERS_BUCO database table.
 *
 */
@Entity
@Table(name = "SER_V_CHK_DEF_UD_NOVERS_BUCO")

public class SerVChkDefUdNoversBuco implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flDefUdNonVers;
    private BigDecimal idSerie;
    private BigDecimal idVerSerie;

    public SerVChkDefUdNoversBuco() {/* Hibernate */
    }

    @Column(name = "FL_DEF_UD_NON_VERS", columnDefinition = "char(1)")
    public String getFlDefUdNonVers() {
        return this.flDefUdNonVers;
    }

    public void setFlDefUdNonVers(String flDefUdNonVers) {
        this.flDefUdNonVers = flDefUdNonVers;
    }

    @Column(name = "ID_SERIE")
    public BigDecimal getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    @Id
    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

}
