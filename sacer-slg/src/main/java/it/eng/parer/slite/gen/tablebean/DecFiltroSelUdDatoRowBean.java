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

import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecFiltroSelUdAttb;
import it.eng.parer.entity.DecFiltroSelUdDato;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Filtro_Sel_Ud_Dato
 *
 */
public class DecFiltroSelUdDatoRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 13 January 2015 09:49" )
     */
    private static final long serialVersionUID = 1L;

    public static DecFiltroSelUdDatoTableDescriptor TABLE_DESCRIPTOR = new DecFiltroSelUdDatoTableDescriptor();

    public DecFiltroSelUdDatoRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdFiltroSelUdDato() {
        return getBigDecimal("id_filtro_sel_ud_dato");
    }

    public void setIdFiltroSelUdDato(BigDecimal idFiltroSelUdDato) {
        setObject("id_filtro_sel_ud_dato", idFiltroSelUdDato);
    }

    public BigDecimal getIdFiltroSelUdAttb() {
        return getBigDecimal("id_filtro_sel_ud_attb");
    }

    public void setIdFiltroSelUdAttb(BigDecimal idFiltroSelUdAttb) {
        setObject("id_filtro_sel_ud_attb", idFiltroSelUdAttb);
    }

    public BigDecimal getIdAttribDatiSpec() {
        return getBigDecimal("id_attrib_dati_spec");
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        setObject("id_attrib_dati_spec", idAttribDatiSpec);
    }

    public String getTiEntitaSacer() {
        return getString("ti_entita_sacer");
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        setObject("ti_entita_sacer", tiEntitaSacer);
    }

    public String getNmTipoUnitaDoc() {
        return getString("nm_tipo_unita_doc");
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        setObject("nm_tipo_unita_doc", nmTipoUnitaDoc);
    }

    public String getNmTipoDoc() {
        return getString("nm_tipo_doc");
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        setObject("nm_tipo_doc", nmTipoDoc);
    }

    public String getDsListaVersioniXsd() {
        return getString("ds_lista_versioni_xsd");
    }

    public void setDsListaVersioniXsd(String dsListaVersioniXsd) {
        setObject("ds_lista_versioni_xsd", dsListaVersioniXsd);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecFiltroSelUdDato entity = (DecFiltroSelUdDato) obj;

        this.setIdFiltroSelUdDato(
                entity.getIdFiltroSelUdDato() == null ? null : BigDecimal.valueOf(entity.getIdFiltroSelUdDato()));

        if (entity.getDecFiltroSelUdAttb() != null) {
            this.setIdFiltroSelUdAttb(new BigDecimal(entity.getDecFiltroSelUdAttb().getIdFiltroSelUdAttb()));
        }

        if (entity.getDecAttribDatiSpec() != null) {
            this.setIdAttribDatiSpec(new BigDecimal(entity.getDecAttribDatiSpec().getIdAttribDatiSpec()));
        }

        this.setTiEntitaSacer(entity.getTiEntitaSacer());
        this.setNmTipoUnitaDoc(entity.getNmTipoUnitaDoc());
        this.setNmTipoDoc(entity.getNmTipoDoc());
        this.setDsListaVersioniXsd(entity.getDsListaVersioniXsd());
    }

    @Override
    public DecFiltroSelUdDato rowBeanToEntity() {
        DecFiltroSelUdDato entity = new DecFiltroSelUdDato();
        if (this.getIdFiltroSelUdDato() != null) {
            entity.setIdFiltroSelUdDato(this.getIdFiltroSelUdDato().longValue());
        }
        if (this.getIdFiltroSelUdAttb() != null) {
            if (entity.getDecFiltroSelUdAttb() == null) {
                entity.setDecFiltroSelUdAttb(new DecFiltroSelUdAttb());
            }
            entity.getDecFiltroSelUdAttb().setIdFiltroSelUdAttb(this.getIdFiltroSelUdAttb().longValue());
        }
        if (this.getIdAttribDatiSpec() != null) {
            if (entity.getDecAttribDatiSpec() == null) {
                entity.setDecAttribDatiSpec(new DecAttribDatiSpec());
            }
            entity.getDecAttribDatiSpec().setIdAttribDatiSpec(this.getIdAttribDatiSpec().longValue());
        }
        entity.setTiEntitaSacer(this.getTiEntitaSacer());
        entity.setNmTipoUnitaDoc(this.getNmTipoUnitaDoc());
        entity.setNmTipoDoc(this.getNmTipoDoc());
        entity.setDsListaVersioniXsd(this.getDsListaVersioniXsd());
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
