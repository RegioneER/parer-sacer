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
 *         Bean per la tabella Dec_Criterio_Raggr
 *
 */
public class DecCriterioRaggrTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 9 August 2017 12:13" )
     */

    public static final String SELECT = "Select * from Dec_Criterio_Raggr /**/";
    public static final String TABLE_NAME = "Dec_Criterio_Raggr";
    public static final String COL_ID_CRITERIO_RAGGR = "id_criterio_raggr";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_NM_CRITERIO_RAGGR = "nm_criterio_raggr";
    public static final String COL_DS_CRITERIO_RAGGR = "ds_criterio_raggr";
    public static final String COL_NI_MAX_COMP = "ni_max_comp";
    public static final String COL_NI_MAX_ELENCHI_BY_GG = "ni_max_elenchi_by_gg";
    public static final String COL_TI_SCAD_CHIUS_VOLUME = "ti_scad_chius_volume";
    public static final String COL_TI_TEMPO_SCAD_CHIUS = "ti_tempo_scad_chius";
    public static final String COL_NI_TEMPO_SCAD_CHIUS = "ni_tempo_scad_chius";
    public static final String COL_AA_KEY_UNITA_DOC = "aa_key_unita_doc";
    public static final String COL_CD_KEY_UNITA_DOC = "cd_key_unita_doc";
    public static final String COL_FL_UNITA_DOC_FIRMATO = "fl_unita_doc_firmato";
    public static final String COL_DT_CREAZIONE_UNITA_DOC_DA = "dt_creazione_unita_doc_da";
    public static final String COL_DT_CREAZIONE_UNITA_DOC_A = "dt_creazione_unita_doc_a";
    public static final String COL_FL_FORZA_ACCETTAZIONE = "fl_forza_accettazione";
    public static final String COL_FL_FORZA_CONSERVAZIONE = "fl_forza_conservazione";
    public static final String COL_DT_ISTITUZ = "dt_istituz";
    public static final String COL_DT_SOPPRES = "dt_soppres";
    public static final String COL_FL_FILTRO_TIPO_UNITA_DOC = "fl_filtro_tipo_unita_doc";
    public static final String COL_FL_FILTRO_REGISTRO_KEY = "fl_filtro_registro_key";
    public static final String COL_FL_FILTRO_RANGE_REGISTRO_KEY = "fl_filtro_range_registro_key";
    public static final String COL_AA_KEY_UNITA_DOC_DA = "aa_key_unita_doc_da";
    public static final String COL_AA_KEY_UNITA_DOC_A = "aa_key_unita_doc_a";
    public static final String COL_CD_KEY_UNITA_DOC_DA = "cd_key_unita_doc_da";
    public static final String COL_CD_KEY_UNITA_DOC_A = "cd_key_unita_doc_a";
    public static final String COL_FL_FILTRO_TI_ESITO_VERIF_FIRME = "fl_filtro_ti_esito_verif_firme";
    public static final String COL_DL_OGGETTO_UNITA_DOC = "dl_oggetto_unita_doc";
    public static final String COL_DT_REG_UNITA_DOC_DA = "dt_reg_unita_doc_da";
    public static final String COL_DT_REG_UNITA_DOC_A = "dt_reg_unita_doc_a";
    public static final String COL_FL_FILTRO_TIPO_DOC = "fl_filtro_tipo_doc";
    public static final String COL_DL_DOC = "dl_doc";
    public static final String COL_DS_AUTORE_DOC = "ds_autore_doc";
    public static final String COL_BL_FILTRI_DATI_SPEC_DOC = "bl_filtri_dati_spec_doc";
    public static final String COL_TI_CONSERVAZIONE = "ti_conservazione";
    public static final String COL_FL_FILTRO_SISTEMA_MIGRAZ = "fl_filtro_sistema_migraz";
    public static final String COL_NT_CRITERIO_RAGGR = "nt_criterio_raggr";
    public static final String COL_FL_CRITERIO_RAGGR_STANDARD = "fl_criterio_raggr_standard";
    public static final String COL_FL_CRITERIO_RAGGR_FISC = "fl_criterio_raggr_fisc";
    public static final String COL_TI_GEST_ELENCO_CRITERIO = "ti_gest_elenco_criterio";
    public static final String COL_TI_VALID_ELENCO = "ti_valid_elenco";
    public static final String COL_TI_MOD_VALID_ELENCO = "ti_mod_valid_elenco";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_CRITERIO_RAGGR, new ColumnDescriptor(COL_ID_CRITERIO_RAGGR, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
        map.put(COL_NM_CRITERIO_RAGGR, new ColumnDescriptor(COL_NM_CRITERIO_RAGGR, Types.VARCHAR, 100, false));
        map.put(COL_DS_CRITERIO_RAGGR, new ColumnDescriptor(COL_DS_CRITERIO_RAGGR, Types.VARCHAR, 254, false));
        map.put(COL_NI_MAX_COMP, new ColumnDescriptor(COL_NI_MAX_COMP, Types.DECIMAL, 22, false));
        map.put(COL_NI_MAX_ELENCHI_BY_GG, new ColumnDescriptor(COL_NI_MAX_ELENCHI_BY_GG, Types.DECIMAL, 22, false));
        map.put(COL_TI_SCAD_CHIUS_VOLUME, new ColumnDescriptor(COL_TI_SCAD_CHIUS_VOLUME, Types.VARCHAR, 20, false));
        map.put(COL_TI_TEMPO_SCAD_CHIUS, new ColumnDescriptor(COL_TI_TEMPO_SCAD_CHIUS, Types.VARCHAR, 20, false));
        map.put(COL_NI_TEMPO_SCAD_CHIUS, new ColumnDescriptor(COL_NI_TEMPO_SCAD_CHIUS, Types.DECIMAL, 22, false));
        map.put(COL_AA_KEY_UNITA_DOC, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC, Types.DECIMAL, 22, false));
        map.put(COL_CD_KEY_UNITA_DOC, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC, Types.VARCHAR, 100, false));
        map.put(COL_FL_UNITA_DOC_FIRMATO, new ColumnDescriptor(COL_FL_UNITA_DOC_FIRMATO, Types.VARCHAR, 1, false));
        map.put(COL_DT_CREAZIONE_UNITA_DOC_DA,
                new ColumnDescriptor(COL_DT_CREAZIONE_UNITA_DOC_DA, Types.TIMESTAMP, 7, false));
        map.put(COL_DT_CREAZIONE_UNITA_DOC_A,
                new ColumnDescriptor(COL_DT_CREAZIONE_UNITA_DOC_A, Types.TIMESTAMP, 7, false));
        map.put(COL_FL_FORZA_ACCETTAZIONE, new ColumnDescriptor(COL_FL_FORZA_ACCETTAZIONE, Types.VARCHAR, 1, false));
        map.put(COL_FL_FORZA_CONSERVAZIONE, new ColumnDescriptor(COL_FL_FORZA_CONSERVAZIONE, Types.VARCHAR, 1, false));
        map.put(COL_DT_ISTITUZ, new ColumnDescriptor(COL_DT_ISTITUZ, Types.TIMESTAMP, 7, false));
        map.put(COL_DT_SOPPRES, new ColumnDescriptor(COL_DT_SOPPRES, Types.TIMESTAMP, 7, false));
        map.put(COL_FL_FILTRO_TIPO_UNITA_DOC,
                new ColumnDescriptor(COL_FL_FILTRO_TIPO_UNITA_DOC, Types.VARCHAR, 1, false));
        map.put(COL_FL_FILTRO_REGISTRO_KEY, new ColumnDescriptor(COL_FL_FILTRO_REGISTRO_KEY, Types.VARCHAR, 1, false));
        map.put(COL_FL_FILTRO_RANGE_REGISTRO_KEY,
                new ColumnDescriptor(COL_FL_FILTRO_RANGE_REGISTRO_KEY, Types.VARCHAR, 1, false));
        map.put(COL_AA_KEY_UNITA_DOC_DA, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC_DA, Types.DECIMAL, 22, false));
        map.put(COL_AA_KEY_UNITA_DOC_A, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC_A, Types.DECIMAL, 22, false));
        map.put(COL_CD_KEY_UNITA_DOC_DA, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC_DA, Types.VARCHAR, 100, false));
        map.put(COL_CD_KEY_UNITA_DOC_A, new ColumnDescriptor(COL_CD_KEY_UNITA_DOC_A, Types.VARCHAR, 100, false));
        map.put(COL_FL_FILTRO_TI_ESITO_VERIF_FIRME,
                new ColumnDescriptor(COL_FL_FILTRO_TI_ESITO_VERIF_FIRME, Types.VARCHAR, 1, false));
        map.put(COL_DL_OGGETTO_UNITA_DOC, new ColumnDescriptor(COL_DL_OGGETTO_UNITA_DOC, Types.VARCHAR, 1024, false));
        map.put(COL_DT_REG_UNITA_DOC_DA, new ColumnDescriptor(COL_DT_REG_UNITA_DOC_DA, Types.TIMESTAMP, 7, false));
        map.put(COL_DT_REG_UNITA_DOC_A, new ColumnDescriptor(COL_DT_REG_UNITA_DOC_A, Types.TIMESTAMP, 7, false));
        map.put(COL_FL_FILTRO_TIPO_DOC, new ColumnDescriptor(COL_FL_FILTRO_TIPO_DOC, Types.VARCHAR, 1, false));
        map.put(COL_DL_DOC, new ColumnDescriptor(COL_DL_DOC, Types.VARCHAR, 1024, false));
        map.put(COL_DS_AUTORE_DOC, new ColumnDescriptor(COL_DS_AUTORE_DOC, Types.VARCHAR, 254, false));
        map.put(COL_BL_FILTRI_DATI_SPEC_DOC,
                new ColumnDescriptor(COL_BL_FILTRI_DATI_SPEC_DOC, Types.CLOB, 4000, false));
        map.put(COL_TI_CONSERVAZIONE, new ColumnDescriptor(COL_TI_CONSERVAZIONE, Types.VARCHAR, 20, false));
        map.put(COL_FL_FILTRO_SISTEMA_MIGRAZ,
                new ColumnDescriptor(COL_FL_FILTRO_SISTEMA_MIGRAZ, Types.VARCHAR, 1, false));
        map.put(COL_NT_CRITERIO_RAGGR, new ColumnDescriptor(COL_NT_CRITERIO_RAGGR, Types.VARCHAR, 2000, false));
        map.put(COL_FL_CRITERIO_RAGGR_STANDARD,
                new ColumnDescriptor(COL_FL_CRITERIO_RAGGR_STANDARD, Types.VARCHAR, 1, false));
        map.put(COL_FL_CRITERIO_RAGGR_FISC, new ColumnDescriptor(COL_FL_CRITERIO_RAGGR_FISC, Types.VARCHAR, 1, false));
        map.put(COL_TI_GEST_ELENCO_CRITERIO,
                new ColumnDescriptor(COL_TI_GEST_ELENCO_CRITERIO, Types.VARCHAR, 30, false));
        map.put(COL_TI_VALID_ELENCO, new ColumnDescriptor(COL_TI_VALID_ELENCO, Types.VARCHAR, 30, false));
        map.put(COL_TI_MOD_VALID_ELENCO, new ColumnDescriptor(COL_TI_MOD_VALID_ELENCO, Types.VARCHAR, 30, false));
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
