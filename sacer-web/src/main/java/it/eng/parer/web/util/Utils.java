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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.util;

import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import java.math.BigDecimal;
import java.util.Iterator;

/**
 *
 * @author Iacolucci_M
 *
 *         Dato un table Beanfa una query su nmDesc usando come valore descQuery e torna il valore della colonna nmKey.
 *         Usato per ottenere da un combo box il valore ID data una descrizione
 */
public class Utils {
    public static final BigDecimal getDecodedBigDecimalFromTablebean(AbstractBaseTable tb, String nmKey, String nmDesc,
            String descQuery) {
        BigDecimal id = null;
        if (tb != null) {
            Iterator<BaseRow> it = tb.iterator();
            while (it.hasNext()) {
                BaseRow r = it.next();
                if (r.getString(nmDesc).equals(descQuery)) {
                    id = r.getBigDecimal(nmKey);
                    break;
                }
            }
        }
        return id;
    }
}
