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

package it.eng.parer.elencoVersFascicoli.utils;

import java.math.BigDecimal;
import java.util.Date;

import it.eng.parer.web.util.Constants.TipoEntitaSacer;

/**
 *
 * @author DiLorenzo_F
 */
public class FasFascicoloObj {

    private BigDecimal id;
    private TipoEntitaSacer tiEntitaSacer;
    private BigDecimal aaFascicolo;
    private Date tsVersFascicolo;

    public FasFascicoloObj(BigDecimal id, TipoEntitaSacer tiEntitaSacer, BigDecimal aaFascicolo,
            Date tsVersFascicolo) {
        this.id = id;
        this.tiEntitaSacer = tiEntitaSacer;
        this.aaFascicolo = aaFascicolo;
        this.tsVersFascicolo = tsVersFascicolo;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public TipoEntitaSacer getTiEntitaSacer() {
        return tiEntitaSacer;
    }

    public void setTiEntitaSacer(TipoEntitaSacer tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public BigDecimal getAaFascicolo() {
        return aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    public Date getTsVersFascicolo() {
        return tsVersFascicolo;
    }

    public void setTsVersFascicolo(Date tsVersFascicolo) {
        this.tsVersFascicolo = tsVersFascicolo;
    }
}
