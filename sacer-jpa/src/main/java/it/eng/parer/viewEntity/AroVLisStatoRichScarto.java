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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the ARO_V_LIS_STATO_RICH_SCARTO database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_STATO_RICH_SCARTO")
@NamedQuery(name = "AroVLisStatoRichScarto.findAll", query = "SELECT a FROM AroVLisStatoRichScarto a")
public class AroVLisStatoRichScarto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dsNotaRichScartoVers;
    private Date dtRegStatoRichScartoVers;
    private BigDecimal idRichScartoVers;
    private BigDecimal idStatoRichScartoVers;
    private String nmUserid;
    private BigDecimal pgStatoRichScartoVers;
    private String tiStatoRichScartoVers;

    public AroVLisStatoRichScarto() {
        /* Hibernate */
    }

    @Column(name = "DS_NOTA_RICH_SCARTO_VERS")
    public String getDsNotaRichScartoVers() {
        return this.dsNotaRichScartoVers;
    }

    public void setDsNotaRichScartoVers(String dsNotaRichScartoVers) {
        this.dsNotaRichScartoVers = dsNotaRichScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_RICH_SCARTO_VERS")
    public Date getDtRegStatoRichScartoVers() {
        return this.dtRegStatoRichScartoVers;
    }

    public void setDtRegStatoRichScartoVers(Date dtRegStatoRichScartoVers) {
        this.dtRegStatoRichScartoVers = dtRegStatoRichScartoVers;
    }

    @Column(name = "ID_RICH_SCARTO_VERS")
    public BigDecimal getIdRichScartoVers() {
        return this.idRichScartoVers;
    }

    public void setIdRichScartoVers(BigDecimal idRichScartoVers) {
        this.idRichScartoVers = idRichScartoVers;
    }

    @Id
    @Column(name = "ID_STATO_RICH_SCARTO_VERS")
    public BigDecimal getIdStatoRichScartoVers() {
        return this.idStatoRichScartoVers;
    }

    public void setIdStatoRichScartoVers(BigDecimal idStatoRichScartoVers) {
        this.idStatoRichScartoVers = idStatoRichScartoVers;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "PG_STATO_RICH_SCARTO_VERS")
    public BigDecimal getPgStatoRichScartoVers() {
        return this.pgStatoRichScartoVers;
    }

    public void setPgStatoRichScartoVers(BigDecimal pgStatoRichScartoVers) {
        this.pgStatoRichScartoVers = pgStatoRichScartoVers;
    }

    @Column(name = "TI_STATO_RICH_SCARTO_VERS")
    public String getTiStatoRichScartoVers() {
        return this.tiStatoRichScartoVers;
    }

    public void setTiStatoRichScartoVers(String tiStatoRichScartoVers) {
        this.tiStatoRichScartoVers = tiStatoRichScartoVers;
    }

}