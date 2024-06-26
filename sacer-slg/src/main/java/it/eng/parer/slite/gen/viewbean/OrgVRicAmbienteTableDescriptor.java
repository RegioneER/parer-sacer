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
 *         Bean per la tabella Org_V_Ric_Ambiente
 *
 */
public class OrgVRicAmbienteTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 19 February 2019 11:39" )
     */

    public static final String SELECT = "Select * from Org_V_Ric_Ambiente /**/";
    public static final String TABLE_NAME = "Org_V_Ric_Ambiente";
    public static final String COL_ID_USER_IAM = "id_user_iam";
    public static final String COL_ID_AMBIENTE = "id_ambiente";
    public static final String COL_NM_AMBIENTE = "nm_ambiente";
    public static final String COL_DS_AMBIENTE = "ds_ambiente";
    public static final String COL_DT_INI_VAL = "dt_ini_val";
    public static final String COL_DT_FIN_VAL = "dt_fin_val";
    public static final String COL_ID_ENTE_CONVERV = "id_ente_converv";
    public static final String COL_ID_ENTE_GESTORE = "id_ente_gestore";
    public static final String COL_NM_ENTE_CONSERV = "nm_ente_conserv";
    public static final String COL_NM_ENTE_GESTORE = "nm_ente_gestore";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_USER_IAM, new ColumnDescriptor(COL_ID_USER_IAM, Types.DECIMAL, 22, true));
        map.put(COL_ID_AMBIENTE, new ColumnDescriptor(COL_ID_AMBIENTE, Types.DECIMAL, 22, true));
        map.put(COL_NM_AMBIENTE, new ColumnDescriptor(COL_NM_AMBIENTE, Types.VARCHAR, 100, true));
        map.put(COL_DS_AMBIENTE, new ColumnDescriptor(COL_DS_AMBIENTE, Types.VARCHAR, 254, true));
        map.put(COL_DT_INI_VAL, new ColumnDescriptor(COL_DT_INI_VAL, Types.TIMESTAMP, 7, true));
        map.put(COL_DT_FIN_VAL, new ColumnDescriptor(COL_DT_FIN_VAL, Types.TIMESTAMP, 7, true));
        map.put(COL_ID_ENTE_CONVERV, new ColumnDescriptor(COL_ID_ENTE_CONVERV, Types.DECIMAL, 22, true));
        map.put(COL_ID_ENTE_GESTORE, new ColumnDescriptor(COL_ID_ENTE_GESTORE, Types.DECIMAL, 22, true));
        map.put(COL_NM_ENTE_CONSERV, new ColumnDescriptor(COL_NM_ENTE_CONSERV, Types.VARCHAR, 254, true));
        map.put(COL_NM_ENTE_GESTORE, new ColumnDescriptor(COL_NM_ENTE_GESTORE, Types.VARCHAR, 254, true));
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
