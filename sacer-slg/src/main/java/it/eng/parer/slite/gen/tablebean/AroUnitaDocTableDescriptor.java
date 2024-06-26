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
 *         Bean per la tabella Aro_Unita_Doc
 *
 */
public class AroUnitaDocTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */

    public static final String SELECT = "Select * from Aro_Unita_Doc /**/";
    public static final String TABLE_NAME = "Aro_Unita_Doc";
    public static final String COL_ID_UNITA_DOC = "id_unita_doc";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_CD_REGISTRO_KEY_UNITA_DOC = "cd_registro_key_unita_doc";
    public static final String COL_AA_KEY_UNITA_DOC = "aa_key_unita_doc";
    public static final String COL_CD_KEY_UNITA_DOC = "cd_key_unita_doc";
    public static final String COL_DT_ANNUL = "dt_annul";
    public static final String COL_TI_ANNUL = "ti_annul";
    public static final String COL_ID_TIPO_UNITA_DOC = "id_tipo_unita_doc";
    public static final String COL_FL_FORZA_ACCETTAZIONE = "fl_forza_accettazione";
    public static final String COL_FL_FORZA_CONSERVAZIONE = "fl_forza_conservazione";
    public static final String COL_FL_FORZA_COLLEGAMENTO = "fl_forza_collegamento";
    public static final String COL_DT_CREAZIONE = "dt_creazione";
    public static final String COL_FL_CARTACEO = "fl_cartaceo";
    public static final String COL_NI_ALLEG = "ni_alleg";
    public static final String COL_NI_ANNESSI = "ni_annessi";
    public static final String COL_NI_ANNOT = "ni_annot";
    public static final String COL_FL_UNITA_DOC_FIRMATO = "fl_unita_doc_firmato";
    public static final String COL_TI_ESITO_VERIF_FIRME = "ti_esito_verif_firme";
    public static final String COL_DS_MSG_ESITO_VERIF_FIRME = "ds_msg_esito_verif_firme";
    public static final String COL_DS_CLASSIF_PRINC = "ds_classif_princ";
    public static final String COL_CD_FASCIC_PRINC = "cd_fascic_princ";
    public static final String COL_DS_OGGETTO_FASCIC_PRINC = "ds_oggetto_fascic_princ";
    public static final String COL_CD_SOTTOFASCIC_PRINC = "cd_sottofascic_princ";
    public static final String COL_DS_OGGETTO_SOTTOFASCIC_PRINC = "ds_oggetto_sottofascic_princ";
    public static final String COL_DL_OGGETTO_UNITA_DOC = "dl_oggetto_unita_doc";
    public static final String COL_DT_REG_UNITA_DOC = "dt_reg_unita_doc";
    public static final String COL_DS_UFF_COMP_UNITA_DOC = "ds_uff_comp_unita_doc";
    public static final String COL_TI_CONSERVAZIONE = "ti_conservazione";
    public static final String COL_ID_USER_VERS = "id_user_vers";
    public static final String COL_DS_KEY_ORD = "ds_key_ord";
    public static final String COL_ID_REGISTRO_UNITA_DOC = "id_registro_unita_doc";
    public static final String COL_NM_SISTEMA_MIGRAZ = "nm_sistema_migraz";
    public static final String COL_NT_UNITA_DOC = "nt_unita_doc";
    public static final String COL_NT_ANNUL = "nt_annul";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_UNITA_DOC, new ColumnDescriptor(COL_ID_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
        map.put(COL_CD_REGISTRO_KEY_UNITA_DOC,
                new ColumnDescriptor(COL_CD_REGISTRO_KEY_UNITA_DOC, Types.VARCHAR, 100, false));
        map.put(COL_AA_KEY_UNITA_DOC, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_CD_KEY_UNITA_DOC, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC, Types.VARCHAR, 100, false));
        map.put(COL_DT_ANNUL, new ColumnDescriptor(COL_DT_ANNUL, Types.TIMESTAMP, 7, false));
        map.put(COL_TI_ANNUL, new ColumnDescriptor(COL_TI_ANNUL, Types.VARCHAR, 20, false));
        map.put(COL_ID_TIPO_UNITA_DOC, new ColumnDescriptor(COL_ID_TIPO_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_FL_FORZA_ACCETTAZIONE, new ColumnDescriptor(COL_FL_FORZA_ACCETTAZIONE, Types.VARCHAR, 1, false));
        map.put(COL_FL_FORZA_CONSERVAZIONE, new ColumnDescriptor(COL_FL_FORZA_CONSERVAZIONE, Types.VARCHAR, 1, false));
        map.put(COL_FL_FORZA_COLLEGAMENTO, new ColumnDescriptor(COL_FL_FORZA_COLLEGAMENTO, Types.VARCHAR, 1, false));
        map.put(COL_DT_CREAZIONE, new ColumnDescriptor(COL_DT_CREAZIONE, Types.TIMESTAMP, 7, false));
        map.put(COL_FL_CARTACEO, new ColumnDescriptor(COL_FL_CARTACEO, Types.VARCHAR, 1, false));
        map.put(COL_NI_ALLEG, new ColumnDescriptor(COL_NI_ALLEG, Types.DECIMAL, 22, false));
        map.put(COL_NI_ANNESSI, new ColumnDescriptor(COL_NI_ANNESSI, Types.DECIMAL, 22, false));
        map.put(COL_NI_ANNOT, new ColumnDescriptor(COL_NI_ANNOT, Types.DECIMAL, 22, false));
        map.put(COL_FL_UNITA_DOC_FIRMATO, new ColumnDescriptor(COL_FL_UNITA_DOC_FIRMATO, Types.VARCHAR, 1, false));
        map.put(COL_TI_ESITO_VERIF_FIRME, new ColumnDescriptor(COL_TI_ESITO_VERIF_FIRME, Types.VARCHAR, 20, false));
        map.put(COL_DS_MSG_ESITO_VERIF_FIRME,
                new ColumnDescriptor(COL_DS_MSG_ESITO_VERIF_FIRME, Types.VARCHAR, 254, false));
        map.put(COL_DS_CLASSIF_PRINC, new ColumnDescriptor(COL_DS_CLASSIF_PRINC, Types.VARCHAR, 254, false));
        map.put(COL_CD_FASCIC_PRINC, new ColumnDescriptor(COL_CD_FASCIC_PRINC, Types.VARCHAR, 100, false));
        map.put(COL_DS_OGGETTO_FASCIC_PRINC,
                new ColumnDescriptor(COL_DS_OGGETTO_FASCIC_PRINC, Types.VARCHAR, 1024, false));
        map.put(COL_CD_SOTTOFASCIC_PRINC, new ColumnDescriptor(COL_CD_SOTTOFASCIC_PRINC, Types.VARCHAR, 100, false));
        map.put(COL_DS_OGGETTO_SOTTOFASCIC_PRINC,
                new ColumnDescriptor(COL_DS_OGGETTO_SOTTOFASCIC_PRINC, Types.VARCHAR, 1024, false));
        map.put(COL_DL_OGGETTO_UNITA_DOC, new ColumnDescriptor(COL_DL_OGGETTO_UNITA_DOC, Types.VARCHAR, 4000, false));
        map.put(COL_DT_REG_UNITA_DOC, new ColumnDescriptor(COL_DT_REG_UNITA_DOC, Types.TIMESTAMP, 7, false));
        map.put(COL_DS_UFF_COMP_UNITA_DOC, new ColumnDescriptor(COL_DS_UFF_COMP_UNITA_DOC, Types.VARCHAR, 254, false));
        map.put(COL_TI_CONSERVAZIONE, new ColumnDescriptor(COL_TI_CONSERVAZIONE, Types.VARCHAR, 20, false));
        map.put(COL_ID_USER_VERS, new ColumnDescriptor(COL_ID_USER_VERS, Types.DECIMAL, 22, false));
        map.put(COL_DS_KEY_ORD, new ColumnDescriptor(COL_DS_KEY_ORD, Types.VARCHAR, 100, false));
        map.put(COL_ID_REGISTRO_UNITA_DOC, new ColumnDescriptor(COL_ID_REGISTRO_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_NM_SISTEMA_MIGRAZ, new ColumnDescriptor(COL_NM_SISTEMA_MIGRAZ, Types.VARCHAR, 100, false));
        map.put(COL_NT_UNITA_DOC, new ColumnDescriptor(COL_NT_UNITA_DOC, Types.VARCHAR, 2000, false));
        map.put(COL_NT_ANNUL, new ColumnDescriptor(COL_NT_ANNUL, Types.VARCHAR, 2000, false));
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
