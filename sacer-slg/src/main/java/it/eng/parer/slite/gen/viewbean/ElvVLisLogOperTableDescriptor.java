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
 *         Bean per la tabella Elv_V_Lis_Log_Oper
 *
 */
public class ElvVLisLogOperTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 20 April 2015 09:51" )
     */

    public static final String SELECT = "Select * from Elv_V_Lis_Log_Oper /**/";
    public static final String TABLE_NAME = "Elv_V_Lis_Log_Oper";
    public static final String COL_ID_AMBIENTE = "id_ambiente";
    public static final String COL_ID_ENTE = "id_ente";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_NM_AMBIENTE = "nm_ambiente";
    public static final String COL_NM_ENTE = "nm_ente";
    public static final String COL_NM_STRUT = "nm_strut";
    public static final String COL_ID_LOG_ELENCO_VERS = "id_log_elenco_vers";
    public static final String COL_ID_ELENCO_VERS = "id_elenco_vers";
    public static final String COL_TI_OPER = "ti_oper";
    public static final String COL_TM_OPER = "tm_oper";
    public static final String COL_NM_ELENCO = "nm_elenco";
    public static final String COL_TI_MOD_OPER = "ti_mod_oper";
    public static final String COL_DT_SCAD_CHIUS = "dt_scad_chius";
    public static final String COL_NI_MAX_COMP = "ni_max_comp";
    public static final String COL_CD_DOC = "cd_doc";
    public static final String COL_DL_MOTIVO_CHIUS = "dl_motivo_chius";
    public static final String COL_NM_COGNOME_FIRMATARIO = "nm_cognome_firmatario";
    public static final String COL_NM_NOME_FIRMATARIO = "nm_nome_firmatario";
    public static final String COL_ID_LOG_JOB = "id_log_job";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_AMBIENTE, new ColumnDescriptor(COL_ID_AMBIENTE, Types.DECIMAL, 22, true));
        map.put(COL_ID_ENTE, new ColumnDescriptor(COL_ID_ENTE, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE, new ColumnDescriptor(COL_NM_AMBIENTE, Types.VARCHAR, 100, true));
        map.put(COL_NM_ENTE, new ColumnDescriptor(COL_NM_ENTE, Types.VARCHAR, 100, true));
        map.put(COL_NM_STRUT, new ColumnDescriptor(COL_NM_STRUT, Types.VARCHAR, 100, true));
        map.put(COL_ID_LOG_ELENCO_VERS, new ColumnDescriptor(COL_ID_LOG_ELENCO_VERS, Types.DECIMAL, 22, true));
        map.put(COL_ID_ELENCO_VERS, new ColumnDescriptor(COL_ID_ELENCO_VERS, Types.DECIMAL, 22, true));
        map.put(COL_TI_OPER, new ColumnDescriptor(COL_TI_OPER, Types.VARCHAR, 30, true));
        map.put(COL_TM_OPER, new ColumnDescriptor(COL_TM_OPER, Types.TIMESTAMP, 11, true));
        map.put(COL_NM_ELENCO, new ColumnDescriptor(COL_NM_ELENCO, Types.VARCHAR, 100, true));
        map.put(COL_TI_MOD_OPER, new ColumnDescriptor(COL_TI_MOD_OPER, Types.VARCHAR, 10, true));
        map.put(COL_DT_SCAD_CHIUS, new ColumnDescriptor(COL_DT_SCAD_CHIUS, Types.TIMESTAMP, 7, true));
        map.put(COL_NI_MAX_COMP, new ColumnDescriptor(COL_NI_MAX_COMP, Types.DECIMAL, 22, true));
        map.put(COL_CD_DOC, new ColumnDescriptor(COL_CD_DOC, Types.VARCHAR, 304, true));
        map.put(COL_DL_MOTIVO_CHIUS, new ColumnDescriptor(COL_DL_MOTIVO_CHIUS, Types.VARCHAR, 1024, true));
        map.put(COL_NM_COGNOME_FIRMATARIO, new ColumnDescriptor(COL_NM_COGNOME_FIRMATARIO, Types.VARCHAR, 100, true));
        map.put(COL_NM_NOME_FIRMATARIO, new ColumnDescriptor(COL_NM_NOME_FIRMATARIO, Types.VARCHAR, 100, true));
        map.put(COL_ID_LOG_JOB, new ColumnDescriptor(COL_ID_LOG_JOB, Types.DECIMAL, 22, true));
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
