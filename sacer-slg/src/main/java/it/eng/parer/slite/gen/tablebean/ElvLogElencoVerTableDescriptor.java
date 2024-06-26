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

package it.eng.parer.slite.gen.tablebean;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella Elv_Log_Elenco_Vers
 *
 */
public class ElvLogElencoVerTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 31 March 2015 14:33" )
     */

    public static final String SELECT = "Select * from Elv_Log_Elenco_Vers /**/";
    public static final String TABLE_NAME = "Elv_Log_Elenco_Vers";
    public static final String COL_ID_LOG_ELENCO_VERS = "id_log_elenco_vers";
    public static final String COL_ID_USER = "id_user";
    public static final String COL_ID_ELENCO_VERS = "id_elenco_vers";
    public static final String COL_TM_OPER = "tm_oper";
    public static final String COL_TI_OPER = "ti_oper";
    public static final String COL_NM_ELENCO = "nm_elenco";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_ID_LOG_JOB = "id_log_job";
    public static final String COL_CD_REGISTRO_KEY_UNITA_DOC = "cd_registro_key_unita_doc";
    public static final String COL_AA_KEY_UNITA_DOC = "aa_key_unita_doc";
    public static final String COL_CD_KEY_UNITA_DOC = "cd_key_unita_doc";
    public static final String COL_TI_DOC = "ti_doc";
    public static final String COL_PG_DOC = "pg_doc";
    public static final String COL_PG_UPD_UNITA_DOC = "pg_upd_unita_doc";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_LOG_ELENCO_VERS, new ColumnDescriptor(COL_ID_LOG_ELENCO_VERS, Types.DECIMAL, 22, true));
        map.put(COL_ID_USER, new ColumnDescriptor(COL_ID_USER, Types.DECIMAL, 22, false));
        map.put(COL_ID_ELENCO_VERS, new ColumnDescriptor(COL_ID_ELENCO_VERS, Types.DECIMAL, 22, false));
        map.put(COL_TM_OPER, new ColumnDescriptor(COL_TM_OPER, Types.TIMESTAMP, 11, false));
        map.put(COL_TI_OPER, new ColumnDescriptor(COL_TI_OPER, Types.VARCHAR, 30, false));
        map.put(COL_NM_ELENCO, new ColumnDescriptor(COL_NM_ELENCO, Types.VARCHAR, 100, false));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
        map.put(COL_ID_LOG_JOB, new ColumnDescriptor(COL_ID_LOG_JOB, Types.DECIMAL, 22, false));
        map.put(COL_CD_REGISTRO_KEY_UNITA_DOC,
                new ColumnDescriptor(COL_CD_REGISTRO_KEY_UNITA_DOC, Types.VARCHAR, 100, false));
        map.put(COL_AA_KEY_UNITA_DOC, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_CD_KEY_UNITA_DOC, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC, Types.VARCHAR, 100, false));
        map.put(COL_TI_DOC, new ColumnDescriptor(COL_TI_DOC, Types.VARCHAR, 20, false));
        map.put(COL_PG_DOC, new ColumnDescriptor(COL_PG_DOC, Types.DECIMAL, 22, false));
        map.put(COL_PG_UPD_UNITA_DOC, new ColumnDescriptor(COL_PG_UPD_UNITA_DOC, Types.DECIMAL, 22, false));
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
