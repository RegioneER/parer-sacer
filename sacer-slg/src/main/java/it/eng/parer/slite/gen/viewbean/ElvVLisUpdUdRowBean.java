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

import it.eng.parer.viewEntity.ElvVLisUpdUd;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Elv_V_Lis_Upd_Ud
 *
 */
public class ElvVLisUpdUdRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 19 October 2018 11:20" )
     */
    private static final long serialVersionUID = 1L;

    public static ElvVLisUpdUdTableDescriptor TABLE_DESCRIPTOR = new ElvVLisUpdUdTableDescriptor();

    public ElvVLisUpdUdRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdElencoVers() {
        return getBigDecimal("id_elenco_vers");
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        setObject("id_elenco_vers", idElencoVers);
    }

    public BigDecimal getIdUnitaDoc() {
        return getBigDecimal("id_unita_doc");
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        setObject("id_unita_doc", idUnitaDoc);
    }

    public BigDecimal getIdStrutUnitaDoc() {
        return getBigDecimal("id_strut_unita_doc");
    }

    public void setIdStrutUnitaDoc(BigDecimal idStrutUnitaDoc) {
        setObject("id_strut_unita_doc", idStrutUnitaDoc);
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

    public String getDsKeyOrd() {
        return getString("ds_key_ord");
    }

    public void setDsKeyOrd(String dsKeyOrd) {
        setObject("ds_key_ord", dsKeyOrd);
    }

    public String getTiStatoUpdElencoVers() {
        return getString("ti_stato_upd_elenco_vers");
    }

    public void setTiStatoUpdElencoVers(String tiStatoUpdElencoVers) {
        setObject("ti_stato_upd_elenco_vers", tiStatoUpdElencoVers);
    }

    public String getTiStatoConservazione() {
        return getString("ti_stato_conservazione");
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        setObject("ti_stato_conservazione", tiStatoConservazione);
    }

    public BigDecimal getIdUpdUnitaDoc() {
        return getBigDecimal("id_upd_unita_doc");
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
        setObject("id_upd_unita_doc", idUpdUnitaDoc);
    }

    public BigDecimal getPgUpdUnitaDoc() {
        return getBigDecimal("pg_upd_unita_doc");
    }

    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
        setObject("pg_upd_unita_doc", pgUpdUnitaDoc);
    }

    public Timestamp getTsIniSes() {
        return getTimestamp("ts_ini_ses");
    }

    public void setTsIniSes(Timestamp tsIniSes) {
        setObject("ts_ini_ses", tsIniSes);
    }

    public String getDsUrnUpdUnitaDoc() {
        return getString("ds_urn_upd_unita_doc");
    }

    public void setDsUrnUpdUnitaDoc(String dsUrnUpdUnitaDoc) {
        setObject("ds_urn_upd_unita_doc", dsUrnUpdUnitaDoc);
    }

    @Override
    public void entityToRowBean(Object obj) {
        ElvVLisUpdUd entity = (ElvVLisUpdUd) obj;
        this.setIdElencoVers(entity.getIdElencoVers());
        this.setIdUnitaDoc(entity.getIdUnitaDoc());
        this.setIdStrutUnitaDoc(entity.getIdStrutUnitaDoc());
        this.setCdRegistroKeyUnitaDoc(entity.getCdRegistroKeyUnitaDoc());
        this.setAaKeyUnitaDoc(entity.getAaKeyUnitaDoc());
        this.setCdKeyUnitaDoc(entity.getCdKeyUnitaDoc());
        this.setDsKeyOrd(entity.getDsKeyOrd());
        this.setTiStatoUpdElencoVers(entity.getTiStatoUpdElencoVers());
        this.setTiStatoConservazione(entity.getTiStatoConservazione());
        this.setIdUpdUnitaDoc(entity.getIdUpdUnitaDoc());
        this.setPgUpdUnitaDoc(entity.getPgUpdUnitaDoc());
        if (entity.getTsIniSes() != null) {
            this.setTsIniSes(new Timestamp(entity.getTsIniSes().getTime()));
        }
        this.setDsUrnUpdUnitaDoc(entity.getDsUrnUpdUnitaDoc());
    }

    @Override
    public ElvVLisUpdUd rowBeanToEntity() {
        ElvVLisUpdUd entity = new ElvVLisUpdUd();
        entity.setIdElencoVers(this.getIdElencoVers());
        entity.setIdUnitaDoc(this.getIdUnitaDoc());
        entity.setIdStrutUnitaDoc(this.getIdStrutUnitaDoc());
        entity.setCdRegistroKeyUnitaDoc(this.getCdRegistroKeyUnitaDoc());
        entity.setAaKeyUnitaDoc(this.getAaKeyUnitaDoc());
        entity.setCdKeyUnitaDoc(this.getCdKeyUnitaDoc());
        entity.setDsKeyOrd(this.getDsKeyOrd());
        entity.setTiStatoUpdElencoVers(this.getTiStatoUpdElencoVers());
        entity.setTiStatoConservazione(this.getTiStatoConservazione());
        entity.setIdUpdUnitaDoc(this.getIdUpdUnitaDoc());
        entity.setPgUpdUnitaDoc(this.getPgUpdUnitaDoc());
        entity.setTsIniSes(this.getTsIniSes());
        entity.setDsUrnUpdUnitaDoc(this.getDsUrnUpdUnitaDoc());
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
