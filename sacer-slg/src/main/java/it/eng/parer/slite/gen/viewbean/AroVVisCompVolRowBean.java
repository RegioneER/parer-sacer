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

import it.eng.parer.viewEntity.AroVVisCompVol;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_V_Vis_Comp_Vol
 *
 */
public class AroVVisCompVolRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 17 July 2015 11:12" )
     */
    private static final long serialVersionUID = 1L;

    public static AroVVisCompVolTableDescriptor TABLE_DESCRIPTOR = new AroVVisCompVolTableDescriptor();

    public AroVVisCompVolRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdCompDoc() {
        return getBigDecimal("id_comp_doc");
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        setObject("id_comp_doc", idCompDoc);
    }

    public BigDecimal getIdVolumeConserv() {
        return getBigDecimal("id_volume_conserv");
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
        setObject("id_volume_conserv", idVolumeConserv);
    }

    public String getNmVolumeConserv() {
        return getString("nm_volume_conserv");
    }

    public void setNmVolumeConserv(String nmVolumeConserv) {
        setObject("nm_volume_conserv", nmVolumeConserv);
    }

    public String getTiStatoVolumeConserv() {
        return getString("ti_stato_volume_conserv");
    }

    public void setTiStatoVolumeConserv(String tiStatoVolumeConserv) {
        setObject("ti_stato_volume_conserv", tiStatoVolumeConserv);
    }

    public Timestamp getDtChiusVolume() {
        return getTimestamp("dt_chius_volume");
    }

    public void setDtChiusVolume(Timestamp dtChiusVolume) {
        setObject("dt_chius_volume", dtChiusVolume);
    }

    public String getTiEsitoVerifFirmeChius() {
        return getString("ti_esito_verif_firme_chius");
    }

    public void setTiEsitoVerifFirmeChius(String tiEsitoVerifFirmeChius) {
        setObject("ti_esito_verif_firme_chius", tiEsitoVerifFirmeChius);
    }

    public String getDsEsitoVerifFirmeChius() {
        return getString("ds_esito_verif_firme_chius");
    }

    public void setDsEsitoVerifFirmeChius(String dsEsitoVerifFirmeChius) {
        setObject("ds_esito_verif_firme_chius", dsEsitoVerifFirmeChius);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroVVisCompVol entity = (AroVVisCompVol) obj;
        this.setIdCompDoc(entity.getIdCompDoc());
        this.setIdVolumeConserv(entity.getIdVolumeConserv());
        this.setNmVolumeConserv(entity.getNmVolumeConserv());
        this.setTiStatoVolumeConserv(entity.getTiStatoVolumeConserv());
        if (entity.getDtChiusVolume() != null) {
            this.setDtChiusVolume(new Timestamp(entity.getDtChiusVolume().getTime()));
        }
        this.setTiEsitoVerifFirmeChius(entity.getTiEsitoVerifFirmeChius());
        this.setDsEsitoVerifFirmeChius(entity.getDsEsitoVerifFirmeChius());
    }

    @Override
    public AroVVisCompVol rowBeanToEntity() {
        AroVVisCompVol entity = new AroVVisCompVol();
        entity.setIdCompDoc(this.getIdCompDoc());
        entity.setIdVolumeConserv(this.getIdVolumeConserv());
        entity.setNmVolumeConserv(this.getNmVolumeConserv());
        entity.setTiStatoVolumeConserv(this.getTiStatoVolumeConserv());
        entity.setDtChiusVolume(this.getDtChiusVolume());
        entity.setTiEsitoVerifFirmeChius(this.getTiEsitoVerifFirmeChius());
        entity.setDsEsitoVerifFirmeChius(this.getDsEsitoVerifFirmeChius());
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
