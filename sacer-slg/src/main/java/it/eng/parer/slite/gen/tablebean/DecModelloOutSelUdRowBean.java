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

import java.math.BigDecimal;

import it.eng.parer.entity.DecModelloOutSelUd;
import it.eng.parer.entity.DecModelloTipoSerie;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Modello_Out_Sel_Ud
 *
 */
public class DecModelloOutSelUdRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 7 April 2016 13:47" )
     */
    private static final long serialVersionUID = 1L;

    public static DecModelloOutSelUdTableDescriptor TABLE_DESCRIPTOR = new DecModelloOutSelUdTableDescriptor();

    public DecModelloOutSelUdRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdModelloOutSelUd() {
        return getBigDecimal("id_modello_out_sel_ud");
    }

    public void setIdModelloOutSelUd(BigDecimal idModelloOutSelUd) {
        setObject("id_modello_out_sel_ud", idModelloOutSelUd);
    }

    public BigDecimal getIdModelloTipoSerie() {
        return getBigDecimal("id_modello_tipo_serie");
    }

    public void setIdModelloTipoSerie(BigDecimal idModelloTipoSerie) {
        setObject("id_modello_tipo_serie", idModelloTipoSerie);
    }

    public String getTiOut() {
        return getString("ti_out");
    }

    public void setTiOut(String tiOut) {
        setObject("ti_out", tiOut);
    }

    public String getDlFormatoOut() {
        return getString("dl_formato_out");
    }

    public void setDlFormatoOut(String dlFormatoOut) {
        setObject("dl_formato_out", dlFormatoOut);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecModelloOutSelUd entity = (DecModelloOutSelUd) obj;

        this.setIdModelloOutSelUd(
                entity.getIdModelloOutSelUd() == null ? null : BigDecimal.valueOf(entity.getIdModelloOutSelUd()));

        if (entity.getDecModelloTipoSerie() != null) {
            this.setIdModelloTipoSerie(new BigDecimal(entity.getDecModelloTipoSerie().getIdModelloTipoSerie()));
        }

        this.setTiOut(entity.getTiOut());
        this.setDlFormatoOut(entity.getDlFormatoOut());
    }

    @Override
    public DecModelloOutSelUd rowBeanToEntity() {
        DecModelloOutSelUd entity = new DecModelloOutSelUd();
        if (this.getIdModelloOutSelUd() != null) {
            entity.setIdModelloOutSelUd(this.getIdModelloOutSelUd().longValue());
        }
        if (this.getIdModelloTipoSerie() != null) {
            if (entity.getDecModelloTipoSerie() == null) {
                entity.setDecModelloTipoSerie(new DecModelloTipoSerie());
            }
            entity.getDecModelloTipoSerie().setIdModelloTipoSerie(this.getIdModelloTipoSerie().longValue());
        }
        entity.setTiOut(this.getTiOut());
        entity.setDlFormatoOut(this.getDlFormatoOut());
        return entity;
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
