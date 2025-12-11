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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ELV_V_LIS_UD_BY_STATO database table.
 *
 */
@Entity
@Table(name = "ELV_V_LIS_UD_BY_STATO")

public class ElvVLisUdByStato implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal idElencoVers;
    private BigDecimal idUnitaDoc;
    private String tiStatoElencoVers;
    private String tiMotivo;
    private String tiStatoUd;
    private Date tsLastResetStato;

    public ElvVLisUdByStato() {/* Hibernate */
    }

    @Column(name = "TI_STATO_ELENCO_VERS")
    public String getTiStatoElencoVers() {
        return this.tiStatoElencoVers;
    }

    public void setTiStatoElencoVers(String tiStatoElencoVers) {
        this.tiStatoElencoVers = tiStatoElencoVers;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "TI_MOTIVO")
    public String getTiMotivo() {
        return this.tiMotivo;
    }

    public void setTiMotivo(String tiMotivo) {
        this.tiMotivo = tiMotivo;
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "TI_STATO_UD")
    public String getTiStatoUd() {
        return this.tiStatoUd;
    }

    public void setTiStatoUd(String tiStatoUd) {
        this.tiStatoUd = tiStatoUd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_LAST_RESET_STATO")
    public Date getTsLastResetStato() {
        return this.tsLastResetStato;
    }

    public void setTsLastResetStato(Date tsLastResetStato) {
        this.tsLastResetStato = tsLastResetStato;
    }

}
