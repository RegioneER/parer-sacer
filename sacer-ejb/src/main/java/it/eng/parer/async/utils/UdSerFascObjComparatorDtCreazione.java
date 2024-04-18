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

package it.eng.parer.async.utils;

import java.util.Comparator;

/**
 *
 * @author DiLorenzo_F
 */
public class UdSerFascObjComparatorDtCreazione implements Comparator<UdSerFascObj> {

    @Override
    public int compare(UdSerFascObj o1, UdSerFascObj o2) {
        /* Tip: Per ottimizzare l'estrazione bisogna ordinare il result set. Per ora gestisco i file in ordine sparso */
        if (o1.getTiEntitaSacer().equals(o2.getTiEntitaSacer())) {
            // if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() < 0) {
            // return -1;
            // } else if (o1.getDtCreazione().getTime() - o2.getDtCreazione().getTime() > 0) {
            // return 1;
            // } else {
            return 0;
            // }
        } else
            return 0;
    }
}
