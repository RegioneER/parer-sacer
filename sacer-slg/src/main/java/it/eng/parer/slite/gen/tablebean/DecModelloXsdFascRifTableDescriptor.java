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

package it.eng.parer.slite.gen.tablebean;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * TableDescriptor per la tabella Dec_Modello_Xsd_Fasc_Rif
 *
 */
public class DecModelloXsdFascRifTableDescriptor extends TableDescriptor {

    private static final long serialVersionUID = 1L;

    public static final String SELECT = "Select * from Dec_Modello_Xsd_Fasc_Rif /**/";
    public static final String TABLE_NAME = "Dec_Modello_Xsd_Fasc_Rif";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put("id_modello_xsd_fasc_rif",
                new ColumnDescriptor("id_modello_xsd_fasc_rif", Types.DECIMAL, 22, true));
        map.put("id_modello_xsd_fascicolo_padre",
                new ColumnDescriptor("id_modello_xsd_fascicolo_padre", Types.DECIMAL, 22, false));
        map.put("id_modello_xsd_fascicolo_target",
                new ColumnDescriptor("id_modello_xsd_fascicolo_target", Types.DECIMAL, 22, false));
        map.put("ti_riferimento", new ColumnDescriptor("ti_riferimento", Types.VARCHAR, 20, false));
        map.put("namespace_uri", new ColumnDescriptor("namespace_uri", Types.VARCHAR, 500, false));
        map.put("schema_location",
                new ColumnDescriptor("schema_location", Types.VARCHAR, 500, false));
        map.put("dt_istituz", new ColumnDescriptor("dt_istituz", Types.TIMESTAMP, 7, false));
        map.put("dt_soppres", new ColumnDescriptor("dt_soppres", Types.TIMESTAMP, 7, false));
        map.put("cd_xsd_target", new ColumnDescriptor("cd_xsd_target", Types.VARCHAR, 100, false));
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
