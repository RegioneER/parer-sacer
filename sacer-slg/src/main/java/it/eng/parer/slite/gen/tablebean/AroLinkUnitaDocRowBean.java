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

import it.eng.parer.entity.AroLinkUnitaDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_Link_Unita_Doc
 *
 */
public class AroLinkUnitaDocRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static AroLinkUnitaDocTableDescriptor TABLE_DESCRIPTOR = new AroLinkUnitaDocTableDescriptor();

    public AroLinkUnitaDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdLinkUnitaDoc() {
        return getBigDecimal("id_link_unita_doc");
    }

    public void setIdLinkUnitaDoc(BigDecimal idLinkUnitaDoc) {
        setObject("id_link_unita_doc", idLinkUnitaDoc);
    }

    public BigDecimal getIdUnitaDoc() {
        return getBigDecimal("id_unita_doc");
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        setObject("id_unita_doc", idUnitaDoc);
    }

    public String getCdRegistroKeyUnitaDocLink() {
        return getString("cd_registro_key_unita_doc_link");
    }

    public void setCdRegistroKeyUnitaDocLink(String cdRegistroKeyUnitaDocLink) {
        setObject("cd_registro_key_unita_doc_link", cdRegistroKeyUnitaDocLink);
    }

    public BigDecimal getAaKeyUnitaDocLink() {
        return getBigDecimal("aa_key_unita_doc_link");
    }

    public void setAaKeyUnitaDocLink(BigDecimal aaKeyUnitaDocLink) {
        setObject("aa_key_unita_doc_link", aaKeyUnitaDocLink);
    }

    public String getCdKeyUnitaDocLink() {
        return getString("cd_key_unita_doc_link");
    }

    public void setCdKeyUnitaDocLink(String cdKeyUnitaDocLink) {
        setObject("cd_key_unita_doc_link", cdKeyUnitaDocLink);
    }

    public String getDsLinkUnitaDoc() {
        return getString("ds_link_unita_doc");
    }

    public void setDsLinkUnitaDoc(String dsLinkUnitaDoc) {
        setObject("ds_link_unita_doc", dsLinkUnitaDoc);
    }

    public BigDecimal getIdUnitaDocLink() {
        return getBigDecimal("id_unita_doc_link");
    }

    public void setIdUnitaDocLink(BigDecimal idUnitaDocLink) {
        setObject("id_unita_doc_link", idUnitaDocLink);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroLinkUnitaDoc entity = (AroLinkUnitaDoc) obj;
        this.setIdLinkUnitaDoc(
                entity.getIdLinkUnitaDoc() == null ? null : BigDecimal.valueOf(entity.getIdLinkUnitaDoc()));

        if (entity.getAroUnitaDoc() != null) {
            this.setIdUnitaDoc(new BigDecimal(entity.getAroUnitaDoc().getIdUnitaDoc()));
        }

        this.setCdRegistroKeyUnitaDocLink(entity.getCdRegistroKeyUnitaDocLink());
        this.setAaKeyUnitaDocLink(entity.getAaKeyUnitaDocLink());
        this.setCdKeyUnitaDocLink(entity.getCdKeyUnitaDocLink());
        this.setDsLinkUnitaDoc(entity.getDsLinkUnitaDoc());

        if (entity.getAroUnitaDocLink() != null) {
            this.setIdUnitaDocLink(new BigDecimal(entity.getAroUnitaDocLink().getIdUnitaDoc()));
        }

        this.setIdStrut(entity.getIdStrut());
    }

    @Override
    public AroLinkUnitaDoc rowBeanToEntity() {
        AroLinkUnitaDoc entity = new AroLinkUnitaDoc();
        if (this.getIdLinkUnitaDoc() != null) {
            entity.setIdLinkUnitaDoc(this.getIdLinkUnitaDoc().longValue());
        }
        if (this.getIdUnitaDoc() != null) {
            if (entity.getAroUnitaDoc() == null) {
                entity.setAroUnitaDoc(new AroUnitaDoc());
            }
            entity.getAroUnitaDoc().setIdUnitaDoc(this.getIdUnitaDoc().longValue());
        }
        entity.setCdRegistroKeyUnitaDocLink(this.getCdRegistroKeyUnitaDocLink());
        entity.setAaKeyUnitaDocLink(this.getAaKeyUnitaDocLink());
        entity.setCdKeyUnitaDocLink(this.getCdKeyUnitaDocLink());
        entity.setDsLinkUnitaDoc(this.getDsLinkUnitaDoc());
        if (this.getIdUnitaDocLink() != null) {
            if (entity.getAroUnitaDocLink() == null) {
                entity.setAroUnitaDocLink(new AroUnitaDoc());
            }
            entity.getAroUnitaDocLink().setIdUnitaDoc(this.getIdUnitaDocLink().longValue());
        }
        entity.setIdStrut(this.getIdStrut());
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
