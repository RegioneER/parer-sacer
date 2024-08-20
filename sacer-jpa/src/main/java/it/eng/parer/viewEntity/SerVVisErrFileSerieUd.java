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
 * The persistent class for the SER_V_VIS_ERR_FILE_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_VIS_ERR_FILE_SERIE_UD")

public class SerVVisErrFileSerieUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private String cdCompositoSerie;
    private String cdFirstUnitaDoc;
    private String cdLastUnitaDoc;
    private String cdVerSerie;
    private String dsRecErr;
    private String dsSerie;
    private Date dtStatoContenutoVerSerie;
    private String flErrContenuto;
    private BigDecimal idAmbiente;
    private BigDecimal idContenutoVerSerie;
    private BigDecimal idEnte;
    private BigDecimal idErrFileInput;
    private BigDecimal idFileInputVerSerie;
    private BigDecimal idSerie;
    private BigDecimal idStatoSerieCor;
    private BigDecimal idStatoVerSerieCor;
    private BigDecimal idStrut;
    private BigDecimal idTipoSerie;
    private BigDecimal idVerSerie;
    private BigDecimal niRecErr;
    private BigDecimal niUnitaDoc;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoSerie;
    private String tiErrRec;
    private String tiScopoFileInputVerSerie;
    private String tiStatoContenutoVerSerie;
    private String tiStatoSerieCor;
    private String tiStatoVerSerieCor;

    public SerVVisErrFileSerieUd() {/* Hibernate */
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

    @Column(name = "CD_FIRST_UNITA_DOC")
    public String getCdFirstUnitaDoc() {
        return this.cdFirstUnitaDoc;
    }

    public void setCdFirstUnitaDoc(String cdFirstUnitaDoc) {
        this.cdFirstUnitaDoc = cdFirstUnitaDoc;
    }

    @Column(name = "CD_LAST_UNITA_DOC")
    public String getCdLastUnitaDoc() {
        return this.cdLastUnitaDoc;
    }

    public void setCdLastUnitaDoc(String cdLastUnitaDoc) {
        this.cdLastUnitaDoc = cdLastUnitaDoc;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
        return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
        this.cdVerSerie = cdVerSerie;
    }

    @Column(name = "DS_REC_ERR")
    public String getDsRecErr() {
        return this.dsRecErr;
    }

    public void setDsRecErr(String dsRecErr) {
        this.dsRecErr = dsRecErr;
    }

    @Column(name = "DS_SERIE")
    public String getDsSerie() {
        return this.dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_CONTENUTO_VER_SERIE")
    public Date getDtStatoContenutoVerSerie() {
        return this.dtStatoContenutoVerSerie;
    }

    public void setDtStatoContenutoVerSerie(Date dtStatoContenutoVerSerie) {
        this.dtStatoContenutoVerSerie = dtStatoContenutoVerSerie;
    }

    @Column(name = "FL_ERR_CONTENUTO", columnDefinition = "char(1)")
    public String getFlErrContenuto() {
        return this.flErrContenuto;
    }

    public void setFlErrContenuto(String flErrContenuto) {
        this.flErrContenuto = flErrContenuto;
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

    @Id
    @Column(name = "ID_ERR_FILE_INPUT")
    public BigDecimal getIdErrFileInput() {
        return this.idErrFileInput;
    }

    public void setIdErrFileInput(BigDecimal idErrFileInput) {
        this.idErrFileInput = idErrFileInput;
    }

    @Column(name = "ID_FILE_INPUT_VER_SERIE")
    public BigDecimal getIdFileInputVerSerie() {
        return this.idFileInputVerSerie;
    }

    public void setIdFileInputVerSerie(BigDecimal idFileInputVerSerie) {
        this.idFileInputVerSerie = idFileInputVerSerie;
    }

    @Column(name = "ID_SERIE")
    public BigDecimal getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    @Column(name = "ID_STATO_SERIE_COR")
    public BigDecimal getIdStatoSerieCor() {
        return this.idStatoSerieCor;
    }

    public void setIdStatoSerieCor(BigDecimal idStatoSerieCor) {
        this.idStatoSerieCor = idStatoSerieCor;
    }

    @Column(name = "ID_STATO_VER_SERIE_COR")
    public BigDecimal getIdStatoVerSerieCor() {
        return this.idStatoVerSerieCor;
    }

    public void setIdStatoVerSerieCor(BigDecimal idStatoVerSerieCor) {
        this.idStatoVerSerieCor = idStatoVerSerieCor;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_SERIE")
    public BigDecimal getIdTipoSerie() {
        return this.idTipoSerie;
    }

    public void setIdTipoSerie(BigDecimal idTipoSerie) {
        this.idTipoSerie = idTipoSerie;
    }

    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_REC_ERR")
    public BigDecimal getNiRecErr() {
        return this.niRecErr;
    }

    public void setNiRecErr(BigDecimal niRecErr) {
        this.niRecErr = niRecErr;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
        return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
        this.niUnitaDoc = niUnitaDoc;
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

    @Column(name = "TI_ERR_REC")
    public String getTiErrRec() {
        return this.tiErrRec;
    }

    public void setTiErrRec(String tiErrRec) {
        this.tiErrRec = tiErrRec;
    }

    @Column(name = "TI_SCOPO_FILE_INPUT_VER_SERIE")
    public String getTiScopoFileInputVerSerie() {
        return this.tiScopoFileInputVerSerie;
    }

    public void setTiScopoFileInputVerSerie(String tiScopoFileInputVerSerie) {
        this.tiScopoFileInputVerSerie = tiScopoFileInputVerSerie;
    }

    @Column(name = "TI_STATO_CONTENUTO_VER_SERIE")
    public String getTiStatoContenutoVerSerie() {
        return this.tiStatoContenutoVerSerie;
    }

    public void setTiStatoContenutoVerSerie(String tiStatoContenutoVerSerie) {
        this.tiStatoContenutoVerSerie = tiStatoContenutoVerSerie;
    }

    @Column(name = "TI_STATO_SERIE_COR")
    public String getTiStatoSerieCor() {
        return this.tiStatoSerieCor;
    }

    public void setTiStatoSerieCor(String tiStatoSerieCor) {
        this.tiStatoSerieCor = tiStatoSerieCor;
    }

    @Column(name = "TI_STATO_VER_SERIE_COR")
    public String getTiStatoVerSerieCor() {
        return this.tiStatoVerSerieCor;
    }

    public void setTiStatoVerSerieCor(String tiStatoVerSerieCor) {
        this.tiStatoVerSerieCor = tiStatoVerSerieCor;
    }

}
