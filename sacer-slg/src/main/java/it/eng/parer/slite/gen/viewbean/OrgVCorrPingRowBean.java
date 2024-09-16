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

import it.eng.parer.viewEntity.OrgVCorrPing;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 *
 * @author gpiccioli
 *
 *         Bean per la tabella Org_V_Corr_Ping
 */
public class OrgVCorrPingRowBean extends BaseRow implements JEEBaseRowInterface {
    private static final long serialVersionUID = 1L;

    public static OrgVCorrPingTableDescriptor TABLE_DESCRIPTOR = new OrgVCorrPingTableDescriptor();

    public OrgVCorrPingRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdDichVersSacer() {
        return getBigDecimal("id_dich_vers_sacer");
    }

    public void setIdDichVersSacer(BigDecimal idDichVersSacer) {
        setObject("id_dich_vers_sacer", idDichVersSacer);
    }

    public BigDecimal getIdVers() {
        return getBigDecimal("id_vers");
    }

    public void setIdVers(BigDecimal idVers) {
        setObject("id_vers", idVers);
    }

    public String getNmVers() {
        return getString("nm_vers");
    }

    public void setNmVers(String nmVers) {
        setObject("nm_vers", nmVers);
    }

    public String getTiDichVers() {
        return getString("ti_dich_vers");
    }

    public void setTiDichVers(String tiDichVers) {
        setObject("ti_dich_vers", tiDichVers);
    }

    public BigDecimal getIdOrganizIam() {
        return getBigDecimal("id_organiz_iam");
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
        setObject("id_organiz_iam", idOrganizIam);
    }

    public BigDecimal getIdOrganizApplic() {
        return getBigDecimal("id_organiz_applic");
    }

    public void setIdOrganizApplic(BigDecimal idOrganizApplic) {
        setObject("id_organiz_applic", idOrganizApplic);
    }

    public String getNmEntita() {
        return getString("nm_entita");
    }

    public void setNmEntita(String nmEntita) {
        setObject("nm_entita", nmEntita);
    }

    public String getNmAmbienteVers() {
        return getString("nm_ambiente_vers");
    }

    public void setNmAmbienteVers(String nmAMbienteVers) {
        setObject("nm_ambiente_vers", nmAMbienteVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        OrgVCorrPing entity = (OrgVCorrPing) obj;

        this.setIdDichVersSacer(BigDecimal.valueOf(entity.getIdDichVersSacer()));

        this.setIdVers(BigDecimal.valueOf(entity.getIdVers()));

        this.setNmVers(entity.getNmVers());
        this.setTiDichVers(entity.getTiDichVers());
        this.setIdOrganizIam(BigDecimal.valueOf(entity.getIdOrganizIam()));
        this.setIdOrganizApplic(BigDecimal.valueOf(entity.getIdOrganizApplic()));
        this.setNmEntita(entity.getNmEntita());
        this.setNmAmbienteVers(entity.getNmAmbienteVers());
    }

    @Override
    public OrgVCorrPing rowBeanToEntity() {
        OrgVCorrPing entity = new OrgVCorrPing();
        entity.setIdDichVersSacer(this.getIdDichVersSacer().longValue());
        entity.setIdVers(this.getIdVers().longValue());
        entity.setNmVers(this.getNmVers());
        entity.setTiDichVers(this.getTiDichVers());
        entity.setIdOrganizIam(this.getIdOrganizIam().longValue());
        entity.setIdOrganizApplic(this.getIdOrganizApplic().longValue());
        entity.setNmEntita(this.getNmEntita());
        entity.setNmAmbienteVers(this.getNmAmbienteVers());
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
