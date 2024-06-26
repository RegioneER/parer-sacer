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
 *         Bean per la tabella Dec_Uso_Modello_Xsd_Uni_Doc
 *
 */
public class DecUsoModelloXsdUniDocTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 27 November 2017 11:33" )
     */

    public static final String SELECT = "Select * from Dec_Uso_Modello_Xsd_Uni_Doc /**/";
    public static final String TABLE_NAME = "Dec_Uso_Modello_Xsd_Uni_Doc";
    public static final String COL_ID_USO_MODELLO_XSD_UNI_DOC = "id_uso_modello_xsd_uni_doc";
    public static final String COL_ID_TIPO_UNITA_DOC = "id_tipo_unita_doc";
    public static final String COL_ID_MODELLO_XSD_UNI_DOC = "id_modello_xsd_ud";
    public static final String COL_FL_STANDARD = "fl_standard";
    public static final String COL_DT_ISTITUZ = "dt_istituz";
    public static final String COL_DT_SOPPRES = "dt_soppres";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_USO_MODELLO_XSD_UNI_DOC,
                new ColumnDescriptor(COL_ID_USO_MODELLO_XSD_UNI_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_TIPO_UNITA_DOC, new ColumnDescriptor(COL_ID_TIPO_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_ID_MODELLO_XSD_UNI_DOC, new ColumnDescriptor(COL_ID_MODELLO_XSD_UNI_DOC, Types.DECIMAL, 22, false));
        map.put(COL_FL_STANDARD, new ColumnDescriptor(COL_FL_STANDARD, Types.VARCHAR, 1, false));
        map.put(COL_DT_ISTITUZ, new ColumnDescriptor(COL_DT_ISTITUZ, Types.TIMESTAMP, 7, false));
        map.put(COL_DT_SOPPRES, new ColumnDescriptor(COL_DT_SOPPRES, Types.TIMESTAMP, 7, false));
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
