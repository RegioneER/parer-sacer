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

import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoSerieUd;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Tipo_Serie_Ud
 *
 */
public class DecTipoSerieUdRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 16 March 2015 14:47" )
     */
    private static final long serialVersionUID = 1L;

    public static DecTipoSerieUdTableDescriptor TABLE_DESCRIPTOR = new DecTipoSerieUdTableDescriptor();

    public DecTipoSerieUdRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoSerieUd() {
        return getBigDecimal("id_tipo_serie_ud");
    }

    public void setIdTipoSerieUd(BigDecimal idTipoSerieUd) {
        setObject("id_tipo_serie_ud", idTipoSerieUd);
    }

    public BigDecimal getIdTipoSerie() {
        return getBigDecimal("id_tipo_serie");
    }

    public void setIdTipoSerie(BigDecimal idTipoSerie) {
        setObject("id_tipo_serie", idTipoSerie);
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

    public String getFlSelUnitaDocAnnul() {
        return getString("fl_sel_unita_doc_annul");
    }

    public void setFlSelUnitaDocAnnul(String flSelUnitaDocAnnul) {
        setObject("fl_sel_unita_doc_annul", flSelUnitaDocAnnul);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecTipoSerieUd entity = (DecTipoSerieUd) obj;

        this.setIdTipoSerieUd(entity.getIdTipoSerieUd() == null ? null : BigDecimal.valueOf(entity.getIdTipoSerieUd()));

        if (entity.getDecTipoSerie() != null) {
            this.setIdTipoSerie(new BigDecimal(entity.getDecTipoSerie().getIdTipoSerie()));
        }

        if (entity.getDecRegistroUnitaDoc() != null) {
            this.setIdRegistroUnitaDoc(new BigDecimal(entity.getDecRegistroUnitaDoc().getIdRegistroUnitaDoc()));
        }

        if (entity.getDecTipoUnitaDoc() != null) {
            this.setIdTipoUnitaDoc(new BigDecimal(entity.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
        }

        this.setFlSelUnitaDocAnnul(entity.getFlSelUnitaDocAnnul());
    }

    @Override
    public DecTipoSerieUd rowBeanToEntity() {
        DecTipoSerieUd entity = new DecTipoSerieUd();
        if (this.getIdTipoSerieUd() != null) {
            entity.setIdTipoSerieUd(this.getIdTipoSerieUd().longValue());
        }
        if (this.getIdTipoSerie() != null) {
            if (entity.getDecTipoSerie() == null) {
                entity.setDecTipoSerie(new DecTipoSerie());
            }
            entity.getDecTipoSerie().setIdTipoSerie(this.getIdTipoSerie().longValue());
        }
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
        entity.setFlSelUnitaDocAnnul(this.getFlSelUnitaDocAnnul());
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
