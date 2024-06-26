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
 *         Bean per la tabella Mon_V_Lis_Upd_Ud
 *
 */
public class MonVLisUpdUdTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 31 October 2019 10:13" )
     */

    public static final String SELECT = "Select * from Mon_V_Lis_Upd_Ud /**/";
    public static final String TABLE_NAME = "Mon_V_Lis_Upd_Ud";
    public static final String COL_ID_USER_IAM_COR = "id_user_iam_cor";
    public static final String COL_ID_AMBIENTE = "id_ambiente";
    public static final String COL_NM_AMBIENTE = "nm_ambiente";
    public static final String COL_ID_ENTE = "id_ente";
    public static final String COL_NM_ENTE = "nm_ente";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_NM_STRUT = "nm_strut";
    public static final String COL_ID_TIPO_UNITA_DOC = "id_tipo_unita_doc";
    public static final String COL_NM_TIPO_UNITA_DOC = "nm_tipo_unita_doc";
    public static final String COL_ID_REGISTRO_UNITA_DOC = "id_registro_unita_doc";
    public static final String COL_CD_REGISTRO_KEY_UNITA_DOC = "cd_registro_key_unita_doc";
    public static final String COL_ID_TIPO_DOC_PRINC = "id_tipo_doc_princ";
    public static final String COL_NM_TIPO_DOC_PRINC = "nm_tipo_doc_princ";
    public static final String COL_AA_KEY_UNITA_DOC = "aa_key_unita_doc";
    public static final String COL_TI_STATO_UPD_ELENCO_VERS = "ti_stato_upd_elenco_vers";
    public static final String COL_TS_INI_SES = "ts_ini_ses";
    public static final String COL_ID_UPD_UNITA_DOC = "id_upd_unita_doc";
    public static final String COL_ID_UNITA_DOC = "id_unita_doc";
    public static final String COL_PG_UPD_UNITA_DOC = "pg_upd_unita_doc";
    public static final String COL_FL_FORZA_UPD = "fl_forza_upd";
    public static final String COL_NT_UPD = "nt_upd";
    public static final String COL_FL_SES_UPD_KO_RISOLTI = "fl_ses_upd_ko_risolti";
    public static final String COL_DS_TS_INI_SES = "ds_ts_ini_ses";
    public static final String COL_DS_ENTE_STRUT = "ds_ente_strut";
    public static final String COL_DS_UNITA_DOC = "ds_unita_doc";
    public static final String COL_CD_KEY_UNITA_DOC = "cd_key_unita_doc";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_USER_IAM_COR, new ColumnDescriptor(COL_ID_USER_IAM_COR, Types.DECIMAL, 22, true));
        map.put(COL_ID_AMBIENTE, new ColumnDescriptor(COL_ID_AMBIENTE, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE, new ColumnDescriptor(COL_NM_AMBIENTE, Types.VARCHAR, 100, true));
        map.put(COL_ID_ENTE, new ColumnDescriptor(COL_ID_ENTE, Types.DECIMAL, 22, true));
        map.put(COL_NM_ENTE, new ColumnDescriptor(COL_NM_ENTE, Types.VARCHAR, 100, true));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, true));
        map.put(COL_NM_STRUT, new ColumnDescriptor(COL_NM_STRUT, Types.VARCHAR, 100, true));
        map.put(COL_ID_TIPO_UNITA_DOC, new ColumnDescriptor(COL_ID_TIPO_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_UNITA_DOC, new ColumnDescriptor(COL_NM_TIPO_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_ID_REGISTRO_UNITA_DOC, new ColumnDescriptor(COL_ID_REGISTRO_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_CD_REGISTRO_KEY_UNITA_DOC,
                new ColumnDescriptor(COL_CD_REGISTRO_KEY_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_ID_TIPO_DOC_PRINC, new ColumnDescriptor(COL_ID_TIPO_DOC_PRINC, Types.DECIMAL, 22, true));
        map.put(COL_NM_TIPO_DOC_PRINC, new ColumnDescriptor(COL_NM_TIPO_DOC_PRINC, Types.VARCHAR, 100, true));
        map.put(COL_AA_KEY_UNITA_DOC, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_TI_STATO_UPD_ELENCO_VERS,
                new ColumnDescriptor(COL_TI_STATO_UPD_ELENCO_VERS, Types.VARCHAR, 50, true));
        map.put(COL_TS_INI_SES, new ColumnDescriptor(COL_TS_INI_SES, Types.TIMESTAMP, 11, true));
        map.put(COL_ID_UPD_UNITA_DOC, new ColumnDescriptor(COL_ID_UPD_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_UNITA_DOC, new ColumnDescriptor(COL_ID_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_PG_UPD_UNITA_DOC, new ColumnDescriptor(COL_PG_UPD_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_FL_FORZA_UPD, new ColumnDescriptor(COL_FL_FORZA_UPD, Types.VARCHAR, 1, true));
        map.put(COL_NT_UPD, new ColumnDescriptor(COL_NT_UPD, Types.VARCHAR, 4000, true));
        map.put(COL_FL_SES_UPD_KO_RISOLTI, new ColumnDescriptor(COL_FL_SES_UPD_KO_RISOLTI, Types.VARCHAR, 1, true));
        map.put(COL_DS_TS_INI_SES, new ColumnDescriptor(COL_DS_TS_INI_SES, Types.VARCHAR, 29, true));
        map.put(COL_DS_ENTE_STRUT, new ColumnDescriptor(COL_DS_ENTE_STRUT, Types.VARCHAR, 201, true));
        map.put(COL_DS_UNITA_DOC, new ColumnDescriptor(COL_DS_UNITA_DOC, Types.VARCHAR, 242, true));
        map.put(COL_CD_KEY_UNITA_DOC, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC, Types.VARCHAR, 100, true));
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
