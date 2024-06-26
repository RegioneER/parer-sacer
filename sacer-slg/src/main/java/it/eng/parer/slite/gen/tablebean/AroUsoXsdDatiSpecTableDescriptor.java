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
 *         Bean per la tabella Aro_Uso_Xsd_Dati_Spec
 *
 */
public class AroUsoXsdDatiSpecTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */

    public static final String SELECT = "Select * from Aro_Uso_Xsd_Dati_Spec /**/";
    public static final String TABLE_NAME = "Aro_Uso_Xsd_Dati_Spec";
    public static final String COL_ID_USO_XSD_DATI_SPEC = "id_uso_xsd_dati_spec";
    public static final String COL_TI_USO_XSD = "ti_uso_xsd";
    public static final String COL_TI_ENTITA_SACER = "ti_entita_sacer";
    public static final String COL_ID_XSD_DATI_SPEC = "id_xsd_dati_spec";
    public static final String COL_ID_UNITA_DOC = "id_unita_doc";
    public static final String COL_ID_DOC = "id_doc";
    public static final String COL_ID_COMP_DOC = "id_comp_doc";
    public static final String COL_ID_STRUT = "id_strut";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_USO_XSD_DATI_SPEC, new ColumnDescriptor(COL_ID_USO_XSD_DATI_SPEC, Types.DECIMAL, 22, true));
        map.put(COL_TI_USO_XSD, new ColumnDescriptor(COL_TI_USO_XSD, Types.VARCHAR, 20, false));
        map.put(COL_TI_ENTITA_SACER, new ColumnDescriptor(COL_TI_ENTITA_SACER, Types.VARCHAR, 20, false));
        map.put(COL_ID_XSD_DATI_SPEC, new ColumnDescriptor(COL_ID_XSD_DATI_SPEC, Types.DECIMAL, 22, false));
        map.put(COL_ID_UNITA_DOC, new ColumnDescriptor(COL_ID_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_ID_DOC, new ColumnDescriptor(COL_ID_DOC, Types.DECIMAL, 22, false));
        map.put(COL_ID_COMP_DOC, new ColumnDescriptor(COL_ID_COMP_DOC, Types.DECIMAL, 22, false));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
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
