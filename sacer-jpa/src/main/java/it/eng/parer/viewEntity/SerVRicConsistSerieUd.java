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
 * The persistent class for the SER_V_RIC_CONSIST_SERIE_UD database table.
 * 
 */
@Entity
@Table(name = "SER_V_RIC_CONSIST_SERIE_UD")

public class SerVRicConsistSerieUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private String cdCompositoSerie;
    private String cdVerSerie;
    private String dsSerie;
    private Date dtComunicConsistVerSerie;
    private String flPresenzaConsistAttesa;
    private String flPresenzaContenEff;
    private BigDecimal idAmbiente;
    private BigDecimal idConsistVerSerie;
    private BigDecimal idContenutoAcq;
    private BigDecimal idContenutoCalc;
    private BigDecimal idContenutoEff;
    private BigDecimal idEnte;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idSerie;
    private BigDecimal idStrut;
    private BigDecimal idTipoSerie;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idVerSerie;
    private BigDecimal niLacuneMancanti;
    private BigDecimal niLacuneNonProdotte;
    private BigDecimal niUnitaDoc;
    private BigDecimal niUnitaDocAttese;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoSerie;
    private String tiModConsistFirstLast;
    private String tiStatoContenutoAcq;
    private String tiStatoContenutoCalc;
    private String tiStatoContenutoEff;
    private String tiStatoVerSerie;

    public SerVRicConsistSerieUd() {/* Hibernate */
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
    @Column(name = "DT_COMUNIC_CONSIST_VER_SERIE")
    public Date getDtComunicConsistVerSerie() {
        return this.dtComunicConsistVerSerie;
    }

    public void setDtComunicConsistVerSerie(Date dtComunicConsistVerSerie) {
        this.dtComunicConsistVerSerie = dtComunicConsistVerSerie;
    }

    @Column(name = "FL_PRESENZA_CONSIST_ATTESA", columnDefinition = "char(1)")
    public String getFlPresenzaConsistAttesa() {
        return this.flPresenzaConsistAttesa;
    }

    public void setFlPresenzaConsistAttesa(String flPresenzaConsistAttesa) {
        this.flPresenzaConsistAttesa = flPresenzaConsistAttesa;
    }

    @Column(name = "FL_PRESENZA_CONTEN_EFF", columnDefinition = "char(1)")
    public String getFlPresenzaContenEff() {
        return this.flPresenzaContenEff;
    }

    public void setFlPresenzaContenEff(String flPresenzaContenEff) {
        this.flPresenzaContenEff = flPresenzaContenEff;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_CONSIST_VER_SERIE")
    public BigDecimal getIdConsistVerSerie() {
        return this.idConsistVerSerie;
    }

    public void setIdConsistVerSerie(BigDecimal idConsistVerSerie) {
        this.idConsistVerSerie = idConsistVerSerie;
    }

    @Column(name = "ID_CONTENUTO_ACQ")
    public BigDecimal getIdContenutoAcq() {
        return this.idContenutoAcq;
    }

    public void setIdContenutoAcq(BigDecimal idContenutoAcq) {
        this.idContenutoAcq = idContenutoAcq;
    }

    @Column(name = "ID_CONTENUTO_CALC")
    public BigDecimal getIdContenutoCalc() {
        return this.idContenutoCalc;
    }

    public void setIdContenutoCalc(BigDecimal idContenutoCalc) {
        this.idContenutoCalc = idContenutoCalc;
    }

    @Column(name = "ID_CONTENUTO_EFF")
    public BigDecimal getIdContenutoEff() {
        return this.idContenutoEff;
    }

    public void setIdContenutoEff(BigDecimal idContenutoEff) {
        this.idContenutoEff = idContenutoEff;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
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

    @Column(name = "ID_TIPO_SERIE")
    public BigDecimal getIdTipoSerie() {
        return this.idTipoSerie;
    }

    public void setIdTipoSerie(BigDecimal idTipoSerie) {
        this.idTipoSerie = idTipoSerie;
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

    @Column(name = "NI_LACUNE_MANCANTI")
    public BigDecimal getNiLacuneMancanti() {
        return this.niLacuneMancanti;
    }

    public void setNiLacuneMancanti(BigDecimal niLacuneMancanti) {
        this.niLacuneMancanti = niLacuneMancanti;
    }

    @Column(name = "NI_LACUNE_NON_PRODOTTE")
    public BigDecimal getNiLacuneNonProdotte() {
        return this.niLacuneNonProdotte;
    }

    public void setNiLacuneNonProdotte(BigDecimal niLacuneNonProdotte) {
        this.niLacuneNonProdotte = niLacuneNonProdotte;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
        return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
        this.niUnitaDoc = niUnitaDoc;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
        return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        this.niUnitaDocAttese = niUnitaDocAttese;
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

    @Column(name = "TI_MOD_CONSIST_FIRST_LAST")
    public String getTiModConsistFirstLast() {
        return this.tiModConsistFirstLast;
    }

    public void setTiModConsistFirstLast(String tiModConsistFirstLast) {
        this.tiModConsistFirstLast = tiModConsistFirstLast;
    }

    @Column(name = "TI_STATO_CONTENUTO_ACQ")
    public String getTiStatoContenutoAcq() {
        return this.tiStatoContenutoAcq;
    }

    public void setTiStatoContenutoAcq(String tiStatoContenutoAcq) {
        this.tiStatoContenutoAcq = tiStatoContenutoAcq;
    }

    @Column(name = "TI_STATO_CONTENUTO_CALC")
    public String getTiStatoContenutoCalc() {
        return this.tiStatoContenutoCalc;
    }

    public void setTiStatoContenutoCalc(String tiStatoContenutoCalc) {
        this.tiStatoContenutoCalc = tiStatoContenutoCalc;
    }

    @Column(name = "TI_STATO_CONTENUTO_EFF")
    public String getTiStatoContenutoEff() {
        return this.tiStatoContenutoEff;
    }

    public void setTiStatoContenutoEff(String tiStatoContenutoEff) {
        this.tiStatoContenutoEff = tiStatoContenutoEff;
    }

    @Column(name = "TI_STATO_VER_SERIE")
    public String getTiStatoVerSerie() {
        return this.tiStatoVerSerie;
    }

    public void setTiStatoVerSerie(String tiStatoVerSerie) {
        this.tiStatoVerSerie = tiStatoVerSerie;
    }

}
