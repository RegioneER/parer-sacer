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

package it.eng.parer.elencoVersamento.utils;

import java.math.BigDecimal;

/**
 * Aggiornamento dell'unità documentaria presente in elenco.
 *
 * @author DiLorenzo_F
 */
public class AggiornamentoInElenco {

    private long idUpdUnitaDoc;
    private BigDecimal pgUpdUnitaDoc;

    public AggiornamentoInElenco(long idUpdUnitaDoc, BigDecimal pgUpdUnitaDoc) {
        this.idUpdUnitaDoc = idUpdUnitaDoc;
        this.pgUpdUnitaDoc = pgUpdUnitaDoc;
    }

    /**
     * Id aggiornamento.
     *
     * @return id (chiave) dell'aggiornamento
     */
    public long getIdUpdUnitaDoc() {
        return idUpdUnitaDoc;
    }

    /**
     * URN dell'aggiornamento calcolatata
     *
     * @return Urn calcolata
     */
    public BigDecimal getPgUpdUnitaDoc() {
        return pgUpdUnitaDoc;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (this.idUpdUnitaDoc ^ (this.idUpdUnitaDoc >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AggiornamentoInElenco other = (AggiornamentoInElenco) obj;
        if (this.idUpdUnitaDoc != other.idUpdUnitaDoc) {
            return false;
        }
        return true;
    }

}
