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

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella AroVChkStatoCorRichSoftDelete
 *
 */
public class AroVChkStatoCorRichSoftDeleteTableDescriptor extends TableDescriptor {

    public static final String SELECT = "Select * from Aro_V_Chk_Stato_Cor_Rich_Soft_Delete /**/";
    public static final String TABLE_NAME = "Aro_V_Chk_Stato_Cor_Rich_Soft_Delete";
    public static final String COL_ID_RICHIESTA_SACER = "id_richiesta_sacer";
    // public static final String COL_ID_RICHIESTA_SACER = "id_richiesta_sacer";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
	map.put(COL_ID_RICHIESTA_SACER,
		new ColumnDescriptor(COL_ID_RICHIESTA_SACER, Types.DECIMAL, 22, true));
	// map.put(COL_FL_FMT_NUMERO_OK, new ColumnDescriptor(COL_FL_FMT_NUMERO_OK, Types.VARCHAR,
	// 1, true));
    }

    public Map<String, ColumnDescriptor> getColumnMap() {
	return map;
    }

    public String getTableName() {
	return TABLE_NAME;
    }

    public String getStatement() {
	return SELECT;
    }

}
