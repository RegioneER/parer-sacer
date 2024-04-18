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

import javax.persistence.*;

/**
 * The persistent class for the MON_V_CHK_FASC_BY_ENTE database table.
 *
 */
@Entity
@Table(name = "MON_V_CHK_FASC_BY_ENTE")
@NamedQuery(name = "MonVChkFascByEnte.findByEnteUser", query = "SELECT m FROM MonVChkFascByEnte m WHERE m.monVChkFascByEnteId.idEnte = :idEnte AND m.monVChkFascByEnteId.idUserIam = :idUser")
public class MonVChkFascByEnte implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flFasc30gg;
    private String flFascAttesaSched30gg;
    private String flFascAttesaSchedB30gg;
    private String flFascAttesaSchedCorr;
    private String flFascB30gg;
    private String flFascCorr;
    private String flFascNoselSched30gg;
    private String flFascNoselSchedB30gg;
    private String flFascNoselSchedCorr;
    private MonVChkFascByEnteId monVChkFascByEnteId;

    @EmbeddedId
    public MonVChkFascByEnteId getMonVChkFascByEnteId() {
        return monVChkFascByEnteId;
    }

    public void setMonVChkFascByEnteId(MonVChkFascByEnteId monVChkFascByEnteId) {
        this.monVChkFascByEnteId = monVChkFascByEnteId;
    }

    public MonVChkFascByEnte() {/* Hibernate */
    }

    @Column(name = "FL_FASC_30GG", columnDefinition = "char")
    public String getFlFasc30gg() {
        return this.flFasc30gg;
    }

    public void setFlFasc30gg(String flFasc30gg) {
        this.flFasc30gg = flFasc30gg;
    }

    @Column(name = "FL_FASC_ATTESA_SCHED_30GG", columnDefinition = "char")
    public String getFlFascAttesaSched30gg() {
        return this.flFascAttesaSched30gg;
    }

    public void setFlFascAttesaSched30gg(String flFascAttesaSched30gg) {
        this.flFascAttesaSched30gg = flFascAttesaSched30gg;
    }

    @Column(name = "FL_FASC_ATTESA_SCHED_B30GG", columnDefinition = "char")
    public String getFlFascAttesaSchedB30gg() {
        return this.flFascAttesaSchedB30gg;
    }

    public void setFlFascAttesaSchedB30gg(String flFascAttesaSchedB30gg) {
        this.flFascAttesaSchedB30gg = flFascAttesaSchedB30gg;
    }

    @Column(name = "FL_FASC_ATTESA_SCHED_CORR", columnDefinition = "char(1)")
    public String getFlFascAttesaSchedCorr() {
        return this.flFascAttesaSchedCorr;
    }

    public void setFlFascAttesaSchedCorr(String flFascAttesaSchedCorr) {
        this.flFascAttesaSchedCorr = flFascAttesaSchedCorr;
    }

    @Column(name = "FL_FASC_B30GG", columnDefinition = "char")
    public String getFlFascB30gg() {
        return this.flFascB30gg;
    }

    public void setFlFascB30gg(String flFascB30gg) {
        this.flFascB30gg = flFascB30gg;
    }

    @Column(name = "FL_FASC_CORR", columnDefinition = "char(1)")
    public String getFlFascCorr() {
        return this.flFascCorr;
    }

    public void setFlFascCorr(String flFascCorr) {
        this.flFascCorr = flFascCorr;
    }

    @Column(name = "FL_FASC_NOSEL_SCHED_30GG", columnDefinition = "char")
    public String getFlFascNoselSched30gg() {
        return this.flFascNoselSched30gg;
    }

    public void setFlFascNoselSched30gg(String flFascNoselSched30gg) {
        this.flFascNoselSched30gg = flFascNoselSched30gg;
    }

    @Column(name = "FL_FASC_NOSEL_SCHED_B30GG", columnDefinition = "char")
    public String getFlFascNoselSchedB30gg() {
        return this.flFascNoselSchedB30gg;
    }

    public void setFlFascNoselSchedB30gg(String flFascNoselSchedB30gg) {
        this.flFascNoselSchedB30gg = flFascNoselSchedB30gg;
    }

    @Column(name = "FL_FASC_NOSEL_SCHED_CORR", columnDefinition = "char(1)")
    public String getFlFascNoselSchedCorr() {
        return this.flFascNoselSchedCorr;
    }

    public void setFlFascNoselSchedCorr(String flFascNoselSchedCorr) {
        this.flFascNoselSchedCorr = flFascNoselSchedCorr;
    }

}
