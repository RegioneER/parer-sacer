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
 *         Bean per la tabella Dec_Formato_File_Standard
 *
 */
public class DecFormatoFileStandardTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */

    public static final String SELECT = "Select * from Dec_Formato_File_Standard /**/";
    public static final String TABLE_NAME = "Dec_Formato_File_Standard";
    public static final String COL_ID_FORMATO_FILE_STANDARD = "id_formato_file_standard";
    public static final String COL_NM_FORMATO_FILE_STANDARD = "nm_formato_file_standard";
    public static final String COL_DS_FORMATO_FILE_STANDARD = "ds_formato_file_standard";
    public static final String COL_CD_VERSIONE = "cd_versione";
    public static final String COL_DS_COPYRIGHT = "ds_copyright";
    public static final String COL_NM_MIMETYPE_FILE = "nm_mimetype_file";
    public static final String COL_TI_ESITO_CONTR_FORMATO = "ti_esito_contr_formato";
    public static final String COL_FL_FORMATO_CONCAT = "fl_formato_concat";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_FORMATO_FILE_STANDARD,
                new ColumnDescriptor(COL_ID_FORMATO_FILE_STANDARD, Types.DECIMAL, 22, true));
        map.put(COL_NM_FORMATO_FILE_STANDARD,
                new ColumnDescriptor(COL_NM_FORMATO_FILE_STANDARD, Types.VARCHAR, 100, false));
        map.put(COL_DS_FORMATO_FILE_STANDARD,
                new ColumnDescriptor(COL_DS_FORMATO_FILE_STANDARD, Types.VARCHAR, 254, false));
        map.put(COL_CD_VERSIONE, new ColumnDescriptor(COL_CD_VERSIONE, Types.VARCHAR, 100, false));
        map.put(COL_DS_COPYRIGHT, new ColumnDescriptor(COL_DS_COPYRIGHT, Types.VARCHAR, 254, false));
        map.put(COL_NM_MIMETYPE_FILE, new ColumnDescriptor(COL_NM_MIMETYPE_FILE, Types.VARCHAR, 100, false));
        map.put(COL_TI_ESITO_CONTR_FORMATO, new ColumnDescriptor(COL_TI_ESITO_CONTR_FORMATO, Types.VARCHAR, 20, false));
        map.put(COL_FL_FORMATO_CONCAT, new ColumnDescriptor(COL_FL_FORMATO_CONCAT, Types.VARCHAR, 1, false));
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
