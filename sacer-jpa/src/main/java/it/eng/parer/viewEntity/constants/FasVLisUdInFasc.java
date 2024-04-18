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

package it.eng.parer.viewEntity.constants;

/**
 * FasVLisUdInFasc's constraint
 *
 * @author Moretti_Lu
 */
public final class FasVLisUdInFasc {

    private FasVLisUdInFasc() {
    }

    /**
     * Tipo dello stato in conservazione
     */
    public enum TiStatoConservazione {
        AIP_DA_GENERARE, AIP_FIRMATO, AIP_GENERATO, AIP_IN_AGGIORNAMENTO, ANNULLATA, IN_ARCHIVIO, IN_CUSTODIA,
        IN_VOLUME_DI_CONSERVAZIONE, PRESA_IN_CARICO, VERSAMENTO_IN_ARCHIVIO
    }
}
