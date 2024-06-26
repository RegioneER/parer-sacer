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

import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.MonContaUdDocComp;
import it.eng.parer.entity.OrgSubStrut;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_Conta_Ud_Doc_Comp
 *
 */
public class MonContaUdDocCompRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 12 December 2014 14:48" )
     */
    private static final long serialVersionUID = 1L;

    public static MonContaUdDocCompTableDescriptor TABLE_DESCRIPTOR = new MonContaUdDocCompTableDescriptor();

    public MonContaUdDocCompRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdContaUdDocComp() {
        return getBigDecimal("id_conta_ud_doc_comp");
    }

    public void setIdContaUdDocComp(BigDecimal idContaUdDocComp) {
        setObject("id_conta_ud_doc_comp", idContaUdDocComp);
    }

    public Timestamp getDtRifConta() {
        return getTimestamp("dt_rif_conta");
    }

    public void setDtRifConta(Timestamp dtRifConta) {
        setObject("dt_rif_conta", dtRifConta);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public BigDecimal getIdSubStrut() {
        return getBigDecimal("id_sub_strut");
    }

    public void setIdSubStrut(BigDecimal idSubStrut) {
        setObject("id_sub_strut", idSubStrut);
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return getBigDecimal("aa_key_unita_doc");
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        setObject("aa_key_unita_doc", aaKeyUnitaDoc);
    }

    public BigDecimal getIdRegistroUnitaDoc() {
        return getBigDecimal("id_registro_unita_doc");
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        setObject("id_registro_unita_doc", idRegistroUnitaDoc);
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return getBigDecimal("id_tipo_unita_doc");
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        setObject("id_tipo_unita_doc", idTipoUnitaDoc);
    }

    public BigDecimal getIdTipoDocPrinc() {
        return getBigDecimal("id_tipo_doc_princ");
    }

    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
        setObject("id_tipo_doc_princ", idTipoDocPrinc);
    }

    public BigDecimal getNiUnitaDoc() {
        return getBigDecimal("ni_unita_doc");
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
        setObject("ni_unita_doc", niUnitaDoc);
    }

    public BigDecimal getNiDoc() {
        return getBigDecimal("ni_doc");
    }

    public void setNiDoc(BigDecimal niDoc) {
        setObject("ni_doc", niDoc);
    }

    public BigDecimal getNiComp() {
        return getBigDecimal("ni_comp");
    }

    public void setNiComp(BigDecimal niComp) {
        setObject("ni_comp", niComp);
    }

    public BigDecimal getNiSize() {
        return getBigDecimal("ni_size");
    }

    public void setNiSize(BigDecimal niSize) {
        setObject("ni_size", niSize);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonContaUdDocComp entity = (MonContaUdDocComp) obj;

        this.setIdContaUdDocComp(
                entity.getIdContaUdDocComp() == null ? null : BigDecimal.valueOf(entity.getIdContaUdDocComp()));
        if (entity.getDtRifConta() != null) {
            this.setDtRifConta(new Timestamp(entity.getDtRifConta().getTime()));
        }
        this.setIdStrut(entity.getIdStrut());

        if (entity.getOrgSubStrut() != null) {
            this.setIdSubStrut(new BigDecimal(entity.getOrgSubStrut().getIdSubStrut()));
        }

        this.setAaKeyUnitaDoc(entity.getAaKeyUnitaDoc());

        if (entity.getDecRegistroUnitaDoc() != null) {
            this.setIdRegistroUnitaDoc(new BigDecimal(entity.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()));
        }

        if (entity.getDecTipoUnitaDoc() != null) {
            this.setIdTipoUnitaDoc(new BigDecimal(entity.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
        }

        if (entity.getDecTipoDoc() != null) {
            this.setIdTipoDocPrinc(new BigDecimal(entity.getDecTipoDoc().getIdTipoDoc()));
        }

    }

    @Override
    public MonContaUdDocComp rowBeanToEntity() {
        MonContaUdDocComp entity = new MonContaUdDocComp();
        if (this.getIdContaUdDocComp() != null) {
            entity.setIdContaUdDocComp(this.getIdContaUdDocComp().longValue());
        }
        entity.setDtRifConta(this.getDtRifConta());
        entity.setIdStrut(this.getIdStrut());
        if (this.getIdSubStrut() != null) {
            if (entity.getOrgSubStrut() == null) {
                entity.setOrgSubStrut(new OrgSubStrut());
            }
            entity.getOrgSubStrut().setIdSubStrut(this.getIdSubStrut().longValue());
        }
        entity.setAaKeyUnitaDoc(this.getAaKeyUnitaDoc());
        if (this.getIdRegistroUnitaDoc() != null) {
            if (entity.getDecRegistroUnitaDoc() == null) {
                entity.setDecRegistroUnitaDoc(new DecRegistroUnitaDoc());
            }
            entity.getDecRegistroUnitaDoc().setIdRegistroUnitaDoc(this.getIdRegistroUnitaDoc().longValue());
        }
        if (this.getIdTipoUnitaDoc() != null) {
            if (entity.getDecTipoUnitaDoc() == null) {
                entity.setDecTipoUnitaDoc(new DecTipoUnitaDoc());
            }
            entity.getDecTipoUnitaDoc().setIdTipoUnitaDoc(this.getIdTipoUnitaDoc().longValue());
        }
        if (this.getIdTipoDocPrinc() != null) {
            if (entity.getDecTipoDoc() == null) {
                entity.setDecTipoDoc(new DecTipoDoc());
            }
            entity.getDecTipoDoc().setIdTipoDoc(this.getIdTipoDocPrinc().longValue());
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
