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

package it.eng.parer.slite.gen.tablebean;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella Org_Tipo_Servizio
 *
 */
public class OrgTipoServizioTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 24 June 2016 11:34" )
     */

    public static final String SELECT = "Select * from Org_Tipo_Servizio /**/";
    public static final String TABLE_NAME = "Org_Tipo_Servizio";
    public static final String COL_ID_TIPO_SERVIZIO = "id_tipo_servizio";
    public static final String COL_CD_TIPO_SERVIZIO = "cd_tipo_servizio";
    public static final String COL_DS_TIPO_SERVIZIO = "ds_tipo_servizio";
    public static final String COL_TI_CLASSE_TIPO_SERVIZIO = "ti_classe_tipo_servizio";
    public static final String COL_TIPO_FATTURAZIONE = "tipo_fatturazione";
    public static final String COL_GG_FATTURAZIONE = "gg_fatturazione";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_TIPO_SERVIZIO, new ColumnDescriptor(COL_ID_TIPO_SERVIZIO, Types.DECIMAL, 22, true));
        map.put(COL_CD_TIPO_SERVIZIO, new ColumnDescriptor(COL_CD_TIPO_SERVIZIO, Types.VARCHAR, 100, false));
        map.put(COL_DS_TIPO_SERVIZIO, new ColumnDescriptor(COL_DS_TIPO_SERVIZIO, Types.VARCHAR, 254, false));
        map.put(COL_TI_CLASSE_TIPO_SERVIZIO,
                new ColumnDescriptor(COL_TI_CLASSE_TIPO_SERVIZIO, Types.VARCHAR, 30, false));
        map.put(COL_TIPO_FATTURAZIONE, new ColumnDescriptor(COL_TIPO_FATTURAZIONE, Types.VARCHAR, 30, false));
        map.put(COL_GG_FATTURAZIONE, new ColumnDescriptor(COL_GG_FATTURAZIONE, Types.VARCHAR, 5, false));
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
