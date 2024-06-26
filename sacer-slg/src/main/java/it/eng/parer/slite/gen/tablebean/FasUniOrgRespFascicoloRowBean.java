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

import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasUniOrgRespFascicolo;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Fas_Uni_Org_Resp_Fascicolo
 *
 */
public class FasUniOrgRespFascicoloRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Thursday, 14 December 2017 15:25" )
     */
    private static final long serialVersionUID = 1L;

    public static FasUniOrgRespFascicoloTableDescriptor TABLE_DESCRIPTOR = new FasUniOrgRespFascicoloTableDescriptor();

    public FasUniOrgRespFascicoloRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdUniOrgRespFascicolo() {
        return getBigDecimal("id_uni_org_resp_fascicolo");
    }

    public void setIdUniOrgRespFascicolo(BigDecimal idUniOrgRespFascicolo) {
        setObject("id_uni_org_resp_fascicolo", idUniOrgRespFascicolo);
    }

    public BigDecimal getIdFascicolo() {
        return getBigDecimal("id_fascicolo");
    }

    public void setIdFascicolo(BigDecimal idFascicolo) {
        setObject("id_fascicolo", idFascicolo);
    }

    public String getCdUniOrgResp() {
        return getString("cd_uni_org_resp");
    }

    public void setCdUniOrgResp(String cdUniOrgResp) {
        setObject("cd_uni_org_resp", cdUniOrgResp);
    }

    @Override
    public void entityToRowBean(Object obj) {
        FasUniOrgRespFascicolo entity = (FasUniOrgRespFascicolo) obj;

        this.setIdUniOrgRespFascicolo(entity.getIdUniOrgRespFascicolo() == null ? null
                : BigDecimal.valueOf(entity.getIdUniOrgRespFascicolo()));

        if (entity.getFasFascicolo() != null) {
            this.setIdFascicolo(new BigDecimal(entity.getFasFascicolo().getIdFascicolo()));
        }

        this.setCdUniOrgResp(entity.getCdUniOrgResp());
    }

    @Override
    public FasUniOrgRespFascicolo rowBeanToEntity() {
        FasUniOrgRespFascicolo entity = new FasUniOrgRespFascicolo();
        if (this.getIdUniOrgRespFascicolo() != null) {
            entity.setIdUniOrgRespFascicolo(this.getIdUniOrgRespFascicolo().longValue());
        }
        if (this.getIdFascicolo() != null) {
            if (entity.getFasFascicolo() == null) {
                entity.setFasFascicolo(new FasFascicolo());
            }
            entity.getFasFascicolo().setIdFascicolo(this.getIdFascicolo().longValue());
        }
        entity.setCdUniOrgResp(this.getCdUniOrgResp());
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
