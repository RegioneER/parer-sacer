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
 *         Bean per la tabella Dec_V_Calc_Ti_Serv_On_Tipo_Ud
 *
 */
public class DecVCalcTiServOnTipoUdTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 21 December 2016 10:45" )
     */
    public static final String SELECT = "Select * from Dec_V_Calc_Ti_Serv_On_Tipo_Ud /**/";
    public static final String TABLE_NAME = "Dec_V_Calc_Ti_Serv_On_Tipo_Ud";
    public static final String COL_ID_CATEG_TIPO_UNITA_DOC = "id_categ_tipo_unita_doc";
    public static final String COL_ID_STRUT = "id_strut";
    public static final String COL_ID_TIPO_SERVIZIO_CONSERV = "id_tipo_servizio_conserv";
    public static final String COL_ID_TIPO_SERVIZIO_ATTIV = "id_tipo_servizio_attiv";
    public static final String COL_CD_ALGO_TARIFFARIO = "cd_algo_tariffario";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_CATEG_TIPO_UNITA_DOC,
                new ColumnDescriptor(COL_ID_CATEG_TIPO_UNITA_DOC, Types.DECIMAL, 22, true));
        map.put(COL_ID_STRUT, new ColumnDescriptor(COL_ID_STRUT, Types.DECIMAL, 22, true));
        map.put(COL_ID_TIPO_SERVIZIO_CONSERV,
                new ColumnDescriptor(COL_ID_TIPO_SERVIZIO_CONSERV, Types.DECIMAL, 22, true));
        map.put(COL_ID_TIPO_SERVIZIO_ATTIV, new ColumnDescriptor(COL_ID_TIPO_SERVIZIO_ATTIV, Types.DECIMAL, 22, true));
        map.put(COL_CD_ALGO_TARIFFARIO, new ColumnDescriptor(COL_CD_ALGO_TARIFFARIO, Types.VARCHAR, 14, true));
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
