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
 *         Bean per la tabella Dec_V_Chk_Criteri_By_Tipo_Ud
 *
 */
public class DecVChkCriteriByTipoUdTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 17 October 2017 14:27" )
     */

    public static final String SELECT = "Select * from Dec_V_Chk_Criteri_By_Tipo_Ud /**/";
    public static final String TABLE_NAME = "Dec_V_Chk_Criteri_By_Tipo_Ud";
    public static final String COL_ID_TIPO_UNITA_DOC = "id_tipo_unita_doc";
    public static final String COL_NM_TIPO_UNITA_DOC = "nm_tipo_unita_doc";
    public static final String COL_ID_CRITERIO_RAGGR = "id_criterio_raggr";
    public static final String COL_NM_CRITERIO_RAGGR = "nm_criterio_raggr";
    public static final String COL_FL_CRITERIO_RAGGR_STANDARD = "fl_criterio_raggr_standard";
    public static final String COL_FL_CRITERIO_RAGGR_FISC = "fl_criterio_raggr_fisc";
    public static final String COL_FL_CRITERIO_COERENTE = "fl_criterio_coerente";
    public static final String COL_DS_CRITERIO_NON_COERENTE = "ds_criterio_non_coerente";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_TIPO_UNITA_DOC, new ColumnDescriptor(COL_ID_TIPO_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_UNITA_DOC, new ColumnDescriptor(COL_NM_TIPO_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_ID_CRITERIO_RAGGR, new ColumnDescriptor(COL_ID_CRITERIO_RAGGR, Types.DECIMAL, 22, true));
        map.put(COL_NM_CRITERIO_RAGGR, new ColumnDescriptor(COL_NM_CRITERIO_RAGGR, Types.VARCHAR, 100, true));
        map.put(COL_FL_CRITERIO_RAGGR_STANDARD,
                new ColumnDescriptor(COL_FL_CRITERIO_RAGGR_STANDARD, Types.VARCHAR, 1, true));
        map.put(COL_FL_CRITERIO_RAGGR_FISC, new ColumnDescriptor(COL_FL_CRITERIO_RAGGR_FISC, Types.VARCHAR, 1, true));
        map.put(COL_FL_CRITERIO_COERENTE, new ColumnDescriptor(COL_FL_CRITERIO_COERENTE, Types.VARCHAR, 1, true));
        map.put(COL_DS_CRITERIO_NON_COERENTE,
                new ColumnDescriptor(COL_DS_CRITERIO_NON_COERENTE, Types.VARCHAR, 129, true));
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
