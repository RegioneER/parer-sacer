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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the SER_V_LIS_SER_DA_VALIDARE database table.
 */
@Entity
@Table(name = "SER_V_LIS_SER_DA_VALIDARE")

public class SerVLisSerDaValidare implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal aaSerie;

    private String cdCompositoSerie;

    private String cdVerSerie;

    private String dsSerie;

    private Date dtFineSelSerie;

    private Date dtInizioSelSerie;

    private String flErrContenutoEff;

    private BigDecimal idAmbiente;

    private BigDecimal idContenutoVerSerie;

    private BigDecimal idEnte;

    private BigDecimal idSerie;

    private BigDecimal idStrut;

    private String nmAmbiente;

    private String nmEnte;

    private String nmStrut;

    private String nmTipoSerie;

    public SerVLisSerDaValidare() {/* Hibernate */
    }

    @Column(name = "AA_SERIE")
    public BigDecimal getAaSerie() {
        return this.aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
        this.aaSerie = aaSerie;
    }

    @Column(name = "CD_COMPOSITO_SERIE")
    public String getCdCompositoSerie() {
        return this.cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        this.cdCompositoSerie = cdCompositoSerie;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
        return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
        this.cdVerSerie = cdVerSerie;
    }

    @Column(name = "DS_SERIE")
    public String getDsSerie() {
        return this.dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
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
    @Column(name = "DT_INIZIO_SEL_SERIE")
    public Date getDtInizioSelSerie() {
        return this.dtInizioSelSerie;
    }

    public void setDtInizioSelSerie(Date dtInizioSelSerie) {
        this.dtInizioSelSerie = dtInizioSelSerie;
    }

    @Column(name = "FL_ERR_CONTENUTO_EFF", columnDefinition = "char(1)")
    public String getFlErrContenutoEff() {
        return this.flErrContenutoEff;
    }

    public void setFlErrContenutoEff(String flErrContenutoEff) {
        this.flErrContenutoEff = flErrContenutoEff;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_CONTENUTO_VER_SERIE")
    public BigDecimal getIdContenutoVerSerie() {
        return this.idContenutoVerSerie;
    }

    public void setIdContenutoVerSerie(BigDecimal idContenutoVerSerie) {
        this.idContenutoVerSerie = idContenutoVerSerie;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_SERIE")
    public BigDecimal getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_SERIE")
    public String getNmTipoSerie() {
        return this.nmTipoSerie;
    }

    public void setNmTipoSerie(String nmTipoSerie) {
        this.nmTipoSerie = nmTipoSerie;
    }

    private SerVLisSerDaValidareId serVLisSerDaValidareId;

    @EmbeddedId()
    public SerVLisSerDaValidareId getSerVLisSerDaValidareId() {
        return serVLisSerDaValidareId;
    }

    public void setSerVLisSerDaValidareId(SerVLisSerDaValidareId serVLisSerDaValidareId) {
        this.serVLisSerDaValidareId = serVLisSerDaValidareId;
    }
}
