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

import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.MonTipoUnitaDocUserVers;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Mon_Tipo_Unita_Doc_User_Vers
 *
 */
public class MonTipoUnitaDocUserVersRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 27 October 2016 11:39" )
     */
    private static final long serialVersionUID = 1L;

    public static MonTipoUnitaDocUserVersTableDescriptor TABLE_DESCRIPTOR = new MonTipoUnitaDocUserVersTableDescriptor();

    public MonTipoUnitaDocUserVersRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoUnitaDocUserVers() {
        return getBigDecimal("id_tipo_unita_doc_user_vers");
    }

    public void setIdTipoUnitaDocUserVers(BigDecimal idTipoUnitaDocUserVers) {
        setObject("id_tipo_unita_doc_user_vers", idTipoUnitaDocUserVers);
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return getBigDecimal("id_tipo_unita_doc");
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        setObject("id_tipo_unita_doc", idTipoUnitaDoc);
    }

    public BigDecimal getIdUserIam() {
        return getBigDecimal("id_user_iam");
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        setObject("id_user_iam", idUserIam);
    }

    public Timestamp getDtRifConta() {
        return getTimestamp("dt_rif_conta");
    }

    public void setDtRifConta(Timestamp dtRifConta) {
        setObject("dt_rif_conta", dtRifConta);
    }

    public BigDecimal getNiUnitaDocVers() {
        return getBigDecimal("ni_unita_doc_vers");
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
        setObject("ni_unita_doc_vers", niUnitaDocVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        MonTipoUnitaDocUserVers entity = (MonTipoUnitaDocUserVers) obj;

        this.setIdTipoUnitaDocUserVers(entity.getIdTipoUnitaDocUserVers() == null ? null
                : BigDecimal.valueOf(entity.getIdTipoUnitaDocUserVers()));

        if (entity.getDecTipoUnitaDoc() != null) {
            this.setIdTipoUnitaDoc(new BigDecimal(entity.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
        }

        if (entity.getIamUser() != null) {
            this.setIdUserIam(new BigDecimal(entity.getIamUser().getIdUserIam()));
        }

        if (entity.getDtRifConta() != null) {
            this.setDtRifConta(new Timestamp(entity.getDtRifConta().getTime()));
        }
        this.setNiUnitaDocVers(entity.getNiUnitaDocVers());
    }

    @Override
    public MonTipoUnitaDocUserVers rowBeanToEntity() {
        MonTipoUnitaDocUserVers entity = new MonTipoUnitaDocUserVers();
        if (this.getIdTipoUnitaDocUserVers() != null) {
            entity.setIdTipoUnitaDocUserVers(this.getIdTipoUnitaDocUserVers().longValue());
        }
        if (this.getIdTipoUnitaDoc() != null) {
            if (entity.getDecTipoUnitaDoc() == null) {
                entity.setDecTipoUnitaDoc(new DecTipoUnitaDoc());
            }
            entity.getDecTipoUnitaDoc().setIdTipoUnitaDoc(this.getIdTipoUnitaDoc().longValue());
        }
        if (this.getIdUserIam() != null) {
            if (entity.getIamUser() == null) {
                entity.setIamUser(new IamUser());
            }
            entity.getIamUser().setIdUserIam(this.getIdUserIam().longValue());
        }
        entity.setDtRifConta(this.getDtRifConta());
        entity.setNiUnitaDocVers(this.getNiUnitaDocVers());
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
