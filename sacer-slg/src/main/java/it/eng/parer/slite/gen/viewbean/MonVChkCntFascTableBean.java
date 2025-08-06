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

package it.eng.parer.slite.gen.viewbean;

import java.util.Iterator;

/**
 * ViewBean per la vista
 *
 */
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * TableBean per la tabella
 *
 */
public class MonVChkCntFascTableBean extends AbstractBaseTable<MonVChkCntFascRowBean> {

    private static final long serialVersionUID = 1L;

    public static MonVChkCntFascTableDescriptor TABLE_DESCRIPTOR = new MonVChkCntFascTableDescriptor();

    public MonVChkCntFascTableBean() {
	super();
    }

    protected MonVChkCntFascRowBean createRow() {
	return new MonVChkCntFascRowBean();
    }

    public TableDescriptor getTableDescriptor() {
	return TABLE_DESCRIPTOR;
    }

    @Deprecated
    public Iterator<MonVChkCntFascRowBean> getRowsIterator() {
	return iterator();
    }
}
