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

import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;
import it.eng.parer.entity.DmUdDelStatoRichiesta;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * RowBean per la tabella DM_UD_DEL_STATO_RICHIESTA
 */
public class DmUdDelStatoRichiestaRowBean extends BaseRow
        implements BaseRowInterface, JEEBaseRowInterface {

    public static DmUdDelStatoRichiestaTableDescriptor TABLE_DESCRIPTOR = new DmUdDelStatoRichiestaTableDescriptor();

    public DmUdDelStatoRichiestaRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdStatoUdDelRichiesta() {
        return getBigDecimal("id_stato_ud_del_richiesta");
    }

    public void setIdStatoUdDelRichiesta(BigDecimal idStatoUdDelRichiesta) {
        setObject("id_stato_ud_del_richiesta", idStatoUdDelRichiesta);
    }

    public String getTiStatoInternoRich() {
        return getString("ti_stato_interno_rich");
    }

    public void setTiStatoInternoRich(String tiStatoInternoRich) {
        setObject("ti_stato_interno_rich", tiStatoInternoRich);
    }

    public String getDsStatoInternoRich() {
        return getString("ds_stato_interno_rich");
    }

    public void setDsStatoInternoRich(String dsStatoInternoRich) {
        setObject("ds_stato_interno_rich", dsStatoInternoRich);
    }

    public Timestamp getDtRegStato() {
        return getTimestamp("dt_reg_stato");
    }

    public void setDtRegStato(Timestamp dtRegStato) {
        setObject("dt_reg_stato", dtRegStato);
    }

    public BigDecimal getPgStatoRich() {
        return getBigDecimal("pg_stato_rich");
    }

    public void setPgStatoRich(BigDecimal pgStatoRich) {
        setObject("pg_stato_rich", pgStatoRich);
    }

    public BigDecimal getIdUdDelRichiesta() {
        return getBigDecimal("id_ud_del_richiesta");
    }

    public void setIdUdDelRichiesta(BigDecimal idUdDelRichiesta) {
        setObject("id_ud_del_richiesta", idUdDelRichiesta);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DmUdDelStatoRichiesta entity = (DmUdDelStatoRichiesta) obj;
        if (entity.getIdStatoUdDelRichiesta() != null) {
            this.setIdStatoUdDelRichiesta(BigDecimal.valueOf(entity.getIdStatoUdDelRichiesta()));
        }
        if (entity.getDecodStatoInterno() != null) {
            this.setTiStatoInternoRich(entity.getDecodStatoInterno().getTiStatoInternoRich());
            this.setDsStatoInternoRich(entity.getDecodStatoInterno().getDsStatoInternoRich());
        }
        if (entity.getDtRegStato() != null) {
            this.setDtRegStato(new Timestamp(entity.getDtRegStato().getTime()));
        }
        this.setPgStatoRich(entity.getPgStatoRich());
        if (entity.getDmUdDelRichieste() != null
                && entity.getDmUdDelRichieste().getIdUdDelRichiesta() != null) {
            this.setIdUdDelRichiesta(
                    BigDecimal.valueOf(entity.getDmUdDelRichieste().getIdUdDelRichiesta()));
        }
    }

    @Override
    public DmUdDelStatoRichiesta rowBeanToEntity() {
        DmUdDelStatoRichiesta entity = new DmUdDelStatoRichiesta();
        if (this.getIdStatoUdDelRichiesta() != null) {
            entity.setIdStatoUdDelRichiesta(this.getIdStatoUdDelRichiesta().longValue());
        }
        entity.setDtRegStato(this.getDtRegStato());
        entity.setPgStatoRich(this.getPgStatoRich());
        return entity;
    }

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
