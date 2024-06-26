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
 *         Bean per la tabella Aro_Comp_Doc
 *
 */
public class AroCompDocTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */

    public static final String SELECT = "Select * from Aro_Comp_Doc /**/";
    public static final String TABLE_NAME = "Aro_Comp_Doc";
    public static final String COL_ID_COMP_DOC = "id_comp_doc";
    public static final String COL_ID_STRUT_DOC = "id_strut_doc";
    public static final String COL_NI_ORD_COMP_DOC = "ni_ord_comp_doc";
    public static final String COL_TI_SUPPORTO_COMP = "ti_supporto_comp";
    public static final String COL_ID_TIPO_COMP_DOC = "id_tipo_comp_doc";
    public static final String COL_DS_NOME_COMP_VERS = "ds_nome_comp_vers";
    public static final String COL_ID_FORMATO_FILE_VERS = "id_formato_file_vers";
    public static final String COL_ID_TIPO_RAPPR_COMP = "id_tipo_rappr_comp";
    public static final String COL_DS_HASH_FILE_VERS = "ds_hash_file_vers";
    public static final String COL_DL_URN_COMP_VERS = "dl_urn_comp_vers";
    public static final String COL_DS_ID_COMP_VERS = "ds_id_comp_vers";
    public static final String COL_TM_RIF_TEMP_VERS = "tm_rif_temp_vers";
    public static final String COL_DS_RIF_TEMP_VERS = "ds_rif_temp_vers";
    public static final String COL_FL_RIF_TEMP_DATA_FIRMA_VERS = "fl_rif_temp_data_firma_vers";
    public static final String COL_DS_URN_COMP_CALC = "ds_urn_comp_calc";
    public static final String COL_ID_FORMATO_FILE_CALC = "id_formato_file_calc";
    public static final String COL_DS_HASH_FILE_CALC = "ds_hash_file_calc";
    public static final String COL_DS_ALGO_HASH_FILE_CALC = "ds_algo_hash_file_calc";
    public static final String COL_CD_ENCODING_HASH_FILE_CALC = "cd_encoding_hash_file_calc";
    public static final String COL_TI_ESITO_CONTR_FORMATO_FILE = "ti_esito_contr_formato_file";
    public static final String COL_FL_COMP_FIRMATO = "fl_comp_firmato";
    public static final String COL_TI_ESITO_VERIF_FIRME = "ti_esito_verif_firme";
    public static final String COL_DS_MSG_ESITO_VERIF_FIRME = "ds_msg_esito_verif_firme";
    public static final String COL_NI_SIZE_FILE_CALC = "ni_size_file_calc";
    public static final String COL_ID_COMP_DOC_PADRE = "id_comp_doc_padre";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_DS_MSG_ESITO_CONTR_FORMATO = "ds_msg_esito_contr_formato";
    public static final String COL_DS_FORMATO_RAPPR_CALC = "ds_formato_rappr_calc";
    public static final String COL_ID_UNITA_DOC_RIF = "id_unita_doc_rif";
    public static final String COL_DS_FORMATO_RAPPR_ESTESO_CALC = "ds_formato_rappr_esteso_calc";
    public static final String COL_FL_NO_CALC_FMT_VERIF_FIRME = "fl_no_calc_fmt_verif_firme";
    public static final String COL_FL_NO_CALC_HASH_FILE = "fl_no_calc_hash_file";
    public static final String COL_DS_NOME_FILE_ARK = "ds_nome_file_ark";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_COMP_DOC, new ColumnDescriptor(COL_ID_COMP_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT_DOC, new ColumnDescriptor(COL_ID_STRUT_DOC, Types.DECIMAL, 22, false));
        map.put(COL_NI_ORD_COMP_DOC, new ColumnDescriptor(COL_NI_ORD_COMP_DOC, Types.DECIMAL, 22, false));
        map.put(COL_TI_SUPPORTO_COMP, new ColumnDescriptor(COL_TI_SUPPORTO_COMP, Types.VARCHAR, 20, false));
        map.put(COL_ID_TIPO_COMP_DOC, new ColumnDescriptor(COL_ID_TIPO_COMP_DOC, Types.DECIMAL, 22, false));
        map.put(COL_DS_NOME_COMP_VERS, new ColumnDescriptor(COL_DS_NOME_COMP_VERS, Types.VARCHAR, 254, false));
        map.put(COL_ID_FORMATO_FILE_VERS, new ColumnDescriptor(COL_ID_FORMATO_FILE_VERS, Types.DECIMAL, 22, false));
        map.put(COL_ID_TIPO_RAPPR_COMP, new ColumnDescriptor(COL_ID_TIPO_RAPPR_COMP, Types.DECIMAL, 22, false));
        map.put(COL_DS_HASH_FILE_VERS, new ColumnDescriptor(COL_DS_HASH_FILE_VERS, Types.VARCHAR, 254, false));
        map.put(COL_DL_URN_COMP_VERS, new ColumnDescriptor(COL_DL_URN_COMP_VERS, Types.VARCHAR, 1024, false));
        map.put(COL_DS_ID_COMP_VERS, new ColumnDescriptor(COL_DS_ID_COMP_VERS, Types.VARCHAR, 254, false));
        map.put(COL_TM_RIF_TEMP_VERS, new ColumnDescriptor(COL_TM_RIF_TEMP_VERS, Types.TIMESTAMP, 7, false));
        map.put(COL_DS_RIF_TEMP_VERS, new ColumnDescriptor(COL_DS_RIF_TEMP_VERS, Types.VARCHAR, 254, false));
        map.put(COL_FL_RIF_TEMP_DATA_FIRMA_VERS,
                new ColumnDescriptor(COL_FL_RIF_TEMP_DATA_FIRMA_VERS, Types.VARCHAR, 1, false));
        map.put(COL_DS_URN_COMP_CALC, new ColumnDescriptor(COL_DS_URN_COMP_CALC, Types.VARCHAR, 254, false));
        map.put(COL_ID_FORMATO_FILE_CALC, new ColumnDescriptor(COL_ID_FORMATO_FILE_CALC, Types.DECIMAL, 22, false));
        map.put(COL_DS_HASH_FILE_CALC, new ColumnDescriptor(COL_DS_HASH_FILE_CALC, Types.VARCHAR, 254, false));
        map.put(COL_DS_ALGO_HASH_FILE_CALC,
                new ColumnDescriptor(COL_DS_ALGO_HASH_FILE_CALC, Types.VARCHAR, 254, false));
        map.put(COL_CD_ENCODING_HASH_FILE_CALC,
                new ColumnDescriptor(COL_CD_ENCODING_HASH_FILE_CALC, Types.VARCHAR, 20, false));
        map.put(COL_TI_ESITO_CONTR_FORMATO_FILE,
                new ColumnDescriptor(COL_TI_ESITO_CONTR_FORMATO_FILE, Types.VARCHAR, 20, false));
        map.put(COL_FL_COMP_FIRMATO, new ColumnDescriptor(COL_FL_COMP_FIRMATO, Types.VARCHAR, 1, false));
        map.put(COL_TI_ESITO_VERIF_FIRME, new ColumnDescriptor(COL_TI_ESITO_VERIF_FIRME, Types.VARCHAR, 20, false));
        map.put(COL_DS_MSG_ESITO_VERIF_FIRME,
                new ColumnDescriptor(COL_DS_MSG_ESITO_VERIF_FIRME, Types.VARCHAR, 254, false));
        map.put(COL_NI_SIZE_FILE_CALC, new ColumnDescriptor(COL_NI_SIZE_FILE_CALC, Types.DECIMAL, 22, false));
        map.put(COL_ID_COMP_DOC_PADRE, new ColumnDescriptor(COL_ID_COMP_DOC_PADRE, Types.DECIMAL, 22, false));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
        map.put(COL_DS_MSG_ESITO_CONTR_FORMATO,
                new ColumnDescriptor(COL_DS_MSG_ESITO_CONTR_FORMATO, Types.VARCHAR, 1024, false));
        map.put(COL_DS_FORMATO_RAPPR_CALC, new ColumnDescriptor(COL_DS_FORMATO_RAPPR_CALC, Types.VARCHAR, 254, false));
        map.put(COL_ID_UNITA_DOC_RIF, new ColumnDescriptor(COL_ID_UNITA_DOC_RIF, Types.DECIMAL, 22, false));
        map.put(COL_DS_FORMATO_RAPPR_ESTESO_CALC,
                new ColumnDescriptor(COL_DS_FORMATO_RAPPR_ESTESO_CALC, Types.VARCHAR, 254, false));
        map.put(COL_FL_NO_CALC_FMT_VERIF_FIRME,
                new ColumnDescriptor(COL_FL_NO_CALC_FMT_VERIF_FIRME, Types.VARCHAR, 1, false));
        map.put(COL_FL_NO_CALC_HASH_FILE, new ColumnDescriptor(COL_FL_NO_CALC_HASH_FILE, Types.VARCHAR, 1, false));
        map.put(COL_DS_NOME_FILE_ARK, new ColumnDescriptor(COL_DS_NOME_FILE_ARK, Types.VARCHAR, 254, false));
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
