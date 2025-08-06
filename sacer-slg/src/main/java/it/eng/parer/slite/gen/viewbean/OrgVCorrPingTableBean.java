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

import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * Table bean per la tabella Org_V_Corr_Ping
 *
 * @author gpiccioli
 */
public class OrgVCorrPingTableBean extends AbstractBaseTable<OrgVCorrPingRowBean> {
    private static final long serialVersionUID = 1L;

    public static OrgVCorrPingTableDescriptor TABLE_DESCRIPTOR = new OrgVCorrPingTableDescriptor();

    public OrgVCorrPingTableBean() {
	super();
    }

    public TableDescriptor getTableDescriptor() {
	return TABLE_DESCRIPTOR;
    }

    protected OrgVCorrPingRowBean createRow() {
	return new OrgVCorrPingRowBean();
    }

}
