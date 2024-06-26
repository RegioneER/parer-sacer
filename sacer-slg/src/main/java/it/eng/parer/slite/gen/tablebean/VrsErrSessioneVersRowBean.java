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

import it.eng.parer.entity.VrsDatiSessioneVers;
import it.eng.parer.entity.VrsErrSessioneVers;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Vrs_Err_Sessione_Vers
 *
 */
public class VrsErrSessioneVersRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static VrsErrSessioneVersTableDescriptor TABLE_DESCRIPTOR = new VrsErrSessioneVersTableDescriptor();

    public VrsErrSessioneVersRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdErrSessioneVers() {
        return getBigDecimal("id_err_sessione_vers");
    }

    public void setIdErrSessioneVers(BigDecimal idErrSessioneVers) {
        setObject("id_err_sessione_vers", idErrSessioneVers);
    }

    public BigDecimal getIdDatiSessioneVers() {
        return getBigDecimal("id_dati_sessione_vers");
    }

    public void setIdDatiSessioneVers(BigDecimal idDatiSessioneVers) {
        setObject("id_dati_sessione_vers", idDatiSessioneVers);
    }

    public BigDecimal getPgErrSessioneVers() {
        return getBigDecimal("pg_err_sessione_vers");
    }

    public void setPgErrSessioneVers(BigDecimal pgErrSessioneVers) {
        setObject("pg_err_sessione_vers", pgErrSessioneVers);
    }

    public String getTiErr() {
        return getString("ti_err");
    }

    public void setTiErr(String tiErr) {
        setObject("ti_err", tiErr);
    }

    public String getDsErr() {
        return getString("ds_err");
    }

    public void setDsErr(String dsErr) {
        setObject("ds_err", dsErr);
    }

    public String getCdErr() {
        return getString("cd_err");
    }

    public void setCdErr(String cdErr) {
        setObject("cd_err", cdErr);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getFlErrPrinc() {
        return getString("fl_err_princ");
    }

    public void setFlErrPrinc(String flErrPrinc) {
        setObject("fl_err_princ", flErrPrinc);
    }

    @Override
    public void entityToRowBean(Object obj) {
        VrsErrSessioneVers entity = (VrsErrSessioneVers) obj;

        this.setIdErrSessioneVers(
                entity.getIdErrSessioneVers() == null ? null : BigDecimal.valueOf(entity.getIdErrSessioneVers()));

        if (entity.getVrsDatiSessioneVers() != null) {
            this.setIdDatiSessioneVers(new BigDecimal(entity.getVrsDatiSessioneVers().getIdDatiSessioneVers()));
        }

        this.setPgErrSessioneVers(entity.getPgErrSessioneVers());
        this.setTiErr(entity.getTiErr());
        this.setDsErr(entity.getDsErr());
        this.setCdErr(entity.getCdErr());
        this.setIdStrut(entity.getIdStrut());
        this.setFlErrPrinc(entity.getFlErrPrinc());
    }

    @Override
    public VrsErrSessioneVers rowBeanToEntity() {
        VrsErrSessioneVers entity = new VrsErrSessioneVers();
        if (this.getIdErrSessioneVers() != null) {
            entity.setIdErrSessioneVers(this.getIdErrSessioneVers().longValue());
        }
        if (this.getIdDatiSessioneVers() != null) {
            if (entity.getVrsDatiSessioneVers() == null) {
                entity.setVrsDatiSessioneVers(new VrsDatiSessioneVers());
            }
            entity.getVrsDatiSessioneVers().setIdDatiSessioneVers(this.getIdDatiSessioneVers().longValue());
        }
        entity.setPgErrSessioneVers(this.getPgErrSessioneVers());
        entity.setTiErr(this.getTiErr());
        entity.setDsErr(this.getDsErr());
        entity.setCdErr(this.getCdErr());
        entity.setIdStrut(this.getIdStrut());
        entity.setFlErrPrinc(this.getFlErrPrinc());
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
