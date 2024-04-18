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

/**
 * Componente presente in elenco con stato IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.
 *
 * @author Snidero_L
 */
public class UnitaDocumentariaInElenco {

    private long idUnitaDoc;
    private boolean flSoloDocAggiunti;

    public UnitaDocumentariaInElenco(long idUnitaDoc, Boolean flSoloDocAggiunti) {
        this.idUnitaDoc = idUnitaDoc;
        this.flSoloDocAggiunti = flSoloDocAggiunti;
    }

    /**
     * Id Unità documentaria
     *
     * @return long id
     */
    public long getIdUnitaDoc() {
        return idUnitaDoc;
    }

    /**
     * Ritorna vero se e solo se l'id in elenco è stato calcolato solo a causa di documenti aggiunti.
     *
     * @return boolean true o false
     */
    public boolean isFlSoloDocAggiunti() {
        return flSoloDocAggiunti;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (int) (this.idUnitaDoc ^ (this.idUnitaDoc >>> 32));
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
        final UnitaDocumentariaInElenco other = (UnitaDocumentariaInElenco) obj;
        if (this.idUnitaDoc != other.idUnitaDoc) {
            return false;
        }
        return true;
    }

}
