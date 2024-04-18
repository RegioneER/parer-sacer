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

package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;

/**
 * The persistent class for the DEC_VERSIONE_WS database table.
 * 
 */
@Entity
@Table(name = "DEC_VERSIONE_WS")

public class DecVersioneWs implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdVersioneWs;
    private BigDecimal idVersioneWs;
    private String tiWs;

    public DecVersioneWs() {
    }

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
        return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
        this.cdVersioneWs = cdVersioneWs;
    }

    @Id
    @Column(name = "ID_VERSIONE_WS")
    public BigDecimal getIdVersioneWs() {
        return this.idVersioneWs;
    }

    public void setIdVersioneWs(BigDecimal idVersioneWs) {
        this.idVersioneWs = idVersioneWs;
    }

    @Column(name = "TI_WS")
    public String getTiWs() {
        return this.tiWs;
    }

    public void setTiWs(String tiWs) {
        this.tiWs = tiWs;
    }

}
