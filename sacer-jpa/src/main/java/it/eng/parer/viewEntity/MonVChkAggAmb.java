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
 * The persistent class for the MON_V_CHK_AGG_AMB database table.
 * 
 */
@Entity
@Table(name = "MON_V_CHK_AGG_AMB")

public class MonVChkAggAmb implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flAggNorisolub30gg;
    private String flAggNorisolubCorr;
    private String flAggNoverif30gg;
    private String flAggNoverifCorr;
    private String flAggRisolti30gg;
    private String flAggRisoltiCorr;
    private String flAggVerif30gg;
    private String flAggVerifCorr;
    private MonVChkAggAmbId monVChkAggAmbId;

    @EmbeddedId
    public MonVChkAggAmbId getMonVChkAggAmbId() {
        return monVChkAggAmbId;
    }

    public void setMonVChkAggAmbId(MonVChkAggAmbId monVChkAggAmbId) {
        this.monVChkAggAmbId = monVChkAggAmbId;
    }

    public MonVChkAggAmb() {/* Hibernate */
    }

    @Column(name = "FL_AGG_NORISOLUB_30GG", columnDefinition = "char")
    public String getFlAggNorisolub30gg() {
        return this.flAggNorisolub30gg;
    }

    public void setFlAggNorisolub30gg(String flAggNorisolub30gg) {
        this.flAggNorisolub30gg = flAggNorisolub30gg;
    }

    @Column(name = "FL_AGG_NORISOLUB_CORR", columnDefinition = "char(1)")
    public String getFlAggNorisolubCorr() {
        return this.flAggNorisolubCorr;
    }

    public void setFlAggNorisolubCorr(String flAggNorisolubCorr) {
        this.flAggNorisolubCorr = flAggNorisolubCorr;
    }

    @Column(name = "FL_AGG_NOVERIF_30GG", columnDefinition = "char")
    public String getFlAggNoverif30gg() {
        return this.flAggNoverif30gg;
    }

    public void setFlAggNoverif30gg(String flAggNoverif30gg) {
        this.flAggNoverif30gg = flAggNoverif30gg;
    }

    @Column(name = "FL_AGG_NOVERIF_CORR", columnDefinition = "char(1)")
    public String getFlAggNoverifCorr() {
        return this.flAggNoverifCorr;
    }

    public void setFlAggNoverifCorr(String flAggNoverifCorr) {
        this.flAggNoverifCorr = flAggNoverifCorr;
    }

    @Column(name = "FL_AGG_RISOLTI_30GG", columnDefinition = "char")
    public String getFlAggRisolti30gg() {
        return this.flAggRisolti30gg;
    }

    public void setFlAggRisolti30gg(String flAggRisolti30gg) {
        this.flAggRisolti30gg = flAggRisolti30gg;
    }

    @Column(name = "FL_AGG_RISOLTI_CORR", columnDefinition = "char(1)")
    public String getFlAggRisoltiCorr() {
        return this.flAggRisoltiCorr;
    }

    public void setFlAggRisoltiCorr(String flAggRisoltiCorr) {
        this.flAggRisoltiCorr = flAggRisoltiCorr;
    }

    @Column(name = "FL_AGG_VERIF_30GG", columnDefinition = "char")
    public String getFlAggVerif30gg() {
        return this.flAggVerif30gg;
    }

    public void setFlAggVerif30gg(String flAggVerif30gg) {
        this.flAggVerif30gg = flAggVerif30gg;
    }

    @Column(name = "FL_AGG_VERIF_CORR", columnDefinition = "char(1)")
    public String getFlAggVerifCorr() {
        return this.flAggVerifCorr;
    }

    public void setFlAggVerifCorr(String flAggVerifCorr) {
        this.flAggVerifCorr = flAggVerifCorr;
    }

}
