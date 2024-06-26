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
import java.sql.Timestamp;

import it.eng.parer.entity.DecTipoStrutDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Tipo_Strut_Doc
 *
 */
public class DecTipoStrutDocRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static DecTipoStrutDocTableDescriptor TABLE_DESCRIPTOR = new DecTipoStrutDocTableDescriptor();

    public DecTipoStrutDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoStrutDoc() {
        return getBigDecimal("id_tipo_strut_doc");
    }

    public void setIdTipoStrutDoc(BigDecimal idTipoStrutDoc) {
        setObject("id_tipo_strut_doc", idTipoStrutDoc);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getNmTipoStrutDoc() {
        return getString("nm_tipo_strut_doc");
    }

    public void setNmTipoStrutDoc(String nmTipoStrutDoc) {
        setObject("nm_tipo_strut_doc", nmTipoStrutDoc);
    }

    public String getDsTipoStrutDoc() {
        return getString("ds_tipo_strut_doc");
    }

    public void setDsTipoStrutDoc(String dsTipoStrutDoc) {
        setObject("ds_tipo_strut_doc", dsTipoStrutDoc);
    }

    public Timestamp getDtIstituz() {
        return getTimestamp("dt_istituz");
    }

    public void setDtIstituz(Timestamp dtIstituz) {
        setObject("dt_istituz", dtIstituz);
    }

    public Timestamp getDtSoppres() {
        return getTimestamp("dt_soppres");
    }

    public void setDtSoppres(Timestamp dtSoppres) {
        setObject("dt_soppres", dtSoppres);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecTipoStrutDoc entity = (DecTipoStrutDoc) obj;

        this.setIdTipoStrutDoc(
                entity.getIdTipoStrutDoc() == null ? null : BigDecimal.valueOf(entity.getIdTipoStrutDoc()));

        if (entity.getOrgStrut() != null) {
            this.setIdStrut(new BigDecimal(entity.getOrgStrut().getIdStrut()));
        }

        this.setNmTipoStrutDoc(entity.getNmTipoStrutDoc());
        this.setDsTipoStrutDoc(entity.getDsTipoStrutDoc());
        if (entity.getDtIstituz() != null) {
            this.setDtIstituz(new Timestamp(entity.getDtIstituz().getTime()));
        }
        if (entity.getDtSoppres() != null) {
            this.setDtSoppres(new Timestamp(entity.getDtSoppres().getTime()));
        }
    }

    @Override
    public DecTipoStrutDoc rowBeanToEntity() {
        DecTipoStrutDoc entity = new DecTipoStrutDoc();
        if (this.getIdTipoStrutDoc() != null) {
            entity.setIdTipoStrutDoc(this.getIdTipoStrutDoc().longValue());
        }
        if (this.getIdStrut() != null) {
            if (entity.getOrgStrut() == null) {
                entity.setOrgStrut(new OrgStrut());
            }
            entity.getOrgStrut().setIdStrut(this.getIdStrut().longValue());
        }
        entity.setNmTipoStrutDoc(this.getNmTipoStrutDoc());
        entity.setDsTipoStrutDoc(this.getDsTipoStrutDoc());
        entity.setDtIstituz(this.getDtIstituz());
        entity.setDtSoppres(this.getDtSoppres());
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
