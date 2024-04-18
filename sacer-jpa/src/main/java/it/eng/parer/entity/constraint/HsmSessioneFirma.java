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

package it.eng.parer.entity.constraint;

/**
 * HsmSessioneFirma constraint
 *
 * @author DiLorenzo_F
 */
public final class HsmSessioneFirma {

    private HsmSessioneFirma() {
    }

    /**
     * ti_sessione_firma IN ('ELENCHI', 'ELENCHI_FASC', 'ELENCHI_INDICI_AIP_FASC', 'ELENCO_INDICI_AIP', 'SERIE')
     */
    public enum TiSessioneFirma {
        ELENCHI, SERIE, ELENCO_INDICI_AIP, ELENCHI_FASC, ELENCHI_INDICI_AIP_FASC
    }

    /**
     * ti_esito_sessione_firma IN ('ERRORE', 'OK', 'WARNING')
     */
    public enum TiEsitoSessioneFirma {
        OK, WARNING, ERRORE
    }
}
