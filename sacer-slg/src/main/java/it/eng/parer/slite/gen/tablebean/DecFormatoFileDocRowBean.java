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

import it.eng.parer.entity.DecFormatoFileDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Formato_File_Doc
 *
 */
public class DecFormatoFileDocRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static DecFormatoFileDocTableDescriptor TABLE_DESCRIPTOR = new DecFormatoFileDocTableDescriptor();

    public DecFormatoFileDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdFormatoFileDoc() {
        return getBigDecimal("id_formato_file_doc");
    }

    public void setIdFormatoFileDoc(BigDecimal idFormatoFileDoc) {
        setObject("id_formato_file_doc", idFormatoFileDoc);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getNmFormatoFileDoc() {
        return getString("nm_formato_file_doc");
    }

    public void setNmFormatoFileDoc(String nmFormatoFileDoc) {
        setObject("nm_formato_file_doc", nmFormatoFileDoc);
    }

    public String getDsFormatoFileDoc() {
        return getString("ds_formato_file_doc");
    }

    public void setDsFormatoFileDoc(String dsFormatoFileDoc) {
        setObject("ds_formato_file_doc", dsFormatoFileDoc);
    }

    public String getCdVersione() {
        return getString("cd_versione");
    }

    public void setCdVersione(String cdVersione) {
        setObject("cd_versione", cdVersione);
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
        DecFormatoFileDoc entity = (DecFormatoFileDoc) obj;

        this.setIdFormatoFileDoc(
                entity.getIdFormatoFileDoc() == null ? null : BigDecimal.valueOf(entity.getIdFormatoFileDoc()));

        if (entity.getOrgStrut() != null) {
            this.setIdStrut(new BigDecimal(entity.getOrgStrut().getIdStrut()));
        }

        this.setNmFormatoFileDoc(entity.getNmFormatoFileDoc());
        this.setDsFormatoFileDoc(entity.getDsFormatoFileDoc());
        this.setCdVersione(entity.getCdVersione());
        if (entity.getDtIstituz() != null) {
            this.setDtIstituz(new Timestamp(entity.getDtIstituz().getTime()));
        }
        if (entity.getDtSoppres() != null) {
            this.setDtSoppres(new Timestamp(entity.getDtSoppres().getTime()));
        }
    }

    @Override
    public DecFormatoFileDoc rowBeanToEntity() {
        DecFormatoFileDoc entity = new DecFormatoFileDoc();
        if (this.getIdFormatoFileDoc() != null) {
            entity.setIdFormatoFileDoc(this.getIdFormatoFileDoc().longValue());
        }
        if (this.getIdStrut() != null) {
            if (entity.getOrgStrut() == null) {
                entity.setOrgStrut(new OrgStrut());
            }
            entity.getOrgStrut().setIdStrut(this.getIdStrut().longValue());
        }
        entity.setNmFormatoFileDoc(this.getNmFormatoFileDoc());
        entity.setDsFormatoFileDoc(this.getDsFormatoFileDoc());
        entity.setCdVersione(this.getCdVersione());
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
