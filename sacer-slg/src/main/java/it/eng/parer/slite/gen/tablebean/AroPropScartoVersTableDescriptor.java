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

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * Bean descriptor per la tabella ARO_PROP_SCARTO_VERS
 *
 */
public class AroPropScartoVersTableDescriptor extends TableDescriptor {

    private static final long serialVersionUID = 1L;

    public static final String SELECT = "Select * from ARO_PROP_SCARTO_VERS /**/";
    public static final String TABLE_NAME = "ARO_PROP_SCARTO_VERS";

    // Nomi delle colonne
    public static final String COL_ID_PROP_SCARTO_VERS = "id_prop_scarto_vers";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_PG_PROP_SCARTO_VERS = "pg_prop_scarto_vers";
    public static final String COL_DS_PROP_SCARTO_VERS = "ds_prop_scarto_vers";
    public static final String COL_NT_PROP_SCARTO_VERS = "nt_prop_scarto_vers";
    // public static final String COL_TI_STATO_PROP_SCARTO = "ti_stato_prop_scarto";
    public static final String COL_FL_CONFERMATA = "fl_confermata";
    public static final String COL_DT_CREAZIONE = "dt_creazione";
    public static final String COL_DT_ULTIMA_MOD = "dt_ultima_mod";
    public static final String COL_ID_USER_CREAZIONE = "id_user_creazione";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        // ID_PROP_SCARTO_VERS è la Primary Key (ultimo parametro = true)
        map.put(COL_ID_PROP_SCARTO_VERS,
                new ColumnDescriptor(COL_ID_PROP_SCARTO_VERS, Types.DECIMAL, 22, true));

        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
        map.put(COL_PG_PROP_SCARTO_VERS,
                new ColumnDescriptor(COL_PG_PROP_SCARTO_VERS, Types.VARCHAR, 100, false));
        map.put(COL_DS_PROP_SCARTO_VERS,
                new ColumnDescriptor(COL_DS_PROP_SCARTO_VERS, Types.VARCHAR, 254, false));
        map.put(COL_NT_PROP_SCARTO_VERS,
                new ColumnDescriptor(COL_NT_PROP_SCARTO_VERS, Types.VARCHAR, 2000, false));
        // map.put(COL_TI_STATO_PROP_SCARTO,
        // new ColumnDescriptor(COL_TI_STATO_PROP_SCARTO, Types.VARCHAR, 50, false));
        map.put(COL_FL_CONFERMATA, new ColumnDescriptor(COL_FL_CONFERMATA, Types.CHAR, 1, false));
        map.put(COL_DT_CREAZIONE, new ColumnDescriptor(COL_DT_CREAZIONE, Types.DATE, 0, false));
        map.put(COL_DT_ULTIMA_MOD, new ColumnDescriptor(COL_DT_ULTIMA_MOD, Types.DATE, 0, false));
        map.put(COL_ID_USER_CREAZIONE,
                new ColumnDescriptor(COL_ID_USER_CREAZIONE, Types.DECIMAL, 22, false));
    }

    @Override
    public Map<String, ColumnDescriptor> getColumnMap() {
        return map;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public String getStatement() {
        return SELECT;
    }

}