/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.slite.gen.tablebean;

import java.math.BigDecimal;
import java.util.Date;

import it.eng.parer.entity.AroPropScartoVers;
import it.eng.parer.entity.OrgStrut;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import java.sql.Timestamp;

/**
 * RowBean per la tabella AroPropScartoVers
 *
 */
public class AroPropScartoVersRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    // Assumendo che la classe TableDescriptor esista o verrà generata dal framework
    public static AroPropScartoVersTableDescriptor TABLE_DESCRIPTOR = new AroPropScartoVersTableDescriptor();

    public AroPropScartoVersRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // ===================================================================================
    // Getter e Setter mappati sui campi DB
    // ===================================================================================

    public BigDecimal getIdPropScartoVers() {
        return getBigDecimal("id_prop_scarto_vers");
    }

    public void setIdPropScartoVers(BigDecimal idPropScartoVers) {
        setObject("id_prop_scarto_vers", idPropScartoVers);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public BigDecimal getPgPropScartoVers() {
        return getBigDecimal("pg_prop_scarto_vers");
    }

    public void setPgPropScartoVers(BigDecimal pgPropScartoVers) {
        setObject("pg_prop_scarto_vers", pgPropScartoVers);
    }

    public String getDsPropScartoVers() {
        return getString("ds_prop_scarto_vers");
    }

    public void setDsPropScartoVers(String dsPropScartoVers) {
        setObject("ds_prop_scarto_vers", dsPropScartoVers);
    }

    public String getNtPropScartoVers() {
        return getString("nt_prop_scarto_vers");
    }

    public void setNtPropScartoVers(String ntPropScartoVers) {
        setObject("nt_prop_scarto_vers", ntPropScartoVers);
    }

    // public String getTiStatoPropScarto() {
    // return getString("ti_stato_prop_scarto");
    // }
    //
    // public void setTiStatoPropScarto(String tiStatoPropScarto) {
    // setObject("ti_stato_prop_scarto", tiStatoPropScarto);
    // }

    public String getFlConfermata() {
        return getString("fl_confermata");
    }

    public void setFlConfermata(String flConfermata) {
        setObject("fl_confermata", flConfermata);
    }

    public Date getDtCreazione() {
        return getTimestamp("dt_creazione");
    }

    public void setDtCreazione(Timestamp dtCreazione) {
        setObject("dt_creazione", dtCreazione);
    }

    public Date getDtUltimaMod() {
        return getTimestamp("dt_ultima_mod");
    }

    public void setDtUltimaMod(Timestamp dtUltimaMod) {
        setObject("dt_ultima_mod", dtUltimaMod);
    }

    public BigDecimal getIdUserIam() {
        return getBigDecimal("id_user_iam");
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        setObject("id_user_iam", idUserIam);
    }

    // ===================================================================================
    // Mappatura Entity -> RowBean
    // ===================================================================================
    @Override
    public void entityToRowBean(Object obj) {
        AroPropScartoVers entity = (AroPropScartoVers) obj;

        this.setIdPropScartoVers(entity.getIdPropScartoVers() == null ? null
                : BigDecimal.valueOf(entity.getIdPropScartoVers()));

        // this.setIdStrut(entity.getIdStrut());
        this.setPgPropScartoVers(entity.getPgPropScartoVers());
        this.setDsPropScartoVers(entity.getDsPropScartoVers());
        this.setNtPropScartoVers(entity.getNtPropScartoVers());
        // this.setTiStatoPropScarto(entity.getTiStatoPropScarto());
        if (entity.getDtCreazione() != null) {
            this.setDtCreazione(new Timestamp(entity.getDtCreazione().getTime()));
        }
        if (entity.getDtUltimaMod() != null) {
            this.setDtUltimaMod(new Timestamp(entity.getDtUltimaMod().getTime()));
        }
        // this.setIdUserIam(entity.getIdUserIam());
    }

    // ===================================================================================
    // Mappatura RowBean -> Entity
    // ===================================================================================
    @Override
    public AroPropScartoVers rowBeanToEntity() {
        AroPropScartoVers entity = new AroPropScartoVers();

        if (this.getIdPropScartoVers() != null) {
            entity.setIdPropScartoVers(this.getIdPropScartoVers().longValue());
        }

        // entity.setIdStrut(this.getIdStrut());

        entity.setPgPropScartoVers(this.getPgPropScartoVers());
        entity.setDsPropScartoVers(this.getDsPropScartoVers());
        entity.setNtPropScartoVers(this.getNtPropScartoVers());
        // entity.setTiStatoPropScarto(this.getTiStatoPropScarto());
        entity.setDtCreazione(this.getDtCreazione());
        entity.setDtUltimaMod(this.getDtUltimaMod());

        // entity.setIdUserIam(this.getIdUserIam());

        return entity;
    }

    // ===================================================================================
    // Gestione della paginazione (Standard Spago/SLite)
    // ===================================================================================

    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return getObject("rownum") != null ? Integer.parseInt(getObject("rownum").toString())
                : null;
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return getObject("rnum") != null ? Integer.parseInt(getObject("rnum").toString()) : null;
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return getObject("numrecords") != null
                ? Integer.parseInt(getObject("numrecords").toString())
                : null;
    }
}