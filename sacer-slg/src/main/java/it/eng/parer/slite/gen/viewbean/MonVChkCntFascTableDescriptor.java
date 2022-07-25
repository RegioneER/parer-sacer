package it.eng.parer.slite.gen.viewbean;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Sloth
 *
 *         Bean per la tabella
 *
 */
public class MonVChkCntFascTableDescriptor extends TableDescriptor {

    public static final String SELECT = "Select * from DUAL /**/";
    public static final String TABLE_NAME = "DUAL";

    public static final String COL_FL_FASC_CORR = "fl_fasc_corr";
    public static final String COL_FL_FASC_30GG = "fl_fasc_30gg";
    public static final String COL_FL_FASC_ATTESA_SCHED_CORR = "fl_fasc_attesa_sched_corr";
    public static final String COL_FL_FASC_ATTESA_SCHED_30GG = "fl_fasc_attesa_sched_30gg";
    public static final String COL_FL_FASC_NOSEL_SCHED_CORR = "fl_fasc_nosel_sched_corr";
    public static final String COL_FL_FASC_NOSEL_SCHED_30GG = "fl_fasc_nosel_sched_30gg";

    public static final String COL_FL_FASC_KO_FALL_CORR = "col_fl_fasc_ko_fall_corr";
    public static final String COL_FL_FASC_KO_FALL_30GG = "col_fl_fasc_ko_fall_30gg";
    public static final String COL_FL_FASC_KO_VERIF_CORR = "col_fl_fasc_ko_verif_corr";
    public static final String COL_FL_FASC_KO_VERIF_30GG = "col_fl_fasc_ko_verif_30gg";
    public static final String COL_FL_FASC_KO_NON_VERIF_CORR = "col_fl_fasc_ko_non_verif_corr";
    public static final String COL_FL_FASC_KO_NON_VERIF_30GG = "col_fl_fasc_ko_non_verif_30gg";
    public static final String COL_FL_FASC_KO_NON_RISOLUB_CORR = "col_fl_fasc_ko_non_risolub_corr";
    public static final String COL_FL_FASC_KO_NON_RISOLUB_30GG = "col_fl_fasc_ko_non_risolub_30gg";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_FL_FASC_CORR, new ColumnDescriptor(COL_FL_FASC_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_30GG, new ColumnDescriptor(COL_FL_FASC_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_ATTESA_SCHED_CORR,
                new ColumnDescriptor(COL_FL_FASC_ATTESA_SCHED_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_ATTESA_SCHED_30GG,
                new ColumnDescriptor(COL_FL_FASC_ATTESA_SCHED_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_NOSEL_SCHED_CORR,
                new ColumnDescriptor(COL_FL_FASC_NOSEL_SCHED_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_NOSEL_SCHED_30GG,
                new ColumnDescriptor(COL_FL_FASC_NOSEL_SCHED_30GG, Types.VARCHAR, 1, true));

        map.put(COL_FL_FASC_KO_FALL_CORR, new ColumnDescriptor(COL_FL_FASC_KO_FALL_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_FALL_30GG, new ColumnDescriptor(COL_FL_FASC_KO_FALL_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_VERIF_CORR, new ColumnDescriptor(COL_FL_FASC_KO_VERIF_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_VERIF_30GG, new ColumnDescriptor(COL_FL_FASC_KO_VERIF_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_NON_VERIF_CORR,
                new ColumnDescriptor(COL_FL_FASC_KO_NON_VERIF_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_NON_VERIF_30GG,
                new ColumnDescriptor(COL_FL_FASC_KO_NON_VERIF_30GG, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_NON_RISOLUB_CORR,
                new ColumnDescriptor(COL_FL_FASC_KO_NON_RISOLUB_CORR, Types.VARCHAR, 1, true));
        map.put(COL_FL_FASC_KO_NON_RISOLUB_30GG,
                new ColumnDescriptor(COL_FL_FASC_KO_NON_RISOLUB_30GG, Types.VARCHAR, 1, true));

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
