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
 * The persistent class for the MON_V_CHK_DOC_TIPO_UD database table.
 *
 */
@Entity
@Table(name = "MON_V_CHK_DOC_TIPO_UD")

public class MonVChkDocTipoUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flDoc30gg;
    private String flDocAttesaMem30gg;
    private String flDocAttesaMemCorr;
    private String flDocAttesaSched30gg;
    private String flDocAttesaSchedCorr;
    private String flDocCorr;
    private String flDocNoselSched30gg;
    private String flDocNoselSchedCorr;
    private BigDecimal idTipoUnitaDoc;

    public MonVChkDocTipoUd() {/* Hibernate */
    }

    @Column(name = "FL_DOC_30GG", columnDefinition = "char")
    public String getFlDoc30gg() {
        return this.flDoc30gg;
    }

    public void setFlDoc30gg(String flDoc30gg) {
        this.flDoc30gg = flDoc30gg;
    }

    @Column(name = "FL_DOC_ATTESA_MEM_30GG", columnDefinition = "char")
    public String getFlDocAttesaMem30gg() {
        return this.flDocAttesaMem30gg;
    }

    public void setFlDocAttesaMem30gg(String flDocAttesaMem30gg) {
        this.flDocAttesaMem30gg = flDocAttesaMem30gg;
    }

    @Column(name = "FL_DOC_ATTESA_MEM_CORR", columnDefinition = "char(1)")
    public String getFlDocAttesaMemCorr() {
        return this.flDocAttesaMemCorr;
    }

    public void setFlDocAttesaMemCorr(String flDocAttesaMemCorr) {
        this.flDocAttesaMemCorr = flDocAttesaMemCorr;
    }

    @Column(name = "FL_DOC_ATTESA_SCHED_30GG", columnDefinition = "char")
    public String getFlDocAttesaSched30gg() {
        return this.flDocAttesaSched30gg;
    }

    public void setFlDocAttesaSched30gg(String flDocAttesaSched30gg) {
        this.flDocAttesaSched30gg = flDocAttesaSched30gg;
    }

    @Column(name = "FL_DOC_ATTESA_SCHED_CORR", columnDefinition = "char(1)")
    public String getFlDocAttesaSchedCorr() {
        return this.flDocAttesaSchedCorr;
    }

    public void setFlDocAttesaSchedCorr(String flDocAttesaSchedCorr) {
        this.flDocAttesaSchedCorr = flDocAttesaSchedCorr;
    }

    @Column(name = "FL_DOC_CORR", columnDefinition = "char(1)")
    public String getFlDocCorr() {
        return this.flDocCorr;
    }

    public void setFlDocCorr(String flDocCorr) {
        this.flDocCorr = flDocCorr;
    }

    @Column(name = "FL_DOC_NOSEL_SCHED_30GG", columnDefinition = "char")
    public String getFlDocNoselSched30gg() {
        return this.flDocNoselSched30gg;
    }

    public void setFlDocNoselSched30gg(String flDocNoselSched30gg) {
        this.flDocNoselSched30gg = flDocNoselSched30gg;
    }

    @Column(name = "FL_DOC_NOSEL_SCHED_CORR", columnDefinition = "char(1)")
    public String getFlDocNoselSchedCorr() {
        return this.flDocNoselSchedCorr;
    }

    public void setFlDocNoselSchedCorr(String flDocNoselSchedCorr) {
        this.flDocNoselSchedCorr = flDocNoselSchedCorr;
    }

    @Id
    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

}
