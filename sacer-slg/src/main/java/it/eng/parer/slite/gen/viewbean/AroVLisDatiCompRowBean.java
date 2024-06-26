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

import java.math.BigDecimal;

import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_V_Lis_Dati_Comp
 *
 */
public class AroVLisDatiCompRowBean extends BaseRow {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 14 September 2011 11:38" )
     */
    private static final long serialVersionUID = 1L;

    public static AroVLisDatiCompTableDescriptor TABLE_DESCRIPTOR = new AroVLisDatiCompTableDescriptor();

    public AroVLisDatiCompRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdCompDoc() {
        return getBigDecimal("id_comp_doc");
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        setObject("id_comp_doc", idCompDoc);
    }

    public BigDecimal getIdDatiComp() {
        return getBigDecimal("id_dati_comp");
    }

    public void setIdDatiComp(BigDecimal idDatiComp) {
        setObject("id_dati_comp", idDatiComp);
    }

    public String getNmAttribTipoComp() {
        return getString("nm_attrib_tipo_comp");
    }

    public void setNmAttribTipoComp(String nmAttribTipoComp) {
        setObject("nm_attrib_tipo_comp", nmAttribTipoComp);
    }

    public String getDsAttribTipoComp() {
        return getString("ds_attrib_tipo_comp");
    }

    public void setDsAttribTipoComp(String dsAttribTipoComp) {
        setObject("ds_attrib_tipo_comp", dsAttribTipoComp);
    }

    public String getDlValore() {
        return getString("dl_valore");
    }

    public void setDlValore(String dlValore) {
        setObject("dl_valore", dlValore);
    }

    public BigDecimal getNiOrdAttrib() {
        return getBigDecimal("ni_ord_attrib");
    }

    public void setNiOrdAttrib(BigDecimal niOrdAttrib) {
        setObject("ni_ord_attrib", niOrdAttrib);
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.parseInt(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.parseInt(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.parseInt(getObject("numrecords").toString());
    }

}
