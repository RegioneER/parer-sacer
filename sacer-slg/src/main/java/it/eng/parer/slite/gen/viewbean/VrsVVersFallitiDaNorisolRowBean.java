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

import it.eng.parer.viewEntity.VrsVVersFallitiDaNorisol;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Vrs_V_Vers_Falliti_Da_Norisol
 *
 */
public class VrsVVersFallitiDaNorisolRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 25 November 2014 12:44" )
     */
    private static final long serialVersionUID = 1L;

    public static VrsVVersFallitiDaNorisolTableDescriptor TABLE_DESCRIPTOR = new VrsVVersFallitiDaNorisolTableDescriptor();

    public VrsVVersFallitiDaNorisolRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public BigDecimal getIdSessioneVers() {
        return getBigDecimal("id_sessione_vers");
    }

    public void setIdSessioneVers(BigDecimal idSessioneVers) {
        setObject("id_sessione_vers", idSessioneVers);
    }

    public Timestamp getDtApertura() {
        return getTimestamp("dt_apertura");
    }

    public void setDtApertura(Timestamp dtApertura) {
        setObject("dt_apertura", dtApertura);
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

    public String getCdKeyDocVers() {
        return getString("cd_key_doc_vers");
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
        setObject("cd_key_doc_vers", cdKeyDocVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        VrsVVersFallitiDaNorisol entity = (VrsVVersFallitiDaNorisol) obj;
        this.setIdStrut(entity.getIdStrut());
        this.setIdSessioneVers(entity.getIdSessioneVers());
        if (entity.getDtApertura() != null) {
            this.setDtApertura(new Timestamp(entity.getDtApertura().getTime()));
        }
        this.setCdRegistroKeyUnitaDoc(entity.getCdRegistroKeyUnitaDoc());
        this.setAaKeyUnitaDoc(entity.getAaKeyUnitaDoc());
        this.setCdKeyUnitaDoc(entity.getCdKeyUnitaDoc());
        this.setCdKeyDocVers(entity.getCdKeyDocVers());
    }

    @Override
    public VrsVVersFallitiDaNorisol rowBeanToEntity() {
        VrsVVersFallitiDaNorisol entity = new VrsVVersFallitiDaNorisol();
        entity.setIdStrut(this.getIdStrut());
        entity.setIdSessioneVers(this.getIdSessioneVers());
        entity.setDtApertura(this.getDtApertura());
        entity.setCdRegistroKeyUnitaDoc(this.getCdRegistroKeyUnitaDoc());
        entity.setAaKeyUnitaDoc(this.getAaKeyUnitaDoc());
        entity.setCdKeyUnitaDoc(this.getCdKeyUnitaDoc());
        entity.setCdKeyDocVers(this.getCdKeyDocVers());
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
