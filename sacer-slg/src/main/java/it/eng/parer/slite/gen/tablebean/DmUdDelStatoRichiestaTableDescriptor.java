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

package it.eng.parer.slite.gen.tablebean;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bean per la tabella DM_UD_DEL_STATO_RICHIESTA
 */
public class DmUdDelStatoRichiestaTableDescriptor extends TableDescriptor {

    public static final String SELECT = "Select * from DM_UD_DEL_STATO_RICHIESTA /**/";
    public static final String TABLE_NAME = "DM_UD_DEL_STATO_RICHIESTA";

    public static final String COL_ID_STATO_UD_DEL_RICHIESTA = "ID_STATO_UD_DEL_RICHIESTA";
    public static final String COL_TI_STATO_INTERNO_RICH = "TI_STATO_INTERNO_RICH";
    public static final String COL_DS_STATO_INTERNO_RICH = "DS_STATO_INTERNO_RICH";
    public static final String COL_DT_REG_STATO = "DT_REG_STATO";
    public static final String COL_PG_STATO_RICH = "PG_STATO_RICH";
    public static final String COL_ID_UD_DEL_RICHIESTA = "ID_UD_DEL_RICHIESTA";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_STATO_UD_DEL_RICHIESTA,
                new ColumnDescriptor(COL_ID_STATO_UD_DEL_RICHIESTA, Types.DECIMAL, 22, true));
        map.put(COL_TI_STATO_INTERNO_RICH,
                new ColumnDescriptor(COL_TI_STATO_INTERNO_RICH, Types.VARCHAR, 100, false));
        map.put(COL_DS_STATO_INTERNO_RICH,
                new ColumnDescriptor(COL_DS_STATO_INTERNO_RICH, Types.VARCHAR, 200, true));
        map.put(COL_DT_REG_STATO,
                new ColumnDescriptor(COL_DT_REG_STATO, Types.TIMESTAMP, 7, false));
        map.put(COL_PG_STATO_RICH,
                new ColumnDescriptor(COL_PG_STATO_RICH, Types.DECIMAL, 10, false));
        map.put(COL_ID_UD_DEL_RICHIESTA,
                new ColumnDescriptor(COL_ID_UD_DEL_RICHIESTA, Types.DECIMAL, 22, false));
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
