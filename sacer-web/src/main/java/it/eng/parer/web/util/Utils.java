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
