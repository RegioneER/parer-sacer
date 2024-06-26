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

import it.eng.parer.viewEntity.VrsVSessioneVersRisolta;
import it.eng.parer.viewEntity.VrsVSessioneVersRisoltaId;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Vrs_V_Sessione_Vers_Risolta
 *
 */
public class VrsVSessioneVersRisoltaRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 20 December 2012 16:14" )
     */
    private static final long serialVersionUID = 1L;

    public static VrsVSessioneVersRisoltaTableDescriptor TABLE_DESCRIPTOR = new VrsVSessioneVersRisoltaTableDescriptor();

    public VrsVSessioneVersRisoltaRowBean() {
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

    public String getTiSessioneVers() {
        return getString("ti_sessione_vers");
    }

    public void setTiSessioneVers(String tiSessioneVers) {
        setObject("ti_sessione_vers", tiSessioneVers);
    }

    public String getTiStatoSessioneVers() {
        return getString("ti_stato_sessione_vers");
    }

    public void setTiStatoSessioneVers(String tiStatoSessioneVers) {
        setObject("ti_stato_sessione_vers", tiStatoSessioneVers);
    }

    public Timestamp getDtChiusura() {
        return getTimestamp("dt_chiusura");
    }

    public void setDtChiusura(Timestamp dtChiusura) {
        setObject("dt_chiusura", dtChiusura);
    }

    public String getFlSesRisolta() {
        return getString("fl_ses_risolta");
    }

    public void setFlSesRisolta(String flSesRisolta) {
        setObject("fl_ses_risolta", flSesRisolta);
    }

    public String getFlSesNonRisolub() {
        return getString("fl_ses_non_risolub");
    }

    public void setFlSesNonRisolub(String flSesNonRisolub) {
        setObject("fl_ses_non_risolub", flSesNonRisolub);
    }

    public String getTiDtCreazione() {
        return getString("ti_dt_creazione");
    }

    public void setTiDtCreazione(String tiDtCreazione) {
        setObject("ti_dt_creazione", tiDtCreazione);
    }

    public String getFlVerif() {
        return getString("fl_verif");
    }

    public void setFlVerif(String flVerif) {
        setObject("fl_verif", flVerif);
    }

    @Override
    public void entityToRowBean(Object obj) {
        VrsVSessioneVersRisolta entity = (VrsVSessioneVersRisolta) obj;
        this.setIdStrut(entity.getIdStrut());
        if (entity.getVrsVSessioneVersRisoltaId() != null) {
            this.setTiSessioneVers(entity.getVrsVSessioneVersRisoltaId().getTiSessioneVers());
            this.setTiStatoSessioneVers(entity.getVrsVSessioneVersRisoltaId().getTiStatoSessioneVers());
            if (entity.getVrsVSessioneVersRisoltaId().getDtChiusura() != null) {
                this.setDtChiusura(new Timestamp(entity.getVrsVSessioneVersRisoltaId().getDtChiusura().getTime()));
            }
            this.setFlSesRisolta(entity.getVrsVSessioneVersRisoltaId().getFlSesRisolta());
            this.setFlSesNonRisolub(entity.getVrsVSessioneVersRisoltaId().getFlSesNonRisolub());
            this.setTiDtCreazione(entity.getVrsVSessioneVersRisoltaId().getTiDtCreazione());
            this.setFlVerif(entity.getVrsVSessioneVersRisoltaId().getFlVerif());
        }
    }

    @Override
    public VrsVSessioneVersRisolta rowBeanToEntity() {
        VrsVSessioneVersRisolta entity = new VrsVSessioneVersRisolta();
        entity.setIdStrut(this.getIdStrut());
        entity.setVrsVSessioneVersRisoltaId(new VrsVSessioneVersRisoltaId());
        entity.getVrsVSessioneVersRisoltaId().setTiSessioneVers(this.getTiSessioneVers());
        entity.getVrsVSessioneVersRisoltaId().setTiStatoSessioneVers(this.getTiStatoSessioneVers());
        entity.getVrsVSessioneVersRisoltaId().setDtChiusura(this.getDtChiusura());
        entity.getVrsVSessioneVersRisoltaId().setFlSesRisolta(this.getFlSesRisolta());
        entity.getVrsVSessioneVersRisoltaId().setFlSesNonRisolub(this.getFlSesNonRisolub());
        entity.getVrsVSessioneVersRisoltaId().setTiDtCreazione(this.getTiDtCreazione());
        entity.getVrsVSessioneVersRisoltaId().setFlVerif(this.getFlVerif());
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
