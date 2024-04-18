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
 * The persistent class for the SER_V_RIC_SERIE_UD_USR database table.
 * 
 */
@Entity
@Table(name = "SER_V_RIC_SERIE_UD_USR")

public class SerVRicSerieUdUsr implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private String cdCompositoSerie;
    private String cdVerSerie;
    private String dsChiaveFirst;
    private String dsChiaveLast;
    private String dsSerie;
    private Date dtRegUnitaDocFirst;
    private Date dtRegUnitaDocLast;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idFirstUnitaDoc;
    private BigDecimal idLastUnitaDoc;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idSerie;
    private BigDecimal idStrut;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idVerSerie;
    private BigDecimal niUdContenuto;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String tiStatoSerie;
    private String tiStatoVerSerie;

    public SerVRicSerieUdUsr() {/* Hibernate */
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

    @Column(name = "DS_CHIAVE_FIRST")
    public String getDsChiaveFirst() {
        return this.dsChiaveFirst;
    }

    public void setDsChiaveFirst(String dsChiaveFirst) {
        this.dsChiaveFirst = dsChiaveFirst;
    }

    @Column(name = "DS_CHIAVE_LAST")
    public String getDsChiaveLast() {
        return this.dsChiaveLast;
    }

    public void setDsChiaveLast(String dsChiaveLast) {
        this.dsChiaveLast = dsChiaveLast;
    }

    @Column(name = "DS_SERIE")
    public String getDsSerie() {
        return this.dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_REG_UNITA_DOC_FIRST")
    public Date getDtRegUnitaDocFirst() {
        return this.dtRegUnitaDocFirst;
    }

    public void setDtRegUnitaDocFirst(Date dtRegUnitaDocFirst) {
        this.dtRegUnitaDocFirst = dtRegUnitaDocFirst;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_REG_UNITA_DOC_LAST")
    public Date getDtRegUnitaDocLast() {
        return this.dtRegUnitaDocLast;
    }

    public void setDtRegUnitaDocLast(Date dtRegUnitaDocLast) {
        this.dtRegUnitaDocLast = dtRegUnitaDocLast;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_FIRST_UNITA_DOC")
    public BigDecimal getIdFirstUnitaDoc() {
        return this.idFirstUnitaDoc;
    }

    public void setIdFirstUnitaDoc(BigDecimal idFirstUnitaDoc) {
        this.idFirstUnitaDoc = idFirstUnitaDoc;
    }

    @Column(name = "ID_LAST_UNITA_DOC")
    public BigDecimal getIdLastUnitaDoc() {
        return this.idLastUnitaDoc;
    }

    public void setIdLastUnitaDoc(BigDecimal idLastUnitaDoc) {
        this.idLastUnitaDoc = idLastUnitaDoc;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
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

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Id
    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_UD_CONTENUTO")
    public BigDecimal getNiUdContenuto() {
        return this.niUdContenuto;
    }

    public void setNiUdContenuto(BigDecimal niUdContenuto) {
        this.niUdContenuto = niUdContenuto;
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

    @Column(name = "TI_STATO_SERIE")
    public String getTiStatoSerie() {
        return this.tiStatoSerie;
    }

    public void setTiStatoSerie(String tiStatoSerie) {
        this.tiStatoSerie = tiStatoSerie;
    }

    @Column(name = "TI_STATO_VER_SERIE")
    public String getTiStatoVerSerie() {
        return this.tiStatoVerSerie;
    }

    public void setTiStatoVerSerie(String tiStatoVerSerie) {
        this.tiStatoVerSerie = tiStatoVerSerie;
    }

}
