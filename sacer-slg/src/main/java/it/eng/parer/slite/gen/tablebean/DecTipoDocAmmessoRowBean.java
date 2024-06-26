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

import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoDocAmmesso;
import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Tipo_Doc_Ammesso
 *
 */
public class DecTipoDocAmmessoRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static DecTipoDocAmmessoTableDescriptor TABLE_DESCRIPTOR = new DecTipoDocAmmessoTableDescriptor();

    public DecTipoDocAmmessoRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoDocAmmesso() {
        return getBigDecimal("id_tipo_doc_ammesso");
    }

    public void setIdTipoDocAmmesso(BigDecimal idTipoDocAmmesso) {
        setObject("id_tipo_doc_ammesso", idTipoDocAmmesso);
    }

    public BigDecimal getIdTipoStrutUnitaDoc() {
        return getBigDecimal("id_tipo_strut_unita_doc");
    }

    public void setIdTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        setObject("id_tipo_strut_unita_doc", idTipoStrutUnitaDoc);
    }

    public BigDecimal getIdTipoDoc() {
        return getBigDecimal("id_tipo_doc");
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        setObject("id_tipo_doc", idTipoDoc);
    }

    public String getTiDoc() {
        return getString("ti_doc");
    }

    public void setTiDoc(String tiDoc) {
        setObject("ti_doc", tiDoc);
    }

    public String getFlObbl() {
        return getString("fl_obbl");
    }

    public void setFlObbl(String flObbl) {
        setObject("fl_obbl", flObbl);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecTipoDocAmmesso entity = (DecTipoDocAmmesso) obj;

        this.setIdTipoDocAmmesso(
                entity.getIdTipoDocAmmesso() == null ? null : BigDecimal.valueOf(entity.getIdTipoDocAmmesso()));

        if (entity.getDecTipoStrutUnitaDoc() != null) {
            this.setIdTipoStrutUnitaDoc(new BigDecimal(entity.getDecTipoStrutUnitaDoc().getIdTipoStrutUnitaDoc()));
        }

        if (entity.getDecTipoDoc() != null) {
            this.setIdTipoDoc(new BigDecimal(entity.getDecTipoDoc().getIdTipoDoc()));
        }

        this.setTiDoc(entity.getTiDoc());
        this.setFlObbl(entity.getFlObbl());
    }

    @Override
    public DecTipoDocAmmesso rowBeanToEntity() {
        DecTipoDocAmmesso entity = new DecTipoDocAmmesso();
        if (this.getIdTipoDocAmmesso() != null) {
            entity.setIdTipoDocAmmesso(this.getIdTipoDocAmmesso().longValue());
        }
        if (this.getIdTipoStrutUnitaDoc() != null) {
            if (entity.getDecTipoStrutUnitaDoc() == null) {
                entity.setDecTipoStrutUnitaDoc(new DecTipoStrutUnitaDoc());
            }
            entity.getDecTipoStrutUnitaDoc().setIdTipoStrutUnitaDoc(this.getIdTipoStrutUnitaDoc().longValue());
        }
        if (this.getIdTipoDoc() != null) {
            if (entity.getDecTipoDoc() == null) {
                entity.setDecTipoDoc(new DecTipoDoc());
            }
            entity.getDecTipoDoc().setIdTipoDoc(this.getIdTipoDoc().longValue());
        }
        entity.setTiDoc(this.getTiDoc());
        entity.setFlObbl(this.getFlObbl());
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
