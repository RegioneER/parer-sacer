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
 *         Bean per la tabella Mon_V_Chk_Agg_Strut
 *
 */
public class MonVChkCntAggTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 17 October 2014 16:24" )
     */

    public static final String SELECT = "Select * from Mon_V_Chk_Agg_Strut /**/";
    public static final String TABLE_NAME = "Mon_V_Chk_Agg_Strut";
    public static final String COL_FL_AGG_RISOLTI_CORR = "fl_agg_risolti_corr";
    public static final String COL_FL_AGG_RISOLTI_30GG = "fl_agg_risolti_30gg";
    public static final String COL_FL_AGG_NOVERIF_CORR = "fl_agg_noverif_corr";
    public static final String COL_FL_AGG_NOVERIF_30GG = "fl_agg_noverif_30gg";
    public static final String COL_FL_AGG_VERIF_CORR = "fl_agg_verif_corr";
    public static final String COL_FL_AGG_VERIF_30GG = "fl_agg_verif_30gg";
    public static final String COL_FL_AGG_NORISOLUB_CORR = "fl_agg_norisolub_corr";
    public static final String COL_FL_AGG_NORISOLUB_30GG = "fl_agg_norisolub_30gg";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_FL_AGG_RISOLTI_CORR, new ColumnDescriptor(COL_FL_AGG_RISOLTI_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_RISOLTI_30GG, new ColumnDescriptor(COL_FL_AGG_RISOLTI_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_NOVERIF_CORR, new ColumnDescriptor(COL_FL_AGG_NOVERIF_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_NOVERIF_30GG, new ColumnDescriptor(COL_FL_AGG_NOVERIF_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_VERIF_CORR, new ColumnDescriptor(COL_FL_AGG_VERIF_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_VERIF_30GG, new ColumnDescriptor(COL_FL_AGG_VERIF_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_NORISOLUB_CORR, new ColumnDescriptor(COL_FL_AGG_NORISOLUB_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_AGG_NORISOLUB_30GG, new ColumnDescriptor(COL_FL_AGG_NORISOLUB_30GG, Types.VARCHAR, 1, true));
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
