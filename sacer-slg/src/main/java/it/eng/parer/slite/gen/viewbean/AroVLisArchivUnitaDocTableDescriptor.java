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
 *         Bean per la tabella Aro_V_Lis_Archiv_Unita_Doc
 *
 */
public class AroVLisArchivUnitaDocTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 20 December 2012 16:14" )
     */

    public static final String SELECT = "Select * from Aro_V_Lis_Archiv_Unita_Doc /**/";
    public static final String TABLE_NAME = "Aro_V_Lis_Archiv_Unita_Doc";
    public static final String COL_ID_UNITA_DOC = "id_unita_doc";
    public static final String COL_ID_ARCHIV_SEC = "id_archiv_sec";
    public static final String COL_DS_CLASSIF = "ds_classif";
    public static final String COL_CD_FASCIC = "cd_fascic";
    public static final String COL_DS_OGGETTO_FASCIC = "ds_oggetto_fascic";
    public static final String COL_CD_SOTTOFASCIC = "cd_sottofascic";
    public static final String COL_DS_OGGETTO_SOTTOFASCIC = "ds_oggetto_sottofascic";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_UNITA_DOC, new ColumnDescriptor(COL_ID_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_ARCHIV_SEC, new ColumnDescriptor(COL_ID_ARCHIV_SEC, Types.DECIMAL, 22, true));
        map.put(COL_DS_CLASSIF, new ColumnDescriptor(COL_DS_CLASSIF, Types.VARCHAR, 254, true));
        map.put(COL_CD_FASCIC, new ColumnDescriptor(COL_CD_FASCIC, Types.VARCHAR, 100, true));
        map.put(COL_DS_OGGETTO_FASCIC, new ColumnDescriptor(COL_DS_OGGETTO_FASCIC, Types.VARCHAR, 1024, true));
        map.put(COL_CD_SOTTOFASCIC, new ColumnDescriptor(COL_CD_SOTTOFASCIC, Types.VARCHAR, 100, true));
        map.put(COL_DS_OGGETTO_SOTTOFASCIC,
                new ColumnDescriptor(COL_DS_OGGETTO_SOTTOFASCIC, Types.VARCHAR, 1024, true));
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
