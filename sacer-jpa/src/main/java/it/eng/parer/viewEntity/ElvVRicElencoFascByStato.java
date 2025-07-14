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
 * The persistent class for the ELV_V_RIC_ELENCO_FASC_BY_STATO database table.
 *
 */
@Entity
@Table(name = "ELV_V_RIC_ELENCO_FASC_BY_STATO")

public class ElvVRicElencoFascByStato implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaFascicolo;
    private Date dtChiusura;
    private Date tsCreazioneElenco;
    private Date dtFirma;
    private String dlMotivoChius;
    private String flElencoStandard;
    private BigDecimal idAmbiente;
    private BigDecimal idCriterioRaggrFasc;
    private BigDecimal idElencoVersFasc;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private BigDecimal idUserIam;
    private BigDecimal niFascVersElenco;
    private String nmAmbiente;
    private String nmCriterioRaggr;
    private String nmEnte;
    private String nmStrut;
    private String ntElencoChiuso;
    private String ntIndiceElenco;
    private String tiStato;
    private Date tsStato;
    private String cdVoceTitol;
    private String nmTipoFascicolo;
    private BigDecimal idStatoElencoVersFascCor;
    private BigDecimal niIndiciAip;
    private Date dtCreazioneElencoIxAip;
    private String flAnnull;

    public ElvVRicElencoFascByStato() {/* Hibernate */
    }

    public ElvVRicElencoFascByStato(BigDecimal idElencoVersFasc, String tiStato,
	    BigDecimal aaFascicolo, BigDecimal niFascVersElenco, String dlMotivoChius,
	    Date tsCreazioneElenco, Date dtChiusura, Date dtFirma, BigDecimal idCriterioRaggrFasc,
	    String nmCriterioRaggr, String ntElencoChiuso, String ntIndiceElenco, String nmAmbiente,
	    String nmEnte, String nmStrut, String flElencoStandard, String cdVoceTitol,
	    String nmTipoFascicolo, String flAnnull) {
	this.idElencoVersFasc = idElencoVersFasc;
	this.tiStato = tiStato;
	this.aaFascicolo = aaFascicolo;
	this.niFascVersElenco = niFascVersElenco;
	this.dlMotivoChius = dlMotivoChius;
	this.tsCreazioneElenco = tsCreazioneElenco;
	this.dtChiusura = dtChiusura;
	this.dtFirma = dtFirma;
	this.idCriterioRaggrFasc = idCriterioRaggrFasc;
	this.nmCriterioRaggr = nmCriterioRaggr;
	this.ntElencoChiuso = ntElencoChiuso;
	this.ntIndiceElenco = ntIndiceElenco;
	this.nmAmbiente = nmAmbiente;
	this.nmEnte = nmEnte;
	this.nmStrut = nmStrut;
	this.flElencoStandard = flElencoStandard;
	this.cdVoceTitol = cdVoceTitol;
	this.nmTipoFascicolo = nmTipoFascicolo;
	this.flAnnull = flAnnull;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
	return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
	this.aaFascicolo = aaFascicolo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
	return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
	this.dtChiusura = dtChiusura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_CREAZIONE_ELENCO")
    public Date getTsCreazioneElenco() {
	return this.tsCreazioneElenco;
    }

    public void setTsCreazioneElenco(Date tsCreazioneElenco) {
	this.tsCreazioneElenco = tsCreazioneElenco;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA")
    public Date getDtFirma() {
	return this.dtFirma;
    }

    public void setDtFirma(Date dtFirma) {
	this.dtFirma = dtFirma;
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
	return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
	this.dlMotivoChius = dlMotivoChius;
    }

    @Column(name = "FL_ELENCO_STANDARD", columnDefinition = "char(1)")
    public String getFlElencoStandard() {
	return this.flElencoStandard;
    }

    public void setFlElencoStandard(String flElencoStandard) {
	this.flElencoStandard = flElencoStandard;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
	return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_CRITERIO_RAGGR_FASC")
    public BigDecimal getIdCriterioRaggrFasc() {
	return this.idCriterioRaggrFasc;
    }

    public void setIdCriterioRaggrFasc(BigDecimal idCriterioRaggrFasc) {
	this.idCriterioRaggrFasc = idCriterioRaggrFasc;
    }

    @Id
    @Column(name = "ID_ELENCO_VERS_FASC")
    public BigDecimal getIdElencoVersFasc() {
	return this.idElencoVersFasc;
    }

    public void setIdElencoVersFasc(BigDecimal idElencoVersFasc) {
	this.idElencoVersFasc = idElencoVersFasc;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
	return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }

    @Column(name = "NI_FASC_VERS_ELENCO")
    public BigDecimal getNiFascVersElenco() {
	return this.niFascVersElenco;
    }

    public void setNiFascVersElenco(BigDecimal niFascVersElenco) {
	this.niFascVersElenco = niFascVersElenco;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
	return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
	this.nmCriterioRaggr = nmCriterioRaggr;
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

    @Column(name = "NT_ELENCO_CHIUSO")
    public String getNtElencoChiuso() {
	return this.ntElencoChiuso;
    }

    public void setNtElencoChiuso(String ntElencoChiuso) {
	this.ntElencoChiuso = ntElencoChiuso;
    }

    @Column(name = "NT_INDICE_ELENCO")
    public String getNtIndiceElenco() {
	return this.ntIndiceElenco;
    }

    public void setNtIndiceElenco(String ntIndiceElenco) {
	this.ntIndiceElenco = ntIndiceElenco;
    }

    @Column(name = "TI_STATO")
    public String getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(String tiStato) {
	this.tiStato = tiStato;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_STATO")
    public Date getTsStato() {
	return this.tsStato;
    }

    public void setTsStato(Date tsStato) {
	this.tsStato = tsStato;
    }

    @Column(name = "CD_VOCE_TITOL")
    public String getCdVoceTitol() {
	return this.cdVoceTitol;
    }

    public void setCdVoceTitol(String cdVoceTitol) {
	this.cdVoceTitol = cdVoceTitol;
    }

    @Column(name = "NM_TIPO_FASCICOLO")
    public String getNmTipoFascicolo() {
	return this.nmTipoFascicolo;
    }

    public void setNmTipoFascicolo(String nmTipoFascicolo) {
	this.nmTipoFascicolo = nmTipoFascicolo;
    }

    @Column(name = "ID_STATO_ELENCO_VERS_FASC_COR")
    public BigDecimal getIdStatoElencoVersFascCor() {
	return this.idStatoElencoVersFascCor;
    }

    public void setIdStatoElencoVersFascCor(BigDecimal idStatoElencoVersFascCor) {
	this.idStatoElencoVersFascCor = idStatoElencoVersFascCor;
    }

    @Column(name = "NI_INDICI_AIP")
    public BigDecimal getNiIndiciAip() {
	return this.niIndiciAip;
    }

    public void setNiIndiciAip(BigDecimal niIndiciAip) {
	this.niIndiciAip = niIndiciAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_ELENCO_IX_AIP")
    public Date getDtCreazioneElencoIxAip() {
	return this.dtCreazioneElencoIxAip;
    }

    public void setDtCreazioneElencoIxAip(Date dtCreazioneElencoIxAip) {
	this.dtCreazioneElencoIxAip = dtCreazioneElencoIxAip;
    }

    @Column(name = "FL_ANNULL", columnDefinition = "char(1)")
    public String getFlAnnull() {
	return this.flAnnull;
    }

    public void setFlAnnull(String flAnnull) {
	this.flAnnull = flAnnull;
    }
}
