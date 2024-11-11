/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.eng.parer.slite.gen.tablebean;

import static it.eng.parer.slite.gen.tablebean.VrsSessioneVersKoTableBean.TABLE_DESCRIPTOR;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 *
 * @author gpiccioli
 */
public class VrsSessioneVersKoEliminateTableBean extends AbstractBaseTable<VrsSessioneVersKoEliminateRowBean> {

    private static final long serialVersionUID = 1L;

    public static VrsSessioneVersKoEliminateTableDescriptor TABLE_DESCRIPTOR = new VrsSessioneVersKoEliminateTableDescriptor();

    public VrsSessioneVersKoEliminateTableBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    @Override
    protected VrsSessioneVersKoEliminateRowBean createRow() {
        return new VrsSessioneVersKoEliminateRowBean();
    }

}
