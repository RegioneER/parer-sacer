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

import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerSerie;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Ser_Serie
 *
 */
public class SerSerieRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Friday, 24 April 2015 14:33" )
     */
    private static final long serialVersionUID = 1L;

    public static SerSerieTableDescriptor TABLE_DESCRIPTOR = new SerSerieTableDescriptor();

    public SerSerieRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdSerie() {
        return getBigDecimal("id_serie");
    }

    public void setIdSerie(BigDecimal idSerie) {
        setObject("id_serie", idSerie);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getCdCompositoSerie() {
        return getString("cd_composito_serie");
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        setObject("cd_composito_serie", cdCompositoSerie);
    }

    public String getCdSerieNormaliz() {
        return getString("cd_serie_normaliz");
    }

    public void setCdSerieNormaliz(String cdSerieNormaliz) {
        setObject("cd_serie_normaliz", cdSerieNormaliz);
    }

    public BigDecimal getAaSerie() {
        return getBigDecimal("aa_serie");
    }

    public void setAaSerie(BigDecimal aaSerie) {
        setObject("aa_serie", aaSerie);
    }

    public String getDsSerie() {
        return getString("ds_serie");
    }

    public void setDsSerie(String dsSerie) {
        setObject("ds_serie", dsSerie);
    }

    public BigDecimal getIdTipoSerie() {
        return getBigDecimal("id_tipo_serie");
    }

    public void setIdTipoSerie(BigDecimal idTipoSerie) {
        setObject("id_tipo_serie", idTipoSerie);
    }

    public BigDecimal getNiAnniConserv() {
        return getBigDecimal("ni_anni_conserv");
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
        setObject("ni_anni_conserv", niAnniConserv);
    }

    public BigDecimal getIdSeriePadre() {
        return getBigDecimal("id_serie_padre");
    }

    public void setIdSeriePadre(BigDecimal idSeriePadre) {
        setObject("id_serie_padre", idSeriePadre);
    }

    @Override
    public void entityToRowBean(Object obj) {
        SerSerie entity = (SerSerie) obj;

        this.setIdSerie(entity.getIdSerie() == null ? null : BigDecimal.valueOf(entity.getIdSerie()));

        if (entity.getOrgStrut() != null) {
            this.setIdStrut(new BigDecimal(entity.getOrgStrut().getIdStrut()));
        }

        this.setCdCompositoSerie(entity.getCdCompositoSerie());
        this.setCdSerieNormaliz(entity.getCdSerieNormaliz());
        this.setAaSerie(entity.getAaSerie());
        this.setDsSerie(entity.getDsSerie());

        if (entity.getDecTipoSerie() != null) {
            this.setIdTipoSerie(new BigDecimal(entity.getDecTipoSerie().getIdTipoSerie()));
        }

        this.setNiAnniConserv(entity.getNiAnniConserv());

        if (entity.getSerSeriePadre() != null) {
            this.setIdSeriePadre(new BigDecimal(entity.getSerSeriePadre().getIdSerie()));
        }

    }

    @Override
    public SerSerie rowBeanToEntity() {
        SerSerie entity = new SerSerie();
        if (this.getIdSerie() != null) {
            entity.setIdSerie(this.getIdSerie().longValue());
        }
        if (this.getIdStrut() != null) {
            if (entity.getOrgStrut() == null) {
                entity.setOrgStrut(new OrgStrut());
            }
            entity.getOrgStrut().setIdStrut(this.getIdStrut().longValue());
        }
        entity.setCdCompositoSerie(this.getCdCompositoSerie());
        entity.setCdSerieNormaliz(this.getCdSerieNormaliz());
        entity.setAaSerie(this.getAaSerie());
        entity.setDsSerie(this.getDsSerie());
        if (this.getIdTipoSerie() != null) {
            if (entity.getDecTipoSerie() == null) {
                entity.setDecTipoSerie(new DecTipoSerie());
            }
            entity.getDecTipoSerie().setIdTipoSerie(this.getIdTipoSerie().longValue());
        }
        entity.setNiAnniConserv(this.getNiAnniConserv());
        if (this.getIdSeriePadre() != null) {
            if (entity.getSerSeriePadre() == null) {
                entity.setSerSeriePadre(new SerSerie());
            }
            entity.getSerSeriePadre().setIdSerie(this.getIdSeriePadre().longValue());
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
