/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_CHK_UD_TIPO_UD database table.
 *
 */
@Entity
@Table(name = "MON_V_CHK_UD_TIPO_UD")

public class MonVChkUdTipoUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private String flUd30gg;
    private String flUdAttesaMem30gg;
    private String flUdAttesaMemCorr;
    private String flUdAttesaSched30gg;
    private String flUdAttesaSchedCorr;
    private String flUdCorr;
    private String flUdNoselSched30gg;
    private String flUdNoselSchedCorr;
    private BigDecimal idTipoUnitaDoc;

    public MonVChkUdTipoUd() {/* Hibernate */
    }

    @Column(name = "FL_UD_30GG", columnDefinition = "char")
    public String getFlUd30gg() {
        return this.flUd30gg;
    }

    public void setFlUd30gg(String flUd30gg) {
        this.flUd30gg = flUd30gg;
    }

    @Column(name = "FL_UD_ATTESA_MEM_30GG", columnDefinition = "char")
    public String getFlUdAttesaMem30gg() {
        return this.flUdAttesaMem30gg;
    }

    public void setFlUdAttesaMem30gg(String flUdAttesaMem30gg) {
        this.flUdAttesaMem30gg = flUdAttesaMem30gg;
    }

    @Column(name = "FL_UD_ATTESA_MEM_CORR", columnDefinition = "char(1)")
    public String getFlUdAttesaMemCorr() {
        return this.flUdAttesaMemCorr;
    }

    public void setFlUdAttesaMemCorr(String flUdAttesaMemCorr) {
        this.flUdAttesaMemCorr = flUdAttesaMemCorr;
    }

    @Column(name = "FL_UD_ATTESA_SCHED_30GG", columnDefinition = "char")
    public String getFlUdAttesaSched30gg() {
        return this.flUdAttesaSched30gg;
    }

    public void setFlUdAttesaSched30gg(String flUdAttesaSched30gg) {
        this.flUdAttesaSched30gg = flUdAttesaSched30gg;
    }

    @Column(name = "FL_UD_ATTESA_SCHED_CORR", columnDefinition = "char(1)")
    public String getFlUdAttesaSchedCorr() {
        return this.flUdAttesaSchedCorr;
    }

    public void setFlUdAttesaSchedCorr(String flUdAttesaSchedCorr) {
        this.flUdAttesaSchedCorr = flUdAttesaSchedCorr;
    }

    @Column(name = "FL_UD_CORR", columnDefinition = "char(1)")
    public String getFlUdCorr() {
        return this.flUdCorr;
    }

    public void setFlUdCorr(String flUdCorr) {
        this.flUdCorr = flUdCorr;
    }

    @Column(name = "FL_UD_NOSEL_SCHED_30GG", columnDefinition = "char")
    public String getFlUdNoselSched30gg() {
        return this.flUdNoselSched30gg;
    }

    public void setFlUdNoselSched30gg(String flUdNoselSched30gg) {
        this.flUdNoselSched30gg = flUdNoselSched30gg;
    }

    @Column(name = "FL_UD_NOSEL_SCHED_CORR", columnDefinition = "char(1)")
    public String getFlUdNoselSchedCorr() {
        return this.flUdNoselSchedCorr;
    }

    public void setFlUdNoselSchedCorr(String flUdNoselSchedCorr) {
        this.flUdNoselSchedCorr = flUdNoselSchedCorr;
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
