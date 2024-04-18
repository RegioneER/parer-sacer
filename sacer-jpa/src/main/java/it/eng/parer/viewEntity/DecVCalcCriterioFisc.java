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
 * The persistent class for the DEC_V_CALC_CRITERIO_FISC database table.
 *
 */
@Entity
@Table(name = "DEC_V_CALC_CRITERIO_FISC")

public class DecVCalcCriterioFisc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsMessaggio;
    private BigDecimal idCriterioRaggr;
    private String nmCriterioRaggr;

    public DecVCalcCriterioFisc() {/* Hibernate */
    }

    @Column(name = "DS_MESSAGGIO")
    public String getDsMessaggio() {
        return this.dsMessaggio;
    }

    public void setDsMessaggio(String dsMessaggio) {
        this.dsMessaggio = dsMessaggio;
    }

    @Id
    @Column(name = "ID_CRITERIO_RAGGR")
    public BigDecimal getIdCriterioRaggr() {
        return this.idCriterioRaggr;
    }

    public void setIdCriterioRaggr(BigDecimal idCriterioRaggr) {
        this.idCriterioRaggr = idCriterioRaggr;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
        return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
        this.nmCriterioRaggr = nmCriterioRaggr;
    }

}
