/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.slite.gen.viewbean;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 *
 * Bean per la tabella Org_Doc_Processo_Conserv
 *
 */
public class DecDocProcessoConservTableDescriptor extends TableDescriptor {

    public static final String SELECT = "Select * from Org_Doc_Processo_Conserv /**/";
    public static final String TABLE_NAME = "Org_Doc_Processo_Conserv";
    public static final String COL_ID_DOC_PROCESSO_CONSERV = "id_doc_processo_conserv";
    public static final String COL_ID_ENTE_SIAM = "id_ente_siam";
    public static final String COL_ID_ORGANIZ_IAM = "id_organiz_iam";
    public static final String COL_AA_DOC_PROCESSO_CONSERV = "aa_doc_processo_conserv";
    public static final String COL_BL_DOC_PROCESSO_CONSERV = "bl_doc_processo_conserv";
    public static final String COL_CD_KEY_DOC_PROCESSO_CONSERV = "cd_key_doc_processo_conserv";
    public static final String COL_CD_REGISTRO_DOC_PROCESSO_CONSERV = "cd_registro_doc_processo_conserv";
    public static final String COL_DS_DOC_PROCESSO_CONSERV = "ds_doc_processo_conserv";
    public static final String COL_DT_DOC_PROCESSO_CONSERV = "dt_doc_processo_conserv";
    public static final String COL_NM_FILE_DOC_PROCESSO_CONSERV = "nm_file_doc_processo_conserv";
    public static final String COL_PG_DOC_PROCESSO_CONSERV = "pg_doc_processo_conserv";
    public static final String COL_ID_TIPO_DOC_PROCESSO_CONSERV = "id_tipo_doc_processo_conserv";
    public static final String COL_NM_TIPO_DOC = "nm_tipo_doc";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_ID_DOC_PROCESSO_CONSERV, Types.DECIMAL, 22, true));
        map.put(COL_ID_ENTE_SIAM, new ColumnDescriptor(COL_ID_ENTE_SIAM, Types.DECIMAL, 22, false));
        map.put(COL_ID_ORGANIZ_IAM,
                new ColumnDescriptor(COL_ID_ORGANIZ_IAM, Types.DECIMAL, 22, false));
        map.put(COL_AA_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_AA_DOC_PROCESSO_CONSERV, Types.DECIMAL, 22, false));
        map.put(COL_BL_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_BL_DOC_PROCESSO_CONSERV, Types.BLOB, 4000, false));
        map.put(COL_CD_KEY_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_CD_KEY_DOC_PROCESSO_CONSERV, Types.VARCHAR, 100, false));
        map.put(COL_CD_REGISTRO_DOC_PROCESSO_CONSERV, new ColumnDescriptor(
                COL_CD_REGISTRO_DOC_PROCESSO_CONSERV, Types.VARCHAR, 100, false));
        map.put(COL_DS_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_DS_DOC_PROCESSO_CONSERV, Types.VARCHAR, 4000, false));
        map.put(COL_DT_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_DT_DOC_PROCESSO_CONSERV, Types.TIMESTAMP, 7, false));
        map.put(COL_NM_FILE_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_NM_FILE_DOC_PROCESSO_CONSERV, Types.VARCHAR, 254, false));
        map.put(COL_PG_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_PG_DOC_PROCESSO_CONSERV, Types.DECIMAL, 22, false));
        map.put(COL_ID_TIPO_DOC_PROCESSO_CONSERV,
                new ColumnDescriptor(COL_ID_TIPO_DOC_PROCESSO_CONSERV, Types.DECIMAL, 22, false));
        map.put(COL_NM_TIPO_DOC, new ColumnDescriptor(COL_NM_TIPO_DOC, Types.VARCHAR, 100, false));
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
