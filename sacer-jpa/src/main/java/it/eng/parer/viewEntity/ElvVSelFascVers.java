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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ELV_V_SEL_FASC_VERS database table.
 */
@Entity
@Table(name = "ELV_V_SEL_FASC_VERS")

public class ElvVSelFascVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal aaFascicolo;

    private Date tsVersFascicolo;

    public ElvVSelFascVers() {/* Hibernate */
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_VERS_FASCICOLO")
    public Date getTsVersFascicolo() {
        return this.tsVersFascicolo;
    }

    public void setTsVersFascicolo(Date tsVersFascicolo) {
        this.tsVersFascicolo = tsVersFascicolo;
    }

    private ElvVSelFascVersId elvVSelFascVersId;

    @EmbeddedId()
    public ElvVSelFascVersId getElvVSelFascVersId() {
        return elvVSelFascVersId;
    }

    public void setElvVSelFascVersId(ElvVSelFascVersId elvVSelFascVersId) {
        this.elvVSelFascVersId = elvVSelFascVersId;
    }
}
