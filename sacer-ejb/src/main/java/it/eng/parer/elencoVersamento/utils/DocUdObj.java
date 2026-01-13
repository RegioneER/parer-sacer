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

package it.eng.parer.elencoVersamento.utils;

import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Agati_D modified by Gilioli_P
 */
public class DocUdObj {

    private BigDecimal id;
    private TipoEntitaSacer tiEntitaSacer;
    private BigDecimal aaKeyUnitaDoc;
    private Date dtCreazione;

    public DocUdObj(BigDecimal id, TipoEntitaSacer tiEntitaSacer, BigDecimal aaKeyUnitadoc,
            Date dtCreazione) {
        this.id = id;
        this.tiEntitaSacer = tiEntitaSacer;
        this.aaKeyUnitaDoc = aaKeyUnitadoc;
        this.dtCreazione = dtCreazione;
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

    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }
}
