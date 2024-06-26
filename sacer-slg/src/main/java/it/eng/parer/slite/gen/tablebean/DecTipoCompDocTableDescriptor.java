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
 *         Bean per la tabella Dec_Tipo_Comp_Doc
 *
 */
public class DecTipoCompDocTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 18 August 2015 09:48" )
     */

    public static final String SELECT = "Select * from Dec_Tipo_Comp_Doc /**/";
    public static final String TABLE_NAME = "Dec_Tipo_Comp_Doc";
    public static final String COL_ID_TIPO_COMP_DOC = "id_tipo_comp_doc";
    public static final String COL_ID_TIPO_STRUT_DOC = "id_tipo_strut_doc";
    public static final String COL_NM_TIPO_COMP_DOC = "nm_tipo_comp_doc";
    public static final String COL_DS_TIPO_COMP_DOC = "ds_tipo_comp_doc";
    public static final String COL_DT_ISTITUZ = "dt_istituz";
    public static final String COL_DT_SOPPRES = "dt_soppres";
    public static final String COL_TI_USO_COMP_DOC = "ti_uso_comp_doc";
    public static final String COL_FL_GESTITI = "fl_gestiti";
    public static final String COL_FL_IDONEI = "fl_idonei";
    public static final String COL_FL_DEPRECATI = "fl_deprecati";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_TIPO_COMP_DOC, new ColumnDescriptor(COL_ID_TIPO_COMP_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_TIPO_STRUT_DOC, new ColumnDescriptor(COL_ID_TIPO_STRUT_DOC, Types.DECIMAL, 22, false));
        map.put(COL_NM_TIPO_COMP_DOC, new ColumnDescriptor(COL_NM_TIPO_COMP_DOC, Types.VARCHAR, 100, false));
        map.put(COL_DS_TIPO_COMP_DOC, new ColumnDescriptor(COL_DS_TIPO_COMP_DOC, Types.VARCHAR, 254, false));
        map.put(COL_DT_ISTITUZ, new ColumnDescriptor(COL_DT_ISTITUZ, Types.TIMESTAMP, 7, false));
        map.put(COL_DT_SOPPRES, new ColumnDescriptor(COL_DT_SOPPRES, Types.TIMESTAMP, 7, false));
        map.put(COL_TI_USO_COMP_DOC, new ColumnDescriptor(COL_TI_USO_COMP_DOC, Types.VARCHAR, 20, false));
        map.put(COL_FL_GESTITI, new ColumnDescriptor(COL_FL_GESTITI, Types.VARCHAR, 1, false));
        map.put(COL_FL_IDONEI, new ColumnDescriptor(COL_FL_IDONEI, Types.VARCHAR, 1, false));
        map.put(COL_FL_DEPRECATI, new ColumnDescriptor(COL_FL_DEPRECATI, Types.VARCHAR, 1, false));
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
