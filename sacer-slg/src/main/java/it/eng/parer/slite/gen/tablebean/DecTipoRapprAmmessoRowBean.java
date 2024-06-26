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

import it.eng.parer.entity.DecTipoCompDoc;
import it.eng.parer.entity.DecTipoRapprAmmesso;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Tipo_Rappr_Ammesso
 *
 */
public class DecTipoRapprAmmessoRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 14 July 2015 11:54" )
     */
    private static final long serialVersionUID = 1L;

    public static DecTipoRapprAmmessoTableDescriptor TABLE_DESCRIPTOR = new DecTipoRapprAmmessoTableDescriptor();

    public DecTipoRapprAmmessoRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoRapprAmmesso() {
        return getBigDecimal("id_tipo_rappr_ammesso");
    }

    public void setIdTipoRapprAmmesso(BigDecimal idTipoRapprAmmesso) {
        setObject("id_tipo_rappr_ammesso", idTipoRapprAmmesso);
    }

    public BigDecimal getIdTipoCompDoc() {
        return getBigDecimal("id_tipo_comp_doc");
    }

    public void setIdTipoCompDoc(BigDecimal idTipoCompDoc) {
        setObject("id_tipo_comp_doc", idTipoCompDoc);
    }

    public BigDecimal getIdTipoRapprComp() {
        return getBigDecimal("id_tipo_rappr_comp");
    }

    public void setIdTipoRapprComp(BigDecimal idTipoRapprComp) {
        setObject("id_tipo_rappr_comp", idTipoRapprComp);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecTipoRapprAmmesso entity = (DecTipoRapprAmmesso) obj;

        this.setIdTipoRapprAmmesso(
                entity.getIdTipoRapprAmmesso() == null ? null : BigDecimal.valueOf(entity.getIdTipoRapprAmmesso()));

        if (entity.getDecTipoCompDoc() != null) {
            this.setIdTipoCompDoc(new BigDecimal(entity.getDecTipoCompDoc().getIdTipoCompDoc()));
        }

        if (entity.getDecTipoRapprComp() != null) {
            this.setIdTipoRapprComp(new BigDecimal(entity.getDecTipoRapprComp().getIdTipoRapprComp()));
        }

    }

    @Override
    public DecTipoRapprAmmesso rowBeanToEntity() {
        DecTipoRapprAmmesso entity = new DecTipoRapprAmmesso();
        if (this.getIdTipoRapprAmmesso() != null) {
            entity.setIdTipoRapprAmmesso(this.getIdTipoRapprAmmesso().longValue());
        }
        if (this.getIdTipoCompDoc() != null) {
            if (entity.getDecTipoCompDoc() == null) {
                entity.setDecTipoCompDoc(new DecTipoCompDoc());
            }
            entity.getDecTipoCompDoc().setIdTipoCompDoc(this.getIdTipoCompDoc().longValue());
        }
        if (this.getIdTipoRapprComp() != null) {
            if (entity.getDecTipoRapprComp() == null) {
                entity.setDecTipoRapprComp(new DecTipoRapprComp());
            }
            entity.getDecTipoRapprComp().setIdTipoRapprComp(this.getIdTipoRapprComp().longValue());
        }
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
