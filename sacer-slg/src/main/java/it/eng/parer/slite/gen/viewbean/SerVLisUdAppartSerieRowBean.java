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

import it.eng.parer.viewEntity.SerVLisUdAppartSerie;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Ser_V_Lis_Ud_Appart_Serie
 *
 */
public class SerVLisUdAppartSerieRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 29 October 2015 16:09" )
     */
    private static final long serialVersionUID = 1L;

    public static SerVLisUdAppartSerieTableDescriptor TABLE_DESCRIPTOR = new SerVLisUdAppartSerieTableDescriptor();

    public SerVLisUdAppartSerieRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdUdAppartVerSerie() {
        return getBigDecimal("id_ud_appart_ver_serie");
    }

    public void setIdUdAppartVerSerie(BigDecimal idUdAppartVerSerie) {
        setObject("id_ud_appart_ver_serie", idUdAppartVerSerie);
    }

    public BigDecimal getIdContenutoVerSerie() {
        return getBigDecimal("id_contenuto_ver_serie");
    }

    public void setIdContenutoVerSerie(BigDecimal idContenutoVerSerie) {
        setObject("id_contenuto_ver_serie", idContenutoVerSerie);
    }

    public BigDecimal getIdUnitaDoc() {
        return getBigDecimal("id_unita_doc");
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        setObject("id_unita_doc", idUnitaDoc);
    }

    public String getCdUdSerie() {
        return getString("cd_ud_serie");
    }

    public void setCdUdSerie(String cdUdSerie) {
        setObject("cd_ud_serie", cdUdSerie);
    }

    public Timestamp getDtUdSerie() {
        return getTimestamp("dt_ud_serie");
    }

    public void setDtUdSerie(Timestamp dtUdSerie) {
        setObject("dt_ud_serie", dtUdSerie);
    }

    public String getInfoUdSerie() {
        return getString("info_ud_serie");
    }

    public void setInfoUdSerie(String infoUdSerie) {
        setObject("info_ud_serie", infoUdSerie);
    }

    public String getDsKeyOrdUdSerie() {
        return getString("ds_key_ord_ud_serie");
    }

    public void setDsKeyOrdUdSerie(String dsKeyOrdUdSerie) {
        setObject("ds_key_ord_ud_serie", dsKeyOrdUdSerie);
    }

    public BigDecimal getPgUdSerie() {
        return getBigDecimal("pg_ud_serie");
    }

    public void setPgUdSerie(BigDecimal pgUdSerie) {
        setObject("pg_ud_serie", pgUdSerie);
    }

    public BigDecimal getIdVolVerSerie() {
        return getBigDecimal("id_vol_ver_serie");
    }

    public void setIdVolVerSerie(BigDecimal idVolVerSerie) {
        setObject("id_vol_ver_serie", idVolVerSerie);
    }

    public String getCdKeyUnitaDoc() {
        return getString("cd_key_unita_doc");
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        setObject("cd_key_unita_doc", cdKeyUnitaDoc);
    }

    public String getFlUnitaDocAnnul() {
        return getString("fl_unita_doc_annul");
    }

    public void setFlUnitaDocAnnul(String flUnitaDocAnnul) {
        setObject("fl_unita_doc_annul", flUnitaDocAnnul);
    }

    public String getFlPresenteContenuto1() {
        return getString("fl_presente_contenuto_1");
    }

    public void setFlPresenteContenuto1(String fl_presente_contenuto_1) {
        setObject("fl_presente_contenuto_1", fl_presente_contenuto_1);
    }

    public String getFlPresenteContenuto2() {
        return getString("fl_presente_contenuto_2");
    }

    public void setFlPresenteContenuto2(String fl_presente_contenuto_2) {
        setObject("fl_presente_contenuto_2", fl_presente_contenuto_2);
    }

    public String getFlPresenteAltraSerie() {
        return getString("fl_presente_altra_serie");
    }

    public void setFlPresenteAltraSerie(String flPresenteAltraSerie) {
        setObject("fl_presente_altra_serie", flPresenteAltraSerie);
    }

    public String getTiStatoConservazione() {
        return getString("ti_stato_conservazione");
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        setObject("ti_stato_conservazione", tiStatoConservazione);
    }

    @Override
    public void entityToRowBean(Object obj) {
        SerVLisUdAppartSerie entity = (SerVLisUdAppartSerie) obj;
        this.setIdUdAppartVerSerie(entity.getIdUdAppartVerSerie());
        this.setIdContenutoVerSerie(entity.getIdContenutoVerSerie());
        this.setIdUnitaDoc(entity.getIdUnitaDoc());
        this.setCdUdSerie(entity.getCdUdSerie());
        if (entity.getDtUdSerie() != null) {
            this.setDtUdSerie(new Timestamp(entity.getDtUdSerie().getTime()));
        }
        this.setInfoUdSerie(entity.getInfoUdSerie());
        this.setDsKeyOrdUdSerie(entity.getDsKeyOrdUdSerie());
        this.setPgUdSerie(entity.getPgUdSerie());
        this.setIdVolVerSerie(entity.getIdVolVerSerie());
        this.setCdKeyUnitaDoc(entity.getCdKeyUnitaDoc());
        this.setFlUnitaDocAnnul(entity.getFlUnitaDocAnnul());
        this.setFlPresenteContenuto1(entity.getFlPresenteContenuto1());
        this.setFlPresenteContenuto2(entity.getFlPresenteContenuto2());
        this.setFlPresenteAltraSerie(entity.getFlPresenteAltraSerie());
        this.setTiStatoConservazione(entity.getTiStatoConservazione());
    }

    @Override
    public SerVLisUdAppartSerie rowBeanToEntity() {
        SerVLisUdAppartSerie entity = new SerVLisUdAppartSerie();
        entity.setIdUdAppartVerSerie(this.getIdUdAppartVerSerie());
        entity.setIdContenutoVerSerie(this.getIdContenutoVerSerie());
        entity.setIdUnitaDoc(this.getIdUnitaDoc());
        entity.setCdUdSerie(this.getCdUdSerie());
        entity.setDtUdSerie(this.getDtUdSerie());
        entity.setInfoUdSerie(this.getInfoUdSerie());
        entity.setDsKeyOrdUdSerie(this.getDsKeyOrdUdSerie());
        entity.setPgUdSerie(this.getPgUdSerie());
        entity.setIdVolVerSerie(this.getIdVolVerSerie());
        entity.setCdKeyUnitaDoc(this.getCdKeyUnitaDoc());
        entity.setFlUnitaDocAnnul(this.getFlUnitaDocAnnul());
        entity.setFlPresenteContenuto1(this.getFlPresenteContenuto1());
        entity.setFlPresenteContenuto2(this.getFlPresenteContenuto2());
        entity.setFlPresenteAltraSerie(this.getFlPresenteAltraSerie());
        entity.setTiStatoConservazione(this.getTiStatoConservazione());
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
