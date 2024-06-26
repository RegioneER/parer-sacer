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

package it.eng.parer.slite.gen.viewbean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import it.eng.parer.viewEntity.AroVLisItemRichAnnvrs;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_V_Lis_Item_Rich_Annvrs
 *
 */
public class AroVLisItemRichAnnvrsRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 25 October 2018 17:30" )
     */
    private static final long serialVersionUID = 1L;

    public static AroVLisItemRichAnnvrsTableDescriptor TABLE_DESCRIPTOR = new AroVLisItemRichAnnvrsTableDescriptor();

    public AroVLisItemRichAnnvrsRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdItemRichAnnulVers() {
        return getBigDecimal("id_item_rich_annul_vers");
    }

    public void setIdItemRichAnnulVers(BigDecimal idItemRichAnnulVers) {
        setObject("id_item_rich_annul_vers", idItemRichAnnulVers);
    }

    public BigDecimal getIdRichAnnulVers() {
        return getBigDecimal("id_rich_annul_vers");
    }

    public void setIdRichAnnulVers(BigDecimal idRichAnnulVers) {
        setObject("id_rich_annul_vers", idRichAnnulVers);
    }

    public BigDecimal getPgItemRichAnnulVers() {
        return getBigDecimal("pg_item_rich_annul_vers");
    }

    public void setPgItemRichAnnulVers(BigDecimal pgItemRichAnnulVers) {
        setObject("pg_item_rich_annul_vers", pgItemRichAnnulVers);
    }

    public String getDsKeyItem() {
        return getString("ds_key_item");
    }

    public void setDsKeyItem(String dsKeyItem) {
        setObject("ds_key_item", dsKeyItem);
    }

    public String getTiStatoItem() {
        return getString("ti_stato_item");
    }

    public void setTiStatoItem(String tiStatoItem) {
        setObject("ti_stato_item", tiStatoItem);
    }

    public Timestamp getDtCreazione() {
        return getTimestamp("dt_creazione");
    }

    public void setDtCreazione(Timestamp dtCreazione) {
        setObject("dt_creazione", dtCreazione);
    }

    public BigDecimal getIdUnitaDoc() {
        return getBigDecimal("id_unita_doc");
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        setObject("id_unita_doc", idUnitaDoc);
    }

    public String getDsListaErr() {
        return getString("ds_lista_err");
    }

    public void setDsListaErr(String dsListaErr) {
        setObject("ds_lista_err", dsListaErr);
    }

    public String getTiItemRichAnnulVers() {
        return getString("ti_item_rich_annul_vers");
    }

    public void setTiItemRichAnnulVers(String tiItemRichAnnulVers) {
        setObject("ti_item_rich_annul_vers", tiItemRichAnnulVers);
    }

    public String getCdRegistroKeyUnitaDoc() {
        return getString("cd_registro_key_unita_doc");
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        setObject("cd_registro_key_unita_doc", cdRegistroKeyUnitaDoc);
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return getBigDecimal("aa_key_unita_doc");
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        setObject("aa_key_unita_doc", aaKeyUnitaDoc);
    }

    public String getCdKeyUnitaDoc() {
        return getString("cd_key_unita_doc");
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        setObject("cd_key_unita_doc", cdKeyUnitaDoc);
    }

    public BigDecimal getAaFascicolo() {
        return getBigDecimal("aa_fascicolo");
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        setObject("aa_fascicolo", aaFascicolo);
    }

    public String getCdKeyFascicolo() {
        return getString("cd_key_fascicolo");
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        setObject("cd_key_fascicolo", cdKeyFascicolo);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroVLisItemRichAnnvrs entity = (AroVLisItemRichAnnvrs) obj;
        this.setIdItemRichAnnulVers(entity.getIdItemRichAnnulVers());
        this.setIdRichAnnulVers(entity.getIdRichAnnulVers());
        this.setPgItemRichAnnulVers(entity.getPgItemRichAnnulVers());
        this.setDsKeyItem(entity.getDsKeyItem());
        this.setTiStatoItem(entity.getTiStatoItem());
        if (entity.getDtCreazione() != null) {
            this.setDtCreazione(new Timestamp(entity.getDtCreazione().getTime()));
        }
        this.setIdUnitaDoc(entity.getIdUnitaDoc());
        this.setDsListaErr(entity.getDsListaErr());
        this.setTiItemRichAnnulVers(entity.getTiItemRichAnnulVers());
        this.setCdRegistroKeyUnitaDoc(entity.getCdRegistroKeyUnitaDoc());
        this.setAaKeyUnitaDoc(entity.getAaKeyUnitaDoc());
        this.setCdKeyUnitaDoc(entity.getCdKeyUnitaDoc());
        this.setAaFascicolo(entity.getAaFascicolo());
        this.setCdKeyFascicolo(entity.getCdKeyFascicolo());
    }

    @Override
    public AroVLisItemRichAnnvrs rowBeanToEntity() {
        AroVLisItemRichAnnvrs entity = new AroVLisItemRichAnnvrs();
        entity.setIdItemRichAnnulVers(this.getIdItemRichAnnulVers());
        entity.setIdRichAnnulVers(this.getIdRichAnnulVers());
        entity.setPgItemRichAnnulVers(this.getPgItemRichAnnulVers());
        entity.setDsKeyItem(this.getDsKeyItem());
        entity.setTiStatoItem(this.getTiStatoItem());
        entity.setDtCreazione(this.getDtCreazione());
        entity.setIdUnitaDoc(this.getIdUnitaDoc());
        entity.setDsListaErr(this.getDsListaErr());
        entity.setTiItemRichAnnulVers(this.getTiItemRichAnnulVers());
        entity.setCdRegistroKeyUnitaDoc(this.getCdRegistroKeyUnitaDoc());
        entity.setAaKeyUnitaDoc(this.getAaKeyUnitaDoc());
        entity.setCdKeyUnitaDoc(this.getCdKeyUnitaDoc());
        entity.setAaFascicolo(this.getAaFascicolo());
        entity.setCdKeyFascicolo(this.getCdKeyFascicolo());
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
