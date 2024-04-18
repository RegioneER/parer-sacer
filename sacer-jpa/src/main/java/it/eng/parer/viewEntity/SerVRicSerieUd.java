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
 * The persistent class for the SER_V_RIC_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_RIC_SERIE_UD")

public class SerVRicSerieUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private String cdCompositoSerie;
    private String cdVerSerie;
    private String dsElabBloccata;
    private String dsSerie;
    private Date dtFineSelSerie;
    private Date dtInizioSelSerie;
    private String flDaRigenera;
    private String flErrContenutoAcq;
    private String flErrContenutoCalc;
    private String flErrContenutoEff;
    private String flErrContenutoFile;
    private String flErrValidazione;
    private String flPresenzaConsistAttesa;
    private String flUpdAnnulUnitaDoc;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idModelloTipoSerie;
    private BigDecimal idSerie;
    private BigDecimal idStrut;
    private BigDecimal idTipoSerie;
    private BigDecimal idVerSerie;
    private BigDecimal niUdContenutoAcq;
    private BigDecimal niUdContenutoCalc;
    private BigDecimal niUdContenutoEff;
    private String nmAmbiente;
    private String nmEnte;
    private String nmModelloTipoSerie;
    private String nmStrut;
    private String nmTipoSerie;
    private String tiCreaStandard;
    private String tiStatoContenutoAcq;
    private String tiStatoContenutoCalc;
    private String tiStatoContenutoEff;
    private String tiStatoSerie;
    private String tiStatoVerSerie;

    public SerVRicSerieUd() {/* Hibernate */
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

    @Column(name = "DS_ELAB_BLOCCATA")
    public String getDsElabBloccata() {
        return this.dsElabBloccata;
    }

    public void setDsElabBloccata(String dsElabBloccata) {
        this.dsElabBloccata = dsElabBloccata;
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

    @Column(name = "FL_DA_RIGENERA", columnDefinition = "char(1)")
    public String getFlDaRigenera() {
        return this.flDaRigenera;
    }

    public void setFlDaRigenera(String flDaRigenera) {
        this.flDaRigenera = flDaRigenera;
    }

    @Column(name = "FL_ERR_CONTENUTO_ACQ", columnDefinition = "char(1)")
    public String getFlErrContenutoAcq() {
        return this.flErrContenutoAcq;
    }

    public void setFlErrContenutoAcq(String flErrContenutoAcq) {
        this.flErrContenutoAcq = flErrContenutoAcq;
    }

    @Column(name = "FL_ERR_CONTENUTO_CALC", columnDefinition = "char(1)")
    public String getFlErrContenutoCalc() {
        return this.flErrContenutoCalc;
    }

    public void setFlErrContenutoCalc(String flErrContenutoCalc) {
        this.flErrContenutoCalc = flErrContenutoCalc;
    }

    @Column(name = "FL_ERR_CONTENUTO_EFF", columnDefinition = "char(1)")
    public String getFlErrContenutoEff() {
        return this.flErrContenutoEff;
    }

    public void setFlErrContenutoEff(String flErrContenutoEff) {
        this.flErrContenutoEff = flErrContenutoEff;
    }

    @Column(name = "FL_ERR_CONTENUTO_FILE", columnDefinition = "char(1)")
    public String getFlErrContenutoFile() {
        return this.flErrContenutoFile;
    }

    public void setFlErrContenutoFile(String flErrContenutoFile) {
        this.flErrContenutoFile = flErrContenutoFile;
    }

    @Column(name = "FL_ERR_VALIDAZIONE", columnDefinition = "char(1)")
    public String getFlErrValidazione() {
        return this.flErrValidazione;
    }

    public void setFlErrValidazione(String flErrValidazione) {
        this.flErrValidazione = flErrValidazione;
    }

    @Column(name = "FL_PRESENZA_CONSIST_ATTESA", columnDefinition = "char(1)")
    public String getFlPresenzaConsistAttesa() {
        return this.flPresenzaConsistAttesa;
    }

    public void setFlPresenzaConsistAttesa(String flPresenzaConsistAttesa) {
        this.flPresenzaConsistAttesa = flPresenzaConsistAttesa;
    }

    @Column(name = "FL_UPD_ANNUL_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlUpdAnnulUnitaDoc() {
        return this.flUpdAnnulUnitaDoc;
    }

    public void setFlUpdAnnulUnitaDoc(String flUpdAnnulUnitaDoc) {
        this.flUpdAnnulUnitaDoc = flUpdAnnulUnitaDoc;
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

    @Column(name = "ID_MODELLO_TIPO_SERIE")
    public BigDecimal getIdModelloTipoSerie() {
        return this.idModelloTipoSerie;
    }

    public void setIdModelloTipoSerie(BigDecimal idModelloTipoSerie) {
        this.idModelloTipoSerie = idModelloTipoSerie;
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

    @Id
    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_UD_CONTENUTO_ACQ")
    public BigDecimal getNiUdContenutoAcq() {
        return this.niUdContenutoAcq;
    }

    public void setNiUdContenutoAcq(BigDecimal niUdContenutoAcq) {
        this.niUdContenutoAcq = niUdContenutoAcq;
    }

    @Column(name = "NI_UD_CONTENUTO_CALC")
    public BigDecimal getNiUdContenutoCalc() {
        return this.niUdContenutoCalc;
    }

    public void setNiUdContenutoCalc(BigDecimal niUdContenutoCalc) {
        this.niUdContenutoCalc = niUdContenutoCalc;
    }

    @Column(name = "NI_UD_CONTENUTO_EFF")
    public BigDecimal getNiUdContenutoEff() {
        return this.niUdContenutoEff;
    }

    public void setNiUdContenutoEff(BigDecimal niUdContenutoEff) {
        this.niUdContenutoEff = niUdContenutoEff;
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

    @Column(name = "NM_MODELLO_TIPO_SERIE")
    public String getNmModelloTipoSerie() {
        return this.nmModelloTipoSerie;
    }

    public void setNmModelloTipoSerie(String nmModelloTipoSerie) {
        this.nmModelloTipoSerie = nmModelloTipoSerie;
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

    @Column(name = "TI_CREA_STANDARD")
    public String getTiCreaStandard() {
        return this.tiCreaStandard;
    }

    public void setTiCreaStandard(String tiCreaStandard) {
        this.tiCreaStandard = tiCreaStandard;
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
