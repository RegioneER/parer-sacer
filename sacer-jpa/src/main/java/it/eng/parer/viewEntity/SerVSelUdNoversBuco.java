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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the SER_V_SEL_UD_NOVERS_BUCO database table.
 */
@Entity
@Table(name = "SER_V_SEL_UD_NOVERS_BUCO")

public class SerVSelUdNoversBuco implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idSerie;

    private BigDecimal pgUnitaDoc;

    public SerVSelUdNoversBuco() {/* Hibernate */
    }

    @Column(name = "ID_SERIE")
    public BigDecimal getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    @Column(name = "PG_UNITA_DOC")
    public BigDecimal getPgUnitaDoc() {
        return this.pgUnitaDoc;
    }

    public void setPgUnitaDoc(BigDecimal pgUnitaDoc) {
        this.pgUnitaDoc = pgUnitaDoc;
    }

    private SerVSelUdNoversBucoId serVSelUdNoversBucoId;

    @EmbeddedId()
    public SerVSelUdNoversBucoId getSerVSelUdNoversBucoId() {
        return serVSelUdNoversBucoId;
    }

    public void setSerVSelUdNoversBucoId(SerVSelUdNoversBucoId serVSelUdNoversBucoId) {
        this.serVSelUdNoversBucoId = serVSelUdNoversBucoId;
    }
}
