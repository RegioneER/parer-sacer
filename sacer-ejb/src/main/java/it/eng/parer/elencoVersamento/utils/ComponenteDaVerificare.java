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

import java.util.Date;

/**
 *
 * @author Quaranta_M
 */
public class ComponenteDaVerificare {

    private long idCompDoc;
    private Date dtCreazione;
    private String flCompFirmato;

    public ComponenteDaVerificare(long idCompDoc, java.util.Date dtCreazione,
            String flCompFirmato) {
        this.idCompDoc = idCompDoc;
        this.dtCreazione = dtCreazione;
        this.flCompFirmato = flCompFirmato;

    }

    public long getIdCompDoc() {
        return idCompDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public String getFlCompFirmato() {
        return flCompFirmato;
    }

    public boolean isFlCompFirmato() {
        return flCompFirmato != null && flCompFirmato.equalsIgnoreCase("1");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (int) (this.idCompDoc ^ (this.idCompDoc >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponenteDaVerificare other = (ComponenteDaVerificare) obj;
        if (this.idCompDoc != other.idCompDoc) {
            return false;
        }
        return true;
    }

}
