/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.eng.parer.slite.gen.tablebean;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author gpiccioli
 */
public class VrsSessioneVersKoEliminateTableDescriptor extends TableDescriptor {

    public static final String SELECT = "Select * from Vrs_Sessione_Vers_Ko_Eliminate /**/";
    public static final String TABLE_NAME = "Vrs_Sessione_Vers_Ko_Eliminate";
    public static final String COL_ID_SESSIONE_VERS_KO_ELIMINATE = "id_sessione_vers_eliminate";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_NM_STRUT = "nm_strut";
    public static final String COL_DS_STRUT = "ds_strut";
    public static final String COL_NI_SES_ELIMINATE = "ni_ses_eliminate";
    public static final String COL_DT_ELAB = "dt_elab";
    public static final String COL_DT_RIF = "dt_rif";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
	map.put(COL_ID_SESSIONE_VERS_KO_ELIMINATE,
		new ColumnDescriptor(COL_ID_SESSIONE_VERS_KO_ELIMINATE, Types.DECIMAL, 22, true));
	map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, false));
	map.put(COL_NM_STRUT, new ColumnDescriptor(COL_NM_STRUT, Types.VARCHAR, 100, false));
	map.put(COL_DS_STRUT, new ColumnDescriptor(COL_DS_STRUT, Types.VARCHAR, 254, false));
	map.put(COL_NI_SES_ELIMINATE,
		new ColumnDescriptor(COL_NI_SES_ELIMINATE, Types.DECIMAL, 22, false));
	map.put(COL_DT_ELAB, new ColumnDescriptor(COL_DT_ELAB, Types.TIMESTAMP, 7, false));
	map.put(COL_DT_RIF, new ColumnDescriptor(COL_DT_RIF, Types.TIMESTAMP, 7, false));
    }

    public Map<String, ColumnDescriptor> getColumnMap() {
	return map;
    }

    @Override
    public String getTableName() {
	return TABLE_NAME;
    }

    @Override
    public String getStatement() {
	return SELECT;
    }
}
