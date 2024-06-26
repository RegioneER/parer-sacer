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
 *         Bean per la tabella Mon_V_Lis_Uni_Doc_Da_Annul
 *
 */
public class MonVLisUniDocDaAnnulTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 25 November 2015 10:49" )
     */

    public static final String SELECT = "Select * from Mon_V_Lis_Uni_Doc_Da_Annul /**/";
    public static final String TABLE_NAME = "Mon_V_Lis_Uni_Doc_Da_Annul";
    public static final String COL_ID_USER_IAM = "id_user_iam";
    public static final String COL_ID_AMBIENTE = "id_ambiente";
    public static final String COL_NM_AMBIENTE = "nm_ambiente";
    public static final String COL_ID_ENTE = "id_ente";
    public static final String COL_NM_ENTE = "nm_ente";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_NM_STRUT = "nm_strut";
    public static final String COL_ID_UNITA_DOC = "id_unita_doc";
    public static final String COL_CD_REGISTRO_KEY_UNITA_DOC = "cd_registro_key_unita_doc";
    public static final String COL_AA_KEY_UNITA_DOC = "aa_key_unita_doc";
    public static final String COL_CD_KEY_UNITA_DOC = "cd_key_unita_doc";
    public static final String COL_ID_TIPO_UNITA_DOC = "id_tipo_unita_doc";
    public static final String COL_NM_TIPO_UNITA_DOC = "nm_tipo_unita_doc";
    public static final String COL_DS_ORD_DOC = "ds_ord_doc";
    public static final String COL_ID_DOC = "id_doc";
    public static final String COL_TI_DOC = "ti_doc";
    public static final String COL_PG_DOC = "pg_doc";
    public static final String COL_ID_TIPO_DOC = "id_tipo_doc";
    public static final String COL_NM_TIPO_DOC = "nm_tipo_doc";
    public static final String COL_DT_CREAZIONE = "dt_creazione";
    public static final String COL_TI_STATO_ANNUL = "ti_stato_annul";
    public static final String COL_DT_ANNUL = "dt_annul";
    public static final String COL_FL_VERS_NUOVO = "fl_vers_nuovo";
    public static final String COL_ID_SUB_STRUT = "id_sub_strut";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_USER_IAM, new ColumnDescriptor(COL_ID_USER_IAM, Types.DECIMAL, 22, true));
        map.put(COL_ID_AMBIENTE, new ColumnDescriptor(COL_ID_AMBIENTE, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE, new ColumnDescriptor(COL_NM_AMBIENTE, Types.VARCHAR, 100, true));
        map.put(COL_ID_ENTE, new ColumnDescriptor(COL_ID_ENTE, Types.DECIMAL, 22, true));
        map.put(COL_NM_ENTE, new ColumnDescriptor(COL_NM_ENTE, Types.VARCHAR, 100, true));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, true));
        map.put(COL_NM_STRUT, new ColumnDescriptor(COL_NM_STRUT, Types.VARCHAR, 100, true));
        map.put(COL_ID_UNITA_DOC, new ColumnDescriptor(COL_ID_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_CD_REGISTRO_KEY_UNITA_DOC,
                new ColumnDescriptor(COL_CD_REGISTRO_KEY_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_AA_KEY_UNITA_DOC, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_CD_KEY_UNITA_DOC, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_ID_TIPO_UNITA_DOC, new ColumnDescriptor(COL_ID_TIPO_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_UNITA_DOC, new ColumnDescriptor(COL_NM_TIPO_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_DS_ORD_DOC, new ColumnDescriptor(COL_DS_ORD_DOC, Types.VARCHAR, 100, true));
        map.put(COL_ID_DOC, new ColumnDescriptor(COL_ID_DOC, Types.DECIMAL, 22, true));
        map.put(COL_TI_DOC, new ColumnDescriptor(COL_TI_DOC, Types.VARCHAR, 20, true));
        map.put(COL_PG_DOC, new ColumnDescriptor(COL_PG_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_TIPO_DOC, new ColumnDescriptor(COL_ID_TIPO_DOC, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_DOC, new ColumnDescriptor(COL_NM_TIPO_DOC, Types.VARCHAR, 100, true));
        map.put(COL_DT_CREAZIONE, new ColumnDescriptor(COL_DT_CREAZIONE, Types.TIMESTAMP, 7, true));
        map.put(COL_TI_STATO_ANNUL, new ColumnDescriptor(COL_TI_STATO_ANNUL, Types.VARCHAR, 20, true));
        map.put(COL_DT_ANNUL, new ColumnDescriptor(COL_DT_ANNUL, Types.TIMESTAMP, 7, true));
        map.put(COL_FL_VERS_NUOVO, new ColumnDescriptor(COL_FL_VERS_NUOVO, Types.VARCHAR, 1, true));
        map.put(COL_ID_SUB_STRUT, new ColumnDescriptor(COL_ID_SUB_STRUT, Types.DECIMAL, 22, true));
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
