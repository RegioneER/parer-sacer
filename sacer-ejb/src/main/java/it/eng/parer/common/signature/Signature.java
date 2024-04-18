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

package it.eng.parer.common.signature;

/**
 * Constants about the signature methods
 *
 * @author Moretti_Lu
 */
public abstract class Signature {

    private Signature() {
    }

    /**
     * Costante utilizzata come parametro per IL CAMPO NM_PARAM_APPLICA della APL_PARAM_APPLIC
     */
    public static final String SISTEMA_FIRMA = "SISTEMA_FIRMA";

    /**
     * Costante utilizzata come parametro per la sessione di firma degli elenchi nell'action
     */
    public static final String FUTURE_ATTR_ELENCHI = "ATTR_FUTURE_SIGNATURE_ELENCHI";

    /**
     * Costante utilizzata come parametro per la sessione di firma degli elenchi di versamento fascicoli nell'action
     */
    public static final String FUTURE_ATTR_ELENCHI_FASC = "ATTR_FUTURE_SIGNATURE_ELENCHI_FASC";

    /**
     * Costante utilizzata come parametro per la sessione di firma degli elenchi indici AIP nell'action
     */
    public static final String FUTURE_ATTR_ELENCHI_INDICI_AIP = "ATTR_FUTURE_SIGNATURE_ELENCHI_INDICI_AIP";

    /**
     * Costante utilizzata come parametro per la sessione di firma degli elenchi indici AIP fascicoli nell'action
     */
    public static final String FUTURE_ATTR_ELENCHI_INDICI_AIP_FASC = "ATTR_FUTURE_SIGNATURE_ELENCHI_INDICI_AIP_FASC";

    /**
     * Costante utilizzata come parametro per la sessione di firma delle serie nell'action
     */
    public static final String FUTURE_ATTR_SERIE = "ATTR_FUTURE_SIGNATURE_SERIE";

    /**
     * Costante utilizzata come stato di assenza sessione di firma per il polling JS
     */
    public static final String NO_SESSION = "NO_SESSION";
    /**
     * Costante utilizzata come stato di lavorazione sessione di firma per il polling JS
     */
    public static final String WORKING = "WORKING";

    /**
     * The kinds of signature methods
     */
    public enum SistemaFirma {
        HSM_TEST, HSM_PROD
    }
}
