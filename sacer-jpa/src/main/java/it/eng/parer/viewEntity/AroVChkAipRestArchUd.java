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
 * The persistent class for the ARO_V_CHK_AIP_REST_ARCH_UD database table.
 *
 */
@Entity
@Table(name = "ARO_V_CHK_AIP_REST_ARCH_UD")

public class AroVChkAipRestArchUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flEstratto;
    private String flErrore;
    private String flDaElaborare;
    private String flAllDaElab;
    private BigDecimal idRichiestaRa;

    public AroVChkAipRestArchUd() {/* Hibernate */
    }

    @Column(name = "FL_ESTRATTO", columnDefinition = "number")
    public String getFlEstratto() {
        return this.flEstratto;
    }

    public void setFlEstratto(String flEstratto) {
        this.flEstratto = flEstratto;
    }

    @Column(name = "FL_ERRORE", columnDefinition = "number")
    public String getFlErrore() {
        return this.flErrore;
    }

    public void setFlErrore(String flErrore) {
        this.flErrore = flErrore;
    }

    @Column(name = "FL_DA_ELABORARE", columnDefinition = "number")
    public String getFlDaElaborare() {
        return flDaElaborare;
    }

    public void setFlDaElaborare(String flDaElaborare) {
        this.flDaElaborare = flDaElaborare;
    }

    @Column(name = "FL_ALL_DA_ELAB", columnDefinition = "number")
    public String getFlAllDaElab() {
        return flAllDaElab;
    }

    public void setFlAllDaElab(String flAllDaElab) {
        this.flAllDaElab = flAllDaElab;
    }

    @Id
    @Column(name = "ID_RICHIESTA_RA")
    public BigDecimal getIdRichiestaRa() {
        return this.idRichiestaRa;
    }

    public void setIdRichiestaRa(BigDecimal idRichiestaRa) {
        this.idRichiestaRa = idRichiestaRa;
    }

}
