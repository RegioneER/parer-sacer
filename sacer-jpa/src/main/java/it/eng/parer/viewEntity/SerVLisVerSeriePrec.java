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
 * The persistent class for the SER_V_LIS_VER_SERIE_PREC database table.
 *
 */
@Entity
@Table(name = "SER_V_LIS_VER_SERIE_PREC")

public class SerVLisVerSeriePrec implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdFirstUnitaDocEff;
    private String cdLastUnitaDocEff;
    private String cdVerSerie;
    private Date dtFineSelSerie;
    private Date dtFirmaVerSerie;
    private Date dtInizioSelSerie;
    private BigDecimal idVerSerie;
    private BigDecimal idVerSerieInput;
    private BigDecimal niUnitaDocEff;
    private BigDecimal pgVerSerie;

    public SerVLisVerSeriePrec() {/* Hibernate */
    }

    @Column(name = "CD_FIRST_UNITA_DOC_EFF")
    public String getCdFirstUnitaDocEff() {
        return this.cdFirstUnitaDocEff;
    }

    public void setCdFirstUnitaDocEff(String cdFirstUnitaDocEff) {
        this.cdFirstUnitaDocEff = cdFirstUnitaDocEff;
    }

    @Column(name = "CD_LAST_UNITA_DOC_EFF")
    public String getCdLastUnitaDocEff() {
        return this.cdLastUnitaDocEff;
    }

    public void setCdLastUnitaDocEff(String cdLastUnitaDocEff) {
        this.cdLastUnitaDocEff = cdLastUnitaDocEff;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
        return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
        this.cdVerSerie = cdVerSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_SEL_SERIE")
    public Date getDtFineSelSerie() {
        return this.dtFineSelSerie;
    }

    public void setDtFineSelSerie(Date dtFineSelSerie) {
        this.dtFineSelSerie = dtFineSelSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA_VER_SERIE")
    public Date getDtFirmaVerSerie() {
        return this.dtFirmaVerSerie;
    }

    public void setDtFirmaVerSerie(Date dtFirmaVerSerie) {
        this.dtFirmaVerSerie = dtFirmaVerSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INIZIO_SEL_SERIE")
    public Date getDtInizioSelSerie() {
        return this.dtInizioSelSerie;
    }

    public void setDtInizioSelSerie(Date dtInizioSelSerie) {
        this.dtInizioSelSerie = dtInizioSelSerie;
    }

    @Id
    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "ID_VER_SERIE_INPUT")
    public BigDecimal getIdVerSerieInput() {
        return this.idVerSerieInput;
    }

    public void setIdVerSerieInput(BigDecimal idVerSerieInput) {
        this.idVerSerieInput = idVerSerieInput;
    }

    @Column(name = "NI_UNITA_DOC_EFF")
    public BigDecimal getNiUnitaDocEff() {
        return this.niUnitaDocEff;
    }

    public void setNiUnitaDocEff(BigDecimal niUnitaDocEff) {
        this.niUnitaDocEff = niUnitaDocEff;
    }

    @Column(name = "PG_VER_SERIE")
    public BigDecimal getPgVerSerie() {
        return this.pgVerSerie;
    }

    public void setPgVerSerie(BigDecimal pgVerSerie) {
        this.pgVerSerie = pgVerSerie;
    }

}
