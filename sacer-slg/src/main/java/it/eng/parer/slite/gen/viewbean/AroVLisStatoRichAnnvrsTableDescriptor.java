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

package it.eng.parer.slite.gen.viewbean;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella Aro_V_Lis_Stato_Rich_Annvrs
 *
 */
public class AroVLisStatoRichAnnvrsTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 7 March 2016 13:30" )
     */

    public static final String SELECT = "Select * from Aro_V_Lis_Stato_Rich_Annvrs /**/";
    public static final String TABLE_NAME = "Aro_V_Lis_Stato_Rich_Annvrs";
    public static final String COL_ID_RICH_ANNUL_VERS = "id_rich_annul_vers";
    public static final String COL_ID_STATO_RICH_ANNUL_VERS = "id_stato_rich_annul_vers";
    public static final String COL_PG_STATO_RICH_ANNUL_VERS = "pg_stato_rich_annul_vers";
    public static final String COL_TI_STATO_RICH_ANNUL_VERS = "ti_stato_rich_annul_vers";
    public static final String COL_DT_REG_STATO_RICH_ANNUL_VERS = "dt_reg_stato_rich_annul_vers";
    public static final String COL_DS_NOTA_RICH_ANNUL_VERS = "ds_nota_rich_annul_vers";
    public static final String COL_NM_USERID = "nm_userid";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_RICH_ANNUL_VERS, new ColumnDescriptor(COL_ID_RICH_ANNUL_VERS, Types.DECIMAL, 22, true));
        map.put(COL_ID_STATO_RICH_ANNUL_VERS,
                new ColumnDescriptor(COL_ID_STATO_RICH_ANNUL_VERS, Types.DECIMAL, 22, true));
        map.put(COL_PG_STATO_RICH_ANNUL_VERS,
                new ColumnDescriptor(COL_PG_STATO_RICH_ANNUL_VERS, Types.DECIMAL, 22, true));
        map.put(COL_TI_STATO_RICH_ANNUL_VERS,
                new ColumnDescriptor(COL_TI_STATO_RICH_ANNUL_VERS, Types.VARCHAR, 30, true));
        map.put(COL_DT_REG_STATO_RICH_ANNUL_VERS,
                new ColumnDescriptor(COL_DT_REG_STATO_RICH_ANNUL_VERS, Types.TIMESTAMP, 7, true));
        map.put(COL_DS_NOTA_RICH_ANNUL_VERS,
                new ColumnDescriptor(COL_DS_NOTA_RICH_ANNUL_VERS, Types.VARCHAR, 1024, true));
        map.put(COL_NM_USERID, new ColumnDescriptor(COL_NM_USERID, Types.VARCHAR, 100, true));
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
