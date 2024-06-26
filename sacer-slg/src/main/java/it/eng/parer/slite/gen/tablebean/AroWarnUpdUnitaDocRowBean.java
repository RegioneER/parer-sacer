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

import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroWarnUpdUnitaDoc;
import it.eng.parer.entity.DecControlloWs;
import it.eng.parer.entity.DecErrSacer;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_Warn_Upd_Unita_Doc
 *
 */
public class AroWarnUpdUnitaDocRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 19 November 2018 10:58" )
     */
    private static final long serialVersionUID = 1L;

    public static AroWarnUpdUnitaDocTableDescriptor TABLE_DESCRIPTOR = new AroWarnUpdUnitaDocTableDescriptor();

    public AroWarnUpdUnitaDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdWarnUpdUnitaDoc() {
        return getBigDecimal("id_warn_upd_unita_doc");
    }

    public void setIdWarnUpdUnitaDoc(BigDecimal idWarnUpdUnitaDoc) {
        setObject("id_warn_upd_unita_doc", idWarnUpdUnitaDoc);
    }

    public BigDecimal getIdUpdUnitaDoc() {
        return getBigDecimal("id_upd_unita_doc");
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
        setObject("id_upd_unita_doc", idUpdUnitaDoc);
    }

    public BigDecimal getPgWarn() {
        return getBigDecimal("pg_warn");
    }

    public void setPgWarn(BigDecimal pgWarn) {
        setObject("pg_warn", pgWarn);
    }

    public BigDecimal getIdErrSacer() {
        return getBigDecimal("id_err_sacer");
    }

    public void setIdErrSacer(BigDecimal idErrSacer) {
        setObject("id_err_sacer", idErrSacer);
    }

    public String getDsErr() {
        return getString("ds_err");
    }

    public void setDsErr(String dsErr) {
        setObject("ds_err", dsErr);
    }

    public BigDecimal getIdControlloWs() {
        return getBigDecimal("id_controllo_ws");
    }

    public void setIdControlloWs(BigDecimal idControlloWs) {
        setObject("id_controllo_ws", idControlloWs);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroWarnUpdUnitaDoc entity = (AroWarnUpdUnitaDoc) obj;
        this.setIdWarnUpdUnitaDoc(
                entity.getIdWarnUpdUnitaDoc() == null ? null : BigDecimal.valueOf(entity.getIdWarnUpdUnitaDoc()));

        if (entity.getAroUpdUnitaDoc() != null) {
            this.setIdUpdUnitaDoc(new BigDecimal(entity.getAroUpdUnitaDoc().getIdUpdUnitaDoc()));
        }

        this.setPgWarn(entity.getPgWarn());

        if (entity.getDecErrSacer() != null) {
            this.setIdErrSacer(new BigDecimal(entity.getDecErrSacer().getIdErrSacer()));
        }

        this.setDsErr(entity.getDsErr());

        if (entity.getDecControlloWs() != null) {
            this.setIdControlloWs(new BigDecimal(entity.getDecControlloWs().getIdControlloWs()));
        }

    }

    @Override
    public AroWarnUpdUnitaDoc rowBeanToEntity() {
        AroWarnUpdUnitaDoc entity = new AroWarnUpdUnitaDoc();
        if (this.getIdWarnUpdUnitaDoc() != null) {
            entity.setIdWarnUpdUnitaDoc(this.getIdWarnUpdUnitaDoc().longValue());
        }
        if (this.getIdUpdUnitaDoc() != null) {
            if (entity.getAroUpdUnitaDoc() == null) {
                entity.setAroUpdUnitaDoc(new AroUpdUnitaDoc());
            }
            entity.getAroUpdUnitaDoc().setIdUpdUnitaDoc(this.getIdUpdUnitaDoc().longValue());
        }
        entity.setPgWarn(this.getPgWarn());
        if (this.getIdErrSacer() != null) {
            if (entity.getDecErrSacer() == null) {
                entity.setDecErrSacer(new DecErrSacer());
            }
            entity.getDecErrSacer().setIdErrSacer(this.getIdErrSacer().longValue());
        }
        entity.setDsErr(this.getDsErr());
        if (this.getIdControlloWs() != null) {
            if (entity.getDecControlloWs() == null) {
                entity.setDecControlloWs(new DecControlloWs());
            }
            entity.getDecControlloWs().setIdControlloWs(this.getIdControlloWs().longValue());
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
