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
 *         Bean per la tabella Ser_V_Lis_Err_File_Serie_Ud
 *
 */
public class SerVLisErrFileSerieUdTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 12 May 2015 16:05" )
     */

    public static final String SELECT = "Select * from Ser_V_Lis_Err_File_Serie_Ud /**/";
    public static final String TABLE_NAME = "Ser_V_Lis_Err_File_Serie_Ud";
    public static final String COL_ID_VER_SERIE = "id_ver_serie";
    public static final String COL_TI_SCOPO_FILE_INPUT_VER_SERIE = "ti_scopo_file_input_ver_serie";
    public static final String COL_ID_FILE_INPUT_VER_SERIE = "id_file_input_ver_serie";
    public static final String COL_ID_ERR_FILE_INPUT = "id_err_file_input";
    public static final String COL_NI_REC_ERR = "ni_rec_err";
    public static final String COL_TI_ERR_REC = "ti_err_rec";
    public static final String COL_DS_REC_ERR = "ds_rec_err";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_VER_SERIE, new ColumnDescriptor(COL_ID_VER_SERIE, Types.DECIMAL, 22, true));
        map.put(COL_TI_SCOPO_FILE_INPUT_VER_SERIE,
                new ColumnDescriptor(COL_TI_SCOPO_FILE_INPUT_VER_SERIE, Types.VARCHAR, 20, true));
        map.put(COL_ID_FILE_INPUT_VER_SERIE,
                new ColumnDescriptor(COL_ID_FILE_INPUT_VER_SERIE, Types.DECIMAL, 22, true));
        map.put(COL_ID_ERR_FILE_INPUT, new ColumnDescriptor(COL_ID_ERR_FILE_INPUT, Types.DECIMAL, 22, true));
        map.put(COL_NI_REC_ERR, new ColumnDescriptor(COL_NI_REC_ERR, Types.DECIMAL, 22, true));
        map.put(COL_TI_ERR_REC, new ColumnDescriptor(COL_TI_ERR_REC, Types.VARCHAR, 30, true));
        map.put(COL_DS_REC_ERR, new ColumnDescriptor(COL_DS_REC_ERR, Types.VARCHAR, 4000, true));
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
