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
 *
 * @author DiLorenzo_F
 */
public class PayLoadComparatorAnnoDtCreazione extends PayLoadComparatorDtCreazione {

    @Override
    public int compare(PayLoad p1, PayLoad p2) {
        if (p1.getTipoEntitaSacer().equals(p2.getTipoEntitaSacer())) {
            if (p1.getAaKeyUnitaDoc() > p2.getAaKeyUnitaDoc()) {
                return 1;
            } else if (p1.getAaKeyUnitaDoc() == p2.getAaKeyUnitaDoc()) { // A PARITA' DI ANNO,
                                                                         // ORDINO PER DATA
                                                                         // CREAZIONE
                return super.compare(p1, p2);
            } else {
                return -1;
            }
        } else
            return 0;
    }
}
