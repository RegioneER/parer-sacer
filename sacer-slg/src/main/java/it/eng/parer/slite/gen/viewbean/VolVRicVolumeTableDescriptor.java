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
 *         Bean per la tabella Vol_V_Ric_Volume
 *
 */
public class VolVRicVolumeTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 20 December 2012 16:14" )
     */

    public static final String SELECT = "Select * from Vol_V_Ric_Volume /**/";
    public static final String TABLE_NAME = "Vol_V_Ric_Volume";
    public static final String COL_ID_VOLUME_CONSERV = "id_volume_conserv";
    public static final String COL_ID_STRUT_VOLUME = "id_strut_volume";
    public static final String COL_NM_VOLUME_CONSERV = "nm_volume_conserv";
    public static final String COL_DS_VOLUME_CONSERV = "ds_volume_conserv";
    public static final String COL_TI_STATO_VOLUME_CONSERV = "ti_stato_volume_conserv";
    public static final String COL_DT_CREAZIONE = "dt_creazione";
    public static final String COL_NI_MAX_UNITA_DOC = "ni_max_unita_doc";
    public static final String COL_NI_MAX_COMP = "ni_max_comp";
    public static final String COL_TI_SCAD_CHIUS_VOLUME = "ti_scad_chius_volume";
    public static final String COL_NI_TEMPO_SCAD_CHIUS = "ni_tempo_scad_chius";
    public static final String COL_TI_TEMPO_SCAD_CHIUS = "ti_tempo_scad_chius";
    public static final String COL_NI_TEMPO_SCAD_CHIUS_FIRME = "ni_tempo_scad_chius_firme";
    public static final String COL_TI_TEMPO_SCAD_CHIUS_FIRME = "ti_tempo_scad_chius_firme";
    public static final String COL_NI_UNITA_DOC_VOLUME = "ni_unita_doc_volume";
    public static final String COL_NI_COMP_VOLUME = "ni_comp_volume";
    public static final String COL_NI_KB_SIZE = "ni_kb_size";
    public static final String COL_DT_SCAD_CHIUS = "dt_scad_chius";
    public static final String COL_NT_INDICE_VOLUME = "nt_indice_volume";
    public static final String COL_DT_CHIUS = "dt_chius";
    public static final String COL_DL_MOTIVO_CHIUS = "dl_motivo_chius";
    public static final String COL_DT_FIRMA_MARCA = "dt_firma_marca";
    public static final String COL_TI_PRESENZA_FIRME = "ti_presenza_firme";
    public static final String COL_TI_VAL_FIRME = "ti_val_firme";
    public static final String COL_CD_VERSIONE_INDICE = "cd_versione_indice";
    public static final String COL_NT_VOLUME_CHIUSO = "nt_volume_chiuso";
    public static final String COL_ID_CRITERIO_RAGGR = "id_criterio_raggr";
    public static final String COL_ID_STRUT_CRITERIO = "id_strut_criterio";
    public static final String COL_NM_CRITERIO_RAGGR = "nm_criterio_raggr";
    public static final String COL_ID_APPART_UNITA_DOC_VOLUME = "id_appart_unita_doc_volume";
    public static final String COL_ID_UNITA_DOC = "id_unita_doc";
    public static final String COL_ID_STRUT_UNITA_DOC = "id_strut_unita_doc";
    public static final String COL_CD_REGISTRO_KEY_UNITA_DOC = "cd_registro_key_unita_doc";
    public static final String COL_AA_KEY_UNITA_DOC = "aa_key_unita_doc";
    public static final String COL_CD_KEY_UNITA_DOC = "cd_key_unita_doc";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_VOLUME_CONSERV, new ColumnDescriptor(COL_ID_VOLUME_CONSERV, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT_VOLUME, new ColumnDescriptor(COL_ID_STRUT_VOLUME, Types.DECIMAL, 22, true));
        map.put(COL_NM_VOLUME_CONSERV, new ColumnDescriptor(COL_NM_VOLUME_CONSERV, Types.VARCHAR, 100, true));
        map.put(COL_DS_VOLUME_CONSERV, new ColumnDescriptor(COL_DS_VOLUME_CONSERV, Types.VARCHAR, 254, true));
        map.put(COL_TI_STATO_VOLUME_CONSERV,
                new ColumnDescriptor(COL_TI_STATO_VOLUME_CONSERV, Types.VARCHAR, 20, true));
        map.put(COL_DT_CREAZIONE, new ColumnDescriptor(COL_DT_CREAZIONE, Types.TIMESTAMP, 7, true));
        map.put(COL_NI_MAX_UNITA_DOC, new ColumnDescriptor(COL_NI_MAX_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_NI_MAX_COMP, new ColumnDescriptor(COL_NI_MAX_COMP, Types.DECIMAL, 22, true));
        map.put(COL_TI_SCAD_CHIUS_VOLUME, new ColumnDescriptor(COL_TI_SCAD_CHIUS_VOLUME, Types.VARCHAR, 20, true));
        map.put(COL_NI_TEMPO_SCAD_CHIUS, new ColumnDescriptor(COL_NI_TEMPO_SCAD_CHIUS, Types.DECIMAL, 22, true));
        map.put(COL_TI_TEMPO_SCAD_CHIUS, new ColumnDescriptor(COL_TI_TEMPO_SCAD_CHIUS, Types.VARCHAR, 20, true));
        map.put(COL_NI_TEMPO_SCAD_CHIUS_FIRME,
                new ColumnDescriptor(COL_NI_TEMPO_SCAD_CHIUS_FIRME, Types.DECIMAL, 22, true));
        map.put(COL_TI_TEMPO_SCAD_CHIUS_FIRME,
                new ColumnDescriptor(COL_TI_TEMPO_SCAD_CHIUS_FIRME, Types.VARCHAR, 20, true));
        map.put(COL_NI_UNITA_DOC_VOLUME, new ColumnDescriptor(COL_NI_UNITA_DOC_VOLUME, Types.DECIMAL, 22, true));
        map.put(COL_NI_COMP_VOLUME, new ColumnDescriptor(COL_NI_COMP_VOLUME, Types.DECIMAL, 22, true));
        map.put(COL_NI_KB_SIZE, new ColumnDescriptor(COL_NI_KB_SIZE, Types.DECIMAL, 22, true));
        map.put(COL_DT_SCAD_CHIUS, new ColumnDescriptor(COL_DT_SCAD_CHIUS, Types.TIMESTAMP, 7, true));
        map.put(COL_NT_INDICE_VOLUME, new ColumnDescriptor(COL_NT_INDICE_VOLUME, Types.VARCHAR, 2000, true));
        map.put(COL_DT_CHIUS, new ColumnDescriptor(COL_DT_CHIUS, Types.TIMESTAMP, 7, true));
        map.put(COL_DL_MOTIVO_CHIUS, new ColumnDescriptor(COL_DL_MOTIVO_CHIUS, Types.VARCHAR, 1024, true));
        map.put(COL_DT_FIRMA_MARCA, new ColumnDescriptor(COL_DT_FIRMA_MARCA, Types.TIMESTAMP, 7, true));
        map.put(COL_TI_PRESENZA_FIRME, new ColumnDescriptor(COL_TI_PRESENZA_FIRME, Types.VARCHAR, 20, true));
        map.put(COL_TI_VAL_FIRME, new ColumnDescriptor(COL_TI_VAL_FIRME, Types.VARCHAR, 20, true));
        map.put(COL_CD_VERSIONE_INDICE, new ColumnDescriptor(COL_CD_VERSIONE_INDICE, Types.VARCHAR, 100, true));
        map.put(COL_NT_VOLUME_CHIUSO, new ColumnDescriptor(COL_NT_VOLUME_CHIUSO, Types.VARCHAR, 2000, true));
        map.put(COL_ID_CRITERIO_RAGGR, new ColumnDescriptor(COL_ID_CRITERIO_RAGGR, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT_CRITERIO, new ColumnDescriptor(COL_ID_STRUT_CRITERIO, Types.DECIMAL, 22, true));
        map.put(COL_NM_CRITERIO_RAGGR, new ColumnDescriptor(COL_NM_CRITERIO_RAGGR, Types.VARCHAR, 100, true));
        map.put(COL_ID_APPART_UNITA_DOC_VOLUME,
                new ColumnDescriptor(COL_ID_APPART_UNITA_DOC_VOLUME, Types.DECIMAL, 22, true));
        map.put(COL_ID_UNITA_DOC, new ColumnDescriptor(COL_ID_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT_UNITA_DOC, new ColumnDescriptor(COL_ID_STRUT_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_CD_REGISTRO_KEY_UNITA_DOC,
                new ColumnDescriptor(COL_CD_REGISTRO_KEY_UNITA_DOC, Types.VARCHAR, 100, true));
        map.put(COL_AA_KEY_UNITA_DOC, new ColumnDescriptor(COL_AA_KEY_UNITA_DOC, Types.DECIMAL, 22, true));
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
