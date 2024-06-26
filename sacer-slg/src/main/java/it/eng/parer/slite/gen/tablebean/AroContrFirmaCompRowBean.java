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

import it.eng.parer.entity.AroContrFirmaComp;
import it.eng.parer.entity.AroFirmaComp;
import it.eng.parer.entity.FirCrl;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_Contr_Firma_Comp
 *
 */
public class AroContrFirmaCompRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static AroContrFirmaCompTableDescriptor TABLE_DESCRIPTOR = new AroContrFirmaCompTableDescriptor();

    public AroContrFirmaCompRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdContrFirmaComp() {
        return getBigDecimal("id_contr_firma_comp");
    }

    public void setIdContrFirmaComp(BigDecimal idContrFirmaComp) {
        setObject("id_contr_firma_comp", idContrFirmaComp);
    }

    public BigDecimal getIdFirmaComp() {
        return getBigDecimal("id_firma_comp");
    }

    public void setIdFirmaComp(BigDecimal idFirmaComp) {
        setObject("id_firma_comp", idFirmaComp);
    }

    public String getTiContr() {
        return getString("ti_contr");
    }

    public void setTiContr(String tiContr) {
        setObject("ti_contr", tiContr);
    }

    public String getTiEsitoContrFirma() {
        return getString("ti_esito_contr_firma");
    }

    public void setTiEsitoContrFirma(String tiEsitoContrFirma) {
        setObject("ti_esito_contr_firma", tiEsitoContrFirma);
    }

    public String getDsMsgEsitoContrFirma() {
        return getString("ds_msg_esito_contr_firma");
    }

    public void setDsMsgEsitoContrFirma(String dsMsgEsitoContrFirma) {
        setObject("ds_msg_esito_contr_firma", dsMsgEsitoContrFirma);
    }

    public BigDecimal getIdCrlUsata() {
        return getBigDecimal("id_crl_usata");
    }

    public void setIdCrlUsata(BigDecimal idCrlUsata) {
        setObject("id_crl_usata", idCrlUsata);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroContrFirmaComp entity = (AroContrFirmaComp) obj;
        this.setIdContrFirmaComp(
                entity.getIdContrFirmaComp() == null ? null : BigDecimal.valueOf(entity.getIdContrFirmaComp()));

        if (entity.getAroFirmaComp() != null) {
            this.setIdFirmaComp(new BigDecimal(entity.getAroFirmaComp().getIdFirmaComp()));
        }

        this.setTiContr(entity.getTiContr());
        this.setTiEsitoContrFirma(entity.getTiEsitoContrFirma());
        this.setDsMsgEsitoContrFirma(entity.getDsMsgEsitoContrFirma());

        if (entity.getFirCrl() != null) {
            this.setIdCrlUsata(new BigDecimal(entity.getFirCrl().getIdCrl()));
        }

    }

    @Override
    public AroContrFirmaComp rowBeanToEntity() {
        AroContrFirmaComp entity = new AroContrFirmaComp();
        if (this.getIdContrFirmaComp() != null) {
            entity.setIdContrFirmaComp(this.getIdContrFirmaComp().longValue());
        }
        if (this.getIdFirmaComp() != null) {
            if (entity.getAroFirmaComp() == null) {
                entity.setAroFirmaComp(new AroFirmaComp());
            }
            entity.getAroFirmaComp().setIdFirmaComp(this.getIdFirmaComp().longValue());
        }
        entity.setTiContr(this.getTiContr());
        entity.setTiEsitoContrFirma(this.getTiEsitoContrFirma());
        entity.setDsMsgEsitoContrFirma(this.getDsMsgEsitoContrFirma());
        if (this.getIdCrlUsata() != null) {
            if (entity.getFirCrl() == null) {
                entity.setFirCrl(new FirCrl());
            }
            entity.getFirCrl().setIdCrl(this.getIdCrlUsata().longValue());
        }
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
