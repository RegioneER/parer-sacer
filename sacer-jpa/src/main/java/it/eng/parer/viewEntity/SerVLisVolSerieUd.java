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
 * The persistent class for the SER_V_LIS_VOL_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_LIS_VOL_SERIE_UD")

public class SerVLisVolSerieUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdFirstUnitaDocVol;
    private String cdLastUnitaDocVol;
    private Date dtFirstUnitaDocVol;
    private Date dtLastUnitaDocVol;
    private BigDecimal idVerSerie;
    private BigDecimal idVolVerSerie;
    private BigDecimal niUnitaDocVol;
    private BigDecimal pgVolVerSerie;

    public SerVLisVolSerieUd() {/* Hibernate */
    }

    @Column(name = "CD_FIRST_UNITA_DOC_VOL")
    public String getCdFirstUnitaDocVol() {
        return this.cdFirstUnitaDocVol;
    }

    public void setCdFirstUnitaDocVol(String cdFirstUnitaDocVol) {
        this.cdFirstUnitaDocVol = cdFirstUnitaDocVol;
    }

    @Column(name = "CD_LAST_UNITA_DOC_VOL")
    public String getCdLastUnitaDocVol() {
        return this.cdLastUnitaDocVol;
    }

    public void setCdLastUnitaDocVol(String cdLastUnitaDocVol) {
        this.cdLastUnitaDocVol = cdLastUnitaDocVol;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_UNITA_DOC_VOL")
    public Date getDtFirstUnitaDocVol() {
        return this.dtFirstUnitaDocVol;
    }

    public void setDtFirstUnitaDocVol(Date dtFirstUnitaDocVol) {
        this.dtFirstUnitaDocVol = dtFirstUnitaDocVol;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_UNITA_DOC_VOL")
    public Date getDtLastUnitaDocVol() {
        return this.dtLastUnitaDocVol;
    }

    public void setDtLastUnitaDocVol(Date dtLastUnitaDocVol) {
        this.dtLastUnitaDocVol = dtLastUnitaDocVol;
    }

    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Id
    @Column(name = "ID_VOL_VER_SERIE")
    public BigDecimal getIdVolVerSerie() {
        return this.idVolVerSerie;
    }

    public void setIdVolVerSerie(BigDecimal idVolVerSerie) {
        this.idVolVerSerie = idVolVerSerie;
    }

    @Column(name = "NI_UNITA_DOC_VOL")
    public BigDecimal getNiUnitaDocVol() {
        return this.niUnitaDocVol;
    }

    public void setNiUnitaDocVol(BigDecimal niUnitaDocVol) {
        this.niUnitaDocVol = niUnitaDocVol;
    }

    @Column(name = "PG_VOL_VER_SERIE")
    public BigDecimal getPgVolVerSerie() {
        return this.pgVolVerSerie;
    }

    public void setPgVolVerSerie(BigDecimal pgVolVerSerie) {
        this.pgVolVerSerie = pgVolVerSerie;
    }

}
