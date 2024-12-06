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
