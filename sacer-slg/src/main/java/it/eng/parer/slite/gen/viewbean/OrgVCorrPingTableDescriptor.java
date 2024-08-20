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
 *
 * @author gpiccioli
 *
 *         Bean per la tabella Org_V_Corr_Ping
 */
public class OrgVCorrPingTableDescriptor extends TableDescriptor {
    public static final String SELECT = "Select * from Org_V_Corr_Ping /**/";
    public static final String TABLE_NAME = "Org_V_Corr_Ping";
    public static final String COL_ID_DICH_VERS_SACER = "id_dich_vers_sacer";
    public static final String COL_ID_VERS = "id_vers";
    public static final String COL_NM_VERS = "nm_vers";
    public static final String COL_TI_DICH_VERS = "ti_dich_vers";
    public static final String COL_ID_ORGANIZ_IAM = "id_organiz_iam";
    public static final String COL_ID_ORGANIZ_APPLIC = "id_organiz_applic";
    public static final String COL_NM_ENTITA = "nm_entita";
    public static final String COL_NM_AMBIENTE_VERS = "nm_ambiente_vers";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_DICH_VERS_SACER, new ColumnDescriptor(COL_ID_DICH_VERS_SACER, Types.DECIMAL, 22, true));
        map.put(COL_ID_VERS, new ColumnDescriptor(COL_ID_VERS, Types.DECIMAL, 22, false));
        map.put(COL_NM_VERS, new ColumnDescriptor(COL_NM_VERS, Types.VARCHAR, 100, false));
        map.put(COL_TI_DICH_VERS, new ColumnDescriptor(COL_TI_DICH_VERS, Types.VARCHAR, 20, false));
        map.put(COL_ID_ORGANIZ_IAM, new ColumnDescriptor(COL_ID_ORGANIZ_IAM, Types.DECIMAL, 22, false));
        map.put(COL_ID_ORGANIZ_APPLIC, new ColumnDescriptor(COL_ID_ORGANIZ_APPLIC, Types.DECIMAL, 22, false));
        map.put(COL_NM_ENTITA, new ColumnDescriptor(COL_NM_ENTITA, Types.VARCHAR, 20, false));
        map.put(COL_NM_AMBIENTE_VERS, new ColumnDescriptor(COL_NM_AMBIENTE_VERS, Types.VARCHAR, 100, false));
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
