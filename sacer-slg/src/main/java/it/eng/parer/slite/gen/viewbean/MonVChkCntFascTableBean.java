package it.eng.parer.slite.gen.viewbean;

/**
 * ViewBean per la vista 
 *
 */
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

import java.util.Iterator;

/**
 * TableBean per la tabella
 *
 */
public class MonVChkCntFascTableBean extends AbstractBaseTable<MonVChkCntFascRowBean> {

    public static MonVChkCntFascTableDescriptor TABLE_DESCRIPTOR = new MonVChkCntFascTableDescriptor();

    public MonVChkCntFascTableBean() {
        super();
    }

    protected MonVChkCntFascRowBean createRow() {
        return new MonVChkCntFascRowBean();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    @Deprecated
    public Iterator<MonVChkCntFascRowBean> getRowsIterator() {
        return iterator();
    }
}
